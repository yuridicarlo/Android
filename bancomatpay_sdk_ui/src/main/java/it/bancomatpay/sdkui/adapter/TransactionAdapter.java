package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.model.DateTransaction;
import it.bancomatpay.sdkui.model.Transaction;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.TransactionSeparator;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DateDisplayData> mValues;
    private final TransactionAdapter.InteractionListener mListener;
    private final Context context = PayCore.getAppContext();

    public TransactionAdapter(List<DateDisplayData> items, TransactionAdapter.InteractionListener listener) {
        mValues = getList(items);
        mListener = listener;
    }

    private List<DateDisplayData> getList(List<DateDisplayData> items) {
        List<DateDisplayData> itemsWithSeparator = new ArrayList<>();

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
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transactions_list_item, parent, false);
            return new ViewHolderUser(view);
        }
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_separator_item, parent, false);
        return new ViewHolderSeparator(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            final ViewHolderUser holderUser = (ViewHolderUser) holder;
            holderUser.mItem = (DateTransaction) mValues.get(position);

            ContactItem contact = ApplicationModel.getInstance().getContactItem(holderUser.mItem.getTransaction().getPhoneNumber());

            if (contact == null || holderUser.mItem.getTransaction().getTransactionType() == TransactionType.P2B) {
                holderUser.title.setText(holderUser.mItem.getTransaction().getTitle());
            } else {
                holderUser.title.setText(contact.getTitle());
            }
            setTransactionImage(holderUser, contact);

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);

            String sign = "";
            BigDecimal amount = BigDecimalUtils.ZERO;
            BigDecimal totalAmount = BigDecimalUtils.ZERO;

            if (holderUser.mItem.getTransaction().getAmount().signum() > 0) {
                //sign = "+ ";
                amount = holderUser.mItem.getTransaction().getAmount();
            } else if (holderUser.mItem.getTransaction().getAmount().signum() < 0) {
                sign = "- ";
                amount = holderUser.mItem.getTransaction().getAmount().multiply(BigDecimalUtils.MINUS_ONE);
            }

            if (holderUser.mItem.getTransaction().getTotalAmount() != null) {
                if (holderUser.mItem.getTransaction().getTotalAmount().signum() > 0) {
                    totalAmount = holderUser.mItem.getTransaction().getTotalAmount();
                } else if (holderUser.mItem.getTransaction().getTotalAmount().signum() < 0) {
                    sign = "- ";
                    totalAmount = holderUser.mItem.getTransaction().getTotalAmount().multiply(BigDecimalUtils.MINUS_ONE);
                }
            }  //else totalAmount = amount;

            if(totalAmount != null && (holderUser.mItem.getTransaction().getTransactionType() == TransactionType.P2B || holderUser.mItem.getTransaction().getTransactionType() == TransactionType.B2P)){
                holderUser.price.setText(String.format("%s%s", sign, format.format(totalAmount)));
            }else holderUser.price.setText(String.format("%s%s", sign, format.format(amount)));

            switch (holderUser.mItem.getTransaction().getTransactionStatus()) {
                case PAG:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_money_payment);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    break;

                case STR:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_ricevuto);
                    holderUser.label.setText(R.string.transactions_money_starling);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.transactions_money_received));
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    break;

                case RIC:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_ricevuto);
                    holderUser.label.setText(R.string.transactions_money_received);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.transactions_money_received));
                    break;

                case INV:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_money_send);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    if (TextUtils.isEmpty(holderUser.mItem.getTransaction().getIdSct())) {
                        holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    }
                    break;

                case PND:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_attesa);
                    holderUser.label.setText(R.string.transactions_money_wait);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
                case ANN_P2B:
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_money_cancel);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;

                case ANN_P2P:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_money_cancel);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;

                case ADD:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_addebito);
                    holderUser.label.setText(R.string.direct_debit_label);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
                case ATM:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.profileImage.setImageResource(R.drawable.prelievo_atm);
                    holderUser.label.setText(R.string.transactions_withdraw_executed);
                    holderUser.title.setText(context.getString(R.string.withdraw_transaction_atm_label, holderUser.mItem.getTransaction().getTitle()));
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
                case POS:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_withdraw_executed);
                    holderUser.profileImage.setImageResource(R.drawable.prelievo_pos);
                    holderUser.title.setText(context.getString(R.string.withdraw_transaction_pos_label, holderUser.mItem.getTransaction().getTitle()));
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
                case ANN_ATM:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.withdraw_transaction_detail_failed);
                    holderUser.profileImage.setImageResource(R.drawable.prelievo_atm);
                    holderUser.title.setText(context.getString(R.string.withdraw_transaction_atm_label, holderUser.mItem.getTransaction().getTitle()));
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
                case ANN_POS:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.withdraw_transaction_detail_failed);
                    holderUser.profileImage.setImageResource(R.drawable.prelievo_pos);
                    holderUser.title.setText(context.getString(R.string.withdraw_transaction_pos_label, holderUser.mItem.getTransaction().getTitle()));
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
                case CASHBACK:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_ricevuto);
                    holderUser.label.setText(R.string.transactions_money_received);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.transactions_money_received));
                    holderUser.title.setText(R.string.bpay_cashback_title);
                    holderUser.profileImage.setImageResource(R.drawable.placeholder_cashback);
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
            }
            holderUser.view.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null && (holderUser.mItem.getTransaction().getTransactionStatus() != TransactionData.Status.CASHBACK)) {
                    mListener.onListViewInteraction(holderUser.mItem.getTransaction());
                }
            }));

        } else {
            final ViewHolderSeparator holderSeparator = (ViewHolderSeparator) holder;
            holderSeparator.mItem = (TransactionSeparator) mValues.get(position);
            holderSeparator.textSeparator.setText(holderSeparator.mItem.getDateName());
        }
    }

    private void setTransactionImage(ViewHolderUser holderUser, ContactItem contact) {
        Bitmap bitmap = holderUser.mItem.getTransaction().getBitmap();
        if (bitmap != null) {
            if (holderUser.mItem.getTransaction().getTransactionType() == TransactionType.P2B) {
                holderUser.profileImage.setVisibility(View.VISIBLE);
                holderUser.profileImageCircle.setVisibility(View.INVISIBLE);
                holderUser.profileImage.setImageResource(R.drawable.placeholder_merchant);
                holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
            } else if (holderUser.mItem.getTransaction().getTransactionType() == TransactionType.P2P) {
                if (contact == null || (contact.getType() == ItemInterface.Type.NONE && TextUtils.isEmpty(contact.getDescription()))) {
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    holderUser.profileImage.setVisibility(View.VISIBLE);
                    holderUser.profileImageCircle.setVisibility(View.INVISIBLE);
                    holderUser.profileImage.setImageResource(R.drawable.placeholder_contact_list);
                } else {
                    if (contact.getType() != ContactItem.Type.NONE && holderUser.mItem.getTransaction().showBancomatLogo()) {
                        holderUser.contactConsumerLogo.setVisibility(View.VISIBLE);
                    } else {
                        holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    }

                    holderUser.profileImage.setVisibility(View.INVISIBLE);
                    holderUser.profileImageCircle.setVisibility(View.VISIBLE);
                    holderUser.profileImageCircle.setImageBitmap(bitmap);
                }
            }
            holderUser.noImageText.setVisibility(View.GONE);
        } else {
            holderUser.profileImage.setVisibility(View.VISIBLE);
            holderUser.profileImageCircle.setVisibility(View.INVISIBLE);
            holderUser.profileImage.setImageResource(R.drawable.placeholder_circle_consumer);
            holderUser.noImageText.setVisibility(View.VISIBLE);
            holderUser.noImageText.setText(holderUser.mItem.getTransaction().getInitials());
            if (contact != null && contact.getType() != ContactItem.Type.NONE && holderUser.mItem.getTransaction().showBancomatLogo()) {
                holderUser.contactConsumerLogo.setVisibility(View.VISIBLE);
            } else {
                holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (mValues.get(position) instanceof TransactionSeparator) {
            viewType = 1;
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


    private static class ViewHolderUser extends RecyclerView.ViewHolder {

        ImageView profileImageCircle;
        ImageView profileImage;
        ImageView statusImage;
        ImageView contactConsumerLogo;
        TextView price;
        TextView title;
        TextView label;
        TextView noImageText;
        View view;

        DateTransaction mItem;

        ViewHolderUser(View view) {
            super(view);
            profileImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            profileImage = view.findViewById(R.id.contact_consumer_image_profile);
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
        void onListViewInteraction(Transaction item);
    }

}