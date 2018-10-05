package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class SourceReport implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private String lnno;
	private BigDecimal Total_Disbursed_Amount = BigDecimal.ZERO;
	private BigDecimal Total_UnDisbursed_Amount = BigDecimal.ZERO;
	private BigDecimal Loan_Amount = BigDecimal.ZERO;
	private BigDecimal Total_EMI_Amount = BigDecimal.ZERO;
	private BigDecimal Total_Interest_Amount = BigDecimal.ZERO;
	private BigDecimal Total_Principal_Amount = BigDecimal.ZERO;
	private BigDecimal EMI_Received_Amount = BigDecimal.ZERO;
	private BigDecimal Principal_Received_Amount = BigDecimal.ZERO;
	private BigDecimal Interest_Received_Amount = BigDecimal.ZERO;
	private BigDecimal EMI_Outstanding_Amount = BigDecimal.ZERO;
	private BigDecimal Principle_Outstanding = BigDecimal.ZERO;
	private BigDecimal Interest_Outstanding = BigDecimal.ZERO;
	private BigDecimal Tot_Pastdue_Amount = BigDecimal.ZERO;
	private BigDecimal Tot_Pastdue_Principal = BigDecimal.ZERO;
	private BigDecimal Tot_Pastdue_Interest = BigDecimal.ZERO;
	private BigDecimal Interest_Prev_Month = BigDecimal.ZERO;
	private BigDecimal Odc_Due = BigDecimal.ZERO;
	private BigDecimal Odc_Colln = BigDecimal.ZERO;
	private BigDecimal Odc_Tbc = BigDecimal.ZERO;
	
	public String getLnno() {
		return lnno;
	}
	public void setLnno(String lnno) {
		this.lnno = lnno;
	}
	public BigDecimal getTotal_Disbursed_Amount() {
		return Total_Disbursed_Amount;
	}
	public void setTotal_Disbursed_Amount(BigDecimal total_Disbursed_Amount) {
		Total_Disbursed_Amount = total_Disbursed_Amount;
	}
	public BigDecimal getTotal_UnDisbursed_Amount() {
		return Total_UnDisbursed_Amount;
	}
	public void setTotal_UnDisbursed_Amount(BigDecimal total_UnDisbursed_Amount) {
		Total_UnDisbursed_Amount = total_UnDisbursed_Amount;
	}
	public BigDecimal getLoan_Amount() {
		return Loan_Amount;
	}
	public void setLoan_Amount(BigDecimal loan_Amount) {
		Loan_Amount = loan_Amount;
	}
	public BigDecimal getTotal_EMI_Amount() {
		return Total_EMI_Amount;
	}
	public void setTotal_EMI_Amount(BigDecimal total_EMI_Amount) {
		Total_EMI_Amount = total_EMI_Amount;
	}
	public BigDecimal getTotal_Interest_Amount() {
		return Total_Interest_Amount;
	}
	public void setTotal_Interest_Amount(BigDecimal total_Interest_Amount) {
		Total_Interest_Amount = total_Interest_Amount;
	}
	public BigDecimal getTotal_Principal_Amount() {
		return Total_Principal_Amount;
	}
	public void setTotal_Principal_Amount(BigDecimal total_Principal_Amount) {
		Total_Principal_Amount = total_Principal_Amount;
	}
	public BigDecimal getEMI_Received_Amount() {
		return EMI_Received_Amount;
	}
	public void setEMI_Received_Amount(BigDecimal eMI_Received_Amount) {
		EMI_Received_Amount = eMI_Received_Amount;
	}
	public BigDecimal getPrincipal_Received_Amount() {
		return Principal_Received_Amount;
	}
	public void setPrincipal_Received_Amount(BigDecimal principal_Received_Amount) {
		Principal_Received_Amount = principal_Received_Amount;
	}
	public BigDecimal getInterest_Received_Amount() {
		return Interest_Received_Amount;
	}
	public void setInterest_Received_Amount(BigDecimal interest_Received_Amount) {
		Interest_Received_Amount = interest_Received_Amount;
	}
	public BigDecimal getEMI_Outstanding_Amount() {
		return EMI_Outstanding_Amount;
	}
	public void setEMI_Outstanding_Amount(BigDecimal eMI_Outstanding_Amount) {
		EMI_Outstanding_Amount = eMI_Outstanding_Amount;
	}
	public BigDecimal getPrinciple_Outstanding() {
		return Principle_Outstanding;
	}
	public void setPrinciple_Outstanding(BigDecimal principle_Outstanding) {
		Principle_Outstanding = principle_Outstanding;
	}
	public BigDecimal getInterest_Outstanding() {
		return Interest_Outstanding;
	}
	public void setInterest_Outstanding(BigDecimal interest_Outstanding) {
		Interest_Outstanding = interest_Outstanding;
	}
	public BigDecimal getTot_Pastdue_Amount() {
		return Tot_Pastdue_Amount;
	}
	public void setTot_Pastdue_Amount(BigDecimal tot_Pastdue_Amount) {
		Tot_Pastdue_Amount = tot_Pastdue_Amount;
	}
	public BigDecimal getTot_Pastdue_Principal() {
		return Tot_Pastdue_Principal;
	}
	public void setTot_Pastdue_Principal(BigDecimal tot_Pastdue_Principal) {
		Tot_Pastdue_Principal = tot_Pastdue_Principal;
	}
	public BigDecimal getTot_Pastdue_Interest() {
		return Tot_Pastdue_Interest;
	}
	public void setTot_Pastdue_Interest(BigDecimal tot_Pastdue_Interest) {
		Tot_Pastdue_Interest = tot_Pastdue_Interest;
	}
	public BigDecimal getInterest_Prev_Month() {
		return Interest_Prev_Month;
	}
	public void setInterest_Prev_Month(BigDecimal interest_Prev_Month) {
		Interest_Prev_Month = interest_Prev_Month;
	}
	public BigDecimal getOdc_Due() {
		return Odc_Due;
	}
	public void setOdc_Due(BigDecimal odc_Due) {
		Odc_Due = odc_Due;
	}
	public BigDecimal getOdc_Colln() {
		return Odc_Colln;
	}
	public void setOdc_Colln(BigDecimal odc_Colln) {
		Odc_Colln = odc_Colln;
	}
	public BigDecimal getOdc_Tbc() {
		return Odc_Tbc;
	}
	public void setOdc_Tbc(BigDecimal odc_Tbc) {
		Odc_Tbc = odc_Tbc;
	}

}
