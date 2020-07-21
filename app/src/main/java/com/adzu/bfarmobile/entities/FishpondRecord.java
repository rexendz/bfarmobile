package com.adzu.bfarmobile.entities;

import java.util.Date;

public class FishpondRecord {

    private long fishpond_id;
    private float ph_level;
    private float salinity;
    private float temperature;
    private float pressure;
    private float do_level;
    private String sim_number;
    private String record_date;
    private String record_datetime;

    public String getRecord_datetime() {
        return record_datetime;
    }

    public void setRecord_datetime(String record_datetime) {
        this.record_datetime = record_datetime;
    }

    public String getSim_number() {
        return sim_number;
    }

    public void setSim_number(String sim_number) {
        this.sim_number = sim_number;
    }


    public long getFishpond_id() {
        return fishpond_id;
    }

    public float getPressure(){
        return pressure;
    }

    public void setPressure(float pressure){
        this.pressure = pressure;
    }

    public void setFishpond_id(long fishpond_id) {
        this.fishpond_id = fishpond_id;
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

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getDo_level() {
        return do_level;
    }

    public void setDo_level(float do_level) {
        this.do_level = do_level;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }


}
