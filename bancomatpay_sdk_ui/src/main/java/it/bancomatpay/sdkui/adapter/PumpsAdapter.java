package it.bancomatpay.sdkui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bancomatpay.sdk.manager.task.model.Till;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class PumpsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final List<Till> mValues;
	private final PumpInteractionListener listener;
	private final Context context;

	private Till lastSelectedPump;

	public PumpsAdapter(Context context, List<Till> values, PumpInteractionListener listener) {
		this.context = context;
		this.mValues = values;
		this.listener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.pump_list_item, parent, false);
		return new ViewHolderPump(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

		ViewHolderPump holderItem = (ViewHolderPump) holder;
		holderItem.mItem = mValues.get(position);

		holderItem.textPumpNumber.setText(holderItem.mItem.getName());

		if (holderItem.mItem.isEnabled()) {
			holderItem.imagePump.setImageResource(R.drawable.petrol_active);
			holderItem.textPumpNumber.setTextColor(ContextCompat.getColor(context, R.color.colorAccentBancomat));
		} else {
			holderItem.imagePump.setImageResource(R.drawable.petrol_disactive);
			holderItem.textPumpNumber.setTextColor(ContextCompat.getColor(context, R.color.fuel_pump_disabled));
		}

		if (holderItem.mItem.isSelected()) {
			holderItem.imageBorder.setVisibility(View.VISIBLE);
		} else {
			holderItem.imageBorder.setVisibility(View.INVISIBLE);
		}

		if (holderItem.mItem.isEnabled()) {
			holderItem.mView.setOnClickListener(new CustomOnClickListener(v -> {
				if (listener != null) {
					listener.onPumpSelected(holderItem.mItem);
				}
			}));
		} else {
			holderItem.mView.setOnClickListener(new CustomOnClickListener(null));
		}

	}

	@Override
	public int getItemCount() {
		if (mValues != null) {
			return mValues.size();
		}
		return 0;
	}

	public void setSelection(Till pumpData) {
		if (lastSelectedPump != null) {
			lastSelectedPump.setSelected(false);
		}
		notifyItemChanged(mValues.indexOf(lastSelectedPump));
		lastSelectedPump = pumpData;
		pumpData.setSelected(true);

		notifyItemChanged(mValues.indexOf(lastSelectedPump));
		notifyItemRangeChanged(0, mValues.size());
	}

	public Till getSelectedPump() {
		return lastSelectedPump;
	}

	public static class ViewHolderPump extends RecyclerView.ViewHolder {

		CardView mView;
		ImageView imagePump;
		TextView textPumpNumber;
		ImageView imageBorder;

		Till mItem;

		ViewHolderPump(View view) {
			super(view);
			mView = view.findViewById(R.id.card_view_pump_item);
			imagePump = view.findViewById(R.id.image_pump);
			textPumpNumber = view.findViewById(R.id.text_pump_number);
			imageBorder = view.findViewById(R.id.image_pump_selected_border);
		}
	}

	public interface PumpInteractionListener {
		void onPumpSelected(Till pumpData);
	}

}
