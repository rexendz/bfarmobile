package com.adzu.bfarmobile.entities;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class FishpondRecord implements Parcelable {

    private float ph_level;
    private float salinity;
    private float temperature;
    private float do_level;
    private String sim_number;
    private Long timestamp;

    public FishpondRecord(){

    }

    protected FishpondRecord(Parcel in) {
        ph_level = in.readFloat();
        salinity = in.readFloat();
        temperature = in.readFloat();
        do_level = in.readFloat();
        sim_number = in.readString();
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
    }

    public static final Creator<FishpondRecord> CREATOR = new Creator<FishpondRecord>() {
        @Override
        public FishpondRecord createFromParcel(Parcel in) {
            return new FishpondRecord(in);
        }

        @Override
        public FishpondRecord[] newArray(int size) {
            return new FishpondRecord[size];
        }
    };

    public Long getTimestamp() { return timestamp;}

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSim_number() {
        return sim_number;
    }

    public void setSim_number(String sim_number) {
        this.sim_number = sim_number;
    }

    public float getPh_level() {
        return ph_level;
    }

    public void setPh_level(float ph_level) {
        this.ph_level = ph_level;
    }

    public float getSalinity() {
        return salinity;
    }

    public void setSalinity(float salinity) {
        this.salinity = salinity;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getTemperatureCelsius() { return (temperature - 273.15f);}

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getDo_level() {
        return do_level;
    }

    public void setDo_level(float do_level) {
        this.do_level = do_level;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(ph_level);
        parcel.writeFloat(salinity);
        parcel.writeFloat(temperature);
        parcel.writeFloat(do_level);
        parcel.writeString(sim_number);
        if (timestamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(timestamp);
        }
    }
}
