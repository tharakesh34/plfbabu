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
 * FileName    		:  HolidayMasterListCtrl.java                                                   * 	  
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
package com.pennant.webui.smtmasters.holidaymaster;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.webui.smtmasters.holidaymaster.model.HolidayMasterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterList.zul file.
 */
public class HolidayMasterListCtrl extends GFCBaseListCtrl<HolidayMaster> {
	private static final long serialVersionUID = 5550212164288969546L;
	private static final Logger logger = Logger.getLogger(HolidayMasterListCtrl.class);

	protected Window window_HolidayMasterList;
	protected Borderlayout borderLayout_HolidayMasterList;
	protected Paging pagingHolidayMasterList;
	protected Listbox listBoxHolidayMaster;

	protected Listheader listheader_HolidayCode;
	protected Listheader listheader_HolidayYear;
	protected Listheader listheader_HolidayType;

	protected Button button_HolidayMasterList_NewHolidayMaster;
	protected Button button_HolidayMasterList_HolidayMasterSearchDialog;

	protected Textbox holidayCode;
	protected Intbox holidayYear;
	protected Combobox holidayType;

	protected Listbox sortOperator_holidayCode;
	protected Listbox sortOperator_holidayYear;
	protected Listbox sortOperator_holidayType;

	// row count for listbox
	private int countRows;

	private transient HolidayMasterService holidayMasterService;

	/**
	 * default constructor.<br>
	 */
	public HolidayMasterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "HolidayMaster";
		super.pageRightName = "HolidayMasterList";
		super.tableName = "SMTHolidayMaster_AView";
		super.queueTableName = "SMTHolidayMaster_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_HolidayMasterList(Event event) {
		// Set the page level components.
		setPageComponents(window_HolidayMasterList, borderLayout_HolidayMasterList, listBoxHolidayMaster,
				pagingHolidayMasterList);
		setItemRender(new HolidayMasterListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_HolidayMasterList_NewHolidayMaster, "button_HolidayMasterList_NewHolidayMaster", true);
		registerButton(button_HolidayMasterList_HolidayMasterSearchDialog);

		registerField("HolidayCode", listheader_HolidayCode, SortOrder.ASC, holidayCode, sortOperator_holidayCode,
				Operators.STRING);
		registerField("holidayYear", listheader_HolidayYear, SortOrder.NONE, holidayYear, sortOperator_holidayYear,
				Operators.NUMERIC);
		registerField("holidayType", listheader_HolidayType, SortOrder.NONE, holidayType, sortOperator_holidayType,
				Operators.STRING);

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
	public void onClick$button_HolidayMasterList_HolidayMasterSearchDialog(Event event) {
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
	public void onClick$button_HolidayMasterList_NewHolidayMaster(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		HolidayMaster holidayMaster = new HolidayMaster();
		holidayMaster.setNewRecord(true);
		holidayMaster.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(holidayMaster);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onHolidayMasterItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxHolidayMaster.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		BigDecimal year = (BigDecimal) selectedItem.getAttribute("year");
		HolidayMaster holidayMaster = holidayMasterService.getHolidayMasterById(id, year);

		if (holidayMaster == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND HolidayCode='" + holidayMaster.getHolidayCode() + "' AND version="
				+ holidayMaster.getVersion() + " ";

		if (doCheckAuthority(holidayMaster, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && holidayMaster.getWorkflowId() == 0) {
				holidayMaster.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(holidayMaster);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aHolidayMaster
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(HolidayMaster aHolidayMaster) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("holidayMaster", aHolidayMaster);
		arg.put("holidayMasterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterDialog.zul", null,
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

	public void setHolidayMasterService(HolidayMasterService holidayMasterService) {
		this.holidayMasterService = holidayMasterService;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}
}