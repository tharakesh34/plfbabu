package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinOCRHeader extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;
	private long headerID = Long.MIN_VALUE;
	private String ocrID;
	private String ocrDescription;
	private int customerPortion;
	private String ocrApplicable;
	private boolean splitApplicable;
	private String finReference;

	private List<FinOCRDetail> ocrDetailList = new ArrayList<FinOCRDetail>();
	private List<FinOCRCapture> finOCRCapturesList = new ArrayList<FinOCRCapture>();
	private boolean newRecord = false;
	private FinOCRHeader befImage;
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private boolean definitionApproved = false;

	public FinOCRHeader() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("definitionApproved");
		excludeFields.add("ocrDetailList");
		excludeFields.add("finOCRCapturesList");
		return excludeFields;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public String getOcrID() {
		return ocrID;
	}

	public void setOcrID(String ocrID) {
		this.ocrID = ocrID;
	}

	public String getOcrDescription() {
		return ocrDescription;
	}

	public void setOcrDescription(String ocrDescription) {
		this.ocrDescription = ocrDescription;
	}

	public int getCustomerPortion() {
		return customerPortion;
	}

	public void setCustomerPortion(int custPortion) {
		this.customerPortion = custPortion;
	}

	public String getOcrApplicable() {
		return ocrApplicable;
	}

	public void setOcrApplicable(String ocrApplicableOn) {
		this.ocrApplicable = ocrApplicableOn;
	}

	public boolean getSplitApplicable() {
		return splitApplicable;
	}

	public void setSplitApplicable(boolean splitApplicable) {
		this.splitApplicable = splitApplicable;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinOCRHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FinOCRHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<FinOCRDetail> getOcrDetailList() {
		return ocrDetailList;
	}

	public void setOcrDetailList(List<FinOCRDetail> ocrDetailList) {
		this.ocrDetailList = ocrDetailList;
	}

	public List<FinOCRCapture> getFinOCRCapturesList() {
		return finOCRCapturesList;
	}

	public void setFinOCRCapturesList(List<FinOCRCapture> finOCRCapturesList) {
		this.finOCRCapturesList = finOCRCapturesList;
	}

	public boolean isDefinitionApproved() {
		return definitionApproved;
	}

	public void setDefinitionApproved(boolean definitionApproved) {
		this.definitionApproved = definitionApproved;
	}

}
