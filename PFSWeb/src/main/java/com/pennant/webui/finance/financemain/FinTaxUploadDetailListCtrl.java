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
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinTaxDetailUploadListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-06-2017 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-06-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.service.finance.FinTaxUploadDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.financemain.model.FinTaxUploadDetailItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * @author S051
 * 
 */
public class FinTaxUploadDetailListCtrl extends GFCBaseListCtrl<FinTaxUploadHeader> {

	private static final long serialVersionUID = -5901195042041627750L;
	private final static Logger logger = LogManager.getLogger(FinTaxUploadDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the ZUL-file are getting autoWired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinTaxUploadDetailList; // autoWired
	protected Borderlayout borderLayout_FinTaxDetailUploadList; // autoWired
	protected Paging pagingFinTaxDetailUploadList; // autoWired
	protected Listbox listBoxFinTaxUploadDetail;
	protected Grid searchGrid; // autoWired

	// List headers
	protected Listheader listheader_BatchReference; // autoWired
	protected Listheader listheader_FileName; // autoWired
	protected Listheader listheader_BatchCreatedDate; // autoWired
	protected Listheader listheader_NumberOfRecords; // autoWired
	protected Listheader listheader_Status; // autoWired
	protected Listheader listheader_RecordStatus; // autoWired
	protected Listheader listheader_RecordType; // autoWired

	// NEEDED for the ReUse in the SearchWindow

	protected Intbox batchReference; // autowired
	protected Listbox sortOperator_BatchReference; // autowired

	protected Textbox fileName; // autowired
	protected Listbox sortOperator_FileName; // autowired

	protected Datebox batchCreationDate; // autowired
	protected Listbox sortOperator_BatchCreationDate; // autowired

	protected Datebox batchApprovedDate; // autowired
	protected Listbox sortOperator_BatchApprovedDate; // autowired

	protected Combobox recordStatus; // autowired
	protected Listbox sortOperator_RecordStatus; // autowired

	protected Listbox recordType; // autowired
	protected Listbox sortOperator_RecordType; // autowired

	protected Listheader listheader_LoanStatus;

	// checkRights
	protected Button button_FinTaxUploadDetailList_Search; // autoWired
	protected Button button_FinTaxUploadDetailList_NewFinTaxUploadDetail; // autoWired

	private FinTaxUploadDetailService finTaxUploadDetailService;

	/**
	 * default constructor.<br>
	 */
	public FinTaxUploadDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinTaxUploadHeader";
		super.pageRightName = "FinTaxUploadHeaderList";
		super.tableName = "FinTaxUploadHeader_view";
		super.queueTableName = "FinTaxUploadHeader_Tview";
	}

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCreate$window_FinTaxUploadDetailList(Event event) throws Exception {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_FinTaxUploadDetailList, borderLayout_FinTaxDetailUploadList, listBoxFinTaxUploadDetail,
				pagingFinTaxDetailUploadList);
		setItemRender(new FinTaxUploadDetailItemRenderer());

		registerButton(button_FinTaxUploadDetailList_Search);
		registerButton(button_FinTaxUploadDetailList_NewFinTaxUploadDetail,
				"button_FinTaxUploadDetailList_NewFinTaxUploadDetail", true);

		registerField("batchReference", listheader_BatchReference, SortOrder.ASC, batchReference,
				sortOperator_BatchReference, Operators.NUMERIC);
		registerField("fileName", listheader_FileName, SortOrder.NONE, fileName, sortOperator_FileName,
				Operators.STRING);

		registerField("BatchCreatedDate", listheader_BatchCreatedDate, SortOrder.NONE, batchCreationDate,
				sortOperator_BatchCreationDate, Operators.DATE);

		registerField("batchApprovedDate", listheader_BatchCreatedDate, SortOrder.NONE, batchApprovedDate,
				sortOperator_BatchApprovedDate, Operators.DATE);

		registerField("numberofRecords", listheader_NumberOfRecords);
		registerField("status", listheader_Status);

		registerField("RecordStatus", listheader_RecordStatus);
		registerField("RecordType", listheader_RecordType);
		doSeFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();

	}

	private void doSeFieldProperties() {
		this.batchCreationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.batchApprovedDate.setFormat(DateFormat.SHORT_DATE.getPattern());

	}

	/**
	 * Method for call the FinanceMain dialog
	 */
	public void onClick$button_SearchDialog(Event event) throws Exception {
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
	 * @throws InterruptedException
	 */
	public void onClick$button_FinTaxUploadDetailList_NewFinTaxUploadDetail(Event event) throws InterruptedException {
		logger.debug("Entering");
		final FinTaxUploadHeader finTaxDetailUploadHeader = new FinTaxUploadHeader();
		finTaxDetailUploadHeader.setNewRecord(true);
		finTaxDetailUploadHeader.setWorkflowId(getWorkFlowId());
		doShowDialogPage(finTaxDetailUploadHeader);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onFinTaxUploadDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// Get the selected record.
		Listitem selectedItem = this.listBoxFinTaxUploadDetail.getSelectedItem();

		// Get the selected entity.
		long reference = (Long) selectedItem.getAttribute("id");

		FinTaxUploadHeader finTaxUploadHeader = finTaxUploadDetailService.getFinTaxUploadHeaderByRef(reference);

		if (finTaxUploadHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where BatchReference=?";

		if (doCheckAuthority(finTaxUploadHeader, whereCond, new Object[] { finTaxUploadHeader.getBatchReference() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && finTaxUploadHeader.getWorkflowId() == 0) {
				finTaxUploadHeader.setWorkflowId(getWorkFlowId());
			}

			doLoadWorkFlow(finTaxUploadHeader.isWorkflow(), finTaxUploadHeader.getWorkflowId(),
					finTaxUploadHeader.getNextTaskId());

			List<FinTaxUploadDetail> finTaxUploadDetailList = finTaxUploadDetailService.getFinTaxDetailUploadById(
					String.valueOf(finTaxUploadHeader.getBatchReference()), "_View",
					"'" + PennantConstants.RCD_STATUS_APPROVED + "'");
			finTaxUploadHeader.setFinTaxUploadDetailList(finTaxUploadDetailList);
			doShowDialogPage(finTaxUploadHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	private void doShowDialogPage(FinTaxUploadHeader aFinTaxDetailUploadheader) {
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
		arg.put("finTaxUploadDetailListCtrl", this);
		arg.put("finTaxUploadHeader", aFinTaxDetailUploadheader);

		// call the zul-file with the parameters packed in a map
		try {
			String zulPage = "/WEB-INF/pages/Finance/FinanceMain/FinTaxUploadDetailDialog.zul";
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
		final JdbcSearchObject<FinTaxUploadHeader> soFeePosting = searchObject;
		this.pagingFinTaxDetailUploadList.setActivePage(0);
		this.getPagedListWrapper().setSearchObject(soFeePosting);
		if (this.listBoxFinTaxUploadDetail != null) {
			this.listBoxFinTaxUploadDetail.getListModel();
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		doShowHelp(event);

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FinTaxUploadDetailList_Search(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) throws InterruptedException {
		new PTListReportUtils("FinTaxUploadHeader", searchObject, this.pagingFinTaxDetailUploadList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	public FinTaxUploadDetailService getFinTaxUploadDetailService() {
		return finTaxUploadDetailService;
	}

	public void setFinTaxUploadDetailService(FinTaxUploadDetailService finTaxUploadDetailService) {
		this.finTaxUploadDetailService = finTaxUploadDetailService;
	}

}
