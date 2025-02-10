package it.bancomatpay.sdk.manager.task;

public abstract class CancelableTask<E> extends ExtendedTask<E> {

    protected CancelableTask(OnCompleteResultListener<E> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {

    }

    protected abstract void cancel();

}
