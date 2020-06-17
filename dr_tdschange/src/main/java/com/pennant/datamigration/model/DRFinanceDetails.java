package com.pennant.datamigration.model;

public class DRFinanceDetails
{
    private String finReference;
    private String Status;
    private long logKey;
    private int reconStatus;
    private String category;
    private boolean blockedNow;
    private boolean updRch;
    private boolean updRcd;
    private boolean updRph;
    private boolean updRpd;
    private boolean updRsd;
    private boolean updFsd;
    private boolean updRad;
    private boolean dltRch;
    private boolean dltRcd;
    private boolean dltRph;
    private boolean dltRpd;
    private boolean dltRsd;
    private boolean dltRad;
    private boolean newRph;
    private boolean newRpd;
    private boolean newRsd;
    private boolean newRad;
    private boolean updFea;
    private boolean newFea;
    private boolean dltFea;
    private boolean updPmd;
    private String remarks;
    
    public DRFinanceDetails() {
        this.reconStatus = 0;
        this.blockedNow = false;
        this.updRch = false;
        this.updRcd = false;
        this.updRph = false;
        this.updRpd = false;
        this.updRsd = false;
        this.updFsd = false;
        this.updRad = false;
        this.dltRch = false;
        this.dltRcd = false;
        this.dltRph = false;
        this.dltRpd = false;
        this.dltRsd = false;
        this.dltRad = false;
        this.newRph = false;
        this.newRpd = false;
        this.newRsd = false;
        this.newRad = false;
        this.updFea = false;
        this.newFea = false;
        this.dltFea = false;
        this.updPmd = false;
        this.remarks = "";
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String category) {
        this.category = category;
    }
    
    public String getStatus() {
        return this.Status;
    }
    
    public void setStatus(final String status) {
        this.Status = status;
    }
    
    public int getReconStatus() {
        return this.reconStatus;
    }
    
    public void setReconStatus(final int reconStatus) {
        this.reconStatus = reconStatus;
    }
    
    public boolean isBlockedNow() {
        return this.blockedNow;
    }
    
    public void setBlockedNow(final boolean blockedNow) {
        this.blockedNow = blockedNow;
    }
    
    public boolean isUpdRch() {
        return this.updRch;
    }
    
    public void setUpdRch(final boolean updRch) {
        this.updRch = updRch;
    }
    
    public boolean isUpdRcd() {
        return this.updRcd;
    }
    
    public void setUpdRcd(final boolean updRcd) {
        this.updRcd = updRcd;
    }
    
    public boolean isUpdRph() {
        return this.updRph;
    }
    
    public void setUpdRph(final boolean updRph) {
        this.updRph = updRph;
    }
    
    public boolean isUpdRpd() {
        return this.updRpd;
    }
    
    public void setUpdRpd(final boolean updRpd) {
        this.updRpd = updRpd;
    }
    
    public boolean isUpdRsd() {
        return this.updRsd;
    }
    
    public void setUpdRsd(final boolean updRsd) {
        this.updRsd = updRsd;
    }
    
    public boolean isUpdFsd() {
        return this.updFsd;
    }
    
    public void setUpdFsd(final boolean updFsd) {
        this.updFsd = updFsd;
    }
    
    public boolean isUpdRad() {
        return this.updRad;
    }
    
    public void setUpdRad(final boolean updRad) {
        this.updRad = updRad;
    }
    
    public boolean isDltRch() {
        return this.dltRch;
    }
    
    public void setDltRch(final boolean dltRch) {
        this.dltRch = dltRch;
    }
    
    public boolean isDltRcd() {
        return this.dltRcd;
    }
    
    public void setDltRcd(final boolean dltRcd) {
        this.dltRcd = dltRcd;
    }
    
    public boolean isDltRph() {
        return this.dltRph;
    }
    
    public void setDltRph(final boolean dltRph) {
        this.dltRph = dltRph;
    }
    
    public boolean isDltRpd() {
        return this.dltRpd;
    }
    
    public void setDltRpd(final boolean dltRpd) {
        this.dltRpd = dltRpd;
    }
    
    public boolean isDltRsd() {
        return this.dltRsd;
    }
    
    public void setDltRsd(final boolean dltRsd) {
        this.dltRsd = dltRsd;
    }
    
    public boolean isDltRad() {
        return this.dltRad;
    }
    
    public void setDltRad(final boolean dltRad) {
        this.dltRad = dltRad;
    }
    
    public boolean isNewRph() {
        return this.newRph;
    }
    
    public void setNewRph(final boolean newRph) {
        this.newRph = newRph;
    }
    
    public boolean isNewRpd() {
        return this.newRpd;
    }
    
    public void setNewRpd(final boolean newRpd) {
        this.newRpd = newRpd;
    }
    
    public boolean isNewRsd() {
        return this.newRsd;
    }
    
    public void setNewRsd(final boolean newRsd) {
        this.newRsd = newRsd;
    }
    
    public boolean isNewRad() {
        return this.newRad;
    }
    
    public void setNewRad(final boolean newRad) {
        this.newRad = newRad;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public boolean isUpdFea() {
        return this.updFea;
    }
    
    public void setUpdFea(final boolean updFea) {
        this.updFea = updFea;
    }
    
    public boolean isNewFea() {
        return this.newFea;
    }
    
    public void setNewFea(final boolean newFea) {
        this.newFea = newFea;
    }
    
    public boolean isDltFea() {
        return this.dltFea;
    }
    
    public void setDltFea(final boolean dltFea) {
        this.dltFea = dltFea;
    }
    
    public boolean isUpdPmd() {
        return this.updPmd;
    }
    
    public void setUpdPmd(final boolean updPmd) {
        this.updPmd = updPmd;
    }
    
    public long getLogKey() {
        return this.logKey;
    }
    
    public void setLogKey(final long logKey) {
        this.logKey = logKey;
    }
}