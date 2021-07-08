package com.pennanttech.pff.model;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "DATA")
@XmlAccessorType(XmlAccessType.FIELD)
public class PMAYResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "ID")
	private PMAYDetailsRespData pmayDetailsRespData;

	public PMAYDetailsRespData getPmayDetailsRespData() {
		return pmayDetailsRespData;
	}

	public void setPmayDetailsRespData(PMAYDetailsRespData pmayDetailsRespData) {
		this.pmayDetailsRespData = pmayDetailsRespData;
	}

}
