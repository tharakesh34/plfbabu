package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AMOUNTOFCREDITTRANSACTIONS")
@XmlAccessorType(XmlAccessType.FIELD)
public class AmtOfCreditTransactions {
	@XmlElement(name = "item")
	private List<BigDecimal> items;

	public List<BigDecimal> getItems() {
		return items;
	}

	public void setItems(List<BigDecimal> items) {
		this.items = items;
	}

}
