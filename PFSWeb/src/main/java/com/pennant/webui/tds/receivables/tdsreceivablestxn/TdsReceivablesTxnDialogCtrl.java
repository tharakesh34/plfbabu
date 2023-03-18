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
 * * FileName : TdsReceivablesTxnDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.tds.receivables.tdsreceivablestxn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnStatus;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/tds.receivables/TdsReceivablesTxn/TDSReceivablesTxnDialog.zul
 * file. <br>
 */
public class TdsReceivablesTxnDialogCtrl extends GFCBaseCtrl<TdsReceivablesTxn> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TdsReceivablesTxnDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TdsReceivablesTxnDialog;
	protected Longbox transactionID;
	protected Textbox certificateNo;
	protected Datebox certificateDate;
	protected Textbox certificateQuarter;
	protected Textbox assessmentYear;
	protected Datebox dateOfReceipt;
	protected Decimalbox certificateAmount;
	protected Decimalbox balanceAmount;
	protected Textbox tanNumber;
	protected Decimalbox runningBalance;
	protected Combobox tranFinancialYear;
	protected Button btnView;
	protected Label label_TransactionID;
	protected Label label_CertificateNo;
	protected Label label_CertificateDate;
	protected Label label_CertificateQuarter;
	protected Label label_AssessmentYear;
	protected Label label_DateOfReceipt;
	protected Label label_CertificateAmount;
	protected Label label_BalanceAmount;
	protected Label label_TanNumber;
	protected Label label_TranFinancialYear;
	protected Label label_RunningBalance;
	private CurrencyBox crrAdjustment = null;
	private Hbox hbox = null;
	private A hyperlink = null;
	private Listhead listheader_TdsReceivablesTxn;
	private Listhead listheader_TdsReceivablesPost;

	protected Listbox listTdsReceivablesTxns;
	protected Listbox listTdsReceivablePost;
	private int ccyFormatter = 0;
	private String module;
	private TdsReceivable tdsReceivable;
	private List<TdsReceivablesTxn> tdsReceivablesTxnList = new ArrayList<TdsReceivablesTxn>();
	private Listheader listheader_AdjustmentAmount;
	private Listheader listheader_PostingsAdjustmentAmount;
	private Label window_TdsReceivablesTxnDialogTitle;

	private List<ValueLabel> financialYearList;
	private TdsReceivablesTxnListCtrl tdsReceivablesTxnListCtrl;
	private TdsReceivablesTxnCancelListCtrl tdsReceivablesTxnCancelListCtrl;
	private TdsReceivablesTxn tdsReceivablesTxn;

	private String centerStyle = "text-align:center;";
	private String rightStyle = "text-align:right;";

	private transient TdsReceivablesTxnService tdsReceivablesTxnService;
	private JVPostingService jVPostingService;

	private int recActivePage = 0;
	private int jvActivePage = 0;

	/**
	 * default constructor.<br>
	 */
	public TdsReceivablesTxnDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TdsReceivablesTxnDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_TdsReceivablesTxnDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		try {

			setPageComponents(window_TdsReceivablesTxnDialog);
			// Get the required arguments.
			doLoadPageArguments();
			setModuleComponents(module);

			if (!enqiryModule) {
				doLoadWorkFlow(this.tdsReceivablesTxn.isWorkflow(), this.tdsReceivablesTxn.getWorkflowId(),
						this.tdsReceivablesTxn.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			this.listTdsReceivablesTxns.setHeight("320px");
			this.listTdsReceivablePost.setHeight("320px");
			listTdsReceivablesTxns.setPageSize(8);
			listTdsReceivablePost.setPageSize(8);

			ccyFormatter = CurrencyUtil.getFormat("INR");

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.tdsReceivable);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doLoadPageArguments() {

		if (enqiryModule) {
			this.module = PennantConstants.RECEIVABLE_ENQUIRY_MODULE;
		} else {

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
			}

		}

		if (arguments.containsKey("tdsReceivable")) {
			this.tdsReceivable = (TdsReceivable) arguments.get("tdsReceivable");
			this.tdsReceivablesTxnList = tdsReceivable.getTdsReceivablesTxnList();
			TdsReceivable befImage2 = new TdsReceivable();
			BeanUtils.copyProperties(this.tdsReceivable, befImage2);
			this.tdsReceivable.setBefImage(befImage2);

		}

		if (arguments.containsKey("tdsReceivablesTxn")) {
			this.tdsReceivablesTxn = (TdsReceivablesTxn) arguments.get("tdsReceivablesTxn");

			if (tdsReceivablesTxn != null) {
				this.tdsReceivable.setTdsReceivablesTxn(this.tdsReceivablesTxn);
				TdsReceivablesTxn befImage = new TdsReceivablesTxn();
				BeanUtils.copyProperties(this.tdsReceivablesTxn, befImage);
				this.tdsReceivablesTxn.setBefImage(befImage);
			}

		}

		if (arguments.containsKey("tdsReceivablesTxnListCtrl")) {
			this.tdsReceivablesTxnListCtrl = (TdsReceivablesTxnListCtrl) arguments.get("tdsReceivablesTxnListCtrl");
		}

		if (arguments.containsKey("tdsReceivablesTxnCancelListCtrl")) {
			this.tdsReceivablesTxnCancelListCtrl = (TdsReceivablesTxnCancelListCtrl) arguments
					.get("tdsReceivablesTxnCancelListCtrl");
		}

	}

	private void setModuleComponents(String module) {

		switch (module) {

		case PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE:
			this.window_TdsReceivablesTxnDialogTitle
					.setValue(Labels.getLabel("window_TdsReceivablesTxnDialogAdj.title"));
			break;

		case PennantConstants.RECEIVABLE_ADJUSTMENT_CNCL_MODULE:
			this.window_TdsReceivablesTxnDialogTitle
					.setValue(Labels.getLabel("window_TdsReceivablesTxnDialogCancel.title"));
			break;

		case PennantConstants.RECEIVABLE_CANCEL_MODULE:
			this.window_TdsReceivablesTxnDialogTitle
					.setValue(Labels.getLabel("window_TdsReceivablesTxnDialogEnquiry.title"));
			window_TdsReceivablesTxnDialog.setWidth("98%");
			window_TdsReceivablesTxnDialog.setHeight("90%");
			super.enqiryModule = true;
			groupboxWf.setVisible(false);
			break;

		default:
			this.window_TdsReceivablesTxnDialogTitle
					.setValue(Labels.getLabel("window_TdsReceivablesTxnDialogEnquiry.title"));

		}
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.balanceAmount.setMaxlength(18);
		this.certificateDate.setFormat(PennantConstants.dateFormat);
		this.dateOfReceipt.setFormat(PennantConstants.dateFormat);
		this.balanceAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.balanceAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.balanceAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.certificateAmount.setMaxlength(18);
		this.certificateAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.certificateAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.certificateAmount.setScale(PennantConstants.defaultCCYDecPos);

		if (!this.tdsReceivable.isNew()) {
			this.tranFinancialYear.setDisabled(true);
		}

		this.transactionID.setReadonly(true);
		this.certificateNo.setReadonly(true);
		this.certificateDate.setDisabled(true);
		this.certificateQuarter.setReadonly(true);
		this.assessmentYear.setReadonly(true);
		this.dateOfReceipt.setDisabled(true);
		this.certificateAmount.setReadonly(true);
		this.balanceAmount.setReadonly(true);
		this.tanNumber.setReadonly(true);
		this.runningBalance.setReadonly(true);
		this.balanceAmount.setReadonly(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TdsReceivablesTxnDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TdsReceivablesTxnDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TdsReceivablesTxnDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TdsReceivablesTxnDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.transactionID.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_TransactionID"));
		this.label_TransactionID.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_TransactionID"));

		if (!StringUtils.equals(PennantConstants.RECEIVABLE_CANCEL_MODULE, this.module)) {
			this.btnView.setVisible(getUserWorkspace().isAllowed("button_TdsReceivablesTxnDialog_btnView"));

			this.label_CertificateNo.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateNo"));
			this.label_CertificateDate
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateDate"));
			this.label_CertificateQuarter
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateQuarter"));
			this.label_AssessmentYear
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_AssessmentYear"));
			this.label_DateOfReceipt.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_DateOfReceipt"));
			this.label_CertificateAmount
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateAmount"));
			this.label_BalanceAmount.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_BalanceAmount"));
			this.label_TanNumber.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_TanNumber"));
			this.label_TranFinancialYear
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_TranFinancialYear"));

			this.certificateNo.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateNo"));
			this.certificateDate.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateDate"));
			this.certificateQuarter
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateQuarter"));
			this.assessmentYear.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_AssessmentYear"));
			this.dateOfReceipt.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_DateOfReceipt"));
			this.certificateAmount
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_CertificateAmount"));
			this.balanceAmount.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_BalanceAmount"));
			this.tanNumber.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_TanNumber"));
			this.tranFinancialYear
					.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_TranFinancialYear"));
			if (!StringUtils.equals(PennantConstants.RECEIVABLE_ADJUSTMENT_CNCL_MODULE, this.module)
					&& !super.enqiryModule) {
				this.runningBalance.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_RunningBalance"));
				this.label_RunningBalance
						.setVisible(getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_RunningBalance"));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		if (StringUtils.equals(PennantConstants.RECEIVABLE_CANCEL_MODULE, this.module))
			this.window_TdsReceivablesTxnDialog.detach();
		else
			doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.tdsReceivablesTxn);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		if (StringUtils.equals(PennantConstants.RECEIVABLE_ADJUSTMENT_CNCL_MODULE, this.module)) {
			tdsReceivablesTxnCancelListCtrl.search();
		} else
			tdsReceivablesTxnListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.tdsReceivable);
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param tdsReceivablesTxn
	 * 
	 */
	public void doWriteBeanToComponents(TdsReceivable aTdsReceivable) {
		logger.debug(Literal.ENTERING);
		try {
			this.certificateNo.setValue(aTdsReceivable.getCertificateNumber());
			this.certificateDate.setValue(aTdsReceivable.getCertificateDate());
			this.certificateQuarter.setValue(aTdsReceivable.getCertificateQuarter());
			this.assessmentYear.setValue(aTdsReceivable.getAssessmentYear());
			this.dateOfReceipt.setValue(aTdsReceivable.getDateOfReceipt());
			this.certificateAmount.setValue(aTdsReceivable.getCertificateAmount());
			this.balanceAmount.setValue(PennantApplicationUtil.formateAmount(aTdsReceivable.getBalanceAmount(),
					PennantConstants.defaultCCYDecPos));
			this.tanNumber.setValue(aTdsReceivable.getTanNumber());
			this.runningBalance.setValue(
					PennantApplicationUtil.formateAmount(new BigDecimal(0), PennantConstants.defaultCCYDecPos));
			fillComboBox(this.tranFinancialYear, aTdsReceivable.getAssessmentYear(), dofillFinancialYearList(), "");

			this.balanceAmount.setValue(PennantApplicationUtil.formateAmount(aTdsReceivable.getBalanceAmount(),
					PennantConstants.defaultCCYDecPos));
			tdsReceivablesTxn = aTdsReceivable.getTdsReceivablesTxn();
			if (tdsReceivablesTxn != null && !tdsReceivablesTxn.isNewRecord()) {
				this.tranFinancialYear.setValue(aTdsReceivable.getTdsReceivablesTxn().getFinTranYear());
				this.transactionID.setValue(aTdsReceivable.getTdsReceivablesTxn().getTxnID());
				allocateItem(aTdsReceivable.getTdsReceivablesTxnList());
			}
			this.recordStatus.setValue(aTdsReceivable.getTdsReceivablesTxn().getRecordStatus());

			logger.debug(Literal.LEAVING);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}

	}

	public List<ValueLabel> dofillFinancialYearList() {
		logger.debug(Literal.ENTERING);

		Date finDate = tdsReceivablesTxnService.getMaxFinancialDate(this.tdsReceivable.getTanID());
		financialYearList = new ArrayList<ValueLabel>();
		if (finDate != null) {
			Date appDate = SysParamUtil.getAppDate();
			int finYear = DateUtil.getYear(finDate);
			int appYear = DateUtil.getYear(appDate);
			Date strtFinDate = DateUtil.getDate(Integer.valueOf(DateUtil.getYear(appDate)), 3, 1);

			if (DateUtil.compare(appDate, strtFinDate) == -1) {
				appYear--;
			}

			for (int i = appYear; i >= finYear - 1; i--) {
				financialYearList.add(new ValueLabel(i + PennantConstants.KEY_SEPERATOR + (i + 1),
						i + PennantConstants.KEY_SEPERATOR + (i + 1)));
				this.listTdsReceivablesTxns.getItems().clear();
				this.listTdsReceivablePost.getItems().clear();
			}
		}
		logger.debug(Literal.LEAVING);

		return financialYearList;
	}

	public void onClick$btnView(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tdsReceivable", this.tdsReceivable);
		Executions.createComponents("/WEB-INF/pages/tds.receivables/TdsReceivableCancel/TdsReceivableCancelView.zul",
				null, map);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$tranFinancialYear(Event event) throws Exception {

		logger.debug(Literal.ENTERING);
		this.listTdsReceivablePost.getItems().clear();
		this.listTdsReceivablesTxns.getItems().clear();
		if (!PennantConstants.List_Select.equals(this.tranFinancialYear.getSelectedItem().getValue().toString())) {
			String Years[] = tranFinancialYear.getSelectedItem().getValue().toString()
					.split(PennantConstants.KEY_SEPERATOR);

			Date fromDate = DateUtil.getDate(Integer.valueOf(Years[0]), 3, 1);
			Date toDate = DateUtil.getDate(Integer.valueOf(Years[1]), 2, 31);

			List<TdsReceivablesTxn> tdsReceivablesTxnsList = tdsReceivablesTxnService
					.getTdsReceivablesTxnsByTanId(this.tdsReceivable.getTanID(), fromDate, toDate);
			this.tdsReceivablesTxnList.clear();
			if (CollectionUtils.isNotEmpty(tdsReceivablesTxnsList)) {

				this.tdsReceivablesTxnList = tdsReceivablesTxnsList.stream().filter(
						tdsReceivablesTxn -> tdsReceivablesTxn.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0)
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(tdsReceivablesTxnList)
						&& CollectionUtils.isNotEmpty(tdsReceivablesTxnsList)) {
					MessageUtil.showMessage("Receipts for the Current Selected Financial Year are already adjusted");
					this.tranFinancialYear.setSelectedIndex(0);
				}
			}
		} else {
			tdsReceivablesTxnList.clear();
		}
		allocateItem(this.tdsReceivablesTxnList);

		logger.debug(Literal.LEAVING);
	}

	public void allocateItem(List<TdsReceivablesTxn> tdsReceivablesTxns) {

		try {
			this.listTdsReceivablePost.getItems().clear();
			this.listTdsReceivablesTxns.getItems().clear();
			BigDecimal runningBalance = BigDecimal.ZERO;
			BigDecimal balanceAmount = BigDecimal.ZERO;
			for (TdsReceivablesTxn tdsReceivablesTxn : tdsReceivablesTxns) {

				if (StringUtils.equals("R", tdsReceivablesTxn.getModule())) {

					Listitem item = new Listitem();
					Listcell lc;

					// FinReference
					lc = new Listcell(tdsReceivablesTxn.getFinReference());// 2
					lc.setStyle(centerStyle);
					lc.setParent(item);

					// ReceiptId
					lc = new Listcell(String.valueOf(tdsReceivablesTxn.getReceiptID()));
					lc.setStyle(centerStyle);
					lc.setParent(item);

					// ReceiptDate
					lc = new Listcell(String.valueOf(DateUtil.formatToLongDate(tdsReceivablesTxn.getReceiptDate())));
					lc.setStyle(centerStyle);
					lc.setParent(item);

					// Receipt Purpose
					lc = new Listcell(tdsReceivablesTxn.getReferenceType());
					lc.setStyle(centerStyle);
					lc.setParent(item);

					// Receipt Amount
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getReceiptAmount(), ccyFormatter));
					lc.setStyle(rightStyle);
					lc.setParent(item);

					// TDS Receivable Amount
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getTdsReceivable(), ccyFormatter));
					lc.setStyle(rightStyle);
					lc.setParent(item);

					// TDS Adjusted
					lc = new Listcell(
							PennantApplicationUtil.amountFormate(tdsReceivablesTxn.getTdsAdjusted(), ccyFormatter));
					lc.setStyle(rightStyle);
					lc.setParent(item);

					if (StringUtils.equals(PennantConstants.RECEIVABLE_CANCEL_MODULE, this.module)) {
						listheader_TdsReceivablesTxn.removeChild(listheader_AdjustmentAmount);
					} else {
						// Adjustment Amount
						lc = new Listcell();
						lc.setStyle(rightStyle);
						hbox = new Hbox();
						crrAdjustment = new CurrencyBox();
						crrAdjustment.setBalUnvisible(true);
						crrAdjustment.setStyle(rightStyle);
						crrAdjustment.setFormat(PennantApplicationUtil.getAmountFormate(2));
						crrAdjustment.setScale(2);
						crrAdjustment.setValue(
								PennantApplicationUtil.formateAmount(tdsReceivablesTxn.getAdjustmentAmount(), 2));
						crrAdjustment.setTextBoxWidth(130);

						if (getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_listheader_AdjustmentAmount")
								&& StringUtils.equals(PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE, this.module)) {
							crrAdjustment.setReadonly(false);
						} else
							crrAdjustment.setReadonly(true);

						crrAdjustment.setAttribute("object", tdsReceivablesTxn);
						crrAdjustment.addForward("onValueChange", self, "onChangeAdjustmentAmount", crrAdjustment);
						hbox.appendChild(crrAdjustment);
						hbox.setStyle(rightStyle);
						lc.setAttribute("cBox", crrAdjustment);
						lc.appendChild(hbox);
						lc.setParent(item);

						runningBalance = runningBalance.add(crrAdjustment.getValidateValue());
					}
					// TDS Balance Amount.
					balanceAmount = tdsReceivablesTxn.getTdsReceivable().subtract(tdsReceivablesTxn.getTdsAdjusted());
					lc = new Listcell(PennantApplicationUtil.amountFormate(balanceAmount, ccyFormatter));
					lc.setStyle(rightStyle);
					lc.setParent(item);

					hyperlink = new A();
					lc = new Listcell();
					hyperlink.setLabel(Labels.getLabel("listheader_ReceiptDetail.label"));
					hyperlink.addEventListener(Events.ON_CLICK,
							event -> showReceipts(tdsReceivablesTxn.getReceiptID()));
					lc.appendChild(hyperlink);
					lc.setStyle(centerStyle);
					lc.setParent(item);

					hyperlink = new A();
					lc = new Listcell();
					hyperlink.setLabel(Labels.getLabel("listheader_PostingDetail.label"));
					lc.appendChild(hyperlink);
					lc.setStyle(centerStyle);
					lc.setParent(item);
					hyperlink.addEventListener(Events.ON_CLICK,
							event -> showPostings(tdsReceivablesTxn.getReceiptID()));

					this.listTdsReceivablesTxns.appendChild(item);
				} else {
					runningBalance = allocatePostingsItem(tdsReceivablesTxn, runningBalance);
				}
			}
			this.tdsReceivable.setTdsReceivablesTxnList(tdsReceivablesTxns);
			this.runningBalance.setValue(this.balanceAmount.getValue().subtract(runningBalance));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	public BigDecimal allocatePostingsItem(TdsReceivablesTxn tdsReceivablesPost, BigDecimal runningBalance) {

		try {
			BigDecimal balanceAmount = BigDecimal.ZERO;

			Listitem item = new Listitem();
			Listcell lc;

			// FinReference
			lc = new Listcell(tdsReceivablesPost.getFinReference());// 2
			lc.setStyle(centerStyle);
			lc.setParent(item);

			// BatchRef
			lc = new Listcell(String.valueOf(tdsReceivablesPost.getReceiptID()));
			lc.setStyle(centerStyle);
			lc.setParent(item);

			// PostingDate
			lc = new Listcell(String.valueOf(DateUtil.formatToLongDate(tdsReceivablesPost.getReceiptDate())));
			lc.setStyle(centerStyle);
			lc.setParent(item);

			// TDS Receivable Amount
			lc = new Listcell(
					PennantApplicationUtil.amountFormate(tdsReceivablesPost.getTdsReceivable(), ccyFormatter));
			lc.setStyle(rightStyle);
			lc.setParent(item);

			// TDS Adjusted
			lc = new Listcell(PennantApplicationUtil.amountFormate(tdsReceivablesPost.getTdsAdjusted(), ccyFormatter));
			lc.setStyle(rightStyle);
			lc.setParent(item);

			if (StringUtils.equals(PennantConstants.RECEIVABLE_CANCEL_MODULE, this.module)) {
				listheader_TdsReceivablesPost.removeChild(listheader_PostingsAdjustmentAmount);
			} else {
				// Adjustment Amount
				lc = new Listcell();
				lc.setStyle(centerStyle);
				hbox = new Hbox();
				crrAdjustment = new CurrencyBox();
				crrAdjustment.setBalUnvisible(true);
				crrAdjustment.setStyle(rightStyle);
				crrAdjustment.setFormat(PennantApplicationUtil.getAmountFormate(2));
				crrAdjustment.setScale(2);
				crrAdjustment
						.setValue(PennantApplicationUtil.formateAmount(tdsReceivablesPost.getAdjustmentAmount(), 2));
				crrAdjustment.setTextBoxWidth(130);

				if (getUserWorkspace().isAllowed("TdsReceivablesTxnDialog_listheader_AdjustmentAmount")
						&& StringUtils.equals(PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE, this.module)) {
					crrAdjustment.setReadonly(false);
				} else
					crrAdjustment.setReadonly(true);

				crrAdjustment.setAttribute("object", tdsReceivablesPost);
				crrAdjustment.addForward("onValueChange", self, "onChangeAdjustmentAmount", crrAdjustment);
				hbox.appendChild(crrAdjustment);
				hbox.setStyle(rightStyle);
				lc.setAttribute("cBox", crrAdjustment);
				lc.appendChild(hbox);
				lc.setParent(item);

				runningBalance = runningBalance.add(crrAdjustment.getValidateValue());
			}
			// TDS Balance Amount.
			balanceAmount = tdsReceivablesPost.getTdsReceivable().subtract(tdsReceivablesPost.getTdsAdjusted());
			lc = new Listcell(PennantApplicationUtil.amountFormate(balanceAmount, ccyFormatter));
			lc.setStyle(rightStyle);
			lc.setParent(item);

			hyperlink = new A();
			lc = new Listcell();
			hyperlink.setLabel(Labels.getLabel("listheader_PostingDetail.label"));
			lc.appendChild(hyperlink);
			lc.setStyle(centerStyle);
			lc.setParent(item);
			hyperlink.addEventListener(Events.ON_CLICK, event -> showJvPostings(tdsReceivablesPost.getReceiptID()));

			this.listTdsReceivablePost.appendChild(item);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return runningBalance;
	}

	private void showJvPostings(long batchRef) {
		logger.debug(Literal.ENTERING);
		JVPosting jVPosting = jVPostingService.getJVPostingById(batchRef);
		Map<String, Object> arg = new HashMap<>();

		arg.put("jVPosting", jVPosting);
		arg.put("tdsAdjEnq", true);
		arg.put("enqModule", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Others/JVPosting/JVPostingDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void showPostings(long receiptId) {

		logger.debug(Literal.ENTERING);
		Map<String, Object> arg = new HashMap<>();

		arg.put("receiptId", receiptId);
		try {
			Executions.createComponents("/WEB-INF/pages/tds.receivables/PostingDetails/TdsPostingDetails.zul", null,
					arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChangeAdjustmentAmount(ForwardEvent event) throws Exception {

		CurrencyBox adjustmentAmount = (CurrencyBox) event.getOrigin().getTarget();
		Clients.clearWrongValue(adjustmentAmount);
		BigDecimal amount;

		if (StringUtils.isBlank(adjustmentAmount.getCcyTextBox().getValue())) {
			amount = BigDecimal.ZERO;
		} else
			amount = PennantApplicationUtil.unFormateAmount(adjustmentAmount.getValidateValue(), ccyFormatter);

		TdsReceivablesTxn tdsReceivablesTxn = (TdsReceivablesTxn) adjustmentAmount.getAttribute("object");
		BigDecimal balanceAmount = tdsReceivablesTxn.getTdsReceivable().subtract(tdsReceivablesTxn.getTdsAdjusted());

		if (balanceAmount.compareTo(amount) == -1) {
			adjustmentAmount.setValue(BigDecimal.ZERO);
			throw new WrongValueException(adjustmentAmount,
					Labels.getLabel("label_TdsReceivableDialog_AdjustementAmountErrorMsg.value"));
		} else {
			tdsReceivablesTxn.setAdjustmentAmount(amount);
		}
		recActivePage = this.listTdsReceivablesTxns.getActivePage();
		jvActivePage = this.listTdsReceivablePost.getActivePage();

		allocateItem(this.tdsReceivablesTxnList);

		this.listTdsReceivablesTxns.setActivePage(recActivePage);
		this.listTdsReceivablePost.setActivePage(jvActivePage);

	}

	private void showReceipts(long receiptId) {

		logger.debug(Literal.ENTERING);
		Map<String, Object> arg = new HashMap<>();

		arg.put("receiptHeaderid", receiptId);
		try {
			Executions.createComponents("/WEB-INF/pages/tds.receivables/ReceiptDetails/TdsReceiptDetails.zul", null,
					arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTDSReceivablesTxn
	 */
	public void doWriteComponentsToBean(TdsReceivable tdsReceivable) {
		logger.debug(Literal.ENTERING);

		PennantApplicationUtil.unFormateAmount(this.runningBalance.getValue(), PennantConstants.defaultCCYDecPos);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {

			BigDecimal RunningBalance = tdsReceivable.getBalanceAmount().subtract(PennantApplicationUtil
					.unFormateAmount(this.runningBalance.getValue(), PennantConstants.defaultCCYDecPos));
			BigDecimal certificateAmount = PennantApplicationUtil.unFormateAmount(this.certificateAmount.getValue(),
					PennantConstants.defaultCCYDecPos);

			if (StringUtils.equals(module, PennantConstants.RECEIVABLE_ADJUSTMENT_CNCL_MODULE)) {
				tdsReceivable.setBalanceAmount(tdsReceivable.getBalanceAmount().add(RunningBalance));
				tdsReceivable.setUtilizedAmount(certificateAmount.subtract(tdsReceivable.getBalanceAmount()));

			} else {
				tdsReceivable.setBalanceAmount(tdsReceivable.getBalanceAmount().subtract(RunningBalance));
				tdsReceivable.setUtilizedAmount(certificateAmount.subtract(tdsReceivable.getBalanceAmount()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!tranFinancialYear.isDisabled() && PennantConstants.List_Select
				.equals(this.tranFinancialYear.getSelectedItem().getValue().toString())) {
			wve.add(new WrongValueException(this.tranFinancialYear, Labels.getLabel("CHECK_NO_EMPTY",
					new String[] { Labels.getLabel("label_TdsReceivablesTxnDialog_TranFinancialYear.value") })));
		}

		Date appDate = SysParamUtil.getAppDate();
		if (CollectionUtils.isNotEmpty(this.tdsReceivablesTxnList)) {
			this.tdsReceivablesTxnList.forEach(tdsReceivablesTxn -> {
				tdsReceivablesTxn.setReceivableID(tdsReceivable.getId());
				tdsReceivablesTxn.setTranDate(appDate);
				if (StringUtils.equals(module, PennantConstants.RECEIVABLE_ADJUSTMENT_CNCL_MODULE)) {
					tdsReceivablesTxn.setStatus(TdsReceivablesTxnStatus.ADJUSTMENTCANCEL.getCode());
				}
				tdsReceivablesTxn.setFinTranYear(this.tranFinancialYear.getSelectedItem().getLabel());
			});
			tdsReceivable.setTdsReceivablesTxnList(this.tdsReceivablesTxnList);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = (WrongValueException[]) wve.toArray(new WrongValueException[wve.size()]);
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param tdsReceivablesTxn The entity that need to be render.
	 */
	public void doShowDialog(TdsReceivable tdsReceivable) {
		logger.debug(Literal.ENTERING);
		try {

			if (tdsReceivable.getTdsReceivablesTxn() != null && tdsReceivable.getTdsReceivablesTxn().isNew()) {
				this.btnCtrl.setInitNew();
				doEdit();
				// setFocus
			} else {
				if (isWorkFlowEnabled()) {
					if (tdsReceivable.getTdsReceivablesTxn() != null
							&& StringUtils.isNotBlank(tdsReceivable.getTdsReceivablesTxn().getRecordType())) {
						this.btnNotes.setVisible(true);
						doEdit();
					}
				} else {
					this.btnCtrl.setInitEdit();
					doReadOnly();
					btnCancel.setVisible(false);
				}
			}
			doEdit();

			if (enqiryModule) {
				this.btnCtrl.setBtnStatus_Enquiry();
				this.btnNotes.setVisible(false);
			}

			doWriteBeanToComponents(tdsReceivable);

			if (StringUtils.equals(PennantConstants.RECEIVABLE_CANCEL_MODULE, this.module)) {
				window_TdsReceivablesTxnDialog.doModal();
			} else
				setDialog(DialogType.EMBEDDED);

			logger.debug(Literal.LEAVING);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
	}

	/**
	 * Deletes a TdsReceivablesTxn object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final TdsReceivable aTdsReceivable = new TdsReceivable();
		BeanUtils.copyProperties(this.tdsReceivablesTxn, aTdsReceivable);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aTdsReceivable.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aTdsReceivable.getRecordType()).equals("")) {
				aTdsReceivable.setVersion(aTdsReceivable.getVersion() + 1);
				aTdsReceivable.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTdsReceivable.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aTdsReceivable.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTdsReceivable.getNextTaskId(),
							aTdsReceivable);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aTdsReceivable, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.tdsReceivablesTxn.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final TdsReceivable aTdsReceivable = new TdsReceivable();
		final TdsReceivablesTxn aTdsReceivablesTxn = new TdsReceivablesTxn();
		BeanUtils.copyProperties(this.tdsReceivable, aTdsReceivable);
		BeanUtils.copyProperties(this.tdsReceivablesTxn, aTdsReceivablesTxn);

		boolean isNew = false;

		doWriteComponentsToBean(aTdsReceivable);

		if (!tranFinancialYear.isDisabled()
				&& !"#".equals(this.tranFinancialYear.getSelectedItem().getValue().toString())
				&& CollectionUtils.isEmpty(this.tdsReceivablesTxnList)) {
			MessageUtil.showError(Labels.getLabel("No_Financial_Available"));
			return;
		}

		if (StringUtils.equals(module, PennantConstants.RECEIVABLE_ADJUSTMENT_MODULE)) {
			BigDecimal totalAdjAmount = BigDecimal.ZERO;
			BigDecimal balAmount = PennantApplicationUtil.unFormateAmount(balanceAmount.getValue(), ccyFormatter);
			for (TdsReceivablesTxn tdsReceivablesTxns : this.tdsReceivablesTxnList) {
				totalAdjAmount = totalAdjAmount.add(tdsReceivablesTxns.getAdjustmentAmount());
			}

			if (totalAdjAmount.compareTo(BigDecimal.ZERO) == 0) {
				MessageUtil.showError(Labels.getLabel("label_TdsReceivableDialog_UnAdjustedErrorMsg.value"));
				return;
			}

			if (balAmount.compareTo(totalAdjAmount) == -1) {
				MessageUtil.showError(Labels.getLabel("label_TdsReceivableDialog_AdjBalanceAmountErrorMsg.value"));
				return;

			}
		}
		isNew = aTdsReceivablesTxn.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTdsReceivablesTxn.getRecordType())) {
				aTdsReceivablesTxn.setVersion(aTdsReceivablesTxn.getVersion() + 1);
				if (isNew) {
					aTdsReceivablesTxn.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTdsReceivablesTxn.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTdsReceivablesTxn.setNewRecord(true);
				}
			}
		} else {
			aTdsReceivablesTxn.setVersion(aTdsReceivablesTxn.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		aTdsReceivable.setTdsReceivablesTxn(aTdsReceivablesTxn);
		try {
			if (doProcess(aTdsReceivable, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getstatus(aTdsReceivablesTxn.getRoleCode(),
						aTdsReceivablesTxn.getNextRoleCode(), aTdsReceivable.getCertificateNumber(),
						" TDS Certificate ", aTdsReceivablesTxn.getRecordStatus());
				if (StringUtils.equals(aTdsReceivablesTxn.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " TDS Certificate:" + aTdsReceivable.getCertificateNumber() + " Approved Successfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.tdsReceivable.getId());
	}

	private boolean doProcess(TdsReceivable aTdsReceivable, String tranType) throws Exception {
		logger.debug(Literal.ENTERING);
		TdsReceivablesTxn tdsReceivablesTxn = aTdsReceivable.getTdsReceivablesTxn();
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		tdsReceivablesTxn.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		tdsReceivablesTxn.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		tdsReceivablesTxn.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			tdsReceivablesTxn.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(tdsReceivablesTxn.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, tdsReceivablesTxn);
				}

				if (isNotesMandatory(taskId, tdsReceivablesTxn)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			tdsReceivablesTxn.setTaskId(taskId);
			tdsReceivablesTxn.setNextTaskId(nextTaskId);
			tdsReceivablesTxn.setRoleCode(getRole());
			tdsReceivablesTxn.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aTdsReceivable, tranType);
			String operationRefs = getServiceOperations(taskId, tdsReceivablesTxn);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aTdsReceivable, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aTdsReceivable, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * @throws Exception
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TdsReceivable aTdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		TdsReceivablesTxn aTdsReceivablesTxn = aTdsReceivable.getTdsReceivablesTxn();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = tdsReceivablesTxnService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = tdsReceivablesTxnService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = tdsReceivablesTxnService.doApprove(auditHeader);

					if (aTdsReceivablesTxn.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = tdsReceivablesTxnService.doReject(auditHeader);
					if (aTdsReceivablesTxn.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_TdsReceivablesTxnDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_TdsReceivablesTxnDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.tdsReceivablesTxn), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TdsReceivable aTdsReceivable, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTdsReceivable.getBefImage(), aTdsReceivable);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aTdsReceivable.getUserDetails(),
				getOverideMap());
	}

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

	public void setTdsReceivablesTxnListCtrl(TdsReceivablesTxnListCtrl tdsReceivablesTxnListCtrl) {
		this.tdsReceivablesTxnListCtrl = tdsReceivablesTxnListCtrl;
	}

	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

}