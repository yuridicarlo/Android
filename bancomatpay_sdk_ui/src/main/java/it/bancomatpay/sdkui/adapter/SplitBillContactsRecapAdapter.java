package it.bancomatpay.sdkui.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.SplitBillContactRecapItemBinding;
import it.bancomatpay.sdkui.model.SplitItemConsumer;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class SplitBillContactsRecapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = SplitBillContactsRecapAdapter.class.getSimpleName();

    public static final int CONTACT_VIEW_TYPE = 1;
    public static final int ME_VIEW_TYPE = 2;

    private List<SplitItemConsumer> mValues;
    private String meLabel;

    public SplitBillContactsRecapAdapter(String meLabel, List<SplitItemConsumer> contacts) {
        this.mValues = contacts;
        this.meLabel = meLabel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SplitBillContactRecapItemBinding itemBinding = SplitBillContactRecapItemBinding.inflate(layoutInflater, parent, false);
        if (viewType == CONTACT_VIEW_TYPE) {
            return new SplitBillContactsRecapAdapter.CircleViewHolder(itemBinding);
        } else {
            return new SplitBillContactsRecapAdapter.CircleMeViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SplitBillContactsRecapAdapter.CircleViewHolder) {
            ((SplitBillContactsRecapAdapter.CircleViewHolder) holder).bind(mValues.get(position), position);
        } else {
            ((SplitBillContactsRecapAdapter.CircleMeViewHolder) holder).bind(meLabel, mValues.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && meLabel != null) {
            return ME_VIEW_TYPE;
        } else {
            return CONTACT_VIEW_TYPE;
        }
    }

    private static class CircleViewHolder extends RecyclerView.ViewHolder {

        SplitBillContactRecapItemBinding binding;
        public CircleViewHolder(@NonNull SplitBillContactRecapItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SplitItemConsumer contactItem, int position) {
            binding.contactConsumerName.setText(contactItem.getTitle());
            binding.textPhoneNumber.setText(contactItem.getPhoneNumber());
            binding.amount.setText(StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(contactItem.getAmount())));
            if(contactItem.showBancomatLogo()) {
                binding.contactConsumerIsActive.setVisibility(View.VISIBLE);
            } else {
                binding.contactConsumerIsActive.setVisibility(View.GONE);
            }

            Bitmap bitmap = contactItem.getBitmap();
            if(bitmap != null) {
                binding.contactConsumerImageProfileCircle.setImageBitmap(contactItem.getBitmap());
                binding.contactConsumerLetter.setVisibility(View.GONE);
            } else {
                binding.contactConsumerLetter.setText(contactItem.getLetter());
                binding.contactConsumerLetter.setVisibility(View.VISIBLE);
            }

            if (contactItem.getSplitBillState() != null) {
                binding.textPhoneNumber.setVisibility(View.GONE);
                binding.transactionImageStatus.setVisibility(View.VISIBLE);
                binding.transactionTextStatus.setVisibility(View.VISIBLE);

                switch (contactItem.getSplitBillState()) {
                    case ACCEPTED:
                        binding.transactionTextStatus.setText(R.string.transaction_detail_money_done);
                        binding.transactionImageStatus.setImageResource(R.drawable.movimenti_denaro_inviato);
                        break;
                    case SENT:
                        binding.transactionTextStatus.setText(R.string.transactions_money_wait);
                        binding.transactionImageStatus.setImageResource(R.drawable.movimenti_denaro_attesa);
                        break;
                    case FAILED:
                        binding.transactionTextStatus.setText(R.string.transaction_detail_money_refused);
                        binding.transactionImageStatus.setImageResource(R.drawable.ico_non_disponibile);
                        break;
                    case EXPIRED:
                        binding.transactionTextStatus.setText(R.string.transaction_detail_money_expired);
                        binding.transactionImageStatus.setImageResource(R.drawable.ico_non_disponibile);
                        break;
                    case NO_BPAY:
                        binding.transactionTextStatus.setText(R.string.transactions_money_no_bpay);
                        binding.transactionImageStatus.setImageResource(R.drawable.ico_non_disponibile);
                        break;
                }
            }
        }
    }

    private static class CircleMeViewHolder extends RecyclerView.ViewHolder {

        SplitBillContactRecapItemBinding binding;

        public CircleMeViewHolder(@NonNull SplitBillContactRecapItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String meLabel, SplitItemConsumer contactItem) {
            binding.contactConsumerName.setText(meLabel);
            binding.contactConsumerLetter.setText(contactItem.getLetter());
            binding.textPhoneNumber.setVisibility(View.GONE);
            binding.amount.setText(StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(contactItem.getAmount())));

            if(contactItem.isImageAvailable()) {
                binding.contactConsumerImageProfileCircle.setImageBitmap(contactItem.getBitmap());
                binding.contactConsumerLetter.setVisibility(View.GONE);
            } else {
                binding.contactConsumerLetter.setText(contactItem.getLetter());
                binding.contactConsumerLetter.setVisibility(View.VISIBLE);
            }
        }

    }
}
