package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FeeWaiverHeader table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FeeWaiverHeader extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long waiverId = Long.MIN_VALUE;
	@XmlElement
	private String finReference;
	private String event;
	@XmlElement
	private String remarks;
	private Date postingDate;
	@XmlElement
	private Date valueDate;
	private boolean newRecord;
	private FeeWaiverHeader befImage;
	private String lovValue;
	private LoggedInUser userDetails;
	private boolean isAlwtoProceed = true;
	@XmlElementWrapper(name = "feeWaiverDetails")
	@XmlElement
	private List<FeeWaiverDetail> feeWaiverDetails = new ArrayList<FeeWaiverDetail>();
	private List<FinanceRepayments> rpyList = new ArrayList<FinanceRepayments>();

	private String finSourceID = null;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeWaiverDetails");
		excludeFields.add("auditDetailMap");
		excludeFields.add("isAlwtoProceed");
		excludeFields.add("rpyList");
		excludeFields.add("finSourceID");
		return excludeFields;
	}

	public FeeWaiverHeader() {
		super();
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return this.waiverId;
	}

	@Override
	public void setId(long id) {
		this.waiverId = id;
	}

	public long getWaiverId() {
		return waiverId;
	}

	public void setWaiverId(long waiverId) {
		this.waiverId = waiverId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
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

	public FeeWaiverHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FeeWaiverHeader befImage) {
		this.befImage = befImage;
	}

	public List<FeeWaiverDetail> getFeeWaiverDetails() {
		return feeWaiverDetails;
	}

	public void setFeeWaiverDetails(List<FeeWaiverDetail> feeWaiverDetails) {
		this.feeWaiverDetails = feeWaiverDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isAlwtoProceed() {
		return isAlwtoProceed;
	}

	public void setAlwtoProceed(boolean isAlwtoProceed) {
		this.isAlwtoProceed = isAlwtoProceed;
	}

	public List<FinanceRepayments> getRpyList() {
		return rpyList;
	}

	public void setRpyList(List<FinanceRepayments> rpyList) {
		this.rpyList = rpyList;
	}

	public String getFinSourceID() {
		return finSourceID;
	}

	public void setFinSourceID(String finSourceID) {
		this.finSourceID = finSourceID;
	}
}
