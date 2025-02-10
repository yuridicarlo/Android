package it.bancomat.pay.consumer.activation.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.List;

import it.bancomat.pay.consumer.network.dto.PendingPayment;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class PendingPaymentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final List<PendingPayment> mValues;
	private final PendingPaymentInteraction listener;

	public PendingPaymentsAdapter(List<PendingPayment> payments, PendingPaymentInteraction listener) {
		this.mValues = payments;
		this.listener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.item_pending_payments, viewGroup, false);
		return new PendingPaymentViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

		PendingPaymentViewHolder viewHolder = (PendingPaymentViewHolder) holder;

		ContactItem contactItem = ApplicationModel.getInstance().getContactItem(mValues.get(i).getMsisdnSender());
		if (contactItem != null) {

			viewHolder.textName.setText(contactItem.getName());

			//al momento non è gestita l'immagine dell'utente ricevuta dal server

			viewHolder.textLetter.setText(getInitials(contactItem.getName()));
			viewHolder.textNumber.setText(contactItem.getPhoneNumber());
		} else {
			viewHolder.textName.setText(mValues.get(i).getMsisdnSender());
			viewHolder.textNumber.setVisibility(View.INVISIBLE);
			viewHolder.imageProfile.setImageResource(R.drawable.placeholder_contact_list);
			viewHolder.textLetter.setVisibility(View.GONE);

		}
		viewHolder.textAmount.setText(StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(mValues.get(i).getAmount())));

		viewHolder.mView.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				listener.OnPaymentClick(mValues.get(holder.getAdapterPosition()));
			}
		}));

	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public void removePayment(String paymentId) {
		for (Iterator<PendingPayment> iterator = mValues.iterator(); iterator.hasNext(); ) {
			PendingPayment payment = iterator.next();
			if (payment.getPaymentId().equals(paymentId)) {
				iterator.remove();
				notifyItemRemoved(mValues.indexOf(payment));
				notifyItemRangeChanged(0, mValues.size());
			}
		}
	}

	private String getInitials(@NonNull String completeName) {
		if (completeName.contains(" ")) {
			String first = completeName.substring(0, 1);
			if (first.length() > 1) {
				return first.toUpperCase();
			} else {
				String[] nameList = completeName.split(" ");
				if (TextUtils.isEmpty(first)) {
					first = nameList[0].substring(0, 1);
				}
				if (TextUtils.isEmpty(nameList[1])) {
					return first.toUpperCase();
				} else if (nameList[1].length() >= 1) {
					return first.toUpperCase() + nameList[1].substring(0, 1).toUpperCase();
				}
			}
		}
		if (!TextUtils.isEmpty(completeName)) {
			return completeName.substring(0, 1).toUpperCase();
		} else {
			return "";
		}
	}

	private static class PendingPaymentViewHolder extends RecyclerView.ViewHolder {

		View mView;
		TextView textName;
		TextView textNumber;
		TextView textAmount;
		TextView textLetter;
		ImageView imageProfile;
		ImageView logo;

		PendingPaymentViewHolder(View view) {
			super(view);
			this.mView = view;
			this.textName = view.findViewById(R.id.contact_consumer_name);
			this.textNumber = view.findViewById(R.id.contact_consumer_number);
			this.textAmount = view.findViewById(R.id.text_amount);
			this.textLetter = view.findViewById(R.id.contact_consumer_letter);
			this.imageProfile = view.findViewById(R.id.contact_consumer_image_profile);
			this.logo = view.findViewById(R.id.contact_consumer_is_active);
		}

	}

	public interface PendingPaymentInteraction {
		void OnPaymentClick(PendingPayment pendingPayment);
	}

}
