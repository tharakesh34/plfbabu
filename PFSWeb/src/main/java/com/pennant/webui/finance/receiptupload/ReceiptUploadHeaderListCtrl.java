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
 * * FileName : UploadHeaderListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.receiptupload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
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

public class ReceiptUploadHeaderListCtrl extends GFCBaseListCtrl<ReceiptUploadHeader> {
	private static final long serialVersionUID = 1817958653208633892L;

	protected Window window_ReceiptUploadList;
	protected Borderlayout borderLayout_ReceiptUploadList;
	protected Paging pagingReceiptUploadList;
	protected Listbox listBoxReceiptUpload;

	protected Intbox uploadId;
	protected Textbox fileName;

	protected Listbox sortOperator_uploadId;
	protected Listbox sortOperator_fileName;

	protected Listhead listHeadReceiptUpload;
	protected Listheader listheader_UploadID;
	protected Listheader listheader_FileName;
	protected Listheader listheader_Success;
	protected Listheader listheader_FailedCount;
	protected Listheader listheader_TotalCount;
	protected Listheader listheader_ProcCount;

	protected Listheader listHeader_CheckBox;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Listbox sortOperator_entityCode;

	protected Button btnReject;
	protected Button btnApprove;
	protected Button btndownload;

	protected Row row0;
	protected Row row1;
	protected Row row_AlwWorkflow;
	protected ExtendedCombobox entityCode;
	private Map<Long, String> receiptIdMap = new HashMap<>();

	boolean isApprovalMenu = false;

	protected Button button_ReceiptUploadList_NewReceiptUpload;
	protected Button button_ReceiptUploadList_ReceiptUploadSearchDialog;

	private transient ReceiptUploadHeaderService receiptUploadHeaderService;

	private ReceiptUploadApprovalProcess receiptUploadApprovalProcess;

	public static Map<Long, Integer> importStatusMap = new HashMap<>();
	public List<Progressmeter> progressMeterList = new ArrayList<>();

	public static Map<Long, ReceiptUploadLog> approveStatusMap = new HashMap<>();
	public List<Progressmeter> progressMeterApprList = new ArrayList<>();
	private Timer timer;

	public ReceiptUploadHeaderListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		if (isApprovalMenu) {
			this.searchObject.addWhereClause(
					" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "'" + ",'Attempt')");
		} else {
			this.searchObject.addWhereClause(" RECORDSTATUS not in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		}
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ReceiptUploadHeader";
		super.pageRightName = "UploadHeaderList";
		super.tableName = "ReceiptUploadHeader_AView";

		if (StringUtils.equals(getArgument("approval"), "Y")) {
			this.isApprovalMenu = true;
			super.queueTableName = "ReceiptUploadHeader_TView";
		} else {
			this.isApprovalMenu = false;
			super.queueTableName = "ReceiptUploadHeader_View";
		}
	}

	public void onCreate$window_ReceiptUploadList(Event event) {
		timer.setDelay(1000);
		progressMeterList.clear();
		progressMeterApprList.clear();

		setPageComponents(window_ReceiptUploadList, borderLayout_ReceiptUploadList, listBoxReceiptUpload,
				pagingReceiptUploadList);

		if (isApprovalMenu) {
			setApprovalItemRenderer();
			registerField("attemptStatus");
			registerField("attemptNo");
			registerField("recordStatus");

			setComparator((a, b) -> {
				ReceiptUploadHeader data = (ReceiptUploadHeader) a;
				ReceiptUploadHeader data2 = (ReceiptUploadHeader) b;
				return Long.compare(data.getUploadHeaderId(), data2.getUploadHeaderId());
			});
		} else {
			listHeadReceiptUpload.removeChild(listheader_ProcCount);
			listHeadReceiptUpload.removeChild(listHeader_CheckBox);
			setItemRenderer();
			registerField("recordStatus", listheader_RecordStatus, SortOrder.NONE, recordStatus,
					sortOperator_RecordStatus, Operators.STRING);
			searchObject.addSortDesc("uploadHeaderId");
		}

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
		registerField("uploadProgress");

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
			search();
		}

	}

	private void doSetFieldProperties() {

		this.row_AlwWorkflow.setVisible(false);
		this.btnApprove.setVisible(true);
		this.btndownload.setVisible(true);
		this.btnReject.setVisible(true);
		this.print.setVisible(false);
		this.listHeader_CheckBox.setVisible(true);
		this.button_ReceiptUploadList_NewReceiptUpload.setVisible(false);
		this.row0.setVisible(true);
		this.row1.setVisible(true);
		this.listheader_UploadID.setVisible(true);

		if (isApprovalMenu) {
			listItem_Checkbox = new Listitem();
			listCell_Checkbox = new Listcell();
			listHeader_CheckBox_Comp = new Checkbox();
			listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
			listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
			listItem_Checkbox.appendChild(listCell_Checkbox);

			if (listHeader_CheckBox.getChildren() != null) {
				listHeader_CheckBox.getChildren().clear();
			}
			listHeader_CheckBox.appendChild(listHeader_CheckBox_Comp);
		}

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
	public void onClick_listHeaderCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxReceiptUpload.getItems().size(); i++) {
			Listitem listitem = listBoxReceiptUpload.getItems().get(i);
			if (listitem instanceof Listgroup) {
				Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
				if (cb.isDisabled())
					continue;
				cb.setChecked(listHeader_CheckBox_Comp.isChecked());
			}
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

		logger.debug(Literal.LEAVING);
	}

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
		this.entityCode.setConstraint("");
		this.entityCode.setErrorMessage("");
	}

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

	public void onClick$button_ReceiptUploadList_NewReceiptUpload(Event event) {
		logger.debug(Literal.ENTERING);

		ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
		receiptUploadHeader.setNewRecord(true);
		receiptUploadHeader.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("receiptUploadHeader", receiptUploadHeader);
		arg.put("receiptUploadListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/SelectReceiptUploadHeaderDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onReceiptUploadItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxReceiptUpload.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		long id = (long) selectedItem.getAttribute("id");
		ReceiptUploadHeader receiptUploadHeader = receiptUploadHeaderService.getUploadHeaderById(id, false);

		if (receiptUploadHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (receiptUploadHeader.getUploadProgress() == ReceiptUploadConstants.RECEIPT_IMPORTINPROCESS) {
			MessageUtil.showMessage("Import is in process for this record");
			search();
			return;
		}

		if (StringUtils.equals(receiptUploadHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
			return;
		}

		String whereCond = " where UploadHeaderId=?";

		if (doCheckAuthority(receiptUploadHeader, whereCond,
				new Object[] { receiptUploadHeader.getUploadHeaderId() })) {
			if (isWorkFlowEnabled() && receiptUploadHeader.getWorkflowId() == 0) {
				receiptUploadHeader.setWorkflowId(getWorkFlowId());
			}

			logUserAccess("menu_Item_ReceiptUpload", String.valueOf(id));
			doShowDialogPage(receiptUploadHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(ReceiptUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> args = new HashMap<String, Object>();

		boolean enqiury = StringUtils.equals(uploadHeader.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED);

		args.put("enqModule", enqiury);
		args.put("uploadReceiptHeader", uploadHeader);
		args.put("receiptUploadListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/ReceiptUploadHeaderDialog.zul", null,
					args);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		doPrintResults();
	}

	private void setApprovalItemRenderer() {
		setItemRender((item, upldHdr, count) -> {
			Listcell lc;
			long Id = upldHdr.getUploadHeaderId();
			if (item instanceof Listgroup) {
				lc = new Listcell();

				Checkbox listCheckBox = new Checkbox();
				listCheckBox.setValue(Id);
				listCheckBox.addForward("onClick", self, "onClick_listCellCheckBox");

				if (listHeader_CheckBox_Comp.isChecked() || receiptIdMap.containsKey(Id)) {
					listCheckBox.setChecked(true);
				}

				if (upldHdr.getUploadProgress() == ReceiptUploadConstants.RECEIPT_INPROCESS) {
					listCheckBox.setChecked(false);
					listCheckBox.setDisabled(true);
				}

				lc.appendChild(listCheckBox);
				item.appendChild(lc);

				lc = new Listcell(String.valueOf(Id));
				item.appendChild(lc);

				lc = new Listcell(upldHdr.getFileName());
				lc.setStyle("font-weight:bold;color:##FF4500;");
				item.appendChild(lc);

				lc = new Listcell(String.valueOf(upldHdr.getTotalRecords()));
				lc.setStyle("font-weight:bold;color:##FF4500;");
				item.appendChild(lc);

				lc = new Listcell();
				item.appendChild(lc);

				lc = new Listcell();
				item.appendChild(lc);

				lc = new Listcell();
				item.appendChild(lc);

				lc = new Listcell(upldHdr.getRecordStatus());
				item.appendChild(lc);

				return;
			}

			lc = new Listcell();
			lc.setParent(item);

			if (upldHdr.getAttemptNo() > 0) {
				lc = new Listcell("Attempt" + upldHdr.getAttemptNo());
			} else
				lc = new Listcell("Imported");

			lc.setParent(item);

			lc = new Listcell();

			Progressmeter pm = new Progressmeter();
			pm.setStyle("position: relative !important;");
			pm.setWidth("195px");

			if (upldHdr.getAttemptStatus() == ReceiptUploadConstants.ATTEMPSTATUS_INPROCESS
					&& approveStatusMap.containsKey(upldHdr.getId())) {
				pm.setValue(approveStatusMap.get(upldHdr.getId()).getProgress());
				pm.setAttribute("id", Id);
				progressMeterApprList.add(pm);
				lc.appendChild(pm);
			} else {
				pm.setAttribute("id", null);
			}
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			String idAttmpt = Id + "" + upldHdr.getAttemptNo();
			lc = new Listcell(String.valueOf(upldHdr.getProcRecords()));
			lc.setId("proc" + idAttmpt);
			lc.setParent(item);

			lc = new Listcell(String.valueOf(upldHdr.getSuccessCount()));
			lc.setId("succ" + idAttmpt);
			lc.setParent(item);

			lc = new Listcell(String.valueOf(upldHdr.getFailedCount()));
			lc.setId("fail" + idAttmpt);
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			item.setAttribute("id", Id);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onReceiptUploadItemDoubleClicked");
		});
	}

	private void setItemRenderer() {
		setItemRender((item, upldHdr, count) -> {
			Listcell lc;

			lc = new Listcell(String.valueOf(upldHdr.getUploadHeaderId()));
			lc.setParent(item);

			lc = new Listcell(upldHdr.getFileName());

			Hbox hb = new Hbox();
			hb.setHflex("5");
			hb.setStyle("padding:5px;");
			Label lb = new Label("Import Progress:");
			lb.setStyle("font-size:11px;font-weight:bold;color:grey;");

			Progressmeter pm = new Progressmeter();
			pm.setWidth("195px");
			pm.setStyle("position: relative !important;");

			hb.appendChild(lb);
			hb.appendChild(pm);

			if (upldHdr.getUploadProgress() == ReceiptUploadConstants.RECEIPT_IMPORTINPROCESS
					&& importStatusMap.containsKey(upldHdr.getId())) {
				pm.setValue(importStatusMap.get(upldHdr.getId()));
				pm.setAttribute("id", upldHdr.getUploadHeaderId());
				progressMeterList.add(pm);
				lc.appendChild(hb);
			}
			lc.setParent(item);

			lc = new Listcell(String.valueOf(upldHdr.getTotalRecords()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(upldHdr.getSuccessCount()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(upldHdr.getFailedCount()));
			lc.setParent(item);

			String uploadStatus = null;

			switch (upldHdr.getUploadProgress()) {
			case ReceiptUploadConstants.RECEIPT_IMPORTED:
				uploadStatus = "Imported";
				break;
			case ReceiptUploadConstants.RECEIPT_IMPORTINPROCESS:
				uploadStatus = "Importing..";
				break;
			case ReceiptUploadConstants.RECEIPT_IMPORTFAILED:
				uploadStatus = "Import Failed";
				break;

			default:
				uploadStatus = upldHdr.getRecordStatus();
				break;
			}
			lc = new Listcell(uploadStatus);
			lc.setParent(item);

			item.setAttribute("id", upldHdr.getUploadHeaderId());
			ComponentsCtrl.applyForward(item, "onDoubleClick=onReceiptUploadItemDoubleClicked");
		});
	}

	private void updateListCell(long Id, Function<ReceiptUploadLog, AtomicInteger> fun, String name) {
		try {
			ReceiptUploadLog ual = approveStatusMap.get(Id);
			Listcell lc1 = (Listcell) this.listBoxReceiptUpload.getFellowIfAny(name + Id + ual.getAttemptNo());
			lc1.setLabel(String.valueOf(fun.apply(ual).get()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

		}
	}

	public void onTimer$timer(Event event) {

		if (isApprovalMenu) {

			progressMeterApprList.removeIf(e -> {
				Long id = (Long) e.getAttribute("id");

				if (approveStatusMap.containsKey(id)) {
					updateListCell(id, log -> log.getFailRecords(), "fail");
					updateListCell(id, log -> log.getSuccessRecords(), "succ");
					updateListCell(id, log -> log.getProcessedRecords(), "proc");
					int progress = approveStatusMap.get(id).getProgress();
					e.setValue(progress);
					e.setTooltiptext(progress + "%");
					return false;
				}

				e.getParent().getParent().removeChild(e.getParent());
				search();
				return true;
			});
			return;
		}

		progressMeterList.removeIf(e -> {
			Long id = (Long) e.getAttribute("id");
			if (importStatusMap.containsKey(id)) {
				int progress = importStatusMap.get(id);
				e.setValue(progress);
				e.setTooltiptext(progress + "%");
				return false;
			}
			search();
			return true;
		});
	}

	public void onClick_listCellCheckBox(ForwardEvent event) {

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

		if (receiptidList.size() == 1) {
			for (long id : receiptidList) {
				byte[] byteArray = getExcelData(String.valueOf(id));
				Filedownload.save(new AMedia(String.valueOf(id), "xls", "application/vnd.ms-excel", byteArray));
				this.receiptUploadHeaderService.updateUploadProgress(id, ReceiptUploadConstants.RECEIPT_DOWNLOADED);
			}
		} else if (receiptidList.size() > 1) {

			try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
				try (ZipOutputStream out = new ZipOutputStream(arrayOutputStream)) {
					for (long id : receiptidList) {
						byte[] byteArray = getExcelData(String.valueOf(id));
						out.putNextEntry(new ZipEntry(id + ".xls"));
						out.write(byteArray);
						out.closeEntry();

						this.receiptUploadHeaderService.updateUploadProgress(id,
								ReceiptUploadConstants.RECEIPT_DOWNLOADED);
					}

					String zipfileName = "ReceiptUpload.zip";

					byte[] tobytes = arrayOutputStream.toByteArray();
					Filedownload.save(new AMedia(zipfileName, "zip", "application/*", tobytes));
				}
			}
		}

		doRefresh();
		Clients.showNotification(Labels.getLabel("label_DataExtractionList_DownloadedSuccess.value"), "info", null,
				null, -1);

		logger.debug(Literal.LEAVING);
	}

	private List<Long> getListofReceiptUpload() {

		List<Long> headerIdsList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			headerIdsList = getReceiptUploadHeaderList();
		} else {
			headerIdsList = new ArrayList<Long>(receiptIdMap.keySet());
		}
		return headerIdsList;
	}

	public void onClick$btnApprove(Event event) {
		logger.debug(Literal.ENTERING);

		listHeader_CheckBox_Comp.setDisabled(true);

		List<Long> headerIdList = getListofReceiptUpload();
		receiptIdMap.clear();

		if (headerIdList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("ReceiptUploadDataList_NoEmpty"));
			return;
		}

		Collections.sort(headerIdList);

		logger.info("Checking Authority for HeaderIds {}", headerIdList);
		List<ReceiptUploadHeader> uploadHeadersList = doCheckAuthority(headerIdList, true);

		if (uploadHeadersList.isEmpty()) {
			logger.info("User is not Allowed to Approve");
			return;
		}

		try {
			approveStatusMap.putAll(receiptUploadHeaderService.updateProgress(uploadHeadersList));

			logger.info("Started Approval Process for the HeaderId's{}", headerIdList);

			doApprove(uploadHeadersList);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		listHeader_CheckBox_Comp.setChecked(false);
		search();
		Clients.showNotification("Receipt Process initialized.", "info", null, null, -1);
		listHeader_CheckBox_Comp.setDisabled(false);
		logger.debug(Literal.LEAVING);
	}

	private List<ReceiptUploadHeader> doCheckAuthority(List<Long> receiptidList, boolean getSucessRecords) {

		List<ReceiptUploadHeader> uploadHeaderList = new ArrayList<>();

		for (long id : receiptidList) {
			ReceiptUploadHeader receiptUploadHeader = receiptUploadHeaderService.getUploadHeaderById(id, false);

			if (receiptUploadHeader.getUploadProgress() == ReceiptUploadConstants.RECEIPT_INPROCESS) {
				continue;
			}

			String whereCond = " UploadHeaderId= ?";

			if (doCheckAuthority(receiptUploadHeader, whereCond,
					new Object[] { receiptUploadHeader.getUploadHeaderId() })) {
				if (isWorkFlowEnabled() && receiptUploadHeader.getWorkflowId() == 0) {
					receiptUploadHeader.setWorkflowId(getWorkFlowId());
				}
				doLoadWorkFlow(isWorkFlowEnabled(), receiptUploadHeader.getWorkflowId(),
						receiptUploadHeader.getNextTaskId());
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return new ArrayList<ReceiptUploadHeader>();

			}
			uploadHeaderList.add(receiptUploadHeader);
		}

		return uploadHeaderList;
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

	private void doApprove(List<ReceiptUploadHeader> uploadHeaderList) {
		try {
			new Thread(() -> {

				List<Long> headerIdList = uploadHeaderList.stream().map(ReceiptUploadHeader::getId)
						.collect(Collectors.toList());
				try {
					receiptUploadApprovalProcess.approveReceipts(headerIdList, getUserWorkspace().getLoggedInUser(),
							approveStatusMap);

					for (ReceiptUploadHeader ruh : uploadHeaderList) {
						logger.info("Approving Header ", ruh.getId(), headerIdList);

						ruh.setUploadProgress(ReceiptUploadConstants.RECEIPT_APPROVED);
						doProcess(ruh, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_APPROVED);
					}
				} catch (Exception e) {
					headerIdList.forEach(ruh -> receiptUploadHeaderService.updateUploadProgress(ruh,
							ReceiptUploadConstants.RECEIPT_PROCESSFAILED));
					logger.error(Literal.EXCEPTION, e);
				} finally {
					logger.info("Updating Status as Completed for the HeaderIdIs{}", headerIdList, headerIdList);
					receiptUploadHeaderService.updateStatus(headerIdList, approveStatusMap);
				}
			}).start();

			Thread.sleep(1000);

		} catch (Exception e) {
			MessageUtil.showError(e);
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

	private byte[] getExcelData(String id) {
		logger.debug(Literal.ENTERING);

		String whereCond = " where t.uploadHeaderId in (" + "'" + id + "'" + ") and  ProcessingStatus ="
				+ ReceiptDetailStatus.SUCCESS.getValue();
		StringBuilder searchCriteria = new StringBuilder(" ");

		String reportPath = PathUtil.REPORTS_ORGANIZATION;
		String reportName = "ReceiptUploadApprover";
		String userName = getUserWorkspace().getLoggedInUser().getFullName();

		logger.debug(Literal.LEAVING);
		return ReportsUtil.getExcelData(reportPath, reportName, userName, whereCond, searchCriteria);
	}

	protected boolean doProcess(ReceiptUploadHeader aReceiptUploadHeader, String tranType, String rcdStatus) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReceiptUploadHeader aReceiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = receiptUploadHeaderService.delete(auditHeader);
				} else {
					auditHeader = receiptUploadHeaderService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = receiptUploadHeaderService.doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aReceiptUploadHeader.getRecordType())) {
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = receiptUploadHeaderService.doReject(auditHeader);

					if (aReceiptUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(ReceiptUploadHeader aReceiptUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReceiptUploadHeader.getBefImage(),
				aReceiptUploadHeader);
		return new AuditHeader(String.valueOf(aReceiptUploadHeader.getId()), null, null, null, auditDetail,
				aReceiptUploadHeader.getUserDetails(), getOverideMap());
	}

	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public void setReceiptUploadApprovalProcess(ReceiptUploadApprovalProcess receiptUploadApprovalProcess) {
		this.receiptUploadApprovalProcess = receiptUploadApprovalProcess;
	}
}