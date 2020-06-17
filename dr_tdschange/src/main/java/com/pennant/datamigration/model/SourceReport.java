package com.pennant.datamigration.model;

import java.io.*;
import java.math.*;

public class SourceReport implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private String lan_No;
    private long finID;
    private BigDecimal Total_Disbursed_Amount;
    private BigDecimal Total_UnDisbursed_Amount;
    private BigDecimal Loan_Amount;
    private BigDecimal Total_EMI_Amount;
    private BigDecimal Total_Interest_Amount;
    private BigDecimal Total_Principal_Amount;
    private BigDecimal EMI_Received_Amount;
    private BigDecimal Principal_Received_Amount;
    private BigDecimal Interest_Received_Amount;
    private BigDecimal EMI_Outstanding_Amount;
    private BigDecimal Principle_Outstanding;
    private BigDecimal Interest_Outstanding;
    private BigDecimal Tot_Pastdue_Amount;
    private BigDecimal Tot_Pastdue_Principal;
    private BigDecimal Tot_Pastdue_Interest;
    private BigDecimal Interest_Prev_Month;
    private BigDecimal Odc_Due;
    private BigDecimal Odc_Colln;
    private BigDecimal Odc_Tbc;
    
    public SourceReport() {
        this.Total_Disbursed_Amount = BigDecimal.ZERO;
        this.Total_UnDisbursed_Amount = BigDecimal.ZERO;
        this.Loan_Amount = BigDecimal.ZERO;
        this.Total_EMI_Amount = BigDecimal.ZERO;
        this.Total_Interest_Amount = BigDecimal.ZERO;
        this.Total_Principal_Amount = BigDecimal.ZERO;
        this.EMI_Received_Amount = BigDecimal.ZERO;
        this.Principal_Received_Amount = BigDecimal.ZERO;
        this.Interest_Received_Amount = BigDecimal.ZERO;
        this.EMI_Outstanding_Amount = BigDecimal.ZERO;
        this.Principle_Outstanding = BigDecimal.ZERO;
        this.Interest_Outstanding = BigDecimal.ZERO;
        this.Tot_Pastdue_Amount = BigDecimal.ZERO;
        this.Tot_Pastdue_Principal = BigDecimal.ZERO;
        this.Tot_Pastdue_Interest = BigDecimal.ZERO;
        this.Interest_Prev_Month = BigDecimal.ZERO;
        this.Odc_Due = BigDecimal.ZERO;
        this.Odc_Colln = BigDecimal.ZERO;
        this.Odc_Tbc = BigDecimal.ZERO;
    }
    
    public BigDecimal getTotal_Disbursed_Amount() {
        return this.Total_Disbursed_Amount;
    }
    
    public void setTotal_Disbursed_Amount(final BigDecimal total_Disbursed_Amount) {
        this.Total_Disbursed_Amount = total_Disbursed_Amount;
    }
    
    public BigDecimal getTotal_UnDisbursed_Amount() {
        return this.Total_UnDisbursed_Amount;
    }
    
    public void setTotal_UnDisbursed_Amount(final BigDecimal total_UnDisbursed_Amount) {
        this.Total_UnDisbursed_Amount = total_UnDisbursed_Amount;
    }
    
    public BigDecimal getLoan_Amount() {
        return this.Loan_Amount;
    }
    
    public void setLoan_Amount(final BigDecimal loan_Amount) {
        this.Loan_Amount = loan_Amount;
    }
    
    public BigDecimal getTotal_EMI_Amount() {
        return this.Total_EMI_Amount;
    }
    
    public void setTotal_EMI_Amount(final BigDecimal total_EMI_Amount) {
        this.Total_EMI_Amount = total_EMI_Amount;
    }
    
    public BigDecimal getTotal_Interest_Amount() {
        return this.Total_Interest_Amount;
    }
    
    public void setTotal_Interest_Amount(final BigDecimal total_Interest_Amount) {
        this.Total_Interest_Amount = total_Interest_Amount;
    }
    
    public BigDecimal getTotal_Principal_Amount() {
        return this.Total_Principal_Amount;
    }
    
    public void setTotal_Principal_Amount(final BigDecimal total_Principal_Amount) {
        this.Total_Principal_Amount = total_Principal_Amount;
    }
    
    public BigDecimal getEMI_Received_Amount() {
        return this.EMI_Received_Amount;
    }
    
    public void setEMI_Received_Amount(final BigDecimal eMI_Received_Amount) {
        this.EMI_Received_Amount = eMI_Received_Amount;
    }
    
    public BigDecimal getPrincipal_Received_Amount() {
        return this.Principal_Received_Amount;
    }
    
    public void setPrincipal_Received_Amount(final BigDecimal principal_Received_Amount) {
        this.Principal_Received_Amount = principal_Received_Amount;
    }
    
    public BigDecimal getInterest_Received_Amount() {
        return this.Interest_Received_Amount;
    }
    
    public void setInterest_Received_Amount(final BigDecimal interest_Received_Amount) {
        this.Interest_Received_Amount = interest_Received_Amount;
    }
    
    public BigDecimal getEMI_Outstanding_Amount() {
        return this.EMI_Outstanding_Amount;
    }
    
    public void setEMI_Outstanding_Amount(final BigDecimal eMI_Outstanding_Amount) {
        this.EMI_Outstanding_Amount = eMI_Outstanding_Amount;
    }
    
    public BigDecimal getPrinciple_Outstanding() {
        return this.Principle_Outstanding;
    }
    
    public void setPrinciple_Outstanding(final BigDecimal principle_Outstanding) {
        this.Principle_Outstanding = principle_Outstanding;
    }
    
    public BigDecimal getInterest_Outstanding() {
        return this.Interest_Outstanding;
    }
    
    public void setInterest_Outstanding(final BigDecimal interest_Outstanding) {
        this.Interest_Outstanding = interest_Outstanding;
    }
    
    public BigDecimal getTot_Pastdue_Amount() {
        return this.Tot_Pastdue_Amount;
    }
    
    public void setTot_Pastdue_Amount(final BigDecimal tot_Pastdue_Amount) {
        this.Tot_Pastdue_Amount = tot_Pastdue_Amount;
    }
    
    public BigDecimal getTot_Pastdue_Principal() {
        return this.Tot_Pastdue_Principal;
    }
    
    public void setTot_Pastdue_Principal(final BigDecimal tot_Pastdue_Principal) {
        this.Tot_Pastdue_Principal = tot_Pastdue_Principal;
    }
    
    public BigDecimal getTot_Pastdue_Interest() {
        return this.Tot_Pastdue_Interest;
    }
    
    public void setTot_Pastdue_Interest(final BigDecimal tot_Pastdue_Interest) {
        this.Tot_Pastdue_Interest = tot_Pastdue_Interest;
    }
    
    public BigDecimal getInterest_Prev_Month() {
        return this.Interest_Prev_Month;
    }
    
    public void setInterest_Prev_Month(final BigDecimal interest_Prev_Month) {
        this.Interest_Prev_Month = interest_Prev_Month;
    }
    
    public BigDecimal getOdc_Due() {
        return this.Odc_Due;
    }
    
    public void setOdc_Due(final BigDecimal odc_Due) {
        this.Odc_Due = odc_Due;
    }
    
    public BigDecimal getOdc_Colln() {
        return this.Odc_Colln;
    }
    
    public void setOdc_Colln(final BigDecimal odc_Colln) {
        this.Odc_Colln = odc_Colln;
    }
    
    public BigDecimal getOdc_Tbc() {
        return this.Odc_Tbc;
    }
    
    public void setOdc_Tbc(final BigDecimal odc_Tbc) {
        this.Odc_Tbc = odc_Tbc;
    }
    
    public String getLan_No() {
        return this.lan_No;
    }
    
    public void setLan_No(final String lan_No) {
        this.lan_No = lan_No;
    }
    
    public long getFinID() {
        return this.finID;
    }
    
    public void setFinID(final long finID) {
        this.finID = finID;
    }
}