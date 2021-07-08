package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeClosure implements Serializable {
	private static final long serialVersionUID = 1051334309884378798L;

	private Date valueDate;
	private BigDecimal accuredIntTillDate = BigDecimal.ZERO;
	private BigDecimal foreCloseAmount = BigDecimal.ZERO;
	private BigDecimal chargeAmount = BigDecimal.ZERO;
	private BigDecimal bounceCharge = BigDecimal.ZERO;
	private BigDecimal LPIAmount = BigDecimal.ZERO;
	private BigDecimal receivableADFee = BigDecimal.ZERO;

	public ForeClosure() {
		super();
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

	public BigDecimal getBounceCharge() {
		return bounceCharge;
	}

	public void setBounceCharge(BigDecimal bounceCharge) {
		this.bounceCharge = bounceCharge;
	}

	public BigDecimal getLPIAmount() {
		return LPIAmount;
	}

	public void setLPIAmount(BigDecimal lPIAmount) {
		LPIAmount = lPIAmount;
	}

	public BigDecimal getReceivableADFee() {
		return receivableADFee;
	}

	public void setReceivableADFee(BigDecimal receivableADFee) {
		this.receivableADFee = receivableADFee;
	}
}
