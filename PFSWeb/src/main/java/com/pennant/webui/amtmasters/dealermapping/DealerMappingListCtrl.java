/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VehicleDealerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * *
 * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * 01-05-2018 Vinay 0.2 Module code added for individual module * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.amtmasters.dealermapping;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennant.backend.service.dealermapping.DealerMappingService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.dealermapping.webui.DealerMappingListModelItemRenderer;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMaster/VehicleDealer/VehicleDealerList.zul file.
 */
public class DealerMappingListCtrl extends GFCBaseListCtrl<DealerMapping> {
	private static final long serialVersionUID = 259921702952389829L;
	private static final Logger logger = LogManager.getLogger(DealerMappingListCtrl.class);

	protected Window window_dealerMappingList;
	protected Borderlayout borderLayout_DealerMappingList;
	protected Paging pagingDealerMappingList;
	protected Listbox listBoxDealerMapping;

	protected Listheader listheader_MerchantName;
	protected Listheader listheader_StoreName;
	protected Listheader listheader_StoreAddress;
	protected Listheader listheader_StoreCity;
	protected Listheader listheader_StoreID;
	protected Listheader listheader_DealerCode;
	protected Listheader listheader_Active;

	protected Button button_DealerMappingList_NewDealerMapping;
	protected Button button_DealerMappingList_DealerMappingSearchDialog;

	protected Textbox merchantName;
	protected Textbox storeName;
	protected Textbox storeAddress;
	protected Textbox storeCity;
	protected Textbox storeId;
	protected Textbox dealerCode;
	protected Checkbox active;

	protected Listbox sortOperator_merchantName;
	protected Listbox sortOperator_storeName;

	private transient DealerMappingService dealerMappingService;

	/**
	 * default constructor.<br>
	 */
	public DealerMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DealerMapping";
		super.pageRightName = "DealerMappingList";
		super.tableName = "CD_DealerMapping_VIEW";
		super.queueTableName = "CD_DealerMapping_VIEW";
		super.enquiryTableName = "CD_DealerMapping_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_dealerMappingList(Event event) {
		// Set the page level components.
		setPageComponents(window_dealerMappingList, borderLayout_DealerMappingList, listBoxDealerMapping,
				pagingDealerMappingList);
		setItemRender(new DealerMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DealerMappingList_NewDealerMapping, "button_DealerMappingList_NewDealerMapping", true);
		registerButton(button_DealerMappingList_DealerMappingSearchDialog);

		registerField("merchantName", listheader_MerchantName, SortOrder.NONE, merchantName, sortOperator_merchantName,
				Operators.STRING);
		registerField("storeId", listheader_StoreName, SortOrder.NONE, storeName, sortOperator_storeName,
				Operators.STRING);
		// registerField("merchantName");
		registerField("storeName");
		registerField("storeCity");
		registerField("storeAddress");
		registerField("dealerCode");
		registerField("posId");
		registerField("active");
		registerField("DealerMapId");

		doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_DealerMappingList_DealerMappingSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_DealerMappingList_NewDealerMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		DealerMapping dealerMapping = new DealerMapping();
		dealerMapping.setNewRecord(true);
		dealerMapping.setWorkflowId(getWorkFlowId());
		dealerMapping.setDealerCode(dealerMappingService.getDealerCode());

		// Display the dialog page.
		doShowDialogPage(dealerMapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onDealerMappingItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxDealerMapping.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("dealerMapId");
		DealerMapping dealerMapping = dealerMappingService.getDealerMappingById(id);

		if (dealerMapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where DealerMapId=?";

		if (doCheckAuthority(dealerMapping, whereCond, new Object[] { dealerMapping.getDealerMapId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dealerMapping.getWorkflowId() == 0) {
				dealerMapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dealerMapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param dealerMapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DealerMapping dealerMapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("dealerMapping", dealerMapping);
		arg.put("dealerMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/DealerMapping/DealerMappingDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.merchantName.setMaxlength(100);
		this.storeName.setMaxlength(100);
		this.recordStatus.setMaxlength(50);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setDealerMappingService(DealerMappingService dealerMappingService) {
		this.dealerMappingService = dealerMappingService;
	}

}