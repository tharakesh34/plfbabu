package com.pennant.pff.revwriteoffupload.model;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class RevWriteOffUploadDetail extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String remarks;
	private String event;
	private FinanceMain financeMain;

	public RevWriteOffUploadDetail() {
		super();
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

}