package it.bancomat.pay.consumer.init.fragment.activation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.notification.MyFirebaseMessagingService;
import it.bancomat.pay.consumer.notification.MyHmsMessageService;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.consumer.databinding.FragmentActivationCompletedBinding;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ELAPSED;


public class ActivationCompletedFragment extends Fragment {

    FragmentActivationCompletedBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivationCompletedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        binding.buttonGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                        .startBancomatPayFlow(requireActivity(), null,
                                AppBancomatDataManager.getInstance().getTokens().getOauth());
                FullStackSdkDataManager.getInstance().putHomePanelExpanded(false);
                requireActivity().finish();
            }
        });

        if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            MyFirebaseMessagingService.registerCurrentToken(requireActivity());
        } else if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            MyHmsMessageService.registerCurrentToken(requireActivity());
        }

        HashMap<String, String> mapEventParams = new HashMap<>();
        mapEventParams.put(PARAM_ELAPSED, CjUtils.getInstance().getActivationTimeElapsed());
        CjUtils.getInstance().sendCustomerJourneyTagEvent(getContext(), KEY_ACTIVATION_COMPLETED, mapEventParams, true);


    }

}