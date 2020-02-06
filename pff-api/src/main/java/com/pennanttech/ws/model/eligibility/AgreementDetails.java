package com.pennanttech.ws.model.eligibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;

public class AgreementDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<AgreementData> agreementsList = new ArrayList<>();
	private WSReturnStatus returnStatus;

	public AgreementDetails() {
		super();
	}

	public List<AgreementData> getAgreementsList() {
		return agreementsList;
	}

	public void setAgreementsList(List<AgreementData> agreementsList) {
		this.agreementsList = agreementsList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
