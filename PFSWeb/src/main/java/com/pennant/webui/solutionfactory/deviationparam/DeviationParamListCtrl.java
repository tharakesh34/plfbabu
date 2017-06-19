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
 * FileName    		:  DeviationParamListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-06-2015    														*
 *                                                                  						*
 * Modified Date    :  22-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.solutionfactory.deviationparam;

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

import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.solutionfactory.DeviationParamService;
import com.pennant.webui.solutionfactory.deviationparam.model.DeviationParamListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/DeviationParam/DeviationParamList.zul file.
 */
public class DeviationParamListCtrl extends GFCBaseListCtrl<DeviationParam> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DeviationParamListCtrl.class);

	protected Window window_DeviationParamList;
	protected Borderlayout borderLayout_DeviationParamList;
	protected Paging pagingDeviationParamList;
	protected Listbox listBoxDeviationParam;

	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_DataType;
	protected Listheader listheader_Type;

	protected Button button_DeviationParamList_NewDeviationParam;
	protected Button button_DeviationParamList_DeviationParamSearch;

	protected Textbox code;
	protected Textbox description;
	protected Textbox type;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Type;

	private transient DeviationParamService deviationParamService;

	/**
	 * default constructor.<br>
	 */
	public DeviationParamListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DeviationParam";
		super.pageRightName = "DeviationParamList";
		super.tableName = "DeviationParams_AView";
		super.queueTableName = "DeviationParams_View";
		super.enquiryTableName = "DeviationParams_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DeviationParamList(Event event) {
		// Set the page level components.
		setPageComponents(window_DeviationParamList, borderLayout_DeviationParamList, listBoxDeviationParam,
				pagingDeviationParamList);
		setItemRender(new DeviationParamListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DeviationParamList_NewDeviationParam, "button_DeviationParamList_NewDeviationParam", true);
		registerButton(button_DeviationParamList_DeviationParamSearch);

		registerField("code", listheader_Code, SortOrder.ASC, code, sortOperator_Code, Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("type", listheader_Type, SortOrder.NONE, type, sortOperator_Type, Operators.STRING);
		registerField("dataType", listheader_DataType);

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
	public void onClick$button_DeviationParamList_DeviationParamSearch(Event event) {
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
	public void onClick$button_DeviationParamList_NewDeviationParam(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		DeviationParam deviationParam = new DeviationParam();
		deviationParam.setNewRecord(true);
		deviationParam.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(deviationParam);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDeviationParamItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDeviationParam.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		DeviationParam deviationParam = deviationParamService.getDeviationParamById(id);

		if (deviationParam == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND Code='" + deviationParam.getCode() + "' AND version=" + deviationParam.getVersion()
				+ " ";

		if (doCheckAuthority(deviationParam, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && deviationParam.getWorkflowId() == 0) {
				deviationParam.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(deviationParam);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param deviationParam
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DeviationParam deviationParam) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("deviationParam", deviationParam);
		arg.put("deviationParamListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/DeviationParam/DeviationParamDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setDeviationParamService(DeviationParamService deviationParamService) {
		this.deviationParamService = deviationParamService;
	}

}