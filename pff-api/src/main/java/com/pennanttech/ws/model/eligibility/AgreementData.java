package com.pennanttech.ws.model.eligibility;

import com.pennant.backend.model.WSReturnStatus;

public class AgreementData {

	private String cif;
	
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

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}
	
	
}
