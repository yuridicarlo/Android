package it.bancomat.pay.consumer.extended.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent;
import it.bancomat.pay.consumer.extended.flowmanager.HomeFlowManagerExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.activities.HomeActivity;
import it.bancomatpay.sdkui.fragment.home.HomeCameraFragment;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.LogoBankSingleton;

import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationEvent.START;
import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationEvent.STOP;
import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationPage.B2P_CASHBACK;
import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationPage.P2B_ECOMMERCE;
import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationPage.P2B_PAYMENT;
import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationPage.P2B_QR;
import static it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent.EAnimationPage.P2P_PAYMENT;

public class HomeCameraFragmentExtended extends HomeCameraFragment {

	private static final String TAG = HomeCameraFragmentExtended.class.getSimpleName();

	private ServicesPagerAdapterExtended adapterExtended;
	private Thread threadLogo;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		toolbarHome.setLeftImageVisibility(false);
		toolbarHome.setOnClickLeftImageListener(null);

		adapterPagerServices = new ServicesPagerAdapterExtended(getChildFragmentManager(), getLifecycle());
		viewpager.setAdapter(adapterPagerServices);

		ViewPager2.OnPageChangeCallback homeTutorialPageChangeCallbackExtended = new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				pageIndicatorView.setSelection(position);

				if (adapterPagerServices instanceof ServicesPagerAdapterExtended) {
					adapterExtended = (ServicesPagerAdapterExtended) adapterPagerServices;

					if (isP2PPaymentPageAvailable && isP2BPaymentPageAvailable && isP2BPaymentQRPageAvailable) { //Both P2P and P2B

						switch (position) {
							case 0:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2P_PAYMENT));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("BplayCashbackSection");
								break;
							case 1:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2P_PAYMENT));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("P2PPaymentSection");
								break;
							case 2:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2P_PAYMENT));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("P2BQrSection");
								break;
							case 3:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2P_PAYMENT));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("P2BEcommerceSection");
								break;
							default:
								break;
						}

					} else if (!isP2PPaymentPageAvailable) { //Only P2B

						switch (position) {
							case 0:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("BplayCashbackSection");
								break;
							case 1:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("P2BQrSection");
								break;
							case 2:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_QR));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_ECOMMERCE));
								sendCustomerJourneyTagSlideTour("P2BEcommerceSection");
								break;
							default:
								break;
						}

					} else if (!isP2BPaymentPageAvailable && !isP2BPaymentQRPageAvailable) { //Only P2P

						switch (position){
							case 0:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2P_PAYMENT));
								sendCustomerJourneyTagSlideTour("BplayCashbackSection");
								break;
							case 1:
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, B2P_CASHBACK));
								EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2P_PAYMENT));
								sendCustomerJourneyTagSlideTour("P2PPaymentSection");
								break;
						}

					}

				}
			}
		};
		viewpager.unregisterOnPageChangeCallback(homeTutorialPageChangeCallback);
		viewpager.registerOnPageChangeCallback(homeTutorialPageChangeCallbackExtended);

		handlerUi.postDelayed(() -> homeTutorialPageChangeCallbackExtended.onPageSelected(0), 1500);

		if (!FullStackSdkDataManager.getInstance().isHomePanelExpanded()) {
			viewpager.setVisibility(View.INVISIBLE);
			int timeout = layoutPlafond.getVisibility() == View.VISIBLE ? 300 : 1500;
			handlerUi.postDelayed(() -> AnimationFadeUtil.startFadeInAnimationV1(viewpager, 200), timeout);
		}

		String bankUuid = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();
		if (!TextUtils.isEmpty(bankUuid)) {
			DataBank dataBank = DataBankManager.getDataBank(bankUuid);
			if (dataBank != null) {

				if (LogoBankSingleton.getInstance().getLogoBank() == null) {
					threadLogo = new Thread(() -> {
						try {
							Bitmap bitmapLogoSearch = Picasso.get().load(dataBank.getLogoSearch())
									.placeholder(R.drawable.empty)
									.networkPolicy(NetworkPolicy.NO_STORE)
									.memoryPolicy(MemoryPolicy.NO_STORE)
									.networkPolicy(NetworkPolicy.NO_STORE)
									.get();


							Drawable drawableLogoSearch = new BitmapDrawable(PayCore.getAppContext().getResources(), bitmapLogoSearch);
							LogoBankSingleton.getInstance().setLogoBank(drawableLogoSearch);
							CustomLogger.d(TAG, "Bank logo loaded successfully");

						} catch (IOException e) {
							CustomLogger.e(TAG, "Bank logo not loaded: " + e.getMessage());
						}
					});
					threadLogo.start();
				}

				if (!TextUtils.isEmpty(dataBank.getLogoHome())) {
					Picasso.get().load(dataBank.getLogoHome())
							.placeholder(R.drawable.empty)
							.networkPolicy(NetworkPolicy.NO_STORE)
							.memoryPolicy(MemoryPolicy.NO_STORE)
							.networkPolicy(NetworkPolicy.NO_STORE)
							.into(toolbarHome.getCenterLeftImageReference(), new Callback() {
								@Override
								public void onSuccess() {
									toolbarHome.setCenterLeftImageVisibility(View.VISIBLE);
								}

								@Override
								public void onError(Exception e) {
									toolbarHome.setCenterLeftImageVisibility(View.GONE);
								}
							});
				}

			}

		}

		buttonPayInStores.setOnClickListener(new CustomOnClickListener(v -> {
			if(getActivity() instanceof HomeActivity){
				if(((HomeActivity) getActivity()).checkSectionNavigable()){
					HomeFlowManagerExtended.goToMerchantList(requireActivity());
				}
			}
		}));
	}

	@Override
	public void onDestroyView() {
		handlerUi.removeCallbacksAndMessages(null);
		if (threadLogo != null) threadLogo.interrupt();
		super.onDestroyView();
	}

	private class ServicesPagerAdapterExtended extends FragmentStateAdapter {

		public ServicesPagerAdapterExtended(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
			super(fragmentManager, lifecycle);
		}

		@NonNull
		@Override
		public Fragment createFragment(int position) {
			Fragment fragment = new Fragment();

			if (isP2PPaymentPageAvailable && isP2BPaymentPageAvailable && isP2BPaymentQRPageAvailable) { //Both P2P and P2B

				switch (position) {
					case 0:
						fragment = new HomeB2PCashbackFragmentExtended();
						break;
					case 1:
						fragment = new HomeP2PPaymentFragmentExtended();
						break;
					case 2:
						fragment = new HomeP2BQrFragmentExtended();
						break;
					case 3:
						fragment = new HomeP2BEcommerceFragmentExtended();
						break;
					default:
						break;
				}

			} else if (!isP2PPaymentPageAvailable) { //Only P2B

				switch (position) {
					case 0:
						fragment = new HomeB2PCashbackFragmentExtended();
						break;
					case 1:
						fragment = new HomeP2BQrFragmentExtended();
						break;
					case 2:
						fragment = new HomeP2BEcommerceFragmentExtended();
						break;
					default:
						break;
				}

			} else if (!isP2BPaymentPageAvailable && !isP2BPaymentQRPageAvailable) { //Only P2P

				switch (position){
					case 0:
						fragment = new HomeB2PCashbackFragmentExtended();
						break;
					case 1:
						fragment = new HomeP2PPaymentFragmentExtended();
						break;
				}

			}

			return fragment;
		}

		@Override
		public int getItemCount() {
			return NUM_PAGES;
		}
	}

	@Override
	protected void clickHomeFabButton() {
		super.clickHomeFabButton();

		if (adapterExtended != null) {

			if (isP2PPaymentPageAvailable && isP2BPaymentPageAvailable && isP2BPaymentQRPageAvailable) { //Both P2P and P2B

				if (expandableLayout.isExpanded()) {
					switch (viewpager.getCurrentItem()) {
						case 0:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2P_PAYMENT));
							break;
						case 1:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_QR));
							break;
						case 2:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_ECOMMERCE));
							break;
					}
				} else {
					switch (viewpager.getCurrentItem()) {
						case 0:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2P_PAYMENT));
							break;
						case 1:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_QR));
							break;
						case 2:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
							break;
					}
				}

			} else if (!isP2PPaymentPageAvailable) { //Only P2B

				if (expandableLayout.isExpanded()) {
					switch (viewpager.getCurrentItem()) {
						case 0:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2P_PAYMENT));
							break;
						case 1:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2B_ECOMMERCE));
							break;
					}
				} else {
					switch (viewpager.getCurrentItem()) {
						case 0:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2P_PAYMENT));
							break;
						case 1:
							EventBus.getDefault().post(new HomeCarouselAnimationEvent(STOP, P2B_ECOMMERCE));
							break;
					}
				}

			} else if (!isP2BPaymentPageAvailable && !isP2BPaymentQRPageAvailable) { //Only P2P

				if (viewpager.getCurrentItem() == 0) {
					EventBus.getDefault().post(new HomeCarouselAnimationEvent(START, P2P_PAYMENT));
				}

			}

		}
	}

}
