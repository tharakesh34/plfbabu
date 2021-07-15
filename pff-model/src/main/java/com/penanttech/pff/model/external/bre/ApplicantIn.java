package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantIn {

	@JsonCreator
	public ApplicantIn() {
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
