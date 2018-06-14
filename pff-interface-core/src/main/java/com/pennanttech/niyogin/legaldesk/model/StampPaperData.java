package com.pennanttech.niyogin.legaldesk.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "firstParty", "firstPartyAddress", "stampAmount", "stampDutyPaidBy" })
@XmlRootElement(name = "stamp_paper_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class StampPaperData {

	@XmlElement(name = "first_party")
	private String			firstParty;

	@XmlElement(name = "first_party_address")
	private PartyAddress	firstPartyAddress;

	@XmlElement(name = "stamp_amount")
	private BigDecimal		stampAmount	= BigDecimal.ZERO;

	@XmlElement(name = "stamp_duty_paid_by")
	private String			stampDutyPaidBy;

	public String getFirstParty() {
		return firstParty;
	}

	public void setFirstParty(String firstParty) {
		this.firstParty = firstParty;
	}

	public PartyAddress getFirstPartyAddress() {
		return firstPartyAddress;
	}

	public void setFirstPartyAddress(PartyAddress firstPartyAddress) {
		this.firstPartyAddress = firstPartyAddress;
	}

	public BigDecimal getStampAmount() {
		return stampAmount;
	}

	public void setStampAmount(BigDecimal stampAmount) {
		this.stampAmount = stampAmount;
	}

	public String getStampDutyPaidBy() {
		return stampDutyPaidBy;
	}

	public void setStampDutyPaidBy(String stampDutyPaidBy) {
		this.stampDutyPaidBy = stampDutyPaidBy;
	}

}
