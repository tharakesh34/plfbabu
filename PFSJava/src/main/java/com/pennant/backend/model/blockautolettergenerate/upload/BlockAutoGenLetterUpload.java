package com.pennant.backend.model.blockautolettergenerate.upload;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class BlockAutoGenLetterUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String remarks;
	private String action;
	private LoggedInUser userDetails;

	public BlockAutoGenLetterUpload() {
		super();
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}