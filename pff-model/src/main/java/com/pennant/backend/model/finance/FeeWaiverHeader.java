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

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FeeWaiverHeader table</b>.<br>
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FeeWaiverHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long waiverId = Long.MIN_VALUE;
	private long finID;
	@XmlElement
	private String finReference;
	private String event;
	@XmlElement
	private String remarks;
	private Date postingDate;
	@XmlElement
	private Date valueDate;
	private FeeWaiverHeader befImage;
	private String lovValue;
	private LoggedInUser userDetails;
	private boolean isAlwtoProceed = true;
	@XmlElementWrapper(name = "feeWaiverDetails")
	@XmlElement
	private List<FeeWaiverDetail> feeWaiverDetails = new ArrayList<FeeWaiverDetail>();
	private List<FinanceRepayments> rpyList = new ArrayList<FinanceRepayments>();
	private List<FinServiceInstruction> finServiceInstructions = new ArrayList<>();

	private String finSourceID = null;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
	private ExtendedFieldRender extendedFieldRender = null;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeWaiverDetails");
		excludeFields.add("auditDetailMap");
		excludeFields.add("isAlwtoProceed");
		excludeFields.add("rpyList");
		excludeFields.add("finSourceID");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("finServiceInstructions");

		return excludeFields;
	}

	public FeeWaiverHeader() {
		super();
	}

	public long getId() {
		return this.waiverId;
	}

	public void setId(long id) {
		this.waiverId = id;
	}

	public long getWaiverId() {
		return waiverId;
	}

	public void setWaiverId(long waiverId) {
		this.waiverId = waiverId;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
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

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public List<FinServiceInstruction> getFinServiceInstructions() {
		return finServiceInstructions;
	}

	public void setFinServiceInstructions(List<FinServiceInstruction> finServiceInstructions) {
		this.finServiceInstructions = finServiceInstructions;
	}

	public FinServiceInstruction getFinServiceInstruction() {
		FinServiceInstruction finServiceInstruction = null;
		if (finServiceInstructions != null && !finServiceInstructions.isEmpty()) {
			finServiceInstruction = finServiceInstructions.get(0);
		}
		return finServiceInstruction;
	}

	public void setFinServiceInstruction(FinServiceInstruction finServiceInstruction) {

		if (finServiceInstruction != null) {
			if (finServiceInstructions == null) {
				finServiceInstructions = new ArrayList<FinServiceInstruction>();
			}
			this.finServiceInstructions.add(finServiceInstruction);
		}
	}
}
