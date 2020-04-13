package edu.famu.orderdemo;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    public static final String KEY_NAME ="name";
    public static final  String KEY_ADDRESS = "address";

    public String getName() {return getString(KEY_NAME);}
    public void setName(String name) { put(KEY_NAME, name);}

    public String getAddress() {return getString(KEY_ADDRESS);}
    public void setAddress(String address) { put(KEY_ADDRESS, address);}

}
