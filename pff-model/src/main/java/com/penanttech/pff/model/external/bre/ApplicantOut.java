package com.penanttech.pff.model.external.bre;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
