package it.bancomatpay.sdk.manager.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.io.Serializable;

//interfaccia che tutti i metodi esposti da LiveData per mascherare l'oggetto ConsumableValue
public interface LiveEvent<T extends Serializable> extends Serializable {

    void setListener(@NonNull LifecycleOwner owner, @NonNull EventListener<T> observer);

    boolean hasListener();

    void removeListener();

    T getValue();

    void setRunning(boolean b);

    boolean isNotPending();
}
