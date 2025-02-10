package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.bancomatpay.consumer.databinding.FragmentInsertPhoneNumberBinding;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumberManager;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.widgets.LabelTelephone;

public class InsertPhoneFragment extends Fragment implements LabelTelephone.LabelListener {

    public static final String DEFAULT_PREFIX = "+39";
    public static final String DEFAULT_COUNTRY_CODE = "IT";

    private static final String PHONE_NUMBER_STUB = "0000000000";
    private static final int PHONE_LENGTH_DEFAULT = 15;

    @Override
    public void onResume() {
        onNumberInserted(binding.insertNumberEditText.getPhoneFormatted());
        super.onResume();
    }

    private FragmentInsertPhoneNumberBinding binding;

    private InteractionListener listener;
    private DataPrefixPhoneNumber mDefaultData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInsertPhoneNumberBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.keyboardCustom.setKeyboardType(KeyboardType.PHONE_KEYBOARD);
        binding.keyboardCustom.hideNumPadComma();
        binding.keyboardCustom.setKeyboardListener(binding.insertNumberEditText);
        //	binding.insertNumberEditText.setOnPrefixSelectedListener();
        binding.insertNumberEditText.setMaxLength(PHONE_LENGTH_DEFAULT);
        binding.insertNumberEditText.setInitialText("");

        binding.insertNumberEditText.setLabelListener(this);
        binding.cancelButtonInsertNumber.setOnClickListener(v -> binding.insertNumberEditText.onDeleteCharacter());

        binding.cancelButtonInsertNumber.setOnLongClickListener(v -> {
            binding.insertNumberEditText.onDeleteAllText();
            // cancelButtonInsertNumber.setVisibility(View.INVISIBLE);
            return true;
        });

        //  if (AppBancomatDataManager.getInstance().getDataPrefixPhoneNumber() != null) {
        if (FullStackSdkDataManager.getInstance().getDataPrefixPhoneNumber() != null) {
            //   mDefaultData = AppBancomatDataManager.getInstance().getDataPrefixPhoneNumber();
            mDefaultData = FullStackSdkDataManager.getInstance().getDataPrefixPhoneNumber();
        } else {
            // mDefaultData = DataPrefixPhoneNumberManager.getData(DEFAULT_COUNTRY_CODE);
            mDefaultData = DataPrefixPhoneNumberManager.getData(DEFAULT_COUNTRY_CODE);
        }

        if (mDefaultData != null) {
            binding.insertNumberEditText.setPrefixItem(mDefaultData);
        }
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
                    + " must implement InsertPhoneFragment.InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onNumberInserted(String number) {
        if (isResumed() && isVisible()) {
            if (PhoneNumber.isValidNumber(binding.insertNumberEditText.getPhoneFormatted())
                    || number.equals(PHONE_NUMBER_STUB)) {
                binding.keyboardCustom.post(() -> binding.keyboardCustom.setButtonNextEnabled(true));
                binding.keyboardCustom.getButtonContinueOn().setOnClickListener(new CustomOnClickListener(v -> {
                    if (listener != null) {
                        mDefaultData = binding.insertNumberEditText.getDataPrefixPhoneNumber();
                        if (mDefaultData != null) {
                            //  AppBancomatDataManager.getInstance().putDataPrefixPhoneNumber(mDefaultData);
                            FullStackSdkDataManager.getInstance().putDataPrefixPhoneNumber(mDefaultData);
                            //    AppBancomatDataManager.getInstance().putPrefixCountryCode(mDefaultData.getPrefix());
                            FullStackSdkDataManager.getInstance().putPrefixCountryCode(mDefaultData.getPrefix());
                        } else {
                            //  AppBancomatDataManager.getInstance().putDataPrefixPhoneNumber(DataPrefixPhoneNumberManager.getData(DEFAULT_COUNTRY_CODE));
                            FullStackSdkDataManager.getInstance().putDataPrefixPhoneNumber(DataPrefixPhoneNumberManager.getData(DEFAULT_COUNTRY_CODE));
                            //       AppBancomatDataManager.getInstance().putPrefixCountryCode(DEFAULT_PREFIX);
                            FullStackSdkDataManager.getInstance().putPrefixCountryCode(DEFAULT_PREFIX);
                        }

                        listener.onPhoneInserted(binding.insertNumberEditText.getPhoneFormatted(), binding.insertNumberEditText.getValue());

                    }
                }));
            } else {
                binding.keyboardCustom.post(() -> binding.keyboardCustom.setButtonNextEnabled(false));
            }
        }
    }

    @Override
    public void onNumberDeleted(boolean deleteAll) {
        if (isResumed() && isVisible()) {
            if (deleteAll) {
                binding.keyboardCustom.reset();
            } else {
                binding.keyboardCustom.deleteCharacter();
            }
        }
    }

    public interface InteractionListener {
        void onPhoneInserted(String phoneFormatted, String phone);
    }

}

