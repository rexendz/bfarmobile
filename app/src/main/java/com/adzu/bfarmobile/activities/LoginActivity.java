package com.adzu.bfarmobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }

        mAuth = FirebaseAuth.getInstance();
        activityActive = true;

        if (ref == null)
            ref = FirebaseDatabase.getInstance().getReference();
        connection_status = findViewById(R.id.text_connection);
        connection_status.setVisibility(View.INVISIBLE);
        DatabaseUtil.startListeningForConnection();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission Denied. Access to SMS is required to be able to receive fishpond data offline from BFAR", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void login_action(View view) {
        loginSuccess = false;

        final EditText field_user = findViewById(R.id.field_user);
        final EditText field_pass = findViewById(R.id.field_pass);
        final String username = field_user.getText().toString();
        final String password = field_pass.getText().toString();

        if (username.isEmpty()) {
            field_user.setError("Please Enter Username");
            field_user.requestFocus();
        } else if (password.isEmpty()) {
            field_pass.setError("Please Enter Password");
            field_pass.requestFocus();
        } else {
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("Test", "Signin success");
                    } else {
                        Log.d("test", "onComplete: log unsuccessful");
                    }
                }
            });
            button_login.setEnabled(false);
            button_signup.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            DatabaseUtil.readDataByUsername(username, ref.child("account"), new OnGetDataListener() {
                @Override
                public void dataExists(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
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

                            if (connectTimer != null) {
                                connectTimer.cancel();
                                connectTimer = null;
                                isConnected = -1;
                            }
                            if (timer2 != null) {
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
                    if (!DatabaseUtil.isConnected()) {
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

        connectTimer.scheduleAtFixedRate(timerTask, 3000, 100);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (DatabaseUtil.isConnected() && isConnected == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Connection Established", Toast.LENGTH_LONG).show();
                            connection_status.setVisibility(View.INVISIBLE);
                            isConnected = 1;
                        }
                    });
                } else if (!DatabaseUtil.isConnected() && isConnected == 1) {
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
        };

    }


    @Override
    protected void onPause() {
        if (connectTimer != null) {
            connectTimer.cancel();
            connectTimer = null;
        }
        super.onPause();
    }

    public void signup_action(View view) {
        final Intent myIntent = new Intent(this, SignupActivity.class);

        if (DatabaseUtil.isConnected()) {
            startActivity(myIntent);
        } else {
            Toast.makeText(getApplicationContext(), "App is in offline mode. Connect to the internet to access this feature", Toast.LENGTH_LONG).show();
        }
    }

}