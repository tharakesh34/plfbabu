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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.FrequencyBox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/ChangeFrequencyDialog.zul file.
 */
public class ChangeFrequencyDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long					serialVersionUID	= 454600127282110738L;
	private final static Logger					logger				= Logger.getLogger(ChangeFrequencyDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_ChangeFrequencyDialog;

	protected FrequencyBox						repayFrq;
	protected Combobox							cbFrqFromDate;
	protected Datebox							grcPeriodEndDate;
	protected Datebox							nextGrcRepayDate;
	protected Datebox							nextRepayDate;
	protected Checkbox							pftIntact;
	protected Uppercasebox						serviceReqNo;
	protected Textbox							remarks;
	
	protected Row								row_GrcPeriodEndDate;
	protected Row								row_grcNextRepayDate;

	// not auto wired vars
	private FinScheduleData						finScheduleData;															// overhanded per param
	private ScheduleDetailDialogCtrl			financeMainDialogCtrl;

	private transient ChangeFrequencyService	changeFrequencyService;

	/**
	 * default constructor.<br>
	 */
	public ChangeFrequencyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ChangeFrequencyDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangeFrequencyDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			// we get the FinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIF/FinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setFinanceMainDialogCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ChangeFrequencyDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
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
			this.window_ChangeFrequencyDialog.onClose();
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
		this.grcPeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.repayFrq.setMandatoryStyle(true);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");

	}

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
		doWriteComponentsToBean();
		this.window_ChangeFrequencyDialog.onClose();
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

		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();

		this.repayFrq.setDisableFrqCode(true);
		this.repayFrq.setDisableFrqMonth(true);
		this.repayFrq.setDisableFrqDay(false);
		this.repayFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		if (aFinSchData.getFinanceType().isFinPftUnChanged()) {
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		} else {
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}
		fillSchFromDates(aFinSchData.getFinanceScheduleDetails());

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
			Date grcEndDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			FinanceScheduleDetail prvSchd = null;
			boolean isPrvShcdAdded = false;
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if(i == 0){
					prvSchd = curSchd;
				}

				// Not Allowing Grace Period Dates
				if(curSchd.getSchDate().compareTo(grcEndDate) <= 0){
					if(curSchd.getSchDate().compareTo(grcEndDate) == 0){
						prvSchd = curSchd;
					}
					continue;
				}
				//Change Frequency is not allowed for the schedule which has the presenment
				if(curSchd.getPresentmentId() > 0){
					prvSchd = curSchd;
					continue;
				}
				//Not Review Date
				if (!curSchd.isRepayOnSchDate() && !getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					continue;
				}
				
				// Only allowed if payment amount is greater than Zero
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);
					
					prvSchd = curSchd;
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
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
				
				if(curSchd.getSchDate().compareTo(DateUtility.getAppDate()) < 0){
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);
					prvSchd = curSchd;
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

		try {
			if (isValidComboValue(this.repayFrq.getFrqDayCombobox(), Labels.getLabel("label_FrqDay.value"))) {
				frq = this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue();
			}
			finServiceInstruction.setRepayFrq(this.repayFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbFrqFromDate, Labels.getLabel("label_ChangeFrequencyDialog_FromDate.value"))) {
				fromDate = (Date) this.cbFrqFromDate.getSelectedItem().getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.row_GrcPeriodEndDate.isVisible()) {
			
			try {
				if (fromDate != null && fromDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
					if (this.grcPeriodEndDate.getValue() == null) {
						throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("FIELD_IS_MAND",
								new String[] { Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value") }));
					} else {
						if (this.grcPeriodEndDate.getValue().compareTo(financeMain.getFinStartDate()) < 0) {
							throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel(
									"DATE_ALLOWED_MINDATE_EQUAL",
									new String[] {
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"),
											Labels.getLabel("label_ChangeFrequencyDialog_FinStartDate.value") }));
						} else if (this.grcPeriodEndDate.getValue().compareTo(fromDate) < 0) {
							throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel(
									"DATE_ALLOWED_MINDATE",
									new String[] {
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"),
											DateUtility.formatToLongDate(fromDate) }));
						} else if (!financeMain.isNewRecord()
								&& !StringUtils.trimToEmpty(financeMain.getRecordType()).equals(
										PennantConstants.RECORD_TYPE_NEW)) {

							if (StringUtils.equals(getFinScheduleData().getFinanceType().getFinCategory(),
									FinanceConstants.PRODUCT_IJARAH)
									|| StringUtils.equals(getFinScheduleData().getFinanceType().getFinCategory(),
											FinanceConstants.PRODUCT_FWIJARAH)) {
								Date curBussDate = DateUtility.getAppDate();
								if (this.grcPeriodEndDate.getValue().compareTo(curBussDate) <= 0) {
									throw new WrongValueException(
											this.grcPeriodEndDate,
											Labels.getLabel(
													"DATE_ALLOWED_MINDATE_EQUAL",
													new String[] {
															Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"),
															Labels.getLabel("label_ChangeFrequencyDialog_CurBussDate.value") }));
								}
							}
						}
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			finServiceInstruction.setNextGrcRepayDate(this.nextGrcRepayDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.nextGrcRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null) {
				if (this.nextGrcRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) > 0) {
					throw new WrongValueException(this.nextGrcRepayDate, Labels.getLabel("DATE_ALLOWED_MAXDATE",
							new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value"),
									Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value") }));
				} else if (this.nextGrcRepayDate.getValue().compareTo(financeMain.getFinStartDate()) <= 0) {
					throw new WrongValueException(this.nextGrcRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE",
							new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value"),
									Labels.getLabel("label_ChangeFrequencyDialog_FinStartDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.nextRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null) {
				if (this.nextRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) <= 0) {
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel(
							"DATE_ALLOWED_MINDATE",
							new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),
									Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.nextRepayDate.getValue() != null && fromDate != null) {
				if (this.nextRepayDate.getValue().compareTo(fromDate) <= 0) {
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel("DATE_ALLOWED_MINDATE",
							new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),
									DateUtility.formatToShortDate(fromDate) }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.nextRepayDate.getValue() != null && this.nextGrcRepayDate.getValue() != null) {
				if (this.nextRepayDate.getValue().compareTo(this.nextGrcRepayDate.getValue()) <= 0) {
					throw new WrongValueException(this.nextRepayDate, Labels.getLabel(
							"DATE_ALLOWED_MINDATE",
							new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),
									Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setNextRepayDate(this.nextRepayDate.getValue());
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
		finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_CHGFRQ);
		finServiceInstruction.setFromDate(fromDate);
		finServiceInstruction.setRepayFrq(frq);
		finServiceInstruction.setGrcPeriodEndDate(this.grcPeriodEndDate.getValue());
		finServiceInstruction.setNextGrcRepayDate(this.nextGrcRepayDate.getValue());
		finServiceInstruction.setNextRepayDate(this.nextRepayDate.getValue());

		// call change frequency method to calculate new schedules
		setFinScheduleData(changeFrequencyService.doChangeFrequency(getFinScheduleData(), finServiceInstruction));
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

		//Show Error Details in Schedule Maintenance
		if (getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		} else {
			getFinScheduleData().setSchduleGenerated(true);
			if (getFinanceMainDialogCtrl() != null) {
				try {
					getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
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

		logger.debug("Leaving");
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

	public void setChangeFrequencyService(ChangeFrequencyService changeFrequencyService) {
		this.changeFrequencyService = changeFrequencyService;
	}

}