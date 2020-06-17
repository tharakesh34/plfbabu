package com.pennant.datamigration.model;

import java.math.BigDecimal;

public class ExcessCorrections
{
    private String finReference;
    private long excessID;
    private BigDecimal oldExcessAmount;
    private BigDecimal oldExcessUtilized;
    private BigDecimal oldExcessReserved;
    private BigDecimal oldExcessBalance;
    private BigDecimal newExcessAmount;
    private BigDecimal newExcessUtilized;
    private BigDecimal newExcessBalance;
    private BigDecimal refundExcess;
    private BigDecimal oldRPHExcess;
    private BigDecimal feeExcess;
    private BigDecimal oldGLBal;
    private BigDecimal presentBounceExcess;
    private BigDecimal bpiExcess;
    private BigDecimal diffExcess;
    private boolean feaAdjust;
    private boolean rphAdjust;
    private boolean glAdjust;
    private boolean radAdjust;
    private long status;
    private String category;
    private BigDecimal ffrAllocated;
    
    public ExcessCorrections() {
        this.finReference = null;
        this.oldExcessAmount = BigDecimal.ZERO;
        this.oldExcessUtilized = BigDecimal.ZERO;
        this.oldExcessReserved = BigDecimal.ZERO;
        this.oldExcessBalance = BigDecimal.ZERO;
        this.newExcessAmount = BigDecimal.ZERO;
        this.newExcessUtilized = BigDecimal.ZERO;
        this.newExcessBalance = BigDecimal.ZERO;
        this.refundExcess = BigDecimal.ZERO;
        this.oldRPHExcess = BigDecimal.ZERO;
        this.feeExcess = BigDecimal.ZERO;
        this.oldGLBal = BigDecimal.ZERO;
        this.presentBounceExcess = BigDecimal.ZERO;
        this.bpiExcess = BigDecimal.ZERO;
        this.diffExcess = BigDecimal.ZERO;
        this.feaAdjust = false;
        this.rphAdjust = false;
        this.glAdjust = false;
        this.radAdjust = false;
        this.ffrAllocated = BigDecimal.ZERO;
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public long getExcessID() {
        return this.excessID;
    }
    
    public void setExcessID(final long excessID) {
        this.excessID = excessID;
    }
    
    public BigDecimal getOldExcessAmount() {
        return this.oldExcessAmount;
    }
    
    public void setOldExcessAmount(final BigDecimal oldExcessAmount) {
        this.oldExcessAmount = oldExcessAmount;
    }
    
    public BigDecimal getOldExcessUtilized() {
        return this.oldExcessUtilized;
    }
    
    public void setOldExcessUtilized(final BigDecimal oldExcessUtilized) {
        this.oldExcessUtilized = oldExcessUtilized;
    }
    
    public BigDecimal getOldExcessBalance() {
        return this.oldExcessBalance;
    }
    
    public void setOldExcessBalance(final BigDecimal oldExcessBalance) {
        this.oldExcessBalance = oldExcessBalance;
    }
    
    public BigDecimal getNewExcessAmount() {
        return this.newExcessAmount;
    }
    
    public void setNewExcessAmount(final BigDecimal newExcessAmount) {
        this.newExcessAmount = newExcessAmount;
    }
    
    public BigDecimal getNewExcessUtilized() {
        return this.newExcessUtilized;
    }
    
    public void setNewExcessUtilized(final BigDecimal newExcessUtilized) {
        this.newExcessUtilized = newExcessUtilized;
    }
    
    public BigDecimal getNewExcessBalance() {
        return this.newExcessBalance;
    }
    
    public void setNewExcessBalance(final BigDecimal newExcessBalance) {
        this.newExcessBalance = newExcessBalance;
    }
    
    public BigDecimal getOldRPHExcess() {
        return this.oldRPHExcess;
    }
    
    public void setOldRPHExcess(final BigDecimal oldRPHExcess) {
        this.oldRPHExcess = oldRPHExcess;
    }
    
    public BigDecimal getOldGLBal() {
        return this.oldGLBal;
    }
    
    public void setOldGLBal(final BigDecimal oldGLBal) {
        this.oldGLBal = oldGLBal;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String category) {
        this.category = category;
    }
    
    public BigDecimal getFeeExcess() {
        return this.feeExcess;
    }
    
    public void setFeeExcess(final BigDecimal feeExcess) {
        this.feeExcess = feeExcess;
    }
    
    public BigDecimal getFfrAllocated() {
        return this.ffrAllocated;
    }
    
    public void setFfrAllocated(final BigDecimal ffrAllocated) {
        this.ffrAllocated = ffrAllocated;
    }
    
    public BigDecimal getPresentBounceExcess() {
        return this.presentBounceExcess;
    }
    
    public void setPresentBounceExcess(final BigDecimal presentBounceExcess) {
        this.presentBounceExcess = presentBounceExcess;
    }
    
    public BigDecimal getBpiExcess() {
        return this.bpiExcess;
    }
    
    public void setBpiExcess(final BigDecimal bpiExcess) {
        this.bpiExcess = bpiExcess;
    }
    
    public long getStatus() {
        return this.status;
    }
    
    public void setStatus(final long status) {
        this.status = status;
    }
    
    public BigDecimal getDiffExcess() {
        return this.diffExcess;
    }
    
    public void setDiffExcess(final BigDecimal diffExcess) {
        this.diffExcess = diffExcess;
    }
    
    public BigDecimal getRefundExcess() {
        return this.refundExcess;
    }
    
    public void setRefundExcess(final BigDecimal refundExcess) {
        this.refundExcess = refundExcess;
    }
    
    public boolean isFeaAdjust() {
        return this.feaAdjust;
    }
    
    public void setFeaAdjust(final boolean feaAdjust) {
        this.feaAdjust = feaAdjust;
    }
    
    public boolean isRphAdjust() {
        return this.rphAdjust;
    }
    
    public void setRphAdjust(final boolean rphAdjust) {
        this.rphAdjust = rphAdjust;
    }
    
    public boolean isGlAdjust() {
        return this.glAdjust;
    }
    
    public void setGlAdjust(final boolean glAdjust) {
        this.glAdjust = glAdjust;
    }
    
    public boolean isRadAdjust() {
        return this.radAdjust;
    }
    
    public void setRadAdjust(final boolean radAdjust) {
        this.radAdjust = radAdjust;
    }
    
    public BigDecimal getOldExcessReserved() {
        return this.oldExcessReserved;
    }
    
    public void setOldExcessReserved(final BigDecimal oldExcessReserved) {
        this.oldExcessReserved = oldExcessReserved;
    }
}