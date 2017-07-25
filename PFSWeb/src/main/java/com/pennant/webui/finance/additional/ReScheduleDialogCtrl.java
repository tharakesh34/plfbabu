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
 * FileName    		:  ReScheduleDialogCtrl.java                          	            * 	  
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
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
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/ReScheduleDialog.zul file.
 */
public class ReScheduleDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = Logger.getLogger(ReScheduleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReScheduleDialog; 		
	protected FrequencyBox repayFrq;
	protected FrequencyBox grcPftFrq;
	protected FrequencyBox grcCpzFrq;
	protected FrequencyBox grcRvwFrq;
	protected FrequencyBox repayPftFrq;
	protected FrequencyBox repayRvwFrq;
	protected FrequencyBox repayCpzFrq;
	protected Combobox cbFrqFromDate; 	
	protected Combobox cbSchdMthd; 
	protected Datebox grcPeriodEndDate; 				
	protected Datebox nextGrcRepayDate; 				
	protected Datebox nextRepayDate; 					
	protected Intbox numberOfTerms; 					
	protected Decimalbox repayPftRate; 					
	protected Checkbox pftIntact; 	
	protected RateBox  rate;  	
	protected Uppercasebox serviceReqNo;
	protected Textbox	   remarks;
	
	protected Row row_GrcPeriodEndDate;
	protected Row row_grcNextRepayDate;
	protected Row row_Rate;
	protected Row row_PftIntact;
	protected Row row_GrcFrq;
	protected Row row_GrcRvwFrq;
	
	// not auto wired vars
	private FinScheduleData finScheduleData; 				// overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;
	private transient ReScheduleService reScheduleService;
	private boolean appDateValidationReq = false;
	
	/**
	 * default constructor.<br>
	 */
	public ReScheduleDialogCtrl() {
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
	public void onCreate$window_ReScheduleDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReScheduleDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments
						.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}
			
			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq  = (boolean) arguments.get("appDateValidationReq");
			}

			// we get the FinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIF/FinanceMain here.
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
			this.window_ReScheduleDialog.onClose();
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
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReScheduleDialog.onClose();
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
		this.repayFrq.setMandatoryStyle(true);
		this.grcPftFrq.setMandatoryStyle(true);
		this.grcCpzFrq.setMandatoryStyle(true);
		this.grcRvwFrq.setMandatoryStyle(true);
		this.repayPftFrq.setMandatoryStyle(true);
		this.repayRvwFrq.setMandatoryStyle(true);
		this.repayCpzFrq.setMandatoryStyle(true);
		this.rate.setBaseProperties("BaseRateCode","BRType","BRTypeDesc");
		this.rate.setSpecialProperties("SplRateCode","SRType","SRTypeDesc");
		this.rate.setEffectiveRateVisible(true);
		this.numberOfTerms.setMaxlength(3);
		this.repayPftRate.setMaxlength(13);
		this.repayPftRate.setFormat(PennantConstants.rateFormate9);
		this.repayPftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.repayPftRate.setScale(9);
		this.grcPeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		
		this.row_GrcPeriodEndDate.setVisible(false); 
		this.row_GrcFrq.setVisible(false); 
		this.row_GrcRvwFrq.setVisible(false); 
		logger.debug("Leaving");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		if (!this.repayPftRate.isDisabled()) {
			this.repayPftRate.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_ReScheduleDialog_RepayPftRate.value"),9, false, false,9999));
		}
		if (!this.rate.isMarginReadonly()) {
			this.rate.setMarginConstraint(new PTDecimalValidator(Labels.getLabel("label_ReScheduleDialog_MarginRate.value"), 9, false, true, -9999,9999));
		}
		if(!this.rate.getBaseComp().isReadonly()) {
			this.rate.setBaseConstraint(new PTStringValidator(Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),null,false, true));
		}
	}
	
	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReSchd(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
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
	 * The Click event is raised when the Close event is occurred.
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
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();
		this.window_ReScheduleDialog.onClose();
		logger.debug("Leaving");
	}
	
	public void doClearMessage(){
		this.cbFrqFromDate.setConstraint("");
		this.grcPeriodEndDate.setConstraint("");
		this.nextGrcRepayDate.setConstraint("");
		this.nextRepayDate.setConstraint("");
		this.repayPftRate.setConstraint("");
		this.rate.setBaseConstraint("");
		this.rate.setSpecialConstraint("");
		this.rate.setMarginConstraint("");
		this.serviceReqNo.setConstraint("");
		this.remarks.setConstraint("");
		
		
		this.cbFrqFromDate.setErrorMessage("");
		this.grcPeriodEndDate.setErrorMessage("");
		this.nextGrcRepayDate.setErrorMessage("");
		this.nextRepayDate.setErrorMessage("");
		this.repayPftRate.setErrorMessage("");
		this.rate.setBaseErrorMessage("");
		this.rate.setSpecialErrorMessage("");
		this.rate.setMarginErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		fillComboBox(this.cbSchdMthd,aFinanceMain.getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY," );
		this.cbSchdMthd.setDisabled(true);
		
		if(StringUtils.equals(aFinSchData.getFinanceType().getFinCategory(), FinanceConstants.PRODUCT_QARDHASSAN)){
			this.row_Rate.setVisible(false);
			this.row_PftIntact.setVisible(false);
		}
		
		this.repayFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		this.grcPftFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.grcPftFrq.setValue(aFinanceMain.getGrcPftFrq());
		this.grcCpzFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.grcCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
		this.grcRvwFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.grcRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
		this.repayPftFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		this.repayRvwFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
		this.repayCpzFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
		fillSchFromDates(aFinSchData.getFinanceScheduleDetails());
		if(aFinSchData.getFinanceType().isFinPftUnChanged()){
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		}else{
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}
		
		// Frequencies based on Conditions
		if(!aFinanceMain.isAllowGrcPftRvw()){
			this.grcRvwFrq.setDisabled(true);
		}
		if(!aFinanceMain.isAllowGrcCpz()){
			this.grcCpzFrq.setDisabled(true);
		}
		if(!aFinanceMain.isAllowRepayRvw()){
			this.repayRvwFrq.setDisabled(true);
		}
		if(!aFinanceMain.isAllowRepayCpz()){
			this.repayCpzFrq.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbFrqFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		this.cbFrqFromDate.appendChild(comboitem);
		this.cbFrqFromDate.setSelectedItem(comboitem);
		
		if (financeScheduleDetails != null) {
			Date curBussDate = DateUtility.getAppDate();
			FinanceScheduleDetail prvSchd = null;
			boolean isPrvShcdAdded = false;
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if(i == 0){
					prvSchd = curSchd;
				}

				// Not allow Before Current Business Date
				if(appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					prvSchd = curSchd;
					continue;
				}
				
				// Only allowed if payment amount is greater than Zero
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
					prvSchd = curSchd;
					continue;
				}

				//Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);
					
					prvSchd = curSchd;
					continue;
				}

				if(i == financeScheduleDetails.size() -1){
					continue;
				}
				
				if(prvSchd != null && !isPrvShcdAdded){
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtility.formatToLongDate(prvSchd.getSchDate()) + " " + prvSchd.getSpecifier());
					comboitem.setValue(prvSchd.getSchDate());
					comboitem.setAttribute("fromSpecifier", prvSchd.getSpecifier());
					this.cbFrqFromDate.appendChild(comboitem);
					isPrvShcdAdded = true;
				}
				
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());
				this.cbFrqFromDate.appendChild(comboitem);
				
				if(curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0){
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date fromDate = null;
		String frq = "";
		
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		
		boolean frqValid = true;
		
		try {
			if (this.repayFrq.isValidComboValue()) {
				frq = this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue();
			}
			finServiceInstruction.setRepayFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (this.row_GrcFrq.isVisible() && this.grcPftFrq.isValidComboValue()) {
				frq = this.grcPftFrq.getValue() == null ? "" : this.grcPftFrq.getValue();
			}
			finServiceInstruction.setGrcPftFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (financeMain.isAllowGrcCpz() && this.grcCpzFrq.isValidComboValue()) {
				frq = this.grcCpzFrq.getValue() == null ? "" : this.grcCpzFrq.getValue();
			}
			finServiceInstruction.setGrcCpzFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (financeMain.isAllowGrcPftRvw() && this.grcRvwFrq.isValidComboValue()) {
				frq = this.grcRvwFrq.getValue() == null ? "" : this.grcRvwFrq.getValue();
			}
			finServiceInstruction.setGrcRvwFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (this.repayPftFrq.isValidComboValue()) {
				frq = this.repayPftFrq.getValue() == null ? "" : this.repayPftFrq.getValue();
			}
			finServiceInstruction.setRepayPftFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (financeMain.isAllowRepayRvw() && this.repayRvwFrq.isValidComboValue()) {
				frq = this.repayRvwFrq.getValue() == null ? "" : this.repayRvwFrq.getValue();
			}
			finServiceInstruction.setRepayRvwFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (financeMain.isAllowRepayCpz() && this.repayCpzFrq.isValidComboValue()) {
				frq = this.repayCpzFrq.getValue() == null ? "" : this.repayCpzFrq.getValue();
			}
			finServiceInstruction.setRepayCpzFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			if (isValidComboValue(this.cbFrqFromDate, Labels.getLabel("label_ReScheduleDialog_FromDate.value"))) {
				fromDate = (Date)this.cbFrqFromDate.getSelectedItem().getValue();
				finServiceInstruction.setFromDate((Date)this.cbFrqFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (isValidComboValue(this.cbSchdMthd, Labels.getLabel("label_ReScheduleDialog_SchdMthd.value")) && this.cbSchdMthd.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.cbSchdMthd));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if(this.row_GrcPeriodEndDate.isVisible()){
			try {
				if(fromDate != null && fromDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0){
					if(this.grcPeriodEndDate.getValue() == null){
						throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("FIELD_IS_MAND", new String[]{
								Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value")}));
					}else {
						if(this.grcPeriodEndDate.getValue().compareTo(financeMain.getFinStartDate()) < 0){
							throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[]{
									Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value"), Labels.getLabel("label_ReScheduleDialog_FinStartDate.value")}));
						}else if(!financeMain.isNewRecord() && 
								!StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
							
							if(StringUtils.trimToEmpty(getFinScheduleData().getFinanceType().getFinCategory()).equals(FinanceConstants.PRODUCT_IJARAH)||
							   StringUtils.trimToEmpty(getFinScheduleData().getFinanceType().getFinCategory()).equals(FinanceConstants.PRODUCT_FWIJARAH)){
								Date curBussDate = DateUtility.getAppDate();
								if(this.grcPeriodEndDate.getValue().compareTo(curBussDate) <= 0){
									throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[]{
											Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value"), Labels.getLabel("label_ReScheduleDialog_CurBussDate.value")}));
								}
							}
						}
					}
				}
				finServiceInstruction.setGrcPeriodEndDate(this.grcPeriodEndDate.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
		}
		try {
			finServiceInstruction.setNextGrcRepayDate(this.nextGrcRepayDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if(this.nextGrcRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null){
				if(this.nextGrcRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) > 0){
					throw new WrongValueException(this.nextGrcRepayDate, Labels.getLabel("DATE_ALLOWED_MAXDATE", new String[]{
							Labels.getLabel("label_ReScheduleDialog_NextGrcRepayDate.value"), Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value")}));
				}else if(this.nextGrcRepayDate.getValue().compareTo(financeMain.getFinStartDate()) <= 0){
						throw new WrongValueException(this.nextGrcRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
								Labels.getLabel("label_ReScheduleDialog_NextGrcRepayDate.value"), Labels.getLabel("label_ReScheduleDialog_FinStartDate.value")}));
					}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.nextRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null){
				if(this.nextRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) <= 0){
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
							Labels.getLabel("label_ReScheduleDialog_NextRepayDate.value"), Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value")}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.nextRepayDate.getValue() != null && fromDate != null){
				if(this.nextRepayDate.getValue().compareTo(fromDate) <= 0){
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
							Labels.getLabel("label_ReScheduleDialog_NextRepayDate.value"),DateUtility.formatToShortDate(fromDate)}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.nextRepayDate.getValue() != null && this.nextGrcRepayDate.getValue() != null){
				if(this.nextRepayDate.getValue().compareTo(this.nextGrcRepayDate.getValue()) <= 0){
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
							Labels.getLabel("label_ReScheduleDialog_NextRepayDate.value"), Labels.getLabel("label_ReScheduleDialog_NextGrcRepayDate.value")}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			finServiceInstruction.setNextRepayDate(this.nextRepayDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if(this.numberOfTerms.getValue() == null ){
				throw new WrongValueException(this.numberOfTerms, Labels.getLabel("FIELD_IS_MAND", new String[]{
						Labels.getLabel("label_ReScheduleDialog_NumberOftTerms.value")}));
			}
			if(this.numberOfTerms.intValue() <= 0 ){
				throw new WrongValueException(this.numberOfTerms, Labels.getLabel("NUMBER_MINVALUE", new String[]{
						Labels.getLabel("label_ReScheduleDialog_NumberOftTerms.value"), " 0 "}));
			}
			if(frqValid){
				Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
				int termsAllowed =FrequencyUtil.getTerms(this.repayFrq.getValue(), 
						financeMain.getNextRepayPftDate(), appEndDate, true, false).getTerms();
				if(this.numberOfTerms.intValue() > termsAllowed){
					throw new WrongValueException(this.numberOfTerms,Labels.getLabel("NUMBER_MAXVALUE",new String[]{
							Labels.getLabel("label_ReScheduleDialog_NumberOftTerms.value"),String.valueOf(termsAllowed)}));
				}
			}
			finServiceInstruction.setTerms(this.numberOfTerms.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setActualRate(this.repayPftRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
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
			if(StringUtils.trimToNull(this.rate.getBaseValue()) == null && this.rate.getMarginValue() != null &&
					this.rate.getMarginValue().compareTo(BigDecimal.ZERO) != 0){
				throw new WrongValueException(rate.getMarginComp(),Labels.getLabel("FIELD_EMPTY",new String[]{
						Labels.getLabel("label_ReScheduleDialog_MarginRate.value")}));
				
			}
			finServiceInstruction.setMargin(this.rate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// IF Single Rate required based on Origination Selection please comment below try-catch block
		try {
			if (this.row_Rate.isVisible() && ((this.repayPftRate.getValue() != null && this.repayPftRate.getValue().compareTo(BigDecimal.ZERO) > 0)
					&& (StringUtils.isNotEmpty(this.rate.getBaseValue())))) {
				throw new WrongValueException(this.repayPftRate, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),
						Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_Rate.isVisible() && (this.repayPftRate.getValue() == null
					&& (StringUtils.isEmpty(this.rate.getBaseValue())))) {
				throw new WrongValueException(this.repayPftRate, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),
						Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value") }));
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
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		finServiceInstruction.setPftIntact(this.pftIntact.isChecked());
		finServiceInstruction.setFinReference(financeMain.getFinReference());
		finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_RESCHD);

		// Service details calling for Schedule calculation
		setFinScheduleData(reScheduleService.doReSchedule(getFinScheduleData(), finServiceInstruction));
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);
		
		//Show Error Details in Schedule Maintenance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);
			if(getFinanceMainDialogCtrl()!=null){
				try {
					getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		
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
	
	public void onChange$cbFrqFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.row_GrcPeriodEndDate.setVisible(false); 
		this.row_GrcFrq.setVisible(false); 
		this.row_GrcRvwFrq.setVisible(false); 
		this.row_grcNextRepayDate.setVisible(true);
		
		this.cbSchdMthd.setDisabled(true);
		fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,");
		
		if(this.cbFrqFromDate.getSelectedIndex() != 0){
			Date fromDate = (Date)this.cbFrqFromDate.getSelectedItem().getValue();
			
			List<FinanceScheduleDetail> financeScheduleDetails = getFinScheduleData().getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {
					
					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
					
					if(curSchd.isRepayOnSchDate() ||
							(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) || 
							fromDate.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0){
						if(fromDate.compareTo(curSchd.getSchDate()) == 0){
							if(fromDate.compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) < 0){
								this.row_GrcPeriodEndDate.setVisible(true); 
								this.row_GrcFrq.setVisible(true); 
								this.row_GrcRvwFrq.setVisible(true); 
								fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
							}else{
								fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,");
							}
							break;
						}
					}
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	public void onSelectCode$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		this.grcPeriodEndDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectMonth$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		this.grcPeriodEndDate.setText("");
		logger.debug("Leaving" + event.toString());
	}
	
	public void onSelectDay$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		this.grcPeriodEndDate.setText("");
		logger.debug("Leaving" + event.toString());
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

}
