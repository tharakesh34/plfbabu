package com.pennanttech.pff.overdraft.model;

import java.util.Date;

import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;

public class OverdraftDTO extends FinanceMain {
	private static final long serialVersionUID = 1L;

	private Date nextSchdDate;
	private FeeType txnChrFeeType = new FeeType();
	private FeeType colChrFeeType = new FeeType();
	private FinODPenaltyRate penaltyRate = new FinODPenaltyRate();

	public OverdraftDTO() {
		super();
	}

	public OverdraftDTO copyEntity() {

		OverdraftDTO od = new OverdraftDTO();
		od.setNextSchdDate(this.nextSchdDate);
		od.setTxnChrFeeType(this.txnChrFeeType.copyEntity());
		od.setColChrFeeType(this.colChrFeeType.copyEntity());

		return od;
	}

	public Date getNextSchdDate() {
		return nextSchdDate;
	}

	public void setNextSchdDate(Date nextSchdDate) {
		this.nextSchdDate = nextSchdDate;
	}

	public FeeType getTxnChrFeeType() {
		return txnChrFeeType;
	}

	public void setTxnChrFeeType(FeeType txnChrFeeType) {
		this.txnChrFeeType = txnChrFeeType;
	}

	public FeeType getColChrFeeType() {
		return colChrFeeType;
	}

	public void setColChrFeeType(FeeType colChrFeeType) {
		this.colChrFeeType = colChrFeeType;
	}

	public FinODPenaltyRate getPenaltyRate() {
		return penaltyRate;
	}

	public void setPenaltyRate(FinODPenaltyRate penaltyRate) {
		this.penaltyRate = penaltyRate;
	}
}
