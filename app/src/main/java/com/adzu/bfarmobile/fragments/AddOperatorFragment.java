package com.adzu.bfarmobile.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public class AddOperatorFragment extends Fragment implements Spinner.OnItemSelectedListener{
    private EditText issuance_date;
    private EditText expiry_date;
    private Calendar myCalendar;
    private ConstraintLayout rootLayout;
    private AnimationDrawable animDrawable;
    private int textChosen = 0;
    final private String[] c_sibugay = { "Alicia", "Buug", "Diplahan", "Imelda", "Ipil", "Kabasalan", "Mabuhay", "Malangas", "Naga", "Olutanga", "Payao", "Roseller Lim", "Siay", "Talusan", "Titay", "Tungawan" };
    final private String[] c_delsur = { "Aurora", "Bayog", "Dimataling", "Dinas", "Dumalinao", "Dumingag", "Guipos", "Josefina", "Kumalarang", "Labangan", "Lakewood", "Lapuyan", "Mahayag", "Margosatubig", "Midsalip", "Molave", "Pagadian", "Pitogo", "Ramon Magsaysay", "San Miguel", "San Pablo", "Sominot", "Tabina", "Tambulig", "Tigbao", "Tukuran", "Vincenzo A. Sagun" };
    final private String[] c_delnor = { "Baliguian", "Godod", "Gutalac", "Jose Dalman", "Kalawit", "Katipunan", "La Libertad", "Labason", "Leon B. Postigo", "Liloy", "Manukan", "Mutia", "Piñan", "Polanco", "President Manuel A. Roxas", "Rizal", "Salug", "Sergio Osmeña Sr.", "Siayan", "Sibuco", "Sibutad", "Sindangan", "Siocon", "Sirawai", "Tampilisan" };
    final private String[] cities = { "Zamboanga City", "Zamboanga del Norte", "Zamboanga del Sur", "Zamboanga Sibugay" };
    private Spinner s_city;
    private Spinner s_municipality;
    private ArrayAdapter<String> adapter_nor;
    private ArrayAdapter<String> adapter_sur;
    private ArrayAdapter<String> adapter_sib;
    private ArrayAdapter<String> adapter_empty;
    private Button button, button1;

    private Account existingAccount;

    private String selectedCity;
    private String selectedMunicipality;

    private EditText op_fla;
    private EditText op_firstname;
    private EditText op_middlename;
    private EditText op_lastname;
    private IntlPhoneInput op_sim1;
    private IntlPhoneInput op_sim2;
    private EditText op_size;
    private EditText op_barangay;
    private ProgressBar loading;

    private long count = -1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operator, container, false);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(textChosen == 1)
            issuance_date.setText(sdf.format(myCalendar.getTime()));
        else if(textChosen == 2)
            expiry_date.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myCalendar = Calendar.getInstance();
        loading = view.findViewById(R.id.progressBar2);

        op_fla = view.findViewById(R.id.op_fla);
        op_firstname = view.findViewById(R.id.op_firstname);
        op_middlename = view.findViewById(R.id.op_middlename);
        op_lastname = view.findViewById(R.id.op_lastname);
        op_sim1 = view.findViewById(R.id.op_sim1);
        op_sim2 = view.findViewById(R.id.op_sim2);
        op_size = view.findViewById(R.id.op_size);
        op_barangay = view.findViewById(R.id.op_barangay);

        rootLayout = view.findViewById(R.id.root_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        s_city = view.findViewById(R.id.spinner_city);
        s_municipality = view.findViewById(R.id.spinner_municipality);

        op_sim1.setEmptyDefault("PH");
        op_sim2.setEmptyDefault("PH");

        List<String> empty = new ArrayList<>();
        empty.add("No Municipality");

        op_fla.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    long fla_data = -25565;
                    try {
                        fla_data = Long.parseLong(op_fla.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace(); // Empty number
                    }
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("account");
                    DatabaseUtil.readDataByFLA(fla_data, ref, new OnGetDataListener() {
                        @Override
                        public void dataRetrieved(DataSnapshot dataSnapshot) {
                            existingAccount = new Account((Map<String, Object>) dataSnapshot.getValue());
                            op_firstname.setText(existingAccount.getFirstname());
                            op_middlename.setText(existingAccount.getMiddlename());
                            op_lastname.setText(existingAccount.getLastname());
                            op_sim1.setNumber(existingAccount.getSim1());
                            op_sim2.setNumber(existingAccount.getSim2());
                        }

                        @Override
                        public void dataExists(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
            }
        });

        ArrayList<String> spinner_list = new ArrayList<>(Arrays.asList(cities));
        ArrayList<String> mun_nor = new ArrayList<>(Arrays.asList(c_delnor));
        ArrayList<String> mun_sur = new ArrayList<>(Arrays.asList(c_delsur));
        ArrayList<String> mun_sib = new ArrayList<>(Arrays.asList(c_sibugay));
        ArrayList<String> mun_empty = new ArrayList<>(empty);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item2, spinner_list);
        adapter_nor = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item2, mun_nor);
        adapter_sur = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item2, mun_sur);
        adapter_sib = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item2, mun_sib);
        adapter_empty = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item2, mun_empty);

        s_city.setAdapter(adapter);
        s_municipality.setAdapter(null);
        s_municipality.setEnabled(false);

        s_city.setOnItemSelectedListener(this);
        s_municipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMunicipality = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        issuance_date = view.findViewById(R.id.issuance_date);
        expiry_date = view.findViewById(R.id.expiry_date);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR, i);
                myCalendar.set(Calendar.MONTH, i1);
                myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();

            }

        };

        issuance_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(view.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                textChosen = 1;

            }
        });

        expiry_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(view.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                textChosen = 2;
            }
        });

        button = view.findViewById(R.id.button_register);
        button1 = view.findViewById(R.id.button_clear);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String f1 = op_firstname.getText().toString();
                String f2 = op_middlename.getText().toString();
                String f3 = op_lastname.getText().toString();
                String f4 = op_fla.getText().toString();
                String f5 = op_barangay.getText().toString();
                String f6 = "";
                String f7 = "";
                String f8 = op_size.getText().toString();
                String f9 = issuance_date.getText().toString();
                String f10 = expiry_date.getText().toString();
                if (!op_sim1.isValid() || !op_sim2.isValid()) {
                    Toast.makeText(view.getContext(), "Please enter valid phone number", Toast.LENGTH_LONG).show();
                } else {
                    f6 = op_sim1.getNumber();
                    f7 = op_sim2.getNumber();
                    if (f1.isEmpty() || f2.isEmpty() || f3.isEmpty() || f4.isEmpty() || f5.isEmpty() || f6.isEmpty() || f7.isEmpty() || f8.isEmpty() || f9.isEmpty() || f10.isEmpty()) {
                        Toast.makeText(view.getContext(), "Please fill in the missing fields", Toast.LENGTH_LONG).show();
                    } else {
                        final FishpondOperator operator = new FishpondOperator();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("operator");

                        operator.setFirstname(f1);
                        operator.setMiddlename(f2);
                        operator.setLastname(f3);
                        operator.setFla_number(Long.parseLong(f4));
                        operator.setBarangay(f5);
                        operator.setSim1(f6);
                        operator.setSim2(f7);
                        operator.setCityProvince(selectedCity);
                        operator.setMunicipality(selectedMunicipality);
                        operator.setFishpond_size(f8);
                        operator.setIssuance_date(f9);
                        operator.setExpiration_date(f10);
                        String id = ref.push().getKey();
                        ref.child(id).setValue(operator);
                        Toast.makeText(view.getContext(), "Operator Registered!", Toast.LENGTH_LONG).show();
                        ((EditText) ((LinearLayout) op_sim1.getChildAt(0)).getChildAt(1)).setText("");
                        ((EditText) ((LinearLayout) op_sim2.getChildAt(0)).getChildAt(1)).setText("");

                        op_firstname.setText("");
                        op_middlename.setText("");
                        op_lastname.setText("");
                        op_fla.setText("");
                        op_barangay.setText("");
                        op_size.setText("");
                        issuance_date.setText("");
                        expiry_date.setText("");
                    }
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                op_fla.setText("");
                op_firstname.setText("");
                op_middlename.setText("");
                op_lastname.setText("");
                ((EditText) ((LinearLayout) op_sim1.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) op_sim2.getChildAt(0)).getChildAt(1)).setText("");
                op_size.setText("");
                op_barangay.setText("");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i) {
            case 0:
                selectedCity = "Zamboanga City";
                s_municipality.setAdapter(adapter_empty);
                s_municipality.setEnabled(false);
                break;
            case 1:
                selectedCity = "Zamboanga del Norte";
                s_municipality.setAdapter(adapter_nor);
                s_municipality.setEnabled(true);
                break;
            case 2:
                selectedCity = "Zamboanga del Sur";
                s_municipality.setAdapter(adapter_sur);
                s_municipality.setEnabled(true);
                break;
            case 3:
                selectedCity = "Zamboanga Sibugay";
                s_municipality.setAdapter(adapter_sib);
                s_municipality.setEnabled(true);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
