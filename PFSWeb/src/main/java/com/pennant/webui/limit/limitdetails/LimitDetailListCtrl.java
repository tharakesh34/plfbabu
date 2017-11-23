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
 * FileName    		:  LimitDetailsListCtrl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.limit.limitdetails;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.limit.limitdetails.model.LimitDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Limit/LimitDetails/LimitDetailsList.zul file.<br>
 * ************************************************************<br>
 */
public class LimitDetailListCtrl extends GFCBaseListCtrl<LimitHeader> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger				logger				= Logger.getLogger(LimitDetailListCtrl.class);

	/*
	 * ************************************************************************
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ************************************************************************
	 */
	protected Window 		window_LimitDetailsList;
	protected Borderlayout  borderLayout_LimitDetailsList;
	protected Paging 		pagingLimitDetailsList;
	protected Listbox 		listBoxLimitDetails;

	// List headers
	protected Listheader 	listheader_Name;
	protected Listheader 	listheader_Id;
	protected Listheader 	listheader_ResponsibleBranch;
	protected Listheader 	listheader_LimitStructureCode;
	protected Listheader 	listheader_Currency;
	protected Listheader 	listheader_ExpiryDate;
	protected Listheader 	listheader_ReviewDate;
	protected Listheader	listheader_Active;
	protected Listheader 	listheader_RecordStatus;
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button 		btnHelp;
	protected Button 		button_LimitDetailsList_NewLimitDetails;
	protected Button 		button_LimitDetailsList_LimitDetailsSearch;
	protected Label  		label_LimitDetailsList_RecordStatus;
	protected Label  		label_LimitDetailsList_RecordType;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<LimitHeader> searchObj;

	private transient LimitDetailService limitDetailService;
	private transient WorkFlowDetails workFlowDetails = null;

	protected Textbox 		name;
	protected Listbox 		sortOperator_Name;

	protected Textbox 		id;
	protected Listbox 		sortOperator_Id;

	protected Textbox 		responsibleBranch;
	protected Listbox 		sortOperator_ResponsibleBranch;

	protected Textbox 		currency;
	protected Listbox 		sortOperator_Currency;

	protected Datebox 		expiryDate;
	protected Listbox 		sortOperator_ExpiryDate;

	protected Datebox 		reviewDate;
	protected Listbox 		sortOperator_ReviewDate;

	protected Textbox		limitStructureCode;
	protected Listbox 		sortOperator_LimitStructureCode;

	protected Listbox 		sortOperator_active;
	protected Checkbox 		active;

	protected Textbox 		recordStatus;
	protected Listbox 		recordType;
	protected Listbox 		sortOperator_RecordStatus;
	protected Listbox 		sortOperator_RecordType;
	protected Grid 			searchGrid;
	protected Textbox 		moduleType;
	protected Radio 		fromApproved;
	protected Radio 		fromWorkFlow;
	protected Row 			workFlowFrom;
	private   Textbox		limitType;

	private transient boolean approvedList = false;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;

	/**
	 * default constructor.<br>
	 */
	public LimitDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LimitHeader";
		super.pageRightName = "CustomerLimitDetailsList";
		super.tableName = "LimitHeader_AView";
		super.queueTableName = "LimitHeader_View";
		super.enquiryTableName = "LimitHeader_AView";
	}

	
	// ***************************************************//
	// *************** Component Events ******************//
	// ***************************************************//

	public void onCreate$window_LimitDetailsList(Event event) throws Exception {
		logger.debug("Entering");
		setPageComponents(window_LimitDetailsList, borderLayout_LimitDetailsList, listBoxLimitDetails, pagingLimitDetailsList);
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("LimitHeader");
		boolean wfAvailable = true;
		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitHeader");
			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}
		
		// ********************* DropDown ListBox ************************//



		this.sortOperator_Id.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_Id.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ResponsibleBranch.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ResponsibleBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_Currency.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_Currency.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ExpiryDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_ExpiryDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ReviewDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_ReviewDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_LimitStructureCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_LimitStructureCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_active.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_active.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_RecordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_RecordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
			this.sortOperator_RecordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = setRecordType(this.recordType);
			this.sortOperator_RecordType.setSelectedIndex(1);
			this.recordType.setSelectedIndex(0);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.label_LimitDetailsList_RecordStatus.setVisible(false);
			this.label_LimitDetailsList_RecordType.setVisible(false);
			this.sortOperator_RecordStatus.setVisible(false);
			this.sortOperator_RecordType.setVisible(false);
		}
		/* set components visible dependent on the users rights */
		doCheckRights();
		this.borderLayout_LimitDetailsList.setHeight(getBorderLayoutHeight());
		this.listBoxLimitDetails.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size()));
		// set the paging parameters
		this.pagingLimitDetailsList.setPageSize(getListRows());
		this.pagingLimitDetailsList.setDetailed(true);

		if(StringUtils.equals(LimitConstants.LIMIT_CUST, limitType.getValue())){

			this.sortOperator_Name.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_Name.setItemRenderer(new SearchOperatorListModelItemRenderer());

			listheader_Name.setSortAscending(new FieldComparator("CustShrtName, GroupName", true));
			listheader_Name.setSortDescending(new FieldComparator("CustShrtName, GroupName", false));

			listheader_Id.setSortAscending(new FieldComparator("CustCIF, CustGrpCode", true));
			listheader_Id.setSortDescending(new FieldComparator("CustCIF, CustGrpCode", false));

		}else if(StringUtils.equals(LimitConstants.LIMIT_RULE, limitType.getValue())){
			this.sortOperator_Name.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_Name.setItemRenderer(new SearchOperatorListModelItemRenderer());
			
			listheader_Id.setSortAscending(new FieldComparator("RuleCode", true));
			listheader_Id.setSortDescending(new FieldComparator("RuleCode", false));
			listheader_Id.setLabel(Labels.getLabel("label_LimitDetailsList_Rule.value"));
			
			listheader_Name.setSortAscending(new FieldComparator("QueryDesc", true));
			listheader_Name.setSortDescending(new FieldComparator("QueryDesc", false));
			listheader_Name.setLabel("Description");
		}else{
			this.sortOperator_Name.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_Name.setItemRenderer(new SearchOperatorListModelItemRenderer());

			listheader_Name.setSortAscending(new FieldComparator("CustShrtName, GroupName,RuleCode", true));
			listheader_Name.setSortDescending(new FieldComparator("CustShrtName, GroupName,RuleCode", false));

			listheader_Id.setSortAscending(new FieldComparator("CustCIF, CustGrpCode,QueryDesc", true));
			listheader_Id.setSortDescending(new FieldComparator("CustCIF, CustGrpCode,QueryDesc", false));
		}
		this.listheader_ResponsibleBranch.setSortAscending(new FieldComparator("responsibleBranchName", true));
		this.listheader_ResponsibleBranch.setSortDescending(new FieldComparator("responsibleBranchName", false));
		this.listheader_Currency.setSortAscending(new FieldComparator("limitCcy", true));
		this.listheader_Currency.setSortDescending(new FieldComparator("limitCcy", false));
		this.listheader_LimitStructureCode.setSortAscending(new FieldComparator("limitStructureCode", true));
		this.listheader_LimitStructureCode.setSortDescending(new FieldComparator("limitStructureCode", false));
		this.listheader_ExpiryDate.setSortAscending(new FieldComparator("limitExpiryDate", true));
		this.listheader_ExpiryDate.setSortDescending(new FieldComparator("limitExpiryDate", false));
		this.listheader_ReviewDate.setSortAscending(new FieldComparator("LimitRvwDate", true));
		this.listheader_ReviewDate.setSortDescending(new FieldComparator("LimitRvwDate", false));
		this.listheader_Active.setSortAscending(new FieldComparator("active", true));
		this.listheader_Active.setSortDescending(new FieldComparator("active", false));

		// set the itemRenderer
		this.listBoxLimitDetails.setItemRenderer(new LimitDetailListModelItemRenderer());
		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_LimitDetailsList_NewLimitDetails.setVisible(false);
			this.button_LimitDetailsList_LimitDetailsSearch.setVisible(false);
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_LimitStructureList_LimitStructureSearch(Event event) {
		doSearch();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		this.sortOperator_Name.setSelectedIndex(0);
		this.name.setValue("");
		this.sortOperator_Id.setSelectedIndex(0);
		this.id.setValue("");
		this.sortOperator_ResponsibleBranch.setSelectedIndex(0);
		this.responsibleBranch.setValue("");
		this.sortOperator_Currency.setSelectedIndex(0);
		this.currency.setValue("");
		this.sortOperator_ExpiryDate.setSelectedIndex(0);
		this.expiryDate.setValue(null);
		this.sortOperator_ReviewDate.setSelectedIndex(0);
		this.reviewDate.setValue(null);
		this.sortOperator_LimitStructureCode.setSelectedIndex(0);
		this.limitStructureCode.setValue("");

		if (isWorkFlowEnabled()) {
			this.sortOperator_RecordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_RecordType.setSelectedIndex(1);
			this.recordType.setSelectedIndex(0);
		}
		doReset();
		doSearch();
	}

	
	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_LimitDetailsList_NewLimitDetails(Event event) throws Exception {
		logger.debug(event.toString());
		// Create a new entity.
		LimitHeader aLimitHeader = new LimitHeader();
		aLimitHeader.setNewRecord(true);
		aLimitHeader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		if(StringUtils.equals(LimitConstants.LIMIT_RULE,limitType.getValue())){
			doShowDialogPage(aLimitHeader);
		}else
			showDetailViewLimitHeader(aLimitHeader);

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.limit.limitdetail.model.
	 * LimitDetailsListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onLimitDetailsItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// Get the selected record.
		Listitem selectedItem = this.listBoxLimitDetails.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		LimitHeader aLimitHeader = limitDetailService.getCustomerLimits(id);

		if (aLimitHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}else{
			aLimitHeader.setCustFullName(aLimitHeader.getCustShrtName());
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND HeaderId='" + aLimitHeader.getId() + "'  AND version=" + aLimitHeader.getVersion() + " ";


		if (doCheckAuthority(aLimitHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aLimitHeader.getWorkflowId() == 0) {
				aLimitHeader.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(aLimitHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}


	/**
	 * Invoke Search
	 */
	public void onClick$button_LimitDetailsList_LimitDetailsSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_LimitDetailsList);
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

	
	@Override
	protected void doPrintResults() {
		try {
			if(StringUtils.equals(LimitConstants.LIMIT_UTILIZATION, limitType.getValue())){
				this.searchObj.addTabelName("LimitHeader_AView");
				new PTListReportUtils("LimitUtilization", searchObj, this.pagingLimitDetailsList.getTotalSize() + 1);
			}else{
				this.searchObj.addTabelName("LimitHeader_View");
				new PTListReportUtils(moduleCode, searchObj, this.pagingLimitDetailsList.getTotalSize() + 1);
			}
			
		} catch (InterruptedException e) {
			logger.error("Exception:", e);
		}
	}
	

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	// ******************************************************************//
	// ************************ GUI operations ************************//
	// ******************************************************************//

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("CustomerLimitDetailsList");
		if (moduleType == null) {
			this.button_LimitDetailsList_NewLimitDetails.setVisible(getUserWorkspace().isAllowed("button_CustomerLimitDetailsList_NewCustomerLimitDetails"));
		} else {
			this.button_LimitDetailsList_NewLimitDetails.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	protected void doShowDialogPage(LimitHeader aLimitHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();


		arg.put("enqiryModule", super.enqiryModule);
		arg.put("limitHeader", aLimitHeader);
		arg.put("limitDetailListCtrl", this);
		arg.put("LimitType", limitType.getValue());

		// call the zul-file with the parameters packed in a map
		try {
			if (StringUtils.equals(limitType.getValue(), "LTUZ")) {
				Executions.createComponents("/WEB-INF/pages/Limit/LimitDetails/LimitUtilizationDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Limit/LimitDetails/LimitDetailsDialog.zul", null, arg);
			}
		}  catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	private void showDetailViewLimitHeader(LimitHeader aLimitHeader) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		Map<String, Object> map = getDefaultArguments();
		map.put("limitHeader", aLimitHeader);
		if (moduleType != null) {
			map.put("enqModule", true);
		} else {
			map.put("enqModule", false);
		}
		map.put("LimitType", limitType.getValue());
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the LimitDetailsListbox
		 * from the dialog when we do a delete, edit or insert a
		 * LimitDetails.
		 */
		map.put("limitDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Limit/LimitDetails/LimtDetailsHeaderDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */

	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<LimitHeader>(LimitHeader.class, getListRows());
		this.searchObj.addTabelName("LimitHeader_View");

		// ********************* DropDown ListBox ************************//
		this.searchObj.addField("headerId");
		this.searchObj.addSort("customerId", false);
		if(StringUtils.equals(LimitConstants.LIMIT_CUST, limitType.getValue())){
			this.searchObj.addFilterNull("Rulecode");			
		}else if(StringUtils.equals(LimitConstants.LIMIT_RULE, limitType.getValue())){
			this.searchObj.addFilterNotNull("Rulecode");
		}
		this.searchObj.addField("CustShrtName");
		this.searchObj.addField("customerGroup");
		this.searchObj.addField("GroupName");
		this.searchObj.addField("customerId");
		this.searchObj.addField("CustFullName");
		this.searchObj.addField("responsibleBranch");
		this.searchObj.addField("responsibleBranchName");
		this.searchObj.addField("LimitCcy");
		this.searchObj.addField("LimitExpiryDate");
		this.searchObj.addField("LimitRvwDate");
		this.searchObj.addField("limitStructureCode");
		this.searchObj.addField("StructureName");
		this.searchObj.addField("LimitSetupRemarks");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		this.searchObj.addField("custCIF");
		this.searchObj.addField("custGrpCode");
		this.searchObj.addField("RuleCode");
		this.searchObj.addField("Active");
		this.searchObj.addField("QueryDesc");

		// Workflow
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_LimitDetailsList_NewLimitDetails.setVisible(true);
			} else {
				button_LimitDetailsList_NewLimitDetails.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("LimitHeader_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("LimitHeader_AView");
		}

		if(StringUtils.equals(LimitConstants.LIMIT_CUST, limitType.getValue())){

			// Customer Group
			if (!StringUtils.trimToEmpty(this.name.getValue()).equals("")) {
				String condition = this.sortOperator_Name.getSelectedItem().getLabel();
				String whereName = new String();
				if (!condition.equals("%")) {
					whereName = " CustShrtName " + condition +" '" + this.name.getValue() + "' OR GroupName " + condition +" '"+ this.name.getValue() + "'";
				} else {
					whereName = " CustShrtName LIKE '"+ condition + this.name.getValue()  + condition +"' OR GroupName  LIKE '"+ condition + this.name.getValue() + condition + "'";
				}
				searchObj.addWhereClause(whereName);

			}
			// Customer Id
			if (!StringUtils.trimToEmpty(this.id.getValue()).equals("")) {
				String condition = this.sortOperator_Id.getSelectedItem().getLabel();
				String whereId = new String();
				if (!condition.equals("%")) {
					whereId = " CustCIF " + condition +" '" + this.id.getValue() + "' OR CustGrpCode " + condition +" '"+ this.id.getValue() + "'";
				} else {
					whereId = " CustCIF LIKE '"+ condition + this.id.getValue()  + condition +"' OR CustGrpCode  LIKE '"+ condition + this.id.getValue() + condition + "'";
				}
				searchObj.addWhereClause(whereId);

			}
		}else if(StringUtils.equals(LimitConstants.LIMIT_RULE, limitType.getValue())){
			//RuleCode
			if (!StringUtils.trimToEmpty(this.id.getValue()).equals("")) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_Id.getSelectedItem(), this.id.getValue(), "RuleCode");
			}
			if (!StringUtils.trimToEmpty(this.name.getValue()).equals("")) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_Name.getSelectedItem(), this.name.getValue(), "QueryDesc");
			}
		}else{
			{

				// Customer Group
				if (!StringUtils.trimToEmpty(this.name.getValue()).equals("")) {
					String condition = this.sortOperator_Name.getSelectedItem().getLabel();
					String whereName = new String();
					if (!condition.equals("%")) {
						whereName = " CustShrtName " + condition +" '" + this.name.getValue() + "' OR GroupName " + condition +" '"+ this.name.getValue() + "'";
					} else {
						whereName = " CustShrtName LIKE '"+ condition + this.name.getValue()  + condition +"' OR GroupName  LIKE '"+ condition + this.name.getValue() + condition + "'";
					}
					searchObj.addWhereClause(whereName);

				}
				// Customer Id
				if (!StringUtils.trimToEmpty(this.id.getValue()).equals("")) {
					String condition = this.sortOperator_Id.getSelectedItem().getLabel();
					String whereId = new String();
					if (!condition.equals("%")) {
						whereId = " CustCIF " + condition +" '" + this.id.getValue() + "' OR CustGrpCode " + condition +" '"+ this.id.getValue() + "'";
					} else {
						whereId = " CustCIF LIKE '"+ condition + this.id.getValue()  + condition +"' OR CustGrpCode  LIKE '"+ condition + this.id.getValue() + condition + "'";
					}
					searchObj.addWhereClause(whereId);

				}
			}
		}

		// Responsible Branch
		if (!StringUtils.trimToEmpty(this.responsibleBranch.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_ResponsibleBranch.getSelectedItem(), this.responsibleBranch.getValue(), "ResponsibleBranchName");
		}
		// Currency
		if (!StringUtils.trimToEmpty(this.currency.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_Currency.getSelectedItem(), this.currency.getValue(), "limitCcy");
		}
		// Expiry Date
		if (this.expiryDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_ExpiryDate.getSelectedItem(), DateUtility.formatDate(this.expiryDate.getValue(), PennantConstants.DBDateFormat), "LimitExpiryDate");
		}
		// Review Date
		if (this.reviewDate.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_ReviewDate.getSelectedItem(), DateUtility.formatDate(this.reviewDate.getValue(), PennantConstants.DBDateFormat), "LimitRvwDate");
		}

		// Active
		int intActive=0;
		if(this.active.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_active.getSelectedItem(),intActive, "Active");
		// Limit Structure Code
		if (!StringUtils.trimToEmpty(this.limitStructureCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_LimitStructureCode.getSelectedItem(), this.limitStructureCode.getValue(), "limitStructureCode");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !this.recordType.getSelectedItem().getValue().toString().isEmpty()) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_RecordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxLimitDetails, this.pagingLimitDetailsList);

		logger.debug("Leaving");
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
		this.id.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.id.setValue(customer.getCustCIF());
		} else {
			this.id.setValue("");
		}
		logger.debug("Leaving ");
	}




	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public JdbcSearchObject<LimitHeader> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<LimitHeader> searchObj) {
		this.searchObj = searchObj;
	}

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

}