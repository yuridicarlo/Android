package it.bancomat.pay.consumer.network.callable;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.request.DtoGetAuthorizationTokenRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoGetAuthorizationTokenUnencrypted;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.dto.response.DtoGetAuthorizationTokenResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.interactor.DtoPinCryptedInteractor;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;

import static it.bancomatpay.sdk.manager.task.ExtendedTask.getJsessionClient;

public class GetAuthorizationToken implements Callable<DtoGetAuthorizationTokenResponse> {

    private final AppAuthenticationInterface authenticationInterface;
    private final String pinOperationType;
    private final String amount;
    private final String msisdnSender;
    private final String receiver;
    private final String operationId;

    public GetAuthorizationToken(AppAuthenticationInterface authenticationInterface, AuthenticationOperationType pinOperationType, String operationId, String amount, String msisdnSender, String receiver) {
        this.authenticationInterface = authenticationInterface;
        this.pinOperationType = pinOperationType.toString();
        this.operationId = operationId;
        this.amount = amount;
        this.msisdnSender = msisdnSender;
        this.receiver = receiver;
    }

    @Override
    public DtoGetAuthorizationTokenResponse call() throws Exception {
        DtoGetAuthorizationTokenUnencrypted dtoGetAuthorizationTokenUnencrypted = new DtoGetAuthorizationTokenUnencrypted();
        dtoGetAuthorizationTokenUnencrypted.setOperation(pinOperationType);
        dtoGetAuthorizationTokenUnencrypted.setOperationId(operationId);
        dtoGetAuthorizationTokenUnencrypted.setAmount(amount);
        dtoGetAuthorizationTokenUnencrypted.setSender(msisdnSender);
        dtoGetAuthorizationTokenUnencrypted.setReceiver(receiver);

        String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss").format(new Date());
        dtoGetAuthorizationTokenUnencrypted.setTimestamp(currentTimestamp);

        DtoPinCrypted dtoPinCrypted = new DtoPinCryptedInteractor<>(authenticationInterface, dtoGetAuthorizationTokenUnencrypted).call();


        DtoGetAuthorizationTokenRequest req = new DtoGetAuthorizationTokenRequest();
        req.setDtoPinCrypted(dtoPinCrypted);

        AppHandleRequestInteractor<DtoGetAuthorizationTokenRequest, DtoGetAuthorizationTokenResponse> appHandleRequestInteractor = new AppHandleRequestInteractor<>(DtoGetAuthorizationTokenResponse.class, req, AppCmd.GET_AUTHORIZATION_TOKEN, getJsessionClient());

        Result<DtoGetAuthorizationTokenResponse> result = NetworkUtil.getResult(appHandleRequestInteractor);
        if(!TextUtils.isEmpty(result.getResult().getLoyaltyToken())) {
            LoyaltyTokenManager.getInstance().setLoyaltyToken(result.getResult().getLoyaltyToken());
        }
        return result.getResult();
    }
}
