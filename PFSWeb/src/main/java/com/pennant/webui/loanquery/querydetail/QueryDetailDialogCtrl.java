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
 * FileName    		:  QueryDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.loanquery.querydetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.zkoss.zul.Checkbox;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.MailTemplateData;
import com.pennant.backend.service.administration.SecurityUserOperationsService;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.configuration.EmailBodyType;
import com.pennanttech.pennapps.notification.email.configuration.RecipientType;
import com.pennanttech.pennapps.notification.email.model.MessageAddress;
import com.pennanttech.pennapps.notification.email.model.MessageAttachment;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.web.util.MessageUtil;

import freemarker.template.Configuration;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LoanQuery/QueryDetail/queryDetailDialog.zul file. <br>
 */
public class QueryDetailDialogCtrl extends GFCBaseCtrl<QueryDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(QueryDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
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
	protected Paging pagingQueryDetailDialog;
	protected Checkbox checkbox;
	protected Button delete;
	protected Button multy_Delete;
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
	private ArrayList<ValueLabel> assignedRolesList = new ArrayList<>();
	private List<DocumentDetails> documentDetails = new ArrayList<>();
	private Map<String, DocumentDetails> map = new HashMap<>();

	private QueryDetail queryDetail; // overhanded per param
	private FinanceMain financeMain = null;

	private transient QueryDetailListCtrl queryDetailListCtrl; // overhanded
	private transient FinQueryDetailListCtrl finQueryDetailListCtrl; // overhanded
	// per
	// param
	private transient QueryDetailService queryDetailService;
	private MailTemplateService mailTemplateService;
	private DocumentManagerDAO documentManagerDAO;
	private MailUtil mailUtil;
	private Configuration freemarkerMailConfiguration;
	private SecurityUserOperationsService securityUserOperationsService;
	List<String> emailList = null;
	private String roleCode;
	private Sampling sampling = null;
	private LegalDetail legalDetail = null;

	@Autowired
	private EmailEngine emailEngine;

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
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.queryDetail.getId()));
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
	public void onCreate$window_QueryDetailDialog(Event event) throws Exception {
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
			this.finReference.setTextBoxWidth(280);
			this.finReference.setModuleName("QryFinanceMain");
			this.finReference.setValueColumn("FinReference");
			this.finReference.setDescColumn("FinType");
			this.finReference.setValidateColumns(new String[] { "FinReference" });
		}

		this.qryCategory.setMandatoryStyle(true);
		this.qryCategory.setTextBoxWidth(280);
		this.qryCategory.setModuleName("QueryCategory");
		this.qryCategory.setValueColumn("Code");
		this.qryCategory.setDescColumn("Description");
		this.qryCategory.setValidateColumns(new String[] { "Code" });
		this.custDocType.setProperties("CustDocumentType", "DocTypeCode", "DocTypeDesc", false, 25);
		this.custDocType.setValidateColumns(new String[] { "DocTypeCode" });
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
		
		if(isEnquiry()){
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
			} else {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
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
				documentDetail.setDocReceivedDate(DateUtility.getAppDate());
				documentDetail.setVersion(1);
				documentDetail.setLastMntBy(getUserWorkspace().getUserId());
				textbox.setAttribute("data", documentDetail);
			} else {
				DocumentDetails documentDetail = (DocumentDetails) textbox.getAttribute("data");
				documentDetail.setDocImage(docData);
				documentDetail.setDocModule(FinanceConstants.QUERY_MANAGEMENT);
				textbox.setAttribute("data", documentDetail);
			}
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the upload
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
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
					lc = new Listcell();
					checkbox = new Checkbox();
					checkbox.setAttribute("documentDetail", documentDetail);
					checkbox.addForward("onClick", self, "onClick_CheckBox");
					if (!documentDetail.isNew()) {
						checkbox.setDisabled(true);
					}
					checkbox.setParent(lc);
					lc.setParent(item);
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
					if (!documentDetail.isNew()) {
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
		if (detail != null && !detail.isNew()) {
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
		if (documentDetails != null && documentDetails.getDocImage() == null
				&& documentDetails.getDocRefId() != Long.MIN_VALUE) {
			DocumentManager docManager = documentManagerDAO.getById(documentDetails.getDocRefId());
			if (docManager != null) {
				documentDetails.setDocImage(docManager.getDocImage());
			}
		}

		if (documentDetails.getDocName().endsWith(".doc") || documentDetails.getDocName().endsWith(".docx")) {
			Filedownload.save(new AMedia(documentDetails.getDocName(), "msword", "application/msword",
					documentDetails.getDocImage()));
		} else if (documentDetails.getDocName().endsWith(".xls") || documentDetails.getDocName().endsWith(".xlsx")) {
			Filedownload.save(new AMedia(documentDetails.getDocName(), "xls", "application/vnd.ms-excel",
					documentDetails.getDocImage()));
		} else if (documentDetails.getDocName().endsWith(".png") || documentDetails.getDocName().endsWith(".jpeg")
				|| documentDetails.getDocName().endsWith(".pdf") || documentDetails.getDocName().endsWith(".jpg")) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("documentDetails", documentDetails);
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/QueryDocumentView.zul", null, map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Filling the QueryModuleDocumentMap details based on checked and unchecked
	 * events of onClick_CheckBox.
	 */
	public void onClick_CheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		DocumentDetails documentDetails = (DocumentDetails) checkBox.getAttribute("documentDetail");

		if (checkBox.isChecked()) {
			map.put(documentDetails.getDocName(), documentDetails);
		} else {
			map.remove(documentDetails.getDocName());
		}

		if (map.size() == this.pagingQueryDetailDialog.getTotalSize()) {
			checkbox.setChecked(true);
		} else {
			checkbox.setChecked(false);
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
			} else {
				this.qryCategory.setValue("");
				this.qryCategory.setAttribute("Id", Long.MIN_VALUE);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finReference(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());

		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setAttribute("FinReference", "");
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setAttribute("FinReference", details.getFinReference());
				setRolesList(details.getWorkflowId());
				fillComboBox(this.assignedRole, "", assignedRolesList, "");
			} else {
				this.finReference.setValue("");
				this.finReference.setAttribute("FinReference", "");
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
		doShowNotes(this.queryDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
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
			//TODO fin reference for sampling
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
				fillComboBox(this.assignedRole, aQueryDetail.getAssignedRole(), assignedRolesList, currentRole);
			} else {
				fillComboBox(this.assignedRole, aQueryDetail.getAssignedRole(), assignedRolesList, "");
			}
		} else {
			fillComboBox(this.status, aQueryDetail.getStatus(), queryModuleStatusList, "");
			this.assignedRole.setValue(aQueryDetail.getAssignedRole());
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
	private void setValueLabelList() {
		String workFlowTpe = null;
		if (financeMain != null && financeMain.getWorkflowId() > 0) { // In Loan
			workFlowTpe = PennantApplicationUtil.getWorkFlowType(financeMain.getWorkflowId());
		} else if (sampling != null && sampling.getWorkflowId() > 0) {
			workFlowTpe = PennantApplicationUtil.getWorkFlowType(sampling.getWorkflowId());
		} else if (legalDetail != null && legalDetail.getWorkflowId() > 0) {
			workFlowTpe = PennantApplicationUtil.getWorkFlowType(legalDetail.getWorkflowId());
		}
		WorkFlowDetails workflow = null;
		workflow = WorkFlowUtil.getDetailsByType(workFlowTpe);
		ValueLabel valueLabel;
		if (workflow != null && workflow.getFlowRoles().length >= 0) {
			for (int j = 0; j < workflow.getFlowRoles().length; j++) {
				String[] s2 = workflow.getFlowRoles()[j].split("\\;");
				for (int i = 0; i < s2.length; i++) {
					valueLabel = new ValueLabel();
					valueLabel.setLabel(s2[i]);
					valueLabel.setValue(s2[i]);
					assignedRolesList.add(valueLabel);
				}
			}
		}
	}

	private void setRolesList(Long workFlowId) {
		if (workFlowId != null && workFlowId > 0) { // Master menu for default
			// workflow as Maker and
			// Checker(1)
			String workFlowType = PennantApplicationUtil.getWorkFlowType(workFlowId);
			WorkFlowDetails workflow = null;
			workflow = WorkFlowUtil.getDetailsByType(workFlowType);
			ValueLabel valueLabel;
			if (workflow != null && workflow.getFlowRoles().length >= 0) {
				for (int j = 0; j < workflow.getFlowRoles().length; j++) {
					String[] s2 = workflow.getFlowRoles()[j].split("\\;");
					for (int i = 0; i < s2.length; i++) {
						valueLabel = new ValueLabel();
						valueLabel.setLabel(s2[i]);
						valueLabel.setValue(s2[i]);
						assignedRolesList.add(valueLabel);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotifyTo(Event event) throws Exception {
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
			try {
				templatePrep(queryDetail, str);
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
				getMailUtil().sendNotification(notification, queryDetail);

			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving  ");
	}

	@SuppressWarnings("unused")
	private void templatePrep(QueryDetail queryDetail, String[] mailId) {
		List<String> emailList = null;
		MailTemplate mailTemplate = null;
		MailTemplateData templateData = new MailTemplateData();
		templateData.setFinReference(queryDetail.getFinReference());
		templateData.setCustShrtName(getUserWorkspace().getUserDetails().getUsername());
		String templateID = SysParamUtil.getValueAsString(SMTParameterConstants.QRY_MGMT_TEMPLATE);
		mailTemplate = getMailTemplateService().getApprovedMailTemplateById(Long.valueOf(templateID));
		if (mailTemplate != null && mailTemplate.isActive()) {
			mailTemplate.setLovDescMailId(mailId);
			try {
				mailUtil.parseMail(mailTemplate, templateData);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (queryDetail.getDocumentDetailsList() != null) {
				Map<String,byte[]> attchments= new HashMap<>();
				for (DocumentDetails documentDetails : queryDetail.getDocumentDetailsList()) {
					attchments.put(documentDetails.getDocName(), documentDetails.getDocImage());
				}
				mailTemplate.setAttchments(attchments);
			}

			if (mailTemplate.isEmailTemplate()
					&& StringUtils.isNotEmpty(StringUtils.join(mailTemplate.getLovDescMailId(), ","))) {
				try {
					Notification emailMessage = new Notification();
					emailMessage.setKeyReference("");
					emailMessage.setModule("QRY_MGMT");
					emailMessage.setSubModule("QRY_MGMT");
					emailMessage.setStage("");
					emailMessage.setSubject(mailTemplate.getEmailSubject());
					emailMessage.setContent(mailTemplate.getEmailContent());
					emailMessage.setContentType(EmailBodyType.HTML.getKey());

					for (String email : mailTemplate.getLovDescMailId()) {
						MessageAddress address = new MessageAddress();
						address.setEmailId(email);
						address.setRecipientType(RecipientType.TO.getKey());
						emailMessage.getAddressesList().add(address);
					}

					Map<String, byte[]> attachments = mailTemplate.getAttchments();
					if (MapUtils.isNotEmpty(attachments)) {
						for (Entry<String, byte[]> document : attachments.entrySet()) {
							MessageAttachment attachment = new MessageAttachment();
							attachment.setAttachment(document.getValue());
							attachment.setFileName(document.getKey());
							emailMessage.getAttachmentList().add(attachment);
						}
					}

					emailEngine.sendEmail(emailMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method Used for set list of values been class to components Product
	 * Deviation list
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

		logger.debug("Entering");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aQueryDetail
	 */
	public void doWriteComponentsToBean(QueryDetail aQueryDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String usrnameDate = "--" + getUserWorkspace().getUserDetails().getUsername() + "," + DateUtility.getAppDate();

		try {
			aQueryDetail.setModule(this.module.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Finance Reference
		try {
			aQueryDetail.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//  Reference
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
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Notes
		try {
			if (StringUtils.equals("Resubmit", this.status.getValue())) {
				aQueryDetail.setQryNotes(this.qryNotes.getValue() + '\n' + this.closerNotes.getValue() + usrnameDate);
			} else if (StringUtils.equals("Open", this.status.getValue()) && queryDetail.isNew()) {
				aQueryDetail.setQryNotes(this.qryNotes.getValue() + usrnameDate);
			} else {
				aQueryDetail.setQryNotes(this.qryNotes.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Assigned Role
		try {
			aQueryDetail.setAssignedRole(this.assignedRole.getValue());
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
			aQueryDetail.setRaisedBy(getUserWorkspace().getUserId());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Raised On
		try {
			aQueryDetail.setRaisedOn(new Timestamp(System.currentTimeMillis()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Respons Notes
		try {
			if (StringUtils.equals("Resolve", this.status.getValue())
					&& StringUtils.equals("Resubmit", queryDetail.getStatus())) {
				aQueryDetail.setResponsNotes(
						this.responsNotesMnt.getValue() + '\n' + this.responsNotes.getValue() + usrnameDate);
			} else if (StringUtils.equals("Resubmit", this.status.getValue())) {
				aQueryDetail.setResponsNotes(this.responsNotes.getValue());
			} else if (StringUtils.equals("Close", this.status.getValue())) {
				aQueryDetail.setResponsNotes(this.responsNotes.getValue());
			} else {
				aQueryDetail.setResponsNotes(this.responsNotes.getValue() + usrnameDate);
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
				aQueryDetail.setResponseOn(new Timestamp(System.currentTimeMillis()));
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
				aQueryDetail.setCloserOn(new Timestamp(System.currentTimeMillis()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Assigned Role
		try {
			aQueryDetail.setAssignedRole(this.assignedRole.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document Details
		aQueryDetail.setDocumentDetailsList(getDocumentDetails());

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
	 * @param queryDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(QueryDetail queryDetail) {
		logger.debug(Literal.LEAVING);

		String[] roleCodes = new String[1];
		roleCodes[0] = queryDetail.getAssignedRole();
		List<String> role = securityUserOperationsService.getUsersByRoles(roleCodes);

		setValueLabelList();
		doWriteBeanToComponents(queryDetail);

		if (queryDetail != null && queryDetail.isNew()) {
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

		if (queryDetail.isNew()) {
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
					this.responseOn.setValue(DateUtility.getAppDate());
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
					this.closerOn.setValue(DateUtility.getAppDate());
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
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			doReadOnly();
		}

		if (queryDetail.isNew()) {
			this.btnSave.setLabel("Send Query");
		}

		if (isEnquiry()) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			doReadOnly();
		}
		this.btnNotes.setVisible(false);
		if (financeMain != null && financeMain.getWorkflowId() > 0) {
			this.window_QueryDetailDialog.setHeight("70%");
			this.window_QueryDetailDialog.setWidth("85%");
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
		logger.debug(Literal.LEAVING);

		if (!this.finReference.isReadonly() || this.finReference.isReadonly()) {
			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_QueryDetailDialog_FinReference.value"), null, true));
		}
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
			this.assignedRole.setConstraint(new StaticListValidator(assignedRolesList,
					Labels.getLabel("label_QueryDetailDialog_AssignedRole.value")));
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
		logger.debug(Literal.LEAVING);

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

	/**
	 * Deletes a QueryDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final QueryDetail aQueryDetail = new QueryDetail();
		BeanUtils.copyProperties(this.queryDetail, aQueryDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aQueryDetail.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aQueryDetail.getRecordType()).equals("")) {
				aQueryDetail.setVersion(aQueryDetail.getVersion() + 1);
				aQueryDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aQueryDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aQueryDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aQueryDetail.getNextTaskId(),
							aQueryDetail);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aQueryDetail, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.queryDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(true, this.status);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(isReadOnly("QueryDetailDialog_Status"), this.status);
		}

		/*
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"),
		 * this.qryCategory);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_QryNotes"),
		 * this.qryNotes);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_AssignedRole"),
		 * this.assignedRole);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_RaisedBy"),
		 * this.raisedBy);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_RaisedOn"),
		 * this.raisedOn);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_ResponsNotes"),
		 * this.responsNotes);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_ResponseBy"),
		 * this.responseBy);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_ResponseOn"),
		 * this.responseOn);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CloserNotes"),
		 * this.closerNotes);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CloserBy"),
		 * this.closerBy);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CloserOn"),
		 * this.closerOn);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"),
		 * this.custDocType);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"),
		 * this.docRemarks);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"),
		 * this.btnUploadDoc);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"),
		 * this.btnUploadDocs);
		 * readOnlyComponent(isReadOnly("QueryDetailDialog_CategoryId"),
		 * this.btnNotifyTo);
		 */
		if (this.financeMain == null && this.sampling == null) {
			readOnlyComponent(false, this.finReference);
		}
		
		if (PennantConstants.QUERY_LEGAL_VERIFICATION.equals(this.module.getValue())) {
			readOnlyComponent(true, this.finReference);
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
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.finReference);
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
		// readOnlyComponent(true, this.checkbox);
		readOnlyComponent(true, this.delete);
		this.multy_Delete.setVisible(false);
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(QueryDetail aQueryDetail, String tranType) {
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
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		QueryDetail aQueryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
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

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
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

}
