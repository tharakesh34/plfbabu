package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.RepaymentProcessUtil;
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

		//	check at least one presentment exists or not.
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

		//if presentment exists then fetch all the presentment related to the customer at once process accordingly

		Date businessDate = custEODEvent.getEodValueDate();
		long custID = custEODEvent.getCustomer().getCustID();

		List<PresentmentDetail> presentments = getPresentmentHeaderDAO().getPresentmenToPost(custID, businessDate);

		for (PresentmentDetail presentmentDetail : presentments) {

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
			header.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
			header.setLogSchInPresentment(true);
			header.setPostBranch("EOD");//FIXME

			//work flow details
			header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();
			FinEODEvent finEODEvent = getFinEODEvent(finEODEvents, finref);

			FinanceMain financeMain = finEODEvent.getFinanceMain();
			FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();

			List<FinanceScheduleDetail> scheduleDetails = finEODEvent.getFinanceScheduleDetails();
			String repayHeirarchy = finEODEvent.getFinType().getRpyHierarchy();

			FinReceiptDetail receiptDetail = null;
			if (advanceAmt.compareTo(BigDecimal.ZERO) > 0) {
				receiptDetail = new FinReceiptDetail();
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
				receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
				receiptDetail.setAmount(advanceAmt);
				receiptDetail.setValueDate(schDate);
				receiptDetail.setReceivedDate(businessDate);
				receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
				receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
				receiptDetails.add(receiptDetail);

			}

			if (presentmentAmt.compareTo(BigDecimal.ZERO) > 0) {
				receiptDetail = new FinReceiptDetail();
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
				receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
				receiptDetail.setAmount(presentmentAmt);
				receiptDetail.setValueDate(schDate);
				receiptDetail.setReceivedDate(businessDate);
				receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
				receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
				receiptDetails.add(receiptDetail);
			}

			header.setReceiptDetails(receiptDetails);
			repaymentProcessUtil.calcualteAndPayReceipt(financeMain, custEODEvent.getCustomer(), scheduleDetails,null,
					profitDetail, header, repayHeirarchy, businessDate);
			getPresentmentHeaderDAO().updateReceptId(presentmentDetail.getId(), header.getReceiptID());

		}

		logger.debug(" Leaving ");

	}

	private FinEODEvent getFinEODEvent(List<FinEODEvent> finEODEvents, String finref) {

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getFinanceMain().getFinReference().equals(finref)) {
				return finEODEvent;
			}

		}
		return null;
	}

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

}
