package it.bancomat.pay.consumer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import it.bancomatpay.consumer.databinding.WidgetsKeyboardOtpBinding;


public class KeyboardCode extends LinearLayout {

    private final static String TAG = KeyboardCode.class.getSimpleName();

    WidgetsKeyboardOtpBinding binding;

    public KeyboardCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsKeyboardOtpBinding.inflate(LayoutInflater.from(context), this, true);
        binding.numPadKeyX.setOnLongClickListener(v -> {
            binding.getModel().cancelAllDigits();
            return true;
        });
    }

    public void setModel(KeyboardCodeObservable keyboardCodeObservable){
        binding.setModel(keyboardCodeObservable);
    }
}
