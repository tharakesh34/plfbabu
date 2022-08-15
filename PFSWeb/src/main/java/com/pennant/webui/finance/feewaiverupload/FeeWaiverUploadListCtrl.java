package com.pennant.webui.finance.feewaiverupload;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
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

import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennant.backend.service.finance.FeeWaiverUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.finance.feewaiverupload.model.FeeWaiverUploadListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FeeWaiverUploadListCtrl extends GFCBaseListCtrl<FeeWaiverUploadHeader> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = LogManager.getLogger(FeeWaiverUploadListCtrl.class);

	protected Window window_FeeWaiverUploadList;
	protected Borderlayout borderLayout_FeeWaiverUploadList;
	protected Listbox listBoxFeeWaiverUpload;
	protected Paging pagingFeeWaiverUploadList;

	protected Textbox fileName;
	protected Datebox transactionDate;
	protected Intbox uploadId;

	protected Button btnReject;
	protected Button btnApprove;
	protected Button btnDownload;

	protected Label label_FeeWaiverUploadList_TransactionDate;
	protected Label label_FeeWaiverUploadList_UploadId;

	protected Listbox sortOperator_FileName;
	protected Listbox sortOperator_TransactionDate;
	protected Listbox sortOperator_UploadId;

	protected Button button_FeeWaiverUploadList_SearchDialog;

	protected Listheader listheader_FeeWaiverUploadList_UploadId;
	protected Listheader listheader_FeeWaiverUploadList_FileName;
	protected Listheader listheader_FeeWaiverUploadList_TransactionDate;
	protected Listheader listheader_FeeWaiverUploadList_Total;
	protected Listheader listheader_FeeWaiverUploadList_Success;
	protected Listheader listheader_FeeWaiverUploadList_Failed;
	protected Listheader listheader_FeeWaiverUploadList_UserName;

	private String module = "";

	private transient FeeWaiverUploadHeaderService feeWaiverUploadHeaderService;

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FeeWaiverUploadHeader";
		super.pageRightName = "FeeWaiverUploadList";
		super.tableName = "FeeWaiverUploadHeader_AView";
		super.queueTableName = "FeeWaiverUploadHeader_TView";
		super.enquiryTableName = "FeeWaiverUploadHeader_View";

		this.module = getArgument("module");

		if (UploadConstants.FEE_WAIVER_MAKER.equals(this.module)
				|| UploadConstants.FEE_WAIVER_APPROVER.equals(this.module)) {
			super.moduleCode = "FeeWaiverUploadHeader";
		}

		if (UploadConstants.FEE_WAIVER_APPROVER.equals(this.module)) {
			this.btnDownload.setVisible(true);
			this.btnApprove.setVisible(true);
			this.btnReject.setVisible(true);
			this.listBoxFeeWaiverUpload.setCheckmark(true);
		}
		this.transactionDate.setFormat(PennantConstants.dateFormat);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (UploadConstants.FEE_WAIVER_MAKER.equals(this.module)) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("Module", UploadConstants.MODULE_FEE_WAIVER, Filter.OP_EQUAL);
			this.searchObject.addFilters(filters);
			this.searchObject.addFilterOr(new Filter("NextRoleCode", null, Filter.OP_EQUAL),
					new Filter("NextRoleCode", "%MAKER%", Filter.OP_LIKE));

		} else if (UploadConstants.FEE_WAIVER_APPROVER.equals(this.module)) {
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("Module", UploadConstants.MODULE_FEE_WAIVER, Filter.OP_EQUAL);
			filters[1] = new Filter("NextRoleCode", "%APPROVER%", Filter.OP_LIKE);
			this.searchObject.addFilters(filters);
		} else {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("Module", this.module, Filter.OP_EQUAL);
			this.searchObject.addFilters(filters);
		}
	}

	public void onCreate$window_FeeWaiverUploadList(Event event) {
		setPageComponents(window_FeeWaiverUploadList, borderLayout_FeeWaiverUploadList, listBoxFeeWaiverUpload,
				pagingFeeWaiverUploadList);
		setItemRender(new FeeWaiverUploadListModelItemRenderer(this.module));

		registerButton(button_FeeWaiverUploadList_SearchDialog);

		registerField("UploadId", listheader_FeeWaiverUploadList_UploadId, SortOrder.ASC, uploadId,
				sortOperator_UploadId, Operators.NUMERIC);
		registerField("FileName", listheader_FeeWaiverUploadList_FileName, SortOrder.NONE, fileName,
				sortOperator_FileName, Operators.STRING);
		registerField("TransactionDate", listheader_FeeWaiverUploadList_TransactionDate, SortOrder.ASC, transactionDate,
				sortOperator_TransactionDate, Operators.DATE);
		registerField("TotalRecords", listheader_FeeWaiverUploadList_Total, SortOrder.NONE);
		registerField("SuccessCount", listheader_FeeWaiverUploadList_Success, SortOrder.NONE);
		registerField("FailedCount", listheader_FeeWaiverUploadList_Failed, SortOrder.NONE);
		registerField("LastMntBy", listheader_FeeWaiverUploadList_UserName, SortOrder.NONE);
		registerField("Module");
		registerField("UserName");

		setDefaultData();

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	private void setDefaultData() {

		// TODO verify this code
		if (UploadConstants.MODULE_FEE_WAIVER.equals(this.module)) {
			this.label_FeeWaiverUploadList_TransactionDate.setVisible(true);
			this.sortOperator_TransactionDate.setVisible(true);
			this.transactionDate.setVisible(true);
			this.row_AlwWorkflow.setVisible(true);
			this.label_FeeWaiverUploadList_UploadId.setVisible(false);
			this.sortOperator_UploadId.setVisible(false);
			this.uploadId.setVisible(false);
			this.listheader_FeeWaiverUploadList_TransactionDate.setVisible(true);
			this.listheader_FeeWaiverUploadList_UploadId.setVisible(false);
			this.listheader_FeeWaiverUploadList_UserName.setVisible(false);
		}
	}

	public void onClick$button_FeeWaiverUploadList_SearchDialog(Event event) {
		search();
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	public void onClick$btnReject(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.listBoxFeeWaiverUpload.getSelectedItems().isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		boolean processCompleted = true;
		if (UploadConstants.FEE_WAIVER_APPROVER.equals(this.module)) {
			processCompleted = saveWaiverUploadDetails(true);
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

		if (this.listBoxFeeWaiverUpload.getSelectedItems().isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		if (UploadConstants.FEE_WAIVER_APPROVER.equals(this.module)) {
			processCompleted = saveWaiverUploadDetails(false);
		}
		if (!processCompleted) {
			return;
		}

		doReset();
		search();
		Clients.showNotification("Upload request is processed successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	private boolean saveWaiverUploadDetails(boolean rejectSts) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		List<FeeWaiverUploadHeader> uploadHeaderList = new ArrayList<>();

		List<Long> downloadUploadIDList = new ArrayList<>();
		List<Long> authorityUploadIDList = new ArrayList<>();

		for (Listitem listitem : this.listBoxFeeWaiverUpload.getSelectedItems()) {
			FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) listitem.getAttribute("data");
			// Check Authority
			if (doCheckAuthority(uploadHeader)) {
				authorityUploadIDList.add(uploadHeader.getUploadId());
			}

			boolean isDownload = feeWaiverUploadHeaderService.isFileDownload(uploadHeader.getUploadId(), "_View");
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

		for (FeeWaiverUploadHeader uploadHeader : uploadHeaderList) {
			boolean isNew = false;
			uploadHeader = feeWaiverUploadHeaderService.getUploadHeaderById(uploadHeader.getUploadId(), "_View");

			FeeWaiverUploadHeader befuploadHeader = new FeeWaiverUploadHeader();

			BeanUtils.copyProperties(uploadHeader, befuploadHeader);

			uploadHeader.setBefImage(befuploadHeader);

			List<FeeWaiverUpload> uploadFeeWaivers = feeWaiverUploadHeaderService
					.getFeeWaiverListByUploadId(uploadHeader.getUploadId());

			for (FeeWaiverUpload uploadFeeWaiver : uploadFeeWaivers) {

				String reason = "";
				String feeTypeCode = StringUtils.trimToEmpty(uploadFeeWaiver.getFeeTypeCode());
				if (StringUtils.equals(uploadFeeWaiver.getStatus(), UploadConstants.UPLOAD_STATUS_SUCCESS)) {
					FeeType fee = feeWaiverUploadHeaderService.getApprovedFeeTypeByFeeCode(feeTypeCode);
					if (fee == null) {
						reason = reason + "Fee type doesn't exist.";
						rejectSts = true;
					} else if (!fee.isActive()) {
						reason = reason + "Fee type is Inactive State.";
						rejectSts = true;
					}
				}

				if (rejectSts) {
					if (StringUtils.isBlank(reason)) {
						reason = Labels.getLabel("APPROVER_REJECT_REASON");
					}
					uploadFeeWaiver.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
					uploadFeeWaiver.setReason(reason);
					uploadFeeWaiver.setRejectStage(UploadConstants.UPLOAD_APPROVER_STAGE);
				} else {
					uploadHeader.setApprovedDate(SysParamUtil.getAppDate());
				}
			}

			uploadHeader.setUploadFeeWaivers(uploadFeeWaivers);

			isNew = uploadHeader.isNewRecord();
			String tranType;

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(uploadHeader.getRecordType())) {
					uploadHeader.setVersion(uploadHeader.getVersion() + 1);
					if (isNew) {
						uploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						uploadHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						uploadHeader.setNewRecord(true);
					}
				}
			} else {
				uploadHeader.setVersion(uploadHeader.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			// save it to database
			try {
				doProcess(uploadHeader, tranType);
				processCompleted = true;
			} catch (Exception e) {
				MessageUtil.showError(e);
				processCompleted = false;
			}
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;

	}

	private boolean doCheckAuthority(FeeWaiverUploadHeader uploadHeader) {

		// Check whether the user has authority to change/view the record.
		String whereCond = " where UploadHeaderId=?";

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

	public boolean doProcess(FeeWaiverUploadHeader aUploadHeader, String tranType) throws Exception {
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

	private AuditHeader getAuditHeader(FeeWaiverUploadHeader aUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aUploadHeader.getBefImage(), aUploadHeader);
		return new AuditHeader(String.valueOf(aUploadHeader.getUploadId()), null, null, null, auditDetail,
				aUploadHeader.getUserDetails(), getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;

		try {
			if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

				aAuditHeader = feeWaiverUploadHeaderService.doApprove(aAuditHeader);

			} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
				aAuditHeader = feeWaiverUploadHeaderService.doReject(aAuditHeader);

			} else {
				aAuditHeader.setErrorDetails(
						new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
				retValue = ErrorControl.showErrorControl(this.window_FeeWaiverUploadList, aAuditHeader);
				return processCompleted;
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_FeeWaiverUploadList, aAuditHeader);
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

		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);

		return processCompleted;
	}

	public void onClick$btnDownload(Event event) throws IOException {
		logger.debug(Literal.ENTERING);

		if (this.listBoxFeeWaiverUpload.getSelectedItems().isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		try {
			if (this.listBoxFeeWaiverUpload.getSelectedCount() > 1) {

				MessageUtil.showError(Labels.getLabel("MORETHEN_FILE"));
				return;

			} else {

				Listitem listitem = listBoxFeeWaiverUpload.getSelectedItem();
				FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) listitem.getAttribute("data");

				StringBuilder searchCriteriaDesc = new StringBuilder(" ");
				String whereCond = "Where FILENAME in (" + "'" + uploadHeader.getFileName() + "'" + ")";

				searchCriteriaDesc.append("File Name is " + uploadHeader.getFileName());

				String usrName = getUserWorkspace().getLoggedInUser().getFullName();
				ReportsUtil.generateReport(usrName, "BulkFeeWaiverUploadReport", whereCond, searchCriteriaDesc);

				feeWaiverUploadHeaderService.updateFileDownload(uploadHeader.getUploadId(), true, "_Temp");
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

	public void onClick$print(Event event) {
		doPrintResults();
	}

	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setFeeWaiverUploadHeaderService(FeeWaiverUploadHeaderService feeWaiverUploadHeaderService) {
		this.feeWaiverUploadHeaderService = feeWaiverUploadHeaderService;
	}

}
