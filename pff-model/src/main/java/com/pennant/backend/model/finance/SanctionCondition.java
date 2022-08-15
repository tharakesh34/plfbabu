package com.pennant.backend.model.finance;

public class SanctionCondition {

	private String finReference = "";

	private String condition = "";

	private String status = "";

	private boolean applicable = false;

	public SanctionCondition copyEntity() {
		SanctionCondition entity = new SanctionCondition();
		entity.setFinReference(this.finReference);
		entity.setCondition(this.condition);
		entity.setStatus(this.status);
		entity.setApplicable(this.applicable);
		return entity;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}
}
