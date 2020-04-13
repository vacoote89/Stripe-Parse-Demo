package edu.famu.orderdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MakePayment extends AppCompatActivity {

    private Button btnPay;
    private static final String TAG = "MakePayment";

    private OkHttpClient httpClient = new OkHttpClient();
    private String clientSecret;
    private Order newOrder = new Order();
    private PaymentMethods paymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);
        makePayment();

    }

    private void makePayment() {
        final WeakReference<MakePayment> ref = new WeakReference<>(this);
        btnPay = findViewById(R.id.btnPay);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //JSONObject for deliveryAddress on database
                JSONObject json = new JSONObject();

                try {
                    json.put("objectID", "f24h39kl");
                    json.put("streetAddress","123 N Cumberland Rd");
                    json.put("city","Tallahassee");
                    json.put("state","FL");
                    json.put("zipCode","32307");
                    json.put("alias","home");
                    json.put("geopoint",null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Retrieve a Restaurant object from the database
                ParseQuery<Restaurant> resQuery = new ParseQuery<>(Restaurant.class);
                Restaurant tempRestaurant = new Restaurant();
                try {
                    tempRestaurant = resQuery.get("ID1mvGip6t");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Retrieve a PaymentMethod object from the database
                ParseQuery<PaymentMethods> payQuery = new ParseQuery<>(PaymentMethods.class);
                try {
                    paymentMethod = payQuery.get("QvjwRBnZMW");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Generate random price
                //TODO: Remove when interface created
                Random rand = new Random();
                // Generate random integers in range 0 to 999 and 0 to 99
                int dollars = rand.nextInt(1000);
                int cents = rand.nextInt(100);
                double price = dollars + ((double)cents/100);

                //populate new order object
                newOrder.setTotal(price);
                newOrder.setUser(ParseUser.getCurrentUser());
                newOrder.setDeliveryAddress(json);
                newOrder.setStatus("New");

                if(tempRestaurant != null)
                    newOrder.setRestaurant(tempRestaurant);

                final JSONObject metadata = new JSONObject();

                newOrder.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        try {
                            metadata.put("order_id", newOrder.getObjectId());
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        //Populate data to post to server
                        FormBody body = new FormBody.Builder()
                                .add("amount", "320.44")
                                .add("customer", "cus_H3wu6tsKnIf9Ur")
                                .add("receipt_email", "vanessa.coote@gmail.com")
                                .add("metadata", metadata.toString())
                                .add("payment_method", paymentMethod.getStripeId())
                                .build();

                        //Create the request to be sent over HTTP
                        //TODO: Replace backend_app_id in string.xml
                        //TODO: Replace backend_url in string.xml in production
                        Request request = new Request.Builder()
                                .addHeader("applicationId",getString(R.string.backend_app_id))
                                .url(getString(R.string.backend_url) + "create_intent")
                                .post(body)
                                .build();

                        //send call
                        httpClient.newCall(request).enqueue(new MakePayment.PostCallback(ref));

                    }
                });
                
            }
        });

    }

    private class PostCallback implements Callback {
        @NonNull
        private final WeakReference<MakePayment> activityRef;
        PostCallback(@NonNull WeakReference<MakePayment> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void onFailure(@NotNull Call call, IOException e) {
            e.printStackTrace();
            Toast.makeText(activityRef.get(), "Payment failed.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(@NotNull Call call, Response response) throws IOException {
            final MakePayment activity = activityRef.get();
            final String responseStr = response.toString();
            if(activity == null){
                return;
            }

            if(!response.isSuccessful()){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG,responseStr);
                        Toast.makeText(activity, "Payment failed.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {

                activity.onMakePaymentSuccess(response);
            }
        }
    }

    private void onMakePaymentSuccess(Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        Map<String, Object> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(), type
        );

        //Get payment intent client secret
        clientSecret = Objects.requireNonNull(responseMap.get("client_secret")).toString();

        //create transaction record for database
        Transaction transaction = new Transaction();
        transaction.setIntentId(clientSecret);
        transaction.setOrder(newOrder);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setStatus("PENDING");

        transaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(getApplicationContext(), "Payment complete.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Payment failed to add to database.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
