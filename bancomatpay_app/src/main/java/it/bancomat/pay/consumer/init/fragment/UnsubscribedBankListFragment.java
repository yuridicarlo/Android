package it.bancomat.pay.consumer.init.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.adapter.DataBankUnsubscribedListAdapter;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomatpay.consumer.databinding.FragmentUnsubscribedBankListBinding;

public class UnsubscribedBankListFragment extends Fragment implements DataBankUnsubscribedListAdapter.UnsubscribedBankClickListener {
    private FragmentUnsubscribedBankListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUnsubscribedBankListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.activationBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        List<DataBank> dataBank = DataBankManager.getUnsubscribedDataBankList();

        DataBankUnsubscribedListAdapter adapter = new DataBankUnsubscribedListAdapter(dataBank, this);
        binding.activationBankList.setAdapter(adapter);
    }

    @Override
    public void onBankInteraction(DataBank dataBank) {
        if (!TextUtils.isEmpty(dataBank.getLinkStore())) {
            ActivationFlowManager.goToPlayStore(requireActivity(), dataBank);
        }
    }
}
