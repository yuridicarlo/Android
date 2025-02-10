package it.bancomat.pay.consumer.init;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.huawei.hms.support.sms.ReadSmsManager;

import it.bancomat.pay.consumer.activation.GoogleSmsBroadcastReceiver;
import it.bancomat.pay.consumer.activation.HuaweiSmsBroadcastReceiver;
import it.bancomat.pay.consumer.activation.OtpReceiveListener;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;

public class ActivationActivity extends BaseInitActivity implements OtpReceiveListener {

    private final static String TAG = ActivationActivity.class.getSimpleName();
    private static final int SWIPE_MIN_DISTANCE = -50;
    private GestureDetectorCompat mDetector;
    private boolean dialogVisible;

    InitViewModel initViewModel;

    @Override
    int getContentView() {
        return R.layout.activity_init_activation;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel = new ViewModelProvider(this).get(InitViewModel.class);

        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        dialogVisible = false;
        Animation animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
        View v = findViewById(R.id.root);
        v.startAnimation(animSlideUp);

        MyGestureDetector myGestureListener = new MyGestureDetector();
        mDetector = new GestureDetectorCompat(this,  myGestureListener);
        v.setOnTouchListener(myGestureListener);


        v.setOnTouchListener(new MyGestureDetector());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //setto il background della status bar
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.sdk_background));
        }

    }

    @Override
    public void finish() {
        super.finish();
        //elimino transazione di uscita
        overridePendingTransition(0, 0);
    }

    @Override
    public void onOtpReceived(String otp) {
        initViewModel.userMonitoring(initViewModel.getBankUUID(), UserMonitoringConstants.ACTIVATION_TAG, UserMonitoringConstants.ACTIVATION_OTP_AUTOFILL, "" );
        initViewModel.getKeyboardCodeObservable().setCode(otp);
    }

    @Override
    public void onOtpTimeOut() {
        CustomLogger.e(TAG, "Sms otp receiver timeout");
    }

    public void initSmsReceiver() {
        if(!initViewModel.isSMSReceiverActive()) {
            if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
                initHuaweiSmsReceiver();
            } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
                initGoogleSmsReceiver();
            }
            int timeoutMillis = 5 * 60 * 1000; // 5 minutes = timeout of the receiver, as there is no api
            new Handler().postDelayed(() -> initViewModel.setSMSReceiverActive(false), timeoutMillis);
        }
    }

    private void initGoogleSmsReceiver() {
        GoogleSmsBroadcastReceiver.setOtpListener(this);
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        com.google.android.gms.tasks.Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {
            CustomLogger.d(TAG, "Waiting for the OTP");
            initViewModel.setSMSReceiverActive(true);
        });
        task.addOnFailureListener(e -> {
            initViewModel.setSMSReceiverActive(false);
            CustomLogger.e(TAG, "Cannot Start SMS Retriever: " + e.toString());
        });
    }

    private void initHuaweiSmsReceiver() {
        HuaweiSmsBroadcastReceiver.setOtpListener(this);
        com.huawei.hmf.tasks.Task<Void> task = ReadSmsManager.start(this);
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                // The service is enabled successfully. Perform other operations as needed.
                initViewModel.setSMSReceiverActive(true);
                CustomLogger.d(TAG, "Waiting for the OTP");
            } else {
                initViewModel.setSMSReceiverActive(false);
                CustomLogger.e(TAG, "Cannot Start SMS Retriever: " + (task1.getException() != null ? task1.getException().toString() : ""));
            }
        });
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

        @Override
        public boolean onDown(MotionEvent event) {
            //CustomLogger.d(TAG,"onDown: " + event.toString());
            return true;
        }

        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            CustomLogger.d("saddsa", "distange" + distanceY);
            if(distanceY < SWIPE_MIN_DISTANCE){
                showConfirmAbortDialog();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            //CustomLogger.d(TAG, "onFling: " + event1.toString() + event2.toString());
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mDetector.onTouchEvent(event);
        }
    }


    public synchronized void showConfirmAbortDialog(){
        if(!dialogVisible) {
            dialogVisible = true;
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.confirm_abort_dialog_title)
                    .setMessage(R.string.confirm_abort_dialog_description)
                    .setPositiveButton(R.string.confirm_abort_dialog_confirm, (dialog, id) -> {
                        dialog.dismiss();
                        Animation animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                        View v = findViewById(R.id.root);
                        v.startAnimation(animSlideUp);
                        animSlideUp.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                finish();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                    })
                    .setNegativeButton(R.string.confirm_abort_dialog_cancel, (dialog, id) -> {
                        dialog.dismiss();
                        dialogVisible = false;
                    })
                    .setCancelable(false);
            builder.showDialog(this);
        }

    }

    public void hideKeyboard() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
