package com.pennant.webui.financemanagement.paymentMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.webui.financemanagement.receipts.LoanClosureEnquiryDialogCtrl;
import com.pennant.webui.financemanagement.receipts.ReceiptDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class EventFeeDetailsDialogCtrl extends GFCBaseCtrl<ReceiptAllocationDetail> {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -1289603949144743239L;
	private static final Logger logger = Logger.getLogger(EventFeeDetailsDialogCtrl.class);

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

	public void onCreate$window_EventFeeDetails(Event event) throws Exception {
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
		if (!isLoanClosure){
			allocationPaid.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_excessAdjustTo"));
		}
		allocationPaid.setValue(feeDetail.getActPercentage().toString());
		allocationPaid.addForward("onFulfill", this.window_EventFeeDetails, "onAllocatePaidChange", 1);
		lc.appendChild(allocationPaid);
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(feeDetail.getCalculatedAmount(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		//While Click on Fee Details button at the first time Amount calculation wrong for New Fee Percent fixed.
		FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		BigDecimal calculatedAmt = feeDetail.getCalculatedOn();
		calculatedAmt = calculatedAmt.multiply(feeDetail.getActPercentage()).divide(BigDecimal.valueOf(100), 2,
				RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());
		feeDetail.setActualAmount(calculatedAmt);

		lc = new Listcell(PennantApplicationUtil.amountFormate(feeDetail.getActualAmount(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		this.listDetails.appendChild(item);

		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		receiptCalculator.setTotals(receiptData, 0);
		
		for (ReceiptAllocationDetail alloc : receiptData.getReceiptHeader().getAllocations()) {
			if (!receiptData.isForeClosure()) {
				if (alloc.getAllocationType().equals(RepayConstants.ALLOCATION_FEE)){
					alloc.setPaidAmount(alloc.getDueAmount());
					alloc.setPaidGST(alloc.getDueGST());
					alloc.setTotalPaid(alloc.getDueAmount());
				}
			}
		}
		//receiptCalculator.recalAutoAllocation(receiptData, receiptData.getReceiptHeader().getValueDate(), false);
		if (isLoanClosure) {
			loanClosureEnquiryDialogCtrl.doFillAllocationDetail();
		} else {
			receiptDialogCtrl.doFillAllocationDetail();
		}
		doClose(this.btnSave.isVisible());
		logger.debug("Leaving" + event.toString());

	}

	public void onAllocatePaidChange(ForwardEvent event) throws Exception {
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
			if (feeDetail.getPercentage().compareTo(BigDecimal.ZERO) > 0 && calculatedAmt.compareTo(BigDecimal.ZERO) > 0) {
				
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(financeMain.getFinReference());
				
				BigDecimal feePercent = feeDetail.getPercentage().divide(BigDecimal.valueOf(100), 4,
						RoundingMode.HALF_DOWN);
				BigDecimal gstPercentage = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);
				BigDecimal gstCalPercentage = gstPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN);
				BigDecimal totFeePay = gstCalPercentage.multiply(feePercent);
				calcPerc = calcPerc.add(feePercent).add(totFeePay);

				// Fee Amount Calculation
				calculatedAmt = calculatedAmt.divide(calcPerc, 0, RoundingMode.HALF_DOWN);
			}
		}
		
		calculatedAmt = calculatedAmt.multiply(newPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());
		
		feeDetail.setActualAmount(calculatedAmt);
		paidAmount = summary.getPaidAmount();
		if (!StringUtils.isEmpty(detail.getTaxType())) {
			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(calculatedAmt);
			taxSplit.setTaxType(detail.getTaxType());
			taxSplit = receiptCalculator.getGST(receiptData.getFinanceDetail(), taxSplit);
			//fee percentage add two times
			calculatedAmt = calculatedAmt.add(taxSplit.gettGST());
			gstAmount = gstAmount.add(taxSplit.gettGST());
			paidAmount = calculatedAmt;
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
		summary.setPaidAmount(paidAmount);
		summary.setTotalPaid(paidAmount);
		detail.setTotalPaid(paidAmount);
		detail.setPaidAmount(paidAmount);

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

}
