package it.bancomatpay.sdk.manager.task.model;

import java.util.ArrayList;

public class DirectDebitsHistoryData {

    private ArrayList<DirectDebitHistoryElement> directDebitHistoryElements;

    public ArrayList<DirectDebitHistoryElement> getDirectDebitHistoryElementList() {
        return directDebitHistoryElements;
    }

    public void setDirectDebitHistoryElementList(ArrayList<DirectDebitHistoryElement> directDebitHistoryElements) {
        this.directDebitHistoryElements = directDebitHistoryElements;
    }

}
