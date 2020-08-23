package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibilityApclOut implements Serializable{
	

	private static final long serialVersionUID = 1L;
	@JsonProperty("element")
	private List<EligibilityApclOutElement> eligApclOutElement;
	
	@JsonCreator
	public EligibilityApclOut(){}

	public List<EligibilityApclOutElement> getEligApclOutElement() {
		return eligApclOutElement;
	}

	public void setEligApclOutElement(List<EligibilityApclOutElement> eligApclOutElement) {
		this.eligApclOutElement = eligApclOutElement;
	}



}