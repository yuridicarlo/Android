package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DtoGetDirectDebitsHistoryResponse implements Serializable {

    private List<DtoDirectDebitHistoryElement> dtoDirectDebitHistoryElements;

    public void setDtoGetDirectDebitsHistoryElementList(List<DtoDirectDebitHistoryElement> dtoGetDirectDebitsHistoryElementList) {
        this.dtoDirectDebitHistoryElements = dtoGetDirectDebitsHistoryElementList;
    }

    public List<DtoDirectDebitHistoryElement> getDtoGetDirectDebitsHistoryElementList() {
        if (dtoDirectDebitHistoryElements == null) {
            dtoDirectDebitHistoryElements = new ArrayList<>();
        }
        return this.dtoDirectDebitHistoryElements;
    }

}
