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
 * FileName    		:  CostOfFundListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.costoffund;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennant.backend.service.applicationmaster.CostOfFundService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.applicationmaster.costoffund.model.CostOfFundListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CostOfFund/CostOfFundList.zul file.
 */
public class CostOfFundListCtrl extends GFCBaseListCtrl<CostOfFund> {
	private static final long serialVersionUID = 8263433171238545613L;
	private static final Logger logger = Logger.getLogger(CostOfFundListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CostOfFundList;
	protected Borderlayout borderLayout_CostOfFundList;
	protected Paging pagingCostOfFundList;
	protected Listbox listBoxCostOfFund;

	protected Textbox cofCode;
	protected Textbox cofDesc;
	protected Datebox cofEffDate;
	protected Decimalbox cofRate;

	protected Listbox sortOperator_cofCode;
	protected Listbox sortOperator_cofDesc;
	protected Listbox sortOperator_cofEffDate;
	protected Listbox sortOperator_cofRate;

	// List headers
	protected Listheader listheader_CofCode;
	protected Listheader listheader_CofDesc;
	protected Listheader listheader_CofEffDate;
	protected Listheader listheader_CofRate;

	// checkRights
	protected Button button_CostOfFundList_NewCostOfFund;
	protected Button button_CostOfFundList_CostOfFundSearchDialog;

	private transient CostOfFundService costOfFundService;

	/**
	 * default constructor.<br>
	 */
	public CostOfFundListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CostOfFund";
		super.pageRightName = "CostOfFundsList";
		super.tableName = "CostOfFunds_AView";
		super.queueTableName = "CostOfFunds_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CostOfFundList(Event event) {
		// Set the page level components.
		setPageComponents(window_CostOfFundList, borderLayout_CostOfFundList, listBoxCostOfFund, pagingCostOfFundList);
		setItemRender(new CostOfFundListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CostOfFundList_NewCostOfFund, "button_CostOfFundList_NewCostOfFund", true);
		registerButton(button_CostOfFundList_CostOfFundSearchDialog);

		registerField("cofCode", listheader_CofCode, SortOrder.ASC, cofCode, sortOperator_cofCode, Operators.STRING);
		registerField("lovDescCofTypeName", listheader_CofDesc, SortOrder.NONE, cofDesc, sortOperator_cofDesc,
				Operators.STRING);
		registerField("cofEffDate", listheader_CofEffDate, SortOrder.NONE, cofEffDate, sortOperator_cofEffDate,
				Operators.DATE);
		registerField("currency");
		registerField("cofRate", listheader_CofRate, SortOrder.NONE, cofRate, sortOperator_cofRate, Operators.NUMERIC);

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
	public void onClick$button_CostOfFundList_CostOfFundSearchDialog(Event event) {
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
	public void onClick$button_CostOfFundList_NewCostOfFund(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CostOfFund costOfFund = new CostOfFund();
		costOfFund.setNewRecord(true);
		costOfFund.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(costOfFund);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCostOfFundItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCostOfFund.getSelectedItem();

		// Get the selected entity.
		String cofCode = (String) selectedItem.getAttribute("cofCode");
		String currency = (String) selectedItem.getAttribute("currency");
		Date cofEffDate = (Date) selectedItem.getAttribute("cofEffDate");

		CostOfFund costOfFund = costOfFundService.getCostOfFundById(cofCode, currency, cofEffDate);

		if (costOfFund == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CofCode='" + costOfFund.getCofCode() + "' AND CofEffDate='"
				+ DateUtility.formatDate(costOfFund.getCofEffDate(), PennantConstants.DBDateFormat) + "' AND version="
				+ costOfFund.getVersion() + " ";

		if (doCheckAuthority(costOfFund, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && costOfFund.getWorkflowId() == 0) {
				costOfFund.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(costOfFund);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCostOfFund
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CostOfFund aCostOfFund) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("costOfFund", aCostOfFund);
		arg.put("costOfFundListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CostOfFundRate/CostOfFundDialog.zul", null, arg);
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

	public void setCostOfFundService(CostOfFundService costOfFundService) {
		this.costOfFundService = costOfFundService;
	}

}