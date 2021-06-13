package com.vacc;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

/**
 * Azure Functions with HTTP Trigger.
 */
public class SelectHospitalLocation {

    public static final String storageConnectionString = System.getenv("AzureWebJobsStorage");

    /**
     * This function listens at endpoint "/api/SelectHosp". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/SelectHosp
     * 2. curl {your host}/api/SelectHosp?name=HTTP%20Query
     */
    @FunctionName("SelectHosp")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        return request.createResponseBuilder(HttpStatus.OK).body(selectHospitals()).build();
    }

    // UNUSED
    public String listTables() {
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);

            // Create the table client.
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

            StringBuilder result = new StringBuilder();
            // Loop through the collection of table names.
            for (String table : tableClient.listTables()) {
                // Output each table name.
                result.append(table).append(", ");
            }
            return result.toString();
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
        return "";
    }

    public String selectHospitals() {
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);

            // Create the table client.
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

            // Create a cloud table object for the table.
            CloudTable cloudTable = tableClient.getTableReference("HospitalsList");

            // Specify a partition query, using "Smith" as the partition key filter.
            TableQuery<HospitalLocation> partitionQuery =
                    TableQuery.from(HospitalLocation.class);

            StringBuilder result = new StringBuilder();

            // Loop through the results, displaying information about the entity.
            for (HospitalLocation entity : cloudTable.execute(partitionQuery)) {
                result.append(entity.getPartitionKey() +
                                      ";" + entity.getRowKey() +
                                      ";" + entity.getInfo() + "\n");
            }
            return result.toString();
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
        return "";
    }
}
