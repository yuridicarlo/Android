package it.bancomatpay.sdkui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.bancomatpay.sdkui.databinding.FragmentHomeServicesP2pPaymentBinding;

public class HomeP2PPaymentFragment extends Fragment {

    private FragmentHomeServicesP2pPaymentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeServicesP2pPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
