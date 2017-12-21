package com.pennant.webui.applicationmaster.returnedcheque;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.service.applicationmaster.ReturnedChequeService;
import com.pennant.webui.applicationmaster.returnedcheque.model.ReturnedChequeListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

public class ReturnedChequeListCtrl extends GFCBaseListCtrl<ReturnedChequeDetails> {
	private static final long serialVersionUID = 485796535935527728L;
	private static final Logger logger = Logger.getLogger(ReturnedChequeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReturnedChequeList;
	protected Paging pagingReturnedChequeList;
	protected Listbox listBoxReturnedCheque;
	protected Borderlayout borderLayout_ReturnedChequeList;

	// List headers
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_ChequeNo;
	protected Listheader listheader_Amount;

	// Buttons
	protected Button button_ReturnedChequeList_NewReturnedCheque;
	protected Button button_ReturnedChequeList_ReturnedChequeSearchDialog;

	protected Textbox custCIF;
	protected Textbox chequeNo;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_chequeNO;

	private transient ReturnedChequeService returnedChequeService;

	/**
	 * default constructor.<br>
	 */
	public ReturnedChequeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReturnedChequeDetails";
		super.pageRightName = "ReturnedChequeList";
		super.tableName = "ReturnedCheques_View";
		super.queueTableName = "ReturnedCheques_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReturnedChequeList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReturnedChequeList, borderLayout_ReturnedChequeList, listBoxReturnedCheque,
				pagingReturnedChequeList);
		setItemRender(new ReturnedChequeListModelItemRender());

		// Register buttons and fields.
		registerButton(button_ReturnedChequeList_NewReturnedCheque, "button_ReturnedChequeList_NewReturnedChequeList",
				true);
		registerButton(button_ReturnedChequeList_ReturnedChequeSearchDialog);

		registerField("custCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF, Operators.STRING);
		registerField("chequeNo", listheader_ChequeNo, SortOrder.NONE, chequeNo, sortOperator_chequeNO,
				Operators.STRING);
		registerField("amount", listheader_Amount, SortOrder.NONE);

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
	public void onClick$button_ReturnedChequeList_ReturnedChequeSearchDialog(Event event) {
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
	public void onClick$button_ReturnedChequeList_NewReturnedCheque(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ReturnedChequeDetails returnedCheque = new ReturnedChequeDetails();
		returnedCheque.setNewRecord(true);
		returnedCheque.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(returnedCheque);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onReturnedChequeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReturnedCheque.getSelectedItem();

		// Get the selected entity.
		String custCIF = (String) selectedItem.getAttribute("custCIF");
		String chequeNo = (String) selectedItem.getAttribute("chequeNo");
		ReturnedChequeDetails returnedCheque = returnedChequeService.getReturnedChequesById(custCIF, chequeNo);

		if (returnedCheque == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustCIF='" + returnedCheque.getCustCIF() + "'AND ChequeNo='"
				+ returnedCheque.getChequeNo() + "' AND version=" + returnedCheque.getVersion() + " ";

		if (doCheckAuthority(returnedCheque, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && returnedCheque.getWorkflowId() == 0) {
				returnedCheque.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(returnedCheque);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param returnedCheque
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReturnedChequeDetails returnedCheque) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("returnedCheque", returnedCheque);
		arg.put("returnedChequeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ReturnedCheques/ReturnedChequeDialog.zul",
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

	public void setReturnedChequeService(ReturnedChequeService returnedChequeService) {
		this.returnedChequeService = returnedChequeService;
	}
}
