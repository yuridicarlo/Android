package it.bancomat.pay.consumer.init.fragment.activation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.bancomat.pay.consumer.biometric.PromptInfoHelper;
import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.network.dto.VerifyOtpCodeData;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentInsertOtpCodeBinding;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.lifecycle.CompletableListener;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXT_SERVER_EXPIRED_OTP;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXT_SERVER_MAX_OTP_NUMBER_REACHED;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXT_SERVER_WRONG_OTP;


public class InsertOtpCodeFragment extends Fragment {

    FragmentInsertOtpCodeBinding binding;
    InitViewModel initViewModel;
    WindowViewModel windowViewModel;

    private final static String TAG = InsertOtpCodeFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInsertOtpCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(getActivity() instanceof ActivationActivity){
                    ((ActivationActivity) getActivity()).showConfirmAbortDialog();
                }
            }
        });

        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);
        windowViewModel = new ViewModelProvider(requireActivity()).get(WindowViewModel.class);

        binding.otpTextLabel.setText(getString(R.string.insert_otp_text, initViewModel.getMaskedPhoneNumber()));

        binding.insertNumberEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =  (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(binding.insertNumberEditText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                binding.insertNumberEditText.requestFocus();
            }
        }, 1500);

        binding.setModel(initViewModel.getKeyboardCodeObservable());

        binding.keyboardButtonContinueOn.setOnClickListener(v -> {
            windowViewModel.showLoader();
            initViewModel.verifyOtpCode();

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.insertNumberEditText.getWindowToken(), 0);
        });



        binding.resendOtpButton.setOnClickListener(v -> {
            windowViewModel.showLoader();
            initViewModel.resendOtpCode();
            if(getActivity() instanceof ActivationActivity){
                ((ActivationActivity) getActivity()).initSmsReceiver();
            }
        });

        initViewModel.verifyOtpCodeResponse().setListener(getViewLifecycleOwner(), new SingleListener<VerifyOtpCodeData>() {
            @Override
            public void onSuccess(VerifyOtpCodeData response) {
                if(response.isMultiIbanBank()){
                    windowViewModel.hideLoader();
                    NavHelper.navigate(getActivity(), InsertOtpCodeFragmentDirections.actionInsertOtpCodeFragmentToSelectIbanFragment());
                }else {
                    initViewModel.biometricEnroll(requireContext());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                if(throwable instanceof ServerException) {
                    Result result = ((ServerException) throwable).getResult();
                    if (result.getStatusCode() == EXT_SERVER_WRONG_OTP) {
                        binding.getModel().setShowError(true);
                        binding.errorMessage.setText(R.string.activation_insert_otp_fragment_error_wrong);

                    } else if (result.getStatusCode() == EXT_SERVER_EXPIRED_OTP) {
                        binding.getModel().setShowError(true);
                        binding.errorMessage.setText(R.string.activation_insert_otp_fragment_error_expired);

                    } else if (result.getStatusCode() == EXT_SERVER_MAX_OTP_NUMBER_REACHED) {
                        binding.getModel().setShowError(true);
                        binding.errorMessage.setText(R.string.activation_insert_otp_fragment_error_max_otp);

                    } else {
                        NavHelper.showSnackBarMessage(requireActivity(), throwable);
                    }
                }else {
                    NavHelper.showSnackBarMessage(requireActivity(), throwable);
                }
            }
        });

        initViewModel.resendOtpCodeResponse().setListener(getViewLifecycleOwner(), new CompletableListener() {
            @Override
            protected void onComplete() {
                windowViewModel.hideLoader();
                SnackbarUtil.showSnackbarMessageCustom(requireActivity(), getString(R.string.activation_insert_otp_fragment_otp_sent), v -> { });

            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.showSnackBarMessage(requireActivity(), throwable);
            }
        });

        initViewModel.biometricEnrollResponse().setListener(getViewLifecycleOwner(), new SingleListener<BiometricEnrollData>() {

            @Override
            public void onSuccess(BiometricEnrollData response) {
                initViewModel.storeBiometricEnrollData(requireActivity(), PromptInfoHelper.getPromptInfo());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), InsertOtpCodeFragmentDirections.actionInsertOtpCodeFragmentToActivationErrorFragment());

            }
        });

        initViewModel.storeBiometricEnrollDataResponse().setListener(getViewLifecycleOwner(), new CompletableListener() {
            @Override
            protected void onComplete() {
                initViewModel.initSdk(requireActivity());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), InsertOtpCodeFragmentDirections.actionInsertOtpCodeFragmentToActivationErrorFragment());
            }
        });


        initViewModel.initSdkResponse().setListener(getViewLifecycleOwner(), new CompletableListener() {
            @Override
            protected void onComplete() {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), InsertOtpCodeFragmentDirections.actionInsertOtpCodeFragmentToActivationCompletedFragment());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), InsertOtpCodeFragmentDirections.actionInsertOtpCodeFragmentToActivationErrorFragment());
            }
        });

    }


}