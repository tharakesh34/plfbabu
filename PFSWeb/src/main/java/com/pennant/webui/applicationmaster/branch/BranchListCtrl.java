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
 * FileName    		:  BranchListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.branch;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.webui.applicationmaster.branch.model.BranchListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Branch/BranchList.zul file.
 */
public class BranchListCtrl extends GFCBaseListCtrl<Branch> {
	private static final long serialVersionUID = 1237735044265585362L;
	private static final Logger logger = Logger.getLogger(BranchListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BranchList;
	protected Borderlayout borderLayout_BranchList;
	protected Paging pagingBranchList;
	protected Listbox listBoxBranch;

	protected Textbox branchCode;
	protected Textbox branchDesc;
	protected Textbox branchCity;
	protected Textbox branchProvince;
	protected Textbox branchCountry;
	protected Textbox branchSwiftBrnCde;
	protected Textbox branchArea;
	protected Checkbox branchIsActive;

	protected Listbox sortOperator_branchCode;
	protected Listbox sortOperator_branchDesc;
	protected Listbox sortOperator_branchCity;
	protected Listbox sortOperator_branchProvince;
	protected Listbox sortOperator_branchCountry;
	protected Listbox sortOperator_branchSwiftBankCde;
	protected Listbox sortOperator_branchArea;
	protected Listbox sortOperator_branchIsActive;

	// List headers
	protected Listheader listheader_BranchCode;
	protected Listheader listheader_BranchDesc;
	protected Listheader listheader_BranchCity;
	protected Listheader listheader_BranchProvince;
	protected Listheader listheader_BranchCountry;
	protected Listheader listheader_BranchSwiftBankCde;
	protected Listheader listheader_BranchArea;
	protected Listheader listheader_BranchIsActive;

	// checkRights
	protected Button button_BranchList_NewBranch;
	protected Button button_BranchList_BranchSearchDialog;

	private transient BranchService branchService;

	/**
	 * default constructor.<br>
	 */
	public BranchListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Branch";
		super.pageRightName = "BranchList";
		super.tableName = "RMTBranches_AView";
		super.queueTableName = "RMTBranches_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BranchList(Event event) {

		// Set the page level components.
		setPageComponents(window_BranchList, borderLayout_BranchList, listBoxBranch, pagingBranchList);
		setItemRender(new BranchListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BranchList_NewBranch, "button_BranchList_NewBranch", true);
		registerButton(button_BranchList_BranchSearchDialog);

		registerField("branchCode", listheader_BranchCode, SortOrder.ASC, branchCode, sortOperator_branchCode,
				Operators.STRING);
		registerField("branchDesc", listheader_BranchDesc, SortOrder.NONE, branchDesc, sortOperator_branchDesc,
				Operators.STRING);
		registerField("branchCity", listheader_BranchCity, SortOrder.NONE, branchCity, sortOperator_branchCity,
				Operators.STRING);
		registerField("branchProvince", listheader_BranchProvince, SortOrder.NONE, branchProvince,
				sortOperator_branchProvince, Operators.STRING);
		registerField("lovDescBranchCityName");
		registerField("lovDescBranchProvinceName");
		registerField("lovDescBranchCountryName");
		registerField("lovDescBranchSwiftCountryName");
		registerField("branchCountry", listheader_BranchCountry, SortOrder.NONE, branchCountry,
				sortOperator_branchCountry, Operators.STRING);
		registerField("branchSwiftBrnCde", listheader_BranchSwiftBankCde, SortOrder.NONE, branchSwiftBrnCde,
				sortOperator_branchSwiftBankCde, Operators.STRING);
		registerField("pinAreaDesc", listheader_BranchArea, SortOrder.NONE, branchArea,
				sortOperator_branchArea, Operators.STRING);
		registerField("branchIsActive", listheader_BranchIsActive, SortOrder.NONE, branchIsActive,
				sortOperator_branchIsActive, Operators.SIMPLE);

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
	public void onClick$button_BranchList_BranchSearchDialog(Event event) {
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
	public void onClick$button_BranchList_NewBranch(Event event) {
		logger.debug("Entering");
		// Create a new entity.
		Branch branch = new Branch();
		branch.setNewRecord(true);
		branch.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(branch);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBranchItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBranch.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Branch branch = branchService.getBranchById(id);

		if (branch == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BranchCode='" + branch.getBranchCode() + "' AND version=" + branch.getVersion() + " ";

		if (doCheckAuthority(branch, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && branch.getWorkflowId() == 0) {
				branch.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(branch);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBranch
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Branch aBranch) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("branch", aBranch);
		arg.put("branchListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Branch/BranchDialog.zul", null, arg);
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

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
}