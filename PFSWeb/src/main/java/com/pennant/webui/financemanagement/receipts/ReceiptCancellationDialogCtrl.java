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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : ReceiptCancellationDialogCtrl.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 03-06-2011
 * 
 * Modified Date : 03-06-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.pff.fee.AdviseType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptCancellationDialog.zul
 */
public class ReceiptCancellationDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(ReceiptCancellationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReceiptCancellationDialog;
	protected Borderlayout borderlayout_Realization;
	protected Label windowTitle;

	// Receipt Details
	protected Textbox finType;
	protected Textbox finReference;
	protected Textbox finCcy;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Datebox nextInstResetDate;
	protected Label label_ReceiptCancellationDialog_NextInstResetDate;
	protected Hbox hbox_ReceiptCancellationDialog_NextInstResetDate;

	protected Combobox receiptPurpose;
	protected Combobox excessAdjustTo;
	protected Combobox receiptMode;
	protected CurrencyBox receiptAmount;
	protected Combobox allocationMethod;
	protected Combobox effScheduleMethod;
	protected ExtendedCombobox bounceCode;
	protected CurrencyBox bounceCharge;
	protected Textbox bounceRemarks;
	protected ExtendedCombobox cancelReason;
	protected Textbox extReference;

	protected Groupbox gb_ReceiptDetails;
	protected Caption caption_receiptDetail;
	protected Label label_ReceiptCancellationDialog_favourNo;
	protected Label label_ReceiptCancellationDialog_ChequeAccountNo;

	protected Uppercasebox favourNo;
	protected Datebox valueDate;
	protected ExtendedCombobox bankCode;
	protected ExtendedCombobox bankBranch;
	protected Textbox favourName;
	protected Datebox depositDate;
	protected Uppercasebox depositNo;
	protected Uppercasebox paymentRef;
	protected Uppercasebox transactionRef;
	protected AccountSelectionBox chequeAcNo;
	protected ExtendedCombobox fundingAccount;
	protected Label label_ReceiptCancellationDialog_FundingAccount;
	protected Datebox receivedDate;
	protected Textbox remarks;
	protected Label label_ReceiptCancellationDialog_BounceDate;
	protected Hbox hbox_ReceiptCancellationDialog_BounceDate;
	protected Datebox bounceDate;

	protected Row row_BounceReason;
	protected Row row_CancelReason;
	protected Row row_BounceRemarks;

	protected Row row_favourNo;
	protected Row row_BankCode;
	protected Row row_DepositDate;
	protected Row row_PaymentRef;
	protected Row row_ChequeAcNo;
	protected Row row_fundingAcNo;
	protected Row row_remarks;

	// Payment Schedule Details
	protected Textbox payment_finType;
	protected Textbox payment_finReference;
	protected Textbox payment_finCcy;
	protected Textbox payment_CustCIF;
	protected Textbox payment_finBranch;

	// List Header Details on payent Details
	protected Listheader listheader_Tds;
	protected Listheader listheader_LatePft;
	protected Listheader listheader_Refund;
	protected Listheader listheader_Penalty;
	protected Listheader listheader_SchdFee;

	protected Listbox listBoxReceipts;
	protected Listbox listBoxPayment;
	protected Listbox listBoxPosting;
	protected Tab receiptDetailsTab;
	protected Tab repaymentDetailsTab;
	protected Tab postingDetailsTab;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	// Postings Details
	protected Textbox posting_finType;
	protected Textbox posting_finReference;
	protected Textbox posting_finCcy;
	protected Textbox posting_CustCIF;
	protected Textbox posting_finBranch;

	protected Groupbox gb_FeeDetail;
	// TODO: labels are same
	protected Listbox listBoxFeeDetail;

	private FinReceiptHeader receiptHeader = null;
	private ReceiptCancellationListCtrl receiptCancellationListCtrl;
	private ReceiptCancellationService receiptCancellationService;
	private RuleService ruleService;
	private String module;

	protected Textbox receiptId;
	protected Textbox promotionCode;
	protected Textbox payment_receiptId;
	protected Textbox payment_promotionCode;
	protected Textbox posting_receiptId;
	protected Textbox posting_promotionCode;

	protected Groupbox groupbox_Finance;
	protected Groupbox groupbox_Customer;
	protected Groupbox groupbox_Other;
	protected ExtendedCombobox custID;
	protected Textbox reference;
	protected ExtendedCombobox postBranch;
	protected ExtendedCombobox cashierBranch;
	protected ExtendedCombobox finDivision;
	protected String selectMethodName = "onSelectTab";
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;

	protected ExtendedFieldCtrl extendedFieldCtrl = null;
	private String moduleDefiner = null;

	private FinReceiptData receiptData = null;

	/**
	 * default constructor.<br>
	 */
	public ReceiptCancellationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			super.pageRightName = "ReceiptBounceDialog";
		} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			super.pageRightName = "ReceiptCancellationDialog";
		} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			super.pageRightName = "ReceiptCancellationDialog";
		}
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ReceiptCancellationDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReceiptCancellationDialog);

		try {
			if (arguments.containsKey("receiptHeader")) {

				setReceiptHeader((FinReceiptHeader) arguments.get("receiptHeader"));
				FinReceiptHeader befImage = new FinReceiptHeader();

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(getReceiptHeader());
				getReceiptHeader().setBefImage(befImage);

				if (getReceiptHeader().getManualAdvise() != null) {
					ManualAdvise adviseBefImage = cloner.deepClone(getReceiptHeader().getManualAdvise());
					getReceiptHeader().getManualAdvise().setBefImage(adviseBefImage);
				}
			}

			if (arguments.containsKey("receiptData")) {
				setReceiptData((FinReceiptData) arguments.get("receiptData"));
			}

			this.module = (String) arguments.get("module");
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
				super.pageRightName = "ReceiptBounceDialog";
				this.windowTitle.setValue(Labels.getLabel("window_ReceiptBounceDialog.title"));
			} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
				super.pageRightName = "ReceiptCancellationDialog";
				this.windowTitle.setValue(Labels.getLabel("window_ReceiptCancellationDialog.title"));
			} else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
				super.pageRightName = "ReceiptCancellationDialog";
				this.windowTitle.setValue(Labels.getLabel("window_FeeReceiptCancellationDialog.title"));
				this.repaymentDetailsTab.setVisible(false);
			}

			if (arguments.containsKey("moduleCode")) {
				moduleCode = (String) arguments.get("moduleCode");
			}

			this.receiptCancellationListCtrl = (ReceiptCancellationListCtrl) arguments
					.get("receiptCancellationListCtrl");
			doLoadWorkFlow(receiptHeader.isWorkflow(), receiptHeader.getWorkflowId(), receiptHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(receiptHeader.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
				}
			} else {
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			checkAndSetModDef(moduleCode);

			doReadonly();
			if (StringUtils.isNotBlank(receiptHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}
			if (StringUtils.equals(FinanceConstants.PRODUCT_GOLD, receiptHeader.getProductCategory())) {
				this.label_ReceiptCancellationDialog_NextInstResetDate.setVisible(true);
				this.hbox_ReceiptCancellationDialog_NextInstResetDate.setVisible(true);
			}

			// Reset Finance Repay Header Details
			doWriteBeanToComponents();
			this.borderlayout_Realization.setHeight(getBorderLayoutHeight());
			this.listBoxPayment.setHeight(getListBoxHeight(6));
			this.listBoxPosting.setHeight(getListBoxHeight(6));
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReceiptCancellationDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void checkAndSetModDef(String moduleCode) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(moduleCode)) {
			if ("ReceiptCancellation".equals(moduleCode)) {
				moduleDefiner = FinServiceEvent.UPFRONT_FEE_CAN;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_" + this.pageRightName + "_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_" + this.pageRightName + "_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_" + this.pageRightName + "_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_" + this.pageRightName + "_btnSave"));
		this.btnCancel.setVisible(false);

		// Bounce Reason Fields
		readOnlyComponent(isReadOnly(this.pageRightName + "_bounceCode"), this.bounceCode);
		// readOnlyComponent(isReadOnly("+this.pageRightName+"_bounceCharge"), this.bounceCharge);
		readOnlyComponent(true, this.bounceCharge);
		readOnlyComponent(isReadOnly(this.pageRightName + "_bounceRemarks"), this.bounceRemarks);
		readOnlyComponent(isReadOnly(this.pageRightName + "_bounceDate"), this.bounceDate);
		readOnlyComponent(isReadOnly(this.pageRightName + "_cancelReason"), this.cancelReason);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		int formatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());

		// Receipts Details
		this.receiptAmount.setProperties(true, formatter);

		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setMandatoryStyle(true);
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(
				new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });

		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setMandatoryStyle(true);
		this.fundingAccount.setValueColumn("PartnerBankID");
		this.fundingAccount.setDescColumn("PartnerBankCode");
		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankID" });

		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);

		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(100);
		this.bounceDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourName.setMaxlength(50);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(6);
		this.depositDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.depositNo.setMaxlength(50);
		this.paymentRef.setMaxlength(50);
		this.transactionRef.setMaxlength(50);

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });

		this.bankBranch.setModuleName("BankBranch");
		this.bankBranch.setValueColumn("IFSC");
		this.bankBranch.setDescColumn("BranchDesc");
		this.bankBranch.setDisplayStyle(2);
		this.bankBranch.setValidateColumns(new String[] { "IFSC" });

		this.bounceCode.setModuleName("BounceReason");
		this.bounceCode.setMandatoryStyle(true);
		this.bounceCode.setValueColumn("BounceID");
		this.bounceCode.setValueType(DataType.LONG);
		this.bounceCode.setDescColumn("Reason");
		this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceID", "BounceCode", "Lovdesccategory", "Reason" });

		this.bounceCharge.setProperties(false, formatter);
		this.bounceRemarks.setMaxlength(100);

		this.nextInstResetDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(true);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		if (RepayConstants.RECEIPTTO_FINANCE.equals(this.receiptHeader.getRecAgainst())) {
			this.fundingAccount.setModuleName("FinTypePartner");
			this.fundingAccount.setMandatoryStyle(true);
			this.fundingAccount.setValueColumn("PartnerBankID");
			this.fundingAccount.setDescColumn("PartnerBankCode");
			this.fundingAccount.setValueType(DataType.LONG);
			this.fundingAccount.setDisplayStyle(2);
			this.fundingAccount.setValidateColumns(new String[] { "PartnerBankID" });
			this.groupbox_Finance.setVisible(true);
		} else {
			this.fundingAccount.setModuleName("PartnerBank");
			this.fundingAccount.setValueColumn("PartnerBankId");
			this.fundingAccount.setDescColumn("PartnerBankCode");
			this.fundingAccount.setValidateColumns(
					new String[] { "PartnerBankId", "PartnerBankCode", "PartnerBankName", "BankCode" });
			this.fundingAccount.setMandatoryStyle(true);

			if (RepayConstants.RECEIPTTO_CUSTOMER.equals(this.receiptHeader.getRecAgainst())) {
				this.groupbox_Customer.setVisible(true);
			} else if (RepayConstants.RECEIPTTO_OTHER.equals(this.receiptHeader.getRecAgainst())) {
				this.groupbox_Other.setVisible(true);
				this.reference.setMaxlength(20);
			}
		}

		// Post Branch
		this.postBranch.setModuleName("Branch");
		this.postBranch.setValueColumn("BranchCode");
		this.postBranch.setDescColumn("BranchDesc");
		this.postBranch.setValidateColumns(new String[] { "BranchCode" });

		// Cashier Branch
		this.cashierBranch.setModuleName("Branch");
		this.cashierBranch.setValueColumn("BranchCode");
		this.cashierBranch.setDescColumn("BranchDesc");
		this.cashierBranch.setValidateColumns(new String[] { "BranchCode" });

		// Fin Division
		this.finDivision.setModuleName("DivisionDetail");
		this.finDivision.setValueColumn("DivisionCode");
		this.finDivision.setDescColumn("DivisionCodeDesc");
		this.finDivision.setValidateColumns(new String[] { "DivisionCode" });

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadonly() {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(true, this.receiptPurpose);
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.receiptMode);
		readOnlyComponent(true, this.receiptAmount);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);

		// Receipt Details
		readOnlyComponent(true, this.favourNo);
		readOnlyComponent(true, this.valueDate);
		readOnlyComponent(true, this.bankCode);
		readOnlyComponent(true, this.bankBranch);
		readOnlyComponent(true, this.favourName);
		readOnlyComponent(true, this.depositDate);
		readOnlyComponent(true, this.depositNo);
		readOnlyComponent(true, this.chequeAcNo);
		readOnlyComponent(true, this.fundingAccount);
		readOnlyComponent(true, this.paymentRef);
		readOnlyComponent(true, this.transactionRef);
		readOnlyComponent(true, this.receivedDate);
		readOnlyComponent(true, this.remarks);

		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.reference);
		readOnlyComponent(true, this.cashierBranch);
		readOnlyComponent(true, this.postBranch);
		readOnlyComponent(true, this.finDivision);

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
		if (extendedFieldCtrl != null && receiptHeader.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		this.receiptCancellationListCtrl.search();
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select)
				|| StringUtils.equals(recMode, ReceiptMode.EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else {

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.receiptAmount.setMandatory(false);
			this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);

			if (StringUtils.equals(recMode, ReceiptMode.CHEQUE) || StringUtils.equals(recMode, ReceiptMode.DD)) {

				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);

				if (StringUtils.equals(recMode, ReceiptMode.CHEQUE)) {
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptCancellationDialog_ChequeAccountNo.setVisible(true);
					this.chequeAcNo.setVisible(true);
					this.label_ReceiptCancellationDialog_favourNo
							.setValue(Labels.getLabel("label_ReceiptCancellationDialog_ChequeFavourNo.value"));
				} else {
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptCancellationDialog_ChequeAccountNo.setVisible(false);
					this.chequeAcNo.setVisible(false);
					this.label_ReceiptCancellationDialog_favourNo
							.setValue(Labels.getLabel("label_ReceiptCancellationDialog_DDFavourNo.value"));
				}

			} else if (StringUtils.equals(recMode, ReceiptMode.CASH)) {

				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);

			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_fundingAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() {
		logger.debug("Entering");

		// Duplicate Creation of Object
		Cloner cloner = new Cloner();
		FinReceiptHeader aReceiptHeader = cloner.deepClone(getReceiptHeader());

		ArrayList<WrongValueException> wve = new ArrayList<>();
		boolean recReject = false;
		if (this.userAction.getSelectedItem() != null
				&& ("Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
			recReject = true;
		}

		if (!recReject) {
			doSetValidation();
		}

		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)
				|| (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)
						&& ReceiptMode.CHEQUE.equalsIgnoreCase(this.receiptHeader.getReceiptMode()))) {
			aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_BOUNCE);
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
				aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_CANCEL);
			}
			try {
				aReceiptHeader.setBounceDate(this.bounceDate.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}

			// Bounce Details capturing
			ManualAdvise bounce = aReceiptHeader.getManualAdvise();
			if (bounce == null) {
				bounce = new ManualAdvise();
				bounce.setNewRecord(true);
			}

			bounce.setAdviseType(AdviseType.RECEIVABLE.id());
			Long finID = aReceiptHeader.getFinID();
			bounce.setFinID(finID == null ? 0 : finID);
			bounce.setFinReference(aReceiptHeader.getReference());
			bounce.setFeeTypeID(0);
			bounce.setSequence(0);
			try {
				bounce.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.bounceCharge.getActualValue(),
						CurrencyUtil.getFormat(aReceiptHeader.getFinCcy())));
			} catch (WrongValueException e) {
				wve.add(e);
			}

			bounce.setPaidAmount(BigDecimal.ZERO);
			bounce.setWaivedAmount(BigDecimal.ZERO);
			bounce.setValueDate(SysParamUtil.getAppDate());
			bounce.setPostDate(SysParamUtil.getPostDate());

			try {
				bounce.setRemarks(this.bounceRemarks.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
			bounce.setReceiptID(aReceiptHeader.getReceiptID());
			try {
				bounce.setBounceID(Long.valueOf(this.bounceCode.getValue()));
			} catch (WrongValueException e) {
				wve.add(e);
			}

			doRemoveValidation();

			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				this.receiptDetailsTab.setSelected(true);
				throw new WrongValuesException(wvea);
			}

			aReceiptHeader.setManualAdvise(bounce);
		} else {
			aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_CANCEL);

			try {
				aReceiptHeader.setCancelReason(this.cancelReason.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
			// Document Details Saving
			if (documentDetailDialogCtrl != null) {
				aReceiptHeader.setDocumentDetails(documentDetailDialogCtrl.getDocumentDetailsList());
			} else {
				aReceiptHeader.setDocumentDetails(aReceiptHeader.getDocumentDetails());
			}
			doRemoveValidation();

			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				this.receiptDetailsTab.setSelected(true);
				throw new WrongValuesException(wvea);
			}

			aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_CANCEL);
		}

		// Extended Fields
		if (aReceiptHeader.getExtendedFieldHeader() != null) {
			aReceiptHeader.setExtendedFieldRender(extendedFieldCtrl.save(true));

			FinReceiptHeader rh = getReceiptHeader();

			if (aReceiptHeader.getExtendedFieldRender() != null) {
				ExtendedFieldExtension efe = new ExtendedFieldExtension();
				if (extendedFieldCtrl.getExtendedFieldExtension() != null) {
					BeanUtils.copyProperties(extendedFieldCtrl.getExtendedFieldExtension(), efe);
				}

				efe.setExtenrnalRef(Long.toString(rh.getReceiptID()));
				efe.setPurpose(rh.getReceiptPurpose());
				efe.setModeStatus(rh.getReceiptModeStatus());
				efe.setSequence(aReceiptHeader.getExtendedFieldRender().getSeqNo());
				efe.setEvent(PennantStaticListUtil.getFinEventCode(aReceiptHeader.getExtendedFieldHeader().getEvent()));

				aReceiptHeader.setExtendedFieldExtension(efe);
			}
		}

		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReceiptHeader.getRecordType())) {
				aReceiptHeader.setVersion(aReceiptHeader.getVersion() + 1);
				aReceiptHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aReceiptHeader.setNewRecord(true);
			}
		} else {
			aReceiptHeader.setVersion(aReceiptHeader.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		try {
			if (doProcess(aReceiptHeader, tranType)) {

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aReceiptHeader.getNextTaskId())) {
					aReceiptHeader.setNextRoleCode("");
				}

				/*
				 * boolean isCancel = false; if (!StringUtils.equals(PennantConstants.RCD_STATUS_SAVED,
				 * aReceiptHeader.getRecordStatus()) && StringUtils.equals(this.module,
				 * RepayConstants.MODULETYPE_CANCEL)) { isCancel = true; }
				 */

				String ref = null;
				if (FinServiceEvent.FEEPAYMENT.equals(receiptHeader.getReceiptPurpose())) {
					ref = aReceiptHeader.getExtReference();
				} else if (this.groupbox_Customer.isVisible()) {
					ref = this.custID.getValue();
				} else if (this.groupbox_Other.isVisible()) {
					ref = this.reference.getValue();
				} else if (this.groupbox_Finance.isVisible()) {
					ref = this.finReference.getValue();
				}

				String msg = PennantApplicationUtil.getSavingStatus(aReceiptHeader.getRoleCode(),
						aReceiptHeader.getNextRoleCode(), ref, " Finance ", aReceiptHeader.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				if (extendedFieldCtrl != null && aReceiptHeader.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onFulfill$bounceCode(Event event) {
		logger.debug("Entering" + event.toString());

		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)
				|| ReceiptMode.CHEQUE.equalsIgnoreCase(this.receiptHeader.getReceiptMode())) {
			this.bounceCharge.setValue(BigDecimal.ZERO);
			return;
		}

		Object dataObject = bounceCode.getObject();

		if (dataObject instanceof String) {
			this.bounceCode.setValue(dataObject.toString());
		} else {
			BounceReason bounceReason = (BounceReason) dataObject;

			if (bounceReason == null) {
				return;
			}

			Map<String, Object> executeMap = bounceReason.getDeclaredFieldValues();

			if (this.receiptHeader != null) {
				List<FinReceiptDetail> receiptDetails = this.receiptHeader.getReceiptDetails();
				if (receiptDetails != null && !receiptDetails.isEmpty()) {
					for (FinReceiptDetail finReceiptDetail : receiptDetails) {
						if (StringUtils.equals(this.receiptHeader.getReceiptMode(),
								finReceiptDetail.getPaymentType())) {
							finReceiptDetail.getDeclaredFieldValues(executeMap);
							break;
						}
					}
				}
			}

			Map<String, Object> eventMapping = null;
			Rule rule = getRuleService().getRuleById(bounceReason.getRuleID(), "");
			BigDecimal bounceAmt = BigDecimal.ZERO;
			int formatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());
			eventMapping = receiptCancellationService.getGLSubHeadCodes(receiptHeader.getFinID());
			if (rule != null) {

				if (eventMapping != null) {
					executeMap.put("emptype", eventMapping.get("EMPTYPE"));
					executeMap.put("branchcity", eventMapping.get("BRANCHCITY"));
					executeMap.put("fincollateralreq", eventMapping.get("FINCOLLATERALREQ"));
					executeMap.put("btloan", eventMapping.get("BTLOAN"));
					executeMap.put("ae_businessvertical", eventMapping.get("BUSINESSVERTICAL"));
					BigDecimal alwFlexi = (BigDecimal) eventMapping.get("ALWFLEXI");
					executeMap.put("ae_alwflexi", alwFlexi.compareTo(BigDecimal.ZERO) == 0 ? false : true);
					executeMap.put("ae_finbranch", eventMapping.get("FINBRANCH"));
					executeMap.put("ae_entitycode", eventMapping.get("ENTITYCODE"));
				}
				executeMap.put("br_finType", getReceiptHeader().getFinType());
				bounceAmt = (BigDecimal) RuleExecutionUtil.executeRule(rule.getSQLRule(), executeMap,
						getReceiptHeader().getFinCcy(), RuleReturnType.DECIMAL);
				// unFormating BounceAmt
				bounceAmt = PennantApplicationUtil.unFormateAmount(bounceAmt, formatter);
			}
			this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(bounceAmt, formatter));
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.bounceCode.isReadonly()) {
			this.bounceCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReceiptCancellationDialog_BounceReason.value"), null, true, true));
		}

		if (!this.cancelReason.isReadonly()) {
			this.cancelReason.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReceiptCancellationDialog_CancelReason.value"), null, true, true));
		}

		if (!this.bounceRemarks.isReadonly()) {
			this.bounceRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReceiptCancellationDialog_BounceRemarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (!this.bounceDate.isDisabled()) {
			this.bounceDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_ReceiptCancellationDialog_BounceDate.value"), true, null, null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.bounceCode.setConstraint("");
		this.bounceRemarks.setConstraint("");
		this.cancelReason.setConstraint("");
		this.bounceDate.setConstraint("");
		this.bounceCode.setErrorMessage("");
		this.bounceRemarks.setErrorMessage("");
		this.cancelReason.setErrorMessage("");
		this.bounceDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 * 
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader header = getReceiptHeader();

		this.finType.setValue(header.getFinType() + "-" + header.getFinTypeDesc());
		this.finReference.setValue(header.getReference());
		this.finCcy.setValue(header.getFinCcy() + "-" + header.getFinCcyDesc());
		this.finBranch.setValue(header.getFinBranch() + "-" + header.getFinBranchDesc());
		this.custCIF.setValue(header.getCustCIF() + "-" + header.getCustShrtName());
		int finFormatter = CurrencyUtil.getFormat(header.getFinCcy());
		this.nextInstResetDate.setValue(header.getNextRepayRvwDate());
		this.remarks.setValue(header.getRemarks());

		fillComboBox(this.receiptPurpose, header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(),
				"");
		if (!StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
			fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), "");
		} else {
			fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModesByFeePayment(),
					"");
		}

		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));

		fillComboBox(this.allocationMethod, header.getAllocationType(), PennantStaticListUtil.getAllocationMethods(),
				"");
		fillComboBox(this.effScheduleMethod, header.getEffectSchdMethod(), PennantStaticListUtil.getEarlyPayEffectOn(),
				",NOEFCT,");
		this.cancelReason.setValue(header.getCancelReason(), header.getCancelReasonDesc());
		checkByReceiptMode(header.getReceiptMode(), false);
		this.bounceDate.setValue(header.getBounceDate());
		if (header.getBounceDate() == null) {
			this.bounceDate.setValue(SysParamUtil.getAppDate());
		}
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			this.bounceDate.setValue(null);
		}
		ManualAdvise bounceReason = header.getManualAdvise();
		if (bounceReason != null) {
			this.bounceCode.setValue(String.valueOf(bounceReason.getBounceID()), bounceReason.getBounceCode());
			this.bounceCharge
					.setValue(PennantApplicationUtil.formateAmount(bounceReason.getAdviseAmount(), finFormatter));
			this.bounceRemarks.setValue(bounceReason.getRemarks());
		}

		// Repayments Schedule Basic Details
		this.payment_finType.setValue(header.getFinType() + "-" + header.getFinTypeDesc());
		this.payment_finReference.setValue(header.getReference());
		this.payment_finCcy.setValue(header.getFinCcy() + "-" + header.getFinCcyDesc());
		this.payment_finBranch.setValue(header.getFinBranch() + "-" + header.getFinBranchDesc());
		this.payment_CustCIF.setValue(header.getCustCIF() + "-" + header.getCustShrtName());

		boolean isBounceProcess = false;
		this.listBoxReceipts.getItems().clear();
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			isBounceProcess = true;
			this.row_CancelReason.setVisible(false);
			this.cancelReason.setMandatoryStyle(false);
			this.cancelReason.setReadonly(true);
			this.label_ReceiptCancellationDialog_BounceDate.setVisible(true);
			this.hbox_ReceiptCancellationDialog_BounceDate.setVisible(true);
		} else {
			this.row_BounceReason.setVisible(false);
			this.row_BounceRemarks.setVisible(false);
			this.bounceCode.setMandatoryStyle(false);
			this.bounceCode.setReadonly(true);
			this.bounceRemarks.setReadonly(true);
			this.label_ReceiptCancellationDialog_BounceDate.setVisible(false);
			this.hbox_ReceiptCancellationDialog_BounceDate.setVisible(false);
		}

		// in case of Fee Bounce
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)
				&& ReceiptMode.CHEQUE.equalsIgnoreCase(this.receiptHeader.getReceiptMode())) {
			this.row_CancelReason.setVisible(false);
			this.cancelReason.setMandatoryStyle(false);
			this.cancelReason.setReadonly(true);
			this.label_ReceiptCancellationDialog_BounceDate.setVisible(false);
			this.hbox_ReceiptCancellationDialog_BounceDate.setVisible(false);
			this.row_BounceReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);
			this.bounceCode.setMandatoryStyle(true);
			this.bounceCode.setReadonly(false);
			this.bounceRemarks.setReadonly(false);
		}

		// Separating Receipt Amounts based on user entry, if exists
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		if (header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {

				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				doFillReceipts(receiptDetail, finFormatter);
				boolean isReceiptModeDetail = false;

				if (!StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {

					this.favourNo.setValue(receiptDetail.getFavourNumber());
					this.valueDate.setValue(receiptDetail.getValueDate());
					this.bankCode.setValue(receiptDetail.getBankCode());
					this.bankCode.setDescription(receiptDetail.getBankCodeDesc());
					this.bankBranch.setValue(receiptDetail.getiFSC());
					this.bankBranch.setDescription(receiptDetail.getBranchDesc());
					this.favourName.setValue(receiptDetail.getFavourName());
					this.depositDate.setValue(receiptDetail.getDepositDate());
					this.depositNo.setValue(receiptDetail.getDepositNo());
					this.paymentRef.setValue(receiptDetail.getPaymentRef());
					this.transactionRef.setValue(receiptDetail.getTransactionRef());
					this.chequeAcNo.setValue(receiptDetail.getChequeAcNo());
					this.fundingAccount.setValue(String.valueOf(receiptDetail.getFundingAc()));
					this.fundingAccount.setDescription(receiptDetail.getFundingAcDesc());
					this.receivedDate.setValue(receiptDetail.getReceivedDate());

					isReceiptModeDetail = true;
				}
				if (StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CASH, receiptDetail.getPaymentType())
						&& StringUtils.equals(FinanceConstants.PRODUCT_GOLD, receiptHeader.getProductCategory())) {
					this.fundingAccount.setVisible(false);
					this.label_ReceiptCancellationDialog_FundingAccount.setVisible(false);
				}

				// If Bounce Process and not a Receipt Mode Record then Continue process
				if (isBounceProcess && !isReceiptModeDetail) {
					continue;
				}

				// Getting All Repayments Schedule Details for Display of Payments
				List<FinRepayHeader> repayHeaderList = receiptDetail.getRepayHeaders();
				for (int j = 0; j < repayHeaderList.size(); j++) {
					if (repayHeaderList.get(j).getRepayScheduleDetails() != null) {
						rpySchdList.addAll(repayHeaderList.get(j).getRepayScheduleDetails());
					}
				}
			}

			// Making Single Set of Repay Schedule Details and sent to Rendering
			if (!rpySchdList.isEmpty()) {

				Cloner cloner = new Cloner();
				List<RepayScheduleDetail> tempRpySchdList = cloner.deepClone(rpySchdList);
				Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();

				for (RepayScheduleDetail rpySchd : tempRpySchdList) {

					RepayScheduleDetail curRpySchd = null;
					if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
						curRpySchd = rpySchdMap.get(rpySchd.getSchDate());

						if (curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0) {
							curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
						}

						if (curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0) {
							curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
						}

						curRpySchd.setPrincipalSchdPayNow(
								curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
						curRpySchd.setProfitSchdPayNow(
								curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
						curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
						curRpySchd.setLatePftSchdPayNow(
								curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
						curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
						curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
						rpySchdMap.remove(rpySchd.getSchDate());
					} else {
						curRpySchd = rpySchd;
					}

					// Adding New Repay Schedule Object to Map after Summing data
					rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
				}

				doFillRepaySchedules(sortRpySchdDetails(new ArrayList<>(rpySchdMap.values())));
			}
			doFillFeeDetails(receiptHeader.getPaidFeeList(), finFormatter);

			// Posting Details
			this.postingDetailsTab.addForward(Events.ON_SELECT, this.window_ReceiptCancellationDialog,
					"onSelectPostingsTab");
		}
		this.receiptId.setValue(String.valueOf(header.getReceiptID()));
		this.promotionCode.setValue(header.getPromotionCode());

		this.payment_receiptId.setValue(String.valueOf(header.getReceiptID()));
		this.payment_promotionCode.setValue(header.getPromotionCode());

		this.postBranch.setValue(header.getPostBranch(), header.getPostBranchDesc());
		this.cashierBranch.setValue(header.getCashierBranch(), header.getCashierBranchDesc());
		this.finDivision.setValue(header.getFinDivision(), header.getFinDivisionDesc());

		if (this.groupbox_Finance.isVisible()) {
			readOnlyComponent(true, this.postBranch);
			readOnlyComponent(true, this.finDivision);
		} else if (this.groupbox_Customer.isVisible()) {
			this.custID.setAttribute("custID", header.getReference());
			this.custID.setValue(header.getCustomerCIF(), header.getCustomerName());
			this.extReference.setValue(StringUtils.trimToEmpty(header.getExtReference()));
		} else if (this.groupbox_Other.isVisible()) {
			this.reference.setValue(header.getReference());
		}
		appendDocumentDetailTab();

		appendExtendedFieldDetails(receiptHeader, moduleDefiner);

		this.recordStatus.setValue(header.getRecordStatus());
		logger.debug("Leaving");
	}

	protected void appendExtendedFieldDetails(FinReceiptHeader receiptHeader, String finEvent) {
		logger.debug(Literal.ENTERING);

		try {
			if (receiptHeader == null) {
				return;
			}
			if (finEvent.isEmpty()) {
				finEvent = FinServiceEvent.ORG;
			}

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, receiptHeader.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setExtendedFieldExtnt(true);
			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());

			ExtendedFieldExtension extendedFieldExtension = null;

			Boolean newRecord = (PennantConstants.RCD_STATUS_APPROVED.equals(receiptHeader.getRecordStatus())
					|| receiptHeader.getRecordStatus() == null) ? true : false;

			long instructionUID = Long.MIN_VALUE;

			if (!newRecord) {
				extendedFieldExtension = extendedFieldCtrl.getExtendedFieldExtension(
						Long.toString(receiptHeader.getReceiptID()), receiptHeader.getReceiptModeStatus(),
						PennantStaticListUtil.getFinEventCode(finEvent));
				instructionUID = extendedFieldExtension.getInstructionUID();
			}

			extendedFieldCtrl.setDataLoadReq(newRecord);

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(receiptHeader.getReference(), instructionUID);

			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			receiptHeader.setExtendedFieldHeader(extendedFieldHeader);
			receiptHeader.setExtendedFieldRender(extendedFieldRender);
			receiptHeader.setExtendedFieldExtension(extendedFieldExtension);

			if (receiptHeader.getBefImage() != null) {
				receiptHeader.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				receiptHeader.getBefImage().setExtendedFieldRender(extendedFieldRender);
				receiptHeader.getBefImage().setExtendedFieldExtension(extendedFieldExtension);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(receiptHeader.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(window_ReceiptCancellationDialog);
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		if (this.finReference.getValue().length() <= 0) {
			return null;
		}

		FinanceMain fm = receiptCancellationService.getFinBasicDetails(this.finReference.getValue());
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, fm.getFinType());
		arrayList.add(1, fm.getFinCcy());
		arrayList.add(2, PennantApplicationUtil.getLabelDesc(fm.getScheduleMethod(),
				PennantStaticListUtil.getScheduleMethods()));
		arrayList.add(3, fm.getFinReference());
		arrayList.add(4, PennantApplicationUtil.getLabelDesc(fm.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis()));
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, fm.getCustShrtName());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}

	/**
	 * Method for Rendering Receipt Amount Details
	 */
	private void doFillReceipts(FinReceiptDetail receiptDetail, int finFormatter) {
		logger.debug("Entering");

		Listitem item = new Listitem();
		Listcell lc = null;
		String label = "";
		if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
				|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
				|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PRESENTMENT)) {

			label = Labels.getLabel("label_ReceiptCancellationDialog_ExcessType_" + receiptDetail.getPaymentType());

		} else if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {
			label = receiptDetail.getFeeTypeDesc();
		} else {
			label = PennantApplicationUtil.getLabelDesc(receiptDetail.getPaymentType(),
					PennantStaticListUtil.getReceiptModes());
		}

		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(receiptDetail.getAmount(), finFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(Labels.getLabel("label_ReceiptCancellationDialog_Status_" + receiptDetail.getStatus()));
		if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_APPROVED)) {
			lc.setStyle("font-weight:bold;color: #0252d3;");
		} else if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			lc.setStyle("font-weight:bold;color: #00a83d;");
		} else if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			lc.setStyle("font-weight:bold;color: #f44b42;");
		} else if (StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
			lc.setStyle("font-weight:bold;color: #f48341;");
		}
		lc.setParent(item);
		this.listBoxReceipts.appendChild(item);
		logger.debug("Leaving");
	}

	/**
	 * Method for Selecting Posting Details tab
	 * 
	 * @param event
	 */
	public void onSelectPostingsTab(ForwardEvent event) {
		logger.debug("Entering");

		this.postingDetailsTab.removeForward(Events.ON_SELECT, this.window_ReceiptCancellationDialog,
				"onSelectPostingsTab");

		FinReceiptHeader header = getReceiptHeader();
		// Repayments Schedule Basic Details
		this.posting_finType.setValue(header.getFinType() + "-" + header.getFinTypeDesc());
		this.posting_finReference.setValue(header.getExtReference());
		this.posting_finCcy.setValue(header.getFinCcy() + "-" + header.getFinCcyDesc());
		this.posting_finBranch.setValue(header.getFinBranch() + "-" + header.getFinBranchDesc());
		this.posting_CustCIF.setValue(header.getCustCIF() + "-" + header.getCustShrtName());

		this.posting_receiptId.setValue(String.valueOf(header.getReceiptID()));
		this.posting_promotionCode.setValue(header.getPromotionCode());

		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(receiptHeader.getRecAgainst())
				&& StringUtils.isNotBlank(receiptHeader.getExtReference())) {
			this.posting_finBranch.setValue(header.getPostBranch() + "-" + header.getPostBranchDesc());
			this.posting_CustCIF.setValue(header.getCustomerCIF() + "-" + header.getCustomerName());
		}

		boolean isBounceProcess = false;
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			isBounceProcess = true;
		}

		// Identifying Transaction List
		List<Long> tranIdList = new ArrayList<>();
		if (!StringUtils.equals(header.getReceiptPurpose(), FinServiceEvent.FEEPAYMENT)) {
			if (header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()) {
				for (int i = 0; i < header.getReceiptDetails().size(); i++) {

					FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
					boolean isReceiptModeDetail = false;

					if (!StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
							&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
							&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {

						isReceiptModeDetail = true;
					}

					// If Bounce Process and not a Receipt Mode Record then Continue process
					if (isBounceProcess && !isReceiptModeDetail) {
						continue;
					}

					// List out all Transaction Id's
					List<FinRepayHeader> repayHeaderList = receiptDetail.getRepayHeaders();
					for (int j = 0; j < repayHeaderList.size(); j++) {
						tranIdList.add(repayHeaderList.get(j).getLinkedTranId());
					}

					if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEE)) {
						tranIdList.add(receiptDetail.getRepayHeader().getLinkedTranId());
					}

				}
			}
		}

		// Posting Details Rendering
		List<ReturnDataSet> postings = null;
		if (!tranIdList.isEmpty()) {
			postings = getReceiptCancellationService().getPostingsByTranIdList(tranIdList);
		} else {
			postings = getReceiptCancellationService().getPostingsByPostRef(header.getReceiptID());
		}
		doFillPostings(postings);
		logger.debug("Leaving");
	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param repayScheduleDetails
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {

		if (repayScheduleDetails != null && repayScheduleDetails.size() > 0) {
			Collections.sort(repayScheduleDetails, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail detail1, RepayScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return repayScheduleDetails;
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug("Entering");

		// setRepaySchdList(sortRpySchdDetails(repaySchdList));
		this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalTds = BigDecimal.ZERO;
		BigDecimal totalLatePft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;

		BigDecimal totSchdFeePaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		int finFormatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtil.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getProfitSchdBal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getPrincipalSchdBal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getProfitSchdPayNow(), finFormatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getTdsSchdPayNow(), finFormatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getLatePftSchdPayNow(), finFormatter));
				totalLatePft = totalLatePft.add(repaySchd.getLatePftSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(repaySchd.getPrincipalSchdPayNow(), finFormatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getPenaltyPayNow(), finFormatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getMaxWaiver(), finFormatter));
				} else {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getRefundMax(), finFormatter));
				}
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal refundPft = BigDecimal.ZERO;
				if (repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
					if (repaySchd.isAllowRefund()) {
						refundPft = repaySchd.getRefundReq();
						totalRefund = totalRefund.add(refundPft);
					} else if (repaySchd.isAllowWaiver()) {
						refundPft = repaySchd.getWaivedAmt();
						totalWaived = totalWaived.add(refundPft);
					}
				}

				lc = new Listcell(PennantApplicationUtil.amountFormate(refundPft, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Fee Details
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getSchdFeePayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPrincipalSchdPayNow())
						.add(repaySchd.getSchdFeePayNow()).add(repaySchd.getPenaltyPayNow())
						.add(repaySchd.getLatePftSchdPayNow()).subtract(refundPft);
				lc = new Listcell(PennantApplicationUtil.amountFormate(netPay, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdFeeBal());

				lc = new Listcell(PennantApplicationUtil.amountFormate(netBalance.subtract(
						netPay.subtract(repaySchd.getPenaltyPayNow()).subtract(repaySchd.getLatePftSchdPayNow())),
						finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				this.listBoxPayment.appendChild(item);
			}

			// Summary Details
			Map<String, BigDecimal> paymentMap = new HashMap<String, BigDecimal>();
			paymentMap.put("totalRefund", totalRefund);
			paymentMap.put("totalCharge", totalCharge);
			paymentMap.put("totalPft", totalPft);
			paymentMap.put("totalTds", totalTds);
			paymentMap.put("totalLatePft", totalLatePft);
			paymentMap.put("totalPri", totalPri);

			paymentMap.put("schdFeePaid", totSchdFeePaid);

			doFillSummaryDetails(paymentMap, finFormatter);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(Map<String, BigDecimal> paymentMap, int finFormatter) {

		Listcell lc;
		Listitem item;

		// Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_summary.label"));
		lc.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(15);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

		BigDecimal totalSchAmount = BigDecimal.ZERO;

		if (paymentMap.get("totalRefund").compareTo(BigDecimal.ZERO) > 0) {
			this.listheader_Refund.setVisible(true);
			totalSchAmount = totalSchAmount.subtract(paymentMap.get("totalRefund"));
			fillListItem(Labels.getLabel("listcell_totalRefund.label"), paymentMap.get("totalRefund"), finFormatter);
		} else {
			this.listheader_Refund.setVisible(false);
		}
		if (paymentMap.get("totalCharge").compareTo(BigDecimal.ZERO) > 0) {
			this.listheader_Penalty.setVisible(true);
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalCharge"));
			fillListItem(Labels.getLabel("listcell_totalPenalty.label"), paymentMap.get("totalCharge"), finFormatter);
		} else {
			this.listheader_Penalty.setVisible(false);
		}
		if (paymentMap.get("totalPft").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPft"));
			fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), paymentMap.get("totalPft"), finFormatter);
		}
		if (paymentMap.get("totalTds").compareTo(BigDecimal.ZERO) > 0) {
			fillListItem(Labels.getLabel("listcell_totalTdsPayNow.label"), paymentMap.get("totalTds"), finFormatter);
			this.listheader_Tds.setVisible(true);
		} else {
			this.listheader_Tds.setVisible(false);
		}
		if (paymentMap.get("totalLatePft").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalLatePft"));
			this.listheader_LatePft.setVisible(true);
			fillListItem(Labels.getLabel("listcell_totalLatePftPayNow.label"), paymentMap.get("totalLatePft"),
					finFormatter);
		} else {
			this.listheader_LatePft.setVisible(false);
		}
		if (paymentMap.get("totalPri").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));
			fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"), finFormatter);
		}

		if (paymentMap.get("schdFeePaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdFeePaid"));
			this.listheader_SchdFee.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdFeePayNow.label"), paymentMap.get("schdFeePaid"), finFormatter);
		} else {
			this.listheader_SchdFee.setVisible(false);
		}

		fillListItem(Labels.getLabel("listcell_totalSchAmount.label"), totalSchAmount, finFormatter);

	}

	/**
	 * Method for Showing List Item
	 * 
	 * @param label
	 * @param fieldValue
	 */
	private void fillListItem(String label, BigDecimal fieldValue, int finFormatter) {

		Listcell lc;
		Listitem item;

		item = new Listitem();
		lc = new Listcell();
		lc.setParent(item);
		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;");
		lc.setSpan(2);
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(fieldValue, finFormatter));
		lc.setStyle("text-align:right;color:#f36800;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(12);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

	}

	/**
	 * Method for Showing Posting Details which are going to be reversed
	 * 
	 * @param linkedTranId
	 */
	private void doFillPostings(List<ReturnDataSet> postingList) {
		logger.debug("Entering");

		if (postingList != null && !postingList.isEmpty()) {
			Listitem item;
			for (ReturnDataSet returnDataSet : postingList) {
				item = new Listitem();
				Listcell lc = new Listcell();
				if (returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
					lc = new Listcell(Labels.getLabel("common.Debit"));
				} else if (returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
					lc = new Listcell(Labels.getLabel("common.Credit"));
				}
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranDesc());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getRevTranCode());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranCode());
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getGlCode());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getAcCcy());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(returnDataSet.getPostAmount(),
						CurrencyUtil.getFormat(returnDataSet.getAcCcy())));
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				this.listBoxPosting.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(FinReceiptHeader aReceiptHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aReceiptHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReceiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReceiptHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (aReceiptHeader.getManualAdvise() != null) {
			aReceiptHeader.getManualAdvise().setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			aReceiptHeader.getManualAdvise().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aReceiptHeader.getManualAdvise().setUserDetails(getUserWorkspace().getLoggedInUser());
		}

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aReceiptHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if (aReceiptHeader.getManualAdvise() != null) {
				aReceiptHeader.getManualAdvise().setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReceiptHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReceiptHeader);
				}

				if (isNotesMandatory(taskId, aReceiptHeader)) {
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

			aReceiptHeader.setTaskId(taskId);
			aReceiptHeader.setNextTaskId(nextTaskId);
			aReceiptHeader.setRoleCode(getRole());
			aReceiptHeader.setNextRoleCode(nextRoleCode);

			if (aReceiptHeader.getManualAdvise() != null) {
				aReceiptHeader.getManualAdvise().setTaskId(taskId);
				aReceiptHeader.getManualAdvise().setNextTaskId(nextTaskId);
				aReceiptHeader.getManualAdvise().setRoleCode(getRole());
				aReceiptHeader.getManualAdvise().setNextRoleCode(nextRoleCode);
			}
			// Document Details
			if (CollectionUtils.isNotEmpty(aReceiptHeader.getDocumentDetails())) {
				for (DocumentDetails details : aReceiptHeader.getDocumentDetails()) {
					details.setReferenceId(String.valueOf(aReceiptHeader.getId()));
					details.setDocModule(PennantConstants.FEE_DOC_MODULE_NAME);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aReceiptHeader.getRecordStatus());
					details.setWorkflowId(aReceiptHeader.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aReceiptHeader.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aReceiptHeader.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			auditHeader = getAuditHeader(aReceiptHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aReceiptHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReceiptHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aReceiptHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinReceiptHeader aReceiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setReceiptHeader(aReceiptHeader);

		if (aReceiptHeader.getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aReceiptHeader.getExtendedFieldRender();
			details.setReference(aReceiptHeader.getReference());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(aReceiptHeader.getRecordStatus());
			details.setVersion(aReceiptHeader.getVersion());
			details.setWorkflowId(aReceiptHeader.getWorkflowId());
			details.setRecordType(aReceiptHeader.getRecordType());
			details.setTaskId(aReceiptHeader.getTaskId());
			details.setNextTaskId(aReceiptHeader.getNextTaskId());
			details.setRoleCode(aReceiptHeader.getRoleCode());
			details.setNextRoleCode(aReceiptHeader.getNextRoleCode());
			details.setNewRecord(aReceiptHeader.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aReceiptHeader.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(aReceiptHeader.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		if (aReceiptHeader.getExtendedFieldExtension() != null) {
			ExtendedFieldExtension details = aReceiptHeader.getExtendedFieldExtension();
			ExtendedFieldRender render = aReceiptHeader.getExtendedFieldRender();
			details.setExtenrnalRef(Long.toString(aReceiptHeader.getReceiptID()));
			details.setPurpose(aReceiptHeader.getReceiptPurpose());
			details.setModeStatus(aReceiptHeader.getReceiptModeStatus());
			details.setEvent(PennantStaticListUtil.getFinEventCode(aReceiptHeader.getExtendedFieldHeader().getEvent()));
			details.setSequence(aReceiptHeader.getExtendedFieldRender().getSeqNo());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(render.getRecordStatus());
			details.setRecordType(render.getRecordType());
			details.setVersion(render.getVersion());
			details.setWorkflowId(render.getWorkflowId());
			details.setTaskId(render.getTaskId());
			details.setNextTaskId(render.getNextTaskId());
			details.setRoleCode(render.getRoleCode());
			details.setNextRoleCode(render.getNextRoleCode());
			details.setNewRecord(render.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aReceiptHeader.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(render.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setExtendedFieldExtension(aReceiptHeader.getExtendedFieldExtension());
		financeDetail.setExtendedFieldRender(aReceiptHeader.getExtendedFieldRender());
		financeDetail.setExtendedFieldHeader(aReceiptHeader.getExtendedFieldHeader());

		receiptData.setFinanceDetail(financeDetail);

		auditHeader.getAuditDetail().setModelData(receiptData);

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				auditHeader = getReceiptCancellationService().saveOrUpdate(auditHeader);

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getReceiptCancellationService().doApprove(auditHeader);

					if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getReceiptCancellationService().doReject(auditHeader);
					if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ReceiptCancellationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ReceiptCancellationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					// deleteNotes(getNotes(), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptHeader receiptHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, receiptHeader);
		return new AuditHeader(String.valueOf(receiptHeader.getReceiptID()), null, null, null, auditDetail,
				receiptHeader.getUserDetails(), getOverideMap());
	}

	/**
	 * Method for Filling Fee details which are going to be paid on Origination Process
	 */
	private void doFillFeeDetails(List<FinFeeDetail> feeDetails, int finFormatter) {
		logger.debug(Literal.ENTERING);
		Listcell lc;
		Listitem item;

		this.listBoxFeeDetail.getItems().clear();
		if (feeDetails != null) {
			BigDecimal totalPaid = BigDecimal.ZERO;
			this.gb_FeeDetail.setVisible(true);

			// IMD Total Allocated Amount with GST
			Decimalbox totalPaidBox = getDecimalbox(finFormatter, true);
			for (int i = 0; i < feeDetails.size(); i++) {
				FinFeeDetail fee = feeDetails.get(i);
				item = new Listitem();

				FinFeeReceipt feeReceipt = fee.getFinFeeReceipts().get(0);

				// FeeType Desc
				lc = new Listcell(
						StringUtils.isNotEmpty(fee.getVasReference()) ? fee.getVasReference() : fee.getFeeTypeDesc());
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);

				// Net Amount Orginal
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getNetAmountOriginal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Net Amount GST
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getNetAmountGST(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Net Amount TDS
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getNetTDS(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Total Net Amount
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getNetAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Allocated Amount total
				Decimalbox allocAmtTotBox = getDecimalbox(finFormatter, true);
				allocAmtTotBox.setValue(PennantApplicationUtil.formateAmount(feeReceipt.getPaidAmount(), finFormatter));
				lc = new Listcell();
				lc.appendChild(allocAmtTotBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				this.listBoxFeeDetail.appendChild(item);
				totalPaid = totalPaid.add(feeReceipt.getPaidAmount());
			}

			if (totalPaid.compareTo(BigDecimal.ZERO) >= 0) {
				totalPaidBox.setValue(PennantApplicationUtil.formateAmount(totalPaid, finFormatter));
				doFillSummaryDetails(totalPaidBox);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(Decimalbox totalPaidBox) {

		Listcell lc;
		Listitem item;

		// Summary Details
		item = new Listitem();
		item.setId("LISTITEM_SUMMARY");
		lc = new Listcell(Labels.getLabel("listcell_Total.label"));
		item.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(5);
		lc.setParent(item);

		lc = new Listcell();
		lc.appendChild(totalPaidBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell("");
		lc.setParent(item);
		this.listBoxFeeDetail.appendChild(item);
	}

	private Decimalbox getDecimalbox(int finFormatter, boolean readOnly) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setWidth("75px");
		decimalbox.setMaxlength(18);
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		decimalbox.setDisabled(readOnly);
		return decimalbox;
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(getReceiptHeader());
	}

	@Override
	protected String getReference() {
		return String.valueOf(getReceiptHeader().getReceiptID());
	}

	/**
	 * Method for Rendering Document Details Data
	 */
	private void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getReceiptHeader().getDocumentDetails());
		map.put("module", DocumentCategories.UPFNT_FEE_RECEIPTS.getKey());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
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
		ComponentsCtrl.applyForward(tab, "onSelect=" + selectMethodName);
		logger.debug(Literal.LEAVING);
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", DocumentCategories.UPFNT_FEE_RECEIPTS.getKey());
		map.put("enqiryModule", enqiryModule);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isEditable", !isReadOnly("button_" + this.pageRightName + "_btnNewDocuments"));
		return map;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.receiptId.getValue());
		arrayList.add(1, this.finReference.getValue());
		arrayList.add(2, this.promotionCode.getValue());
		arrayList.add(3, this.finType.getValue());
		arrayList.add(4, this.finCcy.getValue());
		arrayList.add(5, this.finBranch.getValue());
		arrayList.add(6, this.custCIF.getValue());
		return arrayList;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}

	public void setReceiptHeader(FinReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public ReceiptCancellationService getReceiptCancellationService() {
		return receiptCancellationService;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public void setReceiptData(FinReceiptData receiptData) {
		this.receiptData = receiptData;
	}
}