package com.vacc;

public class HospitalRow {
    private final String code;
    private final String name;

    public HospitalRow(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
