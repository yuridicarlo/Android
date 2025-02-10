package it.bancomatpay.sdkui.fragment.documents;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.documents.DocumentDetailActivity;
import it.bancomatpay.sdkui.databinding.FragmentDocumentDataBinding;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.CustomEditText;

import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.HAS_FISCAL_CODE;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.IS_ADD_OTHER_DOCUMENT;


public class DocumentDataFragment extends Fragment {

    private FragmentDocumentDataBinding binding;

    private static final String DOCUMENT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";

    private BcmDocument document;
    private InteractionListener listener;

    private Date dateEmission;
    private Date dateExpiration;
    private DtoDocument.DocumentTypeEnum documentType;

    private boolean isAddOtherDocument;
    private boolean hasFiscalCode;


    public static DocumentDataFragment newInstance(BcmDocument document, InteractionListener listener, boolean isAddOtherDocument, boolean hasFiscalCode, DtoDocument.DocumentTypeEnum documentType) {
        DocumentDataFragment fragment = new DocumentDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(DOCUMENT_EXTRA, document);
        args.putBoolean(IS_ADD_OTHER_DOCUMENT, isAddOtherDocument);
        args.putBoolean(HAS_FISCAL_CODE, hasFiscalCode);
        args.putSerializable(DOCUMENT_TYPE, documentType);
        fragment.setArguments(args);

        fragment.setInteractionListener(listener);

        return fragment;
    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            document = (BcmDocument) getArguments().getSerializable(DOCUMENT_EXTRA);
            isAddOtherDocument = getArguments().getBoolean(IS_ADD_OTHER_DOCUMENT, false);
            hasFiscalCode = getArguments().getBoolean(HAS_FISCAL_CODE, false);
            documentType = (DtoDocument.DocumentTypeEnum) getArguments().getSerializable(DOCUMENT_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDocumentDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEditTextWatchers();
        initEditTextValues();

        binding.buttonSaveDocument.setEnabled(false);

        binding.buttonSaveDocument.setOnClickListener(new CustomOnClickListener(v -> {
            if (checkEditTextErrors() && listener != null) {
                updateDocument();
                listener.saveDocument(document);
            }
        }));
        binding.imageScanFiscalCode.setOnClickListener(new CustomOnClickListener(v -> {
            if (getActivity() instanceof DocumentDetailActivity) {
                DocumentDetailActivity activity = (DocumentDetailActivity) getActivity();
                DocumentsFlowManager.goToScanFiscalCode(requireActivity(), activity.getActivityResultLauncherFiscalCode());
            }
        }));
    }

    @Override
    public void onDestroyView() {

        binding.editTextDocumentName.removeCustomOnFocusChangeListener();
        binding.editTextSurname.removeCustomOnFocusChangeListener();
        binding.editTextName.removeCustomOnFocusChangeListener();
        binding.editTextDocumentNumber.removeCustomOnFocusChangeListener();
        binding.editTextIssuer.removeCustomOnFocusChangeListener();
        binding.editTextDateEmission.removeCustomOnFocusChangeListener();
        binding.editTextDateExpiration.removeCustomOnFocusChangeListener();
        binding.editTextNotes.removeCustomOnFocusChangeListener();

        super.onDestroyView();

        binding = null;
    }

    private void updateDocument() {

        if (document == null) {
            document = new BcmDocument();
        }

        document.setDocumentName(binding.editTextDocumentName.getText());
        document.setSurname(binding.editTextSurname.getText());
        document.setName(binding.editTextName.getText());
        document.setDocumentNumber(binding.editTextDocumentNumber.getText());
        if (dateEmission != null) {
            document.setIssuingDate(getDateString(dateEmission));
        }
        if (!hasFiscalCode) {
            document.setIssuingInstitution(binding.editTextIssuer.getText());
        } else {
            document.setFiscalCode(binding.editTextIssuer.getText());
        }
        document.setExpirationDate(getDateString(dateExpiration));
        document.setNote(binding.editTextNotes.getText());
    }

    private void initEditTextValues() {

        binding.editTextDocumentName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        binding.editTextSurname.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        binding.editTextName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        binding.editTextDocumentNumber.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        binding.editTextIssuer.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        binding.editTextDateEmission.setInputType(InputType.TYPE_NULL);
        binding.editTextDateExpiration.setInputType(InputType.TYPE_NULL);
        binding.editTextNotes.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        binding.editTextDocumentName.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextName.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextSurname.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextDocumentNumber.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextIssuer.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextDateEmission.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextDateExpiration.setErrorBackground(R.drawable.edit_text_error_background_rounded);
        binding.editTextNotes.setErrorBackground(R.drawable.edit_text_error_background_rounded);

        if (isAddOtherDocument ||
                (document != null && document.getDocumentType() == DtoDocument.DocumentTypeEnum.OTHER)) {
            binding.layoutDocumentName.setVisibility(View.VISIBLE);
        }

        if (hasFiscalCode) {
            //editTextDateEmissionBox.setVisibility(View.GONE);
            binding.editTextIssuer.setHintText(getString(R.string.add_document_hint_fiscal_code));
            binding.editTextIssuer.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            setBarcodeImageVisible(true);
        }

        if (document != null) {

            if (document.getDocumentType() == DtoDocument.DocumentTypeEnum.OTHER) {
                binding.editTextDocumentName.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(document.getDocumentName())) {
                binding.editTextDocumentName.setText(document.getDocumentName());
                binding.editTextDocumentName.setDeleteVisibility(false);
            }
            if (!TextUtils.isEmpty(document.getName())) {
                binding.editTextName.setText(document.getName());
                binding.editTextName.setDeleteVisibility(false);
            }
            if (!TextUtils.isEmpty(document.getSurname())) {
                binding.editTextSurname.setText(document.getSurname());
                binding.editTextSurname.setDeleteVisibility(false);
            }
            if (!TextUtils.isEmpty(document.getDocumentNumber())) {
                binding.editTextDocumentNumber.setText(document.getDocumentNumber());
                binding.editTextDocumentNumber.setDeleteVisibility(false);
            }
            if (!TextUtils.isEmpty(document.getIssuingInstitution())) {
                binding.editTextIssuer.setText(document.getIssuingInstitution());
                binding.editTextIssuer.setDeleteVisibility(false);
                setBarcodeImageVisible(true);
            } else if (!TextUtils.isEmpty(document.getFiscalCode())) {
                binding.editTextIssuer.setText(document.getFiscalCode());
                binding.editTextIssuer.setDeleteVisibility(false);
                setBarcodeImageVisible(true);
            }
            if (document.getIssuingDate() != null) {

                String dateString = document.getIssuingDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DOCUMENT_DATE_FORMAT);
                Date convertedDate = new Date();
                try {
                    convertedDate = dateFormat.parse(dateString);
                    dateEmission = convertedDate;
                } catch (ParseException ignored) {
                }

                binding.editTextDateEmission.setText(getDateStringFormatted(convertedDate));
                binding.editTextDateEmission.setDeleteVisibility(false);
            }
            if (document.getExpirationDate() != null) {

                String dateString = document.getExpirationDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DOCUMENT_DATE_FORMAT);
                Date convertedDate = new Date();
                try {
                    convertedDate = dateFormat.parse(dateString);
                    dateExpiration = convertedDate;
                } catch (ParseException ignored) {
                }

                binding.editTextDateExpiration.setText(getDateStringFormatted(convertedDate));
                binding.editTextDateExpiration.setDeleteVisibility(false);
            }
            if (!TextUtils.isEmpty(document.getNote())) {
                binding.editTextNotes.setText(document.getNote());
                binding.editTextNotes.setDeleteVisibility(false);
            }

        }
    }

    private void initEditTextWatchers() {

        Calendar calendar = Calendar.getInstance();

        TextWatcher textWatcherDocumentName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveDocument.setEnabled(isFormFilled());
                if (binding.editTextDocumentName.hasError()) {
                    binding.editTextDocumentName.clearError();
                }
                if (!binding.editTextDocumentName.getText().isEmpty()) {
                    binding.editTextDocumentName.setDeleteVisibility(true);
                } else {
                    binding.editTextDocumentName.setDeleteVisibility(false);
                    binding.editTextDocumentName.setError(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        View.OnFocusChangeListener focusListenerDocumentName = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextDocumentName.setDeleteVisibility(true);
                }
                if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
                }
                if (binding.editTextDocumentName.hasError()) {
                    binding.editTextDocumentName.clearError();
                }
            } else {
                binding.editTextDocumentName.setDeleteVisibility(false);
            }
        };
        binding.editTextDocumentName.setCustomOnFocusChangeListener(focusListenerDocumentName);
        binding.editTextDocumentName.addTextChangedListener(textWatcherDocumentName);

        TextWatcher textWatcherSurname = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveDocument.setEnabled(isFormFilled());
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
                if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
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
                binding.buttonSaveDocument.setEnabled(isFormFilled());
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
                if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
                }
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
                binding.buttonSaveDocument.setEnabled(isFormFilled());
                if (binding.editTextDocumentNumber.hasError()) {
                    binding.editTextDocumentNumber.clearError();
                }
                if (!binding.editTextDocumentNumber.getText().isEmpty()) {
                    binding.editTextDocumentNumber.setDeleteVisibility(true);
                } else {
                    binding.editTextDocumentNumber.setDeleteVisibility(false);
                    binding.editTextDocumentNumber.setError(true);
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
                    binding.editTextDocumentNumber.setDeleteVisibility(true);
                }
                if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
                }
                if (binding.editTextDocumentNumber.hasError()) {
                    binding.editTextDocumentNumber.clearError();
                }
            } else {
                binding.editTextDocumentNumber.setDeleteVisibility(false);
            }
        };
        binding.editTextDocumentNumber.setCustomOnFocusChangeListener(focusListenerDocumentNumber);
        binding.editTextDocumentNumber.addTextChangedListener(textWatcherDocumentNumber);

        TextWatcher textWatcherIssuer = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveDocument.setEnabled(isFormFilled());
                if (binding.editTextIssuer.hasError()) {
                    binding.editTextIssuer.clearError();
                }
                if (!binding.editTextIssuer.getText().isEmpty()) {
                    binding.editTextIssuer.setDeleteVisibility(true);
                    setBarcodeImageVisible(false);
                } else {
                    binding.editTextIssuer.setDeleteVisibility(false);
                    setBarcodeImageVisible(true);
                    binding.editTextIssuer.setError(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        View.OnFocusChangeListener focusListenerIssuer = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextIssuer.setDeleteVisibility(true);
                    setBarcodeImageVisible(false);
                }
                if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
                }
                if (binding.editTextIssuer.hasError()) {
                    binding.editTextIssuer.clearError();
                }
            } else {
                binding.editTextIssuer.setDeleteVisibility(false);
                setBarcodeImageVisible(true);
            }
        };
        binding.editTextIssuer.setCustomOnFocusChangeListener(focusListenerIssuer);
        binding.editTextIssuer.addTextChangedListener(textWatcherIssuer);

        TextWatcher textWatcherDateEmission = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveDocument.setEnabled(isFormFilled());
                if (binding.editTextDateEmission.hasError()) {
                    binding.editTextDateEmission.clearError();
                }
                if (!binding.editTextDateEmission.getText().isEmpty()) {
                    binding.editTextDateEmission.setDeleteVisibility(true);
                } else {
                    binding.editTextDateEmission.setDeleteVisibility(false);
                    if ((document!= null && document.getDocumentType() != DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD) || (documentType!= null && documentType != DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD)) {
                        binding.editTextDateEmission.setError(true);
                    } else {
                        binding.editTextDateEmission.setError(false);
                        binding.buttonSaveDocument.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        DatePickerDialog.OnDateSetListener datePickerEmission = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.editTextDateEmission.setText(getDateStringFormatted(calendar.getTime()));
            dateEmission = calendar.getTime();
            resetFocus(binding.editTextDateEmission, true);
        };
        View.OnFocusChangeListener focusListenerDateEmission = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {

                binding.editTextDateExpiration.setDeleteVisibility(false);

                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextDateEmission.setDeleteVisibility(true);
                }
                if (binding.editTextDateEmission.hasError()) {
                    binding.editTextDateEmission.clearError();
                }

                if (listener != null) {
                    listener.hideKeyboard();
                }

                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(), datePickerEmission,
                        calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    dialog.setOnShowListener(d -> dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)));
                }
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog1, which) -> resetFocus(binding.editTextDateEmission, !editText.getText().toString().isEmpty()));
                dialog.setOnCancelListener(dialog12 -> resetFocus(binding.editTextDateEmission, !editText.getText().toString().isEmpty()));
                dialog.getDatePicker().setMaxDate(calendar1.getTimeInMillis());
                dialog.show();

            } else {
                binding.editTextDateEmission.setDeleteVisibility(false);
            }
        };
        binding.editTextDateEmission.setCustomOnFocusChangeListener(focusListenerDateEmission);
        binding.editTextDateEmission.addTextChangedListener(textWatcherDateEmission);

        TextWatcher textWatcherDateExpiration = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveDocument.setEnabled(isFormFilled());
                if (binding.editTextDateExpiration.hasError()) {
                    binding.editTextDateExpiration.clearError();
                }
                if (!binding.editTextDateExpiration.getText().isEmpty()) {
                    binding.editTextDateExpiration.setDeleteVisibility(true);
                    if (!binding.addDateToCalendarBox.isExpanded() & binding.addDateToCalendarText.requestFocusFromTouch()) {
                        binding.addDateToCalendarBox.expand();
                        binding.addDateToCalendarText.setOnClickListener(new CustomOnClickListener(v -> addEventOnCalendar()));
                    }
                } else {
                    binding.editTextDateExpiration.setDeleteVisibility(false);
                    binding.editTextDateExpiration.setError(true);
                    if (binding.addDateToCalendarBox.isExpanded()) {
                        binding.addDateToCalendarBox.collapse();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        DatePickerDialog.OnDateSetListener datePickerExpiration = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.editTextDateExpiration.setText(getDateStringFormatted(calendar.getTime()));
            dateExpiration = calendar.getTime();
            resetFocus(binding.editTextDateExpiration, true);
        };
        View.OnFocusChangeListener focusListenerDateExpiration = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {

                binding.editTextDateEmission.setDeleteVisibility(false);

                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextDateExpiration.setDeleteVisibility(true);
                }
                if (binding.editTextDateExpiration.hasError()) {
                    binding.editTextDateExpiration.clearError();
                }

                if (listener != null) {
                    listener.hideKeyboard();
                }

                Calendar calendar2 = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(), datePickerExpiration,
                        calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH));
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    dialog.setOnShowListener(d -> dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)));
                }
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog1, which) -> resetFocus(binding.editTextDateExpiration, !editText.getText().toString().isEmpty()));
                dialog.setOnCancelListener(dialog12 -> resetFocus(binding.editTextDateExpiration, !editText.getText().toString().isEmpty()));
                dialog.getDatePicker().setMinDate(calendar2.getTimeInMillis());
                dialog.show();

            } else {
                binding.editTextDateExpiration.setDeleteVisibility(false);
            }
        };
        binding.editTextDateExpiration.setCustomOnFocusChangeListener(focusListenerDateExpiration);
        binding.editTextDateExpiration.addTextChangedListener(textWatcherDateExpiration);

        TextWatcher textWatcherNotes = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence source, int start, int before, int count) {
                binding.buttonSaveDocument.setEnabled(isFormFilled());
                binding.editTextNotes.setDeleteVisibility(!binding.editTextNotes.getText().isEmpty());
                if (binding.editTextNotes.hasError()) {
                    binding.editTextNotes.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        View.OnFocusChangeListener focusListenerNotes = (v, hasFocus) -> {
            EditText editText = ((EditText) v);
            if (hasFocus) {
                if (!editText.getText().toString().isEmpty()) {
                    binding.editTextNotes.setDeleteVisibility(true);
                }
                if (lastEditTextDate != null) {
                    lastEditTextDate.setDeleteVisibility(false);
                }
                if (binding.editTextNotes.hasError()) {
                    binding.editTextNotes.clearError();
                }
            } else {
                binding.editTextNotes.setDeleteVisibility(false);
            }
        };
        binding.editTextNotes.setCustomOnFocusChangeListener(focusListenerNotes);
        binding.editTextNotes.addTextChangedListener(textWatcherNotes);
    }

    public void addEventOnCalendar() {
        Calendar beginTime = Calendar.getInstance();
        String date = binding.editTextDateExpiration.getText();
        String[] values = date.split("/");
        int day = Integer.parseInt(values[0]);
        int month = Integer.parseInt(values[1]) - 1;
        int year = Integer.parseInt(values[2]);
        beginTime.set(year, month, day, 0, 0);

        String event;
        if (!TextUtils.isEmpty(binding.editTextDocumentName.getText())) {
            event = getString(R.string.expiration_date_label, binding.editTextDocumentName.getText());
        } else {
            event = getString(R.string.expiration_date_label, getDocumentNameFromType(documentType));
        }
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, 23, 59);
        Intent intent = new Intent(Intent.ACTION_EDIT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.TITLE, event);
        startActivity(intent);
    }

    private String getDocumentNameFromType(DtoDocument.DocumentTypeEnum documentType) {
        String documentName;

        DtoDocument.DocumentTypeEnum type = DtoDocument.DocumentTypeEnum.OTHER;
        if (document != null) {
            type = document.getDocumentType();
        } else if (documentType != null) {
            type = documentType;
        }

        if (type != null) {
            switch (type) {
                case PAPER_IDENTITY_CARD:
                    documentName = getString(R.string.popup_menu_voice_1);
                    break;
                case ELECTRONIC_IDENTITY_CARD:
                    documentName = getString(R.string.popup_menu_voice_2);
                    break;
                case HEALTH_INSURANCE_CARD:
                    documentName = getString(R.string.popup_menu_voice_3);
                    break;
                case DRIVING_LICENSE:
                    documentName = getString(R.string.popup_menu_voice_4);
                    break;
                case PASSPORT:
                    documentName = getString(R.string.popup_menu_voice_5);
                    break;
                case OTHER:
                    documentName = getString(R.string.popup_menu_voice_6);
                    break;
                default:
                    documentName = "";
                    break;
            }
        } else {
            documentName = "";
        }

        return documentName.toLowerCase();
    }

    private boolean checkEditTextErrors() {

        final String REGEX_ONLY_LETTERS = "^[a-zA-Z .,'-]*$";
        final String REGEX_FISCAL_CODE =
                "^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}" +
                        "(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MN" +
                        "P-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS" +
                        "][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T]" +
                        "[26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV" +
                        "][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP" +
                        "-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1" +
                        "-9MNP-V]))[A-Z]$";

        if (!Pattern.matches(REGEX_ONLY_LETTERS, binding.editTextName.getText())) {
            binding.editTextName.setError(true);
        }
        if (!Pattern.matches(REGEX_ONLY_LETTERS, binding.editTextSurname.getText())) {
            binding.editTextSurname.setError(true);
        }
        if (hasFiscalCode && !Pattern.matches(REGEX_FISCAL_CODE, binding.editTextIssuer.getText())) {
            binding.editTextIssuer.setError(true);
        }

        return !binding.editTextName.hasError() && !binding.editTextSurname.hasError() && !binding.editTextIssuer.hasError();

    }

    private boolean isFormFilled() {

        boolean conditionOtherDocument = binding.editTextDocumentNumber.getText().length() > 0
                && binding.editTextIssuer.getText().length() > 0
                && binding.editTextDateEmission.getText().length() > 0
                && binding.editTextDateExpiration.getText().length() > 0
                && binding.editTextDocumentName.getText().length() > 0;

        boolean conditionOrdinaryDocument = binding.editTextDocumentNumber.getText().length() > 0
                && binding.editTextIssuer.getText().length() > 0
                && binding.editTextDateEmission.getText().length() > 0
                && binding.editTextDateExpiration.getText().length() > 0;

        boolean conditionFiscalCodeDocument = binding.editTextDocumentNumber.getText().length() > 0
                && binding.editTextIssuer.getText().length() > 0
                && binding.editTextDateExpiration.getText().length() > 0;

        if (document != null) {
            if (document.getDocumentType() == DtoDocument.DocumentTypeEnum.OTHER) {
                return conditionOtherDocument;
            } else if (document.getDocumentType() == DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD) {
                return conditionFiscalCodeDocument;
            } else {
                return conditionOrdinaryDocument;
            }
        } else {
            if (isAddOtherDocument) {
                return conditionOtherDocument;
            } else if (hasFiscalCode) {
                return conditionFiscalCodeDocument;
            } else {
                return conditionOrdinaryDocument;
            }
        }
    }

    private void setBarcodeImageVisible(boolean visible) {
        if (hasFiscalCode && visible) {
            binding.imageScanFiscalCode.setVisibility(View.VISIBLE);
        } else {
            binding.imageScanFiscalCode.setVisibility(View.INVISIBLE);
        }
    }

    public void updateButtonLayout(boolean isKeyboardOpen) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.buttonSaveDocument.getLayoutParams();
        if (isKeyboardOpen) {
            binding.buttonSaveDocument.setBackgroundResource(R.drawable.button_square_background_state_list);
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = 0;
        } else {
            binding.buttonSaveDocument.setBackgroundResource(R.drawable.button_round_background_state_list);
            params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
            params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
            params.bottomMargin = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
        }
        binding.buttonSaveDocument.setLayoutParams(params);
    }

    private String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DOCUMENT_DATE_FORMAT);
        return formatter.format(date);
    }

    private String getDateStringFormatted(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    private CustomEditText lastEditTextDate;

    private void resetFocus(CustomEditText editText, boolean deleteVisibility) {
        if (getActivity() != null) {
            if (getActivity().getCurrentFocus() != null) {
                getActivity().getCurrentFocus().clearFocus();
                editText.setDeleteVisibility(deleteVisibility);
                lastEditTextDate = editText;
            }
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    public void updateLayout() {
        binding.buttonSaveDocument.setEnabled(false);
        if (binding.addDateToCalendarBox.isExpanded()) {
            binding.addDateToCalendarBox.collapse();
        }
    }

    public void setFiscalCodeValue(String fiscalCode) {
        binding.editTextIssuer.setText(fiscalCode);
        binding.editTextIssuer.setDeleteVisibility(false);
    }

    public interface InteractionListener {
        void hideKeyboard();
        void saveDocument(@NonNull BcmDocument document);
    }

}
