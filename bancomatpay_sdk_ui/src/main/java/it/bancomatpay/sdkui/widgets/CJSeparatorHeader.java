package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsCjSeparatorHeaderBinding;

public class CJSeparatorHeader extends RelativeLayout {

	private final WidgetsCjSeparatorHeaderBinding binding;

	public CJSeparatorHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		binding = WidgetsCjSeparatorHeaderBinding.inflate(LayoutInflater.from(context), this, true);

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CJSeparatorHeader);
		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.CJSeparatorHeader_title_cj) {
				binding.textTitle.setText(a.getString(attr));
			}
		}
		a.recycle();
	}

	public void setText(CharSequence text) {
		binding.textTitle.setText(text);
	}

}
