package com.vacc.model;

public class InsertTerm {
    private String datetime;

    private int max;

    public InsertTerm(String datetime, int max) {
        this.datetime = datetime;
        this.max = max;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
