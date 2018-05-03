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
 * FileName    		:  CommodityBrokerDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.commodity.commoditybrokerdetail;

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

import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.service.finance.commodity.CommodityBrokerDetailService;
import com.pennant.webui.finance.commodity.commoditybrokerdetail.model.CommodityBrokerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance.Commodity/CommodityBrokerDetail
 * /CommodityBrokerDetailList.zul file.
 */
public class CommodityBrokerDetailListCtrl extends GFCBaseListCtrl<CommodityBrokerDetail> {
	private static final long serialVersionUID = -6540154685309200504L;
	private static final Logger logger = Logger.getLogger(CommodityBrokerDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CommodityBrokerDetailList;
	protected Borderlayout borderLayout_CommodityBrokerDetailList;
	protected Paging pagingCommodityBrokerDetailList;
	protected Listbox listBoxCommodityBrokerDetail;

	protected Listheader listheader_BrokerCode;
	protected Listheader listheader_BrokerCIF;
	protected Listheader listheader_BrokerName;
	protected Listheader listheader_BrokerFrom;
	protected Listheader listheader_BrokerCountry;

	protected Button button_CommodityBrokerDetailList_NewCommodityBrokerDetail;
	protected Button button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog;

	protected Textbox brokerCode;
	protected Textbox brokerCIF;
	protected Textbox brokerName;
	protected Datebox brokerFrom;
	protected Textbox brokerAddrCountry;

	protected Listbox sortOperator_brokerCode;
	protected Listbox sortOperator_brokerCIF;
	protected Listbox sortOperator_brokerName;
	protected Listbox sortOperator_brokerFrom;
	protected Listbox sortOperator_brokerAddrCountry;

	private transient CommodityBrokerDetailService commodityBrokerDetailService;

	/**
	 * default constructor.<br>
	 */
	public CommodityBrokerDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CommodityBrokerDetail";
		super.pageRightName = "CommodityBrokerDetailList";
		super.tableName = "FCMTBrokerDetail_AView";
		super.queueTableName = "FCMTBrokerDetail_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CommodityBrokerDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_CommodityBrokerDetailList, borderLayout_CommodityBrokerDetailList,
				listBoxCommodityBrokerDetail, pagingCommodityBrokerDetailList);
		setItemRender(new CommodityBrokerDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CommodityBrokerDetailList_NewCommodityBrokerDetail,
				"button_CommodityBrokerDetailList_NewCommodityBrokerDetail", true);
		registerButton(button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog);

		registerField("brokerCode", listheader_BrokerCode, SortOrder.ASC, brokerCode, sortOperator_brokerCode,
				Operators.STRING);
		registerField("lovDescBrokerCIF", listheader_BrokerCIF, SortOrder.NONE, brokerCIF, sortOperator_brokerCIF,
				Operators.STRING);
		registerField("lovDescBrokerShortName", listheader_BrokerName, SortOrder.NONE, brokerName,
				sortOperator_brokerName, Operators.STRING);
		this.brokerFrom.setFormat(DateFormat.SHORT_DATE.getPattern());
		registerField("brokerFrom", listheader_BrokerFrom, SortOrder.NONE, brokerFrom, sortOperator_brokerFrom,
				Operators.DATE);
		registerField("brokerAddrCountry", listheader_BrokerCountry, SortOrder.NONE, brokerAddrCountry,
				sortOperator_brokerAddrCountry, Operators.STRING);

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
	public void onClick$button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog(Event event) {
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
	public void onClick$button_CommodityBrokerDetailList_NewCommodityBrokerDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CommodityBrokerDetail commodityBrokerDetail = new CommodityBrokerDetail();
		commodityBrokerDetail.setNewRecord(true);
		commodityBrokerDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(commodityBrokerDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCommodityBrokerDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCommodityBrokerDetail.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CommodityBrokerDetail commodityBrokerDetail = commodityBrokerDetailService.getCommodityBrokerDetailById(id);

		if (commodityBrokerDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BrokerCode='" + commodityBrokerDetail.getBrokerCode() + "' AND version="
				+ commodityBrokerDetail.getVersion() + " ";
		if (doCheckAuthority(commodityBrokerDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && commodityBrokerDetail.getWorkflowId() == 0) {
				commodityBrokerDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(commodityBrokerDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCommodityBrokerDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CommodityBrokerDetail aCommodityBrokerDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commodityBrokerDetail", aCommodityBrokerDetail);
		arg.put("commodityBrokerDetailListCtrl", this);

		try {
			Executions
					.createComponents(
							"/WEB-INF/pages/Finance/Commodity/CommodityBrokerDetail/CommodityBrokerDetailDialog.zul",
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

	public void setCommodityBrokerDetailService(CommodityBrokerDetailService commodityBrokerDetailService) {
		this.commodityBrokerDetailService = commodityBrokerDetailService;
	}

}