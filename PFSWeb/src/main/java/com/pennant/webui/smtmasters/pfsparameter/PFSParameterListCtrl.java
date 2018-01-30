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
 * FileName    		:  PFSParameterListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-07-2011    														*
 *                                                                  						*
 * Modified Date    :  12-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-07-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.smtmasters.pfsparameter;

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

import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.webui.smtmasters.pfsparameter.model.PFSParameterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/PFSParameter/PFSParameterList.zul file.
 */
public class PFSParameterListCtrl extends GFCBaseListCtrl<PFSParameter> {
	private static final long serialVersionUID = 8002179731510010018L;
	private static final Logger logger = Logger.getLogger(PFSParameterListCtrl.class);

	protected Window window_PFSParameterList;
	protected Borderlayout borderLayout_PFSParameterList;
	protected Paging pagingPFSParameterList;
	protected Listbox listBoxPFSParameter;

	protected Textbox sysParmCode;
	protected Listbox sortOperator_sysParmCode;
	protected Textbox sysParmDesc;
	protected Listbox sortOperator_sysParmDesc;
	protected Textbox sysParmValue;
	protected Listbox sortOperator_sysParmValue;

	protected Listheader listheader_SysParmCode;
	protected Listheader listheader_SysParmDesc;
	protected Listheader listheader_SysParmValue;

	protected Button button_PFSParameterList_NewPFSParameter;
	protected Button button_PFSParameterList_PFSParameterSearchDialog;

	private transient PFSParameterService systemParameterService;

	/**
	 * default constructor.<br>
	 */
	public PFSParameterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PFSParameter";
		super.pageRightName = "PFSParameterList";
		super.tableName = "SMTparameters_AView";
		super.queueTableName = "SMTparameters_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PFSParameterList(Event event) {
		// Set the page level components.
		setPageComponents(window_PFSParameterList, borderLayout_PFSParameterList, listBoxPFSParameter,
				pagingPFSParameterList);
		setItemRender(new PFSParameterListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PFSParameterList_NewPFSParameter, "button_PFSParameterList_NewPFSParameter", true);
		registerButton(button_PFSParameterList_PFSParameterSearchDialog);

		registerField("SysParmCode", listheader_SysParmCode, SortOrder.ASC, sysParmCode, sortOperator_sysParmCode,
				Operators.STRING);
		registerField("SysParmDesc", listheader_SysParmDesc, SortOrder.NONE, sysParmDesc, sortOperator_sysParmDesc,
				Operators.STRING);
		registerField("SysParmValue", listheader_SysParmValue, SortOrder.NONE, sysParmValue, sortOperator_sysParmValue,
				Operators.STRING);

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
	public void onClick$button_PFSParameterList_PFSParameterSearchDialog(Event event) {
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
	public void onClick$button_PFSParameterList_NewPFSParameter(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		PFSParameter aPFSParameter = new PFSParameter();
		aPFSParameter.setNewRecord(true);
		aPFSParameter.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aPFSParameter);

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPFSParameterItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPFSParameter.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		PFSParameter aPFSParameter = systemParameterService.getPFSParameterById(id);

		if (aPFSParameter == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SysParmCode='" + aPFSParameter.getSysParmCode() + "' AND version="
				+ aPFSParameter.getVersion() + " ";

		if (doCheckAuthority(aPFSParameter, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aPFSParameter.getWorkflowId() == 0) {
				aPFSParameter.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aPFSParameter);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PFSParameter aPFSParameter){
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("pFSParameter", aPFSParameter);
		arg.put("pFSParameterListCtrl", this);

		try {
			Executions
					.createComponents("/WEB-INF/pages/SolutionFactory/PFSParameter/PFSParameterDialog.zul", null, arg);
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

	public void setSystemParameterService(PFSParameterService systemParameterService) {
		this.systemParameterService = systemParameterService;
	}

}