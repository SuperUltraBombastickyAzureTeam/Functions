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
import com.vacc.model.Hospital;
import com.vacc.util.SQLHelper;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HospitalLogin {

    /**
     * This function listens at endpoint "/api/HospitalLogin". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HospitalLogin
     * 2. curl {your host}/api/HospitalLogin?name=HTTP%20Query
     */
    @FunctionName("HospitalLogin")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Empty body").build();
        } else {
            try (PreparedStatement statement = SQLHelper.getConnection().prepareStatement("SELECT passwrd FROM hospitals WHERE username = ?;")) {
                Hospital hosp = new Gson().fromJson(request.getBody().orElse(null), Hospital.class);
                context.getLogger().info(hosp.toString());
                context.getLogger().info(hosp.getUsername());
                context.getLogger().info(hosp.getPasswrd());
                if (hosp.getPasswrd().isEmpty() || hosp.getUsername().isEmpty()) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Wrong username or password").build();
                }
                statement.setString(1, hosp.getUsername());
                ResultSet rs = statement.executeQuery();
                String passwrd = null;
                while (rs.next()) {
                    passwrd = rs.getString("passwrd");
                }
                if (passwrd.equals(hosp.getPasswrd())) {
                    context.getLogger().info("DONE");
                    return request.createResponseBuilder(HttpStatus.OK).body("Logged-in successfully").build();
                }
            } catch (SQLException e) {
                context.getLogger().severe(e.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("SQL ERROR").build();
            } catch (Exception ex) {
                context.getLogger().severe(ex.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Wrong username or password").build();
            }
        }
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Wrong username or password").build();
    }
}
