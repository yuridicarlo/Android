package it.bancomatpay.sdkui.model;

import static it.bancomatpay.sdkui.utilities.MapperConsumer.getCategoryIconResource;

import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class ShopItemConsumer extends ShopItem implements ListTile {

    private ShopCategory shopCategory;

    public ShopCategory getShopCategory() {
        return shopCategory;
    }

    public void setShopCategory(ShopCategory shopCategory) {
        this.shopCategory = shopCategory;
    }

    @Override
    public int getLeadingIconRes() {
        return getCategoryIconResource(shopCategory);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSubtitle() {
        if(address != null) {
            StringBuilder builder = new StringBuilder();
            if(address.getStreet() != null) {
                builder.append(address.getStreet()+", ");
            }
            if(address.getPostalCode() != null) {
                builder.append(address.getPostalCode()+" ");
            }
            if(address.getCity() != null) {
                builder.append(address.getCity());
            }

            return builder.toString();
        }
        return null;
    }

    @Override
    public String getTrailingText() {
        if(distance != null)
            return distance + "km";
        return null;
    }

    @Override
    public boolean performFilter(String filter) {
        return StringUtils.contains(new String[]{getTitle()}, filter);
    }
}
