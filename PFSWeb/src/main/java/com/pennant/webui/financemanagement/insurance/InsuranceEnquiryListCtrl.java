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
 * FileName    		:  FinanceSelectCtrl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.insurance;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.webui.financemanagement.insurance.model.InsuranceEnquiryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Insurance/InsuranceEnquiry.zul file.
 */
public class InsuranceEnquiryListCtrl extends GFCBaseListCtrl<InsuranceDetails> {
	private static final long serialVersionUID = -5081318673331825306L;
	private static final Logger logger = LogManager.getLogger(InsuranceEnquiryListCtrl.class);

	protected Window window_InsuranceEnquiry;
	protected Borderlayout borderlayout_InsuranceSelect;
	protected Paging pagingInsuranceList;
	protected Listbox listBoxInsurance;

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_InsuranceReference;
	protected Listbox sortOperator_PolicyNumber;
	protected Listbox sortOperator_VasManufacturer;
	protected Listbox sortOperator_RecordStatus;
	protected Listbox sortOperator_RecordType;

	protected Textbox finReference;
	protected Textbox insuranceReference;
	protected Textbox policyNumber;
	protected ExtendedCombobox vasManufacturer;

	protected Button btnClear;
	protected Button btnSearch;

	protected Listheader listheader_FinReference;
	protected Listheader listheader_InsuranceReference;
	protected Listheader listheader_PolicyNumber;
	protected Listheader listheader_VasManufacturer;

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InsuranceDetailsEnq";
		super.tableName = "InsuranceDetails_eview";
		super.queueTableName = "InsuranceDetails_eview";
	}

	protected void doAddFilters() {
		super.doAddFilters();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("ProductCtg", VASConsatnts.VAS_CATEGORY_VASI, Filter.OP_EQUAL);
		this.searchObject.addFilterAnd(filters);
	}

	public InsuranceEnquiryListCtrl() {
		super();
	}

	public void onCreate$window_InsuranceEnquiry(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		setPageComponents(window_InsuranceEnquiry, borderlayout_InsuranceSelect, listBoxInsurance, pagingInsuranceList);
		setItemRender(new InsuranceEnquiryListModelItemRenderer());

		registerButton(btnSearch);

		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("reference", listheader_InsuranceReference, SortOrder.NONE, insuranceReference,
				sortOperator_InsuranceReference, Operators.STRING);
		registerField("PolicyNumber", listheader_PolicyNumber, SortOrder.NONE, policyNumber, sortOperator_PolicyNumber,
				Operators.STRING);
		registerField("VasProviderId", listheader_VasManufacturer, SortOrder.NONE, vasManufacturer,
				sortOperator_VasManufacturer, Operators.NUMERIC);
		registerField("VasProviderDesc");
		registerField("paymentMode");
		registerField("ProductCtg");
		doSetFieldProperties();
		doRenderPage();
		search();

		logger.debug(Literal.LEAVING + event.toString());
	}

	protected void doSetFieldProperties() {
		this.vasManufacturer.setModuleName("VehicleDealer");
		this.vasManufacturer.setValueColumn("DealerId");
		this.vasManufacturer.setDescColumn("DealerName");
		this.vasManufacturer.setValidateColumns(new String[] { "DealerId" });
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSearch(Event event) {
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

	public void onInsuranceEnquiryItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		Listitem selectedItem = this.listBoxInsurance.getSelectedItem();

		InsuranceDetails insuranceDetails = (InsuranceDetails) selectedItem.getAttribute("insuranceDetails");

		if (insuranceDetails == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		doShowDialogPage(insuranceDetails);

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(InsuranceDetails insuranceDetails) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("insuranceDetails", insuranceDetails);
		arg.put("listCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Insurance/InsuranceEnquiryDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

}