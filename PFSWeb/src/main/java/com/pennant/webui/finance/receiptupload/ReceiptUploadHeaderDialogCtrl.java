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
 * * FileName : ReceiptUploadHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2018 Somasekhar 0.1 * 07-09-2018 Somasekhar 0.2 change two user workflow to * single user workflow * * * * * *
 * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.receiptupload;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/ReceiptDialog/ReceiptDialogDialog.zul file.
 */
public class ReceiptUploadHeaderDialogCtrl extends GFCBaseCtrl<ReceiptUploadHeader> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ReceiptUploadHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReceiptUploadDialog; // autoWired
	protected Textbox txtFileName; // autoWired
	protected Listbox listBoxUploadDetais;
	protected Listbox listBoxUploadFailedDetais;
	protected Paging pagingSuccessRecordList;
	protected Paging pagingFailedRecordList;
	private Grid grid_basicDetails;

	private boolean enqModule = false;
	private ReceiptUploadHeader receiptUploadHeader;
	private ReceiptUploadHeaderService receiptUploadHeaderService;
	private ReceiptUploadHeaderListCtrl receiptUploadListCtrl;

	private PagedListWrapper<ReceiptUploadDetail> receiptSuccessPagedListWrapper;
	private PagedListWrapper<ReceiptUploadDetail> receiptFailedPagedListWrapper;

	/**
	 * default constructor.<br>
	 */
	public ReceiptUploadHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReceiptUpload";
		super.moduleCode = "ReceiptUploadHeader";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected ReceiptDialog object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ReceiptUploadDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReceiptUploadDialog);

		/* set components visible dependent of the users rights */
		try {

			this.receiptUploadHeader = (ReceiptUploadHeader) arguments.get("uploadReceiptHeader");

			ReceiptUploadHeader befImage = new ReceiptUploadHeader();
			BeanUtils.copyProperties(this.receiptUploadHeader, befImage);
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			}

			this.receiptUploadHeader.setBefImage(befImage);
			setReceiptUploadHeader(this.receiptUploadHeader);
			setReceiptUploadListCtrl((ReceiptUploadHeaderListCtrl) arguments.get("receiptUploadListCtrl"));

			doLoadWorkFlow(this.receiptUploadHeader.isWorkflow(), this.receiptUploadHeader.getWorkflowId(),
					this.receiptUploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			setReceiptFailedUploadPageList();
			setReceiptSuccessUploadPageList();

			getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
			doCheckRights();

			getBorderLayoutHeight();
			int dialogHeight = grid_basicDetails.getRows().getVisibleItemCount() * 20 + 170;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listBoxUploadDetais.setHeight(listboxHeight + "px");
			int listRows = Math.round(listboxHeight / 24) - 1;
			pagingSuccessRecordList.setPageSize(listRows);

			listBoxUploadFailedDetais.setHeight(listboxHeight + "px");
			pagingFailedRecordList.setPageSize(listRows);

			// set Field Properties
			doSetFieldProperties();

			getBorderLayoutHeight();
			this.listBoxUploadDetais.setHeight(borderLayoutHeight - 130 + "px");
			this.listBoxUploadFailedDetais.setHeight(borderLayoutHeight - 130 + "px");

			doShowDialog(getReceiptUploadHeader());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReceiptUploadDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReceiptUpload_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReceiptUpload_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReceiptUpload_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReceiptUpload_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_ReceiptUploadDialog);
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
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.receiptUploadHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	public void doWriteBeanToComponents(ReceiptUploadHeader ruh) {
		logger.debug("Entering");

		List<ReceiptUploadDetail> successReceiptUploadList = new ArrayList<>();
		List<ReceiptUploadDetail> failReceiptUploadList = new ArrayList<>();

		this.txtFileName.setValue(ruh.getFileName());
		this.recordStatus.setValue(ruh.getRecordStatus());
		for (ReceiptUploadDetail rud : ruh.getReceiptUploadList()) {
			if (ReceiptDetailStatus.SUCCESS.getValue() == rud.getProcessingStatus()) {
				successReceiptUploadList.add(rud);
			} else {
				failReceiptUploadList.add(rud);
			}
		}

		// setting success records in success tab
		this.pagingSuccessRecordList.setDetailed(true);
		getReceiptSuccessPagedListWrapper().initList(successReceiptUploadList, this.listBoxUploadDetais,
				this.pagingSuccessRecordList);
		this.listBoxUploadDetais.setItemRenderer(new ReceiptDetailHeaderListModelItemRenderer());

		// setting failed records in failed tab
		this.pagingFailedRecordList.setDetailed(true);
		getReceiptFailedPagedListWrapper().initList(failReceiptUploadList, this.listBoxUploadFailedDetais,
				this.pagingFailedRecordList);
		this.listBoxUploadFailedDetais.setItemRenderer(new ReceiptDetailHeaderListModelItemRenderer());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param ruh
	 */
	public void doWriteComponentsToBean(ReceiptUploadHeader ruh) {
		ruh.setTransactionDate(SysParamUtil.getAppDate());

		// count
		int sucess = 0;
		int failed = 0;

		if (ruh.getReceiptUploadList() != null && !ruh.getReceiptUploadList().isEmpty()) {
			for (ReceiptUploadDetail rud : ruh.getReceiptUploadList()) {
				if (ReceiptDetailStatus.SUCCESS.getValue() == rud.getProcessingStatus()) {
					sucess = sucess + 1;
				}
				if (ReceiptDetailStatus.FAILED.getValue() == rud.getProcessingStatus()) {
					failed = failed + 1;
				}
			}
		}
		ruh.setTotalRecords(sucess + failed);
		ruh.setFailedCount(failed);
		ruh.setSuccessCount(sucess);
		ruh.setRecordStatus(this.recordStatus.getValue());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aReceiptUploadHeader
	 */
	public void doShowDialog(ReceiptUploadHeader aReceiptUploadHeader) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aReceiptUploadHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.txtFileName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.txtFileName.focus();
				if (StringUtils.isNotBlank(aReceiptUploadHeader.getRecordType())) {
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
			doWriteBeanToComponents(aReceiptUploadHeader);

			if (enqModule) {
				this.btnNew.setVisible(false);
				this.btnEdit.setVisible(false);
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReceiptUploadDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ReceiptUploadHeader aReceiptUploadHeader = new ReceiptUploadHeader();
		BeanUtils.copyProperties(getReceiptUploadHeader(), aReceiptUploadHeader);

		doDelete("ReceiptUpload Header" + " : " + aReceiptUploadHeader.getUploadHeaderId(), aReceiptUploadHeader);

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void onDoDelete(final ReceiptUploadHeader ruh) {
		String tranType = PennantConstants.TRAN_DEL;

		ruh.setVersion(ruh.getVersion() + 1);
		ruh.setRecordType(PennantConstants.RECORD_TYPE_DEL);

		try {
			if (doProcess(ruh, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getReceiptUploadHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		this.txtFileName.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.receiptUploadHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(!enqModule);
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.txtFileName.setReadonly(true);

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
		this.txtFileName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final ReceiptUploadHeader aReceiptUploadHeader = new ReceiptUploadHeader();
		BeanUtils.copyProperties(getReceiptUploadHeader(), aReceiptUploadHeader);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		// fill the ReceiptDialog object with the components data
		doWriteComponentsToBean(aReceiptUploadHeader);

		if ((aReceiptUploadHeader.getTotalRecords()) == aReceiptUploadHeader.getFailedCount()) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoSucessRecords"));
			return;
		}

		// check if file is save already
		if (aReceiptUploadHeader.isNewRecord()) {
			boolean fileExist = this.receiptUploadHeaderService.isFileNameExist(aReceiptUploadHeader.getFileName());
			if (fileExist) {
				MessageUtil.showError(Labels.getLabel("label_File_Exits"));
				return;
			}
		}

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aReceiptUploadHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReceiptUploadHeader.getRecordType())) {
				aReceiptUploadHeader.setVersion(aReceiptUploadHeader.getVersion() + 1);
				if (isNew) {
					aReceiptUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReceiptUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReceiptUploadHeader.setNewRecord(true);
				}
			}
		} else {
			aReceiptUploadHeader.setVersion(aReceiptUploadHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aReceiptUploadHeader, tranType)) {
				refreshList();
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
	 * @param aReceiptUploadHeader (ReceiptDialog)
	 * 
	 * @param tranType             (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(ReceiptUploadHeader aReceiptUploadHeader, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aReceiptUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReceiptUploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReceiptUploadHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aReceiptUploadHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReceiptUploadHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReceiptUploadHeader);
				}

				if (isNotesMandatory(taskId, aReceiptUploadHeader)) {
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

			aReceiptUploadHeader.setTaskId(taskId);
			aReceiptUploadHeader.setNextTaskId(nextTaskId);
			aReceiptUploadHeader.setRoleCode(getRole());
			aReceiptUploadHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aReceiptUploadHeader, tranType);

			String operationRefs = getServiceOperations(taskId, aReceiptUploadHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReceiptUploadHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			// to make it as single user marking workflow enable false
			// remove it need for two user
			// ### 7/9/2018,Ticket id:124998
			// remove setting of record status and change doprocess method type
			// to null,if need two user workflow
			aReceiptUploadHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			aReceiptUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			auditHeader = getAuditHeader(aReceiptUploadHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, PennantConstants.method_doApprove);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReceiptUploadHeader aReceiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getReceiptUploadHeaderService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getReceiptUploadHeaderService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getReceiptUploadHeaderService().doApprove(auditHeader);

					if (aReceiptUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getReceiptUploadHeaderService().doReject(auditHeader);

					if (aReceiptUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ReceiptUploadDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ReceiptUploadDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.receiptUploadHeader), true);
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

	// WorkFlow Components

	/**
	 * @param aReceiptUploadHeader
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(ReceiptUploadHeader aReceiptUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReceiptUploadHeader.getBefImage(),
				aReceiptUploadHeader);
		return new AuditHeader(String.valueOf(aReceiptUploadHeader.getId()), null, null, null, auditDetail,
				aReceiptUploadHeader.getUserDetails(), getOverideMap());

	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.receiptUploadHeader);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getReceiptUploadListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.receiptUploadHeader.getUploadHeaderId());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ReceiptUploadHeaderService getReceiptUploadHeaderService() {
		return receiptUploadHeaderService;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public ReceiptUploadHeaderListCtrl getReceiptUploadListCtrl() {
		return receiptUploadListCtrl;
	}

	public void setReceiptUploadListCtrl(ReceiptUploadHeaderListCtrl receiptUploadListCtrl) {
		this.receiptUploadListCtrl = receiptUploadListCtrl;
	}

	public PagedListWrapper<ReceiptUploadDetail> getReceiptSuccessPagedListWrapper() {
		return receiptSuccessPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setReceiptSuccessUploadPageList() {
		if (this.receiptSuccessPagedListWrapper == null) {
			this.receiptSuccessPagedListWrapper = (PagedListWrapper<ReceiptUploadDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<ReceiptUploadDetail> getReceiptFailedPagedListWrapper() {
		return receiptFailedPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setReceiptFailedUploadPageList() {
		if (this.receiptFailedPagedListWrapper == null) {
			this.receiptFailedPagedListWrapper = (PagedListWrapper<ReceiptUploadDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public ReceiptUploadHeader getReceiptUploadHeader() {
		return receiptUploadHeader;
	}

	public void setReceiptUploadHeader(ReceiptUploadHeader receiptUploadHeader) {
		this.receiptUploadHeader = receiptUploadHeader;
	}
}
