package com.penanttech.pff.model.external.bre;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantOut {

	@JsonCreator	
	public ApplicantOut() {
	}
	
	@JsonProperty("element")
	List<ApplicantOutElement> element;

	public List<ApplicantOutElement> getElement() {
		return element;
	}

	public void setElement(List<ApplicantOutElement> element) {
		this.element = element;
	}

	
}
