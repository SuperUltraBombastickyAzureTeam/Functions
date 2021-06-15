package com.vacc.model;

public class FormPatient {
    private final String guid;
    private final String firstName;
    private final String surname;
    private final String birthDate;
    private final String residence;
    private final String phoneNumber;
    private final String mail;
    private final String comment;
    private final long insuranceNumber;

    public FormPatient(String guid, String firstName, String surname, String birthDate, String residence, String phoneNumber, String mail, String comment, long insuranceNumber) {
        this.guid = guid;
        this.firstName = firstName;
        this.surname = surname;
        this.birthDate = birthDate;
        this.residence = residence;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.comment = comment;
        this.insuranceNumber = insuranceNumber;
    }

    public String getGuid() {
        return guid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getResidence() {
        return residence;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMail() {
        return mail;
    }

    public String getComment() {
        return comment;
    }

    public long getInsuranceNumber() {
        return insuranceNumber;
    }
}