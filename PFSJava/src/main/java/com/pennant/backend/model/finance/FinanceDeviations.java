package com.pennant.backend.model.finance;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;

public class FinanceDeviations implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 8456523350616062070L;

	private long deviationId = Long.MIN_VALUE;
	private String finReference;
	private String module;
	private String deviationCode;
	private String deviationType;
	private String deviationValue;
	private String userRole;
	private String delegationRole;
	private String approvalStatus;
	private Timestamp deviationDate;
	private String deviationUserId;
	private String delegatedUserId;
	private boolean newRecord = false;
	private FinanceDeviations befImage;
	
	private String custCIF;
	private String custShrtName;
	private long custID ;

	public FinanceDeviations() {
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIF");
		excludeFields.add("custID");
		excludeFields.add("custShrtName");

		return excludeFields;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDeviationCode() {
		return deviationCode;
	}

	public void setDeviationCode(String deviationCode) {
		this.deviationCode = deviationCode;
	}

	public String getDeviationType() {
		return deviationType;
	}

	public void setDeviationType(String deviationType) {
		this.deviationType = deviationType;
	}

	public String getDeviationValue() {
		return deviationValue;
	}

	public void setDeviationValue(String deviationValue) {
		this.deviationValue = deviationValue;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getDelegationRole() {
		return delegationRole;
	}

	public void setDelegationRole(String delegationRole) {
		this.delegationRole = delegationRole;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getDeviationUserId() {
		return deviationUserId;
	}

	public void setDeviationUserId(String deviationUserId) {
		this.deviationUserId = deviationUserId;
	}

	public String getDelegatedUserId() {
		return delegatedUserId;
	}

	public void setDelegatedUserId(String delegatedUserId) {
		this.delegatedUserId = delegatedUserId;
	}

	public Timestamp getDeviationDate() {
		return deviationDate;
	}

	public void setDeviationDate(Timestamp deviationDate) {
		this.deviationDate = deviationDate;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getDeviationId() {
		return deviationId;
	}

	public void setDeviationId(long deviationId) {
		this.deviationId = deviationId;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@Override
	public boolean isNew() {
		return newRecord;
	}

	@Override
	public long getId() {
		return deviationId;
	}

	@Override
	public void setId(long id) {
		this.deviationId = id;
	}

	public FinanceDeviations getBefImage() {
		return befImage;
	}

	public void setBefImage(FinanceDeviations befImage) {
		this.befImage = befImage;
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

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

}
