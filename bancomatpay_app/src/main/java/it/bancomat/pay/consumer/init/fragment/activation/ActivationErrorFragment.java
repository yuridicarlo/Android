package it.bancomat.pay.consumer.init.fragment.activation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.bancomat.pay.consumer.biometric.PromptInfoHelper;
import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.databinding.FragmentActivationErrorBinding;
import it.bancomatpay.sdk.manager.lifecycle.CompletableListener;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;

public class ActivationErrorFragment extends Fragment {

    FragmentActivationErrorBinding binding;
    InitViewModel initViewModel;
    WindowViewModel windowViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentActivationErrorBinding.inflate(inflater, container, false);
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

        initViewModel.biometricEnrollResponse().setListener(getViewLifecycleOwner(), new SingleListener<BiometricEnrollData>() {

            @Override
            public void onSuccess(BiometricEnrollData response) {
                initViewModel.storeBiometricEnrollData(requireActivity(), PromptInfoHelper.getPromptInfo());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.showSnackBarMessage(getActivity(), throwable);
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
                NavHelper.showSnackBarMessage(getActivity(), throwable);
            }
        });


        initViewModel.initSdkResponse().setListener(getViewLifecycleOwner(), new CompletableListener() {
            @Override
            protected void onComplete() {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), ActivationErrorFragmentDirections.actionActivationErrorFragmentToActivationCompletedFragment());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.showSnackBarMessage(getActivity(), throwable);
            }
        });


        binding.buttonRetrySdkActivation.setOnClickListener(v -> {
            switch (initViewModel.getErrorState()){
                case RETRY_INIT_SDK:
                    initViewModel.initSdk(getActivity());
                    windowViewModel.showLoader();
                    break;
                case RETRY_STORE_BIOMETRIC_ENROLL_DATA:
                    initViewModel.storeBiometricEnrollData(getActivity(), PromptInfoHelper.getPromptInfo());
                    windowViewModel.showLoader();
                    break;
                case RETRY_BIOMETRIC_ENROLL:
                    initViewModel.biometricEnroll(requireContext());
                    windowViewModel.showLoader();
                    break;
            }
        });

    }
}