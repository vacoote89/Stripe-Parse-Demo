package edu.famu.orderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateCard extends AppCompatActivity {

    private static final String TAG = "CreateCard";

    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentMethodId;

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);
        saveCard();
    }

    private void saveCard() {

        final WeakReference<CreateCard> ref = new WeakReference<>(this);
        // Hook up the pay button to the card widget and stripe instance
        Button btnAddCard = findViewById(R.id.btnAddCard);
        btnAddCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            card = cardInputWidget.getCard();

            if(card != null) {

                //Add data to post to server
                FormBody body = new FormBody.Builder()
                    .add("name", "Jane Doe")
                    .add("customer", "cus_H3wu6tsKnIf9Ur")
                    .add("city", "Tallahassee")
                    .add("state", "FL")
                    .add("line1", "1234 N Monroe St")
                    .add("line2", "")
                    .add("postal_code", Objects.requireNonNull(card.getAddressZip()))
                    .add("number", Objects.requireNonNull(card.getNumber()))
                    .add("exp_month", Objects.requireNonNull(card.getExpMonth()).toString())
                    .add("exp_year", Objects.requireNonNull(card.getExpYear()).toString())
                    .add("cvc", Objects.requireNonNull(card.getCvc()))
                    .build();

                //Create the request to be sent over HTTP
                //TODO: Replace backend_app_id in string.xml
                //TODO: Replace backend_url in string.xml in production
                Request request = new Request.Builder()
                        .addHeader("applicationId",getString(R.string.backend_app_id))
                        .url(getString(R.string.backend_url) + "create_payment_method")
                        .post(body)
                        .build();
                httpClient.newCall(request).enqueue(new PostCallback(ref));
            }

            }
        });
    }


    private class PostCallback implements Callback {
        @NonNull
        private final WeakReference<CreateCard> activityRef;

        private PostCallback(@NonNull WeakReference<CreateCard> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void onFailure(@NotNull Call call, IOException e) {
            e.printStackTrace();
            Toast.makeText(activityRef.get(),"Create card has failed.",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(@NotNull Call call, Response response) throws IOException {

            final CreateCard activity = activityRef.get();
            final String responseStr = response.toString();
            if(activity == null){
                return;
            }

            if(!response.isSuccessful()){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Create card has failed." , Toast.LENGTH_LONG).show();
                        Log.d(TAG, responseStr);
                    }
                });
            }
            else {

                activity.onCreateCardSuccess(response);
            }
        }
    }

    private void onCreateCardSuccess(Response response) throws IOException {

        //Make response object into a Map
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        Map<String, Object> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(), type
        );

        //Get payment method id from response
       paymentMethodId = Objects.requireNonNull(responseMap.get("id")).toString();

        //Create PaymentMethods object
        //Class is name PaymentMethods to eliminate reference conflicts with class of the same name in the Stripe SDK
        PaymentMethods pay = new PaymentMethods();
        pay.setStripeId(paymentMethodId);
        pay.setBrand(card.getBrand().toString());
        pay.setExpMonth(card.getExpMonth());
        pay.setExpYear(card.getExpYear());
        pay.setLast4(card.getLast4());
        pay.setUser(ParseUser.getCurrentUser());

        pay.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            if(e == null) {
                Toast.makeText(getApplicationContext(), "New Card Added.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Card Failed to add to database.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            }
        });


    }
}
