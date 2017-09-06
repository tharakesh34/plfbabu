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
 *											    											*
 * FileName    		:  WIRateChangeDialogCtrl.java                          	            * 	  
 *                                                                    			    		*
 * Author      		:  PENNANT TECHONOLOGIES              				    				*
 *                                                                  			    		*
 * Creation Date    :  05-10-2011    							    						*
 *                                                                  			    		*
 * Modified Date    :  05-10-2011    							    						*
 *                                                                  			    		*
 * Description 		:                                             			    			*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-10-2011       Pennant	                 0.1                                        	* 
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
package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class RateChangeDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -4578996988245614938L;
	private static final Logger logger = Logger.getLogger(RateChangeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RateChangeDialog; 					
	protected Decimalbox rateChange; 							
	protected Combobox cbRateChangeFromDate;  					
	protected Combobox cbRateChangeToDate;  					
	protected Combobox profitDaysBasis;  								
	protected RateBox  rate;  								
	protected Combobox cbReCalType; 							
	protected Combobox cbRecalFromDate; 						
	protected Combobox cbRecalToDate; 	 						    
	protected Row baseRateRow;	 								
	protected Row rateAmountRow; 								
	protected Row tillDateRow; 									
	protected Row fromDateRow; 		
	protected Row reviewDatesRow;
	protected Radio reviewDates;
	protected Radio anyDate;
	protected Row profitDayBasisRow;
	protected Row recalTypeRow;
	protected Label  label_RateChangeDialog_FromDate;
	protected Label label_RateChangeDialog_ToDate;
	protected Label label_RateChangeDialog_Rate;
	protected Row	reviewDateFromDateRow;
	protected Row	reviewDateToDateRow;
	protected Row	anyDateFromDateRow;
	protected Row	anyDateToDateRow;
	protected Datebox anyDateRateChangeFromDate;
	protected Datebox anyDateRateChangeToDate;
	protected Uppercasebox 	serviceReqNo;
	protected Textbox		remarks;
	
	// not auto wired vars
	private FinScheduleData finScheduleData; 					// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 		// overhanded per param
	private transient ScheduleDetailDialogCtrl financeMainDialogCtrl;

	Calendar calender=Calendar.getInstance();
	private transient boolean validationOn;
	private transient RateChangeService rateChangeService;
	private boolean appDateValidationReq = false;
	/**
	 * default constructor.<br>
	 */
	public RateChangeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceMain object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RateChangeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RateChangeDialog);

		try {

			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments
						.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeScheduleDetail")) {
				this.setFinanceScheduleDetail((FinanceScheduleDetail) arguments
						.get("financeScheduleDetail"));
				setFinanceScheduleDetail(this.financeScheduleDetail);
			} else {
				setFinanceScheduleDetail(null);
			}
			
			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq  = (boolean) arguments.get("appDateValidationReq");
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) arguments
						.get("financeMainDialogCtrl"));
			} else {
				setFinanceMainDialogCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RateChangeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws Exception
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.MODAL);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_RateChangeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.anyDateRateChangeFromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.anyDateRateChangeToDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateChange.setScale(9);
		this.rate.setBaseProperties("BaseRateCode","BRType","BRTypeDesc");
		this.rate.setSpecialProperties("SplRateCode","SRType","SRTypeDesc");
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.rateChange.setReadonly(true);
		this.profitDaysBasis.setDisabled(true);
		this.cbRateChangeFromDate.setReadonly(true);
		this.cbRateChangeToDate.setReadonly(true);
		this.cbReCalType.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 * @throws InterruptedException 
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) throws InterruptedException {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		FinanceType aFinType = aFinSchData.getFinanceType();

		this.reviewDatesRow.setVisible(true);
		StringBuilder excludeFileds=new StringBuilder(",ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,STEPPOS,");
		if(aFinType.isRateChgAnyDay()){
			this.anyDate.setDisabled(false);
			this.anyDate.setVisible(true);
			this.anyDate.setSelected(true);
			this.anyDateFromDateRow.setVisible(true);
			this.anyDateToDateRow.setVisible(true);
			this.reviewDateFromDateRow.setVisible(false);
			this.reviewDateToDateRow.setVisible(false);
			this.anyDateRateChangeFromDate.setVisible(true);
			this.anyDateRateChangeFromDate.setValue(DateUtility.getAppDate());
			this.anyDateRateChangeToDate.setVisible(true);
			this.anyDateRateChangeToDate.setValue(aFinanceMain.getMaturityDate());
			excludeFileds.append("CURPRD,");
		}else{
			this.anyDate.setVisible(false);
			this.reviewDates.setSelected(true);
			this.anyDate.setDisabled(true);
			this.anyDateFromDateRow.setVisible(false);
			this.anyDateToDateRow.setVisible(false);
			this.reviewDateFromDateRow.setVisible(true);
			this.reviewDateToDateRow.setVisible(true);
		}
		
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,aFinanceMain.getProductCategory())){
			this.profitDayBasisRow.setVisible(false);
			this.label_RateChangeDialog_FromDate.setValue(Labels.getLabel("label_RateChangeDialog_ODFromDate.value"));
			this.label_RateChangeDialog_ToDate.setValue(Labels.getLabel("label_RateChangeDialog_ODToDate.value"));
			this.label_RateChangeDialog_Rate.setValue(Labels.getLabel("label_RateChangeDialog_ODRate.value"));
			this.recalTypeRow.setVisible(false);
		}
		fillComboBox(profitDaysBasis, aFinanceMain.getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");

		this.baseRateRow.setVisible(true);
		this.rate.setEffectiveRateVisible(true);
		this.rateChange.setVisible(true);
		this.rateAmountRow.setVisible(true);

		this.cbRateChangeFromDate.setVisible(true);
		fillSchFromDates(this.cbRateChangeFromDate,aFinSchData.getFinanceScheduleDetails());
		this.cbRateChangeToDate.setVisible(true);
		if(getFinanceScheduleDetail() != null ) {
			fillSchToDates(this.cbRateChangeToDate,aFinSchData.getFinanceScheduleDetails(), getFinanceScheduleDetail().getSchDate() );
		}else {
			fillSchToDates(this.cbRateChangeToDate,aFinSchData.getFinanceScheduleDetails(), aFinanceMain.getFinStartDate() );
		}
		setEffectiveRate();

		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		if (getFinanceScheduleDetail() != null) {
			if(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE) ||
					getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)){
				if(getFinanceScheduleDetail().getBaseRate()!=null){
					this.rate.setBaseValue(getFinanceScheduleDetail().getBaseRate());
					this.rate.setSpecialValue(getFinanceScheduleDetail().getSplRate());
					this.rate.setMarginValue(getFinanceScheduleDetail().getMrgRate());
					RateDetail rateDetail = RateUtil.rates(getFinanceScheduleDetail().getBaseRate(),aFinanceMain.getFinCcy(),
							getFinanceScheduleDetail().getSplRate(), getFinanceScheduleDetail().getMrgRate(),
							aFinanceMain.getGrcMinRate() , aFinanceMain.getGrcMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				}else {
					this.rateChange.setValue(getFinanceScheduleDetail().getActRate());
					this.rate.setEffRateValue(getFinanceScheduleDetail().getActRate());
				}
				if(profitDayBasisRow.isVisible()){
					fillComboBox(profitDaysBasis, aFinanceMain.getGrcProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
				}
			}else if((getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_REPAY)) ||
					(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {
				if(getFinanceScheduleDetail().getBaseRate()!=null) {
					this.rate.setBaseValue(getFinanceScheduleDetail().getBaseRate());
					this.rate.setSpecialValue(getFinanceScheduleDetail().getSplRate());
					this.rate.setMarginValue(getFinanceScheduleDetail().getMrgRate());
					RateDetail rateDetail = RateUtil.rates(getFinanceScheduleDetail().getBaseRate(),aFinanceMain.getFinCcy(),
							getFinanceScheduleDetail().getSplRate(), getFinanceScheduleDetail().getMrgRate(),
							aFinanceMain.getRpyMinRate() , aFinanceMain.getRpyMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				}else {
					this.rateChange.setValue(getFinanceScheduleDetail().getActRate());
					this.rate.setEffRateValue(getFinanceScheduleDetail().getActRate());
				}
				if(profitDayBasisRow.isVisible()){
					fillComboBox(profitDaysBasis, aFinanceMain.getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
				}
			}
		}
		//TillDate is being Excluded if there is no ratereview in Grace and Payment
		if(!aFinanceMain.isAllowGrcPftRvw() && !aFinanceMain.isAllowRepayRvw()){
			excludeFileds.append("TILLDATE,");
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		}
		
		if(!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,aFinanceMain.getProductCategory())){
			//Check if schedule header is null or not and set the recal type fields.

			String schmethod = StringUtils.trimToEmpty(aFinSchData.getFinanceMain().getScheduleMethod());
			if (schmethod.equals(CalculationConstants.SCHMTHD_PRI_PFT)) {
				excludeFileds.append("ADJMDT,");
			}
			if (getFinScheduleData().getFinanceMain().getNumberOfTerms() == 1) {
				excludeFileds.append("TILLMDT,");
				fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
			} else {
				fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
			}

			changeRecalType();

			this.fromDateRow.setVisible(false);
			if("TILLMDT".equals(StringUtils.trimToEmpty(aFinSchData.getFinanceMain().getRecalType()))){
				this.fromDateRow.setVisible(true);
			}
		}else{
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		}
		logger.debug("Leaving");
	}
	
	
	public void onChange$anyDateRateChangeFromDate(Event event){
		logger.debug("Entering");
		
		Date alwdBackDate	= DateUtility.addDays(DateUtility.getAppDate(),-SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
		
		if(this.anyDateRateChangeFromDate!=null && DateUtility.compare(this.anyDateRateChangeFromDate.getValue(),alwdBackDate)<0){

			throw new WrongValueException(this.anyDateRateChangeFromDate,Labels.getLabel("NUMBER_MINVALUE" ,new String[]{
					Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),alwdBackDate.toString()}));

		}
		
		this.cbRecalFromDate.getItems().clear();
		if (this.cbReCalType.getSelectedIndex() > 0) {
			if ((this.cbReCalType.getSelectedItem().getValue().toString()).equals(CalculationConstants.RPYCHG_TILLDATE)
					|| (this.cbReCalType.getSelectedItem().getValue().toString())
							.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				if (this.anyDateRateChangeFromDate.getValue() != null) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							this.anyDateRateChangeFromDate.getValue(), false);
				}
			} else {
				if (this.anyDateRateChangeFromDate.getValue() != null) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							this.anyDateRateChangeFromDate.getValue(), false);
				} else {
					this.cbRecalFromDate.setSelectedIndex(0);
				}
			}
		}

		logger.debug("Leaving");
	}
	
	
	public void onChange$anyDateRateChangeToDate(Event event){
		logger.debug("Entering");
		if(this.anyDateRateChangeToDate!=null && DateUtility.compare(this.anyDateRateChangeToDate.getValue(),this.anyDateRateChangeFromDate.getValue())<0){
			throw new WrongValueException(this.anyDateRateChangeToDate,Labels.getLabel("NUMBER_MINVALUE" ,new String[]{
					Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value")}));
		}
		if(this.anyDateRateChangeToDate!=null &&DateUtility.compare(this.anyDateRateChangeToDate.getValue(),getFinScheduleData().getFinanceMain().getMaturityDate())>0){
			throw new WrongValueException(this.anyDateRateChangeToDate,Labels.getLabel("NUMBER_MAXVALUE" ,new String[]{
					Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
					DateUtility.formatUtilDate(getFinScheduleData().getFinanceMain().getMaturityDate(), PennantConstants.DBDateFormat)}));
		}
		
		logger.debug("Leaving");
	}
	
	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbRateChangeFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		
		boolean includedPrvSchTerm  = false;
		boolean isOdSelected = true;
		boolean isOverdraft = false;
		
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,getFinScheduleData().getFinanceMain().getProductCategory())){
			isOverdraft=true;
		}
		
		Date curBussDate = DateUtility.getAppDate();
		
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				
				//Review from Date should be Greater than the appdate 
				if(appDateValidationReq && DateUtility.compare(curSchd.getSchDate(),curBussDate)<=0){
					includedPrvSchTerm = false;
					continue;
				}
				
				//Not Review Date
				if (!curSchd.isRvwOnSchDate() ) {
					if(getFinScheduleData().getFinanceMain().isAllowGrcPeriod() && 
							curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) == 0
							&& curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0){
						//Proceed Further
					}else{
						continue;
					}
				}
				
				//Schedule Date Passed last review date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayRvwDate())) {
					continue;
				}

				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}
				
				//Schedule Date Passed last capitalize date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayCpzDate())) {
					continue;
				}

				//Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}
				
				// Excluding Present generated file Schedule Terms
				if(curSchd.getPresentmentId() > 0){
					this.cbRateChangeFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					includedPrvSchTerm = false;
					continue;
				}

				//Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.cbRateChangeFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					includedPrvSchTerm = false;
					continue;
				}
				
				
				if(i == financeScheduleDetails.size() -1){
					continue;
				}
				
				if((i-1 > 0) && !includedPrvSchTerm){
					
					FinanceScheduleDetail prvSchd = financeScheduleDetails.get(i-1);
					
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtility.formatToLongDate(prvSchd.getSchDate())+" "+prvSchd.getSpecifier());
					comboitem.setValue(prvSchd.getSchDate());
					comboitem.setAttribute("fromSpecifier",prvSchd.getSpecifier());
					dateCombobox.appendChild(comboitem);
					
					includedPrvSchTerm = true;
				}
				
				if(i==0){
					includedPrvSchTerm = true;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());

				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else { 
					dateCombobox.appendChild(comboitem);
				}
				//in over draft review from date to be selected the date which is greater than the app date
				if(isOverdraft && isOdSelected ){
					dateCombobox.setSelectedItem(comboitem);
					isOdSelected =false;
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in to date combo */
	public void fillSchToDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillAfter) {
		logger.debug("Entering");
		this.cbRateChangeToDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		Date maturityDate = getFinScheduleData().getFinanceMain().getMaturityDate();
		
		boolean isOdSelected = true;
		boolean isOverdraft = false;
		
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,getFinScheduleData().getFinanceMain().getProductCategory())){
			isOverdraft=true;
		}
		
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//in Overdraft the Review from Date should be Greater than the appdate 
				if(isOverdraft && DateUtility.compare(curSchd.getSchDate(),DateUtility.getAppDate())<0){
					continue;
				}
				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}
				
				//Not Review Date
				if (!curSchd.isRvwOnSchDate()) {
					if (curSchd.getSchDate().compareTo(maturityDate)!=0|| this.cbRateChangeFromDate.getSelectedIndex()<=0) {
						continue;	
					}
				}
				
				//Schedule Date Passed last review date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayRvwDate())) {
					continue;
				}

				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}

				//Schedule Date Passed last capitalize date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayCpzDate())) {
					continue;
				}
				// if Recal Type is TillDate then the Maturity dateis not been allowed 
				if (this.cbReCalType.getSelectedIndex() > 0 && StringUtils.equals(getComboboxValue(this.cbReCalType),
						CalculationConstants.RPYCHG_TILLDATE)) {
					if (i == financeScheduleDetails.size() - 1) {
						continue;
					}
				}
				
				//Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("toSpecifier",curSchd.getSpecifier());
				
				if (getFinanceScheduleDetail() != null &&  curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) >= 0) {
					dateCombobox.appendChild(comboitem);
				} else if(curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
					//in overdraft the next review to date need to be selected the date which is after the from date
					if(isOverdraft && isOdSelected ){
						dateCombobox.setSelectedItem(comboitem);
						isOdSelected =false;
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		doClearMessage();
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			finServiceInstruction.setBaseRate(StringUtils.trimToNull(this.rate.getBaseValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setSplRate(StringUtils.trimToNull(this.rate.getSpecialValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setMargin(this.rate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.rateChange.getValue() != null && !this.rateChange.isReadonly()) {
				if(this.rateChange.getValue().compareTo(BigDecimal.ZERO)<0){
					throw new WrongValueException(this.rateChange, Labels.getLabel("NUMBER_NOT_NEGATIVE",
							new String[] { Labels.getLabel("label_RateChangeDialog_Rate.value") }));
				} 
				/*else if(this.rateChange.getValue().compareTo(finMain.getRpyMaxRate())>0){
			throw new WrongValueException(this.rateChange,Labels.getLabel("NUMBER_MAXVALUE_EQ" ,new String[]{
					Labels.getLabel("label_RateChangeDialog_Rate.value"),finMain.getRpyMaxRate().toString()}));
		}else if(this.rateChange.getValue().compareTo(finMain.getRpyMinRate())<0){
			throw new WrongValueException(this.rateChange, Labels.getLabel("NUMBER_MINVALUE_EQ",
					new String[]{Labels.getLabel("label_RateChangeDialog_Rate.value"),finMain.getRpyMinRate().toString()}));
		}
		 for base*/
				finServiceInstruction.setActualRate(this.rateChange.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// IF Single Rate required based on Origination Selection please comment below try-catch block
		try {
			if ((this.rateAmountRow.isVisible() && this.rateChange.getValue() != null && this.rateChange.getValue().compareTo(BigDecimal.ZERO) > 0)
					&& (StringUtils.isNotEmpty(this.rate.getBaseValue()))) {
				throw new WrongValueException(this.rateChange, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_RateChangeDialog_BaseRate.value"),
						Labels.getLabel("label_RateChangeDialog_Rate.value") }));
			}
			if ((this.rateAmountRow.isVisible() && this.rateChange.getValue() == null)
					&& (StringUtils.isEmpty(this.rate.getBaseValue()))) {
				throw new WrongValueException(this.rateChange, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_RateChangeDialog_BaseRate.value"),
						Labels.getLabel("label_RateChangeDialog_Rate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// BaseRate margin validation
		try {
			if(StringUtils.trimToNull(this.rate.getBaseValue()) == null && this.rate.getMarginValue() != null &&
					this.rate.getMarginValue().compareTo(BigDecimal.ZERO) !=0){
				throw new WrongValueException(rate.getMarginComp(),Labels.getLabel("FIELD_EMPTY",new String[]{
						Labels.getLabel("label_RateChangeDialog_MarginRate.value")}));
				
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Last date
		Date lastPaidDate = getFinScheduleData().getFinanceMain().getFinStartDate();
		Date currBussDate = DateUtility.getAppDate();
		for (int i = 1; i < getFinScheduleData().getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || 
					curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0 ||
					curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0 ||
					curSchd.getSchdInsPaid().compareTo(BigDecimal.ZERO) > 0 ||
					curSchd.getSuplRentPaid().compareTo(BigDecimal.ZERO) > 0 ||
					curSchd.getIncrCostPaid().compareTo(BigDecimal.ZERO) > 0) {
				lastPaidDate = curSchd.getSchDate();
			}
			
			if(curSchd.getSchDate().compareTo(currBussDate) == 0){
				lastPaidDate = currBussDate;
			}else if(curSchd.getSchDate().compareTo(currBussDate) < 0){
				if(curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0){
					lastPaidDate = curSchd.getSchDate();
				}
			}
		}
		
		// Month End Date or Last installment which is Greater should be considered
		Date mnthEndDate = DateUtility.getMonthEndDate(DateUtility.addMonths(currBussDate, -1));
		if(mnthEndDate.compareTo(lastPaidDate) > 0){
			lastPaidDate = DateUtility.addDays(mnthEndDate,1);
		}
		
		// Back Date Allowed Condition Check
		Date alwdBackDate	= DateUtility.addDays(currBussDate,-SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
		if(lastPaidDate.compareTo(alwdBackDate) < 0){
			lastPaidDate = alwdBackDate;
		}
		
		try {
			if(this.reviewDateFromDateRow.isVisible()){
				if (isValidComboValue( this.cbRateChangeFromDate, Labels.getLabel("label_RateChangeDialog_FromDate.value"))) {
					finMain.setEventFromDate((Date)this.cbRateChangeFromDate.getSelectedItem().getValue());
					finServiceInstruction.setFromDate((Date)this.cbRateChangeFromDate.getSelectedItem().getValue());
				}
			}else{
				if(this.anyDateFromDateRow.isVisible() && this.anyDateRateChangeFromDate.getValue()== null){
					throw new WrongValueException(this.anyDateRateChangeFromDate,Labels.getLabel("FIELD_NO_EMPTY",new String[]{
							Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value")}));
				}else if(this.anyDateFromDateRow.isVisible() && this.anyDateRateChangeFromDate.getValue()!= null){
					
					if(this.anyDateRateChangeFromDate.getValue().compareTo(lastPaidDate) < 0){
						throw new WrongValueException(this.anyDateRateChangeFromDate,Labels.getLabel("DATE_ALLOWED_AFTER",new String[]{
								Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
								DateUtil.formatToLongDate(lastPaidDate)}));
					}else if(this.anyDateRateChangeFromDate.getValue().compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) > 0){
						throw new WrongValueException(this.anyDateRateChangeFromDate,Labels.getLabel("DATE_ALLOWED_BEFORE",new String[]{
								Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
								DateUtil.formatToLongDate(getFinScheduleData().getFinanceMain().getMaturityDate())}));
					}
				}
				finMain.setEventFromDate(this.anyDateRateChangeFromDate.getValue());
				finServiceInstruction.setFromDate(this.anyDateRateChangeFromDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.profitDayBasisRow.isVisible()){
				isValidComboValue(this.profitDaysBasis, Labels.getLabel("label_RateChangeDialog_ProfitDaysBasis.value"));
				finServiceInstruction.setPftDaysBasis(getComboboxValue(this.profitDaysBasis));
			}else{
				finServiceInstruction.setPftDaysBasis(getComboboxValue(this.profitDaysBasis));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.reviewDateToDateRow.isVisible()){
				if (isValidComboValue(this.cbRateChangeToDate, Labels.getLabel("label_RateChangeDialog_ToDate.value"))
						&& this.cbRateChangeFromDate.getSelectedIndex() != 0) {
					if (((Date) this.cbRateChangeToDate.getSelectedItem().getValue())
							.compareTo((Date) this.cbRateChangeFromDate.getSelectedItem().getValue()) < 0) {
						throw new WrongValueException(
								this.cbRateChangeToDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",new String[]{
										Labels.getLabel("label_RateChangeDialog_ToDate.value"),
										Labels.getLabel("label_RateChangeDialog_FromDate.value")}));
					} else {
						finMain.setEventToDate((Date)this.cbRateChangeToDate.getSelectedItem().getValue());
					}
					finServiceInstruction.setToDate((Date)this.cbRateChangeToDate.getSelectedItem().getValue());
				}
			}else{
				if(this.anyDateToDateRow.isVisible() && this.anyDateRateChangeToDate.getValue()== null){
					throw new WrongValueException(this.anyDateRateChangeToDate,Labels.getLabel("FIELD_NO_EMPTY",new String[]{
							Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value")}));	
				}else if(this.anyDateToDateRow.isVisible() && this.anyDateRateChangeToDate.getValue()!= null){
					if(this.anyDateRateChangeToDate.getValue().compareTo(lastPaidDate) < 0){
						throw new WrongValueException(this.anyDateRateChangeToDate,Labels.getLabel("DATE_ALLOWED_AFTER",new String[]{
								Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
								DateUtil.formatToLongDate(lastPaidDate)}));
					}else if(this.anyDateRateChangeToDate.getValue().compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) > 0){
						throw new WrongValueException(this.anyDateRateChangeToDate,Labels.getLabel("DATE_ALLOWED_BEFORE",new String[]{
								Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
								DateUtil.formatToLongDate(getFinScheduleData().getFinanceMain().getMaturityDate())}));
					}
				}
				finServiceInstruction.setToDate(this.anyDateRateChangeToDate.getValue());
				finMain.setEventToDate(this.anyDateRateChangeToDate.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try{
			if (this.recalTypeRow.isVisible() && isValidComboValue(this.cbReCalType,
					Labels.getLabel("label_RateChangeDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				finMain.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
				finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
			}else{
				finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		if(this.fromDateRow.isVisible()){
			try {
				if(this.cbRecalFromDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbRecalFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_RateChangeDialog_RecalFromDate.value") }));
				}
				if(this.cbRateChangeFromDate.getSelectedIndex()>0 && ((Date) this.cbRecalFromDate.getSelectedItem().getValue())
						.compareTo((Date) this.cbRateChangeFromDate.getSelectedItem().getValue()) < 0){
					throw new WrongValueException(
							this.cbRecalFromDate,Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[]{ Labels.getLabel("label_RateChangeDialog_RecalFromDate.value"),
									DateUtility.formatToLongDate((Date)this.cbRateChangeToDate.getSelectedItem().getValue())
							}));
				}
				finMain.setRecalFromDate((Date)this.cbRecalFromDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalFromDate(finMain.getRecalFromDate());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if(this.tillDateRow.isVisible()){
			try {
				if(this.cbRecalToDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_RateChangeDialog_RecalToDate.value") }));
				}
				if(this.cbRecalFromDate.getSelectedIndex()>0 && ((Date) this.cbRecalFromDate.getSelectedItem().getValue())
						.compareTo((Date) this.cbRecalToDate.getSelectedItem().getValue()) > 0){
					throw new WrongValueException(
							this.cbRecalToDate,Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[]{ Labels.getLabel("label_RateChangeDialog_RecalToDate.value"),
									DateUtility.formatToLongDate((Date)this.cbRecalFromDate.getSelectedItem().getValue())
							}));
				}
				
				finMain.setRecalToDate((Date)this.cbRecalToDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalToDate(finMain.getRecalFromDate());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
		}
		
		try{
			if(anyDate.isChecked()){
				if(this.anyDateRateChangeFromDate.getValue() != null && this.anyDateRateChangeToDate.getValue() != null){
					if(this.anyDateRateChangeFromDate.getValue().compareTo(this.anyDateRateChangeToDate.getValue()) >= 0){
						throw new WrongValueException(this.anyDateRateChangeToDate,Labels.getLabel("DATE_ALLOWED_AFTER",new String[]{
								Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
								Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value")}));	
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setServiceReqNo(this.serviceReqNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,getFinScheduleData().getFinanceMain().getProductCategory())){
			finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			getFinScheduleData().getFinanceMain().setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		}
		
		finServiceInstruction.setFinReference(getFinScheduleData().getFinanceMain().getFinReference());
		finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_RATECHG);
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);
		
		// Service details calling for Schedule calculation
		setFinScheduleData(rateChangeService.getRateChangeDetails(getFinScheduleData(), finServiceInstruction));
		
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		//Show Error Details in Schedule Maintainance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);

			if(getFinanceMainDialogCtrl()!=null){
				getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
			this.window_RateChangeDialog.onClose();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.rateChange.isVisible()) {
			this.rateChange.setConstraint(new PTDecimalValidator(Labels.getLabel("label_RateChangeDialog_Rate.value"), 
					9, false, false, 0, 9999));
		}
		if (!this.rate.isMarginReadonly()) {
			this.rate.setMarginConstraint(new PTDecimalValidator(Labels.getLabel("label_RateChangeDialog_MarginRate.value"), 9, false, true, -9999, 9999));
		}
		if(this.baseRateRow.isVisible()) {
			this.rate.setBaseConstraint(new PTStringValidator(Labels.getLabel("label_RateChangeDialog_BaseRate.value"),null,false, true));
		}
		logger.debug("Leaving");
	}
	

	/**
	 * Method to clear error message
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		
		this.rate.setBaseConstraint("");
		this.rate.setSpecialConstraint("");
		this.rate.setMarginConstraint("");
		this.rateChange.setConstraint("");
		this.cbRateChangeFromDate.setConstraint("");
		this.cbRateChangeToDate.setConstraint("");
		this.cbReCalType.setConstraint("");
		this.profitDaysBasis.setConstraint("");
		this.serviceReqNo.setConstraint("");
		this.remarks.setConstraint("");
		this.anyDateRateChangeFromDate.setConstraint("");
		this.anyDateRateChangeToDate.setConstraint("");
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method to clear error messages
	 * 
	 * */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		
		this.rate.setBaseErrorMessage("");
		this.rate.setSpecialErrorMessage("");
		this.rate.setMarginErrorMessage("");
		this.rateChange.clearErrorMessage();
		this.cbRateChangeFromDate.clearErrorMessage();
		this.cbRateChangeToDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.profitDaysBasis.clearErrorMessage();
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.anyDateRateChangeFromDate.setErrorMessage("");
		this.anyDateRateChangeToDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddReviewRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(getFinanceScheduleDetail()!=null){
			if(isDataChanged()){
				doSave();
			}else{
				MessageUtil.showError("No Data has been changed.");
			}
		}else{
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}
	
	/**
	 * The Click event is raised when the Close event is occurred. <br>
	 * 
	 * @param event
	 * 
	 * */
	public void onClose(Event event) {
		doClose(false);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		doSetValidation();
		doWriteComponentsToBean();
		logger.debug("Leaving");
	}

	public void onFulfill$rate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.rate.getBaseComp());
		Clients.clearWrongValue(this.rate.getSpecialComp());
		this.rate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent)event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if(StringUtils.equals(rateType, PennantConstants.RATE_BASE)){
			Object dataObject = rate.getBaseObject();
			if (dataObject instanceof String) {
				this.rate.setBaseValue(dataObject.toString());
				this.rate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0),2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.rate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),getFinScheduleData().getFinanceMain().getFinCcy(),
							this.rate.getSpecialValue(), this.rate.getMarginValue()==null?BigDecimal.ZERO:this.rate.getMarginValue(),
									getFinScheduleData().getFinanceMain().getRpyMinRate(), getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if(rateDetail.getErrorDetails() == null){
						this.rate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.rate.setBaseValue("");
					}
				}
			}
		}else if(StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)){
			Object dataObject = rate.getSpecialObject();
			if (dataObject instanceof String) {
				this.rate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.rate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),getFinScheduleData().getFinanceMain().getFinCcy(),
							this.rate.getSpecialValue(),this.rate.getMarginValue()==null?BigDecimal.ZERO:this.rate.getMarginValue(),
									getFinScheduleData().getFinanceMain().getRpyMinRate(), getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if(rateDetail.getErrorDetails() == null){
						this.rate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.rate.setSpecialValue("");
					}
				}
			}
		}else if(StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)){
			setEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}
	

	//Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		changeRecalType();
		logger.debug("Leaving" + event.toString());
	}
	
	private void changeRecalType(){
		if(this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLDATE)){
			this.tillDateRow.setVisible(true);
			this.fromDateRow.setVisible(true);
			
			// RateChangeTodate is not allowed to select the maturity Date when recal type is Tilldate
			if (this.cbRateChangeToDate.getSelectedIndex() > 0
					&& DateUtility.compare((Date) this.cbRateChangeToDate.getSelectedItem().getValue(),
							getFinScheduleData().getFinanceMain().getMaturityDate()) == 0) {
				throw new WrongValueException(this.cbRateChangeToDate,
						Labels.getLabel("label_RateChange_MaturityDate"));

			}
			if(this.cbRateChangeFromDate.getSelectedIndex() > 0 ){
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
				if(this.cbRateChangeToDate.getSelectedIndex() > 0 ){
					fillSchToDates(this.cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRateChangeToDate.getSelectedItem().getValue(), true);
				}
			}else if (anyDateRateChangeFromDate.getValue() != null){
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						anyDateRateChangeFromDate.getValue(), false);
				fillSchToDates(this.cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(),
						anyDateRateChangeToDate.getValue(), true);
			}
			
		}else if(this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLMDT)){
			this.tillDateRow.setVisible(false);
			this.fromDateRow.setVisible(true);
			
			if(this.cbRateChangeFromDate.getSelectedIndex() > 0){
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
			}else if (anyDateRateChangeFromDate.getValue() != null){
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						anyDateRateChangeFromDate.getValue(), false);
			}
		}else {
			this.tillDateRow.setVisible(false);
			this.fromDateRow.setVisible(false);
		}
	}
	
	public void onChange$cbRateChangeToDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.cbRecalFromDate.getItems().clear();
		if (this.cbReCalType.getSelectedIndex() > 0) {
			if ((this.cbReCalType.getSelectedItem().getValue().toString()).equals(CalculationConstants.RPYCHG_TILLDATE)
					|| (this.cbReCalType.getSelectedItem().getValue().toString())
							.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				if (this.cbRateChangeFromDate.getSelectedIndex() > 0) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
				}
			} else {
				if (this.cbRateChangeFromDate.getSelectedIndex() > 0) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
				} else {
					if(this.cbRecalFromDate.getItemCount() > 0){
						this.cbRecalFromDate.setSelectedIndex(0);
					}
				}
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void fillSchToDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails,
			Date fillAfter, boolean includeFromDate) {
		logger.debug("Entering");
		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				if (i == financeScheduleDetails.size() - 1) {
					continue;
				}
				
				if ((curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
						&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0
								&& curSchd.isRepayOnSchDate() && !curSchd.isSchPftPaid())
								|| (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
										&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid()))) {

					comboitem = new Comboitem();
					comboitem.setLabel(
							DateUtility.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
					comboitem.setAttribute("toSpecifier", curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());

					if (includeFromDate && DateUtility.compare(curSchd.getSchDate(), fillAfter) >= 0) {
						dateCombobox.appendChild(comboitem);
						if (getFinanceScheduleDetail() != null
								&& curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
							dateCombobox.setSelectedItem(comboitem);
						}
					} else if (!includeFromDate && DateUtility.compare(curSchd.getSchDate(), fillAfter) > 0) {
						dateCombobox.appendChild(comboitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}
	
	public void onCheck$reviewDates(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		doClearMessage();
		
		this.reviewDateFromDateRow.setVisible(true);
		this.cbRateChangeFromDate.setVisible(true);
		this.reviewDateToDateRow.setVisible(true);
		this.cbRateChangeToDate.setVisible(true);
		this.anyDateFromDateRow.setVisible(false);
		this.anyDateToDateRow.setVisible(false);
		
		StringBuilder excludeFileds=new StringBuilder(",ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,STEPPOS,");
		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		changeRecalType();
		
		fillSchFromDates(this.cbRateChangeFromDate,
				getFinScheduleData().getFinanceScheduleDetails());
		if(getFinScheduleData().getFinanceScheduleDetails() != null && this.cbRateChangeFromDate.getSelectedIndex() > 0) {
			fillSchToDates(this.cbRateChangeToDate,
					getFinScheduleData().getFinanceScheduleDetails(), (Date)this.cbRateChangeFromDate.getSelectedItem().getValue(),false);
		}else {
			fillSchToDates(this.cbRateChangeToDate,
					getFinScheduleData().getFinanceScheduleDetails(), getFinScheduleData().getFinanceMain().getFinStartDate() );
		}
		logger.debug("Leaving " + event.toString());
	}

	
	public void onCheck$anyDate(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		doClearMessage();
		
		this.reviewDateFromDateRow.setVisible(false);
		this.reviewDateToDateRow.setVisible(false);
		this.anyDateFromDateRow.setVisible(true);
		this.anyDateRateChangeFromDate.setVisible(true);
		this.anyDateRateChangeToDate.setVisible(true);
		this.anyDateToDateRow.setVisible(true);
		
		StringBuilder excludeFileds=new StringBuilder(",CURPRD,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,STEPPOS,");
		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		changeRecalType();
		
		logger.debug("Leaving " + event.toString());
	}
	
	
	private void setEffectiveRate() throws InterruptedException{
		if(StringUtils.isBlank(this.rate.getBaseValue())){
			this.rate.setEffRateText(PennantApplicationUtil.formatRate((this.rate.getMarginValue()==null?BigDecimal.ZERO:this.rate.getMarginValue()).doubleValue(),2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(), getFinScheduleData().getFinanceMain().getFinCcy(),
				this.rate.getSpecialValue(),this.rate.getMarginValue()==null?BigDecimal.ZERO:this.rate.getMarginValue(),
						getFinScheduleData().getFinanceMain().getRpyMinRate(), getFinScheduleData().getFinanceMain().getRpyMaxRate());
		if(rateDetail.getErrorDetails() == null){
			this.rate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.rate.setSpecialValue("");
		}
	}
	
	/** To fill to dates based on selected from date*/
	public void onChange$cbRateChangeFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.rate.setBaseValue("");
		this.rate.setSpecialValue("");
		this.rate.setEffRateValue(BigDecimal.ZERO);
		StringBuilder excludeFileds=new StringBuilder(",STEPPOS,");
		
		if(!getFinScheduleData().getFinanceMain().isAllowGrcPftRvw() && !getFinScheduleData().getFinanceMain().isAllowRepayRvw()){
			excludeFileds.append(",TILLDATE");
		}
		
		if(getFinScheduleData().getFinanceMain().getNumberOfTerms() == 1){
			excludeFileds.append(",TILLMDT,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
			fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		}else{
			excludeFileds.append(",ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
			fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(),  excludeFileds.toString());
		}
		if (isValidComboValue(this.cbRateChangeFromDate,Labels.getLabel("label_RateChangeDialog_FromDate.value"))) {
			this.cbRateChangeToDate.getItems().clear();
			String frSpecifier = this.cbRateChangeFromDate.getSelectedItem().getAttribute("fromSpecifier").toString();
			fillSchToDates(this.cbRateChangeToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRateChangeFromDate.getSelectedItem().getValue());
			
			if(getFinScheduleData().getFinanceMain().isAllowGrcPeriod() && 
					(frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE) ||
					frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {
				
				if(getFinScheduleData().getFinanceMain().getGraceBaseRate()!=null) {
					this.rate.setBaseValue(getFinScheduleData().getFinanceMain().getGraceBaseRate());
					this.rate.setSpecialValue(getFinScheduleData().getFinanceMain().getGraceSpecialRate());
					RateDetail rateDetail = RateUtil.rates(getFinScheduleData().getFinanceMain().getGraceBaseRate(),
							getFinScheduleData().getFinanceMain().getFinCcy(),
							getFinScheduleData().getFinanceMain().getGraceSpecialRate(), BigDecimal.ZERO,
							getFinScheduleData().getFinanceMain().getGrcMinRate(), getFinScheduleData().getFinanceMain().getGrcMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				}
				
				if(frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE) && 
						getFinScheduleData().getFinanceMain().getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_NOPAY)){
					
					if(getFinScheduleData().getFinanceMain().getNumberOfTerms() == 1){
						excludeFileds.append(",TILLMDT,CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
						fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(),  excludeFileds.toString());
					}else{
						excludeFileds.append(",CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
						fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(),  excludeFileds.toString());
					}
				}
				
				fillComboBox(profitDaysBasis, getFinScheduleData().getFinanceMain().getGrcProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
			}else if((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY)) ||
					(frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {
				
				if(getFinScheduleData().getFinanceMain().getRepayBaseRate()!=null) {
					this.rate.setBaseValue(getFinScheduleData().getFinanceMain().getRepayBaseRate());
					this.rate.setSpecialValue(getFinScheduleData().getFinanceMain().getRepaySpecialRate());
					RateDetail rateDetail = RateUtil.rates(getFinScheduleData().getFinanceMain().getRepayBaseRate(),
							getFinScheduleData().getFinanceMain().getFinCcy(),
							getFinScheduleData().getFinanceMain().getRepaySpecialRate(), BigDecimal.ZERO,
							getFinScheduleData().getFinanceMain().getRpyMinRate(), getFinScheduleData().getFinanceMain().getRpyMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				}
				fillComboBox(profitDaysBasis, getFinScheduleData().getFinanceMain().getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
			}
		}
		
		changeRecalType();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$cbRecalFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbRecalFromDate.getSelectedIndex() > 0){
			this.cbRecalToDate.getItems().clear();
			//As discussed with Pradeep ,include from date has set to true,if any issue should recheck it
			Date recalFrom  = (Date)this.cbRecalFromDate.getSelectedItem().getValue();
			if(reviewDates.isChecked()){
				if(this.cbRateChangeToDate.getSelectedIndex() > 0){
					Date ratechgTo  = (Date)this.cbRateChangeToDate.getSelectedItem().getValue();
					if(ratechgTo.compareTo(recalFrom) > 0){
						fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), ratechgTo, true);	
					}else{
						fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), recalFrom, true);	
					}
				}
			}else if(anyDate.isChecked()){
					Date ratechgTo  = (Date)this.anyDateRateChangeFromDate.getValue();
					if(DateUtility.compare(ratechgTo,recalFrom) > 0){
						fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), ratechgTo, true);	
					}else{
						fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), recalFrom, true);	
					}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void setRateChangeService(RateChangeService rateChangeService) {
		this.rateChangeService = rateChangeService;
	}
	
}
