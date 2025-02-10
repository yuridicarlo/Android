package it.bancomat.pay.consumer.init.fragment.activation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentInsertActivationCodeBinding;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdkui.activities.disclosure.PermissionDisclosureActivity;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.QRCODE_WRONG_ACTIVATION_CODE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.DISCLOSURE_TYPE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CAMERA_CONSENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_INSERT_CODE;


public class InsertActivationCodeFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CAMERA = 123;
    FragmentInsertActivationCodeBinding binding;
    InitViewModel initViewModel;
    WindowViewModel windowViewModel;
    private final static String TAG = InsertActivationCodeFragment.class.getSimpleName();

    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_CAMERA_CONSENT,result.getResultCode(),data);
            });

    ActivityResultLauncher<String> activityCameraRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            managePermissionRequestResult(REQUEST_PERMISSION_CAMERA);
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInsertActivationCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);
        windowViewModel = new ViewModelProvider(requireActivity()).get(WindowViewModel.class);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				if(getActivity() instanceof ActivationActivity){
				    ((ActivationActivity) getActivity()).showConfirmAbortDialog();
                }
			}
		});

        if(getActivity() instanceof ActivationActivity){
            ((ActivationActivity) getActivity()).initSmsReceiver();
        }

        binding.setActivationCode(initViewModel.getObservableActivationCode());
        binding.homeBankingEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.homeBankingEditText.requestFocus();
            }
        }, 1000);
        windowViewModel.setNavigationBarColor(R.color.white_background);
        binding.confirmButton.setOnClickListener(v -> {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(getContext(), KEY_ACTIVATION_INSERT_CODE, null, true);
            initViewModel.userMonitoring(initViewModel.getBankUUID(),
                    UserMonitoringConstants.ACTIVATION_TAG,
                    UserMonitoringConstants.ACTIVATION_TROUGH_CODE,
                    "");

            initViewModel.setActivationCodeFromManual(initViewModel.getObservableActivationCode().getActivationCode());
            initViewModel.verifyActivationCode();
            windowViewModel.showLoader();
        });

        binding.qrButton.setOnClickListener(v -> {

            if (checkCameraPermission()) {
                if(getActivity() instanceof ActivationActivity){
                    ((ActivationActivity) getActivity()).hideKeyboard();
                }
                NavHelper.navigate(getActivity(), InsertActivationCodeFragmentDirections.actionInsertActivationCodeFragmentToScanQrCodeFragment());

            } else {
                if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)) {
                    Intent intent = new Intent(getContext(), PermissionDisclosureActivity.class);
                    intent.putExtra(DISCLOSURE_TYPE, CAMERA_DISCLOSURE);
                    activityResultLauncherCamera.launch(intent);

                } else{
                    activityCameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA);


                }
            }

        });

        initViewModel.getVerifyActivationCodeResponse().setListener(getViewLifecycleOwner(), new SingleListener<VerifyActionCodeData>() {
            @Override
            public void onSuccess(VerifyActionCodeData response) {
                windowViewModel.hideLoader();

                NavHelper.navigate(getActivity(), InsertActivationCodeFragmentDirections.actionInsertActivationCodeFragmentToInsertOtpCodeFragment());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                if(throwable instanceof ServerException) {
                    if(((ServerException) throwable).getResult().getStatusCode() == QRCODE_WRONG_ACTIVATION_CODE) {
                        initViewModel.getObservableActivationCode().setShowError(true);
                    }else {
                        NavHelper.showSnackBarMessage(requireActivity(), throwable);
                    }
                }else {
                    NavHelper.showSnackBarMessage(requireActivity(), throwable);
                }
            }
        });

        if(initViewModel.isFromDeepLink()){
            initViewModel.getObservableActivationCode().setActivationCode(initViewModel.getActivationCodeDecoded());
            initViewModel.setFromDeepLink(false);
            windowViewModel.showLoader();
            initViewModel.verifyActivationCode();
        }
    }



    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SHOW_CAMERA_CONSENT) {
            if (checkCameraPermission()) {
                NavHelper.navigate(getActivity(), InsertActivationCodeFragmentDirections.actionInsertActivationCodeFragmentToScanQrCodeFragment());
            }
        }
    }

    protected void managePermissionRequestResult(int requestCode) {
        if(requestCode == REQUEST_PERMISSION_CAMERA){
            if (checkCameraPermission()) {
                NavHelper.navigate(getActivity(), InsertActivationCodeFragmentDirections.actionInsertActivationCodeFragmentToScanQrCodeFragment());
            }
        }
    }

    private boolean checkCameraPermission(){
        return ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}