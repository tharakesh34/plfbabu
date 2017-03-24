package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "applyODPenalty", "oDIncGrcDays", "oDGraceDays", "oDChargeType", "oDChargeCalOn",
		"oDChargeAmtOrPerc", "oDAllowWaiver", "oDMaxWaiverPerc" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinODPenaltyRate implements java.io.Serializable {

	private static final long serialVersionUID = 279834448061406028L;

	private String finReference;
	private Date finEffectDate;
	@XmlElement
	private boolean applyODPenalty;
	@XmlElement(name = "odIncGrcDays")
	private boolean oDIncGrcDays;
	@XmlElement(name = "odChargeType")
	private String oDChargeType;
	@XmlElement(name = "odGraceDays")
	private int oDGraceDays;
	@XmlElement(name = "odChargeCalOn")
	private String oDChargeCalOn;
	@XmlElement(name = "odChargeAmtOrPerc")
	private BigDecimal oDChargeAmtOrPerc = BigDecimal.ZERO;
	@XmlElement(name = "odAllowWaiver")
	private boolean oDAllowWaiver;
	@XmlElement(name = "odMaxWaiverPerc")
	private BigDecimal oDMaxWaiverPerc = BigDecimal.ZERO;
	
	// API validation purpose only
	@SuppressWarnings("unused")
	private FinODPenaltyRate validateFinODPenaltyRate = this;

	public FinODPenaltyRate() {

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

	public Date getFinEffectDate() {
		return finEffectDate;
	}

	public void setFinEffectDate(Date finEffectDate) {
		this.finEffectDate = finEffectDate;
	}

	public boolean isApplyODPenalty() {
		return applyODPenalty;
	}

	public void setApplyODPenalty(boolean applyODPenalty) {
		this.applyODPenalty = applyODPenalty;
	}

	public boolean isODIncGrcDays() {
		return oDIncGrcDays;
	}

	public void setODIncGrcDays(boolean oDIncGrcDays) {
		this.oDIncGrcDays = oDIncGrcDays;
	}

	public String getODChargeType() {
		return oDChargeType;
	}

	public void setODChargeType(String oDChargeType) {
		this.oDChargeType = oDChargeType;
	}

	public int getODGraceDays() {
		return oDGraceDays;
	}

	public void setODGraceDays(int oDGraceDays) {
		this.oDGraceDays = oDGraceDays;
	}

	public String getODChargeCalOn() {
		return oDChargeCalOn;
	}

	public void setODChargeCalOn(String oDChargeCalOn) {
		this.oDChargeCalOn = oDChargeCalOn;
	}

	public BigDecimal getODChargeAmtOrPerc() {
		return oDChargeAmtOrPerc;
	}

	public void setODChargeAmtOrPerc(BigDecimal oDChargeAmtOrPerc) {
		this.oDChargeAmtOrPerc = oDChargeAmtOrPerc;
	}

	public boolean isODAllowWaiver() {
		return oDAllowWaiver;
	}

	public void setODAllowWaiver(boolean oDAllowWaiver) {
		this.oDAllowWaiver = oDAllowWaiver;
	}

	public BigDecimal getODMaxWaiverPerc() {
		return oDMaxWaiverPerc;
	}

	public void setODMaxWaiverPerc(BigDecimal oDMaxWaiverPerc) {
		this.oDMaxWaiverPerc = oDMaxWaiverPerc;
	}

}
