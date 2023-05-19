package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.pff.presentment.ExcludeReasonCode;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.presentment.exception.PresentmentException;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class ReceiptPaymentService {
	private static Logger logger = LogManager.getLogger(ReceiptPaymentService.class);

	private RepaymentProcessUtil repaymentProcessUtil;
	private ReceiptCalculator receiptCalculator;

	private LoanPaymentService loanPaymentService;
	private OverdrafLoanService overdrafLoanService;
	private ReceiptCancellationService receiptCancellationService;

	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private GenerateLetterService generateLetterService;

	public ReceiptPaymentService() {
		super();
	}

	public void processReceipts(ReceiptDTO receiptDTO, int idxPresentment) {
		logger.debug(Literal.ENTERING);

		receiptDTO.setNoReserve(false);
		receiptDTO.setPdDetailsExits(true);

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		if (pd == null) {
			logger.info("Preparing presentment detail for EMI In Advance...");

			receiptDTO.setNoReserve(true);
			receiptDTO.setPdDetailsExits(false);
			pd = preparePD(receiptDTO, idxPresentment);
			receiptDTO.setPresentmentDetail(pd);
		} else {
			List<FinExcessMovement> excessMovements = pd.getExcessMovements();

			for (FinExcessMovement fem : excessMovements) {
				FinExcessAmount fea = new FinExcessAmount();

				fea.setExcessID(fem.getReceiptID());
				fea.setBalanceAmt(fem.getAmount());

				receiptDTO.getEmiInAdvance().add(fea);
			}
		}

		if (pd != null) {
			logger.info("Creating Receipts in EOD...");
			createReceipt(receiptDTO);
		}

		logger.debug(Literal.LEAVING);
	}

	public void createReceipt(ReceiptDTO receiptDTO) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		pd.setAppDate(receiptDTO.getValuedate());

		if (pd.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
			logger.info("Creating Receipts for EMI In Advance...");
			createEMIInAdvReceipt(receiptDTO);
		}

		if (pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			RequestSource requestSource = receiptDTO.getRequestSource();
			if (requestSource == RequestSource.EOD || requestSource == RequestSource.PRMNT_EXT) {
				createReceiptAndBounce(receiptDTO);
			}

			if (receiptDTO.isCreatePrmntReceipt()) {
				logger.info("Creating Receipts for Presentment...");
				createPresentmentReceipt(receiptDTO);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void createEMIInAdvReceipt(ReceiptDTO receiptDTO) {
		PresentmentDetail pd = receiptDTO.getPresentmentDetail();
		FinanceMain fm = receiptDTO.getFinanceMain();

		List<FinExcessAmount> emiInAdvList = receiptDTO.getEmiInAdvance();

		BigDecimal dueAmount = pd.getAdvanceAmt();

		for (FinExcessAmount fea : emiInAdvList) {
			BigDecimal advanceAmt = fea.getBalanceAmt();

			if (dueAmount.compareTo(advanceAmt) < 0) {
				advanceAmt = dueAmount;
			}

			FinReceiptHeader rch = prepareRCH(receiptDTO, advanceAmt);
			rch.setReceiptMode(RepayConstants.PAYTYPE_EXCESS);
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
			rch.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);

			FinReceiptDetail rcd = prepareRCD(receiptDTO, advanceAmt);
			rcd.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
			rcd.setPayAgainstID(fea.getExcessID());
			rcd.setStatus(RepayConstants.PAYSTATUS_REALIZED);
			rcd.setNoReserve(receiptDTO.isNoReserve());

			List<FinReceiptDetail> list = new ArrayList<>();
			list.add(rcd);
			rch.setReceiptDetails(list);

			XcessPayables xcessPayable = new XcessPayables();
			xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
			xcessPayable.setAmount(advanceAmt);
			xcessPayable.setTotPaidNow(advanceAmt);

			rch.getXcessPayables().add(xcessPayable);

			receiptDTO.setFinReceiptHeader(rch);

			try {
				repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);
			} catch (Exception e) {
				throw new AppException();
			}

			dueAmount = dueAmount.subtract(advanceAmt);
		}

		EventProperties ep = fm.getEventProperties();
		Date appDate = ep.isParameterLoaded() ? ep.getAppDate() : SysParamUtil.getAppDate();

		if (ProductUtil.isOverDraft(fm) && DateUtil.compare(appDate, fm.getMaturityDate()) < 0) {
			return;
		}

		long finID = pd.getFinID();
		List<FinanceScheduleDetail> schedules = receiptDTO.getSchedules();

		if (loanPaymentService.isSchdFullyPaid(new LoanPayment(finID, fm.getFinReference(), schedules, appDate))) {
			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, appDate);
			profitDetailDAO.updateFinPftMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false);

			generateLetterService.saveClosedLoanLetterGenerator(fm, appDate);
		}
	}

	private void createPresentmentReceipt(ReceiptDTO receiptDTO) {
		PresentmentDetail pd = receiptDTO.getPresentmentDetail();
		FinanceMain fm = receiptDTO.getFinanceMain();

		FinReceiptHeader rch = prepareRCH(receiptDTO, pd.getPresentmentAmt());
		rch.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);

		if (!receiptDTO.isPdDetailsExits()) {
			rch.setReceiptMode(ReceiptMode.EXCESS);
		}

		FinReceiptDetail rcd = prepareRCD(receiptDTO, pd.getPresentmentAmt());
		rcd.setPaymentType(ReceiptMode.PRESENTMENT);

		List<FinReceiptDetail> list = new ArrayList<>();
		list.add(rcd);
		rch.setReceiptDetails(list);

		receiptDTO.setFinReceiptHeader(rch);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);

		try {
			financeMainDAO.updateSchdVersion(fm, true);
		} catch (ConcurrencyException e) {
			throw new PresentmentException(PresentmentError.PRMNT512);
		}

		if (pd.getId() != Long.MIN_VALUE) {
			pd.setReceiptID(rch.getReceiptID());
			presentmentDetailDAO.updateReceptIdAndAmounts(pd);
		}
	}

	private void createReceiptAndBounce(ReceiptDTO receiptDTO) {
		FinanceMain fm = receiptDTO.getFinanceMain();
		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		int excludeReason = pd.getExcludeReason();
		String resonCode = ExcludeReasonCode.reasonCode(excludeReason);
		String instrumentType = pd.getInstrumentType();

		EventProperties ep = fm.getEventProperties();
		Map<String, String> excludeMap = ep.getUpfrontBounceCodes();
		Map<String, String> bounceForPD = receiptDTO.getBounceForPD();

		if (MapUtils.isEmpty(excludeMap) && MapUtils.isEmpty(bounceForPD)) {
			return;
		}

		RequestSource requestSource = receiptDTO.getRequestSource();

		String returnCode = "";
		if (requestSource == RequestSource.EOD) {
			returnCode = excludeMap.get(resonCode.concat("$").concat(instrumentType));
		} else {
			returnCode = bounceForPD.get(resonCode.concat("$").concat(instrumentType));
		}

		if (returnCode == null) {
			return;
		}

		logger.info("Creating receipt for the presentment exclude reason {} and Return Code {}", excludeReason,
				returnCode);

		createPresentmentReceipt(receiptDTO);

		Date appDate = fm.getAppDate();

		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setEodDate(pd.getAppDate());
		custEODEvent.setCustomer(receiptDTO.getCustomer());

		FinEODEvent finEODEvent = new FinEODEvent();
		fm.setAppDate(pd.getAppDate());
		finEODEvent.setFinanceMain(fm);
		finEODEvent.setFinType(receiptDTO.getFinType());
		finEODEvent.setFinProfitDetail(receiptDTO.getProfitDetail());
		finEODEvent.setFinanceScheduleDetails(receiptDTO.getSchedules());
		finEODEvent.setFinODDetails(finODDetailsDAO.getFinODBalByFinRef(fm.getFinID()));

		List<FinEODEvent> list = new ArrayList<>();
		list.add(finEODEvent);

		custEODEvent.setFinEODEvents(list);

		FinReceiptHeader rch = receiptDTO.getFinReceiptHeader();

		pd.setStatus(RepayConstants.PEXC_BOUNCE);
		pd.setReceiptID(rch.getReceiptID());
		pd.setBounceCode(returnCode);

		logger.info("Bouncing the receipt with Bounce Code {}", returnCode);
		receiptCancellationService.presentmentCancellation(pd, custEODEvent);

		fm.setAppDate(appDate);

		presentmentDetailDAO.updatePresentmentIdAsZero(pd.getId());
	}

	private PresentmentDetail preparePD(ReceiptDTO receiptDTO, int idxPresentment) {
		List<FinanceScheduleDetail> schedules = receiptDTO.getSchedules();
		FinanceScheduleDetail schedule = schedules.get(idxPresentment);

		FinanceMain fm = receiptDTO.getFinanceMain();
		Customer customer = receiptDTO.getCustomer();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		Date schDate = schedule.getSchDate();

		BigDecimal profitDue = schedule.getProfitSchd().subtract(schedule.getSchdPftPaid());
		BigDecimal principalDue = schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid());
		BigDecimal feeDue = schedule.getFeeSchd().subtract(schedule.getSchdFeePaid());

		if (schedule.isTDSApplicable()) {
			profitDue = profitDue.subtract(receiptCalculator.getTDS(fm, profitDue));
		}

		BigDecimal odAmount = BigDecimal.ZERO;

		if (ProductUtil.isOverDraftChargeReq(fm)) {
			String custBranch = customer.getCustAddrProvince();
			odAmount = overdrafLoanService.calculateODAmounts(fm, schDate, custBranch).getCurOverdraftTxnChrg();
		}

		BigDecimal dueAmount = profitDue.add(principalDue).add(feeDue).add(odAmount);

		if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("No dues to create receipt.");
			return null;
		}

		List<FinExcessAmount> emiInAdvance = receiptDTO.getEmiInAdvance();

		BigDecimal balanceAmount = BigDecimal.ZERO;
		for (FinExcessAmount fea : emiInAdvance) {
			balanceAmount = balanceAmount.add(fea.getBalanceAmt());
		}

		if (balanceAmount.compareTo(dueAmount) >= 0) {
			balanceAmount = dueAmount;
		}

		PresentmentDetail pd = new PresentmentDetail();
		pd.setFinID(finID);
		pd.setFinReference(finReference);
		pd.setSchDate(schDate);
		pd.setAdvanceAmt(balanceAmount);
		pd.setPresentmentAmt(BigDecimal.ZERO);

		logger.info("Presentment Detail Bean Created.");
		return pd;
	}

	private FinReceiptHeader prepareRCH(ReceiptDTO receiptDTO, BigDecimal receiptAmount) {
		FinReceiptHeader rch = new FinReceiptHeader();

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		rch.setFinID(pd.getFinID());
		rch.setReference(pd.getFinReference());
		rch.setReceiptDate(pd.getSchDate());
		rch.setRealizationDate(pd.getSchDate());
		rch.setReceiptAmount(receiptAmount);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setActFinReceipt(true);
		rch.setReceivedDate(receiptDTO.getBussinessDate());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setLastMntBy(pd.getLastMntBy());
		rch.setVersion(rch.getVersion() + 1);
		rch.setLogSchInPresentment(true);
		rch.setPostBranch(PennantConstants.APP_PHASE_EOD);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		return rch;
	}

	private FinReceiptDetail prepareRCD(ReceiptDTO receiptDTO, BigDecimal receiptAmount) {
		FinReceiptDetail rcd = new FinReceiptDetail();

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setValueDate(pd.getSchDate());
		rcd.setReceivedDate(receiptDTO.getBussinessDate());
		rcd.setAmount(receiptAmount);
		rcd.setDueAmount(receiptAmount);
		rcd.setFundingAc(pd.getPartnerBankId());
		rcd.setPartnerBankAc(pd.getAccountNo());
		rcd.setPartnerBankAcType(pd.getAcType());

		return rcd;
	}

	@Autowired
	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setLoanPaymentService(LoanPaymentService loanPaymentService) {
		this.loanPaymentService = loanPaymentService;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setProfitDetailDAO(FinanceProfitDetailDAO profitDetailDAO) {
		this.profitDetailDAO = profitDetailDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setGenerateLetterService(GenerateLetterService generateLetterService) {
		this.generateLetterService = generateLetterService;
	}

}