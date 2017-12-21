package com.pennant.webui.financemanagement.bulkratechange;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.webui.finance.enquiry.model.BulkRateChangeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

public class BulkRateChangeListCtrl extends GFCBaseListCtrl<BulkRateChangeHeader> {
	private static final long serialVersionUID = 9086034736503097868L;
	private static final Logger logger = Logger.getLogger(BulkRateChangeListCtrl.class);

	protected Window window_BulkRateChangeList;
	protected Borderlayout borderLayout_BulkRateChangeList;
	protected Paging pagingBulkRateChangeList;
	protected Listbox listBoxBulkRateChange;

	protected Listheader listheader_BulkRateReference;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FromDate;
	protected Listheader listheader_RateChange;
	protected Listheader listheader_reCaType;
	protected Listheader listheader_LastMntOn;

	protected Button button_BulkRateChangeList_NewBulkRateChange;
	protected Button button_BulkRateChangeList_BulkRateChangeSearchDialog;

	protected Listbox sortOperator_BulkRateReference;
	protected Listbox sortOperator_FinType;
	protected Listbox sortOperator_FromDate;
	protected Listbox sortOperator_RateChange;

	protected Textbox bulkRateReference;
	protected Textbox finType;
	protected Datebox fromDate_One;
	protected Datebox fromDate_Two;
	protected Label label_FromDate;
	protected int oldVar_sortOperator_FromDate = -1;
	protected Decimalbox rateChange;

	protected Label label_BulkRateChangeSearch_newProcessedRate;

	private FinanceDetailService financeDetailService;
	private BulkRateChangeProcessService bulkRateChangeProcessService;

	/**
	 * default constructor.<br>
	 */
	public BulkRateChangeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BulkRateChangeHeader";
		super.pageRightName = "BulkRateChngList";
		super.tableName = "BulkRateChangeHeader_AView";
		super.queueTableName = "BulkRateChangeHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BulkRateChangeList(Event event) {
		// Set the page level components.
		setPageComponents(window_BulkRateChangeList, borderLayout_BulkRateChangeList, listBoxBulkRateChange,
				pagingBulkRateChangeList);
		setItemRender(new BulkRateChangeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BulkRateChangeList_NewBulkRateChange, "button_BulkRateChangeList_NewBulkRateChange", true);
		registerButton(button_BulkRateChangeList_BulkRateChangeSearchDialog);

		registerField("BulkRateChangeRef", listheader_BulkRateReference, SortOrder.ASC, bulkRateReference,
				sortOperator_BulkRateReference, Operators.STRING);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		registerField("FromDate", listheader_FromDate, SortOrder.NONE, fromDate_One, sortOperator_FromDate,
				Operators.DATE);
		registerField("RateChange", listheader_RateChange, SortOrder.NONE, rateChange, sortOperator_RateChange,
				Operators.NUMERIC);
		registerField("LastMntOn", listheader_LastMntOn, SortOrder.NONE, rateChange, sortOperator_RateChange,
				Operators.NUMERIC);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_BulkRateChangeList_BulkRateChangeSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_BulkRateChangeList_NewBulkRateChange(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BulkRateChangeHeader aBulkRateChangeHeader = new BulkRateChangeHeader();
		aBulkRateChangeHeader.setNewRecord(true);
		aBulkRateChangeHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aBulkRateChangeHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBulkRateChangeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		final Listitem item = this.listBoxBulkRateChange.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			BulkRateChangeHeader aBulkRateChangeHeader = (BulkRateChangeHeader) item.getAttribute("data");
			BulkRateChangeHeader bulkRateChangeHeader =  bulkRateChangeProcessService.getApprovedBulkRateChangeHeaderByRef(aBulkRateChangeHeader.getBulkRateChangeRef());

			if (bulkRateChangeHeader == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND BulkRateChangeRef ='" + bulkRateChangeHeader.getBulkRateChangeRef() + "' AND version="
					+ bulkRateChangeHeader.getVersion() + " ";

			if (doCheckAuthority(bulkRateChangeHeader, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && bulkRateChangeHeader.getWorkflowId() == 0) {
					bulkRateChangeHeader.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(bulkRateChangeHeader);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBulkRateChangeHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BulkRateChangeHeader aBulkRateChangeHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("bulkRateChangeHeader", aBulkRateChangeHeader);
		arg.put("bulkRateChangeListCtrl", this);
		arg.put("moduleCode", super.moduleCode);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/BulkRateChange/BulkRateChangeDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setBulkRateChangeProcessService(BulkRateChangeProcessService bulkRateChangeProcessService) {
		this.bulkRateChangeProcessService = bulkRateChangeProcessService;
	}
}