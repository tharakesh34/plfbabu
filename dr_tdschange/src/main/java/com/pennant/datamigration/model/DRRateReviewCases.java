package com.pennant.datamigration.model;

import java.util.*;

public class DRRateReviewCases
{
    private String finReference;
    private Date startDate;
    private int orgTerms;
    private Date orgMDT;
    private Date maxEndDate;
    private int newFMTerms;
    private int newFSDTerms;
    private Date newFMMDT;
    private Date newFSDMDT;
    private int reconStatus;
    private String statusRemarks;
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }
    
    public int getOrgTerms() {
        return this.orgTerms;
    }
    
    public void setOrgTerms(final int orgTerms) {
        this.orgTerms = orgTerms;
    }
    
    public Date getOrgMDT() {
        return this.orgMDT;
    }
    
    public void setOrgMDT(final Date orgMDT) {
        this.orgMDT = orgMDT;
    }
    
    public Date getMaxEndDate() {
        return this.maxEndDate;
    }
    
    public void setMaxEndDate(final Date maxEndDate) {
        this.maxEndDate = maxEndDate;
    }
    
    public int getNewFMTerms() {
        return this.newFMTerms;
    }
    
    public void setNewFMTerms(final int newFMTerms) {
        this.newFMTerms = newFMTerms;
    }
    
    public int getNewFSDTerms() {
        return this.newFSDTerms;
    }
    
    public void setNewFSDTerms(final int newFSDTerms) {
        this.newFSDTerms = newFSDTerms;
    }
    
    public Date getNewFMMDT() {
        return this.newFMMDT;
    }
    
    public void setNewFMMDT(final Date newFMMDT) {
        this.newFMMDT = newFMMDT;
    }
    
    public Date getNewFSDMDT() {
        return this.newFSDMDT;
    }
    
    public void setNewFSDMDT(final Date newFSDMDT) {
        this.newFSDMDT = newFSDMDT;
    }
    
    public int getReconStatus() {
        return this.reconStatus;
    }
    
    public void setReconStatus(final int reconStatus) {
        this.reconStatus = reconStatus;
    }
    
    public String getStatusRemarks() {
        return this.statusRemarks;
    }
    
    public void setStatusRemarks(final String statusRemarks) {
        this.statusRemarks = statusRemarks;
    }
}