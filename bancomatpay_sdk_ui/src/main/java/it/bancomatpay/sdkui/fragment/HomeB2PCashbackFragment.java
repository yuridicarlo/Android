package it.bancomatpay.sdkui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.bancomatpay.sdkui.databinding.FragmentHomeServicesB2pCashbackBinding;

public class HomeB2PCashbackFragment extends Fragment {

    private FragmentHomeServicesB2pCashbackBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeServicesB2pCashbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
