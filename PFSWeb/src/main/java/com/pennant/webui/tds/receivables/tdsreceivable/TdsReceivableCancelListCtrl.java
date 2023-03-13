package com.pennant.webui.tds.receivables.tdsreceivable;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.service.tds.receivables.TdsReceivableService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.webui.tds.receivables.tdsreceivable.model.TdsReceivableCancelListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class TdsReceivableCancelListCtrl extends GFCBaseListCtrl<TdsReceivable> {
	private static final long serialVersionUID = 1L;

	protected Window window_TdsReceivableCancelList;
	protected Borderlayout borderLayout_TdsReceivableCancelList;
	protected Paging pagingTdsReceivableCancelList;
	protected Listbox listBoxTdsReceivableCancel;

	// List headers
	protected Listheader listheader_TanNumber;
	protected Listheader listheader_TanHolderName;
	protected Listheader listheader_CertificateNumber;
	protected Listheader listheader_CertificateDate;
	protected Listheader listheader_CertificateAmount;
	protected Listheader listheader_AdjustedAmount;
	protected Listheader listheader_BalanceAmount;

	// checkRights

	protected Button button_TdsReceivableCancelList_TdsReceivableSearch;

	// Search Fields
	protected Longbox tanID; // autowired
	protected Textbox tanNumber;// autowired
	protected Textbox certificateNumber; // autowired
	protected Datebox certificateDate;// autowired
	protected Decimalbox certificateAmount;// autowired
	protected Decimalbox utilizedAmount;// autowired
	protected Decimalbox balanceAmount;// autowired

	protected Listbox sortOperator_TANID;
	protected Listbox sortOperator_TANNumber;
	protected Listbox sortOperator_CertificateNumber;
	protected Listbox sortOperator_CertificateDate;
	protected Listbox sortOperator_CertificateQuarter;

	private transient TdsReceivableService tdsReceivableService;
	private transient TdsReceivablesTxnService tdsReceivablesTxnService;

	/**
	 * default constructor.<br>
	 */
	public TdsReceivableCancelListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CancelCertificate";
		super.pageRightName = "TdsReceivableCancelList";
		super.tableName = "TDS_RECEIVABLES";
		super.queueTableName = "TDS_RECEIVABLES_VIEW";
		super.enquiryTableName = "TDS_RECEIVABLES_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TdsReceivableCancelList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_TdsReceivableCancelList, borderLayout_TdsReceivableCancelList,
				listBoxTdsReceivableCancel, pagingTdsReceivableCancelList);
		setItemRender(new TdsReceivableCancelListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_TdsReceivableCancelList_TdsReceivableSearch);
		registerField("id");
		registerField("tANID");
		registerField("tanNumber", listheader_TanNumber, SortOrder.NONE, tanNumber, sortOperator_TANNumber,
				Operators.STRING);
		registerField("tanHolderName", listheader_TanHolderName);
		registerField("certificateNumber", listheader_CertificateNumber, SortOrder.NONE, certificateNumber,
				sortOperator_CertificateNumber, Operators.STRING);
		registerField("certificateDate", listheader_CertificateDate, SortOrder.NONE, certificateDate,
				sortOperator_CertificateDate, Operators.DATE);
		registerField("certificateAmount", listheader_CertificateAmount);
		registerField("utilizedAmount", listheader_AdjustedAmount);
		registerField("balanceAmount", listheader_BalanceAmount);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TdsReceivableCancelList_TdsReceivableSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onTdsReceivableCancelItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxTdsReceivableCancel.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");

		TdsReceivable tdsReceivable = tdsReceivableService.getTdsReceivable(id, TableType.VIEW);

		if (tdsReceivable == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		tdsReceivable.setTdsReceivablesTxnList(tdsReceivablesTxnService.getTdsReceivablesTxnByReceivableId(id));

		String whereCond = " where id=?";

		if (doCheckAuthority(tdsReceivable, whereCond, new Object[] { tdsReceivable.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && tdsReceivable.getWorkflowId() == 0) {
				tdsReceivable.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(tdsReceivable);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param tdsreceivable The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TdsReceivable tdsReceivable) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("tdsReceivable", tdsReceivable);
		arg.put("tdsReceivableCancelListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivableCancel/TdsReceivableCancelDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CancelTDSReceivableList_PrintList(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setTdsReceivableService(TdsReceivableService tdsReceivableService) {
		this.tdsReceivableService = tdsReceivableService;
	}

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

}
