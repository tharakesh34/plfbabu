package com.pennant.webui.finance.additional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.financeservice.HoldEMIService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;


public class HoldEMIDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long				serialVersionUID	= 454600127282110738L;
	private final static Logger				logger				= Logger.getLogger(HoldEMIDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_HoldEMIDialog;
	protected Combobox						holdEMIFromDate;
	protected Datebox						holdEMIToDate;
	protected Uppercasebox					serviceReqNo;	
	protected Textbox						remarks;

	// not auto wired vars
	private FinScheduleData					finScheduleData;														// overhanded per param
	private FinanceScheduleDetail			financeScheduleDetail;													// overhanded per param
	private ScheduleDetailDialogCtrl		financeMainDialogCtrl;

	private transient HoldEMIService	holdEMIService;
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
	 * @throws Exception
	 */
	public void onCreate$window_HoldEMIDialog(Event event) throws Exception {
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
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_HoldEMIDialog.onClose();
		}
		logger.debug("Leaving");
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
	 * */
	public void onClose(Event event) {
		doClose(false);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws WrongValueException
	 */
	private void doSave() throws InterruptedException, WrongValueException,
	IllegalAccessException, InvocationTargetException {
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
		logger.debug("Leaving");
	}

/*	*//**
	 * Method to clear error message
	 *//*
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.holdEMIFromDate.setConstraint("");
		this.holdEMIToDate.setConstraint("");
		logger.debug("Leaving");
	}*/
	
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
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws Exception
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug("Entering");
		try {

			doReadOnly();
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);
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
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		fillSchFromDates(this.holdEMIFromDate, aFinSchData.getFinanceScheduleDetails());
		logger.debug("Leaving");
	}


	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		finServiceInstruction.setFinReference(finMain.getFinReference());

		try {
			finServiceInstruction.setFromDate((Date) this.holdEMIFromDate.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.holdEMIToDate.getValue()==null){
				throw new WrongValueException(this.holdEMIToDate, Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_HoldEMIDialog_ToDate.value")}));
			}
			finServiceInstruction.setToDate(this.holdEMIToDate.getValue());
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

		finServiceInstruction.setFinEvent(getScheduleDetailDialogCtrl().getFinanceDetail().getModuleDefiner());
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

		// Schedule Calculator method calling
		AuditDetail auditDetail = holdEMIService.doValidations(finServiceInstruction);
		
		if (auditDetail.getErrorDetails() != null && auditDetail.getErrorDetails().size()>0 ) {
			MessageUtil.showErrorMessage(auditDetail.getErrorDetails().get(0).getError());
			auditDetail.getErrorDetails().clear();
		}else{
			setFinScheduleData(holdEMIService.getHoldEmiDetails(getFinScheduleData(),finServiceInstruction));
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

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				FinanceMain financeMain = getFinScheduleData().getFinanceMain();
				
				if ((i == 0 || i == financeScheduleDetails.size() - 1)) {
					continue;
				}
				
				if(StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())){
					continue;
				}
				
				if(!curSchd.isRepayOnSchDate() && 
						curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0){
					continue;
					
				}
				
				if(financeMain.getGrcPeriodEndDate().compareTo(curSchd.getSchDate()) >= 0) {
					continue;
				}
				
				// If maturity Terms, not include in list
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
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
	
	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws WrongValueException
	 */
	public void onClick$btnHoldEMI(Event event)
			throws InterruptedException, WrongValueException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		if (getFinanceScheduleDetail() != null) {
			if (isDataChanged()) {
				doSave();
			} else {
				MessageUtil.showErrorMessage("No Data has been changed.");
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
