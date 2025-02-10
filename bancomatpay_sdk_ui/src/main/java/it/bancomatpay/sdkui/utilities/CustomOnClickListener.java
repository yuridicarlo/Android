package it.bancomatpay.sdkui.utilities;

import android.os.SystemClock;
import android.view.View;

public class CustomOnClickListener implements View.OnClickListener {

	View.OnClickListener listener;
	private long mLastClickTime = 0;

	public CustomOnClickListener(View.OnClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();
		if (listener != null) {
			this.listener.onClick(v);
		}
	}

}
