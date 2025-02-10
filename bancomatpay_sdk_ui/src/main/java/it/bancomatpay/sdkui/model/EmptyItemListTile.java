package it.bancomatpay.sdkui.model;

public class EmptyItemListTile implements ListTile {
    @Override
    public int getLeadingIconRes() {
        return 0;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public String getTrailingText() {
        return null;
    }

    @Override
    public boolean performFilter(String filter) {
        return false;
    }
}
