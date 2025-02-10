package it.bancomatpay.sdkui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.math.BigDecimal;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.manager.task.model.Thresholds;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ActivityBcmSpendingLimitsBinding;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.ProgressBarSpendingLimits;

public class SpendingLimitsActivity extends GenericErrorActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(SpendingLimitsActivity.class.getSimpleName());
        ActivityBcmSpendingLimitsBinding binding = ActivityBcmSpendingLimitsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

        BancomatSdk sdk = BancomatSdk.getInstance();
        Thresholds thresholdsRubric = sdk.getProfileData().getP2PThresholds();
        if (thresholdsRubric != null) {
            setProgressBar(binding.profileSpendingProgressBarDayRubric, thresholdsRubric.getThresholdDayMinValue(), thresholdsRubric.getThresholdDayMaxValue(), thresholdsRubric.getThresholdDayValue());
            setProgressBar(binding.profileSpendingProgressBarMonthRubric, thresholdsRubric.getThresholdMonthMinValue(), thresholdsRubric.getThresholdMonthMaxValue(), thresholdsRubric.getThresholdMonthValue());
            binding.maxTransaction.setText(getString(R.string.profile_spending_limits_max_limit, format(thresholdsRubric.getThresholdTransactionMaxValue())));
        }

        Thresholds thresholdsMerchant = sdk.getProfileData().getP2BThresholds();
        if (thresholdsMerchant != null) {
            setProgressBar(binding.profileSpendingProgressBarDayMerchant, thresholdsMerchant.getThresholdDayMinValue(), thresholdsMerchant.getThresholdDayMaxValue(), thresholdsMerchant.getThresholdDayValue());
            setProgressBar(binding.profileSpendingProgressBarMonthMerchant, thresholdsMerchant.getThresholdMonthMinValue(), thresholdsMerchant.getThresholdMonthMaxValue(), thresholdsMerchant.getThresholdMonthValue());
        }

        //TODO Verificare come gestire la doppia textview per limite massimo rubrica/merchant
        //maxTransaction.setText(getString(R.string.profile_spending_limits_max_limit, format(thresholdsMerchant.getThresholdTransactionMaxValue())));

    }

    private void setProgressBar(ProgressBarSpendingLimits progressBar, long min, long max, long current) {
        progressBar.setRangeProgress(min, max, current);
        progressBar.setMinPrice(format(current));
        progressBar.setMaxPrice(format(max));
    }

    private String format(long value) {
        BigDecimal bigDecimal = BigDecimalUtils.getBigDecimalFromCents((int) value);
        return StringUtils.getFormattedValue(bigDecimal);
    }

}
