package com.pennant.webui.financemanagement.insurance;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.webui.financemanagement.insurance.model.InsPaymentUploadListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InsPaymentUploadListCtrl extends GFCBaseListCtrl<InsurancePaymentInstructions> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(InsPaymentUploadListCtrl.class);

	protected Window window_InsPaymentUploadList;
	protected Borderlayout borderLayout_InsPaymentUploadList;
	protected Paging pagingInsPaymentUploadList;
	protected Listbox listBoxInsPaymentUpload;

	protected Listheader listheader_EntityCode;
	protected Listheader listheader_PaymentType;

	protected Button button_InsPaymentUploadList_NewInsPaymentUpload;
	protected Button button_InsPaymentUploadList_InsPaymentUploadSearch;

	// Search Fields
	protected ExtendedCombobox entityCode;
	protected Textbox paymentType;

	protected Listbox sortOperator_EntityCode;
	protected Listbox sortOperator_PaymentType;

	/**
	 * default constructor.<br>
	 */
	public InsPaymentUploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InsurancePaymentInstructions";
		super.pageRightName = "InsurancePaymentInstructions";
		super.tableName = "InsurancePaymentInstructions";
		super.queueTableName = "InsurancePaymentInstructions";
		super.enquiryTableName = "InsurancePaymentInstructions";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_InsPaymentUploadList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_InsPaymentUploadList, borderLayout_InsPaymentUploadList, listBoxInsPaymentUpload,
				pagingInsPaymentUploadList);
		setItemRender(new InsPaymentUploadListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InsPaymentUploadList_NewInsPaymentUpload);
		registerButton(button_InsPaymentUploadList_InsPaymentUploadSearch);
		this.button_InsPaymentUploadList_NewInsPaymentUpload.setVisible(true);

		registerField("id");
		registerField("entityCode", listheader_EntityCode, SortOrder.NONE, entityCode, sortOperator_EntityCode,
				Operators.STRING);
		registerField("paymentType", listheader_PaymentType, SortOrder.NONE, paymentType, sortOperator_PaymentType,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		doSetFieldProperties();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.listheader_RecordStatus.setVisible(true);
		this.listheader_RecordType.setVisible(true);

		this.entityCode.setModuleName("Entity");
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_InsPaymentUploadList_InsPaymentUploadSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param entityCodeName
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
	public void onClick$button_InsPaymentUploadList_NewInsPaymentUpload(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		InsurancePaymentInstructions insurancePaymentHeader = new InsurancePaymentInstructions();
		insurancePaymentHeader.setNewRecord(true);
		insurancePaymentHeader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(insurancePaymentHeader);
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(InsurancePaymentInstructions instructions) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("insurancePaymentInstructions", instructions);
		arg.put("insurancePaymentUploadListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Insurance/InsPaymentUploadDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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

}
