package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeClosureLetter implements Serializable {
	private static final long serialVersionUID = 1051334309884378798L;

	private Date valueDate;
	private BigDecimal outStandPrincipal = BigDecimal.ZERO;
	private BigDecimal pricipleAmount = BigDecimal.ZERO;
	private BigDecimal futurePricipleAmount = BigDecimal.ZERO;
	private BigDecimal interestAmount = BigDecimal.ZERO;
	private BigDecimal futureInterestAmount = BigDecimal.ZERO;
	private BigDecimal accuredIntTillDate = BigDecimal.ZERO;
	private BigDecimal foreCloseAmount = BigDecimal.ZERO;
	private BigDecimal excessAmount = BigDecimal.ZERO;
	private BigDecimal emiInAdvance = BigDecimal.ZERO;
	private BigDecimal chargeAmount = BigDecimal.ZERO;
	private BigDecimal bounceCharge = BigDecimal.ZERO;
	private BigDecimal totalLPIAmount = BigDecimal.ZERO;
	private BigDecimal receivableAdviceAmt = BigDecimal.ZERO;

	public ForeClosureLetter() {
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

	public BigDecimal getOutStandPrincipal() {
		return outStandPrincipal;
	}

	public void setOutStandPrincipal(BigDecimal outStandPrincipal) {
		this.outStandPrincipal = outStandPrincipal;
	}

	public BigDecimal getPricipleAmount() {
		return pricipleAmount;
	}

	public void setPricipleAmount(BigDecimal pricipleAmount) {
		this.pricipleAmount = pricipleAmount;
	}

	public BigDecimal getFuturePricipleAmount() {
		return futurePricipleAmount;
	}

	public void setFuturePricipleAmount(BigDecimal futurePricipleAmount) {
		this.futurePricipleAmount = futurePricipleAmount;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}

	public BigDecimal getFutureInterestAmount() {
		return futureInterestAmount;
	}

	public void setFutureInterestAmount(BigDecimal futureInterestAmount) {
		this.futureInterestAmount = futureInterestAmount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getBounceCharge() {
		return bounceCharge;
	}

	public void setBounceCharge(BigDecimal bounceCharge) {
		this.bounceCharge = bounceCharge;
	}

	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	public BigDecimal getEmiInAdvance() {
		return emiInAdvance;
	}

	public void setEmiInAdvance(BigDecimal emiInAdvance) {
		this.emiInAdvance = emiInAdvance;
	}

	public BigDecimal getTotalLPIAmount() {
		return totalLPIAmount;
	}

	public void setTotalLPIAmount(BigDecimal totalLPIAmount) {
		this.totalLPIAmount = totalLPIAmount;
	}

	public BigDecimal getReceivableAdviceAmt() {
		return receivableAdviceAmt;
	}

	public void setReceivableAdviceAmt(BigDecimal receivableAdviceAmt) {
		this.receivableAdviceAmt = receivableAdviceAmt;
	}

}