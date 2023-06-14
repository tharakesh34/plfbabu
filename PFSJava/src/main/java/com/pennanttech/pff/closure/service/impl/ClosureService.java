package com.pennanttech.pff.closure.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
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
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ExcessType;
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
		for (FinEODEvent finEOD : finEODEvents) {
			FinanceMain fm = finEOD.getFinanceMain();

			if (!fm.isFinIsActive()) {
				continue;
			}

			BigDecimal excessAmt = getExcessBalance(finEOD.getFinExcessAmounts());

			if (excessAmt.compareTo(BigDecimal.ZERO) > 0) {
				ReceiptDTO receiptDTO = prepareReceiptRTO(finEOD);
				receiptDTO.setCustomer(custEODEvent.getCustomer());

				prepareReceipt(finEOD, excessAmt, receiptDTO);
			}
		}
	}

	private void processReceipt(FinEODEvent finEODEvent, BigDecimal calcClosureAmt, ReceiptDTO receiptDTO) {
		logger.debug(Literal.ENTERING);

		FinServiceInstruction fsi = prepareFinInstruction(finEODEvent, calcClosureAmt, receiptDTO);

		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		FinScheduleData schdData = fd.getFinScheduleData();

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			updateUtilizedExcess(finEODEvent.getFinExcessAmounts(), false);

			WSReturnStatus returnStatus = new WSReturnStatus();
			ErrorDetail error = schdData.getErrorDetails().get(0);
			returnStatus.setReturnCode(error.getCode());
			returnStatus.setReturnText(error.getError());
			fd.setReturnStatus(returnStatus);
			return;
		} else {
			updateTerminationExcessAmount(finEODEvent, calcClosureAmt);
			finEODEvent.setFinODDetails(new ArrayList<>());
			finEODEvent.setFinanceScheduleDetails(schdData.getFinanceScheduleDetails());
			finEODEvent.setFinProfitDetail(schdData.getFinPftDeatil());
		}

		logger.debug(Literal.ENTERING);
	}

	private FinServiceInstruction prepareFinInstruction(FinEODEvent finEODEvent, BigDecimal calcClosureAmt,
			ReceiptDTO receiptDTO) {

		List<FinFeeDetail> fees = receiptDTO.getFees();

		FinanceMain fm = finEODEvent.getFinanceMain();
		fm.setAppDate(fm.getEventProperties().getAppDate());

		FinServiceInstruction fsi = new FinServiceInstruction();

		fsi.setFinReference(fm.getFinReference());
		fsi.setFinID(finEODEvent.getFinanceMain().getFinID());
		Date appDate = fm.getAppDate();

		fsi.setFromDate(appDate);
		fsi.setAmount(calcClosureAmt);

		/* Payment Mode should be empty for Closer receipt with Termination Excess */
		fsi.setPaymentMode(" ");
		fsi.setExcessAdjustTo(ExcessType.TEXCESS);
		fsi.setPanNumber(receiptDTO.getCustomer().getCustCRCPR());
		fsi.setReqType("Post");
		fsi.setNonStp(true);
		fsi.setRequestSource(RequestSource.EOD);
		fsi.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		fsi.setValueDate(appDate);
		fsi.setLoggedInUser(PFSBatchAdmin.loggedInUser);

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceivedDate(appDate);
		rcd.setValueDate(appDate);
		fsi.setReceiptDetail(rcd);

		for (FinFeeDetail fee : fees) {
			fee.setPaidAmount(fee.getActualAmount());
		}

		fsi.setFinFeeDetails(fees);

		FinReceiptDetail rd = fsi.getReceiptDetail();

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(rd.getReceivedDate());
			rd.setValueDate(rd.getReceivedDate());
		}

		if (fsi.getReceiptDetail().getReceivedDate() == null) {
			fsi.getReceiptDetail().setReceivedDate(appDate);
		}

		fsi.setReceivedDate(rd.getReceivedDate());
		fsi.setNewReceipt(true);

		String paymentMode = fsi.getPaymentMode();
		if (fsi.getRealizationDate() == null && ReceiptMode.CHEQUE.equals(paymentMode)
				|| ReceiptMode.DD.equals(paymentMode)) {
			fsi.setRealizationDate(appDate);
		} else {
			fsi.setRealizationDate(appDate);
		}
		return fsi;
	}

	private void updateTerminationExcessAmount(FinEODEvent finEODEvent, BigDecimal receiptAmt) {
		List<FinExcessAmount> finExcessAmounts = finEODEvent.getFinExcessAmounts();
		BigDecimal utilized = BigDecimal.ZERO;
		BigDecimal balance = BigDecimal.ZERO;

		for (FinExcessAmount finex : finExcessAmounts) {
			if (receiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				return;
			}

			long excessID = finex.getExcessID();
			BigDecimal excesAmt = finex.getBalanceAmt();

			if (ExcessType.TEXCESS.equals(finex.getAmountType())) {
				if (receiptAmt.compareTo(excesAmt) >= 0) {
					balance = BigDecimal.ZERO;
					utilized = excesAmt;
				} else if (excesAmt.compareTo(receiptAmt) >= 0) {
					balance = excesAmt.subtract(receiptAmt);
					utilized = receiptAmt;
				}

				receiptAmt = receiptAmt.subtract(utilized);
				finExcessAmountDAO.updateTerminationExcess(excessID, utilized, balance, utilized);
			}
		}
	}

	private ReceiptDTO prepareReceiptRTO(FinEODEvent finEODEvent) {
		FinScheduleData schdData = new FinScheduleData();
		FinanceType financeType = finEODEvent.getFinType();
		FinanceMain fm = finEODEvent.getFinanceMain();
		Date appDate = fm.getEventProperties().getAppDate();
		fm.setAppDate(appDate);
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

	private void updateUtilizedExcess(List<FinExcessAmount> finExcessAmounts, boolean isReserve) {
		for (FinExcessAmount fea : finExcessAmounts) {
			if (!ExcessType.TEXCESS.equals(fea.getAmountType())) {
				continue;
			}

			BigDecimal balanceAmount = fea.getBalanceAmt();

			if (isReserve) {
				if (balanceAmount.compareTo(BigDecimal.ZERO) > 0) {
					finExcessAmountDAO.updateExcessAmount(fea.getExcessID(), "R", balanceAmount);
				}
			} else {
				finExcessAmountDAO.updateTerminationExcess(fea.getExcessID(), BigDecimal.ZERO, balanceAmount,
						balanceAmount);
			}
		}
	}

	private BigDecimal getExcessBalance(List<FinExcessAmount> finExcessAmounts) {
		BigDecimal excessAmt = BigDecimal.ZERO;

		for (FinExcessAmount fea : finExcessAmounts) {
			if (!ExcessType.TEXCESS.equals(fea.getAmountType())) {
				continue;
			}

			excessAmt = excessAmt.add(fea.getBalanceAmt());
		}

		return excessAmt;
	}

	private void prepareReceipt(FinEODEvent finEOD, BigDecimal excessAmt, ReceiptDTO receiptDTO) {
		BigDecimal calcClosureAmt = LoanClosureCalculator.computeClosureAmount(receiptDTO, true);

		if (calcClosureAmt.compareTo(excessAmt.add(finEOD.getFinType().getClosureThresholdLimit())) > 0) {
			return;
		}

		BigDecimal receiptAmount = calcClosureAmt;

		if (excessAmt.compareTo(calcClosureAmt) <= 0) {
			receiptAmount = excessAmt;
		}

		updateUtilizedExcess(finEOD.getFinExcessAmounts(), true);
		processReceipt(finEOD, receiptAmount, receiptDTO);

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
