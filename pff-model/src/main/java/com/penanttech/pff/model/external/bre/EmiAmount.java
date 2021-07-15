package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmiAmount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonCreator
	public EmiAmount() {
	}

	ArrayList<String> item;

	public ArrayList<String> getItem() {
		return item;
	}

	public void setItem(ArrayList<String> item) {
		this.item = item;
	}
}