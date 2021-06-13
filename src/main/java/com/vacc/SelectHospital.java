package com.vacc;

import java.util.*;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class SelectHospital {
    /**
     * This function listens at endpoint "/api/SelectHospital". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/SelectHospital
     * 2. curl {your host}/api/SelectHospital?name=HTTP%20Query
     */
    @FunctionName("SelectHospital")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @TableInput(name = "hospitals", tableName = "HospitalsList",connection = "AzureWebJobsStorage") HospitalLocation[] hospitals,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        Map<String, List<HospitalRow>> map = new HashMap<>();
        for (HospitalLocation hospital : hospitals) {
            if(!map.containsKey(hospital.getPartitionKey())) {
                map.put(hospital.getPartitionKey(), new ArrayList<>());
            }
            map.get(hospital.getPartitionKey()).add(new HospitalRow(hospital.getRowKey(), hospital.getInfo()));
        }
        String body = new Gson().toJson(map);
        return request.createResponseBuilder(HttpStatus.OK).body(body).build();
    }
}
