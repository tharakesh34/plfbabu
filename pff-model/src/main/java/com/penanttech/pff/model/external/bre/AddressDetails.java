package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDetails {

	@JsonCreator
	public AddressDetails() {
	    super();
	}

	@XmlElement(name = "element")
	private List<AddressElement> addElement;

	public List<AddressElement> getAddElement() {
		return addElement;
	}

	public void setAddElement(List<AddressElement> addElement) {
		this.addElement = addElement;
	}

}
