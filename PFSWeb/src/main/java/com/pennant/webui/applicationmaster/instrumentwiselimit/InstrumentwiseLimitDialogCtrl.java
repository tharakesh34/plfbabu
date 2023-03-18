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
 * * FileName : InstrumentwiseLimitDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-01-2018 * *
 * Modified Date : 18-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-01-2018 PENNANT 0.1 * 20-08-2018 Somasekhar.p 0.2 Commented exclude Payment field, * discussed with siva as no
 * effect with* existing functionality * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.instrumentwiselimit;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.InstrumentwiseLimitService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/InstrumentwiseLimit/instrumentwiseLimitDialog.zul file. <br>
 */
public class InstrumentwiseLimitDialogCtrl extends GFCBaseCtrl<InstrumentwiseLimit> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InstrumentwiseLimitDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_InstrumentwiseLimitDialog;
	protected Combobox instrumentMode;
	protected CurrencyBox paymentMinAmtperTrans;
	protected CurrencyBox paymentMaxAmtperTran;
	protected CurrencyBox paymentMaxAmtperDay;
	protected CurrencyBox receiptMinAmtperTran;
	protected CurrencyBox receiptMaxAmtperTran;
	protected CurrencyBox receiptMaxAmtperDay;
	// IMPS Splitting changes
	protected CurrencyBox maxAmtPerInstruction;
	protected Row row_MaxAmtPerInstruction;

	private InstrumentwiseLimit instrumentwiseLimit; // overhanded per param

	private transient InstrumentwiseLimitListCtrl instrumentwiseLimitListCtrl; // overhanded
																				// per
																				// param
	private transient InstrumentwiseLimitService instrumentwiseLimitService;

	private List<ValueLabel> listInstrumentMode = PennantStaticListUtil.getPaymentTypes(true, false);

	/**
	 * default constructor.<br>
	 */
	public InstrumentwiseLimitDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InstrumentwiseLimitDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.instrumentwiseLimit.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_InstrumentwiseLimitDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_InstrumentwiseLimitDialog);

		try {
			// Get the required arguments.
			this.instrumentwiseLimit = (InstrumentwiseLimit) arguments.get("instrumentwiseLimit");
			this.instrumentwiseLimitListCtrl = (InstrumentwiseLimitListCtrl) arguments
					.get("instrumentwiseLimitListCtrl");

			if (this.instrumentwiseLimit == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			InstrumentwiseLimit instrumentwiseLimit = new InstrumentwiseLimit();
			BeanUtils.copyProperties(this.instrumentwiseLimit, instrumentwiseLimit);
			this.instrumentwiseLimit.setBefImage(instrumentwiseLimit);

			// Render the page and display the data.
			doLoadWorkFlow(this.instrumentwiseLimit.isWorkflow(), this.instrumentwiseLimit.getWorkflowId(),
					this.instrumentwiseLimit.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.instrumentwiseLimit);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.paymentMinAmtperTrans.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.paymentMinAmtperTrans
				.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.paymentMinAmtperTrans.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.paymentMinAmtperTrans.setScale(PennantConstants.defaultCCYDecPos);

		this.paymentMaxAmtperTran.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.paymentMaxAmtperTran.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.paymentMaxAmtperTran.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.paymentMaxAmtperTran.setScale(PennantConstants.defaultCCYDecPos);

		this.paymentMaxAmtperDay.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.paymentMaxAmtperDay.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.paymentMaxAmtperDay.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.paymentMaxAmtperDay.setScale(PennantConstants.defaultCCYDecPos);

		this.receiptMinAmtperTran.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.receiptMinAmtperTran.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.receiptMinAmtperTran.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.receiptMinAmtperTran.setScale(PennantConstants.defaultCCYDecPos);

		this.receiptMaxAmtperTran.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.receiptMaxAmtperTran.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.receiptMaxAmtperTran.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.receiptMaxAmtperTran.setScale(PennantConstants.defaultCCYDecPos);

		this.receiptMaxAmtperDay.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.receiptMaxAmtperDay.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.receiptMaxAmtperDay.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.receiptMaxAmtperDay.setScale(PennantConstants.defaultCCYDecPos);

		this.maxAmtPerInstruction.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.maxAmtPerInstruction.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.maxAmtPerInstruction.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.maxAmtPerInstruction.setScale(PennantConstants.defaultCCYDecPos);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InstrumentwiseLimitDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InstrumentwiseLimitDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InstrumentwiseLimitDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InstrumentwiseLimitDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.instrumentwiseLimit);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		instrumentwiseLimitListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.instrumentwiseLimit.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param instrumentwiseLimit
	 * 
	 */
	public void doWriteBeanToComponents(InstrumentwiseLimit aInstrumentwiseLimit) {
		logger.debug(Literal.ENTERING);

		// String excludeModes = ","+DisbursementConstants.PAYMENT_TYPE_ESCROW+",";//## PSD Ticket id:124998,Receipt
		// Upload
		fillComboBox(this.instrumentMode, aInstrumentwiseLimit.getInstrumentMode(), listInstrumentMode, "");
		this.paymentMinAmtperTrans.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getPaymentMinAmtperTrans(), PennantConstants.defaultCCYDecPos));
		this.paymentMaxAmtperTran.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getPaymentMaxAmtperTran(), PennantConstants.defaultCCYDecPos));
		this.paymentMaxAmtperDay.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getPaymentMaxAmtperDay(), PennantConstants.defaultCCYDecPos));
		this.receiptMinAmtperTran.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getReceiptMinAmtperTran(), PennantConstants.defaultCCYDecPos));
		this.receiptMaxAmtperTran.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getReceiptMaxAmtperTran(), PennantConstants.defaultCCYDecPos));
		this.receiptMaxAmtperDay.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getReceiptMaxAmtperDay(), PennantConstants.defaultCCYDecPos));
		this.maxAmtPerInstruction.setValue(PennantApplicationUtil
				.formateAmount(aInstrumentwiseLimit.getMaxAmtPerInstruction(), PennantConstants.defaultCCYDecPos));

		if (BigDecimal.ZERO.compareTo(aInstrumentwiseLimit.getMaxAmtPerInstruction()) < 0) {
			this.row_MaxAmtPerInstruction.setVisible(true);
		} else {
			this.row_MaxAmtPerInstruction.setVisible(false);
		}

		this.recordStatus.setValue(aInstrumentwiseLimit.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInstrumentwiseLimit
	 */
	public void doWriteComponentsToBean(InstrumentwiseLimit aInstrumentwiseLimit) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Instrument Mode
		try {
			String strInstrumentMode = null;
			if (this.instrumentMode.getSelectedItem() != null) {
				strInstrumentMode = this.instrumentMode.getSelectedItem().getValue().toString();
			}
			if (strInstrumentMode != null && !PennantConstants.List_Select.equals(strInstrumentMode)) {
				aInstrumentwiseLimit.setInstrumentMode(strInstrumentMode);

			} else {
				aInstrumentwiseLimit.setInstrumentMode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payments Min, Max Amounts per Transactions
		try {
			BigDecimal paymentMinAmtperTrans = PennantApplicationUtil
					.unFormateAmount(this.paymentMinAmtperTrans.getActualValue(), PennantConstants.defaultCCYDecPos);
			BigDecimal paymentMaxAmtperTran = PennantApplicationUtil
					.unFormateAmount(this.paymentMaxAmtperTran.getActualValue(), PennantConstants.defaultCCYDecPos);
			BigDecimal paymentMaxAmtperDay = PennantApplicationUtil
					.unFormateAmount(this.paymentMaxAmtperDay.getActualValue(), PennantConstants.defaultCCYDecPos);

			if (paymentMinAmtperTrans != null && paymentMaxAmtperTran != null) {
				if (paymentMinAmtperTrans.compareTo(paymentMaxAmtperTran) > 0) {
					throw new WrongValueException(this.paymentMinAmtperTrans,
							Labels.getLabel("label_InstrumentwiseLimitDialog_PaymentMinAmtperTrans.value"));
				}
			}

			if (paymentMaxAmtperTran != null && paymentMaxAmtperDay != null) {

				if (paymentMaxAmtperTran.compareTo(paymentMaxAmtperDay) > 0) {
					throw new WrongValueException(this.paymentMaxAmtperDay,
							Labels.getLabel("label_InstrumentwiseLimitDialog_PaymentMaxAmtperDay2.value"));
				}
			}

			aInstrumentwiseLimit.setPaymentMinAmtperTrans(paymentMinAmtperTrans);
			aInstrumentwiseLimit.setPaymentMaxAmtperTran(paymentMaxAmtperTran);
			aInstrumentwiseLimit.setPaymentMaxAmtperDay(paymentMaxAmtperDay);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Receipts Min, Max Amounts per Transactions
		try {
			BigDecimal receiptMinAmtperTran = PennantApplicationUtil
					.unFormateAmount(this.receiptMinAmtperTran.getActualValue(), PennantConstants.defaultCCYDecPos);
			BigDecimal receiptMaxAmtperTran = PennantApplicationUtil
					.unFormateAmount(this.receiptMaxAmtperTran.getActualValue(), PennantConstants.defaultCCYDecPos);
			BigDecimal receiptMaxAmtperDay = PennantApplicationUtil
					.unFormateAmount(this.receiptMaxAmtperDay.getActualValue(), PennantConstants.defaultCCYDecPos);

			if (receiptMinAmtperTran != null && receiptMaxAmtperTran != null) {
				if (receiptMinAmtperTran.compareTo(receiptMaxAmtperTran) == 1) {
					throw new WrongValueException(this.receiptMinAmtperTran,
							Labels.getLabel("label_InstrumentwiseLimitDialog_ReceiptMinAmtperTrans.value"));
				}
			}

			if (receiptMinAmtperTran != null && receiptMaxAmtperTran != null && receiptMaxAmtperDay != null) {
				if (receiptMinAmtperTran.compareTo(receiptMaxAmtperDay) > 0) {
					throw new WrongValueException(this.receiptMaxAmtperDay,
							Labels.getLabel("label_InstrumentwiseLimitDialog_ReceiptMaxAmtperDay1.value"));
				}
				if (receiptMaxAmtperTran.compareTo(receiptMaxAmtperDay) > 0) {
					throw new WrongValueException(this.receiptMaxAmtperDay,
							Labels.getLabel("label_InstrumentwiseLimitDialog_ReceiptMaxAmtperDay2.value"));
				}
			}

			aInstrumentwiseLimit.setReceiptMinAmtperTran(receiptMinAmtperTran);
			aInstrumentwiseLimit.setReceiptMaxAmtperTran(receiptMaxAmtperTran);
			aInstrumentwiseLimit.setReceiptMaxAmtperDay(receiptMaxAmtperDay);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Imps Splitting (payment Max Amount per Instruction)
		if (this.row_MaxAmtPerInstruction.isVisible()) {
			try {
				BigDecimal maxAmtPerInstruction = PennantApplicationUtil
						.unFormateAmount(this.maxAmtPerInstruction.getActualValue(), PennantConstants.defaultCCYDecPos);
				aInstrumentwiseLimit.setMaxAmtPerInstruction(maxAmtPerInstruction);
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aInstrumentwiseLimit.setMaxAmtPerInstruction(BigDecimal.ZERO);
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param instrumentwiseLimit The entity that need to be render.
	 */
	public void doShowDialog(InstrumentwiseLimit instrumentwiseLimit) {
		logger.debug(Literal.ENTERING);

		if (instrumentwiseLimit.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.paymentMinAmtperTrans.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(instrumentwiseLimit.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.paymentMinAmtperTrans.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		// once record approved than delete and notes button visible false
		if (StringUtils.contains(instrumentwiseLimit.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
			this.btnNotes.setVisible(false);
			this.btnDelete.setVisible(false);
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(instrumentwiseLimit);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		doClearMessage();

		if (!this.instrumentMode.isDisabled()) {
			this.instrumentMode.setConstraint(new StaticListValidator(listInstrumentMode,
					Labels.getLabel("label_InstrumentwiseLimitDialog_InstrumentMode.value")));
		}
		if (!this.paymentMinAmtperTrans.isReadonly()) {
			this.paymentMinAmtperTrans.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_InstrumentwiseLimitDialog_PaymentMinAmtperTrans.value"),
					PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.paymentMaxAmtperTran.isReadonly()) {
			this.paymentMaxAmtperTran.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_InstrumentwiseLimitDialog_PaymentMaxAmtperTran.value"),
					PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.paymentMaxAmtperDay.isReadonly()) {
			this.paymentMaxAmtperDay.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_InstrumentwiseLimitDialog_PaymentMaxAmtperDay.value"),
							PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.receiptMinAmtperTran.isReadonly()) {
			this.receiptMinAmtperTran.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_InstrumentwiseLimitDialog_ReceiptMinAmtperTran.value"),
					PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.receiptMaxAmtperTran.isReadonly()) {
			this.receiptMaxAmtperTran.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_InstrumentwiseLimitDialog_ReceiptMaxAmtperTran.value"),
					PennantConstants.defaultCCYDecPos, false, false));
		}
		if (!this.receiptMaxAmtperDay.isReadonly()) {
			this.receiptMaxAmtperDay.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_InstrumentwiseLimitDialog_ReceiptMaxAmtperDay.value"),
							PennantConstants.defaultCCYDecPos, false, false));
		}

		if (this.row_MaxAmtPerInstruction.isVisible() && !this.maxAmtPerInstruction.isReadonly()) {
			this.maxAmtPerInstruction.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_InstrumentwiseLimitDialog_MaxAmtPerInstruction.value"),
					PennantConstants.defaultCCYDecPos, true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.instrumentMode.setConstraint("");
		this.paymentMinAmtperTrans.setConstraint("");
		this.paymentMaxAmtperTran.setConstraint("");
		this.paymentMaxAmtperDay.setConstraint("");
		this.receiptMinAmtperTran.setConstraint("");
		this.receiptMaxAmtperTran.setConstraint("");
		this.receiptMaxAmtperDay.setConstraint("");
		this.maxAmtPerInstruction.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.paymentMinAmtperTrans);
		Clients.clearWrongValue(this.paymentMaxAmtperTran);
		Clients.clearWrongValue(this.paymentMaxAmtperTran);
		Clients.clearWrongValue(this.receiptMinAmtperTran);
		Clients.clearWrongValue(this.receiptMaxAmtperTran);
		Clients.clearWrongValue(this.receiptMaxAmtperDay);
		Clients.clearWrongValue(this.maxAmtPerInstruction);

		this.paymentMinAmtperTrans.clearErrorMessage();
		this.paymentMaxAmtperTran.clearErrorMessage();
		this.paymentMaxAmtperDay.clearErrorMessage();
		this.receiptMinAmtperTran.clearErrorMessage();
		this.receiptMaxAmtperTran.clearErrorMessage();
		this.receiptMaxAmtperDay.clearErrorMessage();
		this.maxAmtPerInstruction.clearErrorMessage();

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final InstrumentwiseLimit aInstrumentwiseLimit = new InstrumentwiseLimit();
		BeanUtils.copyProperties(this.instrumentwiseLimit, aInstrumentwiseLimit);

		doDelete(String.valueOf(aInstrumentwiseLimit.getId()), aInstrumentwiseLimit);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.instrumentwiseLimit.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_InstrumentMode"), this.instrumentMode);
		} else {
			readOnlyComponent(true, this.instrumentMode);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_PaymentMinAmtperTrans"), this.paymentMinAmtperTrans);
		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_PaymentMaxAmtperTran"), this.paymentMaxAmtperTran);
		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_PaymentMaxAmtperDay"), this.paymentMaxAmtperDay);
		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_ReceiptMinAmtperTran"), this.receiptMinAmtperTran);
		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_ReceiptMaxAmtperTran"), this.receiptMaxAmtperTran);
		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_ReceiptMaxAmtperDay"), this.receiptMaxAmtperDay);
		readOnlyComponent(isReadOnly("InstrumentwiseLimitDialog_MaxAmtPerInstruction"), this.maxAmtPerInstruction);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.instrumentwiseLimit.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.instrumentMode);
		readOnlyComponent(true, this.paymentMinAmtperTrans);
		readOnlyComponent(true, this.paymentMaxAmtperTran);
		readOnlyComponent(true, this.paymentMaxAmtperDay);
		readOnlyComponent(true, this.receiptMinAmtperTran);
		readOnlyComponent(true, this.receiptMaxAmtperTran);
		readOnlyComponent(true, this.receiptMaxAmtperDay);
		readOnlyComponent(true, this.maxAmtPerInstruction);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.instrumentMode.setSelectedIndex(0);
		this.paymentMinAmtperTrans.setValue("");
		this.paymentMaxAmtperTran.setValue("");
		this.paymentMaxAmtperDay.setValue("");
		this.receiptMinAmtperTran.setValue("");
		this.receiptMaxAmtperTran.setValue("");
		this.receiptMaxAmtperDay.setValue("");
		this.maxAmtPerInstruction.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final InstrumentwiseLimit aInstrumentwiseLimit = new InstrumentwiseLimit();
		BeanUtils.copyProperties(this.instrumentwiseLimit, aInstrumentwiseLimit);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aInstrumentwiseLimit);

		isNew = aInstrumentwiseLimit.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aInstrumentwiseLimit.getRecordType())) {
				aInstrumentwiseLimit.setVersion(aInstrumentwiseLimit.getVersion() + 1);
				if (isNew) {
					aInstrumentwiseLimit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInstrumentwiseLimit.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInstrumentwiseLimit.setNewRecord(true);
				}
			}
		} else {
			aInstrumentwiseLimit.setVersion(aInstrumentwiseLimit.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aInstrumentwiseLimit, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
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
	protected boolean doProcess(InstrumentwiseLimit aInstrumentwiseLimit, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aInstrumentwiseLimit.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aInstrumentwiseLimit.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInstrumentwiseLimit.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aInstrumentwiseLimit.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aInstrumentwiseLimit.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aInstrumentwiseLimit);
				}

				if (isNotesMandatory(taskId, aInstrumentwiseLimit)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aInstrumentwiseLimit.setTaskId(taskId);
			aInstrumentwiseLimit.setNextTaskId(nextTaskId);
			aInstrumentwiseLimit.setRoleCode(getRole());
			aInstrumentwiseLimit.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aInstrumentwiseLimit, tranType);
			String operationRefs = getServiceOperations(taskId, aInstrumentwiseLimit);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aInstrumentwiseLimit, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aInstrumentwiseLimit, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		InstrumentwiseLimit aInstrumentwiseLimit = (InstrumentwiseLimit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = instrumentwiseLimitService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = instrumentwiseLimitService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = instrumentwiseLimitService.doApprove(auditHeader);

					if (aInstrumentwiseLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = instrumentwiseLimitService.doReject(auditHeader);
					if (aInstrumentwiseLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_InstrumentwiseLimitDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_InstrumentwiseLimitDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.instrumentwiseLimit), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(InstrumentwiseLimit aInstrumentwiseLimit, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aInstrumentwiseLimit.getBefImage(),
				aInstrumentwiseLimit);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aInstrumentwiseLimit.getUserDetails(),
				getOverideMap());
	}

	public void setInstrumentwiseLimitService(InstrumentwiseLimitService instrumentwiseLimitService) {
		this.instrumentwiseLimitService = instrumentwiseLimitService;
	}

}
