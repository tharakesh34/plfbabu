package com.penanttech.pff.model.external.bre;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetail {

	@JsonCreator
	public AccountDetail() {
	}

	@JsonProperty("element")
	private List<AccountElement> acctElement;

	public List<AccountElement> getAcctElement() {
		return acctElement;
	}

	public void setAcctElement(List<AccountElement> acctElement) {
		this.acctElement = acctElement;
	}

}