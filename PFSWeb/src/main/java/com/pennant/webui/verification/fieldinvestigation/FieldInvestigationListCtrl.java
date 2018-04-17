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
package com.pennant.webui.verification.fieldinvestigation;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.verification.fieldinvestigation.model.FieldInvestigationListModelItemRenderer;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.service.FieldInvestigationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Verification/FieldInvestigation/FieldInvestigationList.zul
 * file.
 * 
 */
public class FieldInvestigationListCtrl extends GFCBaseListCtrl<FieldInvestigation> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FieldInvestigationListCtrl.class);

	protected Window window_FieldInvestigationList;
	protected Borderlayout borderLayout_FieldInvestigationList;
	protected Paging pagingFieldInvestigationList;
	protected Listbox listBoxFieldInvestigation;

	// List headers
	protected Listheader listheader_CIF;
	protected Listheader listheader_AddressType;
	protected Listheader listheader_PinCode;
	protected Listheader listheader_LoanReference;
	protected Listheader listheader_Agency;

	// checkRights
	protected Button button_FieldInvestigationList_NewFieldInvestigation;
	protected Button button_FieldInvestigationList_FieldInvestigationSearch;

	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_AddressType;
	protected Listbox sortOperator_PinCode;
	protected Listbox sortOperator_LoanReference;
	protected Listbox sortOperator_Agency;

	protected Textbox cif;
	protected Textbox addressType;
	protected Textbox pinCode;
	protected Textbox loanReference;
	protected ExtendedCombobox agency; 

	@Autowired
	private transient FieldInvestigationService fieldInvestigationService;

	/**
	 * default constructor.<br>
	 */
	public FieldInvestigationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FieldInvestigation";
		super.pageRightName = "FieldInvestigationList";
		super.tableName = "verification_fi_view";
		super.queueTableName = "verification_fi_view";
		super.enquiryTableName = "verification_fi_view";
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FieldInvestigationList(Event event) {
		logger.debug(Literal.ENTERING);
		
		doSetFieldProperties();
		// Set the page level components.
		setPageComponents(window_FieldInvestigationList, borderLayout_FieldInvestigationList, listBoxFieldInvestigation,
				pagingFieldInvestigationList);
		setItemRender(new FieldInvestigationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FieldInvestigationList_FieldInvestigationSearch);
		registerButton(button_FieldInvestigationList_NewFieldInvestigation, "button_FieldInvestigationList_btnNew",
				false);

		registerField("verificationid");
		registerField("cif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("addressType", listheader_AddressType, SortOrder.ASC, addressType, sortOperator_AddressType,
				Operators.STRING);
		registerField("zipCode", listheader_PinCode, SortOrder.ASC, pinCode, sortOperator_PinCode, Operators.STRING);
		registerField("keyReference", listheader_LoanReference, SortOrder.ASC, loanReference,
				sortOperator_LoanReference, Operators.STRING);
		registerField("agency", listheader_Agency, SortOrder.ASC, agency, sortOperator_Agency, Operators.DEFAULT);
		registerField("agencyName");
		// Render the page and display the data.
		doRenderPage();
		search();
	}
	
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Agency
		this.agency.setMaxlength(50);
		this.agency.setTextBoxWidth(120);
		this.agency.setModuleName("FIVAgencies");
		this.agency.setValueColumn("DealerName");
		this.agency.setDescColumn("DealerCity");
		this.agency.setValidateColumns(new String[] { "DealerName" ,"DealerCity" });
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilter(new Filter("recordType", "", Filter.OP_NOT_EQUAL));
		}
		if (agency.getAttribute("agency") != null) {
			this.searchObject.removeFiltersOnProperty("agency");
			long agencyId = Long.parseLong(agency.getAttribute("agency").toString());
			this.searchObject.addFilter(new Filter("agency", agencyId, Filter.OP_EQUAL));
		}
		
		this.searchObject.addFilter(new Filter("reinitid", Filter.OP_NOT_NULL));
		//this.searchObject.addFilter(new Filter("dealertype", Agencies.FIAGENCY.getKey(), Filter.OP_EQUAL));
		//this.searchObject.addFilter(new Filter("reasontypecode", StatuReasons.FISRES.getKey(), Filter.OP_EQUAL));
	}

	public void onFulfill$agency(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = agency.getObject();
		if (dataObject instanceof String) {
			this.agency.setValue(dataObject.toString());
			this.agency.setDescription("");
			this.agency.setAttribute("agency", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.agency.setAttribute("agency", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	protected void doReset() {
		super.doReset();
		this.agency.setAttribute("agency", null);
	}
	
	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FieldInvestigationList_FieldInvestigationSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button.
	 * Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FieldInvestigationList_NewFieldInvestigation(Event event) {
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onFieldInvestigationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFieldInvestigation.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		FieldInvestigation fi = fieldInvestigationService.getFieldInvestigation(id);

		if (fi == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(fi.getId());
		whereCond.append(" AND  version=");
		whereCond.append(fi.getVersion());

		if (doCheckAuthority(fi, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && fi.getWorkflowId() == 0) {
				fi.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(fi);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param fieldinvestigation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FieldInvestigation fieldInvestigation) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("fieldInvestigation", fieldInvestigation);
		arg.put("fieldInvestigationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Verification/FieldInvestigation/FieldInvestigationDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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
}