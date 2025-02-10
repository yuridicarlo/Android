package it.bancomatpay.sdk.manager.task.interactor;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.network.MessageEnrichment;
import it.bancomatpay.sdk.manager.network.api.BancomatServerApi;
import it.bancomatpay.sdk.manager.network.dto.DtoAppInfo;
import it.bancomatpay.sdk.manager.network.dto.DtoSvc;
import it.bancomatpay.sdk.manager.network.dto.DtoUserInfo;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;
import it.bancomatpay.sdk.manager.network.dto.request.DtoHandleRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoServerResponse;
import it.bancomatpay.sdk.manager.security.encryption.MessageDecryption;
import it.bancomatpay.sdk.manager.security.encryption.MessageEncryption;
import it.bancomatpay.sdk.manager.security.signature.MessageSignature;
import it.bancomatpay.sdk.manager.security.signature.MessageSignatureVerify;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankInfo;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.ErrorException;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import retrofit2.Call;
import retrofit2.Response;

public class HandleRequestInteractor<F, G> implements Callable<DtoAppResponse<G>> {

    private static final String TAG = HandleRequestInteractor.class.getSimpleName();
    private Error error = new Error(StatusCode.Mobile.GENERIC_ERROR + TAG);

    private Class<G> responseType;
    private Cmd cmd;
    private F req;
    private String jsessionClient;

    private String abiCode;
    private String groupCode;
    private String serviceType;


    public HandleRequestInteractor(Class<G> responseType, F req, Cmd cmd,
                                   String jsessionClient) {
        this.responseType = responseType;
        this.req = req;
        this.cmd = cmd;

        this.jsessionClient = jsessionClient;
    }

    public HandleRequestInteractor(Class<G> responseType, F req, Cmd cmd,
                                   String jsessionClient, String abiCode, String groupCode) {
        this.responseType = responseType;
        this.req = req;
        this.cmd = cmd;

        this.jsessionClient = jsessionClient;
        this.abiCode = abiCode;
        this.groupCode = groupCode;
    }

    public HandleRequestInteractor(Class<G> responseType, F req, Cmd cmd,
                                   String jsessionClient, @Nullable BankServices.EBankService serviceType) {
        this.responseType = responseType;
        this.req = req;
        this.cmd = cmd;

        this.jsessionClient = jsessionClient;
        if (serviceType != null) {
            this.serviceType = serviceType.getServiceName();
        } else {
            this.serviceType = BankServices.EBankService.BASE.getServiceName();
        }
    }

    private DtoAppResponse<G> handleRequest() throws IOException {

        BancomatDataManager dataManager = BancomatDataManager.getInstance();

        Gson gson = new Gson();
        DtoUserInfo userInfo = new DtoUserInfo();
        userInfo.setUserId(dataManager.getUserAccountId());
        userInfo.setJsessionClient(jsessionClient);

        DtoAppInfo appInfo = new DtoAppInfo();
        if (!TextUtils.isEmpty(abiCode) && !TextUtils.isEmpty(groupCode)) {
            //Flusso di initSDK
            appInfo.setAbiCode(abiCode);
            appInfo.setGroupCode(groupCode);
        } else if (Cmd.isCmdCrossService(cmd)) {
            //Flusso che richiede serviceType
            BankInfo bankInfo = dataManager.getBankInfo();
            if (bankInfo != null && !TextUtils.isEmpty(bankInfo.getAbiCode()) && !TextUtils.isEmpty(bankInfo.getGroupCode())) {
                appInfo.setAbiCode(bankInfo.getAbiCode());
                appInfo.setGroupCode(bankInfo.getGroupCode());
            }
            //TODO INIZIALIZZARE CORRETTAMENTE L'APPTOKEN
            appInfo.setAppToken("app_token_stub");
            appInfo.setServiceType(serviceType);
        } else {
            //Tutti gli altri flussi
            BankInfo bankInfo = dataManager.getBankInfo();
            if (bankInfo != null && !TextUtils.isEmpty(bankInfo.getAbiCode()) && !TextUtils.isEmpty(bankInfo.getGroupCode())) {
                appInfo.setAbiCode(bankInfo.getAbiCode());
                appInfo.setGroupCode(bankInfo.getGroupCode());
            }
            //TODO INIZIALIZZARE CORRETTAMENTE L'APPTOKEN
            appInfo.setAppToken("app_token_stub");
        }

        DtoAppRequest<F> dtoAppRequest = new DtoAppRequest<>();
        dtoAppRequest.setCmd(cmd.cmdName);
        dtoAppRequest.setReq(req);

        String plainMessage = gson.toJson(dtoAppRequest);
        CustomLogger.d(TAG, "HandleRequestInteractor plainMessageRequest: " + plainMessage);

        DtoSvc dtoSvc = createDtoCiphed(dtoAppRequest, userInfo, appInfo);

        /*Tokens tokens = dataManager.getTokens();
        String token = "";
        if (tokens != null) {
            token = "Bearer " + tokens.getOauth();
        }

        Call<DtoServerResponse> syncCall = BancomatServerApi.getBancomatServer()
                .handleRequest(dtoSvc, token);
        */

        Call<DtoServerResponse> syncCall = BancomatServerApi.getBancomatServer()
                .handleRequest(dtoSvc, "Bearer " + SessionManager.getInstance().getSessionToken());
        Response<DtoServerResponse> callResponse = syncCall.execute();
        if (!callResponse.isSuccessful()) {
            error = new HttpError(callResponse.code());
            return null;
        }
        DtoServerResponse response = callResponse.body();

        MessageSignatureVerify signatureVerify = MessageSignatureVerify.Factory.create(cmd);
        MessageDecryption mDecry = MessageDecryption.Factory.create(cmd);

        plainMessage = mDecry.getDecryptedMessage(response.getDtoSvc().getB(), PayCore.getAppContext());
        CustomLogger.d(TAG, "HandleRequestInteractor plainMessageResponse: " + plainMessage);
        boolean responseOk = signatureVerify.checkMessageSignature(plainMessage, response.getDtoSvc().getS());

        DtoResponse<G> dtoResponse;
        Type type = getType(DtoResponse.class, responseType);

        if (responseOk) {
            dtoResponse = gson.fromJson(plainMessage, type);
            return dtoResponse.getDtoAppResponse();

        } else {
            error = new Error(StatusCode.Mobile.SECURITY + TAG);
            return null;
        }
    }

    private DtoSvc createDtoCiphed(DtoAppRequest<?> appRequest, DtoUserInfo userInfo, DtoAppInfo appInfo) {

        Gson gson = new Gson();
        Context context = PayCore.getAppContext();
        final MessageEnrichment mEnrich = MessageEnrichment.Factory.create();

        MessageSignature mSign = MessageSignature.Factory.create(cmd);
        MessageEncryption mEncry = MessageEncryption.Factory.create(cmd);

        DtoHandleRequest enrichMessage = mEnrich.doEnrichment(appRequest, context);
        enrichMessage.setDtoUserInfo(userInfo);
        enrichMessage.setDtoAppInfo(appInfo);

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
            @NonNull
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{parameter};
            }

            @NonNull
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