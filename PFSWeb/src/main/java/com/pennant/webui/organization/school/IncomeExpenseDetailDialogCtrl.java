package com.pennant.webui.organization.school;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.incomeexpensedetail.service.IncomeExpenseDetailService;
import com.pennanttech.pff.organization.school.model.IncomeExpenseDetail;
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public class IncomeExpenseDetailDialogCtrl extends GFCBaseCtrl<IncomeExpenseHeader>{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(IncomeExpenseDetailDialogCtrl.class);
	
	protected Window window_IncomeExpenseDetailsDialog;
	protected Button btnNew_SchoolCoreIncome;
	protected Button btnNew_SchoolNonCoreIncome;
	protected Button btnNew_SchoolExpense;
	
	protected Listbox listBoxSchoolCoreIncomeDetails;
	protected Listbox listBoxSchoolNonCoreIncomeDetails;
	protected Listbox listBoxSchoolExpenseDetails;
	
	private String module = "";
	private IncomeExpenseHeader incomeExpenseHeader;
	
	protected Intbox noOfStudents;
	protected Combobox frqOfCollection;
	protected Intbox multiplier;
	protected Decimalbox feeCharged;
	protected Decimalbox feeRecBasisFrq;
	protected Decimalbox totalCore;
	private List<IncomeExpenseDetail> coreIncomeDetailList = new ArrayList<>();
	private List<IncomeExpenseDetail> schoolCoreIncomes;
	
	private List<IncomeExpenseDetail> nonCoreIncomeDetailList = new ArrayList<>();
	private List<IncomeExpenseDetail> schoolNonCoreIncomes;
	
	private List<IncomeExpenseDetail> expenseDetailList = new ArrayList<>();
	private List<IncomeExpenseDetail> schoolExpenses;
	
	
	private transient IncomeExpenseDetailListCtrl incomeExpenseDetailListCtrl;
	
	@Autowired
	protected IncomeExpenseDetailService incomeExpenseDetailService;
	
	List<ValueLabel> years = new ArrayList<>();
	
	private int coreIncomeCount =0;
	private int nonCoreIncomeCount =0;
	private int expenseCount = 0;
	
	public IncomeExpenseDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IncomeExpenseDetailDialog";
	}

	public void onCreate$window_IncomeExpenseDetailsDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_IncomeExpenseDetailsDialog);

		try {
			// Get the required arguments.
			this.incomeExpenseHeader = (IncomeExpenseHeader) arguments.get("incomeExpenseHeader");

			if (arguments.get("incomeExpenseDetailListCtrl") != null) {
				this.incomeExpenseDetailListCtrl = (IncomeExpenseDetailListCtrl) arguments.get("incomeExpenseDetailListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}
			
			if (arguments.get("module") != null) {
				module = (String) arguments.get("module");
			}

			if (this.incomeExpenseHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
			BeanUtils.copyProperties(this.incomeExpenseHeader, incomeExpenseHeader);
			this.incomeExpenseHeader.setBefImage(incomeExpenseHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.incomeExpenseHeader.isWorkflow(), this.incomeExpenseHeader.getWorkflowId(), this.incomeExpenseHeader.getNextTaskId());

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
		
		int currentYear = DateUtil.getYear(DateUtility.getAppDate());
		for(int i=0; i<=10;i++){
			int year=currentYear;
			currentYear = year-1;
			//years.add(new ValueLabel(String.valueOf(year),String.valueOf(currentYear+"-").concat(String.valueOf(year))));
			years.add(new ValueLabel(String.valueOf(year),String.valueOf(year)));
		}
		
		
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		/*this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IncomeExpenseDetailList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IncomeExpenseDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IncomeExpenseDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IncomeExpenseDetailDialog_btnSave"));*/

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
		this.recordStatus.setValue(incomeExpenseHeader.getRecordStatus());
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.incomeExpenseHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			}

		/*readOnlyComponent(isReadOnly("OrganizationSchoolDialog_CIF"), this.cif);
		readOnlyComponent(isReadOnly("OrganizationSchoolDialog_Name"), this.name);
		readOnlyComponent(isReadOnly("OrganizationSchoolDialog_Code"), this.dateOfInc);
		readOnlyComponent(isReadOnly("OrganizationSchoolDialog_DateOfIncorporation"), this.dateOfInc);*/

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
		doWriteComponentsToBean(incomeExpenseHeader);
		
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
	
	public void doWriteComponentsToBean(IncomeExpenseHeader incomeExpenseHeader) throws InterruptedException{
		
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
				setValue(listitem, "finYear");
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
				setValue(listitem, "considered");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
			
			IncomeExpenseDetail aSchoolCoreIncome = (IncomeExpenseDetail) listitem.getAttribute("data");
			aSchoolCoreIncome.setIncomeExpense("Income");
			aSchoolCoreIncome.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aSchoolCoreIncome.setCreatedOn(DateUtility.getAppDate());
			
			boolean isNew = false;
			isNew = aSchoolCoreIncome.isNew();
			String tranType = "";

			/*if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aSchoolCoreIncome.getRecordType())) {
					aSchoolCoreIncome.setVersion(aSchoolCoreIncome.getVersion() + 1);
					if (isNew) {
						aSchoolCoreIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aSchoolCoreIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aSchoolCoreIncome.setNewRecord(true);
					}
				}
			}*/ 
				if(aSchoolCoreIncome.isNewRecord()){
						aSchoolCoreIncome.setVersion(1);
						aSchoolCoreIncome.setRecordType(PennantConstants.RCD_ADD);
					}else{
						tranType = PennantConstants.TRAN_UPD;
					}

					if(StringUtils.isBlank(aSchoolCoreIncome.getRecordType())){
						aSchoolCoreIncome.setVersion(aSchoolCoreIncome.getVersion()+1);
						aSchoolCoreIncome.setRecordType(PennantConstants.RCD_UPD);
					}

					if(aSchoolCoreIncome.getRecordType().equals(PennantConstants.RCD_ADD) && aSchoolCoreIncome.isNewRecord()){
						tranType =PennantConstants.TRAN_ADD;
					} else if(aSchoolCoreIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
						tranType =PennantConstants.TRAN_UPD;
					}
				else{
					aSchoolCoreIncome.setVersion(aSchoolCoreIncome.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}			
			try {
				AuditHeader auditHeader = newCoreIncomeProcess(aSchoolCoreIncome, tranType);
				if(auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0){
					auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
					closeDialog();
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					setCoreIncomeDetailList(schoolCoreIncomes);
				}
			}
			catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
			
		}
		
		for (Listitem listitem : listBoxSchoolNonCoreIncomeDetails.getItems()) {
			try {
				setValue(listitem, "nonCorefinYear");
			} catch (WrongValueException we) {
				wve.add(we);
			}
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
				setValue(listitem, "avgCollectionPerUnit");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
			
			IncomeExpenseDetail aSchoolNonCoreIncome = (IncomeExpenseDetail) listitem.getAttribute("data");
			aSchoolNonCoreIncome.setIncomeExpense("Income");
			aSchoolNonCoreIncome.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aSchoolNonCoreIncome.setCreatedOn(DateUtility.getAppDate());
			
			boolean isNew = false;
			isNew = aSchoolNonCoreIncome.isNew();
			String tranType = "";

				if(aSchoolNonCoreIncome.isNewRecord()){
						aSchoolNonCoreIncome.setVersion(1);
						aSchoolNonCoreIncome.setRecordType(PennantConstants.RCD_ADD);
					}else{
						tranType = PennantConstants.TRAN_UPD;
					}

					if(StringUtils.isBlank(aSchoolNonCoreIncome.getRecordType())){
						aSchoolNonCoreIncome.setVersion(aSchoolNonCoreIncome.getVersion()+1);
						aSchoolNonCoreIncome.setRecordType(PennantConstants.RCD_UPD);
					}

					if(aSchoolNonCoreIncome.getRecordType().equals(PennantConstants.RCD_ADD) && aSchoolNonCoreIncome.isNewRecord()){
						tranType =PennantConstants.TRAN_ADD;
					} else if(aSchoolNonCoreIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
						tranType =PennantConstants.TRAN_UPD;
					}
				else{
					aSchoolNonCoreIncome.setVersion(aSchoolNonCoreIncome.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}			
			try {
				AuditHeader auditHeader = newNonCoreIncomeProcess(aSchoolNonCoreIncome, tranType);
				if(auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0){
					auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
					closeDialog();
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					setNonCoreIncomeDetailList(schoolNonCoreIncomes);
				}
			}
			catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
		}
		
		for (Listitem listitem : listBoxSchoolExpenseDetails.getItems()) {
			try {
				setValue(listitem, "ExpenseFinYear");
			} catch (WrongValueException we) {
				wve.add(we);
			}
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
				setValue(listitem, "ExpensConsidered");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}

			IncomeExpenseDetail aSchoolExpense = (IncomeExpenseDetail) listitem.getAttribute("data");
			aSchoolExpense.setIncomeExpense("Expense");
			aSchoolExpense.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aSchoolExpense.setCreatedOn(DateUtility.getAppDate());
			
			boolean isNew = false;
			isNew = aSchoolExpense.isNew();
			String tranType = "";

				if(aSchoolExpense.isNewRecord()){
					aSchoolExpense.setVersion(1);
					aSchoolExpense.setRecordType(PennantConstants.RCD_ADD);
					}else{
						tranType = PennantConstants.TRAN_UPD;
					}

					if(StringUtils.isBlank(aSchoolExpense.getRecordType())){
						aSchoolExpense.setVersion(aSchoolExpense.getVersion()+1);
						aSchoolExpense.setRecordType(PennantConstants.RCD_UPD);
					}

					if(aSchoolExpense.getRecordType().equals(PennantConstants.RCD_ADD) && aSchoolExpense.isNewRecord()){
						tranType =PennantConstants.TRAN_ADD;
					} else if(aSchoolExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
						tranType =PennantConstants.TRAN_UPD;
					}
				else{
					aSchoolExpense.setVersion(aSchoolExpense.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}			
			try {
				AuditHeader auditHeader = newSchoolExpenseProcess(aSchoolExpense, tranType);
				if(auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0){
					auditHeader = ErrorControl.showErrorDetails(this.window_IncomeExpenseDetailsDialog, auditHeader);
					closeDialog();
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					setExpenseDetailList(schoolExpenses);
				}
			}
			catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
		}
		incomeExpenseHeader.setCoreIncomeList(this.coreIncomeDetailList);
		incomeExpenseHeader.setNonCoreIncomeList(this.nonCoreIncomeDetailList);
		incomeExpenseHeader.setExpenseList(this.expenseDetailList);
	}
	
	private AuditHeader newSchoolExpenseProcess(IncomeExpenseDetail aSchoolExpense, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader= getAuditHeader(aSchoolExpense, tranType);
		//coreIncomeDetailList = new ArrayList<>();
		schoolExpenses = new ArrayList<>();
		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aSchoolExpense.getCategory());
		
		errParm[0] = "Category" + ":"+ valueParm[0];
		
		List<IncomeExpenseDetail> incomeExpenseList = null;

		if (expenseDetailList != null
				&& expenseDetailList.size() > 0) {
			incomeExpenseList = expenseDetailList;
		} 

		if (incomeExpenseList != null && incomeExpenseList.size() > 0){
			for (int i = 0; i < incomeExpenseList.size(); i++) {
				IncomeExpenseDetail incomeExpenseDetail = incomeExpenseList.get(i);

				if ((incomeExpenseDetail.getCategory()) == aSchoolExpense.getCategory()){ // Both Current and Existing list rating same

					if(aSchoolExpense.isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41008",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							schoolExpenses.add(incomeExpenseDetail);
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							schoolExpenses.add(incomeExpenseDetail);
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < incomeExpenseHeader.getCoreIncomeList().size(); j++) {
								IncomeExpenseDetail income =  incomeExpenseHeader.getCoreIncomeList().get(j);
								if(income.getCategory().equals(aSchoolExpense.getCategory())){
									schoolExpenses.add(income);
								}
							}
						}
					}else{
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							schoolExpenses.add(incomeExpenseDetail);
						}
					}
				}else{
					schoolExpenses.add(incomeExpenseDetail);
				}
			}
		}

		if(!recordAdded){
			schoolExpenses.add(aSchoolExpense);
		}
		return auditHeader;
	}

	private AuditHeader newNonCoreIncomeProcess(IncomeExpenseDetail aincomeExpenseDetail, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader= getAuditHeader(aincomeExpenseDetail, tranType);
		//coreIncomeDetailList = new ArrayList<>();
		schoolNonCoreIncomes = new ArrayList<>();
		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aincomeExpenseDetail.getCategory());
		
		errParm[0] = "Category" + ":"+ valueParm[0];
		
		List<IncomeExpenseDetail> incomeExpenseList = null;

		if (nonCoreIncomeDetailList != null
				&& nonCoreIncomeDetailList.size() > 0) {
			incomeExpenseList = nonCoreIncomeDetailList;
		} 

		if (incomeExpenseList != null && incomeExpenseList.size() > 0){
			for (int i = 0; i < incomeExpenseList.size(); i++) {
				IncomeExpenseDetail incomeExpenseDetail = incomeExpenseList.get(i);

				if ((incomeExpenseDetail.getCategory()) == aincomeExpenseDetail.getCategory()){ // Both Current and Existing list rating same

					if(aincomeExpenseDetail.isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41008",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							schoolNonCoreIncomes.add(incomeExpenseDetail);
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							schoolNonCoreIncomes.add(incomeExpenseDetail);
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < incomeExpenseHeader.getCoreIncomeList().size(); j++) {
								IncomeExpenseDetail income =  incomeExpenseHeader.getCoreIncomeList().get(j);
								if(income.getCategory().equals(aincomeExpenseDetail.getCategory())){
									schoolNonCoreIncomes.add(income);
								}
							}
						}
					}else{
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							schoolNonCoreIncomes.add(incomeExpenseDetail);
						}
					}
				}else{
					schoolNonCoreIncomes.add(incomeExpenseDetail);
				}
			}
		}

		if(!recordAdded){
			schoolNonCoreIncomes.add(aincomeExpenseDetail);
		}
		return auditHeader;
	}

	private void setValue(Listitem listitem, String comonentId) {
		IncomeExpenseDetail schIncome = null;

		schIncome = (IncomeExpenseDetail) listitem.getAttribute("data");
		switch (comonentId) {
		case "category":
			Combobox combobox = (Combobox) getComponent(listitem, "category");
			String category = getComboboxValue(combobox);
			if (!combobox.isDisabled() && "#".equals(category) ) {
				throw new WrongValueException(combobox, Labels.getLabel("STATIC_INVALID",
						new String[] { "Cateogory" }));
			}
			schIncome.setCategory(category);
			break;
		case "finYear":
			Intbox intbox = (Intbox) getComponent(listitem, "finYear");
			int finYear = intbox.getValue();
			if (!intbox.isDisabled() && "#".equals(finYear)) {
				throw new WrongValueException(intbox, Labels.getLabel("STATIC_INVALID",
						new String[] { "Financial Year" }));
			}
			schIncome.setFinancialYear(finYear);
			break;
		case "noOfStudents":
			Intbox inybox1 = (Intbox) getComponent(listitem, "noOfStudents");
			int noOfStudents = inybox1.getValue();
			if (!inybox1.isReadonly() && noOfStudents <= 0 ) {
				throw new WrongValueException(inybox1, Labels.getLabel("STATIC_INVALID",
						new String[] { "No Of Students" }));
			}
			schIncome.setUnits(noOfStudents);
			break;
		case "feeCharged":
			Decimalbox textBox1 = (Decimalbox) getComponent(listitem, "feeCharged");
			BigDecimal feeCharged = textBox1.getValue();
			if (!(textBox1.isReadonly())  && (feeCharged.intValue() <= 0)) {
				throw new WrongValueException(textBox1, Labels.getLabel("FIELD_RANGE", new Object[] {
						"Fee Charged" , 1, 10000 }));
			}
			schIncome.setUnitPrice(feeCharged);
			break;
		case "frqOfCollection":
			Combobox combobox2 = (Combobox) getComponent(listitem, "frqOfCollection");
			String frqOfCollection = getComboboxValue(combobox2);
			if (!combobox2.isDisabled() && "#".equals(frqOfCollection)) {
				throw new WrongValueException(combobox2, Labels.getLabel("STATIC_INVALID",
						new String[] { "Collection Frequency" }));
			}
			schIncome.setFrequency(Integer.parseInt(frqOfCollection));
			break;
			
		case "considered":
			Checkbox checkbox = (Checkbox) getComponent(listitem, "considered");
			schIncome.setConsider(checkbox.isChecked());
			break;

		default:
			break;
		}
		schIncome.setRecordStatus(this.recordStatus.getValue());
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

		AuditHeader auditHeader= getAuditHeader(aincomeExpenseDetail, tranType);
		//coreIncomeDetailList = new ArrayList<>();
		schoolCoreIncomes = new ArrayList<>();
		String[] valueParm = new String[4];
		String[] errParm = new String[4];

		valueParm[0] = String.valueOf(aincomeExpenseDetail.getCategory());
		
		errParm[0] = "Category" + ":"+ valueParm[0];
		
		List<IncomeExpenseDetail> incomeExpenseList = null;

		if (coreIncomeDetailList != null
				&& coreIncomeDetailList.size() > 0) {
			incomeExpenseList = coreIncomeDetailList;
		} 

		if (incomeExpenseList != null && incomeExpenseList.size() > 0){
			for (int i = 0; i < incomeExpenseList.size(); i++) {
				IncomeExpenseDetail incomeExpenseDetail = incomeExpenseList.get(i);

				if ((incomeExpenseDetail.getCategory()) == aincomeExpenseDetail.getCategory()){ // Both Current and Existing list rating same

					if(aincomeExpenseDetail.isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41008",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							schoolCoreIncomes.add(incomeExpenseDetail);
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							incomeExpenseDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							schoolCoreIncomes.add(incomeExpenseDetail);
						}else if(incomeExpenseDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < incomeExpenseHeader.getCoreIncomeList().size(); j++) {
								IncomeExpenseDetail income =  incomeExpenseHeader.getCoreIncomeList().get(j);
								if(income.getCategory().equals(aincomeExpenseDetail.getCategory())){
									schoolCoreIncomes.add(income);
								}
							}
						}
					}else{
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							schoolCoreIncomes.add(incomeExpenseDetail);
						}
					}
				}else{
					schoolCoreIncomes.add(incomeExpenseDetail);
				}
			}
		}

		if(!recordAdded){
			schoolCoreIncomes.add(aincomeExpenseDetail);
		}
		return auditHeader;
	} 
	
	private AuditHeader getAuditHeader(IncomeExpenseDetail aIncomeExpenseDetail,String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aIncomeExpenseDetail.getBefImage(), aIncomeExpenseDetail);

		return new AuditHeader(getReference(), String.valueOf(aIncomeExpenseDetail.getCustId()),
				null, null, auditDetail, aIncomeExpenseDetail.getUserDetails(), getOverideMap());
	}

	
	public void onClick$btnNew_SchoolCoreIncome(Event event) {
		logger.debug(Literal.ENTERING);
		
		IncomeExpenseDetail schCoreIncome = new IncomeExpenseDetail();
		schCoreIncome.setNewRecord(true);
		//schCoreIncome.setOrgId(incomeExpenseHeader.getOrgId());
		schCoreIncome.setWorkflowId(0);
		schCoreIncome.setName(incomeExpenseHeader.getName());
		schCoreIncome.setType("Income"); 
		schCoreIncome.setCoreIncome(true);
		
		incomeExpenseHeader.setSchoolIncomeExpense(schCoreIncome);
		renderCoreIncomeDetails(incomeExpenseHeader);
		logger.debug(Literal.LEAVING);

	}

	public void renderCoreIncomeDetails(IncomeExpenseHeader incomeExpenseHeader) {

		List<ValueLabel> frqOfCollectionList = new ArrayList<>();
		frqOfCollectionList.add(new ValueLabel("1", "Yearly"));
		frqOfCollectionList.add(new ValueLabel("2", "Half-Yearly"));
		frqOfCollectionList.add(new ValueLabel("4", "Quarterly"));
		frqOfCollectionList.add(new ValueLabel("12", "Monthly"));
		
		List<ValueLabel> categories = new ArrayList<>();
		categories.add(new ValueLabel("LKG", "LKG"));
		categories.add(new ValueLabel("UKG", "UKG"));
		categories.add(new ValueLabel("FIRST", "FIRST"));
		categories.add(new ValueLabel("SECOND", "SECOND"));
		int size = 0;
		if (incomeExpenseHeader.getCoreIncomeList().size() > 0 && incomeExpenseHeader.getSchoolIncomeExpense() == null ) {
			setCoreIncomeDetailList(incomeExpenseHeader.getCoreIncomeList());
			size = incomeExpenseHeader.getCoreIncomeList().size();
		} else if (incomeExpenseHeader.getSchoolIncomeExpense() != null) {
			size = 1;
		}

		for (int i = 0; i < size; i++) {
			IncomeExpenseDetail schIncome;
			coreIncomeCount++;

			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getCoreIncomeList()) && incomeExpenseHeader.getSchoolIncomeExpense() == null) {
				schIncome = incomeExpenseHeader.getCoreIncomeList().get(i);
			} else {
				schIncome = incomeExpenseHeader.getSchoolIncomeExpense();
			}
			Listitem item = new Listitem();
			Listcell listCell;

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
			// fillComboBox(finYear, String.valueOf(schIncome.getFinancialYear()), years, "");
			finYear.setValue(incomeExpenseHeader.getFinancialYear());
			finYear.setReadonly(true);
			listCell.appendChild(finYear);
			listCell.setParent(item);

			// Category
			listCell = new Listcell();
			listCell.setId("category".concat(String.valueOf(coreIncomeCount)));
			Combobox category = new Combobox();
			category.addForward("onChange", self, "onChangeCategory", category);
			fillComboBox(category, schIncome.getCategory(), categories, "");
			listCell.appendChild(category);
			listCell.setParent(item);

			// Number Of Students
			listCell = new Listcell();
			listCell.setId("noOfStudents".concat(String.valueOf(coreIncomeCount)));
		    noOfStudents = new Intbox();
			noOfStudents.addForward("onChange", self, "onChangecalculateFeeReceiptFrq", noOfStudents);
			noOfStudents.setValue(schIncome.getUnits());
			listCell.appendChild(noOfStudents);
			listCell.setParent(item);
						
			// Fee Charged Per Student P.A
			listCell = new Listcell();
			listCell.setId("feeCharged".concat(String.valueOf(coreIncomeCount)));
		    feeCharged = new Decimalbox();
			feeCharged.addForward("onChange", self, "onChangecalculateFeeReceiptFrq", feeCharged);
			feeCharged.setValue(schIncome.getUnitPrice());
			listCell.appendChild(feeCharged);
			listCell.setParent(item);

			// Frequency Of Collection
			listCell = new Listcell();
			listCell.setId("frqOfCollection".concat(String.valueOf(coreIncomeCount)));
		    frqOfCollection = new Combobox();
			fillComboBox(frqOfCollection, String.valueOf(schIncome.getFrequency()), frqOfCollectionList, "");
			frqOfCollection.addForward("onChange", self, "onChangeFrqOfCollection", frqOfCollection);
			listCell.appendChild(frqOfCollection);
			listCell.setParent(item);

			// Multiplier
			listCell = new Listcell();
			listCell.setId("multiplier".concat(String.valueOf(coreIncomeCount)));
			multiplier = new Intbox();
			multiplier.setReadonly(true);
			multiplier.addForward("onChange", self, "onChangecalculateFeeReceiptFrq", multiplier);
			listCell.appendChild(multiplier);
			listCell.setParent(item);

			// Fee Receipt Basis Frequency
			listCell = new Listcell();
			listCell.setId("feeRecBasisFrq".concat(String.valueOf(coreIncomeCount)));
		    feeRecBasisFrq = new Decimalbox();
			feeRecBasisFrq.setReadonly(true);
			listCell.appendChild(feeRecBasisFrq);
			listCell.setParent(item);

			// Total Core
			listCell = new Listcell();
			listCell.setId("totalCore".concat(String.valueOf(coreIncomeCount)));
		    totalCore = new Decimalbox();
			totalCore.setReadonly(true);
			listCell.appendChild(totalCore);
			listCell.setParent(item);

			// To Be Considered
			listCell = new Listcell();
			listCell.setId("considered".concat(String.valueOf(coreIncomeCount)));
			Checkbox considered = new Checkbox();
			if(schIncome.isNewRecord()){
			considered.setChecked(true);
			}else {
				considered.setChecked(schIncome.isConsider());
			}
			listCell.appendChild(considered);
			listCell.setParent(item);
			item.setAttribute("data", schIncome);
			
			onChangeFrqOfCollection(null);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onCoreIncomeItemDoubleClicked");
			if(!this.listBoxSchoolCoreIncomeDetails.getItems().isEmpty()){
				this.listBoxSchoolCoreIncomeDetails.getItems().add(0, item);
			}else {
				this.listBoxSchoolCoreIncomeDetails.appendChild(item);
			}
		}
	}
	
	public void onChangecalculateFeeReceiptFrq(ForwardEvent event){
		logger.debug(Literal.ENTERING);
		if(feeCharged.getValue().intValue()!=0 && multiplier.getValue()!=null && multiplier.getValue().intValue()!=0){
			feeRecBasisFrq.setValue(BigDecimal.valueOf(feeCharged.getValue().intValue()/multiplier.getValue().intValue()));
		}else{
			feeRecBasisFrq.setValue(BigDecimal.ZERO);
		}
		if(noOfStudents.getValue()!=null && feeCharged.getValue()!=null){
			totalCore.setValue(BigDecimal.valueOf(noOfStudents.getValue()*feeCharged.getValue().intValue()));
		}else{
			totalCore.setValue(BigDecimal.ZERO);
		}
		logger.debug(Literal.LEAVING);
	}
	
	public void onChangeFrqOfCollection(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		if (event != null) {
			frqOfCollection = (Combobox) event.getData();
		}
		if (frqOfCollection != null) {
			String multiplierValue = getComboboxValue(frqOfCollection);

			if (!"#".equals(multiplierValue)) {
				multiplier.setValue(Integer.parseInt(getComboboxValue(frqOfCollection)));
				onChangecalculateFeeReceiptFrq(event);
			} else {
				multiplier.setValue(0);
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	public void onChangeCategory(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		if (this.listBoxSchoolCoreIncomeDetails.getItems().size() > 1) {
			Listitem listitem = listBoxSchoolCoreIncomeDetails.getItemAtIndex(0);
			Combobox combobox = (Combobox) getComponent(listitem, "category");
			String category = getComboboxValue(combobox);
			
			for (int i = 1; i < listBoxSchoolCoreIncomeDetails.getItems().size(); i++) {
				Listitem item = listBoxSchoolCoreIncomeDetails.getItemAtIndex(i);
				Combobox combobox1 = (Combobox) getComponent(item, "category");
				if (getComboboxValue(combobox1).equals(category)) {
					combobox.setErrorMessage("Same category already exist");
					throw new WrongValueException(combobox, "Same category already exist");
				}else {
					combobox.setErrorMessage("");
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	public void onClick$btnNew_SchoolNonCoreIncome(Event event){
		logger.debug(Literal.ENTERING);
		
		IncomeExpenseDetail schNonCoreIncome = new IncomeExpenseDetail();
		schNonCoreIncome.setNewRecord(true);
		schNonCoreIncome.setOrgId(incomeExpenseHeader.getOrgId());
		schNonCoreIncome.setWorkflowId(0);
		schNonCoreIncome.setName(incomeExpenseHeader.getName());
		//schNonCoreIncome.setType("NonCoreIncome");
		schNonCoreIncome.setCoreIncome(false);
		
		incomeExpenseHeader.setSchoolIncomeExpense(schNonCoreIncome);
		renderNonCoreTncomeDetails(incomeExpenseHeader);
		
		logger.debug(Literal.LEAVING);
		
	}

	public void renderNonCoreTncomeDetails(IncomeExpenseHeader incomeExpenseHeaderx) {
		
		int size = 0;
		if (incomeExpenseHeaderx.getCoreIncomeList().size() > 0 && incomeExpenseHeaderx.getSchoolIncomeExpense() == null ) {
			size = incomeExpenseHeaderx.getCoreIncomeList().size();
			setNonCoreIncomeDetailList(incomeExpenseHeader.getNonCoreIncomeList());
		} else if (incomeExpenseHeaderx.getSchoolIncomeExpense() != null) {
			size = 1;
		}
		
		for (int i = 0; i < size; i++) {
			IncomeExpenseDetail schNonCoreIncome;
			nonCoreIncomeCount++;

			if (CollectionUtils.isNotEmpty(incomeExpenseHeaderx.getCoreIncomeList())) {
				schNonCoreIncome = incomeExpenseHeaderx.getCoreIncomeList().get(i);
			} else {
				schNonCoreIncome = incomeExpenseHeaderx.getSchoolIncomeExpense();
			}
		
		Listitem item = new Listitem();
		Listcell listCell;
		
		//School Name
		listCell=new Listcell();
		Textbox schoolName = new Textbox();
		schoolName.setReadonly(true);
		schoolName.setValue(incomeExpenseHeaderx.getName());
		listCell.appendChild(schoolName);
		listCell.setParent(item);
		
		//Financial year
		listCell=new Listcell();
		listCell.setId("nonCorefinYear".concat(String.valueOf(nonCoreIncomeCount)));
		Combobox finYear = new Combobox();
		fillComboBox(finYear, String.valueOf(schNonCoreIncome.getFinancialYear()), years, "");
		listCell.appendChild(finYear);
		listCell.setParent(item);
		
		//Product/Service
		listCell = new Listcell();
		listCell.setId("prodService".concat(String.valueOf(nonCoreIncomeCount)));
		ExtendedCombobox prodService=new ExtendedCombobox();
		
		prodService.setMaxlength(10);
		prodService.setMandatoryStyle(false);
		prodService.setModuleName("ProductType");
		prodService.setValueColumn("FieldCodeValue");
		prodService.setDescColumn("ValueDesc");
		prodService.setValidateColumns(new String[] { "FieldCodeValue" });
		
		/*
		if (!schNonCoreIncome.isNewRecord()) { TODO
			prodService.setValue(StringUtils.trimToEmpty(schNonCoreIncome.getLoockUpId()),
					StringUtils.trimToEmpty(schNonCoreIncome.getReasonDesc()));
			if (schNonCoreIncome.getLoockUpId() = null) {
				prodService.setAttribute("ReasonId", fi.getReason());
			} else {
				prodService.setAttribute("ReasonId", null);
			}
		}
		*/
		listCell.appendChild(prodService);
		listCell.setParent(item);
		
		// Number Of Units Served
		listCell = new Listcell();
		listCell.setId("noOfUnitsServed".concat(String.valueOf(nonCoreIncomeCount)));
		Intbox noOfUnitsServed = new Intbox();
		noOfUnitsServed.setValue(schNonCoreIncome.getUnits());
		listCell.appendChild(noOfUnitsServed);
		listCell.setParent(item);
	
		//Average Collection Per Unit
		listCell=new Listcell();
		listCell.setId("avgCollectionPerUnit".concat(String.valueOf(nonCoreIncomeCount)));
		Decimalbox avgCollectionPerUnit = new Decimalbox();
		avgCollectionPerUnit.setValue(schNonCoreIncome.getUnitPrice());
		listCell.appendChild(avgCollectionPerUnit);
		listCell.setParent(item);
		
		//Total NonCore
		listCell=new Listcell();
		listCell.setId("totalNonCore".concat(String.valueOf(nonCoreIncomeCount)));
		Decimalbox totalNonCore = new Decimalbox();
		totalNonCore.setReadonly(true);
		listCell.appendChild(totalNonCore);
		listCell.setParent(item);
		
		//To Be Considered
		listCell=new Listcell();
		listCell.setId("nonCoreconsidered".concat(String.valueOf(nonCoreIncomeCount)));
		Checkbox considered = new Checkbox();
		if(schNonCoreIncome.isNewRecord()){
			considered.setChecked(true);
		}else {
			considered.setChecked(schNonCoreIncome.isConsider());
		}
		listCell.appendChild(considered);
		listCell.setParent(item);
		item.setAttribute("data", schNonCoreIncome);
		
		this.listBoxSchoolNonCoreIncomeDetails.appendChild(item);
		
		}
	}
	
	public void onClick$btnNew_SchoolExpense(Event event){
		logger.debug(Literal.ENTERING);
		
		IncomeExpenseDetail schExpense = new IncomeExpenseDetail();
		schExpense.setNewRecord(true);
		schExpense.setOrgId(incomeExpenseHeader.getOrgId());
		schExpense.setWorkflowId(0);
		schExpense.setName(incomeExpenseHeader.getName());
		//schExpense.setType("Expense"); TODO
		schExpense.setCoreIncome(false);
		
		incomeExpenseHeader.setSchoolIncomeExpense(schExpense);
		renderExpenseDetails(incomeExpenseHeader);
		
		logger.debug(Literal.LEAVING);
		
	}

	public void renderExpenseDetails(IncomeExpenseHeader incomeExpenseHeader) {

		int size = 0;
		if (incomeExpenseHeader.getCoreIncomeList().size() > 0 && incomeExpenseHeader.getSchoolIncomeExpense() == null) {
			size = incomeExpenseHeader.getCoreIncomeList().size();
		} else if (incomeExpenseHeader.getSchoolIncomeExpense() != null) {
			size = 1;
		}

		for (int i = 0; i < size; i++) {
			IncomeExpenseDetail schExpense;
			expenseCount++;

			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getCoreIncomeList())) {
				schExpense = incomeExpenseHeader.getCoreIncomeList().get(i);
			} else {
				schExpense = incomeExpenseHeader.getSchoolIncomeExpense();
			}

			Listitem item = new Listitem();
			Listcell listCell;
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
			Combobox finYear = new Combobox();
			fillComboBox(finYear, String.valueOf(schExpense.getFinancialYear()), years, "");
			listCell.appendChild(finYear);
			listCell.setParent(item);

			// Expense Type
			listCell = new Listcell();
			listCell.setId("expenseType".concat(String.valueOf(expenseCount)));
			ExtendedCombobox expenseType = new ExtendedCombobox();

			expenseType.setMaxlength(10);
			expenseType.setMandatoryStyle(false);
			expenseType.setModuleName("ExpenseType");
			expenseType.setValueColumn("ExpenseTypeCode");
			expenseType.setDescColumn("ExpenseTypeDesc");
			expenseType.setValidateColumns(new String[] { "ExpenseTypeCode" });

			listCell.appendChild(expenseType);
			listCell.setParent(item);

			// Expense Incurred
			listCell = new Listcell();
			listCell.setId("expenseIncurred".concat(String.valueOf(expenseCount)));
			Decimalbox expenseIncurred = new Decimalbox();
			expenseIncurred.setReadonly(true);
			listCell.appendChild(expenseIncurred);
			listCell.setParent(item);

			// To Be Considered
			listCell = new Listcell();
			listCell.setId("ExpensConsidered".concat(String.valueOf(expenseCount)));
			Checkbox considered = new Checkbox();
			if(schExpense.isNewRecord()){
			considered.setChecked(true);
			}else {
				considered.setChecked(schExpense.isConsider());
			}
			listCell.appendChild(considered);
			listCell.setParent(item);
			item.setAttribute("data", schExpense);

			this.listBoxSchoolExpenseDetails.appendChild(item);
		}

	}
	
	private AuditHeader getAuditHeader(IncomeExpenseHeader incomeExpenseHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, incomeExpenseHeader.getBefImage(), incomeExpenseHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, incomeExpenseHeader.getUserDetails(),
				getOverideMap());
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
			ErrorControl.showErrorControl(this.window_IncomeExpenseDetailsDialog,auditHeader);
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
