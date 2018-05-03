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
 * FileName    		:  SukukBondListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmasters.sukukbond;

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

import com.pennant.backend.model.applicationmasters.SukukBond;
import com.pennant.backend.service.applicationmaster.SukukBondService;
import com.pennant.webui.applicationmasters.sukukbond.model.SukukBondListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SukukBond/SukukBondList.zul file.
 */
public class SukukBondListCtrl extends GFCBaseListCtrl<SukukBond> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SukukBondListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SukukBondList;
	protected Borderlayout borderLayout_SukukBondList;
	protected Paging pagingSukukBondList;
	protected Listbox listBoxSukukBond;

	protected Listheader listheader_BondCode;
	protected Listheader listheader_BondDesc;

	protected Button button_SukukBondList_NewSukukBond;
	protected Button button_SukukBondList_SukukBondSearch;

	protected Textbox bondCode;
	protected Textbox bondDesc;

	protected Listbox sortOperator_BondCode;
	protected Listbox sortOperator_BondDesc;

	private transient SukukBondService sukukBondService;

	/**
	 * default constructor.<br>
	 */
	public SukukBondListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SukukBond";
		super.pageRightName = "SukukBondList";
		super.tableName = "SukukBonds_AView";
		super.queueTableName = "SukukBonds_View";
		super.enquiryTableName = "SukukBonds_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SukukBondList(Event event) {
		// Set the page level components.
		setPageComponents(window_SukukBondList, borderLayout_SukukBondList, listBoxSukukBond, pagingSukukBondList);
		setItemRender(new SukukBondListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SukukBondList_NewSukukBond, "button_SukukBondList_NewSukukBond", true);
		registerButton(button_SukukBondList_SukukBondSearch);

		registerField("bondCode", listheader_BondCode, SortOrder.ASC, bondCode, sortOperator_BondCode, Operators.STRING);
		registerField("bondDesc", listheader_BondDesc, SortOrder.NONE, bondDesc, sortOperator_BondDesc,
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
	public void onClick$button_SukukBondList_SukukBondSearch(Event event) {
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
	public void onClick$button_SukukBondList_NewSukukBond(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SukukBond sukukBond = new SukukBond();
		sukukBond.setNewRecord(true);
		sukukBond.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(sukukBond);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSukukBondItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSukukBond.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		SukukBond sukukBond = sukukBondService.getSukukBondById(id);

		if (sukukBond == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BondCode ='" + sukukBond.getBondCode() + "' AND version=" + sukukBond.getVersion()
				+ " ";

		if (doCheckAuthority(sukukBond, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && sukukBond.getWorkflowId() == 0) {
				sukukBond.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(sukukBond);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
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
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSukukBond
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SukukBond aSukukBond) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("sukukBond", aSukukBond);
		arg.put("sukukBondListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/SukukBond/SukukBondDialog.zul", null, arg);
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

	public void setSukukBondService(SukukBondService sukukBondService) {
		this.sukukBondService = sukukBondService;
	}
}