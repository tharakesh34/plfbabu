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
 * FileName    		:  CustomerPRelationListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customerprelation;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.service.customermasters.CustomerPRelationService;
import com.pennant.webui.customermasters.customerprelation.model.CustomerPRelationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationList.zul file.
 */
public class CustomerPRelationListCtrl extends GFCBaseListCtrl<CustomerPRelation> {
	private static final long serialVersionUID = 823316129893394604L;
	private static final Logger logger = Logger.getLogger(CustomerPRelationListCtrl.class);

	protected Window window_CustomerPRelationList;
	protected Borderlayout borderLayout_CustomerPRelationList;
	protected Paging pagingCustomerPRelationList;
	protected Listbox listBoxCustomerPRelation;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_PRCustPRSNo;
	protected Listheader listheader_PRRelationCode;
	protected Listheader listheader_PRRelationCustID;
	protected Listheader listheader_PRisGuardian;
	protected Listheader listheader_PRSName;

	protected Textbox pRCustCIF;
	protected Intbox pRCustPRSNo;
	protected Textbox pRRelationCode;
	protected Textbox pRRelationCustID;
	protected Checkbox pRisGuardian;
	protected Textbox pRSName;
	
	protected Listbox sortOperator_pRSName;
	protected Listbox sortOperator_pRCustCIF;
	protected Listbox sortOperator_pRCustPRSNo;
	protected Listbox sortOperator_pRRelationCode;
	protected Listbox sortOperator_pRRelationCustID;
	protected Listbox sortOperator_pRisGuardian;

	protected Button button_CustomerPRelationList_NewCustomerPRelation;
	protected Button button_CustomerPRelationList_CustomerPRelationSearchDialog;

	private transient CustomerPRelationService customerPRelationService;

	/**
	 * default constructor.<br>
	 */
	public CustomerPRelationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustomerPRelation";
		super.pageRightName = "CustomerPRelationList";
		super.tableName = "CustomersPRelations_AView";
		super.queueTableName = "CustomersPRelations_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerPRelationList(Event event) {
		// Set the page level components.
		setPageComponents(window_CustomerPRelationList, borderLayout_CustomerPRelationList, listBoxCustomerPRelation,
				pagingCustomerPRelationList);
		setItemRender(new CustomerPRelationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CustomerPRelationList_NewCustomerPRelation,
				"button_CustomerPRelationList_NewCustomerPRelation", true);
		registerButton(button_CustomerPRelationList_CustomerPRelationSearchDialog);

		registerField("prcustId", listheader_CustCIF, SortOrder.ASC, pRCustCIF, sortOperator_pRCustCIF,
				Operators.STRING);
		registerField("pRCustPRSNo", listheader_PRCustPRSNo, SortOrder.NONE, pRCustPRSNo, sortOperator_pRCustPRSNo,
				Operators.NUMERIC);
		registerField("pRRelationCode", listheader_PRRelationCode, SortOrder.NONE, pRRelationCode,
				sortOperator_pRRelationCode, Operators.STRING);
		registerField("lovDescPRRelationCodeName");
		registerField("pRRelationCustID", listheader_PRRelationCustID, SortOrder.NONE, pRRelationCustID,
				sortOperator_pRRelationCustID, Operators.STRING);
		registerField("pRisGuardian", listheader_PRisGuardian, SortOrder.NONE, pRisGuardian, sortOperator_pRisGuardian,
				Operators.BOOLEAN);
		registerField("pRSName", listheader_PRSName, SortOrder.NONE, pRSName, sortOperator_pRSName, Operators.STRING);

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
	public void onClick$button_CustomerPRelationList_CustomerPRelationSearchDialog(Event event) {
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
	public void onClick$button_CustomerPRelationList_NewCustomerPRelation(Event event) {
		logger.debug("Entering");
		// create a new CustomerPRelation object, We GET it from the backEnd.
		final CustomerPRelation customerPRelation = new CustomerPRelation();
		customerPRelation.setNewRecord(true);
		customerPRelation.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(customerPRelation);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerPRelationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected CustomerPRelation object
		final Listitem item = this.listBoxCustomerPRelation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("id");
			int pRCustPRSNo = (int) item.getAttribute("PRCustPRSNo");
			final CustomerPRelation customerPRelation = customerPRelationService.getCustomerPRelationById(id,
					pRCustPRSNo);
			if (customerPRelation == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND prcustid='" + customerPRelation.getPRCustID() + "' AND version="
					+ customerPRelation.getVersion() + " ";

			if (doCheckAuthority(customerPRelation, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && customerPRelation.getWorkflowId() == 0) {
					customerPRelation.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(customerPRelation);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerPRelation customerPRelation) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("customerPRelation", customerPRelation);
		arg.put("customerPRelationListCtrl", this);
		arg.put("newRecord", customerPRelation.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
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

	public void setCustomerPRelationService(CustomerPRelationService customerPRelationService) {
		this.customerPRelationService = customerPRelationService;
	}
}