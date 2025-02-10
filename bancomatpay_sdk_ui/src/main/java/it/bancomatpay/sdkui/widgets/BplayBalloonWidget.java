package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import it.bancomatpay.sdkui.databinding.WidgetBplayBalloonBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class BplayBalloonWidget extends ConstraintLayout {

private final WidgetBplayBalloonBinding binding;
OnCloseButtonInteractionListener mListener;

    public interface OnCloseButtonInteractionListener{
        void closeBalloonButtonInteractionListener();
    }

    public void setBalloonListener(OnCloseButtonInteractionListener listener){
        this.mListener = listener;
    }

    public BplayBalloonWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetBplayBalloonBinding.inflate(LayoutInflater.from(context), this, true);
        binding.closeButton.setOnClickListener(new CustomOnClickListener(v -> {
            if (mListener != null) {
                mListener.closeBalloonButtonInteractionListener();
            }
        }));
    }


}
