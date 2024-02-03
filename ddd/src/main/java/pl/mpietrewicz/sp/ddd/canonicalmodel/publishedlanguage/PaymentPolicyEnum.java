package pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage;

public enum PaymentPolicyEnum {

    CONTINUATION(0),
    RENEWAL_WITH_UNDERPAYMENT(3),
    RENEWAL_WITHOUT_UNDERPAYMENT(3),
    NO_RENEWAL(0);

    private final int graceMonths;

    PaymentPolicyEnum(int graceMonths) {
        this.graceMonths = graceMonths;
    }

    public int getGraceMonths() {
        return graceMonths;
    }

}