package it.bancomatpay.sdkui.utilities;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.bancomatpay.sdkui.R;

public class SnackbarUtil {

	private static final int DURATION = 4000;
	private static int marginTop = 50;

	public static void setMarginTop(int marginTop) {
		SnackbarUtil.marginTop = marginTop;
	}

	/*public static void showSnackbarMessage(Activity activity, String message) {
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
		if (viewGroup != null) {
			Snackbar snackbar = Snackbar.make(viewGroup, message, DURATION);
			View view = snackbar.getView();
			view.setBackgroundResource(R.drawable.snackbar_message_background);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
			params.gravity = Gravity.TOP;
			params.topMargin = 50;
			view.setLayoutParams(params);
			TextView tv = view.findViewById(R.id.snackbar_text);
			tv.setTextColor(ContextCompat.getColor(activity, android.R.color.white));
			tv.setTypeface(typeface);
			snackbar.show();
		}
	}

	public static void showSnackbarAction(Activity activity, String message, String action, View.OnClickListener clickListener) {
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
		if (viewGroup != null) {
			Snackbar snackbar = Snackbar.make(viewGroup, message, DURATION)
					.setAction(action, clickListener)
					.setActionTextColor(ContextCompat.getColor(activity, android.R.color.white));
			View view = snackbar.getView();
			view.setBackgroundResource(R.drawable.snackbar_message_background);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
			params.gravity = Gravity.TOP;
			params.topMargin = 50;
			view.setLayoutParams(params);
			TextView tv = view.findViewById(R.id.snackbar_text);
			tv.setTextColor(ContextCompat.getColor(activity, android.R.color.white));
			tv.setTypeface(typeface);
			snackbar.show();
		}
	}*/

	public static void showSnackbarMessageCustom(Activity activity, String message) {
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
		if (viewGroup != null) {
			Snackbar snackbar = Snackbar.make(viewGroup, message, DURATION);
			Snackbar.SnackbarLayout view = (Snackbar.SnackbarLayout) snackbar.getView();
			view.setBackgroundResource(R.drawable.snackbar_message_background);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
			params.gravity = Gravity.TOP;
			params.topMargin = marginTop;
			view.setLayoutParams(params);
			TextView tv = view.findViewById(R.id.snackbar_text);
			tv.setHeight(0);

			View snackView = View.inflate(activity, R.layout.snackbar_custom, null);
			TextView textMessage = snackView.findViewById(R.id.text_snackbar);
			textMessage.setText(message);

			snackView.findViewById(R.id.layout_snackbar_buttons).setVisibility(View.GONE);

			view.setPadding(0, 0, 0, 0);
			view.addView(snackView, 0);

			snackbar.show();
		}
	}

	public static void showSnackbarMessageCustom(Activity activity, String message, View.OnClickListener dismissListener) {
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
		if (viewGroup != null) {
			Snackbar snackbar = Snackbar.make(viewGroup, message, DURATION);
			Snackbar.SnackbarLayout view = (Snackbar.SnackbarLayout) snackbar.getView();
			view.setBackgroundResource(R.drawable.snackbar_message_background);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
			params.gravity = Gravity.TOP;
			params.topMargin = marginTop;
			view.setLayoutParams(params);
			TextView tv = view.findViewById(R.id.snackbar_text);
			tv.setHeight(0);

			View snackView = View.inflate(activity, R.layout.snackbar_custom, null);
			TextView textMessage = snackView.findViewById(R.id.text_snackbar);
			textMessage.setText(message);

			snackView.findViewById(R.id.layout_snackbar_buttons).setVisibility(View.GONE);

			view.setPadding(0, 0, 0, 0);
			view.addView(snackView, 0);

			snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
				@Override
				public void onDismissed(Snackbar transientBottomBar, int event) {
					super.onDismissed(transientBottomBar, event);
					dismissListener.onClick(snackView);
				}
			});

			snackbar.show();
		}
	}

	/*public static void showSnackbarActionCustom(Activity activity, String message,
	                                            String action1, View.OnClickListener listener1,
	                                            @Nullable String action2, @Nullable View.OnClickListener listener2) {
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
		if (viewGroup != null) {
			Snackbar snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_INDEFINITE);
			Snackbar.SnackbarLayout view = (Snackbar.SnackbarLayout) snackbar.getView();
			view.setBackgroundResource(R.drawable.snackbar_message_background);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
			params.gravity = Gravity.TOP;
			params.topMargin = marginTop;
			view.setLayoutParams(params);
			TextView tv = view.findViewById(R.id.snackbar_text);
			tv.setHeight(0);

			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View snackView = inflater.inflate(R.layout.snackbar_custom, null);
			TextView textMessage = snackView.findViewById(R.id.text_snackbar);
			textMessage.setText(message);

			Button button1 = snackView.findViewById(R.id.button_snackbar_1);
			Button button2 = snackView.findViewById(R.id.button_snackbar_2);

			button1.setText(action1);
			button1.setOnClickListener(new CustomOnClickListener(v -> {
				listener1.onClick(v);
				snackbar.dismiss();
			}));

			if (TextUtils.isEmpty(action2) || listener2 == null) {
				button2.setVisibility(View.GONE);
			} else {
				button2.setText(action2);
				button2.setOnClickListener(new CustomOnClickListener(v -> {
					listener2.onClick(v);
					snackbar.dismiss();
				}));
			}

			view.setPadding(0, 0, 0, 0);
			view.addView(snackView, 0);

			snackbar.show();
		}
	}*/

}
