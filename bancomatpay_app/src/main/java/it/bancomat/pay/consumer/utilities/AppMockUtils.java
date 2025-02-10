package it.bancomat.pay.consumer.utilities;

import java.math.BigDecimal;
import java.util.ArrayList;

import it.bancomatpay.sdk.manager.task.model.Address;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.model.ShopItemConsumer;

public class AppMockUtils {

    public static ArrayList<ShopItem> fakeStoreLocatorSearchTaskResponse() {
        ArrayList<ShopItem> physicalShops = new ArrayList<>();
        ShopCategory restaurantCat= new ShopCategory();
        restaurantCat.setTitle("Ristoranti");
        restaurantCat.setImageName("cat_ristoranti");
        restaurantCat.setUuid("afafge1");

        ShopCategory homeCat= new ShopCategory();
        homeCat.setTitle("Casa");
        homeCat.setImageName("cat_casa");
        homeCat.setUuid("afafge2");

        ShopItemConsumer item1 = new ShopItemConsumer();
        item1.setName("Bar Nazionale");
        item1.setShopCategory(restaurantCat);
        Address address1 = new Address();
        address1.setStreet("Via riva 21");
        item1.setAddress(address1);
        item1.setDistance(BigDecimal.valueOf(1.0));
        item1.setLatitude(45.07049);
        item1.setLongitude(7.68682);

        ShopItemConsumer item2 = new ShopItemConsumer();
        item2.setName("Kebab di chieri");
        item2.setShopCategory(restaurantCat);
        Address address2 = new Address();
        address2.setStreet("Via riva 21");
        item2.setAddress(address2);
        item2.setDistance(BigDecimal.valueOf(2.0));
        item2.setLatitude(45.09949);
        item2.setLongitude(7.69982);

        ShopItemConsumer item3 = new ShopItemConsumer();
        item3.setName("Q8");
        item3.setShopCategory(homeCat);
        Address address3 = new Address();
        address3.setStreet("Via riva 21");
        item3.setAddress(address3);
        item3.setDistance(BigDecimal.valueOf(3.5));
        item3.setLatitude(45.03049);
        item3.setLongitude(7.69982);

        ShopItemConsumer item4 = new ShopItemConsumer();
        item4.setName("Fish & (micro)chips");
        item4.setShopCategory(homeCat);
        Address address4 = new Address();
        address4.setStreet("Via riva 21");
        item4.setAddress(address4);
        item4.setDistance(BigDecimal.valueOf(0.2));
        item4.setLatitude(45.07449);
        item4.setLongitude(7.63682);

        physicalShops.add(item1);
        physicalShops.add(item2);
        physicalShops.add(item3);
        physicalShops.add(item4);

        return physicalShops;
    }

    public static ArrayList<ShopItem> fakeStoreLocatorSearchTaskResponseEcommerce() {
        ArrayList<ShopItem> ecommerceShops = new ArrayList<>();

        ShopCategory restaurantCat= new ShopCategory();
        restaurantCat.setTitle("Ristoranti");
        restaurantCat.setImageName("cat_ristoranti");
        restaurantCat.setUuid("afafge1");

        ShopCategory homeCat= new ShopCategory();
        homeCat.setTitle("Casa");
        homeCat.setImageName("cat_casa");
        homeCat.setUuid("afafge2");

        ShopItemConsumer item1 = new ShopItemConsumer();
        item1.setName("E - Bar Nazionale");
        item1.setShopCategory(restaurantCat);

        ShopItemConsumer item2 = new ShopItemConsumer();
        item2.setName("E - Kebab di chieri");
        item2.setShopCategory(restaurantCat);

        ShopItemConsumer item3 = new ShopItemConsumer();
        item3.setName("E - Q8");
        item3.setShopCategory(homeCat);

        ShopItemConsumer item4 = new ShopItemConsumer();
        item4.setName("E - Fish & (micro)chips");
        item4.setShopCategory(homeCat);

        ecommerceShops.add(item1);
        ecommerceShops.add(item2);
        ecommerceShops.add(item3);
        ecommerceShops.add(item4);

        return ecommerceShops;
    }
}
