package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class Thresholds implements Serializable {

    protected long thresholdTransactionMaxValue;
    protected long thresholdMonthMaxValue;
    protected long thresholdMonthMinValue;
    protected long thresholdMonthValue;
    protected long thresholdDayMaxValue;
    protected long thresholdDayMinValue;
    protected long thresholdDayValue;

    public long getThresholdMonthMinValue() {
        return thresholdMonthMinValue;
    }

    public void setThresholdMonthMinValue(long thresholdMonthMinValue) {
        this.thresholdMonthMinValue = thresholdMonthMinValue;
    }

    public long getThresholdDayMinValue() {
        return thresholdDayMinValue;
    }

    public void setThresholdDayMinValue(long thresholdDayMinValue) {
        this.thresholdDayMinValue = thresholdDayMinValue;
    }

    public long getThresholdTransactionMaxValue() {
        return thresholdTransactionMaxValue;
    }

    public void setThresholdTransactionMaxValue(long thresholdTransactionMaxValue) {
        this.thresholdTransactionMaxValue = thresholdTransactionMaxValue;
    }

    public long getThresholdMonthMaxValue() {
        return thresholdMonthMaxValue;
    }

    public void setThresholdMonthMaxValue(long thresholdMonthMaxValue) {
        this.thresholdMonthMaxValue = thresholdMonthMaxValue;
    }

    public long getThresholdMonthValue() {
        return thresholdMonthValue;
    }

    public void setThresholdMonthValue(long thresholdMonthValue) {
        this.thresholdMonthValue = thresholdMonthValue;
    }

    public long getThresholdDayMaxValue() {
        return thresholdDayMaxValue;
    }

    public void setThresholdDayMaxValue(long thresholdDayMaxValue) {
        this.thresholdDayMaxValue = thresholdDayMaxValue;
    }

    public long getThresholdDayValue() {
        return thresholdDayValue;
    }

    public void setThresholdDayValue(long thresholdDayValue) {
        this.thresholdDayValue = thresholdDayValue;
    }
}
