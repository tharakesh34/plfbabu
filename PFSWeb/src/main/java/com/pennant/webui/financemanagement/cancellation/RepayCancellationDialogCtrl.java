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
 * * FileName : RepayCancellationDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.cancellation;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.RepaymentCancellationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/RulesFactory/RepayCancellation/RepayCancellationDialog.zul file.
 */
public class RepayCancellationDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = -4882190027181576764L;
	private static final Logger logger = LogManager.getLogger(RepayCancellationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RepayCancellationDialog; // autowired

	protected Label finReference; // autowired
	protected Label finType; // autowired
	protected Label custId; // autowired
	protected Label finBranch; // autowired
	protected Label postDate; // autowired
	protected Label rpyAmount; // autowired

	protected Grid grid_Basicdetails; // autoWired
	protected Listbox listBoxRepayDetail;

	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;

	// not auto wired vars
	private transient FinanceSelectCtrl financeSelectCtrl; // overhanded per param
	private FinanceDetail financeDetail = null;
	private FinanceMain financeMain = null;

	private transient boolean validationOn;

	protected Button btnHelp; // autowire
	protected Button btnRepayCancel;

	// ServiceDAOs / Domain Classes
	private transient RepaymentCancellationService repaymentCancellationService;
	int listRows;
	protected Listbox listBoxCancelRepayPosting;
	protected Tab postingDetailsTab;
	private boolean isManualPostingReversal = false;

	/**
	 * default constructor.<br>
	 */
	public RepayCancellationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RepayCancelDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected RepayCancellation object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_RepayCancellationDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RepayCancellationDialog);

		try {
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				FinanceMain befImage = new FinanceMain();
				setFinanceMain(getFinanceDetail().getFinScheduleData().getFinanceMain());
				BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
				setFinanceDetail(getFinanceDetail());
			}

			// READ OVERHANDED params !
			// we get the financeMainListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeMain here.

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "RepayCancelDialog", menuItemRightName);
			} else {
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RepayCancellationDialog.onClose();
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
		getUserWorkspace().allocateAuthorities("RepayCancelDialog", getRole(), menuItemRightName);
		this.btnRepayCancel.setVisible(getUserWorkspace().isAllowed("button_RepayCancelDialog_btnSave"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		String fontStyle = "font-weight:bold;";

		// Empty sent any required attributes
		this.finReference.setStyle(fontStyle);
		this.finType.setStyle(fontStyle);
		this.custId.setStyle(fontStyle);
		this.finBranch.setStyle(fontStyle);
		this.postDate.setStyle(fontStyle);
		this.rpyAmount.setStyle(fontStyle);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnRepayCancel(Event event) {
		logger.debug("Entering" + event.toString());

		boolean recSave = false;
		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
				recSave = true;
			}
		}

		if (isManualPostingReversal && !recSave) {
			final String msg = Labels.getLabel("message.Question.Are_you_sure_todo_Manual_Reversal_Postings") + "\n";

			MessageUtil.confirm(msg, evnt -> {
				if (Messagebox.ON_YES.equals(evnt.getName())) {
					doSave();
				}
			});
		} else {
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRepayCancellation (RepayCancellation)
	 */
	public void doWriteBeanToComponents(FinanceDetail financeDetail) {
		logger.debug("Entering");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		this.finReference.setValue(financeMain.getFinReference());
		this.finType.setValue(financeMain.getFinType() + "-" + financeMain.getLovDescFinTypeName());
		this.custId.setValue(financeMain.getLovDescCustCIF() + "-" + financeMain.getLovDescCustShrtName());
		this.finBranch.setValue(financeMain.getFinBranch() + "-" + financeMain.getLovDescFinBranchName());

		List<FinanceRepayments> repayList = financeDetail.getFinScheduleData().getRepayDetails();
		if (repayList != null && repayList.size() > 0) {

			this.postDate.setValue(DateUtil.formatToLongDate(repayList.get(0).getFinPostDate()));
			this.rpyAmount.setValue(CurrencyUtil.format(repayList.get(0).getFinRpyAmount(), format));
			doFilllistbox(repayList);

			// Posting Details
			if (repayList.get(0).getLinkedTranId() != 0) {
				doFillPostingdetails(repayList.get(0).getLinkedTranId());
			} else {
				this.postingDetailsTab.setVisible(false);
				isManualPostingReversal = true;
			}
		}

		this.recordStatus.setValue(financeMain.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (StringUtils.isNotBlank(afinanceDetail.getFinScheduleData().getFinanceMain().getRecordType())) {
			this.btnNotes.setVisible(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail);
			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RepayCancellationDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	public void doFilllistbox(List<FinanceRepayments> repayList) {
		logger.debug("Entering");

		if (repayList != null && repayList.size() > 0) {

			int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
			Listitem item = null;
			String sclass = "text-align:right;";

			for (int i = 0; i < repayList.size(); i++) {

				FinanceRepayments repay = repayList.get(i);

				Listcell lc;
				item = new Listitem();

				lc = new Listcell(DateUtil.formatToLongDate(repay.getFinSchdDate()));
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(repay.getFinSchdPriPaid(), formatter));
				lc.setStyle(sclass);
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(repay.getFinSchdPftPaid(), formatter));
				lc.setStyle(sclass);
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(repay.getFinTotSchdPaid(), formatter));
				lc.setStyle(sclass);
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(repay.getFinFee(), formatter));
				lc.setStyle(sclass);
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(repay.getFinWaiver(), formatter));
				lc.setStyle(sclass);
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(repay.getFinRefund(), formatter));
				lc.setStyle(sclass);
				lc.setParent(item);

				this.listBoxRepayDetail.appendChild(item);

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		// doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for User
				if (StringUtils.isNotBlank(aFinanceMain.getNextTaskId()) && !StringUtils
						.trimToEmpty(aFinanceMain.getNextRoleCode()).equals(aFinanceMain.getRoleCode())) {
					// getMailUtil().sendMail(1, PennantConstants.TEMPLATE_FOR_AE, aFinanceMain);
				}

				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {

				} else {
					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),
						finishedTasks);

			}

			FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				auditHeader = getRepaymentCancellationService().saveOrUpdate(auditHeader);

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getRepaymentCancellationService().doApprove(auditHeader);

					if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getRepaymentCancellationService().doReject(auditHeader);
					if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_RepayCancellationDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_RepayCancellationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.financeMain), true);
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
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.financeMain);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeMain.getFinReference());
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	private void doFillPostingdetails(long linkedTranId) {
		logger.debug("Entering");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<ReturnDataSet> jdbcSearchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		jdbcSearchObject.addTabelName("Postings");
		jdbcSearchObject.addFilterEqual("linkedTranId", linkedTranId);
		List<ReturnDataSet> postingList = pagedListService.getBySearchObject(jdbcSearchObject);
		if (postingList != null && !postingList.isEmpty()) {
			Listitem item;
			for (ReturnDataSet returnDataSet : postingList) {
				item = new Listitem();
				Listcell lc = new Listcell();
				if (returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
					lc = new Listcell(Labels.getLabel("common.Debit"));
				} else if (returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
					lc = new Listcell(Labels.getLabel("common.Credit"));
				}
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranDesc());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getRevTranCode());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranCode());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getAcCcy());
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(returnDataSet.getPostAmount(),
						CurrencyUtil.getFormat(financeMain.getFinCcy())));
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setParent(item);
				this.listBoxCancelRepayPosting.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public RepaymentCancellationService getRepaymentCancellationService() {
		return repaymentCancellationService;
	}

	public void setRepaymentCancellationService(RepaymentCancellationService repaymentCancellationService) {
		this.repaymentCancellationService = repaymentCancellationService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}