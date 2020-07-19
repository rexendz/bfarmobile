package com.adzu.bfarmobile.entities;

import com.google.firebase.database.DataSnapshot;

public interface OnGetDataListener {
    void dataRetrieved(DataSnapshot dataSnapshot);
    void dataExists(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
