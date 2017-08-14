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
import java.util.Arrays;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
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
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.BounceReason;
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
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinFeeDetail;
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
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.component.Uppercasebox;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.util.TemplateEngine;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.StageAccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul
 */
public class ReceiptDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long								serialVersionUID					= 966281186831332116L;
	private static final Logger								logger								= Logger.getLogger(ReceiptDialogCtrl.class);

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
	protected Decimalbox									custPaid;
	protected Row											row_RealizationDate;
	protected Datebox										realizationDate;
	protected Label				 							label_ReceiptDialog_ReceiptModeStatus;
	protected Hbox											hbox_ReceiptModeStatus;
	protected Combobox										receiptModeStatus;
	protected Row											row_BounceReason;
	protected ExtendedCombobox								bounceCode;
	protected CurrencyBox									bounceCharge;
	protected Row											row_BounceRemarks;
	protected Textbox										bounceRemarks;
	protected Datebox										bounceDate;
	protected Row											row_CancelReason;
	protected ExtendedCombobox								cancelReason;
	
	protected Groupbox										gb_ReceiptDetails;
	protected Caption										caption_receiptDetail;
	protected Label											label_ReceiptDialog_favourNo;
	protected Uppercasebox									favourNo;
	protected Datebox										valueDate;
	protected ExtendedCombobox								bankCode;
	protected Textbox										favourName;
	protected Datebox										depositDate;
	protected Uppercasebox									depositNo;
	protected Uppercasebox									paymentRef;
	protected Uppercasebox									transactionRef;
	protected AccountSelectionBox							chequeAcNo;
	protected ExtendedCombobox								fundingAccount;
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
	protected Listheader									listheader_Tds;
	protected Listheader									listheader_LatePft;
	protected Listheader									listheader_Refund;
	protected Listheader									listheader_Penalty;
	protected Listheader									listheader_InsPayment;
	protected Listheader									listheader_SchdFee;
	protected Listheader									listheader_SuplRent;
	protected Listheader									listheader_IncrCost;
	
	// Overdraft Details Headers
	protected Listheader 									listheader_LimitChange;
	protected Listheader 									listheader_AvailableLimit;
	protected Listheader 									listheader_ODLimit;

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
	protected Listheader									listheader_ReceiptSchedule_SchFee;

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
	private transient CommitmentService						commitmentService;
	private transient ReceiptCalculator						receiptCalculator;
	private transient FinanceReferenceDetailService			financeReferenceDetailService;
	private transient AccrualService 						accrualService;

	private transient AccountingDetailDialogCtrl			accountingDetailDialogCtrl			= null;
	private transient DocumentDetailDialogCtrl				documentDetailDialogCtrl			= null;
	private transient AgreementDetailDialogCtrl				agreementDetailDialogCtrl			= null;
	private transient CustomerDialogCtrl					customerDialogCtrl					= null;
	private transient StageAccountingDetailDialogCtrl		stageAccountingDetailDialogCtrl		= null;
	private transient FinanceCheckListReferenceDialogCtrl	financeCheckListReferenceDialogCtrl	= null;

	private FinReceiptData									receiptData							= null;
	private FinReceiptHeader								receiptHeader						= null;
	private List<FinExcessAmount>							excessList							= null;
	private List<FinExcessAmountReserve>					excessReserveList					= null;
	private List<ManualAdvise>								payableList							= null;
	private List<ManualAdviseReserve>						payableReserveList					= null;
	private FinanceDetail									financeDetail;
	private FinanceType										financeType;
	private RepayMain										repayMain							= null;

	private LinkedHashMap<String, RepayScheduleDetail>		refundMap;
	private MailUtil										mailUtil;

	private Map<String, BigDecimal> waivedAllocationMap = new HashMap<>();
	private Map<String, BigDecimal> paidAllocationMap = new HashMap<>();
	private String recordType = "";
	private int version = 0;
	private FinanceMain befImage;
	
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
		isReceiptsProcess = true;

		try {
			if (arguments.containsKey("repayData")) {
				
				setReceiptData((FinReceiptData) arguments.get("repayData"));
				financeDetail = getReceiptData().getFinanceDetail();
				financeType = financeDetail.getFinScheduleData().getFinanceType();
				setFinanceDetail(financeDetail);
				receiptHeader = getReceiptData().getReceiptHeader();
				
				// Excess Amounts
				setExcessList(receiptHeader.getExcessAmounts());
				setExcessReserveList(receiptHeader.getExcessReserves());
				
				// Payable Amounts
				setPayableList(receiptHeader.getPayableAdvises());
				setPayableReserveList(receiptHeader.getPayableReserves());
				
				recordType = financeDetail.getFinScheduleData().getFinanceMain().getRecordType();
				version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();

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
			
			Date valueDate = DateUtility.getAppDate();
			if(receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()){
				for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
					if(StringUtils.equals(receiptHeader.getReceiptDetails().get(i).getPaymentType(), receiptHeader.getReceiptMode()) &&
							!StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_EXCESS)){
						valueDate = receiptHeader.getReceiptDetails().get(i).getReceivedDate();
					}
				}
			}

			// READ OVERHANDED parameters !
			if (!setSummaryData(false, valueDate)) {

				// set Read only mode accordingly if the object is new or not.
				if (StringUtils.isBlank(financeMain.getRecordType())) {
					doEdit();
					this.btnReceipt.setDisabled(true);
					this.btnChangeReceipt.setDisabled(true);
				}

				//Reset Finance Repay Header Details
				doWriteBeanToComponents();
				if (StringUtils.isNotBlank(financeMain.getRecordType())) {
					this.btnNotes.setVisible(true);
					doReadonly(false);
				}

				this.borderlayout_Receipt.setHeight(getBorderLayoutHeight());
				this.listBoxPayment.setHeight(getListBoxHeight(6));
				this.listBoxSchedule.setHeight(getListBoxHeight(6));
				this.receiptDetailsTab.setSelected(true);

				// Setting tile Name based on Service Action
				this.windowTitle.setValue(Labels.getLabel(moduleDefiner+"_Window.Title"));
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		this.receipt_paidByCustomer.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.receiptAmount.setProperties(true , formatter);
		this.realizationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setMandatoryStyle(true);
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });
		
		this.bounceCode.setModuleName("BounceReason");
		this.bounceCode.setMandatoryStyle(true);
		this.bounceCode.setValueColumn("BounceID");
		this.bounceCode.setDescColumn("BounceCode");
		this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceID" , "BounceCode", "Category", "Reason" });
		
		this.bounceCharge.setProperties(false , formatter);
		this.bounceRemarks.setMaxlength(100);
		this.bounceDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
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

		// Allocation Details
		this.allocation_finType.setMaxlength(8);
		this.allocation_finReference.setMaxlength(20);
		this.allocation_finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.allocation_finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
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
		readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		
		// Bounce/Realization/Cancel Reason Fields
		readOnlyComponent(isReadOnly("ReceiptDialog_realizationDate"), this.realizationDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceCode"), this.bounceCode);
		readOnlyComponent(true, this.bounceCharge);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceRemarks"), this.bounceRemarks);
		readOnlyComponent(isReadOnly("ReceiptDialog_bounceDate"), this.bounceDate);
		readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"), this.cancelReason);
		readOnlyComponent(isReadOnly("ReceiptDialog_receiptModeStatus"), this.receiptModeStatus);
		
		//Receipt Details
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
		readOnlyComponent(isReadOnly("ReceiptDialog_cashReceivedDate"), this.receivedDate);

		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadonly(boolean isUserAction) {
		logger.debug("Entering");
		
		// Receipt Details
		readOnlyComponent(true, this.receiptPurpose);
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.receiptMode);
		readOnlyComponent(true, this.receiptAmount);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);
		
		//Receipt Details
		if(isUserAction){
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
			readOnlyComponent(true, this.receivedDate);
			readOnlyComponent(true, this.remarks);
			
			// Bounce/Realization/Cancel Reason Fields
			readOnlyComponent(true, this.realizationDate);
			readOnlyComponent(true, this.bounceCode);
			readOnlyComponent(true, this.bounceCharge);
			readOnlyComponent(true, this.bounceRemarks);
			readOnlyComponent(true, this.bounceDate);
			readOnlyComponent(true, this.cancelReason);
			readOnlyComponent(true, this.receiptModeStatus);
			
		}else{
			
			// Bounce/Realization/Cancel Reason Fields
			readOnlyComponent(isReadOnly("ReceiptDialog_realizationDate"), this.realizationDate);
			readOnlyComponent(isReadOnly("ReceiptDialog_bounceCode"), this.bounceCode);
			readOnlyComponent(true, this.bounceCharge);
			readOnlyComponent(isReadOnly("ReceiptDialog_bounceRemarks"), this.bounceRemarks);
			readOnlyComponent(isReadOnly("ReceiptDialog_bounceDate"), this.bounceDate);
			readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"), this.cancelReason);
			readOnlyComponent(isReadOnly("ReceiptDialog_receiptModeStatus"), this.receiptModeStatus);
			
			//Receipt Details
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
			readOnlyComponent(true, this.receivedDate);
			readOnlyComponent(isReadOnly("ReceiptDialog_remarks"), this.remarks);
		}

		// Excess amount set to readonly
		if(listBoxExcess.getFellowIfAny("ExcessAmount_E") != null){
			CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_E");
			excessBox.setReadonly(true);
		}

		// EMI in Advance Amount
		if(listBoxExcess.getFellowIfAny("ExcessAmount_A") != null){
			CurrencyBox emiInAdvBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_A");
			emiInAdvBox.setReadonly(true);
		}
		
		// Payable Amounts
		List<Listitem> payableItems = this.listBoxExcess.getItems();
		for (int i = 0; i < payableItems.size(); i++) {
			Listitem item = payableItems.get(i);
			if(item.getId().contains("Payable")){
				CurrencyBox payableAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny(item.getId().replaceAll("Item", "Amount"));
				payableAmount.setReadonly(true);
			}
		}
		
		// Pastdue Allocations
		List<Listitem> pastdueItems = this.listBoxPastdues.getItems();
		for (int i = 0; i < pastdueItems.size(); i++) {
			Listitem item = pastdueItems.get(i);
			if(item.getId().contains("Allocate")){
				CurrencyBox paidBox = (CurrencyBox) this.listBoxPastdues.getFellowIfAny(item.getId().replaceAll("Item", "Paid"));
				paidBox.setReadonly(true);
				CurrencyBox waivedBox = (CurrencyBox) this.listBoxPastdues.getFellowIfAny(item.getId().replaceAll("Item", "Waived"));
				waivedBox.setReadonly(true);
			}
		}
		
		// Manual Advise Allocations
		List<Listitem> advises = this.listBoxManualAdvises.getItems();
		for (int i = 0; i < advises.size(); i++) {
			Listitem item = advises.get(i);
			if(item.getId().contains("Allocate")){
				CurrencyBox paidBox = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny(item.getId().replaceAll("Item", "Paid"));
				paidBox.setReadonly(true);
				CurrencyBox waivedBox = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny(item.getId().replaceAll("Item", "AdvWaived"));
				waivedBox.setReadonly(true);
			}
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
	private boolean setSummaryData(boolean isChgReceipt, Date valueDate) throws InterruptedException, IllegalAccessException,
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
			//aFinScheduleData.getFinFeeDetailList().addAll(this.receiptService.getFinFeeDetailById(finReference.getValue(), false, "_TView", eventCode));
			getFinanceDetail().setFinScheduleData(aFinScheduleData);
		} else {
			Cloner cloner = new Cloner();
			aFinScheduleData = cloner.deepClone(getFinanceDetail().getFinScheduleData());
		}

		receiptData.setAccruedTillLBD(aFinScheduleData.getFinanceMain().getLovDescAccruedTillLBD());
		receiptData.setFinanceDetail(getFinanceDetail());
		
		BigDecimal totReceiptAmount = getTotalReceiptAmount(true);
		if (isChgReceipt) {
			receiptData.setTotReceiptAmount(totReceiptAmount);
		}else{
			receiptData.setTotReceiptAmount(getReceiptHeader().getReceiptAmount().subtract(getReceiptHeader().getTotFeeAmount()));
		}
		
		
		receiptData = getReceiptCalculator().initiateReceipt(receiptData, aFinScheduleData, valueDate, getReceiptHeader().getReceiptPurpose(), false);
		if(StringUtils.isEmpty(financeMain.getRcdMaintainSts())){
			setReceiptData(receiptData);
		}else{
			getReceiptData().setRepayMain(receiptData.getRepayMain());
			getReceiptData().setAllocationMap(receiptData.getAllocationMap());
			getReceiptData().setAllocationDescMap(receiptData.getAllocationDescMap());
		}
		receiptData.getRepayMain().setLovDescFinFormatter(finformatter);
		setRepayMain(receiptData.getRepayMain());

		this.finType.setValue(getRepayMain().getFinType());
		this.finReference.setValue(getRepayMain().getFinReference());
		this.finCcy.setValue(getRepayMain().getFinCcy());
		fillComboBox(this.profitDayBasis, getRepayMain().getProfitDaysBais(), PennantStaticListUtil.getProfitDaysBasis(), "");
		this.custID.setValue(getRepayMain().getCustID());
		String custShrtname = "";
		if(customer != null){
			this.lovDescCustCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
			custShrtname = customer.getCustShrtName();
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

		//Total Overdue Penalty Amount
		this.pendingODC.setValue(PennantAppUtil.formateAmount(receiptData.getPendingODC(), finformatter));

		// Receipt Basic Details
		this.receipt_finType.setValue(getRepayMain().getFinType());
		this.receipt_finReference.setValue(getRepayMain().getFinReference());
		this.receipt_finCcy.setValue(getRepayMain().getFinCcy());
		this.receipt_finBranch.setValue(getRepayMain().getFinBranch());
		if(customer != null){
			this.receipt_CustCIF.setValue(customer.getCustCIF());
			if(StringUtils.isNotEmpty(custShrtname)){
				this.receipt_CustCIF.setValue(customer.getCustCIF()+"-"+custShrtname);
			}
		}
		this.receipt_paidByCustomer.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		// Allocation Basic Details
		this.allocation_finType.setValue(getRepayMain().getFinType());
		this.allocation_finReference.setValue(getRepayMain().getFinReference());
		this.allocation_finCcy.setValue(getRepayMain().getFinCcy());
		this.allocation_finBranch.setValue(getRepayMain().getFinBranch());
		if(customer != null){
			this.allocation_CustCIF.setValue(customer.getCustCIF());
			if(StringUtils.isNotEmpty(custShrtname)){
				this.allocation_CustCIF.setValue(customer.getCustCIF()+"-"+custShrtname);
			}
		}
		this.allocation_paidByCustomer.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		// Repayment Schedule Basic Details
		this.payment_finType.setValue(getRepayMain().getFinType());
		this.payment_finReference.setValue(getRepayMain().getFinReference());
		this.payment_finCcy.setValue(getRepayMain().getFinCcy());
		this.payment_finBranch.setValue(getRepayMain().getFinBranch());
		if(customer != null){
			this.payment_CustCIF.setValue(customer.getCustCIF());
			if(StringUtils.isNotEmpty(custShrtname)){
				this.payment_CustCIF.setValue(customer.getCustCIF()+"-"+custShrtname);
			}
		}
		this.payment_paidByCustomer.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

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
	public void onClick$btnCalcReceipts(Event event) throws InterruptedException, WrongValueException, InterfaceException {
		logger.debug("Entering" + event.toString());

		// Validate Required Fields Data
		if (!isValidateData(true)) {
			return;
		}

		String recptPurpose = getComboboxValue(this.receiptPurpose);
		if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) ||
				StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			recalEarlyPaySchd(getReceiptData(), recptPurpose);
		} else {
			FinReceiptData receiptData = null;
			receiptData = calculateRepayments(getFinanceDetail().getFinScheduleData());
			if(receiptData != null){
				setRepayDetailData(receiptData);
			}
			setReceiptData(receiptData);
		}
		
		// Do readonly to all components
		doReadonly(true);
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Processing Calculation button visible , if amount modified
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onFulfill$receiptAmount(Event event) throws InterruptedException {
		logger.debug("Entering");
		
		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));
		
		waivedAllocationMap = new HashMap<>();
		paidAllocationMap = new HashMap<>();
		percentageFees(false); 
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Processing Calculation button visible , if Value Date modified
	 * @param event
	 */
	public void onChange$receivedDate(Event event) {
		logger.debug("Entering");
		this.btnChangeReceipt.setDisabled(true);
		this.btnReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));
		
		waivedAllocationMap = new HashMap<>();
		paidAllocationMap = new HashMap<>();
		
		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		fillComboBox(this.allocationMethod, RepayConstants.ALLOCATIONTYPE_AUTO, PennantStaticListUtil.getAllocationMethods(), "");
		
		// Check Auto Allocation Process existence
		setAutoAllocationPayments(true);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Resetting amounts and allocations based on changing Fee amount
	 */
	public void onFeeAmountChange(){
		logger.debug("Entering");
		
		this.btnReceipt.setDisabled(true);
		this.btnChangeReceipt.setDisabled(true);
		this.btnCalcReceipts.setDisabled(!getUserWorkspace().isAllowed("button_ReceiptDialog_btnCalcReceipts"));
		this.effectiveScheduleTab.setVisible(false);

		if (tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null) {
			tabpanelsBoxIndexCenter.removeChild(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"));
		}

		if (tabsIndexCenter.getFellowIfAny("dashboardTab") != null) {
			tabsIndexCenter.removeChild(tabsIndexCenter.getFellowIfAny("dashboardTab"));
		}
		
		doEdit();
		String rcptPurpose = getComboboxValue(this.receiptPurpose);
		checkByReceiptPurpose(rcptPurpose, false);
		checkByReceiptMode(getComboboxValue(this.receiptMode), false);
		
		// Excess amount set to readonly
		if(listBoxExcess.getFellowIfAny("ExcessAmount_E") != null){
			CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_E");
			excessBox.setReadonly(isReadOnly("ReceiptDialog_ExcessAmount"));
		}

		// EMI in Advance Amount
		if(listBoxExcess.getFellowIfAny("ExcessAmount_A") != null){
			CurrencyBox emiInAdvBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_A");
			emiInAdvBox.setReadonly(isReadOnly("ReceiptDialog_ExcessAmount"));
		}
		
		// Payable Amounts
		List<Listitem> payableItems = this.listBoxExcess.getItems();
		for (int i = 0; i < payableItems.size(); i++) {
			Listitem item = payableItems.get(i);
			if(item.getId().contains("Payable")){
				CurrencyBox payableAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny(item.getId().replaceAll("Item", "Amount"));
				payableAmount.setReadonly(isReadOnly("ReceiptDialog_PayableAmount"));
			}
		}

		// Pastdue Allocations
		String allocateMthd = getComboboxValue(this.allocationMethod);
		List<Listitem> pastdueItems = this.listBoxPastdues.getItems();
		boolean isAllocateAllowed = false;
		for (int i = 0; i < pastdueItems.size(); i++) {
			Listitem item = pastdueItems.get(i);
			isAllocateAllowed = true;
			if(item.getId().contains("Allocate")){
				if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
					CurrencyBox paidBox = (CurrencyBox) this.listBoxPastdues.getFellowIfAny(item.getId().replaceAll("Item", "Paid"));
					paidBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}
				CurrencyBox waivedBox = (CurrencyBox) this.listBoxPastdues.getFellowIfAny(item.getId().replaceAll("Item", "Waived"));
				if(StringUtils.equals(rcptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					waivedBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}else{
					
					String allocationType = waivedBox.getId().replace("", "");
					if(allocationType.contains("_")){
						allocationType = allocationType.substring(0, allocationType.indexOf("_"));
					}
					
					if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_ODC)){
						waivedBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
					}else{
						waivedBox.setReadonly(true);
					}
				}
			}
		}

		// Manual Advise Allocations
		List<Listitem> advises = this.listBoxManualAdvises.getItems();
		for (int i = 0; i < advises.size(); i++) {
			Listitem item = advises.get(i);
			isAllocateAllowed = true;
			if(item.getId().contains("Allocate")){
				if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
					CurrencyBox paidBox = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny(item.getId().replaceAll("Item", "Paid"));
					paidBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}
				CurrencyBox waivedBox = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny(item.getId().replaceAll("Item", "AdvWaived"));
				waivedBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
			}
		}
		
		// Checking Allocation method read only case
		if(!isAllocateAllowed){
			readOnlyComponent(true, this.allocationMethod);
			this.allocationMethod.setSelectedIndex(0);
		}

		if(this.excessAdjustTo.getSelectedIndex() > 0){
			readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		}else{
			readOnlyComponent(true, this.excessAdjustTo);
		}
		
		waivedAllocationMap = new HashMap<>();
		paidAllocationMap = new HashMap<>();
		
		// Check Auto Allocation Process existence
		setAutoAllocationPayments(true);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Resetting totals based on Modifications in Screen
	 */
	public void resetFeeAmounts(Boolean isFeeConsiderOnAmount){
		logger.debug("Entering");
		
		BigDecimal feeToBePaid = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		if(getFinFeeDetailListCtrl() != null){
			feeToBePaid = getFinFeeDetailListCtrl().getFeePaidAmount(formatter);
		}
		
		BigDecimal totReceiptAmount = getTotalReceiptAmount(isFeeConsiderOnAmount);
		BigDecimal totReceivable = BigDecimal.ZERO;
		if(totReceiptAmount.compareTo(BigDecimal.ZERO) > 0){
			totReceivable = totReceiptAmount.add(feeToBePaid);
		}
		this.receipt_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(totReceivable, formatter));
		this.allocation_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(totReceivable, formatter));
		this.payment_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(totReceivable, formatter));
		
		BigDecimal totalDue = BigDecimal.ZERO;
		BigDecimal totalPaid = BigDecimal.ZERO;
		// Past due Details
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalDue") != null){
			Label due = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalDue");
			totalDue = PennantApplicationUtil.unFormateAmount(new BigDecimal(due.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalPaid") != null){
			Label paid = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalPaid");
			totalPaid = PennantApplicationUtil.unFormateAmount(new BigDecimal(paid.getValue().replaceAll(",", "")), formatter);
		}
		
		BigDecimal totalAdvDue = BigDecimal.ZERO;
		BigDecimal totalAdvPaid = BigDecimal.ZERO;
		// Manual Advises
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalDue") != null){
			Label due = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalDue");
			totalAdvDue = PennantApplicationUtil.unFormateAmount(new BigDecimal(due.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalPaid") != null){
			Label paid = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalPaid");
			totalAdvPaid = PennantApplicationUtil.unFormateAmount(new BigDecimal(paid.getValue().replaceAll(",", "")), formatter);
		}
		
		// User entered Receipt amounts and paid on manual Allocation validation
		BigDecimal remBal = totReceiptAmount.subtract(totalPaid).subtract(totalAdvPaid); 
		if(remBal.compareTo(BigDecimal.ZERO) < 0){
			remBal = BigDecimal.ZERO;
		}
		
		this.custPaid.setValue(PennantApplicationUtil.formateAmount(totalDue.add(totalAdvDue).add(feeToBePaid), formatter));
		resetCustpaid(getComboboxValue(this.receiptPurpose), totalDue.add(totalAdvDue).add(feeToBePaid), formatter);
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBal, formatter));
		
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
	
	public void onFulfill$bounceCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bounceCode.getObject();

		if (dataObject instanceof String) {
			this.bounceCode.setValue(dataObject.toString());
		} else {
			BounceReason bounceReason = (BounceReason) dataObject;
			if (bounceReason != null) {
				HashMap<String, Object> executeMap = bounceReason.getDeclaredFieldValues();

				if (this.receiptHeader != null) {
					if (this.receiptHeader.getReceiptDetails() != null
							&& !this.receiptHeader.getReceiptDetails().isEmpty()) {
						for (FinReceiptDetail finReceiptDetail : this.receiptHeader.getReceiptDetails()) {
							if (StringUtils.equals(this.receiptHeader.getReceiptMode(),
									finReceiptDetail.getPaymentType())) {
								finReceiptDetail.getDeclaredFieldValues(executeMap);
								break;
							}
						}
					}
				}

				Rule rule = getRuleService().getRuleById(bounceReason.getRuleID(), "");
				BigDecimal bounceAmt = BigDecimal.ZERO;
				int formatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());
				if (rule != null) {
					bounceAmt = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executeMap,
							getReceiptHeader().getFinCcy(), RuleReturnType.DECIMAL);
					// unFormating BounceAmt
					bounceAmt = PennantApplicationUtil.unFormateAmount(bounceAmt, formatter);
				}
				this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(bounceAmt, formatter));
			}
		}
	}
	
	/**
	 * Method for Processing Captured details based on Receipt Purpose
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onChange$receiptPurpose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		String recPurpose = this.receiptPurpose.getSelectedItem().getValue().toString();
		checkByReceiptPurpose(recPurpose, true);
		
		boolean makeFeeRender = false;
		eventCode = "";
		
		if (this.receiptPurpose.getSelectedIndex() > 0) {
			if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				eventCode = AccountEventConstants.ACCEVENT_REPAY;
			} else if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
			} else if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
			}

			makeFeeRender = true;
		}
		paidAllocationMap = new HashMap<>();
		waivedAllocationMap = new HashMap<>();
		feesRecalculation(makeFeeRender, false);
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Excess Amount Adjustments when Early settlement Selected on default
	 * @param recPurpose
	 */
	private void doExcessAdjustments(String recPurpose){
		// If Early Settlement then Excess Paid's should be set automatically
		if (!StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			return;
		}

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		BigDecimal totalDue = BigDecimal.ZERO;
		// Past due Details
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalDue") != null){
			Label due = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalDue");
			totalDue = PennantApplicationUtil.unFormateAmount(new BigDecimal(due.getValue().replaceAll(",", "")), formatter);
		}
		// Manual Advises
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalDue") != null){
			Label due = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalDue");
			totalDue = totalDue.add(PennantApplicationUtil.unFormateAmount(new BigDecimal(due.getValue().replaceAll(",", "")), formatter));
		}
		
		// Fee Amounts including
		BigDecimal feeToBePaid = BigDecimal.ZERO;
		if(getFinFeeDetailListCtrl() != null){
			feeToBePaid = getFinFeeDetailListCtrl().getFeePaidAmount(formatter);
			totalDue = totalDue.add(feeToBePaid);
		}

		List<FinExcessAmount> excessAmountList = getExcessList();
		if(excessAmountList != null && !excessAmountList.isEmpty()){
			for (FinExcessAmount excess : excessAmountList) {
				
				if(totalDue.compareTo(BigDecimal.ZERO) == 0){
					break;
				}

				// Fetch Excess Amounts
				if(listBoxExcess.getFellowIfAny("ExcessAmount_"+excess.getAmountType()) != null){
					CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_"+excess.getAmountType());
					BigDecimal balAmount = excess.getBalanceAmt();
					if(getExcessReserveList() != null && !getExcessReserveList().isEmpty()){
						for (FinExcessAmountReserve reserve : getExcessReserveList()) {
							if(reserve.getExcessID() == excess.getExcessID()){
								balAmount = balAmount.add(reserve.getReservedAmt());
								break;
							}
						}
					}
					
					// Paid Amount Setting
					if(totalDue.compareTo(balAmount) < 0){
						balAmount = totalDue;
					}
					excessBox.setValue(PennantApplicationUtil.formateAmount(balAmount, formatter));
					totalDue = totalDue.subtract(balAmount);
					
					// Balance amount Setting
					if(listBoxExcess.getFellowIfAny("ExcessBal_"+excess.getAmountType()) != null){
						Label label = (Label) listBoxExcess.getFellowIfAny("ExcessBal_"+excess.getAmountType());
						label.setValue(PennantApplicationUtil.amountFormate(excess.getBalanceAmt().subtract(balAmount), formatter));
					}
					
				}
			}
		}

		// Payable Amounts
		List<ManualAdvise> payableList = getPayableList();
		if(payableList != null && !payableList.isEmpty()){
			for (ManualAdvise payable : payableList) {
				
				if(totalDue.compareTo(BigDecimal.ZERO) == 0){
					break;
				}

				// Fetch Excess Amounts
				if(listBoxExcess.getFellowIfAny("PayableAmount_"+payable.getAdviseID()) != null){
					CurrencyBox payableBox = (CurrencyBox) listBoxExcess.getFellowIfAny("PayableAmount_"+payable.getAdviseID());
					BigDecimal balAmount = payable.getBalanceAmt();
					if(getPayableReserveList() != null && !getPayableReserveList().isEmpty()){
						for (ManualAdviseReserve reserve : getPayableReserveList()) {
							if(reserve.getAdviseID() == payable.getAdviseID()){
								balAmount = balAmount.add(reserve.getReservedAmt());
								break;
							}
						}
					}
					if(totalDue.compareTo(balAmount) < 0){
						balAmount = totalDue;
					}
					payableBox.setValue(PennantApplicationUtil.formateAmount(balAmount, formatter));
					totalDue = totalDue.subtract(balAmount);
					
					// Balance amount Setting
					if(listBoxExcess.getFellowIfAny("PayableBal_"+payable.getAdviseID()) != null){
						Label label = (Label) listBoxExcess.getFellowIfAny("PayableBal_"+payable.getAdviseID());
						label.setValue(PennantApplicationUtil.amountFormate(payable.getBalanceAmt().subtract(balAmount), formatter));
					}
				}
			}
		}
	}

	private void feesRecalculation(boolean makeFeeRender, boolean isFeeConsiderOnAmount) throws InterruptedException {
		logger.debug("Entering");
		
		List<FinTypeFees> finTypeFeesList = null;
		if (StringUtils.isNotEmpty(eventCode)) {
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			int moduleID = FinanceConstants.MODULEID_FINTYPE;
			
			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				moduleID = FinanceConstants.MODULEID_PROMOTION;
			}

			// Finance Type Fee details based on Selected Receipt Purpose Event
			finTypeFeesList = this.financeDetailService.getFinTypeFees(financeMain.getFinType(), eventCode, false, moduleID);
		}
		
		// Existing Fee Details maintenance
		List<FinFeeDetail> finFeeDetails = new ArrayList<>();
		for (FinFeeDetail fee : getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
			
			// If Origination Fees, just make the record is set to Invisible
			if (fee.isOriginationFee()) {
				fee.setRcdVisible(false);
			} else {

				// If Fees is newly added to List (Without DB save) should be removed from List
				if (fee.isNew()) {
					continue;
				} else {

					// If Event changed and existing fee should be removed from list, if it is already available in DB
					if (StringUtils.equals(fee.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
						fee.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						fee.setRcdVisible(false);
						
					// If Fee available and is in cancel state, should be reverted back to Original State
					} else if (StringUtils.equals(fee.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {

						if(finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
							for (FinTypeFees finTypeFee : finTypeFeesList) {
								if(finTypeFee.getFeeTypeID() == fee.getFeeTypeID() &&
										StringUtils.equals(finTypeFee.getFinEvent(), fee.getFinEvent())) {
									
									fee.setRecordType(PennantConstants.RECORD_TYPE_NEW);
									fee.setRcdVisible(true);
									
									// Based on FinType Fees, new list will be added in the FinFeeListCtrl,
									// So if already exists in available list it should be removed from FinTypeFees
									finTypeFeesList.remove(0);
								}
							}
						}
					}
				}
			}
			
			// Fee Details List Preparation for Rendering
			finFeeDetails.add(fee);
		}
		
		getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
		getFinanceDetail().getFinScheduleData().setFinFeeDetailList(finFeeDetails);
		
		// To set Payment details by default using Auto Allocation mode , if exists
		setAutoAllocationPayments(isFeeConsiderOnAmount);
		
		//Fee Details Tab Addition
		appendFeeDetailTab(makeFeeRender);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Setting Fields based on Receipt Purpose selected
	 * @param recPurpose
	 */
	private void checkByReceiptPurpose(String recPurpose, boolean isUserAction) {
		logger.debug("Entering");
		
		readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
		readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		readOnlyComponent(isReadOnly("ReceiptDialog_allocationMethod"), this.allocationMethod);
		
		if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) ||
				StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			readOnlyComponent(true, this.effScheduleMethod);
			this.effScheduleMethod.setSelectedIndex(0);
			
			if(StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE) && this.allocationMethod.getSelectedIndex() == 0){
				fillComboBox(this.allocationMethod, RepayConstants.ALLOCATIONTYPE_AUTO, PennantStaticListUtil.getAllocationMethods(), "");
			}
		} else if (StringUtils.equals(recPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			readOnlyComponent(true, this.excessAdjustTo);
			this.excessAdjustTo.setSelectedIndex(0);
			if(isUserAction){
				String dftEPMethod = getFinanceDetail().getFinScheduleData().getFinanceType().getFinScheduleOn();

				List<ValueLabel> epyMethodList = new ArrayList<>();
				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				if (StringUtils.isNotEmpty(financeType.getAlwEarlyPayMethods())) {
					String[] epMthds = financeType.getAlwEarlyPayMethods().trim().split(",");
					if (epMthds.length > 0) {
						List<String> list = Arrays.asList(epMthds);
						for (ValueLabel epMthd : PennantStaticListUtil.getEarlyPayEffectOn()) {
							if (list.contains(epMthd.getValue().trim())) {
								epyMethodList.add(epMthd);
							}
						}
					}
				}

				fillComboBox(this.effScheduleMethod, dftEPMethod, epyMethodList, "");
			}
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
		
		if(isUserAction){
			this.receiptAmount.setValue(BigDecimal.ZERO);
			this.favourNo.setValue("");
			this.valueDate.setValue(DateUtility.getAppDate());
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.bankCode.setObject(null);
			this.favourName.setValue("");
			this.depositDate.setValue(null);
			this.depositNo.setValue("");
			this.paymentRef.setValue("");
			this.transactionRef.setValue("");
			this.chequeAcNo.setValue("");
			this.fundingAccount.setValue("");
			this.fundingAccount.setDescription("");
			this.fundingAccount.setObject(null);
			this.receivedDate.setValue(DateUtility.getAppDate());
		}
		
		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select) ||
				StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);
			
		} else{

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.receiptAmount.setMandatory(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_receiptAmount"), this.receiptAmount);
			
			Filter fundingAcFilters[] = new Filter[3];
			fundingAcFilters[0] = new Filter("Purpose", RepayConstants.RECEIPTTYPE_RECIPT, Filter.OP_EQUAL);
			fundingAcFilters[1] = new Filter("FinType", financeType.getFinType(), Filter.OP_EQUAL);
			fundingAcFilters[2] = new Filter("PaymentMode", recMode, Filter.OP_EQUAL);
			Filter.and(fundingAcFilters);
			this.fundingAccount.setFilters(fundingAcFilters);
			this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);
			
			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {
				
				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);
				
				if(StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)){
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value"));
					
					if(isUserAction){
						this.depositDate.setValue(DateUtility.getAppDate());
						this.receivedDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(DateUtility.getAppDate());
					}
					
				}else{
					this.row_ChequeAcNo.setVisible(false);
					this.label_ReceiptDialog_favourNo.setValue(Labels.getLabel("label_ReceiptDialog_DDFavourNo.value"));
					
					if(isUserAction){
						this.depositDate.setValue(DateUtility.getAppDate());
						this.valueDate.setValue(DateUtility.getAppDate());
					}
				}
				
				if(isUserAction){
					this.favourName.setValue(Labels.getLabel("label_ClientName"));
				}
				
			} else if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)) {
				
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);
				
				if(isUserAction){
					this.receivedDate.setValue(DateUtility.getAppDate());
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

		// Due to changes in Receipt Amount, call Auto Allocations
		if(isUserAction){
			setAutoAllocationPayments(true);
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
		//waivedAllocationMap = new HashMap<>();
		if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)){
			setAutoAllocationPayments(true);
		}else if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
			doFillAllocationDetail(null, null, true, true);
			resetFeeAmounts(true);
		}else{
			this.allocationDetailsTab.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Calculating Auto Allocation Amount paid now and set against Allocation Details
	 * @param event
	 */
	public void onChange$receiptModeStatus(Event event) {
		logger.debug("Entering");

		// Based on Status of Mode Details will be set to Visible
		String status = this.receiptModeStatus.getSelectedItem().getValue().toString();
		resetModeStatus(status);
		logger.debug("Leaving");
	}
	
	private void resetModeStatus(String status){
		readOnlyComponent(true, this.bounceCode);
		readOnlyComponent(true, this.bounceCharge);
		readOnlyComponent(true, this.bounceRemarks);
		readOnlyComponent(true, this.bounceDate);
		readOnlyComponent(true, this.cancelReason);
		readOnlyComponent(true, this.realizationDate);
		
		this.row_CancelReason.setVisible(false);
		this.row_BounceReason.setVisible(false);
		this.row_BounceRemarks.setVisible(false);
		this.row_RealizationDate.setVisible(false);
		
		if(StringUtils.equals(status, RepayConstants.PAYSTATUS_BOUNCE)){

			this.row_BounceReason.setVisible(true);
			this.row_BounceRemarks.setVisible(true);
			
			readOnlyComponent(isReadOnly("ReceiptDialog_bounceCode"), this.bounceCode);
			readOnlyComponent(true, this.bounceCharge);
			readOnlyComponent(isReadOnly("ReceiptDialog_bounceRemarks"), this.bounceRemarks);
			readOnlyComponent(isReadOnly("ReceiptDialog_bounceDate"), this.bounceDate);

		} else if(StringUtils.equals(status, RepayConstants.PAYSTATUS_CANCEL)){

			this.row_CancelReason.setVisible(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_cancelReason"), this.cancelReason);

		} else if(StringUtils.equals(status, RepayConstants.PAYSTATUS_REALIZED)){

			this.row_RealizationDate.setVisible(true);
			readOnlyComponent(isReadOnly("ReceiptDialog_realizationDate"), this.realizationDate);
			
		}
	}
	
	/**
	 * Method for Allocation Details recalculation
	 */
	private void setAutoAllocationPayments(boolean isFeeConsiderOnAmount){
		logger.debug("Entering");
		
		this.allocationMethod.setConstraint("");
		this.allocationMethod.setErrorMessage("");
		this.receiptPurpose.setConstraint("");
		this.receiptPurpose.setErrorMessage("");
		String allocateMthd = getComboboxValue(this.allocationMethod);
		String tempReceiptPurpose = getComboboxValue(this.receiptPurpose);
		
		Date valueDate = DateUtility.getAppDate();
		if(this.receivedDate.getValue() != null){
			valueDate = this.receivedDate.getValue();
		}
		
		// Set total , If Receipt Purpose is Early settlement
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setBuildProcess("I");
		FinScheduleData schData = new FinScheduleData();
		Cloner cloner = new Cloner();
		schData = cloner.deepClone(getFinanceDetail().getFinScheduleData());
		
		// Excess Adjustments After calculation of Total Paid's
		doExcessAdjustments(tempReceiptPurpose);

		receiptData.setAccruedTillLBD(schData.getFinanceMain().getLovDescAccruedTillLBD());
		receiptData.setFinanceDetail(getFinanceDetail());
		BigDecimal totReceiptAmount = getTotalReceiptAmount(isFeeConsiderOnAmount);
		receiptData.setTotReceiptAmount(totReceiptAmount);
		setReceiptData(getReceiptCalculator().initiateReceipt(receiptData, schData, valueDate, tempReceiptPurpose, false));

		doFillAllocationDetail(null, null, false, isFeeConsiderOnAmount);
		
		// Allocation Process start
		if(!StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)){
			resetFeeAmounts(isFeeConsiderOnAmount);
			logger.debug("Leaving");
			return;
		}
		
		// Calling for Past due Amount Auto Calculation Process
		FinScheduleData aFinScheduleData = getFinanceDetailService().getFinSchDataForReceipt(this.finReference.getValue(), "_AView");
		Map<String, BigDecimal> paidAllocatedMap = getReceiptCalculator().recalAutoAllocation(aFinScheduleData, 
				totReceiptAmount, valueDate, tempReceiptPurpose, false);
		
		// Render Allocation Details & Manual Advises
		if(paidAllocatedMap != null && !paidAllocatedMap.isEmpty()){
			List<String> paidMapKeys = new ArrayList<>(paidAllocatedMap.keySet());
			for (int i = 0; i < paidMapKeys.size(); i++) {
				BigDecimal allocate = paidAllocatedMap.get(paidMapKeys.get(i));
				this.paidAllocationMap.put("AllocatePaid_"+paidMapKeys.get(i), allocate);
			}
		}

		doFillAllocationDetail(null, paidAllocatedMap, true, isFeeConsiderOnAmount);
		resetFeeAmounts(isFeeConsiderOnAmount);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for fetch sum of Total user entered Receipts amounts
	 * @return
	 */
	public BigDecimal getTotalReceiptAmount(boolean feeTobeConsider){
		
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal totalReceiptAmount = BigDecimal.ZERO;
		
		// Fetch Receipt Mode related Amount
		totalReceiptAmount = PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(), formatter);
		
		// Fetch Excess Amounts
		if(listBoxExcess.getFellowIfAny("ExcessAmount_E") != null){
			CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_E");
			totalReceiptAmount = totalReceiptAmount.add(PennantApplicationUtil.unFormateAmount(excessBox.getActualValue(), formatter));
		}
		
		// Fetch EMI in Advance Amount
		if(listBoxExcess.getFellowIfAny("ExcessAmount_A") != null){
			CurrencyBox emiInAdvBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_A");
			totalReceiptAmount = totalReceiptAmount.add(PennantApplicationUtil.unFormateAmount(emiInAdvBox.getActualValue(), formatter));
		}
		
		// Payable Amounts
		List<Listitem> payableItems = this.listBoxExcess.getItems();
		for (int i = 0; i < payableItems.size(); i++) {
			Listitem item = payableItems.get(i);
			if(item.getId().contains("Payable")){
				CurrencyBox payableAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny(item.getId().replaceAll("Item", "Amount"));
				totalReceiptAmount = totalReceiptAmount.add(PennantApplicationUtil.unFormateAmount(payableAmount.getActualValue(), formatter));
			}
		}
		
		if (feeTobeConsider) {
			// Fee Details
			if(getFinFeeDetailListCtrl() != null){
				BigDecimal feeToBePaid = getFinFeeDetailListCtrl().getFeePaidAmount(formatter);
				totalReceiptAmount = totalReceiptAmount.subtract(feeToBePaid);
				
				// Actual Fee Paid Amount is more than Receipt Amount then Make Zero
				if(totalReceiptAmount.compareTo(BigDecimal.ZERO) < 0){
					totalReceiptAmount = BigDecimal.ZERO;
				}
			}
		}
		
		return totalReceiptAmount;
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterruptedException
	 */
	public void recalEarlyPaySchd(FinReceiptData receiptData, String recptPurpose) throws InterruptedException {
		logger.debug("Entering");
		
		//Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		financeDetail.setFinScheduleData(getFinanceDetailService().getFinSchDataForReceipt(this.finReference.getValue(), "_AView"));
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		// Setting Effective Recalculation Schedule Method
		String method = null;
		if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
			method = getComboboxValue(this.effScheduleMethod);
		}else if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			method = CalculationConstants.EARLYPAY_ADJMUR;
		}
		
		// Schedule re-modifications only when Effective Schedule Method modified
		if (!StringUtils.equals(method, CalculationConstants.EARLYPAY_NOEFCT)) {
			
			// Setting Early Payment Amount for Calculation, 
			//Not required to Set in case of Early settlement(Already calculated in ReceiptCaculator)
			if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
				receiptData.getRepayMain().setEarlyPayAmount(PennantApplicationUtil.unFormateAmount(this.remBalAfterAllocation.getValue(), 	formatter));
			}

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
				if (detail.getSchDate().compareTo(receiptData.getRepayMain().getEarlyPayOnSchDate()) == 0) {
					if (StringUtils.equals(method, CalculationConstants.EARLYPAY_RECPFI)) {
						detail.setEarlyPaid(detail.getEarlyPaid().add(receiptData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					} else {
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						receiptData.getRepayMain().setEarlyPayAmount(detail.getPrincipalSchd().add(
								receiptData.getRepayMain().getEarlyPayAmount()).add(earlypaidBal));
					}
					if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
						detail.setPartialPaidAmt(detail.getPartialPaidAmt().add(
								(PennantApplicationUtil.unFormateAmount(this.remBalAfterAllocation.getValue(),formatter))));
					}
				}
				if (detail.getSchDate().compareTo(receiptData.getRepayMain().getEarlyPayOnSchDate()) >= 0) {
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			finScheduleData.setFinanceType(getFinanceType());
			
			// Finding Next Repay Schedule on date
			Date nextRepaySchDate = receiptData.getRepayMain().getEarlyPayNextSchDate();
			if(nextRepaySchDate == null){
				for (FinanceScheduleDetail curSchd : finScheduleData.getFinanceScheduleDetails()) {
					if(DateUtility.compare(curSchd.getSchDate(), receiptData.getRepayMain().getEarlyPayOnSchDate()) <= 0){
						if(DateUtility.compare(curSchd.getSchDate(), aFinanceMain.getGrcPeriodEndDate()) <= 0){
							if(StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_PFT) ||
									StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_PRI_PFT)){
								finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI_PFT);
							}else{
								finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI);
							}
						}else{
							finScheduleData.getFinanceMain().setRecalSchdMethod(curSchd.getSchdMethod());
						}
						if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) &&
								DateUtility.compare(curSchd.getSchDate(), receiptData.getRepayMain().getEarlyPayOnSchDate()) == 0){
							if(StringUtils.equals(finScheduleData.getFinanceMain().getRecalSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)){
								receiptData.getRepayMain().setEarlyPayAmount(receiptData.getRepayMain().getEarlyPayAmount().add(curSchd.getProfitSchd()));
							}else if(StringUtils.equals(finScheduleData.getFinanceMain().getRecalSchdMethod(), CalculationConstants.SCHMTHD_PFT)){
								finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI_PFT);
							}else if(StringUtils.equals(finScheduleData.getFinanceMain().getRecalSchdMethod(), CalculationConstants.SCHMTHD_PRI_PFT)){
								if(DateUtility.compare(curSchd.getSchDate(), aFinanceMain.getGrcPeriodEndDate()) > 0){
									receiptData.getRepayMain().setEarlyPayAmount(receiptData.getRepayMain().getEarlyPayAmount().add(curSchd.getProfitSchd()));
									finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_EQUAL);
								}
							}
						}
					}else{
						nextRepaySchDate = curSchd.getSchDate();
						break;
					}
				}
			}else{
				if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
					finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI);
				}else if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI_PFT);
				}
			}

			//Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, receiptData.getRepayMain()
					.getEarlyPayOnSchDate(), nextRepaySchDate, receiptData.getRepayMain().getEarlyPayAmount(), method);

			// Validation against Future Disbursements, if Closing balance is becoming zero before future disbursement date
			if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				List<FinanceDisbursement> disbList = finScheduleData.getDisbursementDetails();
				Date actualMaturity = finScheduleData.getFinanceMain().getCalMaturity();
				for (int i = 0; i < disbList.size(); i++) {
					FinanceDisbursement curDisb = disbList.get(i);
					if(curDisb.getDisbDate().compareTo(actualMaturity) >= 0){
						MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetails("30577", null)));
						Events.sendEvent(Events.ON_CLICK, this.btnChangeReceipt, null);
						logger.debug("Leaving");
						return;
					}
				}
			}

			financeDetail.setFinScheduleData(finScheduleData);
			aFinanceMain = finScheduleData.getFinanceMain();
			aFinanceMain.setWorkflowId(getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());
			setFinanceDetail(financeDetail);//Object Setting for Future save purpose
			receiptData.setFinanceDetail(financeDetail);
			
			// Fee Details Setting from Recalculation process of Schedule
			if(getFinFeeDetailListCtrl() != null){
				List<FinFeeDetail> eventFees = getFinFeeDetailListCtrl().getFinFeeDetailList();
				for (FinFeeDetail fee : getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
					fee.setRcdVisible(false);
				}
				getFinanceDetail().getFinScheduleData().getFinFeeDetailList().addAll(eventFees);
				getFinFeeDetailListCtrl().doFillFinFeeDetailList(getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
			}

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

		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,aFinScheduleData.getFinanceMain().getProductCategory())) {
			this.listheader_AvailableLimit.setVisible(true);
			this.listheader_ODLimit.setVisible(true);
			this.listheader_LimitChange.setVisible(true);
			
			listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitChange"));
			listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ODLimit"));
			listheader_AvailableLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AvailableLimit"));
		}

		FinanceScheduleDetail prvSchDetail = null;
		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if (sdSize > 0) {

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
			
			//Schedule Fee Column Visibility Check
			boolean isSchdFee = false;
			List<FinanceScheduleDetail> schdList = aFinScheduleData.getFinanceScheduleDetails();
			for (int i = 0; i < schdList.size(); i++) {
				FinanceScheduleDetail curSchd = schdList.get(i);
				if(curSchd.getFeeSchd().compareTo(BigDecimal.ZERO) > 0){
					isSchdFee = true;
					break;
				}
			}

			if (isSchdFee) {
				this.listheader_ReceiptSchedule_SchFee.setVisible(true);
			} else {
				this.listheader_ReceiptSchedule_SchFee.setVisible(false);
			}

			//Clear all the listitems in listbox
			this.listBoxSchedule.getItems().clear();
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
				
				finRender.render(map, prvSchDetail, false, true, true, aFinScheduleData.getFinFeeDetailList(), showRate, false);
				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, true, true, aFinScheduleData.getFinFeeDetailList(), showRate, false);
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
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		receiptHeader.setReceiptAmount(getTotalReceiptAmount(false));
		BigDecimal feeAmount = BigDecimal.ZERO;
		if(getFinFeeDetailListCtrl() != null){
			feeAmount = getFinFeeDetailListCtrl().getFeePaidAmount(finFormatter);
		}
		receiptHeader.setTotFeeAmount(feeAmount);
		receiptHeader.getAllocations().clear();
		
		// Basic Receipt Mode Details
		this.receipt_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(receiptHeader.getReceiptAmount(), finFormatter));
		this.allocation_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(receiptHeader.getReceiptAmount(), finFormatter));
		this.payment_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(receiptHeader.getReceiptAmount(), finFormatter));
		
		// Excess Amounts
		Map<String, FinExcessAmount> excessMap = new HashMap<>();
		if(getExcessList() != null && !getExcessList().isEmpty()){
			for (int i = 0; i < getExcessList().size(); i++) {
				excessMap.put(getExcessList().get(i).getAmountType(), getExcessList().get(i));
			}
		}
		
		// Payable Amounts
		Map<Long, ManualAdvise> payableMap = new HashMap<>();
		if(getPayableList() != null && !getPayableList().isEmpty()){
			for (int i = 0; i < getPayableList().size(); i++) {
				payableMap.put(getPayableList().get(i).getAdviseID(), getPayableList().get(i));
			}
		}
		
		List<FinReceiptDetail> receiptDetailList = new ArrayList<>();
		int payOrder = 1;
		// EMI In Advance Receipt Mode
		if(this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EMIINADV) != null){
			FinReceiptDetail receiptDetail = null;
			CurrencyBox emiAdvance = (CurrencyBox) this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EMIINADV);
			
			if(excessMap.containsKey(RepayConstants.EXAMOUNTTYPE_EMIINADV)){
				receiptDetail = getExistingReceiptDetail(receiptHeader, RepayConstants.PAYTYPE_EMIINADV, 
						excessMap.get(RepayConstants.EXAMOUNTTYPE_EMIINADV).getExcessID());
			}
			if(emiAdvance.getActualValue().compareTo(BigDecimal.ZERO) > 0){
				if(receiptDetail == null){
					receiptDetail = new FinReceiptDetail();
				}
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
				receiptDetail.setPayAgainstID(excessMap.get(RepayConstants.EXAMOUNTTYPE_EMIINADV).getExcessID());
				receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(emiAdvance.getActualValue(), finFormatter));
				receiptDetail.setValueDate(this.valueDate.getValue());
				receiptDetail.setReceivedDate(this.valueDate.getValue());
				receiptDetail.setDelRecord(false);// Internal Purpose
				receiptDetail.setPayOrder(payOrder);
				payOrder = payOrder + 1;
				receiptDetail.getAdvMovements().clear();
				receiptDetail.getRepayHeaders().clear();
				receiptDetailList.add(receiptDetail);
			}else{
				if(receiptDetail != null){
					receiptDetail.setDelRecord(true);
					receiptDetail.getAdvMovements().clear();
					receiptDetail.getRepayHeaders().clear();
					receiptDetailList.add(receiptDetail);
				}
			}
		}
		
		// Excess Amount Receipt Detail
		if(this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EXCESS) != null){
			CurrencyBox excessAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny("ExcessAmount_"+RepayConstants.EXAMOUNTTYPE_EXCESS);
			
			FinReceiptDetail receiptDetail = null;
			if(excessMap.containsKey(RepayConstants.EXAMOUNTTYPE_EXCESS)){
				receiptDetail = getExistingReceiptDetail(receiptHeader, RepayConstants.PAYTYPE_EXCESS, 
						excessMap.get(RepayConstants.EXAMOUNTTYPE_EXCESS).getExcessID());
			}
			if(excessAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0){
				if(receiptDetail == null){
					receiptDetail = new FinReceiptDetail();
				}
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EXCESS);
				receiptDetail.setPayAgainstID(excessMap.get(RepayConstants.EXAMOUNTTYPE_EXCESS).getExcessID());
				receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(excessAmount.getActualValue(), finFormatter));
				receiptDetail.setValueDate(this.valueDate.getValue());
				receiptDetail.setReceivedDate(this.valueDate.getValue());
				receiptDetail.setDelRecord(false);// Internal Purpose
				receiptDetail.setPayOrder(payOrder);
				payOrder = payOrder + 1;
				receiptDetail.getAdvMovements().clear();
				receiptDetail.getRepayHeaders().clear();
				receiptDetailList.add(receiptDetail);
			}else{
				if(receiptDetail != null){
					receiptDetail.setDelRecord(true);
					receiptDetail.getAdvMovements().clear();
					receiptDetail.getRepayHeaders().clear();
					receiptDetailList.add(receiptDetail);
				}
			}
		}
		
		// Payable Advise Receipt Modes 
		List<Listitem> payableItems = this.listBoxExcess.getItems();
		for (int i = 0; i < payableItems.size(); i++) {
			Listitem item = payableItems.get(i);
			if(item.getId().contains("Payable")){
				CurrencyBox payableAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny(item.getId().replaceAll("Item", "Amount"));
				long payableAdviseID = Long.valueOf(payableAmount.getId().substring(payableAmount.getId().indexOf("_")+1));
				FinReceiptDetail receiptDetail = null;
				receiptDetail = getExistingReceiptDetail(receiptHeader, RepayConstants.PAYTYPE_PAYABLE, payableAdviseID);
				if(payableAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0){
					if(receiptDetail == null){
						receiptDetail = new FinReceiptDetail();
					}
					receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
					receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
					receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PAYABLE);
					receiptDetail.setPayAgainstID(payableMap.get(payableAdviseID).getAdviseID());
					receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(payableAmount.getActualValue(), finFormatter));
					receiptDetail.setValueDate(this.valueDate.getValue());
					receiptDetail.setReceivedDate(this.valueDate.getValue());
					receiptDetail.setDelRecord(false);// Internal Purpose
					receiptDetail.setPayOrder(payOrder);
					payOrder = payOrder + 1;
					receiptDetail.getAdvMovements().clear();
					receiptDetail.getRepayHeaders().clear();
					receiptDetailList.add(receiptDetail);
				}else{
					if(receiptDetail != null){
						receiptDetail.setDelRecord(true);
						receiptDetail.getAdvMovements().clear();
						receiptDetail.getRepayHeaders().clear();
						receiptDetailList.add(receiptDetail);
					}
				}
			}
		}
		
		// Receipt Mode case
		FinReceiptDetail receiptDetail = getExistingReceiptDetail(receiptHeader, "", 0);
		if(!StringUtils.equals(RepayConstants.RECEIPTMODE_EXCESS, receiptHeader.getReceiptMode())){
			if(receiptDetail == null){
				receiptDetail = new FinReceiptDetail();
			}
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(receiptHeader.getReceiptMode());
			receiptDetail.setPayAgainstID(0);
			receiptDetail.setAmount(PennantApplicationUtil.unFormateAmount(this.receiptAmount.getActualValue(), finFormatter));
			receiptDetail.setFavourNumber(this.favourNo.getValue());
			receiptDetail.setValueDate(this.valueDate.getValue());
			receiptDetail.setBankCode(this.bankCode.getValue());
			receiptDetail.setFavourName(this.favourName.getValue());
			receiptDetail.setDepositDate(this.depositDate.getValue());
			receiptDetail.setDepositNo(this.depositNo.getValue());
			receiptDetail.setPaymentRef(this.paymentRef.getValue());
			receiptDetail.setTransactionRef(this.transactionRef.getValue());
			receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
			receiptDetail.setFundingAc(Long.valueOf(this.fundingAccount.getValue()));
			receiptDetail.setReceivedDate(this.receivedDate.getValue());
			receiptDetail.setDelRecord(false);// Internal Purpose
			receiptDetail.setPayOrder(payOrder);
			payOrder = payOrder + 1;
			receiptDetail.getAdvMovements().clear();
			receiptDetail.getRepayHeaders().clear();
			receiptDetailList.add(receiptDetail);
		}else{
			if(receiptDetail != null){
				receiptDetail.setDelRecord(true);
				receiptDetail.getAdvMovements().clear();
				receiptDetail.getRepayHeaders().clear();
				receiptDetailList.add(receiptDetail);
			}
		}
		
		receiptHeader.setReceiptDetails(receiptDetailList);
		receiptHeader.setRemarks(this.remarks.getValue());
		
		// Prepare Allocation Details
		List<String> allocateTypes = new ArrayList<>(getReceiptData().getAllocationMap().keySet());
		ReceiptAllocationDetail allocationDetail = null;
		for (int i = 0; i < allocateTypes.size(); i++) {
			allocationDetail = new ReceiptAllocationDetail();
			
			String allocationType = allocateTypes.get(i);
			long allocateTo = 0;
			if(allocateTypes.get(i).contains("_")){
				allocationType = allocateTypes.get(i).substring(0, allocateTypes.get(i).indexOf("_"));
				allocateTo = Long.valueOf(allocateTypes.get(i).substring(allocateTypes.get(i).indexOf("_")+1));
			}
			
			allocationDetail.setAllocationID(i+1);
			allocationDetail.setAllocationType(allocationType);
			allocationDetail.setAllocationTo(allocateTo);
			if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV) ||
					StringUtils.equals(allocationType, RepayConstants.ALLOCATION_BOUNCE)){
				
				if(this.listBoxManualAdvises.getFellowIfAny("AllocatePaid_"+allocateTypes.get(i)) != null){
					CurrencyBox paidAllocate = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny("AllocatePaid_"+allocateTypes.get(i));
					allocationDetail.setPaidAmount(PennantApplicationUtil.unFormateAmount(paidAllocate.getActualValue(), finFormatter));
				}
				
				if(this.listBoxManualAdvises.getFellowIfAny("AllocateAdvWaived_"+allocateTypes.get(i)) != null){
					CurrencyBox waivedAllocate = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny("AllocateAdvWaived_"+allocateTypes.get(i));
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
			
			if(allocationDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 || 
					allocationDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0){
				receiptHeader.getAllocations().add(allocationDetail);
			}
		}
		
		// Setting Extra amount for Partial Settlement case
		if(StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)){
			boolean isPriRcdFound = false;
			for (ReceiptAllocationDetail detail : receiptHeader.getAllocations()) {
				if(StringUtils.equals(detail.getAllocationType(), RepayConstants.ALLOCATION_PRI) && 
						this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0){
					detail.setPaidAmount(detail.getPaidAmount().add(PennantApplicationUtil.unFormateAmount(this.remBalAfterAllocation.getValue(), finFormatter)));
					isPriRcdFound = true;
					break;
				}
			}
			if(!isPriRcdFound){
				allocationDetail = new ReceiptAllocationDetail();
				allocationDetail.setAllocationID(receiptHeader.getAllocations().size()+1);
				allocationDetail.setAllocationType(RepayConstants.ALLOCATION_PRI);
				allocationDetail.setAllocationTo(0);
				allocationDetail.setPaidAmount(PennantApplicationUtil.unFormateAmount(this.remBalAfterAllocation.getValue(), finFormatter));
				receiptHeader.getAllocations().add(allocationDetail);
			}
		}
		
		excessMap = null;
		getReceiptData().setReceiptHeader(receiptHeader);
		
		Date valueDate = DateUtility.getAppDate();
		if(this.receivedDate.getValue() != null){
			valueDate = this.receivedDate.getValue();
		}
		receiptData = getReceiptCalculator().initiateReceipt(getReceiptData(), aFinScheduleData, valueDate, receiptHeader.getReceiptPurpose(), false);
		setReceiptData(receiptData);

		logger.debug("Leaving");
		return receiptData;
	}
	
	/**
	 * Method for identifying the existing record
	 * @param header
	 * @param receiptType
	 * @return
	 */
	private FinReceiptDetail getExistingReceiptDetail(FinReceiptHeader header, String receiptType, long payAgainstID){
		FinReceiptDetail detail = null;
		for (FinReceiptDetail receiptDetail : header.getReceiptDetails()) {
			if(StringUtils.equals(receiptDetail.getPaymentType(), receiptType) && 
					receiptDetail.getPayAgainstID() == payAgainstID){
				detail = receiptDetail;
				break;
			}
			if(StringUtils.isEmpty(receiptType) && receiptDetail.getPayAgainstID() == 0){
				detail = receiptDetail;
				break;
			}
		}
		return detail;
	}

	private void setRepayDetailData(FinReceiptData receiptData) throws InterruptedException {
		logger.debug("Entering");

		// Repay Schedule Data rebuild
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();
		for (int i = 0; i < receiptDetailList.size(); i++) {
			List<FinRepayHeader> repayHeaderList = receiptDetailList.get(i).getRepayHeaders();
			for (int j = 0; j < repayHeaderList.size(); j++) {
				if(repayHeaderList.get(j).getRepayScheduleDetails() != null){
					rpySchdList.addAll(repayHeaderList.get(j).getRepayScheduleDetails());
				}
			}
		}
		
		// Making Single Set of Repay Schedule Details and sent to Rendering
		Cloner cloner = new Cloner();
		List<RepayScheduleDetail> tempRpySchdList = cloner.deepClone(rpySchdList);
		Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
		for (RepayScheduleDetail rpySchd : tempRpySchdList) {
			
			RepayScheduleDetail curRpySchd = null;
			if(rpySchdMap.containsKey(rpySchd.getSchDate())){
				curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
				
				if(curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0){
					curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
				}
				
				if(curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0){
					curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
				}
				
				curRpySchd.setPrincipalSchdPayNow(curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
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
		if(rpySchdMap.isEmpty()){
			this.repaymentDetailsTab.setVisible(false);
			this.receiptDetailsTab.setSelected(true);
		}else{
			this.repaymentDetailsTab.setVisible(true);
			this.repaymentDetailsTab.setSelected(true);
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
	public void onClick$btnChangeReceipt(Event event) throws InterruptedException, IllegalAccessException,
	InvocationTargetException {
		logger.debug("Entering" + event.toString());
		
		Date valueDate = DateUtility.getAppDate();
		if(this.receivedDate.getValue() != null){
			valueDate = this.receivedDate.getValue();
		}

		setSummaryData(true, valueDate);
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
		
		doEdit();
		String rcptPurpose = getComboboxValue(this.receiptPurpose);
		checkByReceiptPurpose(rcptPurpose, false);
		checkByReceiptMode(getComboboxValue(this.receiptMode), false);
		
		// Excess amount set to readonly
		if(listBoxExcess.getFellowIfAny("ExcessAmount_E") != null){
			CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_E");
			excessBox.setReadonly(isReadOnly("ReceiptDialog_ExcessAmount"));
		}

		// EMI in Advance Amount
		if(listBoxExcess.getFellowIfAny("ExcessAmount_A") != null){
			CurrencyBox emiInAdvBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_A");
			emiInAdvBox.setReadonly(isReadOnly("ReceiptDialog_ExcessAmount"));
		}
		
		// Payable Amounts
		List<Listitem> payableItems = this.listBoxExcess.getItems();
		for (int i = 0; i < payableItems.size(); i++) {
			Listitem item = payableItems.get(i);
			if(item.getId().contains("Payable")){
				CurrencyBox payableAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny(item.getId().replaceAll("Item", "Amount"));
				payableAmount.setReadonly(isReadOnly("ReceiptDialog_PayableAmount"));
			}
		}

		// Pastdue Allocations
		String allocateMthd = getComboboxValue(this.allocationMethod);
		List<Listitem> pastdueItems = this.listBoxPastdues.getItems();
		boolean isAllocateAllowed = false;
		for (int i = 0; i < pastdueItems.size(); i++) {
			Listitem item = pastdueItems.get(i);
			isAllocateAllowed = true;
			if(item.getId().contains("Allocate")){
				if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
					CurrencyBox paidBox = (CurrencyBox) this.listBoxPastdues.getFellowIfAny(item.getId().replaceAll("Item", "Paid"));
					paidBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}
				CurrencyBox waivedBox = (CurrencyBox) this.listBoxPastdues.getFellowIfAny(item.getId().replaceAll("Item", "Waived"));
				if(StringUtils.equals(rcptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					waivedBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}else{
					
					String allocationType = waivedBox.getId().replace("", "");
					if(allocationType.contains("_")){
						allocationType = allocationType.substring(0, allocationType.indexOf("_"));
					}
					
					if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_ODC)){
						waivedBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
					}else{
						waivedBox.setReadonly(true);
					}
				}
			}
		}

		// Manual Advise Allocations
		List<Listitem> advises = this.listBoxManualAdvises.getItems();
		for (int i = 0; i < advises.size(); i++) {
			Listitem item = advises.get(i);
			isAllocateAllowed = true;
			if(item.getId().contains("Allocate")){
				if(StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)){
					CurrencyBox paidBox = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny(item.getId().replaceAll("Item", "Paid"));
					paidBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}
				CurrencyBox waivedBox = (CurrencyBox) this.listBoxManualAdvises.getFellowIfAny(item.getId().replaceAll("Item", "AdvWaived"));
				waivedBox.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
			}
		}
		
		// Checking Allocation method read only case
		if(!isAllocateAllowed){
			readOnlyComponent(true, this.allocationMethod);
			this.allocationMethod.setSelectedIndex(0);
		}

		if (this.excessAdjustTo.getSelectedIndex() > 0) {
			readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
		} else {
			readOnlyComponent(true, this.excessAdjustTo);
		}
		
		Events.sendEvent("onFulfill", this.receiptAmount, null);
		
		//feesRecalculation(true);
		
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
			
			if(!recReject){
				doClearMessage();
				doSetValidation();
				doWriteComponentsToBean();
				
				FinReceiptData data = getReceiptData();
				List<FinReceiptDetail> receiptDetails = data.getReceiptHeader().getReceiptDetails();
				
				int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
				BigDecimal totReceiptAmt = getTotalReceiptAmount(true);
				BigDecimal feeAmount = BigDecimal.ZERO;
				if(getFinFeeDetailListCtrl() != null){
					feeAmount = getFinFeeDetailListCtrl().getFeePaidAmount(finFormatter);
				}
				totReceiptAmt = totReceiptAmt.add(feeAmount);
				data.getReceiptHeader().setTotFeeAmount(feeAmount);
				data.getReceiptHeader().setReceiptAmount(totReceiptAmt);
				
				for (FinReceiptDetail receiptDetail : receiptDetails) {
					if(!StringUtils.equals(RepayConstants.RECEIPTMODE_EXCESS, data.getReceiptHeader().getReceiptMode()) && 
							StringUtils.equals(receiptDetail.getPaymentType(), data.getReceiptHeader().getReceiptMode())){
						receiptDetail.setFavourNumber(this.favourNo.getValue());
						receiptDetail.setValueDate(this.valueDate.getValue());
						receiptDetail.setBankCode(this.bankCode.getValue());
						receiptDetail.setFavourName(this.favourName.getValue());
						receiptDetail.setDepositDate(this.depositDate.getValue());
						receiptDetail.setDepositNo(this.depositNo.getValue());
						receiptDetail.setPaymentRef(this.paymentRef.getValue());
						receiptDetail.setTransactionRef(this.transactionRef.getValue());
						receiptDetail.setChequeAcNo(this.chequeAcNo.getValue());
						receiptDetail.setFundingAc(Long.valueOf(this.fundingAccount.getValue()));
						receiptDetail.setReceivedDate(this.receivedDate.getValue());
					}
				}
			}

			if (recReject || isValidateData(false)) {
				//If Schedule Re-modified Save into DB or else only add Repayments Details
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

		FinReceiptData data = getReceiptData();
		data.setFinanceDetail(getFinanceDetail());
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
		
		FinanceMain aFinanceMain = data.getFinanceDetail().getFinScheduleData().getFinanceMain();
		//Resetting Service Task ID's from Original State
		aFinanceMain .setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);
		aFinanceMain.setRecordType(recordType);
		aFinanceMain.setVersion(version);
		aFinanceMain.setBefImage(befImage);
		
		// Receipt Header Details workflow fields
		FinReceiptHeader receiptHeader = data.getReceiptHeader();
		receiptHeader.setReference(aFinanceMain.getFinReference());
		receiptHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		receiptHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		receiptHeader.setRecordStatus(aFinanceMain.getRecordStatus());
		receiptHeader.setWorkflowId(aFinanceMain.getWorkflowId());
		
		// Check Accounting Verification Required or not
		boolean isAccVerificationReq = false;
		for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
			FinReceiptDetail receiptDetail = receiptHeader.getReceiptDetails().get(i);
			for (int j = 0; j < receiptDetail.getRepayHeaders().size(); j++) {
				
				FinRepayHeader repayHeader = receiptDetail.getRepayHeaders().get(j);
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent())
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {
					isAccVerificationReq = true;
				}
			}
		}

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
				
				aReceiptData.getReceiptHeader().setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aReceiptData.getReceiptHeader().setVersion(1);
				aReceiptData.getReceiptHeader().setNewRecord(true);
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
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum().compareTo(
					getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aReceiptData.getFinanceDetail().setStageAccountingList(null);
		}

		if (!recSave && getAccountingDetailDialogCtrl() != null && isAccVerificationReq) {
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
			
			aFinanceMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
			aReceiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
			
			// Setting Receipt Mode to FinanceMain Object for Workflow Process
			aFinanceMain.setReceiptMode(aReceiptData.getReceiptHeader().getReceiptMode());
			aFinanceMain.setReceiptPurpose(aReceiptData.getReceiptHeader().getReceiptPurpose());
			aFinanceMain.setReceiptModeStatus(aReceiptData.getReceiptHeader().getReceiptModeStatus());
			aFinanceMain.setWaivedAmt(aReceiptData.getReceiptHeader().getWaviedAmt());
			
			if (doProcess(aReceiptData, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				//Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Loan ",
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

						String reference = aFinanceMain.getFinReference();
						if (StringUtils.isNotEmpty(aFinanceMain.getNextRoleCode())) {
							if (!PennantConstants.RCD_STATUS_CANCELLED.equals(aFinanceMain.getRecordStatus())) {
								String[] to = aFinanceMain.getNextRoleCode().split(",");
								String message;

								if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
									message = Labels.getLabel("REC_FINALIZED_MESSAGE");
								} else {
									message = Labels.getLabel("REC_PENDING_MESSAGE");
								}
								message += " with Reference" + ":" + reference;

								getEventManager().publish(message, to, finDivision, aFinanceMain.getFinBranch());
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
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		// Receipt Header Details
		FinReceiptHeader header = getReceiptHeader();
		fillComboBox(this.receiptPurpose, header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), "");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
		this.remarks.setValue(header.getRemarks());

		String allocateMthd = header.getAllocationType();
		if(StringUtils.isEmpty(allocateMthd)){
			allocateMthd = RepayConstants.ALLOCATIONTYPE_AUTO;
		}
		fillComboBox(this.allocationMethod, allocateMthd, PennantStaticListUtil.getAllocationMethods(), "");
		
		List<ValueLabel> epyMethodList = new ArrayList<>();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (StringUtils.isNotEmpty(financeType.getAlwEarlyPayMethods())) {
			String[] epMthds = financeType.getAlwEarlyPayMethods().trim().split(",");
			if (epMthds.length > 0) {
				List<String> list = Arrays.asList(epMthds);
				for (ValueLabel epMthd : PennantStaticListUtil.getEarlyPayEffectOn()) {
					if (list.contains(epMthd.getValue().trim())) {
						epyMethodList.add(epMthd);
					}
				}
			}
		}
		
		fillComboBox(this.effScheduleMethod, header.getEffectSchdMethod(), epyMethodList, "");
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
		fillComboBox(this.receiptModeStatus, header.getReceiptModeStatus(), PennantStaticListUtil.getReceiptModeStatus(), "");
		
		// Receipt Mode Status Details
		if(!isReadOnly("ReceiptDialog_receiptModeStatus") || this.receiptModeStatus.getSelectedIndex() > 0){
			this.label_ReceiptDialog_ReceiptModeStatus.setVisible(true);
			this.hbox_ReceiptModeStatus.setVisible(true);
		}
		
		// Based on Status of Mode Details will be set to Visible
		if(StringUtils.equals(header.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)){
			
			this.bounceDate.setValue(header.getBounceDate());
			if(header.getBounceDate() == null){
				this.bounceDate.setValue(DateUtility.getAppDate());
			}
			
			ManualAdvise bounceReason = header.getManualAdvise();
			if(bounceReason != null){
				this.bounceCode.setValue(String.valueOf(bounceReason.getBounceID()), bounceReason.getBounceCode());
				this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(bounceReason.getAdviseAmount(),finFormatter));
				this.bounceRemarks.setValue(bounceReason.getRemarks());
			}
			
		} else if(StringUtils.equals(header.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)){
			this.cancelReason.setValue(header.getCancelReason(), header.getCancelReasonDesc());
		} else if(StringUtils.equals(header.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)){
			this.realizationDate.setValue(header.getRealizationDate());
		}
		
		resetModeStatus(header.getReceiptModeStatus());

		// Receipt Mode Details , if FinReceiptDetails Exists
		this.receipt_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));
		this.allocation_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));
		this.payment_paidByCustomer.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));
		checkByReceiptPurpose(header.getReceiptPurpose() , false);
		checkByReceiptMode(header.getReceiptMode(), false);
		
		// Separating Receipt Amounts based on user entry, if exists
		Map<String, BigDecimal> receiptAmountsMap = new HashMap<>();
		if(header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()){
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
					receiptAmountsMap.put(receiptDetail.getPaymentType()+"_"+receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
				}else{
					receiptAmountsMap.put(receiptDetail.getPaymentType(), receiptDetail.getAmount());
				}
				if(!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) && 
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) &&
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
					this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(receiptDetail.getAmount(), finFormatter));
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
				}
			}
		}
		
		// Render Excess Amount Details
		doFillExcessAmounts(receiptAmountsMap);
		doFillPayableAmounts(receiptAmountsMap);
		receiptAmountsMap = null;

		// Render Allocation Details & Manual Advises
		if(header.getAllocations() != null && !header.getAllocations().isEmpty()){
			for (int i = 0; i < header.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocate = header.getAllocations().get(i);
				if(allocate.getAllocationTo() == 0 || allocate.getAllocationTo() == Long.MIN_VALUE){
					paidAllocationMap.put(allocate.getAllocationType(), allocate.getPaidAmount());
					if(StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_MANADV) ||
							StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)){
						waivedAllocationMap.put("AllocateAdvWaived_"+allocate.getAllocationType(), allocate.getWaivedAmount());
					}else{
						waivedAllocationMap.put("AllocateWaived_"+allocate.getAllocationType(), allocate.getWaivedAmount());
					}
				}else{
					paidAllocationMap.put(allocate.getAllocationType()+"_"+allocate.getAllocationTo(), allocate.getPaidAmount());
					if(StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_MANADV) ||
							StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)){
						waivedAllocationMap.put("AllocateAdvWaived_"+allocate.getAllocationType()+"_"+allocate.getAllocationTo(), allocate.getWaivedAmount());
					}else{
						waivedAllocationMap.put("AllocateWaived_"+allocate.getAllocationType()+"_"+allocate.getAllocationTo(), allocate.getWaivedAmount());
					}
				}
			}
		}
		doFillAllocationDetail(header.getAllocations(), null, false,true);

		// Only In case of partial settlement process, Display details for effective Schedule
		boolean visibleSchdTab = true;
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, header.getReceiptPurpose())) {

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
			visibleSchdTab = false;

		}
		
		// On Loading Data Render for Schedule
		if (getReceiptHeader() != null && getReceiptHeader().getReceiptDetails() != null
				&& !getReceiptHeader().getReceiptDetails().isEmpty()) {
			this.btnCalcReceipts.setDisabled(true);
			setRepayDetailData(getReceiptData());
		}

		getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);

		//Customer Details   
		appendCustomerDetailTab();

		//Fee Details Tab Addition
		boolean isLoadProcess = false;
		if(StringUtils.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordType())){
			isLoadProcess = true;
		}
		appendFeeDetailTab(isLoadProcess);

		// Schedule Details
		if(visibleSchdTab){
			appendScheduleDetailTab(true, false);
		}

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
	private void doFillExcessAmounts(Map<String, BigDecimal> receiptAmountsMap){
		logger.debug("Entering");

		// Excess Amounts
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		List<String> excessAmountTypes = new ArrayList<>();
		excessAmountTypes.add(RepayConstants.EXAMOUNTTYPE_EMIINADV);
		excessAmountTypes.add(RepayConstants.EXAMOUNTTYPE_EXCESS);

		Map<String, BigDecimal> excessMap = new HashMap<>();
		List<FinExcessAmount> excessAmountList = getExcessList();
		if(excessAmountList != null && !excessAmountList.isEmpty()){
			for (int i = 0; i < excessAmountList.size(); i++) {
				BigDecimal balAmount = excessAmountList.get(i).getBalanceAmt();
				if(getExcessReserveList() != null && !getExcessReserveList().isEmpty()){
					for (FinExcessAmountReserve reserve : getExcessReserveList()) {
						if(reserve.getExcessID() == excessAmountList.get(i).getExcessID()){
							balAmount = balAmount.add(reserve.getReservedAmt());
							break;
						}
					}
				}
				excessMap.put(excessAmountList.get(i).getAmountType(), balAmount);
			}
		}

		BigDecimal excessBal = BigDecimal.ZERO;
		Listitem item = null;
		Listcell lc = null;
		this.listBoxExcess.getItems().clear();
		for (int i = 0; i < excessAmountTypes.size(); i++) {

			String excessAmtType = excessAmountTypes.get(i);
			if(excessMap.containsKey(excessAmtType)){
				excessBal = excessMap.get(excessAmtType);
			}else{
				excessBal = BigDecimal.ZERO;
			}
			item = new Listitem();

			lc = new Listcell(Labels.getLabel("label_RecceiptDialog_ExcessType_"+excessAmtType));
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(excessBal, finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			BigDecimal paidAmount = BigDecimal.ZERO;
			if(StringUtils.equals(excessAmtType, RepayConstants.EXAMOUNTTYPE_EXCESS) && 
					receiptAmountsMap.containsKey(RepayConstants.PAYTYPE_EXCESS)){
				paidAmount = receiptAmountsMap.get(RepayConstants.PAYTYPE_EXCESS);
			}else if(StringUtils.equals(excessAmtType, RepayConstants.EXAMOUNTTYPE_EMIINADV) && 
					receiptAmountsMap.containsKey(RepayConstants.PAYTYPE_EMIINADV)){
				paidAmount = receiptAmountsMap.get(RepayConstants.PAYTYPE_EMIINADV);
			}
			
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

			BigDecimal balanceAmount = excessBal.subtract(paidAmount);
			Label balLabel = new Label(PennantApplicationUtil.amountFormate(balanceAmount, finFormatter));
			balLabel.setId("ExcessBal_"+excessAmtType);

			List<Object> list = new ArrayList<>();
			list.add(excessBal);
			list.add(excessAmount);
			list.add(balLabel);
			excessAmount.addForward("onFulfill", this.window_ReceiptDialog, "onExcessPayableAmountChange", list);
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
	 * Method for Rendering Payable Amount Details
	 */
	private void doFillPayableAmounts(Map<String, BigDecimal> receiptAmountsMap){
		logger.debug("Entering");

		// Excess Amounts
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		Map<Long, BigDecimal> payableMap = new HashMap<>();
		List<ManualAdvise> payableAmountList = getPayableList();
		if(payableAmountList != null && !payableAmountList.isEmpty()){
			for (int i = 0; i < payableAmountList.size(); i++) {
				BigDecimal balAmount = payableAmountList.get(i).getBalanceAmt();
				if(getPayableReserveList() != null && !getPayableReserveList().isEmpty()){
					for (ManualAdviseReserve reserve : getPayableReserveList()) {
						if(reserve.getAdviseID() == payableAmountList.get(i).getAdviseID()){
							balAmount = balAmount.add(reserve.getReservedAmt());
							break;
						}
					}
				}
				payableMap.put(payableAmountList.get(i).getAdviseID(), balAmount);
			}
		}

		BigDecimal payableBal = BigDecimal.ZERO;
		Listitem item = null;
		Listcell lc = null;
		for (int i = 0; i < payableAmountList.size(); i++) {

			ManualAdvise payableAdvise = payableAmountList.get(i);
			if(payableMap.containsKey(payableAdvise.getAdviseID())){
				payableBal = payableMap.get(payableAdvise.getAdviseID());
			}else{
				payableBal = BigDecimal.ZERO;
			}
			item = new Listitem();

			lc = new Listcell(payableAdvise.getFeeTypeDesc());
			lc.setStyle("font-weight:bold;color: #191a1c;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(payableBal, finFormatter));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			BigDecimal paidAmount = BigDecimal.ZERO;
			if(receiptAmountsMap.containsKey(RepayConstants.PAYTYPE_PAYABLE+"_"+payableAdvise.getAdviseID())){
				paidAmount = receiptAmountsMap.get(RepayConstants.PAYTYPE_PAYABLE+"_"+payableAdvise.getAdviseID());
			}
			
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			CurrencyBox payableAmt = new CurrencyBox();
			payableAmt.setStyle("text-align:right;");
			payableAmt.setBalUnvisible(true);
			setProps(payableAmt, false, finFormatter, 120);
			payableAmt.setId("PayableAmount_"+payableAdvise.getAdviseID());
			payableAmt.setValue(PennantApplicationUtil.formateAmount(paidAmount, finFormatter));
			payableAmt.setReadonly(isReadOnly("ReceiptDialog_PayableAmount"));
			lc.appendChild(payableAmt);
			lc.setParent(item);

			BigDecimal balanceAmount = payableBal.subtract(paidAmount);
			Label balLabel = new Label(PennantApplicationUtil.amountFormate(balanceAmount, finFormatter));
			balLabel.setId("PayableBal_"+payableAdvise.getAdviseID());

			List<Object> list = new ArrayList<>();
			list.add(payableBal);
			list.add(payableAmt);
			list.add(balLabel);
			payableAmt.addForward("onFulfill", this.window_ReceiptDialog, "onExcessPayableAmountChange", list);
			lc = new Listcell();
			lc.appendChild(balLabel);
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			item.setId("PayableItem_"+payableAdvise.getAdviseID());
			this.listBoxExcess.appendChild(item);
		}
		payableMap = null;

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Allocation Details based on Allocation Method (Auto/Manual)
	 * @param header
	 * @param allocatePaidMap
	 */
	private void doFillAllocationDetail(List<ReceiptAllocationDetail> allocations, Map<String, BigDecimal> allocatePaidMap, boolean isUserAction, boolean isFeeConsiderOnAmount){
		logger.debug("Entering");
		
		// Allocation Details & Manual Advises
		Map<String, ReceiptAllocationDetail> allocationMap = new HashMap<>();
		if(allocations != null && !allocations.isEmpty()){
			for (int i = 0; i < allocations.size(); i++) {
				if(allocations.get(i).getAllocationTo() != 0){
					allocationMap.put(allocations.get(i).getAllocationType()+"_"+allocations.get(i).getAllocationTo(), allocations.get(i));
				}else{
					allocationMap.put(allocations.get(i).getAllocationType(), allocations.get(i));
				}
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
		BigDecimal totalAdvDueAmount = BigDecimal.ZERO;
		BigDecimal totalAdvPaidAmount = BigDecimal.ZERO;
		BigDecimal totalAdvWaivedAmount = BigDecimal.ZERO;
		
		if(allocateTypes != null && !allocateTypes.isEmpty()){

			for (int i = 0; i < allocateTypes.size(); i++) {

				String allocationType = allocateTypes.get(i);
				if(allocationMap.containsKey(allocationType)){
					allocation = allocationMap.get(allocationType);
				}else{
					allocation = new ReceiptAllocationDetail();
				}

				if(allocateTypes.get(i).contains("_")){
					allocationType = allocateTypes.get(i).substring(0, allocateTypes.get(i).indexOf("_"));
				}
				
				BigDecimal totalCalAmount = getReceiptData().getAllocationMap().get(allocateTypes.get(i));

				item = new Listitem();
				String label = Labels.getLabel("label_RecceiptDialog_AllocationType_"+allocationType);
				if(allocateTypes.get(i).contains("_")){
					if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV)){
						label = getReceiptData().getAllocationDescMap().get(allocateTypes.get(i));
					}else{
						label = label+" : "+getReceiptData().getAllocationDescMap().get(allocateTypes.get(i));
					}
				}
				lc = new Listcell(label);
				lc.setStyle("font-weight:bold;color: #191a1c;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(totalCalAmount, finFormatter));
				lc.setId("AllocateAmount_"+allocateTypes.get(i));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell();
				CurrencyBox allocationPaid = new CurrencyBox();
				allocationPaid.setStyle("text-align:right;");
				allocationPaid.setBalUnvisible(true);
				setProps(allocationPaid, false, finFormatter, 120);
				allocationPaid.setId("AllocatePaid_"+allocateTypes.get(i));
				if(allocatePaidMap != null){
					if(allocatePaidMap.containsKey(allocateTypes.get(i))){
						BigDecimal autoCalPaidAmt = allocatePaidMap.get(allocateTypes.get(i));
						allocationPaid.setValue(PennantApplicationUtil.formateAmount(autoCalPaidAmt, finFormatter));
					}else{
						allocationPaid.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
					}
				}else{
					if(isUserAction){
						if(paidAllocationMap != null && paidAllocationMap.containsKey("AllocatePaid_"+allocateTypes.get(i))){
							allocationPaid.setValue(PennantApplicationUtil.formateAmount(paidAllocationMap.get("AllocatePaid_"+allocateTypes.get(i)), finFormatter));
						}else{
							allocationPaid.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
						}
					}else{
						allocationPaid.setValue(PennantApplicationUtil.formateAmount(allocation.getPaidAmount(), finFormatter));
						if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_PRI)){
							if(allocation.getPaidAmount().compareTo(totalCalAmount) > 0){
								allocationPaid.setValue(PennantApplicationUtil.formateAmount(totalCalAmount, finFormatter));
								this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(allocation.getPaidAmount().subtract(totalCalAmount), finFormatter));
							}
						}
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
				if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV) || 
						StringUtils.equals(allocationType, RepayConstants.ALLOCATION_BOUNCE)){
					allocationWaived.setId("AllocateAdvWaived_"+allocateTypes.get(i));
				}else{
					allocationWaived.setId("AllocateWaived_"+allocateTypes.get(i));
				}
				
				// Amount Setting
				if(waivedAllocationMap != null && waivedAllocationMap.containsKey("AllocateWaived_"+allocateTypes.get(i))){
					allocationWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocationMap.get("AllocateWaived_"+allocateTypes.get(i)), finFormatter));
				}else if(waivedAllocationMap != null && waivedAllocationMap.containsKey("AllocateAdvWaived_"+allocateTypes.get(i))){
					allocationWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocationMap.get("AllocateAdvWaived_"+allocateTypes.get(i)), finFormatter));
				}else{
					allocationWaived.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, finFormatter));
				}

				if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					allocationWaived.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
				}else{
					if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV) ||
							StringUtils.equals(allocationType, RepayConstants.ALLOCATION_ODC) ||
							StringUtils.equals(allocationType, RepayConstants.ALLOCATION_BOUNCE)){
						allocationWaived.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
					}else{
						allocationWaived.setReadonly(true);
					}
				}

				lc.appendChild(allocationWaived);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Setting On Change Event for Amounts
				List<Object> paidList = new ArrayList<>();
				paidList.add(totalCalAmount);
				paidList.add(allocationPaid);
				paidList.add(allocationWaived);
				allocationPaid.addForward("onFulfill", this.window_ReceiptDialog, "onAllocatePaidChange", paidList);

				// Setting On Change Event for Amounts
				List<Object> waivedList = new ArrayList<>();
				waivedList.add(totalCalAmount);
				waivedList.add(allocationPaid);
				waivedList.add(allocationWaived);
				allocationWaived.addForward("onFulfill", this.window_ReceiptDialog, "onAllocateWaivedChange", waivedList);

				// Set ID to Item
				item.setId("AllocateItem_"+allocateTypes.get(i));
				
				// Not editable for TDS Amount
				if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_TDS) || 
						StringUtils.equals(allocationType, RepayConstants.ALLOCATION_PFT)){
					allocationPaid.setReadonly(true);
					if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_PFT) && 
							StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
						allocationWaived.setReadonly(isReadOnly("ReceiptDialog_PastdueAmount"));
					}else{
						allocationWaived.setReadonly(true);
					}
				}

				if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_MANADV) ||
						StringUtils.equals(allocationType, RepayConstants.ALLOCATION_BOUNCE)){
					this.listBoxManualAdvises.appendChild(item);
					totalAdvDueAmount = totalAdvDueAmount.add(totalCalAmount);
					totalAdvPaidAmount = totalAdvPaidAmount.add(PennantApplicationUtil.unFormateAmount(allocationPaid.getActualValue(), finFormatter));
					totalAdvWaivedAmount = totalAdvWaivedAmount.add(PennantApplicationUtil.unFormateAmount(allocationWaived.getActualValue(), finFormatter));
				}else{
					this.listBoxPastdues.appendChild(item);
					if(StringUtils.equals(allocationType, RepayConstants.ALLOCATION_TDS) || 
							StringUtils.equals(allocationType, RepayConstants.ALLOCATION_PFT)){
						
						// Nothing TODO
					}else{
						totalDueAmount = totalDueAmount.add(totalCalAmount);
						totalPaidAmount = totalPaidAmount.add(PennantApplicationUtil.unFormateAmount(allocationPaid.getActualValue(), finFormatter));
						totalWaivedAmount = totalWaivedAmount.add(PennantApplicationUtil.unFormateAmount(allocationWaived.getActualValue(), finFormatter));
					}
				}
			}
		}else{
			readOnlyComponent(true, this.allocationMethod);
			this.allocationMethod.setSelectedIndex(0);
		}
		
		// Creating Pastdue Totals to verify against calculations & for validation
		if(totalDueAmount.compareTo(BigDecimal.ZERO) > 0){
			addFooter(totalDueAmount, totalPaidAmount, totalWaivedAmount, finFormatter, true);
		}
		
		// Creating Manual Advise Totals to verify against calculations & for validation
		if(totalAdvDueAmount.compareTo(BigDecimal.ZERO) > 0){
			addFooter(totalAdvDueAmount, totalAdvPaidAmount, totalAdvWaivedAmount, finFormatter, false);
		}
		
		// Setting Valid Components to open based upon Remaining Balance
		BigDecimal totReceiptAmount = getTotalReceiptAmount(isFeeConsiderOnAmount);
		BigDecimal remBal = totReceiptAmount.subtract(totalPaidAmount).subtract(totalAdvPaidAmount);
		if(remBal.compareTo(BigDecimal.ZERO) < 0){
			remBal = BigDecimal.ZERO;
		}
		this.remBalAfterAllocation.setValue(PennantApplicationUtil.formateAmount(remBal, finFormatter));
		this.custPaid.setValue(PennantApplicationUtil.formateAmount(totalDueAmount.add(totalAdvDueAmount), finFormatter));
		resetCustpaid(tempReceiptPurpose, totalDueAmount.add(totalAdvDueAmount), finFormatter);
		
		if(this.remBalAfterAllocation.getValue().compareTo(BigDecimal.ZERO) > 0){
			
			if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)){
				readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
				if(isUserAction){
					fillComboBox(this.excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS, PennantStaticListUtil.getExcessAdjustmentTypes(), "");
				}
			}else if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
				readOnlyComponent(isReadOnly("ReceiptDialog_effScheduleMethod"), this.effScheduleMethod);
			}else if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				readOnlyComponent(isReadOnly("ReceiptDialog_excessAdjustTo"), this.excessAdjustTo);
				if(isUserAction){
					fillComboBox(this.excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS, PennantStaticListUtil.getExcessAdjustmentTypes(), "");
				}
			}
			
		}else{
			readOnlyComponent(true, this.excessAdjustTo);
			readOnlyComponent(true, this.effScheduleMethod);
			if(isUserAction){
				this.excessAdjustTo.setSelectedIndex(0);
				this.effScheduleMethod.setSelectedIndex(0);
			}
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Adding footer to show Totals
	 * @param dueAmount
	 * @param paidAmount
	 * @param waivedAmount
	 * @param formatter
	 * @param isPastDue
	 */
	private void addFooter(BigDecimal dueAmount,BigDecimal paidAmount,BigDecimal waivedAmount, int formatter, boolean isPastDue){
		
		String compId = "allocation_"; 
		if(!isPastDue){
			compId = "manAdvise_"; 
		}

		// Creating Totals to verify against calculations & for validation
		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;align:bottom;");
		Listcell lc = new Listcell(Labels.getLabel("label_RecceiptDialog_AllocationType_Totals"));
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		lc = new Listcell();
		Label label = new Label(PennantAppUtil.amountFormate(dueAmount, formatter));
		label.setId(compId+"totalDue");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(paidAmount, formatter));
		label.setId(compId+"totalPaid");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(waivedAmount, formatter));
		label.setId(compId+"totalWaived");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);
		
		if(isPastDue){
			this.listBoxPastdues.appendChild(item);
		}else{
			this.listBoxManualAdvises.appendChild(item);
		}
	}
	
	/**
	 * Method for Resetting value of Customer To be Paid 
	 * @param purpose
	 * @param totCustPaid
	 */
	private void resetCustpaid(String purpose, BigDecimal totCustPaid, int formatter){
		
		if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			
			// Fetch Excess Amounts
			BigDecimal totExAutoPaid = BigDecimal.ZERO;
			if(listBoxExcess.getFellowIfAny("ExcessAmount_E") != null){
				CurrencyBox excessBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_E");
				totExAutoPaid = totExAutoPaid.add(PennantApplicationUtil.unFormateAmount(excessBox.getActualValue(), formatter));
			}
			
			// Fetch EMI in Advance Amount
			if(listBoxExcess.getFellowIfAny("ExcessAmount_A") != null){
				CurrencyBox emiInAdvBox = (CurrencyBox) listBoxExcess.getFellowIfAny("ExcessAmount_A");
				totExAutoPaid = totExAutoPaid.add(PennantApplicationUtil.unFormateAmount(emiInAdvBox.getActualValue(), formatter));
			}
			
			// Payable Amounts
			List<Listitem> payableItems = this.listBoxExcess.getItems();
			for (int i = 0; i < payableItems.size(); i++) {
				Listitem item = payableItems.get(i);
				if(item.getId().contains("Payable")){
					CurrencyBox payableAmount = (CurrencyBox) this.listBoxExcess.getFellowIfAny(item.getId().replaceAll("Item", "Amount"));
					totExAutoPaid = totExAutoPaid.add(PennantApplicationUtil.unFormateAmount(payableAmount.getActualValue(), formatter));
				}
			}
			
			this.custPaid.setValue(PennantApplicationUtil.formateAmount(totCustPaid.subtract(totExAutoPaid), formatter));
		}
	}
	
	/**
	 * Method for action Event of Changing Profit Amount/Schedule Profit on Schedule term
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onExcessPayableAmountChange(ForwardEvent event)throws Exception{
		logger.debug("Entering");
		
		percentageFees(false);
		
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		waivedAllocationMap = new HashMap<>();
		paidAllocationMap = new HashMap<>();
		
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
		setAutoAllocationPayments(true);
		
		logger.debug("Leaving");
	}

	private void percentageFees(boolean isFeeConsiderOnAmount) throws InterruptedException {
		logger.debug("Entering");
		
		List<FinFeeDetail> finFeeDetails = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			feesRecalculation(true, isFeeConsiderOnAmount);
		}else{
			setAutoAllocationPayments(false);
		}
		
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
		CurrencyBox allocateWaived = (CurrencyBox) list.get(2);
		
		BigDecimal paidAllocateAmt = PennantApplicationUtil.unFormateAmount(allocatePaid.getActualValue(), finFormatter);
		BigDecimal waivedAllocateAmt = PennantApplicationUtil.unFormateAmount(allocateWaived.getActualValue(), finFormatter);
		if(paidAllocateAmt.compareTo(pastdueAmt.subtract(waivedAllocateAmt)) > 0){
			paidAllocateAmt = pastdueAmt.subtract(waivedAllocateAmt);
			allocatePaid.setValue(PennantApplicationUtil.formateAmount(paidAllocateAmt, finFormatter));
		}
		
		BigDecimal tdsCalculated = BigDecimal.ZERO;
		if(StringUtils.equals(allocatePaid.getId(), "AllocatePaid_"+RepayConstants.ALLOCATION_NPFT)){
			if(getFinanceDetail().getFinScheduleData().getFinanceMain().isTDSApplicable()){

				if(this.listBoxPastdues.getFellowIfAny("AllocatePaid_"+RepayConstants.ALLOCATION_TDS) != null){
					BigDecimal tdsMultiplier = BigDecimal.ONE;
					BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
					int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/

					if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
						tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
					}

					CurrencyBox allocateTDSPaid = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocatePaid_"+RepayConstants.ALLOCATION_TDS);
					BigDecimal actPftAdjust = paidAllocateAmt.multiply(tdsMultiplier);
					if(getReceiptData().getAllocationMap().get(RepayConstants.ALLOCATION_PFT) != null){
						BigDecimal balPft = getReceiptData().getAllocationMap().get(RepayConstants.ALLOCATION_PFT);
						if(actPftAdjust.compareTo(balPft) > 0){
							actPftAdjust = balPft;
						}
					}
					tdsCalculated = actPftAdjust.subtract(paidAllocateAmt);
					allocateTDSPaid.setValue(PennantApplicationUtil.formateAmount(tdsCalculated, finFormatter));
					
					CurrencyBox allocatePFTPaid = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocatePaid_"+RepayConstants.ALLOCATION_PFT);
					allocatePFTPaid.setValue(PennantApplicationUtil.formateAmount(actPftAdjust, finFormatter));

					if(paidAllocationMap != null){ 
						if(paidAllocationMap.containsKey(allocateTDSPaid.getId())){
							paidAllocationMap.remove(allocateTDSPaid.getId());
						}
						if(paidAllocationMap.containsKey(allocatePFTPaid.getId())){
							paidAllocationMap.remove(allocatePFTPaid.getId());
						}
						paidAllocationMap.put(allocateTDSPaid.getId(), tdsCalculated);
						paidAllocationMap.put(allocatePFTPaid.getId(), actPftAdjust);
					}
				}
			}
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
		doFillAllocationDetail(null, allocateTypePaidMap, true, true);
		
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
		CurrencyBox allocatePaid = (CurrencyBox) list.get(1);
		CurrencyBox allocateWaived = (CurrencyBox) list.get(2);
		
		BigDecimal paidAllocateAmt = PennantApplicationUtil.unFormateAmount(allocatePaid.getActualValue(), finFormatter);
		BigDecimal waivedAllocateAmt = PennantApplicationUtil.unFormateAmount(allocateWaived.getActualValue(), finFormatter);
		if(waivedAllocateAmt.compareTo(pastdueAmt.subtract(paidAllocateAmt)) > 0){
			waivedAllocateAmt = pastdueAmt.subtract(paidAllocateAmt);
			allocateWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocateAmt, finFormatter));
		}
		
		// Setting to Map for future usage on Rendering
		if(waivedAllocationMap != null){ 
			if(waivedAllocationMap.containsKey(allocateWaived.getId())){
				waivedAllocationMap.remove(allocateWaived.getId());
			}
			waivedAllocationMap.put(allocateWaived.getId(), waivedAllocateAmt);
		}
		
		BigDecimal tdsCalculated = BigDecimal.ZERO;
		if(StringUtils.equals(allocateWaived.getId(), "AllocateWaived_"+RepayConstants.ALLOCATION_NPFT)){
			if(getFinanceDetail().getFinScheduleData().getFinanceMain().isTDSApplicable()){

				if(this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_TDS) != null){
					BigDecimal tdsMultiplier = BigDecimal.ONE;
					BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
					int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/

					if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
						tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
					}

					CurrencyBox allocateTDSWaived = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_TDS);
					BigDecimal actPftAdjust = waivedAllocateAmt.multiply(tdsMultiplier);
					if(getReceiptData().getAllocationMap().get(RepayConstants.ALLOCATION_PFT) != null){
						BigDecimal balPft = getReceiptData().getAllocationMap().get(RepayConstants.ALLOCATION_PFT);
						if(paidAllocationMap.containsKey("AllocatePaid_"+RepayConstants.ALLOCATION_PFT)){
							balPft = balPft.subtract(paidAllocationMap.get("AllocatePaid_"+RepayConstants.ALLOCATION_PFT));
						}
						if(actPftAdjust.compareTo(balPft) > 0){
							actPftAdjust = balPft;
						}
					}
					tdsCalculated = actPftAdjust.subtract(waivedAllocateAmt);
					allocateTDSWaived.setValue(PennantApplicationUtil.formateAmount(tdsCalculated, finFormatter));
					
					CurrencyBox allocatePFTWaived = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_PFT);
					allocatePFTWaived.setValue(PennantApplicationUtil.formateAmount(actPftAdjust, finFormatter));

					if(waivedAllocationMap != null){ 
						if(waivedAllocationMap.containsKey(allocateTDSWaived.getId())){
							waivedAllocationMap.remove(allocateTDSWaived.getId());
						}
						if(waivedAllocationMap.containsKey(allocatePFTWaived.getId())){
							waivedAllocationMap.remove(allocatePFTWaived.getId());
						}
						waivedAllocationMap.put(allocateTDSWaived.getId(), tdsCalculated);
						waivedAllocationMap.put(allocatePFTWaived.getId(), actPftAdjust);
					}
				}
			}else{
				if(this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_PFT) != null){
					CurrencyBox allocatePFTWaived = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_PFT);
					allocatePFTWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocateAmt, finFormatter));
				}
			}
		}
		
		if(StringUtils.equals(allocateWaived.getId(), "AllocateWaived_"+RepayConstants.ALLOCATION_PFT)){
			if(getFinanceDetail().getFinScheduleData().getFinanceMain().isTDSApplicable()){

				if(this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_TDS) != null){
					BigDecimal tdsMultiplier = BigDecimal.ONE;
					BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
					int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/

					if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
						tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
					}

					CurrencyBox allocateTDSWaived = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_TDS);
					BigDecimal actPftAdjust = waivedAllocateAmt.divide(tdsMultiplier , 0, RoundingMode.HALF_DOWN);
					if(getReceiptData().getAllocationMap().get(RepayConstants.ALLOCATION_NPFT) != null){
						BigDecimal balPft = getReceiptData().getAllocationMap().get(RepayConstants.ALLOCATION_NPFT);
						if(paidAllocationMap.containsKey("AllocatePaid_"+RepayConstants.ALLOCATION_NPFT)){
							balPft = balPft.subtract(paidAllocationMap.get("AllocatePaid_"+RepayConstants.ALLOCATION_NPFT));
						}
						if(actPftAdjust.compareTo(balPft) > 0){
							actPftAdjust = balPft;
						}
					}
					tdsCalculated = waivedAllocateAmt.subtract(actPftAdjust);
					allocateTDSWaived.setValue(PennantApplicationUtil.formateAmount(tdsCalculated, finFormatter));
					
					CurrencyBox allocateNPFTWaived = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_NPFT);
					allocateNPFTWaived.setValue(PennantApplicationUtil.formateAmount(actPftAdjust, finFormatter));

					if(waivedAllocationMap != null){ 
						if(waivedAllocationMap.containsKey(allocateTDSWaived.getId())){
							waivedAllocationMap.remove(allocateTDSWaived.getId());
						}
						if(waivedAllocationMap.containsKey(allocateNPFTWaived.getId())){
							waivedAllocationMap.remove(allocateNPFTWaived.getId());
						}
						waivedAllocationMap.put(allocateTDSWaived.getId(), tdsCalculated);
						waivedAllocationMap.put(allocateNPFTWaived.getId(), actPftAdjust);
					}
				}
			}else{
				
				if(this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_NPFT) != null){
					CurrencyBox allocateNPFTWaived = (CurrencyBox) this.listBoxPastdues.getFellowIfAny("AllocateWaived_"+RepayConstants.ALLOCATION_NPFT);
					allocateNPFTWaived.setValue(PennantApplicationUtil.formateAmount(waivedAllocateAmt, finFormatter));
				}
			}
		}
		
		List<String> waivedBoxKeys = new ArrayList<>(waivedAllocationMap.keySet());
		BigDecimal totalPDWaived = BigDecimal.ZERO;
		BigDecimal totalMAWaived = BigDecimal.ZERO;
		for (int i = 0; i < waivedBoxKeys.size(); i++) {
			String waivedBoxId = waivedBoxKeys.get(i);
			if(waivedBoxId.startsWith("AllocateWaived_")){
				if(StringUtils.equals(waivedBoxId, "AllocateWaived_"+RepayConstants.ALLOCATION_TDS) || 
						StringUtils.equals(waivedBoxId, "AllocateWaived_"+RepayConstants.ALLOCATION_PFT)){
					// Nothing To do
				}else{
					totalPDWaived = totalPDWaived.add(waivedAllocationMap.get(waivedBoxId));
				}
			}else {
				totalMAWaived = totalMAWaived.add(waivedAllocationMap.get(waivedBoxId));
			}
		}
		
		// Totals Pastdue Waived Amount Resetting
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalWaived") != null){
			Label label = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalWaived");
			label.setValue(PennantApplicationUtil.amountFormate(totalPDWaived, finFormatter));
		}
		
		// Totals Manual Advises Waived Amount Resetting
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalWaived") != null){
			Label label = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalWaived");
			label.setValue(PennantApplicationUtil.amountFormate(totalMAWaived, finFormatter));
		}
		
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
		
		if(this.row_RealizationDate.isVisible() && !this.realizationDate.isDisabled()){
			this.realizationDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptRealizationDialog_RealizationDate.value"), 
					true, this.valueDate.getValue(), DateUtility.getAppDate(), true));
		}
		
		if (StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			
			if(!this.chequeAcNo.isReadonly()){
				this.chequeAcNo.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_ChequeAccountNo.value"), null, false));
			}
		}
		
		if(!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_EXCESS)){
			if(!this.fundingAccount.isReadonly()){
				this.fundingAccount.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_FundingAccount.value"), null, true));
			}

			if(!this.receivedDate.isDisabled()){
				Date prvMaxReceivedDate = getReceiptService().getMaxReceiptDate(getFinanceMain().getFinReference());
				if(prvMaxReceivedDate == null){
					prvMaxReceivedDate = getFinanceMain().getFinStartDate();
				}
				this.receivedDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ReceivedDate.value"), true, 
						prvMaxReceivedDate, DateUtility.getAppDate(), true));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD) ||
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_CHEQUE)){
			
			if(!this.favourNo.isReadonly()){
				String label = Labels.getLabel("label_ReceiptDialog_ChequeFavourNo.value");
				if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_DD)){
					label = Labels.getLabel("label_ReceiptDialog_DDFavourNo.value");
				}
				this.favourNo.setConstraint(new PTStringValidator(label, PennantRegularExpressions.REGEX_NUMERIC, true, 1, 6));
			}
			
			if(!this.valueDate.isDisabled()){
				this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_ValueDate.value"), true, 
						getFinanceMain().getFinStartDate(), DateUtility.getAppDate(), true));
			}
			
			if(!this.bankCode.isReadonly()){
				this.bankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_BankCode.value"), null, true, true));
			}
			
			if(!this.favourName.isReadonly()){
				this.favourName.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_favourName.value"),
						PennantRegularExpressions.REGEX_FAVOURING_NAME, true));
			}
			
			if(!this.depositDate.isDisabled()){
				this.depositDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_DepositDate.value"), true, 
						getFinanceMain().getFinStartDate(), DateUtility.getAppDate(), true));
			}
			
			if(!this.depositNo.isReadonly()){
				this.depositNo.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_depositNo.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}
		
		if(StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_NEFT) || 
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_RTGS) || 
				StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_IMPS)){
			
			if(!this.transactionRef.isReadonly()){
				this.transactionRef.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_tranReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
			}
		}
		
		if(!StringUtils.equals(recptMode, RepayConstants.RECEIPTMODE_EXCESS)){
			if(!this.paymentRef.isReadonly()){
				this.paymentRef.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_paymentReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
			}
		}
		
		if(!this.remarks.isReadonly()){
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_Remarks.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		
		if (this.row_BounceReason.isVisible() && !this.bounceCode.isReadonly()) {
			this.bounceCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_BounceReason.value"), null,true, true));
		}
		
		if (this.row_CancelReason.isVisible() && !this.cancelReason.isReadonly()) {
			this.cancelReason.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_CancelReason.value"), null,true, true));
		}

		if(this.row_BounceRemarks.isVisible() && !this.bounceRemarks.isReadonly()){
			this.bounceRemarks.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptDialog_BounceRemarks.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		
		if (this.row_BounceRemarks.isVisible() && !this.bounceDate.isDisabled() ) {
			this.bounceDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptDialog_BounceDate.value"),
					true, null, null, true));
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
		Date curBussDate = DateUtility.getAppDate();
		
		FinReceiptHeader header = getReceiptHeader();
		header.setReceiptDate(curBussDate);
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
		
		String status = "";
		try {
			if(!isReadOnly("ReceiptDialog_receiptModeStatus") && 
					isValidComboValue(this.receiptModeStatus, Labels.getLabel("label_ReceiptDialog_ReceiptModeStatus.value"))){
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
			if(bounce == null){
				bounce = new ManualAdvise();
			}
			
			bounce.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
			bounce.setFinReference(header.getReference());
			bounce.setFeeTypeID(0);
			bounce.setSequence(0);
			
			try {
				bounce.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.bounceCharge.getActualValue(), CurrencyUtil.getFormat(header.getFinCcy())));
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
		}else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_CANCEL)) {
			
			try {
				header.setCancelReason(this.cancelReason.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
			
		}else if (StringUtils.equals(status, RepayConstants.PAYSTATUS_REALIZED)) {
			
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
			this.receivedDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			header.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Finance Fee Details
		if (getFinFeeDetailListCtrl() != null) {
			getFinFeeDetailListCtrl().processFeeDetails(getReceiptData().getFinanceDetail().getFinScheduleData());
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
	public FinanceDetail onExecuteStageAccDetail() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		getFinanceDetail().setModuleDefiner(FinanceConstants.FINSER_EVENT_RECEIPT);
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
		Date dateValueDate = DateUtility.getAppDate();
		if(this.receivedDate.getValue() != null){
			dateValueDate = this.receivedDate.getValue();
		}

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain, getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails(), profitDetail, eventCode, dateValueDate, dateValueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		accrualService.calProfitDetails(finMain, receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), newProfitDetail, dateValueDate);
		amountCodes.setBpi(finMain.getBpiAmount());
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));

		List<ReturnDataSet> returnSetEntries = new ArrayList<>();
		BigDecimal totRpyPri = BigDecimal.ZERO;
		boolean feesExecuted = false;
		boolean pftChgExecuted = false;
		for (FinReceiptDetail receiptDetail : getReceiptHeader().getReceiptDetails()) {

			for (FinRepayHeader repayHeader : receiptDetail.getRepayHeaders()) {

				if(!StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent()) &&
						!StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent()) &&
						!StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())){

					// Accounting Postings Process Execution
					aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
					amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
					amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
					amountCodes.setToExcessAmt(BigDecimal.ZERO);
					amountCodes.setToEmiAdvance(BigDecimal.ZERO);
					if(StringUtils.equals(repayHeader.getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS)){
						amountCodes.setToExcessAmt(repayHeader.getRepayAmount());
					}else {
						amountCodes.setToEmiAdvance(repayHeader.getRepayAmount());
					}

					aeEvent.getAcSetIDList().clear();
					if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
						aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
					} else {
						aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
					}

					HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues(); 
					if(!feesExecuted && StringUtils.equals(getReceiptHeader().getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY)){
						feesExecuted = true;
						prepareFeeRulesMap(amountCodes, dataMap,receiptDetail.getPaymentType());
					}
					aeEvent.setDataMap(dataMap);

					// Accounting Entry Execution
					aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
					returnSetEntries.addAll(aeEvent.getReturnDataSet());

					amountCodes.setToExcessAmt(BigDecimal.ZERO);
					amountCodes.setToEmiAdvance(BigDecimal.ZERO);

					continue;
				}

				List<RepayScheduleDetail> repaySchdList = repayHeader.getRepayScheduleDetails();
				for (RepayScheduleDetail rsd : repaySchdList) {

					//Set Repay Amount Codes
					amountCodes.setRpTot(amountCodes.getRpTot().add(rsd.getPrincipalSchdPayNow()).add(rsd.getProfitSchdPayNow()).add(rsd.getLatePftSchdPayNow()));
					amountCodes.setRpPft(amountCodes.getRpPft().add(rsd.getProfitSchdPayNow()).add(rsd.getLatePftSchdPayNow()));
					amountCodes.setRpPri(amountCodes.getRpPri().add(rsd.getPrincipalSchdPayNow()));
					amountCodes.setRpTds(amountCodes.getRpTds().add(rsd.getTdsSchdPayNow()));
					amountCodes.setPenaltyWaived(BigDecimal.ZERO);
					totRpyPri = totRpyPri.add(rsd.getPrincipalSchdPayNow());

					// Penalties
					amountCodes.setPenaltyPaid(amountCodes.getPenaltyPaid().add(rsd.getPenaltyPayNow()));
					amountCodes.setPenaltyWaived(amountCodes.getPenaltyWaived().add(rsd.getWaivedAmt()));

					// Fee Details
					amountCodes.setSchFeePay(amountCodes.getSchFeePay().add(rsd.getSchdFeePayNow()));
					amountCodes.setInsPay(amountCodes.getInsPay().add(rsd.getSchdInsPayNow()));

					// Waived Amounts
					amountCodes.setPriWaived(amountCodes.getPriWaived().add(rsd.getPriSchdWaivedNow()));
					amountCodes.setPftWaived(amountCodes.getPftWaived().add(rsd.getPftSchdWaivedNow()).add(rsd.getLatePftSchdWaivedNow()));
					amountCodes.setFeeWaived(amountCodes.getFeeWaived().add(rsd.getSchdFeeWaivedNow()));
					amountCodes.setInsWaived(amountCodes.getInsWaived().add(rsd.getSchdInsWaivedNow()));
				}

				amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
				amountCodes.setExcessAmt(BigDecimal.ZERO);
				amountCodes.setEmiInAdvance(BigDecimal.ZERO);
				amountCodes.setPayableAdvise(BigDecimal.ZERO);
				if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS)){
					amountCodes.setExcessAmt(amountCodes.getRpTot());
					amountCodes.setRpExcessTds(amountCodes.getRpTds());
					amountCodes.setRpTds(BigDecimal.ZERO);
					amountCodes.setRpTot(BigDecimal.ZERO);
				}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
					amountCodes.setEmiInAdvance(amountCodes.getRpTot());
					amountCodes.setRpEmiAdvTds(amountCodes.getRpTds());
					amountCodes.setRpTds(BigDecimal.ZERO);
					amountCodes.setRpTot(BigDecimal.ZERO);
				}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
					amountCodes.setPayableAdvise(amountCodes.getRpTot());
					amountCodes.setRpPayableTds(amountCodes.getRpTds());
					amountCodes.setRpTds(BigDecimal.ZERO);
					amountCodes.setRpTot(BigDecimal.ZERO);
				}

				// Accounting Event Code Setting
				aeEvent.getAcSetIDList().clear();
				if(StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_SCHDRPY)){
					eventCode = AccountEventConstants.ACCEVENT_REPAY;
				}else if(StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYRPY)){
					eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
					if(pftChgExecuted){
						amountCodes.setPftChg(BigDecimal.ZERO);
					}
					pftChgExecuted = true;
				}else if(StringUtils.equals(repayHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
					if(pftChgExecuted){
						amountCodes.setPftChg(BigDecimal.ZERO);
					}
					pftChgExecuted = true;
				}

				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION));
				} else {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));
				}

				aeEvent.setAccountingEvent(eventCode);
				HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues(); 
				if(!feesExecuted && (StringUtils.equals(getReceiptHeader().getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY) ||
						(!StringUtils.equals(getReceiptHeader().getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY) &&
								StringUtils.equals(getReceiptHeader().getReceiptPurpose(), repayHeader.getFinEvent())))){
					feesExecuted = true;
					prepareFeeRulesMap(amountCodes, dataMap,receiptDetail.getPaymentType());
				}
				aeEvent.setDataMap(dataMap);
				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

				if(amountCodes.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0 || 
						amountCodes.getPenaltyWaived().compareTo(BigDecimal.ZERO) > 0){

					if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS)){
						amountCodes.setExPenaltyPaid(amountCodes.getPenaltyPaid());
						amountCodes.setExPenaltyWaived(amountCodes.getPenaltyWaived());
						amountCodes.setPenaltyPaid(BigDecimal.ZERO);
						amountCodes.setPenaltyWaived(BigDecimal.ZERO);
					}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
						amountCodes.setEAPenaltyPaid(amountCodes.getPenaltyPaid());
						amountCodes.setEAPenaltyWaived(amountCodes.getPenaltyWaived());
						amountCodes.setPenaltyPaid(BigDecimal.ZERO);
						amountCodes.setPenaltyWaived(BigDecimal.ZERO);
					}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
						amountCodes.setPAPenaltyPaid(amountCodes.getPenaltyPaid());
						amountCodes.setPAPenaltyWaived(amountCodes.getPenaltyWaived());
						amountCodes.setPenaltyPaid(BigDecimal.ZERO);
						amountCodes.setPenaltyWaived(BigDecimal.ZERO);
					}

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
				amountCodes.setFeeWaived(BigDecimal.ZERO);
				amountCodes.setInsWaived(BigDecimal.ZERO);
				amountCodes.setExcessAmt(BigDecimal.ZERO);
				amountCodes.setEmiInAdvance(BigDecimal.ZERO);
				amountCodes.setPayableAdvise(BigDecimal.ZERO);
				amountCodes.setPenaltyPaid(BigDecimal.ZERO);
				amountCodes.setPenaltyWaived(BigDecimal.ZERO);
				amountCodes.setRpTds(BigDecimal.ZERO);
				amountCodes.setRpExcessTds(BigDecimal.ZERO);
				amountCodes.setRpEmiAdvTds(BigDecimal.ZERO);
				amountCodes.setRpPayableTds(BigDecimal.ZERO);
				amountCodes.setExPenaltyPaid(BigDecimal.ZERO);
				amountCodes.setExPenaltyWaived(BigDecimal.ZERO);
				amountCodes.setEAPenaltyPaid(BigDecimal.ZERO);
				amountCodes.setEAPenaltyWaived(BigDecimal.ZERO);
				amountCodes.setPAPenaltyPaid(BigDecimal.ZERO);
				amountCodes.setPAPenaltyWaived(BigDecimal.ZERO);

			}

			// Manual Advise Postings
			List<ManualAdviseMovements> movements = receiptDetail.getAdvMovements();
			if(movements != null && !movements.isEmpty()){

				// Summing Same Type of Fee Types to Single Field
				HashMap<String, BigDecimal> movementMap = new HashMap<>(); 
				for (int i = 0; i < movements.size(); i++) {
					ManualAdviseMovements movement = movements.get(i);

					BigDecimal amount = BigDecimal.ZERO;
					if(movementMap.containsKey(movement.getFeeTypeCode() + "_P")){
						amount = movementMap.get(movement.getFeeTypeCode() + "_P");
					}
					movementMap.put(movement.getFeeTypeCode() + "_P", amount.add(movement.getPaidAmount()));

					amount = BigDecimal.ZERO;
					if(movementMap.containsKey(movement.getFeeTypeCode() + "_W")){
						amount = movementMap.get(movement.getFeeTypeCode() + "_W");
					}
					movementMap.put(movement.getFeeTypeCode() + "_W",  amount.add(movement.getWaivedAmount()));

					String payType = "";
					if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS)){
						payType = "EX_";
					}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
						payType = "EA_";
					}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
						payType = "PA_";
					}else{
						payType = "PB_";
					}
					amount = BigDecimal.ZERO;
					if(movementMap.containsKey(payType + movement.getFeeTypeCode() + "_P")){
						amount = movementMap.get(payType + movement.getFeeTypeCode() + "_P");
					}
					movementMap.put(payType + movement.getFeeTypeCode() + "_P",  amount.add(movement.getPaidAmount()));
				}

				// Accounting Postings Process Execution
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_REPAY);
				amountCodes.setPartnerBankAc(receiptDetail.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(receiptDetail.getPartnerBankAcType());
				aeEvent.getAcSetIDList().clear();
				if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), 
							AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_PROMOTION));
				} else {
					aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), 
							AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE));
				}

				HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues(); 
				dataMap.putAll(movementMap);
				aeEvent.setDataMap(dataMap);

				// Accounting Entry Execution
				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
				returnSetEntries.addAll(aeEvent.getReturnDataSet());

			}
		}

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			if(StringUtils.isNotEmpty(finMain.getFinCommitmentRef())){
				Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				int format = CurrencyUtil.getFormat(commitment.getCmtCcy());

				if(commitment != null && commitment.isRevolving()){
					aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_CMTRPY);
					amountCodes.setCmtAmt(BigDecimal.ZERO);
					amountCodes.setChgAmt(BigDecimal.ZERO);
					amountCodes.setDisburse(BigDecimal.ZERO);
					amountCodes.setRpPri(CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(), totRpyPri));

					HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
					aeEvent.setDataMap(dataMap);
					aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);

					//FIXME: PV: 04MAY17 why separate method is required for commitment dialog show
					getAccountingDetailDialogCtrl().doFillCmtAccounting(aeEvent.getReturnDataSet(), format);
					getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList().addAll(aeEvent.getReturnDataSet()); 
				} 
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding Fee details to Amount codes on Accounting execution
	 * @param amountCodes
	 * @param dataMap
	 */
	private void prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap, String payType) {
		logger.debug("Entering");

		List<FinFeeDetail> finFeeDetailList = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (finFeeDetailList != null) {
			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if(!finFeeDetail.isRcdVisible()){
					continue;
				}
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
				
				if(StringUtils.equals(payType, RepayConstants.PAYTYPE_EXCESS)){
					payType = "EX_";
				}else if(StringUtils.equals(payType, RepayConstants.PAYTYPE_EMIINADV)){
					payType = "EA_";
				}else if(StringUtils.equals(payType, RepayConstants.PAYTYPE_PAYABLE)){
					payType = "PA_";
				}else{
					payType = "PB_";
				}
				dataMap.put(payType + finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());
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

			//Get Finance Type Transaction Entry By event
			
			Long accountSetId = Long.MIN_VALUE;
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			String finType = finMain.getFinType();;
			int moduleID = FinanceConstants.MODULEID_FINTYPE;
			if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
				finType = finMain.getPromotionCode();
				moduleID = FinanceConstants.MODULEID_PROMOTION;
			}
			
			String purpose = getComboboxValue(receiptPurpose);
			if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				accountSetId = AccountingConfigCache.getAccountSetID(finType, AccountEventConstants.ACCEVENT_EARLYSTL, moduleID);
			} else if (StringUtils.equals(purpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				accountSetId = AccountingConfigCache.getAccountSetID(finType, AccountEventConstants.ACCEVENT_EARLYPAY, moduleID);
			} else {
				accountSetId = AccountingConfigCache.getAccountSetID(finType, AccountEventConstants.ACCEVENT_REPAY, moduleID);
			}

			//Accounting Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", financeDetail);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("acSetID", accountSetId);
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
		afinanceMain.setWorkflowId(getWorkFlowId());

		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		aRepayData.getFinanceDetail().getFinScheduleData().setFinanceMain(afinanceMain);

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
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
						
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReversal)) {
						auditHeader = getReceiptService().doReversal(auditHeader);
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

		//setRepaySchdList(sortRpySchdDetails(repaySchdList));
		refundMap = new LinkedHashMap<String, RepayScheduleDetail>();
		this.listBoxPayment.getItems().clear();
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
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getTdsSchdPayNow(), finFormatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getLatePftSchdPayNow(), finFormatter));
				totalLatePft = totalLatePft.add(repaySchd.getLatePftSchdPayNow());
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
						.add(repaySchd.getPenaltyPayNow()).add(repaySchd.getLatePftSchdPayNow())
						.subtract(refundPft);
				lc = new Listcell(PennantAppUtil.amountFormate(netPay, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdInsBal()).add(repaySchd.getSchdFeeBal())
						.add(repaySchd.getSchdSuplRentBal()).add(repaySchd.getSchdIncrCostBal());
						
				lc = new Listcell(PennantAppUtil.amountFormate(netBalance.subtract(netPay.subtract(
						repaySchd.getPenaltyPayNow()).subtract(repaySchd.getLatePftSchdPayNow())), finFormatter));
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

		//Summary Details
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
			fillListItem(Labels.getLabel("listcell_totalRefund.label"), paymentMap.get("totalRefund"));
		}else{
			this.listheader_Refund.setVisible(false);
		}
		if (paymentMap.get("totalCharge").compareTo(BigDecimal.ZERO) > 0) {
			this.listheader_Penalty.setVisible(true);
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalCharge"));
			fillListItem(Labels.getLabel("listcell_totalPenalty.label"), paymentMap.get("totalCharge"));
		}else{
			this.listheader_Penalty.setVisible(false);
		}
		if (paymentMap.get("totalPft").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPft"));
			fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), paymentMap.get("totalPft"));
		}
		if (paymentMap.get("totalTds").compareTo(BigDecimal.ZERO) > 0) {
			fillListItem(Labels.getLabel("listcell_totalTdsPayNow.label"), paymentMap.get("totalTds"));
			this.listheader_Tds.setVisible(true);
		}else{
			this.listheader_Tds.setVisible(false);
		}
		if (paymentMap.get("totalLatePft").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalLatePft"));
			this.listheader_LatePft.setVisible(true);
			fillListItem(Labels.getLabel("listcell_totalLatePftPayNow.label"), paymentMap.get("totalLatePft"));
		}else{
			this.listheader_LatePft.setVisible(false);
		}
		if (paymentMap.get("totalPri").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));
			fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"));
		}

		if (paymentMap.get("insPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("insPaid"));
			this.listheader_InsPayment.setVisible(true);
			fillListItem(Labels.getLabel("listcell_insFeePayNow.label"), paymentMap.get("insPaid"));
		}else{
			this.listheader_InsPayment.setVisible(false);
		}
		if (paymentMap.get("schdFeePaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdFeePaid"));
			this.listheader_SchdFee.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdFeePayNow.label"), paymentMap.get("schdFeePaid"));
		}else{
			this.listheader_SchdFee.setVisible(false);
		}
		if (paymentMap.get("schdSuplRentPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdSuplRentPaid"));
			this.listheader_SuplRent.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdSuplRentPayNow.label"), paymentMap.get("schdSuplRentPaid"));
		}else{
			this.listheader_SuplRent.setVisible(false);
		}
		if (paymentMap.get("schdIncrCostPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdIncrCostPaid"));
			this.listheader_IncrCost.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdIncrCostPayNow.label"), paymentMap.get("schdIncrCostPaid"));
		}else{
			this.listheader_IncrCost.setVisible(false);
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
	private boolean isValidateData(boolean isCalProcess) throws InterruptedException, InterfaceException {
 		logger.debug("Entering");
		
		// Validate Field Details
		if(isCalProcess){
			doClearMessage();
			doSetValidation();
			doWriteComponentsToBean();
		}

		Date receiptValueDate = DateUtility.getAppDate();
		if(this.receivedDate.getValue() != null){
			receiptValueDate = this.receivedDate.getValue();
		}
		
		String tempReceiptPurpose = getComboboxValue(this.receiptPurpose);
		
		if (getFinanceDetail().getFinScheduleData().getFinanceMain() != null &&
				!StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
				&& receiptValueDate.compareTo(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Date"));
			return false;
		}
		
		// Entered Receipt Amount Match case test with allocations
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal totReceiptAmount = getTotalReceiptAmount(true);
		if(totReceiptAmount.compareTo(BigDecimal.ZERO) == 0){
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_NoReceiptAmount"));
			return false;
		}
		
		// Not allowed to pay more amount and adjust balance to Excess / EMI In Advance
		BigDecimal rcptAmount = PennantApplicationUtil.unFormateAmount(receiptAmount.getValidateValue(), formatter);
		if(totReceiptAmount.compareTo(rcptAmount) > 0 && this.excessAdjustTo.getSelectedIndex() > 0){
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_NoExcessAmount"));
			return false;
		}
		
		BigDecimal totalDue = BigDecimal.ZERO;
		BigDecimal totalPaid = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		// Past due Details
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalDue") != null){
			Label due = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalDue");
			totalDue = PennantApplicationUtil.unFormateAmount(new BigDecimal(due.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalPaid") != null){
			Label paid = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalPaid");
			totalPaid = PennantApplicationUtil.unFormateAmount(new BigDecimal(paid.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxPastdues.getFellowIfAny("allocation_totalWaived") != null){
			Label waived = (Label) this.listBoxPastdues.getFellowIfAny("allocation_totalWaived");
			totalWaived = PennantApplicationUtil.unFormateAmount(new BigDecimal(waived.getValue().replaceAll(",", "")), formatter);
		}
		
		BigDecimal totalAdvDue = BigDecimal.ZERO;
		BigDecimal totalAdvPaid = BigDecimal.ZERO;
		BigDecimal totalAdvWaived = BigDecimal.ZERO;
		// Manual Advises
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalDue") != null){
			Label due = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalDue");
			totalAdvDue = PennantApplicationUtil.unFormateAmount(new BigDecimal(due.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalPaid") != null){
			Label paid = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalPaid");
			totalAdvPaid = PennantApplicationUtil.unFormateAmount(new BigDecimal(paid.getValue().replaceAll(",", "")), formatter);
		}
		
		if(this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalWaived") != null){
			Label waived = (Label) this.listBoxManualAdvises.getFellowIfAny("manAdvise_totalWaived");
			totalAdvWaived = PennantApplicationUtil.unFormateAmount(new BigDecimal(waived.getValue().replaceAll(",", "")), formatter);
		}
		
		// User entered Receipt amounts and paid on manual Allocation validation
		BigDecimal remBal = totReceiptAmount.subtract(totalPaid).subtract(totalAdvPaid); 
		if(remBal.compareTo(BigDecimal.ZERO) < 0){
			MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_InsufficientAmount"));
			return false;
		}
		
		if(!isCalProcess){
			return true;
		}
		
		// Finance Should not allow for Partial Settlement & Early settlement when Maturity Date reaches Current application Date
		if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) ||
				StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){ 

			if((StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) && 
					DateUtility.compare(getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), receiptValueDate) < 0) ||
					(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE) &&
							DateUtility.compare(getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), receiptValueDate) < 0)){
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_MaturityDate" ,
						new String[] { PennantAppUtil.getlabelDesc(tempReceiptPurpose, PennantStaticListUtil.getReceiptPurpose())}));
				return false;
			}
		}

		// No excess amount validation on partial Settlement
		if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){ 
			if(remBal.compareTo(BigDecimal.ZERO) <= 0){
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_PartialSettlement"));
				return false;
			}else if(totalDue.subtract(totalPaid.subtract(totalWaived)).compareTo(BigDecimal.ZERO) > 0){
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_PastAmount_PartialSettlement"));
				return false;
			}else {

				// Check the max Schedule payment amount
				List<FinanceScheduleDetail> scheduleList = getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
				BigDecimal closingBal = null;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) >= 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}
					if (DateUtility.compare(receiptValueDate, curSchd.getSchDate()) == 0 || closingBal == null) {
						closingBal = closingBal.subtract(curSchd.getSchdPriPaid().subtract(curSchd.getSchdPftPaid()));
						break;
					}
				}
				
				if (closingBal != null) {
					if (remBal.compareTo(closingBal) >= 0) {
						MessageUtil.showError(Labels.getLabel("FIELD_IS_LESSER",
								new String[] { Labels.getLabel("label_ReceiptDialog_Valid_TotalPartialSettlementAmount"),
										PennantApplicationUtil.amountFormate(closingBal, formatter) }));
						return false;
					}
				} 
			}
		}
		
		// Early settlement Validation , if entered amount not sufficient with paid and waived amounts
		if(StringUtils.equals(tempReceiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			BigDecimal earlySettleBal = totReceiptAmount.subtract(totalDue.subtract(totalWaived)).subtract(totalAdvDue.subtract(totalAdvWaived));
			if(earlySettleBal.compareTo(BigDecimal.ZERO) < 0){
				MessageUtil.showError(Labels.getLabel("label_ReceiptDialog_Valid_Amount_EarlySettlement"));
				return false;
			}
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

		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(financeMain.getFinReference());;
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
	 * Method to Update Reject Finance Details
	 */
	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");
	}
	
	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendFeeDetailTab(boolean isLoadProcess) throws InterruptedException {
		logger.debug("Entering");
		
		try {
			Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE));
			
			if (tab == null) {
				createTab(AssetConstants.UNIQUE_ID_FEE, isLoadProcess);
				tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE));
				Tabpanel tabPanel = getTabpanel(AssetConstants.UNIQUE_ID_FEE);
				if (tabPanel != null) {
					tabPanel.getChildren().clear();
				}

				HashMap<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEE));
				map.put("moduleDefiner", this.moduleDefiner);
				map.put("eventCode", eventCode);
				map.put("isReceiptsProcess", isReceiptsProcess);
				map.put("numberOfTermsLabel", Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"));
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FEE), map);
			} 
			
			tab.setVisible(isLoadProcess);
			
			if (isLoadProcess) {
				if (getFinFeeDetailListCtrl() != null) {
					getFinFeeDetailListCtrl().setEventCode(eventCode);
					getFinFeeDetailListCtrl().setReceiptsProcess(isReceiptsProcess);
					financeDetail.getFinScheduleData().setFeeEvent(eventCode);
					getFinFeeDetailListCtrl().doWriteBeanToComponents(financeDetail);
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}
	
	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}
	
	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}
	
	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}
	
	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
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

	public List<FinExcessAmountReserve> getExcessReserveList() {
		return excessReserveList;
	}

	public void setExcessReserveList(List<FinExcessAmountReserve> excessReserveList) {
		this.excessReserveList = excessReserveList;
	}

	public List<ManualAdvise> getPayableList() {
		return payableList;
	}

	public void setPayableList(List<ManualAdvise> payableList) {
		this.payableList = payableList;
	}

	public List<ManualAdviseReserve> getPayableReserveList() {
		return payableReserveList;
	}

	public void setPayableReserveList(List<ManualAdviseReserve> payableReserveList) {
		this.payableReserveList = payableReserveList;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}
	
	/**
	 * to get the Remaining Balance After Allocation Amount
	 * @return
	 */
	public BigDecimal getRemBalAfterAllocationAmt() {	//Used in Fees Execution
		return remBalAfterAllocation.getValue();
	}

	/**
	 * to get the Customer Paid Amount
	 * @return
	 */
	public BigDecimal getCustPaidAmt() {	//Used in Fees Execution
		return this.custPaid.getValue();
	}
}