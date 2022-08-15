package com.pennanttech.pff.documents.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DocumentStatus extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private String finReference;
	private boolean newRecord = false;
	private DocumentStatus befImage;
	private LoggedInUser userDetails;
	private List<DocumentStatusDetail> dsList = new ArrayList<>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

	private String finType;
	private String finBranch;
	private Date finStartDate;
	private Date maturityDate;
	private String finCcy;
	private String scheduleMethod;
	private String profitDaysBasis;
	private String custCIF;
	private String custShrtName;
	private String finCategory;
	private String branchDesc;
	private String finTypeDesc;
	private boolean finIsActive;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("dsList");
		excludeFields.add("auditDetailMap");
		excludeFields.add("finType");
		excludeFields.add("finBranch");
		excludeFields.add("finStartDate");
		excludeFields.add("maturityDate");
		excludeFields.add("finCcy");
		excludeFields.add("scheduleMethod");
		excludeFields.add("profitDaysBasis");
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("finCategory");
		excludeFields.add("branchDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("finIsActive");

		return excludeFields;
	}

	public DocumentStatus() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public DocumentStatus getBefImage() {
		return befImage;
	}

	public void setBefImage(DocumentStatus befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<DocumentStatusDetail> getDsList() {
		return dsList;
	}

	public void setDsList(List<DocumentStatusDetail> dsList) {
		this.dsList = dsList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

}
