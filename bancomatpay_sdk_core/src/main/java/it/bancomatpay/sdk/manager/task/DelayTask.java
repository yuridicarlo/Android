package it.bancomatpay.sdk.manager.task;

import android.os.Handler;
import android.os.Looper;

import it.bancomatpay.sdk.core.OnCompleteListener;

public class DelayTask<E> extends CancelableTask<E>{

    private CancelableTask<?> extendedTask;
    private long millisecond;
    private Handler handler;

    public DelayTask(OnCompleteResultListener<E> mListener, CancelableTask<?> extendedTask, long millisecond) {
        super(mListener);
        this.extendedTask = extendedTask;
        this.millisecond = millisecond;
    }

    @Override
    protected void start() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> extendedTask.start(), millisecond);
    }

    @Override
    protected void cancel() {
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        extendedTask.cancel();
    }

    @Override
    public void setMasterListener(OnCompleteListener masterListener) {
        super.setMasterListener(masterListener);
        extendedTask.setMasterListener(masterListener);
    }

}
