package com.adzu.bfarmobile.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.FishpondRecord;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.adzu.bfarmobile.entities.TimePickerFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddRecordFragment extends Fragment{

    private FishpondRecord fishpondRecord;

    private IntlPhoneInput sim;
    private EditText text_ph, text_sal, text_temp, text_do;
    private Calendar myCalendar;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button_record);
        text_ph = view.findViewById(R.id.add_ph);
        text_sal = view.findViewById(R.id.add_salinity);
        text_temp = view.findViewById(R.id.add_temperature);
        text_do = view.findViewById(R.id.add_dolevel);
        sim = view.findViewById(R.id.add_sim);
        sim.setEmptyDefault("PH");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text_do.getText().toString().isEmpty() || text_temp.getText().toString().isEmpty() || text_ph.getText().toString().isEmpty() || text_sal.getText().toString().isEmpty())
                    Toast.makeText(view.getContext(), "Fill in missing fields", Toast.LENGTH_LONG).show();

                else{
                    fishpondRecord = new FishpondRecord();
                    fishpondRecord.setDo_level(Float.parseFloat(text_do.getText().toString()));
                    fishpondRecord.setPh_level(Float.parseFloat(text_ph.getText().toString()));
                    fishpondRecord.setSalinity(Float.parseFloat(text_sal.getText().toString()));
                    fishpondRecord.setTemperature(Float.parseFloat(text_temp.getText().toString()));
                    if (!sim.isValid())
                        Toast.makeText(view.getContext(), "Please enter valid phone number", Toast.LENGTH_LONG).show();
                    else
                        fishpondRecord.setSim_number(sim.getNumber());

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("fishpond_record");
                    String id = ref.push().getKey();
                    Map<String, Object> value = new HashMap<>();
                    value.put("sim_number", fishpondRecord.getSim_number());
                    value.put("ph_level", fishpondRecord.getPh_level());
                    value.put("salinity", fishpondRecord.getSalinity());
                    value.put("temperature", fishpondRecord.getTemperature());
                    value.put("do_level", fishpondRecord.getDo_level());
                    value.put("timestamp", ServerValue.TIMESTAMP);
                    ref.child(id).setValue(value);

                    Toast.makeText(view.getContext(), "Record Added!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
