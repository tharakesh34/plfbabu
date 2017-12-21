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
 * FileName    		:  RatingTypeListCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.ratingtype;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.bmtmasters.RatingTypeService;
import com.pennant.webui.bmtmasters.ratingtype.model.RatingTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/BMTMasters/RatingType/RatingTypeList.zul file.
 */
public class RatingTypeListCtrl extends GFCBaseListCtrl<RatingType> {
	private static final long serialVersionUID = -342231205402716010L;
	private static final Logger logger = Logger.getLogger(RatingTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RatingTypeList;
	protected Borderlayout borderLayout_RatingTypeList;
	protected Paging pagingRatingTypeList;
	protected Listbox listBoxRatingType;

	// List headers
	protected Listheader listheader_RatingType;
	protected Listheader listheader_RatingTypeDesc;
	protected Listheader listheader_ValueType;
	protected Listheader listheader_ValueLen;
	protected Listheader listheader_RatingIsActive;

	protected Textbox ratingType;
	protected Textbox ratingTypeDesc;
	protected Checkbox valueType;
	protected Intbox valueLen;
	protected Checkbox ratingIsActive;

	protected Listbox sortOperator_ratingType;
	protected Listbox sortOperator_ratingTypeDesc;
	protected Listbox sortOperator_valueType;
	protected Listbox sortOperator_valueLen;
	protected Listbox sortOperator_ratingIsActive;

	// checkRights
	protected Button button_RatingTypeList_NewRatingType;
	protected Button button_RatingTypeList_RatingTypeSearchDialog;

	private transient RatingTypeService ratingTypeService;

	/**
	 * default constructor.<br>
	 */
	public RatingTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "RatingType";
		super.pageRightName = "RatingTypeList";
		super.tableName = "BMTRatingTypes_AView";
		super.queueTableName = "BMTRatingTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_RatingTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_RatingTypeList, borderLayout_RatingTypeList, listBoxRatingType, pagingRatingTypeList);
		setItemRender(new RatingTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_RatingTypeList_NewRatingType, "button_RatingTypeList_NewRatingType", true);
		registerButton(button_RatingTypeList_RatingTypeSearchDialog);

		registerField("ratingType", listheader_RatingType, SortOrder.ASC, ratingType, sortOperator_ratingType,
				Operators.STRING);
		registerField("ratingTypeDesc", listheader_RatingTypeDesc, SortOrder.NONE, ratingTypeDesc,
				sortOperator_ratingTypeDesc, Operators.STRING);
		registerField("valueType", listheader_ValueType, SortOrder.NONE, valueType, sortOperator_valueType,
				Operators.BOOLEAN);
		registerField("valueLen", listheader_ValueLen, SortOrder.NONE, valueLen, sortOperator_valueLen, Operators.NUMERIC);
		registerField("ratingIsActive", listheader_RatingIsActive, SortOrder.NONE, ratingIsActive,
				sortOperator_ratingIsActive, Operators.BOOLEAN);

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
	public void onClick$button_RatingTypeList_RatingTypeSearchDialog(Event event) {
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
	public void onClick$button_RatingTypeList_NewRatingType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		RatingType ratingType = new RatingType();
		ratingType.setNewRecord(true);
		ratingType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(ratingType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onRatingTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxRatingType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		RatingType aRatingType = ratingTypeService.getRatingTypeById(id);

		if (aRatingType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND RatingType='" + aRatingType.getRatingType() + "' AND version="
				+ aRatingType.getVersion() + " ";

		if (doCheckAuthority(aRatingType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aRatingType.getWorkflowId() == 0) {
				aRatingType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aRatingType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aRatingType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(RatingType aRatingType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("ratingType", aRatingType);
		arg.put("ratingTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/BMTMasters/RatingType/RatingTypeDialog.zul", null, arg);
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

	public void setRatingTypeService(RatingTypeService ratingTypeService) {
		this.ratingTypeService = ratingTypeService;
	}
}