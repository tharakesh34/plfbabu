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
 * * FileName : ScheduleRateReportHeaderCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-05-2019 * *
 * Modified Date : 13-05-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-05-2019 Pennant 0.1 * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.reports;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ScheduleRateReport;
import com.pennant.backend.model.finance.ScheduleRateReportHeader;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.web.util.ComponentUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Reports/ScheduleRateReportHeaderDialogCtrl.zul file.
 */
public class ScheduleRateReportHeaderCtrl extends GFCBaseCtrl<ScheduleRateReportHeader> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = LogManager.getLogger(ScheduleRateReportHeaderCtrl.class);

	protected Window window_ScheduleRateReportHeaderDialogCtrl;
	protected ExtendedCombobox finReference;
	protected Datebox startDate;
	protected Datebox endDate;
	protected Radio pdf;
	protected Radio excel;

	private ScheduleRateReportHeader scheduleRateReportHeader = new ScheduleRateReportHeader();
	private transient FinanceDetailService financeDetailService;

	public ScheduleRateReportHeaderCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 */
	public void onCreate$window_ScheduleRateReportHeaderDialogCtrl(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ScheduleRateReportHeaderDialogCtrl);

		try {
			doSetFieldProperties();
			this.window_ScheduleRateReportHeaderDialogCtrl.doModal();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
			closeDialog();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		// Finance Reference
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setMandatoryStyle(true);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(140);

		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.endDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		// Close the current window
		this.window_ScheduleRateReportHeaderDialogCtrl.onClose();

		// Close the current menu item
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnGenereate(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		doWriteComponentsToBean(this.scheduleRateReportHeader);

		long finID = ComponentUtil.getFinID(this.finReference);

		ScheduleRateReportHeader header = prepareSchdRateReportData(this.scheduleRateReportHeader, finID,
				this.startDate.getValue(), this.endDate.getValue());

		List<Object> list = new ArrayList<Object>();
		list.add(header.getRateReports());

		try {
			boolean isExcel = this.excel.isChecked();
			String userName = getUserWorkspace().getLoggedInUser().getFullName();
			if (isExcel) {
				ReportsUtil.downloadExcel("FINENQ_ScheduleRateReport", header, list, userName);
				return;
			}

			ReportsUtil.showPDF("FINENQ_ScheduleRateReport", header, list, userName);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		this.window_ScheduleRateReportHeaderDialogCtrl.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
				this.startDate.setValue(details.getFinStartDate());
				this.endDate.setValue(SysParamUtil.getAppDate());
			} else {
				this.finReference.setValue("");
				this.startDate.setValue(null);
				this.endDate.setValue(null);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param statementOfAccount
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doWriteComponentsToBean(ScheduleRateReportHeader scheduleRateReportHeader)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// FinReference
		try {
			this.finReference.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Start Date
		try {
			this.startDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// End Date
		try {
			this.endDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private ScheduleRateReportHeader prepareSchdRateReportData(ScheduleRateReportHeader rrh, long finID, Date startDate,
			Date endDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = this.financeDetailService.getFinanceMainForRateReport(finID, "_AView");

		// Header Details
		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());
		rrh.setCif(fm.getLovDescCustCIF());
		rrh.setCustName(fm.getLovDescCustShrtName());
		rrh.setFinReference(fm.getFinReference());
		rrh.setDisbursedAmt(PennantApplicationUtil.amountFormate(fm.getFinCurrAssetValue(), formatter));
		rrh.setInstDaysBasis(PennantStaticListUtil.getlabelDesc(fm.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis()));

		// Schedule Details
		List<FinanceScheduleDetail> scheduleDetails = this.financeDetailService.getFinSchdDetailsForRateReport(finID);

		scheduleDetails = sortSchdDetails(scheduleDetails);

		List<ScheduleRateReport> rateReportsList = new ArrayList<>();
		ScheduleRateReport rateReport = null;
		int size = scheduleDetails.size();
		FinanceScheduleDetail prvSchd = scheduleDetails.get(0);
		boolean endDateCompleted = false;

		for (int i = 1; i < size; i++) {

			FinanceScheduleDetail curSchd = scheduleDetails.get(i);
			prvSchd = scheduleDetails.get(i - 1);
			if (DateUtil.compare(curSchd.getSchDate(), startDate) < 0) {
				continue;
			}
			if ((DateUtil.compare(curSchd.getSchDate(), endDate) > 0 && endDateCompleted)) {
				break;
			}

			rateReport = new ScheduleRateReport();
			rateReport.setInstNo(String.valueOf(curSchd.getInstNumber()));
			rateReport.setBalForPftCal(PennantApplicationUtil.amountFormate(curSchd.getBalanceForPftCal(), formatter));

			Date fromDate = null;
			Date toDate = null;

			boolean startDateisFrqDate = false;
			if (DateUtil.compare(prvSchd.getSchDate(), startDate) < 0) {
				fromDate = startDate;
			} else {
				fromDate = prvSchd.getSchDate();
				startDateisFrqDate = true;
			}

			boolean endDateisFrqDate = false;
			if (DateUtil.compare(curSchd.getSchDate(), endDate) > 0) {
				toDate = endDate;
				endDateCompleted = true;
			} else {
				toDate = curSchd.getSchDate();
				endDateisFrqDate = true;
				if (DateUtil.compare(curSchd.getSchDate(), endDate) == 0) {
					endDateCompleted = true;
				}
			}

			rateReport.setStartDate(DateUtil.formatToLongDate(fromDate));
			rateReport.setEndDate(DateUtil.formatToLongDate(toDate));
			rateReport.setDays(String.valueOf(DateUtil.getDaysBetween(fromDate, toDate)));
			rateReport.setRate(PennantApplicationUtil.formatRate(prvSchd.getCalculatedRate().doubleValue(), 9));

			if (startDateisFrqDate && endDateisFrqDate) {
				rateReport.setCalcPft(PennantApplicationUtil.amountFormate(curSchd.getProfitCalc(), formatter));
			} else {
				BigDecimal calInt = CalculationUtil.calInterest(fromDate, toDate, curSchd.getBalanceForPftCal(),
						prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

				BigDecimal calIntRounded = BigDecimal.ZERO;
				if (calInt.compareTo(BigDecimal.ZERO) > 0) {
					calIntRounded = CalculationUtil.roundAmount(calInt, fm.getCalRoundingMode(),
							fm.getRoundingTarget());
				}
				rateReport.setCalcPft(PennantApplicationUtil.amountFormate(calIntRounded, formatter));
			}
			rateReportsList.add(rateReport);
		}

		rrh.setRateReports(rateReportsList);

		logger.debug(Literal.LEAVING);
		return rrh;
	}

	public static List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint("");
		this.startDate.setConstraint("");
		this.endDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finReference.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.endDate.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doRemoveValidation();

		// Finance Type
		this.finReference.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SRReportDialog_FinReference.value"), null, true, true));

		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		// Start Date
		if (!this.startDate.isDisabled()) {
			this.startDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_SRReportDialog_StartDate.value"), true));
		}
		// end Date
		if (!this.endDate.isDisabled()) {
			try {
				this.startDate.getValue();
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SRReportDialog_EndDate.value"),
						true, this.startDate.getValue(), appEndDate, false));
			} catch (WrongValueException we) {
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SRReportDialog_EndDate.value"),
						true, true, null, false));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}