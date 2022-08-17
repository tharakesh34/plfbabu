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
 * * FileName : TDSReceivablesTxnListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.tds.receivables.tdsreceivablestxn;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.tds.receivables.TdsReceivableService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.tds.receivables.tdsreceivablestxn.model.TdsReceivablesTxnListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

/**
 * This is the controller class for the /WEB-INF/pages/tds.receivables/TdsReceivablesTxn/TDSReceivablesTxnList.zul file.
 * 
 */
public class TdsReceivablesTxnListCtrl extends GFCBaseListCtrl<TdsReceivable> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TdsReceivablesTxnListCtrl.class);

	protected Window window_TdsReceivablesTxnList;
	protected Borderlayout borderLayout_TdsReceivablesTxnList;
	protected Paging pagingTdsReceivablesTxnList;
	protected Listbox listBoxTdsReceivablesTxn;

	// List headers
	protected Listheader listheader_TanNumber;
	protected Listheader listheader_TanHolderName;
	protected Listheader listheader_AssessmentYear;
	protected Listheader listheader_CertificateNumber;
	protected Listheader listheader_CertificateDate;
	protected Listheader listheader_CertificateAmount;
	protected Listheader listheader_UtilizedAmount;
	protected Listheader listheader_BalanceAmount;

	// checkRights
	protected Button button_TdsReceivablesTxnList_NewTdsReceivablesTxn;
	protected Button button_TdsReceivablesTxnList_TdsReceivablesTxnSearch;

	protected Textbox certificateNumber; // autowired
	protected Datebox certificateDate; // autowired
	protected Textbox tanNumber; // autowired
	protected Textbox assessmentYear; // autowired

	protected Listbox sortOperator_CertificateNumber;
	protected Listbox sortOperator_CertificateDate;
	protected Listbox sortOperator_TanNumber;
	protected Listbox sortOperator_AssessmentYear;

	private transient TdsReceivablesTxnService tdsReceivablesTxnService;
	private transient TdsReceivableService tdsReceivableService;

	/**
	 * default constructor.<br>
	 */
	public TdsReceivablesTxnListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CertificateAdjustment";
		super.pageRightName = "TDSReceivableList";
		super.tableName = "TDS_RECEIVABLES_TXN_TVIEW";
		super.queueTableName = "TDS_RECEIVABLES_TXN_TVIEW";
		super.enquiryTableName = "TDS_RECEIVABLES_TXN_TVIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_TdsReceivablesTxnList(Event event) {
		logger.debug(Literal.ENTERING);
		try {

			// Set the page level components.
			setPageComponents(window_TdsReceivablesTxnList, borderLayout_TdsReceivablesTxnList,
					listBoxTdsReceivablesTxn, pagingTdsReceivablesTxnList);
			setItemRender(new TdsReceivablesTxnListModelItemRenderer());

			// Register buttons and fields.
			registerButton(button_TdsReceivablesTxnList_TdsReceivablesTxnSearch);
			registerButton(button_TdsReceivablesTxnList_NewTdsReceivablesTxn,
					"button_TDSReceivablesTxnList_NewTDSReceivablesTxn", true);
			registerField("tanNumber", listheader_TanNumber, SortOrder.NONE, tanNumber, sortOperator_TanNumber,
					Operators.STRING);
			registerField("tanHolderName", listheader_TanHolderName);
			registerField("assessmentYear", listheader_AssessmentYear, SortOrder.NONE, assessmentYear,
					sortOperator_AssessmentYear, Operators.STRING);
			registerField("CertificateNumber", listheader_CertificateNumber, SortOrder.NONE, certificateNumber,
					sortOperator_CertificateNumber, Operators.STRING);
			registerField("CertificateDate", listheader_CertificateDate, SortOrder.NONE, certificateDate,
					sortOperator_CertificateDate, Operators.DATE);
			registerField("CertificateAmount", listheader_CertificateAmount);
			registerField("UtilizedAmount", listheader_UtilizedAmount);
			registerField("BalanceAmount", listheader_BalanceAmount);
			registerField("ID");
			registerField("txnID");
			registerField("RecordStatus");
			doRenderPage();
			search();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_TdsReceivablesTxnList_TdsReceivablesTxnSearch(Event event) {
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
	public void onClick$button_TdsReceivablesTxnList_NewTdsReceivablesTxn(Event event) {
		logger.debug(Literal.ENTERING);

		TdsReceivablesTxn tdsReceivablesTxn = new TdsReceivablesTxn();
		tdsReceivablesTxn.setNewRecord(true);
		tdsReceivablesTxn.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("tdsReceivablesTxnListCtrl", this);
		arg.put("tdsReceivablesTxn", tdsReceivablesTxn);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivablesTxn/SelectTdsReceivableDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onTdsReceivableTxnItemDoubleClicked(Event event) {

		logger.debug(Literal.ENTERING);
		// Get the selected record.
		Listitem selectedItem = this.listBoxTdsReceivablesTxn.getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		TdsReceivable tdsReceivable = (TdsReceivable) selectedItem.getAttribute("data");

		TdsReceivable aTdsReceivable = tdsReceivableService.getTdsReceivable(tdsReceivable.getId(), TableType.AVIEW);

		if (aTdsReceivable == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		aTdsReceivable.setTdsReceivablesTxnList(tdsReceivablesTxnService.getTdsReceivablesTxnsByTxnId(
				tdsReceivable.getTxnID(), TableType.TEMP_TAB, PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE));
		TdsReceivablesTxn tdsReceivablesTxn = aTdsReceivable.getTdsReceivablesTxnList().get(0);
		String whereCond = "where id=? ";

		if (doCheckAuthority(tdsReceivablesTxn, whereCond, new Object[] { tdsReceivablesTxn.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && tdsReceivablesTxn.getWorkflowId() == 0) {
				tdsReceivablesTxn.setWorkflowId(getWorkFlowId());
			}
			aTdsReceivable.setTdsReceivablesTxn(tdsReceivablesTxn);
			doShowDialogPage(aTdsReceivable);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param TdsReceivablesTxn The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TdsReceivable tdsReceivable) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("tdsReceivable", tdsReceivable);
		arg.put("tdsReceivablesTxn", tdsReceivable.getTdsReceivablesTxn());
		arg.put("tdsReceivablesTxnListCtrl", this);
		arg.put("module", PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivablesTxn/TdsReceivablesTxnDialog.zul", null,
					arg);
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

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

	public void setTdsReceivableService(TdsReceivableService tdsReceivableService) {
		this.tdsReceivableService = tdsReceivableService;
	}

}