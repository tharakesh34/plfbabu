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
 * * FileName : CheckListListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * * Modified
 * Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.vasMovement;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.VasMovement;
import com.pennant.backend.service.applicationmaster.VasMovementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/VasMovement/VasMovementList.zul file.
 */
public class VasMovementListCtrl extends GFCBaseListCtrl<VasMovement> {
	private static final Logger logger = LogManager.getLogger(VasMovementListCtrl.class);
	private static final long serialVersionUID = 1L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VasMovementList;
	protected Borderlayout borderLayout_VasMovementList;
	protected Paging pagingVasMovementList;
	protected Listbox listBoxVasMovement;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_InitiatedBy;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinBranch;
	protected Listheader listheader_FinStartDate;
	protected Listheader listheader_MaturityDate;
	protected Listheader listheader_FinAmount;
	protected Listheader listheader_FinCcy;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_NumberOfTerms;

	protected Textbox finReference;
	protected Textbox custCIF;
	protected Textbox finType;
	protected Textbox finCcy;
	protected Textbox finBranch;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finCcy;
	protected Listbox sortOperator_finBranch;

	protected Button button_VasMovementList_VasMovementSearchDialog;
	private transient VasMovementService vasMovementService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private int oldVar_sortOperator_finReference;

	/**
	 * default constructor.<br>
	 */
	public VasMovementListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VasMovement";
		super.pageRightName = "VasMovementList";
		super.tableName = "VasMovement_AView";
		super.queueTableName = "VasMovement_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_VasMovementList(Event event) {
		// Set the page level components.
		setPageComponents(window_VasMovementList, borderLayout_VasMovementList, listBoxVasMovement,
				pagingVasMovementList);
		setItemRender(new VasMovementListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VasMovementList_VasMovementSearchDialog);

		registerField("vasMovementId");
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_FinReference,
				Operators.MULTISELECT);
		registerField("custCif", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF,
				Operators.MULTISELECT);
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType,
				Operators.MULTISELECT);
		registerField("finCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.MULTISELECT);
		registerField("finBranch", listheader_FinBranch, SortOrder.NONE, finBranch, sortOperator_finBranch,
				Operators.MULTISELECT);
		registerField("finStartdate", listheader_FinStartDate, SortOrder.NONE);
		registerField("maturityDate", listheader_MaturityDate, SortOrder.NONE);
		registerField("NumberOfTerms", listheader_NumberOfTerms, SortOrder.NONE);
		registerField("finAmount", listheader_FinAmount, SortOrder.NONE);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Entering");

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_VasMovementList_VasMovementSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onVasMovementItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVasMovement.getSelectedItem();

		// Get the selected entity.
		long finID = (Long) selectedItem.getAttribute("finID");
		String finReference = (String) selectedItem.getAttribute("id");
		VasMovement vasMovement = vasMovementService.getVasMovementById(finID);

		if (vasMovement == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (vasMovement.getVasMovementId() <= 0) {
			vasMovement.setNewRecord(true);
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND VasMovementId=" + vasMovement.getVasMovementId() + " AND version="
				+ vasMovement.getVersion() + " ";

		if (doCheckAuthority(vasMovement, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vasMovement.getWorkflowId() == 0) {
				vasMovement.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vasMovement);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_finBranch, this.finBranch, "Branch");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on button "btnSearchFinRef" button
	 * 
	 * @param event
	 */

	public void onClick$btnSearchFinRef(Event event) {
		setSearchValue(sortOperator_FinReference, this.finReference, "FinanceMain");
	}

	public void onSelect$sortOperator_finReference(Event event) {
		this.oldVar_sortOperator_finReference = doChangeStringOperator(sortOperator_FinReference,
				oldVar_sortOperator_finReference, this.finReference);
	}

	private int doChangeStringOperator(Listbox listbox, int oldOperator, Textbox textbox) {

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

		if (oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN) {
			if (!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)) {
				textbox.setValue("");
			}
		} else {
			if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
				textbox.setValue("");
			}
		}
		return searchOpId;

	}

	/**
	 * When user clicks on "btnSearchBranch" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_finBranch, this.finBranch, "Branch");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_finType, this.finType, "FinanceType");

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_finCcy, this.finCcy, "Currency");

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCheckList The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VasMovement aVasMovement) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vasMovement", aVasMovement);
		arg.put("vasMovementListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/VasMovement/VasMovementDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public VasMovementService getVasMovementService() {
		return vasMovementService;
	}

	public void setVasMovementService(VasMovementService vasMovementService) {
		this.vasMovementService = vasMovementService;
	}

}