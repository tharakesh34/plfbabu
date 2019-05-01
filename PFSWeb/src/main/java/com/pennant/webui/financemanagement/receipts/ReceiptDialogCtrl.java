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
 * FileName    		:  ReceiptDialogCtrl.java                           					*
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-06-2011    														*
 *                                                                  						*
 * Modified Date    :  03-06-2011    														*
 *                                                                  						*
 * Description 		:																		*	
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                        	* 
 * 29-09-2018       somasekhar               0.2         added backdate sp also,            * 
 * 10-10-2018       somasekhar               0.3         Ticket id:124998,defaulting receipt* 
 *                                                       purpose and excessadjustto for     * 
 *                                                       closed loans                       *
 *                                                       Ticket id:124998                   * 
 * 13-06-2018       Siva					 0.2        Receipt auto printing on approval   * 
 *                                                                                          * 
 * 13-06-2018       Siva					 0.3        Receipt Print Option Added 			* 
 *                                                                                          * 
 * 17-06-2018		Srinivasa Varma			 0.4		PSD 126950                          * 
 *                                                                                          *
 * 19-06-2018		Siva			 		 0.5		Auto Receipt Number Generation      * 
 * 																							*
 * 28-06-2018		Siva			 		 0.6		Stop printing Receipt if receipt 
 * 												     mode status is either cancel or Bounce * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

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
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
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
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
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
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.ReceiptTaxDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.reports.ReceiptReport;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.core.EventManager;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
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
import com.pennant.webui.finance.financemain.StageAccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.financemanagement.paymentMode.ReceiptListCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul
 */
public class ReceiptDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(ReceiptDialogCtrl.class);

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
	protected Decimalbox recepitInProcess;
	protected Decimalbox recepitInprocessManual;
	protected Textbox finCcy;
	protected Decimalbox paidByCustomer;

	protected Button btnSearchCustCIF;
	protected Button btnSearchFinreference;
	protected Button btnSearchReceiptInProcess;

	// Receipt Details
	protected Groupbox gb_ReceiptDetails;
	protected Textbox receiptId;
	protected Combobox receiptPurpose;
	protected Combobox receiptMode;
	protected Label receiptTypeLabel;
	protected Combobox receiptChannel;
	protected Combobox subReceiptMode;
	protected Datebox receiptDate;
	protected CurrencyBox receiptAmount;
	protected Combobox excessAdjustTo;
	protected Decimalbox remBalAfterAllocation;
	protected Decimalbox custPaid;
	protected Label label_ReceiptDialog_ReceiptModeStatus;
	protected Hbox hbox_ReceiptModeStatus;
	protected Combobox receiptModeStatus;
	protected Row row_RealizationDate;
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

	protected Textbox loanClosure_custCIF;
	protected ExtendedCombobox loanClosure_finReference;
	protected Combobox loanClosure_knockOffFrom;
	protected ExtendedCombobox loanClosure_refId;
	protected Datebox LoanClosure_receiptDate;
	protected Datebox LoanClosure_intTillDate;

	// Transaction Details
	protected Combobox receivedFrom;
	protected Textbox panNumber;
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
	protected AccountSelectionBox chequeAcNo;
	protected Uppercasebox paymentRef;
	// protected Datebox receivedDate;

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

	// Receipt Due Details
	protected Listbox listBoxPastdues;
	protected Listbox listBoxSchedule;

	protected Listheader listheader_ScheduleEndBal;
	protected Listheader listheader_ReceiptSchedule_SchFee;

	// Overdraft Details Headers
	protected Listheader listheader_LimitChange;
	protected Listheader listheader_AvailableLimit;
	protected Listheader listheader_ODLimit;

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

	// Buttons
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab receiptDetailsTab;
	protected Tab effectiveScheduleTab;
	protected Button btnPrint;
	protected Button btnReceipt;
	protected Button btnChangeReceipt;
	protected Button btnCalcReceipts;

	private RuleService ruleService;
	private CustomerDetailsService customerDetailsService;
	private ReceiptService receiptService;
	private ReceiptCancellationService receiptCancellationService;
	private FinanceDetailService financeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private AccountEngineExecution engineExecution;
	private CommitmentService commitmentService;
	private ReceiptCalculator receiptCalculator;
	private AccrualService accrualService;
	private PartnerBankService partnerBankService;
	private NotificationService notificationService;
	private FeeWaiverHeaderService feeWaiverHeaderService;
	private EventManager eventManager;
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
	protected HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

	private boolean isPanMandatory = false;
	private boolean isKnockOff = false;
	private boolean isForeClosure = false;
	private boolean isEarlySettle = false;
	private Space panSpace;

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
	 * @throws Exception
	 */
	public void onCreate$window_ReceiptDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReceiptDialog);
		FinReceiptData receiptData = new FinReceiptData();
		FinanceMain financeMain = null;
		FinReceiptHeader finReceiptHeader = new FinReceiptHeader();

		try {
			if (arguments.containsKey("receiptData")) {
				setReceiptData((FinReceiptData) arguments.get("receiptData"));
				receiptData = getReceiptData();

				finReceiptHeader = receiptData.getReceiptHeader();

				financeDetail = receiptData.getFinanceDetail();
				financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				setFinanceDetail(financeDetail);

				formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
				amountFormat = PennantApplicationUtil.getAmountFormate(formatter);

				recordType = finReceiptHeader.getRecordType();

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(finReceiptHeader);
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

			if (arguments.containsKey("receiptListCtrl")) {
				setReceiptListCtrl((ReceiptListCtrl) arguments.get("receiptListCtrl"));
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

			// READ OVERHANDED parameters !
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();

			FinanceProfitDetail finPftDeatils = fsd.getFinPftDeatil();
			finPftDeatils = accrualService.calProfitDetails(financeMain, fsd.getFinanceScheduleDetails(), finPftDeatils,
					receiptData.getReceiptHeader().getReceiptDate());

			// set if new receord
			if (StringUtils.isBlank(finReceiptHeader.getRecordType())) {
				if (StringUtils.isBlank(receiptData.getReceiptHeader().getAllocationType())) {
					receiptData.getReceiptHeader().setAllocationType("A");
				}
				if (StringUtils.isBlank(receiptData.getReceiptHeader().getExcessAdjustTo())) {
					receiptData.getReceiptHeader().setExcessAdjustTo("E");
				}
			}

			// receiptData =
			// getReceiptCalculator().removeUnwantedManAloc(receiptData);
			setSummaryData(false);
			// set Read only mode accordingly if the object is new or not.
			if (StringUtils.isBlank(finReceiptHeader.getRecordType())) {
				doEdit();
				this.btnReceipt.setDisabled(true);
			}

			doShowDialog(finReceiptHeader);

			// set default data for closed loans
			setClosedLoanDetails(finReceiptHeader.getReference());

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
			if (receiptPurposeCtg == 2 && isEarlySettle) {
				this.excessAdjustTo.setDisabled(true);
				fillComboBox(allocationMethod, "A", PennantStaticListUtil.getAllocationMethods(), ",M,");
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
			}
			if (isForeClosure || isEarlySettle) {
				this.gb_Payable.setVisible(true);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReceiptDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Showing Customer details on Clicking Customer View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		CustomerDetails customerDetails = getCustomerDetailsService()
				.getCustomerById(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		map.put("customerDetails", customerDetails);
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul", null, map);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Finance details on Clicking Finance View Button
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchFinreference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		// Preparation of Finance Enquiry Data
		FinReceiptHeader finReceiptHeader = receiptData.getReceiptHeader();
		FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
		aFinanceEnq.setFinReference(finReceiptHeader.getReference());
		aFinanceEnq.setFinType(finReceiptHeader.getFinType());
		aFinanceEnq.setLovDescFinTypeName(finReceiptHeader.getFinTypeDesc());
		aFinanceEnq.setFinCcy(finReceiptHeader.getFinCcy());
		aFinanceEnq.setScheduleMethod(finReceiptHeader.getScheduleMethod());
		aFinanceEnq.setProfitDaysBasis(finReceiptHeader.getPftDaysBasis());
		aFinanceEnq.setFinBranch(finReceiptHeader.getFinBranch());
		aFinanceEnq.setLovDescFinBranchName(finReceiptHeader.getFinBranchDesc());
		aFinanceEnq.setLovDescCustCIF(finReceiptHeader.getCustCIF());

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", aFinanceEnq);
		map.put("ReceiptDialog", this);
		map.put("enquiryType", "FINENQ");
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.window_ReceiptDialog, map);

		logger.debug("Leaving " + event.toString());
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
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole(), menuItemRightName);

		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));
		logger.debug("Leaving");
	}

	//// FIXME: PV. Its should be deleted. closed status is already fetched at
	//// the time of loading. Else include closed status field along with main
	//// data fetching
	/**
	 * ticket id:124998,checking closed loans and setting default data
	 * 
	 * @param finReference
	 */
	private void setClosedLoanDetails(String finReference) {
		String closingStatus = getReceiptService().getClosingStatus(finReference, TableType.MAIN_TAB, false);
		if (StringUtils.isNotEmpty(closingStatus)
				&& !StringUtils.equals(closingStatus, FinanceConstants.CLOSE_STATUS_CANCELLED)) {
			fillComboBox(this.receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY,
					PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,EarlySettlement,EarlyPayment,");
			fillComboBox(this.excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS,
					PennantStaticListUtil.getExcessAdjustmentTypes(), ",A,");
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		// Receipts Details
		this.priBal.setFormat(amountFormat);
		this.pftBal.setFormat(amountFormat);
		this.priDue.setFormat(amountFormat);
		this.pftDue.setFormat(amountFormat);
		this.bounceDueAmount.setFormat(amountFormat);
		this.otnerChargeDue.setFormat(amountFormat);
		this.recepitInProcess.setFormat(amountFormat);
		this.recepitInprocessManual.setFormat(amountFormat);

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.paidByCustomer.setFormat(amountFormat);
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

		if (DisbursementConstants.PAYMENT_TYPE_MOB
				.equals(receiptData.getReceiptHeader().getReceiptChannel().toString())) {
			this.collectionAgentId.setMandatoryStyle(true);
		}

		appendScheduleMethod(receiptData.getReceiptHeader());
		BigDecimal recAmount = PennantApplicationUtil.formateAmount(receiptData.getReceiptHeader().getReceiptAmount(),
				formatter);
		BigDecimal cashLimit = new BigDecimal(
				SysParamUtil.getSystemParameterObject("RECEIPTCASHPAN").getSysParmValue());
		if (recAmount.compareTo(cashLimit) > 0 && StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
				DisbursementConstants.PAYMENT_TYPE_CASH)) {
			this.panSpace.setSclass("mandatory");
			isPanMandatory = true;
		}

		if (!StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER) && (StringUtils
				.equals(receiptData.getReceiptHeader().getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
						DisbursementConstants.PAYMENT_TYPE_DD))) {
			this.row_DepositDate.setVisible(true);
			this.row_DepositBank.setVisible(true);
			this.hbox_ReceiptDialog_DepositDate.setVisible(true);
		}

		if (StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_MAKER)
				|| StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_APPROVER)) {
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
		}

		if (isKnockOff) {
			// this.gb_TransactionDetails.setVisible(false);
			this.gb_InstrumentDetails.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param Receipt
	 * @throws Exception
	 */
	public void doShowDialog(FinReceiptHeader finReceiptHeader) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (finReceiptHeader.isNew()) {
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
			logger.error("Exception: ", e);
			this.window_ReceiptDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_collectionAgent"), this.collectionAgentId);
		readOnlyComponent(isReadOnly("ReceiptDialog_receivedFrom"), this.receivedFrom);
		readOnlyComponent(isReadOnly("ReceiptDialog_panNumber"), this.panNumber);
		readOnlyComponent(isReadOnly("ReceiptDialog_externalRefrenceNumber"), this.externalRefrenceNumber);

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
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);
		readOnlyComponent(true, this.collectionAgentId);
		readOnlyComponent(true, this.panNumber);
		readOnlyComponent(true, this.externalRefrenceNumber);

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

		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgReceipt
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean setSummaryData(boolean isChgReceipt)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		receiptPurposeCtg = getReceiptCalculator()
				.setReceiptCategory(receiptData.getReceiptHeader().getReceiptPurpose());
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		this.finReference.setValue(rch.getReference());

		if (receiptPurposeCtg == 2) {
			receiptData.setForeClosure(isForeClosure);
			if (!isForeClosure) {
				isEarlySettle = true;
			}
		}

		Date valDate = rch.getValueDate();
		receiptData.setValueDate(valDate);
		if (orgReceiptData != null) {
			receiptData = orgReceiptData;
		} else {
			receiptService.calcuateDues(receiptData);
			if (!RepayConstants.ALLOCATIONTYPE_MANUAL.equals(receiptData.getReceiptHeader().getAllocationType())
					&& receiptData.isCalReq()) {
				receiptData = getReceiptCalculator().recalAutoAllocation(receiptData,
						receiptData.getReceiptHeader().getValueDate(), false);
			}
			if (!receiptData.isCalReq()) {
				for (ReceiptAllocationDetail allocate : receiptData.getAllocList()) {
					allocate.setTotalPaid(allocate.getPaidAmount());
					allocate.setTotRecv(allocate.getTotalDue());
					if (allocate.getAllocationTo() == 0) {
						allocate.setTypeDesc(
								Labels.getLabel("label_RecceiptDialog_AllocationType_" + allocate.getAllocationType()));
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

		this.priBal.setValue(PennantAppUtil.formateAmount(rpyMain.getPrincipalBalance(), formatter));
		this.pftBal.setValue(PennantAppUtil.formateAmount(rpyMain.getProfitBalance(), formatter));
		this.priDue.setValue(PennantAppUtil.formateAmount(rpyMain.getOverduePrincipal(), formatter));
		this.pftDue.setValue(PennantAppUtil.formateAmount(rpyMain.getOverdueProfit(), formatter));
		this.bounceDueAmount.setValue(PennantAppUtil.formateAmount(rch.getTotalBounces().getTotalDue(), formatter));
		this.otnerChargeDue.setValue(PennantAppUtil.formateAmount(rch.getTotalRcvAdvises().getTotalDue(), formatter));

		// Receipt Basic Details
		this.custCIF.setValue(custCIFname);
		this.finReference.setValue(rpyMain.getFinReference());
		this.finType.setValue(rpyMain.getFinType() + " - " + rpyMain.getLovDescFinTypeName());
		this.finBranch.setValue(rpyMain.getFinBranch() + " - " + rpyMain.getLovDescFinBranchName());
		this.finCcy.setValue(rpyMain.getFinCcy());

		setBalances();
		logger.debug("Leaving");
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
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnReceipt.isVisible());
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
		logger.debug("Entering" + event.toString());
		if (!isValidateData(true)) {
			return;
		}
		Cloner cloner = new Cloner();
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(orgScheduleList);
		FinReceiptData tempReceiptData = cloner.deepClone(receiptData);
		setOrgReceiptData(tempReceiptData);

		boolean isCalcCompleted = true;
		if (receiptPurposeCtg > 0) {
			isCalcCompleted = recalEarlyPaySchd(true);
			if (isCalcCompleted) {
				this.effectiveScheduleTab.setVisible(true);
			}
		} else {
			isCalcCompleted = true;
			/*
			 * receiptData = calculateRepayments(); setRepayDetailData();
			 */
		}

		Listitem item;
		for (int i = 0; i < receiptData.getReceiptHeader().getAllocationsSummary().size(); i++) {

			item = listBoxPastdues.getItems().get(i);
			CurrencyBox allocationWaived = (CurrencyBox) item.getFellowIfAny("AllocateWaived_" + i);
			allocationWaived.setReadonly(true);
		}

		// Do readonly to all components
		if (isCalcCompleted) {
			doReadOnly(true);
			this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
			this.btnChangeReceipt.setDisabled(true);
			this.btnCalcReceipts.setDisabled(true);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Processing Calculation button visible , if amount modified
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$receiptAmount(Event event) throws InterruptedException {
		logger.debug("Entering");

		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));

		BigDecimal receiptAmount = this.receiptAmount.getActualValue();
		receiptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount, formatter);
		receiptData.getReceiptHeader().setReceiptAmount(receiptAmount);

		resetAllocationPayments();
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Calculation button visible , if Value Date modified
	 * 
	 * @param event
	 */
	public void onChange$receivedDate(Event event) {
		logger.debug("Entering");

		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));

		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		fillComboBox(this.allocationMethod, RepayConstants.ALLOCATIONTYPE_AUTO,
				PennantStaticListUtil.getAllocationMethods(), "");

		resetAllocationPayments();

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
				HashMap<String, Object> executeMap = bounceReason.getDeclaredFieldValues();

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

		if (!StringUtils.isEmpty(dType) && !StringUtils.equals(dType, PennantConstants.List_Select)
				&& StringUtils.equals(dType, RepayConstants.RECEIPTMODE_ESCROW)) {

			fillComboBox(this.receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY,
					PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
			this.receiptPurpose.setDisabled(true);
		} else {
			this.receiptPurpose.setDisabled(false);
		}
		checkByReceiptMode(dType, true);
		resetAllocationPayments();

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
			this.valueDate.setValue(DateUtility.getAppDate());
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

			FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
			Filter fundingAcFilters[] = new Filter[4];
			fundingAcFilters[0] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
			fundingAcFilters[1] = new Filter("FinType", finType.getFinType(), Filter.OP_EQUAL);
			fundingAcFilters[2] = new Filter("PaymentMode", recMode, Filter.OP_EQUAL);
			if (RepayConstants.RECEIPTMODE_ONLINE.equals(recMode)) {
				fundingAcFilters[2] = new Filter("PaymentMode", receiptData.getReceiptHeader().getSubReceiptMode(),
						Filter.OP_EQUAL);
			}
			fundingAcFilters[3] = new Filter("EntityCode", finType.getLovDescEntityCode(), Filter.OP_EQUAL);
			Filter.and(fundingAcFilters);
			this.fundingAccount.setFilters(fundingAcFilters);
			// this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);

			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {

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

				if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)) {
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo
							.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));

					if (isUserAction) {
						this.depositDate.setValue(DateUtility.getAppDate());
						// this.receivedDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(DateUtility.getAppDate());
					}

				} else {
					this.row_ChequeAcNo.setVisible(false);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_DDFavourNo.value"));

					if (isUserAction) {
						this.depositDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(DateUtility.getAppDate());
					}
				}

				if (isUserAction) {
					this.favourName.setValue(Labels.getLabel("label_ClientName"));
				}

			} else if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)) {

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
				&& ((StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
						|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)))) {
			this.fundingAccount.setMandatoryStyle(true);
			this.fundingAccount.setReadonly(false);

		} else if (StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER)
				&& ((!StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
						&& !StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)
						&& !StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)))) {
			this.fundingAccount.setMandatoryStyle(true);
		}

		// Due to changes in Receipt Amount, call Auto Allocations
		if (isUserAction) {
			resetAllocationPayments();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * 
	 * @param event
	 */
	public void onChange$allocationMethod(Event event) {
		logger.debug("Entering");
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);

		if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			resetAllocationPayments();
		} else if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)) {
			receiptData.getReceiptHeader().setAllocationType(allocateMthd);
			doFillAllocationDetail();
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
		if (!rch.getReceiptMode().equals(RepayConstants.RECEIPTMODE_CHEQUE)
				&& !rch.getReceiptMode().equals(RepayConstants.RECEIPTMODE_DD)) {
			exclude = ",R,B,";
		}
		if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			this.bounceDate.setValue(rch.getBounceDate());
			if (rch.getBounceDate() == null) {
				this.bounceDate.setValue(DateUtility.getAppDate());
			}

			ManualAdvise bounceReason = rch.getManualAdvise();
			if (bounceReason != null) {
				this.bounceCode.setValue(String.valueOf(bounceReason.getBounceID()), bounceReason.getBounceCode());
				this.bounceCharge
						.setValue(PennantApplicationUtil.formateAmount(bounceReason.getAdviseAmount(), formatter));
				this.bounceRemarks.setValue(bounceReason.getRemarks());
			}
		} else if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
			this.cancelReason.setValue(rch.getCancelReason(), rch.getCancelReasonDesc());
		} else if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			this.realizationDate.setValue(rch.getRealizationDate());
		}

		fillComboBox(this.receiptModeStatus, rch.getReceiptModeStatus(), PennantStaticListUtil.getReceiptModeStatus(),
				exclude);
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
	 * Method for Allocation Details recalculation
	 */
	private void resetAllocationPayments() {
		logger.debug("Entering");

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
		if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			receiptData = getReceiptCalculator().recalAutoAllocation(receiptData, valueDate, false);
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
		logger.debug("Leaving");
	}

	/**
	 * Method for on Changing Waiver Amounts
	 */
	private void changeWaiver() {
		receiptData = getReceiptCalculator().changeAllocations(receiptData);
		doFillAllocationDetail();
		setBalances();
	}

	/**
	 * Method for on Changing Paid Amounts
	 */
	private void changePaid() {
		receiptData = getReceiptCalculator().setTotals(receiptData, 0);
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
		logger.debug("Entering");
		// Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		if (receiptPurposeCtg == 1) {
			rch.setEffectSchdMethod(getComboboxValue(this.effScheduleMethod));
		}

		receiptData = getReceiptService().recalEarlyPaySchedule(receiptData);
		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
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
		String eventDesc = PennantAppUtil.getlabelDesc(receiptData.getReceiptHeader().getReceiptPurpose(),
				PennantStaticListUtil.getReceiptPurpose());
		for (int i = 0; i < disbList.size(); i++) {
			FinanceDisbursement curDisb = disbList.get(i);
			if (curDisb.getDisbDate().compareTo(actualMaturity) > 0) {
				MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("30577", new String[] { eventDesc })));
				Events.sendEvent(Events.ON_CLICK, this.btnChangeReceipt, null);
				logger.debug("Leaving");
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
			this.finSchProfitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis()));
			this.finSchReference.setValue(aFinanceMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getGrcPeriodEndDate()));
			this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn().toString() + "%");

			// Fill Effective Schedule Details
			this.effectiveScheduleTab.setVisible(true);

			/*
			 * // Dashboard Details Report doLoadTabsData(); doShowReportChart(fsd);
			 */

			// Repayments Calculation
			/*
			 * receiptData = calculateRepayments(); setRepayDetailData();
			 */
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * 
	 * @param aFinScheduleData
	 *            (FinScheduleData)
	 * 
	 */
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		// FIXME: PV: CODE REVIEW PENDING
		FinanceMain financeMain = aFinScheduleData.getFinanceMain();

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
				|| financeMain.isAlwFlexi()) {

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
			logger.debug("Leaving");
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

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finSchdData", aFinScheduleData);

			map.put("financeScheduleDetail", aScheduleDetail);
			map.put("paymentDetailsMap", rpyDetailsMap);
			map.put("penaltyDetailsMap", penaltyDetailsMap);
			map.put("window", this.window_ReceiptDialog);

			finRender.render(map, prvSchDetail, false, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
					false);
			if (i == sdSize - 1) {
				finRender.render(map, prvSchDetail, true, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
						false);
				break;
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Allocations based on Receipt Details
	 * 
	 * @return
	 */
	private FinReceiptData calculateRepayments() {
		logger.debug("Entering");

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
			if (isEarlySettle) {
				receiptData = createNonXcessRCD();
			}
		} else {
			receiptData = createNonXcessRCD();
		}
		rch.setRemarks(this.remarks.getValue());

		logger.debug("Leaving");
		return receiptData;
	}

	private String payType(String mode) {
		String payType = "";
		if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_EMIINADV)) {
			payType = RepayConstants.EXAMOUNTTYPE_EMIINADV;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_EXCESS)) {
			payType = RepayConstants.EXAMOUNTTYPE_EXCESS;
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

		// Create a new Receipt Detail for every type of excess/payable
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);

			if (payable.getTotPaidNow().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);

			if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_EMIINADV)) {
				rcd.setPaymentType(RepayConstants.RECEIPTMODE_EMIINADV);
			} else if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_EXCESS)) {
				rcd.setPaymentType(RepayConstants.RECEIPTMODE_EXCESS);
			} else if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_ADVINT)) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_ADVINT);
			} else if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_ADVEMI)) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_ADVEMI);
			} else {
				rcd.setPaymentType(RepayConstants.RECEIPTMODE_PAYABLE);
			}

			rcd.setPayAgainstID(payable.getPayableID());
			if (receiptData.getTotalPastDues().compareTo(payable.getTotPaidNow()) >= 0) {
				rcd.setDueAmount(payable.getTotPaidNow());
				receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(payable.getPaidNow()));
			} else {
				rcd.setDueAmount(receiptData.getTotalPastDues());
				receiptData.setTotalPastDues(BigDecimal.ZERO);
			}
			if (receiptPurposeCtg == 1) {
				rcd.setAmount(receiptData.getReceiptHeader().getReceiptAmount());
			} else {
				rcd.setAmount(rcd.getDueAmount());
			}
			rcd.setValueDate(rch.getValueDate());
			rcd.setReceivedDate(rch.getValueDate());
			// rcd.setReceivedDate(this.receivedDate.getValue());
			rcd.setPayOrder(rcdList.size() + 1);
			rcd.setReceiptSeqID(getReceiptSeqID(rcd));

			if (payable.getPaidGST().compareTo(BigDecimal.ZERO) > 0) {
				ReceiptTaxDetail taxDetail = new ReceiptTaxDetail();
				taxDetail.setTaxComponent(payable.getTaxType());
				taxDetail.setTotalGST(payable.getPaidGST());
				taxDetail.setPaidCGST(payable.getPaidCGST());
				taxDetail.setPaidSGST(payable.getPaidSGST());
				taxDetail.setPaidUGST(payable.getPaidUGST());
				taxDetail.setPaidIGST(payable.getPaidIGST());
				rcd.setReceiptTaxDetail(taxDetail);
			}

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

	private FinReceiptData createNonXcessRCD() {

		if (StringUtils.equals(RepayConstants.RECEIPTMODE_EXCESS, receiptData.getReceiptHeader().getReceiptMode())
				|| StringUtils.equals(RepayConstants.RECEIPTMODE_EMIINADV,
						receiptData.getReceiptHeader().getReceiptMode())
				|| StringUtils.equals(RepayConstants.RECEIPTMODE_PAYABLE,
						receiptData.getReceiptHeader().getReceiptMode())) {
			return receiptData;
		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
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

		boolean partnerBankReq = false;
		if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, rcd.getPaymentType())) {
			partnerBankReq = true;
		}

		if (partnerBankReq) {
			FinTypePartnerBank bank = (FinTypePartnerBank) this.fundingAccount.getObject();
			if (bank != null) {
				rcd.setFundingAc(bank.getPartnerBankID());
				rcd.setFundingAcDesc(bank.getPartnerBankName());
			} else {
				rcd.setFundingAc(0);
				rcd.setFundingAcDesc("");
			}
		}

		// rcd.setReceivedDate(this.receivedDate.getValue());
		rcd.setPayOrder(rcdList.size() + 1);
		if (rcd.getReceiptSeqID() <= 0) {
			rcdList.add(rcd);
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
			if (recDtl.getPaymentType().equals(dtl.getPaymentType())
					&& recDtl.getPayAgainstID() == dtl.getPayAgainstID()) {
				receiptSeqId = dtl.getReceiptSeqID();
			}
		}
		return receiptSeqId;
	}

	private void setRepayDetailData() throws InterruptedException {
		logger.debug("Entering");
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
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
				curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
				curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
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
	 * @throws Exception
	 */
	public void onClick$btnReceipt(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// FIXME: PV: CODE REVIEW PENDING
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() throws WrongValueException, InterruptedException {
		logger.debug("Entering");
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
			if (!recReject) {
				if (receiptPurposeCtg == 1 || receiptPurposeCtg == 2) {
					recalEarlyPaySchd(false);
				}

				FinReceiptData data = receiptData;
				if (!StringUtils.equals(receiptData.getReceiptHeader().getReceiptModeStatus(),
						RepayConstants.PAYSTATUS_BOUNCE)
						&& !StringUtils.equals(receiptData.getReceiptHeader().getReceiptModeStatus(),
								RepayConstants.PAYSTATUS_CANCEL)) {
					calculateRepayments();
				}
				List<FinReceiptDetail> receiptDetails = data.getReceiptHeader().getReceiptDetails();

				BigDecimal totReceiptAmt = receiptData.getTotReceiptAmount();
				BigDecimal feeAmount = receiptData.getReceiptHeader().getTotFeeAmount();
				data.getReceiptHeader().setTotFeeAmount(feeAmount);
				data.getReceiptHeader().setReceiptAmount(totReceiptAmt);
				data.getReceiptHeader().setRemarks(this.remarks.getValue());

				for (FinReceiptDetail receiptDetail : receiptDetails) {
					if (!StringUtils.equals(RepayConstants.RECEIPTMODE_EXCESS, data.getReceiptHeader().getReceiptMode())
							&& StringUtils.equals(receiptDetail.getPaymentType(),
									data.getReceiptHeader().getReceiptMode())) {
						receiptDetail.setFavourNumber(this.favourNo.getValue());
						receiptDetail.setValueDate(this.valueDate.getValue());
						receiptDetail.setBankCode(this.bankCode.getValue());
						receiptDetail.setFavourName(this.favourName.getValue());
						receiptDetail.setDepositDate(this.depositDate.getValue());
						receiptDetail.setDepositNo(this.depositNo.getValue());
						receiptDetail.setPaymentRef(this.paymentRef.getValue());
						receiptDetail.setTransactionRef(this.transactionRef.getValue());
						receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
						// receiptDetail.setReceivedDate(this.receivedDate.getValue());

						boolean partnerBankReq = false;
						if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, receiptDetail.getPaymentType())) {
							partnerBankReq = true;
						}
						// FIXME
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
								receiptDetail.setFundingAc(0);
								receiptDetail.setFundingAcDesc("");
							}
						}
						// ### 30-OCT-2018,Ticket id :124998
						receiptDetail.setStatus(getComboboxValue(receiptModeStatus));
					}
				}
				// Extended Fields
				/*
				 * if (data.getFinanceDetail().getExtendedFieldHeader() != null) {
				 * data.getFinanceDetail().setExtendedFieldRender( extendedFieldCtrl.save(!recSave)); }
				 */

			}

			/*
			 * FinReceiptHeader rch = receiptData.getReceiptHeader(); boolean isNew = rch.isNew(); String tranType = "";
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
		logger.debug("Leaving");
	}

	/**
	 * Method for Process Repayment Details
	 * 
	 * @throws Exception
	 */
	private void doProcessReceipt() throws Exception {
		logger.debug("Entering");
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
		Cloner cloner = new Cloner();
		FinReceiptData aReceiptData = cloner.deepClone(receiptData);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(rch.getRecordType())) {

				aReceiptData.getReceiptHeader().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aReceiptData.getReceiptHeader().setVersion(1);
				if (aReceiptData.getReceiptHeader().isNew()) {
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

			if (doProcess(aReceiptData, tranType)) {

				if (getReceiptListCtrl() != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(rch.getNextTaskId())) {
					rch.setNextRoleCode("");

					// Auto Printing of Cashier Receipt on Submission from
					// Cashier Stage
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						if (!StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)
								&& !StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
							Events.sendEvent("onClick", this.btnPrint, null);
						}
					}

				}
				String msg = PennantApplicationUtil.getSavingStatus(rch.getRoleCode(), rch.getNextRoleCode(),
						rch.getReference(), " Loan ", rch.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					// notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					// // FIXME Check with siva
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);

					notification.setModule("RECEIPTS");
					notification.setSubModule(module);
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(financeMain.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());

					try {
						notificationService.sendNotifications(notification, getFinanceDetail(),
								financeMain.getFinType(), getFinanceDetail().getDocumentDetailsList());
					} catch (Exception e) {
						logger.debug(Literal.EXCEPTION, e);

					}

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

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		this.favourName.setValue(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getEntityDesc());
		this.finReference.setValue(rch.getReference());
		if (StringUtils.isEmpty(rch.getAllocationType())) {
			rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		}
		if (isKnockOff) {
			this.row_knockOffRef.setVisible(true);
			this.knockoffReferenec.setValue(String.valueOf(rch.getKnockOffRefId()));
			this.row_DepositBank.setVisible(false);
		}

		fillComboBox(this.receiptPurpose, rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(),
				",FeePayment,");
		this.receiptPurpose.setDisabled(true);
		/*
		 * fillComboBox(this.excessAdjustTo, rch.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(),
		 * "");
		 */

		if (finType.isDeveloperFinance()) {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getReceiptPaymentModes(), "");
		} else {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getReceiptPaymentModes(), "");
		}

		if (isKnockOff) {
			fillComboBox(this.receiptMode, rch.getReceiptMode(), PennantStaticListUtil.getKnockOffFromVlaues(), "A,P");
		}

		this.receiptMode.setDisabled(true);
		appendReceiptMode(rch);
		// appendScheduleMethod(rch);

		this.receiptAmount.setValue(PennantApplicationUtil
				.formateAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()), formatter));
		if (isEarlySettle) {
			this.receiptAmount.setValue(PennantApplicationUtil
					.formateAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()), formatter));
		}
		this.receiptAmount.setDisabled(true);
		this.remarks.setValue(rch.getRemarks());
		this.receiptDate.setValue(rch.getReceiptDate());
		this.receiptId.setValue(String.valueOf(rch.getReceiptID()));
		this.receiptDate.setDisabled(true);

		if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYSETTLE) && isEarlySettle) {
			fillComboBox(this.allocationMethod, rch.getAllocationType(), PennantStaticListUtil.getAllocationMethods(),
					",M,");
			this.allocationMethod.setDisabled(true);
		} else {
			fillComboBox(this.allocationMethod, rch.getAllocationType(), PennantStaticListUtil.getAllocationMethods(),
					"");
		}

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

		if (rch.getReceiptMode().equals(RepayConstants.RECEIPTMODE_CASH)) {
			this.panNumber.setValue(rch.getPanNumber());
		}

		resetModeStatus(rch.getReceiptModeStatus());

		// Receipt Mode Details , if FinReceiptDetails Exists
		setBalances();
		checkByReceiptMode(rch.getReceiptMode(), false);
		this.valueDate.setValue(rch.getValueDate());
		// Separating Receipt Amounts based on user entry, if exists
		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail rcd = rch.getReceiptDetails().get(i);

				if (!StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
						&& !StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
						&& !StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
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
					this.externalRefrenceNumber.setValue(rch.getExtReference());
					this.chequeAcNo.setValue(rcd.getChequeAcNo());
					// this.receivedDate.setValue(rcd.getReceivedDate());

					boolean partnerBankReq = false;
					if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, rcd.getPaymentType())) {
						partnerBankReq = true;
					}

					if (partnerBankReq) {
						this.fundingAccount.setAttribute("fundingAccID", rcd.getFundingAc());
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

		getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);

		if (visibleSchdTab) {
			appendScheduleDetailTab(true, false);
		}

		this.recordStatus.setValue(receiptHeader.getRecordStatus());
		if (receiptPurposeCtg == 2
				&& (StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, receiptHeader.getReceiptMode())
						|| StringUtils.equals(RepayConstants.RECEIPTMODE_DD, receiptHeader.getReceiptMode()))) {
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

		if ((RepayConstants.RECEIPTMODE_ONLINE.equals(rch.getReceiptMode())) && rch.getSubReceiptMode() != null
				&& !StringUtils.equals(rch.getSubReceiptMode(), PennantConstants.List_Select)) {
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

		if (receiptPurposeCtg != 1) {
			scheduleLabel.setValue(Labels.getLabel("label_ReceiptPayment_ExcessAmountAdjustment.value"));
			this.excessAdjustTo.setVisible(true);
			this.excessAdjustTo.setDisabled(false);
			fillComboBox(excessAdjustTo, rch.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
			if (receiptPurposeCtg == 2) {
				fillComboBox(excessAdjustTo, "E", PennantStaticListUtil.getExcessAdjustmentTypes(), ",A,");
				this.excessAdjustTo.setDisabled(true);
				this.excessAdjustTo.setReadonly(true);
			}

		} else {
			this.effScheduleMethod.setVisible(true);
			this.effScheduleMethod.setDisabled(false);
			this.excessAdjustTo.setVisible(false);
			this.excessAdjustTo.setDisabled(true);
			scheduleLabel.setValue(Labels.getLabel("label_ReceiptDialog_EffecScheduleMethod.value"));
			List<ValueLabel> epyMethodList = getEffectiveSchdMethods();
			String defaultMethod = StringUtils.isEmpty(rch.getEffectSchdMethod()) ? epyMethodList.get(0).getValue()
					: rch.getEffectSchdMethod();
			fillComboBox(effScheduleMethod, defaultMethod, getEffectiveSchdMethods(), "");
		}

	}

	private List<ValueLabel> getEffectiveSchdMethods() {
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType finType = finScheduleData.getFinanceType();
		List<ValueLabel> epyMethodList = new ArrayList<>();
		if (finMain.isAlwFlexi() || finType.isDeveloperFinance()) {
			epyMethodList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
		} else {
			if (finMain.isStepFinance() && finMain.isAllowGrcPeriod()
					&& StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)
					&& DateUtility.compare(receiptData.getValueDate(), finMain.getGrcPeriodEndDate()) <= 0
					&& (StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
							|| StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT))) {
				epyMethodList
						.add(new ValueLabel(CalculationConstants.RPYCHG_STEPPOS, Labels.getLabel("label_POSStep")));
			} else {
				if (StringUtils.isNotEmpty(finType.getAlwEarlyPayMethods())) {
					String[] epMthds = finType.getAlwEarlyPayMethods().trim().split(",");
					if (epMthds.length > 0) {
						List<String> list = Arrays.asList(epMthds);
						for (ValueLabel label : PennantStaticListUtil.getEarlyPayEffectOn()) {
							if (list.contains(label.getValue().trim())) {
								epyMethodList.add(label);
							}
						}
					}
				}
			}
		}
		return epyMethodList;
	}

	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * 
	 * @param header
	 * @param allocatePaidMap
	 */

	private void doFillAllocationDetail() {
		logger.debug("Entering");
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

		for (int i = 0; i < allocationList.size(); i++) {
			createAllocateItem(allocationList.get(i), isManAdv, label, i);
		}

		addDueFooter(formatter);
		addExcessAmt();

		if (receiptData.getPaidNow().compareTo(receiptData.getReceiptHeader().getReceiptAmount()) > 0) {
			MessageUtil.showError(Labels.getLabel("label_Allocation_More_than_receipt"));
			return;
		}

		logger.debug("Leaving");
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(receiptData.getRemBal(), formatter));

			lc.setId("ExcessAmount");
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			this.listBoxPastdues.appendChild(item);
		}
	}

	private void createAllocateItem(ReceiptAllocationDetail allocate, boolean isManAdv, String desc, int idx) {
		logger.debug("Entering");
		String allocateMthd = getComboboxValue(this.allocationMethod);

		Listitem item = new Listitem();
		Listcell lc = null;
		addBoldTextCell(item, allocate.getTypeDesc(), allocate.isSubListAvailable(), idx);
		addAmountCell(item, allocate.getTotRecv(), ("AllocateActualDue_" + idx), false);
		// FIXME: PV. Pending code to get in process allocations
		addAmountCell(item, allocate.getInProcess(), ("AllocateInProess_" + idx), true);
		addAmountCell(item, allocate.getTotalDue(), ("AllocateCurDue_" + idx), true);

		// Editable Amount - Total Paid
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

		lc = new Listcell();
		CurrencyBox allocationWaived = new CurrencyBox();
		allocationWaived.setStyle("text-align:right;");
		allocationWaived.setBalUnvisible(true, true);
		setProps(allocationWaived, false, formatter, 120);
		allocationWaived.setId("AllocateWaived_" + idx);
		allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocate.getWaivedAmount(), formatter));
		allocationWaived.addForward("onFulfill", this.window_ReceiptDialog, "onAllocateWaivedChange", idx);
		allocationWaived.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_WaivedAmount"));

		if (isForeClosure) {
			if (PennantStaticListUtil.getNoWaiverList().contains(allocate.getAllocationType())) {
				allocationWaived.setReadonly(true);
			}
		}

		lc.appendChild(allocationWaived);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		if (allocate.isEditable() && StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)) {
			// allocationPaid.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
			allocationPaid.setReadonly(!getUserWorkspace().isAllowed("ReceiptDialog_PaidAmount"));
		}

		// Balance Due AMount
		addAmountCell(item, allocate.getBalance(), ("AllocateBalDue_" + idx), true);

		// if (allocate.isEditable()){
		this.listBoxPastdues.appendChild(item);
		// }

		logger.debug("Leaving");
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
		BigDecimal inProc = BigDecimal.ZERO;
		BigDecimal paid = BigDecimal.ZERO;
		BigDecimal waived = BigDecimal.ZERO;
		List<ReceiptAllocationDetail> allocList = receiptData.getReceiptHeader().getAllocationsSummary();
		if (!receiptData.isCalReq()) {
			allocList = receiptData.getReceiptHeader().getAllocations();
		}

		for (ReceiptAllocationDetail allocate : allocList) {

			if (allocate.isEditable()) {
				totRecv = totRecv.add(allocate.getTotRecv());
				totDue = totDue.add(allocate.getTotalDue());
				inProc = inProc.add(allocate.getInProcess());
				paid = paid.add(allocate.getPaidAmount());
				waived = waived.add(allocate.getWaivedAmount());
			}

		}
		receiptData.setPaidNow(paid);
		addAmountCell(item, totRecv, null, true);
		addAmountCell(item, inProc, null, true);
		addAmountCell(item, totDue, null, true);
		addAmountCell(item, paid, null, true);
		addAmountCell(item, waived, null, true);
		addAmountCell(item, totDue.subtract(paid).subtract(waived), null, true);

		this.listBoxPastdues.appendChild(item);
	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAllocatePaidChange(ForwardEvent event) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocatePaid_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocationsSummary().get(idx);

		CurrencyBox allocationPaid = (CurrencyBox) this.listBoxPastdues.getFellow(id);

		BigDecimal paidAmount = PennantApplicationUtil.unFormateAmount(allocationPaid.getValidateValue(), formatter);
		BigDecimal dueAmount = rch.getAllocationsSummary().get(idx).getTotalDue();
		BigDecimal waivedAmount = rch.getAllocationsSummary().get(idx).getWaivedAmount();
		if (paidAmount.compareTo(dueAmount.subtract(waivedAmount)) > 0) {
			paidAmount = dueAmount.subtract(waivedAmount);
		}
		rch.getAllocationsSummary().get(idx).setTotalPaid(paidAmount);
		rch.getAllocationsSummary().get(idx).setPaidAmount(paidAmount);

		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitAllocSummary(receiptData, idx);
		} else {
			if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_EMI)) {
				allocateEmi(paidAmount);
			} else {
				for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
					if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())) {
						allocteDtl.setTotalPaid(paidAmount);
						allocteDtl.setPaidAmount(paidAmount);
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
		logger.debug("Leaving");
	}

	private void allocateEmi(BigDecimal paidAmount) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, paidAmount);
		for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
			if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_PFT)) {
				if (emiSplit[1].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[1] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[1]);
				allocteDtl.setPaidAmount(emiSplit[1]);
			}

			if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_NPFT)) {
				if (emiSplit[2].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[2] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[2]);
				allocteDtl.setPaidAmount(emiSplit[2]);
			}
			if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_PRI)) {
				if (emiSplit[0].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount())) > 0) {
					emiSplit[0] = allocteDtl.getTotalDue().subtract(allocteDtl.getWaivedAmount());
				}
				allocteDtl.setTotalPaid(emiSplit[0]);
				allocteDtl.setPaidAmount(emiSplit[0]);
			}
			if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_TDS)) {
				allocteDtl.setTotalPaid(emiSplit[1].subtract(emiSplit[2]));
				allocteDtl.setPaidAmount(emiSplit[1].subtract(emiSplit[2]));
			}
			if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_EMI)) {
				allocteDtl.setTotalPaid(paidAmount);
				allocteDtl.setPaidAmount(paidAmount);
			}
		}

	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onAllocateWaivedChange(ForwardEvent event) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocateWaived_" + idx;

		BigDecimal priWaived = BigDecimal.ZERO;
		BigDecimal netPftWaived = BigDecimal.ZERO;
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
		if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_PRI)) {
			isEmiWaived = true;
			priWaived = allocate.getWaivedAmount();
		}

		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitAllocSummary(receiptData, idx);
		} else {
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(allocate.getAllocationType())) {
					allocteDtl.setWaivedAmount(allocate.getWaivedAmount());
					allocteDtl.setPaidAmount(allocate.getPaidAmount());
					allocteDtl.setTotalPaid(allocate.getTotalPaid());
					break;
				}
			}
		}

		if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_FUT_PFT)) {
			FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();
			List<FinanceScheduleDetail> schdDtls = fsd.getFinanceScheduleDetails();
			FinanceScheduleDetail lastSchd = schdDtls.get(schdDtls.size() - 1);
			BigDecimal npftWaived = BigDecimal.ZERO;
			BigDecimal tdsWaived = BigDecimal.ZERO;
			if (lastSchd.isTDSApplicable()) {
				tdsWaived = getReceiptCalculator().getTDS(waivedAmount);
				npftWaived = waivedAmount.subtract(tdsWaived);
			} else {
				npftWaived = waivedAmount;
			}
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_FUT_NPFT)) {
					adjustWaiver(allocteDtl, npftWaived);
				}
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_FUT_TDS)) {
					adjustWaiver(allocteDtl, tdsWaived);
				}
			}
		}

		if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_PFT)) {
			BigDecimal npftWaived = receiptCalculator.getNetProfit(receiptData, allocate.getWaivedAmount());
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_NPFT)) {
					isEmiWaived = true;
					netPftWaived = npftWaived;
					adjustWaiver(allocteDtl, npftWaived);
				}
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_TDS)) {
					adjustWaiver(allocteDtl, allocate.getWaivedAmount().subtract(npftWaived));
				}
			}
		}
		// Adjusting emi waiver
		if (isEmiWaived) {
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocationsSummary()) {
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_EMI)) {
					allocate.setWaivedAmount(BigDecimal.ZERO);
					adjustWaiver(allocteDtl, allocteDtl.getWaivedAmount().add(priWaived.add(netPftWaived)));
					break;
				}
			}
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_EMI)) {
					allocteDtl.setWaivedAmount(BigDecimal.ZERO);
					adjustWaiver(allocteDtl, allocteDtl.getWaivedAmount().add(priWaived.add(netPftWaived)));
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
		logger.debug("Leaving");
	}

	public ReceiptAllocationDetail adjustWaiver(ReceiptAllocationDetail allocate, BigDecimal waiverNow) {
		BigDecimal dueAmount = allocate.getTotalDue();
		BigDecimal paidAmount = allocate.getTotalPaid();
		BigDecimal waivedAmount = allocate.getWaivedAmount();
		BigDecimal balAmount = dueAmount.subtract(paidAmount).subtract(waivedAmount);
		if (waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (balAmount.compareTo(BigDecimal.ZERO) == 0) {
				if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
					paidAmount = paidAmount.add(waivedAmount);
				}
			}
			waivedAmount = BigDecimal.ZERO;
		}
		balAmount = dueAmount.subtract(paidAmount).subtract(waivedAmount);
		if (waiverNow.compareTo(balAmount) > 0) {
			paidAmount = paidAmount.subtract(waiverNow.subtract(balAmount));
		}
		allocate.setTotalPaid(paidAmount);
		allocate.setWaivedAmount(waiverNow);
		allocate.setPaidAmount(paidAmount);
		return allocate;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		Date fromDate = rch.getValueDate();
		Date toDate = DateUtility.getAppDate();
		if (FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			fromDate = rch.getReceiptDate();
		}

		// FIXME: PV: CODE REVIEW PENDING
		if (!this.receiptPurpose.isDisabled()) {
			this.receiptPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPurpose(),
					Labels.getLabel("label_ReceiptDialog_ReceiptPurpose.value")));
		}

		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled()) {
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
							fromDate, toDate, true));
		}

		if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)) {

			if (!this.chequeAcNo.isReadonly()) {
				this.chequeAcNo.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptDialog_ChequeAccountNo.value"), null, false));
			}
		}

		if (!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			if (StringUtils.equals(module, FinanceConstants.DEPOSIT_MAKER)
					&& (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD))) {
				if (!this.fundingAccount.isReadonly()) {
					this.fundingAccount.setConstraint(new PTStringValidator(
							Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true));
				}
			} else if (StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER)
					&& (!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)
							&& !StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD)
							&& !StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CASH))) {
				if (!this.fundingAccount.isReadonly()) {
					this.fundingAccount.setConstraint(new PTStringValidator(
							Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true));
				}
			}

			/*
			 * if (!this.receivedDate.isDisabled()) { Date prvMaxReceivedDate =
			 * getReceiptService().getMaxReceiptDate(financeMain.getFinReference ());
			 * 
			 * // ### 26-09-2018 Ticket id :124998 if (prvMaxReceivedDate == null || receiptPurposeCtg == 0) {
			 * prvMaxReceivedDate = financeMain.getFinStartDate(); } Date curBussDate = DateUtility.getAppDate(); if
			 * (DateUtility.compare(prvMaxReceivedDate, curBussDate) > 0) { curBussDate = prvMaxReceivedDate; }
			 * this.receivedDate .setConstraint(new PTDateValidator(Labels.getLabel(
			 * "label_ReceiptDialog_ReceivedDate.value"), true, prvMaxReceivedDate, curBussDate, true)); }
			 */
		}

		if (!this.collectionAgentId.isReadonly() && this.collectionAgentId.isVisible()) {
			this.collectionAgentId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_CollectionAgentId.value"),
							null, collectionAgentId.isMandatory(), true));
		}
		if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD)
				|| StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)) {

			if (!this.favourNo.isReadonly()) {
				String label = Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value");
				if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD)) {
					label = Labels.getLabel("label_ReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(
						new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}

			if (!this.valueDate.isDisabled()) {
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ValueDate.value"),
						true, financeMain.getFinStartDate(), DateUtility.getAppDate(), true));
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
								true, financeMain.getFinStartDate(), DateUtility.getAppDate(), true));
			}

			if (!this.depositNo.isReadonly()) {
				this.depositNo
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_depositNo.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}

		if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_ONLINE)) {

			if (!this.transactionRef.isReadonly()) {
				this.transactionRef
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_tranReference.value"),
								PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
		}

		if (!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_EXCESS)) {
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
		int finFormatter = CurrencyUtil
				.getFormat(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		Date curBussDate = DateUtility.getAppDate();
		FinReceiptHeader header = receiptData.getReceiptHeader();
		header.setReceiptDate(curBussDate);
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReference(this.finReference.getValue());
		try {
			header.setReceiptPurpose(getComboboxValue(receiptPurpose));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * try { header.setSubReceiptMode(getComboboxValue(receiptType)); } catch (WrongValueException we) {
		 * wve.add(we); }
		 */
		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
			if (header.getSubReceiptMode().equals("#")) {
				header.setSubReceiptMode(header.getReceiptMode());
			}
		} catch (WrongValueException we) {
			wve.add(we);
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
		if (effScheduleMethod.isVisible()) {
			try {
				header.setEffectSchdMethod(getComboboxValue(effScheduleMethod));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			header.setReceiptDate(this.receiptDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		String status = "";
		try {
			if (!isReadOnly("ReceiptDialog_receiptModeStatus") && isValidComboValue(this.receiptModeStatus,
					Labels.getLabel("label_ReceiptDialog_ReceiptModeStatus.value"))) {
				status = getComboboxValue(receiptModeStatus);
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

			bounce.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
			bounce.setFinReference(header.getReference());
			bounce.setFeeTypeID(0);
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
			bounce.setPostDate(DateUtility.getPostDate());

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
			header.setExtReference(this.externalRefrenceNumber.getValue());
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
			if (receivedFrom.isVisible()) {
				header.setReceivedFrom(getComboboxValue(receivedFrom));
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
			this.receiptDetailsTab.setSelected(true);
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	private void validateReceivedDate() {
		logger.debug("Entering");
		Date appDate = DateUtility.getAppDate();
		// Date receivedDate = this.receivedDate.getValue();
		Date curMonthStartDate = DateUtility.getMonthStartDate(appDate);
		Date currentMonthScheduleDate = null;
		// FIXME: PV: CODE REVIEW PENDING
		// Get the current month schedule date
		List<FinanceScheduleDetail> financeScheduleDetails = receiptData.getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails();
		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
			if ((DateUtility.getMonth(appDate) == DateUtility.getMonth(curSchd.getSchDate()))
					&& (DateUtility.getYear(appDate) == DateUtility.getYear(curSchd.getSchDate()))
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

		logger.debug("Leaving");
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
	 * 
	 * @throws Exception
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public FinanceDetail onExecuteStageAccDetail() throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
			getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

			// Finance Accounting Details Execution
			executeAccounting(true);
		} else {
			receiptData.getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);
		}

		logger.debug("Leaving");
		return receiptData.getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 * 
	 * @throws Exception
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(finMain.getFinReference());
		Date dateValueDate = DateUtility.getAppDate();
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
		Map<String, BigDecimal> extDataMap = new HashMap<>();
		BigDecimal totPayable = BigDecimal.ZERO;
		for (int rcpt = 0; rcpt < rcptSize; rcpt++) {

			FinReceiptDetail receiptDetail = receiptDetails.get(rcpt);
			if (!payableLoopProcess && !StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
					FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				extDataMap = new HashMap<>();
				totPayable = BigDecimal.ZERO;
			}

			totPayable = totPayable.add(receiptDetail.getAmount());
			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
				extDataMap.put("PA_ReceiptAmount", totPayable);
			} else if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)) {
				extDataMap.put("EX_ReceiptAmount", receiptDetail.getAmount());
			} else if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)) {
				extDataMap.put("EA_ReceiptAmount", receiptDetail.getAmount());
			} else if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_ADVINT)) {
				extDataMap.put("EAI_ReceiptAmount", receiptDetail.getAmount());
			}  else if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_ADVEMI)) {
				extDataMap.put("EAE_ReceiptAmount", receiptDetail.getAmount());
			} else {
				extDataMap.put("PB_ReceiptAmount", receiptDetail.getAmount());
			}

			addZeroifNotContains(extDataMap, "PA_ReceiptAmount");
			addZeroifNotContains(extDataMap, "EX_ReceiptAmount");
			addZeroifNotContains(extDataMap, "EA_ReceiptAmount");
			addZeroifNotContains(extDataMap, "PB_ReceiptAmount");
			addZeroifNotContains(extDataMap, "EAI_ReceiptAmount");
			addZeroifNotContains(extDataMap, "EAE_ReceiptAmount");

			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
				if (extDataMap.containsKey(receiptDetail.getFeeTypeCode() + "_P")) {
					extDataMap.put(receiptDetail.getFeeTypeCode() + "_P",
							extDataMap.get(receiptDetail.getFeeTypeCode() + "_P").add(receiptDetail.getAmount()));
				} else {
					extDataMap.put(receiptDetail.getFeeTypeCode() + "_P", receiptDetail.getAmount());
				}
				if (receiptDetail.getReceiptTaxDetail() != null) {
					if (extDataMap.containsKey(receiptDetail.getFeeTypeCode() + "_CGST_P")) {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_CGST_P",
								extDataMap.get(receiptDetail.getFeeTypeCode() + "_CGST_P")
										.add(receiptDetail.getReceiptTaxDetail().getPaidCGST()));
					} else {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_CGST_P",
								receiptDetail.getReceiptTaxDetail().getPaidCGST());
					}
					if (extDataMap.containsKey(receiptDetail.getFeeTypeCode() + "_SGST_P")) {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_SGST_P",
								extDataMap.get(receiptDetail.getFeeTypeCode() + "_SGST_P")
										.add(receiptDetail.getReceiptTaxDetail().getPaidSGST()));
					} else {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_SGST_P",
								receiptDetail.getReceiptTaxDetail().getPaidSGST());
					}
					if (extDataMap.containsKey(receiptDetail.getFeeTypeCode() + "_UGST_P")) {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_UGST_P",
								extDataMap.get(receiptDetail.getFeeTypeCode() + "_UGST_P")
										.add(receiptDetail.getReceiptTaxDetail().getPaidUGST()));
					} else {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_UGST_P",
								receiptDetail.getReceiptTaxDetail().getPaidUGST());
					}
					if (extDataMap.containsKey(receiptDetail.getFeeTypeCode() + "_IGST_P")) {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_IGST_P",
								extDataMap.get(receiptDetail.getFeeTypeCode() + "_IGST_P")
										.add(receiptDetail.getReceiptTaxDetail().getPaidIGST()));
					} else {
						extDataMap.put(receiptDetail.getFeeTypeCode() + "_IGST_P",
								receiptDetail.getReceiptTaxDetail().getPaidIGST());
					}
				}
			}

			FinRepayHeader repayHeader = receiptDetail.getRepayHeader();

			extDataMap.clear();

			amountCodes.setPenaltyPaid(BigDecimal.ZERO);
			amountCodes.setPenaltyWaived(BigDecimal.ZERO);
			amountCodes.setPaymentType(receiptDetail.getPaymentType());
			amountCodes.setUserBranch(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());

			// FIXME: FIND THE LOGIC TO SET payableLoopProcess = false; AS PER
			// OLD CODE
			payableLoopProcess = false;

			if (!StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent())
					&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
					&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
				amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
				amountCodes.setToExcessAmt(BigDecimal.ZERO);
				amountCodes.setToEmiAdvance(BigDecimal.ZERO);
				if (StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS)) {
					amountCodes.setToExcessAmt(repayHeader.getRepayAmount());
				} else {
					amountCodes.setToEmiAdvance(repayHeader.getRepayAmount());
				}

				aeEvent.getAcSetIDList().clear();
				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
							AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
				} else {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(),
							AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
				}

				// Assignment Percentage
				Set<String> excludeFees = null;
				if (finMain.getAssignmentId() > 0) {
					Assignment assignment = getReceiptService().getAssignment(finMain.getAssignmentId(), "");
					if (assignment != null) {
						amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
						List<AssignmentDealExcludedFee> excludeFeesList = getReceiptService()
								.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
						if (CollectionUtils.isNotEmpty(excludeFeesList)) {
							excludeFees = new HashSet<String>();
							for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
								excludeFees.add(excludeFee.getFeeTypeCode());
							}
						}
					}
				}

				HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

				if (excludeFees != null) {
					dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
				}

				if (!feesExecuted && StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
						FinanceConstants.FINSER_EVENT_SCHDRPY)) {
					feesExecuted = true;
					prepareFeeRulesMap(amountCodes, dataMap, receiptDetail.getPaymentType());
				}

				// Receipt Detail external usage Fields Insertion into
				// DataMap
				dataMap.putAll(extDataMap);

				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
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
				penaltyCGSTPaid = penaltyCGSTPaid.add(rsd.getPaidPenaltyCGST());
				penaltySGSTPaid = penaltySGSTPaid.add(rsd.getPaidPenaltySGST());
				penaltyIGSTPaid = penaltyIGSTPaid.add(rsd.getPaidPenaltyIGST());
				penaltyUGSTPaid = penaltyUGSTPaid.add(rsd.getPaidPenaltyUGST());

				// Fee Details
				amountCodes.setSchFeePay(amountCodes.getSchFeePay().add(rsd.getSchdFeePayNow()));
				amountCodes.setInsPay(amountCodes.getInsPay().add(rsd.getSchdInsPayNow()));

				// Waived Amounts
				amountCodes.setPriWaived(amountCodes.getPriWaived().add(rsd.getPriSchdWaivedNow()));
				amountCodes.setPftWaived(amountCodes.getPftWaived().add(rsd.getPftSchdWaivedNow()));
				amountCodes.setLpiWaived(amountCodes.getLpiWaived().add(rsd.getLatePftSchdWaivedNow()));
				amountCodes.setFeeWaived(amountCodes.getFeeWaived().add(rsd.getSchdFeeWaivedNow()));
				amountCodes.setInsWaived(amountCodes.getInsWaived().add(rsd.getSchdInsWaivedNow()));
			}

			amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());

			// If Payable Continue for All Advises
			if (payableLoopProcess) {
				continue;
			}

			// Accrual & Future Paid Details
			if (StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

				int schSize = receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size();
				FinanceScheduleDetail lastSchd = receiptData.getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails().get(schSize - 1);

				FinanceScheduleDetail oldLastSchd = null;
				if (lastSchd.isFrqDate()) {
					oldLastSchd = getFinanceDetailService().getFinSchduleDetails(finMain.getFinReference(),
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

				Date curMonthStartDate = DateUtility.getMonthStartDate(lastSchd.getSchDate());

				// UnAccrual Calculation
				BigDecimal unaccrue = BigDecimal.ZERO;

				if (oldLastSchd == null) {
					FinanceScheduleDetail lastPrvSchd = receiptData.getFinanceDetail().getFinScheduleData()
							.getFinanceScheduleDetails().get(schSize - 2);
					if (DateUtility.compare(curMonthStartDate, lastPrvSchd.getSchDate()) <= 0) {

						// Accrual amounts
						amountCodes.setAccruedPaid(BigDecimal.ZERO);
						amountCodes.setAccrueWaived(BigDecimal.ZERO);

						// UnAccrual Amounts
						unaccrue = newProfitDetail.getTotalPftSchd().subtract(newProfitDetail.getAmzTillLBD());

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
					} else {

						// UnAccrual Amounts
						unaccrue = newProfitDetail.getTotalPftSchd().subtract(newProfitDetail.getPrvMthAmz());

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

						// Accrual amounts
						BigDecimal accrue = newProfitDetail.getTotalPftSchd().subtract(newProfitDetail.getAmzTillLBD())
								.subtract(unaccrue);

						// Accrual Paid
						if (amountCodes.getPftWaived().compareTo(unaccrue.add(accrue)) >= 0) {
							amountCodes.setAccruedPaid(BigDecimal.ZERO);
						} else {
							if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
								amountCodes.setAccruedPaid(accrue.add(unaccrue).subtract(amountCodes.getPftWaived()));
							} else {
								amountCodes.setAccruedPaid(accrue);
							}
						}

						// Accrual Waived
						if (amountCodes.getPftWaived().compareTo(accrue.add(unaccrue)) >= 0) {
							amountCodes.setAccrueWaived(accrue);
						} else {
							if (amountCodes.getPftWaived().compareTo(unaccrue) >= 0) {
								amountCodes.setAccrueWaived(amountCodes.getPftWaived().subtract(unaccrue));
							} else {
								amountCodes.setAccrueWaived(BigDecimal.ZERO);
							}
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
				} else {

					// Accrual amounts
					amountCodes.setAccruedPaid(BigDecimal.ZERO);
					amountCodes.setAccrueWaived(BigDecimal.ZERO);

					// UnAccrual amounts
					amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
					amountCodes.setUnAccrueWaived(BigDecimal.ZERO);

					BigDecimal lastSchdPriBal = lastSchd.getPrincipalSchd()
							.subtract(oldLastSchd.getPrincipalSchd().subtract(oldLastSchd.getSchdPriPaid()));

					// Future Principal Paid
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setFuturePriPaid(BigDecimal.ZERO);
					} else {
						amountCodes.setFuturePriPaid(lastSchdPriBal.subtract(amountCodes.getPriWaived()));
					}

					// Future Principal Waived
					if (amountCodes.getPriWaived().compareTo(lastSchdPriBal) > 0) {
						amountCodes.setFuturePriWaived(lastSchdPriBal);
					} else {
						amountCodes.setFuturePriWaived(amountCodes.getPriWaived());
					}
				}

				if (finMain.isTDSApplicable()) {
					// TDS for Last Installment
					BigDecimal tdsPerc = new BigDecimal(
							SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					amountCodes.setLastSchTds((amountCodes.getLastSchPftPaid().multiply(tdsPerc))
							.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN));

					// Splitting TDS amount into Accrued and Unaccrued Paid
					// basis
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
			if (StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				eventCode = AccountEventConstants.ACCEVENT_REPAY;
			} else if (StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
				if (pftChgExecuted) {
					amountCodes.setPftChg(BigDecimal.ZERO);
				}
				pftChgExecuted = true;
			} else if (StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
				if (pftChgExecuted) {
					amountCodes.setPftChg(BigDecimal.ZERO);
				}
				pftChgExecuted = true;
			}

			if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
				aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
						eventCode, FinanceConstants.MODULEID_PROMOTION));
			} else {
				aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
						FinanceConstants.MODULEID_FINTYPE));
			}

			aeEvent.setAccountingEvent(eventCode);

			// Assignment Percentage
			Set<String> excludeFees = null;
			if (finMain.getAssignmentId() > 0) {
				Assignment assignment = getReceiptService().getAssignment(finMain.getAssignmentId(), "");
				if (assignment != null) {
					amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
					List<AssignmentDealExcludedFee> excludeFeesList = getReceiptService()
							.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
					if (CollectionUtils.isNotEmpty(excludeFeesList)) {
						excludeFees = new HashSet<String>();
						for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
							excludeFees.add(excludeFee.getFeeTypeCode());
						}
					}
				}
			}

			HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

			if (excludeFees != null) {
				dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
			}

			// Receipt Detail external usage Fields Insertion into DataMap
			dataMap.putAll(extDataMap);

			if (!feesExecuted && (StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
					FinanceConstants.FINSER_EVENT_SCHDRPY)
					|| (!StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
							FinanceConstants.FINSER_EVENT_SCHDRPY)
							&& StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
									repayHeader.getFinEvent())))) {
				feesExecuted = true;
				prepareFeeRulesMap(amountCodes, dataMap, receiptDetail.getPaymentType());
			}
			aeEvent.setDataMap(dataMap);
			aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
			returnSetEntries.addAll(aeEvent.getReturnDataSet());

			if (amountCodes.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0
					|| amountCodes.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {

				aeEvent.getAcSetIDList().clear();
				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
							AccountEventConstants.ACCEVENT_LATEPAY, FinanceConstants.MODULEID_PROMOTION));
				} else {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(),
							AccountEventConstants.ACCEVENT_LATEPAY, FinanceConstants.MODULEID_FINTYPE));
				}

				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

				// LPP GST Amount setting
				aeEvent.getDataMap().put("LPP_CGST_P", penaltyCGSTPaid);
				aeEvent.getDataMap().put("LPP_SGST_P", penaltySGSTPaid);
				aeEvent.getDataMap().put("LPP_UGST_P", penaltyIGSTPaid);
				aeEvent.getDataMap().put("LPP_IGST_P", penaltyUGSTPaid);

				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());
			}

			// Reset Payment Details
			amountCodes.setRpTot(BigDecimal.ZERO);
			amountCodes.setRpPft(BigDecimal.ZERO);
			amountCodes.setRpPri(BigDecimal.ZERO);
			amountCodes.setSchFeePay(BigDecimal.ZERO);
			amountCodes.setInsPay(BigDecimal.ZERO);
			amountCodes.setPriWaived(BigDecimal.ZERO);
			amountCodes.setPftWaived(BigDecimal.ZERO);
			amountCodes.setLpiWaived(BigDecimal.ZERO);
			amountCodes.setFeeWaived(BigDecimal.ZERO);
			amountCodes.setInsWaived(BigDecimal.ZERO);
			amountCodes.setPenaltyPaid(BigDecimal.ZERO);
			amountCodes.setPenaltyWaived(BigDecimal.ZERO);
			amountCodes.setRpTds(BigDecimal.ZERO);
			amountCodes.setAccruedPaid(BigDecimal.ZERO);
			amountCodes.setUnAccruedPaid(BigDecimal.ZERO);
			amountCodes.setFuturePriPaid(BigDecimal.ZERO);

			// Manual Advise Postings
			List<ManualAdviseMovements> movements = receiptDetail.getAdvMovements();
			if (movements != null && !movements.isEmpty()) {

				// Summing Same Type of Fee Types to Single Field
				HashMap<String, BigDecimal> movementMap = new HashMap<>();
				for (int i = 0; i < movements.size(); i++) {
					ManualAdviseMovements movement = movements.get(i);

					// Bounce Charges
					BigDecimal amount = BigDecimal.ZERO;
					String keyCode = null;
					if (StringUtils.isEmpty(movement.getFeeTypeCode())) {

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

					// Tax Details
					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_CGST_P")) {
						amount = movementMap.get(keyCode + "_CGST_P");
					}
					movementMap.put(keyCode + "_CGST_P", amount.add(movement.getPaidCGST()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_SGST_P")) {
						amount = movementMap.get(keyCode + "_SGST_P");
					}
					movementMap.put(keyCode + "_SGST_P", amount.add(movement.getPaidSGST()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_IGST_P")) {
						amount = movementMap.get(keyCode + "_IGST_P");
					}
					movementMap.put(keyCode + "_IGST_P", amount.add(movement.getPaidIGST()));

					amount = BigDecimal.ZERO;
					if (movementMap.containsKey(keyCode + "_UGST_P")) {
						amount = movementMap.get(keyCode + "_UGST_P");
					}
					movementMap.put(keyCode + "_UGST_P", amount.add(movement.getPaidUGST()));

				}

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
				amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
				amountCodes.setPaymentType(receiptDetail.getPaymentType());
				amountCodes.setUserBranch(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
				aeEvent.getAcSetIDList().clear();
				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
							AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
				} else {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(),
							AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
				}

				addZeroifNotContains(movementMap, "bounceChargePaid");
				addZeroifNotContains(movementMap, "bounceCharge_CGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_IGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_SGST_P");
				addZeroifNotContains(movementMap, "bounceCharge_UGST_P");

				// Assignment Percentage
				excludeFees = null;
				if (finMain.getAssignmentId() > 0) {
					Assignment assignment = getReceiptService().getAssignment(finMain.getAssignmentId(), "");
					if (assignment != null) {
						amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
						List<AssignmentDealExcludedFee> excludeFeesList = getReceiptService()
								.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
						if (CollectionUtils.isNotEmpty(excludeFeesList)) {
							excludeFees = new HashSet<String>();
							for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
								excludeFees.add(excludeFee.getFeeTypeCode());
							}
						}
					}
				}

				dataMap = amountCodes.getDeclaredFieldValues();

				if (excludeFees != null) {
					dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
				}

				dataMap.putAll(movementMap);

				// if Repay headers not exists on the Receipt, then add Excess
				// Detail map
				if (receiptDetail.getRepayHeader() == null) {
					dataMap.putAll(extDataMap);
				}
				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

			}
		}

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			if (StringUtils.isNotEmpty(finMain.getFinCommitmentRef())) {
				Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				int format = CurrencyUtil.getFormat(commitment.getCmtCcy());

				if (commitment != null && commitment.isRevolving()) {
					aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_CMTRPY);
					amountCodes.setCmtAmt(BigDecimal.ZERO);
					amountCodes.setChgAmt(BigDecimal.ZERO);
					amountCodes.setDisburse(BigDecimal.ZERO);
					amountCodes.setRpPri(
							CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(), totRpyPri));

					// Assignment Percentage
					Set<String> excludeFees = null;
					if (finMain.getAssignmentId() > 0) {
						Assignment assignment = getReceiptService().getAssignment(finMain.getAssignmentId(), "");
						if (assignment != null) {
							amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
							List<AssignmentDealExcludedFee> excludeFeesList = getReceiptService()
									.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
							if (CollectionUtils.isNotEmpty(excludeFeesList)) {
								excludeFees = new HashSet<String>();
								for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
									excludeFees.add(excludeFee.getFeeTypeCode());
								}
							}
						}
					}

					HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

					if (excludeFees != null) {
						dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
					}

					aeEvent.setDataMap(dataMap);
					aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);

					// FIXME: PV: 04MAY17 why separate method is required for
					// commitment dialog show
					getAccountingDetailDialogCtrl().doFillCmtAccounting(aeEvent.getReturnDataSet(), format);
					getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList()
							.addAll(aeEvent.getReturnDataSet());
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding Fee details to Amount codes on Accounting execution
	 * 
	 * @param amountCodes
	 * @param dataMap
	 */
	private void prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap, String payType) {
		logger.debug("Entering");
		List<FinFeeDetail> finFeeDetailList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (finFeeDetailList != null) {
			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if (!finFeeDetail.isRcdVisible()) {
					continue;
				}
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_N", finFeeDetail.getNetAmount());

				if (StringUtils.equals(payType, RepayConstants.RECEIPTMODE_EXCESS)) {
					payType = "EX_";
				} else if (StringUtils.equals(payType, RepayConstants.RECEIPTMODE_EMIINADV)) {
					payType = "EA_";
				} else if (StringUtils.equals(payType, RepayConstants.RECEIPTMODE_PAYABLE)) {
					payType = "PA_";
				} else {
					payType = "PB_";
				}
				dataMap.put(payType + finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());

				// Calculated Amount
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_C", finFeeDetail.getFinTaxDetails().getActualCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_C", finFeeDetail.getFinTaxDetails().getActualSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_C", finFeeDetail.getFinTaxDetails().getActualIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_C", finFeeDetail.getFinTaxDetails().getActualUGST());

				// Paid Amount
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_P", finFeeDetail.getFinTaxDetails().getPaidCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_P", finFeeDetail.getFinTaxDetails().getPaidSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_P", finFeeDetail.getFinTaxDetails().getPaidIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_P", finFeeDetail.getFinTaxDetails().getPaidUGST());

				// Net Amount
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_N", finFeeDetail.getFinTaxDetails().getNetCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_N", finFeeDetail.getFinTaxDetails().getNetSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_N", finFeeDetail.getFinTaxDetails().getNetIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_N", finFeeDetail.getFinTaxDetails().getNetUGST());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab() {
		logger.debug("Entering");
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
		String finType = finMain.getFinType();
		;
		int moduleID = FinanceConstants.MODULEID_FINTYPE;
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			finType = finMain.getPromotionCode();
			moduleID = FinanceConstants.MODULEID_PROMOTION;
		}

		if (receiptPurposeCtg == 2) {
			accountSetId = AccountingConfigCache.getAccountSetID(finType, AccountEventConstants.ACCEVENT_EARLYSTL,
					moduleID);
		} else if (receiptPurposeCtg == 1) {
			accountSetId = AccountingConfigCache.getAccountSetID(finType, AccountEventConstants.ACCEVENT_EARLYPAY,
					moduleID);
		} else {
			accountSetId = AccountingConfigCache.getAccountSetID(finType, AccountEventConstants.ACCEVENT_REPAY,
					moduleID);
		}

		// Accounting Detail Tab
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", receiptData.getFinanceDetail());
		map.put("finHeaderList", getFinBasicDetails());
		map.put("acSetID", accountSetId);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);

		Tab tab = null;
		if (tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
			tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
			tab.setVisible(true);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

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
	 * @throws Exception
	 */
	private boolean doProcess(FinReceiptData aReceiptData, String tranType) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		// FinanceMain afinanceMain =
		// aReceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = aReceiptData.getReceiptHeader();

		rch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setUserDetails(getUserWorkspace().getLoggedInUser());
		rch.setWorkflowId(getWorkFlowId());

		rch.setUserDetails(getUserWorkspace().getLoggedInUser());
		aReceiptData.setReceiptHeader(rch);

		if (aReceiptData.getFinanceDetail().getExtendedFieldRender() != null) {
			int seqNo = 0;
			ExtendedFieldRender details = aReceiptData.getFinanceDetail().getExtendedFieldRender();
			details.setReference(rch.getReference());
			details.setSeqNo(++seqNo);
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(rch.getRecordStatus());
			details.setRecordType(rch.getRecordType());
			details.setVersion(rch.getVersion());
			details.setWorkflowId(rch.getWorkflowId());
			details.setTaskId(taskId);
			details.setNextTaskId(nextTaskId);
			details.setRoleCode(getRole());
			details.setNextRoleCode(nextRoleCode);
			details.setNewRecord(rch.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(rch.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(rch.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

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

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckDepositProc)) {
					FinReceiptData tReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

					// Check whether deposit process already completed or not,
					// if Re-submission allowed on Realization stage
					// Otherwise till approval of Deposit process , receipt
					// should be wait for realization
					FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
					if (ImplementationConstants.DEPOSIT_PROC_REQ) {
						if (StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, receiptHeader.getReceiptMode())
								|| StringUtils.equals(RepayConstants.RECEIPTMODE_DD, receiptHeader.getReceiptMode())) {
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
				FinReceiptData tRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
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
	 * @throws Exception
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinReceiptData aRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader afinanceMain = aRepayData.getReceiptHeader();

		aRepayData.setForeClosure(isForeClosure);

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {

					auditHeader = getReceiptService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

						if (afinanceMain.isNew()) {
							((FinReceiptData) auditHeader.getAuditDetail().getModelData()).getFinanceDetail()
									.setDirectFinalApprove(true);
						}

						auditHeader = getReceiptService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getReceiptService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReversal)) {
						auditHeader = getReceiptService().doReversal(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
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
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug("Entering");
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
		BigDecimal totSchdSuplRentPaid = BigDecimal.ZERO;
		BigDecimal totSchdIncrCostPaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtility.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdBal(), formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()), formatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getTdsSchdPayNow(), formatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()), formatter));
				totalLatePft = totalLatePft
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()), formatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil
						.amountFormate(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()), formatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getMaxWaiver(), formatter));
				} else {
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRefundMax(), formatter));
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

				lc = new Listcell(PennantAppUtil.amountFormate(refundPft, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Fee Details
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdInsPayNow(), formatter));
				lc.setStyle("text-align:right;");
				totInsPaid = totInsPaid.add(repaySchd.getSchdInsPayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdFeePayNow(), formatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdSuplRentPayNow(), formatter));
				lc.setStyle("text-align:right;");
				totSchdSuplRentPaid = totSchdSuplRentPaid.add(repaySchd.getSchdSuplRentPayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdIncrCostPayNow(), formatter));
				lc.setStyle("text-align:right;");
				totSchdIncrCostPaid = totSchdIncrCostPaid.add(repaySchd.getSchdIncrCostPayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPftSchdWaivedNow())
						.add(repaySchd.getPrincipalSchdPayNow().add(repaySchd.getPriSchdWaivedNow()))
						.add(repaySchd.getSchdInsPayNow()).add(repaySchd.getSchdFeePayNow())
						.add(repaySchd.getSchdSuplRentPayNow()).add(repaySchd.getSchdIncrCostPayNow())
						.add(repaySchd.getLatePftSchdPayNow().add(repaySchd.getLatePftSchdWaivedNow()))
						.add(repaySchd.getPenaltyPayNow().add(repaySchd.getWaivedAmt()).subtract(refundPft));
				lc = new Listcell(PennantAppUtil.amountFormate(netPay, formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdInsBal()).add(repaySchd.getSchdFeeBal())
						.add(repaySchd.getSchdSuplRentBal()).add(repaySchd.getSchdIncrCostBal());

				lc = new Listcell(PennantAppUtil.amountFormate(
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
			paymentMap.put("schdSuplRentPaid", totSchdSuplRentPaid);
			paymentMap.put("schdIncrCostPaid", totSchdIncrCostPaid);

			doFillSummaryDetails(paymentMap);
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

		/*
		 * if (paymentMap.get("insPaid").compareTo(BigDecimal.ZERO) > 0) { totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("insPaid")); this.listheader_InsPayment.setVisible(true);
		 * fillListItem(Labels.getLabel("listcell_insFeePayNow.label"), paymentMap.get("insPaid")); } else {
		 * this.listheader_InsPayment.setVisible(false); } if (paymentMap.get("schdFeePaid").compareTo(BigDecimal.ZERO)
		 * > 0) { totalSchAmount = totalSchAmount.add(paymentMap.get("schdFeePaid"));
		 * this.listheader_SchdFee.setVisible(true); fillListItem(Labels.getLabel("listcell_schdFeePayNow.label"),
		 * paymentMap.get("schdFeePaid")); } else { this.listheader_SchdFee.setVisible(false); } if
		 * (paymentMap.get("schdSuplRentPaid").compareTo(BigDecimal.ZERO) > 0) { totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("schdSuplRentPaid")); this.listheader_SuplRent.setVisible(true);
		 * fillListItem(Labels.getLabel("listcell_schdSuplRentPayNow.label"), paymentMap.get("schdSuplRentPaid")); }
		 * else { this.listheader_SuplRent.setVisible(false); } if
		 * (paymentMap.get("schdIncrCostPaid").compareTo(BigDecimal.ZERO) > 0) { totalSchAmount =
		 * totalSchAmount.add(paymentMap.get("schdIncrCostPaid")); this.listheader_IncrCost.setVisible(true);
		 * fillListItem(Labels.getLabel("listcell_schdIncrCostPayNow.label"), paymentMap.get("schdIncrCostPaid")); }
		 * else { this.listheader_IncrCost.setVisible(false); }
		 */

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
				PennantAppUtil.amountFormate(fieldValue, receiptData.getRepayMain().getLovDescFinFormatter()));
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
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
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
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		// Validate Field Details
		if (isCalProcess) {
			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
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
		if (receiptData.getPaidNow().compareTo(rch.getReceiptAmount()) > 0) {
			MessageUtil.showError(Labels.getLabel("label_Allocation_More_than_receipt"));
			return false;
		}
		// in case of early settlement,do not allow before first installment
		// date(based on AlwEarlySettleBefrFirstInstn in finType )
		if (receiptPurposeCtg == 2 && !financeType.isAlwCloBefDUe()) {
			Date firstInstDate = getFirstInstDate(
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
			if (firstInstDate != null && receiptValueDate.compareTo(firstInstDate) < 0) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_First_Inst_Date",
						new String[] { firstInstDate.toString() }));
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
		if (totReceiptAmount.compareTo(BigDecimal.ZERO) == 0) {
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
			if (balPending.compareTo(BigDecimal.ZERO) != 0) {
				MessageUtil
						.showError(Labels.getLabel("label_ReceiptDialog_Valid_Settlement", new String[] { PennantAppUtil
								.getlabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}
		if (!isCalProcess) {
			return true;
		}

		// Finance Should not allow for Partial Settlement & Early settlement
		// when Maturity Date reaches Current application Date
		if (receiptPurposeCtg == 1 || receiptPurposeCtg == 2) {

			if (financeMain.getMaturityDate().compareTo(receiptValueDate) < 0) {
				MessageUtil.showError(
						Labels.getLabel("label_ReceiptDialog_Valid_MaturityDate", new String[] { PennantAppUtil
								.getlabelDesc(rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()) }));
				return false;
			}
		}

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
				BigDecimal closingBal = null;
				boolean isValidPPDate = true;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) == 0
							&& StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
							&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
							&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
						isValidPPDate = false;
					}
					if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) >= 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}
					if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) == 0 || closingBal == null) {
						closingBal = closingBal.subtract(curSchd.getSchdPriPaid().subtract(curSchd.getSchdPftPaid()));
						break;
					}
				}

				if (!isValidPPDate) {
					MessageUtil.showError(Labels.getLabel("RECEIPT_INVALID_VALUEDATE"));
					return false;
				}

				if (closingBal != null) {
					if (receiptData.getRemBal().compareTo(closingBal) >= 0) {
						MessageUtil.showError(Labels.getLabel("FIELD_IS_LESSER",
								new String[] {
										Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
										PennantApplicationUtil.amountFormate(closingBal, formatter) }));
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

			// If Schedule Already Paid, not allowed to do Early settlement on
			// same received date
			// when Date is with in Grace and No Profit Payment case
			if (financeMain.isAllowGrcPeriod()) {
				boolean isAlwEarlyStl = true;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) == 0) {
						if (DateUtility.compare(curSchd.getSchDate(), receiptData.getFinanceDetail()
								.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0) {
							BigDecimal pftBal = scheduleList.get(i - 1).getProfitBalance().add(curSchd.getProfitCalc())
									.subtract(curSchd.getSchdPftPaid())
									.subtract(scheduleList.get(i - 1).getCpzAmount());

							if (pftBal.compareTo(BigDecimal.ZERO) > 0 && curSchd.isSchPftPaid()) {
								isAlwEarlyStl = false;
								break;
							}
						}
					} else if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) < 0) {
						break;
					}
				}

				if (!isAlwEarlyStl) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_RePaid_EarlySettlement"));
					return false;
				}
			}
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * 
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	private void doLoadTabsData() throws InterruptedException {
		logger.debug("Entering ");
		// FIXME: PV: CODE REVIEW PENDING
		boolean createTab = false;
		if (tabsIndexCenter.getFellowIfAny("dashboardTab") == null) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Dashboard");
			tab.setId("dashboardTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectDashboardTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("graphTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		Div graphDivTabDiv = new Div();
		graphDivTabDiv.setHeight("100%");
		graphDivTabDiv.setStyle("overflow:auto;");
		tabpanel.appendChild(graphDivTabDiv);
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		logger.debug("Leaving ");
	}

	/**
	 * Method to show report chart
	 */
	public void doShowReportChart(FinScheduleData finScheduleData) {
		logger.debug("Entering ");
		// FIXME: PV: CODE REVIEW PENDING
		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();

		// For Finance Vs Amounts Chart z
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount(finScheduleData, formatter);

		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts",
				"Loan Amount =" + PennantAppUtil.amountFormate(
						PennantAppUtil.unFormateAmount(finScheduleData.getFinanceMain().getFinAmount(), formatter),
						formatter),
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
		logger.debug("Leaving ");
	}

	/**
	 * Method to get report data from repayments table.
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForRepayments(FinScheduleData scheduleData, int formatter) {
		logger.debug("Entering ");
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
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Payment Amount", PennantAppUtil.formateAmount(curSchd.getRepayAmount(), format)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Principal", PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(), format)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Interest", PennantAppUtil.formateAmount(curSchd.getProfitSchd(), format)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	/**
	 * This method returns data for Finance vs amount chart
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForFinVsAmount(FinScheduleData scheduleData, int formatter) {
		logger.debug("Entering ");
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
				financeAmount = financeAmount.add(PennantAppUtil.formateAmount(curSchd.getDisbAmount(), format));
				downPayment = downPayment.add(PennantAppUtil.formateAmount(curSchd.getDownPaymentAmount(), format));
				capitalized = capitalized.add(PennantAppUtil.formateAmount(curSchd.getCpzAmount(), format));

				scheduleProfit = scheduleProfit.add(PennantAppUtil.formateAmount(curSchd.getProfitSchd(), format));
				schedulePrincipal = schedulePrincipal
						.add(PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(), format));

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
		logger.debug("Leaving ");
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
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
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
		List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
		List<FinanceProfitDetail> finpftDetails = new ArrayList<FinanceProfitDetail>();
		financeMains.addAll(getFinanceDetailService().getFinanceMainForLinkedLoans(finReference.getValue()));

		if (CollectionUtils.isNotEmpty(financeMains)) {
			List<String> finRefList = new ArrayList<>();
			for (FinanceMain finMain : financeMains) {
				if (StringUtils.equals(receiptData.getFinReference(), finMain.getFinReference())) {
					continue;
				}
				finRefList.add(finMain.getFinReference());
			}
			finpftDetails.addAll(getFinanceDetailService().getFinProfitListByFinRefList(finRefList));
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMains", financeMains);
		map.put("finpftDetails", finpftDetails);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/LinkedLoansDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * onChanging fundingAccount details
	 * 
	 * @param event
	 */
	public void onFulfill$fundingAccount(Event event) {
		logger.debug("Entering");
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
		this.fundingAccount.setAttribute("fundingAccID", partnerBankID);

		logger.debug("Leaving");
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
		logger.debug("Entering");
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
		logger.debug("Leaving");
	}

	/** new code to display chart by skipping jsps code end */

	// Printer integration starts

	public void onClick$btnPrint(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// FIXME: PV: CODE REVIEW PENDING
		try {

			String reportName = "Receipt";
			String templatePath = PathUtil.getPath(PathUtil.REPORTS_FINANCE) + "/";
			String templateName = reportName + PennantConstants.DOC_TYPE_WORD_EXT;
			AgreementEngine engine = new AgreementEngine(templatePath, templatePath);
			engine.setTemplate(templateName);
			engine.loadTemplate();
			reportName = "Receipt_" + this.finReference.getValue() + "_"
					+ receiptData.getReceiptHeader().getReceiptID();

			ReceiptReport receipt = new ReceiptReport();
			receipt.setUserName(getUserWorkspace().getLoggedInUser().getUserName() + " - "
					+ getUserWorkspace().getLoggedInUser().getFullName());
			receipt.setFinReference(this.finReference.getValue());
			receipt.setCustName(receiptData.getReceiptHeader().getCustShrtName());

			BigDecimal totalReceiptAmt = receiptData.getTotReceiptAmount();
			int finFormatter = CurrencyUtil
					.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
			receipt.setReceiptAmount(PennantApplicationUtil.amountFormate(totalReceiptAmt, finFormatter));
			receipt.setReceiptAmountInWords(NumberToEnglishWords
					.getAmountInText(PennantApplicationUtil.formateAmount(totalReceiptAmt, finFormatter), ""));
			receipt.setAppDate(DateUtility.formatToLongDate(DateUtility.getAppDate()));

			/*
			 * Date eventFromDate = this.receivedDate.getValue(); if (eventFromDate == null) { eventFromDate =
			 * DateUtility.getAppDate(); }
			 */
			// receipt.setReceiptDate(DateUtility.formatToLongDate(eventFromDate));
			receipt.setReceiptNo(this.paymentRef.getValue());
			receipt.setPaymentMode(this.receiptMode.getSelectedItem().getLabel().toString());
			engine.mergeFields(receipt);

			boolean isDirectPrint = false;
			try {
				if (isDirectPrint) {
					try {
						byte[] documentByteArray = engine.getDocumentInByteArray(reportName, SaveFormat.PDF);
						String encodedString = Base64.encodeBase64String(documentByteArray);
						Clients.evalJavaScript(
								"PrinterUtil.print('window_ReceiptDialog','onPrintSuccess','" + encodedString + "')");

					} catch (Exception e) {
						logger.error(Labels.getLabel("message.error.printerNotImpl"));
						engine.showDocument(this.window_ReceiptDialog, reportName, SaveFormat.PDF);
					}
				} else {
					engine.showDocument(this.window_ReceiptDialog, reportName, SaveFormat.PDF);
				}
			} catch (Exception e) {
				logger.error(Labels.getLabel("message.error.agreementNotFound"));
			}

		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.agreementNotFound"));
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
			eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (receiptPurposeCtg == 2) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
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

		// Total Net Receivable
		BigDecimal paidByCustomer = pd.getTotalDue().add(adv.getTotalDue()).add(fee.getTotalDue());
		paidByCustomer = paidByCustomer.subtract(pd.getWaivedAmount()).subtract(adv.getWaivedAmount())
				.subtract(fee.getWaivedAmount());
		this.paidByCustomer.setValue(PennantAppUtil.formateAmount(paidByCustomer, formatter));

		// To be Paid by Customer = Net Receivable - Excess paid
		BigDecimal custToBePaid = paidByCustomer.subtract(xa.getTotalPaid());
		this.custPaid.setValue(PennantAppUtil.formateAmount(custToBePaid, formatter));

		// Remaining Balance = Receipt Amount + To be Paid by Customer - Paid by
		// Customer (Allocated)
		BigDecimal remBalAfterAllocation = receiptData.getTotReceiptAmount().subtract(pd.getTotalPaid())
				.subtract(adv.getTotalPaid()).subtract(fee.getTotalPaid());
		if (remBalAfterAllocation.compareTo(BigDecimal.ZERO) <= 0) {
			remBalAfterAllocation = BigDecimal.ZERO;
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(false);
		}
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBalAfterAllocation, formatter));

	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		notes.setRoleCode(getRole());
		logger.debug("Leaving ");
		return notes;
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

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
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

	private Date getFirstInstDate(List<FinanceScheduleDetail> financeScheduleDetail) {

		// Finding First Installment Date
		Date firstInstDate = null;
		for (FinanceScheduleDetail scheduleDetail : financeScheduleDetail) {

			BigDecimal repayAmt = scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd())
					.subtract(scheduleDetail.getPartialPaidAmt());

			// InstNumber issue with Partial Settlement before first installment
			if (repayAmt.compareTo(BigDecimal.ZERO) > 0) {
				firstInstDate = scheduleDetail.getSchDate();
				break;
			}
		}
		return firstInstDate;
	}

	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

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

			final HashMap<String, Object> map = getDefaultArguments();

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
		logger.debug("Leaving");
	}

	public void appendEffectScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

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

			final HashMap<String, Object> map = getDefaultArguments();

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
		logger.debug("Leaving");
	}

	protected void doStoreServiceIds(FinReceiptHeader finReceiptHeader) {
		this.curRoleCode = finReceiptHeader.getRoleCode();
		this.curNextRoleCode = finReceiptHeader.getNextRoleCode();
		this.curTaskId = finReceiptHeader.getTaskId();
		this.curNextTaskId = finReceiptHeader.getNextTaskId();
		// this.curNextUserId = finReceiptHeader.getNextUserId();
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));

		return map;
	}

	private void doFillExcessPayables() {
		logger.debug("Entering");
		if (!isForeClosure && !isEarlySettle) {
			return;
		}
		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();
		this.listBoxExcess.getItems().clear();

		for (int i = 0; i < xcessPayableList.size(); i++) {
			createXcessPayableItem(xcessPayableList.get(i), i);
		}
		addXcessFooter(formatter);
		logger.debug("Leaving");
	}

	private void createXcessPayableItem(XcessPayables xcessPayable, int idx) {
		// List Item
		Listitem item = new Listitem();
		Listcell lc = null;
		// FIXME: PV: CODE REVIEW PENDING
		addBoldTextCell(item, xcessPayable.getPayableDesc(), false, idx);
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

		addAmountCell(item, xcess.getDueAmount(), null, true);
		addAmountCell(item, BigDecimal.ZERO, null, true);
		addAmountCell(item, xcess.getTotalPaid(), null, true);
		addAmountCell(item, xcess.getBalance(), null, true);

		this.listBoxExcess.appendChild(item);
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

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
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

}