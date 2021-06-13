package com.vacc.model;

public class Term {
    private String PartitionKey;
    private String RowKey;
    private int Current;
    private int Max;
    private String Registered;

    public Term(String partitionKey, String rowKey, int current, int max, String registered) {
        this.PartitionKey = partitionKey;
        this.RowKey = rowKey;
        this.Current = current;
        this.Max = max;
        this.Registered = registered;
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
