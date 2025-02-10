package it.bancomatpay.sdk.manager.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.io.Serializable;

//interfaccia che tutti i metodi esposti da LiveData per mascherare l'oggetto SingleResponse
public interface LiveSingle<T extends Serializable> extends Serializable {

    void setListener(@NonNull LifecycleOwner owner, @NonNull SingleListener<T> observer);

    boolean hasListener();

    void removeListener();

    boolean hasExecuted();

    T getValue();

    Throwable getThrowable();

    void setRunning(boolean b);

    boolean isNotPending();
}
