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
 * * FileName : WIRateChangeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
 * Modified Date : 05-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SanctionBasedSchedule;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.BaseRateCodeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class RateChangeDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -4578996988245614938L;
	private static final Logger logger = LogManager.getLogger(RateChangeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RateChangeDialog;
	protected Decimalbox rateChange;
	protected Combobox cbRateChangeFromDate;
	protected Combobox cbRateChangeToDate;
	protected Combobox profitDaysBasis;
	protected RateBox rate;
	protected Combobox cbReCalType;
	protected Combobox cbRecalFromDate;
	protected Combobox cbRecalToDate;
	protected Row baseRateRow;
	protected Row rateAmountRow;
	protected Row tillDateRow;
	protected Row fromDateRow;
	protected Row reviewDatesRow;
	protected Radio reviewDates;
	protected Radio anyDate;
	protected Row profitDayBasisRow;
	protected Row recalTypeRow;
	protected Label label_RateChangeDialog_FromDate;
	protected Label label_RateChangeDialog_ToDate;
	protected Label label_RateChangeDialog_Rate;
	protected Row reviewDateFromDateRow;
	protected Row reviewDateToDateRow;
	protected Row anyDateFromDateRow;
	protected Row anyDateToDateRow;

	protected Row baseRateRvwFrqRow;
	protected FrequencyBox bRRepayRvwFrq;

	protected Datebox anyDateRateChangeFromDate;
	protected Datebox anyDateRateChangeToDate;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private transient ScheduleDetailDialogCtrl financeMainDialogCtrl;

	Calendar calender = Calendar.getInstance();
	private transient boolean validationOn;
	private transient RateChangeService rateChangeService;
	private transient BaseRateCodeService baseRateCodeService;
	private boolean appDateValidationReq = false;
	private String moduleDefiner = "";

	/**
	 * default constructor.<br>
	 */
	public RateChangeDialogCtrl() {
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
	public void onCreate$window_RateChangeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RateChangeDialog);

		try {

			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeScheduleDetail")) {
				this.setFinanceScheduleDetail((FinanceScheduleDetail) arguments.get("financeScheduleDetail"));
				setFinanceScheduleDetail(this.financeScheduleDetail);
			} else {
				setFinanceScheduleDetail(null);
			}

			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq = (boolean) arguments.get("appDateValidationReq");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
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
			this.window_RateChangeDialog.onClose();
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

			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RateChangeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.anyDateRateChangeFromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.anyDateRateChangeToDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.rateChange.setScale(9);
		this.rate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.rate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		this.bRRepayRvwFrq.setDisabled(true);
		this.bRRepayRvwFrq.getFrqDayCombobox().setWidth("35px");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.rateChange.setReadonly(true);
		this.profitDaysBasis.setDisabled(true);
		this.cbRateChangeFromDate.setReadonly(true);
		this.cbRateChangeToDate.setReadonly(true);
		this.cbReCalType.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		FinanceType aFinType = aFinSchData.getFinanceType();

		this.reviewDatesRow.setVisible(true);
		// FIXME PV @03/JUN/2018 ADJTERMS Testing
		// StringBuilder excludeFileds=new StringBuilder(",ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
		StringBuilder excludeFileds = new StringBuilder(",ADDTERM,ADDLAST,ADDRECAL,");
		if (aFinType.isRateChgAnyDay()) {
			this.anyDate.setDisabled(false);
			this.anyDate.setVisible(true);
			this.anyDate.setSelected(true);
			this.anyDateFromDateRow.setVisible(true);
			this.anyDateToDateRow.setVisible(true);
			this.reviewDateFromDateRow.setVisible(false);
			this.reviewDateToDateRow.setVisible(false);
			this.anyDateRateChangeFromDate.setVisible(true);
			this.anyDateRateChangeFromDate.setValue(SysParamUtil.getAppDate());
			this.anyDateRateChangeToDate.setVisible(true);
			this.anyDateRateChangeToDate.setValue(aFinanceMain.getMaturityDate());
			excludeFileds.append("CURPRD,");
		} else {
			this.anyDate.setVisible(false);
			this.reviewDates.setSelected(true);
			this.anyDate.setDisabled(true);
			this.anyDateFromDateRow.setVisible(false);
			this.anyDateToDateRow.setVisible(false);
			this.reviewDateFromDateRow.setVisible(true);
			this.reviewDateToDateRow.setVisible(true);
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			this.profitDayBasisRow.setVisible(false);
			this.label_RateChangeDialog_FromDate.setValue(Labels.getLabel("label_RateChangeDialog_ODFromDate.value"));
			this.label_RateChangeDialog_ToDate.setValue(Labels.getLabel("label_RateChangeDialog_ODToDate.value"));
			this.label_RateChangeDialog_Rate.setValue(Labels.getLabel("label_RateChangeDialog_ODRate.value"));
			this.recalTypeRow.setVisible(false);
		}
		fillComboBox(profitDaysBasis, aFinanceMain.getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(),
				"");

		this.baseRateRow.setVisible(true);
		this.rate.setEffectiveRateVisible(true);
		this.rateChange.setVisible(true);
		this.rateAmountRow.setVisible(true);

		this.cbRateChangeFromDate.setVisible(true);
		fillSchFromDates(this.cbRateChangeFromDate, aFinSchData.getFinanceScheduleDetails());
		this.cbRateChangeToDate.setVisible(true);
		if (getFinanceScheduleDetail() != null) {
			fillSchToDates(this.cbRateChangeToDate, aFinSchData.getFinanceScheduleDetails(),
					getFinanceScheduleDetail().getSchDate());
		} else {
			fillSchToDates(this.cbRateChangeToDate, aFinSchData.getFinanceScheduleDetails(),
					aFinanceMain.getFinStartDate());
		}
		setEffectiveRate();

		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		if (getFinanceScheduleDetail() != null) {
			if (getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE)
					|| getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)) {
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					this.rate.setBaseValue(getFinanceScheduleDetail().getBaseRate());
					this.rate.setSpecialValue(getFinanceScheduleDetail().getSplRate());
					this.rate.setMarginValue(getFinanceScheduleDetail().getMrgRate());
					RateDetail rateDetail = RateUtil.rates(getFinanceScheduleDetail().getBaseRate(),
							aFinanceMain.getFinCcy(), getFinanceScheduleDetail().getSplRate(),
							getFinanceScheduleDetail().getMrgRate(), aFinanceMain.getGrcMinRate(),
							aFinanceMain.getGrcMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				} else {
					this.rateChange.setValue(getFinanceScheduleDetail().getActRate());
					this.rate.setEffRateValue(getFinanceScheduleDetail().getActRate());
				}
				if (profitDayBasisRow.isVisible()) {
					fillComboBox(profitDaysBasis, aFinanceMain.getGrcProfitDaysBasis(),
							PennantStaticListUtil.getProfitDaysBasis(), "");
				}
			} else if ((getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_REPAY))
					|| (getFinanceScheduleDetail().getSpecifier()
							.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					this.rate.setBaseValue(getFinanceScheduleDetail().getBaseRate());
					this.rate.setSpecialValue(getFinanceScheduleDetail().getSplRate());
					this.rate.setMarginValue(getFinanceScheduleDetail().getMrgRate());
					RateDetail rateDetail = RateUtil.rates(getFinanceScheduleDetail().getBaseRate(),
							aFinanceMain.getFinCcy(), getFinanceScheduleDetail().getSplRate(),
							getFinanceScheduleDetail().getMrgRate(), aFinanceMain.getRpyMinRate(),
							aFinanceMain.getRpyMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				} else {
					this.rateChange.setValue(getFinanceScheduleDetail().getActRate());
					this.rate.setEffRateValue(getFinanceScheduleDetail().getActRate());
				}
				if (profitDayBasisRow.isVisible()) {
					fillComboBox(profitDaysBasis, aFinanceMain.getProfitDaysBasis(),
							PennantStaticListUtil.getProfitDaysBasis(), "");
				}
			}
		}

		// Stepping POS Exclude for Recal Type
		if (!aFinanceMain.isStepFinance()
				|| !StringUtils.equals(getFinScheduleData().getFinanceMain().getStepType(),
						FinanceConstants.STEPTYPE_PRIBAL)
				|| !(StringUtils.equals(aFinanceMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
						|| StringUtils.equals(aFinanceMain.getScheduleMethod(),
								CalculationConstants.SCHMTHD_PRI_PFT))) {
			excludeFileds.append("STEPPOS,");
		}

		excludeFileds = SanctionBasedSchedule.getSanctionRecalExcludeFlds(aFinSchData, excludeFileds);

		// TillDate is being Excluded if there is no ratereview in Grace and Payment
		if (!aFinanceMain.isAllowGrcPftRvw() && !aFinanceMain.isAllowRepayRvw()) {
			excludeFileds.append("TILLDATE,");
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
					PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		}

		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			// Check if schedule header is null or not and set the recal type fields.

			String schmethod = StringUtils.trimToEmpty(aFinSchData.getFinanceMain().getScheduleMethod());

			// PV: 24AUG19. Kotak Sanction Based Schedule only allow PRI_PFT and Recal Type as ADJMDT
			if (!aFinSchData.getFinanceMain().isSanBsdSchdle()
					&& schmethod.equals(CalculationConstants.SCHMTHD_PRI_PFT)) {
				excludeFileds.append("ADJMDT,");
			}
			if (getFinScheduleData().getFinanceMain().getNumberOfTerms() == 1) {
				excludeFileds.append("TILLMDT,");
				fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
						PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
			} else {
				fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
						PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
			}

			changeRecalType();

		} else {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
					PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		}

		if (aFinType.isRateChgAnyDay()) {
			processStepLoans(aFinanceMain, SysParamUtil.getAppDate());
		}
		logger.debug("Leaving");
	}

	public void onChange$anyDateRateChangeFromDate(Event event) {
		logger.debug("Entering");

		Date alwdBackDate = DateUtil.addDays(SysParamUtil.getAppDate(),
				-SysParamUtil.getValueAsInt(SMTParameterConstants.RATE_CHANGE_FROM_DATE_BACK_DAYS));

		if (this.anyDateRateChangeFromDate != null
				&& DateUtil.compare(this.anyDateRateChangeFromDate.getValue(), alwdBackDate) < 0) {

			throw new WrongValueException(this.anyDateRateChangeFromDate,
					Labels.getLabel("NUMBER_MINVALUE",
							new String[] { Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
									alwdBackDate.toString() }));

		}

		this.cbRecalFromDate.getItems().clear();
		if (this.cbReCalType.getSelectedIndex() > 0) {
			if ((this.cbReCalType.getSelectedItem().getValue().toString()).equals(CalculationConstants.RPYCHG_TILLDATE)
					|| (this.cbReCalType.getSelectedItem().getValue().toString())
							.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				if (this.anyDateRateChangeFromDate.getValue() != null) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							this.anyDateRateChangeFromDate.getValue(), false);
				}
			} else {
				if (this.anyDateRateChangeFromDate.getValue() != null) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							this.anyDateRateChangeFromDate.getValue(), false);
				} else {
					this.cbRecalFromDate.setSelectedIndex(0);
				}
			}
		}

		if (this.anyDateRateChangeFromDate.getValue() != null) {
			processStepLoans(getFinScheduleData().getFinanceMain(), this.anyDateRateChangeFromDate.getValue());
		}

		logger.debug("Leaving");
	}

	public void onChange$anyDateRateChangeToDate(Event event) {
		logger.debug("Entering");
		if (this.anyDateRateChangeToDate != null && DateUtil.compare(this.anyDateRateChangeToDate.getValue(),
				this.anyDateRateChangeFromDate.getValue()) < 0) {
			throw new WrongValueException(this.anyDateRateChangeToDate,
					Labels.getLabel("NUMBER_MINVALUE",
							new String[] { Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
									Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value") }));
		}
		if (this.anyDateRateChangeToDate != null && DateUtil.compare(this.anyDateRateChangeToDate.getValue(),
				getFinScheduleData().getFinanceMain().getMaturityDate()) > 0) {
			throw new WrongValueException(this.anyDateRateChangeToDate,
					Labels.getLabel("NUMBER_MAXVALUE",
							new String[] { Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
									DateUtil.format(getFinScheduleData().getFinanceMain().getMaturityDate(),
											PennantConstants.DBDateFormat) }));
		}

		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbRateChangeFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		boolean includedPrvSchTerm = false;
		boolean isOdSelected = true;
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		boolean allowBackDatedRateChange = SysParamUtil
				.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE);
		Date curBussDate = SysParamUtil.getAppDate();
		Date allowBackDate = null;

		if (allowBackDatedRateChange) {
			appDateValidationReq = false;
			for (FinanceScheduleDetail scheduleDetail : financeScheduleDetails) {
				if (DateUtil.compare(scheduleDetail.getSchDate(), curBussDate) < 0) {
					if (scheduleDetail.isRvwOnSchDate()) {
						allowBackDate = scheduleDetail.getSchDate();
					}
				}
			}
		}

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Review from Date should be Greater than the appdate
				if (appDateValidationReq && DateUtil.compare(curSchd.getSchDate(), curBussDate) <= 0) {
					includedPrvSchTerm = false;
					continue;
				}

				// Not Review Date
				if (!curSchd.isRvwOnSchDate() && !allowBackDatedRateChange) {
					if (getFinScheduleData().getFinanceMain().isAllowGrcPeriod()
							&& curSchd.getSchDate()
									.compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) == 0
							&& curSchd.getSchDate()
									.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) != 0) {
						// Proceed Further
					} else {
						continue;
					}
				}

				if (allowBackDatedRateChange) {
					if (DateUtil.compare(curSchd.getSchDate(), allowBackDate) <= 0) {
						continue;
					}
				}

				// Schedule Date Passed last review date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayRvwDate())) {
					continue;
				}

				// Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}

				// Schedule Date Passed last capitalize date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayCpzDate())) {
					continue;
				}

				// Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}

				// Excluding Present generated file Schedule Terms
				if (curSchd.getPresentmentId() > 0) {
					this.cbRateChangeFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					includedPrvSchTerm = false;
					continue;
				}

				// Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
						|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.cbRateChangeFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					includedPrvSchTerm = false;
					continue;
				}

				if (i == financeScheduleDetails.size() - 1) {
					continue;
				}

				if ((i - 1 > 0) && !includedPrvSchTerm) {

					FinanceScheduleDetail prvSchd = financeScheduleDetails.get(i - 1);

					comboitem = new Comboitem();
					comboitem.setLabel(
							DateUtil.formatToLongDate(prvSchd.getSchDate()) + " " + prvSchd.getSpecifier());
					comboitem.setValue(prvSchd.getSchDate());
					comboitem.setAttribute("fromSpecifier", prvSchd.getSpecifier());
					dateCombobox.appendChild(comboitem);

					includedPrvSchTerm = true;
				}

				if (i == 0) {
					includedPrvSchTerm = true;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());

				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if (curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
						includedPrvSchTerm = true;
					}
				} else {
					dateCombobox.appendChild(comboitem);
					includedPrvSchTerm = true;
				}
				// in over draft review from date to be selected the date which is greater than the app date
				if (isOverdraft && isOdSelected) {
					dateCombobox.setSelectedItem(comboitem);
					isOdSelected = false;
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in to date combo */
	public void fillSchToDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails,
			Date fillAfter) {
		logger.debug("Entering");
		this.cbRateChangeToDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		Date maturityDate = getFinScheduleData().getFinanceMain().getMaturityDate();

		boolean isOdSelected = true;
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		boolean allowBackDatedRateChange = SysParamUtil
				.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE);
		Date curBussDate = SysParamUtil.getAppDate();
		Date allowBackDate = null;

		Date lastSchdDate = null;

		if (allowBackDatedRateChange) {
			for (FinanceScheduleDetail scheduleDetail : financeScheduleDetails) {
				if (DateUtil.compare(scheduleDetail.getSchDate(), maturityDate) <= 0) {
					lastSchdDate = scheduleDetail.getSchDate();
				}
			}
		}

		if (allowBackDatedRateChange) {
			appDateValidationReq = false;
			for (FinanceScheduleDetail scheduleDetail : financeScheduleDetails) {
				if (DateUtil.compare(scheduleDetail.getSchDate(), curBussDate) < 0) {
					if (scheduleDetail.isRvwOnSchDate()) {
						allowBackDate = scheduleDetail.getSchDate();
					}
				}
			}
		}

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// in Overdraft the Review from Date should be Greater than the appdate
				if (isOverdraft && DateUtil.compare(curSchd.getSchDate(), SysParamUtil.getAppDate()) < 0
						&& !allowBackDatedRateChange) {
					continue;
				}

				if (allowBackDatedRateChange) {
					if (DateUtil.compare(curSchd.getSchDate(), allowBackDate) <= 0) {
						continue;
					}
				}

				// Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Not Review Date
				if (!curSchd.isRvwOnSchDate() && !allowBackDatedRateChange) {
					if (curSchd.getSchDate().compareTo(maturityDate) != 0
							|| this.cbRateChangeFromDate.getSelectedIndex() <= 0) {
						continue;
					}
				}

				// Schedule Date Passed last review date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayRvwDate())) {
					continue;
				}

				// Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}

				// Schedule Date Passed last capitalize date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayCpzDate())) {
					continue;
				}
				// if Recal Type is TillDate then the Maturity dateis not been allowed
				if (this.cbReCalType.getSelectedIndex() > 0 && StringUtils.equals(getComboboxValue(this.cbReCalType),
						CalculationConstants.RPYCHG_TILLDATE)) {
					if (i == financeScheduleDetails.size() - 1) {
						continue;
					}
				}

				// Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}

				if (allowBackDatedRateChange && (DateUtil.compare(curSchd.getSchDate(), lastSchdDate) != 0)) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("toSpecifier", curSchd.getSpecifier());

				if (getFinanceScheduleDetail() != null
						&& curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) >= 0) {
					dateCombobox.appendChild(comboitem);
				} else if (curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
					// in overdraft the next review to date need to be selected the date which is after the from date
					if (isOverdraft && isOdSelected) {
						dateCombobox.setSelectedItem(comboitem);
						isOdSelected = false;
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
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		FinServiceInstruction finServInst = new FinServiceInstruction();
		FinScheduleData schdData = getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceMain finMain = fm;
		doClearMessage();
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		boolean allowBackDatedRateChange = SysParamUtil
				.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE);

		try {
			finServInst.setBaseRate(StringUtils.trimToNull(this.rate.getBaseValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServInst.setSplRate(StringUtils.trimToNull(this.rate.getSpecialValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServInst.setMargin(this.rate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.rateChange.getValue() != null && !this.rateChange.isReadonly()) {
				if (this.rateChange.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(this.rateChange, Labels.getLabel("NUMBER_NOT_NEGATIVE",
							new String[] { Labels.getLabel("label_RateChangeDialog_Rate.value") }));
				}
				/*
				 * else if(this.rateChange.getValue().compareTo(finMain.getRpyMaxRate())>0){ throw new
				 * WrongValueException(this.rateChange,Labels.getLabel("NUMBER_MAXVALUE_EQ" ,new String[]{
				 * Labels.getLabel("label_RateChangeDialog_Rate.value"),finMain.getRpyMaxRate().toString()})); }else
				 * if(this.rateChange.getValue().compareTo(finMain.getRpyMinRate())<0){ throw new
				 * WrongValueException(this.rateChange, Labels.getLabel("NUMBER_MINVALUE_EQ", new
				 * String[]{Labels.getLabel("label_RateChangeDialog_Rate.value"),finMain.getRpyMinRate().toString()}));
				 * } for base
				 */
				finServInst.setActualRate(this.rateChange.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// IF Single Rate required based on Origination Selection please comment below try-catch block
		try {
			if ((this.rateAmountRow.isVisible() && this.rateChange.getValue() != null
					&& this.rateChange.getValue().compareTo(BigDecimal.ZERO) > 0)
					&& (StringUtils.isNotEmpty(this.rate.getBaseValue()))) {
				throw new WrongValueException(this.rateChange,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_RateChangeDialog_BaseRate.value"),
										Labels.getLabel("label_RateChangeDialog_Rate.value") }));
			}
			if ((this.rateAmountRow.isVisible() && this.rateChange.getValue() == null)
					&& (StringUtils.isEmpty(this.rate.getBaseValue()))) {
				throw new WrongValueException(this.rateChange,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_RateChangeDialog_BaseRate.value"),
										Labels.getLabel("label_RateChangeDialog_Rate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// BaseRate margin validation
		try {
			if (StringUtils.trimToNull(this.rate.getBaseValue()) == null && this.rate.getMarginValue() != null
					&& this.rate.getMarginValue().compareTo(BigDecimal.ZERO) != 0) {
				throw new WrongValueException(rate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_RateChangeDialog_MarginRate.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Last date
		Date lastPaidDate = fm.getFinStartDate();
		Date currBussDate = SysParamUtil.getAppDate();
		List<FinanceScheduleDetail> schdList = schdData.getFinanceScheduleDetails();
		for (int i = 1; i < schdList.size(); i++) {

			FinanceScheduleDetail curSchd = schdList.get(i);
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {
				lastPaidDate = curSchd.getSchDate();
			}

			if (allowBackDatedRateChange) {
				Date allowdBackDate = null;
				for (FinanceScheduleDetail scheduleDetail : schdList) {
					if (DateUtil.compare(scheduleDetail.getSchDate(), currBussDate) < 0) {
						if (scheduleDetail.isRvwOnSchDate()) {
							allowdBackDate = scheduleDetail.getSchDate();
						}
					}
				}
				if ((curSchd.getSchDate().compareTo(currBussDate) < 0)
						&& (DateUtil.compare(curSchd.getSchDate(), allowdBackDate) <= 0)) {
					if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						lastPaidDate = curSchd.getSchDate();
					}
				}
			} else {
				if (curSchd.getSchDate().compareTo(currBussDate) == 0) {
					lastPaidDate = currBussDate;
				} else if ((curSchd.getSchDate().compareTo(currBussDate) < 0)) {
					if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						lastPaidDate = curSchd.getSchDate();
					}
				}
			}

			// If presentment Exists not allowed to do Rate Change
			if (curSchd.getPresentmentId() > 0) {
				lastPaidDate = curSchd.getSchDate();
			}
		}

		if (!allowBackDatedRateChange) {
			// Month End Date or Last installment which is Greater should be considered
			Date mnthEndDate = DateUtil.getMonthEnd(DateUtil.addMonths(currBussDate, -1));
			if (mnthEndDate.compareTo(lastPaidDate) > 0) {
				lastPaidDate = DateUtil.addDays(mnthEndDate, 1);
			}
		}

		// Back Date Allowed Condition Check
		Date alwdBackDate = DateUtil.addDays(currBussDate,
				-SysParamUtil.getValueAsInt(SMTParameterConstants.RATE_CHANGE_FROM_DATE_BACK_DAYS));
		if (lastPaidDate.compareTo(alwdBackDate) < 0) {
			lastPaidDate = alwdBackDate;
		}
		Date backDate = null;
		for (FinanceScheduleDetail scheduleDetail : schdList) {
			if (DateUtil.compare(scheduleDetail.getSchDate(), currBussDate) < 0) {
				if (scheduleDetail.isRvwOnSchDate()) {
					backDate = scheduleDetail.getSchDate();
				}
			}
		}

		try {
			if (this.reviewDateFromDateRow.isVisible()) {
				if (isValidComboValue(this.cbRateChangeFromDate,
						Labels.getLabel("label_RateChangeDialog_FromDate.value"))) {
					finMain.setEventFromDate((Date) this.cbRateChangeFromDate.getSelectedItem().getValue());
					finServInst.setFromDate((Date) this.cbRateChangeFromDate.getSelectedItem().getValue());
				}
			} else {
				if (this.anyDateFromDateRow.isVisible() && this.anyDateRateChangeFromDate.getValue() == null) {
					throw new WrongValueException(this.anyDateRateChangeFromDate,
							Labels.getLabel("FIELD_NO_EMPTY", new String[] {
									Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value") }));
				} else if (this.anyDateFromDateRow.isVisible() && this.anyDateRateChangeFromDate.getValue() != null) {
					if (allowBackDatedRateChange & backDate != null) {
						if (this.anyDateRateChangeFromDate.getValue().compareTo(backDate) < 0) {
							throw new WrongValueException(this.anyDateRateChangeFromDate, Labels.getLabel(
									"DATE_ALLOWED_AFTER",
									new String[] {
											Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
											DateUtil.formatToLongDate(backDate) }));
						} else if (this.anyDateRateChangeFromDate.getValue().compareTo(fm.getMaturityDate()) > 0) {
							throw new WrongValueException(this.anyDateRateChangeFromDate, Labels.getLabel(
									"DATE_ALLOWED_BEFORE",
									new String[] {
											Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
											DateUtil.formatToLongDate(fm.getMaturityDate()) }));
						}
					} else {

						if (this.anyDateRateChangeFromDate.getValue().compareTo(lastPaidDate) < 0) {
							throw new WrongValueException(this.anyDateRateChangeFromDate, Labels.getLabel(
									"DATE_ALLOWED_AFTER",
									new String[] {
											Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
											DateUtil.formatToLongDate(lastPaidDate) }));
						} else if (this.anyDateRateChangeFromDate.getValue().compareTo(fm.getMaturityDate()) > 0) {
							throw new WrongValueException(this.anyDateRateChangeFromDate, Labels.getLabel(
									"DATE_ALLOWED_BEFORE",
									new String[] {
											Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value"),
											DateUtil.formatToLongDate(fm.getMaturityDate()) }));
						}
					}

				}
				finMain.setEventFromDate(this.anyDateRateChangeFromDate.getValue());
				finServInst.setFromDate(this.anyDateRateChangeFromDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.profitDayBasisRow.isVisible()) {
				isValidComboValue(this.profitDaysBasis,
						Labels.getLabel("label_RateChangeDialog_ProfitDaysBasis.value"));
				finServInst.setPftDaysBasis(getComboboxValue(this.profitDaysBasis));
			} else {
				finServInst.setPftDaysBasis(getComboboxValue(this.profitDaysBasis));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.reviewDateToDateRow.isVisible() && this.profitDaysBasis.isDisabled()) {
				if (isValidComboValue(this.cbRateChangeToDate, Labels.getLabel("label_RateChangeDialog_ToDate.value"))
						&& this.cbRateChangeFromDate.getSelectedIndex() != 0) {
					if (((Date) this.cbRateChangeToDate.getSelectedItem().getValue())
							.compareTo((Date) this.cbRateChangeFromDate.getSelectedItem().getValue()) < 0) {
						throw new WrongValueException(this.cbRateChangeToDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",
										new String[] { Labels.getLabel("label_RateChangeDialog_ToDate.value"),
												Labels.getLabel("label_RateChangeDialog_FromDate.value") }));
					} else {
						finMain.setEventToDate((Date) this.cbRateChangeToDate.getSelectedItem().getValue());
					}
					finServInst.setToDate((Date) this.cbRateChangeToDate.getSelectedItem().getValue());
				}
			} else {
				if (this.anyDateToDateRow.isVisible() && this.anyDateRateChangeToDate.getValue() == null) {
					throw new WrongValueException(this.anyDateRateChangeToDate, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value") }));
				} else if (this.anyDateToDateRow.isVisible() && this.anyDateRateChangeToDate.getValue() != null) {
					if (this.anyDateRateChangeToDate.getValue().compareTo(lastPaidDate) < 0) {
						throw new WrongValueException(this.anyDateRateChangeToDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",
										new String[] {
												Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
												DateUtil.formatToLongDate(lastPaidDate) }));
					} else if (this.anyDateRateChangeToDate.getValue().compareTo(fm.getMaturityDate()) > 0) {
						throw new WrongValueException(this.anyDateRateChangeToDate,
								Labels.getLabel("DATE_ALLOWED_BEFORE",
										new String[] {
												Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
												DateUtil.formatToLongDate(fm.getMaturityDate()) }));
					}
				}
				finServInst.setToDate(this.anyDateRateChangeToDate.getValue());
				finMain.setEventToDate(this.anyDateRateChangeToDate.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.recalTypeRow.isVisible()
					&& isValidComboValue(this.cbReCalType, Labels.getLabel("label_RateChangeDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				finMain.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
				finServInst.setRecalType(getComboboxValue(this.cbReCalType));
			} else {
				finServInst.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (this.fromDateRow.isVisible()) {
			try {
				if (this.cbRecalFromDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbRecalFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_RateChangeDialog_RecalFromDate.value") }));
				}
				if (this.cbRateChangeFromDate.getSelectedIndex() > 0
						&& ((Date) this.cbRecalFromDate.getSelectedItem().getValue())
								.compareTo((Date) this.cbRateChangeFromDate.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(this.cbRecalFromDate, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_RateChangeDialog_RecalFromDate.value"), DateUtil
									.formatToLongDate((Date) this.cbRateChangeToDate.getSelectedItem().getValue()) }));
				}
				finMain.setRecalFromDate((Date) this.cbRecalFromDate.getSelectedItem().getValue());
				finServInst.setRecalFromDate(finMain.getRecalFromDate());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.tillDateRow.isVisible()) {
			try {
				if (this.cbRecalToDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_RateChangeDialog_RecalToDate.value") }));
				}
				if (this.cbRecalFromDate.getSelectedIndex() > 0
						&& ((Date) this.cbRecalFromDate.getSelectedItem().getValue())
								.compareTo((Date) this.cbRecalToDate.getSelectedItem().getValue()) > 0) {
					throw new WrongValueException(this.cbRecalToDate, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_RateChangeDialog_RecalToDate.value"), DateUtil
									.formatToLongDate((Date) this.cbRecalFromDate.getSelectedItem().getValue()) }));
				}

				finMain.setRecalToDate((Date) this.cbRecalToDate.getSelectedItem().getValue());
				finServInst.setRecalToDate(finMain.getRecalFromDate());
			} catch (WrongValueException we) {
				wve.add(we);
			}

		}

		try {
			if (anyDate.isChecked()) {
				if (this.anyDateRateChangeFromDate.getValue() != null
						&& this.anyDateRateChangeToDate.getValue() != null) {
					if (this.anyDateRateChangeFromDate.getValue()
							.compareTo(this.anyDateRateChangeToDate.getValue()) >= 0) {
						throw new WrongValueException(this.anyDateRateChangeToDate,
								Labels.getLabel("DATE_ALLOWED_AFTER", new String[] {
										Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeToDate.value"),
										Labels.getLabel("label_RateChangeDialog_AnyDateRateChangeFromDate.value") }));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServInst.setServiceReqNo(this.serviceReqNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServInst.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		BaseRateCode baseRateCode = null;
		if (allowBackDatedRateChange && StringUtils.trimToNull(finServInst.getBaseRate()) != null) {

			baseRateCode = this.baseRateCodeService.getBaseRateCodeById(finServInst.getBaseRate());

			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_FRQ_TERMS_VALIDATION)) {
				String errMsg = validateFrq(schdData, finServInst, baseRateCode);

				if (StringUtils.trimToNull(errMsg) != null) {
					throw new WrongValueException(this.rate, errMsg);
				}
			}

			BigDecimal marginRate = finServInst.getMargin();
			fm.setSkipRateReset(false);
			if (marginRate != null && marginRate.compareTo(BigDecimal.ZERO) != 0) {
				if (MessageUtil.confirm("Do you want to proceed with margin rate only.",
						MessageUtil.YES | MessageUtil.NO) == MessageUtil.YES) {
					// Calculating the old base rate if margin exists
					calcRates(schdData, finServInst, baseRateCode);
				}
			}
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			finServInst.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		}

		finServInst.setFinID(fm.getFinID());
		finServInst.setFinReference(fm.getFinReference());
		finServInst.setFinEvent(FinServiceEvent.RATECHG);

		if (allowBackDatedRateChange && StringUtils.trimToNull(finServInst.getBaseRate()) != null
				&& StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			String bRRpyRvwFrq = baseRateCode.getbRRepayRvwFrq();
			fm.setbRRpyRvwFrq(bRRpyRvwFrq);
		} else {
			fm.setbRRpyRvwFrq(null);
		}

		if (this.recalTypeRow.isVisible() && CalculationConstants.RPYCHG_STEPINST
				.equals(this.cbReCalType.getSelectedItem().getValue().toString())) {
			Date fromDate = (Date) this.cbRecalFromDate.getSelectedItem().getValue();
			Date maturityDate = getFinScheduleData().getFinanceMain().getMaturityDate();
			finServInst.setSchdMethod(CalculationConstants.RPYCHG_ADJTERMS);
			finMain.setRecalType(CalculationConstants.RPYCHG_ADJTERMS);
			finMain.setRecalFromDate(fromDate);
			finMain.setRecalToDate(maturityDate);
			finMain.setScheduleMaintained(true);
			finServInst.setRecalFromDate(fromDate);
			finServInst.setRecalToDate(maturityDate);
		}

		FinanceStepPolicyDetail rpyStp = null;
		if (CollectionUtils.isNotEmpty(getFinScheduleData().getStepPolicyDetails())) {
			rpyStp = sortSPDList(finMain);
		}

		if (this.recalTypeRow.isVisible() && this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_STEPINST)) {
			Date maturityDate = getFinScheduleData().getFinanceMain().getMaturityDate();

			if (PennantConstants.STEPPING_CALC_PERC.equals(finMain.getCalcOfSteps())) {
				Date fromDate = (Date) this.cbRecalFromDate.getSelectedItem().getValue();
				finServInst.setSchdMethod(CalculationConstants.RPYCHG_ADJTERMS);
				finMain.setRecalType(CalculationConstants.RPYCHG_ADJTERMS);
				finMain.setRecalFromDate(fromDate);
				finMain.setRecalToDate(maturityDate);
				finServInst.setRecalFromDate(fromDate);
				finServInst.setRecalToDate(maturityDate);
			} else if (PennantConstants.STEPPING_CALC_AMT.equals(finMain.getCalcOfSteps())) {
				List<FinanceScheduleDetail> fsdList = getFinScheduleData().getFinanceScheduleDetails();
				int fsdSize = fsdList.size();
				FinanceScheduleDetail fsd = getFinScheduleData().getFinanceScheduleDetails().get(fsdSize - 1);
				finMain.setAdjTerms(0);
				if (rpyStp != null && fsd.getSchDate().compareTo(rpyStp.getStepEnd()) != 0) {
					finMain.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);
					finMain.setRecalToDate(maturityDate);
					int months = DateUtil.getMonthsBetween(fsd.getSchDate(), rpyStp.getStepEnd());
					finMain.setAdjTerms(months);

					for (FinanceScheduleDetail finSch : fsdList) {
						if (finSch.getSchDate().compareTo(SysParamUtil.getAppDate()) > 0) {
							finMain.setRecalFromDate(finSch.getSchDate());
							break;
						}
					}

					finServInst.setRecalFromDate(finMain.getRecalFromDate());
					finServInst.setRecalToDate(maturityDate);
					finServInst.setRecalType(finMain.getRecalType());
					finServInst.setTerms(finMain.getAdjTerms());
				} else {
					BigDecimal recalculateRate = BigDecimal.ZERO;
					if (StringUtils.isNotBlank(finServInst.getBaseRate())) {
						recalculateRate = this.rate.getEffRateValue();
					} else if (finServInst.getActualRate().compareTo(BigDecimal.ZERO) > 0) {
						recalculateRate = finServInst.getActualRate();
					}

					if (CalculationConstants.SCHMTHD_EQUAL.equals(finMain.getScheduleMethod())) {
						if (recalculateRate.compareTo(fsd.getCalculatedRate()) > 0) {
							finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
							finMain.setRecalFromDate(rpyStp.getStepStart());

							finServInst.setRecalFromDate(finMain.getRecalFromDate());
						} else {
							finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
						}
					} else {
						finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
						for (FinanceScheduleDetail finSch : fsdList) {
							if (finSch.getSchDate().compareTo(finMain.getEventFromDate()) > 0) {
								if (!finSch.isRepayOnSchDate()) {
									continue;
								}
								finMain.setRecalFromDate(finSch.getSchDate());
								break;
							}
						}
						finMain.setResetOrgBal(false);
						finServInst.setRecalFromDate(finMain.getRecalFromDate());
					}
					finServInst.setRecalType(finMain.getRecalType());
				}
			}
		}

		// Service details calling for Schedule calculation
		setFinScheduleData(rateChangeService.getRateChangeDetails(schdData, finServInst, moduleDefiner));

		finServInst.setPftChg(schdData.getPftChg());
		fm.resetRecalculationFields();
		schdData.setFinServiceInstruction(finServInst);

		// Show Error Details in Schedule Maintainance
		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			MessageUtil.showError(schdData.getErrorDetails().get(0));
			schdData.getErrorDetails().clear();
		} else {
			schdData.setSchduleGenerated(true);

			if (getFinanceMainDialogCtrl() != null) {
				getFinanceMainDialogCtrl().doFillScheduleList(schdData);
			}
			this.window_RateChangeDialog.onClose();
		}

		logger.debug("Leaving");
	}

	private FinanceStepPolicyDetail sortSPDList(FinanceMain finMain) {
		List<FinanceStepPolicyDetail> spdList = new ArrayList<>();
		List<FinanceStepPolicyDetail> rpyList = new ArrayList<>(1);
		List<FinanceStepPolicyDetail> grcList = new ArrayList<>(1);
		FinanceStepPolicyDetail rpyStp = null;

		for (FinanceStepPolicyDetail spd : getFinScheduleData().getStepPolicyDetails()) {
			if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(spd.getStepSpecifier())) {
				rpyList.add(spd);
			} else {
				grcList.add(spd);
			}
		}

		if (CollectionUtils.isNotEmpty(rpyList)) {
			Collections.sort(rpyList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
					: step1.getStepNo() < step2.getStepNo() ? -1 : 0);
			rpyStp = rpyList.get(rpyList.size() - 1);
			finMain.setRpyStps(true);
		}

		if (CollectionUtils.isNotEmpty(grcList)) {
			Collections.sort(grcList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
					: step1.getStepNo() < step2.getStepNo() ? -1 : 0);
			finMain.setGrcStps(true);
		}

		spdList.addAll(grcList);
		spdList.addAll(rpyList);
		getFinScheduleData().setStepPolicyDetails(spdList);
		return rpyStp;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.rateChange.isVisible()) {
			this.rateChange.setConstraint(new PTDecimalValidator(Labels.getLabel("label_RateChangeDialog_Rate.value"),
					9, false, false, 0, 9999));
		}
		if (!this.rate.isMarginReadonly()) {
			this.rate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_RateChangeDialog_MarginRate.value"), 9, false, true, -9999, 9999));
		}
		if (this.baseRateRow.isVisible()) {
			this.rate.setBaseConstraint(
					new PTStringValidator(Labels.getLabel("label_RateChangeDialog_BaseRate.value"), null, false, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.rate.setBaseConstraint("");
		this.rate.setSpecialConstraint("");
		this.rate.setMarginConstraint("");
		this.rateChange.setConstraint("");
		this.cbRateChangeFromDate.setConstraint("");
		this.cbRateChangeToDate.setConstraint("");
		this.cbReCalType.setConstraint("");
		this.profitDaysBasis.setConstraint("");
		this.serviceReqNo.setConstraint("");
		this.remarks.setConstraint("");
		this.anyDateRateChangeFromDate.setConstraint("");
		this.anyDateRateChangeToDate.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages
	 * 
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);

		this.rate.setBaseErrorMessage("");
		this.rate.setSpecialErrorMessage("");
		this.rate.setMarginErrorMessage("");
		this.rateChange.clearErrorMessage();
		this.cbRateChangeFromDate.clearErrorMessage();
		this.cbRateChangeToDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.profitDaysBasis.clearErrorMessage();
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.anyDateRateChangeFromDate.setErrorMessage("");
		this.anyDateRateChangeToDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddReviewRate(Event event) throws InterruptedException {
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
	 * The Click event is raised when the Close event is occurred. <br>
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

	public void onFulfill$rate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.rate.getBaseComp());
		Clients.clearWrongValue(this.rate.getSpecialComp());
		this.rate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;

		this.baseRateRvwFrqRow.setVisible(false);

		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = rate.getBaseObject();
			if (dataObject instanceof String) {
				this.rate.setBaseValue(dataObject.toString());
				this.rate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.rate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.rate.getSpecialValue(),
							this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.rate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.rate.setBaseValue("");
					}

					if (StringUtils.trimToNull(details.getbRRepayRvwFrq()) != null
							&& (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE))) {
						this.bRRepayRvwFrq.setValue(details.getbRRepayRvwFrq());
						this.baseRateRvwFrqRow.setVisible(true);
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = rate.getSpecialObject();
			if (dataObject instanceof String) {
				this.rate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.rate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.rate.getSpecialValue(),
							this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.rate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.rate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}

	// Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		changeRecalType();
		logger.debug("Leaving" + event.toString());
	}

	private void changeRecalType() {

		setCalTypesForManualSchdule();

		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			this.tillDateRow.setVisible(true);
			this.fromDateRow.setVisible(true);

			// RateChangeTodate is not allowed to select the maturity Date when recal type is Tilldate
			if (this.cbRateChangeToDate.getSelectedIndex() > 0
					&& DateUtil.compare((Date) this.cbRateChangeToDate.getSelectedItem().getValue(),
							getFinScheduleData().getFinanceMain().getMaturityDate()) == 0) {
				throw new WrongValueException(this.cbRateChangeToDate,
						Labels.getLabel("label_RateChange_MaturityDate"));

			}
			if (this.cbRateChangeFromDate.getSelectedIndex() > 0) {
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
				if (this.cbRateChangeToDate.getSelectedIndex() > 0) {
					fillSchToDates(this.cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRateChangeToDate.getSelectedItem().getValue(), true);
				}
			} else if (anyDateRateChangeFromDate.getValue() != null) {
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						anyDateRateChangeFromDate.getValue(), false);
				fillSchToDates(this.cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(),
						anyDateRateChangeToDate.getValue(), true);
			}

		} else if (this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.tillDateRow.setVisible(false);
			this.fromDateRow.setVisible(true);

			if (this.cbRateChangeFromDate.getSelectedIndex() > 0) {
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
			} else if (anyDateRateChangeFromDate.getValue() != null) {
				fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
						anyDateRateChangeFromDate.getValue(), false);
			}
		} else {
			this.tillDateRow.setVisible(false);
			this.fromDateRow.setVisible(false);
		}
	}

	/**
	 * For open amortization loans RecalType is allowed only TILLDATE And TILLMDT
	 */
	private void setCalTypesForManualSchdule() {
		FinanceMain fm = getFinScheduleData().getFinanceMain();

		if (fm.isManualSchedule()) {
			this.cbReCalType.setDisabled(true);
			fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_TILLMDT, PennantStaticListUtil.getSchCalCodes(),
					",ADDTERM,ADDLAST,ADDRECAL,STEPPOS,TILLDATE,ADJMDT,ADJTERMS,");
		}

	}

	public void onChange$cbRateChangeToDate(Event event) {
		logger.debug("Entering" + event.toString());

		this.cbRecalFromDate.getItems().clear();
		Object cbRateChange = this.cbRateChangeFromDate.getSelectedItem().getValue();
		if (this.cbReCalType.getSelectedIndex() > 0) {
			if ((this.cbReCalType.getSelectedItem().getValue().toString()).equals(CalculationConstants.RPYCHG_TILLDATE)
					|| (this.cbReCalType.getSelectedItem().getValue().toString())
							.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				if (this.cbRateChangeFromDate.getSelectedIndex() > 0) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) cbRateChange, false);
				}
			} else {
				if (this.cbRateChangeFromDate.getSelectedIndex() > 0) {
					fillSchToDates(this.cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) cbRateChange, false);
				} else {
					if (this.cbRecalFromDate.getItemCount() > 0) {
						this.cbRecalFromDate.setSelectedIndex(0);
					}
				}
			}
		}

		FinanceMain fm = getFinScheduleData().getFinanceMain();
		if (this.anyDateRateChangeFromDate.getValue() != null) {
			processStepLoans(fm, this.anyDateRateChangeFromDate.getValue());
		}

		logger.debug("Leaving" + event.toString());
	}

	public void fillSchToDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails,
			Date fillAfter, boolean includeFromDate) {
		logger.debug("Entering");

		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		boolean allowBackDatedRateChange = SysParamUtil
				.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE);

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				if (i == financeScheduleDetails.size() - 1) {
					continue;
				}

				if ((curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
						&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0
								&& !curSchd.isSchPftPaid())
								|| (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
										&& !curSchd.isSchPriPaid()))) {

					if (allowBackDatedRateChange
							&& DateUtil.compare(curSchd.getSchDate(), SysParamUtil.getAppDate()) < 0) {
						continue;
					}

					comboitem = new Comboitem();
					comboitem.setLabel(
							DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
					comboitem.setAttribute("toSpecifier", curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());

					if (includeFromDate && DateUtil.compare(curSchd.getSchDate(), fillAfter) >= 0) {
						dateCombobox.appendChild(comboitem);
						if (getFinanceScheduleDetail() != null
								&& curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
							dateCombobox.setSelectedItem(comboitem);
						}
					} else if (!includeFromDate && DateUtil.compare(curSchd.getSchDate(), fillAfter) > 0) {
						dateCombobox.appendChild(comboitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCheck$reviewDates(Event event) {
		logger.debug("Entering " + event.toString());

		doClearMessage();

		this.reviewDateFromDateRow.setVisible(true);
		this.cbRateChangeFromDate.setVisible(true);
		this.reviewDateToDateRow.setVisible(true);
		this.cbRateChangeToDate.setVisible(true);
		this.anyDateFromDateRow.setVisible(false);
		this.anyDateToDateRow.setVisible(false);
		// FIXME PV @03/JUN/2018 ADJTERMS Testing
		// StringBuilder excludeFileds=new StringBuilder(",ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
		StringBuilder excludeFileds = new StringBuilder(",ADDTERM,ADDLAST,ADDRECAL,");
		// Stepping POS Exclude for Recal Type
		if (!getFinScheduleData().getFinanceMain().isStepFinance()
				|| !StringUtils.equals(getFinScheduleData().getFinanceMain().getStepType(),
						FinanceConstants.STEPTYPE_PRIBAL)
				|| !(StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(),
						CalculationConstants.SCHMTHD_PRI)
						|| StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(),
								CalculationConstants.SCHMTHD_PRI_PFT))) {
			excludeFileds.append("STEPPOS,");
		}

		SanctionBasedSchedule.getSanctionRecalExcludeFlds(getFinScheduleData(), excludeFileds);

		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		changeRecalType();

		fillSchFromDates(this.cbRateChangeFromDate, getFinScheduleData().getFinanceScheduleDetails());
		if (getFinScheduleData().getFinanceScheduleDetails() != null
				&& this.cbRateChangeFromDate.getSelectedIndex() > 0) {
			fillSchToDates(this.cbRateChangeToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRateChangeFromDate.getSelectedItem().getValue(), false);
		} else {
			fillSchToDates(this.cbRateChangeToDate, getFinScheduleData().getFinanceScheduleDetails(),
					getFinScheduleData().getFinanceMain().getFinStartDate());
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onCheck$anyDate(Event event) {
		logger.debug("Entering " + event.toString());

		doClearMessage();

		this.reviewDateFromDateRow.setVisible(false);
		this.reviewDateToDateRow.setVisible(false);
		this.anyDateFromDateRow.setVisible(true);
		this.anyDateRateChangeFromDate.setVisible(true);
		this.anyDateRateChangeToDate.setVisible(true);
		this.anyDateToDateRow.setVisible(true);
		// FIXME PV @03/JUN/2018 ADJTERMS Testing
		// StringBuilder excludeFileds=new StringBuilder(",CURPRD,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
		StringBuilder excludeFileds = new StringBuilder(",CURPRD,ADDTERM,ADDLAST,ADDRECAL,");
		// Stepping POS Exclude for Recal Type
		if (!getFinScheduleData().getFinanceMain().isStepFinance()
				|| !StringUtils.equals(getFinScheduleData().getFinanceMain().getStepType(),
						FinanceConstants.STEPTYPE_PRIBAL)
				|| !(StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(),
						CalculationConstants.SCHMTHD_PRI)
						|| StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(),
								CalculationConstants.SCHMTHD_PRI_PFT))) {
			excludeFileds.append("STEPPOS,");
		}

		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
		changeRecalType();

		if (this.anyDateRateChangeFromDate.getValue() != null) {
			processStepLoans(getFinScheduleData().getFinanceMain(), this.anyDateRateChangeFromDate.getValue());
		}

		logger.debug("Leaving " + event.toString());
	}

	private void setEffectiveRate() {
		if (StringUtils.isBlank(this.rate.getBaseValue())) {
			this.rate.setEffRateText(PennantApplicationUtil.formatRate(
					(this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue()).doubleValue(),
					2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),
				getFinScheduleData().getFinanceMain().getFinCcy(), this.rate.getSpecialValue(),
				this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue(),
				getFinScheduleData().getFinanceMain().getRpyMinRate(),
				getFinScheduleData().getFinanceMain().getRpyMaxRate());
		if (rateDetail.getErrorDetails() == null) {
			this.rate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.rate.setSpecialValue("");
		}
	}

	/** To fill to dates based on selected from date */
	public void onChange$cbRateChangeFromDate(Event event) {
		logger.debug("Entering" + event.toString());

		this.rate.setBaseValue("");
		this.rate.setSpecialValue("");
		this.rate.setEffRateValue(BigDecimal.ZERO);
		StringBuilder excludeFileds = new StringBuilder("");

		FinanceMain fm = getFinScheduleData().getFinanceMain();
		if (!fm.isAllowGrcPftRvw() && !fm.isAllowRepayRvw()) {
			excludeFileds.append(",TILLDATE");
		}

		// Stepping POS Exclude for Recal Type
		if (!fm.isStepFinance() || !StringUtils.equals(fm.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)
				|| !(StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
						|| StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT))) {
			excludeFileds.append(",STEPPOS");
		}

		Object cbRateChange = this.cbRateChangeFromDate.getSelectedItem().getValue();
		if (isValidComboValue(this.cbRateChangeFromDate, Labels.getLabel("label_RateChangeDialog_FromDate.value"))) {

			if (fm.getNumberOfTerms() == 1) {
				// FIXME PV @03/JUN/2018 ADJTERMS Testing
				// excludeFileds.append(",TILLMDT,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
				excludeFileds.append(",TILLMDT,ADDTERM,ADDLAST,ADDRECAL,");

				SanctionBasedSchedule.getSanctionRecalExcludeFlds(getFinScheduleData(), excludeFileds);

				fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
			} else {
				// FIXME PV @03/JUN/2018 ADJTERMS Testing
				// excludeFileds.append(",ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
				excludeFileds.append(",ADDTERM,ADDLAST,ADDRECAL,");
				SanctionBasedSchedule.getSanctionRecalExcludeFlds(getFinScheduleData(), excludeFileds);
				fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), excludeFileds.toString());
			}

			this.cbRateChangeToDate.getItems().clear();
			String frSpecifier = this.cbRateChangeFromDate.getSelectedItem().getAttribute("fromSpecifier").toString();
			fillSchToDates(this.cbRateChangeToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) cbRateChange);

			if (fm.isAllowGrcPeriod() && (frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE)
					|| frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {

				if (fm.getGraceBaseRate() != null) {
					this.rate.setBaseValue(fm.getGraceBaseRate());
					this.rate.setSpecialValue(fm.getGraceSpecialRate());
					RateDetail rateDetail = RateUtil.rates(fm.getGraceBaseRate(), fm.getFinCcy(),
							fm.getGraceSpecialRate(), BigDecimal.ZERO, fm.getGrcMinRate(), fm.getGrcMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				}

				if (frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE)
						&& fm.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_NOPAY)) {

					if (fm.getNumberOfTerms() == 1) {
						// FIXME PV @03/JUN/2018 ADJTERMS Testing
						// excludeFileds.append(",TILLMDT,CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
						excludeFileds.append(",TILLMDT,CURPRD,TILLDATE,ADDTERM,ADDLAST,ADDRECAL,");
						SanctionBasedSchedule.getSanctionRecalExcludeFlds(getFinScheduleData(), excludeFileds);
						fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(),
								excludeFileds.toString());
					} else {
						// FIXME PV @03/JUN/2018 ADJTERMS Testing
						// excludeFileds.append(",CURPRD,TILLDATE,ADDTERM,ADDLAST,ADDRECAL,");
						excludeFileds.append(",CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
						SanctionBasedSchedule.getSanctionRecalExcludeFlds(getFinScheduleData(), excludeFileds);
						fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(),
								excludeFileds.toString());
					}
				}

				fillComboBox(profitDaysBasis, fm.getGrcProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(),
						"");
			} else if ((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY))
					|| (frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {

				if (fm.getRepayBaseRate() != null) {
					this.rate.setBaseValue(fm.getRepayBaseRate());
					this.rate.setSpecialValue(fm.getRepaySpecialRate());
					RateDetail rateDetail = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(),
							fm.getRepaySpecialRate(), BigDecimal.ZERO, fm.getRpyMinRate(), fm.getRpyMaxRate());
					this.rate.setEffRateValue(rateDetail.getNetRefRateLoan());
				}
				fillComboBox(profitDaysBasis, fm.getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(), "");
			}

			changeRecalType();
		}

		if (this.anyDateRateChangeFromDate.getValue() != null) {
			processStepLoans(getFinScheduleData().getFinanceMain(), this.anyDateRateChangeFromDate.getValue());
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbRecalFromDate(Event event) {
		logger.debug("Entering" + event.toString());

		boolean allowBackDatedRateChange = SysParamUtil
				.isAllowed(SMTParameterConstants.ALLOW_INCLUDE_FROMDATE_ADD_RATE_CHANGE);

		if (this.cbRecalFromDate.getSelectedIndex() > 0) {
			this.cbRecalToDate.getItems().clear();
			// As discussed with Pradeep ,include from date has set to true,if any issue should recheck it
			Date recalFrom = (Date) this.cbRecalFromDate.getSelectedItem().getValue();
			if (reviewDates.isChecked()) {
				if (this.cbRateChangeToDate.getSelectedIndex() > 0) {
					Date ratechgTo = (Date) this.cbRateChangeToDate.getSelectedItem().getValue();
					if (ratechgTo.compareTo(recalFrom) > 0) {
						fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), ratechgTo,
								allowBackDatedRateChange);
					} else {
						fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), recalFrom,
								allowBackDatedRateChange);
					}
				}
			} else if (anyDate.isChecked()) {
				Date ratechgTo = (Date) this.anyDateRateChangeFromDate.getValue();
				if (DateUtil.compare(ratechgTo, recalFrom) > 0) {
					fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), ratechgTo,
							allowBackDatedRateChange);
				} else {
					fillSchToDates(cbRecalToDate, getFinScheduleData().getFinanceScheduleDetails(), recalFrom,
							allowBackDatedRateChange);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// Validating the baserate frequency against the recal from date and to date.
	private String validateFrq(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction,
			BaseRateCode baseRateCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder errMsg = new StringBuilder();

		if (baseRateCode == null || StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) == null) {
			return errMsg.toString();
		}

		String bRRepayRvwFrq = baseRateCode.getbRRepayRvwFrq();
		String frqCode = FrequencyUtil.getFrequencyCode(bRRepayRvwFrq);

		List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();

		if (CollectionUtils.isEmpty(financeScheduleDetails)) {
			return errMsg.toString();
		}

		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		Date schdDate = financeScheduleDetails.get(0).getSchDate();
		int schdDay = DateUtil.getDay(schdDate);

		int schdCount = 0;
		Date newEvtFromDate = evtFromDate;

		// Calculating the number of schedules between recal from and to dates
		while (DateUtil.compare(evtToDate, newEvtFromDate) >= 0) {
			int day = DateUtil.getDay(newEvtFromDate);
			if (schdDay == day) {
				schdCount = schdCount + 1;
			}
			newEvtFromDate = DateUtil.addDays(newEvtFromDate, 1);
		}

		// Validating the number schedules against the frequency code.
		switch (frqCode) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			if (schdCount < 12) {
				errMsg.append(" Selected Frequency is Yearly, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate review frequency Yearly.");
			}
			break;
		case FrequencyCodeTypes.FRQ_2YEARLY:
			if (schdCount < 24) {
				errMsg.append(" Selected Frequency is 2-Yearly, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate frequency 2-Yearly.");
			}
			break;
		case FrequencyCodeTypes.FRQ_3YEARLY:
			if (schdCount < 36) {
				errMsg.append(" Selected Frequency is Yearly, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate frequency 3-Yearly.");
			}
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			if (schdCount < 6) {
				errMsg.append(" Selected Frequency is HalfYearly, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate frequency Half Yearly.");
			}
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			if (schdCount < 3) {
				errMsg.append(" Selected Frequency is Quarterly, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate frequency Quarterly.");
			}
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			if (schdCount < 2) {
				errMsg.append(" Selected Frequency is Every two months, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate frequency Every two months.");
			}
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			if (schdCount < 1) {
				errMsg.append(" Selected Frequency is monthly, from date : ");
				errMsg.append(DateUtil.formatToLongDate(evtFromDate));
				errMsg.append(", to date : ").append(DateUtil.formatToLongDate(evtToDate));
				errMsg.append(". not allowed for the rate frequency monthly.");
			}
			break;
		default:
			break;
		}
		logger.debug(Literal.LEAVING);

		return errMsg.toString();
	}

	/**
	 * Method for Resetting Review Schedule Flags with generation of New Dates, if not exists
	 */
	private FinScheduleData resetRvwFrqSchDates(FinScheduleData finScheduleData, String frequency,
			FinServiceInstruction finServiceInst) {

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		if (CollectionUtils.isEmpty(finSchdDetails)) {
			return finScheduleData;
		}

		sortSchdDetails(finSchdDetails);
		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		// Fetching the Schedule details from recal from date.
		FinanceScheduleDetail oldRateSchd = null;
		for (FinanceScheduleDetail detail : finSchdDetails) {
			if (DateUtil.compare(detail.getSchDate(), evtFromDate) > 0) {
				break;
			}
			oldRateSchd = detail;
		}

		boolean resetByOldRate = true;
		String baseRate = null;
		if (oldRateSchd == null) {
			resetByOldRate = false;
		} else {
			baseRate = oldRateSchd.getBaseRate();
		}

		if (StringUtils.isBlank(baseRate)) {
			resetByOldRate = false;
		}

		BigDecimal newMrgRate = finServiceInst.getMargin();
		if (resetByOldRate) {
			if (newMrgRate != null && newMrgRate.compareTo(BigDecimal.ZERO) != 0) {
				int usrDecision = MessageUtil.confirm("Do you want to proceed with margin rate only.",
						MessageUtil.YES | MessageUtil.NO);

				if (usrDecision == MessageUtil.NO) {
					resetByOldRate = false;
				}
			} else {
				resetByOldRate = false;
			}
		}

		if (StringUtils.isBlank(frequency) || StringUtils.equals(baseRate, finServiceInst.getBaseRate())) {
			if (resetByOldRate) {
				setRatesOnSchedule(oldRateSchd, finScheduleData, finServiceInst);
			}
			return finScheduleData;
		}

		int sdSize = finSchdDetails.size();
		Map<Date, FinanceScheduleDetail> schdMap = new HashMap<>();

		for (int i = 0; i < sdSize; i++) {

			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			// Setting Rates between From date and To date
			if (DateUtil.compare(schdDate, evtFromDate) >= 0 && DateUtil.compare(schdDate, evtToDate) <= 0) {
				if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate() || curSchd.isCpzOnSchDate()
						|| curSchd.isDisbOnSchDate()) {
					curSchd.setRvwOnSchDate(false);
					schdMap.put(schdDate, curSchd);

					finSchdDetails.remove(i);
					i = i - 1;
					sdSize = sdSize - 1;
					continue;
				}

				if (curSchd.isFrqDate()) {
					String pftFrq = null;
					if (finMain.isAllowGrcPeriod()
							&& DateUtil.compare(schdDate, finMain.getGrcPeriodEndDate()) <= 0) {
						pftFrq = finMain.getGrcPftFrq();
					} else {
						pftFrq = finMain.getRepayPftFrq();
					}

					if (FrequencyUtil.isFrqDate(pftFrq, schdDate)) {
						curSchd.setRvwOnSchDate(false);
						schdMap.put(schdDate, curSchd);

						finSchdDetails.remove(i);
						i = i - 1;
						sdSize = sdSize - 1;
						continue;
					}
				}

				if (curSchd.isRvwOnSchDate()) {
					finSchdDetails.remove(i);
					i = i - 1;
					sdSize = sdSize - 1;
					continue;
				}

			}
			if (DateUtil.compare(schdDate, evtToDate) > 0) {
				break;
			}
		}

		finScheduleData.setScheduleMap(schdMap);
		frequency = resetFrqByEventDate(frequency, evtFromDate);
		finScheduleData = ScheduleGenerator.setRvwDatesOnRateFrq(finScheduleData, frequency);
		sortSchdDetails(finScheduleData.getFinanceScheduleDetails());
		if (resetByOldRate) {
			setRatesOnSchedule(oldRateSchd, finScheduleData, finServiceInst);
		}

		return finScheduleData;
	}

	private String resetFrqByEventDate(String frequency, Date eventFromDate) {

		String mnth = "";
		String frqCode = FrequencyUtil.getFrequencyCode(frequency);

		if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode) || FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)
				|| FrequencyCodeTypes.FRQ_BIMONTHLY.equals(frqCode)) {
			mnth = FrequencyUtil.getMonthFrqValue(
					DateUtil.format(eventFromDate, PennantConstants.DBDateFormat).split("-")[1], frqCode);
		} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode) || FrequencyCodeTypes.FRQ_2YEARLY.equals(frqCode)
				|| FrequencyCodeTypes.FRQ_3YEARLY.equals(frqCode)) {
			mnth = DateUtil.format(eventFromDate, PennantConstants.DBDateFormat).split("-")[1];
		} else {
			mnth = "00";
		}
		mnth = frqCode.concat(mnth);

		String day = StringUtils.substring(frequency, 3, 5);
		// day = DateUtility.format(eventFromDate, PennantConstants.DBDateFormat).split("-")[2];

		if (FrequencyCodeTypes.FRQ_DAILY.equals(frqCode)) {
			day = "00";
		} else if (FrequencyCodeTypes.FRQ_WEEKLY.equals(frqCode)) {
			day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 7), 2, "0");
		} else if (FrequencyCodeTypes.FRQ_FORTNIGHTLY.equals(frqCode)) {
			day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 14), 2, "0");
		}
		frequency = mnth.concat(day);

		return frequency;
	}

	private void setRatesOnSchedule(FinanceScheduleDetail oldRateSchd, FinScheduleData scheduleData,
			FinServiceInstruction finServiceInst) {

		FinanceMain finMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = scheduleData.getFinanceScheduleDetails();

		String baseRate = oldRateSchd.getBaseRate();
		BigDecimal calculatedRate = oldRateSchd.getCalculatedRate();
		BigDecimal marginRate = oldRateSchd.getMrgRate();
		BigDecimal oldCalRate = calculatedRate.subtract(marginRate);
		BigDecimal newCalRate = oldCalRate
				.add(finServiceInst.getMargin() == null ? BigDecimal.ZERO : finServiceInst.getMargin());
		finMain.setSkipRateReset(true);

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		int sdSize = finSchdDetails.size();
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			if (DateUtil.compare(schdDate, evtFromDate) >= 0 && DateUtil.compare(schdDate, evtToDate) <= 0) {
				curSchd.setBaseRate(baseRate);
				curSchd.setSplRate(finServiceInst.getSplRate());
				curSchd.setMrgRate(finServiceInst.getMargin());
				curSchd.setCalculatedRate(newCalRate);
				curSchd.setActRate(finServiceInst.getActualRate());
			}
		}
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

	// Calculating the old base rate if margin exists
	private void calcRates(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst,
			BaseRateCode baseRateCode) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		if (CollectionUtils.isEmpty(financeScheduleDetails)) {
			financeMain.setSkipRateReset(false);
			return;
		}

		FinanceMain finMain = finScheduleData.getFinanceMain();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		FinanceScheduleDetail prevScheduleDetail = null;

		// Fetching the Schedule details from recal from date.
		for (FinanceScheduleDetail detail : financeScheduleDetails) {
			if (DateUtil.compare(detail.getSchDate(), evtFromDate) >= 0) {
				break;
			} else {
				prevScheduleDetail = detail;
			}
		}

		if (prevScheduleDetail == null) {
			financeMain.setSkipRateReset(false);
			return;
		}

		String baseRate = prevScheduleDetail.getBaseRate();
		BigDecimal calculatedRate = prevScheduleDetail.getCalculatedRate();
		BigDecimal marginRate = prevScheduleDetail.getMrgRate();
		BigDecimal oldBaseRate = calculatedRate.subtract(marginRate);
		finServiceInst.setActualRate(oldBaseRate.add(finServiceInst.getMargin()));
		financeMain.setSkipRateReset(true);

		int sdSize = financeScheduleDetails.size();
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			if ((DateUtil.compare(schdDate, evtFromDate) >= 0 && DateUtil.compare(schdDate, evtToDate) < 0)
					|| (i == (sdSize - 1))) {
				curSchd.setBaseRate(baseRate);
				curSchd.setSplRate(finServiceInst.getSplRate());
				curSchd.setMrgRate(finServiceInst.getMargin());
				curSchd.setCalculatedRate(finServiceInst.getActualRate());
				curSchd.setActRate(finServiceInst.getActualRate());
			}

		}
		logger.debug(Literal.LEAVING);
		return;
	}

	private void processStepLoans(FinanceMain finMain, Date fromDate) {
		boolean isStepLoan = false;

		if (finMain.isStepFinance()) {
			if (StringUtils.isNotBlank(finMain.getStepPolicy())
					|| (finMain.isAlwManualSteps() && finMain.getNoOfSteps() > 0)) {
				isStepLoan = true;
			}
		}

		if (!isStepLoan) {
			return;
		}

		readOnlyComponent(true, this.cbReCalType);
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(CalculationConstants.RPYCHG_STEPINST);
		comboitem.setLabel(Labels.getLabel("label_" + CalculationConstants.RPYCHG_STEPINST));
		this.cbReCalType.appendChild(comboitem);
		this.cbReCalType.setSelectedItem(comboitem);

		FinScheduleData schdData = getFinScheduleData();
		List<RepayInstruction> rpst = schdData.getRepayInstructions();
		Date recalFromDate = null;

		if (PennantConstants.STEPPING_CALC_PERC.equals(finMain.getCalcOfSteps())) {
			if (fromDate.compareTo(finMain.getGrcPeriodEndDate()) > 0) {
				RepayInstruction rins = rpst.get(rpst.size() - 1);
				recalFromDate = rins.getRepayDate();
				finMain.setRecalSteps(false);
			} else {
				finMain.setRecalSteps(true);
				for (RepayInstruction repayInstruction : rpst) {
					if (repayInstruction.getRepayDate().compareTo(finMain.getGrcPeriodEndDate()) > 0) {
						recalFromDate = repayInstruction.getRepayDate();
						break;
					}
				}
			}

			fromDateRow.setVisible(true);
		} else if (PennantConstants.STEPPING_CALC_AMT.equals(finMain.getCalcOfSteps())) {
			fromDateRow.setVisible(false);
		}

		comboitem = new Comboitem();
		Comboitem comboitem1 = new Comboitem();
		comboitem.setLabel(DateUtil.formatToLongDate(recalFromDate));
		comboitem.setValue(recalFromDate);
		cbRecalFromDate.appendChild(comboitem1);
		cbRecalFromDate.appendChild(comboitem);
		cbRecalFromDate.setSelectedItem(comboitem);
		readOnlyComponent(true, this.cbRecalFromDate);
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

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void setRateChangeService(RateChangeService rateChangeService) {
		this.rateChangeService = rateChangeService;
	}

	public void setBaseRateCodeService(BaseRateCodeService baseRateCodeService) {
		this.baseRateCodeService = baseRateCodeService;
	}

}
