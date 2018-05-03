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
 * FileName    		:  RatingCodeListCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.ratingcode;

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

import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.webui.bmtmasters.ratingcode.model.RatingCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/BMTMasters/RatingCode/RatingCodeList.zul file.
 */
public class RatingCodeListCtrl extends GFCBaseListCtrl<RatingCode> {
	private static final long serialVersionUID = 6738264839727215072L;
	private static final Logger logger = Logger.getLogger(RatingCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RatingCodeList;
	protected Borderlayout borderLayout_RatingCodeList;
	protected Paging pagingRatingCodeList;
	protected Listbox listBoxRatingCode;

	// List headers
	protected Listheader listheader_RatingType;
	protected Listheader listheader_RatingCode;
	protected Listheader listheader_RatingCodeDesc;
	protected Listheader listheader_RatingIsActive;

	protected Textbox ratingType;
	protected Textbox ratingCode;
	protected Textbox ratingCodeDesc;
	protected Checkbox ratingIsActive;

	protected Listbox sortOperator_ratingType;
	protected Listbox sortOperator_ratingCode;
	protected Listbox sortOperator_ratingCodeDesc;
	protected Listbox sortOperator_ratingIsActive;

	// checkRights
	protected Button button_RatingCodeList_NewRatingCode;
	protected Button button_RatingCodeList_RatingCodeSearchDialog;

	private transient RatingCodeService ratingCodeService;

	/**
	 * default constructor.<br>
	 */
	public RatingCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "RatingCode";
		super.pageRightName = "RatingCodeList";
		super.tableName = "BMTRatingCodes_AView";
		super.queueTableName = "BMTRatingCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_RatingCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_RatingCodeList, borderLayout_RatingCodeList, listBoxRatingCode, pagingRatingCodeList);
		setItemRender(new RatingCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_RatingCodeList_NewRatingCode, "button_RatingCodeList_NewRatingCode", true);
		registerButton(button_RatingCodeList_RatingCodeSearchDialog);

		registerField("ratingType", listheader_RatingType, SortOrder.ASC, ratingType, sortOperator_ratingType,
				Operators.STRING);
		registerField("ratingCode", listheader_RatingCode, SortOrder.NONE, ratingCode, sortOperator_ratingCode,
				Operators.STRING);
		registerField("ratingCodeDesc", listheader_RatingCodeDesc, SortOrder.NONE, ratingCodeDesc,
				sortOperator_ratingCodeDesc, Operators.STRING);
		registerField("ratingIsActive", listheader_RatingIsActive, SortOrder.NONE, ratingIsActive,
				sortOperator_ratingIsActive, Operators.BOOLEAN);
		registerField("lovDescRatingTypeName");

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
	public void onClick$button_RatingCodeList_RatingCodeSearchDialog(Event event) {
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
	public void onClick$button_RatingCodeList_NewRatingCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		RatingCode ratingCode = new RatingCode();
		ratingCode.setNewRecord(true);
		ratingCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(ratingCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onRatingCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxRatingCode.getSelectedItem();

		// Get the selected entity.
		String ratingType = (String) selectedItem.getAttribute("ratingType");
		String ratingCode = (String) selectedItem.getAttribute("ratingCode");

		RatingCode aRatingCode = ratingCodeService.getRatingCodeById(ratingType, ratingCode);

		if (aRatingCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND RatingType='" + aRatingCode.getRatingType() + "' AND version="
				+ aRatingCode.getVersion() + " ";

		if (doCheckAuthority(aRatingCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aRatingCode.getWorkflowId() == 0) {
				aRatingCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aRatingCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aRatingCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(RatingCode aRatingCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("ratingCode", aRatingCode);
		arg.put("ratingCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/BMTMasters/RatingCode/RatingCodeDialog.zul", null, arg);
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

	public void setRatingCodeService(RatingCodeService ratingCodeService) {
		this.ratingCodeService = ratingCodeService;
	}
}