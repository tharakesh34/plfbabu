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
 * FileName    		:  ProfitCenterListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.profitcenter;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.service.applicationmaster.ProfitCenterService;
import com.pennant.webui.applicationmaster.profitcenter.model.ProfitCenterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/ProfitCenter/ProfitCenterList.zul
 * file.
 * 
 */
public class ProfitCenterListCtrl extends GFCBaseListCtrl<ProfitCenter> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ProfitCenterListCtrl.class);

	protected Window window_ProfitCenterList;
	protected Borderlayout borderLayout_ProfitCenterList;
	protected Paging pagingProfitCenterList;
	protected Listbox listBoxProfitCenter;

	// List headers
	protected Listheader listheader_ProfitCenterCode;
	protected Listheader listheader_ProfitCenterDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_ProfitCenterList_NewProfitCenter;
	protected Button button_ProfitCenterList_ProfitCenterSearch;

	// Search Fields
	protected Textbox profitCenterCode; // autowired
	protected Textbox profitCenterDesc; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_ProfitCenterCode;
	protected Listbox sortOperator_ProfitCenterDesc;
	protected Listbox sortOperator_Active;

	private transient ProfitCenterService profitCenterService;

	/**
	 * default constructor.<br>
	 */
	public ProfitCenterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ProfitCenter";
		super.pageRightName = "ProfitCenterList";
		super.tableName = "ProfitCenters_AView";
		super.queueTableName = "ProfitCenters_View";
		super.enquiryTableName = "ProfitCenters_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProfitCenterList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ProfitCenterList, borderLayout_ProfitCenterList, listBoxProfitCenter,
				pagingProfitCenterList);
		setItemRender(new ProfitCenterListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ProfitCenterList_ProfitCenterSearch);
		registerButton(button_ProfitCenterList_NewProfitCenter, "button_ProfitCenterList_NewProfitCenter", true);

		registerField("profitCenterID");
		registerField("profitCenterCode", listheader_ProfitCenterCode, SortOrder.NONE, profitCenterCode,
				sortOperator_ProfitCenterCode, Operators.STRING);
		registerField("profitCenterDesc", listheader_ProfitCenterDesc, SortOrder.NONE, profitCenterDesc,
				sortOperator_ProfitCenterDesc, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

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
	public void onClick$button_ProfitCenterList_ProfitCenterSearch(Event event) {
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
	public void onClick$button_ProfitCenterList_NewProfitCenter(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ProfitCenter profitcenter = new ProfitCenter();
		profitcenter.setNewRecord(true);
		profitcenter.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(profitcenter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onProfitCenterItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxProfitCenter.getSelectedItem();
		final long profitCenterID = (long) selectedItem.getAttribute("profitCenterID");
		ProfitCenter profitcenter = profitCenterService.getProfitCenter(profitCenterID);

		if (profitcenter == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  AND  ProfitCenterID = ");
		whereCond.append(profitcenter.getProfitCenterID());
		whereCond.append(" AND  version=");
		whereCond.append(profitcenter.getVersion());

		if (doCheckAuthority(profitcenter, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && profitcenter.getWorkflowId() == 0) {
				profitcenter.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(profitcenter);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param profitcenter
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ProfitCenter profitcenter) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("profitCenter", profitcenter);
		arg.put("profitCenterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/ProfitCenter/ProfitCenterDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setProfitCenterService(ProfitCenterService profitCenterService) {
		this.profitCenterService = profitCenterService;
	}
}