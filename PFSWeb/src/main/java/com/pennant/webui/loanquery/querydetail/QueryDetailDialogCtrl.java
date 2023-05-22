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
 * * FileName : QueryDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-05-2018 * *
 * Modified Date : 09-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.loanquery.querydetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.notifications.service.NotificationService;

import freemarker.template.Configuration;

/**
 * This is the controller class for the /WEB-INF/pages/LoanQuery/QueryDetail/queryDetailDialog.zul file. <br>
 */
public class QueryDetailDialogCtrl extends GFCBaseCtrl<QueryDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(QueryDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Tabs tabsIndexCenter;
	protected Tab queryDetails;
	protected Window window_QueryDetailDialog;
	protected ExtendedCombobox finReference;
	protected Textbox reference;
	protected ExtendedCombobox qryCategory;
	protected Space space_QryNotes;
	protected Textbox qryNotes;
	protected Space space_AssignedRole;
	protected Combobox assignedRole;
	protected Space space_NotifyTo;
	protected Textbox notifyTo;
	protected Space space_Status;
	protected Combobox status;
	protected Space space_RaisedBy;
	protected Textbox raisedBy;
	protected Space space_RaisedOn;
	protected Datebox raisedOn;
	protected Textbox responsNotes;
	protected Textbox responseBy;
	protected Datebox responseOn;
	protected Label label_CloserNotes;
	protected Label label_CloserBy;
	protected Label label_CloserOn;
	protected Textbox closerNotes;
	protected Textbox closerBy;
	protected Longbox id;
	protected Datebox closerOn;
	protected Textbox documnetName;
	protected Textbox docRemarks;
	protected Listbox listBoxQueryDetail;
	protected Row row5;
	protected Row row6;
	protected Row row7;
	protected Row row8;
	protected Listheader listheader_RadioButton;
	protected Button delete;
	protected ExtendedCombobox custDocType;
	protected Button btnUploadDoc;
	protected Button btnUploadDocs;
	protected Button btnNotifyTo;
	protected Textbox qryNotesMnt;
	protected Textbox responsNotesMnt;
	protected Textbox module;

	private boolean enquiry = false;
	// protected Textbox code;
	// protected Textbox description;

	private final List<ValueLabel> queryModuleStatusList = PennantStaticListUtil.getQueryModuleStatusList();
	private List<ValueLabel> roles = new ArrayList<>();
	private List<DocumentDetails> documentDetails = new ArrayList<>();

	private QueryDetail queryDetail; // overhanded per param
	private FinanceMain financeMain = null;

	private transient QueryDetailListCtrl queryDetailListCtrl; // overhanded
	private transient FinQueryDetailListCtrl finQueryDetailListCtrl; // overhanded
	// per
	// param
	private transient QueryDetailService queryDetailService;
	private FinanceMainService financeMainService;
	private MailTemplateService mailTemplateService;
	private NotificationService notificationService;
	private Configuration freemarkerMailConfiguration;
	private SecurityUserOperationsService securityUserOperationsService;
	List<String> emailList = null;
	private String roleCode;
	private Sampling sampling = null;
	private LegalDetail legalDetail = null;

	/**
	 * default constructor.<br>
	 */
	public QueryDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "QueryDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.queryDetail.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_QueryDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_QueryDetailDialog);

		try {
			// Get the required arguments.
			this.queryDetail = (QueryDetail) arguments.get("queryDetail");
			this.setFinQueryDetailListCtrl((FinQueryDetailListCtrl) arguments.get("finQueryDetailListCtrl"));

			this.setQueryDetailListCtrl((QueryDetailListCtrl) arguments.get("queryDetailListCtrl"));

			if (arguments.containsKey("financeMain")) {
				this.financeMain = (FinanceMain) arguments.get("financeMain");
				this.module.setValue(PennantConstants.QUERY_ORIGINATION);
			}

			if (arguments.containsKey("sampling")) {
				this.sampling = (Sampling) arguments.get("sampling");
				this.module.setValue(PennantConstants.QUERY_SAMPLING);
			}

			if (arguments.containsKey("legalDetail")) {
				this.legalDetail = (LegalDetail) arguments.get("legalDetail");
				this.module.setValue(PennantConstants.QUERY_LEGAL_VERIFICATION);
			}

			if (arguments.containsKey("legalModuleName")) {
				String legalModuleName = (String) arguments.get("legalModuleName");
				if (StringUtils.trimToNull(legalModuleName) != null
						&& StringUtils.trimToNull(this.module.getValue()) == null) {
					this.module.setValue(legalModuleName);
				}
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			if (this.queryDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}
			doLoadWorkFlow(this.queryDetail.isWorkflow(), this.queryDetail.getWorkflowId(),
					this.queryDetail.getNextTaskId());
			// Store the before image.
			QueryDetail queryDetail = new QueryDetail();
			BeanUtils.copyProperties(this.queryDetail, queryDetail);
			this.queryDetail.setBefImage(queryDetail);

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.queryDetail);
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
		doReadOnly();

		if (this.queryDetail != null) {
			this.finReference.setMandatoryStyle(true);
			this.finReference.setTextBoxWidth(151);
			this.finReference.setModuleName("QryFinanceMain");
			this.finReference.setValueColumn("FinReference");
			this.finReference.setDescColumn("FinType");
			this.finReference.setValidateColumns(new String[] { "FinReference" });
		}

		this.qryCategory.setMandatoryStyle(true);
		this.qryCategory.setMaxlength(50);
		this.qryCategory.setTextBoxWidth(80);
		this.qryCategory.setModuleName("QueryCategory");
		this.qryCategory.setValueColumn("Code");
		this.qryCategory.setDescColumn("Description");
		this.qryCategory.setValidateColumns(new String[] { "Code" });

		this.custDocType.setProperties("CustDocumentType", "DocTypeCode", "DocTypeDesc", false, 25);
		this.custDocType.setValidateColumns(new String[] { "DocTypeCode" });
		this.custDocType.setMaxlength(50);
		this.custDocType.setTextBoxWidth(80);
		// this.qryNotes.setMaxlength(2000);
		this.assignedRole.setMaxlength(100);
		this.notifyTo.setMaxlength(1000);
		this.raisedBy.setMaxlength(19);
		this.raisedOn.setDisabled(true);
		this.raisedOn.setFormat(PennantConstants.dateTimeFormat);
		// this.responsNotes.setMaxlength(2000);
		this.responseBy.setMaxlength(19);
		this.responseOn.setFormat(PennantConstants.dateTimeFormat);
		// this.closerNotes.setMaxlength(2000);
		this.closerBy.setMaxlength(19);
		this.closerOn.setFormat(PennantConstants.dateTimeFormat);
		this.docRemarks.setMaxlength(50);
		this.module.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnDelete"));
		// this.btnSave.setVisible(getUserWorkspace().isAllowed("button_QueryDetailDialog_btnSave"));
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);

		if (isEnquiry()) {
			this.btnSave.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.documnetName);
		logger.debug("Leaving" + event.toString());
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");

		try {

			List<DocType> allowed = new ArrayList<>();
			allowed.add(DocType.PDF);
			allowed.add(DocType.JPEG);
			allowed.add(DocType.JPG);
			allowed.add(DocType.PNG);
			allowed.add(DocType.DOC);
			allowed.add(DocType.DOCX);
			allowed.add(DocType.XLS);
			allowed.add(DocType.XLSX);
			allowed.add(DocType.ZIP);
			allowed.add(DocType.Z7);
			allowed.add(DocType.RAR);
			allowed.add(DocType.TXT);
			allowed.add(DocType.MSG);

			if (!MediaUtil.isValid(media, allowed)) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
				return;
			}

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
			} else if (media.getName().endsWith(".sql")) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			} else if (media.getName().endsWith(".exe")) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			} else if ("application/octet-stream".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_7Z;
			} else if ("application/x-rar-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_RAR;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] docData = IOUtils.toByteArray(media.getStreamData());
			textbox.setValue(fileName);
			if (textbox.getAttribute("data") == null) {
				DocumentDetails documentDetail = new DocumentDetails();
				documentDetail.setReferenceId(this.finReference.getValue());
				documentDetail.setDocName(fileName);
				documentDetail.setDocImage(docData);
				documentDetail.setDocModule(FinanceConstants.QUERY_MANAGEMENT);
				documentDetail.setDoctype(docType);
				documentDetail.setNewRecord(true);
				documentDetail.setCustDocVerifiedBy(getUserWorkspace().getUserDetails().getUserId());
				documentDetail.setDocReceived(true);
				documentDetail.setDocReceivedDate(SysParamUtil.getAppDate());
				documentDetail.setVersion(1);
				documentDetail.setLastMntBy(getUserWorkspace().getUserId());
				documentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				// documentDetail.setCustId();
				textbox.setAttribute("data", documentDetail);
			} else {
				DocumentDetails documentDetail = (DocumentDetails) textbox.getAttribute("data");
				documentDetail.setDocImage(docData);
				documentDetail.setDocModule(FinanceConstants.QUERY_MANAGEMENT);
				documentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				textbox.setAttribute("data", documentDetail);
			}
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the upload button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnUploadDocs(Event event) {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(documnetName);
		Clients.clearWrongValue(custDocType);

		if (this.documnetName.getValue() == null || StringUtils.isEmpty(this.documnetName.getValue())) {
			throw new WrongValueException(this.documnetName, Labels.getLabel("MUST_BE_UPLOADED",
					new String[] { Labels.getLabel("label_QueryDetailDialog_Attachment.value") }));
		} else if (this.custDocType.getValidatedValue() == null
				|| StringUtils.isEmpty(this.custDocType.getValidatedValue())) {
			throw new WrongValueException(this.custDocType, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_QueryDetailDialog_CustDocType.value") }));
		} else {
			DocumentDetails documentDetail = (DocumentDetails) this.documnetName.getAttribute("data");

			// Remarks
			documentDetail.setDocPurpose(this.docRemarks.getValue());
			documentDetail.setDocCategory(this.custDocType.getValue());
			// Object adding to List
			getDocumentDetails().add(documentDetail);
			// clear values
			this.documnetName.setAttribute("data", null);
			this.documnetName.setValue("");
			this.custDocType.setValue("");
			this.custDocType.setDescription("");
			this.docRemarks.setValue("");
		}
		doFillDocumentDetails(getDocumentDetails());
		logger.debug(Literal.LEAVING);
	}

	public void doFillDocumentDetails(List<DocumentDetails> documentDetails) {
		logger.debug("Entering");
		this.listBoxQueryDetail.getItems().clear();
		setDocumentDetails(documentDetails);
		if (documentDetails != null) {
			if (!documentDetails.isEmpty()) {
				for (DocumentDetails documentDetail : documentDetails) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(String.valueOf(documentDetail.getLastMntBy()));
					lc.setParent(item);
					lc = new Listcell(documentDetail.getDocName());
					lc.setParent(item);
					lc = new Listcell(documentDetail.getDocPurpose());
					lc.setParent(item);
					lc = new Listcell();
					Hbox hbox = new Hbox();
					Button view = new Button();
					view.setLabel("View");
					view.addForward("onClick", this.window_QueryDetailDialog, "onClickViewButton", documentDetail);
					delete = new Button();
					delete.setLabel("Delete");
					delete.addForward("onClick", this.window_QueryDetailDialog, "onClickDeleteButton", documentDetail);
					if (!documentDetail.isNewRecord()) {
						delete.setVisible(false);
					}
					view.setParent(hbox);
					delete.setParent(hbox);
					hbox.setParent(lc);
					lc.setParent(item);
					item.setAttribute("data", documentDetail);
					this.listBoxQueryDetail.appendChild(item);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onClickDeleteButton(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem listitem = (Listitem) event.getOrigin().getTarget().getParent().getParent().getParent();
		DocumentDetails detail = (DocumentDetails) listitem.getAttribute("data");
		if (detail != null && !detail.isNewRecord()) {
			detail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
			detail.setRecordType(PennantConstants.RCD_DEL);
		}
		getDocumentDetails().remove(listitem.getIndex());
		listBoxQueryDetail.removeChild(listitem);
		doFillDocumentDetails(getDocumentDetails());
		listitem.setAttribute("data", detail);

		logger.debug(Literal.LEAVING);
	}

	public void onClickViewButton(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		DocumentDetails documentDetails = (DocumentDetails) event.getData();

		// Set Image data to bean
		if (documentDetails != null && documentDetails.getDocImage() == null && documentDetails.getDocRefId() != null) {
			byte[] docManager = queryDetailService.getdocImage(documentDetails.getDocRefId());
			if (docManager != null) {
				documentDetails.setDocImage(docManager);
			}
		}

		String docName = documentDetails.getDocName().toLowerCase();
		if (docName.endsWith(".doc") || docName.endsWith(".docx")) {
			Filedownload.save(new AMedia(docName, "msword", "application/msword", documentDetails.getDocImage()));
		} else if (docName.endsWith(".xls") || docName.endsWith(".xlsx")) {
			Filedownload.save(new AMedia(docName, "xls", "application/vnd.ms-excel", documentDetails.getDocImage()));
		} else if (docName.endsWith(".png") || docName.endsWith(".jpeg") || docName.endsWith(".pdf")
				|| docName.endsWith(".jpg")) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("documentDetails", documentDetails);
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/QueryDocumentView.zul", null, map);
		} else if (docName.endsWith(".zip")) {
			Filedownload.save(new AMedia(docName, "x-zip-compressed", "application/x-zip-compressed",
					documentDetails.getDocImage()));
		} else if (docName.endsWith(".7z")) {
			Filedownload.save(
					new AMedia(docName, "octet-stream", "application/octet-stream", documentDetails.getDocImage()));
		} else if (docName.endsWith(".rar")) {
			Filedownload.save(new AMedia(docName, "x-rar-compressed", "application/x-rar-compressed",
					documentDetails.getDocImage()));
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$qryCategory(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());

		Object dataObject = qryCategory.getObject();

		if (dataObject instanceof String) {
			this.qryCategory.setValue(dataObject.toString());
			this.qryCategory.setAttribute("Id", Long.MIN_VALUE);
		} else {
			QueryCategory details = (QueryCategory) dataObject;
			if (details != null) {
				this.qryCategory.setAttribute("Id", details.getId());
				this.qryCategory.setAttribute("Code", details.getCode());
			} else {
				this.qryCategory.setValue("");
				this.qryCategory.setAttribute("Id", Long.MIN_VALUE);
			}
		}

		logger.debug("Leaving" + event.toString());
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
		doShowNotes(this.queryDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		if (queryDetailListCtrl != null) {
			queryDetailListCtrl.search();
		} else if (finQueryDetailListCtrl != null) {
			finQueryDetailListCtrl.search();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.queryDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param queryDetail
	 * 
	 */
	public void doWriteBeanToComponents(QueryDetail aQueryDetail) {
		logger.debug(Literal.ENTERING);

		List<String> currentRole = new ArrayList<>();
		if (financeMain != null) {
			this.reference.setValue(financeMain.getFinReference());
			this.finReference.setValue(financeMain.getFinReference());
			currentRole.add(roleCode);
		} else if (sampling != null) {
			this.reference.setValue(sampling.getKeyReference());
			// TODO fin reference for sampling
			currentRole.add(roleCode);
		} else if (legalDetail != null) {
			this.reference.setValue(legalDetail.getLegalReference());
			this.finReference.setValue(legalDetail.getLoanReference());
			currentRole.add(roleCode);
		} else {
			this.reference.setValue(aQueryDetail.getReference());
			this.finReference.setValue(aQueryDetail.getFinReference());
		}
		this.qryNotes.setValue(aQueryDetail.getQryNotes());
		this.notifyTo.setValue(aQueryDetail.getNotifyTo());
		doFillNotifyDetails(aQueryDetail.getNotifyTo());
		this.raisedBy.setValue(aQueryDetail.getUsrLogin());
		this.raisedOn.setValue(aQueryDetail.getRaisedOn());
		this.responsNotes.setValue(aQueryDetail.getResponsNotes());
		this.closerNotes.setValue(StringUtils.trim(aQueryDetail.getCloserNotes()));
		if (aQueryDetail.isNewRecord()) {
			fillComboBox(this.status, "Open", queryModuleStatusList, "");
			if (financeMain != null || sampling != null || legalDetail != null) {
				fillComboBox(this.assignedRole, aQueryDetail.getAssignedRole(), roles, currentRole);
			} else {
				fillComboBox(this.assignedRole, aQueryDetail.getAssignedRole(), roles, "");
			}
		} else {
			fillComboBox(this.status, aQueryDetail.getStatus(), queryModuleStatusList, "");
			fillComboBox(assignedRole, aQueryDetail.getAssignedRole(), roles, "");
			if (roles.size() == PennantStaticListUtil.getQueryDetailExtRolesList().size()) {
				assignedRole.setValue(aQueryDetail.getAssignedRole());
			}
			this.qryCategory.setAttribute("mandateID", aQueryDetail.getCategoryCode());
			this.qryCategory.setValue(aQueryDetail.getCategoryCode());
			this.qryCategory.setDescription(aQueryDetail.getCategoryDescription());
			this.module.setValue(aQueryDetail.getModule());
		}
		if (aQueryDetail.getDocumentDetailsList() != null && aQueryDetail.getDocumentDetailsList().size() > 0) {
			doFillDocumentDetails(aQueryDetail.getDocumentDetailsList());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Prepare roles list from user workspace for assign roles list
	 * 
	 * @return
	 */
	private void setRoles() {
		// Add the extended roles.
		roles.addAll(PennantStaticListUtil.getQueryDetailExtRolesList());

		// Get the work-flow id.
		long workflowId = 0;

		if (financeMain != null) {
			workflowId = financeMain.getWorkflowId();
		} else if (sampling != null) {
			workflowId = sampling.getWorkflowId();
		} else if (legalDetail != null) {
			workflowId = legalDetail.getWorkflowId();
		}

		if (workflowId == 0) {
			return;
		}

		// Get the work-flow details.
		String workflowType = PennantApplicationUtil.getWorkFlowType(workflowId);
		WorkFlowDetails workflow = WorkFlowUtil.getDetailsByType(workflowType);

		if (workflow == null) {
			return;
		}

		ValueLabel valueLabel;
		String key;

		if (workflow.getRoles().length > 0) {
			for (Property role : PennantApplicationUtil.getRoles(workflow.getRoles())) {
				key = (String) role.getKey();

				valueLabel = new ValueLabel();
				valueLabel.setValue(key);
				valueLabel.setLabel(key.concat(" - ").concat(role.getValue()));

				roles.add(valueLabel);
			}
		}
		// Get the QueryloanORLegalWorkflow details.
		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES"))) {
			String loanORLegalWorkflowType = null;
			if (financeMain == null && legalDetail != null) {
				long loanWorkFLowID = getFinanceMainService().getLoanWorkFlowIdByFinRef(legalDetail.getLoanReference(),
						"_View");
				if (loanWorkFLowID == 0) {
					return;
				}
				loanORLegalWorkflowType = PennantApplicationUtil.getWorkFlowType(loanWorkFLowID);
			} else if (financeMain != null && legalDetail == null) {
				loanORLegalWorkflowType = "LEGAL_DETAILS";
			}

			WorkFlowDetails loanORLegalWorkflow = WorkFlowUtil.getDetailsByType(loanORLegalWorkflowType);
			ValueLabel loanValueLabel;
			String loanKey;
			for (Property role : PennantApplicationUtil.getRoles(loanORLegalWorkflow.getRoles())) {
				loanKey = (String) role.getKey();

				loanValueLabel = new ValueLabel();
				loanValueLabel.setValue(loanKey);
				loanValueLabel.setLabel(loanKey.concat(" - ").concat(role.getValue()));

				roles.add(loanValueLabel);
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnNotifyTo(Event event) {
		logger.debug("Entering  " + event.toString());
		this.notifyTo.setErrorMessage("");
		if (this.assignedRole.getSelectedItem().getValue() != null
				&& !this.assignedRole.getSelectedItem().getValue().toString().equals("#")) {
			Object dataObject = MultiSelectionSearchListBox.show(this.window_QueryDetailDialog, "SecurityUserEmails",
					this.notifyTo.getValue(),
					new Filter[] {
							new Filter("RoleCd", this.assignedRole.getSelectedItem().getValue(), Filter.OP_EQUAL),
							new Filter("UsrEmail", "", Filter.OP_NOT_EQUAL) });

			if (dataObject != null) {
				String details = (String) dataObject;
				this.notifyTo.setValue(details);
			}
		}
		logger.debug("Leaving  " + event.toString());

	}

	private void sendMail(QueryDetail queryDetail) {
		logger.debug("Entering  ");
		// Mail ID details preparation
		if (this.notifyTo.getValue() != null && !this.notifyTo.getValue().isEmpty()) {
			String str[] = this.notifyTo.getValue().split(",");
			Notification notification = new Notification();
			notification.setKeyReference(queryDetail.getFinReference());
			notification.setModule("LOAN");
			notification.setSubModule("QRY_MGMT");
			notification.setTemplateCode(SMTParameterConstants.QRY_MGMT_TEMPLATE);
			List<String> emails = Arrays.asList(str);
			notification.setEmails(emails);

			List<DocumentDetails> documents = queryDetail.getDocumentDetailsList();
			Map<String, byte[]> map = new HashMap<String, byte[]>();
			for (DocumentDetails documentDetail : documents) {
				map.put(documentDetail.getDocName(), documentDetail.getDocImage());
			}

			notification.setAttachments(map);

			notificationService.sendNotification(notification, queryDetail);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method Used for set list of values been class to components Product Deviation list
	 * 
	 * @param Product
	 */
	public void doFillNotifyDetails(String notifyTo) {
		logger.debug("Entering");

		if (notifyTo == null || notifyTo.isEmpty()) {
			return;
		}
		String tempDeviationCode = "";
		if (StringUtils.isEmpty(tempDeviationCode)) {
			tempDeviationCode = notifyTo;
		} else {
			// tempDeviationCode = notifyTo.split(",");
		}
		this.notifyTo.setValue(tempDeviationCode);

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aQueryDetail
	 */
	public void doWriteComponentsToBean(QueryDetail aQueryDetail) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Timestamp curntDateTime = new Timestamp(System.currentTimeMillis());

		try {
			aQueryDetail.setModule(this.module.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.financeMain != null) {
			try {
				aQueryDetail.setFinID(this.financeMain.getFinID());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// Finance Reference
		try {
			aQueryDetail.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Reference
		try {
			aQueryDetail.setReference(this.reference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Category Id
		try {
			this.qryCategory.getValidatedValue();
			Object obj = this.qryCategory.getAttribute("Id");
			if (obj != null) {
				aQueryDetail.setCategoryId(Long.valueOf(String.valueOf(obj)));
				aQueryDetail.setCategoryCode(String.valueOf(this.qryCategory.getAttribute("Code")));
				aQueryDetail.setCategoryDescription(qryCategory.getDescription());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Notes
		try {
			if (StringUtils.equals("Resubmit", this.status.getValue())) {
				aQueryDetail.setQryNotes(this.qryNotes.getValue() + '\n' + this.closerNotes.getValue());
			} else if (StringUtils.equals("Open", this.status.getValue()) && queryDetail.isNewRecord()) {
				aQueryDetail.setQryNotes(this.qryNotes.getValue());
			} else {
				aQueryDetail.setQryNotes(this.qryNotes.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Assigned Role
		try {
			if (StringUtils.equals("Resolve", this.status.getValue())
					|| StringUtils.equals(this.status.getValue(), "Close")
					|| StringUtils.equals(this.status.getValue(), "Resubmit")) {
				aQueryDetail.setAssignedRole(queryDetail.getAssignedRole());
			} else {
				aQueryDetail.setAssignedRole(assignedRole.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Notify To
		try {
			aQueryDetail.setNotifyTo(this.notifyTo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Status
		try {
			aQueryDetail.setStatus(this.status.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Raised By
		try {
			if (StringUtils.equals("Resolve", this.status.getValue())
					|| StringUtils.equals(this.status.getValue(), "Close")
					|| StringUtils.equals(this.status.getValue(), "Resubmit")) {
				aQueryDetail.setRaisedBy(queryDetail.getRaisedBy());
			} else {
				aQueryDetail.setRaisedBy(getUserWorkspace().getUserId());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Raised On
		try {
			aQueryDetail.setRaisedOn(curntDateTime);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Respons Notes
		try {
			if (StringUtils.equals("Resolve", this.status.getValue())
					&& StringUtils.equals("Resubmit", queryDetail.getStatus())) {
				aQueryDetail.setResponsNotes(this.responsNotesMnt.getValue() + '\n' + this.responsNotes.getValue());
			} else if (StringUtils.equals("Resubmit", this.status.getValue())) {
				aQueryDetail.setResponsNotes(this.responsNotes.getValue());
			} else if (StringUtils.equals("Close", this.status.getValue())) {
				aQueryDetail.setResponsNotes(this.responsNotes.getValue());
			} else {
				aQueryDetail.setResponsNotes(this.responsNotes.getValue());
			}
			// aQueryDetail.setResponsNotes(this.responsNotes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Response By
		try {
			if (StringUtils.equals(this.status.getValue(), "Close")
					|| StringUtils.equals(this.status.getValue(), "Resubmit")) {
				aQueryDetail.setResponseBy(queryDetail.getResponseBy());
			} else {
				aQueryDetail.setResponseBy(getUserWorkspace().getUserId());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Response On
		try {
			if (this.responseOn.getValue() != null) {
				aQueryDetail.setResponseOn(curntDateTime);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Closer Notes
		try {
			aQueryDetail.setCloserNotes(this.closerNotes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Closer By
		try {
			aQueryDetail.setCloserBy(getUserWorkspace().getUserId());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Closer On
		try {
			if (this.closerOn.getValue() != null) {
				aQueryDetail.setCloserOn(curntDateTime);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document Details
		aQueryDetail.setDocumentDetailsList(getDocumentDetails());

		aQueryDetail.setFinType(this.finReference.getDescription());
		// raisedUsrrole
		aQueryDetail.setRaisedUsrRole(this.roleCode);

		// Finance Type Setting is required as it is used in Interface.
		if (this.financeMain != null) {
			aQueryDetail.setFinType(this.financeMain.getFinType());
		} else if (getFinanceMainService() != null && reference != null
				&& StringUtils.isNotBlank(reference.getValue())) {
			aQueryDetail.setFinType(getFinanceMainService().getFinanceType(reference.getValue(), TableType.VIEW));
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			this.queryDetails.setSelected(true);
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
	 * @param queryDetail The entity that need to be render.
	 */
	public void doShowDialog(QueryDetail queryDetail) {
		logger.debug(Literal.ENTERING);

		String[] roleCodes = new String[1];
		roleCodes[0] = queryDetail.getAssignedRole();
		List<String> role = securityUserOperationsService.getUsersByRoles(roleCodes);

		setRoles();
		doWriteBeanToComponents(queryDetail);

		if (queryDetail != null && queryDetail.isNewRecord()) {
			this.qryNotes.setMaxlength(2000);
			this.responsNotes.setMaxlength(2000);
			this.closerNotes.setMaxlength(2000);
		} else {
			if (StringUtils.equals("Open", this.status.getValue())) {
				this.responsNotes.setMaxlength(2000);
			} else if (StringUtils.equals("Resolve", this.status.getValue())) {
				this.closerNotes.setMaxlength(2000);
			} else if (StringUtils.equals("Resubmit", this.status.getValue())) {
				this.responsNotes.setMaxlength(2000);
			} else if (StringUtils.equals("Close", this.status.getValue())) {
				this.closerNotes.setMaxlength(2000);
			}
		}

		if (queryDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.qryNotes.focus();
		} else {
			this.qryNotes.focus();
			btnCancel.setVisible(false);
			// Assigned role and record role is same when record going to
			// editable
			if ((financeMain != null && StringUtils.equals(queryDetail.getAssignedRole(), financeMain.getRoleCode()))
					|| (queryDetail != null && role.contains(getUserWorkspace().getUserDetails().getUsername()))
					|| (StringUtils.equals(String.valueOf(getUserWorkspace().getUserId()),
							String.valueOf(queryDetail.getRaisedBy())))) {
				List<String> list = new ArrayList<>();
				Date appDate = SysParamUtil.getAppDate();
				if ((queryDetail.getStatus().equals("Open")
						&& !StringUtils.equals(String.valueOf(getUserWorkspace().getUserId()),
								String.valueOf(queryDetail.getRaisedBy())))
						|| (queryDetail.getStatus().equals("Resubmit")
								&& !StringUtils.equals(String.valueOf(getUserWorkspace().getUserId()),
										String.valueOf(queryDetail.getCloserBy())))) {
					doReadOnly();
					readOnlyComponent(false, this.responsNotes);
					this.row5.setVisible(true);
					this.row6.setVisible(false);
					this.responseBy.setValue(getUserWorkspace().getUserDetails().getUsername());
					this.responseOn.setValue(appDate);
					readOnlyComponent(false, this.docRemarks);
					readOnlyComponent(false, this.btnUploadDoc);
					readOnlyComponent(false, this.btnUploadDocs);
					readOnlyComponent(false, this.custDocType);

					if (StringUtils.equals("Resubmit", queryDetail.getStatus())) {
						fillComboBox(this.status, "Resolve", queryModuleStatusList, "");
						this.responsNotes.setValue("");
						this.responsNotesMnt.setVisible(true);
						this.responsNotesMnt.setValue(queryDetail.getResponsNotes());

						this.qryNotes.setValue(queryDetail.getQryNotes());
						this.label_CloserNotes.setValue(Labels.getLabel("label_QueryDetailDialog_ResubmitNotes.value"));
						this.row6.setVisible(false);
					} else {
						fillComboBox(this.status, "Resolve", queryModuleStatusList, "");
					}

				} else if (queryDetail.getStatus().equals("Resolve") && StringUtils.equals(
						String.valueOf(getUserWorkspace().getUserId()), String.valueOf(queryDetail.getRaisedBy()))) {
					this.row5.setVisible(true);
					this.row6.setVisible(true);
					this.row7.setVisible(true);
					readOnlyComponent(false, this.closerNotes);
					this.responseBy.setValue(queryDetail.getResponseUser());
					this.responseOn.setValue(queryDetail.getResponseOn());
					this.closerNotes.setValue("");
					this.closerBy.setValue(getUserWorkspace().getUserDetails().getUsername());
					this.closerOn.setValue(appDate);
					readOnlyComponent(false, this.status);

					readOnlyComponent(false, this.docRemarks);
					readOnlyComponent(false, this.btnUploadDoc);
					readOnlyComponent(false, this.btnUploadDocs);
					readOnlyComponent(false, this.custDocType);

					if (StringUtils.equals("Resubmit", queryDetail.getStatus())) {
						readOnlyComponent(false, this.status);
						list.add(Labels.getLabel("label_QueryDetailDialog_Opened"));
						list.add(Labels.getLabel("label_QueryDetailDialog_Closed"));
						fillComboBox(this.status, "Close", queryModuleStatusList, list);
						this.responsNotesMnt.setVisible(true);
						this.responsNotesMnt.setValue(queryDetail.getCloserNotes());
					} else {

						list.add(Labels.getLabel("label_QueryDetailDialog_Opened"));
						list.add(Labels.getLabel("label_QueryDetailDialog_Resolved"));
						fillComboBox(this.status, "Close", queryModuleStatusList, list);
						this.status.setValue("Close");
					}

				} else if (queryDetail.getStatus().equals("Close")) {
					this.row5.setVisible(true);
					this.row6.setVisible(true);
					this.row7.setVisible(true);
					this.row8.setVisible(true);
					this.btnSave.setVisible(false);
					doReadOnly();
					this.responseBy.setValue(queryDetail.getResponseUser());
					this.responseOn.setValue(queryDetail.getResponseOn());
					this.closerBy.setValue(queryDetail.getCloserUser());
					this.closerOn.setValue(queryDetail.getCloserOn());
				} else {
					if (queryDetail.getStatus().equals("Open")) {
						this.row5.setVisible(false);
					} else if (queryDetail.getStatus().equals("Resolve")) {
						this.row5.setVisible(true);
						this.row6.setVisible(true);
					} else if (queryDetail.getStatus().equals("Resubmit")) {
						this.row5.setVisible(true);
						this.row6.setVisible(true);
						this.row7.setVisible(true);
						this.row8.setVisible(true);
						this.label_CloserNotes.setValue(Labels.getLabel("label_QueryDetailDialog_ResubmitNotes.value"));
						this.label_CloserBy.setValue(Labels.getLabel("label_QueryDetailDialog_ResubmitBy.value"));
						this.label_CloserOn.setValue(Labels.getLabel("label_QueryDetailDialog_ResubmitOn.value"));
					} else {
						this.row5.setVisible(true);
						this.row6.setVisible(true);
						this.row7.setVisible(true);
						this.row8.setVisible(true);
					}
					doReadOnly();
					this.btnSave.setVisible(false);
					this.closerBy.setValue(queryDetail.getCloserUser());
					this.closerOn.setValue(queryDetail.getCloserOn());
					this.responseBy.setValue(queryDetail.getResponseUser());
					this.responseOn.setValue(queryDetail.getResponseOn());
				}
			} else {
				if (queryDetail.getStatus().equals("Close")) {
					this.row5.setVisible(true);
					this.row6.setVisible(true);
					this.row7.setVisible(true);
					this.row8.setVisible(true);
					this.btnSave.setVisible(false);
					doReadOnly();
					this.responseBy.setValue(queryDetail.getResponseUser());
					this.responseOn.setValue(queryDetail.getResponseOn());
					this.closerBy.setValue(queryDetail.getCloserUser());
					this.closerOn.setValue(queryDetail.getCloserOn());
				} else {
					this.row5.setVisible(false);
					this.row6.setVisible(false);
					this.row7.setVisible(false);
					this.row8.setVisible(false);
					this.btnSave.setVisible(false);
					doReadOnly();
					this.responseBy.setValue(queryDetail.getResponseUser());
					this.responseOn.setValue(queryDetail.getResponseOn());
					this.closerBy.setValue(queryDetail.getCloserUser());
					this.closerOn.setValue(queryDetail.getCloserOn());
				}
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			doReadOnly();
		}

		if (queryDetail.isNewRecord()) {
			this.btnSave.setLabel("Send Query");
		}

		if (isEnquiry()) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			doReadOnly();
		}
		this.btnNotes.setVisible(false);
		if (financeMain != null && financeMain.getWorkflowId() > 0) {
			this.window_QueryDetailDialog.doModal();
		} else {
			setDialog(DialogType.MODAL);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onSelect$status(Event event) {
		logger.debug("Entering" + event.toString());
		if (StringUtils.equals("Resubmit", this.status.getValue())) {
			this.label_CloserNotes.setValue(Labels.getLabel("label_QueryDetailDialog_ResubmitNotes.value"));
		} else {
			this.label_CloserNotes.setValue(Labels.getLabel("label_QueryDetailDialog_CloserNotes.value"));
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint(
				new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_FinReference.value"), null, true));
		if (!this.qryCategory.isReadonly()) {
			this.qryCategory
					.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_CategoryId.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.custDocType.isReadonly()) {
			this.custDocType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_CustDocType.value"), null, true));
		}
		if (!this.qryNotes.isReadonly()) {
			this.qryNotes.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_QryNotes.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.assignedRole.isDisabled()) {
			this.assignedRole.setConstraint(
					new StaticListValidator(roles, Labels.getLabel("label_QueryDetailDialog_AssignedRole.value")));
		}
		if (!this.notifyTo.isReadonly()) {
			this.notifyTo.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_NotifyTo.value"),
					PennantRegularExpressions.REGEX_NAME, false));
		}
		if (!this.status.isReadonly()) {
			this.status.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_Status.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.raisedBy.isReadonly()) {
			this.raisedBy.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_QueryDetailDialog_RaisedBy.value"), true, false, 0));
		}
		if (!this.raisedOn.isReadonly()) {
			this.raisedOn.setConstraint(
					new PTDateValidator(Labels.getLabel("label_QueryDetailDialog_RaisedOn.value"), false));
		}
		if (this.row5.isVisible() && !this.responsNotes.isReadonly()) {
			this.responsNotes
					.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_ResponsNotes.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.responseBy.isReadonly()) {
			this.responseBy.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_QueryDetailDialog_ResponseBy.value"), false, false, 0));
		}
		if (!this.responseOn.isReadonly()) {
			this.responseOn.setConstraint(
					new PTDateValidator(Labels.getLabel("label_QueryDetailDialog_ResponseOn.value"), false));
		}
		if (this.row7.isVisible() && !this.closerNotes.isReadonly()) {
			this.closerNotes
					.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_CloserNotes.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.closerBy.isReadonly()) {
			this.closerBy.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_QueryDetailDialog_CloserBy.value"), false, false, 0));
		}
		if (!this.closerOn.isReadonly()) {
			this.closerOn.setConstraint(
					new PTDateValidator(Labels.getLabel("label_QueryDetailDialog_CloserOn.value"), false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setConstraint("");
		this.qryCategory.setConstraint("");
		this.qryNotes.setConstraint("");
		this.assignedRole.setConstraint("");
		this.notifyTo.setConstraint("");
		this.status.setConstraint("");
		this.raisedBy.setConstraint("");
		this.raisedOn.setConstraint("");
		this.responsNotes.setConstraint("");
		this.responseBy.setConstraint("");
		this.responseOn.setConstraint("");
		this.closerNotes.setConstraint("");
		this.closerBy.setConstraint("");
		this.closerOn.setConstraint("");

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

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final QueryDetail aQueryDetail = new QueryDetail();
		BeanUtils.copyProperties(this.queryDetail, aQueryDetail);

		doDelete(String.valueOf(aQueryDetail.getId()), aQueryDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.queryDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(true, this.status);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(isReadOnly("QueryDetailDialog_Status"), this.status);
		}

		readOnlyComponent(false, this.qryCategory);
		readOnlyComponent(false, this.qryNotes);
		readOnlyComponent(false, this.assignedRole);
		readOnlyComponent(false, this.raisedBy);
		readOnlyComponent(false, this.raisedOn);
		readOnlyComponent(false, this.responsNotes);
		readOnlyComponent(false, this.responseBy);
		readOnlyComponent(false, this.responseOn);
		readOnlyComponent(false, this.closerNotes);
		readOnlyComponent(false, this.closerBy);
		readOnlyComponent(false, this.closerOn);
		readOnlyComponent(false, this.custDocType);
		readOnlyComponent(false, this.docRemarks);
		readOnlyComponent(false, this.btnUploadDoc);
		readOnlyComponent(false, this.btnUploadDocs);
		readOnlyComponent(false, this.btnNotifyTo);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.qryCategory);
		readOnlyComponent(true, this.qryNotes);
		readOnlyComponent(true, this.assignedRole);
		readOnlyComponent(true, this.notifyTo);
		readOnlyComponent(true, this.status);
		readOnlyComponent(true, this.raisedBy);
		readOnlyComponent(true, this.raisedOn);
		readOnlyComponent(true, this.responsNotes);
		readOnlyComponent(true, this.responseBy);
		readOnlyComponent(true, this.responseOn);
		readOnlyComponent(true, this.closerNotes);
		readOnlyComponent(true, this.closerBy);
		readOnlyComponent(true, this.closerOn);
		readOnlyComponent(true, this.custDocType);
		readOnlyComponent(true, this.docRemarks);
		readOnlyComponent(true, this.btnUploadDoc);
		readOnlyComponent(true, this.btnUploadDocs);
		readOnlyComponent(true, this.delete);
		readOnlyComponent(true, this.btnNotifyTo);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.finReference.setValue("");
		this.qryCategory.setValue("");
		this.qryCategory.setDescription("");
		this.qryNotes.setValue("");
		this.assignedRole.setValue("");
		this.notifyTo.setValue("");
		this.status.setValue("");
		this.raisedBy.setText("");
		this.raisedOn.setText("");
		this.responsNotes.setValue("");
		this.responseBy.setText("");
		this.responseOn.setText("");
		this.closerNotes.setValue("");
		this.closerBy.setText("");
		this.closerOn.setText("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final QueryDetail aQueryDetail = new QueryDetail();
		BeanUtils.copyProperties(this.queryDetail, aQueryDetail);

		doSetValidation();
		doWriteComponentsToBean(aQueryDetail);

		String tranType = "";
		aQueryDetail.setVersion(aQueryDetail.getVersion() + 1);

		try {
			if (doProcess(aQueryDetail, tranType)) {
				// Send mail when query raised.
				if (StringUtils.equals(aQueryDetail.getStatus(), "Open")) {
					sendMail(aQueryDetail);
				}
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	protected boolean doProcess(QueryDetail aQueryDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;

		aQueryDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aQueryDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aQueryDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		auditHeader = getAuditHeader(aQueryDetail, tranType);
		processCompleted = doSaveProcess(auditHeader, null);

		logger.debug("Leaving");
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
		QueryDetail aQueryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = queryDetailService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = queryDetailService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = queryDetailService.doApprove(auditHeader);

					if (aQueryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = queryDetailService.doReject(auditHeader);
					if (aQueryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_QueryDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_QueryDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.queryDetail), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(QueryDetail aQueryDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aQueryDetail.getBefImage(), aQueryDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aQueryDetail.getUserDetails(),
				getOverideMap());
	}

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}

	public FinQueryDetailListCtrl getFinQueryDetailListCtrl() {
		return finQueryDetailListCtrl;
	}

	public void setFinQueryDetailListCtrl(FinQueryDetailListCtrl finQueryDetailListCtrl) {
		this.finQueryDetailListCtrl = finQueryDetailListCtrl;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public MailTemplateService getMailTemplateService() {
		return mailTemplateService;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public Configuration getFreemarkerMailConfiguration() {
		return freemarkerMailConfiguration;
	}

	public void setFreemarkerMailConfiguration(Configuration freemarkerMailConfiguration) {
		this.freemarkerMailConfiguration = freemarkerMailConfiguration;
	}

	public QueryDetailListCtrl getQueryDetailListCtrl() {
		return queryDetailListCtrl;
	}

	public void setQueryDetailListCtrl(QueryDetailListCtrl queryDetailListCtrl) {
		this.queryDetailListCtrl = queryDetailListCtrl;
	}

	public SecurityUserOperationsService getSecurityUserOperationsService() {
		return securityUserOperationsService;
	}

	public void setSecurityUserOperationsService(SecurityUserOperationsService securityUserOperationsService) {
		this.securityUserOperationsService = securityUserOperationsService;
	}

	public LegalDetail getLegalDetail() {
		return legalDetail;
	}

	public void setLegalDetail(LegalDetail legalDetail) {
		this.legalDetail = legalDetail;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

}
