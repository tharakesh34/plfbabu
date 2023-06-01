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
 * * FileName : ReceiptUploadApprover.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-07-2018 * *
 * Modified Date : 18-07-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-07-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.receiptupload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Button;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/ReceiptDialog/ReceiptDialogDialog.zul file.
 */
public class ReceiptUploadApprovalDialogCtrl extends GFCBaseCtrl<ReceiptUploadHeader> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ReceiptUploadApprovalDialogCtrl.class);

	protected Window window_ReceiptUploadApproval; // autoWired

	private ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
	private ReceiptUploadHeaderService receiptUploadHeaderService;

	protected ExtendedCombobox entity;
	protected Textbox fileName;

	protected Button btnFileName;
	protected Button btnRefresh;
	protected Button btndownload;
	protected Button btnApprove;
	protected Button btnReject;
	protected Space spaceID;

	/**
	 * default constructor.<br>
	 */
	public ReceiptUploadApprovalDialogCtrl() {
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
	public void onCreate$window_ReceiptUploadApproval(Event event) {

		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(this.window_ReceiptUploadApproval);

		try {

			// Store the before image.
			ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
			BeanUtils.copyProperties(this.receiptUploadHeader, receiptUploadHeader);
			this.receiptUploadHeader.setBefImage(receiptUploadHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.receiptUploadHeader.isWorkflow(), this.receiptUploadHeader.getWorkflowId(),
					this.receiptUploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.receiptUploadHeader);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.entity.setModuleName("Entity");
		this.entity.setMandatoryStyle(true);
		this.entity.setDisplayStyle(2);
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		this.btnFileName.setDisabled(true);

		logger.debug("Leaving");
	}

	public void onFulfill$entity(Event event) {

		logger.debug("Entering" + event.toString());

		Object dataObject = entity.getObject();

		if (dataObject instanceof String || dataObject == null) {
			this.entity.setValue("");
			this.entity.setDescription("");
			this.btnFileName.setDisabled(true);
			this.spaceID.setSclass("");
		} else {
			Entity details = (Entity) dataObject;
			if (details != null) {
				this.btnFileName.setDisabled(false);
				this.spaceID.setSclass("mandatory");
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btndownload(Event event) throws IOException {
		logger.debug(Literal.ENTERING);
		doWriteComponentsToBean(this.receiptUploadHeader);

		List<String> listUploadId = Arrays.asList(receiptUploadHeader.getFileName().split(","));

		if (listUploadId.size() == 1) {
			for (String id : listUploadId) {
				byte[] byteArray = getExcelData(id);
				Filedownload.save(new AMedia(id, "xls", "application/vnd.ms-excel", byteArray));
			}
		} else if (listUploadId.size() > 1) {

			try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();) {
				try (ZipOutputStream out = new ZipOutputStream(arrayOutputStream);) {
					for (String id : listUploadId) {
						out.putNextEntry(new ZipEntry(id + ".xls"));
						out.write(getExcelData(id));
						out.closeEntry();
					}

					String zipfileName = "ReceiptUpload.zip";

					byte[] tobytes = arrayOutputStream.toByteArray();
					Filedownload.save(new AMedia(zipfileName, "zip", "application/*", tobytes));
				}
			}
		}

		clearData();
		Clients.showNotification("Data downloaded successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	private byte[] getExcelData(String id) {
		logger.debug(Literal.ENTERING);

		String whereCond = " where receiptUploadId in (" + "'" + id + "'" + ") and  UploadStatus in (" + "'"
				+ ReceiptDetailStatus.SUCCESS.getValue() + "'" + ")";
		StringBuilder searchCriteria = new StringBuilder(" ");

		String reportPath = PathUtil.REPORTS_ORGANIZATION;
		String reportName = "ReceiptUploadApprover";
		String userName = getUserWorkspace().getLoggedInUser().getFullName();

		logger.debug(Literal.LEAVING);
		return ReportsUtil.getExcelData(reportPath, reportName, userName, whereCond, searchCriteria);
	}

	public void onClick$btnApprove(Event event) {

		logger.debug(Literal.ENTERING);
		doWriteComponentsToBean(this.receiptUploadHeader);

		List<String> listUploadId = Arrays.asList(receiptUploadHeader.getFileName().split(","));

		boolean isFileDownloaded = checkFileDownloaded(listUploadId);

		if (isFileDownloaded) {
			return;
		}

		// check whether Connection established
		doConnectionEstablished();

		for (String id : listUploadId) {
			// ReceiptUploadHeader receiptUploadHeader =
			// receiptUploadHeaderService.getUploadHeaderById(Long.valueOf(id));

			doApprove(receiptUploadHeader);

			// this.receiptUploadHeaderService.updateUploadProgress(id, PennantConstants.RECEIPT_APPROVED);
		}

		clearData();

		Clients.showNotification("Approved successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	private void doApprove(ReceiptUploadHeader aReceiptUploadHeader) {

		// Call Api
		List<ReceiptUploadDetail> receiptUploadDetailList = callApi(aReceiptUploadHeader);

		int sucessCount = 0;
		int failedCount = 0;
		if (receiptUploadDetailList != null && !receiptUploadDetailList.isEmpty()) {
			for (ReceiptUploadDetail receiptUploadDetail : receiptUploadDetailList) {
				if (ReceiptDetailStatus.SUCCESS.getValue() == receiptUploadDetail.getProcessingStatus()) {
					sucessCount = sucessCount + 1;
				} else {
					failedCount = failedCount + 1;
				}
			}
		}

		// Update Details
		// this.receiptUploadHeaderService.updateStatusOFList(receiptUploadDetailList);

		// this.receiptUploadHeaderService.uploadHeaderStatusCnt(aReceiptUploadHeader.getReceiptUploadId(),
		// sucessCount,failedCount);

	}

	private boolean checkFileDownloaded(List<String> listUploadId) {

		for (String id : listUploadId) {

			// boolean isDownloaded = this.receiptUploadHeaderService.isFileDownloaded(id,
			// PennantConstants.RECEIPT_DOWNLOADED);
			/*
			 * if (!isDownloaded) { MessageUtil.showError("Upload id:" + id + " is not download atleast one time");
			 * return true; }
			 */
		}
		return false;
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug("Entering");

		clearData();

		logger.debug("Leaving");
	}

	private void clearData() {
		doRemoveValidation();
		doClearMessage();
		doClear();
		this.btnFileName.setDisabled(true);
	}

	public void onClick$btnReject(Event event) {

		logger.debug(Literal.ENTERING);
		doWriteComponentsToBean(this.receiptUploadHeader);

		List<String> listUploadId = Arrays.asList(receiptUploadHeader.getFileName().split(","));

		boolean isFileDownloaded = checkFileDownloaded(listUploadId);
		if (isFileDownloaded) {
			return;
		}

		for (String id : listUploadId) {
			// this.receiptUploadHeaderService.updateUploadProgress(id, PennantConstants.RECEIPT_REJECTED);
			this.receiptUploadHeaderService.updateRejectStatusById(id, Labels.getLabel("receiptUpload_Reject"));
		}

		clearData();
		Clients.showNotification("Rejected successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnFileName(Event event) {
		logger.debug("Entering");

		this.fileName.setConstraint("");
		String selectedValues = null;

		List<Integer> list = new ArrayList<>();
		list.add(PennantConstants.RECEIPT_DOWNLOADED);
		list.add(PennantConstants.RECEIPT_DEFAULT);

		Filter[] filter = new Filter[2];
		filter[0] = new Filter("EntityCODE", this.entity.getValidatedValue(), Filter.OP_EQUAL);
		filter[1] = Filter.in("UploadProgress", list);

		selectedValues = (String) MultiSelectionSearchListBox.show(this.window_ReceiptUploadApproval,
				"ReceiptUploadHeader", this.fileName.getValue(), filter);

		if (selectedValues != null) {

			this.fileName.setValue(selectedValues);
			if (StringUtils.isNotEmpty(selectedValues)) {
				List<String> mandVasList = Arrays.asList(this.fileName.getValue().split(","));
				List<String> addList = new ArrayList<>();
				for (int i = 0; i < mandVasList.size(); i++) {
					if (!("," + this.fileName.getValue() + ",").contains("," + mandVasList.get(i) + ",")) {
						addList.add(mandVasList.get(i));
					}
				}

				if (!addList.isEmpty()) {
					String fileName = this.fileName.getValue();
					for (int i = 0; i < addList.size(); i++) {
						if (StringUtils.isEmpty(fileName)) {
							fileName = addList.get(i);
						} else {
							fileName = fileName + "," + addList.get(i);
						}
					}
					this.fileName.setValue(fileName);
				}
			}
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

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ReceiptUploadApproval);
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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReceiptUploadheader ReceiptDialog
	 */
	public void doWriteBeanToComponents(ReceiptUploadHeader aReceiptUploadheader) {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReceiptUploadHeader
	 */
	public void doWriteComponentsToBean(ReceiptUploadHeader aReceiptUploadHeader) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		doRemoveValidation();
		try {
			if (!this.entity.isReadonly() && this.entity.getValue() != null) {
				this.entity.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptUpload_entity.value"),
						PennantRegularExpressions.REGEX_ALPHANUM, true));
				this.receiptUploadHeader.setEntityCode(this.entity.getValue());
				this.receiptUploadHeader.setEntityCodeDesc(this.entity.getDescription());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.btnFileName.isDisabled()) {
				if (StringUtils.trimToNull(this.fileName.getValue()) == null) {
					throw new WrongValueException(this.fileName, Labels.getLabel("empty_file"));
				}

				this.receiptUploadHeader.setFileName(this.fileName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
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
			this.fileName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.fileName.focus();
				if (StringUtils.isNotBlank(aReceiptUploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aReceiptUploadHeader);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReceiptUploadApproval.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.fileName.setConstraint("");
		this.entity.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fileName.setErrorMessage("");
		this.entity.setErrorMessage("");
		logger.debug("Leaving");
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
		this.fileName.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.receiptUploadHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.entity.setValue("");
		this.entity.setDescription("");
		this.fileName.setValue("");
		logger.debug("Leaving");
	}

	private void doConnectionEstablished() {
		// connection

		WebClient client = null;
		Response response = null;

		// URLAuthorization
		String authorization = SysParamUtil.getValueAsString("URLAuthorization");

		try {
			String url = SysParamUtil.getValueAsString("RECEIPTAPIURL");
			client = getClient(url, "1231212", authorization);
			response = client.get();
		} catch (Exception e) {
			client.close();
			client = null;
			MessageUtil.showError("Connection not established, Please check URl and Authorization");
			return;
		} finally {
			client.close();
			client = null;
		}

		if (response.getStatus() == 404) {
			MessageUtil.showError("Connection not established, Please check URl and Authorization");
			return;
		}

	}

	private List<ReceiptUploadDetail> callApi(ReceiptUploadHeader aReceiptUploadHeader) {
		logger.debug(Literal.ENTERING);

		List<ReceiptUploadDetail> receiptUploadList = new ArrayList<>();
		for (ReceiptUploadDetail receiptUploadDetail : aReceiptUploadHeader.getReceiptUploadList()) {

			if (receiptUploadDetail.getProcessingStatus() == ReceiptDetailStatus.FAILED.getValue()) {
				receiptUploadList.add(receiptUploadDetail);
				continue;
			}

			ReceiptUploadDetail UploadDetail = new ReceiptUploadDetail();
			// logger.debug("API REQUEST :: " + receiptUploadDetail.getJsonObject());
			String ReturnText = null;
			String ReturnCode = null;
			WebClient client = null;
			String extraHeaderValue = null;
			String[] responseArray = new String[4];
			String headerMessageId = null;
			String authorization = null;
			String extraHeader = null;
			JSONObject reqJson = new JSONObject("");

			String messageId = String.valueOf(Calendar.getInstance().getTimeInMillis());
			try {

				String url = SysParamUtil.getValueAsString("RECEIPTAPIURL");
				// URLAuthorization

				authorization = SysParamUtil.getValueAsString("URLAuthorization");

				if (reqJson.getString("receiptPurpose").equalsIgnoreCase("ES")) {
					url = url + "finInstructionRest/loanInstructionService/earlySettlement";
				} else if (reqJson.getString("receiptPurpose").equalsIgnoreCase("SP")) {
					url = url + "finInstructionRest/loanInstructionService/manualPayment";
				} else if (reqJson.getString("receiptPurpose").equalsIgnoreCase("EP")) {
					url = url + "finInstructionRest/loanInstructionService/partialSettlement";
				} else {
					url = " ";
				}

				reqJson.remove("reqType");
				reqJson.put("reqType", "Post");
				reqJson.put("UploadDetailId", receiptUploadDetail.getUploadDetailId());

				if (!StringUtils.isBlank(url)) {
					client = getClient(url, messageId, authorization);
					Response response = client.post(reqJson.toString());
					String body = response.readEntity(String.class);
					if (headerMessageId == null && StringUtils.isBlank(body)) {
						throw new RuntimeException(BatchUploadProcessorConstatnt.UNABLE_TO_PROCESS);
					}
					logger.info("MESSAGEID :: " + headerMessageId + "  API RESPONSE :: " + body);

					if (response.getStatus() == 200 && body != null) {
						JSONObject parentBody = new JSONObject(body);
						if (StringUtils.isNotBlank(extraHeader)) {
							if (!parentBody.isNull(BatchUploadProcessorConstatnt.FIN_REFERENCE)) {
								extraHeaderValue = String
										.valueOf(parentBody.get(BatchUploadProcessorConstatnt.FIN_REFERENCE));
							} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.MANDATE_ID)) {
								extraHeaderValue = String
										.valueOf(parentBody.get(BatchUploadProcessorConstatnt.MANDATE_ID));
							} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.WORKFLOW_DESIGN_ID)) {
								extraHeaderValue = String
										.valueOf(parentBody.get(BatchUploadProcessorConstatnt.WORKFLOW_DESIGN_ID));
							} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.LIMIT_Id)) {
								extraHeaderValue = String
										.valueOf(parentBody.getString(BatchUploadProcessorConstatnt.LIMIT_Id));
							}
						}
						parentBody = parentBody.getJSONObject(BatchUploadProcessorConstatnt.RETURN_STATUS);
						ReturnText = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_TEXT);
						ReturnCode = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_CODE);

					}
				} else {
					ReturnText = Labels.getLabel("inValid_ReceiptPurpose");
					ReturnCode = PennantConstants.ERR_9999;
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				client.close();
				client = null;
			}
			responseArray[0] = ReturnCode;
			responseArray[1] = ReturnText;
			responseArray[2] = extraHeaderValue;
			responseArray[3] = headerMessageId;

			UploadDetail.setUploadDetailId(receiptUploadDetail.getUploadDetailId());
			UploadDetail.setUploadheaderId(receiptUploadDetail.getUploadheaderId());

			if (StringUtils.equals("0000", ReturnCode)) {
				UploadDetail.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
				UploadDetail.setReason("");
			} else {
				UploadDetail.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
				UploadDetail.setReason(ReturnText);
				UploadDetail.setReceiptId(null);
			}
			receiptUploadList.add(UploadDetail);
		}

		logger.debug(Literal.LEAVING);
		return receiptUploadList;

	}

	private WebClient getClient(String serviceEndPoint, String messageId, String authorization) {
		WebClient client = null;
		try {
			client = WebClient.create(serviceEndPoint);
			client.accept(MediaType.APPLICATION_JSON);
			client.type(MediaType.APPLICATION_JSON);
			client.header(BatchUploadProcessorConstatnt.AUTHORIZATION_KEY, authorization);
			client.header(BatchUploadProcessorConstatnt.MESSAGE_ID, messageId);

		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
		return client;
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

	public void setReceiptUploadHeader(ReceiptUploadHeader receiptUploadHeader) {
		this.receiptUploadHeader = receiptUploadHeader;
	}

	public ReceiptUploadHeader getReceiptUploadHeader() {
		return receiptUploadHeader;
	}
}
