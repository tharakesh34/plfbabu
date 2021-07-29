/**
 * - * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CovenantType.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-02-2019 * * Modified Date :
 * 06-02-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-02-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance.covenant;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CovenantType table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "covenantType")
public class CovenantType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	@XmlElement
	private String code;
	@XmlElement
	private String description;
	@XmlElement
	private String category;
	private String categoryName;
	@XmlElement
	private String docType;
	private String docTypeName;
	private boolean allowPostPonement;
	private int maxAllowedDays;
	private String allowedPaymentModes;
	private boolean alertsRequired;
	@XmlElement
	private String frequency;
	private int graceDays = 0;
	private int alertDays = 0;
	@XmlElement
	private String alertType;
	private String alertTypeName;
	private String alertToRoles;
	private String alertToRolesName;
	@XmlElement
	private Long userTemplate;
	private String userTemplateCode;
	private String userTemplateName;
	@XmlElement
	private Long customerTemplate;
	private String customerTemplateCode;
	private String customerTemplateName;
	private String lovValue;
	private CovenantType befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private String covenantType;
	@XmlElement(name = "alertsRequired")
	private String strAlertsRequired;
	@XmlElement(name = "graceDays")
	public Integer lGraceDays;
	@XmlElement(name = "alertDays")
	public Integer lAlertDays;
	@XmlElement(name = "allowPostPonement")
	private String strAllowPostPonement;

	public CovenantType() {
		super();
	}

	public CovenantType(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("categoryName");
		excludeFields.add("docTypeName");
		excludeFields.add("alertTypeName");
		excludeFields.add("alertToRolesName");
		excludeFields.add("userTemplateName");
		excludeFields.add("customerTemplateName");
		excludeFields.add("userTemplateCode");
		excludeFields.add("customerTemplateCode");
		excludeFields.add("AlertRoleCode");
		excludeFields.add("AlertRoleDesc");
		// excludeFields.add("los");
		// excludeFields.add("otc");
		// excludeFields.add("pdd");
		// excludeFields.add("CovenantTypeId");
		excludeFields.add("strAlertsRequired");
		excludeFields.add("lGraceDays");
		excludeFields.add("lAlertDays");
		excludeFields.add("strAllowPostPonement");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocTypeName() {
		return this.docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public boolean isAllowPostPonement() {
		return allowPostPonement;
	}

	public void setAllowPostPonement(boolean allowPostPonement) {
		this.allowPostPonement = allowPostPonement;
	}

	public int getMaxAllowedDays() {
		return maxAllowedDays;
	}

	public void setMaxAllowedDays(int maxAllowedDays) {
		this.maxAllowedDays = maxAllowedDays;
	}

	public String getAllowedPaymentModes() {
		return allowedPaymentModes;
	}

	public void setAllowedPaymentModes(String allowedPaymentModes) {
		this.allowedPaymentModes = allowedPaymentModes;
	}

	public boolean isAlertsRequired() {
		return alertsRequired;
	}

	public void setAlertsRequired(boolean alertsRequired) {
		this.alertsRequired = alertsRequired;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public int getGraceDays() {
		return graceDays;
	}

	public void setGraceDays(int graceDays) {
		this.graceDays = graceDays;
	}

	public int getAlertDays() {
		return alertDays;
	}

	public void setAlertDays(int alertDays) {
		this.alertDays = alertDays;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getAlertTypeName() {
		return this.alertTypeName;
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
		return this.alertToRolesName;
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

	public String getUserTemplateName() {
		return this.userTemplateName;
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

	public String getCustomerTemplateName() {
		return this.customerTemplateName;
	}

	public void setCustomerTemplateName(String customerTemplateName) {
		this.customerTemplateName = customerTemplateName;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CovenantType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CovenantType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getUserTemplateCode() {
		return userTemplateCode;
	}

	public void setUserTemplateCode(String userTemplateCode) {
		this.userTemplateCode = userTemplateCode;
	}

	public String getCustomerTemplateCode() {
		return customerTemplateCode;
	}

	public void setCustomerTemplateCode(String customerTemplateCode) {
		this.customerTemplateCode = customerTemplateCode;
	}

	public String getCovenantType() {
		return covenantType;
	}

	public void setCovenantType(String covenantType) {
		this.covenantType = covenantType;
	}

	public String getStrAlertsRequired() {
		return strAlertsRequired;
	}

	public void setStrAlertsRequired(String strAlertsRequired) {
		this.strAlertsRequired = strAlertsRequired;
	}

	public Integer getlGraceDays() {
		return lGraceDays;
	}

	public void setlGraceDays(Integer lGraceDays) {
		this.lGraceDays = lGraceDays;
	}

	public Integer getlAlertDays() {
		return lAlertDays;
	}

	public void setlAlertDays(Integer lAlertDays) {
		this.lAlertDays = lAlertDays;
	}

	public String getStrAllowPostPonement() {
		return strAllowPostPonement;
	}

	public void setStrAllowPostPonement(String strAllowPostPonement) {
		this.strAllowPostPonement = strAllowPostPonement;
	}

}
