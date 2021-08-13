package com.pennanttech.pff.eod.collateral.reval.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CollateralRevaluation implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long batchId;
	private long finID;
	private String finReference;
	private String collateralType;
	private String collateralRef;
	private String collateralCCY;
	private BigDecimal marketValue;
	private BigDecimal unitPrice;
	private BigDecimal noOfUnits;
	private BigDecimal collateralValue;
	private BigDecimal currentCollateralValue;
	private BigDecimal bankLTV;
	private BigDecimal currentBankLTV;
	private BigDecimal bankValuation;
	private BigDecimal currentBankValuation;
	private BigDecimal thresholdLTV;
	private BigDecimal pos;
	private long commodityId;
	private Date valueDate;
	private boolean sendAlert;
	private boolean alertsRequired;
	private String alertType;
	private String alertToRoles;
	private String customerTemplateCode;
	private String userTemplateCode;
	private String tableName;
	private String hsnCode;
	private BigDecimal currentValue;
	private List<CollateralRevaluation> collHSNData;

	public CollateralRevaluation() {
		super();
	}

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

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getNoOfUnits() {
		return noOfUnits;
	}

	public void setNoOfUnits(BigDecimal noOfUnits) {
		this.noOfUnits = noOfUnits;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public BigDecimal getCurrentCollateralValue() {
		return currentCollateralValue;
	}

	public void setCurrentCollateralValue(BigDecimal currentCollateralValue) {
		this.currentCollateralValue = currentCollateralValue;
	}

	public BigDecimal getBankLTV() {
		return bankLTV;
	}

	public void setBankLTV(BigDecimal bankLTV) {
		this.bankLTV = bankLTV;
	}

	public BigDecimal getCurrentBankLTV() {
		return currentBankLTV;
	}

	public void setCurrentBankLTV(BigDecimal currentBankLTV) {
		this.currentBankLTV = currentBankLTV;
	}

	public BigDecimal getBankValuation() {
		return bankValuation;
	}

	public void setBankValuation(BigDecimal bankValuation) {
		this.bankValuation = bankValuation;
	}

	public BigDecimal getCurrentBankValuation() {
		return currentBankValuation;
	}

	public void setCurrentBankValuation(BigDecimal currentBankValuation) {
		this.currentBankValuation = currentBankValuation;
	}

	public BigDecimal getThresholdLTV() {
		return thresholdLTV;
	}

	public void setThresholdLTV(BigDecimal thresholdLTV) {
		this.thresholdLTV = thresholdLTV;
	}

	public BigDecimal getPos() {
		return pos;
	}

	public void setPos(BigDecimal pos) {
		this.pos = pos;
	}

	public long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(long commodityId) {
		this.commodityId = commodityId;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public boolean isSendAlert() {
		return sendAlert;
	}

	public void setSendAlert(boolean sendAlert) {
		this.sendAlert = sendAlert;
	}

	public boolean isAlertsRequired() {
		return alertsRequired;
	}

	public void setAlertsRequired(boolean alertsRequired) {
		this.alertsRequired = alertsRequired;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
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

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public List<CollateralRevaluation> getCollHSNData() {
		return collHSNData;
	}

	public void setCollHSNData(List<CollateralRevaluation> collHSNData) {
		this.collHSNData = collHSNData;
	}

}
