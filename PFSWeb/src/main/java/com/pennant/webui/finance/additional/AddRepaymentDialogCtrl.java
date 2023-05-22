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
 * * FileName : WIApplyChangeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SanctionBasedSchedule;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/WIAddRateChange.zul file.
 */
public class AddRepaymentDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(AddRepaymentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChangeRepaymentDialog;
	protected Combobox cbReCalType;
	protected Combobox cbRepayFromDate;
	protected Combobox cbRepayToDate;
	protected Combobox cbFromDate;
	protected Combobox cbTillDate;
	protected Combobox cbSchdMthd;
	protected CurrencyBox wIAmount;
	protected Row fromDateRow;
	protected Row tillDateRow;
	protected Row pftIntactRow;
	protected Checkbox pftIntact;
	protected Row numOfTermsRow;
	protected Intbox adjTerms;
	protected Label label_ChangeRepaymentDialog_TillToDate;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;

	private transient int overrideCount = 0;
	private transient boolean validationOn;
	private transient String frSpecifier = "";
	private transient String toSpecifier = "";

	private BigDecimal totalAlwRpyAmt = BigDecimal.ZERO;
	private transient AddRepaymentService addRepaymentService;
	private boolean appDateValidationReq = false;
	private String moduleDefiner;

	/**
	 * default constructor.<br>
	 */
	public AddRepaymentDialogCtrl() {
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
	public void onCreate$window_ChangeRepaymentDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangeRepaymentDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeScheduleDetail")) {
				setFinanceScheduleDetail((FinanceScheduleDetail) arguments.get("financeScheduleDetail"));
			} else {
				setFinanceScheduleDetail(null);
			}

			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq = (boolean) arguments.get("appDateValidationReq");
			}

			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) arguments.get("moduleDefiner");
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

			boolean applySanctionCheck = SanctionBasedSchedule.isApplySanctionBasedSchedule(getFinScheduleData());
			getFinScheduleData().getFinanceMain().setApplySanctionCheck(applySanctionCheck);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ChangeRepaymentDialog.onClose();
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
			this.window_ChangeRepaymentDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cbRepayFromDate.setDisabled(true);
		this.cbRepayToDate.setDisabled(true);
		this.wIAmount.setDisabled(true);
		this.cbReCalType.setDisabled(true);
		this.cbTillDate.setDisabled(true);
		this.cbFromDate.setDisabled(true);
		this.cbSchdMthd.setDisabled(true);
		this.adjTerms.setReadonly(true);
		this.pftIntact.setDisabled(true);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		this.wIAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.wIAmount.setScale(format);
		this.wIAmount.setTextBoxWidth(150);

		this.adjTerms.setMaxlength(2);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");

	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeRepay(Event event) throws InterruptedException {
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
		this.window_ChangeRepaymentDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		fillSchFromDates(this.cbRepayFromDate, aFinSchData.getFinanceScheduleDetails());

		if (getFinanceScheduleDetail() != null) {
			fillSchToDates(this.cbRepayToDate, aFinSchData.getFinanceScheduleDetails(),
					getFinanceScheduleDetail().getSchDate(), true);
		} else {
			fillSchToDates(this.cbRepayToDate, aFinSchData.getFinanceScheduleDetails(),
					aFinSchData.getFinanceMain().getFinStartDate(), true);
		}
		if (aFinSchData.getFinanceType().isFinPftUnChanged()) {
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		} else {
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}

		Date startDate = aFinSchData.getFinanceMain().getFinStartDate();
		if (getFinanceScheduleDetail() != null) {
			startDate = getFinanceScheduleDetail().getSchDate();
		}

		if (StringUtils.equals(aFinSchData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			fillSchToDates(this.cbFromDate, aFinSchData.getFinanceScheduleDetails(), startDate, false);
			fillSchToDates(this.cbTillDate, aFinSchData.getFinanceScheduleDetails(), startDate, false);

			this.fromDateRow.setVisible(true);
			this.tillDateRow.setVisible(true);

		} else if (StringUtils.equals(aFinSchData.getFinanceMain().getRecalType(),
				CalculationConstants.RPYCHG_TILLMDT)) {
			fillSchToDates(this.cbFromDate, aFinSchData.getFinanceScheduleDetails(), startDate, false);

			this.fromDateRow.setVisible(true);
		}

		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		int format = CurrencyUtil.getFormat(aFinSchData.getFinanceMain().getFinCcy());
		if (getFinanceScheduleDetail() != null) {
			if (getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE)) {
				this.wIAmount.setValue(CurrencyUtil.parse(getFinanceScheduleDetail().getPrincipalSchd(), format));
			} else {
				this.wIAmount.setValue(CurrencyUtil.parse(getFinanceScheduleDetail().getRepayAmount(), format));
			}
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
				this.cbSchdMthd.setDisabled(false);
				this.wIAmount.setDisabled(true);
			} else {
				fillComboBox(this.cbSchdMthd, getFinanceScheduleDetail().getSchdMethod(),
						PennantStaticListUtil.getScheduleMethods(), nonGrcExclFields);
				this.cbSchdMthd.setDisabled(true);
				this.wIAmount.setDisabled(false);
			}
		} else {
			fillComboBox(this.cbSchdMthd, "", PennantStaticListUtil.getScheduleMethods(), nonGrcExclFields);
			this.cbSchdMthd.setDisabled(true);
		}

		if (this.cbSchdMthd.getSelectedItem().getValue().toString().equals(CalculationConstants.SCHMTHD_PFTCAP)) {
			this.wIAmount.setMandatory(true);
			this.wIAmount.setValue(BigDecimal.ZERO);
		}

		// Check if schedule header is null or not and set the recal type fields.
		if (aFinSchData.getFinanceMain().isApplySanctionCheck()) {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
					PennantStaticListUtil.getSchCalCodes(),
					",TILLMDT,TILLDATE,ADDRECAL,ADDLAST,ADJTERMS,CURPRD,ADDTERM,STEPPOS,");
		} else {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
					PennantStaticListUtil.getSchCalCodes(), ",ADDLAST,ADJTERMS,CURPRD,STEPPOS,");// ,ADDTERM
		}

		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
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

				if (FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
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
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());

				dateCombobox.appendChild(comboitem);
				if (getFinanceScheduleDetail() != null) {
					if (curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	public void fillSchToDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails,
			Date fillAfter, boolean includeFromDate) {
		logger.debug("Entering");

		boolean isSnctBsdSchd = getFinScheduleData().getFinanceMain().isSanBsdSchdle();

		if ("cbRepayToDate".equals(dateCombobox.getId())) {
			this.cbRepayToDate.getItems().clear();
		} else if ("cbTillDate".equals(dateCombobox.getId())) {
			this.cbTillDate.getItems().clear();
		} else if ("cbFromDate".equals(dateCombobox.getId())) {
			this.cbFromDate.getItems().clear();
		}
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				FinanceMain financeMain = getFinScheduleData().getFinanceMain();

				if (i == 0 || i == financeScheduleDetails.size() - 1) {
					continue;
				}

				if ("cbRepayToDate".equals(dateCombobox.getId())) {
					if (fillAfter.compareTo(financeMain.getGrcPeriodEndDate()) <= 0
							&& curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
						continue;
					}
				} else {
					if (financeMain.getGrcPeriodEndDate().compareTo(curSchd.getSchDate()) >= 0) {
						continue;
					}
				}

				if (FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
					continue;
				}

				// If maturity Terms, not include in list
				if (!isSnctBsdSchd && curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (curSchd.isRvwOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isCpzOnSchDate()
						&& !curSchd.isPftOnSchDate()) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setAttribute("toSpecifier", curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				if (includeFromDate && curSchd.getSchDate().compareTo(fillAfter) >= 0) {
					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null
							&& curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else if (!includeFromDate && curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doClearMessages() {
		this.cbReCalType.setConstraint("");
		this.cbRepayFromDate.setConstraint("");
		this.cbRepayToDate.setConstraint("");
		this.cbFromDate.setConstraint("");
		this.cbTillDate.setConstraint("");
		this.cbSchdMthd.setConstraint("");
		this.wIAmount.setConstraint("");
		this.adjTerms.setConstraint("");

		this.cbReCalType.setErrorMessage("");
		this.cbRepayFromDate.setErrorMessage("");
		this.cbRepayToDate.setErrorMessage("");
		this.cbFromDate.setErrorMessage("");
		this.cbTillDate.setErrorMessage("");
		this.cbSchdMthd.setErrorMessage("");
		this.wIAmount.setErrorMessage("");
		this.adjTerms.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		doClearMessages();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		finServiceInstruction.setFinID(finMain.getFinID());
		finServiceInstruction.setFinReference(finMain.getFinReference());
		int format = CurrencyUtil.getFormat(finMain.getFinCcy());

		try {
			if (isValidComboValue(this.cbRepayFromDate,
					Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value"))) {
				finMain.setEventFromDate((Date) this.cbRepayFromDate.getSelectedItem().getValue());
			}

			finServiceInstruction.setFromDate((Date) this.cbRepayFromDate.getSelectedItem().getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbRepayToDate, Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))
					&& this.cbRepayFromDate.getSelectedIndex() != 0) {
				if (((Date) this.cbRepayToDate.getSelectedItem().getValue())
						.compareTo((Date) this.cbRepayFromDate.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(this.cbRepayToDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"),
											Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value") }));
				} else {
					finMain.setEventToDate((Date) this.cbRepayToDate.getSelectedItem().getValue());
				}

				finServiceInstruction.setToDate((Date) this.cbRepayToDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// allow zero for Re payment
			if (this.wIAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0 || this.cbSchdMthd.getSelectedItem()
					.getValue().toString().equals(CalculationConstants.SCHMTHD_PFTCAP)) {
				BigDecimal amount = PennantApplicationUtil.unFormateAmount(this.wIAmount.getValidateValue(), format);

				totalAlwRpyAmt = BigDecimal.ZERO;
				if (getFinScheduleData().getFinanceScheduleDetails() != null
						&& this.cbRepayFromDate.getSelectedIndex() > 0) {
					for (int i = 0; i < getFinScheduleData().getFinanceScheduleDetails().size(); i++) {

						FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);

						// Not before Selected From date
						if (curSchd.getSchDate().compareTo(finServiceInstruction.getFromDate()) < 0) {
							continue;
						}

						// Maximum Outstanding Repay amount Allowed to Change
						totalAlwRpyAmt = totalAlwRpyAmt.add(curSchd.getPrincipalSchd());
					}
				}

				if (amount.compareTo(totalAlwRpyAmt) > 0) {
					throw new WrongValueException(this.wIAmount.getErrorComp(),
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_ChangeRepaymentDialog_RepayAmount.value"),
											PennantApplicationUtil.amountFormate(totalAlwRpyAmt, format) }));
				}
				finServiceInstruction.setAmount(amount);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!frSpecifier.equals(toSpecifier)) {
				if (!((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE)
						|| frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))
						&& (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE)
								|| (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))))
						|| ((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY)
								|| frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_MATURITY))
								&& (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY)
										|| (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_MATURITY))))) {
					throw new WrongValueException(this.cbRepayToDate,
							Labels.getLabel("DATES_SAME_PERIOD",
									new String[] { Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"),
											Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value") }));
				}
			}
			if (isValidComboValue(this.cbSchdMthd, Labels.getLabel("label_ChangeRepaymentDialog_GrcSchdMthd.value"))
					&& this.cbSchdMthd.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.cbSchdMthd));
				finMain.setRecalSchdMethod(getComboboxValue(this.cbSchdMthd));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_ChangeRepaymentDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				finMain.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());

			}
			finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.fromDateRow.isVisible()) {
			try {
				if (this.cbFromDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ChangeRepaymentDialog_CalFromDate.value") }));
				}
				if (this.cbRepayToDate.getSelectedIndex() > 0 && ((Date) this.cbFromDate.getSelectedItem().getValue())
						.compareTo((Date) this.cbRepayToDate.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(this.cbFromDate, Labels.getLabel("DATE_ALLOWED_AFTER", new String[] {
							Labels.getLabel("label_ChangeRepaymentDialog_CalFromDate.value"),
							DateUtil.formatToLongDate((Date) this.cbRepayToDate.getSelectedItem().getValue()) }));
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
							new String[] { Labels.getLabel("label_ChangeRepaymentDialog_CalToDate.value") }));
				}
				if (this.cbReCalType.getSelectedItem().getValue().toString()
						.equals(CalculationConstants.RPYCHG_TILLDATE)) {
					if (this.cbFromDate.getSelectedIndex() > 0 && this.cbRepayToDate.getSelectedIndex() > 0
							&& ((Date) this.cbTillDate.getSelectedItem().getValue())
									.compareTo((Date) this.cbRepayToDate.getSelectedItem().getValue()) < 0) {
						throw new WrongValueException(this.cbTillDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",
										new String[] { Labels.getLabel("label_ChangeRepaymentDialog_CalToDate.value"),
												DateUtil.formatToLongDate(
														(Date) this.cbRepayToDate.getSelectedItem().getValue()) }));
					}
				} else {
					if (this.cbRepayToDate.getSelectedIndex() > 0
							&& ((Date) this.cbTillDate.getSelectedItem().getValue())
									.compareTo((Date) this.cbRepayToDate.getSelectedItem().getValue()) < 0) {
						throw new WrongValueException(this.cbTillDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",
										new String[] { Labels.getLabel("label_ChangeRepaymentDialog_CalToDate.value"),
												DateUtil.formatToLongDate(
														(Date) this.cbRepayToDate.getSelectedItem().getValue()) }));
					}
				}

				if (this.cbTillDate.getItemCount() > 0) {
					finServiceInstruction.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			finMain.setPftIntact(this.pftIntact.isChecked());
			finServiceInstruction.setPftIntact(this.pftIntact.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		finMain.setAdjTerms(0);
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

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (!"#".equals(getComboboxValue(this.cbSchdMthd))
				&& !getComboboxValue(this.cbSchdMthd).equals(getFinScheduleData().getFinanceType().getFinSchdMthd())
				&& overrideCount == 0) {
			doClearMessage();
			if (this.cbRepayFromDate.getSelectedItem().getAttribute("fromSpecifier")
					.equals(CalculationConstants.SCH_SPECIFIER_GRACE)) {
				if (!showMessage(ErrorUtil.getErrorDetail(
						new ErrorDetail("scheduleMethod", "65002", new String[] {},
								new String[] { finServiceInstruction.getSchdMethod() }),
						getUserWorkspace().getUserLanguage()).getError(), Labels.getLabel("message.Overide"))) {
					return;
				}
			}
		}

		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT)
				|| this.cbReCalType.getSelectedItem().getValue().toString()
						.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
			Date fromDate = (Date) this.cbFromDate.getSelectedItem().getValue();
			finMain.setRecalFromDate(fromDate);
			finMain.setRecalToDate(finMain.getMaturityDate());
		} else if (this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			finMain.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
			finMain.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
		} else {
			finMain.setRecalToDate(finMain.getMaturityDate());

		}
		finServiceInstruction.setFinEvent(FinServiceEvent.CHGRPY);

		// Schedule Calculator method calling
		getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
		setFinScheduleData(addRepaymentService.getAddRepaymentDetails(getFinScheduleData(), finServiceInstruction,
				this.moduleDefiner));

		finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

		// Show Error Details in Schedule Maintenance
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

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		this.wIAmount
				.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ChangeRepaymentDialog_RepayAmount.value"),
						CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy()), true, false));
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.wIAmount.clearErrorMessage();
		this.cbRepayFromDate.clearErrorMessage();
		this.cbRepayToDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.cbTillDate.clearErrorMessage();
		this.cbFromDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	// Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		String selectedRecalType = this.cbReCalType.getSelectedItem().getValue().toString();

		if (selectedRecalType.equals(CalculationConstants.RPYCHG_TILLDATE)
				|| selectedRecalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {

			this.fromDateRow.setVisible(true);
			this.numOfTermsRow.setVisible(false);
			if (selectedRecalType.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				this.tillDateRow.setVisible(true);
				fillSchFromDates(this.cbFromDate, getFinScheduleData().getFinanceScheduleDetails());

				try {
					if (isValidComboValue(this.cbRepayToDate,
							Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))) {
						fillSchToDates(this.cbTillDate, getFinScheduleData().getFinanceScheduleDetails(),
								(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
					}
				} catch (WrongValueException e) {
					this.cbReCalType.setSelectedIndex(0);
					throw e;
				}

			} else {
				this.tillDateRow.setVisible(false);
			}

			try {
				if (isValidComboValue(this.cbRepayToDate,
						Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))) {
					fillSchToDates(this.cbFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
				}
			} catch (WrongValueException e) {
				this.cbReCalType.setSelectedIndex(0);
				this.fromDateRow.setVisible(false);
				throw e;
			}

		} else if (selectedRecalType.equals(CalculationConstants.RPYCHG_ADDRECAL)
				|| selectedRecalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {

			this.fromDateRow.setVisible(true);
			this.numOfTermsRow.setVisible(true);

			try {
				if (isValidComboValue(this.cbRepayToDate,
						Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"))) {
					fillSchToDates(this.cbFromDate, getFinScheduleData().getFinanceScheduleDetails(),
							(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
				}
			} catch (WrongValueException e) {
				this.cbReCalType.setSelectedIndex(0);
				throw e;
			}
			this.tillDateRow.setVisible(false);

		} else {
			if (this.cbTillDate.getItemCount() > 0) {
				this.cbTillDate.setSelectedIndex(0);
			}
			this.tillDateRow.setVisible(false);
			this.fromDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.cbFromDate.getSelectedIndex() > 0) {
			fillSchToDates(this.cbTillDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbFromDate.getSelectedItem().getValue(), true);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbSchdMthd(Event event) {
		logger.debug("Entering" + event.toString());
		overrideCount = 0;

		String schdMthd = this.cbSchdMthd.getSelectedItem().getValue().toString();

		this.wIAmount.setMandatory(false);
		if (StringUtils.equals(schdMthd, CalculationConstants.SCHMTHD_PFT)
				|| StringUtils.equals(schdMthd, CalculationConstants.SCHMTHD_PFTCPZ)
				|| StringUtils.equals(schdMthd, CalculationConstants.SCHMTHD_NOPAY)) {
			this.wIAmount.setValue(CurrencyUtil.parse(BigDecimal.ZERO,
					CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())));
			this.wIAmount.setDisabled(true);
		} else {
			this.wIAmount.setValue(BigDecimal.ZERO);
			this.wIAmount.setDisabled(false);
			if (StringUtils.equals(schdMthd, CalculationConstants.SCHMTHD_PFTCAP)) {
				this.wIAmount.setMandatory(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbRepayToDate(Event event) {
		logger.debug("Entering" + event.toString());
		fillSchdMethod();

		int totalCount = this.cbRepayToDate.getItemCount();

		Date repayToDate = this.cbRepayToDate.getSelectedItem().getValue();

		if (this.cbRepayToDate.getSelectedIndex() > 0) {

			if (getFinScheduleData().getFinanceMain().isApplySanctionCheck()) {
				fillComboBox(this.cbReCalType, getFinScheduleData().getFinanceMain().getRecalType(),
						PennantStaticListUtil.getSchCalCodes(),
						",TILLMDT,TILLDATE,ADDRECAL,ADDLAST,ADJTERMS,CURPRD,ADDTERM,STEPPOS,");
			} else if (repayToDate.compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0
					|| this.cbRepayToDate.getSelectedIndex() != (totalCount - 1)) {
				fillComboBox(this.cbReCalType, getFinScheduleData().getFinanceMain().getRecalType(),
						PennantStaticListUtil.getSchCalCodes(), ",ADDLAST,ADJTERMS,CURPRD,ADDTERM,STEPPOS,");
			} else {
				fillComboBox(this.cbReCalType, getFinScheduleData().getFinanceMain().getRecalType(),
						PennantStaticListUtil.getSchCalCodes(),
						",ADDLAST,ADJTERMS,CURPRD,ADDTERM,TILLDATE,TILLMDT,ADDRECAL,STEPPOS,");
			}

			fillSchToDates(this.cbTillDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);

			fillSchToDates(this.cbFromDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRepayToDate.getSelectedItem().getValue(), false);
		} else {
			this.cbFromDate.setSelectedIndex(0);
			this.cbTillDate.setSelectedIndex(0);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbRepayFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		// fillSchdMethod();
		this.cbRepayToDate.setDisabled(true);
		if (isValidComboValue(this.cbRepayFromDate, Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value"))) {
			fillSchToDates(this.cbRepayToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRepayFromDate.getSelectedItem().getValue(), true);
			this.cbRepayToDate.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to show error message
	 * 
	 * @param e (Exception)
	 * @throws InterruptedException
	 * @return true/false (boolean)
	 */
	private boolean showMessage(String msg, String title) throws InterruptedException {
		logger.debug("Entering");

		if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.OVERIDE) {
			overrideCount = 1;
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method to fill schedule methods based on selected dates.
	 * 
	 */
	private void fillSchdMethod() {
		logger.debug("Entering");
		String excludeFields = ",EQUAL,PRI_PFT,PRI,POSINT,";
		String nonGrcExclFields = ",GRCNDPAY,PFTCAP,";
		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinScheduleData().getFinanceMain().getProductCategory())) {
			nonGrcExclFields = ",GRCNDPAY,PFTCAP,POSINT,";
		}
		if (this.cbRepayFromDate.getSelectedIndex() > 0 && this.cbRepayToDate.getSelectedIndex() > 0) {
			frSpecifier = this.cbRepayFromDate.getSelectedItem().getAttribute("fromSpecifier").toString();
			toSpecifier = this.cbRepayToDate.getSelectedItem().getAttribute("toSpecifier").toString();
			if ((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE)
					|| frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))
					&& (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE)
							|| (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)))) {
				this.cbSchdMthd.setDisabled(true);

				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), excludeFields);
			} else if ((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY)
					|| frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_MATURITY))
					&& (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY)
							|| (toSpecifier.equals(CalculationConstants.SCH_SPECIFIER_MATURITY)))) {
				fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(),
						PennantStaticListUtil.getScheduleMethods(), nonGrcExclFields);
				this.cbSchdMthd.setDisabled(true);
				if (this.cbSchdMthd.getSelectedItem().getValue().toString().equals(CalculationConstants.SCHMTHD_PFT)
						|| this.cbSchdMthd.getSelectedItem().getValue().toString()
								.equals(CalculationConstants.SCHMTHD_PFTCPZ)) {
					this.wIAmount.setValue(CurrencyUtil.parse(BigDecimal.ZERO,
							CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())));
					this.wIAmount.setDisabled(true);
				} else {
					this.wIAmount.setValue(BigDecimal.ZERO);
					this.wIAmount.setDisabled(false);
				}
			} else if (!frSpecifier.equals(toSpecifier)) {
				this.cbSchdMthd.setDisabled(true);
				this.cbSchdMthd.setSelectedIndex(0);
				throw new WrongValueException(this.cbRepayToDate,
						Labels.getLabel("DATES_SAME_PERIOD",
								new String[] { Labels.getLabel("label_ChangeRepaymentDialog_ToDate.value"),
										Labels.getLabel("label_ChangeRepaymentDialog_FromDate.value") }));
			}
		} else {
			this.cbSchdMthd.setSelectedIndex(0);
		}

		if (this.cbSchdMthd.getSelectedItem().getValue().toString().equals(CalculationConstants.SCHMTHD_PFTCAP)) {
			this.wIAmount.setMandatory(true);
			this.wIAmount.setValue(BigDecimal.ZERO);
		}
		logger.debug("Leaving");
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

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setAddRepaymentService(AddRepaymentService addRepaymentService) {
		this.addRepaymentService = addRepaymentService;
	}

}
