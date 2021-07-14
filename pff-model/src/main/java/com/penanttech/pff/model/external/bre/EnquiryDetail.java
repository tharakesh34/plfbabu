package com.penanttech.pff.model.external.bre;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnquiryDetail {

	
	@JsonCreator
	public EnquiryDetail() {
	}

	@JsonProperty("element")
	private List<EnquiryElement> enqElement;

	public List<EnquiryElement> getEnqElement() {
		return enqElement;
	}

	public void setEnqElement(List<EnquiryElement> enqElement) {
		this.enqElement = enqElement;
	}

}