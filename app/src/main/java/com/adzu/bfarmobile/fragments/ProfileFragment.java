package com.adzu.bfarmobile.fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.activities.LoginActivity;
import com.adzu.bfarmobile.activities.MainActivity;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.FishpondRecord;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.adzu.bfarmobile.entities.OperatorAdapter;
import com.adzu.bfarmobile.entities.ProfileAdapter;
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

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View view;
    private long fla_num;
    private FishpondOperator operator;
    private TextView opnum, opname, opfla, opsim1, opsim2, opaddress, opsize, opissuance, opexpiry, opdetails, opstatus;
    private ConstraintLayout opdetails_short;
    private ExpandableLayout expandableLayout, expandableLayout2;
    private List<FishpondRecord> recordList;
    private FishpondRecord latestRecord;
    private TableRow table;
    private int count, count1;
    private LinearLayout layoutss;
    private ProfileListListener profileListListener;
    private OperatorListListener operatorListListener;
    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerView;

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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        layoutss = view.findViewById(R.id.table_container);
        table = view.findViewById(R.id.latest_data1);
        recordList = new ArrayList<>();

        expandableLayout = view.findViewById(R.id.expandable_layout2);
        expandableLayout2 = view.findViewById(R.id.expandable_layout3);
        opdetails = view.findViewById(R.id.operatordetails);
        opdetails_short = view.findViewById(R.id.opdetails_short);
        TextView fishpond_data = view.findViewById(R.id.fishpond_data);

        fishpond_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!expandableLayout2.isExpanded()) {
                    if(!recordList.isEmpty()) {
                        table.setVisibility(View.INVISIBLE);
                        layoutss.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 42));
                        expandableLayout2.expand();
                    }
                }
                else {
                    if(!recordList.isEmpty()) {
                        layoutss.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 84));
                        table.setVisibility(View.VISIBLE);
                        expandableLayout2.collapse();
                    }
                }
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView3);

        opdetails.setOnClickListener(this);
        opdetails_short.setOnClickListener(this);


        opstatus = view.findViewById(R.id.profile_opstatus);
        opnum = view.findViewById(R.id.profile_opnum);
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
            public void dataRetrieved(DataSnapshot dataSnapshot) {
                operator = new FishpondOperator((Map<String, Object>)dataSnapshot.getValue());
                Log.d("Test", "Operator Name: " + operator.getFullName());
                opnum.setText(String.valueOf(operator.getOperator_number()));
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
                    ((TextView) view.findViewById(R.id.latest_pressure)).setText(String.valueOf(latestRecord.getPressure()));
                    ((TextView) view.findViewById(R.id.latest_temperature)).setText(String.valueOf(latestRecord.getTemperature()));
                    ((TextView) view.findViewById(R.id.latest_salinity)).setText(String.valueOf(latestRecord.getSalinity()));
                    ((TextView) view.findViewById(R.id.norecord)).setVisibility(View.GONE);

                    profileAdapter = new ProfileAdapter(view.getContext(), recordList);

                    recyclerView = view.findViewById(R.id.recyclerView3);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


                    recyclerView.setAdapter(profileAdapter);
                }
            }
        };

        operatorListListener = new OperatorListListener() {
            @Override
            public void onGetList(int count) {
                if(count == 0){

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("fishpond_record");
                    Query query = ref2.orderByChild("sim_number").equalTo(operator.getSim2());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()){
                                recordList.add(snap.getValue(FishpondRecord.class));
                            }try {
                                Collections.sort(recordList, new Comparator<FishpondRecord>() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public int compare(FishpondRecord t2, FishpondRecord t1) {
                                        return Long.compare(t1.getTimestamp(), t2.getTimestamp());
                                    }
                                });
                            } catch(Exception e){
                                Toast.makeText(getContext(), "Cannot sort records", Toast.LENGTH_LONG).show();
                            }
                            if(!recordList.isEmpty()) {
                                ((TextView)view.findViewById(R.id.fishpond_data)).setText("Fishpond Data from " + operator.getSim2());
                                latestRecord = recordList.get(0);
                                profileListListener.onGetList(count1++);
                            }
                            else{
                                table.setVisibility(View.GONE);
                                ((TextView)view.findViewById(R.id.norecord)).setVisibility(View.VISIBLE);
                                ((TextView)view.findViewById(R.id.norecord)).setText("No Record From Sim\n" + operator.getSim2());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        };

    }

    @Override
    public void onClick(View view) {
        if(!expandableLayout.isExpanded())
            expandableLayout.expand();
        else
            expandableLayout.collapse();
    }
}