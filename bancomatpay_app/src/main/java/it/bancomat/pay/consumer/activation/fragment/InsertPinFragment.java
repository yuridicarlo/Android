package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.bancomat.pay.consumer.widget.LabelPasswordLogin;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentInsertPinBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;

public class InsertPinFragment extends Fragment implements LabelPasswordLogin.LabelListener {

    private InteractionListener listener;

    private FragmentInsertPinBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInsertPinBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.labelPin.changeColor();
        binding.keyboardPin.changeColor();

        binding.keyboardPin.setKeyboardListener(binding.labelPin);
        binding.labelPin.setMaxElements(5);
        binding.labelPin.setListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setStepRepeatPin() {
        binding.textTitle.setText(getString(R.string.bcmpay_activation_label));
        binding.textDescription.setText(getString(R.string.repeat_pin_text));
        binding.keyboardPin.reset();
    }

    public void setErrorRepeatPin() {
        binding.textError.setText(getString(R.string.pin_not_corresponding));
        if (binding.textError.getVisibility() != View.VISIBLE) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
        }
        binding.labelPin.shake();
        binding.keyboardPin.reset();
        binding.textDescription.setText(getString(R.string.choose_pin_text));
    }

    public void resetKeyboard() {
        binding.keyboardPin.reset();
    }

    public void resetError() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
    }

    public void setErrorInvalidPin() {
        binding.textError.setText(getString(R.string.invalid_pin_format));
        if (binding.textError.getVisibility() != View.VISIBLE) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
        }
        binding.labelPin.shake();
        binding.keyboardPin.reset();
    }

    @Override
    public void onPinInserted(String pin) {
        if (listener != null) {
            listener.onPinInserted(pin);
        }
    }

    @Override
    public void onDeleteLongClicked() {
        Vibrator v = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(30, 100));
        } else {
            //deprecated in API 26
            v.vibrate(30);
        }
    }

    @Override
    public void onStartEditing() {
        if (listener != null) {
            listener.onStartEditing();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InsertOtpFragment.InteractionListener) {
            listener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InsertPinFragment.InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface InteractionListener {
        void onPinInserted(String pin);

        void onStartEditing();
    }

}
