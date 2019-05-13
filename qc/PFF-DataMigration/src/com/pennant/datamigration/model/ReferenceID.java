package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rmtmasters.FinanceType;

public class ReferenceID implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private List<FinanceType> finTypes = new ArrayList<FinanceType>(1);
	private long manualAdviseID = 333;
	private long excessID = 0;
	private long excessMovementID = 0;
	private long presentmentID = 0;
	private long repayID = 0;
	private long receiptAlocID = 0;
	private Date appDate = DateUtility.getAppDate();
	private List<FeeTypeVsGLMapping> feeVsGLList = new ArrayList<FeeTypeVsGLMapping>(1);
	private long linkedTranID = 0;
	private int tranOrder = 0;
	private String account;
	private String accountType;
	private BigDecimal postAmount = BigDecimal.ZERO;
	private String tranDesc;
	private String drOrcr;
	private long madMovementID = 0;
	private BigDecimal odcReceived = BigDecimal.ZERO;
	private BigDecimal bounceReceived = BigDecimal.ZERO;
	private BigDecimal totalBankAmount = BigDecimal.ZERO;

	public long getManualAdviseID() {
		return manualAdviseID;
	}

	public void setManualAdviseID(long manualAdviseID) {
		this.manualAdviseID = manualAdviseID;
	}

	public long getExcessID() {
		return excessID;
	}

	public void setExcessID(long excessID) {
		this.excessID = excessID;
	}

	public long getExcessMovementID() {
		return excessMovementID;
	}

	public void setExcessMovementID(long excessMovementID) {
		this.excessMovementID = excessMovementID;
	}

	public long getPresentmentID() {
		return presentmentID;
	}

	public void setPresentmentID(long presentmentID) {
		this.presentmentID = presentmentID;
	}

	public long getRepayID() {
		return repayID;
	}

	public void setRepayID(long repayID) {
		this.repayID = repayID;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public List<FinanceType> getFinTypes() {
		return finTypes;
	}

	public void setFinTypes(List<FinanceType> finTypes) {
		this.finTypes = finTypes;
	}

	public List<FeeTypeVsGLMapping> getFeeVsGLList() {
		return feeVsGLList;
	}

	public void setFeeVsGLList(List<FeeTypeVsGLMapping> feeVsGLList) {
		this.feeVsGLList = feeVsGLList;
	}

	public long getLinkedTranID() {
		return linkedTranID;
	}

	public void setLinkedTranID(long linkedTranID) {
		this.linkedTranID = linkedTranID;
	}

	public int getTranOrder() {
		return tranOrder;
	}

	public void setTranOrder(int tranOrder) {
		this.tranOrder = tranOrder;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getPostAmount() {
		return postAmount;
	}

	public void setPostAmount(BigDecimal postAmount) {
		this.postAmount = postAmount;
	}

	public String getTranDesc() {
		return tranDesc;
	}

	public void setTranDesc(String tranDesc) {
		this.tranDesc = tranDesc;
	}

	public String getDrOrcr() {
		return drOrcr;
	}

	public void setDrOrcr(String drOrcr) {
		this.drOrcr = drOrcr;
	}

	public long getMadMovementID() {
		return madMovementID;
	}

	public void setMadMovementID(long madMovementID) {
		this.madMovementID = madMovementID;
	}

	public BigDecimal getOdcReceived() {
		return odcReceived;
	}

	public void setOdcReceived(BigDecimal odcReceived) {
		this.odcReceived = odcReceived;
	}

	public BigDecimal getBounceReceived() {
		return bounceReceived;
	}

	public void setBounceReceived(BigDecimal bounceReceived) {
		this.bounceReceived = bounceReceived;
	}

	public BigDecimal getTotalBankAmount() {
		return totalBankAmount;
	}

	public void setTotalBankAmount(BigDecimal totalBankAmount) {
		this.totalBankAmount = totalBankAmount;
	}

	public long getReceiptAlocID() {
		return receiptAlocID;
	}

	public void setReceiptAlocID(long receiptAlocID) {
		this.receiptAlocID = receiptAlocID;
	}

}
