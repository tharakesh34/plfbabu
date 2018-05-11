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
 * FileName    		:  ManualDeviation.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2018    														*
 *                                                                  						*
 * Modified Date    :  03-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2018       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ManualDeviation table</b>.<br>
 *
 */
public class ManualDeviation extends AbstractWorkflowEntity implements Entity {
	private static final long	serialVersionUID	= 1L;

	private long				deviationID			= Long.MIN_VALUE;
	private String				code;
	private String				description;
	private String				module;
	private String				moduleName;
	private long				categorization;
	private String				categorizationCode;
	private String				categorizationName;
	private long				severity;
	private boolean				active				= false;
	private boolean				newRecord			= false;
	private String				lovValue;
	private ManualDeviation		befImage;
	private LoggedInUser		userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public ManualDeviation() {
		super();
	}

	public ManualDeviation(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("moduleName");
		excludeFields.add("categorizationName");
		excludeFields.add("severityName");
		excludeFields.add("categorizationCode");
		excludeFields.add("severityCode");
		return excludeFields;
	}

	public long getId() {
		return deviationID;
	}

	public void setId(long id) {
		this.deviationID = id;
	}

	public long getDeviationID() {
		return deviationID;
	}

	public void setDeviationID(long deviationID) {
		this.deviationID = deviationID;
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public long getCategorization() {
		return categorization;
	}

	public void setCategorization(long categorization) {
		this.categorization = categorization;
	}

	public String getCategorizationName() {
		return this.categorizationName;
	}

	public void setCategorizationName(String categorizationName) {
		this.categorizationName = categorizationName;
	}

	public long getSeverity() {
		return severity;
	}

	public void setSeverity(long severity) {
		this.severity = severity;
	}


	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public ManualDeviation getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ManualDeviation beforeImage) {
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

	public String getCategorizationCode() {
		return categorizationCode;
	}

	public void setCategorizationCode(String categorizationCode) {
		this.categorizationCode = categorizationCode;
	}


}
