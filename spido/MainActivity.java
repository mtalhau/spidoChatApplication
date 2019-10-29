package com.example.spido;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Boolean loginModeActive = false;
    Button signupButton;
    EditText usernameEditText;
    EditText passwordEditText;
    TextView signupTextView;
    TextView signupDetailTextView;
    TextView signupLoginSwitchTextView;

    public void redirectToUsers () {
        if (ParseUser.getCurrentUser() != null) {
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();
            OneSignal.sendTag("User_ID",ParseUser.getCurrentUser().getUsername());
            Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
            startActivity(intent);
        }
    }


    public void toggleLoginMode(View view) {


        if (loginModeActive) {
            loginModeActive = false;
            signupTextView.setText("Sign Up");
            signupDetailTextView.setText("Please sign up with username and password to continue.");
            signupButton.setText("SIGN UP");
            signupLoginSwitchTextView.setText("Already have an account? Login");
        }
        else {
            loginModeActive = true;
            signupTextView.setText("Login Now");
            signupDetailTextView.setText("Please Login to continue.");
            signupButton.setText("LOG IN");
            signupLoginSwitchTextView.setText("Or, Sign Up");

        }
    }

    public void loginSignup (View view) {

        if (loginModeActive) {
            final ProgressDialog nDialog;
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Logging you in..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(false);
            nDialog.show();
            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        nDialog.dismiss();
                        redirectToUsers();
                    }
                    else {
                        nDialog.dismiss();
                        String message = e.getMessage();
                        if (message.contains("java")) {
                            message = e.getMessage().substring(e.getMessage().indexOf(" "));
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            final ProgressDialog n1Dialog;
            n1Dialog = new ProgressDialog(MainActivity.this);
            n1Dialog.setMessage("Loading..");
            n1Dialog.setTitle("Creating your account..");
            n1Dialog.setIndeterminate(false);
            n1Dialog.setCancelable(false);
            n1Dialog.show();
            ParseUser user = new ParseUser();
            user.setUsername(usernameEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        Toast.makeText(MainActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                        n1Dialog.dismiss();
                        //
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null && objects.size() > 0) {
                                    for (ParseUser user : objects) {
                                        ParseObject message = new ParseObject("Message");
                                        message.put("sender", ParseUser.getCurrentUser().getUsername());
                                        message.put("recipient", user.getUsername());
                                        message.put("message", "Hi there, I'm using Spido!");
                                        message.saveInBackground();
                                    }
                                }
                            }
                        });
                        //
                        redirectToUsers();
                    }
                    else {
                        n1Dialog.dismiss();
                        String message = e.getMessage();
                        if (message.contains("java")) {
                            message = e.getMessage().substring(e.getMessage().indexOf(" "));
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redirectToUsers();
        signupButton = findViewById(R.id.signupButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupTextView = findViewById(R.id.signupTextView);
        signupDetailTextView = findViewById(R.id.signupDetailTextView);
        signupLoginSwitchTextView = findViewById(R.id.signupLoginSwitchTextView);
    }
}
