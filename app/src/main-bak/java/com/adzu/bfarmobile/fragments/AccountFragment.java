package com.adzu.bfarmobile.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.AccountAdapter;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AccountFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, AccountAdapter.AccountListClickListener {

    private RecyclerView recyclerView;
    private List<Account> accountList;
    private AccountAdapter accountAdapter;
    private Account currentAccount;
    private View view;
    private boolean isActive;
    private RadioGroup rg1, rg2;
    private Timer timer;
    private int filter;
    private DatabaseReference ref;
    private int count;

    public void setCurrentAccount(Account account){
        currentAccount = account;
    }

    private AccountListListener accountListListener = new AccountListListener() {
        @Override
        public void onGetList() {
            if(count == 0) {
                accountAdapter = new AccountAdapter(getContext(), accountList, AccountFragment.this);

                recyclerView = view.findViewById(R.id.recyclerView2);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


                recyclerView.setAdapter(accountAdapter);
            }
        }
    };

    public interface AccountListListener{
        void onGetList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isActive = true;
        count = 0;
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        accountList = new ArrayList<>();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isActive) {
                                populateAccountCards();
                            }
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }, 0, 500);

        rg1 = view.findViewById(R.id.fg_acc1);
        rg2 = view.findViewById(R.id.fg_acc2);

        rg1.setOnCheckedChangeListener(this);
        rg2.setOnCheckedChangeListener(this);

        ref = FirebaseDatabase.getInstance().getReference().child("account");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    accountList.add(snap.getValue(Account.class));
                }
                accountListListener.onGetList();
                count++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public AccountAdapter getAccountAdapter(){
        return this.accountAdapter;
    }

    /*
    0 - Deactivate Account
    1 - Remove Account
    2 - Remove as Admin
    3 - Make Admin
    4 - Activate Account
     */
    private void modifyAccount(DatabaseReference ref, final int i, final String user) {
        DatabaseUtil dbUtil = new DatabaseUtil();
        if(user.equals("elmer"))
            Toast.makeText(getContext(), "Cannot Modify Root User", Toast.LENGTH_LONG).show();
        else {
            final Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            dbUtil.readDataByUsername(user, ref, new OnGetDataListener() {
                @Override
                public void dataRetrieved(final DataSnapshot dataSnapshot) {
                    switch (i) {
                        case 0:
                            DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getContext(), "Account Deactivated", Toast.LENGTH_LONG).show();
                                            dataSnapshot.getRef().child("activated").setValue(false);
                                            dataSnapshot.getRef().child("deactivated_by").setValue(currentAccount.getUsername());
                                            dataSnapshot.getRef().child("admin").setValue(false);
                                            dataSnapshot.getRef().child("removed_admin_by").setValue(currentAccount.getUsername());
                                            fragmentTransaction.detach(currentFragment);
                                            fragmentTransaction.attach(currentFragment);
                                            fragmentTransaction.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("Deactivate Account of " + user + "?").setPositiveButton("Yes", dialogClickListener1)
                                    .setNegativeButton("No", dialogClickListener1).show();
                            break;
                        case 1:
                            DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                            dataSnapshot.getRef().removeValue();
                                            fragmentTransaction.detach(currentFragment);
                                            fragmentTransaction.attach(currentFragment);
                                            fragmentTransaction.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                            builder2.setMessage("Delete Account of " + user + "?\nWARNING: ALL DATA WILL BE LOST").setPositiveButton("Yes", dialogClickListener2)
                                    .setNegativeButton("No", dialogClickListener2).show();
                            break;
                        case 2:
                            DialogInterface.OnClickListener dialogClickListener3 = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getContext(), "Removed Account's Admin Privilege", Toast.LENGTH_LONG).show();
                                            dataSnapshot.getRef().child("admin").setValue(false);
                                            dataSnapshot.getRef().child("removed_admin_by").setValue(currentAccount.getUsername());
                                            dataSnapshot.getRef().child("made_admin_by").setValue("NONE");
                                            fragmentTransaction.detach(currentFragment);
                                            fragmentTransaction.attach(currentFragment);
                                            fragmentTransaction.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                            builder3.setMessage("Remove Admin Access of " + user + "?").setPositiveButton("Yes", dialogClickListener3)
                                    .setNegativeButton("No", dialogClickListener3).show();
                            break;
                        case 3:
                            DialogInterface.OnClickListener dialogClickListener4 = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getContext(), "Account Granted Admin Privileges", Toast.LENGTH_LONG).show();
                                            dataSnapshot.getRef().child("admin").setValue(true);
                                            dataSnapshot.getRef().child("made_admin_by").setValue(currentAccount.getUsername());
                                            dataSnapshot.getRef().child("removed_admin_by").setValue("NONE");
                                            fragmentTransaction.detach(currentFragment);
                                            fragmentTransaction.attach(currentFragment);
                                            fragmentTransaction.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(getContext());
                            builder4.setMessage("Grant Admin Access to " + user + "?").setPositiveButton("Yes", dialogClickListener4)
                                    .setNegativeButton("No", dialogClickListener4).show();
                            break;
                        case 4:
                            DialogInterface.OnClickListener dialogClickListener5 = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Toast.makeText(getContext(), "Account Activated", Toast.LENGTH_LONG).show();
                                            dataSnapshot.getRef().child("activated").setValue(true);
                                            dataSnapshot.getRef().child("activated_by").setValue(currentAccount.getUsername());
                                            dataSnapshot.getRef().child("deactivated_by").setValue("NONE");
                                            fragmentTransaction.detach(currentFragment);
                                            fragmentTransaction.attach(currentFragment);
                                            fragmentTransaction.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder5 = new AlertDialog.Builder(getContext());
                            builder5.setMessage("Activate Account of " + user + "?").setPositiveButton("Yes", dialogClickListener5)
                                    .setNegativeButton("No", dialogClickListener5).show();
                            break;

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
    }

    public void populateAccountCards(){
        if(accountAdapter != null) {
            try {
                accountAdapter.setFilter(filter);
                accountAdapter.getFilter().filter("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(timer != null){
            timer.cancel();
        }
    }

    public void setActive(boolean status){
        isActive = status;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton rb = view.findViewById(i);
        switch (radioGroup.getId()) {
            case R.id.fg_acc1:
                rg2.setOnCheckedChangeListener(null);
                rg2.clearCheck();
                rg2.setOnCheckedChangeListener(this);
                filter = radioGroup.indexOfChild(rb);
                break;
            case R.id.fg_acc2:
                rg1.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg1.setOnCheckedChangeListener(this);
                filter = radioGroup.indexOfChild(rb) + 3;
                break;
        }
        populateAccountCards();
    }
    @Override
    public void leftButtonClick(int i, String button, String username) {
        switch(button){
            case "DEACTIVATE ACCOUNT":
                modifyAccount(ref, 0, username);
                break;
            case "REMOVE REQUEST":
                modifyAccount(ref, 1, username);
                break;
        }
    }

    @Override
    public void rightButtonClick(int i, String button, String username) {
        switch(button){
            case "REMOVE AS ADMIN":
                modifyAccount(ref, 2, username);
                break;
            case "MAKE ADMIN":
                modifyAccount(ref, 3, username);
                break;
            case "ACTIVATE ACCOUNT":
                modifyAccount(ref, 4, username);
                break;
        }
    }
}