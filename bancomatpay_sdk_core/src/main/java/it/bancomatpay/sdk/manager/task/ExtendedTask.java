package it.bancomatpay.sdk.manager.task;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.events.CheckRootWarningEvent;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.DexguardSecurityCheck;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.ExtendedException;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public abstract class ExtendedTask<E> extends Task {

    protected static final String TAG = ExtendedTask.class.getSimpleName();

    private static String JSESSION_CLIENT;

    public static synchronized String getJsessionClient() {
        if (TextUtils.isEmpty(JSESSION_CLIENT)) {
            JSESSION_CLIENT = UUID.randomUUID().toString();
        }
        return JSESSION_CLIENT;
    }

    protected ExtendedTask(OnCompleteResultListener<E> mListener) {
        super(mListener);
    }

    protected abstract void start();

    @Override
    public void execute() {

        Completable.fromAction(() -> {
            if (BancomatDataManager.getInstance().isAntirootCheckEnabled()) {
                DexguardSecurityCheck dexguardSecurityCheck = new DexguardSecurityCheck();
                dexguardSecurityCheck.checkDebug();
                dexguardSecurityCheck.checkEmulator();
                dexguardSecurityCheck.checkRoot();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        try {
                            start();
                        } catch (ExtendedException eex) {
                            sendError(new ExtendedError(eex, eex.getStatusCodeInterface()));
                        } catch (Exception ex) {
                            sendError(new ExtendedError(ex, StatusCode.Mobile.GENERIC_ERROR));
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (BancomatDataManager.getInstance().isBlockIfRooted()) {
                            sendError(new ExtendedError(e, StatusCode.Mobile.ROOTED));
                        } else {
                            EventBus.getDefault().post(new CheckRootWarningEvent());
                        }

                    }

                });

    }


    @Override
    public void sendError(Error error) {
        super.sendError(error);
    }

    public boolean managedError(Error error) {
        if (error instanceof HttpError) {
            HttpError httpError = (HttpError) error;
            return httpError.getCode() == 401;
        }
        return false;
    }

    public void sendErrorSessionExpired() {
        if (mListener != null) {
            Result<?> result = new Result<>();
            result.setStatusCode(StatusCode.Http.UNAUTHORIZED);
            result.setStatusCodeDetail("SDKAbortType_SESSION_EXPIRED");
            result.setStatusCodeMessage("Session expired, please refresh session token");
            mListener.onComplete(result);
        }
    }

    protected void prepareResult(Result<?> result, DtoAppResponse<?> response) {
        result.setStatusCode(Mapper.getStatusCodeInterface(response.getDtoStatus()));
        result.setStatusCodeDetail(Mapper.getExtraMessage(response.getDtoStatus()));
        result.setStatusCodeMessage(Mapper.getStatusCodeMessage(response.getDtoStatus()));
    }

}

