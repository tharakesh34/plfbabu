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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class FinScheduleListItemRenderer implements Serializable{

	private static final long serialVersionUID = 598041940390030115L;
	private final static Logger logger = Logger.getLogger(FinScheduleListItemRenderer.class);

	protected FinScheduleData finScheduleData;
	protected FinanceScheduleDetail financeScheduleDetail;
	protected Window  window;
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
	private boolean 	addExternalCols = false;
	private boolean 	showAdvRate = false;
	private boolean 	isEMIHEditable = false;
	private transient 	BigDecimal 	totalAdvPft = BigDecimal.ZERO;
	private BigDecimal 	odLimit = BigDecimal.ZERO;
	private BigDecimal 	limitIncreaseAmt = BigDecimal.ZERO;
	boolean isLimitIncrease = false;
	private BigDecimal 	availableLimit = BigDecimal.ZERO;
	private BigDecimal 	limitDrop	= BigDecimal.ZERO;
	BigDecimal odLimitDropAmt = BigDecimal.ZERO;
	boolean isLimitDrop = false;
	int odCount = 0;
	String isODChangeAmt = ""; 

	public FinScheduleListItemRenderer() {

	}



	//FIXME:This method need to be removed once the fee changes is completed in servicing.
	//It is duplicate of render method in this class with few changes to support origination fees .  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void renderOrg(HashMap map, FinanceScheduleDetail prvSchDetail, boolean lastRecord,
			boolean allowRvwRateEdit,boolean isRepayEnquiry ,
			List<FinFeeDetail> finFeeDetailList, boolean showRate, boolean displayStepInfo) {
		logger.debug("Entering");
		lastRec = lastRecord;

		// READ OVERHANDED parameters !
		if (map.containsKey("finSchdData")) {
			setFinScheduleData((FinScheduleData) map.get("finSchdData"));
		}

		if (map.containsKey("financeScheduleDetail")) {
			setFinanceScheduleDetail((FinanceScheduleDetail) map.get("financeScheduleDetail"));
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

		if (map.containsKey("showAdvRate")) {
			showAdvRate = (Boolean) map.get("showAdvRate");
		}
		if (map.containsKey("isEMIHEditable")) {
			isEMIHEditable = (Boolean) map.get("isEMIHEditable");
		}

		if (map.containsKey("totalAdvPft")) {
			totalAdvPft = (BigDecimal) map.get("totalAdvPft");
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
		FinanceType financeType = getFinScheduleData().getFinanceType();
		String product = financeType.getFinCategory();

		// Schedule Fee Details existis or not Checking
		List<FeeRule> feeRuleList = getFinScheduleData().getFeeRules();
		for (int i = 0; i < feeRuleList.size(); i++) {

			FeeRule feeRule = feeRuleList.get(i);
			if(!StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE) &&
					!StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
				isSchdFee = true;
				break;
			}
		}

		// For Structured Murabaha product
		if(product.equals(FinanceConstants.PRODUCT_STRUCTMUR) ||
				(product.equals(FinanceConstants.PRODUCT_IJARAH)|| 
						product.equals(FinanceConstants.PRODUCT_FWIJARAH))|| StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())){
			addExternalCols = true;
		}

		showStepDetail = displayStepInfo;
		int count = 1;
		this.closingBal = getFinanceScheduleDetail().getClosingBalance();
		boolean isEditable = false;
		boolean isRate = false;
		boolean showZeroEndBal = false;
		boolean isGrcBaseRate = false;
		boolean isRpyBaseRate = false;
		if(accrueValue != null){

			Date lastAccrueDate = DateUtility.getAppDate();
			if((!lastRec && lastAccrueDate.compareTo(prvSchDetail.getSchDate()) >= 0 && 
					lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) < 0)  || 
					(lastRec && lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) > 0)){
				count = 3;

				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_AccrueAmount.label"),
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, accrueValue, BigDecimal.ZERO,false, false,  true, false, false, "#1D883C", "",5, 
						null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, true);
				count = 1;
			}
		}

		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());

		if (lastRec) {

			isEditable = false;
			isRate = false;
			showZeroEndBal = false;
			isGrcBaseRate = false;
			isRpyBaseRate = false;
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalPftSch.label"),
					aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft()),
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			count = 3;

			BigDecimal totalRebate = BigDecimal.ZERO;
			if(BigDecimal.ZERO.compareTo(totalAdvPft) < 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalAdvPftSch.label"),
						totalAdvPft.subtract(aFinanceMain.getTotalGracePft()),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

				totalRebate = aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft()).subtract(totalAdvPft);
			}

			if(BigDecimal.ZERO.compareTo(totalRebate) < 0){
				/*doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalRebate.label"),
						totalRebate,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);*/
			}

			int grcDays = 0;
			boolean showGrossPft = false;
			if(aFinanceMain.isAllowGrcPeriod()){

				grcDays = DateUtility.getDaysBetween(aFinanceMain.getFinStartDate(),aFinanceMain.getGrcPeriodEndDate());
				if(grcDays > 0 && aFinanceMain.getTotalGracePft().compareTo(BigDecimal.ZERO) > 0){
					showGrossPft = true;
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalGrcPftSch.label"),
							aFinanceMain.getTotalGracePft(),BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,	BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
				}
			}
			if(aFinanceMain.getTotalCpz().compareTo(BigDecimal.ZERO) != 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalCpz.label"),
						aFinanceMain.getTotalCpz(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			if(showGrossPft){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalGrossPft.label", ""),
						aFinanceMain.getTotalGrossPft(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalRepayAmt.label", ""),
					aFinanceMain.getTotalRepayAmt(),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

			if(BigDecimal.ZERO.compareTo(totalAdvPft) != 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalAdvRepayAmt.label", ""),
						aFinanceMain.getTotalRepayAmt().subtract(aFinanceMain.getTotalProfit().subtract(
								aFinanceMain.getTotalGracePft())).add(totalAdvPft),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			if(aFinanceMain.isAllowGrcPeriod() && grcDays > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalGrcDays.label", ""),
						new BigDecimal(grcDays),BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",15, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalDays.label", ""),
					new BigDecimal(DateUtility.getDaysBetween(aFinanceMain.getFinStartDate(), 
							aFinanceMain.getMaturityDate())),BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",15, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

		} else {

			//OverdraftSchedule drop Limits

			if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())){
				String label = "";
				int droplinefrqDay = FrequencyUtil.getIntFrequencyDay(aFinanceMain.getDroplineFrq());
				int rpyFrqDay =  FrequencyUtil.getIntFrequencyDay(aFinanceMain.getRepayFrq());
				boolean isSameDropLineDate = false;
				boolean odAvailable = false;
				if(getFinScheduleData().getOverdraftScheduleDetails()!=null &&  getFinScheduleData().getOverdraftScheduleDetails().size()>0){
					for(OverdraftScheduleDetail odSchedule:getFinScheduleData().getOverdraftScheduleDetails()){

						isLimitDrop = true;

						if(odSchedule.getDroplineDate().compareTo(aFinanceMain.getFinStartDate())==0 && 
								DateUtility.compare(odSchedule.getDroplineDate(), getFinanceScheduleDetail().getDefSchdDate())==0){
							label = Labels.getLabel("label_limitOverdraft");
							limitDrop = odSchedule.getLimitDrop();
							odLimit = odSchedule.getODLimit();
							availableLimit = odSchedule.getODLimit();
							odCount = 0;
							break;
						}else if(droplinefrqDay != rpyFrqDay){
							if(DateUtility.compare(odSchedule.getDroplineDate(),prvSchDetail.getDefSchdDate()) > 0 && 
									DateUtility.compare(odSchedule.getDroplineDate(), getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getDroplineDate())>0){
								if(odCount == getFinScheduleData().getOverdraftScheduleDetails().size()-2 && 
										DateUtility.compare(odSchedule.getDroplineDate(),aFinanceMain.getMaturityDate())== 0 && 
										DateUtility.compare(odSchedule.getDroplineDate(), getFinanceScheduleDetail().getDefSchdDate())==0 ){
									label = Labels.getLabel("label_LimitExpiry");
									odAvailable = true;
									isSameDropLineDate = true;
								}else if(DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate())<= 0){
									label = Labels.getLabel("label_LimitDrop");
									odAvailable = true;
								}
							}
						}else if(DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate()) == 0){
							isSameDropLineDate = true;
							if(DateUtility.compare(odSchedule.getDroplineDate(), aFinanceMain.getMaturityDate()) == 0){
								label = Labels.getLabel("label_LimitExpiry");
								odAvailable = true;
							}else{
								label = Labels.getLabel("label_LimitDrop");
								odAvailable = true;
							}
						}else{
							if(droplinefrqDay == rpyFrqDay){
								if(DateUtility.compare(odSchedule.getDroplineDate(),prvSchDetail.getDefSchdDate()) > 0 && 
										DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate())< 0){
									label = Labels.getLabel("label_LimitDrop");
									odAvailable = true;
								}
							}
						}

						if(odAvailable){
							limitDrop = odSchedule.getLimitDrop();
							odLimit = odSchedule.getODLimit();
							if(getFinanceScheduleDetail().isDisbOnSchDate() && DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate())== 0){
								availableLimit = odSchedule.getODLimit().subtract(getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getDisbAmount()));
							}else{
								availableLimit = odSchedule.getODLimit().subtract(getFinanceScheduleDetail().getClosingBalance());
							}
							if(DateUtility.compare(getFinanceScheduleDetail().getDefSchdDate(), aFinanceMain.getMaturityDate()) != 0){
								odCount = odCount +1;
							}else{
								odCount = getFinScheduleData().getOverdraftScheduleDetails().size()-1;
							}
							if(StringUtils.equals(Labels.getLabel("label_LimitDrop"),label)){
								if(odSchedule.getODLimit().compareTo(getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getODLimit())>0){
									label = Labels.getLabel("label_LimitIncrease");
									limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
								}
							}
							break;
						}
					}
				}else{
					isLimitDrop = false;
					odLimit = getFinScheduleData().getFinanceMain().getFinAssetValue();
					availableLimit = odLimit.subtract(getFinanceScheduleDetail().getClosingBalance().
							subtract(getFinanceScheduleDetail().getDisbAmount()));
					if(DateUtility.compare(getFinanceScheduleDetail().getDefSchdDate(), getFinScheduleData().getFinanceMain().getMaturityDate())==0){
						if(getFinanceScheduleDetail().getClosingBalance().compareTo(BigDecimal.ZERO)==0){
							availableLimit = BigDecimal.ZERO;
						}
					}
					
				}
				//to display the limit Increase amount only when the limit is changed
				if(!StringUtils.equals(label, Labels.getLabel("label_LimitIncrease"))){
					limitIncreaseAmt = BigDecimal.ZERO;
				}

				if(StringUtils.isNotBlank(label)){

					BigDecimal closingBalance =getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getDisbAmount());

					doFillListBox(getFinanceScheduleDetail(), count,label,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getCpzAmount(),
							BigDecimal.ZERO,BigDecimal.ZERO,closingBalance,isEditable, isRate,
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",0, null,false,limitIncreaseAmt,limitDrop,availableLimit,odLimit, true);
					count = 1;

					if( isSameDropLineDate || DateUtility.compare(finScheduleData.getFinanceMain().getFinStartDate(), getFinanceScheduleDetail().getDefSchdDate()) == 0){
						count = 2;
					}
				}
			}

			if (getFinanceScheduleDetail().isPftOnSchDate() && !(getFinanceScheduleDetail().isRepayOnSchDate() ||
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

				String label = Labels.getLabel("label_listcell_profitCalc.label");
				if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
					label = Labels.getLabel("label_listcell_BPIAmount.label");
					if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
						label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
					}
				}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
					label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
				}

				doFillListBox(getFinanceScheduleDetail(), count, label,	getFinanceScheduleDetail().getProfitCalc(),getFinanceScheduleDetail().getSuplRent(),getFinanceScheduleDetail().getIncrCost(),
						getFinanceScheduleDetail().getFeeSchd(),getFinanceScheduleDetail().getTDSAmount() == null ? BigDecimal.ZERO :getFinanceScheduleDetail().getTDSAmount() , 
								getFinanceScheduleDetail().getAdvProfit(), getFinanceScheduleDetail().getProfitSchd(), 
								getFinanceScheduleDetail().getPrincipalSchd(),getFinanceScheduleDetail().getAdvRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()),
								getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()).add(
										getFinanceScheduleDetail().getSuplRent()).add(getFinanceScheduleDetail().getIncrCost()), 
								getFinanceScheduleDetail().getClosingBalance(),isEditable, isRate, showZeroEndBal,
								isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				count = 2;
				//As confirmed with pradeep this can be removed
				/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowRvwRateEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}*/
			}else if(!getFinanceScheduleDetail().isPftOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate() && 
					!getFinanceScheduleDetail().isRvwOnSchDate() && !getFinanceScheduleDetail().isDisbOnSchDate()){

				if(prvSchDetail.getCalculatedRate().compareTo(getFinanceScheduleDetail().getCalculatedRate()) == 0 &&
						getFinanceScheduleDetail().getClosingBalance().compareTo(BigDecimal.ZERO) != 0){
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_profitCalc.label"),
							getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO , 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
							getFinanceScheduleDetail().getClosingBalance(),false, false, false,
							false, false, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
				}
			}

			if (getFinanceScheduleDetail().isDisbOnSchDate()) {
				isEditable = true;
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				BigDecimal curTotDisbAmt = BigDecimal.ZERO;
				for (int i = 0; i < getFinScheduleData().getDisbursementDetails().size(); i++) {

					FinanceDisbursement curDisb = getFinScheduleData().getDisbursementDetails().get(i);
					if(DateUtility.compare(curDisb.getDisbDate(), getFinanceScheduleDetail().getSchDate()) == 0){

						curTotDisbAmt = curTotDisbAmt.add(curDisb.getDisbAmount());
						//Subtract the available amount with the curDisb amount
						availableLimit = availableLimit.subtract(curDisb.getDisbAmount());
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_disbursement.label")+"( Seq : "+curDisb.getDisbSeq()+")",
								getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO, curDisb.getDisbAmount(),getFinanceScheduleDetail().getClosingBalance().subtract(
										getFinanceScheduleDetail().getFeeChargeAmt() == null ? BigDecimal.ZERO : getFinanceScheduleDetail().getFeeChargeAmt()).subtract(
												getFinanceScheduleDetail().getInsuranceAmt() == null ? BigDecimal.ZERO : getFinanceScheduleDetail().getInsuranceAmt()).subtract(
														getFinanceScheduleDetail().getDisbAmount()).add(curTotDisbAmt)
								.add(getFinanceScheduleDetail().getDownPaymentAmount()),isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

						count = 2;
					}
				}

				if (getFinanceScheduleDetail().isDownpaymentOnSchDate()) {
					isEditable = false;
					isRate = false;
					showZeroEndBal = false;
					isGrcBaseRate = false;
					isRpyBaseRate = false;

					BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_downPayment.label"),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO ,BigDecimal.ZERO , BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
							getFinanceScheduleDetail().getDownPaymentAmount(),getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt),isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				}

				// Fee Charge Details
				if(finFeeDetailList != null && getFinanceScheduleDetail().getFeeChargeAmt() != null &&
						getFinanceScheduleDetail().getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0){
					BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();
					BigDecimal insuranceAmt = getFinanceScheduleDetail().getInsuranceAmt();

					for (FinFeeDetail finFeeDetail : finFeeDetailList) {

						if(finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) < 1){
							continue;
						}

						if(StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){

							BigDecimal actFeeCharge = finFeeDetail.getRemainingFee();
							if(actFeeCharge.compareTo(BigDecimal.ZERO) >= 0){

								doFillListBox(getFinanceScheduleDetail(), count, finFeeDetail.getFeeTypeDesc(),BigDecimal.ZERO,BigDecimal.ZERO,
										BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO , BigDecimal.ZERO, actFeeCharge, actFeeCharge,
										getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt).subtract(insuranceAmt).add(actFeeCharge),
										false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,true,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

								feeChargeAmt = feeChargeAmt.subtract(actFeeCharge);
							}
						}
					}

				}

				// Insurance Details
				if(getFinanceScheduleDetail().getInsuranceAmt() != null &&
						getFinanceScheduleDetail().getInsuranceAmt().compareTo(BigDecimal.ZERO) > 0){

					BigDecimal insuranceAmt = getFinanceScheduleDetail().getInsuranceAmt();
					for (FinInsurances insurance : getFinScheduleData().getFinInsuranceList()) {

						if(StringUtils.equals(insurance.getPaymentMethod(),InsuranceConstants.PAYTYPE_ADD_DISB)){

							BigDecimal actInsuranceAmt = insurance.getAmount();
							if(actInsuranceAmt.compareTo(BigDecimal.ZERO) > 0){

								doFillListBox(getFinanceScheduleDetail(), count, insurance.getInsuranceTypeDesc() +"("+insurance.getInsReference()+")",BigDecimal.ZERO,BigDecimal.ZERO,
										BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO , BigDecimal.ZERO, actInsuranceAmt, actInsuranceAmt,
										getFinanceScheduleDetail().getClosingBalance().subtract(insuranceAmt).add(actInsuranceAmt),
										false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,true,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

								insuranceAmt = insuranceAmt.subtract(actInsuranceAmt);
							}
						}
					}
				}

				//Event Description Details
				if (getFinScheduleData().getFinanceType().getFinCategory().equals(FinanceConstants.PRODUCT_ISTISNA)) {
					if (getFinScheduleData().getDisbursementDetails()!=null) {
						for (FinanceDisbursement disbursement : getFinScheduleData().getDisbursementDetails()) {
							if (getFinanceScheduleDetail().getSchDate().compareTo(disbursement.getDisbDate()) == 0) {
								String remarks = "";
								if ("B".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Billing " + " - ";
								} else if ("A".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Advance " + " - ";
								} else if ("E".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Expense " + " - ";
								} else if ("C".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Consultancy Fee " + " - ";
								}
								if ("B".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks =remarks+ disbursement.getDisbRemarks() + " " + PennantAppUtil.amountFormate(disbursement.getDisbClaim(),format);
								} else {
									remarks =remarks+ disbursement.getDisbRemarks() + " " + PennantAppUtil.amountFormate(disbursement.getDisbAmount(),format);
								}
								doFillListBox(getFinanceScheduleDetail(), 2, remarks, BigDecimal.ZERO,BigDecimal.ZERO ,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, 
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,  false, false, false, false, false, "", "", 2, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
							}

						}
					}
				}

				/*if (this.btnAddDisbursement != null && this.btnAddDisbursement.isVisible() && 
						getFinScheduleData().getFinanceType().isFinIsAlwMD()) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisburseItemDoubleClicked");
				}*/

			}

			if (getFinanceScheduleDetail().isRepayOnSchDate() ||
					(getFinanceScheduleDetail().isPftOnSchDate() && getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				isRate = false;
				showZeroEndBal = true;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				if(getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {
					String colorClass = "";

					String label = Labels.getLabel("label_listcell_repay.label");
					if(getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0 &&
							(StringUtils.equals(aFinanceMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY) || 
									StringUtils.equals(aFinanceMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_GRCENDPAY))){
						label = Labels.getLabel("label_listcell_profitCalc.label");
					}
					if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
							label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
						}
					}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					}

					isEditable = true;

					//Checking Rollover Condition to make Closing Bal
					BigDecimal closingBal = BigDecimal.ZERO;
					if(finScheduleData.getFinanceMain().getNextRolloverDate() != null && 
							getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().getNextRolloverDate()) == 0)	{
						closingBal = getFinanceScheduleDetail().getRolloverAmount().subtract(getFinanceScheduleDetail().getCpzAmount());
					}else{
						closingBal = getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getCpzAmount());
					}

					doFillListBox(getFinanceScheduleDetail(), count, label,	getFinanceScheduleDetail().getProfitCalc(),
							getFinanceScheduleDetail().getSuplRent(),getFinanceScheduleDetail().getIncrCost(),getFinanceScheduleDetail().getFeeSchd(),
							getFinanceScheduleDetail().getTDSAmount() == null ? BigDecimal.ZERO :getFinanceScheduleDetail().getTDSAmount(),
									getFinanceScheduleDetail().getAdvProfit(), getFinanceScheduleDetail().getProfitSchd(), getFinanceScheduleDetail().getPrincipalSchd(),
									getFinanceScheduleDetail().getAdvRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()),
									getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()).add(
											getFinanceScheduleDetail().getSuplRent()).add(getFinanceScheduleDetail().getIncrCost()),closingBal,
									isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", colorClass,0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
					/*if (getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().getMaturityDate()) != 0 
							&& this.btnChangeRepay != null && this.btnChangeRepay.isVisible()) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onRepayItemDoubleClicked");
					}*/
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

				String label = Labels.getLabel("label_listcell_capital.label");
				if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
					label = Labels.getLabel("label_listcell_BPIcapital.label");
				}

				doFillListBox(getFinanceScheduleDetail(), count, label,
						getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getCpzAmount(),
						BigDecimal.ZERO,BigDecimal.ZERO,getFinanceScheduleDetail().getClosingBalance(),isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				count = 2;
				//As confirmed with pradeep this can be removed
				/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowRvwRateEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}*/

			}

			//To show repayment details 
			if(isRepayEnquiry && repayDetailsMap != null && repayDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				setFinanceRepayments(repayDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
				for (int i = 0; i < getFinanceRepayments().size(); i++) {
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_AmountPaid.label", 
							new String[]{DateUtility.formatToLongDate(getFinanceRepayments().get(i).getFinPostDate())}),
							BigDecimal.ZERO,getFinanceRepayments().get(i).getSchdInsPaid(),getFinanceRepayments().get(i).getSchdIncrCostPaid(),getFinanceRepayments().get(i).getSchdFeePaid(),BigDecimal.ZERO,
							BigDecimal.ZERO, getFinanceRepayments().get(i).getFinSchdPftPaid(),getFinanceRepayments().get(i).getFinSchdPriPaid(), BigDecimal.ZERO,
							getFinanceRepayments().get(i).getFinSchdPftPaid().add(getFinanceRepayments().get(i).getFinSchdPriPaid()).add(
									getFinanceRepayments().get(i).getSchdFeePaid()).add(getFinanceRepayments().get(i).getSchdInsPaid()).add(getFinanceRepayments().get(i).getSchdIncrCostPaid()),
							BigDecimal.ZERO, false, isRate,  showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#330066", "color_Repayment",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
				}
			}

			//To show Penalty details 
			if(isRepayEnquiry && penaltyDetailsMap != null && penaltyDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				List<OverdueChargeRecovery> recoverys = penaltyDetailsMap.get(getFinanceScheduleDetail().getSchDate());
				for (int i = 0; i < recoverys.size(); i++) {
					if(recoverys.get(i).getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_PenaltyPaid.label", 
								new String[]{DateUtility.formatToLongDate(recoverys.get(i).getMovementDate())}),BigDecimal.ZERO,BigDecimal.ZERO ,
								BigDecimal.ZERO,BigDecimal.ZERO ,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, recoverys.get(i).getPenaltyPaid(),
								BigDecimal.ZERO,false, isRate,  false, isGrcBaseRate, isRpyBaseRate, "#FF0000", "color_RepaymentOverdue",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					}
					count = 2;
				}
				recoverys = null;
			}

			//WriteOff Details 
			if(getFinanceScheduleDetail().getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0 ||
					getFinanceScheduleDetail().getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_Writeoff.label"),
						BigDecimal.ZERO, getFinanceScheduleDetail().getWriteoffSuplRent() , getFinanceScheduleDetail().getWriteoffIncrCost(),
						getFinanceScheduleDetail().getWriteoffSchFee() ,BigDecimal.ZERO,BigDecimal.ZERO,
						getFinanceScheduleDetail().getWriteoffProfit(),getFinanceScheduleDetail().getWriteoffPrincipal(), 
						BigDecimal.ZERO,getFinanceScheduleDetail().getWriteoffProfit().add(getFinanceScheduleDetail().getWriteoffPrincipal().add(
								getFinanceScheduleDetail().getWriteoffSchFee())),
						BigDecimal.ZERO,false, false,  false, false, false, "#FF0000", "color_RepaymentOverdue",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				count = 2;
			}


			BigDecimal totalPaid = getFinanceScheduleDetail().getSchdPftPaid().add(getFinanceScheduleDetail().getSchdPriPaid()).add(
					getFinanceScheduleDetail().getSchdFeePaid());
			BigDecimal totalSchd = getFinanceScheduleDetail().getProfitSchd().add(getFinanceScheduleDetail().getPrincipalSchd()).add(getFinanceScheduleDetail().getFeeSchd());

			if(totalPaid.compareTo(BigDecimal.ZERO) > 0 && totalSchd.compareTo(totalPaid) > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_UnpaidAmount.label"),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						getFinanceScheduleDetail().getProfitSchd().subtract(getFinanceScheduleDetail().getSchdPftPaid()), 
						getFinanceScheduleDetail().getPrincipalSchd().subtract(getFinanceScheduleDetail().getSchdPriPaid()),
						BigDecimal.ZERO,totalSchd.subtract(totalPaid), BigDecimal.ZERO,
						false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#056DA1", "",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

			}

			if (getFinanceScheduleDetail().isRvwOnSchDate() || showRate) {
				if(getFinanceScheduleDetail().isRvwOnSchDate()){ 
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = true;
				showZeroEndBal = false;
				if (getFinanceScheduleDetail().getBaseRate() != null && StringUtils.equals(CalculationConstants.SCH_SPECIFIER_GRACE,getFinanceScheduleDetail().getSpecifier())) {
					isGrcBaseRate = true;
				}
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if (aFinanceMain.getMaturityDate().compareTo(getFinanceScheduleDetail().getSchDate()) != 0) {
					if (getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {

						if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0 &&  
								getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0){

							// Calculated Profit Display
							if(!getFinanceScheduleDetail().isDisbOnSchDate() && count == 1){

								String label = Labels.getLabel("label_listcell_profitCalc.label");
								if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
									label = Labels.getLabel("label_listcell_BPIAmount.label");
									if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
										label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
									}
								}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
									label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
								}
								doFillListBox(getFinanceScheduleDetail(), count, label,
										getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO , 
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
										getFinanceScheduleDetail().getClosingBalance(),false, false, showZeroEndBal,
										isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
								count++;
							}

						}

					}else if(getFinanceScheduleDetail().isCalOnIndRate()){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_reviewIndRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_IndRateAdded_label", 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, false,
								false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						count = 2;
						//As confirmed with pradeep this can be removed
						/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}*/
					}else {

						// Calculated Profit Display
						if(!getFinanceScheduleDetail().isDisbOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate() &&
								getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0 && count == 1){

							String label = Labels.getLabel("label_listcell_profitCalc.label");
							if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
								label = Labels.getLabel("label_listcell_BPIAmount.label");
								if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
									label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
								}
							}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
								label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
							}
							doFillListBox(getFinanceScheduleDetail(), count, label,
									getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO , 
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
									getFinanceScheduleDetail().getClosingBalance(),false, false, showZeroEndBal,
									isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
							count++;
						}

						String flatRateConvert = "listcell_flatRateChangeAdded_label";
						if(CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())){

							doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_flatRate.label"),
									getFinanceScheduleDetail().getActRate(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
									BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,false, isRate,
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

							//Event Description Details
							doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
									new String[]{String.valueOf(PennantApplicationUtil.formatRate(prvSchDetail.getActRate().doubleValue(),PennantConstants.rateFormate)),
											String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getActRate().doubleValue(),PennantConstants.rateFormate))}),
									BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, 
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
							flatRateConvert = "listcell_flatRateConvertChangeAdded_label";
						}

						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_reviewRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
						count = 2;

						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(prvSchDetail.getCalculatedRate().doubleValue(),PennantConstants.rateFormate)),
										String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
								BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						if(showAdvRate){
							//Advised profit rate
							doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_advisedProfitRate.label"),
									getFinanceScheduleDetail().getAdvCalRate(), BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
									BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, isEditable, isRate, 
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",3, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

							count = 2;
						}

						count = 2;
						//As confirmed with pradeep this can be removed
						/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}*/
					}
				}
			}else if(showAdvRate){
				//Advised profit rate
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_advisedProfitRate.label"),
						getFinanceScheduleDetail().getAdvCalRate(),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, true, 
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",3, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

				count = 2;
			}

			if (!getFinanceScheduleDetail().isRepayOnSchDate() && !getFinanceScheduleDetail().isPftOnSchDate() && 
					!(getFinanceScheduleDetail().isRvwOnSchDate() || showRate) && 
					StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)) {
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				if(getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {
					String colorClass = "";

					String label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					isEditable = false;

					//Checking Rollover Condition to make Closing Bal
					BigDecimal closingBal = BigDecimal.ZERO;
					if(finScheduleData.getFinanceMain().getNextRolloverDate() != null && 
							getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().getNextRolloverDate()) == 0)	{
						closingBal = getFinanceScheduleDetail().getRolloverAmount().subtract(getFinanceScheduleDetail().getCpzAmount());
					}else{
						closingBal = getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getCpzAmount());
					}

					doFillListBox(getFinanceScheduleDetail(), count, label,	getFinanceScheduleDetail().getProfitCalc(),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,closingBal,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", colorClass,0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
				}
			}

			if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
				isEditable = true;
				isRate = true;
				showZeroEndBal = false;
				if (getFinanceScheduleDetail().getBaseRate() != null && StringUtils.equals(CalculationConstants.SCH_SPECIFIER_GRACE,getFinanceScheduleDetail().getSpecifier())) {
					isGrcBaseRate = true;
				}
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if(getFinanceScheduleDetail().isCalOnIndRate()){
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_reviewIndRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate,
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_IndRateAdded_label", 
							new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,false,
							false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

					count = 2;
					//As confirmed with pradeep this can be removed
					/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}*/
				}else {

					String flatRateConvert = "listcell_flatRateAdded_label";
					BigDecimal rate = getFinanceScheduleDetail().getCalculatedRate();
					if(CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_flatRate.label"),
								getFinanceScheduleDetail().getActRate(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,false, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getActRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO,false,
								false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						flatRateConvert = "label_listcell_flatRateConvertAdded_label";
						rate = getFinanceScheduleDetail().getActRate();
					}

					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
							new String[]{String.valueOf(PennantApplicationUtil.formatRate(rate.doubleValue(),PennantConstants.rateFormate)), 
									String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, 
							BigDecimal.ZERO, BigDecimal.ZERO,false,
							false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

					//Advised profit rate
					if(showAdvRate){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_advisedProfitRate.label"),
								getFinanceScheduleDetail().getAdvCalRate(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, 
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",3, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
					}

					count = 2;
					//As confirmed with pradeep this can be removed
					/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() &&  allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}*/
				}
			}

			//Early Paid Schedule Details
			if(getFinanceScheduleDetail().getEarlyPaid().compareTo(BigDecimal.ZERO) > 0 ){

				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_EarlyPaidDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(getFinanceScheduleDetail().getEarlyPaid(),format),
								PennantAppUtil.amountFormate(getFinanceScheduleDetail().getEarlyPaidBal(),format)}),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,false,
						false, false, false, false, "","",2, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

			}else if(getFinanceScheduleDetail().getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0 ){

				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_EarlyPayBalDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(
								getFinanceScheduleDetail().getEarlyPaidBal(),format)}),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			// Rollover Schedule Details
			if (getFinanceScheduleDetail().isRolloverOnSchDate()) {

				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_Rollover.label"),
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
						getFinanceScheduleDetail().getRolloverAmount(), BigDecimal.ZERO,BigDecimal.ZERO, 
						BigDecimal.ZERO,false, false, true,false, false, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}
		}

		logger.debug("Leaving");
	}


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

		if (map.containsKey("showAdvRate")) {
			showAdvRate = (Boolean) map.get("showAdvRate");
		}

		if (map.containsKey("totalAdvPft")) {
			totalAdvPft = (BigDecimal) map.get("totalAdvPft");
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
		FinanceType financeType = getFinScheduleData().getFinanceType();
		String product = financeType.getFinCategory();

		// Schedule Fee Details existis or not Checking
		List<FeeRule> feeRuleList = getFinScheduleData().getFeeRules();
		for (int i = 0; i < feeRuleList.size(); i++) {

			FeeRule feeRule = feeRuleList.get(i);
			if(!StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE) &&
					!StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
				isSchdFee = true;
				break;
			}
		}

		// For Structured Murabaha product
		if(product.equals(FinanceConstants.PRODUCT_STRUCTMUR) ||
				(product.equals(FinanceConstants.PRODUCT_IJARAH)|| 
						product.equals(FinanceConstants.PRODUCT_FWIJARAH))|| StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())){
			addExternalCols = true;
		}

		showStepDetail = displayStepInfo;
		int count = 1;
		this.closingBal = getFinanceScheduleDetail().getClosingBalance();
		boolean isEditable = false;
		boolean isRate = false;
		boolean showZeroEndBal = false;
		boolean isGrcBaseRate = false;
		boolean isRpyBaseRate = false;
		if(accrueValue != null){

			Date lastAccrueDate = DateUtility.getAppDate();
			if((!lastRec && lastAccrueDate.compareTo(prvSchDetail.getSchDate()) >= 0 && 
					lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) < 0)  || 
					(lastRec && lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) > 0)){
				count = 3;

				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_AccrueAmount.label"),
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, accrueValue, BigDecimal.ZERO,false, false,  true, false, false, "#1D883C", "",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
				count = 1;
			}
		}

		if (lastRec) {

			isEditable = false;
			isRate = false;
			showZeroEndBal = false;
			isGrcBaseRate = false;
			isRpyBaseRate = false;
			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalPftSch.label"),
					aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft()),
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			count = 3;

			BigDecimal totalRebate = BigDecimal.ZERO;
			if(BigDecimal.ZERO.compareTo(totalAdvPft) < 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalAdvPftSch.label"),
						totalAdvPft.subtract(aFinanceMain.getTotalGracePft()),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

				totalRebate = aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft()).subtract(totalAdvPft);
			}

			if(BigDecimal.ZERO.compareTo(totalRebate) < 0){
				/*doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalRebate.label"),
						totalRebate,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false);*/
			}

			int grcDays = 0;
			boolean showGrossPft = false;
			if(aFinanceMain.isAllowGrcPeriod()){

				grcDays = DateUtility.getDaysBetween(aFinanceMain.getFinStartDate(),aFinanceMain.getGrcPeriodEndDate());
				if(grcDays > 0 && aFinanceMain.getTotalGracePft().compareTo(BigDecimal.ZERO) > 0){
					showGrossPft = true;
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalGrcPftSch.label"),
							aFinanceMain.getTotalGracePft(),BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,	BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
				}
			}
			if(aFinanceMain.getTotalCpz().compareTo(BigDecimal.ZERO) != 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalCpz.label"),
						aFinanceMain.getTotalCpz(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			if(showGrossPft){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalGrossPft.label", ""),
						aFinanceMain.getTotalGrossPft(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalRepayAmt.label", ""),
					aFinanceMain.getTotalRepayAmt(),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

			if(BigDecimal.ZERO.compareTo(totalAdvPft) != 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalAdvRepayAmt.label", ""),
						aFinanceMain.getTotalRepayAmt().subtract(aFinanceMain.getTotalProfit().subtract(
								aFinanceMain.getTotalGracePft())).add(totalAdvPft),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			if(aFinanceMain.isAllowGrcPeriod() && grcDays > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalGrcDays.label", ""),
						new BigDecimal(grcDays),BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",15, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalDays.label", ""),
					new BigDecimal(DateUtility.getDaysBetween(aFinanceMain.getFinStartDate(), 
							aFinanceMain.getMaturityDate())),BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",15, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

		} else {

			//OverdraftSchedule drop Limits

			if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())){
				String label = "";
				int droplinefrqDay = FrequencyUtil.getIntFrequencyDay(aFinanceMain.getDroplineFrq());
				int rpyFrqDay =  FrequencyUtil.getIntFrequencyDay(aFinanceMain.getRepayFrq());
				boolean isSameDropLineDate = false;
				boolean odAvailable = false;
				if(getFinScheduleData().getOverdraftScheduleDetails()!=null &&  getFinScheduleData().getOverdraftScheduleDetails().size()>0){
					for(OverdraftScheduleDetail odSchedule:getFinScheduleData().getOverdraftScheduleDetails()){

						isLimitDrop = true;

						if(odSchedule.getDroplineDate().compareTo(aFinanceMain.getFinStartDate())==0 && 
								DateUtility.compare(odSchedule.getDroplineDate(), getFinanceScheduleDetail().getDefSchdDate())==0){
							label = Labels.getLabel("label_limitOverdraft");
							limitDrop = odSchedule.getLimitDrop();
							odLimit = odSchedule.getODLimit();
							availableLimit = odSchedule.getODLimit();
							odCount = 0;
							break;
						}else if(droplinefrqDay != rpyFrqDay){
							if(isLimitIncrease){
								odSchedule = getFinScheduleData().getOverdraftScheduleDetails().get(odCount);
								odAvailable = true;
							}else if(DateUtility.compare(odSchedule.getDroplineDate(),prvSchDetail.getDefSchdDate()) > 0 && 
									DateUtility.compare(odSchedule.getDroplineDate(), getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getDroplineDate())>0){
								 if(odCount == getFinScheduleData().getOverdraftScheduleDetails().size()-2 && 
										DateUtility.compare(odSchedule.getDroplineDate(),aFinanceMain.getMaturityDate())== 0 && 
										DateUtility.compare(odSchedule.getDroplineDate(), getFinanceScheduleDetail().getDefSchdDate())==0 ){
									label = Labels.getLabel("label_LimitExpiry");
									odAvailable = true;
									isSameDropLineDate = true;
								}else if(DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate())<= 0){
									if(DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate()) == 0){
										isSameDropLineDate = true;
									}
									label = Labels.getLabel("label_LimitDrop");
									if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0){
										if(odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO)<=0){
											label = Labels.getLabel("label_LimitIncrease");
											limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
										}
									}
									odAvailable = true;
								}
							}
						}else if(DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate()) == 0){
							isSameDropLineDate = true;
							if(isLimitIncrease){
								odSchedule = getFinScheduleData().getOverdraftScheduleDetails().get(odCount);
								odAvailable = true;
							}else if(DateUtility.compare(odSchedule.getDroplineDate(), aFinanceMain.getMaturityDate()) == 0){
								label = Labels.getLabel("label_LimitExpiry");
								odAvailable = true;
							}else{
								label = Labels.getLabel("label_LimitDrop");
								if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0){
									 if(odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO)<=0){
									label = Labels.getLabel("label_LimitIncrease");
									limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
									 }
								}
								odAvailable = true;
							}
						}else{
							if(droplinefrqDay == rpyFrqDay){
								if(DateUtility.compare(odSchedule.getDroplineDate(),prvSchDetail.getDefSchdDate()) > 0 && 
										DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate())< 0){
									label = Labels.getLabel("label_LimitDrop");
									odAvailable = true;
								}
							}
						}

						if(odAvailable){
							if((odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0 && odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO)<0) || isLimitIncrease){
								limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
								availableLimit = availableLimit.add(limitIncreaseAmt);
								odLimit = odLimit.add(limitIncreaseAmt);
								limitDrop = limitIncreaseAmt;
								if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0 && odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO)<0){
									odCount = odCount +1;
								}else{
									if(isLimitIncrease){
										label = Labels.getLabel("label_LimitIncrease");
										isLimitIncrease = false;
										odCount = odCount +1;
									}
								}
							}else{
								limitDrop = odSchedule.getLimitDrop();
								odLimit = odSchedule.getODLimit();
								if(getFinanceScheduleDetail().isDisbOnSchDate() && DateUtility.compare(odSchedule.getDroplineDate(),getFinanceScheduleDetail().getDefSchdDate())== 0){
									availableLimit = odSchedule.getODLimit().subtract(getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getDisbAmount()));
								}else{
									availableLimit = odSchedule.getODLimit().subtract(getFinanceScheduleDetail().getClosingBalance());
								}
								if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0 && odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO)>0 ){
									isLimitIncrease = true;
								}
								if(DateUtility.compare(getFinanceScheduleDetail().getDefSchdDate(), aFinanceMain.getMaturityDate()) != 0){
									odCount = odCount +1;
								}else{
									odCount = getFinScheduleData().getOverdraftScheduleDetails().size()-1;
								}
							}
							
							break;
						}
					}
				}else{
					isLimitDrop = false;
					odLimit = getFinScheduleData().getFinanceMain().getFinAssetValue();
					availableLimit = odLimit.subtract(getFinanceScheduleDetail().getClosingBalance().
							subtract(getFinanceScheduleDetail().getDisbAmount()));
					if(DateUtility.compare(getFinanceScheduleDetail().getDefSchdDate(), getFinScheduleData().getFinanceMain().getMaturityDate())==0){
						if(getFinanceScheduleDetail().getClosingBalance().compareTo(BigDecimal.ZERO)==0){
							availableLimit = BigDecimal.ZERO;
						}
					}
				}
				//to display the limit Increase amount only when the limit is changed
				if(!StringUtils.equals(label, Labels.getLabel("label_LimitIncrease"))){
					limitIncreaseAmt = BigDecimal.ZERO;
				}

				if(StringUtils.isNotBlank(label)){

						BigDecimal closingBalance =getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getDisbAmount());
						
						if(StringUtils.equals(label, Labels.getLabel("label_LimitIncrease")) && 
								DateUtility.compare(prvSchDetail.getSchDate(),getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getDroplineDate())==0){
							//count = 2;
						}
						doFillListBox(getFinanceScheduleDetail(), count,label,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getCpzAmount(),
								BigDecimal.ZERO,BigDecimal.ZERO,closingBalance,isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",0, null,false,limitIncreaseAmt,limitDrop,availableLimit,odLimit, true);
						count = 1;

						if( isSameDropLineDate || DateUtility.compare(finScheduleData.getFinanceMain().getFinStartDate(), getFinanceScheduleDetail().getDefSchdDate()) == 0){
							count = 2;
						}
				}
			}

			if (getFinanceScheduleDetail().isPftOnSchDate() && !(getFinanceScheduleDetail().isRepayOnSchDate() ||
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

				String label = Labels.getLabel("label_listcell_profitCalc.label");
				if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
					label = Labels.getLabel("label_listcell_BPIAmount.label");
					if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
						label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
					}
				}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
					label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
				}

				doFillListBox(getFinanceScheduleDetail(), count, label,
						getFinanceScheduleDetail().getProfitCalc(),getFinanceScheduleDetail().getSuplRent(),getFinanceScheduleDetail().getIncrCost(),
						getFinanceScheduleDetail().getFeeSchd(),getFinanceScheduleDetail().getTDSAmount() == null ? BigDecimal.ZERO :getFinanceScheduleDetail().getTDSAmount() , 
								getFinanceScheduleDetail().getAdvProfit(), getFinanceScheduleDetail().getProfitSchd(), 
								getFinanceScheduleDetail().getPrincipalSchd(),getFinanceScheduleDetail().getAdvRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()),
								getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()).add(
										getFinanceScheduleDetail().getSuplRent()).add(getFinanceScheduleDetail().getIncrCost()), 
								getFinanceScheduleDetail().getClosingBalance(),isEditable, isRate, showZeroEndBal,
								isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				count = 2;
				//As confirmed with pradeep this can be removed
				/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowRvwRateEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}*/
			}else if(!getFinanceScheduleDetail().isPftOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate() && 
					!getFinanceScheduleDetail().isRvwOnSchDate() && !getFinanceScheduleDetail().isDisbOnSchDate()){

				if(prvSchDetail.getCalculatedRate().compareTo(getFinanceScheduleDetail().getCalculatedRate()) == 0 &&
						getFinanceScheduleDetail().getClosingBalance().compareTo(BigDecimal.ZERO) != 0){
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_profitCalc.label"),
							getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO , 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
							getFinanceScheduleDetail().getClosingBalance(),false, false, false,
							false, false, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
				}
			}

			if (getFinanceScheduleDetail().isDisbOnSchDate()) {
				isEditable = true;
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				BigDecimal curTotDisbAmt = BigDecimal.ZERO;
				for (int i = 0; i < getFinScheduleData().getDisbursementDetails().size(); i++) {

					FinanceDisbursement curDisb = getFinScheduleData().getDisbursementDetails().get(i);
					if(DateUtility.compare(curDisb.getDisbDate(), getFinanceScheduleDetail().getSchDate()) == 0){
						curTotDisbAmt = curTotDisbAmt.add(curDisb.getDisbAmount());
						//Subtract the available amount with the curDisb amount
						availableLimit = availableLimit.subtract(curDisb.getDisbAmount());
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_disbursement.label")+" (Seq : "+curDisb.getDisbSeq()+")",
								getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO, curDisb.getDisbAmount(),getFinanceScheduleDetail().getClosingBalance().subtract(
										getFinanceScheduleDetail().getFeeChargeAmt() == null ? BigDecimal.ZERO : getFinanceScheduleDetail().getFeeChargeAmt()).subtract(
												getFinanceScheduleDetail().getInsuranceAmt() == null ? BigDecimal.ZERO : getFinanceScheduleDetail().getInsuranceAmt()).subtract(
														getFinanceScheduleDetail().getDisbAmount()).add(curTotDisbAmt)
								.add(getFinanceScheduleDetail().getDownPaymentAmount()),isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

						count = 2;
					}
				}

				if (getFinanceScheduleDetail().isDownpaymentOnSchDate()) {
					isEditable = false;
					isRate = false;
					showZeroEndBal = false;
					isGrcBaseRate = false;
					isRpyBaseRate = false;

					BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_downPayment.label"),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO ,BigDecimal.ZERO , BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
							getFinanceScheduleDetail().getDownPaymentAmount(),getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt),isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				}

				// Fee Charge Details
				if(feeRuleDetailsMap != null && getFinanceScheduleDetail().getFeeChargeAmt() != null &&
						getFinanceScheduleDetail().getFeeChargeAmt().compareTo(BigDecimal.ZERO) >= 0){

					if(feeRuleDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())){
						List<FeeRule> feeList = new ArrayList<FeeRule>(feeRuleDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
						feeList = sortFeeRules(feeList);

						BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();
						BigDecimal insuranceAmt = getFinanceScheduleDetail().getInsuranceAmt();

						for (FeeRule feeRule : feeList) {

							if(StringUtils.equals(feeRule.getFeeMethod(),CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){

								BigDecimal actFeeCharge = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());
								if(actFeeCharge.compareTo(BigDecimal.ZERO) >= 0){

									doFillListBox(getFinanceScheduleDetail(), count, feeRule.getFeeCodeDesc(),BigDecimal.ZERO,BigDecimal.ZERO,
											BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO , BigDecimal.ZERO, actFeeCharge, actFeeCharge,
											getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt).subtract(insuranceAmt).add(actFeeCharge),
											false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,true,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

									feeChargeAmt = feeChargeAmt.subtract(actFeeCharge);
								}
							}
						}
					}
				}

				// Insurance Details
				if(getFinanceScheduleDetail().getInsuranceAmt() != null &&
						getFinanceScheduleDetail().getInsuranceAmt().compareTo(BigDecimal.ZERO) > 0){

					BigDecimal insuranceAmt = getFinanceScheduleDetail().getInsuranceAmt();
					for (FinInsurances insurance : getFinScheduleData().getFinInsuranceList()) {

						if(StringUtils.equals(insurance.getPaymentMethod(),InsuranceConstants.PAYTYPE_ADD_DISB)){

							BigDecimal actInsuranceAmt = insurance.getAmount();
							if(actInsuranceAmt.compareTo(BigDecimal.ZERO) > 0){

								doFillListBox(getFinanceScheduleDetail(), count, insurance.getInsuranceTypeDesc() +"("+insurance.getInsReference()+")",BigDecimal.ZERO,BigDecimal.ZERO,
										BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO , BigDecimal.ZERO, actInsuranceAmt, actInsuranceAmt,
										getFinanceScheduleDetail().getClosingBalance().subtract(insuranceAmt).add(actInsuranceAmt),
										false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217","color_Disbursement",0, null,true,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

								insuranceAmt = insuranceAmt.subtract(actInsuranceAmt);
							}
						}
					}
				}

				//Event Description Details
				int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
				if (getFinScheduleData().getFinanceType().getFinCategory().equals(FinanceConstants.PRODUCT_ISTISNA)) {
					if (getFinScheduleData().getDisbursementDetails()!=null) {
						for (FinanceDisbursement disbursement : getFinScheduleData().getDisbursementDetails()) {
							if (getFinanceScheduleDetail().getSchDate().compareTo(disbursement.getDisbDate()) == 0) {
								String remarks = "";
								if ("B".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Billing " + " - ";
								} else if ("A".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Advance " + " - ";
								} else if ("E".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Expense " + " - ";
								} else if ("C".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks = remarks + " Consultancy Fee " + " - ";
								}
								if ("B".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))) {
									remarks =remarks+ disbursement.getDisbRemarks() + " " + PennantAppUtil.amountFormate(disbursement.getDisbClaim(),format);
								} else {
									remarks =remarks+ disbursement.getDisbRemarks() + " " + PennantAppUtil.amountFormate(disbursement.getDisbAmount(),format);
								}
								doFillListBox(getFinanceScheduleDetail(), 2, remarks, BigDecimal.ZERO,BigDecimal.ZERO ,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, 
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,  false, false, false, false, false, "", "", 2, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
							}

						}
					}
				}

				/*if (this.btnAddDisbursement != null && this.btnAddDisbursement.isVisible() && 
						getFinScheduleData().getFinanceType().isFinIsAlwMD()) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisburseItemDoubleClicked");
				}*/

			}

			if (getFinanceScheduleDetail().isRepayOnSchDate() ||
					(getFinanceScheduleDetail().isPftOnSchDate() && getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				isRate = false;
				showZeroEndBal = true;
				isGrcBaseRate = false;
				isRpyBaseRate = false;
				if(!(getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0)) {
					String colorClass = "";

					String label = Labels.getLabel("label_listcell_repay.label");
					if(getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0 &&
							(StringUtils.equals(aFinanceMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY) || 
									StringUtils.equals(aFinanceMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_GRCENDPAY))){
						label = Labels.getLabel("label_listcell_profitCalc.label");
					}
					if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
							label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
						}
					}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					}

					isEditable = true;

					//Checking Rollover Condition to make Closing Bal
					BigDecimal closingBal = BigDecimal.ZERO;
					if(finScheduleData.getFinanceMain().getNextRolloverDate() != null && 
							getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().getNextRolloverDate()) == 0)	{
						closingBal = getFinanceScheduleDetail().getRolloverAmount().subtract(getFinanceScheduleDetail().getCpzAmount());
					}else{
						closingBal = getFinanceScheduleDetail().getClosingBalance().subtract(getFinanceScheduleDetail().getCpzAmount());
					}

					doFillListBox(getFinanceScheduleDetail(), count, label,	getFinanceScheduleDetail().getProfitCalc(),
							getFinanceScheduleDetail().getSuplRent(),getFinanceScheduleDetail().getIncrCost(),getFinanceScheduleDetail().getFeeSchd(),
							getFinanceScheduleDetail().getTDSAmount() == null ? BigDecimal.ZERO :getFinanceScheduleDetail().getTDSAmount(),
									getFinanceScheduleDetail().getAdvProfit(), getFinanceScheduleDetail().getProfitSchd(), getFinanceScheduleDetail().getPrincipalSchd(),
									getFinanceScheduleDetail().getAdvRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()),
									getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd()).add(
											getFinanceScheduleDetail().getSuplRent()).add(getFinanceScheduleDetail().getIncrCost()),closingBal,
									isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", colorClass,0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
					/*if (getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().getMaturityDate()) != 0 
							&& this.btnChangeRepay != null && this.btnChangeRepay.isVisible()) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onRepayItemDoubleClicked");
					}*/
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

				String label = Labels.getLabel("label_listcell_capital.label");
				if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
					label = Labels.getLabel("label_listcell_BPIcapital.label");
				}

				doFillListBox(getFinanceScheduleDetail(), count, label,
						getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getCpzAmount(),
						BigDecimal.ZERO,BigDecimal.ZERO,getFinanceScheduleDetail().getClosingBalance(),isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				count = 2;
				//As confirmed with pradeep this can be removed
				/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && getFinanceScheduleDetail().isRvwOnSchDate() &&
						getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
						allowRvwRateEdit) {
					ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
				}*/

			}

			//To show repayment details 
			if(isRepayEnquiry && repayDetailsMap != null && repayDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				setFinanceRepayments(repayDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
				for (int i = 0; i < getFinanceRepayments().size(); i++) {
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_AmountPaid.label", 
							new String[]{DateUtility.formatToLongDate(getFinanceRepayments().get(i).getFinPostDate())}),
							BigDecimal.ZERO,getFinanceRepayments().get(i).getSchdInsPaid(),getFinanceRepayments().get(i).getSchdIncrCostPaid(),getFinanceRepayments().get(i).getSchdFeePaid(),BigDecimal.ZERO,
							BigDecimal.ZERO, getFinanceRepayments().get(i).getFinSchdPftPaid(),getFinanceRepayments().get(i).getFinSchdPriPaid(), BigDecimal.ZERO,
							getFinanceRepayments().get(i).getFinSchdPftPaid().add(getFinanceRepayments().get(i).getFinSchdPriPaid()).add(
									getFinanceRepayments().get(i).getSchdFeePaid()).add(getFinanceRepayments().get(i).getSchdInsPaid()).add(getFinanceRepayments().get(i).getSchdIncrCostPaid()),
							BigDecimal.ZERO, false, isRate,  showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#330066", "color_Repayment",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					count = 2;
				}
			}

			//To show Penalty details 
			if(isRepayEnquiry && penaltyDetailsMap != null && penaltyDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				List<OverdueChargeRecovery> recoverys = penaltyDetailsMap.get(getFinanceScheduleDetail().getSchDate());
				for (int i = 0; i < recoverys.size(); i++) {
					if(recoverys.get(i).getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_PenaltyPaid.label", 
								new String[]{DateUtility.formatToLongDate(recoverys.get(i).getMovementDate())}),BigDecimal.ZERO,BigDecimal.ZERO ,
								BigDecimal.ZERO,BigDecimal.ZERO ,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, recoverys.get(i).getPenaltyPaid(),
								BigDecimal.ZERO,false, isRate,  false, isGrcBaseRate, isRpyBaseRate, "#FF0000", "color_RepaymentOverdue",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
					}
					count = 2;
				}
				recoverys = null;
			}

			//WriteOff Details 
			if(getFinanceScheduleDetail().getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0 ||
					getFinanceScheduleDetail().getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_Writeoff.label"),
						BigDecimal.ZERO, getFinanceScheduleDetail().getWriteoffSuplRent() , getFinanceScheduleDetail().getWriteoffIncrCost(),
						getFinanceScheduleDetail().getWriteoffSchFee() ,BigDecimal.ZERO,BigDecimal.ZERO,
						getFinanceScheduleDetail().getWriteoffProfit(),getFinanceScheduleDetail().getWriteoffPrincipal(), 
						BigDecimal.ZERO,getFinanceScheduleDetail().getWriteoffProfit().add(getFinanceScheduleDetail().getWriteoffPrincipal().add(
								getFinanceScheduleDetail().getWriteoffSchFee())),
						BigDecimal.ZERO,false, false,  false, false, false, "#FF0000", "color_RepaymentOverdue",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
				count = 2;
			}


			BigDecimal totalPaid = getFinanceScheduleDetail().getSchdPftPaid().add(getFinanceScheduleDetail().getSchdPriPaid()).add(
					getFinanceScheduleDetail().getSchdFeePaid());
			BigDecimal totalSchd = getFinanceScheduleDetail().getProfitSchd().add(getFinanceScheduleDetail().getPrincipalSchd()).add(getFinanceScheduleDetail().getFeeSchd());

			if(totalPaid.compareTo(BigDecimal.ZERO) > 0 && totalSchd.compareTo(totalPaid) > 0){
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_UnpaidAmount.label"),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						getFinanceScheduleDetail().getProfitSchd().subtract(getFinanceScheduleDetail().getSchdPftPaid()), 
						getFinanceScheduleDetail().getPrincipalSchd().subtract(getFinanceScheduleDetail().getSchdPriPaid()),
						BigDecimal.ZERO,totalSchd.subtract(totalPaid), BigDecimal.ZERO,
						false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#056DA1", "",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);

			}

			if (getFinanceScheduleDetail().isRvwOnSchDate() || showRate) {
				if(getFinanceScheduleDetail().isRvwOnSchDate()){ 
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = true;
				showZeroEndBal = false;
				if (getFinanceScheduleDetail().getBaseRate() != null && StringUtils.equals(CalculationConstants.SCH_SPECIFIER_GRACE,getFinanceScheduleDetail().getSpecifier())) {
					isGrcBaseRate = true;
				}
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if (aFinanceMain.getMaturityDate().compareTo(getFinanceScheduleDetail().getSchDate()) != 0) {
					if (getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {

						if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0 &&  
								getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0){

							// Calculated Profit Display
							if(!getFinanceScheduleDetail().isDisbOnSchDate() && count == 1){

								String label = Labels.getLabel("label_listcell_profitCalc.label");
								if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
									label = Labels.getLabel("label_listcell_BPIAmount.label");
									if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
										label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
									}
								}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
									label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
								}
								doFillListBox(getFinanceScheduleDetail(), count, label,
										getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO , 
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
										getFinanceScheduleDetail().getClosingBalance(),false, false, showZeroEndBal,
										isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
								count++;
							}

						}

					}else if(getFinanceScheduleDetail().isCalOnIndRate()){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_reviewIndRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_IndRateAdded_label", 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, false,
								false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						count = 2;
						//As confirmed with pradeep this can be removed
						/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}*/
					}else {


						// Calculated Profit Display
						if(!getFinanceScheduleDetail().isDisbOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate() && 
								getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0 && count == 1){

							String label = Labels.getLabel("label_listcell_profitCalc.label");
							if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
								label = Labels.getLabel("label_listcell_BPIAmount.label");
								if(getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
									label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate())});
								}
							}else if(StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
								label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
							}

							doFillListBox(getFinanceScheduleDetail(), count, label,
									getFinanceScheduleDetail().getProfitCalc(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO , 
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
									getFinanceScheduleDetail().getClosingBalance(),false, false, showZeroEndBal,
									isGrcBaseRate, isRpyBaseRate, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,availableLimit,odLimit, false);
							count++;
						}

						String flatRateConvert = "listcell_flatRateChangeAdded_label";
						if(CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())){

							doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_flatRate.label"),
									getFinanceScheduleDetail().getActRate(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
									BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,false, isRate,
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

							//Event Description Details
							doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
									new String[]{String.valueOf(PennantApplicationUtil.formatRate(prvSchDetail.getActRate().doubleValue(),PennantConstants.rateFormate)),
											String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getActRate().doubleValue(),PennantConstants.rateFormate))}),
									BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, 
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
							flatRateConvert = "listcell_flatRateConvertChangeAdded_label";
							count = 2;
						}

						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_reviewRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
						count = 2;

						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(prvSchDetail.getCalculatedRate().doubleValue(),PennantConstants.rateFormate)),
										String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
								BigDecimal.ZERO, BigDecimal.ZERO, false,
								false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						if(showAdvRate){
							//Advised profit rate
							doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_advisedProfitRate.label"),
									getFinanceScheduleDetail().getAdvCalRate(), BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
									BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, isEditable, isRate, 
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",3, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

							count = 2;
						}

						count = 2;
						//As confirmed with pradeep this can be removed
						/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit && getFinanceScheduleDetail().isRvwOnSchDate()) {
							ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
						}*/
					}
				}
			}else if(showAdvRate){
				//Advised profit rate
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_advisedProfitRate.label"),
						getFinanceScheduleDetail().getAdvCalRate(),BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, true, 
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",3, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

				count = 2;
			}

			if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
				isEditable = true;
				isRate = true;
				showZeroEndBal = false;
				if (getFinanceScheduleDetail().getBaseRate() != null && StringUtils.equals(CalculationConstants.SCH_SPECIFIER_GRACE,getFinanceScheduleDetail().getSpecifier())) {
					isGrcBaseRate = true;
				}
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if(getFinanceScheduleDetail().isCalOnIndRate()){
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_reviewIndRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate,
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_IndRateAdded_label", 
							new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,false,
							false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

					count = 2;
					//As confirmed with pradeep this can be removed
					/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}*/
				}else {

					String flatRateConvert = "listcell_flatRateAdded_label";
					BigDecimal rate = getFinanceScheduleDetail().getCalculatedRate();
					if(CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_flatRate.label"),
								getFinanceScheduleDetail().getActRate(), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,false, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						//Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
								new String[]{String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getActRate().doubleValue(),PennantConstants.rateFormate))}),
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO,false,
								false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);

						flatRateConvert = "label_listcell_flatRateConvertAdded_label";
						rate = getFinanceScheduleDetail().getActRate();
					}

					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
							getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,isEditable, isRate, 
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);
					count = 2;

					//Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, 
							new String[]{String.valueOf(PennantApplicationUtil.formatRate(rate.doubleValue(),PennantConstants.rateFormate)), 
									String.valueOf(PennantApplicationUtil.formatRate(getFinanceScheduleDetail().getCalculatedRate().doubleValue(),PennantConstants.rateFormate))}),
							BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, 
							BigDecimal.ZERO, BigDecimal.ZERO,false,
							false, false, false, false, "","",5, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

					//Advised profit rate
					if(showAdvRate){
						doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_advisedProfitRate.label"),
								getFinanceScheduleDetail().getAdvCalRate(), BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
								BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, 
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585","color_ReviewRate",3, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
					}

					count = 2;
					//As confirmed with pradeep this can be removed 
					/*if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() &&  allowRvwRateEdit) {
						ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked");
					}*/
				}
			}

			//Early Paid Schedule Details
			int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
			if(getFinanceScheduleDetail().getEarlyPaid().compareTo(BigDecimal.ZERO) > 0 ){

				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_EarlyPaidDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(getFinanceScheduleDetail().getEarlyPaid(),format),
								PennantAppUtil.amountFormate(getFinanceScheduleDetail().getEarlyPaidBal(),format)}),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,false,
						false, false, false, false, "","",2, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

			}else if(getFinanceScheduleDetail().getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0 ){

				//Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel("label_listcell_EarlyPayBalDetailsAdded_label", 
						new String[]{PennantAppUtil.amountFormate(
								getFinanceScheduleDetail().getEarlyPaidBal(),format)}),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
						false, false, false, false, "","",2, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
			}

			// Rollover Schedule Details
			if (getFinanceScheduleDetail().isRolloverOnSchDate()) {

				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_Rollover.label"),
						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO,
						getFinanceScheduleDetail().getRolloverAmount(), BigDecimal.ZERO,BigDecimal.ZERO, 
						BigDecimal.ZERO,false, false, true,false, false, "","",0, null,false,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, false);
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
	public void doFillListBox(FinanceScheduleDetail data, int count, String eventName, BigDecimal pftAmount, BigDecimal suplRent,
			BigDecimal incrCost,BigDecimal feeAmount,BigDecimal tdsAmount,
			BigDecimal schdlAdvPft, BigDecimal schdlPft, BigDecimal cpzAmount, BigDecimal totalAdvAmount, BigDecimal totalAmount,
			BigDecimal endBal,boolean isEditable, boolean isRate,  boolean showZeroEndBal, 
			boolean isGrcBaseRate, boolean isRpyBaseRate, String bgColor, String lcColor, int fillType, Date progClaimDate, 
			boolean isFee,BigDecimal limitIncreaseAmt,BigDecimal limitDrop,BigDecimal availableLimit,BigDecimal odLimit, boolean isDropLine) {
		logger.debug("Entering");
		listitem = new Listitem();
		Listcell lc = null;
		String strDate = "";
		//String rate = "";
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		FinanceType finType = getFinScheduleData().getFinanceType();
		boolean isODSchdLimit = false;
		int finFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		OverdraftScheduleDetail odSchd = null;

		if (count == 1 && !isDropLine) {
			if (lastRec) {
				strDate = Labels.getLabel("listcell_summary.label");
			}else{

				if (data.isRvwOnSchDate() || data.getSchDate().compareTo(financeMain.getFinStartDate()) == 0) {
					strDate = DateUtility.formatToLongDate(data.getDefSchdDate()) + " [R]";
				} else {
					strDate = DateUtility.formatToLongDate(data.getDefSchdDate());
				}	

				if(StringUtils.equals(data.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)){
					if(DateUtility.compare(data.getDefSchdDate(), data.getSchDate()) != 0 && 
							data.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
						if (data.isRvwOnSchDate() || data.getSchDate().compareTo(financeMain.getFinStartDate()) == 0) {
							strDate = DateUtility.formatToLongDate(data.getSchDate()) + " [R]";
						} else {
							strDate = DateUtility.formatToLongDate(data.getSchDate());
						}	
					}
				}
			}
		}else if(isDropLine && !lastRec){
				odSchd =  getFinScheduleData().getOverdraftScheduleDetails().get(odCount);
				 strDate = DateUtility.formatToLongDate(odSchd.getDroplineDate());
				 isODSchdLimit = true;
		}

		//Progress Claim Date for Billing ISTISNA
		if(fillType == 2 && progClaimDate != null){
			strDate = DateUtility.formatToLongDate(progClaimDate);
		}

		//Color Cell
		lc = new Listcell();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setWidth("10px");
		space.setStyle(getTermColor(lcColor));
		hbox.appendChild(space);
		if(count == 1){
			hbox.appendChild(new Label(String.valueOf((data.getInstNumber() == 0 || lastRec )? "" : data.getInstNumber())));
		}
		lc.appendChild(hbox);
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

		boolean tdsApplicable = financeMain.isTDSApplicable();
		int colSpan = 0;

		if(fillType == 2){
			if (financeMain.isStepFinance() && showStepDetail) {
				if(isSchdFee){
					if(tdsApplicable){
						colSpan = 12;
					}else{
						colSpan = 11;
					}
				}else{
					if(tdsApplicable){
						colSpan = 11;
					}else{
						colSpan = 10;
					}
				}
			} else {
				if(isSchdFee){
					if(addExternalCols){
						if(tdsApplicable){
							colSpan = 11;
						}else{
							colSpan = 10;
						}
					}else{
						if(tdsApplicable){
							colSpan = 9;
						}else{
							colSpan = 8;
						}
					}
				}else{
					if(addExternalCols){
						if(tdsApplicable){
							colSpan = 10;
						}else{
							colSpan = 9;
						}
					}else{
						if(tdsApplicable){
							colSpan = 8;
						}else{
							colSpan = 7;
						}
					}
				}
			}
		}

		if(colSpan > 0){
			lc.setSpan(colSpan+5);
		}
		listitem.appendChild(lc);

		// Amounts array
		BigDecimal amountlist[] = { pftAmount, suplRent, incrCost, feeAmount,tdsAmount, schdlAdvPft, schdlPft,
				cpzAmount, totalAdvAmount,schdlPft.subtract(schdlAdvPft),totalAmount, endBal,limitIncreaseAmt,limitDrop,availableLimit,odLimit};

		if(fillType == 1){
			lc = new Listcell(String.valueOf(amountlist[0].intValue()));
			lc.setStyle("text-align:right;");
			listitem.appendChild(lc);
			lc = new Listcell();
			if(financeMain.isStepFinance() && showStepDetail) {
				if(isSchdFee){
					if(tdsApplicable){
						colSpan = 9;
					}else{
						colSpan = 8;
					}
				}else{
					if(tdsApplicable){
						colSpan = 8;
					}else{
						colSpan = 7;
					}
				}
			}  else {
				if(isSchdFee){
					if(tdsApplicable){
						colSpan = 6;
					}else{
						colSpan = 5;
					}
				}else{
					if(tdsApplicable){
						colSpan = 5;
					}else{
						colSpan = 4;
					}
				}
			}
			if(colSpan > 0){
				lc.setSpan(colSpan+5);
			}
			listitem.appendChild(lc);
		}else if(fillType == 2){
			//Nothing todo
		}else{
			// Append amount listcells to listitem
			for (int i = 0; i < amountlist.length; i++) {
				if (amountlist[i].compareTo(BigDecimal.ZERO) != 0) {
					if (isRate) { // Append % symbol if rate and format using rate format
						//rate = PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate);
						String baseRate = data.getBaseRate();
						String splRate = StringUtils.trimToEmpty(data.getSplRate());
						BigDecimal marginRate = data.getMrgRate() == null ? BigDecimal.ZERO:data.getMrgRate();

						if(fillType == 3){
							baseRate = data.getAdvBaseRate();
							splRate = "";
							marginRate = data.getAdvMargin() == null ? BigDecimal.ZERO:data.getAdvMargin();
						}

						String mrgRate = PennantApplicationUtil.formatRate(marginRate.doubleValue(), 2);
						if ((isGrcBaseRate || fillType == 3)  && (data.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE) ||
								data.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {

							if(StringUtils.isBlank(baseRate)){
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}else{
								lc = new Listcell("[ " +baseRate+(StringUtils.isEmpty(splRate)?"":","+splRate)+
										(StringUtils.isEmpty(mrgRate)?"":","+mrgRate)+" ]"+
										PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}
							lc.setStyle("text-align:right;color:"+bgColor+";");
							if(!isEditable) {
								lc.setStyle("text-align:right;color:"+bgColor+";cursor:default;");
							}
						} else if ((isRpyBaseRate || fillType == 3) && (data.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_REPAY) || 
								data.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {

							if(StringUtils.isBlank(baseRate)){
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(), PennantConstants.rateFormate) + "%");
							}else{
								lc = new Listcell("[ " +baseRate+(StringUtils.isEmpty(splRate)?"":","+splRate)+
										(StringUtils.isEmpty(mrgRate)?"":","+mrgRate)+" ]"+
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
							lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i],finFormatter));
						} 

						if(StringUtils.isNotEmpty(bgColor)) {
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
				}else if(amountlist[i].compareTo(BigDecimal.ZERO) > 0 && (i == 12)){
					if(finType.isDroplineOD() && fillType==0 && !lastRec &&  isODSchdLimit  && limitIncreaseAmt.compareTo(BigDecimal.ZERO)>0){

						lc = new Listcell(PennantAppUtil.amountFormate(limitIncreaseAmt, finFormatter));
						lc.setStyle("text-align:right;font-weight: bold;color:#F87217;");
					}else{
						lc = new Listcell("");
						lc.setStyle("text-align:right;"); 
					}
				}else if(amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 13)){
					if(finType.isDroplineOD() && fillType==0 && !lastRec &&  isODSchdLimit){
						lc = new Listcell(PennantAppUtil.amountFormate(limitDrop, finFormatter));
						if(odSchd.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0){
							lc.setStyle("text-align:right;color:"+bgColor+";");
						}else{
							lc.setStyle("text-align:right;font-weight: bold;color:#F87217;");
						}

					}else{
						lc = new Listcell("");
						lc.setStyle("text-align:right;color:"+bgColor+";");
					}
				}else if(amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 14)){
					if(fillType==0  && !lastRec  && (isLimitDrop|| count ==1||(data.isDisbOnSchDate() && data.isRepayOnSchDate()))){

						lc = new Listcell(PennantAppUtil.amountFormate(availableLimit, finFormatter));
						lc.setStyle("text-align:right;");
						if(!isEditable) {
							lc.setStyle("text-align:right;cursor:default;");
						}
						isLimitDrop = false;
						if(availableLimit.compareTo(BigDecimal.ZERO)<0){
							lc.setStyle("text-align:right;font-weight: bold;color:#F87217;");
						}
					}else{
						lc = new Listcell("");
						lc.setStyle("text-align:right;"); 
						if(availableLimit.compareTo(BigDecimal.ZERO)<0){
							lc.setStyle("text-align:right;font-weight: bold;color:#F87217;");
						}
					}
				}else if(amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 15)){
					if(fillType==0 && !lastRec && showZeroEndBal &&(isLimitDrop || count ==1 ||(data.isDisbOnSchDate() && data.isRepayOnSchDate()) )){

						lc = new Listcell(PennantAppUtil.amountFormate(odLimit, finFormatter));
						lc.setStyle("text-align:right;");
						if(!isEditable) {
							lc.setStyle("text-align:right;cursor:default;");
						}
						isLimitDrop = false;
					}else{
						lc = new Listcell("");
						lc.setStyle("text-align:right;"); 
					}
				}else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && i == amountlist.length - 1 &&
						!lastRec && showZeroEndBal) {
					lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], finFormatter));
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				}else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 1 || i == 2 || i == 3 || 
						(i == 11)) && showZeroEndBal) {
					if(fillType == 5){
						lc = new Listcell("");
					}else{
						lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], finFormatter));
					}
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && ( i == 9) && isFee) {
					lc = new Listcell(PennantAppUtil.amountFormate(amountlist[i], finFormatter));
					if(StringUtils.isNotEmpty(bgColor)) {
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
				}else {
					lc = new Listcell("");
					lc.setStyle("text-align:right;");
					if(!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				}
				listitem.appendChild(lc);
			}

			// For Planned EMI Holiday Dates
			boolean alwEMIHoliday = true;
			Date planEMIHStart = DateUtility.addMonths(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate(),
					getFinScheduleData().getFinanceMain().getPlanEMIHLockPeriod());
			if(DateUtility.compare(data.getSchDate(), planEMIHStart) <= 0 || 
					data.getInstNumber() == 1){
				alwEMIHoliday = false;
			}

			if (!isRate && !lastRec && (data.isRepayOnSchDate() || 
					StringUtils.equals(data.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) && 
					data.getClosingBalance().compareTo(BigDecimal.ZERO) != 0 && alwEMIHoliday) {

				lc = new Listcell();
				Checkbox planEMIHDate = new Checkbox();
				planEMIHDate.setDisabled(isEMIHEditable);

				if(!isEMIHEditable){
					List<Object> dataList = new ArrayList<>();
					dataList.add(planEMIHDate);
					dataList.add(getFinanceScheduleDetail().getSchDate());
					planEMIHDate.addForward("onCheck", this.window, "onCheckPlanEMIHDate", dataList);
				}
				if(StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC) &&
						StringUtils.equals(data.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)){
					planEMIHDate.setChecked(true);
				}
				lc.setStyle("text-align:center;cursor:default;");
				lc.appendChild(planEMIHDate);
			}else{
				lc = new Listcell("");
			}
			listitem.appendChild(lc);

			// for Cash Flow Effect value
			if (financeMain.isStepFinance() && showStepDetail) {
				if (!isRate && !lastRec) {
					lc = new Listcell(PennantAppUtil.amountFormate(data.getOrgPft(), finFormatter));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);

				// for Vs Profit value
				if (!isRate && !lastRec) {
					lc = new Listcell(PennantAppUtil.amountFormate(data.getOrgPri(), finFormatter));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);

				// for Original Principal Due value
				if (!isRate && !lastRec) {
					lc = new Listcell(PennantAppUtil.amountFormate(data.getOrgEndBal(), finFormatter));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);
			}

			// if the schedule specifier is grace end then don't display the tooltip text
			if (isEditable && !lastRec) {
				/*if (isRate && this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled()) { 
					// Append rate to tooltip text without formating
					listitem.setTooltiptext(Labels.getLabel("listbox.ratechangetooltiptext") + "  " + rate);
				} else if (this.btnChangeRepay != null && !this.btnChangeRepay.isDisabled()) {
					listitem.setTooltiptext(Labels.getLabel("listbox.repayamounttooltiptext"));
				}*/
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
		Listcell lc = null;

		lc = new Listcell(DateUtility.formatToLongDate(scheduleDetail.getSchDate()));
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

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 * */
	public List<FinanceScheduleReportData> getScheduleData(FinScheduleData aFinScheduleData, 
			Map<Date,ArrayList<FinanceRepayments>> paymentDetailsMap,  Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap, 
			Map<Date,ArrayList<FeeRule>> feeRuleDetails, boolean includeSummary) {
		logger.debug("Entering");
		BigDecimal odlimitDrop = BigDecimal.ZERO;
		BigDecimal odAvailAmt = BigDecimal.ZERO;
		BigDecimal limitIncreaseAmt = BigDecimal.ZERO;
		setFinScheduleData(aFinScheduleData);
		FinanceScheduleDetail prvSchDetail = null; 
		ArrayList<FinanceScheduleReportData> reportList = new ArrayList<FinanceScheduleReportData>();
		boolean lastRec=false;
		FinanceScheduleReportData data;
		int count = 1;
		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail aScheduleDetail = getFinScheduleData().getFinanceScheduleDetails().get(i);
			this.closingBal = aScheduleDetail.getClosingBalance();
			if(aScheduleDetail.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && 
					i == aFinScheduleData.getFinanceScheduleDetails().size() - 1) {
				lastRec = true;
			}
			if (i == 0) {
				prvSchDetail = aScheduleDetail;
			} else {
				prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
			}
			if (aScheduleDetail.isDisbOnSchDate()) {

				for (int j = 0; j < 2; j++) {

					if(j == 0){
						BigDecimal curTotDisbAmt = BigDecimal.ZERO;
						for (int k = 0; k < aFinScheduleData.getDisbursementDetails().size(); k++) {

							FinanceDisbursement curDisb = aFinScheduleData.getDisbursementDetails().get(k);
							if(DateUtility.compare(curDisb.getDisbDate(), aScheduleDetail.getSchDate()) == 0){
								curTotDisbAmt = curTotDisbAmt.add(curDisb.getDisbAmount());

								data = new FinanceScheduleReportData();	
								data.setLabel(Labels.getLabel("label_listcell_disbursement.label")+"( Seq : "+curDisb.getDisbSeq()+")");
								data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
								data.setSchdPft("");
								data.setTdsAmount("");
								data.setSchdFee("");
								data.setSchdPri(formatAmt(aScheduleDetail.getCpzAmount(),false,false));
								data.setTotalAmount(formatAmt(curDisb.getDisbAmount(),false,false));
								data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance().subtract(
										aScheduleDetail.getFeeChargeAmt()==null? BigDecimal.ZERO :aScheduleDetail.getFeeChargeAmt()).subtract(
												aScheduleDetail.getDisbAmount()).add(curTotDisbAmt)
										.add(aScheduleDetail.getDownPaymentAmount()),false,false));
								data.setLimitDrop("");
								data.setLimitIncreaseAmt(formatAmt(limitIncreaseAmt,false,false));
								data.setTotalLimit(formatAmt(odAvailAmt,false,false));
								BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
								data.setAvailLimit(formatAmt(availLimit,false,false));

								if (count == 1) {
									if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
										data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
									}else {
										data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
									}
									data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
								}else{
									data.setSchDate("");
								}
								reportList.add(data);
							}
						}

						if (aScheduleDetail.isDownpaymentOnSchDate()) {

							BigDecimal feeChargeAmt = aScheduleDetail.getFeeChargeAmt();
							data = new FinanceScheduleReportData();
							data.setLabel(Labels.getLabel("label_listcell_downPayment.label"));
							if (count == 1){
								data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
								data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
							}else{
								data.setSchDate("");
							}
							data.setPftAmount("");
							data.setSchdPft("");
							data.setTdsAmount("");
							data.setSchdFee("");
							data.setSchdPri("");
							data.setLimitDrop("");
							data.setLimitIncreaseAmt("");
							data.setTotalLimit(formatAmt(odAvailAmt,false,false));
							BigDecimal odAvailLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
							data.setAvailLimit(formatAmt(odAvailLimit,false,false));
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
										data.setTdsAmount("");
										data.setSchdFee("");
										data.setLimitDrop("");
										data.setLimitIncreaseAmt("");
										data.setTotalLimit(formatAmt(odAvailAmt,false,false));
										BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
										data.setAvailLimit(formatAmt(availLimit,false,false));

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
			//overdraft facility
			if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,getFinScheduleData().getFinanceMain().getProductCategory())){

				data = new FinanceScheduleReportData();	
				FinanceMain aFinanceMain = aFinScheduleData.getFinanceMain();

				if(aFinScheduleData.getFinanceType().isDroplineOD() && getFinScheduleData().getOverdraftScheduleDetails()!=null && 
						getFinScheduleData().getOverdraftScheduleDetails().size()>0){
					for(OverdraftScheduleDetail odSchedule:getFinScheduleData().getOverdraftScheduleDetails()){

						int droplinefrqDay = FrequencyUtil.getIntFrequencyDay(aFinanceMain.getDroplineFrq());
						int rpyFrqDay =  FrequencyUtil.getIntFrequencyDay(aFinanceMain.getRepayFrq());

						if(odSchedule.getDroplineDate().compareTo(aFinanceMain.getFinStartDate())==0 && 
								DateUtility.compare(odSchedule.getDroplineDate(), aScheduleDetail.getSchDate())==0){
							data.setLabel(Labels.getLabel("label_limitOverdraft"));
							odAvailAmt = odSchedule.getODLimit();
						}else if(droplinefrqDay != rpyFrqDay){
							if(DateUtility.compare(odSchedule.getDroplineDate(),prvSchDetail.getSchDate()) > 0 && 
									DateUtility.compare(odSchedule.getDroplineDate(), getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getDroplineDate())>0){
								if(DateUtility.compare(odSchedule.getDroplineDate(),aScheduleDetail.getSchDate())<= 0){
									data.setLabel(Labels.getLabel("label_LimitDrop"));
									if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0){
										data.setLabel(Labels.getLabel("label_LimitIncrease"));
										limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
									}
									odlimitDrop = odSchedule.getLimitDrop();
									odAvailAmt = odSchedule.getODLimit();
									break;
								}else if(odCount == getFinScheduleData().getOverdraftScheduleDetails().size()-2 && 
										DateUtility.compare(odSchedule.getDroplineDate(),aFinanceMain.getMaturityDate())== 0){
									data.setLabel(Labels.getLabel("label_LimitExpiry"));
									odlimitDrop = odSchedule.getLimitDrop();
									odAvailAmt = odSchedule.getODLimit();
									break;
								}
							}
						}else if(DateUtility.compare(odSchedule.getDroplineDate(),aScheduleDetail.getSchDate()) == 0){
							if(DateUtility.compare(odSchedule.getDroplineDate(), aFinanceMain.getMaturityDate()) == 0){
								data.setLabel(Labels.getLabel("label_LimitExpiry"));
								odlimitDrop = odSchedule.getLimitDrop();
								odAvailAmt = odSchedule.getODLimit();
								break;
							}else{
								data.setLabel(Labels.getLabel("label_LimitDrop"));
								if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0){
									data.setLabel(Labels.getLabel("label_LimitIncrease"));
									limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
								}
								odlimitDrop = odSchedule.getLimitDrop();
								odAvailAmt = odSchedule.getODLimit();
								if(DateUtility.compare(aScheduleDetail.getSchDate(), aFinanceMain.getMaturityDate()) != 0){
									break;
								}
							}
						}else{
							if(droplinefrqDay == rpyFrqDay){
								if(DateUtility.compare(odSchedule.getDroplineDate(),prvSchDetail.getSchDate()) > 0 && 
										DateUtility.compare(odSchedule.getDroplineDate(),aScheduleDetail.getSchDate())< 0){
									data.setLabel(Labels.getLabel("label_LimitDrop"));
									if(odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO)>0){
										data.setLabel(Labels.getLabel("label_LimitIncrease"));
										limitIncreaseAmt = odSchedule.getLimitIncreaseAmt();
									}
									odlimitDrop = odSchedule.getLimitDrop();
									odAvailAmt = odSchedule.getODLimit();
									break;
								}
							}
						}
					}
				}

				if(data.getLabel()!=null){

					if(data.getLabel().equals(Labels.getLabel("label_LimitIncrease"))){
						data.setLimitIncreaseAmt(formatAmt(limitIncreaseAmt,false,false));
					}else{
						data.setLimitIncreaseAmt("");
					}

					data.setLimitDrop(formatAmt(odlimitDrop,false,false));
					data.setTotalLimit(formatAmt(odAvailAmt,false,false));
					BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit,false,false));

					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()){
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
						}else {
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
						}
					}else {
						data.setSchDate("");
					}

					data.setEndBal("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setPftAmount("");				
					data.setSchdPft("");				
					data.setSchdPri("");
					data.setNoOfDays("");
					data.setTotalAmount("");

					reportList.add(data);
					count = 2;
				}else{
					if(!aFinScheduleData.getFinanceType().isDroplineOD()){
						odAvailAmt = aFinanceMain.getFinAssetValue();
						data.setLimitIncreaseAmt("");
						data.setTotalLimit(formatAmt(odAvailAmt,false,false));
						BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
						data.setAvailLimit(formatAmt(availLimit,false,false));
					}
				}
			}

			if (aScheduleDetail.isPftOnSchDate() && !(aScheduleDetail.isRepayOnSchDate() ||
					(aScheduleDetail.isPftOnSchDate() && aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
				if(aScheduleDetail.getProfitCalc().compareTo(BigDecimal.ZERO) > 0){
					data = new FinanceScheduleReportData();	

					String label = Labels.getLabel("label_listcell_profitCalc.label");
					if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if(aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
							label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())});
						}
					}else if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					}

					data.setLabel(label);
					if (count == 1) {
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()) {
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
						}else {
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
						}

						if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)){
							if(DateUtility.compare(aScheduleDetail.getDefSchdDate(), aScheduleDetail.getSchDate()) != 0 && 
									aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
								if (aScheduleDetail.isRvwOnSchDate() || 
										aScheduleDetail.getSchDate().compareTo(aFinScheduleData.getFinanceMain().getFinStartDate()) == 0) {
									data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getSchDate()) + " [R]");
								} else {
									data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getSchDate()));
								}	
							}
						}

					}else {
						data.setSchDate("");
					}
					data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
					data.setTdsAmount(formatAmt(aScheduleDetail.getTDSAmount(),false,false));
					data.setSchdFee(formatAmt(aScheduleDetail.getFeeSchd(),false,false));
					data.setSchdPft(formatAmt(aScheduleDetail.getProfitSchd(),false,true));
					data.setSchdPri(formatAmt(aScheduleDetail.getPrincipalSchd(),false,true));
					data.setTotalAmount(formatAmt(aScheduleDetail.getRepayAmount(),false,false));
					data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
				}
				count = 2;
			}else if(!aScheduleDetail.isPftOnSchDate() && !aScheduleDetail.isRepayOnSchDate() && 
					!aScheduleDetail.isRvwOnSchDate() && !aScheduleDetail.isDisbOnSchDate()){

				if(prvSchDetail.getCalculatedRate().compareTo(aScheduleDetail.getCalculatedRate()) == 0){

					data = new FinanceScheduleReportData();	

					String label = Labels.getLabel("label_listcell_profitCalc.label");
					if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if(aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
							label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())});
						}
					}else if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					}

					data.setLabel(label);
					if (count == 1) {
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()) {
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
						}else {
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
						}

						if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)){
							if(DateUtility.compare(aScheduleDetail.getDefSchdDate(), aScheduleDetail.getSchDate()) != 0 && 
									aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
								if (aScheduleDetail.isRvwOnSchDate() || 
										aScheduleDetail.getSchDate().compareTo(aFinScheduleData.getFinanceMain().getFinStartDate()) == 0) {
									data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getSchDate()) + " [R]");
								} else {
									data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getSchDate()));
								}	
							}
						}

					}else {
						data.setSchDate("");
					}
					data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
					data.setSchdFee(formatAmt(aScheduleDetail.getFeeSchd(),false,false));
					data.setTdsAmount("");
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);

					count = 2;
				}
			}
			if (aScheduleDetail.isCpzOnSchDate() && aScheduleDetail.getCpzAmount().compareTo(BigDecimal.ZERO) != 0) {
				data = new FinanceScheduleReportData();	
				String label = Labels.getLabel("label_listcell_capital.label");
				if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
					label = Labels.getLabel("label_listcell_BPIcapital.label");
				}
				data.setLabel(label);
				if (count == 1){
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					if( aScheduleDetail.isRvwOnSchDate()){
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
					}else {
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
					}
				}else{
					data.setSchDate("");
				}
				data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
				data.setSchdFee(formatAmt(aScheduleDetail.getFeeSchd(),false,false));
				data.setSchdPft("");
				data.setTdsAmount("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
				data.setSchdPri(formatAmt(aScheduleDetail.getCpzAmount(),false,false));
				data.setTotalAmount("");				
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
				reportList.add(data);
				count = 2;
			}
			if (aScheduleDetail.isRepayOnSchDate() ||
					(aScheduleDetail.isPftOnSchDate() && aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				if(!(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0)) {
					data = new FinanceScheduleReportData();	

					String label = Labels.getLabel("label_listcell_repay.label");
					if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if(aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
							label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())});
						}
					}else if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					}
					data.setLabel(label);
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						if( aScheduleDetail.isRvwOnSchDate()){
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
						}else {
							data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
						}
					}else {
						data.setSchDate("");
					}
					data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,true));
					data.setSchdFee(formatAmt(aScheduleDetail.getFeeSchd(),false,false));
					data.setTdsAmount(formatAmt(aScheduleDetail.getTDSAmount(),false,false));
					data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));				
					data.setSchdPft(formatAmt(aScheduleDetail.getProfitSchd(),false,true));				
					data.setSchdPri(formatAmt(aScheduleDetail.getPrincipalSchd(),false,true));
					data.setTotalAmount(formatAmt(aScheduleDetail.getRepayAmount(),false,false));

					if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,getFinScheduleData().getFinanceMain().getProductCategory())){
						data.setLimitDrop("");
						data.setTotalLimit(formatAmt(odAvailAmt,false,false));

						if(data.getLabel().equals(Labels.getLabel("label_LimitIncrease"))){
							data.setLimitIncreaseAmt(formatAmt(limitIncreaseAmt,false,false));
						}else{
							data.setLimitIncreaseAmt("");
						}

						BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
						data.setAvailLimit(formatAmt(availLimit,false,false));
					}else{
						data.setLimitDrop("");
						data.setAvailLimit("");
						data.setTotalLimit("");
						data.setLimitIncreaseAmt("");
					}
					reportList.add(data);
					count = 2;
				}

			}
			//To show repayment details 
			if(paymentDetailsMap != null && paymentDetailsMap.containsKey(aScheduleDetail.getSchDate())) {
				setFinanceRepayments(paymentDetailsMap.get(aScheduleDetail.getSchDate()));
				for (int j = 0; j < getFinanceRepayments().size(); j++) {
					data = new FinanceScheduleReportData();	
					data.setLabel(Labels.getLabel("label_listcell_AmountPaid.label", 
							new String[]{DateUtility.formatToLongDate(getFinanceRepayments().get(j).getFinPostDate())}));
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
					}else {
						data.setSchDate("");
					}

					data.setLimitDrop("");
					data.setSchdFee("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					data.setEndBal(formatAmt(BigDecimal.ZERO,false,false));
					data.setTdsAmount("");
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
					data.setLabel(Labels.getLabel("label_listcell_PenaltyPaid.label", 
							new String[]{DateUtility.formatToLongDate(recovery.getMovementDate())}));
					if (count == 1){
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
					}else {
						data.setSchDate("");
					}

					data.setEndBal("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setPftAmount("");				
					data.setSchdPft("");				
					data.setSchdPri("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					data.setTotalAmount(formatAmt(recovery.getPenaltyPaid(),false,false));
					reportList.add(data);
					count = 2;
				}
			}

			//WriteOff Details 
			if(aScheduleDetail.getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0 ||
					aScheduleDetail.getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0){

				data = new FinanceScheduleReportData();	
				data.setLabel(Labels.getLabel("label_listcell_Writeoff.label"));
				if (count == 1){
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					if( aScheduleDetail.isRvwOnSchDate()){
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
					}else {
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
					}
				}else {
					data.setSchDate("");
				}
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,true));
				data.setTdsAmount("");
				data.setPftAmount(formatAmt(BigDecimal.ZERO,false,false));				
				data.setSchdPft(formatAmt(aScheduleDetail.getWriteoffProfit(),false,true));				
				data.setSchdPri(formatAmt(aScheduleDetail.getWriteoffPrincipal(),false,true));
				data.setTotalAmount(formatAmt(aScheduleDetail.getWriteoffPrincipal().add(aScheduleDetail.getWriteoffProfit()),false,false));
				data.setSchdFee(formatAmt(aScheduleDetail.getWriteoffSchFee(),false,false));
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
				reportList.add(data);
				count = 2;
			}

			if(aScheduleDetail.getProfitCalc().compareTo(BigDecimal.ZERO) > 0 && 
					aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0 &&
					!aScheduleDetail.isDisbOnSchDate() && count == 1){
				data = new FinanceScheduleReportData();	

				String label = Labels.getLabel("label_listcell_profitCalc.label");
				if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_BPI)){
					label = Labels.getLabel("label_listcell_BPIAmount.label");
					if(aScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
						label = Labels.getLabel("label_listcell_BPICalculated.label", new String[]{DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())});
					}
				}else if(StringUtils.equals(aScheduleDetail.getBpiOrHoliday(),FinanceConstants.FLAG_HOLIDAY)){
					label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
				}

				data.setLabel(label);
				if (count == 1) {
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					if( aScheduleDetail.isRvwOnSchDate()) {
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
					}else {
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));
					}
				}else {
					data.setSchDate("");
				}
				data.setPftAmount(formatAmt(aScheduleDetail.getProfitCalc(),false,false));
				data.setTdsAmount(formatAmt(aScheduleDetail.getTDSAmount(),false,false));
				data.setSchdFee(formatAmt(aScheduleDetail.getFeeSchd(),false,false));
				data.setSchdPft("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
				data.setEndBal(formatAmt(aScheduleDetail.getClosingBalance(),false,false));
				reportList.add(data);
				count = 2;
			}

			if (aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {

				if(CalculationConstants.RATE_BASIS_C.equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_flatRate.label"));
					data.setSchDate("");
					data.setPftAmount(formatAmt(aScheduleDetail.getActRate(),true,false));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
					count = 2;
				}

				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_reviewRate.label"));
				if(aScheduleDetail.isCalOnIndRate()){
					data.setLabel(Labels.getLabel("label_listcell_reviewIndRate.label"));
				}
				if (count == 1) {
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
				}else {
					data.setSchDate("");
				}
				data.setPftAmount(formatAmt(aScheduleDetail.getCalculatedRate(),true,false));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setTdsAmount("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
				data.setAvailLimit(formatAmt(availLimit,false,false));
				data.setTotalLimit(formatAmt(odAvailAmt,false,false));

				if(data.getLabel().equals(Labels.getLabel("label_LimitIncrease"))){
					data.setLimitIncreaseAmt(formatAmt(limitIncreaseAmt,false,false));
				}else{
					data.setLimitIncreaseAmt("");
				}

				reportList.add(data);
				count = 2;
			}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0){

				if(CalculationConstants.RATE_BASIS_C.equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_flatRate.label"));
					data.setSchDate("");
					data.setSchdFee("");
					data.setPftAmount(formatAmt(aScheduleDetail.getActRate(),true,false));
					data.setSchdPft("");
					data.setTdsAmount("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit,false,false));
					data.setTotalLimit(formatAmt(odAvailAmt,false,false));
					if(data.getLabel().equals(Labels.getLabel("label_LimitIncrease"))){
						data.setLimitIncreaseAmt(formatAmt(limitIncreaseAmt,false,false));
					}else{
						data.setLimitIncreaseAmt("");
					}

					reportList.add(data);
					count = 2;
				}

				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_reviewRate.label"));
				if(aScheduleDetail.isCalOnIndRate()){
					data.setLabel(Labels.getLabel("label_listcell_reviewIndRate.label"));
				}
				data.setSchDate("");
				data.setPftAmount(formatAmt(aScheduleDetail.getCalculatedRate(),true,false));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTdsAmount("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				if(data.getLabel().equals(Labels.getLabel("label_LimitIncrease"))){
					data.setLimitIncreaseAmt(formatAmt(limitIncreaseAmt,false,false));
				}else{
					data.setLimitIncreaseAmt("");
				}

				BigDecimal availLimit = odAvailAmt.subtract(aScheduleDetail.getClosingBalance());
				data.setAvailLimit(formatAmt(availLimit,false,false));
				data.setTotalLimit(formatAmt(odAvailAmt,false,false));
				reportList.add(data);
				count = 2;
			}else if (aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) == 0
					&& aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {

				boolean isFlatRatebasis = false;
				if(CalculationConstants.RATE_BASIS_C.equals(getFinScheduleData().getFinanceMain().getRepayRateBasis())){
					isFlatRatebasis = true;
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_flatRate.label"));
					if (count == 1) {
						data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
					}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0) {
						data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
					}else {
						data.setSchDate("");
					}
					data.setPftAmount(formatAmt(aScheduleDetail.getActRate(),true,false));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
					count = 2;
				}
				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_reviewRate.label"));
				if(aScheduleDetail.isCalOnIndRate()){
					data.setLabel(Labels.getLabel("label_listcell_reviewIndRate.label"));
				}
				if (count == 1) {
					data.setNoOfDays(String.valueOf(DateUtility.getDaysBetween(aScheduleDetail.getSchDate(), prvSchDetail.getSchDate())));
					data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
				}else if(aScheduleDetail.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0 && !isFlatRatebasis) {
					data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate())+"[R]");
				}else {
					data.setSchDate("");
				}
				data.setPftAmount(formatAmt(aScheduleDetail.getCalculatedRate(),true,false));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setTdsAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
				reportList.add(data);
				count = 2;
			} 

			count = 1;
			if(lastRec && includeSummary){
				data = new FinanceScheduleReportData();
				data.setSchDate(Labels.getLabel("listcell_summary.label"));
				data.setLabel(Labels.getLabel("label_listcell_totalPftSch.label"));
				data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalProfit().subtract(aFinScheduleData.getFinanceMain().getTotalGracePft()),false,false));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setTdsAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
				reportList.add(data);

				if(aFinScheduleData.getFinanceMain().isAllowGrcPeriod()){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("label_listcell_totalGrcPftSch.label"));
					data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalGracePft(),false,true));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTdsAmount("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
				}

				if(aFinScheduleData.getFinanceMain().getTotalCpz().compareTo(BigDecimal.ZERO) != 0){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("label_listcell_totalCpz.label"));
					data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalCpz(),false,true));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
				}

				if(aFinScheduleData.getFinanceMain().isAllowGrcPeriod()){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("label_listcell_totalGrossPft.label"));
					data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalGrossPft(),false,true));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
				}

				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("label_listcell_totalRepayAmt.label"));
				data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalRepayAmt(),false,true));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTdsAmount("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
				reportList.add(data);

				if(aFinScheduleData.getFinanceMain().isAllowGrcPeriod()){
					data = new FinanceScheduleReportData();
					data.setSchDate("");
					data.setLabel(Labels.getLabel("label_listcell_totalGrcDays.label"));
					data.setPftAmount(String.valueOf(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
							getFinScheduleData().getFinanceMain().getGrcPeriodEndDate())));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setTdsAmount("");
					data.setEndBal("");
					data.setLimitDrop("");
					data.setAvailLimit("");
					data.setTotalLimit("");
					data.setLimitIncreaseAmt("");
					reportList.add(data);
				}

				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("label_listcell_totalDays.label"));
				data.setPftAmount(String.valueOf(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(), 
						getFinScheduleData().getFinanceMain().getMaturityDate())));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTdsAmount("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitIncreaseAmt("");
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

		int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());
		int size = aFinScheduleData.getFinanceScheduleDetails().size();

		for (int i = size-1; i >=0 ; i--) {

			FinanceScheduleDetail aScheduleDetail = getFinScheduleData().getFinanceScheduleDetails().get(i);
			data = new FinanceGraphReportData();
			data.setRecordNo(i);
			data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getDefSchdDate()));

			if(i == size-1){
				data.setProfitBal(BigDecimal.ZERO);
				data.setPrincipalBal(BigDecimal.ZERO);
				data.setFinanceBal(BigDecimal.ZERO);
			}else{
				data.setFinanceBal(PennantAppUtil.formateAmount(financeBal,formatter));
				data.setPrincipalBal(PennantAppUtil.formateAmount(principalBal,formatter));
				data.setProfitBal(PennantAppUtil.formateAmount(profitBal,formatter));
			}

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

			if (curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {

				if(i == size-1){
					lastRec = true;
				}

				this.closingBal = curSchd.getClosingBalance();

				data = new FinanceScheduleReportData();	
				data.setSchdSeqNo(String.valueOf(schdSeqNo+1));
				data.setSchDate(DateUtility.formatToLongDate(curSchd.getDefSchdDate()));
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

		int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		if (amount.compareTo(BigDecimal.ZERO) != 0) {
			if (isRate) { // Append % sysmbol if rate and format using rate format
				return new BigDecimal(PennantApplicationUtil.formatRate(amount.doubleValue(), PennantConstants.rateFormate))+" % ";
			} else {
				return PennantAppUtil.amountFormate(amount, format);
			}
		} else  if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && !lastRec && showZeroEndBal) { 
			return PennantAppUtil.amountFormate(amount, format); 
		}else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && lastRec) {
			return PennantAppUtil.amountFormate(amount, format);
		}else if (amount.compareTo(BigDecimal.ZERO) == 0 && showZeroEndBal) { 
			return PennantAppUtil.amountFormate(amount, format);
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

	/* --- Color codes for Finance Schedule details --- */ 
	private String getTermColor(String lcColor){
		String color = "";
		switch (lcColor) {
		case "color_Disbursement":
			color = "background-color: #F87217";
			break;
		case "color_ReviewRate":
			color = "background-color: #E6A9EC";
			break;
		case "color_GracePeriodendDate":
			color = "background-color: #726E6D";
			break;
		case "color_LastScheduleRecord":
			color = "background-color: #726E6D";
			break;
		case "color_Deferred":
			color = "background-color: #E0FFFF";
			break;
		case "color_Repayment":
			color = "background-color: #008000";
			break;
		case "color_EarlyRepayment":
			color = "background-color: #008000";
			break;
		case "color_RepaymentOverdue":
			color = "background-color: #FF0000";
			break;

		default:
			break;
		}
		return color;
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
