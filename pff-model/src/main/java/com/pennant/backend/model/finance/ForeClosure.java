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
	private BigDecimal			accuredIntTillDate	= BigDecimal.ZERO;
	private BigDecimal			foreCloseAmount		= BigDecimal.ZERO;
	private BigDecimal			chargeAmount		= BigDecimal.ZERO;

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

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
}
