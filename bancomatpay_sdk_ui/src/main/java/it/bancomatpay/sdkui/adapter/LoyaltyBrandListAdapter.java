package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;

import static it.bancomatpay.sdk.manager.task.model.LoyaltyBrand.BRAND_UUID_ADD_CARD;

public class LoyaltyBrandListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

	private static final int ITEM_TYPE_OTHER_CARD = 1;
	private static final int ITEM_TYPE_BRANDED_CARD = 2;

	private final Context context;
	private List<LoyaltyBrand> mValues;
	private final List<LoyaltyBrand> mValuesBackup;
	private final LoyaltyBrandListAdapter.InteractionListener mListener;

	public LoyaltyBrandListAdapter(Context context, List<LoyaltyBrand> items, LoyaltyBrandListAdapter.InteractionListener listener) {
		this.context = context;
		mValues = getBrandList(items);
		mValuesBackup = new ArrayList<>();
		mValuesBackup.addAll(mValues);
		mListener = listener;
	}

	private List<LoyaltyBrand> getBrandList(List<LoyaltyBrand> registeredBrands) {
		List<LoyaltyBrand> brandList = new ArrayList<>();
		if (registeredBrands != null && !registeredBrands.isEmpty()) {
			LoyaltyBrand brandAddCard = new LoyaltyBrand();
			brandAddCard.setBrandName(context.getString(R.string.add_new_card));
			brandAddCard.setBrandUuid(BRAND_UUID_ADD_CARD);
			brandList.add(brandAddCard);

			Collections.sort(registeredBrands, new BrandComparatorByName());

			brandList.addAll(registeredBrands);
		}
		return brandList;
	}

	public void updateList(List<LoyaltyBrand> newList) {
		mValues = getBrandList(newList);
		/*DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LoyaltyBrandsDiffUtilCallback(mValues, mValuesBackup));
		diffResult.dispatchUpdatesTo(this);*/
		mValuesBackup.clear();
		mValuesBackup.addAll(mValues);
		notifyDataSetChanged();
	}

	public void addOtherCardItem() {

		LoyaltyBrand brandAddCard = new LoyaltyBrand();
		brandAddCard.setBrandName(context.getString(R.string.add_new_card));
		brandAddCard.setBrandUuid(BRAND_UUID_ADD_CARD);
		mValues.add(brandAddCard);

		/*DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LoyaltyBrandsDiffUtilCallback(mValues, mValuesBackup));
		diffResult.dispatchUpdatesTo(this);*/
		mValuesBackup.clear();
		mValuesBackup.addAll(mValues);
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view;
		if (viewType == ITEM_TYPE_OTHER_CARD) {
			view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.other_card_item, parent, false);
			return new ViewHolderOtherCard(view);
		} else {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.branded_card_item, parent, false);
			return new ViewHolderBrandedCard(view);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder.getItemViewType() == ITEM_TYPE_OTHER_CARD) {

			ViewHolderOtherCard holderItem = (ViewHolderOtherCard) holder;
			Context context = PayCore.getAppContext();
			holderItem.imageLogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.add_card_big));
			holderItem.cardName.setText(R.string.add_new_card);

			holderItem.cardView.setOnClickListener(new CustomOnClickListener(v -> {
				if (mListener != null) {
					mListener.onAddCardClicked();
				}
			}));

		} else if (holder.getItemViewType() == ITEM_TYPE_BRANDED_CARD) {

			ViewHolderBrandedCard holderItem = (ViewHolderBrandedCard) holder;
			holderItem.mItem = mValues.get(position);
			holderItem.cardName.setText(holderItem.mItem.getBrandName());
			holderItem.imageBackground.setCircleBackgroundColor(holderItem.mItem.getCardColor());

			holderItem.imageLogo.setVisibility(View.GONE);
			holderItem.brandedCardPlaceholder.setVisibility(View.VISIBLE);
			holderItem.brandedCardPlaceholder.setText(holderItem.cardName.getText().toString());
			Picasso.get().load(holderItem.mItem.getCardLogoUrl())
					.noPlaceholder()
					.into(holderItem.imageLogo, new Callback() {
						@Override
						public void onSuccess() {
							AnimationFadeUtil.startFadeOutAnimationV1(holderItem.brandedCardPlaceholder,AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
							AnimationFadeUtil.startFadeInAnimationV1(holderItem.imageLogo, AnimationFadeUtil.DEFAULT_DURATION);
						}

						@Override
						public void onError(Exception e) {
							AnimationFadeUtil.startFadeOutAnimationV1(holderItem.imageLogo,AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
							AnimationFadeUtil.startFadeInAnimationV1(holderItem.brandedCardPlaceholder, AnimationFadeUtil.DEFAULT_DURATION);
							holderItem.brandedCardPlaceholder.setText(holderItem.cardName.getText().toString());
						}
					});

			holderItem.cardView.setOnClickListener(new CustomOnClickListener(v -> {
				if (mListener != null) {
					mListener.onListViewInteraction(holderItem.mItem);
				}
			}));

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
		int viewType = 0;
		if (mValues.get(position).getBrandUuid() != null) {
			if (mValues.get(position).getBrandUuid().equals(BRAND_UUID_ADD_CARD)) {
				viewType = ITEM_TYPE_OTHER_CARD;
			} else {
				viewType = ITEM_TYPE_BRANDED_CARD;
			}
		}
		return viewType;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mValues = (List<LoyaltyBrand>) results.values;
				notifyDataSetChanged();
				//DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LoyaltyBrandsDiffUtilCallback(mValues, mValuesBackup));
				//diffResult.dispatchUpdatesTo(LoyaltyBrandListAdapter.this);
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				List<LoyaltyBrand> filteredBrands = new ArrayList<>();

				for (int i = 0; i < mValuesBackup.size(); i++) {
					if (StringUtils.contains(mValuesBackup.get(i).getBrandName(), constraint.toString())) {
						filteredBrands.add(mValuesBackup.get(i));
					}
				}

				CustomLogger.d("ITEM", "" + filteredBrands.size());

				results.count = filteredBrands.size();
				results.values = filteredBrands;

				return results;
			}
		};
	}

	public static class ViewHolderOtherCard extends RecyclerView.ViewHolder {

		CardView cardView;
		ImageView imageLogo;
		TextView cardName;

		ViewHolderOtherCard(View view) {
			super(view);
			cardView = view.findViewById(R.id.card_view_fidelity_card);
			imageLogo = view.findViewById(R.id.image_fidelity_card_logo);
			cardName = view.findViewById(R.id.card_name_label);
		}
	}

	public static class ViewHolderBrandedCard extends RecyclerView.ViewHolder {

		CardView cardView;
		CircleImageView imageBackground;
		ImageView imageLogo;
		TextView cardName;
		TextView brandedCardPlaceholder;

		LoyaltyBrand mItem;

		ViewHolderBrandedCard(View view) {
			super(view);
			cardView = view.findViewById(R.id.card_view_fidelity_card);
			imageBackground = view.findViewById(R.id.image_fidelity_card_background);
			imageLogo = view.findViewById(R.id.image_fidelity_card_logo);
			cardName = view.findViewById(R.id.card_name_label);
			brandedCardPlaceholder = view.findViewById(R.id.branded_card_placeholder);
		}
	}

	static class BrandComparatorByName implements Comparator<LoyaltyBrand> {
		@Override
		public int compare(LoyaltyBrand i1, LoyaltyBrand i2) {
			return Integer.compare(i1.getBrandName().compareToIgnoreCase(i2.getBrandName()), 0);
		}
	}


	public interface InteractionListener {
		void onListViewInteraction(LoyaltyBrand loyaltyBrand);

		void onAddCardClicked();
	}

}
