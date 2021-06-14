package com.vacc.model;

public class FormRegisterPatientResponse {
    private String guid;
    private String hospital;
    private String firstTerm;
    private String secondTerm;

    public FormRegisterPatientResponse(String guid, String hospital, String firstTerm, String secondTerm) {
        this.guid = guid;
        this.hospital = hospital;
        this.firstTerm = firstTerm;
        this.secondTerm = secondTerm;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getFirstTerm() {
        return firstTerm;
    }

    public void setFirstTerm(String firstTerm) {
        this.firstTerm = firstTerm;
    }

    public String getSecondTerm() {
        return secondTerm;
    }

    public void setSecondTerm(String secondTerm) {
        this.secondTerm = secondTerm;
    }
}
