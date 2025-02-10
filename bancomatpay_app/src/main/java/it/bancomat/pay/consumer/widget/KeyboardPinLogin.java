package it.bancomat.pay.consumer.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.WidgetsKeyboardPinLoginBinding;
import it.bancomatpay.sdkui.widgets.KeyboardListener;

public class KeyboardPinLogin extends LinearLayout {

    private final WidgetsKeyboardPinLoginBinding binding;

    private final static String LENGTH = "LENGTH";
    private final static String STATE_TO_SAVE = "STATE_TO_SAVE";

    int currentLength;
    int maxLength = Integer.MAX_VALUE;

    List<View> mNumPadOther;

    public void setKeyboardListener(KeyboardListener<?> keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    KeyboardListener<?> keyboardListener;

    public KeyboardPinLogin(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsKeyboardPinLoginBinding.inflate(LayoutInflater.from(context), this, true);

        View.OnClickListener numberClickListener = button -> {
            currentLength++;
            if (keyboardListener != null) {
                keyboardListener.onTextEntered(((TextView) button).getText().toString());
            }

            updateView();
        };

        binding.numPadKey0.setOnClickListener(numberClickListener);
        binding.numPadKey1.setOnClickListener(numberClickListener);
        binding.numPadKey2.setOnClickListener(numberClickListener);
        binding.numPadKey3.setOnClickListener(numberClickListener);
        binding.numPadKey4.setOnClickListener(numberClickListener);
        binding.numPadKey5.setOnClickListener(numberClickListener);
        binding.numPadKey6.setOnClickListener(numberClickListener);
        binding.numPadKey7.setOnClickListener(numberClickListener);
        binding.numPadKey8.setOnClickListener(numberClickListener);
        binding.numPadKey9.setOnClickListener(numberClickListener);

        mNumPadOther = new ArrayList<>();
        mNumPadOther.add(binding.numPadKey0);
        mNumPadOther.add(binding.numPadKey1);
        mNumPadOther.add(binding.numPadKey2);
        mNumPadOther.add(binding.numPadKey3);
        mNumPadOther.add(binding.numPadKey4);
        mNumPadOther.add(binding.numPadKey5);
        mNumPadOther.add(binding.numPadKey6);
        mNumPadOther.add(binding.numPadKey7);
        mNumPadOther.add(binding.numPadKey8);
        mNumPadOther.add(binding.numPadKey9);
    }

    void onDeleteClicked() {
        if (keyboardListener != null) {
            keyboardListener.onDeleteCharacter();
        }
        currentLength--;
        updateView();
    }

    void onDeleteLongClicked() {
        if (keyboardListener != null) {
            keyboardListener.onDeleteAllText();
        }
        currentLength = 0;
        updateView();
    }

    public void setMaxLength(int max) {
        maxLength = max;
        if (keyboardListener != null) {
            keyboardListener.setMaxElements(max);
        }
        updateView();
    }

    public void updateView() {
        boolean editable = currentLength < maxLength;
        boolean cancelable = currentLength != 0;

        if (cancelable != binding.numPadKeyXImage.isEnabled()) {
            binding.numPadKeyXImage.setEnabled(cancelable);
        }
        if (editable != mNumPadOther.get(0).isEnabled()) {
            for (android.view.View View : mNumPadOther) {
                View.setEnabled(editable);
            }
        }
        if (!editable) {
            binding.numPadKeyXImage.setEnabled(false);
        }
    }

    public void changeColor() {
        /*Context context = BancomatApplication.getAppContext();
        backImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ico_delete));

        for (int i = 0; i < mNumPadOther.size(); i++) {
            ((TextView) mNumPadOther.get(i)).setTextColor(ContextCompat.getColor(context, R.color.greyLight));
        }*/
        Context context = BancomatApplication.getAppContext();
        for (View view : mNumPadOther) {
            if (view instanceof TextView) {
                TextView textNumber = (TextView) view;
                textNumber.setTextColor(ContextCompat.getColor(context, R.color.text_generic_color));
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_TO_SAVE, super.onSaveInstanceState());
        bundle.putInt(LENGTH, currentLength);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentLength = bundle.getInt(LENGTH, 0);
            state = bundle.getParcelable(STATE_TO_SAVE);
            updateView();
        }
        super.onRestoreInstanceState(state);
    }

    public void reset() {
        onDeleteLongClicked();
    }

}
