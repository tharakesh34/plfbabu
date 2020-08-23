package com.pennanttech.pff.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PMAYRequest {

	private List<PmayDetails> pmayDetails = new ArrayList<>();

	public List<PmayDetails> getPmayDetails() {
		return pmayDetails;
	}

	public void setPmayDetails(List<PmayDetails> pmayDetails) {
		this.pmayDetails = pmayDetails;
	}

}
