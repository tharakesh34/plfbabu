package com.pennanttech.pff.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
