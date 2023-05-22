/**
 * Copyright 2011 - Pennant Technologies
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
 * * FileName : LimitStructure.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified Date
 * : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.limit;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Model class for the <b>LimitStructure table</b>.<br>
 *
 */
@XmlType(propOrder = { "structureCode", "structureName", "active", "limitStructureDetailItemsList", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "limitStructure")
public class LimitStructure extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String structureCode;

	@XmlElement
	private String structureName;
	// private String type;
	// private String limitCheckFilter;
	private String limitCategory;
	// private String typeName;
	private String showLimitsIn;

	@XmlElement
	private boolean active;

	@XmlElement(name = "structureDetail")
	private List<LimitStructureDetail> limitStructureDetailItemsList;

	private long createdBy;
	private String createdUser;

	private Timestamp createdOn;
	private String lovValue;
	private LimitStructure befImage;
	private LoggedInUser userDetails;
	private boolean scheduled = false;

	@XmlElement
	private WSReturnStatus returnStatus;

	public LimitStructure() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("LimitStructure"));
	}

	public LimitStructure(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("typeName");
		excludeFields.add("createdUser");
		excludeFields.add("scheduled");
		excludeFields.add("type");
		excludeFields.add("returnStatus");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public String getId() {
		return structureCode;
	}

	public void setId(String id) {
		this.structureCode = id;
	}

	public String getStructureCode() {
		return structureCode;
	}

	public void setStructureCode(String structureCode) {
		this.structureCode = structureCode;
	}

	public String getStructureName() {
		return structureName;
	}

	public void setStructureName(String structureName) {
		this.structureName = structureName;
	}

	/*
	 * public String getType() { return type; } public void setType(String type) { this.type = type; }
	 * 
	 * public String getTypeName() { return this.typeName; }
	 * 
	 * public void setTypeName (String typeName) { this.typeName = typeName; }
	 */

	public long getCreatedBy() {
		return createdBy;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public XMLGregorianCalendar getCreatedDate() throws DatatypeConfigurationException {

		if (createdOn == null) {
			return null;
		}
		return DateUtil.getXMLDate(createdOn);
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LimitStructure getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitStructure beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<LimitStructureDetail> getLimitStructureDetailItemsList() {
		return limitStructureDetailItemsList;
	}

	public void setLimitStructureDetailItemsList(List<LimitStructureDetail> limitStructureDetailItemsList) {
		this.limitStructureDetailItemsList = limitStructureDetailItemsList;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLimitCategory() {
		return limitCategory;
	}

	public void setLimitCategory(String limitCategory) {
		this.limitCategory = limitCategory;
	}

	public String getShowLimitsIn() {
		return showLimitsIn;
	}

	public void setShowLimitsIn(String showLimitsIn) {
		this.showLimitsIn = showLimitsIn;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
	/*
	 * public String getLimitCheckFilter() { return limitCheckFilter; }
	 * 
	 * public void setLimitCheckFilter(String limitCheckFilter) { this.limitCheckFilter = limitCheckFilter; }
	 */
}
