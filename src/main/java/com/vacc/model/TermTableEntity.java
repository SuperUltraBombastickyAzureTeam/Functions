package com.vacc.model;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class TermTableEntity extends TableServiceEntity {
    private int Current;
    private int Max;
    private String Registered;

    public TermTableEntity(String partitionKey, String rowKey) {
        this.partitionKey = partitionKey;
        this.rowKey = rowKey;
    }

    public TermTableEntity() {
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public String getRowKey() {
        return this.rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getRegistered() {
        return Registered;
    }

    public void setRegistered(String Info) {
        this.Registered = Info;
    }

    public int getCurrent() {
        return Current;
    }

    public void setCurrent(int current) {
        this.Current = current;
    }

    public int getMax() {
        return Max;
    }

    public void setMax(int max) {
        this.Max = max;
    }

}
