package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProjectedAccrual implements Serializable {
	private static final long	serialVersionUID	= 7690656031696834080L;
	
	private String				finReference;
	private String				finType;
	private Date				accruedOn;
	private Date				schdDate;
	private BigDecimal			schdPri				= BigDecimal.ZERO;
	private BigDecimal			schdPft				= BigDecimal.ZERO;
	private BigDecimal			schdTot				= BigDecimal.ZERO;
	private BigDecimal			pftAmz				= BigDecimal.ZERO;
	private BigDecimal			pftAccrued			= BigDecimal.ZERO;
	private BigDecimal			cumulativeAccrued	= BigDecimal.ZERO;

	public ProjectedAccrual() {

	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public Date getAccruedOn() {
		return accruedOn;
	}

	public void setAccruedOn(Date accruedOn) {
		this.accruedOn = accruedOn;
	}

	public Date getSchdDate() {
		return schdDate;
	}

	public void setSchdDate(Date schdDate) {
		this.schdDate = schdDate;
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

	public BigDecimal getSchdTot() {
		return schdTot;
	}

	public void setSchdTot(BigDecimal schdTot) {
		this.schdTot = schdTot;
	}

	public BigDecimal getPftAmz() {
		return pftAmz;
	}

	public void setPftAmz(BigDecimal pftAmz) {
		this.pftAmz = pftAmz;
	}

	public BigDecimal getPftAccrued() {
		return pftAccrued;
	}

	public void setPftAccrued(BigDecimal pftAccrued) {
		this.pftAccrued = pftAccrued;
	}

	public BigDecimal getCumulativeAccrued() {
		return cumulativeAccrued;
	}

	public void setCumulativeAccrued(BigDecimal cumulativeAccrued) {
		this.cumulativeAccrued = cumulativeAccrued;
	}




}
