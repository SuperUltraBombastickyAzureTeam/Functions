package com.vacc;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.*;
import com.vacc.model.RegisterPatientRequest;
import com.vacc.model.TermTableEntity;
import com.vacc.util.RetryException;

/**
 * Azure Functions with HTTP Trigger.
 */
public class TermRegisterPatient {
    /**
     * This function listens at endpoint "/api/TermRegisterPatient". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/TermRegisterPatient
     * 2. curl {your host}/api/TermRegisterPatient?name=HTTP%20Query
     */
    @FunctionName("TermRegisterPatient")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<RegisterPatientRequest>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Could not parse body").build();
        }
        CloudTable table = getCloudTable();

        HttpResponseMessage message = withRetries(() -> register(context, request, table, request.getBody().get()), 50, context);
        if (message == null) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Maximum amount of retries reached").build();
        }
        return message;
    }

    private HttpResponseMessage register(ExecutionContext context,
                                         HttpRequestMessage<Optional<RegisterPatientRequest>> httpRequestMessage,
                                         CloudTable table,
                                         RegisterPatientRequest request) throws RetryException {
        try {
            TableOperation select = TableOperation.retrieve(request.getHospital(), request.getDatetime(), TermTableEntity.class);
            TermTableEntity preTerm = table.execute(select).getResultAsType();
            if (preTerm == null) {
                return httpRequestMessage.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Could not find term with of this hospital and datetime")
                        .build();
            }
            if (preTerm.getCurrent() >= preTerm.getMax()) {
                return httpRequestMessage.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Maximum amount of registrations reached")
                        .build();
            }
            preTerm.setCurrent(preTerm.getCurrent() + 1);
            if (preTerm.getRegistered() == null || preTerm.getRegistered().isEmpty()) {
                preTerm.setRegistered(request.getGuid());
            } else {
                if (preTerm.getRegistered().contains(request.getGuid())) {
                    return httpRequestMessage.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("Patient with guid: " + request.getGuid() + "is already registered")
                            .build();
                }
                preTerm.setRegistered(preTerm.getRegistered().concat(";" + request.getGuid()));
            }
            TableOperation update = TableOperation.merge(preTerm);
            TermTableEntity updateResult = table.execute(update).getResultAsType();
            return httpRequestMessage.createResponseBuilder(HttpStatus.OK).body(new Gson().toJson(updateResult)).build();
        } catch (TableServiceException e) {
            if (e.getHttpStatusCode() == 412 && e.getErrorCode().equals("UpdateConditionNotSatisfied")) {
                throw new RetryException("Concurrent update detected", e);
            }
            return httpRequestMessage.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        } catch (StorageException e) {
            return httpRequestMessage.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        }
    }

    private CloudTable getCloudTable() {
        CloudTable table;
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(System.getenv("AzureWebJobsStorage"));
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
            table = tableClient.getTableReference("TimeSlots");
        } catch (URISyntaxException | InvalidKeyException | StorageException e) {
            //throw 500
            throw new RuntimeException(e);
        }
        return table;
    }

    private <T> T withRetries(SupplierWithRetries<T> method, int retryNumber, ExecutionContext context) {
        int numOfRetries = 0;
        Random r = new Random();
        while (numOfRetries < retryNumber) {
            try {
                return method.get();
            } catch (RetryException e) {
                context.getLogger().info(e.getMessage() +
                        " Retrying. (" + numOfRetries + " ouf of " + retryNumber + ")");
                numOfRetries++;
                try {
                    // wait between 50-100ms
                    Thread.sleep(r.nextInt(50)+50);
                } catch (InterruptedException interruptedException) {
                    //shouldn't happen
                    interruptedException.printStackTrace();
                }

            }
        }
        return null;
    }

    @FunctionalInterface
    public interface SupplierWithRetries<T> {
        T get() throws RetryException;
    }
}
