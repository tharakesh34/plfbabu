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
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
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
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
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
	private FeeCalculator feeCalculator;
	private FinTypeFeesDAO finTypeFeesDAO;

	public ClosureService() {
		super();
	}

	public void processTerminationClosure(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			if (!fm.isFinIsActive()) {
				continue;
			}

			BigDecimal receiptAmount = BigDecimal.ZERO;
			ReceiptDTO receiptDTO = prepareReceiptRTO(finEODEvent);
			receiptDTO.setCustomer(custEODEvent.getCustomer());

			BigDecimal calcClosureAmt = LoanClosureCalculator.computeClosureAmount(receiptDTO, true);
			BigDecimal excessAmt = getAvailableExcessAmt(finEODEvent);

			if (calcClosureAmt.compareTo(BigDecimal.ZERO) == 0 || excessAmt.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			if (calcClosureAmt.compareTo(excessAmt.add(finEODEvent.getFinType().getClosureThresholdLimit())) <= 0) {
				if (excessAmt.compareTo(calcClosureAmt) <= 0) {
					receiptAmount = excessAmt;
				} else {
					receiptAmount = calcClosureAmt;
				}

				processReceipt(finEODEvent, receiptAmount, receiptDTO);
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
			// updateTerminationExcessAmount(finEODEvent, calcClosureAmt);
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
		fsi.setLoggedInUser(PFSBatchAdmin.loggedInUser);

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
		FinReceiptData rd = setFinReceiptData(finEODEvent);
		feeCalculator.calculateFees(rd);

		ReceiptDTO receiptDTO = new ReceiptDTO();

		receiptDTO.setFinanceMain(fm);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setOdDetails(
				receiptCalculator.getValueDatePenalties(schdData, BigDecimal.ZERO, appDate, null, true, schedules));
		receiptDTO.setManualAdvises(manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView"));
		receiptDTO.setFees(rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
		receiptDTO.setRoundAdjMth(SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD));
		receiptDTO.setLppFeeType(feeTypeDAO.getTaxDetailByCode(Allocation.ODC));
		receiptDTO.setFinType(financeType);
		receiptDTO.setValuedate(appDate);
		receiptDTO.setPostDate(appDate);
		receiptDTO.setProfitDetail(financeProfitDetailDAO.getFinProfitDetailsById(fm.getFinID()));

		return receiptDTO;
	}

	private FinReceiptData setFinReceiptData(FinEODEvent finEODEvent) {
		String finType = finEODEvent.getFinType().getFinType();

		FinReceiptHeader frh = new FinReceiptHeader();
		FinReceiptData rd = new FinReceiptData();
		FinScheduleData fsd = new FinScheduleData();
		FinanceDetail fd = new FinanceDetail();

		frh.setPartPayAmount(BigDecimal.ZERO);
		fsd.setFinanceType(finEODEvent.getFinType());
		fsd.setFinanceScheduleDetails(finEODEvent.getFinanceScheduleDetails());
		fsd.setFinanceMain(finEODEvent.getFinanceMain());
		fsd.setFeeEvent(AccountingEvent.EARLYSTL);
		fsd.setFinPftDeatil(finEODEvent.getFinProfitDetail());
		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesForLMSEvent(finType, AccountingEvent.EARLYSTL));

		fd.setFinScheduleData(fsd);
		rd.setFinanceDetail(fd);
		rd.setTdPriBal(finEODEvent.getFinProfitDetail().getTdSchdPriBal());
		rd.setReceiptHeader(frh);
		
		return rd;
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

	@Autowired
	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}
}
