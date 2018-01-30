package com.pennanttech.niyogin.bre.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AMOUNTOFCREDITTRANSACTIONS")
@XmlAccessorType(XmlAccessType.FIELD)
public class AmtOfCreditTransactions {
	@XmlElement(name = "item")
	private List<BreItem> items;

	public List<BreItem> getItems() {
		return items;
	}

	public void setItems(List<BreItem> items) {
		this.items = items;
	}
}
