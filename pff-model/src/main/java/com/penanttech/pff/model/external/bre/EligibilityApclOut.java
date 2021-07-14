package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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