package it.bancomat.pay.consumer.activation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.FragmentChooseActivationCodeBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class ChooseActivationModeFragment extends Fragment {

    private FragmentChooseActivationCodeBinding binding;

    private InteractionListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseActivationCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.homeBankingText.setText(getString(R.string.home_banking_text_subtitle));

        binding.openQrcodeCamera.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null) {
                listener.openQrCode();
            }
        }));

        binding.insertHomeBanking.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null) {
                listener.insertHomeBanking();
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
                    + " must implement ChooseActivationModeFragment.InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface InteractionListener {
        void openQrCode();

        void insertHomeBanking();
    }

}

