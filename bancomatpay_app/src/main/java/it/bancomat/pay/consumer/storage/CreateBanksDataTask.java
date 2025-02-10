package it.bancomat.pay.consumer.storage;

import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.network.dto.response.DtoGetBanksConfigurationFileResponse;

import static it.bancomatpay.sdk.manager.db.BanksData.EntryV1.TABLE_NAME;

public class CreateBanksDataTask extends CallableVoid {

    private DtoGetBanksConfigurationFileResponse mBanksConfigurationFileResponse;

    public CreateBanksDataTask(DtoGetBanksConfigurationFileResponse banksConfigurationFileResponse) {
        this.mBanksConfigurationFileResponse = banksConfigurationFileResponse;
    }


    @Override
    public void execute() throws Exception {
        boolean isTableExisting = AppUserDbHelper.getInstance().isTableExists(TABLE_NAME);
        if (!isTableExisting) {
            AppUserDbHelper.getInstance().createBanksDataTable(mBanksConfigurationFileResponse);
        } else {
            AppUserDbHelper.getInstance().saveBanksDataFile(mBanksConfigurationFileResponse);
        }

    }
}
