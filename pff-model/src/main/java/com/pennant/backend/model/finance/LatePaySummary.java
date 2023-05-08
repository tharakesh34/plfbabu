package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class LatePaySummary implements Serializable {

	private static final long serialVersionUID = -5722811453434523809L;

	private Date schDate;
	private Date tillDate;
	private BigDecimal schdPri = BigDecimal.ZERO;
	private BigDecimal schdPft = BigDecimal.ZERO;
	private BigDecimal lpiAmount = BigDecimal.ZERO;
	private BigDecimal lppAmount = BigDecimal.ZERO;
	private BigDecimal priPaid = BigDecimal.ZERO;
	private BigDecimal pftPaid = BigDecimal.ZERO;
	private BigDecimal lpiPaid = BigDecimal.ZERO;
	private BigDecimal lppPaid = BigDecimal.ZERO;
	private BigDecimal priWaived = BigDecimal.ZERO;
	private BigDecimal pftWaived = BigDecimal.ZERO;
	private BigDecimal lpiWaived = BigDecimal.ZERO;
	private BigDecimal lppWaived = BigDecimal.ZERO;
	private BigDecimal priBal = BigDecimal.ZERO;
	private BigDecimal pftBal = BigDecimal.ZERO;
	private BigDecimal lpiBal = BigDecimal.ZERO;
	private BigDecimal lppBal = BigDecimal.ZERO;

	public LatePaySummary() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public Date getTillDate() {
		return tillDate;
	}

	public void setTillDate(Date tillDate) {
		this.tillDate = tillDate;
	}

	public BigDecimal getSchdPri() {
		return schdPri;
	}

	public void setSchdPri(BigDecimal schdPri) {
		this.schdPri = schdPri;
	}

	public BigDecimal getSchdPft() {
		return schdPft;
	}

	public void setSchdPft(BigDecimal schdPft) {
		this.schdPft = schdPft;
	}

	public BigDecimal getLpiAmount() {
		return lpiAmount;
	}

	public void setLpiAmount(BigDecimal lpiAmount) {
		this.lpiAmount = lpiAmount;
	}

	public BigDecimal getLppAmount() {
		return lppAmount;
	}

	public void setLppAmount(BigDecimal lppAmount) {
		this.lppAmount = lppAmount;
	}

	public BigDecimal getPriPaid() {
		return priPaid;
	}

	public void setPriPaid(BigDecimal priPaid) {
		this.priPaid = priPaid;
	}

	public BigDecimal getPftPaid() {
		return pftPaid;
	}

	public void setPftPaid(BigDecimal pftPaid) {
		this.pftPaid = pftPaid;
	}

	public BigDecimal getLpiPaid() {
		return lpiPaid;
	}

	public void setLpiPaid(BigDecimal lpiPaid) {
		this.lpiPaid = lpiPaid;
	}

	public BigDecimal getLppPaid() {
		return lppPaid;
	}

	public void setLppPaid(BigDecimal lppPaid) {
		this.lppPaid = lppPaid;
	}

	public BigDecimal getPriWaived() {
		return priWaived;
	}

	public void setPriWaived(BigDecimal priWaived) {
		this.priWaived = priWaived;
	}

	public BigDecimal getPftWaived() {
		return pftWaived;
	}

	public void setPftWaived(BigDecimal pftWaived) {
		this.pftWaived = pftWaived;
	}

	public BigDecimal getLpiWaived() {
		return lpiWaived;
	}

	public void setLpiWaived(BigDecimal lpiWaived) {
		this.lpiWaived = lpiWaived;
	}

	public BigDecimal getLppWaived() {
		return lppWaived;
	}

	public void setLppWaived(BigDecimal lppWaived) {
		this.lppWaived = lppWaived;
	}

	public BigDecimal getPriBal() {
		return priBal;
	}

	public void setPriBal(BigDecimal priBal) {
		this.priBal = priBal;
	}

	public BigDecimal getPftBal() {
		return pftBal;
	}

	public void setPftBal(BigDecimal pftBal) {
		this.pftBal = pftBal;
	}

	public BigDecimal getLpiBal() {
		return lpiBal;
	}

	public void setLpiBal(BigDecimal lpiBal) {
		this.lpiBal = lpiBal;
	}

	public BigDecimal getLppBal() {
		return lppBal;
	}

	public void setLppBal(BigDecimal lppBal) {
		this.lppBal = lppBal;
	}

}
