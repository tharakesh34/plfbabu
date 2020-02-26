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
 * FileName    		:  GenderListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.gender;

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

import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.service.systemmasters.GenderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.gender.model.GenderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Gender/GenderList.zul file.
 */
public class GenderListCtrl extends GFCBaseListCtrl<Gender> {
	private static final long serialVersionUID = 3226455931949186314L;
	private static final Logger logger = Logger.getLogger(GenderListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_GenderList;
	protected Borderlayout borderLayout_GenderList;
	protected Paging pagingGenderList;
	protected Listbox listBoxGender;

	protected Textbox genderCode;
	protected Textbox genderDesc;
	protected Checkbox genderIsActive;

	protected Listbox sortOperator_genderDesc;
	protected Listbox sortOperator_genderCode;
	protected Listbox sortOperator_genderIsActive;

	// List headers
	protected Listheader listheader_GenderCode;
	protected Listheader listheader_GenderDesc;
	protected Listheader listheader_GenderIsActive;

	// checkRights
	protected Button button_GenderList_NewGender;
	protected Button button_GenderList_GenderSearchDialog;

	private transient GenderService genderService;

	/**
	 * default constructor.<br>
	 */
	public GenderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Gender";
		super.pageRightName = "GenderList";
		super.tableName = "BMTGenders_AView";
		super.queueTableName = "BMTGenders_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("GenderCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_GenderList(Event event) {
		// Set the page level components.
		setPageComponents(window_GenderList, borderLayout_GenderList, listBoxGender, pagingGenderList);
		setItemRender(new GenderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_GenderList_NewGender, "button_GenderList_NewGender", true);
		registerButton(button_GenderList_GenderSearchDialog);

		registerField("genderCode", listheader_GenderCode, SortOrder.ASC, genderCode, sortOperator_genderCode,
				Operators.STRING);
		registerField("genderDesc", listheader_GenderDesc, SortOrder.NONE, genderDesc, sortOperator_genderDesc,
				Operators.STRING);
		registerField("genderIsActive", listheader_GenderIsActive, SortOrder.NONE, genderIsActive,
				sortOperator_genderIsActive, Operators.BOOLEAN);

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
	public void onClick$button_GenderList_GenderSearchDialog(Event event) {
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
	public void onClick$button_GenderList_NewGender(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Gender gender = new Gender();
		gender.setNewRecord(true);
		gender.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(gender);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onGenderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxGender.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Gender gender = genderService.getGenderById(id);

		if (gender == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where GenderCode=?";
		if (doCheckAuthority(gender, whereCond,new Object[]{gender.getGenderCode()})) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && gender.getWorkflowId() == 0) {
				gender.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(gender);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param gender
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Gender gender) {
		logger.debug("Entering");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("gender", gender);
		arg.put("genderListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Gender/GenderDialog.zul", null, arg);
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

	public void setGenderService(GenderService genderService) {
		this.genderService = genderService;
	}
}