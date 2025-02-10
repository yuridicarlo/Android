package it.bancomatpay.sdkui.config;

public class GenericFlags {

    private boolean needsCheckRoot;
    private boolean blockIfRooted;
    private boolean hideSpendingLimits;

    public boolean isNeedsCheckRoot() {
        return needsCheckRoot;
    }

    public void setNeedsCheckRoot(boolean needsCheckRoot) {
        this.needsCheckRoot = needsCheckRoot;
    }

    public boolean isBlockIfRooted() {
        return blockIfRooted;
    }

    public void setBlockIfRooted(boolean blockIfRooted) {
        this.blockIfRooted = blockIfRooted;
    }

    public boolean isHideSpendingLimits() {
        return hideSpendingLimits;
    }

    public void setHideSpendingLimits(boolean hideSpendingLimits) {
        this.hideSpendingLimits = hideSpendingLimits;
    }

}
