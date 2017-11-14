package com.pennant.backend.model.financemanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

@XmlType(propOrder ={
		"flagCode", "flagDesc"
})
@XmlAccessorType(XmlAccessType.NONE)
public class FinFlagsDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private String 				reference;
	@XmlElement
	private String 				flagCode;
	@XmlElement
	private String 				flagDesc;
	private String 				moduleName;
	private boolean 			newRecord = false;
	private FinFlagsDetail 		befImage;
	private LoggedInUser 		userDetails;
	private HashMap<String, 	List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	
	public boolean isNew() {
		return isNewRecord();
	}
	
	public FinFlagsDetail() {
		super();
	}
	public FinFlagsDetail(String  reference) {
		super();
		this.reference = reference;
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("flagDesc");
		return excludeFields;
	}
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getFlagCode() {
		return flagCode;
	}
	public void setFlagCode(String flagCode) {
		this.flagCode = flagCode;
	}
	
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getFlagDesc() {
		return flagDesc;
	}
	public void setFlagDesc(String flagDesc) {
		this.flagDesc = flagDesc;
	}

	public FinFlagsDetail getBefImage() {
	    return befImage;
    }

	public void setBefImage(FinFlagsDetail befImage) {
	    this.befImage = befImage;
    }

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
