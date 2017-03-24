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
 * FileName    		:  PostponementDialogCtrl.java                          	            * 	  
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

import java.lang.reflect.InvocationTargetException;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

public class PostponementDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -7778031557272602004L;
	private final static Logger logger = Logger.getLogger(PostponementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PostponementDialog; 	
	
	protected Combobox cbFromDate; 							
	protected Combobox cbToDate; 	
	protected Combobox cbReCalType; 					
	protected Combobox cbRecalFromDate;					
	protected Combobox cbRecalToDate;					
	protected Intbox adjTerms; 						
	protected Checkbox pftIntact; 	
	protected Uppercasebox	serviceReqNo;
	protected Textbox		remarks;
	protected Button btnPostponement;
	
	protected Row recalTypeRow; 						
	protected Row recallFromDateRow;					
	protected Row recallToDateRow;						
	protected Row numOfTermsRow;	
	protected Row pftIntactRow;							
	
	private String moduleDefiner; 					
	
	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private FeeDetailDialogCtrl feeDetailDialogCtrl;
	private PostponementService postponementService;
	
	/**
	 * default constructor.<br>
	 */
	public PostponementDialogCtrl() {
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
	public void onCreate$window_PostponementDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PostponementDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments
						.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("feeDetailDialogCtrl")) {
				setFeeDetailDialogCtrl((FeeDetailDialogCtrl) arguments
						.get("feeDetailDialogCtrl"));
			}

			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_PostponementDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.adjTerms.setMaxlength(2);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		
		// Postponement Details
		if(StringUtils.equals(FinanceConstants.FINSER_EVENT_POSTPONEMENT, moduleDefiner)){
			this.recalTypeRow.setVisible(false);
			this.recallFromDateRow.setVisible(false);
			this.recallToDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		}else if(StringUtils.equals(FinanceConstants.FINSER_EVENT_UNPLANEMIH, moduleDefiner)){
			this.window_PostponementDialog.setTitle(Labels.getLabel("window_UnPlannedEMiHDialog.title"));
			this.btnPostponement.setLabel(Labels.getLabel("btnUnPlannedEMiH.label"));
			this.btnPostponement.setTooltiptext(Labels.getLabel("btnUnPlannedEMiH.tooltiptext"));
			this.recalTypeRow.setVisible(true);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnPostponement(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doSave() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		doWriteComponentsToBean();
		this.window_PostponementDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		fillSchDates(this.cbFromDate,aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
		fillSchDates(this.cbToDate,aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
		fillSchDates(this.cbRecalFromDate,aFinSchData,  aFinSchData.getFinanceMain().getFinStartDate(), false);
		fillSchDates(this.cbRecalToDate,aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), true);

		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDLAST,ADJTERMS,");
		if(!StringUtils.equals(FinanceConstants.FINSER_EVENT_POSTPONEMENT, moduleDefiner)){
			if(StringUtils.equalsIgnoreCase(getFinScheduleData().getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_TILLDATE) ||
					StringUtils.equalsIgnoreCase(getFinScheduleData().getFinanceMain().getRecalType() , CalculationConstants.RPYCHG_TILLMDT)) {
				this.recallFromDateRow.setVisible(true);
				if(getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLMDT)){
					this.recallToDateRow.setVisible(false);
					fillSchDates(this.cbRecalFromDate,getFinScheduleData(),getFinScheduleData().getFinanceMain().getFinStartDate(), false);
				}else {
					this.recallToDateRow.setVisible(true);
				}
			}
		}

		if(aFinSchData.getFinanceType().isFinPftUnChanged()){
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		}else{
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}
		
		logger.debug("Entering");
	}

	/** To fill schedule dates */
	private void fillSchDates(Combobox dateCombobox,
			FinScheduleData financeDetail, Date fillAfter, boolean includeDate) {
		logger.debug("Entering");
		
		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		Date graceEndDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
		if (financeDetail.getFinanceScheduleDetails() != null) {

			List<FinanceScheduleDetail> financeScheduleDetails = financeDetail.getFinanceScheduleDetails();
			Date curBussDate = DateUtility.getAppDate();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				if(curSchd.getSchDate().before(curBussDate)){
					continue;
				}
				
				if(curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
					continue;
				}
				
				if(DateUtility.compare(curSchd.getSchDate(),graceEndDate)<=0){
					continue;
				}
				
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()));
				comboitem.setValue(curSchd.getSchDate());
				if(fillAfter.compareTo(financeDetail.getFinanceMain().getFinStartDate())==0) {
					dateCombobox.appendChild(comboitem);
				}else if(includeDate && curSchd.getSchDate().compareTo(fillAfter) >= 0) {
					dateCombobox.appendChild(comboitem);
				}else if(!includeDate && curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
				}
				/*
				 * In Recalculation type if Till Date is selected and for the Same date if the profit Balance in schedule is greater
				 * than zero then will set the pftbal attribute
				 */
				if(this.cbReCalType.getSelectedIndex()>=0 &&
						StringUtils.equals(this.cbReCalType.getSelectedItem().getValue().toString(), CalculationConstants.RPYCHG_TILLDATE)
						&& curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) > 0 && fillAfter!=null){
					comboitem.setStyle("color:Red;");
					comboitem.setAttribute("pftBal", curSchd.getProfitBalance());
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
	private void doWriteComponentsToBean() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		
		Date fromDate = null;
		Date toDate = null;
		Date recalFromDate = null;
		Date recalToDate = null;
		try {
			if (isValidComboValue(this.cbFromDate, Labels.getLabel("label_PostponementDialog_FromDate.value"))) {
				fromDate = (Date)this.cbFromDate.getSelectedItem().getValue();
				getFinScheduleData().getFinanceMain().setEventFromDate(fromDate);
				finServiceInstruction.setFromDate(fromDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbToDate, Labels.getLabel("label_PostponementDialog_ToDate.value"))) {
				toDate = (Date)this.cbToDate.getSelectedItem().getValue();
				getFinScheduleData().getFinanceMain().setEventToDate(toDate);
				finServiceInstruction.setToDate(toDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if(this.recalTypeRow.isVisible()){
			try{
				if (isValidComboValue(this.cbReCalType,Labels.getLabel("label_PostponementDialog_RecalType.value"))
						&& this.cbReCalType.getSelectedIndex() != 0) {
					getFinScheduleData().getFinanceMain().setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
					finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
				}
			}catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if(this.recallFromDateRow.isVisible()) {
			try {
				if(this.cbRecalFromDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbRecalFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_PostponementDialog_RecalFromDate.value") }));
				}
				recalFromDate = (Date) this.cbRecalFromDate.getSelectedItem().getValue();
				if(this.cbFromDate.getSelectedIndex()>0 && recalFromDate.compareTo(fromDate) <= 0){
					throw new WrongValueException(this.cbRecalFromDate, Labels.getLabel("DATE_ALLOWED_AFTER",
					new String[]{Labels.getLabel("label_PostponementDialog_RecalFromDate.value"),
							DateUtility.formatToLongDate(fromDate)}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if(this.recallToDateRow.isVisible() && !this.cbRecalToDate.isDisabled()) {
			try {
				if(this.cbRecalToDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_PostponementDialog_RecalToDate.value") }));
				}
				//if schdpftBal greater than zero throw validation
				if(this.cbRecalToDate.getSelectedItem().getAttribute("pftBal") != null){
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("Label_finSchdTillDate"));
				}
				
				recalToDate = (Date) this.cbRecalToDate.getSelectedItem().getValue();
				
				if(this.cbRecalFromDate.getSelectedIndex()>0 && recalToDate.compareTo(recalFromDate) < 0) {
					throw new WrongValueException(this.cbRecalToDate,Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[]{Labels.getLabel("label_PostponementDialog_RecalToDate.value"),
							DateUtility.formatToLongDate(recalFromDate)}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		if (this.numOfTermsRow.isVisible()) {
			try {
				if (this.adjTerms.intValue() <= 0) {
					throw new WrongValueException(this.adjTerms, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_PostponementDialog_Terms.value") }));
				}
				finServiceInstruction.setTerms(this.adjTerms.intValue());

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if(this.pftIntactRow.isVisible()) {
			try {
				finServiceInstruction.setPftIntact(this.pftIntact.isChecked());
			}catch(WrongValueException we) {
				wve.add(we);
			}
		}
		
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		// Adjust Terms Calculation
		int adjTerms = 0;
		int sdSize = getFinScheduleData().getFinanceScheduleDetails().size();
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			if(curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0){
				if(DateUtility.compare(curSchd.getSchDate(), fromDate) >= 0 && 
						DateUtility.compare(curSchd.getSchDate(), toDate) <= 0){
					adjTerms = adjTerms + 1;
				}
			}
		}
		
		if(StringUtils.equals(FinanceConstants.FINSER_EVENT_POSTPONEMENT, moduleDefiner)){
			sdSize = getFinScheduleData().getFinanceScheduleDetails().size();
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
				if(DateUtility.compare(curSchd.getSchDate(), fromDate) >= 0 && 
						DateUtility.compare(curSchd.getSchDate(), toDate) <= 0){
					if (!checkPlannedDeferment(fromDate)) {
						return;
					}
				}
			}
			
			//Check max limit
			if (getFinScheduleData().getFinanceMain().getAvailedDefRpyChange()+adjTerms > getFinScheduleData().getFinanceMain().getDefferments()) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_PostponementDialog_MaxPostponement.value", 
						new String[]{String.valueOf(getFinScheduleData().getFinanceMain().getDefferments())}));
				return;
			}
		}else if(StringUtils.equals(FinanceConstants.FINSER_EVENT_UNPLANEMIH, moduleDefiner)){
			//Check max limit
			if (getFinScheduleData().getFinanceMain().getAvailedUnPlanEmi()+adjTerms > getFinScheduleData().getFinanceMain().getMaxUnplannedEmi()) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_PostponementDialog_MaxUnPlanEMIH.value", 
						new String[]{String.valueOf(getFinScheduleData().getFinanceMain().getMaxUnplannedEmi())}));
				return;
			}
		}
		
		if(getFeeDetailDialogCtrl() != null){
			try {
				setFinScheduleData(getFeeDetailDialogCtrl().doExecuteFeeCharges(true, false, getFinScheduleData(),true,fromDate));
			} catch (PFFInterfaceException e) {
				logger.error("Exception: ", e);
			}
		}
		
		finServiceInstruction.setRecalFromDate(recalFromDate);
		finServiceInstruction.setRecalToDate(recalToDate);
		if(StringUtils.equals(FinanceConstants.FINSER_EVENT_POSTPONEMENT, moduleDefiner)){
			finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_ADDTERM);
			finServiceInstruction.setTerms(adjTerms);
		}
		
		finServiceInstruction.setFinReference(getFinScheduleData().getFinanceMain().getFinReference());
		finServiceInstruction.setFinEvent(getScheduleDetailDialogCtrl().getFinanceDetail().getModuleDefiner());
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);
		getFinScheduleData().setFeeEvent(moduleDefiner);
		
		// Service details calling for Schedule calculation
		setFinScheduleData(this.postponementService.doPostponement(getFinScheduleData(), finServiceInstruction,
				getFinScheduleData().getFinanceMain().getScheduleMethod()));
		
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		//Show Error Details in Schedule Maintenance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			MessageUtil.showErrorMessage(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
			
		}else{

			getFinScheduleData().setSchduleGenerated(true);
			if(StringUtils.isNotBlank(moduleDefiner)){
				int availedDefRpyChange = getFinScheduleData().getFinanceMain().getAvailedDefRpyChange();
				getFinScheduleData().getFinanceMain().setAvailedDefRpyChange(availedDefRpyChange + 1);
			}
				
			if(getScheduleDetailDialogCtrl() != null){
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * TO Check allowed deferment per year
	 * @param defDate
	 * @return
	 * @throws InterruptedException
	 */
	public boolean checkPlannedDeferment(Date defDate) throws InterruptedException {
		logger.debug(" Entering ");

		List<FinanceScheduleDetail> list = getFinScheduleData().getFinanceScheduleDetails();
		FinanceMain financeMain=getFinScheduleData().getFinanceMain();

		int perYear=financeMain.getPlanDeferCount();
		
		// No Planned Deferments Exists. No need of external Validation
		if(perYear == 0){
			return true;
		}
		Date defermentStart = null;
		Date defermentEnd = null;

		Date yearStart = financeMain.getFinStartDate();
		// Check deferment fall on which year.
		while (true) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(yearStart);
			int year = calendar.get(Calendar.YEAR);
			calendar.set(Calendar.YEAR, year + 1);

			Date yearEnd = calendar.getTime();

			if (defDate.compareTo(yearStart) >= 0 && defDate.compareTo(yearEnd) <= 0) {
				defermentStart = yearStart;
				defermentEnd = yearEnd;
				break;
			} else {
				yearStart = yearEnd;
			}
			
			if (yearEnd.compareTo(financeMain.getMaturityDate())>=0) {
				break;
			}
		}

		//Check total deferments made in the specified year.
		if (defermentStart != null && defermentEnd != null) {

			int curretnDefCount = 0;
			for (FinanceScheduleDetail financeScheduleDetail : list) {
				Date schdate = financeScheduleDetail.getSchDate();

				if (schdate.compareTo(defermentStart) >= 0 && schdate.compareTo(defermentEnd) <= 0) {

					if (financeScheduleDetail.isRepayOnSchDate() && financeScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						curretnDefCount++;
					}
				}
			}

			if (curretnDefCount == perYear) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_PostponementDialog_AllowedPerYear.value",new String[]{String.valueOf(perYear)}));
				return false;
			}
		}
		
		logger.debug(" Leaving ");
		return true;
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
	private void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PostponementDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.cbFromDate.clearErrorMessage();
		this.cbRecalFromDate.clearErrorMessage();
		this.cbRecalToDate.clearErrorMessage();
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
			this.numOfTermsRow.setVisible(false);
			if(recalType.equals(CalculationConstants.RPYCHG_TILLMDT)){
				this.recallToDateRow.setVisible(false);
				if(this.cbToDate.getSelectedItem() == null || this.cbToDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
					fillSchDates(this.cbRecalFromDate,getFinScheduleData(),getFinScheduleData().getFinanceMain().getFinStartDate(), true);
				}else{
					fillSchDates(this.cbRecalFromDate,getFinScheduleData(),(Date)this.cbToDate.getSelectedItem().getValue(), false);
				}
			}else {
				if(this.cbRecalFromDate.getSelectedItem() == null || this.cbRecalFromDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
					if(this.cbToDate.getSelectedItem() == null || this.cbToDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
						fillSchDates(this.cbRecalToDate,getFinScheduleData(),getFinScheduleData().getFinanceMain().getFinStartDate(), true);
					}else{
						fillSchDates(this.cbRecalToDate,getFinScheduleData(),(Date)this.cbToDate.getSelectedItem().getValue(), false);
					}
				}else{
					fillSchDates(this.cbRecalToDate,getFinScheduleData(),(Date)this.cbRecalFromDate.getSelectedItem().getValue(), true);
				}
			}
			
		}else if(recalType.equals(CalculationConstants.RPYCHG_ADDTERM) || recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
			this.numOfTermsRow.setVisible(true);
			this.recallFromDateRow.setVisible(false);
			this.cbRecalFromDate.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.cbRecalToDate.setSelectedIndex(0);
			
			if(recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)){
				this.recallFromDateRow.setVisible(true);
				if(this.cbToDate.getSelectedItem() == null || this.cbToDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
					fillSchDates(this.cbRecalFromDate,getFinScheduleData(),getFinScheduleData().getFinanceMain().getFinStartDate(), true);
				}else{
					fillSchDates(this.cbRecalFromDate,getFinScheduleData(),(Date)this.cbToDate.getSelectedItem().getValue(), false);
				}
			}
		}else {
			this.recallFromDateRow.setVisible(false);
			this.cbRecalFromDate.setSelectedIndex(0);
			this.cbRecalToDate.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when user changes from date
	 * @param event
	 * 
	 */
	public void onChange$cbRecalFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.recallToDateRow.isVisible() && !this.cbRecalToDate.isDisabled()) {
			this.cbRecalToDate.getItems().clear();		
			if (isValidComboValue(this.cbRecalFromDate,Labels.getLabel("label_PostponementDialog_TillDateFrom.value"))) {
				fillSchDates(this.cbRecalToDate, getFinScheduleData(),
						(Date) this.cbRecalFromDate.getSelectedItem().getValue(), true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when user changes date
	 * @param event
	 * 
	 */
	public void onChange$cbFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbFromDate.getSelectedIndex() > 0) {
			this.cbReCalType.setDisabled(false);
			this.cbRecalFromDate.getItems().clear();		
			if (isValidComboValue(this.cbFromDate,Labels.getLabel("label_PostponementDialog_FromDate.value"))) {
				fillSchDates(this.cbToDate, getFinScheduleData(),
						(Date) this.cbFromDate.getSelectedItem().getValue(), true);
			}
		}else {
			this.cbReCalType.setSelectedIndex(0);
			this.cbReCalType.setDisabled(true);
			this.recallFromDateRow.setVisible(false);
			this.cbRecalFromDate.setSelectedIndex(0);
			this.cbRecalToDate.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when user changes date
	 * @param event
	 * 
	 */
	public void onChange$cbToDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbToDate.getSelectedIndex() > 0) {
			this.cbRecalFromDate.getItems().clear();	
			this.cbRecalToDate.getItems().clear();	
			if (isValidComboValue(this.cbToDate,Labels.getLabel("label_PostponementDialog_ToDate.value"))) {
				fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
						(Date) this.cbToDate.getSelectedItem().getValue(), false);
				fillSchDates(this.cbRecalToDate, getFinScheduleData(),
						(Date) this.cbToDate.getSelectedItem().getValue(), false);
			}
		}else {
			this.cbReCalType.setSelectedIndex(0);
			this.cbReCalType.setDisabled(true);
			this.recallFromDateRow.setVisible(false);
			this.cbRecalFromDate.setSelectedIndex(0);
			this.cbRecalToDate.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
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

	public void setPostponementService(PostponementService postponementService) {
		this.postponementService = postponementService;
	}
	
}

