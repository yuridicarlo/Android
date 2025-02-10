package it.bancomatpay.sdkui.adapter;

import static it.bancomatpay.sdk.manager.utilities.Constants.MAX_FREQUENTS_NUMBER;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.SeparatorItemConsumer;
import it.bancomatpay.sdkui.model.SplitItemConsumer;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.fastscroll.PopupTextProvider;

public class CheckableContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, PopupTextProvider {

    private static final String TAG = CheckableContactsAdapter.class.getSimpleName();

    private static final int ITEM_TYPE_SEPARATOR = 0;
    private static final int ITEM_TYPE_CONSUMER = 1;
    private static final int ITEM_TYPE_MERCHANT = 2;
    private static final int ITEM_TYPE_BOTH = 3;
    private static final int ITEM_TYPE_CONSUMER_FREQUENT = 4;
    private static final int ITEM_TYPE_SPACE = 5;

    private final Context context;

    private List<ItemInterfaceConsumer> mValues;
    private List<ItemInterfaceConsumer> mValuesBackup;

    private List<FrequentItemConsumer> mFrequentsItemList;

    private List<SplitItemConsumer> mSelectedContactsList;

    private final InteractionListener mListener;

    private CharSequence lastFilter = "";

    public CheckableContactsAdapter(Context context, List<ItemInterfaceConsumer> items,
                                    List<FrequentItemConsumer> frequentsItemList, List<SplitItemConsumer> selectedContactsList, InteractionListener listener) {
        this.context = context;
        mValues = getList(items, frequentsItemList);
        mFrequentsItemList = frequentsItemList;
        mSelectedContactsList = selectedContactsList;
        mValuesBackup = new ArrayList<>();
        mValuesBackup.addAll(mValues);
        mListener = listener;
    }

    public void toggleCheckContact(@NotNull RecyclerView recyclerView, @NotNull SplitItemConsumer contactItem) {
        int position = mValues.indexOf(contactItem);
        notifyItemChanged(position);
        recyclerView.postDelayed(this::notifyDataSetChanged, 200);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE_CONSUMER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.split_bill_choose_contact_item_consumer, parent, false);
            return new ViewHolderContactConsumer(view);
        } else if (viewType == ITEM_TYPE_CONSUMER_FREQUENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.split_bill_choose_contact_item_consumer, parent, false);
            return new ViewHolderContactConsumerFrequent(view);
        } else if (viewType == ITEM_TYPE_SPACE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_space, parent, false);
            return new ViewHolderSpacer(view);
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.send_money_choose_receiver_separator_item, parent, false);

        return new ViewHolderSeparator(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == ITEM_TYPE_CONSUMER) {
            ViewHolderContactConsumer holderItem = (ViewHolderContactConsumer) holder;
            manageViewHolderContact(holderItem, position);
        } else if (holder.getItemViewType() == ITEM_TYPE_CONSUMER_FREQUENT) {
            ViewHolderContactConsumerFrequent holderItem = (ViewHolderContactConsumerFrequent) holder;
            manageViewHolderFrequent(holderItem, position);
        } else if (holder.getItemViewType() == ITEM_TYPE_SPACE) {
            // nothing special to be done
        } else {
            ViewHolderSeparator holderSeparator = (ViewHolderSeparator) holder;
            holderSeparator.mItem = (SeparatorItemConsumer) mValues.get(position);
            if (TextUtils.isEmpty(holderSeparator.mItem.getLetter())) {
                holderSeparator.letter.setText("#");
            } else {
                holderSeparator.letter.setText(holderSeparator.mItem.getLetter());
            }
        }

    }

    boolean isChecked(SplitItemConsumer contact){
        return mSelectedContactsList.contains(contact);
    }
    private void manageViewHolderContact(ViewHolderContactConsumer holderItem, int position) {

        holderItem.mItem = (SplitItemConsumer) mValues.get(position);

        if (holderItem.mItem.showBancomatLogo()) {
            if (!(holderItem.activeBancomat.getVisibility() == View.VISIBLE)) {
                new Handler().post(() ->
                        AnimationFadeUtil.startFadeInAnimationV1(holderItem.activeBancomat, 50));
            }
        } else {
            holderItem.activeBancomat.setVisibility(View.INVISIBLE);
        }

        if (isChecked(holderItem.mItem)) {
            holderItem.imageCheckbox.setImageResource(R.drawable.check_on);
        } else {
            holderItem.imageCheckbox.setImageResource(R.drawable.check_off);
        }

        holderItem.view.setOnClickListener(new CustomOnClickListener(v -> {
            updateChecksFrequent(holderItem.mItem.getItemInterface());
            mListener.onConsumerInteraction(holderItem.mItem);
        }));

        holderItem.name.setText(holderItem.mItem.getTitle());
        holderItem.phone.setText(holderItem.mItem.getPhoneNumber());

        Bitmap bitmap = holderItem.mItem.getBitmap();

        if (bitmap != null) {
            holderItem.profileImageCircle.setVisibility(View.VISIBLE);
            holderItem.profileImage.setVisibility(View.INVISIBLE);
            holderItem.profileImageCircle.setImageBitmap(bitmap);
            holderItem.noImageText.setVisibility(View.GONE);
        } else {
            holderItem.profileImageCircle.setVisibility(View.INVISIBLE);
            holderItem.profileImage.setVisibility(View.VISIBLE);
            holderItem.profileImage.setImageResource(R.drawable.placeholder_circle_consumer);
            holderItem.noImageText.setVisibility(View.VISIBLE);
            holderItem.noImageText.setText(holderItem.mItem.getInitials());
        }
    }

    private void manageViewHolderFrequent(ViewHolderContactConsumerFrequent holderItem, int position) {

        holderItem.mItem = (FrequentItemConsumer) mValues.get(position);

        if (holderItem.mItem.showBancomatLogo()) {
            holderItem.activeBancomat.setVisibility(View.VISIBLE);
        } else {
            holderItem.activeBancomat.setVisibility(View.INVISIBLE);
        }

        if (isChecked(SplitItemConsumer.fromFrequentItemConsumer(holderItem.mItem))) {
            holderItem.imageCheckbox.setImageResource(R.drawable.check_on);
        } else {
            holderItem.imageCheckbox.setImageResource(R.drawable.check_off);
        }

        holderItem.view.setOnClickListener(new CustomOnClickListener(v -> {
            updateChecksFrequent(holderItem.mItem.getItemInterface());
            mListener.onConsumerInteraction(holderItem.mItem);
        }));

        holderItem.name.setText(holderItem.mItem.getTitle());
        holderItem.phone.setText(holderItem.mItem.getPhoneNumber());

        Bitmap bitmap = holderItem.mItem.getBitmap();

        if (bitmap != null) {
            holderItem.profileImageCircle.setVisibility(View.VISIBLE);
            holderItem.profileImage.setVisibility(View.INVISIBLE);
            holderItem.profileImageCircle.setImageBitmap(bitmap);
            holderItem.noImageText.setVisibility(View.GONE);
        } else {
            holderItem.profileImageCircle.setVisibility(View.INVISIBLE);
            holderItem.profileImage.setVisibility(View.VISIBLE);
            holderItem.profileImage.setImageResource(R.drawable.placeholder_circle_consumer);
            holderItem.noImageText.setVisibility(View.VISIBLE);
            holderItem.noImageText.setText(holderItem.mItem.getInitials());
        }
    }

    private void updateChecksFrequent(ItemInterface itemInterface) {
        for(int i = 0; i < mValues.size(); i++){
            if (getItemViewType(i) != ITEM_TYPE_SEPARATOR && getItemViewType(i) != ITEM_TYPE_SPACE){
                ItemInterfaceConsumer item = mValues.get(i);
                if(item.getPhoneNumber().equals(itemInterface.getPhoneNumber()))
                    notifyItemChanged(i);
            }
        }
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
        int viewType = ITEM_TYPE_SEPARATOR;
        if (mValues.get(position) instanceof SplitItemConsumer) {
            SplitItemConsumer contactItem = (SplitItemConsumer) mValues.get(position);
            if (contactItem.isMerchantAndConsumer()) {
                viewType = ITEM_TYPE_BOTH;
            } else if (contactItem.isOnlyMerchant()) {
                viewType = ITEM_TYPE_MERCHANT;
            } else {
                viewType = ITEM_TYPE_CONSUMER;
            }
        } else if (mValues.get(position) instanceof FrequentItemConsumer) {
            viewType = ITEM_TYPE_CONSUMER_FREQUENT;
        }

        if(position == (getItemCount() - 1)) {
            viewType = ITEM_TYPE_SPACE;
        }
        return viewType;
    }

    private List<ItemInterfaceConsumer> getListFrequents(List<FrequentItemConsumer> frequentList, boolean canGetFrequents) {

        List<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();
        if (canGetFrequents
                && frequentList != null && !frequentList.isEmpty()) {

            boolean isHeaderFrequentsAdded = false;
            Collections.sort(frequentList, new UserComparatorByOperationCount());

            for (ItemInterfaceConsumer item : frequentList) {
                if (item instanceof FrequentItemConsumer && ((FrequentItemConsumer) item).getOperationCounter() > 0) {

                    if (!isHeaderFrequentsAdded) {
                        SeparatorItemConsumer itemSeparator = new SeparatorItemConsumer();
                        itemSeparator.setLetter(context.getString(R.string.contacts_list_frequents_header));
                        itemsWithSeparator.add(itemSeparator);
                        isHeaderFrequentsAdded = true;
                    }

                    if (itemsWithSeparator.size() <= MAX_FREQUENTS_NUMBER) {
                        itemsWithSeparator.add(item);
                    }
                }
            }
            //Collections.sort(itemsWithSeparator, new UserComparatorByOperationCount());
        }

        return itemsWithSeparator;
    }

    private List<ItemInterfaceConsumer> getListFavorites(List<ItemInterfaceConsumer> contactList) {

        List<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();
        if (contactList != null && !contactList.isEmpty()) {

            boolean isHeaderFavoritesAdded = false;

            for (ItemInterfaceConsumer item : contactList) {
                if (item instanceof SplitItemConsumer && ((SplitItemConsumer) item).isFavorite()) {

                    if (!isHeaderFavoritesAdded) {
                        SeparatorItemConsumer itemSeparator = new SeparatorItemConsumer();
                        itemSeparator.setLetter(context.getString(R.string.contacts_list_favorites_header));
                        itemsWithSeparator.add(itemSeparator);
                        isHeaderFavoritesAdded = true;
                    }

                    itemsWithSeparator.add(item);
                }
            }
            Collections.sort(itemsWithSeparator, new UserComparatorByPinningTime());
        }

        return itemsWithSeparator;
    }

    private List<ItemInterfaceConsumer> getListContacts(List<ItemInterfaceConsumer> contactList, int indexToStart) {

        List<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();
        if (contactList != null && !contactList.isEmpty()) {

            CustomLogger.d(TAG, "contact size array: " + contactList.size());
            CustomLogger.d(TAG, "indexToStart: " + indexToStart);

            if (indexToStart <= contactList.size() - 1) {

                if (contactList.get(indexToStart) instanceof SeparatorItemConsumer
                        && contactList.get(indexToStart).getLetter().equals(context.getString(R.string.contacts_list_favorites_header))) {
                    contactList.remove(indexToStart);
                }

                //Collections.sort(contactList, new UserComparatorByTitle());

                /*indexToStart = 0;

                String letter = contactList.get(indexToStart).getLetter();

                SeparatorItemConsumer item = new SeparatorItemConsumer();
                item.setLetter(letter);
                itemsWithSeparator.add(item);*/

                SeparatorItemConsumer item = new SeparatorItemConsumer();
                item.setLetter(context.getString(R.string.contacts_title));
                itemsWithSeparator.add(item);

                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i) instanceof SplitItemConsumer && !((SplitItemConsumer) contactList.get(i)).isFavorite()) {
                        /*if (!contactList.get(i).getLetter().equals(letter)) {
                            letter = contactList.get(i).getLetter();

                            SeparatorItemConsumer itemSeparator = new SeparatorItemConsumer();
                            itemSeparator.setLetter(letter);
                            itemsWithSeparator.add(itemSeparator);
                            itemsWithSeparator.add(contactList.get(i));
                        } else {*/
                        itemsWithSeparator.add(contactList.get(i));
                        /*}*/
                    }
                }

                for (int i = 0; i < itemsWithSeparator.size(); i++) {
                    if (itemsWithSeparator.get(i) instanceof SeparatorItemConsumer && itemsWithSeparator.get(i).getLetter().equals("#")
                        && itemsWithSeparator.size() > i && itemsWithSeparator.get(i+1) instanceof SeparatorItemConsumer) {
                        itemsWithSeparator.remove(i);
                        break;
                    }
                }

            }

        }
        return itemsWithSeparator;
    }

    private List<ItemInterfaceConsumer> getList(List<ItemInterfaceConsumer> contactList, List<FrequentItemConsumer> frequentsItemList) {
        List<ItemInterfaceConsumer> itemsMerged = new ArrayList<>();

        Collections.sort(contactList, new UserComparatorByTitle());

        List<ItemInterfaceConsumer> favouriteList = getListFavorites(contactList);
        itemsMerged.addAll(favouriteList);
        List<ItemInterfaceConsumer> otherList = getListContacts(contactList, itemsMerged.size());
        itemsMerged.addAll(otherList);

        boolean canGetFrequents = !itemsMerged.isEmpty();

        List<ItemInterfaceConsumer> frequentList = getListFrequents(frequentsItemList, canGetFrequents);
        itemsMerged.addAll(0, frequentList);

        itemsMerged.add(new SeparatorItemConsumer()); // trailing invisible margin item
        return itemsMerged;
    }

    public void updateModel(List<ItemInterfaceConsumer> items, List<FrequentItemConsumer> frequentsItemList) {
        mValues = getList(items, frequentsItemList);
        mFrequentsItemList = frequentsItemList;
        mValuesBackup = new ArrayList<>();
        mValuesBackup.addAll(mValues);
        getFilter().filter(lastFilter);
        //notifyDataSetChanged();
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
                List<ItemInterfaceConsumer> filteredContacts = new ArrayList<>();
                List<FrequentItemConsumer> filteredFrequentContacts = new ArrayList<>();

                for (int i = 0; i < mValuesBackup.size(); i++) {
                    if (!(mValuesBackup.get(i) instanceof SeparatorItemConsumer)) {
                        if (mValuesBackup.get(i).performFilter(constraint.toString())) {
                            filteredContacts.add(mValuesBackup.get(i));
                        }
                    }
                }

                for (int i = 0; i < mFrequentsItemList.size(); i++) {
                    if (mFrequentsItemList.get(i).performFilter(constraint.toString())) {
                        filteredFrequentContacts.add(mFrequentsItemList.get(i));
                    }
                }

                filteredContacts = getList(filteredContacts, filteredFrequentContacts);

                CustomLogger.d("ITEM", "" + filteredContacts.size());

                results.count = filteredContacts.size();
                results.values = filteredContacts;

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

        ImageView profileImage;
        ImageView profileImageCircle;
        ImageView activeBancomat;
        ImageView imageCheckbox;
        TextView name;
        TextView phone;
        TextView noImageText;

        SplitItemConsumer mItem;
        View view;

        ViewHolderContactConsumer(View view) {
            super(view);
            profileImage = view.findViewById(R.id.contact_consumer_image_profile);
            profileImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            name = view.findViewById(R.id.contact_consumer_name);
            phone = view.findViewById(R.id.contact_consumer_number);
            activeBancomat = view.findViewById(R.id.contact_consumer_is_active);
            noImageText = view.findViewById(R.id.contact_consumer_letter);
            imageCheckbox = view.findViewById(R.id.image_checkbox);
            this.view = view;
        }
    }

    private static class ViewHolderContactConsumerFrequent extends RecyclerView.ViewHolder {

        ImageView profileImage;
        ImageView profileImageCircle;
        ImageView activeBancomat;
        ImageView imageCheckbox;
        TextView name;
        TextView phone;
        TextView noImageText;

        FrequentItemConsumer mItem;
        View view;

        ViewHolderContactConsumerFrequent(View view) {
            super(view);
            profileImage = view.findViewById(R.id.contact_consumer_image_profile);
            profileImageCircle = view.findViewById(R.id.contact_consumer_image_profile_circle);
            name = view.findViewById(R.id.contact_consumer_name);
            phone = view.findViewById(R.id.contact_consumer_number);
            activeBancomat = view.findViewById(R.id.contact_consumer_is_active);
            noImageText = view.findViewById(R.id.contact_consumer_letter);
            imageCheckbox = view.findViewById(R.id.image_checkbox);
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

    private static class ViewHolderSpacer extends RecyclerView.ViewHolder {
        View view;

        ViewHolderSpacer(View view) {
            super(view);
            this.view = view;
        }
    }

    static class UserComparatorByOperationCount implements Comparator<ItemInterfaceConsumer> {
        @Override
        public int compare(ItemInterfaceConsumer i1, ItemInterfaceConsumer i2) {
            if (!(i1 instanceof SeparatorItemConsumer) && !(i2 instanceof SeparatorItemConsumer)) {
                return Integer.compare(((FrequentItemConsumer) i2).getOperationCounter(), ((FrequentItemConsumer) i1).getOperationCounter());
            } else {
                return Integer.compare(i1.getTitle().compareToIgnoreCase(i2.getTitle()), 0);
            }
        }
    }

    static class UserComparatorByPinningTime implements Comparator<ItemInterfaceConsumer> {
        @Override
        public int compare(ItemInterfaceConsumer i1, ItemInterfaceConsumer i2) {
            if (!(i1 instanceof SeparatorItemConsumer) && !(i2 instanceof SeparatorItemConsumer)) {
                return (int) (
                        ((SplitItemConsumer) i2).getDbModel().getPinningTime() - ((SplitItemConsumer) i1).getDbModel().getPinningTime());
            } else {
                return 1;
            }
        }
    }

    static class UserComparatorByTitle implements Comparator<ItemInterfaceConsumer> {
        @Override
        public int compare(ItemInterfaceConsumer i1, ItemInterfaceConsumer i2) {
            return Integer.compare(i1.getTitle().compareToIgnoreCase(i2.getTitle()), 0);
        }
    }

}
