/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceMainListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul file.
 */
public class FinanceDeviationsListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = -5901195042041627750L;
	private static final Logger logger = Logger.getLogger(FinanceDeviationsListCtrl.class);

	protected Window window_FinanceDeviationsList;
	protected Borderlayout borderLayout_FinanceMainList;
	protected Paging pagingFinanceMainList;
	protected Listbox listBoxFinanceMain;

	protected Textbox finReference;
	protected Listbox sortOperator_finReference;
	protected Textbox finType;
	protected Listbox sortOperator_finType;
	protected Textbox custCIF;
	protected Listbox sortOperator_custID;
	protected Longbox custID;
	protected Textbox fincustName;
	protected Listbox sortOperator_custName;
	protected Textbox finMobileNumber;
	protected Listbox sortOperator_mobileNumber;
	protected Textbox finEIDNumber;
	protected Listbox sortOperator_eidNumber;
	protected Textbox finPassPort;
	protected Listbox sortOperator_passPort;
	protected Datebox finDateofBirth;
	protected Listbox sortOperator_finDateofBirth;
	protected Datebox finRequestDate;
	protected Listbox sortOperator_finRequestDate;
	protected Listbox sortOperator_finPromotion;
	protected Textbox finPromotion;
	protected Listbox sortOperator_finRequestStage;
	protected Combobox finRequestStage;
	protected Listbox sortOperator_finQueuePriority;
	protected Combobox finQueuePriority;
	protected Textbox phoneCountryCode;
	protected Textbox phoneAreaCode;
	protected Datebox initiateDate;
	protected Listbox sortOperator_InitiateDate;

	protected Listheader listheader_CustomerCIF;
	protected Listheader listheader_CustomerName;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinCcy;
	protected Listheader listheader_FinAmount;
	protected Listheader listheader_FinancingAmount;
	protected Listheader listheader_Promotion;
	protected Listheader listheader_InitiateDate;
	protected Listheader listheader_Terms;
	protected Listheader listheader_RequestStage;
	protected Listheader listheader_Priority;

	protected Button button_FinanceMainList_FinanceMainSearchDialog;
	protected Button btnRefresh;

	private transient FinanceDeviationsService deviationDetailsService;
	protected int oldVar_sortOperator_finType;

	private Textbox loanType;// Field for Maintain Different Finance Product Types
	private String menuItemRightName = null;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	/**
	 * default constructor.<br>
	 */
	public FinanceDeviationsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceMain";
		super.pageRightName = "FinanceMainList";
		super.tableName = "FinanceMain_LView";
		super.queueTableName = "FinanceMain_LView";
		super.enquiryTableName = "FinanceMain_LView";
	}

	@Override
	protected void doAddFilters() {
		setWorkFlowEnabled(false);
		super.doAddFilters();

		if (StringUtils.isNotBlank(this.finMobileNumber.getValue())
				&& StringUtils.isNotBlank(this.phoneAreaCode.getValue())
				&& StringUtils.isNotBlank(this.phoneCountryCode.getValue())) {
			String phoneNumber = PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
					this.phoneAreaCode.getValue(), this.finMobileNumber.getValue());
			searchObject
					.addFilter(SearchFilterControl.getFilter("PhoneNumber", phoneNumber, sortOperator_mobileNumber));
		}

		searchObject.addFilter(new Filter("InvestmentRef", "", Filter.OP_EQUAL));
		searchObject.addFilter(new Filter("DeviationApproval", 1, Filter.OP_EQUAL));
		searchObject.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_EQUAL));
		searchObject.addSortDesc("Priority");
	}

	@Override
	protected void doReset() {
		super.doReset();
		SearchFilterControl.resetFilters(phoneAreaCode, sortOperator_mobileNumber);
		SearchFilterControl.resetFilters(phoneCountryCode, sortOperator_mobileNumber);
		SearchFilterControl.resetFilters(finMobileNumber, sortOperator_mobileNumber);
	}

	protected void doPrintResults() {
		super.doPrintResults();
		try {
			new PTListReportUtils(this.loanType.getValue() + "FinanceMain", super.searchObject,
					this.pagingFinanceMainList.getTotalSize() + 1);
		} catch (InterruptedException e) {
			logger.error("Exception:", e);

		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceDeviationsList(Event event) {
		// Getting Menu Item Right Name
		if (getCurrentTab() != null) {
			String menuItemName = getCurrentTab();
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");

			if (getUserWorkspace().getHasMenuRights().containsKey(menuItemName)) {
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}
		}

		// Set the page level components.
		setPageComponents(window_FinanceDeviationsList, borderLayout_FinanceMainList, listBoxFinanceMain,
				pagingFinanceMainList);
		setItemRender(new FinanceMainListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceMainList_FinanceMainSearchDialog);

		registerField("LovDescCustCIF", listheader_CustomerCIF, SortOrder.NONE, custCIF, sortOperator_custID,
				Operators.STRING);
		registerField("FinReference", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("lovDescCustShrtName", listheader_CustomerName, SortOrder.NONE, fincustName,
				sortOperator_custName, Operators.STRING);
		registerField("PhoneNumber", finMobileNumber, SortOrder.NONE, sortOperator_mobileNumber, Operators.STRING);
		registerField("LovDescCustCRCPR", finEIDNumber, SortOrder.NONE, sortOperator_eidNumber, Operators.STRING);
		registerField("lovDescCustPassportNo", finPassPort, SortOrder.NONE, sortOperator_passPort, Operators.STRING);
		registerField("LovDescCustDOB", finDateofBirth, SortOrder.NONE, sortOperator_finDateofBirth, Operators.DATE);
		registerField("FinContractDate", finRequestDate, SortOrder.NONE, sortOperator_finRequestDate, Operators.DATE);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType,
				Operators.MULTISELECT);
		registerField("InitiateDate", listheader_InitiateDate, SortOrder.NONE, initiateDate, sortOperator_InitiateDate,
				Operators.DATE);

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("RoleCategory", "Finance", Filter.OP_EQUAL);
		filters[1] = new Filter("RoleCategory", "", Filter.OP_NOT_EQUAL);
		filters[2] = Filter.in("RoleCd", getUserWorkspace().getUserRoles());
		fillComboBox(this.finRequestStage, "", PennantAppUtil.getSecRolesList(filters), "");
		registerField("LovDescRequestStage", listheader_RequestStage, SortOrder.NONE, finRequestStage,
				sortOperator_finRequestStage, Operators.STRING);

		fillComboBox(this.finQueuePriority, "", PennantStaticListUtil.getQueuePriority(), "");
		registerField("Priority", listheader_Priority, SortOrder.NONE, finQueuePriority, sortOperator_finQueuePriority,
				Operators.STRING);

		registerField("LovDescFinTypeName");
		registerField("FinCcy");
		registerField("FinAmount");

		registerField("DownPayment");
		registerField("FeeChargeAmt");
		registerField("FinancingAmount");
		registerField("LovDescProductCodeName", SortOrder.ASC);
		registerField("LovDescFinFormatter");
		registerField("NumberOfTerms");
		registerField("LovDescFinProduct");
		registerField("NextRoleCode");
		registerField("FinPurpose");

		// Render the page and display the data.
		doRenderPage();
		doSetFieldProperties();
		search();

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FinanceMainList_FinanceMainSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinanceMain.getSelectedItem();

		final FinanceMain aFinanceMain = (FinanceMain) selectedItem.getAttribute("data");

		// Get the selected entity.
		FinanceDetail financeDetail = deviationDetailsService.getFinanceDetailById(aFinanceMain.getId());

		if (financeDetail == null) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = aFinanceMain.getId();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",
					errParm, valueParm), getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
		} else {
			doShowDialogPage(financeDetail);
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFinanceDetail
	 *            The entity that need to be passed to the dialog.
	 */
	protected void doShowDialogPage(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeDetail", aFinanceDetail);
		arg.put("financeDeviationsListCtrl", this);
		arg.put("menuItemRightName", menuItemRightName);
		arg.put("approval", menuItemRightName);

		try {
			String productType = aFinanceMain.getLovDescProductCodeName();
			productType = (productType.substring(0, 1)).toUpperCase() + (productType.substring(1)).toLowerCase();

			StringBuilder fileLocaation = new StringBuilder(
					"/WEB-INF/pages/Finance/FinanceMain/FinanceDeviationsDialog.zul");

			Executions.createComponents(fileLocaation.toString(), this.window_FinanceDeviationsList, arg);
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

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_finType, this.finType, "FinanceType");

		logger.debug("Leaving " + event.toString());
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	public String getCurrentTab() {
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		return tabbox.getSelectedTab().getId();
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custID.setMaxlength(26);
		this.finReference.setMaxlength(20);
		this.fincustName.setMaxlength(50);
		this.phoneAreaCode.setMaxlength(3);
		this.phoneCountryCode.setMaxlength(3);
		this.finMobileNumber.setMaxlength(8);
		this.finRequestDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finDateofBirth.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finPromotion.setMaxlength(50);
		this.recordStatus.setMaxlength(50);
		logger.debug("Leaving");
	}

	public void setDeviationDetailsService(FinanceDeviationsService deviationDetailsService) {
		this.deviationDetailsService = deviationDetailsService;
	}

}