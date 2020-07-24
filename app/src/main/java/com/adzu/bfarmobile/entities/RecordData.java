package com.adzu.bfarmobile.entities;

public class RecordData {
    public static short checkTemp(float temp){
        if(temp < 28 && temp >= 25)
            return 0;
        else if(temp < 25)
            return 1;
        else if(temp > 33)
            return 2;
        return -1;
    }

    public static short checkPh(float ph){
        if(ph < 7.5 && ph >= 7.2)
            return 3;
        else if(ph < 7.2)
            return 4;
        else if(ph > 8.5)
            return 5;
        return -1;
    }

    public static short checkSalinity(float salinity){
        if(salinity < 10)
            return 6;
        else if(salinity > 30)
            return 7;
        return -1;
    }

    public static short checkDo(float DO){
        if(DO < 5)
            return 8;
        else if(DO > 6)
            return 9;
        return -1;
    }
}
