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
 * FileName    		:  NotificationsListCtrl.java                                                   * 	  
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
package com.pennant.webui.mail.notifications;


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
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Iframe;
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
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.mail.notifications.model.NotificationsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Notifications/NotificationsList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class NotificationsListCtrl extends GFCBaseListCtrl<Notifications> implements Serializable {

	private static final long serialVersionUID = 5327118548986437717L;
	private final static Logger logger = Logger.getLogger(NotificationsListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 			window_NotificationsList; 			// autoWired
	protected Borderlayout 		borderLayout_NotificationsList; 		// autoWired
	protected Paging 			pagingNotificationsList; 			// autoWired
	protected Listbox 			listBoxNotifications; 				// autoWired

	// List headers
	protected Listheader listheader_NotificationsRuleCode; 				// autoWired
	protected Listheader listheader_NotificationsRuleModule; 			// autoWired
	protected Listheader listheader_NotificationsRuleCodeDesc; 				// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_NotificationsList_NewNotifications; 			// autoWired
	protected Button button_NotificationsList_NotificationsSearchDialog;  // autoWired
	protected Button button_NotificationsList_PrintList; 			// autoWired

	// Filtering Fields for Check List rule
	
	protected Textbox ruleCode; 											// autowired
	protected Listbox sortOperator_ruleCode; 								// autowired
	protected Textbox ruleCodeDesc; 										// autowired
	protected Listbox sortOperator_ruleCodeDesc; 								// autowired
	protected Listbox sortOperator_recordStatus; 							// autowired
	protected Textbox recordStatus; 										// autowired
	protected Listbox sortOperator_recordType; 								// autowired
	protected Listbox recordType; 											// autowired
	
	protected Listbox sortOperator_ruleModule;                              // autowired
	protected Textbox ruleModule;                                      // autowired
	
	protected Row row_AlwWorkflow; 							// autoWired
	
	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false;
	
	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Notifications> searchObj;

	private transient NotificationsService notificationsService;
	private transient WorkFlowDetails workFlowDetails=null;
	Iframe report;

	/**
	 * default constructor.<br>
	 */
	public NotificationsListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected NotificationsCode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_NotificationsList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Notifications");
		boolean wfAvailable=true;
	

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Notifications");

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

		this.sortOperator_ruleCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ruleCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_ruleCodeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ruleCodeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_ruleModule.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_ruleModule.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.row_AlwWorkflow.setVisible(false);
		}
		
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		this.borderLayout_NotificationsList.setHeight(getBorderLayoutHeight());
		this.listBoxNotifications.setHeight(getListBoxHeight(searchGrid.getRows().getChildren().size())); 
		// set the paging params
		this.pagingNotificationsList.setPageSize(getListRows());
		this.pagingNotificationsList.setDetailed(true);

		this.listheader_NotificationsRuleModule.setSortAscending(new FieldComparator("ruleModule", true));
		this.listheader_NotificationsRuleModule.setSortDescending(new FieldComparator("ruleModule", false));
		this.listheader_NotificationsRuleCode.setSortAscending(new FieldComparator("ruleCode", true));
		this.listheader_NotificationsRuleCode.setSortDescending(new FieldComparator("ruleCode", false));
		this.listheader_NotificationsRuleCodeDesc.setSortAscending(new FieldComparator("ruleCodeDesc", true));
		this.listheader_NotificationsRuleCodeDesc.setSortDescending(new FieldComparator("ruleCodeDesc", false));

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
		this.listBoxNotifications.setItemRenderer(new NotificationsListModelItemRenderer());
		
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_NotificationsList_NewNotifications.setVisible(false);
			this.button_NotificationsList_NotificationsSearchDialog.setVisible(false);
			this.button_NotificationsList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("NotificationsList");

		this.button_NotificationsList_NewNotifications.setVisible(getUserWorkspace()
				.isAllowed("button_NotificationsList_NewNotifications"));
		this.button_NotificationsList_NotificationsSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_NotificationsList_NotificationsFindDialog"));
		this.button_NotificationsList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_NotificationsList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.notifications.model.
	 * NotificationsListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onNotificationsItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected Notifications object
		final Listitem item = this.listBoxNotifications.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Notifications aNotifications = (Notifications) item.getAttribute("data");
			final Notifications notifications = getNotificationsService().getNotificationsById(aNotifications.getRuleCode());

			if (notifications == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aNotifications.getRuleCode();
				valueParm[1] = aNotifications.getRuleCodeDesc();

				errParm[0] = PennantJavaUtil.getLabel("label_Notifications_RuleCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_Notifications_RuleModule") + ":" + valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND RuleCode='" + notifications.getRuleCode() 
						+ "' AND version=" + notifications.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Notifications", whereCond, notifications.getTaskId(), notifications.getNextTaskId());

					if (userAcces) {
						showDetailView(notifications);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(notifications);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Notifications dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_NotificationsList_NewNotifications(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Notifications object, We GET it from the backEnd.
		final Notifications aNotifications = getNotificationsService().getNewNotifications();
		showDetailView(aNotifications);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Notifications
	 *            (aNotifications)
	 * @throws Exception
	 */
	private void showDetailView(Notifications aNotifications) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aNotifications.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aNotifications.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notifications", aNotifications);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the NotificationsListbox from the
		 * dialog when we do a delete, edit or insert a Notifications.
		 */
		map.put("notificationsListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/Mail/Notifications/NotificationsDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_NotificationsList);
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
		logger.debug("Entering" +event.toString());
	    this.sortOperator_ruleCode.setSelectedIndex(0);
        this.ruleCode.setValue("");
        this.sortOperator_ruleCodeDesc.setSelectedIndex(0);
        this.ruleCodeDesc.setValue("");
        
        this.sortOperator_ruleModule.setSelectedIndex(0);
   	    this.ruleModule.setValue("");
        
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		this.pagingNotificationsList.setActivePage(0);
		doSearch();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * call the Notifications dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_NotificationsList_NotificationsSearchDialog(Event event) throws Exception {
	logger.debug("Entering" +event.toString());
        doSearch();
	logger.debug("Leaving" +event.toString());
}

	/**
	 * When the notifications print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_NotificationsList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());	
		PTListReportUtils reportUtils = new PTListReportUtils("Notifications", getSearchObj(),this.pagingNotificationsList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	
	public void doSearch(){
		 logger.debug("Entering");
			// ++ create the searchObject and init sorting ++//
			this.searchObj = new JdbcSearchObject<Notifications>(Notifications.class,getListRows());

			// Defualt Sort on the table
			this.searchObj.addSort("ruleCode", false);

			// Workflow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("Notifications_View");

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
				this.searchObj.addTabelName("Notifications_AView");
			}else{
				this.searchObj.addTabelName("Notifications_View");
			}
			
			// Rule Code
			if (!StringUtils.trimToEmpty(this.ruleCode.getValue()).equals("")) {
				searchObj = getSearchFilter(searchObj,this.sortOperator_ruleCode.getSelectedItem(),this.ruleCode.getValue(), "ruleCode");
			}
			
			// Rule Code Description
			if (!StringUtils.trimToEmpty(this.ruleCodeDesc.getValue()).equals("")) {
				searchObj = getSearchFilter(searchObj,this.sortOperator_ruleCodeDesc.getSelectedItem(),this.ruleCodeDesc.getValue(), "ruleCodeDesc");
			}

			// Rule Module
			if (!StringUtils.trimToEmpty(this.ruleModule.getValue()).equals("")) {
				searchObj = getSearchFilter(searchObj,this.sortOperator_ruleModule.getSelectedItem(),this.ruleModule.getValue(), "ruleModule");
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
			getPagedListWrapper().init(this.searchObj, this.listBoxNotifications,this.pagingNotificationsList);
			logger.debug("Leaving");
	}
	
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}
	public NotificationsService getNotificationsService() {
		return this.notificationsService;
	}

	public JdbcSearchObject<Notifications> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Notifications> searchObj) {
		this.searchObj = searchObj;
	}

}