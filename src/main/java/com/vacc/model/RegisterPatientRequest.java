package com.vacc.model;

public class RegisterPatientRequest {
    private String hospital;
    private String datetime;
    private String guid;

    public RegisterPatientRequest(String hospital, String datetime, String guid) {
        this.hospital = hospital;
        this.datetime = datetime;
        this.guid = guid;
    }

    public String getHospital() {
        return hospital;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getGuid() {
        return guid;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
