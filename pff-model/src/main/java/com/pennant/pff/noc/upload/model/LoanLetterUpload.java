package com.pennant.pff.noc.upload.model;

import com.pennant.pff.upload.model.UploadDetails;

public class LoanLetterUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String letterType;
	private String modeOfTransfer;
	private String waiverCharges;

	public LoanLetterUpload() {
		super();
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
}