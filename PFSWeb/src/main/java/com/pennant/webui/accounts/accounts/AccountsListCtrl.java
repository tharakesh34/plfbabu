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
 * FileName    		:  AccountsListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.accounts.accounts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.webui.accounts.accounts.model.AccountsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Accounts/Accounts/AcountsList.zul file.
 */
public class AccountsListCtrl extends GFCBaseListCtrl<Accounts> {
	private static final long serialVersionUID = -7795679541515607779L;

	private static final Logger logger = Logger.getLogger(AccountsListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window       window_AccountsList;            // autoWired
	protected Borderlayout borderLayout_AcountsList;      // autoWired
	protected Paging       pagingAcountsList;             // autoWired
	protected Listbox      listBoxAcounts;                // autoWired
	protected Checkbox     internalAc;
	protected Textbox      recordType;
	// List headers
	protected Listheader   listheader_AccountId;           // autoWired
	protected Listheader   listheader_AcCcy;               // autoWired
	protected Listheader   listheader_AcType;              // autoWired
	protected Listheader   listheader_AcBranch;            // autoWired
	protected Listheader   listheader_AcCustId;            // autoWired
	protected Listheader   listheader_AcFullName;          // autoWired
	protected Listheader   listheader_AcShortName;         // autoWired
	protected Listheader   listheader_AcPurpose;           // autoWired
	protected Listheader   listheader_InternalAc;          // autoWired
	protected Listheader   listheader_CustSysAc;           // autoWired
	protected Listheader   listheader_AcActive;            // autoWired
	protected Listheader   listheader_AcBlocked;           // autoWired
	protected Listheader   listheader_AcClosed;            // autoWired
	protected Listheader   listheader_HostAcNumber;        // autoWired
	
	


	// checkRights
	protected Button btnHelp;                              // autoWired
	protected Button button_AccountsList_NewAccounts;        // autoWired
	protected Button button_AccountsList_PrintList;         // autoWired
	protected Button button_AccountsList_AccountsSearchDialog;// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Accounts> searchObj;

	private transient AccountsService accountsService;

	/**
	 * default constructor.<br>
	 */
	public AccountsListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		moduleCode = "Accounts";
	}

	public void onCreate$window_AccountsList(Event event) throws Exception {
		logger.debug("Entering");

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_AcountsList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingAcountsList.setPageSize(getListRows());
		this.pagingAcountsList.setDetailed(true);

		this.listheader_AccountId.setSortAscending(new FieldComparator("accountId", true));
		this.listheader_AccountId.setSortDescending(new FieldComparator("accountId", false));
		this.listheader_AcCcy.setSortAscending(new FieldComparator("acCcy", true));
		this.listheader_AcCcy.setSortDescending(new FieldComparator("acCcy", false));
		this.listheader_AcType.setSortAscending(new FieldComparator("acType", true));
		this.listheader_AcType.setSortDescending(new FieldComparator("acType", false));
		this.listheader_AcBranch.setSortAscending(new FieldComparator("acBranch", true));
		this.listheader_AcBranch.setSortDescending(new FieldComparator("acBranch", false));
		this.listheader_AcCustId.setSortAscending(new FieldComparator("acCustId", true));
		this.listheader_AcCustId.setSortDescending(new FieldComparator("acCustId", false));
		this.listheader_AcFullName.setSortAscending(new FieldComparator("acFullName", true));
		this.listheader_AcFullName.setSortDescending(new FieldComparator("acFullName", false));
		this.listheader_AcShortName.setSortAscending(new FieldComparator("acShortName", true));
		this.listheader_AcShortName.setSortDescending(new FieldComparator("acShortName", false));
		this.listheader_AcPurpose.setSortAscending(new FieldComparator("acPurpose", true));
		this.listheader_AcPurpose.setSortDescending(new FieldComparator("acPurpose", false));
		this.listheader_InternalAc.setSortAscending(new FieldComparator("internalAc", true));
		this.listheader_InternalAc.setSortDescending(new FieldComparator("internalAc", false));
		this.listheader_CustSysAc.setSortAscending(new FieldComparator("custSysAc", true));
		this.listheader_CustSysAc.setSortDescending(new FieldComparator("custSysAc", false));
		this.listheader_AcActive.setSortAscending(new FieldComparator("acInactive", true));
		this.listheader_AcActive.setSortDescending(new FieldComparator("acInactive", false));
		this.listheader_AcBlocked.setSortAscending(new FieldComparator("acBlocked", true));
		this.listheader_AcBlocked.setSortDescending(new FieldComparator("acBlocked", false));
		this.listheader_AcClosed.setSortAscending(new FieldComparator("acClosed", true));
		this.listheader_AcClosed.setSortDescending(new FieldComparator("acClosed", false));
		this.listheader_HostAcNumber.setSortAscending(new FieldComparator("hostAcNumber", true));
		this.listheader_HostAcNumber.setSortDescending(new FieldComparator("hostAcNumber", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<Accounts>(Accounts.class, getListRows());
		this.searchObj.addSort("accountId", false);
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("Accounts_View");
			if (isFirstTask()) {
				button_AccountsList_NewAccounts.setVisible(true);
			} else {
				button_AccountsList_NewAccounts.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("Accounts_AView");
		}
		if (internalAc.isChecked()) {
			searchObj.addFilter(new Filter("internalAc", "1", Filter.OP_EQUAL));
		} else {
			searchObj.addFilter(new Filter("internalAc", "0", Filter.OP_EQUAL));
		}

		if ("NEW".equals(StringUtils.trimToEmpty(recordType.getValue()))) {
			searchObj.addFilter(new Filter("recordType", recordType.getValue(), Filter.OP_EQUAL));
		} else {
			this.button_AccountsList_NewAccounts.setVisible(false);
			searchObj.addFilterOr(new Filter("recordType", "", Filter.OP_EQUAL),
					new Filter("recordType", recordType.getValue(), Filter.OP_EQUAL));
			searchObj.addFilter(new Filter("acCloseDate", null, Filter.OP_NULL));
		}

		setSearchObj(this.searchObj);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxAcounts, this.pagingAcountsList);
		// set the itemRenderer
		this.listBoxAcounts.setItemRenderer(new AccountsListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("AccountsList");

		this.button_AccountsList_NewAccounts.setVisible(getUserWorkspace().isAllowed("button_AccountsList_NewAccounts"));
		this.button_AccountsList_AccountsSearchDialog.setVisible(getUserWorkspace().isAllowed("button_AccountsList_AccountsFindDialog"));
		this.button_AccountsList_PrintList.setVisible(getUserWorkspace().isAllowed("button_AccountsList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.accounts.acounts.model.AcountsListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onAccountsItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected Accounts object
		final Listitem item = this.listBoxAcounts.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Accounts aAccounts = (Accounts) item.getAttribute("data");
			final Accounts acounts = getAccountsService().getAccountsById(aAccounts.getAccountId());

			if(acounts==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aAccounts.getAccountId();
				errParm[0]=PennantJavaUtil.getLabel("label_AccointId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND AccountId='"+ acounts.getAccountId()+"' AND version=" + acounts.getVersion()+" ";

					boolean userAcces =  validateUserAccess(acounts.getWorkflowId()
							,getUserWorkspace().getLoggedInUser().getLoginUsrID(), "Accounts"
							, whereCond, acounts.getTaskId(), acounts.getNextTaskId());
					if (userAcces){
						doShowDialogPage(acounts);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					doShowDialogPage(acounts);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Accounts dialog with a new empty entry. <br>
	 */
	public void onClick$button_AccountsList_NewAccounts(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// create a new Accounts object, We GET it from the back end.
		final Accounts aAccounts = getAccountsService().getNewAccounts();
		aAccounts.setInternalAc(internalAc.isChecked());
		doShowDialogPage(aAccounts);
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aAccounts
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Accounts aAccounts) {
		logger.debug("Entering");
		
		if (aAccounts.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aAccounts.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> aruments = getDefaultArguments();
		aruments.put("acounts", aAccounts);
		aruments.put("acountsListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			if(aAccounts.isNew()){
				Executions.createComponents("/WEB-INF/pages/Account/Accounts/SelectAccountDetailsDialog.zul",null, aruments);
			}else{
				Executions.createComponents("/WEB-INF/pages/Account/Accounts/AccountsDialog.zul",null, aruments);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_AccountsList);
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		this.pagingAcountsList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AccountsList, event);
		this.window_AccountsList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the Accounts dialog
	 */

	public void onClick$button_AccountsList_AccountsSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		logger.debug(event.toString());
		/*
		 * we can call our AcountsDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Accounts. For handed over
		 * these parameter only a Map is accepted. So we put the Accounts object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("acountsCtrl", this);
		map.put("searchObject", getSearchObj());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Account/Accounts/AccountsSearchDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the accounts print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_AccountsList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("Accounts", getSearchObj(),this.pagingAcountsList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setAccountsService(AccountsService acountsService) {
		this.accountsService = acountsService;
	}

	public AccountsService getAccountsService() {
		return this.accountsService;
	}

	public JdbcSearchObject<Accounts> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Accounts> searchObj) {
		this.searchObj = searchObj;
	}
}