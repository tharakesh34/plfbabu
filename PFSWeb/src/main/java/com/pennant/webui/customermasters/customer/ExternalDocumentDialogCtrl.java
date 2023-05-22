package com.pennant.webui.customermasters.customer;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExternalDocumentDialogCtrl extends GFCBaseCtrl<ExternalDocument> {
	private static final long serialVersionUID = 2559057422619011362L;
	private static final Logger logger = LogManager.getLogger(ExternalDocumentDialogCtrl.class);

	protected Window window_ExtDocumentDetailDialog;
	protected Datebox fromDate;
	protected Datebox toDate;
	protected Radiogroup scanned;
	protected Radio scan;
	protected Radio passWrd;
	protected Radio normal;
	protected Textbox password;
	protected Textbox documnetName;
	protected Button btnUploadDoc;
	protected A btnDownload;
	protected Div docDiv;
	protected Iframe finDocumentPdfView;

	private ExternalDocument externalDocument;
	private boolean newRecord = false;
	private boolean newDocument = false;
	private List<ExternalDocument> documentDetailList;
	private CustomerBankInfoDialogCtrl customerBankInfoDialogCtrl;
	private String finReference;

	public ExternalDocumentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DocumentDetailsDialog";
	}

	public void onCreate$window_ExtDocumentDetailDialog(Event event) {

		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ExtDocumentDetailDialog);

		try {

			if (arguments.containsKey("externalDocument")) {
				this.externalDocument = (ExternalDocument) arguments.get("externalDocument");
				DocumentDetails befImage = new DocumentDetails();
				BeanUtils.copyProperties(this.externalDocument, befImage);
				this.externalDocument.setBefImage(befImage);
				setExternalDocument(this.externalDocument);
			} else {
				setExternalDocument(null);
			}

			if (arguments.containsKey("finReference")) {
				finReference = (String) arguments.get("finReference");
			}

			if (arguments.containsKey("customerBankInfoDialogCtrl")) {

				setCustomerBankInfoDialogCtrl((CustomerBankInfoDialogCtrl) arguments.get("customerBankInfoDialogCtrl"));
				setNewDocument(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.externalDocument.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"),
							"DocumentDetailsDialog");
				}
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			checkRights();
			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		if (externalDocument.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fromDate.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.fromDate.focus();
				if (StringUtils.isNotBlank(externalDocument.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents(externalDocument);
		this.window_ExtDocumentDetailDialog.setHeight("70%");
		this.window_ExtDocumentDetailDialog.setWidth("80%");
		this.window_ExtDocumentDetailDialog.doModal();
	}

	private void doWriteBeanToComponents(ExternalDocument externalDocument) {

		this.documnetName.setAttribute("data", externalDocument);
		this.scanned.setSelectedItem(this.passWrd);
	}

	private void doReadOnly() {
		// TODO Auto-generated method stub

	}

	private void doEdit() {
		// TODO Auto-generated method stub

	}

	private void doSetFieldProperties() {

		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());

	}

	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final ExternalDocument aExternalDocument = new ExternalDocument();
		BeanUtils.copyProperties(getExternalDocument(), aExternalDocument);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aExternalDocument);

		isNew = aExternalDocument.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExternalDocument.getRecordType())) {
				aExternalDocument.setVersion(aExternalDocument.getVersion() + 1);
				if (isNew) {
					aExternalDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExternalDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExternalDocument.setNewRecord(true);
				}
			}
		} else {

			if (isNewDocument()) {
				if (isNewRecord()) {
					aExternalDocument.setVersion(1);
					aExternalDocument.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aExternalDocument.getRecordType())) {
					aExternalDocument.setVersion(aExternalDocument.getVersion() + 1);
					aExternalDocument.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aExternalDocument.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aExternalDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aExternalDocument.setVersion(aExternalDocument.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		try {
			if (isNewDocument()) {
				AuditHeader auditHeader = newDocumentProcess(aExternalDocument, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ExtDocumentDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerBankInfoDialogCtrl().doFillExternalDocuments(this.documentDetailList);
					closeDialog();
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newDocumentProcess(ExternalDocument aExternalDocument, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aExternalDocument, tranType);
		documentDetailList = new ArrayList<ExternalDocument>();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = aExternalDocument.getDocName();
		valueParm[1] = DateUtil.getDatePart(aExternalDocument.getFromDate()).toString().concat("-")
				.concat(DateUtil.getDatePart(aExternalDocument.getToDate()).toString());
		valueParm[2] = DateUtil.getDatePart(aExternalDocument.getToDate()).toString();

		errParm[0] = PennantJavaUtil.getLabel("listheader_DocName.label") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("listheader_FromDate.label") + "-"
				+ PennantJavaUtil.getLabel("listheader_ToDate.label") + ":" + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("listheader_ToDate.label") + ":" + valueParm[2];

		if (CollectionUtils.isNotEmpty(getCustomerBankInfoDialogCtrl().getExternalDocumentsList())) {

			for (int i = 0; i < getCustomerBankInfoDialogCtrl().getExternalDocumentsList().size(); i++) {

				ExternalDocument externalDocment = getCustomerBankInfoDialogCtrl().getExternalDocumentsList().get(i);

				if (StringUtils.equals(externalDocment.getDocName(), aExternalDocument.getDocName())
						&& DateUtil.getDatePart(externalDocment.getFromDate())
								.compareTo(DateUtil.getDatePart(aExternalDocument.getFromDate())) == 0
						&& DateUtil.getDatePart(externalDocment.getToDate())
								.compareTo(DateUtil.getDatePart(aExternalDocument.getToDate())) == 0) {

					if (isNewRecord()) {
						if (!StringUtils.equals(externalDocment.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
							auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
									getUserWorkspace().getUserLanguage()));
							return auditHeader;
						}
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aExternalDocument.getRecordType())) {
							aExternalDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							documentDetailList.add(aExternalDocument);
						} else if (PennantConstants.RCD_ADD.equals(aExternalDocument.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aExternalDocument.getRecordType())) {
							aExternalDocument.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							documentDetailList.add(aExternalDocument);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aExternalDocument.getRecordType())) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							documentDetailList.add(externalDocment);
						}
					}
				} else {
					documentDetailList.add(externalDocment);
				}
			}
		}
		if (!recordAdded) {
			documentDetailList.add(aExternalDocument);
		}
		return auditHeader;
	}

	private AuditHeader getAuditHeader(ExternalDocument externalDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, externalDocument.getBefImage(), externalDocument);

		return new AuditHeader(getReference(), String.valueOf(externalDocument.getId()), null, null, auditDetail,
				externalDocument.getUserDetails(), getOverideMap());
	}

	private void doWriteComponentsToBean(ExternalDocument aExternalDocument) {

		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			Calendar calDate = Calendar.getInstance();
			if (this.fromDate.getValue() != null) {
				calDate.setTime(this.fromDate.getValue());
				Calendar calTimeNow = Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTimeNow.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTimeNow.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTimeNow.get(Calendar.SECOND));
				aExternalDocument.setFromDate(new Timestamp(calDate.getTimeInMillis()));
			} else {
				aExternalDocument.setFromDate(new Timestamp(calDate.getTimeInMillis()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Calendar calDate = Calendar.getInstance();
			if (this.toDate.getValue() != null) {
				calDate.setTime(this.toDate.getValue());
				Calendar calTimeNow = Calendar.getInstance();
				calDate.set(Calendar.HOUR_OF_DAY, calTimeNow.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calTimeNow.get(Calendar.MINUTE));
				calDate.set(Calendar.SECOND, calTimeNow.get(Calendar.SECOND));
				aExternalDocument.setToDate(new Timestamp(calDate.getTimeInMillis()));
			} else {
				aExternalDocument.setToDate(new Timestamp(calDate.getTimeInMillis()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aExternalDocument.setPasswordProtected(this.scanned.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isNotEmpty(this.password.getValue())) {
				aExternalDocument.setPassword(this.password.getValue());
			} else {
				aExternalDocument.setPassword(this.password.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aExternalDocument.setDocName(this.documnetName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.documnetName.getAttribute("data") != null) {
				ExternalDocument details = (ExternalDocument) this.documnetName.getAttribute("data");
				aExternalDocument.setDocImage(details.getDocImage());
				aExternalDocument.setDocType(details.getDocType());
				aExternalDocument.setDocRefId(details.getDocRefId());
			} else {
				aExternalDocument.setDocImage(null);
				aExternalDocument.setDocType(null);
				aExternalDocument.setDocRefId(0);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		aExternalDocument.setFinReference(finReference);

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.ENTERING);

	}

	private void doRemoveValidation() {

		this.documnetName.setConstraint("");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.password.setConstraint("");
	}

	@Override
	protected void doClearMessage() {

		this.documnetName.setErrorMessage("");
		this.fromDate.setErrorMessage("");
		this.toDate.setErrorMessage("");
		this.password.setErrorMessage("");

	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.fromDate.isDisabled()) {
			this.fromDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_ExtDocumentDetailDialog_FromDate.vlaue"),
							true, null, DateUtil.getDatePart(SysParamUtil.getAppDate()), true));
		}

		if (!this.toDate.isDisabled()) {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ExtDocumentDetailDialog_ToDate.value"),
					true, this.fromDate.getValue(), DateUtil.getDatePart(SysParamUtil.getAppDate()), true));
		}

		if (!this.btnUploadDoc.isDisabled()) {
			this.documnetName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ExtDocumentDetailDialog_DocumnetName.value"), null, true));
		}

		if (!this.password.isReadonly()) {
			this.password.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ExtDocumentDetailDialog_Password.value"), null, this.passWrd.isChecked()));
		}

		logger.debug(Literal.LEAVING);
	}

	public void onUpload$btnUploadDoc(UploadEvent event) {
		logger.debug("Entering");

		Media media = event.getMedia();

		if (!PennantAppUtil.uploadDocFormatValidation(media)) {
			return;
		}
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DOCUMENTTYPE_XLS_REQ)) {
			if (media.getName().endsWith(".xls") || media.getName().endsWith(".xlsx")) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
				return;
			}
			if (media.getName().endsWith(".csv") || media.getName().endsWith(".CSV")) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
				return;
			}
			if (media.getName().endsWith(".XLS") || media.getName().endsWith(".XLSX")) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
				return;
			}
		}

		browseDoc(media, this.documnetName);

		doSetDownLoadVisible();
		logger.debug("Leaving");
	}

	public void onCheck$scanned(Event event) {

		this.password.setErrorMessage("");
	}

	private void browseDoc(Media media, Textbox textbox) {
		logger.debug("Entering");

		try {
			boolean isSupported = true;
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else if (media.getName().endsWith(".xls") || media.getName().endsWith(".xlsx")) {
				docType = PennantConstants.DOC_TYPE_EXCEL;
			} else if ("application/x-zip-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_ZIP;
			} else if ("application/octet-stream".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_7Z;
			} else if ("application/x-rar-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_RAR;
			} else {
				isSupported = false;
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
			}
			if (isSupported) {
				String fileName = media.getName();
				final byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
				// Data Fill by QR Bar Code Reader
				if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
					this.finDocumentPdfView
							.setContent(new AMedia(fileName, null, null, new ByteArrayInputStream(ddaImageData)));

				} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
					this.finDocumentPdfView.setContent(media);
				} else if (docType.equals(PennantConstants.DOC_TYPE_WORD)
						|| docType.equals(PennantConstants.DOC_TYPE_MSG)
						|| docType.equals(PennantConstants.DOC_TYPE_EXCEL)) {
					this.docDiv.getChildren().clear();
					this.docDiv.appendChild(
							getDocumentLink(fileName, docType, this.documnetName.getValue(), ddaImageData));
				} else if (docType.equals(PennantConstants.DOC_TYPE_ZIP) || docType.equals(PennantConstants.DOC_TYPE_7Z)
						|| docType.equals(PennantConstants.DOC_TYPE_RAR)) {
					this.docDiv.getChildren().clear();
					this.docDiv.appendChild(
							getDocumentLink(fileName, docType, this.documnetName.getValue(), ddaImageData));
				}

				if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)
						|| docType.equals(PennantConstants.DOC_TYPE_EXCEL)
						|| docType.equals(PennantConstants.DOC_TYPE_ZIP) || docType.equals(PennantConstants.DOC_TYPE_7Z)
						|| docType.equals(PennantConstants.DOC_TYPE_RAR)) {
					this.docDiv.setVisible(true);
					this.finDocumentPdfView.setVisible(false);
				} else {
					this.docDiv.setVisible(false);
					this.finDocumentPdfView.setVisible(true);
				}

				textbox.setValue(fileName);
				if (textbox.getAttribute("data") == null) {
					ExternalDocument externalDocument = new ExternalDocument(docType, fileName, ddaImageData);
					externalDocument.setLovDescNewImage(true);
					textbox.setAttribute("data", externalDocument);
				} else {
					ExternalDocument externalDocument = (ExternalDocument) textbox.getAttribute("data");
					externalDocument.setDocType(docType);
					externalDocument.setDocImage(ddaImageData);
					externalDocument.setDocUri(null);
					externalDocument.setDocRefId(0);
					externalDocument.setLovDescNewImage(true);
					textbox.setAttribute("data", externalDocument);
				}
			}
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}

		logger.debug("Leaving");
	}

	private void doSetDownLoadVisible() {
		this.btnDownload.setVisible(false);
		if (StringUtils.isNotBlank(this.documnetName.getValue())) {
			this.btnDownload.setLabel(this.documnetName.getValue());
			this.btnDownload.setVisible(true);
		}
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public ExternalDocument getExternalDocument() {
		return externalDocument;
	}

	public void setExternalDocument(ExternalDocument externalDocument) {
		this.externalDocument = externalDocument;
	}

	public CustomerBankInfoDialogCtrl getCustomerBankInfoDialogCtrl() {
		return customerBankInfoDialogCtrl;
	}

	public void setCustomerBankInfoDialogCtrl(CustomerBankInfoDialogCtrl customerBankInfoDialogCtrl) {
		this.customerBankInfoDialogCtrl = customerBankInfoDialogCtrl;
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

}
