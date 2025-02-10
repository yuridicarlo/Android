package it.bancomatpay.sdkui.activities.split_bill.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.split_bill.SplitBillActivity;
import it.bancomatpay.sdkui.databinding.FragmentSelectAmountSplitBillBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;
import it.bancomatpay.sdkui.widgets.LabelPaymentAmount;

public class SelectAmountSplitBillFragment extends GenericErrorFragment implements LabelPaymentAmount.LabelListener{

    private static final String TAG = SelectAmountSplitBillFragment.class.getSimpleName();

    private FragmentSelectAmountSplitBillBinding binding;

    private SplitBillViewModel splitBillViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectAmountSplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splitBillViewModel = new ViewModelProvider(requireActivity()).get(SplitBillViewModel.class);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> NavHelper.popBackStack(requireActivity()));

        ((SplitBillActivity) requireActivity()).clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

        binding.getMoneyKeyboard.setKeyboardListener(binding.getMoneyLabel);
        binding.getMoneyKeyboard.setKeyboardType(KeyboardType.DEFAULT);
        binding.getMoneyLabel.setLabelListener(this);


        binding.cancelButtonInsertNumber.setOnClickListener(new CustomOnClickListener(v -> onDeleteNumber()));


        TextWatcher textWatcherTransferReason = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                splitBillViewModel.setCausal(s.toString());
                refreshButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        TextWatcher textWatcherTransferDescription = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                splitBillViewModel.setDescription(s.toString());
                refreshButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.transferReasonEditText.addTextChangedListener(textWatcherTransferReason);
        binding.transferReasonEditText.requestFocus();
        binding.transferReasonEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.transferReasonError.setVisibility(View.GONE);
                setErrorBackground(false);
            }
        });
        binding.transferReasonEditText.setOnClickListener(v -> {
            binding.transferReasonError.setVisibility(View.GONE);
            setErrorBackground(false);
        });
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.transferReasonEditText, InputMethodManager.SHOW_IMPLICIT);
        }
        binding.transferDescriptionEditText.addTextChangedListener(textWatcherTransferDescription);
        binding.getMoneyKeyboard.getButtonContinueOn().setOnClickListener(new CustomOnClickListener(v -> {
            if (StringUtils.isNullOrEmpty(splitBillViewModel.getCausal())) {
                binding.transferReasonError.setVisibility(View.VISIBLE);
                setErrorBackground(true);
            } else if (splitBillViewModel.getCausal().trim().isEmpty()) {
                binding.transferReasonError.setVisibility(View.VISIBLE);
                setErrorBackground(true);
            } else {
                NavHelper.navigate(requireActivity(), SelectAmountSplitBillFragmentDirections.actionSelectAmountSplitBillFragmentToPaymentRecapSplitBillFragment());
            }
        }));

        setUpCloseKeyboard(imm);

    }

    private void setUpCloseKeyboard(InputMethodManager imm) {
        binding.formLayout.setOnClickListener((v)->{
            if (v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        binding.getMoneyLabel.setOnClickListener((v)->{
            if (v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    private void onDeleteNumber() {
        binding.getMoneyLabel.onDeleteCharacter();
    }

    private void refreshButtonEnable() {
        binding.getMoneyKeyboard.setButtonNextEnabled(splitBillViewModel.isButtonEnabled());
        if(splitBillViewModel.isButtonEnabled()){
            binding.getMoneyKeyboard.getButtonContinueOn().setEnabled(true);
        } else {
            binding.getMoneyKeyboard.getButtonContinueOn().setEnabled(false);
        }
    }

    @Override
    public void onMoneyInserted(int money, boolean isDeletingCharacter) {
        splitBillViewModel.setAmountCents(money);
        refreshButtonEnable();

    }

    private void setErrorBackground(boolean isShow) {
        if (isShow) {
            binding.transferReasonEditText.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.textfield_error_background));
        } else {
            binding.transferReasonEditText.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.texfield));
        }
    }
}
