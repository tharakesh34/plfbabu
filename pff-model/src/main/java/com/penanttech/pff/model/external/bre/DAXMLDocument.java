package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DAXMLDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	public DAXMLDocument() {
		// TODO Auto-generated constructor stub
	}

	@XmlElement(name = "OCONTROL")
	OControl oControl;

	public OControl getoControl() {
		return oControl;
	}

	public void setoControl(OControl oControl) {
		this.oControl = oControl;
	}

	@Override
	public String toString() {
		return "DAXMLDocument [oControl=" + oControl + "]";
	}
}