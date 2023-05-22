package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class AutoRefundLoan implements Serializable {
	private static final long serialVersionUID = -5852740108623344237L;

	private long finID;
	private String finReference;
	private int dpdDays;
	private BigDecimal maxRefundAmt = BigDecimal.ZERO;
	private BigDecimal minRefundAmt = BigDecimal.ZERO;
	private String finRepayMethod;
	private boolean finIsActive;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String finCcy;
	private String holdStatus;
	private BigDecimal refundAmt = BigDecimal.ZERO;
	private Timestamp executionTime;
	private String status;
	private String finType;
	private String entityCode;
	private int autoRefCheckDPD;
	private boolean overDueReq;
	private Date activeNDate;
	private Date closedNDate;
	private boolean alwRefundByCheque;
	private BigDecimal overDueAmount = BigDecimal.ZERO;
	private FinanceProfitDetail profitDetail;
	private List<FinODDetails> finODDetails = new ArrayList<>(1);
	private List<FinExcessAmount> excessList = new ArrayList<>();
	private List<ManualAdvise> payableList = new ArrayList<>();
	private List<ManualAdvise> receivableList = new ArrayList<>();
	private List<PaymentDetail> paymentDetails = new ArrayList<>();
	private PaymentInstruction paymentInstruction;
	private boolean writeOffLoan;
	private Date businessDate;
	private Date appDate;
	private Date closedDate;
	private int closedLoanHoldRefundDays;

	private ErrorDetail error = new ErrorDetail();

	public AutoRefundLoan() {
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

	public int getDpdDays() {
		return dpdDays;
	}

	public void setDpdDays(int dpdDays) {
		this.dpdDays = dpdDays;
	}

	public BigDecimal getMaxRefundAmt() {
		return maxRefundAmt;
	}

	public void setMaxRefundAmt(BigDecimal maxRefundAmt) {
		this.maxRefundAmt = maxRefundAmt;
	}

	public BigDecimal getMinRefundAmt() {
		return minRefundAmt;
	}

	public void setMinRefundAmt(BigDecimal minRefundAmt) {
		this.minRefundAmt = minRefundAmt;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getHoldStatus() {
		return holdStatus;
	}

	public void setHoldStatus(String holdStatus) {
		this.holdStatus = holdStatus;
	}

	public BigDecimal getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(BigDecimal refundAmt) {
		this.refundAmt = refundAmt;
	}

	public Timestamp getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Timestamp executionTime) {
		this.executionTime = executionTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public int getAutoRefCheckDPD() {
		return autoRefCheckDPD;
	}

	public void setAutoRefCheckDPD(int autoRefCheckDPD) {
		this.autoRefCheckDPD = autoRefCheckDPD;
	}

	public boolean isOverDueReq() {
		return overDueReq;
	}

	public void setOverDueReq(boolean overDueReq) {
		this.overDueReq = overDueReq;
	}

	public Date getActiveNDate() {
		return activeNDate;
	}

	public void setActiveNDate(Date activeNDate) {
		this.activeNDate = activeNDate;
	}

	public Date getClosedNDate() {
		return closedNDate;
	}

	public void setClosedNDate(Date closedNDate) {
		this.closedNDate = closedNDate;
	}

	public boolean isAlwRefundByCheque() {
		return alwRefundByCheque;
	}

	public void setAlwRefundByCheque(boolean alwRefundByCheque) {
		this.alwRefundByCheque = alwRefundByCheque;
	}

	public BigDecimal getOverDueAmount() {
		return overDueAmount;
	}

	public void setOverDueAmount(BigDecimal overDueAmount) {
		this.overDueAmount = overDueAmount;
	}

	public FinanceProfitDetail getProfitDetail() {
		return profitDetail;
	}

	public void setProfitDetail(FinanceProfitDetail profitDetail) {
		this.profitDetail = profitDetail;
	}

	public List<FinODDetails> getFinODDetails() {
		return finODDetails;
	}

	public void setFinODDetails(List<FinODDetails> finODDetails) {
		this.finODDetails = finODDetails;
	}

	public List<FinExcessAmount> getExcessList() {
		return excessList;
	}

	public void setExcessList(List<FinExcessAmount> excessList) {
		this.excessList = excessList;
	}

	public List<ManualAdvise> getPayableList() {
		return payableList;
	}

	public void setPayableList(List<ManualAdvise> payableList) {
		this.payableList = payableList;
	}

	public List<ManualAdvise> getReceivableList() {
		return receivableList;
	}

	public void setReceivableList(List<ManualAdvise> receivableList) {
		this.receivableList = receivableList;
	}

	public List<PaymentDetail> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(List<PaymentDetail> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public PaymentInstruction getPaymentInstruction() {
		return paymentInstruction;
	}

	public void setPaymentInstruction(PaymentInstruction paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}

	public boolean isWriteOffLoan() {
		return writeOffLoan;
	}

	public void setWriteOffLoan(boolean writeOffLoan) {
		this.writeOffLoan = writeOffLoan;
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public ErrorDetail getError() {
		return error;
	}

	public void setError(ErrorDetail error) {
		this.error = error;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public int getClosedLoanHoldRefundDays() {
		return closedLoanHoldRefundDays;
	}

	public void setClosedLoanHoldRefundDays(int closedLoanHoldRefundDays) {
		this.closedLoanHoldRefundDays = closedLoanHoldRefundDays;
	}

}
