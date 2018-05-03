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
 * FileName    		:  FinanceRepayPriorityListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financerepaypriority;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.service.finance.FinanceRepayPriorityService;
import com.pennant.webui.finance.financerepaypriority.model.FinanceRepayPriorityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceRepayPriority/FinanceRepayPriorityList.zul file.
 */
public class FinanceRepayPriorityListCtrl extends GFCBaseListCtrl<FinanceRepayPriority> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinanceRepayPriorityListCtrl.class);

	protected Window window_FinanceRepayPriorityList;
	protected Borderlayout borderLayout_FinanceRepayPriorityList;
	protected Paging pagingFinanceRepayPriorityList;
	protected Listbox listBoxFinanceRepayPriority;

	protected Listheader listheader_FinType;
	protected Listheader listheader_FinPriority;

	protected Textbox finType;
	protected Listbox sortOperator_finType;
	protected Intbox finPriority;
	protected Listbox sortOperator_finPriority;

	protected Label label_FinanceRepayPrioritySearchResult;

	protected Button button_FinanceRepayPriorityList_NewFinanceRepayPriority;
	protected Button button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog;

	private transient FinanceRepayPriorityService financeRepayPriorityService;

	/**
	 * default constructor.<br>
	 */
	public FinanceRepayPriorityListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceRepayPriority";
		super.pageRightName = "FinanceRepayPriorityList";
		super.tableName = "FinRpyPriority_AView";
		super.queueTableName = "FinRpyPriority_View";
	}

	public void onCreate$window_FinanceRepayPriorityList(Event event) {
		// Set the page level components.
		setPageComponents(window_FinanceRepayPriorityList, borderLayout_FinanceRepayPriorityList,
				listBoxFinanceRepayPriority, pagingFinanceRepayPriorityList);
		setItemRender(new FinanceRepayPriorityListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceRepayPriorityList_NewFinanceRepayPriority,
				"button_FinanceRepayPriorityList_NewFinanceRepayPriority", true);
		registerButton(button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog);

		registerField("finType", listheader_FinType, SortOrder.ASC, finType, sortOperator_finType, Operators.STRING);
		registerField("finPriority", listheader_FinPriority, SortOrder.NONE, finPriority, sortOperator_finPriority,
				Operators.STRING);
		registerField("LovDescFinTypeName");

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
	public void onClick$button_FinanceRepayPriorityList_FinanceRepayPrioritySearchDialog(Event event) {
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
	public void onClick$button_FinanceRepayPriorityList_NewFinanceRepayPriority(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinanceRepayPriority aFinanceRepayPriority = new FinanceRepayPriority();
		aFinanceRepayPriority.setNewRecord(true);
		aFinanceRepayPriority.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aFinanceRepayPriority);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onFinanceRepayPriorityItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinanceRepayPriority.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		FinanceRepayPriority financeRepayPriority = financeRepayPriorityService.getFinanceRepayPriorityById(id);

		if (financeRepayPriority == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinType='" + financeRepayPriority.getFinType() + "' AND version="
				+ financeRepayPriority.getVersion() + " ";

		if (doCheckAuthority(financeRepayPriority, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && financeRepayPriority.getWorkflowId() == 0) {
				financeRepayPriority.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(financeRepayPriority);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFinanceRepayPriority
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceRepayPriority aFinanceRepayPriority) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeRepayPriority", aFinanceRepayPriority);
		arg.put("financeRepayPriorityListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceRepayPriority/FinanceRepayPriorityDialog.zul",
					null, arg);
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

	public void setFinanceRepayPriorityService(FinanceRepayPriorityService financeRepayPriorityService) {
		this.financeRepayPriorityService = financeRepayPriorityService;
	}
}