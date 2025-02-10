package it.bancomatpay.sdkui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdkui.databinding.ActivityBcmSendMoneyV2Binding;

public class SendMoneyActivity extends GenericErrorActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(SendMoneyActivity.class.getSimpleName());
        ActivityBcmSendMoneyV2Binding binding = ActivityBcmSendMoneyV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contactList(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (frequentItems != null) {
            BancomatSdk.getInstance().updateUserFrequent(frequentItems);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactList(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                changeStyle(true);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

}
