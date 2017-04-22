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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  ReceiptDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.EarlySettlementReportData;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.core.EventManager.Notify;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.TemplateEngine;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.StageAccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul
 */
public class ReceiptDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long								serialVersionUID					= 966281186831332116L;
	private final static Logger								logger								= Logger.getLogger(ReceiptDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window										window_ReceiptDialog;
	protected Borderlayout									borderlayout_Receipt;
	protected Label											windowTitle;

	//Summary Details

	protected Textbox										lovDescFinCcyName;
	protected Combobox										profitDayBasis;
	protected Textbox										lovDescCustCIF;
	protected Textbox										lovDescFinBranchName;
	protected Decimalbox									totDisbursements;
	protected Decimalbox									totDownPayment;
	protected Decimalbox									totCpzAmt;
	protected Decimalbox									totPriAmt;
	protected Decimalbox									totPftAmt;
	protected Decimalbox									totFeeAmt;
	protected Decimalbox									totChargeAmt;
	protected Decimalbox									totWaiverAmt;
	protected Decimalbox									schPriTillNextDue;
	protected Decimalbox									schPftTillNextDue;
	protected Decimalbox									totPriPaid;
	protected Decimalbox									totPftPaid;
	protected Decimalbox									totPriDue;
	protected Decimalbox									totPftDue;

	//Receipt Details
	protected Textbox										receipt_finType;
	protected Textbox										receipt_finReference;
	protected Textbox										receipt_finCcy;
	protected Textbox										receipt_finBranch;
	protected Textbox										receipt_CustCIF;
	protected Decimalbox									receipt_paidByCustomer;

	protected Combobox										receiptPurpose;
	protected Combobox										excessAdjustTo;
	protected Combobox										receiptMode;
	protected CurrencyBox									receiptAmount;
	protected Combobox										allocationMethod;
	protected Combobox										effScheduleMethod;
	protected Decimalbox									remBalAfterAllocation;
	
	protected Groupbox										gb_ReceiptDetails;
	protected Caption										caption_receiptDetail;
	protected Label											label_ReceiptDialog_favourNo;
	protected Textbox										favourNo;
	protected Datebox										valueDate;
	protected ExtendedCombobox								bankCode;
	protected Textbox										favourName;
	protected Datebox										depositDate;
	protected Textbox										depositNo;
	protected Textbox										paymentRef;
	protected Textbox										transactionRef;
	protected AccountSelectionBox							chequeAcNo;
	protected AccountSelectionBox							fundingAccount;
	protected Datebox										receivedDate;
	protected Textbox										remarks;
	
	protected Row											row_favourNo;	
	protected Row											row_BankCode;	
	protected Row											row_DepositDate;	
	protected Row											row_PaymentRef;	
	protected Row											row_ChequeAcNo;	
	protected Row											row_fundingAcNo;	
	protected Row											row_remarks;	

	protected Listbox										listBoxExcess;

	//Allocation Details
	protected Textbox										allocation_finType;
	protected Textbox										allocation_finReference;
	protected Textbox										allocation_finCcy;
	protected Textbox										allocation_finBranch;
	protected Textbox										allocation_CustCIF;
	protected Decimalbox									allocation_paidByCustomer;

	protected Listbox										listBoxPastdues;
	protected Listbox										listBoxManualAdvises;
	
	// Payment Schedule Details
	protected Textbox										payment_finType;
	protected Textbox										payment_finReference;
	protected Textbox										payment_finCcy;
	protected Textbox										payment_CustCIF;
	protected Textbox										payment_finBranch;
	protected Decimalbox									payment_paidByCustomer;

	// List Header Details on payent Details
	protected Listheader									listheader_InsPayment;
	protected Listheader									listheader_SchdFee;
	protected Listheader									listheader_SuplRent;
	protected Listheader									listheader_IncrCost;

	//Effective Schedule Tab Details
	protected Label											finSchType;
	protected Label											finSchCcy;
	protected Label											finSchMethod;
	protected Label											finSchProfitDaysBasis;
	protected Label											finSchReference;
	protected Label											finSchGracePeriodEndDate;
	protected Label											effectiveRateOfReturn;

	//Invisible Fields
	protected Decimalbox									overDuePrincipal;
	protected Decimalbox									overDueProfit;
	protected Datebox										lastFullyPaidDate;
	protected Datebox										nextPayDueDate;
	protected Decimalbox									accruedPft;
	protected Decimalbox									pendingODC;
	protected Decimalbox									provisionedAmt;
	protected Row											row_provisionedAmt;

	protected Grid											grid_Summary;
	protected Tabbox										tabbox;
	protected Button										btnPrint;

	protected Tab											summaryDetailsTab;
	protected Tab											receiptDetailsTab;
	protected Tab											allocationDetailsTab;
	protected Tab											repaymentDetailsTab;
	protected Tab											effectiveScheduleTab;
	private Div												graphDivTabDiv;
	private BigDecimal										financeAmount;

	//Buttons
	protected Button										btnReceipt;
	protected Button										btnChangeReceipt;
	protected Button										btnCalcReceipts;
	protected Listbox										listBoxPayment;
	protected Listbox										listBoxSchedule;

	private transient OverdueChargeRecoveryService			overdueChargeRecoveryService;
	private transient AccountsService						accountsService;
	private transient AccountInterfaceService				accountInterfaceService;
	private transient RuleService							ruleService;
	private transient CustomerDetailsService				customerDetailsService;
	private transient ReceiptService						receiptService;
	private transient ProvisionService						provisionService;
	private transient FinanceDetailService					financeDetailService;
	private transient RuleExecutionUtil						ruleExecutionUtil;
	private transient AccountEngineExecution				engineExecution;
	private transient AccountEngineExecutionRIA				engineExecutionRIA;
	private transient CommitmentService						commitmentService;
	private transient ReceiptCalculator						receiptCalculator;
	private transient FinanceReferenceDetailService			financeReferenceDetailService;

	private transient AccountingDetailDialogCtrl			accountingDetailDialogCtrl			= null;
	private transient FeeDetailDialogCtrl					feeDetailDialogCtrl					= null;
	private transient DocumentDetailDialogCtrl				documentDetailDialogCtrl			= null;
	private transient AgreementDetailDialogCtrl				agreementDetailDialogCtrl			= null;
	private transient CustomerDialogCtrl					customerDialogCtrl					= null;
	private transient StageAccountingDetailDialogCtrl		stageAccountingDetailDialogCtrl		= null;
	private transient FinanceCheckListReferenceDialogCtrl	financeCheckListReferenceDialogCtrl	= null;

	private FinReceiptData									receiptData							= null;
	private FinReceiptHeader								receiptHeader						= null;
	private List<FinExcessAmount>							excessList							= null;
	private FinanceDetail									financeDetail;
	private FinanceType										financeType;
	private RepayMain										repayMain							= null;

	private LinkedHashMap<String, RepayScheduleDetail>		refundMap;
	private boolean											isLimitExceeded						= false;
	private boolean											refundAmtValidated					= true;
	private MailUtil										mailUtil;

	private Map<String, BigDecimal> waivedAllocationMap = new HashMap<>();
	private Map<String, BigDecimal> paidAllocationMap = new HashMap<>();
	
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

		try {
			if (arguments.containsKey("repayData")) {
				setReceiptData((FinReceiptData) arguments.get("repayData"));
				FinanceMain befImage = new FinanceMain();
				financeDetail = getReceiptData().getFinanceDetail();
				financeType = financeDetail.getFinScheduleData().getFinanceType();
				setFinanceDetail(financeDetail);
				receiptHeader = getReceiptData().getReceiptHeader();
				setExcessList(receiptHeader.getExcessAmounts());

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(financeDetail.getFinScheduleData().getFinanceMain());
				getReceiptData().getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);

			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
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
			doStoreServiceIds(getFinanceDetail().getFinScheduleData().getFinanceMain());

			// READ OVERHANDED parameters !
			if (!setSummaryData(false)) {

				// set Read only mode accordingly if the object is new or not.
				doEdit();
				if (StringUtils.isNotBlank(financeMain.getRecordType())) {
					this.btnNotes.setVisible(true);
				} else {
					this.btnReceipt.setDisabled(true);
					this.btnChangeReceipt.setDisabled(true);
				}

				//Reset Finance Repay Header Details
				doWriteBeanToComponents();

				this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
				this.listBoxPayment.setHeight(getListBoxHeight(6));
				this.listBoxSchedule.setHeight(getListBoxHeight(6));
				this.receiptDetailsTab.setSelected(true);

				// Setting tile Name based on Service Action
				this.windowTitle.setValue(Labels.getLabel(moduleDefiner+"_Window.Title"));
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_ReceiptDialog.onClose();
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

		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole(), menuItemRightName);

		this.btnReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));
		this.btnCalcReceipts.setVisible(getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));
		this.btnChangeReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		this.finType.setMaxlength(8);
		this.finReference.setMaxlength(20);
		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.lovDescCustCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.finStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.totDisbursements.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totDownPayment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totCpzAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totFeeAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totChargeAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totWaiverAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.schPriTillNextDue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.schPftTillNextDue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPriPaid.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPftPaid.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPriDue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPftDue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

		this.overDuePrincipal.setMaxlength(18);
		this.overDuePrincipal.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.overDueProfit.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.lastFullyPaidDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextPayDueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accruedPft.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.provisionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.pendingODC.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

		//Receipts Details
		this.receipt_finType.setMaxlength(8);
		this.receipt_finReference.setMaxlength(20);
		this.receipt_finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.receipt_finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.receipt_CustCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.receipt_paidByCustomer.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.receiptAmount.setProperties(true , formatter);
		
		this.fundingAccount.setButtonVisible(false);
		this.fundingAccount.setMandatory(true);
		this.fundingAccount.setAcountDetails("", "", true);
		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(100);
		this.favourName.setMaxlength(100);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(50);

		// Allocation Details
		this.allocation_finType.setMaxlength(8);
		this.allocation_finReference.setMaxlength(20);
		this.allocation_finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.allocation_finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.allocation_CustCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.allocation_paidByCustomer.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_receiptPurpose"), this.receiptPurpose);
		readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("ReceiptDialog_receiptMode"), this.receiptMode);
		readOnlyComponent(isReadOnly("ReceiptDialog_receiptAmount"), this.receiptAmount);
		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
		
		//Receipt Details
		readOnlyComponent(isReadOnly("ReceiptDialog_favourNo"), this.favourNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_bankCode"), this.bankCode);
		readOnlyComponent(isReadOnly("ReceiptDialog_favourName"), this.favourName);
		readOnlyComponent(isReadOnly("ReceiptDialog_depositDate"), this.depositDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_depositNo"), this.depositNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_chequeAcNo"), this.chequeAcNo);
		readOnlyComponent(isReadOnly("ReceiptDialog_fundingAccount"), this.fundingAccount);
		readOnlyComponent(isReadOnly("ReceiptDialog_cashReceivedDate"), this.receivedDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);

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
	private boolean setSummaryData(boolean isChgReceipt) throws InterruptedException, IllegalAccessException,
	InvocationTargetException {
		logger.debug("Entering");

		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setBuildProcess("I");

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		int finformatter = CurrencyUtil.getFormat(financeMain.getFinCcy());;

		Customer customer = null;
		if(getFinanceDetail().getCustomerDetails() != null && getFinanceDetail().getCustomerDetails().getCustomer() != null){
			customer = getFinanceDetail().getCustomerDetails().getCustomer();
		}

		FinScheduleData aFinScheduleData = new FinScheduleData();

		if (isChgReceipt) {
			aFinScheduleData = getFinanceDetailService().getFinSchDataForReceipt(financeMain.getFinReference(), "_AView");
			getFinanceDetail().setFinScheduleData(aFinScheduleData);
		} else {
			Cloner cloner = new Cloner();
			aFinScheduleData = cloner.deepClone(getFinanceDetail().getFinScheduleData());
		}

		receiptData.setAccruedTillLBD(aFinScheduleData.getFinanceMain().getLovDescAccruedTillLBD());
		receiptData.setFinanceDetail(getFinanceDetail());
		setReceiptData(getReceiptCalculator().initiateReceipt(receiptData, aFinScheduleData, getReceiptHeader().getReceiptPurpose()));
		receiptData.getRepayMain().setLovDescFinFormatter(finformatter);
		setRepayMain(receiptData.getRepayMain());

		this.finType.setValue(getRepayMain().getFinType());
		this.finReference.setValue(getRepayMain().getFinReference());
		this.finCcy.setValue(getRepayMain().getFinCcy());
		fillComboBox(this.profitDayBasis, getRepayMain().getProfitDaysBais(), PennantStaticListUtil.getProfitDaysBasis(), "");
		this.custID.setValue(getRepayMain().getCustID());
		if(customer != null){
			this.lovDescCustCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
		}
		this.finBranch.setValue(getRepayMain().getFinBranch());
		this.finStartDate.setValue(getRepayMain().getDateStart());
		this.maturityDate.setValue(getRepayMain().getDateMatuirty());
		this.totDisbursements.setValue(PennantAppUtil.formateAmount(getRepayMain().getFinAmount(), finformatter));
		this.totDownPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getDownpayment(), finformatter));

		this.totCpzAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getTotalCapitalize(), finformatter));
		this.totPriAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipal(), finformatter));
		this.totPftAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfit(), finformatter));
		this.totFeeAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getTotalFeeAmt(), finformatter));
		this.totChargeAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));
		this.totWaiverAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));
		this.schPriTillNextDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalPayNow(), finformatter));
		this.schPftTillNextDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitPayNow(), finformatter));
		this.totPriPaid.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipal().subtract(getRepayMain().getPrincipalBalance()), finformatter));
		this.totPftPaid.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfit().subtract(getRepayMain().getProfitBalance()), finformatter));
		this.totPriDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalBalance(), finformatter));
		this.totPftDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitBalance(), finformatter));

		//Repayments modified Details
		this.overDuePrincipal.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverduePrincipal(), finformatter));
		this.overDueProfit.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverdueProfit(), finformatter));
		this.lastFullyPaidDate.setValue(getRepayMain().getDateLastFullyPaid());
		this.nextPayDueDate.setValue(getRepayMain().getDateNextPaymentDue());
		this.accruedPft.setValue(PennantAppUtil.formateAmount(getRepayMain().getAccrued(), finformatter));

		//Total Overdue Penalty Amount TODO: Remove
		BigDecimal pendingODC = getOverdueChargeRecoveryService().getPendingODCAmount(aFinScheduleData.getFinReference());
		receiptData.setPendingODC(pendingODC);
		this.pendingODC.setValue(PennantAppUtil.formateAmount(pendingODC, finformatter));

		// Receipt Basic Details
		this.receipt_finType.setValue(getRepayMain().getFinType());
		this.receipt_finReference.setValue(getRepayMain().getFinReference());
		this.receipt_finCcy.setValue(getRepayMain().getFinCcy());
		this.receipt_finBranch.setValue(getRepayMain().getFinBranch());
		if(customer != null){
			this.receipt_CustCIF.setValue(customer.getCustCIF());
		}
		this.receipt_paidByCustomer.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		// Allocation Basic Details
		this.allocation_finType.setValue(getRepayMain().getFinType());
		this.allocation_finReference.setValue(getRepayMain().getFinReference());
		this.allocation_finCcy.setValue(getRepayMain().getFinCcy());
		this.allocation_finBranch.setValue(getRepayMain().getFinBranch());
		if(customer != null){
			this.allocation_CustCIF.setValue(customer.getCustCIF());
		}
		this.allocation_paidByCustomer.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		// Repayment Schedule Basic Details
		this.payment_finType.setValue(getRepayMain().getFinType());
		this.payment_finReference.setValue(getRepayMain().getFinReference());
		this.payment_finCcy.setValue(getRepayMain().getFinCcy());
		this.payment_finBranch.setValue(getRepayMain().getFinBranch());
		if(customer != null){
			this.payment_CustCIF.setValue(customer.getCustCIF());
		}
		this.payment_paidByCustomer.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		// On Loading Data Render for Schedule
		if (!isChgReceipt) {
			if (getReceiptHeader() != null && getReceiptHeader().getReceiptDetails() != null
					&& !getReceiptHeader().getReceiptDetails().isEmpty()) {
				this.btnCalcReceipts.setDisabled(true);
			}
			//doFillRepaySchedules(getRepaySchdList());
		} else {
			//doFillRepaySchedules(receiptData.getRepayScheduleDetails());
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		arrayList.add(2, financeMain.getScheduleMethod());
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceType().getFinCategory());
		if(getFinanceDetail().getCustomerDetails() != null && getFinanceDetail().getCustomerDetails().getCustomer() != null){
			arrayList.add(9, getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
		}else{
			arrayList.add(9, "");
		}
		arrayList.add(10, false);
		arrayList.add(11, moduleDefiner);
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
	public void onClick$btnCalcReceipts(Event event) throws InterruptedException, WrongValueException, PFFInterfaceException {
		logger.debug("Entering" + event.toString());

		// Validate Required Fields Data
		if (!isValidateData()) {
			return;
		}

		if (StringUtils.equals(getComboboxValue(this.receiptPurpose), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			setEarlyRepayEffectOnSchedule(getReceiptData());
		} else {
			FinReceiptData receiptData = null;
			receiptData = calculateRepayments(getFinanceDetail().getFinScheduleData());
			if(receiptData != null){
				setRepayDetailData(receiptData);
			}
		}
		this.repaymentDetailsTab.setSelected(true);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Processing Calculation button visible , if amount modified
	 * @param event
	 */
	public void onFulfill$receiptAmount(Event event) {
		logger.debug("Entering");
		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));
		
		// Check Auto Allocation Process existence
		setAutoAllocationPayments();
		
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
	}
	
	/**
	 * Method for Processing Captured details based on Receipt Purpose
	 * @param event
	 */
	public void onChange$receiptPurpose(Event event) {
		String recPurpose = this.receiptPurpose.getSelectedItem().getValue().toString();
		checkByReceiptPurpose(recPurpose);
		
		// To set Payment details by default using Auto Allocation mode , if exists
		setAutoAllocationPayments();
	}
	
	/**
	 * Method for Setting Fields based on Receipt Purpose selected
	 * @param recPurpose
	 */
	private void checkByReceiptPurpose(String recPurpose) {
		logger.debug("Entering");
		
		readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		
		if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) ||
				StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			readOnlyComponent(true, this.effScheduleMethod);
			this.effScheduleMethod.setSelectedIndex(0);
			
			if(StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				fillComboBox(this.allocationMethod, RepayConstants.ALLOCATIONTYPE_AUTO, PennantStaticListUtil.getAllocationMethods(), "");
				readOnlyComponent(true, this.allocationMethod);
			}
		} else if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			readOnlyComponent(true, this.excessAdjustTo);
			this.excessAdjustTo.setSelectedIndex(0);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Captured details based on Receipt Mode
	 * @param event
	 */
	public void onChange$receiptMode(Event event) {
		String dType = this.receiptMode.getSelectedItem().getValue().toString();
		checkByReceiptMode(dType, true);
	}

	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");
		
		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select) ||
				StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);
			
			// Due to changes in Receipt Amount, call Auto Allocations
			if(isUserAction){
				setAutoAllocationPayments();
			}
		} else{

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_receiptAmount"), this.receiptAmount);
			
			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {
				
				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);
				this.row_remarks.setVisible(false);
				
				if(StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)){
					this.row_fundingAcNo.setVisible(true);
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));
				}else{
					this.row_fundingAcNo.setVisible(false);
					this.row_ChequeAcNo.setVisible(false);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_DDFavourNo.value"));
				}
				
			} else if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)) {
				
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				this.row_fundingAcNo.setVisible(true);
				this.row_remarks.setVisible(true);
				
			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_fundingAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
				this.row_remarks.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * @param event
	 */
	public void onChange$allocationMethod(Event event) {
		logger.debug("Entering");
		
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);
		this.allocationDetailsTab.setDisabled(false);
		waivedAllocationMap = new HashMap<>();
		paidAllocationMap = new HashMap<>();
		if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)){
			setAutoAllocationPayments();
		}else if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
			doFillAllocationDetail(null, null, true);
		}else{
			this.allocationDetailsTab.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	
	private void setAutoAllocationPayments(){
		logger.debug("Entering");
		
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		this.receiptPurpose.setConstraint("");
		this.receiptPurpose.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);
		String tempReceiptPurpose = getComboboxValue(this.receiptPurpose);
		
		// Set total , If Receipt Purpose is Early settlement
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setBuildProcess("I");
		FinScheduleData schData = new FinScheduleData();
		Cloner cloner = new Cloner();
		schData = cloner.deepClone(getFinanceDetail().getFinScheduleData());

		receiptData.setAccruedTillLBD(schData.getFinanceMain().getLovDescAccruedTillLBD());
		receiptData.setFinanceDetail(getFinanceDetail());
		setReceiptData(getReceiptCalculator().initiateReceipt(receiptData, schData, tempReceiptPurpose));

		doFillAllocationDetail(null, null, false);
		
		// Allocation Process start
		if(!StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)){
			
			//Setting remaining Balance on Manual Allocation
			if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
				int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
				BigDecimal totReceiptAmount = getTotalReceiptAmount();
				
				BigDecimal totalPaid = BigDecimal.ZERO;
				if(this.listBoxPastdues.getFellowIfAny("allocation_totalPaid") != null){
					Label paid = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalPaid");
					totalPaid = PennantApplicationUtil.unFormateAmount(new BigDecimal(paid.getValue().replaceAll(",", "")), formatter);
				}
				
				BigDecimal remBal = totReceiptAmount.subtract(totalPaid);
				if(remBal.compareTo(BigDecimal.ZERO) < 0){
					remBal = BigDecimal.ZERO;
				}
				this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBal, formatter));
			}
			logger.debug("Leaving");
			return;
		}
		
		// Calling for Past due Amount Auto Calculation Process
		FinScheduleData aFinScheduleData = getFinanceDetailService().getFinSchDataForReceipt(this.finReference.getValue(), "_AView");
		Map<String, BigDecimal> paidAllocationMap = getReceiptCalculator().recalAutoAllocation(aFinScheduleData, 
				getTotalReceiptAmount(), tempReceiptPurpose);
		doFillAllocationDetail(null, paidAllocationMap, true);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for fetch sum of Total user entered Receipts amounts
	 * @return
	 */
	private BigDecimal getTotalReceiptAmount(){
		
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal totalReceiptAmount = BigDecimal.ZERO;
		
		// Fetch Receipt Mode related Amount
		totalReceiptAmount = PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(), formatter);
		
		// Fetch Excess Amounts
		if(listBoxExcess.getFellowIfAny("ExcessAmount_E") != null){
			CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_E");
			totalReceiptAmount = totalReceiptAmount.add(PennantApplicationUtil.unFormateAmount(excessBox.getActualValue(), formatter));
		}
		
		//Fetch EMI in Advance Amount
		if(listBoxExcess.getFellowIfAny("ExcessAmount_A") != null){
			CurrencyBox emiInAdvBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_A");
			totalReceiptAmount = totalReceiptAmount.add(PennantApplicationUtil.unFormateAmount(emiInAdvBox.getActualValue(), formatter));
		}
		
		return totalReceiptAmount;
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterruptedException
	 */
	public void setEarlyRepayEffectOnSchedule(FinReceiptData receiptData) throws InterruptedException {
		logger.debug("Entering");
		
		//Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		String method = getComboboxValue(this.effScheduleMethod);
		// Schedule re-modifications only when Effective Schedule Method modified
		if (!StringUtils.equals(method, CalculationConstants.EARLYPAY_NOEFCT)) {
			
			receiptData.getRepayMain().setEarlyPayAmount(PennantApplicationUtil.unFormateAmount(this.remBalAfterAllocation.getValue(), 
					receiptData.getRepayMain().getLovDescFinFormatter()));

			if (StringUtils.equals(method, CalculationConstants.EARLYPAY_RECPFI)
					|| StringUtils.equals(method, CalculationConstants.EARLYPAY_ADMPFI)) {
				aFinanceMain.setPftIntact(true);
			}

			if (receiptData.getRepayMain().getEarlyRepayNewSchd() != null) {
				if (StringUtils.equals(method, CalculationConstants.EARLYPAY_RECPFI)) {
					receiptData.getRepayMain().getEarlyRepayNewSchd().setRepayOnSchDate(false);
					receiptData.getRepayMain().getEarlyRepayNewSchd().setPftOnSchDate(false);
					receiptData.getRepayMain().getEarlyRepayNewSchd().setRepayAmount(BigDecimal.ZERO);
				}
				finScheduleData.getFinanceScheduleDetails().add(receiptData.getRepayMain().getEarlyRepayNewSchd());
			}

			for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
				if (detail.getDefSchdDate().compareTo(receiptData.getRepayMain().getEarlyPayOnSchDate()) == 0) {
					if (StringUtils.equals(method, CalculationConstants.EARLYPAY_RECPFI)) {
						detail.setEarlyPaid(detail.getEarlyPaid().add(receiptData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					} else {
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						receiptData.getRepayMain().setEarlyPayAmount(
								receiptData.getRepayMain().getEarlyPayAmount().add(earlypaidBal));
					}
				}
				if (detail.getDefSchdDate().compareTo(receiptData.getRepayMain().getEarlyPayOnSchDate()) >= 0) {
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			finScheduleData.setFinanceType(getFinanceType());

			//Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, receiptData.getRepayMain()
					.getEarlyPayOnSchDate(), receiptData.getRepayMain().getEarlyPayNextSchDate(), receiptData
					.getRepayMain().getEarlyPayAmount(), method);

			// Validation against Future Disbursements, if Closing balance is becoming zero before future disbursement date
			List<FinanceDisbursement> disbList = finScheduleData.getDisbursementDetails();
			Date actualMaturity = finScheduleData.getFinanceMain().getCalMaturity();
			for (int i = 0; i < disbList.size(); i++) {
				FinanceDisbursement curDisb = disbList.get(i);
				if(curDisb.getDisbDate().compareTo(actualMaturity) >= 0){
					MessageUtil.showErrorMessage(ErrorUtil.getErrorDetail(new ErrorDetails("30577", null)));
					Events.sendEvent(Events.ON_CLICK, this.btnChangeReceipt, null);
					logger.debug("Leaving");
					return;
				}
			}

			financeDetail.setFinScheduleData(finScheduleData);
			aFinanceMain = finScheduleData.getFinanceMain();
			aFinanceMain.setWorkflowId(getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());
			setFinanceDetail(financeDetail);//Object Setting for Future save purpose
			receiptData.setFinanceDetail(financeDetail);

			this.finSchType.setValue(aFinanceMain.getFinType());
			this.finSchCcy.setValue(aFinanceMain.getFinCcy());
			this.finSchMethod.setValue(aFinanceMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis()));
			this.finSchReference.setValue(aFinanceMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getGrcPeriodEndDate()));
			this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn().toString() + "%");

			//Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);
			this.effectiveScheduleTab.setVisible(true);

			//Dashboard Details Report
			doLoadTabsData();
			doShowReportChart(finScheduleData);
		}

		//Repayments Calculation
		receiptData = calculateRepayments(finScheduleData);
		setReceiptData(receiptData);
		setRepayDetailData(receiptData);

		logger.debug("Leaving");
	}

	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
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

		FinanceScheduleDetail prvSchDetail = null;

		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if (sdSize > 0) {

			// Find Out Fee charge Details on Schedule
			Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
			if (aFinScheduleData.getFeeRules() != null && aFinScheduleData.getFeeRules().size() > 0) {
				feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();

				for (FeeRule fee : aFinScheduleData.getFeeRules()) {
					if (feeChargesMap.containsKey(fee.getSchDate())) {
						ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					} else {
						ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
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
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap.get(penaltyDetail
								.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					} else {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}

			//Clear all the listitems in listbox
			this.listBoxSchedule.getItems().clear();
			this.listBoxSchedule.setSizedByContent(true);
			this.listBoxSchedule.setStyle("hflex:min;");

			aFinScheduleData.setFinanceScheduleDetails(sortSchdDetails(aFinScheduleData.getFinanceScheduleDetails()));

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
				finRender.render(map, prvSchDetail, false, true, false, feeChargesMap, showRate, false);
				
				if(aScheduleDetail.getFeeChargeAmt().compareTo(BigDecimal.ZERO) >= 0  && 
						aFinScheduleData.getFinFeeDetailList() != null && !aFinScheduleData.getFinFeeDetailList().isEmpty()){
					finRender.renderOrg(map, prvSchDetail, false, true, false, aFinScheduleData.getFinFeeDetailList(), showRate, false);
				}else{
					finRender.render(map, prvSchDetail, false, true, false, feeChargesMap, showRate, false);
				}

				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, true, false, feeChargesMap, showRate, false);
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Calculate Payment Details based on Entered Receipts
	 * @param financeMain
	 * @param finSchDetails
	 * @param isReCal
	 * @param method
	 * @param valueDate
	 * @return
	 */
	private FinReceiptData calculateRepayments(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		getReceiptData().setBuildProcess("R");
		getReceiptData().getRepayMain().setRepayAmountNow(BigDecimal.ZERO);
		getReceiptData().getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		getReceiptData().getRepayMain().setProfitPayNow(BigDecimal.ZERO);
		
		// Prepare Receipt Details Data
		doClearMessage();
		doSetValidation();
		FinReceiptHeader receiptHeader = doWriteComponentsToBean();
		receiptHeader.setReceiptAmount(getTotalReceiptAmount());
		receiptHeader.getReceiptDetails().clear();
		receiptHeader.getAllocations().clear();
		
		// Basic Receipt Mode Details
		FinReceiptDetail receiptDetail = null;
		if(!StringUtils.equals(RepayConstants.RECEIPTMODE_EXCESS, receiptHeader.getReceiptMode())){
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(receiptHeader.getReceiptMode());
			receiptDetail.setPayAgainstID(0);
			receiptDetail.setAmount(receiptHeader.getReceiptAmount());
			receiptDetail.setFavourNumber(this.favourNo.getValue());
			receiptDetail.setValueDate(this.valueDate.getValue());
			receiptDetail.setBankCode(this.bankCode.getValue());
			receiptDetail.setFavourName(this.favourName.getValue());
			receiptDetail.setDepositDate(this.depositDate.getValue());
			receiptDetail.setDepositNo(this.depositNo.getValue());
			receiptDetail.setPaymentRef(this.paymentRef.getValue());
			receiptDetail.setTransactionRef(this.transactionRef.getValue());
			receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
			receiptDetail.setFundingAc(this.fundingAccount.getValue());
			receiptDetail.setReceivedDate(this.receivedDate.getValue());
			receiptDetail.setRemarks(this.remarks.getValue());
			receiptHeader.getReceiptDetails().add(receiptDetail);
		}
		
		Map<String, FinExcessAmount> excessMap = new HashMap<>();
		if(getExcessList() != null && !getExcessList().isEmpty()){
			for (int i = 0; i < getExcessList().size(); i++) {
				excessMap.put(getExcessList().get(i).getAmountType(), getExcessList().get(i));
			}
		}
		
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		// Excess Amount Receipt Detail
		if(this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EXCESS) != null){
			CurrencyBox excessAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EXCESS);
			if(excessAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0){
				receiptDetail = new FinReceiptDetail();
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EXCESS);
				receiptDetail.setPayAgainstID(excessMap.get(RepayConstants.EXAMOUNTTYPE_EXCESS).getExcessID());
				receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(excessAmount.getActualValue(), finFormatter));
				receiptDetail.setValueDate(this.valueDate.getValue());
				receiptDetail.setReceivedDate(this.valueDate.getValue());
				receiptHeader.getReceiptDetails().add(receiptDetail);
			}
		}
		
		// EMI In Advance Receipt Mode
		if(this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EMIINADV) != null){
			CurrencyBox emiAdvance = (CurrencyBox) this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EMIINADV);
			if(emiAdvance.getActualValue().compareTo(BigDecimal.ZERO) > 0){
				receiptDetail = new FinReceiptDetail();
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
				receiptDetail.setPayAgainstID(excessMap.get(RepayConstants.EXAMOUNTTYPE_EMIINADV).getExcessID());
				receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(emiAdvance.getActualValue(), finFormatter));
				receiptDetail.setValueDate(this.valueDate.getValue());
				receiptDetail.setReceivedDate(this.valueDate.getValue());
				receiptHeader.getReceiptDetails().add(receiptDetail);
			}
		}
		
		// Payable Advise Receipt Modes TODO 
		
		// Prepare Allocation Details
		List<String> allocateTypes = new ArrayList<>(getReceiptData().getAllocationMap().keySet());
		ReceiptAllocationDetail allocationDetail = null;
		for (int i = 0; i < allocateTypes.size(); i++) {
			allocationDetail = new ReceiptAllocationDetail();
			
			String allocationType = allocateTypes.get(i);
			String allocateTo = "";
			if(allocateTypes.get(i).contains("~")){
				allocationType = allocateTypes.get(i).substring(0, allocateTypes.get(i).indexOf("~"));
				allocateTo = allocateTypes.get(i).substring(allocateTypes.get(i).indexOf("~")+1);
			}
			
			allocationDetail.setAllocationID(i+1);
			allocationDetail.setAllocationType(allocationType);
			allocationDetail.setAllocationTo(allocateTo);
			if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV)){
				
				if(this.listBoxManualAdvises.getFellowIfAny("AllocatePaid_"+allocateTypes.get(i)) != null){
					CurrencyBox paidAllocate = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny("AllocatePaid_"+allocateTypes.get(i));
					allocationDetail.setPaidAmount(PennantApplicationUtil.unFormateAmount(paidAllocate.getActualValue(), finFormatter));
				}
				
				if(this.listBoxManualAdvises.getFellowIfAny("AllocateWaived_"+allocateTypes.get(i)) != null){
					CurrencyBox waivedAllocate = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny("AllocateWaived_"+allocateTypes.get(i));
					allocationDetail.setWaivedAmount(PennantApplicationUtil.unFormateAmount(waivedAllocate.getActualValue(), finFormatter));
				}
			}else{
				
				if(this.listBoxPastdues.getFellowIfAny("AllocatePaid_"+allocateTypes.get(i)) != null){
					CurrencyBox paidAllocate = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocatePaid_"+allocateTypes.get(i));
					allocationDetail.setPaidAmount(PennantApplicationUtil.unFormateAmount(paidAllocate.getActualValue(), finFormatter));
				}
				
				if(this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+allocateTypes.get(i)) != null){
					CurrencyBox waivedAllocate = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+allocateTypes.get(i));
					allocationDetail.setWaivedAmount(PennantApplicationUtil.unFormateAmount(waivedAllocate.getActualValue(), finFormatter));
				}
			}
			
			if(allocationDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0){
				receiptHeader.getAllocations().add(allocationDetail);
			}
		}
		
		// Setting Extra amount for Partial Settlement case
		if(StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)){
			for (ReceiptAllocationDetail detail : receiptHeader.getAllocations()) {
				if(StringUtils.equals(detail.getAllocationType(), RepayConstants.ALLOCATION_PRI) && 
						this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0){
					detail.setPaidAmount(detail.getPaidAmount().add(PennantApplicationUtil.unFormateAmount(this.remBalAfterAllocation.getValue(), finFormatter)));
					break;
				}
			}
		}
		
		excessMap = null;
		getReceiptData().setReceiptHeader(receiptHeader);
		receiptData = getReceiptCalculator().initiateReceipt(getReceiptData(), aFinScheduleData, receiptHeader.getReceiptPurpose());
		setReceiptData(receiptData);

		logger.debug("Leaving");
		return receiptData;
	}

	private void setRepayDetailData(FinReceiptData receiptData) throws InterruptedException {
		logger.debug("Entering");

		// Repay Schedule Data rebuild
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();
		for (int i = 0; i < receiptDetailList.size(); i++) {
			List<FinRepayHeader> repayHeaderList = receiptDetailList.get(i).getRepayHeaders();
			for (int j = 0; j < repayHeaderList.size(); j++) {
				rpySchdList.addAll(repayHeaderList.get(j).getRepayScheduleDetails());
			}
		}
		
		// Making Single Set of Repay Schedule Details and sent to Rendering
		Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
		for (RepayScheduleDetail rpySchd : rpySchdList) {
			
			RepayScheduleDetail curRpySchd = null;
			if(rpySchdMap.containsKey(rpySchd.getSchDate())){
				curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
				curRpySchd.setPrincipalSchdPayNow(curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
				curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
				curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
				curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
				rpySchdMap.remove(rpySchd.getSchDate());
			}else{
				curRpySchd = rpySchd;
			}
			
			// Adding New Repay Schedule Object to Map after Summing data
			rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
		}
		
		doFillRepaySchedules(sortRpySchdDetails(new ArrayList<>(rpySchdMap.values())));

		this.btnReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnReceipt"));
		this.btnChangeReceipt.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnChangeReceipt"));
		if (!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			this.btnCalcReceipts.setDisabled(true);
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
	public void onClick$btnChangeReceipt(Event event) throws InterruptedException, IllegalAccessException,
	InvocationTargetException {
		logger.debug("Entering" + event.toString());

		setSummaryData(true);
		this.btnReceipt.setDisabled(true);
		this.btnChangeReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));
		this.receiptDetailsTab.setSelected(true);
		this.effectiveScheduleTab.setVisible(false);

		if (tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null) {
			tabpanelsBoxIndexCenter.removeChild(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"));
		}

		if (tabsIndexCenter.getFellowIfAny("dashboardTab") != null) {
			tabsIndexCenter.removeChild(tabsIndexCenter.getFellowIfAny("dashboardTab"));
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReceipt(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() throws WrongValueException, InterruptedException {
		logger.debug("Entering");

		try {
			boolean recReject = false;
			if (this.userAction.getSelectedItem() != null
					&& ("Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) || "Cancel"
							.equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
				recReject = true;
			}

			if (getFinanceCheckListReferenceDialogCtrl() != null
					&& (getFinanceDetail().getFinRefDetailsList() == null || getFinanceDetail().getFinRefDetailsList()
					.isEmpty())) {
				getFinanceDetail().setCustomerEligibilityCheck(
						prepareCustElgDetail(false).getCustomerEligibilityCheck());
				getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
						getFinanceDetail().getFinanceCheckList(), true);
			}

			if (recReject || isValidateData()) {
				if (!isLimitExceeded) {
					this.btnChangeReceipt.setDisabled(true);
					this.btnCalcReceipts.setDisabled(true);
					
					String recptPurpose = getComboboxValue(this.receiptPurpose);

					//If Schedule Re-modified Save into DB or else only add Repayments Details
					if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {

						processRepayScheduleList(getFinanceDetail().getFinScheduleData().getFinanceMain(),
								getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), getFinanceDetail()
								.getFinScheduleData().getRepayInstructions(), true);
					} else {

						//financeMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(repayAccount.getValue()));
						processRepayScheduleList(getFinanceDetail().getFinScheduleData().getFinanceMain(),
								getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), null, false);
					}

				} else {
					MessageUtil.showErrorMessage(" Limit exceeded ... ");
					return;
				}
			}

		} catch (PFFInterfaceException pfe) {
			logger.error("Exception: ", pfe);
			MessageUtil.showErrorMessage(pfe.getErrorMessage());
			return;
		} catch (WrongValueException we) {
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
	 * @param aFinanceMain
	 * @param finSchDetails
	 * @param repayInstructions
	 * @param schdlReModified
	 * @throws Exception
	 */
	private void processRepayScheduleList(FinanceMain aFinanceMain, List<FinanceScheduleDetail> finSchDetails,
			List<RepayInstruction> repayInstructions, boolean schdlReModified) throws Exception {
		logger.debug("Entering");

		FinReceiptData data = getReceiptData();
		data.setFinanceDetail(getFinanceDetail());
		data.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		data.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(finSchDetails);
		data.getFinanceDetail().getFinScheduleData().setRepayInstructions(repayInstructions);
		//data.setRepayScheduleDetails(getRepaySchdList());
		data.getFinanceDetail().getFinScheduleData().setFinanceType(getFinanceType());
		data.getFinanceDetail().setUserAction(this.userAction.getSelectedItem().getLabel());
		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
			}
		}

		//Prepare Finance Repay Header Details
		//data.setFinRepayHeader(doWriteComponentsToBean(schdlReModified));

		//Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);

		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		FinReceiptData aReceiptData = cloner.deepClone(data);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aFinanceMain.setNewRecord(true);
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		//Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aReceiptData.getFinanceDetail().setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aReceiptData.getFinanceDetail().setDocumentDetailsList(null);
		}

		//Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum().compareTo(
					getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aReceiptData.getFinanceDetail().setStageAccountingList(null);
		}

		if (!recSave && getAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
			if (getAccountingDetailDialogCtrl().getDisbCrSum()
					.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		}

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aReceiptData.getFinanceDetail(), false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aReceiptData.getFinanceDetail().setFinanceCheckList(null);
		}

		aReceiptData.setEventCodeRef(eventCode);

		// save it to database
		try {
			
			aFinanceMain.setRcdMaintainSts(aReceiptData.getReceiptHeader().getReceiptPurpose());
			aReceiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
			
			if (doProcess(aReceiptData, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				//Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				//Mail Alert Notification for Customer/Dealer/Provider...etc
				FinanceDetail financeDetail = aReceiptData.getFinanceDetail();
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					List<String> templateTyeList = new ArrayList<String>();
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_AE);
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_CN);

					List<ValueLabel> referenceIdList = getFinanceReferenceDetailService().getTemplateIdList(
							aFinanceMain.getFinType(), moduleDefiner, getRole(), templateTyeList);

					templateTyeList = null;
					if (!referenceIdList.isEmpty()) {

						boolean isCustomerNotificationExists = false;
						List<Long> notificationIdlist = new ArrayList<Long>();
						for (ValueLabel valueLabel : referenceIdList) {
							notificationIdlist.add(Long.valueOf(valueLabel.getValue()));
							if (NotificationConstants.TEMPLATE_FOR_CN.equals(valueLabel.getLabel())) {
								isCustomerNotificationExists = true;
							}
						}

						// Mail ID details preparation
						Map<String, List<String>> mailIDMap = new HashMap<String, List<String>>();

						// Customer Email Preparation
						if (isCustomerNotificationExists
								&& financeDetail.getCustomerDetails().getCustomerEMailList() != null
								&& !financeDetail.getCustomerDetails().getCustomerEMailList().isEmpty()) {

							List<CustomerEMail> emailList = financeDetail.getCustomerDetails().getCustomerEMailList();
							List<String> custMailIdList = new ArrayList<String>();
							for (CustomerEMail customerEMail : emailList) {
								custMailIdList.add(customerEMail.getCustEMail());
							}
							if (!custMailIdList.isEmpty()) {
								mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, custMailIdList);
							}
						}

						getMailUtil().sendMail(notificationIdlist, financeDetail, mailIDMap, null);
					}

				}

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						// Send message Notification to Users
						if (financeDetail.getFinScheduleData().getFinanceMain().getNextUserId() != null) {

							Notify notify = Notify.valueOf("USER");
							String[] to = financeDetail.getFinScheduleData().getFinanceMain().getNextUserId()
									.split(",");
							if (StringUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinanceMain()
									.getFinReference())) {

								String reference = financeDetail.getFinScheduleData().getFinanceMain()
										.getFinReference();
								if (!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(financeDetail
										.getFinScheduleData().getFinanceMain().getRecordStatus())) {
									getEventManager().publish(
											Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference" + ":"
													+ reference, notify, to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify, to);
							}

						} else {

							String nextRoleCodes = financeDetail.getFinScheduleData().getFinanceMain()
									.getNextRoleCode();
							if (StringUtils.isNotEmpty(nextRoleCodes)) {
								Notify notify = Notify.valueOf("ROLE");
								String[] to = nextRoleCodes.split(",");
								if (StringUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinanceMain()
										.getFinReference())) {

									String reference = financeDetail.getFinScheduleData().getFinanceMain()
											.getFinReference();
									if (!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(financeDetail
											.getFinScheduleData().getFinanceMain().getRecordStatus())) {
										getEventManager().publish(
												Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference" + ":"
														+ reference, notify, to);
									}
								} else {
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify, to);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showErrorMessage(this.window_ReceiptDialog, e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		// Receipt Header Details
		FinReceiptHeader header = getReceiptHeader();
		String processType = header.getReceiptPurpose();
		fillComboBox(this.receiptPurpose, header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), "");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));
		
		String allocateMthd = header.getAllocationType();
		if(StringUtils.isEmpty(allocateMthd)){
			allocateMthd = RepayConstants.ALLOCATIONTYPE_AUTO;
		}
		fillComboBox(this.allocationMethod, allocateMthd, PennantStaticListUtil.getAllocationMethods(), "");
		fillComboBox(this.effScheduleMethod, header.getEffectSchdMethod(), PennantStaticListUtil.getEarlyPayEffectOn(), ",NOEFCT,");
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));

		// Receipt Mode Details , if FinReceiptDetails Exists
		String receiptPaidMode = header.getReceiptMode();
		checkByReceiptMode(receiptPaidMode, false);

		// Render Excess Amount Details
		doFillExcessAmounts();

		// Render Allocation Details & Manual Advises
		if(header.getAllocations() != null && !header.getAllocations().isEmpty()){
			for (int i = 0; i < header.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocate = header.getAllocations().get(i);
				if(StringUtils.isEmpty(allocate.getAllocationTo())){
					paidAllocationMap.put(allocate.getAllocationType(), allocate.getPaidAmount());
					waivedAllocationMap.put(allocate.getAllocationType(), allocate.getWaivedAmount());
				}else{
					paidAllocationMap.put(allocate.getAllocationType()+"~"+allocate.getAllocationTo(), allocate.getPaidAmount());
					waivedAllocationMap.put(allocate.getAllocationType()+"~"+allocate.getAllocationTo(), allocate.getWaivedAmount());
				}
			}
		}
		doFillAllocationDetail(header.getAllocations(), null, false);

		// Only In case of partial settlement process, Display details for effective Schedule
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, processType)) {

			FinanceDetail financeDetail = getFinanceDetail();
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			finScheduleData.setFinanceMain(financeDetail.getFinScheduleData().getFinanceMain());
			finScheduleData.setFinanceScheduleDetails(financeDetail.getFinScheduleData().getFinanceScheduleDetails());

			//Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);
			this.effectiveScheduleTab.setVisible(true);

			//Dashboard Details Report
			doLoadTabsData();
			doShowReportChart(finScheduleData);

		}

		getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);

		//Customer Details   
		appendCustomerDetailTab();

		//Fee Details Tab Addition
		appendFeeDetailTab();

		// Schedule Details
		appendScheduleDetailTab(true, false);

		//Agreement Details
		appendAgreementsDetailTab(true);

		// Check List Details
		appendCheckListDetailTab(getReceiptData().getFinanceDetail(), false, true);

		// Recommendation Details 
		appendRecommendDetailTab(true);

		//Document Details
		appendDocumentDetailTab();

		// Stage Accounting Details
		appendStageAccountingDetailsTab(true);

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			//Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}

		this.recordStatus.setValue(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Excess Amount Details
	 */
	private void doFillExcessAmounts(){
		logger.debug("Entering");

		// Excess Amounts
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		List<String> excessAmountTypes = new ArrayList<>();
		excessAmountTypes.add(RepayConstants.EXAMOUNTTYPE_EXCESS);
		excessAmountTypes.add(RepayConstants.EXAMOUNTTYPE_EMIINADV);

		Map<String, FinExcessAmount> excessMap = new HashMap<>();
		List<FinExcessAmount> excessAmountList = getExcessList();
		if(excessAmountList != null && !excessAmountList.isEmpty()){
			for (int i = 0; i < excessAmountList.size(); i++) {
				excessMap.put(excessAmountList.get(i).getAmountType(), excessAmountList.get(i));
			}
		}

		FinExcessAmount excess = null;
		Listitem item = null;
		Listcell lc = null;
		this.listBoxExcess.getItems().clear();
		for (int i = 0; i < excessAmountTypes.size(); i++) {

			String excessAmtType = excessAmountTypes.get(i);
			if(excessMap.containsKey(excessAmtType)){
				excess = excessMap.get(excessAmtType);
			}else{
				excess = new FinExcessAmount();
			}
			item = new Listitem();

			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_ExcessType_"+excessAmtType));
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(excess.getAmount(), finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			BigDecimal paidAmount = BigDecimal.ZERO;//TODO: Entered Excess Amounts from Receipt Details
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			CurrencyBox excessAmount = new CurrencyBox();
			excessAmount.setStyle("text-align:right;");
			excessAmount.setBalUnvisible(true);
			setProps(excessAmount, false, finFormatter, 120);
			excessAmount.setId("ExcessAmount_"+excessAmtType);
			excessAmount.setValue(PennantApplicationUtil.formateAmount(paidAmount, finFormatter));
			excessAmount.setReadonly(isReadOnly("ReceiptDialog_ExcessAmount"));
			lc.appendChild(excessAmount);
			lc.setParent(item);

			BigDecimal balanceAmount = excess.getAmount().subtract(paidAmount);
			Label balLabel = new Label(PennantApplicationUtil.amountFormate(balanceAmount, finFormatter));
			balLabel.setId("ExcessBal_"+excessAmtType);

			List<Object> list = new ArrayList<>();
			list.add(excess.getAmount());
			list.add(excessAmount);
			list.add(balLabel);
			excessAmount.addForward("onFulfill", this.window_ReceiptDialog, "onExcessAmountChange", list);
			lc = new Listcell();
			lc.appendChild(balLabel);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			this.listBoxExcess.appendChild(item);
		}
		excessAmountTypes = null;
		excessMap = null;

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * @param header
	 * @param allocatePaidMap
	 */
	private void doFillAllocationDetail(List<ReceiptAllocationDetail> allocations, Map<String, BigDecimal> allocatePaidMap, boolean isUserAction){
		logger.debug("Entering");
		
		// Allocation Details & Manual Advises
		Map<String, ReceiptAllocationDetail> allocationMap = new HashMap<>();
		if(allocations != null && !allocations.isEmpty()){
			for (int i = 0; i < allocations.size(); i++) {
				allocationMap.put(allocations.get(i).getAllocationType(), allocations.get(i));
			}
		}
		
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		ReceiptAllocationDetail allocation = null;
		List<String> allocateTypes = new ArrayList<>(getReceiptData().getAllocationMap().keySet());
		Listitem item = null;
		Listcell lc = null;
		
		// Get Receipt Purpose to Make Waiver amount Editable
		doRemoveValidation();
		doClearMessage();
		String tempReceiptPurpose = getComboboxValue(this.receiptPurpose);
		String allocateMthd  = getComboboxValue(this.allocationMethod);
		this.listBoxManualAdvises.getItems().clear();
		this.listBoxPastdues.getItems().clear();
		
		BigDecimal totalDueAmount = BigDecimal.ZERO;
		BigDecimal totalPaidAmount = BigDecimal.ZERO;
		BigDecimal totalWaivedAmount = BigDecimal.ZERO;
		
		for (int i = 0; i < allocateTypes.size(); i++) {

			String allocationType = allocateTypes.get(i);
			if(allocateTypes.get(i).contains("~")){
				allocationType = allocateTypes.get(i).substring(0, allocateTypes.get(i).indexOf("~"));
			}
			if(allocationMap.containsKey(allocationType)){
				allocation = allocationMap.get(allocationType);
			}else{
				allocation = new ReceiptAllocationDetail();
			}
			
			BigDecimal totalCalAmount = getReceiptData().getAllocationMap().get(allocateTypes.get(i));

			item = new Listitem();
			String label = Labels.getLabel("label_RecceiptDialog_AllocationType_"+allocationType);
			if(allocateTypes.get(i).contains("~")){
				label = label+" : "+getReceiptData().getAllocationDescMap().get(allocateTypes.get(i));
			}
			lc = new Listcell(label);
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totalCalAmount, finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell();
			CurrencyBox allocationPaid = new CurrencyBox();
			allocationPaid.setStyle("text-align:right;");
			allocationPaid.setBalUnvisible(true);
			setProps(allocationPaid, false, finFormatter, 120);
			allocationPaid.setId("AllocatePaid_"+allocateTypes.get(i));
			if(allocatePaidMap != null){
				if(allocatePaidMap.containsKey(allocationType)){
					BigDecimal autoCalPaidAmt = allocatePaidMap.get(allocationType);
					allocationPaid.setValue(PennantApplicationUtil.formateAmount(autoCalPaidAmt, finFormatter));
				}else{
					allocationPaid.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
				}
			}else{
				if(isUserAction){
					if(paidAllocationMap != null && paidAllocationMap.containsKey(allocateTypes.get(i))){
						allocationPaid.setValue(PennantApplicationUtil.formateAmount(paidAllocationMap.get(allocateTypes.get(i)), finFormatter));
					}else{
						allocationPaid.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
					}
				}else{
					allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocation.getPaidAmount(), finFormatter));
				}
			}
			
			if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO) || 
					isReadOnly("ReceiptDialog_PastdueAmount")){
				allocationPaid.setReadonly(true);
			}else{
				allocationPaid.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
			}
			
			lc.appendChild(allocationPaid);
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell();
			CurrencyBox allocationWaived = new CurrencyBox();
			allocationWaived.setStyle("text-align:right;");
			allocationWaived.setBalUnvisible(true);
			setProps(allocationWaived, false, finFormatter, 120);
			allocationWaived.setId("AllocateWaived_"+allocateTypes.get(i));
			if(allocatePaidMap != null){
				if(waivedAllocationMap != null && waivedAllocationMap.containsKey(allocateTypes.get(i))){
					allocationWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocationMap.get(allocateTypes.get(i)), finFormatter));
				}else{
					allocationWaived.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
				}
			}else{
				allocationWaived.setValue(PennantApplicationUtil.formateAmount(allocation.getWaivedAmount(), finFormatter));
			}
			
			if(!StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE) || 
					isReadOnly("ReceiptDialog_PastdueAmount")){
				allocationWaived.setReadonly(true);
			}else{
				allocationWaived.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
			}
			
			lc.appendChild(allocationWaived);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			// Setting On Change Event for Amounts
			List<Object> paidList = new ArrayList<>();
			paidList.add(totalCalAmount);
			paidList.add(allocationPaid);
			allocationPaid.addForward("onFulfill", this.window_ReceiptDialog, "onAllocatePaidChange", paidList);
			
			// Setting On Change Event for Amounts
			List<Object> waivedList = new ArrayList<>();
			waivedList.add(totalCalAmount);
			waivedList.add(allocationWaived);
			allocationWaived.addForward("onFulfill", this.window_ReceiptDialog, "onAllocateWaivedChange", waivedList);
			
			if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV)){
				this.listBoxManualAdvises.appendChild(item);
			}else{
				this.listBoxPastdues.appendChild(item);
				totalDueAmount = totalDueAmount.add(totalCalAmount);
				totalPaidAmount = totalPaidAmount.add(PennantApplicationUtil.unFormateAmount(allocationPaid.getActualValue(), finFormatter));
				totalWaivedAmount = totalWaivedAmount.add(PennantApplicationUtil.unFormateAmount(allocationWaived.getActualValue(), finFormatter));
			}
		}
		
		// Creating Totals to verify against calculations & for validation
		if(totalDueAmount.compareTo(BigDecimal.ZERO) > 0){

			item = new Listitem();
			item.setStyle("background-color: #C0EBDF;");
			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);
			
			lc = new Listcell();
			Label label = new Label(PennantAppUtil.amountFormate(totalDueAmount, finFormatter));
			label.setId("allocation_totalDue");
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.appendChild(label);
			lc.setParent(item);

			lc = new Listcell();
			label = new Label(PennantAppUtil.amountFormate(totalPaidAmount, finFormatter));
			label.setId("allocation_totalPaid");
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.appendChild(label);
			lc.setParent(item);
			
			lc = new Listcell();
			label = new Label(PennantAppUtil.amountFormate(totalWaivedAmount, finFormatter));
			label.setId("allocation_totalWaived");
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.appendChild(label);
			lc.setParent(item);
			this.listBoxPastdues.appendChild(item);
			
		}
		
		// Setting Valid Components to open based upon Remaining Balance
		BigDecimal totReceiptAmount = getTotalReceiptAmount();
		BigDecimal remBal = totReceiptAmount.subtract(totalPaidAmount);
		if(remBal.compareTo(BigDecimal.ZERO) < 0){
			remBal = BigDecimal.ZERO;
		}
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBal, finFormatter));
		if(this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0){
			
			if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)){
				readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
			}else if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
				readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
			}else if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
			}
				
		}else{
			readOnlyComponent(true, this.excessAdjustTo);
			this.excessAdjustTo.setSelectedIndex(0);
			readOnlyComponent(true, this.effScheduleMethod);
			this.effScheduleMethod.setSelectedIndex(0);
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for action Event of Changing Profit Amount/Schedule Profit on Schedule term
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onExcessAmountChange(ForwardEvent event)throws Exception{
		logger.debug("Entering");

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		List<Object> list = (List<Object>) event.getData();
		BigDecimal excessAmount = (BigDecimal) list.get(0);
		CurrencyBox excessPaid = (CurrencyBox) list.get(1);
		Label excessBal =  (Label) list.get(2);
		
		BigDecimal paidExcessAmt = PennantApplicationUtil.unFormateAmount(excessPaid.getActualValue(), finFormatter);
		if(paidExcessAmt.compareTo(excessAmount) > 0){
			paidExcessAmt = excessAmount;
			excessPaid.setValue(PennantApplicationUtil.formateAmount(paidExcessAmt, finFormatter));
		}
		
		BigDecimal bal = excessAmount.subtract(paidExcessAmt);
		excessBal.setValue(PennantApplicationUtil.amountFormate(bal, finFormatter));
		
		// Setting Auto Allocation Process
		setAutoAllocationPayments();
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onAllocatePaidChange(ForwardEvent event)throws Exception{
		logger.debug("Entering");
		
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		List<Object> list = (List<Object>) event.getData();
		BigDecimal pastdueAmt = (BigDecimal) list.get(0);
		CurrencyBox allocatePaid = (CurrencyBox) list.get(1);
		
		BigDecimal paidAllocateAmt = PennantApplicationUtil.unFormateAmount(allocatePaid.getActualValue(), finFormatter);
		if(paidAllocateAmt.compareTo(pastdueAmt) > 0){
			paidAllocateAmt = pastdueAmt;
			allocatePaid.setValue(PennantApplicationUtil.formateAmount(paidAllocateAmt, finFormatter));
		}
		
		// Setting to Map for future usage on Rendering
		Map<String, BigDecimal> allocateTypePaidMap = null;
		if(paidAllocationMap != null){ 
			if(paidAllocationMap.containsKey(allocatePaid.getId())){
				paidAllocationMap.remove(allocatePaid.getId());
			}
			paidAllocationMap.put(allocatePaid.getId(), paidAllocateAmt);
			allocateTypePaidMap = new HashMap<>();
			List<String> keys = new ArrayList<>(paidAllocationMap.keySet());
			for (int i = 0; i < keys.size(); i++) {
				allocateTypePaidMap.put(keys.get(i).replace("AllocatePaid_", ""), paidAllocationMap.get(keys.get(i)));
			}
		}
		
		// Render total List box on Change of Amounts
		doFillAllocationDetail(null, allocateTypePaidMap, true);
		
		logger.debug("Leaving");
	}
	
	
	/**
	 * Method for action Event of Changing Allocated Paid Amount on Past due Schedule term
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onAllocateWaivedChange(ForwardEvent event)throws Exception{
		logger.debug("Entering");
		
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		List<Object> list = (List<Object>) event.getData();
		BigDecimal pastdueAmt = (BigDecimal) list.get(0);
		CurrencyBox allocateWaived = (CurrencyBox) list.get(1);
		
		BigDecimal waivedAllocateAmt = PennantApplicationUtil.unFormateAmount(allocateWaived.getActualValue(), finFormatter);
		if(waivedAllocateAmt.compareTo(pastdueAmt) > 0){
			waivedAllocateAmt = pastdueAmt;
			allocateWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocateAmt, finFormatter));
		}
		
		// Setting to Map for future usage on Rendering
		if(waivedAllocationMap != null){ 
			if(waivedAllocationMap.containsKey(allocateWaived.getId())){
				waivedAllocationMap.remove(allocateWaived.getId());
			}
			waivedAllocationMap.put(allocateWaived.getId(), waivedAllocateAmt);
		}
		
		// Setting to Map for future usage on Rendering
		Map<String, BigDecimal> allocateTypePaidMap = null;
		if(paidAllocationMap != null){ 
			allocateTypePaidMap = new HashMap<>();
			List<String> keys = new ArrayList<>(paidAllocationMap.keySet());
			for (int i = 0; i < keys.size(); i++) {
				allocateTypePaidMap.put(keys.get(i).replace("AllocatePaid_", ""), paidAllocationMap.get(keys.get(i)));
			}
		}

		// Render total List box on Change of Amounts
		doFillAllocationDetail(null, allocateTypePaidMap, true);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.receiptPurpose.isDisabled()) {
			this.receiptPurpose.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptPurpose(), Labels
					.getLabel("label_ReceiptDialog_ReceiptPurpose.value")));
		}
		
		String recptMode = getComboboxValue(receiptMode);
		if (!this.receiptMode.isDisabled()) {
			this.receiptMode.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptModes(), Labels
					.getLabel("label_ReceiptDialog_ReceiptMode.value")));
		}
		if (!this.excessAdjustTo.isDisabled()) {
			this.excessAdjustTo.setConstraint(new StaticListValidator(PennantStaticListUtil.getExcessAdjustmentTypes(), Labels
					.getLabel("label_ReceiptDialog_ExcessAdjustTo.value")));
		}
		if (!this.allocationMethod.isDisabled()) {
			this.allocationMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getAllocationMethods(), Labels
					.getLabel("label_ReceiptDialog_AllocationMethod.value")));
		}
		if (!this.effScheduleMethod.isDisabled()) {
			this.effScheduleMethod.setConstraint(new StaticListValidator(PennantStaticListUtil.getEarlyPayEffectOn(), Labels
					.getLabel("label_ReceiptDialog_EffecScheduleMethod.value")));
		}
		
		if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			
			if(!this.chequeAcNo.isReadonly()){
				this.chequeAcNo.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_ChequeAccountNo.value"), null, true));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CASH) ||
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			if(!this.fundingAccount.isReadonly()){
				this.fundingAccount.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true));
			}

			if(!this.receivedDate.isDisabled()){
				this.receivedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ReceivedDate.value"), true, 
						getFinanceMain().getFinStartDate(), DateUtility.getAppDate(), false));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD) ||
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			
			if(!this.favourNo.isReadonly()){
				String label = Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value");
				if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD)){
					label = Labels.getLabel("label_ReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(new PTStringValidator(label, PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
			
			if(!this.valueDate.isDisabled()){
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ValueDate.value"), true, 
						getFinanceMain().getFinStartDate(), DateUtility.getAppDate(), false));
			}
			
			if(!this.bankCode.isReadonly()){
				this.bankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_BankCode.value"), null, true, true));
			}
			
			if(!this.favourName.isReadonly()){
				this.favourName.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_favourName.value"),
						PennantRegularExpressions.REGEX_NAME, true));
			}
			
			if(!this.depositDate.isDisabled()){
				this.depositDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_DepositDate.value"), true, 
						getFinanceMain().getFinStartDate(), DateUtility.getAppDate(), false));
			}
			
			if(!this.depositNo.isReadonly()){
				this.depositNo.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_depositNo.value"),
						PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_NEFT) || 
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_RTGS) || 
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_IMPS)){
			
			if(!this.paymentRef.isReadonly()){
				this.paymentRef.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_paymentReference.value"),
						PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
			}

			if(!this.transactionRef.isReadonly()){
				this.transactionRef.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_tranReference.value"),
						PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
			}
		}
		
		if(!this.remarks.isReadonly()){
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_Remarks.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		
		this.receiptPurpose.setConstraint("");
		this.receiptMode.setConstraint("");
		this.excessAdjustTo.setConstraint("");
		this.allocationMethod.setConstraint("");
		this.effScheduleMethod.setConstraint("");
		
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

		logger.debug("Leaving");
	}

	/**
	 * Method for capturing Fields data from components to bean
	 * @return
	 */
	private FinReceiptHeader doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinReceiptHeader header = getReceiptHeader();
		header.setReceiptDate(DateUtility.getAppDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReference(this.finReference.getValue());
		try {
			header.setReceiptPurpose(getComboboxValue(receiptPurpose));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setReceiptMode(getComboboxValue(receiptMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setExcessAdjustTo(getComboboxValue(excessAdjustTo));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setAllocationType(getComboboxValue(allocationMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setReceiptAmount(PennantApplicationUtil.unFormateAmount(receiptAmount.getValidateValue(), finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setEffectSchdMethod(getComboboxValue(effScheduleMethod));
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
			this.receivedDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.remarks.getValue();
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
		return header;
	}

	/**
	 * Method for Processing Checklist Details when Check list Tab selected
	 */
	public void onSelectCheckListDetailsTab(ForwardEvent event) throws ParseException, InterruptedException,
	IllegalAccessException, InvocationTargetException {
		this.doWriteComponentsToBean();

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}

	}

	/**
	 * Method for Processing Agreement Details when Agreement list Tab selected
	 */
	public void onSelectAgreementDetailTab(ForwardEvent event) throws IllegalAccessException,
	InvocationTargetException, InterruptedException, ParseException {
		this.doWriteComponentsToBean();

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}


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

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		//Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail() throws InterruptedException, IllegalAccessException,
	InvocationTargetException {
		getFinanceDetail().setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);
		return getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 * 
	 * @throws Exception
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception {
		logger.debug("Entering");

		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(finMain.getFinReference());
		Date dateValueDate = DateUtility.getValueDate();

		DataSet dataSet = AEAmounts.createDataSet(finMain, eventCode, dateValueDate, dateValueDate);

		Date curBDay = DateUtility.getAppDate();
		amountCodes = AEAmounts.procAEAmounts(finMain, getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails(), profitDetail, curBDay);
		setAmountCodes(amountCodes);

		List<ReturnDataSet> returnSetEntries = null;

		Map<String, FeeRule> feeRuleMap = null;
		if (getFeeDetailDialogCtrl() != null) {
			feeRuleMap = getFeeDetailDialogCtrl().getFeeRuleDetailsMap();
		}

		if (!getFinanceType().isAllowRIAInvestment()) {
			returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, getAmountCodes(), "N", feeRuleMap,
					false, getFinanceType());
		} else {

			List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(null,
					dataSet.getFinReference());
			returnSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataSet, getAmountCodes(), "N",
					riaDetailList, feeRuleMap);
		}

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			if(!StringUtils.trimToEmpty(finMain.getFinCommitmentRef()).equals("")){
				Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				int format = CurrencyUtil.getFormat(commitment.getCmtCcy());

				if(commitment != null && commitment.isRevolving()){ 
					AECommitment aeCommitment = new AECommitment();
					aeCommitment.setCMTAMT(BigDecimal.ZERO); aeCommitment.setCHGAMT(BigDecimal.ZERO);
					aeCommitment.setDISBURSE(BigDecimal.ZERO);
					aeCommitment.setRPPRI(CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(),
							amountCodes.getRpPri()));

					List<ReturnDataSet> cmtEntries = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment,
							AccountEventConstants.ACCEVENT_CMTRPY, "N", null);
					getAccountingDetailDialogCtrl().doFillCmtAccounting(cmtEntries, format);
					getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList().addAll(cmtEntries); 
				} 
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab(boolean onLoadProcess) {
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
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAccountingDetailTab");

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

		if (!onLoadProcess) {

			//Get Finance Type Details, Transaction Entry By event & Commitment Repay Entries If have any
			financeDetail = getReceiptService().getAccountingDetail(financeDetail, eventCode);

			//Accounting Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", financeDetail);
			map.put("finHeaderList", getFinBasicDetails());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
				tab.setVisible(true);
			}
		}

		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

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
			nextTaskId = getNextTaskIds(taskId, financeMain);
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

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doProcess(FinReceiptData aRepayData, String tranType) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		aRepayData.getFinanceDetail().getFinScheduleData().setFinanceMain(afinanceMain);

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				try {
					if (!notesEntered) {
						MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					logger.error("Exception: ", e);
				}
			}

			auditHeader = getAuditHeader(aRepayData, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getFinanceDetailService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.equals(moduleDefiner, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
					if (isScheduleModify) {
						afinanceMain.setScheduleChange(true);
					} else {
						afinanceMain.setScheduleChange(false);
					}
				} else {
					FinReceiptData tRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tRepayData);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinReceiptData tRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tRepayData.getFinanceDetail().getFinScheduleData()
						.getFinanceMain(), finishedTasks);

			}

			FinReceiptData tRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tRepayData.getFinanceDetail().getFinScheduleData()
					.getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tRepayData);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aRepayData, tranType);
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
	 * @throws InterruptedException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinReceiptData aRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = aRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getReceiptService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getReceiptService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getReceiptService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
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

		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		} catch (PFFInterfaceException e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e.getErrorMessage());
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
		} catch (InvocationTargetException e) {
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

		//setRepaySchdList(sortRpySchdDetails(repaySchdList));
		refundMap = new LinkedHashMap<String, RepayScheduleDetail>();
		this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;

		BigDecimal totInsPaid = BigDecimal.ZERO;
		BigDecimal totSchdFeePaid = BigDecimal.ZERO;
		BigDecimal totSchdSuplRentPaid = BigDecimal.ZERO;
		BigDecimal totSchdIncrCostPaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		int finFormatter = getRepayMain().getLovDescFinFormatter();

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtility.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdBal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdBal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdPayNow(), finFormatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdPayNow(), finFormatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPenaltyPayNow(), finFormatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getMaxWaiver(), finFormatter));
				} else {
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRefundMax(), finFormatter));
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

				lc = new Listcell(PennantAppUtil.amountFormate(refundPft, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				//Fee Details
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdInsPayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totInsPaid = totInsPaid.add(repaySchd.getSchdInsPayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdFeePayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdSuplRentPayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdSuplRentPaid = totSchdSuplRentPaid.add(repaySchd.getSchdSuplRentPayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdIncrCostPayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdIncrCostPaid = totSchdIncrCostPaid.add(repaySchd.getSchdIncrCostPayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPrincipalSchdPayNow())
						.add(repaySchd.getSchdInsPayNow()).add(repaySchd.getSchdFeePayNow())
						.add(repaySchd.getSchdSuplRentPayNow()).add(repaySchd.getSchdIncrCostPayNow())
						.subtract(refundPft);
				lc = new Listcell(PennantAppUtil.amountFormate(netPay, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRepayBalance(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				this.listBoxPayment.appendChild(item);
				if (refundMap.containsKey(repaySchd.getSchDate().toString())) {
					refundMap.remove(repaySchd.getSchDate().toString());
				}
				refundMap.put(repaySchd.getSchDate().toString(), repaySchd);
			}

			//Summary Details
			Map<String, BigDecimal> paymentMap = new HashMap<String, BigDecimal>();
			paymentMap.put("totalRefund", totalRefund);
			paymentMap.put("totalCharge", totalCharge);
			paymentMap.put("totalPft", totalPft);
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

		//Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_summary.label"));
		lc.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(16);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

		BigDecimal totalSchAmount = BigDecimal.ZERO;

		fillListItem(Labels.getLabel("listcell_totalRefund.label"), paymentMap.get("totalRefund"));
		fillListItem(Labels.getLabel("listcell_totalPenalty.label"), paymentMap.get("totalCharge"));
		fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), paymentMap.get("totalPft"));
		fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"));

		totalSchAmount = totalSchAmount.add(paymentMap.get("totalPft"));
		totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));

		if (paymentMap.get("insPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("insPaid"));
			this.listheader_InsPayment.setVisible(true);
			fillListItem(Labels.getLabel("listcell_insFeePayNow.label"), paymentMap.get("insPaid"));
		}
		if (paymentMap.get("schdFeePaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdFeePaid"));
			this.listheader_SchdFee.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdFeePayNow.label"), paymentMap.get("schdFeePaid"));
		}
		if (paymentMap.get("schdSuplRentPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdSuplRentPaid"));
			this.listheader_SuplRent.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdSuplRentPayNow.label"), paymentMap.get("schdSuplRentPaid"));
		}
		if (paymentMap.get("schdIncrCostPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdIncrCostPaid"));
			this.listheader_IncrCost.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdIncrCostPayNow.label"), paymentMap.get("schdIncrCostPaid"));
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
		lc = new Listcell(PennantAppUtil.amountFormate(fieldValue, getRepayMain().getLovDescFinFormatter()));
		lc.setStyle("text-align:right;color:#f36800;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(12);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

	}

	/**
	 * when cursor leaves refund field in the list.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onRefundValueChanged(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) event.getData();
		Decimalbox refundProfit = (Decimalbox) list.get(0);
		String schDate = (String) list.get(1).toString();
		isLimitExceeded = false;
		int finFormatter = getReceiptData().getRepayMain().getLovDescFinFormatter();
		if (refundMap.containsKey(schDate)) {
			RepayScheduleDetail repaySchd = refundMap.get(schDate);

			if (repaySchd.isAllowRefund()) {
				if (repaySchd.getRefundMax().compareTo(
						PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter)) < 0) {
					MessageUtil.showErrorMessage(" Limit exceeded ... ");
					isLimitExceeded = true;
					return;
				}
				repaySchd.setRefundReq(PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter));
			} else if (repaySchd.isAllowWaiver()) {
				if (repaySchd.getMaxWaiver().compareTo(
						PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter)) < 0) {
					MessageUtil.showErrorMessage(" Limit exceeded ... ");
					isLimitExceeded = true;
					return;
				}
				repaySchd.setWaivedAmt(PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter));
			}
			refundMap.remove(schDate);
			refundMap.put(schDate, repaySchd);
		}

		doFillRepaySchedules(sortRpySchdDetails(new ArrayList<RepayScheduleDetail>(refundMap.values())));
		logger.debug("Leaving " + event.toString());
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
	private boolean isValidateData() throws InterruptedException, PFFInterfaceException {
		logger.debug("Entering");
		
		// Validate Field Details
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();

		Date curBussDate = DateUtility.getAppDate();
		if (getFinanceDetail().getFinScheduleData().getFinanceMain() != null
				&& curBussDate.compareTo(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
			MessageUtil.showErrorMessage(Labels.getLabel("label_ReceiptDialog_Valid_Date"));
			return false;
		}
		
		// Entered Receipt Amount Match case test with allocations
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal totReceiptAmount = getTotalReceiptAmount();
		if(totReceiptAmount.compareTo(BigDecimal.ZERO) == 0){
			MessageUtil.showErrorMessage(Labels.getLabel("label_ReceiptDialog_Valid_NoReceiptAmount"));
			return false;
		}
		
		BigDecimal totalPaid = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalPaid") != null){
			Label paid = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalPaid");
			totalPaid = PennantApplicationUtil.unFormateAmount(new BigDecimal(paid.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalWaived") != null){
			Label waived = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalWaived");
			totalWaived = PennantApplicationUtil.unFormateAmount(new BigDecimal(waived.getValue().replaceAll(",", "")), formatter);
		}
		
		BigDecimal remBal = totReceiptAmount.subtract(totalPaid).subtract(totalWaived);
		if(remBal.compareTo(BigDecimal.ZERO) < 0){
			MessageUtil.showErrorMessage(Labels.getLabel("label_ReceiptDialog_Valid_InsufficientAmount"));
			return false;
		}
		
		String tempReceiptPurpose = getComboboxValue(this.receiptPurpose);
		if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) && remBal.compareTo(BigDecimal.ZERO) <= 0){
			MessageUtil.showErrorMessage(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
			return false;
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	private void doLoadTabsData() throws InterruptedException {
		logger.debug("Entering ");

		boolean createTab = false;
		if (tabsIndexCenter.getFellowIfAny("dashboardTab") == null) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Dashboard");
			tab.setId("dashboardTab");
			tabsIndexCenter.appendChild(tab);

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

		graphDivTabDiv = new Div();
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

		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();
		ChartUtil chartUtil = new ChartUtil();

		//For Finance Vs Amounts Chart z
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount(finScheduleData, formatter);

		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts", "Loan Amount ="
				+ PennantAppUtil.amountFormate(PennantAppUtil.unFormateAmount(financeAmount, formatter), formatter),
				"", "");
		aDashboardConfiguration = new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks("pieRadius='90' startingAngle='310'"
				+ "formatNumberScale='0'enableRotation='1'  forceDecimals='1'  decimals='" + formatter + "'");
		String chartStrXML = chartsConfig.getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("Pie3D.swf");
		chartDetail.setChartHeight("160");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");

		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));

		//For Repayments Chart 
		chartsConfig = new ChartsConfig("Payments", "", "", "");
		chartsConfig.setSetElements(getReportDataForRepayments(finScheduleData, formatter));
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig
		.setRemarks("labelDisplay='ROTATE' formatNumberScale='0'"
				+ "rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'  decimals='"
				+ formatter + "'");
		chartStrXML = chartsConfig.getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("MSLine.swf");
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");

		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));
		logger.debug("Leaving ");
	}

	/**
	 * Method to get report data from repayments table.
	 * 
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForRepayments(FinScheduleData scheduleData, int formatter) {
		logger.debug("Entering ");

		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();
		int format=CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());
		ChartSetElement chartSetElement;
		if (listScheduleDetail != null) {
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Payment Amount", PennantAppUtil.formateAmount(curSchd.getRepayAmount(),
									format).setScale(formatter,
											RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Principal", PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(),
									format).setScale(formatter,
											RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Interest", PennantAppUtil.formateAmount(curSchd.getProfitSchd(),
									format).setScale(formatter,
											RoundingMode.HALF_UP));
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

		BigDecimal downPayment= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP); 
		BigDecimal schedulePrincipal= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		int format=CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());

		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();

		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;
			financeAmount = BigDecimal.ZERO;
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				financeAmount = financeAmount.add(PennantAppUtil.formateAmount(curSchd.getDisbAmount(), format));
				downPayment = downPayment
						.add(PennantAppUtil.formateAmount(curSchd.getDownPaymentAmount(), format));
				capitalized = capitalized.add(PennantAppUtil.formateAmount(curSchd.getCpzAmount(), format));

				scheduleProfit = scheduleProfit
						.add(PennantAppUtil.formateAmount(curSchd.getProfitSchd(), format));
				schedulePrincipal = schedulePrincipal.add(PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(),
						format));

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
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail, repayData.getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeDetail.getFinScheduleData().getFinanceMain());
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
	}

	/**
	 * When the print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		String reportName = "InternalMemorandum";
		EarlySettlementReportData earlySettlement = new EarlySettlementReportData();

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		boolean isRetail = false;
		if (getFinanceType() != null) {
			String division = getFinanceType().getFinDivision().trim();
			if (StringUtils.equals(division, FinanceConstants.FIN_DIVISION_RETAIL)) {
				reportName = FinanceConstants.FIN_DIVISION_RETAIL + "_InternalMemorandum.docx";
				isRetail = true;
			}
			earlySettlement.setDeptFrom(getFinanceType().getLovDescFinDivisionName());
		}

		if (financeMain != null) {
			earlySettlement.setAppDate(DateUtility.getAppDate(DateFormat.SHORT_DATE));
			earlySettlement.setFinReference(financeMain.getFinReference());
			earlySettlement.setFinType(financeMain.getFinType());
			earlySettlement.setFinTypeDesc(financeMain.getLovDescFinTypeName());
			earlySettlement.setCustCIF("CIF " + financeMain.getLovDescCustCIF());
			earlySettlement.setCustShrtName(financeMain.getLovDescCustShrtName());
			earlySettlement.setFinStartDate(DateUtility.formatToLongDate(financeMain.getFinStartDate()));
			earlySettlement.setEarlySettlementDate(DateUtility.formatToLongDate(DateUtility.getAppDate()));
		}

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		FinanceProfitDetail profitDetail = getReceiptService().getPftDetailForEarlyStlReport(
				financeMain.getFinReference());
		if (profitDetail != null) {
			BigDecimal financeAmount = financeMain.getFinAmount().add(financeMain.getFeeChargeAmt() != null ? financeMain.getFeeChargeAmt() : BigDecimal.ZERO)
					.subtract(financeMain.getDownPayment()).add(financeMain.getInsuranceAmt() != null ? financeMain.getInsuranceAmt() : BigDecimal.ZERO);
			earlySettlement.setTotalPaidAmount(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(financeAmount, formatter));
			earlySettlement.setTotalTerms(String.valueOf(profitDetail.getNOInst()));
			earlySettlement.setTotalPaidTerms(String.valueOf(profitDetail.getNOPaidInst()));
			earlySettlement
			.setTotalUnpaidTerms(String.valueOf(profitDetail.getNOInst() - profitDetail.getNOPaidInst()));
			earlySettlement.setOutStandingTotal(financeMain.getFinCcy()
					+ " "
					+ PennantApplicationUtil.amountFormate(
							profitDetail.getTotalPriBal().add(profitDetail.getTotalPftBal()), formatter));
			earlySettlement.setOutStandingPft(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(profitDetail.getTotalPftBal(), formatter));

			BigDecimal insAmount = BigDecimal.ZERO;
			FeeRule feeRule = getFinanceDetailService().getInsFee(financeMain.getFinReference());
			if (feeRule != null && feeRule.getFeeAmount() != null) {
				insAmount = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount())
						.subtract(feeRule.getPaidAmount());
			}
			earlySettlement.setInsuranceFee(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(insAmount, formatter));

			int remMonths = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), receiptData.getRepayMain()
					.getRefundCalStartDate() == null ? financeMain.getMaturityDate() : receiptData.getRepayMain()
							.getRefundCalStartDate(), true);
			int totalMonths = DateUtility.getMonthsBetween(financeMain.getMaturityDate(),
					financeMain.getFinStartDate(), false);

			earlySettlement.setPeriodCoverage(String.valueOf(totalMonths - remMonths));
		}

		//Word Format
		if (isRetail) {
			try {

				TemplateEngine engine = new TemplateEngine(reportName);
				reportName = earlySettlement.getFinReference() + "_" + "Memorandum.docx";
				engine.setTemplate("");
				//engine.loadTemplateWithFontSize(11);
				engine.mergeFields(earlySettlement);
				engine.showDocument(this.window_ReceiptDialog, reportName, SaveFormat.DOCX);
				engine.close();
				engine = null;

			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		} else {
			// PDF Format
			ReportGenerationUtil.generateReport(reportName, earlySettlement, new ArrayList<Object>(), true, 1,
					getUserWorkspace().getLoggedInUser().getFullName(), this.window_ReceiptDialog);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method which returns FinanceMain object
	 * 
	 */
	public FinanceMain getFinanceMain() {
		if (getFinanceDetail() != null) {
			return getFinanceDetail().getFinScheduleData().getFinanceMain();
		}
		return null;
	}

	/**
	 * Method which returns customer document title
	 * 
	 */
	public String getCustomerIDNumber(String docTypeCode) {
		if (getFinanceDetail() != null) {
			for (CustomerDocument custDocs : getFinanceDetail().getCustomerDetails().getCustomerDocumentsList()) {
				if (StringUtils.equals(custDocs.getCustDocCategory(), docTypeCode)) {
					return custDocs.getCustDocTitle();
				}
			}
		}
		return null;
	}

	/**
	 * When record is rejected . <br>
	 * 
	 */
	public void doReject() throws InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMain", getFinanceDetail().getFinScheduleData().getFinanceMain());
		map.put("financeMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceReject.zul",
					window_ReceiptDialog, map);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to Update Reject Finance Details
	 */
	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isRefundAmtValidated() {
		return refundAmtValidated;
	}

	public void setRefundAmtValidated(boolean refundAmtValidated) {
		this.refundAmtValidated = refundAmtValidated;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public RepayMain getRepayMain() {
		return repayMain;
	}

	public void setRepayMain(RepayMain repayMain) {
		this.repayMain = repayMain;
	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return overdueChargeRecoveryService;
	}
	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	public AccountsService getAccountsService() {
		return accountsService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
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

	public ProvisionService getProvisionService() {
		return provisionService;
	}
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
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

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}
	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
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

	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}
	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}
	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}
	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public FeeDetailDialogCtrl getFeeDetailDialogCtrl() {
		return feeDetailDialogCtrl;
	}
	public void setFeeDetailDialogCtrl(FeeDetailDialogCtrl feeDetailDialogCtrl) {
		this.feeDetailDialogCtrl = feeDetailDialogCtrl;
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

	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
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

	public FinanceType getFinanceType() {
		return financeType;
	}
	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}
	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public FinReceiptData getReceiptData() {
		return receiptData;
	}
	public void setReceiptData(FinReceiptData receiptData) {
		this.receiptData = receiptData;
	}

	public FinReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}

	public void setReceiptHeader(FinReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public List<FinExcessAmount> getExcessList() {
		return excessList;
	}

	public void setExcessList(List<FinExcessAmount> excessList) {
		this.excessList = excessList;
	}

}