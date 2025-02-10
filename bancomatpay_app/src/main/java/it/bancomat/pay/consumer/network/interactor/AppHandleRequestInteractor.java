package it.bancomat.pay.consumer.network.interactor;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.AppMessageEnrichment;
import it.bancomat.pay.consumer.network.api.AppBancomatServerApi;
import it.bancomat.pay.consumer.network.dto.AppDtoHandleRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoSetCustomerJourneyTagRequest;
import it.bancomat.pay.consumer.network.security.encryption.AppMessageDecryption;
import it.bancomat.pay.consumer.network.security.encryption.AppMessageEncryption;
import it.bancomat.pay.consumer.network.security.signature.AppMessageSignature;
import it.bancomat.pay.consumer.network.security.signature.AppMessageSignatureVerify;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.AppUserDbHelper;
import it.bancomat.pay.consumer.storage.model.AppTokens;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.manager.network.dto.DtoCustomerJourneyTag;
import it.bancomatpay.sdk.manager.network.dto.DtoSvc;
import it.bancomatpay.sdk.manager.network.dto.DtoUserInfo;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoServerResponse;
import it.bancomatpay.sdk.manager.task.ErrorException;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import retrofit2.Call;
import retrofit2.Response;

public class AppHandleRequestInteractor<F, G> implements Callable<DtoAppResponse<G>> {

    private Error error = new Error(StatusCode.Mobile.GENERIC_ERROR + TAG);

    private static final String TAG = AppHandleRequestInteractor.class.getSimpleName();
    private DtoAppRequest<F> dtoAppRequest;
    private Class<G> responseType;
    private AppCmd cmd;
    private F req;
    private String jsessionClient;

    private boolean isSendCjTagOperation = false;
    private CustomerJourneyTag tag;

    public AppHandleRequestInteractor(Class<G> responseType, F req, AppCmd cmd, String jsessionClient) {

        this.responseType = responseType;
        this.req = req;
        this.cmd = cmd;

        this.jsessionClient = jsessionClient;
    }

    //Only for send customer journey tag
    public AppHandleRequestInteractor(Class<G> responseType, CustomerJourneyTag tag, String jsessionClient) {
        this.responseType = responseType;
        this.cmd = AppCmd.SET_CUSTOMER_JOURNEY_TAG;
        this.tag = tag;

        this.isSendCjTagOperation = true;

        this.jsessionClient = jsessionClient;
    }

    private DtoAppResponse<G> handleRequest() throws IOException {

        if (isSendCjTagOperation) {
            AppUserDbHelper.getInstance().addCustomerJourneyTag(tag);

            List<CustomerJourneyTag> savedTags = AppUserDbHelper.getInstance().getCustomerJourneyTagsList();
            List<DtoCustomerJourneyTag> tags = Mapper.getCustomerJourneyTagsRequest(savedTags);

            DtoSetCustomerJourneyTagRequest req = new DtoSetCustomerJourneyTagRequest();
            req.setDtoCustomerJourneyTags(tags);

            this.req = (F) req;
        }

        Gson gson = new Gson();
        DtoUserInfo userInfo = new DtoUserInfo();
        userInfo.setUserId(AppBancomatDataManager.getInstance().getUserAccountId());
        userInfo.setJsessionClient(jsessionClient);

        dtoAppRequest = new DtoAppRequest<>();
        dtoAppRequest.setCmd(cmd.cmdName);
        dtoAppRequest.setReq(req);

        String plainMessage = gson.toJson(dtoAppRequest);
        CustomLogger.d(TAG, "AppHandleRequestInteractor plainMessageRequest: " + plainMessage);

        DtoSvc dtoSvc = createDtoCiphed(dtoAppRequest, userInfo);

        String token = "";
        AppTokens tokens = AppBancomatDataManager.getInstance().getTokens();
        if (tokens != null) {
            if (cmd == AppCmd.REFRESH_TOKEN) {
                token = "Bearer " + tokens.getRefres();
            } else {
                token = "Bearer " + tokens.getOauth();
            }
        }
        Call<DtoServerResponse> syncCall = AppBancomatServerApi.getBancomatServer().handleRequest(dtoSvc, token);
        Response<DtoServerResponse> callResponse = syncCall.execute();
        if (!callResponse.isSuccessful()) {
            error = new HttpError(callResponse.code());
            return null;
        }
        DtoServerResponse response = callResponse.body();

        AppMessageSignatureVerify signatureVerify = AppMessageSignatureVerify.Factory.create(cmd);
        AppMessageDecryption mDecry = AppMessageDecryption.Factory.create(cmd);

        plainMessage = mDecry.getDecryptedMessage(response.getDtoSvc().getB(), BancomatApplication.getAppContext());
        CustomLogger.d(TAG, "AppHandleRequestInteractor plainMessageResponse: " + plainMessage);
        boolean responseOk = signatureVerify.checkMessageSignature(plainMessage, response.getDtoSvc().getS());

        DtoResponse<G> dtoResponse;
        Type type = getType(DtoResponse.class, responseType);

        if (responseOk) {
            dtoResponse = gson.fromJson(plainMessage, type);
            DtoAppResponse<G> dtoAppResponse = dtoResponse.getDtoAppResponse();

            if (dtoAppResponse.getCmd() != null && dtoAppResponse.getCmd().equals(AppCmd.SET_CUSTOMER_JOURNEY_TAG.cmdName)
                    && Mapper.getStatusCodeInterface(dtoAppResponse.getDtoStatus()).isSuccess()) {
                AppUserDbHelper.getInstance().deleteCustomerJourneyTags();
            }

            return dtoAppResponse;

        } else {
            error = new Error(StatusCode.Mobile.SECURITY + TAG);
            return null;
        }
    }

    private DtoSvc createDtoCiphed(DtoAppRequest<?> appRequest, DtoUserInfo userInfo) {
        Gson gson = new Gson();
        Context context = BancomatApplication.getAppContext();
        final AppMessageEnrichment mEnrich = AppMessageEnrichment.Factory.create();

        AppMessageSignature mSign = AppMessageSignature.Factory.create(cmd);
        AppMessageEncryption mEncry = AppMessageEncryption.Factory.create(cmd);

        AppDtoHandleRequest enrichMessage = mEnrich.doEnrichment(appRequest, context);
        enrichMessage.setDtoUserInfo(userInfo);

        String jsonMessageToSend = gson.toJson(enrichMessage);
        String sign = mSign.getMessageSignature(jsonMessageToSend, context);
        String encryptMessage = mEncry.getEncryptedMessage(jsonMessageToSend);
        DtoSvc dtoCiphed = new DtoSvc();
        dtoCiphed.setS(sign);
        dtoCiphed.setB(encryptMessage);
        return dtoCiphed;
    }

    private Type getType(final Class<?> rawClass, final Class<?> parameter) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{parameter};
            }

            @Override
            public Type getRawType() {
                return rawClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    @Override
    public DtoAppResponse<G> call() throws Exception {
        DtoAppResponse<G> g = handleRequest();
        if (g == null) {
            throw new ErrorException(error);
        } else {
            return g;
        }
    }

}