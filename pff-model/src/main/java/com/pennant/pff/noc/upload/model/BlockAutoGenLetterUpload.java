package com.pennant.pff.noc.upload.model;

import com.pennant.pff.upload.model.UploadDetails;

public class BlockAutoGenLetterUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String remarks;
	private String action;

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

}