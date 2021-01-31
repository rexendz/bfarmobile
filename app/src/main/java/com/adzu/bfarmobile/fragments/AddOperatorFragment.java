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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondBoxes;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;
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
    private final int MAX_BOX_COUNT = 10;
    private final int MIN_BOX_COUNT = 1;
    private int boxCount = MIN_BOX_COUNT;
    private EditText boxes;
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

    private ExpandableLayout expandableLayout;
    private ConstraintLayout layout_op;
    private EditText op_fla;
    private EditText op_firstname;
    private EditText op_middlename;
    private EditText op_lastname;
    private TextView simt3, simt4, simt5, simt6, simt7, simt8, simt9, simt10, simt11;
    private IntlPhoneInput sim1, sim2, sim3, sim4, sim5, sim6, sim7, sim8, sim9, sim10, sim11;
    private EditText op_size;
    private EditText op_barangay;
    private ProgressBar loading;
    private ImageButton buttonAdd, buttonSub;

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
        boxes = view.findViewById(R.id.field_box3);
        layout_op = view.findViewById(R.id.layout_op);
        expandableLayout = view.findViewById(R.id.op_expand);
        buttonAdd = view.findViewById(R.id.button_add3);
        buttonSub = view.findViewById(R.id.button_sub3);
        sim1 = view.findViewById(R.id.op_sim1);
        sim2 = view.findViewById(R.id.field_sim2);
        sim3 = view.findViewById(R.id.field_sim3);
        sim4 = view.findViewById(R.id.field_sim4);
        sim5 = view.findViewById(R.id.field_sim5);
        sim6 = view.findViewById(R.id.field_sim6);
        sim7 = view.findViewById(R.id.field_sim7);
        sim8 = view.findViewById(R.id.field_sim8);
        sim9 = view.findViewById(R.id.field_sim9);
        sim10 = view.findViewById(R.id.field_sim10);
        sim11 = view.findViewById(R.id.field_sim11);
        simt3 = view.findViewById(R.id.textView100);
        simt4 = view.findViewById(R.id.textView101);
        simt5 = view.findViewById(R.id.textView102);
        simt6 = view.findViewById(R.id.textView103);
        simt7 = view.findViewById(R.id.textView104);
        simt8 = view.findViewById(R.id.textView105);
        simt9 = view.findViewById(R.id.textView106);
        simt10 = view.findViewById(R.id.textView107);
        simt11 = view.findViewById(R.id.textView108);
        op_size = view.findViewById(R.id.op_size);
        op_barangay = view.findViewById(R.id.op_barangay);

        rootLayout = view.findViewById(R.id.root_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        s_city = view.findViewById(R.id.spinner_city);
        s_municipality = view.findViewById(R.id.spinner_municipality);

        layout_op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else
                    expandableLayout.expand();
            }
        });

        boxes.setText(Integer.toString(MIN_BOX_COUNT));

        boxes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if(Integer.parseInt(boxes.getText().toString()) > MAX_BOX_COUNT) {
                        boxCount = MAX_BOX_COUNT;
                        boxes.setText(Integer.toString(MAX_BOX_COUNT));
                    } else if(Integer.parseInt(boxes.getText().toString()) < MIN_BOX_COUNT) {
                        boxCount = MIN_BOX_COUNT;
                        boxes.setText(Integer.toString(MIN_BOX_COUNT));
                    } else {
                        boxCount = Integer.parseInt(boxes.getText().toString());
                    }
                    filterBoxSims();

                }
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (boxCount < MAX_BOX_COUNT)
                        boxCount++;
                    boxes.setText(Integer.toString(boxCount));
                    filterBoxSims();
            }
        });

        buttonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (boxCount > MIN_BOX_COUNT)
                        boxCount--;
                    boxes.setText(Integer.toString(boxCount));
                    filterBoxSims();
            }
        });

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
                            if(boxCount == 0)
                                boxes.setText("1");
                            else
                                boxes.setText(Long.toString(boxCount));
                            filterBoxSims();
                            sim1.setNumber(existingAccount.getSim1());
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
                String f8 = op_size.getText().toString();
                String f9 = issuance_date.getText().toString();
                String f10 = expiry_date.getText().toString();

                boolean validNumbers = false;
                final FishpondBoxes fishpondBoxes = new FishpondBoxes();
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
                    if (f1.isEmpty() || f2.isEmpty() || f3.isEmpty() || f4.isEmpty() || f5.isEmpty() || f8.isEmpty() || f9.isEmpty() || f10.isEmpty()) {
                        Toast.makeText(view.getContext(), "Please fill in the missing fields", Toast.LENGTH_LONG).show();
                    } else {
                        final FishpondOperator operator = new FishpondOperator();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("operator");

                        operator.setFirstname(f1);
                        operator.setMiddlename(f2);
                        operator.setLastname(f3);
                        operator.setBoxes(boxCount);
                        operator.setFla_number(Long.parseLong(f4));
                        operator.setBarangay(f5);
                        operator.setSim1(sim1.getNumber());
                        operator.setCityProvince(selectedCity);
                        operator.setMunicipality(selectedMunicipality);
                        operator.setFishpond_size(f8);
                        operator.setIssuance_date(f9);
                        operator.setExpiration_date(f10);

                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("operator");
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean flaTaken = false;
                                boolean sim1Taken = false;
                                for (DataSnapshot snap : snapshot.getChildren()){
                                    if(snap.getValue(FishpondOperator.class).getFla_number() == operator.getFla_number()){
                                        flaTaken = true;
                                    }
                                    if(snap.getValue(FishpondOperator.class).getSim1().equals(operator.getSim1())){
                                        sim1Taken = true;
                                    }
                                }
                                if(flaTaken){
                                    op_fla.setError("FLA Number Already Taken!");
                                    op_fla.requestFocus();
                                } else if (sim1Taken) {
                                    Toast.makeText(getContext(), "Sim 1 already taken", Toast.LENGTH_LONG).show();
                                    sim1.requestFocus();
                                } else {
                                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("fishpond_box");
                                    String id2 = Long.toString(operator.getFla_number());
                                    ref3.child(id2).setValue(fishpondBoxes);

                                    String id = ref.push().getKey();
                                    ref.child(id).setValue(operator);
                                    Toast.makeText(getContext(), "Operator Registered!", Toast.LENGTH_LONG).show();
                                    ((EditText) ((LinearLayout) sim1.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim2.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim3.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim4.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim5.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim6.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim7.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim8.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim9.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim10.getChildAt(0)).getChildAt(1)).setText("");
                                    ((EditText) ((LinearLayout) sim11.getChildAt(0)).getChildAt(1)).setText("");

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

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Check for invalid numbers", Toast.LENGTH_LONG).show();
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
                ((EditText) ((LinearLayout) sim1.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim2.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim3.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim4.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim5.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim6.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim7.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim8.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim9.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim10.getChildAt(0)).getChildAt(1)).setText("");
                ((EditText) ((LinearLayout) sim11.getChildAt(0)).getChildAt(1)).setText("");
                boxCount = 0;
                boxes.setText("1");
                filterBoxSims();
                op_size.setText("");
                op_barangay.setText("");
            }
        });
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
