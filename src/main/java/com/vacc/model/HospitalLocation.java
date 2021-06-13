package com.vacc.model;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableEntity;

import java.util.Date;
import java.util.HashMap;

public class HospitalLocation {
    private String PartitionKey;
    private String RowKey;
    private String Info;

    public HospitalLocation(String partitionKey, String rowKey, String Info) {
        this.PartitionKey = partitionKey;
        this.RowKey = rowKey;
        this.Info = Info;
    }

    public String getPartitionKey() {
        return PartitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        PartitionKey = partitionKey;
    }

    public String getRowKey() {
        return RowKey;
    }

    public void setRowKey(String rowKey) {
        RowKey = rowKey;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String Info) {
        this.Info = Info;
    }

    @Override
    public String toString() {
        return "HospitalLocation2{" +
                "PartitionKey='" + PartitionKey + '\'' +
                ", RowKey='" + RowKey + '\'' +
                ", Info='" + Info + '\'' +
                '}';
    }

}
