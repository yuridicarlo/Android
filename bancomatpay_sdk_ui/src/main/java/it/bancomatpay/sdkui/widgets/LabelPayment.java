package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsLabelPaymentBinding;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class LabelPayment extends LinearLayout implements KeyboardListener<Long> {

    int maxLength;
    private final static String PRICE = "PRICE";
    private final static String STATE_TO_SAVE = "STATE_TO_SAVE";
    CustomLabelListener customLabelListener;
    long mPrice = 0;

    private final WidgetsLabelPaymentBinding binding;

    public void setCustomLabelListener(CustomLabelListener customLabelListener) {
        this.customLabelListener = customLabelListener;
    }

    public LabelPayment(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsLabelPaymentBinding.inflate(LayoutInflater.from(context), this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelPayment);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.LabelPayment_maxLength) {
                maxLength = a.getInteger(attr, Integer.MAX_VALUE);
            }
        }
        a.recycle();
        //mCurrencySymbolLabel.setText(mCurrencySymbol);
        updateView();
    }

    public void setPrice(long price) {
        mPrice = price;
        updateView();
    }

    @Override
    public Long getValue() {
        return mPrice;
    }

    @Override
    public void setMaxElements(int i) {

    }

    @Override
    public void onTextEntered(String text) {
        boolean validEntry;
        int multiplication = 10;
        long addition = 0;

        long price = mPrice;

        if (text.contentEquals("0")) {
            validEntry = price > 0 && getNumberOfCharacters(price) < maxLength;
        } else if (text.contentEquals("00")) {
            validEntry = price > 0 && getNumberOfCharacters(price) < maxLength - 1;
            multiplication = 100;
        } else {
            validEntry = getNumberOfCharacters(price) < maxLength;
            addition = Long.parseLong(text);
        }

        if (validEntry) {
            mPrice = (price * multiplication) + addition;
            updateView();
        }
    }

    @Override
    public void onDeleteCharacter() {
        if (mPrice > 0) {
            // we want to drop the last digit so even though we are losing accuracy it is what we need & want
            mPrice = (mPrice / 10);
            updateView();
        }
    }

    @Override
    public void onDeleteAllText() {
        if (mPrice > 0) {
            reset();
        }
    }


    public interface CustomLabelListener {
        void onChange(long price);
    }

    private void reset() {
        mPrice = 0;
        setPrezzo("");
        if (customLabelListener != null) {
            customLabelListener.onChange(mPrice);
        }
        updateView();
    }

    private void updateView() {
//        int precision = homeMenuConfig.getPrecision(); //todo prenderlo da homeConfig
        int precision = 2; //TODO Stub value, check if valid
        if (mPrice > 0) {
            setPrezzo(StringUtils.getAmountFormatted(mPrice, precision));
            if (customLabelListener != null) {
                customLabelListener.onChange(mPrice);
            }
        } else {
            setPrezzo(StringUtils.getAmountFormatted(0, precision));
            if (customLabelListener != null) {
                customLabelListener.onChange(0);
            }
        }
    }

    private void setPrezzo(String price) {

        binding.pagamentoPrezzo.setText("");

        if (price.length() > 0) {

            binding.pagamentoPrezzo.setText(price);
            //mTextLabel.setSelection(price.length());
        }
    }

    private int getNumberOfCharacters(long n) {
        return Long.toString(n).length();

    }

    @Override
    protected Parcelable onSaveInstanceState() {

        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_TO_SAVE, super.onSaveInstanceState());
        bundle.putLong(PRICE, mPrice);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPrice = bundle.getLong(PRICE, 0);
            state = bundle.getParcelable(STATE_TO_SAVE);
            updateView();
        }
        super.onRestoreInstanceState(state);
    }
}

