package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinFeeRefundHeader extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long headerId = Long.MIN_VALUE;
	private String finType;
	private String finBranch;
	private String lovDescCustCIF;
	private String LovDescCustShrtName;
	private String finReference;
	private String fintypedesc;
	private String branchdesc;
	private String finCcy;
	private long custId;
	private List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>(1);
	private List<FinFeeRefundDetails> finFeeRefundDetails = new ArrayList<FinFeeRefundDetails>(1);
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>(1);
	private long linkedTranId = 0;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finType");
		excludeFields.add("finBranch");
		excludeFields.add("lovDescCustCIF");
		excludeFields.add("LovDescCustShrtName");
		excludeFields.add("fintypedesc");
		excludeFields.add("branchdesc");
		excludeFields.add("finCcy");
		excludeFields.add("custId");
		excludeFields.add("finFeeDetailList");
		excludeFields.add("finFeeRefundDetails");
		excludeFields.add("auditDetailMap");
		return excludeFields;
	}

	private boolean newRecord = false;
	private FinFeeRefundHeader befImage;
	private LoggedInUser userDetails;

	public FinFeeRefundHeader() {
		super();
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return this.headerId;
	}

	@Override
	public void setId(long id) {
		this.headerId = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
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

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return LovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		LovDescCustShrtName = lovDescCustShrtName;
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

	public FinFeeRefundHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FinFeeRefundHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<FinFeeRefundDetails> getFinFeeRefundDetails() {
		return finFeeRefundDetails;
	}

	public void setFinFeeRefundDetails(List<FinFeeRefundDetails> finFeeRefundDetails) {
		this.finFeeRefundDetails = finFeeRefundDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getFintypedesc() {
		return fintypedesc;
	}

	public void setFintypedesc(String fintypedesc) {
		this.fintypedesc = fintypedesc;
	}

	public String getBranchdesc() {
		return branchdesc;
	}

	public void setBranchdesc(String branchdesc) {
		this.branchdesc = branchdesc;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public List<FinFeeDetail> getFinFeeDetailList() {
		return finFeeDetailList;
	}

	public void setFinFeeDetailList(List<FinFeeDetail> finFeeDetailList) {
		this.finFeeDetailList = finFeeDetailList;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
