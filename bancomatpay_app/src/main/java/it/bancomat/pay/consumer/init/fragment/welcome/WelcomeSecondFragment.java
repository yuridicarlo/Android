package it.bancomat.pay.consumer.init.fragment.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.consumer.databinding.FragmentWelcomeSecondBinding;

public class WelcomeSecondFragment extends Fragment {

    private FragmentWelcomeSecondBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWelcomeSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViewModel initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);
        initViewModel.getIntroPageSelected().observe(getViewLifecycleOwner(), integer -> {
            if(integer == 1){
                binding.lottieAnimation.playAnimation();
            }else {
                binding.lottieAnimation.cancelAnimation();
            }
        });
    }
}
