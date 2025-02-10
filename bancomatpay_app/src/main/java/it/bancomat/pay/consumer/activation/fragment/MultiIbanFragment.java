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

import java.util.List;

import it.bancomat.pay.consumer.activation.adapter.MultiIbanAdapter;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomatpay.consumer.databinding.FragmentMultiIbanBinding;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class MultiIbanFragment extends Fragment implements MultiIbanAdapter.IbanListClickListener {

    public static final String DATA_BANK_EXTRA = "DATA_BANK_EXTRA";

    private FragmentMultiIbanBinding binding;

    private DataBank dataBank;
    private InteractionListener listener;

    public static MultiIbanFragment newInstance(DataBank dataBank) {
        MultiIbanFragment fragment = new MultiIbanFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATA_BANK_EXTRA, dataBank);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataBank = (DataBank) getArguments().getSerializable(DATA_BANK_EXTRA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMultiIbanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<InstrumentData> instrument = dataBank.getInstrument();
        MultiIbanAdapter adapter = new MultiIbanAdapter(instrument, dataBank.getLogoSearch(), this);
        binding.multiIbanBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.multiIbanBankList.setAdapter(adapter);

        binding.buttonMultiIbanNext.setEnabled(false);

        binding.buttonMultiIbanNext.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null) {
                listener.onProceedSelectIbanButtonClicked(dataBank);
            }
        }));
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
                    + " must implement MultiIbanFragment.InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onIbanClicked(String iban) {
        if (listener != null) {
            listener.onBankIbanSelected(iban);
            binding.buttonMultiIbanNext.setEnabled(true);
        }
    }

    public interface InteractionListener {
        void onBankIbanSelected(String iban);

        void onProceedSelectIbanButtonClicked(DataBank dataBank);
    }

}

