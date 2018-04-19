package com.pennant.webui.verification.fieldinvestigation;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FieldInvestigationDocumentDialogCtrl extends GFCBaseCtrl<DocumentDetails>{
	private static final long serialVersionUID = 5689459854836923943L;
	private static Logger logger = Logger.getLogger(FieldInvestigationDocumentDialogCtrl.class);
	
	protected Window window_FieldInvestigationDocumentDialog;
	protected Textbox fIdocumnetName;
	protected Div finDocumentDiv;
	protected Iframe finDocumentPdfView;
	protected Div docDiv;
	protected Button btnUploadDoc;

	private boolean newRecord = false;
	private boolean newDocument = false;
	private String userRole="";
	private DocumentDetails documentDetails;
	private List<DocumentDetails> documentDetailList;
	private FieldInvestigationDialogCtrl fieldInvestigationDialogCtrl;
	protected Map<String, DocumentDetails> docDetailMap = null;
	
	public FieldInvestigationDocumentDialogCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.pageRightName = "FieldInvestigationDialog";
	}
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerDocument
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FieldInvestigationDocumentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FieldInvestigationDocumentDialog);
		
		try {
			if (arguments.containsKey("documentDetails")) {
				this.documentDetails = (DocumentDetails) arguments.get("documentDetails");
				DocumentDetails befImage = new DocumentDetails();
				BeanUtils.copyProperties(this.documentDetails, befImage);
				this.documentDetails.setBefImage(befImage);
				setDocumentDetails(this.documentDetails);
			} else {
				setDocumentDetails(null);
			}
			
			if (arguments.containsKey("fieldInvestigationDialogCtrl")) {

				setFieldInvestigationDialogCtrl(
						(FieldInvestigationDialogCtrl) arguments.get("fieldInvestigationDialogCtrl"));
				setNewDocument(true);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				if (arguments.containsKey("roleCode")) {
					getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "FIDocumentDialog");
				}
			}
			
			if (getDocumentDetails().isNewRecord()) {
				setNewRecord(true);
			}
			
			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole,
						"FieldInvestigationDialog");
			}
			
			doLoadWorkFlow(this.documentDetails.isWorkflow(),
					this.documentDetails.getWorkflowId(),
					this.documentDetails.getNextTaskId());
			
			this.finDocumentDiv.setHeight(this.borderLayoutHeight - 260 + "px");// 425px
			this.finDocumentPdfView.setHeight(this.borderLayoutHeight - 220 + "px");// 425px
			doCheckRights();
			doShowDialog(this.documentDetails);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerDocument
	 * @throws Exception
	 */
	public void doShowDialog(DocumentDetails documentDetails)
			throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.btnUploadDoc.focus();
		} else {
			if (isNewDocument()){
				doEdit();
				if (getRole().equals(getFirstTaskOwner())) {
					this.btnCtrl.setWFBtnStatus_Edit(true);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(false);
				}
			}else if (isWorkFlowEnabled()) {
				doEdit();
				if (getRole().equals(getFirstTaskOwner())) {
					this.btnCtrl.setWFBtnStatus_Edit(true);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(false);
				}
			}else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(documentDetails);

			if (isNewRecord()) {
				this.groupboxWf.setVisible(false);
			}
			
			if(enqiryModule) {
				this.window_FieldInvestigationDocumentDialog.setHeight("80%");
				this.window_FieldInvestigationDocumentDialog.setWidth("70%");
				this.window_FieldInvestigationDocumentDialog.doModal();
			} else {
				setDialog(DialogType.OVERLAPPED);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FieldInvestigationDocumentDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}
	
	
	
	
	private void doWriteBeanToComponents(DocumentDetails documentDetails) {
		logger.debug("Entering");
	
		this.fIdocumnetName.setValue(documentDetails.getDocName());
		this.fIdocumnetName.setAttribute("data", documentDetails);
		
		AMedia amedia = null;
		if (documentDetails.getDocImage() != null) {
			if (documentDetails.getDoctype().equals(
					PennantConstants.DOC_TYPE_WORD) || documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>"
						+ documentDetails.getDocName() + "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(documentDetails.getDoctype());
				list.add(documentDetails.getDocImage());

				ageementLink.addForward("onClick", window_FieldInvestigationDocumentDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			}else{
				amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
			}
			finDocumentPdfView.setContent(amedia);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
		    this.btnUploadDoc.setVisible(true);
			this.btnCancel.setVisible(false);
		} else {
			this.btnUploadDoc.setVisible(false);
			this.btnCancel.setVisible(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.documentDetails.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {

			if (newDocument) {
				  if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newDocument);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities(this.pageRightName,userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDocumentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDocumentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDocumentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDocumentDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
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
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}
	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		String docName = this.fIdocumnetName.getValue(); 
		doDelete(docName);
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.documentDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		//this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Deletes a DocumentDetails object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete(String doctype) throws InterruptedException {
		logger.debug("Entering");
		final DocumentDetails aDocumentDetails = new DocumentDetails();
		BeanUtils.copyProperties(getDocumentDetails(), aDocumentDetails);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_FinDocumentDetailDialog_DocCategory.value")+" : "+aDocumentDetails.getDocName();
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
				aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
				aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aDocumentDetails.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aDocumentDetails.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewDocument()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newDocumentProcess(aDocumentDetails, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FieldInvestigationDocumentDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getFieldInvestigationDialogCtrl().doFillDocumentDetails(this.documentDetailList);
						
						closeDialog();
					}

				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}
	
	
	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.fIdocumnetName);
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

			//Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.finDocumentPdfView.setContent(new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.finDocumentPdfView.setContent(media);
			}else if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.getChildren().clear();
				Html ageementLink = new Html();
				ageementLink.setStyle("padding:10px;");
				ageementLink.setContent("<a href='' style = 'font-weight:bold'>" + fileName+ "</a> ");

				List<Object> list = new ArrayList<Object>();
				list.add(docType);
				list.add(ddaImageData);
				ageementLink.addForward("onClick", window_FieldInvestigationDocumentDialog, "onDocumentClicked", list);
				this.docDiv.appendChild(ageementLink);
			}

			if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)) {
				this.docDiv.setVisible(true);
				this.finDocumentPdfView.setVisible(false);
			}else{
				this.docDiv.setVisible(false);
				this.finDocumentPdfView.setVisible(true);
			}


			textbox.setValue(fileName);
			if (textbox.getAttribute("data") == null) {
				DocumentDetails documentDetails = new DocumentDetails(VerificationType.FI.getCode(), "", docType, fileName, ddaImageData);
				textbox.setAttribute("data", documentDetails);
			} else {
				DocumentDetails documentDetails = (DocumentDetails) textbox.getAttribute("data");
				documentDetails.setDoctype(docType);
				documentDetails.setDocImage(ddaImageData);
				textbox.setAttribute("data", documentDetails);
			}
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}
	
	
	
	public void doWriteComponentsToBean(DocumentDetails aDocumentDetails) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		aDocumentDetails.setDocModule(VerificationType.FI.getCode());
		
		try {
			
			aDocumentDetails.setDocName(this.fIdocumnetName.getValue());
			if (this.fIdocumnetName.getAttribute("data") != null) {
				DocumentDetails details = (DocumentDetails) this.fIdocumnetName.getAttribute("data");
				aDocumentDetails.setDocImage(details.getDocImage());
				aDocumentDetails.setDoctype(details.getDoctype());
			} else {
				aDocumentDetails.setDocImage(null);
				aDocumentDetails.setDoctype(null);
			}
			aDocumentDetails.setDocRefId(Long.MIN_VALUE);

		} catch (WrongValueException we) {
			wve.add(we);
		}
		

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aDocumentDetails.setRecordStatus(this.recordStatus.getValue());
		setDocumentDetails(aDocumentDetails);
		logger.debug("Leaving");

	}
	
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}
	
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DocumentDetails aDocumentDetails = new DocumentDetails();
		BeanUtils.copyProperties(getDocumentDetails(), aDocumentDetails);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		//doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aDocumentDetails);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aDocumentDetails.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
				aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
				if (isNew) {
					aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDocumentDetails.setNewRecord(true);
				}
			}
		} else {

			if (isNewDocument()) {
				if (isNewRecord()) {
					aDocumentDetails.setVersion(1);
					aDocumentDetails.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aDocumentDetails.getRecordType())) {
					aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
					aDocumentDetails.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aDocumentDetails.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aDocumentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aDocumentDetails.setVersion(aDocumentDetails.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewDocument()) {
				AuditHeader auditHeader = newDocumentProcess(aDocumentDetails, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FieldInvestigationDocumentDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFieldInvestigationDialogCtrl().doFillDocumentDetails(this.documentDetailList);
					
					 closeDialog();
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		closeDialog();
		logger.debug("Leaving");
	}
	
	private AuditHeader newDocumentProcess(DocumentDetails aDocumentDetails, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aDocumentDetails, tranType);
		documentDetailList = new ArrayList<DocumentDetails>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = aDocumentDetails.getDocName();
		errParm[0] = PennantJavaUtil.getLabel("label_DocumnetCategory") + ":" + valueParm[0];
		if (getFieldInvestigationDialogCtrl().getDocumentDetailsList() != null && getFieldInvestigationDialogCtrl().getDocumentDetailsList().size() > 0) {
			for (int i = 0; i < getFieldInvestigationDialogCtrl().getDocumentDetailsList().size(); i++) {
				DocumentDetails documentDetails = getFieldInvestigationDialogCtrl().getDocumentDetailsList().get(i);
				if (documentDetails.getDocName().equals(aDocumentDetails.getDocName())) { // Both Current and Existing list rating same

					if (isNewRecord()) {
						if(!StringUtils.equals(documentDetails.getRecordType(), PennantConstants.RECORD_TYPE_CAN)){							
							auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
							return auditHeader;
						}
						
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aDocumentDetails.getRecordType())) {
							aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							documentDetailList.add(aDocumentDetails);
						} else if (PennantConstants.RCD_ADD.equals(aDocumentDetails.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aDocumentDetails.getRecordType())) {
							aDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							documentDetailList.add(aDocumentDetails);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aDocumentDetails.getRecordType())) {
							recordAdded = true;
							/*		for (int j = 0; j < getFinanceMainDialogCtrl().getFinanceDetail().getFinContributorHeader().getContributorDetailList().size(); j++) {
										DocumentDetails detail =  getFinanceMainDialogCtrl().getFinanceDetail().getFinContributorHeader().getContributorDetailList().get(j);
										if(detail.getCustID() == aDocumentDetails.getCustID()){
											contributorDetails.add(detail);
										}
									}*/
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							documentDetailList.add(documentDetails);
						}
					}
				} else {
					documentDetailList.add(documentDetails);
				}
			}
		}
		if (!recordAdded) {
			documentDetailList.add(aDocumentDetails);
		}
		return auditHeader;
	}
	
	private AuditHeader getAuditHeader(DocumentDetails aDocumentDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDocumentDetails.getBefImage(), aDocumentDetails);

		return new AuditHeader(getReference(), String.valueOf(aDocumentDetails.getDocId()), null, null, auditDetail, aDocumentDetails.getUserDetails(), getOverideMap());
	}
	
	
	public void onDocumentClicked(Event event) throws Exception {

		@SuppressWarnings("unchecked")
		List<Object> list  = (List<Object>) event.getData();
		String docType = (String) list.get(0);
		byte[] ddaImageData= (byte[]) list.get(1);

		if(docType.equals(PennantConstants.DOC_TYPE_WORD)){
			Filedownload.save(ddaImageData, "application/msword", this.fIdocumnetName.getValue());
		}else if(docType.equals(PennantConstants.DOC_TYPE_MSG)){
			Filedownload.save(ddaImageData, "application/octet-stream", this.fIdocumnetName.getValue());
		}
	}
	
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public DocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(DocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewDocument() {
		return newDocument;
	}

	public void setNewDocument(boolean newDocument) {
		this.newDocument = newDocument;
	}

	public List<DocumentDetails> getDocumentDetailList() {
		return documentDetailList;
	}

	public void setDocumentDetailList(List<DocumentDetails> documentDetailList) {
		this.documentDetailList = documentDetailList;
	}

	public FieldInvestigationDialogCtrl getFieldInvestigationDialogCtrl() {
		return fieldInvestigationDialogCtrl;
	}

	public void setFieldInvestigationDialogCtrl(FieldInvestigationDialogCtrl fieldInvestigationDialogCtrl) {
		this.fieldInvestigationDialogCtrl = fieldInvestigationDialogCtrl;
	}
}
