package it.bancomatpay.sdkui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class PetrolAmountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final List<Integer> mValues;
	private final PetrolAmountInteractionListener listener;

	private int selectedAmountCents;

	public PetrolAmountsAdapter(List<Integer> values, PetrolAmountInteractionListener listener, int initialAmount) {
		this.mValues = values;
		this.listener = listener;
		this.selectedAmountCents = initialAmount;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.petrol_amount_list_item, parent, false);
		return new PetrolAmountViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

		PetrolAmountViewHolder holder = (PetrolAmountViewHolder) viewHolder;

		String formattedAmount = StringUtils.getFormattedValueInteger(BigDecimalUtils.getBigDecimalFromCents(mValues.get(position)));
		if (mValues.get(position) == 10000) {
			holder.textAmount.setText(String.format("Max %s", formattedAmount));
			holder.arrowUp.setVisibility(View.VISIBLE);
		} else {
			holder.textAmount.setText(formattedAmount);
			holder.arrowUp.setVisibility(View.INVISIBLE);
		}

		holder.textAmount.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				listener.onAmountSelected(mValues.get(position));
				this.selectedAmountCents = mValues.get(position);
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

	public static class PetrolAmountViewHolder extends RecyclerView.ViewHolder {

		TextView textAmount;
		ImageView arrowUp;

		PetrolAmountViewHolder(View view) {
			super(view);
			textAmount = view.findViewById(R.id.text_amount_item);
			arrowUp = view.findViewById(R.id.image_arrow_up);
		}
	}

	public interface PetrolAmountInteractionListener {
		void onAmountSelected(int amount);
	}

}
