/**
 * Copyright 2011 - naltinnant Technologies
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
 * FileName    		:  FinScheduleListItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-01-2011    														*
 *                                                                  						*
 * Modified Date    :  13-01-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-01-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class FinScheduleListItemRenderer implements Serializable{

	private static final long serialVersionUID = 598041940390030115L;
	private final static Logger logger = Logger.getLogger(FinScheduleListItemRenderer.class);

	protected FinScheduleData finScheduleData;
	protected FinanceScheduleDetail financeScheduleDetail;
	protected List<DefermentDetail> defermentDetail;
	protected static  Window  window;
	private Map<Date,ArrayList<FinanceRepayments>> repayDetailsMap;
	private List<FinanceRepayments> financeRepayments;
	private List<OverdueChargeRecovery> penalties;
	
	private Map<Date,ArrayList<OverdueChargeRecovery>> penaltyDetailsMap;
	private BigDecimal accrueValue;
	private boolean showStepDetail;
	
	private transient BigDecimal 	closingBal = null;
	protected boolean 	lastRec;
	protected Listitem 	listitem;
	protected Listbox 	listBoxSchedule;
	protected Button 	btnAddReviewRate;
	protected Button 	btnChangeRepay;
	protected Button 	btnAddDisbursement;
	
	private boolean 	isSchdFee = false;

	/**
	 * Method to render the list items
	 * 
	 * @param FinanceScheduleDetail
	 *            (financeScheduleDetail)
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void render(HashMap map, FinanceScheduleDetail prvSchDetail, boolean lastRecord,
			boolean allowRvwRateEdit,boolean isRepayEnquiry ,
			Map<Date, ArrayList<FeeRule>> feeRuleDetailsMap, boolean showRate, boolean displayStepInfo) {
		logger.debug("Entering");
		lastRec = lastRecord;

		// READ OVERHANDED parameters !
		if (map.containsKey("finSchdData")) {
			setFinScheduleData((FinScheduleData) map.get("finSchdData"));
		}

		if (map.containsKey("financeScheduleDetail")) {
			setFinanceScheduleDetail((FinanceScheduleDetail) map.get("financeScheduleDetail"));
		}
		
		if (map.containsKey("defermentDetail")) {
			setDefermentDetail((List<DefermentDetail>) map.get("defermentDetail"));
		}
		
		if (map.containsKey("window")) {
			window = (Window) map.get("window");
		}
		
		if (map.containsKey("paymentDetailsMap")) {
			repayDetailsMap = (Map<Date, ArrayList<FinanceRepayments>>) map.get("paymentDetailsMap");
		}
		
		if (map.containsKey("penaltyDetailsMap")) {
			penaltyDetailsMap = (Map<Date, ArrayList<OverdueChargeRecovery>>) map.get("penaltyDetailsMap");
		}
		
		if (map.containsKey("accrueValue")) {
			accrueValue = (BigDecimal) map.get("accrueValue");
		}

		this.listBoxSchedule = (Listbox)window.getFellowIfAny("listBoxSchedule");
		if((Button) window.getFellowIfAny("btnAddReviewRate") != null) {
			this.btnAddReviewRate = (Button) window.getFellowIfAny("btnAddReviewRate");
		}
		if((Button) window.getFellowIfAny("btnChangeRepay") != null) {
			this.btnChangeRepay = (Button) window.getFellowIfAny("btnChangeRepay");
		}
		if((Button) window.getFellowIfAny("btnAddDisbursement") != null) {
			this.btnAddDisbursement =(Button) window.getFellowIfAny("btnAddDisbursement");
		}
		
		if(!getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals(PennantConstants.List_Select) && 
				!getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals("") && 
				!getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
			isSchdFee = true;
		}
		
		showStepDetail = displayStepInfo;
		FinanceMain aFinanceMain = getFinScheduleData().getFinanceMain();
		int count = 1;
		closingBal = getFinanceScheduleDetail().getClosingBalance();
		boolean isEditable = false;
		boolean isRate = false;
		boolean showZeroEndBal = false;
		boolean isGrcBaseRate = false;
		boolean isRpyBaseRate = false;
		
		if(accrueValue != null){

			Date lastAccrueDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			if((!lastRec && lastAccrueDate.compareTo(prvSchDetail.getSchDate()) >= 0 && 
					lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) < 0)  || 
					(lastRec && lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) > 0)){
				count = 3;
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_AccrueAmount.label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, accrueValue,
						BigDecimal.ZERO, false, false,  true, false, false, "#1D883C", "",5, null,false);
				count = 1;
			}
		}

		if (lastRec) {
			
			isEditable = false;
			isRate = false;
			showZeroEndBal = false;
			isGrcBaseRate = false;
			isRpyBaseRate = false;
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalPftSch.label"),
					getFinScheduleData().getFinanceMain().getTotalProfit().subtract(getFinScheduleData().getFinanceMain().getTotalGracePft()),
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
			count = 3;
			
			int grcDays = 0;
			boolean showGrossPft = false;
			if(getFinScheduleData().getFinanceMain().isAllowGrcPeriod()){
				
				grcDays = DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
						getFinScheduleData().getFinanceMain().getGrcPeriodEndDate());
				if(grcDays > 0){
					showGrossPft = true;
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalGrcPftSch.label"),
							getFinScheduleData().getFinanceMain().getTotalGracePft(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,	BigDecimal.ZERO,
							BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
				}
			}
			if(getFinScheduleData().getFinanceMain().getTotalCpz().compareTo(BigDecimal.ZERO) != 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalCpz.label"),
						getFinScheduleData().getFinanceMain().getTotalCpz(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
			}
			
			if(showGrossPft){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalGrossPft.label", ""),
						getFinScheduleData().getFinanceMain().getTotalGrossPft(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
			}
			
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalRepayAmt.label", ""),
					getFinScheduleData().getFinanceMain().getTotalRepayAmt(),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
			
			if(getFinScheduleData().getFinanceMain().isAllowGrcPeriod()){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalGrcDays.label", ""),
						new BigDecimal(grcDays),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",15, null,false);
			}
			
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalDays.label", ""),
					new BigDecimal(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
							getFinScheduleData().getFinanceMain().getMaturityDate())),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",15, null,false);

		} else {
			if (getFinanceScheduleDetail().isPftOnSchDate() && !(getFinanceScheduleDetail().isRepayOnSchDate() || getFinanceScheduleDetail().isDeferedPay() ||
				(getFinanceScheduleDetail().isPftOnSchDate() && getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
					&& !getFinanceScheduleDetail().isDisbOnSchDate()) {
				// if rate change allowed then set the record editable.
				if (getFinanceScheduleDetail().isRvwOnSchDate() && 
					getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = false;
				showZeroEndBal = true;
				isGrcBaseRate = false;
				isRpyBaseRate = false;
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_profitCalc.label"),
						getFinanceScheduleDetail().getProfitCalc(),getFinanceScheduleDetail().getFeeSchd(), getFinanceScheduleDetail().getProfitSchd(), 
						getFinanceScheduleDetail().getPrincipalSchd(), getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()), 
						getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate, showZeroEndBal,
						isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
				count = 2;
				if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowRvwRateEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}
			}
			
			if (getFinanceScheduleDetail().isDisbOnSchDate()) {
				isEditable = true;
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_disbursement.label"),
						getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, getFinanceScheduleDetail().getDisbAmount(),
						getFinanceScheduleDetail().getClosingBalance().subtract(
								getFinanceScheduleDetail().getFeeChargeAmt() == null ? BigDecimal.ZERO : getFinanceScheduleDetail().getFeeChargeAmt())
								.add(getFinanceScheduleDetail().getDownPaymentAmount()),
						isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,false);
				
				count = 2;
				
				if (getFinanceScheduleDetail().isDownpaymentOnSchDate()) {
					isEditable = false;
					isRate = false;
					showZeroEndBal = false;
					isGrcBaseRate = false;
					isRpyBaseRate = false;
					
					BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();
					
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_downPayment.label"),
							BigDecimal.ZERO, BigDecimal.ZERO , BigDecimal.ZERO, BigDecimal.ZERO,
							getFinanceScheduleDetail().getDownPaymentAmount(), 
							getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt), isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);
				}
				
				if(feeRuleDetailsMap != null && getFinanceScheduleDetail().getFeeChargeAmt() != null &&
						 getFinanceScheduleDetail().getFeeChargeAmt().compareTo(BigDecimal.ZERO) >= 0 &&
						 getFinScheduleData().getFinanceMain().getRemFeeSchdMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
					
					if(feeRuleDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())){
						List<FeeRule> feeRuleList = new ArrayList<FeeRule>(feeRuleDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
						feeRuleList = sortFeeRules(feeRuleList);

						BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

						for (FeeRule feeRule : feeRuleList) {
							
							BigDecimal actFeeCharge = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());

							if(feeRule != null && feeRule.isAddFeeCharges() && actFeeCharge.compareTo(BigDecimal.ZERO) >= 0){

								doFillListBox(getFinanceScheduleDetail(), count, feeRule.getFeeCodeDesc(),
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, actFeeCharge,
										getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt).add(actFeeCharge),
										false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,true);

								feeChargeAmt = feeChargeAmt.subtract(actFeeCharge);
							}
						}
					}
				}
				
 				//Event Description Details
				if (getFinScheduleData().getFinanceType().getLovDescProductCodeName().equals(PennantConstants.FINANCE_PRODUCT_ISTISNA)) {
					if (getFinScheduleData().getDisbursementDetails()!=null) {
						for (FinanceDisbursement disbursement : getFinScheduleData().getDisbursementDetails()) {
							if (getFinanceScheduleDetail().getSchDate().compareTo(disbursement.getDisbDate()) == 0) {
								String remarks = "";
								if (StringUtils.trimToEmpty(disbursement.getDisbType()).equals("B")) {
									remarks = remarks + " Billing " + " - ";
								} else if (StringUtils.trimToEmpty(disbursement.getDisbType()).equals("A")) {
									remarks = remarks + " Advance " + " - ";
								} else if (StringUtils.trimToEmpty(disbursement.getDisbType()).equals("E")) {
									remarks = remarks + " Expense " + " - ";
								} else if (StringUtils.trimToEmpty(disbursement.getDisbType()).equals("C")) {
									remarks = remarks + " Consultancy Fee " + " - ";
								}
								if (StringUtils.trimToEmpty(disbursement.getDisbType()).equals("B")) {
									remarks =remarks+ disbursement.getDisbRemarks() + " " + PennantAppUtil.amountFormate(disbursement.getDisbClaim(),getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
								} else {
									remarks =remarks+ disbursement.getDisbRemarks() + " " + PennantAppUtil.amountFormate(disbursement.getDisbAmount(),getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
								}
								doFillListBox(getFinanceScheduleDetail(), 2, remarks, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
										BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 2, null,false);
							}
							
						}
					}
				}
				
				if (this.btnAddDisbursement != null && this.btnAddDisbursement.isVisible() && 
						getFinScheduleData().getFinanceType().isFinIsAlwMD()) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisburseItemDoubleClicked");
				}

			}
			
			if (getFinanceScheduleDetail().isRepayOnSchDate() || getFinanceScheduleDetail().isDeferedPay() ||
					(getFinanceScheduleDetail().isPftOnSchDate() && getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				isRate = false;
				showZeroEndBal = true;
				isGrcBaseRate = false;
				isRpyBaseRate = false;
				if (getFinanceScheduleDetail().isDefered()) {
					
					//TODO -- display allow for calculated profit on Deferred Schedule
					if (getFinanceScheduleDetail().isRvwOnSchDate() && 
							getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {
						isEditable = true;
					} else {
						isEditable = false;
					}
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_profitCalc.label"),
							getFinanceScheduleDetail().getProfitCalc(),getFinanceScheduleDetail().getFeeSchd(), getFinanceScheduleDetail().getProfitSchd(), 
							getFinanceScheduleDetail().getPrincipalSchd(), getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()), 
							getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate, showZeroEndBal,
							isGrcBaseRate, isRpyBaseRate, "","",0, null,false);

					isEditable = true;
					count = 2;
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_defferment.label"),
							BigDecimal.ZERO,BigDecimal.ZERO, //TODO- check on Deferment case for Fee Scheduled Amount
							getFinanceScheduleDetail().getDefProfit(), getFinanceScheduleDetail().getDefPrincipal(),
							getFinanceScheduleDetail().getDefProfit().add(getFinanceScheduleDetail().getDefPrincipal()),
							BigDecimal.ZERO, false,
							isRate,  showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#FF0000","color_Deferred",0, null,false);
					
					if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate()
							&& getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0
							&& allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}
				} else if(!(getFinanceScheduleDetail().getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0)) {
					String colorClass = "";
					
					String label = Labels.getLabel("listcell_repay.label");
					if(getFinScheduleData().getFinanceType().getFinCategory().equals(PennantConstants.FINANCE_PRODUCT_SUKUK)
							|| getFinScheduleData().getFinanceType().getFinCategory().equals(PennantConstants.FINANCE_PRODUCT_SUKUKNRM)){
						label = Labels.getLabel("listcell_couponrepay.label");
					}
					
					isEditable = true;
					doFillListBox(getFinanceScheduleDetail(), count, label,	getFinanceScheduleDetail().getProfitCalc(),getFinanceScheduleDetail().getFeeSchd(),
							getFinanceScheduleDetail().getProfitSchd(), getFinanceScheduleDetail().getPrincipalSchd(),
							getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()), getFinanceScheduleDetail().getClosingBalance(),
							isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", colorClass,0, null,false);
					count = 2;
					if (getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().getMaturityDate()) != 0 
							&& this.btnChangeRepay != null && this.btnChangeRepay.isVisible()) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onRepayItemDoubleClicked");
					}
				}
			}
			
			if (getFinanceScheduleDetail().isCpzOnSchDate() && 
					getFinanceScheduleDetail().getCpzAmount().compareTo(BigDecimal.ZERO) != 0) {
				// if rate change allowed then set the record editable.
				if (getFinanceScheduleDetail().isRvwOnSchDate() && 
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_capital.label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getCpzAmount(),
						BigDecimal.ZERO, getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",0, null,false);
				count = 2;
				if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowRvwRateEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}

			}
			
			//To show repayment details 
			if(isRepayEnquiry && repayDetailsMap != null && repayDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				setFinanceRepayments(repayDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
				for (int i = 0; i < getFinanceRepayments().size(); i++) {
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_AmountPaid.label", 
							new String[]{PennantAppUtil.formateDate(getFinanceRepayments().get(i).getFinPostDate(), PennantConstants.dateFormate)}),
							BigDecimal.ZERO,getFinanceScheduleDetail().getSchdFeePaid(),
							 getFinanceRepayments().get(i).getFinSchdPftPaid(),getFinanceRepayments().get(i).getFinSchdPriPaid(),
							 getFinanceRepayments().get(i).getFinSchdPftPaid().add(getFinanceRepayments().get(i).getFinSchdPriPaid()).add(getFinanceScheduleDetail().getSchdFeePaid()),
							BigDecimal.ZERO, false, isRate,  showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#330066", "color_Repayment",0, null,false);
					count = 2;
				}
			}
			
			//To show Penalty details 
			if(isRepayEnquiry && penaltyDetailsMap != null && penaltyDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				List<OverdueChargeRecovery> recoverys = penaltyDetailsMap.get(getFinanceScheduleDetail().getSchDate());
				for (int i = 0; i < recoverys.size(); i++) {
					if(recoverys.get(i).getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_PenaltyPaid.label", 
								new String[]{PennantAppUtil.formateDate(recoverys.get(i).getMovementDate(), PennantConstants.dateFormate)}),
								BigDecimal.ZERO,BigDecimal.ZERO , BigDecimal.ZERO,BigDecimal.ZERO, recoverys.get(i).getPenaltyPaid(),
								BigDecimal.ZERO, false, isRate,  false, isGrcBaseRate, isRpyBaseRate, "#FF0000", "color_RepaymentOverdue",0, null,false);
					}
					count = 2;
				}
				recoverys = null;
			}
			
			//WriteOff Details 
			if(getFinanceScheduleDetail().getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0 ||
					getFinanceScheduleDetail().getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_Writeoff.label"),
						BigDecimal.ZERO, BigDecimal.ZERO , getFinanceScheduleDetail().getWriteoffProfit(),getFinanceScheduleDetail().getWriteoffPrincipal(), 
						getFinanceScheduleDetail().getWriteoffProfit().add(getFinanceScheduleDetail().getWriteoffPrincipal()),
						BigDecimal.ZERO, false, false,  false, false, false, "#FF0000", "color_RepaymentOverdue",0, null,false);
				count = 2;
			}
			
			if(getDefermentDetail() != null) {
				
				for (DefermentDetail defDetail : getDefermentDetail()) {
					
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_deffermentPay.label", 
							new String[]{PennantAppUtil.formateDate(defDetail.getDeferedSchdDate(), PennantConstants.dateFormate)}),BigDecimal.ZERO, BigDecimal.ZERO,
							defDetail.getDefRpySchdPft(), defDetail.getDefRpySchdPri(),defDetail.getDefRpySchdPft().add(defDetail.getDefRpySchdPri()),
							BigDecimal.ZERO, false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#347C17", "color_Repayment",0, null,false);
					
				}
				
				count = 2;
			}
			
			BigDecimal totalPaid = getFinanceScheduleDetail().getSchdPftPaid().add(getFinanceScheduleDetail().getSchdPriPaid()).add(
					getFinanceScheduleDetail().getDefSchdPftPaid()).add(getFinanceScheduleDetail().getDefSchdPriPaid()).add(getFinanceScheduleDetail().getSchdFeePaid());
			BigDecimal totalSchd = getFinanceScheduleDetail().getProfitSchd().add(getFinanceScheduleDetail().getPrincipalSchd()).add(
					getFinanceScheduleDetail().getDefPrincipalSchd()).add(getFinanceScheduleDetail().getDefProfitSchd()).add(getFinanceScheduleDetail().getFeeSchd());
			
			if(totalPaid.compareTo(BigDecimal.ZERO) > 0 && totalSchd.compareTo(totalPaid) > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_UnpaidAmount.label"),BigDecimal.ZERO,BigDecimal.ZERO,
						getFinanceScheduleDetail().getProfitSchd().subtract(getFinanceScheduleDetail().getSchdPftPaid()), 
						getFinanceScheduleDetail().getPrincipalSchd().subtract(getFinanceScheduleDetail().getSchdPriPaid()),
						totalSchd.subtract(totalPaid), BigDecimal.ZERO,
						false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#056DA1", "",0, null,false);
				
			}

			if (getFinanceScheduleDetail().isRvwOnSchDate() || showRate) {
				if(getFinanceScheduleDetail().isRvwOnSchDate()){ 
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = true;
				showZeroEndBal = false;
				if (aFinanceMain.getGraceBaseRate() != null && getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE)) {
					isGrcBaseRate = true;
				}
				if (aFinanceMain.getRepayBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if (aFinanceMain.getMaturityDate().compareTo(getFinanceScheduleDetail().getSchDate()) != 0) {
					if (getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {
						// nothing to do
					}else if(getFinanceScheduleDetail().isCalOnIndRate()){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewIndRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false);
					
						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_IndRateAdded_label", 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",2, null,false);
						
						count = 2;
						if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}
					}else {
						
						String flatRateConvert = "listcell_flatRateChangeAdded_label";
						if(CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())){
							
							doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_flatRate.label"),
									getFinanceScheduleDetail().getActRate(),BigDecimal.ZERO,BigDecimal.ZERO, 
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, isRate,
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false);
							
							//Event Description Details
							doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
									new String[]{String.valueOf(PennantApplicationUtil.formatRate(prvSchDetail.getActRate().doubleValue(),PennantConstants.rateFormate)),
									String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getActRate().doubleValue(),PennantConstants.rateFormate))}),
									BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
									false, false, false, false, "","",2, null,false);
							flatRateConvert = "listcell_flatRateConvertChangeAdded_label";
						}
						
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false);
						
						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(prvSchDetail.getCalculatedRate().doubleValue(),PennantConstants.rateFormate)),
								String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",2, null,false);
						
						count = 2;
						if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}
					}
				}
			}

			if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
				isEditable = true;
				isRate = true;
				showZeroEndBal = false;
				if (aFinanceMain.getGrcPeriodEndDate().compareTo(aFinanceMain.getFinStartDate()) != 0 && aFinanceMain.getGraceBaseRate() != null) {
					isGrcBaseRate = true;
				}
				if (aFinanceMain.getRepayBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if(getFinanceScheduleDetail().isCalOnIndRate()){
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewIndRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate",0, null,false);
					
					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_IndRateAdded_label", 
							new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
							BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
							false, false, false, false, "","",2, null,false);
					
					count = 2;
					if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}
				}else {
					
					String flatRateConvert = "listcell_flatRateAdded_label";
					BigDecimal rate = getFinanceScheduleDetail().getCalculatedRate();
					if(CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_flatRate.label"),
								getFinanceScheduleDetail().getActRate(), BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false);
						
						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getActRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",2, null,false);
						
						flatRateConvert = "listcell_flatRateConvertAdded_label";
						rate = getFinanceScheduleDetail().getActRate();
					}
					
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false);
					
					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
							new String[]{String.valueOf(PennantApplicationUtil.formatRate(rate.doubleValue(),PennantConstants.rateFormate)), 
							String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
							BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
							false, false, false, false, "","",2, null,false);
					
					count = 2;
					if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() &&  allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}
				}
			}
			
			//Early Paid Schedule Details
			if(getFinanceScheduleDetail().getEarlyPaid().compareTo(BigDecimal.ZERO) > 0 ){
				
				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_EarlyPaidDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(getFinanceScheduleDetail().getEarlyPaid(),aFinanceMain.getLovDescFinFormatter()),
						PennantAppUtil.amountFormate(getFinanceScheduleDetail().getEarlyPaidBal(),aFinanceMain.getLovDescFinFormatter())}),
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null,false);
				
			}else if(getFinanceScheduleDetail().getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0 ){
				
				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_EarlyPayBalDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(
								getFinanceScheduleDetail().getEarlyPaidBal(),aFinanceMain.getLovDescFinFormatter())}),
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null,false);
			}
		}
		
		logger.debug("Leaving");
	}

	
	/**
	 * Method to fill schedule data in listitem
	 * @param data (FinanceSchdeuleDetail)
	 * @param count (int)
	 * @param eventName (String)
	 * @param pftAmount (BigDecimal)
	 * @param schdlPft (BigDecimal)
	 * @param cpzAmount (BigDecimal)
	 * @param totalAmount (BigDecimal)
	 * @param endBal (BigDecimal)
	 * @param isEditable  (boolean)
	 * @param isRate (boolean)
	 * @param showZeroEndBal (boolean)
	 * @param isGrcBaseRate (boolean)
	 * @param isRpyBaseRate (boolean)
	 * @param bgColor (String)
	 * @param lcColor (String)
	 * @param fillType (int) 1-Days, 2-Description Line
	 */
	public void doFillListBox(FinanceScheduleDetail data, int count, String eventName, BigDecimal pftAmount,BigDecimal feeAmount,
			BigDecimal schdlPft, BigDecimal cpzAmount, BigDecimal totalAmount,
			BigDecimal endBal, boolean isEditable, boolean isRate,  boolean showZeroEndBal, 
			boolean isGrcBaseRate, boolean isRpyBaseRate, String bgColor, String lcColor, int fillType, Date progClaimDate, boolean isFee) {
		logger.debug("Entering");
		listitem = new Listitem();
		Listcell lc = null;
		String strDate = "";
		String rate = "";
		if (count == 1 && !lastRec) {
			if (data.isRvwOnSchDate() || data.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
				strDate = PennantAppUtil.formateDate(data.getDefSchdDate(), PennantConstants.dateFormate) + " [R]";
			} else {
				strDate = PennantAppUtil.formateDate(data.getDefSchdDate(), PennantConstants.dateFormate);
			}
		} else if (count == 1 && lastRec) {
			strDate = Labels.getLabel("listcell_summary.label");
		}
		
		//Progress Claim Date for Billing ISTISNA
		if(fillType == 2 && progClaimDate != null){
			strDate = PennantAppUtil.formateDate(progClaimDate, PennantConstants.dateFormate);
		}
		
		//Color Cell
		lc = new Listcell();
		lc.setSclass(lcColor);
		//TODO need to add
		// #008000
		// #FF0000
		
		listitem.appendChild(lc);
		
		// Date listcell
		lc = new Listcell(strDate);
		lc.setStyle("font-weight:bold;");
		if(!isEditable) {
			lc.setStyle("font-weight:bold;cursor:default;");
		}
		listitem.appendChild(lc);

		// Label listcell
		lc = new Listcell(eventName);
		if(!isEditable) {
			lc.setStyle("cursor:default;");
		}
		
		if(fillType == 2){
		  if (getFinScheduleData().getFinanceMain().isStepFinance() && showStepDetail) {
			  if(isSchdFee){
				  lc.setSpan(10);
			  }else{
				lc.setSpan(9);
			  }
		  } else {
			  if(isSchdFee){
				  lc.setSpan(7);
			  }else{
				  lc.setSpan(6);
			  }
		  }
		}
		listitem.appendChild(lc);

		// Amounts array
		BigDecimal amountlist[] = { pftAmount, feeAmount, schdlPft, cpzAmount, totalAmount, endBal };
		
		if(fillType == 1){
			lc = new Listcell(String.valueOf(amountlist[0].intValue()));
			lc.setStyle("text-align:right;");
			listitem.appendChild(lc);
			lc = new Listcell();
			if(getFinScheduleData().getFinanceMain().isStepFinance() && showStepDetail) {
				if(isSchdFee){
					lc.setSpan(8);
				}else{
					lc.setSpan(7);
				}
			}  else {
				if(isSchdFee){
					lc.setSpan(5);
				}else{
					lc.setSpan(4);
				}
			}

			listitem.appendChild(lc);
		}else if(fillType == 2){
			//Nothing todo
		}else{
			// Append amount listcells to listitem
			for (int i = 0; i < amountlist.length; i++) {
				if (amountlist[i].compareTo(BigDecimal.ZERO) != 0) {
					if (isRate) { // Append % sysmbol if rate and format using rate format
						rate = PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate);
						String baseRate = data.getBaseRate();
						String splRate = StringUtils.trimToEmpty(data.getSplRate());
						String marginRate = data.getMrgRate().compareTo(BigDecimal.ZERO) == 0?"":data.getMrgRate().toString();
						if (isGrcBaseRate && (data.getSpecifier().equals(CalculationConstants.GRACE) ||
								data.getSpecifier().equals(CalculationConstants.GRACE_END))) {
							
							if(StringUtils.trimToEmpty(baseRate).equals("")){
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}else{
								lc = new Listcell("[ " +baseRate+""+(splRate.equals("")?"":","+splRate)+""+
										(marginRate.equals("")?"":","+marginRate)+" ]"+
										PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						} else if (isRpyBaseRate && (data.getSpecifier().equals(CalculationConstants.REPAY) || 
								data.getSpecifier().equals(CalculationConstants.GRACE_END))) {
							
							if(StringUtils.trimToEmpty(baseRate).equals("")){
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}else{
								lc = new Listcell("[ " +baseRate+""+(splRate.equals("")?"":","+splRate)+""+
										(marginRate.equals("")?"":","+marginRate)+" ]"+
										PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						} else {
							lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						}
					} else {
						if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && fillType == 15){
							lc = new Listcell("");
							lc.setStyle("text-align:right;"); 
						} else if(fillType == 15){
							lc = new Listcell(String.valueOf(amountlist[i].intValue()));
						} else {
							lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], 
									getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
						} if(!bgColor.equals("")) {
							lc.setStyle("text-align:right;font-weight: bold;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;font-weight: bold;color:"+bgColor+";cursor:default;");
							}
						} else {
							lc.setStyle("text-align:right;");
							if(!isEditable) {
								lc.setStyle("text-align:right;cursor:default;");
							}
						}
					}
				} else if (closingBal.compareTo(BigDecimal.ZERO) == 0 && i == amountlist.length - 1 &&
						!lastRec && showZeroEndBal) {
					lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], 
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 1 || i == 2 || i == 3 ) && showZeroEndBal) {
					if(fillType == 5){
						lc = new Listcell("");
					}else{
						lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], 
								getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					}
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && ( i == 4) && isFee) {
					lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], 
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					if(!bgColor.equals("")) {
						lc.setStyle("text-align:right;font-weight: bold;color:"+bgColor+";");
						if(!isEditable) {
							lc.setStyle("text-align:right;font-weight: bold;color:"+bgColor+";cursor:default;");
						}
					}else {
						lc.setStyle("text-align:right;");
						if(!isEditable) {
							lc.setStyle("text-align:right;cursor:default;");
						}
					}
				} else {
					lc = new Listcell("");
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
					
				}
				listitem.appendChild(lc);
			}
			
			// for Cash Flow Effect value
			if (getFinScheduleData().getFinanceMain().isStepFinance() && showStepDetail) {
				if (!isRate && !lastRec) {
					lc = new Listcell(PennantAppUtil.amountFormate(data.getOrgPft(), 
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);

				// for Vs Profit value
				if (!isRate && !lastRec) {
					lc = new Listcell(PennantAppUtil.amountFormate(data.getOrgPri(), 
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);

				// for Original Principal Due value
				if (!isRate && !lastRec) {
					lc = new Listcell(PennantAppUtil.amountFormate(data.getOrgEndBal(),
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);
			}

			// if the schedule specifier is grace end then don't display the toottip text
			if (isEditable && !lastRec) {
				if (isRate && this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled()) { 
					// Append rate to tooltip text without formating
					listitem.setTooltiptext(Labels.getLabel("listbox.ratechangetooltiptext") + "  " + rate);
				} else if (this.btnChangeRepay != null && !this.btnChangeRepay.isDisabled()) {
					listitem.setTooltiptext(Labels.getLabel("listbox.repayamounttooltiptext"));
				}
				listitem.setAttribute("data", data);
			}
		}
		// Append listitem to listbox
		this.listBoxSchedule.appendChild(listitem);
		logger.debug("Leaving");
	}
	
	public void doFillDPSchedule(Listbox listBoxSchedule, FinanceScheduleDetail scheduleDetail, int formatter){
		logger.debug("Entering");

		listitem = new Listitem();
		Listcell lc = new Listcell();
		lc.setSclass("");
		listitem.appendChild(lc);

		lc = new Listcell(PennantAppUtil.formateDate(scheduleDetail.getSchDate(), PennantConstants.dateFormate));
		lc.setStyle("font-weight:bold;cursor:default;");

		listitem.appendChild(lc);

		//Profit Schedule Amount
		lc = new Listcell(PennantAppUtil.amountFormate(scheduleDetail.getProfitSchd(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Schedule Principle Amount
		lc = new Listcell(PennantAppUtil.amountFormate(scheduleDetail.getPrincipalSchd(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		//Repay Installment Amount
		lc = new Listcell(PennantAppUtil.amountFormate(scheduleDetail.getRepayAmount(),formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Closing Balance Amount
		lc = new Listcell(PennantAppUtil.amountFormate(scheduleDetail.getClosingBalance(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);
		
		listBoxSchedule.appendChild(listitem);
		logger.debug("Leaving");

	}
	
	public void doFillTakafulSchedule(Listbox listBoxSchedule,BigDecimal rate, FinanceScheduleDetail scheduleDetail, 
			int formatter, String finCategory, BigDecimal actualFinAmount){
		logger.debug("Entering");

		listitem = new Listitem();
		Listcell lc = null;

		lc = new Listcell(PennantAppUtil.formateDate(scheduleDetail.getSchDate(), PennantConstants.dateFormate));
		lc.setStyle("font-weight:bold;cursor:default;");
		listitem.appendChild(lc);

		//Takaful Premium Rate
		lc = new Listcell(PennantApplicationUtil.formatRate(rate.doubleValue(), PennantConstants.rateFormate) + "%");
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		//Outstanding Balance
		if(finCategory.equals(PennantConstants.FINANCE_PRODUCT_MURABAHA)){
			lc = new Listcell(PennantAppUtil.amountFormate(actualFinAmount ,formatter));
		}else{
			lc = new Listcell(PennantAppUtil.amountFormate(scheduleDetail.getClosingBalance() ,formatter));
		}
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Takaful Premium Schedule Fee
		lc = new Listcell(PennantAppUtil.amountFormate(scheduleDetail.getTakafulFeeSchd(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);
		
		listBoxSchedule.appendChild(listitem);
		logger.debug("Leaving");

	}

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 * */
	public List<FinanceScheduleReportData> getScheduleData(FinScheduleData aFinScheduleData, 
			Map<Date,ArrayList<FinanceRepayments>> paymentDetailsMap,  Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap, 
			Map<Date,ArrayList<FeeRule>> feeRuleDetails, boolean includeSummary) {
		logger.debug("Entering");
		setFinScheduleData(aFinScheduleData);
		FinanceScheduleDetail prvSchDetail = null; 
		ArrayList<FinanceScheduleReportData> reportList = new ArrayList<FinanceScheduleReportData>();
		boolean lastRec=false;
		FinanceScheduleReportData data;
		BigDecimal totalPftAmount = BigDecimal.ZERO;
		BigDecimal totalGrcPftAmount = BigDecimal.ZERO;
		BigDecimal totalCpzAmount = BigDecimal.ZERO;
		BigDecimal totalGrossAmount = BigDecimal.ZERO;
		BigDecimal totalRepayAmount = BigDecimal.ZERO;
		int count = 1;
		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail aScheduleDetail = getFinScheduleData().getFinanceScheduleDetails().get(i);
			totalPftAmount = totalPftAmount.add(aScheduleDetail.getProfitSchd());
			if("G".equals(aScheduleDetail.getSpecifier()) || "E".equals(aScheduleDetail.getSpecifier())){
				totalGrcPftAmount = totalGrcPftAmount.add(aScheduleDetail.getProfitCalc());
			}
			totalCpzAmount = totalCpzAmount.add(aScheduleDetail.getCpzAmount());
			totalGrossAmount = totalPftAmount.add(totalCpzAmount);
			totalRepayAmount = totalRepayAmount.add(aScheduleDetail.getRepayAmount());
			closingBal = aScheduleDetail.getClosingBalance();
			if(aScheduleDetail.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && 
					i == aFinScheduleData.getFinanceScheduleDetails().size() - 1) {
				lastRec = true;
			}
			if (i == 0) {
				prvSchDetail = aScheduleDetail;
			} else {
				prvSchDetail = getFinScheduleData().getFinanceScheduleDetails().get(i - 1);
			}
			if (aScheduleDetail.isDisbOnSchDate()) {
				
				for (int j = 0; j < 2; j++) {

					if(j == 0){
						
						data = new FinanceScheduleReportData();	
						data.setLabel(Labels.getLabel("listcell_disbursement.label"));
						data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
						data.setSchdPft("");
						data.setSchdPri(formatAmt(aScheduleDetail.getCpzAmount(),false,false));
						data.setTotalAmount(formatAmt(aScheduleDetail.getDisbAmount(),false,false));
						data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance().subtract(
								aScheduleDetail.getFeeChargeAmt()==null? BigDecimal.ZERO :aScheduleDetail.getFeeChargeAmt())
								.add(aScheduleDetail.getDownPaymentAmount()),false,false));
						
						if (count == 1) {
							if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
								data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
										PennantConstants.dateFormate)+"[R]");
							}else {
								data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
										PennantConstants.dateFormate));
							}
							data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						}else{
							data.setSchDate("");
						}
						reportList.add(data);
						
						if (aScheduleDetail.isDownpaymentOnSchDate()) {
							
							BigDecimal feeChargeAmt = aScheduleDetail.getFeeChargeAmt();
							data = new FinanceScheduleReportData();
							data.setLabel(Labels.getLabel("listcell_downPayment.label"));
							if (count == 1){
								data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
								data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
										PennantConstants.dateFormate));
							}else{
								data.setSchDate("");
							}
							data.setPftAmount("");
							data.setSchdPft("");
							data.setSchdPri("");
							data.setTotalAmount(formatAmt(aScheduleDetail.getDownPaymentAmount(),false,false));
							data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance().subtract(feeChargeAmt),false,false));
							reportList.add(data);
						}		
						
					}
					if(j == 1){
						if(feeRuleDetails != null && aScheduleDetail.getFeeChargeAmt()!= null &&
								aScheduleDetail.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0 ){
							
							if(feeRuleDetails.containsKey(aScheduleDetail.getSchDate())){
							
							List<FeeRule> feeChargeList = sortFeeRules(feeRuleDetails.get(aScheduleDetail.getSchDate()));
							
							BigDecimal feeChargeAmt = aScheduleDetail.getFeeChargeAmt();
							for (FeeRule rule : feeChargeList) {
								
								if(rule.getFeeAmount().compareTo(BigDecimal.ZERO) >= 0){
									data = new FinanceScheduleReportData();	

									data.setLabel(rule.getFeeCodeDesc());
									data.setPftAmount("");
									data.setSchdPft("");
									data.setSchdPri("");
									data.setTotalAmount(formatAmt(rule.getFeeAmount().subtract(rule.getWaiverAmount()).subtract(rule.getPaidAmount()),false,true));
									data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance().subtract(feeChargeAmt)
											.add(rule.getFeeAmount().subtract(rule.getWaiverAmount()).subtract(rule.getPaidAmount())),false,false));
									data.setSchDate("");
									reportList.add(data);
									feeChargeAmt = feeChargeAmt.subtract(rule.getFeeAmount());
								}
							}
						}
						}else{
							continue;
						}					
					}
					
					count = 2;
				}
				
			}
			
			if (aScheduleDetail.isPftOnSchDate() && !(aScheduleDetail.isRepayOnSchDate() || aScheduleDetail.isDeferedPay() ||
					(aScheduleDetail.isPftOnSchDate() && aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
				if(aScheduleDetail.getProfitCalc().compareTo(BigDecimal.ZERO) > 0){
					data = new FinanceScheduleReportData();	

					data.setLabel(Labels.getLabel("listcell_profitCalc.label"));
					if (count == 1) {
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()) {
							data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
									PennantConstants.dateFormate)+"[R]");
						}else {
							data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(), PennantConstants.dateFormate));
						}
					}else {
						data.setSchDate("");
					}
					data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
					data.setSchdPft(formatAmt(aScheduleDetail.getProfitSchd(),false,true));
					data.setSchdPri(formatAmt(aScheduleDetail.getPrincipalSchd(),false,true));
					data.setTotalAmount(formatAmt(aScheduleDetail.getRepayAmount(),false,false));
					data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
					reportList.add(data);
				}
				count = 2;
			}
			if (aScheduleDetail.isCpzOnSchDate() && aScheduleDetail.getCpzAmount().compareTo(BigDecimal.ZERO) != 0) {
				data = new FinanceScheduleReportData();	
				data.setLabel(Labels.getLabel("listcell_capital.label"));
				if (count == 1){
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					if( aScheduleDetail.isRvwOnSchDate()){
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
								PennantConstants.dateFormate)+"[R]");
					}else {
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
								PennantConstants.dateFormate));
					}
				}else{
					data.setSchDate("");
				}
				data.setPftAmount("");
				data.setSchdPft("");
				data.setSchdPri(formatAmt(aScheduleDetail.getCpzAmount(),false,false));
				data.setTotalAmount("");				
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
				reportList.add(data);
				count = 2;
			}
			if (aScheduleDetail.isRepayOnSchDate() || aScheduleDetail.isDeferedPay() || 
					(aScheduleDetail.isPftOnSchDate() && aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				if(!(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0)) {
					data = new FinanceScheduleReportData();	
					
					if(getFinScheduleData().getFinanceType().getFinCategory().equals(PennantConstants.FINANCE_PRODUCT_SUKUK)
							|| getFinScheduleData().getFinanceType().getFinCategory().equals(PennantConstants.FINANCE_PRODUCT_SUKUKNRM)){
						data.setLabel(Labels.getLabel("listcell_couponrepay.label"));
					}else{
						data.setLabel(Labels.getLabel("listcell_repay.label"));
					}
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()){
							data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
									PennantConstants.dateFormate)+"[R]");
						}else {
							data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
									PennantConstants.dateFormate));
						}
					}else {
						data.setSchDate("");
					}
					data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,true));
					data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));				
					data.setSchdPft(formatAmt(aScheduleDetail.getProfitSchd(),false,true));				
					data.setSchdPri(formatAmt(aScheduleDetail.getPrincipalSchd(),false,true));
					data.setTotalAmount(formatAmt(aScheduleDetail.getRepayAmount(),false,false));

					reportList.add(data);
					count = 2;
				}
				
				if (aScheduleDetail.isDefered()) {
					
					data = new FinanceScheduleReportData();	
					data.setLabel(Labels.getLabel("listcell_defferment.label"));
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()){
							data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
									PennantConstants.dateFormate)+"[R]");
						}else {
							data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
									PennantConstants.dateFormate));
						}
					}else {
						data.setSchDate("");
					}
					data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,true));
					data.setPftAmount(formatAmt(BigDecimal.ZERO,false,false));				
					data.setSchdPft(formatAmt(aScheduleDetail.getDefProfit(),false,true));				
					data.setSchdPri(formatAmt(aScheduleDetail.getDefPrincipal(),false,true));
					data.setTotalAmount(formatAmt(aScheduleDetail.getDefProfit().add(aScheduleDetail.getDefPrincipal()),false,false));

					reportList.add(data);
					count = 2;
				} 

			}
			//To show repayment details 
			if(paymentDetailsMap != null && paymentDetailsMap.containsKey(aScheduleDetail.getSchDate())) {
				setFinanceRepayments(paymentDetailsMap.get(aScheduleDetail.getSchDate()));
				for (int j = 0; j < getFinanceRepayments().size(); j++) {
					data = new FinanceScheduleReportData();	
					data.setLabel(Labels.getLabel("listcell_AmountPaid.label", 
							new String[]{PennantAppUtil.formateDate(getFinanceRepayments().get(j).getFinPostDate(), PennantConstants.dateFormate)}));
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
								PennantConstants.dateFormate));
					}else {
						data.setSchDate("");
					}
					//TODO -- No field Like 'FinPaidBal' in Finance repayments
					data.setEndBal(formatAmt(BigDecimal.ZERO,false,false));
					data.setPftAmount(formatAmt(BigDecimal.ZERO,false,false));				
					data.setSchdPft(formatAmt(getFinanceRepayments().get(j).getFinSchdPftPaid(),false,false));				
					data.setSchdPri(formatAmt(getFinanceRepayments().get(j).getFinSchdPriPaid(),false,false));
					data.setTotalAmount(formatAmt(getFinanceRepayments().get(j).
							getFinSchdPftPaid().add(getFinanceRepayments().get(j).getFinSchdPriPaid()),false,false));
					reportList.add(data);
					count = 2;
				}
			}
			
			//To show Penalty details 
			if(penaltyDetailsMap != null && penaltyDetailsMap.containsKey(aScheduleDetail.getSchDate())) {
				setPenalties(penaltyDetailsMap.get(aScheduleDetail.getSchDate()));
				for (int j = 0; j < getPenalties().size(); j++) {
					
					OverdueChargeRecovery recovery = getPenalties().get(j);
					data = new FinanceScheduleReportData();	
					data.setLabel(Labels.getLabel("listcell_PenaltyPaid.label", 
							new String[]{PennantAppUtil.formateDate(recovery.getMovementDate(), PennantConstants.dateFormate)}));
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
								PennantConstants.dateFormate));
					}else {
						data.setSchDate("");
					}
					
					data.setEndBal("");
					data.setPftAmount("");				
					data.setSchdPft("");				
					data.setSchdPri("");
					data.setTotalAmount(formatAmt(recovery.getPenaltyPaid(),false,false));
					reportList.add(data);
					count = 2;
				}
			}
			
			//WriteOff Details 
			if(aScheduleDetail.getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0 ||
					aScheduleDetail.getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0){
				
				data = new FinanceScheduleReportData();	
				data.setLabel(Labels.getLabel("listcell_Writeoff.label"));
				if (count == 1){
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					if( aScheduleDetail.isRvwOnSchDate()){
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(), PennantConstants.dateFormate)+"[R]");
					}else {
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(), PennantConstants.dateFormate));
					}
				}else {
					data.setSchDate("");
				}
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,true));
				data.setPftAmount(formatAmt(BigDecimal.ZERO,false,false));				
				data.setSchdPft(formatAmt(aScheduleDetail.getWriteoffProfit(),false,true));				
				data.setSchdPri(formatAmt(aScheduleDetail.getWriteoffPrincipal(),false,true));
				data.setTotalAmount(formatAmt(aScheduleDetail.getWriteoffPrincipal().add(aScheduleDetail.getWriteoffProfit()),false,false));

				reportList.add(data);
				count = 2;
			}
			
			if(aFinScheduleData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
				
				List<DefermentDetail> defermentList = aFinScheduleData.getDefermentMap().get(aScheduleDetail.getSchDate());
				if(defermentList != null){
					for (DefermentDetail defDetail : defermentList) {

						
						data = new FinanceScheduleReportData();	
						data.setLabel(Labels.getLabel("listcell_deffermentPay.label",
								new String[]{PennantAppUtil.formateDate(defDetail.getDeferedSchdDate(), PennantConstants.dateFormate)}));
						if (count == 1){
							data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
							if( aScheduleDetail.isRvwOnSchDate()){
								data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(), PennantConstants.dateFormate)+"[R]");
							}else {
								data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(), PennantConstants.dateFormate));
							}
						}else {
							data.setSchDate("");
						}
						data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,true));
						data.setPftAmount(formatAmt(BigDecimal.ZERO,false,false));				
						data.setSchdPft(formatAmt(defDetail.getDefRpySchdPft(),false,true));				
						data.setSchdPri(formatAmt(defDetail.getDefRpySchdPri(),false,true));
						data.setTotalAmount(formatAmt(defDetail.getDefRpySchdPft().add(defDetail.getDefRpySchdPri()),false,false));

						reportList.add(data);
					}
					count = 2;
				}
			}
			
			if (aScheduleDetail.isRvwOnSchDate() && aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("listcell_reviewRate.label"));
				if(aScheduleDetail.isCalOnIndRate()){
					data.setLabel(Labels.getLabel("listcell_reviewIndRate.label"));
				}
				if (count == 1) {
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
							PennantConstants.dateFormate)+"[R]");
				}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0) {
					data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
							PennantConstants.dateFormate)+"[R]");
				}else {
					data.setSchDate("");
				}
				data.setPftAmount(formatAmt(aScheduleDetail.getCalculatedRate(),true,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);
				count = 2;
			}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0){
				
				if(CalculationConstants.RATE_BASIS_C.equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("listcell_flatRate.label"));
					data.setSchDate("");
					data.setPftAmount(formatAmt(aScheduleDetail.getActRate(),true,false));
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					reportList.add(data);
					count = 2;
				}
				
				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("listcell_reviewRate.label"));
				if(aScheduleDetail.isCalOnIndRate()){
					data.setLabel(Labels.getLabel("listcell_reviewIndRate.label"));
				}
				data.setSchDate("");
				data.setPftAmount(formatAmt(aScheduleDetail.getCalculatedRate(),true,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);
				count = 2;
			}else if (aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) == 0
					&& aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
				
				boolean isFlatRatebasis = false;
				if(CalculationConstants.RATE_BASIS_C.equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
					isFlatRatebasis = true;
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("listcell_flatRate.label"));
					if (count == 1) {
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
								PennantConstants.dateFormate)+"[R]");
					}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0) {
						data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
								PennantConstants.dateFormate)+"[R]");
					}else {
						data.setSchDate("");
					}
					data.setPftAmount(formatAmt(aScheduleDetail.getActRate(),true,false));
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					reportList.add(data);
					count = 2;
				}
				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("listcell_reviewRate.label"));
				if(aScheduleDetail.isCalOnIndRate()){
					data.setLabel(Labels.getLabel("listcell_reviewIndRate.label"));
				}
				if (count == 1) {
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
							PennantConstants.dateFormate)+"[R]");
				}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0 && !isFlatRatebasis) {
					data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
							PennantConstants.dateFormate)+"[R]");
				}else {
					data.setSchDate("");
				}
				data.setPftAmount(formatAmt(aScheduleDetail.getCalculatedRate(),true,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);
				count = 2;
			}	
			count = 1;
			if(lastRec && includeSummary){
				data = new FinanceScheduleReportData();
				data.setSchDate(Labels.getLabel("listcell_summary.label"));
				data.setLabel(Labels.getLabel("listcell_totalPftSch.label"));
				data.setPftAmount(formatAmt(totalPftAmount.subtract(totalGrcPftAmount),false,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);
				
				if(aFinScheduleData.getFinanceMain().isAllowGrcPeriod()){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("listcell_totalGrcPftSch.label"));
					data.setPftAmount(formatAmt(totalGrcPftAmount,false,true));
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					reportList.add(data);
				}

				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("listcell_totalCpz.label"));
				data.setPftAmount(formatAmt(totalCpzAmount,false,true));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				if(totalCpzAmount.compareTo(BigDecimal.ZERO) != 0){
					reportList.add(data);
				}

				if(aFinScheduleData.getFinanceMain().isAllowGrcPeriod()){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("listcell_totalGrossPft.label"));
					data.setPftAmount(formatAmt(totalGrossAmount,false,true));
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					reportList.add(data);
				}

				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("listcell_totalRepayAmt.label"));
				data.setPftAmount(formatAmt(totalRepayAmount,false,true));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);
				
				if(aFinScheduleData.getFinanceMain().isAllowGrcPeriod()){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("listcell_totalGrcDays.label"));
					data.setPftAmount(String.valueOf(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
							getFinScheduleData().getFinanceMain().getGrcPeriodEndDate())));
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					reportList.add(data);
				}
				
				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("listcell_totalDays.label"));
				data.setPftAmount(String.valueOf(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
						getFinScheduleData().getFinanceMain().getMaturityDate())));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);
				
				count = 2;				
			}
		}
		logger.debug("Leaving");
		return reportList;
	}
	
	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 * */
	public List<FinanceGraphReportData> getScheduleGraphData(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");
		
		setFinScheduleData(aFinScheduleData);
		ArrayList<FinanceGraphReportData> reportList = new ArrayList<FinanceGraphReportData>();
		FinanceGraphReportData data;
		
		BigDecimal profitBal = BigDecimal.ZERO;
		BigDecimal principalBal = BigDecimal.ZERO;
		BigDecimal financeBal = BigDecimal.ZERO;
		
		int formatter = aFinScheduleData.getFinanceMain().getLovDescFinFormatter();
		int size = aFinScheduleData.getFinanceScheduleDetails().size();
		
		for (int i = size-1; i >=0 ; i--) {
			
			FinanceScheduleDetail aScheduleDetail = getFinScheduleData().getFinanceScheduleDetails().get(i);
			data = new FinanceGraphReportData();
			data.setRecordNo(i);
			data.setSchDate(DateUtility.formatUtilDate(aScheduleDetail.getDefSchdDate(),
					PennantConstants.dateFormate));
			
			if(i == size-1){
				data.setProfitBal(BigDecimal.ZERO);
				data.setPrincipalBal(BigDecimal.ZERO);
				data.setFinanceBal(BigDecimal.ZERO);
			}else{
				data.setFinanceBal(PennantAppUtil.formateAmount(financeBal,formatter));
				data.setPrincipalBal(PennantAppUtil.formateAmount(principalBal,formatter));
				data.setProfitBal(PennantAppUtil.formateAmount(profitBal,formatter));
			}
			
			/*System.out.println("Date:" + data.getSchDate()+" ------> "+data.getFinanceBal()+
					"-----"+data.getPrincipalBal()+"-----"+data.getProfitBal()); */	
			
			profitBal = profitBal.add(aScheduleDetail.getProfitCalc());
			principalBal = principalBal.add(aScheduleDetail.getPrincipalSchd());
			financeBal = financeBal.add(aScheduleDetail.getPrincipalSchd()).add(aScheduleDetail.getProfitCalc());
			
			reportList.add(data);
		}
		logger.debug("Leaving");
		
		return sortGraphDetail(reportList);
	}
	
	/**
	 * Method for Reporting Schedule Details on Agreement
	 * @param aFinScheduleData
	 * @return
	 */
	public List<FinanceScheduleReportData> getAgreementSchedule(FinScheduleData aFinScheduleData){

		setFinScheduleData(aFinScheduleData);
		ArrayList<FinanceScheduleReportData> reportList = new ArrayList<FinanceScheduleReportData>();
		FinanceScheduleReportData data;
		int schdSeqNo = 0;
		int size = aFinScheduleData.getFinanceScheduleDetails().size();
		lastRec = false;
		
		for (int i = 0; i < size; i++) {

			FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);

			if (curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() || 
					(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				
				if(i == size-1){
					lastRec = true;
				}
				
				closingBal = curSchd.getClosingBalance();
				
				data = new FinanceScheduleReportData();	
				data.setSchdSeqNo(String.valueOf(schdSeqNo+1));
				data.setSchDate(DateUtility.formatUtilDate(curSchd.getDefSchdDate(), PennantConstants.dateFormate));
				data.setEndBal(formatAmt(curSchd.getClosingBalance(),false,true));
				data.setPftAmount(formatAmt(curSchd.getProfitCalc(),false,true));				
				data.setSchdPft(formatAmt(curSchd.getProfitSchd(),false,true));				
				data.setSchdPri(formatAmt(curSchd.getPrincipalSchd(),false,true));
				data.setTotalAmount(formatAmt(curSchd.getRepayAmount(),false,true));

				//Exclude Grace Schedule term Details
				if(curSchd.getDefSchdDate().compareTo(aFinScheduleData.getFinanceMain().getGrcPeriodEndDate()) > 0){
					reportList.add(data);
					schdSeqNo = schdSeqNo+1;
				}

			}
		}
		return reportList;

	}
	
	/**
	 * Method to set format for rate and amount values 
	 *  
	 * @param BigDecimal (amount), Boolean (isRate), boolean (showZeroEndbal)
	 * 
	 * @return String
	 * */
	private String formatAmt(BigDecimal amount,boolean isRate, boolean showZeroEndBal) {
		logger.debug("Entering");
		if (amount.compareTo(BigDecimal.ZERO) != 0) {
			if (isRate) { // Append % sysmbol if rate and format using rate format
				return new BigDecimal(PennantApplicationUtil.formatRate(amount.doubleValue(), PennantConstants.rateFormate))+" % ";
			} else {
				return PennantAppUtil.amountFormate(amount, getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			}
		} else  if (closingBal.compareTo(BigDecimal.ZERO) == 0 && !lastRec && showZeroEndBal) { 
			return PennantAppUtil.amountFormate(amount, getFinScheduleData().getFinanceMain().getLovDescFinFormatter()); 
		}
		else if (closingBal.compareTo(BigDecimal.ZERO) == 0 && lastRec) {
			return PennantAppUtil.amountFormate(amount, getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		}else if (amount.compareTo(BigDecimal.ZERO) == 0 && showZeroEndBal) { 
			return PennantAppUtil.amountFormate(amount, getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		}else {
			return "";
		}
	}
	
	/**
	 * Sorting Fee Rule Details List For Display in Schedule Details
	 * @param feeRuleDetails
	 * @return
	 */
	public List<FeeRule> sortFeeRules(List<FeeRule> feeRuleDetails) {

		if (feeRuleDetails != null && feeRuleDetails.size() > 0) {
			Collections.sort(feeRuleDetails, new Comparator<FeeRule>() {
				@Override
				public int compare(FeeRule detail1, FeeRule detail2) {
					if (detail1.getSeqNo() > detail2.getSeqNo()) {
						return 1;
					}else if(detail1.getSeqNo() == detail2.getSeqNo()) {
						if(detail1.getFeeOrder() > detail2.getFeeOrder()) {
							return 1;
						}
					}
					return 0;
				}
			});
		}

		return feeRuleDetails;
	}

	/**Method for Sorting List of Schedule Details for Graph Report
	 * 
	 * @param feeRuleDetails
	 * @return
	 */
	public List<FinanceGraphReportData> sortGraphDetail(List<FinanceGraphReportData> graphSchdlList) {

		if (graphSchdlList != null && graphSchdlList.size() > 0) {
			Collections.sort(graphSchdlList, new Comparator<FinanceGraphReportData>() {
				@Override
				public int compare(FinanceGraphReportData detail1, FinanceGraphReportData detail2) {
					if (detail1.getRecordNo() > detail2.getRecordNo()) {
						return 1;
					}
					return 0;
				}
			});
		}

		return graphSchdlList;
	}
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public List<DefermentDetail> getDefermentDetail() {
		return defermentDetail;
	}
	public void setDefermentDetail(List<DefermentDetail> defermentDetail) {
		this.defermentDetail = defermentDetail;
	}

	public List<FinanceRepayments> getFinanceRepayments() {
		return financeRepayments;
	}
	public void setFinanceRepayments(List<FinanceRepayments> financeRepayments) {
		this.financeRepayments = financeRepayments;
	}
	
	public List<OverdueChargeRecovery> getPenalties() {
		return penalties;
	}
	public void setPenalties(List<OverdueChargeRecovery> penalties) {
		this.penalties = penalties;
	}

}
