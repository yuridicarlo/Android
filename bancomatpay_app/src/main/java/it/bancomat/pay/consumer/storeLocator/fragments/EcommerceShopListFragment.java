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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.viewmodel.StoreLocatorViewModel;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.StoreLocatorListTileAdapter;
import it.bancomatpay.sdkui.databinding.FragmentEcommerceShopListBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.model.ListTile;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.MapperConsumer;
import it.bancomatpay.sdkui.utilities.NavHelper;

public class EcommerceShopListFragment extends GenericErrorFragment implements StoreLocatorListTileAdapter.InteractionListener {

    private static final String TAG = EcommerceShopListFragment.class.getSimpleName();

    private FragmentEcommerceShopListBinding binding;

    private StoreLocatorListTileAdapter adapter;

    private ShopCategory category;
    private String uuid;
    private String searchText;
    private int pageNum = 1;
    private boolean isFinal;
    private String CATEGORY = "category";
    private Handler handlerUi = new Handler();
    private StoreLocatorViewModel storeLocatorViewModel;

    private StoreLocatorListTileAdapter.OnFilterResultsPublished callback = () -> {
        if (adapter.getItemCount() > 1) {
            showEmptyResult(false);
        } else {
            showEmptyResult(true);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEcommerceShopListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.toolbarSimple.setOnClickLeftImageListener(v -> NavHelper.popBackStack(requireActivity()));
        storeLocatorViewModel = new ViewModelProvider(requireActivity()).get(StoreLocatorViewModel.class);
        ((GenericErrorActivity) requireActivity()).setLightStatusBar(view, it.bancomatpay.sdkui.R.color.white_background);
        Bundle args = getArguments();
        if (args != null) {
            uuid = args.getString(CATEGORY);
            Log.d(TAG, "args received: " + uuid);
        } else {
            uuid = (String) savedInstanceState.getSerializable(CATEGORY);
        }

        category = storeLocatorViewModel.getShopCategoryByUuid(uuid);
        if(category == null) {
            requireActivity().onBackPressed();
        }
        binding.categoryTitle.setText(category.getTitle());

        setRecyclerView();

        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), it.bancomatpay.sdkui.R.color.colorAccentBancomat));

        binding.refresh.setOnRefreshListener(direction -> {
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

        setRefreshing(true, true);
        performSearch(false);

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
//                binding.typeSomethingHintLayout.setVisibility(View.INVISIBLE);
                } else {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
                    adapter.clear();
                    binding.recyclerViewShops.setVisibility(View.INVISIBLE);

//                binding.typeSomethingHintLayout.setVisibility(View.VISIBLE);
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);

                }

                if (TextUtils.isEmpty(text) || text.length() < 3) {
                    if (adapter != null) {
                        adapter.getFilter().filter(text);
                    }
                } else {
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
    }

    void setRecyclerView() {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.recyclerViewShops.setLayoutManager(layoutManagerPanel);
        adapter = new StoreLocatorListTileAdapter(new ArrayList<>(), this, false);
        adapter.setShowDetails(false);
        adapter.setOnResultsPublishedCallback(callback);
        binding.recyclerViewShops.setAdapter(adapter);
    }

    void performSearch(boolean isPaging) {
        showEmptyResult(false);
        BancomatPayApiInterface.Factory.getInstance().doGetStoreLocatorSearchOnlineRequest(requireActivity(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    isFinal = result.getResult().isListComplete();
                    if(!isFinal) {
                        pageNum++;
                        if (result.getResult().getShops().isEmpty()) {
                            performSearch(true);
                        } else {
                            binding.refresh.setDirection(SwipyRefreshLayoutDirection.BOTH);
                            setRefreshing(false, true);
                        }
                    } else {
                        binding.refresh.setDirection(SwipyRefreshLayoutDirection.TOP);
                        setRefreshing(false, true);
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
        }, searchText, uuid, pageNum, SessionManager.getInstance().getSessionToken());
    }

    private synchronized void manageShopList(ShopList shopList) {
        adapter = new StoreLocatorListTileAdapter(MapperConsumer.shopItemConsumerListTilesFromShopItemList(shopList.getShops(), category), this, false);
        adapter.setShowDetails(false);
        adapter.setOnResultsPublishedCallback(callback);
        binding.recyclerViewShops.setAdapter(adapter);
        if(isFinal) {
            showEmptyResult(shopList.getShops().isEmpty());
        }
    }
    private synchronized void manageShopListPaging(ShopList shopList) {
        adapter.addItems(MapperConsumer.shopItemConsumerListTilesFromShopItemList(shopList.getShops()));
//        adapter.addItems(MapperConsumer.shopItemConsumerListTilesFromShopItemList(AppMockUtils.fakeStoreLocatorSearchTaskResponse()));
        int itemsInList = adapter.getItemCount();
        if(isFinal && itemsInList == 1) {
            showEmptyResult(shopList.getShops().isEmpty());
        }
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

    private void showEmptyResult(boolean isShow) {
        if(isShow) {
            binding.shopListEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.shopListEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTap(ListTile item) {
        Log.d(TAG, "Item tapped: "+item.getTitle());
    }

    @Override
    public void onLastItemVisible() {
        if(!isFinal) {
            setRefreshing(true, false);
            performSearch(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CATEGORY, uuid);
    }
}
