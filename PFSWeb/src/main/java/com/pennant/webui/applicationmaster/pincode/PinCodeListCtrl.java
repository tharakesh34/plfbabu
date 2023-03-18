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
 * * FileName : PinCodeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-06-2017 * * Modified Date
 * : 01-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.pincode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.service.applicationmaster.PinCodeService;
import com.pennant.webui.applicationmaster.pincode.model.PinCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.master/PinCode/PinCodeList.zul file.
 * 
 */
public class PinCodeListCtrl extends GFCBaseListCtrl<PinCode> {
	private static final long serialVersionUID = 1L;

	protected Window window_PinCodeList;
	protected Borderlayout borderLayout_PinCodeList;
	protected Paging pagingPinCodeList;
	protected Listbox listBoxPinCode;

	// List headers
	protected Listheader listheader_PinCode;
	protected Listheader listheader_City;
	protected Listheader listheader_AreaName;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_PinCodeList_NewPinCode;
	protected Button button_PinCodeList_PinCodeSearch;

	// Search Fields
	protected Textbox pinCode; // autowired
	protected Textbox areaName; // autowired
	protected Textbox city; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_PinCode;
	protected Listbox sortOperator_City;
	protected Listbox sortOperator_AreaName;
	protected Listbox sortOperator_Active;

	private transient PinCodeService pinCodeService;

	private List<PinCode> pincodeList;

	/**
	 * default constructor.<br>
	 */
	public PinCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PinCode";
		super.pageRightName = "PinCodeList";
		super.tableName = "PinCodes";
		super.queueTableName = "PinCodes";
		super.enquiryTableName = "PinCodes_Temp";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PinCodeList(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(window_PinCodeList, borderLayout_PinCodeList, listBoxPinCode, pagingPinCodeList);
		setItemRender(new PinCodeListModelItemRenderer());

		registerButton(button_PinCodeList_PinCodeSearch);
		registerButton(button_PinCodeList_NewPinCode, "button_PinCodeList_NewPinCode", true);

		registerField("pinCodeId");
		registerField("pinCode", listheader_PinCode, SortOrder.NONE, pinCode, sortOperator_PinCode, Operators.STRING);
		registerField("pCCityName", listheader_City, SortOrder.NONE, city, sortOperator_City, Operators.STRING);
		registerField("areaName", listheader_AreaName, SortOrder.NONE, areaName, sortOperator_AreaName,
				Operators.STRING);

		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active,
				Operators.SIMPLE_NUMARIC);

		this.active.setChecked(true);

		fillListData();

		doRenderPage();

		logger.debug(Literal.LEAVING.concat(event.toString()));

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PinCodeList_PinCodeSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<String> roleCodes = getWorkFlowRoles();

		List<PinCode> pinCodes = pinCodeService.getResult(getSearchFilters(), roleCodes);

		listBoxPinCode.setItemRenderer(new PinCodeListModelItemRenderer());

		pagedListWrapper.initList(pinCodes, listBoxPinCode, pagingPinCodeList);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doReset();
		this.active.setChecked(true);
		fillListData();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$button_PinCodeList_NewPinCode(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		// Create a new entity.
		PinCode pincode = new PinCode();
		pincode.setNewRecord(true);
		pincode.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(pincode);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onPinCodeItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		// Get the selected record.
		Listitem selectedItem = this.listBoxPinCode.getSelectedItem();
		final long pinCodeId = (long) selectedItem.getAttribute("pinCodeId");
		PinCode pincode = pinCodeService.getPinCode(pinCodeId);

		if (pincode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  PinCodeId =?");

		if (doCheckAuthority(pincode, whereCond.toString(), new Object[] { pincode.getPinCodeId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && pincode.getWorkflowId() == 0) {
				pincode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(pincode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param pincode The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PinCode pincode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("pincode", pincode);
		arg.put("pincodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/PinCode/PinCodeDialog.zul", null, arg);
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
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doPrintResults(getReportData());

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

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

	public void fillListData() {
		List<String> roleCodes = getWorkFlowRoles();

		pincodeList = pinCodeService.getResult(getSearchFilters(), roleCodes);

		listBoxPinCode.setItemRenderer(new PinCodeListModelItemRenderer());

		pagedListWrapper.initList(pincodeList, listBoxPinCode, pagingPinCodeList);
	}

	private ISearch getSearchFilters() {
		ISearch search = new Search();

		String tmpPinCode = this.pinCode.getValue();
		if (StringUtils.isNotEmpty(tmpPinCode)) {
			search.getFilters().add(new Filter("pinCode", tmpPinCode, this.sortOperator_PinCode.getSelectedIndex()));
		}

		String tmpAreaName = this.areaName.getValue();
		if (StringUtils.isNotEmpty(tmpAreaName)) {
			search.getFilters().add(new Filter("areaName", tmpAreaName, this.sortOperator_AreaName.getSelectedIndex()));
		}

		String tmpCity = this.city.getValue();
		if (StringUtils.isNotEmpty(tmpCity)) {
			search.getFilters().add(new Filter("PCCityname", tmpCity, this.sortOperator_City.getSelectedIndex()));
		}

		boolean isActive = this.active.isChecked();
		search.getFilters().add(new Filter("Active", isActive, this.sortOperator_Active.getSelectedIndex()));

		String status = this.recordStatus.getValue();
		if (StringUtils.isNotEmpty(status)) {
			search.getFilters()
					.add(new Filter("RecordStatus", status, this.sortOperator_RecordStatus.getSelectedIndex()));
		}

		String recordType = null;
		if (this.recordType.getSelectedItem() != null) {
			recordType = this.recordType.getSelectedItem().getValue();
		}
		if (StringUtils.isNotEmpty(recordType)) {
			search.getFilters()
					.add(new Filter("RecordType", recordType, this.sortOperator_RecordType.getSelectedIndex()));
		}

		return search;
	}

	private List<ReportListDetail> getReportData() {
		List<ReportListDetail> list = new ArrayList<>(0);
		if (CollectionUtils.isEmpty(this.pincodeList)) {
			return list;
		}

		for (PinCode pincode : this.pincodeList) {
			ReportListDetail bc = new ReportListDetail();

			bc.setfieldString01(pincode.getPinCode());
			bc.setfieldString02(pincode.getAreaName());
			bc.setFieldBoolean04(pincode.isActive() ? 1 : 0);

			list.add(bc);
		}

		return list;
	}

	public void setPinCodeService(PinCodeService pinCodeService) {
		this.pinCodeService = pinCodeService;
	}
}