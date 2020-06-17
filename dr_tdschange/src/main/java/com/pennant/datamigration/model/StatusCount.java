package com.pennant.datamigration.model;

public class StatusCount
{
    private String code;
    private String status;
    private int count;
    
    public StatusCount() {
        this.code = "";
        this.status = "";
        this.count = 0;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(final String code) {
        this.code = code;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
}