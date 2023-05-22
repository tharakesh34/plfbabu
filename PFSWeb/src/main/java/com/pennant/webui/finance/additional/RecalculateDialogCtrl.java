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
 * * FileName : WIRecalculateDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.SanctionBasedSchedule;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ManualScheduleDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class RecalculateDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -6125624350998749280L;
	private static final Logger logger = LogManager.getLogger(RecalculateDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RecalculateDialog;
	protected Combobox cbTillDate;
	protected Combobox cbEventFromDate;
	protected Combobox cbReCalType;
	protected Row row_recalType;
	protected Row tillDateRow;
	protected Checkbox pftIntact;
	protected Label label_RecalculateDialog_TillDate;
	protected Row numOfTermsRow;
	protected Intbox adjTerms;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	protected Tab recalculateDetailsTab;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;

	private transient boolean validationOn;
	private transient String moduleDefiner;

	private transient RecalculateService recalService;
	private boolean appDateValidationReq = false;
	private ManualScheduleDialogCtrl manualScheduleDialogCtrl;
	private String roleCode = "";

	/**
	 * default constructor.<br>
	 */
	public RecalculateDialogCtrl() {
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
	public void onCreate$window_RecalculateDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RecalculateDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq = (boolean) arguments.get("appDateValidationReq");
			}

			if (arguments.containsKey("financeScheduleDetail")) {
				this.setFinanceScheduleDetail((FinanceScheduleDetail) arguments.get("financeScheduleDetail"));
				setFinanceScheduleDetail(this.financeScheduleDetail);
			} else {
				setFinanceScheduleDetail(null);
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setScheduleDetailDialogCtrl(null);
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			}

			boolean applySanctionCheck = SanctionBasedSchedule.isApplySanctionBasedSchedule(getFinScheduleData());
			getFinScheduleData().getFinanceMain().setApplySanctionCheck(applySanctionCheck);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
			this.window_RecalculateDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RecalculateDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.adjTerms.setMaxlength(PennantConstants.NUMBER_OF_TERMS_LENGTH);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);

		FinanceMain fm = getFinScheduleData().getFinanceMain();

		// Manual Schedule
		if (fm.isManualSchedule()) {
			appendManualSchdeuleTab(true);
			this.recalculateDetailsTab.setVisible(false);
			this.window_RecalculateDialog.setWidth("80%");
			this.window_RecalculateDialog.setHeight("60%");
		}

		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RecalculateDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		if (aFinSchData.getFinanceMain() != null) {

			String excldValues = ",CURPRD,ADJMDT,ADDTERM,STEPPOS,ADJTERMS,";
			if (StringUtils.equals(moduleDefiner, FinServiceEvent.RMVTERM)) {

				if (aFinSchData.getFinanceMain().isApplySanctionCheck()) {
					excldValues = ",CURPRD,TILLDATE,TILLMDT,ADDTERM,ADDRECAL,STEPPOS,ADJTERMS,";
				} else {
					excldValues = ",CURPRD,ADJMDT,TILLDATE,ADDTERM,ADDRECAL,STEPPOS,ADJTERMS,";
				}
			} else if (StringUtils.equals(moduleDefiner, FinServiceEvent.ADDTERM)) {

				if (aFinSchData.getFinanceMain().isApplySanctionCheck()) {
					excldValues = ",CURPRD,ADDTERM,STEPPOS,ADJTERMS,";
					fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_ADJMDT,
							PennantStaticListUtil.getSchCalCodes(), excldValues);
				} else {
					fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_ADDRECAL,
							PennantStaticListUtil.getSchCalCodes(), excldValues);
				}

				this.row_recalType.setVisible(false);
				this.cbReCalType.setDisabled(true);
				this.window_RecalculateDialog.setTitle(Labels.getLabel("window_AddTermRecalculateDialog.title"));
			} else if (aFinSchData.getFinanceMain().isManualSchedule()) {

				fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_TILLMDT,
						PennantStaticListUtil.getSchCalCodes(), excldValues);

				if (aFinSchData.getFinanceMain().getEventFromDate() == null) {
					aFinSchData.getFinanceMain().setEventFromDate(SysParamUtil.getAppDate());
				}
			} else {

				if (aFinSchData.getFinanceMain().isApplySanctionCheck()) {
					excldValues = ",CURPRD,ADJMDT,TILLDATE,TILLMDT,ADDTERM,ADJTERMS,STEPPOS,ADJTERMS,";
					fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_ADDRECAL,
							PennantStaticListUtil.getSchCalCodes(), excldValues);
				} else {
					fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
							PennantStaticListUtil.getSchCalCodes(), excldValues);
				}
			}
			if (aFinSchData.getFinanceMain().isApplySanctionCheck()) {
				fillSchFromDates(this.cbEventFromDate, aFinSchData, true);
				this.cbEventFromDate.setDisabled(true);
			} else {
				fillSchFromDates(this.cbEventFromDate, aFinSchData, false);
			}

			fillSchDates(this.cbTillDate, aFinSchData, aFinSchData.getFinanceMain().getFinStartDate());
		}
		if (aFinSchData.getFinanceType().isFinPftUnChanged()) {
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		} else {
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}
		changeRecalType(false);
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, FinScheduleData aFinSchData, boolean isDefaultToMDT) {
		logger.debug("Entering");

		FinanceMain fm = aFinSchData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = aFinSchData.getFinanceScheduleDetails();

		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		if (isDefaultToMDT) {
			FinanceScheduleDetail curSchd = schedules.get(schedules.size() - 1);
			comboitem = new Comboitem();
			comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
			comboitem.setValue(curSchd.getSchDate());
			dateCombobox.appendChild(comboitem);
			dateCombobox.setSelectedItem(comboitem);
			return;
		}

		boolean isMaturityDone = false;

		if (schedules != null) {
			Date curBussDate = SysParamUtil.getAppDate();
			for (FinanceScheduleDetail curSchd : schedules) {
				// Manual Schedule Recalculate
				if (fm.isManualSchedule()) {
					if (fm.getEventFromDate() != null) {
						curBussDate = fm.getEventFromDate();
					}
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtil.formatToLongDate(curBussDate) + " " + curSchd.getSpecifier());
					comboitem.setValue(curBussDate);
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					break;
				}

				// Not Allowed for Repayment
				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}

				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				// Not allow Before Current Business Date
				if (appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
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

				// Presentment Exists
				if (curSchd.getPresentmentId() > 0) {
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}

				// Dont add Zero Payment terms after Actual maturity
				if (isMaturityDone) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
				if (getFinanceScheduleDetail() != null
						&& curSchd.getSchDate().equals(getFinanceScheduleDetail().getSchDate())) {
					dateCombobox.setSelectedItem(comboitem);
				}

				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0) {
					isMaturityDone = true;
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
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		String module = "";
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		try {
			if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_RecalculateDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				finMain.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
				finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbEventFromDate, Labels.getLabel("label_RecalculateDialog_FromDate.value"))) {
				finMain.setEventFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
				finServiceInstruction.setFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.tillDateRow.isVisible()) {
			try {
				if (this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_RecalculateDialog_TillDate.value") }));
				}
				if (((Date) this.cbTillDate.getSelectedItem().getValue()).compareTo(finMain.getFinStartDate()) < 0
						|| ((Date) this.cbTillDate.getSelectedItem().getValue())
								.compareTo(finMain.getMaturityDate()) > 0) {
					throw new WrongValueException(this.cbTillDate,
							Labels.getLabel("DATE_ALLOWED_RANGE",
									new String[] { Labels.getLabel("label_RecalculateDialog_TillDate.value"),
											DateUtil.formatToLongDate(finMain.getFinStartDate()),
											DateUtil.formatToLongDate(finMain.getMaturityDate()) }));
				}
				// if schdpftBal greater than zero throw validation
				if (this.cbTillDate.getSelectedItem().getAttribute("pftBal") != null) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("Label_finSchdTillDate"));
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}

		}

		finMain.setAdjTerms(0);
		if (this.numOfTermsRow.isVisible()) {
			try {
				if (this.adjTerms.intValue() <= 0) {
					throw new WrongValueException(this.adjTerms, Labels.getLabel("AMOUNT_NOT_NEGATIVE",
							new String[] { Labels.getLabel("label_RecalculateDialog_Terms.value") }));
				}
				finMain.setAdjTerms(this.adjTerms.intValue());
				finServiceInstruction.setTerms(this.adjTerms.intValue());

			} catch (WrongValueException we) {
				wve.add(we);
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

		finServiceInstruction.setFinID(finMain.getFinID());
		finServiceInstruction.setFinReference(finMain.getFinReference());
		if (StringUtils.isNotBlank(moduleDefiner)) {
			finServiceInstruction.setFinEvent(moduleDefiner);
		} else {
			finServiceInstruction.setFinEvent(FinServiceEvent.RECALCULATE);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		// Manual Schedule Changes
		if (finMain.isManualSchedule()) {

			ManualScheduleHeader scheduleHeader = getFinScheduleData().getManualScheduleHeader();
			List<ManualScheduleDetail> details = null;
			if (scheduleHeader != null) {
				details = scheduleHeader.getManualSchedules();
			}

			int noOfInstall = 0;
			if (manualScheduleDialogCtrl != null) {
				noOfInstall = manualScheduleDialogCtrl.getNoOfInstallments();
			}

			if (CollectionUtils.isEmpty(details) || !scheduleHeader.isValidSchdUpload()) {
				MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				return;
			}
			// Validate the no of installment should match with upload repayment schedule.
			if (StringUtils.equals(moduleDefiner, FinServiceEvent.RECALCULATE) && noOfInstall != details.size()) {
				MessageUtil.showError(Labels.getLabel("NOOFINSTL_ROWS"));
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				return;
			}
			if (scheduleHeader.getTotPrincipleAmt().compareTo(scheduleHeader.getCurPOSAmt()) != 0) {
				MessageUtil.showError(Labels.getLabel("MANUAL_FINAMT_RECAL"));
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				return;
			}
			if (!(scheduleHeader.getNumberOfTerms() == details.size())) {
				MessageUtil.showError(Labels.getLabel("NOOFINSTL_RECALCULATE"));
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				return;
			}

			if (getFinScheduleData().getFinanceMain().isApplySanctionCheck()) {
				fillSchFromDates(this.cbEventFromDate, getFinScheduleData(), true);
				this.cbEventFromDate.setDisabled(true);
			} else {
				fillSchFromDates(this.cbEventFromDate, getFinScheduleData(), false);
			}
		}

		// Calculate Remaining Total Profit From Recalculation Event Date
		BigDecimal adjustedPft = finMain.getTotalProfit();
		// Total Outstanding Principal & profit calculation for Equal Installment amount in Profit Intact process
		BigDecimal schPriDue = BigDecimal.ZERO;
		BigDecimal schPftDue = BigDecimal.ZERO;
		int resetTerms = 0;
		for (int i = 0; i < getFinScheduleData().getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			if (curSchd.getSchDate().compareTo(finMain.getEventFromDate()) >= 0) {
				schPftDue = schPftDue.add(curSchd.getProfitSchd());
				schPriDue = schPriDue.add(curSchd.getPrincipalSchd());
				resetTerms = resetTerms + 1;
			}
		}

		finMain.setPftIntact(this.pftIntact.isChecked());
		finServiceInstruction.setPftIntact(this.pftIntact.isChecked());

		if (this.tillDateRow.isVisible() && this.cbTillDate.getSelectedIndex() > 0) {

			finMain.setRecalFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
			finMain.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());

			finServiceInstruction.setRecalFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
			finServiceInstruction.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());

		} else {
			String recalType = finMain.getRecalType();

			if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)) {
				finMain.setRecalFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
				finMain.setRecalToDate(finMain.getMaturityDate());
				finServiceInstruction.setRecalFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalToDate(finMain.getMaturityDate());

			}

			if (CalculationConstants.RPYCHG_ADDRECAL.equals(recalType)) {
				finMain.setRecalFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
				finMain.setRecalToDate(finMain.getMaturityDate());
				finServiceInstruction.setRecalFromDate((Date) this.cbEventFromDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalToDate(finMain.getMaturityDate());
			}

			if (CalculationConstants.RPYCHG_ADJMDT.equals(recalType)) {
				finMain.setRecalFromDate(finMain.getMaturityDate());
				finMain.setRecalToDate(finMain.getMaturityDate());
				finServiceInstruction.setRecalFromDate(finMain.getMaturityDate());
				finServiceInstruction.setRecalToDate(finMain.getMaturityDate());

				if (finMain.isApplySanctionCheck() && StringUtils.equals(moduleDefiner, FinServiceEvent.ADDTERM)) {
					module = moduleDefiner;
				}

			}
			finMain.setEventToDate(finMain.getMaturityDate());
			finServiceInstruction.setToDate(finMain.getMaturityDate());
		}
		// Service details calling for Schedule calculation
		getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
		setFinScheduleData(recalService.getRecalculateSchdDetails(getFinScheduleData(), module));

		getFinScheduleData().getFinanceMain().resetRecalculationFields();

		/*
		 * Setting Desired Values for the Profit Intact option By Using Change Profit Method Reverse Calculate the total
		 * Profit Amount on Changing Rate Value
		 */
		if (this.pftIntact.isChecked()) {
			finMain.setDesiredProfit(finMain.getTotalGrossPft());
			finMain.setAdjTerms(resetTerms);
			finMain.setSchPftDue(schPftDue);
			finMain.setSchPriDue(schPriDue);
			finMain.setEventFromDate(finMain.getFinStartDate());
			finMain.setEventToDate(finMain.getMaturityDate());
			setFinScheduleData(recalService.getRecalChangeProfit(getFinScheduleData(), adjustedPft));
			setFinScheduleData(recalService.getRecalculateSchdDetails(getFinScheduleData(), ""));
		}

		finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

		// Show Error Details in Schedule Maintenance
		if (getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		} else {
			getFinScheduleData().setSchduleGenerated(true);
			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
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

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddRecalculate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (getFinanceScheduleDetail() != null) {
			if (isDataChanged()) {
				doSave();
			} else {
				MessageUtil.showError("No Data has been changed.");
			}
		} else {
			doSave();
		}
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

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cbReCalType.clearErrorMessage();
		this.cbEventFromDate.clearErrorMessage();
		if (tillDateRow.isVisible()) {
			this.cbTillDate.clearErrorMessage();
		}
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		changeRecalType(true);
		logger.debug("Leaving" + event.toString());
	}

	private void changeRecalType(boolean isAction) {
		this.cbTillDate.clearErrorMessage();
		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			this.tillDateRow.setVisible(true);
			this.label_RecalculateDialog_TillDate.setValue(Labels.getLabel("label_RecalculateDialog_TillDate.value"));

			if (isAction) {
				if (isValidComboValue(this.cbEventFromDate,
						Labels.getLabel("label_RecalculateDialog_FromDate.value"))) {
					fillSchDates(this.cbTillDate, getFinScheduleData(),
							(Date) this.cbEventFromDate.getSelectedItem().getValue());
				}
			}
		} else {
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
		}

		if (getFinScheduleData().getFinanceMain().isApplySanctionCheck()) {
			this.tillDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(true);
		} else if (this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
			this.tillDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(true);
		} else {
			this.numOfTermsRow.setVisible(false);
		}

	}

	public void onChange$cbEventFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		this.cbTillDate.clearErrorMessage();
		if (this.cbEventFromDate.getSelectedIndex() > 0) {
			fillSchDates(cbTillDate, getFinScheduleData(), (Date) this.cbEventFromDate.getSelectedItem().getValue());
			this.cbTillDate.setSelectedIndex(0);
			if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
				this.tillDateRow.setVisible(true);
			} else {
				this.tillDateRow.setVisible(false);
			}
		} else {
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/** To fill schedule dates */
	public void fillSchDates(Combobox dateCombobox, FinScheduleData aFinSchData, Date fillAfter) {
		logger.debug("Entering");

		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (aFinSchData.getFinanceScheduleDetails() != null) {
			List<FinanceScheduleDetail> financeScheduleDetails = aFinSchData.getFinanceScheduleDetails();

			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Not Allowed for Repayment
				if (!(curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
					continue;
				}

				// Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// When Add terms done with zero installments and trying with TILLDATE option
				// Date after closing balance is Zero and not a maturity date should not allow
				if (curSchd.getSchDate().compareTo(aFinSchData.getFinanceMain().getMaturityDate()) != 0
						&& curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				if (fillAfter.compareTo(curSchd.getSchDate()) < 0) {

					comboitem = new Comboitem();
					comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
					comboitem.setValue(curSchd.getSchDate());
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

	/**
	 * append Manual Schedule Tab
	 */
	private void appendManualSchdeuleTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);

		Tab tab = new Tab(Labels.getLabel("label_RecalculateDialog_UploadScheduleDetails"));
		tab.setId(getTabID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
		tab.setVisible(true);
		tab.setSelected(true);
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		final HashMap<String, Object> map = new HashMap<>();

		map.put("moduleDefiner", moduleDefiner);
		map.put("finScheduleData", getFinScheduleData());
		map.put("parentCtrl", this);
		map.put("roleCode", roleCode);

		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ManualScheduleDialog.zul",
				getTabpanel(getTabpanelID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE)), map);

		logger.debug(Literal.LEAVING);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
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

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public void setRecalService(RecalculateService recalService) {
		this.recalService = recalService;
	}

	public void setManualScheduleDialogCtrl(ManualScheduleDialogCtrl manualScheduleDialogCtrl) {
		this.manualScheduleDialogCtrl = manualScheduleDialogCtrl;
	}

}
