package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Banking {

	@JsonCreator
	public Banking() {
	    super();
	}

	@XmlElement(name = "element")
	List<BankingElement> element;

	public List<BankingElement> getElement() {
		return element;
	}

	public void setElement(List<BankingElement> element) {
		this.element = element;
	}

}
