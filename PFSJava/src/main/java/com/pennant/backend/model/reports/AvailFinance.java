package com.pennant.backend.model.reports;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AvailFinance {

	private long CustId;
	private Date finStartDate;
	private String finCommitmentRef = "";
	private String finType = "";
	private String finReference = "";
	private String finCcy = "";
	private String finAmount = "";
	private String totalPftSchd = "";
	private String drawnPrinciple = "";
	private String finAmtBHD = "";
	private String outStandingBal = "";
	private String lastRepay = "";
	private String maturityDate = "";
	private String profitRate = "";
	private String repayFrq = "";
	private String status = "";
	private String finDivision = "";
	private String finDivisionDesc = "";
	private int noInst = 0;

	public AvailFinance() {
	    super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finType");
		excludeFields.add("totalPftSchd");
		return excludeFields;
	}

	public String getFinCommitmentRef() {
		return finCommitmentRef;
	}

	public void setFinCommitmentRef(String finCommitmentRef) {
		this.finCommitmentRef = finCommitmentRef;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getDrawnPrinciple() {
		return drawnPrinciple;
	}

	public void setDrawnPrinciple(String drawnPrinciple) {
		this.drawnPrinciple = drawnPrinciple;
	}

	public String getFinAmtBHD() {
		return finAmtBHD;
	}

	public void setFinAmtBHD(String finAmtBHD) {
		this.finAmtBHD = finAmtBHD;
	}

	public String getOutStandingBal() {
		return outStandingBal;
	}

	public void setOutStandingBal(String outStandingBal) {
		this.outStandingBal = outStandingBal;
	}

	public String getLastRepay() {
		return lastRepay;
	}

	public void setLastRepay(String lastRepay) {
		this.lastRepay = lastRepay;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(String profitRate) {
		this.profitRate = profitRate;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinDivision() {
		return finDivision;
	}

	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public String getFinDivisionDesc() {
		return finDivisionDesc;
	}

	public void setFinDivisionDesc(String finDivisionDesc) {
		this.finDivisionDesc = finDivisionDesc;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public int getNoInst() {
		return noInst;
	}

	public void setNoInst(int noInst) {
		this.noInst = noInst;
	}

	public long getCustId() {
		return CustId;
	}

	public void setCustId(long custId) {
		CustId = custId;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getTotalPftSchd() {
		return totalPftSchd;
	}

	public void setTotalPftSchd(String totalPftSchd) {
		this.totalPftSchd = totalPftSchd;
	}

}
