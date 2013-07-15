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

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.accounttype.model.AccountTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

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
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_AccountTypeList.setHeight(getBorderLayoutHeight());

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
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<AccountType>(AccountType.class,getListRows());
		this.searchObj.addSort("AcType", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTAccountTypes_View");
			if (isFirstTask()) {
				button_AccountTypeList_NewAccountType.setVisible(true);
			} else {
				button_AccountTypeList_NewAccountType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", 
					getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTAccountTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_AccountTypeList_NewAccountType.setVisible(false);
			this.button_AccountTypeList_AccountTypeSearchDialog.setVisible(false);
			this.button_AccountTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxAccountType,this.pagingAccountTypeList);
			// set the itemRenderer
			this.listBoxAccountType.setItemRenderer(
					new AccountTypeListModelItemRenderer());
		}
		logger.debug("Leaving"+event.toString());
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
		this.pagingAccountTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AccountTypeList, event);
		this.window_AccountTypeList.invalidate();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * call the AccountType dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountTypeList_AccountTypeSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		/*
		 * we can call our AccountTypeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected AccountType. For handed over
		 * these parameter only a Map is accepted. So we put the AccountType object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accountTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/AccountType/AccountTypeSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
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