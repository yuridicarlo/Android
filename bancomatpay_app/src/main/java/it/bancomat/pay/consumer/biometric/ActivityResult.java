package it.bancomat.pay.consumer.biometric;

import android.content.Intent;

import androidx.annotation.Nullable;

public class ActivityResult {

    int requestCode;

    int resultCode;

    @Nullable
    Intent data;

    public ActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    @Nullable
    public Intent getData() {
        return data;
    }
}
