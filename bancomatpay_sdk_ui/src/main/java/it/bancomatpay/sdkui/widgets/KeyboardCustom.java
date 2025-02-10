package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsKeyboardCustomBinding;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class KeyboardCustom extends LinearLayout {

	private final WidgetsKeyboardCustomBinding binding;

	private final static String STATE_TO_SAVE = "STATE_TO_SAVE";
	int currentLength;

	KeyboardType keyboardType;
	StringBuilder phone;
	String prefix;
	int maxValue;

	public void setKeyboardListener(KeyboardListener<?> keyboardListener) {
		this.keyboardListener = keyboardListener;
	}

	KeyboardListener<?> keyboardListener;

	public KeyboardCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		binding = WidgetsKeyboardCustomBinding.inflate(LayoutInflater.from(context), this, true);

		OnClickListener listenerNumber = v -> onNumberClicked((TextView) v);

		binding.numPadKey1.setOnClickListener(listenerNumber);
		binding.numPadKey2.setOnClickListener(listenerNumber);
		binding.numPadKey3.setOnClickListener(listenerNumber);
		binding.numPadKey4.setOnClickListener(listenerNumber);
		binding.numPadKey5.setOnClickListener(listenerNumber);
		binding.numPadKey6.setOnClickListener(listenerNumber);
		binding.numPadKey7.setOnClickListener(listenerNumber);
		binding.numPadKey8.setOnClickListener(listenerNumber);
		binding.numPadKey9.setOnClickListener(listenerNumber);
		binding.numPadKey0.setOnClickListener(listenerNumber);
		binding.numPadKeyComma.setOnClickListener(listenerNumber);

		/*prefix = BancomatDataManager.getInstance().getPrefixCountryCode();*/
		prefix = "+39";
		phone = new StringBuilder();
		phone.append(prefix);
	}

	private void onNumberClicked(TextView button) {
		currentLength++;
		if (keyboardListener != null) {
			keyboardListener.onTextEntered(button.getText().toString());
			if (keyboardType == KeyboardType.PHONE_KEYBOARD) {
				phone.append(button.getText());
				if (PhoneNumber.isValidNumber(phone.toString())) {
					binding.keyboardButtonContinueOff.setVisibility(INVISIBLE);
					binding.keyboardButtonContinueOn.setVisibility(VISIBLE);
				} else {
					binding.keyboardButtonContinueOn.setVisibility(INVISIBLE);
					binding.keyboardButtonContinueOff.setVisibility(VISIBLE);
				}
			} else if (keyboardType == KeyboardType.PAYMENT_KEYBOARD) {
				binding.keyboardButtonContinueOff.setVisibility(INVISIBLE);
				binding.keyboardButtonContinueOn.setVisibility(VISIBLE);
			} else if (keyboardType == KeyboardType.OTP_KEYBOARD) {
				// phone.append(button.getText());
				if (currentLength == maxValue) {
					binding.keyboardButtonContinueOff.setVisibility(INVISIBLE);
					binding.keyboardButtonContinueOn.setVisibility(VISIBLE);
				} else {
					binding.keyboardButtonContinueOn.setVisibility(INVISIBLE);
					binding.keyboardButtonContinueOff.setVisibility(VISIBLE);
				}
			}
		} else {
			binding.keyboardButtonContinueOn.setVisibility(INVISIBLE);
			binding.keyboardButtonContinueOff.setVisibility(VISIBLE);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		super.onSaveInstanceState();
		Bundle bundle = new Bundle();
		bundle.putParcelable(STATE_TO_SAVE, super.onSaveInstanceState());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			state = bundle.getParcelable(STATE_TO_SAVE);
		}
		super.onRestoreInstanceState(state);
	}

	public void setKeyboardType(KeyboardType keyboard) {
		keyboardType = keyboard;
		if (keyboardType == KeyboardType.PHONE_KEYBOARD) {
			//mNumPadComma.setText(R.string.widgets_keyboard_pin_num_pad_plus);
			binding.numPadKeyComma.setVisibility(View.INVISIBLE);
			binding.numPadKeyComma.setOnClickListener(new CustomOnClickListener(null));
			maxValue = 13;

		} else if (keyboardType == KeyboardType.PAYMENT_KEYBOARD || keyboardType == KeyboardType.DEFAULT) {
			binding.numPadKeyComma.setText(R.string.widgets_keyboard_pin_num_pad_comma);
		} else if (keyboardType == KeyboardType.OTP_KEYBOARD) {
			binding.numPadKeyComma.setVisibility(INVISIBLE);
			maxValue = 6;
		}
	}


	public void reset() {
		currentLength = 0;
		phone = new StringBuilder();
		if (keyboardType == KeyboardType.PHONE_KEYBOARD) {
			phone.append(prefix);
		}
	}

	public void deleteCharacter() {
		if (currentLength > 0) {
			currentLength--;
		}
		if (phone.length() > 0) {
			phone.deleteCharAt(phone.length() - 1);
		}
	}

	public void hideNumPadComma() {
		binding.numPadKeyComma.setVisibility(INVISIBLE);
	}

	public void setButtonNextEnabled(boolean enabled) {
		if (enabled) {
			binding.keyboardButtonContinueOn.setVisibility(VISIBLE);
			binding.keyboardButtonContinueOff.setVisibility(INVISIBLE);
		} else {
			binding.keyboardButtonContinueOn.setVisibility(INVISIBLE);
			binding.keyboardButtonContinueOff.setVisibility(VISIBLE);
		}
	}

	public void setButtonNextClickListener(View.OnClickListener listener) {
		binding.keyboardButtonContinueOn.setOnClickListener(new CustomOnClickListener(listener));
	}

	public View getButtonContinueOn() {
		return binding.keyboardButtonContinueOn;
	}

	public View getButtonContinueOff() {
		return binding.keyboardButtonContinueOff;
	}

}

