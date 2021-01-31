package com.adzu.bfarmobile.entities;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class OfflineDataStorage extends IntentService {
    private Handler mHandler;
    private Context mContext = this;

    public OfflineDataStorage() {
        super("OfflineDataStorage");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FishpondRecord record = intent.getParcelableExtra("record");
        saveToDatabase(record);
    }

    private void saveToDatabase(final FishpondRecord record) {
        String timestampString = String.valueOf(record.getTimestamp());
        String testKey = "";
        try {
            testKey = timestampString.substring(6, 13) + record.getSim_number().substring(6) + timestampString.substring(0, 6);
        } catch (java.lang.StringIndexOutOfBoundsException e) {
            mHandler.post(new DisplayToast(mContext, "Error parsing timestamp! Data not saved"));
            return;
        }
        final String uniqueKey = testKey;
        Log.d("test", "saveToDatabase: " + uniqueKey);
        if (uniqueKey.equals("")) {
            mHandler.post(new DisplayToast(mContext, "Error creating key! Data not saved"));
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("fishpond_record");
            ref.child(uniqueKey).setValue(record);
            mHandler.post(new DisplayToast(mContext, "Fishpond Data from SMS Successfully Saved!"));
        }
    }
}