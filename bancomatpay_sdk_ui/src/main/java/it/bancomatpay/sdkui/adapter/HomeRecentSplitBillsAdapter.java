package it.bancomatpay.sdkui.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.databinding.RowHomeSplitBillHistoryItemBinding;

public class HomeRecentSplitBillsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = HomeRecentSplitBillsAdapter.class.getSimpleName();

	private List<SplitBillHistory> mValues;
	private final SplitBillHistoryItemInteractionListener listener;

	public HomeRecentSplitBillsAdapter(List<SplitBillHistory> contacts, SplitBillHistoryItemInteractionListener listener) {
		this.mValues = contacts;
		this.listener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

		RowHomeSplitBillHistoryItemBinding itemBinding = RowHomeSplitBillHistoryItemBinding.inflate(layoutInflater, parent, false);
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

		RowHomeSplitBillHistoryItemBinding binding;

		public CircleViewHolder(@NonNull RowHomeSplitBillHistoryItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bind(SplitBillHistory item, int position, SplitBillHistoryItemInteractionListener listener) {
			binding.setItem(item);
			binding.setListener(listener);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				binding.layoutContainer.setTranslationZ(-position);
			}

			binding.label.setText(item.getCausal());

			binding.executePendingBindings();
		}

	}

	public interface SplitBillHistoryItemInteractionListener {
		void onTap(SplitBillHistory item);
	}

}
