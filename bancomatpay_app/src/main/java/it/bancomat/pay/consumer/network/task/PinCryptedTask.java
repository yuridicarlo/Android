package it.bancomat.pay.consumer.network.task;

import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;

public abstract class PinCryptedTask<E> extends RefreshTokenTask<E> {

    protected AppAuthenticationInterface authenticationInterface;

    public PinCryptedTask(OnCompleteResultListener<E> mListener) {
        super(mListener);
    }
}
