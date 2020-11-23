/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.Objects;

import java.util.ArrayList;
import java.util.HashMap;

public class Patient {

    private String patientID;
    private String name;
    private String cholesterol;
    private String effectiveDateChol;
    private String effectiveDateBP;
    private String birthDate;
    private String gender;
    private String addressLine;
    private String city;
    private String postalCode;
    private String state;
    private String country;
    private String Systolic;
    private String Diastolic;

    private HashMap<Integer, ArrayList<String>> XLatestBP = new HashMap<>();

    public HashMap<Integer, ArrayList<String>> getXLatestBP() {
        return XLatestBP;
    }

//    public void setXLatestBP(HashMap<Integer, ArrayList<String>> XLatestBP) {
//        this.XLatestBP = XLatestBP;
//    }



    /**
     * Constructor for Patient Object
     * @param patientID Patient Identification
     * @param name Patient Name
     */
    public Patient(String patientID, String name){
        this.patientID = patientID;
        this.name = name;
    }

    /**
     *
     * @param patientID Patient Identification
     * @param name Patient Name
     * @param cholesterol Patient Cholesterol
     * @param Systolic Patient Systolic Pressure
     * @param Diastolic Patient Diastolic Pressure
     */
    public Patient(String patientID, String name, String cholesterol, String Systolic, String Diastolic){
        this.patientID = patientID;
        this.name = name;
        this.cholesterol = cholesterol;
        this.Systolic = Systolic;
        this.Diastolic = Diastolic;
    }

    /*
    Below are the Accessors and Mutators required to update or get an item from a Patient Object
     */

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public void setEffectiveDateChol(String effectiveDateChol) {
        this.effectiveDateChol = effectiveDateChol;
    }

    public String getEffectiveDateChol() {
        return effectiveDateChol;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getSystolic() {
        return Systolic;
    }

    public void setSystolic(String systolic) {
        Systolic = systolic;
    }

    public String getDiastolic() {
        return Diastolic;
    }

    public void setDiastolic(String diastolic) {
        Diastolic = diastolic;
    }

    public void setEffectiveDateBP(String effectiveDateBP) {
        this.effectiveDateBP = effectiveDateBP;
    }

    public String getEffectiveDateBP() {
        return effectiveDateBP;
    }


    public void setXLatestBP(int key, String effectiveDate, String systolicBP) {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add(effectiveDate);
        tempList.add(systolicBP);
        XLatestBP.put(key, tempList);
    }

    public ArrayList<String> getXLatestBP(int key) {
        return XLatestBP.get(key);
    }


    public String getFormattedXLatestBP(int key) {
        ArrayList<String> XLatest = XLatestBP.get(key);
        String formattedBP = XLatest.get(1) + " (" + XLatest.get(0) + ")";
        return formattedBP;
    }



    public void setExtraDetails(String birthDate, String gender, String addressLine, String city,
                                String postalCode, String state, String country) {
        this.birthDate = birthDate;
        this.gender = gender;
        this.addressLine = addressLine;
        this.city = city;
        this.postalCode = postalCode;
        this.state = state;
        this.country = country;
    }


}



