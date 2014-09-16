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
 * FileName    		:  ChangeFrequencyDialogCtrl.java                          	            * 	  
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/ChangeFrequencyDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ChangeFrequencyDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 454600127282110738L;
	private final static Logger logger = Logger.getLogger(ChangeFrequencyDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ChangeFrequencyDialog; 		// autowired
	protected Textbox repayFrq;
	protected Combobox cbRepayFrqCode; 					// autowired
	protected Combobox cbRepayFrqMth; 					// autowired
	protected Combobox cbRepayFrqDay; 					// autowired
	protected Combobox cbFrqFromDate; 				// autowired
	protected Datebox grcPeriodEndDate; 				// autowired
	protected Datebox nextGrcRepayDate; 				// autowired
	protected Datebox nextRepayDate; 					// autowired
	protected Checkbox pftIntact; 						// autowired
	
	protected Row row_GrcPeriodEndDate;
	protected Row row_grcNextRepayDate;

	// not auto wired vars
	private FinScheduleData finScheduleData; 				// overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_repayFrq;
	private transient int oldVar_cbFrqFromDate;
	private transient Date oldVar_grcPeriodEndDate;
	private transient Date oldVar_nextGrcRepayDate;
	private transient Date oldVar_nextRepayDate;
	private transient boolean oldVar_pftIntact;
	
	private Date tempStartDate = null;
	private int rpyTermsCompleted = 0;

	/**
	 * default constructor.<br>
	 */
	public ChangeFrequencyDialogCtrl() {
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
	public void onCreate$window_ChangeFrequencyDialog(Event event) throws Exception {
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

		// we get the FinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIF/FinanceMain here.
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) args.get("financeMainDialogCtrl"));
		} else {
			setFinanceMainDialogCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_ChangeFrequencyDialog.doModal();
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
			setDialog(this.window_ChangeFrequencyDialog);
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
		this.oldVar_repayFrq = this.repayFrq.getValue();
		this.oldVar_cbFrqFromDate = this.cbFrqFromDate.getSelectedIndex();
		this.oldVar_grcPeriodEndDate = this.grcPeriodEndDate.getValue();
		this.oldVar_nextGrcRepayDate = this.nextGrcRepayDate.getValue();
		this.oldVar_nextRepayDate = this.nextRepayDate.getValue();
		this.oldVar_pftIntact = this.pftIntact.isChecked();
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.repayFrq.setMaxlength(5);
		this.grcPeriodEndDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcRepayDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayDate.setFormat(PennantConstants.dateFormat);
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
	public void onClick$btnChangeFrq(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
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
			this.window_ChangeFrequencyDialog.onClose();
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
		doWriteComponentsToBean();
		this.window_ChangeFrequencyDialog.onClose();
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
		if(this.oldVar_repayFrq != this.repayFrq.getValue()) {
			return true;
		}
		if (this.oldVar_cbFrqFromDate != this.cbFrqFromDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_grcPeriodEndDate != this.grcPeriodEndDate.getValue()) {
			return true;
		}
		if (this.oldVar_nextGrcRepayDate != this.nextGrcRepayDate.getValue()) {
			return true;
		}
		if (this.oldVar_nextRepayDate != this.nextRepayDate.getValue()) {
			return true;
		}
		if (this.oldVar_pftIntact != this.pftIntact.isChecked()) {
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
		
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		
		clearField(this.cbRepayFrqCode);
		fillFrqCode(this.cbRepayFrqCode, aFinanceMain.getRepayFrq(), true);
		clearField(this.cbRepayFrqMth);
		fillFrqMth(this.cbRepayFrqMth, aFinanceMain.getRepayFrq(), true);
		clearField(this.cbRepayFrqDay);
		fillFrqDay(cbRepayFrqDay, aFinanceMain.getRepayFrq(), false);
		this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		
		fillSchFromDates(aFinSchData.getFinanceScheduleDetails());
		
		//this.grcPeriodEndDate.setValue(aFinanceMain.getGrcPeriodEndDate());

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
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Not Review Date
				if (!(curSchd.isRepayOnSchDate() ||
						(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) 
						&& !curSchd.isDeferedPay()) {
					continue;
				}

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}
				
				//Deferred Profit Paid (Partial/Full)
				if (curSchd.getDefSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}
				
				//Deferred Principal Paid (Partial/Full)
				if (curSchd.getDefSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}
				
				if (curSchd.getDefRepaySchd().compareTo(BigDecimal.ZERO) > 0 ) {
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);
					this.cbFrqFromDate.setSelectedItem(comboitem);
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());
				this.cbFrqFromDate.appendChild(comboitem);
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
		
		try {
			if(isValidComboValue(this.cbRepayFrqDay, Labels.getLabel("label_FrqDay.value"))){
				frq = this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbFrqFromDate, Labels.getLabel("label_ChangeFrequencyDialog_FromDate.value"))) {
				fromDate = (Date)this.cbFrqFromDate.getSelectedItem().getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(this.row_GrcPeriodEndDate.isVisible()){
			try {
				if(fromDate != null && fromDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0){
					if(this.grcPeriodEndDate.getValue() == null){
						throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("FIELD_IS_MAND", new String[]{
								Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value")}));
					}else {
						if(this.grcPeriodEndDate.getValue().compareTo(financeMain.getFinStartDate()) < 0){
							throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[]{
									Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"), Labels.getLabel("label_ChangeFrequencyDialog_FinStartDate.value")}));
						}else if(!financeMain.isNewRecord() && 
								!StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
							
							if(StringUtils.trimToEmpty(getFinScheduleData().getFinanceType().getFinCategory()).equals(PennantConstants.FINANCE_PRODUCT_IJARAH)){
								Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
								if(this.grcPeriodEndDate.getValue().compareTo(curBussDate) <= 0){
									throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[]{
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"), Labels.getLabel("label_ChangeFrequencyDialog_CurBussDate.value")}));
								}
							}
						}
					}
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
		}
		try {
			this.nextGrcRepayDate.getValue();
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if(this.nextGrcRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null){
				if(this.nextGrcRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) > 0){
					throw new WrongValueException(this.nextGrcRepayDate, Labels.getLabel("DATE_ALLOWED_MAXDATE", new String[]{
							Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value"), Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value")}));
				}else if(this.nextGrcRepayDate.getValue().compareTo(financeMain.getFinStartDate()) <= 0){
						throw new WrongValueException(this.nextGrcRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
								Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value"), Labels.getLabel("label_ChangeFrequencyDialog_FinStartDate.value")}));
					}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.nextRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null){
				if(this.nextRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) <= 0){
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
							Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"), Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value")}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.nextRepayDate.getValue() != null){
				if(this.nextRepayDate.getValue().compareTo(tempStartDate) <= 0){
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
							Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),DateUtility.formatUtilDate(tempStartDate, PennantConstants.dateFormat)}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.nextRepayDate.getValue() != null && this.nextGrcRepayDate.getValue() != null){
				if(this.nextRepayDate.getValue().compareTo(this.nextGrcRepayDate.getValue()) <= 0){
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE", new String[]{
							Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"), Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value")}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			this.nextRepayDate.getValue();
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		//Check Date Status Specifier
		boolean calFromGrcPeriod = false;
		List<FinanceScheduleDetail> scheduleList = getFinScheduleData().getFinanceScheduleDetails();
		if(fromDate != null && fromDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0){
			calFromGrcPeriod = true;
		}
		
		//Repayment Calculated Rate storing
		Date firstRepayDate = null;
		boolean chkFirstRpyDate = false;
		BigDecimal repayCalRate = financeMain.getRepayProfitRate();
		for (int i = 0; i < scheduleList.size(); i++) {
			if(scheduleList.get(i).getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) >= 0){
				if(chkFirstRpyDate){
					firstRepayDate = scheduleList.get(i).getSchDate();
					break;
				}
				repayCalRate =  scheduleList.get(i).getCalculatedRate();
				chkFirstRpyDate = true;
			}
		}
		
		//Removing Schedule Details from Selected Recalculation From Date
		HashMap<Date, FinanceScheduleDetail> mapList = new HashMap<Date, FinanceScheduleDetail>();
		BigDecimal unModifiedPft = BigDecimal.ZERO;
		if (scheduleList != null) {
			for (int i = 0; i < scheduleList.size(); i++) {
				if(scheduleList.get(i).getSchDate().compareTo(fromDate) >= 0){
					break;
				}
				mapList.put(scheduleList.get(i).getSchDate(), scheduleList.get(i));
				unModifiedPft = unModifiedPft.add(scheduleList.get(i).getProfitSchd()).add(scheduleList.get(i).getDefProfitSchd());
			}
			getFinScheduleData().setScheduleMap(mapList);
		}
		mapList = null;
		
		Date startRepayCalDate = null;
		Date recalToDate = null;
		
		//Setting Event From Date Value
		if(calFromGrcPeriod){
			getFinScheduleData().getFinanceMain().setGrcPeriodEndDate(this.grcPeriodEndDate.getValue());
			if(this.nextGrcRepayDate.getValue() != null){
				getFinScheduleData().getFinanceMain().setEventFromDate(this.nextGrcRepayDate.getValue());
			}else{
				getFinScheduleData().getFinanceMain().setEventFromDate(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
						tempStartDate, "A", false).getNextFrequencyDate());
			}
			
			if(this.nextRepayDate.getValue() != null){
				startRepayCalDate = this.nextRepayDate.getValue();
				recalToDate = this.nextRepayDate.getValue();
			}else{
				startRepayCalDate = FrequencyUtil.getNextDate(frq, 1, this.grcPeriodEndDate.getValue(), "A", false).getNextFrequencyDate();
			}
			
			if (getFinScheduleData().getFinanceMain().getNumberOfTerms() != 0) {
				int noOfRemTerms = getFinScheduleData().getFinanceMain().getNumberOfTerms() - rpyTermsCompleted;

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(frq, noOfRemTerms, startRepayCalDate, "A", true).getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					recalToDate = calendar.getTime();
				}			
				scheduleDateList = null;
			}
			
			getFinScheduleData().getFinanceMain().setMaturityDate(recalToDate);
			
			//Schedule Dates Generation Process calculation		
			FinScheduleData finScheduleData = ScheduleGenerator.getScheduleDateList(getFinScheduleData() ,frq, 
					getFinScheduleData().getFinanceMain().getEventFromDate(), startRepayCalDate, recalToDate, true);
			setFinScheduleData(finScheduleData);
			
		}else{
			
			if(this.nextRepayDate.getValue() != null){
				startRepayCalDate = this.nextRepayDate.getValue();
			}else{
				startRepayCalDate = FrequencyUtil.getNextDate(frq, 1, tempStartDate, "A", false).getNextFrequencyDate();
			}
			
			if (getFinScheduleData().getFinanceMain().getNumberOfTerms() != 0) {
				int noOfRemTerms = getFinScheduleData().getFinanceMain().getNumberOfTerms() - rpyTermsCompleted;

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(frq, noOfRemTerms, startRepayCalDate, "A", true).getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					recalToDate = calendar.getTime();
				}			
				scheduleDateList = null;
			}
			
			getFinScheduleData().getFinanceMain().setEventFromDate(startRepayCalDate);
			getFinScheduleData().getFinanceMain().setMaturityDate(recalToDate);
			
			//Schedule Dates Generation Process calculation		
			FinScheduleData finScheduleData = ScheduleGenerator.getScheduleDateList(getFinScheduleData() ,frq, null,  
					getFinScheduleData().getFinanceMain().getEventFromDate(), recalToDate, true);
			setFinScheduleData(finScheduleData);
			
		}
		
		// Set Deferred scheduled date and schedule method first time
		chkFirstRpyDate = false;
		Date newFirstRpyDate = null;
		for (int i = 0; i < getFinScheduleData().getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				if (!getFinScheduleData().getFinanceMain().isAllowGrcRepay()) {
					curSchd.setSchdMethod(CalculationConstants.NOPAY);
				} else {
					curSchd.setSchdMethod(financeMain.getGrcSchdMthd());
				}
			} else {
				curSchd.setSchdMethod(financeMain.getScheduleMethod());
			}
			
			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) >= 0) {
				if(chkFirstRpyDate && newFirstRpyDate == null){
					newFirstRpyDate = curSchd.getSchDate();
				}
				chkFirstRpyDate = true;
			}

			if(tempStartDate.compareTo(financeMain.getGrcPeriodEndDate()) > 0){
				if(i != 0 && curSchd.getSchDate().compareTo(tempStartDate) > 0){
					curSchd.setCalculatedRate(getFinScheduleData().getFinanceScheduleDetails().get(i-1).getCalculatedRate());
				}
			}else{
				if(curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0){
					if(i != 0 && curSchd.getSchDate().compareTo(tempStartDate) > 0){
						curSchd.setCalculatedRate(getFinScheduleData().getFinanceScheduleDetails().get(i-1).getCalculatedRate());
					}
				}else{
					curSchd.setCalculatedRate(repayCalRate);
				}
			}
		}
		
		//For Grace Period Date Selection check Repay Instruction Details
		List<RepayInstruction> instructionList = getFinScheduleData().getRepayInstructions();
		for (int i = 0; i < instructionList.size(); i++) {
			if(firstRepayDate != null && firstRepayDate.compareTo(instructionList.get(i).getRepayDate()) == 0){
				instructionList.get(i).setRepayDate(newFirstRpyDate);
			}
		}
		
		//Setting Recalculation Type Method
		getFinScheduleData().getFinanceMain().setEventToDate(recalToDate);
		getFinScheduleData().getFinanceMain().setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		
		//Calculate Remaining Total Profit From Recalculation Event Date
		BigDecimal remProfitForRecal = financeMain.getTotalProfit().subtract(unModifiedPft);
		
		//Schedule Recalculation Depends on Frequency Change
		setFinScheduleData(ScheduleCalculator.reCalSchd(getFinScheduleData()));
		
		//By Using Change Profit Method Reverse Calculate the total Profit Amount on Changing Rate Value
		if(this.pftIntact.isChecked()){
			getFinScheduleData().getFinanceMain().setEventFromDate(tempStartDate);
			setFinScheduleData(ScheduleCalculator.changeProfit(getFinScheduleData(), remProfitForRecal));
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
	
	public void onChange$cbFrqFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		tempStartDate = getFinScheduleData().getFinanceMain().getFinStartDate();
		rpyTermsCompleted = 0;
		
		this.row_GrcPeriodEndDate.setVisible(false);
		this.row_grcNextRepayDate.setVisible(false);
		
		if(this.cbFrqFromDate.getSelectedIndex() != 0){
			Date fromDate = (Date)this.cbFrqFromDate.getSelectedItem().getValue();
			
			List<FinanceScheduleDetail> financeScheduleDetails = getFinScheduleData().getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {
					
					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
					
					if(curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() ||
							(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)){
						if(fromDate.compareTo(curSchd.getSchDate()) == 0){
							if(fromDate.compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0){
								this.row_GrcPeriodEndDate.setVisible(true);
								this.row_grcNextRepayDate.setVisible(true);
							}
							break;
						}
						tempStartDate = curSchd.getSchDate();

						if(curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) > 0){
							rpyTermsCompleted = rpyTermsCompleted+1;
						}
					}
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onSelect$cbRepayFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbRepayFrqCode, cbRepayFrqMth, cbRepayFrqDay, this.repayFrq);
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		this.grcPeriodEndDate.setText("");
		logger.debug("Leaving" + event.toString());
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

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

}
