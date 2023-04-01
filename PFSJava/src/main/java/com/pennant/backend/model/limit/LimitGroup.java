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
 * * FileName : LimitGroup.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified Date :
 * 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.limit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Model class for the <b>LimitGroup table</b>.<br>
 * 
 */
public class LimitGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String groupCode;
	private String groupName;
	private boolean active;
	private String groupOf;
	private String limitCategory;

	private long createdBy;
	private Timestamp createdOn;
	private String lovValue;
	private LimitGroup befImage;
	private LoggedInUser userDetails;

	private List<LimitGroupLines> limitGroupLinesList = new ArrayList<LimitGroupLines>();

	public LimitGroup() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("LimitGroup"));
	}

	public LimitGroup(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("limitGroupItemsList");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public String getId() {
		return groupCode;
	}

	public void setId(String id) {
		this.groupCode = id;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
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

	public LimitGroup getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitGroup beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<LimitGroupLines> getLimitGroupLinesList() {
		return limitGroupLinesList;
	}

	public void setLimitGroupLinesList(List<LimitGroupLines> limitGroupLinesLst) {
		this.limitGroupLinesList = limitGroupLinesLst;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getGroupOf() {
		return groupOf;
	}

	public void setGroupOf(String groupOf) {
		this.groupOf = groupOf;
	}

	public String getLimitCategory() {
		return limitCategory;
	}

	public void setLimitCategory(String limitCategory) {
		this.limitCategory = limitCategory;
	}
}
