package com.pennant.webui.financemanagement.insurance;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.webui.financemanagement.insurance.model.InsuranceReconciliationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InsuranceReconciliationListCtrl extends GFCBaseListCtrl<InsuranceDetails> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(InsuranceReconciliationListCtrl.class);

	protected Window window_InsuranceDetailsList;
	protected Borderlayout borderLayout_InsuranceDetailsList;
	protected Paging pagingInsuranceDetailsList;
	protected Listbox listBoxInsuranceDetails;

	protected Listheader listheader_Reference;
	protected Listheader listheader_PolicyNumber;

	protected Button button_InsuranceDetailsList_NewInsuranceDetails;
	protected Button button_InsuranceDetailsList_InsuranceDetailsSearch;

	protected Textbox refernce;
	protected Textbox policyNumber;

	protected Listbox sortOperator_Reference;
	protected Listbox sortOperator_PolicyNumber;

	private transient InsuranceDetailService insuranceDetailService;

	/**
	 * default constructor.<br>
	 */
	public InsuranceReconciliationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InsuranceDetails";
		super.pageRightName = "InsuranceDetailsList";
		super.tableName = "InsuranceDetails_View";
		super.queueTableName = "InsuranceDetails_View";
		super.enquiryTableName = "InsuranceDetails_View";
	}

	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilterEqual("ReconStatus", InsuranceConstants.RECON_STATUS_MANUAL);
		this.searchObject.addFilterEqual("IssuanceStatus", InsuranceConstants.ISSUED);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_InsuranceDetailsList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_InsuranceDetailsList, borderLayout_InsuranceDetailsList, listBoxInsuranceDetails,
				pagingInsuranceDetailsList);
		setItemRender(new InsuranceReconciliationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InsuranceDetailsList_InsuranceDetailsSearch);

		registerField("id");
		registerField("FinID");
		registerField("finReference");
		registerField("postingAgainst");
		registerField("reference", listheader_Reference, SortOrder.NONE, refernce, sortOperator_Reference,
				Operators.STRING);
		registerField("policyNumber", listheader_PolicyNumber, SortOrder.NONE, policyNumber, sortOperator_PolicyNumber,
				Operators.STRING);

		this.button_InsuranceDetailsList_NewInsuranceDetails.setVisible(false);
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_InsuranceDetailsList_InsuranceDetailsSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param entityCodeName An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_InsuranceDetailsList_NewInsuranceDetails(Event event) {
		logger.debug(Literal.ENTERING);

		InsuranceDetails insuranceDetails = new InsuranceDetails();
		insuranceDetails.setNewRecord(true);
		insuranceDetails.setWorkflowId(getWorkFlowId());
		doShowDialogPage(insuranceDetails);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onInsuranceDetailsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxInsuranceDetails.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		InsuranceDetails insuranceDetails = insuranceDetailService.getInsurenceDetailsById(id);

		if (insuranceDetails == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(insuranceDetails, whereCond.toString(), new Object[] { insuranceDetails.getId() })) {
			if (isWorkFlowEnabled() && insuranceDetails.getWorkflowId() == 0) {
				insuranceDetails.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(insuranceDetails);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(InsuranceDetails insuranceDetails) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("insuranceDetails", insuranceDetails);
		arg.put("insuranceDetailsListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Insurance/InsuranceReconciliationDialog.zul", null, arg);
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
	public void onClick$print(Event event) {
		doPrintResults();
	}

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

	public InsuranceDetailService getInsuranceDetailService() {
		return insuranceDetailService;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}

}