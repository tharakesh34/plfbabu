package com.pennanttech.pff.eod.collateral.reval.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CollateralRevaluation implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long batchId;
	private String finReference;
	private String collateralType;
	private String collateralRef;
	private String collateralCCY;
	private BigDecimal collateralValue;
	private BigDecimal marketValue;
	private BigDecimal bankLTV;
	private BigDecimal thresholdLTV;
	private BigDecimal marketLTV;
	private BigDecimal bankValuation;
	private int commodityId;
	private int units;
	private BigDecimal pos;
	private BigDecimal valueDate;
	private BigDecimal sendAlert;
	private String alertToRoles;
	private String customerTemplateCode;
	private String userTemplateCode;

	private String tableName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public String getCollateralCCY() {
		return collateralCCY;
	}

	public void setCollateralCCY(String collateralCCY) {
		this.collateralCCY = collateralCCY;
	}

	public BigDecimal getPos() {
		return pos;
	}

	public void setPos(BigDecimal pos) {
		this.pos = pos;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getBankLTV() {
		return bankLTV;
	}

	public void setBankLTV(BigDecimal bankLTV) {
		this.bankLTV = bankLTV;
	}

	public BigDecimal getThresholdLTV() {
		return thresholdLTV;
	}

	public void setThresholdLTV(BigDecimal thresholdLTV) {
		this.thresholdLTV = thresholdLTV;
	}

	public BigDecimal getMarketLTV() {
		return marketLTV;
	}

	public void setMarketLTV(BigDecimal marketLTV) {
		this.marketLTV = marketLTV;
	}

	public BigDecimal getBankValuation() {
		return bankValuation;
	}

	public void setBankValuation(BigDecimal bankValuation) {
		this.bankValuation = bankValuation;
	}

	public int getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(int commodityId) {
		this.commodityId = commodityId;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public BigDecimal getValueDate() {
		return valueDate;
	}

	public void setValueDate(BigDecimal valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getSendAlert() {
		return sendAlert;
	}

	public void setSendAlert(BigDecimal sendAlert) {
		this.sendAlert = sendAlert;
	}

	public String getAlertToRoles() {
		return alertToRoles;
	}

	public void setAlertToRoles(String alertToRoles) {
		this.alertToRoles = alertToRoles;
	}

	public String getCustomerTemplateCode() {
		return customerTemplateCode;
	}

	public void setCustomerTemplateCode(String customerTemplateCode) {
		this.customerTemplateCode = customerTemplateCode;
	}

	public String getUserTemplateCode() {
		return userTemplateCode;
	}

	public void setUserTemplateCode(String userTemplateCode) {
		this.userTemplateCode = userTemplateCode;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
