package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeClosure implements Serializable {
	private static final long	serialVersionUID	= 1051334309884378798L;

	private Date				valueDate;
	private BigDecimal				accuredIntTillDate;
	private BigDecimal			foreCloseAmount;

	public ForeClosure() {

	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getAccuredIntTillDate() {
		return accuredIntTillDate;
	}

	public void setAccuredIntTillDate(BigDecimal accuredIntTillDate) {
		this.accuredIntTillDate = accuredIntTillDate;
	}

	public BigDecimal getForeCloseAmount() {
		return foreCloseAmount;
	}

	public void setForeCloseAmount(BigDecimal foreCloseAmount) {
		this.foreCloseAmount = foreCloseAmount;
	}
}
