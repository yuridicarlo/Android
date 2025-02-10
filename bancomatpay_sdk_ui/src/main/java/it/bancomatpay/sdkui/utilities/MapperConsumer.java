package it.bancomatpay.sdkui.utilities;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.ListTile;
import it.bancomatpay.sdkui.model.ShopItemConsumer;
import it.bancomatpay.sdkui.model.SplitItemConsumer;

abstract public class MapperConsumer {
    public static List<SplitItemConsumer> splitItemConsumerListFromSplitBeneficiaryList(List<SplitBeneficiary> splitBeneficiaries) {
        List<SplitItemConsumer> splitItemConsumers = new ArrayList<>();
        for (SplitBeneficiary splitBeneficiary : splitBeneficiaries) {
            SplitItemConsumer splitItemConsumer = new SplitItemConsumer(splitBeneficiary.getBeneficiary());
            splitItemConsumer.setAmount(splitBeneficiary.getAmount());
            splitItemConsumer.setSplitBillState(splitBeneficiary.getSplitBillState());
            splitItemConsumers.add(splitItemConsumer);
        }
        return splitItemConsumers;
    }

    public static int getCategoryIconResource(ShopCategory shopCategory) {
        switch (shopCategory.getImageName()) {
            case "cat_abbigliamento":
                return R.drawable.cat_abbigliamento;
            case "cat_hotel":
                return R.drawable.cat_hotel;
            case "cat_casa":
                return R.drawable.cat_casa;
            case "cat_cinema":
                return R.drawable.cat_cinema;
            case "cat_farmacia":
                return R.drawable.cat_farmacia;
            case "cat_market":
                return R.drawable.cat_market;
            case "cat_ristoranti":
                return R.drawable.cat_ristoranti;
            case "cat_medico":
                return R.drawable.cat_medico;
            case "cat_supermercati":
                return R.drawable.cat_supermercati;
            case "cat_tabacchi":
                return R.drawable.cat_tabacchi;
            case "cat_bollette":
                return R.drawable.cat_bollette;
            case "cat_trasporti":
                return R.drawable.cat_trasporti;
            case "cat_altro":
            default:
                return R.drawable.cat_altro;
        }
    }

    public static List<ListTile> shopItemConsumerListTilesFromShopItemList(List<ShopItem> shopItemList) {
        return shopItemConsumerListTilesFromShopItemList(shopItemList, null);
    }

    public static List<ListTile> shopItemConsumerListTilesFromShopItemList(List<ShopItem> shopItemList, ShopCategory defaultShopCategory) {
        List<ListTile> shopItemConsumerList = new ArrayList<>();
        for (ShopItem shopItem : shopItemList) {
            ShopItemConsumer shopItemConsumer = new ShopItemConsumer();
            shopItemConsumer.setShopId(shopItem.getShopId());
            shopItemConsumer.setAddress(shopItem.getAddress());
            shopItemConsumer.setLatitude(shopItem.getLatitude());
            shopItemConsumer.setLongitude(shopItem.getLongitude());
            shopItemConsumer.setDistance(shopItem.getDistance());
            shopItemConsumer.setMsisdn(shopItem.getMsisdn());
            shopItemConsumer.setMail(shopItem.getMail());
            shopItemConsumer.setInsignia(shopItem.getInsignia());
            shopItemConsumer.setTag(shopItem.getTag());
            shopItemConsumer.setName(shopItem.getName());
            shopItemConsumer.setHolderName(shopItem.getHolderName());
            shopItemConsumer.setMerchantType(shopItem.getMerchantType());
            shopItemConsumer.setPaymentCategory(shopItem.getPaymentCategory());
            shopItemConsumer.setTillManagement(shopItem.isTillManagement());
            shopItemConsumer.setTillList(shopItem.getTillList());

            if (shopItem.getMccImageName() == null) {
                if (defaultShopCategory != null) {
                    shopItemConsumer.setShopCategory(defaultShopCategory);
                } else {
                    ShopCategory shopCategory = new ShopCategory();
                    shopCategory.setImageName("");
                    shopItemConsumer.setShopCategory(shopCategory);
                }
            } else {
                ShopCategory shopCategory = new ShopCategory();
                shopCategory.setImageName(shopItem.getMccImageName());
                shopItemConsumer.setShopCategory(shopCategory);
            }

            shopItemConsumerList.add(shopItemConsumer);

        }
        return shopItemConsumerList;
    }


    public static ArrayList<ShopCategory> getShopCategoriesFromDtoShopCategories(List<DtoShopCategory> dtoShopCategories) {
        ArrayList<ShopCategory> shopCategories = new ArrayList<>();
        for (DtoShopCategory dtoShopCategory : dtoShopCategories) {
            ShopCategory shopCategory = new ShopCategory();
            shopCategory.setTitle(dtoShopCategory.getTitle());
            shopCategory.setDescription(dtoShopCategory.getDescription());
            shopCategory.setImageName(dtoShopCategory.getImageName());
            shopCategory.setUuid(dtoShopCategory.getUuid());
            shopCategories.add(shopCategory);
        }
        return shopCategories;
    }
}
