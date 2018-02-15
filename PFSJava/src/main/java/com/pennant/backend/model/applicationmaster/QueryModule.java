package com.pennant.backend.model.applicationmaster;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class QueryModule extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1000185170961421001L;
	
	private String queryModuleCode;
	private String queryModuleDesc;
	private String TableName;
	private String displayColumns;
	private String ResultColumns;
	private boolean subQuery;

	private boolean newRecord=false;
	private String lovValue;
	private QueryModule befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public QueryModule(String queryModuleCode) {
		super();
		this.queryModuleCode= queryModuleCode;
	}
	
	

	public QueryModule(String queryModuleCode, String tableName,  String resultColumns) {
	    super();
	    this.queryModuleCode = queryModuleCode;
	    TableName = tableName;
	    ResultColumns = resultColumns;
    }

	public QueryModule() {
		super();
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("countryCodeName");
		excludeFields.add("provinceCodeName");
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return queryModuleCode;
	}

	public void setId (String id) {
		this.queryModuleCode = id;
	}

	public String getQueryModuleCode() {
    	return queryModuleCode;
    }

	public void setQueryModuleCode(String queryModuleCode) {
    	this.queryModuleCode = queryModuleCode;
    }

	public String getQueryModuleDesc() {
    	return queryModuleDesc;
    }

	public void setQueryModuleDesc(String queryModuleDesc) {
    	this.queryModuleDesc = queryModuleDesc;
    }
	
	public String getTableName() {
    	return TableName;
    }

	public void setTableName(String tableName) {
    	TableName = tableName;
    }

	public String getResultColumns() {
    	return ResultColumns;
    }

	public void setResultColumns(String resultColumns) {
    	ResultColumns = resultColumns;
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

	public QueryModule getBefImage(){
		return this.befImage;
	}

	public void setBefImage(QueryModule beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isSubQuery() {
		return subQuery;
	}

	public void setSubQuery(boolean subQuery) {
		this.subQuery = subQuery;
	}

	public void setDisplayColumns(String displayColumns) {
	    this.displayColumns = displayColumns;
    }

	public String getDisplayColumns() {
	    return displayColumns;
    }
}
