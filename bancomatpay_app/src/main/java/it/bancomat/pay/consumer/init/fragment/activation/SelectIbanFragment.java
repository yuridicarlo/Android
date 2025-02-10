package it.bancomat.pay.consumer.init.fragment.activation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.biometric.PromptInfoHelper;
import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.init.adapter.MultiIbanAdapter;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.databinding.FragmentSelectIbanBinding;
import it.bancomatpay.sdk.manager.lifecycle.CompletableListener;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;


public class SelectIbanFragment extends Fragment implements MultiIbanAdapter.IbanListClickListener {

    FragmentSelectIbanBinding binding;

    InitViewModel initViewModel;
    WindowViewModel windowViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectIbanBinding.inflate(inflater, container, false);
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
        DataBank dataBank = initViewModel.getDataBank();

        List<InstrumentData> instrument = dataBank.getInstrument();
        MultiIbanAdapter adapter = new MultiIbanAdapter(instrument, dataBank.getLogoSearch(), this);
        binding.multiIbanBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.multiIbanBankList.setAdapter(adapter);

        binding.buttonMultiIbanNext.setEnabled(false);

        binding.buttonMultiIbanNext.setOnClickListener(v -> {
            windowViewModel.showLoader();
            initViewModel.biometricEnroll(requireContext());
        });

        initViewModel.biometricEnrollResponse().setListener(getViewLifecycleOwner(), new SingleListener<BiometricEnrollData>() {

            @Override
            public void onSuccess(BiometricEnrollData response) {
                initViewModel.storeBiometricEnrollData(requireActivity(), PromptInfoHelper.getPromptInfo());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), SelectIbanFragmentDirections.actionSelectIbanFragmentToActivationErrorFragment());

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
                NavHelper.navigate(getActivity(), SelectIbanFragmentDirections.actionSelectIbanFragmentToActivationErrorFragment());
            }
        });


        initViewModel.initSdkResponse().setListener(getViewLifecycleOwner(), new CompletableListener() {
            @Override
            protected void onComplete() {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), SelectIbanFragmentDirections.actionSelectIbanFragmentToActivationCompletedFragment());
            }

            @Override
            public void onError(Throwable throwable) {
                windowViewModel.hideLoader();
                NavHelper.navigate(getActivity(), SelectIbanFragmentDirections.actionSelectIbanFragmentToActivationErrorFragment());
            }
        });

}

    @Override
    public void onInstrumentDataClicked(InstrumentData instrumentData) {
        initViewModel.setInstrumentDataSelected(instrumentData);
        binding.buttonMultiIbanNext.setEnabled(true);
    }

}