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
 * FileName    		:  CheckListListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.checklist;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.applicationmaster.CheckListService;
import com.pennant.webui.applicationmaster.checklist.model.CheckListListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CheckList/CheckListList.zul file.
 */
public class CheckListListCtrl extends GFCBaseListCtrl<CheckList> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CheckListListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CheckListList;
	protected Borderlayout borderLayout_CheckListList;
	protected Paging pagingCheckListList;
	protected Listbox listBoxCheckList;

	protected Listheader listheader_CheckListDesc;
	protected Listheader listheader_Active;
	protected Listheader listheader_CheckListMaxCount;
	protected Listheader listheader_CheckListMinCount;

	protected Button button_CheckListList_NewCheckList;
	protected Button button_CheckListList_CheckListSearchDialog;

	protected Textbox checkListDesc;
	protected Intbox checkMinCount;
	protected Intbox checkMaxCount;
	protected Checkbox active;

	protected Listbox sortOperator_checkListDesc;
	protected Listbox sortOperator_checkMinCount;
	protected Listbox sortOperator_checkMaxCount;
	protected Listbox sortOperator_active;

	private transient CheckListService checkListService;

	/**
	 * default constructor.<br>
	 */
	public CheckListListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CheckList";
		super.pageRightName = "CheckListList";
		super.tableName = "BMTCheckList_AView";
		super.queueTableName = "BMTCheckList_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CheckListList(Event event) {
		// Set the page level components.
		setPageComponents(window_CheckListList, borderLayout_CheckListList, listBoxCheckList, pagingCheckListList);
		setItemRender(new CheckListListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CheckListList_NewCheckList, "button_CheckListList_NewCheckList", true);
		registerButton(button_CheckListList_CheckListSearchDialog);

		registerField("checkListId");
		registerField("checkListDesc", listheader_CheckListDesc, SortOrder.ASC, checkListDesc,
				sortOperator_checkListDesc, Operators.STRING);
		registerField("checkMinCount", listheader_CheckListMinCount, SortOrder.NONE, checkMinCount,
				sortOperator_checkMinCount, Operators.NUMERIC);
		registerField("checkMaxCount", listheader_CheckListMaxCount, SortOrder.NONE, checkMaxCount,
				sortOperator_checkMaxCount, Operators.NUMERIC);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Entering");

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CheckListList_CheckListSearchDialog(Event event) {
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
	public void onClick$button_CheckListList_NewCheckList(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CheckList checkList = new CheckList();
		checkList.setNewRecord(true);
		checkList.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(checkList);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCheckListItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCheckList.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		CheckList checkList = checkListService.getCheckListById(id);

		if (checkList == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CheckListId=" + checkList.getCheckListId() + " AND version=" + checkList.getVersion()
				+ " ";

		if (doCheckAuthority(checkList, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && checkList.getWorkflowId() == 0) {
				checkList.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(checkList);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCheckList
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CheckList aCheckList) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("checkList", aCheckList);
		arg.put("checkListListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

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

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setCheckListService(CheckListService checkListService) {
		this.checkListService = checkListService;
	}

}