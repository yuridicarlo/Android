package it.bancomatpay.sdk.manager.task.interactor;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import it.bancomatpay.sdk.manager.db.LoyaltyCardFrequent;
import it.bancomatpay.sdk.manager.db.UserDbHelper;

public class FrequentLoyaltyCardInteractor implements Callable<HashMap<String, Integer>> {

    @Override
    public HashMap<String, Integer> call() {

        HashMap<String, Integer> frequentItems = new HashMap<>();
        List<LoyaltyCardFrequent.Model> cardFrequentList = UserDbHelper.getInstance().getLoyaltyCardFrequentList();
        for (LoyaltyCardFrequent.Model cardFrequent : cardFrequentList) {
            frequentItems.put(cardFrequent.getLoyaltyCardId(), cardFrequent.getOperationCounter());
        }

        return frequentItems;
    }
}
