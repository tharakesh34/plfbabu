package com.pennant.webui.financemanagement.paymentMode;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class BounceDetailsDialogCtrl extends GFCBaseCtrl<ReceiptAllocationDetail> {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -1289603949144743239L;
	private static final Logger logger = LogManager.getLogger(BounceDetailsDialogCtrl.class);

	protected Window window_BounceDetails;
	protected Listbox listBounceDetails;

	protected Listheader listheader_Code;
	protected Listbox listDetails;

	private List<ReceiptAllocationDetail> allocList;
	private String buttonId;

	@SuppressWarnings("unchecked")
	public void onCreate$window_BounceDetails(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BounceDetails);
		try {

			if (arguments.containsKey("details")) {
				this.allocList = (List<ReceiptAllocationDetail>) arguments.get("details");
			}
			if (arguments.containsKey("buttonId")) {
				this.buttonId = (String) arguments.get("buttonId");
				setButtonId(this.buttonId);
			} else {
				setButtonId("");
			}

			doSetFieldProperties();
			this.window_BounceDetails.setHeight(borderLayoutHeight - 15 + "px");
			this.window_BounceDetails.setWidth("95%");
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BounceDetails.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	public void doSetFieldProperties() {
		logger.debug("Entering");

		setDialog(DialogType.EMBEDDED);
		doFillDetails(allocList);

		logger.debug("Leaving");
	}

	public void doFillDetails(List<ReceiptAllocationDetail> details) {

		logger.debug("Entering");
		this.listDetails.getItems().clear();
		Listitem item = null;
		Listcell lc = null;
		BigDecimal rcdAmount = BigDecimal.ZERO;
		BigDecimal dueGST = BigDecimal.ZERO;
		BigDecimal inProgressAmt = BigDecimal.ZERO;
		BigDecimal curDueAmt = BigDecimal.ZERO;
		BigDecimal tdsDueAmt = BigDecimal.ZERO;
		BigDecimal curPaidAmt = BigDecimal.ZERO;
		BigDecimal netPaidAmt = BigDecimal.ZERO;
		BigDecimal curPaidGstAmt = BigDecimal.ZERO;
		BigDecimal curPaidTdsAmt = BigDecimal.ZERO;
		BigDecimal waivedAmt = BigDecimal.ZERO;
		BigDecimal balDueAmount = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocate : details) {

			item = new Listitem();

			lc = new Listcell(allocate.getTypeDesc());
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getTotRecv(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			rcdAmount = rcdAmount.add(allocate.getTotRecv());

			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getInProcess(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			inProgressAmt = inProgressAmt.add(allocate.getInProcess());

			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getDueGST(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			dueGST = dueGST.add(allocate.getDueGST());

			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getTdsDue(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			tdsDueAmt = tdsDueAmt.add(allocate.getTdsDue());

			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getTotalDue(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			curDueAmt = curDueAmt.add(allocate.getTotalDue());

			lc = new Listcell();
			CurrencyBox allocationPaid = new CurrencyBox();
			allocationPaid.setStyle("text-align:right;");
			allocationPaid.setBalUnvisible(true, true);
			setProps(allocationPaid, false, 2, 120);
			allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getTotalPaid(), 2));
			allocationPaid.setReadonly(true);

			lc.appendChild(allocationPaid);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			curPaidAmt = curPaidAmt.add(allocate.getTotalPaid());

			lc = new Listcell();
			CurrencyBox allocationNetPaid = new CurrencyBox();
			allocationNetPaid.setStyle("text-align:right;");
			allocationNetPaid.setBalUnvisible(true, true);
			setProps(allocationNetPaid, false, 2, 120);
			allocationNetPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getPaidAmount(), 2));
			allocationNetPaid.setReadonly(true);

			lc.appendChild(allocationNetPaid);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			netPaidAmt = netPaidAmt.add(allocate.getPaidAmount());

			// Paid GST
			lc = new Listcell();
			CurrencyBox allocationPaidGst = new CurrencyBox();
			allocationPaidGst.setStyle("text-align:right;");
			allocationPaidGst.setBalUnvisible(true, true);
			setProps(allocationPaidGst, false, 2, 120);
			allocationPaidGst.setValue(PennantApplicationUtil.formateAmount(allocate.getPaidGST(), 2));
			allocationPaidGst.setReadonly(true);

			lc.appendChild(allocationPaidGst);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			curPaidGstAmt = curPaidGstAmt.add(allocate.getPaidGST());

			// Paid TDS
			lc = new Listcell();
			CurrencyBox allocationPaidTDS = new CurrencyBox();
			allocationPaidTDS.setStyle("text-align:right;");
			allocationPaidTDS.setBalUnvisible(true, true);
			setProps(allocationPaidTDS, false, 2, 120);
			allocationPaidTDS.setValue(PennantApplicationUtil.formateAmount(allocate.getTdsPaid(), 2));
			allocationPaidTDS.setReadonly(true);

			lc.appendChild(allocationPaidTDS);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			curPaidTdsAmt = curPaidTdsAmt.add(allocate.getTdsPaid());

			// waived Amount
			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getWaivedAmount(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			waivedAmt = waivedAmt.add(allocate.getWaivedAmount());

			// BalanceDueAmount
			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getBalance(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			balDueAmount = balDueAmount.add(allocate.getBalance());

			this.listDetails.appendChild(item);
		}
		addFooter(rcdAmount, inProgressAmt, curDueAmt, curPaidAmt, waivedAmt, dueGST, curPaidGstAmt, balDueAmount,
				tdsDueAmt, curPaidTdsAmt, netPaidAmt, 2);

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding footer to show Totals
	 * 
	 * @param dueAmount
	 * @param paidAmount
	 * @param waivedAmount
	 * @param formatter
	 * @param isPastDue
	 */
	private void addFooter(BigDecimal rcdAmount, BigDecimal inProgressAmt, BigDecimal curDueAmt, BigDecimal curPaidAmt,
			BigDecimal waivedAmt, BigDecimal dueGST, BigDecimal paidGST, BigDecimal balDueAmount, BigDecimal tdsDue,
			BigDecimal tdsPaid, BigDecimal netPaid, int formatter) {

		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		lc = new Listcell();
		Label label = new Label(CurrencyUtil.format(rcdAmount, formatter));
		label.setId("AllocateAmount_rcdAmount");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(inProgressAmt, formatter));
		label.setId("AllocateAmount_inProgressAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(dueGST, formatter));
		label.setId("AllocateAmount_dueGST");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(tdsDue, formatter));
		label.setId("AllocateAmount_dueTDS");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(curDueAmt, formatter));
		label.setId("AllocateAmount_curDueAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(curPaidAmt, formatter));
		label.setId("AllocateAmount_curPaidAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(netPaid, formatter));
		label.setId("AllocateAmount_NetPaidAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(paidGST, formatter));
		label.setId("AllocateAmount_curPaidGSTAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(tdsPaid, formatter));
		label.setId("AllocateAmount_curPaidTDSAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(waivedAmt, formatter));
		label.setId("AllocateAmount_remainingAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(CurrencyUtil.format(balDueAmount, formatter));
		label.setId("AllocateAmount_balDueAmount");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		this.listDetails.appendChild(item);

	}

	public void doFillFeeDetails(List<FinFeeDetail> details) {

		logger.debug("Entering");

		this.listDetails.getItems().clear();

		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		doClose(this.btnSave.isVisible());
		logger.debug("Leaving" + event.toString());

	}

	public String getButtonId() {
		return buttonId;
	}

	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}

}
