package it.bancomat.pay.consumer.extended.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.bancomat.pay.consumer.events.HomeCarouselAnimationEvent;
import it.bancomatpay.consumer.databinding.FragmentHomeServicesB2pCashbackExtendedBinding;

public class HomeB2PCashbackFragmentExtended extends Fragment {

    private FragmentHomeServicesB2pCashbackExtendedBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeServicesB2pCashbackExtendedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HomeCarouselAnimationEvent event) {
        if (event.getPage() == HomeCarouselAnimationEvent.EAnimationPage.B2P_CASHBACK) {
            if (event.getEvent() == HomeCarouselAnimationEvent.EAnimationEvent.START) {
                startAnimation();
            } else if (event.getEvent() == HomeCarouselAnimationEvent.EAnimationEvent.STOP) {
                stopAnimation();
            }
        }
    }

    private void startAnimation() {
        binding.lottieAnimation.playAnimation();
    }

    private void stopAnimation() {
        binding.lottieAnimation.cancelAnimation();
    }
}
