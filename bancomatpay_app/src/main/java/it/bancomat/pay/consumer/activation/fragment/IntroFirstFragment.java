package it.bancomat.pay.consumer.activation.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.bancomatpay.consumer.databinding.FragmentActivationFlowFirstBinding;

public class IntroFirstFragment extends Fragment {

    private FragmentActivationFlowFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActivationFlowFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startAnimation() {
        new Handler().post(() -> binding.lottieAnimation.playAnimation());
    }

    public void stopAnimation() {
        new Handler().post(() -> binding.lottieAnimation.cancelAnimation());
    }

}
