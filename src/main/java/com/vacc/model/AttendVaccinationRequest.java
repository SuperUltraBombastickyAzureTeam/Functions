package com.vacc.model;

public class AttendVaccinationRequest {
    private String guid;
    private String hospitalName;
    private String vaccinationDate;

    public AttendVaccinationRequest(String guid, String hospitalName, String vaccinationDate) {
        this.guid = guid;
        this.hospitalName = hospitalName;
        this.vaccinationDate = vaccinationDate;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(String vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }
}
