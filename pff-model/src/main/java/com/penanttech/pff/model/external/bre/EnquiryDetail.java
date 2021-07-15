package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnquiryDetail {

	@JsonCreator
	public EnquiryDetail() {
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