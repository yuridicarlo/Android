package it.bancomat.pay.consumer.init.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class MultiIbanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<InstrumentData> mValues;
    private final String logoBank;
    private final IbanListClickListener mListener;

    private ImageView lastChecked;

    public MultiIbanAdapter(List<InstrumentData> items, String logo, IbanListClickListener listener) {
        this.mValues = items;
        this.logoBank = logo;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_multi_iban_bank, parent, false);
        return new ViewHolderIban(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        ViewHolderIban holderIban = (ViewHolderIban) holder;

        holderIban.mItem = mValues.get(position);

        Picasso.get()
                .load(logoBank)
                .networkPolicy(NetworkPolicy.NO_STORE)
                .placeholder(R.drawable.empty)
                .error(R.drawable.empty)
                .into(holderIban.imageBankLogo, new Callback() {
                    @Override
                    public void onSuccess() {
                        holderIban.imageBankLogo.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holderIban.imageBankLogo.setVisibility(View.INVISIBLE);
                    }
                });

        holderIban.textIban.setText(holderIban.mItem.getIban());
        holderIban.view.setOnClickListener(new CustomOnClickListener(v -> {
            if (lastChecked != null) {
                lastChecked.setVisibility(View.INVISIBLE);
            }
            lastChecked = holderIban.imageCheck;
            holderIban.imageCheck.setVisibility(View.VISIBLE);
            if (mListener != null) {
                mListener.onInstrumentDataClicked(holderIban.mItem);
            }
        }));

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private static class ViewHolderIban extends RecyclerView.ViewHolder {

        ImageView imageBankLogo;
        TextView textIban;
        ImageView imageCheck;
        View view;
        InstrumentData mItem;

        ViewHolderIban(View view) {
            super(view);
            imageBankLogo = view.findViewById(R.id.profile_iban_image_logo);
            textIban = view.findViewById(R.id.profile_iban_text);
            imageCheck = view.findViewById(R.id.profile_iban_image_check);
            this.view = view;
        }
    }

    public interface IbanListClickListener {
        void onInstrumentDataClicked(InstrumentData instrumentData);
    }

}

