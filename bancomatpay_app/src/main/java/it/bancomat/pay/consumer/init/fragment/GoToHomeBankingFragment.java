package it.bancomat.pay.consumer.init.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentGoToHomeBankingBinding;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.CjUtils;


public class GoToHomeBankingFragment extends Fragment {

    FragmentGoToHomeBankingBinding binding;
    InitViewModel initViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGoToHomeBankingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.insertCodeButton.setOnClickListener(v -> {

            checkDeviceSecured();

        });
        binding.toolbarSimple.setOnClickLeftImageListener(v -> requireActivity().onBackPressed());

        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);


        if(initViewModel.isFromDeepLink()){
            checkDeviceSecured();
        }

    }

    private void checkDeviceSecured() {
        if (BancomatPayApiInterface.Factory.getInstance().isDeviceSecured()) {
            CjUtils.getInstance().startActivationFlow();
            Intent intent = new Intent(getContext(), ActivationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else {
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(getContext());
            builder.setTitle(R.string.enable_biometry_dialog_title)
                    .setMessage(R.string.enable_biometry_dialog_description)
                    .setPositiveButton(R.string.enable_biometry_dialog_confirm, (dialog, id) -> {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        try {
                            startActivity(intent);
                        }catch (ActivityNotFoundException e){}
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.enable_biometry_dialog_cancel, (dialog, id) -> {
                        dialog.dismiss();
                    })
                    .setCancelable(false);
            builder.showDialog(getActivity());
        }
    }
}