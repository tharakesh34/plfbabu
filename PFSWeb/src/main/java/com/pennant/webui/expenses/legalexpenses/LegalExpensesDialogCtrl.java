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
 * * FileName : LegalExpensesDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-04-2016 * *
 * Modified Date : 19-04-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-04-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.expenses.legalexpenses;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.expenses.LegalExpensesService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.web.util.ComponentUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Expenses/LegalExpenses/legalExpensesDialog.zul file. <br>
 * ************************************************************<br>
 */
public class LegalExpensesDialogCtrl extends GFCBaseCtrl<LegalExpenses> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalExpensesDialogCtrl.class);

	/*
	 * ************************************************************************ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ************************************************************************
	 */
	protected Window window_LegalExpensesDialog;
	protected Row row0;
	protected Label label_CustomerId;
	protected Hlayout hlayout_CustomerId;
	protected Space space_CustomerId;

	protected Textbox expReference;
	protected Hbox hbox_expReference;
	protected Label label_ExpReference;

	protected Textbox customerId;
	protected Label label_BookingDate;
	protected Hlayout hlayout_BookingDate;
	protected Space space_BookingDate;

	protected Datebox bookingDate;
	protected Row row1;
	protected Label label_Amount;
	protected Hlayout hlayout_Amount;
	protected Space space_Amount;

	protected CurrencyBox expAmount;
	protected Label label_FinReference;
	protected Hlayout hlayout_FinReference;
	protected Space space_FinReference;

	protected ExtendedCombobox finReference;
	protected Button btnSearchFinReference;
	protected Row row2;
	protected Label label_TransactionType;
	protected Hlayout hlayout_TransactionType;
	protected Space space_TransactionType;

	protected Combobox transactionType;
	protected Label label_Remarks;
	protected Hlayout hlayout_Remarks;
	protected Space space_Remarks;

	protected Textbox remarks;
	protected Row row3;
	protected Label label_RecoveredAmount;
	protected Hlayout hlayout_RecoveredAmount;
	protected Space space_RecoveredAmount;

	protected Decimalbox recoveredAmount;
	protected Label label_Amountdue;
	protected Hlayout hlayout_Amountdue;
	protected Space space_Amountdue;

	protected Decimalbox amountdue;
	protected Row row4;
	protected Label label_IsRecoverdFromMOPA;
	protected Hlayout hlayout_IsRecoverdFromMOPA;
	protected Space space_IsRecoverdFromMOPA;

	protected Checkbox isRecoverdFromMOPA;
	protected Label label_TotalCharges;
	protected Hlayout hlayout_TotalCharges;
	protected Space space_TotalCharges;

	protected Decimalbox totalCharges;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;

	// not auto wired vars
	private LegalExpenses legalExpenses; // overhanded per param
	private transient LegalExpensesListCtrl legalExpensesListCtrl; // overhanded per param

	// ServiceDAOs / Domain Classes
	private transient LegalExpensesService legalExpensesService;
	private transient PagedListService pagedListService;
	private transient FinanceMainService financeMainService;
	private transient JVPostingService jVPostingService;

	private List<ValueLabel> listTransactionType = PennantStaticListUtil.getTransactionTypes();

	/**
	 * default constructor.<br>
	 */
	public LegalExpensesDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalExpensesDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected LegalExpenses object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LegalExpensesDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LegalExpensesDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("legalExpenses")) {
				this.legalExpenses = (LegalExpenses) arguments.get("legalExpenses");
				LegalExpenses befImage = new LegalExpenses();
				BeanUtils.copyProperties(this.legalExpenses, befImage);
				this.legalExpenses.setBefImage(befImage);

				setLegalExpenses(this.legalExpenses);
			} else {
				setLegalExpenses(null);
			}
			doLoadWorkFlow(this.legalExpenses.isWorkflow(), this.legalExpenses.getWorkflowId(),
					this.legalExpenses.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "LegalExpensesDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the legalExpensesListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete legalExpenses here.
			if (arguments.containsKey("legalExpensesListCtrl")) {
				setLegalExpensesListCtrl((LegalExpensesListCtrl) arguments.get("legalExpensesListCtrl"));
			} else {
				setLegalExpensesListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLegalExpenses());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.legalExpenses.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_LegalExpensesDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(
					getNotes("LegalExpenses", getLegalExpenses().getFinReference(), getLegalExpenses().getVersion()),
					this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLegalExpenses
	 * @throws InterruptedException
	 */
	public void doShowDialog(LegalExpenses aLegalExpenses) throws InterruptedException {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doWriteBeanToComponents(aLegalExpenses);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aLegalExpenses.isNewRecord()));

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.finReference, this.bookingDate));

		if (getLegalExpenses().isNewRecord()) {
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		this.finReference.setReadonly(isReadOnly("LegalExpensesDialog_FinReference"));
		this.transactionType.setDisabled(isReadOnly("LegalExpensesDialog_TransactionType"));
		this.bookingDate.setDisabled(isReadOnly("LegalExpensesDialog_BookingDate"));
		this.expAmount.setReadonly(isReadOnly("LegalExpensesDialog_Amount"));
		// this.recoveredAmount.setReadonly(isReadOnly("LegalExpensesDialog_RecoveredAmount"));
		// this.amountdue.setReadonly(isReadOnly("LegalExpensesDialog_Amountdue"));
		this.isRecoverdFromMOPA.setDisabled(isReadOnly("LegalExpensesDialog_IsRecoverdFromMOPA"));
		// this.totalCharges.setReadonly(isReadOnly("LegalExpensesDialog_TotalCharges"));
		this.remarks.setReadonly(isReadOnly("LegalExpensesDialog_Remarks"));

		logger.debug("Leaving");
	}

	// ****************************************************************+
	// ****************************++ helpers ************************++
	// ****************************************************************+

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		if (!enqModule) {
			getUserWorkspace().allocateAuthorities("LegalExpensesDialog", getRole());

			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalExpensesDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalExpensesDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalExpensesDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalExpensesDialog_btnSave"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.bookingDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.expAmount.setMandatory(true);
		this.expAmount.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.expAmount.setScale(2);
		this.expAmount.setTextBoxWidth(170);

		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("Finance");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setWidth("170px");
		this.customerId.setMaxlength(20);

		this.recoveredAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.recoveredAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.amountdue.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.amountdue.setScale(PennantConstants.defaultCCYDecPos);

		this.totalCharges.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.totalCharges.setScale(PennantConstants.defaultCCYDecPos);

		this.remarks.setMaxlength(1000);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "finLimitRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$finReference(Event event) {
		logger.debug("Entering");

		if (StringUtils.isNotBlank(this.finReference.getValue())) {
			Long finID = ComponentUtil.getFinID(this.finReference);
			FinanceMain financeMain = getFinanceMainService().getFinanceMainById(finID, false);
			this.customerId.setValue(Long.toString(financeMain.getCustID()));
			setTotalFeeCharges(finID);
		} else {
			this.customerId.setValue("");
		}

		logger.debug("Leaving");
	}

	/*	*//**
			 * when clicks on button "finLimitRef"
			 * 
			 * @param event
			 * @throws InterruptedException
			 * @throws InterfaceException
			 *//*
				 * public void onValueChange$finAmount(Event event){ logger.debug("Entering");
				 * if(this.recoveredAmount.getActualValue().compareTo(BigDecimal.ZERO)==1){
				 * 
				 * 
				 * 
				 * }
				 * 
				 * logger.debug("Leaving"); }
				 * 
				 * /** Writes the bean data to the components.<br>
				 * 
				 * @param aLegalExpenses LegalExpenses
				 */
	public void doWriteBeanToComponents(LegalExpenses aLegalExpenses) {
		logger.debug("Entering");
		BigDecimal recAmount;
		this.finReference.setValue(aLegalExpenses.getFinReference());
		this.expReference.setValue(aLegalExpenses.getExpReference());
		fillComboBox(this.transactionType, aLegalExpenses.getTransactionType(), listTransactionType, "");
		this.customerId.setValue(aLegalExpenses.getCustomerId());
		// set visibility of exp reference
		setExpreferenceVisibile(aLegalExpenses);
		this.expAmount.setValue(
				PennantApplicationUtil.formateAmount(aLegalExpenses.getAmount(), PennantConstants.defaultCCYDecPos));
		setTotalFeeCharges(aLegalExpenses.getFinID());
		this.remarks.setValue(aLegalExpenses.getRemarks());
		this.amountdue.setValue(
				PennantApplicationUtil.formateAmount(aLegalExpenses.getAmountdue(), PennantConstants.defaultCCYDecPos));
		this.recoveredAmount.setValue(PennantApplicationUtil.formateAmount(aLegalExpenses.getRecoveredAmount(),
				PennantConstants.defaultCCYDecPos));
		if (!aLegalExpenses.isNewRecord()) {
			recAmount = getrecoveredAmt(aLegalExpenses.getExpReference());
			if (recAmount.compareTo(BigDecimal.ZERO) > 0) {
				this.recoveredAmount
						.setValue(PennantApplicationUtil.formateAmount(recAmount, PennantConstants.defaultCCYDecPos));
				this.amountdue.setValue(this.expAmount.getActualValue().subtract(this.recoveredAmount.getValue()));
			}
		}
		this.isRecoverdFromMOPA.setChecked(aLegalExpenses.isIsRecoverdFromMOPA());

		this.recordStatus.setValue(aLegalExpenses.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aLegalExpenses.getRecordType()));
		logger.debug("Leaving");
	}

	private void setExpreferenceVisibile(LegalExpenses aLegalExpenses) {
		if (aLegalExpenses.isNewRecord()) {
			this.bookingDate.setValue(DateUtil.getSysDate());
			this.hbox_expReference.setVisible(false);
			this.label_ExpReference.setVisible(false);

		} else {
			this.bookingDate.setValue(aLegalExpenses.getBookingDate());
			this.label_ExpReference.setVisible(true);
			this.hbox_expReference.setVisible(true);
		}
	}

	private void setTotalFeeCharges(long finID) {
		logger.debug("Entering");
		if (finReference != null) {
			BigDecimal totalCharges = getLegalExpensesService().getTotalCharges(finID);
			this.totalCharges
					.setValue(PennantApplicationUtil.formateAmount(totalCharges, PennantConstants.defaultCCYDecPos));
		} else {
			this.totalCharges.setValue(new BigDecimal(0));
		}

		logger.debug("Leaving");

	}

	private void setAmountDue(BigDecimal amount) {
		logger.debug("Entering");
		BigDecimal amountDue;
		if (amount.compareTo(BigDecimal.ZERO) >= 0) {
			amountDue = amount.subtract(this.recoveredAmount.getValue());
			this.amountdue.setValue(PennantApplicationUtil.formateAmount(amountDue, PennantConstants.defaultCCYDecPos));
		}
		logger.debug("Leaving");

	}

	private BigDecimal getrecoveredAmt(String expReference) {
		BigDecimal recAmount = new BigDecimal(0);
		long batchref = getjVPostingService().getBatchRerbyExpRef(expReference);
		List<JVPostingEntry> jVPostingEntry = getjVPostingService().getJVPostingEntryListById(batchref);
		for (JVPostingEntry postingEntry : jVPostingEntry) {
			recAmount = recAmount.add(postingEntry.getTxnAmount());
		}
		if (jVPostingEntry.size() > 0) {
			recAmount = recAmount.divide(new BigDecimal(jVPostingEntry.size()));
		}
		return recAmount;
	}

	public void onFulfill$expAmount(Event event) {
		logger.debug("Entering");
		setAmountDue(CurrencyUtil.unFormat(this.expAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
		logger.debug("Leaving");

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalExpenses
	 */
	public void doWriteComponentsToBean(LegalExpenses aLegalExpenses) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Customer Id
		try {
			aLegalExpenses.setCustomerId(this.customerId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Booking Date
		try {
			if (this.bookingDate.getValue() != null) {
				aLegalExpenses.setBookingDate(this.bookingDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Amount
		try {
			if (this.expAmount.getValidateValue() != null) {
				aLegalExpenses
						.setAmount(
								PennantApplicationUtil.unFormateAmount(
										this.expAmount.isReadonly() ? this.expAmount.getActualValue()
												: this.expAmount.getValidateValue(),
										PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fin Reference
		try {
			if (this.finReference.getValue() != null) {
				aLegalExpenses.setFinReference(this.finReference.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Transaction Type
		try {
			String strTransactionType = null;
			if (this.transactionType.getSelectedItem() != null) {
				strTransactionType = this.transactionType.getSelectedItem().getValue().toString();
			}
			if (strTransactionType != null && !PennantConstants.List_Select.equals(strTransactionType)) {
				aLegalExpenses.setTransactionType(strTransactionType);
			} else {
				aLegalExpenses.setTransactionType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aLegalExpenses.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Recovered Amount
		try {
			if (this.recoveredAmount.getValue() != null) {
				aLegalExpenses.setRecoveredAmount(PennantApplicationUtil
						.unFormateAmount(this.recoveredAmount.getValue(), PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Amountdue
		try {
			if (this.amountdue.getValue() != null) {
				aLegalExpenses.setAmountdue(PennantApplicationUtil.unFormateAmount(this.amountdue.getValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Is Recoverd From M O P A
		try {
			aLegalExpenses.setIsRecoverdFromMOPA(this.isRecoverdFromMOPA.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Total Charges
		try {
			if (this.totalCharges.getValue() != null) {
				aLegalExpenses.setTotalCharges(PennantApplicationUtil.unFormateAmount(this.totalCharges.getValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Customer Id
		if (!this.customerId.isReadonly()) {
			this.customerId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalExpensesDialog_CustomerId.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		/*
		 * //Booking Date if (!this.bookingDate.isReadonly()){ this.bookingDate.setConstraint(new
		 * PTDateValidator(Labels.getLabel("label_LegalExpensesDialog_BookingDate.value"),true)); }
		 */

		if (this.amountdue.isReadonly()) {
			this.amountdue
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_LegalExpensesDialog_Amountdue.value"),
							PennantRegularExpressions.REGEX_NUMERIC_MAXLENGTH, PennantConstants.defaultCCYDecPos, false,
							false));
		}

		// Amount
		if (!this.expAmount.isReadonly()) {
			this.expAmount
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_LegalExpensesDialog_Amount.value"),
							PennantConstants.defaultCCYDecPos, true, false));
		}
		// Fin Reference
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalExpensesDialog_FinReference.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		// Transaction Type
		if (this.transactionType.isReadonly()) {
			this.transactionType.setConstraint(new StaticListValidator(listTransactionType,
					Labels.getLabel("label_LegalExpensesDialog_TransactionType.value")));
		}
		/*
		 * //Remarks if (!this.remarks.isReadonly()){ this.remarks.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_LegalExpensesDialog_Remarks.value"),PennantRegularExpressions.
		 * REGEX_NAME,true)); }
		 */
		// Recovered Amount
		/*
		 * if (!this.recoveredAmount.isReadonly()){ this.recoveredAmount.setConstraint(new
		 * PTDecimalValidator(Labels.getLabel("label_LegalExpensesDialog_RecoveredAmount.value"),PennantConstants.
		 * defaultCCYDecPos,true,false,0)); }
		 */
		// Amountdue
		/*
		 * if (!this.amountdue.isReadonly()){ this.amountdue.setConstraint(new
		 * PTDecimalValidator(Labels.getLabel("label_LegalExpensesDialog_Amountdue.value"),PennantConstants.
		 * defaultCCYDecPos,true,false,0)); }
		 */
		// Total Charges
		/*
		 * if (!this.totalCharges.isReadonly()){ this.totalCharges.setConstraint(new
		 * PTDecimalValidator(Labels.getLabel("label_LegalExpensesDialog_TotalCharges.value"),PennantConstants.
		 * defaultCCYDecPos,true,false,0)); }
		 */
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.customerId.setConstraint("");
		this.bookingDate.setConstraint("");
		this.expAmount.setConstraint("");
		this.finReference.setConstraint("");
		this.transactionType.setConstraint("");
		this.remarks.setConstraint("");
		this.recoveredAmount.setConstraint("");
		this.amountdue.setConstraint("");
		this.totalCharges.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.customerId.setErrorMessage("");
		this.bookingDate.setErrorMessage("");
		this.expAmount.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.transactionType.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.recoveredAmount.setErrorMessage("");
		this.amountdue.setErrorMessage("");
		this.totalCharges.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getLegalExpensesListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final LegalExpenses aLegalExpenses = new LegalExpenses();
		BeanUtils.copyProperties(getLegalExpenses(), aLegalExpenses);

		doDelete(aLegalExpenses.getExpReference(), aLegalExpenses);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.customerId.setValue("");
		this.bookingDate.setText("");
		this.expAmount.setValue("");
		this.finReference.setValue("");
		this.transactionType.setSelectedIndex(0);
		this.remarks.setValue("");
		this.recoveredAmount.setValue("");
		this.amountdue.setValue("");
		this.isRecoverdFromMOPA.setChecked(false);
		this.totalCharges.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LegalExpenses aLegalExpenses = new LegalExpenses();
		BeanUtils.copyProperties(getLegalExpenses(), aLegalExpenses);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aLegalExpenses.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLegalExpenses.getNextTaskId(), aLegalExpenses);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aLegalExpenses.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the LegalExpenses object with the components data
			doWriteComponentsToBean(aLegalExpenses);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aLegalExpenses.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalExpenses.getRecordType())) {
				aLegalExpenses.setVersion(aLegalExpenses.getVersion() + 1);
				if (isNew) {
					aLegalExpenses.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalExpenses.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalExpenses.setNewRecord(true);
				}
			}
		} else {
			aLegalExpenses.setVersion(aLegalExpenses.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aLegalExpenses, tranType)) {
				// doWriteBeanToComponents(aLegalExpenses);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */

	protected boolean doProcess(LegalExpenses aLegalExpenses, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		aLegalExpenses.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLegalExpenses.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLegalExpenses.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aLegalExpenses.setTaskId(getTaskId());
			aLegalExpenses.setNextTaskId(getNextTaskId());
			aLegalExpenses.setRoleCode(getRole());
			aLegalExpenses.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aLegalExpenses, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aLegalExpenses, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aLegalExpenses, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		LegalExpenses aLegalExpenses = (LegalExpenses) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getLegalExpensesService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getLegalExpensesService().saveOrUpdate(auditHeader);
				}

			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getLegalExpensesService().doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aLegalExpenses.getRecordType())) {
						deleteNotes = true;
					}

				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getLegalExpensesService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aLegalExpenses.getRecordType())) {
						deleteNotes = true;
					}

				} else {

					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LegalExpensesDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_LegalExpensesDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(
							getNotes("LegalExpenses", aLegalExpenses.getFinReference(), aLegalExpenses.getVersion()),
							true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LegalExpenses aLegalExpenses, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalExpenses.getBefImage(), aLegalExpenses);
		return new AuditHeader(String.valueOf(aLegalExpenses.getExpReference()), null, null, null, auditDetail,
				aLegalExpenses.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LegalExpenses getLegalExpenses() {
		return this.legalExpenses;
	}

	public void setLegalExpenses(LegalExpenses legalExpenses) {
		this.legalExpenses = legalExpenses;
	}

	public void setLegalExpensesService(LegalExpensesService legalExpensesService) {
		this.legalExpensesService = legalExpensesService;
	}

	public LegalExpensesService getLegalExpensesService() {
		return this.legalExpensesService;
	}

	public void setLegalExpensesListCtrl(LegalExpensesListCtrl legalExpensesListCtrl) {
		this.legalExpensesListCtrl = legalExpensesListCtrl;
	}

	public LegalExpensesListCtrl getLegalExpensesListCtrl() {
		return this.legalExpensesListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public JVPostingService getjVPostingService() {
		return jVPostingService;
	}

	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

}
