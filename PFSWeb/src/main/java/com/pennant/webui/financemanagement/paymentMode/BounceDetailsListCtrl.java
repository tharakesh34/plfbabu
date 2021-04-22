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
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class BounceDetailsListCtrl extends GFCBaseCtrl<ReceiptAllocationDetail> {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -1289603949144743239L;
	private static final Logger logger = LogManager.getLogger(BounceDetailsListCtrl.class);

	protected Window window_BounceDetails;
	protected Listbox listBounceDetails;

	protected Listheader listheader_Code;
	protected Listbox listDetails;

	private List<ReceiptAllocationDetail> allocList;
	private String buttonId;

	public void onCreate$window_BounceDetails(Event event) throws Exception {
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

			doFieldProperties();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BounceDetails.onClose();
		}
		this.window_BounceDetails.doModal();
		logger.debug("Leaving " + event.toString());
	}

	public void doFieldProperties() {
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
		BigDecimal inProgressAmt = BigDecimal.ZERO;
		BigDecimal curDueAmt = BigDecimal.ZERO;
		BigDecimal curPaidAmt = BigDecimal.ZERO;
		BigDecimal curWaiverAmt = BigDecimal.ZERO;
		BigDecimal remainingAmt = BigDecimal.ZERO;
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(allocate.getDueAmount(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			curDueAmt = curDueAmt.add(allocate.getDueAmount());

			lc = new Listcell();
			CurrencyBox allocationPaid = new CurrencyBox();
			allocationPaid.setStyle("text-align:right;");
			allocationPaid.setBalUnvisible(true, true);
			setProps(allocationPaid, false, 2, 120);
			allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getPaidAmount(), 2));
			allocationPaid.setReadonly(true);

			lc.appendChild(allocationPaid);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			curPaidAmt = curPaidAmt.add(allocate.getPaidAmount());

			// Amount Setting

			lc = new Listcell(PennantApplicationUtil
					.amountFormate(allocate.getDueAmount().subtract(allocate.getPaidAmount()), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			remainingAmt = remainingAmt.add(curDueAmt.subtract(curPaidAmt));

			this.listDetails.appendChild(item);
		}
		addFooter(rcdAmount, inProgressAmt, curDueAmt, curPaidAmt, curWaiverAmt, remainingAmt, 2);

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
			BigDecimal curWaiverAmt, BigDecimal remainingAmt, int formatter) {

		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		lc = new Listcell();
		Label label = new Label(PennantAppUtil.amountFormate(rcdAmount, formatter));
		label.setId("AllocateAmount_rcdAmount");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(inProgressAmt, formatter));
		label.setId("AllocateAmount_inProgressAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(curDueAmt, formatter));
		label.setId("AllocateAmount_curDueAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(curPaidAmt, formatter));
		label.setId("AllocateAmount_curPaidAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(curWaiverAmt, formatter));
		label.setId("AllocateAmount_curWaiverAmt");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(remainingAmt, formatter));
		label.setId("AllocateAmount_remainingAmt");
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
