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
 * FileName    		:  SystemInternalAccountDefinitionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.masters.systeminternalaccountdefinition;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.service.masters.SystemInternalAccountDefinitionService;
import com.pennant.webui.masters.systeminternalaccountdefinition.model.SystemInternalAccountDefinitionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;

/**
 * This is the controller class for the /WEB-INF/pages/Account/SystemInternalAccountDefinition
 * /SystemInternalAccountDefinitionList.zul file.
 */
public class SystemInternalAccountDefinitionListCtrl extends GFCBaseListCtrl<SystemInternalAccountDefinition> {
	private static final long serialVersionUID = 5774729281482510692L;
	private final static Logger logger = Logger.getLogger(SystemInternalAccountDefinitionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SystemInternalAccountDefinitionList;
	protected Borderlayout borderLayout_SystemInternalAccountDefinitionList;
	protected Paging pagingSystemInternalAccountDefinitionList;
	protected Listbox listBoxSystemInternalAccountDefinition;

	protected Listheader listheader_SIACode;
	protected Listheader listheader_SIAName;
	protected Listheader listheader_SIAShortName;
	protected Listheader listheader_SIAAcType;
	protected Listheader listheader_SIANumber;

	protected Button button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition;
	protected Button button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog;

	protected Textbox sIACode;
	protected Textbox sIAName;
	protected Textbox sIAShortName;
	protected Textbox sIAAcType;
	protected Textbox sIANumber;

	protected Listbox sortOperator_sIACode;
	protected Listbox sortOperator_sIAName;
	protected Listbox sortOperator_sIAShortName;
	protected Listbox sortOperator_sIAAcType;
	protected Listbox sortOperator_sIANumber;

	private transient SystemInternalAccountDefinitionService systemInternalAccountDefinitionService;

	/**
	 * default constructor.<br>
	 */
	public SystemInternalAccountDefinitionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SystemInternalAccountDefinition";
		super.pageRightName = "SystemInternalAccountDefinitionList";
		super.tableName = "SystemInternalAccountDef_AView";
		super.queueTableName = "SystemInternalAccountDef_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		if (StringUtils.isNotBlank(this.sIANumber.getValue())) {
			String sIANumberValue = this.sIANumber.getValue();
			sIANumberValue = "00" + sIANumberValue;
			searchObject.addFilter(SearchFilterControl.getFilter("sIANumber", sIANumberValue, sortOperator_sIANumber));
		}
	}

	@Override
	protected void doReset() {
		super.doReset();
		SearchFilterControl.resetFilters(sIANumber, sortOperator_sIANumber);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SystemInternalAccountDefinitionList(Event event) {
		// Set the page level components.
		setPageComponents(window_SystemInternalAccountDefinitionList, borderLayout_SystemInternalAccountDefinitionList,
				listBoxSystemInternalAccountDefinition, pagingSystemInternalAccountDefinitionList);
		setItemRender(new SystemInternalAccountDefinitionListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition,
				"button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition", true);
		registerButton(button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog);

		registerField("SIACode", listheader_SIACode, SortOrder.ASC, sIACode, sortOperator_sIACode, Operators.STRING);
		registerField("sIAName", listheader_SIAName, SortOrder.NONE, sIAName, sortOperator_sIAName, Operators.STRING);
		registerField("sIAShortName", listheader_SIAShortName, SortOrder.NONE, sIAShortName, sortOperator_sIAShortName,
				Operators.STRING);
		registerField("sIAAcType", listheader_SIAAcType, SortOrder.NONE, sIAAcType, sortOperator_sIAAcType,
				Operators.STRING);
		registerField("sIANumber", listheader_SIANumber, SortOrder.NONE);

		SearchFilterControl.renderOperators(this.sortOperator_sIANumber, Operators.STRING);

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
	public void onClick$button_SystemInternalAccountDefinitionList_SystemInternalAccountDefinitionSearchDialog(
			Event event) {
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
	public void onClick$button_SystemInternalAccountDefinitionList_NewSystemInternalAccountDefinition(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SystemInternalAccountDefinition systemInternalAccountDefinition = new SystemInternalAccountDefinition();
		systemInternalAccountDefinition.setNewRecord(true);
		systemInternalAccountDefinition.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(systemInternalAccountDefinition);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSystemInternalAccountDefinitionItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSystemInternalAccountDefinition.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		SystemInternalAccountDefinition systemInternalAccountDefinition = systemInternalAccountDefinitionService
				.getSystemInternalAccountDefinitionById(id);

		if (systemInternalAccountDefinition == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SIACode='" + systemInternalAccountDefinition.getSIACode() + "' AND version="
				+ systemInternalAccountDefinition.getVersion() + " ";

		if (doCheckAuthority(systemInternalAccountDefinition, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && systemInternalAccountDefinition.getWorkflowId() == 0) {
				systemInternalAccountDefinition.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(systemInternalAccountDefinition);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSystemInternalAccountDefinition
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SystemInternalAccountDefinition aSystemInternalAccountDefinition) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("systemInternalAccountDefinition", aSystemInternalAccountDefinition);
		arg.put("systemInternalAccountDefinitionListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Account/SystemInternalAccountDefinition/SystemInternalAccountDefinitionDialog.zul",
					null, arg);
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
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setSystemInternalAccountDefinitionService(
			SystemInternalAccountDefinitionService systemInternalAccountDefinitionService) {
		this.systemInternalAccountDefinitionService = systemInternalAccountDefinitionService;
	}

}