package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.DateDirectDebitMerchant;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.model.DirectDebit;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.DirectDebitSeparator;

public class DirectDebitMerchantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context = PayCore.getAppContext();
    List<DateDisplayData> mValues;
    private final DirectDebitMerchantAdapter.InteractionListener mListener;

    public DirectDebitMerchantAdapter(List<DateDisplayData> mValues, DirectDebitMerchantAdapter.InteractionListener listener) {
        this.mValues = getList(mValues);
        this.mListener = listener;
    }

    private List<DateDisplayData> getList(List<DateDisplayData> items) {
        List<DateDisplayData> itemWithSeparator = new ArrayList<>();
        if (!items.isEmpty()) {
            String date = items.get(0).getDateName();
            DirectDebitSeparator item = new DirectDebitSeparator();
            item.setDateName(date);
            itemWithSeparator.add(item);

            for (int i = 0; i < items.size(); i++) {
                if (!(items.get(i).getDateName()).equals(date)) {
                    date = items.get(i).getDateName();
                    DirectDebitSeparator itemSeparator = new DirectDebitSeparator();
                    itemSeparator.setDateName(date);
                    itemWithSeparator.add(itemSeparator);
                }
                itemWithSeparator.add(items.get(i));

            }
        }
        return itemWithSeparator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_debit_merchant_item, viewGroup, false);
            return new ViewHolderMerchant(view);
        }
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direct_debit_separator_item, viewGroup, false);
        return new ViewHolderSeparator(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            ViewHolderMerchant holderMerchant = (ViewHolderMerchant) viewHolder;
            holderMerchant.mItem = (DateDirectDebitMerchant) mValues.get(i);

            holderMerchant.merchantName.setText(holderMerchant.mItem.getDirectDebit().getMerchantName());
            switch (holderMerchant.mItem.getDirectDebit().getMerchantStatus()){
                case ENABLED:
                    holderMerchant.directDebitStatus.setText(R.string.direct_debit_merchant_detail_status_active);
                    break;
                case DISABLED:
                    holderMerchant.directDebitStatus.setText(R.string.direct_debit_merchant_detail_status_inactive);
                    holderMerchant.directDebitStatus.setTextColor(ContextCompat.getColor(context, R.color.notification_circle_red));

            }
            holderMerchant.view.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onMerchantElementInteraction(holderMerchant.mItem.getDirectDebit());
                }
            }));
        } else {
            ViewHolderSeparator holderSeparator = (ViewHolderSeparator) viewHolder;
            holderSeparator.mItem = (DirectDebitSeparator) mValues.get(i);
            holderSeparator.textSeparator.setText(holderSeparator.mItem.getDateName());
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else return 0;
    }

    public void updateList(ArrayList<DateDisplayData> merchantList) {
        mValues.clear();
        mValues.addAll(getList(merchantList));
        notifyDataSetChanged();
    }

    private static class ViewHolderMerchant extends RecyclerView.ViewHolder {

        TextView merchantName;
        TextView directDebitStatus;
        View view;

        DateDirectDebitMerchant mItem;

        ViewHolderMerchant(View view) {
            super(view);
            merchantName = view.findViewById(R.id.direct_debit_merchant_name);
            directDebitStatus = view.findViewById(R.id.direct_debit_status);

            this.view = view;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (mValues.get(position) instanceof DirectDebitSeparator) {
            viewType = 1;
        }
        return viewType;
    }

    private static class ViewHolderSeparator extends RecyclerView.ViewHolder {

        TextView textSeparator;
        View view;
        DirectDebitSeparator mItem;

        ViewHolderSeparator(View view) {
            super(view);
            textSeparator = view.findViewById(R.id.direct_debit_text_separator);
            this.view = view;
        }
    }

    public interface InteractionListener
    {
        void onMerchantElementInteraction(DirectDebit item);
    }
}