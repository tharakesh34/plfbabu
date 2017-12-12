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
 * FileName    		:  CommodityDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.commodity.commoditydetail;

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

import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.service.finance.commodity.CommodityDetailService;
import com.pennant.webui.finance.commodity.commoditydetail.model.CommodityDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Finance.Commodity/CommodityDetail/CommodityDetailList.zul file.
 */
public class CommodityDetailListCtrl extends GFCBaseListCtrl<CommodityDetail> {
	private static final long serialVersionUID = -5124936298001620783L;
	private static final Logger logger = Logger.getLogger(CommodityDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL -file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CommodityDetailList;
	protected Borderlayout borderLayout_CommodityDetailList;
	protected Paging pagingCommodityDetailList;
	protected Listbox listBoxCommodityDetail;

	protected Listheader listheader_CommodityCode;
	protected Listheader listheader_CommodityName;
	protected Listheader listheader_CommodityUnitCode;
	protected Listheader listheader_CommodityUnitName;

	protected Button button_CommodityDetailList_NewCommodityDetail;
	protected Button button_CommodityDetailList_CommodityDetailSearchDialog;

	protected Textbox commodityCode;
	protected Textbox commodityName;
	protected Textbox commodityUnitCode;
	protected Textbox commodityUnitName;

	protected Listbox sortOperator_commodityCode;
	protected Listbox sortOperator_commodityName;
	protected Listbox sortOperator_commodityUnitCode;
	protected Listbox sortOperator_commodityUnitName;

	private transient CommodityDetailService commodityDetailService;

	/**
	 * default constructor.<br>
	 */
	public CommodityDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CommodityDetail";
		super.pageRightName = "CommodityDetailList";
		super.tableName = "FCMTCommodityDetail_AView";
		super.queueTableName = "FCMTCommodityDetail_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CommodityDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_CommodityDetailList, borderLayout_CommodityDetailList, listBoxCommodityDetail,
				pagingCommodityDetailList);
		setItemRender(new CommodityDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CommodityDetailList_NewCommodityDetail, "button_CommodityDetailList_NewCommodityDetail",
				true);
		registerButton(button_CommodityDetailList_CommodityDetailSearchDialog);

		registerField("commodityCode", listheader_CommodityCode, SortOrder.ASC, commodityCode,
				sortOperator_commodityCode, Operators.STRING);
		registerField("commodityName", listheader_CommodityName, SortOrder.NONE, commodityName,
				sortOperator_commodityName, Operators.STRING);
		registerField("commodityUnitCode", listheader_CommodityUnitCode, SortOrder.NONE, commodityUnitCode,
				sortOperator_commodityUnitCode, Operators.STRING);
		registerField("commodityUnitName", listheader_CommodityUnitName, SortOrder.NONE, commodityUnitName,
				sortOperator_commodityUnitName, Operators.STRING);

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
	public void onClick$button_CommodityDetailList_CommodityDetailSearchDialog(Event event) {
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
	public void onClick$button_CommodityDetailList_NewCommodityDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CommodityDetail commodityDetail = new CommodityDetail();
		commodityDetail.setNewRecord(true);
		commodityDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(commodityDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCommodityDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCommodityDetail.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String commodityUnitCode = (String) selectedItem.getAttribute("commodityUnitCode");

		CommodityDetail commodityDetail = commodityDetailService.getCommodityDetailById(id, commodityUnitCode);

		if (commodityDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CommodityCode='" + commodityDetail.getCommodityCode() + "' AND version="
				+ commodityDetail.getVersion() + " ";
		if (doCheckAuthority(commodityDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && commodityDetail.getWorkflowId() == 0) {
				commodityDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(commodityDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCommodityDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CommodityDetail aCommodityDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commodityDetail", aCommodityDetail);
		arg.put("commodityDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Commodity/CommodityDetail/CommodityDetailDialog.zul",
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

	public void setCommodityDetailService(CommodityDetailService commodityDetailService) {
		this.commodityDetailService = commodityDetailService;
	}

}