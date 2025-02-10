package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.db.UserFrequent;

public class FrequentItem implements ItemInterface, Serializable {

    private ItemInterface itemInterface;
    private UserFrequent.Model dbModel;

    public ItemInterface getItemInterface() {
        return itemInterface;
    }

    public void setItemInterface(ItemInterface itemInterface) {
        this.itemInterface = itemInterface;
    }

    public UserFrequent.Model getDbModel() {
        return dbModel;
    }

    public void setDbModel(UserFrequent.Model dbModel) {
        this.dbModel = dbModel;
    }

    public FrequentItem() {
    }

    public FrequentItem(ItemInterface itemInterface) {
        this.itemInterface = itemInterface;
    }

    public int getOperationCounter() {
        return dbModel.getOperationCounter();
    }

    public void setOperationCounter(int operationCounter) {
        this.dbModel.setOperationCounter(operationCounter);
    }

    @Override
    public String getLetter() {
        return itemInterface.getLetter().toUpperCase();
    }

    @Override
    public Type getType() {
        return itemInterface.getType();
    }

    @Override
    public String getTitle() {
        return itemInterface.getTitle();
    }

    @Override
    public String getDescription() {
        return itemInterface.getDescription();
    }

    @Override
    public String getImage() {
        return itemInterface.getImage();
    }

    @Override
    public String getPhoneNumber() {
        return itemInterface.getPhoneNumber();
    }

    @Override
    public long getId() {
        return itemInterface.getId();
    }

    @Override
    public String getJson() {
        return itemInterface.getJson();
    }
}
