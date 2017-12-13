package com.pennant.backend.model.finance;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinAssetTypes extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private String 		assetType;
	private String		reference;
	private int			seqNo				= 0;
	private boolean		newRecord			= false;
	private LoggedInUser userDetails;
	private FinAssetTypes befImage;
	
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
}
