package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.huawei.hms.support.sms.ReadSmsManager;

import it.bancomat.pay.consumer.activation.GoogleSmsBroadcastReceiver;
import it.bancomat.pay.consumer.activation.HuaweiSmsBroadcastReceiver;
import it.bancomat.pay.consumer.activation.OtpReceiveListener;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.utilities.ActivityTaskManager;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentInsertOtpBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.LabelTelephone;

import static android.view.View.INVISIBLE;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class InsertOtpFragment extends Fragment implements LabelTelephone.LabelListener, OtpReceiveListener {

    private static final String ARG_PHONE = "ARG_PHONE";
    private static final String ARG_BANK_UUID = "ARG_BANK_UUID";
    private static final String TAG = InsertOtpFragment.class.getSimpleName();

    private FragmentInsertOtpBinding binding;

    private String phone;
    private String bankUUID;

	private InteractionListener listener;
	private final int maxLength = 7;

    private Handler handlerUi;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			phone = getArguments().getString(ARG_PHONE);
			bankUUID = getArguments().getString(ARG_BANK_UUID);
		}
	}

	public static InsertOtpFragment newInstance(String phone, String bankUUID) {
		InsertOtpFragment fragment = new InsertOtpFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PHONE, phone);
		args.putString(ARG_BANK_UUID, bankUUID);
		fragment.setArguments(args);
		return fragment;
	}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInsertOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handlerUi = new Handler();

        binding.keyboardCustom.setKeyboardType(KeyboardType.OTP_KEYBOARD);
        binding.keyboardCustom.setKeyboardListener(binding.insertNumberEditText);
        binding.insertNumberEditText.setInitialText("");
        binding.insertNumberEditText.setHintText(getString(R.string.insert_otp_hint), ContextCompat.getColor(requireContext(), R.color.text_hint_color));
        binding.insertNumberEditText.setMaxLength(maxLength);
        binding.insertNumberEditText.setLabelListener(this);
        binding.otpTextLabel.setText(getString(R.string.insert_otp_text, phone));
        binding.cancelButtonInsertNumber.setOnClickListener(v -> binding.insertNumberEditText.onDeleteCharacter());

        binding.cancelButtonInsertNumber.setOnLongClickListener(v -> {
            binding.insertNumberEditText.onDeleteAllText();
            return true;
        });

        binding.resendOtpButton.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null) {
                listener.onResendOtp(phone);
            }
        }));

        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            initHuaweiSmsReceiver();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            initGoogleSmsReceiver();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handlerUi.removeCallbacksAndMessages(null);
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            listener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InsertOtpFragment.InteractionListener");
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
            if (number.length() == maxLength - 1) {
                binding.keyboardCustom.post(() -> binding.keyboardCustom.setButtonNextEnabled(true));
                binding.keyboardCustom.setButtonNextClickListener(v -> {
                    if (listener != null) {
                        listener.onOtpInserted(phone, binding.insertNumberEditText.getValue());
                    }
                });
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
            if (binding.errorMessage.getVisibility() == View.VISIBLE) {
                AnimationFadeUtil.startFadeOutAnimationV1(binding.errorMessage, DEFAULT_DURATION, INVISIBLE);
                binding.imageErrorBackground.setVisibility(INVISIBLE);
            }
        }
    }

    private void initGoogleSmsReceiver() {
        GoogleSmsBroadcastReceiver.setOtpListener(this);
        SmsRetrieverClient client = SmsRetriever.getClient(requireContext());
        com.google.android.gms.tasks.Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> CustomLogger.d(TAG, "Waiting for the OTP"));
        task.addOnFailureListener(e -> CustomLogger.e(TAG, "Cannot Start SMS Retriever: " + e.toString()));
    }

    private void initHuaweiSmsReceiver() {
        HuaweiSmsBroadcastReceiver.setOtpListener(this);
        com.huawei.hmf.tasks.Task<Void> task = ReadSmsManager.start(requireActivity());
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                // The service is enabled successfully. Perform other operations as needed.
                CustomLogger.d(TAG, "Waiting for the OTP");
            } else {
                CustomLogger.e(TAG, "Cannot Start SMS Retriever: " + (task1.getException() != null ? task1.getException().toString() : ""));
            }
        });
    }

    public void onError(String error) {
        binding.errorMessage.setVisibility(View.VISIBLE);
        binding.errorMessage.setText(error);
        binding.imageErrorBackground.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOtpReceived(String otp) {
        CustomLogger.d(TAG, "OTP Received: " + (!TextUtils.isEmpty(otp) ? otp : "otp is null"));

        handlerUi.post(() -> {
            binding.insertNumberEditText.onDeleteAllText();
            if (!TextUtils.isEmpty(otp)) {
                binding.insertNumberEditText.setText(otp);
            }

            Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                Log.d(TAG, "doUserMonitoringTask success");
                            } else {
                                Log.e(TAG, "Error: doUserMonitoring failed");
                            }
                        }
                    },
                    bankUUID,
                    UserMonitoringConstants.ACTIVATION_TAG,
                    UserMonitoringConstants.ACTIVATION_OTP_AUTOFILL,
                    "");
            ((ActivityTaskManager) requireActivity()).addTask(t);

        });
    }

    @Override
    public void onOtpTimeOut() {
        CustomLogger.e(TAG, "Sms otp receiver timeout");
    }

    public interface InteractionListener {
        void onOtpInserted(String phone, String otp);

        void onResendOtp(String phone);
    }

}

