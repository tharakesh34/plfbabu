/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  OptionType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-02-2019    														*
 *                                                                  						*
 * Modified Date    :  22-02-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-02-2019       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>OptionType table</b>.<br>
 *
 */
public class FinOptionType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String code;
	private String description;
	private String optionType;
	private String frequency;
	private String frequencyName;
	private int noticePeriodDays;
	private int alertDays;
	private String alertType;
	private String alertTypeName;
	private String alertToRoles;
	private String alertToRolesName;
	private Long userTemplate;
	private String userTemplateName;
	private Long customerTemplate;
	private String customerTemplateName;
	private String lovValue;
	private FinOptionType befImage;
	private LoggedInUser userDetails;
	private String userTemplateCode;
	private String customerTemplateCode;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinOptionType() {
		super();
	}

	public FinOptionType(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("frequencyName");
		excludeFields.add("alertTypeName");
		excludeFields.add("alertToRolesName");
		excludeFields.add("userTemplateName");
		excludeFields.add("customerTemplateName");
		excludeFields.add("userTemplateCode");
		excludeFields.add("customerTemplateCode");

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

	public String getOptionType() {
		return optionType;
	}

	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getFrequencyName() {
		return this.frequencyName;
	}

	public void setFrequencyName(String frequencyName) {
		this.frequencyName = frequencyName;
	}

	public int getNoticePeriodDays() {
		return noticePeriodDays;
	}

	public void setNoticePeriodDays(int noticePeriodDays) {
		this.noticePeriodDays = noticePeriodDays;
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

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinOptionType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinOptionType beforeImage) {
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

}
