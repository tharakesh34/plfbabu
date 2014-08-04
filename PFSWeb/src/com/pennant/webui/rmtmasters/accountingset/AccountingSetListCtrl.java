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
 * FileName    		:  AccountingSetListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountingset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
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
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.accountingset.model.AccountingSetListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/AccountingSet/AccountingSetList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AccountingSetListCtrl extends GFCBaseListCtrl<AccountingSet> implements Serializable {

	private static final long serialVersionUID	= 4322539879503951300L;
	private final static Logger logger = Logger.getLogger(AccountingSetListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	    window_AccountingSetList;	     // autowired
	protected Borderlayout	borderLayout_AccountingSetList;	 // autowired
	protected Paging	    pagingAccountingSetList;	     // autowired
	protected Listbox	    listBoxAccountingSet;	         // autowired

	// List headers
	protected Listheader	listheader_EventCode;	         // autowired
	protected Listheader	listheader_EventDesc;	         // autowired
	protected Listheader	listheader_AccountSetCode;	     // autowired
	protected Listheader	listheader_AccountSetCodeName;	 // autowired
	protected Listheader	listheader_RecordStatus;	     // autowired
	protected Listheader	listheader_RecordType;

	// Filtering Fieds
	protected Textbox eventCode; 							// autowired
	protected Listbox sortOperator_eventCode; 				// autowired
	protected Textbox accountSetCode; 						// autowired
	protected Listbox sortOperator_accountSetCode; 			// autowired
	protected Textbox accountSetCodeName; 					// autowired
	protected Listbox sortOperator_accountSetCodeName; 		// autowired
	protected Textbox recordStatus; 						// autowired
	protected Listbox recordType;							// autowired
	protected Listbox sortOperator_recordStatus; 			// autowired
	protected Listbox sortOperator_recordType; 				// autowired
	
	protected Listbox sortOperator_accountEventDesc;       // autowired
	protected Textbox accountEventDesc;                    // autowired
	
	protected Label label_AccountingSetSearch_RecordStatus; // autowired
	protected Label label_AccountingSetSearch_RecordType; 	// autowired
	protected Label label_AccountingSetSearchResult; 		// autowired
	
	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;
	
	private transient boolean  approvedList=false;
	// checkRights
	protected Button	    btnHelp;	                                        // autowired
	protected Button	    button_AccountingSetList_NewAccountingSet;	        // autowired
	protected Button	    button_AccountingSetList_AccountingSetSearchDialog;	// autowired
	protected Button	    button_AccountingSetList_PrintList;	                // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AccountingSet>	searchObj;
	private transient AccountingSetService	  accountingSetService;
	private transient WorkFlowDetails	      workFlowDetails	= null;

	/**
	 * default constructor.<br>
	 */
	public AccountingSetListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountingSet object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountingSetList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("AccountingSet");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountingSet");

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
		
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		
		this.sortOperator_eventCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_eventCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_accountSetCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_accountSetCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_accountSetCodeName.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_accountSetCodeName.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_accountEventDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_accountEventDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_AccountingSetSearch_RecordStatus.setVisible(false);
			this.label_AccountingSetSearch_RecordType.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_AccountingSetList.setHeight(getBorderLayoutHeight());
		this.listBoxAccountingSet.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		
		// set the paging parameters
		this.pagingAccountingSetList.setPageSize(getListRows());
		this.pagingAccountingSetList.setDetailed(true);

		this.listheader_EventCode.setSortAscending(new FieldComparator("eventCode", true));
		this.listheader_EventCode.setSortDescending(new FieldComparator("eventCode", false));

		this.listheader_EventDesc.setSortAscending(new FieldComparator("lovDescEventCodeName", true));
		this.listheader_EventDesc.setSortDescending(new FieldComparator("lovDescEventCodeName", false));

		this.listheader_AccountSetCode.setSortAscending(new FieldComparator("accountSetCode", true));
		this.listheader_AccountSetCode.setSortDescending(new FieldComparator("accountSetCode", false));
		
		
		this.listheader_AccountSetCodeName.setSortAscending(new FieldComparator("accountSetCodeName", true));
		this.listheader_AccountSetCodeName.setSortDescending(new FieldComparator("accountSetCodeName", false));

		// set the itemRenderer
		this.listBoxAccountingSet.setItemRenderer(new AccountingSetListModelItemRenderer());
		
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
			this.button_AccountingSetList_NewAccountingSet.setVisible(false);
			this.button_AccountingSetList_AccountingSetSearchDialog.setVisible(false);
			this.button_AccountingSetList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AccountingSetList");

		this.button_AccountingSetList_NewAccountingSet.setVisible(getUserWorkspace().
				isAllowed("button_AccountingSetList_NewAccountingSet"));
		this.button_AccountingSetList_AccountingSetSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_AccountingSetList_AccountingSetFindDialog"));
		this.button_AccountingSetList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_AccountingSetList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.accountingset.model.AccountingSetListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAccountingSetItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected AccountingSet object
		final Listitem item = this.listBoxAccountingSet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AccountingSet aAccountingSet = (AccountingSet) item.getAttribute("data");
			final AccountingSet accountingSet = getAccountingSetService().getAccountingSetById(
					aAccountingSet.getId());

			if (accountingSet == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(aAccountingSet.getId());
				errParm[0] = PennantJavaUtil.getLabel("label_AccountSetid") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005", errParm, valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND AccountSetid=" + accountingSet.getAccountSetid() + 
					" AND version=" + accountingSet.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), 
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "AccountingSet", 
							whereCond, accountingSet.getTaskId(), accountingSet.getNextTaskId());
					if (userAcces) {
						showDetailView(accountingSet);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(accountingSet);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the AccountingSet dialog with a new empty entry. <br>
	 */
	public void onClick$button_AccountingSetList_NewAccountingSet(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new AccountingSet object, We GET it from the backEnd.
		final AccountingSet aAccountingSet = getAccountingSetService().getNewAccountingSet();
		if (event.getData() != null) {
			copyDATA(aAccountingSet, event.getData());
		}
		showDetailView(aAccountingSet);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param AccountingSet (aAccountingSet)
	 * @throws Exception
	 */
	private void showDetailView(AccountingSet aAccountingSet) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aAccountingSet.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aAccountingSet.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accountingSet", aAccountingSet);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listBox ListModel. This is fine for synchronizing the data in the AccountingSetListbox from the dialog when
		 * we do a delete, edit or insert a AccountingSet.
		 */
		map.put("accountingSetListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/AccountingSet/AccountingSetDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_AccountingSetList);
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering" + event.toString());
		
		this.sortOperator_eventCode.setSelectedIndex(0);
		this.eventCode.setValue("");
		this.sortOperator_accountSetCode.setSelectedIndex(0);
		this.accountSetCode.setValue("");
		this.sortOperator_accountSetCodeName.setSelectedIndex(0);
		this.accountSetCodeName.setValue("");
		this.sortOperator_accountEventDesc.setSelectedIndex(0);
		this.accountEventDesc.setValue("");
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		doSearch();
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the AccountingSet dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountingSetList_AccountingSetSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
          doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the accountingSet print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_AccountingSetList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("AccountingSet", getSearchObj(),this.pagingAccountingSetList.getTotalSize() + 1);

		logger.debug("Leaving" + event.toString());
	}

	public void doSearch(){
		
logger.debug("Entering");
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<AccountingSet>(AccountingSet.class,getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("EventCode", false);
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTAccountingSet_View");

			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;
				
			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		
		if(approvedList){
			this.searchObj.addTabelName("RMTAccountingSet_AView");
		}else{
			this.searchObj.addTabelName("RMTAccountingSet_View");
		}
		
		// Accounting Event Code
		if (!StringUtils.trimToEmpty(this.eventCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_eventCode.getSelectedItem(),this.eventCode.getValue(), "eventCode");
		}
		// Accounting Set code
		if (!StringUtils.trimToEmpty(this.accountSetCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_accountSetCode.getSelectedItem(),this.accountSetCode.getValue(), "accountSetCode");
		}
		// Accounting Set code
		if (!StringUtils.trimToEmpty(this.accountEventDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_accountEventDesc.getSelectedItem(),this.accountEventDesc.getValue(), "LovDescEventCodeName");
		}
		
		// Accounting Set code Name
		if (!StringUtils.trimToEmpty(this.accountSetCodeName.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_accountSetCodeName.getSelectedItem(),this.accountSetCodeName.getValue(), "accountSetCodeName");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxAccountingSet,this.pagingAccountingSetList);

		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//	

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}
	public AccountingSetService getAccountingSetService() {
		return this.accountingSetService;
	}

	public JdbcSearchObject<AccountingSet> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<AccountingSet> searchObj) {
		this.searchObj = searchObj;
	}

	private AccountingSet copyDATA(AccountingSet newAcset, Object data) {
		AccountingSet sourceSet = (AccountingSet) data;
		newAcset.setEventCode(sourceSet.getEventCode());
		newAcset.setLovDescEventCodeName(sourceSet.getLovDescEventCodeName());
		List<TransactionEntry> transactionEntries = new ArrayList<TransactionEntry>();

		for (TransactionEntry sourTransEntry : sourceSet.getTransactionEntries()) {
			TransactionEntry trgTransEntry = getAccountingSetService().getNewTransactionEntry();

			trgTransEntry.setAccountSetid(sourTransEntry.getAccountSetid());
			trgTransEntry.setTransOrder(sourTransEntry.getTransOrder());
			trgTransEntry.setTransDesc(sourTransEntry.getTransDesc());
			trgTransEntry.setDebitcredit(sourTransEntry.getDebitcredit());
			trgTransEntry.setShadowPosting(sourTransEntry.isShadowPosting());
			trgTransEntry.setAccount(sourTransEntry.getAccount());
			trgTransEntry.setAccountType(sourTransEntry.getAccountType());
			trgTransEntry.setAccountBranch(sourTransEntry.getAccountBranch());
			trgTransEntry.setLovDescAccountTypeName(sourTransEntry.getLovDescAccountTypeName());
			trgTransEntry.setLovDescAccountBranchName(sourTransEntry.getLovDescAccountBranchName());
			trgTransEntry.setAccountSubHeadRule(sourTransEntry.getAccountSubHeadRule());
			trgTransEntry.setLovDescAccountSubHeadRuleName(sourTransEntry.getLovDescAccountSubHeadRuleName());
			trgTransEntry.setTranscationCode(sourTransEntry.getTranscationCode());
			trgTransEntry.setLovDescTranscationCodeName(sourTransEntry.getLovDescTranscationCodeName());
			trgTransEntry.setRvsTransactionCode(sourTransEntry.getRvsTransactionCode());
			trgTransEntry.setLovDescRvsTransactionCodeName(sourTransEntry.getLovDescRvsTransactionCodeName());
			trgTransEntry.setAmountRule(sourTransEntry.getAmountRule());
			trgTransEntry.setFeeCode(sourTransEntry.getFeeCode());
			//trgTransEntry.setRuleDecider(sourTransEntry.getRuleDecider());
			trgTransEntry.setLovDescFeeCodeName(sourTransEntry.getLovDescFeeCodeName());
			trgTransEntry.setLovDescEventCodeName(sourTransEntry.getLovDescEventCodeName());
			trgTransEntry.setLovDescEventCodeDesc(sourTransEntry.getLovDescEventCodeDesc());
			trgTransEntry.setLovDescAccSetCodeName(sourTransEntry.getLovDescAccSetCodeName());
			trgTransEntry.setLovDescAccSetCodeDesc(sourTransEntry.getLovDescAccSetCodeDesc());
			trgTransEntry.setLovDescSysInAcTypeName(sourTransEntry.getLovDescSysInAcTypeName());

			trgTransEntry.setAccountBranch(sourTransEntry.getAccountBranch());
			trgTransEntry.setVersion(1);
			trgTransEntry.setRecordType(PennantConstants.RCD_ADD);

			//Prepare List Of Entries
			transactionEntries.add(trgTransEntry);
		}
		newAcset.setTransactionEntries(transactionEntries);
		return newAcset;
	}

}