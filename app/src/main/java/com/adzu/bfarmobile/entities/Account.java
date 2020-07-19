package com.adzu.bfarmobile.entities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Account{
    private String firstname;
    private String middlename;
    private String lastname;
    private String username;
    private String password;
    private boolean activated;
    private boolean admin;
    private boolean operator;
    private long FLA;
    private String sim1;
    private String sim2;
    private String activated_by;
    private String made_admin_by;
    private String removed_admin_by;

    public Account(){
        this.activated = false;
        this.admin = false;
        this.activated_by = "NONE";
        this.made_admin_by = "NONE";
        this.removed_admin_by = "NONE";
        this.FLA = -25565;
        this.sim1 = "NONE";
        this.sim2 = "NONE";
    }

    public Account(Map<String, Object> data){
        this.firstname = (String) data.get("firstname");
        this.middlename = (String) data.get("middlename");
        this.lastname = (String) data.get("lastname");
        this.username = (String) data.get("username");
        this.activated_by = (String) data.get("activated_by");
        this.made_admin_by = (String) data.get("made_admin_by");
        this.removed_admin_by = (String) data.get("removed_admin_by");
        this.password = (String) data.get("password");
        this.activated = (boolean) data.get("activated");
        this.admin = (boolean) data.get("admin");
        this.operator = (boolean) data.get("operator");
        this.sim1 = (String) data.get("sim1");
        this.sim2 = (String) data.get("sim2");
    }

    public boolean isOperator() {
        return operator;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setFLA(long FLA) {
        this.FLA = FLA;
    }

    public void setSim1(String sim1) {
        this.sim1 = sim1;
    }

    public void setSim2(String sim2) {
        this.sim2 = sim2;
    }

    public long getFLA() {
        return FLA;
    }

    public String getSim1() {
        return sim1;
    }

    public String getSim2() {
        return sim2;
    }

    public void setOperator(boolean operator){
        this.operator = operator;
    }

    public String getUsername() {
        return username;
    }

    public void setPasswordHashed(){
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, this.password.toCharArray());
        this.password = bcryptHashString;
    }

    public String getPassword() {
        return password;
    }

    public String getActivated_by() {
        return activated_by;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMade_admin_by() {
        return made_admin_by;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getRemoved_admin_by() {
        return removed_admin_by;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setActivated_by(String activated_by) {
        this.activated_by = activated_by;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMade_admin_by(String made_admin_by) {
        this.made_admin_by = made_admin_by;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemoved_admin_by(String removed_admin_by) {
        this.removed_admin_by = removed_admin_by;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
