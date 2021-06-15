package com.vacc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.vacc.model.FormPatient;
import com.vacc.model.FormRegisterPatientResponse;
import com.vacc.model.InsuranceGuidTuple;
import com.vacc.model.Patient;
import com.vacc.util.SQLHelper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class InsertPatientToDb {
    /**
     * This function listens at endpoint "/api/InsertPatientToDb". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/InsertPatientToDb
     * 2. curl {your host}/api/InsertPatientToDb?name=HTTP%20Query
     */
    @FunctionName("InsertPatientToDb")
    public void run(
            @ServiceBusQueueTrigger(name = "request", queueName = "service-bus-queue", connection = "queueconnection", access = AccessRights.LISTEN) String request,
            @TableInput(name = "tuple", tableName = "UniqueInsurances", partitionKey = "unique", rowKey = "{insuranceNumber}", connection = "AzureWebJobsStorage") InsuranceGuidTuple tuple,
            @TableOutput(name = "insurances", tableName = "UniqueInsurances", partitionKey = "unique", rowKey = "{insuranceNumber}", connection = "AzureWebJobsStorage") OutputBinding<InsuranceGuidTuple> insurancesOutput,
            final ExecutionContext context) {
        context.getLogger().info("Java Queue trigger processed a request.");
        context.getLogger().info("Patient: " + request);
        FormPatient patient = new Gson().fromJson(request, FormPatient.class);
        if (tuple != null) {
            return;
        }
        if (patient == null) {
            throw new RuntimeException("Patient could not be parsed.");
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
                insurancesOutput.setValue(new InsuranceGuidTuple("unique", String.valueOf(patient.getInsuranceNumber()), patient.getGuid()));
                context.getLogger().info("DONE");
            } catch (SQLException e) {
                context.getLogger().severe(e.getMessage());
                throw new RuntimeException("SQL error. " + e.getMessage());
            }
        }
    }
}
