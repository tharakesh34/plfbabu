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
 * * FileName : MandateListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified Date
 * : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.mandate.mandate;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.mandate.mandate.model.MandateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateList.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class MandateListCtrl extends GFCBaseListCtrl<Mandate> {
	private static final long serialVersionUID = 1L;

	protected Window window_MandateList;
	protected Borderlayout borderLayout_MandateList;
	protected Paging pagingMandateList;
	protected Listbox listBoxMandate;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_MandateId;
	protected Listheader listheader_CustName;
	protected Listheader listheader_MandateType;
	protected Listheader listheader_BankName;
	protected Listheader listheader_AccNumber;
	protected Listheader listheader_AccType;
	protected Listheader listheader_Amount;
	protected Listheader listheader_SecurityMandate;
	protected Listheader listheader_ExpiryDate;
	protected Listheader listheader_Status;
	protected Listheader listheader_InputDate;

	protected Button button_MandateList_NewMandate;
	protected Button button_MandateList_MandateSearch;

	protected Longbox mandateID;
	protected Textbox custCIF;
	protected Combobox mandateType;
	protected Textbox custShrtName;
	protected Textbox bankName;
	protected Combobox status;
	protected Textbox accNumber;
	protected Combobox accType;
	protected Datebox expiryDate;
	protected Datebox inputDate;
	protected Checkbox securityMandate;
	protected ExtendedCombobox loanReference;
	protected Listbox sortOperator_LoanReference;

	protected Listbox sortOperator_MandateID;
	protected Listbox sortOperator_CustCIF;
	protected Listbox sortOperator_CustName;
	protected Listbox sortOperator_MandateType;
	protected Listbox sortOperator_BankName;
	protected Listbox sortOperator_AccNumber;
	protected Listbox sortOperator_AccType;
	protected Listbox sortOperator_ExpiryDate;
	protected Listbox sortOperator_Status;
	protected Listbox sortOperator_InputDate;
	protected Listbox sortOperator_SecurityMandate;

	private transient MandateService mandateService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	/**
	 * default constructor.<br>
	 */
	public MandateListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Mandate";
		super.pageRightName = "MandateList";
		super.tableName = "Mandates_AView";
		super.queueTableName = "Mandates_View";
		super.enquiryTableName = "Mandates_TView";
	}

	@Override
	protected void doAddFilters() {
		searchObject.addWhereClause(null);

		super.doAddFilters();

		this.searchObject = (JdbcSearchObject<Mandate>) searchObject.removeFiltersOnProperty("OrgReference");

		if (!enqiryModule && !searchObject.getFilters().isEmpty()) {
			searchObject.addFilterEqual("active", 1);
			searchObject.addFilterNotEqual("Status", MandateStatus.FIN);
		}

		String frn = this.loanReference.getValue();
		if (StringUtils.isNotEmpty(frn)) {
			searchObject.addWhereClause(getWhereClause(frn, securityMandate.isChecked()));
		}
	}

	private String getWhereClause(String frn, boolean securityMandate) {
		StringBuilder sql = new StringBuilder();

		sql.append(" MandateID In (Select MandateID From (");
		if (securityMandate) {
			sql.append(" Select MandateID From Mandates Where SecurityMandate = 1 and OrgReference = '");
			sql.append(frn).append("'");
			sql.append(" Union All");
			sql.append(" Select MandateID From Mandates_temp Where SecurityMandate = 1 and OrgReference = '");
			sql.append(frn).append("'");
			sql.append(" Union All");
			sql.append(" Select SecurityMandateID From FinanceMain Where FinReference = '");
			sql.append(frn).append("'");
		} else {
			sql.append(" Select MandateID From Mandates Where SecurityMandate = 0 and OrgReference = '");
			sql.append(frn).append("'");
			sql.append(" Union All");
			sql.append(" Select MandateID From Mandates_temp Where SecurityMandate = 0 and OrgReference = '");
			sql.append(frn).append("'");
			sql.append(" Union All");
			sql.append(" Select MandateID From FinanceMain Where FinReference = '");
			sql.append(frn).append("'");
		}
		sql.append(" ) T )");

		return sql.toString();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateList(Event event) {
		// Set the page level components.
		setPageComponents(window_MandateList, borderLayout_MandateList, listBoxMandate, pagingMandateList);
		setItemRender(new MandateListModelItemRenderer(false));

		// Register buttons and fields.

		registerButton(button_MandateList_MandateSearch);
		registerButton(button_MandateList_NewMandate, "button_MandateList_NewMandate", true);

		fillComboBox(this.mandateType, "", MandateUtil.getInstrumentTypes(), "," + InstrumentType.PDC.code() + ",");
		fillComboBox(this.accType, "", MandateUtil.getAccountTypes(), "");
		fillComboBox(this.status, "", MandateUtil.getMandateStatus(), Collections.singletonList(MandateStatus.FIN));

		registerField("mandateID", listheader_MandateId, SortOrder.ASC, mandateID, sortOperator_MandateID,
				Operators.NUMERIC);
		registerField("custCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("mandateType", listheader_MandateType, SortOrder.NONE, mandateType, sortOperator_MandateType,
				Operators.STRING);
		registerField("custShrtName", listheader_CustName, SortOrder.NONE, custShrtName, sortOperator_CustName,
				Operators.STRING);
		registerField("accNumber", listheader_AccNumber, SortOrder.NONE, accNumber, sortOperator_AccNumber,
				Operators.STRING);
		registerField("accType", listheader_AccType, SortOrder.NONE, accType, sortOperator_AccType, Operators.STRING);
		registerField("expiryDate", listheader_ExpiryDate, SortOrder.NONE, expiryDate, sortOperator_ExpiryDate,
				Operators.DATE);
		registerField("bankName", listheader_BankName, SortOrder.NONE, bankName, sortOperator_BankName,
				Operators.STRING);
		registerField("maxLimit", listheader_Amount, SortOrder.NONE);
		registerField("inputDate", listheader_InputDate, SortOrder.NONE, inputDate, sortOperator_InputDate,
				Operators.DATE);
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status, Operators.STRING);
		registerField("orgReference", loanReference, SortOrder.NONE, sortOperator_LoanReference, Operators.STRING);
		registerField("securityMandate", listheader_SecurityMandate, SortOrder.NONE, securityMandate,
				sortOperator_SecurityMandate, Operators.SIMPLE_NUMARIC);

		doSetFieldProperties();

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_MandateList_MandateSearch(Event event) {
		this.loanReference.setErrorMessage("");
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		this.loanReference.setValue("");
		this.loanReference.setDescription("");
		this.loanReference.setErrorMessage("");
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_MandateList_NewMandate(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Mandate mandate = new Mandate();
		mandate.setNewRecord(true);
		mandate.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		Map<String, Object> arg = getDefaultArguments();
		arg.put("mandate", mandate);
		arg.put("mandateListCtrl", this);
		arg.put("enqModule", enqiryModule);
		arg.put("fromLoan", false);

		Executions.createComponents("/WEB-INF/pages/Mandate/SelectMandateDialog.zul", null, arg);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onMandateItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxMandate.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Mandate mandate = mandateService.getMandateById(id);

		if (mandate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		mandateService.getDocumentImage(mandate);

		// Check whether the user has authority to change/view the record.
		String whereCond = " where MandateID=?";

		if (doCheckAuthority(mandate, whereCond, new Object[] { mandate.getMandateID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && mandate.getWorkflowId() == 0) {
				mandate.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(mandate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param mandate The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Mandate mandate) {
		logger.debug(Literal.ENTERING);
		try {
			Map<String, Object> arg = getDefaultArguments();
			arg.put("mandate", mandate);
			arg.put("mandateListCtrl", this);
			arg.put("enqModule", enqiryModule);
			arg.put("fromLoan", false);

			if (MandateStatus.isAwaitingConf(mandate.getStatus())) {
				arg.put("enqModule", true);
			}

			if (MandateStatus.isApproved(mandate.getStatus()) || MandateStatus.isHold(mandate.getStatus())) {
				arg.put("maintain", true);
			}

			String page = null;

			if (mandate.isSecurityMandate()) {
				page = "/WEB-INF/pages/Mandate/SecurityMandateDialog.zul";
			} else {
				page = "/WEB-INF/pages/Mandate/MandateDialog.zul";
			}

			if (enqiryModule) {
				page = "/WEB-INF/pages/Mandate/MandateDialog.zul";
			}

			Executions.createComponents(page, null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		logger.debug("Entering " + event.toString());

		search();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		logger.debug("Entering " + event.toString());

		search();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		if (enqiryModule) {
			moduleCode = "MandateEnquiry";
		}
		doPrintResults();
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug(Literal.ENTERING);
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		this.loanReference.setModuleName("FinanceMain");
		this.loanReference.setTextBoxWidth(155);
		this.loanReference.setValueColumn("FinReference");
		this.loanReference.setDescColumn("FinType");
		this.loanReference.setValidateColumns(new String[] { "FinReference" });
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}
}