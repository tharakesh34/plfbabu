package com.pennanttech.niyogin.experian.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "segmentCode", "balanceAmt" })
@XmlRootElement(name = "HDETAILS")
@XmlAccessorType(XmlAccessType.FIELD)
public class HDetailsResponse implements Serializable {

	private static final long serialVersionUID = -3995637881465570183L;

	@XmlElement(name = "SegmentCode")
	private String	segmentCode;

	@XmlElement(name = "BalanceAmt")
	private String	balanceAmt;

	public String getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(String balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}
}
