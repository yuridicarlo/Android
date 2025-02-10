package it.bancomat.pay.consumer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomatpay.consumer.R;

public class PasswordViewLogin extends LinearLayout {

    private final static String CURRENT_ELEMENT = "CURRENT_ELEMENT";
    private final static String MAX_ELEMENTS = "MAX_ELEMENTS";
    private final static String STATE_TO_SAVE = "STATE_TO_SAVE";

    int currentElement;
    int maxElements;
    Drawable mImageFullSrc;
    Drawable mImageEmptySrc;
    float mCirclePadding;

    public PasswordViewLogin(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasswordViewLogin);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.PasswordViewLogin_imagePadding) {
                mCirclePadding = a.getDimension(attr, 0);
            } else if (attr == R.styleable.PasswordViewLogin_imageFullSrc) {
                mImageFullSrc = a.getDrawable(attr);
            } else if (attr == R.styleable.PasswordViewLogin_imageEmptySrc) {
                mImageEmptySrc = a.getDrawable(attr);
            }
        }
        a.recycle();
        setGravity(Gravity.CENTER);
    }

    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;
        ImageView imageView;
        int padding = (int) mCirclePadding;
        removeAllViews();
        for (int i = 0; i < maxElements; i++) {
            imageView = new ImageView(getContext());
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setImageDrawable(mImageEmptySrc);
            addView(imageView);
        }
    }

    private void updateView(boolean isDeleting) {
        if (currentElement > maxElements) {
            currentElement = maxElements;
        }
        for (int i = 0; i < currentElement; i++) {
            ImageView currentImage = ((ImageView) getChildAt(i));
            currentImage.setImageDrawable(mImageFullSrc);
            if (i >= currentElement - 1 && !isDeleting) {
                /*if (isDeleting) {
                    Animation zoomOut = AnimationUtils.loadAnimation(BancomatApplication.getAppContext(), R.anim.zoom_out);
                    zoomOut.setInterpolator(new DecelerateInterpolator());
                    currentImage.startAnimation(zoomOut);
                } else {*/
                    Animation zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
                    zoomIn.setInterpolator(new DecelerateInterpolator());
                    currentImage.startAnimation(zoomIn);
                /*}*/
            }
        }
        for (int i = currentElement; i < getChildCount(); i++) {
            ImageView currentImage = ((ImageView) getChildAt(i));
            currentImage.setImageDrawable(mImageEmptySrc);
            /*Animation zoomOut = AnimationUtils.loadAnimation(BancomatApplication.getAppContext(), R.anim.zoom_out);
            zoomOut.setInterpolator(new DecelerateInterpolator());
            currentImage.startAnimation(zoomOut);*/
        }
    }

    public void changeColor() {
        Context context = BancomatApplication.getAppContext();
        mImageFullSrc = ContextCompat.getDrawable(context, R.drawable.pin_azur_full);
        mImageEmptySrc = ContextCompat.getDrawable(context, R.drawable.pin_azur_empty);
    }

    public void setCurrentElement(int currentElement, boolean isDeleting) {
        this.currentElement = currentElement;
        updateView(isDeleting);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_TO_SAVE, super.onSaveInstanceState());
        bundle.putInt(CURRENT_ELEMENT, currentElement);
        bundle.putInt(MAX_ELEMENTS, maxElements);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentElement = bundle.getInt(CURRENT_ELEMENT, 0);
            maxElements = bundle.getInt(MAX_ELEMENTS, 0);
            state = bundle.getParcelable(STATE_TO_SAVE);
            setCurrentElement(maxElements, false);
            //updateView(false);
        }
        super.onRestoreInstanceState(state);
    }

}
