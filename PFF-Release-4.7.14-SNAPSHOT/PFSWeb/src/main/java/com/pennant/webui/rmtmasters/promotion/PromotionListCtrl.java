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
 * FileName    		:  PromotionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.promotion;

import java.io.Serializable;
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

import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.rmtmasters.promotion.model.PromotionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Promotion/Promotion/PromotionList.zul file.<br>
 * 
 */
public class PromotionListCtrl extends GFCBaseListCtrl<Promotion> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PromotionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PromotionList;
	protected Borderlayout borderLayout_PromotionList;
	protected Paging pagingPromotionList;
	protected Listbox listBoxPromotion;

	// List headers
	protected Listheader listheader_PromotionCode;
	protected Listheader listheader_PromotionDesc;
	protected Listheader listheader_FinType;
	protected Listheader listheader_PromotionStartDate;
	protected Listheader listheader_PromotionEndDate;

	// checkRights
	protected Button button_PromotionList_NewPromotion;
	protected Button button_PromotionList_PromotionSearch;

	protected Textbox promotionCode;
	protected Textbox promotionDesc;

	protected Listbox sortOperator_PromotionCode;
	protected Listbox sortOperator_PromotionDesc;

	private transient PromotionService promotionService;

	/**
	 * default constructor.<br>
	 */
	public PromotionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Promotion";
		super.pageRightName = "PromotionList";
		super.tableName = "Promotions_AView";
		super.queueTableName = "Promotions_View";
		super.enquiryTableName = "Promotions_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PromotionList(Event event) throws Exception {

		// Set the page level components.
		setPageComponents(window_PromotionList, borderLayout_PromotionList, listBoxPromotion, pagingPromotionList);
		setItemRender(new PromotionListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PromotionList_NewPromotion, "button_PromotionList_NewPromotion", true);
		registerButton(button_PromotionList_PromotionSearch);

		registerField("PromotionCode", listheader_PromotionCode, SortOrder.ASC, promotionCode,
				sortOperator_PromotionCode, Operators.STRING);
		registerField("PromotionDesc", listheader_PromotionDesc, SortOrder.ASC, promotionDesc,
				sortOperator_PromotionDesc, Operators.STRING);
		registerField("FinType", listheader_FinType, SortOrder.NONE);
		registerField("StartDate", listheader_PromotionStartDate, SortOrder.NONE);
		registerField("EndDate", listheader_PromotionEndDate, SortOrder.NONE);

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
	public void onClick$button_PromotionList_PromotionSearch(Event event) {
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
	public void onClick$button_PromotionList_NewPromotion(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Promotion promotion = new Promotion();
		promotion.setNewRecord(true);
		promotion.setWorkflowId(getWorkFlowId());
		
		Map<String, Object> arg = getDefaultArguments();
		arg.put("promotion", promotion);
		arg.put("promotionListCtrl", this);
		arg.put("role", getRole());

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/Promotion/SelectPromotionDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPromotionItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPromotion.getSelectedItem();

		// Get the selected entity.
		String promotionCode = (String) selectedItem.getAttribute("promotionCode");
		Promotion promotion = promotionService.getPromotionById(promotionCode, FinanceConstants.MODULEID_PROMOTION);

		if (promotion == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PromotionCode='" + promotion.getPromotionCode() + "' AND version=" + promotion.getVersion() + " ";

		if (doCheckAuthority(promotion, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && promotion.getWorkflowId() == 0) {
				promotion.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(promotion);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param promotion
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Promotion promotion) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("promotion", promotion);
		arg.put("promotionListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/Promotion/PromotionDialog.zul", null, arg);
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

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}
}