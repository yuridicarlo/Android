package it.bancomatpay.sdkui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bancomatpay.sdk.manager.task.model.Address;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.AddressCardView;

public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final List<BankIdAddress> mValues;
	private final ItemAddressClickListener mListener;

	public AddressAdapter(List<BankIdAddress> addressList, ItemAddressClickListener listener) {
		this.mValues = addressList;
		this.mListener = listener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.bank_id_address_item, parent, false);
		return new ViewHolderAddress(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

		ViewHolderAddress holder = (ViewHolderAddress) viewHolder;
		holder.mItem = mValues.get(position);

		holder.cardViewAddress.setTextFirstLine(holder.mItem.getAddress().getStreet());
		holder.cardViewAddress.setTextSecondLine(getSecondLineTextFormatted(holder.mItem.getAddress()));
		holder.cardViewAddress.setTextThirdLine(/*holder.mItem.getAddress().getCountry()*/ "Country stub");

		holder.cardViewAddress.setAddressType(holder.mItem.getAddressType());
		holder.cardViewAddress.setFavorite(holder.mItem.isDefaultBillingAddress() || holder.mItem.isDefaultShippingAddress());

		holder.cardViewAddress.setOnClickListener(new CustomOnClickListener(v -> {
			if (mListener != null) {
				mListener.onAddressClicked(holder.mItem);
			}
		}));

	}

	private String getSecondLineTextFormatted(Address addressData) {
		return addressData.getCity() + ",\u00A0" + addressData.getProvince() + "\u00A0" + addressData.getPostalCode();
	}

	@Override
	public int getItemCount() {
		if (mValues != null) {
			return mValues.size();
		}
		return 0;
	}

	public void updateList(List<BankIdAddress> addressList) {
		mValues.clear();
		mValues.addAll(addressList);
		notifyDataSetChanged();
	}

	private static class ViewHolderAddress extends RecyclerView.ViewHolder {

		AddressCardView cardViewAddress;
		BankIdAddress mItem;

		ViewHolderAddress(View view) {
			super(view);
			cardViewAddress = view.findViewById(R.id.address_card_view);
		}
	}

	public interface ItemAddressClickListener {
		void onAddressClicked(BankIdAddress address);
	}

}
