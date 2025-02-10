package it.bancomat.pay.consumer.activation.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.activation.fragment.CaptureQrCodeFragment;
import it.bancomat.pay.consumer.activation.fragment.ChooseActivationModeFragment;
import it.bancomat.pay.consumer.activation.fragment.ChooseValidBankFragment;
import it.bancomat.pay.consumer.activation.fragment.InsertManualActivationCodeFragment;
import it.bancomat.pay.consumer.activation.fragment.InsertOtpFragment;
import it.bancomat.pay.consumer.activation.fragment.InsertPhoneFragment;
import it.bancomat.pay.consumer.activation.fragment.InsertPinFragment;
import it.bancomat.pay.consumer.activation.fragment.MultiIbanFragment;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.ActivationData;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityActivationBinding;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.task.model.BankDataMultiIban;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumberManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_CODE;
import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_FROM_DEEP_LINK;
import static it.bancomat.pay.consumer.activation.ActivationFlowManager.BANK_DATA;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CAMERA_CONSENT;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXT_SERVER_EXPIRED_OTP;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXT_SERVER_MAX_OTP_NUMBER_REACHED;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXT_SERVER_WRONG_OTP;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_INSERT_CODE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_READ_QR;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ELAPSED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class ActivationActivity extends AppGenericErrorActivity
        implements InsertPhoneFragment.InteractionListener, InsertOtpFragment.InteractionListener, ChooseActivationModeFragment.InteractionListener,
        CaptureQrCodeFragment.InteractionListener, InsertManualActivationCodeFragment.InteractionListener, InsertPinFragment.InteractionListener,
        MultiIbanFragment.InteractionListener, ChooseValidBankFragment.InteractionListener {

    private ActivityActivationBinding binding;

    public static final String TAG_INSERT_PHONE = "TAG_INSERT_PHONE";
    public static final String TAG_INSERT_OTP = "TAG_INSERT_OTP";
    public static final String TAG_MULTI_IBAN = "TAG_MULTI_IBAN";
    public static final String TAG_CHOOSE_VALID_BANK = "TAG_CHOOSE_VALID_BANK";
    public static final String TAG_CHOOSE_ACTIVATION_MODE = "TAG_CHOOSE_ACTIVATION_MODE";
    public static final String TAG_INSERT_ACTIVATION_CODE = "TAG_INSERT_ACTIVATION_CODE";
    public static final String TAG_INSERT_PIN = "TAG_INSERT_PIN";
    public static final String TAG_CAPTURE_QR_CODE = "TAG_CAPTURE_QR_CODE";

    private static final String TAG = ActivableBankListActivity.class.getSimpleName();

    private DataBank dataBank;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 122;

    private ActivationData activationData;

    private String activationToken;

    private String firstPin;
    private boolean hasError;
    private String bankLabel;

    private boolean isUncorrectBankSelected = false;

    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                manageResult(REQUEST_CODE_SHOW_CAMERA_CONSENT);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        activationData = new ActivationData();

        dataBank = (DataBank) getIntent().getSerializableExtra(BANK_DATA);

        //Inizializzazione lista prefissi sulle preference
        FullStackSdkDataManager.getInstance().putPrefixList(DataPrefixPhoneNumberManager.getFullDataList());

        if (getIntent().getBooleanExtra(ACTIVATION_FROM_DEEP_LINK, false)) {

            binding.progressBar1.setProgress(binding.progressBar1.getMax());
            binding.progressBar2.setProgress(binding.progressBar2.getMax());
            binding.progressBar3.setProgress(binding.progressBar3.getMax());
            String activationCode = getIntent().getStringExtra(ACTIVATION_CODE);
            sendActivationCode(activationCode);

        } else {

            if (savedInstanceState == null) {
                Fragment newFragment = new InsertPhoneFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_fragment, newFragment, TAG_INSERT_PHONE);
                transaction.commit();
            }

            setProgressNextStep(binding.progressBar1);
        }
    }

    @Override
    public void onPhoneInserted(String phoneFormatted, String phone) {
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doInitUser(result -> {
            if (result != null) {
                if (result.isSuccess()) {

                    activationData.setPhoneNumber(phone);
               //     activationData.setPhonePrefix(AppBancomatDataManager.getInstance().getPrefixCountryCode());
                    activationData.setPhonePrefix(FullStackSdkDataManager.getInstance().getPrefixCountryCode());

                    AppBancomatDataManager.getInstance().putActivationPhoneNumber(phone);

                    Fragment newFragment = InsertOtpFragment.newInstance(phoneFormatted, dataBank.getBankUUID());

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                    transaction.replace(R.id.container_fragment, newFragment, TAG_INSERT_OTP);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, phoneFormatted);
        addTask(t);
    }

    @Override
    public void onOtpInserted(String phone, String otp) {
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doVerifyOtp(result -> {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_OTP);

            if (result != null) {
                if (result.isSuccess()) {

                    if (result.getResult().getBankDataList().isEmpty()) {
                        bankLabel = dataBank.getLabel();
                        ActivationFlowManager.goToNoBankAvailable(this, bankLabel);
                    } else {
                        activationToken = result.getResult().getToken();

                        ArrayList<BankDataMultiIban> bankDatas = result.getResult().getBankDataList();
                        ArrayList<DataBank> dataBanks = DataBankManager.getUserDataBankListMultiIban(bankDatas);

                        HashMap<String, DataBank> mapBanks = new HashMap<>();
                        for (DataBank item : dataBanks) {
                            mapBanks.put(item.getBankUUID(), item);
                        }

                        if (mapBanks.containsKey(dataBank.getBankUUID())) {
                            if (mapBanks.get(dataBank.getBankUUID()).isMultiIban()) {

                                Fragment newFragment = MultiIbanFragment.newInstance(mapBanks.get(dataBank.getBankUUID()));

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                                transaction.replace(R.id.container_fragment, newFragment, TAG_MULTI_IBAN);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            } else {

                                Fragment newFragment = new ChooseActivationModeFragment();

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                                transaction.replace(R.id.container_fragment, newFragment, TAG_CHOOSE_ACTIVATION_MODE);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        } else {

                            isUncorrectBankSelected = true;

                            Fragment newFragment = ChooseValidBankFragment.newInstance(dataBanks, dataBank.getLabel());

                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                            transaction.replace(R.id.container_fragment, newFragment, TAG_CHOOSE_VALID_BANK);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                    }
                } else {
                    if (fragment instanceof InsertOtpFragment) {
                        if (result.getStatusCode() == EXT_SERVER_WRONG_OTP) {
                            ((InsertOtpFragment) fragment).onError(getString(R.string.activation_insert_otp_fragment_error_wrong));
                        } else if (result.getStatusCode() == EXT_SERVER_EXPIRED_OTP) {
                            ((InsertOtpFragment) fragment).onError(getString(R.string.activation_insert_otp_fragment_error_expired));
                        } else if (result.getStatusCode() == EXT_SERVER_MAX_OTP_NUMBER_REACHED) {
                            ((InsertOtpFragment) fragment).onError(getString(R.string.activation_insert_otp_fragment_error_max_otp));
                        } else {
                            showError(result.getStatusCode());
                        }
                    }

                }
            }
        }, otp);

        addTask(t);

    }

    @Override
    public void onResendOtp(String phoneFormatted) {
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doInitUser(result -> {
            if (result != null && !result.isSuccess()) {
                showError(result.getStatusCode());
            }
        }, phoneFormatted);
        addTask(t);
    }

    @Override
    public void openQrCode() {
        String bankUuidChoosed = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_ACTIVATION_READ_QR, null, true);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            CustomLogger.d(TAG, "doUserMonitoringTask success");
                        } else {
                            CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                        }
                    }
                },
                bankUuidChoosed,
                UserMonitoringConstants.ACTIVATION_TAG,
                UserMonitoringConstants.ACTIVATION_THROUGH_QRCODE,
                "");
        addTask(t);
        showPermission();
    }

    @Override
    public void sendHomeBankingCode(String code, String bankUuid) {
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doVerifyActivationCode(result -> {
            if (result != null) {
                if (result.isSuccess()) {

                    if (result.getResult().getBankUUID() != null) {
                        BancomatPayApiInterface.Factory.getInstance().storeBankUuidChoosed(result.getResult().getBankUUID());
                    }

                    activationToken = result.getResult().getToken();

                    Fragment newFragment = new InsertPinFragment();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                    transaction.replace(R.id.container_fragment, newFragment, TAG_INSERT_PIN);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    setProgressNextStep(binding.progressBar2);
                    setProgressNextStep(binding.progressBar3);
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, code, bankUuid);
        addTask(t);
    }

    @Override
    public void insertHomeBanking() {
        String bankUuidChoosed = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();

        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_ACTIVATION_INSERT_CODE, null, true);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            CustomLogger.d(TAG, "doUserMonitoringTask success");
                        } else {
                            CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                        }
                    }
                },
                bankUuidChoosed,
                UserMonitoringConstants.ACTIVATION_TAG,
                UserMonitoringConstants.ACTIVATION_TROUGH_CODE,
                "");
        addTask(t);

        Fragment newFragment = InsertManualActivationCodeFragment.newInstance(bankUuidChoosed);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
        transaction.replace(R.id.container_fragment, newFragment, TAG_INSERT_ACTIVATION_CODE);
        transaction.addToBackStack(null);
        transaction.commit();
        setProgressNextStep(binding.progressBar2);
    }

    @Override
    public void verifyQrCode(String data, String bankUuid) {
        if (!TextUtils.isEmpty(data)) {
            Result<String> activationCode = BancomatPayApiInterface.Factory.getInstance().getActivationCode(data, activationToken);
            if (activationCode.isSuccess()) {
                callActivationCode(activationCode.getResult(), bankUuid);
            } else {
                showError(activationCode.getStatusCode());
                Fragment fragmentCaptureQrCode = getSupportFragmentManager().findFragmentByTag(TAG_CAPTURE_QR_CODE);
                if (fragmentCaptureQrCode instanceof CaptureQrCodeFragment) {
                    new Handler().post(((CaptureQrCodeFragment) fragmentCaptureQrCode)::resumeCapture);
                }
            }
        }
    }

    private void callActivationCode(String activationCode, String bankUuid) {
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doVerifyActivationCode(result -> {
            if (result != null) {
                if (result.isSuccess()) {

                    if (!TextUtils.isEmpty(result.getResult().getBankUUID())) {
                        BancomatPayApiInterface.Factory.getInstance().storeBankUuidChoosed(result.getResult().getBankUUID());
                    }

                    activationToken = result.getResult().getToken();

                    Fragment newFragment = new InsertPinFragment();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                    transaction.replace(R.id.container_fragment, newFragment, TAG_INSERT_PIN);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    setProgressNextStep(binding.progressBar3);
                } else {
                    showError(result.getStatusCode());
                    Fragment fragmentCaptureQrCode = getSupportFragmentManager().findFragmentByTag(TAG_CAPTURE_QR_CODE);
                    if (fragmentCaptureQrCode instanceof CaptureQrCodeFragment) {
                        new Handler().post(((CaptureQrCodeFragment) fragmentCaptureQrCode)::resumeCapture);
                    }
                }
            } else {
                Fragment fragmentCaptureQrCode = getSupportFragmentManager().findFragmentByTag(TAG_CAPTURE_QR_CODE);
                if (fragmentCaptureQrCode instanceof CaptureQrCodeFragment) {
                    new Handler().post(((CaptureQrCodeFragment) fragmentCaptureQrCode)::resumeCapture);
                }
            }
        }, activationCode, bankUuid);
        addTask(t);
    }

    @Override
    public void onPinInserted(String pin) {

        if (TextUtils.isEmpty(firstPin)) {

            if (BancomatPayApiInterface.Factory.getInstance().isValidPin(pin)) {
                firstPin = pin;
                Fragment fragmentInsertPin = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_PIN);
                if (fragmentInsertPin instanceof InsertPinFragment) {
                    ((InsertPinFragment) fragmentInsertPin).setStepRepeatPin();
                }
            } else {
                Fragment fragmentInsertPin = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_PIN);
                if (fragmentInsertPin instanceof InsertPinFragment) {
                    ((InsertPinFragment) fragmentInsertPin).setErrorInvalidPin();
                }
                hasError = true;
            }

        } else {

            if (firstPin.equals(pin)) {

                LoaderHelper.showLoader(this);
                Task<?> t = BancomatPayApiInterface.Factory.getInstance().doSetPin(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            activationData.setAbiCode(result.getResult().getAbiCode());
                            activationData.setGroupCode(result.getResult().getGroupCode());

                            BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                                    .initBancomatSDKWithCheckRoot(this,
                                            activationData.getAbiCode(), activationData.getGroupCode(), activationData.getPhoneNumber(),
                                            activationData.getPhonePrefix(), activationData.getIban(),
                                            AppBancomatDataManager.getInstance().getTokens().getOauth(), result2 -> {
                                                if (result2 != null) {
                                                    if (result2.isSuccess()) {
                                                        HashMap<String, String> mapEventParams = new HashMap<>();
                                                        mapEventParams.put(PARAM_ELAPSED, CjUtils.getInstance().getActivationTimeElapsed());
                                                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_ACTIVATION_COMPLETED, mapEventParams, true);
                                                        ActivationFlowManager.goToActivationCompleted(this, activationData, dataBank.getBankUUID());
                                                    } else if (result2.isSessionExpired()) {
                                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                                    } else {
                                                        ActivationFlowManager.goToActivationCompletedSdkError(this, activationData, dataBank.getBankUUID());
                                                    }
                                                } else {
                                                    ActivationFlowManager.goToActivationCompletedSdkError(this, activationData, dataBank.getBankUUID());
                                                }
                                            });

                        } else {
                            showError(result.getStatusCode());
                            Fragment fragmentInsertPin = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_PIN);
                            if (fragmentInsertPin instanceof InsertPinFragment) {
                                ((InsertPinFragment) fragmentInsertPin).resetKeyboard();
                            }
                        }
                    } else {
                        Fragment fragmentInsertPin = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_PIN);
                        if (fragmentInsertPin instanceof InsertPinFragment) {
                            ((InsertPinFragment) fragmentInsertPin).resetKeyboard();
                        }
                    }
                }, pin, activationToken, activationData.getIban());
                addTask(t);

            } else {

                Fragment fragmentInsertPin = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_PIN);
                if (fragmentInsertPin instanceof InsertPinFragment) {
                    ((InsertPinFragment) fragmentInsertPin).setErrorRepeatPin();
                }
                hasError = true;

            }

        }
    }

    @Override
    public void onStartEditing() {
        if (hasError) {
            firstPin = null;
            hasError = false;
            Fragment fragmentInsertPin = getSupportFragmentManager().findFragmentByTag(TAG_INSERT_PIN);
            if (fragmentInsertPin instanceof InsertPinFragment) {
                ((InsertPinFragment) fragmentInsertPin).resetError();
            }
        }
    }

    private void setProgressNextStep(ProgressBar progressBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new Handler().postDelayed(() -> progressBar.setProgress(binding.progressBar1.getMax(), true),
                    getResources().getInteger(R.integer.fragment_animation_duration));
        } else {
            new Handler().postDelayed(() ->
                    new Thread(() -> {
                        while (progressBar.getProgress() <= progressBar.getMax()) {
                            progressBar.setProgress(progressBar.getProgress() + 5);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }).start(), getResources().getInteger(R.integer.fragment_animation_duration));
        }
    }


    private void setProgressPreviousStep(ProgressBar progressBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            new Handler().postDelayed(() -> progressBar.setProgress(0, true),
                    getResources().getInteger(R.integer.fragment_animation_duration));

        } else {
            new Handler().postDelayed(() ->
                    new Thread(() -> {
                        while (progressBar.getProgress() > 0) {
                            progressBar.setProgress(progressBar.getProgress() - 5);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }).start(), getResources().getInteger(R.integer.fragment_animation_duration));
        }
    }

    @Override
    public void onBankIbanSelected(String iban) {
        activationData.setIban(iban);
    }

    @Override
    public void onProceedSelectIbanButtonClicked(DataBank dataBank) {
        if (!TextUtils.isEmpty(activationData.getIban())) {
            Fragment newFragment = new ChooseActivationModeFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
            transaction.replace(R.id.container_fragment, newFragment, TAG_CHOOSE_ACTIVATION_MODE);
            transaction.addToBackStack(null);
            transaction.commit();
            setProgressNextStep(binding.progressBar2);
        }
    }

    private void showPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            goToQrCaptureFragment();
        } else {
            if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)) {
                PermissionFlowManager.goToCameraDisclosure(this, activityResultLauncherCamera);
            } else {
                goToQrCaptureFragment();
            }
        }
    }

    protected void manageResult(int requestCode) {
        if (requestCode == REQUEST_CODE_SHOW_CAMERA_CONSENT) {
            goToQrCaptureFragment();
        }
    }


    private void goToQrCaptureFragment() {
        String bankUuidChoosed = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();
        Fragment newFragment = CaptureQrCodeFragment.newInstance(bankUuidChoosed);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
        transaction.replace(R.id.container_fragment, newFragment, TAG_CAPTURE_QR_CODE);
        transaction.addToBackStack(null);
        //segnalazione crash store, il chiamante del metodo è request permission result di Android
        transaction.commitAllowingStateLoss();
        setProgressNextStep(binding.progressBar2);
    }

    private void sendActivationCode(String activationCode) {
        String bankUuidChoosed = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doVerifyActivationCode(result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    if (result.getResult().getBankUUID() != null) {
                        BancomatPayApiInterface.Factory.getInstance().storeBankUuidChoosed(result.getResult().getBankUUID());
                    }

                    activationToken = result.getResult().getToken();

                 //   activationData.setPhonePrefix(AppBancomatDataManager.getInstance().getPrefixCountryCode());
                    activationData.setPhonePrefix(FullStackSdkDataManager.getInstance().getPrefixCountryCode());
                    activationData.setPhoneNumber(AppBancomatDataManager.getInstance().getActivationPhoneNumber());

                    Fragment newFragment = new InsertPinFragment();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                    transaction.replace(R.id.container_fragment, newFragment, TAG_INSERT_PIN);
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {
                    showError(result.getStatusCode());
                }
            }
        }, activationCode, bankUuidChoosed);
        addTask(t);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG_CAPTURE_QR_CODE);
                    if (currentFragment instanceof CaptureQrCodeFragment) {
                        ((CaptureQrCodeFragment) currentFragment).initCamera();
                    }
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_PERMISSION, "Camera");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);

                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        //user checked never ask me again
                        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG_CAPTURE_QR_CODE);
                        if (currentFragment instanceof CaptureQrCodeFragment) {
                            ((CaptureQrCodeFragment) currentFragment).initCamera();
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onBankClicked(DataBank dataBank) {

        BancomatPayApiInterface.Factory.getInstance().storeBankUuidChoosed(dataBank.getBankUUID());

        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            CustomLogger.d(TAG, "doUserMonitoringTask success");
                        } else {
                            CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                        }
                    }
                },
                dataBank.getBankUUID(),
                UserMonitoringConstants.ACTIVATION_TAG,
                UserMonitoringConstants.ACTIVATION_BANK_SELECTED,
                "");
        addTask(t);

        if (TextUtils.isEmpty(dataBank.getLinkStore())) {

            if (dataBank.isMultiIban()) {

                Fragment newFragment = MultiIbanFragment.newInstance(dataBank);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                transaction.replace(R.id.container_fragment, newFragment, TAG_MULTI_IBAN);
                transaction.addToBackStack(null);
                transaction.commit();

            } else {

                Fragment newFragment = new ChooseActivationModeFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_to_right, R.anim.slide_from_left);
                transaction.replace(R.id.container_fragment, newFragment, TAG_CHOOSE_ACTIVATION_MODE);
                transaction.addToBackStack(null);
                transaction.commit();

            }

        } else {
            ActivationFlowManager.goToPlayStore(this, dataBank);
        }

    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container_fragment);
        if (currentFragment instanceof InsertPhoneFragment) {
            finish();
        } else if (currentFragment instanceof MultiIbanFragment || currentFragment instanceof ChooseActivationModeFragment) {
            if (currentFragment instanceof ChooseActivationModeFragment) {
                activationData.setIban(null);
            }
            if (isUncorrectBankSelected) {
                setProgressPreviousStep(binding.progressBar2);
                super.onBackPressed();
            } else {
                showReturnToIntroDialog();
            }
        } else if (currentFragment instanceof InsertOtpFragment) {
            setProgressPreviousStep(binding.progressBar2);
            super.onBackPressed();
        } else if (currentFragment instanceof ChooseValidBankFragment) {
            setProgressPreviousStep(binding.progressBar2);
            showReturnToIntroDialog();
        } else if (currentFragment instanceof CaptureQrCodeFragment || currentFragment instanceof InsertManualActivationCodeFragment) {
            super.onBackPressed();
        } else if (currentFragment instanceof InsertPinFragment) {
            showReturnToIntroDialog();
        }
    }

    private void showReturnToIntroDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.warning_title))
                .setCancelable(false)
                .setMessage(getString(R.string.cancel_registration))
                .setPositiveButton(getString(R.string.ok), (dialog, which) ->
                        ActivationFlowManager.goToIntro(this))
                .setNegativeButton(getString(android.R.string.cancel), null);
        builder.show();
    }

}
