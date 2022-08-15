package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class ReceiptPaymentService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(ReceiptPaymentService.class);

	private FinExcessAmountDAO finExcessAmountDAO;
	private RepaymentProcessUtil repaymentProcessUtil;
	private ReceiptCalculator receiptCalculator;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailDAO;
	private LoanPaymentService loanPaymentService;

	public void processrReceipts(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		// check at least one banking presentation exists or not.
		boolean presentment = false;

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getIdxPresentment() >= 0) {
				presentment = true;
				break;
			}
		}

		if (!presentment) {
			return;
		}

		// if banking presentation exists then fetch all the banking presentation related to the customer at once
		// process accordingly

		Date businessDate = custEODEvent.getEodValueDate();
		Customer customer = custEODEvent.getCustomer();
		long custID = customer.getCustID();

		List<PresentmentDetail> presentments = presentmentDetailDAO.getPresentmenToPost(custID, businessDate);

		for (FinEODEvent finEODEvent : finEODEvents) {

			if (finEODEvent.getIdxPresentment() <= 0) {
				continue;
			}

			FinanceMain fm = finEODEvent.getFinanceMain();
			long finID = fm.getFinID();
			String finReference = fm.getFinReference();

			// check banking presentation exists
			PresentmentDetail prestDetails = getPresentmentDetail(presentments, finReference, businessDate);
			if (prestDetails != null) {
				processprestment(prestDetails, finEODEvent, customer, businessDate, false, true);

			} else {
				// if banking presentation not exists check advance EMI
				FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
						RepayConstants.EXAMOUNTTYPE_EMIINADV);

				if (finExcessAmount != null) {
					BigDecimal emiInAdvanceAmt = finExcessAmount.getBalanceAmt();

					if (emiInAdvanceAmt.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					FinanceScheduleDetail sch = finEODEvent.getFinanceScheduleDetails()
							.get(finEODEvent.getIdxPresentment());

					BigDecimal pftDue = sch.getProfitSchd().subtract(sch.getSchdPftPaid());

					BigDecimal balanceAmount = BigDecimal.ZERO;
					if (ProductUtil.isOverDraftChargeReq(fm)) {
						String custBranch = custEODEvent.getCustomer().getCustAddrProvince();
						balanceAmount = overdrafLoanService.calculateODAmounts(fm, sch.getSchDate(), custBranch)
								.getCurOverdraftTxnChrg();
					}

					BigDecimal tdsDue = BigDecimal.ZERO;
					if (sch.isTDSApplicable()) {
						tdsDue = receiptCalculator.getTDS(fm, pftDue);
					}

					BigDecimal priDue = sch.getPrincipalSchd().subtract(sch.getSchdPriPaid());
					BigDecimal feeDue = sch.getFeeSchd().subtract(sch.getSchdFeePaid());
					BigDecimal schAmtDue = pftDue.subtract(tdsDue).add(priDue).add(feeDue).add(balanceAmount);

					if (schAmtDue.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					PresentmentDetail pd = new PresentmentDetail();
					// Schedule Setup
					pd.setFinID(finID);
					pd.setFinReference(finReference);
					pd.setSchDate(sch.getSchDate());

					if (emiInAdvanceAmt.compareTo(schAmtDue) >= 0) {
						pd.setAdvanceAmt(schAmtDue);
					} else {
						pd.setAdvanceAmt(emiInAdvanceAmt);
					}

					pd.setPresentmentAmt(BigDecimal.ZERO);
					pd.setExcessID(finExcessAmount.getExcessID());
					processprestment(pd, finEODEvent, customer, businessDate, true, false);
				}
			}
		}

		logger.debug(Literal.LEAVING);

	}

	public void processprestment(PresentmentDetail pd, FinEODEvent finEODEvent, Customer customer, Date businessDate,
			boolean noReserve, boolean isPDetailsExits) throws Exception {

		long finID = pd.getFinID();
		String finReference = pd.getFinReference();

		Date schDate = pd.getSchDate();
		BigDecimal advanceAmt = pd.getAdvanceAmt();
		BigDecimal presentmentAmt = pd.getPresentmentAmt();

		FinReceiptHeader rch = new FinReceiptHeader();

		rch.setFinID(finID);
		rch.setReference(finReference);
		rch.setReceiptDate(schDate);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);

		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		rch.setReceiptAmount(presentmentAmt);// header.setReceiptAmount(advanceAmt.add(presentmentAmt));
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setActFinReceipt(true);
		rch.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
		rch.setReceivedDate(businessDate);
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setLastMntBy(pd.getLastMntBy());
		rch.setVersion(rch.getVersion() + 1);

		if (!isPDetailsExits) {
			rch.setReceiptMode(RepayConstants.RECEIPTMODE_EXCESS);
		}
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);

		/**
		 * Added below line on 29-NOV-19 to update the realization date (On presentment realization date is not getting
		 * update).
		 */

		FinanceMain fm = finEODEvent.getFinanceMain();

		EventProperties eventProperties = fm.getEventProperties();
		if (eventProperties.isParameterLoaded()) {
			rch.setRealizationDate(eventProperties.getAppDate());
		} else {
			rch.setRealizationDate(SysParamUtil.getAppDate());
		}

		rch.setLogSchInPresentment(true);
		rch.setPostBranch("EOD");

		// work flow details
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		List<FinReceiptDetail> receiptDetails = new ArrayList<>();

		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		/*
		 * we are storing excessID in case of EMIAdvance case only and in advance Amount Other Advance amounts also
		 * included like ADVINT,ADVEMI
		 */
		long excessID = pd.getExcessID();
		if (advanceAmt.compareTo(BigDecimal.ZERO) > 0 && excessID != 0) {
			processAdvanceEMi(pd, finEODEvent, customer, businessDate, noReserve);
		}

		if (ImplementationConstants.PRESENT_RECEIPTS_ON_RESP) {
			logger.info("Stop creating presentment receipts on EOD.");
			return;
		}

		if (presentmentAmt.compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("Presentment Receipts are not creating, due to presentment amount is ZERO");
			return;
		}

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(RepayConstants.RECEIPTMODE_PRESENTMENT);
		rcd.setAmount(presentmentAmt);
		rcd.setDueAmount(presentmentAmt);
		rcd.setValueDate(schDate);
		rcd.setReceivedDate(businessDate);
		rcd.setFundingAc(pd.getPartnerBankId());
		rcd.setPartnerBankAc(pd.getAccountNo());
		rcd.setPartnerBankAcType(pd.getAcType());
		receiptDetails.add(rcd);

		rch.setReceiptDetails(receiptDetails);

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setCustomer(customer);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setFees(null);
		receiptDTO.setProfitDetail(profitDetail);
		receiptDTO.setFinReceiptHeader(rch);
		receiptDTO.setPresentmentHeader(null);
		receiptDTO.setPresentmentDetail(null);
		receiptDTO.setValuedate(businessDate);
		receiptDTO.setPostDate(businessDate);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);
		financeMainDAO.updateSchdVersion(fm, true);
		if (pd.getId() != Long.MIN_VALUE) {
			pd.setReceiptID(rch.getReceiptID());
			presentmentDetailDAO.updateReceptIdAndAmounts(pd);
		}
	}

	private void processAdvanceEMi(PresentmentDetail pd, FinEODEvent finEODEvent, Customer customer, Date businessDate,
			boolean noReserve) throws Exception {

		BigDecimal advanceAmount = pd.getAdvanceAmt();

		if (advanceAmount.compareTo(BigDecimal.ZERO) < 0) {
			return;
		}

		long finID = pd.getFinID();
		String finReference = pd.getFinReference();
		Date schDate = pd.getSchDate();

		FinReceiptHeader rch = new FinReceiptHeader();

		rch.setFinID(finID);
		rch.setReference(finReference);
		rch.setReceiptDate(schDate);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		rch.setReceiptAmount(pd.getAdvanceAmt());
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(RepayConstants.PAYTYPE_EXCESS);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setRealizationDate(schDate);
		rch.setActFinReceipt(true);
		rch.setLogSchInPresentment(true);
		rch.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
		rch.setPostBranch("EOD");// FIXME
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setReceivedDate(businessDate);
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setLastMntBy(pd.getLastMntBy());
		rch.setVersion(rch.getVersion() + 1);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
		rcd.setPayAgainstID(pd.getExcessID());
		rcd.setAmount(pd.getAdvanceAmt());
		rcd.setDueAmount(pd.getAdvanceAmt());
		rcd.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rcd.setValueDate(schDate);
		rcd.setFundingAc(pd.getPartnerBankId());
		rcd.setReceivedDate(businessDate);
		rcd.setPartnerBankAc(pd.getAccountNo());
		rcd.setPartnerBankAcType(pd.getAcType());
		rcd.setNoReserve(noReserve);

		receiptDetails.add(rcd);

		rch.setReceiptDetails(receiptDetails);

		XcessPayables xcessPayable = new XcessPayables();
		xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
		xcessPayable.setAmount(pd.getAdvanceAmt());
		xcessPayable.setTotPaidNow(pd.getAdvanceAmt());

		rch.getXcessPayables().add(xcessPayable);

		FinanceMain fm = finEODEvent.getFinanceMain();
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setCustomer(customer);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setFees(null);
		receiptDTO.setProfitDetail(profitDetail);
		receiptDTO.setFinReceiptHeader(rch);
		receiptDTO.setPresentmentHeader(null);
		receiptDTO.setPresentmentDetail(null);
		receiptDTO.setValuedate(businessDate);
		receiptDTO.setPostDate(businessDate);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);

		EventProperties eventProperties = fm.getEventProperties();
		Date appDate = null;

		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		LoanPayment lp = new LoanPayment(finID, fm.getFinReference(), schedules, appDate);
		boolean isFinFullyPaid = loanPaymentService.isSchdFullyPaid(lp);

		if (isFinFullyPaid) {
			if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())
					&& DateUtil.compare(appDate, fm.getMaturityDate()) < 0) {
				return;
			}

			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, appDate);
			profitDetailDAO.updateFinPftMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false);

		}
	}

	private PresentmentDetail getPresentmentDetail(List<PresentmentDetail> pd, String finReference, Date schDate) {

		for (PresentmentDetail detail : pd) {
			if (detail.getFinReference().equals(finReference) && detail.getSchDate().compareTo(schDate) == 0) {
				return detail;
			}

		}
		return null;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
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
	public void setLoanPaymentService(LoanPaymentService loanPaymentService) {
		this.loanPaymentService = loanPaymentService;
	}

	@Autowired
	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

}
