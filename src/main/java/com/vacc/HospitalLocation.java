package com.vacc;

import java.util.Date;
import java.util.HashMap;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableEntity;

public class HospitalLocation implements TableEntity {

    private String PartitionKey;
    private String RowKey;
    private String Info;

    public HospitalLocation() {
    }

    public HospitalLocation(String partitionKey, String rowKey, String Info) {
        this.PartitionKey = partitionKey;
        this.RowKey = rowKey;
        this.Info = Info;
    }


    @Override
    public String getEtag() {
        return null;
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

    @Override
    public Date getTimestamp() {
        return null;
    }

    @Override
    public void readEntity(HashMap<String, EntityProperty> properties, OperationContext opContext) throws StorageException {

    }

    @Override
    public void setEtag(String etag) {

    }

    public void setRowKey(String rowKey) {
        RowKey = rowKey;
    }

    @Override
    public void setTimestamp(Date timeStamp) {

    }

    @Override
    public HashMap<String, EntityProperty> writeEntity(OperationContext opContext) throws StorageException {
        return null;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String Info) {
        this.Info = Info;
    }
}
