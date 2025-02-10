package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentInsertManualActivationCodeBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class InsertManualActivationCodeFragment extends Fragment {

    private FragmentInsertManualActivationCodeBinding binding;

    private static final String ARG_BANK_UUID = "ARG_BANK_UUID";
    private static final int ACTIVATION_CODE_MIN_LENGTH = 16;

    private String bankUuid;
    private InteractionListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bankUuid = getArguments().getString(ARG_BANK_UUID);
        }
    }

    public static InsertManualActivationCodeFragment newInstance(String bankUuid) {
        InsertManualActivationCodeFragment fragment = new InsertManualActivationCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BANK_UUID, bankUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInsertManualActivationCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.homeBankingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.confirmButton.setEnabled(s.length() >= ACTIVATION_CODE_MIN_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> {
            String activationCode = new String(Base64.encode(binding.homeBankingEditText.getText().toString().getBytes(), Base64.NO_WRAP));
            if (listener != null) {
                listener.sendHomeBankingCode(activationCode, bankUuid);
            }
        }));

        if (getActivity() != null) {
            KeyboardVisibilityEvent.setEventListener(
                    getActivity(), this, isOpen -> {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.confirmButton.getLayoutParams();
                        if (isOpen) {
                            binding.confirmButton.setBackgroundResource(R.drawable.button_square_background_state_list);
                            params.leftMargin = 0;
                            params.rightMargin = 0;
                            params.bottomMargin = 0;
                            binding.scrollInsertManual.postDelayed(() -> binding.scrollInsertManual.fullScroll(ScrollView.FOCUS_DOWN), 150);
                        } else {
                            binding.confirmButton.setBackgroundResource(R.drawable.button_round_background_state_list);
                            params.leftMargin = (int) getResources().getDimension(it.bancomatpay.sdkui.R.dimen.activity_horizontal_margin);
                            params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                            params.bottomMargin = (int) getResources().getDimension(R.dimen.size_25);
                        }
                    });
        }
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
                    + " must implement InsertManualActivationCodeFragment.InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface InteractionListener {
        void sendHomeBankingCode(String code, String bankUuid);
    }

}

