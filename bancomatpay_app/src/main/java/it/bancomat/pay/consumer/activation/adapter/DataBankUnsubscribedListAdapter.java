package it.bancomat.pay.consumer.activation.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class DataBankUnsubscribedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DataBank> mValues;
    private final UnsubscribedBankClickListener listener;

    public DataBankUnsubscribedListAdapter(List<DataBank> items, UnsubscribedBankClickListener listener) {
        this.mValues = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activation_data_bank_unsubscribed, parent, false);
            return new ViewHolderBank(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

            ViewHolderBank holderBank = (ViewHolderBank) holder;
            holderBank.mItem = mValues.get(position);
            holderBank.textPlaceholder.setText(holderBank.mItem.getLabel());
            holderBank.textPlaceholder.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(Uri.decode(holderBank.mItem.getLogoSearch()))
                    .networkPolicy(NetworkPolicy.NO_STORE)
                    .placeholder(R.drawable.empty)
                    .into(holderBank.imageBank, new Callback() {
                        @Override
                        public void onSuccess() {
                            holderBank.imageBank.setVisibility(View.VISIBLE);
                            holderBank.textPlaceholder.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holderBank.imageBank.setVisibility(View.INVISIBLE);
                            holderBank.textPlaceholder.setVisibility(View.VISIBLE);
                        }
                    });

            holderBank.mCardView.setOnClickListener(new CustomOnClickListener(v -> {
                if (listener != null) {
                    listener.onBankInteraction(holderBank.mItem);
                }
            }));

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private static class ViewHolderBank extends RecyclerView.ViewHolder {

        ImageView imageBank;
        TextView textPlaceholder;
        CardView mCardView;

        DataBank mItem;

        ViewHolderBank(View view) {
            super(view);
            imageBank = view.findViewById(R.id.image_bank);
            textPlaceholder = view.findViewById(R.id.text_placeholder);
            mCardView = view.findViewById(R.id.card_view_bank);
        }
    }

    public interface UnsubscribedBankClickListener {
        void onBankInteraction(DataBank dataBank);
    }

}

