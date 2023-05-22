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
 * * FileName : UploadListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2018 * * Modified Date
 * : 04-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.upload;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.JvPostingConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.upload.model.UploadListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/Uploads/UploadList.zul file.
 */
public class UploadListCtrl extends GFCBaseListCtrl<UploadHeader> {
	private static final long serialVersionUID = 5327118548986437717L;

	protected Window window_UploadList;
	protected Borderlayout borderLayout_UploadList;
	protected Listbox listBoxUpload;
	protected Paging pagingUploadList;

	protected Listheader listheader_UploadList_FileName;
	protected Listheader listheader_UploadList_TransactionDate;
	protected Listheader listheader_UploadList_RecordCount;

	protected Button button_UploadList_New;
	protected Button button_UploadList_SearchDialog;

	protected Textbox fileName;
	protected Datebox transactionDate;

	protected Listbox sortOperator_FileName;
	protected Listbox sortOperator_TransactionDate;

	private transient UploadHeaderService uploadHeaderService;

	private String module = "";
	protected Button btnReject;
	protected Button btnApprove;
	protected Button btnDownload;
	protected Label label_UploadList_TransactionDate;
	protected Label label_UploadList_EntityCode;
	protected Listbox sortOperator_Entity;
	protected ExtendedCombobox entity;
	protected Label label_UploadList_UploadId;
	protected Listbox sortOperator_UploadId;
	protected Listheader listheader_UploadList_UploadId;
	protected Intbox uploadId;
	protected Listheader listheader_UploadList_EntityCode;
	protected Listheader listheader_UploadList_UserName;
	protected Listheader listheader_UploadList_Total;
	protected Listheader listheader_UploadList_Success;
	protected Listheader listheader_UploadList_Failed;

	/**
	 * The default constructor.
	 */
	public UploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "UploadHeader";
		super.pageRightName = "UploadHeaderList";
		super.tableName = "UploadHeader_AView";
		super.queueTableName = "UploadHeader_TView";
		super.enquiryTableName = "UploadHeader_View";

		this.module = getArgument("module");

		if (UploadConstants.MANUAL_ADVISE_MAKER.equals(this.module)
				|| UploadConstants.MANUAL_ADVISE_APPROVER.equals(this.module)) {
			super.moduleCode = "ManualUploadHeader";
		}

		if (UploadConstants.MANUAL_ADVISE_APPROVER.equals(this.module)) {
			this.listBoxUpload.setCheckmark(true);
			this.btnDownload.setVisible(true);
			this.btnApprove.setVisible(true);
			this.btnReject.setVisible(true);
		}
		this.transactionDate.setFormat(PennantConstants.dateFormat);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		Filter[] filters = new Filter[1];

		switch (this.module) {
		case JvPostingConstants.MISCELLANEOUSPOSTING_MAKER:
			filters = new Filter[1];
			filters[0] = new Filter("Module", JvPostingConstants.MISCELLANEOUSPOSTING_MODULE, Filter.OP_EQUAL);
			this.searchObject.addFilters(filters);
			this.searchObject.addFilterOr(new Filter("NextRoleCode", null, Filter.OP_EQUAL),
					new Filter("NextRoleCode", "%MAKER%", Filter.OP_LIKE));
			break;
		case JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER:
			filters = new Filter[2];
			filters[0] = new Filter("Module", JvPostingConstants.MISCELLANEOUSPOSTING_MODULE, Filter.OP_EQUAL);
			filters[1] = new Filter("NextRoleCode", "%APPROVER%", Filter.OP_LIKE);
			this.searchObject.addFilters(filters);
			break;
		case UploadConstants.MANUAL_ADVISE_MAKER:
			filters = new Filter[1];
			filters[0] = new Filter("Module", UploadConstants.MODULE_MANUAL_ADVISE, Filter.OP_EQUAL);
			this.searchObject.addFilters(filters);
			this.searchObject.addFilterOr(new Filter("NextRoleCode", null, Filter.OP_EQUAL),
					new Filter("NextRoleCode", "%MAKER%", Filter.OP_LIKE));
			break;
		case UploadConstants.MANUAL_ADVISE_APPROVER:
			filters = new Filter[2];
			filters[0] = new Filter("Module", UploadConstants.MODULE_MANUAL_ADVISE, Filter.OP_EQUAL);
			filters[1] = new Filter("NextRoleCode", "%APPROVER%", Filter.OP_LIKE);
			this.searchObject.addFilters(filters);
			break;
		default:
			filters = new Filter[1];
			filters[0] = new Filter("Module", this.module, Filter.OP_EQUAL);
			this.searchObject.addFilters(filters);
			break;
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_UploadList(Event event) {
		// Set the page level components.
		setPageComponents(window_UploadList, borderLayout_UploadList, listBoxUpload, pagingUploadList);
		setItemRender(new UploadListModelItemRenderer(this.module));

		registerButton(button_UploadList_New, "", false);
		if (UploadConstants.MANUAL_ADVISE_MAKER.equals(this.module)) {
			registerButton(button_UploadList_New, "button_UploadList_NewAdviseUpload", true);
		}

		registerButton(button_UploadList_SearchDialog);

		registerField("UploadId", listheader_UploadList_UploadId, SortOrder.ASC, uploadId, sortOperator_UploadId,
				Operators.NUMERIC);
		registerField("FileName", listheader_UploadList_FileName, SortOrder.NONE, fileName, sortOperator_FileName,
				Operators.STRING);
		registerField("TransactionDate", listheader_UploadList_TransactionDate, SortOrder.ASC, transactionDate,
				sortOperator_TransactionDate, Operators.DATE);
		registerField("TotalRecords", listheader_UploadList_Total, SortOrder.NONE);
		registerField("SuccessCount", listheader_UploadList_Success, SortOrder.NONE);
		registerField("FailedCount", listheader_UploadList_Failed, SortOrder.NONE);
		registerField("LastMntBy", listheader_UploadList_UserName, SortOrder.NONE);
		registerField("EntityCode", listheader_UploadList_EntityCode, SortOrder.NONE, entity, sortOperator_Entity,
				Operators.STRING);
		registerField("Module");
		registerField("UserName");

		setDefaultData();
		doSetFieldProperties();

		// Render the page and display the data.
		doRenderPage();

		if (JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER.equals(this.module)) {
			button_UploadList_New.setVisible(false);
		} else if (JvPostingConstants.MISCELLANEOUSPOSTING_MAKER.equals(this.module)) {
			button_UploadList_New.setVisible(true);
		}

		search();

	}

	public void doSetFieldProperties() {
		logger.debug("Entering");
		this.entity.setMaxlength(8);
		this.entity.setTextBoxWidth(135);
		this.entity.setModuleName("Entity");
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		logger.debug("Leaving");
	}

	private void setDefaultData() {
		if (JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER.equals(this.module)) {
			this.button_UploadList_New.setVisible(false);
			this.entity.setMandatoryStyle(true);
		}

		// TODO verify this code
		if (UploadConstants.MODULE_MANUAL_ADVISE.equals(this.module)) {
			this.label_UploadList_TransactionDate.setVisible(true);
			this.sortOperator_TransactionDate.setVisible(true);
			this.transactionDate.setVisible(true);
			this.row_AlwWorkflow.setVisible(true);
			this.button_UploadList_New.setVisible(true);
			this.label_UploadList_EntityCode.setVisible(false);
			this.sortOperator_Entity.setVisible(false);
			this.entity.setVisible(false);
			this.label_UploadList_UploadId.setVisible(false);
			this.sortOperator_UploadId.setVisible(false);
			this.uploadId.setVisible(false);
			this.listheader_UploadList_TransactionDate.setVisible(true);
			this.listheader_UploadList_UploadId.setVisible(false);
			this.listheader_UploadList_EntityCode.setVisible(false);
			this.listheader_UploadList_UserName.setVisible(false);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_UploadList_SearchDialog(Event event) {
		if (JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER.equals(this.module)) {
			doSetValidations();
		}
		search();
	}

	private void doSetValidations() {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<>();
		try {
			if (!this.entity.isReadonly()) {
				this.entity.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptUploadList_EntityCode.value"), null, true, true));
				this.entity.getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.entity.setConstraint("");
		this.entity.setErrorMessage("");
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doRemoveValidation();
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_UploadList_New(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setNewRecord(true);
		uploadHeader.setWorkflowId(getWorkFlowId());
		uploadHeader.setModule(this.module);
		uploadHeader.setTransactionDate(DateUtil.getSysDate());
		uploadHeader.setMakerId(getUserWorkspace().getUserDetails().getUserId());

		// Display the dialog page.
		doShowDialogPage(uploadHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onUploadItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxUpload.getSelectedItem();
		if (selectedItem != null && selectedItem.getAttribute("id") != null) {
			// Get the selected entity.
			long id = (long) selectedItem.getAttribute("id");
			UploadHeader uploadHeader = uploadHeaderService.getUploadHeaderById(id, "_View");

			if (uploadHeader == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			if (JvPostingConstants.MISCELLANEOUSPOSTING_MAKER.equals(this.module)
					|| JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER.equals(this.module)) {
				uploadHeader.setMiscPostingUploads(
						uploadHeaderService.getMiscPostingUploadListByUploadId(uploadHeader.getUploadId()));
			}
			// Check whether the user has authority to change/view the record.
			String whereCond = " where UploadId = ?";

			if (doCheckAuthority(uploadHeader, whereCond, new Object[] { uploadHeader.getUploadId(), })) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && uploadHeader.getWorkflowId() == 0) {
					uploadHeader.setWorkflowId(getWorkFlowId());
				}

				Map<String, Object> arg = getDefaultArguments();
				arg.put("uploadHeader", uploadHeader);
				arg.put("uploadListCtrl", this);
				arg.put("module", this.module);

				String zulPath = "";

				switch (this.module) {
				case UploadConstants.UPLOAD_MODULE_REFUND:
					zulPath = "/WEB-INF/pages/Finance/Uploads/RefundUploadDialog.zul";
					break;
				case UploadConstants.UPLOAD_MODULE_ASSIGNMENT:
					zulPath = "/WEB-INF/pages/Finance/Uploads/AssignmentUploadDialog.zul";
					break;
				case JvPostingConstants.MISCELLANEOUSPOSTING_MAKER:
					uploadHeader.setModule(JvPostingConstants.MISCELLANEOUSPOSTING_MODULE);
					zulPath = "/WEB-INF/pages/Finance/Uploads/MiscPostingUploadDialog.zul";
					break;
				case JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER:
					uploadHeader.setModule(JvPostingConstants.MISCELLANEOUSPOSTING_MODULE);
					zulPath = "/WEB-INF/pages/Finance/Uploads/MiscPostingUploadDialog.zul";
					break;
				}

				try {
					Executions.createComponents(zulPath, null, arg);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param uploadHeader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(UploadHeader uploadHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("uploadHeader", uploadHeader);
		arg.put("uploadListCtrl", this);
		arg.put("module", this.module);

		String zulPath = "";

		switch (this.module) {
		case UploadConstants.UPLOAD_MODULE_REFUND:
			zulPath = "/WEB-INF/pages/Finance/Uploads/RefundUploadDialog.zul";
			break;
		case UploadConstants.UPLOAD_MODULE_ASSIGNMENT:
			zulPath = "/WEB-INF/pages/Finance/Uploads/AssignmentUploadDialog.zul";
			break;
		case JvPostingConstants.MISCELLANEOUSPOSTING_MAKER:
			uploadHeader.setModule(JvPostingConstants.MISCELLANEOUSPOSTING_MODULE);
			zulPath = "/WEB-INF/pages/Finance/Uploads/SelectMiscPostingUploadDialog.zul";
			break;
		case JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER:
			uploadHeader.setModule(JvPostingConstants.MISCELLANEOUSPOSTING_MODULE);
			zulPath = "/WEB-INF/pages/Finance/Uploads/MiscPostingUploadDialog.zul";
			break;
		case UploadConstants.MANUAL_ADVISE_MAKER:
			zulPath = "/WEB-INF/pages/Finance/UploadManualAdvise/UploadAdviseDialog.zul";
			break;
		}

		try {
			Executions.createComponents(zulPath, null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @return
	 */
	private boolean saveAdviseUploadDetails(boolean rejectSts) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		List<UploadHeader> uploadHeaderList = new ArrayList<>();

		List<Long> downloadUploadIDList = new ArrayList<>();
		List<Long> authorityUploadIDList = new ArrayList<>();

		for (Listitem listitem : this.listBoxUpload.getSelectedItems()) {
			UploadHeader uploadHeader = (UploadHeader) listitem.getAttribute("data");
			// Check Authority
			if (doCheckAuthority(uploadHeader)) {
				authorityUploadIDList.add(uploadHeader.getUploadId());
			}

			boolean isDownload = uploadHeaderService.isFileDownload(uploadHeader.getUploadId(), "_View");
			if (!isDownload) {
				downloadUploadIDList.add(uploadHeader.getUploadId());
			}
			uploadHeaderList.add(uploadHeader);
		}

		// if the file is not downloaded at least one time after maker
		if (CollectionUtils.isNotEmpty(downloadUploadIDList)) {
			MessageUtil.showError(
					"Upload id's:" + downloadUploadIDList + " is not download, please download atleast one time");
			return false;
		}
		// Check Authority
		if (CollectionUtils.isNotEmpty(authorityUploadIDList)) {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			return false;
		}
		Date appDate = SysParamUtil.getAppDate();

		for (UploadHeader uh : uploadHeaderList) {
			boolean isNew = false;
			uh = uploadHeaderService.getUploadHeaderById(uh.getUploadId(), "_View");

			UploadHeader befuploadHeader = new UploadHeader();
			BeanUtils.copyProperties(uh, befuploadHeader);
			uh.setBefImage(befuploadHeader);

			List<UploadManualAdvise> maUpload = uploadHeaderService.getManualAdviseListByUploadId(uh.getUploadId());

			for (UploadManualAdvise ma : maUpload) {
				String reason = "";
				List<String> finEvents = uploadHeaderService.getFinEventByFinRef(ma.getFinReference(), "_Temp");

				if (CollectionUtils.isNotEmpty(finEvents)) {
					if (finEvents.contains(FinServiceEvent.ADDDISB) || finEvents.contains(FinServiceEvent.RATECHG)
							|| finEvents.contains(FinServiceEvent.EARLYRPY)) {
						reason = Labels.getLabel("LOAN_SERVICE_PROCESS");
						rejectSts = true;
					} else if (finEvents.contains(FinServiceEvent.EARLYSETTLE)) {
						reason = Labels.getLabel("LOAN_EARLY_PROCESS");
						rejectSts = true;
					} else if (finEvents.contains(FinServiceEvent.CANCELFIN)) {
						reason = Labels.getLabel("LOAN_CANCEL_PROCESS");
						rejectSts = true;
					}
				}

				if (UploadConstants.UPLOAD_STATUS_SUCCESS.equals(ma.getStatus())) {
					FeeType fee = uploadHeaderService.getApprovedFeeTypeByFeeCode(ma.getFeeTypeCode());
					if (fee == null) {
						reason = reason + "Fee type doesn't exist.";
						rejectSts = true;
					} else if (!fee.isActive()) {
						reason = reason + "Fee type is Inactive State.";
						rejectSts = true;
					} else if (!fee.isManualAdvice()) {
						reason = reason + "Manual Advise not allowed for this Fee Type.";
						rejectSts = true;
					}
				}

				if (rejectSts) {
					if (StringUtils.isBlank(reason)) {
						reason = Labels.getLabel("APPROVER_REJECT_REASON");
					}
					ma.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
					ma.setReason(reason);
					ma.setRejectStage(UploadConstants.UPLOAD_APPROVER_STAGE);
				} else {
					uh.setApprovedDate(appDate);
				}
			}

			uh.setUploadManualAdvises(maUpload);

			isNew = uh.isNewRecord();
			String tranType;

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(uh.getRecordType())) {
					uh.setVersion(uh.getVersion() + 1);
					if (isNew) {
						uh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						uh.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						uh.setNewRecord(true);
					}
				}
			} else {
				uh.setVersion(uh.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			// save it to database
			try {
				doProcess(uh, tranType);
				processCompleted = true;
			} catch (Exception e) {
				MessageUtil.showError(e);
				processCompleted = false;
			}
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;

	}

	/**
	 * check authority
	 * 
	 * @param receiptidList
	 * @return
	 */

	private boolean doCheckAuthority(UploadHeader uploadHeader) {

		// Check whether the user has authority to change/view the record.
		String whereCond = " where UploadHeaderId = ?";

		if (doCheckAuthority(uploadHeader, whereCond, new Object[] { uploadHeader.getUploadId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && uploadHeader.getWorkflowId() == 0) {
				uploadHeader.setWorkflowId(getWorkFlowId());
			}
			doLoadWorkFlow(isWorkFlowEnabled(), uploadHeader.getWorkflowId(), uploadHeader.getNextTaskId());
			return false;
		}
		return true;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aUploadHeader (UploadHeader)
	 * 
	 * @param tranType      (String)
	 * 
	 * @return boolean
	 * 
	 */
	public boolean doProcess(UploadHeader aUploadHeader, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aUploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aUploadHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = aUploadHeader.getTaskId();
			String nextTaskId;
			aUploadHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			nextTaskId = StringUtils.trimToEmpty(aUploadHeader.getNextTaskId());

			nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			if ("".equals(nextTaskId)) {
				nextTaskId = getNextTaskIds(taskId, aUploadHeader);
			}

			aUploadHeader.setTaskId(taskId);
			aUploadHeader.setNextTaskId(nextTaskId);
			aUploadHeader.setRoleCode(aUploadHeader.getNextRoleCode());
			aUploadHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aUploadHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, PennantConstants.method_doApprove);

		} else {
			auditHeader = getAuditHeader(aUploadHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, PennantConstants.method_doApprove);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(UploadHeader aUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aUploadHeader.getBefImage(), aUploadHeader);
		return new AuditHeader(String.valueOf(aUploadHeader.getUploadId()), null, null, null, auditDetail,
				aUploadHeader.getUserDetails(), getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;

		if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

			aAuditHeader = uploadHeaderService.doApprove(aAuditHeader);

		} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
			aAuditHeader = uploadHeaderService.doReject(aAuditHeader);

		} else {
			aAuditHeader.setErrorDetails(
					new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
			retValue = ErrorControl.showErrorControl(this.window_UploadList, aAuditHeader);
			return processCompleted;
		}

		aAuditHeader = ErrorControl.showErrorDetails(this.window_UploadList, aAuditHeader);
		retValue = aAuditHeader.getProcessStatus();

		if (retValue == PennantConstants.porcessCONTINUE) {
			processCompleted = true;
		}

		if (retValue == PennantConstants.porcessOVERIDE) {
			aAuditHeader.setOveride(true);
			aAuditHeader.setErrorMessage(null);
			aAuditHeader.setInfoMessage(null);
			aAuditHeader.setOverideMessage(null);
		}

		setOverideMap(aAuditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);

		return processCompleted;
	}

	public void onClick$btnReject(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.listBoxUpload.getSelectedItems().isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		boolean processCompleted = true;
		if (UploadConstants.MANUAL_ADVISE_APPROVER.equals(this.module)) {
			processCompleted = saveAdviseUploadDetails(true);
		}

		if (!processCompleted) {
			return;
		}
		doReset();
		search();
		Clients.showNotification("Rejected successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnApprove(Event event) {

		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;

		if (this.listBoxUpload.getSelectedItems().isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		if (UploadConstants.MANUAL_ADVISE_APPROVER.equals(this.module)) {
			processCompleted = saveAdviseUploadDetails(false);
		}
		if (!processCompleted) {
			return;
		}

		doReset();
		search();
		Clients.showNotification("Upload request is processed successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDownload(Event event) throws IOException {
		logger.debug(Literal.ENTERING);

		if (this.listBoxUpload.getSelectedItems().isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		try {
			if (this.listBoxUpload.getSelectedCount() > 1) {

				MessageUtil.showError(Labels.getLabel("MORETHEN_FILE"));
				return;

			} else {

				Listitem listitem = listBoxUpload.getSelectedItem();
				UploadHeader uploadHeader = (UploadHeader) listitem.getAttribute("data");

				StringBuilder searchCriteriaDesc = new StringBuilder(" ");
				String whereCond = "Where FILENAME in (" + "'" + uploadHeader.getFileName() + "'"
						+ ")  And EntityCode = '" + uploadHeader.getEntityCode() + "'";

				searchCriteriaDesc.append("File Name is " + uploadHeader.getFileName());

				ReportsUtil.generateReport(getUserWorkspace().getLoggedInUser().getFullName(),
						"ManualAdviseUploadReport", whereCond, searchCriteriaDesc);

				uploadHeaderService.updateFileDownload(uploadHeader.getUploadId(), true, "_Temp");
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		doReset();
		search();
		Clients.showNotification(Labels.getLabel("label_DataExtractionList_DownloadedSuccess.value"), "info", null,
				null, -1);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}
}