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
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class FinScheduleListItemRenderer implements Serializable{

	private static final long serialVersionUID = 598041940390030115L;
	private final static Logger logger = Logger.getLogger(FinScheduleListItemRenderer.class);

	protected FinScheduleData finScheduleData;
	protected FinanceScheduleDetail financeScheduleDetail;
	protected DefermentDetail defermentDetail;
	protected static  Window  window;
	private Map<Date,ArrayList<FinanceRepayments>> repayDetalsMap;
	private List<FinanceRepayments> financeRepayments;
	private transient BigDecimal 	closingBal = null;
	protected boolean 	lastRec;
	protected Listitem 	listitem;
	protected Listbox 	listBoxSchedule;
	protected Button 	btnAddReviewRate;
	protected Button 	btnChangeRepay;
	protected Button 	btnAddDisbursement;

	/**
	 * Method to render the list items
	 * 
	 * @param FinanceScheduleDetail
	 *            (financeScheduleDetail)
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void render(HashMap map, FinanceScheduleDetail prvSchDetail, boolean lastRecord,
			boolean allowEdit,boolean isRepayEnquiry ,
			Map<Date, ArrayList<FeeRule>> feeRuleDetailsMap, boolean showRate) {
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
			setDefermentDetail((DefermentDetail) map.get("defermentDetail"));
		}
		if (map.containsKey("window")) {
			window = (Window) map.get("window");
		}
		if (map.containsKey("paymentDetailsMap")) {
			repayDetalsMap = (Map<Date, ArrayList<FinanceRepayments>>) map.get("paymentDetailsMap");
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
		FinanceMain aFinanceMain = getFinScheduleData().getFinanceMain();
		int count = 1;
		closingBal = getFinanceScheduleDetail().getClosingBalance();
		boolean isEditable = false;
		boolean isRate = false;
		boolean showZeroEndBal = false;
		boolean isGrcBaseRate = false;
		boolean isRpyBaseRate = false;

		if (lastRec) {

			isEditable = false;
			isRate = false;
			showZeroEndBal = false;
			isGrcBaseRate = false;
			isRpyBaseRate = false;
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalPftSch.label"),
					getFinScheduleData().getFinanceMain().getTotalProfit().subtract(getFinScheduleData().getFinanceMain().getTotalGracePft()),
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null);
			count = 3;
			
			int grcDays = 0;
			if(getFinScheduleData().getFinanceMain().isAllowGrcPeriod()){
				
				grcDays = DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
						getFinScheduleData().getFinanceMain().getGrcPeriodEndDate());
				if(grcDays > 0){
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalGrcPftSch.label"),
							getFinScheduleData().getFinanceMain().getTotalGracePft(), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO,
							BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null);
				}
			}
			if(getFinScheduleData().getFinanceMain().getTotalCpz().compareTo(BigDecimal.ZERO) != 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalCpz.label"),
						getFinScheduleData().getFinanceMain().getTotalCpz(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null);
			}
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalGrossPft.label", ""),
					getFinScheduleData().getFinanceMain().getTotalGrossPft(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null);
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalRepayAmt.label", ""),
					getFinScheduleData().getFinanceMain().getTotalRepayAmt(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null);
			
			if(getFinScheduleData().getFinanceMain().isAllowGrcPeriod()){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalGrcDays.label", ""),
						new BigDecimal(grcDays), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",1, null);
			}
			
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_totalDays.label", ""),
					new BigDecimal(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
							getFinScheduleData().getFinanceMain().getMaturityDate())), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",1, null);

		} else {
			if (getFinanceScheduleDetail().isPftOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate() 
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
						getFinanceScheduleDetail().getProfitCalc(), getFinanceScheduleDetail().getProfitSchd(), 
						getFinanceScheduleDetail().getPrincipalSchd(), getFinanceScheduleDetail().getRepayAmount(), 
						getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate, showZeroEndBal,
						isGrcBaseRate, isRpyBaseRate, "","",0, null);
				count = 2;
				if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowEdit) {
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
						getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO,
						BigDecimal.ZERO, getFinanceScheduleDetail().getDisbAmount(),
						getFinanceScheduleDetail().getClosingBalance().subtract(
								getFinanceScheduleDetail().getFeeChargeAmt() == null ? BigDecimal.ZERO : getFinanceScheduleDetail().getFeeChargeAmt())
								.add(getFinanceScheduleDetail().getDownPaymentAmount()),
						isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null);
				
				count = 2;
				
				if(feeRuleDetailsMap != null && getFinanceScheduleDetail().getFeeChargeAmt() != null &&
						 getFinanceScheduleDetail().getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0){
					
					if(feeRuleDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())){
						List<FeeRule> feeRuleList = new ArrayList<FeeRule>(feeRuleDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
						feeRuleList = sortFeeRules(feeRuleList);

						BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

						for (FeeRule feeRule : feeRuleList) {

							if(feeRule != null && feeRule.getFeeAmount().compareTo(BigDecimal.ZERO) > 0){

								doFillListBox(getFinanceScheduleDetail(), count, feeRule.getFeeCodeDesc(),
										BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, feeRule.getFeeAmount(),
										getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt).add(feeRule.getFeeAmount())
										.add(getFinanceScheduleDetail().getDownPaymentAmount()),
										false, isRate,
										showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null);

								feeChargeAmt = feeChargeAmt.subtract(feeRule.getFeeAmount());
							}
						}
					}
				}
				
				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_disbursementAdded_label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null);
				
				if (this.btnAddDisbursement != null && this.btnAddDisbursement.isVisible() && 
						getFinScheduleData().getFinanceType().isFinIsAlwMD() && allowEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisburseItemDoubleClicked");
				}

			}
			
			if (getFinanceScheduleDetail().isDownpaymentOnSchDate()) {
				isEditable = false;
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_downPayment.label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						getFinanceScheduleDetail().getDownPaymentAmount(), 
						getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate, 
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null);
				count = 2;
			}
			
			if (getFinanceScheduleDetail().isRepayOnSchDate()) {
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
							getFinanceScheduleDetail().getProfitCalc(), getFinanceScheduleDetail().getProfitSchd(), 
							getFinanceScheduleDetail().getPrincipalSchd(), getFinanceScheduleDetail().getRepayAmount(), 
							getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate, showZeroEndBal,
							isGrcBaseRate, isRpyBaseRate, "","",0, null);

					isEditable = true;
					count = 2;
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_defferment.label"),
							BigDecimal.ZERO,
							getFinanceScheduleDetail().getDefProfit(), getFinanceScheduleDetail().getDefPrincipal(),
							getFinanceScheduleDetail().getDefProfit().add(getFinanceScheduleDetail().getDefPrincipal()),
							BigDecimal.ZERO, false,
							isRate,  showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#FF0000","color_Deferred",0, null);
					
					if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate()
							&& getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0
							&& allowEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}
				} else if(!(getFinanceScheduleDetail().getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0)) {
					String colorClass = "";
					//TODO Confirm Later  to include (&& getFinanceScheduleDetail().isDefSchPftPaid() && getFinanceScheduleDetail().isDefSchPriPaid())
					/*if(getFinanceScheduleDetail().isSchPftPaid()&& getFinanceScheduleDetail().isSchPriPaid()) {
						colorClass = "color_Repayment";
					}*/
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_repay.label"),
							getFinanceScheduleDetail().getProfitCalc(),
							getFinanceScheduleDetail().getProfitSchd(), getFinanceScheduleDetail().getPrincipalSchd(),
							getFinanceScheduleDetail().getRepayAmount(), getFinanceScheduleDetail().getClosingBalance(),
							isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", colorClass,0, null);
					count = 2;
					if (this.btnChangeRepay != null && !this.btnChangeRepay.isDisabled() && allowEdit) {
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
						BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getCpzAmount(),
						BigDecimal.ZERO, getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",0, null);
				count = 2;
				if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}

			}
			
			//To show repayment details 
			if(isRepayEnquiry && repayDetalsMap != null && repayDetalsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				setFinanceRepayments(repayDetalsMap.get(getFinanceScheduleDetail().getSchDate()));
				for (int i = 0; i < getFinanceRepayments().size(); i++) {
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_AmountPaid.label", 
							new String[]{PennantAppUtil.formateDate(getFinanceRepayments().get(i).getFinPostDate(), PennantConstants.dateFormate)}),
							BigDecimal.ZERO,
							 getFinanceRepayments().get(i).getFinSchdPftPaid(),getFinanceRepayments().get(i).getFinSchdPriPaid(),
							 getFinanceRepayments().get(i).getFinSchdPftPaid().add(getFinanceRepayments().get(i).getFinSchdPriPaid()),
							BigDecimal.ZERO, false, isRate,  showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#330066", "color_Repayment",0, null);
					count = 2;
				}
			}
			if(getDefermentDetail() != null) {
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_deffermentPay.label"),
						BigDecimal.ZERO,
						getFinanceScheduleDetail().getDefProfitSchd(), getFinanceScheduleDetail().getDefPrincipalSchd(),
						getFinanceScheduleDetail().getDefProfitSchd().add(getFinanceScheduleDetail().getDefPrincipalSchd()),
						BigDecimal.ZERO, false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#347C17", "color_Repayment",0, null);
				count = 2;
			}

			if (getFinanceScheduleDetail().isRvwOnSchDate() || showRate) {
				if(getFinanceScheduleDetail().isRvwOnSchDate()){ 
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = true;
				showZeroEndBal = false;
				if (aFinanceMain.getGraceBaseRate() != null && getFinanceScheduleDetail().getSpecifier().equals("G")) {
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
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null);
					
						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_IndRateAdded_label", 
								new String[]{String.valueOf(getFinanceScheduleDetail().getCalculatedRate())}),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",2, null);
						
						count = 2;
						if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}
					}else {
						
						String flatRateConvert = "listcell_flatRateChangeAdded_label";
						if("F".equals(aFinanceMain.getRepayRateBasis())){
							
							doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_flatRate.label"),
									getFinanceScheduleDetail().getActRate(), new BigDecimal(0),
									new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), false, isRate,
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null);
							
							//Event Description Details
							doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
									new String[]{String.valueOf(prvSchDetail.getActRate()),String.valueOf(getFinanceScheduleDetail().getActRate())}),
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
									false, false, false, false, "","",2, null);
							flatRateConvert = "listcell_flatRateConvertChangeAdded_label";
						}
						
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null);
						
						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(prvSchDetail.getCalculatedRate()),String.valueOf(getFinanceScheduleDetail().getCalculatedRate())}),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",2, null);
						
						count = 2;
						if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
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
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate",0, null);
					
					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_IndRateAdded_label", 
							new String[]{String.valueOf(getFinanceScheduleDetail().getCalculatedRate())}),
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
							false, false, false, false, "","",2, null);
					
					count = 2;
					if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}
				}else {
					
					String flatRateConvert = "listcell_flatRateAdded_label";
					BigDecimal rate = getFinanceScheduleDetail().getCalculatedRate();
					if("F".equals(aFinanceMain.getRepayRateBasis())){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_flatRate.label"),
								getFinanceScheduleDetail().getActRate(), new BigDecimal(0),
								new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), false, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null);
						
						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(getFinanceScheduleDetail().getActRate())}),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",2, null);
						
						flatRateConvert = "listcell_flatRateConvertAdded_label";
						rate = getFinanceScheduleDetail().getActRate();
					}
					
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null);
					
					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
							new String[]{String.valueOf(rate), 
							String.valueOf(getFinanceScheduleDetail().getCalculatedRate())}),
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
							false, false, false, false, "","",2, null);
					
					count = 2;
					if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() &&  allowEdit) {
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
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null);
				
			}else if(getFinanceScheduleDetail().getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0 ){
				
				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("listcell_EarlyPayBalDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(
								getFinanceScheduleDetail().getEarlyPaidBal(),aFinanceMain.getLovDescFinFormatter())}),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null);
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
	public void doFillListBox(FinanceScheduleDetail data, int count, String eventName, BigDecimal pftAmount,
			BigDecimal schdlPft, BigDecimal cpzAmount, BigDecimal totalAmount,
			BigDecimal endBal, boolean isEditable, boolean isRate,  boolean showZeroEndBal, 
			boolean isGrcBaseRate, boolean isRpyBaseRate, String bgColor, String lcColor, int fillType, Date progClaimDate) {
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
			lc.setSpan(6);
		}
		listitem.appendChild(lc);

		// Amounts array
		BigDecimal amountlist[] = { pftAmount, schdlPft, cpzAmount, totalAmount, endBal };

		if(fillType == 1){
			lc = new Listcell(String.valueOf(amountlist[0].intValue()));
			lc.setStyle("text-align:right;");
			listitem.appendChild(lc);
			lc = new Listcell("");
			lc.setSpan(4);
			listitem.appendChild(lc);
		}else if(fillType == 2){
			//Nothing todo
		}else{
			// Append amount listcells to listitem
			for (int i = 0; i < amountlist.length; i++) {
				if (amountlist[i].compareTo(BigDecimal.ZERO) != 0) {
					rate = PennantAppUtil.formatRate(amountlist[i].doubleValue(), 9);
					if (isRate) { // Append % sysmbol if rate and format using rate format
						String baseRate = data.getBaseRate();
						String splRate = StringUtils.trimToEmpty(data.getSplRate());
						String marginRate = data.getMrgRate().compareTo(BigDecimal.ZERO) == 0?"":data.getMrgRate().toString();
						if (isGrcBaseRate && (data.getSpecifier().equals(CalculationConstants.GRACE) ||
								data.getSpecifier().equals(CalculationConstants.GRACE_END))) {
							lc = new Listcell("[ " +baseRate+""+(splRate.equals("")?"":","+splRate)+""+
									(marginRate.equals("")?"":","+marginRate)+" ]"+
									PennantAppUtil.formatRate(amountlist[i].doubleValue(), 2) + "%");
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						} else if (isRpyBaseRate && (data.getSpecifier().equals(CalculationConstants.REPAY) || 
								data.getSpecifier().equals(CalculationConstants.GRACE_END))) {
							lc = new Listcell("[ " +baseRate+""+(splRate.equals("")?"":","+splRate)+""+
									(marginRate.equals("")?"":","+marginRate)+" ]"+
									PennantAppUtil.formatRate(amountlist[i].doubleValue(), 2) + "%");
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						} else {
							lc = new Listcell(PennantAppUtil.formatRate(amountlist[i].doubleValue(), 2) + "%");
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						}
					} else {
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
					}
				} else if (closingBal.compareTo(BigDecimal.ZERO) == 0 && i == amountlist.length - 1 &&
						!lastRec && showZeroEndBal) {
					lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], 
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && ( i == 1 || i == 2 ) && showZeroEndBal) {
					lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], 
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
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

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 * */
	public List<FinanceScheduleReportData> getScheduleData(FinScheduleData aFinScheduleData, 
			Map<Date,ArrayList<FinanceRepayments>> paymentDetailsMap, Map<Date,ArrayList<FeeRule>> feeRuleDetails) {
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
						
					}
					if(j == 1){
						if(feeRuleDetails != null && aScheduleDetail.getFeeChargeAmt()!= null &&
								aScheduleDetail.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0 ){
							
							if(feeRuleDetails.containsKey(aScheduleDetail.getSchDate())){
							
							List<FeeRule> feeChargeList = sortFeeRules(feeRuleDetails.get(aScheduleDetail.getSchDate()));
							
							BigDecimal feeChargeAmt = aScheduleDetail.getFeeChargeAmt();
							for (FeeRule rule : feeChargeList) {
								
								if(rule.getFeeAmount().compareTo(BigDecimal.ZERO) > 0){
									data = new FinanceScheduleReportData();	

									data.setLabel(rule.getFeeCodeDesc());
									data.setPftAmount("");
									data.setSchdPft("");
									data.setSchdPri("");
									data.setTotalAmount(formatAmt(rule.getFeeAmount(),false,false));
									data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance().subtract(feeChargeAmt)
											.add(rule.getFeeAmount())
											.add(aScheduleDetail.getDownPaymentAmount()),false,false));
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
			
			if (aScheduleDetail.isDownpaymentOnSchDate()) {
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
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
				reportList.add(data);
				count = 2;
			}		
			
			if (aScheduleDetail.isPftOnSchDate() && !aScheduleDetail.isRepayOnSchDate()) {
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
			if (aScheduleDetail.isRepayOnSchDate()) {
			if(!(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0)) {
				data = new FinanceScheduleReportData();	
				data.setLabel(Labels.getLabel("listcell_repay.label"));
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
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
				data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));				
				data.setSchdPft(formatAmt(aScheduleDetail.getProfitSchd(),false,true));				
				data.setSchdPri(formatAmt(aScheduleDetail.getPrincipalSchd(),false,true));
				data.setTotalAmount(formatAmt(aScheduleDetail.getRepayAmount(),false,false));

				reportList.add(data);
				}
				count = 2;
				
			}
			//To show repayment details 
			if(paymentDetailsMap != null && paymentDetailsMap.containsKey(aScheduleDetail.getSchDate())) {
				setFinanceRepayments(paymentDetailsMap.get(aScheduleDetail.getSchDate()));
				for (int j = 0; j < getFinanceRepayments().size(); j++) {
					data = new FinanceScheduleReportData();	
					data.setLabel(Labels.getLabel("listcell_AmountPaid.label"));
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
				
				if("F".equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
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
				if("F".equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
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
			if(lastRec){
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
				data.setPftAmount(formatAmt(totalCpzAmount,false,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				if(totalCpzAmount.compareTo(BigDecimal.ZERO) != 0){
					reportList.add(data);
				}

				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("listcell_totalGrossPft.label"));
				data.setPftAmount(formatAmt(totalGrossAmount,false,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				reportList.add(data);

				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("listcell_totalRepayAmt.label"));
				data.setPftAmount(formatAmt(totalRepayAmount,false,false));
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
				return new BigDecimal(PennantAppUtil.formatRate(amount.doubleValue(), 2))+" % ";
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
					if (detail1.getFeeOrder() > detail2.getFeeOrder()) {
						return 1;
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

	public DefermentDetail getDefermentDetail() {
		return defermentDetail;
	}

	public void setDefermentDetail(DefermentDetail defermentDetail) {
		this.defermentDetail = defermentDetail;
	}

	public List<FinanceRepayments> getFinanceRepayments() {
		return financeRepayments;
	}

	public void setFinanceRepayments(List<FinanceRepayments> financeRepayments) {
		this.financeRepayments = financeRepayments;
	}
}
