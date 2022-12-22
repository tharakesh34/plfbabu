package com.pennanttech.pff.closure.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class ClosureService {
	private static final Logger logger = LogManager.getLogger(ClosureService.class);

	private ReceiptService receiptService;
	private FeeTypeDAO feeTypeDAO;
	private RepaymentProcessUtil repaymentProcessUtil;

	public ClosureService() {
		super();
	}

	public void processTerminationClosure(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getFinanceMain().isFinIsActive()) {
				ReceiptDTO receiptDTO = prepareReceiptRTO(finEODEvent);
				receiptDTO.setCustomer(custEODEvent.getCustomer());

				BigDecimal calcClosureAmt = LoanClosureCalculator.computeClosureAmount(receiptDTO, true);
				BigDecimal excessAmt = getAvailableExcessAmt(finEODEvent);

				if (calcClosureAmt.compareTo(excessAmt.add(finEODEvent.getFinType().getClosureThresholdLimit())) <= 0) {
					processReceipt(finEODEvent, calcClosureAmt, receiptDTO);
				}

			}
		}
	}

	private void processReceipt(FinEODEvent finEODEvent, BigDecimal calcClosureAmt, ReceiptDTO receiptDTO) {
		List<FinReceiptDetail> list = new ArrayList<>();

		FinReceiptHeader rch = prepareRCH(finEODEvent, calcClosureAmt);
		FinReceiptDetail rcd = prepareRCD(finEODEvent.getFinanceMain(), calcClosureAmt);

		rcd.setPayAgainstID(1);

		list.add(rcd);
		rch.setReceiptDetails(list);

		receiptDTO.setFinReceiptHeader(rch);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);
	}

	private BigDecimal getAvailableExcessAmt(FinEODEvent finEODEvent) {
		List<FinExcessAmount> finExcessAmounts = finEODEvent.getFinExcessAmounts();
		BigDecimal excessAmt = BigDecimal.ZERO;
		for (FinExcessAmount finex : finExcessAmounts) {
			if (RepayConstants.EXCESSADJUSTTO_TEXCESS.equals(finex.getAmountType())) {
				excessAmt = excessAmt.add(finex.getBalanceAmt());
			}
		}

		return excessAmt;
	}

	private ReceiptDTO prepareReceiptRTO(FinEODEvent finEODEvent) {
		FinanceType financeType = finEODEvent.getFinType();
		FinanceMain fm = finEODEvent.getFinanceMain();
		Date appDate = fm.getEventProperties().getAppDate();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		ReceiptDTO receiptDTO = new ReceiptDTO();

		receiptDTO.setFinanceMain(fm);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setOdDetails(finEODEvent.getFinODDetails());
		receiptDTO.setManualAdvises(finEODEvent.getPostingManualAdvises());
		receiptDTO.setFees(null);
		receiptDTO.setRoundAdjMth(SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD));
		receiptDTO.setLppFeeType(feeTypeDAO.getTaxDetailByCode(Allocation.ODC));
		receiptDTO.setFinType(financeType);
		receiptDTO.setValuedate(appDate);
		receiptDTO.setPostDate(appDate);
		receiptDTO.setProfitDetail(finEODEvent.getFinProfitDetail());

		return receiptDTO;
	}

	private FinReceiptHeader prepareRCH(FinEODEvent finEODEvent, BigDecimal receiptAmount) {
		FinReceiptHeader rch = new FinReceiptHeader();
		FinanceMain fm = finEODEvent.getFinanceMain();
		Date appDate = fm.getEventProperties().getAppDate();

		rch.setFinID(fm.getFinID());
		rch.setReference(fm.getFinReference());
		rch.setReceiptDate(appDate);
		rch.setRealizationDate(appDate);
		rch.setReceiptAmount(receiptAmount);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		rch.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_TEXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setActFinReceipt(true);
		rch.setReceivedDate(appDate);
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setVersion(rch.getVersion() + 1);
		rch.setLogSchInPresentment(true);
		rch.setPostBranch(PennantConstants.APP_PHASE_EOD);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setReceiptMode(RepayConstants.PAYTYPE_CASH);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);

		return rch;
	}

	private FinReceiptDetail prepareRCD(FinanceMain fm, BigDecimal receiptAmount) {
		Date appDate = fm.getEventProperties().getAppDate();

		FinReceiptDetail rcd = new FinReceiptDetail();

		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(ReceiptMode.TEXCESS);
		rcd.setAmount(receiptAmount);
		rcd.setDueAmount(receiptAmount);
		rcd.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rcd.setValueDate(appDate);
		rcd.setReceivedDate(appDate);
		rcd.setNoReserve(true);

		return rcd;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

}
