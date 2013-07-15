package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class FinanceProfitDetail {

	private String finReference;
	private long custId = Long.MIN_VALUE;
	private String finBranch;
	private String finType;
	private Date lastMdfDate;
	private BigDecimal totalPftSchd = new BigDecimal(0);
	private BigDecimal totalPftCpz = new BigDecimal(0);
	private BigDecimal totalPftPaid = new BigDecimal(0);
	private BigDecimal totalPftBal = new BigDecimal(0);
	private BigDecimal totalPftPaidInAdv = new BigDecimal(0);
	private BigDecimal totalPriPaid = new BigDecimal(0);
	private BigDecimal totalPriBal = new BigDecimal(0);
	private BigDecimal tdSchdPft = new BigDecimal(0);
	private BigDecimal tdPftCpz = new BigDecimal(0);
	private BigDecimal tdSchdPftPaid = new BigDecimal(0);
	private BigDecimal tdSchdPftBal = new BigDecimal(0);
	private BigDecimal tdPftAccrued = new BigDecimal(0);
	private BigDecimal tdPftAccrueSusp = new BigDecimal(0);
	private BigDecimal tdPftAmortized = new BigDecimal(0);
	private BigDecimal tdPftAmortizedSusp = new BigDecimal(0);
	private BigDecimal tdSchdPri = new BigDecimal(0);
	private BigDecimal tdSchdPriPaid = new BigDecimal(0);
	private BigDecimal tdSchdPriBal = new BigDecimal(0);
	private BigDecimal acrTillLBD = new BigDecimal(0);
	private BigDecimal acrTillNBD = new BigDecimal(0);
	private BigDecimal acrTodayToNBD = new BigDecimal(0);
	private BigDecimal amzTillNBD = new BigDecimal(0);
	private BigDecimal amzTillLBD = new BigDecimal(0);
	private BigDecimal amzTodayToNBD = new BigDecimal(0);

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFinBranch() {
		return finBranch;
	}
	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public Date getLastMdfDate() {
		return lastMdfDate;
	}
	public void setLastMdfDate(Date lastMdfDate) {
		this.lastMdfDate = lastMdfDate;
	}

	public BigDecimal getTotalPftSchd() {
		return totalPftSchd;
	}
	public void setTotalPftSchd(BigDecimal totalPftSchd) {
		this.totalPftSchd = totalPftSchd;
	}

	public BigDecimal getTotalPftCpz() {
		return totalPftCpz;
	}
	public void setTotalPftCpz(BigDecimal totalPftCpz) {
		this.totalPftCpz = totalPftCpz;
	}

	public BigDecimal getTotalPftPaid() {
		return totalPftPaid;
	}
	public void setTotalPftPaid(BigDecimal totalPftPaid) {
		this.totalPftPaid = totalPftPaid;
	}

	public BigDecimal getTotalPftBal() {
		return totalPftBal;
	}
	public void setTotalPftBal(BigDecimal totalPftBal) {
		this.totalPftBal = totalPftBal;
	}

	public BigDecimal getTotalPftPaidInAdv() {
		return totalPftPaidInAdv;
	}
	public void setTotalPftPaidInAdv(BigDecimal totalPftPaidInAdv) {
		this.totalPftPaidInAdv = totalPftPaidInAdv;
	}

	public BigDecimal getTotalPriPaid() {
		return totalPriPaid;
	}
	public void setTotalPriPaid(BigDecimal totalPriPaid) {
		this.totalPriPaid = totalPriPaid;
	}

	public BigDecimal getTotalPriBal() {
		return totalPriBal;
	}
	public void setTotalPriBal(BigDecimal totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getTdSchdPft() {
		return tdSchdPft;
	}
	public void setTdSchdPft(BigDecimal tdSchdPft) {
		this.tdSchdPft = tdSchdPft;
	}

	public BigDecimal getTdPftCpz() {
		return tdPftCpz;
	}
	public void setTdPftCpz(BigDecimal tdPftCpz) {
		this.tdPftCpz = tdPftCpz;
	}

	public BigDecimal getTdSchdPftPaid() {
		return tdSchdPftPaid;
	}
	public void setTdSchdPftPaid(BigDecimal tdSchdPftPaid) {
		this.tdSchdPftPaid = tdSchdPftPaid;
	}

	public BigDecimal getTdSchdPftBal() {
		return tdSchdPftBal;
	}
	public void setTdSchdPftBal(BigDecimal tdSchdPftBal) {
		this.tdSchdPftBal = tdSchdPftBal;
	}

	public BigDecimal getTdPftAccrued() {
		return tdPftAccrued;
	}
	public void setTdPftAccrued(BigDecimal tdPftAccrued) {
		this.tdPftAccrued = tdPftAccrued;
	}

	public BigDecimal getTdPftAccrueSusp() {
		return tdPftAccrueSusp;
	}
	public void setTdPftAccrueSusp(BigDecimal tdPftAccrueSusp) {
		this.tdPftAccrueSusp = tdPftAccrueSusp;
	}

	public BigDecimal getTdPftAmortized() {
		return tdPftAmortized;
	}
	public void setTdPftAmortized(BigDecimal tdPftAmortized) {
		this.tdPftAmortized = tdPftAmortized;
	}

	public BigDecimal getTdPftAmortizedSusp() {
		return tdPftAmortizedSusp;
	}
	public void setTdPftAmortizedSusp(BigDecimal tdPftAmortizedSusp) {
		this.tdPftAmortizedSusp = tdPftAmortizedSusp;
	}

	public BigDecimal getTdSchdPri() {
		return tdSchdPri;
	}
	public void setTdSchdPri(BigDecimal tdSchdPri) {
		this.tdSchdPri = tdSchdPri;
	}

	public BigDecimal getTdSchdPriPaid() {
		return tdSchdPriPaid;
	}
	public void setTdSchdPriPaid(BigDecimal tdSchdPriPaid) {
		this.tdSchdPriPaid = tdSchdPriPaid;
	}

	public BigDecimal getTdSchdPriBal() {
		return tdSchdPriBal;
	}
	public void setTdSchdPriBal(BigDecimal tdSchdPriBal) {
		this.tdSchdPriBal = tdSchdPriBal;
	}

	public BigDecimal getAcrTillLBD() {
		return acrTillLBD;
	}
	public void setAcrTillLBD(BigDecimal acrTillLBD) {
		this.acrTillLBD = acrTillLBD;
	}

	public BigDecimal getAcrTillNBD() {
		return acrTillNBD;
	}
	public void setAcrTillNBD(BigDecimal acrTillNBD) {
		this.acrTillNBD = acrTillNBD;
	}

	public BigDecimal getAcrTodayToNBD() {
		return acrTodayToNBD;
	}
	public void setAcrTodayToNBD(BigDecimal acrTodayToNBDate) {
		this.acrTodayToNBD = acrTodayToNBDate;
	}

	public BigDecimal getAmzTillNBD() {
		return amzTillNBD;
	}
	public void setAmzTillNBD(BigDecimal amzTillNBD) {
		this.amzTillNBD = amzTillNBD;
	}

	public BigDecimal getAmzTillLBD() {
		return amzTillLBD;
	}
	public void setAmzTillLBD(BigDecimal amzTillLBD) {
		this.amzTillLBD = amzTillLBD;
	}

	public BigDecimal getAmzTodayToNBD() {
		return amzTodayToNBD;
	}
	public void setAmzTodayToNBD(BigDecimal amzTodayToNBDate) {
		this.amzTodayToNBD = amzTodayToNBDate;
	}

}
