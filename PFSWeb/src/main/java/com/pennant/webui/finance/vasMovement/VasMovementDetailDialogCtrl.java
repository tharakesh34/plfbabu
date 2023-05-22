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
 * * FileName : VasMovementDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * *
 * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.vasMovement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.VasMovement;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
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
 * This is the controller class for the /WEB-INF/pages/RMTMasters/VasMovementDetail/VasMovementDetailDialog.zul file.
 */
public class VasMovementDetailDialogCtrl extends GFCBaseCtrl<VasMovementDetail> {
	private static final long serialVersionUID = 2164774289694537365L;
	private static final Logger logger = LogManager.getLogger(VasMovementDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VasMovementDetailDialog; // autoWired
	protected ExtendedCombobox vasReference; // autoWired
	protected Datebox modifyDate; // autoWired
	protected CurrencyBox modiftAmt; // autoWired
	protected Longbox vasMovementId;
	protected Button btnSearchVasRec;

	// not auto wired variables
	private VasMovementDetail vasMovementDetail; // overHanded per parameter

	private transient boolean validationOn;

	private boolean isAccessRights = true;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<>();
	private VasMovementDialogCtrl vasMovementDialogCtrl = null;
	private VasMovement vasMovement;
	private boolean isNewRecord = false;
	private List<VasMovementDetail> vasMovementDetailList;
	private VASRecordingService vASRecordingService;

	/**
	 * default constructor.<br>
	 */
	public VasMovementDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VasMovementDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected VasMovementDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_VasMovementDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VasMovementDetailDialog);

		try {

			if (arguments.containsKey("vasMovementDetail")) {
				this.vasMovementDetail = (VasMovementDetail) arguments.get("vasMovementDetail");
				VasMovementDetail befImage = new VasMovementDetail();
				BeanUtils.copyProperties(this.vasMovementDetail, befImage);
				this.vasMovementDetail.setBefImage(befImage);

				setVasMovementDetail(this.vasMovementDetail);
			} else {
				setVasMovementDetail(null);
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("vasMovement")) {
				this.vasMovement = (VasMovement) arguments.get("vasMovement");
				setVasMovement(this.vasMovement);
			}

			if (arguments.containsKey("vasMovementDialogCtrl")) {
				this.vasMovementDialogCtrl = (VasMovementDialogCtrl) arguments.get("vasMovementDialogCtrl");
				setVasMovementDialogCtrl(this.vasMovementDialogCtrl);
				this.vasMovementDetail.setWorkflowId(0);
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			doLoadWorkFlow(this.vasMovementDetail.isWorkflow(), this.vasMovementDetail.getWorkflowId(),
					this.vasMovementDetail.getNextTaskId());

			/*
			 * if (isWorkFlowEnabled()) { this.userAction = setListRecordStatus(this.userAction); }
			 */
			if (arguments.containsKey("isAccessRights")) {
				isAccessRights = (boolean) arguments.get("isAccessRights");
			}
			// READ OVERHANDED parameters !
			// we get the checkListDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete checkListDetail here.

			// set Field Properties

			if (isWorkFlowEnabled()) {
				getUserWorkspace().allocateAuthorities("VasMovementDetailDialog", getRole());
			} else {
				getUserWorkspace().allocateAuthorities("VasMovementDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			doSetFieldProperties();
			doShowDialog(getVasMovementDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VasMovementDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.vasReference.setMandatoryStyle(true);
		this.vasReference.setModuleName("VASRebooking");
		this.vasReference.setWhereClause(" primarylinkref='" + vasMovement.getFinReference() + "'");
		this.vasReference.setValidateColumns(new String[] { "VasReference" });
		this.vasReference.setValueColumn("VasReference");
		this.vasReference.setDescColumn("ProductCode");

		this.modiftAmt.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.modiftAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.modiftAmt.setScale(PennantConstants.defaultCCYDecPos);
		this.modiftAmt.setMandatory(true);

		this.modifyDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.modifyDate.setValue(SysParamUtil.getAppDate());

		// Empty sent any required attributes
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VasMovementDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VasMovementDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VasMovementDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
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
		MessageUtil.showHelpWindow(event, window_VasMovementDetailDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.vasMovementDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVasMovementDetail VasMovementDetail
	 */
	public void doWriteBeanToComponents(VasMovementDetail aVasMovementDetail) {
		logger.debug("Entering");
		this.vasReference.setValue(aVasMovementDetail.getVasReference());
		this.modiftAmt.setValue(PennantApplicationUtil.formateAmount(aVasMovementDetail.getMovementAmt(),
				PennantConstants.defaultCCYDecPos));
		if (aVasMovementDetail.getMovementDate() == null) {
			this.modifyDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.modifyDate.setValue(aVasMovementDetail.getMovementDate());
		}
		this.recordStatus.setValue(aVasMovementDetail.getRecordStatus());

		this.vasReference.setAttribute("ProductCode", aVasMovementDetail.getVasProduct());
		this.vasReference.setAttribute("Manufacture", aVasMovementDetail.getVasProvider());
		this.vasReference.setAttribute("VasAmount", aVasMovementDetail.getVasAmount());
		this.vasMovementId.setValue(aVasMovementDetail.getVasMovementDetailId());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVasMovementDetail
	 */
	public void doWriteComponentsToBean(VasMovementDetail aVasMovementDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aVasMovementDetail.setVasReference(this.vasReference.getValue());
			aVasMovementDetail.setFinReference(vasMovement.getFinReference());
			aVasMovementDetail.setVasProduct(String.valueOf(this.vasReference.getAttribute("ProductCode")));
			aVasMovementDetail.setVasProvider(String.valueOf(this.vasReference.getAttribute("Manufacture")));
			aVasMovementDetail
					.setVasAmount(new BigDecimal(String.valueOf(this.vasReference.getAttribute("VasAmount"))));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.modiftAmt.getActualValue() != null) {
				aVasMovementDetail.setMovementAmt(PennantApplicationUtil
						.unFormateAmount(this.modiftAmt.getActualValue(), PennantConstants.defaultCCYDecPos));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVasMovementDetail.setMovementDate(this.modifyDate.getValue());

			if (this.modifyDate.getValue() != null
					&& this.modifyDate.getValue().compareTo(vasMovement.getFinStartdate()) < 0) {
				throw new WrongValueException(this.modifyDate,
						Labels.getLabel("NUMBER_MINVALUE_EQ",
								new String[] { Labels.getLabel("label_VasMovementDetailDialog_modifyDate.value"),
										Labels.getLabel("label_FinanceMainDialog_FinStartDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aVasMovementDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVasMovementDetail
	 */
	public void doShowDialog(VasMovementDetail aVasMovementDetail) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aVasMovementDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.vasReference.focus();
		} else {
			this.modifyDate.focus();
			this.vasReference.setReadonly(true);
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnEdit.setVisible(false);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aVasMovementDetail);

			if (!isAccessRights) {
				doReadOnly();
				this.modifyDate.setDisabled(true);
				this.btnEdit.setVisible(false);
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
			}

			this.window_VasMovementDetailDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_VasMovementDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.modiftAmt.isReadonly()) {
			this.modiftAmt
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_VasMovementDetailDialog_VasAmt.value"),
							PennantConstants.defaultCCYDecPos, true, true, 0));
		}

		if (!this.modifyDate.isDisabled()) {
			this.modifyDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_VasMovementDetailDialog_modifyDate.value"), true, null,
							SysParamUtil.getAppDate(), true));
		}

		if (!this.vasReference.isReadonly()) {
			this.vasReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_VasMovementDetailDialog_vasRef.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.vasReference.setConstraint("");
		this.modiftAmt.setConstraint("");
		this.modifyDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.vasReference.setErrorMessage("");
		this.modifyDate.setErrorMessage("");
		this.modiftAmt.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final VasMovementDetail checkListDetail, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = newChkListDetailProcess(checkListDetail, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.window_VasMovementDetailDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			getVasMovementDialogCtrl().doFillVasMovementDetailsList(this.vasMovementDetailList);

			this.window_VasMovementDetailDialog.onClose();
		}

		return true;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final VasMovementDetail checkListDetail = new VasMovementDetail();
		BeanUtils.copyProperties(getVasMovementDetail(), checkListDetail);

		doDelete(checkListDetail.getVasReference(), checkListDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getVasMovementDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.vasReference.setReadonly(isReadOnly("VasMovementDetailDialog_VasReference"));
		} else {
			this.btnCancel.setVisible(true);
			this.vasReference.setReadonly(true);
		}

		this.modiftAmt.setReadonly(isReadOnly("VasMovementDetailDialog_ModifyAmt"));
		this.modifyDate.setReadonly(isReadOnly("VasMovementDetailDialog_ModifyDate"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vasMovementDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (getVasMovementDetail().isNewRecord()) {
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.vasReference.setReadonly(true);
		this.modiftAmt.setReadonly(true);
		this.modifyDate.setReadonly(true);

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

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.vasReference.setValue("");
		this.modifyDate.setValue(null);
		this.modiftAmt.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VasMovementDetail aVasMovementDetail = new VasMovementDetail();
		BeanUtils.copyProperties(getVasMovementDetail(), aVasMovementDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the VasMovementDetail object with the components data
		doWriteComponentsToBean(aVasMovementDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aVasMovementDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVasMovementDetail.getRecordType())) {
				aVasMovementDetail.setVersion(aVasMovementDetail.getVersion() + 1);
				if (isNew) {
					aVasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVasMovementDetail.setNewRecord(true);
				}
			}
		} else {
			/* set the tranType according to RecordType */
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aVasMovementDetail.setVersion(1);
				aVasMovementDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aVasMovementDetail.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
				aVasMovementDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aVasMovementDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aVasMovementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			AuditHeader auditHeader = newChkListDetailProcess(aVasMovementDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_VasMovementDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getVasMovementDialogCtrl().doFillVasMovementDetailsList(this.vasMovementDetailList);

				this.window_VasMovementDetailDialog.onClose();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method added the CheckListdetail object into chkListDetailList by setting RecordType according to tranType
	 * <p>
	 * eg: if(tranType==PennantConstants.TRAN_DEL){ aVasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
	 * }
	 * </p>
	 * 
	 * @param aVasMovementDetail (VasMovementDetail)
	 * @param tranType           (String)
	 * @return auditHeader (AuditHeader)
	 * @throws InterruptedException
	 */
	private AuditHeader newChkListDetailProcess(VasMovementDetail aVasMovementDetail, String tranType) {
		logger.debug("Entering ");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aVasMovementDetail, tranType);
		vasMovementDetailList = new ArrayList<VasMovementDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aVasMovementDetail.getVasMovementDetailId());
		errParm[0] = PennantJavaUtil.getLabel("label_VasMovementDetailDialog_VasMovementId.value") + ":" + valueParm[0];

		if (getVasMovementDetail().isNewRecord()) {
			long newSeqNo = 0;
			for (VasMovementDetail vasDetail : getVasMovementDialogCtrl().getVasMovementDetailList()) {
				if (aVasMovementDetail.getVasReference().equals(vasDetail.getVasReference())) {
					if (newSeqNo == 0) {
						newSeqNo = vasDetail.getVasMovementDetailId();
					} else if (newSeqNo < vasDetail.getVasMovementDetailId()) {
						newSeqNo = vasDetail.getVasMovementDetailId();
					}
				}
			}
			newSeqNo = newSeqNo + 1;
			aVasMovementDetail.setVasMovementDetailId(newSeqNo);
		}

		if (getVasMovementDialogCtrl().getVasMovementDetailList() != null
				&& !getVasMovementDialogCtrl().getVasMovementDetailList().isEmpty()) {
			for (int i = 0; i < getVasMovementDialogCtrl().getVasMovementDetailList().size(); i++) {
				VasMovementDetail vasMovementDetail = getVasMovementDialogCtrl().getVasMovementDetailList().get(i);
				if (aVasMovementDetail.getVasMovementDetailId() == (vasMovementDetail.getVasMovementDetailId())
						&& aVasMovementDetail.getVasReference() == (vasMovementDetail.getVasReference())) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aVasMovementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aVasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							vasMovementDetailList.add(aVasMovementDetail);
						} else if (aVasMovementDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aVasMovementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aVasMovementDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							vasMovementDetailList.add(aVasMovementDetail);
						} else if (aVasMovementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getVasMovementDialogCtrl().getVasMovementDetailList().size(); j++) {
								VasMovementDetail vasdetail = getVasMovementDialogCtrl().getVasMovementDetailList()
										.get(j);
								if (aVasMovementDetail.getVasReference().trim()
										.equalsIgnoreCase(vasMovementDetail.getVasReference().trim())) {
									vasMovementDetailList.add(vasdetail);
								}
							}
						} else if (aVasMovementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							if (this.vasMovement != null && this.vasMovement.isWorkflow()) {
								aVasMovementDetail.setNewRecord(true);
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							vasMovementDetailList.add(vasMovementDetail);
						}
					}
				} else {
					vasMovementDetailList.add(vasMovementDetail);
				}
			}
		}
		if (!recordAdded) {
			vasMovementDetailList.add(aVasMovementDetail);
		}
		return auditHeader;
	}

	public void onClick$btnSearchVasRec(Event event) {
		logger.debug("Entering");

		if (StringUtils.isEmpty(this.vasReference.getValue())) {
			return;
		}

		// Set Workflow Details
		VASRecording aVASRecording = getVASRecordingService().getVASRecordingByRef(this.vasReference.getValue(), "",
				enqiryModule);
		if (aVASRecording == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProductCode='" + aVASRecording.getProductCode() + "' AND version="
				+ aVASRecording.getVersion() + " ";

		doShowDialogPage(aVASRecording);

		logger.debug("Leaving");
	}

	private void doShowDialogPage(VASRecording vASRecording) {
		logger.debug("Entering");

		Map<String, Object> arg = new HashMap<>();
		arg.put("vASRecording", vASRecording);
		arg.put("vASRecordingListCtrl", null);
		arg.put("module", "");
		arg.put("moduleCode", moduleCode);
		arg.put("enqiryModule", true);
		arg.put("vasMovement", true);

		try {
			Executions.createComponents("/WEB-INF/pages/VASRecording/VASRecordingDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * @param aVasMovementDetail
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(VasMovementDetail aVasMovementDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVasMovementDetail.getBefImage(), aVasMovementDetail);
		return new AuditHeader(String.valueOf(aVasMovementDetail.getVasMovementId()), null, null, null, auditDetail,
				aVasMovementDetail.getUserDetails(), getOverideMap());
	}

	public VASRecordingService getvASRecordingService() {
		return vASRecordingService;
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public void onFulfill$vasReference(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = vasReference.getObject();
		if (dataObject instanceof String) {

		} else {
			VASRecording details = (VASRecording) dataObject;
			if (details != null) {
				this.vasReference.setAttribute("ProductCode", details.getProductCode());
				this.vasReference.setAttribute("Manufacture", details.getManufacturerDesc());
				this.vasReference.setAttribute("VasAmount", details.getFee());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.vasMovement);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.vasMovement.getVasMovementId());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public VasMovementDetail getVasMovementDetail() {
		return this.vasMovementDetail;
	}

	public void setVasMovementDetail(VasMovementDetail checkListDetail) {
		this.vasMovementDetail = checkListDetail;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public boolean isNewRecord() {
		return isNewRecord;
	}

	public void setNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}

	public void setChkListDetailList(List<VasMovementDetail> chkListDetailList) {
		this.vasMovementDetailList = chkListDetailList;
	}

	public List<VasMovementDetail> getChkListDetailList() {
		return vasMovementDetailList;
	}

	public VasMovement getVasMovement() {
		return vasMovement;
	}

	public void setVasMovement(VasMovement vasMovement) {
		this.vasMovement = vasMovement;
	}

	public VasMovementDialogCtrl getVasMovementDialogCtrl() {
		return vasMovementDialogCtrl;
	}

	public void setVasMovementDialogCtrl(VasMovementDialogCtrl vasMovementDialogCtrl) {
		this.vasMovementDialogCtrl = vasMovementDialogCtrl;
	}

	public void setVASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public VASRecordingService getVASRecordingService() {
		return this.vASRecordingService;
	}

}
