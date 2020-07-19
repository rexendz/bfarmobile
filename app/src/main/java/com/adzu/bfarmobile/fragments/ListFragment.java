package com.adzu.bfarmobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.activities.MainActivity;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.FishpondOperator;
import com.adzu.bfarmobile.entities.OperatorAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ListFragment extends Fragment {
    private Account user;

    private RecyclerView recyclerView;
    private List<FishpondOperator> operatorList;
    private OperatorAdapter operatorAdapter;
    private TextView text_main;
    private View view;
    private Timer timer;

    public ListFragment() {
    }

    public void setUser(Account user){
        this.user = user;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        text_main = view.findViewById(R.id.text_main);


        operatorList = new ArrayList<>();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).populateCards();
                    }
                });
            }

        }, 0, 100);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("operator");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    operatorList.add(snap.getValue(FishpondOperator.class));
                }
                operatorAdapter = new OperatorAdapter(view.getContext(), operatorList);

                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


                recyclerView.setAdapter(operatorAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(timer != null){
            timer.cancel();
        }
    }

    public OperatorAdapter getOperatorAdapter(){
        return operatorAdapter;
    }

    public void updateTextView(int i){
        text_main.setVisibility(i);
    }

}