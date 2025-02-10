package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.Locale;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsLabelPaymentAmountBinding;

public class LabelPaymentAmount extends AbstractLabel {

	private final WidgetsLabelPaymentAmountBinding binding;

	StringBuilder afterCommaText;
	StringBuilder beforeCommaText;
	LabelPaymentAmount.LabelListener listener;

	int centesimalMoney = 0;
	int maxLength = 4; // String.valueOf(Integer.MAX_VALUE).length() - 4; //Fix per non sforare il MAX_INTEGER e non far crashare


//    int maxValue = 2500; //in cents
//    int minValue = 100; //in cents

	public interface LabelListener {
		void onMoneyInserted(int money, boolean isDeletingCharacter);
	}

	public void setLabelListener(LabelPaymentAmount.LabelListener customLabelListener) {
		this.listener = customLabelListener;
	}

	public LabelPaymentAmount(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSaveEnabled(true);
		afterCommaText = new StringBuilder();
		beforeCommaText = new StringBuilder();
		binding = WidgetsLabelPaymentAmountBinding.inflate(LayoutInflater.from(context), this, true);

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelPaymentAmount);
		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.LabelPaymentAmount_cardElevation) {
				binding.cardViewContainer.setCardElevation(a.getDimension(attr, 0));
			} else if (attr == R.styleable.LabelPaymentAmount_labelTextSize) {
				binding.textCurrentThreshold.setTextSize(a.getDimension(attr, 20));
				binding.textCurrentThresholdFake.setTextSize(a.getDimension(attr, 20));
				binding.euroSymbol.setTextSize(a.getDimension(attr, 20));
			}
		}
		a.recycle();
	}

	@Override
	public void setMaxElements(int e) {
		// NON USATA
	}

	public void setMaxLength(int length) {
		this.maxLength = length;
	}

//    public void setMaxValue(int value) {
//        this.maxValue = value;
//    }
//
//    public void setMinValue(int value) {
//        this.minValue = value;
//    }

	@Override
	protected void updateView() {

		NumberFormat formatter = NumberFormat.getInstance(Locale.ITALY);

		if (mText.length() > 0) {

			binding.textCurrentThreshold.setTextColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.payment_total_label_payment_inserted));
			binding.textCurrentThreshold.setVisibility(VISIBLE);
			binding.textCurrentThresholdFake.setVisibility(GONE);
			binding.euroSymbol.setTextColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.payment_total_label_payment_inserted));

			if (mText.lastIndexOf(",") != -1) {
				binding.textCurrentThreshold.setText(String.format("%s,%s",
						formatter.format(Double.parseDouble(beforeCommaText.toString())), afterCommaText.toString()));
			} else {
				binding.textCurrentThreshold.setText(formatter.format(Double.parseDouble(beforeCommaText.toString())));
			}

		} else {
			binding.euroSymbol.setTextColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.grey_money_label));
			binding.textCurrentThresholdFake.setTextColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.grey_money_label));
			binding.textCurrentThreshold.setVisibility(GONE);
			binding.textCurrentThresholdFake.setVisibility(VISIBLE);
		}
	}

	@Override
	public void onTextEntered(String text) {
		CustomLogger.d("TEXT ENTERED", "Text: " + text);

		if (beforeCommaText.length() == 0 && text.equals(",")) {
			return;
		}

		if (mText.lastIndexOf(",") != -1 && text.equals(",")) {
			return;
		}
		if (afterCommaText.length() < 2 /*&& (maxValue > centesimalMoney || minValue < centesimalMoney)*/) {
			if (!text.equals(",")) {
				if (mText.lastIndexOf(",") == -1) {
					beforeCommaText.append(text);
				} else {
					afterCommaText.append(text);
				}
			}
			centesimalMoney = getCentesimalMoney();
			super.onTextEntered(text);
			if (maxLength < mText.length() /*maxValue < centesimalMoney || minValue > centesimalMoney*/
					&& !mText.toString().contains(",")) {
				onDeleteCharacter();
			} else {
				listener.onMoneyInserted(centesimalMoney, false);
			}
		}
	}

	@Override
	public void onDeleteCharacter() {
		if (mText.indexOf(",") != mText.length() - 1) {

			if (mText.lastIndexOf(",") == -1) {
				beforeCommaText.deleteCharAt(beforeCommaText.length() - 1);
			} else {
				afterCommaText.deleteCharAt(afterCommaText.length() - 1);
			}
		}

		centesimalMoney = getCentesimalMoney();
		super.onDeleteCharacter();
		listener.onMoneyInserted(centesimalMoney, true);
	}

	private int getCentesimalMoney() {
		StringBuilder afterCommaTextTmp = new StringBuilder();
		afterCommaTextTmp.append(afterCommaText);
		while (afterCommaTextTmp.length() < 2) {
			afterCommaTextTmp.append("0");
		}
		String toInteger = beforeCommaText.toString() + afterCommaTextTmp.toString();
		if (TextUtils.isEmpty(toInteger)) {
			return 0;
		}
		return Integer.parseInt(toInteger);
	}

	@Override
	public void onDeleteAllText() {

		beforeCommaText.delete(0, beforeCommaText.length());
		afterCommaText.delete(0, afterCommaText.length());
		centesimalMoney = 0;
		super.onDeleteAllText();
		listener.onMoneyInserted(centesimalMoney, true);
	}

	public void setText(String text) {
		binding.textCurrentThreshold.setText(text);
	}

	public void setInitialText(String initialText) {
		if (!TextUtils.isEmpty(initialText) && initialText.contains(",")) {
			mText = new StringBuilder(initialText);
			String[] entireText = initialText.split(",");
			beforeCommaText = new StringBuilder(entireText[0]);
			afterCommaText = new StringBuilder(entireText[1]);
			updateView();
		}
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			binding.textCurrentThreshold.setTextColor(ContextCompat.getColor(getContext(), R.color.payment_total_label_payment_inserted));
			if (getCentesimalMoney() > 0) {
				binding.euroSymbol.setTextColor(ContextCompat.getColor(getContext(), R.color.payment_total_label_payment_inserted));
			}
		} else {
			binding.textCurrentThreshold.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_money_label));
			binding.euroSymbol.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_money_label));
		}
	}

    private static class SavedState extends BaseSavedState {

        String afterCommaText;
        String beforeCommaText;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            afterCommaText = in.readString();
            beforeCommaText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(afterCommaText);
            out.writeString(beforeCommaText);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Obtain any state that our super class wants to save.
        Parcelable superState = super.onSaveInstanceState();

        // Wrap our super class's state with our own.
        SavedState myState = new SavedState(superState);
        myState.afterCommaText = this.afterCommaText.toString();
        myState.beforeCommaText = this.beforeCommaText.toString();

        // Return our state along with our super class's state.
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        // Cast the incoming Parcelable to our custom SavedState. We produced
        // this Parcelable before, so we know what type it is.
        SavedState savedState = (SavedState) state;

        // Grab our properties out of our SavedState.
        this.afterCommaText = new StringBuilder(savedState.afterCommaText);
        this.beforeCommaText = new StringBuilder(savedState.beforeCommaText);

	    // Let our super class process state before we do because we should
	    // depend on our super class, we shouldn't imply that our super class
	    // might need to depend on us.
	    super.onRestoreInstanceState(savedState.getSuperState());

        // Update our visuals in whatever way we want, like...
        requestLayout(); //...or...
        invalidate(); //...or...
        updateView();
    }

}
