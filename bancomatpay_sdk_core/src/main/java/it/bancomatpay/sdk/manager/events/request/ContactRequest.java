package it.bancomatpay.sdk.manager.events.request;

public class ContactRequest {

    private boolean forced;
    private boolean isOneShotMode = false;

    public ContactRequest(boolean forced) {
        this.forced = forced;
    }

    public ContactRequest(boolean forced, boolean isOneShotMode) {
        this.forced = forced;
        this.isOneShotMode = isOneShotMode;
    }

    public boolean isForced() {
        return forced;
    }

    public boolean isOneShotMode() {
        return isOneShotMode;
    }

    public void setOneShotMode(boolean oneShotMode) {
        isOneShotMode = oneShotMode;
    }

}
