package it.bancomat.pay.consumer.network.callable;

import android.text.TextUtils;

import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.request.DtoGetBanksConfigurationFileRequest;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.network.dto.response.DtoGetBanksConfigurationFileResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppUserDbHelper;
import it.bancomat.pay.consumer.storage.CreateBanksDataTask;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.db.BanksData;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class GetBanksConfigurationBank extends CallableVoid {


    private final static String TAG = GetBanksConfigurationBank.class.getSimpleName();

    @Override
    public void execute() throws Exception {

        BanksData.Model banksModel = AppUserDbHelper.getInstance().getBanksData();
        String banksFileVersion;

        if(TextUtils.isEmpty(banksModel.getVersion())){
            banksFileVersion = "0";
        }else {
            banksFileVersion = banksModel.getVersion();
        }
        try {
            DtoGetBanksConfigurationFileRequest request = new DtoGetBanksConfigurationFileRequest();
            request.setVersion(banksFileVersion);
            AppHandleRequestInteractor<DtoGetBanksConfigurationFileRequest, DtoGetBanksConfigurationFileResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoGetBanksConfigurationFileResponse.class, request, AppCmd.GET_BANKS_CONFIGURATION_FILE, ExtendedTask.getJsessionClient());
            Result<DtoGetBanksConfigurationFileResponse> r = NetworkUtil.getResult(requestInteractor);
            if (!TextUtils.isEmpty(r.getResult().getFile())) {
                new CreateBanksDataTask(r.getResult()).execute();
            }

        }catch (Exception e){
            CustomLogger.d(TAG, e);
        }

        banksModel = AppUserDbHelper.getInstance().getBanksData();

        if(TextUtils.isEmpty(banksModel.getFile())){
            DataBankManager.forceLocalBanksJson();

        }else {
            DataBankManager.forceDownloadedBanksJson();

        }

    }
}
