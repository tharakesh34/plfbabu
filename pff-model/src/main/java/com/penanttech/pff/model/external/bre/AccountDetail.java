package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetail {

	@JsonCreator
	public AccountDetail() {
	    super();
	}

	@XmlElement(name = "element")
	private List<AccountElement> acctElement;

	public List<AccountElement> getAcctElement() {
		return acctElement;
	}

	public void setAcctElement(List<AccountElement> acctElement) {
		this.acctElement = acctElement;
	}

}