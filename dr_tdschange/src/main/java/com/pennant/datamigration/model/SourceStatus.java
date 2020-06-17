package com.pennant.datamigration.model;

public class SourceStatus
{
    private String finReference;
    private String errors;
    private String warnings;
    private String information;
    
    public SourceStatus() {
        this.finReference = "";
        this.errors = "";
        this.warnings = "";
        this.information = "";
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public String getErrors() {
        return this.errors;
    }
    
    public void setErrors(final String errors) {
        this.errors = errors;
    }
    
    public String getWarnings() {
        return this.warnings;
    }
    
    public void setWarnings(final String warnings) {
        this.warnings = warnings;
    }
    
    public String getInformation() {
        return this.information;
    }
    
    public void setInformation(final String information) {
        this.information = information;
    }
}