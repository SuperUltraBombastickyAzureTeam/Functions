package com.vacc.model;

import java.util.List;

public class InsertTermRequest {
    private String hospital;
    private List<InsertTerm> terms;

    public InsertTermRequest(String hospital, List<InsertTerm> terms) {
        this.hospital = hospital;
        this.terms = terms;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public List<InsertTerm> getTerms() {
        return terms;
    }

    public void setTerms(List<InsertTerm> terms) {
        this.terms = terms;
    }
}
