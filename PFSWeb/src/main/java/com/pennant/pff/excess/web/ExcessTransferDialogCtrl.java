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
 * 
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ReceiptDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-06-2011 * * Modified
 * Date : 03-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * 29-09-2018 somasekhar 0.2 added backdate sp also, * 10-10-2018 somasekhar 0.3 Ticket
 * id:124998,defaulting receipt* purpose and excessadjustto for * closed loans * Ticket id:124998 * 13-06-2018 Siva 0.2
 * Receipt auto printing on approval * * 13-06-2018 Siva 0.3 Receipt Print Option Added * * 17-06-2018 Srinivasa Varma
 * 0.4 PSD 126950 * * 19-06-2018 Siva 0.5 Auto Receipt Number Generation * * 28-06-2018 Siva 0.6 Stop printing Receipt
 * if receipt mode status is either cancel or Bounce * *
 ********************************************************************************************
 */
package com.pennant.pff.excess.web;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennant.pff.excess.service.ExcessTransferService;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinanceMainListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExcessTransferDialogCtrl extends GFCBaseCtrl<FinExcessTransfer> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(ExcessTransferDialogCtrl.class);

	protected Window window_ExcessTransferDialog;
	protected Borderlayout borderlayout_ExcessTransfer;
	protected Label windowTitle;

	// Loan Summary Details
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox finType;

	protected Button btnSearchCustCIF;
	protected Button btnSearchFinreference;
	protected Button btnSearchReceiptInProcess;

	// Excess Transfer Details
	protected Groupbox gb_ExcessTransferDetails;
	protected Textbox transferId;
	protected Datebox transferDate;
	protected ExtendedCombobox excessReference;
	protected Combobox transferFrom;
	protected Longbox transferFromId;
	protected Combobox transferTo;
	protected Longbox transferToId;
	protected CurrencyBox transferAmount;

	private CustomerDetailsService customerDetailsService;
	private ExcessTransferService excessTransferService;
	private FinanceDetailService financeDetailService;

	protected FinanceMainListCtrl financeMainListCtrl = null;

	private FinExcessTransfer finExcessTransfer;
	private FinanceMain financeMain;
	private ExcessTransferListCtrl excessTransferListCtrl;
	private Customer customer;
	private FinExcessAmount finExcessAmount;

	public ExcessTransferDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExcessTransferDialog";
	}

	public void onCreate$window_ExcessTransferDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(window_ExcessTransferDialog);

		try {
			this.finExcessTransfer = (FinExcessTransfer) arguments.get("finExcessTransfer");
			this.moduleCode = (String) arguments.get("moduleCode");
			this.setFinanceMain((FinanceMain) arguments.get("financeMain"));
			this.setCustomer((Customer) arguments.get("customer"));
			this.excessTransferListCtrl = (ExcessTransferListCtrl) arguments.get("excessTransferListCtrl");

			if (this.finExcessTransfer == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) arguments.get("financeMain"));
			}

			FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
			BeanUtils.copyProperties(this.finExcessTransfer, finExcessTransfer);
			this.finExcessTransfer.setBefImage(finExcessTransfer);
			this.finExcessAmount = this.finExcessTransfer.getFinExcessAmount();

			doLoadWorkFlow(this.finExcessTransfer.isWorkflow(), this.finExcessTransfer.getWorkflowId(),
					this.finExcessTransfer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finExcessTransfer);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		final Map<String, Object> map = new HashMap<>();

		map.put("customerDetails", customerDetailsService.getCustomerById(financeMain.getCustID()));
		map.put("enqiryModule", true);
		map.put("dialogCtrl", this);
		map.put("newRecord", false);
		map.put("CustomerEnq", "CustomerEnq");

		Executions.createComponents(PennantAppUtil.getCustomerPageName(), null, map);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities(getRole(), super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExcessTransferDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExcessTransferDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExcessTransferDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	protected void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.transferAmount.setProperties(true, PennantConstants.defaultCCYDecPos);

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(FinExcessTransfer finExcessTransfer) {
		logger.debug(Literal.LEAVING);

		if (finExcessTransfer.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finExcessTransfer.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				btnCancel.setVisible(false);
			}
		}

		this.excessReference.setMandatoryStyle(true);
		this.transferAmount.setMandatory(true);
		doWriteBeanToComponents(finExcessTransfer);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finExcessTransfer);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finExcessTransfer.getId());
	}

	public void doEdit() {
		logger.debug(Literal.ENTERING);

		this.btnCancel.setVisible(!this.finExcessTransfer.isNewRecord());
		this.excessReference.setReadonly(isReadOnly("ExcessTransferDialog_ExcessReference"));
		this.transferDate.setDisabled(isReadOnly("ExcessTransferDialog_TransferDate"));
		this.transferFrom.setDisabled(isReadOnly("ExcessTransferDialog_TransferFrom"));
		this.transferTo.setDisabled(isReadOnly("ExcessTransferDialog_FinReference"));
		this.transferAmount.setReadonly(isReadOnly("ExcessTransferDialog_TransferAmount"));

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.finExcessTransfer.isNewRecord()) {
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

	public void doReadOnly(boolean isUserAction) {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(!isUserAction);
		this.transferDate.setDisabled(isUserAction);
		this.transferFrom.setDisabled(isUserAction);
		this.excessReference.setReadonly(isUserAction);
		this.transferTo.setDisabled(isUserAction);
		this.transferAmount.setReadonly(isUserAction);
		readOnlyComponent(isUserAction, this.transferAmount);
		this.groupboxWf.setVisible(!isUserAction);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final FinExcessTransfer transfer = new FinExcessTransfer();

		BeanUtils.copyProperties(this.finExcessTransfer, transfer);
		boolean isNew = false;

		doSetValidation();
		doSetLOVValidation();

		doWriteComponentsToBean(transfer);

		isNew = transfer.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(transfer.getRecordType())) {
				transfer.setVersion(transfer.getVersion() + 1);
				if (isNew) {
					transfer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					transfer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					transfer.setNewRecord(true);
				}
			}
		} else {
			transfer.setVersion(transfer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(transfer, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void refreshList() {
		this.excessTransferListCtrl.search();
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.transferAmount.isReadonly()) {
			this.transferAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ExcessTransferDialog_TransferAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		this.transferFrom.setConstraint("");
		this.transferAmount.setConstraint("");
		this.transferTo.setConstraint("");
		logger.debug("Leaving");
	}

	private void doSetLOVValidation() {
		logger.debug("Entering");
		if (!getComboboxValue(this.transferFrom).equals(PennantConstants.List_Select)) {
			if (!this.excessReference.isReadonly()) {
				this.excessReference.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExcessTransferDialog_ExcessReference.value"), null, true));
			}
		}

		logger.debug("Leaving");
	}

	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.excessReference.setConstraint("");
		logger.debug("Leaving");
	}

	private void doWriteBeanToComponents(FinExcessTransfer finExcessTransfer) {
		logger.debug(Literal.ENTERING);
		this.finReference.setValue(finExcessTransfer.getFinReference());
		fillComboBox(this.transferFrom, finExcessTransfer.getTransferFromType(),
				PennantStaticListUtil.getExcessTransferTypes(), "");

		if (this.finExcessTransfer.isNewRecord()) {
			this.transferId.setValue(null);
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.transferId.setValue(String.valueOf(finExcessTransfer.getId()));
			this.custCIF.setValue(finExcessTransfer.getCustCIF());
		}

		this.transferDate.setValue(finExcessTransfer.getTransferDate());
		this.excessReference.setAttribute("ExcessID", finExcessTransfer.getTransferFromId());
		if (finExcessAmount != null) {
			BigDecimal balanceAmount = PennantApplicationUtil.formateAmount(finExcessAmount.getBalanceAmt(), 2);
			this.excessReference.setValue(String.valueOf(finExcessTransfer.getTransferFromId()),
					String.valueOf(balanceAmount));
		} else {
			this.excessReference.setValue(String.valueOf(finExcessTransfer.getTransferFromId()));
		}
		this.transferAmount.setValue(CurrencyUtil.parse(finExcessTransfer.getTransferAmount(), 2));

		this.finType.setValue(financeMain.getFinType());
		this.finBranch.setValue(financeMain.getFinBranch());

		fillComboBox(this.transferTo, finExcessTransfer.getTransferToType(),
				PennantStaticListUtil.getExcessTransferTypes(), "");

		this.recordStatus.setValue(finExcessTransfer.getRecordStatus());

		if (StringUtils.equals(finExcessTransfer.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
			doReadOnly(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(FinExcessTransfer finExcessTransfer) {
		logger.debug("Entering");

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (!this.transferFrom.isDisabled()
					&& getComboboxValue(this.transferFrom).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.transferFrom, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ExcessTransferDialog_TransferFrom.value") }));
			} else {
				finExcessTransfer.setTransferFromType(getComboboxValue(transferFrom));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.transferTo.isDisabled()
					&& getComboboxValue(this.transferTo).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.transferTo, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ExcessTransferDialog_TransferTo.value") }));
			} else {
				finExcessTransfer.setTransferToType(getComboboxValue(transferTo));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finExcessTransfer.setTransferDate(this.transferDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.transferAmount.getActualValue().compareTo(BigDecimal.ZERO) == 0) {
				finExcessTransfer.setTransferAmount(BigDecimal.ZERO);
			} else {
				finExcessTransfer.setTransferAmount(
						PennantApplicationUtil.unFormateAmount(this.transferAmount.getActualValue(), 2));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.excessReference.getValue() != null) {
				finExcessTransfer.setTransferFromId(Long.parseLong(this.excessReference.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.excessReference.getValue() != null) {
				if (StringUtils.isEmpty(this.excessReference.getDescription())) {
					this.excessReference.setDescription(String.valueOf(BigDecimal.ZERO));
				}
				finExcessTransfer.setTransferToId((long) Double.parseDouble(this.excessReference.getDescription()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isNotEmpty(this.excessReference.getDescription())) {
				BigDecimal excessRefAmount = new BigDecimal(this.excessReference.getDescription());
				if (excessRefAmount.compareTo(BigDecimal.ZERO) > 0) {
					if (this.transferAmount.getActualValue().compareTo(excessRefAmount) > 0) {
						throw new WrongValueException(this.transferAmount,
								"Amount Should be less than or equals to Excess Amount.");
					}
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		finExcessTransfer.setRecordStatus(this.recordStatus.getValue());

		if (finExcessTransfer.isNewRecord()) {
			finExcessTransfer.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			finExcessTransfer.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	protected boolean doProcess(FinExcessTransfer aFinExcessTransfer, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinExcessTransfer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinExcessTransfer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinExcessTransfer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinExcessTransfer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinExcessTransfer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinExcessTransfer);
				}

				if (isNotesMandatory(taskId, aFinExcessTransfer)) {
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

			aFinExcessTransfer.setTaskId(taskId);
			aFinExcessTransfer.setNextTaskId(nextTaskId);
			aFinExcessTransfer.setRoleCode(getRole());
			aFinExcessTransfer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinExcessTransfer, tranType);
			String operationRefs = getServiceOperations(taskId, aFinExcessTransfer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinExcessTransfer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinExcessTransfer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = excessTransferService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = excessTransferService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

						((FinExcessTransfer) auditHeader.getAuditDetail().getModelData())
								.setApprovedBy(getUserWorkspace().getLoggedInUser().getUserId());

						((FinExcessTransfer) auditHeader.getAuditDetail().getModelData())
								.setApprovedOn(new Timestamp(System.currentTimeMillis()));

						auditHeader = excessTransferService.doApprove(auditHeader);

						if (finExcessTransfer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = excessTransferService.doReject(auditHeader);
						if (finExcessTransfer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ExcessTransferDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ExcessTransferDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.finExcessTransfer), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (AppException e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinExcessTransfer finExcessTransfer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finExcessTransfer.getBefImage(), finExcessTransfer);
		return new AuditHeader(getReference(), null, null, null, auditDetail, finExcessTransfer.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug(Literal.ENTERING);
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setRoleCode(getRole());
		logger.debug(Literal.LEAVING);
		return notes;
	}

	public void onChange$transferFrom(Event event) throws Exception {
		this.transferAmount.setValue(BigDecimal.ZERO);
		setExcessReference();
	}

	public void onClick$btnSearchFinreference(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		// Preparation of Finance Enquiry Data
		FinanceMain fm = financeMain;
		FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
		aFinanceEnq.setFinID(fm.getFinID());
		aFinanceEnq.setFinReference(fm.getFinReference());
		aFinanceEnq.setFinID(fm.getFinID());
		aFinanceEnq.setFinType(fm.getFinType());
		aFinanceEnq.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		aFinanceEnq.setFinCcy(fm.getFinCcy());
		aFinanceEnq.setScheduleMethod(fm.getScheduleMethod());
		// aFinanceEnq.setProfitDaysBasis(fm.getPf);
		aFinanceEnq.setFinBranch(fm.getFinBranch());
		aFinanceEnq.setLovDescFinBranchName(fm.getLovDescFinBranchName());
		aFinanceEnq.setLovDescCustCIF(fm.getCustCIF());
		aFinanceEnq.setFinIsActive(financeMain.isFinIsActive());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("moduleCode", moduleCode);
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("financeEnquiry", aFinanceEnq);
		map.put("ReceiptDialog", this);
		map.put("isModelWindow", true);
		map.put("enquiryType", "FINENQ");
		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
				this.window_ExcessTransferDialog, map);

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btn_LinkedLoan(Event event) {
		logger.debug(Literal.ENTERING);
		List<FinanceMain> financeMains = new ArrayList<>();
		List<FinanceProfitDetail> finpftDetails = new ArrayList<>();
		financeMains.addAll(financeDetailService.getFinanceMainForLinkedLoans(finReference.getValue()));

		if (CollectionUtils.isNotEmpty(financeMains)) {
			List<Long> finRefList = new ArrayList<>();
			for (FinanceMain finMain : financeMains) {
				if (StringUtils.equals(this.finReference.getValue(), finMain.getFinReference())) {
					continue;
				}
				finRefList.add(finMain.getFinID());
			}
			if (CollectionUtils.isNotEmpty(finRefList)) {
				finpftDetails.addAll(financeDetailService.getFinProfitListByFinRefList(finRefList));
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("financeMains", financeMains);
		map.put("finpftDetails", finpftDetails);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/LinkedLoansDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setExcessReference() {
		if (!getComboboxValue(this.transferFrom).equals(PennantConstants.List_Select)) {
			this.excessReference.setDescColumn("BalanceAmt");
			this.excessReference.setConstraint("");
			this.excessReference.setValue("", "");
			this.excessReference.setValueType(DataType.LONG);
			this.transferAmount.setValue(BigDecimal.ZERO);

			readOnlyComponent(false, excessReference);
			String transferFromType = getComboboxValue(transferFrom);
			Filter filter[] = new Filter[3];
			filter[0] = new Filter("FinReference", this.finReference.getValue(), Filter.OP_EQUAL);
			filter[1] = new Filter("BalanceAmt", BigDecimal.ZERO, Filter.OP_GREATER_THAN);
			filter[2] = new Filter("AmountType", transferFromType, Filter.OP_EQUAL);
			this.excessReference.setModuleName("ExcessTrf");
			this.excessReference.setValueColumn("ExcessID");
			this.excessReference.setDescColumn("BalanceAmt");
			this.excessReference.setValidateColumns(new String[] { "ExcessID" });
			this.excessReference.setFilters(filter);

			if (this.transferFrom.getValue() != null) {
				fillComboBox(this.transferTo, finExcessTransfer.getTransferToType(),
						PennantStaticListUtil.getExcessTransferTypes(), "," + transferFromType + "," + "P" + ",");
			}
		}
	}

	public void onFulfill$excessReference(Event event) {

		logger.debug("Entering " + event.toString());

		this.excessReference.setConstraint("");
		this.excessReference.clearErrorMessage();
		Clients.clearWrongValue(excessReference);
		Object dataObject = this.excessReference.getObject();

		if (dataObject instanceof String) {
			this.excessReference.setValue(dataObject.toString());
			this.excessReference.setDescription("");
		}

		FinExcessAmount fea = (FinExcessAmount) dataObject;

		if (fea != null && fea.getValueDate() != null) {
			this.transferDate.setValue(fea.getValueDate());
			this.transferDate.setDisabled(true);
		}

		logger.debug("Leaving " + event.toString());
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setExcessTransferService(ExcessTransferService excessTransferService) {
		this.excessTransferService = excessTransferService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public void setExcessTransferListCtrl(ExcessTransferListCtrl excessTransferListCtrl) {
		this.excessTransferListCtrl = excessTransferListCtrl;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setFinExcessAmount(FinExcessAmount finExcessAmount) {
		this.finExcessAmount = finExcessAmount;
	}

}