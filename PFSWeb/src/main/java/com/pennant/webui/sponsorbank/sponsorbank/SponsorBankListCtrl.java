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
 * FileName    		:  SponsorBankListCtrl.java                                                   * 	  
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

package com.pennant.webui.sponsorbank.sponsorbank;

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

import com.pennant.backend.model.sponsorbank.SponsorBank;
import com.pennant.backend.service.sponsorbank.SponsorBankService;
import com.pennant.webui.sponsorbank.sponsorbank.model.SponsorBankListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/sponsorbank/SponsorBank/SponsorBankList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SponsorBankListCtrl extends GFCBaseListCtrl<SponsorBank> {

	private static final long				serialVersionUID	= 1L;
	private final static Logger				logger				= Logger.getLogger(SponsorBankListCtrl.class);

	protected Window						window_SponsorBankList;
	protected Borderlayout					borderLayout_SponsorBankList;
	protected Paging						pagingSponsorBankList;
	protected Listbox						listBoxSponsorBank;

	protected Listheader					listheader_SponsorBankCode;
	protected Listheader					listheader_SponsorBankName;
	protected Listheader					listheader_BankBranchCode;

	protected Button						button_SponsorBankList_NewSponsorBank;
	protected Button						button_SponsorBankList_SponsorBankSearch;

	protected Textbox						sponsorBankCode;
	protected Textbox						sponsorBankName;
	protected Textbox						bankCode;
	protected Textbox						bankBranchCode;

	protected Listbox						sortOperator_SponsorBankCode;
	protected Listbox						sortOperator_SponsorBankName;
	protected Listbox						sortOperator_BankCode;
	protected Listbox						sortOperator_BankBranchCode;

	private transient SponsorBankService	sponsorBankService;

	/**
	 * default constructor.<br>
	 */
	public SponsorBankListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SponsorBank";
		super.pageRightName = "SponsorBankList";
		super.tableName = "SponsorBank_AView";
		super.queueTableName = "SponsorBank_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SponsorBankList(Event event) {
		// Set the page level components.
		setPageComponents(window_SponsorBankList, borderLayout_SponsorBankList, listBoxSponsorBank,
				pagingSponsorBankList);
		setItemRender(new SponsorBankListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SponsorBankList_NewSponsorBank, "button_SponsorBankList_NewSponsorBank", true);
		registerButton(button_SponsorBankList_SponsorBankSearch);

		registerField("sponsorBankCode", listheader_SponsorBankCode, SortOrder.ASC, sponsorBankCode,
				sortOperator_SponsorBankCode, Operators.STRING);
		registerField("sponsorBankName", listheader_SponsorBankName, SortOrder.NONE, sponsorBankName,
				sortOperator_SponsorBankName, Operators.STRING);
		registerField("bankCode");
		registerField("bankBranchCode", listheader_BankBranchCode, SortOrder.NONE, bankBranchCode,
				sortOperator_BankBranchCode, Operators.STRING);

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
	public void onClick$button_SponsorBankList_SponsorBankSearch(Event event) {
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
	public void onClick$button_SponsorBankList_NewSponsorBank(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SponsorBank sponsorBank = new SponsorBank();
		sponsorBank.setNewRecord(true);
		sponsorBank.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(sponsorBank);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSponsorBankItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSponsorBank.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		SponsorBank sponsorBank = sponsorBankService.getSponsorBankById(id);

		if (sponsorBank == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SponsorBankCode='" + sponsorBank.getSponsorBankCode() + "' AND version="
				+ sponsorBank.getVersion() + " ";

		if (doCheckAuthority(sponsorBank, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && sponsorBank.getWorkflowId() == 0) {
				sponsorBank.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(sponsorBank);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param sponsorBank
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SponsorBank sponsorBank) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("sponsorBank", sponsorBank);
		arg.put("sponsorBankListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SponsorBank/SponsorBank/SponsorBankDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

	public void setSponsorBankService(SponsorBankService sponsorBankService) {
		this.sponsorBankService = sponsorBankService;
	}

}