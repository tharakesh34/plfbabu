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
 * FileName    		:  LimitStructureListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.limit.limitstructure;

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

import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.service.limit.LimitStructureService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.limit.limitstructure.model.LimitStructureListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Limit/LimitStructure/LimitStructureList.zul
 * file.<br>
 * ************************************************************<br>
 * 
 */
public class LimitStructureListCtrl extends GFCBaseListCtrl<LimitStructure> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LimitStructureListCtrl.class);

	/*
	 * ************************************************************************
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ************************************************************************
	 */
	protected Window 							window_LimitStructureList; 
	protected Paging 							pagingLimitStructureList; 
	protected Borderlayout 						borderLayout_LimitStructureList; 
	protected Listbox 							listBoxLimitStructure; 
	protected Textbox							limitStructureType;

	// List headers
	protected Listheader 						listheader_StructureCode; 
	protected Listheader 						listheader_StructureName; 
	protected Listheader						listheader_Active;


	// checkRights
	protected Button 							button_LimitStructureList_NewLimitStructure; 
	protected Button 							button_LimitStructureList_LimitStructureSearch; 

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<LimitStructure> 	searchObj;
	protected Textbox 							structureCode; 
	protected Listbox 							sortOperator_StructureCode; 
	protected Textbox 							structureName; 
	protected Listbox 							sortOperator_StructureName; 
	protected Listbox 							sortOperator_active;
	protected Checkbox 							active;

	private transient LimitStructureService 	limitStructureService;

	/**
	 * default constructor.<br>
	 */
	public LimitStructureListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "LimitStructure";
		super.pageRightName = "LimitStructureList";
		super.tableName = "LimitStructure_AView";
		super.queueTableName = "LimitStructure_View";
		super.enquiryTableName = "LimitStructure_AView";
	}
	@Override
	protected void doAddFilters() {

		super.doAddFilters();	

		searchObject.addFilterEqual("LimitCategory",limitStructureType.getValue());

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LimitStructureList(Event event) {
		// Set the page level components.
		setPageComponents(window_LimitStructureList, borderLayout_LimitStructureList, listBoxLimitStructure, pagingLimitStructureList);
		setItemRender(new LimitStructureListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LimitStructureList_NewLimitStructure, "button_LimitStructureList_NewLimitStructure", true);
		registerButton(button_LimitStructureList_LimitStructureSearch);


		registerField("structureCode", listheader_StructureCode, SortOrder.ASC, structureCode,
				sortOperator_StructureCode, Operators.STRING);
		registerField("structureName", listheader_StructureName, SortOrder.ASC, structureName,
				sortOperator_StructureName, Operators.STRING);	
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_active,
				Operators.BOOLEAN);

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
	public void onClick$button_LimitStructureList_LimitStructureSearch(Event event) {
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
	public void onClick$button_LimitStructureList_NewLimitStructure(Event event) throws Exception {
		logger.debug("Entering");

		// Create a new entity.
		LimitStructure limitStructure = new LimitStructure();
		limitStructure.setNewRecord(true);
		limitStructure.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(limitStructure);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onLimitStructureItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// Get the selected record.
		Listitem selectedItem = this.listBoxLimitStructure.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		LimitStructure limitStructure = limitStructureService.getLimitStructureById(id);

		if (limitStructure == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND StructureCode='" + limitStructure.getStructureCode()+ "'  AND version="+ limitStructure.getVersion() + " ";


		if (doCheckAuthority(limitStructure, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && limitStructure.getWorkflowId() == 0) {
				limitStructure.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(limitStructure);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LimitStructure aLimitStructure) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("limitStructure", aLimitStructure);		
		aLimitStructure.setLimitCategory(limitStructureType.getValue());
		arg.put("limitStructureListCtrl", this);
		arg.put("enqiryModule", super.enqiryModule);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Limit/LimitStructure/LimitStructureDialog.zul",null,arg);

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
	public void onClick$btnHelp(Event event) {
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

	public void setLimitStructureService(LimitStructureService limitStructureService) {
		this.limitStructureService = limitStructureService;
	}
}