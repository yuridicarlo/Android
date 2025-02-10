package it.bancomatpay.sdkui.activities.bankid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.AddressAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmBankIdAnagraphicBinding;
import it.bancomatpay.sdkui.flowmanager.BankIdFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.ADD_OR_MODIFY_ADDRESS_REQUEST_CODE;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.IS_ADDRESS_LIST_UPDATED;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class BankIdAnagraphicActivity extends GenericErrorActivity implements AddressAdapter.ItemAddressClickListener {

    private static final String TAG = BankIdAnagraphicActivity.class.getSimpleName();

    private AddressAdapter adapter;
    private ActivityBcmBankIdAnagraphicBinding binding;

    private List<BankIdAddress> addressList;
    private boolean isAddressListUpdated = false;

    private int numberBilling = 0;
    private int numberShipping = 0;
    private boolean isBankIdAccessEnabled = false;


    ActivityResultLauncher<Intent> activityResultLauncherModifyAddress = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(ADD_OR_MODIFY_ADDRESS_REQUEST_CODE,result.getResultCode(),data);
            });
    
    private final View.OnClickListener clickListenerAddAddress = v -> {
        resetFocus();
        BankIdFlowManager.goToAddAddress(this, numberBilling < 3, numberShipping < 3, activityResultLauncherModifyAddress);
    };
    private final View.OnClickListener clickListenerSaveEmail = v -> {

        if (isValidEmail(binding.editTextEmail.getText())) {

            LoaderHelper.showLoader(this);

            List<BankIdAddress> bankIdAddresses = ApplicationModel.getInstance().getBankIdContactsData() != null ?
                    ApplicationModel.getInstance().getBankIdContactsData().getBankIdAddresses() : new ArrayList<>();
            BancomatSdkInterface.Factory.getInstance().doSetBankIdContacts(this, result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                binding.buttonAddAddress.setEnabled(true);

                                CustomLogger.d(TAG, "doSetBankIdContacts success!");
                                hideKeyboard();

                                BankIdContactsData bankIdContactsData = ApplicationModel.getInstance().getBankIdContactsData();
                                if (bankIdContactsData == null) {
                                    bankIdContactsData = new BankIdContactsData();
                                }
                                bankIdContactsData.setEmail(binding.editTextEmail.getText());

                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                binding.buttonAddAddress.setEnabled(false);
                                showError(result.getStatusCode());
                            }
                        }
                    },
                    binding.editTextEmail.getText(),
                    bankIdAddresses,
                    SessionManager.getInstance().getSessionToken());

        } else {
            binding.editTextEmail.setError(true);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBcmBankIdAnagraphicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setActivityName(BankIdAnagraphicActivity.class.getSimpleName());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.refresh.setColorSchemeResources(R.color.colorAccentBancomat);
        binding.refresh.setOnRefreshListener(this::doRequest);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding.buttonAddAddress.setOnClickListener(new CustomOnClickListener(clickListenerAddAddress));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewAddresses.setLayoutManager(layoutManager);

        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewAddresses, false);

        adapter = new AddressAdapter(addressList, this);
        binding.recyclerViewAddresses.setAdapter(adapter);

        binding.editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        binding.editTextEmail.setErrorBackground(R.drawable.edit_text_error_background_rounded);

        TextWatcher textWatcherEmail = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (isValidEmail(text)) {
                    binding.editTextEmail.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
             /*   if (TextUtils.isEmpty(binding.editTextEmail.getText()) && binding.buttonAddAddress.isEnabled()) {
                    binding.buttonAddAddress.setEnabled(false);
                }*/
            }
        };
        View.OnFocusChangeListener focusListenerEmail = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextEmail.setDeleteVisibility(true);
                   /* if (!binding.buttonAddAddress.isEnabled()) {
                        binding.buttonAddAddress.setEnabled(true);
                    }*/
                }
                if (binding.editTextEmail.hasError()) {
                    binding.editTextEmail.clearError();
                }
            } else {
                binding.editTextEmail.setDeleteVisibility(false);
            }
        };
        binding.editTextEmail.addTextChangedListener(textWatcherEmail);
        binding.editTextEmail.setCustomOnFocusChangeListener(focusListenerEmail);

        KeyboardVisibilityEvent.setEventListener(
                this, this, isOpen -> {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.buttonAddAddress.getLayoutParams();
                    if (isOpen) {
                        binding.buttonAddAddress.setOnClickListener(new CustomOnClickListener(clickListenerSaveEmail));
                        binding.buttonAddAddress.setBackgroundResource(R.drawable.button_square_background_state_list);
                        binding.buttonAddAddress.setText(getString(R.string.bank_id_button_email_save));
                        binding.buttonAddAddress.setEnabled(true);
                        params.leftMargin = 0;
                        params.rightMargin = 0;
                        params.bottomMargin = 0;
                    } else {
                        binding.buttonAddAddress.setOnClickListener(new CustomOnClickListener(clickListenerAddAddress));
                        binding.buttonAddAddress.setBackgroundResource(R.drawable.button_round_background_state_list);
                        binding.buttonAddAddress.setText(getString(R.string.bank_id_button_add_address));
                        checkAddressNumberLimit(); //Per abilitare o disabilitare il bottone
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

    @Override
    protected void onResume() {
        super.onResume();
        doRequest();
    }

    private void doRequest() {

        if (isAddressListUpdated) {
            isAddressListUpdated = false;
            binding.refresh.setRefreshing(true);
        } else {
            binding.refresh.setRefreshing(false);
            LoaderHelper.showLoader(this);
        }

        BancomatSdkInterface.Factory.getInstance().doGetBankIdContacts(this, result -> {

            binding.refresh.setRefreshing(false);

            if (result != null) {
                if (result.isSuccess()) {

                    BankIdContactsData resultData = result.getResult();
                    ApplicationModel.getInstance().setBankIdContactsData(resultData);

                    checkAddressNumberLimit();

                    if (!TextUtils.isEmpty(resultData.getEmail())) {
                        binding.editTextEmail.setText(resultData.getEmail());
                        binding.editTextEmail.setDeleteVisibility(false);
                      //  binding.buttonAddAddress.setEnabled(true);
                        isBankIdAccessEnabled = true;
                    } else {
                        binding.buttonAddAddress.setEnabled(false);
                    }

                    addressList = resultData.getBankIdAddresses();
                    if (!addressList.isEmpty()) {
                        boolean isListEmpty = false;
                        if (adapter.getItemCount() == 0) {
                            isListEmpty = true;
                        }
                        if (isListEmpty) {
                            adapter = new AddressAdapter(addressList, this);
                            binding.recyclerViewAddresses.setAdapter(adapter);
                            AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewAddresses);
                        } else {
                            adapter.updateList(addressList);
                        }

                        showEmptyText(false);

                    } else {
                        showEmptyText(true);
                    }

                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    isBankIdAccessEnabled = false;
                    showError(result.getStatusCode());
                    showEmptyText(true);
                }
            } else {
                isBankIdAccessEnabled = false;
                showEmptyText(true);
            }
        }, SessionManager.getInstance().getSessionToken());
    }

    private void checkAddressNumberLimit() {
        numberBilling = 0;
        numberShipping = 0;
        HashMap<String, BankIdAddress> bankIdAddressMap = ApplicationModel.getInstance().getBankIdAddressMap();
        for (BankIdAddress item : bankIdAddressMap.values()) {
            if (item.getAddressType() == BankIdAddress.EBankIdAddressType.BOTH) {
                numberBilling++;
                numberShipping++;
            } else if (item.getAddressType() == BankIdAddress.EBankIdAddressType.BILLING) {
                numberBilling++;
            } else if (item.getAddressType() == BankIdAddress.EBankIdAddressType.SHIPPING) {
                numberShipping++;
            }
        }
        binding.buttonAddAddress.setEnabled(numberBilling < 3 || numberShipping < 3);
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void showEmptyText(boolean isEmpty) {
        if (isEmpty) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.textListEmpty, DEFAULT_DURATION);
            binding.layoutRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            binding.textListEmpty.setVisibility(View.INVISIBLE);
            binding.layoutRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAddressClicked(BankIdAddress address) {
        if (!binding.refresh.isRefreshing()) {
            resetFocus();
            BankIdFlowManager.goToModifyAddress(this, address, numberBilling < 3, numberShipping < 3, activityResultLauncherModifyAddress);
        }
    }

    private void resetFocus() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_OR_MODIFY_ADDRESS_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                isAddressListUpdated = data.getBooleanExtra(IS_ADDRESS_LIST_UPDATED, false);
            }
        }
    }
}
