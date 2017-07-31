package com.pennanttech.bajaj.model.alm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ALM implements Serializable {
	private static final long serialVersionUID = 1L;

	long agreementId;
	String agreementNo;
	String productFlag;
	String npaStageId;
	BigDecimal installment;
	BigDecimal prinComp;
	BigDecimal intComp;
	Date dueDate;
	BigDecimal accruedAmt;
	Date accruedOn;
	BigDecimal cumulativeAccrualAmt;
	String advFlag;

	public long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(long agreementId) {
		this.agreementId = agreementId;
	}

	public String getAgreementNo() {
		return agreementNo;
	}

	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}

	public String getProductFlag() {
		return productFlag;
	}

	public void setProductFlag(String productFlag) {
		this.productFlag = productFlag;
	}

	public String getNpaStageId() {
		return npaStageId;
	}

	public void setNpaStageId(String npaStageId) {
		this.npaStageId = npaStageId;
	}

	public BigDecimal getInstallment() {
		return installment;
	}

	public void setInstallment(BigDecimal installment) {
		this.installment = installment;
	}

	public BigDecimal getPrinComp() {
		return prinComp;
	}

	public void setPrinComp(BigDecimal prinComp) {
		this.prinComp = prinComp;
	}

	public BigDecimal getIntComp() {
		return intComp;
	}

	public void setIntComp(BigDecimal intComp) {
		this.intComp = intComp;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getAccruedAmt() {
		return accruedAmt;
	}

	public void setAccruedAmt(BigDecimal accruedAmt) {
		this.accruedAmt = accruedAmt;
	}

	public Date getAccruedOn() {
		return accruedOn;
	}

	public void setAccruedOn(Date accruedOn) {
		this.accruedOn = accruedOn;
	}

	public BigDecimal getCumulativeAccrualAmt() {
		return cumulativeAccrualAmt;
	}

	public void setCumulativeAccrualAmt(BigDecimal cumulativeAccrualAmt) {
		this.cumulativeAccrualAmt = cumulativeAccrualAmt;
	}

	public String getAdvFlag() {
		return advFlag;
	}

	public void setAdvFlag(String advFlag) {
		this.advFlag = advFlag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
