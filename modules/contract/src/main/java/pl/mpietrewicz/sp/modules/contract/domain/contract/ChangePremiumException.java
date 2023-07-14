package pl.mpietrewicz.sp.modules.contract.domain.contract;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@SuppressWarnings("serial")
public class ChangePremiumException extends RuntimeException {

    private AggregateId contractId;
    private ContractStatus contractStatus;

    public ChangePremiumException(String message) {
        super(message);
    }
}