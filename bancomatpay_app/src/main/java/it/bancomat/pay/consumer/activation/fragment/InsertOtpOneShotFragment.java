package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentInsertOtpBinding;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.LabelTelephone;

import static android.view.View.INVISIBLE;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class InsertOtpOneShotFragment extends Fragment implements LabelTelephone.LabelListener {

	private static final String ARG_PHONE = "ARG_PHONE";

	private FragmentInsertOtpBinding binding;

	private String phone;

	private InteractionListener listener;
	int maxLength = 7;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			phone = getArguments().getString(ARG_PHONE);
		}
	}

	public static InsertOtpOneShotFragment newInstance(String phone) {
		InsertOtpOneShotFragment fragment = new InsertOtpOneShotFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PHONE, phone);
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

		binding.keyboardCustom.setKeyboardType(KeyboardType.OTP_KEYBOARD);
		binding.keyboardCustom.setKeyboardListener(binding.insertNumberEditText);
		binding.insertNumberEditText.setInitialText("");
		binding.insertNumberEditText.setHintText(getString(R.string.insert_otp_hint), ContextCompat.getColor(requireContext(), R.color.text_hint_color));
		binding.insertNumberEditText.setMaxLength(maxLength);
		binding.insertNumberEditText.setLabelListener(this);
		binding.otpTextLabel.setText(getString(R.string.insert_otp_text, phone));
		binding.cancelButtonInsertNumber.setOnClickListener(new CustomOnClickListener(v -> binding.insertNumberEditText.onDeleteCharacter()));

		binding.cancelButtonInsertNumber.setOnLongClickListener(v -> {
			binding.insertNumberEditText.onDeleteAllText();
			return true;
		});

		binding.resendOtpButton.setOnClickListener(new CustomOnClickListener(v -> {
			if (listener != null) {
				listener.onResendOtpOneShot(phone);
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
					+ " must implement InsertOtpOneShotFragment.InteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override
	public void onNumberInserted(String number) {
		if (number.length() == maxLength - 1) {
			binding.keyboardCustom.post(() -> binding.keyboardCustom.setButtonNextEnabled(true));
			binding.keyboardCustom.setButtonNextClickListener(v ->
					listener.onOtpOneShotInserted(binding.insertNumberEditText.getValue()));
		} else {
			binding.keyboardCustom.post(() -> binding.keyboardCustom.setButtonNextEnabled(false));
		}

	}

	@Override
	public void onNumberDeleted(boolean deleteAll) {
		if (deleteAll) {
			binding.keyboardCustom.reset();
		} else {
			binding.keyboardCustom.deleteCharacter();
		}
		if (binding.errorMessage.getVisibility()== View.VISIBLE){
			AnimationFadeUtil.startFadeOutAnimationV1(binding.errorMessage, DEFAULT_DURATION, INVISIBLE);
			binding.imageErrorBackground.setVisibility(INVISIBLE);
		}
	}

	public void onError(String error) {
		binding.errorMessage.setVisibility(View.VISIBLE);
		binding.errorMessage.setText(error);
		binding.imageErrorBackground.setVisibility(View.VISIBLE);
	}

	public interface InteractionListener {
		void onOtpOneShotInserted(String otp);

		void onResendOtpOneShot(String phone);
	}

}

