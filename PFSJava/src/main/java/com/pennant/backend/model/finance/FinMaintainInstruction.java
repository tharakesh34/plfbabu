package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Collateral table</b>.<br>
 *
 */

public class FinMaintainInstruction extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long finMaintainId = Long.MIN_VALUE;
	private String finReference;
	private String event;
	private boolean newRecord;
	private String lovValue;
	private FinMaintainInstruction befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private List<FinCovenantType> finCovenantTypeList = new ArrayList<FinCovenantType>(1);
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public FinMaintainInstruction() {
		super();
	}

	public FinMaintainInstruction(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return finMaintainId;
	}

	public void setId(long id) {
		this.finMaintainId = id;
	}

	public long getFinMaintainId() {
		return finMaintainId;
	}

	public void setFinMaintainId(long finMaintainId) {
		this.finMaintainId = finMaintainId;
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

	public FinMaintainInstruction getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinMaintainInstruction beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public List<FinCovenantType> getFinCovenantTypeList() {
		return finCovenantTypeList;
	}

	public void setFinCovenantTypeList(List<FinCovenantType> finCovenantTypeList) {
		this.finCovenantTypeList = finCovenantTypeList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}
