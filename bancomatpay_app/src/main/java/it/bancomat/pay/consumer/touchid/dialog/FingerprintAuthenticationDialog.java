package it.bancomat.pay.consumer.touchid.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FingerprintDialogContainerBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class FingerprintAuthenticationDialog extends DialogFragment {

    private FingerprintDialogContainerBinding binding;

    public static final String ARG_DESCRIPTION = "ARG_DESCRIPTION";

    private static final int MAX_FINGERPRINT_ATTEMPTS = 5;
    private int attemptsNumber;

    public final static int LONG_TIME_OUT = 2300;
    public final static int SHORT_TIME_OUT = 800;

    private Handler handler;
    private String description;

    public interface InteractionListener {
        void onFingerprintDialogCancelPressed();
    }

    InteractionListener listener;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateStatus event) {
        if (event.isSuccess()) {
            showSuccess(event.getMessage());
        } else {
            attemptsNumber++;
            if (attemptsNumber >= MAX_FINGERPRINT_ATTEMPTS) {
                showError(getString(R.string.common_check_fingerprint_authentication_locked), true);
            } else {
                showError(event.getMessage(), event.isDismiss());
            }
        }
    }

    public static FingerprintAuthenticationDialog newInstance(String bankUuid) {
        FingerprintAuthenticationDialog fragment = new FingerprintAuthenticationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_DESCRIPTION, bankUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        }
        if (getArguments() != null) {
            description = getArguments().getString(ARG_DESCRIPTION);
        }

        attemptsNumber = 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FingerprintDialogContainerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!TextUtils.isEmpty(description)) {
            binding.fingerprintStatus.setText(description);
        }

        binding.textCancelAuthentication.setOnClickListener(new CustomOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (listener != null) {
                listener.onFingerprintDialogCancelPressed();
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        binding = null;
    }

    private void showSuccess(String success) {
        binding.fingerprintStatus.setText(success);
        binding.fingerprintIcon.setImageResource(R.drawable.fingerprint_recognized);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        binding.textCancelAuthentication.setOnClickListener(new CustomOnClickListener(null));
        AnimationFadeUtil.startFadeOutAnimationV1(binding.textCancelAuthentication, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
    }

    private void showError(String error, boolean dismiss) {
        binding.fingerprintStatus.setText(error);
        binding.fingerprintIcon.setImageResource(R.drawable.fingerprint_not_recognized);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        if (dismiss) {
            handler.postDelayed(() -> {
                dismissAllowingStateLoss();
                if (listener != null) {
                    listener.onFingerprintDialogCancelPressed();
                }
            }, LONG_TIME_OUT);
        } else {
            handler.postDelayed(() -> {
                if (isVisible()) {
                    binding.fingerprintIcon.setImageResource(R.drawable.fingerprint_for_popup);
                }
            }, SHORT_TIME_OUT);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            listener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FingerprintAuthenticationDialog.InteractionListener");
        }
    }

}
