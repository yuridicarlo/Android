package it.bancomat.pay.consumer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import it.bancomatpay.consumer.databinding.WidgetsLabelTelephoneAppBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.AbstractLabel;

public class AppLabelTelephone extends AbstractLabel {

    private final WidgetsLabelTelephoneAppBinding binding;

    AppLabelTelephone.LabelListener listener;
    String number;
    StringBuilder sb;
    String prefix;
    int maxLength;

    public interface LabelListener {
        void onNumberInserted(String text);

        void onNumberDeleted(boolean deleteAll);

        void onPrefixClicked();
    }

    public void setLabelListener(AppLabelTelephone.LabelListener customLabelListener) {
        this.listener = customLabelListener;
    }

    public AppLabelTelephone(Context context, AttributeSet attrs) {
        super(context, attrs);
        sb = new StringBuilder();
        binding = WidgetsLabelTelephoneAppBinding.inflate(LayoutInflater.from(context), this, true);

        binding.phonePrefix.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null) {
                listener.onPrefixClicked();
            }
        }));
    }

    public void setMaxLength(int length) {
        this.maxLength = length;
    }

    @Override
    protected void updateView() {
        if (mText.length() < maxLength) {
            binding.msisdnDigit.setText(mText);
        } else {
            onDeleteCharacter();
        }
        listener.onNumberInserted(binding.msisdnDigit.getText().toString());
    }

    @Override
    public void onTextEntered(String text) {
        if (mText.length() <= maxLength) {
            super.onTextEntered(text);
        }
    }

    @Override
    public void onDeleteCharacter() {
        if (mText.length() >= 0) {
            super.onDeleteCharacter();
        } else {
            return;
        }
        number = mText.toString();
        listener.onNumberInserted(number);
        listener.onNumberDeleted(false);

    }

    @Override
    public void onDeleteAllText() {
        super.onDeleteAllText();
        listener.onNumberInserted(mText.toString());
        listener.onNumberDeleted(true);
    }

    @Override
    public void setMaxElements(int i) {

    }

    public void setText(String text) {
        mText = new StringBuilder(text);
        updateView();
    }

    public void setPrefixText(String countryCode, String prefix) {
        binding.phonePrefix.setText(String.format("%s %s", countryCode, prefix));
        this.prefix = prefix;
    }

    @Override
    public String getValue() {
        return prefix + super.getValue();
    }

    public String getPhone() {
        return super.getValue();
    }

}


