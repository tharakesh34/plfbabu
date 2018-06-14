package com.pennanttech.niyogin.criff.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "suitFiledStatus", "dateOfSuit", "wilfulDefaulter", "suitAmount", "wilfulDefaultAsOn" })
@XmlRootElement(name = "SUIT-FILED-AND-WILFUL-DEFAULTS")
@XmlAccessorType(XmlAccessType.FIELD)
public class SuitfiledAndWilful implements Serializable {

	private static final long serialVersionUID = -1312467423645883549L;

	@XmlElement(name = "SUIT-FILED-STATUS")
	private String	suitFiledStatus;

	@XmlElement(name = "DATE-OF-SUIT")
	private Date	dateOfSuit;

	@XmlElement(name = "WILFUL-DEFAULTER")
	private String	wilfulDefaulter;

	@XmlElement(name = "SUIT-AMOUNT")
	private String	suitAmount;

	@XmlElement(name = "WILFUL-DEFAULT-AS-ON")
	private Date	wilfulDefaultAsOn;

	public String getSuitFiledStatus() {
		return suitFiledStatus;
	}

	public void setSuitFiledStatus(String suitFiledStatus) {
		this.suitFiledStatus = suitFiledStatus;
	}

	public Date getDateOfSuit() {
		return dateOfSuit;
	}

	public void setDateOfSuit(Date dateOfSuit) {
		this.dateOfSuit = dateOfSuit;
	}

	public String getWilfulDefaulter() {
		return wilfulDefaulter;
	}

	public void setWilfulDefaulter(String wilfulDefaulter) {
		this.wilfulDefaulter = wilfulDefaulter;
	}

	public String getSuitAmount() {
		return suitAmount;
	}

	public void setSuitAmount(String suitAmount) {
		this.suitAmount = suitAmount;
	}

	public Date getWilfulDefaultAsOn() {
		return wilfulDefaultAsOn;
	}

	public void setWilfulDefaultAsOn(Date wilfulDefaultAsOn) {
		this.wilfulDefaultAsOn = wilfulDefaultAsOn;
	}

	@Override
	public String toString() {
		return "SuitfiledAndWilful [suitFiledStatus=" + suitFiledStatus + ", dateOfSuit=" + dateOfSuit
				+ ", wilfulDefaulter=" + wilfulDefaulter + ", suitAmount=" + suitAmount + ", wilfulDefaultAsOn="
				+ wilfulDefaultAsOn + "]";
	}

}
