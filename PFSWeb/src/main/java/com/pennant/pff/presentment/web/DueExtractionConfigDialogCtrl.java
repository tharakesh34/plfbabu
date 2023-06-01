package com.pennant.pff.presentment.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DueExtractionConfigDialogCtrl extends GFCBaseCtrl<InstrumentTypes> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DueExtractionConfigDialogCtrl.class);

	protected Window windowDueExtractionConfigDialog;
	protected Datebox appDate;
	protected Listbox listBox;

	private DueExtractionHeader dueExtractionHeader;

	private DueExtractionConfigListCtrl dueExtractionConfigList;
	private transient DueExtractionConfigService dueExtractionConfigService;

	/**
	 * default constructor.<br>
	 */
	public DueExtractionConfigDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DueExtractionConfigDialog";
	}

	public void onCreate$windowDueExtractionConfigDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowDueExtractionConfigDialog);

		try {

			doCheckRights();

			dueExtractionHeader = (DueExtractionHeader) arguments.get("DueExtractionHeader");
			dueExtractionConfigList = (DueExtractionConfigListCtrl) arguments.get("DueExtractionConfigList");

			doLoadWorkFlow(dueExtractionHeader.isWorkflow(), dueExtractionHeader.getWorkflowId(),
					dueExtractionHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doShowDialog(this.dueExtractionHeader);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.windowDueExtractionConfigDialog.onClose();
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(DueExtractionHeader dueExtractionHeader) {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			if (StringUtils.isNotBlank(dueExtractionHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}
		} else {
			this.btnCtrl.setInitEdit();
			btnCancel.setVisible(false);
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		dueExtractionHeader.setInstruments(dueExtractionConfigService.getInstrumentTypesMap());
		dueExtractionHeader.setConfig(dueExtractionConfigService.getDueExtractionConfig(dueExtractionHeader.getID()));

		doWriteBeanToComponents(dueExtractionHeader);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(DueExtractionHeader dueExtractionHeader) {
		logger.debug(Literal.ENTERING);

		this.appDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.appDate.setValue(SysParamUtil.getAppDate());
		this.appDate.setDisabled(true);

		doFillInstrumentDetails(listBox, dueExtractionHeader);

		logger.debug(Literal.LEAVING);
	}

	private void doFillInstrumentDetails(Listbox listbox, DueExtractionHeader dueExtractionHeader) {
		Listhead head = new Listhead();

		Listheader header = new Listheader("Due Date");
		header.setParent(head);

		Map<Long, InstrumentTypes> instruments = dueExtractionHeader.getInstruments();

		Set<Long> keySet = instruments.keySet();

		List<DueExtractionConfig> dueExtractionConfig = dueExtractionHeader.getConfig();

		Map<Long, List<DueExtractionConfig>> dataMap = new HashMap<>();

		int count = 0;
		for (Long key : keySet) {
			header = new Listheader(instruments.get(key).getCode());
			header.setParent(head);

			List<DueExtractionConfig> list = new ArrayList<>();
			for (DueExtractionConfig dec : dueExtractionConfig) {
				if (dec.getInstrumentID() == key) {
					list.add(dec);
				}
			}

			list = list.stream().sorted((l1, l2) -> l1.getDueDate().compareTo(l2.getDueDate()))
					.collect(Collectors.toList());
			count = list.size();
			dataMap.put(key, list);
		}

		listbox.appendChild(head);

		Listitem item = new Listitem();

		Listcell cell = new Listcell("Configured Days");
		cell.setParent(item);

		for (Long key : keySet) {
			cell = new Listcell();

			Intbox intbox = new Intbox();
			intbox.setDisabled(true);
			InstrumentTypes instrumentType = instruments.get(key);
			intbox.setValue(instrumentType.getExtractionDays());
			cell.appendChild(intbox);

			cell.setParent(item);
		}

		listbox.appendChild(item);

		for (int i = 0; i < count; i++) {
			int row = 0;

			for (Long key : keySet) {
				List<DueExtractionConfig> list = dataMap.get(key);

				DueExtractionConfig config = list.get(i);
				if (row == 0) {
					item = new Listitem();

					cell = new Listcell();

					Datebox datebox = new Datebox();
					datebox.setWidth("100px");
					datebox.setDisabled(true);
					datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
					datebox.setValue(config.getDueDate());

					cell.appendChild(datebox);
					cell.setParent(item);
					row++;
				}

				cell = new Listcell();

				Datebox datebox = new Datebox();
				datebox = new Datebox();
				datebox.setWidth("100px");
				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
				datebox.setDisabled(isDisabled(config));

				if (config.isModified()) {
					datebox.setStyle("background-color:#FFFF00;");
				}

				datebox.setValue(config.getExtractionDate());
				datebox.setAttribute("Changed", false);
				datebox.setAttribute("PEC", config);

				datebox.addForward(Events.ON_CHANGE, this.window, "onChangeExtractionDate");

				cell.appendChild(datebox);
				cell.setParent(item);
			}

			listbox.appendChild(item);
		}
	}

	public void onChangeExtractionDate(ForwardEvent event) {
		Datebox datebox = (Datebox) event.getOrigin().getTarget();

		DueExtractionConfig pec = (DueExtractionConfig) datebox.getAttribute("PEC");

		Date extractionDate = pec.getExtractionDate();

		Date date = datebox.getValue();

		Date appDate = SysParamUtil.getAppDate();

		Date maxDate = DateUtil.addDays(appDate, -5);

		List<WrongValueException> wve = new ArrayList<>();

		if (DateUtil.compare(date, appDate) < 0) {
			String format = Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
					new String[] { "Extraction Date", DateUtil.format(appDate, DateFormat.SHORT_DATE.getPattern()) });
			wve.add(new WrongValueException(datebox, format));
		}

		if (DateUtil.compare(maxDate, date) > 0) {
			String format = Labels.getLabel("DATE_ALLOWED_MAXDATE",
					new String[] { "Extraction Date", DateUtil.format(maxDate, DateFormat.SHORT_DATE.getPattern()) });
			wve.add(new WrongValueException(datebox, format));
		}

		if (DateUtil.compare(extractionDate, date) != 0) {
			datebox.setAttribute("Changed", true);
		}

		showErrorDetails(wve);
	}

	private void showErrorDetails(List<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(wve)) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}

			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isDisabled(DueExtractionConfig config) {
		if (isReadOnly("DueExtractionConfig_ExtractionDate")) {
			return true;
		}

		if (config.getConfigureDays() <= 0) {
			return true;
		}

		if (config.getExtractionDate().compareTo(this.appDate.getValue()) <= 0) {
			return true;
		}

		return false;
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doWriteComponentsToBean() {
		logger.debug(Literal.LEAVING);

		List<Listitem> items = listBox.getItems();

		int index = 0;
		for (Listitem listitem : items) {
			index++;
			if (index == 1) {
				continue;
			}

			updateExtractionDate(listitem.getChildren());
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateExtractionDate(List<Listcell> children) {
		for (Listcell cell : children) {
			Datebox datebox = (Datebox) cell.getFirstChild();

			Object attribute = datebox.getAttribute("Changed");

			if (attribute == null) {
				continue;
			}

			if ((boolean) attribute) {
				DueExtractionConfig pec = (DueExtractionConfig) datebox.getAttribute("PEC");
				pec.setExtractionDate(datebox.getValue());
				pec.setModified(true);
			}
		}
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doSave();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		DueExtractionHeader header = new DueExtractionHeader();

		BeanUtils.copyProperties(dueExtractionHeader, header);

		doWriteComponentsToBean();

		String tranType = "";

		boolean isNew = header.isNewRecord();

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(header.getRecordType()).equals("")) {
				header.setVersion(header.getVersion() + 1);
				if (isNew) {
					header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					header.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					header.setNewRecord(true);
				}
			}
		} else {
			header.setVersion(header.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(header, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public boolean doProcess(DueExtractionHeader header, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		String nextRoleCode = "";

		header.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			header.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(header.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, header);
				}

				if (isNotesMandatory(taskId, header)) {
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

			header.setTaskId(taskId);
			header.setNextTaskId(nextTaskId);
			header.setRoleCode(getRole());
			header.setNextRoleCode(nextRoleCode);

			AuditHeader auditHeader = getAuditHeader(header, tranType);

			String operationRefs = getServiceOperations(taskId, header);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}

		} else {
			AuditHeader auditHeader = getAuditHeader(header, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving ");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(DueExtractionHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, header.getBefImage(), header);
		return new AuditHeader(getReference(), null, null, null, auditDetail, header.getUserDetails(), getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					// auditHeader = autoPresentationService.delete(auditHeader);
				} else {
					auditHeader = dueExtractionConfigService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = dueExtractionConfigService.doApprove(auditHeader);
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = dueExtractionConfigService.doReject(auditHeader);
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.window, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window, auditHeader);
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

	@Override
	protected void refreshList() {
		dueExtractionConfigList.search();
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.dueExtractionHeader);
	}

	@Autowired
	public void setDueExtractionConfigService(DueExtractionConfigService dueExtractionConfigService) {
		this.dueExtractionConfigService = dueExtractionConfigService;
	}

}
