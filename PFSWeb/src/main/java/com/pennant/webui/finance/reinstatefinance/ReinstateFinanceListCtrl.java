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
 * FileName    		:  ReinstateFinanceListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2011    														*
 *                                                                  						*
 * Modified Date    :  23-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.reinstatefinance;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.service.finance.ReinstateFinanceService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.webui.finance.reinstatefinance.model.ReinstateFinanceListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/ReinstateFinance/ReinstateFinanceList.zul file.
 */
public class ReinstateFinanceListCtrl extends GFCBaseListCtrl<ReinstateFinance> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(ReinstateFinanceListCtrl.class);

	protected Window 			window_ReinstateFinanceList; 			
	protected Borderlayout 		borderLayout_ReinstateFinanceList; 		
	protected Paging 			pagingReinstateFinanceList; 			
	protected Listbox 			listBoxReinstateFinance; 				

	protected Textbox 			custCIF; 								
	protected Textbox 			finType; 								
	protected Textbox 			finCcy; 								
	protected Textbox 			finBranch;								
	protected Textbox 			scheduleMethod; 						
	protected Textbox 			profitDaysBasis; 						
	
	protected Button  			btnClose; 								
	protected Div 	  			div_ToolBar;       						

	protected Listbox 			sortOperator_custCIF; 					
	protected Listbox 			sortOperator_finType; 					
	protected Listbox 			sortOperator_finCcy; 					
	protected Listbox 			sortOperator_finBranch;					
	protected Listbox 			sortOperator_scheduleMethod; 			
	protected Listbox 			sortOperator_profitDaysBasis; 			

	protected Listheader 		listheader_FinType;						
	protected Listheader 		listheader_FinProduct;					
	protected Listheader 		listheader_CustCIF;						
	protected Listheader 		listheader_FinRef;						
	protected Listheader 		listheader_FinBranch;					
	protected Listheader 		listheader_FinStartDate;				
	protected Listheader 		listheader_NumberOfTerms;				
	protected Listheader 		listheader_MaturityDate;				
	protected Listheader 		listheader_FinCcy;						
	protected Listheader 		listheader_FinAmount;					
	protected Listheader 		listheader_CurFinAmount;				
	protected Listheader 		listheader_FinReference; 				

	protected Button button_ReinstateFinanceList_NewReinstateFinance; 			
	protected Button button_ReinstateFinanceList_ReinstateFinanceSearchDialog;  

	protected Textbox finReference;                             
	protected Listbox sortOperator_finReference;                

	// NEEDED for the ReUse in the SearchWindow
	private List<Filter> 		filterList;
	
	private transient ReinstateFinanceService reinstateFinanceService;
	private FinanceWorkFlowService  financeWorkFlowService;
	private String buildedWhereCondition = "";


	/**
	 * default constructor.<br>
	 */
	public ReinstateFinanceListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		
		searchObject.addTabelName("ReinstateFinance_View");
		searchObject.addWhereClause(getUsrFinAuthenticationQry(false));
		searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), 
				getUserWorkspace().isAllowed("button_ReinstateFinanceList_NewReinstateFinance"));
		String rolecodeList = "";
		buildedWhereCondition = "";
		if (getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()) {
			for (String role : getUserWorkspace().getUserRoles()) {
				rolecodeList = rolecodeList.concat(role).concat("','");
			}

			if(StringUtils.isNotEmpty(rolecodeList)){
				rolecodeList = rolecodeList.substring(0,rolecodeList.length()-2);
				rolecodeList = "'".concat(rolecodeList);
			}
		}

		if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
			buildedWhereCondition = " (NextRoleCode IS NULL ";
		} else {
			buildedWhereCondition = " (NextRoleCode = '' ";
		}
		buildedWhereCondition = buildedWhereCondition.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
		buildedWhereCondition = buildedWhereCondition.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
		buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");
		buildedWhereCondition = buildedWhereCondition.concat(FinanceConstants.FINSER_EVENT_REINSTATE);
		buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat("))) OR NextRoleCode IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat(") ");

		if(StringUtils.isNotEmpty(buildedWhereCondition)){
			this.searchObject.addWhereClause(buildedWhereCondition); 
		}
		if(filterList != null && !filterList.isEmpty()){
			for (Filter filter : filterList) {
				this.searchObject.addFilter(filter);
			}
		}
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReinstateFinance";
		super.pageRightName = "ReinstateFinanceList";
		super.tableName = "ReinstateFinance_View";
		super.queueTableName = "ReinstateFinance_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReinstateFinanceList(Event event) throws Exception {
		// Set the page level components.
		setPageComponents(window_ReinstateFinanceList, borderLayout_ReinstateFinanceList, listBoxReinstateFinance, pagingReinstateFinanceList);
		setItemRender(new ReinstateFinanceListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReinstateFinanceList_NewReinstateFinance, "button_ReinstateFinanceList_NewReinstateFinance", true);
		registerButton(button_ReinstateFinanceList_ReinstateFinanceSearchDialog);

		registerField("CustCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF,
				Operators.MULTISELECT);
		registerField("FinReference", listheader_FinRef, SortOrder.NONE, finReference, sortOperator_finReference,
				Operators.MULTISELECT);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType,
				Operators.MULTISELECT);
		registerField("finCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.MULTISELECT);
		registerField("finBranch", listheader_FinBranch, SortOrder.NONE, finBranch, sortOperator_finBranch,
				Operators.MULTISELECT);
		registerField("ScheduleMethod", scheduleMethod, SortOrder.NONE, sortOperator_scheduleMethod,
				Operators.MULTISELECT);
		registerField("finCategory");
		registerField("finStartDate");
		registerField("numberOfTerms");
		registerField("maturityDate");
		registerField("feeChargeAmt");

		// Render the page and display the data.
		doRenderPage();
		search();
	
		logger.debug("Entering");

	}
	
	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ReinstateFinanceList_ReinstateFinanceSearchDialog(Event event) {
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
	public void onClick$button_ReinstateFinanceList_NewReinstateFinance(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ReinstateFinance aReinstateFinance = new ReinstateFinance();
		aReinstateFinance.setNewRecord(true);
		aReinstateFinance.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aReinstateFinance);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onReinstateFinanceItemDoubleClicked(Event event) {

		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReinstateFinance.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		ReinstateFinance aReinstateFinance = reinstateFinanceService.getReinstateFinanceById(id);

		if (aReinstateFinance == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinReference='" + aReinstateFinance.getFinReference() + "' AND version="
				+ aReinstateFinance.getVersion() + " ";

		if (doCheckAuthority(aReinstateFinance, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aReinstateFinance.getWorkflowId() == 0) {
				aReinstateFinance.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aReinstateFinance);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}


	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aReinstateFinance
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReinstateFinance aReinstateFinance) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("reinstateFinance", aReinstateFinance);
		arg.put("reinstateFinanceListCtrl", this);
		arg.put("eventCode", FinanceConstants.FINSER_EVENT_REINSTATE);
		try {
			if (aReinstateFinance.isNew()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/SelectFinance/SelectFinReferenceDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/ReinstateFinance/ReinstateFinanceDialog.zul", null,
						arg);
			}

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
	 * When user clicks on "btnSearchFinRef" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinRef(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finReference, this.finReference, "FinanceFlag");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchBranch" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finBranch, this.finBranch, "Branch");

		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finType, this.finType, "FinanceType");

		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finCcy, this.finCcy, "Currency");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_custCIF, this.custCIF, "Customer");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSchdMethod(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_scheduleMethod, this.scheduleMethod, "ScheduleMethod");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchPftDaysBasis(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_profitDaysBasis, this.profitDaysBasis, "InterestRateBasisCode");

		logger.debug("Leaving");

	}
	
	
	public JdbcSearchObject<ReinstateFinance> getSearchObject() {

		
		this.searchObject.addTabelName("ReinstateFinance_View");
		this.searchObject.addWhereClause(getUsrFinAuthenticationQry(false));
		this.searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), 
				getUserWorkspace().isAllowed("button_ReinstateFinanceList_NewReinstateFinance"));
		String rolecodeList = "";
		buildedWhereCondition = "";
		if (getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()) {
			for (String role : getUserWorkspace().getUserRoles()) {
				rolecodeList = rolecodeList.concat(role).concat("','");
			}

			if(StringUtils.isNotEmpty(rolecodeList)){
				rolecodeList = rolecodeList.substring(0,rolecodeList.length()-2);
				rolecodeList = "'".concat(rolecodeList);
			}
		}

		if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
			buildedWhereCondition = " (NextRoleCode IS NULL ";
		} else {
			buildedWhereCondition = " (NextRoleCode = '' ";
		}
		buildedWhereCondition = buildedWhereCondition.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
		buildedWhereCondition = buildedWhereCondition.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
		buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");
		buildedWhereCondition = buildedWhereCondition.concat(FinanceConstants.FINSER_EVENT_REINSTATE);
		buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat("))) OR NextRoleCode IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat(") ");

		if(StringUtils.isNotEmpty(buildedWhereCondition)){
			this.searchObject.addWhereClause(buildedWhereCondition); 
		}
		if(filterList != null && !filterList.isEmpty()){
			for (Filter filter : filterList) {
				this.searchObject.addFilter(filter);
			}
		}
		return this.searchObject;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setReinstateFinanceService(ReinstateFinanceService reinstateFinanceService) {
		this.reinstateFinanceService = reinstateFinanceService;
	}
	public ReinstateFinanceService getReinstateFinanceService() {
		return this.reinstateFinanceService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
}