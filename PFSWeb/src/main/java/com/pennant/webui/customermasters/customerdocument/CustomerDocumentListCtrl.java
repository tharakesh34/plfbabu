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
 * FileName    		:  CustomerDocumentListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customerdocument;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.customermasters.customerdocument.model.CustomerDocumentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentList.zul file.
 */
public class CustomerDocumentListCtrl extends GFCBaseListCtrl<CustomerDocument> {
	private static final long serialVersionUID = 6734364667796871684L;
	private static final Logger logger = Logger.getLogger(CustomerDocumentListCtrl.class);

	protected Window window_CustomerDocumentList;
	protected Borderlayout borderLayout_CustomerDocumentList;
	protected Paging pagingCustomerDocumentList;
	protected Listbox listBoxCustomerDocument;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustDocType;
	protected Listheader listheader_CustDocTitle;
	protected Listheader listheader_CustDocSysName;

	protected Textbox custCIF;
	protected Textbox custDocType;
	protected Textbox custDocTitle;
	protected Textbox custDocSysName;

	protected Listbox sortOperator_custDocSysName;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custDocType;
	protected Listbox sortOperator_custDocTitle;

	protected Label label_CustomerDocumentSearch_RecordStatus;
	protected Label label_CustomerDocumentSearch_RecordType;
	protected Label label_CustomerDocumentSearchResult;

	protected Button button_CustomerDocumentList_NewCustomerDocument;
	protected Button button_CustomerDocumentList_CustomerDocumentSearchDialog;

	private transient CustomerDocumentService customerDocumentService;

	/**
	 * default constructor.<br>
	 */
	public CustomerDocumentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerDocument";
		super.pageRightName = "CustomerDocumentList";
		super.tableName = "CustomerDocuments_AView";
		super.queueTableName = "CustomerDocuments_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerDocumentList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerDocumentList, borderLayout_CustomerDocumentList, listBoxCustomerDocument,
				pagingCustomerDocumentList);
		setItemRender(new CustomerDocumentListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerDocumentList_NewCustomerDocument,
				"button_CustomerDocumentList_NewCustomerDocument", true);
		registerButton(button_CustomerDocumentList_CustomerDocumentSearchDialog);

		registerField("custId");
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("custDocType", listheader_CustDocType, SortOrder.NONE, custDocType, sortOperator_custDocType,
				Operators.STRING);
		registerField("custDocTitle", listheader_CustDocTitle, SortOrder.NONE, custDocTitle, sortOperator_custDocTitle,
				Operators.STRING);
		registerField("custDocSysName", listheader_CustDocSysName, SortOrder.NONE, custDocSysName,
				sortOperator_custDocSysName, Operators.STRING);
		registerField("lovDescCustDocCategory");
		registerField("custDocCategory");
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
	public void onClick$button_CustomerDocumentList_CustomerDocumentSearchDialog(Event event) throws Exception {
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
	public void onClick$button_CustomerDocumentList_NewCustomerDocument(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setNewRecord(true);
		customerDocument.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerDocument);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerDocumentItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCustomerDocument.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		String docCategory = (String) selectedItem.getAttribute("docCategory");
		final CustomerDocument customerDocument = customerDocumentService.getCustomerDocumentById(id, docCategory);

		if (customerDocument == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND custId='" + customerDocument.getCustID() + "' AND version="
				+ customerDocument.getVersion() + " ";

		if (doCheckAuthority(customerDocument, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && customerDocument.getWorkflowId() == 0) {
				customerDocument.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(customerDocument);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerDocument
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerDocument customerDocument) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerDocument", customerDocument);
		arg.put("customerDocumentListCtrl", this);
		arg.put("newRecord", customerDocument.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul",
					null, arg);
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

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilterNotEqual("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW);

	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}
}