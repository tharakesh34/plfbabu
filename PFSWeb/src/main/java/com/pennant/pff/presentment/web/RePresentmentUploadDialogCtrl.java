package com.pennant.pff.presentment.web;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.util.ExcelUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public class RePresentmentUploadDialogCtrl extends GFCBaseCtrl<FileUploadHeader> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(RePresentmentUploadDialogCtrl.class);

	protected Window windowRePresentMentUploadDialog;
	protected Radio downLoad;
	protected Radio upload;
	protected ExtendedCombobox fileName;
	protected Button btnDownload;
	protected Textbox txtFileName;
	protected Button btnBrowse;
	protected Space spaceTxtFileName;
	protected ExtendedCombobox entity;
	protected Button downloadTemplate;
	private int entitySize;

	private FileUploadHeader header;
	private transient UploadService<RePresentmentUploadDetail> rePresentmentUploadService;

	public RePresentmentUploadDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RePresentMentUpload";
		super.moduleCode = "RepresentUploadHeader";
	}

	public void onCreate$windowRePresentMentUploadDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowRePresentMentUploadDialog);

		try {
			this.header = (FileUploadHeader) arguments.get("uploadRePresentMentHeader");

			if (this.header == null) {
				header = rePresentmentUploadService.getUploadHeader(this.moduleCode);

				header.setNewRecord(true);
				header.setType(UploadTypes.RE_PRESENTMENT);
				header.setCreatedBy(getUserWorkspace().getUserDetails().getUserId());
				header.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			}

			header.setAppDate(SysParamUtil.getAppDate());

			FileUploadHeader befImage = new FileUploadHeader();
			BeanUtils.copyProperties(this.header, befImage);

			this.header.setBefImage(befImage);
			setUploadHeader(this.header);

			doLoadWorkFlow(this.header.isWorkflow(), this.header.getWorkflowId(), this.header.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction, false, false);
				getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(this.header);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.windowRePresentMentUploadDialog.onClose();
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void doShowDialog(FileUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		if (uploadHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.txtFileName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.txtFileName.focus();
				if (StringUtils.isNotBlank(uploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doCheckFields();

		try {
			doWriteBeanToComponents();
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.windowRePresentMentUploadDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doSave();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	@Override
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final FileUploadHeader uploadHeader = new FileUploadHeader();
		BeanUtils.copyProperties(this.header, uploadHeader);

		doWriteComponentsToBean(uploadHeader);

		Media media = uploadHeader.getMedia();

		String uploadPath = App.getResourcePath(PathUtil.FILE_UPLOADS_PATH, uploadHeader.getEntityCode(),
				uploadHeader.getType());

		try {
			File file = ExcelUtil.writeFile(uploadPath, media.getName(), media.getByteData());

			logger.info("Validating File content for the filename {}", file.getName());
			if (!validateFileContent(file)) {
				return;
			}

			uploadHeader.setFile(file);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			return;
		}

		uploadHeader.setWorkBook(this.header.getWorkBook());

		doLoadWorkFlow(uploadHeader.isWorkflow(), uploadHeader.getWorkflowId(), uploadHeader.getNextTaskId());

		logger.info("Saving Record as import in progress in RePresentUploadHeader table before validating records");
		prepareHeader(uploadHeader);

		uploadHeader.setId(rePresentmentUploadService.saveHeader(uploadHeader, TableType.TEMP_TAB));
		uploadHeader.setAppDate(header.getAppDate() == null ? SysParamUtil.getAppDate() : header.getAppDate());

		try {
			rePresentmentUploadService.importFile(uploadHeader);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			return;
		}

		closeDialog();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$entity(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.entity.setConstraint("");
		this.entity.setErrorMessage("");

		if (StringUtils.isBlank(this.entity.getValue())) {
			this.entity.setValue("", "");
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onCheck$downLoad(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doClearMessage();
		doCheckFields();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onCheck$upload(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.fileName.setValue("");

		doClearMessage();
		doCheckFields();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$downloadTemplate(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		String entityCode = this.entity.getValue();

		if (StringUtils.isEmpty(entityCode)) {
			throw new WrongValueException(this.entity, "Entity Code is mandatory");
		}

		String module = UploadTypes.RE_PRESENTMENT;
		String template = PathUtil.TEMPLATES;

		String templateName = "Representment01.xls";

		String path = App.getResourcePath(PathUtil.FILE_UPLOADS_PATH, entityCode, module, template);

		try {
			ExcelUtil.downloadTemplate(path, templateName, DocType.XLS);
		} catch (AppException e) {
			templateName = "Representment01.xlsx";
			try {
				ExcelUtil.downloadTemplate(path, templateName, DocType.XLSX);
			} catch (AppException ex) {
				MessageUtil.showError(ex);
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doSetValidation();
		String name = "";
		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (StringUtils.trimToNull(this.fileName.getValue()) == null) {
				throw new WrongValueException(this.fileName, Labels.getLabel("empty_file"));
			}
			name = this.fileName.getDescription();

		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorMessage(wve);

		StringBuilder condition = new StringBuilder();
		condition.append("Where FILENAME in (" + "'" + name + "'" + ")");
		String whereCond = new String(condition);

		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		searchCriteriaDesc.append("File Name is " + header.getFileName());

		String usrName = getUserWorkspace().getLoggedInUser().getFullName();

		// ReportsUtil.generateReport(usrName, "BulkFeeWaiverUploadReport", whereCond, searchCriteriaDesc);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onUpload$btnBrowse(UploadEvent event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.txtFileName.setText("");

		doRemoveValidation();

		Media media = event.getMedia();
		String name = media.getName();

		this.header.setMedia(media);
		this.txtFileName.setText(name);
		this.header.setFileName(name);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));
		closeDialog(true);
		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void closeDialog(boolean confirmationreq) {
		doClose(confirmationreq);
	}

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.txtFileName);
		readOnlyComponent(true, this.btnBrowse);
		readOnlyComponent(true, this.btnDownload);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.btnBrowse);
		this.txtFileName.setErrorMessage("");
		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	protected void doPostClose() {
		Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.header.isNewRecord()) {
			readOnlyComponent(isReadOnly("button_WaiverUploadDialog_btnBrowse"), this.btnDownload);
			this.btnBrowse.setVisible(true);
		} else {
			this.btnBrowse.setVisible(false);
			this.btnDownload.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.header.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug(Literal.LEAVING);
	}

	private void doWriteComponentsToBean(FileUploadHeader header) {
		doRemoveValidation();

		doSetValidation();

		String name = this.txtFileName.getValue();

		try {
			ExcelUtil.isValidFile(name, 200, "^[a-zA-Z0-9 ._]*$");
		} catch (AppException e) {
			throw new WrongValueException(this.txtFileName, e.getMessage());
		}

		try {
			if (!this.entity.isReadonly()) {
				this.entity.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityCode"), null, true, true));
				header.setEntityCode(this.entity.getValue());
			}
		} catch (WrongValueException we) {
			throw new WrongValueException(this.entity, we.getMessage());
		}

		header.setFileName(name);

		header.setProgress(Status.DEFAULT.getValue());
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			String name = StringUtils.trimToNull(this.txtFileName.getValue());
			if (name != null && this.rePresentmentUploadService.isExists(name)) {
				throw new WrongValueException(this.txtFileName,
						this.txtFileName.getValue() + Labels.getLabel("label_File_Exits"));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorMessage(wve);

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		this.entity.setConstraint("");
		this.entity.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RePresentMentUpload_btnSave"));

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.groupboxWf.setVisible(isWorkFlowEnabled());

		this.fileName.setModuleName("FileUploadHeader");
		this.fileName.setMandatoryStyle(true);
		this.fileName.setDisplayStyle(2);
		this.fileName.setValueColumn("Id");
		this.fileName.setDescColumn("FileName");
		this.fileName.setValueType(DataType.LONG);
		this.fileName.setValidateColumns(new String[] { "Id" });

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("Type", header.getType(), Filter.OP_EQUAL);
		this.fileName.setFilters(filter);

		this.entity.setModuleName("Entity");
		this.entity.setMandatoryStyle(true);
		this.entity.setDisplayStyle(2);
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		filter = new Filter[1];
		filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);

		this.entity.setFilters(filter);

		List<Entity> entities = rePresentmentUploadService.getEntities();

		this.entitySize = entities.size();
		if (this.entitySize == 1) {
			this.entity.setValue(entities.get(0).getEntityCode());
			this.entity.setDescColumn(entities.get(0).getEntityDesc());
			this.entity.setReadonly(true);
		}

		this.txtFileName.setReadonly(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	private void doCheckFields() {
		logger.debug(Literal.ENTERING);

		boolean isUpload = this.upload.isChecked();

		this.btnBrowse.setDisabled(!isUpload);
		this.fileName.setMandatoryStyle(!isUpload);
		this.downloadTemplate.setDisabled(!isUpload);
		this.entity.setReadonly(!isUpload || this.entitySize == 1);

		this.btnDownload.setDisabled(isUpload);
		this.fileName.setReadonly(isUpload);
		this.spaceTxtFileName.setSclass(isUpload ? PennantConstants.mandateSclass : "");

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setValue(header.getFileName());
		this.recordStatus.setValue(header.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	private boolean validateFileContent(File file) {
		logger.debug(Literal.ENTERING);

		int sheetIndex = 0;
		int minNoOfRows = 1;
		int rowIndex = 0;

		List<String> headers = getHeaders();

		try {
			Workbook workbook = ExcelUtil.getWorkBook(file);
			header.setWorkBook(workbook);

			ExcelUtil.basicValidations(workbook, sheetIndex, minNoOfRows, rowIndex, headers);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			return false;
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	private List<String> getHeaders() {
		List<String> headers = new ArrayList<>();

		headers.add("LOAN NO");
		headers.add("DUE DATE");

		return headers;
	}

	private void prepareHeader(FileUploadHeader header) {
		header.setNewRecord(true);
		header.setProgress(Status.IN_PROCESS.getValue());

		String taskId = getTaskId(getRole());
		String nextTaskId;
		header.setRecordStatus(userAction.getSelectedItem().getValue().toString());

		if ("Save".equals(userAction.getSelectedItem().getLabel())) {
			nextTaskId = taskId + ";";
		} else {
			nextTaskId = StringUtils.trimToEmpty(header.getNextTaskId());

			nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			if ("".equals(nextTaskId)) {
				nextTaskId = getNextTaskIds(taskId, header);
			}

			if (isNotesMandatory(taskId, header) && !notesEntered) {
				MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
				return;
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

		header.setRoleCode(getRole());
		header.setTaskId(taskId);
		header.setNextTaskId(nextTaskId);
		header.setNextRoleCode(nextRoleCode);
		header.setVersion(header.getVersion() + 1);
		header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setCreatedOn(new Timestamp(System.currentTimeMillis()));

		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		header.setUserDetails(loggedInUser);
		header.setLastMntBy(loggedInUser.getUserId());
		header.setCreatedBy(loggedInUser.getUserId());
	}

	private void showErrorMessage(List<WrongValueException> wve) {
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}

			throw new WrongValuesException(wvea);
		}
	}

	@Autowired
	public void setRePresentmentUploadService(UploadService<RePresentmentUploadDetail> rePresentmentUploadService) {
		this.rePresentmentUploadService = rePresentmentUploadService;
	}

	public void setUploadHeader(FileUploadHeader header) {
		this.header = header;
	}

}
