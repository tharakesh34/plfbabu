package com.pennant.backend.model.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class AssignmentDetails implements Serializable {
	private static final long serialVersionUID = 8987922026116401165L;

	private String module;
	private String reference;
	private String currency;
	private BigDecimal assignedPerc;
	private BigDecimal collateralValue;
	private BigDecimal utilizedValue;
	private BigDecimal totalUtilized;
	private Date cmtExpDate;
	private boolean finIsActive;
	private BigDecimal finCurrAssetValue;
	private BigDecimal finAssetValue;
	private String finLTVCheck;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAssignedPerc() {
		return assignedPerc;
	}

	public void setAssignedPerc(BigDecimal assignedPerc) {
		this.assignedPerc = assignedPerc;
	}

	public BigDecimal getUtilizedValue() {
		return utilizedValue;
	}

	public void setUtilizedValue(BigDecimal utilizedValue) {
		this.utilizedValue = utilizedValue;
	}

	public Date getCmtExpDate() {
		return cmtExpDate;
	}

	public void setCmtExpDate(Date cmtExpDate) {
		this.cmtExpDate = cmtExpDate;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public BigDecimal getTotalUtilized() {
		return totalUtilized;
	}

	public void setTotalUtilized(BigDecimal totalUtilized) {
		this.totalUtilized = totalUtilized;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public String getFinLTVCheck() {
		return finLTVCheck;
	}

	public void setFinLTVCheck(String finLTVCheck) {
		this.finLTVCheck = finLTVCheck;
	}

}
