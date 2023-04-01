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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class PostponementDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -7778031557272602004L;
	private static final Logger logger = LogManager.getLogger(PostponementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PostponementDialog;

	protected Combobox cbFromDate;
	protected Combobox cbToDate;
	protected Combobox cbReCalType;
	protected Combobox cbRecalFromDate;
	protected Combobox cbRecalToDate;
	protected Intbox adjTerms;
	protected Checkbox pftIntact;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;
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
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_PostponementDialog(Event event) {
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
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		if (StringUtils.equals(FinServiceEvent.POSTPONEMENT, moduleDefiner)) {
			this.recalTypeRow.setVisible(false);
			this.recallFromDateRow.setVisible(false);
			this.recallToDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		} else if (StringUtils.equals(FinServiceEvent.UNPLANEMIH, moduleDefiner)) {
			this.window_PostponementDialog.setTitle(Labels.getLabel("window_UnPlannedEMiHDialog.title"));
			this.btnPostponement.setLabel(Labels.getLabel("btnUnPlannedEMiH.label"));
			this.btnPostponement.setTooltiptext(Labels.getLabel("btnUnPlannedEMiH.tooltiptext"));
			this.recalTypeRow.setVisible(true);
		} else if (StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
			this.window_PostponementDialog.setTitle(Labels.getLabel("window_ReAgeHolidayDialog.title"));
			this.btnPostponement.setLabel(Labels.getLabel("btnReAgeHoliday.label"));
			this.btnPostponement.setTooltiptext(Labels.getLabel("btnReAgeHoliday.tooltiptext"));

			this.recalTypeRow.setVisible(false);
			this.recallFromDateRow.setVisible(false);
			this.recallToDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public void onClick$btnPostponement(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering");
		doWriteComponentsToBean();
		this.window_PostponementDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		if (StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
			fillODSchDates(this.cbFromDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
			fillODSchDates(this.cbToDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
		} else {
			fillSchDates(this.cbFromDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
			fillSchDates(this.cbToDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
			fillSchDates(this.cbRecalFromDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), false);
			fillSchDates(this.cbRecalToDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate(), true);
		}
		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDLAST,ADJTERMS,STEPPOS,");

		if (!StringUtils.equals(FinServiceEvent.POSTPONEMENT, moduleDefiner)
				&& !StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
			if (StringUtils.equalsIgnoreCase(getFinScheduleData().getFinanceMain().getRecalType(),
					CalculationConstants.RPYCHG_TILLDATE)
					|| StringUtils.equalsIgnoreCase(getFinScheduleData().getFinanceMain().getRecalType(),
							CalculationConstants.RPYCHG_TILLMDT)) {
				this.recallFromDateRow.setVisible(true);
				if (getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLMDT)) {
					this.recallToDateRow.setVisible(false);
					fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
							getFinScheduleData().getFinanceMain().getFinStartDate(), false);
				} else {
					this.recallToDateRow.setVisible(true);
				}
			}
		}

		if (aFinSchData.getFinanceType().isFinPftUnChanged()) {
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		} else {
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}

		logger.debug("Entering");
	}

	/** To fill schedule dates */
	private void fillSchDates(Combobox dateCombobox, FinScheduleData financeDetail, Date fillAfter,
			boolean includeDate) {
		logger.debug("Entering");

		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		Date graceEndDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
		if (financeDetail.getFinanceScheduleDetails() != null) {

			Date unplanEMIHStart = DateUtil.addMonths(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate(),
					getFinScheduleData().getFinanceMain().getUnPlanEMIHLockPeriod());

			List<FinanceScheduleDetail> financeScheduleDetails = financeDetail.getFinanceScheduleDetails();
			Date curBussDate = SysParamUtil.getAppDate();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Not allow Before Current Business Date
				if (curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					continue;
				}

				if (DateUtil.compare(curSchd.getSchDate(), graceEndDate) <= 0) {
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

				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				if (StringUtils.equals(FinServiceEvent.UNPLANEMIH, moduleDefiner)) {
					if (DateUtil.compare(curSchd.getSchDate(), unplanEMIHStart) <= 0) {
						continue;
					}
				}

				// BPI case not allowed to Re-age except Hold EMI
				if (StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
						&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
				comboitem.setValue(curSchd.getSchDate());
				if (fillAfter.compareTo(financeDetail.getFinanceMain().getFinStartDate()) == 0) {
					dateCombobox.appendChild(comboitem);
				} else if (includeDate && curSchd.getSchDate().compareTo(fillAfter) >= 0) {
					dateCombobox.appendChild(comboitem);
				} else if (!includeDate && curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
				}
				/*
				 * In Recalculation type if Till Date is selected and for the Same date if the profit Balance in
				 * schedule is greater than zero then will set the pftbal attribute
				 */
				if (this.cbReCalType.getSelectedIndex() >= 0
						&& StringUtils.equals(this.cbReCalType.getSelectedItem().getValue().toString(),
								CalculationConstants.RPYCHG_TILLDATE)
						&& curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) > 0 && fillAfter != null) {
					comboitem.setStyle("color:Red;");
					comboitem.setAttribute("pftBal", curSchd.getProfitBalance());
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	private void fillODSchDates(Combobox dateCombobox, FinScheduleData financeDetail, Date fillAfter,
			boolean includeDate) {
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
			Date curBussDate = SysParamUtil.getAppDate();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Dont allow after Current Date(Future Schedules)
				if (curSchd.getSchDate().after(curBussDate)) {
					break;
				}

				// Dont allow Grace period Schedules
				if (DateUtil.compare(curSchd.getSchDate(), graceEndDate) <= 0) {
					continue;
				}

				// BPI case not allowed to Re-age except Hold EMI
				if (StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
						&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
					continue;
				}

				// Profit && Paid (Partial/Full)
				if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0
						&& curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0) {
					continue;
				}

				// If no Payment at Term, not allowed to render
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
				comboitem.setValue(curSchd.getSchDate());
				if (fillAfter.compareTo(financeDetail.getFinanceMain().getFinStartDate()) == 0) {
					dateCombobox.appendChild(comboitem);
				} else if (includeDate && curSchd.getSchDate().compareTo(fillAfter) >= 0) {
					dateCombobox.appendChild(comboitem);
				} else if (!includeDate && curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
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
	private void doWriteComponentsToBean() {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		Date fromDate = null;
		Date toDate = null;
		Date recalFromDate = null;
		Date recalToDate = null;
		FinScheduleData schdData = getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		try {
			if (isValidComboValue(this.cbFromDate, Labels.getLabel("label_PostponementDialog_FromDate.value"))) {
				fromDate = (Date) this.cbFromDate.getSelectedItem().getValue();
				fm.setEventFromDate(fromDate);
				finServiceInstruction.setFromDate(fromDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbToDate, Labels.getLabel("label_PostponementDialog_ToDate.value"))) {
				toDate = (Date) this.cbToDate.getSelectedItem().getValue();
				fm.setEventToDate(toDate);
				finServiceInstruction.setToDate(toDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (this.recalTypeRow.isVisible()) {
			try {
				if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_PostponementDialog_RecalType.value"))
						&& this.cbReCalType.getSelectedIndex() != 0) {
					fm.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
					finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if (this.recallFromDateRow.isVisible()) {
			try {
				if (this.cbRecalFromDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbRecalFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_PostponementDialog_RecalFromDate.value") }));
				}
				recalFromDate = (Date) this.cbRecalFromDate.getSelectedItem().getValue();
				if (this.cbFromDate.getSelectedIndex() > 0 && recalFromDate.compareTo(fromDate) <= 0) {
					throw new WrongValueException(this.cbRecalFromDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_PostponementDialog_RecalFromDate.value"),
											DateUtil.formatToLongDate(fromDate) }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if (this.recallToDateRow.isVisible() && !this.cbRecalToDate.isDisabled()) {
			try {
				if (this.cbRecalToDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_PostponementDialog_RecalToDate.value") }));
				}
				// if schdpftBal greater than zero throw validation
				if (this.cbRecalToDate.getSelectedItem().getAttribute("pftBal") != null) {
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("Label_finSchdTillDate"));
				}

				recalToDate = (Date) this.cbRecalToDate.getSelectedItem().getValue();

				if (this.cbRecalFromDate.getSelectedIndex() > 0 && recalToDate.compareTo(recalFromDate) < 0) {
					throw new WrongValueException(this.cbRecalToDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_PostponementDialog_RecalToDate.value"),
											DateUtil.formatToLongDate(recalFromDate) }));
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
				fm.setAdjTerms(this.adjTerms.intValue());
				finServiceInstruction.setTerms(this.adjTerms.intValue());

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.pftIntactRow.isVisible()) {
			try {
				finServiceInstruction.setPftIntact(this.pftIntact.isChecked());
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

		// Adjust Terms Calculation
		int adjTerms = 0;
		int sdSize = schdData.getFinanceScheduleDetails().size();
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);
			if (curSchd.isRepayOnSchDate()
					|| curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (DateUtil.compare(curSchd.getSchDate(), fromDate) >= 0
						&& DateUtil.compare(curSchd.getSchDate(), toDate) <= 0) {
					adjTerms = adjTerms + 1;
				}
			}
		}

		if (StringUtils.equals(FinServiceEvent.POSTPONEMENT, moduleDefiner)) {
			sdSize = schdData.getFinanceScheduleDetails().size();
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);
				if (DateUtil.compare(curSchd.getSchDate(), fromDate) >= 0
						&& DateUtil.compare(curSchd.getSchDate(), toDate) <= 0) {
					if (!checkPlannedDeferment(fromDate)) {
						return;
					}
				}
			}

			// Check max limit
			if (fm.getAvailedDefRpyChange() + adjTerms > fm.getDefferments()) {
				MessageUtil.showError(Labels.getLabel("label_PostponementDialog_MaxPostponement.value",
						new String[] { String.valueOf(fm.getDefferments()) }));
				return;
			}
		} else if (StringUtils.equals(FinServiceEvent.UNPLANEMIH, moduleDefiner)) {
			// Check max limit
			if (fm.getAvailedUnPlanEmi() + adjTerms > fm.getMaxUnplannedEmi()) {
				MessageUtil.showError(Labels.getLabel("label_PostponementDialog_MaxUnPlanEMIH.value",
						new String[] { String.valueOf(fm.getMaxUnplannedEmi()) }));
				return;
			}
		} else if (StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
			// Check max limit
			if (fm.getAvailedReAgeH() + adjTerms > fm.getMaxReAgeHolidays()) {
				MessageUtil.showError(Labels.getLabel("label_PostponementDialog_MaxReAgeH.value",
						new String[] { String.valueOf(fm.getMaxReAgeHolidays()) }));
				return;
			}
		}

		finServiceInstruction.setRecalFromDate(recalFromDate);
		fm.setRecalFromDate(recalFromDate);
		finServiceInstruction.setRecalToDate(recalToDate);
		fm.setRecalToDate(recalToDate);
		if (StringUtils.equals(FinServiceEvent.POSTPONEMENT, moduleDefiner)
				|| StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
			finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_ADDTERM);
		}

		finServiceInstruction.setFinID(fm.getFinID());
		finServiceInstruction.setFinReference(fm.getFinReference());
		finServiceInstruction.setFinEvent(getScheduleDetailDialogCtrl().getFinanceDetail().getModuleDefiner());
		schdData.setFeeEvent(moduleDefiner);

		// Service details calling for Schedule calculation
		fm.setDevFinCalReq(false);
		if (StringUtils.equals(FinServiceEvent.POSTPONEMENT, moduleDefiner)) {
			setFinScheduleData(
					this.postponementService.doPostponement(schdData, finServiceInstruction, fm.getScheduleMethod()));
		} else if (StringUtils.equals(FinServiceEvent.UNPLANEMIH, moduleDefiner)) {
			setFinScheduleData(this.postponementService.doUnPlannedEMIH(schdData));
		} else if (StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
			setFinScheduleData(
					this.postponementService.doReAging(schdData, finServiceInstruction, fm.getScheduleMethod()));
		}

		finServiceInstruction.setPftChg(schdData.getPftChg());
		fm.resetRecalculationFields();
		schdData.setFinServiceInstruction(finServiceInstruction);

		// Show Error Details in Schedule Maintenance
		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			MessageUtil.showError(schdData.getErrorDetails().get(0));
			schdData.getErrorDetails().clear();

		} else {

			schdData.setSchduleGenerated(true);
			if (StringUtils.isNotBlank(moduleDefiner)) {
				if (StringUtils.equals(FinServiceEvent.POSTPONEMENT, moduleDefiner)) {
					int availedDefRpyChange = fm.getAvailedDefRpyChange();
					fm.setAvailedDefRpyChange(availedDefRpyChange + adjTerms);
				} else if (StringUtils.equals(FinServiceEvent.UNPLANEMIH, moduleDefiner)) {
					int availedUnPlanChange = fm.getAvailedUnPlanEmi();
					fm.setAvailedUnPlanEmi(availedUnPlanChange + adjTerms);
				} else if (StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
					int availedReAgeHChange = fm.getAvailedReAgeH();
					fm.setAvailedReAgeH(availedReAgeHChange + adjTerms);

					// Reset ReAge Buckets after Process
					int newReAgeBuckets = fm.getReAgeBucket() + fm.getDueBucket();
					fm.setReAgeBucket(newReAgeBuckets);
				}
			}

			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * TO Check allowed deferment per year
	 * 
	 * @param defDate
	 * @return
	 */
	public boolean checkPlannedDeferment(Date defDate) {
		logger.debug(" Entering ");

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

		logger.debug(" Leaving ");
		return true;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) {
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
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());

		String recalType = this.cbReCalType.getSelectedItem().getValue().toString();
		if (recalType.equals(CalculationConstants.RPYCHG_TILLDATE)
				|| recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.recallFromDateRow.setVisible(true);
			this.recallToDateRow.setVisible(true);
			this.numOfTermsRow.setVisible(false);
			if (recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				this.recallToDateRow.setVisible(false);
				if (this.cbToDate.getSelectedItem() == null
						|| this.cbToDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
					fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
							getFinScheduleData().getFinanceMain().getFinStartDate(), true);
				} else {
					fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
							(Date) this.cbToDate.getSelectedItem().getValue(), false);
				}
			} else {
				if (this.cbRecalFromDate.getSelectedItem() == null || this.cbRecalFromDate.getSelectedItem().getValue()
						.toString().equals(PennantConstants.List_Select)) {
					if (this.cbToDate.getSelectedItem() == null || this.cbToDate.getSelectedItem().getValue().toString()
							.equals(PennantConstants.List_Select)) {
						fillSchDates(this.cbRecalToDate, getFinScheduleData(),
								getFinScheduleData().getFinanceMain().getFinStartDate(), true);
					} else {
						fillSchDates(this.cbRecalToDate, getFinScheduleData(),
								(Date) this.cbToDate.getSelectedItem().getValue(), false);
					}
				} else {
					fillSchDates(this.cbRecalToDate, getFinScheduleData(),
							(Date) this.cbRecalFromDate.getSelectedItem().getValue(), true);
				}
			}

		} else if (recalType.equals(CalculationConstants.RPYCHG_ADDTERM)
				|| recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
			this.numOfTermsRow.setVisible(true);
			this.recallFromDateRow.setVisible(false);
			this.cbRecalFromDate.setSelectedIndex(0);
			this.recallToDateRow.setVisible(false);
			this.cbRecalToDate.setSelectedIndex(0);

			if (recalType.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
				this.recallFromDateRow.setVisible(true);
				if (this.cbToDate.getSelectedItem() == null
						|| this.cbToDate.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
					fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
							getFinScheduleData().getFinanceMain().getFinStartDate(), true);
				} else {
					fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
							(Date) this.cbToDate.getSelectedItem().getValue(), false);
				}
			}
		} else {
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
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbRecalFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.recallToDateRow.isVisible() && !this.cbRecalToDate.isDisabled()) {
			this.cbRecalToDate.getItems().clear();
			if (isValidComboValue(this.cbRecalFromDate,
					Labels.getLabel("label_PostponementDialog_TillDateFrom.value"))) {
				fillSchDates(this.cbRecalToDate, getFinScheduleData(),
						(Date) this.cbRecalFromDate.getSelectedItem().getValue(), true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when user changes date
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.cbFromDate.getSelectedIndex() > 0) {
			this.cbReCalType.setDisabled(false);
			this.cbRecalFromDate.getItems().clear();
			if (isValidComboValue(this.cbFromDate, Labels.getLabel("label_PostponementDialog_FromDate.value"))) {

				if (StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
					fillODSchDates(this.cbToDate, getFinScheduleData(),
							(Date) this.cbFromDate.getSelectedItem().getValue(), true);
				} else {
					fillSchDates(this.cbToDate, getFinScheduleData(),
							(Date) this.cbFromDate.getSelectedItem().getValue(), true);
				}
			}
		} else {
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
	 * 
	 * @param event
	 * 
	 */
	public void onChange$cbToDate(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.cbToDate.getSelectedIndex() > 0) {
			this.cbRecalFromDate.getItems().clear();
			this.cbRecalToDate.getItems().clear();
			if (!StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)
					&& isValidComboValue(this.cbToDate, Labels.getLabel("label_PostponementDialog_ToDate.value"))) {
				fillSchDates(this.cbRecalFromDate, getFinScheduleData(),
						(Date) this.cbToDate.getSelectedItem().getValue(), false);
				fillSchDates(this.cbRecalToDate, getFinScheduleData(),
						(Date) this.cbToDate.getSelectedItem().getValue(), false);
			}
		} else {
			if (!StringUtils.equals(FinServiceEvent.REAGING, moduleDefiner)) {
				this.cbReCalType.setSelectedIndex(0);
				this.cbReCalType.setDisabled(true);
				this.cbRecalFromDate.setSelectedIndex(0);
				this.cbRecalToDate.setSelectedIndex(0);
			}
			this.recallFromDateRow.setVisible(false);
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

	public void setPostponementService(PostponementService postponementService) {
		this.postponementService = postponementService;
	}

}
