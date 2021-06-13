package com.vacc;

import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.vacc.model.InsertTerm;
import com.vacc.model.InsertTermRequest;
import com.vacc.model.Term;

/**
 * Azure Functions with HTTP Trigger.
 */
public class InsertTerms {
    /**
     * This function listens at endpoint "/api/InsertTerms". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/InsertTerms
     * 2. curl {your host}/api/InsertTerms?name=HTTP%20Query
     */
    @FunctionName("InsertTerms")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @TableOutput(name = "outTerms", tableName = "TimeSlots", partitionKey = "{hospital}", connection = "AzureWebJobsStorage") OutputBinding<Term[]> outTerms,
            final ExecutionContext context) {

        InsertTermRequest termRequest = new Gson().fromJson(request.getBody().orElse(null), InsertTermRequest.class);
        if (termRequest == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
        }
        Term[] out = new Term[termRequest.getTerms().size()];
        for (int i = 0; i < termRequest.getTerms().size(); i++) {
            InsertTerm trm = termRequest.getTerms().get(i);
            out[i] = new Term(termRequest.getHospital(), trm.getDatetime(), 0, trm.getMax(), null);
        }
        outTerms.setValue(out);
        return request.createResponseBuilder(HttpStatus.OK).body(new Gson().toJson(out)).build();
    }
}
