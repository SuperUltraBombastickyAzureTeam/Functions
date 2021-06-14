package com.vacc.model;

public class InsuranceGuidTuple {
    private String PartitionKey;
    private String RowKey;
    private String guid;

    public InsuranceGuidTuple(String partitionKey, String rowKey, String guid) {
        PartitionKey = partitionKey;
        RowKey = rowKey;
        this.guid = guid;
    }


    public String getPartitionKey() {
        return PartitionKey;
    }

    public String getRowKey() {
        return RowKey;
    }

    public String getGuid() {
        return guid;
    }
}
