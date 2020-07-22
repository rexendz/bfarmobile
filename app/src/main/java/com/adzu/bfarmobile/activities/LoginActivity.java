package com.adzu.bfarmobile.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.ConnectivityListener;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private AnimationDrawable animDrawable;
    private ProgressBar progressBar;
    private Button button_login, button_signup;
    private DatabaseReference ref;
    private boolean loginSuccess;
    private TextView connection_status;
    private int isConnected;
    private Timer connectTimer;
    private TimerTask timerTask;
    private CountDownTimer timer2;
    private boolean activityActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityActive = true;

        if(ref == null)
            ref = FirebaseDatabase.getInstance().getReference();
        connection_status = findViewById(R.id.text_connection);
        connection_status.setVisibility(View.INVISIBLE);

        isConnected = isNetworkAvailable() ? 1 : 0;
        if(isConnected == 0) {
            connection_status.setText("OFFLINE MODE");
            Toast.makeText(getApplicationContext(), "Offline mode", Toast.LENGTH_LONG).show();
            connection_status.setVisibility(View.VISIBLE);
        }

        rootLayout = findViewById(R.id.root_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        progressBar = findViewById(R.id.progressBar);
        button_login = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signup);

    }
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

    public void login_action(View view){
        loginSuccess = false;

        final EditText field_user = findViewById(R.id.field_user);
        final EditText field_pass = findViewById(R.id.field_pass);
        final String username = field_user.getText().toString();
        final String password = field_pass.getText().toString();

        if(username.isEmpty()){
            field_user.setError("Please Enter Username");
            field_user.requestFocus();
        }
        else if(password.isEmpty()){
            field_pass.setError("Please Enter Password");
            field_pass.requestFocus();
        }
        else{
            button_login.setEnabled(false);
            button_signup.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            DatabaseUtil.readDataByUsername(username, ref.child("account"), new OnGetDataListener() {
                @Override
                public void dataExists(DataSnapshot dataSnapshot){
                    if(!dataSnapshot.exists()){
                        Toast.makeText(getApplicationContext(), "Username Does Not Exist!", Toast.LENGTH_LONG).show();
                        button_login.setEnabled(true);
                        button_signup.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        field_user.requestFocus();
                        field_user.setError(null);
                    }
                }

                @Override
                public void dataRetrieved(DataSnapshot dataSnapshot) {
                    Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();
                    Account account = new Account(newPost);
                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), account.getPassword());
                    if (result.verified) {
                        if (account.isActivated()) {
                            loginSuccess = true;
                            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_LONG).show();

                            if(connectTimer != null) {
                                connectTimer.cancel();
                                connectTimer = null;
                                isConnected = -1;
                            }
                            if(timer2 != null){
                                timer2.cancel();
                                timer2 = null;
                            }

                            Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("account_username", account.getUsername());
                            extras.putLong("account_fla", account.getFla_number());
                            extras.putString("account_firstname", account.getFirstname());
                            extras.putString("account_middlename", account.getMiddlename());
                            extras.putString("account_lastname", account.getLastname());
                            extras.putBoolean("account_operator", account.isOperator());
                            extras.putString("account_sim1", account.getSim1());
                            extras.putString("account_sim2", account.getSim2());
                            extras.putBoolean("account_admin", account.isAdmin());
                            newIntent.putExtras(extras);
                            startActivity(newIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Account Not Activated!", Toast.LENGTH_LONG).show();
                        }
                        button_login.setEnabled(true);
                        button_signup.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Password!", Toast.LENGTH_LONG).show();
                        button_login.setEnabled(true);
                        button_signup.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        field_pass.requestFocus();
                        field_pass.setError(null);
                    }
                }

                @Override
                public void onStart() {

                    DatabaseUtil.checkConnection(new ConnectivityListener() {
                        @Override
                        public void onConnected() {
                        }

                        @Override
                        public void onDisconnected() {
                            if (activityActive) {
                                Log.d("TEST", "OFFLINE MODE");
                                timer2 = new CountDownTimer(5000, 100) {

                                    @Override
                                    public void onTick(long l) {
                                        if (loginSuccess) {
                                            Log.d("Test", "login success, cancelling timer");
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        if (!loginSuccess) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Failed to retrieve data! You must first login the app with an internet connection at least once to have offline capabilities.", Toast.LENGTH_LONG).show();
                                                    button_login.setEnabled(true);
                                                    button_signup.setEnabled(true);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                        }
                                    }
                                }.start();
                            }
                        }

                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onStart() {

                        }
                    });
                    }

                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                    button_login.setEnabled(true);
                    button_signup.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    public void startTimer() {
        connectTimer = new Timer();

        initializeTimerTask();

        connectTimer.scheduleAtFixedRate(timerTask, 2000, 1000);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                DatabaseUtil.checkConnection(new ConnectivityListener() {
                    @Override
                    public void onConnected() {
                        if (isConnected == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Connection Established", Toast.LENGTH_LONG).show();
                                    connection_status.setVisibility(View.INVISIBLE);
                                    isConnected = 1;
                                }
                            });
                        }
                    }

                    @Override
                    public void onDisconnected() {
                        if (isConnected == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connection_status.setText("OFFLINE MODE");
                                    Toast.makeText(getApplicationContext(), "Connection Lost! Offline mode activated", Toast.LENGTH_LONG).show();
                                    connection_status.setVisibility(View.VISIBLE);
                                    isConnected = 0;
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onStart() {

                    }
                });
            }
        };

    }


    @Override
    protected void onPause() {
        if(connectTimer != null) {
            connectTimer.cancel();
            connectTimer = null;
        }
        super.onPause();
    }

    public void signup_action(View view){
        final Intent myIntent = new Intent(this, SignupActivity.class);
        DatabaseUtil.checkConnection(new ConnectivityListener() {
            @Override
            public void onConnected() {
                startActivity(myIntent);
            }

            @Override
            public void onDisconnected() {
                Toast.makeText(getApplicationContext(), "App is in offline mode. Connect to the internet to access this feature", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onStart() {

            }
        });
    }

}