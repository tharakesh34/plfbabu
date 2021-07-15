package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetail {

	@JsonCreator
	public AccountDetail() {
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