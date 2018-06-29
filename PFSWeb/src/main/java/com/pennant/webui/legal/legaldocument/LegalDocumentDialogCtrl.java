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
 * FileName    		:  LegalDocumentDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.legal.legaldocument;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.legal.legaldetail.LegalDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Legal/LegalDocument/legalDocumentDialog.zul file. <br>
 */
public class LegalDocumentDialogCtrl extends GFCBaseCtrl<LegalDocument> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalDocumentDialogCtrl.class);

	protected Window window_LegalDocumentDialog;

	// Maker
	protected Datebox documentDate;
	protected Textbox documentNo;
	protected Textbox documentDetail;
	protected Textbox surveyNo;
	protected Combobox documentTypeMaker;
	protected Combobox documentCategory;
	protected Combobox scheduleType;
	protected Groupbox gb_documentBasicDetails;

	// Verifier
	protected Combobox documentTypeVerify;
	protected Textbox documentRemarks;
	protected Textbox documentName;
	protected Button btnUploadDoc;
	protected Iframe documentPdfView;
	protected Div documentDiv;
	protected Div docDiv;
	protected Groupbox gb_documentVerifyDetails;

	// Approver
	protected Combobox documentTypeApprove;
	protected Combobox documentAccepted;
	protected Groupbox gb_documentApproverDetails;

	private LegalDocument legalDocument;
	private boolean newRecord = false;
	private boolean newDocumentDetails = false;
	private LegalDetailDialogCtrl legalDetailDialogCtrl;
	private List<LegalDocument> legalDocumentDetailsList;

	private List<ValueLabel> listDocumentType = PennantStaticListUtil.getDocumentTypes();
	private List<ValueLabel> listDocumentCategory = new ArrayList<>();
	private List<ValueLabel> listScheduleType = PennantStaticListUtil.getScheduleTypes();
	private List<ValueLabel> listDocumentTypeVerify = PennantStaticListUtil.getDocumentTypes();
	private List<ValueLabel> listDocumentTypeApprove = PennantStaticListUtil.getDocumentTypes();
	private List<ValueLabel> listDocumentAccepted = PennantStaticListUtil.getDocumentAcceptedList();

	/**
	 * default constructor.<br>
	 */
	public LegalDocumentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalDocumentDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.legalDocument.getLegalDocumentId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_LegalDocumentDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalDocumentDialog);

		try {
			// Get the required arguments.
			this.legalDocument = (LegalDocument) arguments.get("legalDocument");
			if (this.legalDocument == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			this.setLegalDetailDialogCtrl((LegalDetailDialogCtrl) arguments.get("legalDetailDialogCtrl"));

			setNewDocumentDetails(true);
			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.legalDocument.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// Store the before image.
			LegalDocument legalDocument = new LegalDocument();
			BeanUtils.copyProperties(this.legalDocument, legalDocument);
			this.legalDocument.setBefImage(legalDocument);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalDocument.isWorkflow(), this.legalDocument.getWorkflowId(),
					this.legalDocument.getNextTaskId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalDocument);
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

		this.documentDate.setFormat(PennantConstants.dateFormat);
		this.documentDetail.setMaxlength(3000);
		this.documentNo.setMaxlength(20);
		this.surveyNo.setMaxlength(3000);

		this.documentName.setMaxlength(100);
		this.documentRemarks.setMaxlength(3000);
		
		if (getLegalDetailDialogCtrl() != null) {
			getLegalDetailDialogCtrl().setDocumentsValidate(true);
		}

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalDocumentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalDocumentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalDocumentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalDocumentDialog_btnSave"));
		this.btnUploadDoc.setVisible(getUserWorkspace().isAllowed("button_LegalDocumentDialog_btUpload"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
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
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalDocument);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.legalDocument.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalDocument
	 * 
	 */
	public void doWriteBeanToComponents(LegalDocument aLegalDocument) {
		logger.debug(Literal.ENTERING);

		List<DocumentDetails> collateralDocumentList = getLegalDetailDialogCtrl().getLegalDetail().getCollateralDocumentList();
		if (CollectionUtils.isNotEmpty(collateralDocumentList)) {
			for (DocumentDetails documentDetails : collateralDocumentList) {
				ValueLabel valueLabel = new ValueLabel();
				valueLabel.setLabel(documentDetails.getDocCategory());
				valueLabel.setValue(documentDetails.getDocCategory());
				getListDocumentCategory().add(valueLabel);
			}
		}
		
		// Maker
		this.documentDate.setValue(aLegalDocument.getDocumentDate());
		this.documentDetail.setValue(aLegalDocument.getDocumentDetail());
		this.documentNo.setValue(aLegalDocument.getDocumentNo());
		this.surveyNo.setValue(aLegalDocument.getSurveyNo());
		fillComboBox(this.documentTypeMaker, aLegalDocument.getDocumentType(), listDocumentType, "");
		fillComboBox(this.documentCategory, aLegalDocument.getDocumentCategory(), getListDocumentCategory(), "");
		fillComboBox(this.scheduleType, aLegalDocument.getScheduleType(), listScheduleType, "");

		// Verifier
		fillComboBox(this.documentTypeVerify, aLegalDocument.getDocumentTypeVerify(), listDocumentTypeVerify, "");
		this.documentRemarks.setValue(aLegalDocument.getDocumentRemarks());

		// Approver
		fillComboBox(this.documentTypeApprove, aLegalDocument.getDocumentTypeApprove(), listDocumentTypeApprove, "");
		fillComboBox(this.documentAccepted, aLegalDocument.getDocumentAccepted(), listDocumentAccepted, "");

		//Document
		this.documentName.setValue(aLegalDocument.getDocumentName());
		this.documentName.setAttribute("data", aLegalDocument);
		AMedia amedia = null;
		if (aLegalDocument.getDocImage() != null) {
			if (aLegalDocument.getUploadDocumentType().equals(PennantConstants.DOC_TYPE_WORD)
					|| aLegalDocument.getUploadDocumentType().equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent(
						"<a href='' style = 'font-weight:bold'>" + aLegalDocument.getDocumentName() + "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(aLegalDocument.getUploadDocumentType());
				list.add(aLegalDocument.getDocImage());

				ageementLink.addForward("onClick", window_LegalDocumentDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			} else {
				amedia = new AMedia(aLegalDocument.getDocumentName(), null, null, aLegalDocument.getDocImage());
			}
			documentPdfView.setContent(amedia);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalDocument
	 */
	public void doWriteComponentsToBean(LegalDocument aLegalDocument) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		/* Maker */
		try {
			aLegalDocument.setDocumentDate(this.documentDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setDocumentDetail(this.documentDetail.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setDocumentNo(this.documentNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setSurveyNo(this.surveyNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setDocumentType(this.documentTypeMaker.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setDocumentCategory(this.documentCategory.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setScheduleType(this.scheduleType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		/* Verifier */
		try {
			aLegalDocument.setDocumentTypeVerify(this.documentTypeVerify.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setDocumentName(this.documentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalDocument.setDocumentRemarks(this.documentRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.gb_documentVerifyDetails.isVisible()
					&& StringUtils.trimToNull(this.documentName.getValue()) == null) {
				throw new WrongValueException(this.documentName, Labels.getLabel("MUST_BE_UPLOADED",
						new String[] { Labels.getLabel("label_FinDocumentDetailDialog_DocumnetName.value") }));
			}
			aLegalDocument.setDocumentName(this.documentName.getValue());
			LegalDocument details = (LegalDocument) this.documentName.getAttribute("data");
			aLegalDocument.setDocImage(details.getDocImage());
			aLegalDocument.setUploadDocumentType(aLegalDocument.getUploadDocumentType());
			aLegalDocument.setDocumentReference(Long.MIN_VALUE);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Approver
		try {
			aLegalDocument.setDocumentTypeApprove(this.documentTypeApprove.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLegalDocument.setDocumentAccepted(this.documentAccepted.getSelectedItem().getValue().toString());
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
	 * @param legalDocument
	 *            The entity that need to be render.
	 */
	public void doShowDialog(LegalDocument legalDocument) {
		logger.debug(Literal.LEAVING);

		if (legalDocument.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.documentDate.focus();
		} else {
			if (isNewDocumentDetails()) {
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
			doWriteBeanToComponents(legalDocument);
			if (isNewDocumentDetails()) {
				this.groupboxWf.setVisible(false);
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

		if (!this.documentDate.isReadonly()) {
			this.documentDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentDate.value"), true));
		}
		if (!this.documentDetail.isReadonly()) {
			this.documentDetail.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentDetail.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.documentNo.isReadonly()) {
			this.documentNo
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentNo.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.surveyNo.isReadonly()) {
			this.surveyNo
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LegalDocumentDialog_SurveyNo.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.documentTypeMaker.isDisabled()) {
			this.documentTypeMaker.setConstraint(new PTListValidator(
					Labels.getLabel("label_LegalDocumentDialog_DocumentTypeMaker.value"), listDocumentType, true));
		}
		if (!this.documentCategory.isDisabled()) {
			this.documentCategory.setConstraint(
					new PTListValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentCategory.value"),
							getListDocumentCategory(), false));
		}
		if (!this.scheduleType.isDisabled()) {
			this.scheduleType.setConstraint(new PTListValidator(
					Labels.getLabel("label_LegalDocumentDialog_ScheduleType.value"), listScheduleType, false));
		}
		if (!this.documentTypeVerify.isDisabled()) {
			this.documentTypeVerify.setConstraint(
					new PTListValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentTypeVerify.value"),
							listDocumentTypeVerify, true));
		}
		if (!this.documentRemarks.isReadonly()) {
			this.documentRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentRemarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (!this.documentName.isReadonly()) {
			this.documentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentReference.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.documentTypeApprove.isDisabled()) {
			this.documentTypeApprove.setConstraint(
					new PTListValidator(Labels.getLabel("label_LegalDocumentDialog_DocumentTypeApprove.value"),
							listDocumentTypeApprove, true));
		}
		if (!this.documentAccepted.isDisabled()) {
			this.documentAccepted.setConstraint(new PTListValidator(
					Labels.getLabel("label_LegalDocumentDialog_DocumentAccepted.value"), listDocumentAccepted, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.documentDate.setConstraint("");
		this.documentDetail.setConstraint("");
		this.documentNo.setConstraint("");
		this.surveyNo.setConstraint("");
		this.documentTypeMaker.setConstraint("");
		this.documentCategory.setConstraint("");
		this.scheduleType.setConstraint("");
		this.documentTypeVerify.setConstraint("");
		this.documentRemarks.setConstraint("");
		this.documentName.setConstraint("");
		this.documentTypeApprove.setConstraint("");
		this.documentAccepted.setConstraint("");

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
	 * Clears validation error messages from all the fields of the dialog
	 * controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.documentName);
		logger.debug("Leaving" + event.toString());
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");
		try {
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.documentPdfView.setContent(
						new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.documentPdfView.setContent(media);
			} else if (docType.equals(PennantConstants.DOC_TYPE_WORD)
					|| docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + fileName + "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(docType);
				list.add(ddaImageData);
				ageementLink.addForward("onClick", window_LegalDocumentDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			}

			if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.setVisible(true);
				this.documentPdfView.setVisible(false);
			} else {
				this.docDiv.setVisible(false);
				this.documentPdfView.setVisible(true);
			}
			textbox.setValue(fileName);
			if (textbox.getAttribute("data") == null) {
				LegalDocument   document = new LegalDocument();
				document.setUploadDocumentType(docType);
				textbox.setAttribute("data", document);
			} else {
				LegalDocument document = (LegalDocument) textbox.getAttribute("data");
				document.setUploadDocumentType(docType);
				document.setDocImage(ddaImageData);
				textbox.setAttribute("data", document);
			}
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	public void onDocumentClicked(Event event) throws Exception {
		@SuppressWarnings("unchecked")
		List<Object> list  = (List<Object>) event.getData();
		String docType = (String) list.get(0);
		byte[] ddaImageData= (byte[]) list.get(1);

		if (docType.equals(PennantConstants.DOC_TYPE_WORD)) {
			Filedownload.save(ddaImageData, "application/msword", this.documentName.getValue());
		} else if (docType.equals(PennantConstants.DOC_TYPE_MSG)) {
			Filedownload.save(ddaImageData, "application/octet-stream", this.documentName.getValue());
		}
	}
	/**
	 * Deletes a LegalDocument object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalDocument aLegalDocument = new LegalDocument();
		BeanUtils.copyProperties(this.legalDocument, aLegalDocument);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aLegalDocument.getDocumentNo();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {

			if (StringUtils.isBlank(aLegalDocument.getRecordType())) {
				aLegalDocument.setVersion(aLegalDocument.getVersion() + 1);
				aLegalDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aLegalDocument.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					aLegalDocument.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewDocumentDetails()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = processDetails(aLegalDocument, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_LegalDocumentDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (getLegalDetailDialogCtrl() != null) {
							getLegalDetailDialogCtrl().doFillDocumentDetails(this.legalDocumentDetailsList);
						}
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.legalDocument.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		// Maker
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentDate"), this.documentDate);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentDetail"), this.documentDetail);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentNo"), this.documentNo);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_SurveyNo"), this.surveyNo);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentTypeMaker"), this.documentTypeMaker);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentCategory"), this.documentCategory);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_ScheduleType"), this.scheduleType);

		// Verifier
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentTypeVerify"), this.documentTypeVerify);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentRemarks"), this.documentRemarks);

		// Approver
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentTypeApprove"), this.documentTypeApprove);
		readOnlyComponent(isReadOnly("LegalDocumentDialog_DocumentAccepted"), this.documentAccepted);

		// Group boxes visibility based on roles
		this.gb_documentBasicDetails
				.setVisible(getUserWorkspace().isAllowed("LegalDocumentDialog_gb_documentBasicDetails"));
		this.gb_documentVerifyDetails
				.setVisible(getUserWorkspace().isAllowed("LegalDocumentDialog_gb_gb_documentVerifyDetails"));
		this.gb_documentApproverDetails
				.setVisible(getUserWorkspace().isAllowed("LegalDocumentDialog_gb_documentApproverDetails"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.legalDocument.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewDocumentDetails()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewDocumentDetails());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}
	

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewDocumentDetails()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}


	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.documentDate);
		readOnlyComponent(true, this.documentDetail);
		readOnlyComponent(true, this.documentNo);
		readOnlyComponent(true, this.surveyNo);
		readOnlyComponent(true, this.documentTypeMaker);
		readOnlyComponent(true, this.documentCategory);
		readOnlyComponent(true, this.scheduleType);
		readOnlyComponent(true, this.documentTypeVerify);
		readOnlyComponent(true, this.documentRemarks);
		readOnlyComponent(true, this.documentName);
		readOnlyComponent(true, this.documentTypeApprove);
		readOnlyComponent(true, this.documentAccepted);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.documentDate.setText("");
		this.documentDetail.setValue("");
		this.documentNo.setValue("");
		this.surveyNo.setValue("");
		this.documentTypeMaker.setSelectedIndex(0);
		this.documentCategory.setSelectedIndex(0);
		this.scheduleType.setSelectedIndex(0);
		this.documentTypeVerify.setSelectedIndex(0);
		this.documentRemarks.setValue("");
		this.documentName.setValue("");
		this.documentTypeApprove.setSelectedIndex(0);
		this.documentAccepted.setSelectedIndex(0);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final LegalDocument aLegalDocument = new LegalDocument();
		BeanUtils.copyProperties(this.legalDocument, aLegalDocument);
		boolean isNew = false;
		
		doRemoveValidation();
		doRemoveLOVValidation();
		doSetValidation();
		doWriteComponentsToBean(aLegalDocument);
		
		
		isNew = aLegalDocument.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalDocument.getRecordType())) {
				aLegalDocument.setVersion(aLegalDocument.getVersion() + 1);
				if (isNew) {
					aLegalDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalDocument.setNewRecord(true);
				}
			}
		} else {
			if (isNewDocumentDetails()) {
				if (isNewRecord()) {
					aLegalDocument.setVersion(1);
					aLegalDocument.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aLegalDocument.getRecordType())) {
					aLegalDocument.setVersion(aLegalDocument.getVersion() + 1);
					aLegalDocument.setRecordType(PennantConstants.RCD_UPD);
					aLegalDocument.setNewRecord(true);
				}
				if (aLegalDocument.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aLegalDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aLegalDocument.setVersion(aLegalDocument.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewDocumentDetails()) {
				AuditHeader auditHeader = processDetails(aLegalDocument, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalDocumentDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getLegalDetailDialogCtrl() != null) {
						getLegalDetailDialogCtrl().doFillDocumentDetails(this.legalDocumentDetailsList);
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

	private AuditHeader processDetails(LegalDocument aLegalDocument, String tranType) {
		boolean recordAdded = false;
		boolean duplicateRecord = !(isNewRecord());

		AuditHeader auditHeader = getAuditHeader(aLegalDocument, tranType);

		this.legalDocumentDetailsList = new ArrayList<>();
		List<LegalDocument> oldLegalDocumentDetailsList = null;

		if (getLegalDetailDialogCtrl() != null) {
			oldLegalDocumentDetailsList = getLegalDetailDialogCtrl().getLegalDocumentList();
		}

		if (oldLegalDocumentDetailsList != null && !oldLegalDocumentDetailsList.isEmpty()) {
			for (LegalDocument oldLegalApplicantDetail : oldLegalDocumentDetailsList) {
				if (duplicateRecord) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aLegalDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aLegalDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.legalDocumentDetailsList.add(aLegalDocument);
						} else if (aLegalDocument.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aLegalDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aLegalDocument.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.legalDocumentDetailsList.add(aLegalDocument);
						} else if (aLegalDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.legalDocumentDetailsList.add(oldLegalApplicantDetail);
						}
					}
				} else {
					this.legalDocumentDetailsList.add(oldLegalApplicantDetail);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.legalDocumentDetailsList.add(aLegalDocument);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.legalDocumentDetailsList.add(aLegalDocument);
		}
		return auditHeader;
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_LegalDocumentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LegalDocument aLegalDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalDocument.getBefImage(), aLegalDocument);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalDocument.getUserDetails(),
				getOverideMap());
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewDocumentDetails() {
		return newDocumentDetails;
	}

	public void setNewDocumentDetails(boolean newDocumentDetails) {
		this.newDocumentDetails = newDocumentDetails;
	}

	public LegalDetailDialogCtrl getLegalDetailDialogCtrl() {
		return legalDetailDialogCtrl;
	}

	public void setLegalDetailDialogCtrl(LegalDetailDialogCtrl legalDetailDialogCtrl) {
		this.legalDetailDialogCtrl = legalDetailDialogCtrl;
	}

	public List<ValueLabel> getListDocumentCategory() {
		return listDocumentCategory;
	}

	public void setListDocumentCategory(List<ValueLabel> listDocumentCategory) {
		this.listDocumentCategory = listDocumentCategory;
	}

}
