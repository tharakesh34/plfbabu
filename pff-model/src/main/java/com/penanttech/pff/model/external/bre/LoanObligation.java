package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LoanObligation {

	@JsonCreator
	public LoanObligation() {
	    super();
	}

	@XmlElement(name = "element")
	List<LoanObligationElement> element;

	public List<LoanObligationElement> getElement() {
		return element;
	}

	public void setElement(List<LoanObligationElement> element) {
		this.element = element;
	}
}
