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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondBoxes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.rimoto.intlphoneinput.IntlPhoneInput;


public class SignupActivity extends AppCompatActivity {
    private final int MAX_BOX_COUNT = 10;
    private final int MIN_BOX_COUNT = 1;

    private ConstraintLayout signupLayout, signupLayout1;
    private AnimationDrawable animDrawable;
    private DatabaseReference databaseReference;
    private Switch switch1;
    private boolean operator;
    private EditText fla, box;
    private ExpandableLayout expandableLayout;
    private ImageButton buttonAdd, buttonSub;
    private IntlPhoneInput sim1, sim2, sim3, sim4, sim5, sim6, sim7, sim8, sim9, sim10, sim11;
    private int boxCount = MIN_BOX_COUNT;
    private TextView simt3, simt4, simt5, simt6, simt7, simt8, simt9, simt10, simt11;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        operator = false;
        fla = findViewById(R.id.field_fla);
        box = findViewById(R.id.field_box);
        buttonAdd = findViewById(R.id.button_add);
        buttonSub = findViewById(R.id.button_sub);
        sim1 = findViewById(R.id.field_sim1);
        sim2 = findViewById(R.id.field_sim2);
        sim3 = findViewById(R.id.field_sim3);
        sim4 = findViewById(R.id.field_sim4);
        sim5 = findViewById(R.id.field_sim5);
        sim6 = findViewById(R.id.field_sim6);
        sim7 = findViewById(R.id.field_sim7);
        sim8 = findViewById(R.id.field_sim8);
        sim9 = findViewById(R.id.field_sim9);
        sim10 = findViewById(R.id.field_sim10);
        sim11= findViewById(R.id.field_sim11);
        simt3 = findViewById(R.id.textView100);
        simt4 = findViewById(R.id.textView101);
        simt5 = findViewById(R.id.textView102);
        simt6 = findViewById(R.id.textView103);
        simt7 = findViewById(R.id.textView104);
        simt8 = findViewById(R.id.textView105);
        simt9 = findViewById(R.id.textView106);
        simt10 = findViewById(R.id.textView107);
        simt11 = findViewById(R.id.textView108);
        expandableLayout = findViewById(R.id.signup_expand);
        signupLayout1 = findViewById(R.id.layout_signup1);
        sim1.setEmptyDefault("PH");
        sim2.setEmptyDefault("PH");
        sim3.setEmptyDefault("PH");
        sim4.setEmptyDefault("PH");
        sim5.setEmptyDefault("PH");
        sim6.setEmptyDefault("PH");
        sim7.setEmptyDefault("PH");
        sim8.setEmptyDefault("PH");
        sim9.setEmptyDefault("PH");
        sim10.setEmptyDefault("PH");
        sim11.setEmptyDefault("PH");
        sim1.setEnabled(false);
        sim2.setEnabled(false);
        sim3.setEnabled(false);
        sim4.setEnabled(false);
        sim5.setEnabled(false);
        sim6.setEnabled(false);
        sim7.setEnabled(false);
        sim8.setEnabled(false);
        sim9.setEnabled(false);
        sim10.setEnabled(false);
        sim11.setEnabled(false);

        signupLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else
                    expandableLayout.expand();
            }
        });

        box.setText(Integer.toString(MIN_BOX_COUNT));

        databaseReference = FirebaseDatabase.getInstance().getReference("account");

        signupLayout = findViewById(R.id.signup_layout);
        animDrawable = (AnimationDrawable) signupLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if(Integer.parseInt(box.getText().toString()) > MAX_BOX_COUNT) {
                        boxCount = MAX_BOX_COUNT;
                        box.setText(Integer.toString(MAX_BOX_COUNT));
                    } else if(Integer.parseInt(box.getText().toString()) < MIN_BOX_COUNT) {
                        boxCount = MIN_BOX_COUNT;
                        box.setText(Integer.toString(MIN_BOX_COUNT));
                    } else {
                        boxCount = Integer.parseInt(box.getText().toString());
                    }
                    filterBoxSims();

                }
            }
        });

        switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                operator = b;

                fla.setEnabled(b);
                sim1.setEnabled(b);
                sim2.setEnabled(b);
                sim3.setEnabled(b);
                sim4.setEnabled(b);
                sim5.setEnabled(b);
                sim6.setEnabled(b);
                sim7.setEnabled(b);
                sim8.setEnabled(b);
                sim9.setEnabled(b);
                sim10.setEnabled(b);
                sim11.setEnabled(b);
                box.setEnabled(b);
                buttonAdd.setClickable(b);
                buttonSub.setClickable(b);

                if (!b) {
                    fla.setText(null);
                    ((EditText) ((LinearLayout) sim1.getChildAt(0)).getChildAt(1)).setText("");
                    ((EditText) ((LinearLayout) sim2.getChildAt(0)).getChildAt(1)).setText("");
                }
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(operator) {
                    if (boxCount < MAX_BOX_COUNT)
                        boxCount++;
                    box.setText(Integer.toString(boxCount));
                    filterBoxSims();
                }
            }
        });

        buttonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(operator) {
                    if (boxCount > MIN_BOX_COUNT)
                        boxCount--;
                    box.setText(Integer.toString(boxCount));
                    filterBoxSims();
                }
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    public void filterBoxSims() {
        sim3.setVisibility(View.GONE);
        sim4.setVisibility(View.GONE);
        sim5.setVisibility(View.GONE);
        sim6.setVisibility(View.GONE);
        sim7.setVisibility(View.GONE);
        sim8.setVisibility(View.GONE);
        sim9.setVisibility(View.GONE);
        sim10.setVisibility(View.GONE);
        sim11.setVisibility(View.GONE);
        simt3.setVisibility(View.GONE);
        simt4.setVisibility(View.GONE);
        simt5.setVisibility(View.GONE);
        simt6.setVisibility(View.GONE);
        simt7.setVisibility(View.GONE);
        simt8.setVisibility(View.GONE);
        simt9.setVisibility(View.GONE);
        simt10.setVisibility(View.GONE);
        simt11.setVisibility(View.GONE);
        switch(boxCount) {
            case 2:
                sim3.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                break;
            case 3:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                break;
            case 4:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                break;
            case 5:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                sim6.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                simt6.setVisibility(View.VISIBLE);
                break;
            case 6:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                sim6.setVisibility(View.VISIBLE);
                sim7.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                simt6.setVisibility(View.VISIBLE);
                simt7.setVisibility(View.VISIBLE);
                break;
            case 7:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                sim6.setVisibility(View.VISIBLE);
                sim7.setVisibility(View.VISIBLE);
                sim8.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                simt6.setVisibility(View.VISIBLE);
                simt7.setVisibility(View.VISIBLE);
                simt8.setVisibility(View.VISIBLE);
                break;
            case 8:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                sim6.setVisibility(View.VISIBLE);
                sim7.setVisibility(View.VISIBLE);
                sim8.setVisibility(View.VISIBLE);
                sim9.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                simt6.setVisibility(View.VISIBLE);
                simt7.setVisibility(View.VISIBLE);
                simt8.setVisibility(View.VISIBLE);
                simt9.setVisibility(View.VISIBLE);
                break;
            case 9:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                sim6.setVisibility(View.VISIBLE);
                sim7.setVisibility(View.VISIBLE);
                sim8.setVisibility(View.VISIBLE);
                sim9.setVisibility(View.VISIBLE);
                sim10.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                simt6.setVisibility(View.VISIBLE);
                simt7.setVisibility(View.VISIBLE);
                simt8.setVisibility(View.VISIBLE);
                simt9.setVisibility(View.VISIBLE);
                simt10.setVisibility(View.VISIBLE);
                break;
            case 10:
                sim3.setVisibility(View.VISIBLE);
                sim4.setVisibility(View.VISIBLE);
                sim5.setVisibility(View.VISIBLE);
                sim6.setVisibility(View.VISIBLE);
                sim7.setVisibility(View.VISIBLE);
                sim8.setVisibility(View.VISIBLE);
                sim9.setVisibility(View.VISIBLE);
                sim10.setVisibility(View.VISIBLE);
                sim11.setVisibility(View.VISIBLE);
                simt3.setVisibility(View.VISIBLE);
                simt4.setVisibility(View.VISIBLE);
                simt5.setVisibility(View.VISIBLE);
                simt6.setVisibility(View.VISIBLE);
                simt7.setVisibility(View.VISIBLE);
                simt8.setVisibility(View.VISIBLE);
                simt9.setVisibility(View.VISIBLE);
                simt10.setVisibility(View.VISIBLE);
                simt11.setVisibility(View.VISIBLE);
                break;
            default:

        }
    }

    public Context getContext() {
        return this;
    }

    public void signup_action(View view) {
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
        if(operator)
            user.setBoxes(boxCount);
        else
            user.setBoxes(0);
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
        } else if (field_pass.getText().toString().length() < 5) {
            field_pass.setError("Use 5 characters or more for your password");
            field_pass.requestFocus();
        } else {
            if (!switch1.isChecked() || (operatorValid && switch1.isChecked())) {
                mProgress.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("account");
                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("fishpond_box");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean usernameTaken = false;
                        boolean flaTaken = false;
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            if (snap.getValue(Account.class).getUsername() != null && snap.getValue(Account.class).getUsername().equals(user.getUsername())) {
                                usernameTaken = true;
                            }
                            if (snap.getValue(Account.class).getFla_number() == user.getFla_number() && user.getFla_number() > 0) {
                                flaTaken = true;
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
                        } else {
                            boolean boxesPushed = false;
                            if(operator) {
                                boolean validNumbers = false;
                                FishpondBoxes fishpondBoxes = new FishpondBoxes();
                                switch(boxCount) {
                                    case 1:
                                        if(sim2.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 2:
                                        if(sim2.isValid() && sim3.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 3:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 4:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 5:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid() && sim6.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            fishpondBoxes.setBox5_sim(sim6.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 6:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid() && sim6.isValid() && sim7.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            fishpondBoxes.setBox5_sim(sim6.getNumber());
                                            fishpondBoxes.setBox6_sim(sim7.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 7:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid() && sim6.isValid() && sim7.isValid() && sim8.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            fishpondBoxes.setBox5_sim(sim6.getNumber());
                                            fishpondBoxes.setBox6_sim(sim7.getNumber());
                                            fishpondBoxes.setBox7_sim(sim8.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 8:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid() && sim6.isValid() && sim7.isValid() && sim8.isValid() && sim9.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            fishpondBoxes.setBox5_sim(sim6.getNumber());
                                            fishpondBoxes.setBox6_sim(sim7.getNumber());
                                            fishpondBoxes.setBox7_sim(sim8.getNumber());
                                            fishpondBoxes.setBox8_sim(sim9.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 9:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid() && sim6.isValid() && sim7.isValid() && sim8.isValid() && sim9.isValid() && sim10.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            fishpondBoxes.setBox5_sim(sim6.getNumber());
                                            fishpondBoxes.setBox6_sim(sim7.getNumber());
                                            fishpondBoxes.setBox7_sim(sim8.getNumber());
                                            fishpondBoxes.setBox8_sim(sim9.getNumber());
                                            fishpondBoxes.setBox9_sim(sim10.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                    case 10:
                                        if(sim2.isValid() && sim3.isValid() && sim4.isValid() && sim5.isValid() && sim6.isValid() && sim7.isValid() && sim8.isValid() && sim9.isValid() && sim10.isValid() && sim11.isValid()) {
                                            fishpondBoxes.setBox1_sim(sim2.getNumber());
                                            fishpondBoxes.setBox2_sim(sim3.getNumber());
                                            fishpondBoxes.setBox3_sim(sim4.getNumber());
                                            fishpondBoxes.setBox4_sim(sim5.getNumber());
                                            fishpondBoxes.setBox5_sim(sim6.getNumber());
                                            fishpondBoxes.setBox6_sim(sim7.getNumber());
                                            fishpondBoxes.setBox7_sim(sim8.getNumber());
                                            fishpondBoxes.setBox8_sim(sim9.getNumber());
                                            fishpondBoxes.setBox9_sim(sim10.getNumber());
                                            fishpondBoxes.setBox9_sim(sim11.getNumber());
                                            validNumbers = true;
                                        }
                                        break;
                                }
                                if(validNumbers) {
                                    String id2 = fla.getText().toString();
                                    ref2.child(id2).setValue(fishpondBoxes);
                                    boxesPushed = true;
                                } else {
                                    Toast.makeText(getContext(), "Please check for invalid numbers", Toast.LENGTH_LONG).show();
                                }

                            }
                            if(boxesPushed) {
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

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgress.dismiss();
                        Log.d("FIREBASE", String.valueOf(databaseError));
                    }
                });
            }
        }


    }

    public void cancel_action(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}