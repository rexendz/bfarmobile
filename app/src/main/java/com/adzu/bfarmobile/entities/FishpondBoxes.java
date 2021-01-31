package com.adzu.bfarmobile.entities;

import java.util.Date;
import java.util.Map;

public class FishpondBoxes {

    private String box1_sim;
    private String box2_sim;
    private String box3_sim;
    private String box4_sim;
    private String box5_sim;
    private String box6_sim;
    private String box7_sim;
    private String box8_sim;
    private String box9_sim;
    private String box10_sim;

    public FishpondBoxes() {

    }

    public FishpondBoxes(Map<String, Object> data){
        this.box1_sim = (String) data.get("box1_sim");
        this.box2_sim = (String) data.get("box2_sim");
        this.box3_sim = (String) data.get("box3_sim");
        this.box4_sim = (String) data.get("box4_sim");
        this.box5_sim = (String) data.get("box5_sim");
        this.box6_sim = (String) data.get("box6_sim");
        this.box7_sim = (String) data.get("box7_sim");
        this.box8_sim = (String) data.get("box8_sim");
        this.box9_sim = (String) data.get("box9_sim");
        this.box10_sim = (String) data.get("box10_sim");
    }

    public String getBox(int i) {
        switch(i) {
            case 0:
                return this.box1_sim;
            case 1:
                return this.box2_sim;
            case 2:
                return this.box3_sim;
            case 3:
                return this.box4_sim;
            case 4:
                return this.box5_sim;
            case 5:
                return this.box6_sim;
            case 6:
                return this.box7_sim;
            case 7:
                return this.box8_sim;
            case 8:
                return this.box9_sim;
            case 9:
                return this.box10_sim;
        }
        return null;
    }

    public String getBox1_sim() {
        return box1_sim;
    }

    public void setBox1_sim(String box1_sim) {
        this.box1_sim = box1_sim;
    }


    public String getBox2_sim() {
        return box2_sim;
    }

    public void setBox2_sim(String box2_sim) {
        this.box2_sim = box2_sim;
    }

    public String getBox3_sim() {
        return box3_sim;
    }

    public void setBox3_sim(String box3_sim) {
        this.box3_sim = box3_sim;
    }

    public String getBox4_sim() {
        return box4_sim;
    }

    public void setBox4_sim(String box4_sim) {
        this.box4_sim = box4_sim;
    }

    public String getBox5_sim() {
        return box5_sim;
    }

    public void setBox5_sim(String box5_sim) {
        this.box5_sim = box5_sim;
    }

    public String getBox6_sim() {
        return box6_sim;
    }

    public void setBox6_sim(String box6_sim) {
        this.box6_sim = box6_sim;
    }

    public String getBox7_sim() {
        return box7_sim;
    }

    public void setBox7_sim(String box7_sim) {
        this.box7_sim = box7_sim;
    }

    public String getBox8_sim() {
        return box8_sim;
    }

    public void setBox8_sim(String box8_sim) {
        this.box8_sim = box8_sim;
    }

    public String getBox9_sim() {
        return box9_sim;
    }

    public void setBox9_sim(String box9_sim) {
        this.box9_sim = box9_sim;
    }

    public String getBox10_sim() {
        return box10_sim;
    }

    public void setBox10_sim(String box10_sim) {
        this.box10_sim = box10_sim;
    }


}
