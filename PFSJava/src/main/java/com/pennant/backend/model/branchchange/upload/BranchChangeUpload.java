package com.pennant.backend.model.branchchange.upload;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class BranchChangeUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String branchCode;
	private String remarks;
	private LoggedInUser userDetails;
	private String oldBranch;

	public BranchChangeUpload() {
		super();
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getOldBranch() {
		return oldBranch;
	}

	public void setOldBranch(String oldBranch) {
		this.oldBranch = oldBranch;
	}
}
