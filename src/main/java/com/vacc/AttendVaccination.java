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
import com.vacc.model.AttendVaccinationRequest;
import com.vacc.model.Patient;
import com.vacc.util.SQLHelper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class AttendVaccination {

    /**
     * This function listens at endpoint "/api/AttendVaccination". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/AttendVaccination
     * 2. curl {your host}/api/RemoveAttend?name=HTTP%20Query
     */
    @FunctionName("AttendVaccination")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<AttendVaccinationRequest>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Incorrect body").build();
        }
        AttendVaccinationRequest vaccRequest = request.getBody().get();
        if (vaccRequest.getGuid().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("patient guid is missing").build();
        }

        String hospGUID = null;
        try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("SELECT GUID FROM hospitals WHERE username = ?;")) {
            statement.setString(1, vaccRequest.getHospitalName());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                hospGUID = rs.getString("GUID");
            }
        } catch (SQLException e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
        }
        String vaccinationDates = null;
        try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("SELECT vaccinationDates FROM patients WHERE GUID = ?;")) {
            statement.setString(1, vaccRequest.getGuid());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                vaccinationDates = rs.getString("vaccinationDates");
            }
        } catch (SQLException e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
        }

        if (hospGUID == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Hospital not found").build();
        }

        try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("UPDATE patients SET vaccination_dates = ?, hospital_GUID = ? WHERE GUID = ?;")) {
            if (vaccinationDates == null || vaccinationDates.isEmpty()) {
                statement.setString(1, vaccRequest.getVaccinationDate());
            } else {
                statement.setString(1, vaccinationDates + ";" + vaccRequest.getVaccinationDate());
            }
            statement.setString(2, hospGUID);
            statement.setString(3, vaccRequest.getGuid());
            statement.executeUpdate();
        } catch (SQLException e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("DONE").build();
    }
}
