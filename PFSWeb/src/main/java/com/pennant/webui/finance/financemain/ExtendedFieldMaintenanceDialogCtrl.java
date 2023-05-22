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
 * * FileName : ExtendedFieldMaintenanceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 20-01-2021 * * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-01-2021 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.ExtendedFieldMaintenance;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.ExtendedFieldMaintenanceService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennapps.core.util.ObjectUtil;

public class ExtendedFieldMaintenanceDialogCtrl extends GFCBaseCtrl<ExtendedFieldMaintenance> {

	private static final long serialVersionUID = 1050856461018302842L;
	private static final Logger logger = LogManager.getLogger(ExtendedFieldMaintenanceDialogCtrl.class);

	protected Window window_ExtendedFieldMaintenanceDialog;

	protected Listbox listBox_ExtendedFields;
	protected Combobox eventName;
	protected Tab detailsTab;

	private int formatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	protected String moduleCode = "";
	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	private FinanceDetail financeDetail = null;
	private ExtendedFieldMaintenance extendedFieldMaintenance;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	boolean userActivityLog = false;
	private List<ExtendedFieldRender> extendedFieldRenderList;
	private ExtendedFieldHeader extendedFieldHeader;

	private transient ExtendedFieldMaintenanceService extendedFieldMaintenanceService;

	public ExtendedFieldMaintenanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldMaintenanceDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ExtendedFieldMaintenanceDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ExtendedFieldMaintenanceDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeSelectCtrl")) {
			setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
		}

		if (arguments.containsKey("extendedFieldMaintenance")) {
			setExtendedFieldMaintenance((ExtendedFieldMaintenance) arguments.get("extendedFieldMaintenance"));
			this.extendedFieldMaintenance = getExtendedFieldMaintenance();
		}

		if (this.extendedFieldMaintenance == null) {
			throw new AppException(Labels.getLabel("error.unhandled"));
		}

		if (arguments.containsKey("moduleCode")) {
			moduleCode = (String) arguments.get("moduleCode");
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}

		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
		}

		// Store the before image.
		ExtendedFieldMaintenance extendedFieldMaintenance = new ExtendedFieldMaintenance();
		BeanUtils.copyProperties(this.extendedFieldMaintenance, extendedFieldMaintenance);
		this.extendedFieldMaintenance.setBefImage(extendedFieldMaintenance);

		// Render the page and display the data.
		doLoadWorkFlow(this.extendedFieldMaintenance.isWorkflow(), this.extendedFieldMaintenance.getWorkflowId(),
				this.extendedFieldMaintenance.getNextTaskId());

		if (isWorkFlowEnabled()) {
			getUserWorkspace().allocateAuthorities(pageRightName, getRole(), menuItemRightName);
			String recStatus = StringUtils.trimToEmpty(extendedFieldMaintenance.getRecordStatus());
			if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
				this.userAction = setRejectRecordStatus(this.userAction);
			} else {
				this.userAction = setListRecordStatus(this.userAction);
			}
		} else {
			this.south.setHeight("0px");
		}

		doCheckRights();
		doSetFieldProperties();
		doShowDialog(this.extendedFieldMaintenance);

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldMaintenanceDialog_btnSave"));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(ExtendedFieldMaintenance extendedFieldMaintenance) {
		logger.debug(Literal.ENTERING);
		try {
			// set ReadOnly mode accordingly if the object is new or not.
			if (extendedFieldMaintenance.isNewRecord()) {
				this.btnCtrl.setInitNew();
				doEdit();
			} else {
				if (isWorkFlowEnabled()) {
					if (StringUtils.isNotBlank(extendedFieldMaintenance.getRecordType())) {
						this.btnNotes.setVisible(true);
					}
					doEdit();
				} else {
					this.btnCtrl.setInitEdit();
					doReadOnly();
					btnCancel.setVisible(false);
				}
			}

			if (enqiryModule) {
				this.btnCtrl.setBtnStatus_Enquiry();
			}

			// fill the components with the data
			doWriteBeanToComponents(extendedFieldMaintenance);
			setDialog(DialogType.EMBEDDED);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doShowNotes(this.extendedFieldMaintenance);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 * @throws InterruptedException
	 */
	public void doWriteBeanToComponents(ExtendedFieldMaintenance extendedFieldMaintenance) {
		logger.debug(Literal.ENTERING);
		doFillEvents();
		this.recordStatus.setValue(extendedFieldMaintenance.getRecordStatus());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		readOnlyComponent(true, this.eventName);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(isReadOnly("ExtendedFieldMaintenanceDialog_eventName"), this.eventName);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.extendedFieldMaintenance.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/*
	 * Filling the finance events
	 */
	private void doFillEvents() {
		logger.debug(Literal.ENTERING);

		List<String> excludeValue = new ArrayList<>();
		excludeValue.add(FinServiceEvent.SUSPHEAD);
		excludeValue.add(FinServiceEvent.COVENANTS);
		excludeValue.add(FinServiceEvent.CHANGETDS);
		excludeValue.add(FinServiceEvent.PROVISION);
		excludeValue.add(FinServiceEvent.CANCELFIN);
		excludeValue.add(FinServiceEvent.CANCELDISB);
		excludeValue.add(FinServiceEvent.NOCISSUANCE);
		excludeValue.add(FinServiceEvent.OVERDRAFTSCHD);
		excludeValue.add(FinServiceEvent.CHGSCHDMETHOD);

		if (PennantConstants.RCD_STATUS_APPROVED.equals(extendedFieldMaintenance.getRecordStatus())) {
			fillComboBox(eventName, FinServiceEvent.ORG,
					PennantStaticListUtil.getValueLabels(PennantStaticListUtil.getFinEvents(true)), excludeValue);
		} else {
			fillComboBox(eventName, extendedFieldMaintenance.getEvent(),
					PennantStaticListUtil.getValueLabels(PennantStaticListUtil.getFinEvents(true)), excludeValue);
		}

		if (extendedFieldMaintenance.getRecordStatus().equals(PennantConstants.RCD_STATUS_SAVED)) {
			readOnlyComponent(true, this.eventName);
		}

		onChangeEvent(getComboboxValue(this.eventName));

		logger.debug(Literal.LEAVING);
	}

	public void onChange$eventName(Event event) {
		logger.debug(Literal.ENTERING);

		extendedFieldMaintenance.setEvent(getComboboxValue(this.eventName));
		onChangeEvent(getComboboxValue(this.eventName));

		logger.debug(Literal.LEAVING);
	}

	private void onChangeEvent(String eventName) {
		logger.debug(Literal.ENTERING);

		this.listBox_ExtendedFields.getItems().clear();

		this.detailsTab.setLabel(this.eventName.getSelectedItem().getLabel());
		ExtendedFieldCtrl extendedFieldCtrl = new ExtendedFieldCtrl();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		String subModule = financeMain.getFinCategory();
		ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl
				.getExtendedFieldHeader(ExtendedFieldConstants.MODULE_LOAN, subModule, eventName);

		if (extendedFieldHeader == null) {
			return;
		}

		StringBuilder tableName = new StringBuilder();
		tableName.append(ExtendedFieldConstants.MODULE_LOAN);
		tableName.append("_");
		tableName.append(subModule);
		tableName.append("_");
		tableName.append(PennantStaticListUtil.getFinEventCode(eventName));
		tableName.append("_ED");
		try {
			List<ExtendedFieldRender> extendedFieldRenderList = extendedFieldCtrl
					.getExtendedFieldRenderList(financeMain.getFinReference(), tableName.toString(), "_MView");

			doFillExtendedDetails(extendedFieldRenderList, extendedFieldHeader);

			for (ExtendedFieldRender extendedFieldRender : extendedFieldRenderList) {
				extendedFieldRender.setTableName(tableName.toString());
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill the extendedField Render List Details
	 * 
	 * @param extendedFieldRenderList
	 * @param extendedFieldHeader
	 */
	public void doFillExtendedDetails(List<ExtendedFieldRender> extendedFieldRenderList,
			ExtendedFieldHeader extendedFieldHeader) {
		logger.debug(Literal.ENTERING);

		setExtendedFieldRenderList(extendedFieldRenderList);
		setExtendedFieldHeader(extendedFieldHeader);

		this.listBox_ExtendedFields.getItems().clear();
		if (CollectionUtils.isNotEmpty(extendedFieldRenderList)) {
			for (ExtendedFieldRender detail : extendedFieldRenderList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(detail.getReference());
				lc.setParent(item);

				lc = new Listcell(String.valueOf(detail.getSeqNo()));
				lc.setParent(item);

				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("renderObject", detail);
				item.setAttribute("headerObject", extendedFieldHeader);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldDetailItemDoubleClicked");
				this.listBox_ExtendedFields.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Double click the item
	 * 
	 * @param event
	 */
	public void onExtendedFieldDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Listitem item = this.listBox_ExtendedFields.getSelectedItem();

		ExtendedFieldRender fieldRender = (ExtendedFieldRender) item.getAttribute("renderObject");
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) item.getAttribute("headerObject");

		String pageName = PennantApplicationUtil.getExtendedFieldPageName(extendedFieldHeader);

		getUserWorkspace().allocateAuthorities(pageName, getRole());

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldMaintenanceDialogCtrl", this);
		map.put("extendedFieldHeader", extendedFieldHeader);
		map.put("extendedFieldRender", fieldRender);
		map.put("ccyFormat", formatter);
		map.put("isReadOnly", false);
		map.put("roleCode", getRole());
		map.put("moduleType", PennantConstants.MODULETYPE_MAINT);
		map.put("eventName", eventName);
		map.put("moduleDefiner", moduleDefiner);
		map.put("userWorkspace", getUserWorkspace());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",
					window_ExtendedFieldMaintenanceDialog, map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		ExtendedFieldMaintenance extFieldsMaint = new ExtendedFieldMaintenance();
		extFieldsMaint = ObjectUtil.clone(getExtendedFieldMaintenance());
		extFieldsMaint.setEvent(getExtendedFieldHeader().getEvent());
		doWriteComponentsToBean(extFieldsMaint);

		boolean isNew;
		isNew = extFieldsMaint.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(extFieldsMaint.getRecordType())) {
				extFieldsMaint.setVersion(extFieldsMaint.getVersion() + 1);
				if (isNew) {
					extFieldsMaint.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					extFieldsMaint.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					extFieldsMaint.setNewRecord(true);
				}
			}
		} else {
			extFieldsMaint.setVersion(extFieldsMaint.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(extFieldsMaint, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(extFieldsMaint.getRoleCode(),
						extFieldsMaint.getNextRoleCode(), extFieldsMaint.getReference() + "", " Loan ",
						extFieldsMaint.getRecordStatus(), false);
				if (StringUtils.equals(extFieldsMaint.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Loan with Reference " + extFieldsMaint.getReference() + " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);
				if (getUserWorkspace() != null) {
					String pageName = PennantApplicationUtil.getExtendedFieldPageName(extendedFieldHeader);
					if (StringUtils.isNotBlank(pageName)) {
						getUserWorkspace().deAllocateAuthorities(pageName);
					}
				}
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void doWriteComponentsToBean(ExtendedFieldMaintenance extFieldsMaint) {
		logger.debug(Literal.ENTERING);

		List<ExtendedFieldRender> extFieldsList = getExtendedFieldRenderList();
		List<ExtendedFieldRender> updatedExtFieldsList = new ArrayList<ExtendedFieldRender>();

		for (ExtendedFieldRender extendedFieldRender : extFieldsList) {
			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)
					|| extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updatedExtFieldsList.add(extendedFieldRender);
			}
		}
		extFieldsMaint.setExtFieldRenderList(updatedExtFieldsList);
		extFieldsMaint.setExtendedFieldHeader(getExtendedFieldHeader());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = financeSelectCtrl.getSearchObj(true);
		financeSelectCtrl.getPagingFinanceList().setActivePage(0);
		financeSelectCtrl.getPagedListWrapper().setSearchObject(soFinanceMain);
		if (financeSelectCtrl.getListBoxFinance() != null) {
			financeSelectCtrl.getListBoxFinance().getListModel();
		}
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinMaintainInstruction (FinMaintainInstruction)
	 * 
	 * @param tranType                (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(ExtendedFieldMaintenance extFieldsMaint, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		extFieldsMaint.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		extFieldsMaint.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		extFieldsMaint.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			extFieldsMaint.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(extFieldsMaint.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, extFieldsMaint);
				}

				if (isNotesMandatory(taskId, extFieldsMaint)) {
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

			extFieldsMaint.setTaskId(taskId);
			extFieldsMaint.setNextTaskId(nextTaskId);
			extFieldsMaint.setRoleCode(getRole());
			extFieldsMaint.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(extFieldsMaint, tranType);
			String operationRefs = getServiceOperations(taskId, extFieldsMaint);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(extFieldsMaint, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(extFieldsMaint, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		ExtendedFieldMaintenance extFieldsMaint = (ExtendedFieldMaintenance) aAuditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = this.extendedFieldMaintenanceService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = this.extendedFieldMaintenanceService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = this.extendedFieldMaintenanceService.doApprove(aAuditHeader);

					if (extFieldsMaint.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = this.extendedFieldMaintenanceService.doReject(aAuditHeader);

					if (extFieldsMaint.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ExtendedFieldMaintenanceDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldMaintenanceDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.extendedFieldMaintenance), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param ExtendedFieldMaintenance
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ExtendedFieldMaintenance extFieldsMaint, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, extFieldsMaint.getBefImage(), extFieldsMaint);
		return new AuditHeader(getReference(), null, null, null, auditDetail, extFieldsMaint.getUserDetails(),
				getOverideMap());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doClose(this.btnSave.isVisible());

		String pageName = PennantApplicationUtil.getExtendedFieldPageName(extendedFieldHeader);
		if (StringUtils.isNotBlank(pageName)) {
			getUserWorkspace().deAllocateAuthorities(pageName);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.extendedFieldMaintenance.getBefImage());

		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected String getReference() {
		return getExtendedFieldMaintenance().getReference();
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public ExtendedFieldMaintenance getExtendedFieldMaintenance() {
		return extendedFieldMaintenance;
	}

	public void setExtendedFieldMaintenance(ExtendedFieldMaintenance extendedFieldMaintenance) {
		this.extendedFieldMaintenance = extendedFieldMaintenance;
	}

	public void setExtendedFieldMaintenanceService(ExtendedFieldMaintenanceService extendedFieldMaintenanceService) {
		this.extendedFieldMaintenanceService = extendedFieldMaintenanceService;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

}
