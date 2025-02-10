package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebViewCustom extends WebView {

	OnOverScrolledCallback callback;
	boolean canScroll = true;

	public WebViewCustom(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public WebViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebViewCustom(Context context) {
		super(context);
	}

	public interface OnOverScrolledCallback {
		void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
	}


	public void setCanScroll(boolean canScroll) {
		this.canScroll = canScroll;
	}

	public void setOnOverScrolledCallback(OnOverScrolledCallback callback) {
		this.callback = callback;
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		if (callback != null) {
			callback.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		}
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (canScroll) {
			requestDisallowInterceptTouchEvent(true);
			return super.onTouchEvent(event);
		} else {
			return false;
		}
	}

}
