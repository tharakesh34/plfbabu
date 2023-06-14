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
 * FileName : FeeReceiptDialogCtrl.java
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ExcessType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.web.util.ComponentUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/FeeReceiptDialog.zul
 */
public class FeeReceiptDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(FeeReceiptDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FeeReceiptDialog;
	protected Borderlayout borderlayout_FeeReceipt;

	// Receipt Details
	protected ExtendedCombobox finType;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox finCcy;
	protected ExtendedCombobox finBranch;
	protected Textbox custCIF;
	protected Textbox custName;
	protected Groupbox gb_FeeDetail;
	protected Listbox listBoxFeeDetail;

	protected Combobox receiptPurpose;
	protected Combobox excessAdjustTo;
	protected Combobox receiptMode;
	protected CurrencyBox receiptAmount;
	protected Combobox allocationMethod;
	protected Row row_RealizationDate;
	protected Datebox realizationDate;

	protected Groupbox gb_ReceiptDetails;
	protected Caption caption_receiptDetail;
	protected Label label_FeeReceiptDialog_favourNo;
	protected Label label_FeeReceiptDialog_ChequeAccountNo;
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
	protected Datebox receivedDate;
	protected Textbox remarks;

	protected Row row_favourNo;
	protected Row row_BankCode;
	protected Row row_DepositDate;
	protected Row row_PaymentRef;
	protected Row row_ChequeAcNo;
	protected Row row_fundingAcNo;
	protected Row row_remarks;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	// Buttons
	protected Button btnReceipt;

	protected transient FeeReceiptListCtrl feeReceiptListCtrl = null;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private FinReceiptHeader receiptHeader = null;
	private transient FeeReceiptService feeReceiptService;
	private AccountingSetService accountingSetService;
	private AccountEngineExecution engineExecution;
	private Label label_FeeReceiptDialog_FundingAccount;

	protected Groupbox groupbox_Finance;
	protected Groupbox groupbox_Customer;
	protected Groupbox groupbox_Other;
	protected ExtendedCombobox custID;
	protected Textbox extReference;
	protected Textbox reference;
	protected ExtendedCombobox postBranch;
	protected ExtendedCombobox cashierBranch;
	protected ExtendedCombobox finDivision;
	private boolean isAccountingExecuted = false;

	private List<FinFeeDetail> paidFeeList = new ArrayList<FinFeeDetail>();
	private long customerID;
	private Map<String, BigDecimal> taxPercentages;

	// Mapping Fields.
	private final String FINFEEDETAIL = "FINFEEDETAIL";
	private final String ALLOCATED_AMOUNT = "ALLOCATED_AMOUNT";
	private final String ALLOCATED_AMT_GST = "ALLOCATED_AMT_GST";
	private final String ALLOCATED_AMT_TDS = "ALLOCATED_AMT_TDS";
	private final String ALLOCATES_AMT_TOTAL = "ALLOCATES_AMT_TOTAL";

	private static final String FORMATTER = "FORMATTER";
	private static final String LISTITEM_SUMMARY = "LISTITEM_SUMMARY";
	private List<FinFeeReceipt> currentFinReceipts = new ArrayList<FinFeeReceipt>();
	private List<FinFeeReceipt> oldFinReceipts = new ArrayList<FinFeeReceipt>();

	private BigDecimal tdsPerc = null;
	private String tdsRoundMode = null;
	private int tdsRoundingTarget = 0;
	private String taxRoundMode = null;
	private int taxRoundingTarget = 0;
	private boolean isFinTDSApplicable = false;

	protected Listheader listheader_FeeDetailList_NetAmountOriginalTDS;
	protected Listheader listheader_FeeDetailList_PaidTDS;
	protected Listheader listheader_FeeDetailList_RemainingTDS;
	protected Listheader listheader_FeeDetailList_AllocatedAmountTDS;
	protected ExtendedFieldCtrl extendedFieldCtrl = null;
	private String moduleDefiner = null;

	FinanceDetailService financeDetailService = null;

	/**
	 * default constructor.<br>
	 */
	public FeeReceiptDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeReceiptDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FeeReceiptDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeeReceiptDialog);

		try {
			if (arguments.containsKey("receiptHeader")) {
				receiptHeader = (FinReceiptHeader) arguments.get("receiptHeader");

				FinReceiptHeader befImage = ObjectUtil.clone(receiptHeader);
				receiptHeader.setBefImage(befImage);

			}

			if (arguments.containsKey("feeReceiptListCtrl")) {
				feeReceiptListCtrl = (FeeReceiptListCtrl) arguments.get("feeReceiptListCtrl");
			}

			if (arguments.containsKey("moduleCode")) {
				moduleCode = (String) arguments.get("moduleCode");
			}

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

			// set Read only mode accordingly if the object is new or not.
			doEdit();
			if (StringUtils.isNotBlank(receiptHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}

			// Reset Finance Repay Header Details
			doWriteBeanToComponents();
			this.borderlayout_FeeReceipt.setHeight(getBorderLayoutHeight());

			// Setting tile Name based on Service Action
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FeeReceiptDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
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
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		if (getRole().equals("FEERECEIPT_APPROVER")) {
			this.btnReceipt.setLabel("Pay Receipt");
		}
		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_FeeReceiptDialog_btnReceipt"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		int formatter = CurrencyUtil.getFormat(receiptHeader.getFinCcy());

		this.finReference.setModuleName("FinanceMainTemp");
		this.finReference.setMandatoryStyle(true);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		// Remove Receipt Records , because fees in Receipts collected automatically
		Filter referenceFilter[] = new Filter[2];
		referenceFilter[0] = new Filter("RcdMaintainSts", FinServiceEvent.RECEIPT, Filter.OP_NOT_EQUAL);
		referenceFilter[1] = Filter.isNull("RcdMaintainSts");

		Filter filter[] = new Filter[1];
		filter[0] = Filter.or(referenceFilter);
		this.finReference.setFilters(filter);

		// Only once receipt can be processed at a time
		this.finReference.setWhereClause(
				" FinReference NOT IN(Select Coalesce(Reference,'') from FinReceiptHeader_FDView)) AND ( RecordStatus !='Cancelled' AND  RecordStatus !='Rejected'");
		// Receipts Details
		this.receiptAmount.setProperties(true, formatter);
		this.realizationDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);

		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(100);
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
		this.bankBranch.setFilterColumns(new String[] { "IFSC", "MICR" });

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
				this.gb_FeeDetail.setVisible(false);
			} else if (RepayConstants.RECEIPTTO_OTHER.equals(this.receiptHeader.getRecAgainst())) {
				this.groupbox_Other.setVisible(true);
				this.gb_FeeDetail.setVisible(false);
				this.reference.setMaxlength(20);
			}
		}

		// Post Branch
		this.postBranch.setModuleName("Branch");
		this.postBranch.setValueColumn("BranchCode");
		this.postBranch.setDescColumn("BranchDesc");
		this.postBranch.setValidateColumns(new String[] { "BranchCode" });
		this.postBranch.setMandatoryStyle(true);

		// Cashier Branch
		this.cashierBranch.setModuleName("Branch");
		this.cashierBranch.setValueColumn("BranchCode");
		this.cashierBranch.setDescColumn("BranchDesc");
		this.cashierBranch.setValidateColumns(new String[] { "BranchCode" });
		this.cashierBranch.setMandatoryStyle(true);

		// Fin Division
		this.finDivision.setModuleName("DivisionDetail");
		this.finDivision.setValueColumn("DivisionCode");
		this.finDivision.setDescColumn("DivisionCodeDesc");
		this.finDivision.setValidateColumns(new String[] { "DivisionCode" });
		this.finDivision.setMandatoryStyle(true);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		if (this.receiptHeader.isNewRecord()) {
			this.finReference.setReadonly(false);
			readOnlyComponent(false, this.custID);
			readOnlyComponent(false, this.reference);
			readOnlyComponent(false, this.cashierBranch);
			readOnlyComponent(false, this.postBranch);
			readOnlyComponent(false, this.finDivision);
		} else {
			this.finReference.setReadonly(true);
			readOnlyComponent(true, this.custID);
			readOnlyComponent(true, this.reference);
			readOnlyComponent(true, this.cashierBranch);
			readOnlyComponent(true, this.postBranch);
			readOnlyComponent(true, this.finDivision);
		}

		this.finType.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.custName.setReadonly(true);

		// Receipt Details
		readOnlyComponent(true, this.receiptPurpose);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_receiptMode"), this.receiptMode);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_receiptAmount"), this.receiptAmount);
		this.excessAdjustTo.setDisabled(true);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_realizationDate"), this.realizationDate);

		// Receipt Details
		readOnlyComponent(isReadOnly("FeeReceiptDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_bankBranch"), this.bankBranch);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_depositDate"), this.depositDate);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_paymentRef"), this.paymentRef);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_transactionRef"), this.transactionRef);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_fundingAccount"), this.fundingAccount);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_cashReceivedDate"), this.receivedDate);
		readOnlyComponent(isReadOnly("FeeReceiptDialog_remarks"), this.remarks);

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnReceipt.isVisible());
		if (extendedFieldCtrl != null && receiptHeader.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	public void onFulfill$custID(Event event) {
		logger.debug("Entering");

		Object dataObject = custID.getObject();

		if (dataObject instanceof String) {
			this.custID.setValue(dataObject.toString());
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setAttribute("custID", details.getCustID());
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bankCode.getObject();

		if (dataObject instanceof String) {
			this.bankCode.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setAttribute("bankCode", details.getBankCode());
			}
		}

		doSetBankBranchFilter();

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finReference.getObject();
		this.gb_FeeDetail.setVisible(false);
		boolean clearFields = false;
		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			clearFields = true;
		} else {
			FinanceMain main = (FinanceMain) dataObject;
			if (main != null) {
				finReference.setObject(main);
				this.finReference.setValue(main.getFinReference(), "");
				this.finType.setValue(main.getFinType(), main.getLovDescFinTypeName());
				this.finCcy.setValue(main.getFinCcy(), CurrencyUtil.getCcyDesc(main.getFinCcy()));
				this.finBranch.setValue(main.getFinBranch(), main.getLovDescFinBranchName());
				this.custCIF.setValue(main.getLovDescCustCIF());
				this.custName.setValue(main.getLovDescCustShrtName());
				this.finDivision.setValue(main.getLovDescFinDivision());
				this.postBranch.setValue(main.getFinBranch(), main.getLovDescFinBranchName());
				this.isFinTDSApplicable = TDSCalculator.isTDSApplicable(main);

				// setting data
				customerID = main.getCustID();
				setTaxPercentages(calcTaxPercentages());
				doFillFeeDetails(feeReceiptService.getPaidFinFeeDetails(main.getFinReference(),
						this.receiptHeader.getReceiptID(), "_TView"));
			} else {
				clearFields = true;
			}
		}

		if (clearFields) {
			this.finReference.setValue("", "");
			this.finType.setValue("", "");
			this.finCcy.setValue("", "");
			this.finBranch.setValue("", "");
			this.custCIF.setValue("");
			this.custName.setValue("");
			this.finDivision.setValue("", "");
			this.postBranch.setValue("", "");

			this.gb_FeeDetail.setVisible(false);
		}

		if (StringUtils.isNotBlank(this.finReference.getValue())) {
			receiptHeader.setFinCategory(financeDetailService.getOrgFinCategory(this.finReference.getValue()));
			appendExtendedFieldDetails(receiptHeader, moduleDefiner);
		} else {
			if (extendedFieldCtrl != null) {
				extendedFieldCtrl.removeTab(tabsIndexCenter);
				extendedFieldCtrl = null;
				receiptHeader.setExtendedFieldHeader(null);
				receiptHeader.setExtendedFieldRender(null);
				receiptHeader.setExtendedFieldExtension(null);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Filling Fee details which are going to be paid on Origination Process
	 */
	private void doFillFeeDetails(List<FinFeeDetail> feeDetails) {
		logger.debug(Literal.ENTERING);

		Listcell lc;
		Listitem item;
		setParms();

		this.listBoxFeeDetail.getItems().clear();

		if (feeDetails == null) {
			return;
		}

		this.listheader_FeeDetailList_NetAmountOriginalTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		this.listheader_FeeDetailList_PaidTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		this.listheader_FeeDetailList_RemainingTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		this.listheader_FeeDetailList_AllocatedAmountTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);

		int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());
		boolean readOnly = getUserWorkspace().isAllowed("FeeReceiptDialog_AllocatePaid");
		this.gb_FeeDetail.setVisible(true);

		for (FinFeeDetail fee : feeDetails) {
			item = new Listitem();
			FinFeeReceipt feeReceipt = fee.getFinFeeReceipts().get(0);

			String taxComponent = null;

			boolean isExclusive = FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent());
			boolean isInclusive = FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent());

			if (isExclusive) {
				taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
			} else if (isInclusive) {
				taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
			} else {
				taxComponent = Labels.getLabel("label_GST_NotApplicable");
			}

			String feeType = fee.getFeeTypeDesc() + " - (" + taxComponent + ")";
			if (StringUtils.isNotEmpty(fee.getVasReference())) {
				feeType = fee.getVasReference();
				fee.setFeeTypeCode(feeType);
				fee.setFeeTypeDesc(feeType);
			}

			// FeeType Desc
			lc = new Listcell(feeType);
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

			// Paid Amount
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmountOriginal(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Paid Amount GST
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmountGST(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Paid Amount TDS
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidTDS(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Total Paid Amount
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmount(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Remaining fee Amount
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getRemainingFeeOriginal(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Remaining fee Amount GST
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getRemainingFeeGST(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Remaining fee Amount TDS
			lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getRemTDS(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			// Remaining fee Amount with GST
			Decimalbox remainingFeeBox = getDecimalbox(finFormatter, true);
			remainingFeeBox.setValue(PennantApplicationUtil.formateAmount(fee.getRemainingFee(), finFormatter));
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			lc.appendChild(remainingFeeBox);
			lc.setParent(item);

			BigDecimal allocatedAmt = feeReceipt.getPaidAmount();
			BigDecimal allocatedAmtGST = BigDecimal.ZERO;
			BigDecimal totalAlcAmt = feeReceipt.getPaidAmount();
			BigDecimal allocatedTds = BigDecimal.ZERO;
			if (fee.isTdsReq()) {
				allocatedTds = feeReceipt.getPaidTds();
			}
			if (fee.isTaxApplicable() && allocatedAmt.compareTo(BigDecimal.ZERO) != 0) {
				if (fee.getRemainingFee().compareTo(totalAlcAmt) == 0) {
					allocatedAmtGST = fee.getNetAmountGST().subtract(fee.getPaidAmountGST());
					allocatedTds = fee.getNetTDS().subtract(fee.getPaidTDS());
				} else {
					TaxAmountSplit taxSplit = null;
					taxSplit = GSTCalculator.getInclusiveGST(totalAlcAmt.add(feeReceipt.getPaidTds()), taxPercentages);
					allocatedAmtGST = taxSplit.gettGST();
				}
			}
			allocatedAmt = totalAlcAmt.subtract(allocatedAmtGST).add(allocatedTds);

			// Allocated Amount
			Decimalbox allocAmtBox = getDecimalbox(finFormatter, true);
			allocAmtBox.setValue(PennantApplicationUtil.formateAmount(allocatedAmt, finFormatter));
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			lc.appendChild(allocAmtBox);
			lc.setParent(item);

			// Allocate Amount GST
			Decimalbox allocAmtGstBox = getDecimalbox(finFormatter, true);
			allocAmtGstBox.setValue(PennantApplicationUtil.formateAmount(allocatedAmtGST, finFormatter));
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			lc.appendChild(allocAmtGstBox);
			lc.setParent(item);

			// Allocate Amount TDS
			Decimalbox allocAmtTdsBox = getDecimalbox(finFormatter, true);
			allocAmtTdsBox.setValue(PennantApplicationUtil.formateAmount(allocatedTds, finFormatter));
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			lc.appendChild(allocAmtTdsBox);
			lc.setParent(item);

			// Allocated Amount total
			Decimalbox totAllocAmtTotBox = getDecimalbox(finFormatter, !readOnly);
			totAllocAmtTotBox.setValue(PennantApplicationUtil.formateAmount(totalAlcAmt, finFormatter));

			lc = new Listcell();
			lc.appendChild(totAllocAmtTotBox);
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			double remainingFee = PennantApplicationUtil.formateAmount(fee.getRemainingFee(), finFormatter)
					.doubleValue();
			if (remainingFee <= 0) {
				totAllocAmtTotBox.setReadonly(true);
			}

			this.listBoxFeeDetail.appendChild(item);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FINFEEDETAIL, fee);
			map.put(ALLOCATED_AMOUNT, allocAmtBox);
			map.put(ALLOCATED_AMT_GST, allocAmtGstBox);
			map.put(ALLOCATED_AMT_TDS, allocAmtTdsBox);
			map.put(ALLOCATES_AMT_TOTAL, totAllocAmtTotBox);
			map.put(FORMATTER, finFormatter);
			totAllocAmtTotBox.addForward("onChange", window_FeeReceiptDialog, "onChangeFeeAmount", map);
		}

		Listitem summaryItem = createSummaryItem(finFormatter);
		this.listBoxFeeDetail.appendChild(summaryItem);
		doFillSummaryDetails(listBoxFeeDetail);

		receiptHeader.setPaidFeeList(feeDetails);
		setPaidFeeList(feeDetails);

		logger.debug(Literal.LEAVING);
	}

	private Decimalbox getDecimalbox(int finFormatter, boolean readOnly) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setWidth("75px");
		decimalbox.setMaxlength(18);
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		decimalbox.setDisabled(readOnly);
		return decimalbox;
	}

	@SuppressWarnings("unchecked")
	public void onChangeFeeAmount(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		doClearListValidations();
		Map<String, Object> map = (Map<String, Object>) event.getData();
		FinFeeDetail fee = (FinFeeDetail) map.get(FINFEEDETAIL);
		FinFeeReceipt feeReceipt = fee.getFinFeeReceipts().get(0);
		Decimalbox allocAmtBox = (Decimalbox) map.get(ALLOCATED_AMOUNT);
		Decimalbox allocAmtGstBox = (Decimalbox) map.get(ALLOCATED_AMT_GST);
		Decimalbox allocAmtTdsBox = (Decimalbox) map.get(ALLOCATED_AMT_TDS);
		Decimalbox totAllocAmtTotBox = (Decimalbox) map.get(ALLOCATES_AMT_TOTAL);
		int formatter = (int) map.get(FORMATTER);

		BigDecimal allocatedAmtGST = BigDecimal.ZERO;
		BigDecimal allocatedAmtTDS = BigDecimal.ZERO;
		BigDecimal allocatedAmt = BigDecimal.ZERO;
		BigDecimal totAlocAmt = PennantApplicationUtil.unFormateAmount(totAllocAmtTotBox.getValue(), formatter);
		BigDecimal fraction = BigDecimal.ONE;
		BigDecimal totPerc = BigDecimal.ZERO;

		BigDecimal netAmountOriginal = fee.getNetAmountOriginal();
		BigDecimal netTDS = fee.getNetTDS();
		totPerc = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);

		if (fee.isTdsReq() && this.isFinTDSApplicable) {
			allocatedAmtTDS = (netTDS.multiply(totAlocAmt)).divide(netAmountOriginal, 2, RoundingMode.HALF_DOWN);
			allocatedAmtTDS = CalculationUtil.roundAmount(allocatedAmtTDS, tdsRoundMode, tdsRoundingTarget);
			totPerc = taxPercentages.get(RuleConstants.CODE_TOTAL_GST).subtract(tdsPerc);
		}

		if (fee.isTaxApplicable()) {
			fraction = fraction.add(totPerc.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN));

			BigDecimal orgAmt = totAlocAmt.divide(fraction, 0, RoundingMode.HALF_DOWN);
			orgAmt = CalculationUtil.roundAmount(orgAmt, taxRoundMode, taxRoundingTarget);
			allocatedAmtGST = GSTCalculator.getExclusiveGST(orgAmt, taxPercentages).gettGST();

		}

		allocatedAmt = totAlocAmt.subtract(allocatedAmtGST).add(allocatedAmtTDS);

		BigDecimal diffAmt = BigDecimal.ZERO;
		BigDecimal diffGST = BigDecimal.ZERO;
		BigDecimal diffTDS = BigDecimal.ZERO;

		if (totAlocAmt.compareTo(fee.getRemainingFee()) == 0) {
			diffAmt = fee.getRemainingFeeOriginal().subtract(allocatedAmt);
			diffGST = fee.getRemainingFeeGST().subtract(allocatedAmtGST);
			diffTDS = fee.getRemTDS().subtract(allocatedAmtTDS);
		}

		allocatedAmt = allocatedAmt.add(diffAmt);
		allocatedAmtGST = allocatedAmtGST.add(diffGST);
		allocatedAmtTDS = allocatedAmtTDS.add(diffTDS);

		allocAmtGstBox.setValue(PennantApplicationUtil.formateAmount(allocatedAmtGST, formatter));
		allocAmtTdsBox.setValue(PennantApplicationUtil.formateAmount(allocatedAmtTDS, formatter));
		allocAmtBox.setValue(PennantApplicationUtil.formateAmount(allocatedAmt, formatter));
		totAllocAmtTotBox.setValue(PennantApplicationUtil.formateAmount(totAlocAmt, formatter));
		feeReceipt.setPaidAmount(totAlocAmt);
		feeReceipt.setPaidTds(allocatedAmtTDS);

		doFillSummaryDetails(listBoxFeeDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill the IMD summary.
	 * 
	 * @param listBoxFeeDetail
	 */
	private void doFillSummaryDetails(Listbox listBoxFeeDetail) {

		BigDecimal totRemAmt = BigDecimal.ZERO;
		BigDecimal totAllocFeeAmt = BigDecimal.ZERO;
		BigDecimal totAllocGstAmt = BigDecimal.ZERO;
		BigDecimal totAllocTdsAmt = BigDecimal.ZERO;
		BigDecimal totpaid = BigDecimal.ZERO;

		Listitem summaryItem = null;

		for (Listitem listitem : listBoxFeeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			if (LISTITEM_SUMMARY.equalsIgnoreCase(listitem.getId())) {
				summaryItem = listitem;
				continue;
			} else {
				// Total remaining
				Listcell totRemLC = list.get(12);
				Decimalbox totRemBox = (Decimalbox) totRemLC.getFirstChild();
				totRemAmt = totRemAmt.add(totRemBox.getValue());
				// Total Allocated Fee
				Listcell totAllocFeeLC = list.get(13);
				Decimalbox totFeeAmtBox = (Decimalbox) totAllocFeeLC.getFirstChild();
				totAllocFeeAmt = totAllocFeeAmt.add(totFeeAmtBox.getValue());
				// Total Allocated Fee GST
				Listcell totAllocGstLC = list.get(14);
				Decimalbox totGstAmtBox = (Decimalbox) totAllocGstLC.getFirstChild();
				totAllocGstAmt = totAllocGstAmt.add(totGstAmtBox.getValue());
				// Total Allocated Fee GST
				Listcell totAllocTdsLC = list.get(15);
				Decimalbox totTdsAmtBox = (Decimalbox) totAllocTdsLC.getFirstChild();
				totAllocTdsAmt = totAllocTdsAmt.add(totTdsAmtBox.getValue());
				// Total Allocated
				Listcell totAllocAmt = list.get(16);
				Decimalbox totBox = (Decimalbox) totAllocAmt.getFirstChild();
				totpaid = totpaid.add(totBox.getValue() == null ? BigDecimal.ZERO : totBox.getValue());
			}
		}

		List<Listcell> list = summaryItem.getChildren();

		// Total remaining
		Listcell totRemLC = list.get(1);
		Decimalbox totRemBox = (Decimalbox) totRemLC.getFirstChild();
		totRemBox.setValue(totRemAmt);
		// Total Allocated Fee
		Listcell totAllocFeeLC = list.get(2);
		Decimalbox totFeeAmtBox = (Decimalbox) totAllocFeeLC.getFirstChild();
		totFeeAmtBox.setValue(totAllocFeeAmt);
		// Total Allocated Fee GST
		Listcell totAllocGstLC = list.get(3);
		Decimalbox totGstAmtBox = (Decimalbox) totAllocGstLC.getFirstChild();
		totGstAmtBox.setValue(totAllocGstAmt);
		// Total Allocated Fee GST
		Listcell totAllocTdsLC = list.get(4);
		Decimalbox totTdsAmtBox = (Decimalbox) totAllocTdsLC.getFirstChild();
		totTdsAmtBox.setValue(totAllocTdsAmt);
		// Total Allocated
		Listcell totAllocAmt = list.get(5);
		Decimalbox totBox = (Decimalbox) totAllocAmt.getFirstChild();
		totBox.setValue(totpaid);
	}

	/**
	 * Method for creating summary list Item
	 * 
	 * @param formatter
	 * @return
	 */
	private Listitem createSummaryItem(int formatter) {
		Listcell lc;
		Listitem item;

		// Summary Details
		item = new Listitem();
		item.setId(LISTITEM_SUMMARY);
		lc = new Listcell(Labels.getLabel("listcell_Total.label"));
		item.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(12);
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox totalRemBox = getDecimalbox(formatter, true);
		lc.appendChild(totalRemBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox totalAllocFeeBox = getDecimalbox(formatter, true);
		lc.appendChild(totalAllocFeeBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox totalAllocGstBox = getDecimalbox(formatter, true);
		lc.appendChild(totalAllocGstBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox totalAllocTdsBox = getDecimalbox(formatter, true);
		lc.appendChild(totalAllocTdsBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox totalPaidBox = getDecimalbox(formatter, true);
		lc.appendChild(totalPaidBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		return item;
	}

	/**
	 * Method for Processing Captured details based on Receipt Mode
	 * 
	 * @param event
	 */
	public void onChange$receiptMode(Event event) {
		String dType = this.receiptMode.getSelectedItem().getValue().toString();
		checkByReceiptMode(dType, true);
	}

	/**
	 * 
	 * @param event
	 */
	public void onFulfill$postBranch(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = postBranch.getObject();

		if (dataObject instanceof String) {
			this.postBranch.setValue(dataObject.toString(), "");
		} else {
			Branch branchDetails = (Branch) dataObject;

			if (branchDetails != null) {
				this.postBranch.setValue(branchDetails.getBranchCode(), branchDetails.getBranchDesc());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param event
	 */
	public void onFulfill$cashierBranch(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = cashierBranch.getObject();

		if (dataObject instanceof String) {
			this.cashierBranch.setValue(dataObject.toString(), "");
		} else {
			Branch branchDetails = (Branch) dataObject;

			if (branchDetails != null) {
				this.cashierBranch.setValue(branchDetails.getBranchCode(), branchDetails.getBranchDesc());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finDivision(Event event) {
		logger.debug("Entering");

		Object dataObject = finDivision.getObject();

		if (dataObject instanceof String) {
			this.finDivision.setValue(dataObject.toString(), "");
		} else {
			DivisionDetail details = (DivisionDetail) dataObject;
			if (details != null) {
				this.finDivision.setValue(details.getDivisionCode(), details.getDivisionCodeDesc());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");

		Date appDate = SysParamUtil.getAppDate();

		if (isUserAction) {
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.favourNo.setValue("");
			this.valueDate.setValue(appDate);
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankCode.setObject(null);
			this.bankBranch.setValue("");
			this.bankBranch.setDescription("");
			this.bankBranch.setObject(null);
			this.favourName.setValue("");
			this.depositDate.setValue(null);
			this.depositNo.setValue("");
			this.paymentRef.setValue("");
			this.transactionRef.setValue("");
			this.chequeAcNo.setValue("");
			this.fundingAccount.setValue("");
			this.fundingAccount.setDescription("");
			this.fundingAccount.setObject(null);
			this.receivedDate.setValue(appDate);
			this.remarks.setValue("");
		}

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select)
				|| StringUtils.equals(recMode, ReceiptMode.EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else {

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("FeeReceiptDialog_receiptAmount"), this.receiptAmount);

			if (this.groupbox_Finance.isVisible()) {
				Filter fundingAcFilters[] = new Filter[3];
				fundingAcFilters[0] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
				fundingAcFilters[1] = new Filter("FinType", this.finType.getValue(), Filter.OP_EQUAL);
				fundingAcFilters[2] = new Filter("PaymentMode", recMode, Filter.OP_EQUAL);
				Filter.and(fundingAcFilters);
				this.fundingAccount.setFilters(fundingAcFilters);
			}
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
					this.label_FeeReceiptDialog_favourNo
							.setValue(Labels.getLabel("label_FeeReceiptDialog_ChequeFavourNo.value"));
					this.label_FeeReceiptDialog_ChequeAccountNo.setVisible(true);
					this.chequeAcNo.setVisible(true);
					if (isUserAction) {
						this.depositDate.setValue(appDate);
						this.receivedDate.setValue(appDate);
						this.valueDate.setValue(appDate);
					}

				} else {
					this.row_ChequeAcNo.setVisible(true);
					this.label_FeeReceiptDialog_ChequeAccountNo.setVisible(false);
					this.chequeAcNo.setVisible(false);
					this.label_FeeReceiptDialog_favourNo
							.setValue(Labels.getLabel("label_FeeReceiptDialog_DDFavourNo.value"));

					if (isUserAction) {
						this.depositDate.setValue(appDate);
						this.valueDate.setValue(appDate);
					}
				}

				if (isUserAction) {
					this.favourName.setValue(Labels.getLabel("label_ClientName"));
				}

			} else if (StringUtils.equals(recMode, ReceiptMode.CASH)) {

				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				this.label_FeeReceiptDialog_FundingAccount.setVisible(false);
				this.fundingAccount.setVisible(false);
				if (isUserAction) {
					this.receivedDate.setValue(appDate);
				}

			} else {
				this.label_FeeReceiptDialog_FundingAccount.setVisible(true);
				this.fundingAccount.setVisible(true);
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
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
	public void onClick$btnReceipt(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() {
		logger.debug("Entering");

		try {
			boolean recReject = false;
			if (this.userAction.getSelectedItem() != null
					&& ("Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
				recReject = true;
			}

			if (!recReject) {
				doClearMessage();
				doSetValidation();
				doWriteComponentsToBean();
				// Accounting Details Validations

				if (SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTS_SHOW_ACCOUNTING_TAB)) {
					if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) != null
							&& getTab(AssetConstants.UNIQUE_ID_ACCOUNTING).isVisible()) {
						boolean validate = false;
						validate = validateAccounting(validate);
						if (validate && !isAccountingExecuted) {
							MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
							return;
						}
					}
				}
				FinReceiptHeader rch = getReceiptHeader();

				if (rch.getExtReference() == null) {
					rch.setExtReference(rch.getReference());
				}

				if (rch.getReceiptDetails().isEmpty()) {
					FinReceiptDetail rcd = new FinReceiptDetail();
					rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
					rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
					rch.getReceiptDetails().add(rcd);
				}
				rch.setRemarks(this.remarks.getValue());
				if (ReceiptMode.CHEQUE.equals(rch.getReceiptMode()) || ReceiptMode.DD.equals(rch.getReceiptMode())) {
					rch.setTransactionRef(this.favourNo.getValue());
				} else if (ReceiptMode.CASH.equals(rch.getReceiptMode())) {
					rch.setTransactionRef(this.paymentRef.getValue());
				} else {
					rch.setTransactionRef(this.transactionRef.getValue());
				}
				rch.setBankCode(this.bankCode.getValue());
				for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
					rcd.setAmount(rch.getReceiptAmount());
					rcd.setPaymentType(rch.getReceiptMode());
					rcd.setFavourNumber(this.favourNo.getValue());
					rcd.setValueDate(this.valueDate.getValue());
					rcd.setBankCode(this.bankCode.getValue());
					rcd.setFavourName(this.favourName.getValue());
					rcd.setDepositDate(this.depositDate.getValue());
					rcd.setDepositNo(this.depositNo.getValue());
					rcd.setPaymentRef(this.paymentRef.getValue());
					rcd.setTransactionRef(this.transactionRef.getValue());
					rcd.setChequeAcNo(this.chequeAcNo.getValue());
					// bank branch id
					Object obj = this.bankBranch.getAttribute("bankBranchID");
					if (obj != null) {
						rcd.setBankBranchID(Long.valueOf(String.valueOf(obj)));
					} else {
						rcd.setBankBranchID(0);
					}

					if (!this.fundingAccount.getValue().isEmpty()) {
						rcd.setFundingAc(Long.valueOf(this.fundingAccount.getValue()));
					}
					rcd.setReceivedDate(this.receivedDate.getValue());

					if (rcd.getRepayHeaders().isEmpty()) {
						FinRepayHeader repayHeader = new FinRepayHeader();

						repayHeader.setFinID(rch.getFinID());
						repayHeader.setFinReference(rch.getReference());
						repayHeader.setValueDate(this.receivedDate.getValue());
						repayHeader.setFinEvent(FinServiceEvent.FEEPAYMENT);
						repayHeader.setRepayAmount(rch.getReceiptAmount());

						rcd.getRepayHeaders().add(repayHeader);
					} else {
						rcd.getRepayHeaders().get(0).setValueDate(this.receivedDate.getValue());
						rcd.getRepayHeaders().get(0).setRepayAmount(rch.getReceiptAmount());
					}
				}
			}

			// Extended Fields
			if (extendedFieldCtrl != null && receiptHeader.getExtendedFieldHeader() != null) {
				receiptHeader.setExtendedFieldRender(extendedFieldCtrl.save(true));

				FinReceiptHeader rh = getReceiptHeader();

				if (receiptHeader.getExtendedFieldRender() != null) {
					ExtendedFieldExtension efe = new ExtendedFieldExtension();
					if (extendedFieldCtrl.getExtendedFieldExtension() != null) {
						BeanUtils.copyProperties(extendedFieldCtrl.getExtendedFieldExtension(), efe);
					}

					efe.setExtenrnalRef(Long.toString(rh.getReceiptID()));
					efe.setPurpose(rh.getReceiptPurpose());
					efe.setModeStatus(rh.getReceiptModeStatus());
					efe.setSequence(receiptHeader.getExtendedFieldRender().getSeqNo());
					efe.setEvent(
							PennantStaticListUtil.getFinEventCode(receiptHeader.getExtendedFieldHeader().getEvent()));

					receiptHeader.setExtendedFieldExtension(efe);
				}

			}

			// If Schedule Re-modified Save into DB or else only add Repayments Details
			doProcessReceipt();

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		} catch (WrongValuesException we) {
			throw we;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}
		logger.debug("Leaving");
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
	 * Method for Process Repayment Details
	 */
	private void doProcessReceipt() {
		logger.debug("Entering");

		// Receipt Header Details workflow fields
		FinReceiptHeader receiptHeader = getReceiptHeader();
		receiptHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		receiptHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		// Duplicate Creation of Object
		FinReceiptHeader aReceiptHeader = ObjectUtil.clone(receiptHeader);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReceiptHeader.getRecordType())) {
				aReceiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aReceiptHeader.setVersion(1);
				aReceiptHeader.setNewRecord(true);
			} else {
				aReceiptHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}

		} else {
			aReceiptHeader.setVersion(aReceiptHeader.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		// save it to database
		try {

			if (doProcess(aReceiptHeader, tranType)) {

				if (feeReceiptListCtrl != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aReceiptHeader.getNextTaskId())) {
					aReceiptHeader.setNextRoleCode("");
				}
				String ref = null;
				if (this.groupbox_Customer.isVisible()) {
					ref = this.custID.getValue();
				} else if (this.groupbox_Other.isVisible()) {
					ref = this.reference.getValue();
				} else if (this.groupbox_Finance.isVisible()) {
					ref = this.finReference.getValue();
				}
				String msg = PennantApplicationUtil.getSavingStatus(aReceiptHeader.getRoleCode(),
						aReceiptHeader.getNextRoleCode(), ref, " Fee Receipt ", aReceiptHeader.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				if (extendedFieldCtrl != null && aReceiptHeader.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				// User Notifications Message/Alert
				publishNotification(Notify.ROLE, aReceiptHeader.getReference(), aReceiptHeader);

				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
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
		int finFormatter = CurrencyUtil.getFormat(header.getFinCcy());
		fillComboBox(this.receiptPurpose, FinServiceEvent.FEEPAYMENT, PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), ExcessType.getAdjustmentList(), "");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModesByFeePayment(),
				",EXCESS,MOBILE,");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
		this.realizationDate.setValue(header.getRealizationDate());
		if (!isReadOnly("FeeReceiptDialog_realizationDate") || header.getRealizationDate() != null) {
			this.row_RealizationDate.setVisible(true);
		} else {
			this.row_RealizationDate.setVisible(false);
		}
		this.remarks.setValue(header.getRemarks());

		if (!header.isNewRecord()) {
			this.finReference.setValue(header.getReference(), "");
			this.isFinTDSApplicable = header.isFinTDSApplicable();
			this.finType.setValue(header.getFinType(), header.getFinTypeDesc());
			this.finCcy.setValue(header.getFinCcy(), CurrencyUtil.getCcyDesc(header.getFinCcy()));
			this.finBranch.setValue(header.getFinBranch(), header.getFinBranchDesc());
			this.custCIF.setValue(header.getCustCIF());
			this.custName.setValue(header.getCustShrtName());

			this.postBranch.setValue(header.getPostBranch(), header.getPostBranchDesc());
			this.cashierBranch.setValue(header.getCashierBranch(), header.getCashierBranchDesc());
			this.finDivision.setValue(header.getFinDivision(), header.getFinDivisionDesc());

			if (RepayConstants.RECEIPTTO_FINANCE.equals(this.receiptHeader.getRecAgainst())) {
				// Setting Data
				customerID = receiptHeader.getCustID();
				setTaxPercentages(calcTaxPercentages());
				setPaidFeeList(receiptHeader.getPaidFeeList());
			} else if (RepayConstants.RECEIPTTO_CUSTOMER.equals(this.receiptHeader.getRecAgainst())) {
				customerID = receiptHeader.getCustID();
				this.finBranch.setValue(header.getPostBranch(), header.getPostBranchDesc());
				setTaxPercentages(calcTaxPercentages());
			}

		} else {
			this.postBranch.setValue(getUserWorkspace().getLoggedInUser().getBranchCode(),
					getUserWorkspace().getLoggedInUser().getBranchName());
			this.cashierBranch.setValue(getUserWorkspace().getLoggedInUser().getBranchCode(),
					getUserWorkspace().getLoggedInUser().getBranchName());
			SecurityUser securityUser = getFeeReceiptService()
					.getSecurityUserById(getUserWorkspace().getLoggedInUser().getUserId(), "");
			if (securityUser != null && securityUser.isAccessToAllBranches()) {
				readOnlyComponent(false, this.cashierBranch);
			} else {
				readOnlyComponent(true, this.cashierBranch);
			}
		}

		String allocateMthd = header.getAllocationType();
		if (StringUtils.isEmpty(allocateMthd)) {
			allocateMthd = AllocationType.AUTO;
		}
		fillComboBox(this.allocationMethod, allocateMthd, PennantStaticListUtil.getAllocationMethods(), "");
		checkByReceiptMode(header.getReceiptMode(), false);

		// Separating Receipt Amounts based on user entry, if exists
		Map<String, BigDecimal> receiptAmountsMap = new HashMap<>();
		if (header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				receiptAmountsMap.put(receiptDetail.getPaymentType(), receiptDetail.getAmount());
				if (!StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
						&& !StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {
					this.receiptAmount
							.setValue(PennantApplicationUtil.formateAmount(receiptDetail.getAmount(), finFormatter));
					this.favourNo.setValue(receiptDetail.getFavourNumber());
					this.valueDate.setValue(receiptDetail.getValueDate());
					this.bankCode.setValue(receiptDetail.getBankCode());
					this.bankCode.setDescription(receiptDetail.getBankCodeDesc());
					this.favourName.setValue(receiptDetail.getFavourName());
					this.depositDate.setValue(receiptDetail.getDepositDate());
					this.depositNo.setValue(receiptDetail.getDepositNo());
					this.paymentRef.setValue(receiptDetail.getPaymentRef());
					this.transactionRef.setValue(receiptDetail.getTransactionRef());
					this.chequeAcNo.setValue(receiptDetail.getChequeAcNo());
					this.fundingAccount.setValue(String.valueOf(receiptDetail.getFundingAc()));
					this.fundingAccount.setDescription(receiptDetail.getFundingAcDesc());
					this.receivedDate.setValue(receiptDetail.getReceivedDate());

					// bank branch
					if (receiptDetail.getBankBranchID() != Long.MIN_VALUE && receiptDetail.getBankBranchID() != 0) {
						this.bankBranch.setAttribute("bankBranchID", receiptDetail.getBankBranchID());
						this.bankBranch.setValue(StringUtils.trimToEmpty(receiptDetail.getiFSC()));
						this.bankBranch.setDescription(StringUtils.trimToEmpty(receiptDetail.getBranchDesc()));
					}
				}
			}

			doSetBankBranchFilter();
		}

		if (this.groupbox_Finance.isVisible()) {
			// Fee Details
			doFillFeeDetails(header.getPaidFeeList());

			readOnlyComponent(true, this.postBranch);
			readOnlyComponent(true, this.finDivision);
		} else if (this.groupbox_Customer.isVisible()) {
			this.custID.setAttribute("custID", header.getReference());
			this.extReference.setValue(StringUtils.trimToEmpty(header.getExtReference()));
			if (!header.isNewRecord()) {
				this.custID.setValue(header.getCustomerCIF(), header.getCustomerName());
			}
			doFillFeeDetails(header.getPaidFeeList());
		} else if (this.groupbox_Other.isVisible()) {
			this.reference.setValue(header.getReference());
		}

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			// Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}

		if (StringUtils.isNotBlank(receiptHeader.getReference())) {
			appendExtendedFieldDetails(receiptHeader, moduleDefiner);
		}

		this.recordStatus.setValue(header.getRecordStatus());
		logger.debug("Leaving");
	}

	private void checkAndSetModDef(String module) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(module)) {
			if ("FeeReceipt".equals(module)) {
				moduleDefiner = FinServiceEvent.UPFRONT_FEE;
			}
		}

		logger.debug(Literal.LEAVING);
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
					.getExtendedFieldRender(this.finReference.getValue(), instructionUID);

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
			extendedFieldCtrl.setWindow(window_FeeReceiptDialog);
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
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());

			// Fetch Accounting Set ID
			AccountingSet accountingSet = accountingSetService.getAccSetSysDflByEvent(AccountingEvent.FEEPAY,
					AccountingEvent.FEEPAY, "");

			long acSetID = 0;
			if (accountingSet != null) {
				acSetID = accountingSet.getAccountSetid();
			}

			map.put("acSetID", acSetID);
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

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		arrayList.add(2, PennantApplicationUtil.getLabelDesc(getReceiptHeader().getScheduleMethod(),
				PennantStaticListUtil.getScheduleMethods()));
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, PennantApplicationUtil.getLabelDesc(getReceiptHeader().getPftDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis()));
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, this.custName.getValue());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
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
		ComponentsCtrl.applyForward(tab, ("onSelect=onSelectAccountTab"));
		logger.debug("Leaving");
	}

	public void onSelectAccountTab(ForwardEvent event) {

		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, tab, "onSelectAccountTab");
		appendAccountingDetailTab(false);
		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
		}
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void executeAccounting() {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.FEEPAY);

		FinReceiptHeader rch = getReceiptHeader();
		aeEvent.setFinID(rch.getFinID());
		aeEvent.setFinReference(rch.getReference());
		aeEvent.setCustCIF(rch.getCustCIF());
		aeEvent.setBranch(rch.getPostBranch());
		aeEvent.setCcy(rch.getFinCcy());
		aeEvent.setCustID(rch.getCustID());

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(rch.getCustBaseCcy());
		}

		Map<String, Object> map = null;
		Long accountingSetID = 0L;
		if (this.groupbox_Finance.isVisible()) {
			map = feeReceiptService.getGLSubHeadCodes(rch.getFinID());
			accountingSetID = AccountingEngine.getAccountSetID(rch.getFinType(), AccountingEvent.FEEPAY,
					FinanceConstants.MODULEID_FINTYPE);
		} else {
			accountingSetID = getFeeReceiptService().getAccountingSetId(AccountingEvent.FEEPAY, AccountingEvent.FEEPAY);
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(0);
		amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
		amountCodes.setPaymentType(receiptDetail.getPaymentType());
		amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
		amountCodes.setPaidFee(receiptDetail.getAmount());
		amountCodes.setFinType(rch.getFinType());

		if (map != null) {
			amountCodes.setBusinessvertical((String) map.get("BUSINESSVERTICAL"));
			amountCodes.setFinbranch((String) map.get("FINBRANCH"));
			amountCodes.setEntitycode((String) map.get("ENTITYCODE"));
			BigDecimal alwFlexi = BigDecimal.ZERO;
			if (map.get("ALWFLEXI") instanceof Long) {
				long value = (long) map.get("ALWFLEXI");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			} else if (map.get("ALWFLEXI") instanceof BigDecimal) {
				alwFlexi = (BigDecimal) map.get("ALWFLEXI");
				amountCodes.setAlwflexi(alwFlexi.compareTo(BigDecimal.ZERO) == 0 ? false : true);
			} else if (map.get("ALWFLEXI") instanceof Integer) {
				int value = (int) map.get("ALWFLEXI");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			}
		} else {
			amountCodes.setEntitycode(rch.getEntityCode());
			amountCodes.setFinbranch(rch.getPostBranch());
		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		feeReceiptService.prepareFeeRulesMap(receiptHeader, dataMap);

		if (accountingSetID != null && accountingSetID > 0) {
			aeEvent.getAcSetIDList().add(accountingSetID);

			if (map != null) {
				dataMap.put("emptype", map.get("EMPTYPE"));
				dataMap.put("branchcity", map.get("BRANCHCITY"));
				dataMap.put("fincollateralreq", map.get("FINCOLLATERALREQ"));
				dataMap.put("btloan", map.get("BTLOAN"));
			} else {
				dataMap.put("emptype", "");
				dataMap.put("branchcity", "");
				dataMap.put("fincollateralreq", false);
				dataMap.put("btloan", "");
			}

			aeEvent.setDataMap(dataMap);

			// GST parameters
			Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(rch.getFinID());
			if (gstExecutionMap != null) {
				for (String mapkey : gstExecutionMap.keySet()) {
					if (StringUtils.isNotBlank(mapkey)) {
						aeEvent.getDataMap().put(mapkey, gstExecutionMap.get(mapkey));
					}
				}
			}

			// execute accounting
			aeEvent.setDataMap(dataMap);
			engineExecution.getAccEngineExecResults(aeEvent);
			accountingSetEntries.addAll(aeEvent.getReturnDataSet());
		} else {
			Clients.showNotification(Labels.getLabel("label_FeeReceiptDialog_NoAccounting.value"), "warning", null,
					null, -1);
		}

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
			isAccountingExecuted = true;
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		Date appDate = SysParamUtil.getAppDate();

		if (!this.receiptPurpose.isDisabled()) {
			this.receiptPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPurpose(),
					Labels.getLabel("label_FeeReceiptDialog_ReceiptPurpose.value")));
		}

		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModesByFeePayment(),
					Labels.getLabel("label_FeeReceiptDialog_ReceiptMode.value")));
		}
		if (!this.excessAdjustTo.isDisabled()) {
			this.excessAdjustTo.setConstraint(new StaticListValidator(ExcessType.getAdjustmentList(),
					Labels.getLabel("label_FeeReceiptDialog_ExcessAdjustTo.value")));
		}
		if (!this.allocationMethod.isDisabled()) {
			this.allocationMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getAllocationMethods(),
					Labels.getLabel("label_FeeReceiptDialog_AllocationMethod.value")));
		}

		if (this.row_RealizationDate.isVisible() && !this.realizationDate.isDisabled()) {
			this.realizationDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_RealizationDate.value"),
							true, this.valueDate.getValue(), appDate, true));
		}

		if (StringUtils.equals(recptMode, ReceiptMode.CHEQUE)) {

			if (!this.chequeAcNo.isReadonly()) {
				this.chequeAcNo.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FeeReceiptDialog_ChequeAccountNo.value"), null, false));
			}
		}

		if (!StringUtils.equals(recptMode, ReceiptMode.EXCESS)) {
			if (!this.fundingAccount.isReadonly() && this.fundingAccount.isVisible()) {
				this.fundingAccount.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FeeReceiptDialog_FundingAccount.value"), null, true));
			}

			if (!this.receivedDate.isDisabled()) {
				this.receivedDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_ReceivedDate.value"),
								true, SysParamUtil.getValueAsDate(""), appDate, true));
			}
		}

		if (StringUtils.equals(recptMode, ReceiptMode.DD) || StringUtils.equals(recptMode, ReceiptMode.CHEQUE)) {

			if (!this.favourNo.isReadonly()) {
				String label = Labels.getLabel("label_FeeReceiptDialog_ChequeFavourNo.value");
				if (StringUtils.equals(recptMode, ReceiptMode.DD)) {
					label = Labels.getLabel("label_FeeReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(
						new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}

			if (!this.valueDate.isDisabled()) {
				this.valueDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_ValueDate.value"),
								true, SysParamUtil.getValueAsDate(""), appDate, true));
			}

			if (!this.bankCode.isReadonly()) {
				this.bankCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FeeReceiptDialog_BankCode.value"), null, true, true));
			}

			if (!this.favourName.isReadonly()) {
				this.favourName
						.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_favourName.value"),
								PennantRegularExpressions.REGEX_NAME, true));
			}

			if (!this.depositDate.isDisabled() && !this.depositDate.isReadonly()) {
				this.depositDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_FeeReceiptDialog_DepositDate.value"),
								true, SysParamUtil.getValueAsDate(""), appDate, true));
			}

			if (!this.depositNo.isReadonly()) {
				this.depositNo
						.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_depositNo.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		if (StringUtils.equals(recptMode, ReceiptMode.NEFT) || StringUtils.equals(recptMode, ReceiptMode.RTGS)
				|| StringUtils.equals(recptMode, ReceiptMode.IMPS)
				|| StringUtils.equals(recptMode, ReceiptMode.DIGITAL)) {

			if (!this.transactionRef.isReadonly()) {
				this.transactionRef.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_tranReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
		}

		if (!StringUtils.equals(recptMode, ReceiptMode.EXCESS)) {
			if (!this.paymentRef.isReadonly()) {
				this.paymentRef.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_paymentReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}

			if (!this.remarks.isReadonly()) {
				this.remarks
						.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_Remarks.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		}

		if (this.groupbox_Customer.isVisible()) {
			if (!this.custID.isReadonly()) {
				this.custID.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_CustID.value"), null, true));
			}
		} else if (this.groupbox_Other.isVisible()) {
			if (!this.reference.isReadonly()) {
				this.reference.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FeeReceiptDialog_Reference.value"), null, true));
			}
		}

		if (!this.postBranch.isReadonly()) {
			this.postBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FeeReceiptDialog_PostBranch.value"), null, true, true));
		}

		if (!this.cashierBranch.isReadonly()) {
			this.cashierBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FeeReceiptDialog_CashierBranch.value"), null, true, true));
		}

		if (!this.finDivision.isReadonly()) {
			this.finDivision.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FeeReceiptDialog_FinDivision.value"), null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.receiptPurpose.setConstraint("");
		this.receiptMode.setConstraint("");
		this.excessAdjustTo.setConstraint("");
		this.allocationMethod.setConstraint("");
		this.realizationDate.setConstraint("");

		this.favourNo.setConstraint("");
		this.valueDate.setConstraint("");
		this.bankCode.setConstraint("");
		this.favourName.setConstraint("");
		this.depositDate.setConstraint("");
		this.depositNo.setConstraint("");
		this.paymentRef.setConstraint("");
		this.transactionRef.setConstraint("");
		this.chequeAcNo.setConstraint("");
		this.fundingAccount.setConstraint("");
		this.receivedDate.setConstraint("");
		this.remarks.setConstraint("");
		this.custID.setConstraint("");
		this.reference.setConstraint("");
		this.postBranch.setConstraint("");
		this.cashierBranch.setConstraint("");
		this.finDivision.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.receiptPurpose.setErrorMessage("");
		this.receiptMode.setErrorMessage("");
		this.excessAdjustTo.setErrorMessage("");
		this.allocationMethod.setErrorMessage("");
		this.realizationDate.setErrorMessage("");

		this.favourNo.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.favourName.setErrorMessage("");
		this.depositDate.setErrorMessage("");
		this.depositNo.setErrorMessage("");
		this.paymentRef.setErrorMessage("");
		this.transactionRef.setErrorMessage("");
		this.chequeAcNo.setErrorMessage("");
		this.fundingAccount.setErrorMessage("");
		this.receivedDate.setErrorMessage("");
		this.remarks.setErrorMessage("");

		this.custID.setErrorMessage("");
		this.reference.setErrorMessage("");
		this.postBranch.setErrorMessage("");
		this.cashierBranch.setErrorMessage("");
		this.finDivision.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * 
	 * @return
	 */
	private FinReceiptHeader doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();
		int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());

		FinReceiptHeader header = getReceiptHeader();
		// Setting value date from receiupt header for backdated receipt
		header.setReceiptDate(SysParamUtil.getAppDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);

		if (this.groupbox_Finance.isVisible()) {
			if (header.getFinID() == null || header.getFinID() == 0) {
				header.setFinID(ComponentUtil.getFinID(this.finReference));
			}
			header.setReference(this.finReference.getValue());
		} else if (this.groupbox_Customer.isVisible()) {
			// Cust ID
			try {
				this.custID.getValidatedValue();
				String custid = String.valueOf(this.custID.getAttribute("custID"));
				header.setCustID(Long.valueOf((custid)));
				header.setCustomerCIF(this.custID.getValue()); // Customer CIF
				header.setReference(custid);
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else if (this.groupbox_Other.isVisible()) {
			try {
				header.setReference(this.reference.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// Post Branch Code
		try {
			receiptHeader.setPostBranch(this.postBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Cashier Branch Code
		try {
			receiptHeader.setCashierBranch(this.cashierBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FinDivision
		try {
			receiptHeader.setFinDivision(this.finDivision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		header.setReceiptPurpose(FinServiceEvent.FEEPAYMENT);
		header.setCustID(this.customerID);
		String finCcy = this.finCcy.getValue();
		if (StringUtils.isNotBlank(finCcy)) {
			header.setFinCcy(finCcy);
		}
		header.setFinBranch(this.finBranch.getValue());

		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(excessAdjustTo))) {
				header.setExcessAdjustTo(ExcessType.EXCESS);
			} else {
				header.setExcessAdjustTo(getComboboxValue(excessAdjustTo));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setAllocationType(getComboboxValue(allocationMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setReceiptAmount(
					PennantApplicationUtil.unFormateAmount(receiptAmount.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setRealizationDate(this.realizationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Receipt Mode Details
		try {
			this.favourNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.valueDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.bankCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.favourName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.depositDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.depositNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.paymentRef.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.transactionRef.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.chequeAcNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.fundingAccount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setValueDate(this.receivedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.remarks.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		wve.addAll(validateFeeDetails());

		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
		return header;
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

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aReceiptHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

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

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getFeeReceiptService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFeeReceiptService().doApprove(auditHeader);

						if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFeeReceiptService().doReject(auditHeader);
						if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FeeReceiptDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FeeReceiptDialog, auditHeader);
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

		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Exception: ", e);
		} catch (AppException e) {
			MessageUtil.showError(e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(getReceiptHeader());
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptHeader receiptHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, receiptHeader);
		return new AuditHeader(String.valueOf(receiptHeader.getReceiptID()), null, null, null, auditDetail,
				receiptHeader.getUserDetails(), getOverideMap());
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinReceiptHeader> soReceipt = feeReceiptListCtrl.getSearchObject();
		feeReceiptListCtrl.pagingFeeReceiptList.setActivePage(0);
		feeReceiptListCtrl.getPagedListWrapper().setSearchObject(soReceipt);
		if (feeReceiptListCtrl.listBoxFeeReceipt != null) {
			feeReceiptListCtrl.listBoxFeeReceipt.getListModel();
		}
	}

	@Override
	protected String getReference() {
		return this.finReference.getValue();
	}

	private ArrayList<WrongValueException> validateFeeDetails() {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<>();
		doClearListValidations();
		BigDecimal totalPaid = BigDecimal.ZERO;
		int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());
		for (Listitem listitem : listBoxFeeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			if (LISTITEM_SUMMARY.equalsIgnoreCase(listitem.getId())) {
				Listcell totalPaidLC = list.get(5);
				Decimalbox totalPaidAmt = (Decimalbox) totalPaidLC.getFirstChild();
				try {
					totalPaid = totalPaidAmt.getValue();
					if (totalPaidAmt.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(totalPaidLC,
								Labels.getLabel("FIELD_NO_NEGATIVE",
										new String[] {
												Labels.getLabel("listheader_FeeDetailList_AllocatedAmtTotal.label"),
												String.valueOf(BigDecimal.ZERO) }));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
				continue;
			}

			// Total remaining
			Listcell totRemainningLC = list.get(12);
			Decimalbox totRemainingAmtBox = (Decimalbox) totRemainningLC.getFirstChild();
			double remainingFee = totRemainingAmtBox.getValue().doubleValue();

			Listcell totAllocAmtLC = list.get(16);
			Decimalbox totAllocAmt = (Decimalbox) totAllocAmtLC.getFirstChild();
			totAllocAmt.setConstraint(
					new PTDecimalValidator(Labels.getLabel("listheader_FeeDetailList_AllocatedAmount.label"),
							finFormatter, false, false, 0, remainingFee));
			try {
				totAllocAmt.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			if (this.receiptAmount.getValidateValue().compareTo(totalPaid) < 0) {
				String totPaid = PennantApplicationUtil.amountFormate(totalPaid, 0);
				throw new WrongValueException(this.receiptAmount, Labels.getLabel("NUMBER_MINVALUE_EQ",
						new String[] { Labels.getLabel("label_FeeReceiptDialog_ReceiptAmount.value"), totPaid }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Clears validation error messages from all the fields of the ListBox.
	 */
	public void doClearListValidations() {
		logger.debug(Literal.ENTERING);
		for (Listitem listitem : listBoxFeeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			if (LISTITEM_SUMMARY.equalsIgnoreCase(listitem.getId())) {
				Listcell totalPaidLC = list.get(1);
				Clients.clearWrongValue(totalPaidLC);
			} else {
				Listcell paidNetAmtLC = list.get(10);
				Clients.clearWrongValue(paidNetAmtLC);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private Map<String, BigDecimal> calcTaxPercentages() {
		String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
		String finCcy = this.finCcy.getValue();
		String finBranch = this.finBranch.getValue();
		return GSTCalculator.getTaxPercentages(customerID, finCcy, userBranch, finBranch);
	}

	public void onFulfill$bankBranch(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = bankBranch.getObject();

		if (dataObject instanceof String) {
			this.bankBranch.setValue(dataObject.toString());
		} else {
			BankBranch details = (BankBranch) dataObject;

			if (details != null) {
				this.bankBranch.setAttribute("bankBranchID", details.getBankBranchID());
			} else {
				this.bankBranch.setAttribute("bankBranchID", 0);// if user removed the saved data
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSetBankBranchFilter() {
		Filter[] filters = new Filter[1];
		String value = this.bankCode.getValue();
		if (value != null) {
			filters[0] = new Filter("BankCode", value, Filter.OP_EQUAL);
			this.bankBranch.setFilters(filters);
		}
	}

	private void setParms() {
		if (tdsPerc == null || tdsRoundMode == null || tdsRoundingTarget == 0 || taxRoundMode == null
				|| taxRoundingTarget == 0) {
			tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
			taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
			taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);
		}
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

	public FeeReceiptService getFeeReceiptService() {
		return feeReceiptService;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public List<FinFeeDetail> getPaidFeeList() {
		return paidFeeList;
	}

	public void setPaidFeeList(List<FinFeeDetail> paidFeeList) {
		this.paidFeeList = paidFeeList;
	}

	@SuppressWarnings("unused")
	private Map<String, BigDecimal> getTaxPercentages() {
		return taxPercentages;
	}

	public void setTaxPercentages(Map<String, BigDecimal> taxPercentages) {
		this.taxPercentages = taxPercentages;
	}

	public List<FinFeeReceipt> getCurrentFinReceipts() {
		return currentFinReceipts;
	}

	public void setCurrentFinReceipts(List<FinFeeReceipt> currentFinReceipts) {
		this.currentFinReceipts = currentFinReceipts;
	}

	public List<FinFeeReceipt> getOldFinReceipts() {
		return oldFinReceipts;
	}

	public void setOldFinReceipts(List<FinFeeReceipt> oldFinReceipts) {
		this.oldFinReceipts = oldFinReceipts;
	}

	public void setExtendedFieldCtrl(ExtendedFieldCtrl extendedFieldCtrl) {
		this.extendedFieldCtrl = extendedFieldCtrl;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}