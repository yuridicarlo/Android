package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.network.dto.request.DtoModifyDocumentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoModifyDocumentResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class ModifyDocumentTask extends ExtendedTask<BcmDocument> {

    private DtoDocument.DocumentTypeEnum documentType;
    private String documentUuid;
    private String documentNumber;
    private String surname;
    private String name;
    private String fiscalCode;
    private String issuingInstituion;
    private String issuingDate;
    private String expirationDate;
    private String note;
    private String documentName;

    public ModifyDocumentTask(OnCompleteResultListener<BcmDocument> mListener, String documentUuid, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstitution, String issuingDate, String expirationDate, String note, String documentName) {
        super(mListener);
        this.documentType = documentType;
        this.documentUuid = documentUuid;
        this.documentNumber = documentNumber;
        this.surname = surname;
        this.name = name;
        this.fiscalCode = fiscalCode;
        this.issuingInstituion = issuingInstitution;
        this.issuingDate = issuingDate;
        this.expirationDate = expirationDate;
        this.note = note;
        this.documentName = documentName;
    }

    @Override
    protected void start() {
        DtoModifyDocumentRequest req = new DtoModifyDocumentRequest();
        DtoDocument dtoDocument = new DtoDocument();
        dtoDocument.setDocumentUuid(documentUuid);
        dtoDocument.setDocumentType(documentType.toString());
        dtoDocument.setDocumentNumber(documentNumber);
        dtoDocument.setSurname(surname);
        dtoDocument.setName(name);
        dtoDocument.setFiscalCode(fiscalCode);
        dtoDocument.setIssuingInstitution(issuingInstituion);
        dtoDocument.setIssuingDate(issuingDate);
        dtoDocument.setExpirationDate(expirationDate);
        dtoDocument.setNote(note);
        dtoDocument.setDocumentName(documentName);

        req.setDtoDocument(dtoDocument);

        Single.fromCallable(new HandleRequestInteractor<>(DtoModifyDocumentResponse.class, req, Cmd.MODIFY_DOCUMENT, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoModifyDocumentResponse>> listener = new NetworkListener<DtoModifyDocumentResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoModifyDocumentResponse> response) {

            CustomLogger.d(TAG, response.toString());
            Result<BcmDocument> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                if (response.getRes() != null) {
                    BcmDocument document = Mapper.modifyDocumentData(response.getRes());
                    result.setResult(document);
                }
                sendCompletition(result);
            } else {
                sendCompletition(result);
            }
        }
    };

}
