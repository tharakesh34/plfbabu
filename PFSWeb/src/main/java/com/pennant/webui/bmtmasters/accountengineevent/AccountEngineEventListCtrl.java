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
 * FileName    		:  AccountEngineEventListCtrl.java                                                   * 	  
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
package com.pennant.webui.bmtmasters.accountengineevent;

import java.util.Map;

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
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.service.bmtmasters.AccountEngineEventService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.bmtmasters.accountengineevent.model.AccountEngineEventListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/AccountEngineEvent/AccountEngineEventList.zul file.
 */
public class AccountEngineEventListCtrl extends GFCBaseListCtrl<AccountEngineEvent> {
	private static final long serialVersionUID = -3818155098220806436L;
	private static final Logger logger = Logger.getLogger(AccountEngineEventListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * Component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_AccountEngineEventList; 			// autoWired
	protected Borderlayout 	borderLayout_AccountEngineEventList; 	// autoWired
	protected Paging 		pagingAccountEngineEventList; 			// autoWired
	protected Listbox 		listBoxAccountEngineEvent; 				// autoWired

	// List headers
	protected Listheader listheader_AEEventCode; 		// autoWired
	protected Listheader listheader_AEEventCodeDesc; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;			// autoWired

	// checkRights
	protected Button btnHelp; 														// autoWired
	protected Button button_AccountEngineEventList_NewAccountEngineEvent; 			// autoWired
	protected Button button_AccountEngineEventList_AccountEngineEventSearchDialog; 	// autoWired
	protected Button button_AccountEngineEventList_PrintList;				 		// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AccountEngineEvent> searchObj;

	private transient AccountEngineEventService accountEngineEventService;

	/**
	 * default constructor.<br>
	 */
	public AccountEngineEventListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "AccountEngineEvent";
	}

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineEventCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountEngineEventList(Event event)	throws Exception {
		logger.debug("Entering");
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		this.borderLayout_AccountEngineEventList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingAccountEngineEventList.setPageSize(getListRows());
		this.pagingAccountEngineEventList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_AEEventCode.setSortAscending(new FieldComparator("aEEventCode", true));
		this.listheader_AEEventCode.setSortDescending(new FieldComparator("aEEventCode", false));
		this.listheader_AEEventCodeDesc.setSortAscending(new FieldComparator("aEEventCodeDesc", true));
		this.listheader_AEEventCodeDesc.setSortDescending(new FieldComparator("aEEventCodeDesc", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<AccountEngineEvent>(AccountEngineEvent.class, getListRows());
		this.searchObj.addSort("AEEventCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTAEEvents_View");
			if (isFirstTask()) {
				button_AccountEngineEventList_NewAccountEngineEvent.setVisible(true);
			} else {
				button_AccountEngineEventList_NewAccountEngineEvent.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTAEEvents_AView");
		}

		setSearchObj(this.searchObj);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxAccountEngineEvent, this.pagingAccountEngineEventList);
		// set the itemRenderer
		this.listBoxAccountEngineEvent.setItemRenderer(new AccountEngineEventListModelItemRenderer());
					
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("AccountEngineEventList");

		this.button_AccountEngineEventList_NewAccountEngineEvent.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineEventList_NewAccountEngineEvent"));
		this.button_AccountEngineEventList_AccountEngineEventSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineEventList_AccountEngineEventFindDialog"));
		this.button_AccountEngineEventList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineEventList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.accountengineevent.model.
	 * AccountEngineEventListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAccountEngineEventItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected AccountEngineEvent object
		final Listitem item = this.listBoxAccountEngineEvent.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AccountEngineEvent aAccountEngineEvent = (AccountEngineEvent) item.getAttribute("data");
			final AccountEngineEvent accountEngineEvent = getAccountEngineEventService()
			.getAccountEngineEventById(aAccountEngineEvent.getId());

			if (accountEngineEvent == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aAccountEngineEvent.getAEEventCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_AEEventCode") + ":" + aAccountEngineEvent.getAEEventCode();

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {
				String whereCond = " AND AEEventCode='"	+ accountEngineEvent.getAEEventCode()
				+ "' AND version=" + accountEngineEvent.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(accountEngineEvent.getWorkflowId(), getUserWorkspace().getLoggedInUser().getUserId(),
							"AccountEngineEvent", whereCond, accountEngineEvent.getTaskId(), accountEngineEvent.getNextTaskId());
					if (userAcces) {
						showDetailView(accountEngineEvent);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(accountEngineEvent);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the AccountEngineEvent dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountEngineEventList_NewAccountEngineEvent(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new AccountEngineEvent object, We GET it from the backEnd.
		final AccountEngineEvent aAccountEngineEvent = getAccountEngineEventService().getNewAccountEngineEvent();
		showDetailView(aAccountEngineEvent);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param AccountEngineEvent
	 *            (aAccountEngineEvent)
	 * @throws Exception
	 */
	private void showDetailView(AccountEngineEvent aAccountEngineEvent)	throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aAccountEngineEvent.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aAccountEngineEvent.setWorkflowId(getWorkFlowId());
		}
		Map<String, Object> map = getDefaultArguments();
		map.put("accountEngineEvent", aAccountEngineEvent);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the AccountEngineEventListbox from
		 * the dialog when we do a delete, edit or insert a AccountEngineEvent.
		 */
		map.put("accountEngineEventListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/BMTMasters/AccountEngineEvent/AccountEngineEventDialog.zul", null, map);
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
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_AccountEngineEventList);
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
		this.pagingAccountEngineEventList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AccountEngineEventList, event);
		this.window_AccountEngineEventList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the AccountEngineEvent dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_AccountEngineEventList_AccountEngineEventSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our AccountEngineEventDialog ZUL-file with parameters. So
		 * we can call them with a object of the selected AccountEngineEvent.
		 * For handed over these parameter only a Map is accepted. So we put the
		 * AccountEngineEvent object in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("accountEngineEventCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/BMTMasters/AccountEngineEvent/AccountEngineEventSearchDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the accountEngineEvent print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_AccountEngineEventList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("AccountEngineEvent", getSearchObj(),this.pagingAccountEngineEventList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAccountEngineEventService(AccountEngineEventService accountEngineEventService) {
		this.accountEngineEventService = accountEngineEventService;
	}
	public AccountEngineEventService getAccountEngineEventService() {
		return this.accountEngineEventService;
	}

	public JdbcSearchObject<AccountEngineEvent> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<AccountEngineEvent> searchObj) {
		this.searchObj = searchObj;
	}

}