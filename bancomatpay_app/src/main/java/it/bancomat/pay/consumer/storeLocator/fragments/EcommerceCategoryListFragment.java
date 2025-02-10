package it.bancomat.pay.consumer.storeLocator.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.bancomat.pay.consumer.viewmodel.StoreLocatorViewModel;
import it.bancomatpay.sdkui.adapter.StoreLocatorListTileAdapter;
import it.bancomatpay.sdkui.databinding.FragmentEcommerceCategoryListBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.model.ListTile;
import it.bancomatpay.sdkui.model.ShopCategoryConsumer;
import it.bancomatpay.sdkui.utilities.NavHelper;

public class EcommerceCategoryListFragment extends GenericErrorFragment implements StoreLocatorListTileAdapter.InteractionListener{

    private static final String TAG = EcommerceCategoryListFragment.class.getSimpleName();

    private FragmentEcommerceCategoryListBinding binding;

    private StoreLocatorListTileAdapter adapter;
    private StoreLocatorViewModel storeLocatorViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEcommerceCategoryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storeLocatorViewModel = new ViewModelProvider(requireActivity()).get(StoreLocatorViewModel.class);

        storeLocatorViewModel.getShopCategories().observe(requireActivity(), this::setAdapter);
    }

    void setAdapter(ArrayList<ShopCategoryConsumer> list) {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.categoryList.setLayoutManager(layoutManagerPanel);
        List<ListTile> categories = new ArrayList<>(list);
        adapter = new StoreLocatorListTileAdapter(categories, this, true);
        binding.categoryList.setAdapter(adapter);
    }

    @Override
    public void onTap(ListTile item) {
        Log.d(TAG, "Item tapped: "+item.getTitle());
        String category = ((ShopCategoryConsumer) item).getShopCategory().getUuid();
        NavHelper.navigate(requireActivity(), StoreLocatorListFragmentDirections.actionEcommerceCategoryListFragmentToEcommerceShopListFragment(category));
    }

    @Override
    public void onLastItemVisible() {

    }
}
