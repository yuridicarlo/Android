package it.bancomat.pay.consumer.storeLocator.fragments;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.POSITION_DISCLOSURE;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.hms.support.api.client.Status;

import it.bancomat.pay.consumer.viewmodel.StoreLocatorViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.FlagModel;

import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.FragmentStoreLocatorListBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

public class StoreLocatorListFragment extends GenericErrorFragment {

    private static final String TAG = StoreLocatorListFragment.class.getSimpleName();

    private FragmentStoreLocatorListBinding binding;
    private static final int NUM_TABS = 2;
    private StoreLocatorViewModel storeLocatorViewModel;
    ActivityResultLauncher<Intent> activityResultLauncherPosition = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                setAdapter();
                BancomatDataManager dm = BancomatDataManager.getInstance();
                FlagModel flagModel = dm.getFlagModel();
                if (flagModel.isShowStoreLocatorPopUp()) {
                    showDisclaimerPopup();

                    flagModel.setShowStoreLocatorPopUp(false);
                    dm.putFlagModel(flagModel);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStoreLocatorListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        storeLocatorViewModel = new ViewModelProvider(requireActivity()).get(StoreLocatorViewModel.class);

        if(storeLocatorViewModel.isShowBackButton()) {
            binding.toolbarSimple.setVisibility(View.VISIBLE);
            binding.toolbarSimple.setOnClickLeftImageListener(v -> requireActivity().onBackPressed());
        } else {
            binding.toolbarSimple.setVisibility(View.GONE);
        }

        requestLocationPermissions();
        ((GenericErrorActivity) requireActivity()).setLightStatusBar(binding.mainLayout, R.color.white_background);

        ViewPager2 viewPager2 = binding.transactionsViewpager;
        setAdapter();
        viewPager2.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        viewPager2.setUserInputEnabled(false);

        TabLayout tabLayout = binding.transactionsTabs;
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.stores_map));
                    break;
                case 1:
                    tab.setText(getString(R.string.e_commerce));
                    break;
            }
        }).attach();
    }

    private void requestLocationPermissions() {
        if (!areLocationPermissionsActive()) {
            if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(POSITION_DISCLOSURE)) {
                PermissionFlowManager.goToPositionDisclosure(requireActivity(), activityResultLauncherPosition);
            }
        } else {
            checkGPS();
        }
    }

    private boolean areLocationPermissionsActive(){
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    void showDisclaimerPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.warning_title);
        builder.setMessage(R.string.store_locator_disclaimer_popup_text)
                .setCancelable(false);

        builder.setPositiveButton(R.string.store_locator_understood_button, (dialog, id) -> {
            dialog.dismiss();
            if(areLocationPermissionsActive()) {
                checkGPS();
            }
        });

        builder.show();
    }

    private void checkGPS() {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            checkGPShuawei();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            checkGPSgoogle();
        }
    }

    private void checkGPSgoogle() {
        storeLocatorViewModel.setIsGpsResultPositive(null);
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        SettingsClient client = new SettingsClient(requireActivity());
        if (storeLocatorViewModel.isGpsResultPositive().getValue() == null) {
            client.checkLocationSettings(builder.build())
                    .addOnCompleteListener(task -> {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);

                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                        } catch (ApiException exception) {
                            Log.v(" Failed ", String.valueOf(exception.getStatusCode()));

                            switch (exception.getStatusCode()) {

                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied. But could be fixed by showing the
                                    // user a dialog.
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    try {
                                        resolvable.startResolutionForResult(
                                                requireActivity(),
                                                StoreLocatorViewModel.REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                        storeLocatorViewModel.setIsGpsResultPositive(false);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    storeLocatorViewModel.setIsGpsResultPositive(false);
                                    break;
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, e.getMessage());
                    });
        }
    }

    private void checkGPShuawei() {
        storeLocatorViewModel.setIsGpsResultPositive(null);
        com.huawei.hms.location.LocationRequest mLocationRequest = com.huawei.hms.location.LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        com.huawei.hms.location.LocationSettingsRequest.Builder builder = new com.huawei.hms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        com.huawei.hms.location.SettingsClient client = new com.huawei.hms.location.SettingsClient(requireActivity());
        if (storeLocatorViewModel.isGpsResultPositive().getValue() == null) {
            client.checkLocationSettings(builder.build())
                    .addOnCompleteListener(task -> {
                        try {
                            com.huawei.hms.location.LocationSettingsResponse response = task.getResult();

                        } catch (Exception e) {
                            com.huawei.hms.common.ResolvableApiException resolvable = new com.huawei.hms.common.ResolvableApiException(Status.FAILURE);
                            try {
                                    resolvable.startResolutionForResult(
                                            requireActivity(),
                                            StoreLocatorViewModel.REQUEST_CHECK_SETTINGS);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    storeLocatorViewModel.setIsGpsResultPositive(false);
                                }

                        }
                    });


        }
    }

    private class SectionsPagerAdapter extends FragmentStateAdapter {
        SectionsPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Boolean isGpsResultPositive = storeLocatorViewModel.isGpsResultPositive().getValue();
            boolean showMap = isGpsResultPositive == null || Boolean.TRUE.equals(isGpsResultPositive);
            switch (position) {
                case 0:
                    return (areLocationPermissionsActive() && showMap) ? new MerchantMapFragment() : new MerchantMapAlternativeFragment();
                case 1:
                    return new EcommerceCategoryListFragment();
                default:
                    return new Fragment();
            }
        }
        @Override
        public int getItemCount() {
            return NUM_TABS;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter(){
        binding.transactionsViewpager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager(), getLifecycle()));
    }

}
