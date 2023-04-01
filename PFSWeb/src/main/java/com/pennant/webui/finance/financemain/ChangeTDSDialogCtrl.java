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
 * * FileName : LoanDetailsEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ChangeTDSService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class ChangeTDSDialogCtrl extends GFCBaseCtrl<FinMaintainInstruction> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(ChangeTDSDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChangeTDSDialog;
	protected Textbox finReference;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Textbox custShrtName;
	protected Textbox finType;
	protected Textbox currency;
	protected Textbox loanAmount;
	protected Textbox startDate;
	protected Checkbox tDSApplicable;
	protected Tabbox tabbox;
	// protected Datebox installmentDate;
	protected Decimalbox tdsPercentage;

	protected boolean approvedList;
	private FinanceMain financeMain;
	private transient ChangeTDSService changeTDSService;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	private FinMaintainInstruction finMaintainInstruction;
	protected Listbox listBoxTdsDetails;

	protected Row rowFinance;

	boolean facility = false;
	boolean userActivityLog = false;
	protected Label label_windowTitle;
	boolean isTDSChecked = false;
	private Object financeMainDialogCtrl;
	private boolean isEnquiry = false;
	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;

	protected Groupbox eventHistory;
	protected Listbox listBoxEventHistory;
	protected Listheader currentTDS;
	protected Listheader newTDS;
	protected Caption eventHistoryCaption;
	protected Datebox tdsStartDate;
	protected Datebox tdsEndDate;
	protected Row row_TDS2;
	protected Label label_TdsPercentage;
	protected CurrencyBox tdsLimit;
	protected Row row_TDS3;
	private FinanceDetail financeDetail = null;
	boolean istdsAllowToModify = false;
	protected transient Date previousLTDEndDate;

	private List<LowerTaxDeduction> oldLowerTaxDeductionDetail = new ArrayList<LowerTaxDeduction>();
	String allowTaxDeduction = SysParamUtil.getValueAsString(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ);

	/**
	 * default constructor.<br>
	 */
	public ChangeTDSDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ChangeTDSDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ChangeTDSDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangeTDSDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
				this.financeMainDialogCtrl = arguments.get("financeSelectCtrl");
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
			if (arguments.containsKey("TDSCheck")) {
				this.isTDSChecked = (boolean) arguments.get("TDSCheck");
			}

			if (arguments.containsKey("financeMain")) {
				this.financeMain = (FinanceMain) arguments.get("financeMain");
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateAuthorities(pageRightName, getRole());
			}

			if (arguments.containsKey("finMaintainInstruction")) {
				setFinMaintainInstruction((FinMaintainInstruction) arguments.get("finMaintainInstruction"));
				this.finMaintainInstruction = getFinMaintainInstruction();
			}

			if (this.finMaintainInstruction == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			// Store the before image.
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			BeanUtils.copyProperties(this.finMaintainInstruction, finMaintainInstruction);
			this.finMaintainInstruction.setBefImage(finMaintainInstruction);

			// Render the page and display the data.
			doLoadWorkFlow(this.finMaintainInstruction.isWorkflow(), this.finMaintainInstruction.getWorkflowId(),
					this.finMaintainInstruction.getNextTaskId());

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			this.istdsAllowToModify = financeType.isTdsAllowToModify();

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(finMaintainInstruction.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
					// getUserWorkspace().allocateMenuRoleAuthorities(getRole(), this.pageRightName, menuItemRightName);
				}
			} else {
				this.south.setHeight("0px");
			}
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finMaintainInstruction);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.tdsPercentage.setMaxlength(5);
		this.tdsPercentage.setFormat(PennantConstants.rateFormate9);
		this.tdsPercentage.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.tdsPercentage.setScale(2);
		this.tdsStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tdsEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tdsLimit.setProperties(false, getCcyFormat());

		if (PennantConstants.YES.equals(allowTaxDeduction)) {
			if (isTDSChecked && istdsAllowToModify) {
				this.row_TDS2.setVisible(true);
				this.label_TdsPercentage.setVisible(true);
				this.tdsPercentage.setVisible(true);
				this.row_TDS3.setVisible(true);

			} else {
				this.row_TDS2.setVisible(false);
				this.label_TdsPercentage.setVisible(false);
				this.tdsPercentage.setVisible(false);

			}
			if (isTDSChecked && istdsAllowToModify && finMaintainInstruction.getTdsStartDate() != null
					&& finMaintainInstruction.getTdsEndDate() != null) {
				this.row_TDS3.setVisible(true);
			} else {
				this.row_TDS3.setVisible(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		// getUserWorkspace().allocateAuthorities(this.pageRightName, getRole(), menuItemRightName);

		getUserWorkspace().allocateAuthorities(pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinChangeTDSDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinChangeTDSDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinChangeTDSDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinChangeTDSDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	protected void doSave() {
		logger.debug("Entering");

		Cloner cloner = new Cloner();
		FinMaintainInstruction aFinMaintainInstruction = cloner.deepClone(getFinMaintainInstruction());

		doSetValidation();

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
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aFinMaintainInstruction.getRoleCode(),
						aFinMaintainInstruction.getNextRoleCode(), aFinMaintainInstruction.getFinReference() + "",
						" Change TDS ", aFinMaintainInstruction.getRecordStatus(), false);
				if (StringUtils.equals(aFinMaintainInstruction.getRecordStatus(),
						PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Change TDS with Reference " + aFinMaintainInstruction.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	public void doWriteComponentsToBean(FinMaintainInstruction finMaintainInstruction) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		finMaintainInstruction.setFinID(this.financeMain.getFinID());
		finMaintainInstruction.setFinReference(this.financeMain.getFinReference());
		finMaintainInstruction.setEvent(this.moduleDefiner);
		finMaintainInstruction.settDSApplicable(this.tDSApplicable.isChecked());

		finMaintainInstruction.setRecordStatus(this.recordStatus.getValue());
		finMaintainInstruction.setFinCovenantTypeList(null);
		if (this.tDSApplicable.isChecked() && istdsAllowToModify) {
			finMaintainInstruction.setTdsPercentage(this.tdsPercentage.getValue());
			finMaintainInstruction.setTdsStartDate(this.tdsStartDate.getValue());
			finMaintainInstruction.setTdsEndDate(this.tdsEndDate.getValue());
			try {
				finMaintainInstruction.setTdsLimit(
						PennantApplicationUtil.unFormateAmount(this.tdsLimit.getActualValue(), getCcyFormat()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			finMaintainInstruction.setTdsPercentage(BigDecimal.ZERO);
			finMaintainInstruction.setTdsStartDate(null);
			finMaintainInstruction.setTdsEndDate(null);
			finMaintainInstruction.setTdsLimit(BigDecimal.ZERO);
		}

		if (this.tDSApplicable.isChecked() && istdsAllowToModify) {
			List<LowerTaxDeduction> lowerTaxdedecutions = new ArrayList<LowerTaxDeduction>();
			LowerTaxDeduction lowerTxDeduction = new LowerTaxDeduction();
			lowerTxDeduction.setFinID(this.financeMain.getFinID());
			lowerTxDeduction.setFinReference(this.financeMain.getFinReference());
			getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails().clear();
			lowerTxDeduction.setSeqNo(1);

			try {
				lowerTxDeduction.setPercentage(this.tdsPercentage.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				lowerTxDeduction.setStartDate(this.tdsStartDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (this.tdsStartDate.getValue() != null && previousLTDEndDate != null
					&& DateUtil.compare(this.tdsStartDate.getValue(), previousLTDEndDate) <= 0) {
				throw new WrongValueException(this.tdsStartDate, Labels.getLabel("FRQ_DATE_MISMATCH", new String[] {
						"Previous LTD End Date", Labels.getLabel("label_FinanceMainDialog_tDSStartDate.value") }));
			}

			try {
				lowerTxDeduction.setEndDate(this.tdsEndDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				lowerTxDeduction.setLimitAmt(
						PennantApplicationUtil.unFormateAmount(this.tdsLimit.getActualValue(), getCcyFormat()));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			lowerTaxdedecutions.add(lowerTxDeduction);
			getFinanceDetail().getFinScheduleData().setLowerTaxDeductionDetails(lowerTaxdedecutions);
		} else {
			List<LowerTaxDeduction> ltDetails = getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails();

			if (CollectionUtils.isEmpty(ltDetails)) {
				for (LowerTaxDeduction lowerTaxDeduction : ltDetails) {
					lowerTaxDeduction = new LowerTaxDeduction();
					lowerTaxDeduction.setEndDate(null);
					lowerTaxDeduction.setStartDate(null);
					lowerTaxDeduction.setPercentage(BigDecimal.ZERO);
					lowerTaxDeduction.setLimitAmt(BigDecimal.ZERO);
				}
			} else {
				for (LowerTaxDeduction lowerTaxDeduction : ltDetails) {
					lowerTaxDeduction.setEndDate(null);
					lowerTaxDeduction.setStartDate(null);
					lowerTaxDeduction.setPercentage(BigDecimal.ZERO);
					lowerTaxDeduction.setLimitAmt(BigDecimal.ZERO);

				}
			}
			getFinanceDetail().getFinScheduleData().setLowerTaxDeductionDetails(ltDetails);
		}

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
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
	protected boolean doProcess(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		logger.debug("Entering");

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
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinMaintainInstruction
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinMaintainInstruction.getBefImage(),
				aFinMaintainInstruction);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinMaintainInstruction.getUserDetails(),
				getOverideMap());
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
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FinMaintainInstruction aFinMaintainInstruction = (FinMaintainInstruction) aAuditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = changeTDSService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = changeTDSService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = changeTDSService.doApprove(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = changeTDSService.doReject(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ChangeTDSDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_ChangeTDSDialog, aAuditHeader);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering");
		this.custCIF.setValue(financeMain.getCustCIF());
		this.custShrtName.setValue(financeMain.getLovDescCustShrtName());
		this.finReference.setValue(financeMain.getFinReference());
		this.finBranch.setValue(financeMain.getFinBranch());
		this.currency.setValue(StringUtils.trimToEmpty(financeMain.getFinCcy()));
		this.finType.setValue(financeMain.getLovDescFinTypeName());
		this.loanAmount.setValue(PennantApplicationUtil.amountFormate(financeMain.getFinAssetValue(),
				CurrencyUtil.getFormat(financeMain.getFinCcy())));
		this.startDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		this.tDSApplicable.setChecked(isTDSChecked);

		if (isTDSChecked && istdsAllowToModify && finMaintainInstruction.getTdsPercentage() == null) {
			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			this.tdsPercentage.setValue(tdsPerc);
		} else {
			this.tdsPercentage.setValue(finMaintainInstruction.getTdsPercentage());
		}

		this.tdsStartDate.setValue(finMaintainInstruction.getTdsStartDate());
		this.tdsEndDate.setValue(finMaintainInstruction.getTdsEndDate());
		this.tdsLimit
				.setValue(PennantApplicationUtil.formateAmount(finMaintainInstruction.getTdsLimit(), getCcyFormat()));

		this.recordStatus.setValue(finMaintainInstruction.getRecordStatus());
		/*
		 * this.installmentDate .setValue(changeTDSService.getInstallmentDate(financeMain. getFinReference(),
		 * DateUtility.getAppDate())); this.recordStatus.setValue(finMaintainInstruction.getRecordStatus());
		 */
		/*
		 * if (StringUtils.equals(finMaintainInstruction.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
		 * this.eventHistory.setVisible(true);
		 * this.eventHistoryCaption.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_TDSDetails"));
		 * this.currentTDS.setLabel(Labels.getLabel("label_ScheduleDetailDialog_CurrentTDS"));
		 * this.newTDS.setLabel(Labels.getLabel("label_ScheduleDetailDialog_NewTDS"));
		 * 
		 * Listitem item = new Listitem(); Listcell lc; lc = new
		 * Listcell(String.valueOf(financeMain.isTDSApplicable())); lc.setParent(item); lc = new
		 * Listcell(String.valueOf(finMaintainInstruction.istDSApplicable())); lc.setParent(item);
		 * listBoxEventHistory.appendChild(item); }
		 */

		if (getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails() != null
				&& getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails().size() == 1) {
			this.previousLTDEndDate = getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails().get(0)
					.getEndDate();
		}

		/*
		 * Collections.sort(getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails(), new
		 * Comparator<LowerTaxDeduction>() { public int compare(LowerTaxDeduction o1, LowerTaxDeduction o2) { return
		 * DateUtility.compare(o1.getEndDate(), o2.getEndDate()); } });
		 */
		doFillListbox(getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails());
		logger.debug("Leaving");
	}

	private void doFillListbox(List<LowerTaxDeduction> list) {
		this.listBoxTdsDetails.getItems().clear();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (LowerTaxDeduction lowerTaxDeduction : list) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(String.valueOf(lowerTaxDeduction.getPercentage()));
			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(lowerTaxDeduction.getStartDate()));
			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(lowerTaxDeduction.getEndDate()));
			lc.setParent(item);
			lc = new Listcell(
					PennantApplicationUtil.amountFormate(lowerTaxDeduction.getLimitAmt(), CurrencyUtil.getFormat("")));
			lc.setParent(item);
			this.listBoxTdsDetails.appendChild(item);

		}

	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finMaintainInstruction);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finMaintainInstruction.getFinReference());
	}

	/**
	 * Render Notes
	 * 
	 * @param appList
	 * @return
	 */
	public Listbox renderNotes(List<Notes> appList) {
		logger.debug("Entering");

		// Retrieve Notes List By Module Reference
		Listbox listboxNotes = new Listbox();
		Listitem item = null;
		Listcell lc = null;
		String alignSide = "right";
		for (int i = 0; i < appList.size(); i++) {

			Notes note = appList.get(i);
			if (note != null) {

				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
				Html html = new Html();

				if ("right".equals(alignSide)) {
					alignSide = "left";
				} else {
					alignSide = "right";
				}

				/*
				 * String usrAlign = ""; if("right".equals(alignSide)){ usrAlign = "left"; }else{ usrAlign = "right"; }
				 */

				String content = "<p class='triangle-right " + alignSide + "'> <font style='font-weight:bold;'> "
						+ note.getRemarks() + " </font> <br>  ";
				String date = DateUtil.format(note.getInputDate(), PennantConstants.dateTimeAMPMFormat);
				if ("I".equals(note.getRemarkType())) {
					content = content + "<font style='color:#FF0000;float:" + alignSide + ";'>"
							+ note.getUsrLogin().toLowerCase() + " : " + date + "</font></p>";
				} else {
					content = content + "<font style='color:black;float:" + alignSide + ";'>"
							+ note.getUsrLogin().toLowerCase() + " : " + date + "</font></p>";
				}
				html.setContent(content);
				lc.appendChild(html);
				lc.setParent(item);
				listboxNotes.appendChild(item);
			}
		}
		logger.debug("Leaving");
		return listboxNotes;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aFinMaintainInstruction The entity that need to be render.
	 */
	public void doShowDialog(FinMaintainInstruction finMaintainInstruction) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (finMaintainInstruction.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(finMaintainInstruction.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		this.tDSApplicable.setDisabled(isReadOnly("button_FinChangeTDSDialog_tDSApplicable"));

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents();
		doReadOnly();
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(isReadOnly("ChangeTDSDialog_TDSPercentage"), this.tdsPercentage);
		readOnlyComponent(isReadOnly("ChangeTDSDialog_TDSStartDate"), this.tdsStartDate);
		readOnlyComponent(isReadOnly("ChangeTDSDialog_TDSEndDate"), this.tdsEndDate);
		readOnlyComponent(isReadOnly("ChangeTDSDialog_TDSPercentage"), this.tdsLimit);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finMaintainInstruction.isNewRecord()) {
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
		logger.debug(Literal.ENTERING);
		this.finReference.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.finType.setReadonly(true);
		this.currency.setReadonly(true);
		this.loanAmount.setReadonly(true);
		this.startDate.setReadonly(true);
		/*
		 * this.tdsPercentage.setReadonly(true); this.tdsStartDate.setReadonly(true); this.tdsEndDate.setReadonly(true);
		 */
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doClose();

		logger.debug(Literal.LEAVING);
	}

	protected void doClose() {
		if (!userActivityLog) {
			closeDialog();
		} else {
			this.window_ChangeTDSDialog.onClose();
		}
		if (tabbox != null) {
			this.tabbox.getSelectedTab().close();
		}
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		BigDecimal tdsPerc = BigDecimal.ZERO;
		if (this.tdsPercentage.getValue() != null) {
			tdsPerc = this.tdsPercentage.getValue();
		}
		if (this.tdsPercentage.getValue() != null && tdsPerc.compareTo(this.tdsPercentage.getValue()) != 0) {
			if (!this.tdsStartDate.isDisabled()) {
				this.tdsStartDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_ChangeTDS_TdsStartDate.value"), true));
			}

			if (!this.tdsEndDate.isDisabled()) {
				this.tdsEndDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_ChangeTDS_TdsEndDate.value"), true));
			}
		} else {
			this.tdsStartDate.setErrorMessage("");
			this.tdsEndDate.setErrorMessage("");
			if (!this.tdsStartDate.isDisabled()) {
				this.tdsStartDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_ChangeTDS_TdsStartDate.value"), false));
			}

			if (!this.tdsEndDate.isDisabled()) {
				this.tdsEndDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_ChangeTDS_TdsEndDate.value"), false));
			}
		}
		if (!this.tdsLimit.isReadonly()) {
			this.tdsLimit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ChangeTDS_TdsLimit.value"),
					PennantConstants.defaultCCYDecPos, false, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the selectAll CheckBox is checked . <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$tDSApplicable(Event event) throws InterruptedException {
		logger.debug(Literal.LEAVING);
		if (PennantConstants.YES.equals(allowTaxDeduction)) {
			if (this.tDSApplicable.isChecked() && istdsAllowToModify) {
				this.row_TDS2.setVisible(true);
				this.label_TdsPercentage.setVisible(true);
				this.tdsPercentage.setVisible(true);
				BigDecimal tdsPerc = new BigDecimal(
						SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
				this.tdsPercentage.setValue(tdsPerc);
				this.row_TDS3.setVisible(true);
			} else {
				this.row_TDS2.setVisible(false);
				this.label_TdsPercentage.setVisible(false);
				this.tdsPercentage.setVisible(false);
				this.row_TDS3.setVisible(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param event
	 */
	public void onChange$tdsStartDate(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.tdsStartDate.getValue() != null) {

			getEndYearDate();
		} else {
			this.tdsEndDate.setValue(null);
			this.row_TDS3.setVisible(false);
			this.tdsLimit.setValue(BigDecimal.ZERO);
		}
		logger.debug(Literal.LEAVING);

	}

	private void getEndYearDate() {
		int month = DateUtil.getMonth(this.tdsStartDate.getValue());
		int year = DateUtil.getYear(this.tdsStartDate.getValue());
		this.row_TDS3.setVisible(true);

		if (month > 3) {
			Date tdsformateEndDate = null;
			try {
				tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (year + 1));
			} catch (ParseException e) {
				logger.error(Literal.EXCEPTION, e);
			}
			this.tdsEndDate.setValue(tdsformateEndDate);
		} else {
			Date tdsformateEndDate = null;
			try {
				tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (year));
			} catch (ParseException e) {
				logger.error(Literal.EXCEPTION, e);
			}
			this.tdsEndDate.setValue(tdsformateEndDate);
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void onChange$tdsEndDate(Event event) {
		logger.debug(Literal.ENTERING);
		finMaintainInstruction.setTdsLimit(BigDecimal.ZERO);
		if (this.tdsStartDate.getValue() != null && this.tdsEndDate.getValue() != null) {

			int startDatemonth = DateUtil.getMonth(this.tdsStartDate.getValue());
			int startDateyear = DateUtil.getYear(this.tdsStartDate.getValue());
			this.row_TDS3.setVisible(true);
			Date tdsformateEndDate = null;
			Date tdsEndDate = null;
			Date tdsStartDate = null;

			if (startDatemonth > 3) {

				try {
					tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (startDateyear + 1));
				} catch (ParseException e) {
					logger.error(Literal.EXCEPTION, e);
				}
			} else {

				try {
					tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (startDateyear));
				} catch (ParseException e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
			tdsEndDate = this.tdsEndDate.getValue();
			tdsStartDate = this.tdsStartDate.getValue();
			if (DateUtil.compare(tdsformateEndDate, tdsEndDate) == -1
					|| DateUtil.compare(tdsStartDate, tdsEndDate) == 0
					|| DateUtil.compare(tdsEndDate, tdsStartDate) == -1) {
				throw new WrongValueException(this.tdsEndDate,
						"End Date must be after" + " " + DateUtil.format(this.tdsStartDate.getValue(), "dd/MM/yyyy")
								+ " " + "before" + " " + DateUtil.format(tdsformateEndDate, "dd/MM/yyyy"));
			}
		} else {
			this.tdsEndDate.setValue(null);
			this.row_TDS3.setVisible(false);
			this.tdsLimit.setValue(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public ChangeTDSService getChangeTDSService() {
		return changeTDSService;
	}

	public void setChangeTDSService(ChangeTDSService changeTDSService) {
		this.changeTDSService = changeTDSService;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinMaintainInstruction getFinMaintainInstruction() {
		return finMaintainInstruction;
	}

	public void setFinMaintainInstruction(FinMaintainInstruction finMaintainInstruction) {
		this.finMaintainInstruction = finMaintainInstruction;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<LowerTaxDeduction> getOldLowerTaxDeductionDetail() {
		return oldLowerTaxDeductionDetail;
	}

	public void setOldLowerTaxDeductionDetail(List<LowerTaxDeduction> oldLowerTaxDeductionDetail) {
		this.oldLowerTaxDeductionDetail = oldLowerTaxDeductionDetail;
	}

}
