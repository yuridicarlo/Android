package it.bancomat.pay.consumer.storeLocator.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopItem;

import it.bancomatpay.sdkui.model.ListTile;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.MapperConsumer;

public class MerchantMapListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final BcmLocation mLastLocation;
    private List<ListTile> mValues;
    private List<ListTile> mValuesBackup;
    private List<ShopItem> mValuesUnprocessed;
    private final MerchantMapListAdapter.InteractionListener mListener;
    private Context mContext;

    private CharSequence lastFilter = "";

    RecyclerView mRecyclerView;

    public MerchantMapListAdapter(List<ShopItem> items, InteractionListener listener, Context context, BcmLocation mLastLocation) {
        mValues = MapperConsumer.shopItemConsumerListTilesFromShopItemList(items);
        mValuesUnprocessed = items;
        mValuesBackup = new ArrayList();
        mValuesBackup.addAll(mValues);
        mListener = listener;
        mContext = context;
        this.mLastLocation = mLastLocation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_tile_item_4_gmaps, parent, false);
        return new MerchantMapListAdapter.ListTileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MerchantMapListAdapter.ListTileViewHolder holderItem = (MerchantMapListAdapter.ListTileViewHolder) holder;
        ListTile mItem = mValues.get(position);
        ShopItem shopItem = mValuesUnprocessed.get(position);

        holderItem.title.setText(mItem.getTitle());
        holderItem.leadingIcon.setImageResource(mItem.getLeadingIconRes());

        if(mItem.getSubtitle() != null) {
            holderItem.subtitle.setText(mItem.getSubtitle());
            holderItem.subtitle.setVisibility(View.VISIBLE);
        } else {
            holderItem.subtitle.setVisibility(View.GONE);
        }

        if(mItem.getTrailingText() != null) {
            holderItem.trailingText.setText(mItem.getTrailingText());
            holderItem.trailingText.setVisibility(View.VISIBLE);
        } else {
            holderItem.trailingText.setVisibility(View.GONE);
        }

        holderItem.view.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTap(shopItem);
            }
        }));

        SpannableString spannableString = new SpannableString(mContext.getString(R.string.directions));
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holderItem.directionsTextView.setText(spannableString);

        holderItem.trailingText.setText(mContext.getString(R.string.km, shopItem.getDistance()));


        if(shopItem.getLatitude()!=0 && shopItem.getLongitude()!=0) {

            holderItem.directionsLayout.setVisibility(View.VISIBLE);
            holderItem.directionsLayout.setOnClickListener(v -> {
                mListener.onDirectionsTap(shopItem);
            });
        }else{
            holderItem.directionsLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    private static class ListTileViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        TextView trailingText;
        ImageView leadingIcon;
        TextView directionsTextView;
        LinearLayout directionsLayout;
        View view;

        ListTileViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            trailingText = view.findViewById(R.id.trailing_text);
            leadingIcon = view.findViewById(R.id.leading_icon);
            directionsTextView = view.findViewById(R.id.directions_textview);
            directionsLayout = view.findViewById(R.id.directions_layout);
            this.view = view;
        }
    }

    public ShopItem bubbleItemUp(Object tag) {
        ShopItem correspondingItem = null;
        if(tag instanceof String) {
            String tagStr = (String) tag;
            if(mRecyclerView != null) {
                ArrayList<ShopItem> newMValuesUnprocessed = new ArrayList<>();
                for(int i = 0; i < mValuesUnprocessed.size(); i++) {
                    ShopItem item = mValuesUnprocessed.get(i);
                    if(item.getName().equals(tagStr)) {
                        correspondingItem = item;
                        newMValuesUnprocessed.add(0, item);
                    } else {
                        newMValuesUnprocessed.add(item);
                    }
                }
                mValuesUnprocessed = newMValuesUnprocessed;
                mValues = MapperConsumer.shopItemConsumerListTilesFromShopItemList(newMValuesUnprocessed);
                mValuesBackup = new ArrayList();
                mValuesBackup.addAll(mValues);

                mRecyclerView.smoothScrollToPosition(0);
                notifyDataSetChanged();
            }
        }
        return correspondingItem;
    }

    public interface InteractionListener {
        void onTap(ShopItem item);
        void onDirectionsTap(ShopItem item);
    }
}
