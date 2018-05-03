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
 * FileName    		:  BankBranchListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.bankbranch;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.webui.bmtmasters.bankbranch.model.BankBranchListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/BMTMasters/BankBranch/BankBranchList.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class BankBranchListCtrl extends GFCBaseListCtrl<BankBranch> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BankBranchListCtrl.class);

	protected Window window_BankBranchList;
	protected Borderlayout borderLayout_BankBranchList;
	protected Paging pagingBankBranchList;
	protected Listbox listBoxBankBranch;

	protected Listheader listheader_BankCode;
	protected Listheader listheader_BranchCode;
	protected Listheader listheader_City;
	protected Listheader listheader_MICR;
	protected Listheader listheader_IFSC;

	protected Button button_BankBranchList_NewBankBranch;
	protected Button button_BankBranchList_BankBranchSearch;

	protected Textbox bankCode;
	protected Textbox branchCode;
	protected Textbox city;
	protected Textbox mICR;
	protected Textbox iFSC;

	protected Listbox sortOperator_BankCode;
	protected Listbox sortOperator_BranchCode;
	protected Listbox sortOperator_City;
	protected Listbox sortOperator_MICR;
	protected Listbox sortOperator_IFSC;

	private transient BankBranchService bankBranchService;

	/**
	 * default constructor.<br>
	 */
	public BankBranchListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BankBranch";
		super.pageRightName = "BankBranchList";
		super.tableName = "BankBranches_AView";
		super.queueTableName = "BankBranches_View";
		super.enquiryTableName = "BankBranches_TView";
	}
	

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BankBranchList(Event event) {
		// Set the page level components.
		setPageComponents(window_BankBranchList, borderLayout_BankBranchList, listBoxBankBranch, pagingBankBranchList);
		setItemRender(new BankBranchListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BankBranchList_NewBankBranch, "button_BankBranchList_NewBankBranch", true);
		registerButton(button_BankBranchList_BankBranchSearch);

		registerField("bankCode", listheader_BankCode, SortOrder.NONE, bankCode, sortOperator_BankCode,
				Operators.STRING);
		registerField("branchCode", listheader_BranchCode, SortOrder.NONE, branchCode, sortOperator_BranchCode,
				Operators.STRING);
		registerField("bankBranchID");
		registerField("city", listheader_City, SortOrder.NONE, city, sortOperator_City, Operators.STRING);
		registerField("mICR", listheader_MICR, SortOrder.NONE, mICR, sortOperator_MICR, Operators.STRING);
		registerField("iFSC", listheader_IFSC, SortOrder.NONE, iFSC, sortOperator_IFSC, Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_BankBranchList_BankBranchSearch(Event event) {
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

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_BankBranchList_NewBankBranch(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BankBranch bankBranch = new BankBranch();
		bankBranch.setNewRecord(true);
		bankBranch.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(bankBranch);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onBankBranchItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBankBranch.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		BankBranch bankBranch = bankBranchService.getBankBranchById(id);

		if (bankBranch == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BankBranchID='" + bankBranch.getBankBranchID() + "' AND version="
				+ bankBranch.getVersion() + " ";

		if (doCheckAuthority(bankBranch, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && bankBranch.getWorkflowId() == 0) {
				bankBranch.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(bankBranch);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param bankBranch
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BankBranch bankBranch) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("bankBranch", bankBranch);
		arg.put("bankBranchListCtrl", this);
		arg.put("enqModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/BMTMasters/BankBranch/BankBranchDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

}