package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.bancomatpay.sdkui.databinding.DialogCashbackBlueBinding;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_HOME_CLOSE_POPUP;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_HOME_SECTION_POPUP;

public class BlueCustomDialog extends DialogFragment {

    private DialogCashbackBlueBinding binding;
    private InteractionListener listener;

    private final static String REQUEST_CODE = "requestCode";
    private final static String TITLE = "title";
    private final static String MESSAGE = "message";
    private final static String FIRST_BUTTON = "firstButton";
    private final static String SECOND_BUTTON = "secondButton";

    private int requestCode;
    private String title;
    private String message;
    private String firstButton;
    private String secondButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

        if (getArguments() != null) {
            requestCode = getArguments().getInt(REQUEST_CODE);
            title = getArguments().getString(TITLE);
            message = getArguments().getString(MESSAGE);
            firstButton = getArguments().getString(FIRST_BUTTON);
            secondButton = getArguments().getString(SECOND_BUTTON);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        }
    }

    public static BlueCustomDialog newInstance(int requestCode, String title, String message, String firstButton, String secondButton){
        BlueCustomDialog blueCustomDialog = new BlueCustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);
        bundle.putString(FIRST_BUTTON, firstButton);
        bundle.putString(SECOND_BUTTON, secondButton);
        bundle.putInt(REQUEST_CODE, requestCode);
        blueCustomDialog.setArguments(bundle);
        return blueCustomDialog;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogCashbackBlueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.title.setText(title);
        binding.message.setText(message);
        binding.firstButton.setText(firstButton);
        binding.secondButton.setText(secondButton);
        binding.firstButton.setOnClickListener(new CustomOnClickListener(v -> {
            dismissAllowingStateLoss();
            if(listener != null){
                listener.onFirstButtonClicked(requestCode);
            }
        }));
        binding.secondButton.setOnClickListener(new CustomOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (listener != null) {
                listener.onSecondButtonClicked(requestCode);
            }
        }));

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
                    + " must implement CashbackCustomDialog.InteractionListener");
        }
    }

    public interface InteractionListener {
        void onFirstButtonClicked(int requestCode);
        void onSecondButtonClicked(int requestCode);
    }
}
