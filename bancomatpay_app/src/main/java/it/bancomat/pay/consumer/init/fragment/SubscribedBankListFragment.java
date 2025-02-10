package it.bancomat.pay.consumer.init.fragment;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PRE_LOGIN_SELECT_BANK;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_SEARCH_TEXT;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.List;

import it.bancomat.pay.consumer.activation.adapter.DataBankListAdapter;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.utilities.NavHelper;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentSubscribedBankListBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class SubscribedBankListFragment extends Fragment implements DataBankListAdapter.InteractionListener {
    private FragmentSubscribedBankListBinding binding;
    private InitViewModel initViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubscribedBankListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel = new ViewModelProvider(requireActivity()).get(InitViewModel.class);

        binding.activationBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        List<DataBank> dataBank = DataBankManager.getSubscribedDataBankList();

        DataBankListAdapter adapter = new DataBankListAdapter(dataBank, this);
        binding.activationBankList.setAdapter(adapter);

        binding.editTextSearch.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                adapter.getFilter().filter(text);

                if (text.length() > 0) {
                    if (binding.cancelButtonSearch.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, AnimationFadeUtil.DEFAULT_DURATION);
                    }
                } else {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
                }
            }
        };
        binding.editTextSearch.addTextChangedListener(textWatcher);

        binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
            binding.editTextSearch.getText().clear();
            AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
        }));
    }

    @Override
    public void onListBankInteraction(DataBank dataBank) {
        BancomatPayApiInterface.Factory.getInstance().storeBankUuidChoosed(dataBank.getBankUUID());
        HashMap<String, String> mapEventParams = new HashMap<>();
        if (!binding.editTextSearch.getText().toString().isEmpty()) {
            mapEventParams.put(PARAM_SEARCH_TEXT, binding.editTextSearch.getText().toString());
        }
        CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_PRE_LOGIN_SELECT_BANK, mapEventParams, true);

        initViewModel.userMonitoring(dataBank.getBankUUID(),
                UserMonitoringConstants.ACTIVATION_TAG,
                UserMonitoringConstants.ACTIVATION_BANK_SELECTED,
                "");
        NavHelper.navigate(requireActivity(), ActivableBankListFragmentDirections.actionActivableBankListFragmentToGoToHomeBankingFragment());
    }
}
