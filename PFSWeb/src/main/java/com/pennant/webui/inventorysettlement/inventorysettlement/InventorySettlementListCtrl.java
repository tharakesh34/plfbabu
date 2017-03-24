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
 * FileName    		:  InventorySettlementListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.inventorysettlement.inventorysettlement;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.inventorysettlement.InventorySettlement;
import com.pennant.backend.service.inventorysettlement.InventorySettlementService;
import com.pennant.webui.inventorysettlement.inventorysettlement.model.InventorySettlementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/InventorySettlement/InventorySettlement/InventorySettlementList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class InventorySettlementListCtrl extends GFCBaseListCtrl<InventorySettlement> {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(InventorySettlementListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_InventorySettlementList;
	protected Borderlayout borderLayout_InventorySettlementList;
	protected Paging pagingInventorySettlementList;
	protected Listbox listBoxInventorySettlement;

	// List headers
	protected Listheader listheader_Id;
	protected Listheader listheader_BrokerCode;
	protected Listheader listheader_SettlementDate;

	// checkRights
	protected Button btnHelp;
	protected Button button_InventorySettlementList_NewInventorySettlement;
	protected Button button_InventorySettlementList_InventorySettlementSearch;
	
	
	private transient InventorySettlementService inventorySettlementService;

	protected Textbox brokerCode;
	protected Listbox sortOperator_BrokerCode;

	protected Datebox settlementDate;
	protected Listbox sortOperator_SettlementDate;


	/**
	 * default constructor.<br>
	 */
	public InventorySettlementListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InventorySettlement";
		super.pageRightName = "InventorySettlementList";
		super.tableName = "InventorySettlement_AView";
		super.queueTableName = "InventorySettlement_View";
		super.enquiryTableName = "InventorySettlement_TView";
	}

	
	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_InventorySettlementList(Event event) throws Exception {
		logger.debug("Entering");
	
		// Set the page level components.
		setPageComponents(window_InventorySettlementList, borderLayout_InventorySettlementList, listBoxInventorySettlement, pagingInventorySettlementList);
		setItemRender(new InventorySettlementListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InventorySettlementList_NewInventorySettlement, "button_InventorySettlementList_NewInventorySettlement", true);
		registerButton(button_InventorySettlementList_InventorySettlementSearch);
		
		registerField("Id", SortOrder.ASC);
		registerField("brokerCode", listheader_BrokerCode, SortOrder.NONE, brokerCode,
				sortOperator_BrokerCode, Operators.STRING);
		registerField("settlementDate", listheader_SettlementDate, SortOrder.NONE, settlementDate,
				sortOperator_SettlementDate, Operators.STRING);
		
		this.settlementDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
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
	public void onClick$button_InventorySettlementList_InventorySettlementSearch(Event event) {
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
	public void onClick$button_InventorySettlementList_NewInventorySettlement(Event event) throws Exception {
		logger.debug("Entering");

		// Create a new entity.
		InventorySettlement inventorySettlement = new InventorySettlement();
		inventorySettlement.setNewRecord(true);
		inventorySettlement.setWorkflowId(getWorkFlowId());
		
		// Display the dialog page.
		doShowDialogPage(inventorySettlement);

		logger.debug("Leaving");
	}
	
	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onInventorySettlementItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());
		
		// Get the selected record.
		Listitem selectedItem = this.listBoxInventorySettlement.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		InventorySettlement inventorySettlement  = inventorySettlementService.getInventorySettlementById(id);

		if(inventorySettlement==null){
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BrokerCode='" + inventorySettlement.getBrokerCode() + "' AND version=" + 
				inventorySettlement.getVersion() + " ";

		if (doCheckAuthority(inventorySettlement, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && inventorySettlement.getWorkflowId() == 0) {
				inventorySettlement.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(inventorySettlement);
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
	private void doShowDialogPage(InventorySettlement inventorySettlement) throws Exception {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("inventorySettlement", inventorySettlement);
		arg.put("inventorySettlementListCtrl", this);
		arg.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/InventorySettlement/InventorySettlementDialog.zul", null, arg);
		} catch (final Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
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
	 * When the inventorySettlement print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$print(Event event) throws InterruptedException {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the from approved radio button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the from work-flow radio button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		search();
	}
	

	public void setInventorySettlementService(InventorySettlementService inventorySettlementService) {
		this.inventorySettlementService = inventorySettlementService;
	}

}