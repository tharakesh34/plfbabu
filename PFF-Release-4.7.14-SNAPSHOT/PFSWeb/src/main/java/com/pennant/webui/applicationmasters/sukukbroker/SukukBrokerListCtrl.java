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
 * FileName    		:  SukukBrokerListCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmasters.sukukbroker;

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

import com.pennant.backend.model.applicationmasters.SukukBroker;
import com.pennant.backend.service.applicationmaster.SukukBrokerService;
import com.pennant.webui.applicationmasters.sukukbroker.model.SukukBrokerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SukukBroker/SukukBrokerList.zul file.
 */
public class SukukBrokerListCtrl extends GFCBaseListCtrl<SukukBroker> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SukukBrokerListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SukukBrokerList;
	protected Borderlayout borderLayout_SukukBrokerList;
	protected Paging pagingSukukBrokerList;
	protected Listbox listBoxSukukBroker;

	protected Listheader listheader_BrokerCode;
	protected Listheader listheader_BrokerDesc;

	protected Button button_SukukBrokerList_NewSukukBroker;
	protected Button button_SukukBrokerList_SukukBrokerSearch;

	protected Textbox brokerCode;
	protected Textbox brokerDesc;

	protected Listbox sortOperator_BrokerCode;
	protected Listbox sortOperator_BrokerDesc;

	private transient SukukBrokerService sukukBrokerService;

	/**
	 * default constructor.<br>
	 */
	public SukukBrokerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SukukBroker";
		super.pageRightName = "SukukBrokerList";
		super.tableName = "SukukBrokers_AView";
		super.queueTableName = "SukukBrokers_View";
		super.enquiryTableName = "SukukBrokers_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SukukBrokerList(Event event) {
		// Set the page level components.
		setPageComponents(window_SukukBrokerList, borderLayout_SukukBrokerList, listBoxSukukBroker,
				pagingSukukBrokerList);
		setItemRender(new SukukBrokerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SukukBrokerList_NewSukukBroker, "button_SukukBrokerList_NewSukukBroker", true);
		registerButton(button_SukukBrokerList_SukukBrokerSearch);

		registerField("brokerCode", listheader_BrokerCode, SortOrder.ASC, brokerCode, sortOperator_BrokerCode,
				Operators.STRING);
		registerField("brokerDesc", listheader_BrokerDesc, SortOrder.NONE, brokerDesc, sortOperator_BrokerDesc,
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
	public void onClick$button_SukukBrokerList_SukukBrokerSearch(Event event) {
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
	public void onClick$button_SukukBrokerList_NewSukukBroker(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SukukBroker sukukBroker = new SukukBroker();
		sukukBroker.setNewRecord(true);
		sukukBroker.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(sukukBroker);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onSukukBrokerItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSukukBroker.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		SukukBroker sukukBroker = sukukBrokerService.getSukukBrokerById(id);

		if (sukukBroker == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BrokerCode ='" + sukukBroker.getBrokerCode() + "' AND version="
				+ sukukBroker.getVersion() + " ";

		if (doCheckAuthority(sukukBroker, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && sukukBroker.getWorkflowId() == 0) {
				sukukBroker.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(sukukBroker);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSukukBroker
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SukukBroker aSukukBroker) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("sukukBroker", aSukukBroker);
		arg.put("sukukBrokerListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions
					.createComponents("/WEB-INF/pages/ApplicationMaster/SukukBroker/SukukBrokerDialog.zul", null, arg);
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

	public void setSukukBrokerService(SukukBrokerService sukukBrokerService) {
		this.sukukBrokerService = sukukBrokerService;
	}
}