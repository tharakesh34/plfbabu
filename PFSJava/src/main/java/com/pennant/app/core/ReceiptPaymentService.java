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
import com.pennant.app.util.RepaymentPostingsUtil;
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
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReceiptPaymentService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private static Logger logger = LogManager.getLogger(ReceiptPaymentService.class);

	private FinExcessAmountDAO finExcessAmountDAO;
	private RepaymentProcessUtil repaymentProcessUtil;
	private ReceiptCalculator receiptCalculator;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailDAO;

	public void processrReceipts(CustEODEvent custEODEvent) throws Exception {
		logger.debug(Literal.ENTERING);
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		//	check at least one banking presentation  exists or not.
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

		//if banking presentation exists then fetch all the banking presentation related to the customer at once process accordingly

		Date businessDate = custEODEvent.getEodValueDate();
		Customer customer = custEODEvent.getCustomer();
		long custID = customer.getCustID();

		List<PresentmentDetail> presentments = presentmentDetailDAO.getPresentmenToPost(custID, businessDate);

		for (FinEODEvent finEODEvent : finEODEvents) {

			if (finEODEvent.getIdxPresentment() <= 0) {
				continue;
			}

			FinanceMain fm = finEODEvent.getFinanceMain();
			String finReference = fm.getFinReference();

			//check banking presentation exists
			PresentmentDetail prestDetails = getPresentmentDetail(presentments, finReference, businessDate);
			if (prestDetails != null) {
				processprestment(prestDetails, finEODEvent, customer, businessDate, false, true);

			} else {
				//if banking presentation not exists check advance EMI			
				FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
						RepayConstants.EXAMOUNTTYPE_EMIINADV);

				if (finExcessAmount != null) {
					BigDecimal emiInAdvanceAmt = finExcessAmount.getBalanceAmt();

					if (emiInAdvanceAmt.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					FinanceScheduleDetail sch = finEODEvent.getFinanceScheduleDetails()
							.get(finEODEvent.getIdxPresentment());

					BigDecimal pftDue = sch.getProfitSchd().subtract(sch.getSchdPftPaid());

					BigDecimal tdsDue = BigDecimal.ZERO;
					if (sch.isTDSApplicable()) {
						tdsDue = receiptCalculator.getTDS(fm, pftDue);
					}

					BigDecimal priDue = sch.getPrincipalSchd().subtract(sch.getSchdPriPaid());
					BigDecimal feeDue = sch.getFeeSchd().subtract(sch.getSchdFeePaid());
					BigDecimal schAmtDue = pftDue.subtract(tdsDue).add(priDue).add(feeDue);

					if (schAmtDue.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					PresentmentDetail pd = new PresentmentDetail();
					// Schedule Setup
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

	private void processprestment(PresentmentDetail pd, FinEODEvent finEODEvent, Customer customer, Date businessDate,
			boolean noReserve, boolean isPDetailsExits) throws Exception {

		String finReference = pd.getFinReference();
		Date schDate = pd.getSchDate();
		BigDecimal advanceAmt = pd.getAdvanceAmt();
		BigDecimal presentmentAmt = pd.getPresentmentAmt();

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finReference);
		header.setReceiptDate(schDate);
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);

		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(presentmentAmt);//header.setReceiptAmount(advanceAmt.add(presentmentAmt));
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setActFinReceipt(true);
		header.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
		header.setReceivedDate(businessDate);
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setLastMntBy(pd.getLastMntBy());
		header.setVersion(header.getVersion() + 1);

		if (!isPDetailsExits) {
			header.setReceiptMode(RepayConstants.RECEIPTMODE_EXCESS);
		}
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);

		/**
		 * Added below line on 29-NOV-19 to update the realization date (On presentment realization date is not getting
		 * update).
		 */

		FinanceMain fm = finEODEvent.getFinanceMain();

		EventProperties eventProperties = fm.getEventProperties();
		if (eventProperties.isParameterLoaded()) {
			header.setRealizationDate(eventProperties.getAppDate());
		} else {
			header.setRealizationDate(SysParamUtil.getAppDate());
		}

		header.setLogSchInPresentment(true);
		header.setPostBranch("EOD");//FIXME

		//work flow details
		header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		List<FinReceiptDetail> receiptDetails = new ArrayList<>();

		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		String repayHeirarchy = finEODEvent.getFinType().getRpyHierarchy();

		FinReceiptDetail receiptDetail = null;
		/*
		 * we are storing excessID in case of EMIAdvance case only and in advance Amount Other Advance amounts also
		 * included like ADVINT,ADVEMI
		 */
		long excessID = pd.getExcessID();
		if (advanceAmt.compareTo(BigDecimal.ZERO) > 0 && excessID != 0) {
			processAdvanceEMi(pd, finEODEvent, customer, businessDate, noReserve);
		}

		if (ImplementationConstants.PRESEMENT_STOP_RECEIPTS_ON_EOD && presentmentAmt.compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_PRESENTMENT);
			receiptDetail.setAmount(presentmentAmt);
			receiptDetail.setDueAmount(presentmentAmt);
			receiptDetail.setValueDate(schDate);
			receiptDetail.setReceivedDate(businessDate);
			receiptDetail.setFundingAc(pd.getPartnerBankId());
			receiptDetail.setPartnerBankAc(pd.getAccountNo());
			receiptDetail.setPartnerBankAcType(pd.getAcType());
			receiptDetails.add(receiptDetail);

			header.setReceiptDetails(receiptDetails);
			repaymentProcessUtil.calcualteAndPayReceipt(fm, customer, schedules, null, profitDetail, header,
					repayHeirarchy, businessDate, businessDate);
			if (pd.getId() != Long.MIN_VALUE) {
				presentmentDetailDAO.updateReceptId(pd.getId(), header.getReceiptID());
			}
		}
	}

	private void processAdvanceEMi(PresentmentDetail detail, FinEODEvent finEODEvent, Customer customer,
			Date businessDate, boolean noReserve) throws Exception {

		BigDecimal advanceAmount = detail.getAdvanceAmt();

		if (advanceAmount.compareTo(BigDecimal.ZERO) < 0) {
			return;
		}

		String finReference = detail.getFinReference();
		Date schDate = detail.getSchDate();

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finReference);
		header.setReceiptDate(schDate);
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(detail.getAdvanceAmt());
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(RepayConstants.PAYTYPE_EXCESS);
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		header.setRealizationDate(schDate);
		header.setActFinReceipt(true);
		header.setLogSchInPresentment(true);
		header.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
		header.setPostBranch("EOD");//FIXME
		header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		header.setReceivedDate(businessDate);
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setLastMntBy(detail.getLastMntBy());
		header.setVersion(header.getVersion() + 1);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail receiptDetail = new FinReceiptDetail();
		receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
		receiptDetail.setPayAgainstID(detail.getExcessID());
		receiptDetail.setAmount(detail.getAdvanceAmt());
		receiptDetail.setDueAmount(detail.getAdvanceAmt());
		receiptDetail.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		receiptDetail.setValueDate(schDate);
		receiptDetail.setFundingAc(detail.getPartnerBankId());
		receiptDetail.setReceivedDate(businessDate);
		receiptDetail.setPartnerBankAc(detail.getAccountNo());
		receiptDetail.setPartnerBankAcType(detail.getAcType());
		receiptDetail.setNoReserve(noReserve);

		receiptDetails.add(receiptDetail);

		header.setReceiptDetails(receiptDetails);

		XcessPayables xcessPayable = new XcessPayables();
		xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
		xcessPayable.setAmount(detail.getAdvanceAmt());
		xcessPayable.setTotPaidNow(detail.getAdvanceAmt());

		header.getXcessPayables().add(xcessPayable);

		FinanceMain fm = finEODEvent.getFinanceMain();
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

		List<FinanceScheduleDetail> scheduleDetails = finEODEvent.getFinanceScheduleDetails();
		String repayHeirarchy = finEODEvent.getFinType().getRpyHierarchy();
		repaymentProcessUtil.calcualteAndPayReceipt(fm, customer, scheduleDetails, null, profitDetail, header,
				repayHeirarchy, businessDate, businessDate);

		boolean isFinFullyPaid = repaymentPostingsUtil.isSchdFullyPaid(fm.getFinReference(), scheduleDetails);

		if (isFinFullyPaid) {
			EventProperties eventProperties = fm.getEventProperties();
			Date appDate = null;

			if (eventProperties.isParameterLoaded()) {
				appDate = eventProperties.getAppDate();
			} else {
				appDate = SysParamUtil.getAppDate();
			}

			financeMainDAO.updateMaturity(fm.getFinReference(), FinanceConstants.CLOSE_STATUS_MATURED, false, appDate);
			profitDetailDAO.updateFinPftMaturity(fm.getFinReference(), FinanceConstants.CLOSE_STATUS_MATURED, false);

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

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setProfitDetailDAO(FinanceProfitDetailDAO profitDetailDAO) {
		this.profitDetailDAO = profitDetailDAO;
	}
}
