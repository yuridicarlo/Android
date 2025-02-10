package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class UserThresholds implements Serializable {

    private Thresholds p2PThresholds;
    private Thresholds p2BThresholds;

    public Thresholds getP2PThresholds() {
        return p2PThresholds;
    }

    public void setP2PThresholds(Thresholds p2PThresholds) {
        this.p2PThresholds = p2PThresholds;
    }

    public Thresholds getP2BThresholds() {
        return p2BThresholds;
    }

    public void setP2BThresholds(Thresholds p2BThresholds) {
        this.p2BThresholds = p2BThresholds;
    }

}
