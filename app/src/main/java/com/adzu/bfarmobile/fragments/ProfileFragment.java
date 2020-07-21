package com.adzu.bfarmobile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View view;
    private long fla_num;
    private FishpondOperator operator;
    private TextView opnum, opname, opfla, opsim1, opsim2, opaddress, opsize, opissuance, opexpiry, opdetails, opstatus;
    private ExpandableLayout expandableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fla_num = getArguments().getLong("fla_num");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        expandableLayout = view.findViewById(R.id.expandable_layout2);
        opdetails = view.findViewById(R.id.operatordetails);

        opdetails.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        if(!expandableLayout.isExpanded())
            expandableLayout.expand();
        else
            expandableLayout.collapse();
    }
}