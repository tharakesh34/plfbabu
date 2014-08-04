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
 * FileName    		:  AccountTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accounttype;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
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
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.accounttype.model.AccountTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/AccountType/AccountTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AccountTypeListCtrl extends GFCBaseListCtrl<AccountType> implements Serializable {

	private static final long serialVersionUID = -1631313247095254648L;
	private final static Logger logger = Logger.getLogger(AccountTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AccountTypeList; 		// auto wired
	protected Borderlayout 	borderLayout_AccountTypeList; 	// auto wired
	protected Paging 		pagingAccountTypeList; 			// auto wired
	protected Listbox 		listBoxAccountType; 			// auto wired

	// List headers
	protected Listheader listheader_AcType; 	         	// auto wired
	protected Listheader listheader_AcTypeDesc; 	        // auto wired
	protected Listheader listheader_AcPurpose; 		        // auto wired
	protected Listheader listheader_IsInternalAc; 	        // auto wired
	protected Listheader listheader_AcTypeIsActive;         // auto wired
	protected Listheader listheader_RecordStatus; 	        // auto wired
	protected Listheader listheader_RecordType;
	protected Listheader listheader_AcHead;                 // auto wired
	protected Listheader listheader_IsCustSysAccount;       // auto wired
	
	//Search
	protected Textbox 	acType; 							// auto wired
	protected Listbox 	sortOperator_acType; 				// auto wired
	protected Textbox 	acTypeDesc; 						// auto wired
	protected Listbox 	sortOperator_acTypeDesc; 			// auto wired
	protected Combobox 	acPurpose; 							// auto wired
	protected Listbox 	sortOperator_acPurpose; 			// auto wired
	protected Checkbox 	internalAc; 						// auto wired
	protected Listbox 	sortOperator_internalAc; 			// auto wired
	protected Checkbox 	acTypeIsActive; 					// auto wired
	protected Listbox 	sortOperator_acTypeIsActive; 		// auto wired
	protected Checkbox 	isCustSysAccount; 					// auto wired
	protected Listbox 	sortOperator_isCustSysAccount; 		// auto wired
	protected Textbox 	recordStatus; 						// auto wired
	protected Listbox 	recordType;							// auto wired
	protected Listbox 	sortOperator_recordStatus; 			// auto wired
	protected Listbox 	sortOperator_recordType; 			// auto wired

	protected Label label_AccountTypeSearch_RecordStatus; 	// auto wired
	protected Label label_AccountTypeSearch_RecordType; 	// auto wired
	protected Label label_AccountTypeSearchResult; 			// auto wired
	
	protected Grid	                       searchGrid;	
	protected Textbox	                   moduleType;	                                                  // autowired
	protected Radio	                       fromApproved;
	protected Radio	                       fromWorkFlow;
	protected Row	                       workFlowFrom;

	private transient boolean	           approvedList	    = false;
	private List<ValueLabel>           listAccPurposeType = PennantStaticListUtil.getAccountPurpose();

	// checkRights
	protected Button btnHelp; 										// auto wired
	protected Button button_AccountTypeList_NewAccountType; 		// auto wired
	protected Button button_AccountTypeList_AccountTypeSearchDialog;// auto wired
	protected Button button_AccountTypeList_PrintList;		 		// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AccountType> searchObj;
	
	private transient AccountTypeService accountTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public AccountTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountTypeList(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("AccountType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountType");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_acType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acPurpose.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_acPurpose.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_internalAc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_internalAc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_acTypeIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_acTypeIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_isCustSysAccount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_isCustSysAccount.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_AccountTypeSearch_RecordStatus.setVisible(false);
			this.label_AccountTypeSearch_RecordType.setVisible(false);
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_AccountTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxAccountType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingAccountTypeList.setPageSize(getListRows());
		this.pagingAccountTypeList.setDetailed(true);

		this.listheader_AcType.setSortAscending(new FieldComparator("acType", true));
		this.listheader_AcType.setSortDescending(new FieldComparator("acType", false));
		this.listheader_AcTypeDesc.setSortAscending(new FieldComparator("acTypeDesc", true));
		this.listheader_AcTypeDesc.setSortDescending(new FieldComparator("acTypeDesc", false));
		this.listheader_AcPurpose.setSortAscending(new FieldComparator("acPurpose", true));
		this.listheader_AcPurpose.setSortDescending(new FieldComparator("acPurpose", false));
		this.listheader_IsInternalAc.setSortAscending(new FieldComparator("internalAc", true));
		this.listheader_IsInternalAc.setSortDescending(new FieldComparator("internalAc", false));
		this.listheader_IsCustSysAccount.setSortAscending(new FieldComparator("custSysAc", true));
		this.listheader_IsCustSysAccount.setSortDescending(new FieldComparator("custSysAc", false));
		this.listheader_AcHead.setSortAscending(new FieldComparator("acHeadCode", true));
		this.listheader_AcHead.setSortDescending(new FieldComparator("acHeadCode", false));
		this.listheader_AcTypeIsActive.setSortAscending(new FieldComparator("acTypeIsActive", true));
		this.listheader_AcTypeIsActive.setSortDescending(new FieldComparator("acTypeIsActive", false));
		
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
		this.listBoxAccountType.setItemRenderer(new AccountTypeListModelItemRenderer());
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_AccountTypeList_NewAccountType.setVisible(false);
			this.button_AccountTypeList_AccountTypeSearchDialog.setVisible(false);
			this.button_AccountTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		setListAccountPurpose();
		logger.debug("Leaving"+event.toString());
	}
	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setListAccountPurpose() {
		logger.debug("Entering ");
		Comboitem comboitem;
		for (int i = 0; i < listAccPurposeType.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(listAccPurposeType.get(i).getLabel());
			comboitem.setValue(listAccPurposeType.get(i).getValue());
			this.acPurpose.appendChild(comboitem);
		}
		this.acPurpose.setSelectedIndex(0);
		logger.debug("Leaving ");
	}
	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AccountTypeList");
		
		this.button_AccountTypeList_NewAccountType.setVisible(getUserWorkspace()
				.isAllowed("button_AccountTypeList_NewAccountType"));
		this.button_AccountTypeList_AccountTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_AccountTypeList_AccountTypeFindDialog"));
		this.button_AccountTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_AccountTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.accounttype.model.
	 * AccountTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAccountTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// get the selected AccountType object
		final Listitem item = this.listBoxAccountType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AccountType aAccountType = (AccountType) item.getAttribute("data");
			final AccountType accountType = getAccountTypeService().getAccountTypeById(
					aAccountType.getId());
			if(accountType==null){

				String[] valueParm= new String[1];
				String[] errParm= new String[1];

				valueParm[0] =  aAccountType.getAcType();
				errParm[0] = PennantJavaUtil.getLabel("label_AcType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm),
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND AcType='"+ accountType.getAcType()+
				"' AND version=" + accountType.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "AccountType",
							whereCond, accountType.getTaskId(), accountType.getNextTaskId());
					if (userAcces){
						showDetailView(accountType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(accountType);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the AccountType dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountTypeList_NewAccountType(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		// create a new AccountType object, We GET it from the back end.
		final AccountType aAccountType = getAccountTypeService().getNewAccountType();
		if(event.getData()!=null){
			AccountType type=(AccountType)event.getData();
			setObjectData(aAccountType,type);
		}
		showDetailView(aAccountType);
		logger.debug("Leaving"+event.toString());
	}
	
	private AccountType setObjectData(AccountType aAccountType,AccountType type){
		logger.debug("Entering");
		aAccountType.setAcPurpose(type.getAcPurpose());
		aAccountType.setInternalAc(type.isInternalAc());
		aAccountType.setCustSysAc(type.isCustSysAc());
		aAccountType.setAcTypeIsActive(type.isAcTypeIsActive());
		logger.debug("Leaving");
		return aAccountType;
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param AccountType (aAccountType)
	 * @throws Exception
	 */
	private void showDetailView(AccountType aAccountType) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aAccountType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aAccountType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accountType", aAccountType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the AccountTypeListbox from the
		 * dialog when we do a delete, edit or insert a AccountType.
		 */
		map.put("accountTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/AccountType/AccountTypeDialog.zul",null,map);
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
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_AccountTypeList);
		logger.debug("Leaving"+event.toString());
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
		logger.debug("Entering"+event.toString());
		this.sortOperator_acType.setSelectedIndex(0);
		this.acType.setValue("");
		this.sortOperator_acTypeDesc.setSelectedIndex(0);
		this.acTypeDesc.setValue("");
		this.sortOperator_acPurpose.setSelectedIndex(0);
		this.acPurpose.setSelectedIndex(0);
		this.sortOperator_acTypeIsActive.setSelectedIndex(0);
		this.acTypeIsActive.setValue("");
		this.sortOperator_internalAc.setSelectedIndex(0);
		this.internalAc.setValue("");
		this.sortOperator_isCustSysAccount.setSelectedIndex(0);
		this.isCustSysAccount.setValue("");
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		/*this.pagingAccountTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AccountTypeList, event);
		this.window_AccountTypeList.invalidate();*/
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * call the AccountType dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountTypeList_AccountTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doSearch();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When the accountType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_AccountTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("AccountType", getSearchObj(),this.pagingAccountTypeList.getTotalSize()+1);
		logger.debug("Leaving"+event.toString());
	}
	public void doSearch(){
		logger.debug("Entering");
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<AccountType>(AccountType.class,getListRows());
		this.searchObj.addSort("AcType", false);
		this.searchObj.addTabelName("RMTAccountTypes_View");
		if (isWorkFlowEnabled()) {

			if (isFirstTask() && this.moduleType == null) {
				button_AccountTypeList_NewAccountType.setVisible(true);
			} else {
				button_AccountTypeList_NewAccountType.setVisible(false);
			}

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;
			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					this.searchObj.addTabelName("RMTAccountTypes_TView");
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("RMTAccountTypes_AView");
		}
		// System Internal A/c code
		if (!StringUtils.trimToEmpty(this.acType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_acType.getSelectedItem(), this.acType.getValue(), "acType");
		}
		
		// System Internal A/c Name
		if (!StringUtils.trimToEmpty(this.acTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_acTypeDesc.getSelectedItem(), this.acTypeDesc.getValue(), "acTypeDesc");
		}
		if (null !=this.acPurpose.getSelectedItem() && !StringUtils.trimToEmpty(this.acPurpose.getSelectedItem().getValue().toString()).equals("")){
			searchObj = getSearchFilter(searchObj, this.sortOperator_acPurpose.getSelectedItem(), this.acPurpose.getSelectedItem().getValue().toString(), "acPurpose");
		}
		 if (internalAc.isChecked()) { 
			 searchObj = getSearchFilter(searchObj,this.sortOperator_internalAc.getSelectedItem(), 1,"internalAc");
			 } else { 
				 searchObj = getSearchFilter(searchObj,this.sortOperator_internalAc.getSelectedItem(), 0,"internalAc"); 
				 }
		 if (acTypeIsActive.isChecked()) { 
			 searchObj = getSearchFilter(searchObj,this.sortOperator_acTypeIsActive.getSelectedItem(), 1,"acTypeIsActive"); 
			 } else { 
				 searchObj = getSearchFilter(searchObj,this.sortOperator_acTypeIsActive.getSelectedItem(), 0,"acTypeIsActive"); 
				 }
		 if (isCustSysAccount.isChecked()) { 
			 searchObj = getSearchFilter(searchObj,this.sortOperator_isCustSysAccount.getSelectedItem(), 1,"custSysAc"); 
			 } else { 
				 searchObj = getSearchFilter(searchObj,this.sortOperator_isCustSysAccount.getSelectedItem(), 0,"custSysAc"); 
				 }
		
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordStatus.getSelectedItem(), this.recordStatus.getValue(), "RecordStatus");
		}
		// Record Type
		if (this.recordType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_recordType.getSelectedItem(), this.recordType.getSelectedItem().getValue().toString(), "RecordType");
		}
		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxAccountType,this.pagingAccountTypeList);
		logger.debug("Leaving" );
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAccountTypeService(AccountTypeService accountTypeService) {
		this.accountTypeService = accountTypeService;
	}
	public AccountTypeService getAccountTypeService() {
		return this.accountTypeService;
	}

	public JdbcSearchObject<AccountType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<AccountType> searchObj) {
		this.searchObj = searchObj;
	}
}