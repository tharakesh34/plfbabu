package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "providerCode", "providerDesc", "providerRate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class InsuranceProvider implements Serializable {

	private static final long serialVersionUID = -2552074747844730033L;

	public InsuranceProvider() {
		
	}
	
	private String providerCode;
	private String providerDesc;
	private BigDecimal providerRate = BigDecimal.ZERO;

	
	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getProviderDesc() {
		return providerDesc;
	}

	public void setProviderDesc(String providerDesc) {
		this.providerDesc = providerDesc;
	}

	public BigDecimal getProviderRate() {
		return providerRate;
	}

	public void setProviderRate(BigDecimal providerRate) {
		this.providerRate = providerRate;
	}
}
