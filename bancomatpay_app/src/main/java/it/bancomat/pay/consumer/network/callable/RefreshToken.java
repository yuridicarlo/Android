package it.bancomat.pay.consumer.network.callable;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.network.dto.response.DtoRefreshTokenResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class RefreshToken extends CallableVoid {


    @Override
    public void execute() throws Exception {
        DtoAppResponse<DtoRefreshTokenResponse> response = new AppHandleRequestInteractor<Void, DtoRefreshTokenResponse>(DtoRefreshTokenResponse.class, null, AppCmd.REFRESH_TOKEN, ExtendedTask.getJsessionClient()).call();
        Result<Void> result = new Result<>();
        result.setStatusCode(Mapper.getStatusCodeInterface(response.getDtoStatus()));
        result.setStatusCodeDetail(Mapper.getExtraMessage(response.getDtoStatus()));
        result.setStatusCodeMessage(Mapper.getStatusCodeMessage(response.getDtoStatus()));

        if (result.isSuccess()) {
            AppBancomatDataManager.getInstance().putTokens(response.getRes().getTokens().getAuthorizationToken().getToken(), response.getRes().getTokens().getRefreshToken().getToken());
            SessionManager.getInstance().setSessionToken(response.getRes().getTokens().getAuthorizationToken().getToken());
        } else {
            throw new ServerException(result);
        }

    }
}
