package it.bancomat.pay.consumer.activation.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.List;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.adapter.DataBankListAdapter;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityActivableBankListBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PRE_LOGIN_SELECT_BANK;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_SEARCH_TEXT;

public class ActivableBankListActivity extends AppGenericErrorActivity implements DataBankListAdapter.InteractionListener {

    private ActivityActivableBankListBinding binding;

    private static final String TAG = ActivableBankListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivableBankListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Prevent keyboard open
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.activationBankList.setLayoutManager(new LinearLayoutManager(this));
        List<DataBank> dataBank = DataBankManager.getSubscribedDataBankList();
        //Aggiunta nuovo DataBank per renderizzare ultima riga testuale
        /*if (!TextUtils.isEmpty(dataBank.get(dataBank.size() - 1).getBankUUID())) {
            dataBank.add(new DataBank());
        }*/
        DataBankListAdapter adapter = new DataBankListAdapter(dataBank, this);
        binding.activationBankList.setAdapter(adapter);

        binding.editTextSearch.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                adapter.getFilter().filter(text);

                if (text.length() > 0) {
                    if (binding.cancelButtonSearch.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, AnimationFadeUtil.DEFAULT_DURATION);
                    }
                } else {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
                }
            }
        };
        binding.editTextSearch.addTextChangedListener(textWatcher);

        binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
            binding.editTextSearch.getText().clear();
            AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
        }));

        binding.activationPrivacy.setOnClickListener(new CustomOnClickListener(v -> ActivationFlowManager.goToShowTermsAndConditions(this, getString(R.string.privacy_url))));
        binding.activationTermsAndConditions.setOnClickListener(new CustomOnClickListener(v -> ActivationFlowManager.goToShowTermsAndConditions(this, getString(R.string.terms_and_conditions_url))));
        binding.textDiscoverBanks.setOnClickListener(new CustomOnClickListener(v -> ActivationFlowManager.goToNoBcmPayBankList(this)));

    }

    @Override
    public void onListBankInteraction(DataBank dataBank) {
        BancomatPayApiInterface.Factory.getInstance().storeBankUuidChoosed(dataBank.getBankUUID());
        HashMap<String, String> mapEventParams = new HashMap<>();
        if (!binding.editTextSearch.getText().toString().isEmpty()) {
            mapEventParams.put(PARAM_SEARCH_TEXT, binding.editTextSearch.getText().toString());
        }
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PRE_LOGIN_SELECT_BANK, mapEventParams, true);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            CustomLogger.d(TAG, "doUserMonitoringTask success");
                        } else {
                            CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                        }
                    }
                },
                dataBank.getBankUUID(),
                UserMonitoringConstants.ACTIVATION_TAG,
                UserMonitoringConstants.ACTIVATION_BANK_SELECTED,
                "");
        addTask(t);
        ActivationFlowManager.goToActivation(this, dataBank);
    }

}
