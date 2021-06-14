package com.vacc;

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
public class AttendVaccination {

    /**
     * This function listens at endpoint "/api/RemoveAttend". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/RemoveAttend
     * 2. curl {your host}/api/RemoveAttend?name=HTTP%20Query
     */
    @FunctionName("AttendVaccination")
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

        try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("SELECT GUID FROM hospitals WHERE username = ?;")) {
            statement.setString(1, patient.getHospitalName());
            ResultSet rs = statement.executeQuery();
            String GUID = null;
            while (rs.next()) {
                GUID = rs.getString("GUID");
            }
            patient.setHospitalGUID(GUID);
        } catch (SQLException e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
        }

        try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("UPDATE patients SET vaccination_dates = ?, hospital_GUID = ? WHERE GUID = ?;")) {

            statement.setString(1, patient.getVaccinationDates());
            statement.setString(2, patient.getHospitalGUID());
            statement.setString(3, patient.getGuid());
            statement.executeUpdate();
        } catch (SQLException e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("DONE").build();
    }
}
