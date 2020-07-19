package com.adzu.bfarmobile.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private AnimationDrawable animDrawable;
    private ProgressBar progressBar;
    private Button button_login, button_signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        rootLayout = findViewById(R.id.root_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        progressBar = findViewById(R.id.progressBar);
        button_login = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signup);

    }

    public void login_action(View view){
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("account");
            DatabaseUtil.readDataByUsername(username, ref, new OnGetDataListener() {
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
                    String pass = (String) newPost.get("password");
                    boolean activated = (boolean) newPost.get("activated");
                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), pass);
                    if (result.verified) {
                        if (activated) {
                            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_LONG).show();

                            Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                            newIntent.putExtra("account_key", dataSnapshot.getKey());
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
                    Log.d("TESTER", "STARTED!");
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

    public void signup_action(View view){
        Intent myIntent = new Intent(this, SignupActivity.class);
        startActivity(myIntent);
    }
}