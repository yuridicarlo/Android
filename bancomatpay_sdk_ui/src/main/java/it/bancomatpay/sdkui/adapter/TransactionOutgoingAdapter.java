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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.model.DateTransactionOutgoing;
import it.bancomatpay.sdkui.model.TransactionOutgoing;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.TransactionSeparator;

public class TransactionOutgoingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DateDisplayData> mValues;
    private final TransactionOutgoingAdapter.InteractionListener mListener;
    private final Context context = PayCore.getAppContext();

    public TransactionOutgoingAdapter(List<DateDisplayData> items, TransactionOutgoingAdapter.InteractionListener listener) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            ViewHolderUser holderUser = (ViewHolderUser) holder;
            holderUser.mItem = (DateTransactionOutgoing) mValues.get(position);

            ContactItem contact = ApplicationModel.getInstance().getContactItem(holderUser.mItem.getTransaction().getPhoneNumber());
            if (contact == null) {
                holderUser.title.setText(holderUser.mItem.getTransaction().getTitle());
            } else {
                holderUser.title.setText(contact.getTitle());
            }

            Bitmap bitmap = holderUser.mItem.getTransaction().getBitmap();
            if (bitmap != null) {
                /*holderUser.profileImage.setVisibility(View.INVISIBLE);
                holderUser.profileImageCircle.setVisibility(View.VISIBLE);
                holderUser.profileImageCircle.setImageBitmap(bitmap);
                holderUser.noImageText.setVisibility(View.GONE);*/
                if (contact == null || (contact.getType() == ItemInterface.Type.NONE && TextUtils.isEmpty(contact.getDescription()))) {
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    holderUser.profileImage.setVisibility(View.VISIBLE);
                    holderUser.profileImageCircle.setVisibility(View.INVISIBLE);
                    holderUser.profileImage.setImageResource(R.drawable.placeholder_contact_list);
                } else {
                    if (holderUser.mItem.getTransaction().showBancomatLogo()) {
                        holderUser.contactConsumerLogo.setVisibility(View.VISIBLE);
                    } else {
                        holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                    }

                    holderUser.profileImage.setVisibility(View.INVISIBLE);
                    holderUser.profileImageCircle.setVisibility(View.VISIBLE);
                    holderUser.profileImageCircle.setImageBitmap(bitmap);
                }
            } else {
                /*holderUser.profileImage.setVisibility(View.VISIBLE);
                holderUser.profileImageCircle.setVisibility(View.INVISIBLE);
                holderUser.profileImage.setImageResource(R.drawable.placeholder_circle_consumer);
                holderUser.noImageText.setVisibility(View.VISIBLE);
                holderUser.noImageText.setText(holderUser.mItem.getAccess().getInitials());*/
                holderUser.profileImage.setVisibility(View.VISIBLE);
                holderUser.profileImageCircle.setVisibility(View.INVISIBLE);
                holderUser.profileImage.setImageResource(R.drawable.placeholder_circle_consumer);
                holderUser.noImageText.setVisibility(View.VISIBLE);
                holderUser.noImageText.setText(holderUser.mItem.getTransaction().getInitials());
                if (holderUser.mItem.getTransaction().showBancomatLogo()) {
                    holderUser.contactConsumerLogo.setVisibility(View.VISIBLE);
                } else {
                    holderUser.contactConsumerLogo.setVisibility(View.INVISIBLE);
                }
            }

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);

            switch (holderUser.mItem.getTransaction().getTransactionStatus()) {

                case SENT:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_outgoing_payment_request_sent);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;

                case EXPIRED:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_outgoing_payment_request_expired);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;

                case FAILED:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_outgoing_payment_request_failed);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;

                default:
                    holderUser.statusImage.setImageResource(R.drawable.movimenti_denaro_inviato);
                    holderUser.label.setText(R.string.transactions_outgoing_payment_request_sent);
                    holderUser.price.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
                    break;
            }

            holderUser.price.setText(format.format(holderUser.mItem.getTransaction().getAmount()));

            holderUser.view.setOnClickListener(new CustomOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onOutgoingRequestListViewInteraction(holderUser.mItem.getTransaction());
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
        int viewType = 0;
        if (mValues.get(position) instanceof TransactionSeparator) {
            viewType = 1;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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

        ImageView profileImage;
        ImageView profileImageCircle;
        ImageView statusImage;
        ImageView contactConsumerLogo;
        TextView price;
        TextView title;
        TextView label;
        TextView noImageText;
        View view;

        DateTransactionOutgoing mItem;

        ViewHolderUser(View view) {
            super(view);
            profileImage = view.findViewById(R.id.contact_consumer_image_profile);
            profileImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            statusImage = view.findViewById(R.id.transaction_image_status);
            contactConsumerLogo = view.findViewById(R.id.contact_consumer_is_active);
            price = view.findViewById(R.id.transaction_price);
            title = view.findViewById(R.id.contact_consumer_name);
            label = view.findViewById(R.id.text_transaction_state);
            noImageText = view.findViewById(R.id.contact_consumer_letter);
            this.view = view;
        }

    }

    public interface InteractionListener {
        void onOutgoingRequestListViewInteraction(TransactionOutgoing item);
    }

}