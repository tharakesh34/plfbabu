package com.pennant.webui.applicationmaster.policecase;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.service.applicationmaster.PoliceCaseService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.applicationmaster.policecase.model.PoliceCaseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class PoliceCaseListCtrl extends GFCBaseListCtrl<PoliceCaseDetail> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(PoliceCaseListCtrl.class);

	protected Window window_PoliceCaseList;
	protected Borderlayout borderLayout_PoliceCaseList;
	protected Paging pagingPoliceCaseList;
	protected Listbox listBoxPoliceCase;

	protected Listheader listheader_PoliceCaseCustCIF;
	protected Listheader listheader_PoliceCaseDOB;
	protected Listheader listheader_PoliceCaseCustFName;
	protected Listheader listheader_PoliceCaseCustLName;
	protected Listheader listheader_PoliceCaseCustEIDNumber;
	protected Listheader listheader_PoliceCaseCustPassport;
	protected Listheader listheader_PoliceCaseCustMobileNumber;
	protected Listheader listheader_PoliceCaseCustNationality;

	protected Button button_PoliceCaseList_NewPoliceCase;
	protected Button button_PoliceCaseList_PoliceCaseSearchDialog;

	protected Textbox policeCaseCustCIF;
	protected Datebox policeCaseCustDOB;
	protected Textbox policeCaseCustFName;
	protected Textbox policeCaseCustLName;
	protected Textbox policeCaseCustEIDNumber;
	protected Textbox policeCaseCustPassPort;
	protected Textbox policeCaseCustMobileNumber;
	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;
	protected Textbox policeCaseCustNationality;

	protected Listbox sortOperator_policeCaseCustCIF;
	protected Listbox sortOperator_policeCaseCustDOB;
	protected Listbox sortOperator_custFName;
	protected Listbox sortOperator_custLName;
	protected Listbox sortOperator_policeCaseCustEidNumber;
	protected Listbox sortOperator_policeCaseCustPassport;
	protected Listbox sortOperator_custMobileNumber;
	protected Listbox sortOperator_custNationality;

	private transient PoliceCaseService policeCaseService;

	/**
	 * default constructor.<br>
	 */
	public PoliceCaseListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PoliceCaseDetail";
		super.pageRightName = "PoliceCaseCustomersList";
		super.tableName = "PoliceCaseCustomers_View";
		super.queueTableName = "PoliceCaseCustomers_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		super.searchObject.addFilter(new Filter("CustCIF", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		
		String phoneNumber = PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
				this.phoneAreaCode.getValue(), this.policeCaseCustMobileNumber.getValue());
		Filter filter = SearchFilterControl.getFilter("mobileNumber", phoneNumber,
				sortOperator_custMobileNumber);
		if(filter != null){
			searchObject.addFilter(filter);
		}
		
		if (StringUtils.isNotBlank(this.policeCaseCustEIDNumber.getValue())){
			String eIDNumber=PennantApplicationUtil.unFormatEIDNumber(this.policeCaseCustEIDNumber.getValue());
			super.searchObject.addFilter(SearchFilterControl.getFilter("custCRCPR", eIDNumber,
					sortOperator_policeCaseCustEidNumber));
		}
	}

	@Override
	protected void doReset() {
		super.doReset();
		SearchFilterControl.resetFilters(phoneAreaCode, sortOperator_custMobileNumber);
		SearchFilterControl.resetFilters(phoneCountryCode, sortOperator_custMobileNumber);
		SearchFilterControl.resetFilters(policeCaseCustMobileNumber, sortOperator_custMobileNumber);
		SearchFilterControl.resetFilters(policeCaseCustEIDNumber, sortOperator_policeCaseCustEidNumber);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PoliceCaseList(Event event) {

		// Set the page level components.
		setPageComponents(window_PoliceCaseList, borderLayout_PoliceCaseList, listBoxPoliceCase, pagingPoliceCaseList);
		setItemRender(new PoliceCaseListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PoliceCaseList_NewPoliceCase,
				"button_PoliceCaseCustomersList_NewPoliceCaseCustomersList", true);
		registerButton(button_PoliceCaseList_PoliceCaseSearchDialog);

		registerField("custCIF", listheader_PoliceCaseCustCIF, SortOrder.ASC, policeCaseCustCIF,
				sortOperator_policeCaseCustCIF, Operators.STRING);
		registerField("custDOB", listheader_PoliceCaseDOB, SortOrder.NONE, policeCaseCustDOB,
				sortOperator_policeCaseCustDOB, Operators.DATE);
		registerField("custFName", listheader_PoliceCaseCustFName, SortOrder.NONE, policeCaseCustFName,
				sortOperator_custFName, Operators.STRING);
		registerField("custLName", listheader_PoliceCaseCustLName, SortOrder.NONE, policeCaseCustLName,
				sortOperator_custLName, Operators.STRING);
		registerField("custCRCPR", listheader_PoliceCaseCustEIDNumber);
		registerField("custPassportNo", listheader_PoliceCaseCustPassport, SortOrder.NONE, policeCaseCustPassPort,
				sortOperator_policeCaseCustPassport, Operators.STRING);
		registerField("mobileNumber", listheader_PoliceCaseCustMobileNumber);
		registerField("custNationality", listheader_PoliceCaseCustNationality, SortOrder.NONE,
				policeCaseCustNationality, sortOperator_custNationality, Operators.STRING);


		SearchFilterControl.renderOperators(this.sortOperator_custMobileNumber, Operators.STRING);
		SearchFilterControl.renderOperators(this.sortOperator_policeCaseCustEidNumber, Operators.STRING);

		doSetFieldProperties();

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
	public void onClick$button_PoliceCaseList_PoliceCaseSearchDialog(Event event) {
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
	public void onClick$button_PoliceCaseList_NewPoliceCase(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		PoliceCaseDetail policeCaseDetailkDetail = new PoliceCaseDetail();
		policeCaseDetailkDetail.setNewRecord(true);
		policeCaseDetailkDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(policeCaseDetailkDetail);

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPoliceCaseItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPoliceCase.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		PoliceCaseDetail policeCaseDetail = policeCaseService.getPoliceCaseDetailById(id);
		if (policeCaseDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CustCIF='" + policeCaseDetail.getCustCIF() + "'" + " AND version="
				+ policeCaseDetail.getVersion() + " ";

		if (doCheckAuthority(policeCaseDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && policeCaseDetail.getWorkflowId() == 0) {
				policeCaseDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(policeCaseDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.phoneAreaCode.setMaxlength(3);
		this.phoneCountryCode.setMaxlength(3);
		this.policeCaseCustMobileNumber.setMaxlength(8);
		this.policeCaseCustCIF.setMaxlength(6);
		this.policeCaseCustFName.setMaxlength(50);
		this.policeCaseCustLName.setMaxlength(50);
		this.recordStatus.setMaxlength(50);
		this.policeCaseCustEIDNumber.setMaxlength(LengthConstants.LEN_EID);
		this.policeCaseCustPassPort.setMaxlength(50);
		this.policeCaseCustNationality.setMaxlength(2);
		this.policeCaseCustDOB.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aPoliceCaseDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PoliceCaseDetail aPoliceCaseDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("policeCaseDetail", aPoliceCaseDetail);
		arg.put("policeCaseListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/PoliceCase/PoliceCaseDialog.zul", null, arg);
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

	public void setPoliceCaseService(PoliceCaseService policeCaseService) {
		this.policeCaseService = policeCaseService;
	}

}
