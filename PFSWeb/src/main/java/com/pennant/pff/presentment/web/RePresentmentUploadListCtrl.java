package com.pennant.pff.presentment.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennant.pff.presentment.service.ExtractionService;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.file.UploadContants.Status;

public class RePresentmentUploadListCtrl extends GFCBaseListCtrl<FileUploadHeader> {
	private static final long serialVersionUID = 1817958653208633892L;
	private static final Logger logger = LogManager.getLogger(RePresentmentUploadListCtrl.class);

	protected Window listWindow;
	protected Borderlayout borderLayout;
	protected Paging paging;
	protected Row entityRow;
	protected Listbox soEntityCode;
	protected ExtendedCombobox entityCode;
	protected Listbox soUploadId;
	protected Intbox uploadId;
	protected Listbox soFileName;
	protected Textbox fileName;
	protected Row recordStatusRow;
	protected Listbox listBox;
	protected Listhead listHead;
	protected Listheader lhUploadID;
	protected Listheader lhFileName;
	protected Listheader lhSuccessCount;
	protected Listheader lhFailedCount;
	protected Listheader lhTotalCount;
	protected Listheader lhProcCount;
	protected Listheader lhCheckBox;
	protected Listcell lcCheckBox;
	protected Listitem liCheckbox;
	protected Checkbox lhCheckBoxComp;
	protected Button btnReject;
	protected Button btnApprove;
	protected Button btnDownload;
	protected Button buttonSearch;

	private transient UploadService<RePresentmentUploadDetail> rePresentmentUploadService;
	private transient ExtractionService extractionService;

	private Map<Long, String> rePresentIdMap = new HashMap<>();

	public RePresentmentUploadListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addWhereClause(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "RepresentUploadHeader";
		super.pageRightName = "UploadHeaderList";
		super.tableName = "FILE_UPLOAD_HEADER_AVIEW";
		super.queueTableName = "FILE_UPLOAD_HEADER_TVIEW";
	}

	public void onCreate$listWindow(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(listWindow, borderLayout, listBox, paging);

		prepareItemRenderer();

		registerButton(buttonSearch);

		registerField("Id", lhUploadID, SortOrder.NONE, uploadId, soUploadId, Operators.NUMERIC);
		registerField("fileName", lhFileName, SortOrder.NONE, fileName, soFileName, Operators.STRING);
		registerField("successRecords", lhSuccessCount, SortOrder.NONE);
		registerField("failureRecords", lhFailedCount, SortOrder.NONE);
		registerField("progress", lhProcCount, SortOrder.NONE);
		registerField("totalRecords", lhTotalCount, SortOrder.NONE);
		registerField("entityCode", entityCode, SortOrder.NONE, soEntityCode, Operators.STRING);

		doRenderPage();
		this.rePresentIdMap.clear();
		doSetFieldProperties();
		lhCheckBoxComp.setDisabled(listBox.getItems().isEmpty());

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetFieldProperties() {
		this.recordStatusRow.setVisible(false);
		this.btnApprove.setVisible(true);
		this.btnDownload.setVisible(true);
		this.btnReject.setVisible(true);
		this.print.setVisible(false);
		this.lhCheckBox.setVisible(true);
		this.entityRow.setVisible(true);
		this.lhUploadID.setVisible(true);

		liCheckbox = new Listitem();
		lcCheckBox = new Listcell();
		lhCheckBoxComp = new Checkbox();
		lcCheckBox.appendChild(lhCheckBoxComp);
		lhCheckBoxComp.setChecked(false);
		lhCheckBoxComp.addForward("onClick", self, "onClickLHCheckBox");
		liCheckbox.appendChild(lcCheckBox);

		if (lhCheckBox.getChildren() != null) {
			lhCheckBox.getChildren().clear();
		}

		lhCheckBox.appendChild(lhCheckBoxComp);

		List<Entity> entity = rePresentmentUploadService.getEntities();

		if (entity.size() == 1) {
			this.entityCode.setValue(entity.get(0).getEntityCode());
			this.entityCode.setDescColumn(entity.get(0).getEntityDesc());
			this.entityCode.setReadonly(true);
			this.soEntityCode.setDisabled(true);
		}

		this.entityCode.setMaxlength(8);
		this.entityCode.setTextBoxWidth(135);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);

		this.entityCode.setFilters(filter);
	}

	public void onClickLHCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		for (Listitem listitem : listBox.getItems()) {
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			if (cb.isDisabled()) {
				continue;
			}

			cb.setChecked(lhCheckBoxComp.isChecked());
		}

		if (lhCheckBoxComp.isChecked()) {
			for (Long id : getHeaderIds()) {
				rePresentIdMap.put(id, null);
			}
		} else {
			rePresentIdMap.clear();
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private List<Long> getHeaderIds() {
		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();
		searchObject.addField("Id");
		searchObject.addTabelName(this.queueTableName);

		for (SearchFilterControl sfc : searchControls) {
			Filter filter = sfc.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}

		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		searchObject.addWhereClause(whereClause.toString());

		List<Map<String, Long>> list = pagedListWrapper.getPagedListService().getBySearchObject(searchObject);
		List<Long> headerIds = new ArrayList<>();
		list.forEach(map -> headerIds.add(Long.parseLong(String.valueOf(map.get("Id")))));

		return headerIds;
	}

	public void onClick$buttonSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.rePresentIdMap.clear();
		this.lhCheckBoxComp.setChecked(false);
		this.rePresentIdMap.clear();

		doSetValidations();
		search();

		if (listBox.getItems().isEmpty()) {
			lhCheckBoxComp.setDisabled(true);
			listBox.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
		} else {
			lhCheckBoxComp.setDisabled(false);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetValidations() {
		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (!this.entityCode.isReadonly())
				this.entityCode
						.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityCode"), null, true, true));
			this.entityCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}

			throw new WrongValuesException(wvea);
		}
	}

	private void doRemoveValidation() {
		this.entityCode.setConstraint("");
		this.entityCode.setErrorMessage("");
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doRefresh();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doRefresh() {
		doReset();
		doRemoveValidation();

		this.rePresentIdMap.clear();
		this.lhCheckBoxComp.setChecked(false);
		this.listbox.getItems().clear();
		this.entityCode.setValue(null);
		this.uploadId.setValue(null);
		this.fileName.setValue(null);
		this.entityCode.setValue("");
		this.entityCode.setDescColumn("");

		if (!listBox.getItems().isEmpty()) {
			lhCheckBoxComp.setDisabled(false);
		} else {
			lhCheckBoxComp.setDisabled(true);
			listBox.setEmptyMessage("");

		}
		this.paging.setTotalSize(0);
	}

	public void onClick$btnApprove(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		lhCheckBoxComp.setDisabled(true);

		List<Long> headerIdList = getListofRePresentMentUpload();
		rePresentIdMap.clear();

		if (headerIdList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("label_ListNoEmpty"));
			return;
		}

		Collections.sort(headerIdList);

		logger.info("Checking Authority for HeaderIds {}", headerIdList);
		List<FileUploadHeader> uploadHeadersList = doCheckAuthority(headerIdList);

		if (uploadHeadersList.isEmpty()) {
			logger.info("User is not Allowed to Approve");
			return;
		}

		try {
			doApprove(uploadHeadersList);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		lhCheckBoxComp.setChecked(false);
		search();
		Clients.showNotification("RePresentMent Process initialized.", "info", null, null, -1);
		lhCheckBoxComp.setDisabled(false);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnReject(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<Long> rePresentMentList = getListofRePresentMentUpload();

		if (rePresentMentList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("label_ListNoEmpty"));
			return;
		}

		List<FileUploadHeader> list = doCheckAuthority(rePresentMentList);

		if (list.isEmpty()) {
			return;
		}

		list.forEach(fuh -> doProcess(fuh, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_REJECTED));

		doReset();
		search();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doApprove(List<FileUploadHeader> rpuhList) {
		new Thread(() -> {
			List<Long> headerIdList = rpuhList.stream().map(FileUploadHeader::getId).collect(Collectors.toList());
			try {

				for (FileUploadHeader header : rpuhList) {
					logger.info("Approving Header {}, {}", header.getId(), headerIdList);

					header.setProgress(Status.APPROVED.getValue());
					doProcess(header, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_APPROVED);
				}

				int extractPresentment = extractionService.extractRePresentment(headerIdList);

				if (extractPresentment > 0) {
					logger.info("RePresentment Process is Initiated");
				}

			} catch (Exception e) {
				headerIdList.forEach(
						ruh -> rePresentmentUploadService.updateProgress(ruh, Status.PROCESS_FAILED.getValue()));
				logger.error(Literal.EXCEPTION, e);
			} finally {
				logger.info("Updating Status as Completed for the HeaderIdIs {}", headerIdList);
				rePresentmentUploadService.updateStatus(headerIdList);
			}
		}).start();

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private boolean doProcess(FileUploadHeader ruh, String tranType, String rcdStatusApproved) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;

		ruh.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		ruh.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ruh.setUserDetails(getUserWorkspace().getLoggedInUser());
		ruh.setApprovedBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		ruh.setApprovedOn(new Timestamp(System.currentTimeMillis()));

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			ruh.setRecordStatus(rcdStatusApproved);

			if ("Save".equals(rcdStatusApproved)) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(ruh.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, ruh);
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

			ruh.setTaskId(taskId);
			ruh.setNextTaskId(nextTaskId);
			ruh.setRoleCode(getRole());
			ruh.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(ruh, tranType);
			String operationRefs = getServiceOperations(taskId, ruh);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(ruh, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(ruh, tranType);
			processCompleted = doSaveProcess(auditHeader, PennantConstants.method_doApprove);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = rePresentmentUploadService.delete(auditHeader);
				} else {
					auditHeader = rePresentmentUploadService.saveOrUpdate(auditHeader);
				}
			} else {
				switch (StringUtils.trimToEmpty(method)) {
				case PennantConstants.method_doApprove:
					auditHeader = rePresentmentUploadService.doApprove(auditHeader);
					break;
				case PennantConstants.method_doReject:
					auditHeader = rePresentmentUploadService.doReject(auditHeader);
					break;
				default:
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.listWindow, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.listWindow, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
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

	private List<FileUploadHeader> doCheckAuthority(List<Long> headerIDs) {
		List<FileUploadHeader> list = new ArrayList<>();

		for (long id : headerIDs) {
			FileUploadHeader rpuh = rePresentmentUploadService.getUploadHeaderById(id);

			if (rpuh == null || rpuh.getProgress() == Status.IN_PROCESS.getValue()) {
				continue;
			}

			String whereCond = " Id = ?";

			if (doCheckAuthority(rpuh, whereCond, new Object[] { rpuh.getId() })) {
				if (isWorkFlowEnabled() && rpuh.getWorkflowId() == 0) {
					rpuh.setWorkflowId(getWorkFlowId());
				}
				doLoadWorkFlow(isWorkFlowEnabled(), rpuh.getWorkflowId(), rpuh.getNextTaskId());
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return new ArrayList<>();
			}

			list.add(rpuh);
		}

		return list;
	}

	public void onItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBox.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		long id = (long) selectedItem.getAttribute("id");
		FileUploadHeader header = rePresentmentUploadService.getUploadHeaderById(id);

		if (header == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (header.getProgress() == Status.IMPORT_IN_PROCESS.getValue()) {
			MessageUtil.showMessage("Import is in process for this record");
			search();
			return;
		}

		if (PennantConstants.RCD_STATUS_SUBMITTED.equals(header.getRecordStatus())) {
			return;
		}

		String whereCond = " Where Id = ?";

		if (doCheckAuthority(header, whereCond, new Object[] { header.getId() })) {
			if (isWorkFlowEnabled() && header.getWorkflowId() == 0) {
				header.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(header);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(FileUploadHeader header) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> args = new HashMap<>();

		args.put("enqModule", PennantConstants.RCD_STATUS_APPROVED.equals(header.getRecordStatus()));
		args.put("uploadRePresentMentHeader", header);
		args.put("rePresentMentUploadHeaderListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/PresentmentDetail/RePresentmentUploadDialog.zul", null, args);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doPrintResults();
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void prepareItemRenderer() {
		setItemRender((item, uph, count) -> {
			Listcell lc;

			long id = uph.getId();

			lc = new Listcell();

			Checkbox checkBox = new Checkbox();
			checkBox.setValue(id);
			checkBox.addForward("onClick", self, "onClickLCCheckBox");

			checkBox.setChecked(lhCheckBoxComp.isChecked() || rePresentIdMap.containsKey(id));
			boolean checkBoxSts = uph.getProgress() == Status.IMPORT_IN_PROCESS.getValue();
			checkBox.setChecked(checkBoxSts);
			checkBox.setDisabled(checkBoxSts);

			lc.appendChild(checkBox);
			item.appendChild(lc);

			lc = new Listcell(String.valueOf(id));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getFileName()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getTotalRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getProgress()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getSuccessRecords()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getFailureRecords()));
			lc.setParent(item);

			switch (Status.value(uph.getProgress())) {
			case IMPORTED:
				lc = new Listcell("Imported");
				break;
			case IMPORT_IN_PROCESS:
				lc = new Listcell("In Progress..");
				break;
			case IMPORT_FAILED:
				lc = new Listcell("Import Failed");
				break;

			default:
				lc = new Listcell(uph.getRecordStatus());
				break;
			}

			lc.setParent(item);

			lc = new Listcell(String.valueOf(uph.getRecordType()));
			lc.setParent(item);

			item.setAttribute("date", uph);
			item.setAttribute("id", id);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
		});
	}

	public void onClickLCCheckBox(ForwardEvent event) {

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		if (checkBox.isChecked()) {
			rePresentIdMap.put(Long.valueOf(checkBox.getValue().toString()), checkBox.getValue().toString());
		} else {
			rePresentIdMap.remove(Long.valueOf(checkBox.getValue().toString()));
		}

		if (rePresentIdMap.size() == this.paging.getTotalSize()) {
			lhCheckBoxComp.setChecked(true);
		} else {
			lhCheckBoxComp.setChecked(false);
		}
	}

	private List<Long> getListofRePresentMentUpload() {
		if (lhCheckBoxComp.isChecked()) {
			return getHeaderIds();
		}

		return new ArrayList<>(rePresentIdMap.keySet());
	}

	private AuditHeader getAuditHeader(FileUploadHeader rpuh, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, rpuh.getBefImage(), rpuh);
		return new AuditHeader(String.valueOf(rpuh.getId()), null, null, null, ad, rpuh.getUserDetails(),
				getOverideMap());
	}

	public void onClick$help(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		doShowHelp(event);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<Long> headerIdList = getListofRePresentMentUpload();
		rePresentIdMap.clear();

		if (headerIdList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("label_ListNoEmpty"));
			return;
		}

		if (headerIdList.size() > 1) {
			MessageUtil.showError(Labels.getLabel("MORETHEN_FILE"));
			return;
		}

		try {
			rePresentmentUploadService.downloadReport(headerIdList.get(0), "_Temp");
		} catch (AppException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	@Autowired
	public void setRePresentmentUploadService(UploadService<RePresentmentUploadDetail> rePresentmentUploadService) {
		this.rePresentmentUploadService = rePresentmentUploadService;
	}

	@Autowired
	public void setExtractionService(ExtractionService extractionService) {
		this.extractionService = extractionService;
	}
}
