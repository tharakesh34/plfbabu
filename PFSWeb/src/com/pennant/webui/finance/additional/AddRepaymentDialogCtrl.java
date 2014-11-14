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
 * FileName    		:  WIApplyChangeDialogCtrl.java                          	            * 	  
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/WIAddRateChange.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AddRepaymentDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 454600127282110738L;
	private final static Logger logger = Logger.getLogger(AddRepaymentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ChangeRepaymentDialog; 	// autowired
	protected Combobox cbReCalType; 				// autowired
	protected Combobox cbRepayFromDate; 			// autowired
	protected Combobox cbRepayToDate; 				// autowired
	protected Combobox cbFromDate; 			// autowired
	protected Combobox cbTillDate; 					// autowired
	protected Combobox cbSchdMthd; 					// autowired
	protected Decimalbox wIAmount; 					// autowired
	protected Row fromDateRow;                      // autowired
	protected Row tillDateRow;
	protected Row pftIntactRow;						// autowired
	protected Checkbox pftIntact; 					// autowired
	protected Row numOfTermsRow;
	protected Intbox adjTerms; 						// autowired
	protected Label label_ChangeRepaymentDialog_TillToDate; 	// autowired
   
	// not auto wired vars
	private FinScheduleData finScheduleData; 				// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 	// overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient int oldVar_wIRateChangeFromDate;
	private transient int oldVar_wIRateChangeToDate;
	private transient int oldVar_wIRateChangeTillFromDate;
	private transient int oldVar_wIRateChangeTillDate;
	private transient BigDecimal oldVar_wIAmount;
	private transient int oldVar_wISchdMethd;
	private transient int oldVar_wIReCalType;
	private transient boolean oldVar_pftIntact;	
	private transient int oldVar_adjTerms;	

	private transient int overrideCount = 0;
	private transient boolean validationOn;
	Calendar calender = Calendar.getInstance();
	static final List<ValueLabel>	      schMthds	              	  = PennantAppUtil.getScheduleMethod();
	static final List<ValueLabel>	      recalTypes	              = PennantStaticListUtil.getSchCalCodes();
	private transient String excludeFields = ",EQUAL,PRI_PFT,PRI,";
	private transient String frSpecifier = "";
	private transient String toSpecifier = "";
	
	private BigDecimal totalAlwRpyAmt = BigDecimal.ZERO;

	/**
	 * default constructor.<br>
	 */
	public AddRepaymentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceMain object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ChangeRepaymentDialog(Event event)
	throws Exception {
		logger.debug(event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) args.get("finScheduleData");
			setFinScheduleData(this.finScheduleData);
		} else {
			setFinScheduleData(null);
		}
		
		if (args.containsKey("financeScheduleDetail")) {
			setFinanceScheduleDetail((FinanceScheduleDetail) args.get("financeScheduleDetail"));
		} else {
			setFinanceScheduleDetail(null);
		}

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) args.get("financeMainDialogCtrl"));
		} else {
			setFinanceMainDialogCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_ChangeRepaymentDialog.doModal();
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) throws InterruptedException {
		logger.debug("Entering");
		if (aFinScheduleData == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinScheduleData = new FinScheduleData();

			setFinScheduleData(aFinScheduleData);
		} else {
			setFinScheduleData(aFinScheduleData);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ChangeRepaymentDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_wIRateChangeFromDate = this.cbRepayFromDate.getSelectedIndex();
		this.oldVar_wIRateChangeToDate = this.cbRepayToDate.getSelectedIndex();
		this.oldVar_wIRateChangeTillDate = this.cbTillDate.getSelectedIndex();
		this.oldVar_wIRateChangeTillFromDate = this.cbFromDate.getSelectedIndex();
		this.oldVar_wIAmount = this.wIAmount.getValue();
		this.oldVar_wISchdMethd = this.cbSchdMthd.getSelectedIndex();
		this.oldVar_wIReCalType = this.cbReCalType.getSelectedIndex();
		this.oldVar_pftIntact = this.pftIntact.isChecked();
		this.oldVar_adjTerms = this.adjTerms.intValue();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cbRepayFromDate.setDisabled(true);
		this.cbRepayToDate.setDisabled(true);
		this.wIAmount.setDisabled(true);
		this.cbReCalType.setDisabled(true);
		this.cbTillDate.setDisabled(true);
		this.cbFromDate.setDisabled(true);
		this.cbSchdMthd.setDisabled(true);
		this.adjTerms.setReadonly(true);
		this.pftIntact.setDisabled(true);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.wIAmount.setMaxlength(18);
		this.wIAmount.setFormat(PennantApplicationUtil
				.getAmountFormate(getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		this.adjTerms.setMaxlength(2);
		logger.debug("Leaving");

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeRepay(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(getFinanceScheduleDetail()!=null){
			if(isDataChanged()){
				doSave();
			}else{
				PTMessageUtils.showErrorMessage("No Data has been changed.");
			}
		}else{
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when close event is occurred. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * */
	public void onClose(Event event)throws InterruptedException{
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
		
	}

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		
		boolean close = true;
		doClearMessage();
		
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			this.window_ChangeRepaymentDialog.onClose();
		}

		logger.debug("Leaving");
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
		this.window_ChangeRepaymentDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering"); 
		
		if(this.oldVar_wIAmount != this.wIAmount.getValue()) {
			return true;
		}
		if (this.oldVar_wIRateChangeFromDate != this.cbRepayFromDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_wIRateChangeToDate != this.cbRepayToDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_wIRateChangeTillDate != this.cbTillDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_wIRateChangeTillFromDate != this.cbFromDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_wISchdMethd != this.cbSchdMthd.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_wIReCalType != this.cbReCalType.getSelectedIndex()) {
			return true;
		}
		if(this.oldVar_pftIntact != this.pftIntact.isChecked()){
			return true;
		}
		if(this.oldVar_adjTerms != this.adjTerms.intValue()){
			return true;
		}
		
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		
		fillSchFromDates(this.cbRepayFromDate, aFinSchData.getFinanceScheduleDetails());

		if(getFinanceScheduleDetail() != null ) {
			fillSchToDates(this.cbRepayToDate, aFinSchData.getFinanceScheduleDetails(), getFinanceScheduleDetail().getSchDate(),true);
		}else {
			fillSchToDates(this.cbRepayToDate, aFinSchData.getFinanceScheduleDetails(), aFinSchData.getFinanceMain().getFinStartDate(),true);
		}
		
		if(aFinSchData.getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLDATE)){
			fillSchToDates(this.cbFromDate, aFinSchData.getFinanceScheduleDetails(), aFinSchData.getFinanceMain().getFinStartDate(),false);
			fillSchToDates(this.cbTillDate, aFinSchData.getFinanceScheduleDetails(), aFinSchData.getFinanceMain().getFinStartDate(),false);
		} else if(aFinSchData.getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLMDT)){
			fillSchToDates(this.cbTillDate, aFinSchData.getFinanceScheduleDetails(), aFinSchData.getFinanceMain().getFinStartDate(),false);
		}
		
	
		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		if (getFinanceScheduleDetail() != null) {
			if (getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE)) {
				this.wIAmount.setValue(PennantAppUtil.formateAmount(getFinanceScheduleDetail().getPrincipalSchd(),
						aFinSchData.getFinanceMain().getLovDescFinFormatter()));
			} else {
				this.wIAmount.setValue(PennantAppUtil.formateAmount( getFinanceScheduleDetail().getRepayAmount(),
						aFinSchData.getFinanceMain().getLovDescFinFormatter()));
			}
		}

		if(getFinanceScheduleDetail() != null) {
			if(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE) ||
					getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE_END)) {
				fillComboBox(this.cbSchdMthd,getFinanceScheduleDetail().getSchdMethod(), schMthds, excludeFields);
				this.cbSchdMthd.setDisabled(false);
				this.wIAmount.setDisabled(true);
			}else {
				fillComboBox(this.cbSchdMthd,getFinanceScheduleDetail().getSchdMethod(), schMthds, ",GRCNDPAY," );
				this.cbSchdMthd.setDisabled(true);
				this.wIAmount.setDisabled(false);
			}
		}else {
			fillComboBox(this.cbSchdMthd, "", schMthds, ",GRCNDPAY,");
			this.cbSchdMthd.setDisabled(true);
		}
		//Check if schedule header is null or not and set the recal type fields.
		if(aFinSchData.getFinanceMain() != null ) {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), recalTypes, ",ADDLAST,ADJTERMS,CURPRD,");
		}
		if(getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			this.fromDateRow.setVisible(true);
			this.tillDateRow.setVisible(true);
			this.label_ChangeRepaymentDialog_TillToDate.setValue(Labels.getLabel("label_ChangeRepaymentDialog_CalToDate.value"));
		}
		if(getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.tillDateRow.setVisible(true);
			this.label_ChangeRepaymentDialog_TillToDate.setValue(Labels.getLabel("label_ChangeRepaymentDialog_CalFromDate.value"));
		}

		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbRepayFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		
		Date curBussDate = (Date)SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		boolean curTermExcluded = false;
		
		totalAlwRpyAmt = BigDecimal.ZERO;
		
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				
				//Not before Current Business date & not Current Schedule Term Date
				if(curBussDate.compareTo(curSchd.getSchDate()) > 0){
					continue;
				} else if(curBussDate.compareTo(curSchd.getSchDate()) == 0){
					curTermExcluded = true;
				} else{
					if(!curTermExcluded){
						curTermExcluded = true;
					}
				}
				
				//Maximum Outstanding Repay amount Allowed to Change
				if(!curTermExcluded){
					totalAlwRpyAmt = totalAlwRpyAmt.add(curSchd.getPrincipalSchd()).add(curSchd.getProfitSchd()).
							add(curSchd.getDefPrincipalSchd()).add(curSchd.getDefProfitSchd()).subtract(curSchd.getSchdPriPaid()).
							subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPriPaid()).subtract(curSchd.getDefSchdPftPaid());
				}else{
					totalAlwRpyAmt = totalAlwRpyAmt.add(curSchd.getPrincipalSchd()).add(curSchd.getDefPrincipalSchd()).
							subtract(curSchd.getSchdPriPaid()).subtract(curSchd.getDefSchdPriPaid());
				}
				
				if(i == financeScheduleDetails.size()-1){
					continue;	
				}
				
				if ((curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) && 
						((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate() && !curSchd.isSchPftPaid()) ||
						(curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0 && curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid()))) {

					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(),PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
					comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());

					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null) {
						if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
							dateCombobox.setSelectedItem(comboitem);
						}
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	public void fillSchToDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillAfter, boolean includeFromDate) {
		logger.debug("Entering");
		if(dateCombobox.getId().equals("cbRepayToDate")) {
			this.cbRepayToDate.getItems().clear();
		} else if(dateCombobox.getId().equals("cbTillDate")) {
			this.cbTillDate.getItems().clear();
		}
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if ((curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))  
						&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate() && !curSchd.isSchPftPaid()) ||
								(curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0 && curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid()))) {
					
					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
					comboitem.setAttribute("toSpecifier",curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());
					if(includeFromDate && curSchd.getSchDate().compareTo(fillAfter) >= 0) {
						if(i != financeScheduleDetails.size()-1){
							dateCombobox.appendChild(comboitem);
							if(getFinanceScheduleDetail() != null && curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
								dateCombobox.setSelectedItem(comboitem);
							}
						}
					} else if(!includeFromDate && curSchd.getSchDate().compareTo(fillAfter) > 0) {
						dateCombobox.appendChild(comboitem);
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
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String recalScheduleMethod = "";
		try {
			if(this.wIAmount.getValue()!=null) {
				if(this.wIAmount.getValue().compareTo(BigDecimal.ZERO)==-1) {
					throw new WrongValueException(this.wIAmount,Labels.getLabel("AMOUNT_NO_NUMBER",
									new String[]{Labels.getLabel("label_ChangeRepaymentDialog_RepayAmount.value")}));
				}
				
				BigDecimal amount = PennantApplicationUtil.unFormateAmount(this.wIAmount.getValue(), getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				if(amount.compareTo(totalAlwRpyAmt) > 0){
					throw new WrongValueException(this.wIAmount,Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[]{Labels.getLabel("label_ChangeRepaymentDialog_RepayAmount.value"),
							PennantApplicationUtil.amountFormate(totalAlwRpyAmt, getFinScheduleData().getFinanceMain().getLovDescFinFormatter())}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(
					this.cbRepayFromDate,
					Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value"))) {
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)this.cbRepayFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(
					this.cbRepayToDate,
					Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))
					&& this.cbRepayFromDate.getSelectedIndex() != 0) {
				if (((Date) this.cbRepayToDate.getSelectedItem()
						.getValue())
						.compareTo((Date) this.cbRepayFromDate
								.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(
							this.cbRepayToDate,
							Labels.getLabel(
									"DATE_ALLOWED_AFTER",
									new String[]{
											Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"),
											Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value")}));
				} else {
					getFinScheduleData().getFinanceMain().setEventToDate((Date)this.cbRepayToDate.getSelectedItem().getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(!frSpecifier.equals(toSpecifier)) {
				if(!((frSpecifier.equals(CalculationConstants.GRACE) || frSpecifier.equals(CalculationConstants.GRACE_END)) && 
						(toSpecifier.equals(CalculationConstants.GRACE) || (toSpecifier.equals(CalculationConstants.GRACE_END)))) ||
						((frSpecifier.equals(CalculationConstants.REPAY) ||frSpecifier.equals(CalculationConstants.MATURITY)) && 
								(toSpecifier.equals(CalculationConstants.REPAY) || (toSpecifier.equals(CalculationConstants.MATURITY))))) {
					throw new WrongValueException(
							this.cbRepayToDate,
							Labels.getLabel(
									"DATES_SAME_PERIOD",
									new String[]{
											Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"),
											Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value")}));
			  }
			}
			if (isValidComboValue(
					this.cbSchdMthd,
					Labels.getLabel("label_ChangeRepaymentDialog_RecalType.value"))
					&& this.cbSchdMthd.getSelectedIndex() != 0) {
				recalScheduleMethod = this.cbSchdMthd.getSelectedItem().getValue().toString();
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try{
			if (isValidComboValue(
					this.cbReCalType,
					Labels.getLabel("label_ChangeRepaymentDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				getFinScheduleData().getFinanceMain().setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());

			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(this.fromDateRow.isVisible()){
			try {
				if(this.cbFromDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value") }));
				}
				if(this.cbRepayToDate.getSelectedIndex()>0 && ((Date) this.cbFromDate.getSelectedItem()
						.getValue())
						.compareTo((Date) this.cbRepayToDate
								.getSelectedItem().getValue()) < 0){
					throw new WrongValueException(
							this.cbFromDate,
							Labels.getLabel(
									"DATE_ALLOWED_AFTER",
									new String[]{
											Labels.getLabel("label_ChangeRepaymentDialog_TillDate.value"),
											PennantAppUtil.formateDate((Date)this.cbRepayToDate.getSelectedItem().getValue(),
													PennantConstants.dateFormate)
									}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		if(this.tillDateRow.isVisible()){
			try {
				if(this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ChangeRepaymentDialog_TillDate.value") }));
				}
				if(this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)){
					if(this.cbFromDate.getSelectedIndex()>0 && ((Date) this.cbTillDate.getSelectedItem()
							.getValue())
							.compareTo((Date) this.cbRepayToDate
									.getSelectedItem().getValue()) < 0){
						throw new WrongValueException(
								this.cbTillDate,
								Labels.getLabel(
										"DATE_ALLOWED_AFTER",
										new String[]{
												Labels.getLabel("label_ChangeRepaymentDialog_TillDate.value"),
												PennantAppUtil.formateDate((Date)this.cbRepayToDate.getSelectedItem().getValue(),
														PennantConstants.dateFormate)
										}));
					}
				} else {
					if(this.cbRepayToDate.getSelectedIndex()>0 && ((Date) this.cbTillDate.getSelectedItem()
							.getValue())
							.compareTo((Date) this.cbRepayToDate
									.getSelectedItem().getValue()) < 0){
						throw new WrongValueException(
								this.cbTillDate,
								Labels.getLabel(
										"DATE_ALLOWED_AFTER",
										new String[]{
												Labels.getLabel("label_ChangeRepaymentDialog_TillDate.value"),
												PennantAppUtil.formateDate((Date)this.cbRepayToDate.getSelectedItem().getValue(),
														PennantConstants.dateFormate)
										}));
					}
				}
				
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		getFinScheduleData().getFinanceMain().setPftIntact(false);
		try {
			getFinScheduleData().getFinanceMain().setPftIntact(this.pftIntact.isChecked());
		}catch(WrongValueException we) {
			wve.add(we);
		}
		
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		if(!getComboboxValue(this.cbSchdMthd).equals("#") && 
				!getComboboxValue(this.cbSchdMthd).equals(getFinScheduleData().getFinanceType().getFinSchdMthd()) && 
				overrideCount == 0) {
			doClearMessage();
			if(this.cbRepayFromDate.getSelectedItem().getAttribute("fromSpecifier").equals("G")) {
				if(!showMessage(ErrorUtil.getErrorDetail(new ErrorDetails("scheduleMethod", "W0002",
						new String[]{},
						new String[]{recalScheduleMethod}),
						getUserWorkspace().getUserLanguage()).getError(),Labels.getLabel("message.Overide"))){
					return;
				}
			}
		}
		
		getFinScheduleData().getFinanceMain().setAdjTerms(0);
		if(this.numOfTermsRow.isVisible()) {
			try {
				getFinScheduleData().getFinanceMain().setAdjTerms(this.adjTerms.getValue());
			}catch(WrongValueException we) {
				wve.add(we);
			}
		}
		
		if(this.tillDateRow.isVisible()){
			if(this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT)){
				Date fromDate = (Date) this.cbTillDate.getSelectedItem().getValue();
				getFinScheduleData().getFinanceMain().setRecalFromDate(fromDate);
				getFinScheduleData().getFinanceMain().setRecalToDate(getFinScheduleData().getFinanceMain().getMaturityDate());
			}  else {
				getFinScheduleData().getFinanceMain().setRecalFromDate((Date)this.cbFromDate.getSelectedItem().getValue());
				getFinScheduleData().getFinanceMain().setRecalToDate((Date)this.cbTillDate.getSelectedItem().getValue());
			}
			
			setFinScheduleData(ScheduleCalculator.changeRepay(
					getFinScheduleData(), PennantAppUtil.unFormateAmount(
							this.wIAmount.getValue(),getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),recalScheduleMethod));
		}else{
			getFinScheduleData().getFinanceMain().setRecalToDate(getFinScheduleData().getFinanceMain().getMaturityDate());
			setFinScheduleData(ScheduleCalculator.changeRepay(
					getFinScheduleData(), PennantAppUtil.unFormateAmount(
							this.wIAmount.getValue(),getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),recalScheduleMethod));
		}
		
		//Show Error Details in Schedule Maintenance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			PTMessageUtils.showErrorMessage(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);
			if(getFinanceMainDialogCtrl()!=null){
				try {
					getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
	}

	/**
	 * Method to clear error message
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.wIAmount.clearErrorMessage();
		this.cbRepayFromDate.clearErrorMessage();
		this.cbRepayToDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.cbTillDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	//Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		String selectedRecalType = this.cbReCalType.getSelectedItem().getValue().toString();
		
		if (selectedRecalType.equals(CalculationConstants.RPYCHG_TILLDATE) || 
				selectedRecalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			
			if(selectedRecalType.equals(CalculationConstants.RPYCHG_TILLDATE)){
				
				try {
					if(isValidComboValue(this.cbRepayToDate,Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))){
						fillSchToDates(this.cbFromDate, getFinScheduleData().getFinanceScheduleDetails(),
								(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
					}
				} catch (WrongValueException e) {
					this.cbReCalType.setSelectedIndex(0);
					throw e;
				}
				
				this.fromDateRow.setVisible(true);
				this.label_ChangeRepaymentDialog_TillToDate.setValue(Labels.getLabel("label_ChangeRepaymentDialog_CalToDate.value"));
			} else {
				this.fromDateRow.setVisible(false);
				this.label_ChangeRepaymentDialog_TillToDate.setValue(Labels.getLabel("label_ChangeRepaymentDialog_CalFromDate.value"));
			}
			
			try {
				if (isValidComboValue(this.cbRepayToDate,Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))) {
					fillSchToDates(this.cbTillDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
				}
			} catch (WrongValueException e) {
				this.cbReCalType.setSelectedIndex(0);
				this.fromDateRow.setVisible(false);
				throw e;
			}
			this.tillDateRow.setVisible(true);
			this.numOfTermsRow.setVisible(false);
		} else if (selectedRecalType.equals(CalculationConstants.RPYCHG_ADDRECAL)
				|| selectedRecalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
			
			if(this.cbTillDate.getItemCount() > 0){
				this.cbTillDate.setSelectedIndex(0);
			}
			this.fromDateRow.setVisible(false);
			this.tillDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(true);
		} else {
			if(this.cbTillDate.getItemCount() > 0){
				this.cbTillDate.setSelectedIndex(0);
			}
			this.tillDateRow.setVisible(false);
			this.fromDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$cbFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		fillSchToDates(this.cbTillDate, getFinScheduleData().getFinanceScheduleDetails(),
				(Date) this.cbFromDate.getSelectedItem().getValue(), false);
		
		logger.debug("Leaving" + event.toString());
	}
	

	public void onChange$cbSchdMthd(Event event) {
		logger.debug("Entering" + event.toString());
		overrideCount = 0;
		if((this.cbSchdMthd.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.PFT)) || (this.cbSchdMthd.getSelectedItem().getValue().toString()
						.equals(CalculationConstants.NOPAY))){
			this.wIAmount.setValue(PennantAppUtil.formateAmount(
					BigDecimal.ZERO,
					getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
			this.wIAmount.setDisabled(true);
		}else {
			this.wIAmount.setValue(BigDecimal.ZERO);
			this.wIAmount.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$cbRepayToDate(Event event) {
		logger.debug("Entering" + event.toString());
		fillSchdMethod();
		
		if(finScheduleData.getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLDATE)){
			if(this.cbRepayToDate.getSelectedIndex() > 0) {
				fillSchToDates(this.cbFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
			}else {
				this.cbFromDate.setSelectedIndex(0);
			}
		} else {
			if(this.cbRepayToDate.getSelectedIndex() > 0) {
				fillSchToDates(this.cbTillDate, getFinScheduleData().getFinanceScheduleDetails(),
						(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
			}else {
				this.cbTillDate.setSelectedIndex(0);
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	
	public void onChange$cbRepayFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		fillSchdMethod();
		this.cbRepayToDate.setDisabled(true);		
		if (isValidComboValue(this.cbRepayFromDate,Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value"))) {
			fillSchToDates(this.cbRepayToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRepayFromDate.getSelectedItem().getValue(), true);
			this.cbRepayToDate.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method to show error message
	 * @param e (Exception)
	 * @throws InterruptedException 
	 * @return true/false (boolean)
	 * */
	private boolean showMessage(String msg, String title) throws InterruptedException {
		logger.debug("Entering");
		MultiLineMessageBox.doErrorTemplate();
		int conf = MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE,
				MultiLineMessageBox.EXCLAMATION, true);
		if (conf == MultiLineMessageBox.IGNORE) {
			overrideCount = 1;
			logger.debug("Leaving");
			return true;
		}
		logger.debug("Leaving");
		return false;
	}
	
	/**
	 * Method to fill schedule methods based on selected dates.
	 * 
	 * */
	private void fillSchdMethod() {
		logger.debug("Entering");
		if(this.cbRepayFromDate.getSelectedIndex() > 0 && this.cbRepayToDate.getSelectedIndex() > 0) {
			frSpecifier = this.cbRepayFromDate.getSelectedItem().getAttribute("fromSpecifier").toString();
			toSpecifier = this.cbRepayToDate.getSelectedItem().getAttribute("toSpecifier").toString();
			if((frSpecifier.equals(CalculationConstants.GRACE) || frSpecifier.equals(CalculationConstants.GRACE_END)) && 
					(toSpecifier.equals(CalculationConstants.GRACE) || (toSpecifier.equals(CalculationConstants.GRACE_END)))) {
				this.cbSchdMthd.setDisabled(false);
				fillComboBox(this.cbSchdMthd,"", schMthds, excludeFields);
			}else if((frSpecifier.equals(CalculationConstants.REPAY) ||frSpecifier.equals(CalculationConstants.MATURITY)) && 
					(toSpecifier.equals(CalculationConstants.REPAY) || (toSpecifier.equals(CalculationConstants.MATURITY)))) {
				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(), schMthds, ",GRCNDPAY,");
				this.cbSchdMthd.setDisabled(true);
				if(this.cbSchdMthd.getSelectedItem().getValue().toString().equals(CalculationConstants.PFT)) {
					this.wIAmount.setValue(PennantAppUtil.formateAmount(
							BigDecimal.ZERO,
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					this.wIAmount.setDisabled(true);
				}else {
					this.wIAmount.setValue(BigDecimal.ZERO);
					this.wIAmount.setDisabled(false);
				}
			}else if(!frSpecifier.equals(toSpecifier)) {
				this.cbSchdMthd.setDisabled(true);
				this.cbSchdMthd.setSelectedIndex(0);				
				throw new WrongValueException(
						this.cbRepayToDate,
						Labels.getLabel(
								"DATES_SAME_PERIOD",
								new String[]{
										Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"),
										Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value")}));
			}
		}else {
			this.cbSchdMthd.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return validationOn;
	}
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	
}
