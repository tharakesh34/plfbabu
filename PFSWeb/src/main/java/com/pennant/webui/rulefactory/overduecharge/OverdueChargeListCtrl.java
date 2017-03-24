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
 * FileName    		:  OverdueChargeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rulefactory.overduecharge;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.service.rulefactory.OverdueChargeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.webui.rulefactory.overduecharge.model.OverdueChargeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the /WEB-INF/pages/RuleFactory/OverdueCharge/OverdueChargeList.zul
 * file.
 */
public class OverdueChargeListCtrl extends GFCBaseListCtrl<OverdueCharge> {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(OverdueChargeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_OverdueChargeList; // autowired
	protected Borderlayout borderLayout_OverdueChargeList; // autowired
	protected Paging pagingOverdueChargeList; // autowired
	protected Listbox listBoxOverdueCharge; // autowired

	// List headers
	protected Listheader listheader_ODCRuleCode; // autowired
	protected Listheader listheader_ODCPLAccount; // autowired
	protected Listheader listheader_ODCCharityAccount; // autowired
	protected Listheader listheader_ODCPLShare; // autowired
	protected Listheader listheader_ODCSweepCharges; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;
	
	//Search
	protected Textbox oDCRuleCode; // autowired
	protected Listbox sortOperator_oDCRuleCode; // autowired
	protected Textbox oDCPLAccount; // autowired
	protected Listbox sortOperator_oDCPLAccount; // autowired
	protected Textbox oDCCharityAccount; // autowired
	protected Listbox sortOperator_oDCCharityAccount; // autowired
  	protected Decimalbox oDCPLShare; // autowired
  	protected Listbox sortOperator_oDCPLShare; // autowired
	protected Checkbox oDCSweepCharges; // autowired
	protected Listbox sortOperator_oDCSweepCharges; // autowired
	protected Textbox recordStatus; // autowired
	protected Listbox recordType;	// autowired
	protected Listbox sortOperator_recordStatus; // autowired
	protected Listbox sortOperator_recordType; // autowired
	
	protected Label label_OverdueChargeSearch_RecordStatus; // autowired
	protected Label label_OverdueChargeSearch_RecordType; // autowired
	protected Label label_OverdueChargeSearchResult; // autowired

	protected Grid	                       searchGrid;	
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;
	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_OverdueChargeList_NewOverdueCharge; // autowired
	protected Button button_OverdueChargeList_OverdueChargeSearchDialog; // autowired
	protected Button button_OverdueChargeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<OverdueCharge> searchObj;
	
	private transient OverdueChargeService overdueChargeService;
	
	/**
	 * default constructor.<br>
	 */
	public OverdueChargeListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		moduleCode = "OverdueCharge";
	}

	public void onCreate$window_OverdueChargeList(Event event) throws Exception {
		logger.debug("Entering");
		
		// DropDown ListBox		
		this.sortOperator_oDCRuleCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_oDCRuleCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCPLAccount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_oDCPLAccount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCCharityAccount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_oDCCharityAccount.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCPLShare.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_oDCPLShare.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_oDCSweepCharges.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_oDCSweepCharges.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_OverdueChargeSearch_RecordStatus.setVisible(false);
			this.label_OverdueChargeSearch_RecordType.setVisible(false);
		}
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_OverdueChargeList.setHeight(getBorderLayoutHeight());
		this.listBoxOverdueCharge.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingOverdueChargeList.setPageSize(getListRows());
		this.pagingOverdueChargeList.setDetailed(true);

		this.listheader_ODCRuleCode.setSortAscending(new FieldComparator("oDCRuleCode", true));
		this.listheader_ODCRuleCode.setSortDescending(new FieldComparator("oDCRuleCode", false));
		this.listheader_ODCPLAccount.setSortAscending(new FieldComparator("oDCPLAccount", true));
		this.listheader_ODCPLAccount.setSortDescending(new FieldComparator("oDCPLAccount", false));
		this.listheader_ODCCharityAccount.setSortAscending(new FieldComparator("oDCCharityAccount", true));
		this.listheader_ODCCharityAccount.setSortDescending(new FieldComparator("oDCCharityAccount", false));
		this.listheader_ODCPLShare.setSortAscending(new FieldComparator("oDCPLShare", true));
		this.listheader_ODCPLShare.setSortDescending(new FieldComparator("oDCPLShare", false));
		this.listheader_ODCSweepCharges.setSortAscending(new FieldComparator("oDCSweepCharges", true));
		this.listheader_ODCSweepCharges.setSortDescending(new FieldComparator("oDCSweepCharges", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		// set the itemRenderer
		this.listBoxOverdueCharge.setItemRenderer(new OverdueChargeListModelItemRenderer());
		doSearch();
		if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
			this.workFlowFrom.setVisible(false);
			this.fromApproved.setSelected(true);
		}
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxOverdueCharge, this.pagingOverdueChargeList);

		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("OverdueChargeList");
		
		this.button_OverdueChargeList_NewOverdueCharge.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeList_NewOverdueCharge"));
		this.button_OverdueChargeList_OverdueChargeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeList_OverdueChargeFindDialog"));
		this.button_OverdueChargeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.rulefactory.overduecharge.model.OverdueChargeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onOverdueChargeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected OverdueCharge object
		final Listitem item = this.listBoxOverdueCharge.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final OverdueCharge aOverdueCharge = (OverdueCharge) item.getAttribute("data");
			final OverdueCharge overdueCharge = getOverdueChargeService().getOverdueChargeById(aOverdueCharge.getId());
			
			if(overdueCharge==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aOverdueCharge.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_ODCRuleCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showErrorMessage(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND ODCRuleCode='"+ overdueCharge.getODCRuleCode()+"' AND version=" + overdueCharge.getVersion()+" ";

					boolean userAcces =  validateUserAccess(overdueCharge.getWorkflowId(),getUserWorkspace().getLoggedInUser().getLoginUsrID(), "OverdueCharge", whereCond, overdueCharge.getTaskId(), overdueCharge.getNextTaskId());
					if (userAcces){
						showDetailView(overdueCharge);
					}else{
						MessageUtil.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(overdueCharge);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the OverdueCharge dialog with a new empty entry. <br>
	 */
	public void onClick$button_OverdueChargeList_NewOverdueCharge(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new OverdueCharge object, We GET it from the backend.
		final OverdueCharge aOverdueCharge = getOverdueChargeService().getNewOverdueCharge();
		aOverdueCharge.setODCSweepCharges(false);
		showDetailView(aOverdueCharge);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param OverdueCharge (aOverdueCharge)
	 * @throws Exception
	 */
	private void showDetailView(OverdueCharge aOverdueCharge) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aOverdueCharge.getWorkflowId()==0 && isWorkFlowEnabled()){
			aOverdueCharge.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("overdueCharge", aOverdueCharge);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the OverdueChargeListbox from the
		 * dialog when we do a delete, edit or insert a OverdueCharge.
		 */
		map.put("overdueChargeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/OverdueCharge/OverdueChargeDialog.zul",null,map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_OverdueChargeList);
		logger.debug("Leaving");
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug(event.toString());
		this.sortOperator_oDCCharityAccount.setSelectedIndex(0);
		this.oDCCharityAccount.setValue("");
		this.sortOperator_oDCPLAccount.setSelectedIndex(0);
		this.oDCPLAccount.setValue("");
		this.sortOperator_oDCPLShare.setSelectedIndex(0);
		this.oDCPLShare.setValue("");
		this.sortOperator_oDCRuleCode.setSelectedIndex(0);
		this.oDCRuleCode.setValue("");
		this.sortOperator_oDCSweepCharges.setSelectedIndex(0);
		this.oDCSweepCharges.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		/*this.pagingOverdueChargeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_OverdueChargeList, event);
		this.window_OverdueChargeList.invalidate();*/
		logger.debug("Leaving");
	}

	/*
	 * call the OverdueCharge dialog
	 */
	
	public void onClick$button_OverdueChargeList_OverdueChargeSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the overdueCharge print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_OverdueChargeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		PTListReportUtils reportUtils = new PTListReportUtils("OverdueCharge", getSearchObj(),this.pagingOverdueChargeList.getTotalSize()+1);
		logger.debug("Leaving");
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<OverdueCharge>(OverdueCharge.class,getListRows());
		this.searchObj.addSort("ODCRuleCode", false);
		this.searchObj.addTabelName("FinODCHeader_View");
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_OverdueChargeList_NewOverdueCharge.setVisible(true);
			} else {
				button_OverdueChargeList_NewOverdueCharge.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("FinODCHeader_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("FinODCHeader_AView");
		}
		// De-dup parameter query code
		if (StringUtils.isNotBlank(this.oDCRuleCode.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_oDCRuleCode.getSelectedItem(), this.oDCRuleCode.getValue(), "oDCRuleCode");
		}
		
		// De-dup parameter query module
		if (StringUtils.isNotBlank(this.oDCPLAccount.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_oDCPLAccount.getSelectedItem(), this.oDCPLAccount.getValue(), "oDCPLAccount");
		}
		// De-dup parameter query desc
		if (StringUtils.isNotBlank(this.oDCCharityAccount.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_oDCCharityAccount.getSelectedItem(), this.oDCCharityAccount.getValue(), "oDCCharityAccount");
		}
		// De-dup parameter query code
		if (this.oDCPLShare.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_oDCPLShare.getSelectedItem(), this.oDCPLShare.getValue(), "oDCPLShare");
		}
		if (oDCSweepCharges.isChecked()) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_oDCSweepCharges.getSelectedItem(), 1,"oDCSweepCharges");
		} else {
			searchObj = getSearchFilter(searchObj,this.sortOperator_oDCSweepCharges.getSelectedItem(), 0,"oDCSweepCharges");
		}
		// Record Status
		if (StringUtils.isNotBlank(recordStatus.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}
		// Record Type
		if (this.recordType.getSelectedItem() != null 
				&& !"".equals(StringUtils.trimToEmpty(String.valueOf(this.recordType.getSelectedItem().getValue())))) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}
		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxOverdueCharge, this.pagingOverdueChargeList);
		logger.debug("Leaving");
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public void setOverdueChargeService(OverdueChargeService overdueChargeService) {
		this.overdueChargeService = overdueChargeService;
	}

	public OverdueChargeService getOverdueChargeService() {
		return this.overdueChargeService;
	}

	public JdbcSearchObject<OverdueCharge> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<OverdueCharge> searchObj) {
		this.searchObj = searchObj;
	}
}