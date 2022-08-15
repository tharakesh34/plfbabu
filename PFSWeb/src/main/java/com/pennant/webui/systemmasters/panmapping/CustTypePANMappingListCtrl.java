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
package com.pennant.webui.systemmasters.panmapping;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennant.backend.service.systemmasters.CustTypePANMappingService;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.systemmasters.panmapping.model.PANMappingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/PANMapping/CustTypePANMappingList.zul file.
 */
public class CustTypePANMappingListCtrl extends GFCBaseListCtrl<CustTypePANMapping> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = LogManager.getLogger(CustTypePANMappingListCtrl.class);

	protected Window window_PANMappingList;
	protected Borderlayout borderLayout_PANMappingList;
	protected Listbox listBoxPANMapping;
	protected Paging pagingPANMappingList;

	protected Listheader listheader_CustCategory;
	protected Listheader listheader_CustType;
	protected Listheader listheader_panLetter;
	protected Listheader listheader_Active;

	protected Button button_PANMappingList_NewPANMapping;
	protected Button button_PANMappingList_SearchDialog;

	protected Combobox custCategory;
	protected ExtendedCombobox custType;
	protected Uppercasebox panLetter;
	protected Checkbox active;

	protected Listbox sortOperator_custCategory;
	protected Listbox sortOperator_custType;
	protected Listbox sortOperator_panLetter;
	protected Listbox sortOperator_active;

	private transient CustTypePANMappingService custTypePANMappingService;

	/**
	 * The default constructor.
	 */
	public CustTypePANMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CustTypePANMapping";
		super.pageRightName = "PANMappingList";
		super.tableName = "CustTypePANMapping_AView";
		super.queueTableName = "CustTypePANMapping_View";
		super.enquiryTableName = "CustTypePANMapping_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PANMappingList(Event event) {
		// Set the page level components.
		setPageComponents(window_PANMappingList, borderLayout_PANMappingList, listBoxPANMapping, pagingPANMappingList);
		setItemRender(new PANMappingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PANMappingList_NewPANMapping, "button_PANMappingList_NewPANMapping", true);
		registerButton(button_PANMappingList_SearchDialog);

		fillComboBox(this.custCategory, "", PennantAppUtil.getcustCtgCodeList(), "");

		registerField("mappingID");
		registerField("custTypeDesc");
		registerField("custCategory", listheader_CustCategory, SortOrder.ASC, custCategory, sortOperator_custCategory,
				Operators.STRING);
		registerField("custType", listheader_CustType, SortOrder.ASC, custType, sortOperator_custType,
				Operators.STRING);
		registerField("panLetter", listheader_panLetter, SortOrder.NONE, panLetter, sortOperator_panLetter,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);

		// Render the page and display the data.
		doSetFieldProperties();
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PANMappingList_SearchDialog(Event event) {
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
	public void onClick$button_PANMappingList_NewPANMapping(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		CustTypePANMapping custTypePANMapping = new CustTypePANMapping();
		custTypePANMapping.setNewRecord(true);
		custTypePANMapping.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(custTypePANMapping);

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {

		this.custType.setModuleName("CustomerType");
		this.custType.setValueColumn("CustTypeCode");
		this.custType.setDescColumn("CustTypeDesc");
		this.custType.setValidateColumns(new String[] { "CustTypeCode" });
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onPANMappingItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxPANMapping.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		CustTypePANMapping custTypePANMapping = getCustTypePANMappingService().getPANMappingById(id);

		if (custTypePANMapping == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " MappingID='" + custTypePANMapping.getMappingID() + "' AND version="
				+ custTypePANMapping.getVersion() + " ";

		if (doCheckAuthority(custTypePANMapping, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && custTypePANMapping.getWorkflowId() == 0) {
				custTypePANMapping.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(custTypePANMapping);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param custTypePANMapping The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustTypePANMapping custTypePANMapping) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("custTypePANMapping", custTypePANMapping);
		arg.put("panMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/PANMapping/CustTypePANMappingDialog.zul", null,
					arg);
		} catch (Exception e) {
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

	public CustTypePANMappingService getCustTypePANMappingService() {
		return custTypePANMappingService;
	}

	public void setCustTypePANMappingService(CustTypePANMappingService custTypePANMappingService) {
		this.custTypePANMappingService = custTypePANMappingService;
	}
}