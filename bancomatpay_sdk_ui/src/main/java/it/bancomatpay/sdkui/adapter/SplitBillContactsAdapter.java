package it.bancomatpay.sdkui.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.RowSplitBillContactBinding;
import it.bancomatpay.sdkui.databinding.RowSplitBillContactMeBinding;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.SplitItemConsumer;

public class SplitBillContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = SplitBillContactsAdapter.class.getSimpleName();

	public static final int CONTACT_VIEW_TYPE = 1;
	public static final int ME_VIEW_TYPE = 2;

	private ArrayList<SplitItemConsumer> mValues;
	private final SplitMoneyContactClickListener listener;

	public SplitBillContactsAdapter(ArrayList<SplitItemConsumer> contacts, SplitMoneyContactClickListener listener) {
		this.mValues = contacts;
		this.listener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		if (viewType == CONTACT_VIEW_TYPE) {
			RowSplitBillContactBinding itemBinding = RowSplitBillContactBinding.inflate(layoutInflater, parent, false);
			return new CircleViewHolder(itemBinding);
		} else {
			RowSplitBillContactMeBinding itemBinding = RowSplitBillContactMeBinding.inflate(layoutInflater, parent, false);
			return new CircleMeViewHolder(itemBinding);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof CircleViewHolder) {
			((CircleViewHolder) holder).bind(mValues.get(position), position, listener);
		} else {
			((CircleMeViewHolder) holder).bind(mValues.get(position));
		}
	}

	@Override
	public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
		super.onViewRecycled(viewHolder);
		CustomLogger.d(TAG, "View recycled");
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
		if (position == 0) {
			return ME_VIEW_TYPE;
		} else {
			return CONTACT_VIEW_TYPE;
		}
	}

	public synchronized void addContact(@NotNull SplitItemConsumer contactItem) {
		if (!this.mValues.contains(contactItem)) {
			this.mValues.add(contactItem);
			notifyItemInserted(mValues.size() - 1);
		}
	}

	public void removeContact(@NotNull RecyclerView recyclerView, @NotNull SplitItemConsumer contactItem) {
		CustomLogger.d(TAG, "Trying to remove " + contactItem.toString());
		int positionRemoved = mValues.indexOf(contactItem);
		if (mValues.remove(contactItem)) {
			notifyItemRemoved(positionRemoved);
			recyclerView.postDelayed(this::notifyDataSetChanged, 200);
		}
		CustomLogger.d(TAG, "Actual list = " + mValues.toString());
	}


	public ArrayList<SplitItemConsumer> getItemsList() {
		return mValues;
	}

	private static class CircleViewHolder extends RecyclerView.ViewHolder {

		RowSplitBillContactBinding binding;

		public CircleViewHolder(@NonNull RowSplitBillContactBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bind(SplitItemConsumer contactItem, int position, SplitMoneyContactClickListener listener) {
			binding.setContact(contactItem);
			binding.setListener(listener);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				binding.layoutContainer.setTranslationZ(-position);
			}

			if (contactItem.isImageAvailable()) {
				binding.imageContact.setImageBitmap(contactItem.getBitmap());
				binding.textInitials.setVisibility(View.INVISIBLE);
			} else {
				binding.imageContact.setImageResource(R.drawable.contact_list_item_circle_background);
				binding.textInitials.setVisibility(View.VISIBLE);
			}

			binding.executePendingBindings();
		}

	}

	private static class CircleMeViewHolder extends RecyclerView.ViewHolder {

		RowSplitBillContactMeBinding binding;

		public CircleMeViewHolder(@NonNull RowSplitBillContactMeBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bind(SplitItemConsumer contactItem) {
			binding.splitBillMeLabel.setText(contactItem.getLetter());
			if (contactItem.isImageAvailable()) {
				binding.splitBillMeImage.setImageBitmap(contactItem.getBitmap());
				binding.splitBillMeLabel.setVisibility(View.INVISIBLE);
			} else {
				binding.splitBillMeImage.setImageResource(R.drawable.contact_list_item_circle_background);
				binding.splitBillMeLabel.setVisibility(View.VISIBLE);
			}
		}

	}

	public interface SplitMoneyContactClickListener {
		void onContactDelete(SplitItemConsumer contactItem);
	}

}
