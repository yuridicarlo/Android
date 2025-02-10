package it.bancomatpay.sdkui.adapter;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.RowHomeFrequentContactBinding;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;

public class HomeFrequentContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = HomeFrequentContactsAdapter.class.getSimpleName();

	private ArrayList<FrequentItemConsumer> mValues;
	private final FrequentItemInteractionListener listener;

	public HomeFrequentContactsAdapter(ArrayList<FrequentItemConsumer> contacts, FrequentItemInteractionListener listener) {
		this.mValues = contacts;
		this.listener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

		RowHomeFrequentContactBinding itemBinding = RowHomeFrequentContactBinding.inflate(layoutInflater, parent, false);
		return new CircleViewHolder(itemBinding);

	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

			((CircleViewHolder) holder).bind(mValues.get(position), position, listener);

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

	private static class CircleViewHolder extends RecyclerView.ViewHolder {

		RowHomeFrequentContactBinding binding;

		public CircleViewHolder(@NonNull RowHomeFrequentContactBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bind(FrequentItemConsumer contactItem, int position, FrequentItemInteractionListener listener) {
			binding.setContact(contactItem);
			binding.setListener(listener);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				binding.layoutContainer.setTranslationZ(-position);
			}

			Bitmap bitmap = contactItem.getNullableBitmap();
			if (bitmap != null) {
				binding.imageContact.setImageBitmap(bitmap);
				binding.textInitials.setVisibility(View.INVISIBLE);
			} else {
				binding.imageContact.setImageResource(R.drawable.contact_list_item_circle_background);
				binding.textInitials.setVisibility(View.VISIBLE);
			}

			binding.label.setText(contactItem.getTitle());

			binding.executePendingBindings();
		}

	}

	public interface FrequentItemInteractionListener {
		void onTap(FrequentItemConsumer item);
	}

}
