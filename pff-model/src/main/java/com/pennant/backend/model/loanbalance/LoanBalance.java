package com.pennant.backend.model.loanbalance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

@XmlRootElement(name = "loanBalance")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanBalance {

	@XmlElement
	private String finReference;
	@XmlElement
	private Integer totalInstalments;
	@XmlElement
	private Integer advanceInstalments;
	@XmlElement
	private Date dueDate;
	@XmlElement
	private String repayMethod;
	@XmlElement
	private Integer instalmentsPaid;
	@XmlElement
	private BigDecimal overDueInstallment;
	@XmlElement
	private BigDecimal outstandingPri;
	@XmlElement
	private BigDecimal overDueCharges;
	@XmlElement
	private BigDecimal chequeBounceCharges;
	@XmlElement
	private BigDecimal excessMoney;
	private List<FinanceScheduleDetail> finScheduleDetailList;
	@XmlElement
	private WSReturnStatus returnStatus;

	public LoanBalance() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Integer getTotalInstalments() {
		return totalInstalments;
	}

	public void setTotalInstalments(Integer totalInstalments) {
		this.totalInstalments = totalInstalments;
	}

	public Integer getAdvanceInstalments() {
		return advanceInstalments;
	}

	public void setAdvanceInstalments(Integer advanceInstalments) {
		this.advanceInstalments = advanceInstalments;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getRepayMethod() {
		return repayMethod;
	}

	public void setRepayMethod(String repayMethod) {
		this.repayMethod = repayMethod;
	}

	public Integer getInstalmentsPaid() {
		return instalmentsPaid;
	}

	public void setInstalmentsPaid(Integer instalmentsPaid) {
		this.instalmentsPaid = instalmentsPaid;
	}

	public BigDecimal getOverDueInstallment() {
		return overDueInstallment;
	}

	public void setOverDueInstallment(BigDecimal overDueInstallment) {
		this.overDueInstallment = overDueInstallment;
	}

	public BigDecimal getOutstandingPri() {
		return outstandingPri;
	}

	public void setOutstandingPri(BigDecimal outstandingPri) {
		this.outstandingPri = outstandingPri;
	}

	public BigDecimal getOverDueCharges() {
		return overDueCharges;
	}

	public void setOverDueCharges(BigDecimal overDueCharges) {
		this.overDueCharges = overDueCharges;
	}

	public BigDecimal getChequeBounceCharges() {
		return chequeBounceCharges;
	}

	public void setChequeBounceCharges(BigDecimal chequeBounceCharges) {
		this.chequeBounceCharges = chequeBounceCharges;
	}

	public BigDecimal getExcessMoney() {
		return excessMoney;
	}

	public void setExcessMoney(BigDecimal excessMoney) {
		this.excessMoney = excessMoney;
	}

	public List<FinanceScheduleDetail> getFinScheduleDetailList() {
		return finScheduleDetailList;
	}

	public void setFinScheduleDetailList(List<FinanceScheduleDetail> finScheduleDetailList) {
		this.finScheduleDetailList = finScheduleDetailList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
