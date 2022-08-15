package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.tandetails.TanAssignmentService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class TdsCertificateEnquiryDialogCtrl extends GFCBaseCtrl<TdsReceivablesTxn> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(TdsCertificateEnquiryDialogCtrl.class);

	protected Window window_TdsCertificateEnquiryDialog; // autoWired
	protected Listbox listBoxTdsCertificateEnquiry; // autoWired
	protected Listbox listBoxTdsCertificateMiscellaneousEnquiry; // autoWired
	protected Listbox listBoxTanDetails;
	protected Borderlayout borderlayoutTdsCertificateEnquiry; // autoWired
	private Tabpanel tabPanel_dialogWindow;
	private Tabpanel tabPanel_dialogWindowTanDetails;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	protected List<TdsReceivablesTxn> tdsReceivablesTxnList;
	private TanAssignmentService tanAssignmentService;
	protected Tab tdsCertificateTab;
	protected Tab tdsMiscellaneousTab;
	protected Tab viewTanNoTab;
	private int ccyformat = 0;
	BigDecimal balanceAmount = BigDecimal.ZERO;

	/**
	 * default constructor.<br>
	 */
	public TdsCertificateEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_TdsCertificateEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		try {

			// Set the page level components.
			setPageComponents(window_TdsCertificateEnquiryDialog);

			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
				tabPanel_dialogWindowTanDetails = (Tabpanel) event.getTarget().getParent().getParent();
			}

			if (arguments.containsKey("list")) {
				this.tdsReceivablesTxnList = (List<TdsReceivablesTxn>) arguments.get("list");
			} else {
				this.tdsReceivablesTxnList = null;
			}

			if (arguments.containsKey("ccyformat")) {
				this.ccyformat = (Integer) arguments.get("ccyformat");
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
						.get("financeEnquiryHeaderDialogCtrl");
			}

			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	private void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			doFillTdsCertificateDetails(this.tdsReceivablesTxnList);

			int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
			this.listBoxTdsCertificateEnquiry.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
			this.listBoxTdsCertificateMiscellaneousEnquiry.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
			this.window_TdsCertificateEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
			getBorderLayoutHeight();

			if (tabPanel_dialogWindow != null) {
				tabPanel_dialogWindow.appendChild(this.window_TdsCertificateEnquiryDialog);
			}

			if (tabPanel_dialogWindowTanDetails != null) {
				tabPanel_dialogWindowTanDetails.appendChild(this.window_TdsCertificateEnquiryDialog);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$viewTanNoTab() {
		logger.debug(Literal.ENTERING);
		String finReference = (String) arguments.get("finReference");
		List<TanAssignment> tanAssignment = tanAssignmentService.getTanDetailsByReference(finReference);

		doFillTanDetails(tanAssignment);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill the Finance Document Details List
	 * 
	 * @param docDetails
	 */

	public void doFillTdsCertificateDetails(List<TdsReceivablesTxn> tdsReceivablesTxnList) {
		logger.debug(Literal.ENTERING);

		this.listBoxTdsCertificateEnquiry.getItems().clear();
		this.listBoxTdsCertificateMiscellaneousEnquiry.getItems().clear();
		Listitem item;
		if (CollectionUtils.isNotEmpty(tdsReceivablesTxnList)) {
			for (TdsReceivablesTxn tdsReceivablesTxn : tdsReceivablesTxnList) {
				if ("R".equals(tdsReceivablesTxn.getModule())) {
					item = new Listitem();
					Listcell lc;
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getTxnID()));
					lc.setStyle("text-align:left;");
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(tdsReceivablesTxn.getTranDate()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(tdsReceivablesTxn.getReceiptDate()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getReceiptPurpose()));
					lc.setStyle("text-align:left;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getReceiptAmount(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getTdsReceivable(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					// TDS Adjusted
					lc = new Listcell(PennantApplicationUtil.amountFormate(
							tdsReceivablesTxn.getTdsAdjusted().add(tdsReceivablesTxn.getAdjustmentAmount()),
							ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					// TDS Balance Amount
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getBalanceAmount(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getCertificateNumber()));
					lc.setStyle("text-align:left;");
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(tdsReceivablesTxn.getCertificateDate()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getCertificateAmount(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getCertificateBalance(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					if (tdsReceivablesTxn.getStatus() == null) {
						lc = new Listcell(Labels.getLabel("label_Approved"));
					} else {
						switch (tdsReceivablesTxn.getStatus()) {
						case "AC":
							lc = new Listcell(Labels.getLabel("label_AC"));
							break;
						case "RC":
							lc = new Listcell(Labels.getLabel("label_RC"));
							break;
						case "CC":
							lc = new Listcell(Labels.getLabel("label_CC"));
							break;
						}
					}
					lc.setStyle("text-align:left;");
					lc.setParent(item);

					this.listBoxTdsCertificateEnquiry.appendChild(item);
				} else {
					item = new Listitem();
					Listcell lc;
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getTxnID()));
					lc.setStyle("text-align:left;");
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(tdsReceivablesTxn.getTranDate()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(tdsReceivablesTxn.getReceiptDate()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getReceiptID()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getTdsReceivable(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					// TDS Adjusted
					lc = new Listcell(PennantApplicationUtil.amountFormate(
							tdsReceivablesTxn.getTdsAdjusted().add(tdsReceivablesTxn.getAdjustmentAmount()),
							ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					// TDS Balance Amount
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getBalanceAmount(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getCertificateNumber()));
					lc.setStyle("text-align:left;");
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(tdsReceivablesTxn.getCertificateDate()));
					lc.setStyle("text-align:center;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getCertificateAmount(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getCertificateBalance(), ccyformat));
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					if (tdsReceivablesTxn.getStatus() == null) {
						lc = new Listcell(Labels.getLabel("label_Approved"));
					} else {
						switch (tdsReceivablesTxn.getStatus()) {
						case "AC":
							lc = new Listcell(Labels.getLabel("label_AC"));
							break;
						case "RC":
							lc = new Listcell(Labels.getLabel("label_RC"));
							break;
						case "CC":
							lc = new Listcell(Labels.getLabel("label_CC"));
							break;
						}
					}
					lc.setStyle("text-align:left;");
					lc.setParent(item);

					this.listBoxTdsCertificateMiscellaneousEnquiry.appendChild(item);
				}
			}
			logger.debug(Literal.LEAVING);
		}
	}

	public void doFillTanDetails(List<TanAssignment> tanAssignmentList) {
		logger.debug(Literal.ENTERING);

		this.listBoxTanDetails.getItems().clear();

		if (CollectionUtils.isNotEmpty(tanAssignmentList)) {
			for (TanAssignment tanAssignment : tanAssignmentList) {
				Listitem item = new Listitem();
				Listcell lc;
				TanDetail tanDetail = tanAssignment.getTanDetail();
				lc = new Listcell(String.valueOf(tanDetail.getTanNumber()));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(tanDetail.getFinReference()));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(tanDetail.getTanHolderName()));
				lc.setParent(item);
				this.listBoxTanDetails.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void setTanAssignmentService(TanAssignmentService tanAssignmentService) {
		this.tanAssignmentService = tanAssignmentService;
	}

}