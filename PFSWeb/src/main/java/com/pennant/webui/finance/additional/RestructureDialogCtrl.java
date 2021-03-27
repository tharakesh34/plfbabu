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
 * FileName    		:  RestructureDialogCtrl.java													*                           
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  						*
 * Creation Date    :  25-03-2021															*
 *                                                                  						*
 * Modified Date    :  															*
 *                                                                  						*
 * Description 		:												 						*                                 
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2021       Pennant	                 0.1                                            * 
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.RestructureType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/RestructureDialog.zul file.
 */
public class RestructureDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(RestructureDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RestructureDialog;
	protected Combobox recalculationType;
	protected Combobox restructuringReason;
	protected Combobox restructuringType;
	protected Intbox numberOfEMIHoliday;
	protected Intbox numberOfPriHoliday;
	protected Intbox numberOfEMITerms;
	protected Intbox totNoOfRestructuring;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;
	protected Row row_restructureDate;
	protected Row row_grcMaxAmount;
	protected Combobox restructureDate;
	protected CurrencyBox grcMaxAmount;

	private FinScheduleData finScheduleData;
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;
	private transient RestructureService restructureService;
	private boolean appDateValidationReq = false;
	private RestructureType restructureType = new RestructureType();
	private List<ValueLabel> restructureReasonList = PennantAppUtil
			.getActiveFieldCodeList(CalculationConstants.RESTRUCTURE_REASON);

	/**
	 * default constructor.<br>
	 */
	public RestructureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_RestructureDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_RestructureDialog);

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

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setFinanceMainDialogCtrl(null);
			}

			doSetFieldProperties();
			doShowDialog(getFinScheduleData());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RestructureDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);
			setDialog(DialogType.MODAL);

		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_RestructureDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		this.numberOfEMIHoliday.setDisabled(true);
		this.numberOfPriHoliday.setDisabled(true);
		this.numberOfEMITerms.setDisabled(true);
		this.recalculationType.setDisabled(true);
		this.grcMaxAmount.setDisabled(true);
		int finFormatter = 2;
		this.grcMaxAmount.setProperties(false, finFormatter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		if (!this.numberOfEMIHoliday.isDisabled()) {
			this.numberOfEMIHoliday.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_NumberOfEMIHoliday.value"), 0, true,
							false, 0, restructureType.getMaxEmiHoliday()));
		}

		if (!this.numberOfPriHoliday.isDisabled()) {
			this.numberOfPriHoliday.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_NumberOfPriHoliday.value"), 0, true,
							false, 0, restructureType.getMaxPriHoliday()));
		}

		if (!this.numberOfEMITerms.isDisabled()) {
			this.numberOfEMITerms.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_NumberOfEMITerms.value"), 0, true,
							false, 0, restructureType.getMaxEmiTerm()));
		}

		this.totNoOfRestructuring.setConstraint(
				new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_TotNoOfRestructuring.value"), 0, true,
						false, 1, restructureType.getMaxTotTerm()));

		if (this.row_grcMaxAmount.isVisible() && !this.grcMaxAmount.isReadonly()) {
			int finFormatter = 2;
			this.grcMaxAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_RestructureDialog_GrcMaxAmount.value"), finFormatter, false, false));
		}
	}

	/**
	 * When the "Restructure" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnRestructure(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
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
		logger.debug(Literal.ENTERING);
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();
		this.window_RestructureDialog.onClose();
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doClearMessage() {
		this.totNoOfRestructuring.setConstraint("");
		this.serviceReqNo.setConstraint("");
		this.remarks.setConstraint("");
		this.numberOfEMIHoliday.setConstraint("");
		this.numberOfPriHoliday.setConstraint("");
		this.numberOfEMITerms.setConstraint("");
		this.restructuringReason.setConstraint("");
		this.restructuringType.setConstraint("");
		this.recalculationType.setConstraint("");

		this.totNoOfRestructuring.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.numberOfEMIHoliday.setErrorMessage("");
		this.numberOfPriHoliday.setErrorMessage("");
		this.numberOfEMITerms.setErrorMessage("");
		this.restructuringReason.setErrorMessage("");
		this.restructuringType.setErrorMessage("");
		this.recalculationType.setErrorMessage("");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug(Literal.ENTERING);

		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();

		String recalType = CalculationConstants.RST_RECAL_ADDTERM_RECALEMI;
		if (getFinScheduleData().getRestructureDetail() != null
				&& (StringUtils.equals(getFinScheduleData().getRestructureDetail().getRecalculationType(),
						CalculationConstants.RST_RECAL_ADJUSTTENURE))) {
			recalType = CalculationConstants.RST_RECAL_ADJUSTTENURE;
		}
		fillComboBox(this.recalculationType, recalType, PennantStaticListUtil.getRecalTypeList(), "");

		if (getFinScheduleData().getRestructureDetail() != null) {
			fillComboBox(this.restructuringReason, getFinScheduleData().getRestructureDetail().getRestructureReason(),
					restructureReasonList, "");
			int format = 2;
			this.grcMaxAmount.setValue(PennantAppUtil
					.formateAmount(getFinScheduleData().getRestructureDetail().getGrcMaxAmount(), format));
		} else {
			fillComboBox(this.restructuringReason, "", restructureReasonList, "");
		}

		Date fillAfter = aFinanceMain.getFinStartDate();
		fillSchDates(this.restructureDate, aFinSchData, fillAfter);

		// this.recalculationType.setDisabled(true);

		if (getFinScheduleData().getFinServiceInstructions() != null
				&& getFinScheduleData().getFinServiceInstructions().size() > 0) {
			fillComboBox(this.restructuringType,
					getFinScheduleData().getFinServiceInstructions().get(0).getRestructuringType(),
					PennantAppUtil.getRestructureType(), "");
		} else {
			fillComboBox(this.restructuringType, "", PennantAppUtil.getRestructureType(), "");
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$restructuringType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.restructuringType.getSelectedIndex() <= 0) {
			logger.debug(Literal.LEAVING + event.toString());
			return;
		}
		List<RestructureType> restructureTypeList = PennantAppUtil
				.getRestructureType(Long.valueOf(getComboboxValue(this.restructuringType)));
		restructureType = restructureTypeList.get(0);
		this.numberOfEMIHoliday.setValue(0);
		this.numberOfEMITerms.setValue(0);
		this.numberOfPriHoliday.setValue(0);
		this.totNoOfRestructuring.setValue(0);
		this.grcMaxAmount.setValue(BigDecimal.ZERO);

		if (restructureType.getMaxEmiHoliday() > 0) {
			this.numberOfEMIHoliday.setDisabled(false);
		} else {
			this.numberOfEMIHoliday.setDisabled(true);
		}

		if (restructureType.getMaxPriHoliday() > 0) {
			this.numberOfPriHoliday.setDisabled(false);
			this.grcMaxAmount.setDisabled(false);
		} else {
			this.numberOfPriHoliday.setDisabled(true);
			this.grcMaxAmount.setDisabled(true);
		}

		if (restructureType.getMaxEmiTerm() > 0) {
			this.numberOfEMITerms.setDisabled(false);
		} else {
			this.numberOfEMITerms.setDisabled(true);
		}

		String recalType = CalculationConstants.RST_RECAL_ADDTERM_RECALEMI;
		if (StringUtils.equals("Scenario8", restructureType.getRstTypeCode())
				|| StringUtils.equals("Scenario9", restructureType.getRstTypeCode())) {
			recalType = CalculationConstants.RST_RECAL_ADJUSTTENURE;
		}
		fillComboBox(this.recalculationType, recalType, PennantStaticListUtil.getRecalTypeList(), "");

		logger.debug(Literal.LEAVING + event.toString());
	}

	/** To fill schedule dates */

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date fromDate = null;
		String frq = "";

		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		FinanceProfitDetail fpd = restructureService.getFinProfitDetailsById(financeMain.getFinReference());
		List<FinanceScheduleDetail> fsdList = getFinScheduleData().getFinanceScheduleDetails();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		RestructureDetail rstDetail;

		if (getFinScheduleData().getRestructureDetail() != null) {
			rstDetail = getFinScheduleData().getRestructureDetail();
		} else {
			rstDetail = new RestructureDetail();
		}

		getFinScheduleData().setFinPftDeatil(fpd);
		boolean frqValid = true;

		rstDetail.setFinReference(financeMain.getFinReference());
		rstDetail.setOldTenure(fpd.getNOInst());
		rstDetail.setOldBalTenure(fpd.getFutureInst());
		rstDetail.setOldMaturity(financeMain.getMaturityDate());
		rstDetail.setOldInterest(fpd.getTotalPftSchd());
		rstDetail.setOldCpzInterest(fpd.getTotalPftCpz());
		// As per BHFL team request we have consider the final EMI amount from last second schedule.
		FinanceScheduleDetail lastSchd = fsdList.get(fsdList.size() - 2);
		rstDetail.setOldFinalEmi(lastSchd.getRepayAmount());
		rstDetail.setRepayProfitRate(lastSchd.getCalculatedRate());

		// TODO:GANESH:Restructute Need to check with satish.k
		String finStatus = financeMain.getFinStatus();
		if (StringUtils.equals(financeMain.getFinStatus(), "S")) {
			finStatus = "0";
		} else if (finStatus.startsWith("DPD ")) {
			finStatus = finStatus.replace("DPD ", "");
		} else if (finStatus.startsWith("M0")) {
			finStatus = finStatus.replace("M", "");
		}

		rstDetail.setOldBucket(Integer.parseInt(finStatus));
		rstDetail.setOldDpd(fpd.getCurODDays());
		rstDetail.setOldEmiOs((fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()))
				.subtract(fpd.getTdSchdPri().add(fpd.getTdSchdPft())));
		rstDetail.setOldMaxUnplannedEmi(financeMain.getMaxUnplannedEmi());
		rstDetail.setOldAvailedUnplanEmi(financeMain.getAvailedUnPlanEmi());
		rstDetail.setActLoanAmount(financeMain.getFinAssetValue().add(financeMain.getFeeChargeAmt()));
		rstDetail.setFinCurrAssetValue(financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt()));
		rstDetail.setLastBilledDate(fpd.getPrvRpySchDate());
		rstDetail.setAppDate(SysParamUtil.getAppDate());
		rstDetail.setOldPOsAmount(fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
		rstDetail.setOldEmiOverdue(fpd.getTdSchdPri().subtract(fpd.getTotalPriPaid()));
		BigDecimal otherCharge = restructureService.getReceivableAmt(financeMain.getFinReference(), false);
		BigDecimal bounceCharge = restructureService.getReceivableAmt(financeMain.getFinReference(), true);
		rstDetail.setBounceCharge(bounceCharge);
		rstDetail.setOtherCharge(otherCharge);
		BigDecimal penaltyAmount = restructureService.getTotalPenaltyBal(financeMain.getFinReference(), null);
		rstDetail.setOldPenaltyAmount(penaltyAmount);
		rstDetail.setRestructureCharge(BigDecimal.ZERO);
		rstDetail.setNewExtOdDays(fpd.getExtODDays());
		rstDetail.setOldEmiOverdue(fpd.getODProfit().add(fpd.getODPrincipal()));

		// Restructure cannot be done if presentment already sent for future
		// date
		int rstHolidayPeriod = rstDetail.getEmiHldPeriod() + rstDetail.getPriHldPeriod();
		int remainingRepayPeriods = 0;
		Date prvPastDue = financeMain.getFinStartDate();
		Date restructureDate = null;

		if (this.row_restructureDate.isVisible()) {
			try {
				if (this.restructureDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.restructureDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_RestructureDialog_RestructureDate.value") }));
				}
				if (((Date) this.restructureDate.getSelectedItem().getValue())
						.compareTo(financeMain.getFinStartDate()) < 0
						|| ((Date) this.restructureDate.getSelectedItem().getValue())
								.compareTo(financeMain.getMaturityDate()) > 0) {
					throw new WrongValueException(this.restructureDate,
							Labels.getLabel("DATE_ALLOWED_RANGE",
									new String[] { Labels.getLabel("label_RestructureDialog_RestructureDate.value"),
											DateUtility.formatToLongDate(financeMain.getFinStartDate()),
											DateUtility.formatToLongDate(financeMain.getMaturityDate()) }));
				}
				rstDetail.setRestructureDate((Date) this.restructureDate.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		doClearMessage();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			if (fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) < 0) {
				rstDetail.setLastBilledDate(fsd.getSchDate());
				rstDetail.setLastBilledInstNo(fsd.getInstNumber());
				continue;
			}
			if (fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) >= 0 && fsd.isFrqDate()
					&& (fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				if (restructureDate == null) {
					restructureDate = fsd.getSchDate();
				}
				remainingRepayPeriods = remainingRepayPeriods + 1;
			}
		}

		if (restructureDate == null) {
			MessageUtil.showError("No installment pending which is greater than current app date.");
			return;
		}
		rstDetail.setRestructureDate(restructureDate);

		/*
		 * rstDetail.setGraceEndDate(financeMain.getGrcPeriodEndDate()); if (rstDetail.getRestructureDate() != null &&
		 * financeMain.getGrcPeriodEndDate().compareTo(rstDetail.getRestructureDate()) < 0) {
		 * rstDetail.setGracePending(true); } else { rstDetail.setGracePending(false); }
		 */

		// When Tenor Intact required then after restructured period atleast one installment should be available for
		// keeping EMI intact and recalculate
		if (!rstDetail.isTenorChange() && rstHolidayPeriod >= remainingRepayPeriods) {
			MessageUtil.showError(
					"Total of EMI Holidays + Principal Holidays must be less than future repayment periods for EMI intact schedules");
			return;
		}

		// FIXME: PV 30MAR20 TEMPORARY FIX FOR EXISTING ISSUES
		List<RepayInstruction> riList = restructureService.getRepayInstructions(financeMain.getFinReference(), "_AView",
				false);
		if (riList.size() > 1) {
			RepayInstruction ri = riList.get(riList.size() - 1);
			if (ri.getRepayDate().compareTo(financeMain.getMaturityDate()) >= 0) {
				riList.remove(riList.size() - 1);
			}
		}
		getFinScheduleData().setRepayInstructions(riList);

		try {
			if (isValidComboValue(this.recalculationType, Labels.getLabel("label_RestructureDialog_SchdMthd.value"))
					&& this.recalculationType.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.recalculationType));
				rstDetail.setRecalculationType(getComboboxValue(this.recalculationType));
				if (CalculationConstants.RST_RECAL_ADJUSTTENURE.equals(finServiceInstruction.getSchdMethod())) {
					rstDetail.setTenorChange(true);
					rstDetail.setEmiRecal(false);
				} else if (CalculationConstants.RST_RECAL_RECALEMI.equals(finServiceInstruction.getSchdMethod())) {
					rstDetail.setTenorChange(true);
					rstDetail.setEmiRecal(true);
				} else {
					rstDetail.setTenorChange(true);
					rstDetail.setEmiRecal(true);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.restructuringReason,
					Labels.getLabel("label_RestructureDialog_RestructuringReason.value"))
					&& this.restructuringReason.getSelectedIndex() != 0) {
				rstDetail.setRestructureReason(getComboboxValue(this.restructuringReason));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!StringUtils.equals(getComboboxValue(this.restructuringType), PennantConstants.List_Select)) {
				finServiceInstruction.setRestructuringType(this.restructuringType.getSelectedItem().getValue());
				rstDetail.setRestructureType(getComboboxValue(this.restructuringType));
				rstDetail.setRstTypeCode(this.restructureType.getRstTypeCode());
				rstDetail.setRstTypeDesc(this.restructureType.getRstTypeDesc());
			} else {
				throw new WrongValueException(this.restructuringType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_RestructureDialog_RestructuringType.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.numberOfEMIHoliday.intValue() >= 0) {
				rstDetail.setEmiHldPeriod(this.numberOfEMIHoliday.intValue());
			}
			if (this.numberOfEMITerms.intValue() >= 0) {
				rstDetail.setEmiPeriods(this.numberOfEMITerms.intValue());
			}
			if (this.numberOfPriHoliday.intValue() >= 0) {
				rstDetail.setPriHldPeriod(this.numberOfPriHoliday.intValue());
			}
			if (this.totNoOfRestructuring.intValue() <= 0) {
				throw new WrongValueException(this.totNoOfRestructuring, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_RestructureDialog_NumberOftTerms.value"), " 0 " }));
			}
			finServiceInstruction.setTerms(this.totNoOfRestructuring.intValue());
			rstDetail.setTotNoOfRestructure(this.totNoOfRestructuring.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setServiceReqNo(this.serviceReqNo.getValue());
			rstDetail.setServiceRequestNo(this.serviceReqNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setRemarks(this.remarks.getValue());
			rstDetail.setRemark(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			int formatter = 2;
			rstDetail.setGrcMaxAmount(PennantAppUtil.unFormateAmount(this.grcMaxAmount.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doClearMessage();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		getFinScheduleData().setRestructureDetail(rstDetail);
		finServiceInstruction.setFinReference(financeMain.getFinReference());
		finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_RESTRUCTURE);
		BigDecimal oldPft = getFinScheduleData().getFinanceMain().getTotalGrossPft();

		// Service details calling for Schedule calculation
		getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
		setFinScheduleData(ScheduleCalculator.procRestructure(finScheduleData));

		BigDecimal newPft = getFinScheduleData().getFinanceMain().getTotalGrossPft();

		getFinScheduleData().setPftChg(newPft.subtract(oldPft));
		finServiceInstruction.setRecalType(getFinScheduleData().getRestructureDetail().getRecalculationType());
		finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());

		finServiceInstruction.setFromDate(restructureDate);
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
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$numberOfEMIHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeTenurePeriod();
		logger.debug(Literal.LEAVING);
	}

	private void onChangeTenurePeriod() {
		int totValue = this.numberOfEMIHoliday.intValue() + this.numberOfEMITerms.intValue()
				+ this.numberOfPriHoliday.intValue();
		this.totNoOfRestructuring.setValue(totValue);
	}

	public void onChange$numberOfEMITerms(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeTenurePeriod();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$numberOfPriHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeTenurePeriod();
		logger.debug(Literal.LEAVING);
	}

	/** To fill schedule dates */
	public void fillSchDates(Combobox dateCombobox, FinScheduleData aFinSchData, Date fillAfter) {
		logger.debug(Literal.ENTERING);

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
					comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()));
					comboitem.setValue(curSchd.getSchDate());
					dateCombobox.appendChild(comboitem);
				}
			}
		}

		logger.debug(Literal.LEAVING);
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

	public void setRestructureService(RestructureService restructureService) {
		this.restructureService = restructureService;
	}

	public RestructureType getRestructureType() {
		return restructureType;
	}

	public void setRestructureType(RestructureType restructureType) {
		this.restructureType = restructureType;
	}
}
