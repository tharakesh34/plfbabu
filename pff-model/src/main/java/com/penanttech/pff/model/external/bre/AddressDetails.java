package com.penanttech.pff.model.external.bre;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDetails {

	@JsonCreator
	public AddressDetails() {

	}

	@JsonProperty("element")
	private List<AddressElement> addElement;

	public List<AddressElement> getAddElement() {
		return addElement;
	}

	public void setAddElement(List<AddressElement> addElement) {
		this.addElement = addElement;
	}

}
