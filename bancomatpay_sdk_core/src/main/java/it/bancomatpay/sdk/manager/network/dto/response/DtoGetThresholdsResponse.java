package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoThreshold;

public class DtoGetThresholdsResponse implements Serializable {

    protected List<DtoThreshold> p2pThresholds;
    protected List<DtoThreshold> p2bThresholds;

    public List<DtoThreshold> getP2pThresholds() {
        return p2pThresholds;
    }

    public void setP2pThresholds(List<DtoThreshold> p2pThresholds) {
        this.p2pThresholds = p2pThresholds;
    }

    public List<DtoThreshold> getP2bThresholds() {
        return p2bThresholds;
    }

    public void setP2bThresholds(List<DtoThreshold> p2bThresholds) {
        this.p2bThresholds = p2bThresholds;
    }

}
