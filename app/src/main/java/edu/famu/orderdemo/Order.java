package edu.famu.orderdemo;

import android.icu.text.SimpleDateFormat;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;

@ParseClassName("Order")
public class Order extends ParseObject {

    public static final String KEY_TOTAL = "total";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_USER = "user";
    public static final String KEY_DELIVERY="deliveryAddress";
    public static final String KEY_INTENT="intentId";
    public static final String KEY_STATUS="status";

    //Formatter for Date Object
    private String pattern = "MM/dd/yyyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

    public double getTotal() {return  getDouble(KEY_TOTAL);}
    public void setTotal(double total) { put(KEY_TOTAL, total);}

    public Restaurant getRestaurant() { return (Restaurant)getParseObject(KEY_RESTAURANT);}
    public void setRestaurant(Restaurant restaurant) { put(KEY_RESTAURANT, restaurant); }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public JSONObject getDeliveryAddress() {return getJSONObject(KEY_DELIVERY); }
    public void setDeliveryAddress(JSONObject address) { put(KEY_DELIVERY, address);}

    public String getIntentId() { return getString(KEY_INTENT); }
    public void setIntentId(String intentId) { put(KEY_INTENT, intentId); }

    public String getStaus() { return getString(KEY_STATUS); }
    public void setStatus(String status) { put(KEY_STATUS, status); }

    public String getOrderDate(){
        return dateFormat.format(getCreatedAt());
    }
}
