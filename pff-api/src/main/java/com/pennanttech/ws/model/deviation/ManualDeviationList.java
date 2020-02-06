package com.pennanttech.ws.model.deviation;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.ManualDeviation;

public class ManualDeviationList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ManualDeviation> manualDeviationList;

	private WSReturnStatus returnStatus;

	public ManualDeviationList() {
		super();
	}

	public List<ManualDeviation> getManualDeviationList() {
		return manualDeviationList;
	}

	public void setManualDeviationList(List<ManualDeviation> manualDeviationList) {
		this.manualDeviationList = manualDeviationList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
