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
 * FileName    		:  JVPostingListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.Fees.FeePostings;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.Fees.FeePostings.model.FeePostingsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Fees/FeePostings/FeePostingsList.zul file.
 */
public class FeePostingsListCtrl extends GFCBaseListCtrl<FeePostings> {
	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(FeePostingsListCtrl.class);

	protected Window					window_FeePostingList;
	protected Borderlayout				borderLayout_FeePostingList;
	protected Paging					pagingFeePostingList;
	protected Listbox					listBoxFeePosting;

	protected Listheader				listheader_PostingAgainst;
	protected Listheader				listheader_Reference;
	protected Listheader				listheader_FeetypeCode;
	protected Listheader				listheader_PostingAmount;
	protected Listheader				listheader_PostDate;
	protected Listheader				listheader_ValueDate;

	protected Button					button_FeePostingList_NewFeePosting;
	protected Button					button_FeePostingList_FeePostingSearch;

	protected Combobox					postAgainst;
	protected Listbox					sortOperator_PostingAgainst;

	protected Textbox					reference;
	protected Listbox					sortOperator_Reference;

	protected Textbox					feeTypeCode;
	protected Listbox					sortOperator_FeeTypeCode;

	protected Datebox					valueDate;
	protected Listbox					sortOperator_ValueDate;

	protected Textbox					moduleType;

	private transient FeePostingService	feePostingService;

	/**
	 * default constructor.<br>
	 */
	public FeePostingsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FeePostings";
		super.pageRightName = "FeePostings";
		super.tableName = "FeePostings_AView";
		super.queueTableName = "FeePostings_TView";
		super.enquiryTableName = "FeePostings_View";

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FeePostingList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeePostingList, borderLayout_FeePostingList, listBoxFeePosting, pagingFeePostingList);
		setItemRender(new FeePostingsListModelItemRenderer());
		
		registerField("postId");
		registerButton(button_FeePostingList_FeePostingSearch);
		registerButton(button_FeePostingList_NewFeePosting, "button_FeePostingList_NewFeePosting", true);

		registerField("postAgainst", listheader_PostingAgainst, SortOrder.ASC, postAgainst, sortOperator_PostingAgainst,
				Operators.STRING);
		registerField("Reference", listheader_Reference, SortOrder.NONE, reference, sortOperator_Reference,
				Operators.STRING);
		registerField("feeTyeCode", listheader_FeetypeCode, SortOrder.NONE, feeTypeCode, sortOperator_FeeTypeCode,
				Operators.STRING);
		registerField("ValueDate", listheader_ValueDate, SortOrder.NONE, valueDate, sortOperator_ValueDate,
				Operators.DATE);

		registerField("PostingAmount", listheader_PostingAmount);
		registerField("PostDate", listheader_PostDate);
		fillComboBox(this.postAgainst, "", PennantStaticListUtil.getpostingPurposeList(), "");
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
	public void onClick$button_FeePostingList_FeePostingSearch(Event event) {
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
	 * @throws InterruptedException
	 */
	public void onClick$button_FeePostingList_NewFeePosting(Event event) throws InterruptedException {
		logger.debug("Entering");
		final FeePostings aFeePostings = new FeePostings();
		aFeePostings.setNewRecord(true);
		aFeePostings.setWorkflowId(getWorkFlowId());
		doShowDialogPage(aFeePostings);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFeePostingItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFeePosting.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		FeePostings feePostings = feePostingService.getFeePostingsById(id);

		if (feePostings == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PostId='" + feePostings.getPostId() + "' AND version=" + feePostings.getVersion()
				+ " ";

		if (doCheckAuthority(feePostings, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && feePostings.getWorkflowId() == 0) {
				feePostings.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(feePostings);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 * @throws InterruptedException
	 */
	private void doShowDialogPage(FeePostings aFeePostings) throws InterruptedException {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */

		Map<String, Object> arg = getDefaultArguments();
		if (enqiryModule) {
			arg.put("enqModule", true);
		} else {
			arg.put("enqModule", false);
		}
		arg.put("FeePostingsListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			String zulPage = "/WEB-INF/pages/Fees/FeePostings/FeePostingsDialog.zul";
			arg.put("feePostings", aFeePostings);
			Executions.createComponents(zulPage, null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	public void refreshList() {
		final JdbcSearchObject<FeePostings> soFeePosting = searchObject;
		this.pagingFeePostingList.setActivePage(0);
		this.getPagedListWrapper().setSearchObject(soFeePosting);
		if (this.listBoxFeePosting != null) {
			this.listBoxFeePosting.getListModel();
		}
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

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) throws InterruptedException {
		new PTListReportUtils("FeePostings", searchObject, this.pagingFeePostingList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		search();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromWorkflow"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		search();
	}

	public void setFeePostingService(FeePostingService feePostingService) {
		this.feePostingService = feePostingService;
	}

}