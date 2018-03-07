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
 * FileName    		:  CostOfFundCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.costoffundcode;

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

import com.pennant.backend.model.applicationmaster.CostOfFundCode;
import com.pennant.backend.service.applicationmaster.CostOfFundCodeService;
import com.pennant.webui.applicationmaster.costoffundcode.model.CostOfFundCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CostOfFundCode/CostOfFundCodeList.zul file.
 */
public class CostOfFundCodeListCtrl extends GFCBaseListCtrl<CostOfFundCode> {
	private static final long serialVersionUID = 7711473870956306562L;
	private static final Logger logger = Logger.getLogger(CostOfFundCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CostOfFundCodeList;
	protected Borderlayout borderLayout_CostOfFundCodeList;
	protected Paging pagingCostOfFundCodeList;
	protected Listbox listBoxCostOfFundCode;

	protected Textbox cofCode;
	protected Textbox cofDesc;

	protected Listbox sortOperator_cofCode;
	protected Listbox sortOperator_cofDesc;

	// List headers
	protected Listheader listheader_CofCode;
	protected Listheader listheader_CofDesc;

	// checkRights
	protected Button button_CostOfFundCodeList_NewCostOfFundCode;
	protected Button button_CostOfFundCodeList_CostOfFundCodeSearchDialog;

	private transient CostOfFundCodeService costOfFundCodeService;

	/**
	 * default constructor.<br>
	 */
	public CostOfFundCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CostOfFundCode";
		super.pageRightName = "CostOfFundCodesList";
		super.tableName = "CostOfFundCodes_AView";
		super.queueTableName = "CostOfFundCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CostOfFundCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CostOfFundCodeList, borderLayout_CostOfFundCodeList, listBoxCostOfFundCode,
				pagingCostOfFundCodeList);
		setItemRender(new CostOfFundCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CostOfFundCodeList_NewCostOfFundCode, "button_CostOfFundCodeList_NewCostOfFundCode", true);
		registerButton(button_CostOfFundCodeList_CostOfFundCodeSearchDialog);

		registerField("cofCode", listheader_CofCode, SortOrder.ASC, cofCode, sortOperator_cofCode, Operators.STRING);
		registerField("cofDesc", listheader_CofDesc, SortOrder.NONE, cofDesc, sortOperator_cofDesc,
				Operators.STRING);
				
		// Render the page and display the data.
		doRenderPage();
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CostOfFundCodeList_CostOfFundCodeSearchDialog(Event event) {
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
	public void onClick$button_CostOfFundCodeList_NewCostOfFundCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CostOfFundCode costOfFundCode = new CostOfFundCode();
		costOfFundCode.setNewRecord(true);
		costOfFundCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(costOfFundCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCostOfFundCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCostOfFundCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CostOfFundCode costOfFundCode = costOfFundCodeService.getCostOfFundCodeById(id);

		if (costOfFundCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CofCode='" + costOfFundCode.getCofCode() + "' AND version=" + costOfFundCode.getVersion()
				+ " ";

		if (doCheckAuthority(costOfFundCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && costOfFundCode.getWorkflowId() == 0) {
				costOfFundCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(costOfFundCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCostOfFundCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CostOfFundCode aCostOfFundCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("costOfFundCode", aCostOfFundCode);
		arg.put("costOfFundCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CostOfFundCode/CostOfFundCodeDialog.zul", null,
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

	public void setCostOfFundCodeService(CostOfFundCodeService costOfFundCodeService) {
		this.costOfFundCodeService = costOfFundCodeService;
	}
}