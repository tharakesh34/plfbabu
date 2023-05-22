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
 * * FileName : FinanceTaxDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * *
 * Modified Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financetaxdetail;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.financetaxdetail.model.FinanceTaxDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.tax/FinanceTaxDetail/FinanceTaxDetailList.zul file.
 * 
 */
public class FinanceTaxDetailListCtrl extends GFCBaseListCtrl<FinanceTaxDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_FinanceTaxDetailList;
	protected Borderlayout borderLayout_FinanceTaxDetailList;
	protected Paging pagingFinanceTaxDetailList;
	protected Listbox listBoxFinanceTaxDetail;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_ApplicableFor;
	protected Listheader listheader_TaxExempted;
	protected Listheader listheader_TaxNumber;
	protected Listheader listheader_City;
	protected Listheader listheader_PinCode;

	// checkRights
	protected Button button_FinanceTaxDetailList_NewFinanceTaxDetail;
	protected Button button_FinanceTaxDetailList_FinanceTaxDetailSearch;

	// Search Fields
	protected Textbox finReference; // autowired
	protected Combobox applicableFor; // autowired
	protected Checkbox taxExempted; // autowired
	protected Textbox taxNumber; // autowired
	protected Textbox city; // autowired
	protected Textbox pinCode; // autowired

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_ApplicableFor;
	protected Listbox sortOperator_TaxExempted;
	protected Listbox sortOperator_TaxNumber;
	protected Listbox sortOperator_City;
	protected Listbox sortOperator_PinCode;

	private transient FinanceTaxDetailService financeTaxDetailService;
	private transient CustomerAddresDAO customerAddresDAO;

	/**
	 * default constructor.<br>
	 */
	public FinanceTaxDetailListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		/*
		 * if (!enqiryModule) { this.searchObject.addFilterNotEqual("recordType", PennantConstants.RECORD_TYPE_NEW); }
		 */
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceTaxDetail";
		super.pageRightName = "FinanceTaxDetailList";
		super.tableName = "FinTaxDetail_AView";
		super.queueTableName = "FinTaxDetail_View";
		super.enquiryTableName = "FinTaxDetail_View";
		super.workFlowTable = "FinTaxDetail_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceTaxDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinanceTaxDetailList, borderLayout_FinanceTaxDetailList, listBoxFinanceTaxDetail,
				pagingFinanceTaxDetailList);
		setItemRender(new FinanceTaxDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceTaxDetailList_FinanceTaxDetailSearch);
		registerButton(button_FinanceTaxDetailList_NewFinanceTaxDetail,
				"button_FinanceTaxDetailList_NewFinanceTaxDetail", true);

		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("applicableFor", listheader_ApplicableFor, SortOrder.NONE, applicableFor,
				sortOperator_ApplicableFor, Operators.STRING);
		registerField("taxExempted", listheader_TaxExempted, SortOrder.NONE, taxExempted, sortOperator_TaxExempted,
				Operators.BOOLEAN);
		registerField("taxNumber", listheader_TaxNumber, SortOrder.NONE, taxNumber, sortOperator_TaxNumber,
				Operators.STRING);
		registerField("city", listheader_City, SortOrder.NONE, city, sortOperator_City, Operators.STRING);
		registerField("pinCode", listheader_PinCode, SortOrder.NONE, pinCode, sortOperator_PinCode, Operators.STRING);

		fillComboBox(applicableFor, "", PennantStaticListUtil.getTaxApplicableFor(), "");

		// Render the page and display the data.
		doRenderPage();

		search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FinanceTaxDetailList_FinanceTaxDetailSearch(Event event) {
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
	public void onClick$button_FinanceTaxDetailList_NewFinanceTaxDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		FinanceTaxDetail financetaxdetail = new FinanceTaxDetail();
		financetaxdetail.setNewRecord(true);
		financetaxdetail.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(financetaxdetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onFinanceTaxDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinanceTaxDetail.getSelectedItem();
		final long finID = (Long) selectedItem.getAttribute("finID");
		FinanceTaxDetail financetaxdetail = financeTaxDetailService.getFinanceTaxDetail(finID);

		if (financetaxdetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// ### 17-07-2018 - Start - Ticket ID : 127950
		// check whether record present in loan queue
		boolean isFinReferenceExitsinLQ = financeTaxDetailService.isFinReferenceExitsinLQ(finID, TableType.TEMP_TAB,
				false);

		if (isFinReferenceExitsinLQ) {
			MessageUtil.showError(Labels.getLabel("info.not_authorized"));
			return;
		}
		// ### 17-07-2018 - End - Ticket ID : 127950

		StringBuilder whereCond = new StringBuilder();
		whereCond.append(" where  FinReference =?");

		if (doCheckAuthority(financetaxdetail, whereCond.toString(),
				new Object[] { financetaxdetail.getFinReference() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && financetaxdetail.getWorkflowId() == 0) {
				financetaxdetail.setWorkflowId(getWorkFlowId());
			}
			logUserAccess("menu_Item_FinanceTaxDetail", financetaxdetail.getFinReference());
			doShowDialogPage(financetaxdetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financetaxdetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceTaxDetail financetaxdetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		if (enqiryModule) {
			arg.put("enquirymode", true);
		} else {
			arg.put("enquirymode", false);
		}
		arg.put("financeTaxDetail", financetaxdetail);
		arg.put("financeTaxDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceTaxDetail/FinanceTaxDetailDialog.zul", null,
					arg);
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

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}
}