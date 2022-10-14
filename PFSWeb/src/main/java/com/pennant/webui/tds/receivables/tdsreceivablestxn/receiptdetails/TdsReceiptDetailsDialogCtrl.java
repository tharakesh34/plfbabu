package com.pennant.webui.tds.receivables.tdsreceivablestxn.receiptdetails;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

public class TdsReceiptDetailsDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(TdsReceiptDetailsDialogCtrl.class);

	protected Window window_TDSReceiptDetailDialog;
	protected Listbox listBoxTDSReceiptDetail;
	protected Groupbox gb_Receivalble;
	protected long receiptId;
	private int formatter = 0;
	private transient ReceiptService receiptService;

	/**
	 * default constructor.<br>
	 */
	public TdsReceiptDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_TDSReceiptDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TDSReceiptDetailDialog);

		try {
			if (arguments.containsKey("receiptHeaderid")) {
				receiptId = Long.parseLong(String.valueOf(arguments.get("receiptHeaderid")));
			}
			formatter = CurrencyUtil.getFormat("INR");
			doFillAllocationDetail();
			this.window_TDSReceiptDetailDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TDSReceiptDetailDialog.onClose();
		}

		// doShowDialog();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doFillAllocationDetail() {
		logger.debug(Literal.ENTERING);
		FinReceiptHeader header = receiptService.getFinReceiptHeaderById(receiptId, false, "_View");
		List<ReceiptAllocationDetail> allocations = header.getAllocations();
		this.listBoxTDSReceiptDetail.getItems().clear();
		if (allocations.isEmpty()) {
			this.gb_Receivalble.setVisible(false);
			return;
		}

		// Get Receipt Purpose to Make Waiver amount Editable
		doClearMessage();
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal tdsDue = BigDecimal.ZERO;
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal dueAmount = BigDecimal.ZERO;
		BigDecimal tdsPaid = BigDecimal.ZERO;

		for (int i = 0; i < allocations.size(); i++) {
			ReceiptAllocationDetail rad = allocations.get(i);
			if (StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_PP)) {
				continue;
			}
			if (StringUtils.equals(rad.getAllocationType(), Allocation.PFT)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_TDS)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_NPFT)
					|| StringUtils.equals(rad.getAllocationType(), Allocation.PRI)) {
				paidAmount = BigDecimal.ZERO;
			} else {
				paidAmount = rad.getPaidAmount();
				dueAmount = dueAmount.add(rad.getTotalDue());
			}
			sum = sum.add(paidAmount);
			Listitem item = new Listitem();
			String allocDesc = Labels.getLabel("label_RecceiptDialog_AllocationType_" + rad.getAllocationType());
			if (StringUtils.equals(rad.getAllocationType(), Allocation.MANADV)) {
				allocDesc = rad.getTypeDesc();
			}
			if (StringUtils.equals(rad.getAllocationType(), Allocation.FEE)) {
				Filter[] masterCodeFiler = new Filter[1];
				masterCodeFiler[0] = new Filter("FeeTypeId", -rad.getAllocationTo(), Filter.OP_EQUAL);
				allocDesc = PennantApplicationUtil.getDBDescription("FeeType", "FeeTypes", "FeeTypeDesc",
						masterCodeFiler);
			}
			tdsDue = tdsDue.add(rad.getTdsDue());
			tdsPaid = tdsPaid.add(rad.getTdsPaid());
			addBoldTextCell(item, allocDesc, rad.isSubListAvailable(), i);
			addAmountCell(item, rad.getTdsDue(), ("AllocateTdsDue_" + i), false);
			addAmountCell(item, rad.getTotalDue(), ("AllocateDue_" + i), false);
			addAmountCell(item, rad.getTdsPaid(), ("AllocateTdsPaid_" + i), false);
			if (rad.getAllocationType().equalsIgnoreCase(RepayConstants.ALLOCATION_TDS)
					|| rad.getAllocationType().equalsIgnoreCase(RepayConstants.ALLOCATION_NPFT)) {
				addAmountCell(item, rad.getTotalDue(), ("AllocatePaid_" + i), false);
			} else {
				addAmountCell(item, rad.getPaidAmount(), ("AllocatePaid_" + i), false);
			}
			this.listBoxTDSReceiptDetail.appendChild(item);
		}
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		addAmountCell(item, tdsDue, null, true);
		addAmountCell(item, dueAmount, null, true);
		addAmountCell(item, tdsPaid, null, true);
		addAmountCell(item, sum, null, true);
		this.listBoxTDSReceiptDetail.appendChild(item);

		BigDecimal receiptAmount = BigDecimal.ZERO;

		for (FinReceiptDetail recDtl : header.getReceiptDetails()) {
			receiptAmount = receiptAmount.add(recDtl.getAmount());
		}

		// addExcessAmt(receiptAmount.subtract(sum));

		logger.debug(Literal.LEAVING);
	}

	public void addAmountCell(Listitem item, BigDecimal value, String cellID, boolean isBold) {
		Listcell lc = new Listcell(PennantApplicationUtil.amountFormate(value, formatter));

		if (isBold) {
			lc.setStyle("text-align:right;font-weight:bold;");
		} else {
			lc.setStyle("text-align:right;");
		}

		if (!StringUtils.isBlank(cellID)) {
			lc.setId(cellID);
		}

		lc.setParent(item);
	}

	public void addBoldTextCell(Listitem item, String value, boolean hasChild, int buttonId) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		if (hasChild) {
			Button button = new Button("Details");
			button.setId(String.valueOf(buttonId));
			button.addForward("onClick", listBoxTDSReceiptDetail, "onDetailsClick", button.getId());
			lc.appendChild(button);
		}
		lc.setParent(item);
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}
