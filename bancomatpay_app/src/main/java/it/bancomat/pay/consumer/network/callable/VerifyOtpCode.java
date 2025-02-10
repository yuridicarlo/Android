package it.bancomat.pay.consumer.network.callable;

import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.network.dto.VerifyOtpCodeData;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyOTPRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyOTPResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppMapper;
import it.bancomatpay.sdk.Result;

import static it.bancomatpay.sdk.manager.task.ExtendedTask.getJsessionClient;

public class VerifyOtpCode implements Callable<VerifyOtpCodeData> {

    private String otp;
    private VerifyActionCodeData verifyActionCodeData;

    public VerifyOtpCode(VerifyActionCodeData verifyActionCodeData, String otp) {
        this.verifyActionCodeData = verifyActionCodeData;
        this.otp = otp;
    }

    @Override
    public VerifyOtpCodeData call() throws Exception {
        DtoVerifyOTPRequest req = new DtoVerifyOTPRequest();
        req.setOtp(otp);
        req.setToken(verifyActionCodeData.getToken());
        AppHandleRequestInteractor<DtoVerifyOTPRequest, DtoVerifyOTPResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoVerifyOTPResponse.class, req, AppCmd.VERIFY_OTP_V2, getJsessionClient());

        Result<DtoVerifyOTPResponse> response = NetworkUtil.getResult(requestInteractor);

        VerifyOtpCodeData otpData = new VerifyOtpCodeData();

        DataBank res = AppMapper.getDataBank(response.getResult().getDtoBankDatas().get(0));
        DataBank dataBank = DataBankManager.getDataBank(res.getBankUUID());
        dataBank.setInstrument(res.getInstrument());
        otpData.setDataBank(dataBank);
        otpData.setToken(response.getResult().getToken());
        AppBancomatDataManager.getInstance().putActivationToken(response.getResult().getToken());

        return otpData;

    }
}
