package com.vacc;

import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.vacc.model.Term;

/**
 * Azure Functions with HTTP Trigger.
 */
public class FetchTerms {
    /**
     * This function listens at endpoint "/api/FetchTerms". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/FetchTerms
     * 2. curl {your host}/api/FetchTerms?name=HTTP%20Query
     */
    @FunctionName("FetchTerms")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @TableInput(name = "terms", tableName = "TimeSlots", partitionKey = "{hospital}", filter = "RowKey gt '{from}'", connection = "AzureWebJobsStorage") Term[] terms,
            final ExecutionContext context) {
        if (terms.length == 0) {
            return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(new Gson().toJson(terms)).build();
        }
    }
}
