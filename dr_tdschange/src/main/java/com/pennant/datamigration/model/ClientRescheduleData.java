package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ClientRescheduleData implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private String agreementNo;
	private Date transaction_Date;
	private BigDecimal bulk_Refund = BigDecimal.ZERO;
	private BigDecimal additional_Disbursement = BigDecimal.ZERO;
	private BigDecimal roi = BigDecimal.ZERO;
	private BigDecimal pso_At_Transaction = BigDecimal.ZERO;
	private BigDecimal closing_Pos_Post_Transaction = BigDecimal.ZERO;
	private BigDecimal gap_Interest = BigDecimal.ZERO;
	private Date repayment_Effective_Date;
	private String gap_Period;
	
	public String getAgreementNo() {
		return agreementNo;
	}
	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}
	public Date getTransaction_Date() {
		return transaction_Date;
	}
	public void setTransaction_Date(Date transaction_Date) {
		this.transaction_Date = transaction_Date;
	}
	public BigDecimal getBulk_Refund() {
		return bulk_Refund;
	}
	public void setBulk_Refund(BigDecimal bulk_Refund) {
		this.bulk_Refund = bulk_Refund;
	}
	public BigDecimal getAdditional_Disbursement() {
		return additional_Disbursement;
	}
	public void setAdditional_Disbursement(BigDecimal additional_Disbursement) {
		this.additional_Disbursement = additional_Disbursement;
	}
	public BigDecimal getRoi() {
		return roi;
	}
	public void setRoi(BigDecimal roi) {
		this.roi = roi;
	}
	public BigDecimal getPso_At_Transaction() {
		return pso_At_Transaction;
	}
	public void setPso_At_Transaction(BigDecimal pso_At_Transaction) {
		this.pso_At_Transaction = pso_At_Transaction;
	}
	public BigDecimal getClosing_Pos_Post_Transaction() {
		return closing_Pos_Post_Transaction;
	}
	public void setClosing_Pos_Post_Transaction(BigDecimal closing_Pos_Post_Transaction) {
		this.closing_Pos_Post_Transaction = closing_Pos_Post_Transaction;
	}
	public BigDecimal getGap_Interest() {
		return gap_Interest;
	}
	public void setGap_Interest(BigDecimal gap_Interest) {
		this.gap_Interest = gap_Interest;
	}
	public Date getRepayment_Effective_Date() {
		return repayment_Effective_Date;
	}
	public void setRepayment_Effective_Date(Date repayment_Effective_Date) {
		this.repayment_Effective_Date = repayment_Effective_Date;
	}
	public String getGap_Period() {
		return gap_Period;
	}
	public void setGap_Period(String gap_Period) {
		this.gap_Period = gap_Period;
	}

}
