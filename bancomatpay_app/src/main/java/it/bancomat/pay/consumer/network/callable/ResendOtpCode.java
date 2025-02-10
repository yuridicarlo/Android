package it.bancomat.pay.consumer.network.callable;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.network.dto.request.DtoGenerateNewOtpRequest;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.network.dto.response.DtoInitUserResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;

import static it.bancomatpay.sdk.manager.task.ExtendedTask.getJsessionClient;

public class ResendOtpCode extends CallableVoid {

    private VerifyActionCodeData verifyActionCodeData;

    public ResendOtpCode(VerifyActionCodeData verifyActionCodeData) {
        this.verifyActionCodeData = verifyActionCodeData;
    }

    @Override
    public void execute() throws Exception {
        DtoGenerateNewOtpRequest req = new DtoGenerateNewOtpRequest();
        req.setActivationCode(verifyActionCodeData.getActivationCode());
        req.setToken(verifyActionCodeData.getToken());

        AppHandleRequestInteractor<DtoGenerateNewOtpRequest, DtoInitUserResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoInitUserResponse.class, req, AppCmd.GENERATE_NEW_OTP, getJsessionClient());

        NetworkUtil.getResult(requestInteractor);


    }
}
