package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.ActivitySaveContactNumberBinding;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumberManager;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.View.GONE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CONTACT_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CONTACTS_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.PHONE_NUMBER;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class SaveContactNumberActivity extends GenericErrorActivity {

    private static final String TAG = SaveContactNumberActivity.class.getSimpleName();

    public static final String DEFAULT_COUNTRY_CODE = "IT";
    private static final int PERMISSION_CONTACT = 1000;
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_NUMBER = "CONTACT_NUMBER";

    ActivitySaveContactNumberBinding binding;
    private String phoneNumber;
    private List<DataPrefixPhoneNumber> fullDataList = new ArrayList<>();
    private DataPrefixPhoneNumber mDefaultData;


    ActivityResultLauncher<Intent> activityResultLauncherContacts = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                manageResult(REQUEST_CODE_SHOW_CONTACTS_CONSENT);
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySaveContactNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);

        detectCountryCode(phoneNumber);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> showPermissionRequestDialog());

     /*   if (FullStackSdkDataManager.getInstance().getDataPrefixPhoneNumber() != null) {
            mDefaultData = FullStackSdkDataManager.getInstance().getDataPrefixPhoneNumber();
        } else {
            mDefaultData = DataPrefixPhoneNumberManager.getData(DEFAULT_COUNTRY_CODE);
        }
        if (mDefaultData != null) {
            binding.editTextNumber.setPrefixItem(mDefaultData);
        }*/
        initEditTextValues();
        initTextWatchers();
        clickSaveContact();

    }

    private void clickSaveContact() {
        binding.buttonSaveContact.setOnClickListener(new CustomOnClickListener(v -> {
            hideKeyboard();
            if (PhoneNumber.isValidNumber(binding.editTextNumber.getPhoneFormatted())) {
                if (contactExists(binding.editTextNumber.getPhoneFormatted())) {
                    showWarningDialog();
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        addContact();
                    } else if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CONTACT_DISCLOSURE)) { //prima volta faccio vedere disclosure
                        PermissionFlowManager.goToContactDisclosure(this, activityResultLauncherContacts);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_CONTACT);
                    }
                }
            } else {
                binding.editTextNumber.setErrorMessage(getString(R.string.address_book_wrong_msisdn));
            }

        }));
    }

    private void getDataPrefixPhoneNumberFromPrefix(String countryCode) {
        fullDataList = FullStackSdkDataManager.getInstance().getPrefixList();
        mDefaultData = DataPrefixPhoneNumberManager.getData(DEFAULT_COUNTRY_CODE);
        if (!TextUtils.isEmpty(countryCode)) {
            for (DataPrefixPhoneNumber dataPrefixPhoneNumber : fullDataList) {
                if (dataPrefixPhoneNumber.getCountryCode().equals(countryCode)) {
                    mDefaultData = dataPrefixPhoneNumber;
                }
                break;
            }
        }
    }

    private void detectCountryCode(String number) {

        String countryCode = "";
        String NumberStr = number;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber NumberProto = phoneUtil.parse(NumberStr, null);
            countryCode = phoneUtil.getRegionCodeForCountryCode(NumberProto.getCountryCode());

        } catch (NumberParseException e) {
            CustomLogger.e(TAG, "Get country code for number failed: " + e.getMessage());
        }
        getDataPrefixPhoneNumberFromPrefix(countryCode);
    }


    private String getPhoneNumberWithoutPrefix(String number) {
        String NumberStr = number;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber NumberProto = phoneUtil.parse(NumberStr, null);

            return String.valueOf(NumberProto.getNationalNumber());

        } catch (NumberParseException e) {
            CustomLogger.e(TAG, "Get country code for number failed: " + e.getMessage());
            return "";
        }
    }

    private void initEditTextValues() {
        binding.editTextName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        binding.editTextSurname.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        binding.editTextNumber.setInputType(TYPE_CLASS_NUMBER);
        binding.editTextNumber.setPrefixItem(mDefaultData);
        binding.editTextNumber.setText(getPhoneNumberWithoutPrefix(phoneNumber));
        //binding.editTextNumber.setErrorBackground(R.drawable.edit_text_error_background_rounded);
    }

    public boolean contactExists(String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void addContact() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        ops.add(builder.build());

        // Name
        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        if (!TextUtils.isEmpty(binding.editTextSurname.getText())) {
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, binding.editTextSurname.getText());
        }
        if (!TextUtils.isEmpty(binding.editTextName.getText())) {
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, binding.editTextName.getText());
        }
        ops.add(builder.build());

        // Number
        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, binding.editTextNumber.getPhoneFormatted());
        builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        ops.add(builder.build());

        // Add the new contact
        ContentProviderResult[] res;
        try {
            res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                showResultSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showResultSuccess() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.saveContactScrollview, DEFAULT_DURATION, GONE);
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultDesc, AnimationFadeUtil.DEFAULT_DURATION);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageResult, AnimationFadeUtil.DEFAULT_DURATION);
        binding.textTitle.setText(getString(R.string.address_book_success_activity_label));
        binding.resultDesc.setText(getString(R.string.address_book_success_activity_desc));
        binding.buttonSaveContact.setText(getString(R.string.close_button));
        binding.buttonSaveContact.setOnClickListener(v -> {
            setResult();
        });
    }

    private void setResult() {
        String contactName = binding.editTextName.getText();
        String contactSurname = binding.editTextSurname.getText();
        Intent intentResult = new Intent();
        intentResult.putExtra(CONTACT_NAME, contactName + " " + contactSurname);
        intentResult.putExtra(CONTACT_NUMBER, binding.editTextNumber.getPhoneFormatted());
        setResult(RESULT_OK, intentResult);
        finish();
    }


    protected void manageResult(int requestCode) {
        if (requestCode == PERMISSION_CONTACT) {
            addContact();
        }
    }

    private void initTextWatchers() {
        TextWatcher textWatcherSurname = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.buttonSaveContact.setEnabled(isFormFilled());
                binding.editTextSurname.setDeleteVisibility(!binding.editTextSurname.getText().isEmpty());
                if (binding.editTextSurname.hasError()) {
                    binding.editTextSurname.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        View.OnFocusChangeListener focusListenerSurname = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextSurname.setDeleteVisibility(true);
                }
                if (binding.editTextSurname.hasError()) {
                    binding.editTextSurname.clearError();
                }
            } else {
                binding.editTextSurname.setDeleteVisibility(false);
            }
        };
        binding.editTextSurname.setCustomOnFocusChangeListener(focusListenerSurname);
        binding.editTextSurname.addTextChangedListener(textWatcherSurname);
        TextWatcher textWatcherName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveContact.setEnabled(isFormFilled());
                binding.editTextName.setDeleteVisibility(!binding.editTextName.getText().isEmpty());
                if (binding.editTextName.hasError()) {
                    binding.editTextName.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        View.OnFocusChangeListener focusListenerName = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextName.setDeleteVisibility(true);
                }
              /*  if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
                }*/
                if (binding.editTextName.hasError()) {
                    binding.editTextName.clearError();
                }
            } else {
                binding.editTextName.setDeleteVisibility(false);
            }
        };
        binding.editTextName.setCustomOnFocusChangeListener(focusListenerName);
        binding.editTextName.addTextChangedListener(textWatcherName);
        TextWatcher textWatcherDocumentNumber = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveContact.setEnabled(isFormFilled());
                if (binding.editTextNumber.hasError()) {
                    binding.editTextNumber.clearError();
                }
                if (!binding.editTextNumber.getText().isEmpty()) {
                    binding.editTextNumber.setDeleteVisibility(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        View.OnFocusChangeListener focusListenerDocumentNumber = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextNumber.setDeleteVisibility(true);
                }
                if (binding.editTextNumber.hasError()) {
                    binding.editTextNumber.clearError();
                }
            } else {
                binding.editTextNumber.setDeleteVisibility(false);
            }
        };
        binding.editTextNumber.setCustomOnFocusChangeListener(focusListenerDocumentNumber);
        binding.editTextNumber.addTextChangedListener(textWatcherDocumentNumber);
    }

    private void showPermissionRequestDialog() {
        AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
        builder.setTitle(R.string.warning_title)
                .setCancelable(false)
                .setMessage(R.string.address_book_exit_error_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> onBackPressed())
                .setNegativeButton(R.string.cancel, null);
        builder.showDialog(this);
    }

    private void showWarningDialog() {
        AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
        builder.setTitle(R.string.warning_title)
                .setCancelable(false)
                .setMessage(R.string.address_book_warning_dialog_message)
                .setPositiveButton(R.string.ok, null);
        builder.showDialog(this);
    }


    private boolean isFormFilled() {
        return (binding.editTextSurname.getText().length() > 0 ||
                binding.editTextName.getText().length() > 0) && binding.editTextNumber.getText().length() > 0;
    }

}