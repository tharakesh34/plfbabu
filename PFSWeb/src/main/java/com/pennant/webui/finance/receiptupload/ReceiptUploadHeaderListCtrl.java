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
 * FileName    		:  UploadHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.receiptupload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.receiptUpload.ReceiptUploadHeaderProcess;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.receipt.upload.ReceiptUploadApprovalProcess;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/UploadHeader/UploadHeaderList.zul file.
 */
public class ReceiptUploadHeaderListCtrl extends GFCBaseListCtrl<ReceiptUploadHeader> {
	private static final long serialVersionUID = 1817958653208633892L;
	private static final Logger logger = LogManager.getLogger(ReceiptUploadHeaderListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReceiptUploadList;
	protected Borderlayout borderLayout_ReceiptUploadList;
	protected Paging pagingReceiptUploadList;
	protected Listbox listBoxReceiptUpload;

	protected Intbox uploadId;
	protected Textbox fileName;

	protected Listbox sortOperator_uploadId;
	protected Listbox sortOperator_fileName;

	// List headers
	protected Listheader listheader_UploadID;
	protected Listheader listheader_FileName;
	protected Listheader listheader_Success;
	protected Listheader listheader_FailedCount;
	protected Listheader listheader_TotalCount;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Listbox sortOperator_entityCode;

	protected Checkbox list_CheckBox;

	protected Button btnReject;
	protected Button btnApprove;
	protected Button btndownload;

	protected Row row0;
	protected Row row1;
	protected Row row_AlwWorkflow;
	protected ExtendedCombobox entityCode;
	private Map<Long, String> receiptIdMap = new HashMap<>();

	//module
	boolean isApprovalMenu = false;

	// checkRights
	protected Button button_ReceiptUploadList_NewReceiptUpload;
	protected Button button_ReceiptUploadList_ReceiptUploadSearchDialog;

	private transient ReceiptUploadHeaderService receiptUploadHeaderService;
	private ReceiptUploadHeaderProcess recptUploadProcess = null;

	private ReceiptUploadApprovalProcess receiptUploadApprovalProcess;

	/**
	 * default constructor.<br>
	 */
	public ReceiptUploadHeaderListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		if (isApprovalMenu) {
			this.searchObject.addWhereClause(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		} else {
			this.searchObject.addWhereClause(" RECORDSTATUS not in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		}
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReceiptUploadHeader";
		super.pageRightName = "UploadHeaderList";
		super.tableName = "ReceiptUploadHeader_AView";
		super.queueTableName = "ReceiptUploadHeader_View";

		if (StringUtils.equals(getArgument("approval"), "Y")) {
			this.isApprovalMenu = true;
		} else {
			this.isApprovalMenu = false;
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReceiptUploadList(Event event) {
		// Set the page level components.
		setPageComponents(window_ReceiptUploadList, borderLayout_ReceiptUploadList, listBoxReceiptUpload,
				pagingReceiptUploadList);
		setItemRender(new ReceiptUploadListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ReceiptUploadList_NewReceiptUpload, "button_ReceiptUploadList_NewReceiptUpload", true);
		registerButton(button_ReceiptUploadList_ReceiptUploadSearchDialog);

		registerField("uploadHeaderId", listheader_UploadID, SortOrder.NONE, uploadId, sortOperator_uploadId,
				Operators.NUMERIC);
		registerField("fileName", listheader_FileName, SortOrder.NONE, fileName, sortOperator_fileName,
				Operators.STRING);
		registerField("successCount", listheader_Success, SortOrder.NONE);
		registerField("failedCount", listheader_FailedCount, SortOrder.NONE);
		registerField("totalRecords", listheader_TotalCount, SortOrder.NONE);
		registerField("entityCode", entityCode, SortOrder.NONE, sortOperator_entityCode, Operators.STRING);

		// Render the page and display the data.
		doRenderPage();

		if (isApprovalMenu) {

			this.receiptIdMap.clear();
			doSetFieldProperties();
			if (listBoxReceiptUpload.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
			}
		} else {

			// rendering the list page data required or not.
			if (renderListOnLoad) {
				search();
			}
		}

	}

	private void doSetFieldProperties() {

		//button visible
		this.row_AlwWorkflow.setVisible(false);
		this.btnApprove.setVisible(true);
		this.btndownload.setVisible(true);
		this.btnReject.setVisible(true);
		this.print.setVisible(false);
		this.listHeader_CheckBox_Name.setVisible(true);
		this.button_ReceiptUploadList_NewReceiptUpload.setVisible(false);
		this.row0.setVisible(true);
		this.row1.setVisible(true);
		this.listheader_UploadID.setVisible(true);

		listItem_Checkbox = new Listitem();
		listCell_Checkbox = new Listcell();
		listHeader_CheckBox_Comp = new Checkbox();
		listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
		listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
		listItem_Checkbox.appendChild(listCell_Checkbox);

		if (listHeader_CheckBox_Name.getChildren() != null) {
			listHeader_CheckBox_Name.getChildren().clear();
		}
		listHeader_CheckBox_Name.appendChild(listHeader_CheckBox_Comp);

		this.entityCode.setMaxlength(8);
		this.entityCode.setTextBoxWidth(135);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

	}

	/**
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		for (int i = 0; i < listBoxReceiptUpload.getItems().size(); i++) {
			Listitem listitem = listBoxReceiptUpload.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked()) {
			List<Long> mandateIdList = getReceiptUploadHeaderList();
			if (mandateIdList != null) {
				for (Long mandateId : mandateIdList) {
					receiptIdMap.put(mandateId, null);
				}
			}
		} else {
			receiptIdMap.clear();
		}

		logger.debug("Leaving");
	}

	/**
	 * Getting the mandate list using JdbcSearchObject with search criteria..
	 */
	private List<Long> getReceiptUploadHeaderList() {

		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();
		searchObject.addField("uploadHeaderId");
		searchObject.addTabelName(this.queueTableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}

		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		searchObject.addWhereClause(whereClause.toString());

		List<Map<String, Long>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		List<Long> receiptUploadHeaderList = new ArrayList<Long>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Long> map = (Map<String, Long>) list.get(i);
				receiptUploadHeaderList.add(Long.parseLong(String.valueOf(map.get("uploadHeaderId"))));
			}
		}
		return receiptUploadHeaderList;
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ReceiptUploadList_ReceiptUploadSearchDialog(Event event) {

		if (isApprovalMenu) {
			this.receiptIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			this.receiptIdMap.clear();

			doSetValidations();
			search();

			if (listBoxReceiptUpload.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
				listBoxReceiptUpload.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			}
		} else {
			search();
		}

	}

	private void doSetValidations() {

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.entityCode.isReadonly())
				this.entityCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ReceiptUploadList_EntityCode.value"), null, true, true));
			this.entityCode.getValue();
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
	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.entityCode.setConstraint("");
		this.entityCode.setErrorMessage("");
		logger.debug("Leaving ");

	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doRefresh();
	}

	private void doRefresh() {
		if (isApprovalMenu) {
			doReset();
			doRemoveValidation();
			this.receiptIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			this.listbox.getItems().clear();
			this.entityCode.setValue(null);
			this.uploadId.setValue(null);
			this.fileName.setValue(null);
			this.entityCode.setValue("");
			this.entityCode.setDescColumn("");

			if (listBoxReceiptUpload.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
				listBoxReceiptUpload.setEmptyMessage("");

			}
			this.pagingReceiptUploadList.setTotalSize(0);

		} else {

			doReset();
			search();
		}
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ReceiptUploadList_NewReceiptUpload(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
		receiptUploadHeader.setNewRecord(true);
		receiptUploadHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.

		Map<String, Object> arg = getDefaultArguments();
		arg.put("receiptUploadHeader", receiptUploadHeader);
		arg.put("receiptUploadListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/SelectReceiptUploadHeaderDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onReceiptUploadItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReceiptUpload.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		ReceiptUploadHeader receiptUploadHeader = receiptUploadHeaderService.getUploadHeaderById(id, false);

		if (receiptUploadHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (StringUtils.equals(receiptUploadHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where UploadHeaderId=?";

		if (doCheckAuthority(receiptUploadHeader, whereCond,
				new Object[] { receiptUploadHeader.getUploadHeaderId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && receiptUploadHeader.getWorkflowId() == 0) {
				receiptUploadHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(receiptUploadHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param receiptUploadHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ReceiptUploadHeader receiptUploadHeader) {
		logger.debug("Entering");

		Map<String, Object> aruments = new HashMap<String, Object>();

		boolean enqiury = false;
		if (StringUtils.equals(receiptUploadHeader.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
			enqiury = true;
		}

		aruments.put("enqModule", enqiury);
		aruments.put("uploadReceiptHeader", receiptUploadHeader);
		aruments.put("receiptUploadListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {

			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/ReceiptUploadHeaderDialog.zul", null,
					aruments);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	public class ReceiptUploadListModelItemRenderer implements ListitemRenderer<ReceiptUploadHeader>, Serializable {

		private static final long serialVersionUID = 6352065299727172054L;

		public ReceiptUploadListModelItemRenderer() {

		}

		@Override
		public void render(Listitem item, ReceiptUploadHeader receiptUploadHeader, int count) throws Exception {

			Listcell lc;

			if (isApprovalMenu) {
				lc = new Listcell();
				list_CheckBox = new Checkbox();
				list_CheckBox.setValue(receiptUploadHeader.getUploadHeaderId());
				list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
				lc.appendChild(list_CheckBox);
				if (listHeader_CheckBox_Comp.isChecked()) {
					list_CheckBox.setChecked(true);
				} else {
					list_CheckBox.setChecked(receiptIdMap.containsKey(receiptUploadHeader.getUploadHeaderId()));
				}
				lc.setParent(item);
				lc = new Listcell(String.valueOf(receiptUploadHeader.getUploadHeaderId()));
				lc.setParent(item);
			} else {
				lc = new Listcell(String.valueOf(receiptUploadHeader.getUploadHeaderId()));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(receiptUploadHeader.getUploadHeaderId()));
				lc.setParent(item);
			}

			lc = new Listcell(receiptUploadHeader.getFileName());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(receiptUploadHeader.getSuccessCount()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(receiptUploadHeader.getFailedCount()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(receiptUploadHeader.getTotalRecords()));
			lc.setParent(item);
			lc = new Listcell(receiptUploadHeader.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(receiptUploadHeader.getRecordType()));
			lc.setParent(item);

			item.setAttribute("id", receiptUploadHeader.getUploadHeaderId());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onReceiptUploadItemDoubleClicked");
		}
	}

	/**
	 * Filling the receipt Id List details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		if (checkBox.isChecked()) {
			receiptIdMap.put(Long.valueOf(checkBox.getValue().toString()), checkBox.getValue().toString());
		} else {
			receiptIdMap.remove(Long.valueOf(checkBox.getValue().toString()));
		}

		if (receiptIdMap.size() == this.pagingReceiptUploadList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug("Leaving");
	}

	public void onClick$btndownload(Event event) throws IOException {
		logger.debug(Literal.ENTERING);

		List<Long> receiptidList = getListofReceiptUpload();

		if (receiptidList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		List<ReceiptUploadHeader> listReceiptUploadHeader = doCheckAuthority(receiptidList, false);

		if (listReceiptUploadHeader.isEmpty()) {
			return;
		}

		if (receiptidList.size() > 1) {
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(arrayOutputStream);

			for (long id : receiptidList) {
				ByteArrayOutputStream outputStream = null;
				outputStream = doDownloadFiles(String.valueOf(id));
				out.putNextEntry(new ZipEntry(id + ".xls"));
				out.write(outputStream.toByteArray());
				out.closeEntry();

				this.receiptUploadHeaderService.updateUploadProgress(id, ReceiptUploadConstants.RECEIPT_DOWNLOADED);
			}
			out.close();
			String zipfileName = "ReceiptUpload.zip";

			byte[] tobytes = arrayOutputStream.toByteArray();
			arrayOutputStream.close();
			arrayOutputStream = null;

			Filedownload.save(new AMedia(zipfileName, "zip", "application/*", tobytes));
		} else {
			for (long id : receiptidList) {
				ByteArrayOutputStream outputStream = null;
				outputStream = doDownloadFiles(String.valueOf(id));
				Filedownload.save(
						new AMedia(String.valueOf(id), "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
				this.receiptUploadHeaderService.updateUploadProgress(id, ReceiptUploadConstants.RECEIPT_DOWNLOADED);
			}
		}

		doRefresh();
		Clients.showNotification(Labels.getLabel("label_DataExtractionList_DownloadedSuccess.value"), "info", null,
				null, -1);

		logger.debug(Literal.LEAVING);
	}

	private List<Long> getListofReceiptUpload() {

		List<Long> receiptidList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			receiptidList = getReceiptUploadHeaderList();
		} else {
			receiptidList = new ArrayList<Long>(receiptIdMap.keySet());
		}
		return receiptidList;
	}

	public void onClick$btnApprove(Event event) {

		logger.debug(Literal.ENTERING);

		List<Long> receiptidList = getListofReceiptUpload();

		if (receiptidList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		//boolean isFileDownloaded = checkFileDownloaded(receiptidList);

		//FIXME: PV temporary disabled
		//		if (isFileDownloaded) {
		//		return;
		//}

		//check whether Connection established 
		//	if(!doConnectionEstablished()){
		//	return;
		//}

		//sorting
		Collections.sort(receiptidList);

		List<ReceiptUploadHeader> listReceiptUploadHeader = doCheckAuthority(receiptidList, true);

		if (listReceiptUploadHeader.isEmpty()) {
			return;
		}

		List<Long> listReceiptUploadId = new ArrayList<>();
		for (ReceiptUploadHeader receiptUploadHeader : listReceiptUploadHeader) {

			listReceiptUploadId.add(receiptUploadHeader.getUploadHeaderId());

			receiptUploadHeader.setUploadProgress(ReceiptUploadConstants.RECEIPT_APPROVED);

			//call dosaveProgress
			doProcess(receiptUploadHeader, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_APPROVED);
		}
		doApprove(listReceiptUploadId);

		doRefresh();
		Clients.showNotification("Receipt Process initialized.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * check authority
	 * 
	 * @param receiptidList
	 * @return
	 */
	private List<ReceiptUploadHeader> doCheckAuthority(List<Long> receiptidList, boolean getSucessRecords) {

		List<ReceiptUploadHeader> receiptUploadHeaderList = new ArrayList<>();

		for (long id : receiptidList) {
			ReceiptUploadHeader receiptUploadHeader = receiptUploadHeaderService.getUploadHeaderById(id, false);

			// Check whether the user has authority to change/view the record.
			String whereCond = " UploadHeaderId= ?";

			if (doCheckAuthority(receiptUploadHeader, whereCond,
					new Object[] { receiptUploadHeader.getUploadHeaderId() })) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && receiptUploadHeader.getWorkflowId() == 0) {
					receiptUploadHeader.setWorkflowId(getWorkFlowId());
				}
				doLoadWorkFlow(isWorkFlowEnabled(), receiptUploadHeader.getWorkflowId(),
						receiptUploadHeader.getNextTaskId());
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return new ArrayList<ReceiptUploadHeader>();

			}
			receiptUploadHeaderList.add(receiptUploadHeader);
		}

		return receiptUploadHeaderList;
	}

	private boolean checkFileDownloaded(List<Long> listUploadId) {

		for (long id : listUploadId) {

			boolean isDownloaded = this.receiptUploadHeaderService.isFileDownloaded(id,
					ReceiptUploadConstants.RECEIPT_DOWNLOADED);

			if (!isDownloaded) {
				MessageUtil.showError("Upload id:" + id + " is not download atleast one time");
				return true;
			}
		}
		return false;
	}

	private void doApprove(List<Long> listReceiptUploadId) {

		try {

			Thread thread = new Thread(new ReceiptUploadThread(listReceiptUploadId));
			thread.start();
			Thread.sleep(1000);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public class ReceiptUploadThread implements Runnable {
		List<Long> listReceiptUploadId;

		public ReceiptUploadThread(List<Long> listReceiptUploadId) {
			super();
			this.listReceiptUploadId = listReceiptUploadId;
		}

		@Override
		public void run() {
			receiptUploadApprovalProcess.approveReceipts(listReceiptUploadId, getUserWorkspace().getLoggedInUser());

			for (Long headerId : listReceiptUploadId) {
				int[] statuscount = receiptUploadHeaderService.getHeaderStatusCnt(headerId);
				receiptUploadHeaderService.uploadHeaderStatusCnt(headerId, statuscount[0], statuscount[1]);
			}

		}

	}

	public void onClick$btnReject(Event event) {

		logger.debug(Literal.ENTERING);
		List<Long> receiptidList = getListofReceiptUpload();

		if (receiptidList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		boolean isFileDownloaded = checkFileDownloaded(receiptidList);
		if (isFileDownloaded) {
			return;
		}

		List<ReceiptUploadHeader> listReceiptUploadHeader = doCheckAuthority(receiptidList, false);

		if (listReceiptUploadHeader.isEmpty()) {
			return;
		}

		for (ReceiptUploadHeader receiptUploadHeader : listReceiptUploadHeader) {
			doProcess(receiptUploadHeader, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_REJECTED);
		}

		doRefresh();
		Clients.showNotification("Rejected successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * download excel files based on upload ids
	 * 
	 * @param receiptUploadHeader
	 */
	private ByteArrayOutputStream doDownloadFiles(String id) {
		logger.debug(Literal.ENTERING);

		String whereCond = " where t.uploadHeaderId in (" + "'" + id + "'" + ") and  UploadStatus in (" + "'"
				+ PennantConstants.UPLOAD_STATUS_SUCCESS + "'" + ")";
		StringBuilder searchCriteria = new StringBuilder(" ");

		String reportName = "";

		reportName = "ReceiptUploadApprover";
		ByteArrayOutputStream outputStream = null;
		outputStream = generateReport(getUserWorkspace().getLoggedInUser().getFullName(), reportName, whereCond,
				searchCriteria, this.window_ReceiptUploadList, true, id);

		logger.debug(Literal.LEAVING);
		return outputStream;
	}

	/**
	 * Method For generating Report based upon passing Data
	 * 
	 * @param reportName
	 * @param userName
	 * @param whereCond
	 * @param searchCriteriaDesc
	 * @param dialogWindow
	 * @param createExcel
	 * @throws JRException
	 * @throws InterruptedException
	 */
	public ByteArrayOutputStream generateReport(String userName, String reportName, String whereCond,
			StringBuilder searchCriteriaDesc, Window window, boolean createExcel, String id) {
		logger.debug("Entering");

		Connection connection = null;
		DataSource dataSourceObj = null;
		ByteArrayOutputStream outputStream = null;
		try {

			dataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
			connection = dataSourceObj.getConnection();

			HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(5);
			reportArgumentsMap.put("userName", userName);
			reportArgumentsMap.put("reportHeading", reportName);
			reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
			reportArgumentsMap.put("appDate", DateUtility.getAppDate());
			reportArgumentsMap.put("appCcy", SysParamUtil.getAppCurrency());
			reportArgumentsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
			reportArgumentsMap.put("unitParam", "Pff");
			reportArgumentsMap.put("whereCondition", whereCond);
			reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
			reportArgumentsMap.put("bankName", Labels.getLabel("label_ClientName"));
			reportArgumentsMap.put("searchCriteria", searchCriteriaDesc.toString());
			String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/" + reportName + ".jasper";

			Connection con = null;
			DataSource reportDataSourceObj = null;

			try {
				File file = new File(reportSrc);
				if (file.exists()) {

					logger.debug("Buffer started");

					reportDataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
					con = reportDataSourceObj.getConnection();

					String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);

					JRXlsExporter excelExporter = new JRXlsExporter();
					excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printfileName);
					excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
							Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
					excelExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, id);

					outputStream = new ByteArrayOutputStream();
					excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);

					excelExporter.exportReport();
				}
			} catch (JRException e) {
				logger.error(e.getMessage());
			}
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			connection = null;
			dataSourceObj = null;
		}

		logger.debug("Leaving");
		return outputStream;
	}

	private boolean doConnectionEstablished() {
		//connection 

		WebClient client = null;
		Response response = null;

		try {
			String url = SysParamUtil.getValueAsString(ReceiptUploadConstants.RU_API_URL);
			recptUploadProcess = new ReceiptUploadHeaderProcess();
			client = recptUploadProcess.getClient(url, String.valueOf(Math.random()));
			response = client.get();
		} catch (Exception e) {
			client.close();
			client = null;
			MessageUtil.showError("Connection not established, Please check URl and Authorization");
			return false;
		} finally {
			client.close();
			client = null;
		}

		if (response.getStatus() == 404) {
			MessageUtil.showError("Connection not established, Please check URl and Authorization");
			return false;
		}

		return true;

	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReceiptUploadHeader
	 *            (ReceiptDialog)
	 * 
	 * @param tranType
	 *            (String)
	 * @param rcdStatusApproved
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ReceiptUploadHeader aReceiptUploadHeader, String tranType, String rcdStatus) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aReceiptUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aReceiptUploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReceiptUploadHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aReceiptUploadHeader.setRecordStatus(rcdStatus);

			if ("Save".equals(rcdStatus)) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReceiptUploadHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReceiptUploadHeader);
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
			processCompleted = doSaveProcess(auditHeader, PennantConstants.method_doApprove);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReceiptUploadHeader aReceiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getReceiptUploadHeaderService().delete(auditHeader);
					} else {
						auditHeader = getReceiptUploadHeaderService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getReceiptUploadHeaderService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aReceiptUploadHeader.getRecordType())) {
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getReceiptUploadHeaderService().doReject(auditHeader);

						if (aReceiptUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ReceiptUploadList, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ReceiptUploadList, auditHeader);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

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
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public ReceiptUploadHeaderService getReceiptUploadHeaderService() {
		return receiptUploadHeaderService;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public void setReceiptUploadApprovalProcess(ReceiptUploadApprovalProcess receiptUploadApprovalProcess) {
		this.receiptUploadApprovalProcess = receiptUploadApprovalProcess;
	}
}