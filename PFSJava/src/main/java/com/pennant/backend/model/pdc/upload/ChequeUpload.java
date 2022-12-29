package com.pennant.backend.model.pdc.upload;

import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.pff.upload.model.UploadDetails;

public class ChequeUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private ChequeDetail chequeDetail;
	private String action;

	public ChequeUpload() {
		super();
	}

	public ChequeDetail getChequeDetail() {
		return chequeDetail;
	}

	public void setChequeDetail(ChequeDetail chequeDetail) {
		this.chequeDetail = chequeDetail;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}