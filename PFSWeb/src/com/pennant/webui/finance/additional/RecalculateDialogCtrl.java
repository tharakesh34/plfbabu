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
 * FileName    		:  WIRecalculateDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.additional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceMainDialogCtrl;
import com.pennant.webui.finance.wiffinancemain.WIFFinanceMainDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class RecalculateDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(RecalculateDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_RecalculateDialog; 				// autowired
	protected Combobox cbTillDate; 							// autowired
	protected Combobox cbFromDate; 							// autowired
	protected Combobox cbReCalType; 						// autowired
	protected Row tillDateRow;
	// not auto wired vars
	private FinScheduleData finScheduleData; 				// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 	// overhanded per param
	private transient WIFFinanceMainDialogCtrl wIFFinanceMainDialogCtrl;
	private transient FinanceMainDialogCtrl financeMainDialogCtrl;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_fromDate;
	private transient String oldVar_reCalType;
	private transient int oldVar_tillDate;
	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public RecalculateDialogCtrl() {
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
	public void onCreate$window_RecalculateDialog(Event event) throws Exception {
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
			this.setFinanceScheduleDetail((FinanceScheduleDetail) args.get("financeScheduleDetail"));
			setFinanceScheduleDetail(this.financeScheduleDetail);
		} else {
			setFinanceScheduleDetail(null);
		}

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (args.containsKey("wIFFinanceMainDialogCtrl")) {
			setWIFFinanceMainDialogCtrl((WIFFinanceMainDialogCtrl) args
					.get("wIFFinanceMainDialogCtrl"));
		} else {
			setWIFFinanceMainDialogCtrl(null);
		}
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((FinanceMainDialogCtrl) args
					.get("financeMainDialogCtrl"));
		} else {
			setFinanceMainDialogCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_RecalculateDialog.doModal();
		logger.debug("Leaving");
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	/**
	 * @return the financeScheduleDetail
	 */
	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	/**
	 * @param financeScheduleDetail the financeScheduleDetail to set
	 */
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	/**
	 * @return the wIFFinanceMainDialogCtrl
	 */
	public WIFFinanceMainDialogCtrl getWIFFinanceMainDialogCtrl() {
		return wIFFinanceMainDialogCtrl;
	}

	/**
	 * @param wIFFinanceMainDialogCtrl the wIFFinanceMainDialogCtrl to set
	 */
	public void setWIFFinanceMainDialogCtrl(
			WIFFinanceMainDialogCtrl wIFFinanceMainDialogCtrl) {
		this.wIFFinanceMainDialogCtrl = wIFFinanceMainDialogCtrl;
	}

	/**
	 * @return the financeMainDialogCtrl
	 */
	public FinanceMainDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	/**
	 * @param financeMainDialogCtrl the financeMainDialogCtrl to set
	 */
	public void setFinanceMainDialogCtrl(FinanceMainDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	
	/**
	 * @return the validationOn
	 */
	public boolean isValidationOn() {
		return validationOn;
	}

	/**
	 * @param validationOn the validationOn to set
	 */
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
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
		this.oldVar_reCalType = this.cbReCalType.getValue();
		this.oldVar_tillDate = this.cbTillDate.getSelectedIndex();
		this.oldVar_fromDate = this.cbFromDate.getValue();
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
			setDialog(this.window_RecalculateDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}	


	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		if(aFinSchData.getFinanceMain() != null ){
			fillSchReCalTypes(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType());
			fillSchFromDates(this.cbFromDate, aFinSchData.getFinanceScheduleDetails());
			fillSchDates(this.cbTillDate,aFinSchData);
		}
		logger.debug("Leaving");	
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				if (financeScheduleDetails.get(i).isRepayOnSchDate()) {
					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(
							financeScheduleDetails.get(i).getSchDate(),
							PennantConstants.dateFormate));
					comboitem.setValue(financeScheduleDetails.get(i).getSchDate());
					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null
							&& financeScheduleDetails.get(i).getSchDate()
							.equals(getFinanceScheduleDetail().getSchDate())) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}


	/** To fill schedule calculation types in combo */
	public void fillSchReCalTypes(Combobox codeCombobox, String value){
		logger.debug("Entering");
		List<ValueLabel> recalTypes = PennantAppUtil.getSchCalCodes();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);
		for (int i = 0; i < recalTypes.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(recalTypes.get(i).getValue());
			comboitem.setLabel(recalTypes.get(i).getLabel());
			if(!recalTypes.get(i).getValue().equals(CalculationConstants.RPYCHG_CURPRD)){
				codeCombobox.appendChild(comboitem);
				if (StringUtils.trimToEmpty(value).equals(recalTypes.get(i).getValue())) {
					codeCombobox.setSelectedItem(comboitem);
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
	public void doWriteComponentsToBean() {
		logger.debug("Entering");
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try{
			if (isValidComboValue(
					this.cbReCalType,
					Labels.getLabel("label_RecalculateDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				getFinScheduleData().getFinanceMain().setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());

			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(
					this.cbFromDate,
					Labels.getLabel("label_RecalculateDialog_FromDate.value"))) {
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)this.cbFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if(this.tillDateRow.isVisible()){
			try {
				if(this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_TillDate.value") }));
				}
				if(((Date)this.cbTillDate.getSelectedItem().getValue()).compareTo(
						getFinScheduleData().getFinanceMain().getFinStartDate())<0 ||
						((Date)this.cbTillDate.getSelectedItem().getValue()).compareTo(getFinScheduleData().getFinanceMain().getMaturityDate())>0){
					throw new WrongValueException(
							this.cbTillDate,
							Labels.getLabel(
									"DATE_RANGE",
									new String[]{
											Labels.getLabel("label_RecalculateDialog_TillDate.value"),
											PennantAppUtil.formateDate(getFinScheduleData().getFinanceMain().getFinStartDate(),
													PennantConstants.dateFormate),
													PennantAppUtil.formateDate(getFinScheduleData().getFinanceMain().getMaturityDate(),
															PennantConstants.dateFormate)
									}));
				}
			} catch (WrongValueException we) {
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
		if(this.tillDateRow.isVisible() && this.cbTillDate.getSelectedIndex() > 0){
			getFinScheduleData().getFinanceMain().setRecalToDate((Date)this.cbTillDate.getSelectedItem().getValue());
			setFinScheduleData(ScheduleCalculator.reCalSchd(getFinScheduleData()));
		}else {
			setFinScheduleData(ScheduleCalculator.reCalSchd(getFinScheduleData()));
		}
		getFinScheduleData().setSchduleGenerated(true);
		if(getFinanceMainDialogCtrl()!=null){
			this.financeMainDialogCtrl.doFillScheduleList(getFinScheduleData(), null);
		}else {
			this.wIFFinanceMainDialogCtrl.doFillScheduleList(getFinScheduleData());
		}
		
		this.window_RecalculateDialog.onClose();
		logger.debug("Leaving");
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
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
	public void onClick$btnAddRecalculate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(getFinanceScheduleDetail()!=null){
			if(isDataChanged()){
				doSave();
			}else{
				PTMessageUtils.showErrorMessage("No Data has been changed.");
				// TODO close dialog if message not needed.
				//this.window_WIApplyChangeDialog.onClose();
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


	private void doClose() throws InterruptedException {
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
			this.window_RecalculateDialog.onClose();
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
		logger.debug("Leaving");
	}

	private void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cbReCalType.clearErrorMessage();
		this.cbFromDate.clearErrorMessage();
		if(tillDateRow.isVisible()){
			this.cbTillDate.clearErrorMessage();
		}
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

		if (this.oldVar_reCalType != this.cbReCalType
				.getValue()) {
			return true;
		}
		if ( tillDateRow.isVisible() && this.oldVar_tillDate != this.cbTillDate.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_fromDate != this.cbFromDate
				.getValue()) {
			return true;
		}
		return false;
	}

	//Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		this.cbTillDate.clearErrorMessage();
		if(this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLDATE)){
			this.tillDateRow.setVisible(true);
		}else {
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	/** To fill schedule dates */
	public void fillSchDates(Combobox dateCombobox, FinScheduleData aFinSchData) {
		logger.debug("Entering");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (aFinSchData.getFinanceScheduleDetails() != null) {
			List<FinanceScheduleDetail> financeScheduleDetails = aFinSchData.getFinanceScheduleDetails();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(
						financeScheduleDetails.get(i).getSchDate(),
						PennantConstants.dateFormate));
				comboitem.setValue(financeScheduleDetails.get(i).getSchDate());
				dateCombobox.appendChild(comboitem);
			}
		}
		logger.debug("Leaving");
	}
}

