package com.pennanttech.pff.commodity.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Commodity extends CommodityType {
	private static final long serialVersionUID = 1L;

	private String HSNCode;
	private BigDecimal currentValue;
	private long CommodityType;
	private String CommodityTypeCode;
	private boolean upload;
	private boolean alertsRequired;
	private String alertType;
	private String alertTypeName;
	private String alertToRoles;
	private String alertToRolesName;
	private Long userTemplate;
	private String userTemplateCode;
	private String userTemplateName;
	private Long customerTemplate;
	private String customerTemplateCode;
	private String customerTemplateName;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("UnitType");
		excludeFields.add("CommodityTypeCode");
		excludeFields.add("Id");
		excludeFields.add("upload");
		excludeFields.add("alertTypeName");
		excludeFields.add("alertToRolesName");
		excludeFields.add("userTemplateName");
		excludeFields.add("customerTemplateName");
		excludeFields.add("userTemplateCode");
		excludeFields.add("customerTemplateCode");
		return excludeFields;
	}

	public String getHSNCode() {
		return HSNCode;
	}

	public void setHSNCode(String hSNCode) {
		HSNCode = hSNCode;
	}

	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public long getCommodityType() {
		return CommodityType;
	}

	public void setCommodityType(long commodityType) {
		CommodityType = commodityType;
	}

	public String getCommodityTypeCode() {
		return CommodityTypeCode;
	}

	public void setCommodityTypeCode(String commodityTypeCode) {
		CommodityTypeCode = commodityTypeCode;
	}

	public boolean isUpload() {
		return upload;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
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

	public String getAlertTypeName() {
		return alertTypeName;
	}

	public void setAlertTypeName(String alertTypeName) {
		this.alertTypeName = alertTypeName;
	}

	public String getAlertToRoles() {
		return alertToRoles;
	}

	public void setAlertToRoles(String alertToRoles) {
		this.alertToRoles = alertToRoles;
	}

	public String getAlertToRolesName() {
		return alertToRolesName;
	}

	public void setAlertToRolesName(String alertToRolesName) {
		this.alertToRolesName = alertToRolesName;
	}

	public Long getUserTemplate() {
		return userTemplate;
	}

	public void setUserTemplate(Long userTemplate) {
		this.userTemplate = userTemplate;
	}

	public String getUserTemplateCode() {
		return userTemplateCode;
	}

	public void setUserTemplateCode(String userTemplateCode) {
		this.userTemplateCode = userTemplateCode;
	}

	public String getUserTemplateName() {
		return userTemplateName;
	}

	public void setUserTemplateName(String userTemplateName) {
		this.userTemplateName = userTemplateName;
	}

	public Long getCustomerTemplate() {
		return customerTemplate;
	}

	public void setCustomerTemplate(Long customerTemplate) {
		this.customerTemplate = customerTemplate;
	}

	public String getCustomerTemplateCode() {
		return customerTemplateCode;
	}

	public void setCustomerTemplateCode(String customerTemplateCode) {
		this.customerTemplateCode = customerTemplateCode;
	}

	public String getCustomerTemplateName() {
		return customerTemplateName;
	}

	public void setCustomerTemplateName(String customerTemplateName) {
		this.customerTemplateName = customerTemplateName;
	}

}
