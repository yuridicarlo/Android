package it.bancomat.pay.consumer.network.callable;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.manager.network.dto.DtoUserMonitoringRequest;
import it.bancomatpay.sdk.manager.task.ExtendedTask;

public class UserMonitoring extends CallableVoid {

    private String bankUUID;
    private String tag;
    private String event;
    private String note;

    public UserMonitoring(String bankUUID, String tag, String event, String note) {
        this.bankUUID = bankUUID;
        this.tag = tag;
        this.event = event;
        this.note = note;
    }

    @Override
    public void execute() throws Exception {
        DtoUserMonitoringRequest request = new DtoUserMonitoringRequest();
        request.setBankUUID(bankUUID);
        request.setTag(tag);
        request.setEvent(event);
        request.setNote(note);
        new AppHandleRequestInteractor<>(Void.class, request, AppCmd.USER_MONITORING, ExtendedTask.getJsessionClient()).call();
    }


}

