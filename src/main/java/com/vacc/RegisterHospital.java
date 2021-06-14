package com.vacc;

import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.vacc.model.Hospital;
import com.vacc.model.InsuranceGuidTuple;
import com.vacc.util.SQLHelper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class RegisterHospital {
    /**
     * This function listens at endpoint "/api/RegisterHospital". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/RegisterHospital
     * 2. curl {your host}/api/RegisterHospital?name=HTTP%20Query
     */
    @FunctionName("RegisterHospital")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String body = request.getBody().orElse(null);
        Hospital hospital = new Gson().fromJson(body, Hospital.class);
        if (body == null || hospital == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            try(PreparedStatement statement = SQLHelper.getConnection().prepareStatement("INSERT INTO hospitals (GUID, username, passwrd, comment) values (?,?,?,?);")) {
                statement.setString(1, hospital.getGUID());
                statement.setString(2, hospital.getUsername());
                //ENCRYPT?
                statement.setString(3, hospital.getPasswrd());
                statement.setString(4, hospital.getComment());
                statement.executeUpdate();
                context.getLogger().info("DONE");
            } catch (SQLException e) {
                context.getLogger().severe(e.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
            }
            return request.createResponseBuilder(HttpStatus.OK).build();
        }
    }
}
