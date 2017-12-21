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
 * FileName    		:  WeekendMasterListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.smtmasters.weekendmaster;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.service.smtmasters.WeekendMasterService;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.smtmasters.weekendmaster.model.WeekendMasterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/WeekendMaster/WeekendMasterList.zul file.
 */
public class WeekendMasterListCtrl extends GFCBaseListCtrl<WeekendMaster> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(WeekendMasterListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_WeekendMasterList;
	protected Borderlayout borderLayout_WeekendMasterList;
	protected Paging pagingWeekendMasterList;
	protected Listbox listBoxWeekendMaster;

	protected Button button_WeekendMasterList_NewWeekendMaster;
	protected Button button_WeekendMasterList_WeekendMasterSearchDialog;

	protected Listheader listheader_WeekendCode;
	protected Listheader listheader_WeekendDesc;
	protected Listheader listheader_Weekend;

	protected Uppercasebox weekendCode;
	protected Textbox weekendDesc;
	protected Combobox weekend;

	protected Listbox sortOperator_weekendCode;
	protected Listbox sortOperator_weekendDesc;
	protected Listbox sortOperator_weekend;

	protected Row weekendRow;

	// row count for listbox
	private int countRows;

	private transient WeekendMasterService weekendMasterService;

	/**
	 * default constructor.<br>
	 */
	public WeekendMasterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "WeekendMaster";
		super.pageRightName = "WeekendMasterList";
		super.tableName = "SMTWeekendMaster_AView";
		super.queueTableName = "SMTWeekendMaster_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_WeekendMasterList(Event event) {

		this.weekendRow.setVisible(false);

		// Set the page level components.
		setPageComponents(window_WeekendMasterList, borderLayout_WeekendMasterList, listBoxWeekendMaster,
				pagingWeekendMasterList);
		setItemRender(new WeekendMasterListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_WeekendMasterList_NewWeekendMaster, "button_WeekendMasterList_NewWeekendMaster", true);
		registerButton(button_WeekendMasterList_WeekendMasterSearchDialog);

		registerField("WeekendCode", listheader_WeekendCode, SortOrder.ASC, weekendCode, sortOperator_weekendCode,
				Operators.STRING);
		registerField("weekendDesc", listheader_WeekendDesc, SortOrder.NONE, weekendDesc, sortOperator_weekendDesc,
				Operators.STRING);
		registerField("weekend", listheader_Weekend, SortOrder.NONE, weekend, sortOperator_weekend, Operators.STRING);

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
	public void onClick$button_WeekendMasterList_WeekendMasterSearchDialog(Event event) {
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
	public void onClick$button_WeekendMasterList_NewWeekendMaster(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		WeekendMaster weekendMaster = new WeekendMaster();
		weekendMaster.setNewRecord(true);
		weekendMaster.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(weekendMaster);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onWeekendMasterItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxWeekendMaster.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		WeekendMaster weekendMaster = weekendMasterService.getWeekendMasterById(id);

		if (weekendMaster == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND WeekendCode='" + weekendMaster.getWeekendCode() + "' AND version="
				+ weekendMaster.getVersion() + " ";

		if (doCheckAuthority(weekendMaster, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && weekendMaster.getWorkflowId() == 0) {
				weekendMaster.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(weekendMaster);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aWeekendMaster
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(WeekendMaster aWeekendMaster) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("weekendMaster", aWeekendMaster);
		arg.put("weekendMasterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/WeekendMaster/WeekendMasterDialog.zul", null,
					arg);
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

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public void setWeekendMasterService(WeekendMasterService weekendMasterService) {
		this.weekendMasterService = weekendMasterService;
	}
}