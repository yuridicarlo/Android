package it.bancomat.pay.consumer;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.fragment.IntroFirstFragment;
import it.bancomat.pay.consumer.activation.fragment.IntroSecondFragment;
import it.bancomat.pay.consumer.activation.fragment.IntroThirdFragment;
import it.bancomat.pay.consumer.exception.BanksDataDbException;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.storage.AppUserDbHelper;
import it.bancomat.pay.consumer.utilities.AppFullscreenActivity;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityActivationIntroBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.db.BanksData;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class IntroActivity extends AppFullscreenActivity {

    private static final String TAG = IntroActivity.class.getSimpleName();

    private static final int NUM_PAGES = 3;
    private ServicesPagerAdapter adapterPagerServices;
    public ActivityActivationIntroBinding binding;

   /* @Override
    protected void onResume() {
        if(!hasAlreadyInsets){
            if (BancomatDataManager.getInstance().getScreenInsetBottom() == 0) {
                saveInsets(getWindow().getDecorView());
            }
            applyInsets();
        }

        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapterPagerServices = new ServicesPagerAdapter(getSupportFragmentManager());
        binding.pageIndicatorView.setVisibility(View.VISIBLE);
        binding.viewpager.setAdapter(adapterPagerServices);
        binding.viewpager.setOffscreenPageLimit(NUM_PAGES);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pageIndex) {
                switch (pageIndex) {
                    case 0:
                        if (getFirstFragment() != null) {
                            ((IntroFirstFragment) getFirstFragment()).startAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroFirstFragment is null");
                        }
                        if (getSecondFragment() != null) {
                            ((IntroSecondFragment) getSecondFragment()).stopAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroSecondFragment is null");
                        }
                        if (getThirdFragment() != null) {
                            ((IntroThirdFragment) getThirdFragment()).stopAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroThirdFragment is null");
                        }
                        break;
                    case 1:
                        if (getFirstFragment() != null) {
                            ((IntroFirstFragment) getFirstFragment()).stopAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroFirstFragment is null");
                        }
                        if (getSecondFragment() != null) {
                            ((IntroSecondFragment) getSecondFragment()).startAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroSecondFragment is null");
                        }
                        if (getThirdFragment() != null) {
                            ((IntroThirdFragment) getThirdFragment()).stopAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroThirdFragment is null");
                        }
                        break;
                    case 2:
                        if (getFirstFragment() != null) {
                            ((IntroFirstFragment) getFirstFragment()).stopAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroFirstFragment is null");
                        }
                        if (getSecondFragment() != null) {
                            ((IntroSecondFragment) getSecondFragment()).stopAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroSecondFragment is null");
                        }
                        if (getThirdFragment() != null) {
                            ((IntroThirdFragment) getThirdFragment()).startAnimation();
                        } else {
                            CustomLogger.e(TAG, "IntroThirdFragment is null");
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        binding.activationButton.setOnClickListener(new CustomOnClickListener(v -> {
            CjUtils.getInstance().startActivationFlow();
            ActivationFlowManager.goToActivableBankList(this);
        }));
        if (BancomatDataManager.getInstance().getScreenInsetBottom() == 0) {
            saveInsets(getWindow().getDecorView());
        }
        applyInsets();
    }

    private void applyInsets() {
        RelativeLayout.LayoutParams bottomLayoutParams = (RelativeLayout.LayoutParams) binding.layoutBottomButtons.getLayoutParams();

        if (!hasSoftKeys()) {
            bottomLayoutParams.setMargins(16, 0, 16, 0);
            binding.layoutBottomButtons.setLayoutParams(bottomLayoutParams);
        } else {
            binding.layoutBottomButtons.post(() -> {
                int insetBottom = BancomatDataManager.getInstance().getScreenInsetBottom();
                if (insetBottom != 0) {
                    bottomLayoutParams.bottomMargin = insetBottom;
                } else {
                    bottomLayoutParams.bottomMargin = 120;
                }
                binding.layoutBottomButtons.setLayoutParams(bottomLayoutParams);
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        checkBanksFileUpdate();
    }

    private static class ServicesPagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> mPageReferenceMap = new SparseArray<>();

        ServicesPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new IntroFirstFragment();
                    break;
                case 1:
                    fragment = new IntroSecondFragment();
                    break;
                case 2:
                    fragment = new IntroThirdFragment();
                    break;
                default:
                    fragment = new IntroFirstFragment();
                    break;
            }

            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
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

    private Fragment getFirstFragment() {
        return adapterPagerServices != null ? adapterPagerServices.getFragment(0) : null;
    }

    private Fragment getSecondFragment() {
        return adapterPagerServices != null ? adapterPagerServices.getFragment(1) : null;
    }

    private Fragment getThirdFragment() {
        return adapterPagerServices != null ? adapterPagerServices.getFragment(2) : null;
    }

    private void checkBanksFileUpdate() {
        CustomLogger.d(TAG, "Checking banks file update...");

        try {
            BanksData.Model banksModel = AppUserDbHelper.getInstance().getBanksData();
            if (banksModel != null) {
                String banksFileVersion = banksModel.getVersion();

                final BancomatPayApiInterface sdk = BancomatPayApiInterface.Factory.getInstance();
                Task<?> t = sdk.doGetBanksConfigurationFile(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            if (!TextUtils.isEmpty(result.getResult().getFile())) {
                                AppUserDbHelper.getInstance().saveBanksDataFile(result.getResult());
                            }
                        }
                    }
                }, banksFileVersion);
                addTask(t);

            }

        } catch (BanksDataDbException exception) {
            CustomLogger.e(TAG, "BanksDataDbException: " + exception.getMessage());
        }

    }

}
