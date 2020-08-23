package com.penanttech.pff.model.external.bre;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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