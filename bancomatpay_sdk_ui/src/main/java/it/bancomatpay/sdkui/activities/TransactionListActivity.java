package it.bancomatpay.sdkui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.TransactionAdapter;
import it.bancomatpay.sdkui.adapter.TransactionOutgoingAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmTransactionsBinding;
import it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager;
import it.bancomatpay.sdkui.flowmanager.TransactionsFlowManager;
import it.bancomatpay.sdkui.fragment.TransactionsOutgoingPaymentRequestFragment;
import it.bancomatpay.sdkui.fragment.TransactionsSentAndReceivedFragment;
import it.bancomatpay.sdkui.model.Transaction;
import it.bancomatpay.sdkui.model.TransactionOutgoing;

public class TransactionListActivity extends GenericErrorActivity implements TransactionAdapter.InteractionListener,
        TransactionOutgoingAdapter.InteractionListener {

    private ActivityBcmTransactionsBinding binding;

    private static final int NUM_TABS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(TransactionListActivity.class.getSimpleName());
        binding = ActivityBcmTransactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());


        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        binding.transactionsViewpager.setAdapter(mSectionsPagerAdapter);
        binding.transactionsViewpager.setOffscreenPageLimit(2);

        binding.transactionsViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tabSelected = binding.transactionsTabs.getTabAt(position);
                if (tabSelected != null) {
                    tabSelected.select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        binding.transactionsViewpager.setOffscreenPageLimit(NUM_TABS);

        TabLayout.Tab tab0 = binding.transactionsTabs.getTabAt(0);
        if (tab0 != null) {
            tab0.setText(getString(R.string.send_money_tab));
        }
        TabLayout.Tab tab1 = binding.transactionsTabs.getTabAt(1);
        if (tab1 != null) {
            tab1.setText(getString(R.string.get_money_tab));
        }
        binding.transactionsTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.transactionsViewpager));

    }

    @Override
    public void onListViewInteraction(Transaction item) {
        TransactionsFlowManager.goToDetailTransaction(this, item);
    }

    @Override
    public void onOutgoingRequestListViewInteraction(TransactionOutgoing item) {
        TransactionsFlowManager.goToDetailTransactionOutgoing(this, item);
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TransactionsSentAndReceivedFragment();
                case 1:
                    return new TransactionsOutgoingPaymentRequestFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public Parcelable saveState() {
            Bundle bundle = (Bundle) super.saveState();
            if (bundle != null) {
                bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
            }
            return bundle;
        }

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {
            BCMAbortCallback.getInstance().getAuthenticationListener()
                    .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_EXIT);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

}
