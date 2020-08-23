package com.penanttech.pff.model.external.bre;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantIn {

	@JsonCreator	
	public ApplicantIn() {
	}
	
	@JsonProperty("element")
	List<ApplicantInElement> element;

	public List<ApplicantInElement> getElement() {
		return element;
	}

	public void setElement(List<ApplicantInElement> element) {
		this.element = element;
	}

	
}
