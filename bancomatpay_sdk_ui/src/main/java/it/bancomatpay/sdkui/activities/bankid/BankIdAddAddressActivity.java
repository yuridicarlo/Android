package it.bancomatpay.sdkui.activities.bankid;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.Address;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmBankIdAddAddressBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.ADDRESS_DATA_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.CAN_ADD_BILLING_ADDRESS;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.CAN_ADD_SHIPPING_ADDRESS;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.CAN_EDIT_BILLING_ADDRESS;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.CAN_EDIT_SHIPPING_ADDRESS;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.IS_ADDRESS_LIST_UPDATED;

public class BankIdAddAddressActivity extends GenericErrorActivity {

    private static final String TAG = BankIdAddAddressActivity.class.getSimpleName();

    private BankIdAddress addressData;
    private ActivityBcmBankIdAddAddressBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(BankIdAddAddressActivity.class.getSimpleName());
        binding = ActivityBcmBankIdAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        binding.toolbarSimple.setOnClickRightImageListener(v -> showDialogDeleteAddress());
        binding.toolbarSimple.setRightImageVisibility(false);
        binding.buttonSaveAddress.setOnClickListener(new CustomOnClickListener(v -> onClickButtonSaveAddress()));

        addressData = (BankIdAddress) getIntent().getSerializableExtra(ADDRESS_DATA_EXTRA);
        if (addressData != null) {
            binding.toolbarSimple.setRightImageVisibility(true);
            binding.textTitle.setText(getString(R.string.bank_id_title_modify_address));

            boolean canEditBillingAddress = getIntent().getBooleanExtra(CAN_EDIT_BILLING_ADDRESS, true);
            boolean canEditShippingAddress = getIntent().getBooleanExtra(CAN_EDIT_SHIPPING_ADDRESS, true);
            if (addressData.getAddressType() == BankIdAddress.EBankIdAddressType.SHIPPING) {
	            if (!canEditBillingAddress) {
                    binding.checkboxAddressBilling.setEnabled(false);
	            }
            } else if (addressData.getAddressType() == BankIdAddress.EBankIdAddressType.BILLING) {
	            if (!canEditShippingAddress) {
                    binding.checkboxAddressExpedition.setEnabled(false);
	            }
            } else if (addressData.getAddressType() == BankIdAddress.EBankIdAddressType.BOTH) {
                if (!canEditBillingAddress) {
                    binding.checkboxAddressBilling.setEnabled(false);
                }
                if (!canEditShippingAddress) {
                    binding.checkboxAddressExpedition.setEnabled(false);
                }
            }
        } else {
            binding.toolbarSimple.setRightImageVisibility(false);

            boolean canAddBillingAddress = getIntent().getBooleanExtra(CAN_ADD_BILLING_ADDRESS, true);
            if (!canAddBillingAddress) {
                binding.checkboxAddressBilling.setEnabled(false);
                binding.imageStarBilling.setClickable(true);
               // binding.imageStarBilling.setImageResource(R.drawable.star_grey);

                binding.checkboxAddressExpedition.setChecked(true);
            }
            boolean canAddShippingAddress = getIntent().getBooleanExtra(CAN_ADD_SHIPPING_ADDRESS, true);
            if (!canAddShippingAddress) {
                binding.checkboxAddressExpedition.setEnabled(false);
                binding.imageStarExpedition.setClickable(true);
               // binding.imageStarExpedition.setImageResource(R.drawable.star_grey);

                binding.checkboxAddressBilling.setChecked(true);
            }
        }

        initLayout();

        KeyboardVisibilityEvent.setEventListener(
                this, this, isOpen -> {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.buttonSaveAddress.getLayoutParams();
                    if (isOpen) {
                        binding.buttonSaveAddress.setBackgroundResource(R.drawable.button_square_background_state_list);
                        params.leftMargin = 0;
                        params.rightMargin = 0;
                        params.bottomMargin = 0;
                    } else {
                        binding.buttonSaveAddress.setBackgroundResource(R.drawable.button_round_background_state_list);
                        params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        params.bottomMargin = (int) getResources().getDimension(R.dimen.size_25);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void initLayout() {

        if (addressData != null) {

            binding.editTextName.setText(addressData.getCareOf());
            binding.editTextAddress.setText(addressData.getAddress().getStreet());
            binding.editTextCity.setText(addressData.getAddress().getCity());
            binding.editTextProvince.setText(addressData.getAddress().getProvince());
            binding.editTextCap.setText(addressData.getAddress().getPostalCode());
            binding.editTextCountry.setText(addressData.getCountry());

            binding.editTextName.setDeleteVisibility(false);
            binding.editTextAddress.setDeleteVisibility(false);
            binding.editTextCity.setDeleteVisibility(false);
            binding.editTextProvince.setDeleteVisibility(false);
            binding.editTextCap.setDeleteVisibility(false);
            binding.editTextCountry.setDeleteVisibility(false);

            if (addressData.getAddressType() == BankIdAddress.EBankIdAddressType.SHIPPING) {
                binding.checkboxAddressExpedition.setChecked(true);
            } else if (addressData.getAddressType() == BankIdAddress.EBankIdAddressType.BILLING) {
                binding.checkboxAddressBilling.setChecked(true);
            } else if (addressData.getAddressType() == BankIdAddress.EBankIdAddressType.BOTH) {
                binding.checkboxAddressExpedition.setChecked(true);
                binding.checkboxAddressBilling.setChecked(true);
            }

            if (addressData.isDefaultShippingAddress()) {
                binding.imageStarExpedition.setImageResource(R.drawable.star_blue);
            }
            if (addressData.isDefaultBillingAddress()) {
                binding.imageStarBilling.setImageResource(R.drawable.star_blue);
            }

        } else {
            binding.editTextName.setHintTextSize(13);
            binding.editTextAddress.setHintTextSize(13);
            binding.editTextCap.setHintTextSize(13);
            binding.editTextCountry.setHintTextSize(13);
            binding.editTextCity.setHintTextSize(13);
            binding.editTextProvince.setHintTextSize(13);
        }

        binding.editTextName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        binding.editTextAddress.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        binding.editTextCity.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        binding.editTextProvince.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        binding.editTextProvince.setMaxLength(2);
        binding.editTextCap.setInputType(InputType.TYPE_CLASS_NUMBER);
        binding.editTextCap.setMaxLength(5);
        binding.editTextCountry.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        TextWatcher textWatcherName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.buttonSaveAddress.setEnabled(isFormFilled());
                binding.editTextName.setDeleteVisibility(!binding.editTextName.getText().isEmpty());
                if (binding.editTextName.hasError()) {
                    binding.editTextName.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        };
        View.OnFocusChangeListener focusListenerName = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextName.setDeleteVisibility(true);
                }
                if (binding.editTextName.hasError()) {
                    binding.editTextName.clearError();
                }
            } else {
                binding.editTextName.setDeleteVisibility(false);
            }
        };
        binding.editTextName.addTextChangedListener(textWatcherName);
        binding.editTextName.setCustomOnFocusChangeListener(focusListenerName);

        TextWatcher textWatcherAddress = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.buttonSaveAddress.setEnabled(isFormFilled());
                binding.editTextAddress.setDeleteVisibility(!binding.editTextAddress.getText().isEmpty());
                if (binding.editTextAddress.hasError()) {
                    binding.editTextAddress.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        };
        View.OnFocusChangeListener focusListenerAddress = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextAddress.setDeleteVisibility(true);
                }
                if (binding.editTextAddress.hasError()) {
                    binding.editTextAddress.clearError();
                }
            } else {
                binding.editTextAddress.setDeleteVisibility(false);
            }
        };
        binding.editTextAddress.addTextChangedListener(textWatcherAddress);
        binding.editTextAddress.setCustomOnFocusChangeListener(focusListenerAddress);

        TextWatcher textWatcherCity = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.buttonSaveAddress.setEnabled(isFormFilled());
                binding.editTextCity.setDeleteVisibility(!binding.editTextCity.getText().isEmpty());
                if (binding.editTextCity.hasError()) {
                    binding.editTextCity.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        };
        View.OnFocusChangeListener focusListenerCity = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextCity.setDeleteVisibility(true);
                }
                if (binding.editTextCity.hasError()) {
                    binding.editTextCity.clearError();
                }
            } else {
                binding.editTextCity.setDeleteVisibility(false);
            }
        };
        binding.editTextCity.addTextChangedListener(textWatcherCity);
        binding.editTextCity.setCustomOnFocusChangeListener(focusListenerCity);

        View.OnFocusChangeListener focusListenerProvince = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextProvince.setDeleteVisibility(true);
                }
                if (binding.editTextProvince.hasError()) {
                    binding.editTextProvince.clearError();
                }
            } else {
                binding.editTextProvince.setDeleteVisibility(false);
            }
        };
        binding.editTextProvince.setCustomOnFocusChangeListener(focusListenerProvince);

        TextWatcher textWatcherCap = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.buttonSaveAddress.setEnabled(isFormFilled());
                binding.editTextCap.setDeleteVisibility(!binding.editTextCap.getText().isEmpty());
                if (binding.editTextCap.hasError()) {
                    binding.editTextCap.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        };
        View.OnFocusChangeListener focusListenerCap = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextCap.setDeleteVisibility(true);
                }
                if (binding.editTextCap.hasError()) {
                    binding.editTextCap.clearError();
                }
            } else {
                binding.editTextCap.setDeleteVisibility(false);
            }
        };
        binding.editTextCap.addTextChangedListener(textWatcherCap);
        binding.editTextCap.setCustomOnFocusChangeListener(focusListenerCap);

        TextWatcher textWatcherCountry = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.buttonSaveAddress.setEnabled(isFormFilled());
                binding.editTextCountry.setDeleteVisibility(!binding.editTextCountry.getText().isEmpty());
                if (binding.editTextCountry.hasError()) {
                    binding.editTextCountry.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        };
        View.OnFocusChangeListener focusListenerCountry = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextCountry.setDeleteVisibility(true);
                }
                if (binding.editTextCountry.hasError()) {
                    binding.editTextCountry.clearError();
                }
            } else {
                binding.editTextCountry.setDeleteVisibility(false);
            }
        };
        binding.editTextCountry.addTextChangedListener(textWatcherCountry);
        binding.editTextCountry.setCustomOnFocusChangeListener(focusListenerCountry);

        binding.imageStarExpedition.setOnClickListener(new CustomOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.star_grey);
            if (binding.imageStarExpedition.getDrawable().getConstantState() != null && drawable != null
                    && binding.imageStarExpedition.getDrawable().getConstantState().equals(drawable.getConstantState())
                    && binding.checkboxAddressExpedition.isChecked()) {
                binding.imageStarExpedition.setImageResource(R.drawable.star_blue);
            } else {
                binding.imageStarExpedition.setImageResource(R.drawable.star_grey);
            }
        }));

        binding.imageStarBilling.setOnClickListener(new CustomOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.star_grey);
            if (binding.imageStarBilling.getDrawable().getConstantState() != null && drawable != null
                    && binding.imageStarBilling.getDrawable().getConstantState().equals(drawable.getConstantState())
                  && binding.checkboxAddressBilling.isChecked()) {
                binding.imageStarBilling.setImageResource(R.drawable.star_blue);
            } else {
                binding.imageStarBilling.setImageResource(R.drawable.star_grey);
            }
        }));

        binding.checkboxAddressExpedition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.imageStarExpedition.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_grey));
            binding.buttonSaveAddress.setEnabled(isFormFilled());
        });

        binding.checkboxAddressBilling.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.imageStarBilling.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_grey));
            binding.buttonSaveAddress.setEnabled(isFormFilled());
        });
        binding.buttonSaveAddress.setEnabled(isFormFilled());

    }

    private boolean isFormFilled() {
        return binding.editTextAddress.getText().length() > 0
                && binding.editTextCity.getText().length() > 0
                && binding.editTextProvince.getText().length() > 0
                && binding.editTextCap.getText().length() > 0
                && binding.editTextCountry.getText().length() > 0
                && (binding.checkboxAddressExpedition.isChecked() || binding.checkboxAddressBilling.isChecked());
    }

    private void onClickButtonSaveAddress(){
        HashMap<String, BankIdAddress> bankIdAddressMap = ApplicationModel.getInstance().getBankIdAddressMap();

        if (addressData == null) {
            addressData = new BankIdAddress();
        }
        bankIdAddressMap.remove(this.addressData.getAddressId());

        Drawable starBlue = ContextCompat.getDrawable(this, R.drawable.star_blue);
        if (binding.imageStarBilling.getDrawable().getConstantState() != null && starBlue != null
                && binding.imageStarBilling.getDrawable().getConstantState().equals(starBlue.getConstantState())) {
            for (BankIdAddress item : bankIdAddressMap.values()) {
                item.setDefaultBillingAddress(false);
            }
        }
        if (binding.imageStarExpedition.getDrawable().getConstantState() != null && starBlue != null
                && binding.imageStarExpedition.getDrawable().getConstantState().equals(starBlue.getConstantState())) {
            for (BankIdAddress item : bankIdAddressMap.values()) {
                item.setDefaultShippingAddress(false);
            }
        }

        updateBankIdAddress();

        bankIdAddressMap.put(this.addressData.getAddressId(), this.addressData);

        String email = ApplicationModel.getInstance().getBankIdContactsData() != null ?
                ApplicationModel.getInstance().getBankIdContactsData().getEmail() : "";

        BankIdContactsData newContactsData = new BankIdContactsData();
        newContactsData.setEmail(email);
        newContactsData.setBankIdAddress(new ArrayList<>(bankIdAddressMap.values()));
        ApplicationModel.getInstance().setBankIdContactsData(newContactsData);

        LoaderHelper.showLoader(this);
        BancomatSdkInterface.Factory.getInstance().doSetBankIdContacts(this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            CustomLogger.d(TAG, "doSetBankIdContacts successful!");

                            Intent intentResult = new Intent();
                            intentResult.putExtra(IS_ADDRESS_LIST_UPDATED, true);
                            setResult(RESULT_OK, intentResult);
                            finish();

                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                },
                email,
                ApplicationModel.getInstance().getBankIdContactsData().getBankIdAddresses(),
                SessionManager.getInstance().getSessionToken());

    }

    private void updateBankIdAddress() {

        addressData.setCareOf(binding.editTextName.getText());
        addressData.setCountry(binding.editTextCountry.getText());

        Address address = new Address();
        address.setCity(binding.editTextCity.getText());
        address.setProvince(binding.editTextProvince.getText());
        address.setStreet(binding.editTextAddress.getText());
        address.setPostalCode(binding.editTextCap.getText());

        addressData.setAddress(address);
        if (binding.checkboxAddressBilling.isChecked() && binding.checkboxAddressExpedition.isChecked()) {
            addressData.setAddressType(BankIdAddress.EBankIdAddressType.BOTH);
        } else if (binding.checkboxAddressBilling.isChecked()) {
            addressData.setAddressType(BankIdAddress.EBankIdAddressType.BILLING);
        } else if (binding.checkboxAddressExpedition.isChecked()) {
            addressData.setAddressType(BankIdAddress.EBankIdAddressType.SHIPPING);
        } else {
            addressData.setAddressType(null);
        }

    boolean isDefaultBilling = false;
    boolean isDefaultShipping = false;
        Drawable starBlue = ContextCompat.getDrawable(this, R.drawable.star_blue);
        if (binding.imageStarBilling.getDrawable().getConstantState() != null && starBlue != null
                && binding.imageStarBilling.getDrawable().getConstantState().equals(starBlue.getConstantState())) {
            isDefaultBilling = true;
        }
        if (binding.imageStarExpedition.getDrawable().getConstantState() != null && starBlue != null
                && binding.imageStarExpedition.getDrawable().getConstantState().equals(starBlue.getConstantState())) {
            isDefaultShipping = true;
        }

        addressData.setDefaultBillingAddress(isDefaultBilling);
        addressData.setDefaultShippingAddress(isDefaultShipping);
    }

    private void showDialogDeleteAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(getString(R.string.bank_id_remove_address_message))
                .setPositiveButton(R.string.yes, (dialog, id) -> {

                    String email = ApplicationModel.getInstance().getBankIdContactsData() != null ?
                            ApplicationModel.getInstance().getBankIdContactsData().getEmail() : "";

                    HashMap<String, BankIdAddress> bankIdAddressMap = ApplicationModel.getInstance().getBankIdAddressMap();
                    bankIdAddressMap.remove(this.addressData.getAddressId());
                    BankIdContactsData newContactsData = new BankIdContactsData();
                    newContactsData.setEmail(email);
                    newContactsData.setBankIdAddress(new ArrayList<>(bankIdAddressMap.values()));
                    ApplicationModel.getInstance().setBankIdContactsData(newContactsData);

                    LoaderHelper.showLoader(this);
                    BancomatSdkInterface.Factory.getInstance().doSetBankIdContacts(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

                                        CustomLogger.d(TAG, "doSetBankIdContacts successful!");

                                        Intent intentResult = new Intent();
                                        intentResult.putExtra(IS_ADDRESS_LIST_UPDATED, true);
                                        setResult(RESULT_OK, intentResult);
                                        finish();

                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                    } else {
                                        showError(result.getStatusCode());
                                    }
                                }
                            },
                            email,
                            ApplicationModel.getInstance().getBankIdContactsData().getBankIdAddresses(),
                            SessionManager.getInstance().getSessionToken());

                })
                .setNegativeButton(R.string.no, null)
                .setCancelable(false);
        builder.show();
    }

}
