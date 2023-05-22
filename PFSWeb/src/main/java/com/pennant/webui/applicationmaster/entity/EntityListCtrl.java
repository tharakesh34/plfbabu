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
 * * FileName : EntityListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-06-2017 * * Modified Date
 * : 15-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.entity;

import java.util.Map;

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

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.webui.applicationmaster.entity.model.EntityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/Entity/EntityList.zul file.
 * 
 */
public class EntityListCtrl extends GFCBaseListCtrl<Entity> {
	private static final long serialVersionUID = 1L;

	protected Window window_EntityList;
	protected Borderlayout borderLayout_EntityList;
	protected Paging pagingEntityList;
	protected Listbox listBoxEntity;

	// List headers
	protected Listheader listheader_EntityCode;
	protected Listheader listheader_EntityDesc;
	protected Listheader listheader_Country;
	protected Listheader listheader_StateCode;
	protected Listheader listheader_CityCode;
	protected Listheader listheader_PinCode;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_EntityList_NewEntity;
	protected Button button_EntityList_EntitySearch;

	// Search Fields
	protected Textbox entityCode; // autowired
	protected Textbox entityDesc; // autowired
	protected Textbox country; // autowired
	protected Textbox stateCode; // autowired
	protected Textbox cityCode; // autowired
	protected Textbox pinCode; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_EntityCode;
	protected Listbox sortOperator_EntityDesc;
	protected Listbox sortOperator_Country;
	protected Listbox sortOperator_StateCode;
	protected Listbox sortOperator_CityCode;
	protected Listbox sortOperator_PinCode;
	protected Listbox sortOperator_Active;

	private transient EntityService entityService;

	/**
	 * default constructor.<br>
	 */
	public EntityListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Entity";
		super.pageRightName = "EntityList";
		super.tableName = "Entity_AView";
		super.queueTableName = "Entity_View";
		super.enquiryTableName = "Entity_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_EntityList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_EntityList, borderLayout_EntityList, listBoxEntity, pagingEntityList);
		setItemRender(new EntityListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_EntityList_EntitySearch);
		registerButton(button_EntityList_NewEntity, "button_EntityList_NewEntity", true);

		registerField("entityCode", listheader_EntityCode, SortOrder.NONE, entityCode, sortOperator_EntityCode,
				Operators.STRING);
		registerField("entityDesc", listheader_EntityDesc, SortOrder.NONE, entityDesc, sortOperator_EntityDesc,
				Operators.STRING);
		registerField("country", listheader_Country, SortOrder.NONE, country, sortOperator_Country, Operators.STRING);
		registerField("stateCode", listheader_StateCode, SortOrder.NONE, stateCode, sortOperator_StateCode,
				Operators.STRING);
		registerField("cityCode", listheader_CityCode, SortOrder.NONE, cityCode, sortOperator_CityCode,
				Operators.STRING);
		registerField("pinCode", listheader_PinCode, SortOrder.NONE, pinCode, sortOperator_PinCode, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_EntityList_EntitySearch(Event event) {
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
	public void onClick$button_EntityList_NewEntity(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Entity entity = new Entity();
		entity.setNewRecord(true);
		entity.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(entity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onEntityItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxEntity.getSelectedItem();
		final String entityCode = (String) selectedItem.getAttribute("entityCode");
		Entity entity = entityService.getEntity(entityCode);

		if (entity == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  EntityCode =?");

		if (doCheckAuthority(entity, whereCond.toString(), new Object[] { entity.getEntityCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && entity.getWorkflowId() == 0) {
				entity.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(entity);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param entity The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Entity entity) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("entity", entity);
		arg.put("entityListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Entity/EntityDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
}