package it.bancomatpay.sdkui.model;

import static it.bancomatpay.sdkui.utilities.MapperConsumer.getCategoryIconResource;

import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class ShopCategoryConsumer implements ListTile{

    private ShopCategory shopCategory;

    public ShopCategoryConsumer(ShopCategory shopCategory) {
        this.shopCategory = shopCategory;
    }

    public ShopCategory getShopCategory() {
        return shopCategory;
    }

    @Override
    public int getLeadingIconRes() {
        return getCategoryIconResource(shopCategory);
    }

    @Override
    public String getTitle() {
        return shopCategory.getTitle();
    }

    @Override
    public String getSubtitle() { return null; }

    @Override
    public String getTrailingText() {
        return null;
    }

    @Override
    public boolean performFilter(String filter) {
        return StringUtils.contains(new String[]{shopCategory.getDescription(), getTitle()}, filter);
    }
}
