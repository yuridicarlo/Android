package it.bancomat.pay.consumer.utilities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.login.LoginFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApi;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.task.GetConfigurationBankFileTask;
import it.bancomat.pay.consumer.utilities.statuscode.AppStatusCode;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.events.CheckRootWarningEvent;
import it.bancomatpay.sdk.manager.events.TaskEventError;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.NO_RETE;

public abstract class AppGenericErrorActivity extends ActivityTaskManager {

    boolean lockExitApp = true;
    public boolean lockGenericError = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PayCore.setAppContext(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        SnackbarUtil.setMarginTop(50);
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
        onCompleteWithError(event.getTask(), event.getError());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckRootWarningEvent event) {
        manageError(StatusCode.Mobile.ROOTED);
    }

    @Override
    public void onCompleteWithError(Task<?> task, Error e) {
        if (task instanceof GetConfigurationBankFileTask) {
            super.onComplete(task);
        } else {
            super.onCompleteWithError(task, e);
            if (e instanceof ExtendedError) {
                StatusCodeInterface statusCodeInterface = ((ExtendedError) e).getStatusCodeInterface();
                manageError(statusCodeInterface);
            } else {
                if (BancomatPayApi.getInstance().isDeviceOnline(this)) {
                    manageError(StatusCode.Http.GENERIC_ERROR);
                } else {
                    manageError(NO_RETE);
                }
            }
        }
        LoaderHelper.dismissLoader();
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

        if (statusCodeInterface instanceof AppStatusCode.ServerExtended) {
            AppStatusCode.ServerExtended server = (AppStatusCode.ServerExtended) statusCodeInterface;
            if (server == AppStatusCode.ServerExtended.PIN_LOCKED) {
                LoginFlowManager.goToLostPin(this, true);
            } else {
                showError(StatusCode.Server.GENERIC_SERVER_ERROR);
            }
        }
    }

    public synchronized void showExitAppError() {
        if (lockExitApp) {
            lockExitApp = false;
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setMessage(R.string.exit_app_error_message)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        BancomatPayApi sdk = BancomatPayApi.getInstance();
                        sdk.deleteUserData();
                        //ActivationFlowManager.goToIntro(this);
                        lockExitApp = true;

                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbort(BancomatFullStackSdkInterfaceExtended.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED);
                        /*finishAffinity();*/
                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                .goToHome(this, true, false, true);

                    })
                    .setCancelable(false);
            builder.showDialog(this);
        }
    }

    public synchronized void showUserNotEnabledError() {
        if (lockExitApp) {
            lockExitApp = false;
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setMessage(R.string.deactivation_message)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        BancomatPayApi sdk = BancomatPayApi.getInstance();
                        sdk.deleteUserData();
                        //ActivationFlowManager.goToIntro(this);
                        lockExitApp = true;

                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbort(BancomatFullStackSdkInterfaceExtended.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED);
                        /*finishAffinity();*/
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
                        BancomatSdk sdk = BancomatSdk.getInstance();
                        sdk.deleteUserData();
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

            int idString = AppErrorMapper.getStringFromStatusCode(statusCodeInterface);
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

            int idString = AppErrorMapper.getStringFromStatusCode(statusCodeInterface);
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setCancelable(false)
                    .setMessage(idString)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        clickListener.onClick(dialog, which);
                        lockGenericError = true;
                    });
            builder.showDialog(this);
        }
    }

    public synchronized void showErrorFingerprintAndUsePin(StatusCodeInterface statusCodeInterface, DialogInterface.OnClickListener clickListener) {
        if (lockGenericError) {
            lockGenericError = false;

            int idString = AppErrorMapper.getStringFromStatusCode(statusCodeInterface);
            AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
            builder.setTitle(R.string.warning_title)
                    .setCancelable(false)
                    .setMessage(idString)
                    .setPositiveButton(R.string.ok, clickListener);
            builder.showDialog(this);

	        /*SnackbarUtil.showSnackbarActionCustom(this, getString(idString),
			        getString(R.string.ok), v -> {
				        clickListener.onClick(v);
				        lockGenericError = true;
			        }, null, null);*/

        }
    }

    public synchronized void showErrorRetryInit(View.OnClickListener clickListener) {
        if (lockGenericError) {
            if (BancomatPayApiInterface.Factory.getInstance().isDeviceOnline(this)) {
                //lockGenericError = false;

                AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
                builder.setTitle(R.string.warning_title)
                        .setCancelable(false)
                        .setMessage(getString(R.string.init_error_message));

                AlertDialog alertDialog = builder.create();
                alertDialog.setButton(
                        DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.retry),
                        (dialog, which) -> clickListener.onClick(alertDialog.getButton(which))
                );
                alertDialog.show();

                /*SnackbarUtil.showSnackbarActionCustom(this, getString(R.string.init_error_message),
                        getString(R.string.ok), clickListener, null, null);*/

            } else {
                //lockGenericError = false;
                int idString = AppErrorMapper.getStringFromStatusCode(NO_RETE);

                AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
                builder.setTitle(R.string.warning_title)
                        .setCancelable(false)
                        .setMessage(idString);

                AlertDialog alertDialog = builder.create();
                alertDialog.setButton(
                        DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.ok),
                        (dialog, which) -> clickListener.onClick(alertDialog.getButton(which))
                );
                alertDialog.show();

                /*SnackbarUtil.showSnackbarActionCustom(this, getString(idString),
                        getString(R.string.ok), clickListener, null, null);*/

            }
        }
    }

}
