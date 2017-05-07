package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	private RepaymentProcessUtil	repaymentProcessUtil;

	/**
	 * @param custId
	 * @param custEODEvents
	 * @param date
	 * @throws Exception
	 */
	public void processrReceipts(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date businessDate = custEODEvent.getEodValueDate();
		for (FinEODEvent finEODEvent : finEODEvents) {
			List<PresentmentDetail> presentmentList = finEODEvent.getPresentmentDetails();

			for (PresentmentDetail presentmentDetail : presentmentList) {

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

				//work flow details
				header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

				List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

				FinEODEvent eodEvent = getFinEODEvent(custEODEvent, finref);
				FinanceMain financeMain = eodEvent.getFinanceMain();
				FinanceProfitDetail profitDetail = eodEvent.getFinProfitDetail();

				List<FinanceScheduleDetail> scheduleDetails = eodEvent.getFinanceScheduleDetails();
				String repayHeirarchy = eodEvent.getFinType().getRpyHierarchy();

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
					receiptDetails.add(receiptDetail);
				}

				header.setReceiptDetails(receiptDetails);
				repaymentProcessUtil.calcualteAndPayReceipt(financeMain, scheduleDetails, profitDetail, header,
						repayHeirarchy, businessDate);

			}

		}

	}

	private FinEODEvent getFinEODEvent(CustEODEvent custEODEvent, String finref) {
		List<FinEODEvent> custEODEvents = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEODEvent : custEODEvents) {
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
