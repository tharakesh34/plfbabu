package com.pennant.backend.model.reports;

public class AvailCommitment {

	private String cmtReference = "";
	private String cmtAmount = "";
	private String cmtUtilizedAmount = "";
	private String newDeal = "";
	private String cmtAvailable = "";
	private String negCmtAvailFlag = "F";
	private String cmtExpDate = "";
	private String cmtExpFlag = "F";
	private String revolving = "";
	private String cmtCcy = "";
	private String guarantor = "";
	private String agent = "";
	private String cmtAccount = "";
	private String cmtNotes = "";
	private String cmtTitle = "";
	private int ccyEditField = 0;
	private long custId = 0;
	private String limitCcy = "";

	private String finReference = "";
	private String finCcy = "";
	private String finAmount = "";
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

	public AvailCommitment() {
	    super();
	}

	public String getCmtReference() {
		return cmtReference;
	}

	public void setCmtReference(String cmtReference) {
		this.cmtReference = cmtReference;
	}

	public String getCmtAmount() {
		return cmtAmount;
	}

	public void setCmtAmount(String cmtAmount) {
		this.cmtAmount = cmtAmount;
	}

	public String getCmtUtilizedAmount() {
		return cmtUtilizedAmount;
	}

	public void setCmtUtilizedAmount(String cmtUtilizedAmount) {
		this.cmtUtilizedAmount = cmtUtilizedAmount;
	}

	public String getNewDeal() {
		return newDeal;
	}

	public void setNewDeal(String newDeal) {
		this.newDeal = newDeal;
	}

	public String getCmtAvailable() {
		return cmtAvailable;
	}

	public void setCmtAvailable(String cmtAvailable) {
		this.cmtAvailable = cmtAvailable;
	}

	public String getCmtExpDate() {
		return cmtExpDate;
	}

	public void setCmtExpDate(String cmtExpDate) {
		this.cmtExpDate = cmtExpDate;
	}

	public String getRevolving() {
		return revolving;
	}

	public void setRevolving(String revolving) {
		this.revolving = revolving;
	}

	public String getCmtCcy() {
		return cmtCcy;
	}

	public void setCmtCcy(String cmtCcy) {
		this.cmtCcy = cmtCcy;
	}

	public String getGuarantor() {
		return guarantor;
	}

	public void setGuarantor(String guarantor) {
		this.guarantor = guarantor;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getCmtAccount() {
		return cmtAccount;
	}

	public void setCmtAccount(String cmtAccount) {
		this.cmtAccount = cmtAccount;
	}

	public String getCmtNotes() {
		return cmtNotes;
	}

	public void setCmtNotes(String cmtNotes) {
		this.cmtNotes = cmtNotes;
	}

	public String getCmtTitle() {
		return cmtTitle;
	}

	public void setCmtTitle(String cmtTitle) {
		this.cmtTitle = cmtTitle;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
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

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getNegCmtAvailFlag() {
		return negCmtAvailFlag;
	}

	public void setNegCmtAvailFlag(String negCmtAvailFlag) {
		this.negCmtAvailFlag = negCmtAvailFlag;
	}

	public String getCmtExpFlag() {
		return cmtExpFlag;
	}

	public void setCmtExpFlag(String cmtExpFlag) {
		this.cmtExpFlag = cmtExpFlag;
	}

	public String getLimitCcy() {
		return limitCcy;
	}

	public void setLimitCcy(String limitCcy) {
		this.limitCcy = limitCcy;
	}

}
