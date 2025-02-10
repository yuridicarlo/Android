package it.bancomatpay.sdkui.model;

public interface InteractionListener {
    void onConsumerInteraction(ItemInterfaceConsumer item);
    void onMerchantInteraction(ItemInterfaceConsumer item);
    void onImageConsumerInteraction(ItemInterfaceConsumer item);
    void onImageMerchantInteraction(ItemInterfaceConsumer item);
}
