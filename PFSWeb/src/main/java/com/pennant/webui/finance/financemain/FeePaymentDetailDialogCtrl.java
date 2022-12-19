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
 * * FileName : FeePaymentDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeePaymentDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FeePaymentDetailDetail/feePaymentDetailDetailDialog.zul file.
 */
public class FeePaymentDetailDialogCtrl extends GFCBaseCtrl<FeePaymentDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FeePaymentDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FeePaymentDetailDialog;

	protected Intbox paymentSequence;
	protected Combobox paymentMethod;
	protected CurrencyBox paymentAmount;
	protected Datebox valueDate;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;
	protected Button btnGetCust;

	// not auto wired vars
	private FeePaymentDetail feePaymentDetail; // overhanded per param

	private transient boolean newFinance;

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;

	private Object financeMainDialogCtrl;
	private FinFeeDetailListCtrl finFeeDetailListCtrl;
	private boolean newRecord = false;
	private boolean newCustomer = false;
	private int ccyFormatter = 0;

	@SuppressWarnings("unused")
	private String moduleType = "";

	private List<FeePaymentDetail> feePaymentDetailDetails;

	/**
	 * default constructor.<br>
	 */
	public FeePaymentDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeePaymentDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FeePaymentDetailDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FeePaymentDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeePaymentDetailDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("feePaymentDetail")) {
				this.feePaymentDetail = (FeePaymentDetail) arguments.get("feePaymentDetail");
				FeePaymentDetail befImage = new FeePaymentDetail();
				BeanUtils.copyProperties(this.feePaymentDetail, befImage);
				this.feePaymentDetail.setBefImage(befImage);

				setFeePaymentDetail(this.feePaymentDetail);
			} else {
				setFeePaymentDetail(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getFeePaymentDetail().isNewRecord()) {
				setNewRecord(true);
			}
			if (arguments.containsKey("finFeeDetailListCtrl")) {
				setFinFeeDetailListCtrl((FinFeeDetailListCtrl) arguments.get("finFeeDetailListCtrl"));
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {

				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("ccyFormatter")) {
					ccyFormatter = (Integer) arguments.get("ccyFormatter");
				}

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.feePaymentDetail.setWorkflowId(0);
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FeePaymentDetailDialog");
			}

			doLoadWorkFlow(this.feePaymentDetail.isWorkflow(), this.feePaymentDetail.getWorkflowId(),
					this.feePaymentDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FeePaymentDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFeePaymentDetail());
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
		doCancel();
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
		MessageUtil.showHelpWindow(event, window_FeePaymentDetailDialog);
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
		doShowNotes(this.feePaymentDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.feePaymentDetail.getFinReference());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFeePaymentDetailDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FeePaymentDetail aFeePaymentDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFeePaymentDetail.isNewRecord()) {

			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.paymentMethod.focus();
		} else {
			this.paymentMethod.focus();
			if (isNewFinance()) {
				if (enqModule) {
					doReadOnly();
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aFeePaymentDetail);

			this.window_FeePaymentDetailDialog.setHeight("55%");
			this.window_FeePaymentDetailDialog.setWidth("75%");
			this.gb_statusDetails.setVisible(false);
			this.window_FeePaymentDetailDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		this.paymentSequence.setReadonly(isReadOnly("FeePaymentDetailDialog_paymentSequence"));
		this.paymentMethod.setDisabled(isReadOnly("FeePaymentDetailDialog_paymentMethod"));
		this.paymentAmount.setDisabled(isReadOnly("FeePaymentDetailDialog_paymentAmount"));
		this.valueDate.setDisabled(isReadOnly("FeePaymentDetailDialog_valueDate"));

		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.paymentMethod.setDisabled(true);
		this.paymentAmount.setDisabled(true);
		this.valueDate.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	// Helpers

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
			getUserWorkspace().allocateAuthorities("FeePaymentDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FeePaymentDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeePaymentDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FeePaymentDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FeePaymentDetailDialog_btnSave"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.paymentAmount.setMandatory(true);
		this.paymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.paymentAmount.setScale(ccyFormatter);
		this.paymentAmount.setTextBoxWidth(150);

		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setWidth("150px");

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.feePaymentDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFeePaymentDetail FeePaymentDetailDetail
	 */
	public void doWriteBeanToComponents(FeePaymentDetail aFinAdvnancePayments) {
		logger.debug("Entering");

		fillComboBox(this.paymentMethod, aFinAdvnancePayments.getPaymentMethod(),
				PennantStaticListUtil.getRemFeeSchdMethods(), "");
		this.paymentAmount
				.setValue(PennantApplicationUtil.formateAmount(aFinAdvnancePayments.getPaymentAmount(), ccyFormatter));
		this.valueDate.setValue(aFinAdvnancePayments.getValueDate());

		this.recordStatus.setValue(aFinAdvnancePayments.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aFinAdvnancePayments.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFeePaymentDetail
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FeePaymentDetail aFeePaymentDetail) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if ("#".equals(getComboboxValue(this.paymentMethod))) {
				if (this.paymentMethod.isVisible() && !this.paymentMethod.isDisabled()) {
					throw new WrongValueException(this.paymentMethod, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FeePaymentDetailDialog_PaymentMethod.value") }));
				} else {
					aFeePaymentDetail.setPaymentReference(null);
				}
			} else {
				aFeePaymentDetail.setPaymentReference(getComboboxValue(this.paymentMethod));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeePaymentDetail.setPaymentAmount(
					CurrencyUtil.unFormat(this.paymentAmount.isReadonly() ? this.paymentAmount.getActualValue()
							: this.paymentAmount.getValidateValue(), ccyFormatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.valueDate.getValue() != null) {
				aFeePaymentDetail.setValueDate(this.valueDate.getValue());
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
		aFeePaymentDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.paymentMethod.isDisabled()) {
			this.paymentMethod.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FeePaymentDetailDialog_PaymentMethod.value"), null, true));
		}
		if (!this.paymentAmount.isDisabled()) {
			this.paymentAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FeePaymentDetailDialog_PaymentAmount.value"), ccyFormatter, true, false));
		}
		if (!this.valueDate.isDisabled()) {
			this.valueDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FeePaymentDetailDialog_ValueDate.value"), true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.paymentMethod.setConstraint("");
		this.paymentAmount.setConstraint("");
		this.valueDate.setConstraint("");
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
		this.paymentMethod.setErrorMessage("");
		this.paymentAmount.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final FeePaymentDetail aFeePaymentDetail, String tranType) {
		if (isNewCustomer()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newFeePaymentDetailProcess(aFeePaymentDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FeePaymentDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinFeeDetailListCtrl().doFillFeePaymentDetails(this.feePaymentDetailDetails, true);
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FeePaymentDetail aFeePaymentDetail = new FeePaymentDetail();
		BeanUtils.copyProperties(getFeePaymentDetail(), aFeePaymentDetail);

		final String keyReference = Labels.getLabel("label_FeePaymentDetailDialog_PaymentSequence.value") + " : "
				+ aFeePaymentDetail.getPaymentReference();

		doDelete(keyReference, aFeePaymentDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.paymentMethod.setValue("");
		this.paymentAmount.setValue("");
		this.valueDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FeePaymentDetail aFeePaymentDetail = new FeePaymentDetail();
		BeanUtils.copyProperties(getFeePaymentDetail(), aFeePaymentDetail);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aFeePaymentDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFeePaymentDetail.getNextTaskId(),
					aFeePaymentDetail);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFeePaymentDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the FeePaymentDetailDetail object with the components data
			doWriteComponentsToBean(aFeePaymentDetail);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFeePaymentDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFeePaymentDetail.getRecordType())) {
				aFeePaymentDetail.setVersion(aFeePaymentDetail.getVersion() + 1);
				if (isNew) {
					aFeePaymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFeePaymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFeePaymentDetail.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aFeePaymentDetail.setVersion(1);
					aFeePaymentDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFeePaymentDetail.getRecordType())) {
					aFeePaymentDetail.setVersion(aFeePaymentDetail.getVersion() + 1);
					aFeePaymentDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFeePaymentDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFeePaymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aFeePaymentDetail.setVersion(aFeePaymentDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewCustomer()) {
				AuditHeader auditHeader = newFeePaymentDetailProcess(aFeePaymentDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FeePaymentDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinFeeDetailListCtrl().doFillFeePaymentDetails(this.feePaymentDetailDetails, true);
					closeDialog();
				}
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFeePaymentDetailProcess(FeePaymentDetail afeePaymentDetail, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(afeePaymentDetail, tranType);
		feePaymentDetailDetails = new ArrayList<FeePaymentDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(afeePaymentDetail.getFinReference());
		valueParm[1] = StringUtils.trimToEmpty(afeePaymentDetail.getPaymentReference());

		errParm[0] = PennantJavaUtil.getLabel("FeePaymentDetail_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("FeePaymentDetail_PaymentSeq") + ":" + valueParm[1];

		List<FeePaymentDetail> listPayments = null;
		listPayments = getFinFeeDetailListCtrl().getFeePaymentDetailList();
		if (listPayments != null && listPayments.size() > 0) {
			for (int i = 0; i < listPayments.size(); i++) {
				FeePaymentDetail loanDetail = listPayments.get(i);

				if (StringUtils.equals(afeePaymentDetail.getPaymentReference(), loanDetail.getPaymentReference())) { // Both
																														// Current
																														// and
																														// Existing
																														// list
																														// rating
																														// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(afeePaymentDetail.getRecordType())) {
							afeePaymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							feePaymentDetailDetails.add(afeePaymentDetail);
						} else if (PennantConstants.RCD_ADD.equals(afeePaymentDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(afeePaymentDetail.getRecordType())) {
							afeePaymentDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							feePaymentDetailDetails.add(afeePaymentDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(afeePaymentDetail.getRecordType())) {
							recordAdded = true;
							List<FeePaymentDetail> listPaymentsApproved = null;
							listPaymentsApproved = getFinFeeDetailListCtrl().getFinanceDetail()
									.getFeePaymentDetailList();
							for (int j = 0; j < listPaymentsApproved.size(); j++) {
								FeePaymentDetail detail = listPaymentsApproved.get(j);
								if (detail.getFinReference() == afeePaymentDetail.getFinReference()
										&& StringUtils.equals(detail.getPaymentReference(),
												afeePaymentDetail.getPaymentReference())) {
									feePaymentDetailDetails.add(detail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							feePaymentDetailDetails.add(loanDetail);
						}
					}
				} else {
					feePaymentDetailDetails.add(loanDetail);
				}
			}
		}
		if (!recordAdded) {
			feePaymentDetailDetails.add(afeePaymentDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FeePaymentDetail aFeePaymentDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeePaymentDetail.getBefImage(), aFeePaymentDetail);
		return new AuditHeader(aFeePaymentDetail.getFinReference(), null, null, null, auditDetail,
				aFeePaymentDetail.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FeePaymentDetail getFeePaymentDetail() {
		return this.feePaymentDetail;
	}

	public void setFeePaymentDetail(FeePaymentDetail feePaymentDetail) {
		this.feePaymentDetail = feePaymentDetail;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public void setFeePaymentDetailDetails(List<FeePaymentDetail> feePaymentDetailDetails) {
		this.feePaymentDetailDetails = feePaymentDetailDetails;
	}

	public List<FeePaymentDetail> getFeePaymentDetailDetails() {
		return feePaymentDetailDetails;
	}

}
