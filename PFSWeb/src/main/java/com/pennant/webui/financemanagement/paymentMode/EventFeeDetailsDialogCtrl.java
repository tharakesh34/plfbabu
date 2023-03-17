package com.pennant.webui.financemanagement.paymentMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.webui.financemanagement.receipts.CrossLoanKnockOffDialogCtrl;
import com.pennant.webui.financemanagement.receipts.LoanClosureEnquiryDialogCtrl;
import com.pennant.webui.financemanagement.receipts.ReceiptDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class EventFeeDetailsDialogCtrl extends GFCBaseCtrl<ReceiptAllocationDetail> {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -1289603949144743239L;
	private static final Logger logger = LogManager.getLogger(EventFeeDetailsDialogCtrl.class);

	protected Window window_EventFeeDetails;
	protected Listbox listBounceDetails;

	protected Listheader listheader_Code;
	protected Listbox listDetails;

	private FinReceiptData receiptData;
	private String buttonId;
	private BigDecimal newPercent = BigDecimal.ZERO;

	private ReceiptAllocationDetail summary = null;
	private ReceiptAllocationDetail detail = null;
	private FinFeeDetail feeDetail = null;
	private ReceiptDialogCtrl receiptDialogCtrl;
	private LoanClosureEnquiryDialogCtrl loanClosureEnquiryDialogCtrl;
	private boolean isLoanClosure = false;
	private ReceiptCalculator receiptCalculator;
	private CrossLoanKnockOffDialogCtrl crossLoanKnockOffDialogCtrl;

	public void onCreate$window_EventFeeDetails(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EventFeeDetails);
		try {

			if (arguments.containsKey("data")) {
				this.receiptData = (FinReceiptData) arguments.get("data");
			}
			if (arguments.containsKey("receiptDialogCtrl")) {
				this.receiptDialogCtrl = (ReceiptDialogCtrl) arguments.get("receiptDialogCtrl");
			}
			if (arguments.containsKey("crossLoanKnockOffDialogCtrl")) {
				this.crossLoanKnockOffDialogCtrl = (CrossLoanKnockOffDialogCtrl) arguments
						.get("crossLoanKnockOffDialogCtrl");
			}
			if (arguments.containsKey("loanClosureEnquiryDialogCtrl")) {
				this.loanClosureEnquiryDialogCtrl = (LoanClosureEnquiryDialogCtrl) arguments
						.get("loanClosureEnquiryDialogCtrl");
			}
			if (arguments.containsKey("isLoanClosure")) {
				this.isLoanClosure = (boolean) arguments.get("isLoanClosure");
			}

			if (arguments.containsKey("buttonId")) {
				this.buttonId = (String) arguments.get("buttonId");
				setButtonId(this.buttonId);
			} else {
				setButtonId("");
			}

			doSetFieldProperties();
			this.window_EventFeeDetails.setHeight(borderLayoutHeight - 15 + "px");
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_EventFeeDetails.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	public void doSetFieldProperties() {
		logger.debug("Entering");

		setDialog(DialogType.EMBEDDED);
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		summary = rch.getAllocationsSummary().get(Integer.parseInt(buttonId));

		for (FinFeeDetail fee : receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
			if (summary.getAllocationTo() == -(fee.getFeeTypeID())) {
				feeDetail = fee;
				break;
			}
		}
		for (ReceiptAllocationDetail allocate : rch.getAllocations()) {
			if (summary.getAllocationTo() == allocate.getAllocationTo()) {
				detail = allocate;
				break;
			}
		}
		doFillDetails(feeDetail);

		logger.debug("Leaving");
	}

	public void doFillDetails(FinFeeDetail feeDetail) {

		logger.debug("Entering");
		this.listDetails.getItems().clear();
		Listitem item = null;
		Listcell lc = null;

		item = new Listitem();
		lc = new Listcell(feeDetail.getFeeTypeDesc());
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);

		lc = new Listcell(feeDetail.getPercentage().toString());
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		CurrencyBox allocationPaid = new CurrencyBox();
		allocationPaid.setStyle("text-align:right;");
		allocationPaid.setId("NewPercentage");
		setProps(allocationPaid, false, 2, 120);
		if (!isLoanClosure) {
			allocationPaid.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_excessAdjustTo"));
		}
		if (crossLoanKnockOffDialogCtrl != null) {
			allocationPaid.setReadonly(false);
		}
		allocationPaid.setValue(feeDetail.getActPercentage().toString());
		allocationPaid.addForward("onFulfill", this.window_EventFeeDetails, "onAllocatePaidChange", 1);
		lc.appendChild(allocationPaid);
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(feeDetail.getCalculatedAmount(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		/*
		 * // While Click on Fee Details button at the first time Amount // calculation wrong for New Fee Percent fixed.
		 * FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(); BigDecimal
		 * calculatedAmt = feeDetail.getCalculatedOn(); calculatedAmt =
		 * calculatedAmt.multiply(feeDetail.getActPercentage()).divide(BigDecimal.valueOf(100), 2,
		 * RoundingMode.HALF_DOWN); calculatedAmt = CalculationUtil.roundAmount(calculatedAmt,
		 * financeMain.getCalRoundingMode(), financeMain.getRoundingTarget()); feeDetail.setActualAmount(calculatedAmt);
		 */

		lc = new Listcell(PennantApplicationUtil.amountFormate(feeDetail.getActualAmount(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		this.listDetails.appendChild(item);

		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		receiptCalculator.setXcessPayables(receiptData);
		receiptCalculator.recalAutoAllocation(receiptData, false);

		if (isLoanClosure) {
			loanClosureEnquiryDialogCtrl.doFillAllocationDetail();
		} else if (crossLoanKnockOffDialogCtrl != null) {
			crossLoanKnockOffDialogCtrl.setReceiptData(receiptData);
			crossLoanKnockOffDialogCtrl.doFillAllocationDetail();
		} else {
			receiptDialogCtrl.setReceiptData(receiptData);
			receiptDialogCtrl.doFillAllocationDetail();
		}
		doClose(this.btnSave.isVisible());
		logger.debug("Leaving" + event.toString());

	}

	public void onAllocatePaidChange(ForwardEvent event) {
		String id = "NewPercentage";
		CurrencyBox newPercentage = (CurrencyBox) this.listDetails.getFellow(id);
		newPercent = newPercentage.getValidateValue();
		feeDetail.setActPercentage(newPercent);
		BigDecimal gstAmount = BigDecimal.ZERO;
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal calculatedAmt = feeDetail.getCalculatedOn();
		FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		if (PennantConstants.FEE_CALCULATEDON_ADJUSTEDPRINCIPAL.equals(feeDetail.getCalculateOn())) {
			feeDetail.setPercentage(newPercent);
			calculatedAmt = feeDetail.getActualOldAmount();

			BigDecimal calcPerc = BigDecimal.ONE;
			if (feeDetail.getPercentage().compareTo(BigDecimal.ZERO) > 0
					&& calculatedAmt.compareTo(BigDecimal.ZERO) > 0) {

				BigDecimal feePercent = feeDetail.getPercentage().divide(BigDecimal.valueOf(100), 4,
						RoundingMode.HALF_DOWN);

				if (StringUtils.equals(feeDetail.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
					Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(financeMain);
					BigDecimal gstPercentage = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);
					BigDecimal gstCalPercentage = gstPercentage.divide(BigDecimal.valueOf(100), 4,
							RoundingMode.HALF_DOWN);
					BigDecimal totFeePay = gstCalPercentage.multiply(feePercent);
					calcPerc = calcPerc.add(feePercent).add(totFeePay);
				} else {
					calcPerc = calcPerc.add(feePercent);
				}

				// Fee Amount Calculation
				calculatedAmt = calculatedAmt.divide(calcPerc, 0, RoundingMode.HALF_DOWN);
			}
		}
		BigDecimal taxableAmt = calculatedAmt;
		if (StringUtils.equals(feeDetail.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {

		}

		calculatedAmt = calculatedAmt.multiply(newPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());

		feeDetail.setActualAmount(calculatedAmt);
		paidAmount = summary.getPaidAmount();
		if (!StringUtils.isEmpty(detail.getTaxType())) {
			TaxAmountSplit taxSplit = GSTCalculator.calculateGST(receiptData.getFinanceDetail(), detail.getTaxType(),
					calculatedAmt, BigDecimal.ZERO);
			// fee percentage add two times
			if (StringUtils.equals(detail.getTaxType(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
				calculatedAmt = calculatedAmt.add(taxSplit.gettGST());
			}
			gstAmount = gstAmount.add(taxSplit.gettGST());
		}
		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (paidAmount.compareTo(calculatedAmt) > 0) {
				paidAmount = calculatedAmt;
			}
		}
		summary.setTotalDue(calculatedAmt);
		summary.setTotRecv(calculatedAmt);
		summary.setDueGST(gstAmount);
		detail.setDueGST(gstAmount);
		detail.setTotRecv(calculatedAmt);
		detail.setTotalDue(calculatedAmt);
		if (detail.isTdsReq()) {
			BigDecimal taxableAmount = BigDecimal.ZERO;
			if (StringUtils.isNotEmpty(feeDetail.getTaxComponent())) {
				taxableAmount = detail.getTotRecv().subtract(detail.getDueGST());
			} else {
				taxableAmount = detail.getTotRecv();
			}

			BigDecimal tdsAmount = receiptCalculator
					.getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), taxableAmount);
			detail.setTdsDue(tdsAmount);
			detail.setTotalDue(calculatedAmt.subtract(tdsAmount));
			summary.setTotalDue(calculatedAmt.subtract(tdsAmount));
		}
		detail.setPaidAmount(paidAmount);

		if (detail.isTdsReq()) {
			if (detail.getTotalDue().compareTo(paidAmount) == 0) {
				detail.setTdsPaid(detail.getTdsDue());
			} else {
				BigDecimal amount = receiptCalculator.getPaidAmount(detail, detail.getPaidAmount());
				BigDecimal tdsPaidNow = receiptCalculator
						.getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), amount);
				detail.setTdsPaid(detail.getTdsPaid().add(tdsPaidNow));

			}
		}
		detail.setTotalPaid(detail.getPaidAmount().add(detail.getTdsPaid()));
		doFillDetails(feeDetail);
	}

	public String getButtonId() {
		return buttonId;
	}

	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setCrossLoanKnockOffDialogCtrl(CrossLoanKnockOffDialogCtrl crossLoanKnockOffDialogCtrl) {
		this.crossLoanKnockOffDialogCtrl = crossLoanKnockOffDialogCtrl;
	}

}
