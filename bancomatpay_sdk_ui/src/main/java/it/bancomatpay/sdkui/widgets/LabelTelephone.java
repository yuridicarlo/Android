package it.bancomatpay.sdkui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.WidgetsLabelTelephoneBinding;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

public class LabelTelephone extends AbstractLabel {

    private final WidgetsLabelTelephoneBinding binding;

    LabelTelephone.LabelListener listener;
    String number;
    String prefix;
    int maxLength;
    int height;
    int width;
    DataPrefixPhoneNumber mItem;
    List<DataPrefixPhoneNumber> items;

    public interface LabelListener {
        void onNumberInserted(String text);

        void onNumberDeleted(boolean deleteAll);
    }

    public void setLabelListener(LabelTelephone.LabelListener customLabelListener) {
        this.listener = customLabelListener;
    }

    public LabelTelephone(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetsLabelTelephoneBinding.inflate(LayoutInflater.from(context), this, true);
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

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelTelephone);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.LabelTelephone_showPrefixSelector) {
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

    }


    public void setMaxLength(int length) {
        this.maxLength = length;
    }

    @Override
    protected void updateView() {
        if (mText.length() > 0) {
            if (mText.length() < maxLength) {
                binding.msisdnDigit.setText(mText);
            } else {
                onDeleteCharacter();
            }
            if (listener != null) {
                listener.onNumberInserted(binding.msisdnDigit.getText().toString());
            }
        } else {
            binding.msisdnDigit.setText("");
        }
    }

    @Override
    public void onTextEntered(String text) {

        if (mText.length() == 0 && text.equals(prefix)) {
            return;
        }

        if (mText.length() <= maxLength) {
            super.onTextEntered(text);
        }
    }

    @Override
    public void onDeleteCharacter() {
        super.onDeleteCharacter();
        number = /*prefix +*/ mText.toString();
        listener.onNumberInserted(number);
        listener.onNumberDeleted(false);

    }

    @Override
    public void onDeleteAllText() {
        super.onDeleteAllText();
        if (!TextUtils.isEmpty(mText.toString())) {
            listener.onNumberInserted(mText.toString());
        }
        listener.onNumberDeleted(true);
    }

    @Override
    public void setMaxElements(int i) {

    }

    public void setText(String text) {
        mText = new StringBuilder(text);
        updateView();
    }

    public void setInitialText(String initialText) {
        mText = new StringBuilder(initialText);
        binding.msisdnDigit.setText(initialText);
        if (TextUtils.isEmpty(initialText)) {
            prefix = "";
        }
    }

    public void setHintText(String hintText, int color) {
        binding.msisdnDigit.setHint(hintText);
        binding.msisdnDigit.setHintTextColor(color);
    }

    public String getPrefix() {
        TextView prefix = findViewById(R.id.text_prefix_value_new);
        return prefix.getText().toString();
    }

    public String getPhoneFormatted() {

        return getPrefix() + getValue();
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

    public DataPrefixPhoneNumber getDataPrefixPhoneNumber() {
        return mItem;
    }


    private void updateDialog(int position, WidgetsLabelTelephoneBinding binding) {

        String prefix = items.get(position).getPrefix();
        binding.textPrefixValueNew.setText(prefix);
        String flagPath = items.get(position).getLogoFlag();
        int resId = getContext().getResources().getIdentifier(flagPath, "drawable", getContext().getPackageName());
        binding.imageFlagNew.setImageResource(resId);
        mItem = items.get(position);
    }

}


