package it.bancomat.pay.consumer.network.dto.response;

import java.util.concurrent.Callable;

import it.bancomatpay.sdk.manager.model.VoidResponse;

public abstract class CallableVoid implements Callable<VoidResponse> {

    @Override
    public VoidResponse call() throws Exception {
        execute();
        return VoidResponse.VALUE;
    }

    public abstract void execute() throws Exception;
}
