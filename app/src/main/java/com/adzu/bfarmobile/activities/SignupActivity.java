package com.adzu.bfarmobile.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;


public class SignupActivity extends AppCompatActivity {
    private ConstraintLayout signupLayout;
    private AnimationDrawable animDrawable;
    private DatabaseReference databaseReference;
    private Switch switch1;
    private boolean operator;
    private EditText fla;
    private IntlPhoneInput sim1, sim2;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        operator = false;
        fla = findViewById(R.id.field_fla);
        sim1 = findViewById(R.id.field_sim1);
        sim2 = findViewById(R.id.field_sim2);
        sim1.setEmptyDefault("PH");
        sim2.setEmptyDefault("PH");
        sim1.setEnabled(false);
        sim2.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("account");

        signupLayout = findViewById(R.id.signup_layout);
        animDrawable = (AnimationDrawable) signupLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                operator = b;

                fla.setEnabled(b);
                sim1.setEnabled(b);
                sim2.setEnabled(b);

                if (!b) {
                    fla.setText(null);
                    ((EditText) ((LinearLayout) sim1.getChildAt(0)).getChildAt(1)).setText("");
                    ((EditText) ((LinearLayout) sim2.getChildAt(0)).getChildAt(1)).setText("");
                }
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    public Context getContext() {
        return this;
    }

    public void signup_action(View view) {
        if (DatabaseUtil.isConnected()) {
            final Account user = new Account();
            final EditText field_firstname = findViewById(R.id.field_firstname);
            final EditText field_middlename = findViewById(R.id.field_middlename);
            final EditText field_lastname = findViewById(R.id.field_lastname);
            final EditText field_user = findViewById(R.id.field_user);
            final EditText field_pass = findViewById(R.id.field_pass);
            final EditText field_cpass = findViewById(R.id.field_cpass);
            user.setFirstname(field_firstname.getText().toString());
            user.setMiddlename(field_middlename.getText().toString());
            user.setLastname(field_lastname.getText().toString());
            user.setUsername(field_user.getText().toString());
            user.setPassword(field_pass.getText().toString());
            user.setOperator(operator);
            boolean operatorValid = false;

            if (switch1.isChecked()) {
                user.setFla_number(Long.parseLong(fla.getText().toString()));
                if (sim1.isValid() && sim2.isValid()) {
                    user.setSim1(sim1.getNumber());
                    user.setSim2(sim2.getNumber());
                    if (fla.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please fill in the operator details.", Toast.LENGTH_LONG).show();
                        fla.requestFocus();
                    }
                    operatorValid = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid phone number", Toast.LENGTH_LONG).show();
                }
            }

            if (field_firstname.getText().toString().isEmpty() || field_middlename.getText().toString().isEmpty() || field_lastname.getText().toString().isEmpty() || field_user.getText().toString().isEmpty() || field_pass.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill up the missing fields.", Toast.LENGTH_LONG).show();
            } else if (!field_pass.getText().toString().equals(field_cpass.getText().toString())) {
                field_cpass.setError("Password does not match!");
                field_cpass.requestFocus();
            } else {
                if (!switch1.isChecked() || (operatorValid && switch1.isChecked())) {
                    mProgress.show();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("account");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean usernameTaken = false;
                            boolean flaTaken = false;
                            boolean sim1Taken = false;
                            boolean sim2Taken = false;
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                if (snap.getValue(Account.class).getUsername().equals(user.getUsername())) {
                                    usernameTaken = true;
                                }
                                if (snap.getValue(Account.class).getFla_number() == user.getFla_number() && user.getFla_number() > 0) {
                                    flaTaken = true;
                                }
                                if (snap.getValue(Account.class).getSim1() == user.getSim1()) {
                                    sim1Taken = true;
                                }
                                if (snap.getValue(Account.class).getSim2() == user.getSim2()) {
                                    sim2Taken = true;
                                }

                            }
                            if (usernameTaken) {
                                field_user.setError("Username Already Taken");
                                field_user.requestFocus();
                                mProgress.dismiss();
                            } else if (flaTaken) {
                                fla.setError("FLA Number Already Taken");
                                fla.requestFocus();
                                mProgress.dismiss();
                            } else if (sim1Taken) {
                                Toast.makeText(getContext(), "Sim 1 number already taken", Toast.LENGTH_LONG).show();
                                sim1.requestFocus();
                                mProgress.dismiss();
                            } else if (sim2Taken) {
                                Toast.makeText(getContext(), "Sim 2 number already taken", Toast.LENGTH_LONG).show();
                                sim2.requestFocus();
                                mProgress.dismiss();
                            } else {
                                user.setPasswordHashed();
                                String id = databaseReference.push().getKey();
                                databaseReference.child(id).setValue(user);
                                user.setUsername(null);
                                user.setPassword(null);

                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setTitle("BFAR Registration");
                                alert.setMessage("Account Successfully Created!\nPlease contact the administrators for account activation.");
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                mProgress.dismiss();
                                alert.show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mProgress.dismiss();
                            Log.d("FIREBASE", String.valueOf(databaseError));
                        }
                    });
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "App is in offline mode. Connect to the internet to access this feature", Toast.LENGTH_LONG).show();
        }

    }

    public void cancel_action(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}