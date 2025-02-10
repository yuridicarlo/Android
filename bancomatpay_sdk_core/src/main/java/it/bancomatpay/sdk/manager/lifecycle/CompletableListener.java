package it.bancomatpay.sdk.manager.lifecycle;


import it.bancomatpay.sdk.manager.model.VoidResponse;

public abstract class CompletableListener extends SingleListener<VoidResponse> {

    @Override
    public void onSuccess(VoidResponse response) {
        onComplete();
    }

    protected abstract void onComplete();

}
