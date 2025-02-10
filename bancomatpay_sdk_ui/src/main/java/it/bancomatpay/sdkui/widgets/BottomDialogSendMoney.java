package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumberManager;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

public class BottomDialogSendMoney implements LabelTelephone.LabelListener {

    private final InsertNumberConfirmListener listener;
    private final Context context;

    private BottomSheetDialog dialog;
    private BottomSheetBehavior<?> behavior;

    private LabelTelephone insertNumberEditText;
    private KeyboardCustom keyboardCustom;

    private static final int maxLength = 15;

    public BottomDialogSendMoney(@NonNull Context context, @NonNull InsertNumberConfirmListener listener) {
        this.listener = listener;
        this.context = context;
        //Refresh preferences lista prefissi per retrocompatibilità
        FullStackSdkDataManager.getInstance().putPrefixList(DataPrefixPhoneNumberManager.getFullDataList());
    }

    public void showDialog() {

        dialog = new BottomSheetDialog(context, R.style.BottomSheetCustom);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);

        View bottomSheetView = inflater.inflate(R.layout.bottom_dialog_send_money_keyboard, null);
        dialog.setContentView(bottomSheetView);

        insertNumberEditText = dialog.findViewById(R.id.insert_number_edit_text);
        if (insertNumberEditText != null) {
            insertNumberEditText.setMaxLength(maxLength);
            insertNumberEditText.setInitialText("");
        }
        ImageView cancelButtonInsertNumber = dialog.findViewById(R.id.cancel_button_insert_number);

        keyboardCustom = dialog.findViewById(R.id.keyboard_custom);
        if (keyboardCustom != null) {
            keyboardCustom.setKeyboardType(KeyboardType.PHONE_KEYBOARD);
            keyboardCustom.setKeyboardListener(insertNumberEditText);
        }

        insertNumberEditText.setLabelListener(this);

        if (cancelButtonInsertNumber != null) {
            cancelButtonInsertNumber.setOnClickListener(v -> insertNumberEditText.onDeleteCharacter());
            cancelButtonInsertNumber.setOnLongClickListener(v -> {

                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, 100));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(30);
                }

                insertNumberEditText.onDeleteAllText();
                // cancelButtonInsertNumber.setVisibility(View.INVISIBLE);
                return true;
            });
        }

        dialog.setOnDismissListener(dialog -> behavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
            }
            return true;
        });

        behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();
    }

    public boolean isVisible() {
        return dialog != null && dialog.isShowing();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public void onNumberInserted(String number) {
        if (PhoneNumber.isValidNumber(insertNumberEditText.getPhoneFormatted())) {
            keyboardCustom.post(() -> keyboardCustom.setButtonNextEnabled(true));
            keyboardCustom.setButtonNextClickListener(v -> {
                if (listener != null) {
                    listener.onNumberInserted(insertNumberEditText.getPhoneFormatted());
                }
            });
        } else {
            keyboardCustom.post(() -> keyboardCustom.setButtonNextEnabled(false));
        }
    }

    @Override
    public void onNumberDeleted(boolean deleteAll) {
        if (deleteAll) {
            keyboardCustom.reset();
        } else {
            keyboardCustom.deleteCharacter();
        }
    }

    private AdapterView.OnItemSelectedListener getOnPrefixSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onNumberInserted(insertNumberEditText.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Non usato
            }
        };
    }

    public interface InsertNumberConfirmListener {
        void onNumberInserted(String number);
    }

}
