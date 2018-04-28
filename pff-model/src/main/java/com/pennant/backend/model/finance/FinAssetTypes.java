package com.pennant.backend.model.finance;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinAssetTypes extends AbstractWorkflowEntity {

	private static final long	serialVersionUID	= 1L;

	private Long				assetTypeId			= Long.MIN_VALUE;
	private String				assetType;
	private String				reference;
	private int					seqNo				= 0;
	private boolean				newRecord			= false;
	private LoggedInUser		userDetails;
	private FinAssetTypes		befImage;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinAssetTypes getBefImage() {
		return befImage;
	}

	public void setBefImage(FinAssetTypes befImage) {
		this.befImage = befImage;
	}

	public Long getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(Long assetTypeId) {
		this.assetTypeId = assetTypeId;
	}

}
