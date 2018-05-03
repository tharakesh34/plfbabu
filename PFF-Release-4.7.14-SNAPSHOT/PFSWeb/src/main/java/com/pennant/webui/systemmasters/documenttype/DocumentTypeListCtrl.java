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
 * FileName    		:  DocumentTypeListCtrl.java                                            * 	  
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
package com.pennant.webui.systemmasters.documenttype;

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

import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.webui.systemmasters.documenttype.model.DocumentTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/DocumentType/DocumentTypeList.zul file.
 */
public class DocumentTypeListCtrl extends GFCBaseListCtrl<DocumentType> {
	private static final long serialVersionUID = -2450046413192453914L;
	private static final Logger logger = Logger.getLogger(DocumentTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DocumentTypeList;
	protected Borderlayout borderLayout_DocumentTypeList;
	protected Paging pagingDocumentTypeList;
	protected Listbox listBoxDocumentType;

	protected Textbox docTypeCode;
	protected Textbox docTypeDesc;
	protected Checkbox docIsMandatory;
	protected Checkbox docTypeIsActive;

	protected Listbox sortOperator_docTypeDesc;
	protected Listbox sortOperator_docTypeCode;
	protected Listbox sortOperator_docIsMandatory;
	protected Listbox sortOperator_docTypeIsActive;

	// List headers
	protected Listheader listheader_DocTypeCode;
	protected Listheader listheader_DocTypeDesc;
	protected Listheader listheader_DocIsMandatory;
	protected Listheader listheader_DocTypeIsActive;

	// checkRights
	protected Button button_DocumentTypeList_NewDocumentType;
	protected Button button_DocumentTypeList_DocumentTypeSearchDialog;

	private transient DocumentTypeService documentTypeService;

	/**
	 * default constructor.<br>
	 */
	public DocumentTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DocumentType";
		super.pageRightName = "DocumentTypeList";
		super.tableName = "BMTDocumentTypes_AView";
		super.queueTableName = "BMTDocumentTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DocumentTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_DocumentTypeList, borderLayout_DocumentTypeList, listBoxDocumentType,
				pagingDocumentTypeList);
		setItemRender(new DocumentTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DocumentTypeList_NewDocumentType, "button_DocumentTypeList_NewDocumentType", true);
		registerButton(button_DocumentTypeList_DocumentTypeSearchDialog);

		registerField("docTypeCode", listheader_DocTypeCode, SortOrder.ASC, docTypeCode, sortOperator_docTypeCode,
				Operators.STRING);
		registerField("docTypeDesc", listheader_DocTypeDesc, SortOrder.NONE, docTypeDesc, sortOperator_docTypeDesc,
				Operators.STRING);
		registerField("docTypeIsActive", listheader_DocTypeIsActive, SortOrder.NONE, docTypeIsActive,
				sortOperator_docTypeIsActive, Operators.BOOLEAN);
		registerField("docIsMandatory", listheader_DocIsMandatory, SortOrder.NONE, docIsMandatory,
				sortOperator_docIsMandatory, Operators.BOOLEAN);

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
	public void onClick$button_DocumentTypeList_DocumentTypeSearchDialog(Event event) {
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
	public void onClick$button_DocumentTypeList_NewDocumentType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		DocumentType documentType = new DocumentType();
		documentType.setNewRecord(true);
		documentType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(documentType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDocumentTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDocumentType.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		DocumentType documentType = documentTypeService.getDocumentTypeById(id);

		if (documentType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND DocTypeCode='" + documentType.getDocTypeCode() + "' AND version="
				+ documentType.getVersion() + " ";

		if (doCheckAuthority(documentType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && documentType.getWorkflowId() == 0) {
				documentType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(documentType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param documentType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DocumentType documentType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("documentType", documentType);
		arg.put("documentTypeListCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/DocumentType/DocumentTypeDialog.zul", null, arg);
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

	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
}