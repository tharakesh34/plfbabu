package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
	@XmlElement(name = "POS")
	private BigDecimal pos;
	@XmlElement(name = "Principal Overdue")
	private BigDecimal odprincipal;
	@XmlElement(name = "Interest Overdue")
	private BigDecimal odprofit;
	@XmlElement(name = "Total Charges Overdue")
	private BigDecimal totChagrOved;
	@XmlElement(name = "Writeoff Amount")
	private BigDecimal writeOffAmt;
	@XmlElement(name = "Excess Amount")
	private BigDecimal excessAmt;
	@XmlElement(name = "Foreclosure Amount")
	private BigDecimal foreclosureAmt;
	@XmlElement(name = "Loan Maturity Date")
	private Date maturityDate;
	@XmlElement(name = "PenaltyDue")
	private BigDecimal penaltyDue;

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

	public BigDecimal getPos() {
		return pos;
	}

	public void setPos(BigDecimal pos) {
		this.pos = pos;
	}

	public BigDecimal getOdprincipal() {
		return odprincipal;
	}

	public void setOdprincipal(BigDecimal odprincipal) {
		this.odprincipal = odprincipal;
	}

	public BigDecimal getOdprofit() {
		return odprofit;
	}

	public void setOdprofit(BigDecimal odprofit) {
		this.odprofit = odprofit;
	}

	public BigDecimal getTotChagrOved() {
		return totChagrOved;
	}

	public void setTotChagrOved(BigDecimal totChagrOved) {
		this.totChagrOved = totChagrOved;
	}

	public BigDecimal getWriteOffAmt() {
		return writeOffAmt;
	}

	public void setWriteOffAmt(BigDecimal writeOffAmt) {
		this.writeOffAmt = writeOffAmt;
	}

	public BigDecimal getExcessAmt() {
		return excessAmt;
	}

	public void setExcessAmt(BigDecimal excessAmt) {
		this.excessAmt = excessAmt;
	}

	public BigDecimal getForeclosureAmt() {
		return foreclosureAmt;
	}

	public void setForeclosureAmt(BigDecimal foreclosureAmt) {
		this.foreclosureAmt = foreclosureAmt;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getPenaltyDue() {
		return penaltyDue;
	}

	public void setPenaltyDue(BigDecimal penaltyDue) {
		this.penaltyDue = penaltyDue;
	}
}
