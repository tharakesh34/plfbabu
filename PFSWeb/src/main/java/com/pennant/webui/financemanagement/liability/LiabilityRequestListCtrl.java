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
 * FileName    		:  LiabilityRequestListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2015    														*
 *                                                                  						*
 * Modified Date    :  31-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.liability;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.liability.service.LiabilityRequestService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.financemanagement.liability.model.LiabilityRequestListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance
 * Management/LiabilityRequest/LiabilityRequestList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class LiabilityRequestListCtrl extends GFCBaseListCtrl<LiabilityRequest> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LiabilityRequestListCtrl.class);

	protected Window window_LiabilityRequestList; 
	protected Borderlayout borderLayout_LiabilityRequestList; 
	protected Paging pagingLiabilityRequestList; 
	protected Listbox listBoxLiabilityRequest; 

	// List headers
	protected Listheader listheader_FinReference; 
	protected Listheader listheader_InitiatedBy; 
	protected Listheader listheader_FinType; 
	protected Listheader listheader_FinBranch; 
	protected Listheader listheader_FinStartDate; 
	protected Listheader listheader_MaturityDate; 
	protected Listheader listheader_FinAmount; 
	protected Listheader listheader_FinCcy; 
	protected Listheader listheader_CustCIF; 
	protected Listheader listheader_NumberOfTerms; 

	protected Button button_LiabilityRequestList_NewLiabilityRequest; 
	protected Button button_LiabilityRequestList_LiabilityRequestSearch; 

	private LiabilityRequestService liabilityRequestService;
	private FinanceDetailService financeDetailService;
	private WorkFlowDetails workFlowDetails = null;
	private CustomerDetailsService customerDetailsService;

	protected Textbox finReference; 
	protected Textbox custCIF; 
	protected Textbox finType; 
	protected Textbox finCcy; 
	protected Textbox finBranch; 
	protected Listbox sortOperator_FinReference; 
	protected Listbox sortOperator_custCIF; 
	protected Listbox sortOperator_finType; 
	protected Listbox sortOperator_finCcy; 
	protected Listbox sortOperator_finBranch; 

	protected Longbox initiatedBy; 
	protected Listbox sortOperator_InitiatedBy; 

	protected Textbox moduleType; 
	protected Tabbox tabbox;

	private String moduleDefiner = "";
	private String workflowCode = "";
	private String eventCodeRef = "";
	
	private int oldVar_sortOperator_finReference;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private String buildedWhereCondition = "";

	/**
	 * default constructor.<br>
	 */
	public LiabilityRequestListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LiabilityRequest";
		super.pageRightName = "LiabilityRequestList";
		super.tableName = "FinLiabilityReq_View";
		super.queueTableName = "FinLiabilityReq_View";

	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		searchObject.addFilter(new Filter("FinEvent", moduleDefiner, Filter.OP_EQUAL));
		searchObject.addWhereClause(getUsrFinAuthenticationQry(false));
		/*searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
				true);*/
		
		String rolecodeList = "";
		buildedWhereCondition = "";
		if (getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()) {
			for (String role : getUserWorkspace().getUserRoles()) {
				rolecodeList = rolecodeList.concat(role).concat("','");
			}

			if (StringUtils.isNotEmpty(rolecodeList)) {
				rolecodeList = rolecodeList.substring(0, rolecodeList.length() - 2);
				rolecodeList = "'".concat(rolecodeList);
			}
		}

		if (StringUtils.isNotEmpty(workflowCode)) {

			if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
				buildedWhereCondition = " (NextRoleCode IS NULL ";
			} else {
				buildedWhereCondition = " (NextRoleCode = '' ";
			}
			buildedWhereCondition = buildedWhereCondition
					.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
			buildedWhereCondition = buildedWhereCondition
					.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
			buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");
			buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
			buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
			buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
			buildedWhereCondition = buildedWhereCondition.concat("))) OR NextRoleCode IN(");
			buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
			buildedWhereCondition = buildedWhereCondition.concat(") ");

		}
		if (StringUtils.isNotEmpty(buildedWhereCondition)) {
		searchObject.addWhereClause(buildedWhereCondition);
		}
	}
	
	
	
	public void onCreate$window_LiabilityRequestList(Event event) throws Exception {
		logger.debug("Entering");

		if (event.getTarget().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();

			String menuItemName = tabbox.getSelectedTab().getId();
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");
			if (getUserWorkspace().getHasMenuRights().containsKey(menuItemName)) {
				// menuItemRightName =
				// getUserWorkspace().getHasMenuRights().get(menuItemName);
			}

			// Set Workflow codes based on tab id's
			checkAndSetModDef(tabbox);
		}

		// Set the page level components.
		setPageComponents(window_LiabilityRequestList, borderLayout_LiabilityRequestList, listBoxLiabilityRequest, pagingLiabilityRequestList);
		setItemRender(new LiabilityRequestListModelItemRenderer());

		boolean accessToCreateNewNOC = getFinanceDetailService().checkFirstTaskOwnerAccess(getUserWorkspace().getUserRoleSet(),
				workflowCode, PennantConstants.WORFLOW_MODULE_FINANCE);
		setFirstTask(accessToCreateNewNOC);
		
		// Register buttons and fields.
		registerButton(button_LiabilityRequestList_NewLiabilityRequest, "button_LiabilityRequestList_NewLiabilityRequest", true);
		registerButton(button_LiabilityRequestList_LiabilityRequestSearch);

		registerField("FinReference", listheader_FinReference, SortOrder.ASC,finReference,sortOperator_FinReference,
				Operators.MULTISELECT);
		registerField("InitiatedBy",initiatedBy, SortOrder.NONE,  sortOperator_InitiatedBy,
				Operators.STRING);
		registerField("CustCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF,
				Operators.MULTISELECT);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType,
				Operators.MULTISELECT);
		registerField("finCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.MULTISELECT);
		registerField("finBranch", listheader_FinBranch, SortOrder.NONE, finBranch, sortOperator_finBranch,
				Operators.MULTISELECT);
		registerField("FinStartDate",listheader_FinStartDate,SortOrder.NONE);
		registerField("FinEvent",SortOrder.NONE);
		registerField("MaturityDate",listheader_MaturityDate,SortOrder.NONE);
		registerField("NumberOfTerms",listheader_NumberOfTerms,SortOrder.NONE);
		registerField("FinAmount",listheader_FinAmount,SortOrder.NONE);
		// Render the page and display the data.
		doRenderPage();
		search();
	
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_LiabilityRequestList_LiabilityRequestSearch(Event event) {
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
	public void onClick$button_LiabilityRequestList_NewLiabilityRequest(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		LiabilityRequest aLiabilityRequest = new LiabilityRequest();
		aLiabilityRequest.setNewRecord(true);
		aLiabilityRequest.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aLiabilityRequest);

		logger.debug("Leaving");
	}

	
	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onLiabilityRequestItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLiabilityRequest.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String finEvent = (String) selectedItem.getAttribute("finEvent");
		LiabilityRequest aLiabilityRequest = liabilityRequestService.getLiabilityRequestById(id, finEvent);

		if (aLiabilityRequest == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		} else {

			String userRole = aLiabilityRequest.getNextRoleCode();
			if (StringUtils.isBlank(aLiabilityRequest.getRecordType())) {
				
				// Set Workflow Details
				setWorkflowDetails(aLiabilityRequest.getFinType());

				if (workFlowDetails == null) {
					MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
					return;
				}

				if (StringUtils.isBlank(userRole)) {
					userRole = workFlowDetails.getFirstTaskOwner();
				}
				
				aLiabilityRequest.setWorkflowId(workFlowDetails.getWorkFlowId());
				
				
				
			}

			// Fetch Total Finance Details Object
			FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(
					aLiabilityRequest.getFinReference(), "_View", false);
			financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(false);
			financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
					financeDetail.getFinScheduleData().getFinanceMain().getCustID(), true, "_View"));
			financeDetail.setDocumentDetailsList(getFinanceDetailService().getFinDocByFinRef(
					aLiabilityRequest.getFinReference(), moduleDefiner, "_View"));
			financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail, userRole, "DDE",
					eventCodeRef, moduleDefiner, false);
			if (financeDetail != null) {
				aLiabilityRequest.setFinanceDetail(financeDetail);
			}

		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinReference='" + aLiabilityRequest.getId() + "'" + " AND version="
				+ aLiabilityRequest.getVersion() + " ";

		if (doCheckAuthority(aLiabilityRequest, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aLiabilityRequest.getWorkflowId() == 0) {
				aLiabilityRequest.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aLiabilityRequest);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

		



	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aLiabilityRequest
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LiabilityRequest aLiabilityRequest) {

		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("liabilityRequest", aLiabilityRequest);
		arg.put("financeDetail", aLiabilityRequest.getFinanceDetail());
		arg.put("liabilityRequestListCtrl", this);
		arg.put("moduleDefiner", moduleDefiner);
		arg.put("eventCode", eventCodeRef);

		try {
			if (aLiabilityRequest.isNew()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/SelectFinance/SelectFinReferenceDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/LiabilityRequest/LiabilityRequestDialog.zul", null,
						arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void setWorkflowDetails(String finType) {

		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.isNotEmpty(workflowCode)) {
			FinanceWorkFlow financeWorkflow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(finType,
					workflowCode, PennantConstants.WORFLOW_MODULE_FINANCE);//TODO: Check Promotion case
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}

	}

	public void onClick$btnSearchCustBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_finBranch, this.finBranch, "Branch");

		logger.debug("Leaving" + event.toString());
	}
	/**
	 * When user clicks on button "btnSearchFinRef" button
	 * 
	 * @param event
	 */

	public void onClick$btnSearchFinRef(Event event) {
		logger.debug("Entering " + event.toString());
		Filter[] module = new Filter[1];
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_NOCISSUANCE)) {
			module[0] = new Filter("FinEvent",FinanceConstants.FINSER_EVENT_NOCISSUANCE, Filter.OP_EQUAL);
		}else if(moduleDefiner.equals(FinanceConstants.FINSER_EVENT_LIABILITYREQ)){
			module[0] = new Filter("FinEvent",FinanceConstants.FINSER_EVENT_LIABILITYREQ, Filter.OP_EQUAL);
		}else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_TIMELYCLOSURE)) {
			module[0] = new Filter("FinEvent",FinanceConstants.FINSER_EVENT_TIMELYCLOSURE, Filter.OP_EQUAL);
		}else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_INSCLAIM)) {
			module[0] = new Filter("FinEvent",FinanceConstants.FINSER_EVENT_INSCLAIM, Filter.OP_EQUAL);
		}
		if (this.oldVar_sortOperator_finReference == Filter.OP_IN
				|| this.oldVar_sortOperator_finReference == Filter.OP_NOT_IN) {
			// Calling MultiSelection ListBox From DB
			String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_LiabilityRequestList,
					"LiabilityRequest", this.finReference.getValue(), module,StringUtils.trimToNull(this.searchObject.getWhereClause()));
			if (selectedValues != null) {
				this.finReference.setValue(selectedValues);
			}

		} else {

			Object dataObject = ExtendedSearchListBox.show(this.window_LiabilityRequestList, "LiabilityRequest",this.finReference.getValue(),module,StringUtils.trimToNull(this.searchObject.getWhereClause()));
			if (dataObject instanceof String) {
				this.finReference.setValue("");
			} else {
				LiabilityRequest details = (LiabilityRequest) dataObject;
				if (details != null) {
					this.finReference.setValue(details.getFinReference());
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void onSelect$sortOperator_finReference(Event event) {
		this.oldVar_sortOperator_finReference = doChangeStringOperator(sortOperator_FinReference, oldVar_sortOperator_finReference, this.finReference);
	}
	
	private int doChangeStringOperator(Listbox listbox, int oldOperator, Textbox textbox) {

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if (oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN) {
			if (!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)) {
				textbox.setValue("");
			}
		} else {
			if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
				textbox.setValue("");
			}
		}
		return searchOpId;

	}

	/**
	 * When user clicks on "btnSearchBranch" button This method displays
	 * ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering  " + event.toString());
		
		setSearchValue(sortOperator_finBranch, this.finBranch, "Branch");
		
		logger.debug("Leaving" + event.toString());
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

	/**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_finCcy, this.finCcy, "Currency");
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
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
	 * Method to check and set module definer value
	 * 
	 * @param tab
	 *            (Tab)
	 * */
	private void checkAndSetModDef(Tabbox tabbox) {
		logger.debug("Entering");
		// filterList = new ArrayList<Filter>();

		if (tabbox != null) {
			Tab tab = tabbox.getSelectedTab();
			if (tab != null) {
				if ("tab_LiabilityRequest".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_LIABILITYREQ;
					eventCodeRef = AccountEventConstants.ACCEVENT_LIABILITY;
					workflowCode = FinanceConstants.FINSER_EVENT_LIABILITYREQ;
				} else if ("tab_NOCIssuance".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_NOCISSUANCE;
					eventCodeRef = AccountEventConstants.ACCEVENT_NOCISSUANCE;
					workflowCode = FinanceConstants.FINSER_EVENT_NOCISSUANCE;
				} else if ("tab_TimelyClosure".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_TIMELYCLOSURE;
					eventCodeRef = AccountEventConstants.ACCEVENT_TAKAFULCLAIM;
					workflowCode = FinanceConstants.FINSER_EVENT_TIMELYCLOSURE;
				} else if ("tab_TakafulClaim".equals(tab.getId())) {
					moduleDefiner = FinanceConstants.FINSER_EVENT_INSCLAIM;
					eventCodeRef = AccountEventConstants.ACCEVENT_TAKAFULCLAIM;
					workflowCode = FinanceConstants.FINSER_EVENT_INSCLAIM;
				}
				return;
			}
		} else {
			workflowCode = "";
			return;
		}
		logger.debug("Leaving");
	}


	public void setLiabilityRequestService(LiabilityRequestService liabilityRequestService) {
		this.liabilityRequestService = liabilityRequestService;
	}

	public LiabilityRequestService getLiabilityRequestService() {
		return this.liabilityRequestService;
	}

	
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return this.financeWorkFlowService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}