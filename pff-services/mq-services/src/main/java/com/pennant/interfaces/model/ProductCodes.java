package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProductCodes")
public class ProductCodes {
	
	private String productCode;
	private String limitRef;

	@XmlElement(name="ProductCode")
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getLimitRef() {
		return limitRef;
	}
	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}
	
}
