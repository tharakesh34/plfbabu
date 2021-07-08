package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "pastduePftCalMthd", "pastduePftMargin" })
@XmlAccessorType(XmlAccessType.FIELD)
public class OverdueProfitDetail implements Serializable {

	private static final long serialVersionUID = -2555526236766955537L;

	public OverdueProfitDetail() {

	}

	private String pastduePftCalMthd;
	private BigDecimal pastduePftMargin = BigDecimal.ZERO;

	public String getPastduePftCalMthd() {
		return pastduePftCalMthd;
	}

	public void setPastduePftCalMthd(String pastduePftCalMthd) {
		this.pastduePftCalMthd = pastduePftCalMthd;
	}

	public BigDecimal getPastduePftMargin() {
		return pastduePftMargin;
	}

	public void setPastduePftMargin(BigDecimal pastduePftMargin) {
		this.pastduePftMargin = pastduePftMargin;
	}
}
