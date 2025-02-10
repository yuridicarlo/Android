package it.bancomatpay.sdk.core;

import java.util.Objects;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public abstract class Task<E> {

    protected static String TAG = Task.class.getSimpleName();
    protected OnCompleteTaskListener<E> mListener;
    private OnCompleteListener masterListener;

    public void setMasterListener(OnCompleteListener masterListener) {
        this.masterListener = masterListener;
    }

    public Task(OnCompleteTaskListener<E> mListener) {
        this.mListener = mListener;
    }

    public void removeListener() {
        mListener = null;
        masterListener = null;
    }

    protected void sendCompletition(E result) {
        if (mListener != null) {
            try {
                mListener.onComplete(result);
            } catch (Exception e) {
                CustomLogger.d(TAG, e.getMessage());
            }
        }
        if (masterListener != null) {
            try {
                masterListener.onComplete(this);
            } catch (Exception e) {
                CustomLogger.d(TAG, e.getMessage());
            }
        }
    }

    public void sendError(Error error) {
        if (mListener != null) {
            mListener.onComplete(null);
        }
        if (masterListener != null) {
            masterListener.onCompleteWithError(this, error);
        }
    }

    public abstract void execute();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task<?> task = (Task<?>) o;
        return Objects.equals(mListener, task.mListener) && Objects.equals(masterListener, task.masterListener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mListener, masterListener);
    }
}

