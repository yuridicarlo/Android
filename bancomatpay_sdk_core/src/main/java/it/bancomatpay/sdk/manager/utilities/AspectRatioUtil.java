package it.bancomatpay.sdk.manager.utilities;

import android.app.Activity;
import android.graphics.Point;
import androidx.annotation.NonNull;

public class AspectRatioUtil {

	private EAspectRatio aspectRatio;

	public AspectRatioUtil(@NonNull Activity activityContext) {
		Point size = new Point();
		activityContext.getWindowManager().getDefaultDisplay().getRealSize(size);
		int screenWidth = size.x;
		int screenHeight = size.y;
		float ratio = (float) screenHeight / (float) screenWidth;
		if (ratio == EAspectRatio.RATIO_16_9.getRatio()) {
			aspectRatio = EAspectRatio.RATIO_16_9;
		} else if (ratio == EAspectRatio.RATIO_18_5_9.getRatio()) {
			aspectRatio = EAspectRatio.RATIO_18_5_9;
		}
	}

	public EAspectRatio getAspectRatio() {
		return this.aspectRatio;
	}

	public enum EAspectRatio {

		RATIO_16_9(1.77777778f), //Almost all devices
		RATIO_18_9(2f), //Tall devices
		RATIO_18_5_9(2.0555556f); //Tall and thin devices. Galaxy S8, S8+ ecc.

		private float ratio;

		EAspectRatio(float ratio) {
			this.ratio = ratio;
		}

		public float getRatio() {
			return ratio;
		}

	}

}
