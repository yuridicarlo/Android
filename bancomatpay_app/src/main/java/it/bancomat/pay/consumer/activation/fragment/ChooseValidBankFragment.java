package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.bancomat.pay.consumer.activation.adapter.DataBankListAdapter;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentChooseValidBankBinding;

public class ChooseValidBankFragment extends Fragment implements DataBankListAdapter.InteractionListener  {

    private FragmentChooseValidBankBinding binding;

    private static final String ARG_BANKS = "ARG_BANKS";
    private static final String ARG_BANK_LABEL = "ARG_BANK_LABEL";

    private ArrayList<DataBank> bankList;
    private String bankLabel;
    private InteractionListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bankList = (ArrayList<DataBank>) getArguments().getSerializable(ARG_BANKS);
            bankLabel = getArguments().getString(ARG_BANK_LABEL);
        }
    }

    public static ChooseValidBankFragment newInstance(List<DataBank> bankList, String bankLabel) {
        ChooseValidBankFragment fragment = new ChooseValidBankFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BANKS, (Serializable) bankList);
        args.putString(ARG_BANK_LABEL, bankLabel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChooseValidBankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.chooseValidBankLabel1.setText(getString(R.string.choose_valid_bank_fragment_text_1,bankLabel));

        binding.activableBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<DataBank> dataBanks = filterSubscribedBankList(bankList);

        if (dataBanks.isEmpty()) {
            dataBanks = filterUnsubscribedBankList(bankList);
            binding.chooseValidBankLabel1.setText(getString(R.string.choose_valid_bank_unsubscribed_fragment_text_1));
            binding.chooseValidBankLabel2.setText(getString(R.string.choose_valid_bank_unsubscribed_fragment_text_2));
        }

        //dataBanks.add(new DataBank());

        DataBankListAdapter adapter = new DataBankListAdapter(dataBanks,  this);
        binding.activableBankList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            listener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onListBankInteraction(DataBank dataBank) {
        if (listener != null) {
            listener.onBankClicked(dataBank);
        }
    }

    /*@Override
    public void onListLastRowInteraction() {
        ActivationFlowManager.goToNoBcmPayBankList(getActivity());
    }*/

    private ArrayList<DataBank> filterSubscribedBankList(ArrayList<DataBank> bankList) {
        ArrayList<DataBank> filteredList = new ArrayList<>();
        Map<String, DataBank> subscribedBankMap = DataBankManager.getSubscribedDataBankMap();
        for (DataBank item : bankList) {
            if (subscribedBankMap.containsKey(item.getBankUUID())) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private ArrayList<DataBank> filterUnsubscribedBankList(ArrayList<DataBank> bankList) {
        ArrayList<DataBank> filteredList = new ArrayList<>();
        Map<String, DataBank> subscribedBankMap = DataBankManager.getUnsubscribedDataBankMap();
        for (DataBank item : bankList) {
            if (subscribedBankMap.containsKey(item.getBankUUID())) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    public interface InteractionListener {
        void onBankClicked(DataBank dataBank);
    }

}
