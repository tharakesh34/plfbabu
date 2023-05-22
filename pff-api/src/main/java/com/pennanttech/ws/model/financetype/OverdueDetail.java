package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "applyODPenalty", "oDIncGrcDays", "oDChargeCalOn", "oDGraceDays", "oDChargeType",
		"oDChargeAmtOrPerc", "oDAllowWaiver", "oDMaxWaiverPerc" })
@XmlAccessorType(XmlAccessType.FIELD)
public class OverdueDetail implements Serializable {

	private static final long serialVersionUID = 2302891876161608684L;

	public OverdueDetail() {
	    super();
	}

	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	private String oDChargeCalOn;
	private int oDGraceDays;
	private String oDChargeType;
	private BigDecimal oDChargeAmtOrPerc = BigDecimal.ZERO;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc = BigDecimal.ZERO;

	// private String pastduePftCalMthd;
	// private BigDecimal pastduePftMargin = BigDecimal.ZERO;

	public boolean isApplyODPenalty() {
		return applyODPenalty;
	}

	public void setApplyODPenalty(boolean applyODPenalty) {
		this.applyODPenalty = applyODPenalty;
	}

	public boolean isoDIncGrcDays() {
		return oDIncGrcDays;
	}

	public void setoDIncGrcDays(boolean oDIncGrcDays) {
		this.oDIncGrcDays = oDIncGrcDays;
	}

	public String getoDChargeCalOn() {
		return oDChargeCalOn;
	}

	public void setoDChargeCalOn(String oDChargeCalOn) {
		this.oDChargeCalOn = oDChargeCalOn;
	}

	public int getoDGraceDays() {
		return oDGraceDays;
	}

	public void setoDGraceDays(int oDGraceDays) {
		this.oDGraceDays = oDGraceDays;
	}

	public String getoDChargeType() {
		return oDChargeType;
	}

	public void setoDChargeType(String oDChargeType) {
		this.oDChargeType = oDChargeType;
	}

	public BigDecimal getoDChargeAmtOrPerc() {
		return oDChargeAmtOrPerc;
	}

	public void setoDChargeAmtOrPerc(BigDecimal oDChargeAmtOrPerc) {
		this.oDChargeAmtOrPerc = oDChargeAmtOrPerc;
	}

	public boolean isoDAllowWaiver() {
		return oDAllowWaiver;
	}

	public void setoDAllowWaiver(boolean oDAllowWaiver) {
		this.oDAllowWaiver = oDAllowWaiver;
	}

	public BigDecimal getoDMaxWaiverPerc() {
		return oDMaxWaiverPerc;
	}

	public void setoDMaxWaiverPerc(BigDecimal oDMaxWaiverPerc) {
		this.oDMaxWaiverPerc = oDMaxWaiverPerc;
	}
}
