package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantIn {

	@JsonCreator
	public ApplicantIn() {
	    super();
	}

	@XmlElement(name = "element")
	List<ApplicantInElement> element;

	public List<ApplicantInElement> getElement() {
		return element;
	}

	public void setElement(List<ApplicantInElement> element) {
		this.element = element;
	}

}
