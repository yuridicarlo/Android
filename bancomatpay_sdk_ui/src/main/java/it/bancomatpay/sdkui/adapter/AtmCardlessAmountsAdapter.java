package it.bancomatpay.sdkui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class AtmCardlessAmountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final List<Integer> mValues;
	private final AtmAmountInteractionListener listener;

	private int selectedAmountCents;

	public AtmCardlessAmountsAdapter(List<Integer> values, int initialCentsAmount, AtmAmountInteractionListener listener) {
		this.mValues = values;
		this.listener = listener;
		this.selectedAmountCents = initialCentsAmount;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.atm_amount_list_item, parent, false);
		return new AtmAmountViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

		AtmAmountViewHolder holder = (AtmAmountViewHolder) viewHolder;
		if (mValues.get(position) != 0) {
			holder.textAmount.setText(StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(mValues.get(position))));
		} else {
			holder.textAmount.setText(PayCore.getAppContext().getString(R.string.atm_cardless_other_amount));
		}

		holder.textAmount.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				if (mValues.get(position) != 0) {
					listener.onAmountSelected(mValues.get(position));
					this.selectedAmountCents = mValues.get(position);
				} else {
					listener.onOtherAmountSelected();
				}
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

	public int getSelectedItemAmount() {
		return this.selectedAmountCents;
	}

	public static class AtmAmountViewHolder extends RecyclerView.ViewHolder {

		TextView textAmount;

		AtmAmountViewHolder(View view) {
			super(view);
			textAmount = view.findViewById(R.id.text_amount_item);
		}
	}

	public interface AtmAmountInteractionListener {
		void onAmountSelected(int amount);

		void onOtherAmountSelected();
	}

}
