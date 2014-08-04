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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/Additional/ChangeProfitDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ChangeProfitDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -686158342325561513L;
	private final static Logger logger = Logger.getLogger(ChangeProfitDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ChangeProfitDialog; 	// autowired
	protected Combobox cbProfitFromDate; 			// autowired
	protected Combobox cbProfitToDate; 				// autowired
	protected Decimalbox wIAmount; 					// autowired
	protected Date actPftFromDate = null;
	
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient int oldVar_wIProfitChangeFromDate;
	private transient int oldVar_wIProfitChangeToDate;
	private transient BigDecimal oldVar_wIAmount;

	// not auto wired vars
	private FinScheduleData finScheduleData = null; 				// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail = null; 	// overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;

	/**
	 * default constructor.<br>
	 */
	public ChangeProfitDialogCtrl() {
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
	public void onCreate$window_ChangeProfitDialog(Event event) throws Exception {
		logger.debug(event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("finScheduleData")) {
			setFinScheduleData((FinScheduleData) args.get("finScheduleData"));
		} 
		
		if (args.containsKey("financeScheduleDetail")) {
			setFinanceScheduleDetail((FinanceScheduleDetail) args.get("financeScheduleDetail"));
		} 

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (args.containsKey("financeMainDialogCtrl")) {
			setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) args.get("financeMainDialogCtrl"));
		} 

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_ChangeProfitDialog.doModal();
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
			setDialog(this.window_ChangeProfitDialog);
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
		this.oldVar_wIProfitChangeFromDate = this.cbProfitFromDate.getSelectedIndex();
		this.oldVar_wIProfitChangeToDate = this.cbProfitToDate.getSelectedIndex();
		this.oldVar_wIAmount = this.wIAmount.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cbProfitFromDate.setReadonly(true);
		this.cbProfitToDate.setReadonly(true);
		this.wIAmount.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.wIAmount.setMaxlength(18);
		this.wIAmount.setFormat(PennantAppUtil
				.getAmountFormate(getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
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
	public void onClick$btnChangeProfit(Event event) throws InterruptedException {
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
			this.window_ChangeProfitDialog.onClose();
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
		this.window_ChangeProfitDialog.onClose();
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
		if (this.oldVar_wIProfitChangeFromDate != this.cbProfitFromDate
				.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_wIProfitChangeToDate != this.cbProfitToDate
				.getSelectedIndex()) {
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
		fillSchFromDates(this.cbProfitFromDate,
				aFinSchData.getFinanceScheduleDetails());
		
		actPftFromDate = aFinSchData.getFinanceMain().getFinStartDate();

		if(getFinanceScheduleDetail() != null ) {
			fillSchToDates(this.cbProfitToDate,
					aFinSchData.getFinanceScheduleDetails(), getFinanceScheduleDetail().getSchDate());
		}else {
			fillSchToDates(this.cbProfitToDate,
					aFinSchData.getFinanceScheduleDetails(), aFinSchData.getFinanceMain().getFinStartDate());
		}
		
		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		if (getFinanceScheduleDetail() != null) {
			if (getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE)) {
				this.wIAmount.setValue(PennantAppUtil.formateAmount(
						getFinanceScheduleDetail().getPrincipalSchd(),
						aFinSchData.getFinanceMain().getLovDescFinFormatter()));
			} else {
				this.wIAmount.setValue(PennantAppUtil.formateAmount(
						getFinanceScheduleDetail().getRepayAmount(),
						aFinSchData.getFinanceMain()
						.getLovDescFinFormatter()));
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbProfitFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Not Allowed for Repayment
				/*if (!curSchd.isRepayOnSchDate() ) {
					continue;
				}*/

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
				comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	public void fillSchToDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillAfter) {
		logger.debug("Entering");
		if(dateCombobox.getId().equals("cbProfitToDate")) {
			this.cbProfitToDate.getItems().clear();
		} 
		
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				/*//Not Allowed for Repayment
				if (!curSchd.isRepayOnSchDate() ) {
					continue;
				}*/

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
				comboitem.setAttribute("toSpecifier",curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else if(curSchd.getSchDate().compareTo(fillAfter) >= 0) {
					dateCombobox.appendChild(comboitem);
				} else if(curSchd.getSchDate().compareTo(fillAfter) < 0) {
					actPftFromDate = curSchd.getSchDate();
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
		try {
			if(this.wIAmount.getValue()!=null) {
				if(this.wIAmount.getValue().compareTo(BigDecimal.ZERO)==-1) {
					throw new WrongValueException(
							this.wIAmount,
							Labels.getLabel(
									"AMOUNT_NO_NUMBER",
									new String[]{
											Labels.getLabel("label_ChangeProfitDialog_RepayAmount.value")
									}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(
					this.cbProfitFromDate,
					Labels.getLabel("label_ChangeProfitDialog_FromDate.value"))) {
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)this.cbProfitFromDate.getSelectedItem().getValue());
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)actPftFromDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(
					this.cbProfitToDate,
					Labels.getLabel("label_ChangeProfitDialog_ToDate.value"))
					&& this.cbProfitFromDate.getSelectedIndex() != 0) {
				if (((Date) this.cbProfitToDate.getSelectedItem()
						.getValue())
						.compareTo((Date) this.cbProfitFromDate
								.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(
							this.cbProfitToDate,
							Labels.getLabel(
									"DATE_ALLOWED_AFTER",
									new String[]{
											Labels.getLabel("label_ChangeProfitDialog_ToDate.value"),
											Labels.getLabel("label_ChangeProfitDialog_FromDate.value")}));
				} else {
					getFinScheduleData().getFinanceMain().setEventToDate((Date)this.cbProfitToDate.getSelectedItem().getValue());
				}
			}
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
		
		setFinScheduleData(ScheduleCalculator.changeProfit(
				getFinScheduleData(), PennantAppUtil.unFormateAmount(
						this.wIAmount.getValue(),
						getFinScheduleData().getFinanceMain().getLovDescFinFormatter())));
		
		//Show Error Details in Schedule Maintainance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			PTMessageUtils.showErrorMessage(getFinScheduleData().getErrorDetails().get(0));
		}else{
			getFinScheduleData().setSchduleGenerated(true);
			if(getScheduleDetailDialogCtrl()!=null){
				try {
					getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
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
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.wIAmount.clearErrorMessage();
		this.cbProfitFromDate.clearErrorMessage();
		this.cbProfitToDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	
	public void onChange$cbProfitFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		this.cbProfitToDate.setDisabled(true);		
		if (isValidComboValue(this.cbProfitFromDate,Labels.getLabel("label_ChangeProfitDialog_FromDate.value"))) {
			fillSchToDates(this.cbProfitToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbProfitFromDate.getSelectedItem().getValue());
			this.cbProfitToDate.setDisabled(false);
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
	
	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}
	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}
	
}
