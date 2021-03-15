package com.pennant.backend.model.finance;

public class SecondaryAccount {

	private String finReference;
	private int priority;
	private String accountNumber;
	private String finEvent;

	public SecondaryAccount copyEntity() {
		SecondaryAccount entity = new SecondaryAccount();
		entity.setFinReference(this.finReference);
		entity.setPriority(this.priority);
		entity.setAccountNumber(this.accountNumber);
		entity.setFinEvent(this.finEvent);
		return entity;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

}
