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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : FeeReceiptDialogCtrl.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 19-12-2019
 * 
 * Modified Date : 19-12-2019
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2019 Ganesh.P 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PrvsFinFeeRefund;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeRefundService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/FinFeeRefundDialog.zul
 */
public class FinFeeRefundDialogCtrl extends GFCBaseCtrl<FinFeeRefundHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(FinFeeRefundDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinFeeRefundDialog;
	protected Borderlayout borderlayout_FinFeeRefund;

	// Receipt Details
	protected ExtendedCombobox finType;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox finCcy;
	protected ExtendedCombobox finBranch;
	protected Textbox custCIF;
	protected Textbox custName;
	protected Groupbox gb_FeeDetail;
	protected Listbox listBoxFeeDetail;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	private FinFeeRefundService finFeeRefundService;
	private FinanceDetail financeDetail;

	// Buttons
	protected Button btnReceipt;

	protected transient FinFeeRefundListCtrl finFeeRefundListCtrl = null;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private FinFeeRefundHeader feeRefundHeader = null;
	private transient FeeReceiptService feeReceiptService;
	private AccountingSetService accountingSetService;
	private AccountEngineExecution engineExecution;

	private List<FinFeeDetail> paidFeeList = new ArrayList<FinFeeDetail>();
	private long custID;
	private Map<String, BigDecimal> taxPercentages;

	// Mapping Fields.
	private final String FINFEEREFUNDDETAIL = "FINFEEREFUNDDETAIL";
	private final String PRVFINFEEREFUNDDETAIL = "PRVFINFEEREFUNDDETAIL";
	private final String ALLOCATED_AMOUNT = "ALLOCATED_AMOUNT";
	private final String ALLOCATED_AMT_GST = "ALLOCATED_AMT_GST";
	private final String ALLOCATED_AMT_TDS = "ALLOCATED_AMT_TDS";
	private final String ALLOCATES_AMT_TOTAL = "ALLOCATES_AMT_TOTAL";
	private final String FINFEEDETAIL = "FINFEEDETAIL";

	private static final String FORMATTER = "FORMATTER";
	private static final String LISTITEM_SUMMARY = "LISTITEM_SUMMARY";
	private List<FinFeeReceipt> currentFinReceipts = new ArrayList<FinFeeReceipt>();
	private List<FinFeeReceipt> oldFinReceipts = new ArrayList<FinFeeReceipt>();

	private static BigDecimal tdsPerc = null;
	private static String tdsRoundMode = null;
	private static int tdsRoundingTarget = 0;
	private String taxRoundMode = null;
	private int taxRoundingTarget = 0;
	private boolean isFinTDSApplicable = false;

	protected Listheader listheader_FinFeeRefundList_PaidTDS;
	protected Listheader listheader_FinFeeRefundList_TotPrvsRefundTDS;
	protected Listheader listheader_FinFeeRefundList_AllocatedRefundTDS;
	private FinanceDetailService financeDetailService;

	/**
	 * default constructor.<br>
	 */
	public FinFeeRefundDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinFeeRefundDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinFeeRefundDialog(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinFeeRefundDialog);
		try {
			if (arguments.containsKey("finFeeRefundHeader")) {
				feeRefundHeader = (FinFeeRefundHeader) arguments.get("finFeeRefundHeader");

				FinFeeRefundHeader befImage = ObjectUtil.clone(feeRefundHeader);
				feeRefundHeader.setBefImage(befImage);

			}

			if (arguments.containsKey("finFeeRefundsListCtrl")) {
				finFeeRefundListCtrl = (FinFeeRefundListCtrl) arguments.get("finFeeRefundsListCtrl");
			}

			doLoadWorkFlow(feeRefundHeader.isWorkflow(), feeRefundHeader.getWorkflowId(),
					feeRefundHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.feeRefundHeader);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinFeeRefundDialog.onClose();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		// Empty sent any required attributes
		this.finReference.setModuleName("FeeRefundFinanceMain");
		this.finReference.setMandatoryStyle(true);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setWhereClause(" FinReference Not In (Select FinReference From FinFeeRefundHeader_Temp) ");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinFeeRefundDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinFeeRefundDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinFeeRefundDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinFeeRefundDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(FinFeeRefundHeader finFeeRefund) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (finFeeRefund.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finFeeRefund.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				// doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		// fill the components with the data
		doWriteBeanToComponents(finFeeRefund);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug(Literal.ENTERING);
		if (this.feeRefundHeader.isNewRecord()) {
			this.finReference.setReadonly(false);
		} else {
			this.finReference.setReadonly(true);
		}
		this.finType.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.custName.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final FinFeeRefundHeader entity = new FinFeeRefundHeader();
		BeanUtils.copyProperties(this.feeRefundHeader, entity);

		String keyReference = Labels.getLabel("label_FinFeeRefundDialog_FinReference.value") + " : "
				+ feeRefundHeader.getFinReference();

		doDelete(keyReference, feeRefundHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.feeRefundHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final FinFeeRefundHeader feeRefundheader = new FinFeeRefundHeader();
		BeanUtils.copyProperties(this.feeRefundHeader, feeRefundheader);
		boolean isNew;
		doSetValidation();

		Long finID = financeDetailService.getFinID(feeRefundheader.getFinReference());

		feeRefundheader.setFinID(finID);
		doWriteComponentsToBean(feeRefundheader);
		isNew = feeRefundheader.isNewRecord();
		String tranType;
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(feeRefundheader.getRecordType())) {
				feeRefundheader.setVersion(feeRefundheader.getVersion() + 1);
				if (isNew) {
					feeRefundheader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					feeRefundheader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					feeRefundheader.setNewRecord(true);
				}
			}
		} else {
			feeRefundheader.setVersion(feeRefundheader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(feeRefundheader, tranType)) {
				refreshList();

				String roleCode = feeRefundheader.getRoleCode();
				String recordStatus2 = feeRefundheader.getRecordStatus();
				String nextRoleCode2 = feeRefundheader.getNextRoleCode();
				String finReference = feeRefundheader.getFinReference();

				String msg = PennantApplicationUtil.getSavingStatus(roleCode, nextRoleCode2, finReference,
						" Fee Refund ", recordStatus2);
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
	 * @param afinFeeRefundHeader (FinFeeRefundHeader)
	 * 
	 * @param tranType            (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinFeeRefundHeader afinFeeRefundHeader, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		afinFeeRefundHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinFeeRefundHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinFeeRefundHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			afinFeeRefundHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(afinFeeRefundHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, afinFeeRefundHeader);
				}

				if (isNotesMandatory(taskId, afinFeeRefundHeader)) {
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

			afinFeeRefundHeader.setTaskId(taskId);
			afinFeeRefundHeader.setNextTaskId(nextTaskId);
			afinFeeRefundHeader.setRoleCode(getRole());
			afinFeeRefundHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(afinFeeRefundHeader, tranType);
			String operationRefs = getServiceOperations(taskId, afinFeeRefundHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(afinFeeRefundHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(afinFeeRefundHeader, tranType);
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
		FinFeeRefundHeader afinFeeRefundHeader = (FinFeeRefundHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = finFeeRefundService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = finFeeRefundService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = finFeeRefundService.doApprove(aAuditHeader);

					if (afinFeeRefundHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = finFeeRefundService.doReject(aAuditHeader);

					if (afinFeeRefundHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinFeeRefundDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_FinFeeRefundDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.feeRefundHeader), true);
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
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 */
	public void doWriteComponentsToBean(FinFeeRefundHeader finFeeRefundHeader) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		doRemoveValidation();
		doRemoveLOVValidation();
		try {
			if (StringUtils.isBlank(this.finReference.getValidatedValue())) {
				throw new WrongValueException(this.finReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_FinFeeRefundDialog_FinReference.value") }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		wve.addAll(validateFeeRefundDetails());
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		finFeeRefundHeader.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	private ArrayList<WrongValueException> validateFeeRefundDetails() {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<>();
		doClearListValidations();
		for (Listitem listitem : listBoxFeeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			if (LISTITEM_SUMMARY.equalsIgnoreCase(listitem.getId())) {
				Listcell totalRefundLC = list.get(1);
				Decimalbox totRefundAmt = (Decimalbox) totalRefundLC.getFirstChild();
				try {
					if (totRefundAmt.getValue().compareTo(BigDecimal.ZERO) <= 0) {
						throw new WrongValueException(totalRefundLC, Labels.getLabel("FIELD_NO_NEGATIVE",
								new String[] {
										Labels.getLabel("listheader_FinFeeRefundList_AllocatedrefundTotal.label"),
										String.valueOf(BigDecimal.ZERO) }));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
			} else {
				Listcell refundAmtLC = list.get(10);
				Decimalbox refundAmt = (Decimalbox) refundAmtLC.getFirstChild();
				try {
					refundAmt.getValue();
				} catch (WrongValueException we) {
					wve.add(we);
				}

			}
		}
		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Method for Writing Data into Fields from Bean
	 * 
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents(FinFeeRefundHeader feeRefund) {
		if (!feeRefund.isNewRecord()) {
			setFinanceDetail(finFeeRefundService.getFinanceDetailById(feeRefund.getFinID()));
			this.finReference.setValue(feeRefund.getFinReference(), "");
			this.finType.setValue(feeRefund.getFinType(), feeRefund.getFintypedesc());
			this.finCcy.setValue(feeRefund.getFinCcy(), CurrencyUtil.getCcyDesc(feeRefund.getFinCcy()));
			this.finBranch.setValue(feeRefund.getFinBranch(), feeRefund.getBranchdesc());
			this.isFinTDSApplicable = feeRefund.isFinTDSApplicable();

			FinScheduleData scheduleData = getFinanceDetail().getFinScheduleData();
			FinanceMain financeMain = scheduleData.getFinanceMain();
			this.custCIF.setValue(financeMain.getLovDescCustCIF());
			this.custName.setValue(financeMain.getLovDescCustShrtName());
			this.custID = financeMain.getCustID();
			setTaxPercentages(calcTaxPercentages());
			String type = "_TView";
			if (enqiryModule
					|| FinanceConstants.CLOSE_STATUS_CANCELLED.equalsIgnoreCase(financeMain.getClosingStatus())) {
				type = "_View";
			}
			doFillFeeDetails(finFeeRefundService.getPaidFeeDetails(feeRefund, type));
			this.recordStatus.setValue(feeRefund.getRecordStatus());
		}
		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole()))) || (enqiryModule
				&& PennantConstants.RCD_STATUS_APPROVED.equalsIgnoreCase(feeRefund.getRecordStatus()))) {
			// Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}
	}

	/**
	 * Method for Filling Fee details which are going to be paid on Origination Process
	 */
	private void doFillFeeDetails(FinFeeRefundHeader feeRefundHeader) {
		logger.debug(Literal.ENTERING);
		Listcell lc;
		Listitem item;
		setParms();
		List<FinFeeDetail> feeDetails = feeRefundHeader.getFinFeeDetailList();
		List<FinFeeRefundDetails> refundDetailList = feeRefundHeader.getFinFeeRefundDetails();
		this.listBoxFeeDetail.getItems().clear();
		if (feeDetails != null) {
			int finFormatter = CurrencyUtil.getFormat(this.finCcy.getValue());
			boolean readOnly = getUserWorkspace().isAllowed("FinFeeRefundDialog_Refund");
			this.gb_FeeDetail.setVisible(true);

			this.listheader_FinFeeRefundList_PaidTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
			this.listheader_FinFeeRefundList_TotPrvsRefundTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
			this.listheader_FinFeeRefundList_AllocatedRefundTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
			for (int i = 0; i < feeDetails.size(); i++) {
				FinFeeDetail fee = feeDetails.get(i);
				item = new Listitem();
				FinFeeRefundDetails curFinFeeRefund = getFeeRefundDetailByFeeId(refundDetailList, fee, feeRefundHeader);

				PrvsFinFeeRefund prvsFinFeeRefund = finFeeRefundService.getPrvsRefundsByFeeId(fee.getFeeID());

				String taxComponent = null;

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent())) {
					taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
				} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())) {
					taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
				} else {
					taxComponent = Labels.getLabel("label_GST_NotApplicable");
				}
				String feeType = fee.getFeeTypeDesc() + " - (" + taxComponent + ")";
				if (StringUtils.isNotEmpty(fee.getVasReference())) {
					feeType = fee.getVasReference();
					fee.setFeeTypeCode(feeType);
					fee.setFeeTypeDesc(feeType);
				}

				// FeeType Desc
				lc = new Listcell(feeType);
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);

				// Paid Amount
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmountOriginal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Paid Amount GST
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmountGST(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Paid TDS
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidTDS(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Total Paid Amount
				lc = new Listcell(PennantApplicationUtil.amountFormate(fee.getPaidAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Early Refund Amount
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(prvsFinFeeRefund.getTotRefundAmtOriginal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Early Refund Amount GST
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(prvsFinFeeRefund.getTotRefundAmtGST(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Early Refund Amount TDS
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(prvsFinFeeRefund.getTotRefundAmtTDS(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Early Refund Amount with GST
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(prvsFinFeeRefund.getTotRefundAmount(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Refund Amount without GST
				Decimalbox allocAmtBox = getDecimalbox(finFormatter, true);
				allocAmtBox.setValue(CurrencyUtil.parse(curFinFeeRefund.getRefundAmtOriginal(), finFormatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(allocAmtBox);
				lc.setParent(item);

				// Refund Amount GST
				Decimalbox allocAmtGstBox = getDecimalbox(finFormatter, true);
				allocAmtGstBox.setValue(CurrencyUtil.parse(curFinFeeRefund.getRefundAmtGST(), finFormatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(allocAmtGstBox);
				lc.setParent(item);

				Decimalbox allocAmtTdsBox = getDecimalbox(finFormatter, true);
				allocAmtTdsBox.setValue(CurrencyUtil.parse(curFinFeeRefund.getRefundAmtTDS(), finFormatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(allocAmtTdsBox);
				lc.setParent(item);

				// Refund Amount total
				Decimalbox totAllocAmtTotBox = getDecimalbox(finFormatter, !readOnly);
				totAllocAmtTotBox.setValue(CurrencyUtil.parse(curFinFeeRefund.getRefundAmount(), finFormatter));
				lc = new Listcell();
				lc.appendChild(totAllocAmtTotBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				double remainingFee = PennantApplicationUtil
						.formateAmount(fee.getPaidAmount().subtract(prvsFinFeeRefund.getTotRefundAmount()),
								finFormatter)
						.doubleValue();
				if (remainingFee <= 0) {
					totAllocAmtTotBox.setReadonly(true);
				}
				if (!enqiryModule) {
					totAllocAmtTotBox.setConstraint(
							new PTDecimalValidator(Labels.getLabel("listheader_FinFeeRefundList_AllocatedRefund.label"),
									finFormatter, false, false, 0, remainingFee));
				}

				this.listBoxFeeDetail.appendChild(item);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put(FINFEEREFUNDDETAIL, curFinFeeRefund);
				map.put(PRVFINFEEREFUNDDETAIL, prvsFinFeeRefund);
				map.put(ALLOCATED_AMOUNT, allocAmtBox);
				map.put(ALLOCATED_AMT_GST, allocAmtGstBox);
				map.put(ALLOCATED_AMT_TDS, allocAmtTdsBox);
				map.put(ALLOCATES_AMT_TOTAL, totAllocAmtTotBox);
				map.put(FINFEEDETAIL, fee);
				map.put(FORMATTER, finFormatter);

				totAllocAmtTotBox.addForward("onChange", window_FinFeeRefundDialog, "onChangeFeeAmount", map);
			}

			Listitem summaryItem = createSummaryItem(finFormatter);
			this.listBoxFeeDetail.appendChild(summaryItem);
			doFillSummaryDetails(listBoxFeeDetail);

			setPaidFeeList(feeDetails);
		}
		logger.debug(Literal.LEAVING);
	}

	private FinFeeRefundDetails getFeeRefundDetailByFeeId(List<FinFeeRefundDetails> refundDetailList, FinFeeDetail fee,
			FinFeeRefundHeader feeRefundHeader) {
		logger.debug(Literal.ENTERING);
		for (FinFeeRefundDetails finFeeRefundDetails : refundDetailList) {
			if (fee.getFeeID() == finFeeRefundDetails.getFeeId()) {
				logger.debug(Literal.LEAVING);
				return finFeeRefundDetails;
			}
		}
		FinFeeRefundDetails refundDetail = new FinFeeRefundDetails();
		refundDetail.setNewRecord(true);
		refundDetail.setRecordType(PennantConstants.RCD_ADD);
		refundDetail.setFeeId(fee.getFeeID());
		refundDetail.setFeeTypeCode(fee.getFeeTypeCode());
		refundDetail.setTaxComponent(fee.getTaxComponent());
		// TODO:
		feeRefundHeader.getFinFeeRefundDetails().add(refundDetail);
		logger.debug(Literal.LEAVING);
		return refundDetail;
	}

	private Decimalbox getDecimalbox(int finFormatter, boolean readOnly) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setWidth("75px");
		decimalbox.setMaxlength(18);
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		decimalbox.setDisabled(readOnly);
		return decimalbox;
	}

	@SuppressWarnings("unchecked")
	public void onChangeFeeAmount(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = (Map<String, Object>) event.getData();
		FinFeeRefundDetails finFeeRefund = (FinFeeRefundDetails) map.get(FINFEEREFUNDDETAIL);
		PrvsFinFeeRefund prvFinFeeRefund = (PrvsFinFeeRefund) map.get(PRVFINFEEREFUNDDETAIL);
		Decimalbox allocAmtBox = (Decimalbox) map.get(ALLOCATED_AMOUNT);
		Decimalbox allocAmtGstBox = (Decimalbox) map.get(ALLOCATED_AMT_GST);
		Decimalbox allocAmtTdsBox = (Decimalbox) map.get(ALLOCATED_AMT_TDS);
		Decimalbox totAllocAmtTotBox = (Decimalbox) map.get(ALLOCATES_AMT_TOTAL);
		FinFeeDetail fee = (FinFeeDetail) map.get(FINFEEDETAIL);
		int formatter = (int) map.get(FORMATTER);

		BigDecimal allocatedAmtGST = BigDecimal.ZERO;
		BigDecimal allocatedAmtTDS = BigDecimal.ZERO;
		BigDecimal allocatedAmt = BigDecimal.ZERO;
		BigDecimal allocatedAmtTOT = CurrencyUtil.unFormat(totAllocAmtTotBox.getValue(), formatter);
		BigDecimal fraction = BigDecimal.ONE;
		BigDecimal totPerc = BigDecimal.ZERO;

		BigDecimal netAmountOriginal = fee.getNetAmount();
		BigDecimal netTDS = fee.getNetTDS();
		totPerc = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);

		if (fee.isTdsReq() && this.isFinTDSApplicable) {
			allocatedAmtTDS = (netTDS.multiply(allocatedAmtTOT)).divide(netAmountOriginal, 2, RoundingMode.HALF_DOWN);
			allocatedAmtTDS = CalculationUtil.roundAmount(allocatedAmtTDS, tdsRoundMode, tdsRoundingTarget);
			totPerc = taxPercentages.get(RuleConstants.CODE_TOTAL_GST).subtract(tdsPerc);
		}

		if (finFeeRefund.isTaxApplicable()) {
			fraction = fraction.add(totPerc.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN));

			BigDecimal orgAmt = allocatedAmtTOT.divide(fraction, 0, RoundingMode.HALF_DOWN);
			orgAmt = CalculationUtil.roundAmount(orgAmt, taxRoundMode, taxRoundingTarget);
			allocatedAmtGST = GSTCalculator.getExclusiveGST(orgAmt, taxPercentages).gettGST();
		}

		allocatedAmt = allocatedAmtTOT.subtract(allocatedAmtGST).add(allocatedAmtTDS);

		BigDecimal diffAmt = BigDecimal.ZERO;
		BigDecimal diffGST = BigDecimal.ZERO;
		BigDecimal diffTDS = BigDecimal.ZERO;
		BigDecimal remAmt = fee.getPaidAmount().subtract(prvFinFeeRefund.getTotRefundAmount());

		if (allocatedAmtTOT.compareTo(remAmt) == 0) {
			diffAmt = fee.getPaidAmountOriginal().subtract(prvFinFeeRefund.getTotRefundAmtOriginal().add(allocatedAmt));
			diffGST = fee.getPaidAmountGST().subtract(prvFinFeeRefund.getTotRefundAmtGST().add(allocatedAmtGST));
			diffTDS = fee.getPaidTDS().subtract(prvFinFeeRefund.getTotRefundAmtTDS().add(allocatedAmtTDS));
		}

		allocatedAmt = allocatedAmt.add(diffAmt);
		allocatedAmtGST = allocatedAmtGST.add(diffGST);
		allocatedAmtTDS = allocatedAmtTDS.add(diffTDS);

		finFeeRefund.setRefundAmount(allocatedAmtTOT);
		finFeeRefund.setRefundAmtGST(allocatedAmtGST);
		finFeeRefund.setRefundAmtTDS(allocatedAmtTDS);
		finFeeRefund.setRefundAmtOriginal(allocatedAmt);
		allocAmtBox.setValue(CurrencyUtil.parse(allocatedAmt, formatter));
		allocAmtGstBox.setValue(CurrencyUtil.parse(allocatedAmtGST, formatter));
		allocAmtTdsBox.setValue(CurrencyUtil.parse(allocatedAmtTDS, formatter));
		totAllocAmtTotBox.setValue(CurrencyUtil.parse(allocatedAmtTOT, formatter));
		doFillSummaryDetails(listBoxFeeDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for creating Summary List Item.
	 * 
	 * @param formatter
	 * @return
	 */
	private Listitem createSummaryItem(int formatter) {
		Listcell lc;
		Listitem item;

		// Summary Details
		item = new Listitem();
		item.setId(LISTITEM_SUMMARY);
		lc = new Listcell(Labels.getLabel("listcell_Total.label"));
		item.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(12);
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox totalPaidBox = getDecimalbox(formatter, true);
		lc.appendChild(totalPaidBox);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell("");
		lc.setParent(item);
		return item;
	}

	/**
	 * Method for Fill the summary detail
	 * 
	 * @param listBoxFeeDetail
	 */
	private void doFillSummaryDetails(Listbox listBoxFeeDetail) {

		Listitem summaryItem = null;
		BigDecimal totRefund = BigDecimal.ZERO;
		for (Listitem listitem : listBoxFeeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			if (LISTITEM_SUMMARY.equalsIgnoreCase(listitem.getId())) {
				summaryItem = listitem;
				continue;
			} else {
				// Total remaining
				Listcell totRemLC = list.get(12);
				Decimalbox totRemBox = (Decimalbox) totRemLC.getFirstChild();
				totRefund = totRefund.add(totRemBox.getValue());
			}
		}

		List<Listcell> list = summaryItem.getChildren();
		// Total remaining
		Listcell totRemLC = list.get(1);
		Decimalbox totRemBox = (Decimalbox) totRemLC.getFirstChild();
		totRemBox.setValue(totRefund);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finReference.getObject();
		this.gb_FeeDetail.setVisible(false);
		boolean clearFields = false;
		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			clearFields = true;
		} else {
			FinanceMain main = (FinanceMain) dataObject;
			if (main != null) {
				this.finReference.setValue(main.getFinReference(), "");
				this.finType.setValue(main.getFinType(), main.getLovDescFinTypeName());
				this.finCcy.setValue(main.getFinCcy(), CurrencyUtil.getCcyDesc(main.getFinCcy()));
				this.finBranch.setValue(main.getFinBranch(), main.getLovDescFinBranchName());
				this.isFinTDSApplicable = TDSCalculator.isTDSApplicable(main);

				setFinanceDetail(finFeeRefundService.getFinanceDetailById(main.getFinID()));
				FinScheduleData scheduleData = getFinanceDetail().getFinScheduleData();
				FinanceMain financeMain = scheduleData.getFinanceMain();
				this.custCIF.setValue(financeMain.getLovDescCustCIF());
				this.custName.setValue(financeMain.getLovDescCustShrtName());
				// setting data
				custID = financeMain.getCustID();
				setTaxPercentages(calcTaxPercentages());
				this.feeRefundHeader.setFinReference(main.getFinReference());
				this.feeRefundHeader.setFinType(main.getFinType());
				this.feeRefundHeader.setCustId(custID);
				String type = "_TView";
				if (FinanceConstants.CLOSE_STATUS_CANCELLED.equalsIgnoreCase(main.getClosingStatus())) {
					type = "_View";
				}

				finFeeRefundService.getPaidFeeDetails(this.feeRefundHeader, type);
				doFillFeeDetails(this.feeRefundHeader);
			} else {
				clearFields = true;
			}
		}

		if (clearFields) {
			this.finReference.setValue("", "");
			this.finType.setValue("", "");
			this.finCcy.setValue("", "");
			this.finBranch.setValue("", "");
			this.custCIF.setValue("");
			this.custName.setValue("");

			this.gb_FeeDetail.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 */
	public void onClick$btnReceipt(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("finHeaderList", getFinBasicDetails(getFinanceDetail()));

			// Fetch Accounting Set ID
			AccountingSet accountingSet = accountingSetService.getAccSetSysDflByEvent(AccountingEvent.FEEREFUND,
					AccountingEvent.FEEREFUND, "");

			long acSetID = 0;
			if (accountingSet != null) {
				acSetID = accountingSet.getAccountSetid();
			}

			map.put("acSetID", acSetID);
			map.put("postAccReq", false);
			map.put("enqModule", enqiryModule);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if (financeDetail == null) {
			logger.debug(Literal.LEAVING);
			return arrayList;
		}
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();

		arrayList.add(0, main.getFinType());
		arrayList.add(1, main.getFinCcy());
		arrayList.add(2, main.getScheduleMethod());
		arrayList.add(3, main.getFinReference());
		arrayList.add(4, main.getProfitDaysBasis());
		arrayList.add(5, main.getGrcPeriodEndDate());
		arrayList.add(6, main.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(main.getProductCategory())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}

		arrayList.add(8, main.getFinCategory());
		arrayList.add(9, main.getCustShrtName());
		arrayList.add(10, main.isNewRecord());
		arrayList.add(11, "");
		logger.debug(Literal.LEAVING);
		return arrayList;
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=onSelectAccountTab"));
		logger.debug(Literal.LEAVING);
	}

	public void onSelectAccountTab(ForwardEvent event) {

		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, tab, "onSelectAccountTab");
		appendAccountingDetailTab(false);
		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails(getFinanceDetail()));
		}
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void executeAccounting() {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		AEEvent aeEvent = finFeeRefundService.processAccounting(this.feeRefundHeader);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();

		finFeeRefundService.prepareFeeRulesMap(this.feeRefundHeader, dataMap, userBranch);

		// Fetch Accounting Set ID
		Long accountingSetID = AccountingEngine.getAccountSetID(this.feeRefundHeader.getFinType(),
				AccountingEvent.FEEREFUND, FinanceConstants.MODULEID_FINTYPE);

		if (accountingSetID != null && accountingSetID > 0) {
			aeEvent.getAcSetIDList().add(accountingSetID);
			aeEvent.setDataMap(dataMap);
			engineExecution.getAccEngineExecResults(aeEvent);
			accountingSetEntries.addAll(aeEvent.getReturnDataSet());
		} else {
			Clients.showNotification(Labels.getLabel("label_FeeReceiptDialog_NoAccounting.value"), "warning", null,
					null, -1);
		}
		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param finFeeRefundHeader
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinFeeRefundHeader finFeeRefundHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finFeeRefundHeader.getBefImage(), finFeeRefundHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, finFeeRefundHeader.getUserDetails(),
				getOverideMap());
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinFeeRefundHeader> soReceipt = finFeeRefundListCtrl.getSearchObject();
		finFeeRefundListCtrl.pagingFinFeeRefundsList.setActivePage(0);
		finFeeRefundListCtrl.getPagedListWrapper().setSearchObject(soReceipt);
		if (finFeeRefundListCtrl.listBoxFinFeeRefunds != null) {
			finFeeRefundListCtrl.listBoxFinFeeRefunds.getListModel();
		}
	}

	@Override
	protected String getReference() {
		return this.finReference.getValue();
	}

	/**
	 * Clears validation error messages from all the fields of the ListBox.
	 */
	public void doClearListValidations() {
		logger.debug(Literal.ENTERING);
		for (Listitem listitem : listBoxFeeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			if (LISTITEM_SUMMARY.equalsIgnoreCase(listitem.getId())) {
				Listcell totalPaidLC = list.get(1);
				Clients.clearWrongValue(totalPaidLC);
				break;
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private Map<String, BigDecimal> calcTaxPercentages() {
		String userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
		String finCcy = this.finCcy.getValue();
		String finBranch = this.finBranch.getValue();
		return GSTCalculator.getTaxPercentages(custID, finCcy, userBranch, finBranch);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(getFeeRefundHeader());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		finFeeRefundListCtrl.search();
	}

	private void setParms() {
		if (tdsPerc == null || tdsRoundMode == null || tdsRoundingTarget == 0 || taxRoundMode == null
				|| taxRoundingTarget == 0) {
			tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
			taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
			taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FeeReceiptService getFeeReceiptService() {
		return feeReceiptService;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public List<FinFeeDetail> getPaidFeeList() {
		return paidFeeList;
	}

	public void setPaidFeeList(List<FinFeeDetail> paidFeeList) {
		this.paidFeeList = paidFeeList;
	}

	public void setTaxPercentages(Map<String, BigDecimal> taxPercentages) {
		this.taxPercentages = taxPercentages;
	}

	public List<FinFeeReceipt> getCurrentFinReceipts() {
		return currentFinReceipts;
	}

	public void setCurrentFinReceipts(List<FinFeeReceipt> currentFinReceipts) {
		this.currentFinReceipts = currentFinReceipts;
	}

	public List<FinFeeReceipt> getOldFinReceipts() {
		return oldFinReceipts;
	}

	public void setOldFinReceipts(List<FinFeeReceipt> oldFinReceipts) {
		this.oldFinReceipts = oldFinReceipts;
	}

	public FinFeeRefundHeader getFeeRefundHeader() {
		return feeRefundHeader;
	}

	public void setFeeRefundHeader(FinFeeRefundHeader feeRefundHeader) {
		this.feeRefundHeader = feeRefundHeader;
	}

	public void setFinFeeRefundService(FinFeeRefundService finFeeRefundService) {
		this.finFeeRefundService = finFeeRefundService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}