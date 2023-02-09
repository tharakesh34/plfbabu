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
 * * FileName : TDSReceivableDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.tds.receivables.tdsreceivable;

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
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.tds.receivables.TdsReceivableCancelService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.tds.receivables.tdsreceivable.model.TdsReceivableCancelDetailsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

/**
 * This is the controller class for the /WEB-INF/paeges/tds.receivables/TDSReceivable/tDSReceivableDialog.zul file. <br>
 */
public class TdsReceivableCancelDialogCtrl extends GFCBaseCtrl<TdsReceivable> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TdsReceivableCancelDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TdsReceivableCancelDialog;
	protected Textbox tanNumber;
	protected Space space_CertificateNumber;
	protected Textbox certificateNumber;
	protected Datebox certificateDate;
	protected Combobox certificateQuarter;
	protected Textbox assessmentYear;
	protected Decimalbox certificateAmount;
	protected Decimalbox balanceAmount;
	protected Datebox dateOfReceipt;
	protected Listbox listbox_TdsReceivablesTxn;
	protected Grid grid_Basicdetails;
	protected Paging pagingTdsReceivablesTxnList;
	protected Button btnView;

	private TdsReceivable tdsReceivable; // overhanded per param

	// List headers
	protected Listheader listheader_AdjustmentTransactionID;
	protected Listheader listheader_AdjustmentTransactionDate;
	protected Listheader listheader_AdjustmentAmount;

	private transient TdsReceivableCancelListCtrl tdsReceivableCancelListCtrl; // overhanded
																				// per
																				// param
	private transient TdsReceivableCancelService tdsReceivableCancelService;
	private transient TdsReceivablesTxnService tdsReceivablesTxnService;
	List<TdsReceivablesTxn> tdsReceivablesTxnsList = new ArrayList<TdsReceivablesTxn>();
	private PagedListWrapper<TdsReceivablesTxn> tdsReceivablesTxnPagedListWrapper;
	private long id = 0;

	/**
	 * default constructor.<br>
	 */
	public TdsReceivableCancelDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TdsReceivableCancelDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.tdsReceivable.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_TdsReceivableCancelDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TdsReceivableCancelDialog);
		try {
			// Get the required arguments.
			this.tdsReceivable = (TdsReceivable) arguments.get("tdsReceivable");
			this.tdsReceivableCancelListCtrl = (TdsReceivableCancelListCtrl) arguments
					.get("tdsReceivableCancelListCtrl");

			this.tdsReceivablesTxnsList = this.tdsReceivable.getTdsReceivablesTxnList();
			if (this.tdsReceivable == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			TdsReceivable tdsReceivable = new TdsReceivable();
			BeanUtils.copyProperties(this.tdsReceivable, tdsReceivable);
			this.tdsReceivable.setBefImage(tdsReceivable);

			doLoadWorkFlow(this.tdsReceivable.isWorkflow(), this.tdsReceivable.getWorkflowId(),
					this.tdsReceivable.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			setTdsReceivablesTxnPagedListWrapper();
			getTdsReceivablesTxnPagedListWrapper();

			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 100 + 35;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listbox_TdsReceivablesTxn.setHeight(listboxHeight + "px");

			doShowDialog(this.tdsReceivable);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.tanNumber.setMaxlength(10);
		this.certificateNumber.setMaxlength(8);
		this.certificateDate.setFormat(PennantConstants.dateFormat);
		this.assessmentYear.setMaxlength(9);
		this.certificateAmount.setMaxlength(18);
		this.certificateAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.certificateAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.certificateAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.balanceAmount.setMaxlength(18);
		this.balanceAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.balanceAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.balanceAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.dateOfReceipt.setFormat(PennantConstants.dateFormat);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableCancelDialog_btnSave"));
		this.btnView.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableCancelDialog_btnView"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	public void onClick$btnView(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tdsReceivable", this.tdsReceivable);
		Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivableCancel/TdsReceivableCancelView.zul", null,
				map);

		logger.debug(Literal.LEAVING);
	}

	public void onClickTdsReceivablesTxn(Event event) {
		logger.debug("Entering");
		// Get the selected record.
		Listitem selectedItem = this.listbox_TdsReceivablesTxn.getSelectedItem();
		this.id = (long) selectedItem.getAttribute("txnid");

		List<TdsReceivablesTxn> tdsReceivablesTxnList = tdsReceivablesTxnService.getTdsReceivablesTxnsByTxnId(id,
				TableType.MAIN_TAB, PennantConstants.RECEIVABLE_CANCEL_MODULE);
		this.tdsReceivable.setTdsReceivablesTxnList(tdsReceivablesTxnList);
		doShowAdjustmentsDetails(this.tdsReceivable);

		logger.debug(Literal.LEAVING);
	}

	private void doShowAdjustmentsDetails(TdsReceivable tdsReceivable) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> arg = new HashMap<>();
		arg.put("tdsReceivable", tdsReceivable);
		TdsReceivablesTxn tdsReceivablesTxn = tdsReceivable.getTdsReceivablesTxnList().get(0);
		tdsReceivablesTxn.setWorkflowId(0);
		arg.put("tdsReceivablesTxn", tdsReceivablesTxn);
		arg.put("module", PennantConstants.RECEIVABLE_CANCEL_MODULE);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivablesTxn/TdsReceivablesTxnDialog.zul", null,
					arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.tdsReceivable);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		tdsReceivableCancelListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.tdsReceivable.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param tdsReceivable
	 * 
	 */
	public void doWriteBeanToComponents(TdsReceivable aTDSReceivable) {
		logger.debug(Literal.ENTERING);

		this.tanNumber.setValue(aTDSReceivable.getTanNumber());
		this.certificateNumber.setValue(aTDSReceivable.getCertificateNumber());
		this.certificateDate.setValue(aTDSReceivable.getCertificateDate());
		this.certificateAmount.setValue(PennantApplicationUtil.formateAmount(aTDSReceivable.getCertificateAmount(),
				PennantConstants.defaultCCYDecPos));
		this.assessmentYear.setValue(aTDSReceivable.getAssessmentYear());
		this.dateOfReceipt.setValue(aTDSReceivable.getDateOfReceipt());
		this.certificateQuarter.setValue(aTDSReceivable.getCertificateQuarter());
		this.balanceAmount.setValue(PennantApplicationUtil.formateAmount(aTDSReceivable.getBalanceAmount(), 2));

		this.recordStatus.setValue(aTDSReceivable.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTDSReceivable
	 */
	public void doWriteComponentsToBean(TdsReceivable aTDSReceivable) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aTDSReceivable.setTanNumber(this.tanNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Number
		try {
			aTDSReceivable.setCertificateNumber(this.certificateNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Upload On
		try {
			aTDSReceivable.setCertificateDate(this.certificateDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Amount
		try {
			if (this.certificateAmount.getValue() != null) {
				aTDSReceivable.setCertificateAmount(PennantApplicationUtil
						.unFormateAmount(this.certificateAmount.getValue(), PennantConstants.defaultCCYDecPos));
			} else {
				aTDSReceivable.setCertificateAmount(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Balance Amount
		try {
			if (this.balanceAmount.getValue() != null) {
				aTDSReceivable.setBalanceAmount(PennantApplicationUtil.unFormateAmount(this.balanceAmount.getValue(),
						PennantConstants.defaultCCYDecPos));
			} else {
				aTDSReceivable.setBalanceAmount(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Assessment Year
		try {
			aTDSReceivable.setAssessmentYear(this.assessmentYear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Date Of Receipt
		try {
			aTDSReceivable.setDateOfReceipt(this.dateOfReceipt.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Quarter
		try {
			String strCertificateQuarter = null;
			if (this.certificateQuarter.getValue() != null) {
				strCertificateQuarter = this.certificateQuarter.getValue().toString();
			}
			if (strCertificateQuarter != null && !PennantConstants.List_Select.equals(strCertificateQuarter)) {
				aTDSReceivable.setCertificateQuarter(strCertificateQuarter);

			} else {
				aTDSReceivable.setCertificateQuarter(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param tdsReceivable The entity that need to be render.
	 */
	public void doShowDialog(TdsReceivable tdsReceivable) {
		logger.debug(Literal.LEAVING);
		if (tdsReceivable.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(tdsReceivable.getRecordType())) {
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
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(tdsReceivable);
		doFillTdsReceivablesTxn(tdsReceivable.getTdsReceivablesTxnList());
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.tdsReceivable.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.certificateNumber);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.certificateNumber);

		}

		doReadOnly();

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.tdsReceivable.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		this.tanNumber.setReadonly(true);
		this.certificateNumber.setReadonly(true);
		this.certificateDate.setDisabled(true);
		this.certificateQuarter.setDisabled(true);
		this.assessmentYear.setReadonly(true);
		this.certificateAmount.setReadonly(true);
		this.balanceAmount.setReadonly(true);
		this.dateOfReceipt.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.tanNumber.setValue("");
		this.certificateNumber.setValue("");
		this.certificateDate.setText("");
		this.certificateAmount.setValue("");
		this.assessmentYear.setValue("");
		this.balanceAmount.setValue("");
		this.dateOfReceipt.setText("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final TdsReceivable aTDSReceivable = new TdsReceivable();
		BeanUtils.copyProperties(this.tdsReceivable, aTDSReceivable);
		boolean isNew = false;

		doWriteComponentsToBean(aTDSReceivable);

		isNew = aTDSReceivable.isNew();
		String tranType = "";

		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
			}
		}

		// set the cancelTDS
		aTDSReceivable.setStatus(PennantConstants.RECEIVABLE_CANCEL);

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTDSReceivable.getRecordType())) {
				aTDSReceivable.setVersion(aTDSReceivable.getVersion() + 1);
				if (isNew) {
					aTDSReceivable.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTDSReceivable.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTDSReceivable.setNewRecord(true);
				}
			}
		} else {
			aTDSReceivable.setVersion(aTDSReceivable.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aTDSReceivable, tranType)) {
				refreshList();
				// Confirmation message
				String msg = PennantApplicationUtil.getstatus(aTDSReceivable.getRoleCode(),
						aTDSReceivable.getNextRoleCode(), aTDSReceivable.getCertificateNumber(), " TDS Certificate:",
						aTDSReceivable.getRecordStatus());
				if (StringUtils.equals(aTDSReceivable.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " TDS Certificate:" + aTDSReceivable.getCertificateNumber() + " Cancelled Successfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	protected boolean doProcess(TdsReceivable aTdsReceivable, String tranType) throws Exception {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aTdsReceivable.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTdsReceivable.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTdsReceivable.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aTdsReceivable.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aTdsReceivable, finishedTasks);

			if (isNotesMandatory(taskId, aTdsReceivable)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}

			}

			auditHeader = getAuditHeader(aTdsReceivable, PennantConstants.TRAN_WF);
			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];
				TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
				setNextTaskDetails(taskId, aTdsReceivable);
				auditHeader.getAuditDetail().setModelData(tdsReceivable);
				processCompleted = doSaveProcess(auditHeader, method);

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				TdsReceivable tdsReceivables = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tdsReceivables, finishedTasks);
			}
			TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			nextTaskId = getNextTaskIds(taskId, tdsReceivable);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tdsReceivable);
					auditHeader.getAuditDetail().setModelData(tdsReceivable);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aTdsReceivable, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * @throws Exception
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TdsReceivable aTdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				{
					auditHeader = tdsReceivableCancelService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = tdsReceivableCancelService.doApprove(auditHeader);

					if (aTdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = tdsReceivableCancelService.doReject(auditHeader);
					if (aTdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_TdsReceivableCancelDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_TdsReceivableCancelDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.tdsReceivable), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * This method fills TDSReceivableDetails list
	 * 
	 * @param TDSReceivableDetails
	 */
	public void doFillTdsReceivablesTxn(List<TdsReceivablesTxn> tdsReceivablesTxn) {

		logger.debug(Literal.ENTERING);
		this.pagingTdsReceivablesTxnList.setPageSize(100);
		this.setTdsReceivablesTxnsList(tdsReceivablesTxnsList);
		getTdsReceivablesTxnPagedListWrapper().initList(tdsReceivablesTxnsList, this.listbox_TdsReceivablesTxn,
				pagingTdsReceivablesTxnList);
		this.listbox_TdsReceivablesTxn.setItemRenderer(new TdsReceivableCancelDetailsListModelItemRenderer());

		logger.debug(Literal.LEAVING);
	}

	private void setNextTaskDetails(String taskId, TdsReceivable tdsReceivable) {
		logger.debug(Literal.ENTERING);

		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(tdsReceivable.getNextTaskId());

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
			nextTaskId = getNextTaskIds(taskId, tdsReceivable);
		}

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

		tdsReceivable.setTaskId(taskId);
		tdsReceivable.setNextTaskId(nextTaskId);
		tdsReceivable.setRoleCode(getRole());
		tdsReceivable.setNextRoleCode(nextRoleCode);

		logger.debug(Literal.LEAVING);
	}

	private String getServiceTasks(String taskId, TdsReceivable aTdsReceivable, String finishedTasks) {

		logger.debug(Literal.ENTERING);
		String serviceTasks = getServiceOperations(taskId, aTdsReceivable);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}

		logger.debug(Literal.LEAVING);
		return serviceTasks;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TdsReceivable aTdsReceivable, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTdsReceivable.getBefImage(), aTdsReceivable);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aTdsReceivable.getUserDetails(),
				getOverideMap());
	}

	public void setTdsReceivablesTxnsList(List<TdsReceivablesTxn> tdsReceivablesTxnsList) {
		this.tdsReceivablesTxnsList = tdsReceivablesTxnsList;
	}

	public PagedListWrapper<TdsReceivablesTxn> getTdsReceivablesTxnPagedListWrapper() {
		return tdsReceivablesTxnPagedListWrapper;
	}

	public void setTdsReceivablesTxnPagedListWrapper(
			PagedListWrapper<TdsReceivablesTxn> tdsReceivablesTxnPagedListWrapper) {
		this.tdsReceivablesTxnPagedListWrapper = tdsReceivablesTxnPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setTdsReceivablesTxnPagedListWrapper() {
		if (this.tdsReceivablesTxnPagedListWrapper == null) {
			this.tdsReceivablesTxnPagedListWrapper = (PagedListWrapper<TdsReceivablesTxn>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public void setTdsReceivableCancelListCtrl(TdsReceivableCancelListCtrl tdsReceivableCancelListCtrl) {
		this.tdsReceivableCancelListCtrl = tdsReceivableCancelListCtrl;
	}

	public TdsReceivablesTxnService getTdsReceivablesTxnService() {
		return tdsReceivablesTxnService;
	}

	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

	public TdsReceivableCancelService getTdsReceivableCancelService() {
		return tdsReceivableCancelService;
	}

	public void setTdsReceivableCancelService(TdsReceivableCancelService tdsReceivableCancelService) {
		this.tdsReceivableCancelService = tdsReceivableCancelService;
	}

}
