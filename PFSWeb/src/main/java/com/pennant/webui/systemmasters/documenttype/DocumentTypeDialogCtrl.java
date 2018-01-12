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
 * FileName    		:  DocumentTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.documenttype;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;

import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.document.DocumentDataMapping;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/DocumentType/documentTypeDialog.zul file.
 */
public class DocumentTypeDialogCtrl extends GFCBaseCtrl<DocumentType> {
	private static final long serialVersionUID = 1222331967339400466L;
	private static final Logger logger = Logger.getLogger(DocumentTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_DocumentTypeDialog; 	// autoWired

	protected Textbox 	docTypeCode; 				// autoWired
	protected Textbox 	docTypeDesc; 				// autoWired
	protected Checkbox 	docIsMandatory; 			// autoWired
	protected Checkbox 	docExpDateIsMand; 			// autoWired
	protected Checkbox 	docIssueDateMand; 			// autoWired
	protected Checkbox 	docIdNumMand; 				// autoWired
	protected Checkbox 	docTypeIsActive; 			// autoWired
	protected Checkbox 	docIsCustDoc; 				// autoWired
	protected Checkbox 	docIssuedAuthorityMand; 	// autoWired
	protected Checkbox 	docIsPdfExtRequired; 	// autoWired
	protected Checkbox 	docIsPasswordProtected; 	// autoWired
	protected ExtendedCombobox mappingRef;
	protected Row rowMappingRef;
	

	// not autoWired variables
	private DocumentType documentType; // overHanded per parameters
	private transient DocumentTypeListCtrl documentTypeListCtrl; // overHanded per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient DocumentTypeService documentTypeService;

	/**
	 * default constructor.<br>
	 */
	public DocumentTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DocumentTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected DocumentType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DocumentTypeDialog(Event event)	throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DocumentTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("documentType")) {
				this.documentType = (DocumentType) arguments.get("documentType");
				DocumentType befImage = new DocumentType();
				BeanUtils.copyProperties(this.documentType, befImage);
				this.documentType.setBefImage(befImage);
				setDocumentType(this.documentType);
			} else {
				setDocumentType(null);
			}

			doLoadWorkFlow(this.documentType.isWorkflow(),
					this.documentType.getWorkflowId(),
					this.documentType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"DocumentTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the documentTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete documentType here.
			if (arguments.containsKey("documentTypeListCtrl")) {
				setDocumentTypeListCtrl((DocumentTypeListCtrl) arguments
						.get("documentTypeListCtrl"));
			} else {
				setDocumentTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDocumentType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DocumentTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.docTypeCode.setMaxlength(50);
		this.docTypeDesc.setMaxlength(150);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		
		this.mappingRef.setMaxlength(8);
		this.mappingRef.setModuleName("DocumentDataMapping");
		this.mappingRef.setValueColumn("Type");
		this.mappingRef.setDescColumn("TypeDescription");
		this.mappingRef.setValidateColumns(new String[]{"Type"});
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DocumentTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DocumentTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DocumentTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DocumentTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_DocumentTypeDialog);
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
	 * @param event
	 *            An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.documentType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDocumentType
	 *            DocumentType
	 */
	public void doWriteBeanToComponents(DocumentType aDocumentType) {
		logger.debug("Entering");
		this.docTypeCode.setValue(aDocumentType.getDocTypeCode());
		this.docTypeDesc.setValue(aDocumentType.getDocTypeDesc());
		this.docIsMandatory.setChecked(aDocumentType.isDocIsMandatory());
		this.docExpDateIsMand.setChecked(aDocumentType.isDocExpDateIsMand());
		this.docIssueDateMand.setChecked(aDocumentType.isDocIssueDateMand());
		this.docIdNumMand.setChecked(aDocumentType.isDocIdNumMand());
		this.docIssuedAuthorityMand.setChecked(aDocumentType.isDocIssuedAuthorityMand());
		this.docTypeIsActive.setChecked(aDocumentType.isDocTypeIsActive());
		this.docIsCustDoc.setChecked(aDocumentType.isDocIsCustDoc());
		this.recordStatus.setValue(aDocumentType.getRecordStatus());
		this.docIsPdfExtRequired.setChecked(aDocumentType.isDocIsPdfExtRequired());
		this.docIsPasswordProtected.setChecked(aDocumentType.isDocIsPasswordProtected());
		this.mappingRef.setValue(String.valueOf(getDocumentType().getPdfMappingRef()).equals("0")?"":String.valueOf(getDocumentType().getPdfMappingRef()));
		if(aDocumentType.isDocIsPdfExtRequired()){
			this.mappingRef.setMandatoryStyle(true);
		}else{
			this.mappingRef.setMandatoryStyle(false);
		}
		if(aDocumentType.isNew() || (aDocumentType.getRecordType() != null ? aDocumentType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.docTypeIsActive.setChecked(true);
			this.docTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDocumentType
	 */
	public void doWriteComponentsToBean(DocumentType aDocumentType) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aDocumentType.setDocTypeCode(this.docTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDocumentType.setDocTypeDesc(this.docTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDocumentType.setDocIsMandatory(this.docIsMandatory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aDocumentType.setDocIssueDateMand(this.docIssueDateMand.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aDocumentType.setDocIdNumMand(this.docIdNumMand.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aDocumentType.setDocExpDateIsMand(this.docExpDateIsMand.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aDocumentType.setDocIssuedAuthorityMand(this.docIssuedAuthorityMand.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aDocumentType.setDocTypeIsActive(this.docTypeIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDocumentType.setDocIsCustDoc(this.docIsCustDoc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDocumentType.setDocIsPdfExtRequired(this.docIsPdfExtRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDocumentType.setDocIsPasswordProtected(this.docIsPasswordProtected.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.mappingRef.getValue() != null) {
				aDocumentType.setPdfMappingRef(Long.valueOf(org.apache.commons.lang3.StringUtils.isBlank(this.mappingRef.getValue())? "0" : this.mappingRef.getValue()));
			} else {
				aDocumentType.setPdfMappingRef(0);
			}
		}catch (WrongValueException we ) {
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

		aDocumentType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	public void onFulfill$mappingRef(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = this.mappingRef.getObject();

		if (dataObject instanceof DocumentDataMapping) {
			DocumentDataMapping dataMapping = (DocumentDataMapping) dataObject;
			mappingRef.setValue(String.valueOf(dataMapping.getMappingId()));
		}
		logger.debug("Leaving" + event.toString());

	}
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDocumentType
	 * @throws Exception
	 */
	public void doShowDialog(DocumentType aDocumentType) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aDocumentType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.docTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.docTypeDesc.focus();
				if (StringUtils.isNotBlank(aDocumentType.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aDocumentType);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_DocumentTypeDialog.onClose();
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

		if (!this.docTypeCode.isReadonly()){
			this.docTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DocumentTypeDialog_DocTypeCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.docTypeDesc.isReadonly()){
			this.docTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DocumentTypeDialog_DocTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (this.docIsPdfExtRequired.isChecked() && !this.docIsPdfExtRequired.isDisabled()){
			this.mappingRef.setConstraint(new PTStringValidator(Labels.getLabel("label_DocumentTypeDialog_MappingRef.value"), 
					null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.docTypeCode.setConstraint("");
		this.docTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
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
		this.docTypeCode.setErrorMessage("");
		this.docTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a DocumentType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final DocumentType aDocumentType = new DocumentType();
		BeanUtils.copyProperties(getDocumentType(), aDocumentType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
				Labels.getLabel("label_DocumentTypeDialog_DocTypeCode.value")+" : "+aDocumentType.getDocTypeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDocumentType.getRecordType())) {
				aDocumentType.setVersion(aDocumentType.getVersion() + 1);
				aDocumentType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDocumentType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aDocumentType, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getDocumentType().isNewRecord()) {
			this.docTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.docTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.docTypeDesc.setReadonly(isReadOnly("DocumentTypeDialog_docTypeDesc"));
		this.docIsMandatory.setDisabled(isReadOnly("DocumentTypeDialog_docIsMandatory"));
		this.docTypeIsActive.setDisabled(isReadOnly("DocumentTypeDialog_docTypeIsActive"));
		this.docIsCustDoc.setDisabled(isReadOnly("DocumentTypeDialog_docIsMandatory"));
		this.docIsPdfExtRequired.setDisabled(isReadOnly("DocumentTypeDialog_docIsPdfExtRequired"));
		this.docExpDateIsMand.setDisabled(true);
		this.docIssueDateMand.setDisabled(true);
		this.docIdNumMand.setDisabled(true);
		this.docIssuedAuthorityMand.setDisabled(true);
		this.docIsPasswordProtected.setDisabled(isReadOnly("DocumentTypeDialog_docIsPasswordProtected"));
		this.mappingRef.setButtonDisabled(true);
		if(getDocumentType().isDocIsPdfExtRequired()){
			//this.docIsPasswordProtected.setDisabled(isReadOnly("DocumentTypeDialog_docIsPasswordProtected"));
			this.mappingRef.setButtonDisabled(isReadOnly("DocumentTypeDialog_mappingRef"));
			}
		
		if(getDocumentType().isDocIsCustDoc()){
			this.docExpDateIsMand.setDisabled(isReadOnly("DocumentTypeDialog_docExpDateIsMand"));
			this.docIssueDateMand.setDisabled(isReadOnly("DocumentTypeDialog_DocIssueDateMand"));
			this.docIdNumMand.setDisabled(isReadOnly("DocumentTypeDialog_DocIdNumMand"));
			this.docIssuedAuthorityMand.setDisabled(isReadOnly("DocumentTypeDialog_docIssuedAuthorityMand"));
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.documentType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.docTypeCode.setReadonly(true);
		this.docTypeDesc.setReadonly(true);
		this.docIsMandatory.setDisabled(true);
		this.docExpDateIsMand.setDisabled(true);
		this.docIssueDateMand.setDisabled(true);
		this.docIdNumMand.setDisabled(true);
		this.docIssuedAuthorityMand.setDisabled(true);
		this.docTypeIsActive.setDisabled(true);
		this.docIsCustDoc.setDisabled(true);

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
		this.docTypeCode.setValue("");
		this.docTypeDesc.setValue("");
		this.docIsMandatory.setChecked(false);
		this.docTypeIsActive.setChecked(false);
		this.docIsCustDoc.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final DocumentType aDocumentType = new DocumentType();
		BeanUtils.copyProperties(getDocumentType(), aDocumentType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DocumentType object with the components data
		doWriteComponentsToBean(aDocumentType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aDocumentType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDocumentType.getRecordType())) {
				aDocumentType.setVersion(aDocumentType.getVersion() + 1);
				if (isNew) {
					aDocumentType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDocumentType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDocumentType.setNewRecord(true);
				}
			}
		} else {
			aDocumentType.setVersion(aDocumentType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aDocumentType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aDocumentType
	 *            (DocumentType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(DocumentType aDocumentType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDocumentType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDocumentType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDocumentType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDocumentType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDocumentType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDocumentType);
				}

				if (isNotesMandatory(taskId, aDocumentType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aDocumentType.setTaskId(taskId);
			aDocumentType.setNextTaskId(nextTaskId);
			aDocumentType.setRoleCode(getRole());
			aDocumentType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDocumentType, tranType);
			String operationRefs = getServiceOperations(taskId, aDocumentType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDocumentType,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aDocumentType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DocumentType aDocumentType = (DocumentType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getDocumentTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getDocumentTypeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getDocumentTypeService().doApprove(auditHeader);

						if (aDocumentType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getDocumentTypeService().doReject(auditHeader);

						if (aDocumentType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DocumentTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_DocumentTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.documentType), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DocumentType aDocumentType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDocumentType.getBefImage(), aDocumentType);
		return new AuditHeader(String.valueOf(aDocumentType.getId()), null,
				null, null, auditDetail, aDocumentType.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_DocumentTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}
	
	public void onCheck$docIsCustDoc(Event event){
		doCheckCustomerDoc();
	}
	
	
	public void onCheck$docIsPdfExtRequired(Event event){
		doCheckPdfExt();
	}
	
	private void doCheckPdfExt(){
		if(this.docIsPdfExtRequired.isChecked()){
			this.mappingRef.setButtonDisabled(isReadOnly("DocumentTypeDialog_mappingRef"));
			this.mappingRef.setMandatoryStyle(true);
		}else{
			this.mappingRef.setMandatoryStyle(false);
		}
		//this.docIsPasswordProtected.setChecked(getDocumentType().isDocIsPasswordProtected());
//		/this.docIsPasswordProtected.setDisabled(!docIsPdfExtRequired.isChecked());
		this.mappingRef.setButtonDisabled(!docIsPdfExtRequired.isChecked());
		this.mappingRef.setConstraint("");
		this.mappingRef.setErrorMessage("");
		this.mappingRef.setValue("");
		//this.rowMappingRef.setVisible(docIsPdfExtRequired.isChecked());
	}
	
	/**
	 * Method to check document type checked or not
	 * 
	 */
	private void doCheckCustomerDoc(){
		logger.debug("Entering");
		if(docIsCustDoc.isChecked()){
			this.docExpDateIsMand.setDisabled(isReadOnly("DocumentTypeDialog_docExpDateIsMand"));
			this.docIdNumMand.setDisabled(isReadOnly("DocumentTypeDialog_DocIssueDateMand"));
			this.docIssueDateMand.setDisabled(isReadOnly("DocumentTypeDialog_DocIdNumMand"));
			this.docIssuedAuthorityMand.setDisabled(isReadOnly("DocumentTypeDialog_DocIssuedAuthorityMand"));
			this.docExpDateIsMand.setChecked(true);
			this.docIssueDateMand.setChecked(true);
			this.docIdNumMand.setChecked(true);
			this.docIssuedAuthorityMand.setChecked(true);
		}else{
			this.docExpDateIsMand.setChecked(false);
			this.docExpDateIsMand.setDisabled(true);
			this.docIssueDateMand.setChecked(false);
			this.docIssueDateMand.setDisabled(true);
			this.docIdNumMand.setChecked(false);
			this.docIdNumMand.setDisabled(true);
			this.docIssuedAuthorityMand.setChecked(false);
			this.docIssuedAuthorityMand.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.documentType);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getDocumentTypeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.documentType.getDocTypeCode());
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

	public DocumentType getDocumentType() {
		return this.documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
	public DocumentTypeService getDocumentTypeService() {
		return this.documentTypeService;
	}

	public void setDocumentTypeListCtrl(DocumentTypeListCtrl documentTypeListCtrl) {
		this.documentTypeListCtrl = documentTypeListCtrl;
	}
	public DocumentTypeListCtrl getDocumentTypeListCtrl() {
		return this.documentTypeListCtrl;
	}

}
