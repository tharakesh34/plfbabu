package com.penanttech.pff.model.external.bre;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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
