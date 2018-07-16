package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;

public class ReceiptPaymentService extends ServiceHelper {
	private static final long		serialVersionUID	= 1442146139821584760L;
	private static Logger			logger				= Logger.getLogger(ReceiptPaymentService.class);

	@Autowired
	private FinExcessAmountDAO	finExcessAmountDAO;
	private RepaymentProcessUtil	repaymentProcessUtil;

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

				processprestment(prestDetails, finEODEvent, customer, businessDate,false);

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

					BigDecimal schAmtDue = sch.getProfitSchd().add(sch.getPrincipalSchd()).add(sch.getFeeSchd())
							.subtract(sch.getSchdPriPaid()).subtract(sch.getSchdPftPaid())
							.subtract(sch.getSchdFeePaid()).subtract(sch.getTDSAmount());
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
					processprestment(pDetail, finEODEvent, customer, businessDate, true);
				}
			}
		}

		logger.debug(" Leaving ");

	}

	private void processprestment(PresentmentDetail presentmentDetail, FinEODEvent finEODEvent, Customer customer,
			Date businessDate,boolean noReserve) throws Exception {

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
		header.setExcessAdjustTo(PennantConstants.List_Select);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(advanceAmt.add(presentmentAmt));
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(RepayConstants.RECEIPTMODE_PRESENTMENT);
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
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
		if (advanceAmt.compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_EMIINADV);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(advanceAmt);
			receiptDetail.setValueDate(schDate);
			receiptDetail.setReceivedDate(businessDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetail.setNoReserve(noReserve);
			receiptDetails.add(receiptDetail);

		}

		if (presentmentAmt.compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_PRESENTMENT);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentAmt);
			receiptDetail.setValueDate(schDate);
			receiptDetail.setReceivedDate(businessDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetails.add(receiptDetail);
			
		}

		header.setReceiptDetails(receiptDetails);
		repaymentProcessUtil.calcualteAndPayReceipt(financeMain, customer, scheduleDetails, null, profitDetail, header,
				repayHeirarchy, businessDate,businessDate);
		if (presentmentDetail.getId() != Long.MIN_VALUE) {
			getPresentmentDetailDAO().updateReceptId(presentmentDetail.getId(), header.getReceiptID());
		}

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

}
