package com.pennanttech.niyogin.hunter.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class HunterRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long identifier;
	@XmlElement(name = "product_code")
	private String productCode;
	@XmlElement(name = "app_dte")
	private Date appDate;
	private Org org;

	public long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	@Override
	public String toString() {
		return "HunterRequest [identifier=" + identifier + ", productCode=" + productCode + ", appDate=" + appDate
				+ ", org=" + org + "]";
	}

}
