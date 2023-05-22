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
 * * FileName : VASProviderAccDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-09-2018 * *
 * Modified Date : 24-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.vasprovideraccdetail;

import java.util.ArrayList;
import java.util.List;
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.systemmasters.VASProviderAccDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.systemmasters.vasprovideraccdetail.model.VASProviderAccDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/VASProviderAccDetail/VASProviderAccDetailList.zul
 * file.
 * 
 */
public class VASProviderAccDetailListCtrl extends GFCBaseListCtrl<VASProviderAccDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_VASProviderAccDetailList;
	protected Borderlayout borderLayout_VASProviderAccDetailList;
	protected Paging pagingVASProviderAccDetailList;
	protected Listbox listBoxVASProviderAccDetail;

	// List headers
	protected Listheader listheader_ProviderId;
	protected Listheader listheader_PaymentMode;
	protected Listheader listheader_BankBranchID;
	protected Listheader listheader_AccountNumber;
	protected Listheader listheader_ReceivableAdjustment;
	protected Listheader listheader_ReconciliationAmount;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_VASProviderAccDetailList_NewVASProviderAccDetail;
	protected Button button_VASProviderAccDetailList_VASProviderAccDetailSearch;

	// Search Fields
	protected Textbox providerId; // autowired
	protected Combobox paymentMode; // autowired
	protected Textbox bankBranchID; // autowired
	protected Textbox accountNumber; // autowired
	protected Checkbox receivableAdjustment; // autowired
	protected Textbox reconciliationAmount; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_ProviderId;
	protected Listbox sortOperator_PaymentMode;
	protected Listbox sortOperator_BankBranchID;
	protected Listbox sortOperator_AccountNumber;
	protected Listbox sortOperator_ReceivableAdjustment;
	protected Listbox sortOperator_ReconciliationAmount;
	protected Listbox sortOperator_Active;
	private List<ValueLabel> listPaymentMode = PennantStaticListUtil.getPaymentType();

	private transient VASProviderAccDetailService vASProviderAccDetailService;

	/**
	 * default constructor.<br>
	 */
	public VASProviderAccDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VASProviderAccDetail";
		super.pageRightName = "VASProviderAccDetailList";
		super.tableName = "VASProviderAccDetail_AView";
		super.queueTableName = "VASProviderAccDetail_View";
		super.enquiryTableName = "VASProviderAccDetail_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_VASProviderAccDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_VASProviderAccDetailList, borderLayout_VASProviderAccDetailList,
				listBoxVASProviderAccDetail, pagingVASProviderAccDetailList);
		setItemRender(new VASProviderAccDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VASProviderAccDetailList_VASProviderAccDetailSearch);
		registerButton(button_VASProviderAccDetailList_NewVASProviderAccDetail,
				"button_VASProviderAccDetailList_NewVASProviderAccDetail", true);
		List<String> excludeFiles = new ArrayList<String>();
		excludeFiles.add(DisbursementConstants.PAYMENT_TYPE_CASH);
		excludeFiles.add(DisbursementConstants.PAYMENT_TYPE_ESCROW);
		fillComboBox(this.paymentMode, PennantConstants.List_Select, listPaymentMode, excludeFiles);
		registerField("id");
		// registerField("providerDesc");
		registerField("providerDesc", listheader_ProviderId, SortOrder.NONE, providerId, sortOperator_ProviderId,
				Operators.STRING);
		registerField("paymentMode", listheader_PaymentMode, SortOrder.NONE, paymentMode, sortOperator_PaymentMode,
				Operators.STRING);
		registerField("accountNumber", listheader_AccountNumber, SortOrder.NONE, accountNumber,
				sortOperator_AccountNumber, Operators.STRING);
		/*
		 * registerField("bankBranchID", listheader_BankBranchID, SortOrder.NONE, bankBranchID,
		 * sortOperator_BankBranchID, Operators.STRING);
		 */
		/*
		 * registerField("receivableAdjustment", listheader_ReceivableAdjustment, SortOrder.NONE, receivableAdjustment,
		 * sortOperator_ReceivableAdjustment, Operators.BOOLEAN); registerField("reconciliationAmount",
		 * listheader_ReconciliationAmount, SortOrder.NONE, reconciliationAmount, sortOperator_ReconciliationAmount,
		 * Operators.STRING);
		 */
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_VASProviderAccDetailList_VASProviderAccDetailSearch(Event event) {
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
	public void onClick$button_VASProviderAccDetailList_NewVASProviderAccDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		VASProviderAccDetail vasprovideraccdetail = new VASProviderAccDetail();
		vasprovideraccdetail.setNewRecord(true);
		vasprovideraccdetail.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(vasprovideraccdetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onVASProviderAccDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVASProviderAccDetail.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		VASProviderAccDetail vasprovideraccdetail = vASProviderAccDetailService.getVASProviderAccDetail(id);

		if (vasprovideraccdetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id = ?");

		if (doCheckAuthority(vasprovideraccdetail, whereCond.toString(),
				new Object[] { vasprovideraccdetail.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vasprovideraccdetail.getWorkflowId() == 0) {
				vasprovideraccdetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vasprovideraccdetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param vasprovideraccdetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VASProviderAccDetail vasprovideraccdetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASProviderAccDetail", vasprovideraccdetail);
		arg.put("vASProviderAccDetailListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/VASProviderAccDetail/VASProviderAccDetailDialog.zul", null, arg);
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

	public void setVASProviderAccDetailService(VASProviderAccDetailService vASProviderAccDetailService) {
		this.vASProviderAccDetailService = vASProviderAccDetailService;
	}
}