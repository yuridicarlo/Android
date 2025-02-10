package it.bancomatpay.sdkui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class BlockedMerchantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int MERCHANT_VIEW_TYPE = 1;

    private List<BankIdMerchantData> mValues;
    private final InteractionListener mListener;

    public BlockedMerchantsAdapter(List<BankIdMerchantData> items, InteractionListener listener) {
        this.mValues = getList(items);
        this.mListener = listener;
    }

    private List<BankIdMerchantData> getList(List<BankIdMerchantData> items) {
        List<BankIdMerchantData> newList = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            newList.add(new BankIdMerchantData());
            newList.addAll(items);
        }
        return newList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MERCHANT_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.blocked_merchant_item, parent, false);
            return new ViewHolderBlockedMerchant(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.blocked_merchant_header, parent, false);
            return new ViewHolderHeader(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderBlockedMerchant) {

            ViewHolderBlockedMerchant holderBlockedMerchant = (ViewHolderBlockedMerchant) holder;
            holderBlockedMerchant.mItem = mValues.get(position);

            holderBlockedMerchant.merchantName.setText(holderBlockedMerchant.mItem.getMerchantName());
            holderBlockedMerchant.merchantAddress.setVisibility(View.GONE);

            holderBlockedMerchant.cardView.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onListInteraction(holderBlockedMerchant.mItem);
                }
            }));

        }

    }

    public void updateList(ArrayList<BankIdMerchantData> merchantList) {
        mValues = getList(merchantList);
        notifyDataSetChanged();
    }

    public void removeItem(BankIdMerchantData merchant) {
        Iterator<BankIdMerchantData> iterator = mValues.iterator();
        while (iterator.hasNext()) {
            BankIdMerchantData item = iterator.next();
            if (!TextUtils.isEmpty(item.getMerchantTag()) && item.getMerchantTag().equals(merchant.getMerchantTag())
                    && !TextUtils.isEmpty(item.getMerchantName()) && item.getMerchantName().equals(merchant.getMerchantName())) {
                int itemIndex = mValues.indexOf(item);
                iterator.remove();
                notifyItemRemoved(itemIndex);
                break;
            }
        }
        if (mValues.size() == 1
                && TextUtils.isEmpty(mValues.get(0).getMerchantTag())
                && TextUtils.isEmpty(mValues.get(0).getMerchantName())) {
            //Rimuovo header
            mValues.remove(0);
            notifyItemRemoved(0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_VIEW_TYPE;
        }
        return MERCHANT_VIEW_TYPE;
    }

    private static class ViewHolderHeader extends RecyclerView.ViewHolder {

        TextView textHeader;

        ViewHolderHeader(View view) {
            super(view);
            textHeader = view.findViewById(R.id.text_header);
        }
    }

    public static class ViewHolderBlockedMerchant extends RecyclerView.ViewHolder {

        TextView merchantName;
        TextView merchantAddress;
        ImageView merchantImageProfileCircle;
        ImageView imageMerchantLock;
        CardView cardView;
        View mView;

        BankIdMerchantData mItem;

        ViewHolderBlockedMerchant(View view) {
            super(view);
            mView = view;
            merchantName = view.findViewById(R.id.merchant_name);
            merchantAddress = view.findViewById(R.id.merchant_address);
            merchantImageProfileCircle = view.findViewById(R.id.merchant_image_profile);
            imageMerchantLock = view.findViewById(R.id.image_merchant_lock);
            cardView = view.findViewById(R.id.merchant_container);
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    public interface InteractionListener {
        void onListInteraction(BankIdMerchantData blockedMerchant);
    }

}
