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
 * FileName    		:  WIAddDeffermentDialogCtrl.java                          	            * 	  
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
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class AddRmvDeffermentDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7778031557272602004L;
	private final static Logger logger = Logger.getLogger(AddRmvDeffermentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AddRmvDeffermentDialog; 	// autowired
	protected Combobox cbDate; 							// autowired
	protected Combobox cbTillDateFrom;					// autowired
	protected Combobox cbTillDateTo;					// autowired
	protected Combobox cbReCalType; 					// autowired
	protected Combobox cbAddTermAfter; 					// autowired
	protected Checkbox exDefDate; 						// autowired
	protected Row pftIntactRow;						// autowired
	protected Checkbox pftIntact; 						// autowired
	protected Row recalTypeRow; 						// autowired
	protected Row recallFromDateRow;					// autowired
	protected Row recallToDateRow;						// autowired
	protected Row excDefDateRow;						// autowired
	protected Row addTermRow;							// autowired
	private Button btnAddDefferment; 					// autowired
	
	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient FeeDetailDialogCtrl feeDetailDialogCtrl;
	
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient int oldVar_fromDate;
	private transient int oldVar_tillDateFrom;
	private transient int oldVar_tillDateTo;
	private transient boolean oldVar_exDefDate;	
	private transient boolean oldVar_pftIntact;	
	private transient String oldVar_reCalType;
	private transient int  	oldVar_addTermAfter;
	private transient boolean validationOn;
	private boolean addDefferment;

	static final List<ValueLabel>	      recalTypes	              = PennantStaticListUtil.getSchCalCodes();
	static final List<ValueLabel>	      addTermCodes	              = PennantStaticListUtil.getAddTermCodes();
	/**
	 * default constructor.<br>
	 */
	public AddRmvDeffermentDialogCtrl() {
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
	public void onCreate$window_AddRmvDeffermentDialog(Event event) throws Exception {
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

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (args.containsKey("addDeff")) {
			this.setAddDefferment((Boolean) args.get("addDeff"));
		}

		if (args.containsKey("financeMainDialogCtrl")) {
			setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) args.get("financeMainDialogCtrl"));
		} 
		
		if (args.containsKey("feeDetailDialogCtrl")) {
			setFeeDetailDialogCtrl((FeeDetailDialogCtrl) args.get("feeDetailDialogCtrl"));
		} 

		if(isAddDefferment()) {
			if(getFinScheduleData().getFinanceMain().getDefferments() == 0) {
				this.window_AddRmvDeffermentDialog.onClose();
				PTMessageUtils.showErrorMessage("Deferments not allowed.");
				return;
			}else if(getFinScheduleData().getDefermentHeaders().size() >= getFinScheduleData().getFinanceMain().getDefferments()) {
				this.window_AddRmvDeffermentDialog.onClose();
				PTMessageUtils.showErrorMessage("Max deferments limit reached.");
				return;
			}else { 
				doShowDialog(getFinScheduleData());
				this.window_AddRmvDeffermentDialog.doModal();
			}
		}else {
			if(getFinScheduleData().getDefermentHeaders().size() <= 0) {
				this.window_AddRmvDeffermentDialog.onClose();
				PTMessageUtils.showErrorMessage("No Deferments to delete.");
				return;
			}else { 
				doShowDialog(getFinScheduleData());
				this.window_AddRmvDeffermentDialog.doModal();
			}			
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnAddDefferment(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		if(getFinScheduleData().getFinanceScheduleDetails()!=null){
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when close event is occurred. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * */
	public void onClose(Event event)throws InterruptedException, IllegalAccessException, InvocationTargetException{
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	private void doClose() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean close = true;
		doClearMessage();
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
			.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

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
			this.window_AddRmvDeffermentDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void doSave() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		doWriteComponentsToBean();
		this.window_AddRmvDeffermentDialog.onClose();
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
		if (this.oldVar_fromDate != this.cbDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_tillDateFrom != this.cbTillDateFrom.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_tillDateTo != this.cbTillDateTo.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_reCalType != this.cbReCalType.getValue()) {
			return true;
		}
		if(this.oldVar_exDefDate != this.exDefDate.isChecked()){
			return true;
		}
		if(this.oldVar_pftIntact != this.pftIntact.isChecked()){
			return true;
		}
		if (this.oldVar_addTermAfter != this.cbAddTermAfter.getSelectedIndex()) {
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

		this.cbDate.setVisible(true);
		fillSchDates(this.cbDate,aFinSchData, false,aFinSchData.getFinanceMain().getFinStartDate(), true);
		fillSchDates(this.cbTillDateFrom,aFinSchData, false, aFinSchData.getFinanceMain().getFinStartDate(), false);
		fillSchDates(this.cbTillDateTo,aFinSchData, false, aFinSchData.getFinanceMain().getFinStartDate(), true);
		if(isAddDefferment()){
			recalTypeRow.setVisible(true);
			fillComboBox(this.cbReCalType, "", recalTypes, ",CURPRD,ADDLAST,ADDRECAL,");
			if(getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLDATE) ||
					getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLMDT)) {
				this.recallFromDateRow.setVisible(true);
				this.recallToDateRow.setVisible(true);
				this.excDefDateRow.setVisible(true);
				if(getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLMDT)){
					this.cbTillDateTo.setDisabled(true);
					fillSchDates(this.cbTillDateTo,getFinScheduleData(),true,getFinScheduleData().getFinanceMain().getFinStartDate(), false);
				}else {
					this.cbTillDateTo.setDisabled(false);
				}
			}
			this.btnAddDefferment.setLabel(Labels.getLabel("btnAddDefferment.label"));
			this.btnAddDefferment.setTooltiptext(Labels.getLabel("btnAddDefferment.tooltiptext"));
		}else{
			this.btnAddDefferment.setLabel(Labels.getLabel("btnRmvDefferment.label"));
			this.btnAddDefferment.setTooltiptext(Labels.getLabel("btnRmvDefferment.tooltiptext"));
		}
		fillComboBox(this.cbAddTermAfter, "", addTermCodes, "");
		logger.debug("Entering");
	}

	/** To fill schedule dates */
	public void fillSchDates(Combobox dateCombobox,
			FinScheduleData financeDetail, boolean disbField, Date fillAfter, boolean includeDate) {
		logger.debug("Entering");
		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		DefermentDetail prvDD = new DefermentDetail();
		if(!isAddDefferment()) {
			if (financeDetail.getDefermentDetails() != null) {
				List<DefermentDetail> defDetail =  financeDetail.getDefermentDetails();
				for (int i = 0; i < defDetail.size(); i++) {
					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(defDetail.get(i).getDeferedSchdDate(),PennantConstants.dateFormate));
					comboitem.setValue(defDetail.get(i).getDeferedSchdDate());
					if(i>0){
						prvDD = defDetail.get(i-1);
					}else{
						prvDD = defDetail.get(i);
						dateCombobox.appendChild(comboitem);
					}
					if(defDetail.get(i).getDeferedSchdDate().compareTo(prvDD.getDeferedSchdDate())!=0){
						dateCombobox.appendChild(comboitem);
					}
				}
			}
		}else {
			if (financeDetail.getFinanceScheduleDetails() != null) {
				
				//Disabled Combo Condition
				if(disbField){
					dateCombobox.setDisabled(true);
					return;
				}
				//Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
				
				List<FinanceScheduleDetail> financeScheduleDetails = financeDetail.getFinanceScheduleDetails();
				for (int i = 0; i < financeScheduleDetails.size(); i++) {

					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

					//Not Allowed for Repayment
					if (!(curSchd.isRepayOnSchDate() ||
							(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) ) {
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
					
					/*if(curSchd.getSchDate().before(curBussDate)){
						continue;
					}*/

					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate));
					comboitem.setValue(curSchd.getSchDate());
					if(fillAfter.compareTo(financeDetail.getFinanceMain().getFinStartDate())==0) {
						dateCombobox.appendChild(comboitem);
						if (financeDetail.getFinanceMain().getMaturityDate().compareTo(curSchd.getSchDate())==0) {
							if(disbField){
								dateCombobox.setDisabled(true);
							}
						}
					}else if(includeDate && curSchd.getSchDate().compareTo(fillAfter) >= 0) {
						dateCombobox.appendChild(comboitem);
					}else if(!includeDate && curSchd.getSchDate().compareTo(fillAfter) > 0) {
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
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void doWriteComponentsToBean() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date deffermentDate = null;
		Date fromDate = null;
		Date toDate = null;
		try {
			if (isValidComboValue(this.cbDate, Labels.getLabel("label_AddDeffermentDialog_FromDate.value"))) {
				deffermentDate = (Date)this.cbDate.getSelectedItem().getValue();
				if(isAddDefferment()){
					for(int i=0;i<getFinScheduleData().getDefermentDetails().size();i++){
						if(getFinScheduleData().getDefermentDetails().get(i).getDeferedSchdDate().compareTo(
								deffermentDate	)==0){
							throw new WrongValueException(this.cbDate, 
								Labels.getLabel("label_AddDeffermentDialog_Exists.value",
								new String[]{Labels.getLabel("label_AddDeffermentDialog_FromDate.value"),
								PennantAppUtil.formateDate(deffermentDate,PennantConstants.dateFormate)}));
						}
					}
				}
				getFinScheduleData().getFinanceMain().setEventFromDate(deffermentDate);
				getFinScheduleData().getFinanceMain().setEventToDate(deffermentDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if(this.recalTypeRow.isVisible()){
			try{
				if (isValidComboValue(this.cbReCalType,Labels.getLabel("label_AddDeffermentDialog_RecalType.value"))
						&& this.cbReCalType.getSelectedIndex() != 0) {
					getFinScheduleData().getFinanceMain().setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
				}
			}catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if(this.recallFromDateRow.isVisible()) {
			try {
				if(this.cbTillDateFrom.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDateFrom, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDeffermentDialog_TillDateFrom.value") }));
				}
				fromDate = (Date) this.cbTillDateFrom.getSelectedItem().getValue();
				if((this.cbDate.getSelectedIndex()>0 && fromDate.compareTo(deffermentDate) <= 0)){
					throw new WrongValueException(this.cbTillDateFrom, Labels.getLabel("DATE_ALLOWED_AFTER",
					new String[]{Labels.getLabel("label_AddDeffermentDialog_TillDateFrom.value"),
								PennantAppUtil.formateDate(deffermentDate,PennantConstants.dateFormate)}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if(this.recallToDateRow.isVisible() && !this.cbTillDateTo.isDisabled()) {
			try {
				if(this.cbTillDateTo.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDateTo, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDeffermentDialog_TillDateTo.value") }));
				}
				toDate = (Date) this.cbTillDateTo.getSelectedItem().getValue();
				if((this.cbTillDateFrom.getSelectedIndex()>0 && toDate.compareTo(fromDate) < 0) ){
					throw new WrongValueException(this.cbTillDateTo,Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[]{Labels.getLabel("label_AddDeffermentDialog_TillDateTo.value"),
									PennantAppUtil.formateDate(fromDate,PennantConstants.dateFormate)}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if(this.excDefDateRow.isVisible()) {
			try {
				getFinScheduleData().getFinanceMain().setExcludeDeferedDates(this.exDefDate.isChecked());
			}catch(WrongValueException we) {
				wve.add(we);
			}
		}
		if(this.pftIntactRow.isVisible()) {
			try {
				getFinScheduleData().getFinanceMain().setPftIntact(this.pftIntact.isChecked());
			}catch(WrongValueException we) {
				wve.add(we);
			}
		}
		try{
			if(this.addTermRow.isVisible()) {
				if (isValidComboValue(
						this.cbAddTermAfter,
						Labels.getLabel("label_AddDeffermentDialog_AddTermAfter.value"))
						&& this.cbAddTermAfter.getSelectedIndex() != 0) {
					this.cbAddTermAfter.getSelectedItem().getValue().toString();
				}
				getFinScheduleData().getFinanceMain().setAddTermAfter(
						this.cbAddTermAfter.getSelectedItem().getValue().toString());
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		
		
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		if(getFeeDetailDialogCtrl() != null){
			try {
				setFinScheduleData(getFeeDetailDialogCtrl().doExecuteFeeCharges(true, false, getFinScheduleData(),true,deffermentDate));
			} catch (AccountNotFoundException e) {
				logger.error(e.getMessage());
			}
		}
		
		if(isAddDefferment()){
			if(this.recallFromDateRow.isVisible() && this.cbTillDateFrom.getSelectedIndex() > 0){
				getFinScheduleData().getFinanceMain().setRecalFromDate(fromDate);
				if(this.cbTillDateTo.getSelectedIndex() > 0){
					getFinScheduleData().getFinanceMain().setRecalToDate(toDate);
				}
				setFinScheduleData(ScheduleCalculator.addDeferment(getFinScheduleData()));
			}else {
				setFinScheduleData(ScheduleCalculator.addDeferment(getFinScheduleData()));
			}
		}else{
			setFinScheduleData(ScheduleCalculator.rmvDeferment(getFinScheduleData()));
		}
		
		//Show Error Details in Schedule Maintainance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			PTMessageUtils.showErrorMessage(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{

			getFinScheduleData().setSchduleGenerated(true);
			int availedDefRpyChange = getFinScheduleData().getFinanceMain().getAvailedDefRpyChange();
			if(isAddDefferment()){
				getFinScheduleData().getFinanceMain().setAvailedDefRpyChange(availedDefRpyChange + 1);
			}else{
				getFinScheduleData().getFinanceMain().setAvailedDefRpyChange(availedDefRpyChange - 1);
			}
				
			if(getScheduleDetailDialogCtrl()!=null){
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cbDate.setReadonly(true);
		this.cbTillDateFrom.setReadonly(true);
		this.cbTillDateTo.setReadonly(true);
		this.cbReCalType.setReadonly(true);
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
		this.oldVar_fromDate = this.cbDate.getSelectedIndex();
		this.oldVar_tillDateFrom = this.cbTillDateFrom.getSelectedIndex();
		this.oldVar_tillDateTo = this.cbTillDateTo.getSelectedIndex();
		this.oldVar_reCalType = this.cbReCalType.getValue();
		this.oldVar_addTermAfter = this.cbAddTermAfter.getSelectedIndex();
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
			setDialog(this.window_AddRmvDeffermentDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages.
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cbDate.clearErrorMessage();
		this.cbTillDateFrom.clearErrorMessage();
		this.cbTillDateTo.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		logger.debug("Leaving");
	}
	
	/**
	 * when user changes recalculation type 
	 * @param event
	 * 
	 */
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		String recalType = this.cbReCalType.getSelectedItem().getValue().toString();
		if(recalType.equals(CalculationConstants.RPYCHG_TILLDATE) || recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.recallFromDateRow.setVisible(true);
			this.recallToDateRow.setVisible(true);
			this.excDefDateRow.setVisible(true);
			this.addTermRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
			if(recalType.equals(CalculationConstants.RPYCHG_TILLMDT)){
				this.recallToDateRow.setVisible(false);
				if(SystemParameterDetails.getSystemParameterValue("DEF_METHOD").equals(PennantConstants.DEF_METHOD_RECALRATE)){
					this.excDefDateRow.setVisible(false);
				} else {
					this.excDefDateRow.setVisible(true);
				}
				this.cbTillDateTo.setDisabled(true);
				if(this.cbDate.getSelectedItem() == null || this.cbDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
					fillSchDates(this.cbTillDateFrom,getFinScheduleData(),false,getFinScheduleData().getFinanceMain().getFinStartDate(), true);
				}else{
					fillSchDates(this.cbTillDateFrom,getFinScheduleData(),false,(Date)this.cbDate.getSelectedItem().getValue(), false);
				}
			}else {
				this.cbTillDateTo.setDisabled(false);
				if(this.cbTillDateFrom.getSelectedItem() == null || this.cbTillDateFrom.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
					if(this.cbDate.getSelectedItem() == null || this.cbDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
						fillSchDates(this.cbTillDateTo,getFinScheduleData(),false,getFinScheduleData().getFinanceMain().getFinStartDate(), true);
					}else{
						fillSchDates(this.cbTillDateTo,getFinScheduleData(),false,(Date)this.cbDate.getSelectedItem().getValue(), true);
					}
				}else{
					fillSchDates(this.cbTillDateTo,getFinScheduleData(),false,(Date)this.cbTillDateFrom.getSelectedItem().getValue(), true);
				}
			}
		}else if(recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
			this.addTermRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(1);
			this.recallFromDateRow.setVisible(false);
			this.cbTillDateFrom.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.cbTillDateTo.setSelectedIndex(0);
			this.excDefDateRow.setVisible(false);
			this.exDefDate.setChecked(false);
		}else {
			this.recallFromDateRow.setVisible(false);
			this.cbTillDateFrom.setSelectedIndex(0);
			this.cbTillDateTo.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.excDefDateRow.setVisible(false);
			this.exDefDate.setChecked(false);
			this.addTermRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when user changes from date
	 * @param event
	 * 
	 */
	public void onChange$cbTillDateFrom(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.recallToDateRow.isVisible() && !this.cbTillDateTo.isDisabled()) {
			this.cbTillDateTo.getItems().clear();		
			if (isValidComboValue(this.cbTillDateFrom,Labels.getLabel("label_AddDeffermentDialog_TillDateFrom.value"))) {
				fillSchDates(this.cbTillDateTo, getFinScheduleData(),false,
						(Date) this.cbTillDateFrom.getSelectedItem().getValue(), true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when user changes date
	 * @param event
	 * 
	 */
	public void onChange$cbDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbDate.getSelectedIndex() > 0) {
			this.cbReCalType.setDisabled(false);
			this.cbTillDateFrom.getItems().clear();		
			if (isValidComboValue(this.cbDate,Labels.getLabel("label_AddDeffermentDialog_FromDate.value"))) {
				fillSchDates(this.cbTillDateFrom, getFinScheduleData(),false,
						(Date) this.cbDate.getSelectedItem().getValue(), false);
				fillSchDates(this.cbTillDateTo, getFinScheduleData(),false,
						(Date) this.cbDate.getSelectedItem().getValue(), true);
			}
		}else {
			this.cbReCalType.setSelectedIndex(0);
			this.cbReCalType.setDisabled(true);
			this.recallFromDateRow.setVisible(false);
			this.cbTillDateFrom.setSelectedIndex(0);
			this.cbTillDateTo.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.excDefDateRow.setVisible(false);
			this.exDefDate.setChecked(false);
			this.addTermRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
		}
		logger.debug("Leaving" + event.toString());
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

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isAddDefferment() {
		return addDefferment;
	}
	public void setAddDefferment(boolean addDefferment) {
		this.addDefferment = addDefferment;
	}
	
	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}
	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}	
	
	public void setFeeDetailDialogCtrl(FeeDetailDialogCtrl feeDetailDialogCtrl) {
		this.feeDetailDialogCtrl = feeDetailDialogCtrl;
	}
	public FeeDetailDialogCtrl getFeeDetailDialogCtrl() {
		return feeDetailDialogCtrl;
	}

}

