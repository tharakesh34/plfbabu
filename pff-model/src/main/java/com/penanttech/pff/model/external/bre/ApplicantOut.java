package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantOut {

	@JsonCreator
	public ApplicantOut() {
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
