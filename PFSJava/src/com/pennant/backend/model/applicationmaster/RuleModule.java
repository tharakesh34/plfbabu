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

public class RuleModule {
	private String entityCode;
	private String ruleModule;
	private String ruleModuleCode;
	private String ruleModuleDesc;
	private String TableName;
	private int version;

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
	private RuleModule befImage;
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

	public RuleModule(String entityCode) {
		super();
		// getWorkFlowID(entityCode,"City");
		this.workflowId = WorkFlowUtil.getWorkFlowID("City");
		this.entityCode= entityCode;
	}

	public RuleModule(String entityCode, String ruleModuleCode, String tableName,  String resultColumns) {
	    super();
	    this.entityCode = entityCode;
	    this.ruleModuleCode = ruleModuleCode;
	    TableName = tableName;
	   
    }

	public RuleModule() {
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
		return ruleModuleCode;
	}

	public void setId (String id) {
		this.ruleModuleCode = id;
	}
	public String getRuleModule() {
    	return ruleModule;
    }

	public void setRuleModule(String ruleModule) {
    	this.ruleModule = ruleModule;
    }
	public String getRuleModuleCode() {
    	return ruleModuleCode;
    }

	public void setRuleModuleCode(String ruleModuleCode) {
    	this.ruleModuleCode = ruleModuleCode;
    }

	public String getRuleModuleDesc() {
    	return ruleModuleDesc;
    }

	public void setRuleModuleDesc(String ruleModuleDesc) {
    	this.ruleModuleDesc = ruleModuleDesc;
    }
	
	public String getTableName() {
    	return TableName;
    }

	public void setTableName(String tableName) {
    	TableName = tableName;
    }

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
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
	public RuleModule getBefImage(){
		return this.befImage;
	}

	public void setBefImage(RuleModule beforeImage){
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

	
}



