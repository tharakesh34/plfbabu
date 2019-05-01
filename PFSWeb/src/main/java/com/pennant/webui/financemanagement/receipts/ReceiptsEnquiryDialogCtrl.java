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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
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

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.ChartType;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.reports.ReceiptReport;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.core.EventManager;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.AgreementEngine;
import com.pennant.util.PennantAppUtil;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul
 */
public class ReceiptsEnquiryDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(ReceiptsEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReceiptsEnquiryDialog;
	protected Borderlayout borderlayout_Receipt;
	protected Label windowTitle;

	// Loan Summary Details
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox finType;

	/*
	 * protected Decimalbox priBal; protected Decimalbox pftBal; protected Decimalbox priDue; protected Decimalbox
	 * pftDue; protected Decimalbox bounceDueAmount; protected Decimalbox otnerChargeDue; protected Decimalbox
	 * recepitInProcess; protected Decimalbox recepitInprocessManual;
	 */
	protected Textbox finCcy;
	protected Decimalbox paidByCustomer;

	protected Button btnSearchCustCIF;
	protected Button btnSearchFinreference;
	//protected Button btnSearchReceiptInProcess;

	// Receipt Details
	protected Groupbox gb_ReceiptDetails;
	protected Groupbox gb_Receivalble;
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
	protected Datebox cancelDate;
	protected Row row_CancelReason;
	protected ExtendedCombobox cancelReason;
	protected ExtendedCombobox cancelRemarks;
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

	//Payable Details
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

	private FinReceiptHeader befImage;
	private List<ChartDetail> chartDetailList = new ArrayList<ChartDetail>();

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
	private boolean isKnockOff = false;

	/**
	 * default constructor.<br>
	 */
	public ReceiptsEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReceiptDialog";
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReceiptsEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReceiptsEnquiryDialog);
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

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(finReceiptHeader);
				receiptData.getReceiptHeader().setBefImage(befImage);
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}
			if (arguments.containsKey("enquiryModule")) {
				enqiryModule = (Boolean) arguments.get("enquiryModule");
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

			if (arguments.containsKey("receiptListCtrl")) {
				setReceiptListCtrl((ReceiptListCtrl) arguments.get("receiptListCtrl"));
			}

			doLoadWorkFlow(finReceiptHeader.isWorkflow(), finReceiptHeader.getWorkflowId(),
					finReceiptHeader.getNextTaskId());
			receiptPurposeCtg = getReceiptCalculator().setReceiptCategory(finReceiptHeader.getReceiptPurpose());

			if (enqiryModule) {
				setWorkFlowEnabled(false);
				this.south.setHeight("0px");
			}

			doSetFieldProperties();
			this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
			this.listBoxSchedule.setHeight(getListBoxHeight(6));
			this.receiptDetailsTab.setSelected(true);
			//this.btnCalcReceipts.setDisabled(false);
			this.windowTitle.setValue(Labels.getLabel(module + "_Window.Title"));

			doShowDialog(finReceiptHeader);

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReceiptsEnquiryDialog.onClose();
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
				this.window_ReceiptsEnquiryDialog, map);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.paidByCustomer.setFormat(amountFormat);
		this.receiptAmount.setProperties(true, formatter);
		this.realizationDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(
				new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });

		this.bounceCode.setModuleName("BounceReason");
		this.bounceCode.setValueColumn("BounceID");
		this.bounceCode.setValueType(DataType.LONG);
		this.bounceCode.setDescColumn("Reason");
		this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceID", "BounceCode", "Lovdesccategory", "Reason" });

		this.bounceCharge.setProperties(false, formatter);
		this.bounceRemarks.setMaxlength(100);
		this.bounceDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.cancelDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setValueColumn("PartnerBankCode");
		this.fundingAccount.setDescColumn("PartnerBankName");
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankCode" });

		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);
		this.chequeAcNo.setReadonly(true);

		this.remarks.setMaxlength(500);
		this.favourName.setMaxlength(50);
		this.favourName.setDisabled(true);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(6);
		this.depositDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.depositNo.setMaxlength(50);
		this.paymentRef.setMaxlength(50);
		this.transactionRef.setMaxlength(50);

		this.bankCode.setReadonly(true);
		this.fundingAccount.setReadonly(true);
		this.bounceCode.setReadonly(true);
		this.cancelReason.setReadonly(true);
		this.cancelRemarks.setReadonly(true);
		this.cancelRemarks.setMandatoryStyle(false);
		this.collectionAgentId.setReadonly(true);
		this.fundingAccount.setReadonly(true);

		this.bankCode.setModuleName("BankDetail");
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

		appendScheduleMethod(receiptData.getReceiptHeader());

		this.hbox_ReceiptModeStatus.setVisible(true);
		this.row_ReceiptModeStatus.setVisible(true);
		this.receiptModeStatus.setVisible(true);

		if (StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(), RepayConstants.RECEIPTMODE_DD)) {
			this.row_DepositDate.setVisible(true);
			this.row_DepositBank.setVisible(true);
			this.hbox_ReceiptDialog_DepositDate.setVisible(true);

		}
		resetModeStatus(receiptData.getReceiptHeader().getReceiptModeStatus());
		setReceiptModeStatus(receiptData.getReceiptHeader());
		if (isKnockOff) {
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

		try {
			// fill the components with the data
			doWriteBeanToComponents();
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReceiptsEnquiryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
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
		doClose(true);
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
		}

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(false);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else {

			this.gb_ReceiptDetails.setVisible(true);
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_receiptAmount"), this.receiptAmount);

			this.row_remarks.setVisible(true);

			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {

				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.row_PaymentRef.setVisible(false);

				if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)) {
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo
							.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));

					if (isUserAction) {
						this.depositDate.setValue(DateUtility.getAppDate());
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
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				this.row_DepositBank.setVisible(false);
				readOnlyComponent(true, this.fundingAccount);

			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
			}
		}

		if (StringUtils.equals(module, FinanceConstants.DEPOSIT_MAKER)
				&& ((StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
						|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)))) {
			this.fundingAccount.setReadonly(false);

		} else if (StringUtils.equals(module, FinanceConstants.RECEIPT_MAKER)
				&& ((!StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
						&& !StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)
						&& !StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)))) {
		}

		// Due to changes in Receipt Amount, call Auto Allocations

		doFillAllocationDetail();
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
			this.cancelDate.setVisible(true);
			this.cancelDate.setValue(rch.getBounceDate());
			if (rch.getBounceDate() == null) {
				this.bounceDate.setValue(DateUtility.getAppDate());
			}
		} else if (StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			this.realizationDate.setValue(rch.getRealizationDate());
		}

		fillComboBox(this.receiptModeStatus, rch.getReceiptModeStatus(), PennantStaticListUtil.getEnquiryReceiptModeStatus(),
				"");
	}

	private void resetModeStatus(String status) {
		logger.debug("Entering");

		this.row_CancelReason.setVisible(false);
		this.row_BounceReason.setVisible(false);
		this.row_BounceRemarks.setVisible(false);
		this.row_RealizationDate.setVisible(false);

		if (StringUtils.equals(status, RepayConstants.PAYSTATUS_BOUNCE)) {

			this.row_BounceReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);

		} else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_CANCEL)) {

			this.row_CancelReason.setVisible(true);

		} else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_REALIZED)) {

			this.row_RealizationDate.setVisible(true);

		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterruptedException
	 */
	@SuppressWarnings("deprecation")
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
			map.put("window", this.window_ReceiptsEnquiryDialog);

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
	 * Method for Writing Data into Fields from Bean
	 * 
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		String custCIFname = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			custCIFname = customer.getCustCIF();
			if (StringUtils.isNotBlank(customer.getCustShrtName())) {
				custCIFname = custCIFname + "-" + customer.getCustShrtName();
			}
		}

		this.custCIF.setValue(custCIFname);
		this.finReference.setValue(finMain.getFinReference());
		this.finBranch.setValue(finMain.getFinBranch());
		this.finType.setValue(finMain.getFinType());

		this.finType.setValue(finMain.getFinType()); //+ " - " + finMain.getLovDescFinTypeName()
		this.finBranch.setValue(finMain.getFinBranch() + " - " + finMain.getLovDescFinBranchName());

		this.receiptId.setValue(String.valueOf(rch.getReceiptID()));
		fillComboBox(receiptPurpose, rch.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), "");
		this.receiptMode.setValue(rch.getReceiptMode());
		this.receiptDate.setValue(rch.getReceiptDate());
		this.receiptAmount.setValue(rch.getReceiptAmount());
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(rch.getReceiptAmount(), formatter));

		this.receivedFrom.setValue(rch.getReceivedFrom());
		this.panNumber.setValue(rch.getPanNumber());
		fillComboBox(allocationMethod, rch.getAllocationType(), PennantStaticListUtil.getAllocationMethods(), "");
		fillComboBox(excessAdjustTo, rch.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
		this.collectionAgentId.setValue(rch.getCollectionAgentCode(), rch.getCollectionAgentDesc());

		this.remarks.setValue(rch.getRemarks());
		this.fundingAccount.setValue(rch.getPartnerBankCode());
		this.receiptChannel.setValue(rch.getReceiptChannel());
		this.subReceiptMode.setValue(rch.getSubReceiptMode());
		this.realizationDate.setValue(rch.getRealizationDate());

		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail rcd = rch.getReceiptDetails().get(i);

				if (!StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
						&& !StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
						&& !StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {

					this.favourNo.setValue(rcd.getFavourNumber());
					this.valueDate.setValue(rcd.getValueDate());
					this.bankCode.setValue(rcd.getBankCode());
					this.bankCode.setDescription(rcd.getBankCodeDesc());
					this.favourName.setValue(rcd.getFavourName());
					this.depositNo.setValue(rcd.getDepositNo());
					this.depositDate.setValue(rcd.getDepositDate());
					this.transactionRef.setValue(rcd.getTransactionRef());
					this.chequeAcNo.setValue(rcd.getChequeAcNo());
					this.paymentRef.setValue(rcd.getPaymentRef());
					this.externalRefrenceNumber.setValue(rch.getExtReference());
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

		setBalances();
		checkByReceiptMode(rch.getReceiptMode(), false);
		appendReceiptMode(rch);
		doFillExcessPayables();
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
			this.excessAdjustTo.setDisabled(true);
			fillComboBox(excessAdjustTo, rch.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
			if (receiptPurposeCtg == 2) {
				fillComboBox(excessAdjustTo, "E", PennantStaticListUtil.getExcessAdjustmentTypes(), ",A,");
				this.excessAdjustTo.setDisabled(true);
			}

		} else {
			this.effScheduleMethod.setVisible(true);
			this.effScheduleMethod.setDisabled(true);
			this.excessAdjustTo.setVisible(false);
			this.excessAdjustTo.setDisabled(true);
			scheduleLabel.setValue(Labels.getLabel("label_ReceiptDialog_EffecScheduleMethod.value"));
			fillComboBox(effScheduleMethod, rch.getEffectSchdMethod(), PennantStaticListUtil.getEarlyPayEffectOn(), "");
		}

	}

	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * 
	 * @param rch
	 * 
	 * @param header
	 * @param allocatePaidMap
	 */

	private void doFillAllocationDetail() {
		logger.debug("Entering");
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		this.listBoxPastdues.getItems().clear();
		if (allocationList.isEmpty()) {
			this.gb_Receivalble.setVisible(false);
			return;
		}

		// Get Receipt Purpose to Make Waiver amount Editable
		doRemoveValidation();
		doClearMessage();
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal dueAmount = BigDecimal.ZERO;
		for (int i = 0; i < allocationList.size(); i++) {
			ReceiptAllocationDetail rad = allocationList.get(i);
			if (StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_PP)) {
				continue;
			}
			if (StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_PFT)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_TDS)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_NPFT)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_PRI)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_FUT_TDS)
					|| StringUtils.equals(rad.getAllocationType(), RepayConstants.ALLOCATION_FUT_PFT)) {
				paidAmount = BigDecimal.ZERO;
			} else {
				paidAmount = rad.getPaidAmount();
				dueAmount = dueAmount.add(rad.getTotalDue());
			}
			sum = sum.add(paidAmount);
			Listitem item = new Listitem();
			String allocDesc = Labels.getLabel("label_RecceiptDialog_AllocationType_" + rad.getAllocationType());
			addBoldTextCell(item, allocDesc, rad.isSubListAvailable(), i);

			addAmountCell(item, rad.getTotalDue(), ("AllocateDue_" + i), false);
			addAmountCell(item, rad.getPaidAmount(), ("AllocatePaid_" + i), false);

			this.listBoxPastdues.appendChild(item);
		}
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		addAmountCell(item, dueAmount, null, true);
		addAmountCell(item, sum, null, true);
		this.listBoxPastdues.appendChild(item);

		addExcessAmt(receiptData.getReceiptHeader().getReceiptAmount().subtract(sum));

		logger.debug("Leaving");
	}

	private void addExcessAmt(BigDecimal excess) {
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0) {
			Listitem item = new Listitem();
			Listcell lc = null;
			item = new Listitem();
			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_ExcessType_EXCESS"));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(String.valueOf(PennantApplicationUtil.amountFormate(excess, formatter)));
			lc.setId("ExcessAmount");
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			this.listBoxPastdues.appendChild(item);
		}
	}

	private void doFillExcessPayables() {
		logger.debug("Entering");
		/*
		 * if (!isForeClosure && !isEarlySettle){ return; }
		 */
		//this.gb_Payable.setVisible(false);

		receiptData = getReceiptCalculator().setXcessPayables(receiptData);

		List<XcessPayables> xcessPayableList = receiptData.getReceiptHeader().getXcessPayables();
		this.listBoxExcess.getItems().clear();

		for (int i = 0; i < xcessPayableList.size(); i++) {
			createXcessPayableItem(xcessPayableList.get(i), i);
		}
		/* addXcessFooter(formatter); */
		logger.debug("Leaving");
	}

	private void createXcessPayableItem(XcessPayables xcessPayable, int idx) {
		// List Item
		Listitem item = new Listitem();
		Listcell lc = null;
		// FIXME: PV: CODE REVIEW PENDING
		addBoldTextCell(item, xcessPayable.getPayableDesc(), false, idx);
		addAmountCell(item, xcessPayable.getAvailableAmt(), null, false);
		this.listBoxExcess.appendChild(item);
	}

	public void onDetailsClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		String buttonId = (String) event.getData();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("details",
				receiptData.getReceiptHeader().getAllocationsSummary().get(Integer.parseInt(buttonId)).getSubList());
		map.put("buttonId", buttonId);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/BounceDetailsList.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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

		if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_EMI)) {
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

		if (allocate.isSubListAvailable()) {
			getReceiptCalculator().splitAllocSummary(receiptData, idx);
		}

		//changePaid();
		// if no extra balance or partial pay disable excessAdjustTo
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAllocateWaivedChange(ForwardEvent event) throws Exception {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
		int idx = (int) event.getData();
		String id = "AllocateWaived_" + idx;

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		ReceiptAllocationDetail allocate = rch.getAllocations().get(idx);

		CurrencyBox allocationWaived = (CurrencyBox) this.listBoxPastdues.getFellow(id);
		BigDecimal waivedAmount = PennantApplicationUtil.unFormateAmount(allocationWaived.getValidateValue(),
				formatter);
		BigDecimal dueAmount = rch.getAllocations().get(idx).getTotalDue();
		BigDecimal paidAmount = rch.getAllocations().get(idx).getTotalPaid();
		if (waivedAmount.compareTo(dueAmount.subtract(paidAmount)) > 0) {
			waivedAmount = dueAmount.subtract(paidAmount);
		}
		paidAmount = dueAmount.subtract(waivedAmount);
		allocate.setWaivedAmount(waivedAmount);
		allocate.setTotalPaid(paidAmount);

		if (allocate.getAllocationType().equals(RepayConstants.ALLOCATION_EMI)) {
			BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, waivedAmount);
			for (ReceiptAllocationDetail allocteDtl : rch.getAllocations()) {
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_PFT)) {
					if (emiSplit[1].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid())) > 0) {
						emiSplit[1] = allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid());
					}
					allocteDtl.setWaivedAmount(emiSplit[1]);

				}
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_NPFT)) {
					if (emiSplit[2].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid())) > 0) {
						emiSplit[2] = allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid());
					}
					allocteDtl.setWaivedAmount(emiSplit[2]);
				}
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_PRI)) {
					if (emiSplit[0].compareTo(allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid())) > 0) {
						emiSplit[0] = allocteDtl.getTotalDue().subtract(allocteDtl.getTotalPaid());
					}
					allocteDtl.setWaivedAmount(emiSplit[0]);
				}
				if (allocteDtl.getAllocationType().equals(RepayConstants.ALLOCATION_TDS)) {
					allocteDtl.setWaivedAmount(emiSplit[1].subtract(emiSplit[2]));
				}
			}
		}

		//changeWaiver();
		if (this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) <= 0 || receiptPurposeCtg == 1) {
			this.excessAdjustTo.setSelectedIndex(0);
			this.excessAdjustTo.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */

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
		this.remarks.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	@SuppressWarnings("deprecation")
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug("Entering");
		// FIXME: PV: CODE REVIEW PENDING
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
		BigDecimal totalSchAmount = BigDecimal.ZERO;

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
	@SuppressWarnings("deprecation")
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
	 * Method to show report chart
	 */
	@SuppressWarnings("deprecation")
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
	@SuppressWarnings("deprecation")
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
	@SuppressWarnings("deprecation")
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

			Executions.createComponents("/Charts/Chart.zul",
					(Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"),
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
						engine.showDocument(this.window_ReceiptsEnquiryDialog, reportName, SaveFormat.PDF);
					}
				} else {
					engine.showDocument(this.window_ReceiptsEnquiryDialog, reportName, SaveFormat.PDF);
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
			button.addForward("onClick", window_ReceiptsEnquiryDialog, "onDetailsClick", button.getId());
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

	@SuppressWarnings("deprecation")
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

		BigDecimal partPayAmount = receiptCalculator.getPartPaymentAmount(receiptData);
		BigDecimal remainingBal = partPayAmount.subtract(rch.getTotalFees().getTotalPaid());
		rch.setPartPayAmount(partPayAmount);
		rch.setBalAmount(remainingBal);

		receiptData.setRemBal(remainingBal);
		receiptData.setTotReceiptAmount(rch.getReceiptAmount());
		BigDecimal remBalAfterAllocation = rch.getReceiptAmount().subtract(pd.getTotalPaid())
				.subtract(adv.getTotalPaid()).subtract(fee.getTotalPaid());
		if (remBalAfterAllocation.compareTo(BigDecimal.ZERO) <= 0) {
			remBalAfterAllocation = BigDecimal.ZERO;
			this.excessAdjustTo.setDisabled(true);
		} else {
			this.excessAdjustTo.setDisabled(true);
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
