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
 * FileName    		:  IndustryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.industry;

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

import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.service.systemmasters.IndustryService;
import com.pennant.webui.systemmasters.industry.model.IndustryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Industry/IndustryList.zul file.
 */
public class IndustryListCtrl extends GFCBaseListCtrl<Industry> {
	private static final long serialVersionUID = -5021713706640652581L;
	private final static Logger logger = Logger.getLogger(IndustryListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IndustryList;
	protected Borderlayout borderLayout_IndustryList;
	protected Paging pagingIndustryList;
	protected Listbox listBoxIndustry;

	protected Textbox industryCode;
	protected Textbox industryDesc;
	protected Checkbox industryIsActive;

	protected Listbox sortOperator_industryDesc;
	protected Listbox sortOperator_industryCode;
	protected Listbox sortOperator_industryLimit;
	protected Listbox sortOperator_industryIsActive;

	// List headers
	protected Listheader listheader_IndustryCode;
	protected Listheader listheader_IndustryDesc;
	protected Listheader listheader_IndustryLimit;
	protected Listheader listheader_IndustryIsActive;

	// checkRights
	protected Button button_IndustryList_NewIndustry;
	protected Button button_IndustryList_IndustrySearchDialog;

	private transient IndustryService industryService;

	/**
	 * default constructor.<br>
	 */
	public IndustryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Industry";
		super.pageRightName = "IndustryList";
		super.tableName = "BMTIndustries_AView";
		super.queueTableName = "BMTIndustries_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_IndustryList(Event event) {
		// Set the page level components.
		setPageComponents(window_IndustryList, borderLayout_IndustryList, listBoxIndustry, pagingIndustryList);
		setItemRender(new IndustryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_IndustryList_NewIndustry, "button_IndustryList_NewIndustry", true);
		registerButton(button_IndustryList_IndustrySearchDialog);

		registerField("industryCode", listheader_IndustryCode, SortOrder.ASC, industryCode, sortOperator_industryCode,
				Operators.STRING);
		registerField("industryDesc", listheader_IndustryDesc, SortOrder.NONE, industryDesc, sortOperator_industryDesc,
				Operators.STRING);
		registerField("industryIsActive", listheader_IndustryIsActive, SortOrder.NONE, industryIsActive,
				sortOperator_industryIsActive, Operators.BOOLEAN);

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
	public void onClick$button_IndustryList_IndustrySearchDialog(Event event) {
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
	public void onClick$button_IndustryList_NewIndustry(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Industry industry = new Industry();
		industry.setNewRecord(true);
		industry.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(industry);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onIndustryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxIndustry.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Industry industry = industryService.getIndustryById(id);

		if (industry == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND IndustryCode='" + industry.getIndustryCode() + "' AND version="
				+ industry.getVersion() + " ";
		if (doCheckAuthority(industry, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && industry.getWorkflowId() == 0) {
				industry.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(industry);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param industry
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Industry industry) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("industry", industry);
		arg.put("industryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Industry/IndustryDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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
	public void onClick$help(Event event) throws InterruptedException {
		doShowHelp(event);
	}

	public void setIndustryService(IndustryService industryService) {
		this.industryService = industryService;
	}
}