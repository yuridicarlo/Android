package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsProgressBarSpendingLimitsBinding;

public class ProgressBarSpendingLimits extends RelativeLayout {

    private final WidgetsProgressBarSpendingLimitsBinding binding;

    public ProgressBarSpendingLimits(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsProgressBarSpendingLimitsBinding.inflate(LayoutInflater.from(context), this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarSpendingLimits);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ProgressBarSpendingLimits_subtitleName) {
                binding.progressBarSpendingLimitsSubtitle.setText(a.getString(attr));
            }
        }
        a.recycle();
    }

    public void setMinPrice(String label) {
        binding.progressBarSpendingLimitsSpendibile.setText(label);
    }

    public void setMaxPrice(String label) {
        binding.progressBarSpendingLimitsLimit.setText(label);
    }

    public void setRangeProgress(long min, long max, long current) {
        max = max - min;
        current = current - min;
        current = max != 0 ? (current * 1000 / max) : (current * 1000);
        binding.progressBarSpendingLimits.setMax(1000);
        binding.progressBarSpendingLimits.setProgress((int) current);
    }

}