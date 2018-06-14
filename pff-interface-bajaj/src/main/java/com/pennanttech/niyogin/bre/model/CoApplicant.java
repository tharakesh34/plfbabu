package com.pennanttech.niyogin.bre.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "coAppElements" })
@XmlRootElement(name = "COAPPLICANT")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoApplicant {
	@XmlElement(name = "element")
	private List<CoAppElement> coAppElements;

	public List<CoAppElement> getCoAppElements() {
		return coAppElements;
	}

	public void setCoAppElements(List<CoAppElement> coAppElements) {
		this.coAppElements = coAppElements;
	}

}
