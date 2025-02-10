package it.bancomat.pay.consumer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.WidgetsLabelPasswordLoginBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class LabelPasswordLogin extends AbstractLabelLogin {

    private final WidgetsLabelPasswordLoginBinding binding;

    int maxElements;

    public interface LabelListener {
        void onPinInserted(String pin);

        void onDeleteLongClicked();

        void onStartEditing();
    }

    LabelListener listener;

    public void setListener(LabelListener listener) {
        this.listener = listener;
    }

    public void changeColor() {
        binding.password.changeColor();
        binding.cancelButton.setImageResource(R.drawable.cancel_azur);
    }

    public LabelPasswordLogin(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsLabelPasswordLoginBinding.inflate(LayoutInflater.from(context), this, true);

        binding.cancelButton.setOnClickListener(new CustomOnClickListener(v -> onDeleteCharacter()));
        binding.cancelButton.setOnLongClickListener(v -> {
            listener.onDeleteLongClicked();
            onDeleteAllText();
            return true;
        });
    }

    @Override
    public void setMaxElements(int e) {
        maxElements = e;
        binding.password.setMaxElements(e);
        updateView(false);
    }

    @Override
    protected void updateView(boolean isDeleting) {
        if (mText.length() > maxElements) {
            mText.deleteCharAt(mText.length() - 1);
            return;
        }
        binding.password.setCurrentElement(mText.length(), isDeleting);
    }

    @Override
    public void onTextEntered(String text) {
        super.onTextEntered(text);
        if (mText.length() == maxElements && listener != null) {
            listener.onPinInserted(mText.toString());
        }
        if (mText.length() == 1 && listener != null) {
            listener.onStartEditing();
        }
    }

    public void shake() {
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        binding.password.startAnimation(shake);
    }

}
