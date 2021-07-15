package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Banking {

	@JsonCreator
	public Banking() {
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
