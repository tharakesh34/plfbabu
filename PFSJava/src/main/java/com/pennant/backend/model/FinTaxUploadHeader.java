package com.pennant.backend.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * @author durgaprasad.g
 * 
 */
public class FinTaxUploadHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long batchReference;
	private String fileName;
	private long numberofRecords;
	private Date batchCreatedDate;
	private Date batchApprovedDate;
	private String status;
	private FinTaxUploadHeader befImage;
	private LoggedInUser userDetails;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private List<FinTaxUploadDetail> finTaxUploadDetailList;
	private boolean totalSelected;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("totalSelected");

		return excludeFields;
	}

	// Getter and Setter methods
	public long getId() {
		return batchReference;
	}

	public void setId(long id) {
		this.batchReference = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getBatchCreatedDate() {
		return batchCreatedDate;
	}

	public void setBatchCreatedDate(Date batchCreatedDate) {
		this.batchCreatedDate = batchCreatedDate;
	}

	public Date getBatchApprovedDate() {
		return batchApprovedDate;
	}

	public void setBatchApprovedDate(Date batchApprovedDate) {
		this.batchApprovedDate = batchApprovedDate;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getNumberofRecords() {
		return numberofRecords;
	}

	public void setNumberofRecords(long numberofRecords) {
		this.numberofRecords = numberofRecords;
	}

	public List<FinTaxUploadDetail> getFinTaxUploadDetailList() {
		return finTaxUploadDetailList;
	}

	public void setFinTaxUploadDetailList(List<FinTaxUploadDetail> finTaxUploadDetailList) {
		this.finTaxUploadDetailList = finTaxUploadDetailList;
	}

	public long getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(long batchReference) {
		this.batchReference = batchReference;
	}

	public FinTaxUploadHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTaxUploadHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public boolean isTotalSelected() {
		return totalSelected;
	}

	public void setTotalSelected(boolean totalSelected) {
		this.totalSelected = totalSelected;
	}

}
