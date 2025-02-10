package it.bancomat.pay.consumer.activation.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityNotBcmPayUserBinding;

public class NotBcmPayUserActivity extends AppCompatActivity {

    public static final String BANK_LABEL = "BANK_LABEL";

    String bankLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotBcmPayUserBinding binding = ActivityNotBcmPayUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> ActivationFlowManager.goToIntro(this));

        bankLabel = getIntent().getStringExtra(BANK_LABEL);

        binding.textBank.setText(getString(R.string.not_active_user_on_bank_chosen, bankLabel));
    }

}
