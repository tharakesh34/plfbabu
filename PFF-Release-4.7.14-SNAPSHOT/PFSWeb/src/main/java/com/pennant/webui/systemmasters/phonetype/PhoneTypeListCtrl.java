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
 * FileName    		:  PhoneTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.phonetype;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
import com.pennant.webui.systemmasters.phonetype.model.PhoneTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/PhoneType/PhoneTypeList.zul file.
 */
public class PhoneTypeListCtrl extends GFCBaseListCtrl<PhoneType> {
	private static final long serialVersionUID = 5068208109885923909L;
	private static final Logger logger = Logger.getLogger(PhoneTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PhoneTypeList;
	protected Borderlayout borderLayout_PhoneTypeList;
	protected Paging pagingPhoneTypeList;
	protected Listbox listBoxPhoneType;

	protected Textbox phoneTypeCode;
	protected Textbox phoneTypeDesc;
	protected Intbox phoneTypePriority;
	protected Checkbox phoneTypeIsActive;
	protected Row	   row_PhoneTypePriority;

	protected Listbox sortOperator_phoneTypeDesc;
	protected Listbox sortOperator_phoneTypeCode;
	protected Listbox sortOperator_phoneTypePriority;
	protected Listbox sortOperator_phoneTypeIsActive;

	// List headers
	protected Listheader listheader_PhoneTypeCode;
	protected Listheader listheader_PhoneTypeDesc;
	protected Listheader listheader_PhoneTypePriority;
	protected Listheader listheader_PhoneTypeIsActive;

	// checkRights
	protected Button button_PhoneTypeList_NewPhoneType;
	protected Button button_PhoneTypeList_PhoneTypeSearchDialog;

	private transient PhoneTypeService phoneTypeService;

	/**
	 * default constructor.<br>
	 */
	public PhoneTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PhoneType";
		super.pageRightName = "PhoneTypeList";
		super.tableName = "BMTPhoneTypes_AView";
		super.queueTableName = "BMTPhoneTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PhoneTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_PhoneTypeList, borderLayout_PhoneTypeList, listBoxPhoneType, pagingPhoneTypeList);
		setItemRender(new PhoneTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PhoneTypeList_NewPhoneType, "button_PhoneTypeList_NewPhoneType", true);
		registerButton(button_PhoneTypeList_PhoneTypeSearchDialog);

		registerField("phoneTypeCode", listheader_PhoneTypeCode, SortOrder.ASC, phoneTypeCode,
				sortOperator_phoneTypeCode, Operators.STRING);
		registerField("phoneTypeDesc", listheader_PhoneTypeDesc, SortOrder.NONE, phoneTypeDesc,
				sortOperator_phoneTypeDesc, Operators.STRING);
		registerField("phoneTypePriority", listheader_PhoneTypePriority, SortOrder.NONE, phoneTypePriority,
				sortOperator_phoneTypePriority, Operators.NUMERIC);
		registerField("phoneTypeIsActive", listheader_PhoneTypeIsActive, SortOrder.NONE, phoneTypeIsActive,
				sortOperator_phoneTypeIsActive, Operators.BOOLEAN);
		if (ImplementationConstants.ALLOW_PHONETYPE_PRIORITY) {
			this.row_PhoneTypePriority.setVisible(true);
			this.listheader_PhoneTypePriority.setVisible(true);
		} else {
			this.row_PhoneTypePriority.setVisible(false);
			this.listheader_PhoneTypePriority.setVisible(false);
		}

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
	public void onClick$button_PhoneTypeList_PhoneTypeSearchDialog(Event event) {
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
	public void onClick$button_PhoneTypeList_NewPhoneType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		PhoneType phoneType = new PhoneType();
		phoneType.setNewRecord(true);
		phoneType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(phoneType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPhoneTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPhoneType.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		PhoneType phoneType = phoneTypeService.getPhoneTypeById(id);

		if (phoneType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PhoneTypeCode='" + phoneType.getPhoneTypeCode() + "' AND version="
				+ phoneType.getVersion() + " ";
		if (doCheckAuthority(phoneType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && phoneType.getWorkflowId() == 0) {
				phoneType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(phoneType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param phoneType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PhoneType phoneType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("phoneType", phoneType);
		arg.put("phoneTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/PhoneType/PhoneTypeDialog.zul", null, arg);
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

	public void setPhoneTypeService(PhoneTypeService phoneTypeService) {
		this.phoneTypeService = phoneTypeService;
	}
}