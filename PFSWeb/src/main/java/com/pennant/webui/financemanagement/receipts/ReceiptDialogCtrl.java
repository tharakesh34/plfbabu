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
package com.pennant.webui.financemanagement.receipts;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.ChartType;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SanctionBasedSchedule;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.MasterDef;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.reports.ReceiptReport;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.document.DocVerificationUtil;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.util.AgreementEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainListCtrl;
import com.pennant.webui.finance.financemain.ManualScheduleDialogCtrl;
import com.pennant.webui.finance.financemain.StageAccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.financemanagement.paymentMode.ReceiptListCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.util.ReceiptUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul
 */
public class ReceiptDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(ReceiptDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReceiptDialog;
	protected Borderlayout borderlayout_Receipt;
	protected Label windowTitle;

	// Loan Summary Details
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox finType;

	protected Decimalbox priBal;
	protected Decimalbox pftBal;
	protected Decimalbox priDue;
	protected Decimalbox pftDue;
	protected Decimalbox bounceDueAmount;
	protected Decimalbox otnerChargeDue;
	protected Textbox finCcy;
	protected Decimalbox paidByCustomer;

	protected Button btnSearchCustCIF;
	protected Button btnSearchFinreference;
	protected Button btnPrintSchedule;

	// Receipt Details
	protected Groupbox gb_ReceiptDetails;
	protected Textbox receiptId;
	protected Combobox receiptPurpose;
	protected Combobox receiptMode;
	protected Label receiptTypeLabel;
	protected Label label_ReceiptDialog_PartnerBankCode;
	protected Label label_ReceiptDialog_CollectionAgentId;
	protected Combobox receiptChannel;
	protected Combobox subReceiptMode;
	protected Datebox receiptDate;
	protected Datebox receivedDate;
	protected CurrencyBox receiptAmount;
	protected CurrencyBox tDSAmount;
	protected Combobox excessAdjustTo;
	protected Decimalbox remBalAfterAllocation;
	protected Decimalbox custPaid;
	protected Label label_ReceiptDialog_ReceiptModeStatus;
	protected Hbox hbox_ReceiptModeStatus;
	protected Combobox receiptModeStatus;
	protected Row row_RealizationDate;
	protected Row row_closuretype;
	protected Datebox realizationDate;
	protected Row row_BounceReason;
	protected Row row_knockOffRef;
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
	protected Hbox hbox_ReceiptDialog_RealizationDate;

	protected Textbox loanClosure_custCIF;
	protected ExtendedCombobox loanClosure_finReference;
	protected Combobox loanClosure_knockOffFrom;
	protected ExtendedCombobox loanClosure_refId;
	protected Datebox LoanClosure_receiptDate;
	protected Datebox LoanClosure_intTillDate;

	// Transaction Details
	protected Combobox receivedFrom;
	protected Uppercasebox panNumber;
	protected Combobox allocationMethod;
	protected Combobox TransreceiptChannel;
	protected ExtendedCombobox fundingAccount;
	protected ExtendedCombobox collectionAgentId;
	protected Uppercasebox externalRefrenceNumber;
	protected Textbox remarks;

	protected Label scheduleLabel;
	protected Combobox effScheduleMethod;

	// Instrument Details
	protected Groupbox gb_InstrumentDetails;
	protected Uppercasebox favourNo;
	protected Datebox valueDate;
	protected ExtendedCombobox bankCode;
	protected Textbox favourName;
	protected Datebox depositDate;
	protected Uppercasebox depositNo;
	protected Uppercasebox transactionRef;
	protected Row row_CustomerAccount;
	protected ExtendedCombobox customerBankAcct;
	protected AccountSelectionBox chequeAcNo;
	protected Uppercasebox paymentRef;
	// protected Datebox receivedDate;
	protected ExtendedCombobox postBranch;
	protected ExtendedCombobox cashierBranch;
	protected ExtendedCombobox finDivision;
	protected Combobox sourceofFund;

	// Payable Details
	protected Groupbox gb_Payable;
	protected Listbox listBoxExcess;

	// Knockoff Details
	protected Groupbox gb_KnockOffDetails;
	protected Combobox knockOffPurpose;
	protected Combobox knockOffFrom;
	protected Textbox knockoffReferenec;
	protected Datebox knockOffReceiptDate;
	protected CurrencyBox knockOffAmount;
	protected Combobox knockOffAllocMthd;
	protected Textbox knockOffRemark;
	protected Combobox KnockEffectScheduleMthd;
	protected Hbox hbox_KnockEffectScheduleMthd;

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
	protected Row row_knockOff_Type;
	// Receipt Due Details
	protected Listbox listBoxPastdues;
	protected Listbox listBoxSchedule;

	protected Listheader listheader_ScheduleEndBal;
	protected Listheader listheader_ReceiptSchedule_SchFee;

	// Overdraft Details Headers
	protected Listheader listheader_LimitChange;
	protected Listheader listheader_AvailableLimit;
	protected Listheader listheader_ODLimit;
	protected Listheader listheader_ReceiptDialog_TDS;
	protected Listheader listheader_ReceiptDialog_PaidTDS;

	// Effective Schedule Tab Details
	protected Label finSchType;
	protected Label finSchCcy;
	protected Label finSchMethod;
	protected Label finSchProfitDaysBasis;
	protected Label finSchReference;
	protected Label finSchGracePeriodEndDate;
	protected Label effectiveRateOfReturn;

	// Hybrid Changes
	protected Label label_FinGracePeriodEndDate;
	protected Label label_ReceiptDialog_RealizationDate;

	// Buttons
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab receiptDetailsTab;
	protected Tab effectiveScheduleTab;
	protected Button btnPrint;
	protected Button btnReceipt;
	protected Button btnChangeReceipt;
	protected Button btnCalcReceipts;
	// Auto Knock Off Details
	protected Textbox knockOffType;
	protected Space space_transactionRef;

	private RuleService ruleService;
	private CustomerDetailsService customerDetailsService;
	private ReceiptService receiptService;
	private ReceiptCancellationService receiptCancellationService;
	private FinanceDetailService financeDetailService;
	private AccountEngineExecution engineExecution;
	private CommitmentService commitmentService;
	private ReceiptCalculator receiptCalculator;
	private AccrualService accrualService;
	private PartnerBankService partnerBankService;
	private NotificationService notificationService;
	private FeeWaiverHeaderService feeWaiverHeaderService;
	private AccountingDetailDialogCtrl accountingDetailDialogCtrl = null;
	private DocumentDetailDialogCtrl documentDetailDialogCtrl = null;
	private AgreementDetailDialogCtrl agreementDetailDialogCtrl = null;
	private CustomerDialogCtrl customerDialogCtrl = null;
	private StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl = null;
	private FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl = null;
	protected FinanceMainListCtrl financeMainListCtrl = null; // over handed per
																// parameters
	protected ReceiptListCtrl receiptListCtrl = null; // over handed per
														// parameters
	protected ExtendedFieldCtrl extendedFieldCtrl = null;

	private FinReceiptData receiptData = null;
	private FinReceiptData orgReceiptData = null;
	private FinanceDetail financeDetail;
	private FinScheduleData finSchedData = null;
	private FinScheduleData befFinSchedData = null;
	private Map<String, BigDecimal> taxPercMap = null;

	private String recordType = "";
	private FinReceiptHeader befImage;
	private List<ChartDetail> chartDetailList = new ArrayList<ChartDetail>();
	private List<FinanceScheduleDetail> orgScheduleList = new ArrayList<>();

	private List<FinReceiptDetail> recDtls = new ArrayList<>();

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
	private String amountFormat = null;
	private int receiptPurposeCtg = -1;
	private boolean dateChange = true;

	protected boolean recSave = false;
	protected Component checkListChildWindow = null;
	protected boolean isEnquiry = false;
	protected Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();

	private boolean isPanMandatory = false;
	private boolean isKnockOff = false;
	private boolean isForeClosure = false;
	private boolean isClosrMaturedLAN = false;
	private boolean isPartPayment = false;
	private boolean isEarlySettle = false;
	private Space panSpace;

	// In Closure Maker auto generated SOA and Foreclosure Reports
	protected A generateSOA;
	protected A foreClosureLetter;
	protected Groupbox gb_ForeClosureSOA;

	private static final String RECEIPT_PREFIX = "Receipt";
	private static final String RECEIPT_TEMPLATE = RECEIPT_PREFIX + PennantConstants.DOC_TYPE_WORD_EXT;
	private static final String TEMPLATE_PATH = App.getResourcePath(PathUtil.FINANCE_AGREEMENTS, "Receipts");

	private boolean isLinkedBtnClick = false;

	// For EarlySettlement Reason functionality
	private ExtendedCombobox earlySettlementReason;
	ReasonCode reasonCodeData;

	private FeeCalculator feeCalculator;
	private List<ValueLabel> sourceofFundList = PennantAppUtil.getFieldCodeList("SOURCE");
	private FeeTypeDAO feeTypeDAO;

	private RepaymentProcessUtil repaymentProcessUtil;
	private boolean isAccountingExecuted = false;
	private boolean isWIF = false;
	private transient SOAReportGenerationService soaReportGenerationService;
	private LinkedFinancesService linkedFinancesService;
	// ClosureType
	protected Combobox closureType;
	private String moduleDefiner = "";
	private boolean isClosureTypeMandatory = false;
	private transient ManualScheduleDialogCtrl manualScheduleDialogCtrl = null;

	private JointAccountDetailService jointAccountDetailService;
	private FinanceMainService financeMainService;
	Date appDate = SysParamUtil.getAppDate();
	private transient CustomerDocumentService customerDocumentService;
	private ManualAdviseService manualAdviseService;
	private FinTypePartnerBankService finTypePartnerBankService;
	private ClusterService clusterService;

	private boolean isPANVerified = true;

	/**
	 * default constructor.<br>
	 */
	public ReceiptDialogCtrl() {
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
	public void onCreate$window_ReceiptDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ReceiptDialog);
		FinReceiptData receiptData = new FinReceiptData();
		FinanceMain financeMain = null;
		FinReceiptHeader rch = new FinReceiptHeader();

		try {
			if (arguments.containsKey("receiptData")) {
				setReceiptData((FinReceiptData) arguments.get("receiptData"));
				receiptData = getReceiptData();
				isClosrMaturedLAN = receiptData.isClosrMaturedLAN();

				rch = receiptData.getReceiptHeader();

				financeDetail = receiptData.getFinanceDetail();
				financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				setFinanceDetail(financeDetail);

				formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
				amountFormat = PennantApplicationUtil.getAmountFormate(formatter);

				recordType = rch.getRecordType();

				befImage = ObjectUtil.clone(rch);
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
			if (arguments.containsKey("isKnockOff")) {
				isKnockOff = (boolean) arguments.get("isKnockOff");
			}
			if (arguments.containsKey("isForeClosure")) {
				isForeClosure = (boolean) arguments.get("isForeClosure");
			}

			if (arguments.containsKey("isPartPayment")) {
				isPartPayment = (boolean) arguments.get("isPartPayment");
			}

			if (arguments.containsKey("receiptListCtrl")) {
				setReceiptListCtrl((ReceiptListCtrl) arguments.get("receiptListCtrl"));
			}
			if (arguments.containsKey("isWIF")) {
				isWIF = (Boolean) arguments.get("isWIF");
			}

			doLoadWorkFlow(rch.isWorkflow(), rch.getWorkflowId(), rch.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				String recStatus = StringUtils.trimToEmpty(rch.getRecordStatus());
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

			checkAndSetModDef(module);
			// set Field Properties
			doSetFieldProperties();
			doStoreServiceIds(rch);

			// READ OVERHANDED parameters !
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();

			boolean applySanctionCheck = SanctionBasedSchedule.isApplySanctionBasedSchedule(fsd);
			fsd.getFinanceMain().setApplySanctionCheck(applySanctionCheck);

			FinanceProfitDetail finPftDeatils = fsd.getFinPftDeatil();
			finPftDeatils = accrualService.calProfitDetails(financeMain, fsd.getFinanceScheduleDetails(), finPftDeatils,
					receiptData.getReceiptHeader().getReceiptDate());

			// set if new receord
			if (StringUtils.isBlank(rch.getRecordType())) {
				if (StringUtils.isBlank(receiptData.getReceiptHeader().getAllocationType())) {
					receiptData.getReceiptHeader().setAllocationType("A");
				}
				if (StringUtils.isBlank(receiptData.getReceiptHeader().getExcessAdjustTo())) {
					receiptData.getReceiptHeader().setExcessAdjustTo("E");
				}
			}

			// receiptData =
			// getReceiptCalculator().removeUnwantedManAloc(receiptData);

			if (!(FinanceConstants.CLOSURE_APPROVER.equals(module) || FinanceConstants.CLOSURE_MAKER.equals(module))
					&& FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())
					&& RepayConstants.EXCESSADJUSTTO_TEXCESS.equals(receiptData.getExcessType())) {
				doProcessTerminationExcess(receiptData, rch);
			}

			setSummaryData(false);
			// set Read only mode accordingly if the object is new or not.
			if (StringUtils.isBlank(rch.getRecordType())) {
				doEdit();
				this.btnReceipt.setDisabled(true);
			}

			if (ImplementationConstants.AUTO_WAIVER_REQUIRED_FROMSCREEN) {
				BigDecimal closureAmount = receiptData.getCalculatedClosureAmt();
				if (closureAmount.compareTo(rch.getReceiptAmount().add(receiptData.getExcessAvailable())) > 0
						&& FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
					receiptService.waiveThresholdLimit(receiptData);
				}
			}

			doShowDialog(rch);

			// set default data for closed loans
			setClosedLoanDetails(rch.getFinID());

			// Setting tile Name based on Service Action
			this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
			this.listBoxSchedule.setHeight(getListBoxHeight(6));
			this.receiptDetailsTab.setSelected(true);
			if (receiptData.isCalReq()) {
				this.btnCalcReceipts.setDisabled(false);
			} else {
				this.btnCalcReceipts.setDisabled(true);
				this.btnCalcReceipts.setVisible(false);
			}
			this.windowTitle.setValue(Labels.getLabel(module + "_Window.Title"));
			setDialog(DialogType.EMBEDDED);
			if (receiptPurposeCtg == 2) {
				this.excessAdjustTo.setDisabled(true);
				fillComboBox(allocationMethod, "A", PennantStaticListUtil.getAllocationMethods(), ",M,");
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
				this.allocationMethod.setDisabled(true);
			}

			if (financeMain.isUnderSettlement()) {
				this.excessAdjustTo.setDisabled(true);
				fillComboBox(allocationMethod, "N", PennantStaticListUtil.getAllocationMethods());
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
				this.allocationMethod.setDisabled(true);
			}

			if (isForeClosure || isEarlySettle) {
				this.gb_Payable.setVisible(true);
			}
			if (isForeClosure || isKnockOff) {
				receiptData.getReceiptHeader().setDedupCheckRequired(false);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReceiptDialog.onClose();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void checkAndSetModDef(String module) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(module)) {
			if ("RECEIPT_MAKER".equals(module)) {
				moduleDefiner = FinServiceEvent.RECEIPT;
			} else if ("REALIZATION_MAKER".equals(module)) {
				moduleDefiner = FinServiceEvent.REALIZATION;
			} else if ("REALIZATION_APPROVER".equals(module)) {
				String mode = getReceiptData().getReceiptHeader().getReceiptModeStatus();
				if (StringUtils.equals(mode, RepayConstants.PAYSTATUS_INITIATED)) {
					moduleDefiner = FinServiceEvent.RECEIPT;
				} else if (StringUtils.equals(mode, RepayConstants.PAYSTATUS_BOUNCE)
						|| StringUtils.equals(mode, RepayConstants.PAYSTATUS_CANCEL)
						|| StringUtils.equals(mode, RepayConstants.PAYSTATUS_REALIZED)) {
					moduleDefiner = FinServiceEvent.REALIZATION;
				}
			} else if ("DEPOSIT_MAKER".equals(module) || "DEPOSIT_APPROVER".equals(module)) {
				moduleDefiner = FinServiceEvent.RECEIPT;
			} else if ("RECEIPTKNOCKOFF_MAKER".equals(module) || "RECEIPTKNOCKOFF_APPROVER".equals(module)) {
				moduleDefiner = FinServiceEvent.RECEIPTKNOCKOFF;
			} else if ("RECEIPTKNOCKOFFCANCEL_MAKER".equals(module)
					|| "RECEIPTKNOCKOFFCANCEL_APPROVER".equals(module)) {
				moduleDefiner = FinServiceEvent.RECEIPTKNOCKOFF_CAN;
			} else if ("RECEIPTCLOSURE_MAKER".equals(module) || "RECEIPTCLOSURE_APPROVER".equals(module)) {
				moduleDefiner = FinServiceEvent.RECEIPTFORECLOSURE;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Showing Customer details on Clicking Customer View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		CustomerDetails customerDetails = getCustomerDetailsService()
				.getCustomerById(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		String pageName = PennantAppUtil.getCustomerPageName();
		map.put("customerDetails", customerDetails);
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");
		Executions.createComponents(pageName, null, map);

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Showing Finance details on Clicking Finance View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		// Preparation of Finance Enquiry Data
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		FinanceEnquiry finenq = new FinanceEnquiry();
		finenq.setFinID(fm.getFinID());
		finenq.setFinReference(fm.getFinReference());
		finenq.setFinType(fm.getFinType());
		finenq.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		finenq.setFinCcy(fm.getFinCcy());
		finenq.setScheduleMethod(fm.getScheduleMethod());
		finenq.setProfitDaysBasis(fm.getProfitDaysBasis());
		finenq.setFinBranch(fm.getFinBranch());
		finenq.setLovDescFinBranchName(fm.getLovDescFinBranchName());
		finenq.setLovDescCustCIF(fm.getCustCIF());
		finenq.setFinIsActive(fm.isFinIsActive());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", finenq);
		map.put("ReceiptDialog", this);
		map.put("isModelWindow", true);
		map.put("enquiryType", "FINENQ");
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.window_ReceiptDialog, map);

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectAccountTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, tab, "onSelectAccountTab");

		appendAccountingDetailTab(false);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
		}
	}

	protected void appendExtendedFieldDetails(FinanceDetail aFinanceDetail, String finEvent) {
		logger.debug(Literal.ENTERING);

		try {
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			if (aFinanceMain == null) {
				return;
			}
			if (finEvent.isEmpty()) {
				finEvent = FinServiceEvent.ORG;
			}

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, aFinanceMain.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setExtendedFieldExtnt(true);
			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());

			FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

			ExtendedFieldExtension extendedFieldExtension = null;
			String receiptModeStatus = this.receiptModeStatus.getSelectedItem().getValue();

			Boolean newRecord = (PennantConstants.RCD_STATUS_APPROVED.equals(receiptHeader.getRecordStatus())
					|| receiptHeader.getRecordStatus() == null) ? true : false;

			if (FinServiceEvent.RECEIPTKNOCKOFF_CAN.equals(finEvent)
					&& RepayConstants.PAYSTATUS_CANCEL.equals(receiptHeader.getReceiptModeStatus())) {
				newRecord = false;
			}

			if (FinServiceEvent.REALIZATION.equals(finEvent)) {
				if (ReceiptMode.CHEQUE.equals(receiptHeader.getReceiptMode())
						|| ReceiptMode.DD.equals(receiptHeader.getReceiptMode())) {
					if (RepayConstants.PAYSTATUS_REALIZED.equals(receiptHeader.getReceiptModeStatus())
							&& RepayConstants.PAYSTATUS_REALIZED.equals(receiptModeStatus)) {
						newRecord = false;
					}
				}
			}

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
			aFinanceDetail.setExtendedFieldHeader(extendedFieldHeader);
			aFinanceDetail.setExtendedFieldRender(extendedFieldRender);
			aFinanceDetail.setExtendedFieldExtension(extendedFieldExtension);

			if (aFinanceDetail.getBefImage() != null) {
				aFinanceDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aFinanceDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
				aFinanceDetail.getBefImage().setExtendedFieldExtension(extendedFieldExtension);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(window_ReceiptDialog);
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

	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		boolean createTab = false;

		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}

		if (onLoadProcess) {
			return;
		}

		Long acSetID = Long.MIN_VALUE;
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		String purpose = getComboboxValue(receiptPurpose);
		if (StringUtils.equals(purpose, FinServiceEvent.EARLYSETTLE)) {
			acSetID = AccountingEngine.getAccountSetID(finMain, AccountingEvent.EARLYSTL);
		} else if (StringUtils.equals(purpose, FinServiceEvent.EARLYRPY)) {
			acSetID = AccountingEngine.getAccountSetID(finMain, AccountingEvent.EARLYPAY);
		} else {
			acSetID = AccountingEngine.getAccountSetID(finMain, AccountingEvent.REPAY);
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("dialogCtrl", this);
		map.put("financeDetail", financeDetail);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("acSetID", acSetID);
		map.put("DisableZeroCal", false);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);

		Tab tab = null;
		if (tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
			tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
			tab.setVisible(true);
		}

	}

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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole(), menuItemRightName);

		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));
		this.gb_ForeClosureSOA.setVisible(getUserWorkspace().isAllowed("gb_ReceiptDialog_ForeClosure"));
		logger.debug(Literal.LEAVING);
	}

	//// FIXME: PV. Its should be deleted. closed status is already fetched at
	//// the time of loading. Else include closed status field along with main
	//// data fetching
	/**
	 * ticket id:124998,checking closed loans and setting default data
	 * 
	 * @param finReference
	 */
	private void setClosedLoanDetails(long finID) {
		FinanceMain fm = receiptService.getClosingStatus(finID, TableType.MAIN_TAB, false);
		String closingSts = StringUtils.trimToNull(fm.getClosingStatus());
		if (closingSts != null && !FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())
				&& !fm.isWriteoffLoan()) {
			fillComboBox(this.receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,EarlySettlement,EarlyPayment,");

			Set<String> exclude = new HashSet<>();
			exclude.add("A");
			if (!fm.isUnderSettlement()) {
				exclude.add("S");
			}

			List<ValueLabel> excessAdjustmentTypes = PennantStaticListUtil.getExcessAdjustmentTypes();

			fillComboBox(this.excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS,
					excludeComboBox(excessAdjustmentTypes, exclude));
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Receipts Details
		this.priBal.setFormat(amountFormat);
		this.pftBal.setFormat(amountFormat);
		this.priDue.setFormat(amountFormat);
		this.pftDue.setFormat(amountFormat);
		this.bounceDueAmount.setFormat(amountFormat);
		this.otnerChargeDue.setFormat(amountFormat);
		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.paidByCustomer.setFormat(amountFormat);
		this.receiptAmount.setProperties(true, formatter);
		this.tDSAmount.setProperties(false, formatter);
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
		this.bounceCode.setValueColumn("BounceCode");
		// this.bounceCode.setValueType(DataType.LONG);
		this.bounceCode.setDescColumn("Reason");
		// this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceCode", "Lovdesccategory", "Reason" });
		this.bounceCharge.setProperties(false, formatter);
		this.bounceRemarks.setMaxlength(100);
		this.bounceDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.bounceCode
				.setFilters(new Filter[] { new Filter("InstrumentType", InstrumentType.PDC.code(), Filter.OP_EQUAL) });

		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setValueColumn("PartnerBankCode");
		this.fundingAccount.setDescColumn("PartnerBankName");
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankCode" });

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
		this.externalRefrenceNumber.setMaxlength(20);

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });

		this.collectionAgentId.setModuleName("CollectionAgencies");
		this.collectionAgentId.setValueColumn("Id");
		this.collectionAgentId.setValueType(DataType.LONG);
		this.collectionAgentId.setDescColumn("Code");
		this.collectionAgentId.setDisplayStyle(2);
		this.collectionAgentId.setValidateColumns(new String[] { "Id" });
		this.collectionAgentId.setVisible(false);
		this.label_ReceiptDialog_CollectionAgentId.setVisible(false);

		// Post Branch
		this.postBranch.setModuleName("Branch");
		this.postBranch.setValueColumn("BranchCode");
		this.postBranch.setDescColumn("BranchDesc");
		this.postBranch.setValidateColumns(new String[] { "BranchCode" });

		// Customer Account
		this.customerBankAcct.setMandatoryStyle(true);
		this.customerBankAcct.setModuleName("CustomerBankInfoAccntNum");
		this.customerBankAcct.setValueColumn("AccountNumber");
		this.customerBankAcct.setDescColumn("AccountHolderName");
		this.customerBankAcct.setValidateColumns(new String[] { "AccountNumber" });
		Filter[] filters = new Filter[1];
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		filters[0] = new Filter("custID", finMain.getCustID(), Filter.OP_EQUAL);
		this.customerBankAcct.setFilters(filters);

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

		appendScheduleMethod(receiptData.getReceiptHeader());

		if (FinanceConstants.CLOSURE_MAKER.equals(module) || FinanceConstants.CLOSURE_APPROVER.equals(module)
				|| (FinanceConstants.RECEIPT_MAKER.equals(module)
						&& FinServiceEvent.EARLYSETTLE.equals(receiptData.getReceiptHeader().getReceiptPurpose()))) {
			this.row_closuretype.setVisible(true);
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.RECEIPT_CASH_PAN_MANDATORY)) {
			BigDecimal recAmount = PennantApplicationUtil
					.formateAmount(receiptData.getReceiptHeader().getReceiptAmount(), formatter);
			BigDecimal cashLimit = new BigDecimal(
					SysParamUtil.getSystemParameterObject("RECEIPT_CASH_PAN_LIMIT").getSysParmValue());
			if (recAmount.compareTo(cashLimit) > 0 && StringUtils
					.equals(receiptData.getReceiptHeader().getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CASH)) {
				this.panSpace.setSclass("mandatory");
				isPanMandatory = !customerDocumentService
						.getCustomerDocExists(financeDetail.getCustomerDetails().getCustID(), PennantConstants.FORM60);
			}
		}
		if (!StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER) && (StringUtils
				.equals(receiptData.getReceiptHeader().getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
						DisbursementConstants.PAYMENT_TYPE_DD))) {
			this.row_DepositDate.setVisible(true);
			this.row_DepositBank.setVisible(true);
			this.hbox_ReceiptDialog_DepositDate.setVisible(true);
		}

		if (!FinanceConstants.CLOSURE_MAKER.equals(module) && !isKnockOff) {
			if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equals(receiptData.getReceiptHeader().getReceiptMode())
					&& ReceiptMode.ESCROW.equals(receiptData.getReceiptHeader().getSubReceiptMode())) {
				this.row_CustomerAccount.setVisible(true);
			}
		}

		if (StringUtils.equals(module, FinanceConstants.REALIZATION_MAKER)
				|| StringUtils.equals(module, FinanceConstants.REALIZATION_APPROVER)) {
			this.row_CancelReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);
			this.row_RealizationDate.setVisible(true);
			this.row_ReceiptModeStatus.setVisible(true);
			this.label_ReceiptDialog_ReceiptModeStatus.setVisible(true);
			this.hbox_ReceiptModeStatus.setVisible(true);
			this.receiptModeStatus.setVisible(true);
		}

		if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_MAKER)
				|| StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_APPROVER)) {
			this.row_ReceiptModeStatus.setVisible(true);
			this.label_ReceiptDialog_ReceiptModeStatus.setVisible(true);
			this.hbox_ReceiptModeStatus.setVisible(true);
			this.receiptModeStatus.setVisible(true);
			this.realizationDate.setVisible(false);
			this.label_ReceiptDialog_RealizationDate.setVisible(false);
			this.hbox_ReceiptDialog_RealizationDate.setVisible(false);

		}

		if (isKnockOff) {
			// this.gb_TransactionDetails.setVisible(false);
			this.gb_InstrumentDetails.setVisible(false);
			this.row_knockOff_Type.setVisible(true);
		}

		if (StringUtils.equals(module, FinanceConstants.CLOSURE_MAKER)
				|| StringUtils.equals(module, FinanceConstants.CLOSURE_APPROVER)
				|| StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER)) {
			this.earlySettlementReason.setMaxlength(10);
			this.earlySettlementReason.setModuleName("EarlySettlementReason");
			this.earlySettlementReason.setValueColumn("Id");
			this.earlySettlementReason.setDescColumn("Description");
			this.earlySettlementReason.setValueType(DataType.LONG);
			this.earlySettlementReason.setValidateColumns(new String[] { "Id" });
		}

		this.panNumber.addForward("onChange", window_ReceiptDialog, "onChangePanNumber");

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$earlySettlementReason(Event event) {

		if (StringUtils.isBlank(this.earlySettlementReason.getValue())) {
			this.earlySettlementReason.setValue("");
			this.earlySettlementReason.setDescription("");
			return;
		}

		reasonCodeData = (ReasonCode) this.earlySettlementReason.getObject();
		if (reasonCodeData == null) {
			return;
		}

		setEarlySettlementReasonData(reasonCodeData.getId());
	}

	public void setEarlySettlementReasonData(Long reasonId) {

		Search search = new Search(ReasonCode.class);
		search.addFilterEqual("Id", reasonId);

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		reasonCodeData = (ReasonCode) searchProcessor.getResults(search).get(0);

		this.earlySettlementReason.setValue(reasonCodeData.getCode());
		this.earlySettlementReason.setDescription(reasonCodeData.getDescription());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param Receipt
	 */
	public void doShowDialog(FinReceiptHeader finReceiptHeader) {
		logger.debug(Literal.ENTERING);

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
			doWriteBeanToComponents();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_ReceiptDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	public void executeAccounting() {
		FinReceiptData tempReceiptData = receiptData.copyEntity();

		tempReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setSimulateAccounting(true);

		receiptCalculator.getXcessList(tempReceiptData);
		receiptService.calcuateDues(tempReceiptData);
		tempReceiptData = receiptService.recalculateReceipt(tempReceiptData);

		FinanceDetail fd = tempReceiptData.getFinanceDetail();
		FinScheduleData scheduleData = fd.getFinScheduleData();
		FinanceMain fm = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdList = scheduleData.getFinanceScheduleDetails();
		FinanceProfitDetail profitDetail = fd.getFinScheduleData().getFinPftDeatil();
		String roleCode = tempReceiptData.getReceiptHeader().getRoleCode();

		fm.setSimulateAccounting(true);

		FinReceiptHeader rch = tempReceiptData.getReceiptHeader();
		rch.setRoleCode(roleCode);
		rch.setNextRoleCode(nextRoleCode);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);

		if (StringUtils.equals(FinanceConstants.DEPOSIT_APPROVER, rch.getRoleCode())
				|| tempReceiptData.isPresentment()) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}

		if (!ReceiptMode.CHEQUE.equals(rch.getReceiptMode())) {
			rch.setRealizationDate(rch.getValueDate());
		}

		Date appDate = SysParamUtil.getAppDate();
		rch.setReceiptDate(appDate);
		rch.setRcdMaintainSts(null);
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);
		rch.setActFinReceipt(fm.isFinIsActive());
		rch.setValueDate(receiptData.getValueDate());

		if (rch.getReceiptMode() != null && rch.getSubReceiptMode() == null) {
			rch.setSubReceiptMode(rch.getReceiptMode());
		}

		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			if (!fm.isSanBsdSchdle()) {
				int size = scheduleData.getFinanceScheduleDetails().size();
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						fm.setMaturityDate(curSchd.getSchDate());
						break;
					} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						scheduleData.getFinanceScheduleDetails().remove(i);
					}
				}
			}
		}

		Date curBusDate = appDate;
		Date valueDate = rch.getValueDate();

		profitDetail.setLpiAmount(rch.getLpiAmount());
		profitDetail.setGstLpiAmount(rch.getGstLpiAmount());
		profitDetail.setLppAmount(rch.getLppAmount());
		profitDetail.setGstLppAmount(rch.getGstLppAmount());

		List<FinReceiptDetail> receiptDetails = rch.getReceiptDetails();
		if (CollectionUtils.isNotEmpty(receiptDetails)) {
			for (FinReceiptDetail receiptDetail : receiptDetails) {
				// long id = receiptService.getRepayID();
				FinRepayHeader repayHeader = new FinRepayHeader();
				repayHeader.setRepayScheduleDetails(receiptDetail.getRepayHeader().getRepayScheduleDetails());
				receiptDetail.setRepayHeader(repayHeader);
				// receiptDetail.getRepayHeader().setRepayID(id);
			}
		}

		if (isForeClosure && FinanceConstants.CLOSURE_APPROVER.equals(module)
				&& this.paidByCustomer.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			rch.setClosureWithFullWaiver(true);
		}

		FinScheduleData finScheduleData = tempReceiptData.getFinanceDetail().getFinScheduleData();
		schdList = finScheduleData.getFinanceScheduleDetails();
		fd.getFinScheduleData().setFinanceScheduleDetails(schdList);

		List<FinFeeDetail> finFeeDetailList = tempReceiptData.getFinanceDetail().getFinScheduleData()
				.getFinFeeDetailList();

		repaymentProcessUtil.doProcessReceipts(fm, schdList, profitDetail, rch, finFeeDetailList, scheduleData,
				valueDate, curBusDate, fd);

		List<ReturnDataSet> returnDataSet = fm.getReturnDataSet();

		getFinanceDetail().setReturnDataSetList(returnDataSet);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(returnDataSet);
			isAccountingExecuted = true;
		}

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		// Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_collectionAgent"), this.collectionAgentId);
		readOnlyComponent(isReadOnly("ReceiptDialog_receivedFrom"), this.receivedFrom);
		readOnlyComponent(isReadOnly("ReceiptDialog_panNumber"), this.panNumber);
		readOnlyComponent(isReadOnly("ReceiptDialog_externalRefrenceNumber"), this.externalRefrenceNumber);

		// Open Amortization CR
		if (financeMain.isManualSchedule() && receiptPurposeCtg == 1) {
			readOnlyComponent(true, this.allocationMethod);
			readOnlyComponent(true, this.effScheduleMethod);
		} else {
			readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
			readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
		}

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
		readOnlyComponent(isReadOnly("ReceiptDialog_paymentRef"), this.paymentRef);
		readOnlyComponent(isReadOnly("ReceiptDialog_customerBankAcct"), this.customerBankAcct);
		readOnlyComponent(isReadOnly("ReceiptDialog_fundingAccount"), this.fundingAccount);
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_earlysettlement"), this.earlySettlementReason);
		readOnlyComponent(true, this.cashierBranch);
		readOnlyComponent(true, this.postBranch);
		readOnlyComponent(true, this.finDivision);
		this.sourceofFund.setDisabled(true);
		readOnlyComponent(isReadOnly("ReceiptDialog_ClosureType"), this.closureType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadOnly(boolean isUserAction) {
		logger.debug(Literal.ENTERING);

		// Receipt Details
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);
		readOnlyComponent(true, this.collectionAgentId);
		readOnlyComponent(true, this.panNumber);
		readOnlyComponent(true, this.externalRefrenceNumber);
		readOnlyComponent(true, this.cashierBranch);
		readOnlyComponent(true, this.postBranch);
		readOnlyComponent(true, this.finDivision);
		readOnlyComponent(true, this.sourceofFund);

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

			// Bounce/Realization/Cancel Reason Fields
			readOnlyComponent(true, this.realizationDate);
			readOnlyComponent(true, this.bounceCode);
			readOnlyComponent(true, this.bounceCharge);
			readOnlyComponent(true, this.bounceRemarks);
			readOnlyComponent(true, this.bounceDate);
			readOnlyComponent(true, this.cancelReason);
			readOnlyComponent(true, this.receiptModeStatus);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgReceipt
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean setSummaryData(boolean isChgReceipt) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);
		receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(receiptData.getReceiptHeader().getReceiptPurpose());
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		this.finReference.setValue(rch.getReference());

		if (receiptPurposeCtg == 2) {
			isClosrMaturedLAN = !isClosrMaturedLAN && isForeClosure && isClosureMaturedLAN(receiptData);
			receiptData.setForeClosure(isForeClosure);
			receiptData.setClosrMaturedLAN(isClosrMaturedLAN);
			if (!isForeClosure) {
				isEarlySettle = true;
			}
		}

		Date valDate = rch.getValueDate();
		receiptData.setValueDate(valDate);
		if (orgReceiptData != null) {
			receiptData = orgReceiptData;
		} else {
			befFinSchedData = receiptData.getFinanceDetail().getFinScheduleData().copyEntity();
			receiptService.calcuateDues(receiptData);
			if (!AllocationType.MANUAL.equals(receiptData.getReceiptHeader().getAllocationType())
					&& receiptData.isCalReq()) {
				receiptData = getReceiptCalculator().recalAutoAllocation(receiptData, false);
			}
			if (!receiptData.isCalReq()) {
				for (ReceiptAllocationDetail allocate : receiptData.getAllocList()) {
					allocate.setTotalPaid(allocate.getPaidAmount().add(allocate.getTdsPaid()));
					allocate.setTotRecv(allocate.getTotalDue().add(allocate.getTdsDue()));
					if (allocate.getAllocationTo() == 0
							|| Allocation.BOUNCE.equalsIgnoreCase(allocate.getAllocationType())) {
						allocate.setTypeDesc(
								Labels.getLabel("label_RecceiptDialog_AllocationType_" + allocate.getAllocationType()));
					} else if (RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus())
							|| RepayConstants.PAYSTATUS_DEPOSITED.equals(rch.getReceiptModeStatus())
							|| RepayConstants.PAYSTATUS_REALIZED.equals(rch.getReceiptModeStatus())
							|| RepayConstants.PAYSTATUS_CANCEL.equals(rch.getReceiptModeStatus())) {
						if (StringUtils.isNotBlank(allocate.getTypeDesc())) {
							allocate.setTypeDesc(allocate.getTypeDesc());
						} else {
							allocate.setTypeDesc(Labels
									.getLabel("label_RecceiptDialog_AllocationType_" + allocate.getAllocationType()));
						}

					}
					if (!PennantStaticListUtil.getExcludeDues().contains(allocate.getAllocationType())) {
						allocate.setEditable(true);
					}
				}
				receiptData.getReceiptHeader().setAllocations(receiptData.getAllocList());
				getReceiptCalculator().setTotals(receiptData, 0);
			}
		}

		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		orgScheduleList = schdData.getFinanceScheduleDetails();
		RepayMain rpyMain = receiptData.getRepayMain();

		receiptData.setAccruedTillLBD(schdData.getFinanceMain().getLovDescAccruedTillLBD());
		rpyMain.setLovDescFinFormatter(formatter);

		String custCIFname = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			custCIFname = customer.getCustCIF();
			if (StringUtils.isNotBlank(customer.getCustShrtName())) {
				custCIFname = custCIFname + "-" + customer.getCustShrtName();
			}
		}

		this.priBal.setValue(PennantApplicationUtil.formateAmount(rpyMain.getPrincipalBalance(), formatter));
		this.pftBal.setValue(PennantApplicationUtil.formateAmount(rpyMain.getProfitBalance(), formatter));
		this.priDue.setValue(PennantApplicationUtil.formateAmount(rpyMain.getOverduePrincipal(), formatter));
		this.pftDue.setValue(PennantApplicationUtil.formateAmount(rpyMain.getOverdueProfit(), formatter));
		this.bounceDueAmount
				.setValue(PennantApplicationUtil.formateAmount(rch.getTotalBounces().getTotalDue(), formatter));
		this.otnerChargeDue
				.setValue(PennantApplicationUtil.formateAmount(rch.getTotalRcvAdvises().getTotalDue(), formatter));

		// Receipt Basic Details
		this.custCIF.setValue(custCIFname);
		this.finReference.setValue(rpyMain.getFinReference());
		this.finType.setValue(rpyMain.getFinType() + " - " + rpyMain.getLovDescFinTypeName());
		this.finBranch.setValue(rpyMain.getFinBranch() + " - " + rpyMain.getLovDescFinBranchName());
		this.finCcy.setValue(rpyMain.getFinCcy());

		setBalances();
		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean isClosureMaturedLAN(FinReceiptData recData) {
		try {
			FinanceMain fm = recData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			Date maturityDate = fm.getMaturityDate();
			return (DateUtil.compare(maturityDate, appDate) < 0) && fm.isFinIsActive();
		} catch (NullPointerException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return false;
	}

	/**
	 * Method for setting data for Child Tab Headers
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
		arrayList.add(0, finMain.getFinType());
		arrayList.add(1, finMain.getFinCcy());
		arrayList.add(2, finMain.getScheduleMethod());
		arrayList.add(3, finMain.getFinReference());
		arrayList.add(4, finMain.getProfitDaysBasis());
		arrayList.add(5, finMain.getGrcPeriodEndDate());
		arrayList.add(6, finMain.isAllowGrcPeriod());

		// In case of Promotion Product will be Empty
		if (StringUtils.isEmpty(finType.getProduct())) {
			arrayList.add(7, false);
		} else {
			arrayList.add(7, true);
		}
		arrayList.add(8, finType.getFinCategory());
		arrayList.add(9, this.custCIF.getValue());
		arrayList.add(10, false);
		arrayList.add(11, module);
		return arrayList;
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnReceipt.isVisible());
		if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}

		if (manualScheduleDialogCtrl != null) {
			manualScheduleDialogCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Method for calculation of Schedule Repayment details List of data
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 * @throws WrongValueException
	 */
	public void onClick$btnCalcReceipts(Event event)
			throws InterruptedException, WrongValueException, InterfaceException {
		logger.debug(Literal.ENTERING + event.toString());
		if (!isValidateData(true)) {
			return;
		}
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(orgScheduleList);
		FinReceiptData tempReceiptData = ObjectUtil.clone(receiptData);
		setOrgReceiptData(tempReceiptData);
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		Date maturityDate = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate();

		receiptData.getFinanceDetail().getFinScheduleData().getErrorDetails().clear();

		boolean isCalcCompleted = true;
		Date valuDate = receiptData.getReceiptHeader().getValueDate();
		if (finMain.isFinIsActive() && DateUtil.compare(valuDate, maturityDate) <= 0) {
			if (receiptPurposeCtg > 0) {
				isCalcCompleted = recalEarlyPaySchd(true);
				if (isCalcCompleted) {
					this.effectiveScheduleTab.setVisible(true);
					if (isForeClosure || isPartPayment) {
						this.btnPrintSchedule.setVisible(
								getUserWorkspace().isAllowed("button_Receiptdialog_ForeClosureEffschd_btnPrint"));
					}
				}
			} else {
				isCalcCompleted = true;
				/*
				 * receiptData = calculateRepayments(); setRepayDetailData();
				 */
			}
		}

		Listitem item;
		for (int i = 0; i < receiptData.getReceiptHeader().getAllocationsSummary().size(); i++) {
			item = listBoxPastdues.getItems().get(i);
			CurrencyBox allocationWaived = (CurrencyBox) item.getFellowIfAny("AllocateWaived_" + i);
			CurrencyBox allocationNetPaid = (CurrencyBox) item.getFellowIfAny("AllocateNetPaid_" + i);
			allocationWaived.setReadonly(true);
			allocationNetPaid.setReadonly(true);
		}

		// Reload user authorities after clicking linked loans but.
		if (isLinkedBtnClick) {
			getUserWorkspace().allocateAuthorities(super.pageRightName, getRole(), menuItemRightName);
			isLinkedBtnClick = false;
		}

		if (isCalcCompleted) {
			doReadOnly(true);
			getUserWorkspace().allocateAuthorities(super.pageRightName, getRole(), menuItemRightName);
			this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
			this.btnChangeReceipt.setDisabled(true);
			this.btnCalcReceipts.setDisabled(true);
			if (manualScheduleDialogCtrl != null) {
				manualScheduleDialogCtrl.uploadIsVisible();
			}
		}

		receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(orgScheduleList);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Processing Calculation button visible , if amount modified
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$receiptAmount(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));

		BigDecimal receiptAmount = this.receiptAmount.getActualValue();
		receiptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount, formatter);
		receiptData.getReceiptHeader().setReceiptAmount(receiptAmount);

		resetAllocationPayments();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Processing Calculation button visible , if Value Date modified
	 * 
	 * @param event
	 */
	public void onChange$receivedDate(Event event) {
		logger.debug(Literal.ENTERING);

		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));

		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		List<ValueLabel> allocationMethods = PennantStaticListUtil.getAllocationMethods();

		Set<String> exclude = new HashSet<>();
		FinanceMain fm = this.financeDetail.getFinScheduleData().getFinanceMain();

		if (!fm.isUnderSettlement()) {
			exclude.add(AllocationType.NO_ALLOC);
		}

		fillComboBox(this.allocationMethod, AllocationType.AUTO, excludeComboBox(allocationMethods, exclude));

		resetAllocationPayments();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = bankCode.getObject();

		if (dataObject instanceof String) {
			this.bankCode.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setAttribute("bankCode", details.getBankCode());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Selecting Bounce Reason Code in case of Receipt got Bounced
	 * 
	 * @param event
	 */
	public void onFulfill$bounceCode(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		Object dataObject = bounceCode.getObject();

		if (dataObject instanceof String) {
			this.bounceCode.setValue(dataObject.toString());
		} else {
			BounceReason bounceReason = (BounceReason) dataObject;
			if (bounceReason != null) {
				Map<String, Object> executeMap = bounceReason.getDeclaredFieldValues();
				this.bounceCode.setAttribute("BounceId", bounceReason.getId());

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
					int dpdCount = 0;
					executeMap.put("br_finType", receiptHeader.getFinType());
					executeMap.put("eligibilityMethod",
							financeDetail.getFinScheduleData().getFinanceMain().getEligibilityMethod());
					Date schdDate = receiptService.getFinSchdDate(receiptHeader);
					if (schdDate != null) {
						dpdCount = dpdCount + DateUtil.getDaysBetween(schdDate, SysParamUtil.getAppDate());
					}
					executeMap.put("br_dpdcount", dpdCount);
					bounceAmt = (BigDecimal) RuleExecutionUtil.executeRule(rule.getSQLRule(), executeMap,
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
		logger.debug(Literal.ENTERING);

		String dType = this.receiptMode.getSelectedItem().getValue().toString();

		if (!StringUtils.isEmpty(dType) && !PennantConstants.List_Select.equals(dType)
				&& ReceiptMode.ESCROW.equals(dType)) {

			fillComboBox(this.receiptPurpose, FinServiceEvent.EARLYRPY, PennantStaticListUtil.getReceiptPurpose(),
					",FeePayment,");
			this.receiptPurpose.setDisabled(true);
		} else {
			this.receiptPurpose.setDisabled(false);
		}
		checkByReceiptMode(dType, true);
		resetAllocationPayments();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * 
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug(Literal.ENTERING);
		if (isUserAction) {
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.favourNo.setValue("");
			this.valueDate.setValue(SysParamUtil.getAppDate());
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankCode.setObject(null);
			this.favourName.setValue("");
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
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.gb_InstrumentDetails.setVisible(false);

		} else {

			/*
			 * if (StringUtils.isEmpty(this.paymentRef.getValue())) {
			 * this.paymentRef.setValue(ReferenceGenerator.generateNewReceiptNo( )); }
			 */

			this.gb_ReceiptDetails.setVisible(true);
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_receiptAmount"), this.receiptAmount);
			// readOnlyComponent(isReadOnly("ReceiptDialog_fundingAccount"),
			// this.fundingAccount);

			String paymentType = this.receiptMode.getSelectedItem().getValue().toString();
			if (paymentType.equals("ONLINE")) {
				paymentType = this.subReceiptMode.getSelectedItem().getValue().toString();
			}

			FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
				branchWisePartnerBank(paymentType, finType);
			} else {

				Filter fundingAcFilters[] = new Filter[4];
				fundingAcFilters[0] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
				fundingAcFilters[1] = new Filter("FinType", finType.getFinType(), Filter.OP_EQUAL);
				fundingAcFilters[2] = new Filter("PaymentMode", recMode, Filter.OP_EQUAL);
				if (ReceiptMode.ONLINE.equals(recMode)) {
					fundingAcFilters[2] = new Filter("PaymentMode", receiptData.getReceiptHeader().getSubReceiptMode(),
							Filter.OP_EQUAL);
				}
				fundingAcFilters[3] = new Filter("EntityCode", finType.getLovDescEntityCode(), Filter.OP_EQUAL);
				Filter.and(fundingAcFilters);
				this.fundingAccount.setFilters(fundingAcFilters);
			}

			// this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);

			if (ReceiptMode.CHEQUE.equals(recMode) || ReceiptMode.DD.equals(recMode)) {

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

				if (ReceiptMode.CHEQUE.equals(recMode)) {
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

				if (isUserAction) {
					this.favourName.setValue(Labels.getLabel("label_ClientName"));
				}

			} else if (ReceiptMode.CASH.equals(recMode)) {

				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				if (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE) {
					this.row_PaymentRef.setVisible(true);
					this.row_DepositBank.setVisible(true);
					this.space_transactionRef.setSclass("");
				} else {
					this.row_PaymentRef.setVisible(false);
					this.row_DepositBank.setVisible(false);
					readOnlyComponent(true, this.fundingAccount);
				}

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

		if (FinanceConstants.DEPOSIT_MAKER.equals(module)
				&& ((ReceiptMode.CHEQUE.equals(recMode) || ReceiptMode.DD.equals(recMode)))) {

			this.fundingAccount.setMandatoryStyle(true);
			this.fundingAccount.setReadonly(false);

		} else if (FinanceConstants.RECEIPT_MAKER.equals(module) && ((!ReceiptMode.CHEQUE.equals(recMode)
				&& !ReceiptMode.DD.equals(recMode) && !ReceiptMode.CASH.equals(recMode)))) {
			this.fundingAccount.setMandatoryStyle(true);
		} else if (FinanceConstants.RECEIPT_MAKER.equals(module)
				&& ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE
				&& ReceiptMode.CASH.equals(recMode)) {
			this.fundingAccount.setMandatoryStyle(true);
		}
		if (isForeClosure || isKnockOff) {
			this.label_ReceiptDialog_PartnerBankCode.setVisible(false);
			this.fundingAccount.setVisible(false);
		}

		// Due to changes in Receipt Amount, call Auto Allocations
		if (isUserAction) {
			resetAllocationPayments();
		}
		logger.debug(Literal.LEAVING);
	}

	private void branchWisePartnerBank(String paymentMode, FinanceType finType) {
		String branchCode = this.cashierBranch.getValue();
		Long clusterId = null;

		Filter[] filters = new Filter[4];
		filters[0] = new Filter("FinType", finType.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", paymentMode, Filter.OP_EQUAL);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			filters[3] = new Filter("BranchCode", branchCode, Filter.OP_EQUAL);
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(branchCode);
			filters[3] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
		}

		FinTypePartnerBank fpb = new FinTypePartnerBank();

		fpb.setFinType(finType.getFinType());
		fpb.setPurpose(RepayConstants.RECEIPTTYPE_RECIPT);
		fpb.setPaymentMode(paymentMode);
		fpb.setBranchCode(branchCode);
		fpb.setClusterId(clusterId);

		List<FinTypePartnerBank> list = finTypePartnerBankService.getFinTypePartnerBanks(fpb);

		if (list.size() == 1) {
			fpb = list.get(0);
			this.fundingAccount.setAttribute("partnerBankId", fpb.getPartnerBankID());
			this.fundingAccount.setValue(fpb.getPartnerBankCode());
			this.fundingAccount.setDescription(fpb.getPartnerBankName());
		}

		this.fundingAccount.setFilters(filters);
	}

	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * 
	 * @param event
	 */
	public void onChange$allocationMethod(Event event) {
		logger.debug(Literal.ENTERING);
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);

		if (AllocationType.AUTO.equals(allocateMthd)) {
			receiptData.getReceiptHeader().setAllocationType(allocateMthd);
			resetAllocationPayments();
		} else if (AllocationType.MANUAL.equals(allocateMthd)) {
			receiptData.getReceiptHeader().setAllocationType(allocateMthd);
			doFillAllocationDetail();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * 
	 * @param event
	 */
	public void onChange$receiptModeStatus(Event event) {
		logger.debug(Literal.ENTERING);
		// Based on Status of Mode Details will be set to Visible
		String status = this.receiptModeStatus.getSelectedItem().getValue().toString();
		resetModeStatus(status);

		if (FinServiceEvent.REALIZATION.equals(moduleDefiner)) {
			reAppendExtendedFields();
		}

		logger.debug(Literal.LEAVING);
	}

	private void reAppendExtendedFields() {
		logger.debug(Literal.ENTERING);
		String receiptModeStatus = this.receiptModeStatus.getSelectedItem().getValue();

		if (extendedFieldCtrl != null || receiptModeStatus.equals("#")) {
			extendedFieldCtrl.removeTab(tabsIndexCenter);
		}

		appendExtendedFieldDetails(financeDetail, moduleDefiner);
		logger.debug(Literal.LEAVING);
	}

	private void setReceiptModeStatus(FinReceiptHeader rch) {
		String exclude = "";
		String receiptModeStatus = rch.getReceiptModeStatus();

		if (receiptModeStatus != null) {
			switch (receiptModeStatus) {
			case RepayConstants.PAYSTATUS_BOUNCE:
				this.bounceDate.setValue(rch.getBounceDate());
				if (rch.getBounceDate() == null) {
					this.bounceDate.setValue(SysParamUtil.getAppDate());
				}

				ManualAdvise ma = rch.getManualAdvise();
				if (ma != null) {
					this.bounceCode.setValue(String.valueOf(ma.getBounceCode()), ma.getBounceCodeDesc());
					this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(ma.getAdviseAmount(), formatter));
					this.bounceRemarks.setValue(ma.getRemarks());
					this.bounceCode.setAttribute("BounceId", ma.getBounceID());
				}
				break;
			case RepayConstants.PAYSTATUS_CANCEL:
				this.cancelReason.setValue(rch.getCancelReason(), rch.getCancelReasonDesc());
				this.cancelRemarks.setValue(rch.getCancelRemarks());
				break;
			case RepayConstants.PAYSTATUS_REALIZED:
				this.realizationDate.setValue(rch.getRealizationDate());
				if (StringUtils.isEmpty(rch.getNextRoleCode())) {
					exclude = ",R,";
				}
				break;
			default:
				break;
			}
		}

		String receiptMode = rch.getReceiptMode();
		if (!ReceiptMode.CHEQUE.equals(receiptMode) && !ReceiptMode.DD.equals(receiptMode)) {
			exclude = ",R,B,";
		}

		fillComboBox(this.receiptModeStatus, receiptModeStatus, PennantStaticListUtil.getReceiptModeStatus(), exclude);
	}

	private void resetModeStatus(String status) {
		logger.debug(Literal.ENTERING);

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

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {

			this.row_BounceReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);

			// readOnlyComponent(isReadOnly("ReceiptDialog_bounceCode"),
			// this.bounceCode);
			// readOnlyComponent(true, this.bounceCharge);
			// readOnlyComponent(isReadOnly("ReceiptDialog_bounceRemarks"),
			// this.bounceRemarks);
			// readOnlyComponent(isReadOnly("ReceiptDialog_bounceDate"),
			// this.bounceDate);

		} else if (RepayConstants.PAYSTATUS_CANCEL.equals(status)) {

			this.row_CancelReason.setVisible(true);
			// readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"),
			// this.cancelReason);

		} else if (RepayConstants.PAYSTATUS_REALIZED.equals(status)) {

			this.row_RealizationDate.setVisible(true);
			// readOnlyComponent(isReadOnly("ReceiptDialog_realizationDate"),
			// this.realizationDate);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Allocation Details recalculation
	 */
	private void resetAllocationPayments() {
		logger.debug(Literal.ENTERING);

		// FIXME: PV: PUT CONDITIONS FOR AUTO ALLOCATION
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		this.receiptPurpose.setConstraint("");
		this.receiptPurpose.setErrorMessage("");
		// this.receivedDate.setConstraint("");
		// this.receivedDate.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);
		String recPurpose = getComboboxValue(this.receiptPurpose);
		Date valueDate = receiptData.getReceiptHeader().getReceiptDate();

		// FIXME: PV: Resetting receipt data and finschdeduledata was deleted
		receiptData.setBuildProcess("I");
		receiptData.getReceiptHeader().setReceiptPurpose(recPurpose);
		receiptData.getReceiptHeader().getAllocations().clear();
		FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();

		if (receiptPurposeCtg == 2 && dateChange) {
			dateChange = false;
			receiptData.getReceiptHeader().setValueDate(null);
			try {
				receiptData.getRepayMain().setEarlyPayOnSchDate(valueDate);
				recalEarlyPaySchd(false);
			} catch (Exception e) {

			}
		}

		// Initiation of Receipt Data object
		receiptData = getReceiptCalculator().initiateReceipt(receiptData, false);

		// Excess Adjustments After calculation of Total Paid's
		BigDecimal totReceiptAmount = receiptData.getTotReceiptAmount();
		receiptData.setTotReceiptAmount(totReceiptAmount);
		receiptData.setAccruedTillLBD(schData.getFinanceMain().getLovDescAccruedTillLBD());

		// Allocation Process start
		if (AllocationType.AUTO.equals(allocateMthd)) {
			receiptData.setActualOdPaid(BigDecimal.ZERO);
			receiptData = getReceiptCalculator().recalAutoAllocation(receiptData, false);
		}

		doFillAllocationDetail();
		setBalances();

		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			// if no extra balance or partial pay disable excessAdjustTo
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for on Changing Waiver Amounts
	 */
	private void changeWaiver() {
		receiptData = getReceiptCalculator().changeAllocations(receiptData);
		setBalances();
		doFillAllocationDetail();
	}

	/**
	 * Method for on Changing Paid Amounts
	 */
	private void changePaid() {
		receiptData = getReceiptCalculator().setTotals(receiptData, 0);
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		receiptData.getReceiptHeader().setAllocationsSummary(allocationList);
		try {
			setSummaryData(true);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		setBalances();
		doFillAllocationDetail();
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterruptedException
	 */
	public boolean recalEarlyPaySchd(boolean isRecal) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		// Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		if (receiptPurposeCtg == 1) {
			rch.setEffectSchdMethod(getComboboxValue(this.effScheduleMethod));
		}

		receiptData = getReceiptService().recalEarlyPaySchedule(receiptData);
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();

		if (fsd.getErrorDetails() != null && !fsd.getErrorDetails().isEmpty()) {
			MessageUtil.showError(fsd.getErrorDetails().get(0));
			return false;
		}

		// Finding Last maturity date after recalculation.
		List<FinanceScheduleDetail> schList = fsd.getFinanceScheduleDetails();
		Date actualMaturity = fsd.getFinanceMain().getCalMaturity();
		for (int i = schList.size() - 1; i >= 0; i--) {
			if (schList.get(i).getClosingBalance().compareTo(BigDecimal.ZERO) > 0) {
				break;
			}
			actualMaturity = schList.get(i).getSchDate();
		}

		// Validation against Future Disbursements, if Closing balance is
		// becoming BigDecimal.ZERO before future disbursement date
		List<FinanceDisbursement> disbList = fsd.getDisbursementDetails();
		String eventDesc = PennantApplicationUtil.getLabelDesc(receiptData.getReceiptHeader().getReceiptPurpose(),
				PennantStaticListUtil.getReceiptPurpose());
		for (int i = 0; i < disbList.size(); i++) {
			FinanceDisbursement curDisb = disbList.get(i);
			if (curDisb.getDisbDate().compareTo(actualMaturity) > 0) {
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("30577", new String[] { eventDesc })));
				Events.sendEvent(Events.ON_CLICK, this.btnChangeReceipt, null);
				logger.debug(Literal.LEAVING);
				return false;
			}
		}

		getFinanceDetail().setFinScheduleData(fsd);
		FinanceMain aFinanceMain = fsd.getFinanceMain();
		// aFinanceMain.setWorkflowId(getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());

		// Object Setting for Future save purpose
		setFinanceDetail(getFinanceDetail());
		receiptData.setFinanceDetail(getFinanceDetail());
		doFillScheduleList(fsd);

		if (isRecal) {

			this.finSchType.setValue(aFinanceMain.getFinType());
			this.finSchCcy.setValue(aFinanceMain.getFinCcy());
			this.finSchMethod.setValue(aFinanceMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantApplicationUtil.getLabelDesc(aFinanceMain.getProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis()));
			this.finSchReference.setValue(aFinanceMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtil.formatToLongDate(aFinanceMain.getGrcPeriodEndDate()));
			this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn().toString() + "%");

			// Fill Effective Schedule Details
			this.effectiveScheduleTab.setVisible(true);
			setFinSchedData(fsd);

			/*
			 * // Dashboard Details Report doLoadTabsData(); doShowReportChart(fsd);
			 */

			// Repayments Calculation
			/*
			 * receiptData = calculateRepayments(); setRepayDetailData();
			 */
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * 
	 * @param aFinScheduleData (FinScheduleData)
	 * 
	 */
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		// FIXME: PV: CODE REVIEW PENDING
		FinanceMain financeMain = aFinScheduleData.getFinanceMain();

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory()) || financeMain.isAlwFlexi()) {

			this.listheader_AvailableLimit.setVisible(true);
			this.listheader_ODLimit.setVisible(true);
			this.listheader_LimitChange.setVisible(true);

			if (financeMain.isAlwFlexi()) {

				label_FinGracePeriodEndDate
						.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinPureFlexiPeriodEndDate.value"));
				listheader_ScheduleEndBal.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchdUtilization"));
				listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_DropLineLimit"));
				listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitDrop"));

			} else {

				listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitChange"));
				listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ODLimit"));
			}
			listheader_AvailableLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AvailableLimit"));
		}

		FinanceScheduleDetail prvSchDetail = null;
		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();

		if (sdSize == 0) {
			logger.debug(Literal.LEAVING);
			return;
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
		aFinScheduleData = getFinanceDetailService().getFinMaintainenceDetails(aFinScheduleData);
		if (aFinScheduleData.getRepayDetails() != null && aFinScheduleData.getRepayDetails().size() > 0) {
			rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

			for (FinanceRepayments rpyDetail : aFinScheduleData.getRepayDetails()) {
				if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
					ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				} else {
					ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
					rpyDetailList.add(rpyDetail);
					rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
				}
			}
		}

		// Find Out Finance Repayment Details on Schedule
		Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
		if (aFinScheduleData.getPenaltyDetails() != null && aFinScheduleData.getPenaltyDetails().size() > 0) {
			penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

			for (OverdueChargeRecovery penaltyDetail : aFinScheduleData.getPenaltyDetails()) {
				if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
					ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
							.get(penaltyDetail.getFinODSchdDate());
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				} else {
					ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
					penaltyDetailList.add(penaltyDetail);
					penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
				}
			}
		}

		// Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FinanceScheduleDetail> schdList = aFinScheduleData.getFinanceScheduleDetails();
		for (int i = 0; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (curSchd.getFeeSchd().compareTo(BigDecimal.ZERO) > 0) {
				isSchdFee = true;
				break;
			}
		}

		if (isSchdFee) {
			this.listheader_ReceiptSchedule_SchFee.setVisible(true);
		} else {
			this.listheader_ReceiptSchedule_SchFee.setVisible(false);
		}

		// Clear all the listitems in listbox
		this.listBoxSchedule.getItems().clear();
		aFinScheduleData.setFinanceScheduleDetails(
				ScheduleCalculator.sortSchdDetails(aFinScheduleData.getFinanceScheduleDetails()));
		int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());

		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			boolean showRate = false;
			FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
			if (i == 0) {
				prvSchDetail = aScheduleDetail;
				showRate = true;
			} else {
				prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
				if (aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
					showRate = true;
				}
			}

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finSchdData", aFinScheduleData);

			map.put("financeScheduleDetail", aScheduleDetail);
			map.put("paymentDetailsMap", rpyDetailsMap);
			map.put("penaltyDetailsMap", penaltyDetailsMap);
			map.put("window", this.window_ReceiptDialog);
			map.put("formatter", formatter);

			finRender.render(map, prvSchDetail, false, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
					false);

			boolean lastRecord = false;
			if (aScheduleDetail.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && !financeMain.isSanBsdSchdle()
					&& !(financeMain.isInstBasedSchd())) {
				if (!(financeMain.isManualSchedule())
						|| aScheduleDetail.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
					lastRecord = true;
				}
			}

			if (i == sdSize - 1 || lastRecord) {
				finRender.render(map, prvSchDetail, true, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
						false);
				break;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Calculating Allocations based on Receipt Details
	 * 
	 * @return
	 */
	private FinReceiptData calculateRepayments() {
		logger.debug(Literal.ENTERING);

		receiptData.setBuildProcess("R");
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		recDtls = rch.getReceiptDetails();

		// Prepare Receipt Details Data
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();

		BigDecimal pastDues = getReceiptCalculator().getTotalNetPastDue(receiptData);
		receiptData.setTotalPastDues(pastDues);

		if (isKnockOff) {
			String payType = payType(rch.getReceiptMode());
			receiptData = receiptService.updateExcessPay(receiptData, payType, rch.getKnockOffRefId(),
					rch.getReceiptAmount());
			receiptData = createXcessRCD();
		} else if (isForeClosure || isEarlySettle) {
			receiptData = createXcessRCD();
			if (isEarlySettle || CollectionUtils.isEmpty(rch.getReceiptDetails())) {
				receiptData = createNonXcessRCD();
			}
		} else {
			receiptData = createNonXcessRCD();
		}
		rch.setRemarks(this.remarks.getValue());

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private String payType(String mode) {
		String payType = "";
		if (ReceiptMode.EMIINADV.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_EMIINADV;
		} else if (ReceiptMode.EXCESS.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_EXCESS;
		} else if (ReceiptMode.CASHCLT.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_CASHCLT;
		} else if (ReceiptMode.DSF.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_DSF;
		} else if (ReceiptMode.TEXCESS.equals(mode)) {
			payType = RepayConstants.EXAMOUNTTYPE_TEXCESS;
		} else {
			payType = RepayConstants.EXAMOUNTTYPE_PAYABLE;
		}
		return payType;
	}

	private FinReceiptData createXcessRCD() {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		receiptData.getReceiptHeader().setReceiptDetails(rcdList);

		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		Map<String, BigDecimal> taxPercMap = null;

		// Create a new Receipt Detail for every type of excess/payable
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);

			if (payable.getTotPaidNow().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);

			if (RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(payable.getPayableType())) {
				rcd.setPaymentType(ReceiptMode.EMIINADV);
			} else if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(payable.getPayableType())) {
				rcd.setPaymentType(ReceiptMode.EXCESS);
			} else if (RepayConstants.EXAMOUNTTYPE_ADVINT.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_ADVINT);
			} else if (RepayConstants.EXAMOUNTTYPE_ADVEMI.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_ADVEMI);
			} else if (RepayConstants.EXAMOUNTTYPE_CASHCLT.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_CASHCLT);
			} else if (RepayConstants.EXAMOUNTTYPE_DSF.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_DSF);
			} else if (RepayConstants.EXAMOUNTTYPE_TEXCESS.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_TEXCESS);
			} else if (RepayConstants.EXAMOUNTTYPE_SETTLEMENT.equals(payable.getPayableType())) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_SETTLEMENT);
			} else {
				rcd.setPaymentType(ReceiptMode.PAYABLE);
			}

			rcd.setPayAgainstID(payable.getPayableID());
			if (receiptData.getTotalPastDues().compareTo(payable.getTotPaidNow()) >= 0) {
				rcd.setDueAmount(payable.getTotPaidNow());
				receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(payable.getPaidNow()));
			} else {
				rcd.setDueAmount(receiptData.getTotalPastDues());
				receiptData.setTotalPastDues(BigDecimal.ZERO);
			}
			if (receiptPurposeCtg < 2) {
				rcd.setAmount(receiptData.getReceiptHeader().getReceiptAmount());
			} else {
				rcd.setAmount(payable.getTotPaidNow());
			}
			rcd.setValueDate(rch.getValueDate());
			rcd.setReceivedDate(rch.getValueDate());
			// rcd.setReceivedDate(this.receivedDate.getValue());
			rcd.setPayOrder(rcdList.size() + 1);
			rcd.setReceiptSeqID(getReceiptSeqID(rcd));

			ManualAdviseMovements mam = new ManualAdviseMovements();

			mam.setAdviseID(payable.getPayableID());
			mam.setMovementDate(rcd.getReceivedDate());
			mam.setMovementAmount(payable.getTotPaidNow());
			mam.setTaxComponent(payable.getTaxType());
			mam.setPaidAmount(payable.getTotPaidNow());
			mam.setFeeTypeCode(payable.getFeeTypeCode());

			mam.setPaidCGST(payable.getPaidCGST());
			mam.setPaidSGST(payable.getPaidSGST());
			mam.setPaidIGST(payable.getPaidIGST());
			mam.setPaidCESS(payable.getPaidCESS());

			// GST Calculations
			if (StringUtils.isNotBlank(payable.getTaxType())) {
				if (taxPercMap == null) {
					taxPercMap = GSTCalculator.getTaxPercentages(fm);
				}

				TaxHeader taxHeader = new TaxHeader();
				taxHeader.setNewRecord(true);
				taxHeader.setRecordType(PennantConstants.RCD_ADD);
				taxHeader.setVersion(taxHeader.getVersion() + 1);

				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				taxDetails.add(getTaxDetail(RuleConstants.CODE_CGST, taxPercMap.get(RuleConstants.CODE_CGST),
						payable.getPaidCGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_SGST, taxPercMap.get(RuleConstants.CODE_SGST),
						payable.getPaidSGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_IGST, taxPercMap.get(RuleConstants.CODE_IGST),
						payable.getPaidIGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_UGST, taxPercMap.get(RuleConstants.CODE_UGST),
						payable.getPaidUGST()));
				taxDetails.add(getTaxDetail(RuleConstants.CODE_CESS, taxPercMap.get(RuleConstants.CODE_CESS),
						payable.getPaidCESS()));

				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						taxes.setPaidTax(payable.getPaidCGST());
						taxes.setRemFeeTax(taxes.getActualTax().subtract(payable.getPaidCGST()));
						break;
					case RuleConstants.CODE_SGST:
						taxes.setPaidTax(payable.getPaidSGST());
						taxes.setRemFeeTax(taxes.getActualTax().subtract(payable.getPaidSGST()));
						break;
					case RuleConstants.CODE_IGST:
						taxes.setPaidTax(payable.getPaidIGST());
						taxes.setRemFeeTax(taxes.getActualTax().subtract(payable.getPaidIGST()));
						break;
					case RuleConstants.CODE_UGST:
						taxes.setPaidTax(payable.getPaidUGST());
						taxes.setRemFeeTax(taxes.getActualTax().subtract(payable.getPaidUGST()));
						break;
					case RuleConstants.CODE_CESS:
						taxes.setPaidTax(payable.getPaidCESS());
						taxes.setRemFeeTax(taxes.getActualTax().subtract(payable.getPaidCESS()));
						break;
					default:
						break;
					}

				}

				mam.setTaxHeader(taxHeader);
			} else {
				mam.setTaxHeader(null);
			}

			rcd.setPayAdvMovement(mam);

			if (rcd.getReceiptSeqID() <= 0) {
				rcdList.add(rcd);
			}

			if (receiptData.getTotalPastDues().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		// rch.setReceiptDetails(rcdList);
		return receiptData;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, BigDecimal taxAmount) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		taxes.setNetTax(taxAmount);
		taxes.setActualTax(taxAmount);
		return taxes;
	}

	private FinReceiptData createNonXcessRCD() {

		if (ReceiptMode.EXCESS.equals(receiptData.getReceiptHeader().getReceiptMode())
				|| ReceiptMode.EMIINADV.equals(receiptData.getReceiptHeader().getReceiptMode())
				|| StringUtils.equals(ReceiptMode.PAYABLE, receiptData.getReceiptHeader().getReceiptMode())) {
			return receiptData;
		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setTransactionRef(this.favourNo.getValue());
		rch.setBankCode(this.bankCode.getValue());
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		FinReceiptDetail rcd = new FinReceiptDetail();

		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(rch.getSubReceiptMode());
		rcd.setPayAgainstID(0);
		rcd.setAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()));
		if (receiptData.getTotalPastDues().compareTo(rch.getReceiptAmount()) >= 0) {
			rcd.setDueAmount(rch.getReceiptAmount());
			receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(rch.getReceiptAmount()));
		} else {
			rcd.setDueAmount(receiptData.getTotalPastDues());
			receiptData.setTotalPastDues(BigDecimal.ZERO);
		}

		rcd.setFavourNumber(this.favourNo.getValue());
		rcd.setValueDate(rch.getValueDate());
		rcd.setBankCode(this.bankCode.getValue());
		rcd.setFavourName(this.favourName.getValue());
		rcd.setDepositDate(this.depositDate.getValue());
		rcd.setDepositNo(this.depositNo.getValue());
		rcd.setPaymentRef(this.paymentRef.getValue());
		rcd.setTransactionRef(this.transactionRef.getValue());
		rcd.setChequeAcNo(this.chequeAcNo.getValue());
		rcd.setReceivedDate(rch.getValueDate());

		rcd.setReceiptSeqID(getReceiptSeqID(rcd));
		rcd.setReceiptID(rch.getReceiptID());

		boolean partnerBankReq = false;
		if (!ReceiptMode.CASH.equals(rcd.getPaymentType())) {
			partnerBankReq = true;
		}

		if (partnerBankReq) {
			Object object = this.fundingAccount.getAttribute("partnerBankId");
			if (object != null) {
				rcd.setFundingAc(Long.valueOf(object.toString()));
				PartnerBank partnerBank = getPartnerBankService().getApprovedPartnerBankById(rcd.getFundingAc());
				if (partnerBank != null) {
					if (rcd.getFundingAc() > 0 && rcd.getFundingAcCode() == null) {
						rcd.setFundingAcCode(partnerBank.getPartnerBankCode());
						rcd.setFundingAcDesc(partnerBank.getPartnerBankName());
					}
					rcd.setPartnerBankAc(partnerBank.getAccountNo());
					rcd.setPartnerBankAcType(partnerBank.getAcType());
				}
			} else {
				rcd.setFundingAc(null);
				rcd.setFundingAcDesc("");
			}
		}

		// rcd.setReceivedDate(this.receivedDate.getValue());
		if (rcd.getReceiptSeqID() <= 0) {
			rcd.setPayOrder(rcdList.size() + 1);
			rcdList.add(rcd);
		} else {
			for (int i = 0; i < rcdList.size(); i++) {
				FinReceiptDetail finReceiptDetail = rcdList.get(i);
				if (finReceiptDetail.getReceiptSeqID() == rcd.getReceiptSeqID()) {
					rcdList.remove(finReceiptDetail);
					rcd.setPayOrder(finReceiptDetail.getPayOrder());
					rcd.setRepayHeader(finReceiptDetail.getRepayHeader());
					rcd.setDueAmount(finReceiptDetail.getDueAmount());
					rcdList.add(rcd);
				}
			}
		}

		rch.setReceiptDetails(rcdList);
		return receiptData;
	}

	private long getReceiptSeqID(FinReceiptDetail recDtl) {
		long receiptSeqId = 0;
		if (recDtls.isEmpty()) {
			return receiptSeqId;
		}
		for (FinReceiptDetail dtl : recDtls) {
			if (recDtl.getPaymentType() != null) {
				if (recDtl.getPaymentType().equals(dtl.getPaymentType())
						&& recDtl.getPayAgainstID() == dtl.getPayAgainstID()) {
					receiptSeqId = dtl.getReceiptSeqID();
				}
			}
		}
		return receiptSeqId;
	}

	private void setRepayDetailData() {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		// Repay Schedule Data rebuild
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();
		for (int i = 0; i < receiptDetailList.size(); i++) {
			FinRepayHeader rph = receiptDetailList.get(i).getRepayHeader();
			if (rph != null) {
				if (rph.getRepayScheduleDetails() != null) {
					rpySchdList.addAll(rph.getRepayScheduleDetails());
				}
			}

		}

		// Making Single Set of Repay Schedule Details and sent to Rendering
		List<RepayScheduleDetail> tempRpySchdList = ObjectUtil.clone(rpySchdList);
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
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
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
		if (rpySchdMap.isEmpty()) {
			this.receiptDetailsTab.setSelected(true);
		}

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));
		this.btnCalcReceipts.setDisabled(true);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING + event.toString());
		// FIXME: PV: CODE REVIEW PENDING
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
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

			// PAN Verification
			MasterDef md = MasterDefUtil.getMasterDefByType(DocType.PAN);
			if (md != null && md.isProceedException() && StringUtils.isNotBlank(this.panNumber.getValue())
					&& !this.isPANVerified) {
				MessageUtil.showError(md.getKeyType() + " Number Must Be Verified.");
				return;
			}

			// Extended Fields
			if (receiptData.getFinanceDetail().getExtendedFieldHeader() != null) {
				receiptData.getFinanceDetail().setExtendedFieldRender(extendedFieldCtrl.save(!recSave));

				FinReceiptHeader rh = receiptData.getReceiptHeader();

				if (receiptData.getFinanceDetail().getExtendedFieldRender() != null) {
					ExtendedFieldExtension efe = new ExtendedFieldExtension();
					if (extendedFieldCtrl.getExtendedFieldExtension() != null) {
						BeanUtils.copyProperties(extendedFieldCtrl.getExtendedFieldExtension(), efe);
					}

					efe.setExtenrnalRef(Long.toString(rh.getReceiptID()));
					efe.setPurpose(rh.getReceiptPurpose());
					efe.setModeStatus(rh.getReceiptModeStatus());
					efe.setSequence(receiptData.getFinanceDetail().getExtendedFieldRender().getSeqNo());
					efe.setEvent(PennantStaticListUtil
							.getFinEventCode(receiptData.getFinanceDetail().getExtendedFieldHeader().getEvent()));

					receiptData.getFinanceDetail().setExtendedFieldExtension(efe);
				}

			}
			if (!recReject) {
				if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptData.getReceiptHeader().getReceiptModeStatus())
						&& !RepayConstants.PAYSTATUS_CANCEL
								.equals(receiptData.getReceiptHeader().getReceiptModeStatus())) {
					calculateRepayments();
				}
				FinReceiptData data = receiptData;
				FinReceiptHeader rch = receiptData.getReceiptHeader();
				List<FinReceiptDetail> receiptDetails = rch.getReceiptDetails();
				BigDecimal totReceiptAmt = receiptData.getTotReceiptAmount();
				BigDecimal feeAmount = receiptData.getReceiptHeader().getTotFeeAmount();

				rch.setTotFeeAmount(feeAmount);
				rch.setReceiptAmount(totReceiptAmt);
				rch.setRemarks(this.remarks.getValue());

				if (ReceiptMode.CHEQUE.equals(rch.getPaymentType()) || ReceiptMode.DD.equals(rch.getPaymentType())) {
					receiptData.getReceiptHeader().setTransactionRef(this.favourNo.getValue());
				} else {
					receiptData.getReceiptHeader().setTransactionRef(this.transactionRef.getValue());
				}

				rch.setBankCode(this.bankCode.getValue());

				for (FinReceiptDetail receiptDetail : receiptDetails) {
					if (!ReceiptMode.EXCESS.equals(rch.getReceiptMode())
							&& StringUtils.equals(receiptDetail.getPaymentType(), rch.getReceiptMode())) {
						receiptDetail.setFavourNumber(this.favourNo.getValue());
						// PSD#165780
						if ((ReceiptMode.CHEQUE.equals(data.getReceiptHeader().getReceiptMode())
								|| ReceiptMode.DD.equals(data.getReceiptHeader().getReceiptMode()))
								&& RepayConstants.PAYSTATUS_REALIZED
										.equals(receiptData.getReceiptHeader().getReceiptModeStatus())
								&& this.realizationDate.getValue() != null) {
							receiptDetail.setValueDate(this.realizationDate.getValue());
						} else {
							receiptDetail.setValueDate(this.valueDate.getValue());
						}
						receiptDetail.setBankCode(this.bankCode.getValue());
						receiptDetail.setFavourName(this.favourName.getValue());
						receiptDetail.setDepositDate(this.depositDate.getValue());
						receiptDetail.setDepositNo(this.depositNo.getValue());
						receiptDetail.setPaymentRef(this.paymentRef.getValue());
						receiptDetail.setTransactionRef(this.transactionRef.getValue());
						receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
						// receiptDetail.setReceivedDate(this.receivedDate.getValue());

						boolean partnerBankReq = false;
						if (!ReceiptMode.CASH.equals(receiptDetail.getPaymentType())
								|| (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE)) {
							partnerBankReq = true;
						}
						// FIXME
						if (partnerBankReq) {
							Object object = this.fundingAccount.getAttribute("partnerBankId");
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

						receiptData.getReceiptHeader().setDepositDate(this.depositDate.getValue());
						if (ReceiptMode.CHEQUE.equals(receiptDetail.getPaymentType())
								|| ReceiptMode.DD.equals(receiptDetail.getPaymentType())) {
							receiptData.getReceiptHeader().setTransactionRef(this.favourNo.getValue());
						} else {
							receiptData.getReceiptHeader().setTransactionRef(this.transactionRef.getValue());
						}
						// ### 30-OCT-2018,Ticket id :124998
						receiptDetail.setStatus(getComboboxValue(receiptModeStatus));
						receiptData.getReceiptHeader().setValueDate(this.valueDate.getValue());
						receiptData.getReceiptHeader().setPartnerBankId(receiptDetail.getFundingAc());
					}
				}
			}

			/*
			 * FinReceiptHeader rch = receiptData.getReceiptHeader(); boolean isNew = rch.isNewRecord(); String tranType
			 * = "";
			 * 
			 * if (isWorkFlowEnabled()) { tranType = PennantConstants.TRAN_WF; if
			 * (StringUtils.isBlank(rch.getRecordType())) { rch.setVersion(rch.getVersion() + 1); if (isNew) {
			 * rch.setRecordType(PennantConstants.RECORD_TYPE_NEW); } else {
			 * rch.setRecordType(PennantConstants.RECORD_TYPE_UPD); rch.setNewRecord(true); } } } else {
			 * rch.setVersion(rch.getVersion() + 1); if (isNew) { tranType = PennantConstants.TRAN_ADD; } else {
			 * tranType = PennantConstants.TRAN_UPD; } }
			 */

			if (recReject || isValidateData(false)) {
				// If Schedule Re-modified Save into DB or else only add
				// Repayments Details
				doProcessReceipt();
			}

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		} catch (WrongValuesException we) {
			throw we;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean validateAccounting(boolean validate) {
		String userAction = this.userAction.getSelectedItem().getLabel();
		if (userAction.equalsIgnoreCase("Cancel") || userAction.contains("Reject") || userAction.contains("Resubmit")) {
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
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		receiptData.getFinanceDetail().setUserAction(this.userAction.getSelectedItem().getLabel());
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
		// it is required since based on the work flow
		rch.setNextTaskId(curNextTaskId);

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

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aReceiptData.getFinanceDetail()
					.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aReceiptData.getFinanceDetail().setDocumentDetailsList(null);
		}

		String roleCode = rch.getRoleCode();
		if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())
				&& (FinanceConstants.RECEIPT_MAKER.equals(roleCode)
						|| FinanceConstants.CLOSURE_MAKER.equals(roleCode))) {
			List<String> finReferences = linkedFinancesService.getFinReferences(rch.getReference());

			if (CollectionUtils.isNotEmpty(finReferences)) {
				String[] args = new String[2];
				StringBuilder ref = new StringBuilder();

				finReferences.forEach(l1 -> ref.append(l1 + "\n"));

				args[0] = rch.getReference();
				args[1] = ref.toString();

				String message = args[0] + " is Linked with " + "\n" + args[1] + "\n"
						+ "Please Delink the loan first then Proceed. ";

				if (MessageUtil.confirm(message, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
					List<WrongValueException> wve = new ArrayList<>();
					wve.add(new WrongValueException("Please Delink the loan first then Proceed "));
					showErrorDetails(wve);
				}
			}
		}

		// Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum()
					.compareTo(getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aReceiptData.getFinanceDetail().setStageAccountingList(null);
		}

		if (!recSave && getAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
			if (getAccountingDetailDialogCtrl().getDisbCrSum()
					.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		}

		/*
		 * // Finance CheckList Details Tab if (checkListChildWindow != null) { boolean validationSuccess =
		 * doSave_CheckList(aReceiptData.getFinanceDetail(), false); if (!validationSuccess) { return; } } else {
		 * aReceiptData.getFinanceDetail().setFinanceCheckList(null); }
		 */

		aReceiptData.setEventCodeRef(eventCode);

		// save it to database
		try {

			File file = new File(TEMPLATE_PATH + File.separator + RECEIPT_TEMPLATE);

			if (!file.exists()) {
				throw new AppException(
						String.format("%s Template not available in %s loaction", RECEIPT_TEMPLATE, TEMPLATE_PATH));
			}

			if (doProcess(aReceiptData, tranType)) {

				if (getReceiptListCtrl() != null) {
					refreshMaintainList();
				}

				String userAction = StringUtils.trimToEmpty(this.userAction.getSelectedItem().getLabel());

				if (StringUtils.isBlank(nextRoleCode) && !"DEPOSIT_APPROVER".equals(getRole())) {
					if (!"Save".equalsIgnoreCase(userAction) && !"Cancel".equalsIgnoreCase(userAction)
							&& !"Resubmit".equalsIgnoreCase(userAction) && !userAction.contains("Reject")
							&& !userAction.contains("Submit")) {

						String rceiptModeSts = rch.getReceiptModeStatus();
						boolean bounceOrCancel = RepayConstants.PAYSTATUS_BOUNCE.equals(rceiptModeSts)
								|| RepayConstants.PAYSTATUS_CANCEL.equals(rceiptModeSts);
						boolean isReceiptAmount = receiptData.getTotReceiptAmount().compareTo(BigDecimal.ZERO) > 0;

						if (!bounceOrCancel) {
							if (isReceiptAmount) {
								printReceipt();
							} else if (ImplementationConstants.RECEIPT_ALLOW_FULL_WAIVER_ACKNOWLEDGEMENT
									&& !isReceiptAmount) {
								printReceipt();
							}
						}
					}

				}

				String msg = PennantApplicationUtil.getSavingStatus(roleCode, rch.getNextRoleCode(), rch.getReference(),
						" Loan ", rch.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				if (!("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| RepayConstants.RECEIPT_CHANNEL_MOBILE.equals(rch.getReceiptChannel()))) {
					FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);

					notification.setModule("LOAN");
					notification.setSubModule("RECEIPT");
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(receiptData.getReceiptHeader().getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());
					notificationService.sendNotifications(notification, receiptData, financeMain.getFinType(),
							getFinanceDetail().getDocumentDetailsList());
				}

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						String reference = rch.getReference();
						if (StringUtils.isNotEmpty(rch.getNextRoleCode())) {
							if (!PennantConstants.RCD_STATUS_CANCELLED.equals(rch.getRecordStatus())) {
								String[] to = rch.getNextRoleCode().split(",");
								String message;

								if (StringUtils.isBlank(rch.getNextTaskId())) {
									message = Labels.getLabel("REC_FINALIZED_MESSAGE");
								} else {
									message = Labels.getLabel("REC_PENDING_MESSAGE");
								}
								message += " with Reference" + ":" + reference;

								// getEventManager().publish(message, to,
								// finDivision, aFinanceMain.getFinBranch());
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				if (manualScheduleDialogCtrl != null) {
					manualScheduleDialogCtrl.deAllocateAuthorities();
				}

				closeDialog();
			}

		} catch (final Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 */
	private void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date appDate = SysParamUtil.getAppDate();

		this.favourName.setValue(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getEntityDesc());
		this.finReference.setValue(rch.getReference());
		this.postBranch.setValue(rch.getPostBranch(), rch.getPostBranchDesc());
		this.cashierBranch.setValue(rch.getCashierBranch(), rch.getCashierBranchDesc());
		this.finDivision.setValue(rch.getFinDivision(), rch.getFinDivisionDesc());
		this.valueDate.setValue(rch.getValueDate());
		if (StringUtils.isEmpty(rch.getAllocationType())) {
			rch.setAllocationType(AllocationType.AUTO);
		}
		if (isKnockOff) {
			this.row_knockOffRef.setVisible(true);
			this.knockoffReferenec.setValue(String.valueOf(rch.getKnockOffRefId()));
			this.row_DepositBank.setVisible(false);
		}

		if (rch.getClosureType() != null) {
			this.closureType.setDisabled(true);
			this.closureType.setValue(rch.getClosureType());
		}

		fillComboBox(this.receiptPurpose, rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(),
				",FeePayment,");
		this.receiptPurpose.setDisabled(true);

		String excldValues = ",PRESENT";
		if (FinanceConstants.REALIZATION_MAKER.equals(getRole())
				|| FinanceConstants.REALIZATION_APPROVER.equals(getRole())) {
			excldValues = "";
		}

		if (finType.isDeveloperFinance()) {
			excldValues = excldValues.concat(", MOBILE,");
		}

		fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getReceiptPaymentModes(),
				excldValues);

		if (isKnockOff) {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getKnockOffFromVlaues(), "A,P");
		}

		this.receiptMode.setDisabled(true);
		appendReceiptMode(rch);
		// appendScheduleMethod(rch);

		this.receiptAmount.setValue(PennantApplicationUtil
				.formateAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()), formatter));
		this.tDSAmount.setValue(PennantApplicationUtil.formateAmount(rch.getTdsAmount(), formatter));
		this.tDSAmount.setDisabled(true);
		if (isEarlySettle) {
			this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rch.getReceiptAmount(), formatter));
		}
		this.receiptAmount.setDisabled(true);
		this.remarks.setValue(rch.getRemarks());
		this.receiptDate.setValue(appDate);
		this.receiptDate.setValue(rch.getReceiptDate());
		this.receiptId.setValue(String.valueOf(rch.getReceiptID()));
		this.receiptDate.setDisabled(true);

		this.receivedDate.setValue(rch.getReceivedDate());
		this.receivedDate.setDisabled(true);
		if (rch.getReasonCode() != null && rch.getReasonCode() != 0) {
			setEarlySettlementReasonData(rch.getReasonCode());
		}

		List<ValueLabel> allocationMethods = PennantStaticListUtil.getAllocationMethods();

		Set<String> exclude = new HashSet<>();

		if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose()) && isEarlySettle) {
			exclude.add(AllocationType.MANUAL);
			this.allocationMethod.setDisabled(true);
		}

		if (!fm.isUnderSettlement()) {
			exclude.add(AllocationType.NO_ALLOC);
		}

		fillComboBox(this.allocationMethod, rch.getAllocationType(), excludeComboBox(allocationMethods, exclude));

		fillComboBox(this.receivedFrom, RepayConstants.RECEIVED_CUSTOMER, PennantStaticListUtil.getReceivedFrom(), "");

		// doFillEarlyPayMethods(valueDate);
		appendScheduleMethod(receiptData.getReceiptHeader());

		// FIXME: PV: CODE REVIEW PENDING

		// Receipt Mode Status Details
		if (isReadOnly("ReceiptDialog_receiptModeStatus") || this.receiptModeStatus.getSelectedIndex() > 0) {
			this.label_ReceiptDialog_ReceiptModeStatus.setVisible(true);
			this.hbox_ReceiptModeStatus.setVisible(true);
		}

		setReceiptModeStatus(rch);

		this.panNumber.setValue(rch.getPanNumber());

		resetModeStatus(rch.getReceiptModeStatus());

		// Receipt Mode Details , if FinReceiptDetails Exists
		setBalances();
		checkByReceiptMode(rch.getReceiptMode(), false);
		this.valueDate.setValue(rch.getValueDate());

		if (row_knockOff_Type.isVisible()) {
			this.knockOffType.setValue(KnockOffType.getDesc(rch.getKnockOffType()));
		}

		// Separating Receipt Amounts based on user entry, if exists
		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail rcd = rch.getReceiptDetails().get(i);

				if (!ReceiptMode.EXCESS.equals(rcd.getPaymentType())
						&& !ReceiptMode.EMIINADV.equals(rcd.getPaymentType())
						&& !ReceiptMode.PAYABLE.equals(rcd.getPaymentType())
						&& !ReceiptMode.ADVINT.equals(rcd.getPaymentType())) {
					this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rcd.getAmount(), formatter));
					this.favourNo.setValue(rcd.getFavourNumber());
					// PSD#165780
					// this.valueDate.setValue(rcd.getValueDate());
					this.bankCode.setValue(rcd.getBankCode());
					this.bankCode.setDescription(rcd.getBankCodeDesc());
					this.favourName.setValue(rcd.getFavourName());
					this.depositDate.setValue(rcd.getDepositDate());
					this.depositNo.setValue(rcd.getDepositNo());
					this.paymentRef.setValue(rcd.getPaymentRef());
					this.transactionRef.setValue(rcd.getTransactionRef());
					this.externalRefrenceNumber.setValue(rch.getExtReference());
					this.chequeAcNo.setValue(rcd.getChequeAcNo());
					// this.receivedDate.setValue(rcd.getReceivedDate());

					boolean partnerBankReq = false;
					if (!ReceiptMode.CASH.equals(rcd.getPaymentType())
							|| (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE)) {
						partnerBankReq = true;
					}

					if (partnerBankReq) {
						this.fundingAccount.setAttribute("partnerBankId", rcd.getFundingAc());
						this.fundingAccount.setValue(rcd.getFundingAcCode(),
								StringUtils.trimToEmpty(rcd.getFundingAcDesc()));
					}
				}
			}
		}
		doFillExcessPayables();
		// Render Excess Amount Details
		doFillAllocationDetail();

		if (rch.getCollectionAgentId() == 0) {
			this.collectionAgentId.setValue("");
		} else {
			this.collectionAgentId.setValue(String.valueOf(rch.getCollectionAgentId()));
		}

		// Only In case of partial settlement process, Display details for
		// effective Schedule
		boolean visibleSchdTab = true;
		if (receiptPurposeCtg == 1) {

			FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
			finScheduleData.setFinanceMain(getFinanceDetail().getFinScheduleData().getFinanceMain());
			finScheduleData
					.setFinanceScheduleDetails(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());

			// Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);

			// Dashboard Details Report
			/*
			 * doLoadTabsData(); doShowReportChart(finScheduleData);
			 */

		}

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		// On Loading Data Render for Schedule
		if (receiptHeader != null && receiptHeader.getReceiptDetails() != null
				&& !receiptHeader.getReceiptDetails().isEmpty()) {
			this.btnCalcReceipts.setDisabled(true);
			setRepayDetailData();
		}

		getFinanceDetail().setModuleDefiner(FinServiceEvent.RECEIPT);

		if (visibleSchdTab) {
			appendScheduleDetailTab(true, false);
		}

		if (fm.isStepFinance()
				&& CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getStepPolicyDetails())) {
			appendStepDetailTab(true);
		}

		this.recordStatus.setValue(receiptHeader.getRecordStatus());
		if (receiptPurposeCtg == 2 && (ReceiptMode.CHEQUE.equals(receiptHeader.getReceiptMode())
				|| ReceiptMode.DD.equals(receiptHeader.getReceiptMode()))) {
			this.valueDate.setValue(rch.getValueDate());
			this.valueDate.setReadonly(true);
			this.valueDate.setDisabled(true);
		}

		if (FinanceConstants.RECEIPT_MAKER.equals(module) || FinanceConstants.CLOSURE_MAKER.equals(module)) {
			if (this.receiptPurpose.getSelectedItem().getValue().equals(FinServiceEvent.EARLYSETTLE) || isForeClosure) {
				this.earlySettlementReason.setButtonDisabled(false);
			} else {
				this.earlySettlementReason.setButtonDisabled(true);
			}
		}
		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if (isApprover() && SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTS_SHOW_ACCOUNTING_TAB)) {
			appendAccountingDetailTab(true);
		}
		fillComboBox(this.sourceofFund, receiptHeader.getSourceofFund(), sourceofFundList, "");

		// append Extended Fields
		if ((!this.receiptModeStatus.getSelectedItem().getValue().equals("#"))
				|| !FinServiceEvent.REALIZATION.equals(moduleDefiner)) {
			appendExtendedFieldDetails(financeDetail, moduleDefiner);
		}

		// Manual Schedule
		if (fm.isManualSchedule() && StringUtils.equals(receiptHeader.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
			appendManualScheduleDetailTab();
			Tab tab = (Tab) getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
			if (tab != null) {
				tab.setVisible(true);
			}
		}

		// Customer Bank Account number.
		this.customerBankAcct.setValue(StringUtils.trimToEmpty(rch.getCustAcctNumber()),
				StringUtils.trimToEmpty(rch.getCustAcctHolderName()));
		this.customerBankAcct.setAttribute("CustBankId", rch.getCustBankId());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param onLoad
	 */
	public void appendManualScheduleDetailTab() {
		logger.debug(Literal.ENTERING);
		String tabName = "";

		tabName = Labels.getLabel("tab_label_" + AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
		tab.setVisible(false);
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		ComponentsCtrl.applyForward(tab, "onSelect = onSelectManualScheduleDetailsTab");

		final HashMap<String, Object> map = new HashMap<>();

		map.put("moduleDefiner", getFinanceDetail().getModuleDefiner());
		map.put("financeDetail", getFinanceDetail());
		map.put("parentCtrl", this);
		map.put("roleCode", getRole());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ManualScheduleDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_MANUALSCHEDULE), map);

		logger.debug(Literal.LEAVING);

	}

	private void appendReceiptMode(FinReceiptHeader rch) {
		if (PennantConstants.List_Select.equals(rch.getSubReceiptMode())
				&& PennantConstants.List_Select.equals(rch.getReceiptChannel())) {
			receiptTypeLabel.setVisible(false);
			subReceiptMode.setVisible(false);
			receiptChannel.setVisible(false);
			return;
		}

		if ((ReceiptMode.ONLINE.equals(rch.getReceiptMode())) && rch.getSubReceiptMode() != null
				&& !PennantConstants.List_Select.equals(rch.getSubReceiptMode())) {
			receiptTypeLabel.setVisible(true);
			subReceiptMode.setVisible(true);
			receiptTypeLabel.setValue(Labels.getLabel("label_ReceiptPayment_SubReceiptMode.value"));
			fillComboBox(subReceiptMode, rch.getSubReceiptMode(), PennantStaticListUtil.getSubReceiptPaymentModes(),
					"");
			this.subReceiptMode.setDisabled(true);
		} else {
			receiptTypeLabel.setVisible(true);
			receiptChannel.setVisible(true);
			receiptTypeLabel.setValue(Labels.getLabel("label_ReceiptPayment_ReceiptChannel.value"));
			fillComboBox(receiptChannel, rch.getReceiptChannel(), PennantStaticListUtil.getReceiptChannels(), "");
			this.receiptChannel.setDisabled(true);
		}
	}

	private void appendScheduleMethod(FinReceiptHeader rch) {
		String excessAdjustTo = rch.getExcessAdjustTo();
		Set<String> exclude = new HashSet<>();
		List<ValueLabel> excessAdjustToList = PennantStaticListUtil.getExcessAdjustmentTypes();

		if (receiptPurposeCtg != 1) {
			scheduleLabel.setValue(Labels.getLabel("label_ReceiptPayment_ExcessAmountAdjustment.value"));
			this.excessAdjustTo.setVisible(true);
			this.excessAdjustTo.setDisabled(false);

			if (StringUtils.contains(rch.getFinType(), "OD")) {
				excessAdjustTo = "A";
				exclude.add("E");
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
			}

			if (receiptPurposeCtg == 2) {
				exclude.clear();
				exclude.add("A");
				if (StringUtils.isEmpty(rch.getExcessAdjustTo())) {
					excessAdjustTo = "E";
				}

				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
			}

			FinanceMain fm = this.financeDetail.getFinScheduleData().getFinanceMain();

			if (!fm.isUnderSettlement()) {
				exclude.add("S");
			}

			fillComboBox(this.excessAdjustTo, excessAdjustTo, excludeComboBox(excessAdjustToList, exclude));

		} else {
			this.effScheduleMethod.setVisible(true);
			if (getFinanceMain().isManualSchedule()) {
				this.effScheduleMethod.setDisabled(true);
			} else {
				this.effScheduleMethod.setDisabled(false);
			}

			this.excessAdjustTo.setVisible(false);
			this.excessAdjustTo.setDisabled(true);

			this.scheduleLabel.setValue(Labels.getLabel("label_ReceiptDialog_EffecScheduleMethod.value"));

			List<ValueLabel> epyMethodList = getEffectiveSchdMethods();
			String defaultMethod = "";
			String effschmethod = getFinanceDetail().getFinScheduleData().getFinanceType().getFinScheduleOn();

			if (!epyMethodList.isEmpty() && StringUtils.isNotEmpty(effschmethod)) {
				defaultMethod = StringUtils.isEmpty(rch.getEffectSchdMethod()) ? effschmethod
						: rch.getEffectSchdMethod();
			}

			if (!getFinanceMain().isManualSchedule()) {
				fillComboBox(effScheduleMethod, defaultMethod, getEffectiveSchdMethods(), "");
			} else {
				fillComboBox(effScheduleMethod, "", getEffectiveSchdMethods(), "");
			}
		}

	}

	private List<ValueLabel> getEffectiveSchdMethods() {
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType finType = finScheduleData.getFinanceType();
		List<ValueLabel> repyMethodList = new ArrayList<>();
		boolean isRpyStp = false;

		if (finMain.isStepFinance() && (CalculationConstants.SCHMTHD_PRI_PFT.equals(finMain.getScheduleMethod())
				|| CalculationConstants.SCHMTHD_PRI.equals(finMain.getScheduleMethod()))) {
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		}

		if (finMain.isStepFinance()
				&& StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
			List<FinanceStepPolicyDetail> stpDetails = getFinanceDetail().getFinScheduleData().getStepPolicyDetails();
			if (CollectionUtils.isNotEmpty(stpDetails)) {
				for (FinanceStepPolicyDetail stp : stpDetails) {
					if (StringUtils.equals(stp.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
						isRpyStp = true;
						break;
					}
				}
			}
		}

		if (finMain.isApplySanctionCheck()) {
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_ADJMUR, Labels.getLabel("label_Adjust_To_Maturity")));
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		} else if (finMain.isAlwFlexi() || finType.isDeveloperFinance()) {
			repyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		} else {
			if (finMain.isStepFinance() && PennantConstants.STEPPING_CALC_PERC.equals(finMain.getCalcOfSteps())
					&& finMain.isAllowGrcPeriod() && FinanceConstants.STEPTYPE_PRIBAL.equals(finMain.getStepType())
					&& DateUtil.compare(receiptData.getValueDate(), finMain.getGrcPeriodEndDate()) <= 0
					&& (CalculationConstants.SCHMTHD_PRI.equals(finMain.getScheduleMethod())
							|| CalculationConstants.SCHMTHD_PRI_PFT.equals(finMain.getScheduleMethod()))) {
				repyMethodList
						.add(new ValueLabel(CalculationConstants.RPYCHG_STEPPOS, Labels.getLabel("label_POSStep")));
			} else if (finMain.isStepFinance() && isRpyStp
					|| PennantConstants.STEPPING_CALC_PERC.equals(finMain.getCalcOfSteps())) {
				if (finMain.getFinCurrAssetValue().compareTo(finMain.getFinAssetValue()) == 0) {
					repyMethodList.add(new ValueLabel(CalculationConstants.RPYCHG_ADJTNR_STEP,
							Labels.getLabel("label_Step_Adj_Tenor")));
					repyMethodList.add(new ValueLabel(CalculationConstants.RPYCHG_ADJEMI_STEP,
							Labels.getLabel("label_Step_Adj_EMI")));
				} else {
					repyMethodList.add(new ValueLabel(CalculationConstants.RPYCHG_ADJTNR_STEP,
							Labels.getLabel("label_Step_Adj_Tenor")));
				}
			} else {
				if (StringUtils.isNotEmpty(finType.getAlwEarlyPayMethods())) {
					String[] epMthds = finType.getAlwEarlyPayMethods().trim().split(",");
					if (epMthds.length > 0) {
						List<String> list = Arrays.asList(epMthds);
						for (ValueLabel label : PennantStaticListUtil.getEarlyPayEffectOn()) {
							if (list.contains(label.getValue().trim())) {
								repyMethodList.add(label);
							}
						}
					}
				}
			}
		}
		return repyMethodList;
	}

	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * 
	 * @param header
	 * @param allocatePaidMap
	 */

	public void doFillAllocationDetail() {
		logger.debug(Literal.ENTERING);
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocationsSummary();
		if (!receiptData.isCalReq()) {
			allocationList = receiptData.getReceiptHeader().getAllocations();
		}
		this.listBoxPastdues.getItems().clear();

		// Get Receipt Purpose to Make Waiver amount Editable
		String label = Labels.getLabel("label_RecceiptDialog_AllocationType_");
		boolean isManAdv = false;
		doRemoveValidation();
		doClearMessage();

		BigDecimal tdsAmt = receiptData.getReceiptHeader().getTdsAmount();

		BigDecimal totalTDS = tdsAmt;
		int i = 0;
		for (ReceiptAllocationDetail allocate : allocationList) {
			createAllocateItem(allocate, isManAdv, label, i);
			if (tdsAmt.compareTo(BigDecimal.ZERO) == 0) {
				totalTDS = totalTDS.add(allocate.getTdsPaid());
			}
			i++;
		}

		receiptData.getReceiptHeader().setTdsAmount(totalTDS);

		addDueFooter(formatter);
		if (receiptData.getRemBal() != BigDecimal.ZERO) {
			addExcessAmt();
		}

		if (receiptData.getPaidNow().compareTo(receiptData.getReceiptHeader().getReceiptAmount().add(totalTDS)
				.add(receiptData.getExcessAvailable())) > 0 && !receiptData.isForeClosure()) {
			ErrorDetail errorDetails = null;
			String[] valueParm = new String[1];
			String[] errParm = new String[2];
			errParm[0] = PennantApplicationUtil.amountFormate(receiptData.getReceiptHeader().getReceiptAmount()
					.add(totalTDS).add(receiptData.getExcessAvailable()), PennantConstants.defaultCCYDecPos);
			errParm[1] = PennantApplicationUtil.amountFormate(receiptData.getPaidNow(),
					PennantConstants.defaultCCYDecPos);
			valueParm[0] = "";
			errorDetails = ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "WFEE12", errParm, valueParm));
			MessageUtil.showError(errorDetails);
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChangePanNumber(Event event) {
		logger.debug(Literal.ENTERING);
		isPANVerified = false;
		String panNumber = this.panNumber.getValue();
		Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();

		try {
			if (StringUtils.isBlank(panNumber) || !MasterDefUtil.isValidationReq(MasterDefUtil.DocType.PAN)) {
				logger.debug(Literal.LEAVING);
				return;
			}

			DocVerificationHeader header = new DocVerificationHeader();
			header.setDocNumber(panNumber);
			header.setCustCif(customer.getCustCIF());
			header.setDocReference(this.finReference.getValue());

			String msg = Labels.getLabel("lable_Document_reverification.value", new Object[] { "PAN Number" });

			if (DocVerificationUtil.isVerified(panNumber, DocType.PAN)) {
				MessageUtil.confirm(msg, evnt -> {
					if (Messagebox.ON_YES.equals(evnt.getName())) {
						ErrorDetail err = DocVerificationUtil.doValidatePAN(header, true);
						if (err != null) {
							isPANVerified = false;
							MessageUtil.showMessage(err.getMessage());
						} else {
							isPANVerified = true;
							MessageUtil.showMessage(String.format("%s PAN validation successfull.",
									header.getDocVerificationDetail().getFullName()));
						}
					}
				});
			} else {
				ErrorDetail err = DocVerificationUtil.doValidatePAN(header, true);
				if (err != null) {
					isPANVerified = false;
					MessageUtil.showMessage(err.getMessage());
				} else {
					isPANVerified = true;
					MessageUtil.showMessage(String.format("%s PAN validation successfull.",
							header.getDocVerificationDetail().getFullName()));
				}
			}
		} catch (WrongValueException wve) {
			throw wve;
		} catch (InterfaceException ife) {
			MessageUtil.showMessage(ife.getErrorMessage());
		}
	}

	private void addExcessAmt() {
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0) {
			Listitem item = new Listitem();
			Listcell lc = null;
			item = new Listitem();
			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_ExcessType_EXCESS"));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(receiptData.getRemBal(), formatter));

			lc.setId("ExcessAmount");
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			this.listBoxPastdues.appendChild(item);
		}
	}

	private void createAllocateItem(ReceiptAllocationDetail allocate, boolean isManAdv, String desc, int idx) {
		logger.debug(Literal.ENTERING);
		String allocateMthd = getComboboxValue(this.allocationMethod);

		if (Allocation.NPFT.equals(allocate.getAllocationType())
				|| Allocation.FUT_NPFT.equals(allocate.getAllocationType())) {
			return;
		}

		Listitem item = new Listitem();
		Listcell lc = null;
		addBoldTextCell(item, allocate.getTypeDesc(), allocate.isSubListAvailable(), idx);
		if (allocate.getAllocationTo() < 0) {
			for (FinFeeDetail fee : receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
				if (allocate.getAllocationTo() == -(fee.getFeeTypeID())
						&& "PERCENTG".equals(fee.getCalculationType())) {
					lc = (Listcell) item.getChildren().get(0);
					Button button = new Button("Fee Details");
					button.setId(String.valueOf(idx));
					button.addForward("onClick", window_ReceiptDialog, "onFeeDetailsClick", button.getId());
					lc.appendChild(button);

					break;
				}
			}
		}
		addAmountCell(item, allocate.getTotRecv(), ("AllocateActualDue_" + idx), false);
		// FIXME: PV. Pending code to get in process allocations
		addAmountCell(item, allocate.getInProcess(), ("AllocateInProess_" + idx), true);
		addAmountCell(item, allocate.getDueGST(), ("AllocateCurGST_" + idx), true);
		addAmountCell(item, allocate.getTdsDue(), ("AllocateTDSDue_" + idx), true);
		addAmountCell(item, allocate.getTotalDue(), ("AllocateCurDue_" + idx), true);

		lc = new Listcell();
		CurrencyBox allocationPaid = new CurrencyBox();
		allocationPaid.setStyle("text-align:right;");
		allocationPaid.setBalUnvisible(true, true);
		setProps(allocationPaid, false, formatter, 120);
		allocationPaid.setId("AllocatePaid_" + idx);
		allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getTotalPaid(), formatter));
		allocationPaid.addForward("onFulfill", this.window_ReceiptDialog, "onAllocatePaidChange", idx);
		allocationPaid.setReadonly(true);
		lc.appendChild(allocationPaid);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// Editable Amount - Total Paid
		lc = new Listcell();
		CurrencyBox allocationNetPaid = new CurrencyBox();
		allocationNetPaid.setStyle("text-align:right;");
		allocationNetPaid.setBalUnvisible(true, true);
		setProps(allocationNetPaid, false, formatter, 120);
		allocationNetPaid.setId("AllocateNetPaid_" + idx);
		allocationNetPaid.setValue(PennantApplicationUtil.formateAmount(allocate.getPaidAmount(), formatter));
		allocationNetPaid.addForward("onFulfill", this.window_ReceiptDialog, "onAllocateNetPaidChange", idx);
		allocationNetPaid.setReadonly(true);

		lc.appendChild(allocationNetPaid);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		addAmountCell(item, allocate.getPaidGST(), ("PaidGST_" + idx), true);
		addAmountCell(item, allocate.getTdsPaid(), ("PaidTDS_" + idx), true);

		lc = new Listcell();
		CurrencyBox allocationWaived = new CurrencyBox();
		allocationWaived.setStyle("text-align:right;");
		allocationWaived.setBalUnvisible(true, true);
		setProps(allocationWaived, false, formatter, 120);
		allocationWaived.setId("AllocateWaived_" + idx);
		allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocate.getWaivedAmount(), formatter));
		allocationWaived.addForward("onFulfill", this.window_ReceiptDialog, "onAllocateWaivedChange", idx);
		allocationWaived.setReadonly(true);
		if (allocate.getAllocationTo() < 0 && PennantConstants.YES.equals(allocate.getWaiverAccepted())) {
			allocationWaived.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_WaivedAmount"));
		}

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
			String[] arg = new String[1];
			arg[0] = PennantApplicationUtil.amountFormate(allocate.getDueAmount(), formatter);
			allocationWaived.setTooltiptext(Labels.getLabel("label_WaivedAllocation_More_than_receipt", arg));
		}

		if (isForeClosure) {
			allocationWaived.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_WaivedAmount"));
			if (PennantStaticListUtil.getNoWaiverList().contains(allocate.getAllocationType())) {
				allocationWaived.setReadonly(true);
			}
		}

		lc.appendChild(allocationWaived);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		if (AllocationType.MANUAL.equals(allocateMthd)) {
			allocationNetPaid.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_PaidAmount"));
			// allocationPaid.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_PaidAmount"));
		}

		// Balance Due AMount
		addAmountCell(item, allocate.getBalance(), ("AllocateBalDue_" + idx), true);

		// if (allocate.isEditable()){
		this.listBoxPastdues.appendChild(item);
		// }

		logger.debug(Literal.LEAVING);
	}

	public void onFeeDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", receiptData);
		map.put("buttonId", buttonId);
		map.put("receiptDialogCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/EventFeeDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final Map<String, Object> map = new HashMap<String, Object>();
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
	 * Method for Adding footer to show Totals
	 * 
	 * @param dueAmount
	 * @param paidAmount
	 * @param waivedAmount
	 * @param formatter
	 * @param isPastDue
	 */
	private void addDueFooter(int formatter) {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);
		BigDecimal totRecv = BigDecimal.ZERO;
		BigDecimal totDue = BigDecimal.ZERO;
		BigDecimal totGST = BigDecimal.ZERO;
		BigDecimal inProc = BigDecimal.ZERO;
		BigDecimal totPaid = BigDecimal.ZERO;
		BigDecimal paid = BigDecimal.ZERO;
		BigDecimal paidGST = BigDecimal.ZERO;
		BigDecimal waived = BigDecimal.ZERO;
		BigDecimal waivedGST = BigDecimal.ZERO;
		BigDecimal gstAmount = BigDecimal.ZERO;
		BigDecimal tdsPaid = BigDecimal.ZERO;
		BigDecimal tdsDue = BigDecimal.ZERO;

		List<ReceiptAllocationDetail> allocList = receiptData.getReceiptHeader().getAllocationsSummary();

		if (!receiptData.isCalReq()) {
			allocList = receiptData.getReceiptHeader().getAllocations();
		}

		for (ReceiptAllocationDetail allocate : allocList) {

			if (!Allocation.EMI.equals(allocate.getAllocationType())
					&& !Allocation.NPFT.equals(allocate.getAllocationType())
					&& !Allocation.FUT_NPFT.equals(allocate.getAllocationType())) {
				totRecv = totRecv.add(allocate.getTotRecv());
				totGST = totGST.add(allocate.getDueGST());
				totDue = totDue.add(allocate.getTotalDue());
				inProc = inProc.add(allocate.getInProcess());
				totPaid = totPaid.add(allocate.getTotalPaid());
				paid = paid.add(allocate.getPaidAmount());
				paidGST = paidGST.add(allocate.getPaidGST());
				waived = waived.add(allocate.getWaivedAmount());
				tdsDue = tdsDue.add(allocate.getTdsDue());
				tdsPaid = tdsPaid.add(allocate.getTdsPaid());

			}

		}
		receiptData.setPaidNow(paid);
		addAmountCell(item, totRecv, null, true);
		addAmountCell(item, inProc, null, true);
		addAmountCell(item, totGST, null, true);
		addAmountCell(item, tdsDue, null, true);
		addAmountCell(item, totDue, null, true);
		addAmountCell(item, totPaid, null, true);
		addAmountCell(item, paid, null, true);
		addAmountCell(item, paidGST, null, true);
		addAmountCell(item, tdsPaid, null, true);
		addAmountCell(item, waived.subtract(gstAmount), null, true);
		addAmountCell(item, totDue.subtract(paid).subtract(waived), null, true);

		this.listBoxPastdues.appendChild(item);
	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 */
	public void onAllocatePaidChange(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		BigDecimal tds = BigDecimal.ZERO;
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocatePaid_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationPaid = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(allocationPaid.getValidateValue(), formatter);
		BigDecimal dueAmount = allocate.getTotRecv().subtract(allocate.getInProcess());
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
			dueAmount = dueAmount.add(allocate.getDueGST());
		}
		BigDecimal waivedAmount = rch.getAllocationsSummary().get(idx).getWaivedAmount();
		if (paidAmount.compareTo(dueAmount.subtract(waivedAmount)) > 0) {
			paidAmount = dueAmount.subtract(waivedAmount);
		}

		allocate.setTotalPaid(paidAmount);
		allocate.setPaidAmount(paidAmount);

		BigDecimal excGst = getReceiptCalculator().getExclusiveGSTAmount(allocate, paidAmount);
		if (allocate.isTdsReq()) {
			tds = getReceiptCalculator()
					.getTDSAmount(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), excGst);
		}
		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitAllocSummary(receiptData, idx);
		} else {
			if (Allocation.EMI.equals(allocate.getAllocationType())) {
				allocateEmi(paidAmount);
			} else if (Allocation.PFT.equals(allocate.getAllocationType())) {
				allocateNPft(paidAmount);
			} else if (Allocation.PRI.equals(allocate.getAllocationType())) {
				allocatePRI(paidAmount);
			} else {
				for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
					if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
							&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
						allocteDtl.setTotalPaid(paidAmount);
						allocteDtl.setPaidAmount(paidAmount.subtract(tds));
						allocteDtl.setTdsPaid(tds);
						// GST Calculation(always paid amount we are taking the
						// inclusive type here because we are doing reverse
						// calculation here)
						if (allocteDtl.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
							allocteDtl.setPaidCGST(BigDecimal.ZERO);
							allocteDtl.setPaidSGST(BigDecimal.ZERO);
							allocteDtl.setPaidUGST(BigDecimal.ZERO);
							allocteDtl.setPaidIGST(BigDecimal.ZERO);
							allocteDtl.setPaidGST(BigDecimal.ZERO);
							allocteDtl.setPaidCESS(BigDecimal.ZERO);
							getReceiptCalculator().calAllocationGST(financeDetail, paidAmount, allocteDtl,
									FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
						}
					}
				}
			}
		}

		changePaid();

		// if no extra balance or partial pay disable excessAdjustTo
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void allocatePft(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal pft = receiptCalculator.getPftAmount(receiptData.getFinanceDetail().getFinScheduleData(), paidAmount,
				rch.isExcldTdsCal());
		BigDecimal pri = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				pri = allocteDtl.getPaidAmount();
			}
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (pft.compareTo(allocteDtl.getTotalDue().add(allocteDtl.getTdsDue())
						.subtract(allocteDtl.getWaivedAmount())) > 0) {
					pft = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(pft);
				allocteDtl.setPaidAmount(paidAmount);
				allocteDtl.setTdsPaid(pft.subtract(paidAmount));
				if (allocteDtl.getTdsPaid().compareTo(BigDecimal.ZERO) <= 0) {
					allocteDtl.setTdsPaid(BigDecimal.ZERO);
				}
			}

		}
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(pri.add(paidAmount));
				allocteDtl.setPaidAmount(pri.add(paidAmount));
				break;
			}

		}

	}

	private void allocateNPft(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal netPft = receiptCalculator.getNetProfit(receiptData, paidAmount);
		BigDecimal pri = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				pri = allocteDtl.getPaidAmount();
			}
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (netPft.compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					netPft = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(netPft);
				allocteDtl.setTdsPaid(paidAmount.subtract(netPft));
			}

		}
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(pri.add(netPft));
				allocteDtl.setPaidAmount(pri.add(netPft));
				break;
			}

		}

	}

	private void allocatePRI(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal npft = BigDecimal.ZERO;
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(paidAmount);
			}
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				npft = allocteDtl.getPaidAmount();
			}
		}
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(npft.add(paidAmount));
				allocteDtl.setPaidAmount(npft.add(paidAmount));
				break;
			}

		}

	}

	public void onAllocateNetPaidChange(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocateNetPaid_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationPaid = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(allocationPaid.getValidateValue(), formatter);
		BigDecimal dueAmount = rch.getAllocationsSummary().get(idx).getTotalDue();
		BigDecimal waivedAmount = rch.getAllocationsSummary().get(idx).getWaivedAmount();
		if (paidAmount.compareTo(dueAmount.subtract(waivedAmount)) > 0) {
			paidAmount = dueAmount.subtract(waivedAmount);
		}
		BigDecimal totalPaid = getReceiptCalculator().getPaidAmount(allocate, paidAmount);
		allocate.setTotalPaid(paidAmount);
		allocate.setPaidAmount(paidAmount);
		// allocate.setPaidAmount(allocate.getTotRecv());

		// GST Calculations
		if (StringUtils.isNotBlank(allocate.getTaxType())) {
			// always paid amount we are taking the inclusive type here because
			// we are doing reverse calculation here
			allocate.setPaidCGST(BigDecimal.ZERO);
			allocate.setPaidSGST(BigDecimal.ZERO);
			allocate.setPaidUGST(BigDecimal.ZERO);
			allocate.setPaidIGST(BigDecimal.ZERO);
			allocate.setPaidCESS(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			getReceiptCalculator().calAllocationPaidGST(financeDetail, totalPaid, allocate,
					FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
		}

		BigDecimal tdsPaidNow = BigDecimal.ZERO;
		if (allocate.isTdsReq()) {
			if (dueAmount.equals(paidAmount)) {
				tdsPaidNow = allocate.getTdsDue();
			} else {
				tdsPaidNow = getReceiptCalculator().getTDSAmount(financeDetail.getFinScheduleData().getFinanceMain(),
						totalPaid);
				allocate.setTdsPaid(tdsPaidNow);
			}
			allocate.setTotalPaid(totalPaid.add(tdsPaidNow));
		}

		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitNetAllocSummary(receiptData, idx);
		} else {
			if (Allocation.EMI.equals(allocate.getAllocationType())) {
				allocateEmi(paidAmount);
			} else if (Allocation.PFT.equals(allocate.getAllocationType())) {
				allocatePft(paidAmount);
			} else if (Allocation.PRI.equals(allocate.getAllocationType())) {
				allocatePRI(paidAmount);
			} else {
				for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
					if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
							&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
						allocteDtl.setTotalPaid(paidAmount.add(tdsPaidNow));
						allocteDtl.setPaidAmount(paidAmount);
						allocteDtl.setTdsPaid(tdsPaidNow);
						if (allocteDtl.getDueGST().compareTo(BigDecimal.ZERO) > 0) {
							allocteDtl.setPaidCGST(BigDecimal.ZERO);
							allocteDtl.setPaidSGST(BigDecimal.ZERO);
							allocteDtl.setPaidUGST(BigDecimal.ZERO);
							allocteDtl.setPaidIGST(BigDecimal.ZERO);
							allocteDtl.setPaidCESS(BigDecimal.ZERO);
							allocteDtl.setPaidGST(BigDecimal.ZERO);
							getReceiptCalculator().calAllocationPaidGST(financeDetail, allocteDtl.getTotalPaid(),
									allocteDtl, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
						}
					}
				}
			}
		}

		changePaid();

		// if no extra balance or partial pay disable excessAdjustTo
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void allocateEmi(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, paidAmount);
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[2].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[2] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[1]);
				allocteDtl.setPaidAmount(emiSplit[2]);
				allocteDtl.setTdsPaid(emiSplit[1].subtract(emiSplit[2]));
			}

			if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
				if (emiSplit[0].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[0] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[0]);
				allocteDtl.setPaidAmount(emiSplit[0]);
			}
			if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(paidAmount);
			}
		}

	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 */
	public void onAllocateWaivedChange(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		int idx = (int) event.getData();
		String id = "AllocateWaived_" + idx;

		boolean isEmiWaived = false;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationWaived = (CurrencyBox) this.listBoxPastdues.getFellow(id);
		BigDecimal waivedAmount = PennantApplicationUtil.unFormateAmount(allocationWaived.getValidateValue(),
				formatter);

		BigDecimal dueAmount = allocate.getTotalDue();

		if (waivedAmount.compareTo(dueAmount) > 0) {
			waivedAmount = dueAmount;
		}
		allocate.setWaivedAmount(waivedAmount);

		adjustWaiver(allocate, waivedAmount);

		BigDecimal totalPaid = getReceiptCalculator().getPaidAmount(allocate, allocate.getPaidAmount());

		if (StringUtils.isNotBlank(allocate.getTaxType())) {
			// always paid amount we are taking the inclusive type here because
			// we are doing reverse calculation here
			allocate.setPaidCGST(BigDecimal.ZERO);
			allocate.setPaidSGST(BigDecimal.ZERO);
			allocate.setPaidUGST(BigDecimal.ZERO);
			allocate.setPaidIGST(BigDecimal.ZERO);
			allocate.setPaidCESS(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			getReceiptCalculator().calAllocationPaidGST(financeDetail, totalPaid, allocate,
					FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE);
		}

		BigDecimal tdsPaidNow = BigDecimal.ZERO;
		if (allocate.isTdsReq()) {
			tdsPaidNow = getReceiptCalculator().getTDSAmount(financeDetail.getFinScheduleData().getFinanceMain(),
					totalPaid);
			allocate.setTdsPaid(tdsPaidNow);
			allocate.setTotalPaid(totalPaid.add(tdsPaidNow));
		}

		if (Allocation.PRI.equals(allocate.getAllocationType())
				|| Allocation.PFT.equals(allocate.getAllocationType())) {
			isEmiWaived = true;
		}

		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitNetAllocSummary(receiptData, idx);
		} else {
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())
						&& allocteDtl.getAllocationTo() == allocate.getAllocationTo()) {
					allocteDtl.setPaidCGST(BigDecimal.ZERO);
					allocteDtl.setPaidSGST(BigDecimal.ZERO);
					allocteDtl.setPaidIGST(BigDecimal.ZERO);
					allocteDtl.setPaidUGST(BigDecimal.ZERO);
					allocteDtl.setPaidGST(BigDecimal.ZERO);
					// Waiver GST
					allocteDtl.setWaivedCGST(BigDecimal.ZERO);
					allocteDtl.setWaivedSGST(BigDecimal.ZERO);
					allocteDtl.setWaivedIGST(BigDecimal.ZERO);
					allocteDtl.setWaivedUGST(BigDecimal.ZERO);
					allocteDtl.setWaivedGST(BigDecimal.ZERO);
					allocteDtl.setWaivedAmount(allocate.getWaivedAmount());
					allocteDtl.setPaidAmount(allocate.getPaidAmount());
					allocteDtl.setTotalPaid(allocate.getTotalPaid());
				}
			}
		}

		if (Allocation.FUT_PFT.equals(allocate.getAllocationType())) {
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
			List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
			FinanceScheduleDetail lastSchd = schdDtls.get(schdDtls.size() - 1);
			if (lastSchd.isTDSApplicable()) {
				BigDecimal pftNow = receiptCalculator.getNetOffTDS(
						receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), allocate.getPaidAmount());
				allocate.setTotalPaid(pftNow);
				allocate.setTdsPaid(pftNow.subtract(allocate.getPaidAmount()));
			} else {
				allocate.setTotalPaid(allocate.getPaidAmount());
				allocate.setTdsPaid(BigDecimal.ZERO);
			}
		}

		if (Allocation.PFT.equals(allocate.getAllocationType())) {
			BigDecimal pftPaid = receiptCalculator.getPftAmount(receiptData.getFinanceDetail().getFinScheduleData(),
					allocate.getPaidAmount(), rch.isExcldTdsCal());
			isEmiWaived = true;
			allocate.setTotalPaid(pftPaid);
			allocate.setTdsPaid(pftPaid.subtract(allocate.getPaidAmount()));

		}
		// Adjusting emi waiver
		if (isEmiWaived) {
			BigDecimal paid = BigDecimal.ZERO;
			BigDecimal waived = BigDecimal.ZERO;
			BigDecimal totPaid = BigDecimal.ZERO;
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocationsSummary()) {
				if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setPaidAmount(paid);
					allocteDtl.setTotalPaid(totPaid);
					allocteDtl.setWaivedAmount(waived);
					break;
				}
			}
			paid = BigDecimal.ZERO;
			waived = BigDecimal.ZERO;
			totPaid = BigDecimal.ZERO;
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (Allocation.PRI.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.PFT.equals(allocteDtl.getAllocationType())) {
					paid = paid.add(allocteDtl.getPaidAmount());
					totPaid = totPaid.add(allocteDtl.getTotalPaid());
					waived = waived.add(allocteDtl.getWaivedAmount());
				}
				if (Allocation.EMI.equals(allocteDtl.getAllocationType())) {
					allocteDtl.setPaidAmount(paid);
					allocteDtl.setTotalPaid(totPaid);
					allocteDtl.setWaivedAmount(waived);
					break;
				}
			}
		}

		changeWaiver();
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public ReceiptAllocationDetail adjustWaiver(ReceiptAllocationDetail allocate, BigDecimal waiverNow) {
		BigDecimal dueAmount;
		BigDecimal paidAmount;
		BigDecimal waivedAmount = allocate.getWaivedAmount();

		dueAmount = allocate.getTotalDue();
		paidAmount = allocate.getPaidAmount();
		BigDecimal balAmount = dueAmount.subtract(waivedAmount);
		if (balAmount.compareTo(BigDecimal.ZERO) == 0) {
			allocate.setPaidAmount(BigDecimal.ZERO);
			allocate.setTotalPaid(BigDecimal.ZERO);
			allocate.setTdsPaid(BigDecimal.ZERO);
		} else {
			if (balAmount.compareTo(paidAmount) <= 0) {
				paidAmount = balAmount;
				allocate.setWaivedAmount(waiverNow);
				allocate.setPaidAmount(paidAmount);
			}
		}

		// Calculate the Waiver GST values for allocations
		if (waiverNow.compareTo(BigDecimal.ZERO) > 0) {
			allocate.setPaidCGST(BigDecimal.ZERO);
			allocate.setPaidSGST(BigDecimal.ZERO);
			allocate.setPaidUGST(BigDecimal.ZERO);
			allocate.setPaidIGST(BigDecimal.ZERO);
			allocate.setPaidCESS(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			getReceiptCalculator().calAllocationWaiverGST(financeDetail, waiverNow, allocate);
		}

		return allocate;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date fromDate = rch.getValueDate();
		Date toDate = SysParamUtil.getAppDate();
		if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			fromDate = rch.getReceiptDate();
		}
		// FIXME: PV: CODE REVIEW PENDING
		if (!this.receiptPurpose.isDisabled()) {
			this.receiptPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPurpose(),
					Labels.getLabel("label_ReceiptDialog_ReceiptPurpose.value")));
		}

		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled() && this.receiptMode.isVisible()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModes(),
					Labels.getLabel("label_ReceiptDialog_ReceiptMode.value")));
		}
		if (!this.receivedFrom.isDisabled()) {
			this.receivedFrom.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceivedFrom(),
					Labels.getLabel("label_ReceiptDialog_ReceivedFrom.value")));
		}
		if (this.excessAdjustTo.isVisible() && !this.excessAdjustTo.isDisabled()) {
			this.excessAdjustTo.setConstraint(new StaticListValidator(PennantStaticListUtil.getExcessAdjustmentTypes(),
					Labels.getLabel("label_ReceiptDialog_ExcessAdjustTo.value")));
		}
		if (!this.allocationMethod.isDisabled()) {
			this.allocationMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getAllocationMethods(),
					Labels.getLabel("label_ReceiptDialog_AllocationMethod.value")));
		}
		if (this.effScheduleMethod.isVisible() && !this.effScheduleMethod.isDisabled()) {
			this.effScheduleMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getEarlyPayEffectOn(),
					Labels.getLabel("label_ReceiptDialog_EffecScheduleMethod.value")));
		}

		if (this.row_RealizationDate.isVisible() && !this.realizationDate.isDisabled()) {
			this.realizationDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_ReceiptRealizationDialog_RealizationDate.value"), true,
							rch.getDepositDate(), toDate, true));
		}

		if (ReceiptMode.CHEQUE.equals(recptMode)) {

			if (!this.chequeAcNo.isReadonly()) {
				this.chequeAcNo.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptDialog_ChequeAccountNo.value"), null, false));
			}
		}

		/*
		 * if (!ReceiptMode.EXCESS.equals(recptMode)) { if (FinanceConstants.DEPOSIT_MAKER.equals(module) &&
		 * (ReceiptMode.CHEQUE.equals(recptMode) || ReceiptMode.DD.equals(recptMode))) { if
		 * (!this.fundingAccount.isReadonly()) { this.fundingAccount.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true)); } } else if
		 * (FinanceConstants.RECEIPT_MAKER.equals(module) && (!ReceiptMode.CHEQUE.equals(recptMode) &&
		 * !ReceiptMode.DD.equals(recptMode) && !ReceiptMode.CASH.equals(recptMode))) { if
		 * (!this.fundingAccount.isReadonly()) { this.fundingAccount.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true)); } } else if
		 * (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE && ReceiptMode.CASH.equals(recptMode)) {
		 * if (!this.fundingAccount.isReadonly()) { this.fundingAccount.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true)); } }
		 * 
		 * 
		 * if (!this.receivedDate.isDisabled()) { Date prvMaxReceivedDate =
		 * getReceiptService().getMaxReceiptDate(financeMain.getFinReference ());
		 * 
		 * // ### 26-09-2018 Ticket id :124998 if (prvMaxReceivedDate == null || receiptPurposeCtg == 0) {
		 * prvMaxReceivedDate = financeMain.getFinStartDate(); } Date curBussDate = DateUtility.getAppDate(); if
		 * (DateUtility.compare(prvMaxReceivedDate, curBussDate) > 0) { curBussDate = prvMaxReceivedDate; }
		 * this.receivedDate .setConstraint(new PTDateValidator(Labels.getLabel(
		 * "label_ReceiptDialog_ReceivedDate.value"), true, prvMaxReceivedDate, curBussDate, true)); }
		 * 
		 * }
		 */

		if (!this.collectionAgentId.isReadonly() && this.collectionAgentId.isVisible()) {
			this.collectionAgentId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_CollectionAgentId.value"),
							null, collectionAgentId.isMandatory(), true));
		}
		if (ReceiptMode.DD.equals(recptMode) || ReceiptMode.CHEQUE.equals(recptMode)) {

			if (!this.favourNo.isReadonly()) {
				String label = Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value");
				if (ReceiptMode.DD.equals(recptMode)) {
					label = Labels.getLabel("label_ReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(
						new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}

			if (!this.valueDate.isDisabled()) {
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ValueDate.value"),
						true, financeMain.getFinStartDate(), SysParamUtil.getAppDate(), true));
			}

			if (!this.bankCode.isReadonly()) {
				this.bankCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptDialog_IssuingBank.value"), null, true, true));
			}

			if (!this.favourName.isReadonly()) {
				this.favourName
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_favourName.value"),
								PennantRegularExpressions.REGEX_FAVOURING_NAME, true));
			}

			if (!this.depositDate.isReadonly()) {
				this.depositDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_DepositDate.value"),
								true, this.receivedDate.getValue(), SysParamUtil.getAppDate(), true));
			}

			if (!this.depositNo.isReadonly()) {
				this.depositNo
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_depositNo.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		if (ReceiptMode.ONLINE.equals(recptMode)) {

			if (!this.transactionRef.isReadonly()) {
				this.transactionRef
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_tranReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
		}

		if (this.row_CustomerAccount.isVisible() && !this.customerBankAcct.isReadonly()) {
			this.customerBankAcct.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReceiptDialog_CustAccount.value"), null, true, true));
		}

		if (!ReceiptMode.EXCESS.equals(recptMode)) {
			if (!this.paymentRef.isReadonly()) {
				this.paymentRef.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptDialog_paymentReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		/*
		 * if (!this.remarks.isReadonly()) { this.remarks.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_ReceiptDialog_Remarks.value" ),
		 * PennantRegularExpressions.REGEX_DESCRIPTION, true)); }
		 */

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

		if (!this.closureType.isReadonly()) {
			this.closureType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ClosureTypes.value"), null, isClosureTypeMandatory));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.receiptPurpose.setConstraint("");
		this.receiptMode.setConstraint("");
		this.excessAdjustTo.setConstraint("");
		this.allocationMethod.setConstraint("");
		this.effScheduleMethod.setConstraint("");
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
		this.customerBankAcct.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.receiptPurpose.setErrorMessage("");
		this.receiptMode.setErrorMessage("");
		this.excessAdjustTo.setErrorMessage("");
		this.allocationMethod.setErrorMessage("");
		this.effScheduleMethod.setErrorMessage("");
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
		this.customerBankAcct.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * 
	 * @return
	 */
	private void doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING

		ArrayList<WrongValueException> wve = new ArrayList<>();
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		int finFormatter = CurrencyUtil.getFormat(fm.getFinCcy());

		Date curBussDate = SysParamUtil.getAppDate();
		FinReceiptHeader header = receiptData.getReceiptHeader();
		// header.setReceiptDate(curBussDate);
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setFinID(fm.getFinID());
		header.setReference(fm.getFinReference());

		if (this.row_knockOff_Type.isVisible()) {
			if (isKnockOff) {
				header.setKnockOffType(KnockOffType.getCode(this.knockOffType.getValue()));
			}
		}

		try {
			header.setReceiptPurpose(getComboboxValue(receiptPurpose));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * try { header.setSubReceiptMode(getComboboxValue(receiptType)); } catch (WrongValueException we) {
		 * wve.add(we); }
		 */
		if (!RepayConstants.PAYTYPE_PRESENTMENT.equals(header.getReceiptMode())) {
			try {
				if (isForeClosure && receiptData.getTotReceiptAmount().compareTo(BigDecimal.ZERO) == 0) {
					header.setReceiptMode(ReceiptMode.ZERORECEIPT);
				} else {
					header.setReceiptMode(getComboboxValue(receiptMode));
					if ("#".equals(header.getSubReceiptMode())) {
						header.setSubReceiptMode(header.getReceiptMode());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (excessAdjustTo.isVisible()) {
			try {
				header.setExcessAdjustTo(getComboboxValue(excessAdjustTo));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			header.setAllocationType(getComboboxValue(allocationMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!isForeClosure) {
				header.setReceiptAmount(
						PennantApplicationUtil.unFormateAmount(receiptAmount.getValidateValue(), finFormatter));
				if (isEarlySettle) {
					header.setReceiptAmount(header.getReceiptAmount().add(receiptData.getExcessAvailable()));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setTdsAmount(PennantApplicationUtil.unFormateAmount(tDSAmount.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.closureType.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (effScheduleMethod.isVisible() && !this.effScheduleMethod.isDisabled()) {
			try {
				header.setEffectSchdMethod(getComboboxValue(effScheduleMethod));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			header.setReceivedDate(this.receivedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setCustAcctNumber(this.customerBankAcct.getValue());
			header.setCustAcctHolderName(this.customerBankAcct.getDescription());
			Object object = this.customerBankAcct.getAttribute("CustBankId");
			if (object != null) {
				header.setCustBankId(Long.parseLong(object.toString()));
			} else {
				header.setCustBankId(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// if no right given to receiptModeStatus component will be read only mode.
		// to enable the receiptModeStatus component we are forcing to give right to combobox in knockoff cancel maker
		// screen
		try {
			if ((FinanceConstants.KNOCKOFFCAN_MAKER.equals(module))) {
				if (!isReadOnly("ReceiptDialog_receiptModeStatus") && !this.receiptModeStatus.isDisabled()
						&& this.receiptModeStatus.isVisible()) {
					if ("#".equals(getComboboxValue(this.receiptModeStatus))) {
						throw new WrongValueException(this.receiptModeStatus,
								Labels.getLabel("label_ReceiptDialog_ReceiptModeStatus.value") + " is mandatory");
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		String status = "";
		if (row_ReceiptModeStatus.isVisible() && !isReadOnly("ReceiptDialog_receiptModeStatus")) {
			try {
				if (isValidComboValue(this.receiptModeStatus,
						Labels.getLabel("label_ReceiptDialog_ReceiptModeStatus.value"))) {

					status = getComboboxValue(receiptModeStatus);
					header.setReceiptModeStatus(status);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (!RepayConstants.PAYSTATUS_CANCEL.equals(header.getReceiptModeStatus())) {
			try {
				header.setReceiptDate(curBussDate);
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {
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

			FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(PennantConstants.FEETYPE_BOUNCE);
			bounce.setAdviseType(AdviseType.RECEIVABLE.id());
			bounce.setFinID(fm.getFinID());
			bounce.setFinReference(header.getReference());
			bounce.setFeeTypeID(feeType.getFeeTypeID());
			bounce.setSequence(0);

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
				this.bounceCode.getValidatedValue();
				Object object = this.bounceCode.getAttribute("BounceId");
				if (object != null) {
					bounce.setBounceID(NumberUtils.toLong(object.toString()));
				} else {
					bounce.setBounceID(0);
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}

			bounce.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			bounce.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			bounce.setVersion(bounce.getVersion() + 1);

			header.setManualAdvise(bounce);
		} else if (RepayConstants.PAYSTATUS_CANCEL.equals(status)) {

			try {
				header.setCancelReason(this.cancelReason.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}

			try {
				header.setCancelRemarks(this.cancelRemarks.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}

		} else if (RepayConstants.PAYSTATUS_REALIZED.equals(status)) {

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
			this.fundingAccount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			/*
			 * validateReceivedDate(); this.receivedDate.getValue();
			 */
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			Object reasonCodeData = this.earlySettlementReason.getObject();
			if (reasonCodeData != null && reasonCodeData != "") {
				header.setReasonCode(((ReasonCode) reasonCodeData).getId());
			}

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
			if (StringUtils.isNotEmpty(this.collectionAgentId.getValue())) {
				header.setCollectionAgentId(Long.valueOf(this.collectionAgentId.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (receivedFrom.isVisible() && !receivedFrom.isDisabled()) {
				header.setReceivedFrom(getComboboxValue(receivedFrom));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setCashierBranch(this.cashierBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			header.setExtReference(this.externalRefrenceNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// RecAppDate
		if (header.getRecAppDate() == null) {
			header.setRecAppDate(curBussDate);
		}

		try {
			header.setSourceofFund(getComboboxValue(this.sourceofFund));
		} catch (WrongValueException we) {
			wve.add(we);
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

		logger.debug(Literal.LEAVING);
	}

	private void validateReceivedDate() {
		logger.debug(Literal.ENTERING);
		Date appDate = SysParamUtil.getAppDate();
		// Date receivedDate = this.receivedDate.getValue();
		Date curMonthStartDate = DateUtil.getMonthStart(appDate);
		Date currentMonthScheduleDate = null;
		// FIXME: PV: CODE REVIEW PENDING
		// Get the current month schedule date
		List<FinanceScheduleDetail> financeScheduleDetails = receiptData.getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails();
		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
			if ((DateUtil.getMonth(appDate) == DateUtil.getMonth(curSchd.getSchDate()))
					&& (DateUtil.getYear(appDate) == DateUtil.getYear(curSchd.getSchDate()))
					&& (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate())) {
				currentMonthScheduleDate = curSchd.getSchDate();
			}
		}
		// validate the received date with the schedule date
		/*
		 * if (!StringUtils.equals(this.receiptPurpose.getSelectedItem().getValue() .toString(),
		 * FinanceConstants.FINSER_EVENT_SCHDRPY)) { if (receivedDate != null && currentMonthScheduleDate != null &&
		 * currentMonthScheduleDate.before(appDate) && (DateUtility.compare(receivedDate, currentMonthScheduleDate) <
		 * 0)) { throw new WrongValueException(this.receivedDate, Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL", new
		 * String[] { Labels.getLabel("label_ReceiptDialog_ReceivedDate.value"),
		 * DateUtility.formatToShortDate(currentMonthScheduleDate), DateUtility.formatToShortDate(appDate) })); } //
		 * validate the received date with the month start date if (receivedDate != null &&
		 * (DateUtility.compare(receivedDate, curMonthStartDate) < 0)) { throw new
		 * WrongValueException(this.receivedDate, Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL", new String[] {
		 * Labels.getLabel("label_ReceiptDialog_ReceivedDate.value"), DateUtility.formatToShortDate(curMonthStartDate),
		 * DateUtility.formatToShortDate(appDate) })); } }
		 */

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Processing Checklist Details when Check list Tab selected
	 */
	public void onSelectCheckListDetailsTab(ForwardEvent event)
			throws ParseException, InterruptedException, IllegalAccessException, InvocationTargetException {
		this.doWriteComponentsToBean();

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(
					receiptData.getFinanceDetail().getCheckList(), receiptData.getFinanceDetail().getFinanceCheckList(),
					false);
		}

	}

	/**
	 * Method for Processing Agreement Details when Agreement list Tab selected
	 */
	public void onSelectAgreementDetailTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException {
		this.doWriteComponentsToBean();

		// refresh template tab
		if (getAgreementDetailDialogCtrl() != null) {
			getAgreementDetailDialogCtrl().doSetLabels(getFinBasicDetails());
			getAgreementDetailDialogCtrl().doShowDialog(false);
		}
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public FinanceDetail onExecuteStageAccDetail() {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
			getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

			// Finance Accounting Details Execution
			executeAccounting(true);
		} else {
			receiptData.getFinanceDetail().setModuleDefiner(FinServiceEvent.RECEIPT);
		}

		logger.debug(Literal.LEAVING);
		return receiptData.getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(finMain.getFinID());
		Date dateValueDate = SysParamUtil.getAppDate();
		/*
		 * if (this.receivedDate.getValue() != null) { dateValueDate = this.receivedDate.getValue(); }
		 */

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain,
				receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), profitDetail,
				eventCode, dateValueDate, dateValueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		accrualService.calProfitDetails(finMain,
				receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), newProfitDetail,
				dateValueDate);
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();

		// For Bajaj, It should be always positive
		BigDecimal pftchg = totalPftSchdNew.subtract(totalPftSchdOld);
		if (pftchg.compareTo(BigDecimal.ZERO) < 0) {
			pftchg = pftchg.negate();
		}
		amountCodes.setPftChg(pftchg);

		List<ReturnDataSet> returnSetEntries = new ArrayList<>();
		BigDecimal totRpyPri = BigDecimal.ZERO;
		boolean feesExecuted = false;
		boolean pftChgExecuted = false;
		List<FinReceiptDetail> receiptDetails = receiptData.getReceiptHeader().getReceiptDetails();

		boolean payableLoopProcess = false;
		int rcptSize = receiptDetails.size();
		Map<String, BigDecimal> feeMap = new HashMap<>();
		BigDecimal totPayable = BigDecimal.ZERO;

		for (int rcpt = 0; rcpt < rcptSize; rcpt++) {
			FinReceiptDetail rcd = receiptDetails.get(rcpt);

			String feeTypeCode = rcd.getFeeTypeCode();

			if (!payableLoopProcess
					&& !FinServiceEvent.EARLYSETTLE.equals(receiptData.getReceiptHeader().getReceiptPurpose())) {
				feeMap = new HashMap<>();
				totPayable = BigDecimal.ZERO;
			}

			addZeroifNotContains(feeMap, "PA_ReceiptAmount");
			addZeroifNotContains(feeMap, "EX_ReceiptAmount");
			addZeroifNotContains(feeMap, "EA_ReceiptAmount");
			addZeroifNotContains(feeMap, "PB_ReceiptAmount");
			addZeroifNotContains(feeMap, "EAI_ReceiptAmount");
			addZeroifNotContains(feeMap, "EAE_ReceiptAmount");
			addZeroifNotContains(feeMap, "ET_ReceiptAmount");

			addZeroifNotContains(feeMap, feeTypeCode + "_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_SGST_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_IGST_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_UGST_P");
			addZeroifNotContains(feeMap, feeTypeCode + "_CESS_P");

			totPayable = totPayable.add(rcd.getAmount());
			String paymentType = rcd.getPaymentType();
			if (ReceiptMode.PAYABLE.equals(paymentType)) {
				feeMap.put("PA_ReceiptAmount", totPayable);
			} else if (ReceiptMode.EXCESS.equals(paymentType)) {
				feeMap.put("EX_ReceiptAmount", feeMap.get("EX_ReceiptAmount").add(rcd.getAmount()));
			} else if (ReceiptMode.EMIINADV.equals(paymentType)) {
				feeMap.put("EA_ReceiptAmount", feeMap.get("EA_ReceiptAmount").add(rcd.getAmount()));
			} else if (ReceiptMode.TEXCESS.equals(paymentType)) {
				feeMap.put("ET_ReceiptAmount", feeMap.get("ET_ReceiptAmount").add(rcd.getAmount()));
			} else {
				feeMap.put("PB_ReceiptAmount", feeMap.get("PB_ReceiptAmount").add(rcd.getAmount()));
			}

			if (ReceiptMode.PAYABLE.equals(rcd.getPaymentType())) {
				feeMap.put(feeTypeCode + "_P", feeMap.get(feeTypeCode + "_P").add(rcd.getAmount()));

				if (rcd.getPayAdvMovement() != null) {
					TaxHeader taxHeader = rcd.getPayAdvMovement().getTaxHeader();

					Taxes cgstTax = new Taxes();
					Taxes sgstTax = new Taxes();
					Taxes igstTax = new Taxes();
					Taxes ugstTax = new Taxes();
					Taxes cessTax = new Taxes();

					List<Taxes> taxDetails = taxHeader.getTaxDetails();

					if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							String taxType = taxes.getTaxType();

							if (RuleConstants.CODE_CGST.equals(taxType)) {
								cgstTax = taxes;
							} else if (RuleConstants.CODE_SGST.equals(taxType)) {
								sgstTax = taxes;
							} else if (RuleConstants.CODE_IGST.equals(taxType)) {
								igstTax = taxes;
							} else if (RuleConstants.CODE_UGST.equals(taxType)) {
								ugstTax = taxes;
							} else if (RuleConstants.CODE_CESS.equals(taxType)) {
								cessTax = taxes;
							}
						}
					}

					feeMap.put(feeTypeCode + "_CGST_P", feeMap.get(feeTypeCode + "_CGST_P").add(cgstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_SGST_P", feeMap.get(feeTypeCode + "_SGST_P").add(sgstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_UGST_P", feeMap.get(feeTypeCode + "_UGST_P").add(ugstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_IGST_P", feeMap.get(feeTypeCode + "_IGST_P").add(igstTax.getPaidTax()));
					feeMap.put(feeTypeCode + "_CESS_P", feeMap.get(feeTypeCode + "_CESS_P").add(cessTax.getPaidTax()));
				}
			}

			FinRepayHeader repayHeader = rcd.getRepayHeader();

			feeMap.clear();

			amountCodes.setPenaltyPaid(BigDecimal.ZERO);
			amountCodes.setPenaltyWaived(BigDecimal.ZERO);
			amountCodes.setPaymentType(rcd.getPaymentType());
			amountCodes.setUserBranch(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());

			// FIXME: FIND THE LOGIC TO SET payableLoopProcess = false; AS PER
			// OLD CODE
			payableLoopProcess = false;

			if (!FinServiceEvent.SCHDRPY.equals(repayHeader.getFinEvent())
					&& !FinServiceEvent.EARLYRPY.equals(repayHeader.getFinEvent())
					&& !FinServiceEvent.EARLYSETTLE.equals(repayHeader.getFinEvent())) {

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountingEvent.REPAY);
				amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
				amountCodes.setToExcessAmt(BigDecimal.ZERO);
				amountCodes.setToEmiAdvance(BigDecimal.ZERO);
				if (RepayConstants.EXCESSADJUSTTO_EXCESS.equals(repayHeader.getFinEvent())) {
					amountCodes.setToExcessAmt(repayHeader.getRepayAmount());
				} else {
					amountCodes.setToEmiAdvance(repayHeader.getRepayAmount());
				}

				aeEvent.getAcSetIDList().clear();
				aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, AccountingEvent.REPAY));

				Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

				if (!feesExecuted
						&& FinServiceEvent.SCHDRPY.equals(receiptData.getReceiptHeader().getReceiptPurpose())) {
					feesExecuted = true;
					prepareFeeRulesMap(amountCodes, dataMap, rcd.getPaymentType());
				}

				// Receipt Detail external usage Fields Insertion into
				// DataMap
				dataMap.putAll(feeMap);

				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				engineExecution.getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

				amountCodes.setToExcessAmt(BigDecimal.ZERO);
				amountCodes.setToEmiAdvance(BigDecimal.ZERO);

				continue;
			}

			List<RepayScheduleDetail> repaySchdList = repayHeader.getRepayScheduleDetails();
			BigDecimal penaltyCGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltySGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltyIGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltyUGSTPaid = BigDecimal.ZERO;
			BigDecimal penaltyCESSPaid = BigDecimal.ZERO;

			// Penalty Waiver GST Details
			BigDecimal penaltyCGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltySGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltyIGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltyUGSTWaived = BigDecimal.ZERO;
			BigDecimal penaltyCESSWaived = BigDecimal.ZERO;

			for (RepayScheduleDetail rsd : repaySchdList) {

				// Set Repay Amount Codes
				amountCodes.setRpTot(amountCodes.getRpTot().add(rsd.getPrincipalSchdPayNow())
						.add(rsd.getProfitSchdPayNow()).add(rsd.getLatePftSchdPayNow()));
				amountCodes.setRpPft(
						amountCodes.getRpPft().add(rsd.getProfitSchdPayNow()).add(rsd.getLatePftSchdPayNow()));
				amountCodes.setRpPri(amountCodes.getRpPri().add(rsd.getPrincipalSchdPayNow()));
				amountCodes.setRpTds(amountCodes.getRpTds().add(rsd.getTdsSchdPayNow()));
				totRpyPri = totRpyPri.add(rsd.getPrincipalSchdPayNow());

				// Penalties
				amountCodes.setPenaltyPaid(amountCodes.getPenaltyPaid().add(rsd.getPenaltyPayNow()));
				amountCodes.setPenaltyWaived(amountCodes.getPenaltyWaived().add(rsd.getWaivedAmt()));

				TaxHeader taxHeader = rsd.getTaxHeader();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						if (RuleConstants.CODE_CGST.equals(taxes.getTaxType())) {
							penaltyCGSTPaid = penaltyCGSTPaid.add(taxes.getPaidTax());
							penaltyCGSTWaived = penaltyCGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_SGST.equals(taxes.getTaxType())) {
							penaltySGSTPaid = penaltySGSTPaid.add(taxes.getPaidTax());
							penaltySGSTWaived = penaltySGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_IGST.equals(taxes.getTaxType())) {
							penaltyIGSTPaid = penaltyIGSTPaid.add(taxes.getPaidTax());
							penaltyIGSTWaived = penaltyIGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_UGST.equals(taxes.getTaxType())) {
							penaltyUGSTPaid = penaltyUGSTPaid.add(taxes.getPaidTax());
							penaltyUGSTWaived = penaltyUGSTWaived.add(taxes.getWaivedTax());
						} else if (RuleConstants.CODE_CESS.equals(taxes.getTaxType())) {
							penaltyCESSPaid = penaltyCESSPaid.add(taxes.getPaidTax());
							penaltyCESSWaived = penaltyCESSWaived.add(taxes.getWaivedTax());
						}
					}
				}

				// Fee Details
				amountCodes.setSchFeePay(amountCodes.getSchFeePay().add(rsd.getSchdFeePayNow()));

				// Waived Amounts
				amountCodes.setPriWaived(amountCodes.getPriWaived().add(rsd.getPriSchdWaivedNow()));
				amountCodes.setPftWaived(amountCodes.getPftWaived().add(rsd.getPftSchdWaivedNow()));
				amountCodes.setLpiWaived(amountCodes.getLpiWaived().add(rsd.getLatePftSchdWaivedNow()));
				amountCodes.setFeeWaived(amountCodes.getFeeWaived().add(rsd.getSchdFeeWaivedNow()));
			}

			amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());

			// If Payable Continue for All Advises
			if (payableLoopProcess) {
				continue;
			}

			// Accrual & Future Paid Details
			if (FinServiceEvent.EARLYSETTLE.equals(repayHeader.getFinEvent())) {

				int schSize = receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size();
				FinanceScheduleDetail lastSchd = receiptData.getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails().get(schSize - 1);

				FinanceScheduleDetail oldLastSchd = null;
				if (lastSchd.isFrqDate()) {
					oldLastSchd = getFinanceDetailService().getFinSchduleDetails(finMain.getFinID(),
							lastSchd.getSchDate());
				}

				// If Final Schedule not exists on Approved Schedule details
				if (oldLastSchd == null) {
					// Last Schedule Interest Amounts Paid
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setLastSchPftPaid(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())
								.subtract(amountCodes.getPftWaived()));
					}

					// Last Schedule Interest Amounts Waived
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setLastSchPftWaived(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()));
					} else {
						amountCodes.setLastSchPftWaived(amountCodes.getPftWaived());
					}

					// Profit Due Paid
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setPftDuePaid(amountCodes.getRpPft());
					} else {
						amountCodes.setPftDuePaid(amountCodes.getRpPft()
								.subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid()))
								.add(amountCodes.getPftWaived()));
					}

					// Profit Due Waived
					if (amountCodes.getPftWaived()
							.compareTo(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())) > 0) {
						amountCodes.setPftDueWaived(amountCodes.getPftWaived()
								.subtract(lastSchd.getProfitSchd().subtract(lastSchd.getSchdPftPaid())));
					} else {
						amountCodes.setPftDueWaived(BigDecimal.ZERO);
					}

					// Principal Due Paid
					if (amountCodes.getPriWaived()
							.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
						amountCodes.setPriDuePaid(amountCodes.getRpPri());
					} else {
						amountCodes.setPriDuePaid(amountCodes.getRpPri()
								.subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()))
								.add(amountCodes.getPriWaived()));
					}

					// Principal Due Waived
					if (amountCodes.getPriWaived()
							.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
						amountCodes.setPriDueWaived(amountCodes.getPriWaived()
								.subtract(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())));
					} else {
						amountCodes.setPriDueWaived(BigDecimal.ZERO);
					}
				} else {

					// Last Schedule Interest Amounts Paid
					amountCodes.setLastSchPftPaid(BigDecimal.ZERO);
					amountCodes.setLastSchPftWaived(BigDecimal.ZERO);

					// Profit Due Paid
					amountCodes.setPftDuePaid(amountCodes.getRpPft());

					// Profit Due Waived
					amountCodes.setPftDueWaived(amountCodes.getPftWaived());

					BigDecimal lastSchdPriBal = lastSchd.getPrincipalSchd()
							.subtract(oldLastSchd.getPrincipalSchd().subtract(oldLastSchd.getSchdPriPaid()));

					// Principal Due Paid
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setPriDuePaid(amountCodes.getRpPri());
					} else {
						amountCodes.setPriDuePaid(
								amountCodes.getRpPri().subtract(lastSchdPriBal).add(amountCodes.getPriWaived()));
					}

					// Principal Due Waived
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setPriDueWaived(amountCodes.getPriWaived().subtract(lastSchdPriBal));
					} else {
						amountCodes.setPriDueWaived(BigDecimal.ZERO);
					}
				}

				Date curMonthStartDate = DateUtil.getMonthStart(lastSchd.getSchDate());

				// UnAccrual Calculation
				BigDecimal unaccrue = BigDecimal.ZERO;

				// Without Recalculation of Unaccrue to make exact value , subtracting total previous month accrual from
				// actual total profit
				unaccrue = totalPftSchdNew.subtract(newProfitDetail.getAmzTillLBD());

				// UnAccrue Paid
				if (amountCodes.getPftWaived().compareTo(unaccrue) > 0) {
					amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
				} else {
					amountCodes.setUnAccruedPaid(unaccrue.subtract(amountCodes.getPftWaived()));
				}

				// UnAccrue Waived
				if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
					amountCodes.setUnAccrueWaived(unaccrue);
				} else {
					amountCodes.setUnAccrueWaived(amountCodes.getPftWaived());
				}

				// Accrual Calculation
				if (DateUtil.compare(curMonthStartDate, finMain.getFinStartDate()) <= 0) {
					amountCodes.setAccruedPaid(BigDecimal.ZERO);
					amountCodes.setAccrueWaived(BigDecimal.ZERO);
				} else {
					BigDecimal totalUnAccrue = amountCodes.getUnAccrueWaived().add(amountCodes.getUnAccruedPaid());
					BigDecimal totalAccrue = amountCodes.getRpPft().subtract(amountCodes.getUnAccruedPaid());

					// Accrual Paid
					if (amountCodes.getPftWaived().compareTo(totalUnAccrue) >= 0) {
						amountCodes.setAccruedPaid(totalAccrue.add(totalUnAccrue).subtract(amountCodes.getPftWaived()));
					} else {
						amountCodes.setAccruedPaid(totalAccrue);
					}

					// Accrual Waived
					if (amountCodes.getPftWaived().compareTo(totalUnAccrue) >= 0) {
						amountCodes.setAccrueWaived(amountCodes.getPftWaived().subtract(totalUnAccrue));
					} else {
						amountCodes.setAccrueWaived(BigDecimal.ZERO);
					}
				}

				// Future Principal Paid
				if (amountCodes.getPriWaived()
						.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
					amountCodes.setFuturePriPaid(BigDecimal.ZERO);
				} else {
					amountCodes.setFuturePriPaid(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())
							.subtract(amountCodes.getPriWaived()));
				}

				// Future Principal Waived
				if (amountCodes.getPriWaived()
						.compareTo(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid())) > 0) {
					amountCodes.setFuturePriWaived(lastSchd.getPrincipalSchd().subtract(lastSchd.getSchdPriPaid()));
				} else {
					amountCodes.setFuturePriWaived(amountCodes.getPriWaived());
				}

				if (TDSCalculator.isTDSApplicable(finMain)) {
					// TDS for Last Installment
					BigDecimal tdsPerc = new BigDecimal(
							SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					amountCodes.setLastSchTds((amountCodes.getLastSchPftPaid().multiply(tdsPerc))
							.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));

					// Splitting TDS amount into Accrued and Unaccrued Paid basis
					if (amountCodes.getAccruedPaid().compareTo(BigDecimal.ZERO) > 0) {

						BigDecimal accrueTds = (amountCodes.getAccruedPaid().multiply(tdsPerc))
								.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
						BigDecimal unaccrueTds = amountCodes.getLastSchTds().subtract(accrueTds);

						amountCodes.setAccruedTds(accrueTds);
						amountCodes.setUnAccruedTds(unaccrueTds);

					} else {
						amountCodes.setAccruedTds(BigDecimal.ZERO);
						amountCodes.setUnAccruedTds(amountCodes.getLastSchTds());
					}

					// TDS Due
					amountCodes.setDueTds((amountCodes.getPftDuePaid().multiply(tdsPerc))
							.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));
				} else {
					amountCodes.setLastSchTds(BigDecimal.ZERO);
					amountCodes.setDueTds(BigDecimal.ZERO);
				}

			}

			// Accounting Event Code Setting
			aeEvent.getAcSetIDList().clear();
			if (FinServiceEvent.SCHDRPY.equals(repayHeader.getFinEvent())) {
				eventCode = AccountingEvent.REPAY;
			} else if (FinServiceEvent.EARLYRPY.equals(repayHeader.getFinEvent())) {
				eventCode = AccountingEvent.EARLYPAY;
				if (pftChgExecuted) {
					amountCodes.setPftChg(BigDecimal.ZERO);
				}
				pftChgExecuted = true;
			} else if (FinServiceEvent.EARLYSETTLE.equals(repayHeader.getFinEvent())) {
				eventCode = AccountingEvent.EARLYSTL;
				if (pftChgExecuted) {
					amountCodes.setPftChg(BigDecimal.ZERO);
				}
				pftChgExecuted = true;
			}

			aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, eventCode));

			aeEvent.setAccountingEvent(eventCode);

			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

			// Receipt Detail external usage Fields Insertion into DataMap
			dataMap.putAll(feeMap);

			String receiptPurpose = receiptData.getReceiptHeader().getReceiptPurpose();
			if (!feesExecuted && (FinServiceEvent.SCHDRPY.equals(receiptPurpose)
					|| (!FinServiceEvent.SCHDRPY.equals(receiptPurpose)
							&& repayHeader.getFinEvent().equals(receiptPurpose)))) {
				feesExecuted = true;
				prepareFeeRulesMap(amountCodes, dataMap, rcd.getPaymentType());
			}
			aeEvent.setDataMap(dataMap);
			engineExecution.getAccEngineExecResults(aeEvent);
			returnSetEntries.addAll(aeEvent.getReturnDataSet());

			if (amountCodes.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0
					|| amountCodes.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {

				// Get LPP Receivable for Accounting
				FinTaxReceivable taxRcv = getReceiptService().getTaxReceivable(finMain.getFinID(), "LPP");
				if (taxRcv != null) {

					if (taxRcv.getReceivableAmount()
							.compareTo(amountCodes.getPenaltyPaid().add(amountCodes.getPenaltyWaived())) < 0) {
						amountCodes.setPenaltyRcv(taxRcv.getReceivableAmount());
					} else {
						amountCodes.setPenaltyRcv(amountCodes.getPenaltyPaid().add(amountCodes.getPenaltyWaived()));
					}
				}

				if (amountCodes.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0
						|| amountCodes.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {
					aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

					// LPP GST Amount setting
					aeEvent.getDataMap().put("LPP_CGST_P", penaltyCGSTPaid);
					aeEvent.getDataMap().put("LPP_SGST_P", penaltySGSTPaid);
					aeEvent.getDataMap().put("LPP_UGST_P", penaltyIGSTPaid);
					aeEvent.getDataMap().put("LPP_IGST_P", penaltyUGSTPaid);
					aeEvent.getDataMap().put("LPP_CESS_P", penaltyCESSPaid);

					// GST Waivers Details
					aeEvent.getDataMap().put("LPP_CGST_W", penaltyCGSTWaived);
					aeEvent.getDataMap().put("LPP_SGST_W", penaltySGSTWaived);
					aeEvent.getDataMap().put("LPP_UGST_W", penaltyUGSTWaived);
					aeEvent.getDataMap().put("LPP_IGST_W", penaltyUGSTWaived);
					aeEvent.getDataMap().put("LPP_CESS_W", penaltyCESSWaived);

					if (taxRcv != null) {
						if (taxRcv.getCGST().compareTo(penaltyCGSTPaid) < 0) {
							aeEvent.getDataMap().put("LPP_CGST_R", taxRcv.getCGST());
						} else {
							aeEvent.getDataMap().put("LPP_CGST_R", penaltyCGSTPaid);
						}

						if (taxRcv.getSGST().compareTo(penaltySGSTPaid) < 0) {
							aeEvent.getDataMap().put("LPP_SGST_R", taxRcv.getSGST());
						} else {
							aeEvent.getDataMap().put("LPP_SGST_R", penaltySGSTPaid);
						}

						if (taxRcv.getUGST().compareTo(penaltyUGSTPaid) < 0) {
							aeEvent.getDataMap().put("LPP_UGST_R", taxRcv.getUGST());
						} else {
							aeEvent.getDataMap().put("LPP_UGST_R", penaltyUGSTPaid);
						}

						if (taxRcv.getIGST().compareTo(penaltyIGSTPaid) < 0) {
							aeEvent.getDataMap().put("LPP_IGST_R", taxRcv.getIGST());
						} else {
							aeEvent.getDataMap().put("LPP_IGST_R", penaltyIGSTPaid);
						}

						if (taxRcv.getCESS().compareTo(penaltyCESSPaid) < 0) {
							aeEvent.getDataMap().put("LPP_CESS_R", taxRcv.getCESS());
						} else {
							aeEvent.getDataMap().put("LPP_CESS_R", penaltyCESSPaid);
						}
					}
				}

				aeEvent.getAcSetIDList().clear();

				aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, AccountingEvent.LATEPAY));

				aeEvent.setAccountingEvent(AccountingEvent.LATEPAY);
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

				// LPP GST Amount setting
				aeEvent.getDataMap().put("LPP_CGST_P", penaltyCGSTPaid);
				aeEvent.getDataMap().put("LPP_SGST_P", penaltySGSTPaid);
				aeEvent.getDataMap().put("LPP_UGST_P", penaltyIGSTPaid);
				aeEvent.getDataMap().put("LPP_IGST_P", penaltyUGSTPaid);
				aeEvent.getDataMap().put("LPP_CESS_P", penaltyCESSPaid);

				// GST Waivers Details
				aeEvent.getDataMap().put("LPP_CGST_W", penaltyCGSTWaived);
				aeEvent.getDataMap().put("LPP_SGST_W", penaltySGSTWaived);
				aeEvent.getDataMap().put("LPP_UGST_W", penaltyUGSTWaived);
				aeEvent.getDataMap().put("LPP_IGST_W", penaltyUGSTWaived);
				aeEvent.getDataMap().put("LPP_CESS_W", penaltyCESSWaived);

				engineExecution.getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());
			}

			// Reset Payment Details
			amountCodes.setRpTot(BigDecimal.ZERO);
			amountCodes.setRpPft(BigDecimal.ZERO);
			amountCodes.setRpPri(BigDecimal.ZERO);
			amountCodes.setSchFeePay(BigDecimal.ZERO);
			amountCodes.setPriWaived(BigDecimal.ZERO);
			amountCodes.setPftWaived(BigDecimal.ZERO);
			amountCodes.setLpiWaived(BigDecimal.ZERO);
			amountCodes.setFeeWaived(BigDecimal.ZERO);
			amountCodes.setPenaltyPaid(BigDecimal.ZERO);
			amountCodes.setPenaltyWaived(BigDecimal.ZERO);
			amountCodes.setRpTds(BigDecimal.ZERO);
			amountCodes.setAccruedPaid(BigDecimal.ZERO);
			amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
			amountCodes.setFuturePriPaid(BigDecimal.ZERO);

			// Manual Advise Postings
			List<ManualAdviseMovements> movements = rcd.getAdvMovements();
			if (movements != null && !movements.isEmpty()) {

				// Summing Same Type of Fee Types to Single Field
				Map<String, BigDecimal> movementMap = new HashMap<>();
				for (int i = 0; i < movements.size(); i++) {
					ManualAdviseMovements movement = movements.get(i);

					// Bounce Charges
					BigDecimal amount = BigDecimal.ZERO;
					String keyCode = null;
					if (StringUtils.isEmpty(movement.getFeeTypeCode())
							|| Allocation.BOUNCE.equals(movement.getFeeTypeCode())) {

						if (movementMap.containsKey("bounceChargePaid")) {
							amount = movementMap.get("bounceChargePaid");
						}
						movementMap.put("bounceChargePaid", amount.add(movement.getPaidAmount()));

						amount = BigDecimal.ZERO;
						if (movementMap.containsKey("bounceChargeWaived")) {
							amount = movementMap.get("bounceChargeWaived");
						}
						movementMap.put("bounceChargeWaived", amount.add(movement.getWaivedAmount()));
						keyCode = "bounceCharge";
					} else {

						// Receivable Advises
						if (movementMap.containsKey(movement.getFeeTypeCode() + "_P")) {
							amount = movementMap.get(movement.getFeeTypeCode() + "_P");
						}
						movementMap.put(movement.getFeeTypeCode() + "_P", amount.add(movement.getPaidAmount()));

						amount = BigDecimal.ZERO;
						if (movementMap.containsKey(movement.getFeeTypeCode() + "_W")) {
							amount = movementMap.get(movement.getFeeTypeCode() + "_W");
						}
						movementMap.put(movement.getFeeTypeCode() + "_W", amount.add(movement.getWaivedAmount()));

						keyCode = movement.getFeeTypeCode();
					}

					TaxHeader taxHeader = movement.getTaxHeader();
					Taxes cgstTax = new Taxes();
					Taxes sgstTax = new Taxes();
					Taxes igstTax = new Taxes();
					Taxes ugstTax = new Taxes();
					Taxes cessTax = new Taxes();
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							if (RuleConstants.CODE_CGST.equals(taxes.getTaxType())) {
								cgstTax = taxes;
							} else if (RuleConstants.CODE_SGST.equals(taxes.getTaxType())) {
								sgstTax = taxes;
							} else if (RuleConstants.CODE_IGST.equals(taxes.getTaxType())) {
								igstTax = taxes;
							} else if (RuleConstants.CODE_UGST.equals(taxes.getTaxType())) {
								ugstTax = taxes;
							} else if (RuleConstants.CODE_CESS.equals(taxes.getTaxType())) {
								cessTax = taxes;
							}
						}
					}

					// Tax Details
					// Paid GST Details
					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CGST_P")) {
						amount = movementMap.get(keyCode + "_CGST_P");
					}
					movementMap.put(keyCode + "_CGST_P", amount.add(cgstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_SGST_P")) {
						amount = movementMap.get(keyCode + "_SGST_P");
					}
					movementMap.put(keyCode + "_SGST_P", amount.add(sgstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_IGST_P")) {
						amount = movementMap.get(keyCode + "_IGST_P");
					}
					movementMap.put(keyCode + "_IGST_P", amount.add(igstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_UGST_P")) {
						amount = movementMap.get(keyCode + "_UGST_P");
					}
					movementMap.put(keyCode + "_UGST_P", amount.add(ugstTax.getPaidTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CESS_P")) {
						amount = movementMap.get(keyCode + "_CESS_P");
					}
					movementMap.put(keyCode + "_CESS_P", amount.add(cessTax.getPaidTax()));

					// Waiver GST Details
					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CGST_W")) {
						amount = movementMap.get(keyCode + "_CGST_W");
					}
					movementMap.put(keyCode + "_CGST_W", amount.add(cgstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_SGST_W")) {
						amount = movementMap.get(keyCode + "_SGST_W");
					}
					movementMap.put(keyCode + "_SGST_W", amount.add(sgstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_IGST_W")) {
						amount = movementMap.get(keyCode + "_IGST_W");
					}
					movementMap.put(keyCode + "_IGST_W", amount.add(igstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_UGST_W")) {
						amount = movementMap.get(keyCode + "_UGST_W");
					}
					movementMap.put(keyCode + "_UGST_W", amount.add(ugstTax.getWaivedTax()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CESS_W")) {
						amount = movementMap.get(keyCode + "_CESS_W");
					}
					movementMap.put(keyCode + "_CESS_W", amount.add(cessTax.getWaivedTax()));
				}

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountingEvent.REPAY);
				amountCodes.setPartnerBankAc(rcd.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(rcd.getPartnerBankAcType());
				amountCodes.setPaymentType(rcd.getPaymentType());
				amountCodes.setUserBranch(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
				aeEvent.getAcSetIDList().clear();

				aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, AccountingEvent.REPAY));

				// Paid GST Details
				addZeroifNotContains(movementMap, "bounceChargePaid");
				addZeroifNotContains(movementMap, "bounceCharge_CGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_IGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_SGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_UGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_CESS_P");

				// Waiver GST Details
				addZeroifNotContains(movementMap, "bounceChargeWaived");
				addZeroifNotContains(movementMap, "bounceCharge_CGST_W");
				addZeroifNotContains(movementMap, "bounceCharge_IGST_W");
				addZeroifNotContains(movementMap, "bounceCharge_SGST_W");
				addZeroifNotContains(movementMap, "bounceCharge_UGST_W");
				addZeroifNotContains(movementMap, "bounceCharge_CESS_W");

				dataMap = amountCodes.getDeclaredFieldValues();

				dataMap.putAll(movementMap);

				// if Repay headers not exists on the Receipt, then add Excess
				// Detail map
				if (rcd.getRepayHeader() == null) {
					dataMap.putAll(feeMap);
				}
				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				engineExecution.getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

			}
		}

		// Accounting for Manual TDS
		amountCodes.setManualTds(receiptData.getReceiptHeader().getTdsAmount());

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			if (StringUtils.isNotEmpty(finMain.getFinCommitmentRef())) {
				Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				int format = CurrencyUtil.getFormat(commitment.getCmtCcy());

				if (commitment != null && commitment.isRevolving()) {
					aeEvent.setAccountingEvent(AccountingEvent.CMTRPY);
					amountCodes.setCmtAmt(BigDecimal.ZERO);
					amountCodes.setChgAmt(BigDecimal.ZERO);
					amountCodes.setDisburse(BigDecimal.ZERO);
					amountCodes.setRpPri(
							CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(), totRpyPri));

					Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

					aeEvent.setDataMap(dataMap);
					engineExecution.getAccEngineExecResults(aeEvent);

					// FIXME: PV: 04MAY17 why separate method is required for
					// commitment dialog show
					getAccountingDetailDialogCtrl().doFillCmtAccounting(aeEvent.getReturnDataSet(), format);
					getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList()
							.addAll(aeEvent.getReturnDataSet());
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, Object> dataMap, String payType) {
		logger.debug(Literal.ENTERING);
		List<FinFeeDetail> finFeeDetailList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			return;
		}

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!finFeeDetail.isRcdVisible()) {
				continue;
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(finFeeDetail, payType));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab() {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (tabsIndexCenter.getFellowIfAny("accountingTab") == null) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Accounting");
			tab.setId("accountingTab");
			tabsIndexCenter.appendChild(tab);
			// ComponentsCtrl.applyForward(tab,
			// "onSelect=onSelectAccountingDetailTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("accountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
				tabpanel.setVisible(true);
			}
		}

		Long accountSetId = Long.MIN_VALUE;
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		if (receiptPurposeCtg == 2) {
			accountSetId = AccountingEngine.getAccountSetID(finMain, AccountingEvent.EARLYSTL);
		} else if (receiptPurposeCtg == 1) {
			accountSetId = AccountingEngine.getAccountSetID(finMain, AccountingEvent.EARLYPAY);
		} else {
			accountSetId = AccountingEngine.getAccountSetID(finMain, AccountingEvent.REPAY);
		}

		// Accounting Detail Tab
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", receiptData.getFinanceDetail());
		map.put("finHeaderList", getFinBasicDetails());
		map.put("acSetID", accountSetId);
		map.put("DisableZeroCal", false);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);

		Tab tab = null;
		if (tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
			tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
			tab.setVisible(true);
		}
		logger.debug(Literal.LEAVING);
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinReceiptHeader rch, String finishedTasks) {
		logger.debug(Literal.ENTERING);
		String serviceTasks = getServiceOperations(taskId, rch);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug(Literal.LEAVING);
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinReceiptHeader receiptHeader) {
		logger.debug(Literal.ENTERING);
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(FinReceiptData aReceiptData, String tranType) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		// FinanceMain afinanceMain =
		// aReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = aReceiptData.getReceiptHeader();

		rch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setWorkflowId(getWorkFlowId());

		rch.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isForeClosure && FinanceConstants.CLOSURE_APPROVER.equals(module)
				&& this.paidByCustomer.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			rch.setClosureWithFullWaiver(true);
		}

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

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckDepositProc)) {
					FinReceiptData tReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

					// Check whether deposit process already completed or not,
					// if Re-submission allowed on Realization stage
					// Otherwise till approval of Deposit process , receipt
					// should be wait for realization
					FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
					if (ImplementationConstants.DEPOSIT_PROC_REQ) {
						if (ReceiptMode.CHEQUE.equals(receiptHeader.getReceiptMode())
								|| ReceiptMode.DD.equals(receiptHeader.getReceiptMode())) {
							tReceiptData.getReceiptHeader().setDepositProcess(true);
							rch.setDepositProcess(true); // Cash
							// Management
							// Change
						}
					}
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getFinanceDetailService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.equals(module, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
				} else {
					FinReceiptData tReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tReceiptData.getReceiptHeader());
					auditHeader.getAuditDetail().setModelData(tReceiptData);
					processCompleted = doSaveProcess(auditHeader, method);

				}

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
					setNextTaskDetails(taskId, rch);
					auditHeader.getAuditDetail().setModelData(tRepayData);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aReceiptData, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinReceiptData aRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = aRepayData.getReceiptHeader();

		aRepayData.setForeClosure(isForeClosure);
		aRepayData.setClosrMaturedLAN(isClosrMaturedLAN);

		if (aRepayData.getFinanceDetail().getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aRepayData.getFinanceDetail().getExtendedFieldRender();
			details.setReference(rch.getReference());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(rch.getRecordStatus());
			details.setVersion(rch.getVersion());
			details.setWorkflowId(rch.getWorkflowId());
			details.setRecordType(rch.getRecordType());
			details.setTaskId(rch.getTaskId());
			details.setNextTaskId(rch.getNextTaskId());
			details.setRoleCode(rch.getRoleCode());
			details.setNextRoleCode(rch.getNextRoleCode());
			details.setNewRecord(rch.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(rch.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(rch.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		if (aRepayData.getFinanceDetail().getExtendedFieldExtension() != null) {
			ExtendedFieldExtension details = aRepayData.getFinanceDetail().getExtendedFieldExtension();
			ExtendedFieldRender render = aRepayData.getFinanceDetail().getExtendedFieldRender();
			details.setExtenrnalRef(Long.toString(rch.getReceiptID()));
			details.setPurpose(rch.getReceiptPurpose());
			details.setModeStatus(rch.getReceiptModeStatus());
			details.setEvent(PennantStaticListUtil
					.getFinEventCode(aRepayData.getFinanceDetail().getExtendedFieldHeader().getEvent()));
			details.setSequence(aRepayData.getFinanceDetail().getExtendedFieldRender().getSeqNo());
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
			if (PennantConstants.RECORD_TYPE_DEL.equals(rch.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(render.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		aRepayData.setClosrMaturedLAN(isClosrMaturedLAN);

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {
				if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(rch.getRecordStatus())) {
					rch.setDedupCheckRequired(false);
				}

				if (StringUtils.isBlank(method)) {
					auditHeader = receiptService.saveOrUpdate(auditHeader);
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
						manualAdviseService.cancelManualAdvises(fm);

						if (rch.isNewRecord()) {
							((FinReceiptData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail()
									.setDirectFinalApprove(true);
						}

						auditHeader = getReceiptService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(rch.getRecordType())) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						rch.setDedupCheckRequired(false);
						auditHeader = getReceiptService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(rch.getRecordType())) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReversal)) {
						auditHeader = getReceiptService().doReversal(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(rch.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ReceiptDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ReceiptDialog, auditHeader);
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

		} catch (AppException e) {
			MessageUtil.showError(e.getMessage());
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		// setRepaySchdList(sortRpySchdDetails(repaySchdList));
		// this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalTds = BigDecimal.ZERO;
		BigDecimal totalLatePft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;

		BigDecimal totInsPaid = BigDecimal.ZERO;
		BigDecimal totSchdFeePaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtil.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getProfitSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getPrincipalSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()), formatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getTdsSchdPayNow(), formatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()), formatter));
				totalLatePft = totalLatePft
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(
						repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()), formatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil
						.amountFormate(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()), formatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getMaxWaiver(), formatter));
				} else {
					lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getRefundMax(), formatter));
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

				lc = new Listcell(PennantApplicationUtil.amountFormate(refundPft, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Fee Details
				lc = new Listcell(PennantApplicationUtil.amountFormate(repaySchd.getSchdFeePayNow(), formatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow())
						.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()))
						.add(repaySchd.getSchdFeePayNow())
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()))
						.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()).subtract(refundPft));
				lc = new Listcell(PennantApplicationUtil.amountFormate(netPay, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdFeeBal());

				lc = new Listcell(PennantApplicationUtil.amountFormate(
						netBalance.subtract(netPay.subtract(totalCharge).subtract(totalLatePft)), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				// this.listBoxPayment.appendChild(item);
			}

			// Summary Details
			Map<String, BigDecimal> paymentMap = new HashMap<String, BigDecimal>();
			paymentMap.put("totalRefund", totalRefund);
			paymentMap.put("totalCharge", totalCharge);
			paymentMap.put("totalPft", totalPft);
			paymentMap.put("totalTds", totalTds);
			paymentMap.put("totalLatePft", totalLatePft);
			paymentMap.put("totalPri", totalPri);

			paymentMap.put("insPaid", totInsPaid);
			paymentMap.put("schdFeePaid", totSchdFeePaid);

			doFillSummaryDetails(paymentMap);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(Map<String, BigDecimal> paymentMap) {
		Listcell lc;
		Listitem item;
		// Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_summary.label"));
		lc.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(15);
		lc.setParent(item);
		// this.listBoxPayment.appendChild(item);

		BigDecimal totalSchAmount = BigDecimal.ZERO;

		/*
		 * if (paymentMap.get("totalRefund").compareTo(BigDecimal.ZERO) > 0) { this.listheader_Refund.setVisible(true);
		 * totalSchAmount = totalSchAmount.subtract(paymentMap.get("totalRefund"));
		 * fillListItem(Labels.getLabel("listcell_totalRefund.label"), paymentMap.get("totalRefund")); } else {
		 * this.listheader_Refund.setVisible(false); } if (paymentMap.get("totalCharge").compareTo(BigDecimal.ZERO) > 0)
		 * { this.listheader_Penalty.setVisible(true); totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("totalCharge"));
		 * fillListItem(Labels.getLabel("listcell_totalPenalty.label"), paymentMap.get("totalCharge")); } else {
		 * this.listheader_Penalty.setVisible(false); } if (paymentMap.get("totalPft").compareTo(BigDecimal.ZERO) > 0) {
		 * totalSchAmount = totalSchAmount.add(paymentMap.get("totalPft"));
		 * fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), paymentMap.get("totalPft")); } if
		 * (paymentMap.get("totalTds").compareTo(BigDecimal.ZERO) > 0) {
		 * fillListItem(Labels.getLabel("listcell_totalTdsPayNow.label"), paymentMap.get("totalTds"));
		 * this.listheader_Tds.setVisible(true); } else { this.listheader_Tds.setVisible(false); } if
		 * (paymentMap.get("totalLatePft").compareTo(BigDecimal.ZERO) > 0) { totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("totalLatePft")); this.listheader_LatePft.setVisible(true);
		 * fillListItem(Labels.getLabel("listcell_totalLatePftPayNow.label"), paymentMap.get("totalLatePft")); } else {
		 * this.listheader_LatePft.setVisible(false); }
		 */
		if (paymentMap.get("totalPri").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));
			fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"));
		}

		fillListItem(Labels.getLabel("listcell_totalSchAmount.label"), totalSchAmount);

	}

	/**
	 * Method for Showing List Item
	 * 
	 * @param label
	 * @param fieldValue
	 */
	private void fillListItem(String label, BigDecimal fieldValue) {

		Listcell lc;
		Listitem item;
		item = new Listitem();
		lc = new Listcell();
		lc.setParent(item);
		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;");
		lc.setSpan(2);
		lc.setParent(item);
		lc = new Listcell(
				PennantApplicationUtil.amountFormate(fieldValue, receiptData.getRepayMain().getLovDescFinFormatter()));
		lc.setStyle("text-align:right;color:#f36800;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(12);
		lc.setParent(item);
		// this.listBoxPayment.appendChild(item);

	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param repayScheduleDetails
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {
		// FIXME: PV: CODE REVIEW PENDING
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
	 * Method to validate data
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 */
	private boolean isValidateData(boolean isCalProcess) throws InterruptedException, InterfaceException {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		// Validate Field Details
		if (isCalProcess) {
			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
		}

		boolean isOverDraft = false;
		if (FinanceConstants.PRODUCT_ODFACILITY
				.equals(getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}

		/*
		 * if (this.receivedDate.getValue() != null) { receiptValueDate = this.receivedDate.getValue(); }
		 */

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date receiptValueDate = rch.getValueDate();
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		List<FinanceScheduleDetail> scheduleList = finScheduleData.getFinanceScheduleDetails();

		// in case of early pay,do not allow in subvention period
		if (receiptPurposeCtg == 1 && financeMain.isAllowSubvention()) {
			boolean isInSubVention = receiptService.isInSubVention(
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(), receiptValueDate);
			if (isInSubVention) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_SubVention_EndDates"));
				return false;
			}
		}

		BigDecimal payableAmt = rch.getReceiptAmount();

		if (!isCalProcess && !isForeClosure && isEarlySettle) {
			payableAmt = payableAmt.add(receiptData.getExcessAvailable());
		}

		if (receiptData.getPaidNow().compareTo(payableAmt) > 0) {
			String[] args = new String[2];

			args[0] = PennantApplicationUtil.amountFormate(receiptData.getPaidNow(), formatter);
			args[1] = PennantApplicationUtil.amountFormate(payableAmt, formatter);
			MessageUtil.showError(Labels.getLabel("label_Allocation_More_than_receipt", args));
			return false;
		}

		// in case of early settlement,do not allow before first installment
		// date(based on AlwEarlySettleBefrFirstInstn in finType )
		if (receiptPurposeCtg == 2 && !financeType.isAlwCloBefDUe() && !isOverDraft) {
			if (financeMain.getFinStartDate() != null
					&& rch.getValueDate().compareTo(financeMain.getFinStartDate()) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_First_Inst_Date",
						new String[] { DateUtil.formatToLongDate(financeMain.getFinStartDate()) }));
				return false;
			}
		}

		if (receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null && receiptPurposeCtg > 0
				&& receiptValueDate.compareTo(financeMain.getFinStartDate()) == 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Date"));
			return false;
		}

		if (isForeClosure && receiptData.isFCDueChanged()) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_DueAmounts_Changed"));
			return false;
		}

		// Receipt Calculation Value date should not be equal to Any Holiday
		// Schedule Date

		// Entered Receipt Amount Match case test with allocations
		BigDecimal totReceiptAmount = receiptData.getTotReceiptAmount();
		if (totReceiptAmount.compareTo(BigDecimal.ZERO) == 0 && !isForeClosure) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_NoReceiptAmount"));
			return false;
		}

		// Past due Details
		BigDecimal balPending = rch.getTotalPastDues().getBalance().add(rch.getTotalRcvAdvises().getBalance())
				.add(rch.getTotalFees().getBalance());

		// User entered Receipt amounts and paid on manual Allocation validation
		if (receiptData.getRemBal().compareTo(BigDecimal.ZERO) < 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_InsufficientAmount"));
			return false;
		}

		if (receiptPurposeCtg == 2 && isForeClosure) {
			if (balPending.compareTo(BigDecimal.ZERO) > 0
					&& receiptData.getCalculatedClosureAmt().compareTo(BigDecimal.ZERO) > 0) {
				balPending = balPending.subtract(receiptData.getReceiptHeader().getClosureThresholdLimit());
			}
			if (balPending.compareTo(BigDecimal.ZERO) != 0) {
				MessageUtil.showError(
						Labels.getLabel("label_ReceiptDialog_Valid_Settlement", new String[] { PennantApplicationUtil
								.getLabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}

		int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
		receiptValueDate = DateUtil.addDays(receiptValueDate, -(defaultClearingDays));

		// depositDate should be greater than valuedate
		if (this.depositDate.getValue() != null && this.depositDate.getValue().compareTo(receiptValueDate) < 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Invalid_DepositDate",
					new String[] { DateUtil.formatToLongDate(receiptValueDate),
							DateUtil.formatToLongDate(SysParamUtil.getAppDate()) }));
			return false;
		}

		if (isForeClosure && this.paidByCustomer.getValue().compareTo(BigDecimal.ZERO) == 0
				&& (FinanceConstants.CLOSURE_MAKER.equals(module)
						|| FinanceConstants.CLOSURE_APPROVER.equals(module))) {
			if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0 && rch.getClosureType() != null) {
				MessageUtil.showError(Labels.getLabel("Unadjusted_Excess_Amount"));
				return false;
			}
		}

		// Manual Schedule Validations
		if (finScheduleData.getFinanceMain().isManualSchedule()
				&& StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.EARLYRPY)
				&& (isCalProcess || !("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| this.userAction.getSelectedItem().getLabel().contains("Reject")
						|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
						|| this.userAction.getSelectedItem().getLabel().contains("Decline")))) {

			ManualScheduleHeader scheduleHeader = finScheduleData.getManualScheduleHeader();

			if (scheduleHeader == null || CollectionUtils.isEmpty(scheduleHeader.getManualSchedules())
					|| (scheduleHeader != null && !scheduleHeader.isValidSchdUpload())) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);

				if (tab != null) {
					tab.setSelected(true);
				}

				MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
				return false;
			}

			int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
			BigDecimal bal = PennantApplicationUtil.formateAmount(receiptData.getRemBal(), formatter);

			if (scheduleHeader.getCurPOSAmt().subtract(bal).compareTo(scheduleHeader.getTotPrincipleAmt()) != 0) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				MessageUtil.showError(Labels.getLabel("PRIAMT_FINAMT_NOTMATCH"));
				return false;
			}
		}

		if (!isCalProcess) {
			return true;
		}

		// Finance Should not allow for Partial Settlement & Early settlement
		// when Maturity Date reaches Current application Date
		if ((receiptPurposeCtg == 1 || receiptPurposeCtg == 2) && !receiptData.isForeClosure()) {

			if (financeMain.getMaturityDate().compareTo(receiptValueDate) < 0) {
				MessageUtil.showError(
						Labels.getLabel("label_ReceiptDialog_Valid_MaturityDate", new String[] { PennantApplicationUtil
								.getLabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}
		// validation throw in manuval advice payable in receipts
		// Commented because of when we are trying to move this receipt amount to 'EMI in Advance' from 'Excess amount',
		// application is showing the " No dues to knock off.(Below message)"
		// As per the discussion with team this valid scenario, commented the below error.
		/*
		 * if (receiptData.getPaidNow().compareTo(BigDecimal.ZERO) <= 0 && isKnockOff && receiptPurposeCtg == 0) {
		 * MessageUtil.showError(Labels.getLabel("label_Allocation_No_Due_KnockedOff")); return false; }
		 */

		// No excess amount validation on partial Settlement
		if (receiptPurposeCtg == 1) {
			if (receiptData.getRemBal().compareTo(BigDecimal.ZERO) <= 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
				return false;
			} /*
				 * else if (rch.getTotalPastDues().getBalance().compareTo(BigDecimal. ZERO) > 0) {
				 * MessageUtil.showError(Labels.getLabel( "label_ReceiptDialog_Valid_PastAmount_PartialSettlement"));
				 * return false; }
				 */ else {

				// Check the max Schedule payment amount
				BigDecimal closingBal = BigDecimal.ZERO;
				boolean isValidPPDate = true;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0
							&& StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_UNPLANNED.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_HOLIDAY.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_POSTPONE.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_HOLDEMI.equals(curSchd.getBpiOrHoliday())
							&& !FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
						isValidPPDate = false;
					}
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) >= 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}
					if (DateUtil.compare(receiptValueDate, curSchd.getSchDate()) == 0 || closingBal == null) {
						closingBal = closingBal.subtract(curSchd.getSchdPriPaid().subtract(curSchd.getSchdPftPaid()));
						break;
					}
				}

				if (!isValidPPDate) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Invalid_ValueDate"));
					return false;
				}

				if (closingBal != null) {
					if ((receiptData.getRemBal().compareTo(closingBal) >= 0 && !isOverDraft)
							|| ((receiptData.getRemBal().compareTo(closingBal)) > 0 && isOverDraft)) {
						if (!isOverDraft) {
							MessageUtil.showError(Labels.getLabel("FIELD_IS_LESSER",
									new String[] {
											Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
											PennantApplicationUtil.amountFormate(closingBal, formatter) }));
							return false;
						} else {
							MessageUtil.showError(Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] {
											Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
											PennantApplicationUtil.amountFormate(closingBal, formatter) }));
						}
					}
				} else {
					if (isOverDraft) {
						MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
						return false;
					}
				}

				if (receiptPurposeCtg == 1 && isOverDraft) {
					if ((receiptData.getTotReceiptAmount().compareTo(closingBal)) > 0 && isOverDraft) {
						MessageUtil.showError(Labels.getLabel("FIELD_IS_LESSER",
								new String[] {
										Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
										PennantApplicationUtil.amountFormate(receiptData.getRemBal(), formatter) }));
						return false;
					}
				}
			}
		}

		// Early settlement Validation , if entered amount not sufficient with
		// paid and waived amounts
		if (receiptPurposeCtg == 2) {
			BigDecimal earlySettleBal = totReceiptAmount.subtract(balPending);
			if (earlySettleBal.compareTo(BigDecimal.ZERO) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_EarlySettlement"));
				return false;
			}

			// Paid amount still not cleared by paid's or waivers amounts
			if (isForeClosure && balPending.compareTo(BigDecimal.ZERO) > 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Paids_EarlySettlement"));
				return false;
			}

			// PAN Number checking
			if (isPanMandatory) {
				long finID = financeMainService.getFinID(this.finReference.getValue());
				List<Long> coAppCustIds = this.jointAccountDetailService.getCustIdsByFinID(finID);
				coAppCustIds.add(financeMain.getCustID());

				if (!customerDetailsService.isPanFoundByCustIds(coAppCustIds, this.panNumber.getValue())) {
					MessageUtil.showError("PAN Details are not matching with Applicant / Co applicant of the loan");
					return false;
				}
			}

			if (!ImplementationConstants.RECEIPT_ALLOW_FULL_WAIVER) {
				if (isForeClosure && receiptData.getPaidNow().compareTo(BigDecimal.ZERO) == 0) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Allow_FullWaiver"));
					return false;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method to show report chart
	 */
	public void doShowReportChart(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();

		// For Finance Vs Amounts Chart z
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount(finScheduleData, formatter);

		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts",
				"Loan Amount =" + PennantApplicationUtil.amountFormate(PennantApplicationUtil
						.unFormateAmount(finScheduleData.getFinanceMain().getFinAmount(), formatter), formatter),
				"", "");
		aDashboardConfiguration = new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks(ChartType.PIE3D.getRemarks() + " decimals='" + formatter + "'");
		String chartStrXML = chartsConfig.getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.PIE3D.toString());
		chartDetail.setChartHeight("180");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");

		// For Repayments Chart
		chartsConfig = new ChartsConfig("Payments", "", "", "");
		chartsConfig.setSetElements(getReportDataForRepayments(finScheduleData, formatter));
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig.setRemarks(ChartType.MSLINE.getRemarks() + " decimals='" + formatter + "'");
		chartStrXML = chartsConfig.getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.MSLINE.toString());
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");
		chartDetailList.add(chartDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to get report data from repayments table.
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForRepayments(FinScheduleData scheduleData, int formatter) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();
		int format = CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());
		ChartSetElement chartSetElement;
		if (listScheduleDetail != null) {
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"Payment Amount", PennantApplicationUtil.formateAmount(curSchd.getRepayAmount(), format)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()), "Principal",
							PennantApplicationUtil.formateAmount(curSchd.getPrincipalSchd(), format).setScale(formatter,
									RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()), "Interest",
							PennantApplicationUtil.formateAmount(curSchd.getProfitSchd(), format).setScale(formatter,
									RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug(Literal.LEAVING);
		return listChartSetElement;
	}

	/**
	 * This method returns data for Finance vs amount chart
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForFinVsAmount(FinScheduleData scheduleData, int formatter) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		BigDecimal downPayment = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal schedulePrincipal = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		int format = CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());

		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();

		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;
			BigDecimal financeAmount = BigDecimal.ZERO;
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				financeAmount = financeAmount
						.add(PennantApplicationUtil.formateAmount(curSchd.getDisbAmount(), format));
				downPayment = downPayment
						.add(PennantApplicationUtil.formateAmount(curSchd.getDownPaymentAmount(), format));
				capitalized = capitalized.add(PennantApplicationUtil.formateAmount(curSchd.getCpzAmount(), format));

				scheduleProfit = scheduleProfit
						.add(PennantApplicationUtil.formateAmount(curSchd.getProfitSchd(), format));
				schedulePrincipal = schedulePrincipal
						.add(PennantApplicationUtil.formateAmount(curSchd.getPrincipalSchd(), format));

			}
			chartSetElement = new ChartSetElement("Down Payment", downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Capitalized", capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Schedule Interest", scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Schedule Principal", schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}
		logger.debug(Literal.LEAVING);
		return listChartSetElement;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
		// FIXME: PV: CODE REVIEW PENDING
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
		// FIXME: PV: CODE REVIEW PENDING
		doShowNotes(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain());
	}

	protected void refreshMaintainList() {
		// FIXME: PV: CODE REVIEW PENDING
		getReceiptListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(receiptData.getReceiptHeader().getReceiptID());
	}

	// Linked Loans

	public void onClick$btn_LinkedLoan(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING); // FIXME: PV: CODE
		// REVIEW PENDING
		isLinkedBtnClick = true;
		List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
		List<FinanceProfitDetail> finpftDetails = new ArrayList<FinanceProfitDetail>();
		financeMains.addAll(getFinanceDetailService().getFinanceMainForLinkedLoans(finReference.getValue()));

		if (CollectionUtils.isNotEmpty(financeMains)) {
			List<Long> finRefList = new ArrayList<>();
			for (FinanceMain finMain : financeMains) {
				if (StringUtils.equals(receiptData.getFinReference(), finMain.getFinReference())) {
					continue;
				}
				finRefList.add(finMain.getFinID());
			}
			if (CollectionUtils.isNotEmpty(finRefList)) {
				finpftDetails.addAll(getFinanceDetailService().getFinProfitListByFinRefList(finRefList));
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMains", financeMains);
		map.put("finpftDetails", finpftDetails);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/LinkedLoansDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void showErrorDetails(List<WrongValueException> wve) {
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * onChanging fundingAccount details
	 * 
	 * @param event
	 */
	public void onFulfill$fundingAccount(Event event) {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		this.fundingAccount.clearErrorMessage();
		Clients.clearWrongValue(this.fundingAccount);

		long partnerBankID = 0;
		FinTypePartnerBank finTypePartnerBank = null;
		Object dataObject = this.fundingAccount.getObject();

		if (dataObject != null) {
			if (dataObject instanceof FinTypePartnerBank) {
				finTypePartnerBank = (FinTypePartnerBank) dataObject;
				partnerBankID = finTypePartnerBank.getPartnerBankID();
			}
		}
		this.fundingAccount.setAttribute("partnerBankId", partnerBankID);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method which returns customer document title
	 * 
	 */
	public String getCustomerIDNumber(String docTypeCode) {
		// FIXME: PV: CODE REVIEW PENDING
		if (getFinanceDetail() != null) {
			for (CustomerDocument custDocs : getFinanceDetail().getCustomerDetails().getCustomerDocumentsList()) {
				if (StringUtils.equals(custDocs.getCustDocCategory(), docTypeCode)) {
					return custDocs.getCustDocTitle();
				}
			}
		}
		return null;
	}

	/** new code to display chart by skipping jsps code start */
	public void onSelectDashboardTab(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		for (ChartDetail chartDetail : chartDetailList) {
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul", tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"),
					Collections.singletonMap("chartDetail", chartDetail));
		}
		chartDetailList = new ArrayList<ChartDetail>(); // Resetting
		logger.debug(Literal.LEAVING);
	}

	/** new code to display chart by skipping jsps code end */

	// Printer integration starts

	public void onClick$btnPrint(Event event) throws Exception {
		printReceipt();
	}

	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintSchedule(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		List<Object> list = new ArrayList<Object>();
		FinScheduleListItemRenderer finRender;
		if (getFinSchedData() != null) {

			// Fee Charges List Render For First Disbursement only/Existing
			List<FeeRule> feeRuleList = getFinSchedData().getFeeRules();
			FinanceMain financeMain = getFinSchedData().getFinanceMain();

			// Get Finance Fee Details For Schedule Render Purpose In
			// maintenance Stage
			List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
			if (!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())
					&& !isWIF) {
				approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinID(), "", isWIF);
			}
			approvedFeeRules.addAll(feeRuleList);

			Map<Date, ArrayList<FeeRule>> feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
			for (FeeRule fee : approvedFeeRules) {
				if (feeChargesMap.containsKey(fee.getSchDate())) {
					ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
					int seqNo = 0;
					for (FeeRule feeRule : feeChargeList) {
						if (feeRule.getFeeCode().equals(fee.getFeeCode())) {
							if (seqNo < feeRule.getSeqNo() && fee.getSchDate().compareTo(feeRule.getSchDate()) == 0) {
								seqNo = feeRule.getSeqNo();
							}
						}
					}
					fee.setSeqNo(seqNo + 1);
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);

				} else {
					ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);
				}
			}

			finRender = new FinScheduleListItemRenderer();
			list.add(finRender.getScheduleGraphData(getFinSchedData()));
			list.add(finRender.getPrintScheduleData(getFinSchedData(), null, null, true, false, false));

			boolean isSchdFee = false;
			List<FinFeeDetail> finFeeList = getFinSchedData().getFinFeeDetailList();
			for (int i = 0; i < finFeeList.size(); i++) {
				FinFeeDetail finFeeDetail = finFeeList.get(i);
				if (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(finFeeDetail.getFeeScheduleMethod())
						|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(finFeeDetail.getFeeScheduleMethod())
						|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS
								.equals(finFeeDetail.getFeeScheduleMethod())) {
					isSchdFee = true;
					break;
				}
			}

			list.add(isSchdFee);

			// To get Parent Window i.e Finance main based on product
			Component component = this.window_ReceiptDialog;
			Window window = null;
			if (component instanceof Window) {
				window = (Window) component;
			} else {
				window = (Window) this.window_ReceiptDialog.getParent().getParent().getParent().getParent().getParent()
						.getParent().getParent().getParent();
			}
			String reportName = "FINENQ_ScheduleDetail";

			if (FinanceConstants.PRODUCT_CONVENTIONAL.equals(financeMain.getProductCategory())) {
				reportName = "CFINENQ_ScheduleDetail";
			} else if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory())) {
				reportName = "ODFINENQ_ScheduleDetail";
			}

			// Customer CIF && Customer Name Setting
			CustomerDetails customerDetails = getFinanceDetail().getCustomerDetails();
			if (customerDetails != null) {
				Customer customer = customerDetails.getCustomer();
				financeMain.setLovDescCustCIF(customer.getCustCIF());
				financeMain.setLovDescCustShrtName(customer.getCustShrtName());
			} else {
				financeMain.setLovDescCustCIF("");
			}

			if (isWIF) {
				reportName = "WIFENQ_ScheduleDetail";
				WIFCustomer customerDetailsData = getFinanceDetail().getCustomer();
				if (customerDetailsData != null) {
					financeMain.setLovDescCustCIF(String.valueOf(customerDetailsData.getCustID()));
					financeMain.setLovDescCustShrtName(customerDetailsData.getCustShrtName());
				} else {
					financeMain.setLovDescCustCIF("");
				}
			}

			int months = DateUtil.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate());

			int advTerms = 0;
			if (AdvanceType.hasAdvEMI(financeMain.getAdvType())
					&& AdvanceStage.hasFrontEnd(financeMain.getAdvStage())) {
				advTerms = financeMain.getAdvTerms();
			}

			String noOfTerms = String.valueOf(financeMain.getCalTerms() + financeMain.getGraceTerms());
			financeMain.setLovDescTenorName((months / 12) + " Years " + (months % 12) + " Months / "
					+ (StringUtils.isEmpty(noOfTerms) ? "0" : noOfTerms) + advTerms + " Payments");

			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
			String usrName = PennantApplicationUtil.getFullName(securityUser.getUsrFName(), securityUser.getUsrMName(),
					securityUser.getUsrLName());

			ReportsUtil.generatePDF(reportName, financeMain, list, usrName, window);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void printReceipt() throws Exception {
		logger.debug(Literal.ENTERING);

		AgreementEngine engine = null;

		engine = new AgreementEngine(TEMPLATE_PATH);
		engine.setTemplate(RECEIPT_TEMPLATE);
		engine.loadTemplate();

		String reportName = RECEIPT_PREFIX.concat("_");
		reportName = reportName.concat(this.finReference.getValue());
		reportName = reportName.concat("_");
		reportName = reportName.concat(String.valueOf(receiptData.getReceiptHeader().getReceiptID()));

		ReceiptReport receipt = new ReceiptReport();
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		receipt.setReceiptNo(this.receiptId.getValue());
		receipt.setUserName(loggedInUser.getUserName() + " - " + loggedInUser.getFullName());
		receipt.setFinReference(this.finReference.getValue());
		receipt.setCustName(receiptData.getReceiptHeader().getCustShrtName());

		BigDecimal totalReceiptAmt = receiptData.getTotReceiptAmount();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		int finFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		receipt.setReceiptAmount(PennantApplicationUtil.amountFormate(totalReceiptAmt, finFormatter));
		receipt.setReceiptAmountInWords(NumberToEnglishWords
				.getAmountInText(PennantApplicationUtil.formateAmount(totalReceiptAmt, finFormatter), ""));
		receipt.setAppDate(DateUtil.formatToLongDate(SysParamUtil.getAppDate()));
		if (isForeClosure && totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
			receipt.setPaymentMode(ReceiptMode.ZERORECEIPT);
		} else {
			receipt.setPaymentMode(this.receiptMode.getSelectedItem().getLabel().toString());
		}
		receipt.setSubReceiptMode("");
		if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equals(receiptMode.getSelectedItem().getLabel().toString())) {
			receipt.setSubReceiptMode(":" + this.subReceiptMode.getSelectedItem().getLabel().toString());
		}
		receipt.setReceiptDate(DateUtil.formatToLongDate(receiptDate.getValue()));
		// receipt.setReceivedDate(DateUtil.formatToLongDate(receivedDate.getValue()));

		engine.mergeFields(receipt);

		try {
			byte[] documentByteArray = engine.getDocumentInByteArray(SaveFormat.PDF);
			String encodedString = Base64.encodeBase64String(documentByteArray);
			Clients.evalJavaScript(
					"PrinterUtil.print('window_ReceiptDialog','onPrintSuccess','" + encodedString + "')");

		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.printerNotImpl"));
		} finally {
			byte[] docData = engine.getDocumentInByteArray(SaveFormat.PDF);
			showDocument(docData, this.window_ReceiptDialog, reportName, SaveFormat.PDF);
		}

		logger.debug(Literal.LEAVING);
	}

	public void addAmountCell(Listitem item, BigDecimal value, String cellID, boolean isBold) {
		Listcell lc = new Listcell(PennantApplicationUtil.amountFormate(value, formatter));

		if (isBold) {
			lc.setStyle("text-align:right;font-weight:bold;");
		} else {
			lc.setStyle("text-align:right;");
		}

		if (!StringUtils.isBlank(cellID)) {
			lc.setId(cellID);
		}

		lc.setParent(item);
	}

	public void addSimpleTextCell(Listitem item, String value) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);
	}

	public void addBoldTextCell(Listitem item, String value, boolean hasChild, int buttonId) {
		Listcell lc = new Listcell(value);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		if (hasChild) {
			Button button = new Button("Details");
			button.setId(String.valueOf(buttonId));
			button.addForward("onClick", window_ReceiptDialog, "onDetailsClick", button.getId());
			lc.appendChild(button);
		}
		lc.setParent(item);
	}

	public void getFinFeeTypeList(String eventCode) {
		// FIXME: OV. should be removed. already part of receipt calculator
		if (receiptPurposeCtg == 1) {
			eventCode = AccountingEvent.EARLYPAY;
		} else if (receiptPurposeCtg == 2) {
			eventCode = AccountingEvent.EARLYSTL;
		}

		FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			moduleID = FinanceConstants.MODULEID_PROMOTION;
		}

		// Finance Type Fee details based on Selected Receipt Purpose Event
		List<FinTypeFees> finTypeFeesList = this.financeDetailService.getFinTypeFees(financeMain.getFinType(),
				eventCode, false, moduleID);
		receiptData.getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
	}

	private void setBalances() {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		ReceiptAllocationDetail xa = rch.getTotalXcess();
		ReceiptAllocationDetail pd = rch.getTotalPastDues();
		ReceiptAllocationDetail adv = rch.getTotalRcvAdvises();
		ReceiptAllocationDetail fee = rch.getTotalFees();
		ReceiptAllocationDetail bounce = rch.getTotalBounces();

		// Total Net Receivable
		BigDecimal paidByCustomer = pd.getTotalDue().add(adv.getTotalDue()).add(fee.getTotalDue())
				.add(bounce.getTotalDue());

		BigDecimal pdWaived = pd.getWaivedAmount().add(pd.getWaivedGST());
		BigDecimal advWaived = adv.getWaivedAmount().add(adv.getWaivedGST());
		BigDecimal feeWaived = fee.getWaivedAmount().add(fee.getWaivedGST());
		BigDecimal bounceWaived = bounce.getWaivedAmount().add(bounce.getWaivedGST());

		BigDecimal totalWaived = pdWaived.add(advWaived).add(feeWaived).add(bounceWaived);

		paidByCustomer = paidByCustomer.subtract(totalWaived);
		this.paidByCustomer.setValue(PennantApplicationUtil.formateAmount(paidByCustomer, formatter));
		if ((FinanceConstants.CLOSURE_MAKER.equals(module) || FinanceConstants.CLOSURE_APPROVER.equals(module))
				&& this.paidByCustomer.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			isClosureTypeMandatory = true;
		} else {
			isClosureTypeMandatory = false;
		}

		// To be Paid by Customer = Net Receivable - Excess paid
		BigDecimal custToBePaid = paidByCustomer.subtract(xa.getTotalPaid());
		this.custPaid.setValue(PennantApplicationUtil.formateAmount(custToBePaid, formatter));

		// Remaining Balance = Receipt Amount + To be Paid by Customer - Paid by
		// Customer (Allocated)
		BigDecimal duePaidOrg = pd.getTotalPaid().subtract(pd.getTdsPaid());
		BigDecimal advisePaidOrg = adv.getTotalPaid().subtract(adv.getTdsPaid());
		BigDecimal feePaidOrg = fee.getTotalPaid().subtract(fee.getTdsPaid());

		BigDecimal remBalAfterAlloc = (receiptData.getTotReceiptAmount().add(xa.getTotalPaid())).subtract(duePaidOrg)
				.subtract(advisePaidOrg).subtract(feePaidOrg);

		if (!StringUtils.contains(rch.getFinType(), "OD")) {
			if (remBalAfterAlloc.compareTo(BigDecimal.ZERO) <= 0) {
				remBalAfterAlloc = BigDecimal.ZERO;
				this.excessAdjustTo.setDisabled(true);
			} else {
				this.excessAdjustTo.setDisabled(false);
			}
		}
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBalAfterAlloc, formatter));
	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug(Literal.ENTERING);
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		notes.setRoleCode(getRole());
		logger.debug(Literal.LEAVING);
		return notes;
	}

	private boolean isApprover() {
		return getWorkFlow() != null && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())
				&& !(receiptData.getReceiptHeader().getNextRoleCode().contains("MAKER"));
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public StageAccountingDetailDialogCtrl getStageAccountingDetailDialogCtrl() {
		return stageAccountingDetailDialogCtrl;
	}

	public void setStageAccountingDetailDialogCtrl(StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl) {
		this.stageAccountingDetailDialogCtrl = stageAccountingDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug(Literal.ENTERING);

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(module)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final Map<String, Object> map = getDefaultArguments();

			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", module);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			FinanceDetail fd = ObjectUtil.clone(getFinanceDetail());
			fd.setFinScheduleData(befFinSchedData);
			map.put("financeDetail", fd);
			map.put("isEnquiry", true);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				tab.setSelected(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void appendEffectScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug(Literal.ENTERING);

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(module)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final Map<String, Object> map = getDefaultArguments();

			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", module);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("isEnquiry", true);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				tab.setSelected(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	protected void appendStepDetailTab(boolean onLoadProcess) {

		if (onLoadProcess) {
			if (tabpanelsBoxIndexCenter.getFellowIfAny("TAB" + AssetConstants.UNIQUE_ID_STEPDETAILS) == null) {

				Tab tab = new Tab(Labels.getLabel("tab_label_" + AssetConstants.UNIQUE_ID_STEPDETAILS));
				tab.setId("TAB" + AssetConstants.UNIQUE_ID_STEPDETAILS);
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, ("onSelect=" + "onSelectTab"));

				Tabpanel tabpanel = new Tabpanel();
				tabpanel.setId("TABPANEL" + AssetConstants.UNIQUE_ID_STEPDETAILS);
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight("100%");

				final Map<String, Object> map = getDefaultArguments();
				map.put("financeDetail", getFinanceDetail());
				map.put("isWIF", false);
				map.put("isAlwNewStep", isReadOnly("FinanceMainDialog_btnFinStepPolicy"));
				map.put("enquiryModule", true);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StepDetailDialog.zul", tabpanel, map);
				if (tab != null) {
					tab.setDisabled(false);
					tab.setVisible(true);
				}
			}
		}

	}

	protected void doStoreServiceIds(FinReceiptHeader finReceiptHeader) {
		this.curRoleCode = finReceiptHeader.getRoleCode();
		this.curNextRoleCode = finReceiptHeader.getNextRoleCode();
		this.curTaskId = finReceiptHeader.getTaskId();
		this.curNextTaskId = finReceiptHeader.getNextTaskId();
		// this.curNextUserId = finReceiptHeader.getNextUserId();
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));

		return map;
	}

	private void doFillExcessPayables() {
		logger.debug(Literal.ENTERING);
		if (!isForeClosure && !isEarlySettle) {
			return;
		}
		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();
		this.listBoxExcess.getItems().clear();

		for (int i = 0; i < xcessPayableList.size(); i++) {
			createXcessPayableItem(xcessPayableList.get(i), i);
		}
		addXcessFooter(formatter);
		logger.debug(Literal.LEAVING);
	}

	private void createXcessPayableItem(XcessPayables xcessPayable, int idx) {
		// List Item
		Listitem item = new Listitem();
		String payableDesc = xcessPayable.getPayableDesc();

		if (FinServiceEvent.EARLYSETTLE.equals(receiptData.getReceiptHeader().getReceiptPurpose())
				&& RepayConstants.EXAMOUNTTYPE_ADVINT.equals(xcessPayable.getPayableType())) {
			FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			if (financeMain.isTDSApplicable()) {
				payableDesc = payableDesc + "(-TDS)";
			}
		}

		addBoldTextCell(item, payableDesc, false, idx);
		addAmountCell(item, xcessPayable.getAvailableAmt(), null, false);
		addAmountCell(item, BigDecimal.ZERO, null, false);
		addAmountCell(item, xcessPayable.getTotPaidNow(), null, false);
		addAmountCell(item, xcessPayable.getBalanceAmt(), null, false);
		this.listBoxExcess.appendChild(item);
	}

	private void addXcessFooter(int formatter) {
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell("TOTALS");
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		ReceiptAllocationDetail xcess = receiptData.getReceiptHeader().getTotalXcess();

		addAmountCell(item, xcess.getTotalDue(), null, true); // previously it
																// was like
																// getDueAmount()
		addAmountCell(item, BigDecimal.ZERO, null, true);
		addAmountCell(item, xcess.getTotalPaid(), null, true);
		addAmountCell(item, xcess.getBalance(), null, true);

		this.listBoxExcess.appendChild(item);
	}

	public void onClick$foreClosureLetter(Event event) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isModelWindow", true);
		map.put("finReference", this.finReference.getValue());
		map.put("closureType", this.closureType.getValue());
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/SelectLoanClosureEnquiryList.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$generateSOA(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);
		String finReference = this.finReference.getValue();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Date finStartDate = financeMain.getFinStartDate();
		Date appDate = SysParamUtil.getAppDate();

		StatementOfAccount soa = soaReportGenerationService.getStatmentofAccountDetails(finReference, finStartDate,
				appDate, false);

		List<Object> list = new ArrayList<Object>();
		list.add(soa.getSoaSummaryReports());
		list.add(soa.getTransactionReports());
		list.add(soa.getApplicantDetails());
		list.add(soa.getOtherFinanceDetails());
		list.add(soa.getSheduleReports());
		list.add(soa.getInterestRateDetails());

		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		byte[] buf = ReportsUtil.generatePDF("FINENQ_StatementOfAccount", soa, list, userName);

		Filedownload.save(new AMedia("StatementOfAccount", "pdf", "application/pdf", buf));

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$customerBankAcct(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = customerBankAcct.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.customerBankAcct.setValue("");
			this.customerBankAcct.setDescription("");
			this.customerBankAcct.setAttribute("CustBankId", null);
		} else {
			CustomerBankInfo details = (CustomerBankInfo) dataObject;
			if (details != null) {
				this.customerBankAcct.setAttribute("CustBankId", details.getBankId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doProcessTerminationExcess(FinReceiptData receiptData, FinReceiptHeader rch) {
		fillComboBox(this.allocationMethod, "M", PennantStaticListUtil.getAllocationMethods(), ",A,");
		fillComboBox(this.receiptPurpose, FinServiceEvent.SCHDRPY, PennantStaticListUtil.getReceiptPurpose());

		rch.setAllocationType(AllocationType.MANUAL);
		receiptData.setEarlySettle(false);
		receiptData.setValueDate(rch.getReceiptDate());
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_TEXCESS);
		rch.setClosureType(PennantConstants.List_Select);

		rch.getAllocations().removeIf(al -> Allocation.FEE.equals(al.getAllocationType()));

		rch.getAllocations().forEach(al -> receiptCalculator.resetPaidAllocations(al));

		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(rch.getReceiptAmount(), formatter));
		List<FinFeeDetail> finFeeDetailList = new ArrayList<>();
		List<FinTypeFees> finTypeFeesList = new ArrayList<>();

		receiptData.getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
		receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(finFeeDetailList);
		receiptData.getFinanceDetail().getFinFeeConfigList()
				.removeIf(al -> RuleConstants.EVENT_EARLYSTL.equals(al.getFinEvent()));

		if (!FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			this.row_closuretype.setVisible(false);
		}

		resetAllocationPayments();
	}

	public Map<String, BigDecimal> getTaxPercMap() {
		return taxPercMap;
	}

	public void setTaxPercMap(Map<String, BigDecimal> taxPercMap) {
		this.taxPercMap = taxPercMap;
	}

	public PartnerBankService getPartnerBankService() {
		return partnerBankService;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	public FinReceiptData getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(FinReceiptData receiptData) {
		this.receiptData = receiptData;
	}

	public FinReceiptData getOrgReceiptData() {
		return orgReceiptData;
	}

	public void setOrgReceiptData(FinReceiptData orgReceiptData) {
		this.orgReceiptData = orgReceiptData;
	}

	public ReceiptListCtrl getReceiptListCtrl() {
		return receiptListCtrl;
	}

	public void setReceiptListCtrl(ReceiptListCtrl receiptListCtrl) {
		this.receiptListCtrl = receiptListCtrl;
	}

	public ReceiptCancellationService getReceiptCancellationService() {
		return receiptCancellationService;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public FeeCalculator getFeeCalculator() {
		return feeCalculator;
	}

	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public FinScheduleData getFinSchedData() {
		return finSchedData;
	}

	public void setFinSchedData(FinScheduleData finSchedData) {
		this.finSchedData = finSchedData;
	}

	public void setSoaReportGenerationService(SOAReportGenerationService soaReportGenerationService) {
		this.soaReportGenerationService = soaReportGenerationService;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setManualScheduleDialogCtrl(ManualScheduleDialogCtrl manualScheduleDialogCtrl) {
		this.manualScheduleDialogCtrl = manualScheduleDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		if (getFinanceDetail() != null) {
			return getFinanceDetail().getFinScheduleData().getFinanceMain();
		}

		return null;
	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

}