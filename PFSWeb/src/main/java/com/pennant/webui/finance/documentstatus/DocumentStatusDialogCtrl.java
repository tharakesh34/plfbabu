package com.pennant.webui.finance.documentstatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.documents.model.DocumentStatus;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;
import com.pennanttech.pff.documents.service.DocumentService;

public class DocumentStatusDialogCtrl extends GFCBaseCtrl<DocumentStatusDetail> {
	private static final long serialVersionUID = 2416996963170312381L;
	private static final Logger logger = LogManager.getLogger(DocumentStatusDialogCtrl.class);

	protected Window window_documentStatusDialog;
	protected Borderlayout borderlayout_DocumentStatusDialog;

	protected Button btnSave;
	protected Button btnClose;

	protected Label finBasic_finType;
	protected Label finBasic_Reference;
	protected Listbox docmentslistBox;

	protected Textbox finReference_header;
	protected Textbox finStatus_header;
	protected Textbox finType_header;
	protected Textbox finCcy_header;
	protected Textbox scheduleMethod_header;
	protected Textbox profitDaysBasis_header;
	protected Textbox finBranch_header;
	protected Textbox custCIF_header;
	protected Label custShrtName;

	private DocumentStatusListCtrl documentStatusListCtrl;
	private DocumentService documentService;
	private DocumentDetailsDAO documentDetailsDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private DMSService dMSService;

	private FinanceMain fm;
	private DocumentStatus dsh;
	private List<DocumentStatusDetail> list;
	private List<DocumentStatusDetail> documentStatus = new ArrayList<>();

	public DocumentStatusDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DocumentStatusDialog";
	}

	public void onCreate$window_documentStatusDialog(Event event) {
		setPageComponents(window_documentStatusDialog);

		try {
			this.fm = (FinanceMain) arguments.get("financeMain");
			this.dsh = documentService.getDocumentStatus(fm.getFinReference());
			this.dsh.setFinReference(this.fm.getFinReference());
			this.dsh.setFinType(this.fm.getFinType());
			this.dsh.setFinBranch(this.fm.getFinBranch());
			this.dsh.setFinStartDate(this.fm.getFinStartDate());
			this.dsh.setMaturityDate(this.fm.getMaturityDate());
			this.dsh.setFinCcy(this.fm.getFinCcy());
			this.dsh.setScheduleMethod(this.fm.getScheduleMethod());
			this.dsh.setProfitDaysBasis(this.fm.getProfitDaysBasis());
			this.dsh.setCustCIF(this.fm.getLovDescCustCIF());
			this.dsh.setCustShrtName(this.fm.getLovDescCustShrtName());
			this.dsh.setFinCategory(this.fm.getFinCategory());
			this.dsh.setBranchDesc(this.fm.getLovDescFinBranchName());
			this.dsh.setFinTypeDesc(this.fm.getLovDescFinTypeName());
			this.dsh.setWorkflowId(this.fm.getWorkflowId());

			this.documentStatusListCtrl = (DocumentStatusListCtrl) arguments.get("documentStatusListCtrl");

			// Render the page and display the data.
			doLoadWorkFlow(this.fm.isWorkflow(), this.fm.getWorkflowId(), this.dsh.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
				if (StringUtils.isNotBlank(this.dsh.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			this.list = dsh.getDsList();

			doShowDialog();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void doShowDialog() {
		doWriteBeanToComponents();
		doEdit();
		setDialog(DialogType.EMBEDDED);
	}

	public void doWriteBeanToComponents() {
		appendDocuments(list);

		this.finReference_header.setValue(fm.getFinReference());

		if (fm.isFinIsActive()) {
			this.finStatus_header.setValue("Active");
		} else {
			this.finStatus_header.setValue("Matured");
		}

		this.finType_header.setValue(fm.getFinType() + "-" + fm.getLovDescFinTypeName());
		this.finCcy_header.setValue(fm.getFinCcy());
		this.scheduleMethod_header.setValue(fm.getScheduleMethod());
		this.profitDaysBasis_header.setValue(fm.getProfitDaysBasis());

		this.finBranch_header
				.setValue(fm.getFinBranch() == null ? "" : fm.getFinBranch() + "-" + fm.getLovDescFinBranchName());
		this.custCIF_header.setValue(fm.getLovDescCustCIF());
		this.custShrtName.setValue(fm.getLovDescCustShrtName());
		this.custShrtName.setStyle("margin-left:10px; display:inline-block; padding-top:5px; white-space:nowrap;");

		this.recordStatus.setValue(dsh.getRecordStatus());
	}

	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final DocumentStatus adsh = new DocumentStatus();
		BeanUtils.copyProperties(dsh, adsh);
		boolean isNew = false;

		doWriteComponentsToBean(adsh);

		if (!saveDocuments(adsh)) {
			return;
		}

		isNew = adsh.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(adsh.getRecordType())) {
				adsh.setVersion(adsh.getVersion() + 1);
				if (isNew) {
					adsh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					adsh.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					adsh.setNewRecord(true);
				}
			}
		} else {
			adsh.setVersion(adsh.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(adsh, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean saveDocuments(DocumentStatus adsh) {
		if (this.docmentslistBox.getItemCount() == 0) {
			throw new WrongValueException(this.docmentslistBox,
					Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_FeePayables.title") }));
		} else {
			documentStatus.clear();
			for (Listitem listitem : docmentslistBox.getItems()) {
				DocumentStatusDetail ds = (DocumentStatusDetail) listitem.getAttribute("doc");
				boolean isNew = false;

				isNew = ds.isNewRecord();
				if (isWorkFlowEnabled()) {
					if (StringUtils.isBlank(ds.getRecordType())) {
						ds.setVersion(ds.getVersion() + 1);
						if (isNew) {
							ds.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							ds.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							ds.setNewRecord(true);
						}
					}
				} else {
					// set the tranType according to RecordType
					if (isNew) {
						ds.setVersion(1);
						ds.setRecordType(PennantConstants.RCD_ADD);
					}

					if (StringUtils.isBlank(ds.getRecordType())) {
						ds.setRecordType(PennantConstants.RCD_UPD);
					}
				}
				documentStatus.add(ds);
			}

		}
		return true;

	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.dsh);
	}

	protected void refreshList() {
		documentStatusListCtrl.search();
	}

	private boolean doProcess(DocumentStatus adsh, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		adsh.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		adsh.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		adsh.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			adsh.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(adsh.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, adsh);
				}

				if (isNotesMandatory(taskId, adsh)) {
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

			adsh.setTaskId(taskId);
			adsh.setNextTaskId(nextTaskId);
			adsh.setRoleCode(getRole());
			adsh.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(adsh, tranType);
			String operationRefs = getServiceOperations(taskId, adsh);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(adsh, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(adsh, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DocumentStatus adsh = (DocumentStatus) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = documentService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = documentService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					try {
						auditHeader = documentService.doApprove(auditHeader);
					} catch (Exception e) {
						MessageUtil.showError(e);
						return false;
					}

					if (adsh.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = documentService.doReject(auditHeader);
					if (adsh.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_documentStatusDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_documentStatusDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.dsh), true);
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

	private AuditHeader getAuditHeader(DocumentStatus adsh, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, adsh.getBefImage(), adsh);
		return new AuditHeader(getReference(), null, null, null, auditDetail, adsh.getUserDetails(), getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.dsh.getId());
	}

	public String getValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	private void doEdit() {

		List<Listitem> listitems = this.docmentslistBox.getItems();
		for (Listitem listitem : listitems) {
			Combobox status = (Combobox) listitem.getChildren().get(3).getLastChild();
			Textbox remarks = (Textbox) listitem.getChildren().get(4).getLastChild();
			ExtendedCombobox covenants = (ExtendedCombobox) listitem.getChildren().get(5).getLastChild();

			status.setDisabled(isReadOnly("DocumentStatusDialog_status"));
			remarks.setReadonly(isReadOnly("DocumentStatusDialog_remarks"));
			covenants.setReadonly(isReadOnly("DocumentStatusDialog_covenants"));
		}

	}

	private void doWriteComponentsToBean(DocumentStatus knockOff) {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		List<Listitem> listitems = this.docmentslistBox.getItems();

		for (Listitem listitem : listitems) {
			DocumentStatusDetail ds = (DocumentStatusDetail) listitem.getAttribute("doc");

			Combobox status = (Combobox) listitem.getChildren().get(3).getLastChild();

			ds.setStatus(getValue(status));

			Textbox remarks = (Textbox) listitem.getChildren().get(4).getLastChild();
			ds.setRemarks(remarks.getValue());

			ExtendedCombobox covenants = (ExtendedCombobox) listitem.getChildren().get(5).getLastChild();

			StringBuilder list = new StringBuilder();
			for (String covenant : covenants.getSelectedValues().keySet()) {
				if (list.length() > 0) {
					list.append(",");
				}

				list.append(covenant);
			}

			ds.setCovenants(list.toString());
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	private void appendDocuments(List<DocumentStatusDetail> list) {
		if (this.docmentslistBox.getItems() != null) {
			this.docmentslistBox.getItems().clear();
		}

		int i = 0;
		for (DocumentStatusDetail doc : list) {
			i++;
			Listitem item = new Listitem();

			item.setAttribute("doc", doc);

			boolean isdocApproved = false;
			if (doc.getProcessed() == 1) {
				isdocApproved = true;
			}

			if (isdocApproved) {
				item.setDisabled(true);
			}

			// Document Category
			Listcell listCell = new Listcell();
			listCell.setId("docCategory".concat(String.valueOf(i)));
			listCell.appendChild(new Label(doc.getDocument().getDocCategory()));
			listCell.setParent(item);

			// Document Name
			listCell = new Listcell();
			listCell.setId("docName".concat(String.valueOf(i)));
			listCell.appendChild(new Label(doc.getDocument().getDocName()));
			listCell.setParent(item);

			// View
			final Button view = new Button();
			view.setSclass("z-toolbarbutton");
			view.setLabel("View");
			view.addEventListener("onClick",
					new onDocViewDoubleClicked(doc.getDocument().getDocType(), doc.getDocId()));
			listCell = new Listcell();
			listCell.appendChild(view);
			listCell.setParent(item);

			// Status
			listCell = new Listcell();
			listCell.setId("Status".concat(String.valueOf(i)));
			Combobox status = new Combobox();
			status.setWidth("100px");
			status.setReadonly(true);
			doc.setStatus(StringUtils.trimToEmpty(doc.getStatus()));
			status.setValue(doc.getStatus());
			fillStatus(doc, status);
			if (isdocApproved) {
				status.setDisabled(true);
				status.setButtonVisible(false);
			}
			ComponentsCtrl.applyForward(status, "onSelect=onDocStatusSelection");
			listCell.appendChild(status);
			listCell.setParent(item);

			// Remarks
			listCell = new Listcell();
			listCell.setId("remarks".concat(String.valueOf(i)));
			Textbox textbox = new Textbox();
			textbox.setWidth("450px");
			textbox.setHeight("50px");
			textbox.setMultiline(true);
			textbox.setValue(doc.getRemarks());
			if (isdocApproved) {
				textbox.setReadonly(true);
				textbox.setDisabled(true);
			}
			listCell.appendChild(textbox);
			listCell.setParent(item);

			listCell = new Listcell();
			listCell.setId("covenant".concat(String.valueOf(i)));
			ExtendedCombobox covenant = new ExtendedCombobox();
			Map<String, Object> map = new HashMap<>();
			if (StringUtils.isNotEmpty(doc.getCovenants())) {
				covenant.setValue(doc.getCovenants());
				for (String key : doc.getCovenants().split(",")) {
					map.put(key, key);
				}

				covenant.setSelectedValues(map);
			}

			fillCovenants(covenant, doc.getDocument().getDocCategory());
			listCell.appendChild(covenant);
			listCell.setParent(item);
			status.setAttribute("covenant", covenant);

			if (doc.getStatus().equals("R") || isdocApproved) {
				covenant.setReadonly(true);
				covenant.setButtonDisabled(true);
				covenant.setInputAllowed(false);
			} else {
				covenant.setReadonly(false);
				covenant.setButtonDisabled(false);
				covenant.setInputAllowed(true);
			}

			this.docmentslistBox.appendChild(item);

		}

	}

	public void onDocStatusSelection(Event event) {
		logger.info(Literal.ENTERING);

		ForwardEvent fe = (ForwardEvent) event;
		Combobox cb = (Combobox) fe.getOrigin().getTarget();

		String st = cb.getSelectedItem().getLabel();
		ExtendedCombobox ecb = (ExtendedCombobox) cb.getAttribute("covenant");

		if (st.equals("Rejected ")) {
			ecb.setReadonly(true);
			ecb.setButtonDisabled(true);
		} else {
			ecb.setReadonly(false);
			ecb.setButtonDisabled(false);
		}

		logger.info(Literal.LEAVING);

	}

	public final class onDocViewDoubleClicked implements EventListener<Event> {
		private String docType;
		private long docId;

		public onDocViewDoubleClicked(String docType, long docId) {
			this.docType = docType;
			this.docId = docId;
		}

		@Override
		public void onEvent(Event event) throws Exception {
			DocumentDetails detail = null;

			if ("CD".equals(docType)) {
				detail = customerDocumentDAO.getCustDocByCustAndDocType(docId, "_View");
			} else {
				detail = documentDetailsDAO.getDocumentDetailsById(docId, "_View", true);
			}
			if (detail == null || detail.getDocRefId() == null) {
				MessageUtil.showError("Document details not found.");
				return;
			}

			detail.setDocImage(dMSService.getById(detail.getDocRefId()));

			if (StringUtils.isNotBlank(detail.getDocName()) && detail.getDocImage() != null
					&& StringUtils.isNotBlank(detail.getDocImage().toString())) {
				try {
					if (StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)
							|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_MSG)
							|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_DOC)
							|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_DOCX)
							|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_EXCEL)
							|| StringUtils.trimToEmpty(detail.getDoctype()).equalsIgnoreCase("png")) {
						Filedownload.save(detail.getDocImage(), "application/octet-stream", detail.getDocName());
					} else {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("FinDocumentDetail", detail);
						Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
					}
				} catch (Exception e) {
					logger.debug(e);
				}
			} else if (StringUtils.isNotBlank(detail.getDocUri())) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("documentRef", detail);
				Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
			} else {
				MessageUtil.showError("Document Details not Found.");
			}

		}

	}

	private void fillStatus(DocumentStatusDetail doc, Combobox status) {
		fillComboBox(status, doc.getStatus(), getStatusList());
	}

	protected void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (StringUtils.equals(valueLabel.getValue(), value)) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}

	public static List<ValueLabel> getStatusList() {
		List<ValueLabel> list = new ArrayList<>();
		list.add(new ValueLabel("", Labels.getLabel("Combo.Select")));
		list.add(new ValueLabel("A", "Approved "));
		list.add(new ValueLabel("R", "Rejected "));
		return list;
	}

	private void fillCovenants(ExtendedCombobox covenant, String docType) {
		covenant.setMultySelection(true);
		covenant.setModuleName("DocumentStatusCovenant");
		covenant.setValueColumn("Code");
		covenant.setValidateColumns(new String[] { "Code" });
		Filter[] filter = new Filter[2];
		filter[0] = new Filter("KeyReference", this.dsh.getFinReference(), Filter.OP_EQUAL);
		filter[1] = new Filter("DocType", docType, Filter.OP_EQUAL);
		covenant.setFilters(filter);
	}

	public void setDocumentService2(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
