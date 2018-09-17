package com.pennanttech.model.interfacemapping;

import java.io.Serializable;

public class InterfaceMappingDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private String interfaceValue;
	private String interfaceSequence;

	public String getInterfaceValue() {
		return interfaceValue;
	}

	public void setInterfaceValue(String interfaceValue) {
		this.interfaceValue = interfaceValue;
	}

	public String getInterfaceSequence() {
		return interfaceSequence;
	}

	public void setInterfaceSequence(String interfaceSequence) {
		this.interfaceSequence = interfaceSequence;
	}

}
