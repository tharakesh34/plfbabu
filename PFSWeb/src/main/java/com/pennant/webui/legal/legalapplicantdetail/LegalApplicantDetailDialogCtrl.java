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
 * * FileName : LegalApplicantDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-06-2018 *
 * * Modified Date : 16-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legalapplicantdetail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.legal.legaldetail.LegalDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Legal/LegalApplicantDetail/legalApplicantDetailDialog.zul file.
 * <br>
 */
public class LegalApplicantDetailDialogCtrl extends GFCBaseCtrl<LegalApplicantDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalApplicantDetailDialogCtrl.class);

	protected Window window_LegalApplicantDetailDialog;

	protected ExtendedCombobox title;
	protected ExtendedCombobox iDType;
	protected Textbox propertyOwnersName;
	protected Intbox age;
	protected Label label_dob;
	protected Textbox relationshipType;
	protected Textbox iDNo;
	protected Textbox remarks;

	private boolean enquiry = false;
	private boolean newRecord = false;
	private boolean newApplicantDetails = false;

	private LegalApplicantDetail legalApplicantDetail;
	private LegalDetailDialogCtrl legalDetailDialogCtrl;

	@Autowired
	private CustomerDocumentService customerDocumentService;

	private List<LegalApplicantDetail> legalApplicantDetailsList;

	/**
	 * default constructor.<br>
	 */
	public LegalApplicantDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalApplicantDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.legalApplicantDetail.getLegalApplicantId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalApplicantDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalApplicantDetailDialog);

		try {
			// Get the required arguments.
			this.legalApplicantDetail = (LegalApplicantDetail) arguments.get("legalApplicantDetail");
			if (this.legalApplicantDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			this.setLegalDetailDialogCtrl((LegalDetailDialogCtrl) arguments.get("legalDetailDialogCtrl"));
			setNewApplicantDetails(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}

			if (this.legalApplicantDetail.isDefault()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}
			this.legalApplicantDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// Store the before image.
			LegalApplicantDetail legalApplicantDetail = new LegalApplicantDetail();
			BeanUtils.copyProperties(this.legalApplicantDetail, legalApplicantDetail);
			this.legalApplicantDetail.setBefImage(legalApplicantDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalApplicantDetail.isWorkflow(), this.legalApplicantDetail.getWorkflowId(),
					this.legalApplicantDetail.getNextTaskId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalApplicantDetail);
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

		this.title.setTextBoxWidth(110);
		this.title.setModuleName("Salutation");
		this.title.setValueColumn("SalutationCode");
		this.title.setDescColumn("SaluationDesc");
		this.title.setValidateColumns(new String[] { "SalutationCode" });

		this.iDType.setMaxlength(50);
		this.iDType.setTextBoxWidth(110);
		this.iDType.setModuleName("CustDocumentType");
		this.iDType.setValueColumn("DocTypeCode");
		this.iDType.setDescColumn("DocTypeDesc");
		this.iDType.setValidateColumns(new String[] { "DocTypeCode" });

		this.propertyOwnersName.setMaxlength(50);
		this.age.setMaxlength(3);
		this.relationshipType.setMaxlength(50);
		this.iDNo.setMaxlength(20);
		this.remarks.setMaxlength(3000);

		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!isEnquiry()) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalApplicantDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalApplicantDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalApplicantDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalApplicantDetailDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
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
		doShowNotes(this.legalApplicantDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.legalApplicantDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFul$filltitle(Event event) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalApplicantDetail
	 * 
	 */
	public void doWriteBeanToComponents(LegalApplicantDetail aLegalApplicantDetail) {
		logger.debug(Literal.ENTERING);

		if (isNewRecord() && aLegalApplicantDetail.getCustomer() != null) {
			Customer customer = aLegalApplicantDetail.getCustomer();
			if (customer.getCustDOB() != null) {
				String dob = DateUtil.format(customer.getCustDOB(), DateFormat.LONG_DATE.getPattern());
				this.label_dob.setValue(dob);
			}
		}
		this.title.setValue(aLegalApplicantDetail.getTitle());
		this.title.setDescription(aLegalApplicantDetail.getTitleName());
		this.propertyOwnersName.setValue(aLegalApplicantDetail.getPropertyOwnersName());
		this.age.setValue(aLegalApplicantDetail.getAge());

		this.relationshipType.setValue(aLegalApplicantDetail.getRelationshipType());
		this.iDType.setValue(aLegalApplicantDetail.getIDType());
		this.iDType.setDescription(aLegalApplicantDetail.getIDTypeName());
		this.iDNo.setValue(aLegalApplicantDetail.getIDNo());
		this.remarks.setValue(aLegalApplicantDetail.getRemarks());

		logger.debug(Literal.LEAVING);
	}

	public void onChange$age(Event event) {
		logger.debug(Literal.ENTERING);
		this.label_dob.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalApplicantDetail
	 */
	public void doWriteComponentsToBean(LegalApplicantDetail aLegalApplicantDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Title
		try {
			aLegalApplicantDetail.setTitle(this.title.getValidatedValue());
			aLegalApplicantDetail.setTitleName(this.title.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Applicant Name
		try {
			aLegalApplicantDetail.setPropertyOwnersName(this.propertyOwnersName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Age
		try {
			aLegalApplicantDetail.setAge(this.age.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Relationship Type
		try {
			aLegalApplicantDetail.setRelationshipType(this.relationshipType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ID Type
		try {
			aLegalApplicantDetail.setIDType(this.iDType.getValidatedValue());
			aLegalApplicantDetail.setIDTypeName(this.iDType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ID Number
		try {
			aLegalApplicantDetail.setIDNo(this.iDNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aLegalApplicantDetail.setRemarks(this.remarks.getValue());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param legalApplicantDetail The entity that need to be render.
	 */
	public void doShowDialog(LegalApplicantDetail legalApplicantDetail) {
		logger.debug(Literal.LEAVING);

		if (legalApplicantDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.title.focus();
		} else {
			if (isNewApplicantDetails()) {
				doEdit();
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
			doWriteBeanToComponents(legalApplicantDetail);
			if (isNewApplicantDetails()) {
				this.groupboxWf.setVisible(false);
			}
			if (isEnquiry()) {
				this.btnCtrl.setBtnStatus_Enquiry();
				this.btnNotes.setVisible(false);
				doReadOnly();
			}
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.title.isReadonly()) {
			this.title.setConstraint(new PTStringValidator(
					Labels.getLabel("label_LegalApplicantDetailDialog_Title.value"), null, false, true));
		}
		if (!this.propertyOwnersName.isReadonly()) {
			this.propertyOwnersName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalApplicantDetailDialog_PropertyOwnersName.value"),
							PennantRegularExpressions.REGEX_NAME, false));
		}
		if (!this.age.isReadonly()) {
			this.age.setConstraint(new PTNumberValidator(Labels.getLabel("label_LegalApplicantDetailDialog_Age.value"),
					false, false, 100));
		}
		if (!this.relationshipType.isReadonly()) {
			this.relationshipType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalApplicantDetailDialog_RelationshipType.value"),
							PennantRegularExpressions.REGEX_NAME, false));
		}

		if (!this.iDType.isReadonly()) {
			String value = this.iDType.getValue();
			if (StringUtils.isNotBlank(value)) {
				String masterDocType = customerDocumentService.getDocTypeByMasterDefByCode("DOC_TYPE", value);
				String regex = PennantRegularExpressions.REGEX_ALPHANUM_CODE;
				if (StringUtils.equalsIgnoreCase("PAN", masterDocType)) {
					regex = PennantRegularExpressions.REGEX_PANNUMBER;
				} else if (StringUtils.equalsIgnoreCase("AADHAAR", masterDocType)) {
					regex = PennantRegularExpressions.REGEX_AADHAR_NUMBER;
				}
				if (StringUtils.isNotBlank(regex)) {
					iDNo.setConstraint(new PTStringValidator(
							Labels.getLabel("label_LegalApplicantDetailDialog_IDNo.value"), regex, false));
				}
			}
		}

		if (!this.iDType.isReadonly()) {
			this.iDType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_LegalApplicantDetailDialog_IDType.value"), null, false, true));
		}

		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalApplicantDetailDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.title.setConstraint("");
		this.propertyOwnersName.setConstraint("");
		this.age.setConstraint("");
		this.relationshipType.setConstraint("");
		this.iDType.setConstraint("");
		this.iDNo.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final LegalApplicantDetail aLegalApplicantDetail, String tranType) {
		if (isNewApplicantDetails()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = processDetails(aLegalApplicantDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_LegalApplicantDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getLegalDetailDialogCtrl() != null) {
					getLegalDetailDialogCtrl().doFillApplicantDetails(this.legalApplicantDetailsList);
				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalApplicantDetail aLegalApplicantDetail = new LegalApplicantDetail();
		BeanUtils.copyProperties(this.legalApplicantDetail, aLegalApplicantDetail);

		doDelete(aLegalApplicantDetail.getPropertyOwnersName(), aLegalApplicantDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_LegalApplicantDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_Title"), this.title);
		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_PropertyOwnersName"), this.propertyOwnersName);
		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_Age"), this.age);
		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_RelationshipType"), this.relationshipType);
		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_IDType"), this.iDType);
		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_IDNo"), this.iDNo);
		readOnlyComponent(isReadOnly("LegalApplicantDetailDialog_Remarks"), this.remarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.legalApplicantDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewApplicantDetails()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewApplicantDetails());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.title);
		readOnlyComponent(true, this.propertyOwnersName);
		readOnlyComponent(true, this.age);
		readOnlyComponent(true, this.relationshipType);
		readOnlyComponent(true, this.iDType);
		readOnlyComponent(true, this.iDNo);
		readOnlyComponent(true, this.remarks);

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewApplicantDetails()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.title.setValue("");
		this.title.setDescription("");
		this.propertyOwnersName.setValue("");
		this.age.setText("");
		this.relationshipType.setValue("");
		// this.iDType.setSelectedIndex(0);
		this.iDNo.setValue("");
		this.remarks.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LegalApplicantDetail aLegalApplicantDetail = new LegalApplicantDetail();
		BeanUtils.copyProperties(this.legalApplicantDetail, aLegalApplicantDetail);
		boolean isNew = false;

		doRemoveValidation();
		doRemoveLOVValidation();

		doSetValidation();
		doWriteComponentsToBean(aLegalApplicantDetail);

		isNew = aLegalApplicantDetail.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalApplicantDetail.getRecordType())) {
				aLegalApplicantDetail.setVersion(aLegalApplicantDetail.getVersion() + 1);
				if (isNew) {
					aLegalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalApplicantDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewApplicantDetails()) {
				if (isNewRecord()) {
					aLegalApplicantDetail.setVersion(1);
					aLegalApplicantDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aLegalApplicantDetail.getRecordType())) {
					aLegalApplicantDetail.setVersion(aLegalApplicantDetail.getVersion() + 1);
					aLegalApplicantDetail.setRecordType(PennantConstants.RCD_UPD);
					aLegalApplicantDetail.setNewRecord(true);
				}
				if (aLegalApplicantDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aLegalApplicantDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aLegalApplicantDetail.setVersion(aLegalApplicantDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewApplicantDetails()) {
				AuditHeader auditHeader = processDetails(aLegalApplicantDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalApplicantDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getLegalDetailDialogCtrl() != null) {
						getLegalDetailDialogCtrl().doFillApplicantDetails(this.legalApplicantDetailsList);
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader processDetails(LegalApplicantDetail aLegalApplicantDetail, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aLegalApplicantDetail, tranType);

		this.legalApplicantDetailsList = new ArrayList<>();
		List<LegalApplicantDetail> oldLegalApplicantDetailsList = null;

		if (getLegalDetailDialogCtrl() != null) {
			oldLegalApplicantDetailsList = getLegalDetailDialogCtrl().getApplicantDetailList();
		}

		if (oldLegalApplicantDetailsList != null && !oldLegalApplicantDetailsList.isEmpty()) {
			for (LegalApplicantDetail oldDetails : oldLegalApplicantDetailsList) {

				if (oldDetails.getSeqNum() == aLegalApplicantDetail.getSeqNum()) {
					duplicateRecord = true;
				}

				if (duplicateRecord) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aLegalApplicantDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aLegalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.legalApplicantDetailsList.add(aLegalApplicantDetail);
						} else if (aLegalApplicantDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aLegalApplicantDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aLegalApplicantDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.legalApplicantDetailsList.add(aLegalApplicantDetail);
						} else if (aLegalApplicantDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.legalApplicantDetailsList.add(oldDetails);
						}
					}
				} else {
					this.legalApplicantDetailsList.add(oldDetails);
				}
				duplicateRecord = false;
			}
		}

		aLegalApplicantDetail.setDefault(false);
		aLegalApplicantDetail.setCustomerId(this.legalApplicantDetail.getCustomer() == null ? Long.MIN_VALUE
				: this.legalApplicantDetail.getCustomer().getCustID());
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.legalApplicantDetailsList.add(aLegalApplicantDetail);
			recordAdded = true;
		}

		if (!recordAdded) {
			this.legalApplicantDetailsList.add(aLegalApplicantDetail);
		}
		return auditHeader;
	}

	private AuditHeader getAuditHeader(LegalApplicantDetail aLegalApplicantDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalApplicantDetail.getBefImage(),
				aLegalApplicantDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalApplicantDetail.getUserDetails(),
				getOverideMap());
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewApplicantDetails() {
		return newApplicantDetails;
	}

	public void setNewApplicantDetails(boolean newApplicantDetails) {
		this.newApplicantDetails = newApplicantDetails;
	}

	public LegalDetailDialogCtrl getLegalDetailDialogCtrl() {
		return legalDetailDialogCtrl;
	}

	public void setLegalDetailDialogCtrl(LegalDetailDialogCtrl legalDetailDialogCtrl) {
		this.legalDetailDialogCtrl = legalDetailDialogCtrl;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

}
