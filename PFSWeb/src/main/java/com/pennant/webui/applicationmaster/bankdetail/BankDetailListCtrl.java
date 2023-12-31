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
 * * FileName : BankDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.bankdetail;

import java.util.Map;

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

import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.webui.applicationmaster.bankdetail.model.BankDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BankDetail/BankDetailList.zul file.
 */
public class BankDetailListCtrl extends GFCBaseListCtrl<BankDetail> {
	private static final long serialVersionUID = -3571720185247491921L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BankDetailList;
	protected Borderlayout borderLayout_BankDetailList;
	protected Paging pagingBankDetailList;
	protected Listbox listBoxBankDetail;

	protected Textbox bankCode;
	protected Textbox bankName;
	protected Checkbox active;

	protected Listbox sortOperator_bankName;
	protected Listbox sortOperator_active;
	protected Listbox sortOperator_bankCode;

	// List headers
	protected Listheader listheader_BankCode;
	protected Listheader listheader_BankName;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_BankDetailList_NewBankDetail;
	protected Button button_BankDetailList_BankDetailSearchDialog;

	private transient BankDetailService bankDetailService;

	/**
	 * default constructor.<br>
	 */
	public BankDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BankDetail";
		super.pageRightName = "BankDetailList";
		super.tableName = "BMTBankDetail_AView";
		super.queueTableName = "BMTBankDetail_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BankDetailList(Event event) {

		// Set the page level components.
		setPageComponents(window_BankDetailList, borderLayout_BankDetailList, listBoxBankDetail, pagingBankDetailList);
		setItemRender(new BankDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BankDetailList_NewBankDetail, "button_BankDetailList_NewBankDetail", true);
		registerButton(button_BankDetailList_BankDetailSearchDialog);

		registerField("bankCode", listheader_BankCode, SortOrder.ASC, bankCode, sortOperator_bankCode,
				Operators.STRING);
		registerField("bankName", listheader_BankName, SortOrder.NONE, bankName, sortOperator_bankName,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_active, Operators.SIMPLE);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BankDetailList_BankDetailSearchDialog(Event event) {
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
	public void onClick$button_BankDetailList_NewBankDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BankDetail bankDetail = new BankDetail();
		bankDetail.setNewRecord(true);
		bankDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(bankDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onBankDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBankDetail.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		BankDetail bankDetail = bankDetailService.getBankDetailById(id);

		if (bankDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where BankCode=?";

		if (doCheckAuthority(bankDetail, whereCond, new Object[] { bankDetail.getBankCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && bankDetail.getWorkflowId() == 0) {
				bankDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(bankDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBankDetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BankDetail aBankDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("bankDetail", aBankDetail);
		arg.put("bankDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/BankDetail/BankDetailDialog.zul", null, arg);
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

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}