package it.bancomatpay.sdk.manager.task.model;

public interface ItemInterface {

    enum Type{
        NONE, CONSUMER, CONSUMER_PR, MERCHANT, BOTH, BOTH_PR
    }

    String getLetter();

    Type getType();

    String getTitle();

    String getDescription();

    String getImage();

    String getPhoneNumber();

    long getId();

    String getJson();
}
