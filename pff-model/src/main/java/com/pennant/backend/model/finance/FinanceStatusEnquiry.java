package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finReference", "outStandPrincipal", "dueBucket", "finStatus" })
@XmlRootElement(name = "finance")
@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceStatusEnquiry implements Serializable {
	private static final long serialVersionUID = 1L;

	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private BigDecimal outStandPrincipal;
	@XmlElement(name = "DPD")
	private int curODDays = 0;
	@XmlElement
	private String closingStatus;
	@XmlElement
	private String status;

	public FinanceStatusEnquiry() {
		super();
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

	public BigDecimal getOutStandPrincipal() {
		return outStandPrincipal;
	}

	public void setOutStandPrincipal(BigDecimal outStandPrincipal) {
		this.outStandPrincipal = outStandPrincipal;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCurODDays() {
		return curODDays;
	}

	public void setCurODDays(int curODDays) {
		this.curODDays = curODDays;
	}

}
