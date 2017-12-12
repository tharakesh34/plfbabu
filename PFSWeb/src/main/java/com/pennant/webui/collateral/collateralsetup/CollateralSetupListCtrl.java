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
 * FileName    		:  CollateralSetupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-12-2016    														*
 *                                                                  						*
 * Modified Date    :  13-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.collateral.collateralsetup;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.collateral.collateralsetup.model.CollateralSetupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/collateral/CollateralSetup/CollateralSetupList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CollateralSetupListCtrl extends GFCBaseListCtrl<CollateralSetup> {

	private static final long serialVersionUID = -6298187334342045748L;
	private static final Logger					logger				= Logger.getLogger(CollateralSetupListCtrl.class);

	protected Window							window_CollateralSetupList;
	protected Borderlayout						borderLayout_CollateralSetupList;
	protected Paging							pagingCollateralSetupList;
	protected Listbox							listBoxCollateralSetup;

	// List headers
	protected Listheader						listheader_CollateralRef;
	protected Listheader						listheader_DepositorCif;
	protected Listheader						listheader_CollateralType;
	protected Listheader						listheader_CollateralCcy;
	protected Listheader						listheader_MaxCollateralValue;
	protected Listheader						listheader_SpecialLTV;
	protected Listheader						listheader_ExpiryDate;
	protected Listheader						listheader_ReviewFrequency;
	protected Listheader						listheader_NextReviewDate;

	// checkRights
	protected Button							button_CollateralSetupList_NewCollateralSetup;
	protected Button							button_CollateralSetupList_CollateralSetupSearch;
	protected Button							btnRefresh;

	protected Textbox							collateralRef;
	protected Textbox							collateralType;
	protected Textbox							collateralCcy;
	protected Datebox							expiryDate;
	protected Datebox							nextReviewDate;
	protected Textbox							depositorCif;

	protected Listbox							sortOperator_CollateralRef;
	protected Listbox							sortOperator_DepositorCif;
	protected Listbox							sortOperator_CollateralType;
	protected Listbox							sortOperator_CollateralCcy;
	protected Listbox							sortOperator_ExpiryDate;
	protected Listbox							sortOperator_NextReviewDate;

	private transient CollateralSetupService	collateralSetupService;
	private transient FinanceDetailService 		financeDetailService;
	private transient FinanceWorkFlowService 	financeWorkFlowService;
	private transient WorkFlowDetails workFlowDetails  =  null;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	
	private String module = null;
	
	/**
	 * default constructor.<br>
	 */
	public CollateralSetupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CollateralSetup";
		super.pageRightName = "CollateralSetupList";
		super.tableName = "CollateralSetup_AView";
		super.queueTableName = "CollateralSetup_View";
		super.enquiryTableName = "CollateralSetup_View";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_CollateralSetupList(Event event) throws Exception {
		
		//Getting moduleName from mainmenu.xml
		module = getArgument("module");
		
		// Set the page level components.
		setPageComponents(window_CollateralSetupList, borderLayout_CollateralSetupList, listBoxCollateralSetup,
				pagingCollateralSetupList);
		setItemRender(new CollateralSetupListModelItemRenderer());

		
		//SetFormats 
		this.expiryDate.setFormat(PennantConstants.dateFormat);
		this.nextReviewDate.setFormat(PennantConstants.dateFormat);
		
		// Register buttons and fields.
		boolean accessToCreateNewColl = getFinanceDetailService().checkFirstTaskOwnerAccess(getUserWorkspace().getUserRoleSet(),
				FinanceConstants.FINSER_EVENT_ORG, PennantConstants.WORFLOW_MODULE_COLLATERAL);
		setFirstTask(accessToCreateNewColl);
		
		registerButton(button_CollateralSetupList_NewCollateralSetup, "button_CollateralSetupList_NewCollateralSetup",true);
		registerButton(button_CollateralSetupList_CollateralSetupSearch);
		registerField("depositorCif", listheader_DepositorCif, SortOrder.ASC, depositorCif, sortOperator_DepositorCif,
				Operators.STRING);
		registerField("collateralRef", listheader_CollateralRef, SortOrder.ASC, collateralRef,
				sortOperator_CollateralRef, Operators.STRING);
		registerField("collateralType", listheader_CollateralType, SortOrder.ASC, collateralType,
				sortOperator_CollateralType, Operators.STRING);
		registerField("collateralCcy", listheader_CollateralCcy, SortOrder.ASC, collateralCcy,
				sortOperator_CollateralCcy, Operators.STRING);
		registerField("expiryDate", listheader_ExpiryDate, SortOrder.ASC, expiryDate, sortOperator_ExpiryDate,
				Operators.DATE);
		registerField("nextReviewDate", listheader_NextReviewDate, SortOrder.ASC, nextReviewDate, sortOperator_NextReviewDate,
				Operators.DATE);
		registerField("nextRoleCode");

		
		// Render the page and display the data.
		doRenderPage();
		search();
		
		if ("E".equals(module)) {
			this.button_CollateralSetupList_NewCollateralSetup.setVisible(false);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CollateralSetupList_CollateralSetupSearch(Event event) {
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
	public void onClick$button_CollateralSetupList_NewCollateralSetup(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CollateralSetup collateralSetup = new CollateralSetup();
		collateralSetup.setNewRecord(true);

		// Display the dialog page.
		Map<String, Object> arg = getDefaultArguments();
		arg.put("collateralSetup", collateralSetup);
		arg.put("collateralSetupListCtrl", this);
		arg.put("role", getUserWorkspace().getUserRoles());
		
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/SelectCollateralTypeDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.collateral.collateralsetup.model.CollateralSetupListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCollateralSetupItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCollateralSetup.getSelectedItem();

		// Get the selected entity.
		CollateralSetup collateralSetup = (CollateralSetup) selectedItem.getAttribute("collateralSetup");
		
		// Set Workflow Details
		String userRole = collateralSetup.getNextRoleCode();
		if(StringUtils.isEmpty(collateralSetup.getRecordType())){
			setWorkflowDetails(collateralSetup.getCollateralType());
			if(workFlowDetails == null){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
			
			if(StringUtils.isEmpty(userRole)){
				userRole = workFlowDetails.getFirstTaskOwner();
			}
		}
					
		CollateralSetup aCollateralSetup = collateralSetupService.getCollateralSetupByRef(collateralSetup.getCollateralRef(), userRole, false);

		if (aCollateralSetup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		
		//Role Code State Checking
		String nextroleCode = aCollateralSetup.getNextRoleCode();
		if(StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)){
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=aCollateralSetup.getCollateralRef();
			errParm[0]=PennantJavaUtil.getLabel("label_CollateralRef")+":"+valueParm[0];

			ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
					PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetails.getError());
			
			Events.sendEvent(Events.ON_CLICK, this.btnRefresh, null);
			logger.debug("Leaving");
			return;
		}
		
		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CollateralType='" + aCollateralSetup.getCollateralType() + "' AND version=" + aCollateralSetup.getVersion() + " ";

		if (doCheckAuthority(aCollateralSetup, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aCollateralSetup.getWorkflowId() == 0) {
				aCollateralSetup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aCollateralSetup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Dynamic Workflow Details
	 * @param collateralType
	 */
	private void setWorkflowDetails(String collateralType){

		// Setting Workflow Details
		FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(collateralType, 
				FinanceConstants.FINSER_EVENT_ORG, CollateralConstants.MODULE_NAME);

		// Workflow Details Setup
		if (financeWorkFlow != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);

			if (workFlowDetails.getFirstTaskOwner().contains(PennantConstants.DELIMITER_COMMA)) {
				String[] fisttask = workFlowDetails.getFirstTaskOwner().split(PennantConstants.DELIMITER_COMMA);
				for (String string : fisttask) {
					if (getUserWorkspace().isRoleContains(string)) {
						setFirstTask(true);
						break;
					}
				}
			} else {
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			}
			setWorkFlowId(workFlowDetails.getId());
		}

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CollateralSetup collateralSetup) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("collateralSetup", collateralSetup);
		arg.put("collateralSetupListCtrl", this);
		if ("E".equals(module)) {
			arg.put("module", "E");
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null, arg);
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
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.depositorCif.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.depositorCif.setValue(customer.getCustCIF());
		} else {
			this.depositorCif.setValue("");
		}
		logger.debug("Leaving ");
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}
	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	public void setFinanceWorkFlowService(
			FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

}