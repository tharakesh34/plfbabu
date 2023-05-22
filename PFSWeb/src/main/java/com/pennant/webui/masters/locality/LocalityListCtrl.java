/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : LocalityListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * * Modified
 * Date : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.masters.locality;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.masters.Locality;
import com.pennant.backend.service.masters.LocalityService;
import com.pennant.webui.masters.locality.model.LocalityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.masters/Locality/LocalityList.zul file.
 * 
 */
public class LocalityListCtrl extends GFCBaseListCtrl<Locality> {
	private static final long serialVersionUID = 1L;

	protected Window window_LocalityList;
	protected Borderlayout borderLayout_LocalityList;
	protected Paging pagingLocalityList;
	protected Listbox listBoxLocality;

	// List headers
	protected Listheader listheader_id;
	protected Listheader listheader_name;
	protected Listheader listheader_city;

	// checkRights
	protected Button button_LocalityList_NewLocality;
	protected Button button_LocalityList_LocalitySearch;

	// Search Fields
	protected Longbox id; // autowired
	protected Textbox name; // autowired
	protected Textbox city; // autowired

	protected Listbox sortOperator_id;
	protected Listbox sortOperator_name;
	protected Listbox sortOperator_city;

	private transient LocalityService localityService;

	/**
	 * default constructor.<br>
	 */
	public LocalityListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Locality";
		super.pageRightName = "LocalityList";
		super.tableName = "Locality_AView";
		super.queueTableName = "Locality_View";
		super.enquiryTableName = "Locality_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LocalityList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LocalityList, borderLayout_LocalityList, listBoxLocality, pagingLocalityList);
		setItemRender(new LocalityListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LocalityList_LocalitySearch);
		registerButton(button_LocalityList_NewLocality, "button_LocalityList_NewLocality", true);

		registerField("id", listheader_id, SortOrder.NONE, id, sortOperator_id, Operators.NUMERIC);
		registerField("name", listheader_name, SortOrder.NONE, name, sortOperator_name, Operators.STRING);
		registerField("city", listheader_city, SortOrder.NONE, city, sortOperator_city, Operators.STRING);
		registerField("cityName");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_LocalityList_LocalitySearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_LocalityList_NewLocality(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Locality locality = new Locality();
		locality.setNewRecord(true);
		locality.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(locality);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onLocalityItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLocality.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		Locality locality = localityService.getLocality(id);

		if (locality == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =?");

		if (doCheckAuthority(locality, whereCond.toString(), new Object[] { locality.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && locality.getWorkflowId() == 0) {
				locality.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(locality);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param locality The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Locality locality) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("locality", locality);
		arg.put("localityListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/masters/Locality/LocalityDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
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

	public void setLocalityService(LocalityService localityService) {
		this.localityService = localityService;
	}
}