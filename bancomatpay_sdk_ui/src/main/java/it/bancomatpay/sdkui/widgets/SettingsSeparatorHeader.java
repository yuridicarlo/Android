package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsSettingsSeparatorHeaderBinding;

public class SettingsSeparatorHeader extends RelativeLayout {

	private final WidgetsSettingsSeparatorHeaderBinding binding;

	public SettingsSeparatorHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		binding = WidgetsSettingsSeparatorHeaderBinding.inflate(LayoutInflater.from(context), this, true);

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsSeparatorHeader);
		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.SettingsSeparatorHeader_title) {
				binding.textTitle.setText(a.getString(attr));
			}
		}
		a.recycle();
	}

	public void setText(CharSequence text) {
		binding.textTitle.setText(text);
	}

}
