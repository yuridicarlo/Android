package it.bancomat.pay.consumer.extended.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomatpay.consumer.databinding.ActivityChangePinSuccessBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class SettingsChangePinSuccessActivity extends AppGenericErrorActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChangePinSuccessBinding binding = ActivityChangePinSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonClose.setOnClickListener(new CustomOnClickListener(v -> onBackPressed()));
    }

}
