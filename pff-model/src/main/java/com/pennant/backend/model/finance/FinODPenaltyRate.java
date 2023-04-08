package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pff.core.RequestSource;

@XmlType(propOrder = { "applyODPenalty", "oDIncGrcDays", "oDGraceDays", "oDChargeType", "oDChargeCalOn",
		"oDChargeAmtOrPerc", "oDAllowWaiver", "oDMaxWaiverPerc", "extensionODGrcDays", "collecChrgCodeId",
		"collectionAmt" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinODPenaltyRate implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
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
	private BigDecimal oDMinCapAmount = BigDecimal.ZERO;
	private long logKey = 0;
	@XmlElement(name = "extensionODGrcDays")
	private int overDraftExtGraceDays;
	@XmlElement(name = "collecChrgCodeId")
	private long overDraftColChrgFeeType;
	@XmlElement(name = "collectionAmt")
	private BigDecimal overDraftColAmt = BigDecimal.ZERO;

	private String oDRuleCode;
	private boolean oDTDSReq;
	private RequestSource requestSource = RequestSource.UI;

	// API validation purpose only
	@SuppressWarnings("unused")
	private FinODPenaltyRate validateFinODPenaltyRate = this;
	private BigDecimal odMinAmount = BigDecimal.ZERO;

	public FinODPenaltyRate() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("requestSource");
		return excludeFields;
	}

	public FinODPenaltyRate copyEntity() {
		FinODPenaltyRate entity = new FinODPenaltyRate();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setFinEffectDate(this.finEffectDate);
		entity.setApplyODPenalty(this.applyODPenalty);
		entity.setODIncGrcDays(this.oDIncGrcDays);
		entity.setODChargeType(this.oDChargeType);
		entity.setODGraceDays(this.oDGraceDays);
		entity.setODChargeCalOn(this.oDChargeCalOn);
		entity.setODChargeAmtOrPerc(this.oDChargeAmtOrPerc);
		entity.setODAllowWaiver(this.oDAllowWaiver);
		entity.setODMaxWaiverPerc(this.oDMaxWaiverPerc);
		entity.setoDMinCapAmount(this.oDMinCapAmount);
		entity.setLogKey(this.logKey);
		entity.setODRuleCode(this.oDRuleCode);
		entity.setoDTDSReq(this.oDTDSReq);
		entity.setOverDraftExtGraceDays(this.overDraftExtGraceDays);
		entity.setOverDraftColChrgFeeType(this.overDraftColChrgFeeType);
		entity.setOverDraftColAmt(this.overDraftColAmt);
		entity.setOdMinAmount(this.odMinAmount);
		return entity;
	}

	public long getId() {
		return logKey;
	}

	public void setId(long id) {
		this.logKey = id;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

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

	public long getLogKey() {
		return getId();
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}

	public String getODRuleCode() {
		return oDRuleCode;
	}

	public void setODRuleCode(String ODRuleCode) {
		this.oDRuleCode = ODRuleCode;
	}

	public BigDecimal getoDMinCapAmount() {
		return oDMinCapAmount;
	}

	public void setoDMinCapAmount(BigDecimal oDMinCapAmount) {
		this.oDMinCapAmount = oDMinCapAmount;
	}

	public boolean isoDTDSReq() {
		return oDTDSReq;
	}

	public void setoDTDSReq(boolean oDTDSReq) {
		this.oDTDSReq = oDTDSReq;
	}

	public int getOverDraftExtGraceDays() {
		return overDraftExtGraceDays;
	}

	public void setOverDraftExtGraceDays(int overDraftExtGraceDays) {
		this.overDraftExtGraceDays = overDraftExtGraceDays;
	}

	public long getOverDraftColChrgFeeType() {
		return overDraftColChrgFeeType;
	}

	public void setOverDraftColChrgFeeType(long overDraftColChrgFeeType) {
		this.overDraftColChrgFeeType = overDraftColChrgFeeType;
	}

	public BigDecimal getOverDraftColAmt() {
		return overDraftColAmt;
	}

	public void setOverDraftColAmt(BigDecimal overDraftColAmt) {
		this.overDraftColAmt = overDraftColAmt;
	}

	public RequestSource getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(RequestSource requestSource) {
		this.requestSource = requestSource;
	}

	public BigDecimal getOdMinAmount() {
		return odMinAmount;
	}

	public void setOdMinAmount(BigDecimal odMinAmount) {
		this.odMinAmount = odMinAmount;
	}

}
