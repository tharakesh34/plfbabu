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
 * FileName    		:  CommodityInventoryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2015    														*
 *                                                                  						*
 * Modified Date    :  23-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.commodity.commodityinventory;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.service.commodity.CommodityInventoryService;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.commodity.commodityinventory.model.CommodityInventoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Commodity/CommodityInventory/CommodityInventoryList.zul file.
 */
public class CommodityInventoryListCtrl extends GFCBaseListCtrl<CommodityInventory> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CommodityInventoryListCtrl.class);

	protected Window window_CommodityInventoryList;
	protected Borderlayout borderLayout_CommodityInventoryList;
	protected Paging pagingCommodityInventoryList;
	protected Listbox listBoxCommodityInventory;

	protected Listheader listheader_BrokerCode;
	protected Listheader listheader_HoldCertificateNo;
	protected Listheader listheader_CommodityCode;
	protected Listheader listheader_PurchaseDate;
	protected Listheader listheader_FinalSettlementDate;
	protected Listheader listheader_PurchaseAmount;
	protected Listheader listheader_Quantity;
	protected Listheader listheader_BulkPurchase;
	protected Listheader listheader_RequestStage;

	protected Textbox brokerCode;
	protected Textbox holdCertificateNo;
	protected Combobox commodityCode;
	protected Datebox purchaseDate;
	protected Datebox finalSettlementDate;
	protected Combobox requestStage;

	protected Listbox sortOperator_BrokerCode;
	protected Listbox sortOperator_HoldCertificateNo;
	protected Listbox sortOperator_CommodityCode;
	protected Listbox sortOperator_PurchaseDate;
	protected Listbox sortOperator_FinalSettlementDate;
	protected Listbox sortOperator_RequestStage;

	protected Button button_CommodityInventoryList_NewCommodityInventory;
	protected Button button_CommodityInventoryList_CommodityInventorySearch;

	private transient CommodityInventoryService commodityInventoryService;

	protected Label label_CommodityInventoryList_RequestStage;
	private List<ValueLabel> listCommodityCode = PennantAppUtil.getCommodityCodes();

	/**
	 * default constructor.<br>
	 */
	public CommodityInventoryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CommodityInventory";
		super.pageRightName = "CommodityInventoryList";
		super.tableName = "FCMTCommodityInventory_AView";
		super.queueTableName = "FCMTCommodityInventory_View";
		super.enquiryTableName = "FCMTCommodityInventory_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CommodityInventoryList(Event event) {
		// Set the page level components.
		setPageComponents(window_CommodityInventoryList, borderLayout_CommodityInventoryList,
				listBoxCommodityInventory, pagingCommodityInventoryList);
		setItemRender(new CommodityInventoryListModelItemRenderer());

		fillComboBox(this.commodityCode, "", listCommodityCode, "");

		registerButton(button_CommodityInventoryList_NewCommodityInventory,
				"button_CommodityInventoryList_NewCommodityInventory", true);
		registerButton(button_CommodityInventoryList_CommodityInventorySearch);

		registerField("CommodityInvId", SortOrder.ASC);
		registerField("brokerCode", listheader_BrokerCode, SortOrder.NONE, brokerCode, sortOperator_BrokerCode,
				Operators.NUMERIC);
		registerField("holdCertificateNo", listheader_HoldCertificateNo, SortOrder.NONE, holdCertificateNo,
				sortOperator_HoldCertificateNo, Operators.STRING);
		registerField("lovDescCommodityDesc", listheader_CommodityCode, SortOrder.NONE, commodityCode,
				sortOperator_CommodityCode, Operators.SIMPLE_NUMARIC);
		registerField("purchaseDate", listheader_PurchaseDate, SortOrder.NONE, purchaseDate, sortOperator_PurchaseDate,
				Operators.DATE);
		registerField("finalSettlementDate", listheader_FinalSettlementDate, SortOrder.NONE, finalSettlementDate,
				sortOperator_FinalSettlementDate, Operators.DATE);
		registerField("purchaseAmount", listheader_PurchaseAmount, SortOrder.NONE);
		registerField("unitPrice");
		registerField("quantity", listheader_Quantity, SortOrder.NONE);
		registerField("bulkPurchase", listheader_BulkPurchase, SortOrder.NONE);
		registerField("CcyEditField");

		if (isWorkFlowEnabled()) {
			Filter[] filters = new Filter[1];
			filters[0] = Filter.in("RoleCd", getUserWorkspace().getUserRoles());
			fillComboBox(this.requestStage, "", PennantAppUtil.getSecRolesList(filters), "");

			registerField("LovDescRequestStage", listheader_RequestStage, SortOrder.NONE, requestStage,
					sortOperator_RequestStage, Operators.SIMPLE_NUMARIC);
		} else {
			this.requestStage.setVisible(false);
			this.label_CommodityInventoryList_RequestStage.setVisible(false);
			this.sortOperator_RequestStage.setVisible(false);
			this.listheader_RequestStage.setVisible(false);

			registerField("LovDescRequestStage");
		}

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
	public void onClick$button_CommodityInventoryList_CommodityInventorySearch(Event event) {
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
	public void onClick$button_CommodityInventoryList_NewCommodityInventory(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CommodityInventory commodityInventory = new CommodityInventory();
		commodityInventory.setNewRecord(true);
		commodityInventory.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(commodityInventory);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCommodityInventoryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCommodityInventory.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		CommodityInventory commodityInventory = commodityInventoryService.getCommodityInventoryById(id);

		if (commodityInventory == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CommodityInvId='" + commodityInventory.getCommodityInvId() + "' AND version="
				+ commodityInventory.getVersion() + " ";

		if (doCheckAuthority(commodityInventory, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && commodityInventory.getWorkflowId() == 0) {
				commodityInventory.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(commodityInventory);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param commodityInventory
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CommodityInventory commodityInventory) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commodityInventory", commodityInventory);
		arg.put("commodityInventoryListCtrl", this);
		arg.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Commodity/CommodityInventory/CommodityInventoryDialog.zul",
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

	public void setCommodityInventoryService(CommodityInventoryService commodityInventoryService) {
		this.commodityInventoryService = commodityInventoryService;
	}
}