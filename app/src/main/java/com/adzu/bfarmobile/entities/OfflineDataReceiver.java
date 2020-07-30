package com.adzu.bfarmobile.entities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class OfflineDataReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    private static final String BFAR_NUMBER = "+639361529943";
    String msg, phoneNo = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == SMS_RECEIVED) {
            Bundle dataBundle = intent.getExtras();
            if (dataBundle != null) {
                Object[] mypdu = (Object[]) dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for (int i = 0; i < mypdu.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i], format);
                    } else {
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress();
                }
                if (phoneNo.equals(BFAR_NUMBER)) {
                    String[] receivedData = msg.split(",");
                    FishpondRecord record = new FishpondRecord();
                    record.setSim_number(receivedData[0]);
                    record.setTimestamp(Long.parseLong(receivedData[1]));
                    record.setPh_level(Float.parseFloat(receivedData[2]));
                    record.setSalinity(Float.parseFloat(receivedData[3]));
                    record.setTemperature(Float.parseFloat(receivedData[4]));
                    record.setDo_level(Float.parseFloat(receivedData[5]));
                    Intent mIntent = new Intent(context, OfflineDataStorage.class);
                    mIntent.putExtra("record", record);
                    context.startService(mIntent);
                }
            }
        }
    }
}
