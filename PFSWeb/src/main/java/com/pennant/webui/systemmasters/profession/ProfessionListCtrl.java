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
 * FileName    		:  ProfessionListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.profession;

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

import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.service.systemmasters.ProfessionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.profession.model.ProfessionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Profession/ProfessionList.zul file.
 */
public class ProfessionListCtrl extends GFCBaseListCtrl<Profession> {
	private static final long serialVersionUID = 269967917185319880L;
	private static final Logger logger = Logger.getLogger(ProfessionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProfessionList; 
	protected Borderlayout borderLayout_ProfessionList; 
	protected Paging pagingProfessionList; 
	protected Listbox listBoxProfession; 

	protected Textbox professionCode; 
	protected Textbox professionDesc; 
	protected Checkbox professionIsActive; 

	protected Listbox sortOperator_professionDesc; 
	protected Listbox sortOperator_professionCode; 
	protected Listbox sortOperator_professionIsActive; 

	// List headers
	protected Listheader listheader_ProfessionCode; 
	protected Listheader listheader_ProfessionDesc; 
	protected Listheader listheader_ProfessionSelfEmployee; 
	protected Listheader listheader_ProfessionIsActive; 

	// checkRights
	protected Button button_ProfessionList_NewProfession; 
	protected Button button_ProfessionList_ProfessionSearchDialog; 

	private transient ProfessionService professionService;

	/**
	 * default constructor.<br>
	 */
	public ProfessionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Profession";
		super.pageRightName = "ProfessionList";
		super.tableName = "BMTProfessions_AView";
		super.queueTableName = "BMTProfessions_View";
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilter(new Filter("professionCode",PennantConstants.NONE, Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProfessionList(Event event) {
		// Set the page level components.
		setPageComponents(window_ProfessionList, borderLayout_ProfessionList, listBoxProfession, pagingProfessionList);
		setItemRender(new ProfessionListModelItemRenderer());
		
		// Register buttons and fields.
		registerButton(button_ProfessionList_NewProfession, "button_ProfessionList_NewProfession", true);
		registerButton(button_ProfessionList_ProfessionSearchDialog);

		registerField("professionCode", listheader_ProfessionCode, SortOrder.ASC, professionCode,
				sortOperator_professionCode, Operators.STRING);
		registerField("professionDesc", listheader_ProfessionDesc, SortOrder.NONE, professionDesc,
				sortOperator_professionDesc, Operators.STRING);
		registerField("selfEmployee", listheader_ProfessionSelfEmployee, SortOrder.NONE);
		registerField("professionIsActive", listheader_ProfessionIsActive, SortOrder.NONE, professionIsActive,
				sortOperator_professionIsActive, Operators.BOOLEAN);

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
	public void onClick$button_ProfessionList_ProfessionSearchDialog(Event event) {
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
	public void onClick$button_ProfessionList_NewProfession(Event event) {
		logger.debug("Entering");
		
		// Create a new entity.
		Profession profession = new Profession();
		profession.setNewRecord(true);
		profession.setWorkflowId(getWorkFlowId());
		
		// Display the dialog page.
		doShowDialogPage(profession);
		
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onProfessionItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxProfession.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		Profession profession = professionService.getProfessionById(id);

		if (profession == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProfessionCode='" + profession.getProfessionCode() + "' AND version="
				+ profession.getVersion() + " ";
		if (doCheckAuthority(profession, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && profession.getWorkflowId() == 0) {
				profession.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(profession);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param profession
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Profession profession) {
		logger.debug("Entering");
		
		Map<String, Object> arg = getDefaultArguments();
		arg.put("profession", profession);
		arg.put("professionListCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Profession/ProfessionDialog.zul", null, arg);
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

	public void setProfessionService(ProfessionService professionService) {
		this.professionService = professionService;
	}
}