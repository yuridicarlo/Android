package it.bancomatpay.sdkui.fragment.home;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.rd.PageIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.events.request.BankServicesUpdateEvent;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.HomeActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.fragment.HomeB2PCashbackFragment;
import it.bancomatpay.sdkui.fragment.HomeP2BEcommerceFragment;
import it.bancomatpay.sdkui.fragment.HomeP2BPaymentFragment;
import it.bancomatpay.sdkui.fragment.HomeP2BQrFragment;
import it.bancomatpay.sdkui.fragment.HomeP2PPaymentFragment;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.ToolbarHome;

import static it.bancomatpay.sdk.manager.utilities.Constants.QR_CODE_BASE_URL;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2B_POS_WITHDRAWAL_NOT_ACTIVATED_BY_BANK;
import static it.bancomatpay.sdkui.activities.HomeActivity.REQUEST_PERMISSION_CAMERA;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CLOSE_CAMERA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_TOUR;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_OPEN_CAMERA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_QR_CODE_SCAN;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_SLIDE_TO;

public class HomeCameraFragment extends GenericErrorFragment implements BarcodeCallback {

    private CaptureManager capture;
    private DecoratedBarcodeView qrView;

    private boolean isPlafondVisible;
    private boolean isFirstBalanceLoad = true;

    protected int NUM_PAGES;

    protected boolean isP2PPaymentPageAvailable = false;
    protected boolean isP2BPaymentPageAvailable = false;
    protected boolean isP2BPaymentQRPageAvailable = false;

    protected FragmentStateAdapter adapterPagerServices;
    protected ViewPager2.OnPageChangeCallback homeTutorialPageChangeCallback;

    protected Handler handlerUi;

    protected ToolbarHome toolbarHome;
    protected PageIndicatorView pageIndicatorView;
    protected ViewPager2 viewpager;
    FloatingActionButton homeFabButton;
    protected ExpandableLayout expandableLayout;
    ImageView homeImageBackground;
    TextView textQrCodeLabel;
    protected MaterialButton buttonPayInStores;
    ImageView imageEye;
    TextView textPlafond;
    TextView textPlafondHidden;
    protected View layoutPlafond;
    protected View containerCamera;

    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_camera, container, false);

        toolbarHome = view.findViewById(R.id.toolbar_home);
        pageIndicatorView = view.findViewById(R.id.page_indicator_view);
        viewpager = view.findViewById(R.id.viewpager);
        homeFabButton = view.findViewById(R.id.home_fab_button);
        expandableLayout = view.findViewById(R.id.expandable_layout);
        homeImageBackground = view.findViewById(R.id.home_image_background);
        textQrCodeLabel = view.findViewById(R.id.text_qr_code_label);
        buttonPayInStores = view.findViewById(R.id.button_pay_in_stores);
        imageEye = view.findViewById(R.id.image_eye);
        textPlafond = view.findViewById(R.id.text_plafond);
        textPlafondHidden = view.findViewById(R.id.text_plafond_hidden);
        layoutPlafond = view.findViewById(R.id.layout_plafond_value);
        containerCamera = view.findViewById(R.id.container_camera);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qrView = initializeContent(view);

        handlerUi = new Handler();

        toolbarHome.setOnClickLeftImageListener(v ->
                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                        .goToHome(requireActivity(), true, false, false)
        );

        pageIndicatorView.setAutoVisibility(false);

        initBankServices();
        if (adapterPagerServices == null) {
            adapterPagerServices = new ServicesPagerAdapter(getChildFragmentManager(), getLifecycle());
        }
        viewpager.setAdapter(adapterPagerServices);
        viewpager.setOffscreenPageLimit(NUM_PAGES);

        homeTutorialPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                pageIndicatorView.setSelection(position);

                if (isP2PPaymentPageAvailable && isP2BPaymentPageAvailable && isP2BPaymentQRPageAvailable) { //Both P2P and P2B

                    switch (position) {
                        case 0:
                            sendCustomerJourneyTagSlideTour("BplayCashbackSection");
                            break;
                        case 1:
                            sendCustomerJourneyTagSlideTour("P2PPaymentSection");
                            break;
                        case 2:
                            sendCustomerJourneyTagSlideTour("P2BQrSection");
                            break;
                        case 3:
                            sendCustomerJourneyTagSlideTour("P2BEcommerceSection");
                            break;
                        default:
                            break;
                    }

                } else if (!isP2PPaymentPageAvailable) { //Only P2B

                    switch (position) {
                        case 0:
                            sendCustomerJourneyTagSlideTour("BplayCashbackSection");
                            break;
                        case 1:
                            sendCustomerJourneyTagSlideTour("P2BQrSection");
                            break;
                        case 2:
                            sendCustomerJourneyTagSlideTour("P2BEcommerceSection");
                            break;
                        default:
                            break;
                    }

                } else if (!isP2BPaymentPageAvailable && !isP2BPaymentQRPageAvailable) { //Only P2P

                    switch (position) {
                        case 0:
                            sendCustomerJourneyTagSlideTour("BplayCashbackSection");
                            break;
                        case 1:
                            sendCustomerJourneyTagSlideTour("P2PPaymentSection");
                            break;
                    }
                }
            }
        };

        viewpager.registerOnPageChangeCallback(homeTutorialPageChangeCallback);
        pageIndicatorView.setVisibility(View.INVISIBLE);

        homeFabButton.setOnClickListener(new CustomOnClickListener(v -> {
            if(getActivity() instanceof HomeActivity){
                if(((HomeActivity) getActivity()).checkSectionNavigable()){
                    clickHomeFabButton();
                }
            }
        }));

        expandableLayout.setOnExpansionUpdateListener((expansionFraction, state) -> {

            switch (state) {

                case ExpandableLayout.State.EXPANDING:
                case ExpandableLayout.State.COLLAPSING:
                    homeFabButton.setEnabled(false);
                    break;

                case ExpandableLayout.State.COLLAPSED:

                    handlerUi.postDelayed(() -> homeFabButton.setEnabled(true), 500);
                    AnimationFadeUtil.startFadeOutAnimationV2(homeImageBackground, DEFAULT_DURATION, View.INVISIBLE);
                    AnimationFadeUtil.startFadeInAnimationV2(textQrCodeLabel, DEFAULT_DURATION);
                    break;

                case ExpandableLayout.State.EXPANDED:

                    handlerUi.postDelayed(() -> homeFabButton.setEnabled(true), 500);
                    AnimationFadeUtil.startFadeInAnimationV2(pageIndicatorView, DEFAULT_DURATION);
                    if (viewpager.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV2(viewpager, DEFAULT_DURATION);
                    }
                    AnimationFadeUtil.startFadeOutAnimationV2(textQrCodeLabel, DEFAULT_DURATION, View.INVISIBLE);
                    AnimationFadeUtil.startFadeInAnimationV2(
                            homeImageBackground, DEFAULT_DURATION, () ->
                                    handlerUi.postDelayed(() -> {
                                        if (capture != null) {
                                            capture.onPause();
                                        }
                                    }, 500));

                    break;
            }

        });

        boolean isPanelExpanded = FullStackSdkDataManager.getInstance().isHomePanelExpanded();
        if (isPanelExpanded) {
            containerCamera.setVisibility(View.VISIBLE);
            expandableLayout.collapse(false);
            pageIndicatorView.setVisibility(View.INVISIBLE);
            homeImageBackground.setVisibility(View.INVISIBLE);
            homeFabButton.setImageResource(R.drawable.qrcode_closed_icon);
        } else {
            containerCamera.setVisibility(View.GONE);
            expandableLayout.expand(false);
            pageIndicatorView.setVisibility(View.VISIBLE);
            homeImageBackground.setVisibility(View.VISIBLE);
            homeFabButton.setImageResource(R.drawable.qrcode_fotocamera);

            if (viewpager.getAdapter() != null) {
                if (viewpager.getAdapter().getItemCount() <= 1) {
                    pageIndicatorView.setVisibility(View.INVISIBLE);
                } else {
                    pageIndicatorView.setVisibility(View.VISIBLE);
                }
            }
        }


        imageEye.setOnClickListener(new CustomOnClickListener(v -> {
            isPlafondVisible = !isPlafondVisible;
            FullStackSdkDataManager.getInstance().putShowBalance(isPlafondVisible);
            setPlafondText(isPlafondVisible);
        }));

        //   showBplayBalloon();

        if (hasPermissionCamera()) {
            capture = new CaptureManager(requireActivity(), qrView);
            Intent intent = requireActivity().getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
            intent.setAction(Intents.Scan.ACTION);
            capture.initializeFromIntent(intent, savedInstanceState);
            requireActivity().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            capture.decode();
        }
    }

    protected void sendCustomerJourneyTagSlideTour(String tourSection) {
        if (expandableLayout.isExpanded()) {
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_SLIDE_TO, tourSection);

            CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_HOME_TOUR, mapEventParams, false);
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
    public void onMessageEvent(BankServicesUpdateEvent event) {
        initBankServices();
        adapterPagerServices.notifyDataSetChanged();
    }

    /**
     * Da chiamare prima di istanziare l'adapterRecyclerServices del viewpager
     */
    private void initBankServices() {

        BankServices bankServices = BancomatDataManager.getInstance().getBankBankActiveServices();
        List<BankServices.EBankService> servicesList = new ArrayList<>();

        if (bankServices != null) {
            for (BankServices.EBankService service : bankServices.getBankServiceList()) {
                if (service != null) {
                    servicesList.add(BankServices.EBankService.valueOf(service.getServiceName()));
                }
            }
        }

        NUM_PAGES = 0;
        isP2PPaymentPageAvailable = false;
        isP2BPaymentPageAvailable = false;
        isP2BPaymentQRPageAvailable = false;

        if (servicesList.contains(BankServices.EBankService.P2P) && servicesList.contains(BankServices.EBankService.P2B)) {
            NUM_PAGES += 4;

            isP2PPaymentPageAvailable = true;
            isP2BPaymentPageAvailable = true;
            isP2BPaymentQRPageAvailable = true;
        } else if (servicesList.contains(BankServices.EBankService.P2P)) {
            NUM_PAGES++;
            isP2PPaymentPageAvailable = true;
        } else if (servicesList.contains(BankServices.EBankService.P2B)) {
            NUM_PAGES += 3;
            isP2BPaymentPageAvailable = true;
            isP2BPaymentQRPageAvailable = true;
        }

        pageIndicatorView.setCount(NUM_PAGES);
        if (expandableLayout.isExpanded() && NUM_PAGES > 1) {
            pageIndicatorView.setVisibility(View.VISIBLE);
        } else {
            pageIndicatorView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handlerUi.removeCallbacksAndMessages(null);
        if (capture != null) {
            capture.onDestroy();
        }
    }

    public void setToolbarMarginTop(int marginTop) {
        RelativeLayout.LayoutParams spaceParams = (RelativeLayout.LayoutParams) toolbarHome.getLayoutParams();
        spaceParams.topMargin = marginTop;
        toolbarHome.setLayoutParams(spaceParams);
    }

    protected DecoratedBarcodeView initializeContent(View view) {
        qrView = view.findViewById(R.id.merchant_zxing_barcode_scanner);
        qrView.setStatusText("");
        qrView.getViewFinder().setVisibility(View.GONE);
        return qrView;
    }

    protected void clickHomeFabButton() {
        if (expandableLayout.getState() == ExpandableLayout.State.EXPANDED
                || expandableLayout.getState() == ExpandableLayout.State.COLLAPSED) {

            if (expandableLayout.isExpanded()) {

                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        requireContext(), KEY_OPEN_CAMERA, null, false);

                if (!hasPermissionCamera()) {
                    requestPermissionCamera();
                } else if (capture != null) {
                    capture.onResume();
                    if (qrView != null) {
                        qrView.resume();
                        qrView.decodeSingle(this);
                    }
                }
                containerCamera.setVisibility(View.VISIBLE);
                homeFabButton.setImageResource(R.drawable.qrcode_closed_icon);

                FullStackSdkDataManager.getInstance().putHomePanelExpanded(true);

                handlerUi.postDelayed(() -> {
                    AnimationFadeUtil.startFadeOutAnimationV2(
                            pageIndicatorView, DEFAULT_DURATION, View.INVISIBLE);
                    AnimationFadeUtil.startFadeOutAnimationV2(
                            viewpager, DEFAULT_DURATION, View.INVISIBLE,
                            () -> expandableLayout.collapse());
                }, 100);

            } else {

                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        requireContext(), KEY_CLOSE_CAMERA, null, false);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    expandableLayout.expand(false);
                } else {
                    expandableLayout.expand();
                }
                handlerUi.postDelayed(() -> {
                            containerCamera.setVisibility(View.GONE);
                        }, 1000);
                homeFabButton.setImageResource(R.drawable.qrcode_fotocamera);

                pauseCamera();

                FullStackSdkDataManager.getInstance().putHomePanelExpanded(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPlafondVisible = FullStackSdkDataManager.getInstance().getShowBalance();
        if (isPlafondVisible) {
            imageEye.setImageResource(R.drawable.eye_show);
        } else {
            imageEye.setImageResource(R.drawable.eye_hide);
        }

        if (capture != null) {
            capture.onResume();
        }
        if (qrView != null) {
            qrView.resume();
            qrView.decodeSingle(this);
        }
    }

    private void setPlafondText(boolean isPlafondVisible) {
        String amountBalance = StringUtils.getFormattedValue(ApplicationModel.getInstance().getUserData().getBalance());
        textPlafond.setText(amountBalance);
        textPlafondHidden.setText("××××××××");

        if (isPlafondVisible) {

            imageEye.setImageResource(R.drawable.eye_show);

            if (textPlafondHidden.getVisibility() == View.VISIBLE || isFirstBalanceLoad) {
                textPlafond.setVisibility(View.VISIBLE);
                textPlafondHidden.setVisibility(View.INVISIBLE);
            }

        } else {
            imageEye.setImageResource(R.drawable.eye_hide);

            if (textPlafond.getVisibility() == View.VISIBLE || isFirstBalanceLoad) {
                textPlafond.setVisibility(View.INVISIBLE);
                textPlafondHidden.setVisibility(View.VISIBLE);
            }
        }
    }

    private void pauseCamera() {
        if (capture != null) {
            capture.onPause();
        }
        if (qrView != null) {
            qrView.pause();
        }
    }

    @Override
    public void onPause() {
        pauseCamera();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(requireActivity());

        if (capture != null) {
            capture.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (capture != null) {
            capture.onSaveInstanceState(outState);
        }
    }

    public void initCameraFromPermissionResult() {
        if (capture == null) {
            capture = new CaptureManager(requireActivity(), qrView);
        }
        Intent intent = requireActivity().getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
        intent.setAction(Intents.Scan.ACTION);
        capture.initializeFromIntent(intent, null);
        requireActivity().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (qrView != null) {
            //qrView.resume();
            qrView.decodeSingle(this);
        }
    }

    public void closeCameraUi() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            expandableLayout.expand(false);
        } else {
            expandableLayout.expand();
        }
        homeFabButton.setImageResource(R.drawable.qrcode_fotocamera);
        containerCamera.setVisibility(View.GONE);
    }

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return qrView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
	}*/

    @Override
    public void barcodeResult(BarcodeResult result) {
        if (expandableLayout.getState() == ExpandableLayout.State.COLLAPSED || expandableLayout.getState() == ExpandableLayout.State.COLLAPSING) {
            String data = result.toString();
            if (!TextUtils.isEmpty(data)) {
                if (BancomatFullStackSdk.getInstance().isQrCodeLinkPayment(data)) {
                    data = data.substring(data.lastIndexOf(QR_CODE_BASE_URL) + QR_CODE_BASE_URL.length());
                }

                PaymentFlowManager.goToQrPaymentData(requireActivity(), data, false);


            }
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //Non usato
    }


    public void manageUserData(UserData userData) {

        if (userData != null) {

            String amountBalance = StringUtils.getFormattedValue(userData.getBalance());
            if (TextUtils.isEmpty(amountBalance)) {
                textPlafond.setText("");
                textPlafondHidden.setText("××××××××");
                textPlafondHidden.setVisibility(View.VISIBLE);
                FullStackSdkDataManager.getInstance().putShowBalance(false);
            } else {
                isPlafondVisible = FullStackSdkDataManager.getInstance().getShowBalance();
                setPlafondText(isPlafondVisible);
            }

            if (layoutPlafond.getVisibility() != View.VISIBLE) {
                AnimationFadeUtil.startFadeInAnimationV1(layoutPlafond, DEFAULT_DURATION);
            } else {
                layoutPlafond.setVisibility(View.VISIBLE);
            }

        }

    }

    private class ServicesPagerAdapter extends FragmentStateAdapter {

        public ServicesPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = new Fragment();

            if (isP2PPaymentPageAvailable && isP2BPaymentPageAvailable && isP2BPaymentQRPageAvailable) { //Both P2P and P2B

                switch (position) {
                    case 0:
                        fragment = new HomeB2PCashbackFragment();
                        break;
                    case 1:
                        fragment = new HomeP2PPaymentFragment();
                        break;
                    case 2:
                        fragment = new HomeP2BQrFragment();
                        break;
                    case 3:
                        fragment = new HomeP2BEcommerceFragment();
                        break;
                    default:
                        break;
                }

            } else if (!isP2PPaymentPageAvailable) { //Only P2B

                switch (position) {
                    case 0:
                        fragment = new HomeB2PCashbackFragment();
                        break;
                    case 1:
                        fragment = new HomeP2BQrFragment();
                        break;
                    case 2:
                        fragment = new HomeP2BEcommerceFragment();
                        break;
                    default:
                        break;
                }

            } else if (!isP2BPaymentPageAvailable && !isP2BPaymentQRPageAvailable) { //Only P2P
                switch (position) {
                    case 0:
                        fragment = new HomeB2PCashbackFragment();
                        break;
                    case 1:
                        fragment = new HomeP2PPaymentFragment();
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

    public void setFirstBalanceLoad(boolean isFirstLoad) {
        isFirstBalanceLoad = isFirstLoad;
    }

    private boolean hasPermissionCamera() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionCamera() {
        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)) {
            PermissionFlowManager.goToCameraDisclosure(requireActivity(), activityResultLauncherCamera);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
    }

}
