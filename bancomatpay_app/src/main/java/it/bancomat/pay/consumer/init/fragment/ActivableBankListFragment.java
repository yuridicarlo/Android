package it.bancomat.pay.consumer.init.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import it.bancomat.pay.consumer.init.ActivationActivity;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentActivableBankListBinding;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.CjUtils;

import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_CODE;
import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_FROM_DEEP_LINK;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class ActivableBankListFragment extends Fragment {


    private FragmentActivableBankListBinding binding;
    private WindowViewModel windowViewModel;
    private InitViewModel initViewModel;

    private static final int NUM_TABS = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivableBankListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        windowViewModel = new ViewModelProvider(requireActivity()).get(WindowViewModel.class);
        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);

        //Prevent keyboard open
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding.toolbarSimple.setOnClickLeftImageListener(v -> requireActivity().onBackPressed());

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(requireActivity());
        ViewPager2 viewPager2 = binding.transactionsViewpager;
        viewPager2.setAdapter(mSectionsPagerAdapter);
        viewPager2.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        viewPager2.setUserInputEnabled(false);

        TabLayout tabLayout = binding.transactionsTabs;
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setCustomView(mSectionsPagerAdapter.getTabView(requireContext(), getString(R.string.subscribed_banks), position));
                    break;
                case 1:
                    tab.setCustomView(mSectionsPagerAdapter.getTabView(requireContext(), getString(R.string.unsubscribed_banks), position));
                    break;
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView title = tab.getCustomView().findViewById(R.id.tabTitle);
                title.setTextColor(ContextCompat.getColor(requireContext(), R.color.generic_coloured_background));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView title = tab.getCustomView().findViewById(R.id.tabTitle);
                title.setTextColor(ContextCompat.getColor(requireContext(), R.color.tab_unselected_text));
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.insertCodeButton.setOnClickListener(v -> {
            checkDeviceSecured();
        });
        windowViewModel.setStatusBarColor(R.color.white_background);
        windowViewModel.setNavigationBarColor(R.color.white_background);

        if(initViewModel.isFromDeepLink()){
            checkDeviceSecured();
        }
    }


    private void checkDeviceSecured() {
        if (BancomatPayApiInterface.Factory.getInstance().isDeviceSecured()) {
            CjUtils.getInstance().startActivationFlow();
            Intent intent = new Intent(getContext(), ActivationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra(ACTIVATION_FROM_DEEP_LINK, initViewModel.isFromDeepLink());
            intent.putExtra(ACTIVATION_CODE, initViewModel.getActivationCode());
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
        initViewModel.setFromDeepLink(false);
        initViewModel.setActivationCodeFromDeepLink(null);
    }

    private static class SectionsPagerAdapter extends FragmentStateAdapter {

        SectionsPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new SubscribedBankListFragment();
                case 1:
                    return new UnsubscribedBankListFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_TABS;
        }

        public View getTabView(Context context, String title, int position) {
            View v = LayoutInflater.from(context).inflate(R.layout.custom_activation_tab_layout, null);
            TextView tv = (TextView) v.findViewById(R.id.tabTitle);
            tv.setText(title);
            if (position == 0) {
                tv.setTextColor(ContextCompat.getColor(context, R.color.generic_coloured_background));
            }
            return v;
        }
    }
}