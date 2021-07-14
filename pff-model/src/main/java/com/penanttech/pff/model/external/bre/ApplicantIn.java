package com.penanttech.pff.model.external.bre;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
