package it.bancomatpay.sdkui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.List;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsEditTextCustomBinding;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class CustomEditText extends RelativeLayout {

    private final WidgetsEditTextCustomBinding binding;

    protected String textDefault;
    private boolean hasError = false;
    int height;
    int width;

    DataPrefixPhoneNumber mItem;
    private List<DataPrefixPhoneNumber> items;


    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsEditTextCustomBinding.inflate(LayoutInflater.from(context), this, true);

        boolean showCancelDefault = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            height = wm.getCurrentWindowMetrics().getBounds().height();
            width = wm.getCurrentWindowMetrics().getBounds().width();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
        }
        items = FullStackSdkDataManager.getInstance().getPrefixList();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CustomEditText_showErrorText) {
                if (!a.getBoolean(i, true)) {
                    binding.errorMessage.setVisibility(GONE);
                } else {
                    binding.errorMessage.setVisibility(VISIBLE);
                }
            } else if (attr == R.styleable.CustomEditText_showDefaultCancel) {
                showCancelDefault = a.getBoolean(i, true);
            } else if (attr == R.styleable.CustomEditText_hint) {
                binding.editTextField.setHint(a.getString(i));
            } else if (attr == R.styleable.CustomEditText_elevation) {
                binding.editTextCard.setCardElevation(a.getDimension(i, 0));
            } else if (attr == R.styleable.CustomEditText_maxCharacters) {
                binding.editTextField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(a.getInt(i, 500))});
            }else if (attr == R.styleable.CustomEditText_prefixSelector) {
                if (a.getBoolean(attr, false)) {

                    DialogPrefixAdapter adapter =
                            new DialogPrefixAdapter(getContext(), FullStackSdkDataManager.getInstance().getPrefixList());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

                    builder.setAdapter(adapter, (dialog, which) -> {

                        updateDialog(which, binding);

                    });
                    AlertDialog dialog = builder.create();


                    binding.prefixLayout.setOnClickListener(v -> {
                        dialog.show();
                        dialog.getWindow().setLayout((int) (width * 0.77), (int) (height * 0.5));
                    });
                } else {
                    binding.prefixLayout.setVisibility(View.GONE);
                }
            }
        }
        a.recycle();

        textDefault = "";
        binding.editTextField.setSelection(binding.editTextField.getText().length());

        if (showCancelDefault) {
            initDefaultCancel();
        }

        binding.cancelButton.setVisibility(INVISIBLE);

        binding.cancelButton.setOnClickListener(new CustomOnClickListener(v -> {
            binding.editTextField.setText(textDefault);
            binding.editTextField.setSelection(textDefault.length());
            AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButton, DEFAULT_DURATION, View.INVISIBLE);
        }));
    }


    public void setPrefixItem(DataPrefixPhoneNumber dataPrefixPhoneNumber) {
        if (dataPrefixPhoneNumber.getPrefix() != null) {
            binding.textPrefixValueNew.setText(dataPrefixPhoneNumber.getPrefix());
        }
        if (dataPrefixPhoneNumber.getLogoFlag() != null) {
            String flagPath = dataPrefixPhoneNumber.getLogoFlag();
            int resId = getContext().getResources().getIdentifier(flagPath, "drawable", getContext().getPackageName());
            binding.imageFlagNew.setImageResource(resId);

        }
    }

    public String getPhoneFormatted() {

        return getPrefix() + getText();
    }

    public String getPrefix() {
        TextView prefix = findViewById(R.id.text_prefix_value_new);
        return prefix.getText().toString();
    }

    private void initDefaultCancel() {
        binding.editTextField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasError) {
                    hasError = false;
                    binding.errorMessage.setText("");
                }
                if (s.length() > 0 && binding.cancelButton.getVisibility() == INVISIBLE) {
                    binding.cancelButton.setVisibility(VISIBLE);
                } else if (s.length() == textDefault.length() && binding.cancelButton.getVisibility() == VISIBLE) {
                    binding.cancelButton.setVisibility(INVISIBLE);
                }
            }
        });
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        binding.editTextField.addTextChangedListener(textWatcher);
    }

    public void removeTextChangedListener(TextWatcher textWatcher) {
        binding.editTextField.removeTextChangedListener(textWatcher);
    }

    public void setText(String text) {
        binding.editTextField.setText(text);
    }

    public void setErrorMessage(String error) {
        hasError = true;
        binding.errorMessage.setText(error);
        if (binding.errorMessage.getVisibility() != VISIBLE) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.errorMessage, DEFAULT_DURATION);
        }
    }

    public void clearError() {
        hasError = false;
        if (binding.errorMessage.getVisibility() == VISIBLE) {
            AnimationFadeUtil.startFadeOutAnimationV1(binding.errorMessage, DEFAULT_DURATION, INVISIBLE);
        }
        binding.imageErrorBackground.setVisibility(INVISIBLE);
    }

    public void setErrorBackground(int drawableResource) {
        binding.imageErrorBackground.setImageResource(drawableResource);
    }

    public String getText() {
        return binding.editTextField.getText().toString();
    }

    public void setSelection(int length) {
        binding.editTextField.setSelection(length);
    }

    public void setError(boolean error) {
        this.hasError = error;
        if (error) {
            binding.imageErrorBackground.setVisibility(VISIBLE);
        } else {
            binding.imageErrorBackground.setVisibility(INVISIBLE);
        }
    }

    public boolean hasError() {
        return this.hasError;
    }

    public void setDeleteVisibility(boolean cancelVisible) {
        if (cancelVisible) {
            binding.cancelButton.setVisibility(VISIBLE);
        } else {
            binding.cancelButton.setVisibility(INVISIBLE);
        }
    }

    public void setFocusableInTouchMode(boolean focusable) {
        binding.editTextField.setFocusableInTouchMode(focusable);
    }

    public void setInputType(int inputType) {
        binding.editTextField.setInputType(inputType);
    }

    public void setHintText(String editTextLabel) {
        binding.editTextField.setHint(editTextLabel);
    }

	public void setHintTextSize(float textSize){
        binding.editTextField.setTextSize(textSize);
	}

	public void setMaxLength(int maxLength){
        binding.editTextField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
	}

	public void setCustomOnFocusChangeListener(View.OnFocusChangeListener focusListener) {
        binding.editTextField.setOnFocusChangeListener(focusListener);
	}

    public void removeCustomOnFocusChangeListener() {
        binding.editTextField.setOnFocusChangeListener(null);
    }

    public void setMultiLineMode(int maxLines, int imeOptions, boolean singleLine, int overScrollMode, int scrollbarStyle, boolean verticalScrollBarEnabled) {

        View viewContainer = findViewById(R.id.card_view_container);
        viewContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.editTextField.setImeOptions(imeOptions);
        binding.editTextField.setSingleLine(singleLine);
        binding.editTextField.setOverScrollMode(overScrollMode);
        if (verticalScrollBarEnabled) {
            binding.editTextField.setScroller(new Scroller(getContext()));
            binding.editTextField.setVerticalScrollBarEnabled(true);
            binding.editTextField.setScrollBarStyle(scrollbarStyle);
            binding.editTextField.setVerticalScrollbarPosition(SCROLLBAR_POSITION_RIGHT);
        }
        binding.editTextField.setMaxLines(maxLines);

    }

    public void setDefaultCancel(boolean showDefaultCancel) {
        if (showDefaultCancel) {
            initDefaultCancel();
        }
    }

    public void setShowErrorText(boolean showErrorText) {
        if (showErrorText) {
            binding.errorMessage.setVisibility(VISIBLE);
        } else {
            binding.errorMessage.setVisibility(GONE);
        }
    }

    private void updateDialog(int position, WidgetsEditTextCustomBinding binding) {

        String prefix = items.get(position).getPrefix();
        binding.textPrefixValueNew.setText(prefix);
        String flagPath = items.get(position).getLogoFlag();
        int resId = getContext().getResources().getIdentifier(flagPath, "drawable", getContext().getPackageName());
        binding.imageFlagNew.setImageResource(resId);
        mItem = items.get(position);
    }


}
