package com.pennant.backend.model.finance;

import java.io.Serializable;

public class FinStageAccountingLog implements Serializable {
	
    private static final long serialVersionUID = 274530333290518776L;
    
	private String finReference;
	private String finEvent;
	private String roleCode;
	private long linkedTranId = 0;
	private boolean Processed = false;
	public FinStageAccountingLog() {
		
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
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public long getLinkedTranId() {
		return linkedTranId;
	}
	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public boolean isProcessed() {
		return Processed;
	}

	public void setProcessed(boolean processed) {
		Processed = processed;
	}
	
}
