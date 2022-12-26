package com.pennant.backend.model.mandate;

import com.pennant.pff.upload.model.UploadDetails;

public class MandateUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private Mandate mandate;

	public Mandate getMandate() {
		return mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

}
