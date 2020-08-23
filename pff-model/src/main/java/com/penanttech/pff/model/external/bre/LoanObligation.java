package com.penanttech.pff.model.external.bre;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoanObligation {
	
	
	
	@JsonCreator
	public LoanObligation() {
	}

	@JsonProperty("element")
	List<LoanObligationElement> element;

	public List<LoanObligationElement> getElement() {
		return element;
	}

	public void setElement(List<LoanObligationElement> element) {
		this.element = element;
	}	
}
