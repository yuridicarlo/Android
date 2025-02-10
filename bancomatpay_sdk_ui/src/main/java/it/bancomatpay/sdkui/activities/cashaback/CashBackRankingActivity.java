package it.bancomatpay.sdkui.activities.cashaback;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.CashbackData;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityCashbackRankingBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.R;


import static it.bancomatpay.sdkui.flowmanager.CashbackDigitalPaymentFlowManager.CASHBACK_DATA;

public class CashBackRankingActivity extends GenericErrorActivity {

    private ActivityCashbackRankingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCashbackRankingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CashbackData mCashbackData = (CashbackData) getIntent().getSerializableExtra(CASHBACK_DATA);

        binding.toolbarSimple.setOnClickLeftImageListener(new CustomOnClickListener(v -> finish()));

        if (mCashbackData != null) {
            if (mCashbackData.getPeriodStartDate() != null && mCashbackData.getPeriodEndDate() != null) {
                String periodStartDate = StringUtils.getDateStringFormatted(mCashbackData.getPeriodStartDate(), "dd MMM yyy");
                String periodEndDate = StringUtils.getDateStringFormatted(mCashbackData.getPeriodEndDate(), "dd MMM yyy");
                binding.cashbackValidityTime.setText(periodStartDate.concat(" - ").concat(periodEndDate));
            }
            binding.cashbackRankingAmount.setText(
                    StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(mCashbackData.getCashback()))
            );

            String numberRanking = StringUtils.getNumberThousandsFormatted(mCashbackData.getRanking());
            binding.ranking.setText(numberRanking);

            String participantsFormatted = StringUtils.getNumberThousandsFormatted(mCashbackData.getParticipantsNumber());
            binding.totalParticipant.setText(participantsFormatted);
        }
        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}