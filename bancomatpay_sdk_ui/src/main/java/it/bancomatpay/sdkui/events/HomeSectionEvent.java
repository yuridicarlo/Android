package it.bancomatpay.sdkui.events;

public class HomeSectionEvent {

    public enum Type{
        CAMERA, SERVICES, STORES, BPLAY, HOME
    }

    private Type type;

    public HomeSectionEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
