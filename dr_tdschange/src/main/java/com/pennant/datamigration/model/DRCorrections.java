package com.pennant.datamigration.model;

import java.io.Serializable;

public class DRCorrections implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private String finReference;
    private int reasonCode;
    private boolean drRequired;
    private String issues;
    private String remarks;
    private int correctionSts;
    
    public DRCorrections() {
        this.drRequired = false;
        this.correctionSts = 0;
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public int getReasonCode() {
        return this.reasonCode;
    }
    
    public void setReasonCode(final int reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    public boolean isDrRequired() {
        return this.drRequired;
    }
    
    public void setDrRequired(final boolean drRequired) {
        this.drRequired = drRequired;
    }
    
    public String getIssues() {
        return this.issues;
    }
    
    public void setIssues(final String issues) {
        this.issues = issues;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public int getCorrectionSts() {
        return this.correctionSts;
    }
    
    public void setCorrectionSts(final int correctionSts) {
        this.correctionSts = correctionSts;
    }
}