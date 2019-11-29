package com.pennanttech.ws.model.miscellaneous;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoanTypeMiscRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private String finType;
	@XmlElement
	private String finReference;
	@XmlElement
	private String stage;

	public LoanTypeMiscRequest() {
		super();
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

}
