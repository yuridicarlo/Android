package it.bancomatpay.sdkui.adapter;

import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class HomeServicesDetailAdapter extends RecyclerView.Adapter<HomeServicesDetailAdapter.ServiceViewHolder> {

    private final ArrayList<BankServices.EBankService> mValues;
    private final ServiceClickListener listener;

    public HomeServicesDetailAdapter(ArrayList<BankServices.EBankService> servicesDetailList, ServiceClickListener listener) {
        this.mValues = servicesDetailList;
        this.listener = listener;
    }

    @Override
    @NonNull
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder viewHolder, int position) {

        //se P2P
        if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.P2P) {
            viewHolder.textView.setText(R.string.exchange_card_view);
            viewHolder.imageView.setImageResource(R.drawable.servizio_scambia_denaro);
        }
        //se P2B
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.P2B) {
            viewHolder.textView.setText(R.string.merchant_card_view);
            viewHolder.imageView.setImageResource(R.drawable.servizio_paga_negozi);
        }
        //fidelity card
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.LOYALTY_CARD
                || mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.LOYALTY) {
            viewHolder.textView.setText(R.string.loyalty_card_view);
            viewHolder.imageView.setImageResource(R.drawable.servizio_loyalty);
        }
        //documents
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.DOCUMENT) {
            viewHolder.textView.setText(R.string.documents_card_view);
            viewHolder.imageView.setImageResource(R.drawable.servizio_documents);
        }
        //bank id
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.BANKID) {
            viewHolder.textView.setText(getBankUuidSpannableString());
            viewHolder.imageView.setImageResource(R.drawable.servizio_login_bankid);
        }
        //atm cardless
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.ATM) {
            viewHolder.textView.setText(getAtmCardlessSpannableString());
            viewHolder.imageView.setImageResource(R.drawable.servizio_prelievo);
        }
        //split bill
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.SPLIT_BILL) {
            viewHolder.textView.setText(R.string.split_bill_view);
            viewHolder.imageView.setImageResource(R.drawable.servizio_dividi_spesa);
        }
        //debito ricorrente
        else if (mValues.get(viewHolder.getAdapterPosition()) == BankServices.EBankService.DIRECT_DEBITS) {
            viewHolder.textView.setText(R.string.direct_debit_title);
            viewHolder.imageView.setImageResource(R.drawable.servizio_addebiti);
        }

        viewHolder.cardView.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null && !mValues.isEmpty()) {
                listener.onItemClick(mValues.get(position));
            }
        }));

    }

    private SpannableStringBuilder getBankUuidSpannableString() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(PayCore.getAppContext().getString(R.string.servizio_bank_id)).append(" ");
        builder.setSpan(new ImageSpan(PayCore.getAppContext(), R.drawable.logo_bancomat_blue_small),
                builder.length() - 1, builder.length(), 0);

        return builder;
    }

    private SpannableStringBuilder getAtmCardlessSpannableString() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(PayCore.getAppContext().getString(R.string.servizio_atm_cardless)).append(" ");
        builder.setSpan(new ImageSpan(PayCore.getAppContext(), R.drawable.logo_bancomat_blue_small),
                builder.length() - 1, builder.length(), 0);

        return builder;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        CardView cardView;

        ServiceViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.info_text);
            imageView = itemView.findViewById(R.id.cardView_image);
            cardView = itemView.findViewById(R.id.card_view);
        }

    }

    public interface ServiceClickListener {
        void onItemClick(BankServices.EBankService detail);
    }

}