package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinCovenantType;

public class FinCovenantResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<FinCovenantType> finCovenantList;
	private WSReturnStatus returnStatus;

	public FinCovenantResponse() {
		super();
	}

	public List<FinCovenantType> getFinCovenantList() {
		return finCovenantList;
	}

	public void setFinCovenantList(List<FinCovenantType> finCovenantList) {
		this.finCovenantList = finCovenantList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
