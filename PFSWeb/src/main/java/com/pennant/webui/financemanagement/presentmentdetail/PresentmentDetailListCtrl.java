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
 * * FileName : PresentmentDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-05-2017 * *
 * Modified Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.presentmentdetail;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.financemanagement.presentmentheader.model.PresentmentHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentHeader/PresentmentDetailList.zul file.
 * 
 */
public class PresentmentDetailListCtrl extends GFCBaseListCtrl<PresentmentHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_PresentmentHeaderList;
	protected Borderlayout borderLayout_PresentmentHeaderList;
	protected Paging pagingPresentmentHeaderList;
	protected Listbox listBoxPresentmentHeader;

	// List headers
	protected Listheader listheader_Reference;
	protected Listheader listheader_presentmentType;
	protected Listheader listheader_PresentmentDate;
	protected Listheader listheader_BankCode;
	protected Listheader listheader_PartnerBankId;
	protected Listheader listheader_Status;
	protected Listheader listheader_MandateType;
	protected Listheader listheader_EMandateSource;
	protected Listheader listheader_Schdate;
	protected Listheader listheader_Entity;

	// checkRights
	protected Button button_PresentmentHeaderList_NewPresentmentHeader;
	protected Button button_PresentmentHeaderList_PresentmentHeaderSearch;

	// Search Fields
	protected Textbox reference;
	protected Combobox presentmentType;
	protected ExtendedCombobox partnerBank;
	protected Combobox status;
	protected Combobox mandateType;
	protected Datebox schdate;
	protected Datebox presentmentDate;
	protected Textbox bankCode;
	protected Label label_PresentmentHeaderList_BankCode;
	protected ExtendedCombobox entityCode;

	protected Listbox sortOperator_Reference;
	protected Listbox sortOperator_presentmentType;
	protected Listbox sortOperator_PresentmentDate;
	protected Listbox sortOperator_PartnerBankId;
	protected Listbox sortOperator_Status;
	protected Listbox sortOperator_MandateType;
	protected Listbox sortOperator_EMandateSource;
	protected Listbox sortOperator_Schdate;
	protected Listbox sortOperator_BankCode;
	protected Listbox sortOperator_Entity;
	protected Row row_PresentmentHeaderList_BankCode;

	protected Label label_EmandateSource;
	protected ExtendedCombobox emandateSource;

	private transient PresentmentDetailService presentmentDetailService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentHeader";
		super.pageRightName = "PresentmentHeaderList";
		super.tableName = "PresentmentHeader_AView";
		super.queueTableName = "PresentmentHeader_View";
		super.enquiryTableName = "PresentmentHeader_View";
	}

	String moduleType;

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		moduleType = (String) arguments.get("ModuleType");

		if ("N".equals(moduleType)) {
			this.searchObject.addFilterIn("STATUS", 1, 2);
		} else if ("A".equals(moduleType)) {
			this.searchObject.addFilterIn("STATUS", 3);
		} else if ("E".equals(moduleType)) {
			this.help.setVisible(false);
		} else if ("E".equals(moduleType)) {
			this.help.setVisible(false);
		}

		if (InstrumentType.isEMandate(mandateType.getSelectedItem().getValue())) {
			if (!this.emandateSource.getValidatedValue().isEmpty()) {
				this.searchObject.addFilterIn("EMANDATESOURCE", this.emandateSource.getValidatedValue());
			}
		}

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_PresentmentHeaderList, borderLayout_PresentmentHeaderList, listBoxPresentmentHeader,
				pagingPresentmentHeaderList);
		setItemRender(new PresentmentHeaderListModelItemRenderer());

		moduleType = (String) arguments.get("ModuleType");

		// Register buttons and fields.
		doSetFieldProperties();
		registerButton(button_PresentmentHeaderList_PresentmentHeaderSearch);
		registerField("reference", listheader_Reference, SortOrder.NONE, reference, sortOperator_Reference,
				Operators.STRING);
		registerField("presentmentType", listheader_presentmentType, SortOrder.NONE, presentmentType,
				sortOperator_presentmentType, Operators.STRING);
		registerField("entityCode", listheader_Entity, SortOrder.NONE, entityCode, sortOperator_Entity,
				Operators.STRING);
		registerField("presentmentDate", listheader_PresentmentDate, SortOrder.NONE, presentmentDate,
				sortOperator_PresentmentDate, Operators.DATE);
		registerField("bankCode", bankCode, SortOrder.NONE, sortOperator_BankCode, Operators.STRING);
		registerField("bankName", listheader_BankCode, SortOrder.NONE);
		registerField("partnerBankId", listheader_PartnerBankId, SortOrder.NONE, partnerBank,
				sortOperator_PartnerBankId, Operators.SIMPLE_NUMARIC);
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status,
				Operators.SIMPLE_NUMARIC);
		registerField("mandateType", listheader_MandateType, SortOrder.NONE, mandateType, sortOperator_MandateType,
				Operators.STRING);
		registerField("schdate", listheader_Schdate, SortOrder.NONE, schdate, sortOperator_Schdate, Operators.DATE);
		registerField("id");
		registerField("partnerBankCode");
		registerField("partnerBankName");
		registerField("emandateSource");
		if (!SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK)) {
			this.row_PresentmentHeaderList_BankCode.setVisible(false);
			listheader_BankCode.setVisible(false);
			this.label_PresentmentHeaderList_BankCode.setVisible(false);
			this.sortOperator_BankCode.setVisible(false);
			this.bankCode.setVisible(false);
		}
		this.row_AlwWorkflow.setVisible(false);

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PresentmentHeader");
		if (moduleMapping.getWorkflowType() != null) {
			setFirstTask(true);
		}

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.partnerBank.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.partnerBank.setModuleName("PresentMents_PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankId");
		this.partnerBank.setValueType(DataType.LONG);
		this.partnerBank.setDescColumn("PartnerBankCode");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankId" });

		this.entityCode.setModuleName("Entity");
		this.entityCode.setMandatoryStyle(false);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		fillList(status, PennantStaticListUtil.getPresentmentBatchStatusList(), null);
		fillComboBox(this.mandateType, "", MandateUtil.getInstrumentTypesForBE(), "");
		fillComboBox(this.presentmentType, "", PennantStaticListUtil.getPresetmentTypeList(), "");
		this.presentmentDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.schdate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.emandateSource.setModuleName("Mandate_Sources");
		this.emandateSource.setDisplayStyle(2);
		this.emandateSource.setValueColumn("Code");
		this.emandateSource.setDescColumn("Description");
		this.emandateSource.setValidateColumns(new String[] { "Code" });

		logger.debug(Literal.LEAVING);
	}

	public void onSelect$mandateType(Event event) {
		logger.debug(Literal.ENTERING);

		String code = mandateType.getSelectedItem().getValue();

		if (InstrumentType.isEMandate(code)) {
			this.emandateSource.setValue("");
			this.emandateSource.setDescColumn("");
			emandateSource.setVisible(true);
			this.emandateSource.setMandatoryStyle(true);
			label_EmandateSource.setVisible(true);
		} else {
			emandateSource.setVisible(false);
			label_EmandateSource.setVisible(false);
			this.emandateSource.setMandatoryStyle(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PresentmentHeaderList_PresentmentHeaderSearch(Event event) {
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
	public void onClick$button_PresentmentHeaderList_NewPresentmentHeader(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		PresentmentHeader presentmentheader = new PresentmentHeader();
		presentmentheader.setNewRecord(true);
		presentmentheader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(presentmentheader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onPresentmentHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPresentmentHeader.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		PresentmentHeader presentmentheader = presentmentDetailService.getPresentmentHeader(id);

		if (presentmentheader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Validate EOD is in progress or not
		if ("A".equals(moduleType)) {
			String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
			if (StringUtils.equals(phase, PennantConstants.APP_PHASE_EOD)) {
				MessageUtil.showError(Labels.getLabel("Amortization_EOD_Check"));
				return;
			}
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(presentmentheader, whereCond.toString(), new Object[] { presentmentheader.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && presentmentheader.getWorkflowId() == 0) {
				presentmentheader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(presentmentheader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param presentmentheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PresentmentHeader presentmentheader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("presentmentHeader", presentmentheader);
		arg.put("presentmentDetailListCtrl", this);
		arg.put("moduleType", this.moduleType);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/PresentmentDetail/PresentmentDetailDialog.zul", null, arg);
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

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

}