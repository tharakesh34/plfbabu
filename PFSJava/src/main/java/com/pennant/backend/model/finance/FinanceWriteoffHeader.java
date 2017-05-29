package com.pennant.backend.model.finance;

import java.io.Serializable;

public class FinanceWriteoffHeader implements Serializable {
	
    private static final long serialVersionUID = -1477748770396649402L;
    
	//Finance Details
    private String finReference;
    private FinanceWriteoff financeWriteoff;
    private FinanceDetail financeDetail;
	
	public FinanceWriteoffHeader() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
