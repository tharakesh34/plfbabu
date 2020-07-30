package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.customermasters.Customer;
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

public class ReceiptPaymentService extends ServiceHelper {
	private static final long serialVersionUID = 1442146139821584760L;
	private static Logger logger = Logger.getLogger(ReceiptPaymentService.class);

	private FinExcessAmountDAO finExcessAmountDAO;
	private RepaymentProcessUtil repaymentProcessUtil;
	private ReceiptCalculator receiptCalculator;

	/**
	 * @param custId
	 * @param custEODEvents
	 * @param date
	 * @throws Exception
	 */
	public void processrReceipts(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		//	check at least one banking presentation  exists or not.
		boolean presetment = false;
		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getIdxPresentment() >= 0) {
				presetment = true;
				break;
			}
		}

		if (!presetment) {
			return;
		}

		//if banking presentation exists then fetch all the banking presentation related to the customer at once process accordingly

		Date businessDate = custEODEvent.getEodValueDate();
		Customer customer = custEODEvent.getCustomer();
		long custID = customer.getCustID();

		List<PresentmentDetail> presentments = getPresentmentDetailDAO().getPresentmenToPost(custID, businessDate);

		for (FinEODEvent finEODEvent : finEODEvents) {

			if (finEODEvent.getIdxPresentment() <= 0) {
				continue;
			}

			FinanceMain finMain = finEODEvent.getFinanceMain();
			String finReference = finMain.getFinReference();

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
						tdsDue = receiptCalculator.getTDS(finMain, pftDue);
					}

					BigDecimal priDue = sch.getPrincipalSchd().subtract(sch.getSchdPriPaid());
					BigDecimal feeDue = sch.getFeeSchd().subtract(sch.getSchdFeePaid());
					BigDecimal schAmtDue = pftDue.subtract(tdsDue).add(priDue).add(feeDue);

					if (schAmtDue.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					PresentmentDetail pDetail = new PresentmentDetail();
					// Schedule Setup
					pDetail.setFinReference(finReference);
					pDetail.setSchDate(sch.getSchDate());

					if (emiInAdvanceAmt.compareTo(schAmtDue) >= 0) {
						pDetail.setAdvanceAmt(schAmtDue);
					} else {
						pDetail.setAdvanceAmt(emiInAdvanceAmt);
					}

					pDetail.setPresentmentAmt(BigDecimal.ZERO);
					pDetail.setExcessID(finExcessAmount.getExcessID());
					processprestment(pDetail, finEODEvent, customer, businessDate, true, false);
				}
			}
		}

		logger.debug(" Leaving ");

	}

	private void processprestment(PresentmentDetail presentmentDetail, FinEODEvent finEODEvent, Customer customer,
			Date businessDate, boolean noReserve, boolean isPDetailsExits) throws Exception {

		String finref = presentmentDetail.getFinReference();
		Date schDate = presentmentDetail.getSchDate();
		BigDecimal advanceAmt = presentmentDetail.getAdvanceAmt();
		BigDecimal presentmentAmt = presentmentDetail.getPresentmentAmt();

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finref);
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

		if (!isPDetailsExits) {
			header.setReceiptMode(RepayConstants.RECEIPTMODE_EXCESS);
		}
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);

		/**
		 * Added below line on 29-NOV-19 to update the realization date (On presentment realization date is not getting
		 * update).
		 */

		header.setRealizationDate(SysParamUtil.getAppDate());

		header.setLogSchInPresentment(true);
		header.setPostBranch("EOD");//FIXME

		//work flow details
		header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinanceMain financeMain = finEODEvent.getFinanceMain();
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

		List<FinanceScheduleDetail> scheduleDetails = finEODEvent.getFinanceScheduleDetails();
		String repayHeirarchy = finEODEvent.getFinType().getRpyHierarchy();

		FinReceiptDetail receiptDetail = null;
		/*
		 * we are storing excessID in case of EMIAdvance case only and in advance Amount Other Advance amounts also
		 * included like ADVINT,ADVEMI
		 */
		long excessID = presentmentDetail.getExcessID();
		if (advanceAmt.compareTo(BigDecimal.ZERO) > 0 && excessID != 0) {
			processAdvanceEMi(presentmentDetail, finEODEvent, customer, businessDate, noReserve);
		}

		if (presentmentAmt.compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_PRESENTMENT);
			receiptDetail.setAmount(presentmentAmt);
			receiptDetail.setDueAmount(presentmentAmt);
			receiptDetail.setValueDate(schDate);
			receiptDetail.setReceivedDate(businessDate);
			receiptDetail.setFundingAc(presentmentDetail.getPartnerBankId());
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetails.add(receiptDetail);

			header.setReceiptDetails(receiptDetails);
			repaymentProcessUtil.calcualteAndPayReceipt(financeMain, customer, scheduleDetails, null, profitDetail,
					header, repayHeirarchy, businessDate, businessDate);
			if (presentmentDetail.getId() != Long.MIN_VALUE) {
				getPresentmentDetailDAO().updateReceptId(presentmentDetail.getId(), header.getReceiptID());
			}
		}
	}

	private void processAdvanceEMi(PresentmentDetail detail, FinEODEvent finEODEvent, Customer customer,
			Date businessDate, boolean noReserve) throws Exception {

		BigDecimal advanceAmount = detail.getAdvanceAmt();

		if (advanceAmount.compareTo(BigDecimal.ZERO) < 0) {
			return;
		}

		String finref = detail.getFinReference();
		Date schDate = detail.getSchDate();

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finref);
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

		FinanceMain financeMain = finEODEvent.getFinanceMain();
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

		List<FinanceScheduleDetail> scheduleDetails = finEODEvent.getFinanceScheduleDetails();
		String repayHeirarchy = finEODEvent.getFinType().getRpyHierarchy();
		repaymentProcessUtil.calcualteAndPayReceipt(financeMain, customer, scheduleDetails, null, profitDetail, header,
				repayHeirarchy, businessDate, businessDate);

	}

	private PresentmentDetail getPresentmentDetail(List<PresentmentDetail> presentmentDetails, String finref,
			Date schDate) {

		for (PresentmentDetail presentmentDetail : presentmentDetails) {
			if (presentmentDetail.getFinReference().equals(finref)
					&& presentmentDetail.getSchDate().compareTo(schDate) == 0) {
				return presentmentDetail;
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

}
