package edu.famu.orderdemo;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnCard, btnPayment, btnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCard = findViewById(R.id.btnCard);
        btnPayment = findViewById(R.id.btnPayment);
        btnUser = findViewById(R.id.btnUser);

        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreateCard.class);
                startActivity(i);
            }
        });
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MakePayment.class);
                startActivity(i);
            }
        });
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreateUser.class);
                startActivity(i);
            }
        });
/*
        ParseQuery<Order> orderQuery = new ParseQuery<>(Order.class);
        orderQuery.include(Order.KEY_RESTAURANT);
        orderQuery.include(Order.KEY_USER);

        orderQuery.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orders, ParseException e) {
                if(e!= null){
                    Log.e(TAG, "Query Error");
                    e.printStackTrace();
                    return;
                }

                for(Order order : orders){
                    Log.d(TAG, "Total: " + Double.toString(order.getTotal()));
                    Log.d(TAG, "Restaurant Name: " + order.getRestaurant().getName());
                    Log.d(TAG, "User Name: " + order.getUser().getUsername());
                    Log.d(TAG, "Order Date: " + order.getOrderDate());
                    Log.d(TAG, "Delivery Address" + order.getDeliveryAddress().toString());
                }
            }
        });


 */
    }
}
