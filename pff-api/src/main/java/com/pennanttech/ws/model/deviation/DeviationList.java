package com.pennanttech.ws.model.deviation;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceDeviations;

public class DeviationList implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<FinanceDeviations> devitionList;
	private WSReturnStatus returnStatus;

	public DeviationList() {
		super();
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<FinanceDeviations> getDevitionList() {
		return devitionList;
	}

	public void setDevitionList(List<FinanceDeviations> devitionList) {
		this.devitionList = devitionList;
	}

}
