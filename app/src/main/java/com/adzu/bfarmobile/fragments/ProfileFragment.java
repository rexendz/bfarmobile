package com.adzu.bfarmobile.fragments;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.AlphabeticIndex;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.activities.LoginActivity;
import com.adzu.bfarmobile.activities.MainActivity;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.AnalysisAdapter;
import com.adzu.bfarmobile.entities.DataAnalysis;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.FishpondRecord;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.adzu.bfarmobile.entities.OperatorAdapter;
import com.adzu.bfarmobile.entities.ProfileAdapter;
import com.adzu.bfarmobile.entities.RecordData;
import com.adzu.bfarmobile.entities.TimestampToDate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.adzu.bfarmobile.entities.RecordData.checkTemp;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View view;
    private long fla_num;
    private boolean isAdmin;
    private FishpondOperator operator;
    private TextView opname, opfla, opsim1, opsim2, opaddress, opsize, opissuance, opexpiry, opdetails, opstatus, dataAnalysis;
    private ConstraintLayout opdetails_short;
    private ExpandableLayout expandableLayout, expandableLayout2, expandableLayout3;
    private List<FishpondRecord> recordList;
    private List<DataAnalysis> dataList;
    private FishpondRecord latestRecord;
    private TableRow table;
    private int count, count1;
    private LinearLayout layoutss;
    private ProfileListListener profileListListener;
    private OperatorListListener operatorListListener;
    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerView, recyclerView2;
    private Button delete_op;

    public interface ProfileListListener{
        void onGetList(int count);
    }

    public interface OperatorListListener{
        void onGetList(int count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        count = 0;
        count1 = 0;
        this.view = inflater.inflate(R.layout.fragment_profile, container, false);
        fla_num = getArguments().getLong("fla_num");
        isAdmin = getArguments().getBoolean("is_admin");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        layoutss = view.findViewById(R.id.table_container);
        table = view.findViewById(R.id.latest_data1);
        dataAnalysis = view.findViewById(R.id.data_analysis);
        delete_op = view.findViewById(R.id.button_deleteop);

        recordList = new ArrayList<>();
        dataList = new ArrayList<>();

        expandableLayout = view.findViewById(R.id.expandable_layout2);
        expandableLayout2 = view.findViewById(R.id.expandable_layout3);
        expandableLayout3 = view.findViewById(R.id.expandable_layout5);
        opdetails = view.findViewById(R.id.operatordetails);
        opdetails_short = view.findViewById(R.id.opdetails_short);
        TextView fishpond_data = view.findViewById(R.id.fishpond_data);

        fishpond_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordList.size() > 1) {
                    if (!expandableLayout2.isExpanded()) {
                        if (!recordList.isEmpty()) {
                            table.setVisibility(View.INVISIBLE);
                            layoutss.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 42));
                            expandableLayout2.expand();
                        }
                    } else {
                        if (!recordList.isEmpty()) {
                            layoutss.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 84));
                            table.setVisibility(View.VISIBLE);
                            expandableLayout2.collapse();
                        }
                    }
                }
            }
        });

        dataAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!expandableLayout3.isExpanded())
                    expandableLayout3.expand();
                else
                    expandableLayout3.collapse();
            }
        });



        recyclerView = view.findViewById(R.id.recyclerView3);
        recyclerView2 = view.findViewById(R.id.recyclerView4);

        opdetails.setOnClickListener(this);
        opdetails_short.setOnClickListener(this);


        opstatus = view.findViewById(R.id.profile_opstatus);
        opname = view.findViewById(R.id.profile_opname);
        opfla = view.findViewById(R.id.profile_opfla);
        opaddress = view.findViewById(R.id.profile_address);
        opsim1 = view.findViewById(R.id.profile_opsim1);
        opsim2 = view.findViewById(R.id.profile_opsim2);
        opissuance = view.findViewById(R.id.profile_opissuance);
        opexpiry = view.findViewById(R.id.profile_opexpiry);
        opsize = view.findViewById(R.id.profile_opsize);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("operator");
        DatabaseUtil.readDataByFLA(fla_num, ref, new OnGetDataListener() {
            @Override
            public void dataRetrieved(final DataSnapshot dataSnapshot) {
                operator = new FishpondOperator((Map<String, Object>)dataSnapshot.getValue());
                opfla.setText(String.valueOf(operator.getFla_number()));
                opname.setText(operator.getFullName());
                opaddress.setText(operator.getAddress());
                opsim1.setText(operator.getSim1());
                opsim2.setText(operator.getSim2());
                opissuance.setText(operator.getIssuance_date());
                opexpiry.setText(operator.getExpiration_date());
                opsize.setText(operator.getFishpond_size());
                if(operator.isIsActive()){
                    opstatus.setText("Active");
                    opstatus.setBackgroundResource(R.color.colorActive);
                }
                else{
                    opstatus.setText("Expired");
                    opstatus.setBackgroundResource(R.color.colorExpired);
                }

                if(isAdmin){
                    delete_op.setVisibility(View.VISIBLE);
                    delete_op.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getContext(), "Operator Deleted", Toast.LENGTH_LONG).show();
                                            dataSnapshot.getRef().removeValue();
                                            ((MainActivity)getActivity()).replaceFragment(1);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Delete This Operator Account?\nWARNING: ALL DATA WILL BE LOST").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                    });
                }
                operatorListListener.onGetList(count++);
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
        profileListListener = new ProfileListListener() {
            @Override
            public void onGetList(int count) {
                if(count == 0) {
                    ((TextView) view.findViewById(R.id.latest_date)).setText(TimestampToDate.getDate(latestRecord.getTimestamp()));
                    ((TextView) view.findViewById(R.id.latest_ph)).setText(String.valueOf(latestRecord.getPh_level()));
                    ((TextView) view.findViewById(R.id.latest_dolevel)).setText(String.valueOf(latestRecord.getDo_level()));
                    ((TextView) view.findViewById(R.id.latest_temperature)).setText(String.valueOf(latestRecord.getTemperature()));
                    ((TextView) view.findViewById(R.id.latest_salinity)).setText(String.valueOf(latestRecord.getSalinity()));
                    ((TextView) view.findViewById(R.id.norecord)).setVisibility(View.GONE);

                    profileAdapter = new ProfileAdapter(view.getContext(), recordList);

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


                    recyclerView.setAdapter(profileAdapter);

                    short temp = RecordData.checkTemp(latestRecord.getTemperature());
                    short ph = RecordData.checkPh(latestRecord.getPh_level());
                    short sal = RecordData.checkSalinity(latestRecord.getSalinity());
                    short DO = RecordData.checkDo(latestRecord.getDo_level());
                    if(temp != -1)
                        dataList.add(new DataAnalysis(temp));
                    if(ph != -1)
                        dataList.add(new DataAnalysis(ph));
                    if(sal != -1)
                        dataList.add(new DataAnalysis(sal));
                    if(DO != -1)
                        dataList.add(new DataAnalysis(DO));

                    if(dataList.isEmpty())
                        ((ImageView) view.findViewById(R.id.data_error)).setVisibility(View.GONE);
                    else
                        ((ImageView) view.findViewById(R.id.data_error)).setVisibility(View.VISIBLE);

                    AnalysisAdapter analysisAdapter = new AnalysisAdapter(view.getContext(), dataList);

                    recyclerView2.setHasFixedSize(true);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(view.getContext()));

                    recyclerView2.setAdapter(analysisAdapter);
                }
            }
        };

        operatorListListener = new OperatorListListener() {
            @Override
            public void onGetList(int count) {
                if(count == 0) {
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            getTableData();
                        }
                    }, 0, 5000);
                }
            }
        };

    }

    private void getTableData(){
        recordList = new ArrayList<>();
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("fishpond_record");
        Query query = ref2.orderByChild("sim_number").equalTo(operator.getSim2());
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    recordList.add(snap.getValue(FishpondRecord.class));
                }
                if(!recordList.isEmpty()) {
                    ((TextView)view.findViewById(R.id.no_data)).setVisibility(View.GONE);
                    ((TableRow)view.findViewById(R.id.latest_data1)).setVisibility(View.VISIBLE);
                    latestRecord = recordList.get(0);
                    if (recordList.size() > 1) {
                        try {
                            Collections.sort(recordList, new Comparator<FishpondRecord>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public int compare(FishpondRecord t2, FishpondRecord t1) {
                                    return Long.compare(t1.getTimestamp(), t2.getTimestamp());
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Cannot sort records", Toast.LENGTH_LONG).show();
                        }
                    }
                    latestRecord = recordList.get(0);
                    ((TextView) view.findViewById(R.id.fishpond_data)).setText("Fishpond Data from " + operator.getSim2());
                    profileListListener.onGetList(count1++);
                }
                else {
                    table.setVisibility(View.GONE);
                    ((TextView)view.findViewById(R.id.no_data)).setVisibility(View.VISIBLE);
                    ((TextView)view.findViewById(R.id.norecord)).setVisibility(View.VISIBLE);
                    ((TextView)view.findViewById(R.id.norecord)).setText("No Record From Sim\n" + operator.getSim2());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(!expandableLayout.isExpanded())
            expandableLayout.expand();
        else
            expandableLayout.collapse();
    }
}