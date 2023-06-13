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
 * * FileName : PaymentHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.payment.paymentheader;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.feerefund.FeeRefundUtil;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.CustomerPaymentTxnsListCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.autorefund.RefundBeneficiary;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.receipt.constants.ExcessType;

/**
 * This is the controller class for the /WEB-INF/pages/payment/PaymentHeader/paymentHeaderDialog.zul file. <br>
 */
public class PaymentHeaderDialogCtrl extends GFCBaseCtrl<PaymentHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PaymentHeaderDialogCtrl.class);
	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PaymentHeaderDialog;
	protected Borderlayout borderlayoutPaymentHeader;

	protected Grid grid_Basicdetails;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab tabDisbInstructions;
	protected Tab payTypeInstructions;
	protected Tabpanel tabDisbInstructionsTabPanel;
	protected Listbox listBoxPaymentTypeInstructions;
	protected Decimalbox totAmount = null;

	protected Label lbl_LoanReference;
	protected Label lbl_LoanType;
	protected Label lbl_CustCIF;
	protected Label lbl_Currency;
	protected Label lbl_startDate;
	protected Label lbl_MaturityDate;
	protected Label lbl_ODAgainstLoan;
	protected Label lbl_ODAgainstCustomer;

	protected Textbox tranModule;
	protected Textbox tranReference;
	protected Textbox tranBatch;
	protected Longbox paymentId;
	protected Textbox statusCode;
	protected Textbox statusDesc;
	protected Combobox tranStatus;
	protected Row row_tranStatus;
	protected Groupbox gb_TransactionDetails;

	protected Listheader listheader_PaymentHeaderDialog_AvailableAmount;
	protected Listheader listheader_PaymentHeaderDialog_Balance;

	private PaymentHeader paymentHeader;
	private FinanceMain financeMain;

	private transient PaymentHeaderListCtrl paymentHeaderListCtrl;
	private transient CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl;
	private transient PaymentHeaderService paymentHeaderService;
	private transient ReceiptCalculator receiptCalculator;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private transient PaymentInstructionDialogCtrl disbursementInstructionsDialogCtrl;
	private int ccyFormatter = 0;
	private List<PaymentDetail> paymentDetailList = new ArrayList<>();
	protected String selectMethodName = "onSelectTab";
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private boolean isAccountingExecuted = false;
	private Long accountsetId;
	// Add list Manualadvise
	private Listheader listheader_PaymentHeaderDialog_button;
	private Grid grid_basicDetails;
	private Map<String, BigDecimal> taxPercMap = null;
	private boolean isFromCustomerPaymentMenu = false;
	private PaymentTransaction paymentTransaction;
	private transient FinAdvancePaymentsService finAdvancePaymentsService;
	private Button btnSave_payment;
	private FeeTypeService feeTypeService;
	private RefundBeneficiary refundBeneficiary;
	private CrossLoanKnockOffDAO crossLoanKnockOffDAO;
	protected boolean leiMandatory = false;

	private List<String> allowedExcesTypes = PennantStaticListUtil.getAllowedExcessTypeList();

	/**
	 * default constructor.<br>
	 */
	public PaymentHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PaymentHeaderDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.paymentHeader.getPaymentId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PaymentHeaderDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PaymentHeaderDialog);
		try {
			// Get the required arguments.
			this.paymentHeader = (PaymentHeader) arguments.get("paymentHeader");
			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}
			this.paymentHeaderListCtrl = (PaymentHeaderListCtrl) arguments.get("paymentHeaderListCtrl");
			this.financeMain = (FinanceMain) arguments.get("financeMain");

			if (arguments.containsKey("customerPaymentTxnsListCtrl")) {
				customerPaymentTxnsListCtrl = (CustomerPaymentTxnsListCtrl) arguments
						.get("customerPaymentTxnsListCtrl");
			}
			if (arguments.containsKey("isFromCustomerPaymentMenu")) {
				this.isFromCustomerPaymentMenu = (boolean) arguments.get("isFromCustomerPaymentMenu");
				this.paymentTransaction = (PaymentTransaction) arguments.get("paymentTransaction");
			}

			if (this.paymentHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			PaymentHeader paymentHeader = new PaymentHeader();
			BeanUtils.copyProperties(this.paymentHeader, paymentHeader);
			this.paymentHeader.setBefImage(paymentHeader);
			// Render the page and display the data.
			doLoadWorkFlow(this.paymentHeader.isWorkflow(), this.paymentHeader.getWorkflowId(),
					this.paymentHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			}
			if (enqiryModule) {
				listBoxPaymentTypeInstructions.setHeight("350px");
				this.borderlayoutPaymentHeader.setHeight(getBorderLayoutHeight());
			}
			ccyFormatter = CurrencyUtil.getFormat(this.financeMain.getFinCcy());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.paymentHeader);
			this.listBoxPaymentTypeInstructions
					.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount() + 3));

			processPaymentInstructionDetails();
		} catch (Exception e) {
			closeDialog();
			if (getDisbursementInstructionsDialogCtrl() != null) {
				getDisbursementInstructionsDialogCtrl().closeDialog();
			}
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void processPaymentInstructionDetails() {
		logger.debug(Literal.ENTERING);
		if (isFromCustomerPaymentMenu) {
			this.row_tranStatus.setVisible(isFromCustomerPaymentMenu);
			this.tranStatus.setDisabled(enqiryModule);
			this.listBoxPaymentTypeInstructions.setHeight("250px");
			this.btnSave_payment.setVisible(!enqiryModule);
			this.gb_TransactionDetails.setVisible(true);

			String tranModule = "";
			if ("DISB".equals(this.paymentTransaction.getTranModule())) {
				tranModule = "Disbursement";
			} else if ("PYMT".equals(this.paymentTransaction.getTranModule())) {
				tranModule = "Payments";
			}
			this.tranModule.setValue(tranModule);

			this.tranReference.setValue(this.paymentTransaction.getTranReference());
			this.tranBatch.setValue(this.paymentTransaction.getTranBatch());
			this.paymentId.setValue(this.paymentTransaction.getPaymentId());
			this.statusCode.setValue(this.paymentTransaction.getStatusCode());
			this.statusDesc.setValue(this.paymentTransaction.getStatusDesc());
			String tranStatus = "";
			if ("P".equals(this.paymentTransaction.getTranStatus())) {
				tranStatus = DisbursementConstants.STATUS_PAID;
			} else if ("R".equals(this.paymentTransaction.getTranStatus())) {
				tranStatus = DisbursementConstants.STATUS_REJECTED;
			}
			fillComboBox(this.tranStatus, tranStatus, PennantStaticListUtil.getDisbursementStatus(), "");

		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave_payment(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSavebtnSave_payment();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void doSavebtnSave_payment() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PaymentTransaction paymentTransaction = new PaymentTransaction();
		BeanUtils.copyProperties(getPaymentTransaction(), paymentTransaction);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (PennantConstants.List_Select.equals(this.tranStatus.getSelectedItem().getValue())) {
			throw new WrongValueException(this.tranStatus, "Status Update is mandatory.");
		}
		paymentTransaction.setTranStatus(this.tranStatus.getSelectedItem().getValue());
		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		try {
			this.finAdvancePaymentsService.processPayments(paymentTransaction);
			refreshList();
			closeDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doPostClose() {
		if (getDisbursementInstructionsDialogCtrl() != null) {
			getDisbursementInstructionsDialogCtrl().closeDialog();
		}
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.paymentHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		if (paymentHeaderListCtrl != null) {
			paymentHeaderListCtrl.search();
		}
		if (customerPaymentTxnsListCtrl != null) {
			customerPaymentTxnsListCtrl.search();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.paymentHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param paymentHeader
	 * 
	 */
	public void doWriteBeanToComponents(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.ENTERING);

		this.lbl_LoanReference.setValue(this.financeMain.getFinReference());
		this.lbl_LoanType.setValue(this.financeMain.getFinType() + "-" + this.financeMain.getLovDescFinTypeName());
		this.lbl_CustCIF
				.setValue(this.financeMain.getLovDescCustCIF() + "-" + this.financeMain.getLovDescCustShrtName());
		this.lbl_Currency
				.setValue(this.financeMain.getFinCcy() + "- " + CurrencyUtil.getCcyDesc(this.financeMain.getFinCcy()));
		this.lbl_startDate
				.setValue(DateUtil.format(this.financeMain.getFinStartDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate
				.setValue(DateUtil.format(this.financeMain.getMaturityDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_ODAgainstLoan
				.setValue(PennantApplicationUtil.amountFormate(aPaymentHeader.getOdAgainstLoan(), ccyFormatter));
		this.lbl_ODAgainstCustomer
				.setValue(PennantApplicationUtil.amountFormate(aPaymentHeader.getOdAgainstCustomer(), ccyFormatter));
		// Disbursement Instructions tab.
		appendDisbursementInstructionTab(aPaymentHeader);

		// Fill PaymentType Instructions.

		calculatePaymentDetail(aPaymentHeader);

		this.recordStatus.setValue(aPaymentHeader.getRecordStatus());

		// Accounting Details Tab Addition
		if (getWorkFlow() != null && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			appendAccountingDetailTab(aPaymentHeader, true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onSelectTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doClearMessage();

		if (StringUtils.equals(module, AssetConstants.UNIQUE_ID_ACCOUNTING)) {
			doWriteComponentsToBean(paymentHeader);
			appendAccountingDetailTab(this.paymentHeader, false);
		}
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private void appendDisbursementInstructionTab(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.ENTERING);

		PaymentInstruction pi = aPaymentHeader.getPaymentInstruction();

		if (pi == null) {
			Date appDate = SysParamUtil.getAppDate();
			pi = refundBeneficiary.getBeneficiary(this.financeMain.getFinID(), appDate, true);

			if (pi == null) {
				pi = new PaymentInstruction();
			}
		} else {
			pi = FeeRefundUtil.getPI(aPaymentHeader, pi);
		}

		Map<String, Object> map = new HashMap<>();
		map.put("paymentInstruction", pi);
		map.put("roleCode", getRole());
		map.put("paymentHeader", aPaymentHeader);
		map.put("paymentHeaderDialogCtrl", this);
		map.put("financeMain", this.financeMain);
		map.put("tab", this.tabDisbInstructions);
		map.put("ccyFormatter", ccyFormatter);
		map.put("enqiryModule", this.enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Payment/PaymentInstructionDialog.zul",
					tabDisbInstructionsTabPanel, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(PaymentHeader aPaymentHeader, boolean onLoadProcess) {
		logger.debug("Entering");

		PaymentInstruction paymentInstruction = aPaymentHeader.getPaymentInstruction();
		if (paymentInstruction == null) {
			paymentInstruction = new PaymentInstruction();
		}

		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}

		Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING);
		if (tabpanel != null) {
			tabpanel.setHeight(getListBoxHeight(7));

		}
		if (!onLoadProcess) {
			accountsetId = AccountingEngine.getAccountSetID(this.financeMain, AccountingEvent.PAYMTINS,
					FinanceConstants.MODULEID_FINTYPE);

			final Map<String, Object> map = new HashMap<>();
			map.put("paymentInstruction", paymentInstruction);
			map.put("acSetID", accountsetId);
			map.put("enqModule", enqiryModule);
			map.put("dialogCtrl", this);
			map.put("isNotFinanceProcess", true);
			map.put("postAccReq", false);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(listBoxPaymentTypeInstructions.getItems())) {
			for (int i = 0; i < listBoxPaymentTypeInstructions.getItems().size() - 1; i++) {
				Listitem listitem = listBoxPaymentTypeInstructions.getItems().get(i);

				if (!"Y".equals(listitem.getAttribute("index"))) {
					continue;
				}

				List<Listcell> listCells = listitem.getChildren();

				Decimalbox avaibleAmt = (Decimalbox) listCells.get(4).getChildren().get(0);
				Decimalbox payAmt = (Decimalbox) listCells.get(7).getChildren().get(0);
				Decimalbox gstAmt = (Decimalbox) listCells.get(5).getChildren().get(0);

				Clients.clearWrongValue(payAmt);

				BigDecimal total = avaibleAmt.getValue().add(gstAmt.getValue());
				if ((total.compareTo(payAmt.getValue())) == -1) {
					throw new WrongValueException(payAmt,
							Labels.getLabel("label_PaymentHeaderDialog_paymentAmountErrorMsg.value"));
				}
			}

		}

		if (this.totAmount != null) {
			this.totAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PaymentHeaderDialog_totalpaymentAmount.value"), ccyFormatter, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		if (this.totAmount != null) {
			this.totAmount.setConstraint("");
		}
		this.disbursementInstructionsDialogCtrl.leiNumber.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPaymentHeaderd
	 */
	public void doWriteComponentsToBean(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fin Id
		try {
			aPaymentHeader.setFinID(this.financeMain.getFinID());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fin Reference
		try {
			aPaymentHeader.setFinReference(this.financeMain.getFinReference());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Type
		try {
			aPaymentHeader.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Amount
		try {
			aPaymentHeader
					.setPaymentAmount(PennantApplicationUtil.unFormateAmount(this.totAmount.getValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Status
		try {
			if (aPaymentHeader.isNewRecord()) {
				aPaymentHeader.setStatus(RepayConstants.PAYMENT_INTIATED);
				Timestamp sysDate = new Timestamp(System.currentTimeMillis());
				aPaymentHeader.setCreatedOn(sysDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			this.payTypeInstructions.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (getDisbursementInstructionsDialogCtrl() != null) {
			PaymentInstruction paymentInstruction = getDisbursementInstructionsDialogCtrl().onSave();
			aPaymentHeader.setPaymentInstruction(paymentInstruction);
		}
		// Save PaymentDetails
		savePaymentDetails(aPaymentHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param paymentHeader The entity that need to be render.
	 */
	public void doShowDialog(PaymentHeader paymentHeader) {
		logger.debug(Literal.LEAVING);

		if (paymentHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(paymentHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(paymentHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PaymentHeader aPaymentHeader = new PaymentHeader();
		BeanUtils.copyProperties(this.paymentHeader, aPaymentHeader);

		doDelete(String.valueOf(aPaymentHeader.getPaymentId()), aPaymentHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.paymentHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.paymentHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		this.listheader_PaymentHeaderDialog_Balance.setVisible(!enqiryModule);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final PaymentHeader aPaymentHeader = new PaymentHeader();
		BeanUtils.copyProperties(this.paymentHeader, aPaymentHeader);
		boolean isNew = false;

		doSetValidation();

		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			boolean validate = false;
			validate = validateAccounting(validate);
			// Accounting Details Validations
			if (validate) {
				if (!isAccountingExecuted) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (!this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") && accountingDetailDialogCtrl
						.getDisbCrSum().compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}
		}

		// #Bug Fix 153031
		if (totAmount == null) {
			MessageUtil.showError(Labels.getLabel("label_PaymentHeaderDialog_NoPayInstruction.value"));
			return;
		}

		doWriteComponentsToBean(aPaymentHeader);

		isNew = aPaymentHeader.isNewRecord();

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPaymentHeader.getRecordType())) {
				aPaymentHeader.setVersion(aPaymentHeader.getVersion() + 1);
				if (isNew) {
					aPaymentHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPaymentHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPaymentHeader.setNewRecord(true);
				}
			}
		} else {
			aPaymentHeader.setVersion(aPaymentHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(aPaymentHeader, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(aPaymentHeader.getRoleCode(),
						aPaymentHeader.getNextRoleCode(), aPaymentHeader.getFinReference(), " Payment Instructions ",
						aPaymentHeader.getRecordStatus(), aPaymentHeader.getNextRoleCode());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
				if (getDisbursementInstructionsDialogCtrl() != null) {
					getDisbursementInstructionsDialogCtrl().closeDialog();
				}

				// User Notifications Message/Alert
				if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
					publishNotification(Notify.ROLE, aPaymentHeader.getFinReference(), aPaymentHeader);
				} else {
					publishNotification(Notify.ROLE, aPaymentHeader.getFinReference(), aPaymentHeader,
							financeMain.getFinPurpose(), financeMain.getFinBranch());
				}

			}
		} catch (final Exception e) {
			logger.error(e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean validateAccounting(boolean validate) {
		if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")
				|| this.userAction.getSelectedItem().getLabel().contains("Reject")
				|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
			validate = false;
		} else {
			validate = true;
		}
		return validate;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(PaymentHeader aPaymentHeader, String tranType) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPaymentHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPaymentHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPaymentHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPaymentHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			Timestamp sysDate = new Timestamp(System.currentTimeMillis());
			if (PennantConstants.RCD_STATUS_APPROVED.equals(aPaymentHeader.getRecordStatus())) {
				aPaymentHeader.setStatus(RepayConstants.PAYMENT_APPROVE);
				aPaymentHeader.setApprovedOn(sysDate);
			}

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPaymentHeader.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPaymentHeader);
				}

				if (isNotesMandatory(taskId, aPaymentHeader)) {
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
			aPaymentHeader.setTaskId(taskId);
			aPaymentHeader.setNextTaskId(nextTaskId);
			aPaymentHeader.setRoleCode(getRole());
			aPaymentHeader.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(aPaymentHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aPaymentHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPaymentHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPaymentHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		PaymentHeader aPaymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = paymentHeaderService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = paymentHeaderService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = paymentHeaderService.doApprove(auditHeader);
					if (aPaymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = paymentHeaderService.doReject(auditHeader);
					if (aPaymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_PaymentHeaderDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_PaymentHeaderDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.paymentHeader), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(PaymentHeader aPaymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPaymentHeader.getBefImage(), aPaymentHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPaymentHeader.getUserDetails(),
				getOverideMap());

	}

	/****************************************************
	 * Account Executing * ***************************************************
	 */

	/**
	 * Method for Executing Accountng Details
	 */
	public void executeAccounting() {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();

		paymentHeaderService.executeAccountingProcess(aeEvent, this.paymentHeader);
		postingsPreparationUtil.getAccounting(aeEvent);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(aeEvent.getReturnDataSet());
			isAccountingExecuted = true;
		}

		List<PaymentDetail> paymentDetailsList = this.paymentHeader.getPaymentDetailList();
		List<String> feeTypeCodes = new ArrayList<>();

		for (PaymentDetail paymentDetail : paymentDetailsList) {
			feeTypeCodes.add(paymentDetail.getFeeTypeCode());
		}

		if (feeTypeCodes != null && !feeTypeCodes.isEmpty()) {
			aeEvent.setFeesList(feeTypeService.getFeeTypesForAccountingByCode(feeTypeCodes));
		}

		logger.debug(Literal.LEAVING);
	}

	private void calculatePaymentDetail(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.ENTERING);

		List<PaymentDetail> detailList = new ArrayList<>();

		List<FinExcessAmount> excessList = processFinExcessAmount(aPaymentHeader);

		for (FinExcessAmount fea : excessList) {
			PaymentDetail pd = new PaymentDetail();
			if (allowedExcesTypes.contains(fea.getAmountType())) {
				if (enqiryModule) {
					pd.setNewRecord(true);
					pd.setReferenceId(fea.getId());
					pd.setAvailableAmount(fea.getBalanceAmt());
					pd.setAmountType(fea.getAmountType());
					pd.setReceiptID(fea.getReceiptID());
					pd.setValueDate(fea.getValueDate());
				} else {
					/*
					 * BigDecimal progressAmt = paymentHeaderService.getInProgressExcessAmt(this.financeMain.getFinID(),
					 * fea.getReceiptID());
					 */
					pd.setNewRecord(true);
					pd.setReferenceId(fea.getId());
					BigDecimal refAmount = BigDecimal.ZERO;

					BigDecimal amount = crossLoanKnockOffDAO.getTransferAmount(fea.getExcessID());

					for (PaymentDetail pDtl : paymentHeader.getPaymentDetailList()) {
						if (fea.getExcessID() == pDtl.getReferenceId()) {
							refAmount = pDtl.getAmount();
						}
					}
					if (fea.getBalanceAmt().compareTo(BigDecimal.ZERO) == 0) {
						amount = fea.getReservedAmt().subtract(refAmount);
					}
					pd.setAvailableAmount(fea.getBalanceAmt().add(amount)/* .subtract(progressAmt) */);
					pd.setAmountType(fea.getAmountType());
					pd.setReceiptID(fea.getReceiptID());
					pd.setValueDate(fea.getValueDate());
				}
				detailList.add(pd);
			}
		}

		List<ManualAdvise> manualAdviseList = null;
		if (enqiryModule) {
			manualAdviseList = this.paymentHeaderService.getManualAdviseForEnquiry(this.financeMain.getFinID());
		} else {
			manualAdviseList = this.paymentHeaderService.getManualAdvise(this.financeMain.getFinID());
		}

		Map<Long, BigDecimal> advisesInProgess = new HashMap<Long, BigDecimal>();
		advisesInProgess = this.paymentHeaderService.getAdvisesInProgess(this.financeMain.getFinID());

		for (ManualAdvise ma : manualAdviseList) {
			PaymentDetail pd = new PaymentDetail();

			pd.setNewRecord(true);
			pd.setReferenceId(ma.getAdviseID());
			if (advisesInProgess.containsKey(ma.getAdviseID())) {
				BigDecimal progressAmt = advisesInProgess.get(ma.getAdviseID());
				pd.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount())
						.subtract(progressAmt));
			} else {
				pd.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount()));
			}
			pd.setAmountType(String.valueOf(ma.getAdviseType()));
			pd.setFeeTypeCode(ma.getFeeTypeCode());
			pd.setFeeTypeDesc(ma.getFeeTypeDesc());
			pd.setAdviseAmount(ma.getAdviseAmount());
			pd.setValueDate(ma.getValueDate());
			pd.setPrvGST(CalculationUtil.getTotalPaidGST(ma).add(CalculationUtil.getTotalWaivedGST(ma)));
			pd.setManualAdvise(ma);
			pd.setTaxApplicable(ma.isTaxApplicable());
			pd.setTaxComponent(ma.getTaxComponent());

			detailList.add(pd);
		}

		if (aPaymentHeader.isNewRecord()) {
			for (PaymentDetail detail : detailList) {
				if (BigDecimal.ZERO.compareTo(detail.getAvailableAmount()) == -1) {
					getPaymentDetailList().add(detail);
				}
			}
		} else {
			updatePaybleAmounts(detailList, aPaymentHeader.getPaymentDetailList());
		}

		if (CollectionUtils.isEmpty(getPaymentDetailList()) && !this.enqiryModule) {
			throw new AppException("There is no available amount to proceed for Refund.");
		}

		for (PaymentDetail detail : getPaymentDetailList()) {
			if (!AdviseType.isPayable(detail.getAmountType())) {
				continue;
			}

			if (detail.isTaxApplicable()) {
				if (taxPercMap == null) {
					FinanceDetail fd = new FinanceDetail();
					fd.getFinScheduleData().setFinanceMain(financeMain);
					taxPercMap = GSTCalculator.getTaxPercentages(financeMain);
				}

				// GST Calculations
				TaxHeader taxHeader = detail.getTaxHeader();
				Taxes cgstTax = null;
				Taxes sgstTax = null;
				Taxes igstTax = null;
				Taxes ugstTax = null;
				Taxes cessTax = null;
				if (taxHeader == null) {
					taxHeader = new TaxHeader();
					taxHeader.setNewRecord(true);
					taxHeader.setRecordType(PennantConstants.RCD_ADD);
					taxHeader.setVersion(taxHeader.getVersion() + 1);
					detail.setTaxHeader(taxHeader);
				}
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {

						switch (taxes.getTaxType()) {
						case RuleConstants.CODE_CGST:
							cgstTax = taxes;
							break;
						case RuleConstants.CODE_IGST:
							igstTax = taxes;
							break;
						case RuleConstants.CODE_SGST:
							sgstTax = taxes;
							break;
						case RuleConstants.CODE_UGST:
							ugstTax = taxes;
							break;
						case RuleConstants.CODE_CESS:
							cessTax = taxes;
							break;
						default:
							break;
						}
					}
				}

				BigDecimal cGSTPerc = taxPercMap.get(RuleConstants.CODE_CGST);
				BigDecimal sGSTPerc = taxPercMap.get(RuleConstants.CODE_SGST);
				BigDecimal iGSTPerc = taxPercMap.get(RuleConstants.CODE_IGST);
				BigDecimal uGSTPerc = taxPercMap.get(RuleConstants.CODE_UGST);
				BigDecimal cessPerc = taxPercMap.get(RuleConstants.CODE_CESS);

				if (taxHeader.getTaxDetails() == null) {
					taxHeader.setTaxDetails(new ArrayList<>());
				}

				// CGST
				if (cgstTax == null) {
					cgstTax = getTaxDetail(RuleConstants.CODE_CGST, cGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(cgstTax);
				} else {
					cgstTax.setTaxPerc(cGSTPerc);
				}

				// SGST
				if (sgstTax == null) {
					sgstTax = getTaxDetail(RuleConstants.CODE_SGST, sGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(sgstTax);
				} else {
					sgstTax.setTaxPerc(sGSTPerc);
				}

				// IGST
				if (igstTax == null) {
					igstTax = getTaxDetail(RuleConstants.CODE_IGST, iGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(igstTax);
				} else {
					igstTax.setTaxPerc(iGSTPerc);
				}

				// UGST
				if (ugstTax == null) {
					ugstTax = getTaxDetail(RuleConstants.CODE_UGST, uGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(ugstTax);
				} else {
					ugstTax.setTaxPerc(uGSTPerc);
				}

				// CESS percentage
				if (cessTax == null) {
					cessTax = getTaxDetail(RuleConstants.CODE_CESS, cessPerc, taxHeader);
					taxHeader.getTaxDetails().add(cessTax);
				} else {
					cessTax.setTaxPerc(cessPerc);
				}

				TaxAmountSplit taxSplit = null;

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {
					taxSplit = GSTCalculator.getExclusiveGST(detail.getAvailableAmount(), taxPercMap);
				} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(detail.getTaxComponent())) {
					taxSplit = GSTCalculator.getInclusiveGST(detail.getAvailableAmount(), taxPercMap);
				}

				getActualGST(detail, taxSplit);

				if (taxSplit != null) {
					cgstTax.setActualTax(taxSplit.getcGST());
					sgstTax.setActualTax(taxSplit.getsGST());
					igstTax.setActualTax(taxSplit.getiGST());
					ugstTax.setActualTax(taxSplit.getuGST());
					cessTax.setActualTax(taxSplit.getCess());

					cgstTax.setNetTax(taxSplit.getcGST());
					sgstTax.setNetTax(taxSplit.getsGST());
					igstTax.setNetTax(taxSplit.getiGST());
					ugstTax.setNetTax(taxSplit.getuGST());
					cessTax.setNetTax(taxSplit.getCess());
				}

			}
		}

		doFillHeaderList(getPaymentDetailList());
		logger.debug(Literal.LEAVING);
	}

	private void getActualGST(PaymentDetail detail, TaxAmountSplit taxSplit) {
		if (taxSplit == null) {
			return;
		}

		if (detail.getAdviseAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		ManualAdvise ma = detail.getManualAdvise();

		if (ma == null) {
			return;
		}

		TaxAmountSplit adviseSplit = null;

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {
			adviseSplit = GSTCalculator.getExclusiveGST(detail.getAdviseAmount(), taxPercMap);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(detail.getTaxComponent())) {
			adviseSplit = GSTCalculator.getInclusiveGST(detail.getAdviseAmount(), taxPercMap);
		}

		BigDecimal diffGST = BigDecimal.ZERO;

		BigDecimal payableGST = taxSplit.gettGST().add(detail.getPrvGST());

		if (payableGST.compareTo(adviseSplit.gettGST()) > 0) {
			diffGST = payableGST.subtract(adviseSplit.gettGST());
			taxSplit.settGST(taxSplit.gettGST().subtract(diffGST));
		}

		if (diffGST.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		BigDecimal prvCGst = ma.getPaidCGST().add(ma.getWaivedCGST());
		BigDecimal prvSGst = ma.getPaidSGST().add(ma.getWaivedSGST());
		BigDecimal prvIGst = ma.getPaidIGST().add(ma.getWaivedIGST());
		BigDecimal prvUGst = ma.getPaidUGST().add(ma.getWaivedUGST());
		BigDecimal prvCess = ma.getPaidCESS().add(ma.getWaivedCESS());

		BigDecimal diffCGST = taxSplit.getcGST().add(prvCGst).subtract(adviseSplit.getcGST());
		BigDecimal diffSGST = taxSplit.getsGST().add(prvSGst).subtract(adviseSplit.getsGST());
		BigDecimal diffIGST = taxSplit.getiGST().add(prvIGst).subtract(adviseSplit.getiGST());
		BigDecimal diffUGST = taxSplit.getuGST().add(prvUGst).subtract(adviseSplit.getuGST());
		BigDecimal diffCESS = taxSplit.getCess().add(prvCess).subtract(adviseSplit.getCess());

		taxSplit.setcGST(taxSplit.getcGST().subtract(diffCGST));
		taxSplit.setsGST(taxSplit.getsGST().subtract(diffSGST));
		taxSplit.setiGST(taxSplit.getiGST().subtract(diffIGST));
		taxSplit.setuGST(taxSplit.getuGST().subtract(diffUGST));
		taxSplit.setCess(taxSplit.getCess().subtract(diffCESS));
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, TaxHeader taxHeader) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	private List<FinExcessAmount> processFinExcessAmount(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.ENTERING);

		List<FinExcessAmount> excessList = new ArrayList<>();
		if (!this.enqiryModule || (this.enqiryModule
				|| !DisbursementConstants.STATUS_REJECTED.equals(aPaymentHeader.getPaymentInstruction().getStatus()))) {
			excessList = this.paymentHeaderService.getfinExcessAmount(this.financeMain.getFinID());
		}

		if (!FinanceConstants.CLOSE_STATUS_CANCELLED.equalsIgnoreCase(this.financeMain.getClosingStatus())) {
			logger.debug(Literal.LEAVING);
			return excessList;
		}

		List<FinExcessAmount> filterList = new ArrayList<>();
		for (FinExcessAmount fea : excessList) {
			if (ExcessType.EXCESS.equalsIgnoreCase(fea.getAmountType())) {
				filterList.add(fea);
			}
		}

		logger.debug(Literal.LEAVING);
		return filterList;
	}

	// Update the latest balance amount..
	private void updatePaybleAmounts(List<PaymentDetail> newList, List<PaymentDetail> oldList) {
		logger.debug(Literal.ENTERING);

		List<PaymentDetail> tempList = new ArrayList<>();
		tempList.addAll(newList);

		for (PaymentDetail oldDetail : oldList) {
			for (PaymentDetail newDetail : newList) {
				if (oldDetail.getReferenceId() == newDetail.getReferenceId()) {

					BigDecimal amount = oldDetail.getAmount();
					BigDecimal[] gstAmounts = getGSTAmounts(oldDetail);

					amount = amount.subtract(gstAmounts[1]);

					String amountType = oldDetail.getAmountType();

					if (AdviseType.isPayable(amountType)) {
						oldDetail.setAvailableAmount(newDetail.getAvailableAmount());
					} else if (this.enqiryModule) {
						oldDetail.setAvailableAmount((newDetail.getAvailableAmount()));
					} else {
						oldDetail.setAvailableAmount(amount.add(newDetail.getAvailableAmount()));
					}

					oldDetail.setAdviseAmount(newDetail.getAdviseAmount());
					oldDetail.setManualAdvise(newDetail.getManualAdvise());
					oldDetail.setPrvGST(newDetail.getPrvGST());
					oldDetail.setNewRecord(false);
					oldDetail.setFeeTypeCode(newDetail.getFeeTypeCode());
					oldDetail.setFeeTypeDesc(newDetail.getFeeTypeDesc());
					oldDetail.setTaxApplicable(newDetail.isTaxApplicable());
					oldDetail.setTaxComponent(newDetail.getTaxComponent());
					oldDetail.setValueDate(newDetail.getValueDate());
					oldDetail.setReceiptID(newDetail.getReceiptID());
					getPaymentDetailList().add(oldDetail);
					tempList.remove(newDetail);
				}
			}
		}

		for (PaymentDetail newDetail : tempList) {
			if (BigDecimal.ZERO.compareTo(newDetail.getAvailableAmount()) == -1) {
				getPaymentDetailList().add(newDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onPayAmountChange(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox paymentAmt = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(paymentAmt);
		Clients.clearWrongValue(this.totAmount);
		BigDecimal amt1 = BigDecimal.ZERO;
		BigDecimal amount = PennantApplicationUtil.unFormateAmount(paymentAmt.getValue(), ccyFormatter);

		if (BigDecimal.ZERO.compareTo(amount) == 1) {
			amount = BigDecimal.ZERO;
		}

		String excessType = (String) paymentAmt.getAttribute("excessType");

		BigDecimal avaAmount = BigDecimal.ZERO;
		for (PaymentDetail pd : getPaymentDetailList()) {

			if (!excessType.equals(pd.getAmountType())) {
				continue;
			}

			avaAmount = pd.getAvailableAmount().add(avaAmount);
			if (pd.getTaxHeader() != null && FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(pd.getTaxComponent())) {
				// GST Calculations
				TaxHeader taxHeader = pd.getTaxHeader();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				BigDecimal gstAmount = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						gstAmount = gstAmount.add(taxes.getNetTax());
					}
				}
				avaAmount = avaAmount.add(gstAmount);
			}

			if (avaAmount.compareTo(amount) >= 0) {
				pd.setAmount(amount);
				avaAmount = avaAmount.subtract(amount);
				amount = BigDecimal.ZERO;
			} else if (avaAmount.compareTo(amount) == -1) {
				amt1 = amount;
				amt1 = amt1.subtract(avaAmount);
				if (amt1.compareTo(avaAmount) <= 1) {
					pd.setAmount(avaAmount);
				} else {
					pd.setAmount(BigDecimal.ZERO);
				}
				avaAmount = BigDecimal.ZERO;
				amount = amt1;
			}

		}

		if ((amount.compareTo(BigDecimal.ZERO)) < 0) {
			throw new WrongValueException(paymentAmt,
					Labels.getLabel("label_PaymentHeaderDialog_payAmountErrorMsg.value"));
		}

		if ((amount.compareTo(avaAmount)) > 0) {
			amount = avaAmount;
			paymentAmt.setValue(PennantApplicationUtil.formateAmount(avaAmount, ccyFormatter));
		}

		doFillHeaderList(getPaymentDetailList());

		logger.debug("Leaving");
	}

	private void savePaymentDetails(PaymentHeader aPaymentHeader) {
		logger.debug("Entering");

		List<PaymentDetail> list = new ArrayList<PaymentDetail>();
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		long userId = loggedInUser.getUserId();

		if (aPaymentHeader.isNewRecord()) {
			for (PaymentDetail detail : getPaymentDetailList()) {
				if (detail.getAmount() != null && (BigDecimal.ZERO.compareTo(detail.getAmount()) == 0)) {
					continue;
				}
				detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);

				detail.setLastMntBy(userId);
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setUserDetails(loggedInUser);
				detail = calTaxDetail(detail);
				list.add(detail);
			}
		} else {
			for (PaymentDetail detail : getPaymentDetailList()) {
				detail.setLastMntBy(userId);
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setUserDetails(loggedInUser);
				if (detail.isNewRecord()) {
					if (detail.getAmount() != null && (BigDecimal.ZERO.compareTo(detail.getAmount()) == 0)) {
						continue;
					}
					detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					detail.setNewRecord(true);
				} else {
					if (detail.getAmount() != null && (BigDecimal.ZERO.compareTo(detail.getAmount()) == 0)) {
						detail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						detail.setNewRecord(false);
					} else {
						if (!enqiryModule) {
							detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
							detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							detail.setNewRecord(false);
						}
					}
				}
				detail = calTaxDetail(detail);
				list.add(detail);
			}
		}
		aPaymentHeader.setPaymentDetailList(list);
		logger.debug("Leaving");
	}

	/**
	 * Method for Reset or calculate GST amounts based on amounts adjusted
	 * 
	 * @param detail
	 * @return
	 */
	private PaymentDetail calTaxDetail(PaymentDetail detail) {

		if (!AdviseType.isPayable(detail.getAmountType()) || detail.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			detail.setTaxHeader(null);
			return detail;
		}

		TaxHeader taxHeader = detail.getTaxHeader();
		if (detail.isTaxApplicable() && taxHeader != null) {

			if (taxPercMap == null) {
				taxPercMap = GSTCalculator.getTaxPercentages(financeMain);
			}

			// GST Calculations
			Taxes cgstTax = null;
			Taxes sgstTax = null;
			Taxes igstTax = null;
			Taxes ugstTax = null;
			Taxes cessTax = null;
			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						cgstTax = taxes;
						break;
					case RuleConstants.CODE_SGST:
						sgstTax = taxes;
						break;
					case RuleConstants.CODE_IGST:
						igstTax = taxes;
						break;
					case RuleConstants.CODE_UGST:
						ugstTax = taxes;
						break;
					case RuleConstants.CODE_CESS:
						cessTax = taxes;
						break;
					default:
						break;
					}
				}
			}

			TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(detail.getAmount(), taxPercMap);
			getActualGST(detail, taxSplit);
			cgstTax.setPaidTax(taxSplit.getcGST());
			sgstTax.setPaidTax(taxSplit.getsGST());
			igstTax.setPaidTax(taxSplit.getiGST());
			ugstTax.setPaidTax(taxSplit.getuGST());
			cessTax.setPaidTax(taxSplit.getCess());

		}

		return detail;
	}

	private BigDecimal[] getGSTAmounts(PaymentDetail pd) {
		TaxHeader taxHeader = pd.getTaxHeader();

		BigDecimal[] gstAmounts = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };

		if (taxHeader == null) {
			return gstAmounts;
		}

		List<Taxes> taxDetails = taxHeader.getTaxDetails();

		if (CollectionUtils.isEmpty(taxDetails)) {
			return gstAmounts;
		}

		BigDecimal dueGST = BigDecimal.ZERO;
		BigDecimal dueGSTExclusive = BigDecimal.ZERO;

		for (Taxes taxes : taxDetails) {
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(pd.getTaxComponent())) {
				dueGSTExclusive = dueGSTExclusive.add(taxes.getActualTax());
			} else {
				dueGST = dueGST.add(taxes.getActualTax());
			}
		}

		gstAmounts[0] = dueGST;
		gstAmounts[1] = dueGSTExclusive;

		return gstAmounts;
	}

	public void doFillHeaderList(List<PaymentDetail> pdList) {
		this.listBoxPaymentTypeInstructions.getItems().clear();

		this.listheader_PaymentHeaderDialog_button.setVisible(true);

		if (CollectionUtils.isEmpty(pdList)) {
			return;
		}

		Map<String, List<PaymentDetail>> map = new HashMap<>();

		for (PaymentDetail pd : pdList) {
			String excessType = pd.getAmountType();
			if (AdviseType.isPayable(pd.getAmountType())) {
				excessType = String.valueOf(AdviseType.PAYABLE.id());
			}

			List<PaymentDetail> list = map.get(excessType);

			if (list == null) {
				list = new ArrayList<>();
				map.put(excessType, list);
			}

			list.add(pd);
		}

		BigDecimal totalPayAmt = BigDecimal.ZERO;

		for (Entry<String, List<PaymentDetail>> paymentDetail : map.entrySet()) {
			totalPayAmt = totalPayAmt.add(doFillHeaderList(paymentDetail.getKey(), paymentDetail.getValue()));
		}

		// Total Amount
		Listitem item = new Listitem();
		Listcell lc = new Listcell();
		lc.setSpan(6);
		item.appendChild(lc);

		lc = new Listcell("Total Pay Amount");
		lc.setStyle("font-weight:bold;text-align:right;");
		item.appendChild(lc);

		lc = new Listcell();
		totAmount = new Decimalbox();
		totAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		totAmount.setStyle("text-align:right;");
		totAmount.setReadonly(true);

		if (disbursementInstructionsDialogCtrl != null) {
			if (totalPayAmt.compareTo(FinanceConstants.LEI_NUM_LIMIT) > 0) {
				disbursementInstructionsDialogCtrl.leiNum.setSclass("mandatory");
			} else {
				disbursementInstructionsDialogCtrl.leiNum.setSclass("");
				disbursementInstructionsDialogCtrl.leiNumber.setErrorMessage("");
				Clients.clearWrongValue(disbursementInstructionsDialogCtrl.leiNumber);
			}
		}

		totAmount.setValue(PennantApplicationUtil.formateAmount(totalPayAmt, ccyFormatter));

		lc.appendChild(totAmount);
		lc.setParent(item);

		if (disbursementInstructionsDialogCtrl != null) {
			totalPayAmt = PennantApplicationUtil.formateAmount(totalPayAmt, ccyFormatter);
			disbursementInstructionsDialogCtrl.paymentAmount.setValue(totalPayAmt);
		}

		this.listBoxPaymentTypeInstructions.appendChild(item);
	}

	public BigDecimal doFillHeaderList(String excessType, List<PaymentDetail> pdList) {
		logger.debug("Entering");

		BigDecimal totalPayAmt = BigDecimal.ZERO;

		boolean isReadOnly = isReadOnly("PaymentHeaderDialog_paymentAmount");
		Listitem item = null;
		BigDecimal avaAmount = BigDecimal.ZERO;
		BigDecimal dueGST = BigDecimal.ZERO;
		BigDecimal dueGSTExclusive = BigDecimal.ZERO;
		BigDecimal payAmount = BigDecimal.ZERO;

		PaymentDetail temp = null;
		for (PaymentDetail pd : pdList) {

			if (temp == null) {
				temp = pd;
			}

			avaAmount = pd.getAvailableAmount().add(avaAmount);
			payAmount = pd.getAmount().add(payAmount);

			totalPayAmt = payAmount;

			BigDecimal[] gstAmounts = getGSTAmounts(pd);
			if (StringUtils.equals(pd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
				avaAmount = avaAmount.subtract(gstAmounts[0]);
			}

			dueGST = dueGST.add(gstAmounts[0]);
			dueGSTExclusive = dueGSTExclusive.add(gstAmounts[1]);

		}

		Button button = new Button();
		item = new Listitem();
		item.setAttribute("index", "Y");
		Listcell lc;

		if (temp.isExpand()) {
			lc = new Listcell();
			button.setImage("/images/icons/delete.png");
			button.setStyle("background:white;border:0px;");
			button.setAttribute("pd", temp);
			button.addForward("onClick", self, "onExpand");

			lc.appendChild(button);
			lc.setParent(item);
		} else {
			lc = new Listcell();
			button.setImage("/images/icons/add.png");
			button.setStyle("background:#FFFFFF;border:0px;onMouseOver ");
			button.setAttribute("pd", temp);
			button.addForward("onClick", self, "onCollapse");

			lc.appendChild(button);
			lc.setParent(item);
		}

		String amountType = null;

		if (String.valueOf(AdviseType.PAYABLE.id()).equals(excessType)) {
			amountType = Labels.getLabel("label_PaymentHeaderDialog_ManualAdvisePayable.value");
		} else {
			amountType = Labels.getLabel("label_Excess_Type_" + excessType);
		}

		lc = new Listcell(amountType);
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		// Available Amount
		lc = new Listcell();
		lc.appendChild(getDecimalbox(avaAmount, true));
		lc.setParent(item);

		// GST Amount
		BigDecimal totalGST = dueGST.add(dueGSTExclusive);
		lc = new Listcell();
		lc.appendChild(getDecimalbox(totalGST, true));
		lc.setParent(item);

		// Total Available
		BigDecimal totalAvailable = avaAmount.add(totalGST);
		lc = new Listcell();
		lc.appendChild(getDecimalbox(totalAvailable, true));
		lc.setParent(item);

		// Pay Amount
		Decimalbox paymentAmount = getDecimalbox(payAmount, isReadOnly);
		paymentAmount.setAttribute("excessType", excessType);
		paymentAmount.addForward("onChange", self, "onPayAmountChange");

		lc = new Listcell();
		lc.appendChild(paymentAmount);
		lc.setParent(item);

		// Balance Amount
		BigDecimal balanceAmount = totalAvailable.subtract(payAmount);
		lc = new Listcell();
		lc.appendChild(getDecimalbox(balanceAmount, true));
		lc.setParent(item);

		this.listBoxPaymentTypeInstructions.appendChild(item);

		if (temp.isExpand() && !temp.isCollapse()) {
			doFillChildDetail(pdList);
		}

		logger.debug("Leaving");

		return totalPayAmt;
	}

	private Decimalbox getDecimalbox(BigDecimal amount, boolean isReadOnly) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		decimalbox.setStyle("text-align:right; ");
		decimalbox.setReadonly(isReadOnly);
		decimalbox.setValue(PennantApplicationUtil.formateAmount(amount, ccyFormatter));

		return decimalbox;
	}

	private void doFillChildDetail(List<PaymentDetail> paymentDetail) {
		logger.debug("Entering");

		boolean isReadOnly = isReadOnly("PaymentHeaderDialog_paymentAmount");

		BigDecimal totalPayAmt = BigDecimal.ZERO;
		Listitem item = null;

		for (PaymentDetail pd : paymentDetail) {
			item = new Listitem();
			Listcell lc;
			lc = new Listcell();
			lc.setParent(item);

			BigDecimal calGST = BigDecimal.ZERO;
			BigDecimal availAmount = pd.getAvailableAmount();
			BigDecimal paidAmount = pd.getAmount();
			String desc = "";

			if (!ExcessType.EXCESS.equals(pd.getAmountType())) {
				desc = pd.getFeeTypeCode().concat(("-")).concat(pd.getFeeTypeDesc());
				TaxHeader taxHeader = pd.getTaxHeader();
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							calGST = calGST.add(taxes.getActualTax());
						}

						if (StringUtils.equals(pd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
							availAmount = availAmount.subtract(calGST);
							desc = desc.concat(" (Inclusive)");
						} else if (StringUtils.equals(pd.getTaxComponent(),
								FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
							desc = desc.concat(" (Exclusive)");
						}
					}
				}
			}

			lc = new Listcell();
			Hbox hbox = new Hbox();
			Space space = new Space();
			space.setSpacing("20px");
			space.setParent(hbox);
			Label label = new Label();
			label.setValue(desc);
			label.setParent(hbox);
			lc.appendChild(hbox);
			lc.setParent(item);

			String receiptID = "";
			String valueDate = DateUtil.formatToLongDate(pd.getValueDate());

			if (!AdviseType.isPayable(pd.getAmountType())) {
				receiptID = pd.getReceiptID() == null ? "" : String.valueOf(pd.getReceiptID());
			}

			item.appendChild(new Listcell(receiptID));
			item.appendChild(new Listcell(valueDate));

			// Available Amount
			lc = new Listcell();
			lc.appendChild(getDecimalbox(availAmount, true));
			lc.setParent(item);

			// GST Amount
			lc = new Listcell();
			lc.appendChild(getDecimalbox(calGST, true));
			lc.setParent(item);

			// Total Available
			lc = new Listcell();
			lc.appendChild(getDecimalbox(availAmount.add(calGST), true));
			lc.setParent(item);

			// Pay Amount
			Decimalbox paymentAmount = getDecimalbox(paidAmount, isReadOnly);
			paymentAmount.addForward("onChange", self, "onPayAmountForEventChanges");
			paymentAmount.setAttribute("object", pd);
			paymentAmount.setConstraint("NO NEGATIVE");

			lc = new Listcell();
			lc.appendChild(paymentAmount);
			lc.setParent(item);

			// Balance Amount
			BigDecimal balanceAmount = availAmount.add(calGST).subtract(paidAmount);
			lc = new Listcell();
			lc.appendChild(getDecimalbox(balanceAmount, true));
			lc.setParent(item);

			this.listBoxPaymentTypeInstructions.appendChild(item);
		}

		if (getDisbursementInstructionsDialogCtrl() != null) {
			getDisbursementInstructionsDialogCtrl().paymentAmount.setValue(totalPayAmt);
		}

		logger.debug("Leaving");
	}

	public void onPayAmountForEventChanges(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox paymentAmount = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(paymentAmount);
		Clients.clearWrongValue(this.totAmount);

		BigDecimal amount = PennantApplicationUtil.unFormateAmount(paymentAmount.getValue(), ccyFormatter);

		if (BigDecimal.ZERO.compareTo(amount) == 1) {
			amount = BigDecimal.ZERO;
		}

		PaymentDetail paymentDetail = (PaymentDetail) paymentAmount.getAttribute("object");

		BigDecimal avaAmount = BigDecimal.ZERO;
		for (PaymentDetail detail : getPaymentDetailList()) {
			if (paymentDetail.getReferenceId() == detail.getReferenceId()) {
				avaAmount = detail.getAvailableAmount();

				// GST Calculations
				TaxHeader taxHeader = detail.getTaxHeader();
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (taxHeader != null
							&& StringUtils.equals(detail.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)
							&& CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							avaAmount = avaAmount.add(taxes.getActualTax());
						}
					}
				}

				if ((amount.compareTo(avaAmount)) > 0) {
					amount = avaAmount;
					paymentAmount.setValue(PennantApplicationUtil.formateAmount(avaAmount, ccyFormatter));
				}
				detail.setAmount(amount);
			}
		}

		doFillHeaderList(getPaymentDetailList());
		logger.debug("Leaving");
	}

	public void onExpand(ForwardEvent event) {
		Button button = (Button) event.getOrigin().getTarget();
		PaymentDetail pd = (PaymentDetail) button.getAttribute("pd");

		pd.setExpand(false);
		pd.setCollapse(true);

		doFillHeaderList(paymentDetailList);
	}

	public void onCollapse(ForwardEvent event) {
		Button button = (Button) event.getOrigin().getTarget();
		PaymentDetail pd = (PaymentDetail) button.getAttribute("pd");

		pd.setExpand(true);
		pd.setCollapse(false);

		doFillHeaderList(paymentDetailList);
	}

	// Setters and getters
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public List<PaymentDetail> getPaymentDetailList() {
		return paymentDetailList;
	}

	public void setPaymentDetailList(List<PaymentDetail> paymentDetailList) {
		this.paymentDetailList = paymentDetailList;
	}

	public PaymentInstructionDialogCtrl getDisbursementInstructionsDialogCtrl() {
		return disbursementInstructionsDialogCtrl;
	}

	public void setDisbursementInstructionsDialogCtrl(PaymentInstructionDialogCtrl disbursementInstructionsDialogCtrl) {
		this.disbursementInstructionsDialogCtrl = disbursementInstructionsDialogCtrl;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public Map<String, BigDecimal> getTaxPercMap() {
		return taxPercMap;
	}

	public void setTaxPercMap(Map<String, BigDecimal> taxPercMap) {
		this.taxPercMap = taxPercMap;
	}

	public PaymentTransaction getPaymentTransaction() {
		return paymentTransaction;
	}

	public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

	public void setCrossLoanKnockOffDAO(CrossLoanKnockOffDAO crossLoanKnockOffDAO) {
		this.crossLoanKnockOffDAO = crossLoanKnockOffDAO;
	}

}
