package com.pennant.datamigration.model;

import java.util.*;
import java.math.*;

public class DRRateReviewScheduleChange
{
    private String finReference;
    private Date rvwDate;
    private Date recalDate;
    private BigDecimal oldRate;
    private BigDecimal newRate;
    private BigDecimal oldEMI;
    private BigDecimal newEMI;
    private BigDecimal oldLastEMI;
    private BigDecimal newLastEMI;
    private BigDecimal diffEMI;
    private BigDecimal diffLastEMI;
    private BigDecimal diffMaxExpected;
    private int noOfTerms;
    private boolean flexi;
    private BigDecimal oldInterest;
    private BigDecimal newInterest;
    private BigDecimal diffInterest;
    private String category;
    private int reconSts;
    private String remarks;
    private boolean blockedNow;
    
    public DRRateReviewScheduleChange() {
        this.oldRate = BigDecimal.ZERO;
        this.newRate = BigDecimal.ZERO;
        this.oldEMI = BigDecimal.ZERO;
        this.newEMI = BigDecimal.ZERO;
        this.oldLastEMI = BigDecimal.ZERO;
        this.newLastEMI = BigDecimal.ZERO;
        this.diffEMI = BigDecimal.ZERO;
        this.diffLastEMI = BigDecimal.ZERO;
        this.diffMaxExpected = BigDecimal.ZERO;
        this.flexi = false;
        this.oldInterest = BigDecimal.ZERO;
        this.newInterest = BigDecimal.ZERO;
        this.diffInterest = BigDecimal.ZERO;
        this.blockedNow = false;
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public Date getRvwDate() {
        return this.rvwDate;
    }
    
    public void setRvwDate(final Date rvwDate) {
        this.rvwDate = rvwDate;
    }
    
    public BigDecimal getOldRate() {
        return this.oldRate;
    }
    
    public void setOldRate(final BigDecimal oldRate) {
        this.oldRate = oldRate;
    }
    
    public BigDecimal getNewRate() {
        return this.newRate;
    }
    
    public void setNewRate(final BigDecimal newRate) {
        this.newRate = newRate;
    }
    
    public BigDecimal getOldEMI() {
        return this.oldEMI;
    }
    
    public void setOldEMI(final BigDecimal oldEMI) {
        this.oldEMI = oldEMI;
    }
    
    public BigDecimal getNewEMI() {
        return this.newEMI;
    }
    
    public void setNewEMI(final BigDecimal newEMI) {
        this.newEMI = newEMI;
    }
    
    public BigDecimal getOldLastEMI() {
        return this.oldLastEMI;
    }
    
    public void setOldLastEMI(final BigDecimal oldLastEMI) {
        this.oldLastEMI = oldLastEMI;
    }
    
    public BigDecimal getNewLastEMI() {
        return this.newLastEMI;
    }
    
    public void setNewLastEMI(final BigDecimal newLastEMI) {
        this.newLastEMI = newLastEMI;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public BigDecimal getDiffMaxExpected() {
        return this.diffMaxExpected;
    }
    
    public void setDiffMaxExpected(final BigDecimal diffMaxExpected) {
        this.diffMaxExpected = diffMaxExpected;
    }
    
    public int getReconSts() {
        return this.reconSts;
    }
    
    public void setReconSts(final int reconSts) {
        this.reconSts = reconSts;
    }
    
    public BigDecimal getDiffEMI() {
        return this.diffEMI;
    }
    
    public void setDiffEMI(final BigDecimal diffEMI) {
        this.diffEMI = diffEMI;
    }
    
    public BigDecimal getDiffLastEMI() {
        return this.diffLastEMI;
    }
    
    public void setDiffLastEMI(final BigDecimal diffLastEMI) {
        this.diffLastEMI = diffLastEMI;
    }
    
    public int getNoOfTerms() {
        return this.noOfTerms;
    }
    
    public void setNoOfTerms(final int noOfTerms) {
        this.noOfTerms = noOfTerms;
    }
    
    public boolean isFlexi() {
        return this.flexi;
    }
    
    public void setFlexi(final boolean flexi) {
        this.flexi = flexi;
    }
    
    public Date getRecalDate() {
        return this.recalDate;
    }
    
    public void setRecalDate(final Date recalDate) {
        this.recalDate = recalDate;
    }
    
    public boolean isBlockedNow() {
        return this.blockedNow;
    }
    
    public void setBlockedNow(final boolean blockedNow) {
        this.blockedNow = blockedNow;
    }
    
    public BigDecimal getOldInterest() {
        return this.oldInterest;
    }
    
    public void setOldInterest(final BigDecimal oldInterest) {
        this.oldInterest = oldInterest;
    }
    
    public BigDecimal getNewInterest() {
        return this.newInterest;
    }
    
    public void setNewInterest(final BigDecimal newInterest) {
        this.newInterest = newInterest;
    }
    
    public BigDecimal getDiffInterest() {
        return this.diffInterest;
    }
    
    public void setDiffInterest(final BigDecimal diffInterest) {
        this.diffInterest = diffInterest;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String category) {
        this.category = category;
    }
}