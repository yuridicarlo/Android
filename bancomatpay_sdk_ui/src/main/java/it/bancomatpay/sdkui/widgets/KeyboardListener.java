package it.bancomatpay.sdkui.widgets;

public interface KeyboardListener<T>{
    void onTextEntered(String text);
    void onDeleteCharacter();
    void onDeleteAllText();
    T getValue();
    void setMaxElements(int i);
}