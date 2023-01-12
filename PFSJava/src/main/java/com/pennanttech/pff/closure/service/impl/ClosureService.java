package com.pennanttech.pff.closure.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class ClosureService {
	private static final Logger logger = LogManager.getLogger(ClosureService.class);

	private FeeTypeDAO feeTypeDAO;
	private ReceiptService receiptService;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private ReceiptCalculator receiptCalculator;

	public ClosureService() {
		super();
	}

	public void processTerminationClosure(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getFinanceMain().isFinIsActive()) {
				ReceiptDTO receiptDTO = prepareReceiptRTO(finEODEvent);
				receiptDTO.setCustomer(custEODEvent.getCustomer());

				BigDecimal calcClosureAmt = LoanClosureCalculator.computeClosureAmount(receiptDTO, false);
				BigDecimal excessAmt = getAvailableExcessAmt(finEODEvent);

				if (calcClosureAmt.compareTo(BigDecimal.ZERO) == 0 || excessAmt.compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				if (calcClosureAmt.compareTo(excessAmt.add(finEODEvent.getFinType().getClosureThresholdLimit())) <= 0) {
					processReceipt(finEODEvent, calcClosureAmt, receiptDTO);
				}

			}
		}
	}

	private void processReceipt(FinEODEvent finEODEvent, BigDecimal calcClosureAmt, ReceiptDTO receiptDTO) {
		logger.debug(Literal.ENTERING);

		FinServiceInstruction fsi = prepareFinInstruction(finEODEvent, calcClosureAmt, receiptDTO);

		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		FinScheduleData schdData = fd.getFinScheduleData();

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			WSReturnStatus returnStatus = new WSReturnStatus();
			ErrorDetail error = schdData.getErrorDetails().get(0);
			returnStatus.setReturnCode(error.getCode());
			returnStatus.setReturnText(error.getError());
			fd.setReturnStatus(returnStatus);
			return;
		} else {
			updateTerminationExcessAmount(finEODEvent, calcClosureAmt);
		}

		logger.debug(Literal.ENTERING);
	}

	private FinServiceInstruction prepareFinInstruction(FinEODEvent finEODEvent, BigDecimal calcClosureAmt,
			ReceiptDTO receiptDTO) {
		FinanceMain fm = finEODEvent.getFinanceMain();
		fm.setAppDate(fm.getEventProperties().getAppDate());

		FinServiceInstruction fsi = new FinServiceInstruction();

		fsi.setFinReference(fm.getFinReference());
		fsi.setFinID(finEODEvent.getFinanceMain().getFinID());
		fsi.setFromDate(fm.getAppDate());
		fsi.setAmount(calcClosureAmt);
		fsi.setPaymentMode(RepayConstants.PAYTYPE_CASH);
		fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_TEXCESS);
		fsi.setPanNumber(receiptDTO.getCustomer().getCustCRCPR());
		fsi.setReqType("Post");
		fsi.setNonStp(true);
		fsi.setRequestSource(RequestSource.EOD);
		fsi.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		fsi.setValueDate(fm.getAppDate());
		LoggedInUser userDetails = new LoggedInUser();
		fsi.setLoggedInUser(userDetails);

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceivedDate(fm.getAppDate());
		fsi.setReceiptDetail(rcd);

		FinReceiptDetail rd = fsi.getReceiptDetail();

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(rd.getReceivedDate());
			rd.setValueDate(rd.getReceivedDate());
		}

		if (fsi.getReceiptDetail().getReceivedDate() == null) {
			fsi.getReceiptDetail().setReceivedDate(SysParamUtil.getAppDate());
		}

		fsi.setReceivedDate(rd.getReceivedDate());
		fsi.setNewReceipt(true);

		String paymentMode = fsi.getPaymentMode();
		if (fsi.getRealizationDate() == null && ReceiptMode.CHEQUE.equals(paymentMode)
				|| ReceiptMode.DD.equals(paymentMode)) {
			fsi.setRealizationDate(SysParamUtil.getAppDate());
		} else {
			fsi.setRealizationDate(SysParamUtil.getAppDate());
		}
		return fsi;
	}

	private void updateTerminationExcessAmount(FinEODEvent finEODEvent, BigDecimal receiptAmt) {
		List<FinExcessAmount> finExcessAmounts = finEODEvent.getFinExcessAmounts();
		String finReference = finEODEvent.getFinanceMain().getFinReference();
		BigDecimal balns = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal utilized = BigDecimal.ZERO;

		for (FinExcessAmount finex : finExcessAmounts) {

			if (receiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				return;
			}

			long excessID = finex.getExcessID();
			BigDecimal excesAmt = finex.getBalanceAmt();

			if (RepayConstants.EXCESSADJUSTTO_TEXCESS.equals(finex.getAmountType())) {
				if (receiptAmt.compareTo(excesAmt) >= 0) {
					balns = BigDecimal.ZERO;
					utilized = excesAmt;
					amount = BigDecimal.ZERO;
					receiptAmt = receiptAmt.subtract(utilized);
				}

				if (excesAmt.compareTo(receiptAmt) >= 0) {
					balns = excesAmt.subtract(receiptAmt);
					amount = balns;
					utilized = receiptAmt;
					receiptAmt = receiptAmt.subtract(utilized);
				}

				finExcessAmountDAO.updateTerminationExcess(finReference, excessID, utilized, balns, amount);
			}
		}
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
		FinScheduleData schdData = new FinScheduleData();
		FinanceType financeType = finEODEvent.getFinType();
		FinanceMain fm = finEODEvent.getFinanceMain();
		Date appDate = fm.getEventProperties().getAppDate();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		schdData.setFinanceScheduleDetails(schedules);
		schdData.setFinanceMain(fm);

		ReceiptDTO receiptDTO = new ReceiptDTO();

		receiptDTO.setFinanceMain(fm);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setOdDetails(
				receiptCalculator.getValueDatePenalties(schdData, BigDecimal.ZERO, appDate, null, true, schedules));
		receiptDTO.setManualAdvises(manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView"));
		receiptDTO.setFees(null);
		receiptDTO.setRoundAdjMth(SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD));
		receiptDTO.setLppFeeType(feeTypeDAO.getTaxDetailByCode(Allocation.ODC));
		receiptDTO.setFinType(financeType);
		receiptDTO.setValuedate(appDate);
		receiptDTO.setPostDate(appDate);
		receiptDTO.setProfitDetail(financeProfitDetailDAO.getFinProfitDetailsById(fm.getFinID()));

		return receiptDTO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

}