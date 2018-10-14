package com.intervale.testbvg.https.model;

public class PaymentRequest {
    Src src;
    Dst dst;
    Params params;

    String paymentId="MoneyTransfer_Bel_PerevediMe";

    String currency="BYN";
    String commission="200";

    String amount="10";
    String total_amount="102";
    String returnUrl;

    public PaymentRequest(Src src, Dst dst, Params params, String commission, String amount, String total_amount, String returnUrl) {
        this.src = src;
        this.dst = dst;
        this.params = params;
        this.commission = commission;
        this.amount = amount;
        this.total_amount = total_amount;
        this.returnUrl = returnUrl;
    }

    public Src getSrc() {

        return src;
    }

    public void setSrc(Src src) {
        this.src = src;
    }

    public Dst getDst() {
        return dst;
    }

    public void setDst(Dst dst) {
        this.dst = dst;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
