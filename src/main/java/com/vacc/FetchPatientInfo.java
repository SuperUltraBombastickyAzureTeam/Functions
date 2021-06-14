package com.vacc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.vacc.model.Patient;
import com.vacc.util.SQLHelper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class FetchPatientInfo {

    /**
     * This function listens at endpoint "/api/FetchPatientInfo". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/FetchPatientInfo
     * 2. curl {your host}/api/FetchPatientInfo?name=HTTP%20Query
     */
    @FunctionName("FetchPatientInfo")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        Patient patient;
        try {
            patient = new Gson().fromJson(request.getBody().orElse(null), Patient.class);
            if (patient.getGuid().isEmpty()) {
                throw new RuntimeException("Json is not correct");
            }
        } catch (Exception e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error while parsing json").build();
        }
        Patient patientInfo = null;

        try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("SELECT * FROM patients WHERE GUID = ?;")) {
            statement.setString(1, patient.getGuid());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String guid = rs.getString("GUID");
                String firstName = rs.getString("first_name");
                String surname = rs.getString("surname");
                Date birthDate = rs.getDate("birth_date");
                String residence = rs.getString("residence");
                String phoneNumber = rs.getString("phone_number");
                String mail = rs.getString("mail");
                String comment = rs.getString("comment");
                long insuranceNumber = rs.getLong("insurance_number");
                String vaccinationDates = rs.getString("vaccination_dates");
                String hospitalGUID = rs.getString("hospital_GUID");
                patientInfo = new Patient(guid, firstName, surname, birthDate, residence, phoneNumber, mail, comment, insuranceNumber, vaccinationDates, hospitalGUID);
            }
        } catch (SQLException e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
        }
        String json = new Gson().toJson(patientInfo);
        if (json.equals("")){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Null returned").build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(json).build();
    }
}
