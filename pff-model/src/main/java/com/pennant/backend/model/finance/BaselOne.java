package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class BaselOne {

	private String agreementId;
	private String customerId;
	private BigDecimal regEmi;
	private BigDecimal excessmoney;
	private BigDecimal accruedInterest;
	private BigDecimal unEarnedInterest;
	private BigDecimal grossReceivable;
	private BigDecimal suspenseInterest;
	private String assetClassficationId;
	private String securitized;
	private Date exactNpaDate;
	private BigDecimal annualTurnover;
	private String exptype;
	private int tenure;

	public String getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getRegEmi() {
		return regEmi;
	}

	public void setRegEmi(BigDecimal regEmi) {
		this.regEmi = regEmi;
	}

	public BigDecimal getExcessmoney() {
		return excessmoney;
	}

	public void setExcessmoney(BigDecimal excessmoney) {
		this.excessmoney = excessmoney;
	}

	public BigDecimal getAccruedInterest() {
		return accruedInterest;
	}

	public void setAccruedInterest(BigDecimal accruedInterest) {
		this.accruedInterest = accruedInterest;
	}

	public BigDecimal getUnEarnedInterest() {
		return unEarnedInterest;
	}

	public void setUnEarnedInterest(BigDecimal unEarnedInterest) {
		this.unEarnedInterest = unEarnedInterest;
	}

	public BigDecimal getGrossReceivable() {
		return grossReceivable;
	}

	public void setGrossReceivable(BigDecimal grossReceivable) {
		this.grossReceivable = grossReceivable;
	}

	public BigDecimal getSuspenseInterest() {
		return suspenseInterest;
	}

	public void setSuspenseInterest(BigDecimal suspenseInterest) {
		this.suspenseInterest = suspenseInterest;
	}

	public String getAssetClassficationId() {
		return assetClassficationId;
	}

	public void setAssetClassficationId(String assetClassficationId) {
		this.assetClassficationId = assetClassficationId;
	}

	public String getSecuritized() {
		return securitized;
	}

	public void setSecuritized(String securitized) {
		this.securitized = securitized;
	}

	public Date getExactNpaDate() {
		return exactNpaDate;
	}

	public void setExactNpaDate(Date exactNpaDate) {
		this.exactNpaDate = exactNpaDate;
	}

	public BigDecimal getAnnualTurnover() {
		return annualTurnover;
	}

	public void setAnnualTurnover(BigDecimal annualTurnover) {
		this.annualTurnover = annualTurnover;
	}

	public String getExptype() {
		return exptype;
	}

	public void setExptype(String exptype) {
		this.exptype = exptype;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

}
