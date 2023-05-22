package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantOut {

	@JsonCreator
	public ApplicantOut() {
	    super();
	}

	@XmlElement(name = "element")
	List<ApplicantOutElement> element;

	public List<ApplicantOutElement> getElement() {
		return element;
	}

	public void setElement(List<ApplicantOutElement> element) {
		this.element = element;
	}

}
