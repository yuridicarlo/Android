package it.bancomatpay.sdk.manager.events.update;

import java.util.ArrayList;

import it.bancomatpay.sdk.manager.task.model.FrequentItem;

public class FrequentItemUpdate {

    private ArrayList<FrequentItem> frequentItems;

    public FrequentItemUpdate(ArrayList<FrequentItem> recentItems) {
        this.frequentItems = recentItems;
    }

    public ArrayList<FrequentItem> getFrequentItems() {
        return frequentItems;
    }

}
