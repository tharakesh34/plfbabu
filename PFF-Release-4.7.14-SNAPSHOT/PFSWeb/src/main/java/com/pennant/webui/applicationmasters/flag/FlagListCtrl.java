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
 * FileName    		:  FlagListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-07-2015    														*
 *                                                                  						*
 * Modified Date    :  14-07-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-07-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmasters.flag;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.service.applicationmaster.FlagService;
import com.pennant.webui.applicationmasters.flag.model.FlagListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMasters/Flag/FlagList.zul file.
 */
public class FlagListCtrl extends GFCBaseListCtrl<Flag> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FlagListCtrl.class);

	protected Window window_FlagList;
	protected Borderlayout borderLayout_FlagList;
	protected Paging pagingFlagList;
	protected Listbox listBoxFlag;

	protected Textbox flagCode;
	protected Textbox flagDesc;
	protected Checkbox active;

	protected Listbox sortOperator_FlagCode;
	protected Listbox sortOperator_FlagDesc;
	protected Listbox sortOperator_Active;

	protected Listheader listheader_FlagCode;
	protected Listheader listheader_FlagDesc;
	protected Listheader listheader_Active;

	protected Button button_FlagList_NewFlag;
	protected Button button_FlagList_FlagSearch;

	private transient FlagService flagService;

	/**
	 * default constructor.<br>
	 */
	public FlagListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Flag";
		super.pageRightName = "FlagsList";
		super.tableName = "Flags_AView";
		super.queueTableName = "Flags_View";
		super.enquiryTableName = "Flags_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FlagList(Event event) {
		// Set the page level components.
		setPageComponents(window_FlagList, borderLayout_FlagList, listBoxFlag, pagingFlagList);
		setItemRender(new FlagListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FlagList_NewFlag, "button_FlagsList_NewFlagsList", true);
		registerButton(button_FlagList_FlagSearch);

		registerField("flagCode", listheader_FlagCode, SortOrder.ASC, flagCode, sortOperator_FlagCode, Operators.STRING);
		registerField("flagDesc", listheader_FlagDesc, SortOrder.NONE, flagDesc, sortOperator_FlagDesc,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE,active,sortOperator_Active,Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FlagList_FlagSearch(Event event) {
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
	public void onClick$button_FlagList_NewFlag(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Flag flag = new Flag();
		flag.setNewRecord(true);
		flag.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(flag);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFlagItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFlag.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Flag flag = flagService.getFlagById(id);

		if (flag == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FlagCode='" + flag.getFlagCode() + "' AND version=" + flag.getVersion() + " ";

		if (doCheckAuthority(flag, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && flag.getWorkflowId() == 0) {
				flag.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(flag);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFlag
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Flag aFlag) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("flag", aFlag);
		arg.put("flagListCtrl", this);
		arg.put("enqModule", super.enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Flag/FlagDialog.zul", null, arg);
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

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setFlagService(FlagService flagService) {
		this.flagService = flagService;
	}
}