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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.EarlySettlementReportData;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceScheduleDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.TemplateEngine;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * WEB-INF/pages/FinanceManagement/Payments/ManualPayment.zul <br/>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ManualPaymentDialogCtrl extends GFCBaseListCtrl<FinanceMain> {

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger.getLogger(ManualPaymentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 			window_ManualPaymentDialog;
	protected Borderlayout		borderlayout_ManualPayment;

	//Summary Details
	protected Textbox 			finType;
	protected Textbox 			finReference;
	protected Textbox 			finCcy;
	protected Textbox 			lovDescFinCcyName;
	protected Combobox 			profitDayBasis;
	protected Longbox 			custID;
	protected Textbox 			lovDescCustCIF;
	protected Label   			custShrtName;	
	protected Textbox 			finBranch;
	protected Textbox 			lovDescFinBranchName;
	protected Datebox 			finStartDate;
	protected Datebox 			maturityDate;	
	protected Decimalbox 		totDisbursements;
	protected Decimalbox 		totDownPayment;
	protected Decimalbox 		totCpzAmt;
	protected Decimalbox 		totPriAmt;
	protected Decimalbox 		totPftAmt;
	protected Decimalbox 		totFeeAmt;
	protected Decimalbox 		totChargeAmt;
	protected Decimalbox 		totWaiverAmt;
	protected Decimalbox 		schPriTillNextDue;
	protected Decimalbox 		schPftTillNextDue;
	protected Decimalbox 		totPriPaid;
	protected Decimalbox 		totPftPaid;
	protected Decimalbox 		totPriDue;
	protected Decimalbox 		totPftDue;

	//Repayment Details
	protected Textbox 			finType1;
	protected Textbox 			finReference1;
	protected Textbox 			finCcy1;
	protected Textbox 			lovDescFinCcyName1;
	protected Longbox 			custID1;
	protected Textbox 			lovDescCustCIF1;
	protected Label   			custShrtName1;	
	protected Textbox 			finBranch1;
	protected Textbox 			lovDescFinBranchName1;
	protected CurrencyBox 		rpyAmount;
	protected AccountSelectionBox 			repayAccount;
	protected Decimalbox 		priPayment;
	protected Decimalbox 		pftPayment;
	protected Decimalbox 		totPenaltyAmt;
	protected Combobox 			earlyRpyEffectOnSchd;
	protected Decimalbox 		totRefundAmt;
	protected BigDecimal 		oldVar_totRefundAmt;
	protected Decimalbox 		totWaivedAmt;
	protected Label 			label_PaymentDialog_InsRefundAmt;
	protected Hbox           	hbox_insRefundAmt;
	protected Decimalbox 		insRefundAmt;
	protected Decimalbox 		actInsRefundAmt;
	protected Row 				row_paidByCustomer;
	protected Decimalbox 		paidByCustomer;

	//Effective Schedule Tab Details
	protected Label 			finSchType;
	protected Label 			finSchCcy;
	protected Label 			finSchMethod;
	protected Label 			finSchProfitDaysBasis;
	protected Label 			finSchReference;
	protected Label 			finSchGracePeriodEndDate;
	protected Label 			effectiveRateOfReturn;

	//Early Settlement Inquiry Fields
	protected Label 			label_PaymentDialog_RepayAccount;
	protected Row 				row_EarlyRepayEffectOnSchd;
	protected Row 				row_EarlySettleDate;
	protected Datebox 			earlySettlementDate;
	protected Decimalbox 		earlySettlementBal;
	protected Combobox 			earlySettlementTillDate;
	protected Label				label_PaymentDialog_EarlySettlementTillDate;
	protected Hbox				hbox_esTilllDate;

	protected Listbox			listBoxSchedule;

	//Invisible Fields
	protected Decimalbox 		overDuePrincipal;
	protected Decimalbox 		overDueProfit;
	protected Datebox 			lastFullyPaidDate;
	protected Datebox 			nextPayDueDate;
	protected Decimalbox 		accruedPft;
	protected Decimalbox 		pendingODC;
	protected Decimalbox 		provisionedAmt;
	protected Row 				row_provisionedAmt;

	protected Grid 				grid_Summary;
	protected Grid 				grid_Repayment;
	protected Tabbox 			tabbox;
	protected Button 			btnHelp;
	protected Button 			btnClose;
	protected Button 			btnPrint;

	protected Tab 				summaryDetailsTab;
	protected Tab 				repaymentDetailsTab;
	protected Tab 				effectiveScheduleTab;
	private Div           		graphDivTabDiv;
	private BigDecimal 			financeAmount;

	protected Label 			recordStatus; 				// autoWired
	protected Radiogroup 		userAction;					// autoWired
	protected Groupbox 			groupboxWf;					// autoWired
	protected South				south;						// autoWired

	//Buttons
	protected Button 			btnPay;
	protected Button 			btnChangeRepay;
	protected Button 			btnCalcRepayments;
	protected Button 			btnNotes;
	protected Listbox 			listBoxPayment;

	private transient FinanceScheduleDetailService finScheduleDetailService;
	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private transient AccountsService accountsService;
	private transient AccountInterfaceService accountInterfaceService;
	private transient RepaymentPostingsUtil postingsUtil;
	private transient RuleService ruleService;
	private transient CustomerDetailsService customerDetailsService;
	private transient FinanceTypeService financeTypeService;
	private transient ManualPaymentService manualPaymentService;
	private transient ProvisionService provisionService;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private transient FinanceDetailService financeDetailService;
	private transient RuleExecutionUtil ruleExecutionUtil;
	private transient AccountEngineExecution engineExecution;
	private transient AccountEngineExecutionRIA engineExecutionRIA;
	private transient CommitmentService commitmentService;
	private transient CarLoanDetailService carLoanDetailService;

	private transient FinanceSelectCtrl financeSelectCtrl = null;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl = null;
	private transient FeeDetailDialogCtrl feeDetailDialogCtrl = null;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl = null;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl = null; 
	
	private FinanceMain financeMain  = null;
	private FinanceType financeType  = null;
	private List<FinanceScheduleDetail> finSchDetails = null;
	private FinRepayHeader finRepayHeader = null;
	private FinanceDetail financeDetail;
	private RepayData repayData = null;
	private RepayMain repayMain = null;
	private List<RepayScheduleDetail> repaySchdList = null;
	private List<Rule> feeChargesList = null;
	private List<FeeRule> feeRuleList = null;
	private List<DocumentDetails> docList = null;
	private List<FinanceReferenceDetail> agreementList = null;
	
	private IAccounts iAccount;
	private LinkedHashMap<String,RepayScheduleDetail> refundMap;
	private boolean isLimitExceeded = false;
	private String menuItemRightName = null;
	private boolean notes_Entered = false;
	private boolean isSchdRecal = false;
	protected AEAmountCodes 			amountCodes; 					// over handed per parameters
	
	protected Tabs 			tabsIndexCenter;
	protected Tabpanels 	tabpanelsBoxIndexCenter;

	private String moduleDefiner = "";
	private String eventCode = "";
	private final List<ValueLabel> profitDayList = PennantAppUtil.getProfitDaysBasis();
	private final List<ValueLabel> earlyRpyEffectList = PennantStaticListUtil.getScheduleOn();
	private List<ValueLabel> profitDaysBasisList = PennantAppUtil.getProfitDaysBasis();
	private List<ValueLabel> schMethodList = PennantAppUtil.getScheduleMethod();

	private MailUtil mailUtil;
	/**
	 * default constructor.<br>
	 */
	public ManualPaymentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ManualPaymentDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try {
			// get the parameters map that are over handed by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED parameters !
			if (args.containsKey("repayData")) {
				setRepayData((RepayData) args.get("repayData"));
				FinanceMain befImage = new FinanceMain();
				financeMain = getRepayData().getFinanceMain();
				finSchDetails = getRepayData().getScheduleDetails();
				finRepayHeader = getRepayData().getFinRepayHeader();
				setRepaySchdList(getRepayData().getRepayScheduleDetails());
				setFeeChargesList(getRepayData().getFeeCharges());
				setFeeRuleList(getRepayData().getFeeRuleList());
				setFinanceType(getRepayData().getFinanceType());
				setDocList(getRepayData().getDocumentDetailList());
				setAgreementList(getRepayData().getAggrementList());
				
				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(financeMain);
				getRepayData().getFinanceMain().setBefImage(befImage);

			}

			if (args.containsKey("moduleDefiner")) {
				moduleDefiner = (String) args.get("moduleDefiner");
			}
			
			if (args.containsKey("eventCode")) {
				eventCode = (String) args.get("eventCode");
			}

			if (args.containsKey("menuItemRightName")) {
				menuItemRightName = (String) args.get("menuItemRightName");
			}
			
			if (args.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) args.get("financeSelectCtrl"));
			} 

			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
				if(recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)){
					this.userAction = setRejectRecordStatus(this.userAction);
				}else {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().alocateMenuRoleAuthorities(getRole(), "ManualPaymentDialog", menuItemRightName);	
				}
			}else{
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			// READ OVERHANDED parameters !
			if(!doFillFinanceData(false)){
				
				// set Read only mode accordingly if the object is new or not.
				doEdit();
				if (!StringUtils.trimToEmpty(financeMain.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}else{
					if(moduleDefiner.equals(PennantConstants.SCH_REPAY)){
						this.btnPay.setDisabled(true);
						this.btnChangeRepay.setDisabled(true);
						
						this.totRefundAmt.setDisabled(true);
					}
				}
				
				//Reset Finance Repay Header Details
				doWriteBeanToComponents();
				
				setDialog(this.window_ManualPaymentDialog);

				this.borderlayout_ManualPayment.setHeight(getBorderLayoutHeight());
				int rowCount = grid_Summary.getRows().getVisibleItemCount() + grid_Repayment.getRows().getVisibleItemCount();
				int dialogHeight =  rowCount * 20 + 110; 
				int listboxHeight = borderLayoutHeight-dialogHeight;
				this.listBoxPayment.setHeight(listboxHeight+"px");
				this.repaymentDetailsTab.setSelected(true);
				this.rpyAmount.setFocus(true);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.window_ManualPaymentDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		if(!moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
			getUserWorkspace().alocateAuthorities("ManualPaymentDialog",getRole(), menuItemRightName);
			
			this.btnPay.setVisible(getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnPay"));
			this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
			this.btnCalcRepayments.setVisible(getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnCalRepay"));
			
			this.btnPay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnPay"));
			this.btnCalcRepayments.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnCalRepay"));
			this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
		}else{
			this.btnCalcRepayments.setVisible(true);
			this.btnPrint.setVisible(true);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		
		int formatter = financeMain.getLovDescFinFormatter();

		this.finType.setMaxlength(8);
		this.finReference.setMaxlength(20);
		this.finCcy.setMaxlength(8);
		this.lovDescCustCIF.setMaxlength(6);
		this.finBranch.setMaxlength(8);
		this.finStartDate.setFormat(PennantConstants.dateFormate);
		this.maturityDate.setFormat(PennantConstants.dateFormate);	
		this.totDisbursements.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totDownPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totCpzAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPriAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPftAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totFeeAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totChargeAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totWaiverAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.schPriTillNextDue.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.schPftTillNextDue.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPriPaid.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPftPaid.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPriDue.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPftDue.setFormat(PennantAppUtil.getAmountFormate(formatter));

		this.finType1.setMaxlength(8);
		this.finReference1.setMaxlength(20);
		this.finCcy1.setMaxlength(8);
		this.lovDescCustCIF1.setMaxlength(6);
		this.finBranch1.setMaxlength(8);
		this.rpyAmount.setMandatory(true);
		this.rpyAmount.setMaxlength(18);
		this.rpyAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.repayAccount.setMandatoryStyle(true);
		this.priPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.pftPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totPenaltyAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totRefundAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.insRefundAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.actInsRefundAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.paidByCustomer.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.totWaivedAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));

		this.overDuePrincipal.setMaxlength(18);
		this.overDuePrincipal.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.overDueProfit.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.lastFullyPaidDate.setFormat(PennantConstants.dateFormat);
		this.nextPayDueDate.setFormat(PennantConstants.dateFormat);
		this.accruedPft.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.provisionedAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.pendingODC.setFormat(PennantAppUtil.getAmountFormate(formatter));

		this.earlySettlementDate.setFormat(PennantConstants.dateFormat);
		this.earlySettlementBal.setFormat(PennantAppUtil.getAmountFormate(formatter));

		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		this.repayAccount.setReadonly(isReadOnly("ManualPaymentDialog_RepayAccount"));
		if(moduleDefiner.equals(PennantConstants.SCH_REPAY)){
			this.rpyAmount.setDisabled(isReadOnly("ManualPaymentDialog_RepayAmount"));
		}else{
			this.rpyAmount.setDisabled(true);
		}
		
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			this.insRefundAmt.setDisabled(isReadOnly("ManualPaymentDialog_insRefund"));
		}else{
			this.insRefundAmt.setDisabled(true);
		}
		this.totRefundAmt.setDisabled(isReadOnly("ManualPaymentDialog_refundPft"));
		this.earlySettlementDate.setDisabled(isReadOnly("ManualPaymentDialog_EarlyPayDate"));
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
	private boolean doFillFinanceData(boolean isChgRpy) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		RepayData repayData = new RepayData();
		repayData.setBuildProcess("I");

		int finformatter = financeMain.getLovDescFinFormatter();
		
		FinanceMain aFinanceMain = new FinanceMain();
		List<FinanceScheduleDetail> financeScheduleDetails = new ArrayList<FinanceScheduleDetail>();
		
		if(isChgRpy){
			
			FinScheduleData data = getFinanceDetailService().getFinSchDataById(financeMain.getFinReference(), "_AView", false);
			aFinanceMain = data.getFinanceMain();
			financeScheduleDetails = data.getFinanceScheduleDetails();
		}else{

			Cloner cloner = new Cloner();
			aFinanceMain = cloner.deepClone(financeMain);
			financeScheduleDetails = cloner.deepClone(finSchDetails);
		}
		
		String accTypeEvent = "";
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			accTypeEvent = PennantConstants.FinanceAccount_ERLS;
		}else if(moduleDefiner.equals(PennantConstants.SCH_REPAY)){
			accTypeEvent = PennantConstants.FinanceAccount_REPY;
		}
		
		this.repayAccount.setFinanceDetails(aFinanceMain.getFinType(), accTypeEvent, aFinanceMain.getFinCcy());
		this.repayAccount.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.repayAccount.setFormatter(aFinanceMain.getLovDescFinFormatter());
		this.repayAccount.setBranchCode(aFinanceMain.getFinBranch());

		repayData.setAccruedTillLBD(aFinanceMain.getLovDescAccruedTillLBD());
		repayData.setFinanceType(getFinanceType());
		setRepayData(RepayCalculator.initiateRepay(repayData, aFinanceMain, financeScheduleDetails,"", null, false, null, null));
		repayData.getRepayMain().setLovDescFinFormatter(finformatter);
		setRepayMain(repayData.getRepayMain());

		this.finType.setValue(getRepayMain().getFinType());
		this.finReference.setValue(getRepayMain().getFinReference());
		this.finCcy.setValue(getRepayMain().getFinCcy());
		this.lovDescFinCcyName.setValue(getRepayMain().getFinCcy() + "-"+ getRepayMain().getLovDescFinCcyName());
		fillComboBox(this.profitDayBasis, getRepayMain().getProfitDaysBais(), profitDayList, "");
		this.custID.setValue(getRepayMain().getCustID());
		this.lovDescCustCIF.setValue(getRepayMain().getLovDescCustCIF());
		this.custShrtName.setValue(getRepayMain().getLovDescCustShrtName());
		this.finBranch.setValue(getRepayMain().getFinBranch());
		this.lovDescFinBranchName.setValue(getRepayMain().getFinBranch() + "-"+ getRepayMain().getLovDescFinBranchName());
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
		this.finType1.setValue(getRepayMain().getFinType());
		this.finReference1.setValue(getRepayMain().getFinReference());
		this.finCcy1.setValue(getRepayMain().getFinCcy());
		this.lovDescFinCcyName1.setValue(getRepayMain().getFinCcy() + "-"+ getRepayMain().getLovDescFinCcyName());
		this.custID1.setValue(getRepayMain().getCustID());
		this.lovDescCustCIF1.setValue(getRepayMain().getLovDescCustCIF());
		this.custShrtName1.setValue(getRepayMain().getLovDescCustShrtName());
		this.finBranch1.setValue(getRepayMain().getFinBranch());
		this.lovDescFinBranchName1.setValue(getRepayMain().getFinBranch() + "-"+ getRepayMain().getLovDescFinBranchName());
		if(!isChgRpy) {
			this.rpyAmount.setValue(PennantAppUtil.formateAmount(getRepayMain().getRepayAmountNow(), finformatter));
			this.repayAccount.setValue(getRepayMain().getRepayAccountId());
		}
		this.priPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalPayNow(), finformatter));
		this.pftPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitPayNow(), finformatter));
		fillComboBox(this.earlyRpyEffectOnSchd, getRepayMain().getEarlyPayEffectOn(), earlyRpyEffectList, "");
		this.totRefundAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getRefundNow(), finformatter));
		this.totWaivedAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

		this.overDuePrincipal.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverduePrincipal(), finformatter));
		this.overDueProfit.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverdueProfit(), finformatter));
		this.lastFullyPaidDate.setValue(getRepayMain().getDateLastFullyPaid());
		this.nextPayDueDate.setValue(getRepayMain().getDateNextPaymentDue());
		this.accruedPft.setValue(PennantAppUtil.formateAmount(getRepayMain().getAccrued(), finformatter));

		//Total Overdue Penalty Amount
		BigDecimal pendingODC = getOverdueChargeRecoveryService().getPendingODCAmount(aFinanceMain.getFinReference());
		repayData.setPendingODC(pendingODC);
		this.pendingODC.setValue(PennantAppUtil.formateAmount(pendingODC, finformatter));

		//Fill Schedule data
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) || moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){

			this.row_EarlyRepayEffectOnSchd.setVisible(false);

			//Fetch Total Repayment Amount till Maturity date for Early Settlement
			BigDecimal repayAmt = getFinScheduleDetailService().getTotalRepayAmount(aFinanceMain.getFinReference());
			this.rpyAmount.setValue(PennantAppUtil.formateAmount(repayAmt, finformatter));
			this.row_EarlySettleDate.setVisible(true);
			
			if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){

				this.earlySettlementBal.setVisible(false);
				if(getRepaySchdList() == null || getRepaySchdList().isEmpty()){

					if(!isChgRpy || !StringUtils.trimToEmpty(this.repayAccount.getValue()).equals("")){

						Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, isChgRpy);
						this.btnCalcRepayments.setVisible(false);
						this.btnChangeRepay.setVisible(false);
					}else{
						PTMessageUtils.showErrorMessage("Repay Account ID must Exist.");
						return true;
					}
				}else{
					
					Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

					this.earlySettlementDate.setConstraint("");
					this.earlySettlementDate.setErrorMessage("");
					if(this.earlySettlementDate.getValue() == null){
						this.earlySettlementDate.setValue(curBussDate);
					}
					this.hbox_insRefundAmt.setVisible(true);
					this.label_PaymentDialog_InsRefundAmt.setVisible(true);
					
					doFillRepaySchedules(getRepaySchdList());
				}
			}

			if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
				Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, isChgRpy);

				this.label_PaymentDialog_RepayAccount.setValue(Labels.getLabel("label_PaymentDialog_EarlySettleAmount.value"));
				this.repayAccount.setVisible(false);
				this.label_PaymentDialog_EarlySettlementTillDate.setVisible(true);
				this.hbox_esTilllDate.setVisible(true);
				this.earlySettlementBal.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, finformatter));

				this.btnCalcRepayments.setVisible(true);
				this.btnChangeRepay.setVisible(false);
				this.btnPay.setVisible(false);
				this.actInsRefundAmt.setVisible(true);
				this.row_paidByCustomer.setVisible(true);
			}

		} else{
			if(!isChgRpy){
				if(getRepaySchdList() != null && !getRepaySchdList().isEmpty()){
					this.btnCalcRepayments.setDisabled(true);
				}
				doFillRepaySchedules(getRepaySchdList());
			}else{
				doFillRepaySchedules(repayData.getRepayScheduleDetails());
			}
		}
		
		logger.debug("Leaving");
		return false;
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendFeeDetailsTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (getFeeRuleList() != null && getFeeRuleList().isEmpty()) {

			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){
			
			Tab tab = new Tab("Fees");
			tab.setId("feeDetailTab");
			tabsIndexCenter.appendChild(tab);
			
			tabpanel = new Tabpanel();
			tabpanel.setId("feeDetailTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			tab.setVisible(false);
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("feeDetailTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("feeDetailTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if((getFeeRuleList() != null && !getFeeRuleList().isEmpty()) || 
				(getFeeChargesList() != null && !getFeeChargesList().isEmpty())){
			
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(finSchDetails);
			financeDetail.getFinScheduleData().setFinanceType(getFinanceType());
			financeDetail.setFeeCharges(getFeeChargesList());
			financeDetail.getFinScheduleData().setFeeRules(getFeeRuleList());
			setFinanceDetail(financeDetail);
			
			//Fee Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("eventCode", eventCode);
			if (isWorkFlowEnabled()) {
				map.put("isModify", !getUserWorkspace().isReadOnly("ManualPaymentDialog_feeCharge"));
			}else{
				map.put("isModify", true);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeeDetailDialog.zul", tabpanel, map);
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("feeDetailTab");
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	public void appendDocumentDetailTab(){
		logger.debug("Entering");

		Tab tab = new Tab("Documents");
		tab.setId("documentDetailsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("documentsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);

		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		financeDetail.getFinScheduleData().setFinanceType(getFinanceType());
		financeDetail.setDocumentDetailsList(getDocList());
		
		map.put("financeDetail", financeDetail);
		map.put("profitDaysBasisList", profitDaysBasisList);
		map.put("schMethodList", schMethodList);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul", tabpanel, map);

		setDocList(null);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	public void appendAgreementsDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (getAgreementList() != null && !getAgreementList().isEmpty()) {
			if(tabsIndexCenter.getFellowIfAny("agreementsTab") == null){
				createTab = true;
			}
		}
		
		Tabpanel tabpanel = null;
		if(createTab){
			
			Tab tab = new Tab(Labels.getLabel("Tab_Agreements"));
			tab.setId("agreementsTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreementDetailTab");
			
			tabpanel = new Tabpanel();
			tabpanel.setId("agreementsTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");

		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
	
		if (!onLoadProcess && getAgreementList() != null && !getAgreementList().isEmpty()) {

			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetail.getFinScheduleData().setFinanceType(getFinanceType());
			financeDetail.setAggrementList(getAgreementList());
			
			//Agreement Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", financeDetail);
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul", tabpanel, map);
			
		}
		logger.debug("Leaving");
	}
	
	public void onSelectAgreementDetailTab(ForwardEvent event) {
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT , (Tab)null,  "onSelectAgreementDetailTab");
		appendAgreementsDetailTab(false);
	}
	
	public void setAgreementDetailTab(Window window) {
		Tab tab = (Tab) window.getFellowIfAny("agreementsTab");
		if (tab != null) {
			if (!getFinanceDetail().getAggrementList().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreementDetailTab");
			} else {
				tab.setVisible(false);
			}
		}
	}
	
	/** To pass Data For Agreement Child Windows
	 * Used in reflection
	 * @return
	 * @throws Exception 
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail.getFinScheduleData().setFinanceMain(financeMain);
		aFinanceDetail.getFinScheduleData().setFinanceType(getFinanceType());

		String assetCode = aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescAssetCodeName();
		if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {
			aFinanceDetail.setCarLoanDetail(getCarLoanDetailService().getCarLoanDetailById(financeMain.getFinReference()));
		}
		
		logger.debug("Leaving");
		return aFinanceDetail;
	}
	
	/**
	 * Forward Event for onLoad for Accounting Details
	 * @param event
	 */
	public void onSelectAccountingDetailTab(ForwardEvent event) {
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT ,  (Tab)null,  "onSelectAccountingDetailTab");
		appendAccountingDetailTab(false);
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			closeTab();
		} catch (final WrongValuesException e) {
			logger.debug(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calculation of Schedule Repayment details List of data
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException 
	 * @throws WrongValueException 
	 */
	public void onClick$btnCalcRepayments(Event event) throws InterruptedException, WrongValueException, AccountNotFoundException {
		logger.debug("Entering" + event.toString());
		
		boolean isChgRpy = false;
		if(event.getData() != null){
			isChgRpy = (Boolean) event.getData();
		}

		if(isValid(isChgRpy)) {			
			
			this.totRefundAmt.setDisabled(isReadOnly("ManualPaymentDialog_refundPft"));

			RepayData repayData = null;
			if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
				Cloner cloner = new Cloner();
				List<FinanceScheduleDetail> finschDetailList = cloner.deepClone(this.finSchDetails);
				Date valueDate = this.earlySettlementDate.getValue();

				if(this.earlySettlementTillDate.getSelectedItem() != null && 
						!this.earlySettlementTillDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
					finschDetailList = rePrepareScheduleTerms(finschDetailList);
				}

				repayData = calculateRepayments(this.financeMain, finschDetailList , false, null, valueDate);

			}else{
				Date valueDate = null;
				if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
					valueDate = this.earlySettlementDate.getValue();
				}
				repayData = calculateRepayments(this.financeMain, this.finSchDetails , false, null, valueDate);
			}

			if(repayData.getRepayMain().isEarlyPay() && moduleDefiner.equals(PennantConstants.SCH_REPAY)){

				// Show a confirm box
				final String msg = "Do you want to Remodify Effective Schedule Method"+
						"\n *" +this.earlyRpyEffectOnSchd.getSelectedItem().getLabel() +"*" +
						" defined in Finance Types ? ";
				final String title = Labels.getLabel("message.Deleting.Record");
				MultiLineMessageBox.doSetTemplate();

				int conf = (MultiLineMessageBox.show(msg, title,
						MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

				if (conf == MultiLineMessageBox.YES) {
					logger.debug("Modify Effective Schedule Method: Yes");

					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("manualPaymentDialogCtrl", this);
					map.put("repayData", repayData);
					Executions.createComponents(
							"/WEB-INF/pages/FinanceManagement/Payments/EarlypayEffectOnSchedule.zul", 
							this.window_ManualPaymentDialog, map);

				}else{
					logger.debug("Modify Effective Schedule Method: No");
					setEarlyRepayEffectOnSchedule(repayData);
				}


			}else{
				setRepayDetailData(repayData);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Fill Unpaid Schedule Term for Selection of Paid Term 
	 * @param event
	 */
	public void onChange$earlySettlementDate(Event event){
		logger.debug("Entering" + event.toString());
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

		this.earlySettlementDate.setConstraint("");
		this.earlySettlementDate.setErrorMessage("");

		if(this.earlySettlementDate.getValue() == null){
			this.earlySettlementDate.setValue(curBussDate);
		}
		if(this.moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
			fillUnpaidSchDates();
		}else if(this.moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){

			//Check Early Settlement Date, EITHER Equal to Current Buss Date or Last Business Value Date
			Date lastBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_LAST_BUS_DATE");
			if(lastBussDate.compareTo(this.earlySettlementDate.getValue()) > 0 || curBussDate.compareTo(this.earlySettlementDate.getValue()) < 0){
				throw new WrongValueException(this.earlySettlementDate, Labels.getLabel("label_EarlySettlementDate", new String[]{
						lastBussDate.toString() , curBussDate.toString()}));
			}

			//Recalculation for Repayment Schedule Details
			Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, false);

			this.btnCalcRepayments.setVisible(false);
			this.btnChangeRepay.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$rpyAmount(Event event){
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

		if (this.finSchDetails != null) {
			for (FinanceScheduleDetail curSchd : this.finSchDetails) {

				if(curSchd.getSchDate().compareTo(this.earlySettlementDate.getValue()) > 0){
					break;
				}

				if((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0 || 
						(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0 || 
						(curSchd.getDefPrincipalSchd().subtract(curSchd.getDefSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0 || 
						(curSchd.getDefProfitSchd().subtract(curSchd.getDefSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0){

					if(curSchd.isRepayOnSchDate()){

						comboitem = new Comboitem();
						comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), 
								PennantConstants.dateFormate));
						comboitem.setValue(curSchd.getSchDate());
						earlySettlementTillDate.appendChild(comboitem);
					}
				}
			}
		}
	}
	
	/**
	 * Method for Allowing Total Refund Amount as manually also
	 * Revert Back to Old Amount if Entered Manual Amount is Greater than Calculation Refund Amount
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onChange$totRefundAmt(Event event) throws InterruptedException{
		logger.debug("Entering");
		
		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		RepayData aRepayData = cloner.deepClone(getRepayData());
		
		int finFormatter =  aRepayData.getRepayMain().getLovDescFinFormatter();
		BigDecimal manualRefundAmt = PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), finFormatter);
		
		aRepayData.setRepayScheduleDetails(getRepaySchdList());
		
		String sqlRule = getRuleService().getApprovedRuleById("REFUND", "REFUND", "").getSQLRule();
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

			//Check For Early Settlement Enquiry -- on Selecting Future Date TODO
			BigDecimal accrueValue = getFinanceDetailService().getAccrueAmount(financeMain.getFinReference());
			subHeadRule.setACCRUE(accrueValue);

			//Total Tenure
			int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(),false);
			subHeadRule.setTenure(months);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		aRepayData = RepayCalculator.calculateRefunds(aRepayData, manualRefundAmt, true, sqlRule, subHeadRule);
		if(!aRepayData.isSufficientRefund()){
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_ManualRefundExceed", new String[]{
					PennantApplicationUtil.amountFormate(aRepayData.getMaxRefundAmt(), finFormatter)}));
			
			this.totRefundAmt.setValue(PennantApplicationUtil.formateAmount(this.oldVar_totRefundAmt,  finFormatter));
			return;
		}
		
		//Total Outstanding Paid Amount By customer
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ) || moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			this.paidByCustomer.setValue(this.rpyAmount.getValue().subtract(this.totRefundAmt.getValue()).subtract(this.insRefundAmt.getValue()));
		}
		
		//Re-Rendering Repay Schedule Terms
		doFillRepaySchedules(aRepayData.getRepayScheduleDetails());
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Re=Prepare Schedule Term Data Based upon Till Paid Schedule Term
	 * @param scheduleDetails
	 * @return
	 */
	private List<FinanceScheduleDetail> rePrepareScheduleTerms(List<FinanceScheduleDetail> scheduleDetails){
		logger.debug("Entering");

		Date paidTillTerm = (Date)this.earlySettlementTillDate.getSelectedItem().getValue();

		for (FinanceScheduleDetail curSchd : scheduleDetails) {

			if(curSchd.getSchDate().compareTo(paidTillTerm) > 0){
				break;
			}
			
			curSchd.setSchdPriPaid(curSchd.getPrincipalSchd());
			curSchd.setSchdPftPaid(curSchd.getProfitSchd());
			curSchd.setDefSchdPriPaid(curSchd.getDefPrincipalSchd());
			curSchd.setDefSchdPftPaid(curSchd.getDefProfitSchd());

			curSchd.setSchPftPaid(true);
			curSchd.setSchPriPaid(true);
			curSchd.setDefSchPriPaid(true);
			curSchd.setDefSchPriPaid(true);

		}

		logger.debug("Leaving");
		return scheduleDetails;
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method 
	 * @param repayData
	 * @throws InterruptedException
	 */
	public void setEarlyRepayEffectOnSchedule(RepayData repayData) throws InterruptedException{
		logger.debug("Entering");

		//Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = getFinanceDetailService().getApprovedFinanceDetailById(financeMain.getFinReference(),false);
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		setFinanceDetail(null);

		String method = null;
		// Schedule remodifications only when Effective Schedule Method modified
		if(!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || 
				this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(CalculationConstants.EARLYPAY_NOEFCT))){

			method = this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString();
			if(repayData.getRepayMain().getEarlyRepayNewSchd() != null){
				if(CalculationConstants.EARLYPAY_RECPFI.equals(method)){
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setPftOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayAmount(BigDecimal.ZERO);
				}
				finScheduleData.getFinanceScheduleDetails().add(repayData.getRepayMain().getEarlyRepayNewSchd());
			}

			for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
				if(detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) == 0){
					if(CalculationConstants.EARLYPAY_RECPFI.equals(method)){
						detail.setEarlyPaid(detail.getEarlyPaid().add(repayData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					}else{
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						repayData.getRepayMain().setEarlyPayAmount(repayData.getRepayMain().getEarlyPayAmount()
								.add(earlypaidBal));
					}
				}
				if(detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) >= 0){
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

			//Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, repayData.getRepayMain().getEarlyPayOnSchDate(), 
					repayData.getRepayMain().getEarlyPayNextSchDate(), repayData.getRepayMain().getEarlyPayAmount(), method);

			financeDetail.setFinScheduleData(finScheduleData);
			setFinanceDetail(financeDetail);//Object Setting for Future save purpose			
			aFinanceMain = finScheduleData.getFinanceMain();
			aFinanceMain.setWorkflowId(financeMain.getWorkflowId());

			this.finSchType.setValue(aFinanceMain.getFinType());
			this.finSchCcy.setValue(aFinanceMain.getFinCcy() + "-"+ aFinanceMain.getLovDescFinCcyName());
			this.finSchMethod.setValue(aFinanceMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(), profitDayList));
			this.finSchReference.setValue(aFinanceMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtility.formateDate(aFinanceMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
			this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn().toString()+"%");

			//Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);
			this.effectiveScheduleTab.setVisible(true);

			//Dashboard Details Report
			doLoadTabsData();
			doShowReportChart(finScheduleData);
		}

		//Repayments Calculation
		repayData = calculateRepayments(finScheduleData.getFinanceMain(), finScheduleData.getFinanceScheduleDetails(), true, method,null);
		setRepayData(repayData);
		setRepayDetailData(repayData);		

		logger.debug("Leaving");
	}

	public List<FinanceScheduleDetail> sortSchdDetails(
			List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					if (detail1.getSchDate().after(detail2.getSchDate())) {
						return 1;
					}
					return 0;
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * @param aFinScheduleData (FinScheduleData) 
	 *  
	 */
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		FinanceScheduleDetail prvSchDetail =null;

		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if(aFinScheduleData != null && sdSize > 0) {

			// Find Out Fee charge Details on Schedule
			Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
			if(aFinScheduleData.getFeeRules() != null && aFinScheduleData.getFeeRules().size() > 0){
				feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();

				for (FeeRule fee : aFinScheduleData.getFeeRules()) {
					if(feeChargesMap.containsKey(fee.getSchDate())){
						ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}else{
						ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
			if(aFinScheduleData.getRepayDetails() != null && aFinScheduleData.getRepayDetails().size() > 0){
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : aFinScheduleData.getRepayDetails()) {
					if(rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())){
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}else{
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
			if(aFinScheduleData.getPenaltyDetails() != null && aFinScheduleData.getPenaltyDetails().size() > 0){
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				for (OverdueChargeRecovery penaltyDetail : aFinScheduleData.getPenaltyDetails()) {
					if(penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())){
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap.get(penaltyDetail.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}else{
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}

			//Clear all the listitems in listbox
			this.listBoxSchedule.getItems().clear();

			for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
				boolean showRate = false;
				FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
				if(i==0){
					prvSchDetail =aScheduleDetail;
					showRate = true;
				}else {
					prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i-1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinScheduleData);
				if(aFinScheduleData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", aFinScheduleData.getDefermentMap().get(aScheduleDetail.getSchDate()));
				}else {
					map.put("defermentDetail", null);
				}

				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("penaltyDetailsMap", penaltyDetailsMap);
				map.put("window", this.window_ManualPaymentDialog);
				finRender.render(map, prvSchDetail, false, true, false, feeChargesMap, showRate);

				if(i == sdSize - 1){						
					finRender.render(map, prvSchDetail, true, true, false, feeChargesMap, showRate);					
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	private RepayData calculateRepayments(FinanceMain financeMain, List<FinanceScheduleDetail> finSchDetails, boolean isReCal, String method, Date valueDate){

		logger.debug("Entering");

		getRepayData().setBuildProcess("R");
		getRepayData().getRepayMain().setRepayAmountNow(PennantAppUtil.unFormateAmount(this.rpyAmount.getValue(),
				getRepayData().getRepayMain().getLovDescFinFormatter()));
		String sqlRule = getRuleService().getApprovedRuleById("REFUND", "REFUND", "").getSQLRule();
		Customer customer = getCustomerDetailsService().getCustomerForPostings(financeMain.getCustID());
		FinanceType financeType = getFinanceTypeService().getFinanceTypeByFinType(financeMain.getFinType());
		isSchdRecal = true;

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

			//Check For Early Settlement Enquiry -- on Selecting Future Date TODO
			BigDecimal accrueValue = getFinanceDetailService().getAccrueAmount(financeMain.getFinReference());
			subHeadRule.setACCRUE(accrueValue);

			//Total Tenure
			int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(),false);
			subHeadRule.setTenure(months);

			if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) || moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){

				FeeRule takafulFee = getFinanceDetailService().getTakafulFee(financeMain.getFinReference());
				if(takafulFee != null){
					subHeadRule.setCALFEE(takafulFee.getFeeAmount() == null ? BigDecimal.ZERO : takafulFee.getFeeAmount());
					subHeadRule.setWAVFEE(takafulFee.getWaiverAmount() == null ? BigDecimal.ZERO : takafulFee.getWaiverAmount());
					subHeadRule.setPAIDFEE(takafulFee.getPaidAmount() == null ? BigDecimal.ZERO : takafulFee.getPaidAmount());
					getRepayData().setActInsRefundAmt(subHeadRule.getCALFEE().subtract(subHeadRule.getWAVFEE()).subtract(subHeadRule.getPAIDFEE()));
				}
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		getRepayData().getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		getRepayData().getRepayMain().setProfitPayNow(BigDecimal.ZERO);
		getRepayData().setFinanceType(getFinanceType());
		repayData = RepayCalculator.initiateRepay(getRepayData(), financeMain, finSchDetails, sqlRule, subHeadRule, isReCal, method,valueDate);

		//Calculation for Insurance Refund
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) || moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
			this.hbox_insRefundAmt.setVisible(true);
			this.label_PaymentDialog_InsRefundAmt.setVisible(true);
			int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), 
					repayData.getRepayMain().getRefundCalStartDate() == null ? financeMain.getMaturityDate() : repayData.getRepayMain().getRefundCalStartDate(),true);
			subHeadRule.setRemTenure(months);

			String insRefundRule = getRuleService().getApprovedRuleById("INSREFND", "REFUND", "").getSQLRule();
			if(insRefundRule != null){
				BigDecimal refundResult = new BigDecimal(getRuleExecutionUtil().executeRule(insRefundRule,
						subHeadRule, SystemParameterDetails.getGlobaVariableList(),financeMain.getFinCcy()).toString());
				refundResult = refundResult == null ? BigDecimal.ZERO : refundResult.setScale(0, RoundingMode.DOWN);
				repayData.getRepayMain().setInsRefund(refundResult);
				this.insRefundAmt.setValue(PennantApplicationUtil.formateAmount(refundResult, repayData.getRepayMain().getLovDescFinFormatter()));
			}
		}

		setRepayData(repayData);

		logger.debug("Leaving");
		return repayData;
	}

	private void setRepayDetailData(RepayData repayData) throws InterruptedException{
		logger.debug("Entering");

		//Repay Schedule Data rebuild
		doFillRepaySchedules(repayData.getRepayScheduleDetails());			
		this.priPayment.setValue(PennantAppUtil.formateAmount(repayData.getRepayMain().getPrincipalPayNow(),
				repayData.getRepayMain().getLovDescFinFormatter()));
		this.pftPayment.setValue(PennantAppUtil.formateAmount(repayData.getRepayMain().getProfitPayNow(),
				repayData.getRepayMain().getLovDescFinFormatter()));

		this.btnPay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnPay"));
		this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
		this.rpyAmount.setDisabled(true);
		this.repayAccount.setReadonly(isReadOnly("ManualPaymentDialog_RepayAccount"));
		if(financeMain.isAlwIndRate() || financeMain.isGrcAlwIndRate()){
			PTMessageUtils.showErrorMessage(" Indicative Rate schedules not included ... ");
		}
		if(!moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
			this.btnCalcRepayments.setDisabled(true);
		}else{
			BigDecimal paidNow = getRepayMain().getPrincipalPayNow().add(getRepayMain().getProfitPayNow());
			BigDecimal settlementBal = PennantAppUtil.unFormateAmount(this.rpyAmount.getValue(), getRepayMain().getLovDescFinFormatter()).subtract(paidNow);
			this.earlySettlementBal.setValue(PennantAppUtil.formateAmount(settlementBal, getRepayMain().getLovDescFinFormatter()));
			this.actInsRefundAmt.setValue(PennantAppUtil.formateAmount(getRepayData().getActInsRefundAmt(), getRepayMain().getLovDescFinFormatter()));
		}
		
		//Total Outstanding Paid Amount By customer
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ) || moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			this.paidByCustomer.setValue(this.rpyAmount.getValue().subtract(this.totRefundAmt.getValue()).subtract(this.insRefundAmt.getValue()));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayments Amount 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnChangeRepay(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException{
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
		
		if(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null){
			tabpanelsBoxIndexCenter.removeChild(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"));
		}
		
		if(tabsIndexCenter.getFellowIfAny("dashboardTab") != null){
			tabsIndexCenter.removeChild(tabsIndexCenter.getFellowIfAny("dashboardTab"));
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for event of Changing Repayment Amount 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		boolean isChgRpy = true;
		
		if ( this.userAction.getSelectedItem() != null && (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Resubmit")  || 
				 this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Reject") ||
				 this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")) ){
			isChgRpy = false;
		}

		if(isValid(isChgRpy)) {
			if(!isLimitExceeded){
				this.btnChangeRepay.setDisabled(true);
				this.btnCalcRepayments.setDisabled(true);

				//If Schedule Re-modified Save into DB or else only add Repayments Details
				if(!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || 
						this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals("NOEFCT")) && 
						moduleDefiner.equals(PennantConstants.SCH_REPAY)){

					processRepayScheduleList(getFinanceDetail().getFinScheduleData().getFinanceMain(), 
							getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(),
							getFinanceDetail().getFinScheduleData().getRepayInstructions(),true);
				}else{

					if(isChgRpy){
						// Check Available Funding Account Balance
						iAccount = getAccountInterfaceService().fetchAccountAvailableBal(PennantApplicationUtil.unFormatAccountNumber(this.repayAccount.getValue())); 

						// Check Available Funding Account Balance
						boolean accountTypeFound = false;
						if(getFinanceType() != null){
							if(StringUtils.trimToEmpty(getFinanceType().getFinDivision()).equals(PennantConstants.FIN_DIVISION_TREASURY)){
								String acType = SystemParameterDetails.getSystemParameterValue("ALWFULLPAY_TSR_ACTYPE").toString();

								//Account Type Check
								String[] acTypeList = acType.split(",");
								for (int i = 0; i < acTypeList.length; i++) {
									if(iAccount.getAcType().equals(acTypeList[i].trim())){
										accountTypeFound = true;
										break;
									}
								}

							}else {

								//Account Type Check
								String acType = SystemParameterDetails.getSystemParameterValue("ALWFULLPAY_NONTSR_ACTYPE").toString();
								String[] acTypeList = acType.split(",");
								for (int i = 0; i < acTypeList.length; i++) {
									if(iAccount.getAcType().equals(acTypeList[i].trim())){
										accountTypeFound = true;
										break;
									}
								}
							}
						}

						if(!accountTypeFound){

							BigDecimal penalty = BigDecimal.ZERO;
							if(this.totPenaltyAmt.getValue() != null){
								penalty = this.totPenaltyAmt.getValue();
							}

							if(PennantAppUtil.unFormateAmount(this.rpyAmount.getValue().subtract(this.totRefundAmt.getValue()).
															subtract(insRefundAmt.getValue() == null ? BigDecimal.ZERO :insRefundAmt.getValue()).add(penalty),
									getRepayData().getRepayMain().getLovDescFinFormatter()).compareTo(iAccount.getAcAvailableBal()) > 0){
								PTMessageUtils.showErrorMessage(Labels.getLabel("label_InsufficientBalance"));
								this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_ManualPaymentDialog_btnChangeRepay"));
								return;
							}
						}
					}
					//financeMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(repayAccount.getValue()));
					processRepayScheduleList(financeMain, finSchDetails,null,false);
				}

			}else{
				PTMessageUtils.showErrorMessage(" Limit exceeded ... ");
				return;
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Process Repayment Details
	 * @param aFinanceMain
	 * @param finSchDetails
	 * @param repayInstructions
	 * @param schdlReModified
	 * @throws Exception 
	 */
	private void processRepayScheduleList(FinanceMain aFinanceMain, List<FinanceScheduleDetail> finSchDetails, 
			List<RepayInstruction> repayInstructions,boolean schdlReModified) throws Exception{
		logger.debug("Entering");

		RepayData data = new RepayData();
		data.setFinanceMain(aFinanceMain);
		data.setScheduleDetails(finSchDetails);
		data.setRepayInstructions(repayInstructions);
		data.setRepayScheduleDetails(getRepaySchdList());
		data.setFinanceType(getFinanceType());
		
		//Prepare Finance Repay Header Details
		data.setFinRepayHeader(doWriteComponentsToBean(schdlReModified));
		
		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		RepayData aRepayData = cloner.deepClone(data);

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aFinanceMain.setNewRecord(true);
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}
		
		if(getFeeDetailDialogCtrl() != null){
			Map<String, FeeRule> feeMap = getFeeDetailDialogCtrl().getFeeRuleDetailsMap();
			if(feeMap != null && feeMap.size() > 0){
				List<FeeRule> feeList = new ArrayList<FeeRule>(feeMap.values());
				aRepayData.setFeeRuleList(feeList);
			}
		}
		
		//Document Details Saving
		if(getDocumentDetailDialogCtrl() != null){
			aRepayData.setDocumentDetailList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		}else{
			aRepayData.setDocumentDetailList(null);
		}
		
		aRepayData.setEventCodeRef(eventCode);
		
		// save it to database
		try {
			aFinanceMain.setRcdMaintainSts(aRepayData.getFinRepayHeader().getFinEvent());
			aRepayData.setFinanceMain(aFinanceMain);
			if (doProcess(aRepayData, tranType)) {
				
				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				//Customer Notification for Role Identification
				if(StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("")){
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),aFinanceMain.getNextRoleCode(), 
						aFinanceMain.getFinReference(), " Finance ", aFinanceMain.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				//Mail Alert Notification for User
				if(!StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("") && 
						!StringUtils.trimToEmpty(aFinanceMain.getNextRoleCode()).equals(aFinanceMain.getRoleCode())){
					getMailUtil().sendMail("FIN",aRepayData,this);
				}

				closeDialog(this.window_ManualPaymentDialog, "ManualPaymentDialog");
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_ManualPaymentDialog, e);
		}
		logger.debug("Leaving");
	}
	
	private void doWriteBeanToComponents() throws InterruptedException{
		logger.debug("Entering");
		
		int finFormatter  = financeMain.getLovDescFinFormatter();
		FinRepayHeader header = getFinRepayHeader();
		if(header != null){
			
			this.rpyAmount.setValue(PennantApplicationUtil.formateAmount(header.getRepayAmount(), finFormatter));
			this.priPayment.setValue(PennantApplicationUtil.formateAmount(header.getPriAmount(), finFormatter));
			this.pftPayment.setValue(PennantApplicationUtil.formateAmount(header.getPftAmount(), finFormatter));
			this.totWaivedAmt.setValue(PennantApplicationUtil.formateAmount(header.getTotalWaiver(), finFormatter));
			this.totRefundAmt.setValue(PennantApplicationUtil.formateAmount(header.getTotalRefund(), finFormatter));
			this.repayAccount.setValue(header.getRepayAccountId());
			this.insRefundAmt.setValue(PennantApplicationUtil.formateAmount(header.getInsRefund(), finFormatter));
			fillComboBox(earlyRpyEffectOnSchd, header.getEarlyPayEffMtd(), earlyRpyEffectList, "");
			this.earlySettlementDate.setValue(header.getEarlyPayDate());
		}
		
		if(!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || 
				this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(CalculationConstants.EARLYPAY_NOEFCT)) 
				 && moduleDefiner.equals(PennantConstants.SCH_REPAY)){
			
			FinanceDetail financeDetail = getFinanceDetailService().getApprovedFinanceDetailById(financeMain.getFinReference(),false);
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			finScheduleData.setFinanceMain(financeMain);
			finScheduleData.setFinanceScheduleDetails(finSchDetails);
			setFinanceDetail(financeDetail);

			//Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);
			this.effectiveScheduleTab.setVisible(true);

			//Dashboard Details Report
			doLoadTabsData();
			doShowReportChart(finScheduleData);
			
		}
		
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){
			//Fee Details Tab Addition
			appendFeeDetailsTab(true);
			
			//Document Details
			appendDocumentDetailTab();
			
			//Agreement Details
			appendAgreementsDetailTab(true);
		}
		
		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if(!moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ) && getWorkFlow().getTaskTabs(getWorkFlow().getTaskId(getRole())).equals("Accounting")){
			//Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}
		
		this.recordStatus.setValue(financeMain.getRecordStatus());
		logger.debug("Leaving");
	}
	
	private FinRepayHeader doWriteComponentsToBean(boolean isSchdRegenerated){
		logger.debug("Entering");
		
		int finFormatter  = financeMain.getLovDescFinFormatter();
		
		FinRepayHeader header = getFinRepayHeader();
		if(header == null || (isSchdRecal && moduleDefiner.equals(PennantConstants.SCH_REPAY))){
			header = new FinRepayHeader();
			header.setSchdRegenerated(isSchdRegenerated);
		}
		
		header.setFinReference(this.finReference.getValue());
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		header.setValueDate(curBDay);
		header.setFinEvent(moduleDefiner);
		header.setRepayAmount(PennantApplicationUtil.unFormateAmount(this.rpyAmount.getValue(), finFormatter));
		header.setPriAmount(PennantApplicationUtil.unFormateAmount(this.priPayment.getValue(), finFormatter));
		header.setPftAmount(PennantApplicationUtil.unFormateAmount(this.pftPayment.getValue(), finFormatter));
		header.setTotalRefund(PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), finFormatter));
		header.setTotalWaiver(PennantApplicationUtil.unFormateAmount(this.totWaivedAmt.getValue(), finFormatter));
		header.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAccount.getValue()));
		header.setInsRefund(PennantApplicationUtil.unFormateAmount(this.insRefundAmt.getValue(), finFormatter));
		header.setEarlyPayEffMtd(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString());
		header.setEarlyPayDate(this.earlySettlementDate.getValue());
		header.setSchdRegenerated(isSchdRegenerated);
		logger.debug("Leaving");
		return header;
	}

	/**
	 * Method for Executing Eligibility Details
	 * @throws Exception 
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws Exception{
		logger.debug("Entering");

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		//Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Accounting tab Rules
	 * @throws Exception 
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception{
		logger.debug("Entering");

		FinanceMain actfinMain = getRepayData().getFinanceMain() == null ? financeMain : getRepayData().getFinanceMain();
		
		Cloner cloner = new Cloner();
		FinanceMain finMain = cloner.deepClone(actfinMain);
		
		List<FinanceScheduleDetail> schdlDetail = getRepayData().getScheduleDetails().isEmpty() ? finSchDetails : getRepayData().getScheduleDetails();
		FinanceProfitDetail profitDetail = getFinanceDetailService().getFinProfitDetailsById(financeMain.getFinReference());
		FinanceType financeType = getFinanceTypeService().getFinanceTypeById(finMain.getFinType());
		Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		
		finMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(repayAccount.getValue()));
		DataSet dataSet = AEAmounts.createDataSet(finMain, eventCode, dateValueDate, dateValueDate);

		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		amountCodes = AEAmounts.procAEAmounts(finMain,schdlDetail, profitDetail , curBDay);

		//Set Repay Amount Codes
		amountCodes.setRpTot(PennantApplicationUtil.unFormateAmount(this.rpyAmount.getValue(), finMain.getLovDescFinFormatter()));
		amountCodes.setRpPft(PennantApplicationUtil.unFormateAmount(this.pftPayment.getValue(), finMain.getLovDescFinFormatter()));
		amountCodes.setRpPri(PennantApplicationUtil.unFormateAmount(this.priPayment.getValue(), finMain.getLovDescFinFormatter()));
		amountCodes.setRefund(PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), finMain.getLovDescFinFormatter()));
		amountCodes.setInsRefund(PennantApplicationUtil.unFormateAmount(this.insRefundAmt.getValue(), finMain.getLovDescFinFormatter()));
		setAmountCodes(amountCodes);

		List<ReturnDataSet> returnSetEntries = null;
		if(!financeType.isAllowRIAInvestment()){
			
			Map<String, FeeRule> feeRuleMap = null;
			if(getFeeDetailDialogCtrl() != null){
				feeRuleMap = getFeeDetailDialogCtrl().getFeeRuleDetailsMap();
			}
			
			returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, getAmountCodes(), "N", feeRuleMap, false, financeType);
		}else{

			List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(null, dataSet.getFinReference());
			returnSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataSet, getAmountCodes(), "N", riaDetailList);
		}

		if(getAccountingDetailDialogCtrl() != null){
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);

			if(!StringUtils.trimToEmpty(finMain.getFinCommitmentRef()).equals("")){

				Commitment commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());

				if(commitment != null && commitment.isRevolving()){
					AECommitment aeCommitment = new AECommitment();
					aeCommitment.setCMTAMT(BigDecimal.ZERO);
					aeCommitment.setCHGAMT(BigDecimal.ZERO);
					aeCommitment.setDISBURSE(BigDecimal.ZERO);
					aeCommitment.setRPPRI(CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(),
							amountCodes.getRpPri()));

					List<ReturnDataSet> cmtEntries = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTRPY", "N", null);
					getAccountingDetailDialogCtrl().doFillCmtAccounting(cmtEntries, commitment.getCcyEditField());
					getAccountingDetailDialogCtrl().getFinanceDetail().getReturnDataSetList().addAll(cmtEntries);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if(tabsIndexCenter.getFellowIfAny("accountingTab") == null){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){

			Tab tab = new Tab("Accounting");
			tab.setId("accountingTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAccountingDetailTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("accountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
				tabpanel.setVisible(true);
			}
		}
		
		if (!onLoadProcess) {
			
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(getRepayData().getFinanceMain() == null ? financeMain : getRepayData().getFinanceMain());
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(getRepayData().getScheduleDetails().isEmpty()? finSchDetails : getRepayData().getScheduleDetails());	
			financeDetail.getFinScheduleData().setFinanceType(getFinanceType());	
			
			//Get Finance Type Details, Transaction Entry By event & Commitment Repay Entries If have any
			financeDetail = getManualPaymentService().getAccountingDetail(financeDetail, eventCode);

			//Accounting Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", financeDetail);
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
				tab.setVisible(true);
			}
		}
		
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private String getServiceTasks(String taskId, FinanceMain financeMain,
			String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getWorkFlow().getOperationRefs(taskId,
				financeMain);

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
			nextTaskId = getWorkFlow().getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getWorkFlow().firstTask.owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + ",";
					}
					nextRoleCode += getWorkFlow().getTaskOwner(nextTasks[i]);
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
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException 
	 */
	private boolean doProcess(RepayData aRepayData, String tranType) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aRepayData.getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());

		afinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());
		aRepayData.setFinanceMain(afinanceMain);

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, afinanceMain))) {
				try {
					if (!isNotes_Entered()) {
						PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			auditHeader = getAuditHeader(aRepayData, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {

					/*FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					FinanceMain financeMain = tFinanceDetail.getFinScheduleData().getFinanceMain();

					List<Long> templateIDList = getManualPaymentService().getMailTemplatesByFinType(financeMain.getFinType(), financeMain.getRoleCode());
					for (Long templateId : templateIDList) {
						getMailUtil().sendMail(templateId, PennantConstants.TEMPLATE_FOR_CN, financeMain);
					}*/

				} else {
					RepayData tRepayData=  (RepayData) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tRepayData.getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tRepayData);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				RepayData tRepayData =  (RepayData) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tRepayData.getFinanceMain(),finishedTasks);

			}

			RepayData tRepayData =  (RepayData) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getWorkFlow().getNextTaskIds(taskId,tRepayData.getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tRepayData.getFinanceMain());
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
		FinanceMain afinanceMain = aRepayData.getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
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

		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getErrorMsg());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerRating listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug("Entering");

		setRepaySchdList(repaySchdList);
		refundMap = new LinkedHashMap<String, RepayScheduleDetail>();
		this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;
		Listcell lc;
		Listitem item;

		int finFormatter = getRepayMain().getLovDescFinFormatter();
		this.totPenaltyAmt.setValue(PennantAppUtil.formateAmount(totalCharge, finFormatter));

		if(repaySchdList != null){
			for(int i=0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtility.formatDate(repaySchd.getSchDate(), PennantConstants.dateFormate));
				lc.setStyle("font-weight:bold;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdBal(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdBal(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdPayNow(),finFormatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdPayNow(),finFormatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPenaltyAmt(),finFormatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyAmt());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if(repaySchd.getDaysLate() > 0){
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getMaxWaiver(),finFormatter));
				}else{
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRefundMax(),finFormatter));
				}
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				//Open If Allow On change of particular Refund Amount per every Schedule term Individually
				/*Decimalbox refundPft = new Decimalbox();
				refundPft.setWidth("99.9%");
				refundPft.setMaxlength(18);
				refundPft.setFormat(PennantAppUtil.getAmountFormate(finFormatter));
				refundPft.setStyle("border:0px");
				if(repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
					
					refundPft.setDisabled(true);//isReadOnly("ManualPaymentDialog_refundPft")
					refundPft.setInplace(false);//!isReadOnly("ManualPaymentDialog_refundPft")
					List<Object> list = new ArrayList<Object>(2);
					list.add(refundPft);
					list.add(repaySchd.getSchDate());

					if(repaySchd.isAllowRefund()){

						refundPft.setValue(PennantAppUtil.formateAmount((repaySchd.getRefundReq()).compareTo(BigDecimal.ZERO) == 0?
								BigDecimal.ZERO:repaySchd.getRefundReq(), finFormatter));
						totalRefund =  totalRefund.add(repaySchd.getRefundReq());

					}else if(repaySchd.isAllowWaiver()){

						refundPft.setValue(PennantAppUtil.formateAmount(repaySchd.getWaivedAmt(),finFormatter));
						totalWaived =  totalWaived.add(repaySchd.getWaivedAmt());

					}
					refundPft.addForward("onChange",window_ManualPaymentDialog,"onRefundValueChanged", list);
				}else {
					refundPft.setDisabled(true);
					refundPft.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,finFormatter));
					refundPft.setStyle("text-align:right;background:none;border:0px;font-color:#AAAAAA;");
				}*/
				
				BigDecimal refundPft = BigDecimal.ZERO;
				if(repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
					if(repaySchd.isAllowRefund()){
						refundPft = repaySchd.getRefundReq();
						totalRefund =  totalRefund.add(refundPft);
					}else if(repaySchd.isAllowWaiver()){
						refundPft = repaySchd.getWaivedAmt();
						totalWaived =  totalWaived.add(refundPft);
					}
				}
				
				lc = new Listcell(PennantAppUtil.amountFormate(refundPft,finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPrincipalSchdPayNow()).subtract(refundPft);
				lc = new Listcell(PennantAppUtil.amountFormate(netPay,finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRepayBalance(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				this.listBoxPayment.appendChild(item);
				if(refundMap.containsKey(repaySchd.getSchDate().toString())) {
					refundMap.remove(repaySchd.getSchDate().toString());
				}
				refundMap.put(repaySchd.getSchDate().toString(),repaySchd);
			}
			this.totRefundAmt.setValue(PennantAppUtil.formateAmount(totalRefund, finFormatter));
			this.oldVar_totRefundAmt = totalRefund;
			this.totWaivedAmt.setValue(PennantAppUtil.formateAmount(totalWaived, finFormatter));
			this.totPenaltyAmt.setValue(PennantAppUtil.formateAmount(totalCharge, finFormatter));

			//Summary Details
			if(!moduleDefiner.equals(PennantConstants.SCH_REPAY) ){
				doFillSummaryDetails(totalRefund, totalCharge, totalPft, totalPri);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(BigDecimal totalrefund, BigDecimal totalPenalty,  BigDecimal totalPft, BigDecimal totalPri){

		Listcell lc;
		Listitem item;

		//Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_summary.label"));
		lc.setStyle("font-weight:bold;");
		lc.setSpan(10);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

		fillListItem(Labels.getLabel("listcell_totalRefund.label"), totalrefund);
		fillListItem(Labels.getLabel("listcell_totalPenalty.label"), totalPenalty);
		fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), totalPft);
		fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), totalPri);
		fillListItem(Labels.getLabel("listcell_totalSchAmount.label"), totalPft.add(totalPri));

	}

	/**
	 * Method for Showing List Item 
	 * @param label
	 * @param fieldValue
	 */
	private void fillListItem(String label, BigDecimal fieldValue){

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
		lc.setSpan(6);
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
		int finFormatter = getRepayData().getRepayMain().getLovDescFinFormatter();
		if(refundMap.containsKey(schDate)){
			RepayScheduleDetail repaySchd = refundMap.get(schDate);

			if(repaySchd.isAllowRefund()){
				if(repaySchd.getRefundMax().compareTo(PennantAppUtil.unFormateAmount(refundProfit.getValue(),finFormatter))<0) {
					PTMessageUtils.showErrorMessage(" Limit exceeded ... ");
					isLimitExceeded = true;
					return;
				}
				repaySchd.setRefundReq(PennantAppUtil.unFormateAmount(refundProfit.getValue(),finFormatter));
			}else if(repaySchd.isAllowWaiver()){
				if(repaySchd.getMaxWaiver().compareTo(PennantAppUtil.unFormateAmount(refundProfit.getValue(),finFormatter))<0) {
					PTMessageUtils.showErrorMessage(" Limit exceeded ... ");
					isLimitExceeded = true;
					return;
				}
				repaySchd.setWaivedAmt(PennantAppUtil.unFormateAmount(refundProfit.getValue(),finFormatter));
			}
			refundMap.remove(schDate);
			refundMap.put(schDate, repaySchd);
		}

		doFillRepaySchedules(sortRpySchdDetails(new ArrayList<RepayScheduleDetail>(refundMap.values())));
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Sorting Repay Schedule Details
	 * @param repayScheduleDetails
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {

		if (repayScheduleDetails != null && repayScheduleDetails.size() > 0) {
			Collections.sort(repayScheduleDetails, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail detail1, RepayScheduleDetail detail2) {
					if (detail1.getSchDate().after(detail2.getSchDate())) {
						return 1;
					}
					return 0;
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
	private boolean isValid(boolean isChgRpy) throws InterruptedException, AccountNotFoundException {
		logger.debug("Entering");

		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		if(financeMain != null && curBussDate.compareTo(financeMain.getFinStartDate()) == 0) {
			PTMessageUtils.showErrorMessage(" Disbursement Date is Same as Current Business Date. Not Allowed for Repayment. ");
			return false;
		}

		//Check Early Settlement Date, EITHER Equal to Current Buss Date or Last Business Value Date
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){

			this.earlySettlementDate.setConstraint("");
			this.earlySettlementDate.setErrorMessage("");

			Date lastBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_LAST_BUS_DATE");
			if(this.earlySettlementDate.getValue() == null){
				this.earlySettlementDate.setValue(curBussDate);
			}
			if(lastBussDate.compareTo(this.earlySettlementDate.getValue()) > 0 || curBussDate.compareTo(this.earlySettlementDate.getValue()) < 0){
				throw new WrongValueException(this.earlySettlementDate, Labels.getLabel("label_EarlySettlementDate", new String[]{
						lastBussDate.toString() , curBussDate.toString()}));
			}
		}

		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAYENQ)){
			if(this.earlySettlementDate.getValue() == null){
				this.earlySettlementDate.setValue(curBussDate);
			}
			logger.debug("Leaving");
			return true;
		}

		if(isChgRpy && StringUtils.trimToEmpty(this.repayAccount.getValue()).equals("")){
			PTMessageUtils.showErrorMessage(" Please Enter Repayment Account ID ... ");
			return false;
		} else if(this.rpyAmount.getValue() == null || this.rpyAmount.getValue().compareTo(BigDecimal.ZERO) == 0){
			PTMessageUtils.showErrorMessage(" Please Enter Repayment Amount ... ");
			return false;
		} else if(!this.rpyAmount.isDisabled() && this.rpyAmount.getValue().compareTo(BigDecimal.ZERO) > 0){

			if(!moduleDefiner.equals(PennantConstants.SCH_EARLYPAY) && isChgRpy){
				
				if(!StringUtils.trimToEmpty(this.repayAccount.getValue()).equals("")){
					iAccount = getAccountInterfaceService().fetchAccountAvailableBal(PennantApplicationUtil.unFormatAccountNumber(this.repayAccount.getValue())); 

					// Check Available Funding Account Balance
					boolean accountTypeFound = false;
					if(getFinanceType() != null){
						if(StringUtils.trimToEmpty(getFinanceType().getFinDivision()).equals(PennantConstants.FIN_DIVISION_TREASURY)){
							String acType = SystemParameterDetails.getSystemParameterValue("ALWFULLPAY_TSR_ACTYPE").toString();

							//Account Type Check
							String[] acTypeList = acType.split(",");
							for (int i = 0; i < acTypeList.length; i++) {
								if(iAccount.getAcType().equals(acTypeList[i].trim())){
									accountTypeFound = true;
									break;
								}
							}

						}else {

							//Account Type Check
							String acType = SystemParameterDetails.getSystemParameterValue("ALWFULLPAY_NONTSR_ACTYPE").toString();
							String[] acTypeList = acType.split(",");
							for (int i = 0; i < acTypeList.length; i++) {
								if(iAccount.getAcType().equals(acTypeList[i].trim())){
									accountTypeFound = true;
									break;
								}
							}
						}
					}

					if(!accountTypeFound){
						
						BigDecimal penalty = BigDecimal.ZERO;
						if(this.totPenaltyAmt.getValue() != null){
							penalty = this.totPenaltyAmt.getValue();
						}

						if(PennantAppUtil.unFormateAmount(this.rpyAmount.getValue().subtract(this.totRefundAmt.getValue()).
								subtract(insRefundAmt.getValue() == null ? BigDecimal.ZERO :insRefundAmt.getValue()).add(penalty),
								getRepayData().getRepayMain().getLovDescFinFormatter()).compareTo(iAccount.getAcAvailableBal()) > 0){
							PTMessageUtils.showErrorMessage(Labels.getLabel("label_InsufficientBalance"));
							return false;
						}
					}
				}
			}

		} 
		if(getRepayMain().getRepayAmountExcess().compareTo(BigDecimal.ZERO) > 0) {
			PTMessageUtils.showErrorMessage(" Entered amount is more than required ...");
			return false;
		} 
		logger.debug("Leaving");
		return true;
	}

	/**
	 * To Close the tab when fin reference search dialog is closed <br>
	 * IN ManualPaymentDialogCtrl.java void
	 */
	public void closeTab() {
		if(financeMain == null){
			this.window_ManualPaymentDialog.onClose();
		}else{
			closeDialog(this.window_ManualPaymentDialog, "ManualPayment");
		}
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	private void doLoadTabsData() throws InterruptedException {
		logger.debug("Entering ");
		
		boolean createTab = false;
		if(tabsIndexCenter.getFellowIfAny("dashboardTab") == null){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){

			Tab tab = new Tab("Dashboard");
			tab.setId("dashboardTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("graphTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null){
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

		int formatter = finScheduleData.getFinanceMain().getLovDescFinFormatter();
		DashboardConfiguration aDashboardConfiguration=new DashboardConfiguration();
		ChartDetail chartDetail=new ChartDetail();
		ChartUtil chartUtil=new ChartUtil();

		//For Finance Vs Amounts Chart z
		List<ChartSetElement> listChartSetElement=getReportDataForFinVsAmount(finScheduleData, formatter);

		ChartsConfig  chartsConfig=new ChartsConfig("Finance Vs Amounts","FinanceAmount ="
				+PennantAppUtil.amountFormate(PennantAppUtil.unFormateAmount(financeAmount , formatter),formatter),"","");
		aDashboardConfiguration=new DashboardConfiguration();
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(listChartSetElement);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("pieRadius='90' startingAngle='310'" +
				"formatNumberScale='0'enableRotation='1'  forceDecimals='1'  decimals='"+formatter+"'");
		String chartStrXML=aDashboardConfiguration.getLovDescChartsConfig().getChartXML();
		chartDetail=new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("Pie3D.swf");
		chartDetail.setChartHeight("160");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");

		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));

		//For Repayments Chart 
		chartsConfig=new ChartsConfig("Repayments","","","");
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(getReportDataForRepayments(finScheduleData, formatter));
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("labelDisplay='ROTATE' formatNumberScale='0'" +
				"rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'  decimals='"+formatter+"'");
		chartStrXML=aDashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail=new ChartDetail();
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
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForRepayments(FinScheduleData scheduleData, int formatter) {
		logger.debug("Entering ");

		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = scheduleData.getFinanceScheduleDetails();
		ChartSetElement chartSetElement;
		if(listScheduleDetail!=null){
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"RepayAmount",
							PennantAppUtil.formateAmount(listScheduleDetail.get(i).getRepayAmount(), 
									scheduleData.getFinanceMain().getLovDescFinFormatter())
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"PrincipalSchd",PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(), 
									scheduleData.getFinanceMain().getLovDescFinFormatter())
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"ProfitSchd",PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd()
									,scheduleData.getFinanceMain().getLovDescFinFormatter())
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
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForFinVsAmount(FinScheduleData scheduleData, int formatter){
		logger.debug("Entering ");
		
		BigDecimal downPayment= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);;
		BigDecimal scheduleProfit= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);; 
		BigDecimal schedulePrincipal= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		
		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail=scheduleData.getFinanceScheduleDetails();
		int finFormatter = scheduleData.getFinanceMain().getLovDescFinFormatter();
		
		if(listScheduleDetail!=null){
			ChartSetElement chartSetElement;
			financeAmount = BigDecimal.ZERO;
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				financeAmount=financeAmount.add(PennantAppUtil.formateAmount(curSchd.getDisbAmount(), finFormatter));
				downPayment=downPayment.add(PennantAppUtil.formateAmount(curSchd.getDownPaymentAmount(), finFormatter));
				capitalized=capitalized.add(PennantAppUtil.formateAmount(curSchd.getCpzAmount(), finFormatter));

				scheduleProfit=scheduleProfit.add(PennantAppUtil.formateAmount(curSchd.getProfitSchd(), finFormatter));
				schedulePrincipal=schedulePrincipal.add(PennantAppUtil.formateAmount(curSchd.getPrincipalSchd(), finFormatter));

			}
			chartSetElement=new ChartSetElement("DownPayment",downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("Capitalized",capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("ScheduleProfit",scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("SchedulePrincipal",schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(RepayData repayData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,null , repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, 
				auditDetail, repayData.getFinanceMain().getUserDetails(), getOverideMap());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		this.btnNotes.setSclass("");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(moduleDefiner);
		notes.setReference(financeMain.getFinReference());
		notes.setVersion(financeMain.getVersion());
		logger.debug("Leaving ");
		return notes;
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj();
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}
	
	/**
	 * When the  print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		String reportName = "InternalMemorandum";
		EarlySettlementReportData earlySettlement = new EarlySettlementReportData();
		boolean isRetail = false;
		if(getFinanceType() != null){
			String division = getFinanceType().getFinDivision().trim();
			if(division.equalsIgnoreCase(PennantConstants.FIN_DIVISION_RETAIL)){
				reportName = PennantConstants.FIN_DIVISION_RETAIL+"_InternalMemorandum.docx";
				isRetail = true;
			}
			earlySettlement.setDeptFrom(getFinanceType().getLovDescFinDivisionName());
		}

		if (this.financeMain != null) {
			earlySettlement.setAppDate(DateUtility.formatUtilDate((Date)SystemParameterDetails.getSystemParameterValue("APP_DATE"), PennantConstants.dateFormat));
			earlySettlement.setFinReference(financeMain.getFinReference());
			earlySettlement.setFinType(financeMain.getFinType());
			earlySettlement.setFinTypeDesc(financeMain.getLovDescFinTypeName());
			earlySettlement.setCustCIF("CIF "+financeMain.getLovDescCustCIF());
			earlySettlement.setCustShrtName(financeMain.getLovDescCustShrtName());
			earlySettlement.setFinStartDate(DateUtility.formatUtilDate(financeMain.getFinStartDate(), PennantConstants.dateFormate));
			earlySettlement.setEarlySettlementDate(DateUtility.formatUtilDate(this.earlySettlementDate.getValue(), PennantConstants.dateFormate));
		}
		
		int formatter = financeMain.getLovDescFinFormatter();
		
		FinanceProfitDetail profitDetail = getManualPaymentService().getPftDetailForEarlyStlReport(financeMain.getFinReference());
		if(profitDetail != null){
			
			BigDecimal financeAmount = financeMain.getFinAmount().add(financeMain.getFeeChargeAmt() != null ? financeMain.getFeeChargeAmt() : BigDecimal.ZERO).subtract(financeMain.getDownPayment());
			earlySettlement.setTotalPaidAmount(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(financeAmount, formatter));
			earlySettlement.setTotalTerms(String.valueOf(profitDetail.getNOInst()));
			earlySettlement.setTotalPaidTerms(String.valueOf(profitDetail.getNOPaidInst()));
			earlySettlement.setTotalUnpaidTerms(String.valueOf(profitDetail.getNOInst()-profitDetail.getNOPaidInst()));
			earlySettlement.setOutStandingTotal(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(profitDetail.getTotalPriBal().add(profitDetail.getTotalPftBal()), formatter));
			earlySettlement.setOutStandingPft(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(profitDetail.getTotalPftBal(), formatter));
			
			BigDecimal totalRefund = PennantApplicationUtil.unFormateAmount(this.totRefundAmt.getValue(), formatter);
			BigDecimal discountPerc = BigDecimal.ZERO;
			if(profitDetail.getTotalPftBal().compareTo(BigDecimal.ZERO) != 0){
				discountPerc = (totalRefund.divide(profitDetail.getTotalPftBal(), 2, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
			}
			earlySettlement.setDiscountPerc(discountPerc +" %");
			earlySettlement.setDiscountAmount(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(totalRefund, formatter));
			
			BigDecimal insAmount = BigDecimal.ZERO;
			FeeRule feeRule = getFinanceDetailService().getTakafulFee(financeMain.getFinReference());
			if(feeRule != null && feeRule.getFeeAmount() != null){
				insAmount = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());
			}
			earlySettlement.setInsuranceFee(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(insAmount, formatter));
			
			int remMonths = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), 
					repayData.getRepayMain().getRefundCalStartDate() == null ? financeMain.getMaturityDate() : repayData.getRepayMain().getRefundCalStartDate(),true);
			int totalMonths = DateUtility.getMonthsBetween(financeMain.getMaturityDate(),financeMain.getFinStartDate() ,false);
			
			earlySettlement.setPeriodCoverage(String.valueOf(totalMonths - remMonths));
			BigDecimal insRefnd = PennantApplicationUtil.unFormateAmount(this.insRefundAmt.getValue(),formatter) ;
			earlySettlement.setPeriodCoverageAmt(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(insAmount.subtract(insRefnd), formatter));
			earlySettlement.setReturnInsAmount(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(insRefnd, formatter));
			earlySettlement.setTotCustPaidAmount(financeMain.getFinCcy() +" "+PennantApplicationUtil.amountFormate(profitDetail.getTotalPriBal().add(profitDetail.getTotalPftBal()).subtract(
					insRefnd).subtract(totalRefund), formatter));
		}
		
		//Word Format
		if(isRetail){
			try {

				TemplateEngine engine = new TemplateEngine(reportName);
				reportName = earlySettlement.getFinReference() + "_" + "Memorandum.docx";
				engine.setTemplate("");
				engine.loadTemplateWithFontSize(11);
				engine.mergeFields(earlySettlement);
				engine.showDocument(this.window_ManualPaymentDialog, reportName, SaveFormat.DOCX);
				engine.close();
				engine = null;

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}else{
			
			//PDF Format
			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
			String usrName = (securityUser.getUsrFName().trim() +" "+securityUser.getUsrMName().trim()+" "+securityUser.getUsrLName()).trim();
			ReportGenerationUtil.generateReport(reportName, earlySettlement, new ArrayList<Object>() , true, 1, usrName, this.window_ManualPaymentDialog);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public FinanceScheduleDetailService getFinScheduleDetailService() {
		return finScheduleDetailService;
	}
	public void setFinScheduleDetailService(
			FinanceScheduleDetailService finScheduleDetailService) {
		this.finScheduleDetailService = finScheduleDetailService;
	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return overdueChargeRecoveryService;
	}
	public void setOverdueChargeRecoveryService(
			OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	public AccountsService getAccountsService() {
		return accountsService;
	}

	public RepaymentPostingsUtil getPostingsUtil() {
		return postingsUtil;
	}
	public void setPostingsUtil(RepaymentPostingsUtil postingsUtil) {
		this.postingsUtil = postingsUtil;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(
			AccountInterfaceService accountInterfaceService) {
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
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
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

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
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

	public List<Rule> getFeeChargesList() {
		return feeChargesList;
	}
	public void setFeeChargesList(List<Rule> feechargeslIst) {
		this.feeChargesList = feechargeslIst;
	}

	public List<FeeRule> getFeeRuleList() {
		return feeRuleList;
	}
	public void setFeeRuleList(List<FeeRule> feeRuleLIst) {
		this.feeRuleList = feeRuleLIst;
	}

	public FinanceType getFinanceType() {
		return financeType;
	}
	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}
	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public Object getFinanceCheckListReferenceDialogCtrl(){
		return null;
	}

	public List<DocumentDetails> getDocList() {
		return docList;
	}
	public void setDocList(List<DocumentDetails> docList) {
		this.docList = docList;
	}
	
	public List<FinanceReferenceDetail> getAgreementList() {
		return agreementList;
	}
	public void setAgreementList(List<FinanceReferenceDetail> agreementList) {
		this.agreementList = agreementList;
	}

	public CarLoanDetailService getCarLoanDetailService() {
		return carLoanDetailService;
	}
	public void setCarLoanDetailService(CarLoanDetailService carLoanDetailService) {
		this.carLoanDetailService = carLoanDetailService;
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
}