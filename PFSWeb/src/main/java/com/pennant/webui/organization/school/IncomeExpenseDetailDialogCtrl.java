package com.pennant.webui.organization.school;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.incomeexpensedetail.service.IncomeExpenseDetailService;
import com.pennanttech.pff.organization.IncomeExpenseType;
import com.pennanttech.pff.organization.OrganizationUtil;
import com.pennanttech.pff.organization.model.IncomeExpenseDetail;
import com.pennanttech.pff.organization.model.IncomeExpenseHeader;

public class IncomeExpenseDetailDialogCtrl extends GFCBaseCtrl<IncomeExpenseHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(IncomeExpenseDetailDialogCtrl.class);

	protected Window window_IncomeExpenseDetailsDialog;
	protected Button btnNew_SchoolCoreIncome;
	protected Button btnNew_SchoolNonCoreIncome;
	protected Button btnNew_SchoolExpense;

	protected Tab OrgSchoolIncomeDetailsTab;
	protected Tab OrgSchoolExpenseDetailsTab;

	protected Listbox listBoxSchoolCoreIncomeDetails;
	protected Listbox listBoxSchoolNonCoreIncomeDetails;
	protected Listbox listBoxSchoolExpenseDetails;

	private List<IncomeExpenseDetail> coreIncomeDetailList = new ArrayList<>();
	private List<IncomeExpenseDetail> schoolCoreIncomes;
	private List<IncomeExpenseDetail> nonCoreIncomeDetailList = new ArrayList<>();
	private List<IncomeExpenseDetail> schoolNonCoreIncomes;
	private List<IncomeExpenseDetail> expenseDetailList = new ArrayList<>();
	private List<IncomeExpenseDetail> schoolExpenses;

	private IncomeExpenseHeader incomeExpenseHeader;
	private transient IncomeExpenseDetailListCtrl incomeExpenseDetailListCtrl;

	@Autowired
	private IncomeExpenseDetailService incomeExpenseDetailService;

	private List<ValueLabel> categories = OrganizationUtil.getSchoolClassName();
	private List<ValueLabel> frqOfCollectionList = OrganizationUtil.getCollectionFrequencyList();
	private int coreIncomeCount = 0;
	private int nonCoreIncomeCount = 0;
	private int expenseCount = 0;

	public IncomeExpenseDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "OrganizationIncomeExpenseDialog";
	}

	public void onCreate$window_IncomeExpenseDetailsDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_IncomeExpenseDetailsDialog);

		try {
			// Get the required arguments.
			this.incomeExpenseHeader = (IncomeExpenseHeader) arguments.get("incomeExpenseHeader");

			if (arguments.get("incomeExpenseDetailListCtrl") != null) {
				this.incomeExpenseDetailListCtrl = (IncomeExpenseDetailListCtrl) arguments
						.get("incomeExpenseDetailListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (this.incomeExpenseHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
			BeanUtils.copyProperties(this.incomeExpenseHeader, incomeExpenseHeader);
			this.incomeExpenseHeader.setBefImage(incomeExpenseHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.incomeExpenseHeader.isWorkflow(), this.incomeExpenseHeader.getWorkflowId(),
					this.incomeExpenseHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.incomeExpenseHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseDialog_btnSave"));
		this.btnNew_SchoolCoreIncome
				.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseDialog_btnNewCoreIncome"));
		this.btnNew_SchoolNonCoreIncome
				.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseDialog_btnNewNonCoreIncome"));
		this.btnNew_SchoolExpense
				.setVisible(getUserWorkspace().isAllowed("button_OrganizationIncomeExpenseDialog_btnNewExpense"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(IncomeExpenseHeader incomeExpenseHeader) {
		logger.debug(Literal.ENTERING);

		if (incomeExpenseHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(incomeExpenseHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				//doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			this.south.setVisible(false);
		}

		doWriteBeanToComponents(incomeExpenseHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(IncomeExpenseHeader incomeExpenseHeader) {
		renderCoreIncomeDetails(incomeExpenseHeader);
		renderNonCoreIncomeDetails(incomeExpenseHeader);
		renderExpenseDetails(incomeExpenseHeader);
		this.recordStatus.setValue(incomeExpenseHeader.getRecordStatus());
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.incomeExpenseHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.incomeExpenseHeader.isNewRecord()) {
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

	public void onClick$btnSave(Event event) throws InterruptedException {
		doSave();
	}

	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
		BeanUtils.copyProperties(this.incomeExpenseHeader, incomeExpenseHeader);
		boolean isNew = false;
		if (!doWriteComponentsToBean(incomeExpenseHeader)) {
			return;
		}

		isNew = incomeExpenseHeader.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(incomeExpenseHeader.getRecordType())) {
				incomeExpenseHeader.setVersion(incomeExpenseHeader.getVersion() + 1);
				if (isNew) {
					incomeExpenseHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					incomeExpenseHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					incomeExpenseHeader.setNewRecord(true);
				}
			}
		} else {
			incomeExpenseHeader.setVersion(incomeExpenseHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(incomeExpenseHeader, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean doProcess(IncomeExpenseHeader incomeExpenseHeader, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		incomeExpenseHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		incomeExpenseHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		incomeExpenseHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			incomeExpenseHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(incomeExpenseHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, incomeExpenseHeader);
				}

				if (isNotesMandatory(taskId, incomeExpenseHeader)) {
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

			incomeExpenseHeader.setTaskId(taskId);
			incomeExpenseHeader.setNextTaskId(nextTaskId);
			incomeExpenseHeader.setRoleCode(getRole());
			incomeExpenseHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(incomeExpenseHeader, tranType);
			String operationRefs = getServiceOperations(taskId, incomeExpenseHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(incomeExpenseHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(incomeExpenseHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		IncomeExpenseHeader incomeExpenseHeader = (IncomeExpenseHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = incomeExpenseDetailService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = incomeExpenseDetailService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = incomeExpenseDetailService.doApprove(auditHeader);

						if (incomeExpenseHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = incomeExpenseDetailService.doReject(auditHeader);
						if (incomeExpenseHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						ErrorControl.showErrorControl(this.window_IncomeExpenseDetailsDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.incomeExpenseHeader), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public boolean doWriteComponentsToBean(IncomeExpenseHeader incomeExpenseHeader) throws InterruptedException {

		incomeExpenseHeader.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		incomeExpenseHeader.setCreatedOn(DateUtility.getAppDate());

		ArrayList<WrongValueException> wve = new ArrayList<>();
		for (Listitem listitem : listBoxSchoolCoreIncomeDetails.getItems()) {
			try {
				setValue(listitem, "category");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "noOfStudents");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "feeCharged");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "frqOfCollection");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "totalCore");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "considered");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve, this.OrgSchoolIncomeDetailsTab);
			IncomeExpenseDetail aSchoolCoreIncome = (IncomeExpenseDetail) listitem.getAttribute("data");
			aSchoolCoreIncome.setIncomeExpense("INCOME");
			aSchoolCoreIncome.setIncomeExpenseCode("INCOME");
			aSchoolCoreIncome.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aSchoolCoreIncome.setCreatedOn(DateUtility.getAppDate());

			boolean isNew = false;
			isNew = aSchoolCoreIncome.isNew();
			String tranType = "";

			if (aSchoolCoreIncome.isNewRecord()) {
				aSchoolCoreIncome.setVersion(1);
				aSchoolCoreIncome.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aSchoolCoreIncome.getRecordType())) {
				aSchoolCoreIncome.setVersion(aSchoolCoreIncome.getVersion() + 1);
				aSchoolCoreIncome.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aSchoolCoreIncome.getRecordType().equals(PennantConstants.RCD_ADD) && aSchoolCoreIncome.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aSchoolCoreIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			} else {
				aSchoolCoreIncome.setVersion(aSchoolCoreIncome.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			try {
				AuditHeader auditHeader = newCoreIncomeProcess(aSchoolCoreIncome, tranType);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
					setCoreIncomeDetailList(incomeExpenseHeader.getCoreIncomeList());
					return false;
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					setCoreIncomeDetailList(schoolCoreIncomes);
				}
			} catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}

		}

		for (Listitem listitem : listBoxSchoolNonCoreIncomeDetails.getItems()) {
			try {
				setValue(listitem, "prodService");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "noOfUnitsServed");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "totalNonCore");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "avgCollectionPerUnit");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "nonCoreconsidered");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve, this.OrgSchoolIncomeDetailsTab);

			IncomeExpenseDetail aSchoolNonCoreIncome = (IncomeExpenseDetail) listitem.getAttribute("data");
			aSchoolNonCoreIncome.setIncomeExpense("INCOME");
			aSchoolNonCoreIncome.setIncomeExpenseCode("INCOME");
			aSchoolNonCoreIncome.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aSchoolNonCoreIncome.setCreatedOn(DateUtility.getAppDate());

			boolean isNew = false;
			isNew = aSchoolNonCoreIncome.isNew();
			String tranType = "";

			if (aSchoolNonCoreIncome.isNewRecord()) {
				aSchoolNonCoreIncome.setVersion(1);
				aSchoolNonCoreIncome.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aSchoolNonCoreIncome.getRecordType())) {
				aSchoolNonCoreIncome.setVersion(aSchoolNonCoreIncome.getVersion() + 1);
				aSchoolNonCoreIncome.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aSchoolNonCoreIncome.getRecordType().equals(PennantConstants.RCD_ADD)
					&& aSchoolNonCoreIncome.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aSchoolNonCoreIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			} else {
				aSchoolNonCoreIncome.setVersion(aSchoolNonCoreIncome.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			try {
				AuditHeader auditHeader = newNonCoreIncomeProcess(aSchoolNonCoreIncome, tranType);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
					setCoreIncomeDetailList(incomeExpenseHeader.getCoreIncomeList());
					setNonCoreIncomeDetailList(incomeExpenseHeader.getNonCoreIncomeList());
					return false;
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					setNonCoreIncomeDetailList(schoolNonCoreIncomes);
				}
			} catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
		}

		for (Listitem listitem : listBoxSchoolExpenseDetails.getItems()) {
			try {
				setValue(listitem, "expenseType");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "expenseIncurred");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "expenseConsidered");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve, this.OrgSchoolExpenseDetailsTab);

			IncomeExpenseDetail aSchoolExpense = (IncomeExpenseDetail) listitem.getAttribute("data");
			aSchoolExpense.setIncomeExpense("EXPENSE");
			aSchoolExpense.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aSchoolExpense.setCreatedOn(DateUtility.getAppDate());

			boolean isNew = false;
			isNew = aSchoolExpense.isNew();
			String tranType = "";

			if (aSchoolExpense.isNewRecord()) {
				aSchoolExpense.setVersion(1);
				aSchoolExpense.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aSchoolExpense.getRecordType())) {
				aSchoolExpense.setVersion(aSchoolExpense.getVersion() + 1);
				aSchoolExpense.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aSchoolExpense.getRecordType().equals(PennantConstants.RCD_ADD) && aSchoolExpense.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aSchoolExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			} else {
				aSchoolExpense.setVersion(aSchoolExpense.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			try {
				AuditHeader auditHeader = newSchoolExpenseProcess(aSchoolExpense, tranType);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
					setCoreIncomeDetailList(incomeExpenseHeader.getCoreIncomeList());
					setNonCoreIncomeDetailList(incomeExpenseHeader.getNonCoreIncomeList());
					setExpenseDetailList(incomeExpenseHeader.getExpenseList());
					return false;
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					setExpenseDetailList(schoolExpenses);
				}
			} catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
		}
		incomeExpenseHeader.setCoreIncomeList(this.coreIncomeDetailList);
		incomeExpenseHeader.setNonCoreIncomeList(this.nonCoreIncomeDetailList);
		incomeExpenseHeader.setExpenseList(this.expenseDetailList);
		return true;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		if (wve.size() > 0) {
			setCoreIncomeDetailList(incomeExpenseHeader.getCoreIncomeList());
			setNonCoreIncomeDetailList(incomeExpenseHeader.getNonCoreIncomeList());
			setExpenseDetailList(incomeExpenseHeader.getExpenseList());
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newSchoolExpenseProcess(IncomeExpenseDetail aSchoolExpense, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aSchoolExpense, tranType);
		schoolExpenses = new ArrayList<>();
		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aSchoolExpense.getCategory());
		errParm[0] = "Category" + ":" + valueParm[0];

		List<IncomeExpenseDetail> incomeExpenseList = null;

		if (CollectionUtils.isNotEmpty(expenseDetailList)) {
			incomeExpenseList = expenseDetailList;
		}

		if (CollectionUtils.isNotEmpty(incomeExpenseList)) {
			for (int i = 0; i < incomeExpenseList.size(); i++) {
				IncomeExpenseDetail incomeExpenseDetail = incomeExpenseList.get(i);

				if ((incomeExpenseDetail.getCategory()).equals(aSchoolExpense.getCategory())
						&& incomeExpenseDetail.getIncomeExpense().equals(aSchoolExpense.getIncomeExpense())
						&& incomeExpenseDetail.getIncomeExpenseCode().equals(aSchoolExpense.getIncomeExpenseCode())) { // Both Current and Existing list rating same

					if (aSchoolExpense.isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							schoolExpenses.add(incomeExpenseDetail);
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							schoolExpenses.add(incomeExpenseDetail);
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < incomeExpenseHeader.getCoreIncomeList().size(); j++) {
								IncomeExpenseDetail income = incomeExpenseHeader.getCoreIncomeList().get(j);
								if (income.getCategory().equals(aSchoolExpense.getCategory())) {
									schoolExpenses.add(income);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							schoolExpenses.add(incomeExpenseDetail);
						}
					}
				} else {
					schoolExpenses.add(incomeExpenseDetail);
				}
			}
		}

		if (!recordAdded) {
			schoolExpenses.add(aSchoolExpense);
		}
		return auditHeader;
	}

	private AuditHeader newNonCoreIncomeProcess(IncomeExpenseDetail aincomeExpenseDetail, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aincomeExpenseDetail, tranType);
		schoolNonCoreIncomes = new ArrayList<>();
		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aincomeExpenseDetail.getCategory());

		errParm[0] = "Category" + ":" + valueParm[0];

		List<IncomeExpenseDetail> incomeExpenseList = null;

		if (CollectionUtils.isNotEmpty(nonCoreIncomeDetailList)) {
			incomeExpenseList = nonCoreIncomeDetailList;
		}

		if (CollectionUtils.isNotEmpty(incomeExpenseList)) {
			for (int i = 0; i < incomeExpenseList.size(); i++) {
				IncomeExpenseDetail incomeExpenseDetail = incomeExpenseList.get(i);

				if ((incomeExpenseDetail.getCategory()).equals(aincomeExpenseDetail.getCategory())) {

					if (aincomeExpenseDetail.isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							schoolNonCoreIncomes.add(incomeExpenseDetail);
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							schoolNonCoreIncomes.add(incomeExpenseDetail);
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < incomeExpenseHeader.getCoreIncomeList().size(); j++) {
								IncomeExpenseDetail income = incomeExpenseHeader.getCoreIncomeList().get(j);
								if (income.getCategory().equals(aincomeExpenseDetail.getCategory())) {
									schoolNonCoreIncomes.add(income);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							schoolNonCoreIncomes.add(incomeExpenseDetail);
						}
					}
				} else {
					schoolNonCoreIncomes.add(incomeExpenseDetail);
				}
			}
		}

		if (!recordAdded) {
			schoolNonCoreIncomes.add(aincomeExpenseDetail);
		}
		return auditHeader;
	}

	private void setValue(Listitem listitem, String comonentId) {
		IncomeExpenseDetail incomeExpenseDetail = null;

		incomeExpenseDetail = (IncomeExpenseDetail) listitem.getAttribute("data");
		switch (comonentId) {
		case "category":
			Hbox hbox1 = (Hbox) getComponent(listitem, "category");
			Combobox combobox = (Combobox) hbox1.getLastChild();
			String category = getComboboxValue(combobox);
			if (!combobox.isDisabled() && "#".equals(category)) {
				throw new WrongValueException(combobox,
						Labels.getLabel("STATIC_INVALID", new String[] { "Cateogory" }));
			}
			incomeExpenseDetail.setCategory(category);
			break;
		case "noOfStudents":
			int noOfStudents = 0;
			Hbox hbox2 = (Hbox) getComponent(listitem, "noOfStudents");
			Intbox intbox1 = (Intbox) hbox2.getLastChild();

			if (intbox1.getValue() != null) {
				noOfStudents = intbox1.getValue();
			}

			if (!intbox1.isReadonly() && noOfStudents <= 0) {
				throw new WrongValueException(intbox1,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "No Of Students" }));
			}
			incomeExpenseDetail.setUnits(noOfStudents);
			break;
		case "feeCharged":
			BigDecimal feeCharged = BigDecimal.ZERO;
			Hbox hbox3 = (Hbox) getComponent(listitem, "feeCharged");
			CurrencyBox textBox1 = (CurrencyBox) hbox3.getLastChild();
			if (textBox1.getValidateValue() != null) {
				feeCharged = textBox1.getValidateValue();
			}
			if (!(textBox1.isReadonly()) && (feeCharged.intValue() <= 0)) {
				throw new WrongValueException(textBox1,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Fee Charged" }));
			}
			incomeExpenseDetail.setUnitPrice(PennantAppUtil.unFormateAmount(feeCharged, 2));
			break;
		case "frqOfCollection":
			Hbox hbox4 = (Hbox) getComponent(listitem, "frqOfCollection");
			Combobox combobox2 = (Combobox) hbox4.getLastChild();
			String frqOfCollection = getComboboxValue(combobox2);
			if (!combobox2.isDisabled() && "#".equals(frqOfCollection)) {
				throw new WrongValueException(combobox2,
						Labels.getLabel("STATIC_INVALID", new String[] { "Collection Frequency" }));
			}
			incomeExpenseDetail.setFrequency(Integer.parseInt(frqOfCollection));
			break;
		case "totalCore":
			BigDecimal totalCore = BigDecimal.ZERO;
			CurrencyBox decimalbox = (CurrencyBox) getComponent(listitem, "totalCore");
			if (decimalbox.getValidateValue() != null) {
				totalCore = decimalbox.getValidateValue();
			}
			incomeExpenseDetail.setTotal(PennantAppUtil.unFormateAmount(totalCore, 2));
			break;
		case "considered":
			Checkbox checkbox = (Checkbox) getComponent(listitem, "considered");
			incomeExpenseDetail.setConsider(checkbox.isChecked());
			break;
		case "prodService":
			ExtendedCombobox extCombobox = (ExtendedCombobox) getComponent(listitem, "prodService");
			if (!extCombobox.isReadonly() && extCombobox.getValue().isEmpty()) {
				throw new WrongValueException(extCombobox,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "Product/Service" }));
			}
			incomeExpenseDetail.setCategory(extCombobox.getValue());
			extCombobox.getValidatedValue();
			Object object = extCombobox.getAttribute("FieldCodeId");
			if (object != null) {
				incomeExpenseDetail.setLoockUpId(Long.parseLong(object.toString()));
			} else {
				incomeExpenseDetail.setLoockUpId(null);
			}
			break;
		case "noOfUnitsServed":
			int noOfUnitsServed = 0;
			Hbox hbox5 = (Hbox) getComponent(listitem, "noOfUnitsServed");
			Intbox inybox1 = (Intbox) hbox5.getLastChild();
			if (inybox1.getValue() != null) {
				noOfUnitsServed = inybox1.getValue();
			}
			if (!inybox1.isReadonly() && noOfUnitsServed <= 0) {
				throw new WrongValueException(inybox1,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Number Of Units" }));
			}
			incomeExpenseDetail.setUnits(noOfUnitsServed);
			break;
		case "avgCollectionPerUnit":
			BigDecimal avgCollPerUnit = BigDecimal.ZERO;
			Hbox hbox6 = (Hbox) getComponent(listitem, "avgCollectionPerUnit");
			CurrencyBox decimalbox1 = (CurrencyBox) hbox6.getLastChild();
			if (decimalbox1.getValidateValue() != null) {
				avgCollPerUnit = decimalbox1.getValidateValue();
			}
			if (!(decimalbox1.isReadonly()) && (avgCollPerUnit.intValue() <= 0)) {
				throw new WrongValueException(decimalbox1, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] { "Average Collection Per Unit" }));
			}
			incomeExpenseDetail.setUnitPrice(PennantAppUtil.unFormateAmount(avgCollPerUnit, 2));
			break;
		case "totalNonCore":
			CurrencyBox decimalbox3 = (CurrencyBox) getComponent(listitem, "totalNonCore");
			BigDecimal totalNonCore = decimalbox3.getValidateValue();
			incomeExpenseDetail.setTotal(PennantAppUtil.unFormateAmount(totalNonCore, 2));
			break;
		case "nonCoreconsidered":
			Checkbox checkbox1 = (Checkbox) getComponent(listitem, "nonCoreconsidered");
			incomeExpenseDetail.setConsider(checkbox1.isChecked());
			break;
		case "expenseType":
			ExtendedCombobox extCombobox1 = (ExtendedCombobox) getComponent(listitem, "expenseType");
			if (!extCombobox1.isReadonly() && extCombobox1.getValue().isEmpty()) {
				throw new WrongValueException(extCombobox1,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "ExpenseType" }));
			}
			incomeExpenseDetail.setIncomeExpense(extCombobox1.getAttribute("IncomeExpense").toString());
			incomeExpenseDetail.setCategory(extCombobox1.getAttribute("Category").toString());
			incomeExpenseDetail.setIncomeExpenseCode(extCombobox1.getValue());
			break;
		case "expenseIncurred":
			BigDecimal expenseIncurred = BigDecimal.ZERO;
			Hbox hbox7 = (Hbox) getComponent(listitem, "expenseIncurred");
			CurrencyBox decimalbox2 = (CurrencyBox) hbox7.getLastChild();
			if (decimalbox2.getValidateValue() != null) {
				expenseIncurred = decimalbox2.getValidateValue();
			}
			if (!(decimalbox2.isReadonly()) && (expenseIncurred.intValue() <= 0)) {
				throw new WrongValueException(decimalbox2,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Expense Incurred" }));
			}
			incomeExpenseDetail.setUnitPrice(PennantAppUtil.unFormateAmount(expenseIncurred, 2));
			incomeExpenseDetail.setTotal(PennantAppUtil.unFormateAmount(expenseIncurred, 2));
			break;
		case "expenseConsidered":
			Checkbox checkbox2 = (Checkbox) getComponent(listitem, "expenseConsidered");
			incomeExpenseDetail.setConsider(checkbox2.isChecked());
			break;
		default:
			break;
		}
		incomeExpenseDetail.setRecordStatus(this.recordStatus.getValue());
	}

	private Component getComponent(Listitem listitem, String listcellId) {
		List<Listcell> listcels = listitem.getChildren();

		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}

			id = id.replaceAll("\\d", "");
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
	}

	private AuditHeader newCoreIncomeProcess(IncomeExpenseDetail aincomeExpenseDetail, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aincomeExpenseDetail, tranType);
		schoolCoreIncomes = new ArrayList<>();
		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aincomeExpenseDetail.getCategory());

		errParm[0] = "Category" + ":" + valueParm[0];

		List<IncomeExpenseDetail> incomeExpenseList = null;

		if (CollectionUtils.isNotEmpty(coreIncomeDetailList)) {
			incomeExpenseList = coreIncomeDetailList;
		}

		if (CollectionUtils.isNotEmpty(incomeExpenseList)) {
			for (int i = 0; i < incomeExpenseList.size(); i++) {
				IncomeExpenseDetail incomeExpenseDetail = incomeExpenseList.get(i);
				if ((incomeExpenseDetail.getCategory()).equals(aincomeExpenseDetail.getCategory())) {
					if (aincomeExpenseDetail.isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							schoolCoreIncomes.add(incomeExpenseDetail);
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							schoolCoreIncomes.add(incomeExpenseDetail);
						} else if (incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < incomeExpenseHeader.getCoreIncomeList().size(); j++) {
								IncomeExpenseDetail income = incomeExpenseHeader.getCoreIncomeList().get(j);
								if (income.getCategory().equals(aincomeExpenseDetail.getCategory())) {
									schoolCoreIncomes.add(income);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							schoolCoreIncomes.add(incomeExpenseDetail);
						}
					}
				} else {
					schoolCoreIncomes.add(incomeExpenseDetail);
				}
			}
		}

		if (!recordAdded) {
			schoolCoreIncomes.add(aincomeExpenseDetail);
		}
		return auditHeader;
	}

	private AuditHeader getAuditHeader(IncomeExpenseDetail aIncomeExpenseDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aIncomeExpenseDetail.getBefImage(),
				aIncomeExpenseDetail);

		return new AuditHeader(getReference(), String.valueOf(aIncomeExpenseDetail.getCustId()), null, null,
				auditDetail, aIncomeExpenseDetail.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNew_SchoolCoreIncome(Event event) {
		logger.debug(Literal.ENTERING);

		IncomeExpenseDetail schCoreIncome = new IncomeExpenseDetail();
		schCoreIncome.setNewRecord(true);
		schCoreIncome.setWorkflowId(0);
		schCoreIncome.setName(incomeExpenseHeader.getName());
		schCoreIncome.setIncomeExpenseType(IncomeExpenseType.CORE_INCOME.name());
		schCoreIncome.setCoreIncome(true);

		incomeExpenseHeader.setSchoolIncomeExpense(schCoreIncome);
		renderCoreIncomeDetails(incomeExpenseHeader);
		logger.debug(Literal.LEAVING);

	}

	public void renderCoreIncomeDetails(IncomeExpenseHeader incomeExpenseHeader) {
		int size = 0;
		if (incomeExpenseHeader.getCoreIncomeList().size() > 0
				&& incomeExpenseHeader.getSchoolIncomeExpense() == null) {
			setCoreIncomeDetailList(incomeExpenseHeader.getCoreIncomeList());
			size = incomeExpenseHeader.getCoreIncomeList().size();
		} else if (incomeExpenseHeader.getSchoolIncomeExpense() != null) {
			size = 1;
		}

		for (int i = 0; i < size; i++) {
			IncomeExpenseDetail schIncome;
			coreIncomeCount++;

			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getCoreIncomeList())
					&& incomeExpenseHeader.getSchoolIncomeExpense() == null) {
				schIncome = incomeExpenseHeader.getCoreIncomeList().get(i);
			} else {
				schIncome = incomeExpenseHeader.getSchoolIncomeExpense();
			}
			Listitem item = new Listitem();
			Listcell listCell;
			Hbox hbox;
			Space space;

			// School Name
			listCell = new Listcell();
			Textbox schoolName = new Textbox();
			schoolName.setReadonly(true);
			listCell.appendChild(schoolName);
			schoolName.setValue(incomeExpenseHeader.getName());
			listCell.setParent(item);

			// Financial year
			listCell = new Listcell();
			listCell.setId("finYear".concat(String.valueOf(coreIncomeCount)));
			Intbox finYear = new Intbox();
			finYear.setValue(incomeExpenseHeader.getFinancialYear());
			finYear.setReadonly(true);
			listCell.appendChild(finYear);
			listCell.setParent(item);

			// Category
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("category".concat(String.valueOf(coreIncomeCount)));
			Combobox category = new Combobox();
			fillComboBox(category, schIncome.getCategory(), categories, "");
			if (!schIncome.isNewRecord()) {
				category.setDisabled(true);
			}
			hbox.appendChild(space);
			hbox.appendChild(category);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// Number Of Students
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("noOfStudents".concat(String.valueOf(coreIncomeCount)));
			Intbox noOfStudents = new Intbox();
			noOfStudents.addForward("onChange", self, "onChangeCalculateFeeReceiptFrq", item);
			noOfStudents.setValue(schIncome.getUnits());
			noOfStudents.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_Units"));
			hbox.appendChild(space);
			hbox.appendChild(noOfStudents);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// Fee Charged Per Student P.A
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("feeCharged".concat(String.valueOf(coreIncomeCount)));
			CurrencyBox feeCharged = new CurrencyBox();
			feeCharged.getNextSibling();
			feeCharged.setFormat(PennantApplicationUtil.getAmountFormate(2));
			feeCharged.setScale(2);
			feeCharged.addForward("onValueChange", self, "onChangeCalculateFeeReceiptFrq", item);
			feeCharged.setValue(PennantAppUtil.formateAmount(schIncome.getUnitPrice(), 2));
			feeCharged.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_UnitPrice"));
			hbox.appendChild(space);
			hbox.appendChild(feeCharged);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// Frequency Of Collection
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("frqOfCollection".concat(String.valueOf(coreIncomeCount)));
			Combobox frqOfCollection = new Combobox();
			fillComboBox(frqOfCollection, String.valueOf(schIncome.getFrequency()), frqOfCollectionList, "");
			frqOfCollection.addForward("onChange", self, "onChangeFrqOfCollection", item);
			frqOfCollection.setDisabled(isReadOnly("OrganizationIncomeExpenseDialog_CollectionFrequency"));
			hbox.appendChild(space);
			hbox.appendChild(frqOfCollection);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// Multiplier
			listCell = new Listcell();
			listCell.setId("multiplier".concat(String.valueOf(coreIncomeCount)));
			Intbox multiplier = new Intbox();
			multiplier.setReadonly(true);
			multiplier.addForward("onChange", self, "onChangeCalculateFeeReceiptFrq", item);
			if (!"#".equals(getComboboxValue(frqOfCollection))) {
				multiplier.setValue(Integer.parseInt(getComboboxValue(frqOfCollection)));
			}
			listCell.appendChild(multiplier);
			listCell.setParent(item);

			// Fee Receipt Basis Frequency
			listCell = new Listcell();
			listCell.setId("feeRecBasisFrq".concat(String.valueOf(coreIncomeCount)));
			CurrencyBox feeRecBasisFrq = new CurrencyBox();
			feeRecBasisFrq.setFormat(PennantApplicationUtil.getAmountFormate(2));
			feeRecBasisFrq.setScale(2);
			feeRecBasisFrq.setReadonly(true);
			BigDecimal feeCharge = BigDecimal.ZERO;
			int multiply = 0;
			if (feeCharged.getValidateValue().intValue() != 0 && multiplier.getValue() != null
					&& multiplier.getValue().intValue() != 0) {
				feeCharge = PennantAppUtil.unFormateAmount(feeCharged.getValidateValue(), 2);
				multiply = multiplier.getValue();
				feeRecBasisFrq.setValue(PennantAppUtil
						.formateAmount(feeCharge.divide(new BigDecimal(multiply), BigDecimal.ROUND_HALF_DOWN), 2));
			}
			listCell.appendChild(feeRecBasisFrq);
			listCell.setParent(item);

			// Total Core
			listCell = new Listcell();
			listCell.setId("totalCore".concat(String.valueOf(coreIncomeCount)));
			CurrencyBox totalCore = new CurrencyBox();
			totalCore.setFormat(PennantApplicationUtil.getAmountFormate(2));
			totalCore.setScale(2);
			totalCore.setReadonly(true);
			totalCore.setValue(PennantAppUtil.formateAmount(schIncome.getTotal(), 2));
			listCell.appendChild(totalCore);
			listCell.setParent(item);

			// To Be Considered
			listCell = new Listcell();
			listCell.setId("considered".concat(String.valueOf(coreIncomeCount)));
			Checkbox considered = new Checkbox();
			if (schIncome.isNewRecord()) {
				considered.setChecked(true);
			} else {
				considered.setChecked(schIncome.isConsider());
			}
			listCell.appendChild(considered);
			listCell.setParent(item);

			listCell = new Listcell();
			listCell.setId("coreDeleteButton".concat(String.valueOf(coreIncomeCount)));
			Button button = new Button();
			button.setSclass("z-toolbarbutton");
			button.setLabel("Delete");
			button.addForward("onClick", self, "onClickCoreIncomeButtonDelete", item);
			listCell.appendChild(button);
			listCell.setParent(item);
			if (!schIncome.isNewRecord()) {
				button.setVisible(false);
			}

			item.setAttribute("data", schIncome);

			if (!this.listBoxSchoolCoreIncomeDetails.getItems().isEmpty()) {
				this.listBoxSchoolCoreIncomeDetails.getItems().add(0, item);
			} else {
				this.listBoxSchoolCoreIncomeDetails.appendChild(item);
			}
		}
	}

	public void onClickCoreIncomeButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxSchoolCoreIncomeDetails.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
	}

	public void onClickNonCoreIncomeButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxSchoolNonCoreIncomeDetails.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
	}

	public void onClickExpenseButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxSchoolExpenseDetails.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
	}

	public void onChangeCalculateFeeReceiptFrq(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = new Listitem();
		if (event != null) {
			item = (Listitem) event.getData();
		}

		Hbox hbox1 = (Hbox) getComponent(item, "noOfStudents");
		Intbox noOfStudents = (Intbox) hbox1.getLastChild();
		Hbox hbox2 = (Hbox) getComponent(item, "feeCharged");
		CurrencyBox feeCharged = (CurrencyBox) hbox2.getLastChild();
		Intbox multiplier = (Intbox) getComponent(item, "multiplier");
		CurrencyBox feeRecBasisFrq = (CurrencyBox) getComponent(item, "feeRecBasisFrq");
		CurrencyBox totalCore = (CurrencyBox) getComponent(item, "totalCore");

		int mult = 0;
		BigDecimal fee = BigDecimal.ZERO;
		int students = 0;

		if (feeCharged.getValidateValue() != null) {
			fee = feeCharged.getValidateValue();
		}

		if (noOfStudents.getValue() != null) {
			students = noOfStudents.getValue();
		}

		if (multiplier.getValue() != null) {
			mult = multiplier.getValue();
		}

		if (mult != 0) {
			feeRecBasisFrq.setValue(fee.divide(new BigDecimal(mult), BigDecimal.ROUND_HALF_DOWN));
		}
		totalCore.setValue(fee.multiply(new BigDecimal(students)));
		logger.debug(Literal.LEAVING);
	}

	public void onChangeFrqOfCollection(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = new Listitem();
		if (event != null) {
			item = (Listitem) event.getData();
		}
		Hbox hbox1 = (Hbox) getComponent(item, "frqOfCollection");
		Combobox frqCollection = (Combobox) hbox1.getLastChild();
		Intbox multiplier = (Intbox) getComponent(item, "multiplier");
		CurrencyBox feeRecBasisFrq = (CurrencyBox) getComponent(item, "feeRecBasisFrq");
		String frqValue = getComboboxValue(frqCollection);
		if (!"#".equals(frqValue)) {
			multiplier.setValue(Integer.parseInt(frqValue));
			onChangeCalculateFeeReceiptFrq(event);
		} else {
			multiplier.setValue(0);
			feeRecBasisFrq.setValue(BigDecimal.ZERO);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChangeCalculateNonCoreTotal(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = new Listitem();
		if (event != null) {
			item = (Listitem) event.getData();
		}
		Hbox hbox1 = (Hbox) getComponent(item, "noOfUnitsServed");
		Intbox noOfUnitsServed = (Intbox) hbox1.getLastChild();

		Hbox hbox2 = (Hbox) getComponent(item, "avgCollectionPerUnit");
		CurrencyBox avgCollectionPerUnit = (CurrencyBox) hbox2.getLastChild();

		CurrencyBox totalNonCore = (CurrencyBox) getComponent(item, "totalNonCore");
		int noOfUnits = 0;
		BigDecimal avgCollection = BigDecimal.ZERO;
		if (noOfUnitsServed.getValue() != null && avgCollectionPerUnit.getValidateValue() != null) {
			noOfUnits = noOfUnitsServed.getValue();
			avgCollection = avgCollectionPerUnit.getValidateValue();
		}
		totalNonCore.setValue(avgCollection.multiply(new BigDecimal(noOfUnits)));

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew_SchoolNonCoreIncome(Event event) {
		logger.debug(Literal.ENTERING);

		IncomeExpenseDetail schNonCoreIncome = new IncomeExpenseDetail();
		schNonCoreIncome.setNewRecord(true);
		schNonCoreIncome.setOrgId(incomeExpenseHeader.getOrgId());
		schNonCoreIncome.setWorkflowId(0);
		schNonCoreIncome.setName(incomeExpenseHeader.getName());
		schNonCoreIncome.setIncomeExpenseType(IncomeExpenseType.NON_CORE_INCOME.name());
		schNonCoreIncome.setCoreIncome(false);

		incomeExpenseHeader.setSchoolIncomeExpense(schNonCoreIncome);
		renderNonCoreIncomeDetails(incomeExpenseHeader);

		logger.debug(Literal.LEAVING);

	}

	public void renderNonCoreIncomeDetails(IncomeExpenseHeader incomeExpenseHeader) {

		int size = 0;
		if (incomeExpenseHeader.getNonCoreIncomeList().size() > 0
				&& incomeExpenseHeader.getSchoolIncomeExpense() == null) {
			size = incomeExpenseHeader.getNonCoreIncomeList().size();
			setNonCoreIncomeDetailList(incomeExpenseHeader.getNonCoreIncomeList());
		} else if (incomeExpenseHeader.getSchoolIncomeExpense() != null) {
			size = 1;
		}

		for (int i = 0; i < size; i++) {
			IncomeExpenseDetail schNonCoreIncome;
			nonCoreIncomeCount++;

			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getNonCoreIncomeList())
					&& incomeExpenseHeader.getSchoolIncomeExpense() == null) {
				schNonCoreIncome = incomeExpenseHeader.getNonCoreIncomeList().get(i);
			} else {
				schNonCoreIncome = incomeExpenseHeader.getSchoolIncomeExpense();
			}

			Listitem item = new Listitem();
			Listcell listCell;
			Hbox hbox;
			Space space;

			// School Name
			listCell = new Listcell();
			Textbox schoolName = new Textbox();
			schoolName.setReadonly(true);
			schoolName.setValue(incomeExpenseHeader.getName());
			listCell.appendChild(schoolName);
			listCell.setParent(item);

			// Financial year
			listCell = new Listcell();
			listCell.setId("nonCorefinYear".concat(String.valueOf(nonCoreIncomeCount)));
			Intbox finYear = new Intbox();
			finYear.setReadonly(true);
			finYear.setValue(incomeExpenseHeader.getFinancialYear());
			listCell.appendChild(finYear);
			listCell.setParent(item);

			// Product/Service
			listCell = new Listcell();
			listCell.setId("prodService".concat(String.valueOf(nonCoreIncomeCount)));
			ExtendedCombobox prodService = new ExtendedCombobox();

			prodService.setMaxlength(8);
			prodService.getTextbox().setMaxlength(50);
			prodService.setMandatoryStyle(true);
			prodService.setModuleName("ORG_SCHOOL_PRODUCT_TYPES");
			prodService.setValueColumn("FieldCodeValue");
			prodService.setDescColumn("ValueDesc");
			prodService.setValidateColumns(new String[] { "FieldCodeValue" });

			prodService.addForward("onFulfill", self, "onFullFillProdService", prodService);
			prodService.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_ProductService"));
			if (!schNonCoreIncome.isNewRecord()) {
				prodService.setReadonly(true);
				prodService.setValue(StringUtils.trimToEmpty(schNonCoreIncome.getLoockupValue()),
						StringUtils.trimToEmpty(schNonCoreIncome.getLoockupDesc()));
				if (schNonCoreIncome.getLoockUpId() != null) {
					prodService.setAttribute("FieldCodeId", schNonCoreIncome.getLoockUpId());
				} else {
					prodService.setAttribute("FieldCodeId", null);
				}
			}
			listCell.appendChild(prodService);
			listCell.setParent(item);

			// Number Of Units Served
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("noOfUnitsServed".concat(String.valueOf(nonCoreIncomeCount)));
			Intbox noOfUnitsServed = new Intbox();
			noOfUnitsServed.setValue(schNonCoreIncome.getUnits());
			noOfUnitsServed.addForward("onChange", self, "onChangeCalculateNonCoreTotal", item);
			noOfUnitsServed.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_NoOfUnits"));
			hbox.appendChild(space);
			hbox.appendChild(noOfUnitsServed);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// Average Collection Per Unit
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("avgCollectionPerUnit".concat(String.valueOf(nonCoreIncomeCount)));
			CurrencyBox avgCollectionPerUnit = new CurrencyBox();
			avgCollectionPerUnit.setFormat(PennantApplicationUtil.getAmountFormate(2));
			avgCollectionPerUnit.setScale(2);
			avgCollectionPerUnit.setValue(PennantAppUtil.formateAmount(schNonCoreIncome.getUnitPrice(), 2));
			avgCollectionPerUnit.addForward("onValueChange", self, "onChangeCalculateNonCoreTotal", item);
			avgCollectionPerUnit.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_AvgCollection"));
			hbox.appendChild(space);
			hbox.appendChild(avgCollectionPerUnit);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// Total NonCore
			listCell = new Listcell();
			listCell.setId("totalNonCore".concat(String.valueOf(nonCoreIncomeCount)));
			CurrencyBox totalNonCore = new CurrencyBox();
			totalNonCore.setFormat(PennantApplicationUtil.getAmountFormate(2));
			totalNonCore.setScale(2);
			totalNonCore.setReadonly(true);
			totalNonCore.setValue(PennantAppUtil.formateAmount(schNonCoreIncome.getTotal(), 2));
			listCell.appendChild(totalNonCore);
			listCell.setParent(item);

			// To Be Considered
			listCell = new Listcell();
			listCell.setId("nonCoreconsidered".concat(String.valueOf(nonCoreIncomeCount)));
			Checkbox considered = new Checkbox();
			if (schNonCoreIncome.isNewRecord()) {
				considered.setChecked(true);
			} else {
				considered.setChecked(schNonCoreIncome.isConsider());
			}
			listCell.appendChild(considered);
			listCell.setParent(item);

			listCell = new Listcell();
			listCell.setId("nonCoredeleteButton".concat(String.valueOf(nonCoreIncomeCount)));
			Button button = new Button();
			button.setSclass("z-toolbarbutton");
			button.setLabel("Delete");
			button.addForward("onClick", self, "onClickNonCoreIncomeButtonDelete", item);
			listCell.appendChild(button);
			listCell.setParent(item);
			if (!schNonCoreIncome.isNewRecord()) {
				button.setVisible(false);
			}

			item.setAttribute("data", schNonCoreIncome);
			if (!this.listBoxSchoolNonCoreIncomeDetails.getItems().isEmpty()) {
				this.listBoxSchoolNonCoreIncomeDetails.getItems().add(0, item);
			} else {
				this.listBoxSchoolNonCoreIncomeDetails.appendChild(item);
			}
		}
	}

	public void onFullFillProdService(Event event) {
		logger.debug(Literal.ENTERING);
		ExtendedCombobox prodService = (ExtendedCombobox) event.getData();
		Object dataObject = prodService.getObject();
		if (dataObject instanceof String || dataObject == null) {
			prodService.setValue("");
			prodService.setDescription("");
			prodService.setAttribute("FieldCodeId", null);
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			prodService.setAttribute("FieldCodeId", details.getFieldCodeId());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFullFillExpenseType(Event event) {
		logger.debug(Literal.ENTERING);
		ExtendedCombobox expenseType = (ExtendedCombobox) event.getData();
		Object dataObject = expenseType.getObject();
		if (dataObject instanceof String || dataObject == null) {
			expenseType.setValue("");
			expenseType.setDescription("");
		} else {
			IncomeType details = (IncomeType) dataObject;
			expenseType.setAttribute("IncomeExpense", details.getIncomeExpense());
			expenseType.setAttribute("Category", details.getCategory());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew_SchoolExpense(Event event) {
		logger.debug(Literal.ENTERING);

		IncomeExpenseDetail schExpense = new IncomeExpenseDetail();
		schExpense.setNewRecord(true);
		schExpense.setOrgId(incomeExpenseHeader.getOrgId());
		schExpense.setWorkflowId(0);
		schExpense.setName(incomeExpenseHeader.getName());
		schExpense.setIncomeExpenseType(IncomeExpenseType.EXPENSE.name());
		schExpense.setCoreIncome(false);

		incomeExpenseHeader.setSchoolIncomeExpense(schExpense);
		renderExpenseDetails(incomeExpenseHeader);

		logger.debug(Literal.LEAVING);

	}

	public void renderExpenseDetails(IncomeExpenseHeader incomeExpenseHeader) {

		int size = 0;
		if (incomeExpenseHeader.getExpenseList().size() > 0 && incomeExpenseHeader.getSchoolIncomeExpense() == null) {
			setExpenseDetailList(incomeExpenseHeader.getExpenseList());
			size = incomeExpenseHeader.getExpenseList().size();
		} else if (incomeExpenseHeader.getSchoolIncomeExpense() != null) {
			size = 1;
		}

		for (int i = 0; i < size; i++) {
			IncomeExpenseDetail schExpense;
			expenseCount++;

			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getExpenseList())
					&& incomeExpenseHeader.getSchoolIncomeExpense() == null) {
				schExpense = incomeExpenseHeader.getExpenseList().get(i);
			} else {
				schExpense = incomeExpenseHeader.getSchoolIncomeExpense();
			}

			Listitem item = new Listitem();
			Listcell listCell;
			Hbox hbox;
			Space space;
			// School Name
			listCell = new Listcell();
			Textbox schoolName = new Textbox();
			schoolName.setReadonly(true);
			schoolName.setValue(incomeExpenseHeader.getName());
			listCell.appendChild(schoolName);
			listCell.setParent(item);

			// Financial year
			listCell = new Listcell();
			listCell.setId("ExpenseFinYear".concat(String.valueOf(expenseCount)));
			Intbox finYear = new Intbox();
			finYear.setValue(incomeExpenseHeader.getFinancialYear());
			finYear.setReadonly(true);
			listCell.appendChild(finYear);
			listCell.setParent(item);

			// Expense Type
			listCell = new Listcell();
			listCell.setId("expenseType".concat(String.valueOf(expenseCount)));
			ExtendedCombobox expenseType = new ExtendedCombobox();
			expenseType.setMaxlength(8);
			expenseType.setMandatoryStyle(true);
			expenseType.setModuleName("IncomeExpense");
			expenseType.setValueColumn("IncomeTypeCode");
			expenseType.setDescColumn("IncomeTypeDesc");
			expenseType.setValidateColumns(new String[] { "IncomeExpense", "IncomeTypeCode", "Category" });
			expenseType.addForward("onFulfill", self, "onFullFillExpenseType", expenseType);
			Filter expenseTypeFilter[] = new Filter[1];
			expenseTypeFilter[0] = new Filter("IncomeExpense", PennantConstants.EXPENSE, Filter.OP_EQUAL);
			expenseType.setFilters(expenseTypeFilter);
			expenseType.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_ExpenseType"));
			if (!schExpense.isNewRecord()) {
				expenseType.setReadonly(true);
				expenseType.setValue(StringUtils.trimToEmpty(schExpense.getIncomeExpenseCode()),
						StringUtils.trimToEmpty(schExpense.getExpenseDesc()));
				if (schExpense.getIncomeExpenseCode() != null) {
					expenseType.setAttribute("IncomeExpense", schExpense.getIncomeExpense());
					expenseType.setAttribute("Category", schExpense.getCategory());
				} else {
					expenseType.setAttribute("IncomeExpense", null);
					expenseType.setAttribute("Category", null);
				}
			}
			listCell.appendChild(expenseType);
			listCell.setParent(item);

			// Expense Incurred
			listCell = new Listcell();
			hbox = new Hbox();
			space = new Space();
			space.setSpacing("2px");
			space.setSclass("mandatory");
			listCell.setId("expenseIncurred".concat(String.valueOf(expenseCount)));
			CurrencyBox expenseIncurred = new CurrencyBox();
			expenseIncurred.setFormat(PennantApplicationUtil.getAmountFormate(2));
			expenseIncurred.setScale(2);
			expenseIncurred.setValue(PennantAppUtil.formateAmount(schExpense.getUnitPrice(), 2));
			expenseIncurred.setReadonly(isReadOnly("OrganizationIncomeExpenseDialog_ExpenseIncurred"));
			hbox.appendChild(space);
			hbox.appendChild(expenseIncurred);
			listCell.appendChild(hbox);
			listCell.setParent(item);

			// To Be Considered
			listCell = new Listcell();
			listCell.setId("expenseConsidered".concat(String.valueOf(expenseCount)));
			Checkbox considered = new Checkbox();
			if (schExpense.isNewRecord()) {
				considered.setChecked(true);
			} else {
				considered.setChecked(schExpense.isConsider());
			}
			listCell.appendChild(considered);
			listCell.setParent(item);

			listCell = new Listcell();
			listCell.setId("expDeleteButton".concat(String.valueOf(expenseCount)));
			Button button = new Button();
			button.setSclass("z-toolbarbutton");
			button.setLabel("Delete");
			button.addForward("onClick", self, "onClickExpenseButtonDelete", item);
			listCell.appendChild(button);
			listCell.setParent(item);
			if (!schExpense.isNewRecord()) {
				button.setVisible(false);
			}
			item.setAttribute("data", schExpense);

			if (!this.listBoxSchoolExpenseDetails.getItems().isEmpty()) {
				this.listBoxSchoolExpenseDetails.getItems().add(0, item);
			} else {
				this.listBoxSchoolExpenseDetails.appendChild(item);
			}
		}

	}

	private AuditHeader getAuditHeader(IncomeExpenseHeader incomeExpenseHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, incomeExpenseHeader.getBefImage(), incomeExpenseHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, incomeExpenseHeader.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final IncomeExpenseHeader entity = new IncomeExpenseHeader();
		BeanUtils.copyProperties(this.incomeExpenseHeader, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ entity.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (("").equals(StringUtils.trimToEmpty(entity.getRecordType()))) {
				entity.setVersion(entity.getVersion() + 1);
				entity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					entity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					entity.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), entity.getNextTaskId(), entity);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(entity, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		incomeExpenseDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.incomeExpenseHeader);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_IncomeExpenseDetailsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.incomeExpenseHeader.getId());
	}

	public List<IncomeExpenseDetail> getCoreIncomeDetailList() {
		return coreIncomeDetailList;
	}

	public void setCoreIncomeDetailList(List<IncomeExpenseDetail> coreIncomeDetailList) {
		this.coreIncomeDetailList = coreIncomeDetailList;
	}

	public List<IncomeExpenseDetail> getNonCoreIncomeDetailList() {
		return nonCoreIncomeDetailList;
	}

	public void setNonCoreIncomeDetailList(List<IncomeExpenseDetail> nonCoreIncomeDetailList) {
		this.nonCoreIncomeDetailList = nonCoreIncomeDetailList;
	}

	public List<IncomeExpenseDetail> getExpenseDetailList() {
		return expenseDetailList;
	}

	public void setExpenseDetailList(List<IncomeExpenseDetail> expenseDetailList) {
		this.expenseDetailList = expenseDetailList;
	}
}
