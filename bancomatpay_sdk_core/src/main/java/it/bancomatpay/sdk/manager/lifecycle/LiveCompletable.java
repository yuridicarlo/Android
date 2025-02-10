package it.bancomatpay.sdk.manager.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.io.Serializable;

//interfaccia che tutti i metodi esposti da LiveData per mascherare l'oggetto ResponseEvent<Void>
public interface LiveCompletable extends Serializable {

    void setListener(@NonNull LifecycleOwner owner, @NonNull CompletableListener observer);

    boolean hasListener();

    void removeListener();

    boolean hasExecuted();

    Throwable getThrowable();

    void setRunning(boolean b);

    boolean isNotPending();
}
