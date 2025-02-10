package it.bancomatpay.sdkui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.adapter.MultiIbanAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmAccountsManagementBinding;
import it.bancomatpay.sdkui.utilities.LogoBankSingleton;

public class AccountsManagementActivity extends GenericErrorActivity implements MultiIbanAdapter.IbanListClickListener {

    private static final String TAG = AccountsManagementActivity.class.getSimpleName();

    private MultiIbanAdapter adapter;
    private List<InstrumentData> instruments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(AccountsManagementActivity.class.getSimpleName());
        ActivityBcmAccountsManagementBinding binding = ActivityBcmAccountsManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

        UserData userData = ApplicationModel.getInstance().getUserData();
        if (userData != null && userData.getInstruments() != null) {
            instruments = userData.getInstruments();
        } else {
            instruments = new ArrayList<>();
        }
        Drawable logoBank = LogoBankSingleton.getInstance().getLogoBank();
        adapter = new MultiIbanAdapter(instruments, logoBank, this);
        binding.ibanList.setLayoutManager(new LinearLayoutManager(this));
        binding.ibanList.setAdapter(adapter);

    }

    @Override
    public void onIbanClicked(InstrumentData clickedIban) {
        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
        if (clickedIban.getIbanCategory() == InstrumentData.EIbanCategory.IBAN_SEND_MONEY) {

            LoaderHelper.showLoader(this);
            sdk.doSetOutgoingDefaultInstrument(this, result -> {
                LoaderHelper.dismissLoader();
                if (result != null) {
                    if (result.isSuccess()) {
                        for (InstrumentData instrument : instruments) {
                            if (instrument.isDefaultOutgoing()) {
                                instrument.setDefaultOutgoing(false);
                                CustomLogger.d(TAG, "IBAN " + instrument.getIban() + " Outgoing = false");
                            }
                        }
                        clickedIban.setDefaultOutgoing(true);
                        CustomLogger.d(TAG, "IBAN " + clickedIban.getIban() + " Outgoing = false");
                        ApplicationModel.getInstance().getUserData().setInstruments(instruments);
                        adapter.notifyDataSetChanged();
                    } else if (result.isSessionExpired()) {
                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                    } else {
                        showError(result.getStatusCode());
                    }
                }
            }, clickedIban.getCipheredIban(), SessionManager.getInstance().getSessionToken());

        } else if (clickedIban.getIbanCategory() == InstrumentData.EIbanCategory.IBAN_GET_MONEY) {

            LoaderHelper.showLoader(this);
            sdk.doSetIncomingDefaultInstrument(this, result -> {
                LoaderHelper.dismissLoader();
                if (result != null) {
                    if (result.isSuccess()) {
                        for (InstrumentData instrument : instruments) {
                            if (instrument.isDefaultIncoming()) {
                                instrument.setDefaultIncoming(false);
                                CustomLogger.d(TAG, "IBAN " + instrument.getIban() + " Incoming = false");
                            }
                        }
                        clickedIban.setDefaultIncoming(true);
                        CustomLogger.d(TAG, "IBAN " + clickedIban.getIban() + " Incoming = true");
                        ApplicationModel.getInstance().getUserData().setInstruments(instruments);
                        ApplicationModel.getInstance().getUserData().setDefaultReceiverOtherBank(false);
                        adapter.notifyDataSetChanged();
                    } else if (result.isSessionExpired()) {
                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                    } else {
                        showError(result.getStatusCode());
                    }
                }
            }, clickedIban.getCipheredIban(), SessionManager.getInstance().getSessionToken());
        }
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

}
