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
 * FileName    		:  ScheduleDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ScheduleDetailDialogCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(ScheduleDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ScheduleDetailDialog; 			// autoWired
	protected Listbox 		listBoxSchedule; 						// autoWired
	protected Listbox 		listBoxTakafulSchedule; 				// autoWired
	protected Tab	 		financeTakafulSchdDetailsTab;			// autoWired
	protected Borderlayout  borderlayoutScheduleDetail;				// autoWired
	
	//Finance Schedule Details Tab
	protected Grid 			grid_effRateOfReturn; 					// autoWired
	
	protected Label			schdl_finType;
	protected Label			schdl_finReference;
	protected Label			schdl_finCcy;
	protected Label			schdl_profitDaysBasis;
	protected Label			schdl_noOfTerms;
	protected Label			schdl_grcEndDate;
	protected Label			schdl_startDate;
	protected Label			schdl_maturityDate;
	protected Decimalbox	schdl_purchasePrice;
	protected Decimalbox	schdl_otherExp;
	protected Decimalbox	schdl_totalCost;
	protected Decimalbox	schdl_totalPft;
	protected Decimalbox	schdl_contractPrice;
	protected Label 		schdl_BankShare;
	protected Label 		schdl_NonBankShare;
	
	public 	  Label 		effectiveRateOfReturn; 					
	
	protected Label			label_ScheduleDetailDialog_FinType;
	protected Label			label_ScheduleDetailDialog_FinReference;
	protected Label			label_ScheduleDetailDialog_FinCcy;
	protected Label			label_ScheduleDetailDialog_ProfitDaysBasis;
	protected Label			label_ScheduleDetailDialog_NoOfTerms;
	protected Label			label_ScheduleDetailDialog_GrcEndDate;
	protected Label			label_ScheduleDetailDialog_StartDate;
	protected Label			label_ScheduleDetailDialog_MaturityDate;
	protected Label			label_ScheduleDetailDialog_PurchasePrice;
	protected Label			label_ScheduleDetailDialog_OthExpenses;
	protected Label			label_ScheduleDetailDialog_TotalCost;
	protected Label			label_ScheduleDetailDialog_TotalPft;
	protected Label			label_ScheduleDetailDialog_ContractPrice;
	protected Label 		label_FinanceMainDialog_EffectiveRateOfReturn;
	protected Label 		label_ScheduleDetailDialog_NonBankShare;
	protected Label 		label_ScheduleDetailDialog_BankShare;
	protected Label 		label_ScheduleDetailDialog_DownPaySchedule;
	protected Label 		label_ScheduleDetailDialog_DPScheduleLink;
	
	
	protected Button 		btnAddReviewRate; 						// autoWired
	protected Button 		btnChangeRepay; 						// autoWired
	protected Button 		btnAddDisbursement; 					// autoWired
	protected Button 		btnAddDatedSchedule; 					// autoWired
	protected Button 		btnAddDefferment; 						// autoWired
	protected Button 		btnRmvDefferment; 						// autoWired
	protected Button 		btnAddTerms; 							// autoWired
	protected Button 		btnRmvTerms; 							// autoWired
	protected Button 		btnReCalcualte; 						// autoWired
	protected Button 		btnSubSchedule; 						// autoWired
	protected Button 		btnChangeProfit; 						// autoWired
	protected Button 		btnChangeFrq; 						    // autoWired
	protected Button 		btnReschedule; 						    // autoWired
	protected Button 		btnPrintSchedule; 						// autoWired
	protected Button 		btnPrintTakafulSchedule;				// autoWired
	
	protected Listheader    listheader_ScheduleDetailDialog_Date;
	protected Listheader    listheader_ScheduleDetailDialog_ScheduleEvent;
	protected Listheader    listheader_ScheduleDetailDialog_CalProfit;
	protected Listheader    listheader_ScheduleDetailDialog_SchFee;
	protected Listheader    listheader_ScheduleDetailDialog_SchProfit;
	protected Listheader    listheader_ScheduleDetailDialog_Principal;
	protected Listheader    listheader_ScheduleDetailDialog_Total;
	protected Listheader    listheader_ScheduleDetailDialog_ScheduleEndBal;
	
	//Takaful Schedule Tab
	protected Listheader    listheader_TakafulScheduleDetailDialog_Date;
	protected Listheader    listheader_TakafulScheduleDetailDialog_Rate;
	protected Listheader    listheader_TakafulScheduleDetailDialog_FinOSBal;
	protected Listheader    listheader_TakafulScheduleDetailDialog_Premium;
	
	// Newly Added Headers
	protected Listheader    listHeader_cashFlowEffect;
	protected Listheader    listHeader_vSProfit;
	protected Listheader    listHeader_orgPrincipalDue;
	
	private Object financeMainDialogCtrl = null;
	private FeeDetailDialogCtrl feeDetailDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	private FinanceScheduleDetail prvSchDetail;
	private FinScheduleListItemRenderer finRender;
	private FinanceDetailService financeDetailService;
	
	private String moduleDefiner = "";
	private Map<Date,ArrayList<FeeRule>> feeChargesMap = null;
	private List<ValueLabel> profitDaysBasisList = null;
	private boolean isWIF = false;
	private String roleCode = "";
	private String menuItemRightName = null;
	private String defMethod = SystemParameterDetails.getSystemParameterValue("DEF_METHOD").toString();
	protected Row  	row_istisna;
	protected Row  	row_Musharak;
	protected Hbox  hbox_LinkedDownPayRef;
	protected Decimalbox	schdl_Repayprofit;
	protected Decimalbox	schdl_Graceprofit;
	protected Label label_ScheduleDetailDialog_Graceprofit;
	protected Label label_ScheduleDetailDialog_Repayprofit;
	
	/**
	 * default constructor.<br>
	 */
	public ScheduleDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ScheduleDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
		}
		
		if (args.containsKey("moduleDefiner")) {
			moduleDefiner = (String) args.get("moduleDefiner");
		}
		
		if (args.containsKey("isWIF")) {
			isWIF = (Boolean) args.get("isWIF");
		}
		
		if (args.containsKey("roleCode")) {
			roleCode = (String) args.get("roleCode");
		}
		
		if (args.containsKey("menuItemRightName")) {
			menuItemRightName = (String) args.get("menuItemRightName");
		}
		
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}
		
		if (args.containsKey("feeDetailDialogCtrl")) {
			this.feeDetailDialogCtrl = (FeeDetailDialogCtrl) args.get("feeDetailDialogCtrl");
		}
		
		boolean isEnquiry = false;
		if (args.containsKey("isEnquiry")) {
			isEnquiry = (Boolean) args.get("isEnquiry");
		}

		if(!isEnquiry){
			doCheckRights();
		}
		doSetLabels();
		doShowDialog();
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * 
	 * Set the Labels for the ListHeader and Basic Details based oon the Finance Types
	 * right is only a string. <br>
	 */
	private void doSetLabels() {
		logger.debug("Entering");
		String product = getFinScheduleData().getFinanceType().getFinCategory();
		
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();		
		this.schdl_finType.setValue(financeMain.getFinType() +" - "+financeMain.getLovDescFinTypeName());
		this.schdl_finCcy.setValue(financeMain.getFinCcy() +" - "+financeMain.getLovDescFinCcyName());
		this.schdl_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(financeMain.getProfitDaysBasis(), profitDaysBasisList));
		
		String productType = (product.substring(0, 1)).toUpperCase()+(product.substring(1)).toLowerCase();
		label_ScheduleDetailDialog_FinType.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinType.value"));
		label_ScheduleDetailDialog_FinReference.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinReference.value"));
		label_ScheduleDetailDialog_FinCcy.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinCcy.value"));
		label_ScheduleDetailDialog_ProfitDaysBasis.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_ProfitDaysBasis.value"));
		label_ScheduleDetailDialog_NoOfTerms.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_NumberOfTerms.value"));
		label_ScheduleDetailDialog_GrcEndDate.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinGracePeriodEndDate.value"));
		label_ScheduleDetailDialog_StartDate.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinStartDate.value"));
		label_ScheduleDetailDialog_MaturityDate.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinMaturityDate.value"));
		label_ScheduleDetailDialog_PurchasePrice.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_PurchasePrice.value"));
		label_ScheduleDetailDialog_OthExpenses.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_OthExpenses.value"));
		label_ScheduleDetailDialog_TotalCost.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_TotalCost.value"));
		label_ScheduleDetailDialog_TotalPft.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_TotalPft.value"));
		label_ScheduleDetailDialog_ContractPrice.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_ContractPrice.value"));
		label_FinanceMainDialog_EffectiveRateOfReturn.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_EffectiveRateOfReturn.value"));
		
		if (product.equals(PennantConstants.FINANCE_PRODUCT_ISTISNA)) {
			this.row_istisna.setVisible(true);
			this.label_ScheduleDetailDialog_Graceprofit.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_ProfitInGrace.value"));
			this.label_ScheduleDetailDialog_Repayprofit.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_ProfitInRepay.value"));
		}
		
		listheader_ScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Date"));
		listheader_ScheduleDetailDialog_ScheduleEvent.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_ScheduleEvent"));
		listheader_ScheduleDetailDialog_CalProfit.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_CalProfit"));
		listheader_ScheduleDetailDialog_SchFee.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_SchFee"));
		listheader_ScheduleDetailDialog_SchProfit.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_SchProfit"));
		listheader_ScheduleDetailDialog_Principal.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Principal"));
		listheader_ScheduleDetailDialog_Total.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Total"));
		listheader_ScheduleDetailDialog_ScheduleEndBal.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_ScheduleEndBal"));
		listHeader_cashFlowEffect.setLabel(Labels.getLabel("listheader_" + productType +"_CashFlowEffect"));
		listHeader_vSProfit.setLabel(Labels.getLabel("listheader_" + productType +"_VsProfit"));
		listHeader_orgPrincipalDue.setLabel(Labels.getLabel("listheader_" + productType +"_OrgPrincipalDue"));
		
		//Takaful Schedule Tab
		listheader_TakafulScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Date"));
		listheader_TakafulScheduleDetailDialog_Rate.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Rate"));
		listheader_TakafulScheduleDetailDialog_FinOSBal.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_OSPrincipal"));
		listheader_TakafulScheduleDetailDialog_Premium.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_TakafulPremium"));
		
		if(!moduleDefiner.equals("")){
			listHeader_cashFlowEffect.setVisible(false);
			listHeader_vSProfit.setVisible(false);
			listHeader_orgPrincipalDue.setVisible(false);
		}

		if (product.equals(PennantConstants.FINANCE_PRODUCT_MUSHARAKA)) {
			this.row_Musharak.setVisible(true);
			this.label_ScheduleDetailDialog_BankShare.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_BankShare.value"));
			this.label_ScheduleDetailDialog_NonBankShare.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_NonBankShare.value"));
		}
		logger.debug("Leaving");
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
		
		String dialogName = "FinanceMainDialog";
		if(isWIF){
			dialogName = "WIFFinanceMainDialog";
		}

		getUserWorkspace().alocateAuthorities(dialogName,roleCode, menuItemRightName);

		// Schedule related buttons
		this.btnAddReviewRate.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate"));
		this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay"));
		this.btnAddDisbursement.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb"));
		this.btnAddDatedSchedule.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDatedSchd"));
		this.btnAddDefferment.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment"));
		this.btnRmvDefferment.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment"));
		this.btnAddTerms.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddTerms"));
		this.btnRmvTerms.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvTerms"));
		this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));
		this.btnSubSchedule.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnBuildSubSchd"));
		this.btnChangeProfit.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeProfit"));
		this.btnChangeFrq.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeFrq"));
		this.btnReschedule.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnReschedule"));
		
		this.btnAddReviewRate.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate"));
		this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay"));
		this.btnAddDisbursement.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb"));
		this.btnAddDatedSchedule.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDatedSchd"));
		this.btnAddDefferment.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment"));
		this.btnRmvDefferment.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment"));
		this.btnAddTerms.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddTerms"));
		this.btnRmvTerms.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvTerms"));
		this.btnReCalcualte.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));
		this.btnSubSchedule.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnBuildSubSchd"));
		this.btnChangeProfit.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeProfit"));
		this.btnChangeFrq.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeFrq"));
		this.btnReschedule.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnReschedule"));
		
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			
			// fill the components with the data
			doFillScheduleList(this.finScheduleData);
			
			//Schedule Maintenance Buttons hiding for Other Event Operations
			if(!StringUtils.trimToEmpty(moduleDefiner).equals("")){
				doOpenChildWindow();
			}
			
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = {this.getClass()};
					Object[] stringParameter = {this};
					if (financeMainDialogCtrl.getClass().getMethod("setScheduleDetailDialogCtrl", paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setScheduleDetailDialogCtrl", paramType).invoke(financeMainDialogCtrl, stringParameter);
					}

				} catch (Exception e) {
					logger.error(e);
				}
			}

			getBorderLayoutHeight();
			if(isWIF){
				this.listBoxSchedule.setHeight(this.borderLayoutHeight- 275 +"px");
				this.listBoxTakafulSchedule.setHeight(this.borderLayoutHeight- 280 +"px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight-30+"px");
			}else{
				this.listBoxSchedule.setHeight(this.borderLayoutHeight- 305 +"px");
				this.listBoxTakafulSchedule.setHeight(this.borderLayoutHeight- 335 +"px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight-80+"px");
			}

		 if(defMethod.equals(PennantConstants.DEF_METHOD_RECALRATE)){
			 this.btnRmvDefferment.setVisible(false);
		  }
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the Schedule Listbox with provided generated schedule.
	 * 
	 * @param FinScheduleData
	 *            (aFinSchData)
	 */
	public void doFillScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		
		if(getFinScheduleData().getFinanceMain().isStepFinance() && moduleDefiner.equals("")){
			if(getFinScheduleData().getFinanceMain().isAlwManualSteps()){  // TODO  Based on Fin type we need to change list header label name
				this.listHeader_cashFlowEffect.setLabel(Labels.getLabel("listheader_sellingPricePft.label"));
				this.listHeader_vSProfit.setLabel(Labels.getLabel("listheader_rebateBucket.label"));
			}else{
				this.listHeader_cashFlowEffect.setVisible(true);
				this.listHeader_vSProfit.setVisible(true);
				this.listHeader_orgPrincipalDue.setVisible(true);
			}

			String product = getFinScheduleData().getFinanceType().getFinCategory();
			String productType = (product.substring(0, 1)).toUpperCase()+(product.substring(1)).toLowerCase();
			listHeader_cashFlowEffect.setLabel(Labels.getLabel("listheader_" + productType +"_CashFlowEffect"));
			listHeader_vSProfit.setLabel(Labels.getLabel("listheader_" + productType +"_VsProfit"));
			listHeader_orgPrincipalDue.setLabel(Labels.getLabel("listheader_" + productType +"_OrgPrincipalDue"));

		} else {
			this.listHeader_cashFlowEffect.setVisible(false);
			this.listHeader_vSProfit.setVisible(false);
			this.listHeader_orgPrincipalDue.setVisible(false);
		}
		String productType = (getFinScheduleData().getFinanceType().getFinCategory().substring(0, 1)).toUpperCase()+(getFinScheduleData().getFinanceType().getFinCategory().substring(1)).toLowerCase();
		if (getFinScheduleData().getFinanceType().isAllowDownpayPgm()) {
			this.hbox_LinkedDownPayRef.setVisible(true);
			this.label_ScheduleDetailDialog_DownPaySchedule.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_DownPaySchedule.value"));
			this.label_ScheduleDetailDialog_DPScheduleLink.setValue(getFinScheduleData().getFinanceMain().getFinReference()+"_DP");
		}

		if(getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals("") ||
				getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals(PennantConstants.List_Select) ||
				getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
			this.listheader_ScheduleDetailDialog_SchFee.setVisible(false);
		}else{
			this.listheader_ScheduleDetailDialog_SchFee.setVisible(true);
		}

		setFinScheduleData(aFinSchData);
		FinanceMain financeMain = aFinSchData.getFinanceMain();
		int ccyFormatter = financeMain.getLovDescFinFormatter();

		this.schdl_purchasePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_otherExp.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_totalCost.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_totalPft.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_contractPrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_finReference.setValue(financeMain.getFinReference());
		this.schdl_noOfTerms.setValue(String.valueOf(financeMain.getNumberOfTerms() + financeMain.getGraceTerms()));
		this.schdl_grcEndDate.setValue(DateUtility.formatDate(financeMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
		this.schdl_startDate.setValue(DateUtility.formatDate(financeMain.getFinStartDate(), PennantConstants.dateFormate));
		this.schdl_maturityDate.setValue(DateUtility.formatDate(financeMain.getMaturityDate(), PennantConstants.dateFormate));
		this.schdl_purchasePrice.setValue(PennantAppUtil.formateAmount(financeMain.getFinAmount(), ccyFormatter));
		this.schdl_otherExp.setValue(PennantAppUtil.formateAmount(financeMain.getFeeChargeAmt(), ccyFormatter));
		this.schdl_totalCost.setValue(PennantAppUtil.formateAmount(financeMain.getFinAmount().add(
				financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeMain.getFeeChargeAmt()), ccyFormatter));
		this.schdl_totalPft.setValue(PennantAppUtil.formateAmount(financeMain.getTotalProfit(), ccyFormatter));
		this.schdl_contractPrice.setValue(PennantAppUtil.formateAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()).add(
				financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeMain.getFeeChargeAmt()), ccyFormatter));

		if(financeMain.getEffectiveRateOfReturn() == null){
			financeMain.setEffectiveRateOfReturn(BigDecimal.ZERO);
		}
		this.effectiveRateOfReturn.setValue(PennantApplicationUtil.formatRate(financeMain.getEffectiveRateOfReturn().doubleValue(), 
				PennantConstants.rateFormate)+"%");

		if (aFinSchData.getFinanceType().getLovDescProductCodeName().equals(PennantConstants.FINANCE_PRODUCT_ISTISNA)) {

			BigDecimal istisnaExp = BigDecimal.ZERO; 
			BigDecimal totBillingAmt = BigDecimal.ZERO; 
			BigDecimal conslFee = BigDecimal.ZERO; 
			BigDecimal totIstisnaCost = BigDecimal.ZERO; 

			//Amounts Calculation
			for (FinanceDisbursement disburse : aFinSchData.getDisbursementDetails()) {
				if("B".equals(disburse.getDisbType())){
					totBillingAmt = totBillingAmt.add(disburse.getDisbClaim());
				}else if("C".equals(disburse.getDisbType())){
					conslFee = conslFee.add(disburse.getDisbAmount());
				}else if("E".equals(disburse.getDisbType())){
					istisnaExp = istisnaExp.add(disburse.getDisbAmount());
				}

				totIstisnaCost = totIstisnaCost.add(disburse.getDisbAmount());	
			}

			this.schdl_purchasePrice.setValue(PennantAppUtil.formateAmount(istisnaExp, ccyFormatter));
			this.schdl_otherExp.setValue(PennantAppUtil.formateAmount(totBillingAmt, ccyFormatter));
			this.schdl_totalCost.setValue(PennantAppUtil.formateAmount(conslFee, ccyFormatter));
			this.schdl_totalPft.setValue(PennantAppUtil.formateAmount(totIstisnaCost, ccyFormatter));

			// finAmount-down payment + total profit
			BigDecimal projectValue=financeMain.getFinAmount().subtract(financeMain.getDownPayment())
					.add(aFinSchData.getFinanceMain().getTotalProfit());
			this.schdl_contractPrice.setValue(PennantAppUtil.formateAmount(projectValue, ccyFormatter));

			this.schdl_Repayprofit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.schdl_Repayprofit.setValue(PennantAppUtil.formateAmount(financeMain.getTotalProfit().subtract(financeMain.getTotalGracePft()),
					ccyFormatter));

			this.schdl_Graceprofit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.schdl_Graceprofit.setValue(PennantAppUtil.formateAmount(financeMain.getTotalGracePft(), ccyFormatter));
		}

		if (aFinSchData.getFinanceType().getLovDescProductCodeName().equals(PennantConstants.FINANCE_PRODUCT_MUSHARAKA)) {
			BigDecimal finAmount = financeMain.getFinAmount();
			BigDecimal downPayment = financeMain.getDownPayment();

			BigDecimal nonbankShare = downPayment.multiply(new BigDecimal(100)).divide(finAmount,2,RoundingMode.HALF_DOWN);
			BigDecimal bankShare = finAmount.subtract(downPayment).multiply(new BigDecimal(100)).divide(finAmount,2,RoundingMode.HALF_DOWN);

			this.schdl_NonBankShare.setValue(PennantApplicationUtil.formatRate(nonbankShare.doubleValue(), 
					PennantConstants.rateFormate)+"%");
			this.schdl_BankShare.setValue(PennantApplicationUtil.formatRate(bankShare.doubleValue(), 
					PennantConstants.rateFormate)+"%");
		}	

		//Fee Charges List Render For First Disbursement only/Existing
		List<FeeRule> feeRuleList = aFinSchData.getFeeRules();

		//Get Finance Fee Details For Schedule Render Purpose In maintenance Stage
		List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
		if(!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF){
			approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinReference(), isWIF);
		}

		//Check Rights Based on Condition is EITHER WIF or MAIN
		String dialogName = "FinanceMainDialog";
		if(isWIF){
			dialogName = "WIFFinanceMainDialog";
		}

		//New Fee Rules for Schedule Maintenance/New
		feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
		Map<Date,ArrayList<FeeRule>> tempFeeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
		for (FeeRule fee : feeRuleList) {
			if(feeChargesMap.containsKey(fee.getSchDate())){
				ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
				int seqNo = 0;
				for (FeeRule feeRule : feeChargeList) {
					if(feeRule.getFeeCode().equals(fee.getFeeCode())){
						if(seqNo < feeRule.getSeqNo() && fee.getSchDate().compareTo(feeRule.getSchDate()) == 0){
							seqNo = feeRule.getSeqNo();
						}
					}
				}
				fee.setSeqNo(seqNo+1);
				feeChargeList.add(fee);
				feeChargesMap.put(fee.getSchDate(), feeChargeList);
				tempFeeChargesMap.put(fee.getSchDate(), feeChargeList);

			}else{
				ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
				feeChargeList.add(fee);
				feeChargesMap.put(fee.getSchDate(), feeChargeList);
				tempFeeChargesMap.put(fee.getSchDate(), feeChargeList);
			}
		}

		//For Approved Fee Rule Details
		for (FeeRule fee : approvedFeeRules) {
			if(tempFeeChargesMap.containsKey(fee.getSchDate())){
				ArrayList<FeeRule> feeChargeList = tempFeeChargesMap.get(fee.getSchDate());
				int seqNo = 0;
				for (FeeRule feeRule : feeChargeList) {
					if(feeRule.getFeeCode().equals(fee.getFeeCode())){
						if(seqNo < feeRule.getSeqNo() && fee.getSchDate().compareTo(feeRule.getSchDate()) == 0){
							seqNo = feeRule.getSeqNo();
						}
					}
				}
				fee.setSeqNo(seqNo+1);
				feeChargeList.add(fee);
				tempFeeChargesMap.put(fee.getSchDate(), feeChargeList);

			}else{
				ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
				feeChargeList.add(fee);
				tempFeeChargesMap.put(fee.getSchDate(), feeChargeList);
			}
		}

		//Repayment & Penalty Details on Maintainance
		Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
		Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
		if(!moduleDefiner.equals("")){

			aFinSchData = getFinanceDetailService().getFinMaintainenceDetails(aFinSchData);
			// Find Out Finance Repayment Details on Schedule
			if(aFinSchData.getRepayDetails() != null && aFinSchData.getRepayDetails().size() > 0){
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : aFinSchData.getRepayDetails()) {
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
			if(aFinSchData.getPenaltyDetails() != null && aFinSchData.getPenaltyDetails().size() > 0){
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				for (OverdueChargeRecovery penaltyDetail : aFinSchData.getPenaltyDetails()) {
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
		}

		int deferrmentCnt = 0;
		this.btnRmvDefferment.setDisabled(true);
		this.btnRmvDefferment.setVisible(false);
		finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinSchData.getFinanceScheduleDetails().size();
		boolean showTakafulTab = false;
		if (aFinSchData != null && sdSize > 0) {
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();
			this.listBoxTakafulSchedule.getItems().clear();
			this.btnPrintSchedule.setDisabled(false);
			this.btnPrintSchedule.setVisible(true);
			
			this.btnPrintTakafulSchedule.setDisabled(false);
			this.btnPrintTakafulSchedule.setVisible(true);

			boolean allowRvwRate = getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate");

			//Terms Rest On Screen, in Maintenance & and calculation by Maturity Date
			int totGrcTerms = 0;
			int totRepayTerms = 0;
			Date grcEndDate = getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();

			for (int i = 0; i < sdSize; i++) {
				boolean showRate = false;
				FinanceScheduleDetail aScheduleDetail = aFinSchData.getFinanceScheduleDetails().get(i);

				if(i != 0){
					if(aScheduleDetail.getSchDate().compareTo(grcEndDate) <= 0){
						if(aScheduleDetail.isPftOnSchDate()){
							totGrcTerms = totGrcTerms + 1;
						}
					}else{
						if(aScheduleDetail.isRepayOnSchDate() || aScheduleDetail.isDeferedPay() ||
								(aScheduleDetail.isPftOnSchDate() && aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)){
							totRepayTerms = totRepayTerms + 1;
						}else if(aFinSchData.getFinanceMain().isFinRepayPftOnFrq() && 
								(aScheduleDetail.isRepayOnSchDate() || aScheduleDetail.isPftOnSchDate())){
							totRepayTerms = totRepayTerms + 1;
						}
					}
				}

				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = aFinSchData.getFinanceScheduleDetails().get(i - 1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}

				if (aScheduleDetail.isRepayOnSchDate() ||
						(aScheduleDetail.isPftOnSchDate() && aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					if ((aScheduleDetail.getSpecifier().equals(CalculationConstants.GRACE) && 
							aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))
							|| (!aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))) {
						this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay"));
						this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay"));
					}
				}

				if (aScheduleDetail.isRvwOnSchDate()) {
					this.btnAddReviewRate.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate"));
					this.btnAddReviewRate.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate"));
				}

				if (aFinSchData.getFinanceType().isFinIsAlwMD()) {
					if ((aScheduleDetail.getSpecifier().equals(CalculationConstants.GRACE) && 
							aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))
							|| (!aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))) {
						this.btnAddDisbursement.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb"));
						this.btnAddDisbursement.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb"));
					}
				}else{
					this.btnAddDisbursement.setVisible(false);
				}

				if (aFinSchData.getFinanceType().isFinIsAlwDifferment()) {
					if (aFinSchData.getFinanceMain().getDefferments() > 0) {
						this.btnAddDefferment.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment"));
						this.btnAddDefferment.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment"));
					}
				}else{
					this.btnAddDefferment.setVisible(false);
				}

				if (aScheduleDetail.isDefered()) {
					deferrmentCnt = deferrmentCnt + 1;
				}

				if (aFinSchData.getFinanceMain().getDefferments() > 0) {
					if (deferrmentCnt >= aFinSchData.getFinanceMain().getDefferments()) {
						this.btnAddDefferment.setDisabled(true);
						this.btnAddDefferment.setVisible(false);
					}
				}else{
					this.btnAddDefferment.setVisible(false);
				}

				if (aFinSchData.getDefermentHeaders().size() > 0) {
					this.btnRmvDefferment.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment"));
					this.btnRmvDefferment.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment"));
				}else{
					this.btnRmvDefferment.setVisible(false);
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinSchData);
				if (aFinSchData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", aFinSchData.getDefermentMap().get(aScheduleDetail.getSchDate()));
				} else {
					map.put("defermentDetail", null);
				}

				map.put("financeScheduleDetail", aScheduleDetail);
				if(!moduleDefiner.equals("")){
					map.put("paymentDetailsMap", rpyDetailsMap);
					map.put("penaltyDetailsMap", penaltyDetailsMap);
				}
				map.put("window", this.window_ScheduleDetailDialog);

				finRender.render( map, prvSchDetail, false, allowRvwRate, true, tempFeeChargesMap, showRate, moduleDefiner.equals(""));
				
				// Takaful premium Schedule
				if(financeMain.isTakafulRequired() && aScheduleDetail.getTakafulFeeSchd().compareTo(BigDecimal.ZERO) > 0){
					finRender.doFillTakafulSchedule(this.listBoxTakafulSchedule, aFinSchData.getFinanceMain().getTakafulRate(), 
							aScheduleDetail, aFinSchData.getFinanceMain().getLovDescFinFormatter(), aFinSchData.getFinanceType().getFinCategory(),
							financeMain.getFinAmount().add(financeMain.getFeeChargeAmt()).subtract(financeMain.getDownPayment()));
					showTakafulTab = true;
				}

				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, allowRvwRate, true, tempFeeChargesMap, showRate, moduleDefiner.equals(""));
					break;
				}
			}

			//##########################################################################################
			//Reset Schedule terms on Main Controller and Remove If CalTerms & NumberOf terms Differentiated
			//##########################################################################################

			if (getFinanceMainDialogCtrl() != null) {
				try {

					this.schdl_noOfTerms.setValue(String.valueOf(totGrcTerms + totRepayTerms));

					@SuppressWarnings("rawtypes")
					Class[] paramType = {FinScheduleData.class , Integer.class, Integer.class};
					Object[] stringParameter = {aFinSchData, totGrcTerms, totRepayTerms};
					if (financeMainDialogCtrl.getClass().getMethod("resetScheduleTerms", paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("resetScheduleTerms", paramType).invoke(financeMainDialogCtrl, stringParameter);
					}

				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		
		if(!StringUtils.trimToEmpty(moduleDefiner).equals("")){
			hideButtons();
		}
		
		this.financeTakafulSchdDetailsTab.setVisible(showTakafulTab); 
		logger.debug("Leaving");
	}
	
	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		List<Object> list = new ArrayList<Object>();
		FinScheduleListItemRenderer finRender;
		if (getFinScheduleData() != null) {
			
			//Fee Charges List Render For First Disbursement only/Existing
			List<FeeRule> feeRuleList = getFinScheduleData().getFeeRules();
			FinanceMain financeMain = getFinScheduleData().getFinanceMain();
			
			//Get Finance Fee Details For Schedule Render Purpose In maintenance Stage
			List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
			if(!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF){
				approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinReference(), isWIF);
			}
			approvedFeeRules.addAll(feeRuleList);
			
			Map<Date, ArrayList<FeeRule>> feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
			for (FeeRule fee : approvedFeeRules) {
				if(feeChargesMap.containsKey(fee.getSchDate())){
					ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
					int seqNo = 0;
					for (FeeRule feeRule : feeChargeList) {
						if(feeRule.getFeeCode().equals(fee.getFeeCode())){
							if(seqNo < feeRule.getSeqNo() && fee.getSchDate().compareTo(feeRule.getSchDate()) == 0){
								seqNo = feeRule.getSeqNo();
							}
						}
					}
					fee.setSeqNo(seqNo+1);
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);
					
				}else{
					ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);
				}
			}
			
			finRender = new FinScheduleListItemRenderer();
			List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(getFinScheduleData());
			list.add(subList1);
			List<FinanceScheduleReportData> subList = finRender.getScheduleData(getFinScheduleData(),null,null, feeChargesMap,true);
			list.add(subList);
			//To get Parent Window i.e Finance main based on product
			//TODO Give better solution
			Window window= (Window) this.window_ScheduleDetailDialog.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
			
			String reportName = "FINENQ_ScheduleDetail";
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinType().startsWith("CONV")){
				reportName = "CFINENQ_ScheduleDetail";
			}
			
			if(isWIF){
				reportName = "WIFENQ_ScheduleDetail";
				
				int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(),  financeMain.getFinStartDate(),true);
				
				financeMain.setLovDescTenorName((months/12)+" Years "+(months%12) +" Months / "+
						(financeMain.getNumberOfTerms()+ financeMain.getGraceTerms()) +" Payments");
			}
			
			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
			String usrName = (securityUser.getUsrFName().trim() +" "+securityUser.getUsrMName().trim()+" "+securityUser.getUsrLName()).trim();
			
			ReportGenerationUtil.generateReport(reportName, financeMain, list, true, 1,
					usrName, window);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method to hide buttons in schedule details tab
	 **/
	private void hideButtons() {
		logger.debug("Entering");
		
		String dialogName = "FinanceMainDialog";
		if(isWIF){
			dialogName = "WIFFinanceMainDialog";
		}
		
		this.btnReCalcualte.setVisible(false);
		this.btnAddReviewRate.setVisible(false);
		this.btnChangeRepay.setVisible(false);
		this.btnAddDisbursement.setVisible(false);
		this.btnAddDefferment.setVisible(false);
		this.btnRmvDefferment.setVisible(false);
		this.btnAddTerms.setVisible(false);
		this.btnRmvTerms.setVisible(false);
		this.btnSubSchedule.setVisible(false);
		this.btnChangeProfit.setVisible(false);
		this.btnChangeFrq.setVisible(false);
		this.btnReschedule.setVisible(false);

		this.btnAddReviewRate.setDisabled(true);
		this.btnChangeRepay.setDisabled(true);
		this.btnAddDisbursement.setDisabled(true);
		this.btnAddDefferment.setDisabled(true);
		this.btnRmvDefferment.setDisabled(true);
		this.btnAddTerms.setDisabled(true);
		this.btnRmvTerms.setDisabled(true);
		this.btnReCalcualte.setDisabled(true);
		this.btnSubSchedule.setDisabled(true);
		this.btnChangeProfit.setDisabled(true);
		this.btnChangeFrq.setDisabled(true);
		this.btnReschedule.setDisabled(true);
		
		if (moduleDefiner.equals(PennantConstants.ADD_RATE_CHG)) {
			this.btnAddReviewRate.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate"));
			this.btnAddReviewRate.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate"));

		} else if (moduleDefiner.equals(PennantConstants.CHG_REPAY)) {
			this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay"));
			this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay"));

		} else if (moduleDefiner.equals(PennantConstants.ADD_DISB)) {
			this.btnAddDisbursement.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb"));
			this.btnAddDisbursement.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb"));

		} else if (moduleDefiner.equals(PennantConstants.ADD_DEFF)) {
			this.btnAddDefferment.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment"));
			this.btnAddDefferment.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment"));

		} else if (moduleDefiner.equals(PennantConstants.RMV_DEFF)) {
			this.btnRmvDefferment.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment"));
			this.btnRmvDefferment.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment"));

		} else if (moduleDefiner.equals(PennantConstants.ADD_TERMS)) {
			this.btnAddTerms.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddTerms"));
			this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));
			this.btnAddTerms.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddTerms"));
			this.btnReCalcualte.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));

		} else if (moduleDefiner.equals(PennantConstants.RMV_TERMS)) {
			this.btnRmvTerms.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvTerms"));
			this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));
			this.btnRmvTerms.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvTerms"));
			this.btnReCalcualte.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));

		} else if (moduleDefiner.equals(PennantConstants.RECALC)) {
			this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));
			this.btnReCalcualte.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));

		} else if (moduleDefiner.equals(PennantConstants.SUBSCH)) {
			this.btnSubSchedule.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnBuildSubSchd"));
			this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));
			this.btnSubSchedule.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnBuildSubSchd"));
			this.btnReCalcualte.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate"));

		} else if (moduleDefiner.equals(PennantConstants.CHGPFT)) {
			this.btnChangeProfit.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeProfit"));
			this.btnChangeProfit.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeProfit"));
			
		} else if (moduleDefiner.equals(PennantConstants.DATEDSCHD)) {
			this.btnAddDatedSchedule.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDatedSchd"));
			this.btnAddDatedSchedule.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDatedSchd"));
			
		}  else if (moduleDefiner.equals(PennantConstants.CHGFRQ)) {
			this.btnChangeFrq.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeFrq"));
			this.btnChangeFrq.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeFrq"));
			
		}  else if (moduleDefiner.equals(PennantConstants.RESCHD)) {
			this.btnReschedule.setVisible(getUserWorkspace().isAllowed("button_"+dialogName+"_btnReschedule"));
			this.btnReschedule.setDisabled(!getUserWorkspace().isAllowed("button_"+dialogName+"_btnReschedule"));
			
		} 
		logger.debug("Leaving");
	}
	

	/**
	 * Mehtod to capture event when review rate item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onReviewRateItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("reviewrate", true);
			map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
			map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul", window_ScheduleDetailDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to capture event when disbursement item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDisburseItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("disbursement", true);
			map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
			map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
			map.put("isWIF",isWIF);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul", window_ScheduleDetailDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to capture event when repay item is double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRepayItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("repayment", true);
			map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
			map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul", window_ScheduleDetailDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddReviewRate" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddReviewRate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("reviewrate", true);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddRepay" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnChangeRepay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("repayment", true);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button ChangeRepay button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnChangeProfit(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeProfitDialog.zul",window_ScheduleDetailDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		logger.debug("Leaving" + event.toString());
	}  

	/**
	 * when the button Change frequency button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnChangeFrq(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData",getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeFrequencyDialog.zul",window_ScheduleDetailDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		
		logger.debug("Leaving" + event.toString());
	}  
	
	/**
	 * when the button Re Scheduling button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReschedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData",getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ReScheduleDialog.zul",window_ScheduleDetailDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		
		logger.debug("Leaving" + event.toString());
	}  
	
	/**
	 * when the "AddDisbursement" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddDisbursement(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("disbursement", true);
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("isWIF", isWIF);
		
		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "AddDatedSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddDatedSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		
		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddDatedScheduleDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "AddDefferment" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddDefferment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addDeff", true);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddRmvDeffermentDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnRmvDefferment" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRmvDefferment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addDeff", false);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddRmvDeffermentDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnAddTerms" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddTerms(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addTerms", true);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnRmvTerms" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRmvTerms(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addTerms", false);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnSubSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSubSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData",getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/SubScheduleDialog.zul",window_ScheduleDetailDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnReCalcualte" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReCalcualte(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/RecalculateDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method to open child window based on selected menu item
	 * 
	 * */
	private void doOpenChildWindow() {
		logger.debug("Entering");
		
		String dialogName = "FinanceMainDialog";
		if(isWIF){
			dialogName = "WIFFinanceMainDialog";
		}

		if (moduleDefiner.equals(PennantConstants.ADD_RATE_CHG)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddRvwRate")){
				Events.postEvent("onClick$btnAddReviewRate", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.CHG_REPAY)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeRepay")){
				Events.postEvent("onClick$btnChangeRepay", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.ADD_DISB)) {
			if(getFinScheduleData().getFinanceType().isFinIsAlwMD() && 
					getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDisb")){
				Events.postEvent("onClick$btnAddDisbursement", this.window_ScheduleDetailDialog, null);
			}else{
				this.btnAddDisbursement.setVisible(false);
			}
		} else if (moduleDefiner.equals(PennantConstants.ADD_DEFF)) {
			if (getFinScheduleData().getFinanceType().isFinIsAlwDifferment() && 
					getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDeferment")) {
				Events.postEvent("onClick$btnAddDefferment", this.window_ScheduleDetailDialog, null);
			}else{
				this.btnAddDefferment.setVisible(false);
			}
		} else if (moduleDefiner.equals(PennantConstants.RMV_DEFF)) {
			if ((getFinScheduleData().getDefermentHeaders().size() > 0 && 
					getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvDeferment")) &&
					!defMethod.equals(PennantConstants.DEF_METHOD_RECALRATE)) {
				Events.postEvent("onClick$btnRmvDefferment", this.window_ScheduleDetailDialog, null);
			}else{
				this.btnRmvDefferment.setVisible(false);
			}
		} else if (moduleDefiner.equals(PennantConstants.ADD_TERMS)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddTerms")){
				Events.postEvent("onClick$btnAddTerms", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.RMV_TERMS)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRmvTerms")){
				Events.postEvent("onClick$btnRmvTerms", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.RECALC)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnRecalculate")){
				Events.postEvent("onClick$btnReCalcualte", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.SUBSCH)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnBuildSubSchd")){
				Events.postEvent("onClick$btnSubSchedule", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.CHGPFT)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeProfit")){
				Events.postEvent("onClick$btnChangeProfit", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.DATEDSCHD)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnAddDatedSchd")){
				Events.postEvent("onClick$btnAddDatedSchedule", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.CHGFRQ)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnChangeFrq")){
				Events.postEvent("onClick$btnChangeFrq", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(PennantConstants.RESCHD)) {
			if(getUserWorkspace().isAllowed("button_"+dialogName+"_btnReschedule")){
				Events.postEvent("onClick$btnReschedule", this.window_ScheduleDetailDialog, null);
			}
		} 
		logger.debug("Leaving");
	}
	
	/**
	 * when the "Linked Reference " Label is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$label_ScheduleDetailDialog_DPScheduleLink(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try{
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DPScheduleDetailDialog.zul", window_ScheduleDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public Map<Date, ArrayList<FeeRule>> getFeeChargesMap() {
		return feeChargesMap;
	}
	public void setFeeChargesMap(Map<Date, ArrayList<FeeRule>> feeChargesMap) {
		this.feeChargesMap = feeChargesMap;
	}

	public FeeDetailDialogCtrl getFeeDetailDialogCtrl() {
		return feeDetailDialogCtrl;
	}
	public void setFeeDetailDialogCtrl(FeeDetailDialogCtrl feeDetailDialogCtrl) {
		this.feeDetailDialogCtrl = feeDetailDialogCtrl;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
}
