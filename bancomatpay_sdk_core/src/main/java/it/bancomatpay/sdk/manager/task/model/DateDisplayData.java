package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public interface DateDisplayData extends Serializable {
    String getDateName();
    String getShortDateName();
}
