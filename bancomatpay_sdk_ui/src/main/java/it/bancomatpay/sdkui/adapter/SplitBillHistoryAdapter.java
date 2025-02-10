package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.utilities.TransactionSeparator;

public class SplitBillHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DateDisplayData> mValues;
    private final InteractionListener mListener;
    private final Context context = PayCore.getAppContext();

    private int TRANSACTION = 0;
    private int SEPARATOR = 1;

    public SplitBillHistoryAdapter(List<SplitBillHistory> items, InteractionListener listener) {
        mValues = getList(items);
        mListener = listener;
    }

    private List<DateDisplayData> getList(List<SplitBillHistory> items) {
        List<DateDisplayData> itemsWithSeparator = new ArrayList<>();
        if(items.isEmpty()) return itemsWithSeparator;

        String date = items.get(0).getDateName();
        String shortDate = items.get(0).getShortDateName();
        TransactionSeparator item = new TransactionSeparator();
        item.setDateName(date);
        item.setShortDateName(shortDate);
        itemsWithSeparator.add(item);

        for (int i = 0; i < items.size(); i++) {
            if (!(items.get(i).getDateName()).equals(date)) {
                date = items.get(i).getDateName();
                shortDate = items.get(i).getShortDateName();

                TransactionSeparator itemSeparator = new TransactionSeparator();
                itemSeparator.setDateName(date);
                itemSeparator.setShortDateName(shortDate);
                itemsWithSeparator.add(itemSeparator);
            }
            itemsWithSeparator.add(items.get(i));
        }
        return itemsWithSeparator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TRANSACTION) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transactions_list_item, parent, false);
            return new ViewHolderHistoryItem(view);
        }
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_separator_item, parent, false);
        return new ViewHolderSeparator(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == TRANSACTION) {
            final ViewHolderHistoryItem historyHolder = (ViewHolderHistoryItem) holder;
            SplitBillHistory mItem = (SplitBillHistory) mValues.get(position);

            historyHolder.title.setText(mItem.getCausal());
            historyHolder.label.setText(mItem.getCausal());
            historyHolder.leadingImage.setImageResource(R.drawable.dividi_spesa);
            historyHolder.contactConsumerLogo.setVisibility(View.INVISIBLE);

            BigDecimal totalAmount = BigDecimalUtils.getBigDecimalFromCents(mItem.getAmount());
            historyHolder.price.setText(StringUtils.getFormattedValue(totalAmount));


            historyHolder.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
            historyHolder.label.setText(R.string.transactions_money_send);
            historyHolder.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));

            historyHolder.view.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onListViewInteraction(mItem);
                }
            }));

        } else {
            final ViewHolderSeparator holderSeparator = (ViewHolderSeparator) holder;
            holderSeparator.mItem = (TransactionSeparator) mValues.get(position);
            holderSeparator.textSeparator.setText(holderSeparator.mItem.getDateName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = TRANSACTION;
        if (mValues.get(position) instanceof TransactionSeparator) {
            viewType = SEPARATOR;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    private static class ViewHolderSeparator extends RecyclerView.ViewHolder {

        TextView textSeparator;
        View view;

        TransactionSeparator mItem;

        ViewHolderSeparator(View view) {
            super(view);
            textSeparator = view.findViewById(R.id.transactions_text_separator);
            this.view = view;
        }
    }


    private static class ViewHolderHistoryItem extends RecyclerView.ViewHolder {

        ImageView leadingImageCircle;
        ImageView leadingImage;
        ImageView statusImage;
        ImageView contactConsumerLogo;
        TextView price;
        TextView title;
        TextView label;
        TextView noImageText;
        View view;

        SplitBillHistory mItem;

        ViewHolderHistoryItem(View view) {
            super(view);
            leadingImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            leadingImage = view.findViewById(R.id.contact_consumer_image_profile);
            statusImage = view.findViewById(R.id.transaction_image_status);
            price = view.findViewById(R.id.transaction_price);
            title = view.findViewById(R.id.contact_consumer_name);
            label = view.findViewById(R.id.text_transaction_state);
            noImageText = view.findViewById(R.id.contact_consumer_letter);
            contactConsumerLogo = view.findViewById(R.id.contact_consumer_is_active);

            this.view = view;
        }

    }

    public interface InteractionListener {
        void onListViewInteraction(SplitBillHistory item);
    }

}