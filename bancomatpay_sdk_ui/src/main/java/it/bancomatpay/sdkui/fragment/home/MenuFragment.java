package it.bancomatpay.sdkui.fragment.home;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.manager.events.update.UserDataUpdate;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ProfileData;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.BitmapCache;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.ToolbarSimple;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_MENU_SECTION;

public class MenuFragment extends GenericErrorFragment {

	ToolbarSimple toolbar;
	Space spaceTop;
	View layoutProfile;
	TextView profileName;
	ImageView profileImage;
	TextView profileLetterNoImage;
	View layoutNotificationsCircle;
	TextView textNotificationsNumber;
	CardView cardViewMovements;
	CardView cardViewNotifications;
	CardView cardViewSettings;
	//CardView cardViewCashback;

	private InteractionListener listener;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_menu, container, false);

		toolbar = view.findViewById(R.id.toolbar_simple);
		spaceTop = view.findViewById(R.id.space_top);
		layoutProfile = view.findViewById(R.id.layout_profile);
		profileName = view.findViewById(R.id.profile_name);
		profileImage = view.findViewById(R.id.image_profile);
		profileLetterNoImage = view.findViewById(R.id.profile_letter);
		layoutNotificationsCircle = view.findViewById(R.id.layout_notifications_circle);
		textNotificationsNumber = view.findViewById(R.id.text_notifications_number);
		cardViewMovements = view.findViewById(R.id.card_view_movements);
		cardViewNotifications = view.findViewById(R.id.card_view_notifications);
		cardViewSettings = view.findViewById(R.id.card_view_settings);
		//cardViewCashback = view.findViewById(R.id.card_view_cashback);

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initLayout();
	}

	protected void initLayout() {
		toolbar.setOnClickLeftImageListener(v ->
				BCMReturnHomeCallback.getInstance().getReturnHomeListener()
						.goToHome(requireActivity(), true, false, false)
		);
		toolbar.setRightImageVisibility(false);
		toolbar.setRightCenterImageVisibility(false);

		layoutProfile.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				listener.onProfileClick();
			}
		}));

		cardViewMovements.setOnClickListener(new CustomOnClickListener(v -> HomeFlowManager.goToTransactions(requireActivity())));
		cardViewNotifications.setOnClickListener(new CustomOnClickListener(v -> HomeFlowManager.goToNotifications(requireActivity())));
		cardViewSettings.setOnClickListener(new CustomOnClickListener(v -> HomeFlowManager.goToSettings(requireActivity())));
		/*
		cardViewCashback.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				CjUtils.getInstance().sendCustomerJourneyTagEvent(getContext(), KEY_CASHBACK_MENU_SECTION, null, false);
				listener.onCashbackClick();
			}
		}));
		 */

		if (ApplicationModel.getInstance().getUserData() != null) {
			updateProfileView();
		}

		int insetTop = BancomatDataManager.getInstance().getScreenInsetTop();
		if (insetTop != 0) {
			spaceTop.post(() -> {
				LinearLayout.LayoutParams spaceParams = (LinearLayout.LayoutParams) spaceTop.getLayoutParams();
				spaceParams.height = insetTop;
				spaceTop.setLayoutParams(spaceParams);
				spaceTop.requestLayout();
			});
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(UserDataUpdate event) {
		updateProfileView();

		if (event.getUserDataResult().isSuccess()) {
			UserData userData = event.getUserDataResult().getResult();
			if (userData.getPaymentRequestNumber() != null) {
				if (userData.getPaymentRequestNumber().intValue() > 0) {
					layoutNotificationsCircle.post(() -> {
						layoutNotificationsCircle.setVisibility(View.VISIBLE);
						textNotificationsNumber.setText(String.valueOf(userData.getPaymentRequestNumber().intValue()));
					});
				} else {
					layoutNotificationsCircle.post(() -> layoutNotificationsCircle.setVisibility(View.INVISIBLE));
				}
			}
		}
	}

	protected void updateProfileView() {
		ProfileData profileData = BancomatSdk.getInstance().getProfileData();
		if (profileData.getName() != null) {
			profileName.setText(profileData.getName());
		}
		Bitmap bitmap;
		try {
			Uri uri = Uri.parse(profileData.getImage());
			bitmap = BitmapCache.getInstance().getThumbnail(uri, requireContext());
		} catch (Exception e) {
			bitmap = null;
		}
		if (bitmap != null) {
			profileImage.setImageBitmap(bitmap);
			profileLetterNoImage.setVisibility(View.GONE);
		} else {
			if (!TextUtils.isEmpty(profileData.getLetter())) {
				profileImage.setImageResource(R.drawable.profile_letter_circle_background);
				profileLetterNoImage.setVisibility(View.VISIBLE);
				profileLetterNoImage.setText(profileData.getLetter());
			} else {
				profileLetterNoImage.setVisibility(View.GONE);
				profileImage.setImageResource(R.drawable.placeholder_contact);
			}
		}
	}

	public void setMenuFragmentInteractionListener(InteractionListener listener) {
		this.listener = listener;
	}

	public interface InteractionListener {
		void onProfileClick();

		void onCashbackClick();
	}

}
