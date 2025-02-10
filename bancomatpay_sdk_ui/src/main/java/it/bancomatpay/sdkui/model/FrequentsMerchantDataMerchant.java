package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.R;

public class FrequentsMerchantDataMerchant extends FrequentItemConsumer implements MerchantDisplayData {


    public FrequentsMerchantDataMerchant(FrequentItem recentItem) {
        super(recentItem);
    }

    @Override
    public Bitmap getBitmap() {
        try {
            byte[] decodedString = Base64.decode(frequentItem.getImage(), Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        catch (Exception e) {
            return BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                    R.drawable.placeholder_merchant);
        }
    }


    @Override
    public double getLatitude() {
        return ((ShopItem) frequentItem.getItemInterface()).getLatitude();
    }

    @Override
    public double getLongitude() {
        return ((ShopItem) frequentItem.getItemInterface()).getLongitude();
    }
}
