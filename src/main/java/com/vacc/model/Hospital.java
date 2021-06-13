package com.vacc.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Hospital {
    private String GUID;
    private String username;
    private String passwrd;
    private String comment;

    public Hospital(String guid, String username, String passwrd, String comment) {
        this.GUID = guid;
        this.username = username;
        this.passwrd = passwrd;
        this.comment = comment;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswrd() {
        return passwrd;
    }

    public void setPasswrd(String passwrd) {
        this.passwrd = passwrd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "guid='" + GUID + '\'' +
                ", username='" + username + '\'' +
                ", passwrd='" + passwrd + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public static Hospital readHospital(ResultSet resultSet) throws SQLException {
        String guid = resultSet.getString("GUID");
        String username = resultSet.getString("username");
        String passwrd = resultSet.getString("passwrd");
        String comment = resultSet.getString("comment");
        return new Hospital(guid, username, passwrd, comment);
    }
}
