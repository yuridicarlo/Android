package it.bancomat.pay.consumer.init.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.init.fragment.welcome.WelcomeFirstFragment;
import it.bancomat.pay.consumer.init.fragment.welcome.WelcomeSecondFragment;
import it.bancomat.pay.consumer.init.fragment.welcome.WelcomeThirdFragment;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.utilities.CjConstants;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentWelcomeBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;


public class WelcomeFragment extends Fragment {

    FragmentWelcomeBinding binding;

    private static final String TAG = WelcomeFragment.class.getSimpleName();
    private static final int NUM_PAGES = 3;
    private ServicesPagerAdapter adapterPagerServices;
    private InitViewModel initViewModel;
    private WindowViewModel windowViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);
        windowViewModel = new ViewModelProvider(requireActivity()).get(WindowViewModel.class);

        adapterPagerServices = new ServicesPagerAdapter(requireActivity().getSupportFragmentManager());
        binding.pageIndicatorView.setVisibility(View.VISIBLE);
        binding.viewpager.setAdapter(adapterPagerServices);
        binding.viewpager.setOffscreenPageLimit(NUM_PAGES);

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                initViewModel.onIntroPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initViewModel.checkBanksFileUpdate();
        windowViewModel.setStatusBarColor(R.color.blue_status_bar);
        windowViewModel.setNavigationBarColor(R.color.blue_navigation_bar);

        binding.activationButton.setOnClickListener(v -> {
            NavHelper.navigate(requireActivity(), WelcomeFragmentDirections.actionWelcomeFragmentToActivableBankListFragment());
        });

        binding.storeButton.setOnClickListener(v -> {
            CjUtils.getInstance().startStoreLocatorFlow();
            ActivationFlowManager.goToStoreLocator(requireActivity());
        });

        binding.activationPrivacy.setOnClickListener(new CustomOnClickListener(v -> ActivationFlowManager.goToShowTermsAndConditions(requireActivity(), getString(R.string.privacy_url))));
        binding.activationTermsAndConditions.setOnClickListener(new CustomOnClickListener(v -> ActivationFlowManager.goToShowTermsAndConditions(requireActivity(), getString(R.string.terms_and_conditions_url))));
        if(initViewModel.isFromDeepLink()){
            NavHelper.navigate(requireActivity(), WelcomeFragmentDirections.actionWelcomeFragmentToActivableBankListFragment());
        }
    }

    private static class ServicesPagerAdapter extends FragmentStatePagerAdapter {

        ServicesPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new WelcomeFirstFragment();
                case 1:
                    return new WelcomeSecondFragment();
                default:
                    return new WelcomeThirdFragment();

            }
        }


        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

}