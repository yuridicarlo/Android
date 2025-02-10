package it.bancomatpay.sdkui.activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.events.CheckRootWarningEvent;
import it.bancomatpay.sdk.manager.events.TaskEventError;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMKeepAliveCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.ErrorMapper;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.NO_RETE;

public abstract class GenericErrorActivity extends AppCompatActivity {

    boolean lockExitApp = true;
    public boolean lockGenericError = true;

    String activityName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PayCore.setAppContext(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SnackbarUtil.setMarginTop(50);

        BCMKeepAliveCallback.getInstance().getAuthenticationListener().onKeepAliveReceived(activityName);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskEventError event) {
        onCompleteWithError(event.getError());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckRootWarningEvent event) {
        manageError(StatusCode.Mobile.ROOTED);
    }

    protected void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void onCompleteWithError(Error e) {
        if (e instanceof ExtendedError) {
            StatusCodeInterface statusCodeInterface = ((ExtendedError) e).getStatusCodeInterface();
            manageError(statusCodeInterface);
        } else {
            if (BancomatSdk.getInstance().isDeviceOnline(this)) {
                manageError(StatusCode.Http.GENERIC_ERROR);
            } else {
                manageError(NO_RETE);
            }
        }
    }

    public void manageError(StatusCodeInterface statusCodeInterface) {
        if (statusCodeInterface instanceof StatusCode.Mobile) {
            StatusCode.Mobile mobile = (StatusCode.Mobile) statusCodeInterface;
            if (mobile == StatusCode.Mobile.ROOTED) {
                showError(statusCodeInterface);
            } else {
                showError(StatusCode.Mobile.GENERIC_ERROR);
            }
        }
        if (statusCodeInterface instanceof StatusCode.Server) {
            StatusCode.Server server = (StatusCode.Server) statusCodeInterface;
            switch (server) {
                case EXIT_APP:
                    showExitAppError();
                    break;
                case P2P_USER_NOT_ENABLED_ON_BCM_PAY:
                    showUserNotEnabledError();
                    break;
                case NO_ACTIVE_IBAN:
                    showNoMoreIbanActiveError();
                case WRONG_APP_VERSION:
                    showError(StatusCode.Server.WRONG_APP_VERSION);
                    break;
                default:
                    showError(StatusCode.Server.GENERIC_SERVER_ERROR);
            }
        }
        if (statusCodeInterface instanceof StatusCode.Http) {
            StatusCode.Http http = (StatusCode.Http) statusCodeInterface;
            if (http == NO_RETE) {
                showError(NO_RETE);
            } else {
                showError(StatusCode.Http.GENERIC_ERROR);
            }
        }
    }

    public synchronized void showUserNotEnabledError() {
        if (lockExitApp) {
            lockExitApp = false;
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setMessage(R.string.deactivation_message)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        BancomatFullStackSdk sdk = BancomatFullStackSdk.getInstance();
                        sdk.resetBancomatSDK();
                        lockExitApp = true;

                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED);
                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                .goToHome(this, true, false, true);

                    })
                    .setCancelable(false);
            builder.showDialog(this);
        }
    }

    public synchronized void showNoMoreIbanActiveError() {
        if (lockExitApp) {
            lockExitApp = false;
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setMessage(R.string.no_more_active_iban)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        BancomatFullStackSdk sdk = BancomatFullStackSdk.getInstance();
                        sdk.resetBancomatSDK();
                        lockExitApp = true;

                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED);
                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                .goToHome(this, true, false, true);

                    })
                    .setCancelable(false);
            builder.showDialog(this);
        }
    }

    public synchronized void showExitAppError() {
        if (lockExitApp) {
            lockExitApp = false;
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setMessage(R.string.exit_app_error_message)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        BancomatFullStackSdk sdk = BancomatFullStackSdk.getInstance();
                        sdk.resetBancomatSDK();
                        lockExitApp = true;

                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED);
                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                .goToHome(this, true, false, true);

                    })
                    .setCancelable(false);
            builder.showDialog(this);
        }
    }

    public synchronized void showError(StatusCodeInterface statusCodeInterface) {
        if (lockGenericError) {
            lockGenericError = false;

            int idString = ErrorMapper.getStringFromStatusCode(statusCodeInterface);
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error_title);
            builder.setCancelable(false);
            builder.setMessage(idString)
                    .setPositiveButton(R.string.ok, (dialog, id) -> lockGenericError = true);
            builder.show();*/

            SnackbarUtil.showSnackbarMessageCustom(this, getString(idString), v -> lockGenericError = true);

            /*if (statusCodeInterface.equals(NO_RETE)) {
                EventBus.getDefault().post(new NoNetworkEvent());
            }*/

        }
    }

    public synchronized void showErrorAndDoAction(StatusCodeInterface statusCodeInterface, DialogInterface.OnClickListener clickListener) {
        if (lockGenericError) {
            lockGenericError = false;

            int idString = ErrorMapper.getStringFromStatusCode(statusCodeInterface);
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title);
            builder.setCancelable(false);
            builder.setMessage(idString)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        clickListener.onClick(dialog, which);
                        lockGenericError = true;
                    });
            builder.showDialog(this);

	        /*SnackbarUtil.showSnackbarActionCustom(this, getString(idString),
			        getString(R.string.ok), v -> {
				        clickListener.onClick(v);
				        lockGenericError = true;
			        }, null, null);*/

        }
    }

    public void setLightStatusBar(View view, int color) {
        if (Build.VERSION.SDK_INT >= 30){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
            WindowInsetsController ic = getWindow().getInsetsController();
            if(ic!= null){
                ic.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        } else if(Build.VERSION.SDK_INT >= 23){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, color));

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }


    public void clearLightStatusBar(View view, int color) {
        if (Build.VERSION.SDK_INT >= 30){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
            WindowInsetsController ic = getWindow().getInsetsController();
            if(ic!= null){
                ic.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        } else if(Build.VERSION.SDK_INT >= 23){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

}
