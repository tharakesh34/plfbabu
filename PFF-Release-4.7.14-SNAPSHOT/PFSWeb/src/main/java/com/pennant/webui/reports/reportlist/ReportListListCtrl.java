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
 * FileName    		:  ReportListListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.reports.reportlist;

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

import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.service.reports.ReportListService;
import com.pennant.webui.reports.reportlist.model.ReportListListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Reports/ReportList/ReportListList.zul file.
 */
public class ReportListListCtrl extends GFCBaseListCtrl<ReportList> {
	private static final long serialVersionUID = 2474591726313352697L;
	private static final Logger logger = Logger.getLogger(ReportListListCtrl.class);

	protected Window window_ReportListList;
	protected Borderlayout borderLayout_ReportListList;
	protected Listbox listBoxReportList;
	protected Paging pagingReportListList;

	protected Listheader listheader_Module;
	protected Listheader listheader_ReportFileName;
	protected Listheader listheader_ReportHeading;
	protected Listheader listheader_ModuleType;

	protected Button button_ReportListList_NewReportList;
	protected Button button_ReportListList_ReportListSearchDialog;

	protected Textbox module;
	protected Textbox moduleType;
	protected Textbox reportFileName;
	protected Textbox reportHeading;

	protected Listbox sortOperator_repFileName;
	protected Listbox sortOperator_repHeading;
	protected Listbox sortOperator_moduleType;
	protected Listbox sortOperator_module;

	private transient ReportListService reportListService;

	/**
	 * default constructor.<br>
	 */
	public ReportListListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReportList";
		super.pageRightName = "ReportListList";
		super.tableName = "ReportList_AView";
		super.queueTableName = "ReportList_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReportListList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReportListList, borderLayout_ReportListList, listBoxReportList, pagingReportListList);
		setItemRender(new ReportListListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReportListList_NewReportList, "button_ReportListList_NewReportList", true);
		registerButton(button_ReportListList_ReportListSearchDialog);

		registerField("Code");
		registerField("Module", listheader_Module, SortOrder.ASC, module, sortOperator_module, Operators.STRING);
		registerField("reportFileName", listheader_ReportFileName, SortOrder.NONE, reportFileName,
				sortOperator_repFileName, Operators.STRING);
		registerField("reportHeading", listheader_ReportHeading, SortOrder.NONE, reportHeading,
				sortOperator_repHeading, Operators.STRING);
		registerField("moduleType", listheader_ModuleType, SortOrder.NONE, moduleType, sortOperator_moduleType,
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
	public void onClick$button_ReportListList_ReportListSearchDialog(Event event) {
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
	 * Call the ReportList dialog with a new empty entry. <br>
	 */
	public void onClick$button_ReportListList_NewReportList(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ReportList aReportList = new ReportList();
		aReportList.setNewRecord(true);
		aReportList.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aReportList);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onReportListItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReportList.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		ReportList aReportList = reportListService.getReportListById(id);

		if (aReportList == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND Module='" + aReportList.getModule() + "' AND version=" + aReportList.getVersion()
				+ " ";

		if (doCheckAuthority(aReportList, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aReportList.getWorkflowId() == 0) {
				aReportList.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aReportList);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aReportList
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReportList aReportList) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("reportList", aReportList);
		arg.put("reportListListCtrl", this);
		arg.put("moduleCode", super.moduleCode);

		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportList/ReportListDialog.zul", null, arg);
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

	public void setReportListService(ReportListService reportListService) {
		this.reportListService = reportListService;
	}
}