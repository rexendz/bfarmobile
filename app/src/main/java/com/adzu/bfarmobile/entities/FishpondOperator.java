package com.adzu.bfarmobile.entities;

public class FishpondOperator {

    private String firstname;
    private String middlename;
    private String lastname;
    private long operator_number;
    private long fla_number;
    private String fishpond_size;
    private String cityprovince;
    private String barangay;
    private String municipality;
    private java.util.Date issuance_date;
    private java.util.Date expiration_date;
    private String sim1;
    private String sim2;
    private boolean isActive;

    private FishpondRecord[] fishpondRecords;

    public FishpondRecord[] getFishpondRecords() {
        return fishpondRecords;
    }

    public void setFishpondRecords(FishpondRecord[] fishpondRecords) {
        this.fishpondRecords = fishpondRecords;
    }

    public String getSim1() {
        return sim1;
    }

    public void setSim1(String sim_number) {

        this.sim1 = sim_number;
    }

    public String getSim2() {
        return sim2;
    }

    public void setSim2(String sim_number) {

        this.sim2 = sim_number;
    }

    public boolean isIsActive() {
        java.util.Date now = new java.util.Date();
        return expiration_date.compareTo(now)>0;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getAddress(){
        return String.format("%s, %s, %s", barangay, municipality, cityprovince);
    }

    public String getFullName(){
        return firstname + " " +middlename+" "+lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public long getOperator_number() {
        return operator_number;
    }

    public void setOperator_number(long operator_number) {
        this.operator_number = operator_number;
    }

    public long getFla_number() {
        return fla_number;
    }

    public void setFla_number(long fla_number) {
        this.fla_number = fla_number;
    }

    public String getFishpond_size() {
        return fishpond_size;
    }

    public void setFishpond_size(String fishpond_size) {
        this.fishpond_size = fishpond_size;
    }

    public void setCityProvince(String cityprovince) {
        if(cityprovince==null||cityprovince.isEmpty()){
            this.cityprovince = "";
        }else{
            this.cityprovince = cityprovince;
        }
    }

    public void setMunicipality(String municipality) {
        if(municipality==null||municipality.isEmpty()){
            this.municipality = "";
        }else{
            this.municipality = municipality;
        }
    }

    public java.util.Date getIssuance_date() {
        return issuance_date;
    }

    public void setIssuance_date(java.util.Date issuance_date) {
        this.issuance_date = issuance_date;
    }

    public java.util.Date getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(java.util.Date expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getCityProvince() {
        return cityprovince;
    }

    public String getMunicipality() {
        return municipality;
    }

}
