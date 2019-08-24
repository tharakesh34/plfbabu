package com.pennanttech.pff.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OGLTransactionDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String applicantId;
	private String apRefNo;
	private String apLineDescr;
	private String apCompany;
	private String apLOB;
	private String apLocation;
	private String apCostCenter;
	private String apNaturalAc;
	private String apIc;
	private String apFuture1;
	private String apDrcrFlag;
	private BigDecimal apAmount = BigDecimal.ZERO;
	private String createdBy;
	private Date createdDate;
	private String currencyCd;
	private BigDecimal conversionRate = BigDecimal.ZERO;
	private String conversionType;
	private BigDecimal apTxnCurrencyAmount = BigDecimal.ZERO;
	private BigDecimal creditAmount = BigDecimal.ZERO;
	private BigDecimal debitAmount = BigDecimal.ZERO;
	private String product;

	public String getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}

	public String getApRefNo() {
		return apRefNo;
	}

	public void setApRefNo(String apRefNo) {
		this.apRefNo = apRefNo;
	}

	public String getApLineDescr() {
		return apLineDescr;
	}

	public void setApLineDescr(String apLineDescr) {
		this.apLineDescr = apLineDescr;
	}

	public String getApCompany() {
		return apCompany;
	}

	public void setApCompany(String apCompany) {
		this.apCompany = apCompany;
	}

	public String getApLOB() {
		return apLOB;
	}

	public void setApLOB(String apLOB) {
		this.apLOB = apLOB;
	}

	public String getApLocation() {
		return apLocation;
	}

	public void setApLocation(String apLocation) {
		this.apLocation = apLocation;
	}

	public String getApCostCenter() {
		return apCostCenter;
	}

	public void setApCostCenter(String apCostCenter) {
		this.apCostCenter = apCostCenter;
	}

	public String getApNaturalAc() {
		return apNaturalAc;
	}

	public void setApNaturalAc(String apNaturalAc) {
		this.apNaturalAc = apNaturalAc;
	}

	public String getApIc() {
		return apIc;
	}

	public void setApIc(String apIc) {
		this.apIc = apIc;
	}

	public String getApFuture1() {
		return apFuture1;
	}

	public void setApFuture1(String apFuture1) {
		this.apFuture1 = apFuture1;
	}

	public String getApDrcrFlag() {
		return apDrcrFlag;
	}

	public void setApDrcrFlag(String apDrcrFlag) {
		this.apDrcrFlag = apDrcrFlag;
	}

	public BigDecimal getApAmount() {
		return apAmount;
	}

	public void setApAmount(BigDecimal apAmount) {
		this.apAmount = apAmount;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCurrencyCd() {
		return currencyCd;
	}

	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}

	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getConversionType() {
		return conversionType;
	}

	public void setConversionType(String conversionType) {
		this.conversionType = conversionType;
	}

	public BigDecimal getApTxnCurrencyAmount() {
		return apTxnCurrencyAmount;
	}

	public void setApTxnCurrencyAmount(BigDecimal apTxnCurrencyAmount) {
		this.apTxnCurrencyAmount = apTxnCurrencyAmount;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

}
