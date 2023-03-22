/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PostponementDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
 * Modified Date : 05-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-10-2011 Pennant 0.1 * * * * * * * * *
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.PrincipalHolidayService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class PrincipleHolidayDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -7778031557272602004L;
	private static final Logger logger = LogManager.getLogger(PrincipleHolidayDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PrincipleHolidayDialog;

	protected Combobox cbFromDate;
	protected Intbox noOfPriHld;
	protected Combobox cbReCalType;
	protected Textbox remarks;
	protected Button btnPrincipleHoliday;
	private boolean appDateValidationReq = false;
	private PrincipalHolidayService principalHolidayService;

	private String moduleDefiner;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;

	/**
	 * default constructor.<br>
	 */
	public PrincipleHolidayDialogCtrl() {
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
	public void onCreate$window_PrincipleHolidayDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PrincipleHolidayDialog);

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
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq = (boolean) arguments.get("appDateValidationReq");
			}
			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PrincipleHolidayDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		// Empty sent any required attributes
		this.remarks.setMaxlength(200);
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnPrincipleHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * The Click event is raised when the Close event is occurred.
	 * 
	 * @param event
	 * 
	 */
	public void onClose(Event event) {
		doClose(false);
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);
		doSetValidation();
		doWriteComponentsToBean();
		this.window_PrincipleHolidayDialog.onClose();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug(Literal.ENTERING);

		fillSchFromDates(this.cbFromDate, aFinSchData.getFinanceScheduleDetails());
		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDLAST,ADJTERMS,STEPPOS,");

		logger.debug(Literal.LEAVING);
	}

	/** To fill schedule dates */
	private void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug(Literal.ENTERING);
		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		Date curBussDate = SysParamUtil.getAppDate();

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				FinanceMain financeMain = getFinScheduleData().getFinanceMain();

				if (i == 0 || i == financeScheduleDetails.size() - 1) {
					continue;
				}

				if (financeMain.getGrcPeriodEndDate().compareTo(curSchd.getSchDate()) >= 0
						&& !CalculationConstants.SCHMTHD_PFTCAP.equals(curSchd.getSchdMethod())) {
					continue;
				}

				// Not allow Before Current Business Date
				if (appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					continue;
				}

				if (FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())
						|| FinanceConstants.FLAG_HOLIDAY.equals(curSchd.getBpiOrHoliday())) {
					continue;
				}

				// Excluding Present generated file Schedule Terms
				if (curSchd.getPresentmentId() > 0) {
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}

				// Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
						|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}

				// If maturity Terms, not include in list
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (curSchd.isRvwOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isCpzOnSchDate()
						&& !curSchd.isPftOnSchDate()) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());

				if ("B".equals(curSchd.getBpiOrHoliday())) {
					continue;
				} else {
					comboitem.setValue(curSchd.getSchDate());
				}

				dateCombobox.appendChild(comboitem);
				if (getFinanceScheduleDetail() != null) {
					if (curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		Date fromDate = null;
		Date toDate = null;
		Date recalFromDate = null;
		Date recalToDate = null;
		try {
			if (isValidComboValue(this.cbFromDate, Labels.getLabel("label_PostponementDialog_FromDate.value"))) {
				fromDate = (Date) this.cbFromDate.getSelectedItem().getValue();
				getFinScheduleData().getFinanceMain().setEventFromDate(fromDate);
				finServiceInstruction.setFromDate(fromDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setTerms(this.noOfPriHld.getValue());
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

		// Adjust Terms Calculation
		int adjTerms = 0;
		int sdSize = getFinScheduleData().getFinanceScheduleDetails().size();
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			if (curSchd.isRepayOnSchDate()
					|| curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (DateUtility.compare(curSchd.getSchDate(), fromDate) >= 0
						&& DateUtility.compare(curSchd.getSchDate(), toDate) <= 0) {
					adjTerms = adjTerms + 1;
				}
			}
		}

		finServiceInstruction.setRecalFromDate(recalFromDate);
		getFinScheduleData().getFinanceMain().setRecalFromDate(recalFromDate);
		finServiceInstruction.setRecalToDate(recalToDate);
		getFinScheduleData().getFinanceMain().setRecalToDate(recalToDate);
		finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_ADJMDT);

		finServiceInstruction.setFinID(getFinScheduleData().getFinanceMain().getFinID());
		finServiceInstruction.setFinReference(getFinScheduleData().getFinanceMain().getFinReference());
		finServiceInstruction.setFinEvent(getScheduleDetailDialogCtrl().getFinanceDetail().getModuleDefiner());
		getFinScheduleData().setFeeEvent(moduleDefiner);
		getFinScheduleData().setModuleDefiner(moduleDefiner);

		// Service details calling for Schedule calculation
		getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
		finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

		// Show Error Details in Schedule Maintenance
		if (getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();

		} else {
			getFinScheduleData().setSchduleGenerated(true);
			if (StringUtils.isNotBlank(moduleDefiner)) {
				if (FinServiceEvent.PRINH.equals(moduleDefiner)) {
					setFinScheduleData(
							principalHolidayService.doPrincipalHoliday(getFinScheduleData(), finServiceInstruction));

				}
			}

			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
		}
		doRemoveValidation();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * TO Check allowed deferment per year
	 * 
	 * @param defDate
	 * @return
	 * @throws InterruptedException
	 */
	public boolean checkPlannedDeferment(Date defDate) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		List<FinanceScheduleDetail> list = getFinScheduleData().getFinanceScheduleDetails();
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();

		int perYear = financeMain.getPlanDeferCount();

		// No Planned Deferments Exists. No need of external Validation
		if (perYear == 0) {
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

			if (yearEnd.compareTo(financeMain.getMaturityDate()) >= 0) {
				break;
			}
		}

		// Check total deferments made in the specified year.
		if (defermentStart != null && defermentEnd != null) {

			int curretnDefCount = 0;
			for (FinanceScheduleDetail financeScheduleDetail : list) {
				Date schdate = financeScheduleDetail.getSchDate();

				if (schdate.compareTo(defermentStart) >= 0 && schdate.compareTo(defermentEnd) <= 0) {

					if (financeScheduleDetail.isRepayOnSchDate()
							&& financeScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						curretnDefCount++;
					}
				}
			}

			if (curretnDefCount == perYear) {
				MessageUtil.showError(Labels.getLabel("label_PostponementDialog_AllowedPerYear.value",
						new String[] { String.valueOf(perYear) }));
				return false;
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws Exception
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_PrincipleHolidayDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to clear error messages.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.cbFromDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug("Entering");

		setValidation(true);
		if (!this.noOfPriHld.isReadonly()) {
			this.noOfPriHld.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PrincipleHolidayDialog_NoOfPriHld.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		logger.debug(Literal.LEAVING);

	}

	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.noOfPriHld.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when user changes recalculation type
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbReCalType(Event event) {
		//
	}

	/**
	 * when user changes from date
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbRecalFromDate(Event event) {
		//
	}

	/**
	 * when user changes date
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbFromDate(Event event) {
		//
	}

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

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public PrincipalHolidayService getPrincipalHolidayService() {
		return principalHolidayService;
	}

	public void setPrincipalHolidayService(PrincipalHolidayService principalHolidayService) {
		this.principalHolidayService = principalHolidayService;
	}

}
