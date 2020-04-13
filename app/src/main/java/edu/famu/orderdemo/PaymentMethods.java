package edu.famu.orderdemo;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("PaymentMethod")
public class PaymentMethods extends ParseObject {
    public static final String KEY_STRIPE = "stripeId";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_LAST4 = "lastFour";
    public static final String KEY_EXP_MONTH = "expireMonth";
    public static final String KEY_EXP_YEAR = "expireYear";
    public static final String KEY_USER = "user";

    public void setStripeId(String stripeId){ put(KEY_STRIPE, stripeId); }
    public String getStripeId(){ return getString(KEY_STRIPE); }

    public void setBrand(String brand) { put(KEY_BRAND, brand); }
    public String getBrand() { return getString(KEY_BRAND); }

    public void setLast4(String last4) { put(KEY_LAST4, last4); }
    public String getLast4() { return getString(KEY_LAST4); }

    public void setExpMonth(int month) { put(KEY_EXP_MONTH, month); }
    public int getExpMonth() { return getInt(KEY_EXP_MONTH); }

    public void setExpYear(int year) { put(KEY_EXP_YEAR, year); }
    public int getExpYear() { return getInt(KEY_EXP_YEAR); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }
}
