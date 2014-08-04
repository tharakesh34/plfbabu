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
 * * FileName : Query.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-07-2013 * * Modified Date :
 * 04-07-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 04-07-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Query table</b>.<br>
 * 
 */
public class Query implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private String queryCode;
	private String queryModule;
 	private String queryDesc;
	private String sqlQuery;
	private String actualBlock;
	private boolean subQuery;
	private String queryModuleDesc;
	private String tableName;
	private String resultColumns;
	private String displayColumns;
	private QueryModule queryModuleObj;
	private int version;

	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private Query befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode = "";
	@XmlTransient
	private String nextRoleCode = "";
	@XmlTransient
	private String taskId = "";
	@XmlTransient
	private String nextTaskId = "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;
	private boolean active;


	public boolean isNew() {
		return isNewRecord();
	}

	public Query(String queryCode) {
		this.queryCode = queryCode;
	}

	public Query() {
		super();
		this.workflowId = WorkFlowUtil.getWorkFlowID("Query");
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("queryModuleDesc");
		excludeFields.add("tableName");
		excludeFields.add("resultColumns");
		excludeFields.add("displayColumns");
		excludeFields.add("queryModuleObj");
		excludeFields.add("lastMaintainedUser");
		excludeFields.add("lastMaintainedOn");
		
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return queryCode;
	}

	public void setId(String id) {
		this.queryCode = id;
	}

	public String getQueryCode() {
		return queryCode;
	}

	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}

	public String getQueryModule() {
		return queryModule;
	}

	public void setQueryModule(String queryModule) {
		this.queryModule = queryModule;
	}

	public String getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}
	
	public String getSQLQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getActualBlock() {
		return actualBlock;
	}

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public boolean isSubQuery() {
		return subQuery;
	}

	public void setSubQuery(boolean subQuery) {
		this.subQuery = subQuery;
	}

	public String getQueryModuleDesc() {
    	return queryModuleDesc;
    }

	public void setQueryModuleDesc(String queryModuleDesc) {
    	this.queryModuleDesc = queryModuleDesc;
    }

	public String getTableName() {
    	return this.tableName;
    }

	public void setTableName(String tableName) {
    	this.tableName = tableName;
    }

	public String getResultColumns() {
    	return this.resultColumns;
    }

	public void setResultColumns(String resultColumns) {
    	this.resultColumns = resultColumns;
    }

	public String getDisplayColumns() {
    	return displayColumns;
    }

	public void setDisplayColumns(String displayColumns) {
    	this.displayColumns = displayColumns;
    }

	public QueryModule getQueryModuleObj() {
		this.queryModuleObj = null;
		this.queryModuleObj = new QueryModule();
		queryModuleObj.setQueryModuleCode(this.queryModule);
		queryModuleObj.setQueryModuleDesc(this.queryModuleDesc);
		queryModuleObj.setTableName(this.tableName);
		queryModuleObj.setResultColumns(this.resultColumns);
		queryModuleObj.setDisplayColumns(this.displayColumns);
		
    	return queryModuleObj;
    }

	public void setQueryModuleObj(QueryModule queryModule) {
    	this.queryModuleObj = queryModule;
    }
 
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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
	public Query getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Query beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
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
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}


	// Overidden Equals method to handle the comparision
	public boolean equals(Query query) {
		return getId() == query.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Query) {
			Query query = (Query) obj;
			return equals(query);
		}
		return false;
	}
}
