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
 * FileName    		:  DedupParmListCtrl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dedup.dedupparm;

import java.util.List;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.dedup.dedupparm.model.DedupParmListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/DedupParm/DedupParmList.zul file.
 */
public class DedupParmListCtrl extends GFCBaseListCtrl<DedupParm> {
	private static final long serialVersionUID = -2577445041575201178L;
	private static final Logger logger = Logger.getLogger(DedupParmListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DedupParmList;
	protected Borderlayout borderLayout_DedupParmList;
	protected Paging pagingDedupParmList;
	protected Listbox listBoxDedupParm;

	protected Listheader listheader_QueryCode;
	protected Listheader listheader_QueryDesc;
	protected Listheader listheader_CustCtgCode;

	protected Button button_DedupParmList_NewDedupParm;
	protected Button button_DedupParmList_DedupParmSearchDialog;

	protected Textbox queryModule;
	protected Textbox queryCode;
	protected Textbox queryDesc;
	protected Textbox queryModules;
	protected Textbox sQLQuery;
	protected Combobox querySubCode;

	protected Listbox sortOperator_queryCode;
	protected Listbox sortOperator_queryDesc;
	protected Listbox sortOperator_queryModules;
	protected Listbox sortOperator_querySubCode;
	protected Listbox sortOperator_sQLQuery;

	private transient DedupParmService dedupParmService;
	private List<ValueLabel> listCustCtgCode = PennantAppUtil.getcustCtgCodeList();

	/**
	 * default constructor.<br>
	 */
	public DedupParmListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DedupParm";
		super.pageRightName = "DedupParmList";
		super.tableName = "DedupParams_AView";
		super.queueTableName = "DedupParams_View";

	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("queryModule", this.queryModule.getValue(), Filter.OP_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DedupParmList(Event event) {
		// Set the page level components.
		setPageComponents(window_DedupParmList, borderLayout_DedupParmList, listBoxDedupParm, pagingDedupParmList);
		setItemRender(new DedupParmListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DedupParmList_NewDedupParm, "button_DedupParmList_New" + this.queryModule.getValue()
				+ "Dedup", true);
		registerButton(button_DedupParmList_DedupParmSearchDialog);

		registerField("queryId");
		registerField("queryCode", listheader_QueryCode, SortOrder.ASC, queryCode, sortOperator_queryCode,
				Operators.STRING);
		registerField("queryDesc", listheader_QueryDesc, SortOrder.NONE, queryDesc, sortOperator_queryDesc,
				Operators.STRING);
		registerField("queryModule", queryModules, SortOrder.NONE, sortOperator_queryModules, Operators.STRING);
		fillComboBox(this.querySubCode, "", this.listCustCtgCode, "");
		registerField("querySubCode", listheader_CustCtgCode, SortOrder.NONE, querySubCode, sortOperator_querySubCode,
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
	public void onClick$button_DedupParmList_DedupParmSearchDialog(Event event) {
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
	public void onClick$button_DedupParmList_NewDedupParm(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		DedupParm dedupParm = new DedupParm();
		dedupParm.setNewRecord(true);
		dedupParm.setWorkflowId(getWorkFlowId());
		dedupParm.setQueryModule(this.queryModule.getValue());

		// Display the dialog page.
		doShowDialogPage(dedupParm);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDedupParmItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDedupParm.getSelectedItem();

		// Get the selected entity.
		String queryCode = (String) selectedItem.getAttribute("queryCode");
		String queryModule = (String) selectedItem.getAttribute("queryModule");
		String querySubCode = (String) selectedItem.getAttribute("querySubCode");
		DedupParm dedupParm = dedupParmService.getDedupParmById(queryCode, queryModule, querySubCode);

		if (dedupParm == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND QueryCode='" + dedupParm.getQueryCode() + "' AND QueryModule='"
				+ dedupParm.getQueryModule() + "' AND QuerySubCode='" + dedupParm.getQuerySubCode() + "' AND version="
				+ dedupParm.getVersion() + " ";

		if (doCheckAuthority(dedupParm, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dedupParm.getWorkflowId() == 0) {
				dedupParm.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dedupParm);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param dedupParm
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DedupParm dedupParm) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("queryModule", this.queryModule.getValue());
		arg.put("dedupParm", dedupParm);
		arg.put("dedupParmListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/DedupParm/DedupParmDialog.zul", null, arg);
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

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}
}