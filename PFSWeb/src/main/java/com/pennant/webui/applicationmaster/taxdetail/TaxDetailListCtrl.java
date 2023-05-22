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
 * * FileName : TaxDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-06-2017 * * Modified
 * Date : 14-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.taxdetail;

import java.util.Map;

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

import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.service.applicationmaster.TaxDetailService;
import com.pennant.webui.applicationmaster.taxdetail.model.TaxDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/TaxDetail/TaxDetailList.zul file.
 * 
 */
public class TaxDetailListCtrl extends GFCBaseListCtrl<TaxDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_TaxDetailList;
	protected Borderlayout borderLayout_TaxDetailList;
	protected Paging pagingTaxDetailList;
	protected Listbox listBoxTaxDetail;
	protected Textbox entityCode;
	protected Textbox taxCode;
	protected Textbox country;
	protected Textbox stateCode;
	protected Textbox pinCode;
	protected Textbox cityCode;

	protected Listbox sortOperator_Country;
	protected Listbox sortOperator_StateCode;
	protected Listbox sortOperator_EntityCode;
	protected Listbox sortOperator_TaxCode;
	protected Listbox sortOperator_PinCode;
	protected Listbox sortOperator_CityCode;

	protected Listheader listheader_Country;
	protected Listheader listheader_StateCode;
	protected Listheader listheader_EntityCode;
	protected Listheader listheader_TaxCode;
	protected Listheader listheader_PinCode;
	protected Listheader listheader_CityCode;

	// List headers

	// checkRights
	protected Button button_TaxDetailList_NewTaxDetail;
	protected Button button_TaxDetailList_TaxDetailSearch;

	// Search Fields

	private transient TaxDetailService taxDetailService;

	/**
	 * default constructor.<br>
	 */
	public TaxDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "TaxDetail";
		super.pageRightName = "TaxDetailList";
		super.tableName = "TAXDETAIL_AView";
		super.queueTableName = "TAXDETAIL_View";
		super.enquiryTableName = "TAXDETAIL_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TaxDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_TaxDetailList, borderLayout_TaxDetailList, listBoxTaxDetail, pagingTaxDetailList);
		setItemRender(new TaxDetailListModelItemRenderer());
		registerButton(button_TaxDetailList_TaxDetailSearch);
		// Register buttons and fields.
		registerButton(button_TaxDetailList_TaxDetailSearch);
		registerButton(button_TaxDetailList_NewTaxDetail, "button_TaxDetailList_NewTaxDetail", true);
		registerField("countryName", listheader_Country, SortOrder.ASC, country, sortOperator_Country,
				Operators.STRING);
		registerField("provinceName", listheader_StateCode, SortOrder.ASC, stateCode, sortOperator_StateCode,
				Operators.STRING);
		registerField("entityDesc", listheader_EntityCode, SortOrder.ASC, entityCode, sortOperator_EntityCode,
				Operators.STRING);
		registerField("taxCode", listheader_TaxCode, SortOrder.ASC, taxCode, sortOperator_TaxCode, Operators.STRING);
		registerField("pinCode", listheader_PinCode, SortOrder.ASC, pinCode, sortOperator_PinCode, Operators.STRING);
		registerField("cityName", listheader_CityCode, SortOrder.ASC, cityCode, sortOperator_CityCode,
				Operators.STRING);
		registerField("id");
		registerField("country");
		registerField("stateCode");
		registerField("cityCode");
		registerField("entityCode");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TaxDetailList_TaxDetailSearch(Event event) {
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
	public void onClick$button_TaxDetailList_NewTaxDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		TaxDetail taxdetail = new TaxDetail();
		taxdetail.setNewRecord(true);
		taxdetail.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(taxdetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onTaxDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxTaxDetail.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		TaxDetail taxdetail = taxDetailService.getTaxDetail(id);

		if (taxdetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =?");

		if (doCheckAuthority(taxdetail, whereCond.toString(), new Object[] { taxdetail.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && taxdetail.getWorkflowId() == 0) {
				taxdetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(taxdetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param taxdetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TaxDetail taxdetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("taxdetail", taxdetail);
		arg.put("taxdetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/TaxDetail/TaxDetailDialog.zul", null, arg);
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

	public void setTaxDetailService(TaxDetailService taxDetailService) {
		this.taxDetailService = taxDetailService;
	}
}