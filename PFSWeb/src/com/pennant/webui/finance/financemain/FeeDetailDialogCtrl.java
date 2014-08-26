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
 * FileName    		:  FeeDetailDialogCtrl.java                                             * 	  
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
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FeeDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FeeDetailDialogCtrl extends GFCBaseListCtrl<FeeRule> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(FeeDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FeeDetailDialog; 				// autoWired
 
	//Finance Schedule Details Tab
 	protected Label 		fee_finType; 							// autoWired
	protected Label 		fee_finCcy; 							// autoWired
	protected Label 		fee_scheduleMethod; 					// autoWired
	protected Label 		fee_profitDaysBasis; 					// autoWired
	protected Label 		fee_finReference; 						// autoWired
	protected Label 		fee_grcEndDate; 						// autoWired	

	protected Button 		btnFeeCharges;							// autoWired
	protected Label 		label_feeChargesSummaryVal; 			// autoWired
	protected Listbox 		listBoxFinFeeCharges;					// autoWired
	
	private Map<String, BigDecimal> waiverPaidChargesMap = null;
	private Map<String,FeeRule> feeRuleDetailsMap = null;
	private BigDecimal oldVar_FeeChargeAmount = BigDecimal.ZERO;
	 
	private String 				eventCode = "";
	private boolean 			feeChargesExecuted;
  
	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			
	private FinScheduleData 		finScheduleData = null;
	private FinanceMain 			financeMain = null;
	private Object financeMainDialogCtrl = null;
	private boolean isModify = false;	
	private boolean isWIF = false;

	//Bean Setters  by application Context
 	private AccountEngineExecution engineExecution;
 	private CustomerService customerService;
 	private FinanceDetailService financeDetailService;
 	private List<ValueLabel> profitDaysBasisList = null;
	private List<ValueLabel> schMethodList = null;
	
	/**
	 * default constructor.<br>
	 */
	public FeeDetailDialogCtrl() {
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
	public void onCreate$window_FeeDetailDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
 		 
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) args.get("financeDetail"));
		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}
		
		if (args.containsKey("eventCode")) {
			eventCode = (String) args.get("eventCode");
		}
		
		if (args.containsKey("isModify")) {
			isModify = (Boolean) args.get("isModify");
		}
		if (args.containsKey("isWIF")) {
			isWIF = (Boolean) args.get("isWIF");
		}
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}

		doShowDialog(this.financeDetail);

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 */
	public void doWriteBeanToComponents() throws ParseException { 
		logger.debug("Entering");
		
 		this.fee_finType.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinTypeName()));
		this.fee_finCcy.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinCcyName()));
		this.fee_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getScheduleMethod(), schMethodList));
		this.fee_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getProfitDaysBasis(), profitDaysBasisList));
		this.fee_finReference.setValue(StringUtils.trimToEmpty(getFinanceMain().getFinReference()));
		this.fee_grcEndDate.setValue(DateUtility.formatDate(getFinanceMain().getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;
		
		this.feeChargesExecuted = false;
		// fill schedule list and asset tabs
		if (!getFinScheduleData().getFinanceScheduleDetails().isEmpty()){
			
			boolean executeSchTab = false;
			if(getFinScheduleData().getFeeRules() != null && !getFinScheduleData().getFeeRules().isEmpty()) {
				
				dofillFeeCharges(getFinScheduleData().getFeeRules(), true, true,false,getFinScheduleData());
				feeChargesExecuted = true;
				if(!isWIF){
					executeSchTab = true;
				}
				
			}else if (!getFinanceMain().isNewRecord() && 
					!PennantConstants.RECORD_TYPE_NEW.equals(getFinanceMain().getRecordType()) && !isWIF && !eventCode.equals("EARLYSTL")) {
				executeSchTab = true;
			}
			
			if (executeSchTab) {
				try {
					getFinanceMainDialogCtrl().getClass().getMethod("appendScheduleDetailTab", 
							Boolean.class, Boolean.class).invoke(getFinanceMainDialogCtrl(), true, true);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
			// Prepare Amount Code detail Object
			if(!executeSchTab && !isWIF){
				Events.sendEvent("onClick$btnFeeCharges", this.window_FeeDetailDialog, new Boolean[]{true,true});
				feeChargesExecuted = true;
			}
 		}
		
 		if(!feeChargesExecuted){
			if (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty()) {
				dofillFeeCharges(getFinanceDetail().getFeeCharges(), false,false,false,getFinScheduleData());
 			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException, ParseException {
		logger.debug("Entering");
		
		//Fee Charges Tab
		/*this.btnFeeCharges.setDisabled(!isModify);
		this.btnFeeCharges.setVisible(isModify);*/
		
		try {
			getFinanceMainDialogCtrl().getClass().getMethod("setFeeDetailDialogCtrl", 
					this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
		} catch (Exception e) {
			logger.error(e);
		}
		
		// fill the components with the data
		doWriteBeanToComponents();
		
		getBorderLayoutHeight();
		if(isWIF){
			this.listBoxFinFeeCharges.setHeight(this.borderLayoutHeight- 230 +"px");
			this.window_FeeDetailDialog.setHeight(this.borderLayoutHeight-30+"px");
		}else{
			this.listBoxFinFeeCharges.setHeight(this.borderLayoutHeight- 305 +"px");
			this.window_FeeDetailDialog.setHeight(this.borderLayoutHeight-80+"px");
		}
		
		logger.debug("Leaving");
	}
   
	/**
	 * Method to fill list box in FeeCharges Tab <br>
	 * 
	 * @param feeChargesList
	 *            (List)
	 */
	public FinScheduleData dofillFeeCharges(List<?> feeChargesList, boolean isSchdCal, boolean renderSchdl, boolean isReBuild,FinScheduleData finScheduleData) {
		logger.debug("Entering");

		this.listBoxFinFeeCharges.getItems().clear();
		feeRuleDetailsMap = new HashMap<String, FeeRule>();
		BigDecimal feeAmt = BigDecimal.ZERO;
		BigDecimal totalFee = BigDecimal.ZERO;
		int formatter = finScheduleData.getFinanceMain().getLovDescFinFormatter();

		if (feeChargesList != null && !feeChargesList.isEmpty()) {
			for (Object chargeRule : feeChargesList) {
				
				Listitem item = new Listitem();
				Listcell lc;
				if (chargeRule instanceof Rule) {
					Rule rule = (Rule) chargeRule;
					lc = new Listcell(rule.getRuleCode());
					lc.setParent(item);
					lc = new Listcell(rule.getRuleCodeDesc());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (chargeRule instanceof FeeRule) {
					FeeRule feeRule = (FeeRule) chargeRule;
					lc = new Listcell(feeRule.getFeeCode());
					lc.setParent(item);
					lc = new Listcell(feeRule.getFeeCodeDesc());
					lc.setParent(item);
					
					String code = feeRule.getFeeCode()+DateUtility.formateDate(feeRule.getSchDate(),PennantConstants.AS400DateFormat)+feeRule.getSeqNo();
					
					if(renderSchdl){
						if(waiverPaidChargesMap == null){
							waiverPaidChargesMap = new HashMap<String, BigDecimal>();
						}
						
						if(waiverPaidChargesMap.containsKey("cal_"+code)){
							waiverPaidChargesMap.remove("cal_"+code);
						}
						if(waiverPaidChargesMap.containsKey("waiver_"+code)){
							waiverPaidChargesMap.remove("waiver_"+code);
						}
						if(waiverPaidChargesMap.containsKey("paid_"+code)){
							waiverPaidChargesMap.remove("paid_"+code);
						}
						waiverPaidChargesMap.put("cal_"+code,feeRule.getFeeAmount());
						waiverPaidChargesMap.put("waiver_"+code,feeRule.getWaiverAmount());
						waiverPaidChargesMap.put("paid_"+code,feeRule.getPaidAmount());
					}
					
					BigDecimal calAmt;
					if(waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("cal_"+code)){
						calAmt = new BigDecimal(waiverPaidChargesMap.get("cal_"+code).toString()).setScale(0, RoundingMode.FLOOR);
						feeRule.setFeeAmount(calAmt);
					}else{
						calAmt = new BigDecimal(feeRule.getFeeAmount().toString()).setScale(0, RoundingMode.FLOOR);
					}
					
					BigDecimal waiverAmt;
					if(waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("waiver_"+code)){
						waiverAmt = new BigDecimal(waiverPaidChargesMap.get("waiver_"+code).toString()).setScale(0, RoundingMode.FLOOR);
						feeRule.setWaiverAmount(waiverAmt);
					}else{
						waiverAmt = new BigDecimal(feeRule.getWaiverAmount().toString()).setScale(0, RoundingMode.FLOOR);
					}
					
					BigDecimal paidAmt;
					if(waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("paid_"+code)){
						paidAmt = new BigDecimal(waiverPaidChargesMap.get("paid_"+code).toString()).setScale(0, RoundingMode.FLOOR);
						feeRule.setPaidAmount(paidAmt);
					}else{
						paidAmt = new BigDecimal(feeRule.getPaidAmount().toString()).setScale(0, RoundingMode.FLOOR);
					}
					
					if(isModify){
						
						//Calculate Amount
						Decimalbox calBox = new Decimalbox();
						calBox.setWidth("95%");
						calBox.setMaxlength(18);
						calBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
						calBox.setSclass("feeWaiver");
						calBox.setDisabled(false);
						calBox.setId("cal_"+code);
						calBox.setValue(PennantAppUtil.formateAmount(calAmt,formatter));
						lc = new Listcell();
						lc.appendChild(calBox);
						lc.setSclass("inlineMargin");
						lc.setParent(item);		
						
						Decimalbox oldwaiverBox = null;
						Decimalbox maxWaiverBox = null;
						Decimalbox waiverBox = null;
						if(feeRule.isAllowWaiver()){
							
							//Storage Max Waiver Amount
							maxWaiverBox = new Decimalbox();
							maxWaiverBox.setVisible(false);
							maxWaiverBox.setId("maxwaiver_"+code);
							maxWaiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
							BigDecimal maxWaiver = PennantAppUtil.getPercentageValue(feeRule.getFeeAmount(), feeRule.getWaiverPerc());
							maxWaiverBox.setValue(PennantAppUtil.formateAmount(maxWaiver,formatter));
							
							//Storage Old Waiver Amount
							oldwaiverBox = new Decimalbox();
							oldwaiverBox.setVisible(false);
							oldwaiverBox.setId("oldwaiver_"+code);
							oldwaiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
							oldwaiverBox.setValue(PennantAppUtil.formateAmount(waiverAmt,formatter));

							waiverBox = new Decimalbox();
							waiverBox.setWidth("95%");
							waiverBox.setMaxlength(18);
							waiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
							waiverBox.setSclass("feeWaiver");
							waiverBox.setDisabled(false);
							waiverBox.setId("waiver_"+code);
							waiverBox.setValue(PennantAppUtil.formateAmount(waiverAmt,formatter));
							lc = new Listcell();
							waiverBox.setInplace(true);
							lc.appendChild(maxWaiverBox);
							lc.appendChild(oldwaiverBox);
							lc.appendChild(waiverBox);
							lc.setSclass("inlineMargin");
							lc.setParent(item);
						}else{
							lc = new Listcell(PennantAppUtil.amountFormate(waiverAmt,formatter));
							lc.setSclass("text-align:right;");
							lc.setParent(item);
						}

						//Storage Paid Customer Amount
						Decimalbox oldPaidBox = new Decimalbox();
						oldPaidBox.setVisible(false);
						oldPaidBox.setId("oldpaid_"+code);
						oldPaidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
						oldPaidBox.setValue(PennantAppUtil.formateAmount(paidAmt,formatter));

						Decimalbox paidBox = new Decimalbox();
						paidBox.setWidth("95%");
						paidBox.setMaxlength(18);
						paidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
						paidBox.setSclass("feeWaiver");
						paidBox.setDisabled(false);
						paidBox.setId("paid_"+code);
						paidBox.setValue(PennantAppUtil.formateAmount(paidAmt,formatter));
						lc = new Listcell();
						lc.appendChild(oldPaidBox);
						lc.appendChild(paidBox);
						lc.setSclass("inlineMargin");
						lc.setParent(item);		

						List<Object> list = new ArrayList<Object>(5);
						list.add(calBox);
						list.add(paidBox);
						list.add(oldwaiverBox);
						list.add(waiverBox);
						list.add(false);
						list.add(oldPaidBox);
						if(waiverBox != null){
							waiverBox.addForward("onChange",window_FeeDetailDialog,"onChangeFeeAmount",list);
						}

						list = new ArrayList<Object>(4);
						list.add(calBox);
						list.add(waiverBox);
						list.add(oldPaidBox);
						list.add(paidBox);
						list.add(false);
						list.add(oldwaiverBox);
						paidBox.addForward("onChange",window_FeeDetailDialog,"onChangeFeeAmount",list);
						
						list = new ArrayList<Object>(4);
						list.add(calBox);
						list.add(waiverBox);
						list.add(oldPaidBox);
						list.add(paidBox);
						list.add(true);
						list.add(oldwaiverBox);
						list.add(maxWaiverBox);
						calBox.addForward("onChange",window_FeeDetailDialog,"onChangeFeeAmount",list);
					
					}else{
						lc = new Listcell(PennantAppUtil.amountFormate(feeRule.getFeeAmount(),formatter));
						lc.setSclass("text-align:right;cursor:default;");
						lc.setParent(item);
						lc = new Listcell(PennantAppUtil.amountFormate(feeRule.getWaiverAmount(),formatter));
						lc.setSclass("text-align:right;cursor:default;");
						lc.setParent(item);
						lc = new Listcell(PennantAppUtil.amountFormate(feeRule.getPaidAmount(),formatter));
						lc.setSclass("text-align:right;cursor:default;");
						lc.setParent(item);
					}
					
					feeChargesExecuted = true;
					if(!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())){
						
						totalFee = totalFee.add(feeRule.getFeeAmount()).subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());
						if(feeRule.isAddFeeCharges()){
							feeAmt = feeAmt.add(feeRule.getFeeAmount()).subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount());
						}
						
						feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
					}
				}
				this.listBoxFinFeeCharges.appendChild(item);
			}
			
			if(isSchdCal){
				if(renderSchdl){
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFeeChargeAmt(feeAmt);
				}else{
					finScheduleData.getFinanceMain().setFeeChargeAmt(finScheduleData.getFinanceMain().getFeeChargeAmt().subtract(
							oldVar_FeeChargeAmount).add(feeAmt));
					if(!isReBuild){
						oldVar_FeeChargeAmount = feeAmt;
					}
					finScheduleData.getFinanceMain().setCurFeeChargeAmt(feeAmt);
				}
			}else{
				if(finScheduleData != null && PennantConstants.RECORD_TYPE_NEW.equals(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordType())){
					finScheduleData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
				}
			}

			this.label_feeChargesSummaryVal.setValue(PennantAppUtil.amountFormate(totalFee, formatter));
		}
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public void onChangeFeeAmount(ForwardEvent event) throws InterruptedException, IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		logger.debug("Entering" + event.toString());
		
		List<Object> list  = (List<Object>) event.getData();
		Decimalbox calBox = (Decimalbox) list.get(0);
		Decimalbox oppBox = (Decimalbox) list.get(1);
		Decimalbox oldBox = (Decimalbox) list.get(2);
		Decimalbox targetBox = (Decimalbox) list.get(3);
		boolean isCalValueChange = (Boolean) list.get(4);
		Decimalbox oldWaiverBox = (Decimalbox) list.get(5);
		
		int finFormatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		BigDecimal calAmount = PennantAppUtil.unFormateAmount(calBox.getValue(), finFormatter);
		if(isCalValueChange){
			BigDecimal sumAmount = BigDecimal.ZERO;
			if(oppBox != null){
				sumAmount = PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter);
			}
			sumAmount = sumAmount.add(PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
			if(sumAmount.compareTo(calAmount) > 0){
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_ChangeFee.value"));
				
				if(oppBox != null){
					oppBox.setValue(BigDecimal.ZERO);
					oldWaiverBox.setValue(BigDecimal.ZERO);
					
					if(waiverPaidChargesMap.containsKey(oppBox.getId())){
						waiverPaidChargesMap.remove(oppBox.getId());
					}
					waiverPaidChargesMap.put(targetBox.getId(),PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter));
				}
				oldBox.setValue(BigDecimal.ZERO);
				targetBox.setValue(BigDecimal.ZERO);
				
			}
		}		
		
		//Check Condition based on Waiver is Allowed or Not
		BigDecimal balFeeAmount = null;
		if(oppBox == null){
			balFeeAmount = calAmount.subtract(PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
		}else{
			balFeeAmount = calAmount.subtract(PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter))
								.subtract(PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
		}
		if(balFeeAmount.compareTo(BigDecimal.ZERO) < 0){
			
			//Show Error message for Exceeding Entered Amount Limit
			BigDecimal availAmt = null;
			if(oppBox == null){
				availAmt = calAmount;
			}else{
				availAmt = calAmount.subtract(PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter));
			}
			
			String msg = targetBox.getId().substring(0, targetBox.getId().indexOf('_')).toUpperCase()+" Amount for "+
					targetBox.getId().substring(targetBox.getId().indexOf('_')+1).toUpperCase()+" Rule Cannot be greater than Avail Amount:"+
					PennantAppUtil.formateAmount(availAmt, finFormatter);
			PTMessageUtils.showErrorMessage(msg);
			
			//Reset Old Amount back to Target Box
			if(oldBox.getValue().compareTo(availAmt) > 0){
				targetBox.setValue(BigDecimal.ZERO);
			}else{
				targetBox.setValue(oldBox.getValue());
			}
			
		}else{

			boolean isReExecSchedule = false;
			if (getFinanceMain().isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(getFinanceMain().getRecordType()) || isWIF) {
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
			}else{
				isReExecSchedule = true;
			}

			//Set Waiver & paid Value Amount Storage on Change into map
			if(waiverPaidChargesMap == null){
				waiverPaidChargesMap = new HashMap<String, BigDecimal>();
			}

			if(waiverPaidChargesMap.containsKey(calBox.getId())){
				waiverPaidChargesMap.remove(calBox.getId());
			}
			waiverPaidChargesMap.put(calBox.getId(),PennantAppUtil.unFormateAmount(calBox.getValue(), finFormatter));

			if(waiverPaidChargesMap.containsKey(targetBox.getId())){
				waiverPaidChargesMap.remove(targetBox.getId());
			}
			waiverPaidChargesMap.put(targetBox.getId(),PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
			oldBox.setValue(targetBox.getValue());

			//Recalculation for Fee charges After Modified Fee Sequence
			//Because any Fee calculation will Effect on Before Values 
			if(isCalValueChange){

				//Recalculate Total Actual Fee Charge Amount
				List<FeeRule> feeRules = getFinanceDetail().getFinScheduleData().getFeeRules();
				boolean isContinueForCalculation = false;
				List<FeeRule> existFeeRules = new ArrayList<FeeRule>();
				int size = feeRules.size();
				
				for (int i=0; i< size;i++){

					FeeRule feeRule = feeRules.get(i);
					String code = feeRule.getFeeCode()+DateUtility.formateDate(feeRule.getSchDate(),
							PennantConstants.AS400DateFormat)+feeRule.getSeqNo();
					
					if(!isContinueForCalculation){
						existFeeRules.add(feeRule);
					}
					
					if(feeRule.isAddFeeCharges()){
						
						if(isContinueForCalculation){
							if(waiverPaidChargesMap.containsKey("cal_"+code)){
								waiverPaidChargesMap.remove("cal_"+code);
							}
							if(waiverPaidChargesMap.containsKey("waiver_"+code)){
								waiverPaidChargesMap.remove("waiver_"+code);
							}
							if(waiverPaidChargesMap.containsKey("paid_"+code)){
								waiverPaidChargesMap.remove("paid_"+code);
							}
						}

						String calValBoxId = calBox.getId();
						if(calValBoxId.equals("cal_"+code)){

							if(this.listBoxFinFeeCharges.getFellowIfAny("cal_"+code) != null){
								Decimalbox calbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("cal_"+code);
								feeRule.setFeeAmount(PennantAppUtil.unFormateAmount(calbox.getValue(), finFormatter));
							}
							if(this.listBoxFinFeeCharges.getFellowIfAny("waiver_"+code) != null){
								Decimalbox waiverbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("waiver_"+code);
								feeRule.setWaiverAmount(PennantAppUtil.unFormateAmount(waiverbox.getValue(), finFormatter));
							}
							if(this.listBoxFinFeeCharges.getFellowIfAny("paid_"+code) != null){
								Decimalbox paidbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("paid_"+code);
								feeRule.setPaidAmount(PennantAppUtil.unFormateAmount(paidbox.getValue(), finFormatter));
							}
							if(i != size-1){
								
								//Ask For User Confirmation
								final String msg = Labels.getLabel("label_RecalculationforFeeCharges");
								final String title = Labels.getLabel("message.Conformation");
								MultiLineMessageBox.doSetTemplate();

								int conf = (MultiLineMessageBox.show(msg, title,
										MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

								if (conf == MultiLineMessageBox.YES) {
									isContinueForCalculation = true;
								}else{
									break;
								}
							}
						}
					}
				}
				
				//Warning Message To Ask for Recalculation of Fee Details After Modified one if Exists
				if(isContinueForCalculation){
					reExecCalAmtFeeChange(true, getFinScheduleData(), getFinScheduleData().getFinanceMain().getFinStartDate(), existFeeRules);
				}
			}
			
			if(isReExecSchedule){
				
				//Re-rendering Finance Fee Rule Data
				getFinanceDetail().setFinScheduleData(dofillFeeCharges(getFinanceDetail().getFinScheduleData().getFeeRules(),
						true, false, true,getFinanceDetail().getFinScheduleData()));
				
				if(eventCode.equals("")){// TODO -- Need to Modify this , if Add Fee & charges include to Schedule in other Event Actions
					Date disbDate = DateUtility.getUtilDate(targetBox.getId().substring(targetBox.getId().length()-7, targetBox.getId().length()-1), 
							PennantConstants.AS400DateFormat);

					BigDecimal modifiedFeevalue = PennantAppUtil.unFormateAmount(targetBox.getValue().subtract(oldBox.getValue()), finFormatter);
					getFinScheduleData().getFinanceMain().setEventFromDate(disbDate);
					getFinScheduleData().getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);
					getFinScheduleData().getFinanceMain().setRecalToDate(null);
					setFinScheduleData(ScheduleCalculator.addDisbursement(getFinanceDetail().getFinScheduleData(), 
							BigDecimal.ZERO, null, modifiedFeevalue.negate()));

					try {
						getFinanceMainDialogCtrl().getClass().getMethod("reRenderScheduleList", 
								FinScheduleData.class).invoke(getFinanceMainDialogCtrl(), getFinanceDetail().getFinScheduleData());
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}else{

				//Recalculate Total Actual Fee Charge Amount
				List<FeeRule> feeRules = getFinanceDetail().getFinScheduleData().getFeeRules();
				BigDecimal totalFeeCharge = BigDecimal.ZERO;

				for (FeeRule feeRule : feeRules) {

					String code = feeRule.getFeeCode()+DateUtility.formateDate(feeRule.getSchDate(),
							PennantConstants.AS400DateFormat)+feeRule.getSeqNo();

					if(this.listBoxFinFeeCharges.getFellowIfAny("cal_"+code) != null){
						Decimalbox calbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("cal_"+code);
						totalFeeCharge = totalFeeCharge.add(PennantAppUtil.unFormateAmount(calbox.getValue(), finFormatter));
					}
					if(this.listBoxFinFeeCharges.getFellowIfAny("waiver_"+code) != null){
						Decimalbox waiverbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("waiver_"+code);
						totalFeeCharge = totalFeeCharge.subtract(PennantAppUtil.unFormateAmount(waiverbox.getValue(), finFormatter));
					}
					if(this.listBoxFinFeeCharges.getFellowIfAny("paid_"+code) != null){
						Decimalbox paidbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("paid_"+code);
						totalFeeCharge = totalFeeCharge.subtract(PennantAppUtil.unFormateAmount(paidbox.getValue(), finFormatter));
					}
				}
				
				//Finance Fee Charges Recalculated
				this.label_feeChargesSummaryVal.setValue(PennantAppUtil.amountFormate(totalFeeCharge, finFormatter));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

  	/**
	 * Method for Executing Fee Charges Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnFeeCharges(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		boolean isSchdCal = false;
		boolean renderSchdl = false;
		if(event.getData() != null){
			Boolean[] data = (Boolean[]) event.getData();
			isSchdCal = data[0];
			renderSchdl = data[1];
		}

		doExecuteFeeCharges(isSchdCal,renderSchdl,getMainFinaceDetail().getFinScheduleData(),true, 
				getMainFinaceDetail().getFinScheduleData().getFinanceMain().getFinStartDate());

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Execution of Fee Details on Build Event / For Render Schedule process
	 * @param isSchdCal
	 * @param renderSchdl
	 * @param finScheduleData
	 * @param isReBuild
	 * @param feeApplyDate
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException 
	 */
	public FinScheduleData doExecuteFeeCharges(boolean isSchdCal, boolean renderSchdl,
			FinScheduleData finScheduleData, boolean isReBuild, Date feeApplyDate) throws InterruptedException, IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		logger.debug("Entering");
		
		if (!isSchdCal && finScheduleData.getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
			return finScheduleData;
		}
	 
		DataSet dataSet = AEAmounts.createDataSet(finScheduleData.getFinanceMain(), eventCode,
				finScheduleData.getFinanceMain().getFinStartDate(), finScheduleData.getFinanceMain().getFinStartDate());
		
		//Finance Deferment Process Count Setting
		dataSet.setCurRpyDefCount(finScheduleData.getDefermentHeaders() == null ? 1 : finScheduleData.getDefermentHeaders().size()+1);
		
		AEAmountCodes amountCodes = AEAmounts.procAEAmounts(finScheduleData.getFinanceMain(),finScheduleData.getFinanceScheduleDetails(),
				new FinanceProfitDetail(), finScheduleData.getFinanceMain().getFinStartDate());
		
		if(finScheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE && finScheduleData.getFinanceMain().getCustID() != 0){
			boolean jointCustExist = getCustomerService().isJointCustExist(finScheduleData.getFinanceMain().getCustID());
			if(jointCustExist){
				dataSet.setFinJointAcCount(1);
			}
		}
		
		if(isWIF && getFinanceDetail().getCustomer() != null){
			if(getFinanceDetail().getCustomer().isJointCust()){
				dataSet.setFinJointAcCount(1);
			}
		}
		
		if(!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isReBuild){
			dataSet.setDownPayment(BigDecimal.ZERO);
			dataSet.setDownPayBank(BigDecimal.ZERO);
			dataSet.setDownPaySupl(BigDecimal.ZERO);
		}

		List<FeeRule> feeRules = getEngineExecution().getFeeChargesExecResults(dataSet,	amountCodes,
				getFinanceMain().getLovDescFinFormatter(), isWIF, getFinScheduleData().getFinanceType());
		
		//Get Finance Fee Details For Schedule Render Purpose In maintenance Stage
		List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
		if(!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF){
			approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinReference(), isWIF);
		}

		for (FeeRule feeCharge : feeRules) {
			feeCharge.setSchDate(feeApplyDate);
			int seqNo = 0;
			if(!isReBuild){
				for (FeeRule feeRule : finScheduleData.getFeeRules()) {
					feeRule.setNewFee(false);
					if(feeRule.getFeeCode().equals(feeCharge.getFeeCode())){
						if(seqNo < feeRule.getSeqNo() && feeApplyDate.compareTo(feeRule.getSchDate()) == 0){
							seqNo = feeRule.getSeqNo();
						}
					}
				}
			}
			
			//Sequence Updations on Maintenance Module Fees
			if(isReBuild && !financeMain.isNewRecord() && 
					!PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF){
				if(approvedFeeRules != null && !approvedFeeRules.isEmpty()){

					for (FeeRule feeRule : approvedFeeRules) {
						if(feeRule.getFeeCode().equals(feeCharge.getFeeCode())){
							if(seqNo < feeRule.getSeqNo() && feeCharge.getSchDate().compareTo(feeRule.getSchDate()) == 0){
								seqNo = feeRule.getSeqNo();
							}
						}
					}
				}
			}
			
			feeCharge.setSeqNo(seqNo+1);
			feeCharge.setNewFee(true);
		}
		
		if(isReBuild){
			finScheduleData.setFeeRules(feeRules);
		}else{
			finScheduleData.getFeeRules().addAll(feeRules);
		}
		finScheduleData = dofillFeeCharges(finScheduleData.getFeeRules(), isSchdCal,renderSchdl, isReBuild,finScheduleData);
		
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Execution of Fee Details on Build Event / For Render Schedule process
	 * @param isSchdCal
	 * @param renderSchdl
	 * @param finScheduleData
	 * @param isReBuild
	 * @param feeApplyDate
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException 
	 */
	public void reExecCalAmtFeeChange(boolean isSchdCal, FinScheduleData finScheduleData, Date feeApplyDate, List<FeeRule> existFeeRules) 
					throws InterruptedException, IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		logger.debug("Entering");
		
		finScheduleData.getFinanceMain().setCurDisbursementAmt(finScheduleData.getFinanceMain().getFinAmount());
		
		DataSet dataSet = AEAmounts.createDataSet(finScheduleData.getFinanceMain(), eventCode,
				finScheduleData.getFinanceMain().getFinStartDate(), finScheduleData.getFinanceMain().getFinStartDate());
		
		AEAmountCodes amountCodes = AEAmounts.procAEAmounts(finScheduleData.getFinanceMain(),finScheduleData.getFinanceScheduleDetails(),
				new FinanceProfitDetail(), finScheduleData.getFinanceMain().getFinStartDate());
		
		if(finScheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE && finScheduleData.getFinanceMain().getCustID() != 0){
			boolean jointCustExist = getCustomerService().isJointCustExist(finScheduleData.getFinanceMain().getCustID());
			if(jointCustExist){
				dataSet.setFinJointAcCount(1);
			}
		}
		
		if(isWIF && getFinanceDetail().getCustomer() != null){
			if(getFinanceDetail().getCustomer().isJointCust()){
				dataSet.setFinJointAcCount(1);
			}
		}
		
		if(!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())){
			dataSet.setDownPayment(BigDecimal.ZERO);
			dataSet.setDownPayBank(BigDecimal.ZERO);
			dataSet.setDownPaySupl(BigDecimal.ZERO);
		}

		List<FeeRule> feeRules = getEngineExecution().getReExecFeeResults(dataSet,	amountCodes,
				getFinanceMain().getLovDescFinFormatter(), isWIF, getFinScheduleData().getFinanceType(), existFeeRules);
		
		//Get Finance Fee Details For Schedule Render Purpose In maintenance Stage
		List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
		if(!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF){
			approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinReference(), isWIF);
		}

		for (FeeRule feeCharge : feeRules) {
			int seqNo = 0;
			feeCharge.setSchDate(feeApplyDate);
			
			//Sequence Updations on Maintenance Module Fees
			if(!financeMain.isNewRecord() && 
					!PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF){
				if(approvedFeeRules != null && !approvedFeeRules.isEmpty()){

					for (FeeRule feeRule : approvedFeeRules) {
						if(feeRule.getFeeCode().equals(feeCharge.getFeeCode())){
							if(seqNo < feeRule.getSeqNo() && feeCharge.getSchDate().compareTo(feeRule.getSchDate()) == 0){
								seqNo = feeRule.getSeqNo();
							}
						}
					}
				}
			}
			
			feeCharge.setSeqNo(seqNo+1);
			feeCharge.setNewFee(true);
		}
		
		finScheduleData.setFeeRules(feeRules);
		dofillFeeCharges(finScheduleData.getFeeRules(), isSchdCal,true, true ,finScheduleData);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Finance Detail Data From Main Controller
	 * @return
	 */
	public FinanceDetail getMainFinaceDetail(){
		logger.debug("Entering");
		try {
			FinanceDetail financeDetail = (FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail").invoke(getFinanceMainDialogCtrl());
			logger.debug("Leaving");
			return financeDetail;	
 		} catch (Exception e) {
			logger.error(e);
		}
 		logger.debug("Leaving");
		return financeDetail;	
	}

 	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}
 
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public boolean isFeeChargesExecuted() {
		return feeChargesExecuted;
	}
	public void setFeeChargesExecuted(boolean feeChargesExecuted) {
		this.feeChargesExecuted = feeChargesExecuted;
	}

	public Map<String, BigDecimal> getWaiverPaidChargesMap() {
		return waiverPaidChargesMap;
	}
	public void setWaiverPaidChargesMap(Map<String, BigDecimal> waiverPaidChargesMap) {
		this.waiverPaidChargesMap = waiverPaidChargesMap;
	}

	public Map<String, FeeRule> getFeeRuleDetailsMap() {
		return feeRuleDetailsMap;
	}
	public void setFeeRuleDetailsMap(Map<String, FeeRule> feeRuleDetailsMap) {
		this.feeRuleDetailsMap = feeRuleDetailsMap;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	public CustomerService getCustomerService() {
		return customerService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	
}