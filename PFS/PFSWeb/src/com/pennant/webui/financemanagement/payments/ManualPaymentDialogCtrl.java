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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceScheduleDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

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
	protected Decimalbox 		rpyAmount;
	protected Textbox 			repayAccount;
	protected Label 			repayAccountBal;
	protected Decimalbox 		priPayment;
	protected Decimalbox 		pftPayment;
	protected Combobox 			earlyRpyEffectOnSchd;
	protected Decimalbox 		totRefundAmt;
	
	//Effective Schedule Tab Details
	protected Label 			finSchType;
	protected Label 			finSchCcy;
	protected Label 			finSchMethod;
	protected Label 			finSchProfitDaysBasis;
	protected Label 			finSchReference;
	protected Label 			finSchGracePeriodEndDate;
	protected Label 			effectiveRateOfReturn;
	
	protected Listbox			listBoxSchedule;
	
	//Unvisible Fields
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

	protected Tab 				summaryDetailsTab;
	protected Tab 				repaymentDetailsTab;
	protected Tab 				effectiveScheduleTab;
	protected Tab 				dashboardTab;
	private Tabpanel 			tabpanel = null;
	private Div           		graphDivTabDiv;
	protected Tabpanels 		tabpanelsBoxIndexCenter; 
	private BigDecimal 			financeAmount;

	//Buttons
	protected Button 			btnPay;
	protected Button 			btnChangeRepay;
	protected Button 			btnCalcRepayments;
	protected Button 			btnSearchRepayAcctId; 
	protected Listbox 			listBoxPayment;
	protected Decimalbox 		refundPft;
	
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

	protected FinanceMain financeMain  = null;
	protected List<FinanceScheduleDetail> finSchDetails = null;
	private FinanceDetail financeDetail;
	RepayData repayData = null;
	RepayMain repayMain = null;
	List<RepayScheduleDetail> repaySchdList = null;
	private IAccounts iAccount;
	private LinkedHashMap<String,RepayScheduleDetail> refundMap;
	private boolean isLimitExceeded = false;
	
	private String moduleDefiner;
	static final List<ValueLabel> profitDayList = PennantAppUtil.getProfitDaysBasis();
	static final List<ValueLabel> earlyRpyEffectList = PennantAppUtil.getScheduleOn();

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

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("moduleDefiner")) {
			moduleDefiner = (String) args.get("moduleDefiner");
		}
		
		// READ OVERHANDED parameters !
		if (args.containsKey("selectedItem")) {
			financeMain = (FinanceMain) args.get("selectedItem");
			this.window_ManualPaymentDialog.setVisible(false);
			if(!doFillFinanceData(false)){
				this.window_ManualPaymentDialog.setVisible(true);
				setDialog(this.window_ManualPaymentDialog);
				
				this.borderlayout_ManualPayment.setHeight(getBorderLayoutHeight());
				int rowCount = grid_Summary.getRows().getVisibleItemCount() + grid_Repayment.getRows().getVisibleItemCount();
				int dialogHeight =  rowCount * 20 + 110; 
				int listboxHeight = borderLayoutHeight-dialogHeight;
				this.listBoxPayment.setHeight(listboxHeight+"px");
				this.repaymentDetailsTab.setSelected(true);
				this.rpyAmount.setFocus(true);
			}else{
				this.window_ManualPaymentDialog.onClose();
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		
		this.finType.setMaxlength(8);
		this.finReference.setMaxlength(20);
		this.finCcy.setMaxlength(8);
		this.lovDescCustCIF.setMaxlength(6);
		this.finBranch.setMaxlength(8);
		this.finStartDate.setFormat(PennantConstants.dateFormate);
		this.maturityDate.setFormat(PennantConstants.dateFormate);	
		this.totDisbursements.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totDownPayment.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totCpzAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totPriAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totPftAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totFeeAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totChargeAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totWaiverAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.schPriTillNextDue.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.schPftTillNextDue.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totPriPaid.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totPftPaid.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totPriDue.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totPftDue.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		
		this.finType1.setMaxlength(8);
		this.finReference1.setMaxlength(20);
		this.finCcy1.setMaxlength(8);
		this.lovDescCustCIF1.setMaxlength(6);
		this.finBranch1.setMaxlength(8);
		this.rpyAmount.setMaxlength(18);
		this.rpyAmount.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.priPayment.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.pftPayment.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.totRefundAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		
		this.overDuePrincipal.setMaxlength(18);
		this.overDuePrincipal.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.overDueProfit.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.lastFullyPaidDate.setFormat(PennantConstants.dateFormat);
		this.nextPayDueDate.setFormat(PennantConstants.dateFormat);
		this.accruedPft.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.provisionedAmt.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		this.pendingODC.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
		
		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgRpy
	 * @throws InterruptedException 
	 */
	private boolean doFillFinanceData(boolean isChgRpy) throws InterruptedException {
		logger.debug("Entering");
		
		boolean isOverDueExist = true;

		RepayData repayData = new RepayData();
		repayData.setBuildProcess("I");
		finSchDetails = getFinScheduleDetailService().getFinanceScheduleDetailById(financeMain.getFinReference(),"");
		
		BigDecimal pendingODC = BigDecimal.ZERO;
		pendingODC  = getOverdueChargeRecoveryService().getPendingODCAmount(financeMain.getFinReference());
		if(!moduleDefiner.equals(PennantConstants.WRITEOFF)){
			if(pendingODC != null && pendingODC.compareTo(new BigDecimal(0)) > 0) {
				try {
					PTMessageUtils.showErrorMessage("Clear overdue charges to proceed with repayment. ");
					return isOverDueExist;
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.debug(e);
				}
			}else {
				isOverDueExist = false;
				this.btnCalcRepayments.setDisabled(false);
			}
		}
		repayData.setPendingODC(pendingODC);

		repayData.setAccruedTillLBD(financeMain.getLovDescAccruedTillLBD());
		setRepayData(RepayCalculator.initiateRepay(repayData, financeMain, finSchDetails,"", null, false, null));
		repayData.getRepayMain().setLovDescFinFormatter(financeMain.getLovDescFinFormatter());
		setRepayMain(repayData.getRepayMain());
		doSetFieldProperties();		
		
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
		this.totDisbursements.setValue(PennantAppUtil.formateAmount(getRepayMain().getFinAmount(),
				getRepayMain().getLovDescFinFormatter()));
		this.totDownPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getDownpayment(),
				getRepayMain().getLovDescFinFormatter()));
		
		this.totCpzAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getTotalCapitalize(),
				getRepayMain().getLovDescFinFormatter()));
		this.totPriAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipal(),
				getRepayMain().getLovDescFinFormatter()));
		this.totPftAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfit(),
				getRepayMain().getLovDescFinFormatter()));
		this.totFeeAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,
				getRepayMain().getLovDescFinFormatter()));
		this.totChargeAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,
				getRepayMain().getLovDescFinFormatter()));
		this.totWaiverAmt.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,
				getRepayMain().getLovDescFinFormatter()));
		this.schPriTillNextDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalPayNow(),
				getRepayMain().getLovDescFinFormatter()));
		this.schPftTillNextDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitPayNow(),
				getRepayMain().getLovDescFinFormatter()));
		this.totPriPaid.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipal().subtract(getRepayMain().getPrincipalBalance()),
				getRepayMain().getLovDescFinFormatter()));
		this.totPftPaid.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfit().subtract(getRepayMain().getProfitBalance()),
				getRepayMain().getLovDescFinFormatter()));
		this.totPriDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalBalance(),
				getRepayMain().getLovDescFinFormatter()));
		this.totPftDue.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitBalance(),
				getRepayMain().getLovDescFinFormatter()));
		
		//Repayment modified Detaiils
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
			this.rpyAmount.setValue(PennantAppUtil.formateAmount(getRepayMain().getRepayAmountNow(),
					getRepayMain().getLovDescFinFormatter()));
		}
		this.repayAccount.setValue(getRepayMain().getRepayAccountId());
		this.repayAccountBal.setValue(getAcBalance(getRepayMain().getRepayAccountId()));
		this.priPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getPrincipalPayNow(),
				getRepayMain().getLovDescFinFormatter()));
		this.pftPayment.setValue(PennantAppUtil.formateAmount(getRepayMain().getProfitPayNow(),
				getRepayMain().getLovDescFinFormatter()));
		fillComboBox(this.earlyRpyEffectOnSchd, getRepayMain().getEarlyPayEffectOn(), earlyRpyEffectList, "");
		this.totRefundAmt.setValue(PennantAppUtil.formateAmount(getRepayMain().getRefundNow(),
				getRepayMain().getLovDescFinFormatter()));
		
		this.overDuePrincipal.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverduePrincipal(),
				getRepayMain().getLovDescFinFormatter()));
		this.overDueProfit.setValue(PennantAppUtil.formateAmount(getRepayMain().getOverdueProfit(),
				getRepayMain().getLovDescFinFormatter()));
		this.lastFullyPaidDate.setValue(getRepayMain().getDateLastFullyPaid());
		this.nextPayDueDate.setValue(getRepayMain().getDateNextPaymentDue());
		this.accruedPft.setValue(PennantAppUtil.formateAmount(getRepayMain().getAccrued(),
				getRepayMain().getLovDescFinFormatter()));
		this.pendingODC.setValue(PennantAppUtil.formateAmount(pendingODC,
				getRepayMain().getLovDescFinFormatter()));
		
		//Fill Schedule data
		if(moduleDefiner.equals(PennantConstants.SCH_EARLYPAY)){

			//Fetch Total Repayment Amount till Maturity date for Early Settlement
			BigDecimal repayAmt = getFinScheduleDetailService().getTotalRepayAmount(financeMain.getFinReference());

			this.rpyAmount.setValue(PennantAppUtil.formateAmount(repayAmt,
					getRepayMain().getLovDescFinFormatter()));

			if(!StringUtils.trimToEmpty(this.repayAccount.getValue()).equals("")){
				iAccount = new IAccounts();
				iAccount.setAccountId(this.repayAccount.getValue());

				// Check Available Funding Account Balance
				iAccount = getAccountInterfaceService().fetchAccountAvailableBal(iAccount,true);

				if(PennantAppUtil.unFormateAmount(this.rpyAmount.getValue(),
						getRepayData().getRepayMain().getLovDescFinFormatter()).compareTo(iAccount.getAcAvailableBal()) > 0){
					PTMessageUtils.showErrorMessage("Insufficient Balance");
					return true;
				}

				Events.sendEvent("onClick$btnCalcRepayments", this.window_ManualPaymentDialog, null);

				this.btnCalcRepayments.setVisible(false);
				this.btnChangeRepay.setVisible(false);
			}else{
				PTMessageUtils.showErrorMessage("Repay Account ID must Exist.");
				return true;
			}
			
		} else if(moduleDefiner.equals(PennantConstants.WRITEOFF)){
			
			//Fetch Total Repayment Amount till Maturity date for Early Settlement
			BigDecimal repayAmt = getFinScheduleDetailService().getTotalRepayAmount(financeMain.getFinReference());
			this.rpyAmount.setValue(PennantAppUtil.formateAmount(repayAmt,
					getRepayMain().getLovDescFinFormatter()));
			
			BigDecimal priAmt = getFinScheduleDetailService().getTotalUnpaidPriAmount(financeMain.getFinReference());
			this.totPriDue.setValue(PennantAppUtil.formateAmount(priAmt, getRepayMain().getLovDescFinFormatter()));
			
			BigDecimal pftAmt = getFinScheduleDetailService().getTotalUnpaidPftAmount(financeMain.getFinReference());
			this.totPftDue.setValue(PennantAppUtil.formateAmount(pftAmt, getRepayMain().getLovDescFinFormatter()));
			
			//Provisioned Amount For that Finance if Exist
			Provision provision = getProvisionService().getApprovedProvisionById(financeMain.getFinReference());
			BigDecimal provAmt = BigDecimal.ZERO;
			if(provision != null){
				provAmt = provision.getProvisionedAmt();
			}
			
			this.provisionedAmt.setValue(PennantAppUtil.formateAmount(provAmt,
					getRepayMain().getLovDescFinFormatter()));
			
			this.rpyAmount.setDisabled(true);
			this.row_provisionedAmt.setVisible(true);
			this.listBoxPayment.setVisible(false);
			
			this.btnCalcRepayments.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnPay.setVisible(true);
			this.btnPay.setDisabled(false);
			this.btnPay.setLabel(Labels.getLabel("label_PaymentDialog_btnPay_WriteOff.value"));
			/*this.label_PaymentDialog_RpyAmount.setValue(Labels.getLabel("label_PaymentDialog_WO_RpyAmount.value"));
			this.label_PaymentDialog_RepayAccount.setValue(Labels.getLabel("label_PaymentDialog_WO_RepayAccount.value"));
			this.label_PaymentDialog_Principal.setValue(Labels.getLabel("label_PaymentDialog_WO_Principal.value"));
			this.label_PaymentDialog_Profit.setValue(Labels.getLabel("label_PaymentDialog_WO_Profit.value"));*/
			this.btnPay.setTooltiptext("Click to Write-Off");
			
			isOverDueExist = false;
			
		} else{
			doFillRepaySchedules(repayData.getRepayScheduleDetails());
		}

		logger.debug("Leaving");
		return isOverDueExist;
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
	 */
	public void onClick$btnCalcRepayments(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		if(isValid()) {			
			
			RepayData repayData = calculateRepayments(this.financeMain, this.finSchDetails, false, null);
			if(repayData.getRepayMain().isEarlyPay()){
				
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
	 * Method for Schedule Modifications with Effective Schedule Method 
	 * @param repayData
	 * @throws InterruptedException
	 */
	public void setEarlyRepayEffectOnSchedule(RepayData repayData) throws InterruptedException{
		logger.debug("Entering");
		
		//Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(financeMain.getFinReference(),false,"");
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		setFinanceDetail(null);
		
		String method = null;
		// Schedule remodifications only when Effective Schedule Method modified
		if(!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || 
				this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(CalculationConstants.EARLYPAY_NOEFCT))){
			
			method = this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString();
			if(repayData.getRepayMain().getEarlyRepayNewSchd() != null){
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
			financeMain = finScheduleData.getFinanceMain();
			
			this.finSchType.setValue(financeMain.getFinType());
			this.finSchCcy.setValue(financeMain.getFinCcy() + "-"+ financeMain.getLovDescFinCcyName());
			this.finSchMethod.setValue(financeMain.getScheduleMethod());
			this.finSchProfitDaysBasis.setValue(PennantAppUtil.getlabelDesc(financeMain.getProfitDaysBasis(), profitDayList));
			this.finSchReference.setValue(financeMain.getFinReference());
			this.finSchGracePeriodEndDate.setValue(DateUtility.formateDate(financeMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
			this.effectiveRateOfReturn.setValue(financeMain.getEffectiveRateOfReturn().toString()+"%");
			
			//Fill Effective Schedule Details
			doFillScheduleList(finScheduleData);
			this.effectiveScheduleTab.setVisible(true);
			
			//Dashboard Details Report
			doLoadTabsData();
			dashboardTab.setVisible(true);
			doShowReportChart(finScheduleData);
		}
		
		//Repayments Calculation
		repayData = calculateRepayments(finScheduleData.getFinanceMain(), finScheduleData.getFinanceScheduleDetails(), true, method);
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

		boolean lastRec = false;
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
				map.put("window", this.window_ManualPaymentDialog);
				finRender.render(map, prvSchDetail, lastRec, true, false, feeChargesMap, showRate);

				if(i == sdSize - 1){						
					lastRec = true;
					finRender.render(map, prvSchDetail, lastRec, true, false, feeChargesMap, showRate);					
					break;
				}
			}
		}
		logger.debug("Leaving");
	}
	
	private RepayData calculateRepayments(FinanceMain financeMain, List<FinanceScheduleDetail> finSchDetails, boolean isReCal, String method){
		logger.debug("Entering");
		
		getRepayData().setBuildProcess("R");
		getRepayData().getRepayMain().setRepayAmountNow(PennantAppUtil.unFormateAmount(this.rpyAmount.getValue(),
				getRepayData().getRepayMain().getLovDescFinFormatter()));
		String sqlRule = getRuleService().getApprovedRuleById("REFUND", "REFUND", "").getSQLRule();
		Customer customer = getCustomerDetailsService().getCustomerForPostings(financeMain.getCustID());
		FinanceType financeType = getFinanceTypeService().getFinanceTypeByFinType(financeMain.getFinType());
		
		SubHeadRule subHeadRule = new SubHeadRule();
		
		try {
			BeanUtils.copyProperties(subHeadRule, customer);
			subHeadRule.setReqFinAcType(financeType.getFinAcType());
			subHeadRule.setReqFinCcy(financeType.getFinCcy());
			subHeadRule.setReqProduct(financeType.getLovDescProductCodeName());
			subHeadRule.setReqFinType(financeType.getFinType());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		getRepayData().getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		getRepayData().getRepayMain().setProfitPayNow(BigDecimal.ZERO);
		repayData = RepayCalculator.initiateRepay(getRepayData(), financeMain, finSchDetails, sqlRule, subHeadRule, isReCal, method);
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

		this.btnPay.setDisabled(false);
		this.btnChangeRepay.setDisabled(false);
		this.btnCalcRepayments.setDisabled(true);
		this.rpyAmount.setDisabled(true);
		this.btnSearchRepayAcctId.setDisabled(true);
		if(financeMain.isAlwIndRate() || financeMain.isGrcAlwIndRate()){
			PTMessageUtils.showErrorMessage(" Indicative Rate schedules not included ... ");
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayment Amount 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnChangeRepay(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());

		doFillFinanceData(true);
		this.btnPay.setDisabled(true);
		this.btnChangeRepay.setDisabled(true);
		this.btnCalcRepayments.setDisabled(false);
		this.rpyAmount.setDisabled(false);
		this.btnSearchRepayAcctId.setDisabled(false);
		this.repaymentDetailsTab.setSelected(true);
		this.rpyAmount.setFocus(true);
		this.effectiveScheduleTab.setVisible(false);
		this.dashboardTab.setVisible(false);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for event of Changing Repayment Amount 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(!moduleDefiner.equals(PennantConstants.WRITEOFF)){
			if(isValid() && !isLimitExceeded) {
				this.btnChangeRepay.setDisabled(true);
				this.btnCalcRepayments.setDisabled(true);
				
				boolean isSuccess = false;
				//If Schedule Remodified Save into DB or else only add Repayment Details
				if(!(this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select) || 
						this.earlyRpyEffectOnSchd.getSelectedItem().getValue().toString().equals("NOEFCT"))){
					
					isSuccess = saveRepaySchedule(getFinanceDetail().getFinScheduleData().getFinanceMain(), 
							getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(),
							getFinanceDetail().getFinScheduleData().getRepayInstructions(),true);
				}else{
					isSuccess = saveRepaySchedule(financeMain, finSchDetails,null,false);
				}
				
				if(isSuccess){
					closeDialog(this.window_ManualPaymentDialog, "ManualPayment");
				}else {
					PTMessageUtils.showErrorMessage(" Not processed ... ");
					this.btnChangeRepay.setDisabled(false);
					this.btnCalcRepayments.setDisabled(false);
					this.btnPay.setDisabled(true);
				}
			}
			
		}else{
			
			AEAmountCodes amountCodes = null;
			AEAmounts aeAmounts = new AEAmounts();
			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
			DataSet dataSet = aeAmounts.createDataSet(financeMain, "WRITEOFF", 
					curBDay, financeMain.getMaturityDate());		
			
			FinanceProfitDetail financeProfitDetail = getManualPaymentService().getFinProfitDetailsById(financeMain.getFinReference());
			amountCodes = aeAmounts.procAEAmounts(financeMain, finSchDetails,
					financeProfitDetail == null? new FinanceProfitDetail():financeProfitDetail, curBDay);
			
			amountCodes.setProvAmt(PennantAppUtil.unFormateAmount(this.provisionedAmt.getValue(),
					getRepayData().getRepayMain().getLovDescFinFormatter()));
			
			//Checking For Finance is RIA or Not
			boolean isRIAFianance = getFinanceTypeService().checkRIAFinance(financeMain.getFinType());
			
			if((Boolean) getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, false,
					isRIAFianance, "Y", curBDay, null, false).get(0)){

				// Show a confirm box
				final String msg = "Write-off Successfully Done.";
				final String title = Labels.getLabel("message.Information");

				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(msg, title,
						MultiLineMessageBox.OK, MultiLineMessageBox.INFORMATION, true);
				
				getFinanceDetailService().updateFinBlackListStatus(financeMain.getFinReference());
				
			}else{
				
				// Show a confirm box
				final String msg = "Write-off Failed in Postings.";
				final String title = Labels.getLabel("message.Error");

				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(msg, title,
						MultiLineMessageBox.ABORT, MultiLineMessageBox.QUESTION, true);
				
			}
			closeDialog(this.window_ManualPaymentDialog,"");
			
		}
		logger.debug("Leaving" + event.toString());
	}
	
	private boolean saveRepaySchedule(FinanceMain financeMain, List<FinanceScheduleDetail> finSchDetails, 
			List<RepayInstruction> repayInstructions,boolean schdlReModified){
		
		boolean isSuccess = getManualPaymentService().saveOrUpdate(financeMain, finSchDetails, repayInstructions,
				getRepayData().getRepayMain(), getRepayData().getRepayScheduleDetails(), schdlReModified);
		
		return isSuccess;
	}

	/**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchRepayAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Object dataObject;

		List<Accounts> accountList = new ArrayList<Accounts>();
		accountList = getAccountsService().getAccountsByAcPurpose("M");
		String acType = "";
		for (int i = 0; i < accountList.size(); i++) {
			acType = acType + accountList.get(i).getAcType();
		}

		List<IAccounts> iAccountList = new ArrayList<IAccounts>();
		IAccounts iAccount = new IAccounts();
		iAccount.setAcCcy(this.finCcy.getValue());
		iAccount.setAcType(acType);
		iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
		try {
			iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
			dataObject = ExtendedSearchListBox.show(this.window_ManualPaymentDialog,
					"Accounts", iAccountList);
			if (dataObject instanceof String) {
				this.repayAccount.setValue(dataObject.toString());
			} else {
				IAccounts details = (IAccounts) dataObject;
				if (details != null) {
					this.repayAccount.setValue(details.getAccountId());
					this.repayAccountBal.setValue(getAcBalance(details.getAccountId()));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
					Messagebox.ABORT, Messagebox.ERROR);
		}
		logger.debug("Leaving " + event.toString());
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
		BigDecimal totalRefund = new BigDecimal(0);
		if(repaySchdList != null){
			for(int i=0;i<repaySchdList.size();i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(repaySchd.getDefSchdDate()+" ["+repaySchd.getSchdFor()+"] ");
				lc.setStyle("font-weight:bold;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdBal(),
						getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdBal(),
						getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdPayNow(),
						getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdPayNow(),
						getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRefundMax(),
						getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				refundPft = new Decimalbox();
				refundPft.setWidth("99.9%");
				refundPft.setMaxlength(18);
				refundPft.setFormat(PennantAppUtil.getAmountFormate(getRepayMain().getLovDescFinFormatter()));
				refundPft.setStyle("border:0px");
				refundPft.setDisabled(false);
				if(repaySchd.isAllowRefund()) {
					refundPft.setInplace(true);
					List<Object> list = new ArrayList<Object>(3);
					list.add(refundPft);
					list.add(repaySchd.getDefSchdDate());
					refundPft.setValue(PennantAppUtil.formateAmount(
							(repaySchd.getRefundReq()).compareTo(new BigDecimal(0)) == 0?
									repaySchd.getRefundDefault():repaySchd.getRefundReq(),
									getRepayMain().getLovDescFinFormatter()));
					totalRefund =  totalRefund.add(repaySchd.getRefundReq());
					refundPft.addForward("onChange",window_ManualPaymentDialog,"onRefundValueChanged", list);
				}else {
					refundPft.setDisabled(true);
					refundPft.setValue(PennantAppUtil.formateAmount(repaySchd.getRefundDefault(),
							getRepayMain().getLovDescFinFormatter()));
					refundPft.setStyle("text-align:right;background:none;border:0px;font-color:#AAAAAA;");
				}
				lc = new Listcell();
				lc.appendChild(refundPft);
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.
						getPrincipalSchdPayNow()).subtract(PennantAppUtil.unFormateAmount(refundPft.getValue(),
								getRepayData().getRepayMain().getLovDescFinFormatter()));
				lc = new Listcell(PennantAppUtil.amountFormate(netPay,
								getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRepayBalance(),
						getRepayMain().getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				this.listBoxPayment.appendChild(item);
				if(refundMap.containsKey(repaySchd.getSchDate().toString())) {
					refundMap.remove(repaySchd.getSchDate().toString());
				}
				refundMap.put(repaySchd.getSchDate().toString(),repaySchd);
			}
			this.totRefundAmt.setValue(PennantAppUtil.formateAmount(totalRefund,
					getRepayMain().getLovDescFinFormatter()));
		}
		logger.debug("Leaving");
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
		if(refundMap.containsKey(schDate)){
			RepayScheduleDetail scheduleDetail = refundMap.get(schDate);
			if(scheduleDetail.getRefundMax().compareTo(PennantAppUtil.unFormateAmount(refundProfit.getValue(),
					getRepayData().getRepayMain().getLovDescFinFormatter()))<0) {
				PTMessageUtils.showErrorMessage(" Limit exceeded ... ");
				isLimitExceeded = true;
				return;
			}
			scheduleDetail.setRefundReq(PennantAppUtil.unFormateAmount(refundProfit.getValue(),
					getRepayData().getRepayMain().getLovDescFinFormatter()));
			refundMap.remove(schDate);
			refundMap.put(schDate, scheduleDetail);
		}

		doFillRepaySchedules(new ArrayList<RepayScheduleDetail>(refundMap.values()));
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method to validate data
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	private boolean isValid() throws InterruptedException {
		logger.debug("Entering");
		
		if(StringUtils.trimToEmpty(this.repayAccount.getValue()).equals("")){
			PTMessageUtils.showErrorMessage(" Please Enter Repayment Account ID ... ");
			return false;
		}else if(this.rpyAmount.getValue() == null || this.rpyAmount.getValue().compareTo(new BigDecimal(0)) == 0){
			PTMessageUtils.showErrorMessage(" Please Enter Repayment Amount ... ");
			return false;
		}else if(!this.rpyAmount.isDisabled() && this.rpyAmount.getValue().compareTo(new BigDecimal(0)) > 0){
			iAccount = new IAccounts();
			iAccount.setAccountId(this.repayAccount.getValue());

			// Check Available Funding Account Balance
			iAccount = getAccountInterfaceService().fetchAccountAvailableBal(iAccount,true);

			if(PennantAppUtil.unFormateAmount(this.rpyAmount.getValue(),
					getRepayData().getRepayMain().getLovDescFinFormatter()).compareTo(iAccount.getAcAvailableBal()) > 0){
				PTMessageUtils.showErrorMessage("Insufficient Balance");
				return false;
			}
			
		}else if(getRepayMain().getRepayAmountExcess().compareTo(new BigDecimal(0)) > 0) {
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
	
	private String getAcBalance(String acId){
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId),
					getRepayMain().getLovDescFinFormatter());
        }else{
        	return "";
        }
	}
	
	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	private void doLoadTabsData() throws InterruptedException {
		logger.debug("Entering ");
		
		if(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel") != null){
			tabpanelsBoxIndexCenter.removeChild(tabpanelsBoxIndexCenter.getFellowIfAny("graphTabPanel"));
		}
		
		tabpanel = new Tabpanel();
		tabpanel.setId("graphTabPanel");
		graphDivTabDiv = new Div();
		graphDivTabDiv.setHeight("100%");
		this.graphDivTabDiv.setStyle("overflow:auto;");
		this.tabpanel.setHeight(this.borderLayoutHeight-80+"px");
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
		if(listScheduleDetail!=null){
			ChartSetElement chartSetElement;
			financeAmount = BigDecimal.ZERO;
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				financeAmount=financeAmount.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getDisbAmount(), 
						scheduleData.getFinanceMain().getLovDescFinFormatter()));
				downPayment=downPayment.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getDownPaymentAmount(), 
						scheduleData.getFinanceMain().getLovDescFinFormatter()));
				capitalized=capitalized.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getCpzAmount(), 
						scheduleData.getFinanceMain().getLovDescFinFormatter()));

				scheduleProfit=scheduleProfit.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd(), 
						scheduleData.getFinanceMain().getLovDescFinFormatter()));
				schedulePrincipal=schedulePrincipal.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(), 
						scheduleData.getFinanceMain().getLovDescFinFormatter()));

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

}