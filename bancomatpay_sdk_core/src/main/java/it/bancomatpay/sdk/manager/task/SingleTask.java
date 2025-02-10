package it.bancomatpay.sdk.manager.task;

import it.bancomatpay.sdk.core.OnCompleteListener;

public class SingleTask<E> extends ExtendedTask<E> {

    private static CancelableTask<?> lastTask;
    private static final Object sync = new Object();

    public SingleTask(OnCompleteResultListener<E> mListener, CancelableTask<?> e) {
        super(mListener);
        synchronized (sync) {
            if (lastTask != null) {
                lastTask.cancel();
            }
            lastTask = e;
        }
    }

    @Override
    protected void start() {
        lastTask.start();
    }

    @Override
    public void removeListener() {
        super.removeListener();
        if (lastTask != null) {
            lastTask.cancel();
        }
    }

    @Override
    public void setMasterListener(OnCompleteListener masterListener) {
        super.setMasterListener(masterListener);
        lastTask.setMasterListener(masterListener);
    }
}
