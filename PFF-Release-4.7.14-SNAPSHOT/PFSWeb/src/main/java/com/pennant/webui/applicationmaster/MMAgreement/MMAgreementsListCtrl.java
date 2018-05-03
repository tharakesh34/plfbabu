package com.pennant.webui.applicationmaster.MMAgreement;

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

import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.service.applicationmaster.MMAgreementService;
import com.pennant.webui.applicationmaster.MMAgreement.model.MMAgreementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/MMAgreements/MMAgreementsList.zul file.
 */
public class MMAgreementsListCtrl extends GFCBaseListCtrl<MMAgreement> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MMAgreement.class);

	protected Window window_MMAgreementsList;
	protected Borderlayout borderLayout_MMAgreementsList;
	protected Paging pagingMMAgreementsList;
	protected Listbox listBoxMMAgreements;

	protected Textbox custCIF;
	protected Listbox sortOperator_CustCIF;
	protected Textbox mMAReferennce;
	protected Listbox sortOperator_MMAReferennce;

	protected Listheader listheader_MMAReference;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_ContractAmt;
	protected Listheader listheader_ContractDate;
	protected Listheader listheader_Rate;

	protected Button button_MMAgreementsList_NewMMAgreements;
	protected Button button_MMAgreementsList_MMAgreementsSearch;

	private transient MMAgreementService mMAgreementService;

	/**
	 * default constructor.<br>
	 */
	public MMAgreementsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MMAgreement";
		super.pageRightName = "MMAgreementsList";
		super.tableName = "MMAgreements_View";
		super.queueTableName = "MMAgreements_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_MMAgreementsList(Event event) {
		// Set the page level components.
		setPageComponents(window_MMAgreementsList, borderLayout_MMAgreementsList, listBoxMMAgreements,
				pagingMMAgreementsList);
		setItemRender(new MMAgreementListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MMAgreementsList_NewMMAgreements, "button_MMAgreementsList_NewMMAgreements", true);
		registerButton(button_MMAgreementsList_MMAgreementsSearch);

		registerField("MMAId", SortOrder.ASC);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("MMAReference", listheader_MMAReference, SortOrder.NONE, mMAReferennce,
				sortOperator_MMAReferennce, Operators.STRING);
		registerField("ContractAmt", listheader_ContractAmt);
		registerField("ContractDate", listheader_ContractDate);
		registerField("Rate", listheader_Rate);

		// Render the page and display the data.
		doRenderPage();
		search();
		doSetFieldProperties();

		logger.debug("Entering");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCIF.setMaxlength(6);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_MMAgreementsList_MMAgreementsSearch(Event event) {
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
	public void onClick$button_MMAgreementsList_NewMMAgreements(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		MMAgreement aMMAgreement = new MMAgreement();
		aMMAgreement.setNewRecord(true);
		aMMAgreement.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aMMAgreement);

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onMMAgreementItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxMMAgreements.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		MMAgreement aMMAgreement = mMAgreementService.getMMAgreementById(id);

		if (aMMAgreement == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND MMAId='" + aMMAgreement.getMMAId() + "' AND version=" + aMMAgreement.getVersion()
				+ " ";

		if (doCheckAuthority(aMMAgreement, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aMMAgreement.getWorkflowId() == 0) {
				aMMAgreement.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aMMAgreement);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aMMAgreement
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(MMAgreement aMMAgreement) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("MMAgreement", aMMAgreement);
		arg.put("MMAgreementsListCtrl", this);
		arg.put("enqiryModule", super.enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/MMAgreements/MMAgreementsDialog.zul", null,
					arg);
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

	public void setmMAgreementService(MMAgreementService mMAgreementService) {
		this.mMAgreementService = mMAgreementService;
	}
}
