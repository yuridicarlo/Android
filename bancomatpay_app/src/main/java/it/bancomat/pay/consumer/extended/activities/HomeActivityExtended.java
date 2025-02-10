package it.bancomat.pay.consumer.extended.activities;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.bancomat.pay.consumer.biometric.EnrollBiometricActivity;
import it.bancomat.pay.consumer.cj.CjConsentsActivity;
import it.bancomat.pay.consumer.extended.flowmanager.HomeFlowManagerExtended;
import it.bancomat.pay.consumer.home.HomeFragmentRestyle;
import it.bancomat.pay.consumer.init.InitActivity;
import it.bancomat.pay.consumer.login.LoginFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.notification.MyFirebaseMessagingService;
import it.bancomat.pay.consumer.notification.Push;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.DeepLink;
import it.bancomat.pay.consumer.viewmodel.HomeViewModel;
import it.bancomat.pay.consumer.viewmodel.StoreLocatorViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyConsents;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.CustomProgressDialogFragment;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.activities.HomeActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.ShopCategoryConsumer;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjConstants;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.widgets.BlueCustomDialog;
import it.bancomatpay.sdkui.widgets.BplayBalloonWidget;

import static it.bancomat.pay.consumer.extended.BancomatFullStackSdkExtended.PROCESS_PUSH_EXTRA;
import static it.bancomatpay.sdk.manager.utilities.Constants.QR_CODE_BASE_URL;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.NO_ACTIVE_IBAN;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_FLOW_RETURN_TO_INTRO;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW_LOST_PIN;
import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.CASHBACK_SHOW_INFO_DIALOG;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_BPLAY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_PAYMENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_SERVICES;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class HomeActivityExtended extends HomeActivity implements HomeFragmentRestyle.InteractionListener, BplayBalloonWidget.OnCloseButtonInteractionListener, BarcodeCallback {

    private HomeViewModel homeViewModel;
    private StoreLocatorViewModel storeLocatorViewModel;
    private static final int REQUEST_POPUP_BPLAY = 13;
    private static final String TAG = HomeActivityExtended.class.getSimpleName();
    private static long CJ_TIME_TO_WAIT = TimeUnit.DAYS.toMillis(3);
    private static final int REQUEST_CODE_SET_PASSWORD = 1212;
    private static final int POSITION_HOME = 0;
    private static final int POSITION_SERVICES = 1;
    private static final int POSITION_CAMERA = 2;
    private static final int POSITION_BPLAY = 4;
    private static final int POSITION_STORE_LOCATOR = 3;

    protected int FRAGMENT_TAG_CAMERA;
    protected int FRAGMENT_TAG_HOME;
    protected int FRAGMENT_TAG_SERVICES;
    protected int FRAGMENT_TAG_BPLAY;
    protected int FRAGMENT_TAG_STORES;


    private HomeSectionEvent previousSection = new HomeSectionEvent(HomeSectionEvent.Type.CAMERA);

    private int lastTabPosition = 0;

    protected Handler handlerUi;

    TabLayout tabNavigation;
    ImageView cameraButton;
    ImageView imageStatusBarBackground;
    View homeMainLayout;
    View starGifLayout;
    BplayBalloonWidget bplayBalloon;

    private BottomSheetBehavior sheetBehavior;

    private CaptureManager capture;
    private DecoratedBarcodeView qrView;


    ActivityResultLauncher<Intent> activityResultLauncherLostPin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SET_PASSWORD, result.getResultCode(), data);
            });

    ActivityResultLauncher<String> activityPushNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            MyFirebaseMessagingService.registerCurrentToken(this);
        }
        // checkBPlayDialog();
    });

    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bcm_home_new);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        storeLocatorViewModel = new ViewModelProvider(this).get(StoreLocatorViewModel.class);
        homeViewModel.restoreSaveInstanceState(savedInstanceState);
        storeLocatorViewModel.restoreSaveInstanceState(savedInstanceState);

        tabNavigation = findViewById(R.id.tab_navigation);
        cameraButton = findViewById(R.id.camera_button);
        imageStatusBarBackground = findViewById(R.id.image_status_bar_background);
        homeMainLayout = findViewById(R.id.home_main_layout);
        starGifLayout = findViewById(R.id.star_layout);
        bplayBalloon = findViewById(R.id.bplay_balloon);

        CoordinatorLayout coordinatorLayout = findViewById(R.id.bottom_camera_layout_container);
        sheetBehavior = BottomSheetBehavior.from(coordinatorLayout);

        handlerUi = new Handler();

        if (getIntent().getBooleanExtra(FINISH_SDK_FLOW_LOST_PIN, false)) {
            LoginFlowManager.goToLostPin(this, true);
            finish();
        } else if (getIntent().getBooleanExtra(FINISH_FLOW_RETURN_TO_INTRO, false)) {
            Intent intentIntro = new Intent(this, InitActivity.class);
            intentIntro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentIntro);
            finish();
        } else {
            checkBiometricConfigured();
        }

        processPush = getIntent().getBooleanExtra(PROCESS_PUSH_EXTRA, false);


        initNewHome();

        retrieveStoreCategories();

        configureCameraBottomSheet(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initNewHome() {

        initFragments();

        hideStatusBarBackground();


        //bottomNavigationView.setItemIconTintList(null);
        //bottomNavigationView.setSelectedItemId(R.id.action_camera);
        tabNavigation.clearOnTabSelectedListeners();
        tabNavigation.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (NavHelper.getCurrentDestinationId(HomeActivityExtended.this) != -1) {

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(it.bancomatpay.sdkui.R.anim.fade_in, it.bancomatpay.sdkui.R.anim.fade_out, it.bancomatpay.sdkui.R.anim.fade_in, it.bancomatpay.sdkui.R.anim.fade_out);

                    if (position == POSITION_HOME) {
                        previousSection = new HomeSectionEvent(HomeSectionEvent.Type.HOME);
                        clearLightStatusBar(homeMainLayout, it.bancomatpay.sdkui.R.color.blue_status_bar);
                        if (NavHelper.getCurrentDestinationId(HomeActivityExtended.this) != FRAGMENT_TAG_HOME) {
                            NavHelper.replaceFragment(HomeActivityExtended.this, FRAGMENT_TAG_HOME);

                            hideStatusBarBackground();
                            //BancomatPayApiInterface.Factory.getInstance().doGetHomePageDetails();
                            sendCjTagEventLocation(mLastLocation);
                            if (EventBus.getDefault().getStickyEvent(HomeSectionEvent.class) == null) {
                                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                        HomeActivityExtended.this, KEY_HOME_PAYMENT, null, false);
                            } else {
                                EventBus.getDefault().removeStickyEvent(HomeSectionEvent.class);
                            }
                        }
                    } else if (position == POSITION_SERVICES) {
                        if(checkSectionNavigable()) {
                            setLightStatusBar(homeMainLayout, it.bancomatpay.sdkui.R.color.white_background);
                            if (NavHelper.getCurrentDestinationId(HomeActivityExtended.this) != FRAGMENT_TAG_SERVICES) {
                                NavHelper.replaceFragment(HomeActivityExtended.this, FRAGMENT_TAG_SERVICES);
                                AnimationFadeUtil.startFadeInAnimationV1(imageStatusBarBackground, AnimationFadeUtil.DEFAULT_DURATION);
                                if (EventBus.getDefault().getStickyEvent(HomeSectionEvent.class) == null) {
                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                            HomeActivityExtended.this, KEY_HOME_SERVICES, null, false);
                                } else {
                                    EventBus.getDefault().removeStickyEvent(HomeSectionEvent.class);
                                }
                            }
                        } else {
                            onMessageEvent(previousSection);
                        }
                    } else if (position == POSITION_CAMERA) {
                        if(checkSectionNavigable()) {
                            homeViewModel.toggleCameraSheetExpanded();
                        }else {
                            onMessageEvent(previousSection);
                        }
                    } else if (position == POSITION_BPLAY) {
                        if(checkSectionNavigable()) {
                            setLightStatusBar(homeMainLayout, it.bancomatpay.sdkui.R.color.white_background);
                            if (NavHelper.getCurrentDestinationId(HomeActivityExtended.this) != FRAGMENT_TAG_BPLAY) {
                                NavHelper.replaceFragment(HomeActivityExtended.this, FRAGMENT_TAG_BPLAY);
                                goToBplayService();
                                showStatusBarBackground();
                                if (EventBus.getDefault().getStickyEvent(HomeSectionEvent.class) == null) {
                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                            HomeActivityExtended.this, KEY_HOME_BPLAY, null, false);
                                } else {
                                    EventBus.getDefault().removeStickyEvent(HomeSectionEvent.class);
                                }

                            }
                        } else {
                            onMessageEvent(previousSection);
                        }
                    } else if (position == POSITION_STORE_LOCATOR) {
                        if (checkSectionNavigable()) {
                            previousSection = new HomeSectionEvent(HomeSectionEvent.Type.STORES);
                            if (NavHelper.getCurrentDestinationId(HomeActivityExtended.this) != FRAGMENT_TAG_STORES) {
                                if (storeLocatorViewModel.isReloadRequired()) {
                                    retrieveStoreCategories();
                                }
                                NavHelper.replaceFragment(HomeActivityExtended.this, FRAGMENT_TAG_STORES);
                                hideStatusBarBackground();
                                if (EventBus.getDefault().getStickyEvent(HomeSectionEvent.class) == null) {
                                    CjUtils.getInstance().startStoreLocatorFlow();
                                } else {
                                    EventBus.getDefault().removeStickyEvent(HomeSectionEvent.class);
                                }

                            }
                        } else {
                            onMessageEvent(previousSection);
                        }
                    }

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 2) {
                    cameraButton.setImageResource(it.bancomatpay.sdkui.R.drawable.nav_paga);
                    homeViewModel.setCameraSheetExpanded(false);

                } else {
                    lastTabPosition = tab.getPosition();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //
            }
        });

        for (int i = 0; i < tabNavigation.getTabCount(); i++) {
            TabLayout.Tab tab = tabNavigation.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }

        //disable click on middle tab
        LinearLayout tabStrip = ((LinearLayout) tabNavigation.getChildAt(0));
        tabStrip.getChildAt(2).setOnTouchListener((v, event) -> true);

        cameraButton.setOnClickListener((v) -> {
            TabLayout.Tab tabItem = tabNavigation.getTabAt(2);

            if (tabItem != null) {
                if(tabItem.isSelected()) {
                    cameraButton.setImageResource(it.bancomatpay.sdkui.R.drawable.nav_paga);
                    tabItem = tabNavigation.getTabAt(lastTabPosition);
                    tabItem.select();
                } else {
                    cameraButton.setImageResource(it.bancomatpay.sdkui.R.drawable.nav_paga_chiudi);
                    tabItem.select();
                }
            }
        });

//        showStarGifLayout();

    }

    private void configureCameraBottomSheet(Bundle savedInstanceState) {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    homeViewModel.setCameraSheetExpanded(true);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    homeViewModel.setCameraSheetExpanded(false);
                    TabLayout.Tab tabItem = tabNavigation.getTabAt(2);

                    if (tabItem != null) {
                        if(tabItem.isSelected()) {
                            cameraButton.setImageResource(it.bancomatpay.sdkui.R.drawable.nav_paga);
                            tabItem = tabNavigation.getTabAt(lastTabPosition);
                            tabItem.select();
                        }
                    }
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        qrView = findViewById(it.bancomatpay.sdkui.R.id.merchant_zxing_barcode_scanner);
        qrView.setStatusText("");
        qrView.getViewFinder().setVisibility(View.GONE);

        if (hasPermissionCamera()) {
            capture = new CaptureManager(this, qrView);
            Intent intent = this.getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
            intent.setAction(Intents.Scan.ACTION);
            capture.initializeFromIntent(intent, savedInstanceState);
            this.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            capture.decode();
        }

        homeViewModel.isCameraSheetExpanded().observe(this, isExpanded -> {
            if (isExpanded) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                handlerUi.postDelayed(() -> {
                    resumeCamera();
                },100);

            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                handlerUi.postDelayed(() -> {
                    pauseCamera();
                },500);
            }
        });
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        String data = result.toString();
        if (!TextUtils.isEmpty(data)) {
            if (BancomatFullStackSdk.getInstance().isQrCodeLinkPayment(data)) {
                data = data.substring(data.lastIndexOf(QR_CODE_BASE_URL) + QR_CODE_BASE_URL.length());
            }

            PaymentFlowManager.goToQrPaymentData(this, data, false);
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //Non usato
    }

    private void resumeCamera() {
        if (!hasPermissionCamera()) {
            requestPermissionCamera();
        } else if (capture != null) {
            capture.onResume();
            if (qrView != null) {
                qrView.resume();
                qrView.decodeSingle(this);
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

    private boolean hasPermissionCamera() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionCamera() {
        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)) {
            PermissionFlowManager.goToCameraDisclosure(this, activityResultLauncherCamera);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
    }

    private void showStarGifLayout() {
        if (!FullStackSdkDataManager.getInstance().isBplayStarGifShown()) {
            AnimationFadeUtil.startFadeInAnimationV1(starGifLayout, DEFAULT_DURATION);
        } else {
            AnimationFadeUtil.startFadeOutAnimationV1(starGifLayout, DEFAULT_DURATION, View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(HomeSectionEvent event) {
        tabNavigation.post(() -> {
            int position = POSITION_HOME;
            switch (event.getType()){
                case STORES:
                    position = POSITION_STORE_LOCATOR;
                    break;
                case HOME:
                    position = POSITION_HOME;
                    break;
                case CAMERA:
                    position = POSITION_CAMERA;
                    break;
                case SERVICES:
                    position = POSITION_SERVICES;
                    break;
                case BPLAY:
                    position = POSITION_BPLAY;
                    break;
            }

            TabLayout.Tab tabItem = tabNavigation.getTabAt(position);
            dismissProgressDialogBplay();
            if (tabItem != null) {
                tabItem.select();
            }
        });
    }

    private View getTabView(int position) {
        View view = null;
        ImageView img;
        TextView label;
        switch (position) {
            case POSITION_HOME:
                view = View.inflate(this, it.bancomatpay.sdkui.R.layout.bottom_navigation_custom_tab, null);
                img = view.findViewById(it.bancomatpay.sdkui.R.id.image_tab_item);
                img.setImageResource(it.bancomatpay.sdkui.R.drawable.home_selector);

                label = view.findViewById(it.bancomatpay.sdkui.R.id.label_tab_item);
                label.setText(it.bancomatpay.sdkui.R.string.home_navigation_tab_label_home);
                break;
            case POSITION_SERVICES:
                view = View.inflate(this, it.bancomatpay.sdkui.R.layout.bottom_navigation_custom_tab, null);
                img = view.findViewById(it.bancomatpay.sdkui.R.id.image_tab_item);
                img.setImageResource(it.bancomatpay.sdkui.R.drawable.home_services_selector);

                label = view.findViewById(it.bancomatpay.sdkui.R.id.label_tab_item);
                label.setText(it.bancomatpay.sdkui.R.string.home_navigation_tab_label_services);
                break;
            case POSITION_CAMERA:
                view = View.inflate(this, it.bancomatpay.sdkui.R.layout.bottom_navigation_custom_tab_zoomed, null);


                label = view.findViewById(it.bancomatpay.sdkui.R.id.label_tab_item);
                label.setText(it.bancomatpay.sdkui.R.string.home_navigation_tab_label_camera);
                break;
            case POSITION_BPLAY:
                view = View.inflate(this, it.bancomatpay.sdkui.R.layout.bottom_navigation_custom_tab_zoomed_2x, null);
                img = view.findViewById(it.bancomatpay.sdkui.R.id.image_tab_item);
                img.setImageResource(it.bancomatpay.sdkui.R.drawable.home_bplay_selector);

                label = view.findViewById(it.bancomatpay.sdkui.R.id.label_tab_item);
                label.setText(it.bancomatpay.sdkui.R.string.home_navigation_tab_label_bplay);
                break;
            case POSITION_STORE_LOCATOR:
                view = View.inflate(this, it.bancomatpay.sdkui.R.layout.bottom_navigation_custom_tab, null);
                img = view.findViewById(it.bancomatpay.sdkui.R.id.image_tab_item);
                img.setImageResource(it.bancomatpay.sdkui.R.drawable.home_store_selector);

                label = view.findViewById(it.bancomatpay.sdkui.R.id.label_tab_item);
                label.setText(it.bancomatpay.sdkui.R.string.home_navigation_tab_label_stores);
                break;
        }
        return view;
    }

    public void retrieveStoreCategories() {
        BancomatPayApiInterface.Factory.getInstance().doGetStoreLocatorCategories(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    ArrayList<ShopCategoryConsumer> shopCategories = new ArrayList<>();
                    for(ShopCategory shopCategory :result.getResult()) {
                        shopCategories.add(new ShopCategoryConsumer(shopCategory));
                    }
                    storeLocatorViewModel.setShopCategories(shopCategories);
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, SessionManager.getInstance().getSessionToken());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StoreLocatorViewModel.REQUEST_CHECK_SETTINGS || requestCode == 0) {
            if (resultCode == -1) {
                storeLocatorViewModel.setIsGpsResultPositive(true);
            } else {
                storeLocatorViewModel.setIsGpsResultPositive(false);
            }
        }
    }

    private void hideStatusBarBackground() {
        if (imageStatusBarBackground.getVisibility() == View.VISIBLE) {
            AnimationFadeUtil.startFadeOutAnimationV1(imageStatusBarBackground, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        }
    }

    private void showStatusBarBackground() {
        if (imageStatusBarBackground.getVisibility() != View.VISIBLE) {
            AnimationFadeUtil.startFadeInAnimationV1(imageStatusBarBackground, AnimationFadeUtil.DEFAULT_DURATION);
        }
    }

    @Override
    public boolean checkSectionNavigable() {
        return checkBiometricConfigured();
    }

    private boolean checkBiometricConfigured(){
        if(!BancomatPayApiInterface.Factory.getInstance().isBiometricConfigured()){
            if(BancomatPayApiInterface.Factory.getInstance().isDeviceSecured()){
                showMigrationNeed();
            }else {
                showDeviceNotSecured();
            }
            return false;
        }
        return true;
    }


    private void showMigrationNeed(){
        AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
        builder.setTitle(R.string.migration_need_dialog_title)
                .setMessage(R.string.migration_need_dialog_description)
                .setPositiveButton(R.string.migration_need_dialog_confirm, (dialog, id) -> {
                    Intent intentIntro = new Intent(this, EnrollBiometricActivity.class);
                    intentIntro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentIntro);
                    finish();
                })
                .setCancelable(false);
        builder.showDialog(this);

    }




    private void showDeviceNotSecured(){
        AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
        builder.setTitle(R.string.device_not_secured_dialog_title)
                .setMessage(R.string.device_not_secured_dialog_description)
                .setPositiveButton(R.string.device_not_secured_dialog_confirm, (dialog, id) -> {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                 //   startActivityForResult(intent, REQUEST_CODE_SET_PASSWORD);
                    try {
                        activityResultLauncherLostPin.launch(intent);
                    }catch (ActivityNotFoundException e){}

                    dialog.dismiss();
                })
                .setNegativeButton(R.string.device_not_secured_dialog_cancel, (dialog, id) -> {
                    dialog.dismiss();
                })
                .setCancelable(false);
        builder.showDialog(this);
    }

    @Override
    public void goToProfile() {
        HomeFlowManagerExtended.goToProfile(HomeActivityExtended.this);
    }

    @Override
    public void saveCjConsents(CustomerJourneyConsents cjConsents) {
        BancomatPayApiInterface.Factory.getInstance().setProfilingConsent(cjConsents.isProfiling());
        BancomatPayApiInterface.Factory.getInstance().setMarketingConsent(cjConsents.isMarketing());
        BancomatPayApiInterface.Factory.getInstance().setDataToThirdPartiesConsent(cjConsents.isDataToThirdParties());
    }

    @Override
    public void showCustomerJourneyConsents() {
        if(BancomatPayApiInterface.Factory.getInstance().showCjConsentsinHome()){
            //CASO PRIMA ATTIVAZIONE
            if(TextUtils.isEmpty(AppBancomatDataManager.getInstance().getCjConsentsTimestampForKo())){
                Intent intent = new Intent(this, CjConsentsActivity.class);
                startActivity(intent);
            }else if (!TextUtils.isEmpty(AppBancomatDataManager.getInstance().getCjConsentsTimestampForKo())) {

                long currentTimestamp = System.currentTimeMillis();
                long savedTimestamp = Long.parseLong(AppBancomatDataManager.getInstance().getCjConsentsTimestampForKo());

                if ((currentTimestamp - savedTimestamp) >= CJ_TIME_TO_WAIT) {
                    Intent intent = new Intent(this, CjConsentsActivity.class);
                    startActivity(intent);
                }
            }
            BancomatPayApiInterface.Factory.getInstance().setShowCjConsentsinHome(false);
        }
    }

    protected void initFragments() {
         FRAGMENT_TAG_HOME = R.id.homeFragmentRestyle;
         FRAGMENT_TAG_SERVICES = R.id.servicesFragment;
         FRAGMENT_TAG_BPLAY = R.id.bplayFragment;
         FRAGMENT_TAG_STORES = R.id.storeLocatorListFragment;


        if (!hasPermissionPush()) {
            requestPermissionPush();
        }
        // else {
        //     checkBPlayDialog();
        // }

    }

    private void checkBPlayDialog() {
        if(BancomatPayApiInterface.Factory.getInstance().isShowBPlayPopup()){

            BlueCustomDialog dialog = BlueCustomDialog.newInstance(REQUEST_POPUP_BPLAY,
                    getString(it.bancomatpay.sdkui.R.string.bplay_info_popup_title),
                    getString(it.bancomatpay.sdkui.R.string.bplay_info_popup_message),
                    getString(it.bancomatpay.sdkui.R.string.bplay_info_popup_first_button),
                    getString(it.bancomatpay.sdkui.R.string.bplay_info_popup_second_button));
            dialog.show(getSupportFragmentManager(), "");

        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeepLink event) {
        EventBus.getDefault().removeStickyEvent(event);
        PaymentFlowManager.goToQrPaymentData(this, event.getQrDataId(), false);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Push event) {
        CustomLogger.d(TAG, "onMessageEvent " + event);

        if (BancomatPayApiInterface.Factory.getInstance().isBiometricConfigured()) {
            EventBus.getDefault().removeStickyEvent(event);
            processPush = false;
            if (event.getType() == Push.Type.PAYMENT_REQUEST || event.getType() == Push.Type.BANKID || event.getType() == Push.Type.DIRECT_DEBIT) {

                Intent i = new Intent(this, PaymentPushLoadingActivity.class);
                i.putExtra(PaymentPushLoadingActivity.PUSH_DATA, event);
                startActivity(i);

            } else if (event.getType() == Push.Type.PAYMENTS_HISTORY) {

                completePushOperation();
                HomeFlowManager.goToTransactions(this);

            } /*else if (event.isShowDialog()) {

            //TODO NFC Non ancora gestito

            //mostriamo il mNfcProgress facendo la super
            super.onMessageEvent(event);

        }*/ else {
                CustomLogger.d(TAG, "Biometric not Configured ");
                completePushOperation();
            }
        }
    }

    @Override
    public void goToHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked, boolean returnToIntro) {
        Intent i = new Intent(activity, HomeActivityExtended.class);

        i.putExtra(CASHBACK_SHOW_INFO_DIALOG, false);

        boolean finishActivity = false;
        if (finishSdkFlow) {
            i.putExtra(FINISH_SDK_FLOW, true);
        } else if (pinBlocked) {
            i.putExtra(FINISH_SDK_FLOW_LOST_PIN, true);
        } else {
            finishActivity = true;
        }

        if (returnToIntro) {
            i.putExtra(FINISH_FLOW_RETURN_TO_INTRO, true);
        }

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(i);
        if (finishActivity) {
            activity.finish();
        }
    }

    private void callGetUserData() {
        BancomatSdkInterface.Factory.getInstance().getUserData(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    UserData userData = result.getResult();
                    homeViewModel.setUserData(userData);

                    if (userData.getCustomerJourneyConsents() == null && Constants.CUSTOMER_JOURNEY_ENABLED) {
                        showCustomerJourneyConsents();
                    } else {
                        saveCjConsents(userData.getCustomerJourneyConsents());
                    }

                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else if (result.getStatusCode() == NO_ACTIVE_IBAN) {
                    showNoMoreIbanActiveError();
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, SessionManager.getInstance().getSessionToken());
    }

    protected void completePushOperation() {
        callGetUserData();
    }

    @Override
    public void onCashbackClick() {
        verifyBPayTermsAndConditionsAccepted();
    }

    protected void manageProfile() {
        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
        if (ApplicationModel.getInstance().getUserData() != null) {
            // HomeFlowManager.goToProfile(this);
            goToProfile();
        } else {
            sdk.getUserData(this, result -> {
                if (progressLoading != null && progressLoading.isVisible()) {
                    progressLoading.dismissAllowingStateLoss();
                    if (result != null) {
                        if (result.isSuccess()) {
                            //HomeFlowManager.goToProfile(this);
                            goToProfile();
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else if (result.getStatusCode() == NO_ACTIVE_IBAN) {
                            showNoMoreIbanActiveError();
                        } else {
                            showError(result.getStatusCode());
                        }
                    } else {
                        showError(GENERIC_ERROR);
                    }

                } else if (result != null && result.getResult() != null) {
                    homeViewModel.setUserData(result.getResult());
                }
            }, SessionManager.getInstance().getSessionToken());

            progressLoading = new CustomProgressDialogFragment();
            progressLoading.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CAMERA && false) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            } else {
                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_PERMISSION, "Camera");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
            }
        } /*else if (requestCode == PERMISSION_CONTACTS_FRAGMENT) {
            if (fragmentContacts != null && fragmentContacts.isResumed()) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragmentContacts.contactList(true);
                } else {
                    fragmentContacts.stopRefreshing();
                    fragmentContacts.changeStyle(true);

                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_PERMISSION, "Contacts");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                }
            }
        }*/

    }

    private void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_SET_PASSWORD){
            checkBiometricConfigured();
        }
    }

    @Override
    public void onProfileClick() {
        manageProfile();
    }

    public void showBplayBalloon() {
        boolean isFirstInstall = isFirstInstall(this);
        int timeToAccessHome = FullStackSdkDataManager.getInstance().getTimeToAccessInHome();
        if (!FullStackSdkDataManager.getInstance().isBplayBalloonAlreadyShown() && ((timeToAccessHome >= 1) || !isFirstInstall)) {
            Animation balloonEnter = AnimationUtils.loadAnimation(this, it.bancomatpay.sdkui.R.anim.balloon_enter_animation);
            bplayBalloon.setBalloonListener(this);
            AnimationFadeUtil.startFadeInAnimationV1(bplayBalloon, View.VISIBLE);
            bplayBalloon.startAnimation(balloonEnter);
        } else if (FullStackSdkDataManager.getInstance().isBplayBalloonAlreadyShown()) {
            AnimationFadeUtil.startFadeOutAnimationV1(bplayBalloon, DEFAULT_DURATION, View.GONE);
            FullStackSdkDataManager.getInstance().deleteTimeToAccessInHome();
        }
    }

    @Override
    public void closeBalloonButtonInteractionListener() {
        FullStackSdkDataManager.getInstance().putBplayBalloonAlreadyShown(true);
        AnimationFadeUtil.startFadeOutAnimationV1(bplayBalloon, DEFAULT_DURATION, View.GONE);
    }

    @Override
    public void onFirstButtonClicked(int requestCode) {
        if(requestCode == REQUEST_POPUP_BPLAY){

        }else {
            //managed from HomeActivity
            super.onFirstButtonClicked(requestCode);
        }
    }

    @Override
    public void onSecondButtonClicked(int requestCode) {
        if(requestCode == REQUEST_POPUP_BPLAY){
            EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.BPLAY));
        }else {
            //managed from HomeActivity
            super.onSecondButtonClicked(requestCode);
        }
    }

    private boolean hasPermissionPush(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        return false;
    }

    private void requestPermissionPush() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activityPushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        // else {
        //     checkBPlayDialog();
        // }
    }

    @Override
    public void onBackPressed() {
        if(!NavHelper.popBackStack(this))
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        showStarGifLayout();
        if (storeLocatorViewModel.isReloadRequired()) {
            retrieveStoreCategories();
        }

        if (!processPush) {
            callGetUserData();
        }

        if(Boolean.TRUE.equals(homeViewModel.isCameraSheetExpanded().getValue())) {
            resumeCamera();
        }
    }

    @Override
    public void onPause() {
        pauseCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerUi.removeCallbacksAndMessages(null);
        if (capture != null) {
            capture.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        homeViewModel.onSaveInstanceState(outState);
        storeLocatorViewModel.onSaveInstanceState(outState);
        if (capture != null) {
            capture.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }
}
