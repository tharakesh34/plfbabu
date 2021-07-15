package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibilityApclOut implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "element")
	private List<EligibilityApclOutElement> eligApclOutElement;

	@JsonCreator
	public EligibilityApclOut() {
	}

	public List<EligibilityApclOutElement> getEligApclOutElement() {
		return eligApclOutElement;
	}

	public void setEligApclOutElement(List<EligibilityApclOutElement> eligApclOutElement) {
		this.eligApclOutElement = eligApclOutElement;
	}

}