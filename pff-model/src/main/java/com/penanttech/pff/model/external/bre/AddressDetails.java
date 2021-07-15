package com.penanttech.pff.model.external.bre;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDetails {

	@JsonCreator
	public AddressDetails() {

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
