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
 * FileName    		:  LimitStructureDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.limit;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Entity;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Model class for the <b>LimitStructureDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "limitStructureDetailsID", "groupCode", "limitLine", "groupName", "limitLineDesc",
		"structureName", "revolving", "editable", "limitCheck", "itemLevel", "itemSeq" })
@XmlAccessorType(XmlAccessType.NONE)
public class LimitStructureDetail implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;

	@JsonProperty("structureDetailId")
	private long limitStructureDetailsID = Long.MIN_VALUE;

	private String limitStructureCode;

	@XmlElement
	private String groupCode;

	@JsonProperty("lineCode")
	private String limitLine;

	@JsonProperty("sequence")
	private int itemSeq;

	@XmlElement
	private boolean editable;
	private String displayStyle;
	private int version;

	@JsonProperty("structureDetailDesc")
	private String structureName;
	@JsonProperty("lineCodeDesc")
	private String limitLineDesc;
	@JsonProperty("groupCodeDesc")
	private String groupName;
	private int key;
	private String limitCategory;

	@JsonProperty("level")
	private int itemLevel;

	@JsonProperty("check")
	private boolean limitCheck;

	@XmlElement
	private boolean revolving = false;

	private int itemPriority;
	private Map<String, LimitStructureDetail> subGroupsMap = new HashMap<String, LimitStructureDetail>();

	private long createdBy;
	private String createdUser;
	private Timestamp createdOn;
	private long lastMntBy;
	private String lastMaintainedUser;
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	private boolean newRecord = false;
	private String lovValue;
	private LimitStructureDetail befImage;
	private LoggedInUser userDetails;
	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public LimitStructureDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("LimitStructureDetail");
	}

	public LimitStructureDetail(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("structureName");
		excludeFields.add("key");
		excludeFields.add("createdUser");
		excludeFields.add("limitLineDesc");
		excludeFields.add("groupName");
		excludeFields.add("limitSecured");
		excludeFields.add("limitSecurityType");
		excludeFields.add("subGroupsMap");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public long getLimitStructureDetailsID() {
		return limitStructureDetailsID;
	}

	public void setLimitStructureDetailsID(long limitStructureDetailsId) {
		limitStructureDetailsID = limitStructureDetailsId;
	}

	@XmlTransient
	public long getId() {
		return limitStructureDetailsID;
	}

	public void setId(long id) {
		this.limitStructureDetailsID = id;
	}

	public String getLimitStructureCode() {
		return limitStructureCode;
	}

	public void setLimitStructureCode(String limitStructureCode) {
		this.limitStructureCode = limitStructureCode;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		this.displayStyle = displayStyle;
	}

	public String getLimitLine() {
		return limitLine;
	}

	public void setLimitLine(String limitLine) {
		this.limitLine = limitLine;
	}

	public boolean isLimitCheck() {
		return limitCheck;
	}

	public void setLimitCheck(boolean limitCheck) {
		this.limitCheck = limitCheck;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@XmlTransient
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
		return DateUtility.getXMLDate(createdOn);
	}

	@XmlTransient
	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}

	@XmlTransient
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			lastMntOn = DateUtility.ConvertFromXMLTime(xmlCalendar);
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn() throws DatatypeConfigurationException {

		if (lastMntOn == null) {
			return null;
		}
		return DateUtility.getXMLDate(lastMntOn);
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public LimitStructureDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitStructureDetail beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	@XmlTransient
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	@XmlTransient
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	@XmlTransient
	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	@XmlTransient
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@XmlTransient
	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	@XmlTransient
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@XmlTransient
	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getStructureName() {
		return structureName;
	}

	public void setStructureName(String structureName) {
		this.structureName = structureName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getLimitCategory() {
		return limitCategory;
	}

	public void setLimitCategory(String limitCategory) {
		this.limitCategory = limitCategory;
	}

	public int getItemPriority() {
		return itemPriority;
	}

	public void setItemPriority(int itemPriority) {
		this.itemPriority = itemPriority;
	}

	public Map<String, LimitStructureDetail> getSubGroupsMap() {
		return subGroupsMap;
	}

	public void setSubGroupsMap(Map<String, LimitStructureDetail> subGroupsMap) {
		this.subGroupsMap = subGroupsMap;
	}

	public int getItemSeq() {
		return itemSeq;
	}

	public void setItemSeq(int itemSeq) {
		this.itemSeq = itemSeq;
	}

	public int getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}

	public boolean isRevolving() {
		return revolving;
	}

	public void setRevolving(boolean revolving) {
		this.revolving = revolving;
	}

	public String getLimitLineDesc() {
		return limitLineDesc;
	}

	public void setLimitLineDesc(String limitLineDesc) {
		this.limitLineDesc = limitLineDesc;
	}
}
