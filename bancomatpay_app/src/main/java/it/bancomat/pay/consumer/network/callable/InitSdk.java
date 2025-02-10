package it.bancomat.pay.consumer.network.callable;

import android.app.Activity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.AppFlagModel;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.FlagModel;

public class InitSdk extends CallableVoid {

    public InitSdk(Activity activity, String abiCode, String groupCode, String phoneNumber, String phonePrefix, String iban) {
        this.abiCode = abiCode;
        this.groupCode = groupCode;
        this.phoneNumber = phoneNumber;
        this.phonePrefix = phonePrefix;
        this.iban = iban;
        this.activity = activity;
    }

    String abiCode;
    String groupCode;
    String phoneNumber;
    String phonePrefix;
    String iban;
    Activity activity;


    @Override
    public void execute() throws Exception {

        Result<Void> result = getResult();

        if (result != null) {
            if (result.isSessionExpired()) {
                new RefreshToken().execute();
                result = getResult();
            }
            if (!result.isSuccess()) {
                throw new ServerException(result);
            }
        }else {
            throw new NullPointerException("result is null");
        }
    }

    private Result<Void> getResult() throws InterruptedException {
        AtomicReference<Result<Void>> resultAtomicReference = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                .initBancomatSDKWithCheckRoot(activity,
                        abiCode, groupCode, phoneNumber,
                        phonePrefix, iban,
                        AppBancomatDataManager.getInstance().getTokens().getOauth(), result -> {
                            resultAtomicReference.set(result);
                            latch.countDown();
                        });

        latch.await();
        AppFlagModel appFlagModel = AppBancomatDataManager.getInstance().getFlagModel();
        appFlagModel.setShowBPlayPopup(true);
        AppBancomatDataManager.getInstance().putFlagmodel(appFlagModel);

        return resultAtomicReference.get();
    }
}
