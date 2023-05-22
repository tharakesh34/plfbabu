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
 * * FileName : FinanceFlagsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.financeFlags;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceFlagsService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FinanceFlagsDialogCtrl extends GFCBaseCtrl<FinanceFlag> {

	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(FinanceFlagsDialogCtrl.class);

	protected Window window_FinanceFlagsDialog; // autoWired
	protected Borderlayout borderlayoutFinanceFlags; // autoWired
	protected Button btnNew_FinFlagsDetail; // autoWired
	protected Listbox listBoxFinanceFlags; // autoWired
	protected ExtendedCombobox finReference;

	private List<FinFlagsDetail> finFlagsDetailList = new ArrayList<FinFlagsDetail>();
	private transient List<ValueLabel> finFlagsCompList = PennantAppUtil.getFlagDetails();

	protected Grid grid_finflags;
	protected Label finFlags_finType;
	protected Label finFlags_finReference;
	protected Label finFlags_finCcy;
	protected Label finFlags_profitDaysBasis;
	protected Label finFlags_noOfTerms;
	protected Label finFlags_grcEndDate;
	protected Label finFlags_startDate;
	protected Label finFlags_maturityDate;
	protected Decimalbox finFlags_purchasePrice;
	protected Decimalbox finFlags_otherExp;
	protected Decimalbox finFlags_totalCost;
	protected Decimalbox finFlags_totalPft;
	protected Decimalbox finFlags_contractPrice;
	public Label finFlags_effRate;

	protected Label label_FinanceFlagsDialog_FinType;
	protected Label label_FinanceFlagsDialog_FinReference;
	protected Label label_FinanceFlagsDialog_FinCcy;
	protected Label label_FinanceFlagsDialog_ProfitDaysBasis;
	protected Label label_FinanceFlagsDialog_NoOfTerms;
	protected Label label_FinanceFlagsDialog_GrcEndDate;
	protected Label label_FinanceFlagsDialog_StartDate;
	protected Label label_FinanceFlagsDialog_MaturityDate;
	protected Label label_FinanceFlagsDialog_PurchasePrice;
	protected Label label_FinanceFlagsDialog_OthExpenses;
	protected Label label_FinanceFlagsDialog_TotalCost;
	protected Label label_FinanceFlagsDialog_TotalPft;
	protected Label label_FinanceFlagsDialog_ContractPrice;
	protected Label label_FinanceFlagsDialog_EffectiveRateOfReturn;

	protected Row row1;
	protected Row row2;
	protected Row row3;
	protected Row row4;
	protected Row row5;
	protected Row row6;

	protected transient String oldVar_finReference;

	private boolean enqModule = false;
	private transient FinanceFlagsListCtrl financeFlagsListCtrl;
	private FinanceFlag financeFlag;
	private FinanceFlagsService financeFlagsService;
	private PagedListWrapper<FinanceFlag> finFlagslistDetailPagedListWrapper;
	private PagedListService pagedListService;
	private String tempflagcode = "";
	private FinanceWorkFlowService financeWorkFlowService;
	private FinanceMain financeMain;

	/**
	 * default constructor.<br>
	 */
	public FinanceFlagsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FianceFlagsDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceFlags object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinanceFlagsDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceFlagsDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("financeFlag")) {
				this.financeFlag = (FinanceFlag) arguments.get("financeFlag");
				FinanceFlag befImage = new FinanceFlag();
				BeanUtils.copyProperties(this.financeFlag, befImage);
				this.financeFlag.setBefImage(befImage);
				setFinanceFlag(this.financeFlag);
			} else {
				setFinanceFlag(null);
			}

			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
			}

			if (!getFinanceFlag().isNewRecord()) {
				doLoadWorkFlow(this.financeFlag.isWorkflow(), this.financeFlag.getWorkflowId(),
						this.financeFlag.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FianceFlagsDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(true);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			// we get the FinanceFlags controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete FinanceFlagsList here.
			if (arguments.containsKey("financeFlagsListCtrl")) {
				setFinanceFlagsListCtrl((FinanceFlagsListCtrl) arguments.get("financeFlagsListCtrl"));
			} else {
				setFinanceFlagsListCtrl(null);
			}

			doSetFieldProperties();
			this.listBoxFinanceFlags.setHeight(getListBoxHeight(10));
			if (getFinanceFlag().isNewRecord()) {
				fillfinflags(financeMain);
			} else {
				doShowDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceFlagsDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
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
		getUserWorkspace().allocateAuthorities("FinanceFlagsDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceFlagsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceFlagsDialog_btnEdit"));
		// this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceFlagsDialog_btnDelete"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceFlagsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnNew_FinFlagsDetail
				.setVisible(getUserWorkspace().isAllowed("button_FinanceFlagsDialog_btnNew_FinFlagsDetail"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		if (!getFinanceFlag().isNewRecord()) {
			if (isWorkFlowEnabled()) {
				this.groupboxWf.setVisible(true);
			} else {
				this.groupboxWf.setVisible(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
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

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinanceFlag afinanceFlags = new FinanceFlag();
		BeanUtils.copyProperties(getFinanceFlag(), afinanceFlags);

		String keyReference = Labels.getLabel("label_FinanceFlagsDialog_finReference.value") + " : "
				+ afinanceFlags.getFinReference();

		doDelete(keyReference, afinanceFlags);

		logger.debug(Literal.LEAVING);
	}

	// ****************************************************************
	// ********************** Components events ***********************
	// ****************************************************************

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	protected void doSave() {
		logger.debug("Entering");

		final FinanceFlag aFinanceFlags = new FinanceFlag();
		BeanUtils.copyProperties(getFinanceFlag(), aFinanceFlags);
		boolean isNew = false;
		if (isWorkFlowEnabled()) {
			aFinanceFlags.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinanceFlags.getNextTaskId(), aFinanceFlags);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFinanceFlags.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the FinanceFlags object with the components data
			doWriteComponentsToBean(aFinanceFlags);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aFinanceFlags.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceFlags.getRecordType())) {
				aFinanceFlags.setVersion(aFinanceFlags.getVersion() + 1);
				if (isNew) {
					aFinanceFlags.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceFlags.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceFlags.setNewRecord(true);
				}
			}
		} else {
			aFinanceFlags.setVersion(aFinanceFlags.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinanceFlags, tranType)) {
				refreshList();
				closeDialog();
				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceFlags.getNextTaskId())) {
					aFinanceFlags.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceFlags.getRoleCode(),
						aFinanceFlags.getNextRoleCode(), aFinanceFlags.getFinReference(), " Finance ",
						aFinanceFlags.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		// User Notifications Message/Alert
		publishNotification(Notify.ROLE, aFinanceFlags.getFinReference(), aFinanceFlags);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceFlags aFinanceFlags
	 */
	private void doWriteComponentsToBean(FinanceFlag aFinanceFlags) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// FinReference
		try {
			aFinanceFlags.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.listBoxFinanceFlags.getVisibleItemCount() == 0) {
				throw new WrongValueException(this.btnNew_FinFlagsDetail,
						Labels.getLabel("label_FinFlagsDetailList_Mandatory"));
			}
			aFinanceFlags.setFinFlagDetailList(finFlagsDetailList);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceFlagsDialog_finReference.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		Clients.clearWrongValue(this.btnNew_FinFlagsDetail);
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinanceFlag aFinanceFlags, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aFinanceFlags.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceFlags.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceFlags.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aFinanceFlags.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aFinanceFlags, finishedTasks);

			if (isNotesMandatory(taskId, aFinanceFlags)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceFlags, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getFinanceFlagsService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.isNotEmpty(FinServiceEvent.FINFLAGS)
								&& StringUtils.equals(FinServiceEvent.FINFLAGS, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
					if (isScheduleModify) {
						aFinanceFlags.setScheduleChange(true);
					} else {
						aFinanceFlags.setScheduleChange(false);
					}
				} else {
					FinanceFlag tFinanceFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aFinanceFlags);
					auditHeader.getAuditDetail().setModelData(tFinanceFlag);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceFlag tFinanceFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceFlag, finishedTasks);

			}

			FinanceFlag tFinanceFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceFlag);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aFinanceFlags);
					auditHeader.getAuditDetail().setModelData(tFinanceFlag);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aFinanceFlags, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	protected String getServiceTasks(String taskId, FinanceFlag financeFlag, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(financeFlag.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, financeFlag);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, FinanceFlag financeFlag) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeFlag.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeFlag);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		financeFlag.setTaskId(taskId);
		financeFlag.setNextTaskId(nextTaskId);
		financeFlag.setRoleCode(getRole());
		financeFlag.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceFlag aFinanceFlags = (FinanceFlag) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getFinanceFlagsService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getFinanceFlagsService().saveOrUpdate(auditHeader);
				}

			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getFinanceFlagsService().doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aFinanceFlags.getRecordType())) {
						deleteNotes = true;
					}

				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getFinanceFlagsService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aFinanceFlags.getRecordType())) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinanceFlagsDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_FinanceFlagsDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes("FinanceFlags", aFinanceFlags.getFinReference(), aFinanceFlags.getVersion()),
							true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceFlags
	 */
	private void doShowDialog() {
		logger.debug("Entering");

		try {
			FinanceFlag aFinanceFlags = getFinanceFlag();

			doWriteBeanToComponents(aFinanceFlags);
			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aFinanceFlags.isNewRecord()));

			// fill the components with the data
			if (aFinanceFlags.getFinFlagDetailList() != null && aFinanceFlags.getFinFlagDetailList().size() >= 0) {
				doFillFinFlagsList(aFinanceFlags.getFinFlagDetailList());
			}

			this.listBoxFinanceFlags
					.setHeight(getListBoxHeight(this.grid_finflags.getRows().getVisibleItemCount() + 3));

			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceFlagsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void displayComponents(int mode) {
		logger.debug("Entering");
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.finReference, this.finReference));

		if (getFinanceFlag().isNewRecord()) {
			readOnlyComponent(false, this.finReference);
		} else {
			readOnlyComponent(true, this.finReference);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");
		readOnlyComponent(false, this.finReference);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceFlags aFinanceFlags
	 */

	private void doWriteBeanToComponents(FinanceFlag aFinanceFlags) {
		logger.debug("Entering");
		this.finReference.setValue(aFinanceFlags.getFinReference());
		if (!aFinanceFlags.isNewRecord()) {
			doSetLabels(aFinanceFlags);
		}
		this.recordStatus.setValue(aFinanceFlags.getRecordStatus());
		logger.debug("Leaving");
	}

	public void fillfinflags(FinanceMain details) {
		logger.debug("Entering");
		Clients.clearWrongValue(this.btnNew_FinFlagsDetail);
		this.row1.setVisible(false);
		this.row2.setVisible(false);
		this.row3.setVisible(false);
		this.row4.setVisible(false);
		this.row5.setVisible(false);
		this.row6.setVisible(false);
		this.finFlags_finType.setVisible(false);
		label_FinanceFlagsDialog_FinType.setVisible(false);

		this.finReference.setValue(details.getFinReference());
		getFinanceFlag().setFinReference(details.getFinReference());
		doSetLabels(details);

		// Workflow Details
		setWorkflowDetails(details.getFinType());
		getFinanceFlag().setWorkflowId(getWorkFlowId());
		doLoadWorkFlow(this.financeFlag.isWorkflow(), this.financeFlag.getWorkflowId(),
				this.financeFlag.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (getFinanceFlag().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
			this.groupboxWf.setVisible(true);
			doShowDialog();
		}
		this.finReference.setReadonly(true);

		logger.debug("Leaving");
	}

	private void setWorkflowDetails(String finType) {

		// Finance Maintenance Workflow Check & Assignment
		WorkFlowDetails workFlowDetails = null;
		if (StringUtils.isNotEmpty(FinServiceEvent.FINFLAGS)) {
			FinanceWorkFlow financeWorkflow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(finType,
					FinServiceEvent.FINFLAGS, PennantConstants.WORFLOW_MODULE_FINANCE);// TODO: Check Promotion case
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	/**
	 * Method for Reset Finance Main data
	 * 
	 * @param FinanceMain
	 */
	private void doSetLabels(FinanceFlag financeFlag) {
		logger.debug("Entering");

		if (StringUtils.isNotEmpty(financeFlag.getFinReference())) {
			String product = financeFlag.getFinCategory();

			this.row1.setVisible(true);
			this.row2.setVisible(true);
			this.row3.setVisible(true);
			this.row4.setVisible(true);
			this.row5.setVisible(true);
			this.row6.setVisible(true);
			this.finFlags_finType.setVisible(true);
			label_FinanceFlagsDialog_FinType.setVisible(true);

			int ccyFormatter = CurrencyUtil.getFormat(financeFlag.getFinCcy());
			this.finFlags_finType.setValue(financeFlag.getFinType() + " - " + financeFlag.getFinTypeDesc());
			this.finFlags_finCcy
					.setValue(financeFlag.getFinCcy() + " - " + CurrencyUtil.getCcyDesc(financeFlag.getFinCcy()));
			this.finFlags_purchasePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_otherExp.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_totalCost.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_totalPft.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_contractPrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_noOfTerms
					.setValue(String.valueOf(financeFlag.getNumberOfTerms() + financeFlag.getGraceTerms()));
			this.finFlags_startDate.setValue(DateUtil.formatToLongDate(financeFlag.getFinStartDate()));
			this.finFlags_maturityDate.setValue(DateUtil.formatToLongDate(financeFlag.getMaturityDate()));
			this.finFlags_purchasePrice.setValue(CurrencyUtil.parse(financeFlag.getFinAmount(), ccyFormatter));
			this.finFlags_otherExp.setValue(CurrencyUtil.parse(financeFlag.getFeeChargeAmt(), ccyFormatter));
			this.finFlags_totalCost.setValue(CurrencyUtil.parse(
					financeFlag.getFinAmount().subtract(financeFlag.getDownPaySupl()).add(
							financeFlag.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeFlag.getFeeChargeAmt()),
					ccyFormatter));
			this.finFlags_totalPft.setValue(CurrencyUtil.parse(financeFlag.getTotalProfit(), ccyFormatter));
			this.finFlags_contractPrice
					.setValue(CurrencyUtil.parse(
							financeFlag.getFinAmount().subtract(financeFlag.getDownPaySupl())
									.add(financeFlag.getFeeChargeAmt() == null ? BigDecimal.ZERO
											: financeFlag.getFeeChargeAmt())
									.add(financeFlag.getTotalProfit()),
							ccyFormatter));

			if (financeFlag.getEffectiveRateOfReturn() == null) {
				financeFlag.setEffectiveRateOfReturn(BigDecimal.ZERO);
			}
			this.finFlags_effRate
					.setValue(PennantApplicationUtil.formatRate(financeFlag.getEffectiveRateOfReturn().doubleValue(),
							PennantConstants.rateFormate) + "%");

			String productType = (product.substring(0, 1)).toUpperCase() + (product.substring(1)).toLowerCase();
			label_FinanceFlagsDialog_FinType
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinType.value"));
			label_FinanceFlagsDialog_FinCcy
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinCcy.value"));
			label_FinanceFlagsDialog_ProfitDaysBasis
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_ProfitDaysBasis.value"));
			label_FinanceFlagsDialog_NoOfTerms
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_NumberOfTerms.value"));
			label_FinanceFlagsDialog_StartDate
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinStartDate.value"));
			label_FinanceFlagsDialog_MaturityDate
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinMaturityDate.value"));
			label_FinanceFlagsDialog_PurchasePrice
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_PurchasePrice.value"));
			label_FinanceFlagsDialog_OthExpenses
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_OthExpenses.value"));
			label_FinanceFlagsDialog_TotalCost
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_TotalCost.value"));
			label_FinanceFlagsDialog_TotalPft
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_TotalPft.value"));
			label_FinanceFlagsDialog_ContractPrice
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_ContractPrice.value"));
			label_FinanceFlagsDialog_EffectiveRateOfReturn.setValue(
					Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_EffectiveRateOfReturn.value"));
		}

		recordStatus.setValue(financeFlag.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Method for Reset Finance Main data
	 * 
	 * @param FinanceMain
	 */
	private void doSetLabels(FinanceMain financeMain) {
		logger.debug("Entering");

		if (StringUtils.isNotEmpty(financeMain.getFinReference())) {
			String product = financeMain.getLovDescProductCodeName();

			this.row1.setVisible(true);
			this.row2.setVisible(true);
			this.row3.setVisible(true);
			this.row4.setVisible(true);
			this.row5.setVisible(true);
			this.row6.setVisible(true);
			this.finFlags_finType.setVisible(true);
			label_FinanceFlagsDialog_FinType.setVisible(true);

			int ccyFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
			this.finFlags_finType.setValue(financeMain.getFinType() + " - " + financeMain.getLovDescFinTypeName());
			this.finFlags_finCcy.setValue(financeMain.getFinCcy());
			this.finFlags_purchasePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_otherExp.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_totalCost.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_totalPft.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_contractPrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.finFlags_noOfTerms
					.setValue(String.valueOf(financeMain.getNumberOfTerms() + financeMain.getGraceTerms()));
			this.finFlags_startDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
			this.finFlags_maturityDate.setValue(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
			this.finFlags_purchasePrice.setValue(CurrencyUtil.parse(financeMain.getFinAmount(), ccyFormatter));
			this.finFlags_otherExp.setValue(CurrencyUtil.parse(financeMain.getFeeChargeAmt(), ccyFormatter));
			this.finFlags_totalCost.setValue(CurrencyUtil.parse(
					financeMain.getFinAmount().subtract(financeMain.getDownPaySupl()).add(
							financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeMain.getFeeChargeAmt()),
					ccyFormatter));
			this.finFlags_totalPft.setValue(CurrencyUtil.parse(financeMain.getTotalProfit(), ccyFormatter));
			this.finFlags_contractPrice
					.setValue(CurrencyUtil.parse(
							financeMain.getFinAmount().subtract(financeMain.getDownPaySupl())
									.add(financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO
											: financeMain.getFeeChargeAmt())
									.add(financeMain.getTotalProfit()),
							ccyFormatter));

			if (financeMain.getEffectiveRateOfReturn() == null) {
				financeMain.setEffectiveRateOfReturn(BigDecimal.ZERO);
			}
			this.finFlags_effRate
					.setValue(PennantApplicationUtil.formatRate(financeMain.getEffectiveRateOfReturn().doubleValue(),
							PennantConstants.rateFormate) + "%");
			this.finFlags_profitDaysBasis.setValue(financeMain.getProfitDaysBasis());

			String productType = (product.substring(0, 1)).toUpperCase() + (product.substring(1)).toLowerCase();
			label_FinanceFlagsDialog_FinType
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinType.value"));
			label_FinanceFlagsDialog_FinCcy
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinCcy.value"));
			label_FinanceFlagsDialog_ProfitDaysBasis
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_ProfitDaysBasis.value"));
			label_FinanceFlagsDialog_NoOfTerms
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_NumberOfTerms.value"));
			label_FinanceFlagsDialog_StartDate
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinStartDate.value"));
			label_FinanceFlagsDialog_MaturityDate
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinMaturityDate.value"));
			label_FinanceFlagsDialog_PurchasePrice
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_PurchasePrice.value"));
			label_FinanceFlagsDialog_OthExpenses
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_OthExpenses.value"));
			label_FinanceFlagsDialog_TotalCost
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_TotalCost.value"));
			label_FinanceFlagsDialog_TotalPft
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_TotalPft.value"));
			label_FinanceFlagsDialog_ContractPrice
					.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_ContractPrice.value"));
			label_FinanceFlagsDialog_EffectiveRateOfReturn.setValue(
					Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_EffectiveRateOfReturn.value"));
		}

		recordStatus.setValue(financeMain.getRecordStatus());
		logger.debug("Leaving");
	}

	// *******************************************************************//
	// ***************** New Button Event for FlagsDetail List ***********//
	// *******************************************************************//

	// Finance Flags Details
	public void onClick$btnNew_FinFlagsDetail(Event event) {
		logger.debug("Entering" + event.toString());
		doClearMessage();
		Clients.clearWrongValue(listBoxFinanceFlags);
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceFlagsDialog, "Flag", tempflagcode,
				filter);
		if (dataObject != null) {
			String details = (String) dataObject;
			tempflagcode = details;
		}
		doRenderList(Arrays.asList(tempflagcode.split(",")));
		logger.debug("Leaving  " + event.toString());
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param finFlagsDetailList
	 */
	private void doRenderList(List<String> finFlagList) {
		logger.debug("Entering");

		this.finFlagsDetailList.clear();
		this.listBoxFinanceFlags.getItems().clear();
		for (String flagCode : finFlagList) {

			String flagDesc = PennantApplicationUtil.getLabelDesc(flagCode, finFlagsCompList);
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(flagCode);
			lc.setParent(item);
			lc = new Listcell(flagDesc);
			lc.setParent(item);
			if (!StringUtils.isEmpty(flagCode)) {
				this.listBoxFinanceFlags.appendChild(item);
			}
			// List Details preparation
			FinFlagsDetail afinFlagsDetail = new FinFlagsDetail();
			afinFlagsDetail.setFlagCode(flagCode);
			afinFlagsDetail.setFlagDesc(flagDesc);
			this.finFlagsDetailList.add(afinFlagsDetail);
		}
		financeFlag.setFinFlagDetailList(finFlagsDetailList);
		logger.debug("Leaving");
	}

	/**
	 * Method Used for set list of values been class to components finance flags list
	 * 
	 * @param FinanceMain
	 */
	private void doFillFinFlagsList(List<FinFlagsDetail> finFlagsDetailList) {
		logger.debug("Entering");
		this.listBoxFinanceFlags.getItems().clear();

		List<String> tempfinFlagsList = new ArrayList<String>();
		for (FinFlagsDetail finFlagsDetail : finFlagsDetailList) {
			tempfinFlagsList.add(finFlagsDetail.getFlagCode());
			if (StringUtils.isEmpty(tempflagcode)) {
				tempflagcode = finFlagsDetail.getFlagCode();
			} else {
				tempflagcode = tempflagcode.concat(",").concat(finFlagsDetail.getFlagCode());
			}
		}
		doRenderList(tempfinFlagsList);
		logger.debug("Entering");
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
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getFinanceFlagsListCtrl().search();
	}

	// ******************************************************//
	// ***************** WorkFlow Components ****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(FinanceFlag aFinanceFlags, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceFlags.getBefImage(), aFinanceFlags);
		return new AuditHeader(aFinanceFlags.getFinReference(), null, null, null, auditDetail,
				aFinanceFlags.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.financeFlag);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return String.valueOf(getFinanceFlag().getFinReference());
	}

	// ******************************************************//
	// ******************* getter / setter ******************//
	// ******************************************************//

	public FinanceFlagsService getFinanceFlagsService() {
		return financeFlagsService;
	}

	public void setFinanceFlagsService(FinanceFlagsService financeFlagsService) {
		this.financeFlagsService = financeFlagsService;
	}

	public FinanceFlagsListCtrl getFinanceFlagsListCtrl() {
		return financeFlagsListCtrl;
	}

	public void setFinanceFlagsListCtrl(FinanceFlagsListCtrl financeFlagsListCtrl) {
		this.financeFlagsListCtrl = financeFlagsListCtrl;
	}

	@SuppressWarnings("unchecked")
	public void setFinFlagslistDetailPagedListWrapper() {
		if (this.finFlagslistDetailPagedListWrapper == null) {
			this.finFlagslistDetailPagedListWrapper = (PagedListWrapper<FinanceFlag>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<FinanceFlag> getFinFlagslistDetailPagedListWrapper() {
		return finFlagslistDetailPagedListWrapper;
	}

	public FinanceFlag getFinanceFlag() {
		return financeFlag;
	}

	public void setFinanceFlag(FinanceFlag financeFlag) {
		this.financeFlag = financeFlag;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

}
