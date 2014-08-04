package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.util.WorkFlowUtil;

public class QueryModule {
	private String queryModuleCode;
	private String queryModuleDesc;
	private String TableName;
	private String displayColumns;
	private String ResultColumns;
	private int version;
	private boolean subQuery;

	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private QueryModule befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode="";
	@XmlTransient
	private String nextRoleCode= "";
	@XmlTransient
	private String taskId="";
	@XmlTransient
	private String nextTaskId= "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public QueryModule(String queryModuleCode) {
		super();
		this.workflowId = WorkFlowUtil.getWorkFlowID("City");
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
		excludeFields.add("lastMaintainedOn");
		excludeFields.add("lastMaintainedUser");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
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

	public XMLGregorianCalendar getLastMaintainedOn()
	throws DatatypeConfigurationException {

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
	public QueryModule getBefImage(){
		return this.befImage;
	}

	public void setBefImage(QueryModule beforeImage){
		this.befImage=beforeImage;
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
		if (this.workflowId==0){
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


	public boolean isSubQuery() {
		return subQuery;
	}

	public void setSubQuery(boolean subQuery) {
		this.subQuery = subQuery;
	}


	// Overidden Equals method to handle the comparision
	public boolean equals(City city) {
		return getId() == city.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof City) {
			City city = (City) obj;
			return equals(city);
		}
		return false;
	}

	public void setDisplayColumns(String displayColumns) {
	    this.displayColumns = displayColumns;
    }

	public String getDisplayColumns() {
	    return displayColumns;
    }
}


