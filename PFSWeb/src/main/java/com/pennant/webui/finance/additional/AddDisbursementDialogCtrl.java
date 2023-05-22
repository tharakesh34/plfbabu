/**
 * 
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
 * * FileName : AddDisbursementDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Button;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SanctionBasedSchedule;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.FinFeeDetailListCtrl;
import com.pennant.webui.finance.financemain.ManualScheduleDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;

public class AddDisbursementDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 4583907397986780542L;
	private static final Logger logger = LogManager.getLogger(AddDisbursementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AddDisbursementDialog;
	protected Groupbox gb_DisbursementDetails;
	protected CurrencyBox disbAmount;
	protected Datebox fromDate;
	protected Combobox cbFromDate;
	protected Combobox cbTillDate;
	protected Combobox cbReCalType;
	protected Combobox cbSchdMthd;
	protected Intbox adjTerms;
	protected Row fromDateRow;
	protected Row tillDateRow;
	protected Row numOfTermsRow;
	protected Row row_assetUtilization;
	protected Checkbox alwAssetUtilize;
	protected Label label_AddDisbursementDialog_TillDate;
	protected Label label_AddDisbursementDialog_TillFromDate;
	protected Row reCalTypeRow;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;
	protected Row schdMthdRow;

	// Manual Schedule details.
	protected Tab disbursementDetailsTab;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Button btnAddDisbursement;

	private Date lastPaidDate = null;
	private BigDecimal grcEndDisbAmount = BigDecimal.ZERO;
	private String moduleDefiner = "";
	private boolean isWIF = false;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient FinFeeDetailListCtrl finFeeDetailListCtrl;
	private AccountsService accountsService;

	private transient boolean validationOn;
	private transient AddDisbursementService addDisbursementService;
	private OverdrafLoanService overdrafLoanService;
	Date appDate = SysParamUtil.getAppDate();

	protected Checkbox qDP;
	protected Row row_Qdp;

	private ManualScheduleDialogCtrl manualScheduleDialogCtrl;
	private boolean isDisbDetailError = false;
	private String roleCode = "";
	private Date lastDisbDate = null;

	/**
	 * default constructor.<br>
	 */
	public AddDisbursementDialogCtrl() {
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
	public void onCreate$window_AddDisbursementDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AddDisbursementDialog);

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

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			}

			if (arguments.containsKey("isWIF")) {
				isWIF = (boolean) arguments.get("isWIF");
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
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("feeDetailListCtrl")) {
				setFinFeeDetailListCtrl((FinFeeDetailListCtrl) arguments.get("feeDetailListCtrl"));
			}

			if (getFinFeeDetailListCtrl() == null) {
				this.setFinFeeDetailListCtrl((FinFeeDetailListCtrl) scheduleDetailDialogCtrl.getClass()
						.getMethod("getFinFeeDetailListCtrl").invoke(scheduleDetailDialogCtrl));
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AddDisbursementDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		// Empty sent any required attributes
		this.disbAmount.setProperties(false, format);
		this.disbAmount.setMandatory(true);
		this.disbAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.disbAmount.setScale(format);
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.adjTerms.setMaxlength(2);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		this.qDP.setDisabled(false);

		FinanceMain fm = getFinScheduleData().getFinanceMain();
		if (fm.isManualSchedule()) {
			this.window_AddDisbursementDialog.setWidth("60%");
			this.gb_DisbursementDetails.setHeight(borderLayoutHeight - 270 + "px");
			this.window_AddDisbursementDialog.setHeight(borderLayoutHeight - 160 + "px");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AddDisbursementDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = getFinScheduleData().getFinanceMain();

		if (fm.isManualSchedule()) {
			appendManualScheduleTab();

			this.schdMthdRow.setVisible(false);
			this.reCalTypeRow.setVisible(false);
		}

		for (FinanceDisbursement curDisb : aFinSchData.getDisbursementDetails()) {
			if (FinanceConstants.DISB_STATUS_CANCEL.equals(curDisb.getDisbStatus())) {
				continue;
			}
			this.lastDisbDate = curDisb.getDisbDate();
		}

		this.row_Qdp.setVisible(false);
		if (fm.isQuickDisb()) {
			this.row_Qdp.setVisible(true);
		}

		if (ImplementationConstants.ALW_QDP_CUSTOMIZATION) {
			this.row_Qdp.setVisible(false);
		}

		if (getFinanceScheduleDetail() != null) {
			this.disbAmount.setValue(PennantApplicationUtil.formateAmount(getFinanceScheduleDetail().getDisbAmount(),
					CurrencyUtil.getFormat(aFinSchData.getFinanceMain().getFinCcy())));
			this.fromDate.setValue(getFinanceScheduleDetail().getSchDate());
		}

		else {
			this.disbAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
					CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		}
		// Future Disbursements not allowed at any case, because with Future Disbursements
		// there is an issue with SOA, Disbursement Uploads, Fore Closure Report..etc
		if (!isWIF) {
			this.fromDate.setValue(appDate);
			this.fromDate.setDisabled(true);
		}

		String excludeFields = ",EQUAL,PRI_PFT,PRI,POSINT,";
		String nonGrcExclFields = ",GRCNDPAY,PFTCAP,";
		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				aFinSchData.getFinanceMain().getProductCategory())) {
			nonGrcExclFields = ",GRCNDPAY,PFTCAP,POSINT,";
		}
		if (getFinanceScheduleDetail() != null) {
			if (getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE)
					|| getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)) {
				fillComboBox(this.cbSchdMthd, getFinanceScheduleDetail().getSchdMethod(),
						PennantStaticListUtil.getScheduleMethods(), excludeFields);
				this.cbSchdMthd.setDisabled(true);

			} else {
				fillComboBox(this.cbSchdMthd, getFinanceScheduleDetail().getSchdMethod(),
						PennantStaticListUtil.getScheduleMethods(), nonGrcExclFields);
				this.cbSchdMthd.setDisabled(true);
			}
		} else {
			fillComboBox(this.cbSchdMthd, "", PennantStaticListUtil.getScheduleMethods(), nonGrcExclFields);
			this.cbSchdMthd.setDisabled(true);
		}

		// Check if schedule header is null or not and set the recal type
		// fields.
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinSchData.getFinanceMain().getProductCategory())
				|| aFinSchData.getFinanceType().isDeveloperFinance()) {
			this.reCalTypeRow.setVisible(false);
			this.fromDateRow.setVisible(false);
			this.schdMthdRow.setVisible(false);
		} else {

			String exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,";
			boolean isApplySanctionBasedSchd = SanctionBasedSchedule.isApplySanctionBasedSchedule(aFinSchData);

			if (isApplySanctionBasedSchd) {
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,TILLDATE,";
			}

			boolean isStepPOS = false;
			if (aFinSchData.getFinanceMain().isStepFinance() && aFinSchData.getFinanceMain().isAllowGrcPeriod()
					&& StringUtils.equals(aFinSchData.getFinanceMain().getStepType(), FinanceConstants.STEPTYPE_PRIBAL)
					&& (StringUtils.equals(aFinSchData.getFinanceMain().getScheduleMethod(),
							CalculationConstants.SCHMTHD_PRI)
							|| StringUtils.equals(aFinSchData.getFinanceMain().getScheduleMethod(),
									CalculationConstants.SCHMTHD_PRI_PFT))) {
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,";
				isStepPOS = true;
			}

			if (aFinSchData.getFinanceMain().isSanBsdSchdle()) {
				exclRecalTypes = exclRecalTypes.concat("ADDRECAL,");
			}

			if (isStepPOS) {
				fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_STEPPOS,
						PennantStaticListUtil.getDisbCalCodes(), exclRecalTypes);
			} else {
				fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
						PennantStaticListUtil.getDisbCalCodes(), exclRecalTypes);
			}

			if (StringUtils.equals(getFinScheduleData().getFinanceMain().getRecalType(),
					CalculationConstants.RPYCHG_TILLDATE)) {

				fillSchDates(this.cbFromDate, aFinSchData, null);
				fillSchDates(this.cbTillDate, aFinSchData, null);
				this.fromDateRow.setVisible(true);
				this.tillDateRow.setVisible(true);
				this.label_AddDisbursementDialog_TillDate
						.setValue(Labels.getLabel("label_AddDisbursementDialog_TillDate.value"));

			} else if (StringUtils.equals(aFinSchData.getFinanceMain().getRecalType(),
					CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(aFinSchData.getFinanceMain().getRecalType(),
							CalculationConstants.RPYCHG_ADDRECAL)) {

				fillSchDates(cbFromDate, getFinScheduleData(), null);
				this.label_AddDisbursementDialog_TillFromDate
						.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
				this.fromDateRow.setVisible(true);
				this.cbFromDate.setSelectedIndex(0);
			} else {
				fillSchDates(this.cbFromDate, aFinSchData, null);
			}
			changeRecalType();
		}

		// We are setting default Value on From Date. So on changing event should be calculate to reset remaining fields
		if (!isWIF) {
			Events.sendEvent("onChange", fromDate, null);
		}

		logger.debug("Leaving");
	}

	private void processStepLoans(FinanceMain fm) {
		boolean isStepLoan = false;
		Date appDate = SysParamUtil.getAppDate();

		if (fm.isStepFinance()) {
			if (StringUtils.isNotBlank(fm.getStepPolicy()) || (fm.isAlwManualSteps() && fm.getNoOfSteps() > 0)) {
				isStepLoan = true;
			}
		}

		if (!isStepLoan) {
			return;
		}

		readOnlyComponent(true, this.cbReCalType);
		readOnlyComponent(true, cbFromDate);
		Comboitem comboitem = new Comboitem();

		FinScheduleData schdData = getFinScheduleData();
		List<RepayInstruction> rpst = schdData.getRepayInstructions();
		Date recalFromDate = null;

		if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
			comboitem.setValue(CalculationConstants.RPYCHG_STEPINST);
			comboitem.setLabel(Labels.getLabel("label_" + CalculationConstants.RPYCHG_STEPINST));

			if (appDate.compareTo(fm.getGrcPeriodEndDate()) > 0) {
				RepayInstruction rins = rpst.get(rpst.size() - 1);
				recalFromDate = rins.getRepayDate();
				fm.setRecalSteps(false);
			} else {
				fm.setRecalSteps(false);
				recalFromDate = rpst.get(rpst.size() - 1).getRepayDate();
				for (RepayInstruction repayInstruction : rpst) {
					if (repayInstruction.getRepayDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
						recalFromDate = repayInstruction.getRepayDate();
						break;
					}
				}
			}
		} else if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
			List<FinanceStepPolicyDetail> spdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpyList = new ArrayList<>(1);
			List<FinanceStepPolicyDetail> grcList = new ArrayList<>(1);
			FinanceStepPolicyDetail rpyStp = null;

			for (FinanceStepPolicyDetail spd : schdData.getStepPolicyDetails()) {
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
				fm.setRpyStps(true);
			}

			if (CollectionUtils.isNotEmpty(grcList)) {
				Collections.sort(grcList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
						: step1.getStepNo() < step2.getStepNo() ? -1 : 0);
				fm.setGrcStps(true);
			}

			spdList.addAll(grcList);
			spdList.addAll(rpyList);
			schdData.setStepPolicyDetails(spdList);
			List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
			int fsdSize = fsdList.size();
			FinanceScheduleDetail fsd = schdData.getFinanceScheduleDetails().get(fsdSize - 1);
			fm.setAdjTerms(0);

			String recalType = CalculationConstants.RPYCHG_STEPINST;
			if (fsd.getSchDate().compareTo(rpyStp.getStepEnd()) != 0) {
				comboitem.setValue(recalType);
				comboitem.setLabel(Labels.getLabel("label_" + recalType));
				fm.setRecalType(recalType);
				int months = DateUtil.getMonthsBetween(fsd.getSchDate(), rpyStp.getStepEnd());
				fm.setAdjTerms(months);
				fm.setRecalToDate(fm.getMaturityDate());
				for (FinanceScheduleDetail finSch : fsdList) {
					if (finSch.getSchDate().compareTo(appDate) > 0) {
						recalFromDate = finSch.getSchDate();
						break;
					}
				}

			} else {
				comboitem.setValue(recalType);
				comboitem.setLabel(Labels.getLabel("label_" + recalType));
				fm.setRecalType(recalType);
				recalFromDate = rpyStp.getStepStart();
				fm.setRecalToDate(fm.getMaturityDate());
			}

			if (ImplementationConstants.ALLOW_STEP_RECAL_PRORATA) {
				fm.setStepRecalOnProrata(true);
				for (FinanceScheduleDetail finSch : fsdList) {
					if (DateUtil.compare(finSch.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
						continue;
					}
					if (!finSch.isRepayOnSchDate()) {
						continue;
					}
					if (finSch.getSchDate().compareTo(appDate) > 0) {
						recalFromDate = finSch.getSchDate();
						break;
					}
				}
			}
		}

		this.cbReCalType.appendChild(comboitem);
		this.cbReCalType.setSelectedItem(comboitem);

		fm.setRecalFromDate(recalFromDate);
		comboitem = new Comboitem();
		comboitem.setLabel(DateUtil.formatToLongDate(recalFromDate));
		comboitem.setValue(recalFromDate);
		cbFromDate.appendChild(comboitem);
		cbFromDate.setSelectedItem(comboitem);
		this.label_AddDisbursementDialog_TillFromDate
				.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
		this.fromDateRow.setVisible(true);
		this.numOfTermsRow.setVisible(false);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doWriteComponentsToBean(FinScheduleData aFinScheduleData)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		FinanceMain finMain = aFinScheduleData.getFinanceMain();
		int formatter = CurrencyUtil.getFormat(finMain.getFinCcy());
		boolean isOverdraft = false;
		boolean isDevFinance = false;
		boolean isStepLoan = false;
		if (finMain.isStepFinance()) {
			if (StringUtils.isNotBlank(finMain.getStepPolicy())
					|| (finMain.isAlwManualSteps() && finMain.getNoOfSteps() > 0)) {
				isStepLoan = true;
			}
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finMain.getProductCategory())) {
			isOverdraft = true;
		}
		if (aFinScheduleData.getFinanceType().isDeveloperFinance()) {
			isDevFinance = true;
		}

		isDisbDetailError = false;
		boolean isValidDate = true;
		Date maturityDate = finMain.getMaturityDate();
		Date recalFrom = finMain.getFinStartDate();
		try {

			// Closing Balance Maturity Date
			int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
			if (!isOverdraft && !isDevFinance && !finMain.isSanBsdSchdle()) {
				for (int i = sdSize - 1; i > 0; i--) {
					FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);
					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) != 0) {
						break;
					}
					maturityDate = curSchd.getSchDate();
				}
			}
			if (isDevFinance) {
				for (int i = 0; i <= sdSize - 1; i++) {
					FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);
					if (DateUtil.compare(curSchd.getSchDate(), this.fromDate.getValue()) <= 0) {
						continue;
					}
					recalFrom = curSchd.getSchDate();
					break;
				}
			}

			if (DateUtil.compare(this.fromDate.getValue(), appDate) < 0
					|| DateUtil.compare(this.fromDate.getValue(), maturityDate) >= 0
							&& !this.fromDate.isReadonly()) {
				isValidDate = false;
				throw new WrongValueException(this.fromDate,
						Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
								new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
										DateUtil.formatToLongDate(appDate),
										DateUtil.formatToLongDate(maturityDate) }));
			}
			if ((DateUtil.compare(this.fromDate.getValue(), lastPaidDate) <= 0
					|| DateUtil.compare(this.fromDate.getValue(), maturityDate) >= 0)
					&& DateUtil.compare(this.fromDate.getValue(), finMain.getFinStartDate()) != 0
					&& !this.fromDate.isReadonly()) {
				isValidDate = false;
				throw new WrongValueException(this.fromDate,
						Labels.getLabel("DATE_ALLOWED_RANGE",
								new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
										DateUtil.formatToLongDate(lastPaidDate),
										DateUtil.formatToLongDate(maturityDate) }));
			}

			// Last Disbursement Date
			if (lastDisbDate != null && DateUtil.compare(this.fromDate.getValue(), lastDisbDate) < 0) {
				isValidDate = false;
				throw new WrongValueException(this.fromDate,
						Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
								new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
										DateUtil.formatToLongDate(lastDisbDate) }));
			}

			finMain.setEventFromDate(this.fromDate.getValue());
			finServiceInstruction.setFromDate(this.fromDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.disbAmount.getValidateValue();
			finServiceInstruction
					.setAmount(PennantApplicationUtil.unFormateAmount(this.disbAmount.getValidateValue(), formatter));

			if (isOverdraft && isValidDate) {
				OverdraftLimit limit = overdrafLoanService.getLimit(finMain.getFinID());
				BigDecimal monthlyLmtBal = limit.getMonthlyLimitBal();
				BigDecimal actualLmtBal = limit.getActualLimitBal();
				BigDecimal feeAmount = BigDecimal.ZERO;
				BigDecimal disbAmt = PennantApplicationUtil.unFormateAmount(this.disbAmount.getActualValue(),
						formatter);

				if (FinanceConstants.FIXED_AMOUNT.equals(finMain.getOverdraftCalcChrg())) {
					feeAmount = finMain.getOverdraftChrgAmtOrPerc();
				} else {
					BigDecimal feePercent = finMain.getOverdraftChrgAmtOrPerc().divide(new BigDecimal(100),
							RoundingMode.HALF_DOWN);
					feeAmount = PennantApplicationUtil.getPercentageValue(disbAmt, feePercent);
				}

				if (monthlyLmtBal != null || actualLmtBal != null) {
					disbAmt = disbAmt.add(feeAmount);
					if (disbAmt.compareTo(monthlyLmtBal) > 0) {
						throw new WrongValueException(this.disbAmount.getCcyTextBox(),
								Labels.getLabel("od_MonthlyLimit_Validation_Maxvalue"));
					}
					if (disbAmt.compareTo(actualLmtBal) > 0) {
						throw new WrongValueException(this.disbAmount.getCcyTextBox(),
								Labels.getLabel("od_ActualLimit_Validation_Maxvalue"));
					}
				}

				if (StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT,
						aFinScheduleData.getFinanceType().getFinSchdMthd())
						&& !aFinScheduleData.getFinanceType().isDroplineOD()) {
					List<FinanceScheduleDetail> schList = aFinScheduleData.getFinanceScheduleDetails();
					BigDecimal outstandingBal = BigDecimal.ZERO;
					for (int i = 0; i < schList.size(); i++) {
						FinanceScheduleDetail curSchd = schList.get(i);
						outstandingBal = outstandingBal.add(curSchd.getDisbAmount().subtract(curSchd.getSchdPriPaid()));
					}

					if ((finMain.getFinAssetValue().subtract(outstandingBal)).compareTo(
							PennantApplicationUtil.unFormateAmount(this.disbAmount.getActualValue(), formatter)) < 0) {
						throw new WrongValueException(this.disbAmount.getCcyTextBox(),
								Labels.getLabel("od_DisAmountExceeded", new String[] {}));
					}

				} else {
					// Checking against adding disbursement date available limit
					List<OverdraftScheduleDetail> odSchdDetail = aFinScheduleData.getOverdraftScheduleDetails();
					BigDecimal fromDateAvailLimit = BigDecimal.ZERO;
					if (odSchdDetail != null && odSchdDetail.size() > 0) {
						for (int i = 0; i < odSchdDetail.size(); i++) {
							if (odSchdDetail.get(i).getDroplineDate().compareTo(this.fromDate.getValue()) > 0) {
								break;
							}
							fromDateAvailLimit = odSchdDetail.get(i).getODLimit();
						}

						// Schedule Outstanding amount calculation
						List<FinanceScheduleDetail> schList = aFinScheduleData.getFinanceScheduleDetails();
						BigDecimal closingbal = BigDecimal.ZERO;
						for (int i = 0; i < schList.size(); i++) {
							if (DateUtil.compare(schList.get(i).getSchDate(), this.fromDate.getValue()) > 0) {
								break;
							}
							closingbal = schList.get(i).getClosingBalance();
						}

						// Actual Available Limit
						fromDateAvailLimit = fromDateAvailLimit.subtract(closingbal);
					}

					// Validating against Available Limit amount
					if (this.disbAmount.getValidateValue()
							.compareTo(PennantApplicationUtil.formateAmount(fromDateAvailLimit, formatter)) > 0) {
						if (fromDateAvailLimit.compareTo(BigDecimal.ZERO) > 0) {
							throw new WrongValueException(this.disbAmount.getCcyTextBox(),
									Labels.getLabel("od_DisbAmount_Validation_RANGE", new String[] {
											PennantApplicationUtil.amountFormate(fromDateAvailLimit, formatter) }));
						} else {
							throw new WrongValueException(this.disbAmount.getCcyTextBox(),
									Labels.getLabel("od_DisbAmount_Validation", new String[] {}));
						}
					} else {

						// Add Disbursement Limit Checking
						List<FinanceScheduleDetail> schList = aFinScheduleData.getFinanceScheduleDetails();
						BigDecimal totPriBal = BigDecimal.ZERO;
						BigDecimal avalLimit = BigDecimal.ZERO;
						BigDecimal sanctionAmt = aFinScheduleData.getFinanceMain().getFinAssetValue();
						Date appDate = SysParamUtil.getAppDate();

						if (CollectionUtils.isNotEmpty(schList)) {
							for (FinanceScheduleDetail curSchd : schList) {
								totPriBal = totPriBal.add(curSchd.getDisbAmount().subtract(curSchd.getSchdPriPaid()));
								if (DateUtil.compare(curSchd.getSchDate(), appDate) > 0) {
									break;
								}
							}
						}

						avalLimit = sanctionAmt.subtract(totPriBal);
						BigDecimal availableAmount = PennantApplicationUtil.formateAmount(avalLimit, formatter);
						if (availableAmount.compareTo(BigDecimal.ZERO) < 0) {
							availableAmount = BigDecimal.ZERO;
						}

						if (this.disbAmount.getValidateValue().compareTo(availableAmount) > 0) {
							throw new WrongValueException(this.disbAmount,
									Labels.getLabel("label_AddDisbursementDialog_Amount.value")
											+ " Should be less than or equal to "
											+ PennantApplicationUtil.amountFormate(avalLimit, formatter));
						}

					}

					// Checking total Disbursement amounts against available
					// limit to add in Current Disbursement
					List<FinanceScheduleDetail> finSched = aFinScheduleData.getFinanceScheduleDetails();
					BigDecimal avalLimit = finMain.getFinAssetValue();
					for (int i = 1; i < finSched.size() - 1; i++) {

						if (!finSched.get(i).isDisbOnSchDate()) {
							continue;
						} else {

							List<OverdraftScheduleDetail> odDetail = aFinScheduleData.getOverdraftScheduleDetails();
							for (int j = 0; j < odDetail.size() - 1; j++) {

								if (DateUtil.compare(odDetail.get(j).getDroplineDate(),
										finSched.get(i).getSchDate()) <= 0
										&& DateUtil.compare(odDetail.get(j + 1).getDroplineDate(),
												finSched.get(i).getSchDate()) > 0) {
									avalLimit = odDetail.get(j).getODLimit()
											.subtract(finSched.get(i).getClosingBalance());
									break;
								}
							}
						}

						// Available Limit Checking against whole period
						if (avalLimit.compareTo(BigDecimal.ZERO) == 0) {
							throw new WrongValueException(this.disbAmount.getCcyTextBox(),
									Labels.getLabel("od_DisbAmount_Validation", new String[] {}));
						} else if (avalLimit.compareTo(BigDecimal.ZERO) > 0 && (avalLimit.subtract(
								PennantApplicationUtil.unFormateAmount(this.disbAmount.getValidateValue(), formatter)))
										.compareTo(BigDecimal.ZERO) < 0) {
							throw new WrongValueException(this.disbAmount.getCcyTextBox(), Labels.getLabel(
									"od_DisbAmount_Validation_Maxvalue",
									new String[] { PennantApplicationUtil.amountFormate(avalLimit, formatter) }));
						}
					}
				}
				// Allow revolving checking
			} else if (finMain.isAllowRevolving()) {
				BigDecimal avalLimit = finMain.getFinAssetValue().subtract(finMain.getFinCurrAssetValue())
						.add(finMain.getFinRepaymentAmount());
				BigDecimal disbursementAmt = PennantApplicationUtil.unFormateAmount(this.disbAmount.getActualValue(),
						formatter);
				if (disbursementAmt.compareTo(avalLimit) > 0) {
					throw new WrongValueException(this.disbAmount.getCcyTextBox(),
							Labels.getLabel("od_DisbAmount_Validation_Maxvalue",
									new String[] { PennantApplicationUtil.amountFormate(avalLimit, formatter) }));
				}
			}

			int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
			if (finMain.isAllowGrcPeriod() && finMain.isEndGrcPeriodAftrFullDisb()) {
				boolean notAlwBefGrcStrDate = false;
				Date grcStartDate = null;
				for (int i = 0; i <= sdSize - 1; i++) {
					FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);
					if (curSchd.getInstNumber() == 1
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
						grcStartDate = curSchd.getSchDate();
						break;
					}
				}

				if (grcStartDate != null && DateUtil.compare(grcStartDate, this.fromDate.getValue()) > 0) {
					notAlwBefGrcStrDate = true;
				}

				if (notAlwBefGrcStrDate) {
					BigDecimal prvTotDisbValue = BigDecimal.ZERO;
					for (FinanceDisbursement curDisb : aFinScheduleData.getDisbursementDetails()) {
						if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
							continue;
						}

						if (curDisb.getLinkedDisbId() != 0) {
							continue;
						}

						prvTotDisbValue = prvTotDisbValue.add(curDisb.getDisbAmount());
					}
					BigDecimal curTotDisbValue = CurrencyUtil.unFormat(this.disbAmount.getValidateValue(), formatter)
							.add(prvTotDisbValue);

					if (curTotDisbValue.compareTo(finMain.getFinAssetValue()) == 0) {
						isValidDate = false;
						throw new WrongValueException(this.fromDate,
								Labels.getLabel("DATE_ALLOWED_RANGE",
										new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
												DateUtil.formatToLongDate(grcStartDate),
												DateUtil.formatToLongDate(maturityDate) }));
					}
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbSchdMthd, Labels.getLabel("label_AddDisbursementDialog_SchdMthd.value"))
					&& this.cbSchdMthd.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.cbSchdMthd));
				// finMain.setRecalSchdMethod(getComboboxValue(this.cbSchdMthd));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setQuickDisb(this.qDP.isChecked());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.reCalTypeRow.isVisible() && !PennantConstants.STEPPING_CALC_AMT.equals(finMain.getCalcOfSteps())) {
				if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_AddDisbursementDialog_RecalType.value"))
						&& this.cbReCalType.getSelectedIndex() != 0) {
					finMain.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
					finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.fromDateRow.isVisible()) {
			try {
				if (this.cbFromDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value") }));
				}
				if (this.fromDate.getValue() != null && ((Date) this.cbFromDate.getSelectedItem().getValue())
						.compareTo(this.fromDate.getValue()) <= 0) {

					throw new WrongValueException(this.cbFromDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
											DateUtil.formatToLongDate(this.fromDate.getValue()) }));
				}
				finServiceInstruction.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.tillDateRow.isVisible()) {
			try {
				if (this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_TillDate.value") }));
				}

				if (this.cbFromDate.getSelectedIndex() > 0 && ((Date) this.cbTillDate.getSelectedItem().getValue())
						.compareTo((Date) this.cbFromDate.getSelectedItem().getValue()) < 0) {

					throw new WrongValueException(this.cbTillDate, Labels.getLabel("DATE_ALLOWED_AFTER", new String[] {
							Labels.getLabel("label_AddDisbursementDialog_TillDate.value"),
							DateUtil.formatToLongDate((Date) this.cbFromDate.getSelectedItem().getValue()) }));
				}

				if ((this.fromDate.getValue() != null && ((Date) this.cbTillDate.getSelectedItem().getValue())
						.compareTo(this.fromDate.getValue()) < 0)
						|| (((Date) this.cbTillDate.getSelectedItem().getValue())
								.compareTo(this.fromDate.getValue()) == 0)) {

					throw new WrongValueException(this.cbTillDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_AddDisbursementDialog_TillDate.value"),
											DateUtil.formatToLongDate((Date) this.fromDate.getValue()) }));
				}

				// throw Exception if the selected schedule in To Date is having profit balance
				if (this.cbTillDate.getSelectedItem().getAttribute("pftBal") != null) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("Label_finSchdTillDate"));
				}
				finServiceInstruction.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// For step loan adj terms setting in processStepLoans method if steps calculated on amount
		if (!(isStepLoan && PennantConstants.STEPPING_CALC_AMT.equals(finMain.getCalcOfSteps()))) {
			finMain.setAdjTerms(0);
		}
		if (this.numOfTermsRow.isVisible()) {
			try {
				if (this.adjTerms.intValue() <= 0) {
					throw new WrongValueException(this.adjTerms, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_ChangeRepaymentDialog_Terms.value") }));
				}
				finMain.setAdjTerms(this.adjTerms.intValue());
				finServiceInstruction.setTerms(this.adjTerms.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			if (this.alwAssetUtilize.isChecked()) {
				if (grcEndDisbAmount.compareTo(
						PennantApplicationUtil.unFormateAmount(this.disbAmount.getActualValue(), formatter)) < 0) {
					throw new WrongValueException(this.disbAmount, Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
							Labels.getLabel("label_AddDisbursementDialog_Amount.value"),
							PennantApplicationUtil.amountFormate(grcEndDisbAmount,
									CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())) }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			showErrorDetails(wve, disbursementDetailsTab);
		}

		BigDecimal prevDisbAmt = BigDecimal.ZERO;
		BigDecimal disbAmt = PennantApplicationUtil.unFormateAmount(disbAmount.getValidateValue(), formatter);

		for (FinanceDisbursement curDisb : aFinScheduleData.getDisbursementDetails()) {
			prevDisbAmt = prevDisbAmt.add(curDisb.getDisbAmount());
		}

		if (finMain.isAllowGrcPeriod() && finMain.isEndGrcPeriodAftrFullDisb()
				&& DateUtil.compare(finMain.getFinStartDate(), this.fromDate.getValue()) == 0
				&& DateUtil.compare(finMain.getGrcPeriodEndDate(), this.fromDate.getValue()) == 1
				&& finMain.getFinAssetValue().compareTo(disbAmt.add(prevDisbAmt)) == 0) {
			MessageUtil.showError(
					"Full disbursement is not allowed if the loan start date and disbursement date are the same and with in the grace period");
			return;
		}

		// Manual Schedule Validations
		if (finMain.isManualSchedule()) {
			ManualScheduleHeader scheduleHeader = aFinScheduleData.getManualScheduleHeader();

			List<ManualScheduleDetail> details = null;

			if (scheduleHeader != null) {
				details = scheduleHeader.getManualSchedules();
			}

			// Validate Manual Schedule Upload Data
			if (CollectionUtils.isEmpty(details) || !scheduleHeader.isValidSchdUpload()) {

				this.isDisbDetailError = true;
				MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
				Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
				return;
			}

			// Validate Total Uploaded Principal Amount with Current POS and
			// Additional Disbursement Amount
			if (scheduleHeader != null && (scheduleHeader.getTotPrincipleAmt()
					.compareTo(this.disbAmount.getValidateValue().add(scheduleHeader.getCurPOSAmt())) != 0)) {

				MessageUtil.showError(Labels.getLabel("PRIAMT_FINAMT_DISBAMT_NOTMATCH"));
				this.disbursementDetailsTab.setSelected(true);
				isDisbDetailError = true;
				return;
			}

			// Validate DisbDate with First Manual Schedule Date
			if (details.get(0).getSchDate().compareTo(this.fromDate.getValue()) <= 0) {
				MessageUtil.showError(Labels.getLabel("DISB_SCHD_DATE"));
				this.disbursementDetailsTab.setSelected(true);
				isDisbDetailError = true;
				return;
			}
		}

		if (this.alwAssetUtilize.isChecked()) {
			List<FinanceDisbursement> list = aFinScheduleData.getDisbursementDetails();
			for (int i = 0; i < list.size(); i++) {
				FinanceDisbursement disbursement = list.get(i);
				if (this.fromDate.getValue().compareTo(finMain.getGrcPeriodEndDate()) <= 0) {
					if (grcEndDisbAmount.compareTo(finServiceInstruction.getAmount()) == 0) {
						list.remove(i);
						break;
					}
				}
				if (disbursement.getDisbDate().compareTo(finMain.getGrcPeriodEndDate()) == 0
						&& disbursement.getDisbDate().compareTo(finMain.getFinStartDate()) != 0) {
					if (grcEndDisbAmount.compareTo(finServiceInstruction.getAmount()) == 0) {
						list.remove(i);
					} else {
						disbursement.setDisbAmount(
								disbursement.getDisbAmount().subtract(finServiceInstruction.getAmount()));
					}
					break;
				}
			}
			aFinScheduleData.setDisbursementDetails(list);
		}

		boolean posIntProcess = false;
		if (StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT,
				aFinScheduleData.getFinanceType().getFinSchdMthd())) {

			Date startDate = this.fromDate.getValue();
			if (DateUtil.compare(this.fromDate.getValue(),
					aFinScheduleData.getFinanceMain().getFinStartDate()) != 0) {
				startDate = DateUtil.addDays(this.fromDate.getValue(), -1);
			}

			maturityDate = FrequencyUtil.getNextDate(aFinScheduleData.getFinanceMain().getRepayFrq(), 1, startDate,
					HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
			posIntProcess = true;
		}

		aFinScheduleData.getFinanceMain().setEventToDate(maturityDate);
		finServiceInstruction.setToDate(maturityDate);

		finMain.setCurDisbursementAmt(finServiceInstruction.getAmount());
		BigDecimal addingFeeToFinance = BigDecimal.ZERO;

		if (isOverdraft) {

			if (posIntProcess) {
				finMain.setRecalType(CalculationConstants.RPYCHG_TILLDATE);
				finMain.setRecalFromDate(maturityDate);
				finMain.setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI);

				finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_TILLDATE);
				finServiceInstruction.setRecalFromDate(maturityDate);

				// Schedule Details
				List<FinanceScheduleDetail> schList = aFinScheduleData.getFinanceScheduleDetails();
				Date schDateAfterCurInst = null;
				for (int i = 0; i < schList.size(); i++) {

					// Schedule Date Finding after new disbursement Date & before Maturity Date
					if (DateUtil.compare(schList.get(i).getSchDate(), maturityDate) > 0) {
						schDateAfterCurInst = schList.get(i).getSchDate();
						break;
					}

					if (DateUtil.compare(schList.get(i).getSchDate(), maturityDate) == 0) {
						schDateAfterCurInst = null;
						schList.get(i).setPftOnSchDate(true);
						schList.get(i).setRepayOnSchDate(true);
					}
				}

				// Repay Instructions Setting
				boolean rpyInstFound = false;
				boolean futureRpyInst = false;
				List<RepayInstruction> rpyInstructions = aFinScheduleData.getRepayInstructions();
				for (int i = 0; i < rpyInstructions.size(); i++) {
					if (DateUtil.compare(maturityDate, rpyInstructions.get(i).getRepayDate()) == 0) {
						rpyInstructions.get(i).setRepayAmount(
								rpyInstructions.get(i).getRepayAmount().add(finServiceInstruction.getAmount()));
						rpyInstFound = true;
					} else if (DateUtil.compare(maturityDate, rpyInstructions.get(i).getRepayDate()) < 0) {
						futureRpyInst = true;
						break;
					}
				}

				// If instruction not found then add with Disbursement amount
				if (!rpyInstFound) {
					RepayInstruction ri = new RepayInstruction();
					ri.setRepayDate(maturityDate);
					ri.setRepayAmount(finServiceInstruction.getAmount());
					ri.setRepaySchdMethod(CalculationConstants.SCHMTHD_PRI);
					aFinScheduleData.getRepayInstructions().add(ri);
				}

				// If Schedule instruction not found then add with Zero amount
				if (!futureRpyInst && schDateAfterCurInst != null) {
					RepayInstruction ri = new RepayInstruction();
					ri.setRepayDate(schDateAfterCurInst);
					ri.setRepayAmount(BigDecimal.ZERO);
					ri.setRepaySchdMethod(CalculationConstants.SCHMTHD_PRI);
					aFinScheduleData.getRepayInstructions().add(ri);
				}

				sortRepayInstructions(aFinScheduleData.getRepayInstructions());

			} else {
				finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				// Schedule Details
				List<FinanceScheduleDetail> schList = aFinScheduleData.getFinanceScheduleDetails();
				Date schDateAfterCurInst = finMain.getMaturityDate();
				Date rpyStartDate = null;

				for (FinanceScheduleDetail schd : schList) {
					if (rpyStartDate == null && schd.isPftOnSchDate() && schd.isFrqDate()) {
						rpyStartDate = schd.getSchDate();
					}

					if (!schd.isPftOnSchDate() && !schd.isRepayOnSchDate()) {
						continue;
					}
					// Schedule Date Finding after new disbursement Date & before Maturity Date
					if (DateUtil.compare(schd.getSchDate(), this.fromDate.getValue()) > 0) {
						schDateAfterCurInst = schd.getSchDate();
						break;
					}
				}

				finMain.setRecalFromDate(schDateAfterCurInst);
				finServiceInstruction.setRecalFromDate(schDateAfterCurInst);

				// If Schedule instruction not found then add with Zero amount
				String schdMethod = aFinScheduleData.getFinanceType().getFinSchdMthd();

				if (CollectionUtils.isEmpty(aFinScheduleData.getRepayInstructions())
						&& (CalculationConstants.SCHMTHD_PFTCPZ.equals(schdMethod)
								|| CalculationConstants.SCHMTHD_PFT.equals(schdMethod))) {

					if (rpyStartDate != null) {
						RepayInstruction ri = new RepayInstruction();
						ri.setRepayDate(rpyStartDate);
						ri.setRepayAmount(BigDecimal.ZERO);
						ri.setRepaySchdMethod(schdMethod);
						aFinScheduleData.getRepayInstructions().add(ri);
					}
				}
			}

			finMain.setEventFromDate(this.fromDate.getValue());
			finServiceInstruction.setFromDate(this.fromDate.getValue());
			finMain.setRecalToDate(maturityDate);
			finServiceInstruction.setRecalToDate(maturityDate);
		}
		// TODO:Once Check
		if (isDevFinance) {
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			finMain.setEventFromDate(this.fromDate.getValue());
			finMain.setRecalFromDate(this.fromDate.getValue());
			finServiceInstruction.setRecalFromDate(this.fromDate.getValue());
			if (isDevFinance) {
				finMain.setRecalFromDate(recalFrom);
				finServiceInstruction.setRecalFromDate(recalFrom);
			}
			finMain.setRecalToDate(maturityDate);
			finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			finServiceInstruction.setFromDate(this.fromDate.getValue());
			finServiceInstruction.setRecalToDate(maturityDate);
		}

		finServiceInstruction.setFinID(finMain.getFinID());
		finServiceInstruction.setFinReference(finMain.getFinReference());
		finServiceInstruction.setFinEvent(FinServiceEvent.ADDDISB);
		finServiceInstruction.setServiceReqNo(this.serviceReqNo.getValue());
		finServiceInstruction.setRemarks(this.remarks.getValue());

		if (this.reCalTypeRow.isVisible()) {
			if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT)
					|| this.cbReCalType.getSelectedItem().getValue().toString()
							.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
				Date fromDate = (Date) this.cbFromDate.getSelectedItem().getValue();
				finMain.setRecalFromDate(fromDate);
				finMain.setRecalToDate(maturityDate);
				finServiceInstruction.setRecalFromDate(fromDate);
				finServiceInstruction.setRecalToDate(maturityDate);
			} else if (this.cbReCalType.getSelectedItem().getValue().toString()
					.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				finMain.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
				finMain.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
			} else if (isStepLoan && StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)
					&& this.cbReCalType.getSelectedItem().getValue().toString()
							.equals(CalculationConstants.RPYCHG_STEPINST)) {
				finServiceInstruction.setRecalFromDate(finMain.getRecalFromDate());
				finServiceInstruction.setRecalToDate(finMain.getRecalToDate());
				finServiceInstruction.setTerms(finMain.getAdjTerms());
				finServiceInstruction.setRecalType(finMain.getRecalType());
			} else if (this.cbReCalType.getSelectedItem().getValue().toString()
					.equals(CalculationConstants.RPYCHG_STEPINST)) {
				Date fromDate = (Date) this.cbFromDate.getSelectedItem().getValue();
				finMain.setRecalFromDate(fromDate);
				finMain.setRecalToDate(maturityDate);
				finServiceInstruction.setRecalFromDate(fromDate);
				finServiceInstruction.setRecalToDate(maturityDate);
			} else if (isStepLoan && PennantConstants.STEPPING_CALC_AMT.equals(finMain.getCalcOfSteps())
					&& CalculationConstants.RPYCHG_STEPINST
							.equals(this.cbReCalType.getSelectedItem().getValue().toString())) {
				finServiceInstruction.setRecalFromDate(finMain.getRecalFromDate());
				finServiceInstruction.setRecalToDate(finMain.getRecalToDate());
				finServiceInstruction.setTerms(finMain.getAdjTerms());
				finServiceInstruction.setRecalType(finMain.getRecalType());
			}
		}

		if (CollectionUtils.isNotEmpty(aFinScheduleData.getStepPolicyDetails())) {
			aFinScheduleData.setStepPolicyDetails(aFinScheduleData.getStepPolicyDetails(), true);
		}

		List<FinanceScheduleDetail> fsd = finScheduleData.getFinanceScheduleDetails();
		int schdDetailsSize = fsd.size();
		FinanceScheduleDetail curSchd;
		boolean priRecalFromDate = false;
		boolean priRecalToDate = false;

		for (int i = 0; i < schdDetailsSize; i++) {
			curSchd = fsd.get(i);

			if (DateUtil.compare(curSchd.getSchDate(), finMain.getRecalFromDate()) == 0
					&& FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
				priRecalFromDate = true;
			}

			if (DateUtil.compare(curSchd.getSchDate(), finMain.getRecalToDate()) == 0
					&& FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
				priRecalToDate = true;
				break;
			}
		}

		if (priRecalFromDate && priRecalToDate) {
			MessageUtil.showError("Unable to Perform Add Disbursment Due to Principal Holidays");
		}

		// Service details calling for Schedule calculation
		aFinScheduleData.setFinServiceInstruction(finServiceInstruction);
		aFinScheduleData = addDisbursementService.getAddDisbDetails(aFinScheduleData, finServiceInstruction.getAmount(),
				addingFeeToFinance, this.alwAssetUtilize.isChecked(), moduleDefiner);
		finServiceInstruction.setPftChg(aFinScheduleData.getPftChg());
		aFinScheduleData.getFinanceMain().resetRecalculationFields();

		// Set DisbSeq as Reference in Service Instruction
		int disbSeq = 0;
		for (int i = 0; i < aFinScheduleData.getDisbursementDetails().size(); i++) {
			FinanceDisbursement curDisb = aFinScheduleData.getDisbursementDetails().get(i);
			if (curDisb.getDisbSeq() > disbSeq) {
				disbSeq = curDisb.getDisbSeq();
			}
		}
		finServiceInstruction.setReference(String.valueOf(disbSeq));

		// Show Error Details in Schedule Maintenance
		if (aFinScheduleData.getErrorDetails() != null && !aFinScheduleData.getErrorDetails().isEmpty()) {
			MessageUtil.showError(aFinScheduleData.getErrorDetails().get(0));
			aFinScheduleData.getErrorDetails().clear();
		} else {
			aFinScheduleData.setSchduleGenerated(true);
			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(aFinScheduleData);
			}

			try {
				FinFeeDetailListCtrl detailListCtrl = (FinFeeDetailListCtrl) getScheduleDetailDialogCtrl()
						.getFinanceMainDialogCtrl().getClass().getMethod("getFinFeeDetailListCtrl")
						.invoke(getScheduleDetailDialogCtrl().getFinanceMainDialogCtrl());
				if (detailListCtrl != null) {
					detailListCtrl.doExecuteFeeCharges(true, aFinScheduleData);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				logger.error(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	protected void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		if (this.disbAmount.isVisible()) {
			this.disbAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_AddDisbursementDialog_Amount.value"), 0, true, false));
		}
		if (this.fromDate.isVisible()) {
			this.fromDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_AddDisbursementDialog_FromDate.value"), true));
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnAddDisbursement(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING + event.toString());
		if (getFinanceScheduleDetail() != null) {
			if (isDataChanged()) {
				doSave();
			} else {
				MessageUtil.showError("No Data has been changed.");
			}
		} else {
			doSave();
		}
		logger.debug(Literal.LEAVING + event.toString());
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

	protected void doSave() throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);
		doSetValidation();
		doWriteComponentsToBean(getFinScheduleData());
		if (!this.isDisbDetailError) {
			this.window_AddDisbursementDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.disbAmount.setErrorMessage("");
		this.fromDate.setErrorMessage("");
		this.cbReCalType.setErrorMessage("");
		this.adjTerms.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to clear error message
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.disbAmount.setConstraint("");
		this.fromDate.setConstraint("");
		this.cbReCalType.setConstraint("");
		this.adjTerms.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	// Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		changeRecalType();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void changeRecalType() {
		this.numOfTermsRow.setVisible(false);
		this.tillDateRow.setVisible(false);
		this.fromDateRow.setVisible(false);

		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			fillSchDates(cbFromDate, getFinScheduleData(), null);
			fillSchDates(cbTillDate, getFinScheduleData(), null);
			this.cbTillDate.setSelectedIndex(0);
			this.cbFromDate.setSelectedIndex(0);

			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					getFinScheduleData().getFinanceMain().getProductCategory())) {
				this.fromDateRow.setVisible(true);
				this.tillDateRow.setVisible(true);
			}
			this.label_AddDisbursementDialog_TillFromDate
					.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
			this.label_AddDisbursementDialog_TillDate
					.setValue(Labels.getLabel("label_AddDisbursementDialog_TillDate.value"));

		} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDTERM)
				|| this.cbReCalType.getSelectedItem().getValue().toString()
						.equals(CalculationConstants.RPYCHG_ADDRECAL)) {

			this.numOfTermsRow.setVisible(true);

			if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDRECAL)) {
				fillSchDates(cbFromDate, getFinScheduleData(), null);
				this.label_AddDisbursementDialog_TillFromDate
						.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
				this.fromDateRow.setVisible(true);
				this.cbFromDate.setSelectedIndex(0);
			}

		} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADJMDT)
				|| StringUtils.equals(this.cbReCalType.getSelectedItem().getValue().toString(),
						CalculationConstants.RPYCHG_STEPPOS)) {
			// Nothing TO DO
		} else if (this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			fillSchDates(cbFromDate, getFinScheduleData(), null);
			this.label_AddDisbursementDialog_TillFromDate
					.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
			this.fromDateRow.setVisible(true);
			// If DEFAULT_TILLMDT is Specified then Set From Date as Readonly based on the Equitas Requirement .
			int value = 1;
			boolean disableDate = false;
			try {
				value = SysParamUtil.getValueAsInt("DEFAULT_TILLMDT");
				disableDate = true;
			} catch (Exception e) {

			}
			readOnlyComponent(disableDate, cbFromDate);
			if (this.cbFromDate.getItemCount() > value + 1) {
				this.cbFromDate.setSelectedIndex(value);
			} else {
				readOnlyComponent(false, cbFromDate);
				this.cbFromDate.setSelectedIndex(0);
			}

		}
	}

	/**
	 * Method to allow to utilize asset value difference from grace end date
	 * 
	 * @param event
	 */
	public void onChange$fromDate(ForwardEvent event) {
		logger.debug(Literal.ENTERING + event.toString());

		changeFromDate(true);

		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		if (financeMain.isManualSchedule()) {
			financeMain.setEventFromDate(this.fromDate.getValue());
			if (manualScheduleDialogCtrl != null) {
				manualScheduleDialogCtrl.doWriteBeanToComponents(getFinScheduleData());
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * 
	 */
	private void changeFromDate(boolean onChange) {
		logger.debug(Literal.ENTERING);

		boolean isOverDraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}

		if (this.fromDate.getValue() != null && DateUtil.compare(this.fromDate.getValue(),
				getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) < 0) {

			this.cbSchdMthd.setDisabled(true);

			if (isOverDraft) {
				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
			} else {
				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,POSINT,");
			}

		} else {
			if (isOverDraft) {
				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(),
						PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");
			} else {
				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(),
						PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,POSINT,");
			}
			this.cbSchdMthd.setDisabled(true);
		}

		boolean isApplySanctionBasedSchd = SanctionBasedSchedule.isApplySanctionBasedSchedule(getFinScheduleData());

		// STEP POS Recalculation Type Addition Check
		if (this.fromDate.getValue() != null && DateUtil.compare(this.fromDate.getValue(),
				getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0) {

			String exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,";

			if (isApplySanctionBasedSchd) {
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,TILLDATE,";
			}

			boolean isStepPOS = false;
			if (getFinScheduleData().getFinanceMain().isStepFinance()
					&& getFinScheduleData().getFinanceMain().isAllowGrcPeriod()
					&& StringUtils.equals(getFinScheduleData().getFinanceMain().getStepType(),
							FinanceConstants.STEPTYPE_PRIBAL)
					&& (StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(),
							CalculationConstants.SCHMTHD_PRI)
							|| StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(),
									CalculationConstants.SCHMTHD_PRI_PFT))) {
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,";
				isStepPOS = true;
			}

			if (getFinScheduleData().getFinanceMain().isSanBsdSchdle()) {
				exclRecalTypes = exclRecalTypes.concat("ADDRECAL,");
			}

			if (isStepPOS) {
				fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_STEPPOS,
						PennantStaticListUtil.getDisbCalCodes(), exclRecalTypes);
			} else {
				fillComboBox(this.cbReCalType, getFinScheduleData().getFinanceMain().getRecalType(),
						PennantStaticListUtil.getDisbCalCodes(), exclRecalTypes);
			}

		} else {
			String exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,";

			if (isApplySanctionBasedSchd) {
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,TILLDATE,";
			}

			if (getFinScheduleData().getFinanceMain().isSanBsdSchdle()) {
				exclRecalTypes = exclRecalTypes.concat("ADDRECAL,");
			}
			String value = getFinScheduleData().getFinanceMain().getRecalType();
			if (StringUtils.trimToNull(value) == null && PennantStaticListUtil.getDisbCalCodes().size() == 1) {
				value = PennantStaticListUtil.getDisbCalCodes().get(0).getValue();
			}

			if (getFinScheduleData().getFinanceMain().isSanBsdSchdle()) {
				exclRecalTypes = exclRecalTypes.concat("ADDRECAL,");
			}

			fillComboBox(this.cbReCalType, value, PennantStaticListUtil.getDisbCalCodes(), exclRecalTypes);
		}

		fillSchDates(cbFromDate, getFinScheduleData(), null);
		fillSchDates(cbTillDate, getFinScheduleData(), null);

		changeRecalType();
		if (PennantConstants.YES.equalsIgnoreCase(SysParamUtil.getValueAsString("STEP_LOAN_SERVICING_REQ"))) {
			processStepLoans(getFinScheduleData().getFinanceMain());
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$cbFromDate(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.cbFromDate.getSelectedIndex() > 0) {
			fillSchDates(cbTillDate, getFinScheduleData(), (Date) this.cbFromDate.getSelectedItem().getValue());
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/** To fill schedule dates */
	private void fillSchDates(Combobox dateCombobox, FinScheduleData scheduleData, Date fillAfter) {
		logger.debug(Literal.ENTERING);

		dateCombobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		if (scheduleData.getFinanceScheduleDetails() != null) {
			boolean checkForLastPaid = true;

			List<FinanceScheduleDetail> financeScheduleDetails = scheduleData.getFinanceScheduleDetails();
			int allowedDays = SysParamUtil.getValueAsInt("FutureNotAllowedDays_Disb");

			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Check For Last Paid Date
				if (checkForLastPaid) {
					lastPaidDate = curSchd.getSchDate();
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

				checkForLastPaid = false;

				// New Disbursement Date Checking
				if (this.fromDate.getValue() != null && curSchd.getSchDate().compareTo(this.fromDate.getValue()) <= 0) {
					continue;
				}

				if (allowedDays > 0) {
					Date minValidDate = DateUtil.addDays(appDate, allowedDays);
					if (DateUtil.compare(curSchd.getSchDate(), minValidDate) < 0) {
						continue;
					}
				}

				FinanceMain financeMain = scheduleData.getFinanceMain();
				// If maturity Terms, not include in list
				if (!financeMain.isSanBsdSchdle() && curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				// Till Date to Date Setting
				if (fillAfter != null && curSchd.getSchDate().compareTo(fillAfter) <= 0) {
					continue;
				}

				if (!curSchd.isRepayOnSchDate() && !curSchd.isPftOnSchDate()) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortRepayInstructions Description: Sort Repay Instructions
	 * ________________________________________________________________________________________________________________
	 */
	private List<RepayInstruction> sortRepayInstructions(List<RepayInstruction> repayInstructions) {

		if (repayInstructions != null && repayInstructions.size() > 0) {
			Collections.sort(repayInstructions, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					return DateUtil.compare(detail1.getRepayDate(), detail2.getRepayDate());
				}
			});
		}
		return repayInstructions;
	}

	public void onSelect$uploadScheduleTab(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		doSetValidation();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * append Manual Schedule Tab
	 */
	private void appendManualScheduleTab() {
		logger.debug(Literal.ENTERING);
		Tab tab = new Tab(Labels.getLabel("label_AddDisbursementDialog_UploadScheduleDetails"));
		tab.setId(getTabID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
		tab.setVisible(true);
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

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public void setAddDisbursementService(AddDisbursementService addDisbursementService) {
		this.addDisbursementService = addDisbursementService;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public void setManualScheduleDialogCtrl(ManualScheduleDialogCtrl manualScheduleDialogCtrl) {
		this.manualScheduleDialogCtrl = manualScheduleDialogCtrl;
	}

	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}
}
