package com.vacc;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.vacc.model.FormRegisterPatientResponse;
import com.vacc.model.Hospital;
import com.vacc.model.InsuranceGuidTuple;
import com.vacc.model.Patient;
import com.vacc.util.SQLHelper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class FormRegisterPatient {
    /**
     * This function listens at endpoint "/api/FormRegisterPatient". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/FormRegisterPatient
     * 2. curl {your host}/api/FormRegisterPatient?name=HTTP%20Query
     */
    @FunctionName("FormRegisterPatient")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @TableInput(name= "tuple", tableName = "UniqueInsurances", partitionKey = "unique", rowKey = "{insuranceNumber}", connection = "AzureWebJobsStorage") InsuranceGuidTuple tuple,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String body = request.getBody().orElse(null);
        Patient patient = new Gson().fromJson(body, Patient.class);
        if (tuple != null) {
            try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("SELECT username, vaccination_dates FROM patients left join hospitals on (patients.hospital_GUID = hospitals.GUID) WHERE patients.GUID=?")) {
                statement.setString(1, tuple.getGuid());
                ResultSet set = statement.executeQuery();
                set.next();
                String hospital = set.getString("username");
                String dates = set.getString("vaccination_dates");
                String firstDate = null;
                String secondDate = null;
                if (dates != null && !dates.isEmpty()) {
                    if (dates.contains(";")) {
                        String[] s = dates.split(";");
                        firstDate = s[0];
                        secondDate = s[1];
                    } else {
                        firstDate = dates;
                    }
                }
                return request.createResponseBuilder(HttpStatus.OK).body(new Gson()
                        .toJson(new FormRegisterPatientResponse(tuple.getGuid(), hospital, firstDate, secondDate)))
                        .build();
            } catch (SQLException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getMessage()).build();
            }
        }
        if (body == null || patient == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            try(PreparedStatement statement = SQLHelper.getConnection().prepareStatement("INSERT INTO patients (GUID, first_name, surname, birth_date, residence, phone_number, mail, comment, vaccination_dates, insurance_number, hospital_GUID) values (?,?,?,?,?,?,?,?,?,?,?);")) {
                statement.setString(1, patient.getGuid());
                statement.setString(2, patient.getFirstName());
                //ENCRYPT?
                statement.setString(3, patient.getSurname());
                statement.setDate(4, Date.valueOf(patient.getBirthDate()));
                statement.setString(5, patient.getResidence());
                statement.setString(6, patient.getPhoneNumber());
                statement.setString(7, patient.getMail());
                statement.setString(8, patient.getComment());
                statement.setString(9, null);
                statement.setLong(10, patient.getInsuranceNumber());
                statement.setString(11, null);
                statement.executeUpdate();
                context.getLogger().info("DONE");
            } catch (SQLException e) {
                context.getLogger().severe(e.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body(new Gson().toJson(new FormRegisterPatientResponse(patient.getGuid(),null, null, null))).build();
        }
    }
}
