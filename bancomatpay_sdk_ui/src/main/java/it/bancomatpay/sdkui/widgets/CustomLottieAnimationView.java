package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

import it.bancomatpay.sdkui.R;

public class CustomLottieAnimationView extends LottieAnimationView {

    Drawable drawableResource;

    public CustomLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomLottieAnimationView);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CustomLottieAnimationView_lottie_placeholder) {
                drawableResource = a.getDrawable(attr);
            }
        }
        a.recycle();
    }

    @Override
    public void setAnimation(String assetName) {
        new Handler().post(() -> {
            try {
                super.setAnimation(assetName);
            } catch (Exception e) {
                if (drawableResource != null) {
                    setImageDrawable(drawableResource);
                } else {
                    setImageResource(android.R.color.white);
                }
            }
        });
    }

}
