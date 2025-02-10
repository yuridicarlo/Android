package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetDocumentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSetDocumentResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetDocumentTask extends ExtendedTask<String> {

    private String documentName;
    private DtoDocument.DocumentTypeEnum documentType;
    private String documentNumber;
    private String surname;
    private String name;
    private String fiscalCode;
    private String issuingInstituion;
    private String issuingDate;
    private String expirationDate;
    private String note;

    public SetDocumentTask(OnCompleteResultListener<String> mListener, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstituion, String issuingDate, String expirationDate, String note, String documentName) {
        super(mListener);
        this.documentName = documentName;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.surname = surname;
        this.name = name;
        this.fiscalCode = fiscalCode;
        this.issuingInstituion = issuingInstituion;
        this.issuingDate = issuingDate;
        this.expirationDate = expirationDate;
        this.note = note;
    }

    @Override
    protected void start() {
        DtoSetDocumentRequest req = new DtoSetDocumentRequest();
        req.setDocumentName(documentName);
        req.setDocumentType(documentType);
        req.setDocumentNumber(documentNumber);
        req.setSurname(surname);
        req.setName(name);
        req.setFiscalCode(fiscalCode);
        req.setIssuingInstitution(issuingInstituion);
        req.setIssuingDate(issuingDate);
        req.setExpirationDate(expirationDate);
        req.setNote(note);

        Single.fromCallable(new HandleRequestInteractor<>(DtoSetDocumentResponse.class, req, Cmd.SET_DOCUMENT, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSetDocumentResponse>> listener = new NetworkListener<DtoSetDocumentResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSetDocumentResponse> response) {

            CustomLogger.d(TAG, response.toString());
            Result<String> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                if (response.getRes() != null) {
                    String documentUuid = response.getRes().getDocumentUuid();
                    result.setResult(documentUuid);
                }
                sendCompletition(result);
            } else {
                sendCompletition(result);
            }
        }
    };

}
