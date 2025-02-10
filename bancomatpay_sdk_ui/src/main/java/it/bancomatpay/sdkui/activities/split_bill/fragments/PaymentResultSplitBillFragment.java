package it.bancomatpay.sdkui.activities.split_bill.fragments;

import static android.app.Activity.RESULT_OK;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.MULTIMEDIA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;


import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.FragmentPaymentResultSplitBillBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;

public class PaymentResultSplitBillFragment extends GenericErrorFragment {
    private static final String TAG = PaymentResultSplitBillFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSION_STORAGE = 6000;
    private FragmentPaymentResultSplitBillBinding binding;

    private SplitBillViewModel splitBillViewModel;

    ActivityResultLauncher<Intent> activityResultLauncherMultimedia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT,result.getResultCode(),data);
            });


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentResultSplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splitBillViewModel = new ViewModelProvider(requireActivity()).get(SplitBillViewModel.class);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> NavHelper.popBackStack(requireActivity()));

        initTextSwitcher();

        configureUI();

        processPayment();

    }

    private void configureUI() {
        binding.causal.setText(splitBillViewModel.getCausal());
        binding.description.setText(splitBillViewModel.getDescription());
        binding.textAmount.setText(splitBillViewModel.getAmountFormatted());

        binding.loadingImgBackground.setVisibility(View.VISIBLE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setRightImageVisibility(false);
        binding.toolbarSimple.setOnClickRightImageListener((v) -> {
            if (hasPermissionStorage() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                shareAction();
            } else {
                requestPermissionStorage();
            }
        });
    }


    private void processPayment() {
           BancomatSdkInterface.Factory.getInstance().doSplitBill(requireActivity(),
                onPaymentResult(),
                splitBillViewModel.getSelectedContactsListGroup(),
                String.valueOf(splitBillViewModel.getAmountCents()),
                splitBillViewModel.getCausal(), splitBillViewModel.getDescription(),
                SessionManager.getInstance().getSessionToken());
    }

    OnCompleteResultListener<PaymentRequestData> onPaymentResult() {
        return result -> {

            if (result != null) {
                if (result.isSuccess()) {
                    Log.d(TAG, "RESULT OK");
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                    setStatusSuccess();
                } else if (result.isSessionExpired()) {
                    Log.d(TAG, "RESULT SESSION EXPIRED");
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(requireActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else if (result.getStatusCode() instanceof StatusCode.Server) {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                    StatusCode.Server server = (StatusCode.Server) result.getStatusCode();
                    Log.d(TAG, "RESULT SERVER ERROR " + server.toString());
                    if(server == StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_DENARO_BANK_SERVICE_NOT_AVAILABLE) {
                        int idString = R.string.split_bill_recipient_not_enabled;
                        SnackbarUtil.showSnackbarMessageCustom(requireActivity(), getString(idString));
                    } else {
                        showError(result.getStatusCode());
                    }
                    setPaymentRequestStatusFail();
                } else {
                    Log.d(TAG, "RESULT GENERIC ERROR");
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                    showError(result.getStatusCode());
                    setPaymentRequestStatusFail();
                }
            } else {
                Log.d(TAG, "RESULT FAIL");
                AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                setPaymentRequestStatusFail();
            }

            binding.homeButton.post(() ->
                    AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION));
            binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                Log.d(TAG, "Navigating to home");
                requireActivity().finish();
            }));
        };
    }

    private boolean hasPermissionStorage() {
        return ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionStorage() {
        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(MULTIMEDIA_DISCLOSURE)) {
            PermissionFlowManager.goToMultimediaDisclosure(this, activityResultLauncherMultimedia);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT) {
            if (resultCode == RESULT_OK) {
                shareAction();
            }
        }
    }

    private void shareAction() {
        String message = getString(R.string.split_bill_success_share);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getResources().getString(R.string.share));
        startActivity(shareIntent);
    }

    private void initTextSwitcher() {

        binding.resultTextSwitcher.setFactory(() -> new TextView(
                new ContextThemeWrapper(requireActivity(), R.style.AmountLabel), null, 0));

        Animation inAnim = AnimationUtils.loadAnimation(requireActivity(),
                android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(requireActivity(),
                android.R.anim.fade_out);

        inAnim.setDuration(200);
        outAnim.setDuration(200);

        binding.resultTextSwitcher.setInAnimation(inAnim);
        binding.resultTextSwitcher.setOutAnimation(outAnim);

        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, 200);

    }

    private void setStatusSuccess() {
        binding.loadingImgBackground.setVisibility(View.GONE);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultOkKo, 200);

        binding.resultTextSwitcher.setText(getString(R.string.get_money_result_ok));

        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, AnimationFadeUtil.DEFAULT_DURATION);

        binding.resultAnimation.setAnimation(getString(R.string.check_ok));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);

        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ok));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);
        if (!binding.toolbarSimple.isRightImageVisible()) {
            binding.toolbarSimple.setRightImageVisibility(true);
        }

    }

    private void setPaymentRequestStatusFail() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.loadingImgBackground.setVisibility(View.GONE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultOkKo, 200);

        binding.resultTextSwitcher.setText(getString(R.string.get_money_result_ko));
        binding.toolbarSimple.setRightImageVisibility(false);


        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, AnimationFadeUtil.DEFAULT_DURATION);

        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);

        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ko));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);
    }

}
