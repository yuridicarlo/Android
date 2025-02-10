package it.bancomat.pay.consumer.storeLocator.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.utilities.AppMockUtils;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.StoreLocatorListTileAdapter;
import it.bancomatpay.sdkui.databinding.FragmentPhysicalShopListBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.model.ListTile;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.MapperConsumer;
import it.bancomatpay.sdkui.utilities.NavHelper;

public class PhysicalShopListFragment extends GenericErrorFragment implements StoreLocatorListTileAdapter.InteractionListener {

    private static final String TAG = PhysicalShopListFragment.class.getSimpleName();

    private FragmentPhysicalShopListBinding binding;

    private StoreLocatorListTileAdapter adapter;

    private String searchText;
    private boolean isFinal = false;
    private int pageNum = 1;

    private BcmLocation mLastLocation;

    private String BCM_LOCATION = "BCM_LOCATION";

    private Handler handlerUi = new Handler();

    private StoreLocatorListTileAdapter.OnFilterResultsPublished callback = () -> {
        binding.shopListEmpty.setVisibility(View.GONE);
        if (adapter.getItemCount() > 1) {
            binding.typeSomethingHintLayout.setVisibility(View.INVISIBLE);
        } else {
            binding.typeSomethingHintLayout.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhysicalShopListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.toolbarSimple.setOnClickLeftImageListener(v -> NavHelper.popBackStack(requireActivity()));
        ((GenericErrorActivity) requireActivity()).setLightStatusBar(view, it.bancomatpay.sdkui.R.color.white_background);
        Bundle args = getArguments();
        if (args != null) {
            mLastLocation = (BcmLocation) args.getSerializable("location");
            Log.d(TAG, "args received: " + mLastLocation);
        } else {
            mLastLocation = (BcmLocation) savedInstanceState.getSerializable(BCM_LOCATION);
        }

        setRecyclerView();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                searchText = text.toString();
                isFinal = false;
                pageNum = 1;
                if (text.length() > 0) {
                    if (binding.cancelButtonSearch.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, 250);
                    }
                    if(!binding.refresh.isRefreshing()) {
                        binding.recyclerViewShops.setVisibility(View.VISIBLE);
                    }
                } else {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
                    adapter.clear();
                    binding.recyclerViewShops.setVisibility(View.INVISIBLE);
                    binding.typeSomethingHintLayout.setVisibility(View.VISIBLE);
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
                }

                if (TextUtils.isEmpty(text) || text.length() < 3) {
                    if (adapter != null) {
                        adapter.getFilter().filter(text);
                    } else {
                        binding.typeSomethingHintLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.typeSomethingHintLayout.setVisibility(View.INVISIBLE);
                    setRefreshing(true, true);
                    performSearch(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.searchEditText.addTextChangedListener(textWatcher);

        binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
            binding.searchEditText.getText().clear();
            adapter.clear();
            AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
        }));

        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorAccentBancomat));

        binding.refresh.setOnRefreshListener(direction -> {
            if (searchText == null || searchText.length() < 3) {
                handlerUi.postDelayed(() -> setRefreshing(false, true), 500);
                return;
            }
            Log.d(TAG, "Refresh triggered at "
                    + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
            if (direction != SwipyRefreshLayoutDirection.TOP) {
                return;
            }
            isFinal = false;
            pageNum = 1;
            setRefreshing(true, true);
            performSearch(false);
        });
    }

    void performSearch(boolean isPaging) {
        if (searchText == null || searchText.length() < 3) {
            handlerUi.postDelayed(() -> setRefreshing(false, true), 500);
            return;
        }
        showEmptyResult(false);
        BancomatPayApiInterface.Factory.getInstance().doGetStoreLocatorSearchPhysicalRequest(requireActivity(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    isFinal = result.getResult().isListComplete();
                    if (!isFinal) {
                        pageNum++;
                        if (result.getResult().getShops().isEmpty()) {
                            performSearch(true);
                        } else {
                            binding.refresh.setDirection(SwipyRefreshLayoutDirection.BOTH);
                            setRefreshing(false, true);
                        }
                    } else {
                        setRefreshing(false, true);
                        binding.refresh.setDirection(SwipyRefreshLayoutDirection.TOP);
                    }
                    if(isPaging) {
                        manageShopListPaging(result.getResult());
                    } else {
                        manageShopList(result.getResult());
                    }
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(requireActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    setRefreshing(false, true);
                    showError(result.getStatusCode());
                        showEmptyResult(true);
                }
            } else {
                setRefreshing(false, true);
                showEmptyResult(true);
            }
        }, searchText, mLastLocation, pageNum, SessionManager.getInstance().getSessionToken());
    }


    private void setRefreshing(boolean isRefreshing, boolean showOnTop) {
        ViewGroup.LayoutParams layoutParams = binding.refresh.getLayoutParams();
        if(getContext() == null) {  //prevents crashes in low internet situations where users navigates away leaving a loading in progress
            return;
        }
        if (isRefreshing) {
            if(binding.refresh.isRefreshing()) {
                return;
            }
            if (showOnTop) {
                float density = getResources().getDisplayMetrics().density;
                int heightInDp = 80;
                layoutParams.height = (int) (heightInDp * density + 0.5f);
                binding.refresh.setLayoutParams(layoutParams);
                binding.recyclerViewShops.setVisibility(View.GONE);
            } else {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                binding.refresh.setLayoutParams(layoutParams);
                binding.recyclerViewShops.setVisibility(View.VISIBLE);
            }
            binding.refresh.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

            handlerUi.postDelayed(() -> {
                binding.refresh.setRefreshing(true);
            }, 50);

        } else {
            if (layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                binding.refresh.setLayoutParams(layoutParams);
                binding.recyclerViewShops.setVisibility(View.VISIBLE);
            }

            if(isFinal) {
                binding.refresh.setDirection(SwipyRefreshLayoutDirection.TOP);
            } else {
                binding.refresh.setDirection(SwipyRefreshLayoutDirection.BOTH);
            }

            binding.refresh.setRefreshing(false);
        }
    }

    private synchronized void manageShopList(ShopList shopList) {
//        adapter = new StoreLocatorListTileAdapter(MapperConsumer.shopItemConsumerListTilesFromShopItemList(AppMockUtils.fakeStoreLocatorSearchTaskResponse()), this);
        adapter = new StoreLocatorListTileAdapter(MapperConsumer.shopItemConsumerListTilesFromShopItemList(shopList.getShops()), this, false);
        adapter.setOnResultsPublishedCallback(callback);
        binding.recyclerViewShops.setAdapter(adapter);
        if(isFinal) {
            showEmptyResult(shopList.getShops().isEmpty());
        }
    }

    private void showEmptyResult(boolean isShow) {
        if(isShow && searchText.length() >= 3) {
            binding.shopListEmpty.setVisibility(View.VISIBLE);
        } else if (searchText.length() < 3) {
            binding.shopListEmpty.setVisibility(View.GONE);
            binding.typeSomethingHintLayout.setVisibility(View.VISIBLE);
        } else {
            binding.shopListEmpty.setVisibility(View.GONE);
            binding.typeSomethingHintLayout.setVisibility(View.INVISIBLE);
        }
    }

    private synchronized void manageShopListPaging(ShopList shopList) {
//        adapter.addItems(MapperConsumer.shopItemConsumerListTilesFromShopItemList(AppMockUtils.fakeStoreLocatorSearchTaskResponse()));
        adapter.addItems(MapperConsumer.shopItemConsumerListTilesFromShopItemList(shopList.getShops()));
        int itemsInList = adapter.getItemCount();
        if(isFinal && itemsInList == 1) {
            showEmptyResult(shopList.getShops().isEmpty());
        }
    }

    void setRecyclerView() {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.recyclerViewShops.setLayoutManager(layoutManagerPanel);
        adapter = new StoreLocatorListTileAdapter(new ArrayList<>(), this, false);
        adapter.setOnResultsPublishedCallback(callback);
        binding.recyclerViewShops.setAdapter(adapter);
    }

    @Override
    public void onTap(ListTile item) {
        Log.d(TAG, "Item tapped: " + item.getTitle());
    }

    @Override
    public void onLastItemVisible() {
        if(!isFinal && searchText != null && searchText.length() >= 3) {
            setRefreshing(true, false);
            performSearch(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BCM_LOCATION, mLastLocation);
    }
}
