package it.bancomatpay.sdkui.activities.directdebit;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Date;

import it.bancomatpay.sdk.manager.network.dto.response.DirectDebitStatus;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityDirectDebitMerchantDetailBinding;
import it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager;
import it.bancomatpay.sdkui.model.DirectDebit;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class DirectDebitMerchantDetailActivity extends GenericErrorActivity {

    DirectDebit merchantData;

    private ActivityDirectDebitMerchantDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectDebitMerchantDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        binding.toolbarSimple.setCenterImageVisibility(true);
        merchantData = (DirectDebit) getIntent().getSerializableExtra(DirectDebitFlowManager.DIRECT_DEBIT_HISTORY_DATA);
        initLayout();

        clearLightStatusBar(binding.layoutMerchantBackground, R.color.blue_statusbar_color);
    }

    private void initLayout() {
        binding.merchantName.setText(merchantData.getMerchantName());
        binding.ddMerchantType.setText(merchantData.getMerchantDescription());
        switch (merchantData.getMerchantStatus()) {
            case ENABLED:
                binding.ddMerchantDetailStatus.setText(StringUtils.capitalizeString(getString(R.string.direct_debit_merchant_detail_status_active)));
                break;
            case DISABLED:
                binding.ddMerchantDetailStatus.setText(StringUtils.capitalizeString(getString(R.string.direct_debit_merchant_detail_status_inactive)));
                break;
        }

        binding.ddTextIbanMerchant.setText(merchantData.getMerchantIban());
        binding.directDebitAuthDate.setText(getDateStringFormatted(merchantData.getDisplayDate()));
        if (merchantData.getMerchantStatus().equals(DirectDebitStatus.DISABLED)) {
            binding.directDebitEndingDate.setText(getDateStringFormatted(merchantData.getMerchantEndingDate()));
        } else {
            binding.directDebitEndingDateBox.setVisibility(View.GONE);
            binding.ddLineAuth.setVisibility(View.GONE);

        }
    }


    private String getDateStringFormatted(Date date) {
        return StringUtils.getDateStringFormatted(date, "dd MMMM yyyy");
    }

}
