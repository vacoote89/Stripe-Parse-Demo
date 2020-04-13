package edu.famu.orderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LogInActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
  //  private Button btnSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        etUsername = findViewById(R.id.etSUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogIn);
      //  btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });

      /*  btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

       */
    }

   /* private void goToSignUp() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    */

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null)
                {
                    Log.e(TAG, "Issue with login");
                    e.printStackTrace();
                    return;
                }

                goMainActivity();
            }
        });

    }



    private void goMainActivity() {
        Log.d(TAG, "Navigation to main activity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }
}
