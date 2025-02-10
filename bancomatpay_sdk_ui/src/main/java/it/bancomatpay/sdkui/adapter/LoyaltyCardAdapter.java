package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdk.manager.utilities.Constants.MAX_FREQUENTS_CARD;

public class LoyaltyCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_CARD = 1;
    private static final int ITEM_TYPE_SEPARATOR = 2;

    private static final String SPACE_1 = "space_1";
    private static final String SPACE_2 = "space_2";

    private final Context context;
    private List<LoyaltyCard> mValues;
    private final LoyaltyCardAdapter.InteractionListener mListener;

    public LoyaltyCardAdapter(Context context, List<LoyaltyCard> items, LoyaltyCardAdapter.InteractionListener listener) {
        this.context = context;
        mValues = getLoyaltyCardsWithSeparator(items);
        mListener = listener;
    }

    private List<LoyaltyCard> getLoyaltyCardsWithSeparator(List<LoyaltyCard> items) {
        List<LoyaltyCard> completeList;

        List<LoyaltyCard> frequentList = new ArrayList<>();
        List<LoyaltyCard> cardList = new ArrayList<>();

        completeList = prepareFrequentList(items);
        for (LoyaltyCard element : completeList) {
            if (frequentList.size() <= MAX_FREQUENTS_CARD && element.getOperationCounter() > 0) {
                frequentList.add(element);
            } else {
                cardList.add(element);
            }
        }
        Collections.sort(cardList, new CardComparatorByTitle());
        completeList.clear();

        LoyaltyCard itemEmpty = new LoyaltyCard();
        itemEmpty.setLoyaltyCardId(SPACE_1);

        if (!frequentList.isEmpty()) {
            LoyaltyCard cardFrequentsHeader = new LoyaltyCard();
            cardFrequentsHeader.setLoyaltyCardId(context.getString(R.string.card_frequents_header));
            completeList.add(cardFrequentsHeader);
            completeList.add(itemEmpty);
        }

        completeList.addAll(frequentList);

        if (!frequentList.isEmpty() && frequentList.size() % 2 != 0) {
            itemEmpty.setLoyaltyCardId(SPACE_2);
            completeList.add(itemEmpty);
        }

        if (!cardList.isEmpty()) {
            LoyaltyCard cardAllCardsHeader = new LoyaltyCard();
            cardAllCardsHeader.setLoyaltyCardId(context.getString(R.string.all_cards_label));
            completeList.add(cardAllCardsHeader);
            completeList.add(itemEmpty);
        }

        completeList.addAll(cardList);

        return completeList;
    }

    private List<LoyaltyCard> prepareFrequentList(List<LoyaltyCard> items) {
        List<LoyaltyCard> frequentList = new ArrayList<>();
        List<LoyaltyCard> cardList = new ArrayList<>();
        List<LoyaltyCard> completeList = new ArrayList<>();
        for (LoyaltyCard item : items) {
            if (item.getOperationCounter() > 0) {
                frequentList.add(item);
            } else {
                cardList.add(item);
            }
        }
        Collections.sort(frequentList, new CardComparatorByOperationCount());
        completeList.addAll(frequentList);
        completeList.addAll(cardList);
        return completeList;
    }

    /*public void updateList(List<LoyaltyCard> newList) {
        mValues.clear();
        mValues = getLoyaltyCardsWithSeparator(newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LoyaltyCardsDiffUtilCallback(mValues, mValuesBackup));
        diffResult.dispatchUpdatesTo(this);
        mValuesBackup.clear();
        mValuesBackup.addAll(mValues);
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CARD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loyalty_card_item, parent, false);
            return new LoyaltyCardViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loyalty_card_list_separator_item, parent, false);
            return new SeparatorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof LoyaltyCardViewHolder) {

            manageLoyaltyCardViewHolder(holder, position);

        } else if (holder instanceof SeparatorViewHolder) {

            SeparatorViewHolder holderSeparator = (SeparatorViewHolder) holder;
            holderSeparator.mItem = mValues.get(position);

            String titleSeparator = "";
            if (!TextUtils.isEmpty(holderSeparator.mItem.getLoyaltyCardId())) {
                if (holderSeparator.mItem.getLoyaltyCardId().equals(context.getString(R.string.contacts_list_frequents_header))) {
                    titleSeparator = context.getString(R.string.contacts_list_frequents_header);
                } else if (holderSeparator.mItem.getLoyaltyCardId().equals(context.getString(R.string.all_cards_label))) {
                    titleSeparator = context.getString(R.string.all_cards_label);
                }
            }

            holderSeparator.title.setText(titleSeparator);

        }

    }

    /*@Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_SEPARATOR && payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            manageLoyaltyCardViewHolder(holder, position);
        }
    }*/

    private void manageLoyaltyCardViewHolder(RecyclerView.ViewHolder holder, int position) {
        LoyaltyCardViewHolder holderCard = (LoyaltyCardViewHolder) holder;
        holderCard.mItem = mValues.get(position);

        holderCard.cardView.setCardBackgroundColor(holderCard.mItem.getBrand().getCardColor());

        if (holderCard.mItem.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND) {
            holderCard.layoutUnknownBrand.setVisibility(View.INVISIBLE);
            holderCard.textLogoKnownCard.setVisibility(View.VISIBLE);
            holderCard.textLogoKnownCard.setText(holderCard.mItem.getBrand().getBrandName());
            Picasso.get().load(Uri.decode(holderCard.mItem.getBrand().getCardLogoUrl()))
                    .placeholder(R.drawable.empty)
                    .into(holderCard.imageLogo, new Callback() {
                        @Override
                        public void onSuccess() {
                            holderCard.textLogoKnownCard.setVisibility(View.GONE);
                            holderCard.imageLogo.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holderCard.imageLogo.setVisibility(View.GONE);
                            holderCard.textLogoKnownCard.setVisibility(View.VISIBLE);
                            holderCard.textLogoKnownCard.setText(holderCard.mItem.getBrand().getBrandName());
                        }
                    });
        } else if (holderCard.mItem.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {
            holderCard.layoutUnknownBrand.setVisibility(View.VISIBLE);
            holderCard.imageLogo.setVisibility(View.INVISIBLE);
            Result<Bitmap> resultBitmap = Conversion.doGetBitmapFromBase64(holderCard.mItem.getBrand().getCardImage());
            if (resultBitmap.getResult() != null) {
                holderCard.imageUnknownBrand.setImageBitmap(resultBitmap.getResult());
            } else {
                holderCard.imageUnknownBrand.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_merchant_white));
            }
            holderCard.textUnknownBrandName.setText(holderCard.mItem.getBrand().getBrandName());
            holderCard.textLogoKnownCard.setVisibility(View.GONE);
        }

        holderCard.cardView.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.onListViewInteraction(holderCard.mItem);
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
        LoyaltyCard card = mValues.get(position);
        if (TextUtils.isEmpty(card.getLoyaltyCardId())
                || card.getLoyaltyCardId().equals(context.getString(R.string.contacts_list_frequents_header))
                || card.getLoyaltyCardId().equals(context.getString(R.string.all_cards_label))
                || card.getLoyaltyCardId().equals(SPACE_1)
                || card.getLoyaltyCardId().equals(SPACE_2)) {
            return ITEM_TYPE_SEPARATOR;
        } else {
            return ITEM_TYPE_CARD;
        }
    }

    public void updateList(List<LoyaltyCard> items) {
        mValues = getLoyaltyCardsWithSeparator(items);
        notifyDataSetChanged();
    }

    public static class SeparatorViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        LoyaltyCard mItem;

        SeparatorViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.letter_separator);
        }
    }

    public static class LoyaltyCardViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageBackground;
        ImageView imageLogo;

        View layoutUnknownBrand;
        CircleImageView imageUnknownBrand;
        TextView textUnknownBrandName;
        TextView textLogoKnownCard;


        LoyaltyCard mItem;

        LoyaltyCardViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view_fidelity_card);
            imageBackground = view.findViewById(R.id.image_fidelity_card_background_color);
            imageLogo = view.findViewById(R.id.image_fidelity_card_logo);
            layoutUnknownBrand = view.findViewById(R.id.layout_unknown_brand);
            imageUnknownBrand = view.findViewById(R.id.image_card_unknown_brand);
            textUnknownBrandName = view.findViewById(R.id.text_unknown_brand_name);
            textLogoKnownCard = view.findViewById(R.id.text_logo_known_card);
        }
    }

    static class CardComparatorByOperationCount implements Comparator<LoyaltyCard> {
        @Override
        public int compare(LoyaltyCard i1, LoyaltyCard i2) {
            return Integer.compare(i2.getOperationCounter(), i1.getOperationCounter());
        }
    }

    static class CardComparatorByTitle implements Comparator<LoyaltyCard> {
        @Override
        public int compare(LoyaltyCard i1, LoyaltyCard i2) {
            return Integer.compare(i1.getBrand().getBrandName().compareToIgnoreCase(i2.getBrand().getBrandName()), 0);
        }}

    public interface InteractionListener {
        void onListViewInteraction(LoyaltyCard loyaltyCard);
    }

}
