package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceDedup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceDedupResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<FinanceDedup> financeDedup = new ArrayList<>();

	private WSReturnStatus returnStatus;

	public FinanceDedupResponse() {
		super();

	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<FinanceDedup> getFinanceDedup() {
		return financeDedup;
	}

	public void setFinanceDedup(List<FinanceDedup> financeDedup) {
		this.financeDedup = financeDedup;
	}

}
