package com.pennanttech.ws.model.customer;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustCardSales;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "custCardSales", "Id", "returnStatus" })
public class CustomerCardSaleInfoDetails {
	@XmlElement
	private String cif;

	@XmlElementWrapper(name = "CustCardSales")
	@XmlElement
	private CustCardSales CustCardSales;

	@XmlElement
	private long Id = Long.MIN_VALUE;

	@XmlElement
	private WSReturnStatus returnStatus;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public CustCardSales getCustCardSales() {
		return CustCardSales;
	}

	public void setCustCardSales(CustCardSales custCardSales) {
		CustCardSales = custCardSales;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
