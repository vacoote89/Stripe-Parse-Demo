package edu.famu.orderdemo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.stripe.android.PaymentConfiguration;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Order.class);
        ParseObject.registerSubclass(Restaurant.class);
        ParseObject.registerSubclass(PaymentMethods.class);
        ParseObject.registerSubclass(Transaction.class);
        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
     /*   OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
*/
        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("") // should correspond to APP_ID env variable
                .clientKey("")  // set explicitly unless clientKey is explicitly configured on Parse server
                // .clientBuilder(builder)
                .server("").build());

         /*
        Stripe API Setup
        TODO: Replace the string with your publishable key from Stripe Dashboard
        */
        PaymentConfiguration.init(
                getApplicationContext(),
                ""
        );
    }
}
