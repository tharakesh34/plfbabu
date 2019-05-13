package com.pennant.backend.model.financemanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "finType", "receiptMode" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinTypeReceiptModes extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private String finType;
	private String receiptMode;
	private boolean newRecord = false;
	private FinTypeReceiptModes befImage;
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinTypeReceiptModes getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTypeReceiptModes befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

}
