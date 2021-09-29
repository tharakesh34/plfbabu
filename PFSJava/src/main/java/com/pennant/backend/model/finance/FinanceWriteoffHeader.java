package com.pennant.backend.model.finance;

import java.io.Serializable;

public class FinanceWriteoffHeader implements Serializable {

	private static final long serialVersionUID = -1477748770396649402L;

	// Finance Details
	private long finID;
	private String finReference;
	private FinanceWriteoff financeWriteoff;
	private FinanceDetail financeDetail;

	public FinanceWriteoffHeader() {

	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public FinanceWriteoff getFinanceWriteoff() {
		return financeWriteoff;
	}

	public void setFinanceWriteoff(FinanceWriteoff financeWriteoff) {
		this.financeWriteoff = financeWriteoff;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
