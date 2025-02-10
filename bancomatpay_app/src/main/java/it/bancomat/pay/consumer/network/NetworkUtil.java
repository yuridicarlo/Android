package it.bancomat.pay.consumer.network;

import java.io.IOException;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.network.callable.RefreshToken;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ErrorException;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class NetworkUtil {


    public static <T, E> Result<T> getResult(final AppHandleRequestInteractor<E, T> requestInteractor) throws Exception {
        DtoAppResponse<T> response;
        try {
            response = requestInteractor.call();
        }catch (ErrorException errorException){
            if(errorException.getError() instanceof HttpError){
                HttpError httpError = (HttpError) errorException.getError();
                if(httpError.getCode() == 401){
                    new RefreshToken().execute();
                    response = requestInteractor.call();
                }else {
                    throw errorException;
                }
            }else {
                throw errorException;
            }
        }catch (IOException ioException){
            Result<T> result = new Result<>();
            result.setStatusCode(StatusCode.Http.NO_RETE);
            throw new ServerException(result);
        }

        Result<T> result = new Result<>();
        result.setStatusCode(Mapper.getStatusCodeInterface(response.getDtoStatus()));
        result.setStatusCodeDetail(Mapper.getExtraMessage(response.getDtoStatus()));
        result.setStatusCodeMessage(Mapper.getStatusCodeMessage(response.getDtoStatus()));
        result.setResult(response.getRes());
        if(!result.isSuccess()){
            throw new ServerException(result);
        }
        return result;
    }




}
