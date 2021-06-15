package com.vacc;

import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.vacc.model.FormRegisterPatientResponse;
import com.vacc.model.InsuranceGuidTuple;
import com.vacc.model.FormPatient;
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
            @TableInput(name = "tuple", tableName = "UniqueInsurances", partitionKey = "unique", rowKey = "{insuranceNumber}", connection = "AzureWebJobsStorage") InsuranceGuidTuple tuple,
            @ServiceBusQueueOutput(name="queue", queueName = "service-bus-queue", connection = "queueconnection") OutputBinding<String> queue,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String body = request.getBody().orElse(null);
        FormPatient patient = new Gson().fromJson(body, FormPatient.class);
        if (tuple != null) {
            context.getLogger().info("EXECUTED!!!");
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
        queue.setValue(body);
        return request.createResponseBuilder(HttpStatus.OK)
                .body(new Gson().toJson(new FormRegisterPatientResponse(patient.getGuid(),null, null, null)))
                .build();
    }
}
