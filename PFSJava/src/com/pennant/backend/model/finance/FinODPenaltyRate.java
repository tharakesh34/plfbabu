package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class FinODPenaltyRate implements java.io.Serializable {

    private static final long serialVersionUID = 279834448061406028L;
	
    private String finReference;
	private Date finEffectDate;
	private boolean applyODPenalty;
	private boolean oDIncGrcDays;
	private String oDChargeType;
	private int oDGraceDays;
	private String oDChargeCalOn;
	private BigDecimal oDChargeAmtOrPerc;
	private boolean oDAllowWaiver;
	private BigDecimal oDMaxWaiverPerc;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
	
	// Overridden Equals method to handle the comparison
	public boolean equals(FinODPenaltyRate penaltyRate) {
		return getFinReference() == penaltyRate.getFinReference();
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinODPenaltyRate) {
			FinODPenaltyRate penaltyRate = (FinODPenaltyRate) obj;
			return equals(penaltyRate);
		}
		return false;
	}

}
