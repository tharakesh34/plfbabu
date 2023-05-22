package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnquiryDetail {

	@JsonCreator
	public EnquiryDetail() {
	    super();
	}

	@XmlElement(name = "element")
	private List<EnquiryElement> enqElement;

	public List<EnquiryElement> getEnqElement() {
		return enqElement;
	}

	public void setEnqElement(List<EnquiryElement> enqElement) {
		this.enqElement = enqElement;
	}

}