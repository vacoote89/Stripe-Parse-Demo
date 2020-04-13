package edu.famu.orderdemo;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Transaction")
public class Transaction extends ParseObject {

    public static final String KEY_INTENT_ID = "stripeIntentId";
    public static final String KEY_TRANSACTION_ID = "stripeTransactionId";
    public static final String KEY_PAYMENT = "paymentMethod";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ORDER = "order";

    public void setIntentId(String intentId) { put(KEY_INTENT_ID, intentId); }
    public String getIntentId() { return getString(KEY_INTENT_ID); }

    public void setTransactionId(String transactionId) { put(KEY_TRANSACTION_ID, transactionId); }
    public String getTransactionId() { return getString(KEY_TRANSACTION_ID); }

    public void setStatus(String status) { put(KEY_STATUS, status); }
    public String getStatus() { return getString(KEY_STATUS); }

    public void setOrder(Order order) { put(KEY_ORDER, order); }
    public Order getOrder() { return (Order)getParseObject(KEY_ORDER); }

    public void setPaymentMethod(PaymentMethods payment) { put(KEY_PAYMENT, payment); }
    public PaymentMethods getPaymentMethod() { return (PaymentMethods) getParseObject(KEY_PAYMENT); }

}
