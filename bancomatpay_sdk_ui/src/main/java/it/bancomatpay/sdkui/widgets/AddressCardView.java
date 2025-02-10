package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsAddressCardViewBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class AddressCardView extends RelativeLayout {

	private final WidgetsAddressCardViewBinding binding;

	public AddressCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		binding = WidgetsAddressCardViewBinding.inflate(LayoutInflater.from(context), this, true);

	}

	public void setTextFirstLine(String text) {
		binding.textLineFirst.setText(text);
	}

	public void setTextSecondLine(String text) {
		binding.textLineSecond.setText(text);
	}

	public void setTextThirdLine(String text) {
		binding.textLineThird.setText(text);
	}

	public void setAddressType(BankIdAddress.EBankIdAddressType addressType) {
		if (addressType == BankIdAddress.EBankIdAddressType.SHIPPING) {
			binding.textAddressType.setText(getContext().getString(R.string.bank_id_adress_type_expedition));
		} else if (addressType == BankIdAddress.EBankIdAddressType.BILLING) {
			binding.textAddressType.setText(getContext().getString(R.string.bank_id_adress_type_billing));
		} else if (addressType == BankIdAddress.EBankIdAddressType.BOTH) {
			binding.textAddressType.setText(getContext().getString(R.string.bank_id_adress_type_expedition_and_billing));
		}
	}

	public void setFavorite(boolean favorite) {
		if (favorite) {
			binding.imageStar.setVisibility(VISIBLE);
		} else {
			binding.imageStar.setVisibility(INVISIBLE);
		}
	}

	public void setOnClickListener(OnClickListener listener) {
		binding.cardViewAddress.setOnClickListener(new CustomOnClickListener(listener));
	}

}
