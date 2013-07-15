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
 * FileName    		:  AccountEngineRuleListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountenginerule;

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
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.service.rmtmasters.AccountEngineRuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.accountenginerule.model.AccountEngineRuleListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/AccountEngineRule/AccountEngineRuleList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AccountEngineRuleListCtrl extends
		GFCBaseListCtrl<AccountEngineRule> implements Serializable {

	private static final long serialVersionUID = 5308110948749597564L;
	private final static Logger logger = Logger.getLogger(AccountEngineRuleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	
	protected Window 		window_AccountEngineRuleList; 		// auto wired
	protected Borderlayout 	borderLayout_AccountEngineRuleList; // auto wired
	protected Paging 		pagingAccountEngineRuleList; 		// auto wired
	protected Listbox 		listBoxAccountEngineRule; 			// auto wired

	// List headers
	protected Listheader listheader_AEEvent; 		// auto wired
	protected Listheader listheader_AERule; 		// auto wired
	protected Listheader listheader_AERuleDesc; 	// auto wired
	protected Listheader listheader_AEIsSysDefault; // auto wired
	protected Listheader listheader_RecordStatus; 	// auto wired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// auto wired
	protected Button button_AccountEngineRuleList_NewAccountEngineRule; 		// auto wired
	protected Button button_AccountEngineRuleList_AccountEngineRuleSearchDialog;// auto wired
	protected Button button_AccountEngineRuleList_PrintList; 					// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AccountEngineRule> searchObj;
	private transient AccountEngineRuleService accountEngineRuleService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public AccountEngineRuleListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineRule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountEngineRuleList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("AccountEngineRule");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AccountEngineRule");
			
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
		this.borderLayout_AccountEngineRuleList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingAccountEngineRuleList.setPageSize(getListRows());
		this.pagingAccountEngineRuleList.setDetailed(true);

		this.listheader_AEEvent.setSortAscending(new FieldComparator("aEEvent", true));
		this.listheader_AEEvent.setSortDescending(new FieldComparator("aEEvent", false));
		this.listheader_AERule.setSortAscending(new FieldComparator("aERule", true));
		this.listheader_AERule.setSortDescending(new FieldComparator("aERule", false));
		this.listheader_AERuleDesc.setSortAscending(new FieldComparator("aERuleDesc", true));
		this.listheader_AERuleDesc.setSortDescending(new FieldComparator("aERuleDesc", false));
		this.listheader_AEIsSysDefault.setSortAscending(new FieldComparator("aEIsSysDefault", true));
		this.listheader_AEIsSysDefault.setSortDescending(new FieldComparator("aEIsSysDefault", false));
		
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
		this.searchObj = new JdbcSearchObject<AccountEngineRule>(AccountEngineRule.class, getListRows());
		this.searchObj.addSort("AEEvent", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTAERules_View");
			if (isFirstTask()) {
				button_AccountEngineRuleList_NewAccountEngineRule.setVisible(true);
			} else {
				button_AccountEngineRuleList_NewAccountEngineRule.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTAERules_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_AccountEngineRuleList_NewAccountEngineRule.setVisible(false);
			this.button_AccountEngineRuleList_AccountEngineRuleSearchDialog.setVisible(false);
			this.button_AccountEngineRuleList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxAccountEngineRule,this.pagingAccountEngineRuleList);
			// set the itemRenderer
			this.listBoxAccountEngineRule.setItemRenderer(new AccountEngineRuleListModelItemRenderer());
		}	
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().alocateAuthorities("AccountEngineRuleList");
		
		this.button_AccountEngineRuleList_NewAccountEngineRule.setVisible(
				getUserWorkspace().isAllowed("button_AccountEngineRuleList_NewAccountEngineRule"));
		this.button_AccountEngineRuleList_AccountEngineRuleSearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_AccountEngineRuleList_AccountEngineRuleFindDialog"));
		this.button_AccountEngineRuleList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_AccountEngineRuleList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.accountenginerule.model.
	 * AccountEngineRuleListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAccountEngineRuleItemDoubleClicked(Event event) throws Exception {
		logger.debug("Leaving" +event.toString());

		// get the selected AccountEngineRule object
		final Listitem item = this.listBoxAccountEngineRule.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AccountEngineRule aAccountEngineRule = (AccountEngineRule) item
					.getAttribute("data");
			final AccountEngineRule accountEngineRule = getAccountEngineRuleService()
					.getAccountEngineRuleById(aAccountEngineRule.getId());
			if(accountEngineRule==null){

				String[] valueParm = new String[2];
				String[] errParm= new String[2];

				valueParm[0] = aAccountEngineRule.getAEEvent();
				valueParm[1] = aAccountEngineRule.getAERule();

				errParm[0] = PennantJavaUtil.getLabel("label_AEEvent") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_AERule") + ":"+valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",
						errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond = " AND AERuleId='"+ accountEngineRule.getaERuleId() +
										"' AND version="+ accountEngineRule.getVersion() + " ";

				if(isWorkFlowEnabled()){
					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"AccountEngineRule",whereCond, accountEngineRule.getTaskId(),
							accountEngineRule.getNextTaskId());
					if (userAcces){
						showDetailView(accountEngineRule);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(accountEngineRule);
				}
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the AccountEngineRule dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountEngineRuleList_NewAccountEngineRule(Event event) throws Exception {
		 logger.debug("Entering" +event.toString());
		// create a new AccountEngineRule object, We GET it from the back end.
		final AccountEngineRule aAccountEngineRule = getAccountEngineRuleService().getNewAccountEngineRule();
		showDetailView(aAccountEngineRule);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param AccountEngineRule (aAccountEngineRule)
	 * @throws Exception
	 */
	private void showDetailView(AccountEngineRule aAccountEngineRule) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aAccountEngineRule.getWorkflowId()==0 && isWorkFlowEnabled()){
			aAccountEngineRule.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accountEngineRule", aAccountEngineRule);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the AccountEngineRuleListbox from the
		 * dialog when we do a delete, edit or insert a AccountEngineRule.
		 */
		map.put("accountEngineRuleListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/AccountEngineRule/AccountEngineRuleDialog.zul",null,map);
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
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_AccountEngineRuleList);
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		this.pagingAccountEngineRuleList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AccountEngineRuleList, event);
		this.window_AccountEngineRuleList.invalidate();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Method for calling the AccountEngineRule dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountEngineRuleList_AccountEngineRuleSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		/*
		 * we can call our AccountEngineRuleDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected AccountEngineRule. For handed over
		 * these parameter only a Map is accepted. So we put the AccountEngineRule object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("accountEngineRuleCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/AccountEngineRule/AccountEngineRuleSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the accountEngineRule print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_AccountEngineRuleList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("AccountEngineRule", getSearchObj());
		logger.debug("Leaving" +event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setAccountEngineRuleService(AccountEngineRuleService accountEngineRuleService) {
		this.accountEngineRuleService = accountEngineRuleService;
	}
	public AccountEngineRuleService getAccountEngineRuleService() {
		return this.accountEngineRuleService;
	}

	public JdbcSearchObject<AccountEngineRule> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<AccountEngineRule> searchObj) {
		this.searchObj = searchObj;
	}
}