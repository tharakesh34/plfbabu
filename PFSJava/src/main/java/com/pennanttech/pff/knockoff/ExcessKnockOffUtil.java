package com.pennanttech.pff.knockoff;

import java.util.Date;

import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;

public class ExcessKnockOffUtil {

	private ExcessKnockOffUtil() {
		super();
	}

	public static AutoKnockOffExcess getExcessKnockOff(ExcessKnockOff ekf, FinanceMain fm) {
		AutoKnockOffExcess ake = new AutoKnockOffExcess();

		ake.setFinID(fm.getFinID());
		ake.setFinReference(fm.getFinReference());
		ake.setAmountType(ekf.getAmountType());
		ake.setBalanceAmount(ekf.getBalanceAmt());
		ake.setValueDate(ekf.getValueDate());
		ake.setProcessingFlag(true);
		ake.setTotalUtilizedAmnt(ekf.getTotalUtilizedAmnt());
		ake.setPayableID(ekf.getReferenceID());
		ake.setThresholdValue(ekf.getThresholdValue() == null ? "0" : ekf.getThresholdValue());
		ake.setExecutionDay(ekf.getExecutionDay());

		return ake;

	}

	public static AutoKnockOffExcessDetails getKnockOffDetails(ExcessKnockOffDetails ekod) {
		AutoKnockOffExcessDetails aked = new AutoKnockOffExcessDetails();

		aked.setID(ekod.getId());
		aked.setKnockOffID(ekod.getKnockOffID());
		aked.setExcessID(ekod.getExcessID());
		aked.setCode(ekod.getCode());
		aked.setExecutionDays(ekod.getExecutionDays());
		aked.setFinType(ekod.getFinType());
		aked.setFeeTypeCode(ekod.getFeeTypeCode());
		aked.setKnockOffOrder(ekod.getKnockOffOrder());
		aked.setFeeOrder(ekod.getFeeOrder());
		aked.setUtilizedAmnt(ekod.getUtilizedAmnt());
		aked.setFinCcy(ekod.getFinCcy());

		return aked;
	}

	public static CrossLoanKnockOff getCrossLoanKnockOff(AutoKnockOffExcessDetails ako, ExcessKnockOff ekf,
			FinanceMain fm) {

		CrossLoanKnockOff clko = new CrossLoanKnockOff();
		Date receiptDt = fm.getAppDate();

		clko.setPostDate(fm.getAppDate());
		clko.setReceiptDate(receiptDt);
		clko.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		clko.setReceiptAmount(ako.getUtilizedAmnt());
		clko.setToFinReference(fm.getFinReference());
		clko.setFromFinReference(ekf.getFinReference());
		clko.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		clko.setRequestSource(RequestSource.EOD.name());
		clko.setNewRecord(true);

		return clko;
	}

	public static CrossLoanTransfer getCrossLoanTransfer(AutoKnockOffExcessDetails ako, ExcessKnockOff ekf,
			FinanceMain fm) {
		CrossLoanTransfer clt = new CrossLoanTransfer();

		clt.setCustId(ekf.getCustID());
		clt.setFromFinID(ekf.getFinID());
		clt.setToFinID(fm.getFinID());
		clt.setFromFinReference(ekf.getFinReference());
		clt.setToFinReference(fm.getFinReference());
		clt.setTransferAmount(ako.getUtilizedAmnt());
		clt.setFromFinType(ekf.getFinType());
		clt.setToFinType(fm.getFinType());
		clt.setReceiptDate(fm.getAppDate());
		clt.setReceiptAmount(ako.getUtilizedAmnt());
		clt.setExcessType(ekf.getAmountType());
		clt.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		return clt;
	}
}
