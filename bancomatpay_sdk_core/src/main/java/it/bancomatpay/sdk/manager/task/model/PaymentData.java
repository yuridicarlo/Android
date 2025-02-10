package it.bancomatpay.sdk.manager.task.model;

public class PaymentData {

    public enum State {
        EXECUTED,
        PENDING,
        WAIT,
        AUTHORIZED,
        SENT,
        FAILED
    }

    private int lastAttempts;
    private State state;
    private PollingData pollingData;

    public PollingData getPollingData() {
        return pollingData;
    }

    public void setPollingData(PollingData pollingData) {
        this.pollingData = pollingData;
    }

    public int getLastAttempts() {
        return lastAttempts;
    }

    public void setLastAttempts(int lastAttempts) {
        this.lastAttempts = lastAttempts;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
