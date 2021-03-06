package com.adzu.bfarmobile.entities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class DatabaseUtil {

    private static DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    private static boolean isConnected = false;

    private static ValueEventListener connectionListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            isConnected = snapshot.getValue(Boolean.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d("FirebaseDatabase", "DatabaseError: " + error);
        }
    };

    public static void readDataByUsername(String username, final DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        Query query = ref.orderByChild("username").equalTo(username);
        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listener.dataRetrieved(dataSnapshot);
                ref.removeEventListener(this);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.dataExists(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public static void readDataByKey(String account_key, final DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        Query query = ref.orderByKey().equalTo(account_key);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listener.dataRetrieved(dataSnapshot);
                ref.removeEventListener(this);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }

    public static void readDataByFLA(long FLA, final DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        Query query = ref.orderByChild("fla_number").equalTo(FLA);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listener.dataRetrieved(dataSnapshot);
                ref.removeEventListener(this);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }

    public static void startListeningForConnection() {
        if (connectionListener != null) {
            connectedRef.addValueEventListener(connectionListener);
        }
    }

    public static void stopListeningForConnection() {
        if (connectionListener != null) {
            connectedRef.removeEventListener(connectionListener);
        }
    }

    public static boolean isConnected() {
        return isConnected;
    }

}
