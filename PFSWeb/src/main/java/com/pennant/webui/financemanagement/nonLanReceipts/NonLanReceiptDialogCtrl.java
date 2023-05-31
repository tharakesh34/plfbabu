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
 * 
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ReceiptDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-06-2011 * * Modified
 * Date : 03-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * 29-09-2018 somasekhar 0.2 added backdate sp also, * 10-10-2018 somasekhar 0.3 Ticket
 * id:124998,defaulting receipt* purpose and excessadjustto for * closed loans * Ticket id:124998 * 13-06-2018 Siva 0.2
 * Receipt auto printing on approval * * 13-06-2018 Siva 0.3 Receipt Print Option Added * * 17-06-2018 Srinivasa Varma
 * 0.4 PSD 126950 * * 19-06-2018 Siva 0.5 Auto Receipt Number Generation * * 28-06-2018 Siva 0.6 Stop printing Receipt
 * if receipt mode status is either cancel or Bounce * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.nonLanReceipts;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.UploadConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.core.EventManager;
import com.pennant.pff.fee.AdviseType;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
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
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul
 */
public class NonLanReceiptDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(NonLanReceiptDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_NonLanReceiptDialog;
	protected Borderlayout borderlayout_Receipt;
	protected Label windowTitle;

	// Receipt Details
	protected Groupbox gb_ReceiptDetails;
	protected Textbox receiptId;
	protected Combobox receiptMode;
	protected Label receiptTypeLabel;
	protected Combobox receiptChannel;
	protected Combobox subReceiptMode;
	protected Datebox receiptDate;
	protected CurrencyBox receiptAmount;
	protected Label label_ReceiptDialog_ReceiptModeStatus;
	protected Hbox hbox_ReceiptModeStatus;
	protected Combobox receiptModeStatus;
	protected Row row_RealizationDate;
	protected Datebox realizationDate;
	protected Row row_BounceReason;
	protected ExtendedCombobox bounceCode;
	protected CurrencyBox bounceCharge;
	protected Row row_BounceRemarks;
	protected Textbox bounceRemarks;
	protected Datebox bounceDate;
	protected Row row_CancelReason;
	protected ExtendedCombobox cancelReason;
	protected Textbox cancelRemarks;
	protected Row row_ReceiptModeStatus;
	protected Hbox hbox_ReceiptDialog_DepositDate;
	protected Combobox receiptSource;
	protected Label favourNameDesc;
	protected ExtendedCombobox cashierBranch;
	protected Combobox receivedFrom;
	protected Label label_NonReceiptDialog_CustID;
	protected ExtendedCombobox custCIF;
	protected Hbox hbox_NonReceiptDialog;

	// Transaction Details
	protected Textbox panNumber;
	protected ExtendedCombobox fundingAccount;
	protected ExtendedCombobox collectionAgentId;
	protected Uppercasebox extReference;
	protected Textbox remarks;

	// Instrument Details
	protected Groupbox gb_InstrumentDetails;
	protected Uppercasebox favourNo;
	protected Datebox valueDate;
	protected ExtendedCombobox bankCode;
	protected Textbox favourName;
	protected Datebox depositDate;
	protected Uppercasebox depositNo;
	protected Uppercasebox transactionRef;
	protected AccountSelectionBox chequeAcNo;
	protected Uppercasebox paymentRef;

	protected Groupbox gb_TransactionDetails;
	protected Label label_ReceiptDialog_favourNo;
	protected Row row_favourNo;
	protected Row row_BankCode;
	protected Row row_DepositDate;
	protected Row row_DepositBank;
	protected Row row_PaymentRef;
	protected Row row_ChequeAcNo;
	protected Row row_fundingAcNo;
	protected Row row_remarks;

	// Hybrid Changes
	protected Label label_FinGracePeriodEndDate;

	protected Button btnPrint;
	protected Button btnReceipt;

	protected Tab postingDetailsTab;
	protected Tabpanel tabpanel_Postings;
	protected Listbox listBoxPostings;

	private RuleService ruleService;
	private ReceiptService receiptService;
	private NonLanReceiptService nonLanReceiptService;
	private ReceiptCancellationService receiptCancellationService;
	private RuleExecutionUtil ruleExecutionUtil;
	private AccountEngineExecution engineExecution;
	private AccrualService accrualService;
	private PartnerBankService partnerBankService;
	private EventManager eventManager;

	protected NonLanReceiptListCtrl nonLanReceiptListCtrl = null;

	private FinReceiptData receiptData = null;

	private String recordType = "";
	private FinReceiptHeader befImage;

	// Temporary Fix for the User Next role Modification On Submit-Fail & Saving
	// the record
	protected String curRoleCode;
	protected String curNextRoleCode;
	protected String curTaskId;
	protected String curNextTaskId;
	protected String curNextUserId;

	protected String module = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	private int formatter = 0;
	private int receiptPurposeCtg = -1;

	protected boolean recSave = false;
	protected Component checkListChildWindow = null;
	protected boolean isEnquiry = false;
	protected HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

	private boolean isPanMandatory = false;
	private String paymentType;
	private long receiptSeqId;
	private String old_ReceiptModeStatus;

	private List<ValueLabel> receiptModeList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_MODE);
	private List<ValueLabel> receiptChannelList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_CHANNEL);
	private List<ValueLabel> subReceiptModeList = PennantAppUtil
			.getActiveFieldCodeList(RepayConstants.SUB_RECEIPT_MODE);
	private List<ValueLabel> receiptSourceList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_SOURCE);

	public static final String RECEIPT_CHANNEL_MOBILE = "MOB";

	/**
	 * default constructor.<br>
	 */
	public NonLanReceiptDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReceiptDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_NonLanReceiptDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_NonLanReceiptDialog);
		FinReceiptData receiptData = new FinReceiptData();
		FinReceiptHeader finReceiptHeader = new FinReceiptHeader();

		try {
			if (arguments.containsKey("receiptData")) {
				setReceiptData((FinReceiptData) arguments.get("receiptData"));
				receiptData = getReceiptData();

				finReceiptHeader = receiptData.getReceiptHeader();

				formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

				recordType = finReceiptHeader.getRecordType();

				befImage = ObjectUtil.clone(finReceiptHeader);
				receiptData.getReceiptHeader().setBefImage(befImage);
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("nonLanReceiptListCtrl")) {
				setNonLanReceiptListCtrl((NonLanReceiptListCtrl) arguments.get("nonLanReceiptListCtrl"));
			}

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			doLoadWorkFlow(finReceiptHeader.isWorkflow(), finReceiptHeader.getWorkflowId(),
					finReceiptHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				String recStatus = StringUtils.trimToEmpty(finReceiptHeader.getRecordStatus());
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
			doStoreServiceIds(finReceiptHeader);

			// set Read only mode accordingly if the object is new or not.
			if (StringUtils.isBlank(finReceiptHeader.getRecordType())) {
				doEdit();
				// this.btnReceipt.setDisabled(true);
			}

			// Setting tile Name based on Service Action
			this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
			this.windowTitle.setValue("Non-Loan " + Labels.getLabel(module + "_Window.Title"));

			if (!enqiryModule) {
				doShowDialog(finReceiptHeader);
			} else {
				doReadOnly(true);
				this.postingDetailsTab.setVisible(true);
				this.tabpanel_Postings.setVisible(true);
				doFillPostings(finReceiptHeader);
				this.btnReceipt.setVisible(false);
				doWriteBeanToComponents();
				this.windowTitle.setValue(Labels.getLabel("NonLanReceipt_Enquiry_Window.Title"));
			}

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_NonLanReceiptDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void doFillPostings(FinReceiptHeader finReceiptHeader) {
		logger.debug("Entering");
		List<ReturnDataSet> returnDataSet = nonLanReceiptService.getPostingsByPostRefAndFinEvent(
				String.valueOf(finReceiptHeader.getReceiptID()), AccountingEvent.NLRCPT);

		for (ReturnDataSet returnData : returnDataSet) {

			Listitem item = new Listitem();
			Listcell lc;

			// FinEvent
			lc = new Listcell(String.valueOf(returnData.getTranOrderId()));
			lc.setParent(item);

			lc = new Listcell(returnData.getFinEvent());
			lc.setParent(item);

			// Posting Date
			lc = new Listcell(PennantAppUtil.formateDate(returnData.getPostDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			// Value Date
			lc = new Listcell(
					PennantAppUtil.formateDate(returnData.getValueDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			// Transaction Reference
			lc = new Listcell(returnData.getTranDesc());
			lc.setParent(item);

			// Receipt Amount
			lc = new Listcell(returnData.getDrOrCr());
			lc.setParent(item);

			// Excess Amount
			lc = new Listcell(returnData.getTranCode());
			lc.setParent(item);

			// Utilised Amount
			lc = new Listcell(returnData.getRevTranCode());
			lc.setParent(item);

			lc = new Listcell(returnData.getGlCode());
			lc.setParent(item);

			// Reserved Amount
			lc = new Listcell(returnData.getAccount());
			lc.setParent(item);

			lc = new Listcell(returnData.getAcCcy());
			lc.setParent(item);

			// Available Amount
			lc = new Listcell(PennantApplicationUtil.amountFormate(returnData.getPostAmount(), formatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(returnData.getPostStatus());
			lc.setParent(item);

			this.listBoxPostings.appendChild(item);

		}

		logger.debug("Leaving");
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

		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receiptAmount.setProperties(true, formatter);
		this.realizationDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setMandatoryStyle(true);
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(
				new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });

		this.bounceCode.setModuleName("BounceReason");
		this.bounceCode.setMandatoryStyle(true);
		this.bounceCode.setValueColumn("BounceID");
		this.bounceCode.setValueType(DataType.LONG);
		this.bounceCode.setDescColumn("Reason");
		this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceID", "BounceCode", "Lovdesccategory", "Reason" });

		this.bounceCharge.setProperties(false, formatter);
		this.bounceRemarks.setMaxlength(100);
		this.bounceDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setModuleName("ReceiptPartnerBankModes");
		this.fundingAccount.setValueColumn("PartnerBankCode");
		this.fundingAccount.setDescColumn("PartnerBankName");
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankCode" });
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("Entity", getReceiptData().getReceiptHeader().getEntityCode(), Filter.OP_EQUAL);
		String filVal = "";
		/*
		 * if ("#".equals(getReceiptData().getReceiptHeader().getSubReceiptMode())) { filVal =
		 * getReceiptData().getReceiptHeader().getReceiptMode(); } else { filVal =
		 * getReceiptData().getReceiptHeader().getSubReceiptMode(); }
		 */
		filters[1] = new Filter("PaymentMode", filVal, Filter.OP_EQUAL);
		this.fundingAccount.setFilters(filters);

		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);

		// this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(500);
		this.favourName.setMaxlength(50);
		this.favourName.setDisabled(true);
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

		this.collectionAgentId.setModuleName("COLLECTION_AGENCIES");
		this.collectionAgentId.setValueColumn("Code");
		// this.collectionAgentId.setValueType(DataType.LONG);
		this.collectionAgentId.setDescColumn("Description");
		this.collectionAgentId.setDisplayStyle(2);
		this.collectionAgentId.setValidateColumns(new String[] { "Code" });

		if (DisbursementConstants.PAYMENT_TYPE_MOB.equals(receiptData.getReceiptHeader().getReceiptChannel())
				|| ReceiptMode.BANKDEPOSIT.equals(receiptData.getReceiptHeader().getSubReceiptMode())) {
			this.collectionAgentId.setMandatoryStyle(true);
		}

		if (!StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER) && (StringUtils
				.equals(receiptData.getReceiptHeader().getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
						DisbursementConstants.PAYMENT_TYPE_DD))) {
			this.row_DepositDate.setVisible(true);
			this.row_DepositBank.setVisible(true);
			this.hbox_ReceiptDialog_DepositDate.setVisible(true);
		}

		if (StringUtils.equals(module, FinanceConstants.REALIZATION_MAKER)
				|| StringUtils.equals(module, FinanceConstants.REALIZATION_APPROVER) || enqiryModule) {
			this.row_CancelReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);
			this.row_RealizationDate.setVisible(true);
			this.row_ReceiptModeStatus.setVisible(true);
			this.label_ReceiptDialog_ReceiptModeStatus.setVisible(true);
			this.hbox_ReceiptModeStatus.setVisible(true);
			this.receiptModeStatus.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param Receipt
	 */
	public void doShowDialog(FinReceiptHeader finReceiptHeader) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (finReceiptHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			checkByReceiptMode(finReceiptHeader.getReceiptMode(), true);
			doWriteBeanToComponents();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_NonLanReceiptDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_collectionAgent"), this.collectionAgentId);
		readOnlyComponent(isReadOnly("ReceiptDialog_panNumber"), this.panNumber);

		// Bounce/Realization/Cancel Reason Fields
		readOnlyComponent(isReadOnly("ReceiptDialog_realizationDate"), this.realizationDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceCode"), this.bounceCode);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceCharge"), this.bounceCharge);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceRemarks"), this.bounceRemarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceDate"), this.bounceDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"), this.cancelReason);
		readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"), this.cancelRemarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_receiptModeStatus"), this.receiptModeStatus);

		// Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("ReceiptDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("ReceiptDialog_depositDate"), this.depositDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_transactionRef"), this.transactionRef);
		// readOnlyComponent(isReadOnly("ReceiptDialog_cashReceivedDate"),
		// this.receivedDate);

		// Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("ReceiptDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("ReceiptDialog_depositDate"), this.depositDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_paymentRef"), this.paymentRef);
		readOnlyComponent(isReadOnly("ReceiptDialog_transactionRef"), this.transactionRef);
		readOnlyComponent(isReadOnly("ReceiptDialog_fundingAccount"), this.fundingAccount);
		readOnlyComponent(true, this.cashierBranch);
		// readOnlyComponent(true, this.receivedDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadOnly(boolean isUserAction) {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(true, this.collectionAgentId);
		readOnlyComponent(true, this.panNumber);
		readOnlyComponent(true, this.extReference);
		readOnlyComponent(true, this.custCIF);
		readOnlyComponent(true, this.receivedFrom);

		// Receipt Details
		if (isUserAction) {
			readOnlyComponent(true, this.favourNo);
			readOnlyComponent(true, this.valueDate);
			readOnlyComponent(true, this.bankCode);
			readOnlyComponent(true, this.favourName);
			readOnlyComponent(true, this.depositDate);
			readOnlyComponent(true, this.depositNo);
			readOnlyComponent(true, this.chequeAcNo);
			readOnlyComponent(true, this.fundingAccount);
			readOnlyComponent(true, this.paymentRef);
			readOnlyComponent(true, this.transactionRef);
			// readOnlyComponent(true, this.receivedDate);
			readOnlyComponent(true, this.remarks);
			readOnlyComponent(true, this.cashierBranch);

			// Bounce/Realization/Cancel Reason Fields
			readOnlyComponent(true, this.realizationDate);
			readOnlyComponent(true, this.bounceCode);
			readOnlyComponent(true, this.bounceCharge);
			readOnlyComponent(true, this.bounceRemarks);
			readOnlyComponent(true, this.bounceDate);
			readOnlyComponent(true, this.cancelReason);
			readOnlyComponent(true, this.receiptModeStatus);

		}

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnReceipt.isVisible());
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
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Selecting Bounce Reason Code in case of Receipt got Bounced
	 * 
	 * @param event
	 */
	public void onFulfill$bounceCode(Event event) {
		logger.debug("Entering" + event.toString());

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		Object dataObject = bounceCode.getObject();

		if (dataObject instanceof String) {
			this.bounceCode.setValue(dataObject.toString());
		} else {
			BounceReason bounceReason = (BounceReason) dataObject;
			if (bounceReason != null) {
				Map<String, Object> executeMap = bounceReason.getDeclaredFieldValues();

				if (receiptHeader != null) {
					if (receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()) {
						for (FinReceiptDetail finReceiptDetail : receiptHeader.getReceiptDetails()) {
							if (StringUtils.equals(receiptHeader.getReceiptMode(), finReceiptDetail.getPaymentType())) {
								finReceiptDetail.getDeclaredFieldValues(executeMap);
								break;
							}
						}
					}
				}

				Rule rule = getRuleService().getRuleById(bounceReason.getRuleID(), "");
				BigDecimal bounceAmt = BigDecimal.ZERO;
				if (rule != null) {
					executeMap.put("br_finType", receiptHeader.getFinType());
					bounceAmt = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executeMap,
							receiptHeader.getFinCcy(), RuleReturnType.DECIMAL);
					// unFormating BounceAmt
					bounceAmt = PennantApplicationUtil.unFormateAmount(bounceAmt, formatter);
				}
				this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(bounceAmt, formatter));
			}
		}
	}

	/**
	 * Method for Processing Captured details based on Receipt Purpose
	 * 
	 * @param event
	 * @throws InterruptedException
	 */

	/**
	 * Method for Processing Captured details based on Receipt Mode
	 * 
	 * @param event
	 */
	public void onChange$receiptMode(Event event) {
		logger.debug("Entering");

		String dType = this.receiptMode.getSelectedItem().getValue().toString();
		checkByReceiptMode(dType, true);
		logger.debug("Leaving");
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");
		if (isUserAction) {
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.favourNo.setValue("");
			this.valueDate.setValue(SysParamUtil.getAppDate());
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankCode.setObject(null);
			// this.favourName.setValue("");
			this.depositDate.setValue(null);
			this.depositNo.setValue("");
			this.transactionRef.setValue("");
			this.chequeAcNo.setValue("");
			this.fundingAccount.setValue("");
			this.fundingAccount.setDescription("");
			this.fundingAccount.setObject(null);
			// this.receivedDate.setValue(DateUtility.getAppDate());
		}

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else {

			this.gb_ReceiptDetails.setVisible(true);
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_receiptAmount"), this.receiptAmount);

			this.row_remarks.setVisible(true);

			if (StringUtils.equals(recMode, ReceiptMode.CHEQUE) || StringUtils.equals(recMode, ReceiptMode.DD)) {

				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				// this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);

				if (ImplementationConstants.DEPOSIT_PROC_REQ) {
					// this.row_fundingAcNo.setVisible(false);
				} else {
					// this.row_fundingAcNo.setVisible(true);
				}

				if (StringUtils.equals(recMode, ReceiptMode.CHEQUE)) {
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo
							.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));

					if (isUserAction) {
						this.depositDate.setValue(SysParamUtil.getAppDate());
						// this.receivedDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(SysParamUtil.getAppDate());
					}

				} else {
					this.row_ChequeAcNo.setVisible(false);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_DDFavourNo.value"));

					if (isUserAction) {
						this.depositDate.setValue(SysParamUtil.getAppDate());
						this.valueDate.setValue(SysParamUtil.getAppDate());
					}
				}

			} else if (StringUtils.equals(recMode, ReceiptMode.CASH)) {

				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				this.row_DepositBank.setVisible(false);
				// this.row_fundingAcNo.setVisible(false);
				readOnlyComponent(true, this.fundingAccount);

				if (isUserAction) {
					// this.receivedDate.setValue(DateUtility.getAppDate());
				}

			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
			}
		}

		if (StringUtils.equals(module, FinanceConstants.DEPOSIT_MAKER)
				&& (StringUtils.equals(recMode, ReceiptMode.CHEQUE) || StringUtils.equals(recMode, ReceiptMode.DD))) {
			this.fundingAccount.setMandatoryStyle(true);
			this.fundingAccount.setReadonly(false);

		} else if (StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER)
				&& !StringUtils.equals(recMode, ReceiptMode.CHEQUE) && !StringUtils.equals(recMode, ReceiptMode.DD)
				&& !StringUtils.equals(recMode, ReceiptMode.CASH)) {
			this.fundingAccount.setMandatoryStyle(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * 
	 * @param event
	 */
	public void onChange$receiptModeStatus(Event event) {
		logger.debug("Entering");
		// Based on Status of Mode Details will be set to Visible
		String status = this.receiptModeStatus.getSelectedItem().getValue().toString();
		resetModeStatus(status);
		logger.debug("Leaving");
	}

	private void setReceiptModeStatus(FinReceiptHeader rch) {
		String exclude = "";
		if (!rch.getReceiptMode().equals(ReceiptMode.CHEQUE) && !rch.getReceiptMode().equals(ReceiptMode.DD)) {
			exclude = ",R,B,";
		}
		if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			this.bounceDate.setValue(rch.getBounceDate());
			if (rch.getBounceDate() == null) {
				this.bounceDate.setValue(SysParamUtil.getAppDate());
			}

			ManualAdvise bounceReason = rch.getManualAdvise();
			if (bounceReason != null) {
				this.bounceCode.setValue(String.valueOf(bounceReason.getBounceID()), bounceReason.getBounceCodeDesc());
				this.bounceCharge
						.setValue(PennantApplicationUtil.formateAmount(bounceReason.getAdviseAmount(), formatter));
				this.bounceRemarks.setValue(bounceReason.getRemarks());
			}
		} else if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
			this.cancelReason.setValue(rch.getCancelReason(), rch.getCancelReasonDesc());
			this.cancelRemarks.setValue(rch.getCancelRemarks());
		} else if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			this.realizationDate.setValue(rch.getRealizationDate());
		}

		if (enqiryModule) {
			exclude = "";
			fillComboBox(this.receiptModeStatus, rch.getReceiptModeStatus(),
					PennantStaticListUtil.getEnquiryReceiptModeStatus(), exclude);
		} else {
			fillComboBox(this.receiptModeStatus, rch.getReceiptModeStatus(),
					PennantStaticListUtil.getReceiptModeStatus(), exclude);
		}
	}

	private void resetModeStatus(String status) {
		logger.debug("Entering");

		// readOnlyComponent(true, this.bounceCode);
		// readOnlyComponent(true, this.bounceCharge);
		// readOnlyComponent(true, this.bounceRemarks);
		// readOnlyComponent(true, this.bounceDate);
		// readOnlyComponent(true, this.cancelReason);
		// readOnlyComponent(true, this.realizationDate);

		this.row_CancelReason.setVisible(false);
		this.row_BounceReason.setVisible(false);
		this.row_BounceRemarks.setVisible(false);
		this.row_RealizationDate.setVisible(false);

		if (StringUtils.equals(status, RepayConstants.PAYSTATUS_BOUNCE)) {

			this.row_BounceReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);

			// readOnlyComponent(isReadOnly("ReceiptDialog_bounceCode"),
			// this.bounceCode);
			// readOnlyComponent(true, this.bounceCharge);
			// readOnlyComponent(isReadOnly("ReceiptDialog_bounceRemarks"),
			// this.bounceRemarks);
			// readOnlyComponent(isReadOnly("ReceiptDialog_bounceDate"),
			// this.bounceDate);

		} else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_CANCEL)) {

			this.row_CancelReason.setVisible(true);
			// readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"),
			// this.cancelReason);

		} else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_REALIZED)) {

			this.row_RealizationDate.setVisible(true);
			// readOnlyComponent(isReadOnly("ReceiptDialog_realizationDate"),
			// this.realizationDate);

		}

		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayments Amount
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnChangeReceipt(Event event)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();
		// this.btnChangeReceipt.setDisabled(true);
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 */
	public void onClick$btnReceipt(Event event) {
		doSave();
	}

	public void doSave() {
		logger.debug("Entering");
		try {
			boolean recReject = false;
			if (this.userAction.getSelectedItem() != null
					&& ("Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
				recReject = true;
			}

			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
			if (!recReject) {

				FinReceiptData data = receiptData;

				FinReceiptHeader rch = data.getReceiptHeader();
				rch.setBankCode(this.bankCode.getValue());
				rch.setDepositDate(this.depositDate.getValue());

				List<FinReceiptDetail> receiptDetails = rch.getReceiptDetails();

				FinReceiptDetail receiptDetail = new FinReceiptDetail();
				receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
				receiptDetail.setReceiptID(Long.valueOf(this.receiptId.getValue()));
				receiptDetail.setReceiptSeqID(receiptSeqId);
				receiptDetail.setFavourNumber(this.favourNo.getValue());
				receiptDetail.setValueDate(this.valueDate.getValue());
				receiptDetail.setBankCode(this.bankCode.getValue());
				receiptDetail.setFavourName(this.favourName.getValue());
				receiptDetail.setDepositDate(this.depositDate.getValue());
				receiptDetail.setDepositNo(this.depositNo.getValue());
				receiptDetail.setPaymentRef(this.paymentRef.getValue());
				receiptDetail.setTransactionRef(this.transactionRef.getValue());
				receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
				receiptDetail.setPaymentType(paymentType);
				receiptDetail.setPaymentTo(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
				receiptDetail.setReceiptType(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
				// receiptDetail.setReceivedDate(this.receivedDate.getValue());

				boolean partnerBankReq = false;
				if (!StringUtils.equals(ReceiptMode.CASH, receiptDetail.getPaymentType())) {
					partnerBankReq = true;
				}
				if (partnerBankReq) {
					Object object = this.fundingAccount.getAttribute("fundingAccID");
					if (object != null) {
						receiptDetail.setFundingAc(Long.valueOf(object.toString()));
						PartnerBank partnerBank = getPartnerBankService()
								.getApprovedPartnerBankById(receiptDetail.getFundingAc());
						if (partnerBank != null) {
							receiptDetail.setPartnerBankAc(partnerBank.getAccountNo());
							receiptDetail.setPartnerBankAcType(partnerBank.getAcType());
						}
					} else {
						receiptDetail.setFundingAc(null);
						receiptDetail.setFundingAcDesc("");
					}
				}
				// ### 30-OCT-2018,Ticket id :124998
				receiptDetail.setStatus(getComboboxValue(receiptModeStatus));

				if (StringUtils.equals(ReceiptMode.CHEQUE, receiptDetail.getPaymentType())
						|| StringUtils.equals(ReceiptMode.DD, receiptDetail.getPaymentType())) {
					receiptData.getReceiptHeader().setTransactionRef(this.favourNo.getValue());
				} else {
					receiptData.getReceiptHeader().setTransactionRef(this.transactionRef.getValue());
				}

				receiptData.getReceiptHeader().setValueDate(this.valueDate.getValue());
				receiptData.getReceiptHeader().setPartnerBankId(receiptDetail.getFundingAc());

				receiptDetails.clear();
				receiptDetails.add(receiptDetail);

			}
			doProcessReceipt();

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		} catch (AppException pfe) {
			MessageUtil.showError(pfe.getMessage());
			return;
		} catch (WrongValuesException we) {
			throw we;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Process Repayment Details
	 */
	private void doProcessReceipt() {
		logger.debug("Entering");
		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
			}
		}

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		if (isWorkFlowEnabled()) {

			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			rch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(rch.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, rch);
				}

				// PSD#178475 -wrong message populates due to whenever nextTaskId is empty ,sets nextRoleCode as
				// FirstTaskOwner.this is not there is receipts .
				/*
				 * if ("".equals(nextTaskId)) { nextRoleCode = getFirstTaskOwner(); } else { String[] nextTasks =
				 * nextTaskId.split(";");
				 * 
				 * if (nextTasks.length > 0) { for (int i = 0; i < nextTasks.length; i++) { if (nextRoleCode.length() >
				 * 1) { nextRoleCode = nextRoleCode.concat(","); } nextRoleCode += getTaskOwner(nextTasks[i]); } } }
				 */

				if (isNotesMandatory(taskId, rch)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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
			rch.setTaskId(taskId);
			rch.setNextTaskId(nextTaskId);
			rch.setRoleCode(getRole());
			rch.setNextRoleCode(nextRoleCode);
			rch.setRcdMaintainSts("R");
			rch.setRecordType(recordType);
			rch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			rch.setUserDetails(getUserWorkspace().getLoggedInUser());
		}

		// Duplicate Creation of Object
		FinReceiptData aReceiptData = ObjectUtil.clone(receiptData);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(rch.getRecordType())) {

				aReceiptData.getReceiptHeader().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aReceiptData.getReceiptHeader().setVersion(1);
				if (aReceiptData.getReceiptHeader().isNewRecord()) {
					aReceiptData.getReceiptHeader().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReceiptData.getReceiptHeader().setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReceiptData.getReceiptHeader().setNewRecord(true);
				}
			}

		} else {
			rch.setVersion(rch.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		aReceiptData.setEventCodeRef(eventCode);

		// save it to database
		try {

			if (doProcess(aReceiptData, tranType)) {

				if (getNonLanReceiptListCtrl() != null) {
					refreshMaintainList();
				}

				if ((StringUtils.equals(module, FinanceConstants.REALIZATION_APPROVER)
						|| StringUtils.equals(module, FinanceConstants.RECEIPT_APPROVER))
						&& PennantConstants.RCD_STATUS_APPROVED.equals(rch.getRecordStatus())) {
					rch.setNextRoleCode("");
					rch.setRoleCode("");
				}

				if (RepayConstants.NONLAN_RECEIPT_CUSTOMER.equals(rch.getRecAgainst())) {
					String msg = PennantApplicationUtil.getSavingStatus(rch.getRoleCode(), rch.getNextRoleCode(),
							String.valueOf(rch.getCustCIF()), " Customer CIF ", rch.getRecordStatus(), false);
					Clients.showNotification(msg, "info", null, null, -1);
				} else {
					String msg = PennantApplicationUtil.getSavingStatus(rch.getRoleCode(), rch.getNextRoleCode(),
							String.valueOf(rch.getReference()), " External Reference ", rch.getRecordStatus(), false);
					Clients.showNotification(msg, "info", null, null, -1);
				}

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						String reference = String.valueOf(rch.getReceiptID());
						if (StringUtils.isNotEmpty(rch.getNextRoleCode())) {
							if (!PennantConstants.RCD_STATUS_CANCELLED.equals(rch.getRecordStatus())) {
								String[] to = rch.getNextRoleCode().split(",");
								String message;

								if (StringUtils.isBlank(rch.getNextTaskId())) {
									message = Labels.getLabel("REC_FINALIZED_MESSAGE");
								} else {
									message = Labels.getLabel("REC_PENDING_MESSAGE");
								}
								message += " with ReceiptId" + ":" + reference;

								// getEventManager().publish(message, to,
								// finDivision, aFinanceMain.getFinBranch());
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 */
	private void doWriteBeanToComponents() {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		old_ReceiptModeStatus = rch.getReceiptModeStatus();
		this.favourName.setValue(rch.getEntityDesc());
		// this.favourNameDesc.setValue(rch.getEntityCode());
		this.receivedFrom.setValue(rch.getReceivedFrom());

		String received = this.receivedFrom.getValue();
		if (received.equalsIgnoreCase(RepayConstants.RECEIVED_CUSTOMER)) {
			this.extReference.setValue(rch.getExtReference());// customer
			this.custCIF.setValue(rch.getCustCIF(), rch.getCustShrtName());
			rch.setReference(String.valueOf(rch.getCustID()));
		} else {
			this.extReference.setValue(rch.getReference());// non lan
			this.label_NonReceiptDialog_CustID.setVisible(false);
			this.hbox_NonReceiptDialog.setVisible(false);
		}
		fillComboBox(this.receiptSource, rch.getReceiptSource(), receiptSourceList, "");
		fillComboBox(this.receiptMode, rch.getReceiptMode(), receiptModeList, "");
		fillComboBox(this.subReceiptMode, rch.getSubReceiptMode(), subReceiptModeList, "");
		this.receiptMode.setDisabled(true);
		appendReceiptMode(rch);

		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rch.getReceiptAmount(), formatter));
		this.remarks.setValue(rch.getRemarks());
		this.receiptDate.setValue(rch.getReceiptDate());
		this.receiptId.setValue(String.valueOf(rch.getReceiptID()));
		this.receiptDate.setDisabled(true);
		this.panNumber.setValue(rch.getPanNumber());
		this.cashierBranch.setValue(rch.getCashierBranch(), rch.getCashierBranchDesc());

		// Receipt Mode Status Details
		if (isReadOnly("ReceiptDialog_receiptModeStatus") || this.receiptModeStatus.getSelectedIndex() > 0) {
			this.label_ReceiptDialog_ReceiptModeStatus.setVisible(true);
			this.hbox_ReceiptModeStatus.setVisible(true);
		}

		setReceiptModeStatus(rch);

		if (rch.getReceiptMode().equals(ReceiptMode.CASH)) {
			this.panNumber.setValue(rch.getPanNumber());
		}

		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		if (CollectionUtils.isNotEmpty(rcdList)) {
			FinReceiptDetail rcd = rcdList.get(0);
			resetModeStatus(rch.getReceiptModeStatus());
			checkByReceiptMode(rch.getReceiptMode(), false);
			this.valueDate.setValue(rch.getValueDate());
			this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rcd.getAmount(), formatter));
			this.favourNo.setValue(rcd.getFavourNumber());
			this.valueDate.setValue(rcd.getValueDate());
			this.bankCode.setValue(rcd.getBankCode());
			this.bankCode.setDescription(rcd.getBankCodeDesc());
			this.favourName.setValue(rcd.getFavourName());
			this.depositDate.setValue(rcd.getDepositDate());
			this.depositNo.setValue(rcd.getDepositNo());
			this.paymentRef.setValue(rcd.getPaymentRef());
			this.transactionRef.setValue(rcd.getTransactionRef());
			this.chequeAcNo.setValue(rcd.getChequeAcNo());
			receiptSeqId = rcd.getReceiptSeqID();
			// this.receivedDate.setValue(rcd.getReceivedDate());

			boolean partnerBankReq = false;
			if (!StringUtils.equals(ReceiptMode.CASH, rcd.getPaymentType())) {
				partnerBankReq = true;
			}

			if (partnerBankReq) {
				this.fundingAccount.setAttribute("fundingAccID", rcd.getFundingAc());
				this.fundingAccount.setValue(rcd.getFundingAcCode(), StringUtils.trimToEmpty(rcd.getFundingAcDesc()));
			}

		}

		if (rch.getCollectionAgentId() == 0) {
			this.collectionAgentId.setValue("");
		} else {
			this.collectionAgentId.setValue(rch.getCollectionAgentCode(), rch.getCollectionAgentDesc());
			this.collectionAgentId.setObject(new BusinessVertical(rch.getCollectionAgentId()));
		}

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

		this.recordStatus.setValue(receiptHeader.getRecordStatus());
		if (receiptPurposeCtg == 2 && (StringUtils.equals(ReceiptMode.CHEQUE, receiptHeader.getReceiptMode())
				|| StringUtils.equals(ReceiptMode.DD, receiptHeader.getReceiptMode()))) {
			this.valueDate.setValue(rch.getValueDate());
			this.valueDate.setReadonly(true);
			this.valueDate.setDisabled(true);
		}

		logger.debug("Leaving");
	}

	private void appendReceiptMode(FinReceiptHeader rch) {
		if (StringUtils.equals(rch.getSubReceiptMode(), PennantConstants.List_Select)
				&& StringUtils.equals(rch.getReceiptChannel(), PennantConstants.List_Select)) {
			receiptTypeLabel.setVisible(false);
			subReceiptMode.setVisible(false);
			receiptChannel.setVisible(false);
			return;
		}

		if ((ReceiptMode.ONLINE.equals(rch.getReceiptMode())) && rch.getSubReceiptMode() != null
				&& !StringUtils.equals(rch.getSubReceiptMode(), PennantConstants.List_Select)) {
			receiptTypeLabel.setVisible(true);
			subReceiptMode.setVisible(true);
			receiptTypeLabel.setValue(Labels.getLabel("label_ReceiptPayment_SubReceiptMode.value"));
			fillComboBox(subReceiptMode, rch.getSubReceiptMode(), subReceiptModeList, "");
			this.subReceiptMode.setDisabled(true);
		} else {
			receiptTypeLabel.setVisible(true);
			receiptChannel.setVisible(true);
			receiptTypeLabel.setValue(Labels.getLabel("label_ReceiptPayment_ReceiptChannel.value"));
			fillComboBox(receiptChannel, rch.getReceiptChannel(), receiptChannelList, "");
			this.receiptChannel.setDisabled(true);
		}
	}

	public void onDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("details",
				receiptData.getReceiptHeader().getAllocationsSummary().get(Integer.parseInt(buttonId)).getSubList());
		map.put("buttonId", buttonId);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/BounceDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date fromDate = rch.getValueDate();
		Date toDate = SysParamUtil.getAppDate();
		if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			fromDate = rch.getReceiptDate();
		}

		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModes(),
					Labels.getLabel("label_ReceiptDialog_ReceiptMode.value")));
		}

		if (this.row_RealizationDate.isVisible() && !this.realizationDate.isDisabled()) {
			this.realizationDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_ReceiptRealizationDialog_RealizationDate.value"), true,
							fromDate, toDate, true));
		}

		if (StringUtils.equals(recptMode, ReceiptMode.CHEQUE)) {

			if (!this.chequeAcNo.isReadonly()) {
				this.chequeAcNo.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptDialog_ChequeAccountNo.value"), null, false));
			}
		}

		if (StringUtils.equals(module, FinanceConstants.DEPOSIT_MAKER)
				&& (StringUtils.equals(recptMode, ReceiptMode.CHEQUE)
						|| StringUtils.equals(recptMode, ReceiptMode.DD))) {
			if (!this.fundingAccount.isReadonly()) {
				this.fundingAccount.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true));
			}
		} else if (StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER)
				&& (!StringUtils.equals(recptMode, ReceiptMode.CHEQUE) && !StringUtils.equals(recptMode, ReceiptMode.DD)
						&& !StringUtils.equals(recptMode, ReceiptMode.CASH))) {
			if (!this.fundingAccount.isReadonly()) {
				this.fundingAccount.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true));
			}
		}

		if (!this.collectionAgentId.isReadonly() && this.collectionAgentId.isVisible()) {
			this.collectionAgentId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_CollectionAgentId.value"),
							null, collectionAgentId.isMandatory(), true));
		}
		if (StringUtils.equals(recptMode, ReceiptMode.DD) || StringUtils.equals(recptMode, ReceiptMode.CHEQUE)) {

			if (!this.favourNo.isReadonly()) {
				String label = Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value");
				if (StringUtils.equals(recptMode, ReceiptMode.DD)) {
					label = Labels.getLabel("label_ReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(
						new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}

			if (!this.valueDate.isDisabled()) {
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ValueDate.value"),
						true, this.receiptDate.getValue(), SysParamUtil.getAppDate(), true));
			}

			if (!this.bankCode.isReadonly()) {
				this.bankCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptDialog_IssuingBank.value"), null, true, true));
			}

			if (!this.favourName.isReadonly()) {
				this.favourName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_favourName.value"), null, true));
			}

			if (!this.depositDate.isReadonly()) {
				this.depositDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_DepositDate.value"),
								true, this.receiptDate.getValue(), SysParamUtil.getAppDate(), true));
			}

			if (!this.depositNo.isReadonly()) {
				this.depositNo
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_depositNo.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		if (StringUtils.equals(recptMode, ReceiptMode.ONLINE)) {

			if (!this.transactionRef.isReadonly()) {
				this.transactionRef
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_tranReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
		}

		if (!StringUtils.equals(recptMode, ReceiptMode.EXCESS)) {
			if (!this.paymentRef.isReadonly()) {
				this.paymentRef.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_paymentReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		if (this.row_BounceReason.isVisible() && !this.bounceCode.isReadonly()) {
			this.bounceCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReceiptDialog_BounceReason.value"), null, true, true));
		}

		if (this.bounceDate.isVisible() && !this.bounceDate.isReadonly()) {
			this.bounceDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_BounceDate.value"),
					true, fromDate, toDate, true));
		}

		if (this.row_CancelReason.isVisible() && !this.cancelReason.isReadonly()) {
			this.cancelReason.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReceiptDialog_CancelReason.value"), null, true, true));
		}

		if (this.row_BounceRemarks.isVisible() && !this.bounceRemarks.isReadonly()) {
			this.bounceRemarks
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_BounceRemarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (!this.panNumber.isReadonly()) {
			this.panNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_PanNumber.value"),
					PennantRegularExpressions.REGEX_PANNUMBER, isPanMandatory));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.receiptMode.setConstraint("");
		this.realizationDate.setConstraint("");
		this.bounceCode.setConstraint("");
		this.bounceRemarks.setConstraint("");
		this.cancelReason.setConstraint("");
		this.bounceDate.setConstraint("");

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
		this.panNumber.setConstraint("");
		this.collectionAgentId.setConstraint("");
		// this.receivedDate.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.receiptMode.setErrorMessage("");
		this.realizationDate.setErrorMessage("");
		this.bounceCode.setErrorMessage("");
		this.bounceRemarks.setErrorMessage("");
		this.bounceDate.setErrorMessage("");
		this.cancelReason.setErrorMessage("");

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
		// this.receivedDate.setErrorMessage("");
		this.remarks.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * 
	 * @return
	 */
	private void doWriteComponentsToBean() {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING

		ArrayList<WrongValueException> wve = new ArrayList<>();
		Date curBussDate = SysParamUtil.getAppDate();
		FinReceiptHeader header = receiptData.getReceiptHeader();
		header.setReceiptDate(curBussDate);
		String received = this.receivedFrom.getValue();
		String rcptchannnel = this.receiptChannel.getValue();

		header.setReceiptPurpose(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		header.setReceiptType(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		header.setAllocationType(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		if (received.equalsIgnoreCase(RepayConstants.RECEIVED_CUSTOMER)) {
			header.setRecAgainst(RepayConstants.NONLAN_RECEIPT_CUSTOMER);

		} else {
			header.setRecAgainst(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		}

		try {
			header.setEntityCode(header.getEntityCode());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setReceiptSource(getComboboxValue(this.receiptSource));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setReceiptAmount(PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(),
					PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BusinessVertical businessVertical = (BusinessVertical) this.collectionAgentId.getObject();
			if (rcptchannnel.equals(RECEIPT_CHANNEL_MOBILE)
					&& ((received.equalsIgnoreCase(RepayConstants.RECEIVED_CUSTOMER))
							|| (received.equalsIgnoreCase(RepayConstants.RECEIVED_NONLOAN)))) {
				if (businessVertical.getCode() == null) {
					header.setReference(this.collectionAgentId.getValue());
				} else {
					header.setReference(businessVertical.getCode());
				}
			} else if (received.equalsIgnoreCase(RepayConstants.RECEIVED_CUSTOMER)
					&& !receiptChannel.equals(RECEIPT_CHANNEL_MOBILE)) {
				header.setReference(String.valueOf(header.getCustID()));
			} else {
				header.setReference(this.extReference.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
			if ("#".equals(getComboboxValue(this.subReceiptMode))) {
				header.setSubReceiptMode(header.getReceiptMode());
				paymentType = header.getReceiptMode();
			} else {
				header.setSubReceiptMode(getComboboxValue(this.subReceiptMode));
				paymentType = header.getSubReceiptMode();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setReceiptDate(this.receiptDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		String status = "";
		try {
			if (!isReadOnly("ReceiptDialog_receiptModeStatus") && row_ReceiptModeStatus.isVisible()
					&& isValidComboValue(this.receiptModeStatus,
							Labels.getLabel("label_ReceiptDialog_ReceiptModeStatus.value"))) {
				status = getComboboxValue(receiptModeStatus);
				// Validation for if Receipt has already realized
				if (status.equals(old_ReceiptModeStatus) && PennantConstants.RCD_STATUS_APPROVED
						.equals(receiptData.getReceiptHeader().getRecordStatus())) {
					throw new WrongValueException(this.receiptModeStatus,
							"Receipt has already been 'Realized', Please Select other options ");
				}
				header.setReceiptModeStatus(status);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (StringUtils.equals(status, RepayConstants.PAYSTATUS_BOUNCE)) {

			try {
				header.setBounceDate(this.bounceDate.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}

			// Bounce Details capturing
			ManualAdvise bounce = header.getManualAdvise();
			if (bounce == null) {
				bounce = new ManualAdvise();
				bounce.setNewRecord(true);
			}

			bounce.setAdviseType(AdviseType.RECEIVABLE.id());
			bounce.setFinReference(header.getReference());
			bounce.setFeeTypeID(0);
			bounce.setSequence(0);
			bounce.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			bounce.setFinSource(UploadConstants.FINSOURCE_ID_PFF);

			try {
				bounce.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.bounceCharge.getActualValue(),
						CurrencyUtil.getFormat(header.getFinCcy())));
			} catch (WrongValueException e) {
				wve.add(e);
			}

			bounce.setPaidAmount(BigDecimal.ZERO);
			bounce.setWaivedAmount(BigDecimal.ZERO);
			bounce.setValueDate(curBussDate);
			bounce.setPostDate(SysParamUtil.getPostDate());

			try {
				bounce.setRemarks(this.bounceRemarks.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
			bounce.setReceiptID(header.getReceiptID());
			try {
				bounce.setBounceID(Long.valueOf(this.bounceCode.getValue()));
			} catch (WrongValueException e) {
				wve.add(e);
			}

			header.setManualAdvise(bounce);
		} else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_CANCEL)) {

			try {
				header.setCancelReason(this.cancelReason.getValue());
				header.setCancelRemarks(this.cancelRemarks.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}

		} else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_REALIZED)) {

			try {
				header.setRealizationDate(this.realizationDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
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
			header.setCashierBranch(this.cashierBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.fundingAccount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setExtReference(this.extReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotEmpty(this.panNumber.getValue())) {
				header.setPanNumber(this.panNumber.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			BusinessVertical businessVertical = (BusinessVertical) this.collectionAgentId.getObject();
			if (businessVertical != null && businessVertical.getId() != 0) {
				header.setCollectionAgentId(businessVertical.getId());
			} else {
				header.setCollectionAgentId(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	private String getServiceTasks(String taskId, FinReceiptHeader rch, String finishedTasks) {
		logger.debug("Entering");
		String serviceTasks = getServiceOperations(taskId, rch);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinReceiptHeader receiptHeader) {
		logger.debug("Entering");
		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(receiptHeader.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, receiptHeader);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
				}
			}
		}

		receiptHeader.setTaskId(taskId);
		receiptHeader.setNextTaskId(nextTaskId);
		receiptHeader.setRoleCode(getRole());
		receiptHeader.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(FinReceiptData aReceiptData, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinReceiptHeader rch = aReceiptData.getReceiptHeader();

		rch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setUserDetails(getUserWorkspace().getLoggedInUser());
		rch.setWorkflowId(getWorkFlowId());

		rch.setUserDetails(getUserWorkspace().getLoggedInUser());
		aReceiptData.setReceiptHeader(rch);

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			rch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, rch, finishedTasks);

			if (isNotesMandatory(taskId, rch)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aReceiptData, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				FinReceiptData tReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
				setNextTaskDetails(taskId, tReceiptData.getReceiptHeader());
				auditHeader.getAuditDetail().setModelData(tReceiptData);
				processCompleted = doSaveProcess(auditHeader, method);

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				serviceTasks = getServiceTasks(taskId, rch, finishedTasks);

			}

			FinReceiptData tRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, rch);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tRepayData.getReceiptHeader());
					auditHeader.getAuditDetail().setModelData(tRepayData);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aReceiptData, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
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

		FinReceiptData aRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = aRepayData.getReceiptHeader();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {

					auditHeader = getNonLanReceiptService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

						if (rch.isNewRecord()) {
							((FinReceiptData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail()
									.setDirectFinalApprove(true);
						}

						auditHeader = getNonLanReceiptService().doApprove(auditHeader);

						if (rch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getNonLanReceiptService().doReject(auditHeader);
						if (rch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReversal)) {
						auditHeader = getReceiptService().doReversal(auditHeader);
						if (rch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_NonLanReceiptDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_NonLanReceiptDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail,
				repayData.getReceiptHeader().getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(receiptData.getReceiptHeader());
	}

	protected void refreshMaintainList() {
		getNonLanReceiptListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(receiptData.getReceiptHeader().getReceiptID());
	}

	/**
	 * onChanging fundingAccount details
	 * 
	 * @param event
	 */
	public void onFulfill$fundingAccount(Event event) {
		logger.debug("Entering");
		this.fundingAccount.clearErrorMessage();
		Clients.clearWrongValue(this.fundingAccount);

		long partnerBankID = 0;
		PartnerBankModes partnerBankModes = null;
		Object dataObject = this.fundingAccount.getObject();

		if (dataObject != null) {
			if (dataObject instanceof PartnerBankModes) {
				partnerBankModes = (PartnerBankModes) dataObject;
				partnerBankID = partnerBankModes.getPartnerBankId();
			}
		}
		this.fundingAccount.setAttribute("fundingAccID", partnerBankID);

		logger.debug("Leaving");
	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setRoleCode(getRole());
		logger.debug("Leaving ");
		return notes;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	protected void doStoreServiceIds(FinReceiptHeader finReceiptHeader) {
		this.curRoleCode = finReceiptHeader.getRoleCode();
		this.curNextRoleCode = finReceiptHeader.getNextRoleCode();
		this.curTaskId = finReceiptHeader.getTaskId();
		this.curNextTaskId = finReceiptHeader.getNextTaskId();
		// this.curNextUserId = finReceiptHeader.getNextUserId();
	}

	public PartnerBankService getPartnerBankService() {
		return partnerBankService;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public FinReceiptData getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(FinReceiptData receiptData) {
		this.receiptData = receiptData;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public ReceiptCancellationService getReceiptCancellationService() {
		return receiptCancellationService;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public NonLanReceiptListCtrl getNonLanReceiptListCtrl() {
		return nonLanReceiptListCtrl;
	}

	public void setNonLanReceiptListCtrl(NonLanReceiptListCtrl nonLanReceiptListCtrl) {
		this.nonLanReceiptListCtrl = nonLanReceiptListCtrl;
	}

	public NonLanReceiptService getNonLanReceiptService() {
		return nonLanReceiptService;
	}

	public void setNonLanReceiptService(NonLanReceiptService nonLanReceiptService) {
		this.nonLanReceiptService = nonLanReceiptService;
	}

	public long getReceiptSeqId() {
		return receiptSeqId;
	}

	public void setReceiptSeqId(long receiptSeqId) {
		this.receiptSeqId = receiptSeqId;
	}

}