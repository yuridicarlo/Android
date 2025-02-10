package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class PollingData implements Serializable {

    private int pollingMaxTimeSeconds;
    private int pollingRangeTimeSeconds;
    private boolean isPollingNeeded = false;

    public boolean isPollingNeeded() {
        return isPollingNeeded;
    }

    public void setPollingNeeded(boolean pollingNeeded) {
        isPollingNeeded = pollingNeeded;
    }

    public int getPollingMaxTimeSeconds() {
        return pollingMaxTimeSeconds;
    }

    public void setPollingMaxTimeSeconds(int pollingMaxTimeSeconds) {
        this.pollingMaxTimeSeconds = pollingMaxTimeSeconds;
    }

    public int getPollingRangeTimeSeconds() {
        return pollingRangeTimeSeconds;
    }

    public void setPollingRangeTimeSeconds(int pollingRangeTimeSeconds) {
        this.pollingRangeTimeSeconds = pollingRangeTimeSeconds;
    }
}
