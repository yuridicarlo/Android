package it.bancomatpay.sdkui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.SeparatorItemConsumer;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolderShop> implements Filterable {

    private final NumberFormat formatter = NumberFormat.getInstance(Locale.ITALY);

    private List<ItemInterfaceConsumer> mValues;
    private final List<ItemInterfaceConsumer> mValuesBackup;
    private final InteractionListener mListener;

    public ShopsAdapter(List<ItemInterfaceConsumer> items, InteractionListener listener) {
        mValues = items;
        mValuesBackup = new ArrayList<>();
        mValuesBackup.addAll(mValues);
        mListener = listener;
    }

    @NonNull
    @Override
    public ShopsAdapter.ViewHolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_money_shop_item, parent, false);
        return new ViewHolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopsAdapter.ViewHolderShop holderItemShop, int position) {
        holderItemShop.mItem = (ShopsDataMerchant) mValues.get(position);
        if (holderItemShop.mItem.getDistance() != null) {
            String distance = formatter.format(holderItemShop.mItem.getDistance());
            holderItemShop.distance.setText(PayCore.getAppContext().getString(R.string.shop_list_item_km, distance));
        } else {
            holderItemShop.distance.setText("");
        }
        holderItemShop.name.setText(holderItemShop.mItem.getTitle());
        holderItemShop.address.setText(holderItemShop.mItem.getDescription());
        if (holderItemShop.mItem.getBitmap() != null) {
            if (holderItemShop.mItem.isImageAvailable()) {
                holderItemShop.profileImage.setVisibility(View.INVISIBLE);
                holderItemShop.profileImageCircle.setVisibility(View.VISIBLE);
                holderItemShop.profileImageCircle.setImageBitmap(holderItemShop.mItem.getBitmap());
            } else {
                holderItemShop.profileImage.setVisibility(View.VISIBLE);
                holderItemShop.profileImageCircle.setVisibility(View.INVISIBLE);
                if (holderItemShop.mItem.getShopItem().getMerchantType() == ShopItem.EMerchantType.STANDARD) {
                    holderItemShop.profileImage.setImageResource(R.drawable.placeholder_merchant);
                } else if (holderItemShop.mItem.getShopItem().getMerchantType() == ShopItem.EMerchantType.PREAUTHORIZED) {
                    holderItemShop.profileImage.setImageResource(R.drawable.placeholder_petrol);
                }
            }
        }
        holderItemShop.profileImage.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onImageMerchantInteraction(holderItemShop.mItem);
            }
        }));
        holderItemShop.profileImageCircle.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onImageMerchantInteraction(holderItemShop.mItem);
            }
        }));
        holderItemShop.view.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onMerchantInteraction(holderItemShop.mItem);
            }
        }));

    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mValues = (List<ItemInterfaceConsumer>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<ItemInterfaceConsumer> filteredArrayDataBank = new ArrayList<>();

                for (int i = 0; i < mValuesBackup.size(); i++) {
                    if (!(mValuesBackup.get(i) instanceof SeparatorItemConsumer)) {

                        if (mValuesBackup.get(i).performFilter(constraint.toString())) {
                            filteredArrayDataBank.add(mValuesBackup.get(i));
                        }
                    }
                }

                CustomLogger.d("ITEM", "" + filteredArrayDataBank.size());

                results.count = filteredArrayDataBank.size();
                results.values = filteredArrayDataBank;

                return results;
            }
        };
    }

    static class ViewHolderShop extends RecyclerView.ViewHolder {

        ImageView profileImage;
        ImageView profileImageCircle;
        TextView distance;
        TextView name;
        TextView address;
        View view;

        ShopsDataMerchant mItem;

        ViewHolderShop(View view) {
            super(view);
            profileImage = view.findViewById(R.id.contact_consumer_image_profile);
            profileImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            name = view.findViewById(R.id.text_merchant_name);
            address = view.findViewById(R.id.text_merchant_address);
            distance = view.findViewById(R.id.text_merchant_distance);
            this.view = view;
        }
    }

}
