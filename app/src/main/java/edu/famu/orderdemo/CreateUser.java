package edu.famu.orderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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

public class CreateUser extends AppCompatActivity {

    //Interface Objects
    private EditText name, email, phone, line1, line2, city, state, postal;
    private SwitchMaterial addCard;
    private CardInputWidget cardInputWidget;
    private Button btnAddCustomer;

    private static final String TAG ="CreateUser";
    private boolean isChecked = true; //Flag to check if credit card option is set
    private Card card; //Card details objects

    private OkHttpClient httpClient = new OkHttpClient(); //HTTP client to make call to the backend server
    private String paymentMethodId, gotItId;
    ParseUser newUser = new ParseUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        final WeakReference<CreateUser> ref = new WeakReference<>(this); //used to display toast later

        btnAddCustomer = findViewById(R.id.btnAddCustomer);
        addCard = findViewById(R.id.addCard);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        phone = findViewById(R.id.etPhone);
        line1 = findViewById(R.id.etLine1);
        line2 = findViewById(R.id.etLine2);
        city = findViewById(R.id.etCity);
        state = findViewById(R.id.etState);
        postal = findViewById(R.id.etPostal);



        addCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    cardInputWidget.setVisibility(View.VISIBLE);
                    isChecked = true;
                }
                else {
                    cardInputWidget.setVisibility(View.INVISIBLE);
                    isChecked = false;
                }
            }
        });

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Add new user data to object
                newUser.setEmail(email.getText().toString());
                newUser.setUsername(email.getText().toString());
                newUser.setPassword("password");

                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        gotItId = ParseUser.getCurrentUser().getObjectId(); //get the latest user id

                        String url; //url ending
                        FormBody body;

                        //metadata JSON object used to send additional data to Stripe
                        JSONObject metadata = new JSONObject();
                        try {
                            metadata.put("gotit_id", gotItId);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        //test if creating user with or without a card
                        if(isChecked && card != null) {
                            card = cardInputWidget.getCard(); //get a Card object from the widget used to enter card details

                            //add the variables need to send it to the server to the body
                            body = new FormBody.Builder()
                                    .add("name", name.getText().toString())
                                    .add("metadata", metadata.toString())
                                    .add("email", email.getText().toString())
                                    .add("phone", phone.getText().toString())
                                    .add("line1", line1.getText().toString())
                                    .add("line2", line2.getText().toString())
                                    .add("city", city.getText().toString())
                                    .add("state", state.getText().toString())
                                    .add("postal_code", postal.getText().toString())
                                    .add("number", Objects.requireNonNull(card.getNumber()))
                                    .add("exp_month", Objects.requireNonNull(card.getExpMonth()).toString())
                                    .add("exp_year", Objects.requireNonNull(card.getExpYear()).toString())
                                    .add("cvc", Objects.requireNonNull(card.getCvc()))
                                    .build();
                            url="create_customer_w_payment";
                        }
                        else {
                              body = new FormBody.Builder()
                                    .add("name", name.getText().toString())
                                    .add("metadata", metadata.toString())
                                    .add("email", email.getText().toString())
                                    .add("phone", phone.getText().toString())
                                    .add("line1", line1.getText().toString())
                                    .add("line2", line2.getText().toString())
                                    .add("city", city.getText().toString())
                                    .add("state", state.getText().toString())
                                    .add("postal_code", postal.getText().toString())
                                    .build();
                            url = "create_customer";
                        }

                        //Create the request to be sent over HTTP
                        //TODO: Replace backend_app_id in string.xml
                        //TODO: Replace backend_url in string.xml in production
                        Request request = new Request.Builder()
                                .addHeader("applicationId",getString(R.string.backend_app_id))
                                .url(getString(R.string.backend_url) + url)
                                .post(body)
                                .build();

                        //make actual call
                        //the request must be enqueued to run a different thread
                        httpClient.newCall(request).enqueue(new PostCallback(ref));

                    }
                });
            }
        });
    }

    //Method to process the server response
    private class PostCallback implements Callback {
        @NonNull
        private final WeakReference<CreateUser> activityRef;

        PostCallback(@NonNull WeakReference<CreateUser> activityRef) {

            this.activityRef = activityRef;
        }

        @Override
        public void onFailure(@NotNull Call call, IOException e) {
            e.printStackTrace();
            Toast.makeText(activityRef.get(), "Error while creating User", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(@NotNull Call call, Response response) throws IOException {
            final CreateUser activity = activityRef.get();
            final String responseStr = response.toString();
            if(activity == null){
                return;
            }

            if(!response.isSuccessful()){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Error while creating User", Toast.LENGTH_LONG).show();
                        Log.d(TAG, responseStr);
                    }
                });
            }
            else {

                //Calls the success option when a payment method is being created with user
                if(isChecked)
                    activity.onCreateCardSuccess(response);
                else {
                    Toast.makeText(activity, "User created.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        }
    }

    private void onCreateCardSuccess(Response response) throws IOException {

        //convert the reponse text to a Map Object
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        Map<String, Object> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(), type
        );


        paymentMethodId = Objects.requireNonNull(responseMap.get("id")).toString();

        //Create new payment method for Parse
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
