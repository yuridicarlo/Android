package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.SeparatorItemConsumer;
import it.bancomatpay.sdkui.model.SeparatorItemConsumerBlocked;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.fastscroll.PopupTextProvider;

public class BlockedContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, PopupTextProvider {

    private final Context context;
    private List<ItemInterfaceConsumer> mValues;
    private List<ItemInterfaceConsumer> mValuesBackup;

    private final InteractionListener mListener;

    private CharSequence lastFilter = "";

    private static final int ITEM_TYPE_CONSUMER = 1;
    private static final int ITEM_TYPE_EMPTY = 2;
    private static final int ITEM_TYPE_CONSUMER_COMPLEX = 3;
    private static final int ITEM_TYPE_CONSUMER_BLOCKED = 4;
    private static final int ITEM_TYPE_HEADER = 5;

    void setLastFilter(CharSequence lastFilter) {
        this.lastFilter = lastFilter;
    }

    public BlockedContactsAdapter(Context context, List<ItemInterfaceConsumer> items, InteractionListener listener) {
        this.context = context;
        mValues = getList(items);
        mValuesBackup = mValues;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE_CONSUMER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blocked_contacts_item_consumer, parent, false);
                return new ViewHolderContactConsumer(view);
            case ITEM_TYPE_EMPTY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_view_holder, parent, false);
                return new EmptyViewHolder(view);
            case ITEM_TYPE_CONSUMER_COMPLEX:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blocked_contacts_item_consumer, parent, false);
                return new ViewHolderContactConsumer(view);
            case ITEM_TYPE_CONSUMER_BLOCKED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blocked_contacts_item_consumer, parent, false);
                return new ViewHolderContactConsumer(view);
            case ITEM_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blocked_contacts_separator_item, parent, false);
                return new ViewHolderBlockedHeader(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blocked_contacts_separator_item, parent, false);
                return new ViewHolderSeparator(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case ITEM_TYPE_CONSUMER:
                ViewHolderContactConsumer holderItem = (ViewHolderContactConsumer) holder;
                manageViewHolder(holderItem, position);
                break;
            case ITEM_TYPE_EMPTY:
                break;
            case ITEM_TYPE_CONSUMER_COMPLEX:
                ViewHolderContactConsumer holderContactComplex = (ViewHolderContactConsumer) holder;
                manageViewHolder(holderContactComplex, position);
                break;
            case ITEM_TYPE_CONSUMER_BLOCKED:
                ViewHolderContactConsumer holderItemBlocked = (ViewHolderContactConsumer) holder;
                manageViewHolder(holderItemBlocked, position);
                break;
            case ITEM_TYPE_HEADER:
                break;
            default:
                ViewHolderSeparator holderSeparator = (ViewHolderSeparator) holder;
                holderSeparator.mItem = (SeparatorItemConsumer) mValues.get(position);
                if (TextUtils.isEmpty(holderSeparator.mItem.getLetter())) {
                    holderSeparator.letter.setText("#");
                } else {
                    holderSeparator.letter.setText(holderSeparator.mItem.getLetter());
                }
                break;
        }
    }

    private void manageViewHolder(ViewHolderContactConsumer holderItem, int position) {

        holderItem.mItem = (ContactsItemConsumer) mValues.get(position);
        holderItem.name.setText(holderItem.mItem.getTitle());
        holderItem.phone.setText(holderItem.mItem.getDescription());

        Bitmap bitmap = holderItem.mItem.getBitmap();

        if (bitmap != null && holderItem.mItem.isImageAvailable()) {
            holderItem.profileImage.setVisibility(View.INVISIBLE);
            holderItem.profileImageCircle.setVisibility(View.VISIBLE);
            holderItem.profileImageCircle.setImageBitmap(bitmap);
            holderItem.noImageText.setVisibility(View.GONE);
        } else {
            holderItem.profileImage.setVisibility(View.VISIBLE);
            holderItem.profileImageCircle.setVisibility(View.INVISIBLE);
            if (holderItem.mItem.getItemInterface().getType() != ItemInterface.Type.NONE) {
                holderItem.profileImage.setImageResource(R.drawable.placeholder_circle_consumer);
                holderItem.noImageText.setVisibility(View.VISIBLE);
                holderItem.noImageText.setText(holderItem.mItem.getInitials());
            } else {
                holderItem.profileImage.setImageResource(R.drawable.placeholder_contact_list);
                holderItem.noImageText.setVisibility(View.GONE);
            }
        }

        if (holderItem.mItem.isBlocked()) {
            holderItem.blockedCheck.setImageResource(R.drawable.lock);
        } else {
            holderItem.blockedCheck.setImageResource(android.R.color.white);
        }

        holderItem.view.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onConsumerInteraction(holderItem.mItem);
            }
        }));

    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (mValues.get(position) instanceof ContactsItemConsumer) {
            ContactsItemConsumer contactItem = (ContactsItemConsumer) mValues.get(position);
            if (contactItem.isBlocked()) {
                viewType = ITEM_TYPE_CONSUMER_BLOCKED;
            } else if (contactItem.isMerchantAndConsumer()) {
                viewType = ITEM_TYPE_CONSUMER_COMPLEX;
            } else if (contactItem.isOnlyMerchant()) {
                viewType = ITEM_TYPE_EMPTY;
            } else {
                viewType = ITEM_TYPE_CONSUMER;
            }
        } else if (mValues.get(position) instanceof SeparatorItemConsumerBlocked) {
            viewType = ITEM_TYPE_HEADER;
        }
        return viewType;
    }

    public void updateModel(List<ItemInterfaceConsumer> items) {
        mValues = getList(items);
        mValuesBackup = mValues;
        getFilter().filter(lastFilter);
        //notifyDataSetChanged();
    }

    private List<ItemInterfaceConsumer> getList(List<ItemInterfaceConsumer> contactList) {
        List<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();

        //Inserisco nella lista aggiornata prima i contatti bloccati e poi quelli non bloccati
        List<ItemInterfaceConsumer> listBlocked = new ArrayList<>();
        List<ItemInterfaceConsumer> list = new ArrayList<>();
        for (ItemInterfaceConsumer item : contactList) {
            if (((ContactsItemConsumer) item).isBlocked()) {
                listBlocked.add(item);
            } else {
                list.add(item);
            }
        }
        contactList.clear();

        sortList(listBlocked);
        //sortList(list);

        contactList.addAll(listBlocked);
        contactList.addAll(list);

        if (!contactList.isEmpty()) {

            for (int i = 0; i < listBlocked.size(); i++) {
                if (i == 0) {
                    SeparatorItemConsumerBlocked item = new SeparatorItemConsumerBlocked();
                    itemsWithSeparator.add(item);
                }
                itemsWithSeparator.add(contactList.get(i));
            }

            if (!list.isEmpty()) {

                int startPoint = 0;
                if (!listBlocked.isEmpty()) {
                    startPoint = listBlocked.size();
                }

                //String letter = contactList.get(startPoint).getLetter();

                SeparatorItemConsumer item = new SeparatorItemConsumer();
                item.setLetter(context.getString(R.string.contacts_title));
                itemsWithSeparator.add(item);

                for (int i = startPoint; i < contactList.size(); i++) {
//                    if (!contactList.get(i).getLetter().equals(letter)) {
//                        letter = contactList.get(i).getLetter();
//
//                        /*SeparatorItemConsumer itemSeparator = new SeparatorItemConsumer();
//                        itemSeparator.setLetter(letter);
//                        itemsWithSeparator.add(itemSeparator);*/
//                        itemsWithSeparator.add(contactList.get(i));
//                    } else {
                    itemsWithSeparator.add(contactList.get(i));
//                    }
                }

            }

        }

        return itemsWithSeparator;
    }

    private void sortList(List<ItemInterfaceConsumer> list) {
        Collections.sort(list, (z1, z2) -> Integer.compare(z1.getTitle().compareToIgnoreCase(z2.getTitle()), 0));
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mValues = (List<ItemInterfaceConsumer>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                lastFilter = constraint;
                FilterResults results = new FilterResults();
                List<ItemInterfaceConsumer> filteredArrayDataBank = new ArrayList<>();

                for (int i = 0; i < mValuesBackup.size(); i++) {
                    if (mValuesBackup.get(i) instanceof ContactsItemConsumer) {
                        ContactsItemConsumer item = (ContactsItemConsumer) mValuesBackup.get(i);
                        if (item.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                || item.getPhoneNumber().contains(constraint.toString())) {
                            filteredArrayDataBank.add(item);
                        }
                    }
                }

                filteredArrayDataBank = getList(filteredArrayDataBank);

                CustomLogger.d("ITEM", "" + filteredArrayDataBank.size());

                results.count = filteredArrayDataBank.size();
                results.values = filteredArrayDataBank;

                return results;
            }
        };
    }

    @NonNull
    @Override
    public CharSequence getPopupText(@NonNull View view, int position) {
        CharSequence letter = mValues.get(position).getLetter();
        return letter == "Contatti" ? "A" : letter;
    }

    private static class ViewHolderContactConsumer extends RecyclerView.ViewHolder {

        ImageView blockedCheck;
        ImageView profileImage;
        ImageView profileImageCircle;
        ImageView activeBancomat;
        TextView name;
        TextView phone;
        TextView noImageText;
        View view;

        ContactsItemConsumer mItem;

        ViewHolderContactConsumer(View view) {
            super(view);
            blockedCheck = view.findViewById(R.id.image_contact_lock);
            profileImage = view.findViewById(R.id.contact_consumer_image_profile);
            profileImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            name = view.findViewById(R.id.contact_consumer_name);
            phone = view.findViewById(R.id.contact_consumer_number);
            activeBancomat = view.findViewById(R.id.contact_consumer_is_active);
            noImageText = view.findViewById(R.id.contact_consumer_letter);
            this.view = view;
        }
    }

    private static class ViewHolderSeparator extends RecyclerView.ViewHolder {

        TextView letter;

        SeparatorItemConsumer mItem;
        View view;

        ViewHolderSeparator(View view) {
            super(view);
            letter = view.findViewById(R.id.rubric_letter_separator);
            this.view = view;
        }
    }

    private static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolderBlockedHeader extends RecyclerView.ViewHolder {
        ViewHolderBlockedHeader(View view) {
            super(view);
        }
    }

}
