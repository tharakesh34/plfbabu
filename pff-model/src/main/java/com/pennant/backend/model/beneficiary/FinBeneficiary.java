package com.pennant.backend.model.beneficiary;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinBeneficiary extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long beneficiaryID = Long.MIN_VALUE;
	private String finReference;
	private FinBeneficiary befImage;
	private LoggedInUser userDetails;
	private Beneficiary beneficiary;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinBeneficiary() {
		super();
	}

	public FinBeneficiary(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("beneficiary");
		return excludeFields;
	}

	public long getId() {
		return beneficiaryID;
	}

	public void setId(long id) {
		this.beneficiaryID = id;
	}

	public long getBeneficiaryID() {
		return beneficiaryID;
	}

	public void setBeneficiaryID(long beneficiaryID) {
		this.beneficiaryID = beneficiaryID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public FinBeneficiary getBefImage() {
		return befImage;
	}

	public void setBefImage(FinBeneficiary befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Beneficiary getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

}
