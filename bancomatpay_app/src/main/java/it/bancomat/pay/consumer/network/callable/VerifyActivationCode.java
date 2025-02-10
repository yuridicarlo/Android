package it.bancomat.pay.consumer.network.callable;

import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyActivationCodeRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyActivationCodeResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumberManager;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

public class VerifyActivationCode implements Callable<VerifyActionCodeData> {

    private String activationCode;

    public VerifyActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    @Override
    public VerifyActionCodeData call() throws Exception {
        DtoVerifyActivationCodeRequest req = new DtoVerifyActivationCodeRequest();
        req.setActivationCode(activationCode);

        AppHandleRequestInteractor<DtoVerifyActivationCodeRequest, DtoVerifyActivationCodeResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoVerifyActivationCodeResponse.class, req, AppCmd.VERIFY_ACTIVATION_CODE_V2, ExtendedTask.getJsessionClient());

        Result<DtoVerifyActivationCodeResponse> response = NetworkUtil.getResult(requestInteractor);

        AppBancomatDataManager.getInstance().putUserAccountId(response.getResult().getUserId());

        VerifyActionCodeData verifyActionCodeData = new VerifyActionCodeData();
        verifyActionCodeData.setBankUUID(response.getResult().getBankUUID());
        verifyActionCodeData.setToken(response.getResult().getToken());
        verifyActionCodeData.setMsisdn(response.getResult().getMsisdn());
        verifyActionCodeData.setMaskedMsisdn(response.getResult().getMaskedMsisdn());
        verifyActionCodeData.setActivationCode(activationCode);

        DataPrefixPhoneNumber mDefaultData = DataPrefixPhoneNumberManager.getData(response.getResult().getMsisdnAreaCode());


        FullStackSdkDataManager.getInstance().putDataPrefixPhoneNumber(mDefaultData);
        //    AppBancomatDataManager.getInstance().putPrefixCountryCode(mDefaultData.getPrefix());
        FullStackSdkDataManager.getInstance().putPrefixCountryCode(response.getResult().getMsisdnAreaCode());
        AppBancomatDataManager.getInstance().putBankUuid(response.getResult().getBankUUID());


        verifyActionCodeData.setMsisdnAreaCode(response.getResult().getMsisdnAreaCode());
        return verifyActionCodeData;
    }
}