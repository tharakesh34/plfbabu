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
 * FileName    		:  ManualPaymentDialogCtrl.java                           
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
package com.pennant.webui.financemanagement.payments;

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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.ChartType;
import com.pennant.CurrencyBox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.EarlySettlementReportData;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.core.EventManager.Notify;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.StageAccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Payments/ManualPayment.zul
 */
public class ManualPaymentDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long								serialVersionUID					= 966281186831332116L;
	private static final Logger								logger								= Logger.getLogger(ManualPaymentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window										window_ManualPaymentDialog;
	protected Borderlayout									borderlayout_ManualPayment;
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

	//Repayment Details
	protected Textbox										finType1;
	protected Textbox										finReference1;
	protected Textbox										finCcy1;
	protected Textbox										lovDescFinCcyName1;
	protected Longbox										custID1;
	protected Textbox										lovDescCustCIF1;
	protected Label											custShrtName1;
	protected Textbox										finBranch1;
	protected Textbox										lovDescFinBranchName1;
	protected CurrencyBox									rpyAmount;
	protected AccountSelectionBox							repayAccount;
	protected Decimalbox									priPayment;
	protected Decimalbox									pftPayment;
	protected Decimalbox									insPayment;
	protected Decimalbox									schdFeeAmount;
	protected Decimalbox									crInsAmount;
	protected Decimalbox									suplRentAmount;
	protected Decimalbox									incrCostAmount;
	protected Row											row_InsFee;
	protected Row											row_SchdFee;
	protected Row											row_SuplRentNIncrCost;
	protected Decimalbox									totPenaltyAmt;
	protected Combobox										earlyRpyEffectOnSchd;
	protected Decimalbox									totRefundAmt;
	protected BigDecimal									oldVar_totRefundAmt;
	protected Decimalbox									totWaivedAmt;
	protected Label											label_PaymentDialog_InsRefundAmt;
	protected Hbox											hbox_insRefundAmt;
	protected Decimalbox									insRefundAmt;
	protected Decimalbox									actInsRefundAmt;
	protected Row											row_paidByCustomer;
	protected Decimalbox									paidByCustomer;
	protected Row											row_payApportionment;
	protected Combobox										paymentApportionment;
	protected Decimalbox									unEarnedAmount;

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

	//Early Settlement Inquiry Fields
	protected Label											label_PaymentDialog_RepayAccount;
	protected Hbox											hbox_repayAccount;
	protected Row											row_EarlyRepayEffectOnSchd;
	protected Row											row_EarlySettleDate;
	protected Datebox										earlySettlementDate;
	protected Decimalbox									earlySettlementBal;
	protected Combobox										earlySettlementTillDate;
	protected Label											label_PaymentDialog_EarlySettlementTillDate;
	protected Hbox											hbox_esTilllDate;

	protected Listbox										listBoxSchedule;

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
	protected Grid											grid_Repayment;
	protected Tabbox										tabbox;
	protected Button										btnPrint;

	protected Tab											summaryDetailsTab;
	protected Tab											repaymentDetailsTab;
	protected Tab											effectiveScheduleTab;
	private BigDecimal										financeAmount;

	//Buttons
	protected Button										btnPay;
	protected Button										btnChangeRepay;
	protected Button										btnCalcRepayments;
	protected Listbox										listBoxPayment;

	private transient OverdueChargeRecoveryService			overdueChargeRecoveryService;
	private transient AccountsService						accountsService;
	private transient AccountInterfaceService				accountInterfaceService;
	private transient RuleService							ruleService;
	private transient CustomerDetailsService				customerDetailsService;
	private transient ManualPaymentService					manualPaymentService;
	private transient ProvisionService						provisionService;
	private transient FinanceDetailService					financeDetailService;
	private transient RuleExecutionUtil						ruleExecutionUtil;
	private transient AccountEngineExecution				engineExecution;
	private transient CommitmentService						commitmentService;
	private transient RepayCalculator						repayCalculator;
	private transient FinanceReferenceDetailService			financeReferenceDetailService;

	private transient AccountingDetailDialogCtrl			accountingDetailDialogCtrl			= null;
	private transient DocumentDetailDialogCtrl				documentDetailDialogCtrl			= null;
	private transient AgreementDetailDialogCtrl				agreementDetailDialogCtrl			= null;
	private transient CustomerDialogCtrl					customerDialogCtrl					= null;
	private transient StageAccountingDetailDialogCtrl		stageAccountingDetailDialogCtrl		= null;
	private transient FinanceCheckListReferenceDialogCtrl	financeCheckListReferenceDialogCtrl	= null;

	private FinRepayHeader									finRepayHeader						= null;
	private FinanceDetail									financeDetail;
	private FinanceType										financeType;
	private RepayData										repayData							= null;
	private RepayMain										repayMain							= null;
	private List<RepayScheduleDetail>						repaySchdList						= null;

	private IAccounts										iAccount;
	private LinkedHashMap<String, RepayScheduleDetail>		refundMap;
	private boolean											isRefundExceeded					= false;

	private boolean											isSchdRecal							= false;
	private boolean											refundAmtValidated					= true;

	private final List<ValueLabel>							profitDayList						= PennantStaticListUtil
																										.getProfitDaysBasis();
	private final List<ValueLabel>							earlyRpyEffectList					= PennantStaticListUtil
																										.getEarlyPayEffectOn();
	private final List<ValueLabel>							payApprtnList						= PennantStaticListUtil
																										.getPaymentApportionment();

	private MailUtil										mailUtil;
	private List<ChartDetail> chartDetailList = new ArrayList<ChartDetail>(); // storing ChartDetail for feature use
	/**
	 * default constructor.<br>
	 */
	public ManualPaymentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualPaymentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ManualPaymentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ManualPaymentDialog);

		try {
			if (arguments.containsKey("repayData")) {
				setRepayData((RepayData) arguments.get("repayData"));
				FinanceMain befImage = new FinanceMain();
				financeDetail = getRepayData().getFinanceDetail();
				financeType = financeDetail.getFinScheduleData().getFinanceType();
				setFinanceDetail(financeDetail);
				finRepayHeader = getRepayData().getFinRepayHeader();
				setRepaySchdList(getRepayData().getRepayScheduleDetails());

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(financeDetail.getFinScheduleData().getFinanceMain());
				getRepayData().getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);

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
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "ManualPaymentDialog", menuItemRightName);
				}
			} else {
				this.south.setHeight("0px");
			}

			isEnquiry = true;
			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doStoreServiceIds(getFinanceDetail().getFinScheduleData().getFinanceMain());

			// READ OVERHANDED parameters !
			if (!doFillFinanceData(false)) {

				// set Read only mode accordingly if the object is new or not.
				doEdit(false);
				if (StringUtils.isNotBlank(financeMain.getRecordType())) {
					this.btnNotes.setVisible(true);
				} else {
					if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)
							|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY)) {
						//this.btnPay.setDisabled(true);
						this.btnChangeRepay.setDisabled(true);

						this.totRefundAmt.setDisabled(true);
					}
				}

				//Reset Finance Repay Header Details
				doWriteBeanToComponents();

				this.borderlayout_ManualPayment.setHeight(getBorderLayoutHeight());
				this.listBoxPayment
						.setHeight(getListBoxHeight(this.grid_Repayment.getRows().getVisibleItemCount() + 3));
				this.listBoxSchedule.setHeight(getListBoxHeight(6));
				this.repaymentDetailsTab.setSelected(true);
				this.rpyAmount.setFocus(true);

				// Setting tile Name based on Service Action
				if (StringUtils.isNotEmpty(moduleDefiner)) {
					this.windowTitle.setValue(Labels.getLabel(moduleDefiner + "_Window.Title"));
				}

				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ManualPaymentDialog.onClose();
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

		if (!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			getUserWorkspace().allocateAuthorities("ManualPaymentDialog", getRole(), menuItemRightName);

			this.btnPay.setVisible(getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnPay"));
			this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
			this.btnCalcRepayments.setVisible(getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnCalRepay"));

			this.btnPay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnPay"));
			this.btnCalcRepayments.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnCalRepay"));
			this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
		} else {
			this.btnCalcRepayments.setVisible(true);
			this.btnPrint.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
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

		this.finType1.setMaxlength(8);
		this.finReference1.setMaxlength(20);
		this.finCcy1.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.lovDescCustCIF1.setMaxlength(LengthConstants.LEN_CIF);
		this.finBranch1.setMaxlength(LengthConstants.LEN_BRANCH);
		this.rpyAmount.setMandatory(true);
		this.rpyAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.rpyAmount.setScale(formatter);

		this.repayAccount.setAccountDetails(financeMain.getFinType(), AccountConstants.FinanceAccount_REPY,
				financeMain.getFinCcy());
		this.repayAccount.setFormatter(formatter);
		this.repayAccount.setBranchCode(financeMain.getFinBranch());
		this.repayAccount.setCustCIF(financeMain.getLovDescCustCIF());
		this.repayAccount.setMandatoryStyle(true);
		if (!ImplementationConstants.ACCOUNTS_APPLICABLE
				&& !moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			this.label_PaymentDialog_RepayAccount.setVisible(false);
			this.repayAccount.setMandatoryStyle(false);
			this.hbox_repayAccount.setVisible(false);
		}

		this.priPayment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.pftPayment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totPenaltyAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totRefundAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.insRefundAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.actInsRefundAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.paidByCustomer.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.unEarnedAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.totWaivedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

		this.overDuePrincipal.setMaxlength(18);
		this.overDuePrincipal.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.overDueProfit.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.lastFullyPaidDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextPayDueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accruedPft.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.provisionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.pendingODC.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

		this.earlySettlementDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.earlySettlementBal.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit(boolean isChgRpy) {
		logger.debug("Entering");
		this.repayAccount.setReadonly(isReadOnly("ManualPaymentDialog_RepayAccount"));
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			this.rpyAmount.setDisabled(isReadOnly("ManualPaymentDialog_RepayAmount"));
			if (!isChgRpy) {
				if (getRepaySchdList() != null && !getRepaySchdList().isEmpty()) {
					this.rpyAmount.setDisabled(true);
				}
			}
		} else {
			this.rpyAmount.setDisabled(true);
		}

		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			this.insRefundAmt.setDisabled(isReadOnly("ManualPaymentDialog_insRefund"));
		} else {
			this.insRefundAmt.setDisabled(true);
		}
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			this.totRefundAmt.setDisabled(isReadOnly("ManualPaymentDialog_refundPft"));
		} else {
			this.totRefundAmt.setDisabled(true);
		}
		this.earlySettlementDate.setDisabled(isReadOnly("ManualPaymentDialog_EarlyPayDate"));

		// Schedule Payment Apportionment
		this.paymentApportionment.setDisabled(isReadOnly("ManualPaymentDialog_paymentApportionment"));
		if (!isChgRpy) {
			if (getRepaySchdList() != null && !getRepaySchdList().isEmpty()) {
				this.paymentApportionment.setDisabled(true);
			}
		}
		String payApprtnMent = SysParamUtil.getValueAsString("PAY_APPORTIONMENT");
		this.row_payApportionment.setVisible(false);
		if (StringUtils.trimToEmpty(payApprtnMent).equals(FinanceConstants.PAY_APPORTIONMENT_TO_NONE)) {
			this.paymentApportionment.setDisabled(true);
		} else {
			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)
					|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				if (StringUtils.trimToEmpty(payApprtnMent).equals(FinanceConstants.PAY_APPORTIONMENT_TO_ALL)) {
					this.row_payApportionment.setVisible(true);
				} else if (StringUtils.trimToEmpty(payApprtnMent).equals(FinanceConstants.PAY_APPORTIONMENT_TO_PASTDUE)) {
					if ((getRepayMain().getOverduePrincipal().add(getRepayMain().getOverdueProfit()))
							.compareTo(BigDecimal.ZERO) > 0) {
						this.row_payApportionment.setVisible(true);
					}
				} else if (StringUtils.trimToEmpty(payApprtnMent)
						.equals(FinanceConstants.PAY_APPORTIONMENT_TO_SUSPENSE)) {
					if (getRepayData().getFinanceDetail().getCustomerDetails().getCustomer().getCustStsChgDate() != null) {
						this.row_payApportionment.setVisible(true);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgRpy
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean doFillFinanceData(boolean isChgRpy) throws InterruptedException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		RepayData repayData = new RepayData();
		repayData.setBuildProcess("I");

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		int finformatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		;

		Customer customer = null;
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			customer = getFinanceDetail().getCustomerDetails().getCustomer();
		}

		FinanceMain aFinanceMain = new FinanceMain();
		List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<FinanceScheduleDetail>();

		if (isChgRpy) {

			FinScheduleData data = getFinanceDetailService().getFinSchDataById(financeMain.getFinReference(), "_AView",
					false);
			aFinanceMain = data.getFinanceMain();
			aFinanceMain.setWorkflowId(financeMain.getWorkflowId());
			aFinanceMain.setVersion(financeMain.getVersion());
			aFinanceMain.setRecordType(financeMain.getRecordType());
			aFinanceMain.setNewRecord(financeMain.isNewRecord());
			financeScheduleDetails = data.getFinanceScheduleDetails();
			getFinanceDetail().setFinScheduleData(data);
		} else {

			Cloner cloner = new Cloner();
			aFinanceMain = cloner.deepClone(financeMain);
			financeScheduleDetails = cloner.deepClone(getFinanceDetail().getFinScheduleData()
					.getFinanceScheduleDetails());
		}

		financeScheduleDetails = sortSchdDetails(financeScheduleDetails);
		String accTypeEvent = "";
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			accTypeEvent = AccountConstants.FinanceAccount_ERLS;
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			accTypeEvent = AccountConstants.FinanceAccount_REPY;
			this.row_EarlyRepayEffectOnSchd.setVisible(true);
		} else if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			accTypeEvent = AccountConstants.FinanceAccount_REPY;
		}

		this.repayAccount.setAccountDetails(aFinanceMain.getFinType(), accTypeEvent, aFinanceMain.getFinCcy());
		this.repayAccount.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.repayAccount.setFormatter(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
		this.repayAccount.setBranchCode(aFinanceMain.getFinBranch());

		repayData.setAccruedTillLBD(aFinanceMain.getLovDescAccruedTillLBD());
		repayData.setFinanceDetail(getFinanceDetail());
		setRepayData(getRepayCalculator().initiateRepay(repayData, aFinanceMain, financeScheduleDetails, "", null,
				false, null, this.earlySettlementDate.getValue(), null));
		repayData.getRepayMain().setLovDescFinFormatter(finformatter);
		setRepayMain(repayData.getRepayMain());

		this.finType.setValue(getRepayMain().getFinType());
		this.finReference.setValue(getRepayMain().getFinReference());
		this.finCcy.setValue(getRepayMain().getFinCcy());
		fillComboBox(this.profitDayBasis, getRepayMain().getProfitDaysBais(), profitDayList, "");
		this.custID.setValue(getRepayMain().getCustID());
		if (customer != null) {
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
		this.schPriTillNextDue
				.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalPayNow(), finformatter));
		this.schPftTillNextDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitPayNow(), finformatter));
		this.totPriPaid.setValue(PennantAppUtil.formateAmount(
				getRepayMain().getPrincipal().subtract(getRepayMain().getPrincipalBalance()), finformatter));
		this.totPftPaid.setValue(PennantAppUtil.formateAmount(
				getRepayMain().getProfit().subtract(getRepayMain().getProfitBalance()), finformatter));
		this.totPriDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalBalance(), finformatter));
		this.totPftDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitBalance(), finformatter));

		//Repayments modified Details
		this.finType1.setValue(getRepayMain().getFinType());
		this.finReference1.setValue(getRepayMain().getFinReference());
		this.finCcy1.setValue(getRepayMain().getFinCcy());
		this.lovDescFinCcyName1.setValue(getRepayMain().getFinCcy());
		this.custID1.setValue(getRepayMain().getCustID());
		if (customer != null) {
			this.lovDescCustCIF1.setValue(customer.getCustCIF());
			this.custShrtName1.setValue(customer.getCustShrtName());
		}
		this.finBranch1.setValue(getRepayMain().getFinBranch());
		this.lovDescFinBranchName1.setValue(getRepayMain().getFinBranch() + "-"
				+ getRepayMain().getLovDescFinBranchName());
		if (!isChgRpy) {
			this.rpyAmount.setValue(PennantAppUtil.formateAmount(getRepayMain().getRepayAmountNow(), finformatter));
			this.repayAccount.setValue(getRepayMain().getRepayAccountId());
		}
		this.priPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalPayNow(), finformatter));
		this.pftPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitPayNow(), finformatter));
		fillComboBox(this.earlyRpyEffectOnSchd, getRepayMain().getEarlyPayEffectOn(), earlyRpyEffectList, "");
		this.totRefundAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getRefundNow(), finformatter));
		this.totWaivedAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		this.overDuePrincipal
				.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverduePrincipal(), finformatter));
		this.overDueProfit.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverdueProfit(), finformatter));
		this.lastFullyPaidDate.setValue(getRepayMain().getDateLastFullyPaid());
		this.nextPayDueDate.setValue(getRepayMain().getDateNextPaymentDue());
		this.accruedPft.setValue(PennantAppUtil.formateAmount(getRepayMain().getAccrued(), finformatter));

		//Total Overdue Penalty Amount
		BigDecimal pendingODC = getOverdueChargeRecoveryService().getPendingODCAmount(aFinanceMain.getFinReference());
		repayData.setPendingODC(pendingODC);
		this.pendingODC.setValue(PennantAppUtil.formateAmount(pendingODC, finformatter));

		//Fill Schedule data
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {

			//Fetch Total Repayment Amount till Maturity date for Early Settlement
			BigDecimal repayAmt = getFinanceDetailService().getTotalRepayAmount(aFinanceMain.getFinReference());
			this.rpyAmount.setValue(PennantAppUtil.formateAmount(repayAmt, finformatter));
			this.row_EarlySettleDate.setVisible(true);

			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

				if (getRepaySchdList() == null || getRepaySchdList().isEmpty()) {

					if (!isChgRpy || StringUtils.isNotBlank(this.repayAccount.getValue())) {

						Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, isChgRpy);
						this.btnCalcRepayments.setVisible(false);
						this.btnChangeRepay.setVisible(false);
					} else {
						MessageUtil.showError("Repay Account ID must Exist.");
						return true;
					}
				} else {

					Date curBussDate = DateUtility.getAppDate();

					this.earlySettlementDate.setConstraint("");
					this.earlySettlementDate.setErrorMessage("");
					if (this.earlySettlementDate.getValue() == null) {
						this.earlySettlementDate.setValue(curBussDate);
					}
					this.hbox_insRefundAmt.setVisible(false);
					this.label_PaymentDialog_InsRefundAmt.setVisible(false);

					doFillRepaySchedules(getRepaySchdList());
				}
			}

			this.row_paidByCustomer.setVisible(true);
			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
				Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, isChgRpy);

				this.earlySettlementBal.setVisible(true);
				this.label_PaymentDialog_RepayAccount.setValue(Labels
						.getLabel("label_PaymentDialog_EarlySettleAmount.value"));
				this.repayAccount.setVisible(false);
				this.label_PaymentDialog_EarlySettlementTillDate.setVisible(true);
				this.hbox_esTilllDate.setVisible(true);
				this.earlySettlementBal.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

				this.btnCalcRepayments.setVisible(true);
				this.btnChangeRepay.setVisible(false);
				this.btnPay.setVisible(false);
				this.actInsRefundAmt.setVisible(true);
			}

		} else {
			if (!isChgRpy) {
				if (getRepaySchdList() != null && !getRepaySchdList().isEmpty()) {
					this.btnCalcRepayments.setDisabled(true);
					this.rpyAmount.setDisabled(true);
				}
				doFillRepaySchedules(getRepaySchdList());
			} else {
				doFillRepaySchedules(repayData.getRepayScheduleDetails());
			}
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
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			arrayList.add(9, getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
		} else {
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
		doClose(this.btnPay.isVisible());
	}

	/**
	 * Method for calculation of Schedule Repayment details List of data
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 * @throws WrongValueException
	 */
	public void onClick$btnCalcRepayments(Event event) throws InterruptedException, WrongValueException,
			InterfaceException {
		logger.debug("Entering" + event.toString());

		boolean isChgRpy = false;
		if (event.getData() != null) {
			isChgRpy = (Boolean) event.getData();
		}

		if (isValid(isChgRpy, false)) {

			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
					|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
				this.totRefundAmt.setDisabled(isReadOnly("ManualPaymentDialog_refundPft"));
			} else {
				this.totRefundAmt.setDisabled(true);
			}

			RepayData repayData = null;
			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
				Cloner cloner = new Cloner();
				List<FinanceScheduleDetail> finschDetailList = cloner.deepClone(getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails());
				Date valueDate = this.earlySettlementDate.getValue();

				if (this.earlySettlementTillDate.getSelectedItem() != null
						&& !this.earlySettlementTillDate.getSelectedItem().getValue().toString()
								.equals(PennantConstants.List_Select)) {
					finschDetailList = rePrepareScheduleTerms(finschDetailList);
				}

				repayData = calculateRepayments(getFinanceDetail().getFinScheduleData().getFinanceMain(),
						finschDetailList, false, null, valueDate);

			} else {
				Date valueDate = null;
				if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
					valueDate = this.earlySettlementDate.getValue();
				}
				repayData = calculateRepayments(getFinanceDetail().getFinScheduleData().getFinanceMain(),
						getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), false, null, valueDate);
			}

			if (repayData.getRepayMain().isEarlyPay() && moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)) {

				// Show a confirm box
				final String msg = Labels.getLabel("label_EarlypayEffectOnSchedule_Method_Confirm",
						new String[] { this.earlyRpyEffectOnSchd.getSelectedItem().getLabel() });

				if (MessageUtil.confirm(msg) == MessageUtil.YES) {
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("manualPaymentDialogCtrl", this);
					map.put("repayData", repayData);
					Executions.createComponents(
							"/WEB-INF/pages/FinanceManagement/Payments/EarlypayEffectOnSchedule.zul",
							this.window_ManualPaymentDialog, map);

				} else {
					setEarlyRepayEffectOnSchedule(repayData);
				}

			} else {
				setRepayDetailData(repayData);
			}

			// Setting disabled after Calculation
			if (this.row_payApportionment.isVisible()) {
				this.paymentApportionment.setDisabled(true);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Fill Unpaid Schedule Term for Selection of Paid Term
	 * 
	 * @param event
	 */
	public void onChange$earlySettlementDate(Event event) {
		logger.debug("Entering" + event.toString());
		Date curBussDate = DateUtility.getAppDate();

		this.earlySettlementDate.setConstraint("");
		this.earlySettlementDate.setErrorMessage("");

		if (this.earlySettlementDate.getValue() == null) {
			this.earlySettlementDate.setValue(curBussDate);
		}
		if (this.moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			fillUnpaidSchDates();
		} else if (this.moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

			//Check Early Settlement Date, EITHER Equal to Current Buss Date or Last Business Value Date
			Date lastBussDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_LAST);
			if (lastBussDate.compareTo(this.earlySettlementDate.getValue()) > 0
					|| curBussDate.compareTo(this.earlySettlementDate.getValue()) < 0) {
				throw new WrongValueException(this.earlySettlementDate, Labels.getLabel(
						"label_EarlySettlementDate",
						new String[] { DateUtility.formatToLongDate(lastBussDate),
								DateUtility.formatToLongDate(curBussDate) }));
			}

			//Recalculation for Repayment Schedule Details
			Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, false);

			this.btnCalcRepayments.setVisible(false);
			this.btnChangeRepay.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$rpyAmount(Event event) {
		logger.debug("Entering");
		this.btnChangeRepay.setDisabled(true);
		this.btnPay.setDisabled(true);
		this.btnCalcRepayments.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnCalRepay"));
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Unpaid Schedule Terms based upon Early settlement Date selection
	 */
	public void fillUnpaidSchDates() {
		logger.debug("Entering");

		earlySettlementTillDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		earlySettlementTillDate.appendChild(comboitem);
		earlySettlementTillDate.setSelectedItem(comboitem);

		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null) {
			for (FinanceScheduleDetail curSchd : getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()) {

				if (curSchd.getSchDate().compareTo(this.earlySettlementDate.getValue()) > 0) {
					break;
				}

				if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {

					if (curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {

						comboitem = new Comboitem();
						comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()));
						comboitem.setValue(curSchd.getSchDate());
						earlySettlementTillDate.appendChild(comboitem);
					}
				}
			}
		}
	}

	/**
	 * Method for Allowing Total Refund Amount as manually also Revert Back to Old Amount if Entered Manual Amount is
	 * Greater than Calculation Refund Amount
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$totRefundAmt(Event event) throws InterruptedException {
		logger.debug("Entering");
		doCheckRefundCal();
		logger.debug("Leaving");
	}

	private boolean doCheckRefundCal() throws InterruptedException {

		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		RepayData aRepayData = cloner.deepClone(getRepayData());

		int finFormatter = aRepayData.getRepayMain().getLovDescFinFormatter();
		BigDecimal manualRefundAmt = PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), finFormatter);

		aRepayData.setRepayScheduleDetails(getRepaySchdList());
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		Rule refundRule = getRuleService().getApprovedRuleById("REFUND", RuleConstants.MODULE_REFUND,
				RuleConstants.EVENT_REFUND);
		String sqlRule = null;
		if (refundRule != null) {
			sqlRule = refundRule.getSQLRule();
		}
		Customer customer = getCustomerDetailsService().getCustomerForPostings(financeMain.getCustID());

		SubHeadRule subHeadRule = new SubHeadRule();

		try {
			BeanUtils.copyProperties(subHeadRule, customer);
			subHeadRule.setReqFinAcType(financeType.getFinAcType());
			//subHeadRule.setReqFinCcy(financeType.getFinCcy());
			subHeadRule.setReqProduct(financeType.getFinCategory());
			subHeadRule.setReqFinType(financeType.getFinType());
			subHeadRule.setReqFinPurpose(financeMain.getFinPurpose());
			subHeadRule.setReqFinDivision(financeType.getFinDivision());

			//Profit Details
			subHeadRule.setTOTALPFT(getRepayData().getRepayMain().getProfit());
			subHeadRule.setTOTALPFTBAL(getRepayData().getRepayMain().getProfitBalance());

			//Check For Early Settlement Enquiry -- on Selecting Future Date
			BigDecimal accrueValue = getFinanceDetailService().getAccrueAmount(financeMain.getFinReference());
			subHeadRule.setACCRUE(accrueValue);

			//Total Tenure
			int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(),
					false);
			subHeadRule.setTenure(months);

		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

		aRepayData = getRepayCalculator().calculateRefunds(aRepayData, manualRefundAmt, true, sqlRule, subHeadRule);
		if (!aRepayData.isSufficientRefund()) {

			setRefundAmtValidated(false);
			String msg = Labels.getLabel("label_ManualRefundExceed",
					new String[] { PennantApplicationUtil.amountFormate(aRepayData.getMaxRefundAmt(), finFormatter) });
			MessageUtil.showError(msg);
			this.totRefundAmt.setValue(PennantApplicationUtil.formateAmount(this.oldVar_totRefundAmt, finFormatter));

			return false;
		}

		//Total Outstanding Paid Amount By customer
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

			BigDecimal refundVal = totRefundAmt.getValue() == null ? BigDecimal.ZERO : totRefundAmt.getValue();
			this.paidByCustomer.setValue(this.rpyAmount.getActualValue().subtract(refundVal)
					.subtract(this.insRefundAmt.getValue() == null ? BigDecimal.ZERO : this.insRefundAmt.getValue()));
			this.unEarnedAmount.setValue(this.totPftDue.getValue().subtract(this.accruedPft.getValue()));
		}

		//Re-Rendering Repay Schedule Terms
		doFillRepaySchedules(aRepayData.getRepayScheduleDetails());
		setRefundAmtValidated(true);
		return true;
	}

	/**
	 * Method for Re=Prepare Schedule Term Data Based upon Till Paid Schedule Term
	 * 
	 * @param scheduleDetails
	 * @return
	 */
	private List<FinanceScheduleDetail> rePrepareScheduleTerms(List<FinanceScheduleDetail> scheduleDetails) {
		logger.debug("Entering");

		Date paidTillTerm = (Date) this.earlySettlementTillDate.getSelectedItem().getValue();

		for (FinanceScheduleDetail curSchd : scheduleDetails) {

			if (curSchd.getSchDate().compareTo(paidTillTerm) > 0) {
				break;
			}

			curSchd.setSchdPriPaid(curSchd.getPrincipalSchd());
			curSchd.setSchdPftPaid(curSchd.getProfitSchd());

			curSchd.setSchPftPaid(true);
			curSchd.setSchPriPaid(true);
		}

		logger.debug("Leaving");
		return scheduleDetails;
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param repayData
	 * @throws InterruptedException
	 */
	public void setEarlyRepayEffectOnSchedule(RepayData repayData) throws InterruptedException {
		logger.debug("Entering");

		//Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		String method = null;
		// Schedule remodifications only when Effective Schedule Method modified
		if (!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || this.earlyRpyEffectOnSchd
				.getSelectedItem().getValue().toString().equals(CalculationConstants.EARLYPAY_NOEFCT))) {

			method = this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString();

			if (CalculationConstants.EARLYPAY_RECPFI.equals(method)
					|| CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {
				aFinanceMain.setPftIntact(true);
			}

			if (repayData.getRepayMain().getEarlyRepayNewSchd() != null) {
				if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setPftOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayAmount(BigDecimal.ZERO);
				}
				finScheduleData.getFinanceScheduleDetails().add(repayData.getRepayMain().getEarlyRepayNewSchd());
			}

			for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
				if (detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) == 0) {
					if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
						detail.setEarlyPaid(detail.getEarlyPaid().add(repayData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					} else {
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						repayData.getRepayMain().setEarlyPayAmount(
								repayData.getRepayMain().getEarlyPayAmount().add(earlypaidBal));
					}
				}
				if (detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) >= 0) {
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			finScheduleData.setFinanceType(getFinanceType());

			//Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, repayData.getRepayMain()
					.getEarlyPayOnSchDate(), repayData.getRepayMain().getEarlyPayNextSchDate(), repayData
					.getRepayMain().getEarlyPayAmount(), method);

			// Validation against Future Disbursements, if Closing balance is becoming zero before future disbursement date
			List<FinanceDisbursement> disbList = finScheduleData.getDisbursementDetails();
			Date actualMaturity = finScheduleData.getFinanceMain().getCalMaturity();
			for (int i = 0; i < disbList.size(); i++) {
				FinanceDisbursement curDisb = disbList.get(i);
				if (curDisb.getDisbDate().compareTo(actualMaturity) >= 0) {
					MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("30577", null)));
					Events.sendEvent(Events.ON_CLICK, this.btnChangeRepay, null);
					logger.debug("Leaving");
					return;
				}
			}

			financeDetail.setFinScheduleData(finScheduleData);
			aFinanceMain = finScheduleData.getFinanceMain();
			aFinanceMain.setWorkflowId(getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());
			setFinanceDetail(financeDetail);//Object Setting for Future save purpose
			repayData.setFinanceDetail(financeDetail);

			this.finSchType.setValue(aFinanceMain.getFinType());
			this.finSchCcy.setValue(aFinanceMain.getFinCcy());
			this.finSchMethod.setValue(aFinanceMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(),
					profitDayList));
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
		repayData = calculateRepayments(finScheduleData.getFinanceMain(), finScheduleData.getFinanceScheduleDetails(),
				true, method, null);
		setRepayData(repayData);
		setRepayDetailData(repayData);

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
				map.put("window", this.window_ManualPaymentDialog);
				finRender.render(map, prvSchDetail, false, true, false, aFinScheduleData.getFinFeeDetailList(), showRate, false);

				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, true, false, aFinScheduleData.getFinFeeDetailList(), showRate, false);
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	private RepayData calculateRepayments(FinanceMain financeMain, List<FinanceScheduleDetail> finSchDetails,
			boolean isReCal, String method, Date valueDate) {

		logger.debug("Entering");

		getRepayData().setBuildProcess("R");
		getRepayData().getRepayMain().setRepayAmountNow(
				PennantAppUtil.unFormateAmount(this.rpyAmount.getActualValue(), getRepayData().getRepayMain()
						.getLovDescFinFormatter()));

		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			getRepayData().getRepayMain().setPayApportionment(
					this.paymentApportionment.getSelectedItem().getValue().toString());
		} else {
			getRepayData().getRepayMain().setPayApportionment(PennantConstants.List_Select);
		}
		isSchdRecal = true;
		SubHeadRule subHeadRule = null;
		String sqlRule = null;

		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			Rule rule = getRuleService().getApprovedRuleById("REFUND", RuleConstants.MODULE_REFUND,
					RuleConstants.EVENT_REFUND);
			if (rule != null) {
				sqlRule = rule.getSQLRule();
			}
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			if (customer == null) {
				customer = getCustomerDetailsService().getCustomerForPostings(financeMain.getCustID());
			}
			subHeadRule = new SubHeadRule();

			try {
				BeanUtils.copyProperties(subHeadRule, customer);
				subHeadRule.setReqFinAcType(financeType.getFinAcType());
				//subHeadRule.setReqFinCcy(financeType.getFinCcy());
				subHeadRule.setReqProduct(financeType.getFinCategory());
				subHeadRule.setReqFinType(financeType.getFinType());
				subHeadRule.setReqFinPurpose(financeMain.getFinPurpose());
				subHeadRule.setReqFinDivision(financeType.getFinDivision());

				//Profit Details
				subHeadRule.setTOTALPFT(getRepayData().getRepayMain().getProfit());
				subHeadRule.setTOTALPFTBAL(getRepayData().getRepayMain().getProfitBalance());

				//Check For Early Settlement Enquiry -- on Selecting Future Date
				BigDecimal accrueValue = getFinanceDetailService().getAccrueAmount(financeMain.getFinReference());
				subHeadRule.setACCRUE(accrueValue);

				//Total Tenure
				int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(),
						false);
				subHeadRule.setTenure(months);

				FeeRule insAmount = getFinanceDetailService().getInsFee(financeMain.getFinReference());
				if (insAmount != null) {
					subHeadRule
							.setCALFEE(insAmount.getFeeAmount() == null ? BigDecimal.ZERO : insAmount.getFeeAmount());
					subHeadRule.setWAVFEE(insAmount.getWaiverAmount() == null ? BigDecimal.ZERO : insAmount
							.getWaiverAmount());
					subHeadRule.setPAIDFEE(insAmount.getPaidAmount() == null ? BigDecimal.ZERO : insAmount
							.getPaidAmount());
					getRepayData().setActInsRefundAmt(
							subHeadRule.getCALFEE().subtract(subHeadRule.getWAVFEE())
									.subtract(subHeadRule.getPAIDFEE()));
				}

			} catch (IllegalAccessException e) {
				logger.error("Exception: ", e);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}
		}

		getRepayData().getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		getRepayData().getRepayMain().setProfitPayNow(BigDecimal.ZERO);
		repayData = getRepayCalculator().initiateRepay(getRepayData(), financeMain, finSchDetails, sqlRule,
				subHeadRule, isReCal, method, valueDate, moduleDefiner);

		//Calculation for Insurance Refund
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			this.hbox_insRefundAmt.setVisible(false);
			this.label_PaymentDialog_InsRefundAmt.setVisible(false);
			int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), repayData.getRepayMain()
					.getRefundCalStartDate() == null ? financeMain.getMaturityDate() : repayData.getRepayMain()
					.getRefundCalStartDate(), true);
			subHeadRule.setRemTenure(months);

			Rule insRefundRule = getRuleService().getApprovedRuleById("INSREFND", RuleConstants.MODULE_REFUND,
					RuleConstants.EVENT_REFUND);
			if (insRefundRule != null) {

				BigDecimal refundResult = (BigDecimal) getRuleExecutionUtil().executeRule(insRefundRule.getSQLRule(),
						subHeadRule.getDeclaredFieldValues(), financeMain.getFinCcy(), RuleReturnType.DECIMAL);
				repayData.getRepayMain().setInsRefund(refundResult);
				this.insRefundAmt.setValue(PennantApplicationUtil.formateAmount(refundResult, repayData.getRepayMain()
						.getLovDescFinFormatter()));
			} else {
				this.insRefundAmt.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO, repayData
						.getRepayMain().getLovDescFinFormatter()));
			}
		}

		setRepayData(repayData);

		logger.debug("Leaving");
		return repayData;
	}

	private void setRepayDetailData(RepayData repayData) throws InterruptedException {
		logger.debug("Entering");

		//Repay Schedule Data rebuild
		doFillRepaySchedules(repayData.getRepayScheduleDetails());
		this.priPayment.setValue(PennantAppUtil.formateAmount(repayData.getRepayMain().getPrincipalPayNow(), repayData
				.getRepayMain().getLovDescFinFormatter()));
		this.pftPayment.setValue(PennantAppUtil.formateAmount(repayData.getRepayMain().getProfitPayNow(), repayData
				.getRepayMain().getLovDescFinFormatter()));

		this.btnPay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnPay"));
		this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
		this.rpyAmount.setDisabled(true);
		this.repayAccount.setReadonly(isReadOnly("ManualPaymentDialog_RepayAccount"));
		if (!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			this.btnCalcRepayments.setDisabled(true);
		} else {
			BigDecimal paidNow = getRepayMain().getPrincipalPayNow().add(getRepayMain().getProfitPayNow());
			BigDecimal settlementBal = PennantAppUtil.unFormateAmount(this.rpyAmount.getActualValue(),
					getRepayMain().getLovDescFinFormatter()).subtract(paidNow);
			this.earlySettlementBal.setValue(PennantAppUtil.formateAmount(settlementBal, getRepayMain()
					.getLovDescFinFormatter()));
			this.actInsRefundAmt.setValue(PennantAppUtil.formateAmount(getRepayData().getActInsRefundAmt(),
					getRepayMain().getLovDescFinFormatter()));
		}

		//Total Outstanding Paid Amount By customer
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			this.paidByCustomer.setValue(this.rpyAmount.getActualValue().subtract(this.totRefundAmt.getValue())
					.subtract(this.insRefundAmt.getValue()));
			this.unEarnedAmount.setValue(this.totPftDue.getValue().subtract(this.accruedPft.getValue()));
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
	public void onClick$btnChangeRepay(Event event) throws InterruptedException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering" + event.toString());

		doFillFinanceData(true);
		this.totRefundAmt.setDisabled(true);
		this.btnPay.setDisabled(true);
		this.btnChangeRepay.setDisabled(true);
		this.btnCalcRepayments.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnCalRepay"));
		this.rpyAmount.setDisabled(isReadOnly("ManualPaymentDialog_RepayAmount"));
		this.repayAccount.setReadonly(isReadOnly("ManualPaymentDialog_RepayAccount"));
		this.repaymentDetailsTab.setSelected(true);
		this.rpyAmount.setFocus(true);
		this.effectiveScheduleTab.setVisible(false);

		if (tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null) {
			tabpanelsBoxIndexCenter.removeChild(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"));
		}

		if (tabsIndexCenter.getFellowIfAny("dashboardTab") != null) {
			tabsIndexCenter.removeChild(tabsIndexCenter.getFellowIfAny("dashboardTab"));
		}

		// Setting disabled after Calculation
		if (this.row_payApportionment.isVisible()) {
			this.paymentApportionment.setDisabled(isReadOnly("ManualPaymentDialog_paymentApportionment"));
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("unused")
	public void doSave() throws WrongValueException, InterruptedException {
		logger.debug("Entering");

		try {
			boolean isChgRpy = true;

			if (this.userAction.getSelectedItem() != null
					&& ("Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) || "Cancel"
								.equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
				isChgRpy = false;
			}

			if (getFinanceCheckListReferenceDialogCtrl() != null
					&& (getFinanceDetail().getFinRefDetailsList() == null || getFinanceDetail().getFinRefDetailsList()
							.isEmpty())) {
				getFinanceDetail().getFinScheduleData().getFinanceMain().setRefundAmount(this.totRefundAmt.getValue());
				getFinanceDetail().setCustomerEligibilityCheck(
						prepareCustElgDetail(false).getCustomerEligibilityCheck());
				getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
						getFinanceDetail().getFinanceCheckList(), true);
			}

			if (isValid(isChgRpy, true)) {
				if (!isRefundExceeded) {
					this.btnChangeRepay.setDisabled(true);
					this.btnCalcRepayments.setDisabled(true);

					//If Schedule Re-modified Save into DB or else only add Repayments Details
					if (!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString()
							.equals(PennantConstants.List_Select) || "NOEFCT".equals(this.earlyRpyEffectOnSchd
							.getSelectedItem().getValue().toString()))
							&& moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)) {

						processRepayScheduleList(getFinanceDetail().getFinScheduleData().getFinanceMain(),
								getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), getFinanceDetail()
										.getFinScheduleData().getRepayInstructions(), true);
					} else {

						if (isChgRpy && ImplementationConstants.ACCOUNTS_APPLICABLE) {
							// Check Available Funding Account Balance
							iAccount = getAccountInterfaceService().fetchAccountAvailableBal(
									PennantApplicationUtil.unFormatAccountNumber(this.repayAccount.getValue()));

							// Check Available Funding Account Balance
							boolean accountTypeFound = false;
							if (getFinanceType() != null) {
								if (StringUtils.trimToEmpty(getFinanceType().getFinDivision()).equals(
										FinanceConstants.FIN_DIVISION_TREASURY)) {
									String acType = SysParamUtil.getValueAsString("ALWFULLPAY_TSR_ACTYPE");

									//Account Type Check
									String[] acTypeList = acType.split(",");
									for (int i = 0; i < acTypeList.length; i++) {
										if (iAccount.getAcType().equals(acTypeList[i].trim())) {
											accountTypeFound = true;
											break;
										}
									}

								} else {

									//Account Type Check
									String acType = SysParamUtil.getValueAsString("ALWFULLPAY_NONTSR_ACTYPE");
									String[] acTypeList = acType.split(",");
									for (int i = 0; i < acTypeList.length; i++) {
										if (iAccount != null
												&& StringUtils.trimToEmpty(iAccount.getAcType()).equals(
														acTypeList[i].trim())) {
											accountTypeFound = true;
											break;
										}
									}
								}
							}

							if (!accountTypeFound && iAccount != null) {

								if (PennantAppUtil.unFormateAmount(
										this.rpyAmount
												.getActualValue()
												.subtract(this.totRefundAmt.getValue())
												.subtract(
														insRefundAmt.getValue() == null ? BigDecimal.ZERO
																: insRefundAmt.getValue()),
										getRepayData().getRepayMain().getLovDescFinFormatter()).compareTo(
										iAccount.getAcAvailableBal()) > 0) {
									MessageUtil.showError(Labels.getLabel("label_InsufficientBalance"));
									this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed(
											"button_ManualPaymentDialog_btnChangeRepay"));
									return;
								}
							}
						}
						//financeMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(repayAccount.getValue()));
						processRepayScheduleList(getFinanceDetail().getFinScheduleData().getFinanceMain(),
								getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), null, false);
					}

				} else {
					MessageUtil.showError("Limit exceeded ... ");
					return;
				}
			}

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
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

		RepayData data = new RepayData();
		data.setFinanceDetail(getFinanceDetail());
		data.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		data.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(finSchDetails);
		data.getFinanceDetail().getFinScheduleData().setRepayInstructions(repayInstructions);
		data.setRepayScheduleDetails(getRepaySchdList());
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
		data.setFinRepayHeader(doWriteComponentsToBean(schdlReModified));

		//Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);

		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		RepayData aRepayData = cloner.deepClone(data);

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
			aRepayData.getFinanceDetail()
					.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aRepayData.getFinanceDetail().setDocumentDetailsList(null);
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
			aRepayData.getFinanceDetail().setStageAccountingList(null);
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

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aRepayData.getFinanceDetail(), false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aRepayData.getFinanceDetail().setFinanceCheckList(null);
		}

		aRepayData.setEventCodeRef(eventCode);

		// save it to database
		try {
			aFinanceMain.setRcdMaintainSts(aRepayData.getFinRepayHeader().getFinEvent());
			aRepayData.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aRepayData, tranType)) {

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
				FinanceDetail financeDetail = aRepayData.getFinanceDetail();
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

						// Send message Notification to Users
						if (aFinanceMain.getNextUserId() != null) {
							String usrLogins = aFinanceMain.getNextUserId();
							List<String> usrLoginList = new ArrayList<String>();

							if (usrLogins.contains(",")) {
								String[] to = usrLogins.split(",");
								for (String roleCode : to) {
									usrLoginList.add(roleCode);
								}
							} else {
								usrLoginList.add(usrLogins);
							}

							List<String> userLogins = getFinanceDetailService().getUsersLoginList(usrLoginList);

							String[] to = new String[userLogins.size()];
							for (int i = 0; i < userLogins.size(); i++) {
								to[i] = String.valueOf(userLogins.get(i));
							}

							if (StringUtils.isNotEmpty(reference)) {
								if (!PennantConstants.RCD_STATUS_CANCELLED
										.equalsIgnoreCase(aFinanceMain.getRecordStatus())) {
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference"
											+ ":" + reference, Notify.USER, to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), Notify.USER, to);
							}
						} else {
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

	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		FinRepayHeader header = getFinRepayHeader();
		if (header != null) {

			this.rpyAmount.setValue(PennantApplicationUtil.formateAmount(header.getRepayAmount(), finFormatter));
			this.priPayment.setValue(PennantApplicationUtil.formateAmount(header.getPriAmount(), finFormatter));
			this.pftPayment.setValue(PennantApplicationUtil.formateAmount(header.getPftAmount(), finFormatter));
			this.totWaivedAmt.setValue(PennantApplicationUtil.formateAmount(header.getTotalWaiver(), finFormatter));
			this.totRefundAmt.setValue(PennantApplicationUtil.formateAmount(header.getTotalRefund(), finFormatter));
			this.repayAccount.setValue(header.getRepayAccountId());
			this.insRefundAmt.setValue(PennantApplicationUtil.formateAmount(header.getInsRefund(), finFormatter));
			this.insPayment.setValue(PennantApplicationUtil.formateAmount(header.getTotalIns(), finFormatter));
			this.schdFeeAmount.setValue(PennantApplicationUtil.formateAmount(header.getTotalSchdFee(), finFormatter));
			this.suplRentAmount.setValue(PennantApplicationUtil.formateAmount(header.getTotalSuplRent(), finFormatter));
			this.incrCostAmount.setValue(PennantApplicationUtil.formateAmount(header.getTotalIncrCost(), finFormatter));
			fillComboBox(earlyRpyEffectOnSchd, header.getEarlyPayEffMtd(), earlyRpyEffectList, "");
			fillComboBox(this.paymentApportionment, header.getPayApportionment(), payApprtnList, "");
			this.earlySettlementDate.setValue(header.getEarlyPayDate());

		} else {
			fillComboBox(this.paymentApportionment, "", payApprtnList, "");
		}

		//Total Outstanding Paid Amount By customer
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)
				|| moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			this.paidByCustomer.setValue(this.rpyAmount.getActualValue().subtract(this.totRefundAmt.getValue())
					.subtract(this.insRefundAmt.getValue()));
			this.unEarnedAmount.setValue(this.totPftDue.getValue().subtract(this.accruedPft.getValue()));
		}

		if (!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || this.earlyRpyEffectOnSchd
				.getSelectedItem().getValue().toString().equals(CalculationConstants.EARLYPAY_NOEFCT))
				&& moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)) {

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

		getFinanceDetail().setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);

		//Customer Details   
		appendCustomerDetailTab();

		//Fee Details Tab Addition
		appendFeeDetailTab(true);

		// Schedule Details
		appendScheduleDetailTab(true, false);

		//Agreement Details
		appendAgreementsDetailTab(true);

		// Check List Details
		appendCheckListDetailTab(getRepayData().getFinanceDetail(), false, true);

		// Recommendation Details 
		appendRecommendDetailTab(true);

		//Document Details
		appendDocumentDetailTab();

		// Stage Accounting Details
		appendStageAccountingDetailsTab(true);

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if (!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)
				&& "Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			//Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}

		this.recordStatus.setValue(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		logger.debug("Leaving");
	}

	private FinRepayHeader doWriteComponentsToBean(boolean isSchdRegenerated) {
		logger.debug("Entering");

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinRepayHeader header = getFinRepayHeader();
		if (header == null || (isSchdRecal && moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY))) {
			header = new FinRepayHeader();
			header.setSchdRegenerated(isSchdRegenerated);
		}

		header.setFinReference(this.finReference.getValue());
		Date curBDay = DateUtility.getAppDate();
		header.setValueDate(curBDay);
		header.setFinEvent(moduleDefiner);
		header.setRepayAmount(PennantApplicationUtil.unFormateAmount(this.rpyAmount.getActualValue(), finFormatter));
		header.setPriAmount(PennantApplicationUtil.unFormateAmount(this.priPayment.getValue(), finFormatter));
		header.setPftAmount(PennantApplicationUtil.unFormateAmount(this.pftPayment.getValue(), finFormatter));
		header.setTotalRefund(PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), finFormatter));
		header.setTotalWaiver(PennantApplicationUtil.unFormateAmount(this.totWaivedAmt.getValue(), finFormatter));
		header.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAccount.getValue()));
		header.setInsRefund(PennantApplicationUtil.unFormateAmount(this.insRefundAmt.getValue(), finFormatter));
		header.setTotalIns(PennantApplicationUtil.unFormateAmount(this.insPayment.getValue(), finFormatter));
		header.setTotalSchdFee(PennantApplicationUtil.unFormateAmount(this.schdFeeAmount.getValue(), finFormatter));
		header.setTotalSuplRent(PennantApplicationUtil.unFormateAmount(this.suplRentAmount.getValue(), finFormatter));
		header.setTotalIncrCost(PennantApplicationUtil.unFormateAmount(this.incrCostAmount.getValue(), finFormatter));
		header.setEarlyPayEffMtd(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString());
		header.setEarlyPayDate(this.earlySettlementDate.getValue());
		header.setPayApportionment(this.paymentApportionment.getSelectedItem().getValue().toString());
		header.setSchdRegenerated(isSchdRegenerated);
		getFinanceDetail().setFinRepayHeader(header);
		logger.debug("Leaving");
		return header;
	}

	public void onSelectCheckListDetailsTab(ForwardEvent event) throws ParseException, InterruptedException,
			IllegalAccessException, InvocationTargetException {
		getFinanceDetail().getFinScheduleData().getFinanceMain().setRefundAmount(this.totRefundAmt.getValue());
		this.doWriteComponentsToBean(false);

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

	public void onSelectAgreementDetailTab(ForwardEvent event) throws IllegalAccessException,
			InvocationTargetException, InterruptedException, ParseException {
		this.doWriteComponentsToBean(false);

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
		int format = CurrencyUtil.getFormat(finMain.getFinCcy());
		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(finMain.getFinReference());
		Date dateValueDate = DateUtility.getAppValueDate();

		finMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(repayAccount.getValue()));

		Date curBDay = DateUtility.getAppDate();
		aeEvent = AEAmounts.procAEAmounts(finMain, getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails(), profitDetail, eventCode, curBDay, dateValueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		//Set Repay Amount Codes
		amountCodes.setRpTot(PennantApplicationUtil.unFormateAmount(
				this.pftPayment.getValue().add(this.priPayment.getValue()), format));
		amountCodes.setRpPft(PennantApplicationUtil.unFormateAmount(this.pftPayment.getValue(), format));
		amountCodes.setRpPri(PennantApplicationUtil.unFormateAmount(this.priPayment.getValue(), format));
		amountCodes.setRefund(PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), format));
		amountCodes.setInsRefund(PennantApplicationUtil.unFormateAmount(this.insRefundAmt.getValue(), format));
		amountCodes.setInsPay(PennantApplicationUtil.unFormateAmount(this.insPayment.getValue(), format));
		amountCodes.setSchFeePay(PennantApplicationUtil.unFormateAmount(this.schdFeeAmount.getValue(), format));
		amountCodes.setSuplRentPay(PennantApplicationUtil.unFormateAmount(this.suplRentAmount.getValue(), format));
		amountCodes.setIncrCostPay(PennantApplicationUtil.unFormateAmount(this.incrCostAmount.getValue(), format));

		HashMap<String, Object> dataMap = aeEvent.getDataMap();

		List<ReturnDataSet> returnSetEntries = null;

		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);
		aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);

		returnSetEntries = aeEvent.getReturnDataSet();

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			/*
			 * if(!StringUtils.trimToEmpty(finMain.getFinCommitmentRef()).equals("")){
			 * 
			 * Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
			 * 
			 * if(commitment != null && commitment.isRevolving()){ AECommitment aeCommitment = new AECommitment();
			 * aeCommitment.setCMTAMT(BigDecimal.ZERO); aeCommitment.setCHGAMT(BigDecimal.ZERO);
			 * aeCommitment.setDISBURSE(BigDecimal.ZERO);
			 * aeCommitment.setRPPRI(CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(),
			 * amountCodes.getRpPri()));
			 * 
			 * List<ReturnDataSet> cmtEntries = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment,
			 * AccountEventConstants.ACCEVENT_CMTRPY, "N", null);
			 * getAccountingDetailDialogCtrl().doFillCmtAccounting(cmtEntries, commitment.getCcyEditField());
			 * getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList().addAll(cmtEntries); } }
			 */
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
			financeDetail = getManualPaymentService().getAccountingDetail(financeDetail, eventCode);

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
	private boolean doProcess(RepayData aRepayData, String tranType) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
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
					RepayData tRepayData = (RepayData) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tRepayData);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				RepayData tRepayData = (RepayData) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tRepayData.getFinanceDetail().getFinScheduleData()
						.getFinanceMain(), finishedTasks);

			}

			RepayData tRepayData = (RepayData) auditHeader.getAuditDetail().getModelData();

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

		RepayData aRepayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = aRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getManualPaymentService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getManualPaymentService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getManualPaymentService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ManualPaymentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ManualPaymentDialog, auditHeader);
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

		setRepaySchdList(sortRpySchdDetails(repaySchdList));
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
		this.totPenaltyAmt.setValue(PennantAppUtil.formateAmount(totalCharge, finFormatter));

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

				//Open If Allow On change of particular Refund Amount per every Schedule term Individually
				/*
				 * Decimalbox refundPft = new Decimalbox(); refundPft.setWidth("99.9%"); refundPft.setMaxlength(18);
				 * refundPft.setFormat(PennantAppUtil.getAmountFormate(finFormatter)); refundPft.setStyle("border:0px");
				 * if(repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
				 * 
				 * refundPft.setDisabled(true);//isReadOnly("ManualPaymentDialog_refundPft")
				 * refundPft.setInplace(false);//!isReadOnly("ManualPaymentDialog_refundPft") List<Object> list = new
				 * ArrayList<Object>(2); list.add(refundPft); list.add(repaySchd.getSchDate());
				 * 
				 * if(repaySchd.isAllowRefund()){
				 * 
				 * refundPft.setValue(PennantAppUtil.formateAmount((repaySchd.getRefundReq()).compareTo(BigDecimal.ZERO)
				 * == 0? BigDecimal.ZERO:repaySchd.getRefundReq(), finFormatter)); totalRefund =
				 * totalRefund.add(repaySchd.getRefundReq());
				 * 
				 * }else if(repaySchd.isAllowWaiver()){
				 * 
				 * refundPft.setValue(PennantAppUtil.formateAmount(repaySchd.getWaivedAmt(),finFormatter)); totalWaived
				 * = totalWaived.add(repaySchd.getWaivedAmt());
				 * 
				 * } refundPft.addForward("onChange",window_ManualPaymentDialog,"onRefundValueChanged", list); }else {
				 * refundPft.setDisabled(true);
				 * refundPft.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,finFormatter));
				 * refundPft.setStyle("text-align:right;background:none;border:0px;font-color:#AAAAAA;"); }
				 */

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
			this.totRefundAmt.setValue(PennantAppUtil.formateAmount(totalRefund, finFormatter));
			this.oldVar_totRefundAmt = totalRefund;
			this.totWaivedAmt.setValue(PennantAppUtil.formateAmount(totalWaived, finFormatter));
			this.totPenaltyAmt.setValue(PennantAppUtil.formateAmount(totalCharge, finFormatter));

			// Fee Details
			this.insPayment.setValue(PennantAppUtil.formateAmount(totInsPaid, finFormatter));
			this.schdFeeAmount.setValue(PennantAppUtil.formateAmount(totSchdFeePaid, finFormatter));
			this.suplRentAmount.setValue(PennantAppUtil.formateAmount(totSchdSuplRentPaid, finFormatter));
			this.incrCostAmount.setValue(PennantAppUtil.formateAmount(totSchdIncrCostPaid, finFormatter));

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
		isRefundExceeded = false;
		int finFormatter = getRepayData().getRepayMain().getLovDescFinFormatter();
		if (refundMap.containsKey(schDate)) {
			RepayScheduleDetail repaySchd = refundMap.get(schDate);

			if (repaySchd.isAllowRefund()) {
				if (repaySchd.getRefundMax().compareTo(
						PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter)) < 0) {
					MessageUtil.showError("Refund amount exceeded ... ");
					isRefundExceeded = true;
					return;
				}
				repaySchd.setRefundReq(PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter));
			} else if (repaySchd.isAllowWaiver()) {
				if (repaySchd.getMaxWaiver().compareTo(
						PennantAppUtil.unFormateAmount(refundProfit.getValue(), finFormatter)) < 0) {
					MessageUtil.showError("Waiver Amount exceeded ... ");
					isRefundExceeded = true;
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
	@SuppressWarnings("unused")
	private boolean isValid(boolean isChgRpy, boolean isSaveProcess) throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		Date curBussDate = DateUtility.getAppDate();
		if (getFinanceDetail().getFinScheduleData().getFinanceMain() != null
				&& curBussDate.compareTo(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
			MessageUtil.showError("Disbursement Date is Same as Current Business Date. Not Allowed for Repayment. ");
			return false;
		}

		//Check Early Settlement Date, EITHER Equal to Current Buss Date or Last Business Value Date
		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

			this.earlySettlementDate.setConstraint("");
			this.earlySettlementDate.setErrorMessage("");

			Date lastBussDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_LAST);
			if (this.earlySettlementDate.getValue() == null) {
				this.earlySettlementDate.setValue(curBussDate);
			}
			if (isChgRpy
					&& !this.earlySettlementDate.isDisabled()
					&& (lastBussDate.compareTo(this.earlySettlementDate.getValue()) > 0 || curBussDate
							.compareTo(this.earlySettlementDate.getValue()) < 0)) {
				throw new WrongValueException(this.earlySettlementDate, Labels.getLabel(
						"label_EarlySettlementDate",
						new String[] { DateUtility.formatToLongDate(lastBussDate),
								DateUtility.formatToLongDate(curBussDate) }));
			}
		}

		if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			if (this.earlySettlementDate.getValue() == null) {
				this.earlySettlementDate.setValue(curBussDate);
			}

			if (isChgRpy && !this.earlySettlementDate.isDisabled()
					&& curBussDate.compareTo(this.earlySettlementDate.getValue()) > 0) {
				throw new WrongValueException(this.earlySettlementDate, Labels.getLabel(
						"label_MIN_EarlySettlementDate", new String[] { curBussDate.toString() }));
			}

			logger.debug("Leaving");
			return true;
		}

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		if (isChgRpy && StringUtils.isBlank(this.repayAccount.getValue())
				&& ImplementationConstants.ACCOUNTS_APPLICABLE) {
			String validationMsg = "Please Enter " + Labels.getLabel("label_PaymentDialog_RepayAccount.value");
			MessageUtil.showError(validationMsg);
			return false;
		} else if (!rpyAmount.isDisabled() && this.rpyAmount.getActualValue().compareTo(BigDecimal.ZERO) == 0) {
			String validationMsg = "Please Enter " + Labels.getLabel("label_PaymentDialog_RpyAmount.value");
			MessageUtil.showError(validationMsg);
			return false;
		} else if (!this.rpyAmount.isDisabled() && this.rpyAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {

			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYRPY)) {

				// Check Whether Any Future Payments already done in Schedule or not
				List<FinanceScheduleDetail> scheduleList = getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails();
				BigDecimal closingBal = null;
				boolean futureInstPaid = false;
				for (int i = 0; i < scheduleList.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleList.get(i);
					if (DateUtility.compare(DateUtility.getAppDate(), curSchd.getSchDate()) > 0) {
						closingBal = curSchd.getClosingBalance();
						continue;
					}

					if (DateUtility.compare(DateUtility.getAppDate(), curSchd.getSchDate()) == 0 || closingBal == null) {
						closingBal = curSchd.getClosingBalance();
					}

					if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdInsPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSuplRentPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getIncrCostPaid().compareTo(BigDecimal.ZERO) > 0) {

						futureInstPaid = true;
						break;
					}

				}
				if (futureInstPaid) {
					MessageUtil.showError(Labels.getLabel("label_PaymentDialog_PartialSettlement_Future"));
					return false;
				} else if (closingBal != null) {
					BigDecimal payAmount = PennantApplicationUtil.unFormateAmount(this.rpyAmount.getActualValue(),
							formatter);
					if (payAmount.compareTo(closingBal) > 0) {
						MessageUtil.showError(Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
								new String[] { Labels.getLabel("label_PaymentDialog_RpyAmount.value"),
										PennantApplicationUtil.amountFormate(closingBal, formatter) }));
						return false;
					}
				} else {
					//Checking Total Allowed Advance Amount must be less than or equal to Total Principal Due.
					if (this.rpyAmount.getActualValue().compareTo(this.totPriDue.getValue()) > 0) {

						MessageUtil
								.showError(
										Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
												new String[] { Labels.getLabel("label_PaymentDialog_RpyAmount.value"),
														PennantApplicationUtil.amountFormate(
																PennantApplicationUtil.unFormateAmount(
																		this.totPriDue.getValue(), formatter),
																formatter) }));
						return false;
					}
				}
			}

			if (!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) && isChgRpy) {

				if (StringUtils.isNotBlank(this.repayAccount.getValue())) {
					iAccount = getAccountInterfaceService().fetchAccountAvailableBal(
							PennantApplicationUtil.unFormatAccountNumber(this.repayAccount.getValue()));

					// Check Available Funding Account Balance
					boolean accountTypeFound = false;
					if (getFinanceType() != null) {
						if (StringUtils.trimToEmpty(getFinanceType().getFinDivision()).equals(
								FinanceConstants.FIN_DIVISION_TREASURY)) {
							String acType = SysParamUtil.getValueAsString("ALWFULLPAY_TSR_ACTYPE");

							//Account Type Check
							String[] acTypeList = acType.split(",");
							for (int i = 0; i < acTypeList.length; i++) {
								if (StringUtils.equals(iAccount.getAcType(), acTypeList[i].trim())) {
									accountTypeFound = true;
									break;
								}
							}

						} else {

							//Account Type Check
							String acType = SysParamUtil.getValueAsString("ALWFULLPAY_NONTSR_ACTYPE");
							String[] acTypeList = acType.split(",");
							for (int i = 0; i < acTypeList.length; i++) {
								if (StringUtils.equals(iAccount.getAcType(), acTypeList[i].trim())) {
									accountTypeFound = true;
									break;
								}
							}
						}
					}

					if (!accountTypeFound) {

						if (PennantAppUtil.unFormateAmount(
								this.rpyAmount
										.getValidateValue()
										.subtract(this.totRefundAmt.getValue())
										.subtract(
												insRefundAmt.getValue() == null ? BigDecimal.ZERO : insRefundAmt
														.getValue()),
								getRepayData().getRepayMain().getLovDescFinFormatter()).compareTo(
								iAccount.getAcAvailableBal()) > 0) {
							MessageUtil.showError(Labels.getLabel("label_InsufficientBalance"));
							return false;
						}
					}
				}
			}

		}
		if (getRepayMain().getRepayAmountExcess().compareTo(BigDecimal.ZERO) > 0) {
			MessageUtil.showError("Entered amount is more than required ...");
			return false;
		}

		//Refund Rule Validation Process Check
		if (isSaveProcess
				&& (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) || moduleDefiner
						.equals(FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) && !isRefundAmtValidated()) {
			setRefundAmtValidated(true);
			return false;
		}

		//Insurance Refund rule Process Check
		if (isSaveProcess && !this.insRefundAmt.isDisabled()) {
			if (this.insRefundAmt.getValue() == null) {
				this.insRefundAmt.setValue(BigDecimal.ZERO);
			}
			if (this.actInsRefundAmt.getValue() == null) {
				this.actInsRefundAmt.setValue(BigDecimal.ZERO);
			}
			if (this.insRefundAmt.getValue().compareTo(this.actInsRefundAmt.getValue()) > 0) {
				String amount = PennantApplicationUtil.amountFormate(
						PennantApplicationUtil.unFormateAmount(this.actInsRefundAmt.getValue(), formatter), formatter);
				MessageUtil.showError("Insurance Amount must not be greater than value: " + amount);
				return false;
			}
		}

		// Payment Apportionment Validation
		if (!isChgRpy && this.row_payApportionment.isVisible()) {
			isValidComboValue(this.paymentApportionment,
					Labels.getLabel("label_PaymentDialog_PaymentApportionment.value"));
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
		chartsConfig.setRemarks(ChartType.PIE3D.getRemarks()+" decimals='" + formatter + "'");
		String chartStrXML = chartsConfig.getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.PIE3D.toString());
		chartDetail.setChartHeight("180");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");
		chartDetailList.add(chartDetail);

		//For Repayments Chart 
		chartsConfig = new ChartsConfig("Payments", "", "", "");
		chartsConfig.setSetElements(getReportDataForRepayments(finScheduleData, formatter));
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig
				.setRemarks(ChartType.MSLINE.getRemarks()+" decimals='"+ formatter + "'");
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
							"Payment Amount", PennantAppUtil.formateAmount(curSchd.getRepayAmount(), format).setScale(
									formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Principal", PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(), format).setScale(
									formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Interest", PennantAppUtil.formateAmount(curSchd.getProfitSchd(), format).setScale(
									formatter, RoundingMode.HALF_UP));
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

		BigDecimal downPayment = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal schedulePrincipal = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		int format = CurrencyUtil.getFormat(scheduleData.getFinanceMain().getFinCcy());

		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();

		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;
			financeAmount = BigDecimal.ZERO;
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				financeAmount = financeAmount.add(PennantAppUtil.formateAmount(curSchd.getDisbAmount(), format));
				downPayment = downPayment.add(PennantAppUtil.formateAmount(curSchd.getDownPaymentAmount(), format));
				capitalized = capitalized.add(PennantAppUtil.formateAmount(curSchd.getCpzAmount(), format));

				scheduleProfit = scheduleProfit.add(PennantAppUtil.formateAmount(curSchd.getProfitSchd(), format));
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
	private AuditHeader getAuditHeader(RepayData repayData, String tranType) {
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
		earlySettlement.setDeptFrom(getFinanceType().getLovDescFinDivisionName());
	/*	boolean isRetail = false;
		if (getFinanceType() != null) {
			String division = getFinanceType().getFinDivision().trim();
			if (StringUtils.equals(division, FinanceConstants.FIN_DIVISION_RETAIL)) {
				reportName = FinanceConstants.FIN_DIVISION_RETAIL + "_InternalMemorandum.docx";
				isRetail = true;
			}
			earlySettlement.setDeptFrom(getFinanceType().getLovDescFinDivisionName());
		}*/

		if (financeMain != null) {
			earlySettlement.setAppDate(DateUtility.getAppDate(DateFormat.SHORT_DATE));
			earlySettlement.setFinReference(financeMain.getFinReference());
			earlySettlement.setFinType(financeMain.getFinType());
			earlySettlement.setFinTypeDesc(getFinanceType().getFinTypeDesc());
			if(getFinanceDetail().getCustomerDetails() != null && getFinanceDetail().getCustomerDetails().getCustomer() != null){
				earlySettlement.setCustCIF("CIF " + getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF());
				earlySettlement.setCustShrtName(getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
			}
			earlySettlement.setFinStartDate(DateUtility.formatToLongDate(financeMain.getFinStartDate()));
			earlySettlement.setEarlySettlementDate(DateUtility.formatToLongDate(this.earlySettlementDate.getValue()));
		}

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		FinanceProfitDetail profitDetail = getManualPaymentService().getPftDetailForEarlyStlReport(
				financeMain.getFinReference());
		if (profitDetail != null) {
			BigDecimal financeAmount = financeMain.getFinAmount()
					.add(financeMain.getFeeChargeAmt() != null ? financeMain.getFeeChargeAmt() : BigDecimal.ZERO)
					.subtract(financeMain.getDownPayment())
					.add(financeMain.getInsuranceAmt() != null ? financeMain.getInsuranceAmt() : BigDecimal.ZERO);
			earlySettlement.setTotalPaidAmount(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(financeAmount, formatter));
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

			BigDecimal totalRefund = PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), formatter);
			BigDecimal discountPerc = BigDecimal.ZERO;
			if (profitDetail.getTotalPftBal().compareTo(BigDecimal.ZERO) != 0) {
				discountPerc = (totalRefund.divide(profitDetail.getTotalPftBal(), 2, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal(100));
			}
			earlySettlement.setDiscountPerc(discountPerc + " %");
			earlySettlement.setDiscountAmount(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(totalRefund, formatter));

			BigDecimal insAmount = BigDecimal.ZERO;
			FeeRule feeRule = getFinanceDetailService().getInsFee(financeMain.getFinReference());
			if (feeRule != null && feeRule.getFeeAmount() != null) {
				insAmount = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount())
						.subtract(feeRule.getPaidAmount());
			}
			earlySettlement.setInsuranceFee(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(insAmount, formatter));

			int remMonths = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), repayData.getRepayMain()
					.getRefundCalStartDate() == null ? financeMain.getMaturityDate() : repayData.getRepayMain()
					.getRefundCalStartDate(), true);
			int totalMonths = DateUtility.getMonthsBetween(financeMain.getMaturityDate(),
					financeMain.getFinStartDate(), false);

			earlySettlement.setPeriodCoverage(String.valueOf(totalMonths - remMonths));
			BigDecimal insRefnd = PennantApplicationUtil.unFormateAmount(this.insRefundAmt.getValue(), formatter);
			earlySettlement.setPeriodCoverageAmt(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(insAmount.subtract(insRefnd), formatter));
			earlySettlement.setReturnInsAmount(financeMain.getFinCcy() + " "
					+ PennantApplicationUtil.amountFormate(insRefnd, formatter));
			earlySettlement.setTotCustPaidAmount(financeMain.getFinCcy()
					+ " "
					+ PennantApplicationUtil.amountFormate(
							profitDetail.getTotalPriBal().add(profitDetail.getTotalPftBal()).subtract(insRefnd)
									.subtract(totalRefund), formatter));
		}

		//Word Format
		/*if (isRetail) {
			try {

				TemplateEngine engine = new TemplateEngine(reportName);
				//reportName = earlySettlement.getFinReference() + "_" + "Memorandum.docx";
				engine.setTemplate("");
				//engine.loadTemplateWithFontSize(11);
				engine.mergeFields(earlySettlement);
				engine.showDocument(this.window_ManualPaymentDialog, reportName, SaveFormat.DOCX);
				engine.close();
				engine = null;

			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		} else {*/
			// PDF Format
			ReportGenerationUtil.generateReport(reportName, earlySettlement, new ArrayList<Object>(), true, 1,
					getUserWorkspace().getLoggedInUser().getFullName(), this.window_ManualPaymentDialog);
		//}

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
	
	/** new code to display chart by skipping jsps code start */
	public void onSelectDashboardTab(Event event) throws InterruptedException {
		logger.debug("Entering");
		for (ChartDetail chartDetail : chartDetailList) {
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul",
					(Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"),
					Collections.singletonMap("chartDetail", chartDetail));
		}
		chartDetailList =  new ArrayList<ChartDetail>(); // Resetting 
		logger.debug("Leaving");
	}
	/** new code to display chart by skipping jsps code end */

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

	public RepayData getRepayData() {
		return repayData;
	}

	public void setRepayData(RepayData repayData) {
		this.repayData = repayData;
	}

	public RepayMain getRepayMain() {
		return repayMain;
	}

	public void setRepayMain(RepayMain repayMain) {
		this.repayMain = repayMain;
	}

	public List<RepayScheduleDetail> getRepaySchdList() {
		return repaySchdList;
	}

	public void setRepaySchdList(List<RepayScheduleDetail> repaySchdList) {
		this.repaySchdList = repaySchdList;
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

	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	public ManualPaymentService getManualPaymentService() {
		return manualPaymentService;
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

	public FinRepayHeader getFinRepayHeader() {
		return finRepayHeader;
	}

	public void setFinRepayHeader(FinRepayHeader finRepayHeader) {
		this.finRepayHeader = finRepayHeader;
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

	public RepayCalculator getRepayCalculator() {
		return repayCalculator;
	}

	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
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

}