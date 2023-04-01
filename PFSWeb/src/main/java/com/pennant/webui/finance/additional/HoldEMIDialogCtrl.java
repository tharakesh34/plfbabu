package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.HoldEMIService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class HoldEMIDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(HoldEMIDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_HoldEMIDialog;
	protected Combobox holdEMIFromDate;
	protected Datebox holdEMIToDate;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;
	protected Row row_hldEmiFrqToDate;
	protected Row row_hldEmiToDate;
	protected Combobox holdEMIFrqToDate;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;

	private transient HoldEMIService holdEMIService;
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public HoldEMIDialogCtrl() {
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
	public void onCreate$window_HoldEMIDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_HoldEMIDialog);

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

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_HoldEMIDialog.onClose();
		}
		logger.debug("Leaving");
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
		doSetValidation();
		doWriteComponentsToBean();
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.holdEMIToDate.setErrorMessage("");
		this.holdEMIFromDate.setErrorMessage("");
		this.holdEMIFrqToDate.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.holdEMIFromDate.setReadonly(true);
		this.holdEMIToDate.setReadonly(true);
		this.holdEMIFrqToDate.setReadonly(true);
		logger.debug("Leaving");
	}

	/*	*//**
			 * Method to clear error message
			 *//*
				 * private void doRemoveValidation() { logger.debug("Entering"); this.holdEMIFromDate.setConstraint("");
				 * this.holdEMIToDate.setConstraint(""); logger.debug("Leaving"); }
				 */

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.holdEMIToDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");

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

			doReadOnly();
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);
			if (getComboboxValue(holdEMIFromDate).equals(PennantConstants.List_Select)) {
				this.window_HoldEMIDialog.onClose();
			}
			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_HoldEMIDialog.onClose();
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

		fillSchFromDates(this.holdEMIFromDate, aFinSchData.getFinanceScheduleDetails());

		if (getComboboxValue(holdEMIFromDate).equals(PennantConstants.List_Select)) {
			MessageUtil.showError(Labels.getLabel("Label_holdEmi_NoSchedule"));
			return;
		}

		if (StringUtils.isNotEmpty(aFinSchData.getFinanceType().getFrequencyDays())) {
			this.row_hldEmiFrqToDate.setVisible(true);
			getholdEMIFrqToDate();
		} else {
			this.row_hldEmiToDate.setVisible(true);
		}

		logger.debug("Leaving");
	}

	public void onChange$holdEMIFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.equals(getComboboxValue(this.holdEMIFromDate), PennantConstants.List_Select)) {
			getholdEMIFrqToDate();
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$holdEMIFrqToDate(Event event) {
		if (!StringUtils.equals(getComboboxValue(this.holdEMIFrqToDate), PennantConstants.List_Select)) {
			this.holdEMIFrqToDate
					.setValue(DateUtil.formatToLongDate((Date) holdEMIFrqToDate.getSelectedItem().getValue()));
		}
	}

	/*
	 * Method to check whether the Frequency Days are less than the holdemi days, if yes then display those fields in
	 * the combobox.
	 */
	private void getholdEMIFrqToDate() {
		logger.debug("Entering");

		holdEMIFrqToDate.getItems().clear();

		Date hldEMIMaxAlwdDays = DateUtil.addDays((Date) this.holdEMIFromDate.getSelectedItem().getValue(),
				SysParamUtil.getValueAsInt("HOLDEMI_MAXDAYS"));
		if (StringUtils.isNotBlank(getFinScheduleData().getFinanceType().getFrequencyDays())) {
			String[] frqAlwdDays = getFinScheduleData().getFinanceType().getFrequencyDays().split(",");
			int hldEMIMaxAlwdMnth = DateUtil.getMonth(hldEMIMaxAlwdDays);
			int hldEMIMaxAlwdYear = DateUtil.getYear(hldEMIMaxAlwdDays);
			Date date = null;
			List<Date> frqAlwdDate = new ArrayList<Date>();

			for (int i = 0; i < frqAlwdDays.length; i++) {

				int frqDay = Integer.parseInt(frqAlwdDays[i]);
				int emiFromDay = DateUtil.getDay((Date) this.holdEMIFromDate.getSelectedItem().getValue());

				if (frqDay > emiFromDay) {
					int emiFromMnth = DateUtil.getMonth((Date) this.holdEMIFromDate.getSelectedItem().getValue());
					date = DateUtil.getDate(hldEMIMaxAlwdYear, emiFromMnth - 1, frqDay);
					if (DateUtil.getMonth(date) <= emiFromMnth && DateUtil.compare(date, hldEMIMaxAlwdDays) <= 0
							&& DateUtil.compare(date,
									(Date) this.holdEMIFromDate.getSelectedItem().getValue()) > 0) {
						frqAlwdDate.add(date);
					}
				} else {
					date = DateUtil.getDate(hldEMIMaxAlwdYear, hldEMIMaxAlwdMnth - 1, frqDay);
					if (DateUtil.compare(date, hldEMIMaxAlwdDays) <= 0 && DateUtil.compare(date,
							(Date) this.holdEMIFromDate.getSelectedItem().getValue()) > 0) {
						frqAlwdDate.add(date);
					}
				}
				DateUtil.formatToLongDate(date);
			}
			Collections.sort(frqAlwdDate);
			fillSchFrqToDates(holdEMIFrqToDate, frqAlwdDate);
		}
		if (holdEMIFrqToDate.getItemCount() <= 0) {
			this.row_hldEmiFrqToDate.setVisible(false);
			this.row_hldEmiToDate.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() {
		logger.debug("Entering");
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		finServiceInstruction.setFinID(finMain.getFinID());
		finServiceInstruction.setFinReference(finMain.getFinReference());

		try {
			if (!StringUtils.equals(getComboboxValue(this.holdEMIFromDate), PennantConstants.List_Select)) {
				finServiceInstruction.setFromDate((Date) this.holdEMIFromDate.getSelectedItem().getValue());
			} else {
				throw new WrongValueException(this.holdEMIFromDate, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_HoldEMIDialog_FromDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_hldEmiToDate.isVisible()) {
				if (this.holdEMIToDate.getValue() == null) {
					throw new WrongValueException(this.holdEMIToDate, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_HoldEMIDialog_ToDate.value") }));
				}
				finServiceInstruction.setToDate(this.holdEMIToDate.getValue());
			} else {
				if (!StringUtils.equals(getComboboxValue(this.holdEMIFrqToDate), PennantConstants.List_Select)) {
					finServiceInstruction.setToDate((Date) this.holdEMIFrqToDate.getSelectedItem().getValue());
				} else {
					throw new WrongValueException(this.holdEMIFrqToDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_HoldEMIDialog_ToDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
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

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		finServiceInstruction.setFinEvent(FinServiceEvent.HOLDEMI);
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

		// Schedule Calculator method calling
		AuditDetail auditDetail = holdEMIService.doValidations(getFinScheduleData(), finServiceInstruction);

		if (auditDetail.getErrorDetails() != null && auditDetail.getErrorDetails().size() > 0) {
			MessageUtil.showError(auditDetail.getErrorDetails().get(0).getError());
			auditDetail.getErrorDetails().clear();
		} else {
			setFinScheduleData(holdEMIService.getHoldEmiDetails(getFinScheduleData(), finServiceInstruction));
			getFinScheduleData().setSchduleGenerated(true);
			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
			this.window_HoldEMIDialog.onClose();
		}

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

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		boolean isSelect = true;
		if (financeScheduleDetails != null) {
			Date curBussDate = SysParamUtil.getAppDate();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				if ((i == 0 || i == financeScheduleDetails.size() - 1)) {
					continue;
				}

				if (StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())
						|| (curSchd.isSchPftPaid() && curSchd.isSchPriPaid())) {
					continue;
				}

				if (curSchd.getPresentmentId() > 0) {
					continue;
				}

				if (!curSchd.isRepayOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
					continue;

				}

				/*
				 * if(financeMain.getGrcPeriodEndDate().compareTo(curSchd.getSchDate()) >= 0) { continue; }
				 */

				// Not allow Before Current Business Date
				if (curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					continue;
				}

				// If maturity Terms, not include in list
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());

				dateCombobox.appendChild(comboitem);

				if (curSchd.getSchDate().compareTo(SysParamUtil.getAppDate()) >= 0 && isSelect) {
					dateCombobox.setSelectedItem(comboitem);
					isSelect = false;
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFrqToDates(Combobox dateCombobox, List<Date> hldEmiAlwdDaysList) {
		logger.debug("Entering");
		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		boolean isSelect = true;
		if (hldEmiAlwdDaysList != null) {
			for (int i = 0; i < hldEmiAlwdDaysList.size(); i++) {
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(hldEmiAlwdDaysList.get(i)));
				comboitem.setValue(hldEmiAlwdDaysList.get(i));
				dateCombobox.appendChild(comboitem);

				if (hldEmiAlwdDaysList.get(i).compareTo((Date) this.holdEMIFromDate.getSelectedItem().getValue()) > 0
						&& isSelect) {
					dateCombobox.setSelectedItem(comboitem);
					isSelect = false;
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnHoldEMI(Event event) {
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

	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void setHoldEMIService(HoldEMIService holdEMIService) {
		this.holdEMIService = holdEMIService;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

}
