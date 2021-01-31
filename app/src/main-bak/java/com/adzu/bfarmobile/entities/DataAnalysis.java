package com.adzu.bfarmobile.entities;

public class DataAnalysis {
    /*
    0 - TEMP < 28
    1 - TEMP < 25
    2 - TEMP > 33
    3 - PH < 7.5
    4 - PH < 7.2
    5 - PH > 8.5
    6 - SAL < 10
    7 - SAL > 30
    8 - DO < 5
    9 - DO > 6
     */
    private static final String[] warning_msg = {
            "TEMP IS TOO LOW FOR PRAWNS!","TEMP IS TOO LOW!", "TEMP IS TOO HIGH!",
            "PH LEVEL IS TOO LOW FOR PRAWNS!", "PH LEVEL IS TOO LOW!", "PH LEVEL IS TOO HIGH!",
            "SALINITY IS TOO LOW!", "SALINITY IS TOO HIGH!",
            "DO LEVEL IS TOO LOW!", "DO LEVEL IS TOO HIGH!"
    };
    private static final String[] suggestion_msg = {
            "CHANGE WATER IMMEDIATELY!", "CHANGE WATER IMMEDIATELY!", "CHANGE WATER IMMEDIATELY!",
            "CHANGE WATER OR APPLY BAKING SODA TO WATER", "CHANGE WATER OR APPLY BAKING SODA TO WATER", "APPLY LIMING METHOD TO DECREASE PH LEVEL",
            "LET IN SALTY WATER TO POND", "LET IN FRESH WATER TO POND",
            "LOWER TEMPERATURE OF WATER", "CHANGE WATER OR INCREASE TEMPERATURE OF WATER"
    };

    private short warning_type;

    public DataAnalysis(short warning_type){
        this.warning_type = warning_type;
    }

    public String getWarning(){
        return warning_msg[warning_type];
    }

    public String getSuggestion(){
        return suggestion_msg[warning_type];
    }
}
