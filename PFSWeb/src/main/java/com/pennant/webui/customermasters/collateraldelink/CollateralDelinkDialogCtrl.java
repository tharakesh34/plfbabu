package com.pennant.webui.customermasters.collateraldelink;

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
 * * FileName : CollateralHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * 10-05-2019 Srinivasa Varma 0.2 Development Item 82 * * 16-05-2019 Srinivasa Varma 0.3
 * Development Item 82 Corrections * * * * *
 ********************************************************************************************
 */

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinCollateralDelinkService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.commitment.commitment.CommitmentDialogCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/CollateralHeaderDialog.zul file.
 */
public class CollateralDelinkDialogCtrl extends GFCBaseCtrl<CollateralAssignment> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(CollateralDelinkDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CollateralDelinkDialog;
	private FinCollateralDelinkService finCollateralDelinkService;
	protected Borderlayout borderlayoutCollateralAssignment;

	private ArrayList<Object> headerList;
	private FinanceDetail financedetail;
	private FinMaintainInstruction finMaintainInstruction;
	private FinanceSelectCtrl financeSelectCtrl = null;

	// Collateral Total Count Details
	protected Label collateralCount;
	protected Label availableCollateral;
	protected Button btnNew_CollateralAssignment;
	protected Div collateralDiv;
	protected Grid collateralTotalsGrid;
	protected Listbox listBoxCollateralAssignments;
	protected Listbox listBoxAssetTypeHeader;
	protected Button btnNew_AssetType;

	protected North north;
	protected South south;
	private FinanceMainBaseCtrl financeMainDialogCtrl;
	private String roleCode = "";
	private BigDecimal totalValue = BigDecimal.ZERO;
	private BigDecimal utilizedAmount = BigDecimal.ZERO;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;
	private List<CollateralAssignment> collateralAssignments = null;
	private boolean isNotFinanceProcess = false;
	private String moduleName;
	// ### 10-05-2018 Start Development Item 82
	private Map<String, Object> rules = new HashMap<>();

	public Map<String, Object> getRules() {
		return rules;
	}

	private String finLTVCheck;

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
	}

	// ### 10-05-2018 End Development Item 82
	/**
	 * default constructor.<br>
	 */
	public CollateralDelinkDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralAssignmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CollateralDelinkDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralDelinkDialog);

		try {
			if (arguments.containsKey("financeMainDialogCtrl")) {
				if (arguments.get("financeMainDialogCtrl") instanceof FinanceMainBaseCtrl) {
					financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
				}
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, this.pageRightName);
			}

			if (arguments.containsKey("financeDetail")) {
				financedetail = (FinanceDetail) arguments.get("financeDetail");
				if (financedetail != null) {
					if (financedetail.getCollateralAssignmentList() != null) {
						setCollateralAssignments(financedetail.getCollateralAssignmentList());
					}
				}
			}

			if (arguments.containsKey("moduleCode")) {
				this.moduleCode = (String) arguments.get("moduleCode");
				finMaintainInstruction = (FinMaintainInstruction) arguments.get("finMaintainInstruction");

				financeSelectCtrl = (FinanceSelectCtrl) arguments.get("financeSelectCtrl");

				// Store the before image.
				FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
				BeanUtils.copyProperties(this.finMaintainInstruction, finMaintainInstruction);
				this.finMaintainInstruction.setBefImage(finMaintainInstruction);

				doLoadWorkFlow(this.finMaintainInstruction.isWorkflow(), this.finMaintainInstruction.getWorkflowId(),
						this.finMaintainInstruction.getNextTaskId());

				if (isWorkFlowEnabled()) {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), this.pageRightName, this.pageRightName);
				} else {
					this.south.setHeight("0px");
				}
			}

			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
			}

			if (arguments.containsKey("finHeaderList")) {
				headerList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			doEdit();

			doCheckRights();

			doSetFieldProperties();

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CollateralDelinkDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		if (isNotFinanceProcess) {
			// window__title.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doShowDialog() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");

		try {
			// append finance basic details
			appendFinBasicDetails();

			setCollateralAssignments(financedetail.getCollateralAssignmentList());

			// fill the components with the data
			doFillCollateralDetails(getCollateralAssignments(), false);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finMaintainInstruction.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}

			// 26-09-2022: Removed setting controller to the FinanceMainBaseCtrl as the object was never used.

			doWriteBeanToComponents();
			getBorderLayoutHeight();

			this.listBoxCollateralAssignments.setHeight(180 + "px");
			this.window_CollateralDelinkDialog.setHeight(this.borderLayoutHeight + 50 + "px");
			north.setVisible(true);
			south.setVisible(true);
			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CollateralDelinkDialog.onClose();
		}

		logger.debug("Leaving");
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finMaintainInstruction);
	}

	/**
	 * Method for Checking Rights for the Collateral Dialog
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("CollateralAssignmentDialog", this.roleCode);

		logger.debug("Leaving");
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	private int getFormat() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int ccyFormat = 0;
		if (getFinanceMainDialogCtrl() != null && getFinanceMainDialogCtrl() instanceof CommitmentDialogCtrl) {
			ccyFormat = 2;
		} else if (getFinanceMainDialogCtrl() != null
				&& !(getFinanceMainDialogCtrl() instanceof CommitmentDialogCtrl)) {
			ccyFormat = (int) getFinanceMainDialogCtrl().getClass().getMethod("getCcyFormat")
					.invoke(getFinanceMainDialogCtrl());
		}
		return ccyFormat;
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		FinMaintainInstruction aFinMaintainInstruction = new FinMaintainInstruction();

		aFinMaintainInstruction = ObjectUtil.clone(finMaintainInstruction);

		doWriteComponentsToBean(aFinMaintainInstruction);

		boolean isNew;
		isNew = aFinMaintainInstruction.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinMaintainInstruction.getRecordType())) {
				aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
				if (isNew) {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinMaintainInstruction.setNewRecord(true);
				}
			}
		} else {
			aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFinMaintainInstruction, tranType)) {
				refreshList();

				String msg = PennantApplicationUtil.getSavingStatus(aFinMaintainInstruction.getRoleCode(),
						aFinMaintainInstruction.getNextRoleCode(), aFinMaintainInstruction.getFinReference() + "",
						" Collateral Release ", aFinMaintainInstruction.getRecordStatus());
				if (StringUtils.equals(aFinMaintainInstruction.getRecordStatus(),
						PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Collateral Release with Reference " + aFinMaintainInstruction.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);

		if (finMaintainInstruction != null) {
			if (finMaintainInstruction.getRecordStatus() == null) {
				this.recordStatus.setValue("");
			} else {
				this.recordStatus.setValue(finMaintainInstruction.getRecordStatus());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = financeSelectCtrl.getSearchObj(true);
		financeSelectCtrl.getPagingFinanceList().setActivePage(0);
		financeSelectCtrl.getPagedListWrapper().setSearchObject(soFinanceMain);
		if (financeSelectCtrl.getListBoxFinance() != null) {
			financeSelectCtrl.getListBoxFinance().getListModel();
		}
	}

	protected boolean doProcess(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFinMaintainInstruction.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinMaintainInstruction.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinMaintainInstruction.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinMaintainInstruction.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinMaintainInstruction);
				}

				if (isNotesMandatory(taskId, aFinMaintainInstruction)) {
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

			aFinMaintainInstruction.setTaskId(taskId);
			aFinMaintainInstruction.setNextTaskId(nextTaskId);
			aFinMaintainInstruction.setRoleCode(getRole());
			aFinMaintainInstruction.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			String operationRefs = getServiceOperations(taskId, aFinMaintainInstruction);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinMaintainInstruction, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinMaintainInstruction.getBefImage(),
				aFinMaintainInstruction);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinMaintainInstruction.getUserDetails(),
				getOverideMap());
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FinMaintainInstruction aFinMaintainInstruction = (FinMaintainInstruction) aAuditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = finCollateralDelinkService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = finCollateralDelinkService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = finCollateralDelinkService.doApprove(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = finCollateralDelinkService.doReject(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CollateralDelinkDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_CollateralDelinkDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finMaintainInstruction), true);
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

	public void doWriteComponentsToBean(FinMaintainInstruction finMaintainInstruction) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = financedetail.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		finMaintainInstruction.setFinID(fm.getFinID());
		finMaintainInstruction.setFinReference(fm.getFinReference());
		finMaintainInstruction.setEvent(this.moduleCode);

		// List
		finMaintainInstruction.setCollateralAssignments(getCollateralAssignments());

		List<CollateralAssignment> colAs = getCollateralAssignments();
		for (CollateralAssignment coa : colAs) {
			finMaintainInstruction.setRecordStatus(coa.getRecordStatus());
		}

		logger.debug("Leaving");
	}

	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * Method for Filling List box with the list rendering for Assignments
	 * 
	 * @param collateralAssignments
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doFillCollateralDetails(List<CollateralAssignment> collateralAssignments, boolean fromAssignment)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");

		// ### 10-05-2018 Start Development Item 82
		BigDecimal totalBankValuation = new BigDecimal(0);
		BigDecimal balanceAssignedValue = new BigDecimal(0);
		BigDecimal totalAssignedValue = new BigDecimal(0);

		// ### 10-05-2018 End Development Item 82

		// ### 16-05-2018 Start Development Item 82
		BigDecimal totalLtv = new BigDecimal(0);
		int assignedCount = 0;
		// ### 16-05-2018 End Development Item 82

		int totCollateralCount = 0;
		BigDecimal totAssignedColValue = BigDecimal.ZERO;

		this.listBoxCollateralAssignments.getItems().clear();
		setCollateralAssignments(collateralAssignments);
		int ccyFormat = 2;
		BigDecimal loanAssignedValue = BigDecimal.ZERO;
		if (collateralAssignments != null && !collateralAssignments.isEmpty()) {

			for (CollateralAssignment collateralAssignment : collateralAssignments) {
				loanAssignedValue = loanAssignedValue
						.add(collateralAssignment.getBankValuation().multiply(collateralAssignment.getAssignPerc())
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
			}

			for (CollateralAssignment collateralAssignment : collateralAssignments) {

				ccyFormat = CurrencyUtil.getFormat(collateralAssignment.getCollateralCcy());
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(collateralAssignment.getCollateralRef());
				listitem.appendChild(listcell);
				listcell = new Listcell(collateralAssignment.getCollateralCcy());
				listitem.appendChild(listcell);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(collateralAssignment.getBankValuation(), ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);

				BigDecimal curAssignValue = (collateralAssignment.getBankValuation()
						.multiply(collateralAssignment.getAssignPerc())).divide(BigDecimal.valueOf(100), 0,
								RoundingMode.HALF_DOWN);

				listcell = new Listcell(CurrencyUtil.format(curAssignValue, ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);

				// Available Assignment value
				BigDecimal totAssignedValue = collateralAssignment.getBankValuation()
						.multiply(collateralAssignment.getTotAssignedPerc())
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				BigDecimal availAssignValue = collateralAssignment.getBankValuation().subtract(totAssignedValue)
						.subtract(curAssignValue);
				if (availAssignValue.compareTo(BigDecimal.ZERO) < 0) {
					availAssignValue = BigDecimal.ZERO;
				}

				listcell = new Listcell(CurrencyUtil.format(availAssignValue, ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);

				BigDecimal utlzedAmt = BigDecimal.ZERO;
				if (loanAssignedValue.compareTo(BigDecimal.ZERO) > 0) {
					utlzedAmt = (curAssignValue.multiply(utilizedAmount)).divide(loanAssignedValue, 0,
							RoundingMode.HALF_DOWN);
				}
				listcell = new Listcell(CurrencyUtil.format(utlzedAmt, ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);

				BigDecimal availAssignPerc = BigDecimal.ZERO;
				if (collateralAssignment.getBankValuation().compareTo(BigDecimal.ZERO) > 0) {
					availAssignPerc = availAssignValue.multiply(new BigDecimal(100))
							.divide(collateralAssignment.getBankValuation(), 2, RoundingMode.HALF_DOWN);
				}

				listcell = new Listcell(PennantApplicationUtil.formatRate(availAssignPerc.doubleValue(), 2));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);

				listcell = new Listcell(collateralAssignment.getRecordStatus());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantJavaUtil.getLabel(collateralAssignment.getRecordType()));
				listitem.appendChild(listcell);
				listitem.setAttribute("data", collateralAssignment);
				ComponentsCtrl.applyForward(listitem, "onDoubleClick=onCollateralAssignItemDoubleClicked");
				this.listBoxCollateralAssignments.appendChild(listitem);

				// ### 16-05-2018 Start Development Item 82
				if ((StringUtils.equals(collateralAssignment.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
						|| StringUtils.equals(collateralAssignment.getRecordType(),
								PennantConstants.RECORD_TYPE_CAN))) {
					logger.debug(collateralAssignment.getRecordType());
				} else {
					totCollateralCount = totCollateralCount + 1;
					totAssignedColValue = totAssignedColValue.add(curAssignValue);

					totalAssignedValue = totalAssignedValue.add(CurrencyUtil.parse(curAssignValue, ccyFormat));
					totalBankValuation = totalBankValuation
							.add(CurrencyUtil.parse(collateralAssignment.getBankValuation(), ccyFormat));
					assignedCount = assignedCount + 1;
					if (collateralAssignment.getSpecialLTV().compareTo(BigDecimal.ZERO) == 0) {
						totalLtv = totalLtv.add(collateralAssignment.getBankLTV());
					} else {
						totalLtv = totalLtv.add(collateralAssignment.getSpecialLTV());
					}
				}
				// ### 16-05-2018 End Development Item 82
			}
		}

		balanceAssignedValue = totalBankValuation.subtract(totalAssignedValue);
		if (assignedCount > 1) {
			totalLtv = totalLtv.divide(new BigDecimal(assignedCount), 2, RoundingMode.HALF_DOWN);
		}

		this.collateralCount.setValue(PennantApplicationUtil.amountFormate(loanAssignedValue, getFormat()));
		if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(this.finLTVCheck)
				&& utilizedAmount.compareTo(totAssignedColValue) > 0) {
			this.availableCollateral.setValue("Shortfall");
			this.availableCollateral.setStyle("color:red;font-weight:bold;");
		} else if (utilizedAmount.compareTo(totAssignedColValue) > 0) {
			this.availableCollateral.setValue("Shortfall");
			this.availableCollateral.setStyle("color:red;font-weight:bold;");
		} else if (totalValue.compareTo(BigDecimal.ZERO) > 0 && totalValue.compareTo(totAssignedColValue) > 0) {
			this.availableCollateral.setValue("Insufficient for Future Drawdowns");
			this.availableCollateral.setStyle("color:orange;font-weight:bold;");
		} else {
			this.availableCollateral.setValue("Available");
			this.availableCollateral.setStyle("color:Green;font-weight:bold;");
		}

		// ### 10-05-2018 Start Development Item 82

		rules.put("Collaterals_Total_Assigned", totalAssignedValue);
		rules.put("Collaterals_Total_UN_Assigned", balanceAssignedValue);
		rules.put("Collateral_Bank_Valuation", totalBankValuation);

		// ### 10-05-2018 End Development Item 82
		// ### 16-05-2018 Development Item 82
		rules.put("Collateral_Average_LTV", totalLtv);

		logger.debug("Leaving");
	}

	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("moduleName", moduleName);

			map.put("finHeaderList", headerList);
			if (isNotFinanceProcess) {
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
						this.finBasicdetails, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",
						this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {

		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(FinanceMainBaseCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<CollateralAssignment> getCollateralAssignments() {
		return collateralAssignments;
	}

	public void setCollateralAssignments(List<CollateralAssignment> collateralAssignments) {
		this.collateralAssignments = collateralAssignments;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public FinCollateralDelinkService getFinCollateralDelinkService() {
		return finCollateralDelinkService;
	}

	public void setFinCollateralDelinkService(FinCollateralDelinkService finCollateralDelinkService) {
		this.finCollateralDelinkService = finCollateralDelinkService;
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finMaintainInstruction.getFinReference());
	}
}
