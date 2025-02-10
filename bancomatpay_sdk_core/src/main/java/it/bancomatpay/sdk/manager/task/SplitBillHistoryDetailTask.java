package it.bancomatpay.sdk.manager.task;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSplitBillDetailRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSplitBillHistoryDetailResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;

public class SplitBillHistoryDetailTask extends ExtendedTask<List<SplitBeneficiary>> {

    String splitBillUUID;

    public SplitBillHistoryDetailTask(OnCompleteResultListener<List<SplitBeneficiary>> mListener, String splitBillUUID) {
        super(mListener);
        this.splitBillUUID = splitBillUUID;
    }

    @Override
    protected void start() {
        DtoSplitBillDetailRequest req = new DtoSplitBillDetailRequest();
        req.setSplitBillUUID(splitBillUUID);

        Single.fromCallable(new HandleRequestInteractor<>(DtoSplitBillHistoryDetailResponse.class, req, Cmd.GET_SPLIT_BILL_HISTORY_DETAIL, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSplitBillHistoryDetailResponse>> listener = new NetworkListener<DtoSplitBillHistoryDetailResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSplitBillHistoryDetailResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<List<SplitBeneficiary>> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                r.setResult(Mapper.getSplitBillDetail(response.getRes()));

                HashMap<String, ContactItem> contactItemHashMap = ApplicationModel.getInstance().getContactItemHashMap();
                if (contactItemHashMap != null) {
                    enrich(r, contactItemHashMap);
                }
            }

            sendCompletition(r);
        }

    };

    private void enrich(Result<List<SplitBeneficiary>> result, HashMap<String, ContactItem> contactItemHashMap) {
        for (SplitBeneficiary beneficiary : result.getResult()) {
            String key = PhoneNumber.getE164Number(beneficiary.getBeneficiary().getMsisdn());
            if (contactItemHashMap.containsKey(key)) {
                ContactItem contactItem = contactItemHashMap.get(key);
                if (contactItem != null) {
                    beneficiary.setBeneficiary(contactItem);
                }
            }
        }
    }

}
