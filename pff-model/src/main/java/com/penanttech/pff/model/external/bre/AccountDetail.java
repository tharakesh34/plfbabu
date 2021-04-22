package com.penanttech.pff.model.external.bre;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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