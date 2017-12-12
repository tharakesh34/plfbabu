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
 * FileName    		:  ScheduleMethodListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.schedulemethod;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.staticparms.ScheduleMethodService;
import com.pennant.webui.staticparms.schedulemethod.model.ScheduleMethodListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/StaticParms/ScheduleMethod/ScheduleMethodList.zul file.
 */
public class ScheduleMethodListCtrl extends GFCBaseListCtrl<ScheduleMethod> {
	private static final long serialVersionUID = -7332745886128746110L;
	private static final Logger logger = Logger.getLogger(ScheduleMethodListCtrl.class);

	protected Window window_ScheduleMethodList;
	protected Borderlayout borderLayout_ScheduleMethodList;
	protected Paging pagingScheduleMethodList;
	protected Listbox listBoxScheduleMethod;

	protected Listheader listheader_SchdMethod;
	protected Listheader listheader_SchdMethodDesc;

	protected Button button_ScheduleMethodList_NewScheduleMethod;
	protected Button button_ScheduleMethodList_ScheduleMethodSearchDialog;

	protected Textbox schdMethod;
	protected Textbox schdMethodDesc;

	protected Listbox sortOperator_schdMethod;
	protected Listbox sortOperator_schdMethodDesc;

	private transient ScheduleMethodService scheduleMethodService;

	/**
	 * default constructor.<br>
	 */
	public ScheduleMethodListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ScheduleMethod";
		super.pageRightName = "ScheduleMethodList";
		super.tableName = "BMTSchdMethod_AView";
		super.queueTableName = "BMTSchdMethod_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ScheduleMethodList(Event event) {
		// Set the page level components.
		setPageComponents(window_ScheduleMethodList, borderLayout_ScheduleMethodList, listBoxScheduleMethod,
				pagingScheduleMethodList);
		setItemRender(new ScheduleMethodListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ScheduleMethodList_NewScheduleMethod, RIGHT_NOT_ACCESSIBLE, true);
		registerButton(button_ScheduleMethodList_ScheduleMethodSearchDialog);

		registerField("schdMethod", listheader_SchdMethod, SortOrder.ASC, schdMethod, sortOperator_schdMethod,
				Operators.STRING);
		registerField("schdMethodDesc", listheader_SchdMethodDesc, SortOrder.NONE, schdMethodDesc,
				sortOperator_schdMethodDesc, Operators.STRING);

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
	public void onClick$button_ScheduleMethodList_ScheduleMethodSearchDialog(Event event) {
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
	public void onClick$button_ScheduleMethodList_NewScheduleMethod(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ScheduleMethod scheduleMethod = new ScheduleMethod();
		scheduleMethod.setNewRecord(true);
		scheduleMethod.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(scheduleMethod);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onScheduleMethodItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxScheduleMethod.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		ScheduleMethod scheduleMethod = scheduleMethodService.getScheduleMethodById(id);

		if (scheduleMethod == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SchdMethod='" + scheduleMethod.getSchdMethod() + "' AND version="
				+ scheduleMethod.getVersion() + " ";

		if (doCheckAuthority(scheduleMethod, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && scheduleMethod.getWorkflowId() == 0) {
				scheduleMethod.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(scheduleMethod);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aScheduleMethod
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ScheduleMethod aScheduleMethod) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("scheduleMethod", aScheduleMethod);
		arg.put("scheduleMethodListCtrl", this);

		try {
			Executions
					.createComponents("/WEB-INF/pages/StaticParms/ScheduleMethod/ScheduleMethodDialog.zul", null, arg);
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

	public void setScheduleMethodService(ScheduleMethodService scheduleMethodService) {
		this.scheduleMethodService = scheduleMethodService;
	}
}