package com.pennanttech.pff.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "DATA")
@XmlAccessorType(XmlAccessType.FIELD)
public class PMAYResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	@JsonProperty("ID")
	private PMAYDetailsRespData pmayDetailsRespData;

	public PMAYDetailsRespData getPmayDetailsRespData() {
		return pmayDetailsRespData;
	}

	public void setPmayDetailsRespData(PMAYDetailsRespData pmayDetailsRespData) {
		this.pmayDetailsRespData = pmayDetailsRespData;
	}

}
