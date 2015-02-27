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
 * FileName    		:  EligibilityDetailDialogCtrl.java                                     * 	  
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EligibilityDetailDialogCtrl extends GFCBaseListCtrl<FinanceEligibilityDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(EligibilityDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_EligibilityDetailDialog; 			// autoWired
	protected Borderlayout  borderlayoutEligibilityDetail;				// autoWired
	
	//Finance Schedule Details Tab

	protected Label 		elg_finType; 							// autoWired
	protected Label 		elg_finCcy; 							// autoWired
	protected Label 		elg_scheduleMethod; 					// autoWired
	protected Label 		elg_profitDaysBasis; 					// autoWired
	protected Label 		elg_finReference; 						// autoWired
	protected Label 		elg_grcEndDate; 						// autoWired	
	
	//Finance Eligibility Details Tab
	protected Button 		btnElgRule; 							// autoWired
	protected Label 		label_ElgRuleSummaryVal; 				// autoWired
	protected Listbox 		listBoxFinElgRef;						// autoWired
	
 	List<FinanceEligibilityDetail> eligibilityRuleList = null;
 	
	//External Fields usage for Individuals ----> Eligibility Details

	private transient boolean 	custisEligible = true;
	private boolean isWIF = false;
  	
	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	private FinanceMain financeMain = null;
 	
	private EligibilityDetailService eligibilityDetailService;
	private CurrencyService currencyService;
	private RuleService ruleService;
	private RuleExecutionUtil ruleExecutionUtil;
	private List<ValueLabel> profitDaysBasisList = new ArrayList<ValueLabel>();
	private List<ValueLabel> schMethodList = new ArrayList<ValueLabel>();
	
	
	/**
	 * default constructor.<br>
	 */
	public EligibilityDetailDialogCtrl() {
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
	public void onCreate$window_EligibilityDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
			setFinanceMain(getFinScheduleData().getFinanceMain());
		}
		
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}
		
		if (args.containsKey("isWIF")) {
			isWIF = (Boolean) args.get("isWIF");
		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}

		doShowDialog();
		logger.debug("Leaving " + event.toString());
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
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// Eligibility Details Tab 
			this.elg_finType.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinTypeName()));
			this.elg_finCcy.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinCcyName()));
			this.elg_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getScheduleMethod(), schMethodList));
			this.elg_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getProfitDaysBasis(), profitDaysBasisList));
			this.elg_finReference.setValue(StringUtils.trimToEmpty(getFinanceMain().getFinReference()));
			this.elg_grcEndDate.setValue(DateUtility.formatDate(getFinanceMain().getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;
			
			eligibilityRuleList = getFinanceDetail().getElgRuleList();
			if(isWIF){
				doClickEligibility(false);
			}else{
				doFillExecElgList(eligibilityRuleList);
				setCustEligibilityStatus();
			}
			
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setEligibilityDetailDialogCtrl", this.getClass()).invoke(financeMainDialogCtrl, this);
			} catch (InvocationTargetException e) {
				logger.error(e);
			}
			getBorderLayoutHeight();
			this.listBoxFinElgRef.setHeight(this.borderLayoutHeight- 190 - 52+"px");
			this.window_EligibilityDetailDialog.setHeight(this.borderLayoutHeight-80+"px");
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Executing Finance Eligibility Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnElgRule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(!isWIF){
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("doCustomerValidation").invoke(getFinanceMainDialogCtrl());
				setFinanceDetail((FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail").invoke(getFinanceMainDialogCtrl()));
			} catch (Exception e) {
				if(e.getCause().getClass().equals(WrongValuesException.class)){
					throw e;	
				}
				logger.error(e);
			}
		}
		doClickEligibility(false);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Click event for eligibility
	 * @param isSave
	 * @throws Exception 
	 */
	public void doClickEligibility(boolean isSave) throws Exception{
		logger.debug("Entering" );
		this.label_ElgRuleSummaryVal.setValue("");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());
		
		if(isWIF){
			try {
				aFinanceDetail = (FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("dofillEligibilityData").invoke(getFinanceMainDialogCtrl());
			} catch (Exception e) {
				if(e.getCause().getClass().equals(WrongValuesException.class)){
					throw e;	
				}
				logger.error(e);
			}
		}else{
			getCustEligibilityDetail(aFinanceDetail);
		}
		
		for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
			if(financeEligibilityDetail.isExecute()){
				if(!isSave){
					financeEligibilityDetail.setUserOverride(false);
				}
				financeEligibilityDetail = getEligibilityDetailService().getElgResult(financeEligibilityDetail,aFinanceDetail);
			}
			
			if("DSRCAL".equals(financeEligibilityDetail.getLovDescElgRuleCode().trim())){
				aFinanceDetail.getCustomerEligibilityCheck().setDSCR(new BigDecimal(financeEligibilityDetail.getRuleResult()));
				getFinanceDetail().getCustomerEligibilityCheck().setDSCR(new BigDecimal(financeEligibilityDetail.getRuleResult()));
			}
		}
		aFinanceDetail = null;
  		doFillExecElgList(this.eligibilityRuleList );
		setCustEligibilityStatus();
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 */
	private void getCustEligibilityDetail(FinanceDetail financeDetail) {
		logger.debug("Entering");

		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		CustomerEligibilityCheck eligibilityCheck = financeDetail.getCustomerEligibilityCheck();
		if(eligibilityCheck == null){
			eligibilityCheck = new CustomerEligibilityCheck();
		}

		// Eligibility object
		BeanUtils.copyProperties(customer, eligibilityCheck);
		int age = DateUtility.getYearsBetween(customer.getCustDOB(), DateUtility.today());
		eligibilityCheck.setCustAge(age);

		//Minor Age Calculation
		int minorAge = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MINOR_AGE").toString());
		if (age < minorAge) {
			eligibilityCheck.setCustIsMinor(true);
		} else {
			eligibilityCheck.setCustIsMinor(false);
		}

		Currency finCurrency = null;
		//Customer Total Income & Expense Conversion
		if (!StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString())
				.equals(customer.getCustBaseCcy())) {
			finCurrency = getCurrencyService().getCurrencyById(customer.getCustBaseCcy());
			eligibilityCheck.setCustTotalIncome(calculateExchangeRate(customer.getCustTotalIncome(), finCurrency));
			eligibilityCheck.setCustTotalExpense(calculateExchangeRate(customer.getCustTotalExpense(), finCurrency));
		}
		
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
				financeMain.getMaturityDate(), true);

		BigDecimal curFinRpyAmount = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		//Get Customer Employee Designation
		String custEmpDesg = "";
		if(financeDetail.getCustomerDetails().getCustEmployeeDetail() != null){
			custEmpDesg = StringUtils.trimToEmpty(financeDetail.getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
		}
		
		if (months > 0) {
			eligibilityCheck.setTenure(new BigDecimal((months / 12) + "." + (months % 12)));
		}
		eligibilityCheck.setReqFinAmount(financeMain.getFinAmount());

		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		eligibilityCheck.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate,
				customer.getCustBlackListDate()));

		eligibilityCheck.setCustCtgType(customer.getLovDescCustCtgType());
		eligibilityCheck.setReqProduct(financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName());

		//Currently
		if (curFinRpyAmount != null && curFinRpyAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (!StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString())
					.equals(financeMain.getFinCcy())) {

				if (finCurrency != null && finCurrency.getCcyCode().equals(financeMain.getFinCcy())) {
					eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
				} else {
					finCurrency = getCurrencyService().getCurrencyById(financeMain.getFinCcy());
					eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
				}
			} else {
				eligibilityCheck.setCurFinRepayAmt(curFinRpyAmount);
			}
		}

		//set Customer Designation if customer status is Employed
		eligibilityCheck.setCustEmpDesg(custEmpDesg);

		//get Customer Employee Allocation Type if customer status is Employed
		eligibilityCheck.setCustEmpAloc("");//getCustomerDAO().getCustCurEmpAlocType(customer.getCustID())

		//DSR Calculation
		Rule rule = getRuleService().getRuleById("DSRCAL", "ELGRULE", "");
		if (rule != null) {
			List<GlobalVariable> globalVariableList = SystemParameterDetails.getGlobaVariableList();
			Object dscr = getRuleExecutionUtil().executeRule(rule.getSQLRule(), eligibilityCheck, globalVariableList,financeMain.getFinCcy());

			if(dscr == null){
				dscr = BigDecimal.ZERO;
			}else if(new BigDecimal(dscr.toString()).intValue() > 9999){
				dscr = 9999;
			}

			eligibilityCheck.setDSCR(new BigDecimal(dscr.toString()));
		}else{
			eligibilityCheck.setDSCR(BigDecimal.ZERO);
		}

		financeDetail.setCustomerEligibilityCheck(eligibilityCheck);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Calculating Exchange Rate for Finance Schedule Calculation
	 * 
	 * @param amount
	 * @param aCurrency
	 * @return
	 */
	private BigDecimal calculateExchangeRate(BigDecimal amount, Currency aCurrency) {
		if (SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").equals(
				aCurrency.getCcyCode())) {
			return amount;
		} else {
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}

			if (aCurrency != null) {
				amount = amount.multiply(aCurrency.getCcySpotRate());
			}
		}
		return amount;
	}
	
	/**
	 * Method for Rendering Executed Eligibility Details
	 * @param eligibilityDetails
	 */
	public void doFillExecElgList(List<FinanceEligibilityDetail> eligibilityDetails){
		logger.debug("Entering");

		this.listBoxFinElgRef.getItems().clear();
		String overridePerc = "";
		if(eligibilityDetails != null && !eligibilityDetails.isEmpty()){
			for (FinanceEligibilityDetail detail : eligibilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				//Rule Source
				lc = new Listcell("");
				lc.setParent(item);

				//Rule Code
				lc = new Listcell(detail.getLovDescElgRuleCode());
				lc.setParent(item);

				//Rule Code Desc
				lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
				lc.setParent(item);

				//Can Override
				lc = new Listcell();
				Checkbox cbOverride = new Checkbox();
				cbOverride.setDisabled(true);

				if(detail.isCanOverride()){
					cbOverride.setChecked(true);
					overridePerc = String.valueOf(detail.getOverridePerc());
				}else{
					cbOverride.setChecked(false);
					overridePerc = "";
				}
				lc.appendChild(cbOverride);
				lc.setParent(item);

				//Override Value
				lc = new Listcell(overridePerc);
				lc.setParent(item);
				
				//If Rule Not Executed 
				if(detail.getRuleResult().equals("")){
					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);
				}else{ 
					//If Decimal Result for Eligibility
					if("D".equals(detail.getRuleResultType())){
						//IF Error in Executing the Rule
						if(detail.getRuleResult().equals("E")){
							lc = new Listcell(Labels.getLabel("common.InSuffData"));
							lc.setStyle("font-weight:bold;color:red;");
							lc.setParent(item);
							
							lc = new Listcell("");
							lc.setParent(item);
							//IF DSR Calculation Rule
						}else if("DSRCAL".equals(detail.getLovDescElgRuleCode())){

							lc = new Listcell(detail.getRuleResult()+"%");
							lc.setParent(item);

							lc = new Listcell("");
							lc.setParent(item);

						}else{

							lc = new Listcell(PennantAppUtil.amountFormate(new BigDecimal(detail.getRuleResult()),
									getFinanceMain().getLovDescFinFormatter()));
							lc.setStyle("text-align:right;");
							lc.setParent(item);

							lc = new Listcell(PennantAppUtil.amountFormate(detail.getOverrideResult(),
									getFinanceMain().getLovDescFinFormatter()));
							lc.setStyle("text-align:right;");
							lc.setParent(item);
						}
					}else{
						if(detail.isEligible()){
							lc = new Listcell(Labels.getLabel("common.Eligible"));
							lc.setStyle("font-weight:bold;color:green;");
						}else{
							lc = new Listcell(Labels.getLabel("common.Ineligible"));
							lc.setStyle("font-weight:bold;color:red;");
						}					
						lc.setParent(item);

						lc = new Listcell("");
						lc.setParent(item);
					}
					if((!detail.isEligible() && detail.isCanOverride()) || detail.isUserOverride()){
						Checkbox cbElgOverride = new Checkbox();
						cbElgOverride.setChecked(detail.isUserOverride());
						if(detail.isExecute()){
							cbElgOverride.setDisabled(false);
						}else{
							cbElgOverride.setDisabled(true);
						}
						item.setTooltiptext(Labels.getLabel("listitem_ElgRule_tooltiptext"));
						cbElgOverride.addForward("onCheck",window_EligibilityDetailDialog,"onElgRuleItemChecked", detail);
						lc = new Listcell("");
						cbElgOverride.setParent(lc);
						lc.setParent(item);
					}else{
						lc = new Listcell("");
						lc.setParent(item);
					}
				}	
				listBoxFinElgRef.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to capture event when eligible rule item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
 	public void onElgRuleItemChecked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		Checkbox cb = (Checkbox) event.getOrigin().getTarget();
		FinanceEligibilityDetail financeEligibilityDetail = (FinanceEligibilityDetail)event.getData();
		
		financeEligibilityDetail.setUserOverride(cb.isChecked());
		financeEligibilityDetail.setEligible(getEligibilityDetailService().getEligibilityStatus(financeEligibilityDetail,
				financeMain.getFinCcy(), financeMain.getFinAmount()));
		setCustEligibilityStatus();
		Listcell elgLimitLabel = new Listcell();
		try{
			elgLimitLabel = (Listcell) cb.getParent().getPreviousSibling().getPreviousSibling();
		}catch(Exception e){
			e.printStackTrace();
		}if(cb.isChecked() && elgLimitLabel != null){
			elgLimitLabel.setLabel(Labels.getLabel("common.Eligible"));
			elgLimitLabel.setStyle("font-weight:bold;color:green;");
		}else{
			elgLimitLabel.setLabel(Labels.getLabel("common.Ineligible"));
			elgLimitLabel.setStyle("font-weight:bold;color:red;");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * updates the status of the customer to True/False
	 * @param 
	 * @return void
	 */
	public void setCustEligibilityStatus(){
		logger.debug("Entering" );
		setCustisEligible(getEligibilityDetailService().isCustEligible(eligibilityRuleList));
		if(isCustisEligible()){
			label_ElgRuleSummaryVal.setValue(Labels.getLabel("common.Eligible"));
			label_ElgRuleSummaryVal.setStyle("font-weight:bold;color:green;");
		}else{
			label_ElgRuleSummaryVal.setValue(Labels.getLabel("common.Ineligible"));
			label_ElgRuleSummaryVal.setStyle("font-weight:bold;color:red;");
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception 
	 */
	public FinanceDetail doSave_EligibilityList(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering ");
		setFinanceDetail(aFinanceDetail);
		doClickEligibility(true);
 		getFinanceDetail().setElgRuleList(eligibilityRuleList);
 		
		logger.debug("Leaving ");
		return getFinanceDetail();
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public boolean isCustisEligible() {
		return custisEligible;
	}

	public void setCustisEligible(boolean custisEligible) {
		this.custisEligible = custisEligible;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	
}
