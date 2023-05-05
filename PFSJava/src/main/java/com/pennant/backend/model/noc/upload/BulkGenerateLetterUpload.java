package com.pennant.backend.model.noc.upload;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class BulkGenerateLetterUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String letterType;
	private String modeOfTransfer;
	private String waiverCharges;
	private LoggedInUser userDetails;

	public BulkGenerateLetterUpload() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public String getModeOfTransfer() {
		return modeOfTransfer;
	}

	public void setModeOfTransfer(String modeOfTransfer) {
		this.modeOfTransfer = modeOfTransfer;
	}

	public String getWaiverCharges() {
		return waiverCharges;
	}

	public void setWaiverCharges(String waiverCharges) {
		this.waiverCharges = waiverCharges;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}