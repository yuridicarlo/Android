package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.DateAccess;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.utilities.AccessSeparator;

public class BcmPayAccessesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context context;
	private List<DateDisplayData> mValues;

	public BcmPayAccessesAdapter(Context context, List<DateDisplayData> items) {
		this.context = context;
		mValues = getList(items);
	}

	private List<DateDisplayData> getList(List<DateDisplayData> items) {
		List<DateDisplayData> itemsWithSeparator = new ArrayList<>();

		if (!items.isEmpty()) {
			String date = items.get(0).getDateName();
			String shortDate = items.get(0).getShortDateName();
			AccessSeparator item = new AccessSeparator();
			item.setDateName(date);
			item.setShortDateName(shortDate);
			itemsWithSeparator.add(item);

			for (int i = 0; i < items.size(); i++) {
				if (!(items.get(i).getDateName()).equals(date)) {
					date = items.get(i).getDateName();
					shortDate = items.get(i).getShortDateName();
					AccessSeparator itemSeparator = new AccessSeparator();
					itemSeparator.setDateName(date);
					itemSeparator.setShortDateName(shortDate);
					itemsWithSeparator.add(itemSeparator);
					itemsWithSeparator.add(items.get(i));
				} else {
					itemsWithSeparator.add(items.get(i));
				}
			}
		}

		return itemsWithSeparator;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view;
		if (viewType == 0) {
			view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.access_item, parent, false);
			return new ViewHolderAccess(view);
		}
		view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.access_separator_item, parent, false);
		return new ViewHolderSeparator(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
		if (holder.getItemViewType() == 0) {

			ViewHolderAccess holderAccess = (ViewHolderAccess) holder;
			holderAccess.mItem = (DateAccess) mValues.get(position);

			holderAccess.textProviderName.setText(holderAccess.mItem.getBankIdRequest().getBankIdMerchantData().getMerchantName());
			holderAccess.textAccessTime.setText(context.getString(R.string.bank_id_access_time_label, holderAccess.mItem.getShortDateName()));

			holderAccess.textLetter.setText(holderAccess.mItem.getLetter());

			if (position + 1 < getItemCount() && mValues.get(position + 1) instanceof DateAccess){
				holderAccess.accessSeparator.setVisibility(View.VISIBLE);
			}else {holderAccess.accessSeparator.setVisibility(View.GONE);}
		} else {
			ViewHolderSeparator holderSeparator = (ViewHolderSeparator) holder;
			holderSeparator.mItem = (AccessSeparator) mValues.get(position);
			holderSeparator.textSeparator.setText(holderSeparator.mItem.getDateName());
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
		if (mValues.get(position) instanceof AccessSeparator) {
			viewType = 1;
		}
		return viewType;
	}

	public void updateList(ArrayList<DateDisplayData> accessList) {
		mValues.clear();
		mValues.addAll(getList(accessList));
		notifyDataSetChanged();
	}

	private static class ViewHolderSeparator extends RecyclerView.ViewHolder {

		TextView textSeparator;
		View view;

		AccessSeparator mItem;

		ViewHolderSeparator(View view) {
			super(view);
			textSeparator = view.findViewById(R.id.text_title);
			this.view = view;
		}
	}

	public static class ViewHolderAccess extends RecyclerView.ViewHolder {

		CircleImageView accessProviderLogoCircle;
		ImageView accessProviderLogo;
		ImageView accessSeparator;
		TextView textLetter;
		TextView textProviderName;
		TextView textAccessTime;
		View mView;

		DateAccess mItem;

		ViewHolderAccess(View view) {
			super(view);
			mView = view;
			accessProviderLogoCircle = view.findViewById(R.id.image_access_provider_logo_circle);
			accessProviderLogo = view.findViewById(R.id.image_access_provider_logo);
			textLetter = view.findViewById(R.id.text_letter);
			textProviderName = view.findViewById(R.id.text_provider_name);
			textAccessTime = view.findViewById(R.id.text_access_time);
			accessSeparator = view.findViewById(R.id.access_separator);
		}
	}

}
