package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.List;

public class RescheduleLogHeader {

	private String custName;
	private String finReference;
	private List<RescheduleLog> rescheduleLogList = new ArrayList<>();

	public RescheduleLogHeader() {
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public List<RescheduleLog> getRescheduleLogList() {
		return rescheduleLogList;
	}

	public void setRescheduleLogList(List<RescheduleLog> rescheduleLogList) {
		this.rescheduleLogList = rescheduleLogList;
	}

}
