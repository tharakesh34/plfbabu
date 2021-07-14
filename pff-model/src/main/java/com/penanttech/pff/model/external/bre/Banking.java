package com.penanttech.pff.model.external.bre;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Banking {
	
	
	@JsonCreator
	public Banking() {
	}

	@JsonProperty("element")
	List<BankingElement> element;

	public List<BankingElement> getElement() {
		return element;
	}

	public void setElement(List<BankingElement> element) {
		this.element = element;
	}

}
