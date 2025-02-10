package it.bancomatpay.sdkui.model;

import it.bancomatpay.sdkui.utilities.StringUtils;

public interface ListTile {
    int getLeadingIconRes();
    String getTitle();
    String getSubtitle();
    String getTrailingText();
    boolean performFilter(String filter);

}
