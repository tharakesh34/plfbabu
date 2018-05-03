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
 * FileName    		:  EMailTypeListCtrl.java                                               * 	  
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
package com.pennant.webui.systemmasters.emailtype;

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
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.systemmasters.EMailTypeService;
import com.pennant.webui.systemmasters.emailtype.model.EMailTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/EMailType/EMailTypeList.zul file.
 */
public class EMailTypeListCtrl extends GFCBaseListCtrl<EMailType> {
	private static final long serialVersionUID = 2308954215935933494L;
	private static final Logger logger = Logger.getLogger(EMailTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EMailTypeList;
	protected Borderlayout borderLayout_EMailTypeList;
	protected Paging pagingEMailTypeList;
	protected Listbox listBoxEMailType;

	// List headers
	protected Listheader listheader_EmailTypeCode;
	protected Listheader listheader_EmailTypeDesc;
	protected Listheader listheader_EmailTypePriority;
	protected Listheader listheader_EmailTypeIsActive;

	protected Textbox emailTypeCode;
	protected Textbox emailTypeDesc;
	protected Intbox emailTypePriority;
	protected Checkbox emailTypeIsActive;
	protected Row	   row_EmailTypePriority;

	protected Listbox sortOperator_emailTypeCode;
	protected Listbox sortOperator_emailTypeDesc;
	protected Listbox sortOperator_emailTypePriority;
	protected Listbox sortOperator_emailTypeIsActive;

	// checkRights
	protected Button button_EMailTypeList_NewEMailType;
	protected Button button_EMailTypeList_EMailTypeSearchDialog;

	private transient EMailTypeService eMailTypeService;

	/**
	 * default constructor.<br>
	 */
	public EMailTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "EMailType";
		super.pageRightName = "EMailTypeList";
		super.tableName = "BMTEMailTypes_AView";
		super.queueTableName = "BMTEMailTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_EMailTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_EMailTypeList, borderLayout_EMailTypeList, listBoxEMailType, pagingEMailTypeList);
		setItemRender(new EMailTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_EMailTypeList_NewEMailType, "button_EMailTypeList_NewEMailType", true);
		registerButton(button_EMailTypeList_EMailTypeSearchDialog);

		registerField("emailTypeCode", listheader_EmailTypeCode, SortOrder.ASC, emailTypeCode,
				sortOperator_emailTypeCode, Operators.STRING);
		registerField("emailTypeDesc", listheader_EmailTypeDesc, SortOrder.NONE, emailTypeDesc,
				sortOperator_emailTypeDesc, Operators.STRING);
		registerField("emailTypePriority", listheader_EmailTypePriority, SortOrder.NONE, emailTypePriority,
				sortOperator_emailTypePriority, Operators.NUMERIC);
		registerField("emailTypeIsActive", listheader_EmailTypeIsActive, SortOrder.NONE, emailTypeIsActive,
				sortOperator_emailTypeIsActive, Operators.BOOLEAN);
		
		if (ImplementationConstants.ALLOW_EMIALTYPE_PRIORITY) {
			this.row_EmailTypePriority.setVisible(true);
			this.listheader_EmailTypePriority.setVisible(true);
		} else {
			this.row_EmailTypePriority.setVisible(false);
			this.listheader_EmailTypePriority.setVisible(false);
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
	public void onClick$button_EMailTypeList_EMailTypeSearchDialog(Event event) {
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
	public void onClick$button_EMailTypeList_NewEMailType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		EMailType eMailType = new EMailType();
		eMailType.setNewRecord(true);
		eMailType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(eMailType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onEMailTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxEMailType.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		EMailType eMailType = eMailTypeService.getEMailTypeById(id);

		if (eMailType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND EmailTypeCode='" + eMailType.getEmailTypeCode() + "' AND version="
				+ eMailType.getVersion() + " ";

		if (doCheckAuthority(eMailType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && eMailType.getWorkflowId() == 0) {
				eMailType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(eMailType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param eMailType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(EMailType eMailType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("eMailType", eMailType);
		arg.put("eMailTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EMailType/EMailTypeDialog.zul", null, arg);
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

	public void setEMailTypeService(EMailTypeService eMailTypeService) {
		this.eMailTypeService = eMailTypeService;
	}
}