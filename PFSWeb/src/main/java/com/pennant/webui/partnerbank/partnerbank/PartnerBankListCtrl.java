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
 * FileName    		:  PartnerBankListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.partnerbank.partnerbank;

import java.util.List;
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

import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.webui.partnerbank.partnerbank.model.PartnerBankListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/partnerbank/PartnerBank/PartnerBankList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PartnerBankListCtrl extends GFCBaseListCtrl<PartnerBank> {

	private static final long				serialVersionUID	= 1L;
	private static final Logger				logger				= Logger.getLogger(PartnerBankListCtrl.class);

	protected Window						window_PartnerBankList;
	protected Borderlayout					borderLayout_PartnerBankList;
	protected Paging						pagingPartnerBankList;
	protected Listbox						listBoxPartnerBank;

	protected Listheader					listheader_PartnerBankCode;
	protected Listheader					listheader_PartnerBankName;
	protected Listheader					listheader_BankBranchCode;
	protected Listheader					listheader_BankCode;

	protected Button						button_PartnerBankList_NewPartnerBank;
	protected Button						button_PartnerBankList_PartnerBankSearch;

	protected Textbox						partnerBankCode;
	protected Textbox						partnerBankName;
	protected Textbox						bankCode;
	protected Textbox						bankBranchCode;

	protected Listbox						sortOperator_PartnerBankCode;
	protected Listbox						sortOperator_PartnerBankName;
	protected Listbox						sortOperator_BankCode;
	protected Listbox						sortOperator_BankBranchCode;

	private transient PartnerBankService	partnerBankService;

	/**
	 * default constructor.<br>
	 */
	public PartnerBankListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PartnerBank";
		super.pageRightName = "PartnerBankList";
		super.tableName = "PartnerBanks_AView";
		super.queueTableName = "PartnerBanks_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PartnerBankList(Event event) {
		// Set the page level components.
		setPageComponents(window_PartnerBankList, borderLayout_PartnerBankList, listBoxPartnerBank,
				pagingPartnerBankList);
		setItemRender(new PartnerBankListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PartnerBankList_NewPartnerBank, "button_PartnerBankList_NewPartnerBank", true);
		registerButton(button_PartnerBankList_PartnerBankSearch);

		registerField("partnerBankCode", listheader_PartnerBankCode, SortOrder.ASC, partnerBankCode,
				sortOperator_PartnerBankCode, Operators.STRING);
		registerField("partnerBankName", listheader_PartnerBankName, SortOrder.NONE, partnerBankName,
				sortOperator_PartnerBankName, Operators.STRING);
		registerField("bankCode", listheader_BankCode,SortOrder.NONE,bankCode,sortOperator_BankCode,Operators.STRING);
		registerField("bankBranchCode", listheader_BankBranchCode, SortOrder.NONE, bankBranchCode,
				sortOperator_BankBranchCode, Operators.STRING);
		registerField("partnerBankId");

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_PartnerBankList_PartnerBankSearch(Event event) {
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
	public void onClick$button_PartnerBankList_NewPartnerBank(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		PartnerBank partnerBank = new PartnerBank();
		partnerBank.setNewRecord(true);
		partnerBank.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(partnerBank);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPartnerBankItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPartnerBank.getSelectedItem();

		// Get the selected entity.
		long id =  (long) selectedItem.getAttribute("id");
		PartnerBank partnerBank = partnerBankService.getPartnerBankById(id);

		if (partnerBank == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		
		// Get the disbursement modes list.
		List<PartnerBankModes> modesList=this.partnerBankService.getPartnerBankModesId(id);
		List<PartnerBranchModes> branchList=this.partnerBankService.getPartnerBranchModesId(id);
		if(modesList!=null && !modesList.isEmpty()){
			partnerBank.setPartnerBankModesList(modesList);
		}
		if(branchList!=null && !branchList.isEmpty()){
			partnerBank.setPartnerBranchModesList(branchList);
		}
		
		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PartnerBankCode='" + partnerBank.getPartnerBankCode() + "' AND version="
				+ partnerBank.getVersion() + " ";

		if (doCheckAuthority(partnerBank, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && partnerBank.getWorkflowId() == 0) {
				partnerBank.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(partnerBank);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param partnerBank
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PartnerBank partnerBank) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("partnerBank", partnerBank);
		arg.put("partnerBankListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/PartnerBank/PartnerBank/PartnerBankDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

}