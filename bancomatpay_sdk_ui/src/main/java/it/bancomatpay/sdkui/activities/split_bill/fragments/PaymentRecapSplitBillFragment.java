package it.bancomatpay.sdkui.activities.split_bill.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.SplitBillContactsRecapAdapter;
import it.bancomatpay.sdkui.databinding.FragmentPaymentRecapSplitBillBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;

public class PaymentRecapSplitBillFragment extends GenericErrorFragment {
    private static final String TAG = PaymentRecapSplitBillFragment.class.getSimpleName();

    private FragmentPaymentRecapSplitBillBinding binding;

    private SplitBillViewModel splitBillViewModel;

    private SplitBillContactsRecapAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentRecapSplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splitBillViewModel = new ViewModelProvider(requireActivity()).get(SplitBillViewModel.class);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> NavHelper.popBackStack(requireActivity()));

        configureCard();

        adapter = new SplitBillContactsRecapAdapter("Me", splitBillViewModel.getSelectedContactsList());
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        binding.selectedContactsRV.setAdapter(adapter);
        binding.selectedContactsRV.setLayoutManager(llm);

        binding.confirmButton.setOnClickListener(new CustomOnClickListener((r)->{
            if(splitBillViewModel.showAlert()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(R.string.warning_title);
                builder.setMessage(R.string.split_bill_contacts_not_bpay_alert_message)
                        .setCancelable(false);

                builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                    dialog.dismiss();
                    navigate();
                });

                builder.show();
            } else {
                navigate();
            }
        }));
    }

    private void navigate() {
        NavHelper.navigate(requireActivity(), PaymentRecapSplitBillFragmentDirections.actionPaymentRecapSplitBillFragmentToPaymentResultSplitBillFragment());
    }

    private void configureCard() {
        binding.causal.setText(splitBillViewModel.getCausal());
        binding.description.setText(splitBillViewModel.getDescription());
    }
}
