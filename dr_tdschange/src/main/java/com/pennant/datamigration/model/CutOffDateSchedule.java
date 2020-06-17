package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class CutOffDateSchedule implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private String finReference;
	private Date schDate;
	private BigDecimal profitCalc = BigDecimal.ZERO;
	private BigDecimal profitSchd = BigDecimal.ZERO;
	private BigDecimal principalSchd = BigDecimal.ZERO;
	private BigDecimal repayAmount = BigDecimal.ZERO;
	private BigDecimal calculatedRate = BigDecimal.ZERO;
	private BigDecimal cpzAmount = BigDecimal.ZERO;
	private BigDecimal partialPaidAmount = BigDecimal.ZERO;
	private long presentmentID = -1;
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public Date getSchDate() {
		return schDate;
	}
	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}
	public BigDecimal getProfitCalc() {
		return profitCalc;
	}
	public void setProfitCalc(BigDecimal profitCalc) {
		this.profitCalc = profitCalc;
	}
	public BigDecimal getProfitSchd() {
		return profitSchd;
	}
	public void setProfitSchd(BigDecimal profitSchd) {
		this.profitSchd = profitSchd;
	}
	public BigDecimal getPrincipalSchd() {
		return principalSchd;
	}
	public void setPrincipalSchd(BigDecimal principalSchd) {
		this.principalSchd = principalSchd;
	}
	public BigDecimal getRepayAmount() {
		return repayAmount;
	}
	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
	public BigDecimal getCalculatedRate() {
		return calculatedRate;
	}
	public void setCalculatedRate(BigDecimal calculatedRate) {
		this.calculatedRate = calculatedRate;
	}
	public BigDecimal getCpzAmount() {
		return cpzAmount;
	}
	public void setCpzAmount(BigDecimal cpzAmount) {
		this.cpzAmount = cpzAmount;
	}
	public BigDecimal getPartialPaidAmount() {
		return partialPaidAmount;
	}
	public void setPartialPaidAmount(BigDecimal partialPaidAmount) {
		this.partialPaidAmount = partialPaidAmount;
	}
	public long getPresentmentID() {
		return presentmentID;
	}
	public void setPresentmentID(long presentmentID) {
		this.presentmentID = presentmentID;
	}
}
