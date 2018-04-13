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
 * FileName    		:  AddressTypeListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.addresstype;

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
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.service.systemmasters.AddressTypeService;
import com.pennant.webui.systemmasters.addresstype.model.AddressTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/AddressType/AddressTypeList.zul file.
 */
public class AddressTypeListCtrl extends GFCBaseListCtrl<AddressType> {
	private static final long serialVersionUID = 1817958653208633892L;
	private static final Logger logger = Logger.getLogger(AddressTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AddressTypeList;
	protected Borderlayout borderLayout_AddressTypeList;
	protected Paging pagingAddressTypeList;
	protected Listbox listBoxAddressType;

	protected Textbox addrTypeCode;
	protected Textbox addrTypeDesc;
	protected Intbox addrTypePriority;
	protected Checkbox addrTypeFIRequired;
	protected Checkbox addrTypeIsActive;
	protected Row	   row_AddrTypePriority;

	protected Listbox sortOperator_addrTypeCode;
	protected Listbox sortOperator_addrTypeDesc;
	protected Listbox sortOperator_addrTypePriority;
	protected Listbox sortOperator_addrTypeFIRequired;
	protected Listbox sortOperator_addrTypeIsActive;

	// List headers
	protected Listheader listheader_AddrTypeCode;
	protected Listheader listheader_AddrTypeDesc;
	protected Listheader listheader_AddrTypePriority;
	protected Listheader listheader_AddrTypeFIRequired;
	protected Listheader listheader_AddrTypeIsActive;

	// checkRights
	protected Button button_AddressTypeList_NewAddressType;
	protected Button button_AddressTypeList_AddressTypeSearchDialog;

	private transient AddressTypeService addressTypeService;

	/**
	 * default constructor.<br>
	 */
	public AddressTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AddressType";
		super.pageRightName = "AddressTypeList";
		super.tableName = "BMTAddressTypes_AView";
		super.queueTableName = "BMTAddressTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_AddressTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_AddressTypeList, borderLayout_AddressTypeList, listBoxAddressType,
				pagingAddressTypeList);
		setItemRender(new AddressTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AddressTypeList_NewAddressType, "button_AddressTypeList_NewAddressType", true);
		registerButton(button_AddressTypeList_AddressTypeSearchDialog);

		registerField("addrTypeCode", listheader_AddrTypeCode, SortOrder.ASC, addrTypeCode, sortOperator_addrTypeCode,
				Operators.STRING);
		registerField("addrTypeDesc", listheader_AddrTypeDesc, SortOrder.NONE, addrTypeDesc, sortOperator_addrTypeDesc,
				Operators.STRING);
		registerField("addrTypePriority", listheader_AddrTypePriority, SortOrder.NONE, addrTypePriority, sortOperator_addrTypePriority, Operators.STRING);
		registerField("addrTypeFIRequired", listheader_AddrTypeFIRequired, SortOrder.NONE, addrTypeFIRequired, sortOperator_addrTypeFIRequired, Operators.BOOLEAN);
		registerField("addrTypeIsActive", listheader_AddrTypeIsActive, SortOrder.NONE, addrTypeIsActive, sortOperator_addrTypeIsActive, Operators.BOOLEAN);
		if (ImplementationConstants.ALLOW_ADDRESSTYPE_PRIORITY) {
			this.row_AddrTypePriority.setVisible(true);
			this.listheader_AddrTypePriority.setVisible(true);
		} else {
			this.row_AddrTypePriority.setVisible(false);
			this.listheader_AddrTypePriority.setVisible(false);
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
	public void onClick$button_AddressTypeList_AddressTypeSearchDialog(Event event) {
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
	public void onClick$button_AddressTypeList_NewAddressType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		AddressType addressType = new AddressType();
		addressType.setNewRecord(true);
		addressType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(addressType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onAddressTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAddressType.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		AddressType addressType = addressTypeService.getAddressTypeById(id);

		if (addressType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND AddrTypeCode='" + addressType.getAddrTypeCode() + "' AND version="
				+ addressType.getVersion() + " ";

		if (doCheckAuthority(addressType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && addressType.getWorkflowId() == 0) {
				addressType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(addressType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param addressType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AddressType addressType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("addressType", addressType);
		arg.put("addressTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/AddressType/AddressTypeDialog.zul", null, arg);
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

	public void setAddressTypeService(AddressTypeService addressTypeService) {
		this.addressTypeService = addressTypeService;
	}
}