package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionReasonCode implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonCreator
	public DecisionReasonCode() {
	    super();
	}

	ArrayList<String> item;

	public ArrayList<String> getItem() {
		return item;
	}

	public void setItem(ArrayList<String> item) {
		this.item = item;
	}
}
