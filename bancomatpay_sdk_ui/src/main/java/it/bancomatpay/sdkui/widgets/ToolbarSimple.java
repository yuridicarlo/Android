package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsToolbarSimpleBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class ToolbarSimple extends RelativeLayout {

    private final WidgetsToolbarSimpleBinding binding;

    public ToolbarSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsToolbarSimpleBinding.inflate(LayoutInflater.from(context), this, true);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarSimple);

        binding.toolbarLeftImg.setImageDrawable(a.getDrawable(R.styleable.ToolbarSimple_leftImg));
        binding.toolbarRootLayout.setBackground(a.getDrawable(R.styleable.ToolbarSimple_backgroundSrc));
        binding.toolbarCenterImg.setImageDrawable(a.getDrawable(R.styleable.ToolbarSimple_centerImg));
        binding.toolbarRightImg.setImageDrawable(a.getDrawable(R.styleable.ToolbarSimple_rightImg));
        binding.toolbarRightCenterImg.setImageDrawable(a.getDrawable(R.styleable.ToolbarSimple_rightCenterImg));

        a.recycle();
    }

    public void setOnClickLeftImageListener(OnClickListener listener) {
        binding.toolbarLeftImg.setOnClickListener(new CustomOnClickListener(listener));
    }

    public void setOnClickRightImageListener(OnClickListener listener) {
        binding.toolbarRightImg.setOnClickListener(new CustomOnClickListener(listener));
    }

    public void setOnClickRightCenterImageListener(OnClickListener listener) {
        binding.toolbarRightCenterImg.setOnClickListener(new CustomOnClickListener(listener));
    }

    public void setLeftImageVisibility(boolean isVisible) {
        if (isVisible) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.toolbarLeftImg, DEFAULT_DURATION);
        } else {
            binding.toolbarLeftImg.setVisibility(View.INVISIBLE);
        }
    }

    public void setCenterImageVisibility(boolean isVisible) {
        if (isVisible) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.toolbarCenterImg, DEFAULT_DURATION);
        } else {
            binding.toolbarCenterImg.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightImageVisibility(boolean isVisible) {
        if (isVisible) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.toolbarRightImg, DEFAULT_DURATION);
        } else {
            binding.toolbarRightImg.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightCenterImageVisibility(boolean isVisible) {
        if (isVisible) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.toolbarRightCenterImg, DEFAULT_DURATION);
        } else {
            binding.toolbarRightCenterImg.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isLeftImageVisible() {
        return binding.toolbarLeftImg.getVisibility() == View.VISIBLE;
    }

    public boolean isCenterImageVisible() {
        return binding.toolbarCenterImg.getVisibility() == View.VISIBLE;
    }

    public boolean isRightImageVisible() {
        return binding.toolbarRightImg.getVisibility() == View.VISIBLE;
    }

    public void enableRightImage() {
        binding.toolbarRightImg.setEnabled(true);
    }

    public void disableRightImage() {
        binding.toolbarRightImg.setEnabled(false);
    }

    public void setLeftImage(int drawable) {
        binding.toolbarLeftImg.setImageResource(drawable);
    }

    public void setCenterImage(int drawable) {
        binding.toolbarCenterImg.setImageResource(drawable);
    }

    public void setRightImage(int drawable) {
        binding.toolbarRightImg.setImageResource(drawable);
    }

    public View getRightImageReference() {
        return binding.toolbarRightImg;
    }

}
