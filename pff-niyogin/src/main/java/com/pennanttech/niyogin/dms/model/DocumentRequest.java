package com.pennanttech.niyogin.dms.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "document")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentRequest implements Serializable {
	private static final long serialVersionUID = -4272720849985024527L;

	public DocumentRequest() {
		super();
	}

	@XmlElement
	String docRefId;

	public String getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(String docRefId) {
		this.docRefId = docRefId;
	}
}
