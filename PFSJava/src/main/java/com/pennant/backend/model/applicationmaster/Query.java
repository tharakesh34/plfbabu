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

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Query table</b>.<br>
 * 
 */
public class Query extends AbstractWorkflowEntity {
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
	private boolean newRecord;
	private String lovValue;
	private Query befImage;
	private LoggedInUser userDetails;
	private boolean active;

	public boolean isNew() {
		return isNewRecord();
	}

	public Query(String queryCode) {
		super();
		this.queryCode = queryCode;
	}

	public Query() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("queryModuleDesc");
		excludeFields.add("tableName");
		excludeFields.add("resultColumns");
		excludeFields.add("displayColumns");
		excludeFields.add("queryModuleObj");
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public Query getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Query beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
