package com.pennanttech.ws.model.eligibility;

import com.pennant.backend.model.WSReturnStatus;

public class AgreementData {

	private byte [] docContent;
	
	private WSReturnStatus returnStatus;

	public byte [] getDocContent() {
		return docContent;
	}

	public void setDocContent(byte [] docContent) {
		this.docContent = docContent;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
	
	
}
