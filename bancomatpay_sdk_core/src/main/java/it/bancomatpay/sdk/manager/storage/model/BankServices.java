package it.bancomatpay.sdk.manager.storage.model;

import java.util.List;

public class BankServices {

    private List<EBankService> bankServiceList;

    public List<EBankService> getBankServiceList() {
        return bankServiceList;
    }

    public void setBankServiceList(List<EBankService> bankServiceList) {
        this.bankServiceList = bankServiceList;
    }

    public enum EBankService {

        P2P("P2P"),
        P2B("P2B"),
        PAYMENT_REQUEST("PAYMENT_REQUEST"),
        LOYALTY_CARD("LOYALTY_CARD"),
        LOYALTY("LOYALTY"),
        DOCUMENT("DOCUMENT"),
        BANKID("BANKID"),
        ATM("ATM"),
        POS("POS"),
        BPLAY("BPLAY"),
        BASE("BASE"),
        DIRECT_DEBITS("DIRECT_DEBITS"),
        SPLIT_BILL("SPLIT_BILL");

        String serviceName;

        EBankService(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceName() {
            return serviceName;
        }

        public boolean isServiceCard() {
            return this == EBankService.ATM ||
                    this == EBankService.LOYALTY_CARD ||
                    this == EBankService.LOYALTY ||
                    this == EBankService.DOCUMENT ||
                    this == EBankService.BANKID ||
                    this == EBankService.SPLIT_BILL ||
                    this == EBankService.DIRECT_DEBITS;
        }
    }

    public enum EServiceDetail {
        P2P_PAYMENT,
        P2B_PAYMENT,
        LOYALTY_CARD,
        DOCUMENT,
        BANKID,
        ATM,
        BPLAY,
        OTHER
    }

}
