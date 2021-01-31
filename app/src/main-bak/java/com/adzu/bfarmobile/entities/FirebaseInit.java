package com.adzu.bfarmobile.entities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;



public class FirebaseInit extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}