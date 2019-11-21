package com.pennanttech.ws.model.customer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustDedupRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private Object value;
	private String name;

	public CustDedupRequest() {
		super();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
