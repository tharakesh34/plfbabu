package com.pennant.backend.model.configuration;

import java.io.Serializable;

public class VasCustomer implements Serializable {
	private static final long serialVersionUID = 1L;

	private long customerId;
	private String custCIF;
	private String custShrtName;

	public VasCustomer copyEntity() {
		VasCustomer entity = new VasCustomer();
		entity.setCustomerId(this.customerId);
		entity.setCustCIF(this.custCIF);
		entity.setCustShrtName(this.custShrtName);
		return entity;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

}
